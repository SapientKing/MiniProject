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
        //deleteItems.setOnClickListener(this);
        //scanItems.setOnClickListener(this);
        //viewInventory.setOnClickListener(this);
    }

    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {
            //case R.id.addItems : i = new Intent(this,AddProduct.class); startActivity(i); break;
            //case R.id.deleteItems : i = new Intent(this,DeleteProduct.class);startActivity(i); break;
            //case R.id.scanItems : i = new Intent(this,ViewProduct.class);startActivity(i); break;
            //case R.id.viewInventory : i = new Intent(this,ViewInventory.class);startActivity(i); break;
            default: break;
        }
    }
}