package com.example.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewAction extends AppCompatActivity {
    private CardView addItems, deleteItems, scanItems, viewInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_action);

        // Receive data from Intent
        String username = getIntent().getStringExtra("username");

        // Display the username (if needed)
        TextView welcomeMessage = findViewById(R.id.tv_username);
        welcomeMessage.setText("Welcome, " + username + "!");


        addItems = (CardView) findViewById(R.id.addItems);
        deleteItems = (CardView) findViewById(R.id.deleteItems);
        //scanItems = (CardView) findViewById(R.id.scanItems);
        viewInventory = (CardView) findViewById(R.id.viewInventory);

        //addItems.setOnClickListener(this);
        //scanItems.setOnClickListener(this);
        //viewInventory.setOnClickListener(this);

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to DeleteProduct activity
                Intent intent = new Intent(ViewAction.this, add_Product.class);
                startActivity(intent);
            }
        });

        deleteItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to DeleteProduct activity
                Intent intent = new Intent(ViewAction.this, delete_product.class);
                startActivity(intent);
            }
        });

        viewInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to DeleteProduct activity
                Intent intent = new Intent(ViewAction.this, ProductListActivity.class);
                startActivity(intent);
            }
        });
    }
    }
