package com.namooplus.jejurizmandroid.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.adapter.ImageListAdapter;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeungSun-AndBut on 2016. 7. 12..
 */

public class CameraListActivity extends AppCompatActivity {

    @BindView(R.id.app_tool_bar)
    public Toolbar toolbar;

    @BindView(R.id.activity_camera_list_grid_view)
    public GridView mGridView;


    private ArrayList<ImageInfoModel> mImageList;
    private ImageListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_list);

        mImageList = getIntent().getParcelableArrayListExtra("datas");

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("사진 정보 편집");

        mAdapter = new ImageListAdapter(mImageList, this);

        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("HS", "onItemClick : " + position);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_image_list, menu);

        MenuItem mMenu1 = menu.getItem(0);
        MenuItem mMenu2 = menu.getItem(1);

        mMenu1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(CameraListActivity.this, "삭제모드", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mMenu2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(CameraListActivity.this, "글쓰기 모드", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
