package com.miui.gamebooster.gamead;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class LikeInfo implements Parcelable {
    public static final Parcelable.Creator<LikeInfo> CREATOR = new n();
    protected String dataId;
    protected int dataType;
    protected int likeType;

    protected LikeInfo() {
    }

    protected LikeInfo(Parcel parcel) {
        this.dataId = parcel.readString();
        this.dataType = parcel.readInt();
        this.likeType = parcel.readInt();
    }

    public LikeInfo(String str, int i, int i2) {
        this.dataId = str;
        this.dataType = i;
        this.likeType = i2;
    }

    public static LikeInfo fromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        try {
            LikeInfo likeInfo = new LikeInfo();
            likeInfo.dataId = jSONObject.optString("dataId");
            if (TextUtils.isEmpty(likeInfo.dataId)) {
                return null;
            }
            likeInfo.dataType = jSONObject.optInt("dataType");
            likeInfo.likeType = jSONObject.optInt("likeType");
            return likeInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isLeagle(LikeInfo likeInfo) {
        return likeInfo != null && !TextUtils.isEmpty(likeInfo.dataId);
    }

    public static LikeInfo parseFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        LikeInfo likeInfo = new LikeInfo();
        try {
            likeInfo.dataId = uri.getQueryParameter("dataId");
            if (TextUtils.isEmpty(likeInfo.dataId)) {
                return null;
            }
            likeInfo.dataType = Integer.parseInt(uri.getQueryParameter("dataType"));
            likeInfo.likeType = Integer.parseInt(uri.getQueryParameter("likeType"));
            return likeInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toUriParamString(LikeInfo likeInfo) {
        if (!isLeagle(likeInfo)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("dataId=" + likeInfo.dataId);
        sb.append("&dataType=" + likeInfo.dataType);
        sb.append("&likeType=" + likeInfo.likeType);
        return sb.toString();
    }

    public LikeInfo copy() {
        return new LikeInfo(String.valueOf(this.dataId), this.dataType, this.likeType);
    }

    public int describeContents() {
        return 0;
    }

    public String getDataId() {
        return this.dataId;
    }

    public int getDataType() {
        return this.dataType;
    }

    public int getLikeType() {
        return this.likeType;
    }

    public void setDataId(String str) {
        this.dataId = str;
    }

    public void setDataType(int i) {
        this.dataType = i;
    }

    public void setLikeType(int i) {
        this.likeType = i;
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("dataId", this.dataId);
            jSONObject.put("dataType", this.dataType);
            jSONObject.put("likeType", this.likeType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.dataId);
        parcel.writeInt(this.dataType);
        parcel.writeInt(this.likeType);
    }
}
