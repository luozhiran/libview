/**
 * ****************************************************************
 *
 * Copyright (C) YQTH Corporation. All rights reserved.
 *
 * FileName : Voice.java
 * Description : Data structure used to exchange information between
 * tts service and its binder.
 *
 ******************************************************************
 */
package com.yq.tts.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A pojo class describes the data structure used to exchange
 * information between TTS service and its binder.
 */
public class TtsTask implements Parcelable {
    /**The content which you want to speak.*/
    public String content;
    /**The caller who sends speak task to TTS service.*/
    public String caller;
    /**The extra information which can be null.*/
    public String extra;
    /**The message ID of this speak task*/
    public Long id;
    /**UNPUBLISHED: Whether use ring channel to play or not, if yes, play in ring channel,
     * otherwise, use the music channel */
    public boolean ringChannel;
    /**UNPUBLISHED: The message data of this speak task*/
    public Long date;
    /**UNPUBLISHED: Whether starts speak this speak task immediately.*/
    public Boolean immediate;
    /**UNPUBLISHED: Whether discards the first task in task queue*/
    public Boolean skip;
    /**UNPUBLISHED: The priority of this speak task*/
    public int priority;

    public static final Creator<TtsTask> CREATOR = new Creator<TtsTask>() {

        @Override
        public TtsTask createFromParcel(Parcel source) {
            return new TtsTask(source);
        }

        @Override
        public TtsTask[] newArray(int size) {
            return new TtsTask[size];
        }
    };

    public TtsTask() {
        content = "";
        caller = "";
        extra = "";
        immediate = false;
        skip = false;
        priority = 0;
        id = 0L;
        date = 0L;
    }

    private TtsTask(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(caller);
        dest.writeString(extra);
        dest.writeLong(id);
        dest.writeString(Boolean.toString(ringChannel));
        dest.writeLong(date);
        dest.writeString(Boolean.toString(immediate));
        dest.writeString(Boolean.toString(skip));
        dest.writeInt(priority);
    }

    public void readFromParcel(Parcel source) {
        content = source.readString();
        caller = source.readString();
        extra = source.readString();
        id = source.readLong();
        ringChannel = Boolean.parseBoolean(source.readString());
        date = source.readLong();
        immediate = Boolean.parseBoolean(source.readString());
        skip = Boolean.parseBoolean(source.readString());
        priority = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
