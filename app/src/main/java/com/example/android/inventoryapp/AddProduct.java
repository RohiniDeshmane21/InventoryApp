package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class AddProduct extends AppCompatActivity {

    EditText productName,price,quantity;
    ImageView back,done,otherOptions;
    private ProductDbHelper DbHelper;
    long id;
    int priceBundle,quty;
    String pName;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        DbHelper = new ProductDbHelper(this);
        final SQLiteDatabase db = DbHelper.getWritableDatabase();

        done = (ImageView)findViewById(R.id.done);
        back = (ImageView)findViewById(R.id.back);
        otherOptions = (ImageView)findViewById(R.id.moerOptions);

        productName = (EditText)findViewById(R.id.editTextName);
        price = (EditText)findViewById(R.id.editTextPrice);
        quantity = (EditText)findViewById(R.id.editTextQuantity);

        bundle = getIntent().getExtras();

        if(getIntent().hasExtra("productId"))
        {
            id = bundle.getLong("productId");
            pName = bundle.getString("productName");
            priceBundle =bundle.getInt("price");
            quty = bundle.getInt("quantity");

            productName.setText(pName);
            price.setText(String.valueOf(priceBundle));
            quantity.setText(String.valueOf(quty));

            productName.setTag(productName.getKeyListener());
            productName.setKeyListener(null);
            price.setTag(price.getKeyListener());
            price.setKeyListener(null);
            quantity.setTag(quantity.getKeyListener());
            quantity.setKeyListener(null);

            otherOptions.setVisibility(View.VISIBLE);
        }
        else
        {
            otherOptions.setVisibility(View.GONE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().removeExtra("productId");
                getIntent().removeExtra("productName");
                getIntent().removeExtra("price");
                getIntent().removeExtra("quantity");

                productName.setKeyListener((KeyListener) productName.getTag());
                quantity.setKeyListener((KeyListener) quantity.getTag());
                price.setKeyListener((KeyListener) price.getTag());

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

        otherOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(AddProduct.this, otherOptions);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Delete")) {
                             int deletedRecord = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, "_Id=?", new String[]{String.valueOf(id)});
                             Toast.makeText(AddProduct.this, "You Clicked : Delete,  with id " + deletedRecord + " Name " + productName, Toast.LENGTH_SHORT).show();

                        }

                        else if(item.getTitle().equals("Order"))
                        {
                            String productDetails = "Product Name : = "+ pName + "\n Product Price : =  "+ priceBundle + "\n Product Quantity : = "+ quty;
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
                            i.putExtra(Intent.EXTRA_SUBJECT, "Order Product");
                            i.putExtra(Intent.EXTRA_TEXT   , productDetails);
                            try {
                                startActivity(Intent.createChooser(i, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(AddProduct.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                            //Toast.makeText(AddProduct.this, "You Clicked : Order,  with id " + id + " Name " + productName, Toast.LENGTH_SHORT).show();
                        }
                        else if(item.getTitle().equals("Edit"))
                        {
                            productName.setKeyListener((KeyListener) productName.getTag());
                            quantity.setKeyListener((KeyListener) quantity.getTag());
                            price.setKeyListener((KeyListener) price.getTag());
                        }

                    return true;
                    }
            });
                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method
    }

    private void insertHabit()
    {
        SQLiteDatabase db = DbHelper.getWritableDatabase();

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

        if(getIntent().hasExtra("productId"))
        {
                id = bundle.getLong("productId");

                // Insert a new Product into the provider, returning the content URI for the new pet.
                getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI, values, "_Id=?", new String[]{String.valueOf(id)});
                Toast.makeText(this, "Row Updated succesfully",
                        Toast.LENGTH_SHORT).show();

        }
        else
        {
            // Insert a new Product into the provider, returning the content URI for the new pet.
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
        }

    }
}