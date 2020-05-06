package android.hardware.miuiface;

import android.os.Parcel;
import android.os.Parcelable;

public final class Miuiface implements Parcelable {
    public static final Parcelable.Creator<Miuiface> CREATOR = new Parcelable.Creator<Miuiface>() {
        public Miuiface createFromParcel(Parcel parcel) {
            return new Miuiface(parcel);
        }

        public Miuiface[] newArray(int i) {
            return new Miuiface[i];
        }
    };
    private long mDeviceId;
    private int mGroupId;
    private int mMiuifaceId;
    private CharSequence mName;

    private Miuiface(Parcel parcel) {
        this.mName = parcel.readString();
        this.mGroupId = parcel.readInt();
        this.mMiuifaceId = parcel.readInt();
        this.mDeviceId = parcel.readLong();
    }

    public Miuiface(CharSequence charSequence, int i, int i2, long j) {
        this.mName = charSequence;
        this.mGroupId = i;
        this.mMiuifaceId = i2;
        this.mDeviceId = j;
    }

    public int describeContents() {
        return 0;
    }

    public long getDeviceId() {
        return this.mDeviceId;
    }

    public int getGroupId() {
        return this.mGroupId;
    }

    public int getMiuifaceId() {
        return this.mMiuifaceId;
    }

    public CharSequence getName() {
        return this.mName;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mName.toString());
        parcel.writeInt(this.mGroupId);
        parcel.writeInt(this.mMiuifaceId);
        parcel.writeLong(this.mDeviceId);
    }
}
