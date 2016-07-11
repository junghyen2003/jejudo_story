package com.namooplus.jejurizmandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.namooplus.jejurizmandroid.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HeungSun-AndBut on 2016. 6. 5..
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_camera_sign_button)
    public ImageButton mSignCamera;

    @BindView(R.id.activity_main_camera_landscape_button)
    public ImageButton mLandscapeCamera;

    @BindView(R.id.app_tool_bar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("나무소프트");

    }

    @OnClick(R.id.activity_main_camera_sign_button)
    public void signCameraClick(View v) {
        Intent i = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.activity_main_camera_landscape_button)
    public void landscapeCameraClick(View v) {
        Intent i = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(i);
    }
}
