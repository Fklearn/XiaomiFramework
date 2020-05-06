package miui.yellowpage;

import android.content.Context;
import android.text.TextUtils;

public class YellowPagePhone {
    public static final int INVALIDE_YID = -1;
    private static final int MASK_SUSPECT = 240;
    private static final int MASK_T9_SEARCHABLE = 15;
    public static final int TYPE_ANTISPAM = 2;
    public static final int TYPE_MARKED = 3;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_YELLOW_PAGE = 1;
    private int mAntispamProviderId;
    private int mCid;
    private String mCreditImg;
    private int mFlag;
    private boolean mHasCallMenu;
    private int mMarkCount;
    private String mNormalizedNumber;
    private String mNumber;
    private int mNumberType;
    private int mProviderId;
    private String mRawSlogan;
    private String mSlogan;
    private long mT9Rank;
    private String mTag;
    private String mTagPinyin;
    private int mType;
    private boolean mVisible;
    private String mWordAd;
    private long mYpId;
    private String mYpName;
    private String mYpNamePinyin;

    public YellowPagePhone(long ypId, String ypName, String tag, String number, String normalizedNumber, int type, int providerId, int markedCount, boolean visible, String ypNamePinyin, String tagPinyin, boolean hasCallMenu) {
        this.mNumberType = 3;
        this.mYpName = ypName;
        this.mTag = tag;
        this.mNumber = number;
        this.mType = type;
        this.mVisible = visible;
        this.mMarkCount = markedCount;
        this.mYpId = ypId;
        this.mProviderId = providerId;
        this.mYpNamePinyin = ypNamePinyin;
        this.mTagPinyin = tagPinyin;
        this.mNormalizedNumber = normalizedNumber;
        this.mHasCallMenu = hasCallMenu;
    }

    public YellowPagePhone(long ypId, String ypName, String tag, String number, String normalizedNumber, int type, int providerId, int markedCount, boolean visible, String ypNamePinyin, String tagPinyin, boolean suspect, boolean hasCallMenu) {
        this.mNumberType = 3;
        this.mYpName = ypName;
        this.mTag = tag;
        this.mNumber = number;
        this.mType = type;
        this.mVisible = visible;
        this.mMarkCount = markedCount;
        this.mYpId = ypId;
        this.mProviderId = providerId;
        this.mYpNamePinyin = ypNamePinyin;
        this.mTagPinyin = tagPinyin;
        this.mNormalizedNumber = normalizedNumber;
        this.mHasCallMenu = hasCallMenu;
        if (suspect) {
            this.mFlag |= MASK_SUSPECT;
        }
    }

    public YellowPagePhone(long ypId, String ypName, String tag, String number, String normalizedNumber, int type, int providerId, int markedCount, boolean visible, String ypNamePinyin, String tagPinyin) {
        this(ypId, ypName, tag, number, normalizedNumber, type, providerId, markedCount, visible, ypNamePinyin, tagPinyin, false);
    }

    public YellowPagePhone(long ypId, String ypName, String tag, String number, String normalizedNumber, int type, int providerId, int markedCount, boolean visible, String ypNamePinyin, String tagPinyin, int cid) {
        this(ypId, ypName, tag, number, normalizedNumber, type, providerId, markedCount, visible, ypNamePinyin, tagPinyin, false);
        this.mCid = cid;
    }

    public YellowPagePhone setT9Rank(long t9Rank) {
        this.mT9Rank = t9Rank;
        return this;
    }

    public YellowPagePhone setRawSlogan(String slogan) {
        this.mRawSlogan = slogan;
        if (!TextUtils.isEmpty(this.mRawSlogan)) {
            int index = this.mRawSlogan.indexOf("$");
            if (index < 0 || index >= this.mRawSlogan.length() - 1) {
                this.mSlogan = this.mRawSlogan;
            } else {
                this.mSlogan = this.mRawSlogan.substring(0, index);
                this.mWordAd = this.mRawSlogan.substring(index + 1);
            }
        }
        return this;
    }

    public String getRawSlogan() {
        return this.mRawSlogan;
    }

    public YellowPagePhone setCid(int cid) {
        this.mCid = cid;
        return this;
    }

    public long getT9Rank() {
        return this.mT9Rank;
    }

    public String getSlogan() {
        return this.mSlogan;
    }

    public String getWordAd() {
        return this.mWordAd;
    }

    public boolean isAntispam() {
        return this.mCid > 0;
    }

    public boolean hasCallMenu() {
        return this.mHasCallMenu;
    }

    public boolean isUserMarked() {
        return this.mType == 3;
    }

    public boolean isYellowPage() {
        return this.mType == 1;
    }

    public int getPhoneType() {
        return this.mType;
    }

    public int getCid() {
        return this.mCid;
    }

    public String getYellowPageName() {
        return this.mYpName;
    }

    public long getYellowPageId() {
        return this.mYpId;
    }

    public String getTagPinyin() {
        return this.mTagPinyin;
    }

    public String getYellowPagePinyin() {
        return this.mYpNamePinyin;
    }

    public boolean isUnknown() {
        return this.mType == 0;
    }

    public String getTag() {
        return this.mTag;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int getFlag() {
        return this.mFlag;
    }

    public boolean isT9Searchable() {
        return (this.mFlag & 15) == 0;
    }

    public YellowPagePhone setFlag(int flag) {
        this.mFlag = flag;
        return this;
    }

    public String getNormalizedNumber() {
        return this.mNormalizedNumber;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public String getProviderName(Context context) {
        return YellowPageUtils.getProvider(context, this.mProviderId).getName();
    }

    public int getProviderId() {
        return this.mProviderId;
    }

    public boolean isProviderMiui() {
        return this.mProviderId == 0;
    }

    public int getMarkedCount() {
        return this.mMarkCount;
    }

    public int getAntispamProviderId() {
        return this.mAntispamProviderId;
    }

    public YellowPagePhone setAntispamProviderId(int id) {
        this.mAntispamProviderId = id;
        return this;
    }

    public String getCreditImg() {
        return this.mCreditImg;
    }

    public void setCreditImg(String creditImg) {
        this.mCreditImg = creditImg;
    }

    public int getNumberType() {
        return this.mNumberType;
    }

    public void setNumberType(int numberType) {
        this.mNumberType = numberType;
    }

    public boolean isSuspect(Context context) {
        return isMarkedSuspect();
    }

    public boolean isMarkedSuspect() {
        return (this.mFlag & MASK_SUSPECT) > 0;
    }
}
