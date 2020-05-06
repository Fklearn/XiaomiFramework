package androidx.appcompat.widget;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.appcompat.widget.AppCompatSpinner;

class E implements Parcelable.Creator<AppCompatSpinner.SavedState> {
    E() {
    }

    public AppCompatSpinner.SavedState createFromParcel(Parcel parcel) {
        return new AppCompatSpinner.SavedState(parcel);
    }

    public AppCompatSpinner.SavedState[] newArray(int i) {
        return new AppCompatSpinner.SavedState[i];
    }
}
