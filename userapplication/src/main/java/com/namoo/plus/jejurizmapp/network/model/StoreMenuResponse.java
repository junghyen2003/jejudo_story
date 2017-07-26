package com.namoo.plus.jejurizmapp.network.model;

import com.namoo.plus.jejurizmapp.model.MenuModel;

import java.util.ArrayList;

/**
 * Created by jungh on 2017-05-12.
 */

public class StoreMenuResponse {
    private int code;
    private String msg;
    int count;
    private ArrayList<MenuModel> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<MenuModel> getData() {
        return data;
    }

    public void setData(ArrayList<MenuModel> data) {
        this.data = data;
    }
}
