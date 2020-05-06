package com.miui.powercenter.abnormalscan;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class AbScanModel implements Parcelable {
    public static final Parcelable.Creator<AbScanModel> CREATOR = new b();
    private String mAbnormalPkg;
    private ArrayList mAbnormalReason;

    public AbScanModel(String str, ArrayList arrayList) {
        this.mAbnormalPkg = str;
        this.mAbnormalReason = arrayList;
    }

    public int describeContents() {
        return 0;
    }

    public String getAbnormalPkg() {
        return this.mAbnormalPkg;
    }

    public ArrayList getAbnormalReason() {
        return this.mAbnormalReason;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mAbnormalPkg);
        parcel.writeList(this.mAbnormalReason);
    }
}
