package de.cordulagloge.android.bookstore.data;

import android.provider.BaseColumns;

/**
 * Created by Cordula Gloge on 20/06/2018
 */
public final class BookContract {

    private BookContract(){}

    /**
     * Inner class holds constants for the books database table.
     * each entry represent a book in the store
     */
    public final static class BookEntry implements BaseColumns{

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
        public final static String COLUMN_BOOK_SUPPLIER_PHONE= "phone";


    }


}
