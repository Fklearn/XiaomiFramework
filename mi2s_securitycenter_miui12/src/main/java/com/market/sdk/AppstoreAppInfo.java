package com.market.sdk;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.gson.annotations.SerializedName;
import com.market.sdk.utils.e;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

public class AppstoreAppInfo implements Parcelable {
    public static final Parcelable.Creator<AppstoreAppInfo> CREATOR = new d();
    private static final long FLAG_MASK_SHOW_AD_FLAG = 1;
    private static final int SHOW_AD_FLAG = 0;
    private static boolean sNeedInmobiParamter;
    @SerializedName("adInfoPassback")
    public String adInfoPassback;
    @SerializedName("ads")
    public int ads;
    @SerializedName("appId")
    public String appId;
    @SerializedName("appUri")
    public Uri appUri;
    @SerializedName("clickMonitorUrls")
    public List<String> clickMonitorUrls = new ArrayList();
    @SerializedName("digest")
    public String digest;
    @SerializedName("experimentalId")
    public String experimentalId;
    @SerializedName("iconMask")
    public String iconMask;
    @SerializedName("iconUri")
    public Uri iconUri;
    @SerializedName("impressionMonitorUrls")
    public List<String> impressionMonitorUrls = new ArrayList();
    @SerializedName("mApkBriefDescription")
    private String mApkBriefDescription;
    @SerializedName("mApkSize")
    private long mApkSize = -1;
    @SerializedName("mFlag")
    private volatile long mFlag = -1;
    @SerializedName("mParameters")
    private String mParameters;
    @SerializedName("pkgName")
    public String pkgName;
    @SerializedName("title")
    public String title;
    @SerializedName("viewMonitorUrls")
    public List<String> viewMonitorUrls = new ArrayList();

    static {
        boolean b2;
        try {
            if (Build.IS_DEVELOPMENT_VERSION) {
                b2 = e.a("6.3.21");
            } else {
                if (Build.IS_STABLE_VERSION) {
                    b2 = e.b("V7.3.0.0");
                }
            }
            sNeedInmobiParamter = b2;
        } catch (Throwable th) {
            Log.e("MarketManager", th.toString());
        }
    }

    public AppstoreAppInfo() {
    }

    public AppstoreAppInfo(Parcel parcel) {
        this.appId = parcel.readString();
        this.pkgName = parcel.readString();
        this.title = parcel.readString();
        this.ads = parcel.readInt();
        this.digest = parcel.readString();
        this.experimentalId = parcel.readString();
        this.iconMask = parcel.readString();
        this.iconUri = (Uri) Uri.CREATOR.createFromParcel(parcel);
        this.appUri = (Uri) Uri.CREATOR.createFromParcel(parcel);
        if (sNeedInmobiParamter) {
            parcel.readStringList(this.viewMonitorUrls);
            parcel.readStringList(this.clickMonitorUrls);
            parcel.readStringList(this.impressionMonitorUrls);
            this.adInfoPassback = parcel.readString();
        }
    }

    private long getFlag() {
        if (this.mFlag != -1) {
            return this.mFlag;
        }
        Uri uri = this.appUri;
        long j = 0;
        if (uri != null) {
            try {
                j = Long.parseLong(uri.getQueryParameter("config"));
            } catch (Exception unused) {
            }
        }
        this.mFlag = j;
        return this.mFlag;
    }

    public int describeContents() {
        return 0;
    }

    public long getApkSize() {
        return this.mApkSize;
    }

    public String getBriefDescription() {
        return this.mApkBriefDescription;
    }

    public String getParameters() {
        return this.mParameters;
    }

    public void setApkSize(long j) {
        this.mApkSize = j;
    }

    public void setBriefDescription(String str) {
        this.mApkBriefDescription = str;
    }

    public void setParameters(String str) {
        this.mParameters = str;
    }

    public boolean shouldShowAdFlag() {
        return this.ads == 1 && (getFlag() & 1) == 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.appId);
        parcel.writeString(this.pkgName);
        parcel.writeString(this.title);
        parcel.writeInt(this.ads);
        parcel.writeString(this.digest);
        parcel.writeString(this.experimentalId);
        parcel.writeString(this.iconMask);
        Uri.writeToParcel(parcel, this.iconUri);
        Uri.writeToParcel(parcel, this.appUri);
        if (sNeedInmobiParamter) {
            parcel.writeStringList(this.viewMonitorUrls);
            parcel.writeStringList(this.clickMonitorUrls);
            parcel.writeStringList(this.impressionMonitorUrls);
            parcel.writeString(this.adInfoPassback);
        }
    }
}
