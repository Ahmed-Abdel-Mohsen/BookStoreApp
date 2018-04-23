package com.example.android.bookstoreapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ahmed on 4/10/2018.
 */

public final class BookContract {

    public static final class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "book";
        public static final String _ID = "_id";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_NUMBER = "supplier_phone_number";

        public static final String PATH_BOOK = "book";
        public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOK);
    }
}
