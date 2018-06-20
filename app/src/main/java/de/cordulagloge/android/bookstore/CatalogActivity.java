package de.cordulagloge.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import javax.xml.transform.Templates;

import de.cordulagloge.android.bookstore.data.BookDbHelper;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity {

    private final String DB_NAME = "books.db";
    private final int DB_VERSION = 1;
    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCatalogBinding binding = Data setContentView(R.layout.activity_catalog);
        bookDbHelper = new BookDbHelper(this, DB_NAME, DB_VERSION);

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
        TextView dataTextView = findViewById(R.id.data_text_view);
        try {
            while(cursor.moveToNext()){

            }
        }
        finally {
            cursor.close();
        }
    }
}
