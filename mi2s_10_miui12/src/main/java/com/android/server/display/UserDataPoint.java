package com.android.server.display;

public class UserDataPoint {
    private float userBrightness;
    private float userLux;

    public UserDataPoint(float userLux2, float userBrightness2) {
        this.userLux = userLux2;
        this.userBrightness = userBrightness2;
    }

    public float getUserLux() {
        return this.userLux;
    }

    public float getUserBrightness() {
        return this.userBrightness;
    }

    public void setUserLux(float userLux2) {
        this.userLux = userLux2;
    }

    public void setUserBrightness(float userBrightness2) {
        this.userBrightness = userBrightness2;
    }
}
