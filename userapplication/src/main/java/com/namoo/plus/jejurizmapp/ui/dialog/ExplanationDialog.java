package com.namoo.plus.jejurizmapp.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.JPreference;
import com.namoo.plus.jejurizmapp.ui.fragment.ExplanationFragment;
import com.namoo.plus.jejurizmapp.ui.view.DialogViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeungSun-AndBut on 2016. 7. 27..
 */

public class ExplanationDialog extends DialogFragment {

    @BindView(R.id.dialog_explanation_checkbox)
    public CheckBox mCbNoAgain;

    @BindView(R.id.dialog_explanation_viewpager)
    public DialogViewPager mViewPager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_explanation, container);

        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onViewCreated(final View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //임시 페이지 추가
        pagerAdapter.addFrag(ExplanationFragment.newInstance(1));
        pagerAdapter.addFrag(ExplanationFragment.newInstance(2));
        pagerAdapter.addFrag(ExplanationFragment.newInstance(3));

        mViewPager.saveAdapter(pagerAdapter);

        mCbNoAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                JPreference.getInstance(getActivity()).setReadExplanation();
                dismiss();
            }
        });


    }
        //PagerAdapter Class define
    public class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
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
            return null;
        }
    }


}