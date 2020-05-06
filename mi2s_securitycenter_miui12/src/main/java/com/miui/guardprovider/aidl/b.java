package com.miui.guardprovider.aidl;

import android.os.Parcel;
import android.os.Parcelable;

class b implements Parcelable.Creator<VirusInfo> {
    b() {
    }

    public VirusInfo createFromParcel(Parcel parcel) {
        return new VirusInfo(parcel);
    }

    public VirusInfo[] newArray(int i) {
        return new VirusInfo[i];
    }
}
