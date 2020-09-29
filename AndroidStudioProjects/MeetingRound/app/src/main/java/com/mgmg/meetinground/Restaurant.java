package com.mgmg.meetinground;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Restaurant implements Parcelable {
    String id;
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
    List<String> tags;
    List<String> images;

    int agree;
    int disagree;

    public Restaurant() {
        name="name";
        address= "address";
        agree=0;
        disagree=0;
    }

    public Restaurant(String id, String name, String tel, String address, String area, String lat, String lon, String branch, String[] bhour_list, String[] category_list, String[] menu_list, String[] review_list, List<String> tags, List<String> images, int agree, int disagree) {
        this.id = id;
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
        this.agree = agree;
        this.disagree = disagree;
    }

    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        tel = in.readString();
        address = in.readString();
        area = in.readString();
        lat = in.readString();
        lon = in.readString();
        branch = in.readString();
        bhour_list = in.createStringArray();
        category_list = in.createStringArray();
        menu_list = in.createStringArray();
        review_list = in.createStringArray();
        tags = in.createStringArrayList();
        images = in.createStringArrayList();
        agree = in.readInt();
        disagree = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(tel);
        dest.writeString(address);
        dest.writeString(area);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(branch);
        dest.writeStringArray(bhour_list);
        dest.writeStringArray(category_list);
        dest.writeStringArray(menu_list);
        dest.writeStringArray(review_list);
        dest.writeStringList(tags);
        dest.writeStringList(images);
        dest.writeInt(agree);
        dest.writeInt(disagree);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String[] getBhour_list() {
        return bhour_list;
    }

    public void setBhour_list(String[] bhour_list) {
        this.bhour_list = bhour_list;
    }

    public String[] getCategory_list() {
        return category_list;
    }

    public void setCategory_list(String[] category_list) {
        this.category_list = category_list;
    }

    public String[] getMenu_list() {
        return menu_list;
    }

    public void setMenu_list(String[] menu_list) {
        this.menu_list = menu_list;
    }

    public String[] getReview_list() {
        return review_list;
    }

    public void setReview_list(String[] review_list) {
        this.review_list = review_list;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public int getDisagree() {
        return disagree;
    }

    public void setDisagree(int disagree) {
        this.disagree = disagree;
    }
}
