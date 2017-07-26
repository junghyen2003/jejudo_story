package com.namoo.plus.jejurizmapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jungh on 2017-05-18.
 */

public class MuseumModel implements Parcelable {

    private int id;
    private String name;
    private String summary;
    private String addr;
    private double lat;
    private double lng;
    private String operatingTime;
    private String fee;
    private Boolean toilet;
    private Boolean hasWifi;
    private String mainImage;
    private ArrayList<String> images;
    private Date created;
    private Date updated;

    public MuseumModel() {

    }

    public MuseumModel(Parcel source) {
        readFromParcel(source);
    }



    public static final Creator<MuseumModel> CREATOR = new Creator<MuseumModel>() {
        @Override
        public MuseumModel createFromParcel(Parcel in) {
            return new MuseumModel(in);
        }

        @Override
        public MuseumModel[] newArray(int size) {
            return new MuseumModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(mainImage);
    }

    private void readFromParcel(Parcel source) {
        id = source.readInt();
        name = source.readString();
        mainImage = source.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(String operatingTime) {
        this.operatingTime = operatingTime;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public Boolean getToilet() {
        return toilet;
    }

    public void setToilet(Boolean toilet) {
        this.toilet = toilet;
    }

    public Boolean getHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(Boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
