package com.mgmg.meetinground;

public class Restaurant {
    String name;
    String tel;
    String address;
    String area;
    String lat;
    String lon;
    String branch;
    String[] bhour_list;
    String[] category_list;
    String[] menu_list;
    String[] review_list;
    String[] tags;
    String[] images;

    int agree;
    int disagree;

    public Restaurant() {
        name="name";
        address= "address";
        agree=0;
        disagree=0;
    }

    public Restaurant(String name, String tel, String address, String area, String lat, String lon, String branch, String[] bhour_list, String[] category_list, String[] menu_list, String[] review_list, String[] tags, String[] images) {
        this.name = name;
        this.tel = tel;
        this.address = address;
        this.area = area;
        this.lat = lat;
        this.lon = lon;
        this.branch = branch;
        this.bhour_list = bhour_list;
        this.category_list = category_list;
        this.menu_list = menu_list;
        this.review_list = review_list;
        this.tags = tags;
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public String getAddress() {
        return address;
    }

    public String getArea() {
        return area;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getBranch() {
        return branch;
    }

    public String[] getBhour_list() {
        return bhour_list;
    }

    public String[] getCategory_list() {
        return category_list;
    }

    public String[] getMenu_list() {
        return menu_list;
    }

    public String[] getReview_list() {
        return review_list;
    }

    public String[] getTags() {
        return tags;
    }

    public String[] getImages() {
        return images;
    }

    public int getAgree() {
        return agree;
    }

    public int getDisagree() {
        return disagree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public void setDisagree(int disagree) {
        this.disagree = disagree;
    }


}
