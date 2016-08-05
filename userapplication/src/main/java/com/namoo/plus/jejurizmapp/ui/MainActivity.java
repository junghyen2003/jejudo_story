package com.namoo.plus.jejurizmapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.JPreference;
import com.namoo.plus.jejurizmapp.ui.dialog.ExplanationDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_menu_camera)
    public LinearLayout mMenuCamera;

    @BindView(R.id.activity_main_menu_restaurant)
    public LinearLayout mMenuRestaurant;

    @BindView(R.id.activity_main_menu_tour)
    public LinearLayout mMenuTour;

    @BindView(R.id.activity_main_menu_shopping)
    public LinearLayout mMenuShopping;

    @BindView(R.id.activity_main_menu_restroom)
    public LinearLayout mMenuRestRoom;

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

    @OnClick(R.id.activity_main_menu_restaurant)
    public void clickRestaurant() {
        //Intent i = new Intent(this, SearchRestaurantActivity.class);
        //startActivity(i);
    }
    @OnClick(R.id.activity_main_menu_tour)
    public void clickTour() {

    }
    @OnClick(R.id.activity_main_menu_shopping)
    public void clickShopping() {

    }
    @OnClick(R.id.activity_main_menu_restroom)
    public void clickRestroom() {

    }
    @OnClick(R.id.activity_main_menu_camera)
    public void clickCamera() {
        Intent i = new Intent(this, NewCameraActivity.class);
        startActivity(i);
    }

}
