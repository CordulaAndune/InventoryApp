package de.cordulagloge.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

/**
 * RecyclerAdapter using CursorAdapter to interact with the database
 * based on https://stackoverflow.com/a/27732748
 * <p>
 * Created by Cordula Gloge on 12/07/2018
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private Context mContext;
    private CursorAdapter mCursorAdapter;
    private ViewHolder mViewHolder;

    public BookAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.catalog_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                mViewHolder.bindCursor(cursor);
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating is done by cursor adapter
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        mViewHolder = new ViewHolder(v);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursorAdapter.swapCursor(newCursor);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.name_text_view);
            // TODO: databinding
        }

        public void bindCursor(Cursor cursor) {
            String name = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME));
            mNameTextView.setText(name);
        }

    }
}
