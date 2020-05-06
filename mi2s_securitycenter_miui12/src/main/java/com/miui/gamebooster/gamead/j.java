package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import com.miui.gamebooster.gamead.GameInfo;

class j implements Parcelable.Creator<GameInfo.Tag> {
    j() {
    }

    public GameInfo.Tag createFromParcel(Parcel parcel) {
        return new GameInfo.Tag(parcel);
    }

    public GameInfo.Tag[] newArray(int i) {
        return new GameInfo.Tag[i];
    }
}
