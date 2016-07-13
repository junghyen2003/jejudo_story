package com.namooplus.jejurizmandroid.common;

import android.os.Environment;

/**
 * Created by HeungSun-AndBut on 2016. 6. 19..
 */

public class AppSetting {
    public final static int ACTIVITY_CODE_CAMERA_LIST = 52;

    public static final String SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + "/namooplus/images/";
    public static final String SAVE_EXCEL_PATH = Environment.getExternalStorageDirectory() + "/namooplus/";
    public static final String SAVE_IMAGE_TEMP_PATH = Environment.getExternalStorageState() + "/namooplus/temp/";
    public static final int INTERVAL_MAP_REFRESH = 2000;
    public static final int IMAGE_SAMPLE_SIZE = 8; //8배 축소
    public static final boolean FLASH_SETTING_VALUE = false;
}
