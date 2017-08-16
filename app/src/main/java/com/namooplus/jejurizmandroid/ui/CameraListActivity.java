package com.namooplus.jejurizmandroid.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.common.AppSetting;
import com.namooplus.jejurizmandroid.common.Utils;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;

import java.util.ArrayList;
import java.util.List;

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
    private ImageGalleryAdapter mAdapter;

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

        mAdapter = new ImageGalleryAdapter(CameraListActivity.this, mImageList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mImageList.get(position).isChecked()) {
                    mAdapter.getItem(position).setChecked(false);
                } else {
                    mAdapter.getItem(position).setChecked(true);
                }

                mAdapter.notifyDataSetChanged();
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
        AlertDialog.Builder db = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
        db.setTitle(R.string.activity_camera_list_dialog_delete_title)
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
                AlertDialog.Builder db = new AlertDialog.Builder(CameraListActivity.this, R.style.Theme_AppCompat_Dialog);
                db.setTitle(R.string.activity_camera_list_dialog_add_image_title)
                        .setMessage(R.string.activity_camera_list_dialog_add_image_text)
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
        for(int i = 0; i < mAdapter.getCount(); i++) {
            if(mAdapter.getItem(i).isChecked()) {
                newList.add(mAdapter.getItem(i));
            }
        }

        Intent i = new Intent(this, ImageDetailActivity.class);
        i.putParcelableArrayListExtra("datas", newList);
        startActivityForResult(i, AppSetting.ACTIVITY_CODE_IMAGE_DETAIL);
    }

    @OnClick(R.id.activity_camera_list_button_cancle)
    public void onClickCancleButton() {
        mLlBottom.setVisibility(View.GONE);
    }

    public class ImageGalleryAdapter extends ArrayAdapter<ImageInfoModel> {

        Context context;


        public ImageGalleryAdapter(Context context, List<ImageInfoModel> images) {
            super(context, 0, images);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item_image, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ImageInfoModel item = getItem(position);

            Glide.with(context)
                    .load(item.getFilePath())
                    .thumbnail(0.1f)
                    .dontAnimate()
                    .centerCrop()
                    .into(holder.ivImage);

            holder.tvLat.setText("위도 : " + item.getLatitude());
            holder.tvLong.setText("경도 : " + item.getLongitude());
            holder.tvLight.setText("조도 : " + item.getLight());
            holder.tvDir.setText("방향 : " + item.getDirection());

            if(item.isChecked()) {
                holder.chCheck.setVisibility(View.VISIBLE);
            } else {
                holder.chCheck.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    class ViewHolder {
        public ImageView ivImage;
        public TextView tvLat;
        public TextView tvLong;
        public TextView tvLight;
        public TextView tvDir;
        public ImageView chCheck;

        // This is like storing too much data in memory.
        // find a better way to handle this

        public ViewHolder(View view) {
            ivImage = (ImageView) view.findViewById(R.id.listview_item_image);
            tvLat = (TextView) view.findViewById(R.id.listview_item_latitude);
            tvLong = (TextView) view.findViewById(R.id.listview_item_longitude);
            tvLight = (TextView) view.findViewById(R.id.listview_item_light);
            tvDir = (TextView) view.findViewById(R.id.listview_item_direction);
            chCheck = (ImageView) view.findViewById(R.id.listview_item_check_image);
        }

    }
}
