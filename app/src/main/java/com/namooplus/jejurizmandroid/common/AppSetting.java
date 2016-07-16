package com.namooplus.jejurizmandroid.common;

import android.os.Environment;

/**
 * Created by HeungSun-AndBut on 2016. 6. 19..
 */

public class AppSetting {
    public final static int ACTIVITY_CODE_CAMERA_LIST = 52;
    public final static int ACTIVITY_CODE_IMAGE_DETAIL = 59;

    //나무플러스 앱 관련 디렉토리
    public static final String SAVE_SITE_PATH = Environment.getExternalStorageDirectory() + "/namooplus/";
    //이미지가 임시로 저장되는 곳
    public static final String SAVE_IMAGE_TEMP_PATH = Environment.getExternalStorageDirectory() + "/namooplus/temp/";

    //조도, 위치 등 정보 새로고침 주기
    public static final int INTERVAL_MAP_REFRESH = 2000;

    //샘플링 이미지 사이즈
    public static final int IMAGE_SAMPLE_SIZE = 8; //8배 축소

    //같은 업체인지 구분하는 최소 거리
    public static final int SAME_STORE_MIN_DISTANCE = 1000;

    //숫자 구분 문자열로 제목입력에 사용하면 안된다.
    public static final String NAMOO_STRING_SPLIT = "_";
    //엑셀 파일명
    public static final String EXCEL_STRING_FORMAT = ".xls";
    //이미지는 jpg 사용
    public static final String IMAGE_STRING_FORMAT = ".jpg";
}
