package com.example.chahat.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.chahat.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Chahat on 6/12/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    String quantity;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.product_name1);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button saleButton = (Button) view.findViewById(R.id.saleItem);

        // Find the columns of inventory attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex((InventoryEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);

        // Read the inventory attributes from the Cursor for the current product
        String name = cursor.getString(nameColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);

        String price = Integer.toString(productPrice);
        if (TextUtils.isEmpty(price)) {
            price = context.getString(R.string.none_available);
        }

        quantity = Integer.toString(productQuantity);
        if (TextUtils.isEmpty(quantity)) {
            quantity = context.getString(R.string.none_available);
        }


        // Update the TextViews with the attributes for the current product
        nameTextView.setText(name);
        quantityTextView.setText(quantity);
        priceTextView.setText(price);

        final long currentItemId = cursor.getLong(idColumnIndex);
        final Context currentContext = context;
        final int currentQuantity = cursor.getInt(quantityColumnIndex);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(currentItemId, currentContext, currentQuantity);
            }
        });
    }

    public void updateQuantity(long id, Context context, int quantity) {
        if (quantity > 0) {
            quantity -= 1;
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            Uri uri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, Long.toString(id));

            context.getContentResolver().update(uri, values, null, null);
        }

    }

}

