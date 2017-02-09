package com.example.gucheng.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gucheng on 2017/1/24.
 */

public class GcContract {

    public GcContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.gucheng.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    public static class GcEntry implements BaseColumns{

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);


        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_GC_NAME = "name";
        public final static String COLUMN_GC_STOCK = "stock";
        public final static String COLUMN_GC_SALE = "sale";
        public final static String COLUMN_GC_PRICE = "price";
        public final static String COLUMN_GC_IMAGE = "image";
    }
}
