package com.mgmg.meetinground;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RoomDto {
    private String roomId;
    private String roomName;
    private Calendar calendar;
    private String date;
    private String time;
    DatabaseReference database;


    public RoomDto() {}

    public RoomDto(String roomId, String roomName, String date, String time) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.date = date;
        this.time = time;
    }

    public RoomDto(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;

    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RoomDto{" +
                "roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
