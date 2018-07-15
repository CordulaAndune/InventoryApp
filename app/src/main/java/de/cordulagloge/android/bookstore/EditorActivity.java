package de.cordulagloge.android.bookstore;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.cordulagloge.android.bookstore.databinding.ActivityEditorBinding;

public class EditorActivity extends AppCompatActivity {

    private ActivityEditorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_editor);

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
}
