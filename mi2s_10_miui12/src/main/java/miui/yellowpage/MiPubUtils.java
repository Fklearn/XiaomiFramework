package miui.yellowpage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.MiuiIntent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import miui.payment.PaymentManager;
import miui.telephony.phonenumber.Prefix;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MiPubUtils {
    private static final int COLUMN_THUMBNAIL_URL = 1;
    private static final int COLUMN_YELLOWPAGE_NAME = 2;
    private static final int COLUMN_YID = 0;
    private static final String EXTRA_MULTI_MODULE_ENTRY_RAW = "com.miui.yellowpage.extra.MULTI_MODULE_ENTRY_RAW";
    private static final String FOLLOW_CONFIRM_IN_YP_DETAIL = "follow_confirm_in_yp_detail";
    private static final String FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL = "follow_has_confirmed_in_yp_detail";
    private static final String MENU_HAS_BEEN_FIRSTLY_READ = "pref_menu_has_been_firstly_read";
    private static final String MENU_READ_IN_SMS_CONVERSATION = "pref_menu_read_in_sms_conversation";
    private static final String MIPUB_DEVICE_ID = "pref_mipub_random_device_id";
    private static final String RANDOM_BASE_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String TAG = "MipubUtils";
    private static final Pattern XM_ACCOUNT_PATTERN = Pattern.compile(".*@xiaomi.com(/.*)?");
    private static final String[] YELLOWPAGE_PROJECTION = {"yid", YellowPageContract.PhoneLookup.THUMBNAIL_URL, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME};

    public static class NetworkAccessDeniedException extends Exception {
    }

    private MiPubUtils() {
    }

    public static boolean isServiceNumber(Context context, String number) {
        if (!isServiceNumber(number) && !isXiaomiJID(number)) {
            return false;
        }
        return true;
    }

    private static boolean isServiceNumber(String address) {
        try {
            Method method = Class.forName("miui.provider.ExtraTelephony").getDeclaredMethod("isServiceNumber", new Class[]{String.class});
            method.setAccessible(true);
            return ((Boolean) method.invoke((Object) null, new Object[]{address})).booleanValue();
        } catch (Exception e) {
            Log.w(TAG, "invoke ExtraTelephony.isServiceNumber failed", e);
            return false;
        }
    }

    @Deprecated
    public static YellowPageMipub getMipub(Context context, String mipubId) {
        Cursor c;
        Uri uri = Uri.withAppendedPath(YellowPageContract.MiPub.CONTENT_URI_YELLOWPAGE, Uri.encode(mipubId));
        if (YellowPageUtils.isContentProviderInstalled(context, uri) && (c = context.getContentResolver().query(uri, YELLOWPAGE_PROJECTION, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (c.moveToFirst()) {
                    return new YellowPageMipub(mipubId, c.getString(2), c.getLong(0), c.getString(1));
                }
                c.close();
            } finally {
                c.close();
            }
        }
        return null;
    }

    public static boolean isXiaomiAccount(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        return XM_ACCOUNT_PATTERN.matcher(address).matches();
    }

    private static boolean isXiaomiJID(String address) {
        if (!isXiaomiAccount(address)) {
            return false;
        }
        return isNumeric(trimDomainSuffix(address));
    }

    private static boolean isNumeric(String account) {
        return Pattern.compile("[0-9]*").matcher(account).matches();
    }

    public static String trimDomainSuffix(String account) {
        if (TextUtils.isEmpty(account)) {
            return null;
        }
        int index = account.indexOf("@");
        if (index > 0) {
            return account.substring(0, index);
        }
        return account;
    }

    public static YellowPage getLocalYellowPage(Context context, String miId) {
        Cursor cursor;
        if (!TextUtils.isEmpty(miId) && YellowPageUtils.isContentProviderInstalled(context, YellowPageContract.YellowPage.CONTENT_URI) && (cursor = context.getContentResolver().query(YellowPageContract.YellowPage.CONTENT_URI, new String[]{"content"}, "miid=?", new String[]{String.valueOf(miId)}, (String) null)) != null) {
            try {
                if (cursor.moveToFirst()) {
                    return YellowPage.fromJson(cursor.getString(0));
                }
                cursor.close();
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    @Deprecated
    public static YellowPage getYellowPageThrowException(Context context, String miId) throws NetworkAccessDeniedException {
        YellowPage yellowPage = getLocalYellowPage(context, miId);
        if (yellowPage == null) {
            return getCloudYellowPageThrowException(context, miId);
        }
        return yellowPage;
    }

    public static YellowPage getCloudYellowPageThrowException(Context context, String miId) throws NetworkAccessDeniedException {
        Bundle bundle = InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_YELLOWPAGE_BY_MIID, miId);
        String status = bundle.getString("status");
        Log.d(TAG, "getYellowPage:The status is " + status);
        if (MiuiIntent.COMMAND_ICON_PANEL_OK.equals(status)) {
            return parseYellowPage(context, bundle.getString("data"));
        }
        if (!"network_access_denied".equals(status)) {
            return null;
        }
        throw new NetworkAccessDeniedException();
    }

    private static YellowPage parseYellowPage(Context context, String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(data);
            InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_INSERT, json.getString(Tag.TagYellowPage.KEY));
            return YellowPage.fromJson(json.getString(Tag.TagYellowPage.KEY));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static YellowPage getCloudYellowPage(Context context, String miId) {
        try {
            return getCloudYellowPageThrowException(context, miId);
        } catch (NetworkAccessDeniedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLocalYellowPageNavContent(Context context, int type) {
        String navContent = Prefix.EMPTY;
        if (!YellowPageUtils.isContentProviderInstalled(context, YellowPageContract.NavigationTab.CONTENT_URI)) {
            return null;
        }
        Cursor cursor = context.getContentResolver().query(YellowPageContract.NavigationTab.CONTENT_URI, new String[]{"value"}, "type=?", new String[]{String.valueOf(type)}, (String) null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    navContent = cursor.getString(cursor.getColumnIndex("value"));
                }
            } finally {
                cursor.close();
            }
        }
        return navContent;
    }

    public static String getRemoteYellowPageNavContent(Context context, int type) {
        String navContent = Prefix.EMPTY;
        Bundle bundle = InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_NAVIGATION_INFO, String.valueOf(type));
        if (MiuiIntent.COMMAND_ICON_PANEL_OK.equals(bundle.getString("status"))) {
            navContent = bundle.getString("data");
            if (!YellowPageUtils.isContentProviderInstalled(context, YellowPageContract.NavigationTab.CONTENT_URI)) {
                return navContent;
            }
            ContentValues cv = new ContentValues();
            cv.put("value", navContent);
            if (context.getContentResolver().update(YellowPageContract.NavigationTab.CONTENT_URI, cv, "type = ? ", new String[]{String.valueOf(type)}) < 1) {
                context.getContentResolver().insert(YellowPageContract.NavigationTab.CONTENT_URI, cv);
            }
        }
        return navContent;
    }

    public static String getSearchHint(JSONObject navJson) {
        return navJson.optString(Tag.TagServicesData.SEARCH_HINT);
    }

    public static boolean getShowPhoneRecharge(JSONObject navJson) {
        return navJson.optBoolean(Tag.TagServicesData.SHOW_PHONE_RECHARGE, false);
    }

    public static Map<String, Service> getRechargeMap(JSONObject navJson) {
        Map<String, Service> map = new HashMap<>();
        JSONObject dataRechargeJson = navJson.optJSONObject(Tag.TagServicesData.DATA_RECHARGE);
        if (dataRechargeJson != null) {
            map.put(Tag.TagServicesData.DATA_RECHARGE, Service.fromJson(dataRechargeJson));
        }
        JSONObject phoneRechargeJson = navJson.optJSONObject(Tag.TagServicesData.PHONE_RECHAGE);
        if (phoneRechargeJson != null) {
            map.put(Tag.TagServicesData.PHONE_RECHAGE, Service.fromJson(phoneRechargeJson));
        }
        return map;
    }

    public static List<ServicesDataEntry> getServicesList(JSONObject navJson) {
        List<ServicesDataEntry> list = new ArrayList<>();
        try {
            JSONArray navGroups = navJson.getJSONArray(Tag.TagServicesData.NAV_GROUPS);
            for (int i = 0; i < navGroups.length(); i++) {
                list.add(ServicesDataEntry.fromJson(navGroups.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, Prefix.EMPTY, e);
        }
        return list;
    }

    public static List<ServicesDataEntry> getServicesList(String navServices) {
        List<ServicesDataEntry> list = new ArrayList<>();
        try {
            JSONArray navGroups = new JSONObject(navServices).getJSONArray(Tag.TagServicesData.NAV_GROUPS);
            for (int i = 0; i < navGroups.length(); i++) {
                list.add(ServicesDataEntry.fromJson(navGroups.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, Prefix.EMPTY, e);
        }
        return list;
    }

    public static YellowPage getYellowPage(Context context, String miId) {
        YellowPage yellowPage = getLocalYellowPage(context, miId);
        if (yellowPage == null) {
            return getCloudYellowPage(context, miId);
        }
        return yellowPage;
    }

    @Deprecated
    public static HashMap<String, YellowPage> getLocalYellowPages(final Context context, final Set<String> miIds) {
        Cursor cursor;
        if (miIds == null || !YellowPageUtils.isContentProviderInstalled(context, YellowPageContract.YellowPage.CONTENT_URI) || (cursor = context.getContentResolver().query(YellowPageContract.YellowPage.CONTENT_URI, new String[]{"miid", "content"}, "miid IS NOT NULL", (String[]) null, (String) null)) == null) {
            return null;
        }
        HashMap<String, YellowPage> result = new HashMap<>();
        try {
            Log.d(TAG, "Query miids count " + miIds.size());
            while (cursor.moveToNext()) {
                String miId = cursor.getString(0);
                YellowPage yellowPage = YellowPage.fromJson(cursor.getString(1));
                if (miIds.contains(miId)) {
                    result.put(miId, yellowPage);
                    miIds.remove(miId);
                }
            }
            Log.d(TAG, "Query remote yellowpage by miid with cout " + miIds.size());
            if (miIds.size() > 0) {
                ThreadPool.execute(new Runnable() {
                    public void run() {
                        for (String miId : miIds) {
                            if (MiPubUtils.getCloudYellowPage(context, miId) == null) {
                                Log.e(MiPubUtils.TAG, "No yellowpage matched miid " + miId);
                            }
                        }
                    }
                });
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    private static SharedPreferences getMenuReadInSmsPref(Context context) {
        return context.getSharedPreferences(MENU_READ_IN_SMS_CONVERSATION, 0);
    }

    public static boolean isSmsMenuRead(Context context, long yellowPageId) {
        SharedPreferences pref = getMenuReadInSmsPref(context);
        return pref.getBoolean(String.valueOf(yellowPageId), pref.getBoolean(MENU_HAS_BEEN_FIRSTLY_READ, false));
    }

    public static void setSmsMenuRead(Context context, long yellowPageId, boolean read) {
        SharedPreferences pref = getMenuReadInSmsPref(context);
        pref.edit().putBoolean(String.valueOf(yellowPageId), read).commit();
        pref.edit().putBoolean(MENU_HAS_BEEN_FIRSTLY_READ, true).commit();
    }

    private static SharedPreferences getFollowConfirmPref(Context context) {
        return context.getSharedPreferences(FOLLOW_CONFIRM_IN_YP_DETAIL, 0);
    }

    @Deprecated
    public static boolean isFollowConfirmed(Context context) {
        return getFollowConfirmPref(context).getBoolean(FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL, false);
    }

    @Deprecated
    public static void setFollowConfirmed(Context context, boolean hasConfirmed) {
        getFollowConfirmPref(context).edit().putBoolean(FOLLOW_HAS_CONFIRMED_IN_YP_DETAIL, hasConfirmed).commit();
    }

    public static final String randomString(int count) {
        StringBuilder sb = new StringBuilder(count);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            sb.append(RANDOM_BASE_STRING.charAt(random.nextInt(RANDOM_BASE_STRING.length())));
        }
        return sb.toString();
    }

    public static String getDeviceId(Context context) {
        String deviceId = Settings.System.getString(context.getContentResolver(), MIPUB_DEVICE_ID);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        String deviceId2 = getHashedDeviceInfo(context);
        Log.d(TAG, "The random device id is " + deviceId2);
        Settings.System.putString(context.getContentResolver(), MIPUB_DEVICE_ID, deviceId2);
        return deviceId2;
    }

    private static String getHashedDeviceInfo(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.DEVICE_HASHED_DEVICE_INFO).getString("hashed_device_info");
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, long yellowPageId, boolean remote) {
        Bundle args = new Bundle();
        args.putLong("id", yellowPageId);
        args.putBoolean(YellowPageContract.Search.REMOTE_SEARCH, remote);
        args.putBoolean("hasScope", false);
        return convertBundleToModules(InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_MENU, (String) null, args));
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, long yellowPageId, boolean remote, int scope) {
        Bundle args = new Bundle();
        args.putLong("id", yellowPageId);
        args.putBoolean(YellowPageContract.Search.REMOTE_SEARCH, remote);
        args.putBoolean("hasScope", true);
        return convertBundleToModules(context, InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_MENU, (String) null, args), yellowPageId, scope);
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, String jsonString, long yellowPageId) {
        Bundle args = new Bundle();
        args.putLong("id", yellowPageId);
        args.putString("jsonString", jsonString);
        return convertBundleToModules(InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_PARSE_MENU, (String) null, args));
    }

    @Deprecated
    public static ArrayList<ModuleIntent> getYellowPageMenu(Context context, String jsonString, long yellowPageId, int scope) {
        Bundle args = new Bundle();
        args.putLong("id", yellowPageId);
        args.putString("jsonString", jsonString);
        return convertBundleToModules(context, InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_PARSE_SUB_MENU, (String) null, args), yellowPageId, scope);
    }

    public static boolean setYellowPageNetworkAllowed(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_SET_NETWORK_ALLOWED, (String) null, (Bundle) null).getBoolean("networkAllowed", false);
    }

    public static boolean isYellowPageNetworkAllowed(Context context) {
        return InvocationHandler.invoke(context, YellowPageContract.Method.YELLOWPAGE_IS_NETWORK_ALLOWED, (String) null, (Bundle) null).getBoolean("networkAllowed", false);
    }

    private static ArrayList<ModuleIntent> convertBundleToModules(Bundle bundle) {
        if (bundle.getSerializable(PaymentManager.KEY_INTENT) == null) {
            return null;
        }
        ArrayList<ModuleIntent> modules = new ArrayList<>();
        ArrayList<String> titleList = bundle.getStringArrayList("title");
        ArrayList<Integer> idList = bundle.getIntegerArrayList("id");
        ArrayList<Parcelable> intentList = bundle.getParcelableArrayList(PaymentManager.KEY_INTENT);
        for (int i = 0; i < titleList.size(); i++) {
            modules.add(new ModuleIntent(titleList.get(i), (Intent) intentList.get(i), idList.get(i).intValue()));
        }
        return modules;
    }

    private static ArrayList<ModuleIntent> convertBundleToModules(Context context, Bundle bundle, long yellowPageId, int scope) {
        ArrayList<Integer> scopeList;
        ArrayList<Integer> idList;
        Bundle bundle2 = bundle;
        int i = scope;
        if (bundle2.getSerializable(PaymentManager.KEY_INTENT) == null) {
            return null;
        }
        ArrayList<ModuleIntent> modules = new ArrayList<>();
        ArrayList<String> titleList = bundle2.getStringArrayList("title");
        ArrayList<Integer> idList2 = bundle2.getIntegerArrayList("id");
        ArrayList<Integer> scopeList2 = bundle2.getIntegerArrayList("scope");
        ArrayList<Integer> hotIdList = bundle2.getIntegerArrayList("hotId");
        ArrayList<Integer> hotShowCountList = bundle2.getIntegerArrayList("hotShowCount");
        ArrayList<Parcelable> intentList = bundle2.getParcelableArrayList(PaymentManager.KEY_INTENT);
        boolean[] subItemsFlag = bundle2.getBooleanArray("subItemsFlag");
        int i2 = 0;
        while (i2 < titleList.size()) {
            if (i == 0 || (scopeList2.get(i2).intValue() & i) > 0) {
                Intent intent = (Intent) intentList.get(i2);
                Intent intent2 = intent;
                ModuleIntent moduleIntent = new ModuleIntent(titleList.get(i2), intent, idList2.get(i2).intValue(), subItemsFlag[i2], hotIdList.get(i2).intValue(), hotShowCountList.get(i2).intValue());
                if (subItemsFlag[i2]) {
                    try {
                        try {
                            idList = idList2;
                            scopeList = scopeList2;
                            try {
                                moduleIntent.setSubModuleIntent(getYellowPageMenu(context, new JSONObject(intent2.getStringExtra(EXTRA_MULTI_MODULE_ENTRY_RAW)).optString("subItems"), yellowPageId, i));
                            } catch (JSONException e) {
                                e = e;
                            }
                        } catch (JSONException e2) {
                            e = e2;
                            Context context2 = context;
                            idList = idList2;
                            scopeList = scopeList2;
                            long j = yellowPageId;
                            e.printStackTrace();
                            modules.add(moduleIntent);
                            i2++;
                            Bundle bundle3 = bundle;
                            idList2 = idList;
                            scopeList2 = scopeList;
                        }
                    } catch (JSONException e3) {
                        e = e3;
                        Context context3 = context;
                        idList = idList2;
                        scopeList = scopeList2;
                        Intent intent3 = intent2;
                        long j2 = yellowPageId;
                        e.printStackTrace();
                        modules.add(moduleIntent);
                        i2++;
                        Bundle bundle32 = bundle;
                        idList2 = idList;
                        scopeList2 = scopeList;
                    }
                } else {
                    Context context4 = context;
                    idList = idList2;
                    scopeList = scopeList2;
                    Intent intent4 = intent2;
                    long j3 = yellowPageId;
                }
                modules.add(moduleIntent);
            } else {
                Context context5 = context;
                idList = idList2;
                scopeList = scopeList2;
                long j4 = yellowPageId;
            }
            i2++;
            Bundle bundle322 = bundle;
            idList2 = idList;
            scopeList2 = scopeList;
        }
        return modules;
    }

    public static String getMipubName(Context context, String mipubId) {
        Cursor c;
        Uri uri = Uri.withAppendedPath(YellowPageContract.MiPub.CONTENT_URI_YELLOWPAGE, Uri.encode(mipubId));
        if (YellowPageUtils.isContentProviderInstalled(context, uri) && (c = context.getContentResolver().query(uri, YELLOWPAGE_PROJECTION, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getString(2);
                }
                c.close();
            } finally {
                c.close();
            }
        }
        return null;
    }
}
