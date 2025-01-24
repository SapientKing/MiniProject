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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
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
                PRODUCTS_COL_6 + " BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE);
        onCreate(db);
    }


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


    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USERNAME = ? AND PASSWORD = ?", new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    public int validateUsergetid(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ID FROM users WHERE USERNAME = ? AND PASSWORD = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        try {
            if (cursor.moveToFirst()) {
                // Ensure the column "ID" exists
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
                return userId; // Return the user ID
            } else {
                return -1; // User not found
            }
        } catch (IllegalArgumentException e) {
            // Handle missing column
            e.printStackTrace();
            return -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    public boolean insertProduct(String productID, String name, double price, int quantity, String supplier, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCTS_COL_1, productID);
        values.put(PRODUCTS_COL_2, name);
        values.put(PRODUCTS_COL_3, price);
        values.put(PRODUCTS_COL_4, quantity);
        values.put(PRODUCTS_COL_5, supplier);
        values.put(PRODUCTS_COL_6, image);
        long result = db.insert(PRODUCTS_TABLE, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCTS_TABLE, null);
    }

    public Cursor getProductById(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCTS_TABLE + " WHERE " + PRODUCTS_COL_1 + " = ?", new String[]{productId});
    }

    public boolean updateProduct(String productID, String name, double price, int quantity, String supplier, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCTS_COL_2, name);
        values.put(PRODUCTS_COL_3, price);
        values.put(PRODUCTS_COL_4, quantity);
        values.put(PRODUCTS_COL_5, supplier);
        values.put(PRODUCTS_COL_6, image);
        int result = db.update(PRODUCTS_TABLE, values, PRODUCTS_COL_1 + " = ?", new String[]{productID});
        db.close();
        return result > 0;
    }

    public boolean deleteProductById(String productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(PRODUCTS_TABLE, PRODUCTS_COL_1 + " = ?", new String[]{productId});
        db.close();
        return result > 0;
    }

    public boolean updateProductQuantity(String productId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Update quantity
        values.put(PRODUCTS_COL_4, newQuantity);

        // Update the price (recalculate based on quantity if necessary)
        Cursor cursor = db.query(PRODUCTS_TABLE, new String[]{PRODUCTS_COL_3},
                PRODUCTS_COL_1 + " = ?", new String[]{productId}, null, null, null);

        double updatedPrice = 0;
        if (cursor != null && cursor.moveToFirst()) {
            // Ensure the column index is valid before accessing the price column
            int priceIndex = cursor.getColumnIndex(PRODUCTS_COL_3);
            if (priceIndex != -1) {
                double pricePerUnit = cursor.getDouble(priceIndex);
                updatedPrice = pricePerUnit * newQuantity;
            } else {
                // Handle error if the column index is invalid
                Log.e("DatabaseHelper", "Price column not found for product: " + productId);
                cursor.close();
                db.close();
                return false;  // Return false if column is not found
            }
        } else {
            cursor.close();
            db.close();
            return false;  // Return false if product is not found
        }
        cursor.close();

        values.put(PRODUCTS_COL_3, updatedPrice);  // Update the price as well

        // Update product in the database
        int result = db.update(PRODUCTS_TABLE, values, PRODUCTS_COL_1 + " = ?", new String[]{productId});
        db.close();
        return result > 0;
    }

    // Method to get user details by email
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_3 + " = ?", new String[]{email});
    }


    // New method to update user details
    public boolean updateUser (int userId, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, email);
        contentValues.put(COL_4, password);

        // Update based on userId
        int result = db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0; // Return true if the update was successful
    }
    // Method to get user details by userId
    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_1 + " = ?", new String[]{String.valueOf(userId)});
    }



}
