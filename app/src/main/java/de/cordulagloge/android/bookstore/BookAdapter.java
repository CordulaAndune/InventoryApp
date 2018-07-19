package de.cordulagloge.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import de.cordulagloge.android.bookstore.databinding.CatalogListItemBinding;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

/**
 * CursorAdapter to interact with the database
 * based on https://stackoverflow.com/a/27732748 & https://www.blogc.at/2015/10/13/recyclerview-adapters-part-2-recyclerview-cursor-adapter/
 * <p>
 * Created by Cordula Gloge on 12/07/2018
 */
public class BookAdapter extends CursorAdapter {

    private static final String LOG_TAG = BookAdapter.class.getName();
    private Context mContext;

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
        CatalogListItemBinding binding = DataBindingUtil.bind(view);
        if (binding != null) {
            binding.nameTextView.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME)));
            binding.priceTextView.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE)));
            binding.quantityTextView.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY))));
            binding.sellButton.setTag(cursor.getString(cursor.getColumnIndex(BookEntry._ID)));
            binding.sellButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(LOG_TAG, "Button click registered." + view.getTag());
                    /*mContext.getContentResolver().query(uri,
                            new String[]{BookEntry.COLUMN_BOOK_QUANTITY},
                            null,
                            null,
                            null);*/
                }
            });
        }
    }

}
