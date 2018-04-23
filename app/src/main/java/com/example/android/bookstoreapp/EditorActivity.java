package com.example.android.bookstoreapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierNumberEditText;
    private TextView mQuantityTextView;
    private LinearLayout mQuantityLayout;
    private Button mIncreaseButton;
    private Button mDecreaseButton;
    private Button mOrderButton;
    private int mProductQuantity;
    private String mSupplierNumber;
    private Uri currentBookUri;
    private boolean mBookHasChanged = false;

    private static final int BOOK_LOADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductNameEditText = findViewById(R.id.edit_product_name);
        mProductPriceEditText = findViewById(R.id.edit_product_price);
        mProductQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierNumberEditText = findViewById(R.id.edit_supplier_number);
        mQuantityTextView = findViewById(R.id.quantity_text_view);
        mQuantityLayout = findViewById(R.id.quantity_layout);
        mIncreaseButton = findViewById(R.id.increase);
        mDecreaseButton = findViewById(R.id.decrease);
        mOrderButton = findViewById(R.id.order_button);

        View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mBookHasChanged = true;
                return false;
            }
        };
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNumberEditText.setOnTouchListener(mTouchListener);
        mIncreaseButton.setOnTouchListener(mTouchListener);
        mDecreaseButton.setOnTouchListener(mTouchListener);

        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductQuantity++;
                mQuantityTextView.setText(mProductQuantity + "");
            }
        });
        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProductQuantity != 0) {
                    mProductQuantity--;
                    mQuantityTextView.setText(mProductQuantity + "");
                }
            }
        });
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri callerUri = Uri.parse("tel:" + mSupplierNumber);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(callerUri);
                if (ActivityCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });
        currentBookUri = getIntent().getData();
        if (currentBookUri == null) {
            mQuantityLayout.setVisibility(View.GONE);
            mOrderButton.setVisibility(View.GONE);
            setTitle(getString(R.string.add_new_book_label));
        } else {
            mProductQuantityEditText.setVisibility(View.GONE);
            setTitle(getString(R.string.edit_book_label));
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private boolean saveBook() {
        String productName = mProductNameEditText.getText().toString().trim();
        int productPrice = 0;
        if (currentBookUri == null)
            mProductQuantity = 0;
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        mSupplierNumber = mSupplierNumberEditText.getText().toString().trim();

        //Empty Book So Nothing to insert into database
        if (TextUtils.isEmpty(productName) &&
                TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(mSupplierNumber) &&
                TextUtils.isEmpty(mProductPriceEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(mProductQuantityEditText.getText().toString().trim())) {
            return true;
        }
        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, "Enter Product Name", Toast.LENGTH_SHORT).show();
            mProductNameEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(supplierName)) {
            Toast.makeText(this, "Enter Supplier Name", Toast.LENGTH_SHORT).show();
            mSupplierNameEditText.requestFocus();
            return false;
        }
        if (!TextUtils.isEmpty(mProductPriceEditText.getText().toString().trim())) {
            productPrice = Integer.parseInt(mProductPriceEditText.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(mProductQuantityEditText.getText().toString().trim()) && currentBookUri == null) {
            mProductQuantity = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());
        }
        if (TextUtils.isEmpty(mSupplierNumber)) {
            Toast.makeText(this, "Enter Supplier Number", Toast.LENGTH_SHORT).show();
            mSupplierNumberEditText.requestFocus();
            return false;
        }


        ContentValues saveBook = new ContentValues();
        saveBook.put(BookEntry.COLUMN_PRODUCT_NAME, productName);
        saveBook.put(BookEntry.COLUMN_PRICE, productPrice);
        saveBook.put(BookEntry.COLUMN_QUANTITY, mProductQuantity);
        saveBook.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierName);
        saveBook.put(BookEntry.COLUMN_SUPPLIER_NUMBER, mSupplierNumber);
        long savedRows;
        if (currentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, saveBook);
            savedRows = ContentUris.parseId(newUri);
        } else {
            savedRows = getContentResolver().update(currentBookUri, saveBook, null, null);
        }

        if (savedRows < 1) {
            Toast.makeText(this, getString(R.string.error_with_saving_product), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.product_saved), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (saveBook()) {
                    finish();
                }
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_message));
        builder.setPositiveButton(getString(R.string.delete_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBook();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        int rowsDeleted = 0;
        if (currentBookUri != null)
            rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String projection[] = new String[]{
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_NUMBER
        };
        return new CursorLoader(this, currentBookUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0)
            return;
        cursor.moveToNext();
        String productName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        int productPrice = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_PRICE));
        mProductQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
        String supplierName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME));
        mSupplierNumber = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER));

        mProductNameEditText.setText(productName);
        mProductPriceEditText.setText(productPrice + "");
        mQuantityTextView.setText(mProductQuantity + "");
        mSupplierNameEditText.setText(supplierName);
        mSupplierNumberEditText.setText(mSupplierNumber);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText(null);
        mProductPriceEditText.setText(null);
        mQuantityTextView.setText(null);
        mSupplierNameEditText.setText(null);
        mSupplierNumberEditText.setText(null);
    }
}
