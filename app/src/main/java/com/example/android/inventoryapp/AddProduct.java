package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

import java.io.ByteArrayOutputStream;

public class AddProduct extends AppCompatActivity {

    EditText productName,price,quantity,purchaseQuantity;
    Button order;
    ImageView back,done,otherOptions,addPhoto;
    private ProductDbHelper DbHelper;
    long id;
    int priceBundle,quty;
    String pName;
    Bundle bundle;
    public static final int RESULT_GALLERY = 0;
    private final int requestCode = 20;
    byte[] photoPath;

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
        otherOptions = (ImageView)findViewById(R.id.moerOptions);
        addPhoto = (ImageView)findViewById(R.id.imageView);
        productName = (EditText)findViewById(R.id.editTextName);
        price = (EditText)findViewById(R.id.editTextPrice);
        quantity = (EditText)findViewById(R.id.editTextQuantity);
        purchaseQuantity = (EditText)findViewById(R.id.editTextPurchaseQuantity);
        order = (Button)findViewById(R.id.button);

        purchaseQuantity.setVisibility(View.GONE);
        order.setVisibility(View.GONE);

        bundle = getIntent().getExtras();

        if(getIntent().hasExtra("productId"))
        {
            id = bundle.getLong("productId");
            pName = bundle.getString("productName");
            priceBundle =bundle.getInt("price");
            quty = bundle.getInt("quantity");
            photoPath = bundle.getByteArray("photo");

            productName.setText(pName);
            price.setText(String.valueOf(priceBundle));
            quantity.setText(String.valueOf(quty));
            addPhoto.setImageBitmap(BitmapFactory.decodeByteArray(photoPath, 0, photoPath.length));
            bitmap = BitmapFactory.decodeByteArray(photoPath, 0, photoPath.length);

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
            bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_warning_black_36dp);
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
                insertProduct();
            }
        });

        addPhoto .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(AddProduct.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo"))
                        {
                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(photoCaptureIntent, requestCode);
                        }
                        else if (options[item].equals("Choose from Gallery"))
                        {
                            Intent galleryIntent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent , RESULT_GALLERY );
                        }
                        else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
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

                            new AlertDialog.Builder(AddProduct.this)
                                    .setTitle("Delete entry")
                                    .setMessage("Are you sure you want to delete this product?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, "_Id=?", new String[]{String.valueOf(id)});

                                            Toast.makeText(AddProduct.this, "Product deleted Successfully", Toast.LENGTH_SHORT).show();
                                            Intent addProduct = new Intent(AddProduct.this, MainActivity.class);
                                            startActivity(addProduct);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .show();
                      }

                        else if(item.getTitle().equals("Purchase"))
                        {
                            purchaseQuantity.setVisibility(View.VISIBLE);
                            order.setVisibility(View.VISIBLE);

                            order.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                  //  getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI,"_Id=?", new String[]{String.valueOf(id),})
                                    ContentValues values = new ContentValues();
                                    values.put(ProductContract.ProductEntry.PRODUCT_NAME,productName.getText().toString());
                                    values.put(ProductContract.ProductEntry.QUANTITY, (Integer.parseInt(quantity.getText().toString()) - Integer.parseInt(purchaseQuantity.getText().toString())));
                                    values.put(ProductContract.ProductEntry.PRICE,Double.parseDouble(price.getText().toString()));

                                    ByteArrayOutputStream bos=new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);//153*204
                                    img=bos.toByteArray();

                                    values.put(ProductContract.ProductEntry.PHOTO,img);

                                    id = bundle.getLong("productId");
                                    // Insert a new Product into the provider, returning the content URI for the new pet.
                                    getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI, values, "_Id=?", new String[]{String.valueOf(id)});
                                    //Toast.makeText(this, "Product Updated succesfully",Toast.LENGTH_SHORT).show();
                                  //  Intent addProduct = new Intent(AddProduct.this, MainActivity.class);
                                    //startActivity(addProduct);

                                    String productDetails = "Product Name : = " + pName + "\n Product Price : =  " + priceBundle + "\n Product Total Quantity : = " + quty
                                                                +"\n Product Purchase Quantity : = "+ purchaseQuantity.getText();
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("message/rfc822");
                                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
                                    i.putExtra(Intent.EXTRA_SUBJECT, "Order Product");
                                    i.putExtra(Intent.EXTRA_TEXT, productDetails);
                                    try {
                                        startActivity(Intent.createChooser(i, "Send mail..."));
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(AddProduct.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

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

    Bitmap bitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(picturePath), 100, 100);

            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            bitmap = (Bitmap)data.getExtras().get("data");
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);

        }
    }

    private byte[] img=null;

    private void insertProduct()
    {

        ContentValues values = new ContentValues();

        if(productName.getText().toString().matches(""))
        {
            Toast.makeText(this, "Product Name is compulsary",
                    Toast.LENGTH_SHORT).show();
        }
        else if(quantity.getText().toString().matches(""))
        {
            Toast.makeText(this, "Quantity is compulsary",
                    Toast.LENGTH_SHORT).show();
        }
        else if(price.getText().toString().matches(""))
        {
            Toast.makeText(this, "Price Name is compulsary",
                    Toast.LENGTH_SHORT).show();
        }
        else if(addPhoto.getDrawable() == null)
        {
            Toast.makeText(this, "Image is compulsary",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            values.put(ProductContract.ProductEntry.PRODUCT_NAME,productName.getText().toString());
            values.put(ProductContract.ProductEntry.QUANTITY, Integer.parseInt(quantity.getText().toString()));
            values.put(ProductContract.ProductEntry.PRICE,Double.parseDouble(price.getText().toString()));

           // Bitmap b=BitmapFactory.decodeResource(getResources(), R.drawable.ic_warning_black_36dp);
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);//153*204
            img=bos.toByteArray();

            values.put(ProductContract.ProductEntry.PHOTO,img);

            if(getIntent().hasExtra("productId"))
            {
                id = bundle.getLong("productId");
                // Insert a new Product into the provider, returning the content URI for the new pet.
                getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI, values, "_Id=?", new String[]{String.valueOf(id)});
                Toast.makeText(this, "Product Updated succesfully",
                        Toast.LENGTH_SHORT).show();
                Intent addProduct = new Intent(AddProduct.this, MainActivity.class);
                startActivity(addProduct);
            }
            else
            {
                // Insert a new Product into the provider, returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, "Product adding failed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, "Product added sucessfully",
                            Toast.LENGTH_SHORT).show();
                    Intent addProduct = new Intent(AddProduct.this, MainActivity.class);
                    startActivity(addProduct);
                }
            }
        }
    }
}