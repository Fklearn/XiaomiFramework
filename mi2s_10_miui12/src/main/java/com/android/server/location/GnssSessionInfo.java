package com.android.server.location;

import java.util.Calendar;

public class GnssSessionInfo {
    public static final int STATE_FIX = 2;
    public static final int STATE_INIT = 0;
    public static final int STATE_LOSE = 4;
    public static final int STATE_SAVE = 5;
    public static final int STATE_START = 1;
    public static final int STATE_STOP = 3;
    public static final int STATE_UNKNOWN = 100;
    private int mCurrentState;
    private long mEndTime;
    private int mLostTimes;
    private String mPackName;
    private long mStartTime;
    private long mTTFF;

    public GnssSessionInfo() {
        this.mPackName = null;
        this.mStartTime = 0;
        this.mEndTime = 0;
        this.mTTFF = -1;
        this.mLostTimes = 0;
        this.mCurrentState = 100;
        this.mCurrentState = 100;
        this.mPackName = null;
        this.mStartTime = 0;
        this.mEndTime = 0;
        this.mTTFF = -1;
        this.mLostTimes = 0;
    }

    public GnssSessionInfo(String pack, long starttime, long endtime, long ttff, int lost, int state) {
        this.mPackName = null;
        this.mStartTime = 0;
        this.mEndTime = 0;
        this.mTTFF = -1;
        this.mLostTimes = 0;
        this.mCurrentState = 100;
        this.mPackName = pack;
        this.mStartTime = starttime;
        this.mEndTime = endtime;
        this.mTTFF = ttff;
        this.mLostTimes = lost;
        this.mCurrentState = state;
    }

    public void setStart(String pack) {
        this.mStartTime = getCurrentTime();
        this.mPackName = pack;
        this.mCurrentState = 1;
    }

    public void setStart() {
        this.mStartTime = getCurrentTime();
        this.mCurrentState = 1;
    }

    public void setEnd() {
        this.mEndTime = getCurrentTime();
        this.mCurrentState = 3;
    }

    public void setTtffManually(long ttff) {
        this.mTTFF = ttff;
        this.mCurrentState = 2;
    }

    public void setLostTimes() {
        this.mLostTimes++;
        this.mCurrentState = 4;
    }

    public void setLostTimes(int lostTimes) {
        this.mLostTimes = lostTimes;
    }

    public void setTtffAuto() {
        long cur = getCurrentTime();
        long j = this.mStartTime;
        if (cur > j) {
            this.mTTFF = (cur - j) / 1000;
        } else {
            this.mTTFF = -1;
        }
        this.mCurrentState = 2;
    }

    public void newSessionReset() {
        this.mPackName = null;
        this.mStartTime = 0;
        this.mEndTime = 0;
        this.mTTFF = -1;
        this.mLostTimes = 0;
        this.mCurrentState = 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0033, code lost:
        r0 = r7.mCurrentState;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkValidity() {
        /*
            r7 = this;
            int r0 = r7.mCurrentState
            r1 = 0
            r3 = 1
            r4 = 0
            if (r0 != r3) goto L_0x000f
            long r5 = r7.mStartTime
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x000f
            return r4
        L_0x000f:
            int r0 = r7.mCurrentState
            r5 = 3
            if (r0 != r5) goto L_0x001b
            long r5 = r7.mEndTime
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x001b
            return r4
        L_0x001b:
            int r0 = r7.mCurrentState
            r1 = 4
            if (r0 != r1) goto L_0x0025
            int r0 = r7.mLostTimes
            if (r0 != 0) goto L_0x0025
            return r4
        L_0x0025:
            int r0 = r7.mCurrentState
            r1 = 2
            if (r0 != r1) goto L_0x0033
            long r0 = r7.mTTFF
            r5 = -1
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x0033
            return r4
        L_0x0033:
            int r0 = r7.mCurrentState
            r1 = 100
            if (r0 == r1) goto L_0x003d
            if (r0 != 0) goto L_0x003c
            goto L_0x003d
        L_0x003c:
            return r3
        L_0x003d:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.location.GnssSessionInfo.checkValidity():boolean");
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public long getTtff() {
        return this.mTTFF;
    }

    public String getPackName() {
        return this.mPackName;
    }

    public int getStartTimeInHour() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(this.mStartTime);
        return c.get(11);
    }

    public long getRunTime() {
        long j = this.mEndTime;
        long j2 = this.mStartTime;
        if (j >= j2) {
            return (j - j2) / 1000;
        }
        return -1;
    }

    public int getLoseTimes() {
        return this.mLostTimes;
    }

    public String toString() {
        return "state = " + this.mCurrentState + " start time = " + this.mStartTime + " ttff = " + this.mTTFF + " end time = " + this.mEndTime + " lose times = " + this.mLostTimes;
    }
}
