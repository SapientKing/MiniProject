package com.example.mini_project;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class view_inventory extends AppCompatActivity {
    private TextView tvOutputProductId, tvOutputProductName, tvOutputPrice, tvOutputSupplierDetails;
    private ImageView imgProduct;
    private EditText etQuantity;
    private Button btnUpdate;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);
        String username = getIntent().getStringExtra("username");

        // Initialize views
        tvOutputProductId = findViewById(R.id.tvOutputProductId);
        tvOutputProductName = findViewById(R.id.tvOutputProductName);
        tvOutputPrice = findViewById(R.id.tvOutputPrice);
        tvOutputSupplierDetails = findViewById(R.id.tvOutputSupplierDetails);
        imgProduct = findViewById(R.id.imgProduct);
        etQuantity = findViewById(R.id.etQuantity);  // EditText for new quantity
        btnUpdate = findViewById(R.id.btnUpdate);  // Update button

        // Retrieve product details from the Intent
        Intent intent = getIntent();
        String productId = intent.getStringExtra("product_id");
        String productName = intent.getStringExtra("product_name");
        double productPrice = intent.getDoubleExtra("product_price", 0.0);
        int productQuantity = intent.getIntExtra("product_quantity", 0);
        String productSupplier = intent.getStringExtra("product_supplier");
        byte[] productImageBytes = intent.getByteArrayExtra("product_image");

        // Display the product details
        if (productId != null) {
            tvOutputProductId.setText("Product ID: " + productId);
        }
        tvOutputProductName.setText("Product Name: " + productName);
        tvOutputPrice.setText("Price: RM" + String.format("%.2f", productPrice));
        tvOutputSupplierDetails.setText("Supplier: " + productSupplier);

        // Decode the product image if available
        if (productImageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(productImageBytes, 0, productImageBytes.length);
            imgProduct.setImageBitmap(bitmap);
        } else {
            imgProduct.setImageResource(R.drawable.product_pic); // Default image
        }

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Example of fetching product details if needed for further logic
        fetchProductDetails(productId);

        // Set up button click listener for updating quantity and price
        btnUpdate.setOnClickListener(v -> updateQuantityAndPrice());

        // Get passed data
        byte[] imageBytes = getIntent().getByteArrayExtra("product_image");
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgProduct.setImageBitmap(bitmap);  // Set the image
        }
    }

    private void fetchProductDetails(String productId) {
        // Query the database to get the product based on product ID
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getProductById(productId);

            // If the product is found, display its details
            if (cursor != null && cursor.moveToFirst()) {
                // Check if the column indexes are valid (not -1)
                int productIdIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_1);
                int productNameIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_2);
                int priceIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_3);
                int quantityIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_4);
                int supplierIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_5);
                int imageIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_6);

                if (productIdIndex != -1 && productNameIndex != -1 && priceIndex != -1 &&
                        quantityIndex != -1 && supplierIndex != -1 && imageIndex != -1) {

                    String productName = cursor.getString(productNameIndex);
                    double price = cursor.getDouble(priceIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    String supplier = cursor.getString(supplierIndex);
                    byte[] imageBytes = cursor.getBlob(imageIndex);

                    // Set data to TextViews
                    tvOutputProductId.setText(productId);  // Set the actual product ID
                    tvOutputProductName.setText(productName);
                    tvOutputSupplierDetails.setText(supplier);
                    tvOutputPrice.setText(String.format("RM %.2f", price));  // Formatting the price to 2 decimal places
                    etQuantity.setText(String.valueOf(quantity));

                    // Set the product image if available
                    if (imageBytes != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imgProduct.setImageBitmap(bitmap);
                    }
                } else {
                    Toast.makeText(this, "Some columns are missing in the database.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No products found with the provided ID!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error fetching product details.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void updateQuantityAndPrice() {
        // Get the new quantity from EditText
        String quantityText = etQuantity.getText().toString().trim();

        // Validate input
        if (quantityText.isEmpty()) {
            Toast.makeText(this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        int newQuantity = Integer.parseInt(quantityText);

        // Get current product ID (this can be fetched from the UI or from the database cursor)
        String productId = tvOutputProductId.getText().toString();

        // Update quantity in the database
        boolean isUpdated = databaseHelper.updateProductQuantity(productId, newQuantity);

        if (isUpdated) {
            // Recalculate price based on the new quantity
            Cursor cursor = null;
            try {
                cursor = databaseHelper.getProductById(productId);
                if (cursor != null && cursor.moveToFirst()) {
                    // Ensure that the column index is valid before fetching data
                    int priceIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_3);
                    if (priceIndex != -1) {
                        double pricePerUnit = cursor.getDouble(priceIndex);
                        double updatedPrice = pricePerUnit * newQuantity;  // Calculate total price based on new quantity
                        tvOutputPrice.setText(String.format("RM %.2f", updatedPrice));  // Display updated price
                        etQuantity.setText(String.valueOf(newQuantity));  // Update quantity on UI
                    } else {
                        Toast.makeText(this, "Price column not found in the database.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Product not found.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error updating quantity and price.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            Toast.makeText(this, "Quantity updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update quantity.", Toast.LENGTH_SHORT).show();
        }
    }
}
