package com.miui.networkassistant.model;

public class AppDataUsage {
    private long mRxBytes = 0;
    private long mTxBytes = 0;

    public void addRxBytes(long j) {
        this.mRxBytes += j;
    }

    public void addTxBytes(long j) {
        this.mTxBytes += j;
    }

    public long getRxBytes() {
        return this.mRxBytes;
    }

    public long getTotal() {
        return this.mTxBytes + this.mRxBytes;
    }

    public long getTxBytes() {
        return this.mTxBytes;
    }

    public void reset() {
        this.mTxBytes = 0;
        this.mRxBytes = 0;
    }
}
