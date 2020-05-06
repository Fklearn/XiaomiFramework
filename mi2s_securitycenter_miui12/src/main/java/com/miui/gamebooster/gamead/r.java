package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class r implements Parcelable.Creator<User> {
    r() {
    }

    public User createFromParcel(Parcel parcel) {
        return new User(parcel);
    }

    public User[] newArray(int i) {
        return new User[i];
    }
}
