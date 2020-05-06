package miui.vip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.miui.internal.vip.VipConstants;
import com.miui.internal.vip.VipResInputStream;
import com.miui.internal.vip.utils.ImageDownloader;
import com.miui.internal.vip.utils.JsonParser;
import com.miui.internal.vip.utils.RunnableHelper;
import com.miui.internal.vip.utils.Utils;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import miui.accounts.ExtraAccountManager;
import miui.telephony.phonenumber.Prefix;
import miui.util.AppConstants;

public class VipWebClient extends WebViewClient {
    static final String ACCOUNT_AVATAR = "account_avatar/";
    static final String ACCOUNT_CALLBACK = "accountCallback";
    static final String ACCOUNT_FIELD = "account";
    static final String ACHIEVEMENT_CALLBACK = "achievementCallback";
    static final String ARRAY_BEGIN = "[";
    static final String ARRAY_END = "]";
    static final String COMMA = ",";
    static final String CONNECT_SERVICE = "connect_service";
    static final String DEFAULT_AVATAR = "http://request_vip_icon/default_photo";
    static final int DISPLAY_ACHIEVEMENTS_COUNT = 4;
    static final String JS_ACCOUNT = "{id:%s, name:'%s', avatarUrl:'%s'}";
    static final String JS_ACHIEVEMENT = "{id:%d, name:'%s', url:'%s', isOwned:%s}";
    static final String JS_INIT = "if (!window.XiaomiVipClient) {   window.XiaomiVipClient = {ICON_ACHIEVEMENT_LOCK: 'http://request_vip_icon/achievement_lock',vipUser: %s,achievements: %s,account: %s,setVipInfoCallback: function(callback) {   this.vipCallback = callback;},setAchievementCallback: function(callback) {   this.achievementCallback = callback;},setAccountCallback: function(callback) {   this.accountCallback = callback;},openVipTaskView: function() {   this.loadUrl('http://vip_view/vip_view_task');},openVipLevelView: function() {   this.loadUrl('http://vip_view/vip_view_level');},openVipAchievementView: function() {   this.loadUrl('http://vip_view/vip_view_achievements');},openUserDetailView: function() {   this.loadUrl('http://vip_view/user_detail');},loadUrl: function(url) {    var xhr = new XMLHttpRequest();    xhr.open('GET', url, true);    xhr.send();}};} else {   console.log('XiaomiVipCient is already initialized');}XiaomiVipClient.loadUrl('http://vip_view/connect_service?refresh=' + (!XiaomiVipClient.vipUser));console.log('initialization of XiaomiVipCient is completed');";
    static final String JS_UPDATE = "(function(){   var funcName = '%s';   var vName = '%s';   var args = %s;   if (window.XiaomiVipClient) {       XiaomiVipClient[vName] = args;       if (typeof XiaomiVipClient[funcName] == 'function') {           console.log('VipWebClient invokes ' + funcName);           XiaomiVipClient[funcName](args);       }   }})();";
    static final String JS_USER = "{id:%d, level:%d, badgeUrl:'%s'}";
    static final String JS_VIP_CLIENT = "window.XiaomiVipClient = {ICON_ACHIEVEMENT_LOCK: 'http://request_vip_icon/achievement_lock',vipUser: %s,achievements: %s,account: %s,setVipInfoCallback: function(callback) {   this.vipCallback = callback;},setAchievementCallback: function(callback) {   this.achievementCallback = callback;},setAccountCallback: function(callback) {   this.accountCallback = callback;},openVipTaskView: function() {   this.loadUrl('http://vip_view/vip_view_task');},openVipLevelView: function() {   this.loadUrl('http://vip_view/vip_view_level');},openVipAchievementView: function() {   this.loadUrl('http://vip_view/vip_view_achievements');},openUserDetailView: function() {   this.loadUrl('http://vip_view/user_detail');},loadUrl: function(url) {    var xhr = new XMLHttpRequest();    xhr.open('GET', url, true);    xhr.send();}};";
    static final String NULL_STR = "null";
    static final String PARAM_REFRESH = "refresh";
    static final String Q_MARK = "?";
    static final String SCHEMA_VIP_ICON = "http://request_vip_icon/";
    static final String SCHEMA_VIP_VIEW = "http://vip_view/";
    static final String VALUE_TRUE = "true";
    static final String VIEW_USER_DETAIL = "user_detail";
    static final String VIP_ACHIEVEMENTS_FIELD = "achievements";
    static final String VIP_CALLBACK = "vipCallback";
    static final String VIP_USER_FIELD = "vipUser";
    static final String VIP_VIEW_ACHIEVEMENTS = "vip_view_achievements";
    static final String VIP_VIEW_LEVEL = "vip_view_level";
    static final String VIP_VIEW_TASK = "vip_view_task";
    AccountInfo mAccount;
    ImageDownloader.FileDownloadListener mAvatarListener = new ImageDownloader.FileDownloadListener(new ImageDownloader.OnFileDownload() {
        public void onDownload(String filePath) {
            if (!TextUtils.isEmpty(filePath)) {
                String id = null;
                String userName = null;
                String iconName = null;
                synchronized (VipWebClient.this) {
                    if (VipWebClient.this.mAccount != null) {
                        id = VipWebClient.this.mAccount.id;
                        userName = VipWebClient.this.mAccount.userName;
                        iconName = Utils.getName(VipWebClient.this.mAccount.avatarUrl);
                    }
                }
                if (id != null) {
                    VipWebClient vipWebClient = VipWebClient.this;
                    vipWebClient.invokeJsAccountUpdate(id, userName, vipWebClient.getAccountAvatarWebUrl(iconName));
                }
            }
        }
    });
    volatile List<VipAchievement> mCachedAchievements;
    final CopyOnWriteArraySet<String> mLoadingJsSet = new CopyOnWriteArraySet<>();
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Utils.log("VipWebClient.mReceiver.onReceive, action = %s", action);
            if (TextUtils.equals(action, "android.accounts.LOGIN_ACCOUNTS_POST_CHANGED")) {
                boolean hasAccount = Utils.hasAccount();
                Object[] objArr = new Object[1];
                objArr[0] = hasAccount ? "remove" : "add";
                Utils.log("VipWebClient.mReceiver.onReceive, login accounts changed, %s", objArr);
                if (!hasAccount) {
                    VipWebClient.this.batchNotify((VipUserInfo) null, (List<VipAchievement>) null);
                    VipService.instance().disconnect(VipWebClient.this.mVipCallback);
                    return;
                }
                VipService.instance().connect(VipWebClient.this.mVipCallback);
                return;
            }
            VipWebClient.this.notifyAccountUpdate();
        }
    };
    volatile String mStrAccount;
    volatile String mStrUser;
    volatile VipUserInfo mUser;
    QueryCallback mVipCallback = new QueryCallback(16) {
        public void onConnected(boolean serviceAvailable, VipUserInfo user, List<VipAchievement> achievementList) {
            Utils.log("VipWebClient.interceptRequest, onConnected, notify", new Object[0]);
            VipWebClient.this.batchNotify(user, achievementList);
        }

        public void onUserInfo(int code, VipUserInfo user, String errMsg) {
            if (code == 0) {
                VipWebClient.this.notifyVipUserUpdate(user);
            }
        }

        public void onAchievements(int code, List<VipAchievement> list, String errMsg) {
            if (code == 0) {
                VipWebClient.this.notifyAchievementsUpdate(list);
            }
        }
    };
    AtomicReference<WebView> mWebView = new AtomicReference<>();

    static class AccountInfo {
        public String avatarFileName;
        public String avatarUrl;
        public String id;
        public String userName;

        AccountInfo() {
        }
    }

    static class UrlParameters {
        HashMap<String, String> params = new HashMap<>();
        String path;

        UrlParameters() {
        }

        public String toString() {
            return "UrlParameters{path='" + this.path + '\'' + ", params=" + mapToString(this.params) + '}';
        }

        private StringBuilder mapToString(HashMap<String, String> map) {
            StringBuilder str = new StringBuilder("{");
            int initLength = str.length();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (str.length() > initLength) {
                    str.append(", ");
                }
                str.append(entry.getKey());
                str.append(":");
                str.append(entry.getValue());
            }
            str.append("}");
            return str;
        }
    }

    /* access modifiers changed from: private */
    public void batchNotify(VipUserInfo user, List<VipAchievement> achievementList) {
        notifyVipUserUpdate(user);
        notifyAchievementsUpdate(achievementList);
        notifyAccountUpdate();
    }

    public void init(WebView webView) {
        getContext().registerReceiver(this.mReceiver, Utils.ACCOUNT_CHANGE_FILTER);
        connectService();
        initWithData(webView);
    }

    public void clear() {
        this.mWebView.set((Object) null);
        this.mUser = null;
        this.mCachedAchievements = null;
        this.mStrUser = null;
        this.mStrAccount = null;
        VipService.instance().disconnect(this.mVipCallback);
        try {
            getContext().unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
            Utils.log("exception happened on unregisterReceiver, %s", e);
        }
    }

    public boolean shouldIntercept(String url) {
        return url.startsWith(SCHEMA_VIP_ICON) || url.startsWith(SCHEMA_VIP_VIEW);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Math.abs(VipConstants.ACTION_USER_DETAIL.hashCode())) {
            Utils.log("onActivityResult for user detail activity", new Object[0]);
            notifyAccountUpdate();
        }
    }

    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        try {
            WebResourceResponse res = interceptRequest(view, url);
            if (res == null || TextUtils.isEmpty(res.getMimeType())) {
                return null;
            }
            return res;
        } catch (Exception e) {
            Utils.logW("VipWebClient.shouldInterceptRequest, url = %s, exception: %s", url, e);
        }
        return null;
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Utils.log("onPageStarted, url = %s", url);
        initWithData(view);
    }

    private void initWithData(WebView view) {
        WebView oldView = this.mWebView.get();
        if (view != oldView) {
            this.mWebView.compareAndSet(oldView, view);
        }
        Utils.log("VipWebClient.initWithData, init XiaomiVipClient", new Object[0]);
        Object[] objArr = new Object[3];
        boolean isEmpty = TextUtils.isEmpty(this.mStrUser);
        String str = NULL_STR;
        objArr[0] = isEmpty ? str : this.mStrUser;
        objArr[1] = this.mCachedAchievements == null ? str : achievementToJs(this.mCachedAchievements);
        if (!TextUtils.isEmpty(this.mStrAccount)) {
            str = this.mStrAccount;
        }
        objArr[2] = str;
        loadJs(format(JS_INIT, objArr));
    }

    private WebResourceResponse interceptRequest(WebView view, String url) {
        InputStream is;
        Utils.log("interceptRequest, url = %s", url);
        if (url.startsWith(SCHEMA_VIP_ICON)) {
            String iconName = url.substring(SCHEMA_VIP_ICON.length());
            if (iconName.startsWith(ACCOUNT_AVATAR)) {
                is = loadAccountAvatar();
            } else {
                is = new VipResInputStream(iconName, this.mUser != null ? this.mUser.level : 0);
            }
            Utils.log("VipWebClient.shouldInterceptRequest, vip_icon, is = %s", is);
            if (is != null) {
                return new WebResourceResponse("image/*", "base64", is);
            }
        } else if (url.startsWith(SCHEMA_VIP_VIEW)) {
            UrlParameters cmd = parseUrl(url);
            Utils.log("VipWebClient.interceptRequest, cmd = %s", cmd);
            if (TextUtils.equals(CONNECT_SERVICE, cmd.path)) {
                boolean needRefresh = isValueTrue(cmd, PARAM_REFRESH);
                Utils.log("VipWebClient.interceptRequest, needRefresh is %s", Boolean.valueOf(needRefresh));
                if (needRefresh) {
                    Object[] objArr = new Object[2];
                    objArr[0] = this.mUser;
                    objArr[1] = this.mCachedAchievements != null ? Integer.valueOf(this.mCachedAchievements.size()) : "-1";
                    Utils.log("VipWebClient.interceptRequest, do batchNotify, mUser = %s, mCachedAchievements.size = %d", objArr);
                    batchNotify(this.mUser, this.mCachedAchievements);
                }
            } else {
                startAccountActivity(view.getContext(), cmd);
            }
        }
        if (shouldIntercept(url)) {
            return new WebResourceResponse(Prefix.EMPTY, Prefix.EMPTY, (InputStream) null);
        }
        return null;
    }

    private void startAccountActivity(Context ctx, UrlParameters cmd) {
        String action = getActionFromPath(cmd.path);
        Utils.log("VipWebClient.interceptRequest, handleVipAction, path = %s, action = %s", cmd.path, action);
        if (!TextUtils.isEmpty(action)) {
            boolean forResult = false;
            String packageName = VipConstants.VIP_PACKAGE;
            if (TextUtils.equals(action, VipConstants.ACTION_USER_DETAIL)) {
                packageName = "com.xiaomi.account";
                forResult = true;
                if (!Utils.hasAccount()) {
                    action = VipConstants.ACTION_ACCOUNT_WELCOME;
                }
            }
            Utils.startActivity(ctx, action, packageName, forResult);
        }
    }

    private boolean isValueTrue(UrlParameters up, String paramName) {
        return VALUE_TRUE.equalsIgnoreCase(up.params.get(paramName));
    }

    private UrlParameters parseUrl(String url) {
        UrlParameters params = new UrlParameters();
        int paramStart = url.indexOf(Q_MARK);
        params.path = url.substring(SCHEMA_VIP_VIEW.length(), paramStart > 0 ? paramStart : url.length());
        if (paramStart > 0) {
            for (String pair : url.substring(paramStart + 1).split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    params.params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }

    private boolean connectService() {
        Utils.log("VipWebClient.connectService begin, mStrUser = %s", this.mStrUser);
        if (!VipService.instance().isConnected() || TextUtils.isEmpty(this.mStrUser)) {
            Utils.log("VipWebClient.connectService, do connection", new Object[0]);
            VipService.instance().connect(this.mVipCallback);
            return true;
        }
        Utils.log("VipWebClient.connectService, no need to connect", new Object[0]);
        return false;
    }

    private void loadJs(final String js) {
        if (this.mWebView != null && this.mLoadingJsSet.add(js)) {
            if (Utils.isInMainThread()) {
                doLoadJs(js);
            } else {
                RunnableHelper.runInUIThread(new Runnable() {
                    public void run() {
                        VipWebClient.this.doLoadJs(js);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void doLoadJs(String js) {
        WebView view = this.mWebView.get();
        if (view != null) {
            view.loadUrl("javascript: " + js);
        }
        this.mLoadingJsSet.remove(js);
    }

    /* access modifiers changed from: private */
    public void notifyVipUserUpdate(VipUserInfo user) {
        String js;
        if (user != null) {
            this.mUser = user;
            js = vipUserToJs(user);
        } else {
            js = format(JS_USER, 0, 0, Prefix.EMPTY);
        }
        Utils.log("notifyVipUserUpdate, mStrUser = %s, js = %s", this.mStrUser, js);
        if (!TextUtils.equals(this.mStrUser, js)) {
            this.mStrUser = js;
            loadJs(format(JS_UPDATE, VIP_CALLBACK, VIP_USER_FIELD, js));
        }
    }

    /* access modifiers changed from: private */
    public void notifyAchievementsUpdate(List<VipAchievement> list) {
        String js;
        if (!isSameAchievementList(this.mCachedAchievements, list)) {
            this.mCachedAchievements = list;
            if (list == null || list.isEmpty()) {
                js = JsonParser.EMPTY_ARRAY;
            } else {
                js = achievementToJs(list);
            }
            Utils.log("notifyAchievementsUpdate, js = %s", js);
            loadJs(format(JS_UPDATE, ACHIEVEMENT_CALLBACK, VIP_ACHIEVEMENTS_FIELD, js));
        }
    }

    private boolean isSameAchievementList(List<VipAchievement> left, List<VipAchievement> right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null || left.size() != right.size()) {
            return false;
        }
        int count = left.size();
        for (int i = 0; i < count; i++) {
            VipAchievement la = left.get(i);
            VipAchievement ra = right.get(i);
            if (la.id != ra.id || la.isOwned != ra.isOwned || !TextUtils.equals(la.name, ra.name) || !TextUtils.equals(Utils.getName(la.url), Utils.getName(ra.url))) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void notifyAccountUpdate() {
        String userName;
        String avatarUrl;
        String avatarFileName;
        Account account = ExtraAccountManager.getXiaomiAccount(getContext());
        AccountManager am = AccountManager.get(getContext());
        if (account != null) {
            synchronized (this) {
                if (this.mAccount == null) {
                    this.mAccount = new AccountInfo();
                }
                this.mAccount.id = account.name;
                AccountInfo accountInfo = this.mAccount;
                String userData = am.getUserData(account, VipConstants.ACCOUNT_USER_NAME);
                userName = userData;
                accountInfo.userName = userData;
                AccountInfo accountInfo2 = this.mAccount;
                String userData2 = am.getUserData(account, VipConstants.ACCOUNT_AVATAR_URL);
                avatarUrl = userData2;
                accountInfo2.avatarUrl = userData2;
                AccountInfo accountInfo3 = this.mAccount;
                String userData3 = am.getUserData(account, VipConstants.ACCOUNT_AVATAR_FILE_NAME);
                avatarFileName = userData3;
                accountInfo3.avatarFileName = userData3;
            }
            String iconWebUrl = Prefix.EMPTY;
            if (!TextUtils.isEmpty(avatarUrl)) {
                String iconName = Utils.getName(avatarUrl);
                if (!TextUtils.isEmpty(iconName)) {
                    iconWebUrl = getAccountAvatarWebUrl(iconName);
                }
            }
            boolean isWebUrlEmpty = TextUtils.isEmpty(iconWebUrl);
            if (!isWebUrlEmpty) {
                ImageDownloader.loadImage(getContext(), avatarUrl, avatarFileName, this.mAvatarListener);
            }
            if (!TextUtils.isEmpty(userName)) {
                invokeJsAccountUpdate(account.name, userName, isWebUrlEmpty ? DEFAULT_AVATAR : iconWebUrl);
                return;
            }
            return;
        }
        synchronized (this) {
            this.mAccount = null;
        }
        invokeJsAccountUpdate("0", Prefix.EMPTY, DEFAULT_AVATAR);
    }

    private InputStream loadAccountAvatar() {
        String url = Prefix.EMPTY;
        String dirName = Prefix.EMPTY;
        synchronized (this) {
            if (this.mAccount != null) {
                url = this.mAccount.avatarUrl;
                dirName = this.mAccount.avatarFileName;
            }
        }
        Bitmap bmp = ImageDownloader.loadImageFile(getContext(), url, dirName, this.mAvatarListener);
        if (bmp != null) {
            return Utils.bitmapToStream(Utils.createPhoto(bmp));
        }
        return null;
    }

    /* access modifiers changed from: private */
    public String getAccountAvatarWebUrl(String iconName) {
        return "http://request_vip_icon/account_avatar/" + iconName;
    }

    /* access modifiers changed from: private */
    public void invokeJsAccountUpdate(String id, String userName, String avatarUrl) {
        String js = format(JS_ACCOUNT, id, userName, avatarUrl);
        Utils.log("invokeJsAccountUpdate, mStrAccount = %s, js = %s", this.mStrAccount, js);
        if (!TextUtils.equals(js, this.mStrAccount)) {
            this.mStrAccount = js;
            loadJs(format(JS_UPDATE, ACCOUNT_CALLBACK, "account", js));
        }
    }

    private String vipUserToJs(VipUserInfo user) {
        return format(JS_USER, Integer.valueOf(user.userId), Integer.valueOf(user.level), "http://request_vip_icon/level_icon");
    }

    private String achievementToJs(List<VipAchievement> list) {
        StringBuilder sbr = new StringBuilder(ARRAY_BEGIN);
        int initLength = sbr.length();
        if (list != null) {
            int count = Math.min(4, list.size());
            for (int i = 0; i < count; i++) {
                VipAchievement achievement = list.get(i);
                if (sbr.length() > initLength) {
                    sbr.append(COMMA);
                }
                sbr.append(format(JS_ACHIEVEMENT, Long.valueOf(achievement.id), achievement.name, achievement.url, String.valueOf(achievement.isOwned)));
            }
        }
        sbr.append(ARRAY_END);
        return sbr.toString();
    }

    private String getActionFromPath(String path) {
        if (TextUtils.equals(path, VIP_VIEW_LEVEL)) {
            return "com.xiaomi.vip.action.VIP_LEVEL_LIST";
        }
        if (TextUtils.equals(path, VIP_VIEW_TASK)) {
            return "com.xiaomi.vip.action.VIP_TASK_LIST";
        }
        if (TextUtils.equals(path, VIP_VIEW_ACHIEVEMENTS)) {
            return "com.xiaomi.vip.action.VIP_ACHIEVEMENT_LIST";
        }
        if (TextUtils.equals(path, VIEW_USER_DETAIL)) {
            return VipConstants.ACTION_USER_DETAIL;
        }
        return null;
    }

    private Context getContext() {
        return AppConstants.getCurrentApplication().getApplicationContext();
    }

    private String format(String fmt, Object... args) {
        return String.format(Locale.ENGLISH, fmt, args);
    }
}
