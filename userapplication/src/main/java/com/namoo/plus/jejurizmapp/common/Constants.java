package com.namoo.plus.jejurizmapp.common;

import android.os.Environment;

/**
 * Created by HeungSun-AndBut on 2016. 7. 27..
 */

public class Constants {
    public static final int SEARCH_MENU_OPTION_SELECT_RESTAURANT_KIND = 0;
    public static final int SEARCH_MENU_OPTION_SELECT_RESTAURANT_SITUATION = 1;
    public static final int SEARCH_MENU_OPTION_SELECT_TOUR_KIND = 2;
    public static final int SEARCH_MENU_OPTION_SELECT_TOUR_SITUATION = 3;
    public static final int SEARCH_MENU_OPTION_SELECT_SHOPPING = 4;

    public static final int INTERVAL_MAP_REFRESH = 1000;

    //나무플러스 앱 관련 디렉토리
    public static final String SAVE_SITE_PATH = Environment.getExternalStorageDirectory() + "/namooplus/";
    //이미지가 임시로 저장되는 곳
    public static final String SAVE_IMAGE_TEMP_PATH = Environment.getExternalStorageDirectory() + "/namooplus/temp/";

    //이미지는 jpg 사용
    public static final String IMAGE_STRING_FORMAT = ".jpg";

    public static final String NAMOO_PLUS_BASE_URL = "http://211.225.79.43";
    public static final String NAMOO_PLUS_MENU_URL = NAMOO_PLUS_BASE_URL + "/images/test/";

    public static final int STANDARD_WIDTH_SIZE = 1920;
    public static final int STANDARD_HEIGHT_SIZE = 1080;
}
