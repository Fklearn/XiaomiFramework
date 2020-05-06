package miui.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.telephony.phonenumber.Prefix;

public final class ExtraNetwork {
    private static final String ACTION_NETWORK_ASSISTANT_SMS_REPORT = "miui.intent.action.NETWORKASSISTANT_SMS_REPORT";
    public static final String ACTION_NETWORK_BLOCKED = "miui.intent.action.NETWORK_BLOCKED";
    public static final String ACTION_NETWORK_CONNECTED = "miui.intent.action.NETWORK_CONNECTED";
    private static final String ACTION_TRAFFIC_SETTING = "miui.intent.action.NETWORKASSISTANT_OPERATOR_SETTING";
    private static final String ACTION_TRAFFIC_SETTING_INTERNATIONAL = "miui.intent.action.NETWORKASSISTANT_MONTH_PACKAGE_SETTING";
    public static final String BUNDLE_KEY_COMMON = "bundle_key_com";
    public static final String BUNDLE_KEY_HAS_MENU = "bundle_key_has_menu";
    private static final String BUNDLE_KEY_OTHER_APP = "bundle_key_from_other_task";
    public static final String BUNDLE_KEY_PURCHASE_FROM = "bundle_key_purchase_from";
    public static final String BUNDLE_KEY_SLOTID = "bundle_key_slotid";
    private static final String BUNDLE_KEY_SLOT_ID = "sim_slot_num_tag";
    public static final String BUNDLE_KEY_TITLE = "bundle_key_title";
    public static final String BUNDLE_KEY_URL = "bundle_key_url";
    private static final String COLUMN_NAME_MONTH_USED = "month_used";
    private static final String COLUMN_NAME_MONTH_WARNING = "month_warning";
    private static final String COLUMN_NAME_PACKAGE_REMAINED = "package_remained";
    private static final String COLUMN_NAME_PACKAGE_TOTAL = "package_total";
    private static final String COLUMN_NAME_PACKAGE_USED = "package_used";
    private static final String COLUMN_NAME_SLOT_NUM = "slot_num";
    private static final String COLUMN_NAME_SUPPORT = "package_setted";
    private static final String COLUMN_NAME_TODAY_USED = "today_used";
    private static final String COLUMN_NAME_TOTAL_LIMIT = "total_limit";
    public static final int CORRECTION_TYPE_BILL = 2;
    public static final int CORRECTION_TYPE_CALLTIME = 4;
    public static final int CORRECTION_TYPE_TRAFFIC = 1;
    private static final String EXTRA_MIUI_STARTING_WINDOW_LABEL = ":miui:starting_window_label";
    public static final String FIREWALL_MOBILE_RULE = "mobile_rule";
    private static final String FIREWALL_MOBILE_RULE_SLOTNUM = "mobile_rule_slot";
    public static final String FIREWALL_PACKAGE_NAME = "package_name";
    private static final String FIREWALL_SOURCE_PACKAGE_NAME = "source_package_name";
    private static final String FIREWALL_TEMP_MOBILE_RULE = "temp_mobile_rule";
    private static final String FIREWALL_TEMP_MOBILE_RULE_SLOTNUM = "temp_mobile_rule_slot";
    private static final String FIREWALL_TEMP_WIFI_RULE = "temp_wifi_rule";
    private static final String FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/firewall/%s";
    public static final String FIREWALL_WIFI_RULE = "wifi_rule";
    public static final String FROM_PKGNAME = "from_pkgname";
    public static final String IMSI = "imsi";
    private static final String KEY_CORRECTION_TYPE = "correction_type";
    private static final String MOBILE_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/mobile_firewall/%s/%s";
    public static final String MOBILE_RXBYTES = "mobile_rxbytes";
    public static final String MOBILE_TXBYTES = "mobile_txbytes";
    private static final String NETWORKASSISTANT_PURCHASE_ACTION = "miui.intent.action.NETWORKASSISTANT_TRAFFIC_PURCHASE";
    public static final String STORAGE_TIME = "storage_time";
    private static final String TAG = "ExtraNetwork";
    private static final String TEMP_MOBILE_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/temp_mobile_firewall/%s/%s";
    private static final String TEMP_WIFI_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/temp_wifi_firewall/%s";
    public static final String TO_PKGNAME = "to_pkgname";
    public static final String TRACK_PURCHASE_FROM_LOCK_SCREEN_TRAFFIC = "100010";
    public static final String TRACK_PURCHASE_FROM_NETWORK_ASSISTANT_MAIN_PAGE = "100002";
    public static final String TRACK_PURCHASE_FROM_NETWORK_ASSISTANT_MAIN_TOOLBAR = "100001";
    public static final String TRACK_PURCHASE_FROM_PUSH = "100007";
    public static final String TRACK_PURCHASE_FROM_SERCURITY_CENTER_EXAM = "100008";
    public static final String TRACK_PURCHASE_FROM_STATUS_BAR = "100003";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_OVER_LIMIT_DIALOG = "100006";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_OVER_LIMIT_NOTIFY = "100005";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_SORTED = "100009";
    public static final String TRACK_PURCHASE_FROM_TRAFFIC_WARNING_NOTIFY = "100004";
    private static final String TRAFFIC_DISTRIBUTION_URI_STR = "content://com.miui.networkassistant.provider/traffic_distribution";
    public static final String TRAFFIC_PURCHASE_ENABLED = "traffic_purchase_enabled";
    private static final String TRAFFIC_PURCHASE_STATUS_URI_STR = "content://com.miui.networkassistant.provider/na_traffic_purchase";
    private static final String TRAFFIC_PURCHASE_STATUS_URI_STR_ISMI = "content://com.miui.networkassistant.provider/na_traffic_purchase/slotId/%d";
    private static final String URI_BILL_PACKAGE_DETAIL = "content://com.miui.networkassistant.provider/bill_detail";
    private static final String URI_CALL_TIME_PACKAGE_DETAIL = "content://com.miui.networkassistant.provider/calltime_detail";
    private static final String URI_NETWORK_TRAFFIC_INFO = "content://com.miui.networkassistant.provider/datausage_status";
    private static final String URI_SMS_CORRECTION = "content://com.miui.networkassistant.provider/sms_correction";
    private static final String WIFI_FIREWALL_URI_STR = "content://com.miui.networkassistant.provider/wifi_firewall/%s";
    public static final String WIFI_RXBYTES = "wifi_rxbytes";
    public static final String WIFI_TXBYTES = "wifi_txbytes";

    public static void registerFirewallContentObserver(Context context, ContentObserver observer) {
        registerContentObserver(context, String.format(FIREWALL_URI_STR, new Object[]{Prefix.EMPTY}), observer);
    }

    public static void unRegisterFirewallContentObserver(Context context, ContentObserver observer) {
        context.getContentResolver().unregisterContentObserver(observer);
    }

    public static boolean setWifiTempRestrict(Context context, String pkgName, boolean isRestrict) {
        try {
            Uri uri = Uri.parse(String.format(TEMP_WIFI_FIREWALL_URI_STR, new Object[]{pkgName}));
            if (uri != null) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues params = new ContentValues();
                params.put(FIREWALL_TEMP_WIFI_RULE, Boolean.valueOf(isRestrict));
                params.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                if (resolver.update(uri, params, (String) null, (String[]) null) == 1) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "setWifiTempRestrict", e);
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0044, code lost:
        if (r1 != null) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0046, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0055, code lost:
        if (r1 == null) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0058, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isWifiTempRestrict(android.content.Context r10, java.lang.String r11) {
        /*
            r0 = 0
            if (r10 == 0) goto L_0x005f
            boolean r1 = android.text.TextUtils.isEmpty(r11)
            if (r1 == 0) goto L_0x000a
            goto L_0x005f
        L_0x000a:
            r1 = 0
            java.lang.String r2 = "content://com.miui.networkassistant.provider/temp_wifi_firewall/%s"
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x004c }
            r4[r0] = r11     // Catch:{ Exception -> 0x004c }
            java.lang.String r2 = java.lang.String.format(r2, r4)     // Catch:{ Exception -> 0x004c }
            android.net.Uri r5 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x004c }
            android.content.ContentResolver r4 = r10.getContentResolver()     // Catch:{ Exception -> 0x004c }
            r6 = 0
            r7 = 0
            java.lang.String[] r8 = new java.lang.String[r3]     // Catch:{ Exception -> 0x004c }
            r8[r0] = r11     // Catch:{ Exception -> 0x004c }
            r9 = 0
            android.database.Cursor r2 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x004c }
            r1 = r2
            if (r1 == 0) goto L_0x0044
            boolean r2 = r1.moveToFirst()     // Catch:{ Exception -> 0x004c }
            if (r2 == 0) goto L_0x0044
            java.lang.String r2 = "temp_wifi_rule"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ Exception -> 0x004c }
            int r2 = r1.getInt(r2)     // Catch:{ Exception -> 0x004c }
            if (r2 != r3) goto L_0x003f
            r0 = r3
        L_0x003f:
            r1.close()
            return r0
        L_0x0044:
            if (r1 == 0) goto L_0x0058
        L_0x0046:
            r1.close()
            goto L_0x0058
        L_0x004a:
            r0 = move-exception
            goto L_0x0059
        L_0x004c:
            r2 = move-exception
            java.lang.String r3 = "ExtraNetwork"
            java.lang.String r4 = "isWifiTempRestrict"
            android.util.Log.e(r3, r4, r2)     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x0058
            goto L_0x0046
        L_0x0058:
            return r0
        L_0x0059:
            if (r1 == 0) goto L_0x005e
            r1.close()
        L_0x005e:
            throw r0
        L_0x005f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.isWifiTempRestrict(android.content.Context, java.lang.String):boolean");
    }

    public static boolean setMobileTempRestrict(Context context, String pkgName, int slotnum, boolean isRestrict) {
        try {
            Uri uri = Uri.parse(String.format(TEMP_MOBILE_FIREWALL_URI_STR, new Object[]{Integer.valueOf(slotnum), pkgName}));
            if (uri != null) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues params = new ContentValues();
                params.put(FIREWALL_TEMP_MOBILE_RULE_SLOTNUM, Integer.valueOf(slotnum));
                params.put(FIREWALL_TEMP_MOBILE_RULE, Boolean.valueOf(isRestrict));
                params.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                if (resolver.update(uri, params, (String) null, (String[]) null) == 1) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "setMobileTempRestrict", e);
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004b, code lost:
        if (r1 != null) goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004d, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005c, code lost:
        if (r1 == null) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005f, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isMobileTempRestrict(android.content.Context r11, java.lang.String r12, int r13) {
        /*
            r0 = 0
            if (r11 == 0) goto L_0x0066
            boolean r1 = android.text.TextUtils.isEmpty(r12)
            if (r1 == 0) goto L_0x000a
            goto L_0x0066
        L_0x000a:
            r1 = 0
            java.lang.String r2 = "content://com.miui.networkassistant.provider/temp_mobile_firewall/%s/%s"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0053 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x0053 }
            r3[r0] = r4     // Catch:{ Exception -> 0x0053 }
            r4 = 1
            r3[r4] = r12     // Catch:{ Exception -> 0x0053 }
            java.lang.String r2 = java.lang.String.format(r2, r3)     // Catch:{ Exception -> 0x0053 }
            android.net.Uri r6 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0053 }
            android.content.ContentResolver r5 = r11.getContentResolver()     // Catch:{ Exception -> 0x0053 }
            r7 = 0
            r8 = 0
            java.lang.String[] r9 = new java.lang.String[r4]     // Catch:{ Exception -> 0x0053 }
            r9[r0] = r12     // Catch:{ Exception -> 0x0053 }
            r10 = 0
            android.database.Cursor r2 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0053 }
            r1 = r2
            if (r1 == 0) goto L_0x004b
            boolean r2 = r1.moveToFirst()     // Catch:{ Exception -> 0x0053 }
            if (r2 == 0) goto L_0x004b
            java.lang.String r2 = "temp_mobile_rule"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ Exception -> 0x0053 }
            int r2 = r1.getInt(r2)     // Catch:{ Exception -> 0x0053 }
            if (r2 != r4) goto L_0x0046
            r0 = r4
        L_0x0046:
            r1.close()
            return r0
        L_0x004b:
            if (r1 == 0) goto L_0x005f
        L_0x004d:
            r1.close()
            goto L_0x005f
        L_0x0051:
            r0 = move-exception
            goto L_0x0060
        L_0x0053:
            r2 = move-exception
            java.lang.String r3 = "ExtraNetwork"
            java.lang.String r4 = "isMobileTempRestrict"
            android.util.Log.e(r3, r4, r2)     // Catch:{ all -> 0x0051 }
            if (r1 == 0) goto L_0x005f
            goto L_0x004d
        L_0x005f:
            return r0
        L_0x0060:
            if (r1 == 0) goto L_0x0065
            r1.close()
        L_0x0065:
            throw r0
        L_0x0066:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.isMobileTempRestrict(android.content.Context, java.lang.String, int):boolean");
    }

    public static boolean setMobileRestrict(Context context, String pkgName, boolean isRestrict) {
        return setMobileRestrict(context, pkgName, isRestrict, -1);
    }

    public static boolean setMobileRestrict(Context context, String pkgName, boolean isRestrict, int slotNum) {
        try {
            Uri uri = Uri.parse(String.format(MOBILE_FIREWALL_URI_STR, new Object[]{Integer.valueOf(slotNum), pkgName}));
            if (uri != null) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues params = new ContentValues();
                params.put(FIREWALL_MOBILE_RULE_SLOTNUM, Integer.valueOf(slotNum));
                params.put(FIREWALL_MOBILE_RULE, Boolean.valueOf(isRestrict));
                params.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                if (resolver.update(uri, params, (String) null, (String[]) null) == 1) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "setMobileTempRestrict", e);
        }
        return false;
    }

    public static boolean setWifiRestrict(Context context, String pkgName, boolean isRestrict) {
        try {
            Uri uri = Uri.parse(String.format(WIFI_FIREWALL_URI_STR, new Object[]{pkgName}));
            if (uri != null) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues params = new ContentValues();
                params.put(FIREWALL_WIFI_RULE, Boolean.valueOf(isRestrict));
                params.put(FIREWALL_SOURCE_PACKAGE_NAME, context.getPackageName());
                if (resolver.update(uri, params, (String) null, (String[]) null) == 1) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "setWifiTempRestrict", e);
        }
        return false;
    }

    public static boolean isMobileRestrict(Context context, String pkgName) {
        return isMobileRestrict(context, pkgName, -1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004b, code lost:
        if (r1 != null) goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004d, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005c, code lost:
        if (r1 == null) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005f, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isMobileRestrict(android.content.Context r11, java.lang.String r12, int r13) {
        /*
            r0 = 0
            if (r11 == 0) goto L_0x0066
            boolean r1 = android.text.TextUtils.isEmpty(r12)
            if (r1 == 0) goto L_0x000a
            goto L_0x0066
        L_0x000a:
            r1 = 0
            java.lang.String r2 = "content://com.miui.networkassistant.provider/mobile_firewall/%s/%s"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0053 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x0053 }
            r3[r0] = r4     // Catch:{ Exception -> 0x0053 }
            r4 = 1
            r3[r4] = r12     // Catch:{ Exception -> 0x0053 }
            java.lang.String r2 = java.lang.String.format(r2, r3)     // Catch:{ Exception -> 0x0053 }
            android.net.Uri r6 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0053 }
            android.content.ContentResolver r5 = r11.getContentResolver()     // Catch:{ Exception -> 0x0053 }
            r7 = 0
            r8 = 0
            java.lang.String[] r9 = new java.lang.String[r4]     // Catch:{ Exception -> 0x0053 }
            r9[r0] = r12     // Catch:{ Exception -> 0x0053 }
            r10 = 0
            android.database.Cursor r2 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0053 }
            r1 = r2
            if (r1 == 0) goto L_0x004b
            boolean r2 = r1.moveToFirst()     // Catch:{ Exception -> 0x0053 }
            if (r2 == 0) goto L_0x004b
            java.lang.String r2 = "mobile_rule"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ Exception -> 0x0053 }
            int r2 = r1.getInt(r2)     // Catch:{ Exception -> 0x0053 }
            if (r2 != r4) goto L_0x0046
            r0 = r4
        L_0x0046:
            r1.close()
            return r0
        L_0x004b:
            if (r1 == 0) goto L_0x005f
        L_0x004d:
            r1.close()
            goto L_0x005f
        L_0x0051:
            r0 = move-exception
            goto L_0x0060
        L_0x0053:
            r2 = move-exception
            java.lang.String r3 = "ExtraNetwork"
            java.lang.String r4 = "isMobileRestrict"
            android.util.Log.e(r3, r4, r2)     // Catch:{ all -> 0x0051 }
            if (r1 == 0) goto L_0x005f
            goto L_0x004d
        L_0x005f:
            return r0
        L_0x0060:
            if (r1 == 0) goto L_0x0065
            r1.close()
        L_0x0065:
            throw r0
        L_0x0066:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.isMobileRestrict(android.content.Context, java.lang.String, int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0044, code lost:
        if (r1 != null) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0046, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0055, code lost:
        if (r1 == null) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0058, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isWifiRestrict(android.content.Context r10, java.lang.String r11) {
        /*
            r0 = 0
            if (r10 == 0) goto L_0x005f
            boolean r1 = android.text.TextUtils.isEmpty(r11)
            if (r1 == 0) goto L_0x000a
            goto L_0x005f
        L_0x000a:
            r1 = 0
            java.lang.String r2 = "content://com.miui.networkassistant.provider/wifi_firewall/%s"
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x004c }
            r4[r0] = r11     // Catch:{ Exception -> 0x004c }
            java.lang.String r2 = java.lang.String.format(r2, r4)     // Catch:{ Exception -> 0x004c }
            android.net.Uri r5 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x004c }
            android.content.ContentResolver r4 = r10.getContentResolver()     // Catch:{ Exception -> 0x004c }
            r6 = 0
            r7 = 0
            java.lang.String[] r8 = new java.lang.String[r3]     // Catch:{ Exception -> 0x004c }
            r8[r0] = r11     // Catch:{ Exception -> 0x004c }
            r9 = 0
            android.database.Cursor r2 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x004c }
            r1 = r2
            if (r1 == 0) goto L_0x0044
            boolean r2 = r1.moveToFirst()     // Catch:{ Exception -> 0x004c }
            if (r2 == 0) goto L_0x0044
            java.lang.String r2 = "wifi_rule"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ Exception -> 0x004c }
            int r2 = r1.getInt(r2)     // Catch:{ Exception -> 0x004c }
            if (r2 != r3) goto L_0x003f
            r0 = r3
        L_0x003f:
            r1.close()
            return r0
        L_0x0044:
            if (r1 == 0) goto L_0x0058
        L_0x0046:
            r1.close()
            goto L_0x0058
        L_0x004a:
            r0 = move-exception
            goto L_0x0059
        L_0x004c:
            r2 = move-exception
            java.lang.String r3 = "ExtraNetwork"
            java.lang.String r4 = "isWifiRestrict"
            android.util.Log.e(r3, r4, r2)     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x0058
            goto L_0x0046
        L_0x0058:
            return r0
        L_0x0059:
            if (r1 == 0) goto L_0x005e
            r1.close()
        L_0x005e:
            throw r0
        L_0x005f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.isWifiRestrict(android.content.Context, java.lang.String):boolean");
    }

    @Deprecated
    public static boolean insertTrafficDistribution(Context context, String toPackageName, long wifiTxBytes, long wifiRxBytes, long mobileTxBytes, long mobileRxBytes) {
        if (context == null) {
            String str = toPackageName;
        } else if (TextUtils.isEmpty(toPackageName)) {
            String str2 = toPackageName;
        } else {
            try {
                Uri uri = Uri.parse(TRAFFIC_DISTRIBUTION_URI_STR);
                if (uri != null) {
                    String imsi = Prefix.EMPTY;
                    TelephonyManager telephony = (TelephonyManager) context.getSystemService("phone");
                    if (telephony != null) {
                        imsi = telephony.getSubscriberId();
                    }
                    ContentValues values = new ContentValues();
                    values.put(FROM_PKGNAME, context.getPackageName());
                    String str3 = toPackageName;
                    try {
                        values.put(TO_PKGNAME, toPackageName);
                        values.put(MOBILE_RXBYTES, Long.valueOf(mobileRxBytes));
                        values.put(MOBILE_TXBYTES, Long.valueOf(mobileTxBytes));
                        values.put(WIFI_RXBYTES, Long.valueOf(wifiRxBytes));
                        values.put(WIFI_TXBYTES, Long.valueOf(wifiTxBytes));
                        values.put("imsi", imsi);
                        values.put(STORAGE_TIME, Long.valueOf(System.currentTimeMillis()));
                        if (!TextUtils.isEmpty(context.getContentResolver().insert(uri, values).getLastPathSegment())) {
                            return true;
                        }
                    } catch (Exception e) {
                        e = e;
                        Log.e(TAG, "insertTrafficDistribution", e);
                        return false;
                    }
                } else {
                    String str4 = toPackageName;
                }
            } catch (Exception e2) {
                e = e2;
                String str5 = toPackageName;
                Log.e(TAG, "insertTrafficDistribution", e);
                return false;
            }
            return false;
        }
        return false;
    }

    public static boolean isTrafficPurchaseSupported(Context context) {
        if (context == null) {
            return false;
        }
        try {
            return queryTrafficPurchaseStatus(context, Uri.parse(TRAFFIC_PURCHASE_STATUS_URI_STR));
        } catch (Exception e) {
            Log.e(TAG, "isTrafficPurchaseSupported", e);
            return false;
        }
    }

    public static boolean isTrafficPurchaseSupported(Context context, int slotId) {
        if (context != null && slotId >= 0 && slotId < 2) {
            try {
                return queryTrafficPurchaseStatus(context, Uri.parse(String.format(TRAFFIC_PURCHASE_STATUS_URI_STR_ISMI, new Object[]{Integer.valueOf(slotId)})));
            } catch (Exception e) {
                Log.e(TAG, "isTrafficPurchaseSupported", e);
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0039, code lost:
        if (r0 == null) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003b, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0045, code lost:
        if (r0 != null) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0048, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean queryTrafficPurchaseStatus(android.content.Context r8, android.net.Uri r9) {
        /*
            r0 = 0
            r1 = 0
            if (r9 == 0) goto L_0x0045
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch:{ Exception -> 0x0030 }
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r3 = r9
            android.database.Cursor r3 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0030 }
            r0 = r3
            if (r0 == 0) goto L_0x0045
            boolean r3 = r0.moveToFirst()     // Catch:{ Exception -> 0x0030 }
            if (r3 == 0) goto L_0x0045
            java.lang.String r3 = "traffic_purchase_enabled"
            int r3 = r0.getColumnIndex(r3)     // Catch:{ Exception -> 0x0030 }
            java.lang.String r3 = r0.getString(r3)     // Catch:{ Exception -> 0x0030 }
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r3)     // Catch:{ Exception -> 0x0030 }
            boolean r3 = r3.booleanValue()     // Catch:{ Exception -> 0x0030 }
            r1 = r3
            goto L_0x0045
        L_0x002e:
            r2 = move-exception
            goto L_0x003f
        L_0x0030:
            r2 = move-exception
            java.lang.String r3 = "ExtraNetwork"
            java.lang.String r4 = "queryTrafficPurchaseStatus"
            android.util.Log.e(r3, r4, r2)     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x0048
        L_0x003b:
            r0.close()
            goto L_0x0048
        L_0x003f:
            if (r0 == 0) goto L_0x0044
            r0.close()
        L_0x0044:
            throw r2
        L_0x0045:
            if (r0 == 0) goto L_0x0048
            goto L_0x003b
        L_0x0048:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraNetwork.queryTrafficPurchaseStatus(android.content.Context, android.net.Uri):boolean");
    }

    @Deprecated
    public static void navigateToTrafficPurchasePage(Context context) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void navigateToTrafficPurchasePage(Context context, String sourceFrom) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, sourceFrom);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void navigateToRichWebActivity(Context context, String url, String title, boolean hasMenu, String sourceFrom, boolean needNewTask) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_URL, url);
        bundle.putString(BUNDLE_KEY_TITLE, title);
        bundle.putBoolean(BUNDLE_KEY_HAS_MENU, hasMenu);
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, sourceFrom);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.putExtra(":miui:starting_window_label", title);
        if (needNewTask) {
            intent.addFlags(268435456);
        }
        context.startActivity(intent);
    }

    @Deprecated
    public static void navigateToTrafficPurchasePage(Context context, int slotId) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOTID, slotId);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void navigateToTrafficPurchasePage(Context context, int slotId, String sourceFrom) {
        Intent intent = new Intent(NETWORKASSISTANT_PURCHASE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOTID, slotId);
        bundle.putString(BUNDLE_KEY_PURCHASE_FROM, sourceFrom);
        intent.putExtra(BUNDLE_KEY_COMMON, bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static final class DataUsageDetail {
        public long monthTotal;
        public long monthUsed;
        public long monthWarning;
        public long todayUsed;

        public DataUsageDetail(long monthTotal2, long monthUsed2, long monthWarning2, long todayUsed2) {
            this.monthTotal = monthTotal2;
            this.monthUsed = monthUsed2;
            this.monthWarning = monthWarning2;
            this.todayUsed = todayUsed2;
        }

        public String toString() {
            return String.format("monthTotal:%s, monthUsed:%s, monthWarning:%s, todayUsed:%s", new Object[]{Long.valueOf(this.monthTotal), Long.valueOf(this.monthUsed), Long.valueOf(this.monthWarning), Long.valueOf(this.todayUsed)});
        }
    }

    public static DataUsageDetail getUserDataUsageDetail(Context context) {
        if (context == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.parse(URI_NETWORK_TRAFFIC_INFO), (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor == null) {
                    return null;
                }
                cursor.close();
                return null;
            }
            DataUsageDetail dataUsageDetail = new DataUsageDetail(cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TOTAL_LIMIT)), cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_MONTH_USED)), cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_MONTH_WARNING)), cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TODAY_USED)));
            cursor.close();
            return dataUsageDetail;
        } catch (Exception e) {
            Log.e(TAG, "getUserDataUsageDetail", e);
            if (cursor == null) {
                return null;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public static final class PackageDetail {
        public boolean isSupport;
        public long packageRemained;
        public long packageTotal;
        public long packageUsed;
        public int slotNum;

        public PackageDetail(long packageTotal2, long packageUsed2, long packageRemained2, int slotNum2, boolean isSupport2) {
            this.packageTotal = packageTotal2;
            this.packageUsed = packageUsed2;
            this.packageRemained = packageRemained2;
            this.slotNum = slotNum2;
            this.isSupport = isSupport2;
        }

        public String toString() {
            return String.format("packageTotal:%s, packageUsed:%s, packageRemained:%s, slotNum:%s, isSupport:%s", new Object[]{Long.valueOf(this.packageTotal), Long.valueOf(this.packageUsed), Long.valueOf(this.packageRemained), Integer.valueOf(this.slotNum), Boolean.valueOf(this.isSupport)});
        }
    }

    public static List<PackageDetail> getBillPackageDetail(Context context) {
        Cursor cursor = null;
        List<PackageDetail> packageDetails = new ArrayList<>();
        try {
            Cursor cursor2 = context.getContentResolver().query(Uri.parse(URI_BILL_PACKAGE_DETAIL), (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor2 != null) {
                while (cursor2.moveToNext()) {
                    packageDetails.add(new PackageDetail(cursor2.getLong(cursor2.getColumnIndex(COLUMN_NAME_PACKAGE_TOTAL)), cursor2.getLong(cursor2.getColumnIndex(COLUMN_NAME_PACKAGE_USED)), cursor2.getLong(cursor2.getColumnIndex(COLUMN_NAME_PACKAGE_REMAINED)), cursor2.getInt(cursor2.getColumnIndex(COLUMN_NAME_SLOT_NUM)), "true".equals(cursor2.getString(cursor2.getColumnIndex(COLUMN_NAME_SUPPORT)))));
                }
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            return packageDetails;
        } catch (Exception e) {
            Log.e(TAG, "getBillPackageDetail", e);
            if (cursor != null) {
                cursor.close();
            }
            return packageDetails;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public static List<PackageDetail> getCallTimePackageDetail(Context context) {
        Cursor cursor = null;
        List<PackageDetail> packageDetails = new ArrayList<>();
        try {
            Cursor cursor2 = context.getContentResolver().query(Uri.parse(URI_CALL_TIME_PACKAGE_DETAIL), (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor2 != null) {
                while (cursor2.moveToNext()) {
                    packageDetails.add(new PackageDetail(cursor2.getLong(cursor2.getColumnIndex(COLUMN_NAME_PACKAGE_TOTAL)), cursor2.getLong(cursor2.getColumnIndex(COLUMN_NAME_PACKAGE_USED)), cursor2.getLong(cursor2.getColumnIndex(COLUMN_NAME_PACKAGE_REMAINED)), cursor2.getInt(cursor2.getColumnIndex(COLUMN_NAME_SLOT_NUM)), "true".equals(cursor2.getString(cursor2.getColumnIndex(COLUMN_NAME_SUPPORT)))));
                }
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            return packageDetails;
        } catch (Exception e) {
            Log.e(TAG, "getCallTimePackageDetail", e);
            if (cursor != null) {
                cursor.close();
            }
            return packageDetails;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public static void navigateToOperatorSettingActivity(Context context, int slotId) {
        Intent intent;
        if (Build.IS_INTERNATIONAL_BUILD) {
            intent = new Intent(ACTION_TRAFFIC_SETTING_INTERNATIONAL);
        } else {
            intent = new Intent(ACTION_TRAFFIC_SETTING);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SLOT_ID, slotId);
        bundle.putBoolean(BUNDLE_KEY_OTHER_APP, true);
        intent.putExtras(bundle);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static boolean startCorrection(Context context, int slotId, int type) {
        try {
            Uri uri = Uri.parse(URI_SMS_CORRECTION);
            if (uri != null) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(BUNDLE_KEY_SLOT_ID, Integer.valueOf(slotId));
                values.put(KEY_CORRECTION_TYPE, Integer.valueOf(type));
                if (resolver.update(uri, values, (String) null, (String[]) null) == 1) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "startCorrection", e);
        }
        return false;
    }

    public static void navigateToSmsReportActivity(Context context, int slotId, int type) {
        Intent intent = new Intent(ACTION_NETWORK_ASSISTANT_SMS_REPORT);
        intent.putExtra(BUNDLE_KEY_SLOT_ID, slotId);
        intent.putExtra(KEY_CORRECTION_TYPE, type);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void registerPackageContentObserver(Context context, ContentObserver observer, int type) {
        String uriStr;
        if (type == 2) {
            uriStr = URI_BILL_PACKAGE_DETAIL;
        } else if (type == 4) {
            uriStr = URI_CALL_TIME_PACKAGE_DETAIL;
        } else {
            uriStr = URI_NETWORK_TRAFFIC_INFO;
        }
        registerContentObserver(context, uriStr, observer);
    }

    public static void unRegisterPackageContentObserver(Context context, ContentObserver observer) {
        context.getContentResolver().unregisterContentObserver(observer);
    }

    private static void registerContentObserver(Context context, String uriStr, ContentObserver observer) {
        try {
            Method declaredMethod = Class.forName("android.content.ContentResolver").getDeclaredMethod("registerContentObserver", new Class[]{Uri.class, Boolean.TYPE, ContentObserver.class, Integer.TYPE});
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(context.getContentResolver(), new Object[]{Uri.parse(uriStr), true, observer, 0});
        } catch (Exception e) {
            Log.e(TAG, "registerContentObserver error", e);
        }
    }
}
