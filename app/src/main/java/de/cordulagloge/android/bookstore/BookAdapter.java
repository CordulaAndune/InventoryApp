package de.cordulagloge.android.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.text.TextUtils;
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
public class BookAdapter extends CursorAdapter {

    private final Context mContext;
    private Uri mUri;

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
            String quantityString = String.valueOf(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY)));
            binding.quantityTextView.setText(quantityString);
            binding.sellButton.setTag(cursor.getString(cursor.getColumnIndex(BookEntry._ID)));
            binding.sellButton.setText(R.string.buton_sell);
            if (!TextUtils.isEmpty(quantityString)) {
                int quantity = Integer.parseInt(quantityString);
                // enable button only if quantity > 0
                if (quantity <= 0) {
                    binding.sellButton.setEnabled(false);
                } else {
                    binding.sellButton.setEnabled(true);
                }
            }
            // onclickListener for sell button: after click decrease quantity by 1
            binding.sellButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = Integer.parseInt(view.getTag().toString());
                    mUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                    int oldQuantity = Integer.parseInt(binding.quantityTextView.getText().toString());
                    if (oldQuantity > 0) {
                        int newQuantity = oldQuantity - 1;
                        binding.quantityTextView.setText(String.valueOf(newQuantity));
                        if (newQuantity <= 0) {
                            view.setEnabled(false);
                        }
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
}
