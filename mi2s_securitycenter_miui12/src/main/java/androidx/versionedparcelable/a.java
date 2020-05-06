package androidx.versionedparcelable;

import android.os.Parcel;
import android.os.Parcelable;

class a implements Parcelable.Creator<ParcelImpl> {
    a() {
    }

    public ParcelImpl createFromParcel(Parcel parcel) {
        return new ParcelImpl(parcel);
    }

    public ParcelImpl[] newArray(int i) {
        return new ParcelImpl[i];
    }
}
