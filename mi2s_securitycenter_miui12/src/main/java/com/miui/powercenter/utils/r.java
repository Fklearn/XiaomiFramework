package com.miui.powercenter.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.e;
import com.miui.networkassistant.firewall.UserConfigure;
import com.xiaomi.stat.MiStat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import miui.os.SystemProperties;

public class r {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static String f7318a = "/persist/thermal/thermalUser.txt";

    public static String a(Context context) {
        try {
            Bundle call = context.getContentResolver().call(Uri.parse("content://com.miui.powerkeeper.configure/SimpleSettings/misc"), "GET_misc", "key_thermal_configure", (Bundle) null);
            if (call == null || !call.containsKey(MiStat.Param.VALUE)) {
                return "default";
            }
            String string = call.getString(MiStat.Param.VALUE);
            return !TextUtils.isEmpty(string) ? string : "default";
        } catch (Exception e) {
            Log.e("ThermalStoreUtils", "get misc settings error", e);
            return "default";
        }
    }

    public static String a(Context context, int i) {
        return a(b(context, i));
    }

    public static String a(Bundle bundle) {
        return bundle.containsKey("userConfigureStatus") ? bundle.getString("userConfigureStatus") : "disable";
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: android.net.LocalSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: android.net.LocalSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: android.net.LocalSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: android.net.LocalSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: android.net.LocalSocket} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: android.net.LocalSocket} */
    /* JADX WARNING: type inference failed for: r3v6 */
    /*  JADX ERROR: JadxRuntimeException in pass: ProcessVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Code variable not set in r3v6 ?
        	at jadx.core.dex.instructions.args.SSAVar.getCodeVar(SSAVar.java:189)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables.collectCodeVars(ProcessVariables.java:122)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables.visit(ProcessVariables.java:45)
        */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0077 A[SYNTHETIC, Splitter:B:25:0x0077] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0084 A[SYNTHETIC, Splitter:B:31:0x0084] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008d A[SYNTHETIC, Splitter:B:36:0x008d] */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    public static void a(int r7) {
        /*
            java.lang.String r0 = "exception when close socket"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "socket:"
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "ThermalStoreUtils"
            android.util.Log.i(r2, r1)
            r1 = 0
            android.net.LocalSocket r3 = new android.net.LocalSocket     // Catch:{ IOException -> 0x005b, all -> 0x0058 }
            r3.<init>()     // Catch:{ IOException -> 0x005b, all -> 0x0058 }
            android.net.LocalSocketAddress r4 = new android.net.LocalSocketAddress     // Catch:{ IOException -> 0x0056 }
            java.lang.String r5 = "thermal-switch-socket"
            android.net.LocalSocketAddress$Namespace r6 = android.net.LocalSocketAddress.Namespace.RESERVED     // Catch:{ IOException -> 0x0056 }
            r4.<init>(r5, r6)     // Catch:{ IOException -> 0x0056 }
            r3.connect(r4)     // Catch:{ IOException -> 0x0056 }
            java.io.OutputStream r1 = r3.getOutputStream()     // Catch:{ IOException -> 0x0056 }
            if (r1 == 0) goto L_0x0043
            java.lang.String r7 = java.lang.String.valueOf(r7)     // Catch:{ IOException -> 0x0056 }
            byte[] r7 = r7.getBytes()     // Catch:{ IOException -> 0x0056 }
            r1.write(r7)     // Catch:{ IOException -> 0x0056 }
            r1.flush()     // Catch:{ IOException -> 0x0056 }
            java.lang.String r7 = "socket connect successful!"
            android.util.Log.i(r2, r7)     // Catch:{ IOException -> 0x0056 }
        L_0x0043:
            if (r1 == 0) goto L_0x004c
            r1.close()     // Catch:{ IOException -> 0x0049 }
            goto L_0x004c
        L_0x0049:
            android.util.Log.i(r2, r0)
        L_0x004c:
            r3.close()     // Catch:{ IOException -> 0x0050 }
            goto L_0x0081
        L_0x0050:
            android.util.Log.i(r2, r0)
            goto L_0x0081
        L_0x0054:
            r7 = move-exception
            goto L_0x0082
        L_0x0056:
            r7 = move-exception
            goto L_0x005d
        L_0x0058:
            r7 = move-exception
            r3 = r1
            goto L_0x0082
        L_0x005b:
            r7 = move-exception
            r3 = r1
        L_0x005d:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0054 }
            r4.<init>()     // Catch:{ all -> 0x0054 }
            java.lang.String r5 = "exception = "
            r4.append(r5)     // Catch:{ all -> 0x0054 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0054 }
            r4.append(r7)     // Catch:{ all -> 0x0054 }
            java.lang.String r7 = r4.toString()     // Catch:{ all -> 0x0054 }
            android.util.Log.i(r2, r7)     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x007e
            r1.close()     // Catch:{ IOException -> 0x007b }
            goto L_0x007e
        L_0x007b:
            android.util.Log.i(r2, r0)
        L_0x007e:
            if (r3 == 0) goto L_0x0081
            goto L_0x004c
        L_0x0081:
            return
        L_0x0082:
            if (r1 == 0) goto L_0x008b
            r1.close()     // Catch:{ IOException -> 0x0088 }
            goto L_0x008b
        L_0x0088:
            android.util.Log.i(r2, r0)
        L_0x008b:
            if (r3 == 0) goto L_0x0094
            r3.close()     // Catch:{ IOException -> 0x0091 }
            goto L_0x0094
        L_0x0091:
            android.util.Log.i(r2, r0)
        L_0x0094:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.utils.r.a(int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0059 A[SYNTHETIC, Splitter:B:20:0x0059] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0064 A[SYNTHETIC, Splitter:B:25:0x0064] */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(int r4, boolean r5) {
        /*
            if (r5 == 0) goto L_0x000a
            java.lang.String r5 = "persist.sys.thermal.config"
            miui.os.SystemProperties.set(r5, r4)
            b((int) r4)
        L_0x000a:
            java.lang.String r5 = "/sys/class/thermal/thermal_message/sconfig"
            java.lang.String r0 = "ThermalStoreUtils"
            boolean r1 = com.miui.powercenter.utils.f.a(r0, r5)
            if (r1 == 0) goto L_0x006d
            r1 = 0
            java.io.FileWriter r2 = new java.io.FileWriter     // Catch:{ Exception -> 0x0042 }
            r3 = 0
            r2.<init>(r5, r3)     // Catch:{ Exception -> 0x0042 }
            java.lang.String r5 = java.lang.Integer.toString(r4)     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            r2.write(r5)     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            r5.<init>()     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            java.lang.String r1 = "fileWrite:"
            r5.append(r1)     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            r5.append(r4)     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            java.lang.String r4 = r5.toString()     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            android.util.Log.e(r0, r4)     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            r2.close()     // Catch:{ IOException -> 0x005d }
            goto L_0x0070
        L_0x003a:
            r4 = move-exception
            r1 = r2
            goto L_0x0062
        L_0x003d:
            r4 = move-exception
            r1 = r2
            goto L_0x0043
        L_0x0040:
            r4 = move-exception
            goto L_0x0062
        L_0x0042:
            r4 = move-exception
        L_0x0043:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0040 }
            r5.<init>()     // Catch:{ all -> 0x0040 }
            java.lang.String r2 = "thermalComunicateException:"
            r5.append(r2)     // Catch:{ all -> 0x0040 }
            r5.append(r4)     // Catch:{ all -> 0x0040 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x0040 }
            android.util.Log.e(r0, r4)     // Catch:{ all -> 0x0040 }
            if (r1 == 0) goto L_0x0070
            r1.close()     // Catch:{ IOException -> 0x005d }
            goto L_0x0070
        L_0x005d:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x0070
        L_0x0062:
            if (r1 == 0) goto L_0x006c
            r1.close()     // Catch:{ IOException -> 0x0068 }
            goto L_0x006c
        L_0x0068:
            r5 = move-exception
            r5.printStackTrace()
        L_0x006c:
            throw r4
        L_0x006d:
            a((int) r4)
        L_0x0070:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.utils.r.a(int, boolean):void");
    }

    public static void a(Context context, String str) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(MiStat.Param.VALUE, str);
            context.getContentResolver().call(Uri.parse("content://com.miui.powerkeeper.configure/SimpleSettings/misc"), "PUT_misc", "key_thermal_configure", bundle);
        } catch (Exception e) {
            Log.e("ThermalStoreUtils", "insert misc settings error", e);
        }
    }

    public static void a(String str) {
        new Thread(new q(str)).start();
    }

    public static Bundle b(Context context, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt(UserConfigure.Columns.USER_ID, i);
        return context.getContentResolver().call(Uri.parse("content://com.miui.powerkeeper.configure/GlobalFeatureTable"), "GlobalFeatureTablequery", (String) null, bundle);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0051 A[SYNTHETIC, Splitter:B:18:0x0051] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x005c A[SYNTHETIC, Splitter:B:23:0x005c] */
    /* JADX WARNING: Removed duplicated region for block: B:29:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void b(int r5) {
        /*
            java.lang.String r0 = d()
            java.lang.String r1 = "ThermalStoreUtils"
            boolean r2 = com.miui.powercenter.utils.f.a(r1, r0)
            if (r2 == 0) goto L_0x0065
            r2 = 0
            java.io.FileWriter r3 = new java.io.FileWriter     // Catch:{ Exception -> 0x003a }
            r4 = 0
            r3.<init>(r0, r4)     // Catch:{ Exception -> 0x003a }
            java.lang.String r0 = java.lang.Integer.toString(r5)     // Catch:{ Exception -> 0x0035, all -> 0x0032 }
            r3.write(r0)     // Catch:{ Exception -> 0x0035, all -> 0x0032 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0035, all -> 0x0032 }
            r0.<init>()     // Catch:{ Exception -> 0x0035, all -> 0x0032 }
            java.lang.String r2 = "fileWrite:"
            r0.append(r2)     // Catch:{ Exception -> 0x0035, all -> 0x0032 }
            r0.append(r5)     // Catch:{ Exception -> 0x0035, all -> 0x0032 }
            java.lang.String r5 = r0.toString()     // Catch:{ Exception -> 0x0035, all -> 0x0032 }
            android.util.Log.e(r1, r5)     // Catch:{ Exception -> 0x0035, all -> 0x0032 }
            r3.close()     // Catch:{ IOException -> 0x0055 }
            goto L_0x0065
        L_0x0032:
            r5 = move-exception
            r2 = r3
            goto L_0x005a
        L_0x0035:
            r5 = move-exception
            r2 = r3
            goto L_0x003b
        L_0x0038:
            r5 = move-exception
            goto L_0x005a
        L_0x003a:
            r5 = move-exception
        L_0x003b:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0038 }
            r0.<init>()     // Catch:{ all -> 0x0038 }
            java.lang.String r3 = "storeThermalGlobalModeException:"
            r0.append(r3)     // Catch:{ all -> 0x0038 }
            r0.append(r5)     // Catch:{ all -> 0x0038 }
            java.lang.String r5 = r0.toString()     // Catch:{ all -> 0x0038 }
            android.util.Log.e(r1, r5)     // Catch:{ all -> 0x0038 }
            if (r2 == 0) goto L_0x0065
            r2.close()     // Catch:{ IOException -> 0x0055 }
            goto L_0x0065
        L_0x0055:
            r5 = move-exception
            r5.printStackTrace()
            goto L_0x0065
        L_0x005a:
            if (r2 == 0) goto L_0x0064
            r2.close()     // Catch:{ IOException -> 0x0060 }
            goto L_0x0064
        L_0x0060:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0064:
            throw r5
        L_0x0065:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.utils.r.b(int):void");
    }

    /* access modifiers changed from: private */
    public static void b(String str, int i, int i2, int i3) {
        try {
            e.a(Class.forName("android.os.FileUtils"), Integer.TYPE, "setPermissions", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE}, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean b(Context context) {
        try {
            return context.getContentResolver().call(Uri.parse("content://com.miui.powerkeeper.configure"), "warmControlModeSupported", (String) null, (Bundle) null).getBoolean("warmControlModeSupported", false);
        } catch (Exception e) {
            Log.e("ThermalStoreUtils", "get warm control mode error" + e);
            return false;
        }
    }

    public static boolean c() {
        File file = new File(e());
        if (!file.exists() || !file.isDirectory() || file.listFiles().length <= 0) {
            Log.i("ThermalStoreUtils", "checkThermalConfigReadyfalse");
            return false;
        }
        Log.i("ThermalStoreUtils", "checkThermalConfigReadytrue");
        return true;
    }

    public static String d() {
        return SystemProperties.get("sys.thermal.data.path", "/data/vendor/thermal/").concat("thermal-global-mode");
    }

    public static String e() {
        return SystemProperties.get("sys.thermal.data.path", "/data/thermal/").concat("config/");
    }

    /* access modifiers changed from: private */
    public static String f() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }
}
