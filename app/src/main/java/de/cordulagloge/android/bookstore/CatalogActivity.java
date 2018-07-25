package de.cordulagloge.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import de.cordulagloge.android.bookstore.data.BookProvider;
import de.cordulagloge.android.bookstore.databinding.ActivityCatalogBinding;
import de.cordulagloge.android.bookstore.databinding.CatalogListHeaderBinding;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = CatalogActivity.class.getName();
    private static final int INVENTORY_LOADER = 0;
    private BookProvider mBookProvider;
    private ActivityCatalogBinding binding;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_catalog);
        mBookProvider = new BookProvider();
        setToolbar();
        setListView();
        setAddButton();
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    private void setToolbar() {
        setSupportActionBar((Toolbar) binding.toolbar);
    }

    /**
     * Set up Listview and {@link #bookAdapter}
     * onItemClick: open EditorActivity for clicked item
     */
    private void setListView() {
        bookAdapter = new BookAdapter(this, null, 0);
        binding.inventoryList.setAdapter(bookAdapter);
        binding.inventoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent editorIntent = new Intent(CatalogActivity.this, EditorActivity.class);
                editorIntent.setData(ContentUris.withAppendedId(BookEntry.CONTENT_URI, id));
                startActivity(editorIntent);
            }
        });
        binding.inventoryList.setEmptyView(binding.emptyLayout);
        CatalogListHeaderBinding headerBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.catalog_list_header,
                binding.catalogRootLayout,
                false);
        binding.inventoryList.addHeaderView(headerBinding.getRoot());
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
                deleteAllItems();
                break;
            case R.id.menu_delete_sold_out:
                deleteSoldOutItems();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * delete all items in database:
     * show dialog to confirm deletion
     */
    private void deleteAllItems() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_delete_all_items);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getContentResolver().delete(BookEntry.CONTENT_URI,
                        null,
                        null);
            }
        });
        builder.create().show();
    }

    /**
     * delete sold out items (quantity == 0):
     * show AlertDialog for confirmation
     */
    private void deleteSoldOutItems() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_delete_sold_out);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selection = BookEntry.COLUMN_BOOK_QUANTITY + "=?";
                String[] selectionArgs = new String[]{"0"};
                getContentResolver().delete(BookEntry.CONTENT_URI,
                        selection,
                        selectionArgs);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
