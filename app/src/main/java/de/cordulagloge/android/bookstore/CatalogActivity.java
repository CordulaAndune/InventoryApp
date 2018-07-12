package de.cordulagloge.android.bookstore;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import de.cordulagloge.android.bookstore.data.BookProvider;
import de.cordulagloge.android.bookstore.databinding.ActivityCatalogBinding;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = CatalogActivity.class.getName();
    private static final int INVENTORY_LOADER = 0;
    private BookProvider mBookProvider;
    private ActivityCatalogBinding binding;
    private RecyclerView bookList;
    private BookAdapter bookAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_catalog);
        mBookProvider = new BookProvider();
        bookAdapter = new BookAdapter(this, null);
        binding.inventoryList.setAdapter(bookAdapter);
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
       /* binding.insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDummyBook();
                readWholeDataBase();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_all_item:
                break;
            case R.id.menu_delete_sold_out:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    /**
     * insert dummy data into database table books
     */
   /* private void insertDummyBook() {
        SQLiteDatabase bookDB = bookDbHelper.getWritableDatabase();
        // set dummy data
        String bookName = "The Hobbit";
        String bookPrice = "$8.99";
        int bookQuantity = 2;
        String supplier = "bookstore.com";
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, bookName);
        values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplier);
        // insert data into books table
        bookDB.insert(BookEntry.TABLE_NAME, null, values);
    }*/
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // show all columns from books table
        String[] projection = new String[]{BookEntry._ID
                , BookEntry.COLUMN_BOOK_NAME
                , BookEntry.COLUMN_BOOK_PRICE
                , BookEntry.COLUMN_BOOK_QUANTITY
                , BookEntry.COLUMN_BOOK_SUPPLIER
                , BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        // read all columns and rows into cursor
        Cursor cursor = mBookProvider.query(BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            bookAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }
}
