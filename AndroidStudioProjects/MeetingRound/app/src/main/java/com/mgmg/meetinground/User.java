package com.mgmg.meetinground;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class User {

    private String id;
    private String name;
    private String profile;
    private List<String> rooms;

    public User(){};

    public User(String id, String name, String profile, List<String> rooms) {
        this.id = id;
        this.name = name;
        this.profile = profile;
        this.rooms = rooms;
    }

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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", rooms=" + rooms +
                '}';
    }
}
