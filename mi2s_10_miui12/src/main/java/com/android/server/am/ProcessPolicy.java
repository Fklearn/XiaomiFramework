package com.android.server.am;

import android.content.Context;
import android.miui.R;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.BatteryStatsImplInjector;
import com.android.server.ServiceThread;
import com.android.server.pm.DumpState;
import com.miui.enterprise.settings.EnterpriseSettings;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;
import miui.process.ProcessCloudData;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProcessPolicy implements BatteryStatsImplInjector.ActiveCallback {
    private static final double CAMERA_BOOST_THRESHOLD_PERCENT = (Process.getTotalMemory() / RAM_SIZE_1GB < 3 ? 0.375d : 0.25d);
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_ACTIVE = true;
    private static final long DEFAULT_FASTBOOT_THRESHOLDKB = 524288000;
    private static final String DEVICE = Build.DEVICE.toLowerCase();
    private static final boolean DYNAMIC_LIST_CHECK_ADJ = true;
    public static final int FLAG_CAMERA_BOOST_PROTECT_LIST = 8192;
    public static final int FLAG_CLOUD_WHITE_LIST = 4;
    public static final int FLAG_DISABLE_FORCE_STOP = 32;
    public static final int FLAG_DISABLE_TRIM_MEMORY = 16;
    public static final int FLAG_DYNAMIC_WHITE_LIST = 2;
    public static final int FLAG_ENABLE_CALL_PROTECT = 64;
    public static final int FLAG_ENTERPRISE_APP_LIST = 4096;
    public static final int FLAG_FAST_BOOT_APP_LIST = 2048;
    public static final int FLAG_NEED_TRACE_LIST = 128;
    public static final int FLAG_SECRETLY_PROTECT_APP_LIST = 1024;
    public static final int FLAG_STATIC_WHILTE_LIST = 1;
    public static final int FLAG_USER_DEFINED_LIST = 8;
    private static final String JSON_KEY_PACKAGE_NAMES = "pkgs";
    private static final String JSON_KEY_USER_ID = "u";
    private static final int MSG_UPDATE_AUDIO_OFF = 1;
    private static final int MSG_UPDATE_STOP_GPS = 2;
    private static final int PERCEPTIBLE_APP_ADJ = (Build.VERSION.SDK_INT > 23 ? 200 : 2);
    private static final String PREFS_LOCKED_APPS = "Locked_apps";
    private static final int PRIORITY_LEVEL_HEAVY_WEIGHT = 3;
    private static final int PRIORITY_LEVEL_PERCEPTIBLE = 2;
    private static final int PRIORITY_LEVEL_UNKNOWN = -1;
    private static final int PRIORITY_LEVEL_VISIBLE = 1;
    private static final long RAM_SIZE_1GB = 1073741824;
    public static final String REASON_ANR = "anr";
    public static final String REASON_AUTO_IDLE_KILL = "AutoIdleKill";
    public static final String REASON_AUTO_LOCK_OFF_CLEAN = "AutoLockOffClean";
    public static final String REASON_AUTO_LOCK_OFF_CLEAN_BY_PRIORITY = "AutoLockOffCleanByPriority";
    public static final String REASON_AUTO_POWER_KILL = "AutoPowerKill";
    public static final String REASON_AUTO_SLEEP_CLEAN = "AutoSleepClean";
    public static final String REASON_AUTO_SYSTEM_ABNORMAL_CLEAN = "AutoSystemAbnormalClean";
    public static final String REASON_AUTO_THERMAL_KILL = "AutoThermalKill";
    public static final String REASON_CRASH = "crash";
    public static final String REASON_FORCE_CLEAN = "ForceClean";
    public static final String REASON_GAME_CLEAN = "GameClean";
    public static final String REASON_GARBAGE_CLEAN = "GarbageClean";
    public static final String REASON_LOCK_SCREEN_CLEAN = "LockScreenClean";
    public static final String REASON_ONE_KEY_CLEAN = "OneKeyClean";
    public static final String REASON_OPTIMIZATION_CLEAN = "OptimizationClean";
    public static final String REASON_SWIPE_UP_CLEAN = "SwipeUpClean";
    public static final String REASON_UNKNOWN = "Unknown";
    public static final String REASON_USER_DEFINED = "UserDefined";
    public static final String TAG = "ProcessManager";
    public static final String TAG_PM = "ProcessManager";
    public static final boolean TAG_WITH_CLASS_NAME = false;
    private static final long UPDATE_AUDIO_OFF_DELAY = 600;
    private static final long UPDATE_STOP_GPS_DELAY = 1000;
    public static final int USER_ALL = -100;
    /* access modifiers changed from: private */
    @GuardedBy({"sLock"})
    public static SparseArray<ActiveUidRecord> sActiveUidList = new SparseArray<>();
    @GuardedBy({"sLock"})
    private static Map<String, Integer> sAppProtectMap = new HashMap();
    @GuardedBy({"sLock"})
    private static Map<String, String> sBoundFgServiceProtectMap = new HashMap();
    @GuardedBy({"sLock"})
    private static List<String> sCameraBoostProtectList = new ArrayList();
    @GuardedBy({"sLock"})
    private static Map<String, Integer> sCameraMemThresholdMap = new HashMap();
    @GuardedBy({"sLock"})
    private static List<String> sCloudWhiteList = new ArrayList();
    @GuardedBy({"sLock"})
    private static List<String> sDisableForceStopList = new ArrayList();
    @GuardedBy({"sLock"})
    private static List<String> sDisableTrimList = new ArrayList();
    @GuardedBy({"sLock"})
    private static HashMap<String, Boolean> sDynamicWhiteList = new HashMap<>();
    @GuardedBy({"sLock"})
    private static List<String> sEnableCallProtectList = new ArrayList();
    @GuardedBy({"sLock"})
    private static List<String> sEnterpriseAppList = new ArrayList();
    static List<String> sExpKillProcReasons = new ArrayList();
    @GuardedBy({"sLock"})
    private static Map<String, Long> sFastBootAppMap = new HashMap();
    @GuardedBy({"sLock"})
    private static List<String> sFgServiceCheckList = new ArrayList();
    @GuardedBy({"sLock"})
    private static Map<String, Integer> sFgServiceProtectMap = new HashMap();
    /* access modifiers changed from: private */
    public static final Object sLock = new Object();
    @GuardedBy({"sLock"})
    private static HashMap<Integer, Set<String>> sLockedApplicationList = new HashMap<>();
    @GuardedBy({"sLock"})
    private static List<String> sNeedTraceList = new ArrayList();
    public static final SparseArray<Pair<Integer, Integer>> sProcessPriorityMap = new SparseArray<>();
    @GuardedBy({"sLock"})
    private static List<String> sSecretlyProtectAppList = new ArrayList();
    @GuardedBy({"sLock"})
    private static List<String> sStaticWhiteList = new ArrayList();
    /* access modifiers changed from: private */
    @GuardedBy({"sLock"})
    public static SparseArray<ActiveUidRecord> sTempInactiveAudioList = new SparseArray<>();
    /* access modifiers changed from: private */
    @GuardedBy({"sLock"})
    public static SparseArray<ActiveUidRecord> sTempInactiveGPSList = new SparseArray<>();
    @GuardedBy({"sLock"})
    private static List<String> sUserDefinedWhiteList = new ArrayList();
    static List<String> sUserKillProcReasons = new ArrayList();
    private AccessibilityManager mAccessibilityManager;
    private ActiveUpdateHandler mActiveUpdateHandler;
    private ActivityManagerService mActivityManagerService;
    private ProcessManagerService mProcessManagerService;

    static {
        sUserKillProcReasons.add(REASON_ONE_KEY_CLEAN);
        sUserKillProcReasons.add(REASON_FORCE_CLEAN);
        sUserKillProcReasons.add(REASON_GARBAGE_CLEAN);
        sUserKillProcReasons.add(REASON_GAME_CLEAN);
        sUserKillProcReasons.add(REASON_OPTIMIZATION_CLEAN);
        sUserKillProcReasons.add(REASON_SWIPE_UP_CLEAN);
        sUserKillProcReasons.add(REASON_USER_DEFINED);
        sExpKillProcReasons.add(REASON_ANR);
        sExpKillProcReasons.add(REASON_CRASH);
        boolean isLowMemory = ProcessRecordInjector.isLowMemoryDevice();
        long j = 921600;
        sFastBootAppMap.put("com.tencent.mm", Long.valueOf(isLowMemory ? 921600 : 1024000));
        Map<String, Long> map = sFastBootAppMap;
        if (!isLowMemory) {
            j = 1024000;
        }
        map.put("com.tencent.mobileqq", Long.valueOf(j));
        sProcessPriorityMap.put(-1, ProcessUtils.PRIORITY_UNKNOW);
        sProcessPriorityMap.put(1, ProcessUtils.PRIORITY_VISIBLE);
        sProcessPriorityMap.put(2, ProcessUtils.PRIORITY_PERCEPTIBLE);
        sProcessPriorityMap.put(3, ProcessUtils.PRIORITY_HEAVY);
        sAppProtectMap.put("com.miui.voip", 1);
        sAppProtectMap.put("com.miui.bugreport", 1);
        sAppProtectMap.put("com.xiaomi.miplay", 1);
        sAppProtectMap.put("com.miui.virtualsim", 1);
        sAppProtectMap.put("com.miui.touchassistant", 1);
        sAppProtectMap.put("com.xiaomi.joyose", 1);
        sAppProtectMap.put("com.miui.tsmclient", 1);
        sBoundFgServiceProtectMap.put("com.milink.service", "com.xiaomi.miplay_client");
        sFgServiceProtectMap.put("com.miui.voiceassist", 1);
        sCameraMemThresholdMap.put("polaris", 1572864);
        sCameraMemThresholdMap.put("sirius", Integer.valueOf(DumpState.DUMP_DEXOPT));
        sCameraMemThresholdMap.put("dipper", 1572864);
        sCameraMemThresholdMap.put("ursa", 1572864);
        sCameraMemThresholdMap.put("perseus", 1887232);
        sCameraMemThresholdMap.put("equuleus", 1572864);
        sCameraMemThresholdMap.put("cactus", 768000);
    }

    class ActiveUpdateHandler extends Handler {
        public ActiveUpdateHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            ActiveUidRecord uidRecord = (ActiveUidRecord) msg.obj;
            int i = msg.what;
            if (i == 1) {
                checkRemoveActiveUid(uidRecord, 1);
            } else if (i == 2) {
                checkRemoveActiveUid(uidRecord, 2);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void checkRemoveActiveUid(com.android.server.am.ProcessPolicy.ActiveUidRecord r5, int r6) {
            /*
                r4 = this;
                if (r5 == 0) goto L_0x005d
                java.lang.Object r0 = com.android.server.am.ProcessPolicy.sLock
                monitor-enter(r0)
                r1 = 1
                if (r6 == r1) goto L_0x001b
                r1 = 2
                if (r6 == r1) goto L_0x0011
                monitor-exit(r0)     // Catch:{ all -> 0x000f }
                return
            L_0x000f:
                r1 = move-exception
                goto L_0x005b
            L_0x0011:
                android.util.SparseArray r1 = com.android.server.am.ProcessPolicy.sTempInactiveGPSList     // Catch:{ all -> 0x000f }
                int r2 = r5.uid     // Catch:{ all -> 0x000f }
                r1.remove(r2)     // Catch:{ all -> 0x000f }
                goto L_0x0025
            L_0x001b:
                android.util.SparseArray r1 = com.android.server.am.ProcessPolicy.sTempInactiveAudioList     // Catch:{ all -> 0x000f }
                int r2 = r5.uid     // Catch:{ all -> 0x000f }
                r1.remove(r2)     // Catch:{ all -> 0x000f }
            L_0x0025:
                int r1 = r5.flag     // Catch:{ all -> 0x000f }
                int r2 = ~r6     // Catch:{ all -> 0x000f }
                r1 = r1 & r2
                r5.flag = r1     // Catch:{ all -> 0x000f }
                int r1 = r5.flag     // Catch:{ all -> 0x000f }
                if (r1 != 0) goto L_0x0059
                android.util.SparseArray r1 = com.android.server.am.ProcessPolicy.sActiveUidList     // Catch:{ all -> 0x000f }
                int r2 = r5.uid     // Catch:{ all -> 0x000f }
                r1.remove(r2)     // Catch:{ all -> 0x000f }
                java.lang.String r1 = "ProcessManager"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x000f }
                r2.<init>()     // Catch:{ all -> 0x000f }
                java.lang.String r3 = "real remove inactive uid : "
                r2.append(r3)     // Catch:{ all -> 0x000f }
                int r3 = r5.uid     // Catch:{ all -> 0x000f }
                r2.append(r3)     // Catch:{ all -> 0x000f }
                java.lang.String r3 = " flag : "
                r2.append(r3)     // Catch:{ all -> 0x000f }
                r2.append(r6)     // Catch:{ all -> 0x000f }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x000f }
                android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x000f }
            L_0x0059:
                monitor-exit(r0)     // Catch:{ all -> 0x000f }
                goto L_0x005d
            L_0x005b:
                monitor-exit(r0)     // Catch:{ all -> 0x000f }
                throw r1
            L_0x005d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessPolicy.ActiveUpdateHandler.checkRemoveActiveUid(com.android.server.am.ProcessPolicy$ActiveUidRecord, int):void");
        }
    }

    public static final class ActiveUidRecord {
        static final int ACTIVE_AUDIO = 1;
        static final int ACTIVE_GPS = 2;
        static final int NO_ACTIVE = 0;
        public int flag;
        public int uid;

        public ActiveUidRecord(int _uid) {
            this.uid = _uid;
        }

        private void makeActiveString(StringBuilder sb) {
            sb.append("flag :");
            sb.append(this.flag);
            sb.append(' ');
            boolean printed = false;
            int i = this.flag;
            if (i == 0) {
                sb.append("NONE");
                return;
            }
            if ((i & 1) != 0) {
                printed = true;
                sb.append("A");
            }
            if ((this.flag & 2) != 0) {
                if (printed) {
                    sb.append("|");
                }
                sb.append("G");
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ActiveUidRecord{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            UserHandle.formatUid(sb, this.uid);
            sb.append(' ');
            makeActiveString(sb);
            sb.append("}");
            return sb.toString();
        }
    }

    public ProcessPolicy(ProcessManagerService processManagerService, ActivityManagerService ams, AccessibilityManager accessibilityManager, ServiceThread thread) {
        this.mProcessManagerService = processManagerService;
        this.mActivityManagerService = ams;
        this.mAccessibilityManager = accessibilityManager;
        this.mActiveUpdateHandler = new ActiveUpdateHandler(thread.getLooper());
    }

    public void systemReady(Context context) {
        synchronized (sLock) {
            sStaticWhiteList = Arrays.asList(context.getResources().getStringArray(R.array.process_static_white_list));
            sDisableTrimList = Arrays.asList(context.getResources().getStringArray(R.array.process_disable_trim_list));
            sDisableForceStopList = Arrays.asList(context.getResources().getStringArray(R.array.process_disable_force_stop_list));
            sNeedTraceList = Arrays.asList(context.getResources().getStringArray(R.array.need_trace_list));
            sSecretlyProtectAppList = new ArrayList(Arrays.asList(context.getResources().getStringArray(R.array.process_secretly_protect_list)));
            sFgServiceCheckList = Arrays.asList(context.getResources().getStringArray(R.array.process_fg_service_check_list));
            sCameraBoostProtectList = Arrays.asList(context.getResources().getStringArray(R.array.process_camera_boost_protect_list));
        }
        loadLockedAppFromSettings(context);
        updateApplicationLockedState("com.jeejen.family.miui", -100, true);
    }

    public List<String> getWhiteList(int flags) {
        List<String> whiteList = new ArrayList<>();
        synchronized (sLock) {
            if ((flags & 1) != 0) {
                try {
                    whiteList.addAll(sStaticWhiteList);
                } catch (Throwable th) {
                    throw th;
                }
            }
            if ((flags & 2) != 0) {
                whiteList.addAll(sDynamicWhiteList.keySet());
            }
            if ((flags & 4) != 0) {
                whiteList.addAll(sCloudWhiteList);
            }
            if ((flags & 8) != 0) {
                whiteList.addAll(sUserDefinedWhiteList);
            }
            if ((flags & 16) != 0) {
                whiteList.addAll(sDisableTrimList);
            }
            if ((flags & 32) != 0) {
                whiteList.addAll(sDisableForceStopList);
            }
            if ((flags & 64) != 0) {
                whiteList.addAll(sEnableCallProtectList);
            }
            if ((flags & 128) != 0) {
                whiteList.addAll(sNeedTraceList);
            }
            if ((flags & 1024) != 0) {
                whiteList.addAll(sSecretlyProtectAppList);
            }
            if ((flags & 2048) != 0) {
                whiteList.addAll(sFastBootAppMap.keySet());
            }
            if (EnterpriseSettings.ENTERPRISE_ACTIVATED && (flags & 4096) != 0) {
                whiteList.addAll(sEnterpriseAppList);
            }
            if ((flags & 8192) != 0) {
                whiteList.addAll(sCameraBoostProtectList);
            }
        }
        return whiteList;
    }

    public void addWhiteList(int flag, List<String> whiteList, boolean append) {
        List<String> targetWhiteList;
        synchronized (sLock) {
            if ((flag & 1) != 0) {
                try {
                    targetWhiteList = sStaticWhiteList;
                } catch (Throwable th) {
                    throw th;
                }
            } else if ((flag & 4) != 0) {
                targetWhiteList = sCloudWhiteList;
            } else if ((flag & 8) != 0) {
                targetWhiteList = sUserDefinedWhiteList;
            } else if ((flag & 16) != 0) {
                targetWhiteList = sDisableTrimList;
            } else if ((flag & 32) != 0) {
                targetWhiteList = sDisableForceStopList;
            } else if (!EnterpriseSettings.ENTERPRISE_ACTIVATED || (flag & 4096) == 0) {
                targetWhiteList = new ArrayList<>();
                Slog.e("ProcessManager", "addWhiteList with unknown flag=" + flag);
            } else {
                targetWhiteList = sEnterpriseAppList;
            }
            if (!append) {
                targetWhiteList.clear();
            }
            targetWhiteList.addAll(whiteList);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x009a, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
        r9 = r1.mAccessibilityManager.getEnabledAccessibilityServiceList(-1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00a4, code lost:
        if (r9 == null) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00aa, code lost:
        if (r9.isEmpty() != false) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00ac, code lost:
        r0 = r9.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00b4, code lost:
        if (r0.hasNext() == false) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00b6, code lost:
        r8 = r0.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00bc, code lost:
        if (r8 == null) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00c6, code lost:
        if (android.text.TextUtils.isEmpty(r8.getId()) != false) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00c8, code lost:
        r11 = android.content.ComponentName.unflattenFromString(r8.getId());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00d0, code lost:
        if (r11 == null) goto L_0x00d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00d2, code lost:
        r13 = r11.getPackageName();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00d7, code lost:
        r13 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00d8, code lost:
        if (r13 == null) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00de, code lost:
        if (r2.containsKey(r13) != false) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00e0, code lost:
        r14 = r8.getResolveInfo().serviceInfo.applicationInfo;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00ec, code lost:
        if (r14.isSystemApp() != false) goto L_0x00f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00f2, code lost:
        if (r14.isUpdatedSystemApp() == false) goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00f5, code lost:
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00f7, code lost:
        r15 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00f8, code lost:
        if (r15 == false) goto L_0x0102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00fa, code lost:
        r2.put(r13, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0106, code lost:
        if ((r8.feedbackType & 7) == 0) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0108, code lost:
        r2.put(r11.getPackageName(), true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x010f, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0111, code lost:
        android.util.Log.d("ProcessManager", "update DY:" + java.util.Arrays.toString(r2.keySet().toArray()));
        r4 = sLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0136, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:?, code lost:
        sDynamicWhiteList = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0139, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x013a, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.HashMap<java.lang.String, java.lang.Boolean> updateDynamicWhiteList(android.content.Context r17, int r18) {
        /*
            r16 = this;
            r1 = r16
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r2 = r0
            java.lang.String r0 = com.android.server.am.ProcessUtils.getActiveWallpaperPackage(r17)
            r3 = r0
            r4 = 1
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r4)
            if (r0 == 0) goto L_0x0017
            r2.put(r3, r5)
        L_0x0017:
            java.lang.String r0 = com.android.server.am.ProcessUtils.getDefaultInputMethod(r17)
            r6 = r0
            if (r0 == 0) goto L_0x0021
            r2.put(r6, r5)
        L_0x0021:
            java.lang.String r0 = com.android.server.am.ProcessUtils.getActiveTtsEngine(r17)
            r7 = r0
            if (r0 == 0) goto L_0x002b
            r2.put(r7, r5)
        L_0x002b:
            boolean r0 = com.android.server.am.ProcessUtils.isPhoneWorking()
            if (r0 == 0) goto L_0x0036
            java.lang.String r0 = "com.android.incallui"
            r2.put(r0, r5)
        L_0x0036:
            boolean r0 = com.android.server.am.ProcessUtils.isVoipWorking()
            if (r0 == 0) goto L_0x0041
            java.lang.String r0 = "com.miui.voip"
            r2.put(r0, r5)
        L_0x0041:
            com.android.server.am.ActivityManagerService r8 = r1.mActivityManagerService
            monitor-enter(r8)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x013e }
            java.util.List<java.lang.String> r0 = sFgServiceCheckList     // Catch:{ all -> 0x013e }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x013e }
        L_0x004d:
            boolean r9 = r0.hasNext()     // Catch:{ all -> 0x013e }
            r10 = 0
            if (r9 == 0) goto L_0x0097
            java.lang.Object r9 = r0.next()     // Catch:{ all -> 0x013e }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ all -> 0x013e }
            com.android.server.am.ProcessManagerService r11 = r1.mProcessManagerService     // Catch:{ all -> 0x013e }
            r12 = r18
            java.util.List r11 = r11.getProcessRecordList(r9, r12)     // Catch:{ all -> 0x0146 }
            java.util.Iterator r13 = r11.iterator()     // Catch:{ all -> 0x0146 }
        L_0x0066:
            boolean r14 = r13.hasNext()     // Catch:{ all -> 0x0146 }
            if (r14 == 0) goto L_0x0096
            java.lang.Object r14 = r13.next()     // Catch:{ all -> 0x0146 }
            com.android.server.am.ProcessRecord r14 = (com.android.server.am.ProcessRecord) r14     // Catch:{ all -> 0x0146 }
            if (r14 == 0) goto L_0x0095
            boolean r15 = r14.hasForegroundServices()     // Catch:{ all -> 0x0146 }
            if (r15 == 0) goto L_0x0095
            r2.put(r9, r5)     // Catch:{ all -> 0x0146 }
            java.util.Map<java.lang.String, java.lang.String> r13 = sBoundFgServiceProtectMap     // Catch:{ all -> 0x0146 }
            boolean r13 = r13.containsKey(r9)     // Catch:{ all -> 0x0146 }
            if (r13 == 0) goto L_0x0096
            java.util.Map<java.lang.String, java.lang.String> r13 = sBoundFgServiceProtectMap     // Catch:{ all -> 0x0146 }
            java.lang.Object r13 = r13.get(r9)     // Catch:{ all -> 0x0146 }
            java.lang.String r13 = (java.lang.String) r13     // Catch:{ all -> 0x0146 }
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r10)     // Catch:{ all -> 0x0146 }
            r2.put(r13, r10)     // Catch:{ all -> 0x0146 }
            goto L_0x0096
        L_0x0095:
            goto L_0x0066
        L_0x0096:
            goto L_0x004d
        L_0x0097:
            r12 = r18
            monitor-exit(r8)     // Catch:{ all -> 0x0146 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            android.view.accessibility.AccessibilityManager r0 = r1.mAccessibilityManager
            r8 = -1
            java.util.List r9 = r0.getEnabledAccessibilityServiceList(r8)
            if (r9 == 0) goto L_0x0111
            boolean r0 = r9.isEmpty()
            if (r0 != 0) goto L_0x0111
            java.util.Iterator r0 = r9.iterator()
        L_0x00b0:
            boolean r8 = r0.hasNext()
            if (r8 == 0) goto L_0x0111
            java.lang.Object r8 = r0.next()
            android.accessibilityservice.AccessibilityServiceInfo r8 = (android.accessibilityservice.AccessibilityServiceInfo) r8
            if (r8 == 0) goto L_0x010f
            java.lang.String r11 = r8.getId()
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 != 0) goto L_0x010f
            java.lang.String r11 = r8.getId()
            android.content.ComponentName r11 = android.content.ComponentName.unflattenFromString(r11)
            if (r11 == 0) goto L_0x00d7
            java.lang.String r13 = r11.getPackageName()
            goto L_0x00d8
        L_0x00d7:
            r13 = 0
        L_0x00d8:
            if (r13 == 0) goto L_0x010f
            boolean r14 = r2.containsKey(r13)
            if (r14 != 0) goto L_0x010f
            android.content.pm.ResolveInfo r14 = r8.getResolveInfo()
            android.content.pm.ServiceInfo r14 = r14.serviceInfo
            android.content.pm.ApplicationInfo r14 = r14.applicationInfo
            boolean r15 = r14.isSystemApp()
            if (r15 != 0) goto L_0x00f7
            boolean r15 = r14.isUpdatedSystemApp()
            if (r15 == 0) goto L_0x00f5
            goto L_0x00f7
        L_0x00f5:
            r15 = r10
            goto L_0x00f8
        L_0x00f7:
            r15 = r4
        L_0x00f8:
            if (r15 == 0) goto L_0x0102
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r10)
            r2.put(r13, r4)
            goto L_0x010f
        L_0x0102:
            int r4 = r8.feedbackType
            r4 = r4 & 7
            if (r4 == 0) goto L_0x010f
            java.lang.String r4 = r11.getPackageName()
            r2.put(r4, r5)
        L_0x010f:
            r4 = 1
            goto L_0x00b0
        L_0x0111:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "update DY:"
            r0.append(r4)
            java.util.Set r4 = r2.keySet()
            java.lang.Object[] r4 = r4.toArray()
            java.lang.String r4 = java.util.Arrays.toString(r4)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "ProcessManager"
            android.util.Log.d(r4, r0)
            java.lang.Object r4 = sLock
            monitor-enter(r4)
            sDynamicWhiteList = r2     // Catch:{ all -> 0x013b }
            monitor-exit(r4)     // Catch:{ all -> 0x013b }
            return r2
        L_0x013b:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x013b }
            throw r0
        L_0x013e:
            r0 = move-exception
            r12 = r18
        L_0x0141:
            monitor-exit(r8)     // Catch:{ all -> 0x0146 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x0146:
            r0 = move-exception
            goto L_0x0141
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessPolicy.updateDynamicWhiteList(android.content.Context, int):java.util.HashMap");
    }

    public void resetWhiteList(Context context, int userId) {
        updateDynamicWhiteList(context, userId);
        synchronized (sLock) {
            sUserDefinedWhiteList.clear();
        }
    }

    public void updateApplicationLockedState(final Context context, int userId, String packageName, boolean isLocked) {
        updateApplicationLockedState(packageName, userId, isLocked);
        BackgroundThread.getHandler().post(new Runnable() {
            public void run() {
                ProcessPolicy.this.saveLockedAppIntoSettings(context);
            }
        });
        ProcessRecord targetApp = this.mProcessManagerService.getProcessRecord(packageName, userId);
        if (targetApp != null) {
            promoteLockedApp(targetApp);
        }
    }

    private void updateApplicationLockedState(String packageName, int userId, boolean isLocked) {
        synchronized (sLock) {
            Set<String> lockedApplication = sLockedApplicationList.get(Integer.valueOf(userId));
            if (lockedApplication == null) {
                lockedApplication = new HashSet<>();
                sLockedApplicationList.put(Integer.valueOf(userId), lockedApplication);
            }
            if (isLocked) {
                lockedApplication.add(packageName);
            } else {
                lockedApplication.remove(packageName);
                removeDefaultLockedAppIfExists(packageName);
            }
        }
    }

    private void removeDefaultLockedAppIfExists(String packageName) {
        Set<String> defaultLockedApps = sLockedApplicationList.get(-100);
        if (defaultLockedApps != null && defaultLockedApps.contains(packageName)) {
            defaultLockedApps.remove(packageName);
        }
    }

    /* access modifiers changed from: protected */
    public void promoteLockedApp(ProcessRecord app) {
        if (app.isPersistent()) {
            Log.d("ProcessManager", "do not promote " + app.processName);
            return;
        }
        boolean isLocked = isLockedApplication(app.processName, app.userId);
        int targetMaxAdj = isLocked ? 400 : 1001;
        int targetMaxProcState = isLocked ? 14 : 21;
        updateMaxAdjLocked(app, targetMaxAdj, isLocked);
        updateMaxProcStateLocked(app, targetMaxProcState, isLocked);
        Slog.d("ProcessManager", "promoteLockedApp:" + isLocked + ", set " + app.processName + " maxAdj to " + ProcessList.makeOomAdjString(app.maxAdj, false) + ", maxProcState to + " + ProcessList.makeProcStateString(app.maxProcState));
    }

    /* access modifiers changed from: private */
    public void saveLockedAppIntoSettings(Context context) {
        synchronized (sLock) {
            JSONArray userSpaceArray = new JSONArray();
            try {
                for (Integer userId : sLockedApplicationList.keySet()) {
                    JSONObject userSpaceObject = new JSONObject();
                    userSpaceObject.put(JSON_KEY_USER_ID, userId);
                    userSpaceObject.put(JSON_KEY_PACKAGE_NAMES, new JSONArray(sLockedApplicationList.get(userId)));
                    userSpaceArray.put(userSpaceObject);
                }
                Log.d("ProcessManager", "saveLockedAppIntoSettings:" + userSpaceArray.toString());
            } catch (Exception e) {
                Log.d("ProcessManager", "saveLockedAppIntoSettings failed: " + e.toString());
                e.printStackTrace();
            }
            MiuiSettings.System.putString(context.getContentResolver(), "locked_apps", userSpaceArray.toString());
        }
    }

    private void loadLockedAppFromSettings(Context context) {
        synchronized (sLock) {
            String jsonFormatText = MiuiSettings.System.getString(context.getContentResolver(), "locked_apps");
            if (!TextUtils.isEmpty(jsonFormatText)) {
                try {
                    JSONArray userSpaceArray = new JSONArray(jsonFormatText);
                    for (int spaceIndex = 0; spaceIndex < userSpaceArray.length(); spaceIndex++) {
                        JSONObject userSpaceObject = (JSONObject) userSpaceArray.get(spaceIndex);
                        int userId = userSpaceObject.getInt(JSON_KEY_USER_ID);
                        JSONArray packageNameArray = userSpaceObject.getJSONArray(JSON_KEY_PACKAGE_NAMES);
                        Set<String> packageNameSet = new HashSet<>();
                        for (int pkgIndex = 0; pkgIndex < packageNameArray.length(); pkgIndex++) {
                            packageNameSet.add(packageNameArray.getString(pkgIndex));
                        }
                        sLockedApplicationList.put(Integer.valueOf(userId), packageNameSet);
                        Log.d("ProcessManager", "loadLockedAppFromSettings userId:" + userId + "-pkgNames:" + Arrays.toString(packageNameSet.toArray()));
                    }
                } catch (Exception e) {
                    Log.d("ProcessManager", "loadLockedApp failed: " + e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isLockedApplication(String packageName, int userId) {
        synchronized (sLock) {
            if (!isLockedApplicationForUserId(packageName, userId)) {
                if (!isLockedApplicationForUserId(packageName, -100)) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean isLockedApplicationForUserId(String packageName, int userId) {
        Set<String> lockedApplication;
        if (!(packageName == null || (lockedApplication = sLockedApplicationList.get(Integer.valueOf(userId))) == null)) {
            for (String item : lockedApplication) {
                if (item.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getLockedApplication(int userId) {
        List<String> lockedApps = new ArrayList<>();
        Set<String> userApps = sLockedApplicationList.get(Integer.valueOf(userId));
        if (userApps != null && userApps.size() > 0) {
            lockedApps.addAll(userApps);
        }
        lockedApps.addAll(sLockedApplicationList.get(-100));
        return lockedApps;
    }

    public void updateCloudData(ProcessCloudData cloudData) {
        updateCloudWhiteList(cloudData);
        updateAppProtectMap(cloudData);
        updateFgProtectMap(cloudData);
        updateFastBootList(cloudData);
        updateCameraMemThresholdMap(cloudData);
        updateSecretlyProtectAppList(cloudData);
    }

    private void updateCloudWhiteList(ProcessCloudData cloudData) {
        List<String> cloudWhiteList = cloudData.getCloudWhiteList();
        synchronized (sLock) {
            if (cloudWhiteList != null) {
                try {
                    if (!cloudWhiteList.isEmpty() && !cloudWhiteList.equals(sCloudWhiteList)) {
                        sCloudWhiteList.clear();
                        sCloudWhiteList.addAll(cloudWhiteList);
                        Log.d("ProcessManager", "update CL:" + Arrays.toString(sCloudWhiteList.toArray()));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if ((cloudWhiteList == null || cloudWhiteList.isEmpty()) && !sCloudWhiteList.isEmpty()) {
                sCloudWhiteList.clear();
                Log.d("ProcessManager", "update CL:" + Arrays.toString(sCloudWhiteList.toArray()));
            }
        }
    }

    private void updateAppProtectMap(ProcessCloudData cloudData) {
        Map<String, Integer> appProtectMap = cloudData.getAppProtectMap();
        synchronized (sLock) {
            if (appProtectMap != null) {
                try {
                    if (!appProtectMap.isEmpty() && !appProtectMap.equals(sAppProtectMap)) {
                        sAppProtectMap.clear();
                        sAppProtectMap.putAll(appProtectMap);
                        Log.d("ProcessManager", "update AP:" + Arrays.toString(sAppProtectMap.keySet().toArray()));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if ((appProtectMap == null || appProtectMap.isEmpty()) && !sAppProtectMap.isEmpty()) {
                sAppProtectMap.clear();
                Log.d("ProcessManager", "update AP:" + Arrays.toString(sAppProtectMap.keySet().toArray()));
            }
        }
    }

    private void updateFgProtectMap(ProcessCloudData cloudData) {
        Map<String, Integer> fgProtectMap = cloudData.getFgProtectMap();
        synchronized (sLock) {
            if (fgProtectMap != null) {
                try {
                    if (!fgProtectMap.isEmpty() && !fgProtectMap.equals(sFgServiceProtectMap)) {
                        sFgServiceProtectMap.clear();
                        sFgServiceProtectMap.putAll(fgProtectMap);
                        Log.d("ProcessManager", "update FG:" + Arrays.toString(sFgServiceProtectMap.keySet().toArray()));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if ((fgProtectMap == null || fgProtectMap.isEmpty()) && !sFgServiceProtectMap.isEmpty()) {
                sFgServiceProtectMap.clear();
                Log.d("ProcessManager", "update FG:" + Arrays.toString(sFgServiceProtectMap.keySet().toArray()));
            }
        }
    }

    private void updateFastBootList(ProcessCloudData cloudData) {
        Set<String> oldFastBootSet;
        List<String> fastBootList = cloudData.getFastBootList();
        synchronized (sLock) {
            oldFastBootSet = sFastBootAppMap.keySet();
        }
        if (fastBootList != null && !fastBootList.isEmpty() && !oldFastBootSet.equals(new HashSet(fastBootList))) {
            synchronized (sLock) {
                Map<String, Long> temp = new HashMap<>();
                for (String packageName : fastBootList) {
                    long thresholdKb = sFastBootAppMap.get(packageName).longValue();
                    temp.put(packageName, Long.valueOf(thresholdKb > 0 ? thresholdKb : DEFAULT_FASTBOOT_THRESHOLDKB));
                }
                sFastBootAppMap.clear();
                sFastBootAppMap.putAll(temp);
            }
            Log.d("ProcessManager", "update FA:" + Arrays.toString(sFastBootAppMap.keySet().toArray()));
        } else if ((fastBootList == null || fastBootList.isEmpty()) && !sFastBootAppMap.isEmpty()) {
            synchronized (sLock) {
                sFastBootAppMap.clear();
            }
            Log.d("ProcessManager", "update FA:" + Arrays.toString(sFastBootAppMap.keySet().toArray()));
        }
    }

    private void updateCameraMemThresholdMap(ProcessCloudData cloudData) {
        Map<String, Integer> thresholdMap = cloudData.getCameraMemThresholdMap();
        synchronized (sLock) {
            if (thresholdMap != null) {
                try {
                    if (!thresholdMap.isEmpty() && !thresholdMap.equals(sCameraMemThresholdMap)) {
                        sCameraMemThresholdMap.clear();
                        sCameraMemThresholdMap.putAll(thresholdMap);
                        Log.d("ProcessManager", "update CM:" + Arrays.toString(sCameraMemThresholdMap.keySet().toArray()));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if ((thresholdMap == null || thresholdMap.isEmpty()) && !sCameraMemThresholdMap.isEmpty()) {
                sCameraMemThresholdMap.clear();
                Log.d("ProcessManager", "update CM:" + Arrays.toString(sCameraMemThresholdMap.keySet().toArray()));
            }
        }
    }

    private void updateSecretlyProtectAppList(ProcessCloudData cloudData) {
        List<String> secretlyProtectAppList = cloudData.getSecretlyProtectAppList();
        synchronized (sLock) {
            if (secretlyProtectAppList != null) {
                try {
                    if (!secretlyProtectAppList.isEmpty() && !secretlyProtectAppList.equals(sSecretlyProtectAppList)) {
                        sSecretlyProtectAppList.clear();
                        sSecretlyProtectAppList.addAll(secretlyProtectAppList);
                        Log.d("ProcessManager", "update SPAL:" + Arrays.toString(sSecretlyProtectAppList.toArray()));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if ((secretlyProtectAppList == null || secretlyProtectAppList.isEmpty()) && !sSecretlyProtectAppList.isEmpty()) {
                sSecretlyProtectAppList.clear();
                Log.d("ProcessManager", "update SPAL:" + Arrays.toString(sSecretlyProtectAppList.toArray()));
            }
        }
    }

    public boolean isProcessImportant(ProcessRecord app) {
        Pair<Boolean, Boolean> isInDynamicPair = isInDynamicList(app);
        return ((Boolean) isInDynamicPair.first).booleanValue() && (!((Boolean) isInDynamicPair.second).booleanValue() || app.hasForegroundServices() || app.curAdj <= PERCEPTIBLE_APP_ADJ);
    }

    public Pair<Boolean, Boolean> isInDynamicList(ProcessRecord app) {
        if (app != null) {
            synchronized (sLock) {
                String packageName = app.info.packageName;
                if (sDynamicWhiteList.keySet().contains(packageName)) {
                    Pair<Boolean, Boolean> pair = new Pair<>(Boolean.TRUE, sDynamicWhiteList.get(packageName));
                    return pair;
                }
            }
        }
        return new Pair<>(Boolean.FALSE, Boolean.FALSE);
    }

    public boolean isFastBootEnable(String packageName, int uid, boolean checkPss) {
        return uid > 0 && isInFastBootList(packageName, uid, checkPss) && this.mProcessManagerService.isAllowAutoStart(packageName, uid);
    }

    public boolean isInFastBootList(String packageName, int uid, boolean checkPss) {
        long pss;
        boolean res;
        if (checkPss) {
            pss = ProcessUtils.getPackageLastPss(this.mActivityManagerService, this.mProcessManagerService, packageName, UserHandle.getUserId(uid));
        } else {
            pss = 0;
        }
        synchronized (sLock) {
            res = sFastBootAppMap.keySet().contains(packageName);
            if (res && checkPss && pss > sFastBootAppMap.get(packageName).longValue()) {
                Log.w("ProcessManager", "ignore fast boot, caused pkg:" + packageName + " is too large, with pss:" + pss);
                res = false;
            }
        }
        return res;
    }

    public boolean isInAppProtectList(String packageName) {
        boolean contains;
        synchronized (sLock) {
            contains = sAppProtectMap.keySet().contains(packageName);
        }
        return contains;
    }

    public boolean isInSecretlyProtectList(String processName) {
        boolean contains;
        synchronized (sLock) {
            contains = sSecretlyProtectAppList.contains(processName);
        }
        return contains;
    }

    public long getCameraMemThreshold() {
        if (Build.VERSION.SDK_INT >= 28) {
            return (long) (((double) (Process.getTotalMemory() / 1024)) * CAMERA_BOOST_THRESHOLD_PERCENT);
        }
        if (sCameraMemThresholdMap.containsKey(DEVICE)) {
            return (long) sCameraMemThresholdMap.get(DEVICE).intValue();
        }
        return -1;
    }

    public boolean isCameraBoostEnable() {
        return Build.VERSION.SDK_INT >= 28 || sCameraMemThresholdMap.keySet().contains(DEVICE);
    }

    public boolean protectCurrentProcess(ProcessRecord app, boolean isProtected) {
        Integer priorityLevel;
        if (app == null || app.info == null) {
            return false;
        }
        synchronized (sLock) {
            priorityLevel = sAppProtectMap.get(app.info.packageName);
        }
        if (priorityLevel == null) {
            return false;
        }
        updateProcessPriority(app, priorityLevel.intValue(), isProtected);
        Slog.d("ProcessManager", "protectCurrentProcess:" + isProtected + ", set " + app.processName + " maxAdj to " + ProcessList.makeOomAdjString(app.maxAdj, false) + ", maxProcState to + " + ProcessList.makeProcStateString(app.maxProcState));
        return true;
    }

    public void updateProcessForegroundLocked(ProcessRecord app) {
        Integer priorityLevel;
        if (app != null && app.info != null) {
            synchronized (sLock) {
                priorityLevel = sFgServiceProtectMap.get(app.info.packageName);
            }
            if (priorityLevel != null) {
                updateProcessPriority(app, priorityLevel.intValue(), app.hasForegroundServices());
                Slog.d("ProcessManager", "updateProcessForegroundLocked:" + app.hasForegroundServices() + ", set " + app.processName + " maxAdj to " + ProcessList.makeOomAdjString(app.maxAdj, false) + ", maxProcState to + " + ProcessList.makeProcStateString(app.maxProcState));
            }
        }
    }

    private void updateProcessPriority(ProcessRecord app, int priorityLevel, boolean protect) {
        Pair<Integer, Integer> priorityPair = sProcessPriorityMap.get(priorityLevel);
        if (priorityPair != null) {
            updateMaxAdjLocked(app, protect ? ((Integer) priorityPair.first).intValue() : 1001, protect);
            updateMaxProcStateLocked(app, protect ? ((Integer) priorityPair.second).intValue() : 21, protect);
        }
    }

    private void updateMaxAdjLocked(ProcessRecord app, int targetMaxAdj, boolean protect) {
        if (!app.isPersistent()) {
            if (protect && app.maxAdj > targetMaxAdj) {
                app.maxAdj = targetMaxAdj;
            } else if (!protect && app.maxAdj < targetMaxAdj) {
                app.maxAdj = targetMaxAdj;
            }
        }
    }

    private void updateMaxProcStateLocked(ProcessRecord app, int targetMaxProcState, boolean protect) {
        if (protect && app.maxProcState > targetMaxProcState) {
            app.maxProcState = targetMaxProcState;
        } else if (!protect && app.maxProcState < targetMaxProcState) {
            app.maxProcState = targetMaxProcState;
        }
    }

    public List<ActiveUidRecord> getActiveUidRecordList(int flag) {
        List<ActiveUidRecord> records;
        synchronized (sLock) {
            records = new ArrayList<>();
            for (int i = sActiveUidList.size() - 1; i >= 0; i--) {
                ActiveUidRecord r = sActiveUidList.valueAt(i);
                if ((r.flag & flag) != 0) {
                    records.add(r);
                }
            }
        }
        return records;
    }

    public List<Integer> getActiveUidList(int flag) {
        List<Integer> records;
        synchronized (sLock) {
            records = new ArrayList<>();
            for (int i = sActiveUidList.size() - 1; i >= 0; i--) {
                ActiveUidRecord r = sActiveUidList.valueAt(i);
                if ((r.flag & flag) != 0) {
                    records.add(Integer.valueOf(r.uid));
                }
            }
        }
        return records;
    }

    public void noteAudioOnLocked(int uid) {
        if (UserHandle.isApp(uid)) {
            synchronized (sLock) {
                ActiveUidRecord temp = sTempInactiveAudioList.get(uid);
                if (temp != null) {
                    this.mActiveUpdateHandler.removeMessages(1, temp);
                    sTempInactiveAudioList.remove(uid);
                    Slog.d("ProcessManager", "remove temp audio active uid : " + uid);
                } else {
                    ActiveUidRecord r = sActiveUidList.get(uid);
                    if (r == null) {
                        r = new ActiveUidRecord(uid);
                    }
                    r.flag = 1 | r.flag;
                    sActiveUidList.put(uid, r);
                    Slog.d("ProcessManager", "add audio active uid : " + uid);
                }
            }
        }
    }

    public void noteAudioOffLocked(int uid) {
        if (UserHandle.isApp(uid)) {
            synchronized (sLock) {
                ActiveUidRecord r = sActiveUidList.get(uid);
                if (r != null) {
                    sTempInactiveAudioList.put(uid, r);
                    this.mActiveUpdateHandler.sendMessageDelayed(this.mActiveUpdateHandler.obtainMessage(1, r), UPDATE_AUDIO_OFF_DELAY);
                    Slog.d("ProcessManager", "add temp remove audio inactive uid : " + uid);
                }
            }
        }
    }

    public void noteResetAudioLocked() {
        synchronized (sLock) {
            List<ActiveUidRecord> removed = new ArrayList<>();
            int N = sActiveUidList.size();
            for (int i = 0; i < N; i++) {
                ActiveUidRecord r = sActiveUidList.valueAt(i);
                r.flag &= -2;
                if (r.flag == 0) {
                    removed.add(r);
                }
            }
            for (ActiveUidRecord r2 : removed) {
                sActiveUidList.remove(r2.uid);
            }
            Slog.d("ProcessManager", " noteResetAudioLocked removed ActiveUids : " + Arrays.toString(removed.toArray()));
        }
    }

    public void noteStartGpsLocked(int uid) {
        if (UserHandle.isApp(uid)) {
            synchronized (sLock) {
                ActiveUidRecord temp = sTempInactiveGPSList.get(uid);
                if (temp != null) {
                    this.mActiveUpdateHandler.removeMessages(2, temp);
                    sTempInactiveGPSList.remove(uid);
                    Slog.d("ProcessManager", "remove temp gps active uid : " + uid);
                } else {
                    ActiveUidRecord r = sActiveUidList.get(uid);
                    if (r == null) {
                        r = new ActiveUidRecord(uid);
                    }
                    r.flag = 2 | r.flag;
                    sActiveUidList.put(uid, r);
                    Slog.d("ProcessManager", "add gps active uid : " + uid);
                }
            }
        }
    }

    public void noteStopGpsLocked(int uid) {
        if (UserHandle.isApp(uid)) {
            synchronized (sLock) {
                ActiveUidRecord r = sActiveUidList.get(uid);
                if (r != null) {
                    sTempInactiveGPSList.put(uid, r);
                    this.mActiveUpdateHandler.sendMessageDelayed(this.mActiveUpdateHandler.obtainMessage(2, r), 1000);
                    Slog.d("ProcessManager", "add temp remove gps inactive uid : " + uid);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.println("Process Policy:");
        if (sDynamicWhiteList.size() > 0) {
            pw.println("DY:");
            for (String pkg : sDynamicWhiteList.keySet()) {
                pw.print(prefix);
                pw.println(pkg + " : " + sDynamicWhiteList.get(pkg));
            }
        }
        if (sCloudWhiteList.size() > 0) {
            pw.println("CL:");
            for (int i = 0; i < sCloudWhiteList.size(); i++) {
                pw.print(prefix);
                pw.println(sCloudWhiteList.get(i));
            }
        }
        if (sUserDefinedWhiteList.size() > 0) {
            pw.println("US:");
            for (int i2 = 0; i2 < sUserDefinedWhiteList.size(); i2++) {
                pw.print(prefix);
                pw.println(sUserDefinedWhiteList.get(i2));
            }
        }
        if (sLockedApplicationList.size() > 0) {
            pw.println("LO:");
            for (Integer userId : sLockedApplicationList.keySet()) {
                pw.print(prefix);
                pw.println("userId=" + userId);
                for (String app : sLockedApplicationList.get(userId)) {
                    pw.print(prefix);
                    pw.println(app);
                }
            }
        }
        if (sFastBootAppMap.size() > 0) {
            pw.println("FA:");
            for (String protectedPackage : sFastBootAppMap.keySet()) {
                pw.print(prefix);
                pw.println(protectedPackage);
            }
        }
        if (EnterpriseSettings.ENTERPRISE_ACTIVATED) {
            pw.println("EP Activated: true");
            if (sEnterpriseAppList.size() > 0) {
                for (int i3 = 0; i3 < sEnterpriseAppList.size(); i3++) {
                    pw.print(prefix);
                    pw.println(sEnterpriseAppList.get(i3));
                }
            }
        }
        if (sSecretlyProtectAppList.size() > 0) {
            pw.println("SPAL:");
            for (int i4 = 0; i4 < sSecretlyProtectAppList.size(); i4++) {
                pw.print(prefix);
                pw.println(sSecretlyProtectAppList.get(i4));
            }
        }
        if (sActiveUidList.size() > 0) {
            pw.println("ACU:");
            for (int i5 = 0; i5 < sActiveUidList.size(); i5++) {
                pw.print(prefix);
                pw.println(sActiveUidList.valueAt(i5));
            }
        }
    }
}
