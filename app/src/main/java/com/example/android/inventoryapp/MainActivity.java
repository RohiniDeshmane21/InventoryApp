package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
   // Cursor cursor;
    private ListView lvItems;
    private static final int PRODUCT_LOADER = 0;
    private ProductCursorAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find ListView to populate
        lvItems= (ListView) findViewById(R.id.listViewProduct);

        //set up adapter and attache to list view
        pAdapter = new ProductCursorAdapter(this,null);
        lvItems.setAdapter(pAdapter);

        //kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProduct = new Intent(MainActivity.this, AddProduct.class);
                startActivity(addProduct);
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent addProduct = new Intent(MainActivity.this, AddProduct.class);
                //create bundle
                Bundle bundle = new Bundle();
                Cursor cur = (Cursor) parent.getItemAtPosition(position);
                bundle.putLong("productId", cur.getInt(cur.getColumnIndex(ProductContract.ProductEntry._ID)));
                bundle.putString("productName", cur.getString(cur.getColumnIndex(ProductContract.ProductEntry.PRODUCT_NAME)));
                bundle.putInt("price", cur.getInt(cur.getColumnIndex(ProductContract.ProductEntry.PRICE)));
                bundle.putInt("quantity", cur.getInt(cur.getColumnIndex(ProductContract.ProductEntry.QUANTITY)));
                bundle.putByteArray("photo",cur.getBlob(cur.getColumnIndex(ProductContract.ProductEntry.PHOTO)));
                addProduct.putExtras(bundle);
                startActivity(addProduct);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.PRODUCT_NAME,
                ProductContract.ProductEntry.QUANTITY,
                ProductContract.ProductEntry.PHOTO,
                ProductContract.ProductEntry.PRICE };
        return new CursorLoader(this, ProductContract.ProductEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pAdapter.swapCursor(data);

        int cnt = lvItems.getCount();
        View emptyView = findViewById(R.id.empty_view);

        if(lvItems.getCount() == 0)
        {
            // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
            emptyView.setVisibility(View.VISIBLE);
        }
        else
            emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        pAdapter.swapCursor(null);
    }
}