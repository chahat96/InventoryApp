package com.example.chahat.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Chahat on 6/12/2017.
 */

public final class InventoryContract {
    public static final String CONTENT_AUTHORITY = "com.example.chahat.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    private InventoryContract() {
    }

    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_COMPANY = "company";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL = "emailId";
        public final static String COLUMN_PRODUCT_SIZE = "size";
        public final static String COLUMN_PRODUCT_IMAGE = "image";

        public static final int PRODUCT_SIZE_UNKNOWN = 0;
        public static final int PRODUCT_SIZE_EXTRA_SMALL = 1;
        public static final int PRODUCT_SIZE_SMALL = 2;
        public static final int PRODUCT_SIZE_MEDIUM = 3;
        public static final int PRODUCT_SIZE_LARGE = 4;
        public static final int PRODUCT_SIZE_EXTRA_LARGE = 5;

        public static boolean isValidSize(int size) {
            if (size == PRODUCT_SIZE_UNKNOWN || size == PRODUCT_SIZE_SMALL || size == PRODUCT_SIZE_EXTRA_SMALL || size == PRODUCT_SIZE_MEDIUM || size == PRODUCT_SIZE_LARGE || size == PRODUCT_SIZE_EXTRA_LARGE) {
                return true;
            }
            return false;
        }

    }
}
