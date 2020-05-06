package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class p implements Parcelable.Creator<ReplyInfo> {
    p() {
    }

    public ReplyInfo createFromParcel(Parcel parcel) {
        return new ReplyInfo(parcel);
    }

    public ReplyInfo[] newArray(int i) {
        return new ReplyInfo[i];
    }
}
