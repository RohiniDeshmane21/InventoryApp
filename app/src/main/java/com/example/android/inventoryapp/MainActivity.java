package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductCursorAdapter;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Cursor cursor;
    ListView lvItems;
    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  TextView productData = (TextView)findViewById(R.id.textViewMsg);
       // productData.setVisibility(View.GONE);
        // Find ListView to populate
        lvItems= (ListView) findViewById(R.id.listViewProduct);
        View emptyView = findViewById(R.id.empty_view);
        //emptyView.setVisibility(View.GONE);

        if(lvItems.getCount() < 0)
        {
            // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
           emptyView.setVisibility(View.VISIBLE);
        }
        else
           emptyView.setVisibility(View.INVISIBLE);

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

        //set up adapter and attache to list view
        pAdapter = new ProductCursorAdapter(this,null);
        lvItems.setAdapter(pAdapter);

        //kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
    }

   /* @Override
    public void onDestroy() {
        super.onDestroy();
        ListView lv = (ListView) findViewById(R.id.listViewProduct);
        ((CursorAdapter) lv.getAdapter()).getCursor().close();
        cursor.close();
    }
*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.PRODUCT_NAME,
                ProductContract.ProductEntry.QUANTITY,
                ProductContract.ProductEntry.PRICE };

        return new CursorLoader(this, ProductContract.ProductEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        pAdapter.swapCursor(null);
    }

    public void displayDatabaseInfo()
    {
        ProductDbHelper mDbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String productDataToShow = "Product Data";

        String[] projection = { ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.PRODUCT_NAME,
                ProductContract.ProductEntry.QUANTITY,
                ProductContract.ProductEntry.PRICE };
         cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, null, null, null,
                null, null, null);

       // Cursor cursor = getContentResolver().query(ProductContract.ProductEntry.CONTENT_URI,projection,null,null,null);

        try {
            // Setup cursor adapter using cursor from last step
            ProductCursorAdapter todoAdapter = new ProductCursorAdapter(this, cursor);
            // Attach cursor adapter to the ListView
            lvItems.setAdapter(todoAdapter);

          /*  TextView productData = (TextView)findViewById(R.id.textViewMsg);

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
            productData.setText(productDataToShow);*/
        }
        finally {
           // cursor.close();
        }
    }

}