package com.example.android.bookstoreapp.data;

import android.provider.BaseColumns;

/**
 * Created by ahmed on 4/10/2018.
 */

public final class BookContract {

    public static final class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "book";
        public static final String _ID = "_id";
        public static final String COLUMN_PRODUCT_NAME="product_name";
        public static final String COLUMN_PRICE="price";
        public static final String COLUMN_QUANTITY="quantity";
        public static final String COLUMN_SUPPLIER_NAME="supplier_name";
        public static final String COLUMN_SUPPLIER_NUMBER="supplier_phone_number";
    }
}
