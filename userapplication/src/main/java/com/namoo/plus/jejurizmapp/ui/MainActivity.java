package com.namoo.plus.jejurizmapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.JPreference;
import com.namoo.plus.jejurizmapp.ui.dialog.ExplanationDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!JPreference.getInstance(this).getReadExplanation()) {
            new ExplanationDialog().show(getSupportFragmentManager(), "");
        }

    }
}
