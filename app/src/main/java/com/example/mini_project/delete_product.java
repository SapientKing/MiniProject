package com.example.mini_project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class delete_product extends AppCompatActivity {

    private EditText productIdInput;
    private ImageView productImageView;
    private TextView productDetailsTextView;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product);

        // Initialize Views
        productIdInput = findViewById(R.id.delete_product_id_input);
        productImageView = findViewById(R.id.product_image_view);
        productDetailsTextView = findViewById(R.id.product_details);
        Button deleteButton = findViewById(R.id.btnDelete);

        // Initialize Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Delete Button Click
        deleteButton.setOnClickListener(v -> {
            String productId = productIdInput.getText().toString();
            if (productId.isEmpty()) {
                Toast.makeText(this, "Please enter a Product ID", Toast.LENGTH_SHORT).show();
            } else {
                fetchDetailsAndAutoDisplay(productId); // Fetch details and display automatically
            }
        });
    }

    private void fetchDetailsAndAutoDisplay(String productId) {
        // Fetch product details from the database
        Cursor cursor = database.rawQuery("SELECT product_name, price_per_unit, quantity, product_image FROM products WHERE product_id = ?", new String[]{productId});

        if (cursor != null && cursor.moveToFirst()) {
            // Fetch product details
            String productName = cursor.getString(0);
            double pricePerUnit = cursor.getDouble(1);
            int quantity = cursor.getInt(2);
            byte[] imageBytes = cursor.getBlob(3);

            // Automatically display product details
            productDetailsTextView.setText(String.format("Name: %s\nPrice: %.2f\nQuantity: %d", productName, pricePerUnit, quantity));

            // Display product image
            if (imageBytes != null) {
                Bitmap productImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                productImageView.setImageBitmap(productImage);
            } else {
                Glide.with(this).load(R.drawable.ic_placeholder_image).into(productImageView);
            }

            cursor.close();

            // Show confirmation dialog to delete the product
            confirmAndDelete(productId);
        } else {
            // Show error if product not found
            productDetailsTextView.setText("No product found with the given ID.");
            Glide.with(this).load(R.drawable.ic_error_image).into(productImageView); // Error image
        }
    }

    private void confirmAndDelete(String productId) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed with deletion
                    deleteProductFromDatabase(productId);
                })
                .setNegativeButton("No", null) // Dismiss dialog
                .show();
    }

    private void deleteProductFromDatabase(String productId) {
        // Perform the deletion operation
        int rowsDeleted = database.delete("products", "product_id = ?", new String[]{productId});
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Product Deleted Successfully", Toast.LENGTH_SHORT).show();
            resetViews(); // Reset all views after deletion
        } else {
            Toast.makeText(this, "Failed to delete the product", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetViews() {
        // Clear all views after successful deletion
        productImageView.setImageResource(R.drawable.ic_placeholder_image);
        productDetailsTextView.setText("");
        productIdInput.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
