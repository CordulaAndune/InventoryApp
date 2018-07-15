package de.cordulagloge.android.bookstore;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.cordulagloge.android.bookstore.databinding.ActivityEditorBinding;

/**
 * TextInputLayout: based on Tutorial on https://www.androidhive.info/2015/09/android-material-design-floating-labels-for-edittext/
 */

public class EditorActivity extends AppCompatActivity {

    private ActivityEditorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_editor);
        setupTextWatcher();
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
                //TODO: save item
                break;
            case R.id.menu_settings:
                //TODO:settings open
                break;
        }
        return super.onOptionsItemSelected(item);
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
