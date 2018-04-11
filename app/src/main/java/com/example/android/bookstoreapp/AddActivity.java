package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookDbHelper;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class AddActivity extends AppCompatActivity {

    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mProductNameEditText = findViewById(R.id.edit_product_name);
        mProductPriceEditText = findViewById(R.id.edit_product_price);
        mProductQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierNumberEditText = findViewById(R.id.edit_supplier_number);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private void insertBook() {
        String productName = mProductNameEditText.getText().toString().trim();
        int productPrice = Integer.parseInt(mProductPriceEditText.getText().toString().trim());
        int productQuantity = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        int supplierNumber = Integer.parseInt(mSupplierNumberEditText.getText().toString().trim());

        ContentValues newBook = new ContentValues();
        newBook.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
        newBook.put(BookEntry.COLUMN_PRICE, productPrice);
        newBook.put(BookEntry.COLUMN_QUANTITY, productQuantity);
        newBook.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        newBook.put(BookEntry.COLUMN_SUPPLIER_NUMBER, supplierNumber);

        BookDbHelper mDbHelper = new BookDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, newBook);
        if(newRowId == -1){
            Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Product saved with id : " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                insertBook();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
