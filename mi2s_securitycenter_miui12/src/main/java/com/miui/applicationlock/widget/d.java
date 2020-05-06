package com.miui.applicationlock.widget;

import android.os.Parcel;
import android.os.Parcelable;
import com.miui.applicationlock.widget.LockPatternView;

class d implements Parcelable.Creator<LockPatternView.SavedState> {
    d() {
    }

    public LockPatternView.SavedState createFromParcel(Parcel parcel) {
        return new LockPatternView.SavedState(parcel);
    }

    public LockPatternView.SavedState[] newArray(int i) {
        return new LockPatternView.SavedState[i];
    }
}
