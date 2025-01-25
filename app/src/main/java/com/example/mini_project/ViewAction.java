package com.example.mini_project;

import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.File;

public class ViewAction extends AppCompatActivity {
    private CardView addItems, deleteItems, viewInventory;
    private SharedPreferences sharedPreferences;
    private ImageView profileImageView, logoutIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_action);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Load user ID from Intent
        int userId = getIntent().getIntExtra("userId", -1); // Retrieve userId from the intent

        // Set up UI elements
        TextView welcomeMessage = findViewById(R.id.tv_username);
        profileImageView = findViewById(R.id.profile_icon);
        logoutIcon = findViewById(R.id.logout_icon);

        // Load and display user details (username)
        loadUserDetails(welcomeMessage, userId);

        // Load and display profile image
        loadProfileImage(userId);

        // Set OnClickListener for the profile image
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileSettingsActivity and pass the user ID
                Intent intent = new Intent(ViewAction.this, ProfileSettingsActivity.class);
                intent.putExtra("userId", userId); // Pass the user ID
                startActivity(intent);
            }
        });

        // Set OnClickListener for the logout icon
        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear user session (optional)
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Remove all shared preferences data (user session)
                editor.apply();

                // Navigate to MainActivity
                Intent intent = new Intent(ViewAction.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

        // Initialize CardView buttons
        addItems = findViewById(R.id.addItems);
        deleteItems = findViewById(R.id.deleteItems);
        viewInventory = findViewById(R.id.viewInventory);

        // Initialize CardViews and set listeners
        addItems = findViewById(R.id.addItems);
        deleteItems = findViewById(R.id.deleteItems);
        viewInventory = findViewById(R.id.viewInventory);

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAction.this, add_Product.class);
                startActivity(intent);
            }
        });

        deleteItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAction.this, delete_product.class);
                startActivity(intent);
            }
        });

        viewInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAction.this, ProductListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadUserDetails(TextView welcomeMessage, int userId) {
        // Retrieve the username stored in SharedPreferences using the userId
        String username = sharedPreferences.getString("username", "User");

        // Set the welcome message
        welcomeMessage.setText("Welcome, " + username + "!");
    }

    private void loadProfileImage(int userId) {
        String imagePath = sharedPreferences.getString("profile_image_" + userId, null);

        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                profileImageView.setImageURI(Uri.fromFile(imageFile)); // Load the image from local storage
            } else {
                profileImageView.setImageResource(R.drawable.default_profile); // Default image
            }
        } else {
            profileImageView.setImageResource(R.drawable.default_profile); // Default image
        }
    }



}
