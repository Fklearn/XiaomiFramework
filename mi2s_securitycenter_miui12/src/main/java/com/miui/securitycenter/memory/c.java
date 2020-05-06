package com.miui.securitycenter.memory;

import android.os.Parcel;
import android.os.Parcelable;

class c implements Parcelable.Creator<MemoryModel> {
    c() {
    }

    public MemoryModel createFromParcel(Parcel parcel) {
        return new MemoryModel(parcel);
    }

    public MemoryModel[] newArray(int i) {
        return new MemoryModel[i];
    }
}
