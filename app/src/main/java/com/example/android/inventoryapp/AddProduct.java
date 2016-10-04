package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class AddProduct extends AppCompatActivity {

    EditText productName,price,quantity;
    ImageView back,done;
    private ProductDbHelper DbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        DbHelper = new ProductDbHelper(this);

        done = (ImageView)findViewById(R.id.done);
        back = (ImageView)findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addProduct = new Intent(AddProduct.this, MainActivity.class);
                startActivity(addProduct);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertHabit();
            }
        });
    }

    private void insertHabit()
    {
       // SQLiteDatabase db = DbHelper.getWritableDatabase();

        productName = (EditText)findViewById(R.id.editTextName);
        price = (EditText)findViewById(R.id.editTextPrice);
        quantity = (EditText)findViewById(R.id.editTextQuantity);

        ContentValues values = new ContentValues();

        if(productName.getText().toString().equals(null))
        {
            Toast.makeText(this, "Product Name is compulsary",
                    Toast.LENGTH_SHORT).show();
        }
        else if(quantity.getText().toString().equals(null))
        {
            Toast.makeText(this, "Quantity is compulsary",
                    Toast.LENGTH_SHORT).show();
        }
        else if(price.getText().toString().equals(null))
        {
            Toast.makeText(this, "Price Name is compulsary",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            values.put(ProductContract.ProductEntry.PRODUCT_NAME,productName.getText().toString());
            values.put(ProductContract.ProductEntry.QUANTITY, Integer.parseInt(quantity.getText().toString()));
            values.put(ProductContract.ProductEntry.PRICE,Double.parseDouble(price.getText().toString()));
        }

        //long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME,null,values);

        // Insert a new pet into the provider, returning the content URI for the new pet.
        Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Row adding failed",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Row added sucessfully",
                    Toast.LENGTH_SHORT).show();
        }

       // Toast.makeText(AddProduct.this, "Product Added Sucessfully "+newRowId,
              //  Toast.LENGTH_LONG).show();

    }

}
