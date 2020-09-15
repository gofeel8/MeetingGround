package com.mgmg.meetinground;

public class RoomDto {
    private String roomId;
    private String roomName;

    public RoomDto() {}

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

    @Override
    public String toString() {
        return "RoomDto{" +
                "roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
