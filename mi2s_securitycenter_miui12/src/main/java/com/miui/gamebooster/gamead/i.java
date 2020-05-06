package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class i implements Parcelable.Creator<GameInfo> {
    i() {
    }

    public GameInfo createFromParcel(Parcel parcel) {
        return new GameInfo(parcel);
    }

    public GameInfo[] newArray(int i) {
        return new GameInfo[i];
    }
}
