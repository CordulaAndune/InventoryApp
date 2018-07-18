package de.cordulagloge.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

/**
 * RecyclerAdapter using CursorAdapter to interact with the database
 * based on https://stackoverflow.com/a/27732748 & https://www.blogc.at/2015/10/13/recyclerview-adapters-part-2-recyclerview-cursor-adapter/
 * <p>
 * Created by Cordula Gloge on 12/07/2018
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private Context mContext;
    // private CursorAdapter mCursorAdapter;
    private Cursor mCursor;
    private CustomOnItemClickListener mItemClickListener;
    private ViewHolder mViewHolder;

    public BookAdapter(Context context, Cursor cursor, CustomOnItemClickListener itemClickListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mCursor = cursor;
        /*mCursorAdapter = new CursorAdapter(mContext, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.catalog_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                mViewHolder.bindCursor(cursor, mItemClickListener);
            }
        };*/
    }

    protected CursorAdapter mCursorAdapter = new CursorAdapter(mContext, mCursor, 0) {
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.catalog_list_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            mViewHolder.bindCursor(cursor, mItemClickListener);
        }
    };

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        mCursorAdapter.getCursor().moveToPosition(position);
        Cursor cursor = mCursorAdapter.getCursor();
        mCursorAdapter.bindView(null, mContext, cursor);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating is done by cursor adapter
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        mViewHolder = new ViewHolder(v);
        return mViewHolder;
    }

    public Cursor getItem(final int position) {
        return (Cursor) mCursorAdapter.getItem(position);
    }


    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    /**
     * change Cursor of CursorAdapter and notify to change list items
     *
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return;
        }
        mCursor = newCursor;
        mCursorAdapter.swapCursor(newCursor);
        if (newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
    }

    public interface CustomOnItemClickListener {
        void onItemClick(long id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTextView,
                mPriceTextView,
                mQuantityTextView;
        View mParentView;
        private long mId;


        public ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.name_text_view);
            mPriceTextView = itemView.findViewById(R.id.price_text_view);
            mQuantityTextView = itemView.findViewById(R.id.quantity_text_view);
            mParentView = itemView;
        }

        public void bindCursor(Cursor cursor, final CustomOnItemClickListener listener) {
            mNameTextView.setText("");
            mPriceTextView.setText("");
            mQuantityTextView.setText(""); //cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY)));

            mNameTextView.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME)));
            mPriceTextView.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE)));
            mQuantityTextView.setText(String.valueOf(cursor.getLong(cursor.getColumnIndex(BookEntry._ID)))); //cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY)));
            mId = cursor.getLong(cursor.getColumnIndex(BookEntry._ID));
            Log.i("MID:", String.valueOf(mId));
            mParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecyclerView root = (RecyclerView) view.getParent();
                    final int position = root.getChildLayoutPosition(mParentView);
                    Cursor cursor = (Cursor) mCursorAdapter.getItem(position);
                    Log.i("MID:", String.valueOf(cursor.getLong(cursor.getColumnIndex(BookEntry._ID))));
                    listener.onItemClick(cursor.getLong(cursor.getColumnIndex(BookEntry._ID)));
                }
            });
        }

    }
}
