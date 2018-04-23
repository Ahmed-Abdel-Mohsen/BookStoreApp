package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

/**
 * Created by ahmed on 4/10/2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "book.db";
    public static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "( "
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + BookEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT(0),"
                + BookEntry.COLUMN_QUANTITY + " INTEGER,"
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL,"
                + BookEntry.COLUMN_SUPPLIER_NUMBER + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
