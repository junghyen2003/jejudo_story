package com.namooplus.jejurizmandroid.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.adapter.ImageListAdapter;
import com.namooplus.jejurizmandroid.common.AppSetting;
import com.namooplus.jejurizmandroid.common.Utils;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HeungSun-AndBut on 2016. 7. 12..
 */

public class CameraListActivity extends AppCompatActivity {

    @BindView(R.id.app_tool_bar)
    public Toolbar toolbar;

    @BindView(R.id.activity_camera_list_grid_view)
    public GridView mGridView;

    @BindView(R.id.activity_camera_list_bottom)
    public LinearLayout mLlBottom;

    @BindView(R.id.activity_camera_list_button_ok)
    public Button mBtnOk;

    @BindView(R.id.activity_camera_list_button_cancle)
    public Button mBtnCancle;

    private ArrayList<ImageInfoModel> mImageList;
    private ImageListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_list);

        mImageList = getIntent().getParcelableArrayListExtra("datas");

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_camera_list_title_bar));

        mAdapter = new ImageListAdapter(mImageList, CameraListActivity.this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mImageList.get(position).isChecked()) {
                    mImageList.get(position).setChecked(false);
                } else {
                    mImageList.get(position).setChecked(true);
                }

                mAdapter.notifyDataSetChanged();
                mAdapter.notifyDataSetInvalidated();
                //mGridView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mImageList.isEmpty()) {
            checkDialog();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mImageList.isEmpty()) {
                    checkDialog();
                }
                break;
        }
        return true;
    }

    private void checkDialog() {
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setTitle("현재 사진은 삭제 됩니다.")
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.deleteFile(mImageList);
                        mImageList.clear();
                        Intent intent = new Intent();
                        intent.putExtra("datas", mImageList);
                        setResult(AppSetting.ACTIVITY_CODE_CAMERA_LIST, intent);
                        finish(); //확인버튼 누루면 앱 종료
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_image_list, menu);

        MenuItem mMenu1 = menu.getItem(0);
        MenuItem mMenu2 = menu.getItem(1);

        mMenu1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder db = new AlertDialog.Builder(CameraListActivity.this);
                db.setTitle("추가 모드")
                        .setMessage("추가 사진 작업을 진행합니다.")
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra("datas", mImageList);
                                setResult(AppSetting.ACTIVITY_CODE_CAMERA_LIST, intent);
                                finish(); //확인버튼 누루면 앱 종료
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return false;
            }
        });

        mMenu2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mLlBottom.setVisibility(View.VISIBLE);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.activity_camera_list_button_ok)
    public void onClickOkButton() {
        //체크된 정보만 넘기기
        ArrayList<ImageInfoModel> newList = new ArrayList<>();
        for(ImageInfoModel item : mImageList) {
            if(item.isChecked()) {
                newList.add(item);
            }
        }

        Log.i("HS","넘어가는 파일 갯수 : " + newList.size());
        Intent i = new Intent(this, ImageDetailActivity.class);
        i.putParcelableArrayListExtra("datas", mImageList);
        startActivityForResult(i, AppSetting.ACTIVITY_CODE_IMAGE_DETAIL);
    }

    @OnClick(R.id.activity_camera_list_button_cancle)
    public void onClickCancleButton() {
        mLlBottom.setVisibility(View.GONE);
    }
}
