package com.namoo.plus.jejurizmapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.JPreference;
import com.namoo.plus.jejurizmapp.ui.dialog.ExplanationDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.restraurant_imageview)
    public ImageView mRestraurant_imageView;

    @BindView(R.id.museum_imageview)
    public ImageView mMuseum_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.activity_intro_main_title);

        if(!JPreference.getInstance(this).getReadExplanation()) {
            new ExplanationDialog().show(getSupportFragmentManager(), "");
        }
    }

    @OnClick(R.id.restraurant_imageview)
    public void clickCamera() {
        Intent i = new Intent(this, NewCameraActivity.class);
        i.putExtra("menu","restraurant");
        startActivity(i);
    }
    @OnClick(R.id.museum_imageview)
    public void clickCamera2(){
        Intent i = new Intent(this, NewCameraActivity.class);
        i.putExtra("menu","museum");
        startActivity(i);
    }

}
