package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import com.miui.gamebooster.m.C0377h;
import com.xiaomi.stat.MiStat;
import org.json.JSONObject;

public class VerticalInRow implements Parcelable {
    public static final Parcelable.Creator<VerticalInRow> CREATOR = new u();
    private String content;
    private int contentType;
    private int positionIndex;
    private ViewPointVideoInfo videoInfo;

    public VerticalInRow() {
    }

    public VerticalInRow(Parcel parcel) {
        this.contentType = parcel.readInt();
        this.positionIndex = parcel.readInt();
        this.content = parcel.readString();
        this.videoInfo = (ViewPointVideoInfo) parcel.readParcelable(ViewPointVideoInfo.class.getClassLoader());
    }

    public static VerticalInRow parseFromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        VerticalInRow verticalInRow = new VerticalInRow();
        if (jSONObject.has(MiStat.Param.CONTENT)) {
            verticalInRow.content = jSONObject.optString(MiStat.Param.CONTENT);
        }
        if (jSONObject.has("contentType")) {
            verticalInRow.contentType = jSONObject.optInt("contentType");
        }
        if (jSONObject.has("positionIndex")) {
            verticalInRow.positionIndex = jSONObject.optInt("positionIndex");
        }
        if (jSONObject.has("videoInfo")) {
            verticalInRow.videoInfo = new ViewPointVideoInfo(jSONObject.optJSONObject("videoInfo"));
        }
        return verticalInRow;
    }

    public int describeContents() {
        return 0;
    }

    public String getContent() {
        return this.contentType == 1 ? C0377h.b(this.content) : this.content;
    }

    public int getContentType() {
        return this.contentType;
    }

    public int getPositionIndex() {
        return this.positionIndex;
    }

    public ViewPointVideoInfo getVideoInfo() {
        return this.videoInfo;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.contentType);
        parcel.writeInt(this.positionIndex);
        parcel.writeString(this.content);
        parcel.writeParcelable(this.videoInfo, i);
    }
}
