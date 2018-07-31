package de.cordulagloge.android.bookstore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.cordulagloge.android.bookstore.data.BookContract;
import de.cordulagloge.android.bookstore.databinding.ActivityCatalogBinding;

public class CatalogActivity extends AppCompatActivity {

    private ActivityCatalogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_catalog);
        setToolbar();
        setViewPager();
        setAddButton();
    }

    private void setViewPager() {
        binding.viewPager
              .setAdapter(new SimpleFragmentPageAdapter(getSupportFragmentManager(), this));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    /**
     * set Toolbar as actionbar
     */
    private void setToolbar() {
        setSupportActionBar((Toolbar) binding.toolbar);
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
                // delete items with quantity == 0
                deleteSoldOutItems();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * delete all items in database:
     * show dialog to confirm deletion, negative button: close dialog, positive button: delete items
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
                getContentResolver().delete(BookContract.BookEntry.CONTENT_URI,
                        null,
                        null);
            }
        });
        builder.create().show();
    }


    /**
     * delete sold out items (quantity == 0):
     * show AlertDialog for confirmation, negative button: close item, positive button: delete sold out items
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
                String selection = BookContract.BookEntry.COLUMN_BOOK_QUANTITY + "=?";
                String[] selectionArgs = new String[]{"0"};
                getContentResolver().delete(BookContract.BookEntry.CONTENT_URI,
                        selection,
                        selectionArgs);
            }
        });
        builder.create().show();
    }

}
