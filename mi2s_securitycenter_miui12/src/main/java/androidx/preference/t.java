package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.preference.PreferenceGroup;

class t implements Parcelable.Creator<PreferenceGroup.SavedState> {
    t() {
    }

    public PreferenceGroup.SavedState createFromParcel(Parcel parcel) {
        return new PreferenceGroup.SavedState(parcel);
    }

    public PreferenceGroup.SavedState[] newArray(int i) {
        return new PreferenceGroup.SavedState[i];
    }
}
