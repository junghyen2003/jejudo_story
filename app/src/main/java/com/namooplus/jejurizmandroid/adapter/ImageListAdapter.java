package com.namooplus.jejurizmandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;

import java.util.List;

/**
 * Created by HeungSun-AndBut on 2016. 7. 12..
 */

public class ImageListAdapter extends BaseAdapter {

    public ImageView ivImage;
    public TextView tvLat;
    public TextView tvLong;
    public TextView tvLight;
    public TextView tvDir;
    public CheckBox chCheck;

    private boolean checkBoxMode = false;
    private List<ImageInfoModel> imageModels;
    private Context context;

    public ImageListAdapter(List<ImageInfoModel> imageModels, Context context) {
        this.imageModels = imageModels;
        this.context = context;
    }

    public void setCheckBoxMode(boolean checkBoxMode) {
        this.checkBoxMode = checkBoxMode;
    }

    public boolean isCheckBoxMode() {
        return checkBoxMode;
    }

    @Override
    public int getCount() {
        return imageModels.size();
    }

    @Override
    public Object getItem(int position) {
        return imageModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageInfoModel item = imageModels.get(position);
        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.listview_item_image, parent, false);

            ivImage = (ImageView) convertView.findViewById(R.id.listview_item_image);
            tvLat = (TextView) convertView.findViewById(R.id.listview_item_latitude);
            tvLong = (TextView) convertView.findViewById(R.id.listview_item_longitude);
            tvLight = (TextView) convertView.findViewById(R.id.listview_item_light);
            tvDir = (TextView) convertView.findViewById(R.id.listview_item_direction);
            chCheck = (CheckBox) convertView.findViewById(R.id.listview_item_checkbox);
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        ivImage.setImageBitmap(BitmapFactory.decodeFile(item.getFilePath(), options));
        tvLat.setText("위도 : " + item.getLatitude());
        tvLong.setText("경도 : " + item.getLongitude());
        tvLight.setText("조도 : " + item.getLight());
        tvDir.setText("방향 : " + item.getDirection());

        chCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
            }
        });
        return convertView;
    }
}
