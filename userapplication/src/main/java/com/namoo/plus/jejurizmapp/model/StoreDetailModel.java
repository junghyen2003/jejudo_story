package com.namoo.plus.jejurizmapp.model;

import java.util.ArrayList;

/**
 * Created by HeungSun-AndBut on 2016. 7. 29..
 */

public class StoreDetailModel {
    private int id;
    private String name;
    private String summary;
    private String mainImage;
    private ArrayList<String> images;

    public StoreDetailModel() {}
    public StoreDetailModel(int id, String name, String summary, String mainImage, ArrayList<String> images) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.mainImage = mainImage;
        this.images = images;
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
}

