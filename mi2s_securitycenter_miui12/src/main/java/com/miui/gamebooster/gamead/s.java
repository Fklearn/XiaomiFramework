package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import com.miui.gamebooster.gamead.User;

class s implements Parcelable.Creator<User.ExamInfo> {
    s() {
    }

    public User.ExamInfo createFromParcel(Parcel parcel) {
        return new User.ExamInfo(parcel);
    }

    public User.ExamInfo[] newArray(int i) {
        return new User.ExamInfo[i];
    }
}
