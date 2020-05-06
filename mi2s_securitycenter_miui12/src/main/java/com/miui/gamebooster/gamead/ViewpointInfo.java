package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.miui.gamebooster.m.C0377h;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.la;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

public class ViewpointInfo implements Parcelable {
    public static final Parcelable.Creator<ViewpointInfo> CREATOR = new B();
    private ActivityInfo actInfo;
    private String content;
    private long createTime;
    private int dataType;
    private String deviceModel;
    private String deviceShowIcon;
    private boolean firstPost;
    private long gameId;
    private GameInfo gameInfo;
    private int hot;
    private boolean isEditRecommend;
    private int isEssence;
    private boolean isLike = false;
    private int isSetTop;
    private int likeCnt;
    private LikeInfo likeInfo;
    protected boolean mIsCollapsed = true;
    private int mPicCount;
    private ArrayList<Object> mViewPointTagInfos;
    private int mWordCount;
    private MixedContent mixedContent;
    private int owner;
    private int playDuration;
    private int reason;
    protected String relObjIconY;
    protected long relObjId;
    protected String relObjName;
    protected String relObjSubId;
    protected int relObjType;
    private int replyCnt;
    private int score;
    private int status;
    private String title;
    private List<ReplyInfo> topReplys;
    private List<SimpleTopicInfo> topicInfos;
    protected String traceId;
    private long updateTime;
    private User userInfo;
    private ViewPointVideoInfo videoInfo;
    private int viewCount;
    private String viewpointId;

    public ViewpointInfo() {
    }

    protected ViewpointInfo(Parcel parcel) {
        boolean z = false;
        this.viewpointId = parcel.readString();
        this.gameId = parcel.readLong();
        this.userInfo = (User) parcel.readParcelable(User.class.getClassLoader());
        this.title = parcel.readString();
        this.content = parcel.readString();
        this.score = parcel.readInt();
        this.playDuration = parcel.readInt();
        this.likeCnt = parcel.readInt();
        this.replyCnt = parcel.readInt();
        this.updateTime = parcel.readLong();
        this.createTime = parcel.readLong();
        this.status = parcel.readInt();
        this.likeInfo = (LikeInfo) parcel.readParcelable(LikeInfo.class.getClassLoader());
        this.gameInfo = (GameInfo) parcel.readParcelable(GameInfo.class.getClassLoader());
        this.topReplys = parcel.createTypedArrayList(ReplyInfo.CREATOR);
        this.dataType = parcel.readInt();
        this.actInfo = (ActivityInfo) parcel.readParcelable(ActivityInfo.class.getClassLoader());
        this.videoInfo = (ViewPointVideoInfo) parcel.readParcelable(ViewPointVideoInfo.class.getClassLoader());
        this.topicInfos = parcel.createTypedArrayList(SimpleTopicInfo.CREATOR);
        this.firstPost = parcel.readByte() != 0;
        this.mixedContent = (MixedContent) parcel.readParcelable(MixedContent.class.getClassLoader());
        this.reason = parcel.readInt();
        this.owner = parcel.readInt();
        this.mPicCount = parcel.readInt();
        this.mWordCount = parcel.readInt();
        this.viewCount = parcel.readInt();
        this.deviceModel = parcel.readString();
        this.deviceShowIcon = parcel.readString();
        this.hot = parcel.readInt();
        this.isEditRecommend = parcel.readByte() != 0;
        this.isLike = parcel.readByte() != 0 ? true : z;
        this.isEssence = parcel.readInt();
    }

    private static void checkVideoLike(ViewpointInfo viewpointInfo) {
        if (viewpointInfo != null) {
            ViewPointVideoInfo viewPointVideoInfo = null;
            if (viewpointInfo.hasVideo()) {
                if (viewpointInfo.getVideoInfo() == null) {
                    MixedContent mixedContent2 = viewpointInfo.getMixedContent();
                    if (mixedContent2 != null) {
                        List<Horizontal> horizontals = mixedContent2.getHorizontals();
                        if (!C0393y.a((List<?>) horizontals)) {
                            for (Horizontal verticalInRows : horizontals) {
                                Iterator<VerticalInRow> it = verticalInRows.getVerticalInRows().iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        break;
                                    }
                                    VerticalInRow next = it.next();
                                    if (next.getContentType() == 3) {
                                        viewPointVideoInfo = next.getVideoInfo();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    viewPointVideoInfo = viewpointInfo.videoInfo;
                }
            }
            if (viewPointVideoInfo != null) {
                la a2 = la.a();
                String url = viewPointVideoInfo.getUrl();
                String viewpointId2 = viewpointInfo.getViewpointId();
                int dataType2 = viewpointInfo.getDataType();
                boolean z = true;
                if (viewpointInfo.getLikeInfo() == null || viewpointInfo.getLikeInfo().getLikeType() != 1) {
                    z = false;
                }
                a2.a(url, viewpointId2, dataType2, z);
            }
        }
    }

    private boolean hasConentVideo() {
        MixedContent mixedContent2 = this.mixedContent;
        if (mixedContent2 != null && !C0393y.a((List<?>) mixedContent2.getHorizontals())) {
            for (Horizontal verticalInRows : this.mixedContent.getHorizontals()) {
                Iterator<VerticalInRow> it = verticalInRows.getVerticalInRows().iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (it.next().getContentType() == 3) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isLeagle(ViewpointInfo viewpointInfo) {
        return viewpointInfo != null && !TextUtils.isEmpty(viewpointInfo.viewpointId) && User.isLeagle(viewpointInfo.getUserInfo());
    }

    public static ViewpointInfo parseFromJson(JSONObject jSONObject) {
        String str;
        JSONObject jSONObject2 = jSONObject;
        if (jSONObject2 == null) {
            return null;
        }
        try {
            ViewpointInfo viewpointInfo = new ViewpointInfo();
            if (jSONObject2.has(MiStat.Param.CONTENT)) {
                viewpointInfo.content = jSONObject2.optString(MiStat.Param.CONTENT);
            }
            if (jSONObject2.has("createTime")) {
                str = MiStat.Param.CONTENT;
                viewpointInfo.createTime = jSONObject2.optLong("createTime");
            } else {
                str = MiStat.Param.CONTENT;
            }
            if (jSONObject2.has("dataType")) {
                viewpointInfo.dataType = jSONObject2.optInt("dataType");
            }
            if (jSONObject2.has("deviceModel")) {
                viewpointInfo.deviceModel = jSONObject2.optString("deviceModel");
            }
            if (jSONObject2.has("deviceShowIcon")) {
                viewpointInfo.deviceShowIcon = jSONObject2.optString("deviceShowIcon");
            }
            if (jSONObject2.has("gameId")) {
                viewpointInfo.gameId = jSONObject2.optLong("gameId");
            }
            if (jSONObject2.has("hot")) {
                viewpointInfo.hot = jSONObject2.optInt("hot");
            }
            if (jSONObject2.has("isLike") && jSONObject2.optInt("isLike") == 1) {
                viewpointInfo.isLike = true;
            }
            if (jSONObject2.has("likeCnt")) {
                viewpointInfo.likeCnt = jSONObject2.optInt("likeCnt");
            }
            if (jSONObject2.has("mixedContent")) {
                viewpointInfo.mixedContent = MixedContent.parseFromJson(jSONObject2.optJSONObject("mixedContent"));
            }
            if (jSONObject2.has("owner")) {
                viewpointInfo.owner = jSONObject2.optInt("owner");
            }
            if (jSONObject2.has("playDuration")) {
                viewpointInfo.playDuration = jSONObject2.optInt("playDuration");
            }
            if (jSONObject2.has("replyCnt")) {
                viewpointInfo.replyCnt = jSONObject2.optInt("replyCnt");
            }
            if (jSONObject2.has(MiStat.Param.SCORE)) {
                viewpointInfo.score = jSONObject2.optInt(MiStat.Param.SCORE);
            }
            if (jSONObject2.has("status")) {
                viewpointInfo.status = jSONObject2.optInt("status");
            }
            if (jSONObject2.has("videoInfo")) {
                viewpointInfo.videoInfo = new ViewPointVideoInfo(jSONObject2.optJSONObject("videoInfo"));
            }
            if (jSONObject2.has("summaryInfo")) {
                JSONObject jSONObject3 = jSONObject2.getJSONObject("summaryInfo");
                if (jSONObject3.has("picsCnt")) {
                    viewpointInfo.mPicCount = jSONObject3.optInt("picsCnt");
                }
                if (jSONObject3.has("wordsCnt")) {
                    viewpointInfo.mWordCount = jSONObject3.optInt("wordsCnt");
                }
                String str2 = str;
                if (jSONObject3.has(str2)) {
                    viewpointInfo.mixedContent = MixedContent.parseFromJson(jSONObject3.optJSONObject(str2));
                }
                if (jSONObject3.has("videoInfo")) {
                    viewpointInfo.videoInfo = new ViewPointVideoInfo(jSONObject3.optJSONObject("videoInfo"));
                }
                if (jSONObject3.has("summary")) {
                    viewpointInfo.content = jSONObject3.optString("summary");
                }
            }
            if (jSONObject2.has(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME)) {
                viewpointInfo.title = jSONObject2.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            }
            if (jSONObject2.has("traceId")) {
                viewpointInfo.traceId = jSONObject2.optString("traceId");
            }
            if (jSONObject2.has("updateTime")) {
                viewpointInfo.updateTime = jSONObject2.optLong("updateTime");
            }
            if (jSONObject2.has("userInfo")) {
                viewpointInfo.userInfo = User.fromJson(jSONObject2.optJSONObject("userInfo"));
            }
            if (jSONObject2.has("viewCount")) {
                viewpointInfo.viewCount = jSONObject2.optInt("viewCount");
            }
            if (jSONObject2.has("viewpointId")) {
                viewpointInfo.viewpointId = jSONObject2.optString("viewpointId");
            }
            if (jSONObject2.has("isEssence")) {
                viewpointInfo.isEssence = jSONObject2.optInt("isEssence");
            }
            if (jSONObject2.has("isSetTop")) {
                viewpointInfo.isSetTop = jSONObject2.optInt("isSetTop");
            }
            if (jSONObject2.has("gameInfo")) {
                viewpointInfo.gameInfo = GameInfo.fromJson(jSONObject2.optJSONObject("gameInfo"));
            }
            if (jSONObject2.has("relObjId")) {
                viewpointInfo.relObjId = jSONObject2.getLong("relObjId");
            }
            if (jSONObject2.has("relObjType")) {
                viewpointInfo.relObjType = jSONObject2.getInt("relObjType");
            }
            if (jSONObject2.has("relObjSubId")) {
                viewpointInfo.relObjSubId = jSONObject2.getString("relObjSubId");
            }
            checkVideoLike(viewpointInfo);
            return viewpointInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public ActivityInfo getActInfo() {
        return this.actInfo;
    }

    public String getContent() {
        return C0377h.b(this.content);
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public int getDataType() {
        return this.dataType;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public String getDeviceShowIcon() {
        return this.deviceShowIcon;
    }

    public long getGameId() {
        return this.gameId;
    }

    public GameInfo getGameInfo() {
        return this.gameInfo;
    }

    public int getHot() {
        return this.hot;
    }

    public int getLikeCnt() {
        return this.likeCnt;
    }

    public LikeInfo getLikeInfo() {
        return this.likeInfo;
    }

    public MixedContent getMixedContent() {
        return this.mixedContent;
    }

    public int getOwner() {
        return this.owner;
    }

    public int getPicCount() {
        return this.mPicCount;
    }

    public int getPlayDuration() {
        return this.playDuration;
    }

    public int getReason() {
        return this.reason;
    }

    public String getRelObjIconY() {
        return this.relObjIconY;
    }

    public long getRelObjId() {
        return this.relObjId;
    }

    public String getRelObjName() {
        return this.relObjName;
    }

    public String getRelObjSubId() {
        return this.relObjSubId;
    }

    public int getRelObjType() {
        return this.relObjType;
    }

    public int getReplyCnt() {
        return this.replyCnt;
    }

    public int getScore() {
        return this.score;
    }

    public List<SimpleTopicInfo> getSimpleTopicInfos() {
        return this.topicInfos;
    }

    public int getStatus() {
        return this.status;
    }

    public String getTitle() {
        return C0377h.b(this.title);
    }

    public List<ReplyInfo> getTopReplys() {
        return this.topReplys;
    }

    public String getTraceId() {
        return this.traceId;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public User getUserInfo() {
        return this.userInfo;
    }

    public ViewPointVideoInfo getVideoInfo() {
        ViewPointVideoInfo viewPointVideoInfo = this.videoInfo;
        if (viewPointVideoInfo == null || TextUtils.isEmpty(viewPointVideoInfo.getUrl())) {
            return null;
        }
        return this.videoInfo;
    }

    public int getViewCount() {
        return this.viewCount;
    }

    public ArrayList<Object> getViewPointTagInfos() {
        return this.mViewPointTagInfos;
    }

    public String getViewpointId() {
        return this.viewpointId;
    }

    public int getWordCount() {
        return this.mWordCount;
    }

    public boolean hasActivityOrTopic() {
        return this.actInfo != null || !C0393y.a((List<?>) this.topicInfos);
    }

    public boolean hasPic() {
        MixedContent mixedContent2 = this.mixedContent;
        if (mixedContent2 != null && !C0393y.a((List<?>) mixedContent2.getHorizontals())) {
            for (Horizontal verticalInRows : this.mixedContent.getHorizontals()) {
                Iterator<VerticalInRow> it = verticalInRows.getVerticalInRows().iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (it.next().getContentType() == 2) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasVideo() {
        return this.dataType == 3 || hasConentVideo();
    }

    public boolean isCollapsed() {
        return this.mIsCollapsed;
    }

    public boolean isEditRecommend() {
        return this.isEditRecommend;
    }

    public boolean isEssence() {
        return this.isEssence == 1;
    }

    public boolean isFirstPost() {
        return this.firstPost;
    }

    public boolean isLike() {
        if (this.isLike) {
            return true;
        }
        LikeInfo likeInfo2 = this.likeInfo;
        return likeInfo2 != null && likeInfo2.getLikeType() == 1;
    }

    public boolean isSetTop() {
        return this.isSetTop == 1;
    }

    public void setCollapsed(boolean z) {
        this.mIsCollapsed = z;
    }

    public void setHot(int i) {
        this.hot = i;
    }

    public void setLikeCnt(int i) {
        this.likeCnt = i;
    }

    public void setLikeInfo(LikeInfo likeInfo2) {
        this.likeInfo = likeInfo2;
    }

    public void setLikeStatus() {
        this.likeCnt++;
        this.isLike = true;
    }

    public void setReplyCnt(int i) {
        this.replyCnt = i;
    }

    public void setUserInfo(User user) {
        this.userInfo = user;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.viewpointId);
        parcel.writeLong(this.gameId);
        parcel.writeParcelable(this.userInfo, i);
        parcel.writeString(this.title);
        parcel.writeString(this.content);
        parcel.writeInt(this.score);
        parcel.writeInt(this.playDuration);
        parcel.writeInt(this.likeCnt);
        parcel.writeInt(this.replyCnt);
        parcel.writeLong(this.updateTime);
        parcel.writeLong(this.createTime);
        parcel.writeInt(this.status);
        parcel.writeParcelable(this.likeInfo, i);
        parcel.writeParcelable(this.gameInfo, i);
        parcel.writeTypedList(this.topReplys);
        parcel.writeInt(this.dataType);
        parcel.writeParcelable(this.actInfo, i);
        parcel.writeParcelable(this.videoInfo, i);
        parcel.writeTypedList(this.topicInfos);
        parcel.writeByte(this.firstPost ? (byte) 1 : 0);
        parcel.writeParcelable(this.mixedContent, i);
        parcel.writeInt(this.reason);
        parcel.writeInt(this.owner);
        parcel.writeInt(this.mPicCount);
        parcel.writeInt(this.mWordCount);
        parcel.writeInt(this.viewCount);
        parcel.writeString(this.deviceModel);
        parcel.writeString(this.deviceShowIcon);
        parcel.writeInt(this.hot);
        parcel.writeByte(this.isEditRecommend ? (byte) 1 : 0);
        parcel.writeByte(this.isLike ? (byte) 1 : 0);
        parcel.writeInt(this.isEssence);
    }
}
