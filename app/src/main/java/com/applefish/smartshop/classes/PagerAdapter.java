package com.applefish.smartshop.classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Ghiath on 1/6/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.numOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                LatestFragment tab1 = new LatestFragment();
                return tab1;
            case 1:
                MostViewedFragment tab2 = new MostViewedFragment();
                return tab2;
            case 2:
                StoresFragment tab3 = new StoresFragment();
                return tab3;
            default:
                return null;
        }
    }


    public int getItemPosition(Object item) {
            return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}
