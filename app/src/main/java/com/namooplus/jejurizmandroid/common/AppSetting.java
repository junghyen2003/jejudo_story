package com.namooplus.jejurizmandroid.common;

import android.os.Environment;

/**
 * Created by HeungSun-AndBut on 2016. 6. 19..
 */

public class AppSetting {
    public final static int ACTIVITY_CODE_CAMERA_LIST = 52;
    public final static int ACTIVITY_CODE_IMAGE_DETAIL = 59;

    public static final String SAVE_SITE_PATH = Environment.getExternalStorageDirectory() + "/namooplus/";
    public static final String SAVE_IMAGE_TEMP_PATH = Environment.getExternalStorageState() + "/namooplus/temp/";
    public static final int INTERVAL_MAP_REFRESH = 2000;
    public static final int IMAGE_SAMPLE_SIZE = 8; //8배 축소
    public static final boolean FLASH_SETTING_VALUE = false;

    public static final int SAME_STORE_MIN_DISTANCE = 1000;

    public static final String NAMOO_STRING_SPLIT = "_";
    public static final String EXCEL_STRING_FORMAT = ".xls";
    public static final String IMAGE_STRING_FORMAT = ".jpg";
}
