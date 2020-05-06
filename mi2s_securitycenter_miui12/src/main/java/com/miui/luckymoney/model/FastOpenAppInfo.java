package com.miui.luckymoney.model;

import android.content.pm.PackageInfo;
import java.util.ArrayList;

public class FastOpenAppInfo {
    private boolean mIsFastOpen;
    private ArrayList<PackageInfo> mPackageInfos = new ArrayList<>();
    private String mTitle;

    public FastOpenAppInfo(boolean z) {
        this.mIsFastOpen = z;
    }

    public boolean add(PackageInfo packageInfo) {
        return this.mPackageInfos.add(packageInfo);
    }

    public ArrayList<PackageInfo> getPackageInfos() {
        return this.mPackageInfos;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public boolean isFastOpen() {
        return this.mIsFastOpen;
    }

    public void setFastOpen(boolean z) {
        this.mIsFastOpen = z;
    }

    public void setPackageInfos(ArrayList<PackageInfo> arrayList) {
        this.mPackageInfos = arrayList;
    }

    public void setTitle(String str) {
        this.mTitle = str;
    }
}
