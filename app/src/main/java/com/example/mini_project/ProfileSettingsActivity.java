package com.example.mini_project;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileSettingsActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private EditText editUsername, editEmail, editPassword;
    private Button uploadImageButton, saveButton;
    private Uri imageUri; // To store selected image URI
    private SharedPreferences sharedPreferences;
    private int userId; // To store user ID
    private DatabaseHelper dbHelper; // To interact with the database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // Initialize views
        profileImageView = findViewById(R.id.profile_image_view);
        editUsername = findViewById(R.id.edit_username);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        uploadImageButton = findViewById(R.id.upload_image_button);
        saveButton = findViewById(R.id.save_button);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Get user ID from the Intent
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Load user details
        loadUserDetails();

        // Set up upload image button
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Set up save button (for saving user details, not the image)
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();
            }
        });
    }

    private void loadUserDetails() {
        // Load user details from the database using userId
        Cursor cursor = dbHelper.getUserById(userId);  // Get user by userId
        if (cursor != null && cursor.moveToFirst()) {
            // Get user details from the cursor
            String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_2));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_3));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_4));

            // Set values to views (EditTexts)
            editUsername.setText(username);
            editEmail.setText(email);
            editPassword.setText(password);

            // Optionally load the profile image from SharedPreferences
            String imagePath = sharedPreferences.getString("profile_image_" + userId, null);
            if (imagePath != null) {
                imageUri = Uri.parse(imagePath);
                profileImageView.setImageURI(imageUri);  // Set the selected image
            } else {
                profileImageView.setImageResource(R.drawable.default_profile);  // Default image
            }

            cursor.close();  // Always close the cursor
        } else {
            Toast.makeText(this, "User details not found.", Toast.LENGTH_SHORT).show();
            finish();  // If no user found, close the activity
        }
    }

    private void openFileChooser() {
        // Open file picker to select image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void saveImageLocally(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile_image_" + userId + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            // Save the local path in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profile_image_" + userId, file.getAbsolutePath());
            editor.apply();

            // Set the new image Uri
            profileImageView.setImageURI(Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image locally.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            saveImageLocally(imageUri); // Save the image locally
        }
    }



    private void saveUserDetails() {
        // Get input values
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the user details (username, email, password) to the database using userId
        boolean isUpdated = dbHelper.updateUser(userId, username, email, password);
        if (isUpdated) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error updating profile in database.", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(ProfileSettingsActivity.this, ViewAction.class);
        intent.putExtra("userId", userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Prevent stacking
        startActivity(intent);
        finish();

    }

}
