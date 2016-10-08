package com.example.android.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.R;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO: Fill out this method and return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);

    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        final int[] qtyArr = new int[1];
        final Bitmap bitmap = null;

        // TODO: Fill out this method
        // Find fields to populate in inflated template
        final TextView txproductName = (TextView) view.findViewById(R.id.textViewProductName);
        final TextView txprice = (TextView) view.findViewById(R.id.textViewPrice);
        final TextView[] txquantity = {(TextView) view.findViewById(R.id.textViewQuantity)};
        final ImageView img = (ImageView)view.findViewById(R.id.imageView);
        final Button sale = (Button)view.findViewById(R.id.btnSale);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.PRODUCT_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.QUANTITY));
        final double price = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.PRICE));
        byte[] imgbyte = cursor.getBlob(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.PHOTO));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[1024 *32];
        Bitmap bm = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length, options);
        // Populate fields with extracted properties
        txproductName.setText(name);
        txquantity[0].setText(String.valueOf(quantity));
        txprice.setText(String.valueOf(price));
        img.setImageBitmap(bm);

        final View viewNw = view;
        final TextView[] txtQty = {txquantity[0]};

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(txquantity[0].getText().toString()) > 0) {

                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent();
                    final int position = listView.getPositionForView(parentRow);

                    qtyArr[0] = Integer.parseInt(txquantity[0].getText().toString());

                    qtyArr[0] = qtyArr[0] - 1;

                    txtQty[0] = (TextView) view.findViewById(R.id.textViewQuantity);
                    txquantity[0].setText(String.valueOf(qtyArr[0]));

                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.PRODUCT_NAME, txproductName.getText().toString());
                    values.put(ProductContract.ProductEntry.QUANTITY, (qtyArr[0]));
                    values.put(ProductContract.ProductEntry.PRICE, Double.parseDouble(txprice.getText().toString()));

                    context.getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI, values, "_Id=?", new String[]{String.valueOf(position)});

                }
            }
        });
    }
}