package com.android.server;

import android.app.ActivityManager;
import android.app.IProcessObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.ContentObserver;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import com.miui.server.AccessController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import miui.util.AudioManagerHelper;

public class VibratorServiceInjector {
    private static final int MESSAGE_ACTIVITY_CHANGED = 3;
    private static final int MESSAGE_ADD_VIBRATION_LOG = 4;
    private static final int MESSAGE_NOTIFICATION_POST = 2;
    private static final int MESSAGE_UPDATE_SETTINGS = 5;
    private static final int MESSAGE_VIBRATE = 1;
    private static final long NOTIFICATION_VIBRATION_TIME_RATE = 15000;
    private static final long NOTIFICATION_VIBRATION_TIME_THRESHOLD = 700;
    private static final String TAG = "VibratorServiceInjector";
    private static long VIBRATION_THRESHOLD_IN_CALL = 30;
    private static final long VIBRATION_TIME_DELAY = 75;
    private static Context sContext;
    /* access modifiers changed from: private */
    public static Set<Integer> sForegroundUids = new HashSet();
    /* access modifiers changed from: private */
    public static boolean sIncall;
    private static PhoneStateListener sListener;
    /* access modifiers changed from: private */
    public static final Object sLock = new Object();
    /* access modifiers changed from: private */
    public static HashMap<String, NotificationVibrationInfo> sNotificationVibrationInfos = new HashMap<>();
    /* access modifiers changed from: private */
    public static Map<String, LinkedList<VibrationInfo>> sVibrationsCollection = new ArrayMap();
    /* access modifiers changed from: private */
    public static int sVibrationsLimitPerPkg = 10;
    /* access modifiers changed from: private */
    public static VibratorService sVibratorService;
    private static HashSet<String> sWhiteList = new HashSet<>();
    /* access modifiers changed from: private */
    public static WorkerHandler sWorkerHandler;

    static {
        initHandler();
        sWhiteList.add(AccessController.PACKAGE_CAMERA);
        sWhiteList.add("com.miui.voiceassist");
    }

    public static void init(VibratorService service, Context context) {
        sContext = context;
        sVibratorService = service;
        NotificationListenerService listenerService = new NotificationListenerService() {
            public void onNotificationPosted(StatusBarNotification sbn) {
                if (sbn != null) {
                    Message m = Message.obtain();
                    m.obj = sbn;
                    m.what = 2;
                    VibratorServiceInjector.sWorkerHandler.sendMessage(m);
                }
            }
        };
        MyContextWrapper sContextWrapper = new MyContextWrapper(context);
        try {
            listenerService.registerAsSystemService(sContextWrapper, new ComponentName(sContextWrapper, VibratorService.class), -1);
            ActivityManager.getService().registerProcessObserver(new ProcessObserver());
        } catch (RemoteException e) {
            Slog.e(TAG, "Cannot register listener", e);
        }
        sContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, new SettingsObserver(sWorkerHandler), -1);
        sWorkerHandler.sendEmptyMessage(5);
    }

    public static void listenForCallState(Context context) {
        sListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber) {
                boolean unused = VibratorServiceInjector.sIncall = state != 0;
            }
        };
        TelephonyManager.from(context).listen(sListener, 32);
    }

    private static void initHandler() {
        HandlerThread t = new HandlerThread("vibrator-injector");
        t.start();
        sWorkerHandler = new WorkerHandler(t.getLooper());
    }

    public static VibrationEffect shouldVibrateForMiui(int uid, String pkg, VibrationEffect effect, int usageHint, IBinder token, Context context) {
        boolean result = true;
        if (usageHint == 6 || usageHint == 5) {
            result = AudioManagerHelper.isVibrateEnabled(context);
        }
        if (!result) {
            return null;
        }
        if (sForegroundUids.contains(Integer.valueOf(uid)) || sWhiteList.contains(pkg)) {
            addToVibrationsCollection(effect, usageHint, uid, pkg);
            return effect;
        }
        if (UserHandle.isApp(uid) && isNotification(usageHint)) {
            effect = NotificationVibrationController.filterNotificationVibrate(uid, pkg, effect, usageHint, token);
        }
        if (effect != null) {
            addToVibrationsCollection(effect, usageHint, uid, pkg);
        }
        return effect;
    }

    public static long weakenVibrationIfNecessary(long time, int uid) {
        if (!sIncall || !UserHandle.isApp(uid) || time <= VIBRATION_THRESHOLD_IN_CALL) {
            return time;
        }
        return VIBRATION_THRESHOLD_IN_CALL;
    }

    private static class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange, Uri uri) {
            VibratorServiceInjector.updateSettings();
        }
    }

    /* access modifiers changed from: private */
    public static void updateSettings() {
        int endIndex;
        synchronized (sLock) {
            String inputMethodId = Settings.Secure.getString(sContext.getContentResolver(), "default_input_method");
            if (!TextUtils.isEmpty(inputMethodId) && (endIndex = inputMethodId.indexOf(47)) > 0) {
                sWhiteList.add(inputMethodId.substring(0, endIndex));
            }
        }
    }

    private static class MyContextWrapper extends ContextWrapper {
        public MyContextWrapper(Context base) {
            super(base);
        }

        public Looper getMainLooper() {
            return VibratorServiceInjector.sWorkerHandler.getLooper();
        }
    }

    private static class NotificationVibrationInfo {
        VibrationEffect effect;
        boolean isTooOften;
        boolean isValid;
        long lastNotificationTime;
        long lastNotificationVibrationTime;
        long lastVibrationTime;
        String pkg;
        IBinder token;
        int uid;
        int usageHint;

        private NotificationVibrationInfo() {
        }

        public void fillVibrationInfo(int uid2, String pkg2, VibrationEffect effect2, int usageHint2, IBinder token2) {
            this.uid = uid2;
            this.pkg = pkg2;
            this.effect = effect2;
            this.usageHint = usageHint2;
            this.token = token2;
        }
    }

    private static class NotificationVibrationController {
        private NotificationVibrationController() {
        }

        /* access modifiers changed from: private */
        public static void onNotificationPost(StatusBarNotification sbn) {
            synchronized (VibratorServiceInjector.sLock) {
                NotificationVibrationInfo info = (NotificationVibrationInfo) VibratorServiceInjector.sNotificationVibrationInfos.get(sbn.getPackageName());
                if (info == null) {
                    info = new NotificationVibrationInfo();
                    VibratorServiceInjector.sNotificationVibrationInfos.put(sbn.getPackageName(), info);
                }
                long now = System.currentTimeMillis();
                info.lastNotificationTime = now;
                info.isTooOften = now - info.lastNotificationVibrationTime <= VibratorServiceInjector.NOTIFICATION_VIBRATION_TIME_RATE;
                if (isNotificationVibrate(info) && info.isTooOften) {
                    info.isValid = false;
                    VibratorServiceInjector.sWorkerHandler.removeMessages(1, info.pkg);
                    Slog.e(VibratorServiceInjector.TAG, sbn.getPackageName() + "'s notification vibrate too often, skip");
                }
            }
        }

        /* access modifiers changed from: private */
        public static VibrationEffect filterNotificationVibrate(int uid, String pkg, VibrationEffect effect, int usageHint, IBinder token) {
            synchronized (VibratorServiceInjector.sLock) {
                NotificationVibrationInfo info = (NotificationVibrationInfo) VibratorServiceInjector.sNotificationVibrationInfos.get(pkg);
                if (info == null) {
                    info = new NotificationVibrationInfo();
                    VibratorServiceInjector.sNotificationVibrationInfos.put(pkg, info);
                }
                info.lastVibrationTime = System.currentTimeMillis();
                if (isNotificationVibrate(info)) {
                    if (info.isTooOften) {
                        Slog.e(VibratorServiceInjector.TAG, pkg + "'s notification vibrate too often, skip");
                        info.isValid = false;
                        return null;
                    }
                    effect = adjustVibrationEffect(effect);
                }
                if (info.isValid) {
                    info.isValid = false;
                    return effect;
                }
                info.fillVibrationInfo(uid, pkg, effect, usageHint, token);
                Message m = Message.obtain();
                m.obj = info.pkg;
                m.what = 1;
                VibratorServiceInjector.sWorkerHandler.sendMessageDelayed(m, VibratorServiceInjector.VIBRATION_TIME_DELAY);
                return null;
            }
        }

        private static VibrationEffect adjustVibrationEffect(VibrationEffect effect) {
            if (!(effect instanceof VibrationEffect.Waveform)) {
                return effect;
            }
            VibrationEffect.Waveform waveform = (VibrationEffect.Waveform) effect;
            long[] timings = waveform.getTimings();
            int[] amplitudes = waveform.getAmplitudes();
            if (timings.length <= 4 || amplitudes.length <= 4) {
                return effect;
            }
            int repeat = waveform.getRepeatIndex();
            int i = 0;
            long[] jArr = {timings[0], timings[1], timings[2], timings[3]};
            int[] iArr = {amplitudes[0], amplitudes[1], amplitudes[2], amplitudes[3]};
            if (repeat <= 1) {
                i = repeat;
            }
            return new VibrationEffect.Waveform(jArr, iArr, i);
        }

        private static boolean isNotificationVibrate(NotificationVibrationInfo info) {
            return Math.abs(info.lastVibrationTime - info.lastNotificationTime) < VibratorServiceInjector.NOTIFICATION_VIBRATION_TIME_THRESHOLD;
        }
    }

    private static class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r14) {
            /*
                r13 = this;
                int r0 = r14.what
                r1 = 1
                if (r0 == r1) goto L_0x008c
                r1 = 2
                if (r0 == r1) goto L_0x0084
                r1 = 3
                if (r0 == r1) goto L_0x0057
                r1 = 4
                if (r0 == r1) goto L_0x0018
                r1 = 5
                if (r0 == r1) goto L_0x0013
                goto L_0x00c2
            L_0x0013:
                com.android.server.VibratorServiceInjector.updateSettings()
                goto L_0x00c2
            L_0x0018:
                java.lang.Object r0 = com.android.server.VibratorServiceInjector.sLock
                monitor-enter(r0)
                java.lang.Object r1 = r14.obj     // Catch:{ all -> 0x0054 }
                com.android.server.VibratorServiceInjector$VibrationInfo r1 = (com.android.server.VibratorServiceInjector.VibrationInfo) r1     // Catch:{ all -> 0x0054 }
                java.util.Map r2 = com.android.server.VibratorServiceInjector.sVibrationsCollection     // Catch:{ all -> 0x0054 }
                java.lang.String r3 = r1.mOpPkg     // Catch:{ all -> 0x0054 }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x0054 }
                java.util.LinkedList r2 = (java.util.LinkedList) r2     // Catch:{ all -> 0x0054 }
                if (r2 != 0) goto L_0x0042
                java.util.LinkedList r3 = new java.util.LinkedList     // Catch:{ all -> 0x0054 }
                r3.<init>()     // Catch:{ all -> 0x0054 }
                r2 = r3
                java.util.Map r3 = com.android.server.VibratorServiceInjector.sVibrationsCollection     // Catch:{ all -> 0x0054 }
                java.lang.String r4 = r1.mOpPkg     // Catch:{ all -> 0x0054 }
                r3.put(r4, r2)     // Catch:{ all -> 0x0054 }
            L_0x0042:
                int r3 = r2.size()     // Catch:{ all -> 0x0054 }
                int r4 = com.android.server.VibratorServiceInjector.sVibrationsLimitPerPkg     // Catch:{ all -> 0x0054 }
                if (r3 <= r4) goto L_0x004f
                r2.removeFirst()     // Catch:{ all -> 0x0054 }
            L_0x004f:
                r2.addLast(r1)     // Catch:{ all -> 0x0054 }
                monitor-exit(r0)     // Catch:{ all -> 0x0054 }
                goto L_0x00c2
            L_0x0054:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0054 }
                throw r1
            L_0x0057:
                int r0 = r14.arg1
                java.lang.Object r1 = r14.obj
                java.lang.Boolean r1 = (java.lang.Boolean) r1
                boolean r1 = r1.booleanValue()
                java.lang.Object r2 = com.android.server.VibratorServiceInjector.sLock
                monitor-enter(r2)
                if (r1 == 0) goto L_0x0074
                java.util.Set r3 = com.android.server.VibratorServiceInjector.sForegroundUids     // Catch:{ all -> 0x0081 }
                java.lang.Integer r4 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0081 }
                r3.add(r4)     // Catch:{ all -> 0x0081 }
                goto L_0x007f
            L_0x0074:
                java.util.Set r3 = com.android.server.VibratorServiceInjector.sForegroundUids     // Catch:{ all -> 0x0081 }
                java.lang.Integer r4 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0081 }
                r3.remove(r4)     // Catch:{ all -> 0x0081 }
            L_0x007f:
                monitor-exit(r2)     // Catch:{ all -> 0x0081 }
                goto L_0x00c2
            L_0x0081:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0081 }
                throw r3
            L_0x0084:
                java.lang.Object r0 = r14.obj
                android.service.notification.StatusBarNotification r0 = (android.service.notification.StatusBarNotification) r0
                com.android.server.VibratorServiceInjector.NotificationVibrationController.onNotificationPost(r0)
                goto L_0x00c2
            L_0x008c:
                java.lang.Object r0 = com.android.server.VibratorServiceInjector.sLock
                monitor-enter(r0)
                java.lang.Object r2 = r14.obj     // Catch:{ Exception -> 0x00bd }
                java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x00bd }
                java.util.HashMap r3 = com.android.server.VibratorServiceInjector.sNotificationVibrationInfos     // Catch:{ Exception -> 0x00bd }
                java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x00bd }
                com.android.server.VibratorServiceInjector$NotificationVibrationInfo r3 = (com.android.server.VibratorServiceInjector.NotificationVibrationInfo) r3     // Catch:{ Exception -> 0x00bd }
                r3.isValid = r1     // Catch:{ Exception -> 0x00bd }
                long r4 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x00bd }
                r3.lastNotificationVibrationTime = r4     // Catch:{ Exception -> 0x00bd }
                com.android.server.VibratorService r6 = com.android.server.VibratorServiceInjector.sVibratorService     // Catch:{ Exception -> 0x00bd }
                int r7 = r3.uid     // Catch:{ Exception -> 0x00bd }
                java.lang.String r8 = r3.pkg     // Catch:{ Exception -> 0x00bd }
                android.os.VibrationEffect r9 = r3.effect     // Catch:{ Exception -> 0x00bd }
                int r10 = r3.usageHint     // Catch:{ Exception -> 0x00bd }
                java.lang.String r11 = "MESSAGE_VIBRATE"
                android.os.IBinder r12 = r3.token     // Catch:{ Exception -> 0x00bd }
                r6.vibrate(r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x00bd }
                goto L_0x00c1
            L_0x00bb:
                r1 = move-exception
                goto L_0x00c3
            L_0x00bd:
                r1 = move-exception
                r1.printStackTrace()     // Catch:{ all -> 0x00bb }
            L_0x00c1:
                monitor-exit(r0)     // Catch:{ all -> 0x00bb }
            L_0x00c2:
                return
            L_0x00c3:
                monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.VibratorServiceInjector.WorkerHandler.handleMessage(android.os.Message):void");
        }
    }

    private static class VibrationInfo {
        private final long mAddedTime = System.currentTimeMillis();
        private final VibrationEffect mEffect;
        private final boolean mForeground;
        /* access modifiers changed from: private */
        public final String mOpPkg;
        private final int mUid;
        private final int mUsageHint;

        public VibrationInfo(VibrationEffect effect, int usageHint, int uid, String opPkg, boolean isForeground) {
            this.mEffect = effect;
            this.mUsageHint = usageHint;
            this.mUid = uid;
            this.mOpPkg = opPkg;
            this.mForeground = isForeground;
        }

        public String toString() {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(this.mAddedTime));
            return ", mAddedTime: " + date + ", effect: " + this.mEffect + ", usageHint: " + this.mUsageHint + ", uid: " + this.mUid + ", opPkg: " + this.mOpPkg + ", foreground: " + this.mForeground;
        }
    }

    public static void addToVibrationsCollection(VibrationEffect effect, int usageHint, int uid, String pkg) {
        Message m = Message.obtain();
        m.what = 4;
        m.obj = new VibrationInfo(effect, usageHint, uid, pkg, sForegroundUids.contains(Integer.valueOf(uid)));
        sWorkerHandler.sendMessage(m);
    }

    public static void dumpVibrations(FileDescriptor fd, PrintWriter pw, String[] args) {
        synchronized (sLock) {
            pw.println("Previous vibrations of per Pkg:");
            for (LinkedList<VibrationInfo> list : sVibrationsCollection.values()) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    pw.print("    ");
                    pw.println(((VibrationInfo) it.next()).toString());
                }
            }
        }
    }

    private static class ProcessObserver extends IProcessObserver.Stub {
        private ProcessObserver() {
        }

        public void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities) {
            Message m = Message.obtain();
            m.obj = Boolean.valueOf(foregroundActivities);
            m.arg1 = uid;
            m.what = 3;
            VibratorServiceInjector.sWorkerHandler.sendMessage(m);
        }

        public void onForegroundServicesChanged(int pid, int uid, int serviceTypes) {
        }

        public void onProcessDied(int pid, int uid) {
            synchronized (VibratorServiceInjector.sLock) {
                VibratorServiceInjector.sForegroundUids.remove(Integer.valueOf(uid));
            }
        }
    }

    private static boolean isNotification(int usageHint) {
        switch (usageHint) {
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return true;
            default:
                return false;
        }
    }
}
