package com.xiaomi.micloudsdk.stat;

import android.os.Parcel;
import android.os.Parcelable;

class d implements Parcelable.Creator<NetFailedStatParam> {
    d() {
    }

    public NetFailedStatParam createFromParcel(Parcel parcel) {
        return new NetFailedStatParam(parcel);
    }

    public NetFailedStatParam[] newArray(int i) {
        return new NetFailedStatParam[i];
    }
}
