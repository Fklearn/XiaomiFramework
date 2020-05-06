package com.xiaomi.micloudsdk.stat;

import android.os.Parcel;
import android.os.Parcelable;

public class NetSuccessStatParam implements Parcelable {
    public static final Parcelable.Creator<NetSuccessStatParam> CREATOR = new e();
    public final long netFlow;
    public final long requestStartTime;
    public final int responseCode;
    public final int resultType;
    public final int retryCount;
    public final long timeCost;
    public final String url;

    protected NetSuccessStatParam(Parcel parcel) {
        this.url = parcel.readString();
        this.requestStartTime = parcel.readLong();
        this.timeCost = parcel.readLong();
        this.netFlow = parcel.readLong();
        this.resultType = parcel.readInt();
        this.responseCode = parcel.readInt();
        this.retryCount = parcel.readInt();
    }

    public NetSuccessStatParam(String str, long j, long j2, long j3, int i, int i2) {
        this.url = str;
        this.requestStartTime = j;
        this.timeCost = j2;
        this.netFlow = j3;
        this.responseCode = i;
        this.retryCount = i2;
        this.resultType = 0;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "NetSuccessStatParam{url='" + this.url + '\'' + ", requestStartTime=" + this.requestStartTime + ", timeCost=" + this.timeCost + ", netFlow=" + this.netFlow + ", resultType=" + this.resultType + ", responseCode=" + this.responseCode + ", retryCount=" + this.retryCount + '}';
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.url);
        parcel.writeLong(this.requestStartTime);
        parcel.writeLong(this.timeCost);
        parcel.writeLong(this.netFlow);
        parcel.writeInt(this.resultType);
        parcel.writeInt(this.responseCode);
        parcel.writeInt(this.retryCount);
    }
}
