package com.miui.powercenter.deepsave;

import android.os.Parcel;
import android.os.Parcelable;

public class IdeaModel implements Parcelable {
    public static final Parcelable.Creator<IdeaModel> CREATOR = new h();
    public String packageName;
    public String title;
    public String url;

    public IdeaModel() {
    }

    public IdeaModel(Parcel parcel) {
        this.packageName = parcel.readString();
        this.title = parcel.readString();
        this.url = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeString(this.title);
        parcel.writeString(this.url);
    }
}
