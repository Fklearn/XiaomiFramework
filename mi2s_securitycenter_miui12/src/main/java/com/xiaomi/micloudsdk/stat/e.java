package com.xiaomi.micloudsdk.stat;

import android.os.Parcel;
import android.os.Parcelable;

class e implements Parcelable.Creator<NetSuccessStatParam> {
    e() {
    }

    public NetSuccessStatParam createFromParcel(Parcel parcel) {
        return new NetSuccessStatParam(parcel);
    }

    public NetSuccessStatParam[] newArray(int i) {
        return new NetSuccessStatParam[i];
    }
}
