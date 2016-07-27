package com.namoo.plus.jejurizmapp.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.namoo.plus.jejurizmapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeungSun-AndBut on 2016. 7. 27..
 */

public class ExplanationFragment extends Fragment {

    @BindView(R.id.explanation_fragment_image)
    public ImageView imageView;

    private int number;

    public static ExplanationFragment newInstance(int num) {

        Bundle args = new Bundle();
        args.putInt("number", num);
        ExplanationFragment fragment = new ExplanationFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explanation, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        number = getArguments().getInt("number");

        switch (number) {
            case 1:
                imageView.setImageResource(R.drawable.page1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.page2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.page3);
                break;
        }

    }

}