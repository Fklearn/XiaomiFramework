package com.miui.powercenter.autotask;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: com.miui.powercenter.autotask.c  reason: case insensitive filesystem */
class C0474c implements Parcelable.Creator<AutoTask> {
    C0474c() {
    }

    public AutoTask createFromParcel(Parcel parcel) {
        return new AutoTask(parcel);
    }

    public AutoTask[] newArray(int i) {
        return new AutoTask[i];
    }
}
