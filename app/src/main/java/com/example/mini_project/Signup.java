package com.example.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Signup extends AppCompatActivity {

    EditText username, email, password, confirmPassword;
    MaterialButton registerButton;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Views
        username = findViewById(R.id.username);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.txtpassword);
        confirmPassword = findViewById(R.id.EtConPassword);
        registerButton = findViewById(R.id.btnlogin);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        // Handle Registration
        registerButton.setOnClickListener(v -> registerUser());

        // Set OnClickListener for the TextView

        TextView loginRedirect = findViewById(R.id.textView3);
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the MainActivity (Login Page)
                Intent intent = new Intent(Signup.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String usernameInput = username.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String confirmPasswordInput = confirmPassword.getText().toString().trim();

        // Reset all error messages to invisible
        findViewById(R.id.constraintLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.constraintLayout1).setVisibility(View.INVISIBLE);
        findViewById(R.id.constraintLayout2).setVisibility(View.INVISIBLE);
        findViewById(R.id.constraintLayout3).setVisibility(View.INVISIBLE);

        // Validate Input
        boolean hasError = false;

        if (TextUtils.isEmpty(usernameInput)) {
            TextView usernameError = findViewById(R.id._username);
            usernameError.setText("Username is required");
            findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (TextUtils.isEmpty(emailInput)) {
            TextView emailError = findViewById(R.id.tv_email);
            emailError.setText("Email is required");
            findViewById(R.id.constraintLayout1).setVisibility(View.VISIBLE);
            hasError = true;
        } else if (databaseHelper.checkUserExists(emailInput)) {
            TextView emailError = findViewById(R.id.tv_email);
            emailError.setText("Email already registered");
            findViewById(R.id.constraintLayout1).setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (TextUtils.isEmpty(passwordInput)) {
            TextView passwordError = findViewById(R.id.tv_pass);
            passwordError.setText("Password is required");
            findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (TextUtils.isEmpty(confirmPasswordInput)) {
            TextView confirmPasswordError = findViewById(R.id.tv_con_pass);
            confirmPasswordError.setText("Confirm Password is required");
            findViewById(R.id.constraintLayout3).setVisibility(View.VISIBLE);
            hasError = true;
        } else if (!passwordInput.equals(confirmPasswordInput)) {
            TextView confirmPasswordError = findViewById(R.id.tv_con_pass);
            confirmPasswordError.setText("Passwords do not match");
            findViewById(R.id.constraintLayout3).setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (hasError) {
            return; // Stop processing if there are errors
        }

        // Insert Data
        boolean isInserted = databaseHelper.insertData(usernameInput, emailInput, passwordInput);
        if (isInserted) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();

            // Navigate to ViewAction class
            Intent intent = new Intent(Signup.this, MainActivity.class);
            intent.putExtra("username", usernameInput); // Pass data if needed
            startActivity(intent);
            finish(); // Optional: close the current activity
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

}
