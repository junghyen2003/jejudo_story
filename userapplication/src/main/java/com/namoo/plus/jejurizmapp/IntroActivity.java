package com.namoo.plus.jejurizmapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lee on 2016. 6. 29..
 */
public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.intro_image)
    public ImageView mIntroImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(IntroActivity.this);

        
    }
}
