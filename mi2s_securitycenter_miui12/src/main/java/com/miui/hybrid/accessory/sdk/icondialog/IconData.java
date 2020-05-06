package com.miui.hybrid.accessory.sdk.icondialog;

import android.os.Parcel;
import android.os.Parcelable;

public class IconData implements Parcelable {
    public static final Parcelable.Creator<IconData> CREATOR = new Parcelable.Creator<IconData>() {
        /* renamed from: a */
        public IconData createFromParcel(Parcel parcel) {
            return new IconData(parcel);
        }

        /* renamed from: a */
        public IconData[] newArray(int i) {
            return new IconData[i];
        }
    };

    /* renamed from: a  reason: collision with root package name */
    public String f5587a;

    /* renamed from: b  reason: collision with root package name */
    public String f5588b;

    /* renamed from: c  reason: collision with root package name */
    public String f5589c;

    /* renamed from: d  reason: collision with root package name */
    public String f5590d;
    public long e;
    public boolean f = true;

    public IconData() {
    }

    protected IconData(Parcel parcel) {
        this.f5587a = parcel.readString();
        this.f5588b = parcel.readString();
        this.f5589c = parcel.readString();
        this.f5590d = parcel.readString();
        this.e = parcel.readLong();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.f5587a);
        parcel.writeString(this.f5588b);
        parcel.writeString(this.f5589c);
        parcel.writeString(this.f5590d);
        parcel.writeLong(this.e);
    }
}
