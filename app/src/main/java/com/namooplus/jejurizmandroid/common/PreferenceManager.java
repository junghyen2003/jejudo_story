package com.namooplus.jejurizmandroid.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by lee on 2016. 6. 22..
 */
public class PreferenceManager {

    private static final String PREFERENCE = "namooPreference";
    private static final String IMAGE_TITLE = "imageTitle";
    private static final String IMAGE_DIR_PATH_NUM = "imageDirPathNum";

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private Context mContext;

    public static PreferenceManager instance;

    public static PreferenceManager getInstance(Context context) {
        if(instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }
    public PreferenceManager(Context context) {
        this.mContext = context;
    }

    public void saveImageTitle(@NonNull String imageTitle) {
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString(IMAGE_TITLE, imageTitle);
        editor.commit();
    }

    public String getImageTitle() {
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString(IMAGE_TITLE, null);
    }

    public int getLastImagePathNum() {
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(IMAGE_DIR_PATH_NUM, -1);
    }

    public void setLastImagePathNum(int num) {
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putInt(IMAGE_DIR_PATH_NUM, num);
        editor.commit();
    }

}
