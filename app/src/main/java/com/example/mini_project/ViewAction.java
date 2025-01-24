package com.example.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ViewAction extends AppCompatActivity {
    private CardView addItems, deleteItems, viewInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_action);

        // Receive data from Intent
        String username = getIntent().getStringExtra("username");

        // Display the username
        TextView welcomeMessage = findViewById(R.id.tv_username);
        if (username != null) {
            welcomeMessage.setText("Welcome, " + username + "!");
        } else {
            welcomeMessage.setText("Welcome!");
        }

        // Initialize CardView buttons
        addItems = findViewById(R.id.addItems);
        deleteItems = findViewById(R.id.deleteItems);
        viewInventory = findViewById(R.id.viewInventory);

        // Add Items Click Listener
        addItems.setOnClickListener(v -> {
            Intent intent = new Intent(ViewAction.this, add_Product.class);
            intent.putExtra("username", username); // Pass username
            startActivity(intent);
        });

        // Delete Items Click Listener
        deleteItems.setOnClickListener(v -> {
            Intent intent = new Intent(ViewAction.this, delete_product.class);
            intent.putExtra("username", username); // Pass username
            startActivity(intent);
        });

        // View Inventory Click Listener
        viewInventory.setOnClickListener(v -> {
            Intent intent = new Intent(ViewAction.this, ProductListActivity.class);
            intent.putExtra("username", username); // Pass username
            startActivity(intent);
        });
    }
}
