package com.namooplus.jejurizmandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.namooplus.jejurizmandroid.R;
import com.namooplus.jejurizmandroid.model.ImageInfoModel;

import java.util.ArrayList;
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
    public ImageView chCheck;

    private List<ImageInfoModel> imageModels;
    private Context context;

    public ImageListAdapter(List<ImageInfoModel> imageModels, Context context) {
        this.imageModels = imageModels;
        this.context = context;
    }

    public void clear() {
        imageModels.clear();
    }

    public void addAll(ArrayList<ImageInfoModel> items) {
        this.imageModels.addAll(items);
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
            chCheck = (ImageView) convertView.findViewById(R.id.listview_item_check_image);
        }

        Glide.with(context)
                .load(item.getFilePath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivImage);
        //final BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 16;

        //ivImage.setImageBitmap(BitmapFactory.decodeFile(item.getFilePath(), options));
        tvLat.setText("위도 : " + item.getLatitude());
        tvLong.setText("경도 : " + item.getLongitude());
        tvLight.setText("조도 : " + item.getLight());
        tvDir.setText("방향 : " + item.getDirection());

        if(item.isChecked()) {
            chCheck.setVisibility(View.VISIBLE);
        } else {
            chCheck.setVisibility(View.GONE);
        }


        return convertView;
    }
}
