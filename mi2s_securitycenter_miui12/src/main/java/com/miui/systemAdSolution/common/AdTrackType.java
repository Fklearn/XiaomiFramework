package com.miui.systemAdSolution.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AdTrackType implements Parcelable {
    public static final Parcelable.Creator<AdTrackType> CREATOR = new Parcelable.Creator<AdTrackType>() {
        public AdTrackType createFromParcel(Parcel parcel) {
            return new AdTrackType(parcel);
        }

        public AdTrackType[] newArray(int i) {
            return new AdTrackType[i];
        }
    };
    private static final String TAG = "AdTrackType";
    private Type mValue;

    public enum Type {
        TRACK_UNKOWN,
        TRACK_VIEW,
        TRACK_CLICK,
        TRACK_FAIL,
        TRACK_COMPATIBLE,
        TRACK_DISLIKE_AD
    }

    protected AdTrackType(Parcel parcel) {
        int readInt = parcel != null ? parcel.readInt() : -1;
        if (readInt < 0 || readInt >= Type.values().length) {
            this.mValue = Type.TRACK_UNKOWN;
            Log.e(TAG, "the type[" + readInt + "] is not support.");
            return;
        }
        this.mValue = Type.values()[readInt];
    }

    public AdTrackType(Type type) {
        if (type != null) {
            this.mValue = type;
            return;
        }
        throw new IllegalArgumentException("type must not be null.");
    }

    public int describeContents() {
        return 0;
    }

    public Type getValue() {
        return this.mValue;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mValue.ordinal());
    }
}
