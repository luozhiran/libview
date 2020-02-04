package com.yqtec.video;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/8/22.
 */

public class VideoCallInfo implements Parcelable {
    private int roomId;
    private boolean isCallingActive;
    private String receiverFid;
    private int receiverPid;
    private String cmdMessage;
    private boolean isMonitorMode;
    private boolean isNeedRotate;
    private String receiveIdentifier;
    private String mPortraitUrl;
    private String mName;
    private int receiverSex;
    private boolean isAdmin;
    private String callDisplay;
    private int distance;

    public VideoCallInfo() {
    }

    protected VideoCallInfo(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(roomId);
        dest.writeByte((byte) (isCallingActive ? 1 : 0));
        dest.writeString(receiverFid);
        dest.writeInt(receiverPid);
        dest.writeString(cmdMessage);
        dest.writeByte((byte) (isMonitorMode ? 1 : 0));
        dest.writeByte((byte) (isNeedRotate ? 1 : 0));
        dest.writeString(receiveIdentifier);
        dest.writeString(mPortraitUrl);
        dest.writeString(mName);
        dest.writeInt(receiverSex);
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeString(callDisplay);
        dest.writeInt(distance);
    }


    public void readFromParcel(Parcel dest) {
        roomId = dest.readInt();
        isCallingActive = dest.readByte() != 0;
        receiverFid = dest.readString();
        receiverPid = dest.readInt();
        cmdMessage = dest.readString();
        isMonitorMode = dest.readByte() != 0;
        isNeedRotate = dest.readByte() != 0;
        receiveIdentifier = dest.readString();
        mPortraitUrl = dest.readString();
        mName = dest.readString();
        receiverSex = dest.readInt();
        isAdmin = dest.readByte() != 0;
        callDisplay = dest.readString();
        distance = dest.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoCallInfo> CREATOR = new Creator<VideoCallInfo>() {
        @Override
        public VideoCallInfo createFromParcel(Parcel in) {
            return new VideoCallInfo(in);
        }

        @Override
        public VideoCallInfo[] newArray(int size) {
            return new VideoCallInfo[size];
        }
    };


    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public boolean isCallingActive() {
        return isCallingActive;
    }

    public void setCallingActive(boolean callingActive) {
        isCallingActive = callingActive;
    }

    public String getReceiverFid() {
        return receiverFid;
    }

    public void setReceiverFid(String receiverFid) {
        this.receiverFid = receiverFid;
    }

    public int getReceiverPid() {
        return receiverPid;
    }

    public void setReceiverPid(int receiverPid) {
        this.receiverPid = receiverPid;
    }

    public String getCmdMessage() {
        return cmdMessage;
    }

    public void setCmdMessage(String cmdMessage) {
        this.cmdMessage = cmdMessage;
    }

    public boolean isMonitorMode() {
        return isMonitorMode;
    }

    public void setMonitorMode(boolean monitorMode) {
        isMonitorMode = monitorMode;
    }

    public boolean isNeedRotate() {
        return isNeedRotate;
    }

    public void setNeedRotate(boolean needRotate) {
        isNeedRotate = needRotate;
    }

    public String getReceiveIdentifier() {
        return receiveIdentifier;
    }

    public void setReceiveIdentifier(String receiveIdentifier) {
        this.receiveIdentifier = receiveIdentifier;
    }

    public String getmPortraitUrl() {
        return mPortraitUrl;
    }

    public void setmPortraitUrl(String mPortraitUrl) {
        this.mPortraitUrl = mPortraitUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getReceiverSex() {
        return receiverSex;
    }

    public void setReceiverSex(int receiverSex) {
        this.receiverSex = receiverSex;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getCallDisplay() {
        return callDisplay;
    }

    public void setCallDisplay(String callDisplay) {
        this.callDisplay = callDisplay;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
