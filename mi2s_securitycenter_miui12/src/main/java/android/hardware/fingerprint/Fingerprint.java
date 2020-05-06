package android.hardware.fingerprint;

import android.os.Parcel;
import android.os.Parcelable;

public final class Fingerprint implements Parcelable {
    public static final Parcelable.Creator<Fingerprint> CREATOR = new Parcelable.Creator<Fingerprint>() {
        public Fingerprint createFromParcel(Parcel parcel) {
            return new Fingerprint(parcel);
        }

        public Fingerprint[] newArray(int i) {
            return new Fingerprint[i];
        }
    };
    private long mDeviceId;
    private int mFingerId;
    private int mGroupId;
    private CharSequence mName;

    private Fingerprint(Parcel parcel) {
        this.mName = parcel.readString();
        this.mGroupId = parcel.readInt();
        this.mFingerId = parcel.readInt();
        this.mDeviceId = parcel.readLong();
    }

    public Fingerprint(CharSequence charSequence, int i, int i2, long j) {
        this.mName = charSequence;
        this.mGroupId = i;
        this.mFingerId = i2;
        this.mDeviceId = j;
    }

    public int describeContents() {
        return 0;
    }

    public int getBiometricId() {
        return this.mFingerId;
    }

    public long getDeviceId() {
        return this.mDeviceId;
    }

    public int getFingerId() {
        return this.mFingerId;
    }

    public int getGroupId() {
        return this.mGroupId;
    }

    public CharSequence getName() {
        return this.mName;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mName.toString());
        parcel.writeInt(this.mGroupId);
        parcel.writeInt(this.mFingerId);
        parcel.writeLong(this.mDeviceId);
    }
}
