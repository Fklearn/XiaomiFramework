package com.miui.earthquakewarning.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;
import b.b.c.j.x;
import com.miui.common.persistence.b;
import com.miui.earthquakewarning.Constants;

public class Utils {
    private static final String ACCOUNT_TYPE_STR = "com.xiaomi";
    private static final String EMPTY_STR = "";

    private Utils() {
    }

    public static String getAccountId(Context context) {
        Account[] accountsByType;
        AccountManager accountManager = AccountManager.get(context);
        if (accountManager == null || (accountsByType = accountManager.getAccountsByType("com.xiaomi")) == null || accountsByType.length == 0) {
            return "";
        }
        String str = accountsByType[0].name;
        return TextUtils.isEmpty(str) ? "" : str;
    }

    public static String getContact() {
        return b.a(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT, "");
    }

    public static String getContactName() {
        return b.a(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT_NAME, "");
    }

    public static int getPreviousAreaCode() {
        return b.a(Constants.PREFERENCE_KEY_PREVIOUS_AREA_CODE, 0);
    }

    public static int getPreviousAreaDistricCode() {
        return b.a(Constants.PREFERENCE_KEY_PREVIOUS_AREA_DISTRICT_CODE, 0);
    }

    public static int getPreviousBrightness() {
        return b.a(Constants.PREFERENCE_KEY_PREVIOUS_BRIGHTNESS, 0);
    }

    public static int getPreviousBrightnessMode() {
        return b.a(Constants.PREFERENCE_KEY_PREVIOUS_BRIGHTNESS_MODE, 0);
    }

    public static String getPreviousDistrict() {
        return b.a(Constants.PREFERENCE_KEY_PREVIOUS_DISTRICT, "");
    }

    public static int getPreviousGPS() {
        return b.a(Constants.PREFERENCE_KEY_PREVIOUS_GPS, 0);
    }

    public static int getPreviousVolume() {
        return b.a(Constants.PREFERENCE_KEY_PREVIOUS_VOLUME, 0);
    }

    public static long getUploadTopicTime() {
        return b.a(Constants.PREFERENCE_KEY_SET_TOPIC_TIME, 0);
    }

    public static boolean isEarthquakeWarningOpen() {
        return b.a(Constants.PREFERENCE_KEY_OPEN_EARTHQUAKE_WARNING, false);
    }

    public static boolean isEmergencyInfoEmpty() {
        return b.a(Constants.PREF_KEY_ALL_INFO_DELETE, true);
    }

    public static boolean isLowEarthquakeWarningOpen() {
        return b.a(Constants.PREFERENCE_KEY_OPEN_LOW_EARTHQUAKE_WARNING, false);
    }

    public static void setContact(String str) {
        b.b(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT, str);
    }

    public static void setContactName(String str) {
        b.b(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT_NAME, str);
    }

    public static void setEmergencyDeleteAll(boolean z) {
        b.b(Constants.PREF_KEY_ALL_INFO_DELETE, z);
    }

    public static void setLowEarthquakeWarningOpen(boolean z) {
        b.b(Constants.PREFERENCE_KEY_OPEN_LOW_EARTHQUAKE_WARNING, z);
    }

    public static void setPreviousAreaCode(int i) {
        b.b(Constants.PREFERENCE_KEY_PREVIOUS_AREA_CODE, i);
    }

    public static void setPreviousAreaDistrictCode(int i) {
        b.b(Constants.PREFERENCE_KEY_PREVIOUS_AREA_DISTRICT_CODE, i);
    }

    public static void setPreviousBrightness(int i) {
        b.b(Constants.PREFERENCE_KEY_PREVIOUS_BRIGHTNESS, i);
    }

    public static void setPreviousBrightnessMode(int i) {
        b.b(Constants.PREFERENCE_KEY_PREVIOUS_BRIGHTNESS_MODE, i);
    }

    public static void setPreviousDistrict(String str) {
        b.b(Constants.PREFERENCE_KEY_PREVIOUS_DISTRICT, str);
    }

    public static void setPreviousGPS(int i) {
        b.b(Constants.PREFERENCE_KEY_PREVIOUS_GPS, i);
    }

    public static void setPreviousVolume(int i) {
        b.b(Constants.PREFERENCE_KEY_PREVIOUS_VOLUME, i);
    }

    public static void setUploadTopicTime(long j) {
        b.b(Constants.PREFERENCE_KEY_SET_TOPIC_TIME, j);
    }

    public static boolean supportMap(Context context) {
        return x.e(context, Constants.SECURITY_ADD_PACKAGE) >= 91032;
    }

    public static void toggle(boolean z) {
        b.b(Constants.PREFERENCE_KEY_OPEN_EARTHQUAKE_WARNING, z);
    }
}
