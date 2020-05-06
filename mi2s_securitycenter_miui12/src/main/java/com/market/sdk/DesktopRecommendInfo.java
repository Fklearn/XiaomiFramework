package com.market.sdk;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class DesktopRecommendInfo implements Parcelable {
    public static final Parcelable.Creator<DesktopRecommendInfo> CREATOR = new h();
    public static final long DEFAULT_CACHE_TIME = 300000;
    @SerializedName("appInfoList")
    public List<AppstoreAppInfo> appInfoList = new ArrayList();
    @SerializedName("backgroundImageUrl")
    public String backgroundImageUrl = "";
    @SerializedName("bannerList")
    public List<AdsBannerInfo> bannerList = new ArrayList();
    @SerializedName("cacheTime")
    public long cacheTime;
    @SerializedName("description")
    public String description = "";
    @SerializedName("folderId")
    public long folderId = -1;
    @SerializedName("sid")
    public String sid = "";

    public DesktopRecommendInfo() {
    }

    public DesktopRecommendInfo(Parcel parcel) {
        this.folderId = parcel.readLong();
        parcel.readTypedList(this.appInfoList, AppstoreAppInfo.CREATOR);
        parcel.readTypedList(this.bannerList, AdsBannerInfo.CREATOR);
        this.backgroundImageUrl = parcel.readString();
        this.description = parcel.readString();
        this.sid = parcel.readString();
        this.cacheTime = parcel.readLong();
    }

    public static DesktopRecommendInfo restore(String str) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Uri.class, new g());
        return (DesktopRecommendInfo) gsonBuilder.create().fromJson(str, DesktopRecommendInfo.class);
    }

    public String convertToJson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Uri.class, new f(this));
        return gsonBuilder.create().toJson((Object) this);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.folderId);
        parcel.writeTypedList(this.appInfoList);
        parcel.writeTypedList(this.bannerList);
        parcel.writeString(this.backgroundImageUrl);
        parcel.writeString(this.description);
        parcel.writeString(this.sid);
        parcel.writeLong(this.cacheTime);
    }
}
