package com.miui.gamebooster.widget;

import android.os.Parcel;
import android.os.Parcelable;
import com.miui.gamebooster.widget.SwitchButton;

class f implements Parcelable.Creator<SwitchButton.SavedState> {
    f() {
    }

    public SwitchButton.SavedState createFromParcel(Parcel parcel) {
        return new SwitchButton.SavedState(parcel);
    }

    public SwitchButton.SavedState[] newArray(int i) {
        return new SwitchButton.SavedState[i];
    }
}
