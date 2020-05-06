package com.miui.gamebooster.globalgame.module;

import android.graphics.Bitmap;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.market.sdk.utils.b;
import com.miui.gamebooster.globalgame.util.NoProguard;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@Keep
@NoProguard
public class BannerCardBean {
    @SerializedName("buttonColor")
    public String buttonColor;
    @SerializedName("buttonText")
    public String buttonText;
    @SerializedName("cover")
    public String cover;
    @SerializedName("gameList")
    public List<GameListItem> gameList;
    @SerializedName("icon")
    public String icon;
    @SerializedName("id")
    public int id;
    public int index;
    public boolean isFirst;
    public boolean isLast;
    @SerializedName("link")
    public String link;
    @SerializedName("linkType")
    public int linkType;
    @SerializedName("loadImage")
    public String loadImage;
    @Nullable
    public Bitmap loadedBitmap;
    @SerializedName("title")
    public String title;
    @SerializedName("topTitle")
    public String topTitle;
    @SerializedName("type")
    public int type;
    public boolean unjson;

    public BannerCardBean(int i) {
        this.cover = "";
        this.buttonText = "";
        this.buttonColor = "";
        this.icon = "";
        this.link = "";
        this.linkType = 0;
        this.id = 0;
        this.type = 0;
        this.title = "";
        this.topTitle = "";
        this.loadImage = "";
        this.index = 0;
        this.unjson = true;
        this.isFirst = false;
        this.isLast = false;
        this.type = i;
    }

    public BannerCardBean(BannerCardBean bannerCardBean) {
        this(bannerCardBean.cover, bannerCardBean.buttonText, bannerCardBean.buttonColor, bannerCardBean.icon, bannerCardBean.link, bannerCardBean.gameList, bannerCardBean.linkType, bannerCardBean.id, bannerCardBean.type, bannerCardBean.title, bannerCardBean.topTitle, bannerCardBean.loadImage);
    }

    public BannerCardBean(String str, String str2, String str3, String str4, String str5, List<GameListItem> list, int i, int i2, int i3, String str6, String str7, String str8) {
        this.cover = "";
        this.buttonText = "";
        this.buttonColor = "";
        this.icon = "";
        this.link = "";
        this.linkType = 0;
        this.id = 0;
        this.type = 0;
        this.title = "";
        this.topTitle = "";
        this.loadImage = "";
        this.index = 0;
        this.unjson = true;
        this.isFirst = false;
        this.isLast = false;
        this.cover = str;
        this.buttonText = str2;
        this.buttonColor = str3;
        this.icon = str4;
        this.link = str5;
        this.gameList = list;
        this.linkType = i;
        this.id = i2;
        this.type = i3;
        this.title = str6;
        this.topTitle = str7;
        this.loadImage = str8;
    }

    public BannerCardBean(JSONObject jSONObject) {
        this.cover = "";
        this.buttonText = "";
        this.buttonColor = "";
        this.icon = "";
        this.link = "";
        this.linkType = 0;
        this.id = 0;
        this.type = 0;
        this.title = "";
        this.topTitle = "";
        this.loadImage = "";
        this.index = 0;
        this.unjson = true;
        this.isFirst = false;
        this.isLast = false;
        if (jSONObject != null) {
            this.cover = jSONObject.optString("cover");
            this.buttonText = jSONObject.optString("buttonText");
            this.buttonColor = jSONObject.optString("buttonColor");
            this.icon = jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON);
            this.link = jSONObject.optString("link");
            JSONArray optJSONArray = jSONObject.optJSONArray("gameList");
            this.gameList = new ArrayList();
            if (optJSONArray != null) {
                int length = optJSONArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                    if (optJSONObject != null) {
                        this.gameList.add(new GameListItem(optJSONObject));
                    }
                }
            }
            this.linkType = jSONObject.optInt("linkType");
            this.id = jSONObject.optInt("id");
            this.type = jSONObject.optInt("type");
            this.title = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            this.topTitle = jSONObject.optString("topTitle");
            this.loadImage = jSONObject.optString("loadImage");
            this.unjson = false;
        }
    }

    public String getButtonColor() {
        return this.buttonColor;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public String getCover() {
        return this.cover;
    }

    public List<GameListItem> getGameList() {
        return this.gameList;
    }

    public String getIcon() {
        return this.icon;
    }

    public int getId() {
        return this.id;
    }

    public int getIndex() {
        return this.index;
    }

    public String getLink() {
        return this.link;
    }

    public int getLinkType() {
        return this.linkType;
    }

    public String getLoadImage() {
        return this.loadImage;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTopTitle() {
        return this.topTitle;
    }

    public int getType() {
        return this.type;
    }

    public boolean invalidData() {
        return this.type == 8 && b.a(getGameList());
    }

    public void setButtonColor(String str) {
        this.buttonColor = str;
    }

    public void setButtonText(String str) {
        this.buttonText = str;
    }

    public void setCover(String str) {
        this.cover = str;
    }

    public void setGameList(List<GameListItem> list) {
        this.gameList = list;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public void setId(int i) {
        this.id = i;
    }

    public BannerCardBean setIndex(int i) {
        this.index = i;
        return this;
    }

    public void setLink(String str) {
        this.link = str;
    }

    public void setLinkType(int i) {
        this.linkType = i;
    }

    public void setLoadImage(String str) {
        this.loadImage = str;
    }

    public void setTitle(String str) {
        this.topTitle = str;
    }

    public void setTopTitle(String str) {
        this.topTitle = str;
    }

    public BannerCardBean setType(int i) {
        this.type = i;
        return this;
    }
}
