package miui.yellowpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import miui.telephony.phonenumber.Prefix;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class YellowPage {
    private static final String TAG = "YellowPage";
    private String mAddress;
    private String mAlias;
    private String mAuthIconName;
    private String mBrief;
    private String mCatId;
    private String mContent;
    private String mCreditImg;
    private String mExtraData;
    private String mFirmUrl;
    private List<String> mGallery;
    private String mHotCatId;
    private int mHotSort;
    private boolean mIsHot;
    private boolean mIsMasterPage;
    private boolean mIsPreset;
    private String mLatitude;
    private String mLocId;
    private String mLongitude;
    private String mMiId;
    private String mName;
    private List<YellowPagePhone> mPhones = new ArrayList();
    private String mPhotoUrl;
    private String mPinyin;
    private int mProviderId;
    private List<Provider> mProviders;
    private String mSlogan;
    private List<Social> mSocials;
    private String mSourceId;
    private String mSourceUrl;
    private String mThumbnailUrl;
    private String mUrl;
    private long mYid;

    private interface TagCallMenuNIvr {
        public static final String CALL_MENU = "callMenu";
        public static final String ICON = "icon";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String TYPE = "type";
    }

    private interface TagSocial {
        public static final String NAME = "name";
        public static final String PROVIDER = "provider";
        public static final String PROVIDER_NAME = "providerName";
        public static final String URL = "url";
    }

    public static class Provider {
        private int mId;
        private String mSourceUrl;

        public Provider setId(int id) {
            this.mId = id;
            return this;
        }

        public int getId() {
            return this.mId;
        }

        public Provider setSourceUrl(String url) {
            this.mSourceUrl = url;
            return this;
        }

        public String getSourceUrl() {
            return this.mSourceUrl;
        }

        public static Provider fromJson(JSONObject json) {
            try {
                int id = json.getInt("provider");
                return new Provider().setId(id).setSourceUrl(json.getString(Tag.TagYellowPage.SOURCE_URL));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public YellowPage setSlogan(String slogan) {
        this.mSlogan = slogan;
        return this;
    }

    public String getSlogan() {
        return this.mSlogan;
    }

    public YellowPage setLongitude(String longitude) {
        this.mLongitude = longitude;
        return this;
    }

    public String getLongitude() {
        return this.mLongitude;
    }

    public YellowPage setLatitude(String latitude) {
        this.mLatitude = latitude;
        return this;
    }

    public String getLatitude() {
        return this.mLatitude;
    }

    public YellowPage setAuthIconName(String authIconName) {
        this.mAuthIconName = authIconName;
        return this;
    }

    public String getAuthIconName() {
        return this.mAuthIconName;
    }

    public YellowPage setId(long id) {
        this.mYid = id;
        return this;
    }

    public long getId() {
        return this.mYid;
    }

    public YellowPage setUrl(String url) {
        this.mUrl = url;
        return this;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public YellowPage setFirmUrl(String firmUrl) {
        this.mFirmUrl = firmUrl;
        return this;
    }

    public String getCreditImg() {
        return this.mCreditImg;
    }

    public YellowPage setCreditImg(String creditImg) {
        this.mCreditImg = creditImg;
        return this;
    }

    public String getFirmUrl() {
        return this.mFirmUrl;
    }

    public YellowPage setBrief(String brief) {
        this.mBrief = brief;
        return this;
    }

    public String getBrief() {
        return this.mBrief;
    }

    public YellowPage setAddress(String addr) {
        this.mAddress = addr;
        return this;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public YellowPage setAlias(String alias) {
        this.mAlias = alias;
        return this;
    }

    public String getAlias() {
        return this.mAlias;
    }

    public YellowPage setGallery(List<String> gallery) {
        this.mGallery = gallery;
        return this;
    }

    public List<String> getGallery() {
        return this.mGallery;
    }

    public YellowPage setName(String name) {
        this.mName = name;
        return this;
    }

    public String getName() {
        return this.mName;
    }

    public YellowPage setPinyin(String pinyin) {
        this.mPinyin = pinyin;
        return this;
    }

    public String getPinyin() {
        return this.mPinyin;
    }

    public YellowPage setPhones(List<YellowPagePhone> phones) {
        this.mPhones = phones;
        return this;
    }

    public List<YellowPagePhone> getPhones() {
        return this.mPhones;
    }

    public String getProviderName(Context context) {
        return YellowPageUtils.getProvider(context, this.mProviderId).getName();
    }

    public YellowPage setProviderId(int id) {
        this.mProviderId = id;
        return this;
    }

    public int getProviderId() {
        return this.mProviderId;
    }

    public String getMiId() {
        return this.mMiId;
    }

    public YellowPage setMiId(String miId) {
        this.mMiId = miId;
        return this;
    }

    public Bitmap getProviderIcon(Context context) {
        return YellowPageUtils.getProvider(context, this.mProviderId).getIcon();
    }

    public byte[] getPhoto() {
        return null;
    }

    public YellowPage setPhotoName(String url) {
        this.mPhotoUrl = url;
        return this;
    }

    public String getPhotoName() {
        return this.mPhotoUrl;
    }

    public YellowPage setThumbnailName(String name) {
        this.mThumbnailUrl = name;
        return this;
    }

    public String getThumbnailName() {
        return this.mThumbnailUrl;
    }

    public byte[] getThumbnail() {
        return null;
    }

    public YellowPage setSocials(List<Social> socials) {
        this.mSocials = socials;
        return this;
    }

    public List<Social> getSocials() {
        return this.mSocials;
    }

    public YellowPage setIsMasterPage(boolean master) {
        this.mIsMasterPage = master;
        return this;
    }

    public boolean isMasterPage() {
        return this.mIsMasterPage;
    }

    public YellowPage setIsHot(boolean hot) {
        this.mIsHot = hot;
        return this;
    }

    public boolean isHot() {
        return this.mIsHot;
    }

    public YellowPage setIsPreset(boolean isPreset) {
        this.mIsPreset = isPreset;
        return this;
    }

    public boolean isPreset() {
        return this.mIsPreset;
    }

    private YellowPage setContent(String content) {
        this.mContent = content;
        return this;
    }

    public String getContent() {
        return this.mContent;
    }

    public boolean isProviderMiui() {
        return this.mProviderId == 0;
    }

    public YellowPage setSourceUrl(String url) {
        this.mSourceUrl = url;
        return this;
    }

    public String getSourceUrl() {
        return this.mSourceUrl;
    }

    public YellowPage setSourceId(String id) {
        this.mSourceId = id;
        return this;
    }

    public String getSourceId() {
        return this.mSourceId;
    }

    public YellowPage setHotSort(int sort) {
        this.mHotSort = sort;
        return this;
    }

    public int getHotSort() {
        return this.mHotSort;
    }

    public YellowPage setHotCatId(String id) {
        this.mHotCatId = id;
        return this;
    }

    public String getHotCatId() {
        String str = this.mHotCatId;
        return str == null ? Prefix.EMPTY : str;
    }

    public YellowPage setCatId(String catId) {
        this.mCatId = catId;
        return this;
    }

    public String getCatId() {
        return this.mCatId;
    }

    public YellowPage setLocId(String id) {
        this.mLocId = id;
        return this;
    }

    public String getLocId() {
        return this.mLocId;
    }

    public List<Provider> getProviderList() {
        return this.mProviders;
    }

    public YellowPage setProviderList(List<Provider> list) {
        this.mProviders = list;
        return this;
    }

    public YellowPagePhone getPhoneInfo(Context context, String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        String normalizedNumber = YellowPageUtils.getNormalizedNumber(context, number);
        List<YellowPagePhone> list = this.mPhones;
        if (list != null) {
            for (YellowPagePhone phone : list) {
                if (TextUtils.equals(normalizedNumber, phone.getNormalizedNumber())) {
                    return phone;
                }
            }
        }
        return null;
    }

    public String getExtraData() {
        return this.mExtraData;
    }

    public YellowPage setExtraData(String mExtraData2) {
        this.mExtraData = mExtraData2;
        return this;
    }

    public static YellowPage fromJson(String jsonString) {
        try {
            return fromJson(new JSONObject(jsonString));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static YellowPage fromJson(JSONObject json) {
        String name;
        boolean isPreset;
        boolean isMasterPage;
        String address;
        String alias;
        boolean isHotPage;
        String authIcon;
        String miId;
        String latitude;
        String longitude;
        String locId;
        String catId;
        int hotSort;
        String hotCatId;
        String url;
        String firmUrl;
        String sourceUrl;
        String sourceId;
        String brief;
        int pid;
        String pinyin;
        String slogan;
        List<YellowPagePhone> phoneList;
        String creditImg;
        String str;
        boolean isHotPage2;
        List<YellowPagePhone> phoneList2;
        JSONObject jSONObject = json;
        String creditImg2 = "phone";
        try {
            long yid = jSONObject.getLong(Tag.TagYellowPage.YID);
            String name2 = jSONObject.getString(Tag.TagYellowPage.NAME);
            int pid2 = jSONObject.optInt("provider");
            String pinyin2 = jSONObject.optString(Tag.TagYellowPage.PINYIN);
            String alias2 = jSONObject.optString(Tag.TagYellowPage.ALIAS);
            String address2 = jSONObject.optString("address");
            String url2 = jSONObject.optString(Tag.TagYellowPage.URL);
            String firmUrl2 = jSONObject.optString(Tag.TagYellowPage.FIRMURL);
            String creditImg3 = jSONObject.optString(Tag.TagYellowPage.CREDITIMG);
            String sourceUrl2 = jSONObject.optString(Tag.TagYellowPage.SOURCE_URL);
            String sourceId2 = jSONObject.optString(Tag.TagYellowPage.SOURCE_ID);
            String brief2 = jSONObject.optString(Tag.TagYellowPage.BRIEF_INFO);
            String url3 = url2;
            String hotCatId2 = jSONObject.optString("hotCatId");
            int hotSort2 = jSONObject.optInt("hotSort");
            String catId2 = jSONObject.optString("catId");
            String locId2 = jSONObject.optString("locId");
            String longitude2 = jSONObject.optString("longitude");
            String latitude2 = jSONObject.optString("latitude");
            String miId2 = jSONObject.optString("miid");
            String miSudId = jSONObject.optString(Tag.TagYellowPage.MI_SUB_ID);
            String slogan2 = jSONObject.optString("slogan");
            String authIcon2 = jSONObject.optString(Tag.TagYellowPage.AUTH_ICON);
            String alias3 = alias2;
            int pid3 = pid2;
            boolean isMasterPage2 = jSONObject.optInt(Tag.TagYellowPage.TYPE) == 2;
            boolean isHotPage3 = jSONObject.optInt(Tag.TagYellowPage.HOT) == 1;
            String pinyin3 = pinyin2;
            boolean isPreset2 = jSONObject.optInt("buildIn") == 1;
            boolean hasCallMenu = jSONObject.has(TagCallMenuNIvr.CALL_MENU);
            JSONArray phoneArray = jSONObject.optJSONArray(creditImg2);
            if (phoneArray != null) {
                boolean isPreset3 = isPreset2;
                int flag = 0;
                List<YellowPagePhone> phoneList3 = null;
                while (true) {
                    isHotPage2 = isHotPage3;
                    if (flag >= phoneArray.length()) {
                        break;
                    }
                    JSONObject phoneJson = phoneArray.getJSONObject(flag);
                    String brief3 = brief2;
                    String brief4 = phoneJson.getString(creditImg2);
                    String str2 = creditImg2;
                    String sourceId3 = sourceId2;
                    String normalizedNumber = phoneJson.optString(Tag.TagPhone.NORMALIZED_NUMBER);
                    String sourceUrl3 = sourceUrl2;
                    String tag = phoneJson.getString(Tag.TagPhone.Tag);
                    String tagPinyin = phoneJson.getString(Tag.TagPhone.PINYIN);
                    long t9Rank = phoneJson.optLong(Tag.TagPhone.T9_RANK);
                    int atdCatId = phoneJson.optInt(Tag.TagPhone.ATD_CAT_ID);
                    boolean isMasterPage3 = isMasterPage2;
                    String creditImg4 = creditImg3;
                    int count = phoneJson.optInt(Tag.TagPhone.MARKED_COUNT);
                    int antispamProviderId = phoneJson.optInt("provider");
                    int flag2 = phoneJson.optInt("flag");
                    String firmUrl3 = firmUrl2;
                    boolean visible = phoneJson.getInt("hide") == 0;
                    if (phoneList3 == null) {
                        phoneList2 = new ArrayList<>();
                    } else {
                        phoneList2 = phoneList3;
                    }
                    String url4 = url3;
                    String hotCatId3 = hotCatId2;
                    int hotSort3 = hotSort2;
                    String catId3 = catId2;
                    String locId3 = locId2;
                    JSONObject jSONObject2 = phoneJson;
                    List<YellowPagePhone> phoneList4 = phoneList2;
                    JSONArray phoneArray2 = phoneArray;
                    String pinyin4 = pinyin3;
                    int pid4 = pid3;
                    YellowPagePhone ypPhone = new YellowPagePhone(yid, name2, tag, brief4, normalizedNumber, 1, pid4, count, visible, pinyin4, tagPinyin, hasCallMenu);
                    long t9Rank2 = t9Rank;
                    ypPhone.setT9Rank(t9Rank2);
                    String slogan3 = slogan2;
                    ypPhone.setRawSlogan(slogan3);
                    ypPhone.setCid(atdCatId);
                    ypPhone.setFlag(flag2);
                    ypPhone.setAntispamProviderId(antispamProviderId);
                    int i = atdCatId;
                    String creditImg5 = creditImg4;
                    ypPhone.setCreditImg(creditImg5);
                    long j = t9Rank2;
                    List<YellowPagePhone> phoneList5 = phoneList4;
                    phoneList5.add(ypPhone);
                    flag++;
                    creditImg3 = creditImg5;
                    phoneList3 = phoneList5;
                    slogan2 = slogan3;
                    pinyin3 = pinyin4;
                    phoneArray = phoneArray2;
                    pid3 = pid4;
                    brief2 = brief3;
                    creditImg2 = str2;
                    sourceId2 = sourceId3;
                    sourceUrl2 = sourceUrl3;
                    firmUrl2 = firmUrl3;
                    url3 = url4;
                    hotCatId2 = hotCatId3;
                    hotSort2 = hotSort3;
                    catId2 = catId3;
                    locId2 = locId3;
                    longitude2 = longitude2;
                    latitude2 = latitude2;
                    miId2 = miId2;
                    authIcon2 = authIcon2;
                    isHotPage3 = isHotPage2;
                    alias3 = alias3;
                    address2 = address2;
                    isMasterPage2 = isMasterPage3;
                    isPreset3 = isPreset3;
                    name2 = name2;
                }
                isMasterPage = isMasterPage2;
                address = address2;
                sourceUrl = sourceUrl2;
                brief = brief2;
                sourceId = sourceId2;
                name = name2;
                creditImg = creditImg3;
                firmUrl = firmUrl2;
                url = url3;
                hotCatId = hotCatId2;
                hotSort = hotSort2;
                catId = catId2;
                locId = locId2;
                longitude = longitude2;
                latitude = latitude2;
                miId = miId2;
                authIcon = authIcon2;
                alias = alias3;
                pid = pid3;
                pinyin = pinyin3;
                isPreset = isPreset3;
                isHotPage = isHotPage2;
                JSONArray jSONArray = phoneArray;
                int i2 = flag;
                slogan = slogan2;
                phoneList = phoneList3;
            } else {
                isHotPage = isHotPage3;
                isMasterPage = isMasterPage2;
                address = address2;
                sourceUrl = sourceUrl2;
                brief = brief2;
                sourceId = sourceId2;
                isPreset = isPreset2;
                name = name2;
                creditImg = creditImg3;
                firmUrl = firmUrl2;
                url = url3;
                hotCatId = hotCatId2;
                hotSort = hotSort2;
                catId = catId2;
                locId = locId2;
                longitude = longitude2;
                latitude = latitude2;
                miId = miId2;
                authIcon = authIcon2;
                alias = alias3;
                pid = pid3;
                pinyin = pinyin3;
                JSONArray jSONArray2 = phoneArray;
                slogan = slogan2;
                phoneList = null;
            }
            JSONArray socialArray = jSONObject.optJSONArray(Tag.TagYellowPage.SOCIAL);
            List<Social> socialList = null;
            if (socialArray != null && socialArray.length() > 0) {
                socialList = new ArrayList<>();
                for (int i3 = 0; i3 < socialArray.length(); i3++) {
                    JSONObject socialJson = socialArray.getJSONObject(i3);
                    socialList.add(new Social(socialJson.getString("url"), socialJson.getString("name"), socialJson.optInt("provider")));
                }
            }
            List<Provider> providers = new ArrayList<>();
            JSONArray providerArray = jSONObject.optJSONArray(Tag.TagYellowPage.PROVIDER_LIST);
            if (providerArray != null) {
                for (int i4 = 0; i4 < providerArray.length(); i4++) {
                    Provider provider = Provider.fromJson(providerArray.getJSONObject(i4));
                    if (provider != null) {
                        providers.add(provider);
                    }
                }
            }
            String photoUrl = jSONObject.optString(Tag.TagYellowPage.PHOTO);
            String thumbnailUrl = jSONObject.optString(Tag.TagYellowPage.THUMBNAIL);
            String extraData = jSONObject.optString("extraData");
            String brief5 = brief;
            YellowPage brief6 = new YellowPage().setId(yid).setName(name).setPinyin(pinyin).setBrief(brief5);
            String str3 = brief5;
            String alias4 = alias;
            YellowPage alias5 = brief6.setAlias(alias4);
            String str4 = alias4;
            String alias6 = address;
            String str5 = alias6;
            int pid5 = pid;
            YellowPage providerId = alias5.setAddress(alias6).setPhones(phoneList).setSocials(socialList).setThumbnailName(thumbnailUrl).setPhotoName(photoUrl).setProviderId(pid5);
            int i5 = pid5;
            String url5 = url;
            YellowPage url6 = providerId.setUrl(url5);
            String str6 = url5;
            String firmUrl4 = firmUrl;
            YellowPage creditImg6 = url6.setFirmUrl(firmUrl4).setCreditImg(creditImg);
            String str7 = creditImg;
            String creditImg7 = sourceUrl;
            YellowPage sourceUrl4 = creditImg6.setSourceUrl(creditImg7);
            String str8 = creditImg7;
            String sourceId4 = sourceId;
            YellowPage sourceId5 = sourceUrl4.setSourceId(sourceId4);
            String str9 = sourceId4;
            boolean isMasterPage4 = isMasterPage;
            YellowPage isMasterPage5 = sourceId5.setIsMasterPage(isMasterPage4);
            boolean z = isMasterPage4;
            boolean isMasterPage6 = isPreset;
            YellowPage isPreset4 = isMasterPage5.setIsPreset(isMasterPage6);
            boolean z2 = isMasterPage6;
            boolean isHotPage4 = isHotPage;
            YellowPage isHot = isPreset4.setIsHot(isHotPage4);
            boolean z3 = isHotPage4;
            String hotCatId4 = hotCatId;
            YellowPage hotCatId5 = isHot.setHotCatId(hotCatId4);
            String str10 = hotCatId4;
            int hotSort4 = hotSort;
            YellowPage hotSort5 = hotCatId5.setHotSort(hotSort4);
            int i6 = hotSort4;
            String catId4 = catId;
            YellowPage catId5 = hotSort5.setCatId(catId4);
            String str11 = catId4;
            String locId4 = locId;
            YellowPage locId5 = catId5.setLocId(locId4);
            String str12 = locId4;
            String longitude3 = longitude;
            YellowPage longitude4 = locId5.setLongitude(longitude3);
            String str13 = longitude3;
            String longitude5 = latitude;
            String str14 = longitude5;
            YellowPage providerList = longitude4.setLatitude(longitude5).setContent(json.toString()).setSlogan(slogan).setProviderList(providers);
            if (TextUtils.isEmpty(miSudId)) {
                String str15 = firmUrl4;
                String firmUrl5 = miSudId;
                str = miId;
            } else {
                StringBuilder sb = new StringBuilder();
                String str16 = firmUrl4;
                String firmUrl6 = miId;
                sb.append(firmUrl6);
                String str17 = firmUrl6;
                sb.append("/");
                sb.append(miSudId);
                str = sb.toString();
            }
            return providerList.setMiId(str).setAuthIconName(authIcon).setExtraData(extraData);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Social {
        private String mName;
        private int mProviderId;
        private String mUrl;

        public Social(String url, String name, int providerId) {
            this.mUrl = url;
            this.mName = name;
            this.mProviderId = providerId;
        }

        public String getUrl() {
            return this.mUrl;
        }

        public String getName() {
            return this.mName;
        }

        public int getProviderId() {
            return this.mProviderId;
        }
    }
}
