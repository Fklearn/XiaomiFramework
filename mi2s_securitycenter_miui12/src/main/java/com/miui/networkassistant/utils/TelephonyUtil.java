package com.miui.networkassistant.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.ServiceState;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.c.j.y;
import b.b.o.g.c;
import com.miui.support.provider.e;
import java.util.ArrayList;
import java.util.Iterator;
import miui.securitycenter.NetworkUtils;
import miui.telephony.SmsManager;

public class TelephonyUtil {
    public static final String CMCC = "CMCC";
    private static final String KEY_CARRIER_NAME = "carrierName";
    private static final String METHOD_GET_CARRIER_NAME = "getCarrierName";
    public static final String MIMOBILE = "MIMOBILE";
    public static final int OPERATOR_CMCC = 0;
    public static final int OPERATOR_MI_MOBILE = 4;
    public static final int OPERATOR_TELCOM = 2;
    public static final int OPERATOR_UNICOM = 1;
    public static final int OPERATOR_VIRTUAL = 3;
    private static final String PREFIX_MI_MOBIE = "小米";
    private static final String TAG = "TelephonyUtil";
    public static final String TELECOM = "TELECOM";
    public static final String UNICOM = "UNICOM";
    public static final String VIRTUALOPT = "400";
    private static final String VIRTUAL_SIM_CONTENT = "content://com.miui.virtualsim.provider.virtualsimInfo";
    private static ArrayList<String> sNotSupportCorrectionList = new ArrayList<>();

    public interface PhoneNumberLoadedListener {
        void onPhoneNumberLoaded(String str);
    }

    static {
        sNotSupportCorrectionList.add("170");
        sNotSupportCorrectionList.add("171");
    }

    private TelephonyUtil() {
    }

    public static int getCurrentMobileSlotNum() {
        c.a a2 = c.a.a("miui.telephony.SubscriptionManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getDefaultDataSlotId", (Class<?>[]) null, new Object[0]);
        int c2 = a2.c();
        if (c2 < 0 || c2 > 1) {
            return 0;
        }
        return c2;
    }

    public static boolean getDataRoamingEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), getDataRoamingName(), 0) != 0;
    }

    private static String getDataRoamingName() {
        if (!y.a("radio.dataroaming.enable.suffix.subid", false) || getSimCount() == 1) {
            return "data_roaming";
        }
        return "data_roaming" + getDefaultDataSubscriptionId();
    }

    public static int getDataRoamingType(ServiceState serviceState) {
        c.a a2 = c.a.a((Object) serviceState);
        a2.a("getDataRoamingType", (Class<?>[]) null, new Object[0]);
        return a2.c();
    }

    public static int getDefaultDataSubscriptionId() {
        String str;
        int i = -1;
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                c.a a2 = c.a.a("android.telephony.SubscriptionManager");
                a2.b("getDefaultDataSubscriptionId", (Class<?>[]) null, new Object[0]);
                i = a2.c();
                str = "getDefaultDataSubscriptionId >= N, subId:" + i;
            } else {
                c.a a3 = c.a.a("android.telephony.SubscriptionManager");
                a3.b("getDefaultDataSubId", (Class<?>[]) null, new Object[0]);
                i = a3.c();
                str = "getDefaultDataSubscriptionId < N, subId:" + i;
            }
            Log.d(TAG, str);
        } catch (Exception e) {
            Log.e(TAG, "getDefaultDataSubscriptionId ", e);
        }
        return i;
    }

    public static String getImei() {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getDeviceId", (Class<?>[]) null, new Object[0]);
        return a2.f();
    }

    private static String getImsi(Context context, int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getSubscriberIdForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.f();
    }

    public static SmsMessage[] getMessagesFromIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        SmsMessage[] smsMessageArr = null;
        try {
            Object[] objArr = (Object[]) extras.get("pdus");
            Object obj = (String) extras.get("format");
            smsMessageArr = new SmsMessage[objArr.length];
            for (int i = 0; i < objArr.length; i++) {
                c.a a2 = c.a.a("android.telephony.SmsMessage");
                a2.b("createFromPdu", new Class[]{byte[].class, String.class}, (byte[]) objArr[i], obj);
                smsMessageArr[i] = (SmsMessage) a2.d();
            }
        } catch (NullPointerException e) {
            Log.i(TAG, "getMessagesFromIntent", e);
        }
        return smsMessageArr;
    }

    public static int getNetworkTypeForSlot(Context context, int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManagerEx");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getNetworkTypeForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.c();
    }

    public static int getOperator(String str, int i) {
        if (isMiMobileOperator(i)) {
            return 4;
        }
        if (str == null) {
            return -1;
        }
        if (str.startsWith("46001") || str.startsWith("46006") || str.startsWith("46009")) {
            return 1;
        }
        if (str.startsWith("46003") || str.startsWith("46005") || str.startsWith("46011")) {
            return 2;
        }
        return (str.startsWith("46000") || str.startsWith("46002") || str.startsWith("46007")) ? 0 : -1;
    }

    public static String getOperatorStr(String str, int i) {
        int operator = getOperator(str, i);
        return operator == 1 ? UNICOM : operator == 2 ? TELECOM : operator == 0 ? CMCC : operator == 4 ? MIMOBILE : "";
    }

    public static String getOperatorStr(String str, String str2, int i) {
        return isMiMobileOperator(i) ? MIMOBILE : isVirtualOperator(str2) ? VIRTUALOPT : getOperatorStr(str, i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003d, code lost:
        if (r5 == null) goto L_0x0042;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getPhoneNumber(android.content.Context r5, int r6) {
        /*
            r0 = 0
            if (r6 < 0) goto L_0x007f
            r1 = 1
            if (r6 <= r1) goto L_0x0008
            goto L_0x007f
        L_0x0008:
            com.xiaomi.accountsdk.activate.ActivateManager r5 = com.xiaomi.accountsdk.activate.ActivateManager.get(r5)     // Catch:{ Exception -> 0x0011 }
            com.xiaomi.accountsdk.activate.ActivateManager$ActivateManagerFuture r5 = r5.getActivateInfo(r6)     // Catch:{ Exception -> 0x0011 }
            goto L_0x0016
        L_0x0011:
            r5 = move-exception
            r5.printStackTrace()
            r5 = r0
        L_0x0016:
            if (r5 == 0) goto L_0x004a
            r2 = 3000(0xbb8, double:1.482E-320)
            java.util.concurrent.TimeUnit r4 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ IOException -> 0x0039, OperationCancelledException -> 0x0032, CloudServiceFailureException -> 0x002b }
            java.lang.Object r2 = r5.getResult(r2, r4)     // Catch:{ IOException -> 0x0039, OperationCancelledException -> 0x0032, CloudServiceFailureException -> 0x002b }
            android.os.Bundle r2 = (android.os.Bundle) r2     // Catch:{ IOException -> 0x0039, OperationCancelledException -> 0x0032, CloudServiceFailureException -> 0x002b }
            java.lang.String r3 = "activate_phone"
            java.lang.String r2 = r2.getString(r3)     // Catch:{ IOException -> 0x0039, OperationCancelledException -> 0x0032, CloudServiceFailureException -> 0x002b }
            goto L_0x004b
        L_0x0029:
            r6 = move-exception
            goto L_0x0044
        L_0x002b:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x0029 }
            if (r5 == 0) goto L_0x0042
            goto L_0x003f
        L_0x0032:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x0029 }
            if (r5 == 0) goto L_0x0042
            goto L_0x003f
        L_0x0039:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x0029 }
            if (r5 == 0) goto L_0x0042
        L_0x003f:
            r5.cancel(r1)
        L_0x0042:
            r2 = r0
            goto L_0x0050
        L_0x0044:
            if (r5 == 0) goto L_0x0049
            r5.cancel(r1)
        L_0x0049:
            throw r6
        L_0x004a:
            r2 = r0
        L_0x004b:
            if (r5 == 0) goto L_0x0050
            r5.cancel(r1)
        L_0x0050:
            boolean r5 = android.text.TextUtils.isEmpty(r2)
            if (r5 == 0) goto L_0x007e
            java.lang.String r5 = "miui.telephony.TelephonyManager"
            b.b.o.g.c$a r5 = b.b.o.g.c.a.a((java.lang.String) r5)
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r4 = "getDefault"
            r5.b(r4, r0, r3)
            r5.e()
            java.lang.Class[] r0 = new java.lang.Class[r1]
            java.lang.Class r3 = java.lang.Integer.TYPE
            r0[r2] = r3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r1[r2] = r6
            java.lang.String r6 = "getLine1NumberForSlot"
            r5.a(r6, r0, r1)
            java.lang.String r2 = r5.f()
        L_0x007e:
            return r2
        L_0x007f:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r1 = "illegal argument slotnum : "
            r5.append(r1)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "TelephonyUtil"
            android.util.Log.i(r6, r5)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.utils.TelephonyUtil.getPhoneNumber(android.content.Context, int):java.lang.String");
    }

    public static void getPhoneNumber(final Context context, final int i, final Handler handler, final PhoneNumberLoadedListener phoneNumberLoadedListener) {
        a.a(new Runnable() {
            public void run() {
                final String phoneNumber = TelephonyUtil.getPhoneNumber(context, i);
                if (phoneNumberLoadedListener != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            phoneNumberLoadedListener.onPhoneNumberLoaded(phoneNumber);
                        }
                    });
                }
            }
        });
    }

    public static int getSimCount() {
        c.a a2 = c.a.a("android.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getSimCount", (Class<?>[]) null, new Object[0]);
        return a2.c();
    }

    public static String getSimOperatorNameForSlot(int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getSimOperatorNameForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.f();
    }

    public static String getSimSerialNumberForSlot(int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getSimSerialNumberForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.f();
    }

    public static String getSubscriberId(Context context) {
        return getSubscriberId(context, 0);
    }

    public static String getSubscriberId(Context context, int i) {
        Log.i(TAG, "getSubscriberId， slot: " + i);
        return getImsi(context, i);
    }

    public static String getSubscriberId(Context context, int i, String str) {
        String subscriberId = getSubscriberId(context, i);
        return TextUtils.isEmpty(subscriberId) ? str : subscriberId;
    }

    public static String getVirtualSimCarrierName(Context context) {
        Bundle bundle;
        try {
            bundle = context.getContentResolver().call(Uri.parse(VIRTUAL_SIM_CONTENT), METHOD_GET_CARRIER_NAME, (String) null, (Bundle) null);
        } catch (Exception e) {
            Log.e(TAG, "getVirtualSimCarrierName e" + e);
            bundle = null;
        }
        if (bundle == null) {
            return null;
        }
        return bundle.getString(KEY_CARRIER_NAME);
    }

    public static boolean isAirModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    public static boolean isChinaOperator(int i) {
        if (i < 0 || i > 1) {
            Log.i(TAG, "illegal argument slotnum : " + i);
            return false;
        }
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("getSimOperatorForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        String f = a2.f();
        return !TextUtils.isEmpty(f) && f.startsWith("460");
    }

    public static boolean isDomesticRoamingEnable(Context context) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("isDomesticRoamingEnable", new Class[]{Context.class}, context);
        return a2.a();
    }

    public static boolean isMiMobileOperator(int i) {
        String simSerialNumberForSlot = getSimSerialNumberForSlot(i);
        if (simSerialNumberForSlot != null && simSerialNumberForSlot.length() > 11 && TextUtils.equals(simSerialNumberForSlot.substring(9, 12), "423")) {
            return true;
        }
        String simOperatorNameForSlot = getSimOperatorNameForSlot(i);
        return !TextUtils.isEmpty(simOperatorNameForSlot) && simOperatorNameForSlot.startsWith(PREFIX_MI_MOBIE);
    }

    public static boolean isNetworkRoaming(Context context, int i) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManagerEx");
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("isNetworkRoamingForSlot", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        return a2.a();
    }

    public static boolean isPhoneIdleState(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getCallState() == 0;
    }

    public static boolean isSupportDomesticRoaming() {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("isSupportDomesticRoaming", (Class<?>[]) null, new Object[0]);
        return a2.a();
    }

    public static boolean isSupportSmsCorrection(String str) {
        return !MIMOBILE.equals(str);
    }

    private static boolean isVirtualOperator(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String removePhoneNumPrefix = removePhoneNumPrefix(str, "+86");
        Iterator<String> it = sNotSupportCorrectionList.iterator();
        while (it.hasNext()) {
            if (removePhoneNumPrefix.startsWith(it.next())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVirtualSim(Context context, int i) {
        return e.d(context) && i == e.b(context);
    }

    public static String removePhoneNumPrefix(String str, String str2) {
        return (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || !str.startsWith(str2)) ? str : str.substring(str2.length());
    }

    public static void sendTextMessage(String str, String str2, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2, int i) {
        SmsManager smsManager = SmsManager.getDefault(i);
        for (String sendTextMessage : smsManager.divideMessage(str3)) {
            smsManager.sendTextMessage(str, str2, sendTextMessage, pendingIntent, pendingIntent2);
        }
    }

    public static void setDataRoamingEnabled(Context context, boolean z) {
        Settings.Global.putInt(context.getContentResolver(), getDataRoamingName(), z ? 1 : 0);
    }

    public static boolean setDomesticRoamingEnable(Context context, boolean z) {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        a2.b("setDomesticRoamingEnable", new Class[]{Context.class, Boolean.TYPE}, context, Boolean.valueOf(z));
        return a2.a();
    }

    public static void setMobileDataState(Context context, boolean z) {
        NetworkUtils.setMobileDataState(context, z);
    }

    public static boolean setPreferredNetworkType(Context context, int i, int i2) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
        if (telephonyManager != null) {
            try {
                return ((Boolean) b.b.o.g.e.a((Object) telephonyManager, "setPreferredNetworkType", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), Integer.valueOf(i2))).booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
