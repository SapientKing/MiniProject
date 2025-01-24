package com.example.mini_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onViewProductClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product details
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("Price: RM%.2f", product.getPrice()));
        holder.productQuantity.setText("Quantity: " + product.getQuantity());

        // Decode the image byte array and compress it
        if (product.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
            byte[] compressedImage = compressImage(bitmap); // Compress the image
            holder.productImage.setImageBitmap(bitmap);

            // Handle item click
            holder.viewProductButton.setOnClickListener(v -> {
                // Create an Intent to navigate to ActivityViewInventory
                Intent intent = new Intent(context, view_inventory.class);

                // Pass product details to the ViewInventoryActivity
                intent.putExtra("product_id", product.getId());
                intent.putExtra("product_name", product.getName());
                intent.putExtra("product_price", product.getPrice());
                intent.putExtra("product_quantity", product.getQuantity());
                intent.putExtra("product_supplier", product.getSupplier());
                intent.putExtra("product_image", compressedImage);  // Pass compressed image

                // Start ActivityViewInventory
                context.startActivity(intent);
            });
        } else {
            holder.productImage.setImageResource(R.drawable.product_pic);
        }
    }

    // Method to compress the image
    private byte[] compressImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream); // Compress image to 50% quality
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity;
        Button viewProductButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            viewProductButton = itemView.findViewById(R.id.btnViewProduct);
        }
    }
}
