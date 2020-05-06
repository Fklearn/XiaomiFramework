package com.android.server.lights;

import android.icu.text.SimpleDateFormat;
import android.text.TextUtils;
import java.util.Date;

public class LightState {
    public int brightnessMode;
    public String callingPackage;
    public int colorARGB;
    public int flashMode;
    public long mAddedTime;
    public int mId;
    public int offMS;
    public int onMS;
    public int styleType;

    public LightState(int colorARGB2, int flashMode2, int onMS2, int offMS2, int brightnessMode2) {
        this.colorARGB = colorARGB2;
        this.flashMode = flashMode2;
        this.onMS = onMS2;
        this.offMS = offMS2;
        this.brightnessMode = brightnessMode2;
    }

    public LightState(int mId2, int colorARGB2, int flashMode2, int onMS2, int offMS2, int brightnessMode2) {
        this.mAddedTime = System.currentTimeMillis();
        this.mId = mId2;
        this.colorARGB = colorARGB2;
        this.flashMode = flashMode2;
        this.onMS = onMS2;
        this.offMS = offMS2;
        this.brightnessMode = brightnessMode2;
    }

    public LightState(String callingPackage2, int styleType2) {
        this.mAddedTime = System.currentTimeMillis();
        this.styleType = styleType2;
        this.callingPackage = callingPackage2;
    }

    public String toString() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(this.mAddedTime));
        StringBuilder builder = new StringBuilder();
        builder.append(", mAddedTime=");
        builder.append(date);
        if (!TextUtils.isEmpty(this.callingPackage)) {
            builder.append(", callingPackage=");
            builder.append(this.callingPackage);
        }
        if (this.mId != 0) {
            builder.append(", mId=");
            builder.append(this.mId);
        }
        if (this.styleType != 0) {
            builder.append(", mLastLightStyle=");
            builder.append(this.styleType);
        }
        if (this.colorARGB != 0) {
            builder.append(", colorARGB=");
            builder.append(this.colorARGB);
        }
        if (this.onMS != 0) {
            builder.append(", onMS=");
            builder.append(this.onMS);
        }
        if (this.offMS != 0) {
            builder.append(", offMS=");
            builder.append(this.offMS);
        }
        if (this.flashMode != 0) {
            builder.append(", flashMode=");
            builder.append(this.flashMode);
        }
        if (this.brightnessMode != 0) {
            builder.append(", brightnessMode=");
            builder.append(this.brightnessMode);
        }
        return builder.toString();
    }
}
