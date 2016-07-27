package com.namoo.plus.jejurizmapp.ui.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by HeungSun-AndBut on 2016. 7. 27..
 */

public class DialogViewPager extends ViewPager {
    PagerAdapter mPagerAdapter;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mPagerAdapter != null) {
            super.setAdapter(mPagerAdapter);
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
    }

    public void saveAdapter(PagerAdapter pagerAdapter) {
        mPagerAdapter = pagerAdapter;
    }

    public DialogViewPager(Context context) {
        super(context);
    }

    public DialogViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
