package com.namoo.plus.jejurizmapp.network.model;

/**
 * Created by HeungSun-AndBut on 2016. 7. 28..
 */

import com.namoo.plus.jejurizmapp.model.StoreModel;

import java.util.ArrayList;

public class StoreListResponse {

    private int code;
    private String msg;
    private int count;
    private ArrayList<StoreModel> data;

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

    public ArrayList<StoreModel> getData() {
        return data;
    }

    public void setData(ArrayList<StoreModel> data) {
        this.data = data;
    }
}
