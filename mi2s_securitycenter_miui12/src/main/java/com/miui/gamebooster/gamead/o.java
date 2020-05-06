package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class o implements Parcelable.Creator<MixedContent> {
    o() {
    }

    public MixedContent createFromParcel(Parcel parcel) {
        return new MixedContent(parcel);
    }

    public MixedContent[] newArray(int i) {
        return new MixedContent[i];
    }
}
