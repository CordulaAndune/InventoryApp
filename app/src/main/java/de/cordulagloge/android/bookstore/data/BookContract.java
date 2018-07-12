package de.cordulagloge.android.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cordula Gloge on 20/06/2018
 */
public final class BookContract {

    public final static String CONTENT_AUTHORITY = "de.cordulagloge.android.bookstore";
    public final static String CONTENT_SCHEMA = "content://";
    public final static String BOOK_PATH = "books";

    private BookContract() {
    }

    /**
     * Inner class holds constants for the books database table.
     * each entry represent a book in the store
     */
    public final static class BookEntry implements BaseColumns {

        // Uri for the database for the books table
        public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEMA + CONTENT_AUTHORITY);
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, BOOK_PATH);
        /**
         * MIME type of the books table with uri:
         * {@link #CONTENT_URI}
         */
        // whole table
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + BOOK_PATH;
        // single item
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + BOOK_PATH;


        // name of the databse
        public final static String TABLE_NAME = "books";

        // column names
        // _id: unique and autoincrement
        public final static String _ID = BaseColumns._ID;

        // product name: text, non null
        public final static String COLUMN_BOOK_NAME = "product_name";

        // price of the product: text, non null
        public final static String COLUMN_BOOK_PRICE = "price";

        // quantity in store: integer, non null, default 1
        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        // supplier of product: Text
        public final static String COLUMN_BOOK_SUPPLIER = "supplier";

        // suppliers phone number: text
        public final static String COLUMN_BOOK_SUPPLIER_PHONE = "phone";


    }


}
