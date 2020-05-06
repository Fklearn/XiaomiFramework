package miuix.core.widget;

import android.os.Parcel;
import android.os.Parcelable;

class a implements Parcelable.Creator<NestedScrollView$SavedState> {
    a() {
    }

    public NestedScrollView$SavedState createFromParcel(Parcel parcel) {
        return new NestedScrollView$SavedState(parcel);
    }

    public NestedScrollView$SavedState[] newArray(int i) {
        return new NestedScrollView$SavedState[i];
    }
}
