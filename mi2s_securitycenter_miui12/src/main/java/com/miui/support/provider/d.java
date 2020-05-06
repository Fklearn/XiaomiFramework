package com.miui.support.provider;

import android.os.Parcel;
import android.os.Parcelable;
import com.miui.support.provider.MiuiSettingsCompat$SettingsCloudData;

class d implements Parcelable.Creator<MiuiSettingsCompat$SettingsCloudData.CloudData> {
    d() {
    }

    public MiuiSettingsCompat$SettingsCloudData.CloudData createFromParcel(Parcel parcel) {
        return new MiuiSettingsCompat$SettingsCloudData.CloudData(parcel.readString());
    }

    public MiuiSettingsCompat$SettingsCloudData.CloudData[] newArray(int i) {
        return new MiuiSettingsCompat$SettingsCloudData.CloudData[i];
    }
}
