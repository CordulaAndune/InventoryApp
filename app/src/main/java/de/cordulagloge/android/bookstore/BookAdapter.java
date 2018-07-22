package de.cordulagloge.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Toast;

import de.cordulagloge.android.bookstore.databinding.CatalogListItemBinding;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

/**
 * CursorAdapter to interact with the database
 * based on https://stackoverflow.com/a/27732748 & https://www.blogc.at/2015/10/13/recyclerview-adapters-part-2-recyclerview-cursor-adapter/
 * <p>
 * Created by Cordula Gloge on 12/07/2018
 */
public class BookAdapter extends CursorAdapter implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = BookAdapter.class.getName();
    private Context mContext;
    private Uri mUri;
    private final static int QUANTITY_LOADER = 1;

    public BookAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        mContext = context;
       }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.catalog_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final CatalogListItemBinding binding = DataBindingUtil.bind(view);
        if (binding != null) {
            binding.nameTextView.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME)));
            binding.priceTextView.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE)));
            binding.quantityTextView.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY))));
            binding.sellButton.setTag(cursor.getString(cursor.getColumnIndex(BookEntry._ID)));
            binding.sellButton.setText(R.string.buton_sell);
            binding.sellButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = Integer.parseInt(view.getTag().toString());
                    mUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                    int oldQuantity = Integer.parseInt(binding.quantityTextView.getText().toString());
                    if(oldQuantity > 0) {
                        int newQuantity = oldQuantity - 1;
                        binding.quantityTextView.setText(String.valueOf(newQuantity));
                        ContentValues values = new ContentValues();
                        values.put(BookEntry.COLUMN_BOOK_QUANTITY, newQuantity);
                        mContext.getContentResolver().update(mUri,
                                values,
                                null,
                                null);
                    } else {
                        Toast.makeText(mContext, "No item to sell.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{BookEntry._ID,
                BookEntry.COLUMN_BOOK_QUANTITY};

        return new CursorLoader(mContext,
                mUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        // decrease quantity by 1
        int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY));
        quantity--;

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
