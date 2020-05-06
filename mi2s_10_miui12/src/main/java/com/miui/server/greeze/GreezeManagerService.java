package com.miui.server.greeze;

import android.app.ActivityManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Singleton;
import android.util.Slog;
import android.util.SparseArray;
import android.view.inputmethod.InputMethodInfo;
import com.android.internal.app.ProcessMap;
import com.android.internal.util.DumpUtils;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.am.ActivityManagerService;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.miui.server.SplashScreenServiceDelegate;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.greeze.IGreezeManager;
import miui.greeze.IMonitorToken;
import miui.process.ActiveUidInfo;
import miui.process.ForegroundInfo;
import miui.process.ProcessManager;
import miui.process.ProcessManagerInternal;
import miui.process.RunningProcessInfo;

public class GreezeManagerService extends IGreezeManager.Stub {
    private static final int BINDER_STATE_IN_BUSY = 1;
    private static final int BINDER_STATE_IN_IDLE = 0;
    private static final int BINDER_STATE_IN_TRANSACTION = 4;
    private static final int BINDER_STATE_PROC_IN_BUSY = 3;
    private static final int BINDER_STATE_THREAD_IN_BUSY = 2;
    private static final String CLOUD_GREEZER_ENABLE = "cloud_greezer_enable";
    /* access modifiers changed from: private */
    public static boolean DEBUG = SystemProperties.getBoolean(PROPERTY_GZ_DEBUG, false);
    /* access modifiers changed from: private */
    public static boolean DEBUG_AIDL = false;
    /* access modifiers changed from: private */
    public static boolean DEBUG_LAUNCH_FROM_HOME = false;
    /* access modifiers changed from: private */
    public static boolean DEBUG_MILLET = false;
    private static final boolean DEBUG_MONKEY = SystemProperties.getBoolean(PROPERTY_GZ_MONKEY, false);
    private static boolean DEBUG_SKIPUID = false;
    private static final int DUMPSYS_HISTORY_DURATION = 14400000;
    private static final int HISTORY_SIZE = (DEBUG_MONKEY ? 16384 : 4096);
    private static final Singleton<IGreezeManager> IGreezeManagerSingleton = new Singleton<IGreezeManager>() {
        /* access modifiers changed from: protected */
        public IGreezeManager create() {
            return IGreezeManager.Stub.asInterface(ServiceManager.getService(GreezeManagerService.SERVICE_NAME));
        }
    };
    private static final long LAUNCH_FZ_TIMEOUT = SystemProperties.getLong(PROPERTY_GZ_FZTIMEOUT, 3000);
    private static final long MILLET_DELAY_CEILING = 10000;
    private static final long MILLET_DELAY_THRASHOLD = 50;
    private static final int MILLET_MONITOR_ALL = 7;
    private static final int MILLET_MONITOR_BINDER = 1;
    private static final int MILLET_MONITOR_NET = 4;
    private static final int MILLET_MONITOR_SIGNAL = 2;
    static final String PROPERTY_GZ_DEBUG = "persist.sys.gz.debug";
    static final String PROPERTY_GZ_ENABLE = "persist.sys.gz.enable";
    static final String PROPERTY_GZ_FZTIMEOUT = "persist.sys.gz.fztimeout";
    static final String PROPERTY_GZ_MONKEY = "persist.sys.gz.monkey";
    public static final String SERVICE_NAME = "greezer";
    private static final String TAG = "GreezeManager";
    private static final String TIME_FORMAT_PATTERN = "HH:mm:ss.SSS";
    private static final String[] WHITELIST_PKG = {"com.miui.home", SplashScreenServiceDelegate.SPLASHSCREEN_PACKAGE, "com.xiaomi.xmsf"};
    /* access modifiers changed from: private */
    public static boolean sEnable = SystemProperties.getBoolean(PROPERTY_GZ_ENABLE, false);
    private final ActivityManagerService mActivityManagerService;
    private Context mContext;
    private FrozenInfo[] mFrozenHistory = new FrozenInfo[HISTORY_SIZE];
    private final SparseArray<FrozenInfo> mFrozenPids = new SparseArray<>();
    private Method mGetCastPid;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private int mHistoryIndexNext = 0;
    /* access modifiers changed from: private */
    public final SparseArray<IMonitorToken> mMonitorTokens = new SparseArray<>();
    /* access modifiers changed from: private */
    public int mRegisteredMonitor;
    private ServiceThread mThread;
    /* access modifiers changed from: private */
    public boolean milletEnable = false;

    private static native void nAddConcernedUid(int i);

    private static native void nClearConcernedUid();

    private static native void nDelConcernedUid(int i);

    /* access modifiers changed from: private */
    public static native void nLoopOnce();

    private static native void nQueryBinder(int i);

    static /* synthetic */ int access$372(GreezeManagerService x0, int x1) {
        int i = x0.mRegisteredMonitor & x1;
        x0.mRegisteredMonitor = i;
        return i;
    }

    static {
        boolean z = DEBUG;
        DEBUG_LAUNCH_FROM_HOME = z;
        DEBUG_AIDL = z;
        DEBUG_MILLET = z;
        DEBUG_SKIPUID = z;
    }

    private void registerCloudObserver(final Context context) {
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_GREEZER_ENABLE), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                if (uri != null && uri.equals(Settings.System.getUriFor(GreezeManagerService.CLOUD_GREEZER_ENABLE))) {
                    boolean unused = GreezeManagerService.sEnable = Boolean.parseBoolean(Settings.System.getStringForUser(context.getContentResolver(), GreezeManagerService.CLOUD_GREEZER_ENABLE, -2));
                    Slog.w(GreezeManagerService.TAG, "cloud control set received :" + GreezeManagerService.sEnable);
                }
            }
        }, -2);
    }

    private GreezeManagerService(Context context) {
        this.mContext = context;
        this.mThread = GreezeThread.getInstance();
        this.mHandler = new H(this.mThread.getLooper());
        this.mHandler.sendEmptyMessage(3);
        this.mActivityManagerService = ActivityManager.getService();
        if (sEnable) {
            registerCloudObserver(context);
            if (Settings.System.getStringForUser(context.getContentResolver(), CLOUD_GREEZER_ENABLE, -2) != null) {
                sEnable = Boolean.parseBoolean(Settings.System.getStringForUser(context.getContentResolver(), CLOUD_GREEZER_ENABLE, -2));
            }
        }
        try {
            this.mGetCastPid = this.mActivityManagerService.getClass().getMethod("getCastPid", new Class[0]);
        } catch (NoSuchMethodException e) {
            Slog.w(TAG, "AMS.getCastPid() method doesn't exist");
        }
    }

    public static GreezeManagerService getService() {
        return (GreezeManagerService) IGreezeManagerSingleton.get();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.os.IBinder, com.miui.server.greeze.GreezeManagerService] */
    public static void startService(Context context) {
        ServiceManager.addService(SERVICE_NAME, new GreezeManagerService(context));
    }

    class MonitorDeathRecipient implements IBinder.DeathRecipient {
        IMonitorToken mMonitorToken;
        int mType;

        MonitorDeathRecipient(IMonitorToken token, int type) {
            this.mMonitorToken = token;
            this.mType = type;
        }

        public void binderDied() {
            synchronized (GreezeManagerService.this.mMonitorTokens) {
                GreezeManagerService.this.mMonitorTokens.remove(this.mType);
            }
            GreezeManagerService.this.mHandler.sendEmptyMessage(3);
            int i = this.mType;
            if (i == 1) {
                GreezeManagerService.access$372(GreezeManagerService.this, -2);
            } else if (i == 2) {
                GreezeManagerService.access$372(GreezeManagerService.this, -3);
            } else if (i == 3) {
                GreezeManagerService.access$372(GreezeManagerService.this, -5);
            }
            if ((GreezeManagerService.this.mRegisteredMonitor & 7) != 7) {
                boolean unused = GreezeManagerService.this.milletEnable = false;
            }
            Slog.w(GreezeManagerService.TAG, "Monitor (type " + this.mType + ") died, gz stop");
        }
    }

    public boolean registerMonitor(IMonitorToken token, int type) throws RemoteException {
        Slog.i(TAG, "Monitor registered, type " + type + " pid " + getCallingPid());
        this.mHandler.sendEmptyMessage(3);
        synchronized (this.mMonitorTokens) {
            this.mMonitorTokens.put(type, token);
            token.asBinder().linkToDeath(new MonitorDeathRecipient(token, type), 0);
        }
        if (type == 1) {
            this.mRegisteredMonitor |= 1;
        } else if (type == 2) {
            this.mRegisteredMonitor |= 2;
        } else if (type == 3) {
            this.mRegisteredMonitor |= 4;
        }
        if ((this.mRegisteredMonitor & 7) == 7) {
            Slog.i(TAG, "All monitors registered, about to loop once");
            this.mHandler.sendEmptyMessageDelayed(4, 5000);
        }
        return true;
    }

    public void reportSignal(int uid, int pid, long now) {
        long delay = SystemClock.uptimeMillis() - now;
        String msg = "Receive frozen signal: uid=" + uid + " pid=" + pid + " delay=" + delay + "ms";
        if (DEBUG_MILLET) {
            Slog.i(TAG, msg);
        }
        if (delay > MILLET_DELAY_THRASHOLD && delay < 10000) {
            Slog.w(TAG, "Slow Greezer: " + msg);
        }
        thawProcess(pid, 2, msg);
        thawUid(uid, 2, msg);
    }

    public void reportNet(int uid, long now) {
        long delay = SystemClock.uptimeMillis() - now;
        String msg = "Receive frozen pkg net: uid=" + uid + " delay=" + delay + "ms";
        if (DEBUG_MILLET) {
            Slog.i(TAG, msg);
        }
        if (delay > MILLET_DELAY_THRASHOLD && delay < 10000) {
            Slog.w(TAG, "Slow Greezer: " + msg);
        }
        thawUid(uid, 2, msg);
    }

    public void reportBinderTrans(int dstUid, int dstPid, int callerUid, int callerPid, int callerTid, boolean isOneway, long now) throws RemoteException {
        long delay = SystemClock.uptimeMillis() - now;
        String msg = "Receive frozen binder trans: dstUid=" + dstUid + " dstPid=" + dstPid + " callerUid=" + callerUid + " callerPid=" + callerPid + " callerTid=" + callerTid + " delay=" + delay + "ms oneway=" + isOneway;
        if (DEBUG_MILLET) {
            Slog.i(TAG, msg);
        }
        if (delay > MILLET_DELAY_THRASHOLD && delay < 10000) {
            Slog.w(TAG, "Slow Greezer: " + msg);
        }
        thawProcess(dstPid, 2, msg);
        thawUid(dstUid, 2, msg);
    }

    public void reportLoopOnce() {
        if (DEBUG_MILLET) {
            Slog.i(TAG, "Receive millet loop once msg");
        }
        if ((this.mRegisteredMonitor & 7) == 7) {
            this.milletEnable = true;
            Slog.i(TAG, "Receive millet loop once, gz begin to work");
            return;
        }
        Slog.i(TAG, "Receive millet loop once, but monitor not ready");
    }

    public static String stateToString(int state) {
        if (state == 0) {
            return "BINDER_IN_IDLE";
        }
        if (state == 1) {
            return "BINDER_IN_BUSY";
        }
        if (state == 2) {
            return "BINDER_THREAD_IN_BUSY";
        }
        if (state == 3) {
            return "BINDER_PROC_IN_BUSY";
        }
        if (state != 4) {
            return Integer.toString(state);
        }
        return "BINDER_IN_TRANSACTION";
    }

    public void reportBinderState(int uid, int pid, int tid, int binderState, long now) {
        long delay = SystemClock.uptimeMillis() - now;
        String msg = "Receive binder state: uid=" + uid + " pid=" + pid + " tid=" + tid + " delay=" + delay + "ms binderState=" + stateToString(binderState);
        if (DEBUG_MILLET) {
            Slog.i(TAG, msg);
        }
        if (delay > MILLET_DELAY_THRASHOLD && delay < 10000) {
            Slog.w(TAG, "Slow Greezer: " + msg);
        }
        if (binderState == 0) {
            return;
        }
        if (binderState == 1) {
            thawUid(uid, 2, msg);
        } else if (binderState == 2 || binderState == 3 || binderState == 4) {
            thawProcess(pid, 2, msg);
        }
    }

    /* access modifiers changed from: private */
    public void monitorNet(int uid) {
        nAddConcernedUid(uid);
    }

    /* access modifiers changed from: private */
    public void clearMonitorNet(int uid) {
        nDelConcernedUid(uid);
    }

    /* access modifiers changed from: private */
    public void clearMonitorNet() {
        nClearConcernedUid();
    }

    /* access modifiers changed from: private */
    public void queryBinderState(int uid) {
        nQueryBinder(uid);
    }

    /* access modifiers changed from: package-private */
    public Set<Integer> getIMEUid() {
        Set<Integer> uids = new ArraySet<>();
        try {
            for (InputMethodInfo info : ((InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class)).getInputMethodListAsUser(UserHandle.myUserId())) {
                uids.add(Integer.valueOf(info.getServiceInfo().applicationInfo.uid));
            }
        } catch (Exception e) {
            Slog.w(TAG, "Failed to get IME Uid from InputMethodManagerInternal", e);
        }
        return uids;
    }

    /* access modifiers changed from: package-private */
    public Set<Integer> getAudioUid() {
        Set<Integer> activeUids = new ArraySet<>();
        try {
            for (ActiveUidInfo info : ProcessManager.getActiveUidInfo(3)) {
                activeUids.add(Integer.valueOf(info.uid));
            }
        } catch (Exception e) {
            Slog.w(TAG, "Failed to get active audio info from ProcessManager", e);
        }
        return activeUids;
    }

    /* access modifiers changed from: package-private */
    public List<RunningProcess> getProcessByUid(int uid) {
        List<RunningProcess> procs = getUidMap().get(uid);
        if (procs != null) {
            return procs;
        }
        return new ArrayList();
    }

    /* access modifiers changed from: package-private */
    public List<RunningProcess> getProcessList() {
        List<RunningProcess> procs = new ArrayList<>();
        try {
            for (RunningProcessInfo info : ((ProcessManagerInternal) LocalServices.getService(ProcessManagerInternal.class)).getAllRunningProcessInfo()) {
                if (info != null) {
                    procs.add(new RunningProcess(info));
                }
            }
        } catch (Exception e) {
            Slog.w(TAG, "Failed to get RunningProcessInfo from ProcessManager", e);
        }
        if (DEBUG) {
            List<RunningProcess> rst = getProcessListFromAMS();
            Slog.w(TAG, "pms get " + procs.size() + ", ams get " + rst.size());
            if (procs.size() < rst.size()) {
                for (RunningProcess app : rst) {
                    boolean has = false;
                    Iterator<RunningProcess> it = procs.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (app.pid == it.next().pid) {
                                has = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (!has) {
                        Slog.w(TAG, "pms miss " + app.uid + " " + app.pid + " " + app.processName);
                    }
                }
            }
        }
        return procs;
    }

    /* access modifiers changed from: package-private */
    public List<RunningProcess> getProcessListFromAMS() {
        List<RunningProcess> procs = new ArrayList<>();
        try {
            for (ActivityManager.RunningAppProcessInfo info : this.mActivityManagerService.getRunningAppProcesses()) {
                if (info != null) {
                    procs.add(new RunningProcess(info));
                }
            }
        } catch (Exception e) {
            Slog.w(TAG, "Failed to get RunningProcessInfo from ProcessManager", e);
        }
        return procs;
    }

    /* access modifiers changed from: package-private */
    public RunningProcess getProcessByPid(int pid) {
        for (RunningProcess proc : getProcessList()) {
            if (pid == proc.pid) {
                return proc;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public SparseArray<List<RunningProcess>> getUidMap() {
        SparseArray<List<RunningProcess>> uidMap = new SparseArray<>();
        for (RunningProcess proc : getProcessList()) {
            int uid = proc.uid;
            List<RunningProcess> procs = uidMap.get(uid);
            if (procs == null) {
                procs = new ArrayList<>();
                uidMap.put(uid, procs);
            }
            procs.add(proc);
        }
        return uidMap;
    }

    /* access modifiers changed from: package-private */
    public ProcessMap<List<RunningProcess>> getPkgMap() {
        ProcessMap<List<RunningProcess>> map = new ProcessMap<>();
        for (RunningProcess proc : getProcessList()) {
            int uid = proc.uid;
            if (proc.pkgList != null) {
                for (String packageName : proc.pkgList) {
                    List<RunningProcess> procs = (List) map.get(packageName, uid);
                    if (procs == null) {
                        procs = new ArrayList<>();
                        map.put(packageName, uid, procs);
                    }
                    procs.add(proc);
                }
            }
        }
        return map;
    }

    public boolean isUidActive(int uid) {
        try {
            for (ActiveUidInfo info : ProcessManager.getActiveUidInfo(3)) {
                if (info.uid == uid) {
                    return true;
                }
            }
            return false;
        } catch (RemoteException e) {
            Slog.w(TAG, "Failed to get active audio info. Going to freeze uid" + uid + " regardless of whether it using audio", e);
            return false;
        }
    }

    public void freezeThread(int tid) {
        FreezeUtils.freezeTid(tid);
    }

    /* access modifiers changed from: package-private */
    public boolean freezeProcess(RunningProcess proc, long timeout, int fromWho, String reason) {
        int pid = proc.pid;
        if (Process.myPid() == pid) {
            return false;
        }
        boolean done = FreezeUtils.freezePid(pid);
        synchronized (this.mFrozenPids) {
            FrozenInfo info = this.mFrozenPids.get(pid);
            if (info == null) {
                info = new FrozenInfo(proc);
                this.mFrozenPids.put(pid, info);
            }
            info.addFreezeInfo(System.currentTimeMillis(), fromWho, reason);
            if (this.mHandler.hasMessages(1, info)) {
                this.mHandler.removeMessages(1, info);
            }
            if (timeout != 0) {
                Message msg = this.mHandler.obtainMessage(1, info);
                msg.arg1 = pid;
                this.mHandler.sendMessageDelayed(msg, timeout);
            }
        }
        return done;
    }

    public boolean freezePids(int[] pids, long timeout, int fromWho, String reason) {
        int[] iArr = pids;
        if (DEBUG_AIDL) {
            Slog.d(TAG, "AIDL freezePids(" + Arrays.toString(pids) + ", " + timeout + ", " + fromWho + ", " + reason + ")");
        } else {
            long j = timeout;
            int i = fromWho;
            String str = reason;
        }
        if (iArr == null) {
            return true;
        }
        List<RunningProcess> procs = getProcessList();
        boolean allDone = true;
        for (int pid : iArr) {
            RunningProcess target = null;
            for (RunningProcess proc : procs) {
                if (pid == proc.pid) {
                    target = proc;
                }
            }
            if (target == null) {
                Slog.w(TAG, "Failed to freeze invalid pid " + pid);
                allDone = false;
            } else {
                int i2 = pid;
                if (!freezeProcess(target, timeout, fromWho, reason)) {
                    allDone = false;
                }
            }
        }
        if (DEBUG_AIDL) {
            Slog.d(TAG, "AIDL freezePids result: frozen " + FreezeUtils.getFrozenPids());
        }
        return allDone;
    }

    public boolean freezeUids(int[] uids, long timeout, int fromWho, String reason, boolean checkAudioGps) {
        int i;
        int[] iArr = uids;
        boolean z = checkAudioGps;
        if (DEBUG_AIDL) {
            Slog.d(TAG, "AIDL freezeUids(" + Arrays.toString(uids) + ", " + timeout + ", " + fromWho + ", " + reason + " " + z + ")");
        } else {
            long j = timeout;
            int i2 = fromWho;
            String str = reason;
        }
        if (iArr == null) {
            return true;
        }
        SparseArray<List<RunningProcess>> uidMap = getUidMap();
        boolean allDone = true;
        int length = iArr.length;
        int i3 = 0;
        while (i3 < length) {
            int uid = iArr[i3];
            List<RunningProcess> procs = uidMap.get(uid);
            if (procs == null) {
                Slog.w(TAG, "Failed to freeze invalid uid " + uid);
                allDone = false;
                i = i3;
            } else {
                if (z) {
                    if (isUidActive(uid)) {
                        Slog.i(TAG, "Uid " + uid + " is using audio or GPS, won't freeze it, skip it");
                        allDone = false;
                        i = i3;
                    }
                }
                StringBuilder msg = new StringBuilder();
                msg.append("Freezing uid " + uid + ":");
                Iterator<RunningProcess> it = procs.iterator();
                boolean allDone2 = allDone;
                while (it.hasNext()) {
                    RunningProcess proc = it.next();
                    StringBuilder sb = new StringBuilder();
                    sb.append(" ");
                    Iterator<RunningProcess> it2 = it;
                    sb.append(proc.pid);
                    msg.append(sb.toString());
                    RunningProcess runningProcess = proc;
                    StringBuilder msg2 = msg;
                    int uid2 = uid;
                    int i4 = i3;
                    if (!freezeProcess(proc, timeout, fromWho, reason)) {
                        allDone2 = false;
                    }
                    it = it2;
                    msg = msg2;
                    uid = uid2;
                    i3 = i4;
                }
                int i5 = uid;
                i = i3;
                Slog.d(TAG, msg.toString());
                allDone = allDone2;
            }
            i3 = i + 1;
            iArr = uids;
        }
        if (DEBUG_AIDL) {
            Slog.d(TAG, "AIDL freezePids result: frozen " + FreezeUtils.getFrozenPids());
        }
        return allDone;
    }

    public void thawThread(int tid) {
        FreezeUtils.thawTid(tid);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0073, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00bf, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0031, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean thawProcess(int r8, int r9, java.lang.String r10) {
        /*
            r7 = this;
            r0 = 0
            android.util.SparseArray<com.miui.server.greeze.GreezeManagerService$FrozenInfo> r1 = r7.mFrozenPids
            monitor-enter(r1)
            android.util.SparseArray<com.miui.server.greeze.GreezeManagerService$FrozenInfo> r2 = r7.mFrozenPids     // Catch:{ all -> 0x00c0 }
            java.lang.Object r2 = r2.get(r8)     // Catch:{ all -> 0x00c0 }
            com.miui.server.greeze.GreezeManagerService$FrozenInfo r2 = (com.miui.server.greeze.GreezeManagerService.FrozenInfo) r2     // Catch:{ all -> 0x00c0 }
            if (r2 != 0) goto L_0x0032
            boolean r3 = DEBUG     // Catch:{ all -> 0x00c0 }
            if (r3 == 0) goto L_0x0030
            java.lang.String r3 = "GreezeManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c0 }
            r4.<init>()     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = "Thawing a non-frozen process (pid="
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            r4.append(r8)     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = "), won't add into history, reason "
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            r4.append(r10)     // Catch:{ all -> 0x00c0 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00c0 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00c0 }
        L_0x0030:
            monitor-exit(r1)     // Catch:{ all -> 0x00c0 }
            return r0
        L_0x0032:
            r3 = 9999(0x270f, float:1.4012E-41)
            if (r9 == r3) goto L_0x0074
            int r3 = r2.getOwner()     // Catch:{ all -> 0x00c0 }
            if (r3 == r9) goto L_0x0074
            boolean r3 = DEBUG     // Catch:{ all -> 0x00c0 }
            if (r3 == 0) goto L_0x0072
            java.lang.String r3 = "GreezeManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c0 }
            r4.<init>()     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = "Skipping process (pid="
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            r4.append(r8)     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = "), reason "
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            r4.append(r10)     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = "call from: "
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            r4.append(r9)     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = " & owner:"
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            int r5 = r2.getOwner()     // Catch:{ all -> 0x00c0 }
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00c0 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00c0 }
        L_0x0072:
            monitor-exit(r1)     // Catch:{ all -> 0x00c0 }
            return r0
        L_0x0074:
            boolean r3 = com.miui.server.greeze.FreezeUtils.thawPid(r8)     // Catch:{ all -> 0x00c0 }
            r0 = r3
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00c0 }
            r2.mThawTime = r3     // Catch:{ all -> 0x00c0 }
            r2.mThawReason = r10     // Catch:{ all -> 0x00c0 }
            android.util.SparseArray<com.miui.server.greeze.GreezeManagerService$FrozenInfo> r3 = r7.mFrozenPids     // Catch:{ all -> 0x00c0 }
            r3.remove(r8)     // Catch:{ all -> 0x00c0 }
            r7.addHistoryInfo(r2)     // Catch:{ all -> 0x00c0 }
            java.lang.String r3 = "GreezeManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c0 }
            r4.<init>()     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = "THAW "
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            r4.append(r2)     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = " "
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            long r5 = r2.getFrozenDuration()     // Catch:{ all -> 0x00c0 }
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            java.lang.String r5 = "ms"
            r4.append(r5)     // Catch:{ all -> 0x00c0 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00c0 }
            android.util.Slog.d(r3, r4)     // Catch:{ all -> 0x00c0 }
            android.os.Handler r3 = r7.mHandler     // Catch:{ all -> 0x00c0 }
            r4 = 1
            boolean r3 = r3.hasMessages(r4, r2)     // Catch:{ all -> 0x00c0 }
            if (r3 == 0) goto L_0x00be
            android.os.Handler r3 = r7.mHandler     // Catch:{ all -> 0x00c0 }
            r3.removeMessages(r4, r2)     // Catch:{ all -> 0x00c0 }
        L_0x00be:
            monitor-exit(r1)     // Catch:{ all -> 0x00c0 }
            return r0
        L_0x00c0:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00c0 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.greeze.GreezeManagerService.thawProcess(int, int, java.lang.String):boolean");
    }

    public boolean thawPids(int[] pids, int fromWho, String reason) {
        if (DEBUG_AIDL) {
            Slog.d(TAG, "AIDL thawPids(" + Arrays.toString(pids) + ", " + fromWho + ", " + reason + ")");
        }
        boolean allDone = true;
        for (int pid : pids) {
            if (!thawProcess(pid, fromWho, reason)) {
                allDone = false;
            }
        }
        return allDone;
    }

    /* access modifiers changed from: package-private */
    public boolean thawUid(int uid, int fromWho, String reason) {
        if (DEBUG) {
            Slog.d(TAG, "Thaw uid " + uid + ", " + reason);
        }
        boolean allDone = true;
        synchronized (this.mFrozenPids) {
            List<FrozenInfo> toThaw = new ArrayList<>();
            for (int i = 0; i < this.mFrozenPids.size(); i++) {
                FrozenInfo frozen = this.mFrozenPids.valueAt(i);
                if (frozen.uid == uid) {
                    toThaw.add(frozen);
                }
            }
            for (FrozenInfo frozen2 : toThaw) {
                if (!thawProcess(frozen2.pid, fromWho, reason)) {
                    allDone = false;
                }
            }
        }
        return allDone;
    }

    public boolean thawUids(int[] uids, int fromWho, String reason) {
        if (DEBUG_AIDL) {
            Slog.d(TAG, "AIDL thawUids(" + Arrays.toString(uids) + ", " + fromWho + ", " + reason + ")");
        }
        boolean allDone = true;
        for (int uid : uids) {
            if (!thawUid(uid, fromWho, reason)) {
                allDone = false;
            }
        }
        return allDone;
    }

    /* access modifiers changed from: package-private */
    public boolean thawAll(String reason) {
        for (Integer intValue : FreezeUtils.getFrozenPids()) {
            thawProcess(intValue.intValue(), 9999, reason);
        }
        this.mHandler.removeMessages(1);
        synchronized (this.mFrozenPids) {
            this.mFrozenPids.clear();
        }
        for (Integer intValue2 : FreezeUtils.getFrozonTids()) {
            FreezeUtils.thawTid(intValue2.intValue());
        }
        if (FreezeUtils.getFrozenPids().size() == 0) {
            return true;
        }
        return false;
    }

    public boolean thawAll(int module, int fromWho, String reason) {
        if (DEBUG_AIDL) {
            Slog.d(TAG, "AIDL thawAll(" + module + ", " + fromWho + ", " + reason + ")");
        }
        return thawPids(getFrozenPids(module), fromWho, reason);
    }

    public int[] getFrozenPids(int module) {
        if (DEBUG_AIDL) {
            Slog.d(TAG, "AIDL getFrozenPids(" + module + ")");
        }
        if (module == 0 || module == 1 || module == 2) {
            List<Integer> pids = new ArrayList<>();
            synchronized (this.mFrozenPids) {
                for (int i = 0; i < this.mFrozenPids.size(); i++) {
                    FrozenInfo frozen = this.mFrozenPids.valueAt(i);
                    if (frozen.mFromWho.size() != 0 && frozen.getOwner() == module) {
                        pids.add(Integer.valueOf(frozen.pid));
                    }
                }
            }
            return toArray(pids);
        } else if (module != 9999) {
            return new int[0];
        } else {
            return toArray(FreezeUtils.getFrozenPids());
        }
    }

    public int[] getFrozenUids(int module) {
        if (module != 0 && module != 1 && module != 2 && module != 9999) {
            return new int[0];
        }
        HashSet<Integer> uids = new HashSet<>();
        synchronized (this.mFrozenPids) {
            for (int i = 0; i < this.mFrozenPids.size(); i++) {
                FrozenInfo frozen = this.mFrozenPids.valueAt(i);
                if (module == 9999 || (frozen.mFromWho.size() != 0 && frozen.getOwner() == module)) {
                    uids.add(Integer.valueOf(frozen.uid));
                }
            }
        }
        int[] rst = new int[uids.size()];
        int index = 0;
        Iterator<Integer> iterator = uids.iterator();
        while (iterator.hasNext()) {
            rst[index] = iterator.next().intValue();
            index++;
        }
        return rst;
    }

    static int[] toArray(List<Integer> lst) {
        if (lst == null) {
            return new int[0];
        }
        int[] arr = new int[lst.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = lst.get(i).intValue();
        }
        return arr;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00b5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleLaunchBoost(int r34, java.lang.String r35, int r36) {
        /*
            r33 = this;
            r7 = r33
            r8 = r35
            r9 = r36
            java.lang.String r1 = "Failed to get cast pid"
            long r10 = android.os.SystemClock.uptimeMillis()
            r12 = 64
            java.lang.String r0 = "GzBoost"
            android.os.Trace.traceBegin(r12, r0)
            boolean r0 = DEBUG
            java.lang.String r14 = "GzBoost "
            java.lang.String r15 = "GreezeManager"
            if (r0 == 0) goto L_0x0032
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            r0.append(r8)
            java.lang.String r2 = ", start"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Slog.d(r15, r0)
        L_0x0032:
            r2 = -1
            r6 = 0
            java.lang.reflect.Method r0 = r7.mGetCastPid     // Catch:{ IllegalAccessException -> 0x0050, InvocationTargetException -> 0x004b }
            if (r0 == 0) goto L_0x0049
            java.lang.reflect.Method r0 = r7.mGetCastPid     // Catch:{ IllegalAccessException -> 0x0050, InvocationTargetException -> 0x004b }
            com.android.server.am.ActivityManagerService r3 = r7.mActivityManagerService     // Catch:{ IllegalAccessException -> 0x0050, InvocationTargetException -> 0x004b }
            java.lang.Object[] r4 = new java.lang.Object[r6]     // Catch:{ IllegalAccessException -> 0x0050, InvocationTargetException -> 0x004b }
            java.lang.Object r0 = r0.invoke(r3, r4)     // Catch:{ IllegalAccessException -> 0x0050, InvocationTargetException -> 0x004b }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ IllegalAccessException -> 0x0050, InvocationTargetException -> 0x004b }
            int r0 = r0.intValue()     // Catch:{ IllegalAccessException -> 0x0050, InvocationTargetException -> 0x004b }
            r2 = r0
        L_0x0049:
            r5 = r2
            goto L_0x0056
        L_0x004b:
            r0 = move-exception
            android.util.Slog.w(r15, r1, r0)
            goto L_0x0055
        L_0x0050:
            r0 = move-exception
            android.util.Slog.w(r15, r1, r0)
        L_0x0055:
            r5 = r2
        L_0x0056:
            android.util.ArraySet r0 = new android.util.ArraySet
            r0.<init>()
            r3 = r0
            java.util.Set r0 = r33.getAudioUid()
            r3.addAll(r0)
            java.util.Set r0 = r33.getIMEUid()
            r3.addAll(r0)
            miui.process.ForegroundInfo r0 = miui.process.ProcessManager.getForegroundInfo()     // Catch:{ Exception -> 0x0082 }
            int r1 = r0.mForegroundUid     // Catch:{ Exception -> 0x0082 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ Exception -> 0x0082 }
            r3.add(r1)     // Catch:{ Exception -> 0x0082 }
            int r1 = r0.mMultiWindowForegroundUid     // Catch:{ Exception -> 0x0082 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ Exception -> 0x0082 }
            r3.add(r1)     // Catch:{ Exception -> 0x0082 }
            goto L_0x0088
        L_0x0082:
            r0 = move-exception
            java.lang.String r1 = "Failed to get foreground info from ProcessManager"
            android.util.Slog.w(r15, r1, r0)
        L_0x0088:
            boolean r0 = DEBUG_SKIPUID
            if (r0 == 0) goto L_0x00a0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "GzBoost dynamic white list "
            r0.append(r1)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            android.util.Slog.d(r15, r0)
        L_0x00a0:
            r0 = 0
            android.util.SparseArray r4 = r33.getUidMap()
            r1 = 0
            r32 = r1
            r1 = r0
            r0 = r32
        L_0x00ab:
            int r2 = r4.size()
            java.lang.String r12 = "ms"
            java.lang.String r13 = " from "
            if (r0 >= r2) goto L_0x030f
            int r2 = r4.keyAt(r0)
            boolean r16 = android.os.UserHandle.isApp(r2)
            if (r16 == 0) goto L_0x02ed
            r16 = r10
            r10 = r34
            if (r2 == r10) goto L_0x02df
            if (r2 != r9) goto L_0x00d6
            r28 = r0
            r24 = r1
            r21 = r3
            r22 = r4
            r23 = r5
            r20 = r6
            r1 = r7
            goto L_0x02fc
        L_0x00d6:
            java.lang.Integer r11 = java.lang.Integer.valueOf(r2)
            boolean r11 = r3.contains(r11)
            java.lang.String r6 = "GzBoost skip uid "
            if (r11 == 0) goto L_0x010c
            boolean r11 = DEBUG_SKIPUID
            if (r11 == 0) goto L_0x00fd
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r6)
            r11.append(r2)
            java.lang.String r6 = " for dynamic white list"
            r11.append(r6)
            java.lang.String r6 = r11.toString()
            android.util.Slog.d(r15, r6)
        L_0x00fd:
            r28 = r0
            r24 = r1
            r21 = r3
            r22 = r4
            r23 = r5
            r1 = r7
            r20 = 0
            goto L_0x02fc
        L_0x010c:
            java.lang.Object r11 = r4.valueAt(r0)
            java.util.List r11 = (java.util.List) r11
            r19 = 0
            java.lang.String r20 = ""
            java.util.Iterator r21 = r11.iterator()
        L_0x011a:
            boolean r22 = r21.hasNext()
            java.lang.String r10 = " "
            if (r22 == 0) goto L_0x0220
            java.lang.Object r22 = r21.next()
            r23 = r3
            r3 = r22
            com.miui.server.greeze.GreezeManagerService$RunningProcess r3 = (com.miui.server.greeze.GreezeManagerService.RunningProcess) r3
            r22 = r4
            boolean r4 = r3.hasForegroundActivities
            if (r4 == 0) goto L_0x0159
            r19 = 1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r24 = r1
            int r1 = r3.pid
            r4.append(r1)
            r4.append(r10)
            java.lang.String r1 = r3.processName
            r4.append(r1)
            java.lang.String r1 = " has foreground activity"
            r4.append(r1)
            java.lang.String r20 = r4.toString()
            r28 = r0
            r25 = r5
            r0 = r20
            goto L_0x022c
        L_0x0159:
            r24 = r1
            boolean r1 = r3.hasForegroundServices
            if (r1 == 0) goto L_0x0184
            r19 = 1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r4 = r3.pid
            r1.append(r4)
            r1.append(r10)
            java.lang.String r4 = r3.processName
            r1.append(r4)
            java.lang.String r4 = " has foreground service"
            r1.append(r4)
            java.lang.String r20 = r1.toString()
            r28 = r0
            r25 = r5
            r0 = r20
            goto L_0x022c
        L_0x0184:
            int r1 = r3.pid
            if (r1 != r5) goto L_0x01ad
            r19 = 1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r4 = r3.pid
            r1.append(r4)
            r1.append(r10)
            java.lang.String r4 = r3.processName
            r1.append(r4)
            java.lang.String r4 = " has cast activity"
            r1.append(r4)
            java.lang.String r20 = r1.toString()
            r28 = r0
            r25 = r5
            r0 = r20
            goto L_0x022c
        L_0x01ad:
            java.lang.String[] r1 = r3.pkgList
            if (r1 == 0) goto L_0x020c
            java.lang.String[] r1 = r3.pkgList
            int r4 = r1.length
            r25 = r5
            r5 = 0
        L_0x01b7:
            if (r5 >= r4) goto L_0x0209
            r26 = r4
            r4 = r1[r5]
            r27 = r1
            java.lang.String[] r1 = WHITELIST_PKG
            r28 = r0
            int r0 = r1.length
            r7 = 0
        L_0x01c5:
            if (r7 >= r0) goto L_0x01fb
            r29 = r0
            r0 = r1[r7]
            boolean r30 = android.text.TextUtils.equals(r4, r0)
            if (r30 == 0) goto L_0x01f4
            r1 = 1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r30 = r0
            int r0 = r3.pid
            r7.append(r0)
            r7.append(r10)
            java.lang.String r0 = r3.processName
            r7.append(r0)
            java.lang.String r0 = " in whitelist"
            r7.append(r0)
            java.lang.String r0 = r7.toString()
            r20 = r0
            r19 = r1
            goto L_0x01fb
        L_0x01f4:
            r30 = r0
            int r7 = r7 + 1
            r0 = r29
            goto L_0x01c5
        L_0x01fb:
            if (r19 == 0) goto L_0x01fe
            goto L_0x0210
        L_0x01fe:
            int r5 = r5 + 1
            r7 = r33
            r4 = r26
            r1 = r27
            r0 = r28
            goto L_0x01b7
        L_0x0209:
            r28 = r0
            goto L_0x0210
        L_0x020c:
            r28 = r0
            r25 = r5
        L_0x0210:
            r7 = r33
            r10 = r34
            r4 = r22
            r3 = r23
            r1 = r24
            r5 = r25
            r0 = r28
            goto L_0x011a
        L_0x0220:
            r28 = r0
            r24 = r1
            r23 = r3
            r22 = r4
            r25 = r5
            r0 = r20
        L_0x022c:
            if (r19 == 0) goto L_0x0256
            boolean r1 = DEBUG_SKIPUID
            if (r1 == 0) goto L_0x024c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            r1.append(r2)
            java.lang.String r3 = ", because "
            r1.append(r3)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            android.util.Slog.d(r15, r1)
        L_0x024c:
            r20 = 0
            r1 = r33
            r21 = r23
            r23 = r25
            goto L_0x02fc
        L_0x0256:
            java.util.Iterator r7 = r11.iterator()
        L_0x025a:
            boolean r1 = r7.hasNext()
            if (r1 == 0) goto L_0x02ce
            java.lang.Object r1 = r7.next()
            r6 = r1
            com.miui.server.greeze.GreezeManagerService$RunningProcess r6 = (com.miui.server.greeze.GreezeManagerService.RunningProcess) r6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            r1.append(r8)
            r1.append(r13)
            r1.append(r9)
            java.lang.String r3 = ", freezing "
            r1.append(r3)
            int r3 = r6.uid
            r1.append(r3)
            r1.append(r10)
            int r3 = r6.pid
            r1.append(r3)
            r1.append(r10)
            java.lang.String r3 = r6.processName
            r1.append(r3)
            java.lang.String r3 = " timeout="
            r1.append(r3)
            long r3 = LAUNCH_FZ_TIMEOUT
            r1.append(r3)
            r1.append(r12)
            java.lang.String r5 = r1.toString()
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x02aa
            android.util.Slog.d(r15, r5)
        L_0x02aa:
            long r3 = LAUNCH_FZ_TIMEOUT
            r20 = 2
            r1 = r33
            r31 = r2
            r2 = r6
            r21 = r23
            r23 = r25
            r25 = r5
            r5 = r20
            r18 = r6
            r20 = 0
            r6 = r25
            r1.freezeProcess(r2, r3, r5, r6)
            int r24 = r24 + 1
            r25 = r23
            r2 = r31
            r23 = r21
            goto L_0x025a
        L_0x02ce:
            r31 = r2
            r21 = r23
            r23 = r25
            r20 = 0
            r1 = r33
            r1.queryBinderState(r2)
            r1.monitorNet(r2)
            goto L_0x02fc
        L_0x02df:
            r28 = r0
            r24 = r1
            r21 = r3
            r22 = r4
            r23 = r5
            r20 = r6
            r1 = r7
            goto L_0x02fc
        L_0x02ed:
            r28 = r0
            r24 = r1
            r21 = r3
            r22 = r4
            r23 = r5
            r20 = r6
            r1 = r7
            r16 = r10
        L_0x02fc:
            int r0 = r28 + 1
            r7 = r1
            r10 = r16
            r6 = r20
            r3 = r21
            r4 = r22
            r5 = r23
            r1 = r24
            r12 = 64
            goto L_0x00ab
        L_0x030f:
            r28 = r0
            r24 = r1
            r22 = r4
            r23 = r5
            r16 = r10
            r2 = 64
            android.os.Trace.traceEnd(r2)
            long r2 = android.os.SystemClock.uptimeMillis()
            long r2 = r2 - r16
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            r0.append(r8)
            r0.append(r13)
            r0.append(r9)
            java.lang.String r4 = ", froze "
            r0.append(r4)
            r4 = r24
            r0.append(r4)
            java.lang.String r5 = " processes, took "
            r0.append(r5)
            r0.append(r2)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            android.util.Slog.d(r15, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.greeze.GreezeManagerService.handleLaunchBoost(int, java.lang.String, int):void");
    }

    public void gzLaunchBoost(int launchingUid, String launchingActivity, int fromUid, String fromPkg) {
        if (this.milletEnable && sEnable) {
            thawUid(launchingUid, 2, "Launching " + launchingActivity + " from " + fromUid + " " + fromPkg);
            if (TextUtils.equals("com.miui.home", fromPkg)) {
                if (DEBUG_LAUNCH_FROM_HOME) {
                    Slog.d(TAG, "Launching " + launchingActivity + " from " + fromUid + " " + fromPkg);
                }
                Message msg = this.mHandler.obtainMessage(2);
                msg.obj = new LaunchBoostData(launchingUid, launchingActivity, fromUid);
                msg.sendToTarget();
            }
        }
    }

    static final class LaunchBoostData {
        int fromUid;
        String launchingActivity;
        int launchingUid;

        LaunchBoostData(int launchingUid2, String launchingActivity2, int fromUid2) {
            this.launchingUid = launchingUid2;
            this.launchingActivity = launchingActivity2;
            this.fromUid = fromUid2;
        }
    }

    static class GreezeThread extends ServiceThread {
        private static GreezeThread sInstance;

        private GreezeThread() {
            super("Greezer", -2, true);
        }

        private static void ensureThreadLocked() {
            if (sInstance == null) {
                sInstance = new GreezeThread();
                sInstance.start();
            }
        }

        public static GreezeThread getInstance() {
            GreezeThread greezeThread;
            synchronized (GreezeThread.class) {
                ensureThreadLocked();
                greezeThread = sInstance;
            }
            return greezeThread;
        }
    }

    class H extends Handler {
        static final int MSG_LAUNCH_BOOST = 2;
        static final int MSG_MILLET_LOOPONCE = 4;
        static final int MSG_THAW_ALL = 3;
        static final int MSG_THAW_PID = 1;

        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 1) {
                if (i == 2) {
                    LaunchBoostData data = (LaunchBoostData) msg.obj;
                    try {
                        GreezeManagerService.this.handleLaunchBoost(data.launchingUid, data.launchingActivity, data.fromUid);
                    } catch (Exception e) {
                        Slog.w(GreezeManagerService.TAG, "SYH", e);
                    }
                } else if (i == 3) {
                    GreezeManagerService.this.thawAll("from msg");
                } else if (i == 4) {
                    GreezeManagerService.nLoopOnce();
                }
            } else if (msg.arg1 != 0) {
                int pid = msg.arg1;
                GreezeManagerService greezeManagerService = GreezeManagerService.this;
                int owner = ((FrozenInfo) msg.obj).getOwner();
                greezeManagerService.thawProcess(pid, owner, "Timeout pid " + pid);
            }
        }
    }

    static class RunningProcess {
        int adj;
        boolean hasForegroundActivities;
        boolean hasForegroundServices;
        int pid;
        String[] pkgList;
        int procState;
        String processName;
        int uid;

        RunningProcess(RunningProcessInfo info) {
            this.pid = info.mPid;
            this.uid = info.mUid;
            this.processName = info.mProcessName;
            this.pkgList = info.mPkgList != null ? info.mPkgList : new String[0];
            this.adj = info.mAdj;
            this.procState = info.mProcState;
            this.hasForegroundActivities = info.mHasForegroundActivities;
            this.hasForegroundServices = info.mHasForegroundServices;
        }

        RunningProcess(ActivityManager.RunningAppProcessInfo info) {
            this.pid = info.pid;
            this.uid = info.uid;
            this.processName = info.processName;
            this.pkgList = info.pkgList;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.uid);
            sb.append(" ");
            sb.append(this.pid);
            sb.append(" ");
            sb.append(this.processName);
            sb.append(" ");
            sb.append(Arrays.toString(this.pkgList));
            sb.append(" ");
            sb.append(this.adj);
            sb.append(" ");
            sb.append(this.procState);
            String str = "";
            sb.append(this.hasForegroundActivities ? " FA" : str);
            if (this.hasForegroundServices) {
                str = " FS";
            }
            sb.append(str);
            return sb.toString();
        }
    }

    static class FrozenInfo {
        List<String> mFreezeReasons = new ArrayList(16);
        List<Long> mFreezeTimes = new ArrayList(16);
        List<Integer> mFromWho = new ArrayList(16);
        String mThawReason;
        long mThawTime;
        int pid;
        String processName;
        int uid;

        FrozenInfo(RunningProcess processRecord) {
            this.uid = processRecord.uid;
            this.pid = processRecord.pid;
            this.processName = processRecord.processName;
        }

        /* access modifiers changed from: package-private */
        public void addFreezeInfo(long curTime, int fromWho, String reason) {
            this.mFreezeTimes.add(Long.valueOf(curTime));
            this.mFromWho.add(Integer.valueOf(fromWho));
            this.mFreezeReasons.add(reason);
        }

        /* access modifiers changed from: package-private */
        public long getStartTime() {
            if (this.mFreezeTimes.size() == 0) {
                return 0;
            }
            return this.mFreezeTimes.get(0).longValue();
        }

        /* access modifiers changed from: package-private */
        public long getEndTime() {
            return this.mThawTime;
        }

        /* access modifiers changed from: package-private */
        public long getFrozenDuration() {
            if (getStartTime() < getEndTime()) {
                return getEndTime() - getStartTime();
            }
            return 0;
        }

        /* access modifiers changed from: package-private */
        public int getOwner() {
            if (this.mFromWho.size() == 0) {
                return 0;
            }
            List<Integer> list = this.mFromWho;
            return list.get(list.size() - 1).intValue();
        }

        public String toString() {
            return this.uid + " " + this.pid + " " + this.processName;
        }
    }

    private static int ringAdvance(int origin, int increment, int size) {
        int index = (origin + increment) % size;
        return index < 0 ? index + size : index;
    }

    private void addHistoryInfo(FrozenInfo info) {
        FrozenInfo[] frozenInfoArr = this.mFrozenHistory;
        int i = this.mHistoryIndexNext;
        frozenInfoArr[i] = info;
        this.mHistoryIndexNext = ringAdvance(i, 1, HISTORY_SIZE);
    }

    private List<FrozenInfo> getHistoryInfos(long sinceUptime) {
        List<FrozenInfo> ret = new ArrayList<>();
        int index = ringAdvance(this.mHistoryIndexNext, -1, HISTORY_SIZE);
        for (int i = 0; i < HISTORY_SIZE; i++) {
            FrozenInfo[] frozenInfoArr = this.mFrozenHistory;
            if (frozenInfoArr[index] == null || frozenInfoArr[index].mThawTime < sinceUptime) {
                break;
            }
            ret.add(this.mFrozenHistory[index]);
            index = ringAdvance(index, -1, HISTORY_SIZE);
        }
        return ret;
    }

    /* access modifiers changed from: package-private */
    public void dumpHistory(String prefix, FileDescriptor fd, PrintWriter pw) {
        pw.println("Frozen processes in history:");
        List<FrozenInfo> infos = getHistoryInfos(SystemClock.uptimeMillis() - 14400000);
        int index = 1;
        SimpleDateFormat formater = new SimpleDateFormat(TIME_FORMAT_PATTERN);
        for (FrozenInfo info : infos) {
            pw.print(prefix + "  ");
            StringBuilder sb = new StringBuilder();
            sb.append("#");
            int index2 = index + 1;
            sb.append(index);
            pw.print(sb.toString());
            pw.print(" " + formater.format(new Date(info.mThawTime)));
            if (info.uid != 0) {
                pw.print(" " + info.uid);
            }
            pw.print(" " + info.pid);
            if (!TextUtils.isEmpty(info.processName)) {
                pw.print(" " + info.processName);
            }
            pw.println(" " + info.getFrozenDuration() + "ms");
            for (int i = 0; i < info.mFreezeTimes.size(); i++) {
                pw.print(prefix + "    ");
                pw.print("fz: ");
                pw.print(formater.format(new Date(info.mFreezeTimes.get(i).longValue())));
                pw.print(" " + info.mFreezeReasons.get(i));
                pw.println(" from " + info.mFromWho.get(i));
            }
            pw.print(prefix + "    ");
            pw.print("th: ");
            pw.print(formater.format(new Date(info.mThawTime)));
            pw.println(" " + info.mThawReason);
            index = index2;
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpSettings(String prefix, FileDescriptor fd, PrintWriter pw) {
        pw.println(prefix + "Settings:");
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("  enable=");
        sb.append(this.milletEnable && sEnable);
        sb.append(" (");
        sb.append(PROPERTY_GZ_ENABLE);
        sb.append(")");
        pw.println(sb.toString());
        pw.println(prefix + "  debug=" + DEBUG + " (" + PROPERTY_GZ_DEBUG + ")");
        pw.println(prefix + "  monkey=" + DEBUG_MONKEY + " (" + PROPERTY_GZ_MONKEY + ")");
        pw.println(prefix + "  fz_timeout=" + LAUNCH_FZ_TIMEOUT + " (" + PROPERTY_GZ_FZTIMEOUT + ")");
        pw.println(prefix + "  monitor=" + this.milletEnable + " (" + this.mRegisteredMonitor + ")");
    }

    /* access modifiers changed from: package-private */
    public void dumpFrozen(String prefix, FileDescriptor fd, PrintWriter pw) {
        List<Integer> pids = FreezeUtils.getFrozenPids();
        pw.println(prefix + "Frozen pids: " + pids);
        List<Integer> tids = FreezeUtils.getFrozonTids();
        pw.println(prefix + "Frozen tids: " + tids);
        SimpleDateFormat formater = new SimpleDateFormat(TIME_FORMAT_PATTERN);
        pw.println(prefix + "Frozen processes:");
        synchronized (this.mFrozenPids) {
            int n = this.mFrozenPids.size();
            for (int i = 0; i < n; i++) {
                FrozenInfo info = this.mFrozenPids.valueAt(i);
                pw.print(prefix + "  ");
                pw.print("#" + (i + 1));
                pw.println(" pid=" + info.pid);
                for (int index = 0; index < info.mFreezeTimes.size(); index++) {
                    pw.print(prefix + "    ");
                    pw.print("fz: ");
                    pw.print(formater.format(new Date(info.mFreezeTimes.get(index).longValue())));
                    pw.print(" " + info.mFreezeReasons.get(index));
                    pw.println(" from " + info.mFromWho.get(index));
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            dumpSettings("", fd, pw);
            dumpFrozen("", fd, pw);
            dumpHistory("", fd, pw);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.miui.server.greeze.GreezeManagerService$GreezeMangaerShellCommand r0 = new com.miui.server.greeze.GreezeManagerService$GreezeMangaerShellCommand
            r0.<init>(r8)
            r1 = r8
            r2 = r9
            r3 = r10
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r14
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.greeze.GreezeManagerService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    static class GreezeMangaerShellCommand extends ShellCommand {
        GreezeManagerService mService;

        GreezeMangaerShellCommand(GreezeManagerService service) {
            this.mService = service;
        }

        private void runDumpHistory() {
            this.mService.dumpHistory("", getOutFileDescriptor(), getOutPrintWriter());
        }

        private void runListProcesses() {
            PrintWriter pw = getOutPrintWriter();
            List<RunningProcess> list = this.mService.getProcessList();
            pw.println("process total " + list.size());
            for (int i = 0; i < list.size(); i++) {
                pw.printf("  #%d %s", new Object[]{Integer.valueOf(i + 1), list.get(i).toString()});
                pw.println();
            }
        }

        private void runDumpPackages() {
            PrintWriter pw = getOutPrintWriter();
            ProcessMap<List<RunningProcess>> procMap = this.mService.getPkgMap();
            for (String pkgName : procMap.getMap().keySet()) {
                pw.println("pkg " + pkgName);
                SparseArray<List<RunningProcess>> uids = (SparseArray) procMap.getMap().get(pkgName);
                for (int i = 0; i < uids.size(); i++) {
                    int keyAt = uids.keyAt(i);
                    List<RunningProcess> procs = uids.valueAt(i);
                    if (procs != null) {
                        for (RunningProcess proc : procs) {
                            pw.println("  " + proc.toString());
                        }
                    }
                }
            }
        }

        private void runDumpUids() {
            PrintWriter pw = getOutPrintWriter();
            SparseArray<List<RunningProcess>> uidMap = this.mService.getUidMap();
            int N = uidMap.size();
            pw.println("uid total " + N);
            for (int i = 0; i < N; i++) {
                pw.printf("#%d uid %d", new Object[]{Integer.valueOf(i + 1), Integer.valueOf(uidMap.keyAt(i))});
                pw.println();
                for (RunningProcess proc : uidMap.valueAt(i)) {
                    pw.println("  " + proc.toString());
                }
            }
        }

        private void dumpSkipUid() {
            PrintWriter pw = getOutPrintWriter();
            pw.println("audio uid: " + this.mService.getAudioUid());
            pw.println("ime uid: " + this.mService.getIMEUid());
            try {
                ForegroundInfo foregroundInfo = ProcessManager.getForegroundInfo();
                pw.println("foreground uid: " + foregroundInfo.mForegroundUid);
                pw.println("multi window uid: " + foregroundInfo.mMultiWindowForegroundUid);
            } catch (Exception e) {
                Slog.w(GreezeManagerService.TAG, "Failed to get foreground info from ProcessManager", e);
            }
            List<RunningProcess> procs = this.mService.getProcessList();
            Set<Integer> foreActs = new ArraySet<>();
            Set<Integer> foreSvcs = new ArraySet<>();
            for (RunningProcess proc : procs) {
                if (proc.hasForegroundActivities) {
                    foreActs.add(Integer.valueOf(proc.uid));
                }
                if (proc.hasForegroundServices) {
                    foreSvcs.add(Integer.valueOf(proc.uid));
                }
            }
            pw.println("fore act uid: " + foreActs);
            pw.println("fore svc uid: " + foreSvcs);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int onCommand(java.lang.String r14) {
            /*
                r13 = this;
                java.lang.String r0 = ""
                java.io.PrintWriter r1 = r13.getOutPrintWriter()
                if (r14 != 0) goto L_0x000d
                int r0 = r13.handleDefaultCommands(r14)
                return r0
            L_0x000d:
                r2 = -1
                int r3 = r14.hashCode()     // Catch:{ Exception -> 0x0276 }
                r4 = 1
                r5 = 0
                switch(r3) {
                    case -1346846559: goto L_0x00ee;
                    case -1298848381: goto L_0x00e3;
                    case -452147603: goto L_0x00d9;
                    case -75092327: goto L_0x00ce;
                    case 3463: goto L_0x00c3;
                    case 3587: goto L_0x00b8;
                    case 111052: goto L_0x00ad;
                    case 115792: goto L_0x00a2;
                    case 3327652: goto L_0x0097;
                    case 3331227: goto L_0x008b;
                    case 3532159: goto L_0x007f;
                    case 3558826: goto L_0x0074;
                    case 95458899: goto L_0x0068;
                    case 97944631: goto L_0x005d;
                    case 97949436: goto L_0x0052;
                    case 107944136: goto L_0x0046;
                    case 110337687: goto L_0x003b;
                    case 110342492: goto L_0x0030;
                    case 926934164: goto L_0x0024;
                    case 1236319578: goto L_0x0019;
                    default: goto L_0x0017;
                }     // Catch:{ Exception -> 0x0276 }
            L_0x0017:
                goto L_0x00f8
            L_0x0019:
                java.lang.String r3 = "monitor"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 5
                goto L_0x00f9
            L_0x0024:
                java.lang.String r3 = "history"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 17
                goto L_0x00f9
            L_0x0030:
                java.lang.String r3 = "thuid"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 2
                goto L_0x00f9
            L_0x003b:
                java.lang.String r3 = "thpid"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 3
                goto L_0x00f9
            L_0x0046:
                java.lang.String r3 = "query"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 8
                goto L_0x00f9
            L_0x0052:
                java.lang.String r3 = "fzuid"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = r5
                goto L_0x00f9
            L_0x005d:
                java.lang.String r3 = "fzpid"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = r4
                goto L_0x00f9
            L_0x0068:
                java.lang.String r3 = "debug"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 16
                goto L_0x00f9
            L_0x0074:
                java.lang.String r3 = "thaw"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 4
                goto L_0x00f9
            L_0x007f:
                java.lang.String r3 = "skip"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 15
                goto L_0x00f9
            L_0x008b:
                java.lang.String r3 = "lsfz"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 10
                goto L_0x00f9
            L_0x0097:
                java.lang.String r3 = "loop"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 18
                goto L_0x00f9
            L_0x00a2:
                java.lang.String r3 = "uid"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 11
                goto L_0x00f9
            L_0x00ad:
                java.lang.String r3 = "pkg"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 13
                goto L_0x00f9
            L_0x00b8:
                java.lang.String r3 = "ps"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 12
                goto L_0x00f9
            L_0x00c3:
                java.lang.String r3 = "ls"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 9
                goto L_0x00f9
            L_0x00ce:
                java.lang.String r3 = "getUids"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 19
                goto L_0x00f9
            L_0x00d9:
                java.lang.String r3 = "clearmonitor"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 7
                goto L_0x00f9
            L_0x00e3:
                java.lang.String r3 = "enable"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 14
                goto L_0x00f9
            L_0x00ee:
                java.lang.String r3 = "unmonitor"
                boolean r3 = r14.equals(r3)     // Catch:{ Exception -> 0x0276 }
                if (r3 == 0) goto L_0x0017
                r3 = 6
                goto L_0x00f9
            L_0x00f8:
                r3 = r2
            L_0x00f9:
                switch(r3) {
                    case 0: goto L_0x0256;
                    case 1: goto L_0x0238;
                    case 2: goto L_0x021c;
                    case 3: goto L_0x0200;
                    case 4: goto L_0x01f6;
                    case 5: goto L_0x01e8;
                    case 6: goto L_0x01da;
                    case 7: goto L_0x01d4;
                    case 8: goto L_0x01c6;
                    case 9: goto L_0x01ab;
                    case 10: goto L_0x0191;
                    case 11: goto L_0x018d;
                    case 12: goto L_0x0189;
                    case 13: goto L_0x0185;
                    case 14: goto L_0x0165;
                    case 15: goto L_0x0161;
                    case 16: goto L_0x0133;
                    case 17: goto L_0x012f;
                    case 18: goto L_0x012b;
                    case 19: goto L_0x0102;
                    default: goto L_0x00fc;
                }     // Catch:{ Exception -> 0x0276 }
            L_0x00fc:
                int r0 = r13.handleDefaultCommands(r14)     // Catch:{ Exception -> 0x0276 }
                goto L_0x0275
            L_0x0102:
                java.lang.String r0 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0276 }
                com.miui.server.greeze.GreezeManagerService r3 = r13.mService     // Catch:{ Exception -> 0x0276 }
                int[] r3 = r3.getFrozenUids(r0)     // Catch:{ Exception -> 0x0276 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0276 }
                r4.<init>()     // Catch:{ Exception -> 0x0276 }
                java.lang.String r5 = "Frozen uids : "
                r4.append(r5)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r5 = java.util.Arrays.toString(r3)     // Catch:{ Exception -> 0x0276 }
                r4.append(r5)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0276 }
                r1.println(r4)     // Catch:{ Exception -> 0x0276 }
                goto L_0x027a
            L_0x012b:
                com.miui.server.greeze.GreezeManagerService.nLoopOnce()     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x012f:
                r13.runDumpHistory()     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0133:
                java.lang.String r0 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                boolean r0 = java.lang.Boolean.parseBoolean(r0)     // Catch:{ Exception -> 0x0276 }
                boolean r3 = com.miui.server.greeze.GreezeManagerService.DEBUG_MILLET = r0     // Catch:{ Exception -> 0x0276 }
                boolean r3 = com.miui.server.greeze.GreezeManagerService.DEBUG_LAUNCH_FROM_HOME = r3     // Catch:{ Exception -> 0x0276 }
                boolean r3 = com.miui.server.greeze.GreezeManagerService.DEBUG_AIDL = r3     // Catch:{ Exception -> 0x0276 }
                boolean unused = com.miui.server.greeze.GreezeManagerService.DEBUG = r3     // Catch:{ Exception -> 0x0276 }
                com.miui.server.greeze.FreezeUtils.DEBUG = r0     // Catch:{ Exception -> 0x0276 }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0276 }
                r3.<init>()     // Catch:{ Exception -> 0x0276 }
                java.lang.String r4 = "launch debug log enabled "
                r3.append(r4)     // Catch:{ Exception -> 0x0276 }
                r3.append(r0)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0276 }
                r1.println(r3)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0161:
                r13.dumpSkipUid()     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0165:
                java.lang.String r0 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                boolean r0 = java.lang.Boolean.parseBoolean(r0)     // Catch:{ Exception -> 0x0276 }
                boolean unused = com.miui.server.greeze.GreezeManagerService.sEnable = r0     // Catch:{ Exception -> 0x0276 }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0276 }
                r3.<init>()     // Catch:{ Exception -> 0x0276 }
                java.lang.String r4 = "launch freeze enabled "
                r3.append(r4)     // Catch:{ Exception -> 0x0276 }
                r3.append(r0)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0276 }
                r1.println(r3)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0185:
                r13.runDumpPackages()     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0189:
                r13.runListProcesses()     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x018d:
                r13.runDumpUids()     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0191:
                java.io.PrintWriter r0 = r13.getOutPrintWriter()     // Catch:{ Exception -> 0x0276 }
                com.miui.server.greeze.GreezeManagerService r3 = r13.mService     // Catch:{ Exception -> 0x0276 }
                java.lang.String r4 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0276 }
                int[] r3 = r3.getFrozenPids(r4)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r3 = java.util.Arrays.toString(r3)     // Catch:{ Exception -> 0x0276 }
                r0.println(r3)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x01ab:
                com.miui.server.greeze.GreezeManagerService r3 = r13.mService     // Catch:{ Exception -> 0x0276 }
                java.io.FileDescriptor r4 = r13.getOutFileDescriptor()     // Catch:{ Exception -> 0x0276 }
                java.io.PrintWriter r6 = r13.getOutPrintWriter()     // Catch:{ Exception -> 0x0276 }
                r3.dumpSettings(r0, r4, r6)     // Catch:{ Exception -> 0x0276 }
                com.miui.server.greeze.GreezeManagerService r3 = r13.mService     // Catch:{ Exception -> 0x0276 }
                java.io.FileDescriptor r4 = r13.getOutFileDescriptor()     // Catch:{ Exception -> 0x0276 }
                java.io.PrintWriter r6 = r13.getOutPrintWriter()     // Catch:{ Exception -> 0x0276 }
                r3.dumpFrozen(r0, r4, r6)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x01c6:
                com.miui.server.greeze.GreezeManagerService r0 = r13.mService     // Catch:{ Exception -> 0x0276 }
                java.lang.String r3 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x0276 }
                r0.queryBinderState(r3)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x01d4:
                com.miui.server.greeze.GreezeManagerService r0 = r13.mService     // Catch:{ Exception -> 0x0276 }
                r0.clearMonitorNet()     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x01da:
                com.miui.server.greeze.GreezeManagerService r0 = r13.mService     // Catch:{ Exception -> 0x0276 }
                java.lang.String r3 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x0276 }
                r0.clearMonitorNet(r3)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x01e8:
                com.miui.server.greeze.GreezeManagerService r0 = r13.mService     // Catch:{ Exception -> 0x0276 }
                java.lang.String r3 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x0276 }
                r0.monitorNet(r3)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x01f6:
                com.miui.server.greeze.GreezeManagerService r0 = r13.mService     // Catch:{ Exception -> 0x0276 }
                java.lang.String r3 = "ShellCommand: thaw all"
                r4 = 9999(0x270f, float:1.4012E-41)
                r0.thawAll(r4, r4, r3)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0200:
                com.miui.server.greeze.GreezeManagerService r0 = r13.mService     // Catch:{ Exception -> 0x0276 }
                int[] r3 = new int[r4]     // Catch:{ Exception -> 0x0276 }
                java.lang.String r4 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0276 }
                r3[r5] = r4     // Catch:{ Exception -> 0x0276 }
                java.lang.String r4 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r6 = "cmd: thpid"
                r0.thawPids(r3, r4, r6)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x021c:
                com.miui.server.greeze.GreezeManagerService r0 = r13.mService     // Catch:{ Exception -> 0x0276 }
                int[] r3 = new int[r4]     // Catch:{ Exception -> 0x0276 }
                java.lang.String r4 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0276 }
                r3[r5] = r4     // Catch:{ Exception -> 0x0276 }
                java.lang.String r4 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r6 = "cmd: thuid"
                r0.thawUids(r3, r4, r6)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0238:
                com.miui.server.greeze.GreezeManagerService r6 = r13.mService     // Catch:{ Exception -> 0x0276 }
                int[] r7 = new int[r4]     // Catch:{ Exception -> 0x0276 }
                java.lang.String r0 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0276 }
                r7[r5] = r0     // Catch:{ Exception -> 0x0276 }
                r8 = 0
                java.lang.String r0 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r10 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r11 = "cmd: fzpid"
                r6.freezePids(r7, r8, r10, r11)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0256:
                com.miui.server.greeze.GreezeManagerService r6 = r13.mService     // Catch:{ Exception -> 0x0276 }
                int[] r7 = new int[r4]     // Catch:{ Exception -> 0x0276 }
                java.lang.String r0 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0276 }
                r7[r5] = r0     // Catch:{ Exception -> 0x0276 }
                r8 = 0
                java.lang.String r0 = r13.getNextArgRequired()     // Catch:{ Exception -> 0x0276 }
                int r10 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0276 }
                java.lang.String r11 = "cmd: fzuid"
                r12 = 0
                r6.freezeUids(r7, r8, r10, r11, r12)     // Catch:{ Exception -> 0x0276 }
                return r5
            L_0x0275:
                return r0
            L_0x0276:
                r0 = move-exception
                r1.println(r0)
            L_0x027a:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.server.greeze.GreezeManagerService.GreezeMangaerShellCommand.onCommand(java.lang.String):int");
        }

        public void onHelp() {
            PrintWriter pw = getOutPrintWriter();
            pw.println("Greeze manager (greezer) commands:");
            pw.println();
            pw.println("  ls lsfz");
            pw.println("  history");
            pw.println();
            pw.println("  fzpid PID");
            pw.println("  fzuid UID");
            pw.println();
            pw.println("  thpid PID");
            pw.println("  thuid UID");
            pw.println("  thaw");
            pw.println();
            pw.println("  monitor/unmonitor UID");
            pw.println("  clearmonitor");
            pw.println();
            pw.println("  query UID");
            pw.println("    Query binder state in all processes of UID");
            pw.println();
            pw.println("  uid pkg ps");
            pw.println();
            pw.println("  enable true/false");
        }
    }
}
