package androidx.versionedparcelable;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RestrictTo;

@SuppressLint({"BanParcelableUsage"})
@RestrictTo({RestrictTo.a.LIBRARY})
public class ParcelImpl implements Parcelable {
    public static final Parcelable.Creator<ParcelImpl> CREATOR = new a();
    private final d mParcel;

    protected ParcelImpl(Parcel parcel) {
        this.mParcel = new c(parcel).j();
    }

    public ParcelImpl(d dVar) {
        this.mParcel = dVar;
    }

    public int describeContents() {
        return 0;
    }

    public <T extends d> T getVersionedParcel() {
        return this.mParcel;
    }

    public void writeToParcel(Parcel parcel, int i) {
        new c(parcel).a(this.mParcel);
    }
}
