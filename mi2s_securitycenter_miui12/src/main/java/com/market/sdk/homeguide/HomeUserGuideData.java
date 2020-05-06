package com.market.sdk.homeguide;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.market.sdk.AbsParcelable;

public class HomeUserGuideData extends AbsParcelable {
    public static final Parcelable.Creator<HomeUserGuideData> CREATOR = new c();
    private Uri homeScreen;
    private String localFilePath;
    private e viewConfig;

    public HomeUserGuideData() {
    }

    protected HomeUserGuideData(Parcel parcel) {
        super(parcel);
        this.viewConfig = (e) parcel.readSerializable();
        this.homeScreen = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public Uri getHomeScreenUri() {
        return this.homeScreen;
    }

    public String getLocalFilePath() {
        return this.localFilePath;
    }

    public e getViewConfig() {
        return this.viewConfig;
    }

    public boolean isValid() {
        e eVar = this.viewConfig;
        return (eVar == null || !eVar.a() || this.homeScreen == null) ? false : true;
    }

    public void setHomeScreenUri(Uri uri) {
        this.homeScreen = uri;
    }

    public void setLocalFilePath(String str) {
        this.localFilePath = str;
    }

    public void setViewConfig(e eVar) {
        this.viewConfig = eVar;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeSerializable(this.viewConfig);
        parcel.writeParcelable(this.homeScreen, 0);
    }
}
