package com.example.mini_project;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class delete_product extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText txtDelID;
    Button btnDelete;
    TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        // Initialize UI elements
        dbHelper = new DatabaseHelper(this);
        txtDelID = findViewById(R.id.txtdelID);
        btnDelete = findViewById(R.id.btnDelete);
        infoText = findViewById(R.id.infoText);

        // Display all products in infoText
        displayProducts();

        // Set onClickListener for delete button
        btnDelete.setOnClickListener(v -> {
            String productId = txtDelID.getText().toString().trim();
            if (productId.isEmpty()) {
                Toast.makeText(this, "Please enter a Product ID.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isDeleted = dbHelper.deleteProductById(productId);
            if (isDeleted) {
                Toast.makeText(this, "Product deleted successfully.", Toast.LENGTH_SHORT).show();
                displayProducts(); // Refresh the displayed products
                txtDelID.setText(""); // Clear input field
            } else {
                Toast.makeText(this, "Product not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch and display products in the TextView
    private void displayProducts() {
        Cursor cursor = dbHelper.getAllProducts();
        StringBuilder builder = new StringBuilder();
        builder.append("Current Products:\n");

        while (cursor.moveToNext()) {
            builder.append("ID: ").append(cursor.getInt(0))
                    .append(", Name: ").append(cursor.getString(1))
                    .append(", Price: $").append(cursor.getDouble(2))
                    .append(", Quantity: ").append(cursor.getInt(3))
                    .append("\n");
        }
        cursor.close();

        if (builder.length() == "Current Products:\n".length()) {
            builder.append("No products available.");
        }
        infoText.setText(builder.toString());
    }
}
