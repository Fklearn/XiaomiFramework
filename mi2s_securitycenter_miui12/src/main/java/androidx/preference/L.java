package androidx.preference;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.preference.SeekBarPreference;

class L implements Parcelable.Creator<SeekBarPreference.SavedState> {
    L() {
    }

    public SeekBarPreference.SavedState createFromParcel(Parcel parcel) {
        return new SeekBarPreference.SavedState(parcel);
    }

    public SeekBarPreference.SavedState[] newArray(int i) {
        return new SeekBarPreference.SavedState[i];
    }
}
