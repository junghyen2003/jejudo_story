package com.namoo.plus.jejurizmapp.network.model;

import com.namoo.plus.jejurizmapp.model.MuseumModel;

import java.util.ArrayList;

/**
 * Created by jungh on 2017-05-18.
 */

public class MuseumListResponse {

    private int code;
    private String msg;
    private int count;
    private ArrayList<MuseumModel> data;

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

    public ArrayList<MuseumModel> getData() {
        return data;
    }

    public void setData(ArrayList<MuseumModel> data) {
        this.data = data;
    }

}
