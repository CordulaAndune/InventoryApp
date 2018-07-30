package de.cordulagloge.android.bookstore;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Cordula Gloge on 30/07/2018
 */
public class SimpleFragmentPageAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles;

    public SimpleFragmentPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        tabTitles = new String[]{
                context.getString(R.string.tab_all_items)
        };
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllFragment();
            default:
                return new AllFragment();
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
