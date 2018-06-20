package de.cordulagloge.android.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static de.cordulagloge.android.bookstore.data.BookContract.*;

/**
 * Created by Cordula Gloge on 20/06/2018
 */
public class BookDbHelper extends SQLiteOpenHelper {

    private String databaseName;
    private int versionNumber;
    private final String LOG_TAG = BookDbHelper.class.getName();

    public BookDbHelper(Context context, String name, int version) {
        super(context, name, null, version);
        databaseName = name;
        versionNumber = version;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create new database
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "("
                + BookEntry._ID + " PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_PRICE + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_QUANTITY + "INTEGER NOT NULL DEFAULT 1, "
                + BookEntry.COLUMN_BOOK_SUPPLIER + "TEXT NOT NULL DEFAULT 'n.a.', "
                + BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + "TEXT NOT NULL DEFAULT 'n.a.');";
        Log.i(LOG_TAG, SQL_CREATE_BOOKS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
