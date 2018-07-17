package de.cordulagloge.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import de.cordulagloge.android.bookstore.databinding.ActivityEditorBinding;

import static de.cordulagloge.android.bookstore.data.BookContract.BookEntry;

/**
 * TextInputLayout: based on Tutorial on https://www.androidhive.info/2015/09/android-material-design-floating-labels-for-edittext/
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ActivityEditorBinding binding;
    private Uri dataUri;
    private boolean isItemChanged;
    private final static int DATA_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_editor);
        Intent intent = getIntent();
        dataUri = intent.getData();
        binding.quantityEditText.setText("1");
        setupTextWatcher();
        isItemChanged = false;
        if (dataUri != null) {
            getLoaderManager().initLoader(DATA_LOADER, null, this);
        }
    }

    private void setupTextWatcher() {
        binding.nameEditText.addTextChangedListener(new CustomTextWatcher(binding.nameEditText));
        binding.priceEditText.addTextChangedListener(new CustomTextWatcher(binding.priceEditText));
        binding.supplierEditText.addTextChangedListener(new CustomTextWatcher(binding.supplierEditText));
        binding.phoneEditText.addTextChangedListener(new CustomTextWatcher(binding.phoneEditText));
        binding.quantityEditText.addTextChangedListener(new CustomTextWatcher(binding.quantityEditText));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_delete_item:
                // TODO: delete Item
                break;
            case R.id.menu_save_item:
                savePet();
                break;
            case R.id.menu_settings:
                //TODO:settings open
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePet() {
        String name = binding.nameEditText.getText().toString().trim();
        String priceString = binding.priceEditText.getText().toString().trim().replace(",", ".");
        double price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        }
        String supplier = binding.supplierEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String quantityString = binding.quantityEditText.getText().toString().trim();
        int quantity = 1;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        if (TextUtils.isEmpty(name) &&
                TextUtils.isEmpty(supplier) &&
                TextUtils.isEmpty(phone) &&
                price == 0) {
            return;
        }
        // ContentValues
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, name);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplier);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, phone);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        if (dataUri != null) {
            int newRowID = getContentResolver().update(dataUri,
                    values,
                    null,
                    null);
            if (newRowID != -1) {
                Toast.makeText(this, R.string.msg_item_changed, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.msg_item_not_changed, Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri newRowUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newRowUri != null) {
                Toast.makeText(this, R.string.msg_item_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.msg_item_not_saved, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE,
                BookEntry.COLUMN_BOOK_QUANTITY};
        return new CursorLoader(this,
                dataUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // show item data into UI
        if (cursor == null || cursor.getCount() < 1) {
            return;
        } else {
            cursor.moveToFirst();
            binding.nameEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME)));
            binding.priceEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE)));
            binding.supplierEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER)));
            binding.phoneEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)));
            binding.quantityEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        binding.nameEditText.setText("");
        binding.priceEditText.setText("");
        binding.supplierEditText.setText("");
        binding.phoneEditText.setText("");
        binding.quantityEditText.setText("1");
    }

    private class CustomTextWatcher implements TextWatcher {

        private View mView;

        public CustomTextWatcher(View view) {
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            isItemChanged = true;
            switch (mView.getId()) {
                case R.id.name_edit_text:
                    break;
                case R.id.price_edit_text:
                    break;
                case R.id.supplier_edit_text:
                    break;
                case R.id.phone_edit_text:
                    break;
                case R.id.quantity_edit_text:
                    break;
            }
        }
    }
}
