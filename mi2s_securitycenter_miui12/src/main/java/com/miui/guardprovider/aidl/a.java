package com.miui.guardprovider.aidl;

import android.os.Parcel;
import android.os.Parcelable;

class a implements Parcelable.Creator<UpdateInfo> {
    a() {
    }

    public UpdateInfo createFromParcel(Parcel parcel) {
        return new UpdateInfo(parcel);
    }

    public UpdateInfo[] newArray(int i) {
        return new UpdateInfo[i];
    }
}
