package com.example.mini_project;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class view_inventory extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView tvOutputProductId, tvOutputProductName, tvOutputSupplierDetails, tvOutputPrice, tvOutputQuantity;
    private ImageView imgProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        // Initialize views
        tvOutputProductId = findViewById(R.id.tvOutputProductId);
        tvOutputProductName = findViewById(R.id.tvOutputProductName);
        tvOutputSupplierDetails = findViewById(R.id.tvOutputSupplierDetails);
        tvOutputPrice = findViewById(R.id.tvOutputPrice);
        tvOutputQuantity = findViewById(R.id.tvOutputQuantity);
        imgProduct = findViewById(R.id.imgProduct);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Fetch product details when image is clicked
        imgProduct.setOnClickListener(view -> {
            fetchProductDetails();
        });
    }

    private void fetchProductDetails() {
        // Query the database to get all products (or you can query a specific one)
        Cursor cursor = databaseHelper.getAllProducts();

        // If there are products available, display the first one (or customize logic for selection)
        if (cursor != null && cursor.moveToFirst()) {
            String productId = "";
            String productName = "";
            double price = 0.0;
            int quantity = 0;
            String supplier = "";

            // Check if the columns exist and then retrieve the values
            int productIdIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_1);
            int productNameIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_2);
            int priceIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_3);
            int quantityIndex = cursor.getColumnIndex(DatabaseHelper.PRODUCTS_COL_4);
            int supplierIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SUPPLIER);

            if (productIdIndex != -1) {
                productId = cursor.getString(productIdIndex);
            }

            if (productNameIndex != -1) {
                productName = cursor.getString(productNameIndex);
            }

            if (priceIndex != -1) {
                price = cursor.getDouble(priceIndex);
            }

            if (quantityIndex != -1) {
                quantity = cursor.getInt(quantityIndex);
            }

            if (supplierIndex != -1) {
                supplier = cursor.getString(supplierIndex);
            }

            // Set data to TextViews
            tvOutputProductId.setText(productId);
            tvOutputProductName.setText(productName);
            tvOutputSupplierDetails.setText(supplier);
            tvOutputPrice.setText(String.valueOf(price));
            tvOutputQuantity.setText(String.valueOf(quantity));

            // Optionally, close cursor
            cursor.close();
        } else {
            Toast.makeText(this, "No products found!", Toast.LENGTH_SHORT).show();
        }
    }
}
