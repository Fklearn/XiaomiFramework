package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleTopicInfo implements Parcelable {
    public static final Parcelable.Creator<SimpleTopicInfo> CREATOR = new q();
    private String name;
    private int topicId;

    public SimpleTopicInfo() {
    }

    protected SimpleTopicInfo(Parcel parcel) {
        this.topicId = parcel.readInt();
        this.name = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public String getName() {
        return this.name;
    }

    public int getTopicId() {
        return this.topicId;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setTopicId(int i) {
        this.topicId = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.topicId);
        parcel.writeString(this.name);
    }
}
