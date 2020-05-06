package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;

class t implements Parcelable.Creator<UserSettingInfo> {
    t() {
    }

    public UserSettingInfo createFromParcel(Parcel parcel) {
        return new UserSettingInfo(parcel);
    }

    public UserSettingInfo[] newArray(int i) {
        return new UserSettingInfo[i];
    }
}
