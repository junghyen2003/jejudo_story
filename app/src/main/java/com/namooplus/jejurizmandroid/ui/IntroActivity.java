package com.namooplus.jejurizmandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.common.Utils;

public class IntroActivity extends AppCompatActivity {

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent i = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (!Utils.isSDCARDMounted()) {
            Toast.makeText(IntroActivity.this, R.string.activity_intro_no_mounted, Toast.LENGTH_SHORT).show();
        }

    }
}
