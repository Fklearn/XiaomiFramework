package com.miui.guardprovider.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateInfo implements Parcelable {
    public static final Parcelable.Creator<UpdateInfo> CREATOR = new a();
    public String engineName;
    public int updateResult;

    public UpdateInfo() {
    }

    public UpdateInfo(Parcel parcel) {
        this.engineName = parcel.readString();
        this.updateResult = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
        this.engineName = parcel.readString();
        this.updateResult = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.engineName);
        parcel.writeInt(this.updateResult);
    }
}
