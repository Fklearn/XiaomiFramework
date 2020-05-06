package com.market.sdk;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class AbsParcelable implements Parcelable {
    protected int version = 1;

    protected AbsParcelable() {
    }

    protected AbsParcelable(Parcel parcel) {
        this.version = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.version);
    }
}
