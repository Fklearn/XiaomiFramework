package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.cloud.CloudPushConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class GameInfo implements Parcelable {
    public static final Parcelable.Creator<GameInfo> CREATOR = new i();
    protected String editorScore;
    protected String gameIcon;
    protected long gameId;
    protected String gameName;
    private List<Tag> gameTagList = new ArrayList();
    private long mApkSize;
    private String mBanner;
    private Map<Integer, String> mCornersIcon = new HashMap();
    private String mVersionCode;
    protected String packageName;
    protected int status;
    protected String userScore;
    protected int userScoreCnt;

    public static class Tag implements Parcelable {
        public static final Parcelable.Creator<Tag> CREATOR = new j();
        protected String actUrl;
        protected String name;
        protected int tagId;
        protected float x;
        protected float y;

        public Tag() {
        }

        public Tag(int i, String str, String str2) {
            this.tagId = i;
            this.name = str;
            this.actUrl = str2;
        }

        protected Tag(Parcel parcel) {
            this.tagId = parcel.readInt();
            this.name = parcel.readString();
            this.actUrl = parcel.readString();
            this.x = parcel.readFloat();
            this.y = parcel.readFloat();
        }

        public static Tag fromJson(JSONObject jSONObject) {
            Tag tag = new Tag();
            if (jSONObject.has("tagId")) {
                tag.tagId = jSONObject.optInt("tagId");
            }
            if (jSONObject.has(CloudPushConstants.XML_NAME)) {
                tag.name = jSONObject.optString(CloudPushConstants.XML_NAME);
            }
            if (jSONObject.has("actUrl")) {
                tag.actUrl = jSONObject.optString("actUrl");
            }
            return tag;
        }

        public int describeContents() {
            return 0;
        }

        public String getActUrl() {
            return this.actUrl;
        }

        public String getName() {
            return this.name;
        }

        public int getTagId() {
            return this.tagId;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public void setX(float f) {
            this.x = f;
        }

        public void setY(float f) {
            this.y = f;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.tagId);
            parcel.writeString(this.name);
            parcel.writeString(this.actUrl);
            parcel.writeFloat(this.x);
            parcel.writeFloat(this.y);
        }
    }

    protected GameInfo() {
    }

    public GameInfo(long j, String str, String str2, String str3) {
        this.gameId = j;
        this.gameName = str2;
        this.packageName = str;
        this.gameIcon = str3;
    }

    protected GameInfo(Parcel parcel) {
        this.gameId = parcel.readLong();
        this.packageName = parcel.readString();
        this.status = parcel.readInt();
        this.gameName = parcel.readString();
        this.gameIcon = parcel.readString();
        this.editorScore = parcel.readString();
        this.userScoreCnt = parcel.readInt();
        this.userScore = parcel.readString();
        int readInt = parcel.readInt();
        this.mCornersIcon = new HashMap(readInt);
        for (int i = 0; i < readInt; i++) {
            String readString = parcel.readString();
            this.mCornersIcon.put((Integer) parcel.readValue(Integer.class.getClassLoader()), readString);
        }
        this.mBanner = parcel.readString();
        parcel.readList(this.gameTagList, Tag.class.getClassLoader());
    }

    public static GameInfo createTestData(int i) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.gameId = 62230029;
        gameInfo.packageName = "com.pixelbite.sm2.gm";
        gameInfo.status = 1;
        gameInfo.gameName = "太空刑警" + i;
        gameInfo.gameIcon = "AppStore/0133c41f69dc2ecb61a4e85bc0e5715a5e14368db";
        return gameInfo;
    }

    public static GameInfo fromJson(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        GameInfo gameInfo = new GameInfo();
        gameInfo.gameId = jSONObject.optLong("gameId");
        gameInfo.packageName = jSONObject.optString("packageName");
        gameInfo.status = jSONObject.optInt("status");
        gameInfo.gameName = jSONObject.optString("gameName");
        gameInfo.gameIcon = jSONObject.optString("gameIcon");
        gameInfo.editorScore = jSONObject.optString("editorScore");
        gameInfo.userScoreCnt = jSONObject.optInt("userScoreCnt");
        gameInfo.userScore = jSONObject.optString("userScore");
        if (!isLeagle(gameInfo)) {
            return null;
        }
        return gameInfo;
    }

    public static boolean isLeagle(GameInfo gameInfo) {
        return gameInfo != null && gameInfo.gameId > 0 && gameInfo.packageName != null && !TextUtils.isEmpty(gameInfo.gameName);
    }

    public int describeContents() {
        return 0;
    }

    public long getApkSize() {
        return this.mApkSize;
    }

    public String getBanner() {
        return this.mBanner;
    }

    public Map<Integer, String> getCornersIcon() {
        return this.mCornersIcon;
    }

    public String getEditorScore() {
        return this.editorScore;
    }

    public String getGameIcon() {
        return this.gameIcon;
    }

    public String getGameIcon(int i) {
        Map<Integer, String> map = this.mCornersIcon;
        if (map != null && !map.isEmpty()) {
            Iterator<Integer> it = this.mCornersIcon.keySet().iterator();
            String str = null;
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Integer next = it.next();
                String str2 = this.mCornersIcon.get(next);
                if (next.intValue() >= i) {
                    str = str2;
                    break;
                }
                str = str2;
            }
            if (!TextUtils.isEmpty(str)) {
                return str;
            }
        }
        return null;
    }

    public long getGameId() {
        return this.gameId;
    }

    public String getGameName() {
        return this.gameName;
    }

    public List<Tag> getGameTagList() {
        return this.gameTagList;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getStatus() {
        return this.status;
    }

    public String getUserScore() {
        return this.userScore;
    }

    public int getUserScoreCnt() {
        return this.userScoreCnt;
    }

    public String getVersionCode() {
        return this.mVersionCode;
    }

    public void setCornersIcon(Map<Integer, String> map) {
        this.mCornersIcon = map;
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("gameId", this.gameId);
            jSONObject.put("packageName", this.packageName);
            jSONObject.put("status", this.status);
            jSONObject.put("gameName", this.gameName);
            jSONObject.put("gameIcon", this.gameIcon);
            jSONObject.put("editorScore", this.editorScore);
            jSONObject.put("userScoreCnt", this.userScoreCnt);
            jSONObject.put("userScore", this.userScore);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.gameId);
        parcel.writeString(this.packageName);
        parcel.writeInt(this.status);
        parcel.writeString(this.gameName);
        parcel.writeString(this.gameIcon);
        parcel.writeString(this.editorScore);
        parcel.writeInt(this.userScoreCnt);
        parcel.writeString(this.userScore);
        parcel.writeInt(this.mCornersIcon.size());
        for (Map.Entry next : this.mCornersIcon.entrySet()) {
            parcel.writeValue(next.getKey());
            parcel.writeString((String) next.getValue());
        }
        parcel.writeString(this.mBanner);
        parcel.writeList(this.gameTagList);
    }
}
