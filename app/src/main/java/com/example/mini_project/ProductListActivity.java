package com.example.mini_project;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private List<Product> productList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procuct_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        productList = fetchProducts();

        ProductAdapter adapter = new ProductAdapter(this, productList, product -> {
            // Handle "View Product" button click
            Toast.makeText(this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);
    }

    private List<Product> fetchProducts() {
        List<Product> products = new ArrayList<>();
        Cursor cursor = dbHelper.getAllProducts();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2); // Price per unit
                int quantity = cursor.getInt(3); // Quantity available
                String supplier = cursor.getString(4);
                byte[] image = cursor.getBlob(5);

                // Calculate the total price (price * quantity) for the product
                double totalPrice = price * quantity;

                // Add the product with the total price
                products.add(new Product(id, name, totalPrice, quantity, supplier, image));
            }
            cursor.close();
        }
        return products;
    }
}

