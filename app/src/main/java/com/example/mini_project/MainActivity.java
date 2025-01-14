package com.example.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

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

        // Handle login button click
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameInput = username.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (databaseHelper.validateUser(usernameInput, passwordInput)) {
                        // Navigate to the ViewAction activity on successful login
                        Intent intent = new Intent(getApplicationContext(), ViewAction.class);
                        intent.putExtra("username", usernameInput); // Optionally pass the username
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    } else {
                        // Show error message on invalid credentials
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
