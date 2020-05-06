package miuix.preference;

import android.os.Parcel;
import android.os.Parcelable;
import miuix.preference.DropDownPreference;

class g implements Parcelable.Creator<DropDownPreference.SavedState> {
    g() {
    }

    public DropDownPreference.SavedState createFromParcel(Parcel parcel) {
        return new DropDownPreference.SavedState(parcel);
    }

    public DropDownPreference.SavedState[] newArray(int i) {
        return new DropDownPreference.SavedState[i];
    }
}
