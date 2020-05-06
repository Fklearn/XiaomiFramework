package com.android.server.power;

import android.app.ActivityManager;
import android.content.Context;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ServiceManager;
import android.util.Log;
import com.miui.server.greeze.GreezeManagerService;
import java.util.HashSet;
import java.util.Set;

public class IntentFirewallInjector {
    private static final int GET_FROZEN_UIDS = 14;
    private static final String SERVICENAME = "miui.greeze.IGreezeManager";
    private static final String TAG = "IntentFirewallInjector";
    private static PowerManager pm;
    private static Context sContext;
    private static IBinder sIGreeze = null;
    private static PowerManagerService sService = null;
    private static boolean sSystemReady = false;

    public static void init(Context ctx, PowerManagerService powerManagerService) {
        sContext = ctx;
        sService = powerManagerService;
        sSystemReady = true;
        pm = (PowerManager) sContext.getSystemService("power");
    }

    private static boolean checkGreezeBinder() {
        if (sIGreeze != null) {
            return true;
        }
        try {
            sIGreeze = ServiceManager.getService(GreezeManagerService.SERVICE_NAME);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "IntentFirewallInjector get greezemanagerservice", e);
            return false;
        }
    }

    public static boolean checkIntentForFrozenUid(int intentType, int callerUid, int callerPid, int receivingUid) {
        PowerManager powerManager;
        if (!sSystemReady || intentType != 1 || sService == null || !checkGreezeBinder() || (powerManager = pm) == null || powerManager.isScreenOn()) {
            return false;
        }
        int[] frozenUids = getFrozenUids(1);
        if (frozenUids.length > 0) {
            for (int i = 0; i < frozenUids.length; i++) {
                if (frozenUids[i] == receivingUid) {
                    Log.e(TAG, " callerUid = " + callerUid + " callerPid = " + callerPid + " receivingUid = " + receivingUid);
                    StringBuilder sb = new StringBuilder();
                    sb.append(" matched frozen app is ");
                    sb.append(frozenUids[i]);
                    sb.append(" intentType is ");
                    sb.append(intentType);
                    Log.e(TAG, sb.toString());
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0055, code lost:
        if (r1 != null) goto L_0x002f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int[] getFrozenUids(int r7) {
        /*
            android.os.Parcel r0 = android.os.Parcel.obtain()
            android.os.Parcel r1 = android.os.Parcel.obtain()
            r2 = 0
            int[] r3 = new int[r2]
            java.lang.String r4 = "miui.greeze.IGreezeManager"
            r0.writeInterfaceToken(r4)     // Catch:{ Exception -> 0x0035 }
            if (r7 == 0) goto L_0x0018
            r4 = 1
            r0.writeInt(r4)     // Catch:{ Exception -> 0x0035 }
            goto L_0x001b
        L_0x0018:
            r0.writeInt(r7)     // Catch:{ Exception -> 0x0035 }
        L_0x001b:
            android.os.IBinder r4 = sIGreeze     // Catch:{ Exception -> 0x0035 }
            r5 = 14
            r4.transact(r5, r0, r1, r2)     // Catch:{ Exception -> 0x0035 }
            r1.readException()     // Catch:{ Exception -> 0x0035 }
            int[] r2 = r1.createIntArray()     // Catch:{ Exception -> 0x0035 }
            r3 = r2
            r0.recycle()
        L_0x002f:
            r1.recycle()
            goto L_0x0058
        L_0x0033:
            r2 = move-exception
            goto L_0x0059
        L_0x0035:
            r2 = move-exception
            r2.fillInStackTrace()     // Catch:{ all -> 0x0033 }
            java.lang.String r4 = "IntentFirewallInjector"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0033 }
            r5.<init>()     // Catch:{ all -> 0x0033 }
            java.lang.String r6 = "getFrozenUids:"
            r5.append(r6)     // Catch:{ all -> 0x0033 }
            r5.append(r2)     // Catch:{ all -> 0x0033 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0033 }
            android.util.Log.e(r4, r5)     // Catch:{ all -> 0x0033 }
            if (r0 == 0) goto L_0x0055
            r0.recycle()
        L_0x0055:
            if (r1 == 0) goto L_0x0058
            goto L_0x002f
        L_0x0058:
            return r3
        L_0x0059:
            if (r0 == 0) goto L_0x005e
            r0.recycle()
        L_0x005e:
            if (r1 == 0) goto L_0x0063
            r1.recycle()
        L_0x0063:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.IntentFirewallInjector.getFrozenUids(int):int[]");
    }

    private static int getUidForPid(int pid, Context ctx) {
        try {
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : ((ActivityManager) ctx.getSystemService("activity")).getRunningAppProcesses()) {
                if (appProcessInfo.pid == pid) {
                    return appProcessInfo.uid;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getUidForPid", e);
        }
        return -1;
    }

    private static Set<Integer> getUidForPid(int[] pids, Context ctx) {
        Set<Integer> uids = new HashSet<>();
        try {
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : ((ActivityManager) ctx.getSystemService("activity")).getRunningAppProcesses()) {
                int i = 0;
                while (true) {
                    if (i >= pids.length) {
                        break;
                    } else if (appProcessInfo.pid == pids[i]) {
                        uids.add(Integer.valueOf(appProcessInfo.uid));
                        break;
                    } else {
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getUidForPid", e);
        }
        return uids;
    }
}
