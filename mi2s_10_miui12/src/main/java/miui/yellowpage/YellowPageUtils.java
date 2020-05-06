package miui.yellowpage;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import miui.os.Build;
import miui.provider.ExtraTelephony;
import miui.telephony.phonenumber.Prefix;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

public class YellowPageUtils {
    private static final int ANTISPAM_COLUMN_CID = 1;
    private static final int ANTISPAM_COLUMN_MARKED_COUNT = 3;
    private static final int ANTISPAM_COLUMN_NORMALIZED_NUMBER = 4;
    private static final int ANTISPAM_COLUMN_NUMBER_TYPE = 5;
    private static final int ANTISPAM_COLUMN_PID = 0;
    private static final int ANTISPAM_COLUMN_TYPE = 2;
    private static final String[] ANTISPAM_PROJECTION = {"pid", "cid", "type", YellowPageContract.AntispamNumber.MARKED_COUNT, "normalized_number", "number_type"};
    private static final String CLOUD_ANTISPAM = "cloud_antispam";
    private static final int CLOUD_ANTISPAM_DISABLE = 0;
    private static final int CLOUD_ANTISPAM_ENANBLE = 1;
    private static final int PHONE_COLUMN_ATD_COUNT = 15;
    private static final int PHONE_COLUMN_ATD_ID = 13;
    private static final int PHONE_COLUMN_ATD_PROVIDER = 16;
    private static final int PHONE_COLUMN_CALL_MENU = 11;
    private static final int PHONE_COLUMN_CREDIT_IMG = 17;
    private static final int PHONE_COLUMN_NORMALIZED_NUMBER = 9;
    private static final int PHONE_COLUMN_NUMBER_TYPE = 18;
    private static final int PHONE_COLUMN_PHOTO_URL = 3;
    private static final int PHONE_COLUMN_PROVIDER_ID = 1;
    private static final int PHONE_COLUMN_SLOGAN = 14;
    private static final int PHONE_COLUMN_SUSPECT = 10;
    private static final int PHONE_COLUMN_T9_RANK = 12;
    private static final int PHONE_COLUMN_TAG = 2;
    private static final int PHONE_COLUMN_TAG_PINYIN = 7;
    private static final int PHONE_COLUMN_THUMBNAIL_URL = 4;
    private static final int PHONE_COLUMN_VISIBLE = 8;
    private static final int PHONE_COLUMN_YID = 0;
    private static final int PHONE_COLUMN_YP_NAME = 5;
    private static final int PHONE_COLUMN_YP_NAME_PINYIN = 6;
    private static final String[] PHONE_PROJECTION = {"yid", YellowPageContract.PhoneLookup.PROVIDER_ID, "tag", YellowPageContract.PhoneLookup.PHOTO_URL, YellowPageContract.PhoneLookup.THUMBNAIL_URL, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME, YellowPageContract.PhoneLookup.YELLOW_PAGE_NAME_PINYIN, YellowPageContract.PhoneLookup.TAG_PINYIN, "hide", "normalized_number", YellowPageContract.PhoneLookup.SUSPECT, YellowPageContract.PhoneLookup.CALL_MENU, "t9_rank", YellowPageContract.PhoneLookup.ATD_ID, "slogan", YellowPageContract.PhoneLookup.ATD_COUNT, YellowPageContract.PhoneLookup.ATD_PROVIDER, "credit_img", "number_type"};
    private static final String TAG = "YellowPageUtils";
    @SuppressLint({"UseSparseArrays"})
    private static final ConcurrentHashMap<Integer, AntispamCategory> sCidCategories = new ConcurrentHashMap<>();
    @SuppressLint({"UseSparseArrays"})
    private static final HashMap<Integer, YellowPageProvider> sProviders = new HashMap<>();

    private YellowPageUtils() {
    }

    public static String getUserAreaCode(Context context) {
        Cursor c;
        if (isContentProviderInstalled(context, YellowPageContract.UserArea.CONTENT_URI) && (c = context.getContentResolver().query(YellowPageContract.UserArea.CONTENT_URI, new String[]{YellowPageContract.UserArea.AREA_CODE}, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (c.moveToFirst()) {
                    String string = c.getString(0);
                    c.close();
                    return string;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                c.close();
                throw th;
            }
            c.close();
        }
        return null;
    }

    public static String getNormalizedNumber(Context context, String number) {
        return getNormalizedNumber(context, number, true, (String) null);
    }

    public static String getNormalizedNumber(Context context, String number, boolean withCountryCode, String defaultCountryCode) {
        Cursor cursor;
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        Uri.Builder builder = Uri.withAppendedPath(YellowPageContract.PhoneLookup.CONTENT_NORMALIZED_NUMBER, number).buildUpon();
        builder.appendQueryParameter("withCountryCode", withCountryCode ? "true" : "false");
        if (!TextUtils.isEmpty(defaultCountryCode)) {
            builder.appendQueryParameter("defaultCountryCode", defaultCountryCode);
        }
        Uri uri = builder.build();
        if (isContentProviderInstalled(context, uri) && (cursor = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
                cursor.close();
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public static YellowPagePhone getPhoneInfo(Context context, String number, boolean fetchRemote) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        YellowPagePhone phone = queryPhoneInfo(context, number, false);
        if (phone == null && fetchRemote) {
            return queryPhoneInfo(context, number, true);
        }
        if (!fetchRemote) {
            return phone;
        }
        updateCloudPhoneInfo(context, number);
        return phone;
    }

    public static void updatePhoneInfo(Context context, String number) {
        if (!TextUtils.isEmpty(number)) {
            Log.d(TAG, "updatePhoneInfo updateCloud");
            updateCloudPhoneInfo(context, number);
        }
    }

    public static HashMap<String, YellowPagePhone> getLocalYellowPagePhones(Context context, List<String> numbers) {
        return getLocalYellowPagePhones(context, numbers, (HashMap<String, String>) null);
    }

    public static HashMap<String, YellowPagePhone> getLocalYellowPagePhones(Context context, List<String> numbers, HashMap<String, String> outNorNumbersMap) {
        String normalizedNumber;
        Context context2 = context;
        List<String> list = numbers;
        HashMap<String, String> hashMap = outNorNumbersMap;
        if (!isYellowPageAvailable(context) || list == null || numbers.size() == 0 || !isContentProviderInstalled(context2, YellowPageContract.PhoneLookup.CONTENT_URI)) {
            return null;
        }
        HashMap hashMap2 = new HashMap();
        HashMap<String, YellowPagePhone> resultMap = new HashMap<>();
        int i = 0;
        while (i < numbers.size()) {
            String number = list.get(i);
            if (hashMap == null || TextUtils.isEmpty(hashMap.get(number))) {
                normalizedNumber = getNormalizedNumber(context2, number);
                if (TextUtils.isEmpty(normalizedNumber)) {
                    i++;
                } else if (hashMap != null) {
                    hashMap.put(number, normalizedNumber);
                }
            } else {
                normalizedNumber = hashMap.get(number);
            }
            hashMap2.put(normalizedNumber, number);
            if (hashMap2.size() > 50 || i == numbers.size() - 1) {
                StringBuilder sb = new StringBuilder();
                for (String num : hashMap2.keySet()) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append("'");
                    sb.append(num);
                    sb.append("'");
                }
                Cursor c = context.getContentResolver().query(YellowPageContract.PhoneLookup.CONTENT_URI, PHONE_PROJECTION, "normalized_number IN (" + sb.toString() + ")", (String[]) null, (String) null);
                if (c != null) {
                    String str = normalizedNumber;
                    String normalizedNumber2 = number;
                    while (c.moveToNext()) {
                        try {
                            String number2 = (String) hashMap2.get(c.getString(9));
                            resultMap.put(number2, buildYellowPagePhoneFromCursor(c, number2));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Throwable th) {
                            c.close();
                            throw th;
                        }
                    }
                    hashMap2.clear();
                    c.close();
                }
                i++;
            } else {
                i++;
            }
        }
        return resultMap;
    }

    public static void queryExpressInfo(Context context, String bizCode, String serialNumber) {
        if (!queryLocalExpressInfo(context, bizCode, serialNumber)) {
            queryCloudExpressInfo(context, bizCode, serialNumber);
        }
    }

    private static boolean queryLocalExpressInfo(Context context, String bizName, String serialNumber) {
        if (!isYellowPageAvailable(context) || TextUtils.isEmpty(bizName) || TextUtils.isEmpty(serialNumber)) {
            return false;
        }
        Bundle args = new Bundle();
        args.putString("bizName", bizName);
        args.putString("serialNumber", serialNumber);
        return InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_LOCAL_EXPRESS_INFO, (String) null, args).getBoolean("res");
    }

    private static void queryCloudExpressInfo(Context context, String bizCode, String serialNumber) {
        if (isYellowPageAvailable(context) && !TextUtils.isEmpty(bizCode) && !TextUtils.isEmpty(serialNumber)) {
            Bundle args = new Bundle();
            args.putString("bizName", bizCode);
            args.putString("serialNumber", serialNumber);
            InvocationHandler.invoke(context, YellowPageContract.Method.REQUEST_REMOTE_EXPRESS_INFO, (String) null, args);
        }
    }

    public static YellowPageProvider getProvider(Context context, int pid) {
        YellowPageProvider provider = sProviders.get(Integer.valueOf(pid));
        if (provider != null && !TextUtils.isEmpty(provider.getName())) {
            return provider;
        }
        if (!isContentProviderInstalled(context, YellowPageContract.Provider.CONTENT_URI)) {
            return null;
        }
        Cursor c = context.getContentResolver().query(YellowPageContract.Provider.CONTENT_URI, new String[]{"name", "icon", "pid", YellowPageContract.Provider.ICON_BIG}, (String) null, (String[]) null, (String) null);
        if (c != null) {
            while (c.moveToNext()) {
                try {
                    String name = c.getString(0);
                    byte[] iconByte = c.getBlob(1);
                    Bitmap icon = iconByte == null ? null : BitmapFactory.decodeByteArray(iconByte, 0, iconByte.length);
                    byte[] iconByte2 = c.getBlob(3);
                    Bitmap iconBig = iconByte2 == null ? null : BitmapFactory.decodeByteArray(iconByte2, 0, iconByte2.length);
                    int providerId = c.getInt(2);
                    sProviders.put(Integer.valueOf(providerId), new YellowPageProvider(providerId, name, icon, iconBig));
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable th) {
                    c.close();
                    throw th;
                }
            }
            c.close();
        }
        YellowPageProvider provider2 = sProviders.get(Integer.valueOf(pid));
        if (provider2 == null) {
            return YellowPageProvider.DEFAULT_PROVIDER;
        }
        return provider2;
    }

    public static String getCidName(Context context, int cid) {
        if (sCidCategories.containsKey(Integer.valueOf(cid))) {
            AntispamCategory category = sCidCategories.get(Integer.valueOf(cid));
            if (category == null) {
                return null;
            }
            return category.getCategoryName();
        }
        getCategories(context);
        AntispamCategory category2 = sCidCategories.get(Integer.valueOf(cid));
        if (category2 == null) {
            return null;
        }
        return category2.getCategoryName();
    }

    public static List<AntispamCategory> getCategories(Context context) {
        if (!isContentProviderInstalled(context, YellowPageContract.AntispamCategory.CONTENT_URI)) {
            return Collections.emptyList();
        }
        Cursor c = context.getContentResolver().query(YellowPageContract.AntispamCategory.CONTENT_URI, new String[]{"cid", "names", "type", "icon", YellowPageContract.AntispamCategory.ORDER}, (String) null, (String[]) null, (String) null);
        if (c != null) {
            while (c.moveToNext()) {
                try {
                    int categoryId = c.getInt(0);
                    sCidCategories.put(Integer.valueOf(categoryId), new AntispamCategory(categoryId, c.getString(1), c.getInt(2), c.getString(3), c.getInt(4)));
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable th) {
                    c.close();
                    throw th;
                }
            }
            c.close();
        }
        if (sCidCategories.values() == null) {
            return null;
        }
        return new ArrayList(sCidCategories.values());
    }

    public static void markAntiSpam(Context context, String number, int categoryId, boolean delete) {
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("categoryId", Integer.valueOf(categoryId));
        values.put("delete", Boolean.valueOf(delete));
        update(context, YellowPageContract.AntispamNumber.CONTENT_MARK_NUMBER_URI, values, (String) null, (String[]) null);
    }

    public static int createAntispamCategory(Context context, String categoryName) {
        int maxCatId;
        if (TextUtils.isEmpty(categoryName)) {
            Log.d(TAG, "The category name must not be null");
        }
        if (!isContentProviderInstalled(context, YellowPageContract.AntispamCategory.CONTENT_URI)) {
            return 0;
        }
        Cursor c = context.getContentResolver().query(YellowPageContract.AntispamCategory.CONTENT_URI, new String[]{"MAX(cid)"}, (String) null, (String[]) null, (String) null);
        int categoryId = 10000;
        if (c != null) {
            try {
                if (c.moveToFirst() && (maxCatId = c.getInt(0)) >= 10000) {
                    categoryId = maxCatId + 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                c.close();
                throw th;
            }
            c.close();
        }
        ContentValues values = new ContentValues();
        values.put("cid", Integer.valueOf(categoryId));
        values.put("names", categoryName);
        values.put("type", 1);
        insert(context, YellowPageContract.AntispamCategory.CONTENT_URI, values);
        return categoryId;
    }

    public static AntispamCustomCategory getAntispamNumberCategory(Context context, String number) {
        return getAntispamNumberCategory(context, number, true);
    }

    public static AntispamCustomCategory getAntispamNumberCategory(Context context, String number, boolean filterOneRingCall) {
        Context context2 = context;
        String str = number;
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        Uri uri = Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PHONE_LOOKUP_URI, str);
        if (!isContentProviderInstalled(context2, uri)) {
            return null;
        }
        AntispamCustomCategory customCategory = null;
        Cursor c = context.getContentResolver().query(uri, ANTISPAM_PROJECTION, buildAntispamCategorySelection(3, 1, 2, 5), (String[]) null, (String) null, (CancellationSignal) null);
        if (c != null) {
            int userMarkedPosition = -1;
            int presetOrCloudPosition = -1;
            int notInPresetPosition = -1;
            while (c.moveToNext()) {
                try {
                    int i = c.getInt(2);
                    if (i == 1 || i == 2) {
                        presetOrCloudPosition = c.getPosition();
                    } else if (i == 3) {
                        userMarkedPosition = c.getPosition();
                    } else if (i == 5) {
                        notInPresetPosition = c.getPosition();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable th) {
                    c.close();
                    throw th;
                }
            }
            int position = -1;
            if (userMarkedPosition != -1) {
                position = userMarkedPosition;
                Log.d(TAG, "getAntispamNumberCategory(): found type user marked in db");
            } else if (presetOrCloudPosition != -1) {
                position = presetOrCloudPosition;
                Log.d(TAG, "getAntispamNumberCategory(): found type preset/cloud in db");
            } else if (notInPresetPosition != -1) {
                Log.d(TAG, "getAntispamNumberCategory(): found type not in preset in db");
                c.close();
                return null;
            }
            customCategory = getAntispamCategoryFromCursor(context2, c, position, str);
            if (customCategory == null) {
                Log.d(TAG, "getAntispamNumberCategory(): find nothing in db");
            }
            c.close();
        } else {
            Log.d(TAG, "getAntispamNumberCategory(): find nothing in db");
        }
        if (customCategory == null) {
            Cursor c2 = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PRESET_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, (String) null, (String[]) null, (String) null);
            if (c2 != null) {
                try {
                    customCategory = getAntispamCategoryFromCursor(context2, c2, 0, str);
                    if (customCategory != null) {
                        Log.d(TAG, "getAntispamNumberCategory(): found in preset");
                    } else {
                        Log.d(TAG, "getAntispamNumberCategory(): not found in preset");
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                } catch (Throwable th2) {
                    c2.close();
                    throw th2;
                }
                c2.close();
            } else {
                Log.d(TAG, "getAntispamNumberCategory(): find nothing in preset");
            }
        }
        if (customCategory != null) {
            Log.d(TAG, String.format("getAntispamNumberCategory(): number=%s, numberType=%s, category=%s", new Object[]{getLogNumber(number), Integer.valueOf(customCategory.getNumberType()), customCategory.getCategoryName()}));
        } else {
            Log.d(TAG, String.format("getAntispamNumberCategory(): number=%s, not found", new Object[]{getLogNumber(number)}));
        }
        return customCategory;
    }

    private static String buildAntispamCategorySelection(Integer... types) {
        StringBuilder selectionBuilder = new StringBuilder();
        for (Integer append : types) {
            if (selectionBuilder.length() > 0) {
                selectionBuilder.append(" OR ");
            }
            selectionBuilder.append("type");
            selectionBuilder.append("=");
            selectionBuilder.append(append);
        }
        selectionBuilder.insert(0, "(");
        selectionBuilder.append(")");
        return selectionBuilder.toString();
    }

    private static AntispamCustomCategory getAntispamCategoryFromCursor(Context context, Cursor c, int position, String number) {
        Cursor cursor = c;
        if (position == -1 || !c.moveToPosition(position)) {
            return null;
        }
        boolean userMarked = true;
        int categoryId = cursor.getInt(1);
        int type = cursor.getInt(2);
        int markedCount = cursor.getInt(3);
        int numberType = cursor.getInt(5);
        if (type != 3) {
            userMarked = false;
        }
        if (userMarked) {
            getCategories(context);
        }
        AntispamCategory category = sCidCategories.get(Integer.valueOf(categoryId));
        if (category != null) {
            AntispamCustomCategory customCategory = new AntispamCustomCategory(category.getCategoryId(), category.getCategoryAllNames(), category.getCategoryType(), category.getIcon(), category.getOrder(), number, markedCount, userMarked);
            customCategory.setNumberType(numberType);
            return customCategory;
        }
        return null;
    }

    public static boolean isYellowPageAvailable(Context context) {
        Locale locale = Locale.getDefault();
        boolean mainlandBuild = !Build.IS_INTERNATIONAL_BUILD && (Locale.SIMPLIFIED_CHINESE.equals(locale) || Locale.TRADITIONAL_CHINESE.equals(locale) || Locale.US.equals(locale));
        boolean indianBuild = "IN".equals(Build.getRegion());
        if (mainlandBuild || indianBuild) {
            return true;
        }
        return false;
    }

    public static boolean isYellowPageEnable(Context context) {
        return isYellowPageAvailable(context) && isCloudAntispamEnable(context);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0041 A[Catch:{ Exception -> 0x0060, all -> 0x005e }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x005a A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isInSmsWhiteList(android.content.Context r8, java.lang.String r9) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            android.net.Uri r0 = miui.yellowpage.YellowPageContract.AntispamWhiteList.CONTNET_URI
            boolean r0 = isContentProviderInstalled(r8, r0)
            if (r0 != 0) goto L_0x0011
            return r1
        L_0x0011:
            android.content.ContentResolver r2 = r8.getContentResolver()
            android.net.Uri r3 = miui.yellowpage.YellowPageContract.AntispamWhiteList.CONTNET_URI
            java.lang.String r0 = "number"
            java.lang.String[] r4 = new java.lang.String[]{r0}
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "number LIKE '"
            r0.append(r5)
            r0.append(r9)
            java.lang.String r5 = "%'"
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            r6 = 0
            r7 = 0
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7)
            if (r0 == 0) goto L_0x0069
        L_0x003b:
            boolean r2 = r0.moveToNext()     // Catch:{ Exception -> 0x0060 }
            if (r2 == 0) goto L_0x005a
            java.lang.String r2 = r0.getString(r1)     // Catch:{ Exception -> 0x0060 }
            boolean r3 = android.text.TextUtils.equals(r9, r2)     // Catch:{ Exception -> 0x0060 }
            if (r3 != 0) goto L_0x0055
            java.lang.String r3 = "106"
            boolean r3 = r9.startsWith(r3)     // Catch:{ Exception -> 0x0060 }
            if (r3 == 0) goto L_0x0054
            goto L_0x0055
        L_0x0054:
            goto L_0x003b
        L_0x0055:
            r1 = 1
            r0.close()
            return r1
        L_0x005a:
            r0.close()
            goto L_0x0069
        L_0x005e:
            r1 = move-exception
            goto L_0x0065
        L_0x0060:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x005e }
            goto L_0x005a
        L_0x0065:
            r0.close()
            throw r1
        L_0x0069:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.yellowpage.YellowPageUtils.isInSmsWhiteList(android.content.Context, java.lang.String):boolean");
    }

    public static String getIvrMenuByNumber(Context context, String number) {
        Cursor cursor;
        Uri uri = Uri.withAppendedPath(YellowPageContract.Ivr.CONTENT_URI, number);
        if (isContentProviderInstalled(context, uri) && (cursor = context.getContentResolver().query(uri, new String[]{"data"}, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
                cursor.close();
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public static boolean isIvrMenuExist(Context context, String number) {
        Cursor cursor;
        Uri uri = Uri.withAppendedPath(YellowPageContract.Ivr.CONTENT_URI, number);
        if (isContentProviderInstalled(context, uri) && (cursor = context.getContentResolver().query(uri, new String[]{"exist"}, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (cursor.moveToFirst()) {
                    boolean z = true;
                    if (cursor.getInt(0) != 1) {
                        z = false;
                    }
                    return z;
                }
                cursor.close();
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    public static boolean isRemindIgnoredUserSuspectNumber(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.REMIND_USER_SUSPECT_NUMBER);
    }

    public static boolean isFraudNumOnlineIdentificationEnabled(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.ONLINE_FRAUD_ENABLE);
    }

    public static boolean isFraudIncomingNumber(Context context, int simIndex, String incomingNumber, String yid) {
        Cursor cursor;
        Uri.Builder builder = YellowPageContract.PhoneLookup.CONTENT_FRAUD_VERIFY.buildUpon();
        builder.appendQueryParameter("simIndex", String.valueOf(simIndex));
        builder.appendQueryParameter("incoming", incomingNumber);
        builder.appendQueryParameter("yid", yid);
        Uri uri = builder.build();
        boolean z = false;
        if (isContentProviderInstalled(context, uri) && (cursor = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (cursor.moveToFirst()) {
                    if (cursor.getInt(0) > 0) {
                        z = true;
                    }
                    return z;
                }
                cursor.close();
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    public static boolean isNeverRemindSmartAntispamEnable(Context context) {
        return getBooleanSettings(context, YellowPageContract.Settings.NEVER_REMIND_ENABLE_ANTISPAM);
    }

    @Deprecated
    public static void informYellowpagePhoneEvent(Context context, long startTime, long endTime, String source, String type, int extraCallType, String extraNumber, int extraConnType) {
        JSONObject extraData = new JSONObject();
        try {
            extraData.put(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_DIRECTION, extraCallType);
            try {
                extraData.put("number", extraNumber);
            } catch (JSONException e) {
                e = e;
                Context context2 = context;
                String str = source;
                String str2 = type;
                int i = extraConnType;
                e.printStackTrace();
            }
            try {
                extraData.put(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, extraConnType);
                Uri uri = YellowPageContract.MipubPhoneEvent.CONTENT_URI_MIPUB_PHONE_EVENT.buildUpon().appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_START_TIME, String.valueOf(startTime)).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_END_TIME, String.valueOf(endTime)).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_SOURCE, source).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_TYPE, type).appendQueryParameter(YellowPageContract.MipubPhoneEvent.URI_PARAM_EXTRA_DATA, Uri.encode(extraData.toString())).build();
                if (isContentProviderInstalled(context, uri)) {
                    Cursor cursor = null;
                    try {
                        cursor = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null);
                        if (cursor == null) {
                            return;
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        if (cursor == null) {
                            return;
                        }
                    } catch (Throwable th) {
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                    cursor.close();
                }
            } catch (JSONException e3) {
                e = e3;
                Context context3 = context;
                String str3 = source;
                String str4 = type;
                e.printStackTrace();
            }
        } catch (JSONException e4) {
            e = e4;
            Context context4 = context;
            String str5 = source;
            String str6 = type;
            String str7 = extraNumber;
            int i2 = extraConnType;
            e.printStackTrace();
        }
    }

    private static boolean isCloudAntispamEnable(Context context) {
        return Settings.System.getInt(context.getContentResolver(), CLOUD_ANTISPAM, 0) == 1;
    }

    private static boolean getBooleanSettings(Context context, String key) {
        Cursor cursor;
        Uri uri = Uri.withAppendedPath(YellowPageContract.Settings.CONTENT_URI, key);
        boolean z = false;
        if (isContentProviderInstalled(context, uri) && (cursor = context.getContentResolver().query(uri, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
            try {
                if (cursor.moveToFirst()) {
                    if (cursor.getInt(0) > 0) {
                        z = true;
                    }
                    return z;
                }
                cursor.close();
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    private static void updateCloudPhoneInfo(Context context, String number) {
        update(context, Uri.withAppendedPath(YellowPageContract.PhoneLookup.CONTENT_URI_CLOUD, number), new ContentValues(), (String) null, (String[]) null);
    }

    private static YellowPagePhone buildYellowPagePhoneFromCursor(Cursor c, String number) {
        int type;
        Cursor cursor = c;
        int providerId = cursor.getInt(1);
        String tag = cursor.getString(2);
        String ypName = cursor.getString(5);
        long ypId = cursor.getLong(0);
        String ypNamePinyin = cursor.getString(6);
        String tagPinyin = cursor.getString(7);
        String normalizedNumber = cursor.getString(9);
        boolean isVisible = cursor.getInt(8) > 0;
        boolean suspect = cursor.getInt(10) > 0;
        boolean hasCallMenu = cursor.getInt(11) > 0;
        long t9Rank = cursor.getLong(12);
        int atdId = cursor.getInt(13);
        String slogan = cursor.getString(14);
        int markedCount = cursor.getInt(15);
        int atdPid = cursor.getInt(16);
        String creditImg = cursor.getString(17);
        int numberType = cursor.getInt(18);
        if (ypId != -1) {
            type = 1;
        } else if (atdId > 0) {
            type = 2;
        } else {
            type = 0;
        }
        YellowPagePhone ypPhone = new YellowPagePhone(ypId, ypName, tag, number, normalizedNumber, type, providerId, markedCount, isVisible, ypNamePinyin, tagPinyin, suspect, hasCallMenu);
        ypPhone.setT9Rank(t9Rank);
        ypPhone.setRawSlogan(slogan);
        ypPhone.setCreditImg(creditImg);
        ypPhone.setCid(atdId);
        ypPhone.setAntispamProviderId(atdPid);
        ypPhone.setNumberType(numberType);
        return ypPhone;
    }

    private static YellowPagePhone queryPhoneInfo(Context context, String number, boolean fetchRemote) {
        Context context2 = context;
        String str = number;
        Uri uri = Uri.withAppendedPath(fetchRemote ? YellowPageContract.PhoneLookup.CONTENT_URI_CLOUD : YellowPageContract.PhoneLookup.CONTENT_URI, str);
        if (!isContentProviderInstalled(context2, uri)) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, PHONE_PROJECTION, (String) null, (String[]) null, (String) null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    YellowPagePhone buildYellowPagePhoneFromCursor = buildYellowPagePhoneFromCursor(c, str);
                    c.close();
                    return buildYellowPagePhoneFromCursor;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                c.close();
                throw th;
            }
            c.close();
        }
        if (!fetchRemote) {
            Cursor c2 = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, "type<>4", (String[]) null, (String) null);
            if (c2 != null) {
                try {
                    YellowPagePhone phone = buildAntispamInfoFromCursor(context2, c2, str);
                    if (phone != null) {
                        c2.close();
                        return phone;
                    }
                    if (antispamNumberNotInPresetFile(c2)) {
                        c2.close();
                        return null;
                    }
                    c2.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                } catch (Throwable th2) {
                    c2.close();
                    throw th2;
                }
            }
            Cursor c3 = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.AntispamNumber.CONTENT_PRESET_PHONE_LOOKUP_URI, str), ANTISPAM_PROJECTION, (String) null, (String[]) null, (String) null);
            if (c3 != null) {
                try {
                    YellowPagePhone phone2 = buildAntispamInfoFromCursor(context2, c3, str);
                    if (phone2 != null) {
                        c3.close();
                        return phone2;
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                } catch (Throwable th3) {
                    c3.close();
                    throw th3;
                }
                c3.close();
            }
        }
        return null;
    }

    private static boolean antispamNumberNotInPresetFile(Cursor c) {
        if (c == null || c.getCount() == 0 || !c.moveToFirst()) {
            return false;
        }
        while (c.getInt(2) != 5) {
            if (!c.moveToNext()) {
                return false;
            }
        }
        return true;
    }

    private static YellowPagePhone buildAntispamInfoFromCursor(Context context, Cursor c, String number) {
        int i;
        int type;
        Cursor cursor = c;
        YellowPagePhone ypPhone = null;
        while (c.moveToNext()) {
            int cid = cursor.getInt(1);
            if (cid == 0) {
                Log.d(TAG, "invalid cid");
                Context context2 = context;
            } else {
                String cidName = getCidName(context, cid);
                if (TextUtils.isEmpty(cidName)) {
                    continue;
                } else {
                    int providerId = cursor.getInt(0);
                    int type2 = cursor.getInt(2) == 3 ? 3 : 2;
                    int markedCount = cursor.getInt(3);
                    String normalizedNumber = cursor.getString(4);
                    int numberType = cursor.getInt(5);
                    int type3 = type2;
                    YellowPagePhone phone = new YellowPagePhone(-1, (String) null, (2 != type2 || numberType == 0) ? cidName : Prefix.EMPTY, number, normalizedNumber, type2, providerId, markedCount, true, (String) null, (String) null, cid);
                    phone.setNumberType(numberType);
                    if (ypPhone != null) {
                        type = type3;
                        i = 3;
                        if (type != 3) {
                            continue;
                        }
                    } else {
                        type = type3;
                        i = 3;
                    }
                    ypPhone = phone;
                    if (type == i) {
                        break;
                    }
                }
            }
        }
        return ypPhone;
    }

    private static int update(Context context, Uri uri, ContentValues values, String where, String[] selectionArgs) {
        if (isContentProviderInstalled(context, uri)) {
            return context.getContentResolver().update(uri, values, where, selectionArgs);
        }
        Log.d(TAG, "update-The provider is not installed");
        return 0;
    }

    private static Uri insert(Context context, Uri uri, ContentValues values) {
        if (isContentProviderInstalled(context, uri)) {
            return context.getContentResolver().insert(uri, values);
        }
        Log.d(TAG, "insert-The provider is not installed");
        return null;
    }

    public static boolean isContentProviderInstalled(Context context, Uri uri) {
        if (context == null || uri == null) {
            return false;
        }
        ContentProviderClient client = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        if (client == null) {
            Log.e(TAG, "The content provider is not installed");
            return false;
        }
        client.release();
        return true;
    }

    public static String formatPreferenceKey(String key) {
        return String.format(Tag.TagPreference.PREF_FORMAT, new Object[]{key});
    }

    private static String getLogNumber(String number) {
        if (number == null || number.length() == 0) {
            return "[empty number]";
        }
        int len = number.length();
        StringBuilder sb = new StringBuilder();
        if (len <= 4) {
            for (int i = 0; i < len - 2; i++) {
                sb.append(number.charAt(i));
            }
            int n = Math.min(len, 2);
            for (int i2 = 0; i2 < n; i2++) {
                sb.append("*");
            }
        } else if (len == 5 || len == 6) {
            for (int i3 = 0; i3 < len - 3; i3++) {
                sb.append(number.charAt(i3));
            }
            sb.append(ExtraTelephony.PrefixCode);
        } else if (len > 6) {
            for (int i4 = 0; i4 < len - 4; i4++) {
                sb.append(number.charAt(i4));
            }
            sb.append("****");
        }
        return sb.toString();
    }
}
