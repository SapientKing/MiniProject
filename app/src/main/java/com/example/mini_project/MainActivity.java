package com.example.mini_project;

import android.content.Intent;
import android.content.SharedPreferences; // Import SharedPreferences
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences; // Declare SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.txtpassword);
        MaterialButton loginbtn = findViewById(R.id.btnlogin);
        ImageView signupIcon = findViewById(R.id.iv_signup); // Find the ImageView by its ID

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Handle login button click
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = username.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    int userId = databaseHelper.validateUsergetid(usernameInput, passwordInput);
                    if (userId != -1) {
                        // Store the username and user ID in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", usernameInput);
                        editor.putInt("userId", userId); // Save the user ID
                        editor.apply();

                        // Navigate to the ViewAction activity with user ID
                        Intent intent = new Intent(getApplicationContext(), ViewAction.class);
                        intent.putExtra("userId", userId); // Pass user ID to the next activity
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, String.format("User ID: %d - Invalid user ID. Please try again.", userId), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                        username.setText("");
                        password.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "LOGIN FAILED. Please check your credentials!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Handle signup icon click
        signupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Signup.java
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });
    }
}