package com.namooplus.jejurizmandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HeungSun-AndBut on 2016. 7. 12..
 */

public class ImageInfoModel implements Parcelable {
    private String filePath;
    private double latitude;
    private double longitude;
    private float light;
    private float direction;
    private boolean isChecked;
    private int orientation;

    public ImageInfoModel(Parcel source) {
        readFromParcel(source);
    }

    public ImageInfoModel(String filePath, double latitude, double longitude, float light, float direction, int orientation) {
        this.filePath = filePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.light = light;
        this.direction = direction;
        this.orientation = orientation;
        this.isChecked = true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(light);
        dest.writeFloat(direction);
        dest.writeInt(orientation);
        dest.writeInt(isChecked == true ? 1 : 0);
    }

    private void readFromParcel(Parcel source) {
        filePath = source.readString();
        latitude = source.readDouble();
        longitude = source.readDouble();
        light = source.readFloat();
        direction = source.readFloat();
        orientation = source.readInt();
        isChecked = source.readInt() == 1 ? true : false;
    }

    public static final Parcelable.Creator<ImageInfoModel> CREATOR = new Creator<ImageInfoModel>() {
        @Override
        public ImageInfoModel createFromParcel(Parcel source) {
            return new ImageInfoModel(source);
        }

        @Override
        public ImageInfoModel[] newArray(int size) {
            return new ImageInfoModel[size];
        }
    };

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


}
