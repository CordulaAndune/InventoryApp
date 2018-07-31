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

import de.cordulagloge.android.bookstore.databinding.CatalogListHeaderBinding;
import de.cordulagloge.android.bookstore.databinding.ListFragmentsBinding;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class InStockFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListFragmentsBinding binding;
    private BookAdapter bookAdapter;
    private final static int INVENTORY_LOADER = 0;

    public InStockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.list_fragments, container, false);
        setListView();
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        return binding.getRoot();
    }

    private void setListView() {
        bookAdapter = new BookAdapter(getContext(), null, 0);
        binding.inventoryList.setEmptyView(binding.emptyLayout);
        binding.inventoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent editIntent = new Intent(getContext(), EditorActivity.class);
                editIntent.setData(ContentUris.withAppendedId(BookEntry.CONTENT_URI, id));
                startActivity(editIntent);
            }
        });
        binding.inventoryList.setAdapter(bookAdapter);
        CatalogListHeaderBinding headerBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.catalog_list_header,
                binding.inventoryList,
                false);
        binding.inventoryList.addHeaderView(headerBinding.getRoot(), null, false);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // select item with quantity > 0
        String[] projection = new String[]{BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY};
        String selection = BookEntry.COLUMN_BOOK_QUANTITY + ">?";
        String[] selArg = new String[]{"0"};

        return new CursorLoader(getContext(),
                BookEntry.CONTENT_URI,
                projection,
                selection,
                selArg,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        bookAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }
}
