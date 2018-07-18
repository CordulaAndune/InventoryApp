package de.cordulagloge.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
        setRecyclerView();
        setAddButton();
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    /**
     * Set up RecyclerView with horizontal dividers and {@link #bookAdapter}
     * onItemClick: open EditorActivity for clicked item
     */
    private void setRecyclerView() {
        bookAdapter = new BookAdapter(this, null, new BookAdapter.CustomOnItemClickListener() {
            @Override
            public void onItemClick(long id) {
                Intent editorIntent = new Intent(CatalogActivity.this, EditorActivity.class);
                editorIntent.setData(ContentUris.withAppendedId(BookEntry.CONTENT_URI, id));
                startActivity(editorIntent);
            }
        });
        binding.inventoryList.setLayoutManager(new LinearLayoutManager(this));
        binding.inventoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.inventoryList.setAdapter(bookAdapter);
    }

    /**
     * use floating action button to open editorActivity for adding new item
     */
    private void setAddButton() {
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CatalogActivity.this, EditorActivity.class));
            }
        });
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
                // testing
                insertDummyBook();
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
    private void insertDummyBook() {
        // set dummy data
        String bookName = "The Hobbit";
        double bookPrice = 1;
        int bookQuantity = 2;
        String supplier = "bookstore.com";
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, bookName);
        values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplier);
        // insert data into books table
        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // show all columns from books table
        String[] projection = new String[]{BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY};

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // change Cursor
        bookAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }
}
