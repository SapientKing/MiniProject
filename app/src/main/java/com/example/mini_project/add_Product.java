package com.example.mini_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class add_Product extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText productIdInput, productNameInput, supplierInput, priceInput, quantityInput;
    private ImageView productImageView;
    private Bitmap selectedImageBitmap;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Views
        productIdInput = findViewById(R.id.product_input);
        productNameInput = findViewById(R.id.ProName_input);
        supplierInput = findViewById(R.id.Supplier_input);
        priceInput = findViewById(R.id.price_input);
        quantityInput = findViewById(R.id.quantity_input);
        productImageView = findViewById(R.id.imageView);
        Button addButton = findViewById(R.id.btnInsert);

        // Initialize Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Image Upload
        productImageView.setOnClickListener(v -> openImagePicker());

        // Insert Button
        addButton.setOnClickListener(v -> {
            if (validateInputs()) {
                insertProductToDatabase();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                productImageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs() {
        if (productIdInput.getText().toString().isEmpty() ||
                productNameInput.getText().toString().isEmpty() ||
                supplierInput.getText().toString().isEmpty() ||
                priceInput.getText().toString().isEmpty() ||
                quantityInput.getText().toString().isEmpty() ||
                selectedImageBitmap == null) {
            Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void insertProductToDatabase() {
        ContentValues values = new ContentValues();
        values.put("product_id", productIdInput.getText().toString());
        values.put("product_name", productNameInput.getText().toString());
        values.put("supplier_details", supplierInput.getText().toString());
        values.put("price_per_unit", Double.parseDouble(priceInput.getText().toString()));
        values.put("quantity", Integer.parseInt(quantityInput.getText().toString()));
        values.put("product_image", getBitmapAsByteArray(selectedImageBitmap));

        long result = database.insert("products", null, values);
        if (result != -1) {
            Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
            gotoviewaction();
        } else {
            Toast.makeText(this, "Error Adding Product", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private void gotoviewaction(){
        Intent intent = new Intent(add_Product.this, ViewAction.class);
        startActivity(intent);
    }
}
