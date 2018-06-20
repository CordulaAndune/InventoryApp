package de.cordulagloge.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import de.cordulagloge.android.bookstore.data.BookDbHelper;
import de.cordulagloge.android.bookstore.databinding.ActivityCatalogBinding;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity {

    private static final String LOG_TAG = CatalogActivity.class.getName();
    private final String DB_NAME = "books.db";
    private final int DB_VERSION = 1;
    private BookDbHelper bookDbHelper;
    private ActivityCatalogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_catalog);
        bookDbHelper = new BookDbHelper(this, DB_NAME, DB_VERSION);
        binding.insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDummyBook();
                readWholeDataBase();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        readWholeDataBase();
    }

    /**
     * insert dummy data into database table books
     */
    private void insertDummyBook() {
        SQLiteDatabase bookDB = bookDbHelper.getWritableDatabase();

        String bookName = "The Hobbit";
        String bookPrice = "$8.99";
        int bookQuantity = 2;
        String supplier = "bookstore.com";
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, bookName);
        values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplier);

        bookDB.insert(BookEntry.TABLE_NAME, null, values);
    }

    /**
     * read complete database with all columns and rows
     */
    private void readWholeDataBase() {
        SQLiteDatabase bookDB = bookDbHelper.getReadableDatabase();
        String[] projection = new String[]{BookEntry._ID
                , BookEntry.COLUMN_BOOK_NAME
                , BookEntry.COLUMN_BOOK_PRICE
                , BookEntry.COLUMN_BOOK_QUANTITY
                , BookEntry.COLUMN_BOOK_SUPPLIER
                , BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        Cursor cursor = bookDB.query(BookEntry.TABLE_NAME
                , projection
                , null
                , null
                , null
                , null
                , null);

        try {
            binding.dataTextView.setText("Books in table: " + cursor.getCount() + "\n");
            String placeholder = " - ";
            String[] columnNames = cursor.getColumnNames();
            for (String name : columnNames) {
                binding.dataTextView.append(name + placeholder);
            }
            int indexID = cursor.getColumnIndex(BookEntry._ID);
            int indexName = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int indexPrice = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int indexQuantity = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int indexSupplier = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int indexPhone = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            while (cursor.moveToNext()) {
                String row = "\n"
                        + cursor.getString(indexID) + placeholder
                        + cursor.getString(indexName) + placeholder
                        + cursor.getString(indexPrice) + placeholder
                        + cursor.getInt(indexQuantity) + placeholder
                        + cursor.getString(indexSupplier) + placeholder
                        + cursor.getString(indexPhone);
                Log.i(LOG_TAG, row);
                binding.dataTextView.append(row);
            }
        } finally {
            cursor.close();
        }
    }
}
