package com.miui.networkassistant.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TrafficUsedStatus implements Parcelable {
    public static final Parcelable.Creator<TrafficUsedStatus> CREATOR = new Parcelable.Creator<TrafficUsedStatus>() {
        public TrafficUsedStatus createFromParcel(Parcel parcel) {
            return new TrafficUsedStatus(parcel);
        }

        public TrafficUsedStatus[] newArray(int i) {
            return new TrafficUsedStatus[i];
        }
    };
    public static final int ERROR_CODE_GET_SMS_INSTRUCTION_FAILURE = 5;
    public static final int ERROR_CODE_INVALID_SMS = 2;
    public static final int ERROR_CODE_PARSE_FAILURE = 4;
    public static final int ERROR_CODE_SEND_FAILURE = 1;
    public static final int ERROR_CODE_TIMEOUT = 3;
    public static final int ERROR_CODE_WEB_TC_FAILURE = 6;
    public static final int RETURN_CODE_CONFIG_UPDATE = 11;
    public static final int RETURN_CODE_ERROR = 10;
    public static final int RETURN_CODE_OK = 0;
    private boolean mBillEnabled;
    private long mBillRemained;
    private long mBillTotal;
    private long mBillUsed;
    private boolean mCallTimeEnabled;
    private long mCallTimeRemained;
    private long mCallTimeTotal;
    private long mCallTimeUsed;
    private int mCorrectionType;
    private String mEngine;
    private boolean mExtraEnabled;
    private long mExtraRemainB;
    private long mExtraTotalB;
    private long mExtraUsedB;
    private String mFailureSms;
    private boolean mFromWeb;
    private boolean mIsExtraStable;
    private boolean mIsJustOver;
    private boolean mIsLeisureStable;
    private boolean mIsNormalStable;
    private boolean mIsTotalLimitError;
    private boolean mLeisureEnabled;
    private long mLeisureRemainB;
    private long mLeisureTotalB;
    private long mLeisureUsedB;
    private long mRemainTrafficB;
    private int mReturnCode;
    private int mSlotNum;
    private long mTotalTrafficB;
    private long mUsedTrafficB;

    public TrafficUsedStatus(int i) {
        this.mReturnCode = i;
        this.mSlotNum = 0;
    }

    public TrafficUsedStatus(int i, int i2) {
        this(i);
        this.mSlotNum = i2;
    }

    public TrafficUsedStatus(long j, long j2) {
        this.mReturnCode = 0;
        this.mUsedTrafficB = j;
        this.mRemainTrafficB = j2;
    }

    public TrafficUsedStatus(long j, long j2, int i) {
        this(j, j2);
        this.mSlotNum = i;
    }

    public TrafficUsedStatus(Parcel parcel) {
        readFromParcel(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        this.mSlotNum = parcel.readInt();
        this.mReturnCode = parcel.readInt();
        this.mCorrectionType = parcel.readInt();
        this.mFailureSms = parcel.readString();
        this.mEngine = parcel.readString();
        boolean z = false;
        this.mFromWeb = parcel.readInt() == 1;
        this.mIsTotalLimitError = parcel.readInt() == 1;
        this.mIsNormalStable = parcel.readInt() == 1;
        this.mIsJustOver = parcel.readInt() == 1;
        this.mTotalTrafficB = parcel.readLong();
        this.mUsedTrafficB = parcel.readLong();
        this.mRemainTrafficB = parcel.readLong();
        this.mLeisureEnabled = parcel.readInt() == 1;
        this.mIsLeisureStable = parcel.readInt() == 1;
        this.mLeisureTotalB = parcel.readLong();
        this.mLeisureUsedB = parcel.readLong();
        this.mLeisureRemainB = parcel.readLong();
        this.mExtraEnabled = parcel.readInt() == 1;
        this.mIsExtraStable = parcel.readInt() == 1;
        this.mExtraTotalB = parcel.readLong();
        this.mExtraUsedB = parcel.readLong();
        this.mExtraRemainB = parcel.readLong();
        this.mBillEnabled = parcel.readInt() == 1;
        this.mBillTotal = parcel.readLong();
        this.mBillRemained = parcel.readLong();
        this.mBillUsed = parcel.readLong();
        if (parcel.readInt() == 1) {
            z = true;
        }
        this.mCallTimeEnabled = z;
        this.mCallTimeTotal = parcel.readLong();
        this.mCallTimeUsed = parcel.readLong();
        this.mCallTimeRemained = parcel.readLong();
    }

    public int describeContents() {
        return 0;
    }

    public long getBillRemained() {
        return this.mBillRemained;
    }

    public long getBillTotal() {
        return this.mBillTotal;
    }

    public long getBillUsed() {
        return this.mBillUsed;
    }

    public long getCallTimeRemained() {
        return this.mCallTimeRemained;
    }

    public long getCallTimeTotal() {
        return this.mCallTimeTotal;
    }

    public long getCallTimeUsed() {
        return this.mCallTimeUsed;
    }

    public int getCorrectionType() {
        return this.mCorrectionType;
    }

    public String getEngine() {
        return this.mEngine;
    }

    public long getExtraRemainB() {
        return this.mExtraRemainB;
    }

    public long getExtraTotalB() {
        return this.mExtraTotalB;
    }

    public long getExtraUsedB() {
        return this.mExtraUsedB;
    }

    public String getFailureSms() {
        return this.mFailureSms;
    }

    public long getLeisureRemainB() {
        return this.mLeisureRemainB;
    }

    public long getLeisureTotalB() {
        return this.mLeisureTotalB;
    }

    public long getLeisureUsedB() {
        return this.mLeisureUsedB;
    }

    public long getRemainTrafficB() {
        return this.mRemainTrafficB;
    }

    public int getReturnCode() {
        return this.mReturnCode;
    }

    public int getSlotNum() {
        return this.mSlotNum;
    }

    public long getTotalTrafficB() {
        return this.mTotalTrafficB;
    }

    public long getUsedTrafficB() {
        return this.mUsedTrafficB;
    }

    public boolean isBillEnabled() {
        return this.mBillEnabled;
    }

    public boolean isCallTimeEnabled() {
        return this.mCallTimeEnabled;
    }

    public boolean isExtraEnable() {
        return this.mExtraEnabled;
    }

    public boolean isExtraStable() {
        return this.mIsExtraStable;
    }

    public boolean isFromWeb() {
        return this.mFromWeb;
    }

    public boolean isJustOver() {
        return this.mIsJustOver;
    }

    public boolean isLeisureEnable() {
        return this.mLeisureEnabled;
    }

    public boolean isLeisureJustOver() {
        return this.mLeisureTotalB == this.mLeisureUsedB;
    }

    public boolean isLeisureStable() {
        return this.mIsLeisureStable;
    }

    public boolean isNormalJustOver() {
        return this.mTotalTrafficB + this.mExtraTotalB == this.mUsedTrafficB + this.mExtraUsedB;
    }

    public boolean isNormalStable() {
        return this.mIsNormalStable;
    }

    public boolean isTotalLimitError() {
        return this.mIsTotalLimitError;
    }

    public void setBillEnabled(boolean z) {
        this.mBillEnabled = z;
    }

    public void setBillRemained(long j) {
        this.mBillRemained = j;
    }

    public void setBillTotal(long j) {
        this.mBillTotal = j;
    }

    public void setBillUsed(long j) {
        this.mBillUsed = j;
    }

    public void setCallTimeEnabled(boolean z) {
        this.mCallTimeEnabled = z;
    }

    public void setCallTimeRemained(long j) {
        this.mCallTimeRemained = j;
    }

    public void setCallTimeTotal(long j) {
        this.mCallTimeTotal = j;
    }

    public void setCallTimeUsed(long j) {
        this.mCallTimeUsed = j;
    }

    public void setCorrectionType(int i) {
        this.mCorrectionType = i;
    }

    public void setEngine(String str) {
        this.mEngine = str;
    }

    public void setExtraEnable(boolean z) {
        this.mExtraEnabled = z;
    }

    public void setExtraRemainB(long j) {
        this.mExtraRemainB = j;
    }

    public void setExtraStable(boolean z) {
        this.mIsExtraStable = z;
    }

    public void setExtraTotalB(long j) {
        this.mExtraTotalB = j;
    }

    public void setExtraUsedB(long j) {
        this.mExtraUsedB = j;
    }

    public void setFailureSms(String str) {
        this.mFailureSms = str;
    }

    public void setFromWeb(boolean z) {
        this.mFromWeb = z;
    }

    public void setJustOver(boolean z) {
        this.mIsJustOver = z;
    }

    public void setLeisureEnable(boolean z) {
        this.mLeisureEnabled = z;
    }

    public void setLeisureRemainB(long j) {
        this.mLeisureRemainB = j;
    }

    public void setLeisureStable(boolean z) {
        this.mIsLeisureStable = z;
    }

    public void setLeisureTotalB(long j) {
        this.mLeisureTotalB = j;
    }

    public void setLeisureUsedB(long j) {
        this.mLeisureUsedB = j;
    }

    public void setNormalStable(boolean z) {
        this.mIsNormalStable = z;
    }

    public void setRemainTrafficB(long j) {
        this.mRemainTrafficB = j;
    }

    public void setReturnCode(int i) {
        this.mReturnCode = i;
    }

    public void setSlotNum(int i) {
        this.mSlotNum = i;
    }

    public void setTotalLimitError(boolean z) {
        this.mIsTotalLimitError = z;
    }

    public void setTotalTrafficB(long j) {
        this.mTotalTrafficB = j;
    }

    public void setUsedTrafficB(long j) {
        this.mUsedTrafficB = j;
    }

    public String toBillString() {
        return "mReturnCode:" + this.mReturnCode + ";mLeftBill:" + this.mBillRemained;
    }

    public String toString() {
        return "mReturnCode:" + this.mReturnCode + ";mUsedTrafficB:" + this.mUsedTrafficB + ";mLeftTrafficB:" + this.mRemainTrafficB;
    }

    public String toTrafficString() {
        return toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSlotNum);
        parcel.writeInt(this.mReturnCode);
        parcel.writeInt(this.mCorrectionType);
        parcel.writeString(this.mFailureSms);
        parcel.writeString(this.mEngine);
        parcel.writeInt(this.mFromWeb ? 1 : 0);
        parcel.writeInt(this.mIsTotalLimitError ? 1 : 0);
        parcel.writeInt(this.mIsNormalStable ? 1 : 0);
        parcel.writeInt(this.mIsJustOver ? 1 : 0);
        parcel.writeLong(this.mTotalTrafficB);
        parcel.writeLong(this.mUsedTrafficB);
        parcel.writeLong(this.mRemainTrafficB);
        parcel.writeInt(this.mLeisureEnabled ? 1 : 0);
        parcel.writeInt(this.mIsLeisureStable ? 1 : 0);
        parcel.writeLong(this.mLeisureTotalB);
        parcel.writeLong(this.mLeisureUsedB);
        parcel.writeLong(this.mLeisureRemainB);
        parcel.writeInt(this.mExtraEnabled ? 1 : 0);
        parcel.writeInt(this.mIsExtraStable ? 1 : 0);
        parcel.writeLong(this.mExtraTotalB);
        parcel.writeLong(this.mExtraUsedB);
        parcel.writeLong(this.mExtraRemainB);
        parcel.writeInt(this.mBillEnabled ? 1 : 0);
        parcel.writeLong(this.mBillTotal);
        parcel.writeLong(this.mBillUsed);
        parcel.writeLong(this.mBillRemained);
        parcel.writeInt(this.mCallTimeEnabled ? 1 : 0);
        parcel.writeLong(this.mCallTimeTotal);
        parcel.writeLong(this.mCallTimeUsed);
        parcel.writeLong(this.mCallTimeRemained);
    }
}
