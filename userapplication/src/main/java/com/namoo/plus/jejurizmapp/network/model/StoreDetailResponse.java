package com.namoo.plus.jejurizmapp.network.model;

import com.namoo.plus.jejurizmapp.model.StoreDetailModel;

/**
 * Created by HeungSun-AndBut on 2016. 7. 29..
 */

public class StoreDetailResponse {
    private int code;
    private String msg;
    private StoreDetailModel data;

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

    public StoreDetailModel getData() {
        return data;
    }

    public void setData(StoreDetailModel data) {
        this.data = data;
    }
}

