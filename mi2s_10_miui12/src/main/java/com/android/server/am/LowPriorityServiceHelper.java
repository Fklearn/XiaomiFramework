package com.android.server.am;

import android.os.Binder;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.app.MiuiServicePriority;
import com.android.server.am.ActiveServices;
import com.android.server.wm.WindowProcessUtils;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import miui.util.ReflectionUtils;

/* compiled from: ActiveServicesInjector */
class LowPriorityServiceHelper {
    static final boolean DEBUG_DELAYED_STARTS = false;
    static final long DEFAULT_NO_PROC_DELAY_TIME = 300;
    static final long LOW_PRIORITY_DELAY = 150;
    static final long MAX_DELAY_TIME = 1000;
    static final long MAX_TIME_OUT = 1500;
    static final long MIN_DELAY_TIME = 0;
    static final int MSG_BG_RESTART_LOW_PRIORITY = 102;
    static final int MSG_BG_START_LOW_PRIORITY = 101;
    private static final String PACKAGE_NAME_ALL = "*";
    private static final String SEPARATOR = "/";
    static final long SHORT_DELAY = 5;
    static final LowPriorityServiceHelper mInstance = new LowPriorityServiceHelper();
    private final int MAX_SIZE = 30;
    private boolean canOpen = true;
    private boolean closeCheck = true;
    private Field fLowPriorityDelay;
    private Field fLowPriorityDelayRestart;
    private long lastLowPriorityEnforcedTime;
    private final HashMap<String, MiuiServicePriority> mBlacklist = new HashMap<>();
    private final ArrayList<LowPriorityServiceInfo> mLowPriorityList = new ArrayList<>();
    private final HashMap<String, MiuiServicePriority> mWhitelist = new HashMap<>();
    long noProcDelayTime = DEFAULT_NO_PROC_DELAY_TIME;
    private boolean sendNoDelayEnforcedMsg = false;
    private boolean startEnforcedLowPriorityService = false;
    private HashSet<String> unCheckPackage = new HashSet<>();

    private LowPriorityServiceHelper() {
        try {
            this.fLowPriorityDelay = ReflectionUtils.findField(ServiceRecord.class, "lowPriorityDelay");
            this.fLowPriorityDelayRestart = ReflectionUtils.findField(ServiceRecord.class, "lowPriorityDelayRestart");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.IS_CTS_BUILD || this.fLowPriorityDelay == null || this.fLowPriorityDelayRestart == null) {
            this.canOpen = false;
        }
    }

    public void setServicePriority(List<MiuiServicePriority> servicePrioritys) {
        HashMap<String, MiuiServicePriority> uselist;
        if (this.canOpen) {
            for (MiuiServicePriority servicePriority : servicePrioritys) {
                if (servicePriority.inBlacklist) {
                    uselist = this.mBlacklist;
                } else {
                    uselist = this.mWhitelist;
                }
                String key = servicePriority.packageName + "/" + servicePriority.serviceName;
                MiuiServicePriority sp = uselist.get(key);
                if (sp != null) {
                    sp.packageName = servicePriority.packageName;
                    sp.serviceName = servicePriority.serviceName;
                    sp.priority = servicePriority.priority;
                    sp.checkPriority = servicePriority.checkPriority;
                    if (servicePriority.delayTime < 0 || servicePriority.delayTime > 1000) {
                        sp.delayTime = LOW_PRIORITY_DELAY;
                    } else {
                        sp.delayTime = servicePriority.delayTime;
                    }
                } else {
                    uselist.put(key, servicePriority);
                }
            }
            this.unCheckPackage.add("com.cttl.testService");
            this.closeCheck = false;
        }
    }

    public void setNoProcDelayTime(long noProcDelayTime2) {
        if (this.canOpen) {
            if (noProcDelayTime2 < 0 || noProcDelayTime2 > 1000) {
                this.noProcDelayTime = DEFAULT_NO_PROC_DELAY_TIME;
            } else {
                this.noProcDelayTime = noProcDelayTime2;
            }
        }
    }

    public void removeServicePriority(MiuiServicePriority servicePriority, boolean inBlacklist) {
        HashMap<String, MiuiServicePriority> uselist;
        if (!this.closeCheck) {
            if (inBlacklist) {
                uselist = this.mBlacklist;
            } else {
                uselist = this.mWhitelist;
            }
            if (servicePriority.serviceName == null) {
                Iterator<Map.Entry<String, MiuiServicePriority>> it = uselist.entrySet().iterator();
                while (it.hasNext()) {
                    if (uselist.get(it.next().getKey()).packageName.equals(servicePriority.packageName)) {
                        it.remove();
                    }
                }
            }
            uselist.remove(servicePriority.packageName + "/" + servicePriority.serviceName);
        }
    }

    public void closeCheckPriority() {
        if (this.canOpen) {
            this.mBlacklist.clear();
            this.mWhitelist.clear();
            this.unCheckPackage.clear();
            this.closeCheck = true;
        }
    }

    private void countDelayTime(ActiveServices.ServiceMap smap, MiuiServicePriority sp, LowPriorityServiceInfo info) {
        long delay;
        if (sp == null) {
            delay = LOW_PRIORITY_DELAY;
        } else {
            delay = sp.delayTime == 0 ? LOW_PRIORITY_DELAY : sp.delayTime;
        }
        info.delay = delay;
    }

    public boolean callerIsTopApp(ServiceRecord r, ProcessRecord callerApp) {
        if (!this.canOpen) {
            return true;
        }
        ActivityManagerService ams = ExtraActivityManagerService.getActivityManagerService();
        if (callerApp != null) {
            ProcessRecord app = ProcessUtils.getProcessRecordByWPCtl(WindowProcessUtils.getTopRunningProcessController(ams.mActivityTaskManager), ams);
            if (app == null || app.uid != callerApp.uid || callerApp.info == null || !callerApp.info.packageName.equals(app.info.packageName)) {
                return false;
            }
            return true;
        } else if (Binder.getCallingPid() == ActivityManagerService.MY_PID) {
            return true;
        } else {
            if (!this.unCheckPackage.contains(r.packageName)) {
                return false;
            }
            Slog.d("ActiveServicesInjector", "UnCheckPackage : " + r.packageName + " service : " + r.serviceInfo.name);
            return true;
        }
    }

    @Deprecated
    public boolean isLowPriorityDelayStart(ActiveServices.ServiceMap smap, ServiceRecord r, int callerUid, boolean callerFg) {
        return false;
    }

    public boolean isLowPriorityDelayStart(ActiveServices.ServiceMap smap, ServiceRecord r, ProcessRecord callerApp, boolean callerFg, boolean restart) {
        Message msg;
        Message msg2;
        ActiveServices.ServiceMap serviceMap = smap;
        ServiceRecord serviceRecord = r;
        boolean z = restart;
        if (this.closeCheck || UserHandle.getAppId(serviceRecord.appInfo.uid) < 10000) {
            return false;
        }
        MiuiServicePriority sp = null;
        boolean addToLowPriorityList = false;
        if (MiuiSysUserServiceHelper.isAllLimit()) {
            addToLowPriorityList = true;
        } else if (callerFg) {
            HashMap<String, MiuiServicePriority> uselist = this.mBlacklist;
            sp = uselist.get("*/" + serviceRecord.serviceInfo.name);
            if (sp != null) {
                addToLowPriorityList = true;
            } else {
                sp = uselist.get(serviceRecord.packageName + "/" + serviceRecord.serviceInfo.name);
                if (sp != null) {
                    addToLowPriorityList = true;
                }
            }
        } else {
            HashMap<String, MiuiServicePriority> uselist2 = this.mWhitelist;
            sp = uselist2.get(serviceRecord.packageName + "/" + serviceRecord.serviceInfo.name);
            if (sp == null) {
                sp = uselist2.get("*/" + serviceRecord.serviceInfo.name);
                if (sp == null) {
                    addToLowPriorityList = true;
                }
            }
        }
        if (!addToLowPriorityList) {
            return false;
        }
        LowPriorityServiceInfo info = new LowPriorityServiceInfo(serviceRecord, z);
        countDelayTime(serviceMap, sp, info);
        if (z) {
            info.restartPerformed = false;
        }
        this.mLowPriorityList.add(info);
        if (this.mLowPriorityList.size() >= 30) {
            this.lastLowPriorityEnforcedTime = SystemClock.uptimeMillis();
            if (!this.sendNoDelayEnforcedMsg) {
                this.sendNoDelayEnforcedMsg = true;
                if (this.mLowPriorityList.get(0).isRestart) {
                    msg2 = serviceMap.obtainMessage(102);
                } else {
                    msg2 = serviceMap.obtainMessage(101);
                }
                serviceMap.sendMessage(msg2);
            }
        }
        if (this.mLowPriorityList.size() == 1) {
            long now = SystemClock.uptimeMillis();
            if (info.isRestart) {
                msg = serviceMap.obtainMessage(102);
            } else {
                msg = serviceMap.obtainMessage(101);
            }
            if (serviceRecord.app == null || serviceRecord.app.thread == null) {
                this.lastLowPriorityEnforcedTime = (this.lastLowPriorityEnforcedTime - LOW_PRIORITY_DELAY) + DEFAULT_NO_PROC_DELAY_TIME;
            }
            long j = this.lastLowPriorityEnforcedTime;
            if (now >= j) {
                serviceMap.sendMessage(msg);
            } else {
                serviceMap.sendMessageDelayed(msg, j - now);
            }
        }
        return true;
    }

    public boolean forceRemoveServiceLocked(ServiceRecord r) {
        boolean remove = false;
        if (this.mLowPriorityList.size() > 0) {
            int i = 0;
            while (i < this.mLowPriorityList.size()) {
                if (this.mLowPriorityList.get(i).mR.equals(r)) {
                    this.mLowPriorityList.remove(i);
                    remove = true;
                } else {
                    i++;
                }
            }
        }
        return remove;
    }

    public boolean removeService(ServiceRecord r) {
        if (!this.canOpen || r.pendingStarts.size() > 0) {
            return false;
        }
        int i = 0;
        while (i < this.mLowPriorityList.size()) {
            LowPriorityServiceInfo rInfo = this.mLowPriorityList.get(i);
            if (!rInfo.mR.equals(r)) {
                i++;
            } else if (rInfo.isRestart && !rInfo.restartPerformed) {
                return true;
            } else {
                this.mLowPriorityList.remove(i);
                return true;
            }
        }
        return false;
    }

    private void setLowPriorityDelay(ServiceRecord r, boolean lowPriorityDelay) {
        try {
            this.fLowPriorityDelay.setBoolean(r, lowPriorityDelay);
        } catch (Exception e) {
            e.printStackTrace();
            closeCheckPriority();
        }
    }

    private void setLowPriorityDelayRestart(ServiceRecord r, boolean lowPriorityDelayRestart) {
        try {
            this.fLowPriorityDelayRestart.setBoolean(r, lowPriorityDelayRestart);
        } catch (Exception e) {
            e.printStackTrace();
            closeCheckPriority();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        if (r1.mLowPriorityList.size() <= 0) goto L_0x0051;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0046, code lost:
        r12 = r1.mLowPriorityList.get(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0051, code lost:
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0052, code lost:
        if (r12 == null) goto L_0x0081;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005c, code lost:
        if (r1.mLowPriorityList.size() > 30) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005e, code lost:
        r1.sendNoDelayEnforcedMsg = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0064, code lost:
        if (r12.mR.app == null) goto L_0x0075;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006c, code lost:
        if (r12.mR.app.thread != null) goto L_0x006f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006f, code lost:
        r1.lastLowPriorityEnforcedTime = r12.delay + r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0075, code lost:
        r1.lastLowPriorityEnforcedTime = r1.noProcDelayTime + r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x007b, code lost:
        r1.lastLowPriorityEnforcedTime = SHORT_DELAY + r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0081, code lost:
        r1.lastLowPriorityEnforcedTime = LOW_PRIORITY_DELAY + r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x008a, code lost:
        if (r11.isRestart == false) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008c, code lost:
        r11.restartPerformed = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        android.util.Slog.i("ActiveServicesInjector", "RESTART Low priority start of: " + r11.mR);
        setLowPriorityDelayRestart(r11.mR, false);
        r11.mR.ams.mHandler.post(r11.mR.restarter);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00b7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b8, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        android.util.Slog.i("ActiveServicesInjector", "Low priority start of: " + r11.mR);
        setLowPriorityDelay(r11.mR, false);
        r4 = r11.mR.pendingStarts.get(0).intent;
        r5 = r11.mR;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ea, code lost:
        if (r11.mR.app == null) goto L_0x00f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00f2, code lost:
        if (r11.mR.app.thread != null) goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00f5, code lost:
        r7 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00f7, code lost:
        r7 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00f8, code lost:
        r16.startServiceInnerLocked(r17, r4, r5, false, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0100, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0101, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0132  */
    /* JADX WARNING: Removed duplicated region for block: B:67:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void rescheduleDelayedList(long r14, com.android.server.am.ActiveServices r16, com.android.server.am.ActiveServices.ServiceMap r17) {
        /*
            r13 = this;
            r1 = r13
            r8 = r17
            boolean r0 = r1.canOpen
            if (r0 != 0) goto L_0x0008
            return
        L_0x0008:
            boolean r0 = r1.startEnforcedLowPriorityService
            r9 = 102(0x66, float:1.43E-43)
            if (r0 != 0) goto L_0x0120
            r0 = 1
            r1.startEnforcedLowPriorityService = r0
        L_0x0011:
            long r2 = r1.lastLowPriorityEnforcedTime
            int r2 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            r10 = 0
            if (r2 < 0) goto L_0x011e
            java.util.ArrayList<com.android.server.am.LowPriorityServiceInfo> r2 = r1.mLowPriorityList
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x011e
            java.util.ArrayList<com.android.server.am.LowPriorityServiceInfo> r2 = r1.mLowPriorityList
            java.lang.Object r2 = r2.remove(r10)
            r11 = r2
            com.android.server.am.LowPriorityServiceInfo r11 = (com.android.server.am.LowPriorityServiceInfo) r11
            r2 = 0
            boolean r3 = r11.isRestart
            if (r3 != 0) goto L_0x003e
            com.android.server.am.ServiceRecord r3 = r11.mR
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r3 = r3.pendingStarts
            int r3 = r3.size()
            if (r3 != 0) goto L_0x003e
            com.android.server.am.ServiceRecord r3 = r11.mR
            r13.setLowPriorityDelay(r3, r10)
            goto L_0x0011
        L_0x003e:
            java.util.ArrayList<com.android.server.am.LowPriorityServiceInfo> r3 = r1.mLowPriorityList
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x0051
            java.util.ArrayList<com.android.server.am.LowPriorityServiceInfo> r3 = r1.mLowPriorityList
            java.lang.Object r3 = r3.get(r10)
            r2 = r3
            com.android.server.am.LowPriorityServiceInfo r2 = (com.android.server.am.LowPriorityServiceInfo) r2
            r12 = r2
            goto L_0x0052
        L_0x0051:
            r12 = r2
        L_0x0052:
            if (r12 == 0) goto L_0x0081
            java.util.ArrayList<com.android.server.am.LowPriorityServiceInfo> r2 = r1.mLowPriorityList
            int r2 = r2.size()
            r3 = 30
            if (r2 > r3) goto L_0x007b
            r1.sendNoDelayEnforcedMsg = r10
            com.android.server.am.ServiceRecord r2 = r12.mR
            com.android.server.am.ProcessRecord r2 = r2.app
            if (r2 == 0) goto L_0x0075
            com.android.server.am.ServiceRecord r2 = r12.mR
            com.android.server.am.ProcessRecord r2 = r2.app
            android.app.IApplicationThread r2 = r2.thread
            if (r2 != 0) goto L_0x006f
            goto L_0x0075
        L_0x006f:
            long r2 = r12.delay
            long r2 = r2 + r14
            r1.lastLowPriorityEnforcedTime = r2
            goto L_0x0086
        L_0x0075:
            long r2 = r1.noProcDelayTime
            long r2 = r2 + r14
            r1.lastLowPriorityEnforcedTime = r2
            goto L_0x0086
        L_0x007b:
            r2 = 5
            long r2 = r2 + r14
            r1.lastLowPriorityEnforcedTime = r2
            goto L_0x0086
        L_0x0081:
            r2 = 150(0x96, double:7.4E-322)
            long r2 = r2 + r14
            r1.lastLowPriorityEnforcedTime = r2
        L_0x0086:
            boolean r2 = r11.isRestart
            java.lang.String r3 = "ActiveServicesInjector"
            if (r2 == 0) goto L_0x00bc
            r11.restartPerformed = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b7 }
            r0.<init>()     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r2 = "RESTART Low priority start of: "
            r0.append(r2)     // Catch:{ Exception -> 0x00b7 }
            com.android.server.am.ServiceRecord r2 = r11.mR     // Catch:{ Exception -> 0x00b7 }
            r0.append(r2)     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x00b7 }
            android.util.Slog.i(r3, r0)     // Catch:{ Exception -> 0x00b7 }
            com.android.server.am.ServiceRecord r0 = r11.mR     // Catch:{ Exception -> 0x00b7 }
            r13.setLowPriorityDelayRestart(r0, r10)     // Catch:{ Exception -> 0x00b7 }
            com.android.server.am.ServiceRecord r0 = r11.mR     // Catch:{ Exception -> 0x00b7 }
            com.android.server.am.ActivityManagerService r0 = r0.ams     // Catch:{ Exception -> 0x00b7 }
            com.android.server.am.ActivityManagerService$MainHandler r0 = r0.mHandler     // Catch:{ Exception -> 0x00b7 }
            com.android.server.am.ServiceRecord r2 = r11.mR     // Catch:{ Exception -> 0x00b7 }
            java.lang.Runnable r2 = r2.restarter     // Catch:{ Exception -> 0x00b7 }
            r0.post(r2)     // Catch:{ Exception -> 0x00b7 }
            goto L_0x00bb
        L_0x00b7:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00bb:
            goto L_0x0104
        L_0x00bc:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0100 }
            r2.<init>()     // Catch:{ Exception -> 0x0100 }
            java.lang.String r4 = "Low priority start of: "
            r2.append(r4)     // Catch:{ Exception -> 0x0100 }
            com.android.server.am.ServiceRecord r4 = r11.mR     // Catch:{ Exception -> 0x0100 }
            r2.append(r4)     // Catch:{ Exception -> 0x0100 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0100 }
            android.util.Slog.i(r3, r2)     // Catch:{ Exception -> 0x0100 }
            com.android.server.am.ServiceRecord r2 = r11.mR     // Catch:{ Exception -> 0x0100 }
            r13.setLowPriorityDelay(r2, r10)     // Catch:{ Exception -> 0x0100 }
            com.android.server.am.ServiceRecord r2 = r11.mR     // Catch:{ Exception -> 0x0100 }
            java.util.ArrayList<com.android.server.am.ServiceRecord$StartItem> r2 = r2.pendingStarts     // Catch:{ Exception -> 0x0100 }
            java.lang.Object r2 = r2.get(r10)     // Catch:{ Exception -> 0x0100 }
            com.android.server.am.ServiceRecord$StartItem r2 = (com.android.server.am.ServiceRecord.StartItem) r2     // Catch:{ Exception -> 0x0100 }
            android.content.Intent r4 = r2.intent     // Catch:{ Exception -> 0x0100 }
            com.android.server.am.ServiceRecord r5 = r11.mR     // Catch:{ Exception -> 0x0100 }
            r6 = 0
            com.android.server.am.ServiceRecord r2 = r11.mR     // Catch:{ Exception -> 0x0100 }
            com.android.server.am.ProcessRecord r2 = r2.app     // Catch:{ Exception -> 0x0100 }
            if (r2 == 0) goto L_0x00f7
            com.android.server.am.ServiceRecord r2 = r11.mR     // Catch:{ Exception -> 0x0100 }
            com.android.server.am.ProcessRecord r2 = r2.app     // Catch:{ Exception -> 0x0100 }
            android.app.IApplicationThread r2 = r2.thread     // Catch:{ Exception -> 0x0100 }
            if (r2 != 0) goto L_0x00f5
            goto L_0x00f7
        L_0x00f5:
            r7 = r10
            goto L_0x00f8
        L_0x00f7:
            r7 = r0
        L_0x00f8:
            r2 = r16
            r3 = r17
            r2.startServiceInnerLocked(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0100 }
            goto L_0x0104
        L_0x0100:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0104:
            android.os.Message r0 = r8.obtainMessage(r9)
            if (r12 == 0) goto L_0x0119
            boolean r2 = r12.isRestart
            if (r2 == 0) goto L_0x0113
            android.os.Message r0 = r8.obtainMessage(r9)
            goto L_0x0119
        L_0x0113:
            r2 = 101(0x65, float:1.42E-43)
            android.os.Message r0 = r8.obtainMessage(r2)
        L_0x0119:
            long r2 = r1.lastLowPriorityEnforcedTime
            r8.sendMessageAtTime(r0, r2)
        L_0x011e:
            r1.startEnforcedLowPriorityService = r10
        L_0x0120:
            long r2 = r1.lastLowPriorityEnforcedTime
            long r2 = r14 - r2
            r4 = 1500(0x5dc, double:7.41E-321)
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0139
            java.util.ArrayList<com.android.server.am.LowPriorityServiceInfo> r0 = r1.mLowPriorityList
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0139
            android.os.Message r0 = r8.obtainMessage(r9)
            r8.sendMessage(r0)
        L_0x0139:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.LowPriorityServiceHelper.rescheduleDelayedList(long, com.android.server.am.ActiveServices, com.android.server.am.ActiveServices$ServiceMap):void");
    }

    public void dump(PrintWriter pw) {
        pw.println("  MIUI ADD :  LPSH dump start : ");
        pw.println("  LowPriorityList services : ");
        for (int i = 0; i < this.mLowPriorityList.size(); i++) {
            LowPriorityServiceInfo info = this.mLowPriorityList.get(i);
            pw.print("#LPSInfo : ");
            pw.print(info);
            pw.print(" isRestart : ");
            pw.print(info.isRestart);
            pw.print(" delay : ");
            pw.print(info.delay);
            pw.print(" pendingStarts.size : ");
            pw.print(info.mR.pendingStarts.size());
            pw.println("");
            pw.println(info.mR);
            info.mR.dump(pw, "    ");
        }
        pw.println("  Blacklist : ");
        for (String key : this.mBlacklist.keySet()) {
            dumpMiuiServicePriority(pw, this.mBlacklist.get(key));
        }
        pw.println("  Whitelist : ");
        for (String key2 : this.mWhitelist.keySet()) {
            dumpMiuiServicePriority(pw, this.mWhitelist.get(key2));
        }
        pw.println("  LPSH dump END !!!");
    }

    private void dumpMiuiServicePriority(PrintWriter pw, MiuiServicePriority sp) {
        pw.print("#SP : ");
        pw.print(" pkgName=");
        pw.print(sp.packageName);
        pw.print(" sName=");
        pw.print(sp.serviceName);
        pw.print(" prio=");
        pw.print(sp.priority);
        pw.print(" cPrio=");
        pw.print(sp.checkPriority);
        pw.print(" inBlist=");
        pw.print(sp.inBlacklist);
        pw.print(" dTime=");
        pw.print(sp.delayTime);
        pw.println("");
    }
}
