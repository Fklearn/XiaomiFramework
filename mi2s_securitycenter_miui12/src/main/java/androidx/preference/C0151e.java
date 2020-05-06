package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.preference.ListPreference;

/* renamed from: androidx.preference.e  reason: case insensitive filesystem */
class C0151e implements Parcelable.Creator<ListPreference.SavedState> {
    C0151e() {
    }

    public ListPreference.SavedState createFromParcel(Parcel parcel) {
        return new ListPreference.SavedState(parcel);
    }

    public ListPreference.SavedState[] newArray(int i) {
        return new ListPreference.SavedState[i];
    }
}
