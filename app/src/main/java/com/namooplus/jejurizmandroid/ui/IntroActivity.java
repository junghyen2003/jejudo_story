package com.namooplus.jejurizmandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.namooplus.jejurizmandroid.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Intent i = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(i);
    }
}
