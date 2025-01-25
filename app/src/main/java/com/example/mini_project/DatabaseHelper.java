package com.example.mini_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserDatabase.db";

    // Users Table
    public static final String TABLE_NAME = "users";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "USERNAME";
    public static final String COL_3 = "EMAIL";
    public static final String COL_4 = "PASSWORD";

    // Products Table
    public static final String PRODUCTS_TABLE = "products";
    public static final String PRODUCTS_COL_1 = "product_id";
    public static final String PRODUCTS_COL_2 = "product_name";
    public static final String PRODUCTS_COL_3 = "price_per_unit";
    public static final String PRODUCTS_COL_4 = "quantity";
    public static final String PRODUCTS_COL_5 = "supplier_details";
    public static final String PRODUCTS_COL_6 = "product_image"; // To store image as BLOB
    public static final String PRODUCTS_COL_7 = "location"; // To store location as TEXT

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3); // Updated database version to 3
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, EMAIL TEXT, PASSWORD TEXT)");
        db.execSQL("CREATE TABLE " + PRODUCTS_TABLE + " (" +
                PRODUCTS_COL_1 + " TEXT PRIMARY KEY, " +
                PRODUCTS_COL_2 + " TEXT, " +
                PRODUCTS_COL_3 + " REAL, " +
                PRODUCTS_COL_4 + " INTEGER, " +
                PRODUCTS_COL_5 + " TEXT, " +
                PRODUCTS_COL_6 + " BLOB, " +
                PRODUCTS_COL_7 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion < 3) { // Check if the database needs upgrading
            // Check if 'location' column exists
            Cursor cursor = db.rawQuery("PRAGMA table_info(" + PRODUCTS_TABLE + ")", null);
            boolean locationColumnExists = false;
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(1); // Column name is at index 1
                if (PRODUCTS_COL_7.equals(columnName)) {
                    locationColumnExists = true;
                    break;
                }
            }
            cursor.close();

            // Add the 'location' column if it doesn't exist
            if (!locationColumnExists) {
                db.execSQL("ALTER TABLE " + PRODUCTS_TABLE + " ADD COLUMN " + PRODUCTS_COL_7 + " TEXT");
                Log.d("DatabaseHelper", "Added 'location' column to products table");
            } else {
                Log.d("DatabaseHelper", "'location' column already exists in products table");
            }
        }
    }

    // Insert user data
    public boolean insertData(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, email);
        contentValues.put(COL_4, password);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    // Get user by ID
    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_1 + " = ?", new String[]{String.valueOf(userId)});
    }

    // Check if user exists
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean updateUser(int userId, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username); // Update USERNAME
        contentValues.put(COL_3, email);    // Update EMAIL
        contentValues.put(COL_4, password); // Update PASSWORD

        // Update user based on userId
        int result = db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[]{String.valueOf(userId)});
        db.close();

        // Check if update was successful
        return result > 0;
    }

    // Validate user credentials
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USERNAME = ? AND PASSWORD = ?", new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    // Validate user and get ID
    public int validateUsergetid(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ID FROM " + TABLE_NAME + " WHERE USERNAME = ? AND PASSWORD = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow(COL_1));
            } else {
                return -1; // User not found
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // Insert product data with logging
    public boolean insertProduct(String productID, String name, double price, int quantity, String supplier, byte[] image, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCTS_COL_1, productID);
        values.put(PRODUCTS_COL_2, name);
        values.put(PRODUCTS_COL_3, price);
        values.put(PRODUCTS_COL_4, quantity);
        values.put(PRODUCTS_COL_5, supplier);
        values.put(PRODUCTS_COL_6, image);
        values.put(PRODUCTS_COL_7, location);

        Log.d("DatabaseHelper", "Inserting product: " + productID);
        long result = db.insert(PRODUCTS_TABLE, null, values);

        if (result == -1) {
            Log.e("DatabaseHelper", "Error inserting product: " + productID);
        }
        db.close();
        return result != -1;
    }

    // Get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCTS_TABLE, null);
    }

    // Get product by ID
    public Cursor getProductById(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCTS_TABLE + " WHERE " + PRODUCTS_COL_1 + " = ?", new String[]{productId});
    }

    // Update product details
    public boolean updateProduct(String productID, String name, double price, int quantity, String supplier, byte[] image, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCTS_COL_2, name);
        values.put(PRODUCTS_COL_3, price);
        values.put(PRODUCTS_COL_4, quantity);
        values.put(PRODUCTS_COL_5, supplier);
        values.put(PRODUCTS_COL_6, image);
        values.put(PRODUCTS_COL_7, location);

        int result = db.update(PRODUCTS_TABLE, values, PRODUCTS_COL_1 + " = ?", new String[]{productID});
        db.close();
        return result > 0;
    }

    // Update product quantity
    public boolean updateProductQuantity(String productId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCTS_COL_4, newQuantity);

        Cursor cursor = db.query(PRODUCTS_TABLE, new String[]{PRODUCTS_COL_3}, PRODUCTS_COL_1 + " = ?", new String[]{productId}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                double pricePerUnit = cursor.getDouble(cursor.getColumnIndex(PRODUCTS_COL_3));
                values.put(PRODUCTS_COL_3, pricePerUnit * newQuantity);
            }
            cursor.close();
        }

        int result = db.update(PRODUCTS_TABLE, values, PRODUCTS_COL_1 + " = ?", new String[]{productId});
        db.close();
        return result > 0;
    }

    // Delete product by ID
    public boolean deleteProductFromDatabase(String productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(PRODUCTS_TABLE, PRODUCTS_COL_1 + " = ?", new String[]{productId});
        db.close();
        return rowsDeleted > 0;
    }
}
