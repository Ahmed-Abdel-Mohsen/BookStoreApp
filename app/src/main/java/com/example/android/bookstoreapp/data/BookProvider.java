package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

/**
 * Created by ahmed on 4/22/2018.
 */

public class BookProvider extends ContentProvider {

    private BookDbHelper mDbHelper;
    private static final int BOOKS = 1;
    private static final int BOOK_ID = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookEntry.CONTENT_AUTHORITY, BookEntry.PATH_BOOK, BOOKS);
        sUriMatcher.addURI(BookEntry.CONTENT_AUTHORITY, BookEntry.PATH_BOOK + "/#", BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(BookEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long rowId = database.insert(BookEntry.TABLE_NAME, null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int rowDeleted;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        switch (match) {
            case BOOKS:
                rowDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                rowDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Pet requires valid gender");

        }
    }

    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowUpdated = database.update(BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }
}
