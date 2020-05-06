package com.android.server.display;

public class AmbientLightDetails {
    static final int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    private int mBChannel;
    private int mCChannel;
    private int mGChannel;
    private int mLux;
    private int mOriginalBrightness;
    private int mRChannel;
    private int mTargetBrightness;
    private int mUseCount;

    AmbientLightDetails(int lux, int c, int r, int g, int b) {
        this.mLux = lux;
        this.mCChannel = c;
        this.mRChannel = r;
        this.mGChannel = g;
        this.mBChannel = b;
    }

    AmbientLightDetails() {
    }

    public int getLux() {
        return this.mLux;
    }

    public void setLux(int lux) {
        this.mLux = lux;
    }

    public int getCChannel() {
        return this.mCChannel;
    }

    public void setCChannel(int cChannel) {
        this.mCChannel = cChannel;
    }

    public int getRChannel() {
        return this.mRChannel;
    }

    public void setRChannel(int rChannel) {
        this.mRChannel = rChannel;
    }

    public int getGChannel() {
        return this.mGChannel;
    }

    public void setGChannel(int gChannel) {
        this.mGChannel = gChannel;
    }

    public int getBChannel() {
        return this.mBChannel;
    }

    public void setBChannel(int bChannel) {
        this.mBChannel = bChannel;
    }

    public int getUseCount() {
        return this.mUseCount;
    }

    public void setUseCount(int useCount) {
        this.mUseCount = useCount;
    }

    public int getOriginalBrightness() {
        return this.mOriginalBrightness;
    }

    public void setOriginalBrightness(int originalBrightness) {
        this.mOriginalBrightness = originalBrightness;
    }

    public int getTargetBrightness() {
        return this.mTargetBrightness;
    }

    public void setTargetBrightness(int targetBrightness) {
        this.mTargetBrightness = targetBrightness;
    }

    public String toString() {
        return "AmbientLightDetails{Lux=" + this.mLux + ", C=" + this.mCChannel + ", R=" + this.mRChannel + ", G=" + this.mGChannel + ", B=" + this.mBChannel + ", OriginalBrightness=" + this.mOriginalBrightness + ", TargetBrightness=" + this.mTargetBrightness + '}';
    }

    public void buildApproximation() {
        this.mLux = getLuxOptimizationData(this.mLux);
        this.mCChannel = getLuxOptimizationData(this.mCChannel);
        this.mRChannel = getLuxOptimizationData(this.mRChannel);
        this.mGChannel = getLuxOptimizationData(this.mGChannel);
        this.mBChannel = getLuxOptimizationData(this.mBChannel);
        this.mOriginalBrightness = getBrightnessOptimizationData(this.mOriginalBrightness);
        this.mTargetBrightness = getBrightnessOptimizationData(this.mTargetBrightness);
    }

    private int stringSize(int x) {
        int i = 0;
        while (x > sizeTable[i]) {
            i++;
        }
        return i + 1;
    }

    private int getOptimizationSize(int size) {
        if (size == 0 || size == 1) {
            return 0;
        }
        if (size == 2 || size == 3) {
            return size - 1;
        }
        return size - 2;
    }

    private int getLuxOptimizationData(int data) {
        int size = getOptimizationSize(stringSize(data));
        return (int) (((double) Math.round((((double) data) / 1.0d) / Math.pow(10.0d, (double) size))) * Math.pow(10.0d, (double) size));
    }

    private int getBrightnessOptimizationData(int data) {
        int size = getOptimizationSize(stringSize(data));
        if (size >= 3) {
            size--;
        }
        return (int) (((double) Math.round((((double) data) / 1.0d) / Math.pow(10.0d, (double) size))) * Math.pow(10.0d, (double) size));
    }
}
