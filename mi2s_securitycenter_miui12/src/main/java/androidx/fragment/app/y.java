package androidx.fragment.app;

import android.os.Parcel;
import android.os.Parcelable;

class y implements Parcelable.Creator<FragmentTabHost$SavedState> {
    y() {
    }

    public FragmentTabHost$SavedState createFromParcel(Parcel parcel) {
        return new FragmentTabHost$SavedState(parcel);
    }

    public FragmentTabHost$SavedState[] newArray(int i) {
        return new FragmentTabHost$SavedState[i];
    }
}
