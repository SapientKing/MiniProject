package com.example.mini_project;

public class Product {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private String supplier;
    private byte[] image;

    // Constructor and Getters/Setters
    public Product(String id, String name, double price, int quantity, String supplier, byte[] image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public byte[] getImage() {
        return image;
    }
}



