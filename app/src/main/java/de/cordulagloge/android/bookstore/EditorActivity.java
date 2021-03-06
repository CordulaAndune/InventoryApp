package de.cordulagloge.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        // get item uri if item should be changed
        Intent intent = getIntent();
        dataUri = intent.getData();
        setToolbar();
        setQuantityCounter();
        isItemChanged = false;
        if (dataUri != null) {
            getLoaderManager().initLoader(DATA_LOADER, null, this);
        } else {
            orderItem();
            setupTextWatcher();
        }
    }

    /**
     * set Toolbar as actionbar and add up button
     */
    private void setToolbar() {
        setSupportActionBar((Toolbar) binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            // set Title
            if (dataUri == null) {
                actionBar.setTitle(R.string.title_new_item);
            } else {
                actionBar.setTitle(R.string.title_edit_item);
            }
        }
    }

    /**
     * order Items by phone if supplier phone nr is available
     */
    private void orderItem() {
        final String phoneNr = binding.phoneEditText.getText().toString().trim();
        if (phoneNr.length() > 0) {
            binding.orderButton.setEnabled(true);
            binding.orderButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    saveItem();
                    Intent phoneIntent = new Intent();
                    phoneIntent.setType(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + phoneNr));
                    if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(phoneIntent);
                    }
                }
            });
        } else {
            binding.orderButton.setEnabled(false);
        }
    }

    /**
     * set up whole layout:
     * Quantity to 1
     * Decrement + increment button
     */
    private void setQuantityCounter() {
        binding.quantityEditText.setText("1");
        binding.decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = binding.quantityEditText.getText().toString();
                if (!TextUtils.isEmpty(quantityString)) {
                    int quantity = Integer.parseInt(quantityString);
                    // do not decrement to a negative value
                    if (quantity >= 1) {
                        quantity--;
                        binding.quantityEditText.setText(String.valueOf(quantity));
                    } else {
                        Toast.makeText(EditorActivity.this, R.string.msg_quantity_not_decremented, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        binding.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = binding.quantityEditText.getText().toString();
                if (!TextUtils.isEmpty(quantityString)) {
                    int quantity = Integer.parseInt(quantityString);
                    quantity++;
                    binding.quantityEditText.setText(String.valueOf(quantity));
                }
            }
        });
    }

    /**
     * listen for text changes
     */
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
                confirmDeletion();
                break;
            case R.id.menu_save_item:
                if (saveItem()) {
                    finish();
                }
                break;
            case android.R.id.home:
                if (isItemChanged) {
                    // show dialog if data was changed but not saved
                    showUnsavedDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    });
                } else {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isItemChanged) {
            // show dialog if data was changed but not saved
            showUnsavedDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Ask if unsaved changes should be discarded
     *
     * @param positiveListener DialogInterface.OnClickListener for positive button
     */
    private void showUnsavedDialog(DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_discard_unsaved);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok, positiveListener);
        builder.create().show();
    }

    /**
     * Delete current shown item from database
     */
    private void deleteItem() {
        if (dataUri != null) {
            int deletedRows = getContentResolver().delete(dataUri, null, null);
            if (deletedRows != -1) {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item not deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /**
     * Confirm deletion of item
     */
    private void confirmDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_delete_item);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * save inserted data as new item or update existing
     */
    private boolean saveItem() {
        // get values
        String name = binding.nameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.msg_valid_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        String priceString = binding.priceEditText.getText().toString().trim().replace(",", ".");
        double price;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        } else {
            Toast.makeText(this, R.string.mag_valid_price, Toast.LENGTH_SHORT).show();
            return false;
        }
        String supplier = binding.supplierEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String quantityString = binding.quantityEditText.getText().toString().trim();
        int quantity = 1;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        // if no name, supplier, phone and price is inserted, do not save item
        if (TextUtils.isEmpty(name) &&
                TextUtils.isEmpty(supplier) &&
                TextUtils.isEmpty(phone) &&
                price == 0) {
            Toast.makeText(this, R.string.msg_enter_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        // ContentValues
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, name);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplier);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, phone);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        // if dataUri is avaiable, item exists already and have to be updated
        if (dataUri != null) {
            int newRowID = getContentResolver().update(dataUri,
                    values,
                    null,
                    null);
            if (newRowID != -1) {
                isItemChanged = false;
                Toast.makeText(this, R.string.msg_item_changed, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, R.string.msg_item_not_changed, Toast.LENGTH_SHORT).show();
                return false;
            }
            // else a new item should be inserted into database
        } else {
            Uri newRowUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newRowUri != null) {
                isItemChanged = false;
                Toast.makeText(this, R.string.msg_item_saved, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(this, R.string.msg_item_not_saved, Toast.LENGTH_SHORT).show();
                return false;
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
            // if cursor is empty, show empty EditorActivity
            return;
        } else {
            cursor.moveToFirst();
            binding.nameEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME)));
            binding.priceEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE)));
            binding.supplierEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER)));
            binding.phoneEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)));
            binding.quantityEditText.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY)));
        }
        setupTextWatcher();
        orderItem();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        binding.nameEditText.setText("");
        binding.priceEditText.setText("");
        binding.supplierEditText.setText("");
        binding.phoneEditText.setText("");
        binding.quantityEditText.setText("1");
    }

    /**
     * TextWatcher for all EditText views to register if data was changed
     */
    private class CustomTextWatcher implements TextWatcher {

        private final View mView;

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
                    // if phone number was changed, check if order button have to be enabled/disabled
                    orderItem();
                    break;
                case R.id.quantity_edit_text:
                    break;
            }
        }
    }
}
