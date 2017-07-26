package com.namoo.plus.jejurizmapp.model;

import java.util.Date;

/**
 * Created by jungh on 2017-05-12.
 */

public class MenuModel {
    int id;
    String menu;
    int pungency;
    int sourness;
    int sweetness;
    int saltiness;
    int price;
    String image;
    Date created;
    Date updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public int getPungency() {
        return pungency;
    }

    public void setPungency(int pungency) {
        this.pungency = pungency;
    }

    public int getSourness() {
        return sourness;
    }

    public void setSourness(int sourness) {
        this.sourness = sourness;
    }

    public int getSweetness() {
        return sweetness;
    }

    public void setSweetness(int sweetness) {
        this.sweetness = sweetness;
    }

    public int getSaltiness() {
        return saltiness;
    }

    public void setSaltiness(int saltiness) {
        this.saltiness = saltiness;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
