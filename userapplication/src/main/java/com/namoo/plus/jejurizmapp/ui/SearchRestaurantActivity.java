package com.namoo.plus.jejurizmapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.Constants;
import com.namoo.plus.jejurizmapp.ui.fragment.MenuSelectFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeungSun-AndBut on 2016. 7. 27..
 */

public class SearchRestaurantActivity extends AppCompatActivity {

    CharSequence[] titles = new CharSequence[]{"첫번째","두번째"};
    //검색
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    @BindView(R.id.vpPager)
    public ViewPager mViewPager;
    @BindView(R.id.tabs)
    public TabLayout mTabLayout;
    //@BindView(R.id.app_tool_bar)
    //public Toolbar toolbar;

    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_search_restaurant_title);

        //Toolbar define
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mAdapter.addFrag(MenuSelectFragment.newInstance(Constants.SEARCH_MENU_OPTION_SELECT_RESTAURANT_KIND));
        mAdapter.addFrag(MenuSelectFragment.newInstance(Constants.SEARCH_MENU_OPTION_SELECT_RESTAURANT_KIND));
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mViewPager.setAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchItem = menu.getItem(0);

        mSearchView = new SearchView(getSupportActionBar().getThemedContext()) {

            @Override
            public void onActionViewCollapsed() {
                super.onActionViewCollapsed();
            }

            @Override
            public void onActionViewExpanded() {
                super.onActionViewExpanded();
            }

        };


        mSearchView.setQueryHint("검색힌트");
        //mSearchView.setOnQueryTextListener(mQueryTextListener);

        MenuItemCompat.setActionView(mSearchItem, mSearchView);
        MenuItemCompat.collapseActionView(mSearchItem);

        //항상 검색바를 활성화
        mSearchItem.expandActionView();

        return true;
    }

    //PagerAdapter Class define
    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
