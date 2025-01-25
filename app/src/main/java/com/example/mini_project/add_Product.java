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
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class add_Product extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText productIdInput, productNameInput, supplierInput, priceInput, quantityInput;
    private AutoCompleteTextView locationInput;
    private ImageView productImageView;
    private Bitmap selectedImageBitmap;
    private SQLiteDatabase database;
    private PlacesClient placesClient;
    private ArrayList<String> placeSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        String username = getIntent().getStringExtra("username");

        // Initialize Views
        productIdInput = findViewById(R.id.product_input);
        productNameInput = findViewById(R.id.ProName_input);
        supplierInput = findViewById(R.id.Supplier_input);
        priceInput = findViewById(R.id.price_input);
        quantityInput = findViewById(R.id.quantity_input);
        locationInput = findViewById(R.id.location_input);
        productImageView = findViewById(R.id.imageView);
        Button addButton = findViewById(R.id.btnInsert);

        // Initialize Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyD_DY-1qP_MU4KvJ5LtVAGxJc9Ol2qI9NU");
        placesClient = Places.createClient(this);

        // Initialize the list for place suggestions
        placeSuggestions = new ArrayList<>();

        // Set up TextWatcher for location input
        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = locationInput.getText().toString();
                if (query.length() > 2) {  // Trigger search after typing more than 2 characters
                    searchPlace(query);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Image Upload
        productImageView.setOnClickListener(v -> openImagePicker());

        // Insert Button
        addButton.setOnClickListener(v -> {
            if (validateInputs()) {
                insertProductToDatabase();
            }
        });
    }

    private void searchPlace(String query) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            placeSuggestions.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                placeSuggestions.add(prediction.getFullText(null).toString());
            }

            // Create an ArrayAdapter to display the suggestions
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, placeSuggestions);
            locationInput.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching places: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                //productImageView.setBackground(null);
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
                locationInput.getText().toString().isEmpty() ||
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
        values.put("location", locationInput.getText().toString()); // Add location here
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
