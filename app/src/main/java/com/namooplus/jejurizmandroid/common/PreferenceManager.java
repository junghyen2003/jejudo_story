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

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private Context mContext;

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

}
