package com.miui.earthquakewarning.model;

import android.content.Context;
import android.util.Log;
import com.miui.earthquakewarning.soundplay.CalcCountdown;
import java.io.Serializable;
import java.util.Calendar;

public class UserQuakeItem extends QuakeItem implements Serializable {
    private static final String TAG = "UserQuakeItem";
    private int countTruth;
    private int countdown;
    private float distance;
    private float intensity;
    private boolean isReceiveOneMinLater = false;
    private boolean isTrigger;
    private LocationModel location = new LocationModel();
    private long xmUpdateTime;

    public boolean calIC(Context context) {
        CalcCountdown calcCountdown = new CalcCountdown();
        float distance2 = calcCountdown.distance(getEpiLocation().getLatitude(), getEpiLocation().getLongitude(), this.location.getLatitude(), this.location.getLongitude());
        this.intensity = calcCountdown.getIntensity(getMagnitude(), distance2);
        setDistance(distance2);
        this.countdown = (int) calcCountdown.getCountDownSeconds(getDepth(), distance2);
        setCountTruth(this.countdown);
        this.countdown -= (int) ((System.currentTimeMillis() - getStartTime()) / 1000);
        int i = this.countdown;
        if (i <= 300) {
            return i >= -300;
        }
        Log.i(TAG, "countdown is" + this.countdown + "seconds,too long!");
        return false;
    }

    public int getCountTruth() {
        return this.countTruth;
    }

    public int getCountdown() {
        return this.countdown;
    }

    public float getDistance() {
        return this.distance;
    }

    public float getIntensity() {
        return this.intensity;
    }

    public LocationModel getLocation() {
        return this.location;
    }

    public void getReceiveOneMinLater() {
        if (0 != getStartTime()) {
            setReceiveOneMinLater(Calendar.getInstance().getTimeInMillis() - getStartTime() >= 60000);
        }
    }

    public long getXmUpdateTime() {
        return this.xmUpdateTime;
    }

    public boolean isReceiveOneMinLater() {
        return this.isReceiveOneMinLater;
    }

    public boolean isTrigger() {
        return this.isTrigger;
    }

    public void setCountTruth(int i) {
        this.countTruth = i;
    }

    public void setCountdown(int i) {
        this.countdown = i;
    }

    public void setDistance(float f) {
        this.distance = f;
    }

    public void setLocation(LocationModel locationModel) {
        this.location = locationModel;
    }

    public void setReceiveOneMinLater(boolean z) {
        this.isReceiveOneMinLater = z;
    }

    public void setTrigger(boolean z) {
        this.isTrigger = z;
    }

    public void setXmUpdateTime(long j) {
        this.xmUpdateTime = j;
    }
}
