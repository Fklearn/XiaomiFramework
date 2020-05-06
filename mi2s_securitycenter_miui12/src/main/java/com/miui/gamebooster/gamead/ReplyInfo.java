package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.miui.gamebooster.m.C0377h;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReplyInfo implements Parcelable {
    public static final Parcelable.Creator<ReplyInfo> CREATOR = new p();
    protected String content;
    protected long createTime;
    protected String dataId;
    protected int dataType;
    protected long floorHostUuid;
    protected User fromUser;
    protected int isOfficial;
    protected int likeCnt;
    protected LikeInfo likeInfo;
    protected boolean mIsCollapsed = true;
    protected List<String> pictures;
    protected int replyCnt;
    protected int replyFloor;
    protected String replyId;
    protected int seq;
    protected int status;
    protected User toUser;
    protected List<ReplyInfo> topReplys;

    public ReplyInfo() {
    }

    protected ReplyInfo(Parcel parcel) {
        boolean z = true;
        this.replyId = parcel.readString();
        this.fromUser = (User) parcel.readParcelable(User.class.getClassLoader());
        this.toUser = (User) parcel.readParcelable(User.class.getClassLoader());
        this.dataId = parcel.readString();
        this.dataType = parcel.readInt();
        this.content = parcel.readString();
        this.likeCnt = parcel.readInt();
        this.replyCnt = parcel.readInt();
        this.status = parcel.readInt();
        this.createTime = parcel.readLong();
        this.seq = parcel.readInt();
        this.likeInfo = (LikeInfo) parcel.readParcelable(LikeInfo.class.getClassLoader());
        this.topReplys = parcel.createTypedArrayList(CREATOR);
        this.mIsCollapsed = parcel.readByte() == 0 ? false : z;
        this.isOfficial = parcel.readInt();
        this.pictures = parcel.createStringArrayList();
        this.replyFloor = parcel.readInt();
    }

    public static ReplyInfo fromJson(JSONObject jSONObject) {
        try {
            ReplyInfo replyInfo = new ReplyInfo();
            replyInfo.replyId = jSONObject.optString("replyId");
            replyInfo.fromUser = User.fromJson(jSONObject.optJSONObject("fromUser"));
            replyInfo.toUser = User.fromJson(jSONObject.optJSONObject("toUser"));
            if (replyInfo.fromUser != null) {
                if (replyInfo.toUser != null) {
                    replyInfo.dataId = jSONObject.optString("dataId");
                    replyInfo.dataType = jSONObject.optInt("dataType");
                    replyInfo.content = jSONObject.optString(MiStat.Param.CONTENT);
                    replyInfo.likeCnt = jSONObject.optInt("likeCnt");
                    replyInfo.replyCnt = jSONObject.optInt("replyCnt");
                    replyInfo.status = jSONObject.optInt("status");
                    replyInfo.createTime = jSONObject.optLong("createTime");
                    replyInfo.seq = jSONObject.optInt("seq");
                    replyInfo.likeInfo = LikeInfo.fromJson(jSONObject.optJSONObject("likeInfo"));
                    JSONArray optJSONArray = jSONObject.optJSONArray("topReplys");
                    if (optJSONArray != null) {
                        replyInfo.topReplys = new ArrayList();
                        for (int i = 0; i < optJSONArray.length(); i++) {
                            ReplyInfo fromJson = fromJson(new JSONObject(optJSONArray.getString(i)));
                            if (fromJson != null) {
                                replyInfo.topReplys.add(fromJson);
                            }
                        }
                    }
                    replyInfo.isOfficial = jSONObject.optInt("isOfficial");
                    replyInfo.replyFloor = jSONObject.optInt("replyFloor");
                    return replyInfo;
                }
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isLegal(ReplyInfo replyInfo) {
        return replyInfo != null && !TextUtils.isEmpty(replyInfo.replyId);
    }

    public int describeContents() {
        return 0;
    }

    public String getContent() {
        return C0377h.b(this.content);
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public String getDataId() {
        return this.dataId;
    }

    public int getDataType() {
        return this.dataType;
    }

    public long getFloorHostUuid() {
        return this.floorHostUuid;
    }

    public User getFromUser() {
        return this.fromUser;
    }

    public int getIsOfficial() {
        return this.isOfficial;
    }

    public int getLikeCnt() {
        return this.likeCnt;
    }

    public LikeInfo getLikeInfo() {
        return this.likeInfo;
    }

    public List<String> getPictures() {
        return this.pictures;
    }

    public int getReplyCnt() {
        return this.replyCnt;
    }

    public int getReplyFloor() {
        return this.replyFloor;
    }

    public String getReplyId() {
        return this.replyId;
    }

    public int getSeq() {
        return this.seq;
    }

    public int getStatus() {
        return this.status;
    }

    public User getToUser() {
        return this.toUser;
    }

    public List<ReplyInfo> getTopReplys() {
        return this.topReplys;
    }

    public boolean isCollapsed() {
        return this.mIsCollapsed;
    }

    public void setCollapsed(boolean z) {
        this.mIsCollapsed = z;
    }

    public void setContent(String str) {
        this.content = str;
    }

    public void setCreateTime(long j) {
        this.createTime = j;
    }

    public void setDataId(String str) {
        this.dataId = str;
    }

    public void setDataType(int i) {
        this.dataType = i;
    }

    public void setFromUser(User user) {
        this.fromUser = user;
    }

    public void setLikeCnt(int i) {
        this.likeCnt = i;
    }

    public void setLikeInfo(LikeInfo likeInfo2) {
        this.likeInfo = likeInfo2;
    }

    public void setPictures(List<String> list) {
        this.pictures = list;
    }

    public void setReplyCnt(int i) {
        this.replyCnt = i;
    }

    public void setReplyId(String str) {
        this.replyId = str;
    }

    public void setSeq(int i) {
        this.seq = i;
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public void setToUser(User user) {
        this.toUser = user;
    }

    public void setTopReplys(List<ReplyInfo> list) {
        this.topReplys = list;
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("replyId", this.replyId);
            if (this.fromUser != null) {
                jSONObject.put("fromUser", this.fromUser.toJson());
            }
            if (this.toUser != null) {
                jSONObject.put("toUser", this.toUser.toJson());
            }
            jSONObject.put("dataId", this.dataId);
            jSONObject.put("dataType", this.dataType);
            jSONObject.put(MiStat.Param.CONTENT, this.content);
            jSONObject.put("likeCnt", this.likeCnt);
            jSONObject.put("replyCnt", this.replyCnt);
            jSONObject.put("status", this.status);
            jSONObject.put("createTime", this.createTime);
            jSONObject.put("seq", this.seq);
            jSONObject.put("likeInfo", (this.likeInfo != null ? this.likeInfo : new LikeInfo("", 0, 0)).toJson());
            if (this.topReplys != null) {
                JSONArray jSONArray = new JSONArray();
                for (ReplyInfo json : this.topReplys) {
                    jSONArray.put(json.toJson());
                }
                jSONObject.put("topReplys", jSONArray);
            }
            jSONObject.put("isOfficial", this.isOfficial);
            jSONObject.put("replyFloor", this.replyFloor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.replyId);
        parcel.writeParcelable(this.fromUser, i);
        parcel.writeParcelable(this.toUser, i);
        parcel.writeString(this.dataId);
        parcel.writeInt(this.dataType);
        parcel.writeString(this.content);
        parcel.writeInt(this.likeCnt);
        parcel.writeInt(this.replyCnt);
        parcel.writeInt(this.status);
        parcel.writeLong(this.createTime);
        parcel.writeInt(this.seq);
        parcel.writeParcelable(this.likeInfo, i);
        parcel.writeTypedList(this.topReplys);
        parcel.writeByte(this.mIsCollapsed ? (byte) 1 : 0);
        parcel.writeInt(this.isOfficial);
        parcel.writeStringList(this.pictures);
        parcel.writeInt(this.replyFloor);
    }
}
