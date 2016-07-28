package com.namoo.plus.jejurizmapp.model;

/**
 * Created by HeungSun-AndBut on 2016. 7. 27..
 */

public class SearchMenuModel {

    private String mPath;
    private String mTitle;
    private boolean mCheck;

    public SearchMenuModel(String mPath, String mTitle, boolean mCheck) {
        this.mPath = mPath;
        this.mTitle = mTitle;
        this.mCheck = mCheck;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public boolean isCheck() {
        return mCheck;
    }

    public void setCheck(boolean mCheck) {
        this.mCheck = mCheck;
    }
}
