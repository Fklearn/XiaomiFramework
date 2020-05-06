package com.miui.internal.vip.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.miui.internal.vip.VipConstants;
import com.miui.internal.vip.protocol.ErrResult;
import dalvik.system.DexFile;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import miui.accounts.ExtraAccountManager;
import miui.telephony.phonenumber.Prefix;

public class AuthHttpRequest {
    private static final String ACCOUNT_SDK = "com.xiaomi.accountsdk";
    private static final String ALPHA = "alpha";
    /* access modifiers changed from: private */
    public static final DexFile AccountSdkDf;
    private static final String CLS_AUTH_FAIL_EXCEPT = "com.xiaomi.accountsdk.request.AuthenticationFailureException";
    private static final String CLS_CRYPT_CODER = "com.xiaomi.accountsdk.utils.CryptCoder";
    private static final String CLS_EXTEND_AUTH_TOKEN = "com.xiaomi.accountsdk.account.data.ExtendedAuthToken";
    private static final String CLS_SECURE_REQUEST = "com.xiaomi.accountsdk.request.SecureRequest";
    private static final String CLS_STRING_CONTENT = "com.xiaomi.accountsdk.request.SimpleRequest$StringContent";
    private static final Class ClsAuthFailExcept;
    private static final Class ClsCryptCoder;
    private static final Class ClsExtendAuthToken;
    private static final Class ClsSecureRequest;
    private static final Class ClsStringContent;
    private static final String ERR_AUTH_TOKEN = "fail to get auth token";
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final int MAX_RETRY = 5;
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String PARAM_ANDROID_VER = "android_version";
    private static final String PARAM_COUNTRY = "country";
    private static final String PARAM_IMEI = "imei";
    private static final String PARAM_LANGUAGE = "language";
    private static final String PARAM_MIUI_VERSION = "miui_version";
    private static final Class[] PARAM_NONE = new Class[0];
    private static final String PARAM_PRODUCT = "product";
    private static final Class[] PARAM_REQUEST = {String.class, Map.class, Map.class, Boolean.TYPE, String.class, ClsCryptCoder, Integer.class};
    private static final String PARAM_REQUEST_ID = "requestId";
    private static final String PARAM_REQUEST_TIME = "requestTime";
    private static final Class[] PARAM_SINGLE_STRING = {String.class};
    private static final String PARAM_VERSION = "version";
    private static final String SERVER_ALPHA = "api-alpha.vip.miui.com";
    private static final String SERVER_DEV = "api.vip.miui.com";
    private static final String SERVER_TONGJI = "api.vip.miui.com";
    private static final String SERVER_TONGJI_ALPHA = "tongji-alpha.vip.miui.com";
    private static final String SID = "miui_vip";
    private static final String SID_A = "miui_vip_a";
    private static final int TIMEOUT = 10000;
    public static final String TYPE_DATA = "data";
    public static final String TYPE_TONGJI = "tongji";
    private static final AccountSdkLoader sAccountSdkLoader = new AccountSdkLoader();
    private static boolean sClassLoaded;
    public final String command;
    public final boolean isNormal;
    public final String method;
    public final Map<String, String> params;
    public final String type;

    static {
        sClassLoaded = false;
        Class clsExtendToken = null;
        Class clsAuthFail = null;
        Class clsSecureReq = null;
        Class clsStrContent = null;
        Class clsCoder = null;
        DexFile df = null;
        try {
            df = new DexFile("system/framework/cloud-common.jar");
        } catch (Exception e) {
            Utils.logW("failed to open cloud-common, %s", e);
        }
        AccountSdkDf = df;
        try {
            clsExtendToken = df.loadClass(CLS_EXTEND_AUTH_TOKEN, sAccountSdkLoader);
            clsAuthFail = df.loadClass(CLS_AUTH_FAIL_EXCEPT, sAccountSdkLoader);
            clsSecureReq = df.loadClass(CLS_SECURE_REQUEST, sAccountSdkLoader);
            clsCoder = df.loadClass(CLS_CRYPT_CODER, sAccountSdkLoader);
            clsStrContent = df.loadClass(CLS_STRING_CONTENT, sAccountSdkLoader);
            sClassLoaded = true;
        } catch (Exception e2) {
            Utils.logW("failed to load class, %s", e2);
        }
        ClsExtendAuthToken = clsExtendToken;
        ClsAuthFailExcept = clsAuthFail;
        ClsSecureRequest = clsSecureReq;
        ClsStringContent = clsStrContent;
        ClsCryptCoder = clsCoder;
    }

    private static class AccountSdkLoader extends ClassLoader {
        private AccountSdkLoader() {
        }

        /* access modifiers changed from: protected */
        public Class<?> findClass(String className) throws ClassNotFoundException {
            return super.findClass(className);
        }

        public Class<?> loadClass(String className) throws ClassNotFoundException {
            if (AuthHttpRequest.AccountSdkDf == null || !className.startsWith(AuthHttpRequest.ACCOUNT_SDK)) {
                return super.loadClass(className);
            }
            return AuthHttpRequest.AccountSdkDf.loadClass(className, this);
        }
    }

    static class ExtendToken {
        String authToken;
        String security;

        ExtendToken() {
        }

        public String toString() {
            return "ExtendToken{authToken='" + this.authToken + '\'' + ", security='" + this.security + '\'' + '}';
        }
    }

    private String getServer(String type2) {
        String server;
        boolean isAlpha = isXiaomiVipAlpha();
        String str = "api.vip.miui.com";
        if (TYPE_TONGJI.equals(type2)) {
            if (isAlpha) {
                str = SERVER_TONGJI_ALPHA;
            }
            server = str;
        } else {
            if (isAlpha) {
                str = SERVER_ALPHA;
            }
            server = str;
        }
        if (TextUtils.isEmpty(server)) {
            return server;
        }
        if (this.isNormal) {
            return HTTP + server;
        }
        return HTTPS + server;
    }

    public AuthHttpRequest(String cmd, String... pms) {
        this(cmd, "data", METHOD_GET, false, pms);
    }

    public AuthHttpRequest(String cmd, String type2, String method2, boolean isNormal2, String... pms) {
        this.command = cmd;
        this.type = type2;
        this.method = method2;
        this.isNormal = isNormal2;
        this.params = new ArrayMap();
        int i = 0;
        while (pms != null && i < pms.length) {
            int next = i + 1;
            if (next < pms.length) {
                this.params.put(pms[i], pms[next]);
            }
            i = next + 1;
        }
    }

    public String queryAsString() {
        if (sClassLoaded) {
            try {
                String ret = queryWithAccount(getServer(this.type) + this.command, 10000);
                ErrResult error = (ErrResult) JsonParser.parseJsonObject(ret, ErrResult.class);
                if (error == null || error.code <= 0) {
                    return ret;
                }
                Utils.logW("queryAsString failed, %s", error.err);
                return null;
            } catch (Exception e) {
                Utils.logW("queryAsString failed, %s", e);
            }
        }
        return null;
    }

    private static String getAuthToken(AccountManager am, Account account, String sid) throws Exception {
        if (account == null || TextUtils.isEmpty(sid)) {
            Utils.logW("account = %s, sid = %s, failed to get auth token", account, sid);
            return null;
        } else if (TextUtils.isDigitsOnly(account.name)) {
            try {
                return am.getAuthToken(account, sid, (Bundle) null, true, (AccountManagerCallback) null, (Handler) null).getResult().getString("authtoken");
            } catch (OperationCanceledException e) {
                Utils.logW("getAuthToken %s", e);
                return null;
            } catch (AuthenticatorException e2) {
                Utils.logW("getAuthToken %s", e2);
                return null;
            } catch (IOException e3) {
                Utils.logW("getAuthToken %s", e3);
                return null;
            }
        } else {
            throw new IllegalArgumentException("userId is not only digits");
        }
    }

    private String getSid() {
        return isXiaomiVipAlpha() ? SID_A : SID;
    }

    private static boolean isXiaomiVipAlpha() {
        PackageInfo vipPackage = Utils.getPackageInfo(VipConstants.VIP_PACKAGE);
        if (vipPackage != null) {
            return vipPackage.versionName.contains(ALPHA);
        }
        return false;
    }

    private static String getXiaomiVipVersion() {
        PackageInfo vipPackage = Utils.getPackageInfo(VipConstants.VIP_PACKAGE);
        if (vipPackage != null) {
            return vipPackage.versionName;
        }
        return Prefix.EMPTY;
    }

    private String queryWithAccount(String url, int timeoutMillis) throws Exception {
        Account account = ExtraAccountManager.getXiaomiAccount(Utils.getContext());
        if (account == null) {
            Utils.log("AuthHttpRequest.queryWithAccount return null for there is no valid account", new Object[0]);
            return null;
        }
        AccountManager am = AccountManager.get(Utils.getContext());
        int retryTimes = 0 + 1;
        String authToken = getAuthToken(am, account, getSid());
        if (authToken == null) {
            return null;
        }
        try {
            ExtendToken token = getExtendedAuthToken(authToken);
            return requestWithUser(url, getCookies(am, account, token.authToken), token.security, timeoutMillis);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause != null && ReflectionUtils.isTypeMatched(cause.getClass(), ClsAuthFailExcept)) {
                if (retryTimes == 5 || TextUtils.isEmpty(authToken)) {
                    throw e;
                }
                am.invalidateAuthToken(account.type, authToken);
            }
            return null;
        }
    }

    private Map<String, String> getCookies(AccountManager am, Account account, String token) {
        String encryptedUserId = am.getUserData(account, ExtraAccountManager.KEY_ENCRYPTED_USER_ID);
        Map<String, String> map = new ArrayMap<>();
        map.put("cUserId", encryptedUserId);
        map.put("serviceToken", token);
        return map;
    }

    private ExtendToken getExtendedAuthToken(String authToken) throws Exception {
        ExtendToken extendToken = new ExtendToken();
        Object token = ReflectionUtils.invokeMethod((Object) null, ClsExtendAuthToken, "parse", PARAM_SINGLE_STRING, authToken);
        extendToken.security = (String) ReflectionUtils.getField(token, ClsExtendAuthToken, "security");
        extendToken.authToken = (String) ReflectionUtils.getField(token, ClsExtendAuthToken, "authToken");
        if (token != null && extendToken.authToken != null && extendToken.security != null) {
            return extendToken;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ERR_AUTH_TOKEN);
        sb.append(authToken == null ? "/null authToken" : "parse authToken failed");
        throw new IllegalStateException(sb.toString());
    }

    private String requestWithUser(String url, Map<String, String> cookies, String security, int timeoutMillis) throws Exception {
        Object stringContent;
        addAdditionalParams(this.params);
        if (METHOD_GET.equalsIgnoreCase(this.method)) {
            stringContent = ReflectionUtils.invokeMethod((Object) null, ClsSecureRequest, "getAsString", PARAM_REQUEST, url, this.params, cookies, true, security, null, Integer.valueOf(timeoutMillis));
        } else {
            stringContent = ReflectionUtils.invokeMethod((Object) null, ClsSecureRequest, "postAsString", PARAM_REQUEST, url, this.params, cookies, true, security, null, Integer.valueOf(timeoutMillis));
        }
        if (stringContent != null) {
            return (String) ReflectionUtils.invokeMethod(stringContent, ClsStringContent, "getBody", PARAM_NONE, new Object[0]);
        }
        return null;
    }

    private static Map<String, String> addAdditionalParams(Map<String, String> params2) {
        Map<String, String> ret = params2;
        if (ret == null) {
            ret = new ArrayMap<>();
        }
        ret.put(PARAM_REQUEST_ID, getRequestId());
        ret.put(PARAM_REQUEST_TIME, String.valueOf(System.currentTimeMillis()));
        ret.put(PARAM_MIUI_VERSION, DeviceHelper.MIUI_VERSION);
        ret.put("version", getXiaomiVipVersion());
        ret.put(PARAM_PRODUCT, DeviceHelper.PRODUCT);
        ret.put("country", Locale.getDefault().getCountry());
        ret.put(PARAM_LANGUAGE, Locale.getDefault().getLanguage());
        ret.put(PARAM_IMEI, DeviceHelper.getDeviceId());
        ret.put(PARAM_ANDROID_VER, Build.VERSION.RELEASE);
        return ret;
    }

    private static String getRequestId() {
        return UUID.randomUUID().toString().substring(0, 16);
    }
}
