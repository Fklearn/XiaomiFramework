package com.android.server.notification;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.telecom.TelecomManager;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.server.pm.PackageManagerService;
import java.io.PrintWriter;
import java.util.Date;

public class ZenModeFiltering {
    private static final boolean DEBUG = ZenModeHelper.DEBUG;
    static final RepeatCallers REPEAT_CALLERS = new RepeatCallers();
    private static final String TAG = "ZenModeHelper";
    private final Context mContext;
    private ComponentName mDefaultPhoneApp;
    private final NotificationMessagingUtil mMessagingUtil;

    public ZenModeFiltering(Context context) {
        this.mContext = context;
        this.mMessagingUtil = new NotificationMessagingUtil(this.mContext);
    }

    public ZenModeFiltering(Context context, NotificationMessagingUtil messagingUtil) {
        this.mContext = context;
        this.mMessagingUtil = messagingUtil;
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("mDefaultPhoneApp=");
        pw.println(this.mDefaultPhoneApp);
        pw.print(prefix);
        pw.print("RepeatCallers.mThresholdMinutes=");
        pw.println(REPEAT_CALLERS.mThresholdMinutes);
        synchronized (REPEAT_CALLERS) {
            if (!REPEAT_CALLERS.mCalls.isEmpty()) {
                pw.print(prefix);
                pw.println("RepeatCallers.mCalls=");
                for (int i = 0; i < REPEAT_CALLERS.mCalls.size(); i++) {
                    pw.print(prefix);
                    pw.print("  ");
                    pw.print(ZenModeInjector.hideNumbers((String) REPEAT_CALLERS.mCalls.keyAt(i)));
                    pw.print(" at ");
                    pw.println(ts(((Long) REPEAT_CALLERS.mCalls.valueAt(i)).longValue()));
                }
            }
        }
    }

    private static String ts(long time) {
        return new Date(time) + " (" + time + ")";
    }

    public static boolean matchesCallFilter(Context context, int zen, NotificationManager.Policy consolidatedPolicy, UserHandle userHandle, Bundle extras, ValidateNotificationPeople validator, int contactsTimeoutMs, float timeoutAffinity) {
        if (zen == 2 || zen == 3) {
            return false;
        }
        if (zen != 1 || (consolidatedPolicy.allowRepeatCallers() && REPEAT_CALLERS.isRepeat(context, extras))) {
            return true;
        }
        if (!consolidatedPolicy.allowCalls()) {
            return false;
        }
        if (validator != null) {
            return audienceMatches(consolidatedPolicy.allowCallsFrom(), validator.getContactAffinity(userHandle, extras, contactsTimeoutMs, timeoutAffinity));
        }
        return true;
    }

    private static Bundle extras(NotificationRecord record) {
        if (record == null || record.sbn == null || record.sbn.getNotification() == null) {
            return null;
        }
        return record.sbn.getNotification().extras;
    }

    /* access modifiers changed from: protected */
    public void recordCall(NotificationRecord record) {
        REPEAT_CALLERS.recordCall(this.mContext, extras(record));
    }

    public boolean shouldIntercept(int zen, NotificationManager.Policy policy, NotificationRecord record) {
        if (zen == 0 || isCritical(record)) {
            return false;
        }
        if (NotificationManager.Policy.areAllVisualEffectsSuppressed(policy.suppressedVisualEffects) && PackageManagerService.PLATFORM_PACKAGE_NAME.equals(record.sbn.getPackageName()) && 48 == record.sbn.getId()) {
            ZenLog.traceNotIntercepted(record, "systemDndChangedNotification");
            return false;
        } else if (zen != 1) {
            if (zen == 2) {
                ZenLog.traceIntercepted(record, "none");
                return true;
            } else if (zen != 3 || isAlarm(record)) {
                return false;
            } else {
                ZenLog.traceIntercepted(record, "alarmsOnly");
                return true;
            }
        } else if (record.getPackagePriority() == 2 && !MiuiSettings.SilenceMode.isSupported) {
            ZenLog.traceNotIntercepted(record, "priorityApp");
            return false;
        } else if (isAlarm(record)) {
            if (policy.allowAlarms()) {
                return false;
            }
            ZenLog.traceIntercepted(record, "!allowAlarms");
            return true;
        } else if (isCall(record)) {
            if (policy.allowRepeatCallers() && REPEAT_CALLERS.isRepeat(this.mContext, extras(record))) {
                ZenLog.traceNotIntercepted(record, "repeatCaller");
                return false;
            } else if (policy.allowCalls()) {
                return shouldInterceptAudience(policy.allowCallsFrom(), record);
            } else {
                ZenLog.traceIntercepted(record, "!allowCalls");
                return true;
            }
        } else if (isMessage(record)) {
            if (policy.allowMessages()) {
                return shouldInterceptAudience(policy.allowMessagesFrom(), record);
            }
            ZenLog.traceIntercepted(record, "!allowMessages");
            return true;
        } else if (isEvent(record)) {
            if (policy.allowEvents()) {
                return false;
            }
            ZenLog.traceIntercepted(record, "!allowEvents");
            return true;
        } else if (isReminder(record)) {
            if (policy.allowReminders()) {
                return false;
            }
            ZenLog.traceIntercepted(record, "!allowReminders");
            return true;
        } else if (isMedia(record)) {
            if (policy.allowMedia()) {
                return false;
            }
            ZenLog.traceIntercepted(record, "!allowMedia");
            return true;
        } else if (!isSystem(record)) {
            ZenLog.traceIntercepted(record, "!priority");
            return true;
        } else if (policy.allowSystem()) {
            return false;
        } else {
            ZenLog.traceIntercepted(record, "!allowSystem");
            return true;
        }
    }

    private boolean isCritical(NotificationRecord record) {
        return record.getCriticality() < 2;
    }

    private static boolean shouldInterceptAudience(int source, NotificationRecord record) {
        if (audienceMatches(source, record.getContactAffinity())) {
            return false;
        }
        ZenLog.traceIntercepted(record, "!audienceMatches");
        return true;
    }

    protected static boolean isAlarm(NotificationRecord record) {
        return record.isCategory("alarm") || record.isAudioAttributesUsage(4);
    }

    private static boolean isEvent(NotificationRecord record) {
        return record.isCategory("event");
    }

    private static boolean isReminder(NotificationRecord record) {
        return record.isCategory("reminder");
    }

    public boolean isCall(NotificationRecord record) {
        return record != null && (isDefaultPhoneApp(record.sbn.getPackageName()) || record.isCategory("call"));
    }

    public boolean isMedia(NotificationRecord record) {
        AudioAttributes aa = record.getAudioAttributes();
        return aa != null && AudioAttributes.SUPPRESSIBLE_USAGES.get(aa.getUsage()) == 5;
    }

    public boolean isSystem(NotificationRecord record) {
        AudioAttributes aa = record.getAudioAttributes();
        return aa != null && AudioAttributes.SUPPRESSIBLE_USAGES.get(aa.getUsage()) == 6;
    }

    private boolean isDefaultPhoneApp(String pkg) {
        ComponentName componentName;
        if (this.mDefaultPhoneApp == null) {
            TelecomManager telecomm = (TelecomManager) this.mContext.getSystemService("telecom");
            this.mDefaultPhoneApp = telecomm != null ? telecomm.getDefaultPhoneApp() : null;
            if (DEBUG) {
                Slog.d(TAG, "Default phone app: " + this.mDefaultPhoneApp);
            }
        }
        return (pkg == null || (componentName = this.mDefaultPhoneApp) == null || !pkg.equals(componentName.getPackageName())) ? false : true;
    }

    /* access modifiers changed from: protected */
    public boolean isMessage(NotificationRecord record) {
        return this.mMessagingUtil.isMessaging(record.sbn);
    }

    private static boolean audienceMatches(int source, float contactAffinity) {
        if (source == 0) {
            return true;
        }
        if (source != 1) {
            if (source != 2) {
                Slog.w(TAG, "Encountered unknown source: " + source);
                return true;
            } else if (contactAffinity >= 1.0f) {
                return true;
            } else {
                return false;
            }
        } else if (contactAffinity >= 0.5f) {
            return true;
        } else {
            return false;
        }
    }

    private static class RepeatCallers {
        /* access modifiers changed from: private */
        public final ArrayMap<String, Long> mCalls;
        /* access modifiers changed from: private */
        public int mThresholdMinutes;

        private RepeatCallers() {
            this.mCalls = new ArrayMap<>();
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0028, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized void recordCall(android.content.Context r6, android.os.Bundle r7) {
            /*
                r5 = this;
                monitor-enter(r5)
                r5.setThresholdMinutes(r6)     // Catch:{ all -> 0x0029 }
                int r0 = r5.mThresholdMinutes     // Catch:{ all -> 0x0029 }
                if (r0 <= 0) goto L_0x0027
                if (r7 != 0) goto L_0x000b
                goto L_0x0027
            L_0x000b:
                java.lang.String r0 = peopleString(r7)     // Catch:{ all -> 0x0029 }
                if (r0 != 0) goto L_0x0013
                monitor-exit(r5)
                return
            L_0x0013:
                long r1 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0029 }
                android.util.ArrayMap<java.lang.String, java.lang.Long> r3 = r5.mCalls     // Catch:{ all -> 0x0029 }
                r5.cleanUp(r3, r1)     // Catch:{ all -> 0x0029 }
                android.util.ArrayMap<java.lang.String, java.lang.Long> r3 = r5.mCalls     // Catch:{ all -> 0x0029 }
                java.lang.Long r4 = java.lang.Long.valueOf(r1)     // Catch:{ all -> 0x0029 }
                r3.put(r0, r4)     // Catch:{ all -> 0x0029 }
                monitor-exit(r5)
                return
            L_0x0027:
                monitor-exit(r5)
                return
            L_0x0029:
                r6 = move-exception
                monitor-exit(r5)
                throw r6
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.ZenModeFiltering.RepeatCallers.recordCall(android.content.Context, android.os.Bundle):void");
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0026, code lost:
            return false;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized boolean isRepeat(android.content.Context r5, android.os.Bundle r6) {
            /*
                r4 = this;
                monitor-enter(r4)
                r4.setThresholdMinutes(r5)     // Catch:{ all -> 0x0027 }
                int r0 = r4.mThresholdMinutes     // Catch:{ all -> 0x0027 }
                r1 = 0
                if (r0 <= 0) goto L_0x0025
                if (r6 != 0) goto L_0x000c
                goto L_0x0025
            L_0x000c:
                java.lang.String r0 = peopleString(r6)     // Catch:{ all -> 0x0027 }
                if (r0 != 0) goto L_0x0014
                monitor-exit(r4)
                return r1
            L_0x0014:
                long r1 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0027 }
                android.util.ArrayMap<java.lang.String, java.lang.Long> r3 = r4.mCalls     // Catch:{ all -> 0x0027 }
                r4.cleanUp(r3, r1)     // Catch:{ all -> 0x0027 }
                android.util.ArrayMap<java.lang.String, java.lang.Long> r3 = r4.mCalls     // Catch:{ all -> 0x0027 }
                boolean r3 = r3.containsKey(r0)     // Catch:{ all -> 0x0027 }
                monitor-exit(r4)
                return r3
            L_0x0025:
                monitor-exit(r4)
                return r1
            L_0x0027:
                r5 = move-exception
                monitor-exit(r4)
                throw r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.ZenModeFiltering.RepeatCallers.isRepeat(android.content.Context, android.os.Bundle):boolean");
        }

        private synchronized void cleanUp(ArrayMap<String, Long> calls, long now) {
            for (int i = calls.size() - 1; i >= 0; i--) {
                long time = this.mCalls.valueAt(i).longValue();
                if (time > now || now - time > ((long) (this.mThresholdMinutes * 1000 * 60))) {
                    calls.removeAt(i);
                }
            }
        }

        private void setThresholdMinutes(Context context) {
            if (this.mThresholdMinutes <= 0) {
                this.mThresholdMinutes = context.getResources().getInteger(17694963);
            }
        }

        private static String peopleString(Bundle extras) {
            String[] extraPeople = ValidateNotificationPeople.getExtraPeople(extras);
            if (extraPeople == null || extraPeople.length == 0) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (String extraPerson : extraPeople) {
                if (extraPerson != null) {
                    String extraPerson2 = extraPerson.trim();
                    if (!extraPerson2.isEmpty()) {
                        if (sb.length() > 0) {
                            sb.append('|');
                        }
                        sb.append(extraPerson2);
                    }
                }
            }
            if (sb.length() == 0) {
                return null;
            }
            return sb.toString();
        }
    }
}
