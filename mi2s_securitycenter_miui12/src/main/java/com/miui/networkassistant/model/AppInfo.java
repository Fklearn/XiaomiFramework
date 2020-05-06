package com.miui.networkassistant.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Cloneable, Parcelable {
    public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {
        public AppInfo createFromParcel(Parcel parcel) {
            return new AppInfo(parcel);
        }

        public AppInfo[] newArray(int i) {
            return new AppInfo[i];
        }
    };
    public boolean isSystemApp;
    public CharSequence packageName;
    public int tagId;
    public int uid;

    public AppInfo() {
    }

    private AppInfo(Parcel parcel) {
        readFromParcel(parcel);
    }

    public AppInfo(AppInfo appInfo) {
        this.uid = appInfo.uid;
        this.tagId = appInfo.tagId;
        this.packageName = appInfo.packageName;
        this.isSystemApp = appInfo.isSystemApp;
    }

    public AppInfo(CharSequence charSequence) {
        this.packageName = charSequence;
    }

    public AppInfo(CharSequence charSequence, int i) {
        this.packageName = charSequence;
        this.uid = i;
    }

    public AppInfo(CharSequence charSequence, int i, boolean z) {
        this.packageName = charSequence;
        this.isSystemApp = z;
        this.uid = i;
    }

    private void readFromParcel(Parcel parcel) {
        this.uid = parcel.readInt();
        this.tagId = parcel.readInt();
        this.packageName = parcel.readString();
        boolean z = true;
        if (parcel.readInt() != 1) {
            z = false;
        }
        this.isSystemApp = z;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AppInfo)) {
            return false;
        }
        AppInfo appInfo = (AppInfo) obj;
        return this.uid == appInfo.uid && this.packageName.equals(appInfo.packageName);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.uid);
        parcel.writeInt(this.tagId);
        CharSequence charSequence = this.packageName;
        parcel.writeString(charSequence == null ? null : charSequence.toString());
        parcel.writeInt(this.isSystemApp ? 1 : 0);
    }
}
