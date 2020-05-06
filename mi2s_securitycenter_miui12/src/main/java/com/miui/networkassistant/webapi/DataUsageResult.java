package com.miui.networkassistant.webapi;

import b.b.c.g.d;
import com.miui.activityutil.o;
import org.json.JSONObject;

public class DataUsageResult extends d {
    private long mBillLeft;
    private String mBrand;
    private long mCallTimeLeft;
    private long mCallTimeTotal;
    private int mCityCode;
    private long mIdleLeft;
    private boolean mIdleOn;
    private long mIdleTotal;
    private long mLeftFlow;
    private String mOperator;
    private String mPhoneNumber;
    private int mProvinceCode;
    private long mTotal;

    public DataUsageResult(String str) {
        super(str);
    }

    /* access modifiers changed from: protected */
    public boolean doParseJson(JSONObject jSONObject) {
        super.doParseJson(jSONObject);
        if (isSuccess()) {
            JSONObject optJSONObject = jSONObject.optJSONObject("info");
            if (optJSONObject == null) {
                return true;
            }
            this.mTotal = (long) (optJSONObject.optDouble("totalflow", 0.0d) * 1048576.0d);
            this.mLeftFlow = (long) (optJSONObject.optDouble("leftflow", 0.0d) * 1048576.0d);
            this.mIdleOn = o.f2310b.equals(optJSONObject.optString("idleon"));
            this.mPhoneNumber = optJSONObject.optString("identify");
            this.mBrand = optJSONObject.optString("brand");
            this.mOperator = optJSONObject.optString("sp");
            this.mBillLeft = optJSONObject.optLong("balance", -1);
            this.mCallTimeTotal = optJSONObject.optLong("totalCall", -1);
            this.mCallTimeLeft = optJSONObject.optLong("leftCall", -1);
            this.mProvinceCode = optJSONObject.optInt("province", -1);
            this.mCityCode = optJSONObject.optInt("city", -1);
            if (!this.mIdleOn) {
                return true;
            }
            this.mIdleTotal = (long) (optJSONObject.optDouble("idletotal", 0.0d) * 1048576.0d);
            this.mIdleLeft = (long) (optJSONObject.optDouble("idleleft", 0.0d) * 1048576.0d);
            return true;
        }
        this.mDesc = jSONObject.optString("desc");
        this.mOldAge = jSONObject.optInt("oldage");
        return true;
    }

    public long getBillLeft() {
        return this.mBillLeft;
    }

    public String getBrand() {
        return this.mBrand;
    }

    public long getCallTimeLeft() {
        return this.mCallTimeLeft;
    }

    public long getCallTimeTotal() {
        return this.mCallTimeTotal;
    }

    public long getCallTimeUsed() {
        long j = this.mCallTimeTotal - this.mCallTimeLeft;
        if (j > 0) {
            return j;
        }
        return -1;
    }

    public int getCityCode() {
        return this.mCityCode;
    }

    public String getCode() {
        return this.mCode;
    }

    public long getIdleLeft() {
        return this.mIdleLeft;
    }

    public long getIdleTotal() {
        return this.mIdleTotal;
    }

    public long getIdleUsed() {
        long j = this.mIdleTotal - this.mIdleLeft;
        if (j < 0) {
            return 0;
        }
        return j;
    }

    public long getLeftFlow() {
        return this.mLeftFlow;
    }

    public String getOperator() {
        return this.mOperator;
    }

    public String getPhoneNumber() {
        return this.mPhoneNumber;
    }

    public int getProvinceCode() {
        return this.mProvinceCode;
    }

    public long getTotal() {
        return this.mTotal;
    }

    public long getUsedFlow() {
        long j = this.mTotal - this.mLeftFlow;
        if (j < 0) {
            return 0;
        }
        return j;
    }

    public boolean isBillOn() {
        return this.mBillLeft > -1;
    }

    public boolean isCallTimeOn() {
        return this.mCallTimeLeft > -1;
    }

    public boolean isIdleOn() {
        return this.mIdleOn;
    }

    public boolean isServerError() {
        return o.f2312d.equals(this.mCode);
    }

    public boolean isServiceNotSupported() {
        return "2".equals(this.mCode) || o.f2310b.equals(this.mCode);
    }

    public void setBrand(String str) {
        this.mBrand = str;
    }

    public void setCode(String str) {
        this.mCode = str;
    }

    public void setOperator(String str) {
        this.mOperator = str;
    }

    public void setPhoneNumber(String str) {
        this.mPhoneNumber = str;
    }

    public void setTotal(long j) {
        this.mTotal = j;
    }

    public String toString() {
        if (!isSuccess()) {
            return super.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", mTotal: " + this.mTotal + ", mLeftFlow: " + this.mLeftFlow + ", mIdleOn: " + this.mIdleOn + ", mIdleTotal: " + this.mIdleTotal + ", mIdleLeftFlow: " + this.mIdleLeft + ", mBrand: " + this.mBrand + ", mOperator: " + this.mOperator + ", mBillLeft: " + this.mBillLeft + ", mCallTimeTotal: " + this.mCallTimeTotal + ", mCallTimeLeft: " + this.mCallTimeLeft + ", mProvinceCode: " + this.mProvinceCode + ", mCityCode: " + this.mCityCode);
        return sb.toString();
    }
}
