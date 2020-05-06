package com.xiaomi.micloudsdk.stat;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.SocketTimeoutException;
import org.apache.http.conn.ConnectTimeoutException;

public class NetFailedStatParam implements Parcelable {
    public static final Parcelable.Creator<NetFailedStatParam> CREATOR = new d();
    public final String exceptionName;
    public final long requestStartTime;
    public final int resultType;
    public final int retryCount;
    public final long timeCost;
    public final String url;

    protected NetFailedStatParam(Parcel parcel) {
        this.url = parcel.readString();
        this.requestStartTime = parcel.readLong();
        this.timeCost = parcel.readLong();
        this.exceptionName = parcel.readString();
        this.resultType = parcel.readInt();
        this.retryCount = parcel.readInt();
    }

    public NetFailedStatParam(String str, long j, long j2, Throwable th, int i) {
        this.url = str;
        this.requestStartTime = j;
        this.timeCost = j2;
        this.exceptionName = th.getClass().getSimpleName();
        this.resultType = getResultType(th);
        this.retryCount = i;
    }

    private int getResultType(Throwable th) {
        return ((th instanceof ConnectTimeoutException) || (th instanceof SocketTimeoutException)) ? 2 : 1;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "NetFailedStatParam{url='" + this.url + '\'' + ", requestStartTime=" + this.requestStartTime + ", timeCost=" + this.timeCost + ", exceptionName='" + this.exceptionName + '\'' + ", resultType=" + this.resultType + ", retryCount=" + this.retryCount + '}';
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.url);
        parcel.writeLong(this.requestStartTime);
        parcel.writeLong(this.timeCost);
        parcel.writeString(this.exceptionName);
        parcel.writeInt(this.resultType);
        parcel.writeInt(this.retryCount);
    }
}
