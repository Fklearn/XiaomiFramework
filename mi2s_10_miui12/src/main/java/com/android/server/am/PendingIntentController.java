package com.android.server.am;

import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.content.IIntentSender;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.LocalServices;
import com.android.server.am.PendingIntentRecord;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PendingIntentController {
    private static final String TAG = "ActivityManager";
    private static final String TAG_MU = "ActivityManager_MU";
    ActivityManagerInternal mAmInternal;
    final ActivityTaskManagerInternal mAtmInternal;
    final Handler mH;
    final HashMap<PendingIntentRecord.Key, WeakReference<PendingIntentRecord>> mIntentSenderRecords = new HashMap<>();
    final Object mLock = new Object();
    final UserController mUserController;

    PendingIntentController(Looper looper, UserController userController) {
        this.mH = new Handler(looper);
        this.mAtmInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mUserController = userController;
    }

    /* access modifiers changed from: package-private */
    public void onActivityManagerInternalAdded() {
        synchronized (this.mLock) {
            this.mAmInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00b2, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.am.PendingIntentRecord getIntentSender(int r21, java.lang.String r22, int r23, int r24, android.os.IBinder r25, java.lang.String r26, int r27, android.content.Intent[] r28, java.lang.String[] r29, int r30, android.os.Bundle r31) {
        /*
            r20 = this;
            r1 = r20
            r13 = r28
            java.lang.Object r14 = r1.mLock
            monitor-enter(r14)
            r0 = 0
            r15 = 1
            if (r13 == 0) goto L_0x0017
            r2 = r0
        L_0x000c:
            int r3 = r13.length     // Catch:{ all -> 0x00de }
            if (r2 >= r3) goto L_0x0017
            r3 = r13[r2]     // Catch:{ all -> 0x00de }
            r3.setDefusable(r15)     // Catch:{ all -> 0x00de }
            int r2 = r2 + 1
            goto L_0x000c
        L_0x0017:
            r12 = r31
            android.os.Bundle.setDefusable(r12, r15)     // Catch:{ all -> 0x00de }
            r2 = 536870912(0x20000000, float:1.0842022E-19)
            r2 = r30 & r2
            if (r2 == 0) goto L_0x0024
            r2 = r15
            goto L_0x0025
        L_0x0024:
            r2 = r0
        L_0x0025:
            r16 = r2
            r2 = 268435456(0x10000000, float:2.5243549E-29)
            r2 = r30 & r2
            if (r2 == 0) goto L_0x002f
            r2 = r15
            goto L_0x0030
        L_0x002f:
            r2 = r0
        L_0x0030:
            r17 = r2
            r2 = 134217728(0x8000000, float:3.85186E-34)
            r2 = r30 & r2
            if (r2 == 0) goto L_0x0039
            r0 = r15
        L_0x0039:
            r2 = -939524097(0xffffffffc7ffffff, float:-131071.99)
            r18 = r30 & r2
            com.android.server.am.PendingIntentRecord$Key r19 = new com.android.server.am.PendingIntentRecord$Key     // Catch:{ all -> 0x00d8 }
            com.android.server.wm.SafeActivityOptions r11 = com.android.server.wm.SafeActivityOptions.fromBundle(r31)     // Catch:{ all -> 0x00d8 }
            r2 = r19
            r3 = r21
            r4 = r22
            r5 = r25
            r6 = r26
            r7 = r27
            r8 = r28
            r9 = r29
            r10 = r18
            r12 = r24
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x00d8 }
            r2 = r19
            java.util.HashMap<com.android.server.am.PendingIntentRecord$Key, java.lang.ref.WeakReference<com.android.server.am.PendingIntentRecord>> r3 = r1.mIntentSenderRecords     // Catch:{ all -> 0x00d8 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x00d8 }
            java.lang.ref.WeakReference r3 = (java.lang.ref.WeakReference) r3     // Catch:{ all -> 0x00d8 }
            r4 = 0
            if (r3 == 0) goto L_0x006f
            java.lang.Object r5 = r3.get()     // Catch:{ all -> 0x00d8 }
            com.android.server.am.PendingIntentRecord r5 = (com.android.server.am.PendingIntentRecord) r5     // Catch:{ all -> 0x00d8 }
            goto L_0x0070
        L_0x006f:
            r5 = r4
        L_0x0070:
            if (r5 == 0) goto L_0x00be
            if (r17 != 0) goto L_0x00b3
            if (r0 == 0) goto L_0x00af
            com.android.server.am.PendingIntentRecord$Key r6 = r5.key     // Catch:{ all -> 0x00ab }
            android.content.Intent r6 = r6.requestIntent     // Catch:{ all -> 0x00ab }
            if (r6 == 0) goto L_0x008b
            com.android.server.am.PendingIntentRecord$Key r6 = r5.key     // Catch:{ all -> 0x00d8 }
            android.content.Intent r6 = r6.requestIntent     // Catch:{ all -> 0x00d8 }
            if (r13 == 0) goto L_0x0087
            int r7 = r13.length     // Catch:{ all -> 0x00d8 }
            int r7 = r7 - r15
            r7 = r13[r7]     // Catch:{ all -> 0x00d8 }
            goto L_0x0088
        L_0x0087:
            r7 = r4
        L_0x0088:
            r6.replaceExtras(r7)     // Catch:{ all -> 0x00d8 }
        L_0x008b:
            if (r13 == 0) goto L_0x00a0
            int r4 = r13.length     // Catch:{ all -> 0x00ab }
            int r4 = r4 - r15
            com.android.server.am.PendingIntentRecord$Key r6 = r5.key     // Catch:{ all -> 0x00ab }
            android.content.Intent r6 = r6.requestIntent     // Catch:{ all -> 0x00ab }
            r13[r4] = r6     // Catch:{ all -> 0x00ab }
            com.android.server.am.PendingIntentRecord$Key r4 = r5.key     // Catch:{ all -> 0x00ab }
            r4.allIntents = r13     // Catch:{ all -> 0x00ab }
            com.android.server.am.PendingIntentRecord$Key r4 = r5.key     // Catch:{ all -> 0x00ab }
            r6 = r29
            r4.allResolvedTypes = r6     // Catch:{ all -> 0x00d4 }
            goto L_0x00b1
        L_0x00a0:
            r6 = r29
            com.android.server.am.PendingIntentRecord$Key r7 = r5.key     // Catch:{ all -> 0x00d4 }
            r7.allIntents = r4     // Catch:{ all -> 0x00d4 }
            com.android.server.am.PendingIntentRecord$Key r7 = r5.key     // Catch:{ all -> 0x00d4 }
            r7.allResolvedTypes = r4     // Catch:{ all -> 0x00d4 }
            goto L_0x00b1
        L_0x00ab:
            r0 = move-exception
            r6 = r29
            goto L_0x00d5
        L_0x00af:
            r6 = r29
        L_0x00b1:
            monitor-exit(r14)     // Catch:{ all -> 0x00d4 }
            return r5
        L_0x00b3:
            r6 = r29
            r1.makeIntentSenderCanceled(r5)     // Catch:{ all -> 0x00d4 }
            java.util.HashMap<com.android.server.am.PendingIntentRecord$Key, java.lang.ref.WeakReference<com.android.server.am.PendingIntentRecord>> r4 = r1.mIntentSenderRecords     // Catch:{ all -> 0x00d4 }
            r4.remove(r2)     // Catch:{ all -> 0x00d4 }
            goto L_0x00c0
        L_0x00be:
            r6 = r29
        L_0x00c0:
            if (r16 == 0) goto L_0x00c4
            monitor-exit(r14)     // Catch:{ all -> 0x00d4 }
            return r5
        L_0x00c4:
            com.android.server.am.PendingIntentRecord r4 = new com.android.server.am.PendingIntentRecord     // Catch:{ all -> 0x00d4 }
            r7 = r23
            r4.<init>(r1, r2, r7)     // Catch:{ all -> 0x00e7 }
            java.util.HashMap<com.android.server.am.PendingIntentRecord$Key, java.lang.ref.WeakReference<com.android.server.am.PendingIntentRecord>> r5 = r1.mIntentSenderRecords     // Catch:{ all -> 0x00e7 }
            java.lang.ref.WeakReference<com.android.server.am.PendingIntentRecord> r8 = r4.ref     // Catch:{ all -> 0x00e7 }
            r5.put(r2, r8)     // Catch:{ all -> 0x00e7 }
            monitor-exit(r14)     // Catch:{ all -> 0x00e7 }
            return r4
        L_0x00d4:
            r0 = move-exception
        L_0x00d5:
            r7 = r23
            goto L_0x00e5
        L_0x00d8:
            r0 = move-exception
            r7 = r23
            r6 = r29
            goto L_0x00e5
        L_0x00de:
            r0 = move-exception
            r7 = r23
            r6 = r29
            r18 = r30
        L_0x00e5:
            monitor-exit(r14)     // Catch:{ all -> 0x00e7 }
            throw r0
        L_0x00e7:
            r0 = move-exception
            goto L_0x00e5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.PendingIntentController.getIntentSender(int, java.lang.String, int, int, android.os.IBinder, java.lang.String, int, android.content.Intent[], java.lang.String[], int, android.os.Bundle):com.android.server.am.PendingIntentRecord");
    }

    /* access modifiers changed from: package-private */
    public boolean removePendingIntentsForPackage(String packageName, int userId, int appId, boolean doIt) {
        boolean didSomething = false;
        synchronized (this.mLock) {
            if (this.mIntentSenderRecords.size() <= 0) {
                return false;
            }
            Iterator<WeakReference<PendingIntentRecord>> it = this.mIntentSenderRecords.values().iterator();
            while (it.hasNext()) {
                WeakReference<PendingIntentRecord> wpir = it.next();
                if (wpir == null) {
                    it.remove();
                } else {
                    PendingIntentRecord pir = (PendingIntentRecord) wpir.get();
                    if (pir == null) {
                        it.remove();
                    } else {
                        if (packageName == null) {
                            if (pir.key.userId != userId) {
                            }
                        } else if (UserHandle.getAppId(pir.uid) == appId) {
                            if (userId == -1 || pir.key.userId == userId) {
                                if (!pir.key.packageName.equals(packageName)) {
                                }
                            }
                        }
                        if (!doIt) {
                            return true;
                        }
                        didSomething = true;
                        it.remove();
                        makeIntentSenderCanceled(pir);
                        if (pir.key.activity != null) {
                            this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$PendingIntentController$sPmaborOkBSSEP2wiimxXweYDQ.INSTANCE, this, pir.key.activity, pir.ref));
                        }
                    }
                }
            }
            return didSomething;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void cancelIntentSender(IIntentSender sender) {
        if (sender instanceof PendingIntentRecord) {
            synchronized (this.mLock) {
                PendingIntentRecord rec = (PendingIntentRecord) sender;
                try {
                    if (UserHandle.isSameApp(AppGlobals.getPackageManager().getPackageUid(rec.key.packageName, 268435456, UserHandle.getCallingUserId()), Binder.getCallingUid())) {
                        cancelIntentSender(rec, true);
                    } else {
                        String msg = "Permission Denial: cancelIntentSender() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " is not allowed to cancel package " + rec.key.packageName;
                        Slog.w(TAG, msg);
                        throw new SecurityException(msg);
                    }
                } catch (RemoteException e) {
                    throw new SecurityException(e);
                }
            }
        }
    }

    public void cancelIntentSender(PendingIntentRecord rec, boolean cleanActivity) {
        synchronized (this.mLock) {
            makeIntentSenderCanceled(rec);
            this.mIntentSenderRecords.remove(rec.key);
            if (cleanActivity && rec.key.activity != null) {
                this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$PendingIntentController$sPmaborOkBSSEP2wiimxXweYDQ.INSTANCE, this, rec.key.activity, rec.ref));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void registerIntentSenderCancelListener(IIntentSender sender, IResultReceiver receiver) {
        boolean isCancelled;
        if (sender instanceof PendingIntentRecord) {
            synchronized (this.mLock) {
                PendingIntentRecord pendingIntent = (PendingIntentRecord) sender;
                isCancelled = pendingIntent.canceled;
                if (!isCancelled) {
                    pendingIntent.registerCancelListenerLocked(receiver);
                }
            }
            if (isCancelled) {
                try {
                    receiver.send(0, (Bundle) null);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterIntentSenderCancelListener(IIntentSender sender, IResultReceiver receiver) {
        if (sender instanceof PendingIntentRecord) {
            synchronized (this.mLock) {
                ((PendingIntentRecord) sender).unregisterCancelListenerLocked(receiver);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setPendingIntentWhitelistDuration(IIntentSender target, IBinder whitelistToken, long duration) {
        if (!(target instanceof PendingIntentRecord)) {
            Slog.w(TAG, "markAsSentFromNotification(): not a PendingIntentRecord: " + target);
            return;
        }
        synchronized (this.mLock) {
            ((PendingIntentRecord) target).setWhitelistDurationLocked(whitelistToken, duration);
        }
    }

    private void makeIntentSenderCanceled(PendingIntentRecord rec) {
        rec.canceled = true;
        RemoteCallbackList<IResultReceiver> callbacks = rec.detachCancelListenersLocked();
        if (callbacks != null) {
            this.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$PendingIntentController$pDmmJDvS20vSAAXh9qdzbN0P8N0.INSTANCE, this, callbacks));
        }
    }

    /* access modifiers changed from: private */
    public void handlePendingIntentCancelled(RemoteCallbackList<IResultReceiver> callbacks) {
        int N = callbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                callbacks.getBroadcastItem(i).send(0, (Bundle) null);
            } catch (RemoteException e) {
            }
        }
        callbacks.finishBroadcast();
        callbacks.kill();
    }

    /* access modifiers changed from: private */
    public void clearPendingResultForActivity(IBinder activityToken, WeakReference<PendingIntentRecord> pir) {
        this.mAtmInternal.clearPendingResultForActivity(activityToken, pir);
    }

    /* access modifiers changed from: package-private */
    public void dumpPendingIntents(PrintWriter pw, boolean dumpAll, String dumpPackage) {
        synchronized (this.mLock) {
            boolean printed = false;
            pw.println("ACTIVITY MANAGER PENDING INTENTS (dumpsys activity intents)");
            if (this.mIntentSenderRecords.size() > 0) {
                ArrayMap<String, ArrayList<PendingIntentRecord>> byPackage = new ArrayMap<>();
                ArrayList<WeakReference<PendingIntentRecord>> weakRefs = new ArrayList<>();
                Iterator<WeakReference<PendingIntentRecord>> it = this.mIntentSenderRecords.values().iterator();
                while (it.hasNext()) {
                    WeakReference<PendingIntentRecord> ref = it.next();
                    PendingIntentRecord rec = ref != null ? (PendingIntentRecord) ref.get() : null;
                    if (rec == null) {
                        weakRefs.add(ref);
                    } else if (dumpPackage == null || dumpPackage.equals(rec.key.packageName)) {
                        ArrayList<PendingIntentRecord> list = byPackage.get(rec.key.packageName);
                        if (list == null) {
                            list = new ArrayList<>();
                            byPackage.put(rec.key.packageName, list);
                        }
                        list.add(rec);
                    }
                }
                for (int i = 0; i < byPackage.size(); i++) {
                    ArrayList<PendingIntentRecord> intents = byPackage.valueAt(i);
                    printed = true;
                    pw.print("  * ");
                    pw.print(byPackage.keyAt(i));
                    pw.print(": ");
                    pw.print(intents.size());
                    pw.println(" items");
                    for (int j = 0; j < intents.size(); j++) {
                        pw.print("    #");
                        pw.print(j);
                        pw.print(": ");
                        pw.println(intents.get(j));
                        if (dumpAll) {
                            intents.get(j).dump(pw, "      ");
                        }
                    }
                }
                if (weakRefs.size() > 0) {
                    printed = true;
                    pw.println("  * WEAK REFS:");
                    for (int i2 = 0; i2 < weakRefs.size(); i2++) {
                        pw.print("    #");
                        pw.print(i2);
                        pw.print(": ");
                        pw.println(weakRefs.get(i2));
                    }
                }
            }
            if (!printed) {
                pw.println("  (nothing)");
            }
        }
    }
}
