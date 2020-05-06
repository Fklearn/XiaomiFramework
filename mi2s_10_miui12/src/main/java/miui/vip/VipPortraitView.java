package miui.vip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.internal.vip.VipConstants;
import com.miui.internal.vip.VipInternalCallback;
import com.miui.internal.vip.utils.ImageDownloader;
import com.miui.internal.vip.utils.JsonParser;
import com.miui.internal.vip.utils.Utils;
import com.miui.internal.vip.utils.VipDataPref;
import com.miui.system.internal.R;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import miui.accounts.ExtraAccountManager;

public class VipPortraitView extends RelativeLayout {
    static final int ACHIEVEMENT_COUNT = 4;
    static final String BANNER = "banner";
    static final int[] BadgeIconSize = {R.dimen.vip_achievement_icon_size, R.dimen.vip_achievement_icon_size_1};
    static final int[] LayoutId = {R.layout.vip_portrait_view, R.layout.vip_portrait_expand_view};
    static final int MAX_BANNER_COUNT = 2;
    static final int MODEL_COMPACT = 0;
    static final int MODEL_EXPAND = 1;
    static final String PREF_KEY_BANNER = "banner";
    static final String PREF_NAME = "portrait_view";
    static final String STATISTIC_AVATAR = "portrait_avatar";
    static final String STATISTIC_BACKGROUND = "portrait_background";
    static final String STATISTIC_BANNER = "portrait_banner_";
    static final String STATISTIC_CUSTOM_BUTTON = "portrait_custom_button";
    static final String STATISTIC_SIGN = "portrait_sign";
    static final int WRAP_CONTENT = -1;
    public int ARROW_STYLE_CARD;
    public int ARROW_STYLE_LIST;
    View.OnClickListener mAccountWelcomeClick;
    List<VipAchievement> mAchievementList;
    LinearLayout mAchievements;
    ImageView mAction;
    View.OnClickListener mActionClick;
    Drawable mActionIcon;
    View mArrow;
    int mArrowCardMargin;
    int mArrowListMargin;
    int mArrowStyle;
    ImageView mAvatar;
    View.OnClickListener mAvatarClick;
    View.OnClickListener mBackgroundClick;
    ImageView mBadge;
    LinearLayout mBanner;
    View mBannerGroup;
    List<VipBanner> mBannerList;
    Comparator<VipAchievement> mCmpVipAchievement;
    Comparator<VipBanner> mCmpVipBanner;
    Account mExtAccount;
    View mFrame;
    TextView mIdView;
    private final VipInternalCallback mListener;
    Drawable mLockIcon;
    TextView mName;
    VipDataPref mPref;
    private final BroadcastReceiver mReceiver;
    boolean mServiceAvailable;
    boolean mShowBanner;
    int mShowModel;
    TextView mSign;
    View.OnClickListener mSignClick;
    View mSignGroup;
    TextView mTitle;
    View.OnClickListener mUserDetailClick;
    long mUserId;
    VipUserInfo mUserInfo;
    String mUserSign;
    View.OnClickListener mVipLevelListClick;

    static class ClickListenerWrapper implements View.OnClickListener {
        View.OnClickListener mClickListener;
        String mData;

        ClickListenerWrapper(String data, View.OnClickListener clickListener) {
            this.mClickListener = clickListener;
            this.mData = data;
        }

        public void onClick(View v) {
            VipService.instance().sendStatistic(this.mData);
            View.OnClickListener onClickListener = this.mClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    /* access modifiers changed from: private */
    public static <T> boolean isSameList(List<T> left, List<T> right, Comparator<T> cmp) {
        if (left == null) {
            if (right == null) {
                return true;
            }
            return false;
        } else if (right == null || left.size() != right.size()) {
            return false;
        } else {
            int count = left.size();
            for (int i = 0; i < count; i++) {
                if (cmp.compare(left.get(i), right.get(i)) != 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public VipPortraitView(Context context) {
        super(context);
        this.ARROW_STYLE_LIST = 0;
        this.ARROW_STYLE_CARD = 1;
        this.mArrowStyle = this.ARROW_STYLE_LIST;
        this.mShowModel = 0;
        this.mShowBanner = true;
        this.mCmpVipAchievement = new Comparator<VipAchievement>() {
            public int compare(VipAchievement lhs, VipAchievement rhs) {
                return lhs.id != rhs.id ? -1 : 0;
            }
        };
        this.mCmpVipBanner = new Comparator<VipBanner>() {
            public int compare(VipBanner lhs, VipBanner rhs) {
                return (!TextUtils.equals(lhs.name, rhs.name) || !TextUtils.equals(lhs.icon, rhs.icon)) ? -1 : 0;
            }
        };
        this.mAccountWelcomeClick = new View.OnClickListener() {
            public void onClick(View v) {
                Utils.startActivity(VipPortraitView.this.getContext(), VipConstants.ACTION_ACCOUNT_WELCOME, "com.xiaomi.account");
            }
        };
        this.mUserDetailClick = new ClickListenerWrapper(STATISTIC_AVATAR, new View.OnClickListener() {
            public void onClick(View v) {
                Utils.startActivity(VipPortraitView.this.getContext(), VipConstants.ACTION_USER_DETAIL, "com.xiaomi.account");
            }
        });
        this.mAvatarClick = this.mUserDetailClick;
        this.mVipLevelListClick = new ClickListenerWrapper(STATISTIC_BACKGROUND, new View.OnClickListener() {
            public void onClick(View v) {
                Utils.startActivity(VipPortraitView.this.getContext(), "com.xiaomi.vip.action.VIP_LEVEL_LIST", VipConstants.VIP_PACKAGE);
            }
        });
        this.mBackgroundClick = this.mVipLevelListClick;
        this.mListener = new VipInternalCallback(16, 64) {
            public void onUserInfo(int code, VipUserInfo user, String errMsg) {
                Utils.log("VipPortraitView.onUserInfo, code = %d, user = %s, errMsg = %s", Integer.valueOf(code), user, errMsg);
                if (code == 0) {
                    VipPortraitView.this.setVipLevel(user);
                }
            }

            public void onAchievements(int code, List<VipAchievement> list, String errMsg) {
                if (code == 0 && !VipPortraitView.isSameList(VipPortraitView.this.mAchievementList, list, VipPortraitView.this.mCmpVipAchievement)) {
                    VipPortraitView.this.setAchievements(list);
                }
            }

            public void onConnected(boolean serviceAvailable, VipUserInfo user, List<VipAchievement> achievements) {
                Object[] objArr = new Object[3];
                objArr[0] = Boolean.valueOf(serviceAvailable);
                objArr[1] = user;
                objArr[2] = achievements != null ? Arrays.toString(achievements.toArray()) : "null";
                Utils.log("VipPortraitView.onConnected, serviceAvailable = %s, user = %s, achievements = %s", objArr);
                VipPortraitView vipPortraitView = VipPortraitView.this;
                vipPortraitView.mServiceAvailable = serviceAvailable;
                if (serviceAvailable) {
                    Utils.log("VipPortraitView.onConnected, before setAchievements", new Object[0]);
                    if (Utils.hasData((List<?>) achievements)) {
                        VipPortraitView.this.setAchievements(achievements);
                    } else {
                        VipService.instance().queryAchievements();
                    }
                    VipService.instance().queryBanners();
                    Utils.log("VipPortraitView.onConnected, before setVipLevel", new Object[0]);
                    if (user != null) {
                        VipPortraitView.this.setVipLevel(user);
                    } else {
                        VipService.instance().queryUserVipInfo();
                    }
                } else {
                    vipPortraitView.clearVipInfo();
                }
            }

            public void onBanners(int code, List<VipBanner> list, String errMsg) {
                if (code == 0 && !VipPortraitView.isSameList(VipPortraitView.this.mBannerList, list, VipPortraitView.this.mCmpVipBanner)) {
                    VipPortraitView.this.saveBannerData(list);
                    VipPortraitView.this.setBanners(list);
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Utils.log("VipPortraitView.mReceiver, action = %s", action);
                VipPortraitView.this.setAccountData();
                if (!TextUtils.equals(action, "android.accounts.LOGIN_ACCOUNTS_POST_CHANGED")) {
                    return;
                }
                if (intent.getIntExtra("extra_update_type", 0) == 2) {
                    Utils.log("mReciever, user is added, connect vip service", new Object[0]);
                    VipPortraitView.this.connect();
                    return;
                }
                Utils.log("mReciever, user is removed, disconnect vip service", new Object[0]);
                VipPortraitView.this.disconnect();
            }
        };
    }

    public VipPortraitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VipPortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ARROW_STYLE_LIST = 0;
        this.ARROW_STYLE_CARD = 1;
        this.mArrowStyle = this.ARROW_STYLE_LIST;
        this.mShowModel = 0;
        this.mShowBanner = true;
        this.mCmpVipAchievement = new Comparator<VipAchievement>() {
            public int compare(VipAchievement lhs, VipAchievement rhs) {
                return lhs.id != rhs.id ? -1 : 0;
            }
        };
        this.mCmpVipBanner = new Comparator<VipBanner>() {
            public int compare(VipBanner lhs, VipBanner rhs) {
                return (!TextUtils.equals(lhs.name, rhs.name) || !TextUtils.equals(lhs.icon, rhs.icon)) ? -1 : 0;
            }
        };
        this.mAccountWelcomeClick = new View.OnClickListener() {
            public void onClick(View v) {
                Utils.startActivity(VipPortraitView.this.getContext(), VipConstants.ACTION_ACCOUNT_WELCOME, "com.xiaomi.account");
            }
        };
        this.mUserDetailClick = new ClickListenerWrapper(STATISTIC_AVATAR, new View.OnClickListener() {
            public void onClick(View v) {
                Utils.startActivity(VipPortraitView.this.getContext(), VipConstants.ACTION_USER_DETAIL, "com.xiaomi.account");
            }
        });
        this.mAvatarClick = this.mUserDetailClick;
        this.mVipLevelListClick = new ClickListenerWrapper(STATISTIC_BACKGROUND, new View.OnClickListener() {
            public void onClick(View v) {
                Utils.startActivity(VipPortraitView.this.getContext(), "com.xiaomi.vip.action.VIP_LEVEL_LIST", VipConstants.VIP_PACKAGE);
            }
        });
        this.mBackgroundClick = this.mVipLevelListClick;
        this.mListener = new VipInternalCallback(16, 64) {
            public void onUserInfo(int code, VipUserInfo user, String errMsg) {
                Utils.log("VipPortraitView.onUserInfo, code = %d, user = %s, errMsg = %s", Integer.valueOf(code), user, errMsg);
                if (code == 0) {
                    VipPortraitView.this.setVipLevel(user);
                }
            }

            public void onAchievements(int code, List<VipAchievement> list, String errMsg) {
                if (code == 0 && !VipPortraitView.isSameList(VipPortraitView.this.mAchievementList, list, VipPortraitView.this.mCmpVipAchievement)) {
                    VipPortraitView.this.setAchievements(list);
                }
            }

            public void onConnected(boolean serviceAvailable, VipUserInfo user, List<VipAchievement> achievements) {
                Object[] objArr = new Object[3];
                objArr[0] = Boolean.valueOf(serviceAvailable);
                objArr[1] = user;
                objArr[2] = achievements != null ? Arrays.toString(achievements.toArray()) : "null";
                Utils.log("VipPortraitView.onConnected, serviceAvailable = %s, user = %s, achievements = %s", objArr);
                VipPortraitView vipPortraitView = VipPortraitView.this;
                vipPortraitView.mServiceAvailable = serviceAvailable;
                if (serviceAvailable) {
                    Utils.log("VipPortraitView.onConnected, before setAchievements", new Object[0]);
                    if (Utils.hasData((List<?>) achievements)) {
                        VipPortraitView.this.setAchievements(achievements);
                    } else {
                        VipService.instance().queryAchievements();
                    }
                    VipService.instance().queryBanners();
                    Utils.log("VipPortraitView.onConnected, before setVipLevel", new Object[0]);
                    if (user != null) {
                        VipPortraitView.this.setVipLevel(user);
                    } else {
                        VipService.instance().queryUserVipInfo();
                    }
                } else {
                    vipPortraitView.clearVipInfo();
                }
            }

            public void onBanners(int code, List<VipBanner> list, String errMsg) {
                if (code == 0 && !VipPortraitView.isSameList(VipPortraitView.this.mBannerList, list, VipPortraitView.this.mCmpVipBanner)) {
                    VipPortraitView.this.saveBannerData(list);
                    VipPortraitView.this.setBanners(list);
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Utils.log("VipPortraitView.mReceiver, action = %s", action);
                VipPortraitView.this.setAccountData();
                if (!TextUtils.equals(action, "android.accounts.LOGIN_ACCOUNTS_POST_CHANGED")) {
                    return;
                }
                if (intent.getIntExtra("extra_update_type", 0) == 2) {
                    Utils.log("mReciever, user is added, connect vip service", new Object[0]);
                    VipPortraitView.this.connect();
                    return;
                }
                Utils.log("mReciever, user is removed, disconnect vip service", new Object[0]);
                VipPortraitView.this.disconnect();
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VipPortraitView, R.attr.vipShowModel, 0);
        this.mShowModel = a.getInt(R.styleable.VipPortraitView_vipShowModel, 0);
        a.recycle();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Utils.log("VipPortraitView.onAttachedToWindow", new Object[0]);
        getContext().registerReceiver(this.mReceiver, Utils.ACCOUNT_CHANGE_FILTER);
        connect();
        initViewAndSetData();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Utils.log("VipPortraitView.onDetachedToWindow", new Object[0]);
        this.mExtAccount = null;
        ImageDownloader.stop();
        getContext().unregisterReceiver(this.mReceiver);
        disconnect();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        initViewAndSetData();
    }

    public void showBanner(boolean show) {
        this.mShowBanner = show;
        setBanners(this.mBannerList);
    }

    public boolean isShowCompactModel() {
        return this.mShowModel == 0;
    }

    public void setArrowStyle(int style) {
        View view = this.mArrow;
        if (view != null && this.mShowModel == 1) {
            ((RelativeLayout.LayoutParams) view.getLayoutParams()).setMarginEnd(style == this.ARROW_STYLE_CARD ? this.mArrowCardMargin : this.mArrowListMargin);
        }
        this.mArrowStyle = style;
    }

    public void setAvatarViewClickListener(View.OnClickListener listener) {
        View.OnClickListener onClickListener;
        if (listener != null) {
            onClickListener = new ClickListenerWrapper(STATISTIC_AVATAR, listener);
        } else {
            onClickListener = this.mUserDetailClick;
        }
        this.mAvatarClick = onClickListener;
        View view = this.mFrame;
        if (view != null) {
            view.setOnClickListener(this.mAvatarClick);
        }
    }

    public void setSignatureViewClickListener(View.OnClickListener listener) {
        this.mSignClick = new ClickListenerWrapper(STATISTIC_SIGN, listener);
        View view = this.mSignGroup;
        if (view != null) {
            view.setOnClickListener(this.mSignClick);
        }
    }

    public void setOnClickListener(View.OnClickListener l) {
        View.OnClickListener onClickListener;
        if (l != null) {
            onClickListener = new ClickListenerWrapper(STATISTIC_BACKGROUND, l);
        } else {
            onClickListener = this.mServiceAvailable ? this.mVipLevelListClick : null;
        }
        this.mBackgroundClick = onClickListener;
        super.setOnClickListener(this.mBackgroundClick);
    }

    public void setCustomButton(Drawable icon, View.OnClickListener onClick) {
        this.mActionIcon = icon;
        this.mActionClick = new ClickListenerWrapper(STATISTIC_CUSTOM_BUTTON, onClick);
        ImageView imageView = this.mAction;
        if (imageView != null) {
            imageView.setImageDrawable(icon);
            this.mAction.setOnClickListener(this.mActionClick);
        }
    }

    public void setSignature(String signature) {
        if (this.mSignGroup != null) {
            String sign = this.mSign.getText().toString();
            if (!TextUtils.isEmpty(signature)) {
                this.mSignGroup.setVisibility(0);
                if (!signature.equals(sign)) {
                    this.mSign.setText(this.mUserSign);
                }
            } else {
                this.mSignGroup.setVisibility(8);
            }
        }
        this.mUserSign = signature;
    }

    @Deprecated
    public static View getAchievementView(Context ctx, List<VipAchievement> list) {
        return null;
    }

    public void setXiaomiAccount(Account account) {
        this.mExtAccount = account;
        setAccountData();
    }

    public void connect() {
        VipService.instance().connect(this.mListener);
    }

    public void disconnect() {
        VipService.instance().disconnect(this.mListener);
    }

    public void showBottomDivider(boolean show) {
        findViewById(R.id.vip_bottom_divider).setVisibility(show ? 0 : 8);
    }

    private void initViewAndSetData() {
        if (this.mFrame == null) {
            this.mArrowListMargin = getResources().getDimensionPixelSize(R.dimen.vip_margin_arrow_right);
            this.mArrowCardMargin = getResources().getDimensionPixelSize(R.dimen.vip_margin_frame_left);
            initView();
            setAccountData();
            loadBannerData();
        }
    }

    private synchronized VipDataPref getPref() {
        if (this.mPref == null) {
            this.mPref = new VipDataPref(getContext(), PREF_NAME);
        }
        return this.mPref;
    }

    /* access modifiers changed from: private */
    public void saveBannerData(List<VipBanner> bannerList) {
        String bannerJson = JsonParser.toJson(bannerList);
        Utils.log("VipPortraitView.saveBannerData, bannerList = %s, bannerJson = %s", bannerList, bannerJson);
        VipDataPref pref = getPref();
        pref.setString("banner" + this.mUserId, bannerJson);
    }

    private void loadBannerData() {
        if (this.mUserId > 0) {
            VipDataPref pref = getPref();
            List<VipBanner> list = JsonParser.parseJsonArrayAsList(pref.getString("banner" + this.mUserId), VipBanner.class);
            Utils.log("VipPortraitView.loadBannerData, list = %s", Arrays.toString(list.toArray()));
            if (Utils.hasData((List<?>) list)) {
                setBanners(list);
            }
        }
    }

    private void changeModel(int newModel) {
        if (this.mShowModel != newModel) {
            this.mShowModel = newModel;
            removeAllViews();
            initView();
            loadData();
        }
    }

    private void loadData() {
        Utils.log("loadData", new Object[0]);
        setAccountData();
        loadBannerData();
        setVipLevel(this.mUserInfo);
        setSignature(this.mUserSign);
        setCustomButton(this.mActionIcon, this.mActionClick);
        setAchievements(this.mAchievementList);
    }

    private void initView() {
        Utils.log("initView", new Object[0]);
        inflate(getContext(), LayoutId[this.mShowModel], this);
        this.mFrame = findViewById(R.id.vip_id_frame);
        this.mFrame.setOnClickListener(this.mAvatarClick);
        this.mAvatar = (ImageView) findViewById(R.id.vip_id_avatar);
        this.mTitle = (TextView) findViewById(R.id.vip_id_title);
        this.mTitle.setVisibility(8);
        this.mName = (TextView) findViewById(R.id.vip_id_name);
        this.mIdView = (TextView) findViewById(R.id.vip_id_user_id);
        this.mBadge = (ImageView) findViewById(R.id.vip_id_badge);
        this.mSignGroup = findViewById(R.id.vip_id_sign_group);
        this.mSignGroup.setOnClickListener(this.mSignClick);
        this.mSign = (TextView) findViewById(R.id.vip_id_sign);
        this.mAchievements = (LinearLayout) findViewById(R.id.vip_id_achievements);
        this.mAction = (ImageView) findViewById(R.id.vip_id_custom_action);
        this.mArrow = findViewById(R.id.vip_id_arrow);
        setArrowStyle(this.mArrowStyle);
        this.mLockIcon = getContext().getResources().getDrawable(R.drawable.vip_icon_default_achievement);
        initBanner();
    }

    private void initBanner() {
        this.mBannerGroup = findViewById(R.id.vip_id_banner_group);
        this.mBanner = (LinearLayout) findViewById(R.id.vip_id_banner);
    }

    /* access modifiers changed from: private */
    public void clearVipInfo() {
        Utils.log("clearVipInfo", new Object[0]);
        setVipLevel((VipUserInfo) null);
        setAchievements((List<VipAchievement>) null);
        setBanners((List<VipBanner>) null);
    }

    /* access modifiers changed from: private */
    public void setVipLevel(VipUserInfo user) {
        VipUserInfo vipUserInfo;
        Utils.log("setVipLevel", new Object[0]);
        super.setOnClickListener(this.mBackgroundClick);
        if (this.mFrame != null) {
            if (user == null && this.mUserInfo != null) {
                Utils.log("setVipLevel, hide views of vip frame and level", new Object[0]);
                this.mBadge.setImageBitmap((Bitmap) null);
            } else if (user != null && user.level > 0 && ((vipUserInfo = this.mUserInfo) == null || vipUserInfo.level != user.level || this.mBadge.getDrawable() == null)) {
                Utils.log("setVipLevel, level = %d", Integer.valueOf(user.level));
                ImageDownloader.loadImage(getContext(), String.format(VipConstants.LEVEL_IMG, new Object[]{Integer.valueOf(user.level)}), VipService.VIP_LEVEL_ICON, this.mBadge);
            }
        }
        this.mUserInfo = user;
    }

    /* access modifiers changed from: private */
    public void setAccountData() {
        long time = System.currentTimeMillis();
        Account account = this.mExtAccount;
        if (account == null) {
            account = ExtraAccountManager.getXiaomiAccount(getContext());
        }
        Utils.log("setAccountData, account = %s", account);
        if (!(this.mName == null || this.mIdView == null)) {
            AccountManager am = AccountManager.get(getContext());
            if (account != null) {
                this.mUserId = TextUtils.isDigitsOnly(account.name) ? Long.valueOf(account.name).longValue() : 0;
                this.mFrame.setOnClickListener(this.mAvatarClick);
                super.setOnClickListener(this.mBackgroundClick);
                String oldId = this.mIdView.getText().toString();
                if (TextUtils.isEmpty(oldId) || !oldId.equals(account.name)) {
                    Utils.log("setAccountData, data is changed", new Object[0]);
                    this.mIdView.setText(account.name);
                }
                String name = this.mName.getText().toString();
                String userName = am.getUserData(account, VipConstants.ACCOUNT_USER_NAME);
                Utils.log("setAccountData, userName = %s", userName);
                if (TextUtils.isEmpty(name) || !name.equals(userName)) {
                    this.mName.setText(TextUtils.isEmpty(userName) ? account.name : userName);
                }
            } else {
                Utils.log("setAccountData, user isn't signed in", new Object[0]);
                this.mName.setText(R.string.vip_not_login);
                this.mIdView.setText(R.string.vip_login);
                this.mFrame.setOnClickListener(this.mAccountWelcomeClick);
                super.setOnClickListener(this.mAccountWelcomeClick);
                clearVipInfo();
            }
            loadAvatarFile(account, am);
        }
        Utils.log("setAccountData end, elapsed %d", Long.valueOf(System.currentTimeMillis() - time));
    }

    private void showAchievement(boolean show) {
        this.mAchievements.setVisibility(show ? 0 : 8);
    }

    private static void drawAchievementLock(LinearLayout container, Drawable lockIcon, int size) {
        if (lockIcon != null) {
            int iconCount = container.getChildCount();
            for (int i = 0; i < iconCount; i++) {
                View iconView = container.getChildAt(i);
                Object tag = iconView.getTag();
                if ((tag instanceof VipAchievement) && !((VipAchievement) tag).isOwned) {
                    addIconCover(iconView, size, lockIcon);
                }
            }
        }
    }

    private static void addIconCover(View iconView, int size, Drawable lockIcon) {
        ImageView cover = (ImageView) iconView.findViewById(R.id.vip_id_achieve_cover);
        if (cover != null) {
            RelativeLayout.LayoutParams coverLp = (RelativeLayout.LayoutParams) cover.getLayoutParams();
            coverLp.width = size;
            coverLp.height = size;
            cover.setImageDrawable(lockIcon);
        }
    }

    /* access modifiers changed from: private */
    public void setAchievements(List<VipAchievement> infoList) {
        Utils.log("setAchievements", new Object[0]);
        LinearLayout linearLayout = this.mAchievements;
        if (linearLayout != null) {
            int viewCount = linearLayout.getChildCount();
            if (!Utils.hasData((List<?>) infoList)) {
                if (viewCount > 0) {
                    Utils.log("setAchievements, no achievement, remove all views", new Object[0]);
                    this.mAchievements.removeAllViews();
                }
                showAchievement(false);
            } else if ((!isSameList(this.mAchievementList, infoList, this.mCmpVipAchievement)) || viewCount == 0) {
                Utils.log("setAchievements, set achievement list", new Object[0]);
                this.mAchievements.removeAllViews();
                showAchievement(true);
                addAchievementIconToLinearLayout(this.mAchievements, infoList, getAchievementIconSize());
                this.mAchievements.requestLayout();
            }
        }
        this.mAchievementList = infoList;
    }

    private static int getAchievementCount(List<VipAchievement> list) {
        if (list == null) {
            return 0;
        }
        return Math.min(list.size(), 4);
    }

    private void addAchievementIconToLinearLayout(LinearLayout container, List<VipAchievement> infoList, int size) {
        addAchievementIconToLinearLayout(container, infoList, this.mLockIcon, this.mShowModel, size, getWidth());
    }

    private static void addAchievementIconToLinearLayout(LinearLayout container, List<VipAchievement> infoList, Drawable lockIcon, int showModel, int size, int maxWidth) {
        int sideMargin;
        int count = getAchievementCount(infoList);
        int maxIndex = count - 1;
        Context ctx = container.getContext();
        if (showModel == 0) {
            sideMargin = ctx.getResources().getDimensionPixelOffset(R.dimen.vip_margin_4);
        } else {
            sideMargin = ((maxWidth - (ctx.getResources().getDimensionPixelOffset(R.dimen.vip_margin_8) * 2)) - (size * count)) / maxIndex;
        }
        int i = 0;
        while (i < count) {
            addAchievementIcon(container, infoList.get(i), showModel, size, i == maxIndex ? 0 : sideMargin);
            i++;
        }
        drawAchievementLock(container, lockIcon, size);
    }

    private int getAchievementIconSize() {
        return getAchievementIconSize(getContext(), this.mShowModel);
    }

    private static int getAchievementIconSize(Context ctx, int model) {
        return ctx.getResources().getDimensionPixelSize(BadgeIconSize[model]);
    }

    private String getBannerTypeName(long bannerId) {
        return "banner" + String.valueOf(bannerId);
    }

    /* access modifiers changed from: private */
    public void setBanners(List<VipBanner> bannerList) {
        if (this.mBanner != null) {
            if (!this.mShowBanner || !Utils.hasData((List<?>) bannerList)) {
                this.mBannerGroup.setVisibility(8);
            } else {
                this.mBannerGroup.setVisibility(0);
                int viewCount = this.mBanner.getChildCount();
                if (viewCount == 0 || !isSameList(this.mBannerList, bannerList, this.mCmpVipBanner)) {
                    setBannerView(bannerList, viewCount);
                }
                this.mBanner.requestLayout();
            }
        }
        this.mBannerList = bannerList;
    }

    private void setBannerView(List<VipBanner> bannerList, int viewCount) {
        int count = Math.min(2, bannerList.size());
        if (count > viewCount) {
            for (int i = 0; i < count - viewCount; i++) {
                View bannerView = inflate(getContext(), R.layout.vip_banner, (ViewGroup) null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
                lp.weight = 1.0f;
                this.mBanner.addView(bannerView, lp);
            }
        } else if (viewCount > count) {
            for (int i2 = 0; i2 < viewCount - count; i2++) {
                this.mBanner.removeViewAt(i2);
            }
        }
        for (int i3 = 0; i3 < count; i3++) {
            setBannerViewData(this.mBanner.getChildAt(i3), bannerList.get(i3));
        }
    }

    private void setBannerViewData(View bannerView, final VipBanner banner) {
        ImageView bannerIcon = (ImageView) bannerView.findViewById(R.id.vip_id_banner_icon);
        if (TextUtils.isEmpty(banner.icon) || !Utils.isStringUri(banner.icon)) {
            bannerIcon.setImageResource(R.drawable.vip_icon_chalice);
        } else {
            ImageDownloader.loadImage(getContext(), banner.icon, getBannerTypeName(banner.id), bannerIcon);
        }
        ((TextView) bannerView.findViewById(R.id.vip_id_banner_name)).setText(banner.name);
        TextView infoView = (TextView) bannerView.findViewById(R.id.vip_id_banner_info);
        if (!TextUtils.isEmpty(banner.info)) {
            infoView.setText(banner.info);
        } else {
            infoView.setVisibility(8);
        }
        Utils.log("setBannerViewData, banner = %s", banner);
        if (!TextUtils.isEmpty(banner.action)) {
            View.OnClickListener bannerClick = new View.OnClickListener() {
                public void onClick(View v) {
                    Utils.startActivity(VipPortraitView.this.getContext(), banner.action, (String) null, banner.extraParams);
                }
            };
            bannerView.setOnClickListener(new ClickListenerWrapper(STATISTIC_BANNER + banner.id, bannerClick));
        }
    }

    private void loadAvatarFile(Account account, AccountManager am) {
        Utils.log("loadAvatarFile, account = %s", account);
        ImageView imageView = this.mAvatar;
        if (imageView == null) {
            return;
        }
        if (account == null) {
            imageView.setImageResource(R.drawable.vip_default_avatar);
            return;
        }
        String avatarUrl = am.getUserData(account, VipConstants.ACCOUNT_AVATAR_URL);
        String fileName = am.getUserData(account, VipConstants.ACCOUNT_AVATAR_FILE_NAME);
        Utils.log("loadAvatarFile, avatarUrl = %s, fileName = %s", avatarUrl, fileName);
        if (!TextUtils.isEmpty(fileName)) {
            ImageDownloader.loadImage(getContext(), avatarUrl, fileName.replace(account.name, Utils.md5(account.name)), this.mAvatar, true);
            return;
        }
        this.mAvatar.setImageResource(R.drawable.vip_default_avatar);
    }

    private static void addAchievementIcon(LinearLayout container, VipAchievement info, int showModel, int size, int margin) {
        int i = 0;
        Utils.log("addAchievementIcon, info.badgeId = %d, info.name = %s, info.url = %s", Long.valueOf(info.id), info.name, info.url);
        Context ctx = container.getContext();
        View achievementView = inflate(ctx, R.layout.vip_achievement_icon, (ViewGroup) null);
        achievementView.setTag(info);
        ImageView iconView = (ImageView) achievementView.findViewById(R.id.vip_id_achieve_icon);
        RelativeLayout.LayoutParams iconLp = (RelativeLayout.LayoutParams) iconView.getLayoutParams();
        iconLp.width = size;
        iconLp.height = size;
        ImageDownloader.loadImage(ctx, info.url, String.valueOf(info.id), iconView);
        TextView nameView = (TextView) achievementView.findViewById(R.id.vip_id_achieve_name);
        if (showModel == 0) {
            i = 8;
        }
        nameView.setVisibility(i);
        nameView.setText(info.name);
        addViewToAchievements(container, achievementView, -1, margin);
    }

    private static void addViewToAchievements(LinearLayout container, View view, int size, int margin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.setMarginEnd(margin);
        if (size != -1) {
            lp.width = size;
            lp.height = size;
        }
        container.addView(view, lp);
    }
}
