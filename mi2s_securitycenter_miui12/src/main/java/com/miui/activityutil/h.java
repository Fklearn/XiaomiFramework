package com.miui.activityutil;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StatFs;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.JsonWriter;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.networkassistant.config.Constants;
import com.xiaomi.security.devicecredential.SecurityDeviceCredentialManager;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import miui.cloud.CloudPushConstants;
import miui.cloud.Constants;
import miui.util.FeatureParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class h {

    /* renamed from: a  reason: collision with root package name */
    public static final String f2289a = "unknown";

    /* renamed from: b  reason: collision with root package name */
    public static final String f2290b = "default";

    /* renamed from: c  reason: collision with root package name */
    public static final String f2291c = "ota";

    /* renamed from: d  reason: collision with root package name */
    public static final String f2292d = "interval";
    public static final int e = 2;
    public static final int f = 4;
    private static final boolean h = false;
    private static final String i = "InfoHandler";
    private static final String j = aj.a("dXBfZg==");
    private static final String k = "persist.security.adbinput";
    private static final String l = "security_adb_install_enable";
    private static final String m = "com.miui.securitycenter.remoteprovider";
    private static final Uri n = Uri.parse("content://com.miui.securitycenter.remoteprovider");
    private static final String o = "com.miui.securitycenter.provider";
    private static final Uri p;
    private static final Uri q;
    private static final int r = 8;
    private static String s = "default";
    private static String[] t = {"/dev/socket/qemud", "/dev/qemu_pipe", "/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace", "/system/bin/qemu-props"};
    public Context g;

    static {
        Uri parse = Uri.parse("content://com.miui.securitycenter.provider");
        p = parse;
        q = Uri.withAppendedPath(parse, "getserinum");
    }

    public h(Context context) {
        this.g = context;
    }

    private static String A() {
        String a2 = a("ro.product.mod_device", "");
        return (a2 == null || a2.length() == 0) ? Build.DEVICE : a2;
    }

    private static String B() {
        return a("ro.miui.region", "");
    }

    private boolean C() {
        int simState = ((TelephonyManager) this.g.getSystemService("phone")).getSimState();
        return (simState == 1 || simState == 0) ? false : true;
    }

    private String D() {
        try {
            String imei = W() ? ApiCompat.getIMEI() : e.a(((TelephonyManager) this.g.getSystemService("phone")).getDeviceId());
            return !TextUtils.isEmpty(imei) ? imei : f2289a;
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String E() {
        try {
            if (W()) {
                return ApiCompat.a();
            }
            String deviceId = ((TelephonyManager) this.g.getSystemService("phone")).getDeviceId();
            if (deviceId == null) {
                return deviceId;
            }
            try {
                return e.a(deviceId);
            } catch (Exception unused) {
                return deviceId;
            }
        } catch (Exception unused2) {
            return f2289a;
        }
    }

    private static String F() {
        try {
            for (T t2 : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (t2.getName().equalsIgnoreCase("wlan0")) {
                    byte[] hardwareAddress = t2.getHardwareAddress();
                    if (hardwareAddress == null) {
                        return f2289a;
                    }
                    StringBuilder sb = new StringBuilder();
                    int length = hardwareAddress.length;
                    for (int i2 = 0; i2 < length; i2++) {
                        sb.append(String.format("%02X:", new Object[]{Byte.valueOf(hardwareAddress[i2])}));
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    return e.a(sb.toString());
                }
            }
            return f2289a;
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String G() {
        try {
            List<ScanResult> scanResults = ((WifiManager) this.g.getApplicationContext().getSystemService("wifi")).getScanResults();
            Collections.sort(scanResults, new i(this));
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < 5; i2++) {
                arrayList.add(e.b(scanResults.get(i2).BSSID));
            }
            return u.a((Iterable) arrayList, "; ");
        } catch (Exception unused) {
            return f2289a;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x0082 A[SYNTHETIC, Splitter:B:35:0x0082] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x008e A[SYNTHETIC, Splitter:B:42:0x008e] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.miui.activityutil.n H() {
        /*
            java.lang.String r0 = "failed to close reader"
            java.lang.String r1 = "InfoHandler"
            com.miui.activityutil.n r2 = new com.miui.activityutil.n
            r2.<init>()
            r3 = 0
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0077, all -> 0x0074 }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ Exception -> 0x0077, all -> 0x0074 }
            java.lang.String r6 = "/proc/cpuinfo"
            r5.<init>(r6)     // Catch:{ Exception -> 0x0077, all -> 0x0074 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x0077, all -> 0x0074 }
        L_0x0016:
            java.lang.String r3 = r4.readLine()     // Catch:{ Exception -> 0x0072 }
            if (r3 == 0) goto L_0x006e
            boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0072 }
            if (r5 != 0) goto L_0x0016
            java.lang.String r5 = ":"
            java.lang.String[] r3 = r3.split(r5)     // Catch:{ Exception -> 0x0072 }
            int r5 = r3.length     // Catch:{ Exception -> 0x0072 }
            r6 = 2
            if (r5 != r6) goto L_0x0016
            r5 = 0
            r5 = r3[r5]     // Catch:{ Exception -> 0x0072 }
            java.lang.String r5 = r5.trim()     // Catch:{ Exception -> 0x0072 }
            r6 = 1
            r3 = r3[r6]     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = r3.trim()     // Catch:{ Exception -> 0x0072 }
            java.lang.String r6 = "Processor"
            boolean r6 = android.text.TextUtils.equals(r5, r6)     // Catch:{ Exception -> 0x0072 }
            if (r6 == 0) goto L_0x0045
            r2.f2305a = r3     // Catch:{ Exception -> 0x0072 }
            goto L_0x0016
        L_0x0045:
            java.lang.String r6 = r2.f2306b     // Catch:{ Exception -> 0x0072 }
            boolean r6 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x0072 }
            if (r6 == 0) goto L_0x0058
            java.lang.String r6 = "BogoMIPS"
            boolean r6 = android.text.TextUtils.equals(r5, r6)     // Catch:{ Exception -> 0x0072 }
            if (r6 == 0) goto L_0x0058
            r2.f2306b = r3     // Catch:{ Exception -> 0x0072 }
            goto L_0x0016
        L_0x0058:
            java.lang.String r6 = "Hardware"
            boolean r6 = android.text.TextUtils.equals(r5, r6)     // Catch:{ Exception -> 0x0072 }
            if (r6 == 0) goto L_0x0063
            r2.f2307c = r3     // Catch:{ Exception -> 0x0072 }
            goto L_0x0016
        L_0x0063:
            java.lang.String r6 = "Serial"
            boolean r5 = android.text.TextUtils.equals(r5, r6)     // Catch:{ Exception -> 0x0072 }
            if (r5 == 0) goto L_0x0016
            r2.f2308d = r3     // Catch:{ Exception -> 0x0072 }
            goto L_0x0016
        L_0x006e:
            r4.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x008a
        L_0x0072:
            r3 = move-exception
            goto L_0x007b
        L_0x0074:
            r2 = move-exception
            r4 = r3
            goto L_0x008c
        L_0x0077:
            r4 = move-exception
            r7 = r4
            r4 = r3
            r3 = r7
        L_0x007b:
            java.lang.String r5 = "Error when fetch cpu info"
            android.util.Log.e(r1, r5, r3)     // Catch:{ all -> 0x008b }
            if (r4 == 0) goto L_0x008a
            r4.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x008a
        L_0x0086:
            r3 = move-exception
            android.util.Log.e(r1, r0, r3)
        L_0x008a:
            return r2
        L_0x008b:
            r2 = move-exception
        L_0x008c:
            if (r4 == 0) goto L_0x0096
            r4.close()     // Catch:{ IOException -> 0x0092 }
            goto L_0x0096
        L_0x0092:
            r3 = move-exception
            android.util.Log.e(r1, r0, r3)
        L_0x0096:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.h.H():com.miui.activityutil.n");
    }

    private static String I() {
        return a("persist.sys.fp.uid", f2289a);
    }

    private static String J() {
        return a("persist.sys.fp.vendor", f2289a);
    }

    private static String K() {
        return a("persist.sys.fp.module", f2289a);
    }

    private static String L() {
        return a("ro.ril.oem.sno", f2289a);
    }

    private static String M() {
        return a("ro.ril.oem.psno", f2289a);
    }

    private static String N() {
        return a("camera.sensor.rearMain.fuseID", f2289a);
    }

    private static String O() {
        return a("camera.sensor.rearAux.fuseID", f2289a);
    }

    private static String P() {
        return a("camera.sensor.frontMain.fuseID", f2289a);
    }

    private static String Q() {
        return a("camera.sensor.frontIR.fuseID", f2289a);
    }

    private static String R() {
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
                return f2289a;
            }
            String a2 = e.a(defaultAdapter.getAddress());
            return !TextUtils.isEmpty(a2) ? a2 : f2289a;
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String S() {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
                return f2289a;
            }
            ArrayList arrayList = new ArrayList();
            j jVar = new j(this, arrayList, countDownLatch);
            this.g.registerReceiver(jVar, new IntentFilter("android.bluetooth.device.action.FOUND"));
            defaultAdapter.startDiscovery();
            countDownLatch.await(5, TimeUnit.SECONDS);
            this.g.unregisterReceiver(jVar);
            defaultAdapter.cancelDiscovery();
            return u.a((Iterable) arrayList, "; ");
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String T() {
        String string = Settings.Secure.getString(this.g.getContentResolver(), "key_latest_accelerator_info");
        return string == null ? f2289a : string;
    }

    private static boolean U() {
        try {
            File[] listFiles = new File("/sys/block/").listFiles();
            if (listFiles == null) {
                return false;
            }
            for (File file : listFiles) {
                if (file.getName().startsWith("dm-")) {
                    File file2 = new File(file, "/dm/name");
                    if (file2.exists()) {
                        String d2 = d(file2.getAbsolutePath());
                        if (!TextUtils.isEmpty(d2) && (d2.startsWith("system") || d2.startsWith("vroot"))) {
                            return true;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    private String V() {
        TelephonyManager telephonyManager = (TelephonyManager) this.g.getSystemService("phone");
        String str = null;
        try {
            if (W()) {
                str = ApiCompat.b();
            }
            if (str == null) {
                str = telephonyManager.getSubscriberId();
            }
        } catch (Exception unused) {
        }
        return TextUtils.isEmpty(str) ? f2289a : str;
    }

    private static boolean W() {
        try {
            return Class.forName("miui.telephony.TelephonyManager").getMethod("getDefault", new Class[0]) != null;
        } catch (Exception unused) {
        }
    }

    private static String X() {
        return a("ro.secureboot.lockstate", f2289a);
    }

    private static String Y() {
        BufferedReader bufferedReader;
        FileReader fileReader;
        String str;
        StringBuffer stringBuffer = new StringBuffer("");
        FileReader fileReader2 = null;
        try {
            fileReader = new FileReader("/system/etc/hosts");
            try {
                bufferedReader = new BufferedReader(fileReader);
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        } else if (!readLine.contains("localhost") && !readLine.contains("ip6-localhost")) {
                            stringBuffer.append(readLine);
                            stringBuffer.append(",");
                        }
                    } catch (FileNotFoundException unused) {
                        fileReader2 = fileReader;
                        str = "hosts not found!";
                        try {
                            Log.d(i, str);
                            aj.a((Closeable) fileReader2);
                            aj.a((Closeable) bufferedReader);
                            return stringBuffer.toString();
                        } catch (Throwable th) {
                            th = th;
                            fileReader = fileReader2;
                            aj.a((Closeable) fileReader);
                            aj.a((Closeable) bufferedReader);
                            throw th;
                        }
                    } catch (IOException unused2) {
                        fileReader2 = fileReader;
                        str = "readLine error!";
                        Log.d(i, str);
                        aj.a((Closeable) fileReader2);
                        aj.a((Closeable) bufferedReader);
                        return stringBuffer.toString();
                    } catch (Throwable th2) {
                        th = th2;
                        aj.a((Closeable) fileReader);
                        aj.a((Closeable) bufferedReader);
                        throw th;
                    }
                }
                aj.a((Closeable) fileReader);
            } catch (FileNotFoundException unused3) {
                bufferedReader = null;
                fileReader2 = fileReader;
                str = "hosts not found!";
                Log.d(i, str);
                aj.a((Closeable) fileReader2);
                aj.a((Closeable) bufferedReader);
                return stringBuffer.toString();
            } catch (IOException unused4) {
                bufferedReader = null;
                fileReader2 = fileReader;
                str = "readLine error!";
                Log.d(i, str);
                aj.a((Closeable) fileReader2);
                aj.a((Closeable) bufferedReader);
                return stringBuffer.toString();
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = null;
                aj.a((Closeable) fileReader);
                aj.a((Closeable) bufferedReader);
                throw th;
            }
        } catch (FileNotFoundException unused5) {
            bufferedReader = null;
            str = "hosts not found!";
            Log.d(i, str);
            aj.a((Closeable) fileReader2);
            aj.a((Closeable) bufferedReader);
            return stringBuffer.toString();
        } catch (IOException unused6) {
            bufferedReader = null;
            str = "readLine error!";
            Log.d(i, str);
            aj.a((Closeable) fileReader2);
            aj.a((Closeable) bufferedReader);
            return stringBuffer.toString();
        } catch (Throwable th4) {
            th = th4;
            fileReader = null;
            bufferedReader = null;
            aj.a((Closeable) fileReader);
            aj.a((Closeable) bufferedReader);
            throw th;
        }
        aj.a((Closeable) bufferedReader);
        return stringBuffer.toString();
    }

    private String Z() {
        return Settings.Secure.getString(this.g.getContentResolver(), "android_id");
    }

    private static Boolean a(String str, Boolean bool) {
        try {
            return (Boolean) q.a("android.os.SystemProperties", "getBoolean", new Class[]{String.class, Boolean.TYPE}, str, bool);
        } catch (Exception e2) {
            e2.printStackTrace();
            return bool;
        }
    }

    private static String a(long j2) {
        if (j2 <= 0) {
            return "0B";
        }
        int i2 = 0;
        long j3 = j2;
        while (j2 != 0) {
            if (i2 == 2) {
                return j2 + "MB";
            }
            i2++;
            long j4 = j2;
            j2 /= 1024;
            j3 = j4;
        }
        if (i2 <= 1) {
            return j3 + "B";
        }
        return j3 + "KB";
    }

    private static String a(Context context) {
        try {
            return context.getResources().getConfiguration().locale.toString();
        } catch (Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }

    private String a(IntentFilter intentFilter) {
        Uri uri;
        String str;
        Intent intent = new Intent(intentFilter.getAction(0));
        if (intentFilter.countCategories() > 0 && !TextUtils.isEmpty(intentFilter.getCategory(0))) {
            intent.addCategory(intentFilter.getCategory(0));
        }
        if (intentFilter.countDataSchemes() <= 0 || TextUtils.isEmpty(intentFilter.getDataScheme(0))) {
            uri = null;
        } else {
            uri = Uri.parse(intentFilter.getDataScheme(0) + ":");
        }
        if (intentFilter.countDataTypes() <= 0 || TextUtils.isEmpty(intentFilter.getDataType(0))) {
            str = null;
        } else {
            str = intentFilter.getDataType(0);
            if (!str.contains("\\") && !str.contains("/")) {
                str = str + "/*";
            }
        }
        intent.setDataAndType(uri, str);
        ResolveInfo resolveActivity = this.g.getPackageManager().resolveActivity(intent, 0);
        if (resolveActivity == null) {
            return null;
        }
        return resolveActivity.activityInfo.packageName;
    }

    public static String a(String str, String str2) {
        try {
            return (String) q.a("android.os.SystemProperties", "get", new Class[]{String.class, String.class}, str, str2);
        } catch (Exception unused) {
            return str2;
        }
    }

    private static String a(ArrayList arrayList) {
        StringBuilder sb = new StringBuilder();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            sb.append(str);
            sb.append("=");
            sb.append(a(str, ""));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static JSONObject a(String str, JsonWriter jsonWriter) {
        return b(str, jsonWriter, v() ? "/system/lib64" : "/system/lib");
    }

    private static void a(JsonWriter jsonWriter, File file, HashMap hashMap) {
        if (file != null && file.exists()) {
            String e2 = e(file.getPath());
            if (!TextUtils.isEmpty(e2) && e2.length() == 32) {
                e2 = e2.substring(26, 32);
            }
            String name = file.getName();
            PackageInfo packageInfo = (PackageInfo) hashMap.remove(file.getAbsolutePath());
            String a2 = packageInfo != null ? packageInfo.packageName : ApiCompat.a(file);
            jsonWriter.beginObject();
            jsonWriter.name("n").value(name);
            jsonWriter.name(AnimatedProperty.PROPERTY_NAME_H).value(e2);
            jsonWriter.name("pn").value(a2);
            jsonWriter.endObject();
        }
    }

    private static void a(JsonWriter jsonWriter, String str, HashMap hashMap) {
        Vector vector = new Vector();
        vector.add(str);
        jsonWriter.beginArray();
        for (int i2 = 0; i2 < vector.size(); i2++) {
            File[] listFiles = new File((String) vector.get(i2)).listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    String path = file.getPath();
                    if (file.isDirectory()) {
                        vector.add(path);
                    } else if (path.toLowerCase().endsWith(".apk")) {
                        a(jsonWriter, file, hashMap);
                    }
                }
            }
        }
        jsonWriter.endArray();
    }

    private static void a(JsonWriter jsonWriter, HashMap hashMap) {
        jsonWriter.beginArray();
        for (Map.Entry value : new HashMap(hashMap).entrySet()) {
            PackageInfo packageInfo = (PackageInfo) value.getValue();
            if (packageInfo != null) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if ((applicationInfo.flags & 1) == 0) {
                    a(jsonWriter, new File(applicationInfo.publicSourceDir), hashMap);
                }
            }
        }
        jsonWriter.endArray();
    }

    public static void a(String str) {
        s = str;
    }

    private static void a(String str, JsonWriter jsonWriter, String str2) {
        if (new File(str2).exists()) {
            try {
                String e2 = e(str2);
                long length = new File(str2).length();
                jsonWriter.name(str);
                jsonWriter.beginObject();
                jsonWriter.name(AnimatedProperty.PROPERTY_NAME_H).value(e2);
                jsonWriter.name(CloudPushConstants.WATERMARK_TYPE.SUBSCRIPTION).value(length);
                jsonWriter.endObject();
            } catch (Exception unused) {
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:103:0x0789, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x078b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x078c, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x078e, code lost:
        r5 = false;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0779 A[Catch:{ Exception -> 0x0799, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x078b A[ExcHandler: all (r0v1 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:3:0x002c] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x079e  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0109 A[Catch:{ Exception -> 0x078e, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x01f5  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x01f7 A[SYNTHETIC, Splitter:B:31:0x01f7] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0241  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x044c A[ADDED_TO_REGION, Catch:{ Exception -> 0x078e, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x046a  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x046c  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x04ce A[Catch:{ Exception -> 0x078e, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x055e  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0569 A[Catch:{ Exception -> 0x078e, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x05bb A[Catch:{ Exception -> 0x078e, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x060b A[Catch:{ Exception -> 0x078e, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x06c8 A[Catch:{ Exception -> 0x078e, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x06ed A[Catch:{ Exception -> 0x078e, all -> 0x078b }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0757 A[Catch:{ Exception -> 0x0799, all -> 0x078b }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean a(java.io.File r23, int r24) {
        /*
            r22 = this;
            r1 = r22
            java.lang.String r2 = "/system/build.prop"
            java.lang.String r3 = "/default.prop"
            java.lang.String r4 = "com.lbe.security.miui"
            java.lang.String r5 = "com.android.updater"
            java.lang.String r6 = "/system/xbin/su"
            java.lang.String r7 = "ro.build.version.release"
            java.lang.String r8 = ""
            java.lang.String r9 = "unknown"
            com.miui.activityutil.a r10 = new com.miui.activityutil.a
            r11 = r23
            r10.<init>(r11)
            java.io.FileOutputStream r14 = r10.b()     // Catch:{ Exception -> 0x0797, all -> 0x0790 }
            java.util.zip.GZIPOutputStream r15 = new java.util.zip.GZIPOutputStream     // Catch:{ Exception -> 0x0797, all -> 0x0790 }
            r15.<init>(r14)     // Catch:{ Exception -> 0x0797, all -> 0x0790 }
            android.util.JsonWriter r14 = new android.util.JsonWriter     // Catch:{ Exception -> 0x0797, all -> 0x0790 }
            java.io.OutputStreamWriter r13 = new java.io.OutputStreamWriter     // Catch:{ Exception -> 0x0797, all -> 0x0790 }
            r13.<init>(r15)     // Catch:{ Exception -> 0x0797, all -> 0x0790 }
            r14.<init>(r13)     // Catch:{ Exception -> 0x0797, all -> 0x0790 }
            r14.beginObject()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = "pVersion"
            android.util.JsonWriter r13 = r14.name(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11 = 8
            r13.value(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "id"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = r22.c()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "time"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            long r12 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "version"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = e()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "android"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = a((java.lang.String) r7, (java.lang.String) r8)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "androidID"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r12 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.ContentResolver r12 = r12.getContentResolver()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = "android_id"
            java.lang.String r12 = android.provider.Settings.Secure.getString(r12, r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "device"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = "ro.product.mod_device"
            java.lang.String r12 = a((java.lang.String) r12, (java.lang.String) r8)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r12 == 0) goto L_0x0097
            int r13 = r12.length()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r13 != 0) goto L_0x0099
        L_0x0097:
            java.lang.String r12 = android.os.Build.DEVICE     // Catch:{ Exception -> 0x078e, all -> 0x078b }
        L_0x0099:
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "region"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = "ro.miui.region"
            java.lang.String r12 = a((java.lang.String) r12, (java.lang.String) r8)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "language"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r12 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = a((android.content.Context) r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r11 = d()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r11 != 0) goto L_0x00d3
            java.lang.String r11 = "1"
            java.lang.String r12 = "ro.miui.restrict_imei"
            java.lang.String r12 = a((java.lang.String) r12, (java.lang.String) r8)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r11 = r11.equals(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r11 != 0) goto L_0x00d3
            java.lang.String r11 = r22.D()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            goto L_0x00d4
        L_0x00d3:
            r11 = r8
        L_0x00d4:
            java.lang.String r12 = "I"
            android.util.JsonWriter r12 = r14.name(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.value(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "oaid"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r12 = com.miui.activityutil.g.a()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r12 != 0) goto L_0x00eb
        L_0x00e9:
            r12 = r8
            goto L_0x00f8
        L_0x00eb:
            android.content.Context r12 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = com.miui.activityutil.g.a(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r13 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r13 == 0) goto L_0x00f8
            goto L_0x00e9
        L_0x00f8:
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "vaid"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r12 = com.miui.activityutil.g.a()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r12 != 0) goto L_0x0109
        L_0x0107:
            r12 = r8
            goto L_0x0116
        L_0x0109:
            android.content.Context r12 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = com.miui.activityutil.g.b(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r13 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r13 == 0) goto L_0x0116
            goto L_0x0107
        L_0x0116:
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "subId"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = r22.V()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "hosts"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = Y()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            com.miui.activityutil.n r11 = H()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = "cpuId"
            android.util.JsonWriter r12 = r14.name(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = r22.j()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.value(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = "cpuType"
            android.util.JsonWriter r12 = r14.name(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = r11.f2305a     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.value(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = "cpuSpeed"
            android.util.JsonWriter r12 = r14.name(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = r11.f2306b     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.value(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = "cpuHardware"
            android.util.JsonWriter r12 = r14.name(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = r11.f2307c     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.value(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "deviceState"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = "ro.secureboot.lockstate"
            java.lang.String r12 = a((java.lang.String) r12, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "sysPartitionSize"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.<init>()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.os.StatFs r13 = new android.os.StatFs     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r15 = "/system"
            r13.<init>(r15)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            long r16 = r13.getTotalBytes()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            long r18 = r13.getFreeBytes()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            long r20 = r16 - r18
            java.lang.String r13 = "Total:"
            r12.append(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = a((long) r16)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.append(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = " Used:"
            r12.append(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = a((long) r20)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.append(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = " Free:"
            r12.append(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = a((long) r18)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.append(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "dmverityState"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = aa()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "sid"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r12 = k()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "vpn"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r12 = r22.l()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r11.value(r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "root"
            android.util.JsonWriter r11 = r14.name(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.io.File r12 = new java.io.File     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12.<init>(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r12 = c((java.io.File) r12)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r13 = "/system/bin/su"
            if (r12 == 0) goto L_0x01f7
            r13 = r6
            goto L_0x0204
        L_0x01f7:
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r6.<init>(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r6 = c((java.io.File) r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r6 == 0) goto L_0x0203
            goto L_0x0204
        L_0x0203:
            r13 = r9
        L_0x0204:
            r11.value(r13)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "upDisableState"
            android.util.JsonWriter r6 = r14.name(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r11 = r1.c((java.lang.String) r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r6.value(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "upHideState"
            android.util.JsonWriter r6 = r14.name(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r5 = r1.b((java.lang.String) r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r6.value(r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = "auDisableState"
            android.util.JsonWriter r5 = r14.name(r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r6 = r1.c((java.lang.String) r4)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r5.value(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = "auHideState"
            android.util.JsonWriter r5 = r14.name(r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r4 = r1.b((java.lang.String) r4)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r5.value(r4)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r4 = r24 & 4
            java.lang.String r5 = "ro.debuggable"
            if (r4 == 0) goto L_0x03bd
            java.lang.String r6 = "defaultProp"
            android.util.JsonWriter r6 = r14.name(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = e((java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r6.value(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "defaultContent"
            android.util.JsonWriter r6 = r14.name(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r12 = 26
            if (r11 >= r12) goto L_0x025f
            java.lang.String r3 = d((java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            goto L_0x02a7
        L_0x025f:
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.<init>()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "ro.adb.secure"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "ro.secureboot.devicelock"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "ro.secure"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "security.perf_harden"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "ro.allow.mock.location"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.add(r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "debug.atrace.tags.enableflags"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "ro.logdumpd.enabled"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "persist.sys.timezone"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "ro.bootimage.build.date"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "ro.bootimage.build.date.utc"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "ro.bootimage.build.fingerprint"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r11 = "persist.sys.usb.config"
            r3.add(r11)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = a((java.util.ArrayList) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
        L_0x02a7:
            r6.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "sysBuildProp"
            android.util.JsonWriter r3 = r14.name(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = e((java.lang.String) r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.value(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "sysBuildContent"
            android.util.JsonWriter r3 = r14.name(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r6 >= r12) goto L_0x02c7
            java.lang.String r2 = d((java.lang.String) r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            goto L_0x039b
        L_0x02c7:
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.<init>()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.id"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.display.id"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.incremental"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.sdk"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.preview_sdk"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.codename"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.all_codenames"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.add(r7)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.security_patch"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.base_os"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.date"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.date.utc"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.type"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.user"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.host"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.tags"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.flavor"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.model"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.brand"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.name"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.device"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.board"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.cpu.abi"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.cpu.abilist"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.cpu.abilist32"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.cpu.abilist64"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.product.locale"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.wifi.channels"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.board.platform"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.product"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.description"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.fingerprint"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.characteristics"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.internal"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.external"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.version.bsp"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.build.hardware.version"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.miui.version.code_time"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.rom.zone"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.miui.ui.version.code"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "ro.miui.ui.version.name"
            r2.add(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = a((java.util.ArrayList) r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
        L_0x039b:
            r3.value(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "sysXbin"
            java.lang.String r3 = "/system/xbin"
            b(r2, r14, r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "sysLib"
            boolean r3 = v()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r3 == 0) goto L_0x03b3
            java.lang.String r3 = "/system/lib64"
        L_0x03af:
            b(r2, r14, r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            goto L_0x03b6
        L_0x03b3:
            java.lang.String r3 = "/system/lib"
            goto L_0x03af
        L_0x03b6:
            java.lang.String r2 = "rootDir"
            java.lang.String r3 = "/"
            b(r2, r14, r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
        L_0x03bd:
            java.lang.String r2 = "defHome"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.IntentFilter r3 = new android.content.IntentFilter     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.<init>()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "android.intent.action.MAIN"
            r3.addAction(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "android.intent.category.HOME"
            r3.addCategory(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r1.a((android.content.IntentFilter) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "defBrowser"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.IntentFilter r3 = new android.content.IntentFilter     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.<init>()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "android.intent.action.VIEW"
            r3.addAction(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "android.intent.category.DEFAULT"
            r3.addCategory(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "http"
            r3.addDataScheme(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "https"
            r3.addDataScheme(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r1.a((android.content.IntentFilter) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "defMusic"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.p()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "defSMS"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.IntentFilter r3 = new android.content.IntentFilter     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.<init>()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "android.intent.action.SENDTO"
            r3.addAction(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "smsto"
            r3.addDataScheme(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r1.a((android.content.IntentFilter) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "defGallery"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.r()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "sim"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "phone"
            java.lang.Object r3 = r3.getSystemService(r6)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.telephony.TelephonyManager r3 = (android.telephony.TelephonyManager) r3     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            int r3 = r3.getSimState()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r6 = 1
            if (r3 == r6) goto L_0x0450
            if (r3 == 0) goto L_0x0450
            r3 = 1
            goto L_0x0451
        L_0x0450:
            r3 = 0
        L_0x0451:
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "usbDebugState"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r6 = "adb_enabled"
            r7 = 0
            int r3 = android.provider.Settings.Global.getInt(r3, r6, r7)     // Catch:{ Exception -> 0x0789, all -> 0x078b }
            r6 = 1
            if (r3 != r6) goto L_0x046c
            r3 = r6
            goto L_0x046d
        L_0x046c:
            r3 = 0
        L_0x046d:
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "usbInstallState"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r3 = r22.t()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "usbSDebugState"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "persist.security.adbinput"
            java.lang.Boolean r7 = java.lang.Boolean.FALSE     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.Boolean r3 = a((java.lang.String) r3, (java.lang.Boolean) r7)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r3 = r3.booleanValue()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "fpUid"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "persist.sys.fp.uid"
            java.lang.String r3 = a((java.lang.String) r3, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "fpVendor"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "persist.sys.fp.vendor"
            java.lang.String r3 = a((java.lang.String) r3, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "fpModule"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "persist.sys.fp.module"
            java.lang.String r3 = a((java.lang.String) r3, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r2 = U()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "dmVerityEnabled"
            android.util.JsonWriter r3 = r14.name(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.value(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r2 == 0) goto L_0x04dd
            java.lang.String r2 = "dmVerityLog"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "/data/miui/security/dm_verity"
            java.lang.String r3 = d((java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
        L_0x04dd:
            java.lang.String r2 = "memIdEmmc"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "/sys/class/block/mmcblk0/device/serial"
            java.lang.String r3 = d((java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "memIdUfs"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "/d/ufshcd0/dump_string_desc_serial"
            java.lang.String r3 = d((java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "FSN"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "ro.ril.oem.sno"
            java.lang.String r3 = a((java.lang.String) r3, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "PSN"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "ro.ril.oem.psno"
            java.lang.String r3 = a((java.lang.String) r3, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "cameraRearMainFuseID"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "camera.sensor.rearMain.fuseID"
            java.lang.String r3 = a((java.lang.String) r3, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "cameraRearAuxFuseID"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "camera.sensor.rearAux.fuseID"
            java.lang.String r3 = a((java.lang.String) r3, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "cameraFrontMainFuseID"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "camera.sensor.frontMain.fuseID"
            java.lang.String r3 = a((java.lang.String) r3, (java.lang.String) r9)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "frameworkJar"
            java.lang.String r3 = "/system/framework/framework.jar"
            a((java.lang.String) r2, (android.util.JsonWriter) r14, (java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "serviceJar"
            java.lang.String r3 = "/system/framework/services.jar"
            a((java.lang.String) r2, (android.util.JsonWriter) r14, (java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r2 = v()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "bootArt"
            java.lang.String r7 = "bootOat"
            if (r2 == 0) goto L_0x0569
            java.lang.String r2 = "/system/framework/arm64/boot.oat"
            a((java.lang.String) r7, (android.util.JsonWriter) r14, (java.lang.String) r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "/system/framework/arm64/boot.art"
        L_0x0565:
            a((java.lang.String) r3, (android.util.JsonWriter) r14, (java.lang.String) r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            goto L_0x0571
        L_0x0569:
            java.lang.String r2 = "/system/framework/arm/boot.oat"
            a((java.lang.String) r7, (android.util.JsonWriter) r14, (java.lang.String) r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "/system/framework/arm/boot.art"
            goto L_0x0565
        L_0x0571:
            java.lang.String r2 = "roDebuggable"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = a((java.lang.String) r5, (java.lang.String) r8)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "sysPartitionModify"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = x()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "source"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.pm.ApplicationInfo r3 = r3.getApplicationInfo()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r3.packageName     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "trigger"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = s     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "GyroscopeInfo"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = "key_latest_gyroscope_info"
            java.lang.String r3 = android.provider.Settings.Secure.getString(r3, r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r3 != 0) goto L_0x05bc
            r3 = r9
        L_0x05bc:
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "isXiaomiAccountLogin"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            boolean r3 = f(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "batteryLeft"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.ae()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "lightSensor"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r1.b((android.content.Context) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "sensorList"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.af()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "accelerometer"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = "key_latest_accelerator_info"
            java.lang.String r3 = android.provider.Settings.Secure.getString(r3, r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r3 != 0) goto L_0x060c
            r3 = r9
        L_0x060c:
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "screenRes"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.ag()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "totalStorage"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.ah()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "memory"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = ai()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "deviceConfig"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.al()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "barrierFreeApps"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.ao()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "deviceManageApps"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.ap()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "emulatorFiles"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = aq()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "signal"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = r22.am()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "sdcard"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = aj()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            com.miui.activityutil.o r2 = r22.ar()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "screenPower"
            android.util.JsonWriter r3 = r14.name(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = r2.l     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.value(r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "appPower"
            android.util.JsonWriter r3 = r14.name(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = r2.o     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.value(r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "idlePower"
            android.util.JsonWriter r3 = r14.name(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = r2.m     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.value(r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "callPower"
            android.util.JsonWriter r3 = r14.name(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = r2.n     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r3.value(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "compass"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = "key_latest_magnetic_info"
            java.lang.String r3 = android.provider.Settings.Secure.getString(r3, r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r3 != 0) goto L_0x06c9
            r3 = r9
        L_0x06c9:
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "isAgent"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = at()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "batteryStatus"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.Context r3 = r1.g     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r5 = "key_latest_battery_status"
            java.lang.String r3 = android.provider.Settings.Secure.getString(r3, r5)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            if (r3 != 0) goto L_0x06ee
            r3 = r9
        L_0x06ee:
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "cpuMax"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"
            java.lang.String r3 = d((java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "cpuMin"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"
            java.lang.String r3 = d((java.lang.String) r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r2 = "isMtk"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x078e, all -> 0x078b }
            java.lang.String r3 = "is_mediatek"
            r5 = 0
            boolean r3 = miui.util.FeatureParser.getBoolean(r3, r5)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r2 = "manufacturer"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = android.os.Build.MANUFACTURER     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r2 = "phoneType"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = r22.aw()     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r2 = "simOperator"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = r22.ax()     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r2 = "netType"
            android.util.JsonWriter r2 = r14.name(r2)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = r22.ay()     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            r2.value(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.util.HashMap r2 = r22.i()     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            if (r4 == 0) goto L_0x0775
            java.lang.String r3 = "sysAppDetail"
            r14.name(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = "/system/app"
            a((android.util.JsonWriter) r14, (java.lang.String) r3, (java.util.HashMap) r2)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = "sysPrivappDetail"
            r14.name(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = "/system/priv-app"
            a((android.util.JsonWriter) r14, (java.lang.String) r3, (java.util.HashMap) r2)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = "dataMiuiDetail"
            r14.name(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            java.lang.String r3 = "data/miui"
            a((android.util.JsonWriter) r14, (java.lang.String) r3, (java.util.HashMap) r2)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
        L_0x0775:
            r3 = r24 & 2
            if (r3 == 0) goto L_0x0781
            java.lang.String r3 = "dataApps"
            r14.name(r3)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            a((android.util.JsonWriter) r14, (java.util.HashMap) r2)     // Catch:{ Exception -> 0x0799, all -> 0x078b }
        L_0x0781:
            r14.endObject()     // Catch:{ Exception -> 0x0799, all -> 0x078b }
            com.miui.activityutil.aj.a((java.io.Closeable) r14)
            r5 = r6
            goto L_0x079c
        L_0x0789:
            r5 = r7
            goto L_0x0799
        L_0x078b:
            r0 = move-exception
            r2 = r0
            goto L_0x0793
        L_0x078e:
            r5 = 0
            goto L_0x0799
        L_0x0790:
            r0 = move-exception
            r2 = r0
            r14 = 0
        L_0x0793:
            com.miui.activityutil.aj.a((java.io.Closeable) r14)
            throw r2
        L_0x0797:
            r5 = 0
            r14 = 0
        L_0x0799:
            com.miui.activityutil.aj.a((java.io.Closeable) r14)
        L_0x079c:
            if (r5 == 0) goto L_0x07a2
            r10.c()
            goto L_0x07a5
        L_0x07a2:
            r10.a()
        L_0x07a5:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.h.a(java.io.File, int):boolean");
    }

    public static boolean a(File file, byte[] bArr) {
        FileOutputStream fileOutputStream;
        if (!(file == null || bArr == null)) {
            FileOutputStream fileOutputStream2 = null;
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (Exception unused) {
                aj.a((Closeable) fileOutputStream2);
                return false;
            } catch (Throwable th) {
                th = th;
                aj.a((Closeable) fileOutputStream2);
                throw th;
            }
            try {
                fileOutputStream.write(bArr);
                aj.a((Closeable) fileOutputStream);
                return true;
            } catch (Exception unused2) {
                fileOutputStream2 = fileOutputStream;
                aj.a((Closeable) fileOutputStream2);
                return false;
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream2 = fileOutputStream;
                aj.a((Closeable) fileOutputStream2);
                throw th;
            }
        }
        return false;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: byte[]} */
    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v2 */
    /* JADX WARNING: type inference failed for: r0v3, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v7 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] a(java.io.File r5) {
        /*
            r0 = 0
            if (r5 == 0) goto L_0x0041
            boolean r1 = r5.exists()
            if (r1 != 0) goto L_0x000a
            goto L_0x0041
        L_0x000a:
            java.io.ByteArrayOutputStream r1 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0039, all -> 0x0030 }
            r1.<init>()     // Catch:{ Exception -> 0x0039, all -> 0x0030 }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ Exception -> 0x002e, all -> 0x002c }
            r2.<init>(r5)     // Catch:{ Exception -> 0x002e, all -> 0x002c }
            r5 = 4096(0x1000, float:5.74E-42)
            byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x003b, all -> 0x0029 }
        L_0x0018:
            int r3 = r2.read(r5)     // Catch:{ Exception -> 0x003b, all -> 0x0029 }
            r4 = -1
            if (r3 == r4) goto L_0x0024
            r4 = 0
            r1.write(r5, r4, r3)     // Catch:{ Exception -> 0x003b, all -> 0x0029 }
            goto L_0x0018
        L_0x0024:
            byte[] r0 = r1.toByteArray()     // Catch:{ Exception -> 0x003b, all -> 0x0029 }
            goto L_0x003b
        L_0x0029:
            r5 = move-exception
            r0 = r2
            goto L_0x0032
        L_0x002c:
            r5 = move-exception
            goto L_0x0032
        L_0x002e:
            r2 = r0
            goto L_0x003b
        L_0x0030:
            r5 = move-exception
            r1 = r0
        L_0x0032:
            com.miui.activityutil.aj.a((java.io.Closeable) r0)
            com.miui.activityutil.aj.a((java.io.Closeable) r1)
            throw r5
        L_0x0039:
            r1 = r0
            r2 = r1
        L_0x003b:
            com.miui.activityutil.aj.a((java.io.Closeable) r2)
            com.miui.activityutil.aj.a((java.io.Closeable) r1)
        L_0x0041:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.h.a(java.io.File):byte[]");
    }

    private static String aa() {
        BufferedReader bufferedReader;
        String str;
        String str2 = "";
        FileReader fileReader = null;
        try {
            FileReader fileReader2 = new FileReader("/proc/mounts");
            try {
                bufferedReader = new BufferedReader(fileReader2);
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine != null) {
                            int lastIndexOf = readLine.lastIndexOf("/system");
                            if (lastIndexOf > 0) {
                                str2 = readLine.substring(0, lastIndexOf - 1).trim();
                                break;
                            }
                        } else {
                            break;
                        }
                    } catch (FileNotFoundException unused) {
                        fileReader = fileReader2;
                        str = "mounts file not found!";
                        try {
                            Log.d(i, str);
                            aj.a((Closeable) fileReader);
                            aj.a((Closeable) bufferedReader);
                            return str2;
                        } catch (Throwable th) {
                            th = th;
                            aj.a((Closeable) fileReader);
                            aj.a((Closeable) bufferedReader);
                            throw th;
                        }
                    } catch (IOException unused2) {
                        fileReader = fileReader2;
                        str = "readLine error!";
                        Log.d(i, str);
                        aj.a((Closeable) fileReader);
                        aj.a((Closeable) bufferedReader);
                        return str2;
                    } catch (Throwable th2) {
                        th = th2;
                        fileReader = fileReader2;
                        aj.a((Closeable) fileReader);
                        aj.a((Closeable) bufferedReader);
                        throw th;
                    }
                }
                aj.a((Closeable) fileReader2);
            } catch (FileNotFoundException unused3) {
                bufferedReader = null;
                fileReader = fileReader2;
                str = "mounts file not found!";
                Log.d(i, str);
                aj.a((Closeable) fileReader);
                aj.a((Closeable) bufferedReader);
                return str2;
            } catch (IOException unused4) {
                bufferedReader = null;
                fileReader = fileReader2;
                str = "readLine error!";
                Log.d(i, str);
                aj.a((Closeable) fileReader);
                aj.a((Closeable) bufferedReader);
                return str2;
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = null;
                fileReader = fileReader2;
                aj.a((Closeable) fileReader);
                aj.a((Closeable) bufferedReader);
                throw th;
            }
        } catch (FileNotFoundException unused5) {
            bufferedReader = null;
            str = "mounts file not found!";
            Log.d(i, str);
            aj.a((Closeable) fileReader);
            aj.a((Closeable) bufferedReader);
            return str2;
        } catch (IOException unused6) {
            bufferedReader = null;
            str = "readLine error!";
            Log.d(i, str);
            aj.a((Closeable) fileReader);
            aj.a((Closeable) bufferedReader);
            return str2;
        } catch (Throwable th4) {
            th = th4;
            bufferedReader = null;
            aj.a((Closeable) fileReader);
            aj.a((Closeable) bufferedReader);
            throw th;
        }
        aj.a((Closeable) bufferedReader);
        return str2;
    }

    private static String ab() {
        StringBuilder sb = new StringBuilder();
        StatFs statFs = new StatFs("/system");
        long totalBytes = statFs.getTotalBytes();
        long freeBytes = statFs.getFreeBytes();
        sb.append("Total:");
        sb.append(a(totalBytes));
        sb.append(" Used:");
        sb.append(a(totalBytes - freeBytes));
        sb.append(" Free:");
        sb.append(a(freeBytes));
        return sb.toString();
    }

    private static String ac() {
        if (Build.VERSION.SDK_INT < 26) {
            return d("/default.prop");
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("ro.adb.secure");
        arrayList.add("ro.secureboot.devicelock");
        arrayList.add("ro.secure");
        arrayList.add("security.perf_harden");
        arrayList.add("ro.allow.mock.location");
        arrayList.add("ro.debuggable");
        arrayList.add("debug.atrace.tags.enableflags");
        arrayList.add("ro.logdumpd.enabled");
        arrayList.add("persist.sys.timezone");
        arrayList.add("ro.bootimage.build.date");
        arrayList.add("ro.bootimage.build.date.utc");
        arrayList.add("ro.bootimage.build.fingerprint");
        arrayList.add("persist.sys.usb.config");
        return a(arrayList);
    }

    private static String ad() {
        if (Build.VERSION.SDK_INT < 26) {
            return d("/system/build.prop");
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("ro.build.id");
        arrayList.add("ro.build.display.id");
        arrayList.add("ro.build.version.incremental");
        arrayList.add("ro.build.version.sdk");
        arrayList.add("ro.build.version.preview_sdk");
        arrayList.add("ro.build.version.codename");
        arrayList.add("ro.build.version.all_codenames");
        arrayList.add("ro.build.version.release");
        arrayList.add("ro.build.version.security_patch");
        arrayList.add("ro.build.version.base_os");
        arrayList.add("ro.build.date");
        arrayList.add("ro.build.date.utc");
        arrayList.add("ro.build.type");
        arrayList.add("ro.build.user");
        arrayList.add("ro.build.host");
        arrayList.add("ro.build.tags");
        arrayList.add("ro.build.flavor");
        arrayList.add("ro.product.model");
        arrayList.add("ro.product.brand");
        arrayList.add("ro.product.name");
        arrayList.add("ro.product.device");
        arrayList.add("ro.product.board");
        arrayList.add("ro.product.cpu.abi");
        arrayList.add("ro.product.cpu.abilist");
        arrayList.add("ro.product.cpu.abilist32");
        arrayList.add("ro.product.cpu.abilist64");
        arrayList.add("ro.product.locale");
        arrayList.add("ro.wifi.channels");
        arrayList.add("ro.board.platform");
        arrayList.add("ro.build.product");
        arrayList.add("ro.build.description");
        arrayList.add("ro.build.fingerprint");
        arrayList.add("ro.build.characteristics");
        arrayList.add("ro.build.version.internal");
        arrayList.add("ro.build.version.external");
        arrayList.add("ro.build.version.bsp");
        arrayList.add("ro.build.hardware.version");
        arrayList.add("ro.miui.version.code_time");
        arrayList.add("ro.rom.zone");
        arrayList.add("ro.miui.ui.version.code");
        arrayList.add("ro.miui.ui.version.name");
        return a(arrayList);
    }

    private String ae() {
        try {
            int intProperty = ((BatteryManager) this.g.getSystemService("batterymanager")).getIntProperty(4);
            return intProperty + "%";
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String af() {
        StringBuilder sb;
        String name;
        String str = "";
        try {
            for (Sensor next : ((SensorManager) this.g.getSystemService("sensor")).getSensorList(-1)) {
                if (TextUtils.isEmpty(str)) {
                    sb = new StringBuilder();
                    sb.append(str);
                    name = next.getName();
                } else {
                    sb = new StringBuilder();
                    sb.append(str);
                    sb.append(",");
                    name = next.getName();
                }
                sb.append(name);
                str = sb.toString();
            }
            return str;
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String ag() {
        try {
            DisplayMetrics displayMetrics = this.g.getResources().getDisplayMetrics();
            return displayMetrics.heightPixels + AnimatedProperty.PROPERTY_NAME_X + displayMetrics.widthPixels;
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String ah() {
        try {
            return String.valueOf((int) (r.a(this.g).f2324a / C.NANOS_PER_SECOND));
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private static String ai() {
        try {
            Method method = Class.forName("miui.util.HardwareInfo").getMethod("getTotalPhysicalMemory", new Class[0]);
            method.setAccessible(true);
            return String.valueOf((int) (((Long) method.invoke((Object) null, new Object[0])).longValue() / 1073741824));
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private static String aj() {
        try {
            return String.valueOf(new File("/sdcard").exists());
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String ak() {
        try {
            WifiInfo connectionInfo = ((WifiManager) this.g.getApplicationContext().getSystemService("wifi")).getConnectionInfo();
            String valueOf = connectionInfo == null ? null : String.valueOf(connectionInfo.getIpAddress());
            return !TextUtils.isEmpty(valueOf) ? valueOf : f2289a;
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String al() {
        try {
            ConfigurationInfo deviceConfigurationInfo = ((ActivityManager) this.g.getSystemService("activity")).getDeviceConfigurationInfo();
            return deviceConfigurationInfo != null ? deviceConfigurationInfo.toString() : "";
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String am() {
        try {
            StringBuilder sb = new StringBuilder();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            HandlerThread handlerThread = new HandlerThread("signal_working_thread");
            handlerThread.start();
            new Handler(handlerThread.getLooper()).post(new l(this, sb, countDownLatch));
            countDownLatch.await(5, TimeUnit.SECONDS);
            return sb.toString();
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String an() {
        try {
            Account[] accountsByType = AccountManager.get(this.g).getAccountsByType(Constants.XIAOMI_ACCOUNT_TYPE);
            return accountsByType.length > 0 ? e.a(accountsByType[0].name) : f2289a;
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String ao() {
        Set set;
        try {
            List<AccessibilityServiceInfo> installedAccessibilityServiceList = ((AccessibilityManager) q.a("android.view.accessibility.AccessibilityManager", "getInstance", new Class[]{Context.class}, this.g)).getInstalledAccessibilityServiceList();
            boolean z = Settings.Secure.getInt(this.g.getContentResolver(), "accessibility_enabled", 0) == 1;
            String string = Settings.Secure.getString(this.g.getContentResolver(), "enabled_accessibility_services");
            if (string == null) {
                set = Collections.emptySet();
            } else {
                HashSet hashSet = new HashSet();
                TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
                simpleStringSplitter.setString(string);
                while (simpleStringSplitter.hasNext()) {
                    ComponentName unflattenFromString = ComponentName.unflattenFromString(simpleStringSplitter.next());
                    if (unflattenFromString != null) {
                        hashSet.add(unflattenFromString);
                    }
                }
                set = hashSet;
            }
            JSONObject jSONObject = new JSONObject();
            int size = installedAccessibilityServiceList.size();
            for (int i2 = 0; i2 < size; i2++) {
                ServiceInfo serviceInfo = installedAccessibilityServiceList.get(i2).getResolveInfo().serviceInfo;
                jSONObject.put(serviceInfo.packageName, z && set.contains(new ComponentName(serviceInfo.packageName, serviceInfo.name)));
            }
            return jSONObject.toString();
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String ap() {
        StringBuilder sb;
        String packageName;
        String str = "";
        try {
            List<ComponentName> activeAdmins = ((DevicePolicyManager) this.g.getSystemService("device_policy")).getActiveAdmins();
            if (activeAdmins != null) {
                for (ComponentName next : activeAdmins) {
                    if (TextUtils.isEmpty(str)) {
                        sb = new StringBuilder();
                        sb.append(str);
                        packageName = next.getPackageName();
                    } else {
                        sb = new StringBuilder();
                        sb.append(str);
                        sb.append(",");
                        packageName = next.getPackageName();
                    }
                    sb.append(packageName);
                    str = sb.toString();
                }
            }
            return str;
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private static String aq() {
        StringBuilder sb;
        String str = "";
        int i2 = 0;
        while (i2 < t.length) {
            try {
                String str2 = t[i2];
                if (new File(str2).exists()) {
                    if (TextUtils.isEmpty(str)) {
                        sb = new StringBuilder();
                        sb.append(str);
                    } else {
                        sb = new StringBuilder();
                        sb.append(str);
                        sb.append("; ");
                    }
                    sb.append(str2);
                    str = sb.toString();
                }
                i2++;
            } catch (Exception unused) {
                return f2289a;
            }
        }
        return str;
    }

    private o ar() {
        o oVar = new o();
        try {
            Bundle call = this.g.getContentResolver().call(Uri.parse("content://com.miui.powercenter.provider"), "getAppAndHardwarePowerConsume", (String) null, (Bundle) null);
            if (call != null && call.containsKey("app_and_hardware_consume")) {
                JSONObject jSONObject = new JSONObject(call.getString("app_and_hardware_consume"));
                oVar.o = jSONObject.getJSONArray("app_consume_list").toString();
                JSONArray jSONArray = jSONObject.getJSONArray("hardware_consume_list");
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                    if (jSONObject2.has(o.f2309a)) {
                        oVar.m = String.valueOf(jSONObject2.getDouble(o.f2309a));
                    } else if (jSONObject2.has(o.f)) {
                        oVar.l = String.valueOf(jSONObject2.getDouble(o.f));
                    } else if (jSONObject2.has("2")) {
                        oVar.n = String.valueOf(jSONObject2.getDouble("2"));
                    }
                }
            }
        } catch (Exception e2) {
            Log.e(i, "Error when fetch power info", e2);
        }
        return oVar;
    }

    private String as() {
        String string = Settings.Secure.getString(this.g.getContentResolver(), "key_latest_magnetic_info");
        return string == null ? f2289a : string;
    }

    private static String at() {
        try {
            String property = System.getProperty("http.proxyHost");
            String property2 = System.getProperty("http.proxyPort");
            if (property2 == null) {
                property2 = "-1";
            }
            return String.valueOf(!TextUtils.isEmpty(property) && Integer.parseInt(property2) != -1);
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String au() {
        String string = Settings.Secure.getString(this.g.getContentResolver(), "key_latest_battery_status");
        return string == null ? f2289a : string;
    }

    private static boolean av() {
        return FeatureParser.getBoolean("is_mediatek", false);
    }

    private String aw() {
        try {
            int phoneType = ((TelephonyManager) this.g.getSystemService("phone")).getPhoneType();
            return phoneType != 1 ? phoneType != 2 ? phoneType != 3 ? "NONE" : "SIP" : "CDMA" : "GSM";
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String ax() {
        try {
            return ((TelephonyManager) this.g.getSystemService("phone")).getSimOperatorName();
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String ay() {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.g.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                return f2289a;
            }
            int type = activeNetworkInfo.getType();
            if (type != 0) {
                if (type == 1) {
                    return "WIFI";
                }
                if (!(type == 2 || type == 3 || type == 4 || type == 5)) {
                    return f2289a;
                }
            }
            Object a2 = q.a("miui.telephony.TelephonyManagerEx", "getDefault", (Class[]) null, new Object[0]);
            int intValue = ((Integer) q.b(a2, "getNetworkClass", new Class[]{Integer.TYPE}, Integer.valueOf(((Integer) q.b(a2, "getNetworkType", (Class[]) null, new Object[0])).intValue()))).intValue();
            return intValue != 1 ? intValue != 2 ? intValue != 3 ? "type_mobile_unknown" : "4G" : "3G" : "2G";
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private String b(Context context) {
        try {
            StringBuilder sb = new StringBuilder();
            CountDownLatch countDownLatch = new CountDownLatch(1);
            SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            Sensor defaultSensor = sensorManager.getDefaultSensor(5);
            k kVar = new k(this, sb, countDownLatch);
            sensorManager.registerListener(kVar, defaultSensor, 3);
            countDownLatch.await(3, TimeUnit.SECONDS);
            sensorManager.unregisterListener(kVar);
            return sb.toString();
        } catch (Exception unused) {
            return f2289a;
        }
    }

    private static String b(File file) {
        return ApiCompat.a(file);
    }

    private static JSONObject b(String str, JsonWriter jsonWriter, String str2) {
        try {
            File[] listFiles = new File(str2).listFiles();
            if (listFiles == null) {
                return null;
            }
            jsonWriter.name(str);
            jsonWriter.beginObject();
            for (File file : listFiles) {
                jsonWriter.name(file.getName()).value(file.isFile() ? file.length() : 0);
            }
            jsonWriter.endObject();
            return null;
        } catch (Exception e2) {
            Log.d(i, "getPathFiles error!");
            e2.printStackTrace();
            return null;
        }
    }

    private boolean b(String str) {
        return ApiCompat.a(this.g, str);
    }

    private static String c(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "key_latest_gyroscope_info");
        return string == null ? f2289a : string;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0030 A[SYNTHETIC, Splitter:B:23:0x0030] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0036 A[SYNTHETIC, Splitter:B:28:0x0036] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean c(java.io.File r3) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 < r1) goto L_0x003a
            r0 = 0
            r1 = 0
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x001d }
            r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x001d }
            boolean r3 = r3.exists()     // Catch:{ FileNotFoundException -> 0x0018, all -> 0x0015 }
            r2.close()     // Catch:{ IOException -> 0x0033 }
            goto L_0x0033
        L_0x0015:
            r3 = move-exception
            r1 = r2
            goto L_0x0034
        L_0x0018:
            r3 = move-exception
            r1 = r2
            goto L_0x001e
        L_0x001b:
            r3 = move-exception
            goto L_0x0034
        L_0x001d:
            r3 = move-exception
        L_0x001e:
            java.lang.String r3 = r3.getMessage()     // Catch:{ all -> 0x001b }
            if (r3 == 0) goto L_0x002d
            java.lang.String r2 = "denied"
            boolean r3 = r3.contains(r2)     // Catch:{ all -> 0x001b }
            if (r3 == 0) goto L_0x002d
            r0 = 1
        L_0x002d:
            r3 = r0
            if (r1 == 0) goto L_0x0033
            r1.close()     // Catch:{ IOException -> 0x0033 }
        L_0x0033:
            return r3
        L_0x0034:
            if (r1 == 0) goto L_0x0039
            r1.close()     // Catch:{ IOException -> 0x0039 }
        L_0x0039:
            throw r3
        L_0x003a:
            boolean r3 = r3.exists()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.h.c(java.io.File):boolean");
    }

    private boolean c(String str) {
        try {
            return this.g.getPackageManager().getApplicationEnabledSetting(str) == 2;
        } catch (IllegalArgumentException unused) {
            return true;
        }
    }

    private static String d(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "key_latest_gps_info");
        return string == null ? f2289a : string;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0005, code lost:
        r2 = a(new java.io.File(r2));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String d(java.lang.String r2) {
        /*
            java.lang.String r0 = ""
            if (r2 != 0) goto L_0x0005
            return r0
        L_0x0005:
            java.io.File r1 = new java.io.File
            r1.<init>(r2)
            byte[] r2 = a((java.io.File) r1)
            if (r2 != 0) goto L_0x0011
            return r0
        L_0x0011:
            java.lang.String r0 = new java.lang.String
            r0.<init>(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.h.d(java.lang.String):java.lang.String");
    }

    public static boolean d() {
        try {
            return ((String) q.a("android.os.SystemProperties", "get", new Class[]{String.class, String.class}, "ro.product.mod_device", "")).contains("_global");
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static String e() {
        String str = Build.VERSION.INCREMENTAL;
        return (str == null || str.length() == 0) ? a("ro.build.version.incremental", "") : str;
    }

    private static String e(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "key_latest_wifi_ssid_bssid");
        return string == null ? f2289a : string;
    }

    private static String e(String str) {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
            fileInputStream = new FileInputStream(new File(str));
            while (true) {
                try {
                    int read = fileInputStream.read(bArr);
                    if (read > 0) {
                        instance.update(bArr, 0, read);
                    } else {
                        String replace = String.format("%32s", new Object[]{new BigInteger(1, instance.digest()).toString(16)}).replace(' ', '0');
                        aj.a((Closeable) fileInputStream);
                        return replace;
                    }
                } catch (Exception unused) {
                    fileInputStream2 = fileInputStream;
                    aj.a((Closeable) fileInputStream2);
                    return "";
                } catch (Throwable th) {
                    th = th;
                    aj.a((Closeable) fileInputStream);
                    throw th;
                }
            }
        } catch (Exception unused2) {
            aj.a((Closeable) fileInputStream2);
            return "";
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
            aj.a((Closeable) fileInputStream);
            throw th;
        }
    }

    private static boolean f() {
        return o.f2310b.equals(a("ro.miui.restrict_imei", ""));
    }

    private static boolean f(Context context) {
        Account account = null;
        try {
            Account[] accountsByType = AccountManager.get(context).getAccountsByType(Constants.XIAOMI_ACCOUNT_TYPE);
            if (accountsByType.length > 0) {
                account = accountsByType[0];
            }
            return account != null;
        } catch (Exception unused) {
        }
    }

    private String g() {
        if (!g.a()) {
            return "";
        }
        String a2 = g.a(this.g);
        return TextUtils.isEmpty(a2) ? "" : a2;
    }

    private static String g(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor != null) {
                String valueOf = String.valueOf(cursor.getCount());
                if (cursor != null) {
                    cursor.close();
                }
                return valueOf;
            }
            if (cursor == null) {
                return f2289a;
            }
            cursor.close();
            return f2289a;
        } catch (Exception unused) {
            if (cursor == null) {
                return f2289a;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private String h() {
        if (!g.a()) {
            return "";
        }
        String b2 = g.b(this.g);
        return TextUtils.isEmpty(b2) ? "" : b2;
    }

    private static String h(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor != null) {
                String valueOf = String.valueOf(cursor.getCount());
                if (cursor != null) {
                    cursor.close();
                }
                return valueOf;
            }
            if (cursor == null) {
                return f2289a;
            }
            cursor.close();
            return f2289a;
        } catch (Exception unused) {
            if (cursor == null) {
                return f2289a;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private static String i(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://mms-sms/"), "searchCount"), (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor == null) {
                    return f2289a;
                }
                cursor.close();
                return f2289a;
            }
            String valueOf = String.valueOf(cursor.getInt(0));
            if (cursor != null) {
                cursor.close();
            }
            return valueOf;
        } catch (Exception unused) {
            if (cursor == null) {
                return f2289a;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private HashMap i() {
        PackageManager packageManager = this.g.getPackageManager();
        packageManager.getInstalledApplications(8192);
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(8192);
        HashMap hashMap = new HashMap();
        for (PackageInfo next : installedPackages) {
            hashMap.put(next.applicationInfo.publicSourceDir, next);
        }
        return hashMap;
    }

    private String j() {
        String str = f2289a;
        Cursor cursor = null;
        try {
            cursor = this.g.getContentResolver().query(q, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                str = cursor.getString(cursor.getColumnIndex("seriNum"));
            }
        } catch (Exception unused) {
        } catch (Throwable th) {
            aj.a((Closeable) null);
            throw th;
        }
        aj.a((Closeable) cursor);
        return str;
    }

    private static Set j(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
        if (string == null) {
            return Collections.emptySet();
        }
        HashSet hashSet = new HashSet();
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(simpleStringSplitter.next());
            if (unflattenFromString != null) {
                hashSet.add(unflattenFromString);
            }
        }
        return hashSet;
    }

    private static String k() {
        try {
            return SecurityDeviceCredentialManager.getSecurityDeviceId();
        } catch (Exception e2) {
            Log.e(i, "getSid Error", e2);
            return f2289a;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0011, code lost:
        r1 = r1.getNetworkInfo(r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean l() {
        /*
            r3 = this;
            r0 = 0
            android.content.Context r1 = r3.g     // Catch:{ Exception -> 0x0026 }
            java.lang.String r2 = "connectivity"
            java.lang.Object r1 = r1.getSystemService(r2)     // Catch:{ Exception -> 0x0026 }
            android.net.ConnectivityManager r1 = (android.net.ConnectivityManager) r1     // Catch:{ Exception -> 0x0026 }
            android.net.Network r2 = r1.getActiveNetwork()     // Catch:{ Exception -> 0x0026 }
            if (r2 == 0) goto L_0x0026
            android.net.NetworkInfo r1 = r1.getNetworkInfo((android.net.Network) r2)     // Catch:{ Exception -> 0x0026 }
            if (r1 == 0) goto L_0x0026
            boolean r2 = r1.isConnected()     // Catch:{ Exception -> 0x0026 }
            if (r2 == 0) goto L_0x0026
            int r1 = r1.getType()     // Catch:{ Exception -> 0x0026 }
            r2 = 17
            if (r1 != r2) goto L_0x0026
            r0 = 1
        L_0x0026:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.h.l():boolean");
    }

    private static String m() {
        return c(new File("/system/xbin/su")) ? "/system/xbin/su" : c(new File("/system/bin/su")) ? "/system/bin/su" : f2289a;
    }

    private String n() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MAIN");
        intentFilter.addCategory("android.intent.category.HOME");
        return a(intentFilter);
    }

    private String o() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.VIEW");
        intentFilter.addCategory(Constants.System.CATEGORY_DEFALUT);
        intentFilter.addDataScheme("http");
        intentFilter.addDataScheme("https");
        return a(intentFilter);
    }

    private String p() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.VIEW");
        intentFilter.addDataScheme("file");
        try {
            intentFilter.addDataType("audio/*");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return a(intentFilter);
    }

    private String q() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SENDTO");
        intentFilter.addDataScheme("smsto");
        return a(intentFilter);
    }

    private String r() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.VIEW");
        intentFilter.addDataScheme("file");
        try {
            intentFilter.addDataType("image/*");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return a(intentFilter);
    }

    private boolean s() {
        return Settings.Global.getInt(this.g.getContentResolver(), "adb_enabled", 0) == 1;
    }

    private boolean t() {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("type", 1);
            bundle.putString("key", l);
            bundle.putBoolean("default", false);
            Bundle call = this.g.getContentResolver().call(n, "callPreference", "GET", bundle);
            return call != null && call.getBoolean(l, false);
        } catch (Exception unused) {
        }
    }

    private static boolean u() {
        return a(k, Boolean.FALSE).booleanValue();
    }

    private static boolean v() {
        return a("ro.product.cpu.abi", "").contains("64");
    }

    private static String w() {
        return a("ro.debuggable", "");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: java.io.FileReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: java.io.FileReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: java.io.FileReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: java.io.FileReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: java.io.FileReader} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String x() {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = ""
            r0.<init>(r1)
            r1 = 0
            java.io.FileReader r2 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x0057, IOException -> 0x003f, all -> 0x003b }
            java.lang.String r3 = "/cache/otad/otad.log"
            r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x0057, IOException -> 0x003f, all -> 0x003b }
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ FileNotFoundException -> 0x0058, IOException -> 0x0038, all -> 0x0035 }
            r3.<init>(r2)     // Catch:{ FileNotFoundException -> 0x0058, IOException -> 0x0038, all -> 0x0035 }
        L_0x0014:
            java.lang.String r1 = r3.readLine()     // Catch:{ FileNotFoundException -> 0x0033, IOException -> 0x0039, all -> 0x0031 }
            if (r1 == 0) goto L_0x002d
            java.lang.String r4 = "@"
            boolean r4 = r1.startsWith(r4)     // Catch:{ FileNotFoundException -> 0x0033, IOException -> 0x0039, all -> 0x0031 }
            if (r4 == 0) goto L_0x0025
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x0033, IOException -> 0x0039, all -> 0x0031 }
        L_0x0025:
            int r1 = r0.length()     // Catch:{ FileNotFoundException -> 0x0033, IOException -> 0x0039, all -> 0x0031 }
            r4 = 1024(0x400, float:1.435E-42)
            if (r1 <= r4) goto L_0x0014
        L_0x002d:
            com.miui.activityutil.aj.a((java.io.Closeable) r2)
            goto L_0x004a
        L_0x0031:
            r0 = move-exception
            goto L_0x0050
        L_0x0033:
            r1 = r3
            goto L_0x0058
        L_0x0035:
            r0 = move-exception
            r3 = r1
            goto L_0x0050
        L_0x0038:
            r3 = r1
        L_0x0039:
            r1 = r2
            goto L_0x0040
        L_0x003b:
            r0 = move-exception
            r2 = r1
            r3 = r2
            goto L_0x0050
        L_0x003f:
            r3 = r1
        L_0x0040:
            java.lang.String r2 = "InfoHandler"
            java.lang.String r4 = "readLine error!"
            android.util.Log.d(r2, r4)     // Catch:{ all -> 0x004e }
            com.miui.activityutil.aj.a((java.io.Closeable) r1)
        L_0x004a:
            com.miui.activityutil.aj.a((java.io.Closeable) r3)
            goto L_0x005e
        L_0x004e:
            r0 = move-exception
            r2 = r1
        L_0x0050:
            com.miui.activityutil.aj.a((java.io.Closeable) r2)
            com.miui.activityutil.aj.a((java.io.Closeable) r3)
            throw r0
        L_0x0057:
            r2 = r1
        L_0x0058:
            com.miui.activityutil.aj.a((java.io.Closeable) r2)
            com.miui.activityutil.aj.a((java.io.Closeable) r1)
        L_0x005e:
            java.lang.String r0 = r0.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.activityutil.h.x():java.lang.String");
    }

    private static String y() {
        return s;
    }

    private static String z() {
        return a("ro.build.version.release", "");
    }

    public final File a(int i2) {
        File a2 = aj.a(this.g, j);
        if ((!a2.exists() || a2.length() <= 0) && !a(a2, i2)) {
            return null;
        }
        return a2;
    }

    public final void a() {
        File a2 = aj.a(this.g, j);
        if (a2.exists()) {
            a2.delete();
        }
    }

    public final byte[] b() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("p_version", 8);
            jSONObject.put("id", c());
            jSONObject.put("source", this.g.getApplicationInfo().packageName);
            byte[] a2 = p.a(jSONObject.toString().getBytes());
            if (a2 != null) {
                return a2;
            }
            return null;
        } catch (JSONException unused) {
            return null;
        }
    }

    public final String c() {
        return e.a(D() + j());
    }
}
