package com.miui.gamebooster.gamead;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.maml.elements.MusicLyricParser;
import com.xiaomi.stat.MiStat;
import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable {
    public static final Parcelable.Creator<User> CREATOR = new r();
    public static final int GENDER_MAN = 1;
    public static final int GENDER_WOMAN = 2;
    public static final int USER_TYPE_NORMAL = 0;
    private String TAG;
    private int h5GamePlayTimes;
    private int h5GameWinTimes;
    private boolean isBothFollowing;
    private boolean isFollow;
    private boolean isNoTalking;
    private boolean isOfficialCert;
    private long mAvatar;
    private String mBirthday;
    private String mCertIcon;
    private String mCertName;
    private String mCertType;
    private String mCover;
    private ExamInfo mExamInfo;
    private int mFansCount;
    private int mFollowCount;
    private int mGameCount;
    private int mGender;
    private boolean mIsFocused;
    private int mLikeCount;
    private int mNickNamEditCount;
    private String mNickname;
    private String mPhoneNum;
    private String mRemark;
    private String mSign;
    private long mUid;
    private long mUnBlockTs;
    private User mUser;
    private UserSettingInfo mUserSettingInfo;
    private int mUserType;
    private int mViewpointCount;

    private static class ExamInfo implements Parcelable {
        public static final Parcelable.Creator<ExamInfo> CREATOR = new s();
        /* access modifiers changed from: private */
        public boolean isPass;
        /* access modifiers changed from: private */
        public int mScore;
        /* access modifiers changed from: private */
        public long mUploadTime;

        public ExamInfo() {
        }

        protected ExamInfo(Parcel parcel) {
            this.mScore = parcel.readInt();
            this.mUploadTime = parcel.readLong();
            this.isPass = parcel.readByte() != 0;
        }

        public ExamInfo(JSONObject jSONObject) {
            if (jSONObject != null) {
                if (jSONObject.has("isPass")) {
                    this.isPass = jSONObject.optBoolean("isPass");
                }
                if (jSONObject.has(MiStat.Param.SCORE)) {
                    this.mScore = jSONObject.optInt(MiStat.Param.SCORE);
                }
                if (jSONObject.has("uploadTs")) {
                    this.mUploadTime = jSONObject.optLong("uploadTs");
                }
            }
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mScore);
            parcel.writeLong(this.mUploadTime);
            parcel.writeByte(this.isPass ? (byte) 1 : 0);
        }
    }

    public User() {
        this.TAG = User.class.getSimpleName();
        this.mUid = 0;
        this.mFollowCount = 0;
        this.mFansCount = 0;
        this.mUserType = 0;
    }

    public User(long j, long j2, String str) {
        this.TAG = User.class.getSimpleName();
        this.mUid = 0;
        this.mFollowCount = 0;
        this.mFansCount = 0;
        this.mUserType = 0;
        this.mUid = j;
        this.mAvatar = j2;
        this.mNickname = str;
    }

    public User(long j, String str, long j2) {
        this.TAG = User.class.getSimpleName();
        this.mUid = 0;
        this.mFollowCount = 0;
        this.mFansCount = 0;
        this.mUserType = 0;
        this.mUid = j;
        this.mAvatar = j2;
        this.mNickname = str;
    }

    protected User(Parcel parcel) {
        this.TAG = User.class.getSimpleName();
        this.mUid = 0;
        boolean z = false;
        this.mFollowCount = 0;
        this.mFansCount = 0;
        this.mUserType = 0;
        this.TAG = parcel.readString();
        this.mUid = parcel.readLong();
        this.mAvatar = parcel.readLong();
        this.mNickname = parcel.readString();
        this.mGender = parcel.readInt();
        this.mSign = parcel.readString();
        this.mCover = parcel.readString();
        this.mCertType = parcel.readString();
        this.mCertName = parcel.readString();
        this.mCertIcon = parcel.readString();
        this.isOfficialCert = parcel.readByte() != 0;
        this.mRemark = parcel.readString();
        this.mUnBlockTs = parcel.readLong();
        this.mExamInfo = (ExamInfo) parcel.readParcelable(ExamInfo.class.getClassLoader());
        this.mFollowCount = parcel.readInt();
        this.h5GamePlayTimes = parcel.readInt();
        this.h5GameWinTimes = parcel.readInt();
        this.mFansCount = parcel.readInt();
        this.isFollow = parcel.readByte() != 0;
        this.isBothFollowing = parcel.readByte() != 0;
        this.mGameCount = parcel.readInt();
        this.mLikeCount = parcel.readInt();
        this.mUserType = parcel.readInt();
        this.mUserSettingInfo = (UserSettingInfo) parcel.readParcelable(UserSettingInfo.class.getClassLoader());
        this.isNoTalking = parcel.readByte() != 0 ? true : z;
        this.mNickNamEditCount = parcel.readInt();
        this.mPhoneNum = parcel.readString();
        this.mBirthday = parcel.readString();
    }

    public User(User user) {
        this.TAG = User.class.getSimpleName();
        this.mUid = 0;
        this.mFollowCount = 0;
        this.mFansCount = 0;
        this.mUserType = 0;
        this.mUser = user;
    }

    public static User fromJson(JSONObject jSONObject) {
        String str;
        JSONObject jSONObject2 = jSONObject;
        if (jSONObject2 == null) {
            return null;
        }
        try {
            User user = new User();
            if (jSONObject2.has("examInfo")) {
                str = "isBothingFollowing";
                user.mExamInfo = new ExamInfo(jSONObject2.optJSONObject("examInfo"));
            } else {
                str = "isBothingFollowing";
            }
            if (jSONObject2.has("uuid")) {
                user.mUid = jSONObject2.getLong("uuid");
            }
            if (jSONObject2.has("headImgTs")) {
                user.mAvatar = jSONObject2.optLong("headImgTs");
            }
            if (jSONObject2.has("nickname")) {
                user.mNickname = jSONObject2.optString("nickname");
            }
            if (jSONObject2.has("sex")) {
                user.mGender = jSONObject2.optInt("sex");
            }
            if (jSONObject2.has(WarningModel.Columns.SIGNATURE)) {
                user.mSign = jSONObject2.optString(WarningModel.Columns.SIGNATURE);
            }
            if (jSONObject2.has("coverPhoto")) {
                user.mCover = jSONObject2.optString("coverPhoto");
            }
            if (jSONObject2.has("certType")) {
                user.mCertType = jSONObject2.optString("certType");
            }
            if (jSONObject2.has("certName")) {
                user.mCertName = jSONObject2.optString("certName");
            }
            if (jSONObject2.has("certIcon")) {
                user.mCertIcon = jSONObject2.optString("certIcon");
            }
            if (jSONObject2.has("remark")) {
                user.mRemark = jSONObject2.optString("remark");
            }
            if (jSONObject2.has("mFollowCount")) {
                user.mFollowCount = jSONObject2.optInt("mFollowCount");
            }
            if (jSONObject2.has("h5GamePlayTimes")) {
                user.h5GamePlayTimes = jSONObject2.optInt("h5GamePlayTimes");
            }
            if (jSONObject2.has("h5GameWinTimes")) {
                user.h5GameWinTimes = jSONObject2.optInt("h5GameWinTimes");
            }
            if (jSONObject2.has("mFansCount")) {
                user.mFansCount = jSONObject2.optInt("mFansCount");
            }
            if (jSONObject2.has("isFollow")) {
                user.isFollow = jSONObject2.optBoolean("isFollow");
            }
            if (jSONObject2.has("mGameCount")) {
                user.mGameCount = jSONObject2.optInt("mGameCount");
            }
            if (jSONObject2.has("mLikeCount")) {
                user.mLikeCount = jSONObject2.optInt("mLikeCount");
            }
            if (jSONObject2.has("mUserType")) {
                user.mUserType = jSONObject2.optInt("mUserType");
            }
            String str2 = str;
            if (jSONObject2.has(str2)) {
                user.isBothFollowing = jSONObject2.optBoolean(str2);
            }
            if (!TextUtils.isEmpty(user.mCertType)) {
                if (user.mCertType.startsWith("100_")) {
                    user.isOfficialCert = true;
                } else {
                    user.isOfficialCert = false;
                }
            }
            user.mRemark = jSONObject2.optString("remark");
            user.mFollowCount = jSONObject2.optInt("mFollowCount");
            user.mFansCount = jSONObject2.optInt("mFansCount");
            user.isFollow = jSONObject2.optBoolean("isFollow");
            user.mGameCount = jSONObject2.optInt("mGameCount");
            user.mLikeCount = jSONObject2.optInt("mLikeCount");
            user.mUserType = jSONObject2.optInt("mUserType");
            user.isBothFollowing = jSONObject2.optBoolean(str2);
            user.mBirthday = jSONObject2.optString("mBirthday");
            if (isLeagle(user)) {
                return user;
            }
            return null;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public static boolean isLeagle(User user) {
        return user != null && user.mUid >= 0;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return obj != null && (obj instanceof User) && ((User) obj).mUid == this.mUid;
    }

    public long getAvatar() {
        return this.mAvatar;
    }

    public String getBirthday() {
        return this.mBirthday;
    }

    public String getCertIcon() {
        return this.mCertIcon;
    }

    public String getCertName() {
        return this.mCertName;
    }

    public String getCertType() {
        return this.mCertType;
    }

    public String getCover() {
        return this.mCover;
    }

    public ExamInfo getExamInfo() {
        return this.mExamInfo;
    }

    public int getFansCount() {
        return this.mFansCount;
    }

    public int getFollowCount() {
        return this.mFollowCount;
    }

    public int getGameCount() {
        return this.mGameCount;
    }

    public int getGender() {
        return this.mGender;
    }

    public int getH5GamePlayTimes() {
        return this.h5GamePlayTimes;
    }

    public int getH5GameWinTimes() {
        return this.h5GameWinTimes;
    }

    public int getLikCount() {
        return this.mLikeCount;
    }

    public int getLikeCount() {
        return this.mLikeCount;
    }

    public int getNickNamEditCount() {
        return this.mNickNamEditCount;
    }

    public String getNickname() {
        return this.mNickname;
    }

    public String getPhoneNum() {
        return this.mPhoneNum;
    }

    public String getRemark() {
        return this.mRemark;
    }

    public int getScore() {
        ExamInfo examInfo = this.mExamInfo;
        if (examInfo == null) {
            return 0;
        }
        return examInfo.mScore;
    }

    public String getSign() {
        return this.mSign;
    }

    public long getUid() {
        return this.mUid;
    }

    public long getUnBlockTs() {
        return this.mUnBlockTs;
    }

    public long getUploadTs() {
        ExamInfo examInfo = this.mExamInfo;
        if (examInfo == null) {
            return 0;
        }
        return examInfo.mUploadTime;
    }

    public User getUser() {
        return this.mUser;
    }

    public UserSettingInfo getUserSettingInfo() {
        return this.mUserSettingInfo;
    }

    public int getUserType() {
        return this.mUserType;
    }

    public int getViewpointCount() {
        return this.mViewpointCount;
    }

    public boolean isBothFollowing() {
        return this.isBothFollowing;
    }

    public boolean isExamPass() {
        ExamInfo examInfo = this.mExamInfo;
        if (examInfo == null) {
            return false;
        }
        return examInfo.isPass;
    }

    public boolean isFocused() {
        return this.mIsFocused;
    }

    public boolean isFollow() {
        return this.isFollow;
    }

    public boolean isNoTalking() {
        return this.isNoTalking;
    }

    public boolean isOfficialCert() {
        return this.isOfficialCert;
    }

    public boolean isShield() {
        UserSettingInfo userSettingInfo = this.mUserSettingInfo;
        if (userSettingInfo == null) {
            return false;
        }
        return userSettingInfo.isShield();
    }

    public void setAvatar(long j) {
        this.mAvatar = j;
    }

    public void setBirthday(String str) {
        this.mBirthday = str;
    }

    public void setBothFollowing(boolean z) {
        this.isBothFollowing = z;
    }

    public void setCertIcon(String str) {
        this.mCertIcon = str;
    }

    public void setCertName(String str) {
        this.mCertName = str;
    }

    public void setCertType(String str) {
        this.mCertType = str;
    }

    public void setCover(String str) {
        this.mCover = str;
    }

    public void setExamInfo(ExamInfo examInfo) {
        this.mExamInfo = examInfo;
    }

    public void setFansCount(int i) {
        this.mFansCount = i;
    }

    public void setFollow(boolean z) {
        this.isFollow = z;
    }

    public void setFollowCount(int i) {
        this.mFollowCount = i;
    }

    public void setGameCount(int i) {
        this.mGameCount = i;
    }

    public void setGender(int i) {
        this.mGender = i;
    }

    public void setH5GamePlayTimes(int i) {
        this.h5GamePlayTimes = i;
    }

    public void setH5GameWinTimes(int i) {
        this.h5GameWinTimes = i;
    }

    public void setIsFocused(boolean z) {
        this.mIsFocused = z;
    }

    public void setLikeCount(int i) {
        this.mLikeCount = i;
    }

    public void setNickNamEditCount(int i) {
        this.mNickNamEditCount = i;
    }

    public void setNickname(String str) {
        this.mNickname = str;
    }

    public void setNoTalking(boolean z) {
        this.isNoTalking = z;
        if (this.mUserSettingInfo == null) {
            this.mUserSettingInfo = new UserSettingInfo(this.mUid);
        }
        this.mUserSettingInfo.setNoTalking(this.isNoTalking);
    }

    public void setOfficialCert(boolean z) {
        this.isOfficialCert = z;
    }

    public void setPhoneNum(String str) {
        this.mPhoneNum = str;
    }

    public void setRemark(String str) {
        this.mRemark = str;
    }

    public void setShield(boolean z) {
        if (this.mUserSettingInfo == null) {
            this.mUserSettingInfo = new UserSettingInfo(this.mUid);
        }
        this.mUserSettingInfo.setShield(z);
    }

    public void setSign(String str) {
        if (!TextUtils.isEmpty(str)) {
            str = str.replace(MusicLyricParser.CRLF, "").replace("\n", "");
        }
        this.mSign = str;
    }

    public void setUid(long j) {
        this.mUid = j;
    }

    public void setUnBlockTs(long j) {
        this.mUnBlockTs = j;
    }

    public void setUserSettingInfo(UserSettingInfo userSettingInfo) {
        this.mUserSettingInfo = userSettingInfo;
    }

    public void setUserType(int i) {
        this.mUserType = i;
    }

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("uuid", this.mUid);
            jSONObject.put("headImgTs", this.mAvatar);
            jSONObject.put("nickname", this.mNickname);
            jSONObject.put("sex", this.mGender);
            jSONObject.put(WarningModel.Columns.SIGNATURE, this.mSign);
            jSONObject.put("coverPhoto", this.mCover);
            jSONObject.put("mFollowCount", this.mFollowCount);
            jSONObject.put("mFansCount", this.mFansCount);
            jSONObject.put("isFollow", this.isFollow);
            jSONObject.put("mGameCount", this.mGameCount);
            jSONObject.put("mLikeCount", this.mLikeCount);
            jSONObject.put("mUserType", this.mUserType);
            jSONObject.put("isBothingFollowing", this.isBothFollowing);
            jSONObject.put("mBirthday", this.mBirthday);
            jSONObject.put("h5GamePlayTimes", this.h5GamePlayTimes);
            jSONObject.put("h5GameWinTimes", this.h5GameWinTimes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public String toString() {
        return "User{mUid=" + this.mUid + ", mAvatar=" + this.mAvatar + ", mNickname='" + this.mNickname + '\'' + ", mGender=" + this.mGender + ", mSign='" + this.mSign + '\'' + ", mCover='" + this.mCover + '\'' + ", mFollowCount=" + this.mFollowCount + ", mFansCount=" + this.mFansCount + ", isFollow=" + this.isFollow + ", mGameCount=" + this.mGameCount + ", mLikeCount=" + this.mLikeCount + ", mUserType=" + this.mUserType + ", mBirthday=" + this.mBirthday + ", h5GamePlayTimes=" + this.h5GamePlayTimes + ", h5GameWinTimes=" + this.h5GameWinTimes + '}';
    }

    public void updateScore(long j, int i, long j2) {
        if (this.mUid == j) {
            if (this.mExamInfo == null) {
                this.mExamInfo = new ExamInfo();
            }
            long unused = this.mExamInfo.mUploadTime = j2;
            int unused2 = this.mExamInfo.mScore = i;
            if (i >= 60) {
                boolean unused3 = this.mExamInfo.isPass = true;
            }
        }
    }

    public void updateScore(User user) {
        if (user != null) {
            ExamInfo examInfo = user.mExamInfo;
            if (examInfo == null) {
                this.mExamInfo = null;
                return;
            }
            ExamInfo examInfo2 = this.mExamInfo;
            if (examInfo2 == null) {
                this.mExamInfo = examInfo;
                return;
            }
            boolean unused = examInfo2.isPass = examInfo.isPass;
            int unused2 = this.mExamInfo.mScore = user.mExamInfo.mScore;
            long unused3 = this.mExamInfo.mUploadTime = user.mExamInfo.mUploadTime;
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.TAG);
        parcel.writeLong(this.mUid);
        parcel.writeLong(this.mAvatar);
        parcel.writeString(this.mNickname);
        parcel.writeInt(this.mGender);
        parcel.writeString(this.mSign);
        parcel.writeString(this.mCover);
        parcel.writeString(this.mCertType);
        parcel.writeString(this.mCertName);
        parcel.writeString(this.mCertIcon);
        parcel.writeByte(this.isOfficialCert ? (byte) 1 : 0);
        parcel.writeString(this.mRemark);
        parcel.writeLong(this.mUnBlockTs);
        parcel.writeParcelable(this.mExamInfo, i);
        parcel.writeInt(this.mFollowCount);
        parcel.writeInt(this.h5GamePlayTimes);
        parcel.writeInt(this.h5GameWinTimes);
        parcel.writeInt(this.mFansCount);
        parcel.writeByte(this.isFollow ? (byte) 1 : 0);
        parcel.writeByte(this.isBothFollowing ? (byte) 1 : 0);
        parcel.writeInt(this.mGameCount);
        parcel.writeInt(this.mLikeCount);
        parcel.writeInt(this.mUserType);
        parcel.writeParcelable(this.mUserSettingInfo, i);
        parcel.writeByte(this.isNoTalking ? (byte) 1 : 0);
        parcel.writeInt(this.mNickNamEditCount);
        parcel.writeString(this.mPhoneNum);
        parcel.writeString(this.mBirthday);
    }
}
