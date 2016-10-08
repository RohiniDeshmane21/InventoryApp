package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
/**
 * Created by Rupali on 01-10-2016.
 */
public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME =  "productData.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create table statement to string variable
        String SQL_CREATE_PRODUCT_TABLE ="CREATE TABLE "+ ProductEntry.TABLE_NAME + "(" +
                ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.PRODUCT_NAME +" TEXT NOT NULL, "
                + ProductEntry.QUANTITY +" INTEGER NOT NULL, "
                + ProductEntry.PURCHASE_QUANTITY+" INTEGER, "
                + ProductEntry.PHOTO + " BLOB NOT NULL, "
                + ProductEntry.PRICE +" DECIMAL NOT NULL);";
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}