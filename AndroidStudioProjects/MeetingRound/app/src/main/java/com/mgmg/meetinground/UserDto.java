package com.mgmg.meetinground;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class UserDto {

    private String name;
    private String profile;
    private String uId;
    private int invest;

    public UserDto(){};

    public UserDto(String name, String profile, String uId) {
        this.name = name;
        this.profile = profile;
        this.uId = uId;
        invest = 0;
    }

    public UserDto(String name, String profile, String uId, int invest) {
        this.name = name;
        this.profile = profile;
        this.uId = uId;
        this.invest = invest;
    }

    public int getInvest() {
        return invest;
    }

    public void setInvest(int invest) {
        this.invest = invest;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
