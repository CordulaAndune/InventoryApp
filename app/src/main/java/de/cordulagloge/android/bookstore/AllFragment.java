package de.cordulagloge.android.bookstore;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import de.cordulagloge.android.bookstore.data.BookContract;
import de.cordulagloge.android.bookstore.databinding.ListFragmentsBinding;
import de.cordulagloge.android.bookstore.databinding.CatalogListHeaderBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;
    private ListFragmentsBinding binding;
    private BookAdapter bookAdapter;

    public AllFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.list_fragments, container, false);
        setListView();
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        return binding.getRoot();
    }

    /**
     * Set Listview and {@link #bookAdapter}
     * onItemClick: open EditorActivity for clicked item
     */
    private void setListView() {
        bookAdapter = new BookAdapter(getContext(), null, 0);
        binding.inventoryList.setAdapter(bookAdapter);
        binding.inventoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent editorIntent = new Intent(getContext(), EditorActivity.class);
                editorIntent.setData(ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id));
                startActivity(editorIntent);
            }
        });
        binding.inventoryList.setEmptyView(binding.emptyLayout);
        CatalogListHeaderBinding headerBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.catalog_list_header,
                binding.inventoryList,
                false);
        binding.inventoryList.addHeaderView(headerBinding.getRoot(), null, false);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // show all columns from books table
        String[] projection = new String[]{BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY};

        return new CursorLoader(getContext(),
                BookContract.BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // change Cursor
        bookAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }
}

