package androidx.fragment.app;

import android.os.Parcel;
import android.os.Parcelable;

class u implements Parcelable.Creator<FragmentManagerState> {
    u() {
    }

    public FragmentManagerState createFromParcel(Parcel parcel) {
        return new FragmentManagerState(parcel);
    }

    public FragmentManagerState[] newArray(int i) {
        return new FragmentManagerState[i];
    }
}
