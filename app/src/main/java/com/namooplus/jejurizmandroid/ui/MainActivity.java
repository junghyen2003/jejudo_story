package com.namooplus.jejurizmandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.namooplus.jejurizmandroid.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by HeungSun-AndBut on 2016. 6. 5..
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_camera_sign_button)
    private ImageButton mSignCamera;

    @BindView(R.id.activity_main_camera_landscape_button)
    private ImageButton mLandscapeCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);

        Button btn = (Button)findViewById(R.id.activity_main_camera_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(i);
            }
        });
    }

}
