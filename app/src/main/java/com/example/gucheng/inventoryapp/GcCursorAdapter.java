package com.example.gucheng.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gucheng.inventoryapp.data.GcContract.GcEntry;

/**
 * Created by gucheng on 2017/1/24.
 */

class GcCursorAdapter extends CursorAdapter {

    GcCursorAdapter(Context context,Cursor c){
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvStock = (TextView) view.findViewById(R.id.stock);
        TextView tvPrice = (TextView) view.findViewById(R.id.price);
        Button button = (Button) view.findViewById(R.id.sale_btn);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(GcEntry.COLUMN_GC_NAME));
        String count = cursor.getString(cursor.getColumnIndexOrThrow(GcEntry.COLUMN_GC_SALE));
        String stock = cursor.getString(cursor.getColumnIndexOrThrow(GcEntry.COLUMN_GC_STOCK));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(GcEntry.COLUMN_GC_PRICE));
        final int position = cursor.getPosition();

        tvName.setText(name);
        tvStock.setText("Stocks: " + stock + " ,Sale Times: " + count);
        tvPrice.setText(price);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), R.string.sold,Toast.LENGTH_SHORT).show();
                cursor.moveToPosition(position);
                String nameString = cursor.getString(cursor.getColumnIndexOrThrow(GcEntry.COLUMN_GC_NAME));
                int stockInt = cursor.getInt(cursor.getColumnIndexOrThrow(GcEntry.COLUMN_GC_STOCK));
                int saleInt = cursor.getInt(cursor.getColumnIndexOrThrow(GcEntry.COLUMN_GC_SALE));
                int priceInt = cursor.getInt(cursor.getColumnIndexOrThrow(GcEntry.COLUMN_GC_PRICE));
                if (stockInt > 0){
                    stockInt--;
                    saleInt++;
                }
                ContentValues cv = new ContentValues();
                Uri uri = ContentUris.withAppendedId(GcEntry.CONTENT_URI,position+1);
                cv.put(GcEntry.COLUMN_GC_NAME,nameString);
                cv.put(GcEntry.COLUMN_GC_STOCK,stockInt);
                cv.put(GcEntry.COLUMN_GC_SALE,saleInt);
                cv.put(GcEntry.COLUMN_GC_PRICE,priceInt);
                context.getContentResolver().update(uri,cv,null,null);
            }
        });
    }
}
