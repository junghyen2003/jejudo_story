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

    public StoreModel() {

    }
    public StoreModel(Parcel source) {
        readFromParcel(source);
    }

    // 중현 수정
    public StoreModel(int id, String name, String mainImage,
                      Boolean toiletSharedInside, Boolean toiletSharedOutside, Boolean toiletInside,
                      Boolean toiletOutside, Boolean toiletStool, Boolean toiletDirection,
                      Boolean toiletUrinal, Boolean toiletTissue, Boolean toiletClean,
                      Boolean hasWifi, Boolean koreanFood, Boolean chineseFood,
                      Boolean americanFood, Boolean japaneseFood, Boolean cafeFood,
                      Boolean globalFood, Boolean flourBasedFood, Boolean solo,
                      Boolean couple, Boolean family, Boolean team,
                      Boolean diet, Boolean healthy, Boolean vegetable) {
        this.id = id;
        this.name = name;
        this.mainImage = mainImage;
        this.toiletSharedInside = toiletSharedInside;
        this.toiletSharedOutside = toiletSharedOutside;
        this.toiletInside = toiletInside;
        this.toiletOutside = toiletOutside;
        this.toiletStool = toiletStool;
        this.toiletDirection = toiletDirection;
        this.toiletUrinal = toiletUrinal;
        this.toiletTissue = toiletTissue;
        this.toiletClean = toiletClean;
        this.hasWifi = hasWifi;
        this.koreanFood = koreanFood;
        this.chineseFood = chineseFood;
        this.americanFood = americanFood;
        this.japaneseFood = japaneseFood;
        this.cafeFood = cafeFood;
        this.globalFood = globalFood;
        this.flourBasedFood = flourBasedFood;
        this.solo = solo;
        this.couple = couple;
        this.family = family;
        this.team = team;
        this.diet = diet;
        this.healthy = healthy;
        this.vegetable = vegetable;
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


    // 중현 수정
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
}
