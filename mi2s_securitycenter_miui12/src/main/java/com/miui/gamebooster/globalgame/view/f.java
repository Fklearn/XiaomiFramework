package com.miui.gamebooster.globalgame.view;

import android.os.Parcel;
import android.os.Parcelable;

class f implements Parcelable.Creator<SavedState> {
    f() {
    }

    public SavedState createFromParcel(Parcel parcel) {
        return new SavedState(parcel, (f) null);
    }

    public SavedState[] newArray(int i) {
        return new SavedState[i];
    }
}
