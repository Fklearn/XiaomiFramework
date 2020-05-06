package com.miui.networkassistant.vpn.miui;

import android.os.Parcel;
import android.os.Parcelable;

public class MiuiVpnInfo implements Parcelable {
    public static final Parcelable.Creator<MiuiVpnInfo> CREATOR = new Parcelable.Creator<MiuiVpnInfo>() {
        public MiuiVpnInfo createFromParcel(Parcel parcel) {
            return new MiuiVpnInfo(parcel);
        }

        public MiuiVpnInfo[] newArray(int i) {
            return new MiuiVpnInfo[i];
        }
    };
    protected static final int VPN_STATE_DISABLED = 2;
    protected static final int VPN_STATE_ENABLED = 1;
    protected static final int VPN_STATE_NOORDER = 3;
    protected static final int VPN_STATE_UNKNOWN = 0;
    private String mDescribe;
    private String mDetailInfoUrl;
    private String mName;
    private int mState = 0;
    private int mType;

    public MiuiVpnInfo(int i, String str, String str2, String str3, int i2) {
        this.mType = i;
        this.mName = str;
        this.mDescribe = str2;
        this.mDetailInfoUrl = str3;
        this.mState = i2;
    }

    public MiuiVpnInfo(Parcel parcel) {
        this.mType = parcel.readInt();
        this.mName = parcel.readString();
        this.mDescribe = parcel.readString();
        this.mDetailInfoUrl = parcel.readString();
        this.mState = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public String getDescribe() {
        return this.mDescribe;
    }

    public String getDetailInfoUrl() {
        return this.mDetailInfoUrl;
    }

    public String getName() {
        return this.mName;
    }

    public int getState() {
        return this.mState;
    }

    public int getType() {
        return this.mType;
    }

    public void setState(int i) {
        this.mState = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mType);
        parcel.writeString(this.mName);
        parcel.writeString(this.mDescribe);
        parcel.writeString(this.mDetailInfoUrl);
        parcel.writeInt(this.mState);
    }
}
