package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                // .setAction("Action", null).show();
                Intent addProduct = new Intent(MainActivity.this, AddProduct.class);
                startActivity(addProduct);
            }
        });

        //displayDatabaseInfo();
    }

    public void displayDatabaseInfo()
    {
       // ProductDbHelper mDbHelper = new ProductDbHelper(this);
       // SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String productDataToShow = "Product Data";

        String[] projection = { ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.PRODUCT_NAME,
                ProductContract.ProductEntry.QUANTITY,
                ProductContract.ProductEntry.PRICE };
        //Cursor cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, null, null, null,
          //      null, null, null);

        Cursor cursor = getContentResolver().query(ProductContract.ProductEntry.CONTENT_URI,projection,null,null,null);


        try {

            TextView productData = (TextView)findViewById(R.id.textViewMsg);

            int idColomIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
            int productNameColomIndex = cursor.getColumnIndex(ProductContract.ProductEntry.PRODUCT_NAME);
            int productTimeColomIndex = cursor.getColumnIndex(ProductContract.ProductEntry.PRICE);
            int productQuantity = cursor.getColumnIndex(ProductContract.ProductEntry.QUANTITY);

            while (cursor.moveToNext())
            {
                int currentId = cursor.getInt(idColomIndex);
                String currentHabit = cursor.getString(productNameColomIndex);
                String days = cursor.getString(productTimeColomIndex);
                String quantity = cursor.getString(productQuantity);
                productDataToShow = productDataToShow + "\n\n" +currentId + ".  "+"Product : "+ currentHabit+", Quantity : "+quantity +". Price : "+days;
            }
            productData.setText(productDataToShow);
        }
        finally {
            cursor.close();
        }
    }
}
