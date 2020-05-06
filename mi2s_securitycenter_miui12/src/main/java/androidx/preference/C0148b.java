package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.preference.EditTextPreference;

/* renamed from: androidx.preference.b  reason: case insensitive filesystem */
class C0148b implements Parcelable.Creator<EditTextPreference.SavedState> {
    C0148b() {
    }

    public EditTextPreference.SavedState createFromParcel(Parcel parcel) {
        return new EditTextPreference.SavedState(parcel);
    }

    public EditTextPreference.SavedState[] newArray(int i) {
        return new EditTextPreference.SavedState[i];
    }
}
