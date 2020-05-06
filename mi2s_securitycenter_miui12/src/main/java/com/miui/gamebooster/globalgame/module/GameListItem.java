package com.miui.gamebooster.globalgame.module;

import android.support.annotation.Keep;
import com.google.gson.annotations.SerializedName;
import com.miui.gamebooster.globalgame.util.NoProguard;
import com.miui.networkassistant.provider.ProviderConstant;
import com.xiaomi.stat.MiStat;
import miui.cloud.CloudPushConstants;
import org.json.JSONObject;

@Keep
@NoProguard
public class GameListItem {
    @SerializedName("desc")
    public String desc;
    public int gameIndex;
    @SerializedName("gameLink")
    public String gameLink;
    @SerializedName("gameLinkType")
    public int gameLinkType;
    @SerializedName("icon")
    public String icon;
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("score")
    public String score;

    public GameListItem(GameListItem gameListItem) {
        this(gameListItem.score, gameListItem.gameLink, gameListItem.icon, gameListItem.name, gameListItem.gameLinkType, gameListItem.id, gameListItem.desc);
    }

    public GameListItem(String str, String str2, String str3, String str4, int i, int i2, String str5) {
        this.score = "";
        this.gameLink = "";
        this.icon = "";
        this.name = "";
        this.gameLinkType = 0;
        this.id = 0;
        this.desc = "";
        this.gameIndex = 0;
        this.score = str;
        this.gameLink = str2;
        this.icon = str3;
        this.name = str4;
        this.gameLinkType = i;
        this.id = i2;
        this.desc = str5;
    }

    public GameListItem(JSONObject jSONObject) {
        this.score = "";
        this.gameLink = "";
        this.icon = "";
        this.name = "";
        this.gameLinkType = 0;
        this.id = 0;
        this.desc = "";
        this.gameIndex = 0;
        if (jSONObject != null) {
            this.score = jSONObject.optString(MiStat.Param.SCORE);
            this.gameLink = jSONObject.optString("gameLink");
            this.icon = jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON);
            this.name = jSONObject.optString(CloudPushConstants.XML_NAME);
            this.gameLinkType = jSONObject.optInt("gameLinkType");
            this.id = jSONObject.optInt("id");
            this.desc = jSONObject.optString("desc");
        }
    }

    public String getDesc() {
        return this.desc;
    }

    public int getGameIndex() {
        return this.gameIndex;
    }

    public String getGameLink() {
        return this.gameLink;
    }

    public int getGameLinkType() {
        return this.gameLinkType;
    }

    public String getIcon() {
        return this.icon;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getScore() {
        return this.score;
    }

    public void setDesc(String str) {
        this.desc = str;
    }

    public void setGameIndex(int i) {
        this.gameIndex = i;
    }

    public void setGameLink(String str) {
        this.gameLink = str;
    }

    public void setGameLinkType(int i) {
        this.gameLinkType = i;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public void setId(int i) {
        this.id = i;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setScore(String str) {
        this.score = str;
    }
}
