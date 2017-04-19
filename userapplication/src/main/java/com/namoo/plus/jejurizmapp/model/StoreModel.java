package com.namoo.plus.jejurizmapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HeungSun-AndBut on 2016. 7. 28..
 */



public class StoreModel implements Parcelable {
    private int id;
    private String name;
    private String mainImage;

    public StoreModel() {

    }
    public StoreModel(Parcel source) {
        readFromParcel(source);
    }

    // 중현 수정
    public StoreModel(int id, String name, String mainImage, String addr, Boolean toiletSharedInside) {
        this.id = id;
        this.name = name;
        this.mainImage = mainImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // 중현 수정
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(mainImage);
    }

    // 중현 수정
    private void readFromParcel(Parcel source) {
        id = source.readInt();
        name = source.readString();
        mainImage = source.readString();
    }

    public static final Creator<StoreModel> CREATOR = new Creator<StoreModel>() {
        @Override
        public StoreModel createFromParcel(Parcel source) {
            return new StoreModel(source);
        }

        @Override
        public StoreModel[] newArray(int size) {
            return new StoreModel[size];
        }
    };

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

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

}
