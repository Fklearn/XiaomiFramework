package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class ActivityInfo implements Parcelable {
    public static final Parcelable.Creator<ActivityInfo> CREATOR = new C0356a();
    private int actId;
    private String actUrl;
    private long beginTime;
    private long endTime;
    private long gameId;
    private String mActIcon;
    private String name;
    private int status;
    private int type;

    public ActivityInfo() {
    }

    protected ActivityInfo(Parcel parcel) {
        this.actId = parcel.readInt();
        this.name = parcel.readString();
        this.type = parcel.readInt();
        this.beginTime = parcel.readLong();
        this.endTime = parcel.readLong();
        this.status = parcel.readInt();
        this.actUrl = parcel.readString();
        this.gameId = parcel.readLong();
        this.mActIcon = parcel.readString();
    }

    public static boolean isLeagle(ActivityInfo activityInfo) {
        return activityInfo != null && activityInfo.actId > 0 && !TextUtils.isEmpty(activityInfo.name);
    }

    public int describeContents() {
        return 0;
    }

    public String getActIcon() {
        return this.mActIcon;
    }

    public int getActId() {
        return this.actId;
    }

    public String getActUrl() {
        return this.actUrl;
    }

    public long getBeginTime() {
        return this.beginTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public long getGameId() {
        return this.gameId;
    }

    public String getName() {
        return this.name;
    }

    public int getStatus() {
        return this.status;
    }

    public int getType() {
        return this.type;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(this.name);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.actId);
        parcel.writeString(this.name);
        parcel.writeInt(this.type);
        parcel.writeLong(this.beginTime);
        parcel.writeLong(this.endTime);
        parcel.writeInt(this.status);
        parcel.writeString(this.actUrl);
        parcel.writeLong(this.gameId);
        parcel.writeString(this.mActIcon);
    }
}
