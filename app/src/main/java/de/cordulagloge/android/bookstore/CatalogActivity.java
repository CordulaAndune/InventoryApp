package de.cordulagloge.android.bookstore;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.cordulagloge.android.bookstore.data.BookContract;
import de.cordulagloge.android.bookstore.data.BookDbHelper;

import static de.cordulagloge.android.bookstore.data.BookContract.*;

public class CatalogActivity extends AppCompatActivity {

    private final String DB_NAME = "books.db";
    private final int DB_VERSION = 1;
    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        bookDbHelper = new BookDbHelper(this, DB_NAME,DB_VERSION);
    }

    /**
     * insert dummy data into database table books
     */
    private void insertDummyBook(){
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
}
