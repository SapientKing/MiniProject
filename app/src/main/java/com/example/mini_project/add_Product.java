package com.example.mini_project;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class add_Product extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText editID, editName, editPrice, editQuantity, editSupplier;
    Button btnInsert;
    ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);  // Linking to your add_product.xml layout

        // Initialize UI components
        dbHelper = new DatabaseHelper(this);
        editID = findViewById(R.id.EditPID);
        editName = findViewById(R.id.editName);
        editPrice = findViewById(R.id.editprice);
        editQuantity = findViewById(R.id.editQuantity);
        editSupplier = findViewById(R.id.editsupplier);
        btnInsert = findViewById(R.id.btnInsert);
        productImage = findViewById(R.id.Picture);

        // Set onClickListener for Insert button
        btnInsert.setOnClickListener(v -> {
            String productID = editID.getText().toString().trim();
            String name = editName.getText().toString().trim();
            String priceStr = editPrice.getText().toString().trim();
            String quantityStr = editQuantity.getText().toString().trim();
            String supplier = editSupplier.getText().toString().trim();

            if (TextUtils.isEmpty(productID) || TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) ||
                    TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(supplier)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse price and quantity to double and int respectively
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            // Add the product to the database
            boolean result = insertProduct(productID, name, price, quantity, supplier);

            if (result) {
                Toast.makeText(this, "Product Added Successfully!", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "Error Adding Product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to insert product data into the database
    private boolean insertProduct(String productID, String name, double price, int quantity, String supplier) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", productID);
        contentValues.put("NAME", name);
        contentValues.put("PRICE", price);
        contentValues.put("QUANTITY", quantity);
        contentValues.put("SUPPLIER", supplier);

        long result = db.insert("products", null, contentValues);
        db.close();

        return result != -1;
    }

    // Method to clear input fields after a successful insertion
    private void clearFields() {
        editID.setText("");
        editName.setText("");
        editPrice.setText("");
        editQuantity.setText("");
        editSupplier.setText("");
    }
}
