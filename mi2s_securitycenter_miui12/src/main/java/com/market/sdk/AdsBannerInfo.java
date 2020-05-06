package com.market.sdk;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class AdsBannerInfo implements Parcelable {
    public static final Parcelable.Creator<AdsBannerInfo> CREATOR = new a();
    @SerializedName("iconUri")
    public Uri iconUri;
    @SerializedName("iconUrl")
    public String iconUrl;
    @SerializedName("uri")
    public Uri uri;

    public AdsBannerInfo() {
    }

    public AdsBannerInfo(Parcel parcel) {
        this.iconUrl = parcel.readString();
        this.uri = (Uri) Uri.CREATOR.createFromParcel(parcel);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.iconUrl);
        Uri.writeToParcel(parcel, this.uri);
    }
}
