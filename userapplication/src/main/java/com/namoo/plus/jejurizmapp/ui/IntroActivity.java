package com.namoo.plus.jejurizmapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.namoo.plus.jejurizmapp.R;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 * Created by lee on 2016. 6. 29..
 */
public class IntroActivity extends AppCompatActivity {

    Timer mTimer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        mTimer.schedule(task, 2000);
    }

    TimerTask task = new TimerTask() {
        public void run() {
            try {
                Intent i = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
