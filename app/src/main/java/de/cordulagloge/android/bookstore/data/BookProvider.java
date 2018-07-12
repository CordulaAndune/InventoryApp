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

import static de.cordulagloge.android.bookstore.data.BookContract.BOOK_PATH;
import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;
import static de.cordulagloge.android.bookstore.data.BookContract.CONTENT_AUTHORITY;

/**
 * Created by Cordula Gloge on 11/07/2018
 */
public class BookProvider extends ContentProvider {

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
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
