package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import org.json.JSONObject;

public class ViewPointVideoInfo implements Parcelable {
    public static final Parcelable.Creator<ViewPointVideoInfo> CREATOR = new z();
    private String key;
    private String mCover;
    private int mDuration;
    private int mHeight;
    private int mPlayCount;
    private int mSize;
    private String mUrl;
    private String mVideoId;
    private int mWidth;

    public ViewPointVideoInfo() {
    }

    protected ViewPointVideoInfo(Parcel parcel) {
        this.mVideoId = parcel.readString();
        this.mUrl = parcel.readString();
        this.mHeight = parcel.readInt();
        this.mWidth = parcel.readInt();
        this.mSize = parcel.readInt();
        this.mCover = parcel.readString();
        this.mPlayCount = parcel.readInt();
        this.mDuration = parcel.readInt();
        this.key = parcel.readString();
    }

    public ViewPointVideoInfo(String str, String str2, int i, int i2, int i3, String str3, int i4) {
        this.mVideoId = str;
        this.mUrl = str2;
        this.mHeight = i;
        this.mWidth = i2;
        this.mSize = i3;
        this.mCover = str3;
        this.mDuration = i4;
    }

    public ViewPointVideoInfo(String str, JSONObject jSONObject) {
        fromJson(jSONObject);
        this.key = str;
    }

    public ViewPointVideoInfo(JSONObject jSONObject) {
        if (jSONObject != null) {
            fromJson(jSONObject);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void fromJson(JSONObject jSONObject) {
        if (jSONObject != null) {
            if (jSONObject.has("cover")) {
                this.mCover = jSONObject.optString("cover");
            }
            if (jSONObject.has("duration")) {
                this.mDuration = jSONObject.optInt("duration");
            }
            if (jSONObject.has("high")) {
                this.mHeight = jSONObject.optInt("high");
            }
            if (jSONObject.has("playCnt")) {
                this.mPlayCount = jSONObject.optInt("playCnt");
            }
            if (jSONObject.has("size")) {
                this.mSize = jSONObject.optInt("size");
            }
            if (jSONObject.has(MijiaAlertModel.KEY_URL)) {
                this.mUrl = jSONObject.optString(MijiaAlertModel.KEY_URL);
            }
            if (jSONObject.has("videoId")) {
                this.mVideoId = jSONObject.optString("videoId");
            }
            if (jSONObject.has("width")) {
                this.mWidth = jSONObject.optInt("width");
            }
        }
    }

    public String getCover() {
        return this.mCover;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public String getKey() {
        return this.key;
    }

    public int getPlayCount() {
        return this.mPlayCount;
    }

    public int getSize() {
        return this.mSize;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public String getVideoId() {
        return this.mVideoId;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void setCover(String str) {
        this.mCover = str;
    }

    public void setDuration(int i) {
        this.mDuration = i;
    }

    public void setHeight(int i) {
        this.mHeight = i;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public void setPlayCount(int i) {
        this.mPlayCount = i;
    }

    public void setSize(int i) {
        this.mSize = i;
    }

    public void setUrl(String str) {
        this.mUrl = str;
    }

    public void setVideoId(String str) {
        this.mVideoId = str;
    }

    public void setWidth(int i) {
        this.mWidth = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mVideoId);
        parcel.writeString(this.mUrl);
        parcel.writeInt(this.mHeight);
        parcel.writeInt(this.mWidth);
        parcel.writeInt(this.mSize);
        parcel.writeString(this.mCover);
        parcel.writeInt(this.mPlayCount);
        parcel.writeInt(this.mDuration);
        parcel.writeString(this.key);
    }
}
