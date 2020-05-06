package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.preference.Preference;

/* renamed from: androidx.preference.l  reason: case insensitive filesystem */
class C0158l implements Parcelable.Creator<Preference.BaseSavedState> {
    C0158l() {
    }

    public Preference.BaseSavedState createFromParcel(Parcel parcel) {
        return new Preference.BaseSavedState(parcel);
    }

    public Preference.BaseSavedState[] newArray(int i) {
        return new Preference.BaseSavedState[i];
    }
}
