package com.miui.antivirus.model;

import android.os.Parcel;
import android.os.Parcelable;

class f implements Parcelable.Creator<DangerousInfo> {
    f() {
    }

    public DangerousInfo createFromParcel(Parcel parcel) {
        return new DangerousInfo(parcel, (f) null);
    }

    public DangerousInfo[] newArray(int i) {
        return new DangerousInfo[i];
    }
}
