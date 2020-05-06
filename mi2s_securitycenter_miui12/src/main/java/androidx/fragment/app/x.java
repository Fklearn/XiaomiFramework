package androidx.fragment.app;

import android.os.Parcel;
import android.os.Parcelable;

class x implements Parcelable.Creator<FragmentState> {
    x() {
    }

    public FragmentState createFromParcel(Parcel parcel) {
        return new FragmentState(parcel);
    }

    public FragmentState[] newArray(int i) {
        return new FragmentState[i];
    }
}
