package com.miui.gamebooster.model;

import android.support.annotation.Keep;
import android.text.TextUtils;
import android.util.Log;
import com.miui.gamebooster.globalgame.util.NoProguard;

@Keep
@NoProguard
public class ActiveModel {
    private static final String TAG = "ActiveModel";
    private String activityText;
    private String beginTime;
    private String browserUrl;
    private String expireTime;
    private String gamePkgName;
    private String gamePkgNameCn;
    private boolean hasBubbleShow;
    private boolean hasRedPointShow;
    private String id;
    private String imgUrl;
    private String period;
    private long preReqeustTime;
    private int showPoint;

    public String getActivityText() {
        return this.activityText;
    }

    public String getBeginTime() {
        return this.beginTime;
    }

    public String getBrowserUrl() {
        return this.browserUrl;
    }

    public String getExpireTime() {
        return this.expireTime;
    }

    public String getGamePkgName() {
        return this.gamePkgName;
    }

    public String getGamePkgNameCn() {
        return this.gamePkgNameCn;
    }

    public String getId() {
        return this.id;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public long getPeriod() {
        try {
            if (!TextUtils.isEmpty(this.period)) {
                return Long.parseLong(this.period) * 60 * 60 * 1000;
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "number format error", e);
            return 0;
        }
    }

    public long getPreReqeustTime() {
        return this.preReqeustTime;
    }

    public int getShowPoint() {
        return this.showPoint;
    }

    public boolean isHasBubbleShow() {
        return this.hasBubbleShow;
    }

    public boolean isHasRedPointShow() {
        return this.hasRedPointShow;
    }

    public void setActivityText(String str) {
        this.activityText = str;
    }

    public void setBeginTime(String str) {
        this.beginTime = str;
    }

    public void setBrowserUrl(String str) {
        this.browserUrl = str;
    }

    public void setExpireTime(String str) {
        this.expireTime = str;
    }

    public void setGamePkgName(String str) {
        this.gamePkgName = str;
    }

    public void setGamePkgNameCn(String str) {
        this.gamePkgNameCn = str;
    }

    public void setHasBubbleShow(boolean z) {
        this.hasBubbleShow = z;
    }

    public void setHasRedPointShow(boolean z) {
        this.hasRedPointShow = z;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setImgUrl(String str) {
        this.imgUrl = str;
    }

    public void setPeriod(String str) {
        this.period = str;
    }

    public void setPreReqeustTime(long j) {
        this.preReqeustTime = j;
    }

    public void setShowPoint(int i) {
        this.showPoint = i;
    }
}
