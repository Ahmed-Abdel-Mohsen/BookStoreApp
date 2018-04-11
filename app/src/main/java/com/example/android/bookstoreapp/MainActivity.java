package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new BookDbHelper(this);
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = new String[]{
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_NUMBER
        };

        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        try {
            TextView textView = findViewById(R.id.textview_main);
            textView.setText("The book table contains " + cursor.getCount() + " rows\n\n");
            int idIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER);
            while (cursor.moveToNext()) {
                textView.append(cursor.getString(idIndex) + "-" +
                        cursor.getString(productNameIndex) + "-" +
                        cursor.getString(priceIndex) + "-" +
                        cursor.getString(quantityIndex) + "-" +
                        cursor.getString(supplierNameIndex) + "-" +
                        cursor.getString(supplierNumberIndex) + "\n");
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void insertDummyData() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues dummyBook = new ContentValues();
        dummyBook.put(BookEntry.COLUMN_PRODUCT_NAME, "Dummy Book Name");
        dummyBook.put(BookEntry.COLUMN_PRICE, "20$");
        dummyBook.put(BookEntry.COLUMN_QUANTITY, "150");
        dummyBook.put(BookEntry.COLUMN_SUPPLIER_NAME, "Nothing");
        dummyBook.put(BookEntry.COLUMN_SUPPLIER_NUMBER, "01210757269");
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, dummyBook);
        Toast.makeText(this, "new dummy book inserted with row: " + newRowId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_dummy_data:
                insertDummyData();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
