package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.preference.MultiSelectListPreference;

/* renamed from: androidx.preference.h  reason: case insensitive filesystem */
class C0154h implements Parcelable.Creator<MultiSelectListPreference.SavedState> {
    C0154h() {
    }

    public MultiSelectListPreference.SavedState createFromParcel(Parcel parcel) {
        return new MultiSelectListPreference.SavedState(parcel);
    }

    public MultiSelectListPreference.SavedState[] newArray(int i) {
        return new MultiSelectListPreference.SavedState[i];
    }
}
