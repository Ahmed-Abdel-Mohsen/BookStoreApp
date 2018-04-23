package com.example.android.bookstoreapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.R;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;

import java.util.zip.Inflater;

/**
 * Created by ahmed on 4/22/2018.
 */

public class BookCursorAdapter extends CursorAdapter {
    private int position = 0;
    private ViewHolder viewHolder;

    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup viewGroup) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        viewHolder = new ViewHolder();
        viewHolder.name = convertView.findViewById(R.id.book_name);
        viewHolder.price = convertView.findViewById(R.id.book_price);
        viewHolder.quantity = convertView.findViewById(R.id.book_quantity);
        viewHolder.sale = convertView.findViewById(R.id.sale);
        viewHolder.sale.setId(cursor.getPosition() + 1);
        viewHolder.sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity;
                int id;
                Cursor temp = context.getContentResolver().query(BookEntry.CONTENT_URI, null, null,
                        null, null, null);

                if (temp.move(view.getId())) {
                    quantity = temp.getInt(temp.getColumnIndex(BookEntry.COLUMN_QUANTITY));
                    id = temp.getInt(temp.getColumnIndex(BookEntry._ID));
                    if (quantity != 0) {
                        quantity--;
                        Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                        ContentValues saveBook = new ContentValues();
                        saveBook.put(BookEntry.COLUMN_PRODUCT_NAME, temp.getString(temp.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME)));
                        saveBook.put(BookEntry.COLUMN_PRICE, temp.getString(temp.getColumnIndex(BookEntry.COLUMN_PRICE)));
                        saveBook.put(BookEntry.COLUMN_QUANTITY, quantity);
                        saveBook.put(BookEntry.COLUMN_SUPPLIER_NAME, temp.getString(temp.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME)));
                        saveBook.put(BookEntry.COLUMN_SUPPLIER_NUMBER, temp.getString(temp.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER)));
                        context.getContentResolver().update(uri, saveBook, null, null);
                    }
                }
                temp.close();
            }
        });
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        viewHolder = (ViewHolder) view.getTag();

        TextView nameTextView = view.findViewById(R.id.book_name);
        TextView priceTextView = view.findViewById(R.id.book_price);
        TextView quantityTextView = view.findViewById(R.id.book_quantity);
        Button saleButton = view.findViewById(R.id.sale);

        String currentBookName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        String currentBookPrice = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRICE)) + "$";
        String currentBookQuantity = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));

        viewHolder.name.setText(currentBookName);
        viewHolder.price.setText(currentBookPrice);
        viewHolder.quantity.setText(currentBookQuantity);

        /*
        nameTextView.setText(currentBookName);
        priceTextView.setText(currentBookPrice);
        quantityTextView.setText(currentBookQuantity);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView quantity = view.findViewById(R.id.quantity_text_view);

            }
        });*/

    }

    static class ViewHolder {
        private TextView name;
        private TextView price;
        private TextView quantity;
        private Button sale;
    }
}
