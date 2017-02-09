package com.example.gucheng.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gucheng.inventoryapp.data.GcContract.GcEntry;

import java.io.File;
import java.util.Objects;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_LOADER = 0;

    private static final int OPEN_ALBUM = 0;    //调用相册照片
    private static final int TAKE_PHOTO = 1;    //调用相机拍照
    private static final int CROP_PHOTO = 2;    //裁剪照片

    private EditText mNameEditText;
    private EditText mStockEditText;
    private EditText mSaleEditText;
    private EditText mPriceEditText;
    private ImageView mImageView;
    private Button deleteButton;
    private Button saveButton;

    private Uri currentUri;

    private boolean mItemHasChanged = false;

    private String name;
    private int stock;
    private int sale;
    private int price;
    private Bitmap bitmap;

    private String nameString;
    private int stockInt = 0;
    private int saleInt = 0;
    private int priceInt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNameEditText = (EditText) findViewById(R.id.name_edit);
        mStockEditText = (EditText) findViewById(R.id.stock_edit);
        mSaleEditText = (EditText) findViewById(R.id.sale_edit);
        mPriceEditText = (EditText) findViewById(R.id.price_edit);

        deleteButton = (Button) findViewById(R.id.delete_button);
        saveButton = (Button) findViewById(R.id.save_button);
        mImageView = (ImageView) findViewById(R.id.pic_btn);

        Intent intent = getIntent();
        currentUri = intent.getData();
        if(currentUri == null){
            setTitle(getString(R.string.add_item));
            deleteButton.setVisibility(View.GONE);
        }else {
            setTitle(getString(R.string.edit_item));
            getLoaderManager().initLoader(EXISTING_LOADER,null,this);
        }


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mNameEditText.getText())){
                    Toast.makeText(EditorActivity.this, R.string.need_name, Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(mStockEditText.getText())) {
                    Toast.makeText(EditorActivity.this, R.string.need_stock, Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(mPriceEditText.getText())){
                    Toast.makeText(EditorActivity.this, R.string.need_price, Toast.LENGTH_SHORT).show();
                }else {
                    saveItem();
                    finish();
                }
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });
    }

    private void setImage(){
        Intent choosePic = new Intent(Intent.ACTION_PICK);
        choosePic.setType("image/*");
        startActivityForResult(choosePic,OPEN_ALBUM);
    }

    //For helping judge EditTexts if changed.
    private void setEditData(){
        nameString = mNameEditText.getText().toString().trim();
        String stockString = mStockEditText.getText().toString().trim();
        String saleString = mSaleEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(stockString)){
            stockInt = Integer.parseInt(stockString);
        }
        if(!TextUtils.isEmpty(saleString)){
            saleInt = Integer.parseInt(saleString);
        }

        if(!TextUtils.isEmpty(priceString)){
            priceInt = Integer.parseInt(priceString);
        }
    }

    private void saveItem(){
        ContentValues values = new ContentValues();
        setEditData();
        values.put(GcEntry.COLUMN_GC_NAME,nameString);
        values.put(GcEntry.COLUMN_GC_STOCK,stockInt);
        values.put(GcEntry.COLUMN_GC_SALE,saleInt);
        values.put(GcEntry.COLUMN_GC_PRICE,priceInt);

        if(currentUri == null){
            Uri mNewUri = getContentResolver().insert(GcEntry.CONTENT_URI,values);

            if(mNewUri == null){
                Toast.makeText(this, R.string.error_adding,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, R.string.new_item_saved,Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowsChanged = getContentResolver().update(currentUri,values,null,null);

            if(rowsChanged == 0){
                Toast.makeText(this, R.string.error_edit,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, R.string.item_change_saved,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteItem(){
        int mRowsDeleted = getContentResolver().delete(currentUri,null,null);

        if(mRowsDeleted == 0){
            Toast.makeText(this,"Error with deleting item.",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Item deleted.",Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    //Show a alarm when unsaved status.
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //delete confirm.
    public void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        setEditData();
        mItemHasChanged = !(Objects.equals(nameString, "") && stockInt == 0 && saleInt == 0 && priceInt == 0)
                && (!nameString.equals(name) || stockInt != stock || saleInt != sale || priceInt != price);

        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.order_more:
                Intent intentSend = new Intent();
                intentSend.setAction(Intent.ACTION_SEND);
                intentSend.putExtra(Intent.EXTRA_TEXT,getString(R.string.order_more)+ name);
                intentSend.setType("text/plain");
                if(intentSend.resolveActivity(getPackageManager()) != null){
                    startActivity(intentSend);
                }
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                GcEntry._ID,
                GcEntry.COLUMN_GC_NAME,
                GcEntry.COLUMN_GC_STOCK,
                GcEntry.COLUMN_GC_SALE,
                GcEntry.COLUMN_GC_PRICE
        };
        return new CursorLoader(this,currentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() < 1){
            return;
        }
        if(cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(GcEntry.COLUMN_GC_NAME);
            int stockColumnIndex = cursor.getColumnIndex(GcEntry.COLUMN_GC_STOCK);
            int saleColumnIndex = cursor.getColumnIndex(GcEntry.COLUMN_GC_SALE);
            int priceColumnIndex = cursor.getColumnIndex(GcEntry.COLUMN_GC_PRICE);

            name = cursor.getString(nameColumnIndex);
            stock = cursor.getInt(stockColumnIndex);
            sale = cursor.getInt(saleColumnIndex);
            price = cursor.getInt(priceColumnIndex);

            mNameEditText.setText(name);
            mStockEditText.setText(Integer.toString(stock));
            mSaleEditText.setText(Integer.toString(sale));
            mPriceEditText.setText(Integer.toString(price));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mStockEditText.setText(null);
        mSaleEditText.setText(null);
        mPriceEditText.setText(null);
    }
}
