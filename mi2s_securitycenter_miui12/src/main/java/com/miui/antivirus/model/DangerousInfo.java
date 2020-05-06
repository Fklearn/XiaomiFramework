package com.miui.antivirus.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

public class DangerousInfo implements Parcelable {
    public static final Parcelable.Creator<DangerousInfo> CREATOR = new f();
    public static final int INVALID_VERSION_CODE = -1001;
    public static final int NOTIFY_TYPE_ALLOW_CANCEL = 2;
    public static final int NOTIFY_TYPE_NOT_CANCEL = 1;
    private String fileMd5;
    private String language;
    private String msg;
    private int notifyType;
    private String packageName;
    private String sign;
    private int versionCode;

    public DangerousInfo() {
        this.versionCode = INVALID_VERSION_CODE;
    }

    private DangerousInfo(Parcel parcel) {
        this.versionCode = INVALID_VERSION_CODE;
        this.notifyType = parcel.readInt();
        this.packageName = parcel.readString();
        this.sign = parcel.readString();
        this.versionCode = parcel.readInt();
        this.fileMd5 = parcel.readString();
        this.language = parcel.readString();
        this.msg = parcel.readString();
    }

    /* synthetic */ DangerousInfo(Parcel parcel, f fVar) {
        this(parcel);
    }

    public static DangerousInfo create(JSONObject jSONObject) {
        DangerousInfo dangerousInfo = new DangerousInfo();
        dangerousInfo.notifyType = jSONObject.optInt("nt");
        dangerousInfo.packageName = jSONObject.optString("pkg");
        dangerousInfo.sign = jSONObject.optString("sign");
        dangerousInfo.versionCode = jSONObject.optInt("ver", INVALID_VERSION_CODE);
        dangerousInfo.fileMd5 = jSONObject.optString("md5");
        dangerousInfo.language = jSONObject.optString("language");
        dangerousInfo.msg = jSONObject.optString("msg");
        return dangerousInfo;
    }

    public int describeContents() {
        return 0;
    }

    public String getFileMd5() {
        return this.fileMd5;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getMsg() {
        return this.msg;
    }

    public int getNotifyType() {
        return this.notifyType;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getSign() {
        return this.sign;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public void setFileMd5(String str) {
        this.fileMd5 = str;
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public void setMsg(String str) {
        this.msg = str;
    }

    public void setNotifyType(int i) {
        this.notifyType = i;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public void setSign(String str) {
        this.sign = str;
    }

    public void setVersionCode(int i) {
        this.versionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.notifyType);
        parcel.writeString(this.packageName);
        parcel.writeString(this.sign);
        parcel.writeInt(this.versionCode);
        parcel.writeString(this.fileMd5);
        parcel.writeString(this.language);
        parcel.writeString(this.msg);
    }
}
