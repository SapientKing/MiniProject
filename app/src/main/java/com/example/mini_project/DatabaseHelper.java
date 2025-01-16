package com.example.mini_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserDatabase.db";
    public static final String TABLE_NAME = "users";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "USERNAME";
    public static final String COL_3 = "EMAIL";
    public static final String COL_4 = "PASSWORD";

    // Products Table
    public static final String PRODUCTS_TABLE = "products";
    public static final String PRODUCTS_COL_1 = "ID";
    public static final String PRODUCTS_COL_2 = "NAME";
    public static final String PRODUCTS_COL_3 = "PRICE";
    public static final String PRODUCTS_COL_4 = "QUANTITY";
    public static final String COLUMN_SUPPLIER = "SUPPLIER";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, EMAIL TEXT, PASSWORD TEXT)");
        db.execSQL("CREATE TABLE " + PRODUCTS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, PRICE REAL, QUANTITY INTEGER, SUPPLIER TEXT)");
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
        return result != -1;
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USERNAME = ? AND PASSWORD = ?", new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
    public boolean insertProduct(String productID, String name, double price, int quantity, String supplier) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Insert the product data into the ContentValues
        values.put(PRODUCTS_COL_1, productID);
        values.put(PRODUCTS_COL_2, name);
        values.put(PRODUCTS_COL_3, price);
        values.put(PRODUCTS_COL_4, quantity);
        values.put(COLUMN_SUPPLIER, supplier);

        // Insert the data into the products table and get the row ID
        long result = db.insert(PRODUCTS_TABLE, null, values);
        db.close();  // Close the database connection

        return result != -1;  // Return true if insertion is successful, false otherwise
    }

    // Fetch all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCTS_TABLE, null);
    }

    // Delete a product by ID
    public boolean deleteProductById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(PRODUCTS_TABLE, PRODUCTS_COL_1 + "=?", new String[]{id});
        db.close();
        return result > 0;
    }
}




