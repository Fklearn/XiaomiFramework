package com.market.sdk.homeguide;

import android.os.Parcel;
import android.os.Parcelable;
import com.market.sdk.AbsParcelable;

public class HomeUserGuideResult extends AbsParcelable {
    public static final Parcelable.Creator<HomeUserGuideResult> CREATOR = new d();
    public static final int ERROR_INVALID_HOME_SCREEN = 1;
    public static final int ERROR_INVALID_ICON_POS = 2;
    public static final int OK = 0;
    private int errorCode = 0;

    public HomeUserGuideResult(int i) {
        this.errorCode = i;
    }

    public HomeUserGuideResult(Parcel parcel) {
        super(parcel);
        this.errorCode = parcel.readInt();
    }

    public static HomeUserGuideResult of(int i) {
        return new HomeUserGuideResult(i);
    }

    public int describeContents() {
        return 0;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.errorCode);
    }
}
