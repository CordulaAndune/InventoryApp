package de.cordulagloge.android.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import static de.cordulagloge.android.bookstore.data.BookContract.BOOK_PATH;
import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;
import static de.cordulagloge.android.bookstore.data.BookContract.CONTENT_AUTHORITY;

/**
 * Created by Cordula Gloge on 11/07/2018
 */
public class BookProvider extends ContentProvider {

    private static final String LOG_TAG = BookProvider.class.getName();
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;

    static {
        mUriMatcher.addURI(CONTENT_AUTHORITY, BOOK_PATH, BOOKS);
        mUriMatcher.addURI(CONTENT_AUTHORITY, BOOK_PATH + "/#", BOOKS_ID);
    }

    // Database name and version number
    private final String DB_NAME = "books.db";
    private final int DB_VERSION = 1;
    private BookDbHelper mBookDbHelper;

    /**
     * initialize Provider and DbHelper
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        mBookDbHelper = new BookDbHelper(getContext(), DB_NAME, DB_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = mUriMatcher.match(uri);
        SQLiteDatabase database = mBookDbHelper.getReadableDatabase();
        Cursor queryCursor;
        switch (match) {
            case BOOKS:
                queryCursor = database.query(BookEntry.TABLE_NAME,
                        projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , sortOrder);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                queryCursor = database.query(BookEntry.TABLE_NAME,
                        projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , sortOrder);
                break;
            default:
                throw new IllegalStateException("Cannot querey unknown uri " + uri);
        }
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return queryCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("No MIME type found for uri " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                try {
                    makeSanityCheck(contentValues);
                    Uri newUri = insertBook(uri, contentValues);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Invalid value: ", e);
                    return null;
                }
            default:
                throw new IllegalStateException("Unsupported uri for insertion: " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();
        long newRowID = database.insert(BookEntry.TABLE_NAME,
                null,
                contentValues);
        if (newRowID == -1) {
            return null;
        } else {
            return ContentUris.withAppendedId(uri, newRowID);
        }
    }

    private void makeSanityCheck(ContentValues contentValues) throws IllegalArgumentException {
        // check if product name is not null and valid
        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_NAME)) {
            String name = contentValues.getAsString(BookEntry.COLUMN_BOOK_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Item requires a valid name.");
            }
        }
        // check if quantity is positive or 0
        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            int quantity = contentValues.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity has to be positive.");
            }
        }
        // check if price is not null and positive
        if (contentValues.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            double price = contentValues.getAsDouble(BookEntry.COLUMN_BOOK_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Price has to be a positive value");
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mBookDbHelper.getWritableDatabase();
        int deletedRows;
        switch (mUriMatcher.match(uri)) {
            case BOOKS:
                deletedRows = database.delete(BookEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(BookEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion not supported to unknown uri " + uri);
        }
        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updatedRows;
        if (contentValues.size() == 0) {
            return -1;
        }
        switch (mUriMatcher.match(uri)) {
            case BOOKS:
                updatedRows = updateBook(contentValues, selection, selectionArgs);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                updatedRows = updateBook(contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Pet with invalid uri " + uri + " could not be saved");
        }
        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return updatedRows;
        }
        return -1;
    }

    private int updateBook(ContentValues contentValues, String selection, String[] selectionArgs) {
        try {
            makeSanityCheck(contentValues);
            SQLiteDatabase database = mBookDbHelper.getWritableDatabase();
            return database.update(BookEntry.TABLE_NAME,
                    contentValues,
                    selection,
                    selectionArgs);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Invalid value: ", e);
        }
        return 0;
    }
}
