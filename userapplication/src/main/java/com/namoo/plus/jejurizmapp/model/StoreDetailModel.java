package com.namoo.plus.jejurizmapp.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by HeungSun-AndBut on 2016. 7. 29..
 */

public class StoreDetailModel {
    private int id;
    private String name;
    private String summary;
    private String addr;
    private double lat;
    private double lng;
    private String operatingTime;
    private Boolean toiletSharedInside;
    private Boolean toiletSharedOutside;
    private Boolean toiletInside;
    private Boolean toiletOutside;
    private Boolean toiletStool;
    private Boolean toiletDirection;
    private Boolean toiletUrinal;
    private Boolean toiletTissue;
    private Boolean toiletClean;
    private Boolean hasWifi;
    private Boolean koreanFood;
    private Boolean chineseFood;
    private Boolean americanFood;
    private Boolean japaneseFood;
    private Boolean cafeFood;
    private Boolean globalFood;
    private Boolean flourBasedFood;
    private Boolean solo;
    private Boolean couple;
    private Boolean family;
    private Boolean team;
    private Boolean diet;
    private Boolean healthy;
    private Boolean vegetable;
    private String mainImage;
    private ArrayList<String> images;
    private Date created;
    private Date updated;


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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    // 중현 수정

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

    public Boolean getToiletSharedInside() {
        return toiletSharedInside;
    }

    public void setToiletSharedInside(Boolean toiletSharedInside) {
        this.toiletSharedInside = toiletSharedInside;
    }

    public Boolean getToiletSharedOutside() {
        return toiletSharedOutside;
    }

    public void setToiletSharedOutside(Boolean toiletSharedOutside) {
        this.toiletSharedOutside = toiletSharedOutside;
    }

    public Boolean getToiletInside() {
        return toiletInside;
    }

    public void setToiletInside(Boolean toiletInside) {
        this.toiletInside = toiletInside;
    }

    public Boolean getToiletOutside() {
        return toiletOutside;
    }

    public void setToiletOutside(Boolean toiletOutside) {
        this.toiletOutside = toiletOutside;
    }

    public Boolean getToiletStool() {
        return toiletStool;
    }

    public void setToiletStool(Boolean toiletStool) {
        this.toiletStool = toiletStool;
    }

    public Boolean getToiletDirection() {
        return toiletDirection;
    }

    public void setToiletDirection(Boolean toiletDirection) {
        this.toiletDirection = toiletDirection;
    }

    public Boolean getToiletUrinal() {
        return toiletUrinal;
    }

    public void setToiletUrinal(Boolean toiletUrinal) {
        this.toiletUrinal = toiletUrinal;
    }

    public Boolean getToiletTissue() {
        return toiletTissue;
    }

    public void setToiletTissue(Boolean toiletTissue) {
        this.toiletTissue = toiletTissue;
    }

    public Boolean getToiletClean() {
        return toiletClean;
    }

    public void setToiletClean(Boolean toiletClean) {
        this.toiletClean = toiletClean;
    }

    public Boolean getHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(Boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public Boolean getKoreanFood() {
        return koreanFood;
    }

    public void setKoreanFood(Boolean koreanFood) {
        this.koreanFood = koreanFood;
    }

    public Boolean getChineseFood() {
        return chineseFood;
    }

    public void setChineseFood(Boolean chineseFood) {
        this.chineseFood = chineseFood;
    }

    public Boolean getAmericanFood() {
        return americanFood;
    }

    public void setAmericanFood(Boolean americanFood) {
        this.americanFood = americanFood;
    }

    public Boolean getJapaneseFood() {
        return japaneseFood;
    }

    public void setJapaneseFood(Boolean japaneseFood) {
        this.japaneseFood = japaneseFood;
    }

    public Boolean getCafeFood() {
        return cafeFood;
    }

    public void setCafeFood(Boolean cafeFood) {
        this.cafeFood = cafeFood;
    }

    public Boolean getGlobalFood() {
        return globalFood;
    }

    public void setGlobalFood(Boolean globalFood) {
        this.globalFood = globalFood;
    }

    public Boolean getFlourBasedFood() {
        return flourBasedFood;
    }

    public void setFlourBasedFood(Boolean flourBasedFood) {
        this.flourBasedFood = flourBasedFood;
    }

    public Boolean getSolo() {
        return solo;
    }

    public void setSolo(Boolean solo) {
        this.solo = solo;
    }

    public Boolean getCouple() {
        return couple;
    }

    public void setCouple(Boolean couple) {
        this.couple = couple;
    }

    public Boolean getFamily() {
        return family;
    }

    public void setFamily(Boolean family) {
        this.family = family;
    }

    public Boolean getTeam() {
        return team;
    }

    public void setTeam(Boolean team) {
        this.team = team;
    }

    public Boolean getDiet() {
        return diet;
    }

    public void setDiet(Boolean diet) {
        this.diet = diet;
    }

    public Boolean getHealthy() {
        return healthy;
    }

    public void setHealthy(Boolean healthy) {
        this.healthy = healthy;
    }

    public Boolean getVegetable() {
        return vegetable;
    }

    public void setVegetable(Boolean vegetable) {
        this.vegetable = vegetable;
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

