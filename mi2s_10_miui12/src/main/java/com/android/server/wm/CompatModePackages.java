package com.android.server.wm;

import android.app.AppGlobals;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import com.android.server.job.controllers.JobStatus;
import java.util.HashMap;

public final class CompatModePackages {
    private static final int COMPAT_FLAG_DONT_ASK = 1;
    private static final int COMPAT_FLAG_ENABLED = 2;
    private static final int MSG_WRITE = 300;
    private static final String TAG = "ActivityTaskManager";
    private static final String TAG_CONFIGURATION = "ActivityTaskManager";
    private final AtomicFile mFile;
    private final CompatHandler mHandler;
    private final HashMap<String, Integer> mPackages = new HashMap<>();
    private final ActivityTaskManagerService mService;

    private final class CompatHandler extends Handler {
        public CompatHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 300) {
                CompatModePackages.this.saveCompatModes();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0062 A[SYNTHETIC, Splitter:B:13:0x0062] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x005a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public CompatModePackages(com.android.server.wm.ActivityTaskManagerService r17, java.io.File r18, android.os.Handler r19) {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r2 = "Error reading compat-packages"
            java.lang.String r3 = "ActivityTaskManager"
            r16.<init>()
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r1.mPackages = r0
            r4 = r17
            r1.mService = r4
            android.util.AtomicFile r0 = new android.util.AtomicFile
            java.io.File r5 = new java.io.File
            java.lang.String r6 = "packages-compat.xml"
            r7 = r18
            r5.<init>(r7, r6)
            java.lang.String r6 = "compat-mode"
            r0.<init>(r5, r6)
            r1.mFile = r0
            com.android.server.wm.CompatModePackages$CompatHandler r0 = new com.android.server.wm.CompatModePackages$CompatHandler
            android.os.Looper r5 = r19.getLooper()
            r0.<init>(r5)
            r1.mHandler = r0
            r5 = 0
            android.util.AtomicFile r0 = r1.mFile     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            java.io.FileInputStream r0 = r0.openRead()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r5 = r0
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r6 = r0
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            java.lang.String r0 = r0.name()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r6.setInput(r5, r0)     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            int r0 = r6.getEventType()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r8 = r0
        L_0x004c:
            r9 = 1
            r10 = 2
            if (r8 == r10) goto L_0x0058
            if (r8 == r9) goto L_0x0058
            int r0 = r6.next()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r8 = r0
            goto L_0x004c
        L_0x0058:
            if (r8 != r9) goto L_0x0062
            if (r5 == 0) goto L_0x0061
            r5.close()     // Catch:{ IOException -> 0x0060 }
            goto L_0x0061
        L_0x0060:
            r0 = move-exception
        L_0x0061:
            return
        L_0x0062:
            java.lang.String r0 = r6.getName()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            java.lang.String r11 = "compat-packages"
            boolean r11 = r11.equals(r0)     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            if (r11 == 0) goto L_0x00b3
            int r11 = r6.next()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r8 = r11
        L_0x0073:
            if (r8 != r10) goto L_0x00ac
            java.lang.String r11 = r6.getName()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            int r0 = r6.getDepth()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            if (r0 != r10) goto L_0x00ab
            java.lang.String r0 = "pkg"
            boolean r0 = r0.equals(r11)     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            if (r0 == 0) goto L_0x00ab
            java.lang.String r0 = "name"
            r12 = 0
            java.lang.String r0 = r6.getAttributeValue(r12, r0)     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r13 = r0
            if (r13 == 0) goto L_0x00ab
            java.lang.String r0 = "mode"
            java.lang.String r0 = r6.getAttributeValue(r12, r0)     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r12 = r0
            r14 = 0
            if (r12 == 0) goto L_0x00a2
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ NumberFormatException -> 0x00a1 }
            r14 = r0
            goto L_0x00a2
        L_0x00a1:
            r0 = move-exception
        L_0x00a2:
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = r1.mPackages     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r0.put(r13, r15)     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
        L_0x00ab:
            r0 = r11
        L_0x00ac:
            int r11 = r6.next()     // Catch:{ XmlPullParserException -> 0x00ca, IOException -> 0x00be }
            r8 = r11
            if (r8 != r9) goto L_0x0073
        L_0x00b3:
            if (r5 == 0) goto L_0x00d5
            r5.close()     // Catch:{ IOException -> 0x00b9 }
        L_0x00b8:
            goto L_0x00d5
        L_0x00b9:
            r0 = move-exception
            goto L_0x00b8
        L_0x00bb:
            r0 = move-exception
            r2 = r0
            goto L_0x00d6
        L_0x00be:
            r0 = move-exception
            if (r5 == 0) goto L_0x00c4
            android.util.Slog.w(r3, r2, r0)     // Catch:{ all -> 0x00bb }
        L_0x00c4:
            if (r5 == 0) goto L_0x00d5
            r5.close()     // Catch:{ IOException -> 0x00b9 }
            goto L_0x00b8
        L_0x00ca:
            r0 = move-exception
            android.util.Slog.w(r3, r2, r0)     // Catch:{ all -> 0x00bb }
            if (r5 == 0) goto L_0x00d5
            r5.close()     // Catch:{ IOException -> 0x00b9 }
            goto L_0x00b8
        L_0x00d5:
            return
        L_0x00d6:
            if (r5 == 0) goto L_0x00dd
            r5.close()     // Catch:{ IOException -> 0x00dc }
            goto L_0x00dd
        L_0x00dc:
            r0 = move-exception
        L_0x00dd:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.CompatModePackages.<init>(com.android.server.wm.ActivityTaskManagerService, java.io.File, android.os.Handler):void");
    }

    public HashMap<String, Integer> getPackages() {
        return this.mPackages;
    }

    private int getPackageFlags(String packageName) {
        Integer flags = this.mPackages.get(packageName);
        if (flags != null) {
            return flags.intValue();
        }
        return 0;
    }

    public void handlePackageDataClearedLocked(String packageName) {
        removePackage(packageName);
    }

    public void handlePackageUninstalledLocked(String packageName) {
        removePackage(packageName);
    }

    private void removePackage(String packageName) {
        if (this.mPackages.containsKey(packageName)) {
            this.mPackages.remove(packageName);
            scheduleWrite();
        }
    }

    public void handlePackageAddedLocked(String packageName, boolean updated) {
        ApplicationInfo ai = null;
        boolean mayCompat = false;
        try {
            ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, 0);
        } catch (RemoteException e) {
        }
        if (ai != null) {
            CompatibilityInfo ci = compatibilityInfoForPackageLocked(ai);
            if (!ci.alwaysSupportsScreen() && !ci.neverSupportsScreen()) {
                mayCompat = true;
            }
            if (updated && !mayCompat && this.mPackages.containsKey(packageName)) {
                this.mPackages.remove(packageName);
                scheduleWrite();
            }
        }
    }

    private void scheduleWrite() {
        this.mHandler.removeMessages(300);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(300), JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
    }

    public CompatibilityInfo compatibilityInfoForPackageLocked(ApplicationInfo ai) {
        Configuration globalConfig = this.mService.getGlobalConfiguration();
        return new CompatibilityInfo(ai, globalConfig.screenLayout, globalConfig.smallestScreenWidthDp, (getPackageFlags(ai.packageName) & 2) != 0);
    }

    public int computeCompatModeLocked(ApplicationInfo ai) {
        boolean enabled = (getPackageFlags(ai.packageName) & 2) != 0;
        Configuration globalConfig = this.mService.getGlobalConfiguration();
        CompatibilityInfo info = new CompatibilityInfo(ai, globalConfig.screenLayout, globalConfig.smallestScreenWidthDp, enabled);
        if (info.alwaysSupportsScreen()) {
            return -2;
        }
        if (info.neverSupportsScreen()) {
            return -1;
        }
        if (enabled) {
            return 1;
        }
        return 0;
    }

    public boolean getPackageAskCompatModeLocked(String packageName) {
        return (getPackageFlags(packageName) & 1) == 0;
    }

    public void setPackageAskCompatModeLocked(String packageName, boolean ask) {
        setPackageFlagLocked(packageName, 1, ask);
    }

    private void setPackageFlagLocked(String packageName, int flag, boolean set) {
        int curFlags = getPackageFlags(packageName);
        int newFlags = set ? (~flag) & curFlags : curFlags | flag;
        if (curFlags != newFlags) {
            if (newFlags != 0) {
                this.mPackages.put(packageName, Integer.valueOf(newFlags));
            } else {
                this.mPackages.remove(packageName);
            }
            scheduleWrite();
        }
    }

    public int getPackageScreenCompatModeLocked(String packageName) {
        ApplicationInfo ai = null;
        try {
            ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, 0);
        } catch (RemoteException e) {
        }
        if (ai == null) {
            return -3;
        }
        return computeCompatModeLocked(ai);
    }

    public void setPackageScreenCompatModeLocked(String packageName, int mode) {
        ApplicationInfo ai = null;
        try {
            ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, 0);
        } catch (RemoteException e) {
        }
        if (ai == null) {
            Slog.w("ActivityTaskManager", "setPackageScreenCompatMode failed: unknown package " + packageName);
            return;
        }
        setPackageScreenCompatModeLocked(ai, mode);
    }

    /* access modifiers changed from: package-private */
    public void setPackageScreenCompatModeLocked(ApplicationInfo ai, int mode) {
        boolean enable;
        int newFlags;
        String packageName = ai.packageName;
        int curFlags = getPackageFlags(packageName);
        if (mode == 0) {
            enable = false;
        } else if (mode == 1) {
            enable = true;
        } else if (mode != 2) {
            Slog.w("ActivityTaskManager", "Unknown screen compat mode req #" + mode + "; ignoring");
            return;
        } else {
            enable = (curFlags & 2) == 0;
        }
        int newFlags2 = curFlags;
        if (enable) {
            newFlags = 2 | newFlags2;
        } else {
            newFlags = newFlags2 & -3;
        }
        CompatibilityInfo ci = compatibilityInfoForPackageLocked(ai);
        if (ci.alwaysSupportsScreen()) {
            Slog.w("ActivityTaskManager", "Ignoring compat mode change of " + packageName + "; compatibility never needed");
            newFlags = 0;
        }
        if (ci.neverSupportsScreen()) {
            Slog.w("ActivityTaskManager", "Ignoring compat mode change of " + packageName + "; compatibility always needed");
            newFlags = 0;
        }
        if (newFlags != curFlags) {
            if (newFlags != 0) {
                this.mPackages.put(packageName, Integer.valueOf(newFlags));
            } else {
                this.mPackages.remove(packageName);
            }
            CompatibilityInfo ci2 = compatibilityInfoForPackageLocked(ai);
            scheduleWrite();
            ActivityStack stack = this.mService.getTopDisplayFocusedStack();
            ActivityRecord starting = stack.restartPackage(packageName);
            SparseArray<WindowProcessController> pidMap = this.mService.mProcessMap.getPidMap();
            for (int i = pidMap.size() - 1; i >= 0; i--) {
                WindowProcessController app = pidMap.valueAt(i);
                if (app.mPkgList.contains(packageName)) {
                    try {
                        if (app.hasThread()) {
                            app.getThread().updatePackageCompatibilityInfo(packageName, ci2);
                        }
                    } catch (Exception e) {
                    }
                }
            }
            if (starting != null) {
                starting.ensureActivityConfiguration(0, false);
                stack.ensureActivitiesVisibleLocked(starting, 0, false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveCompatModes() {
        /*
            r17 = this;
            r1 = r17
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService
            com.android.server.wm.WindowManagerGlobalLock r2 = r0.mGlobalLock
            monitor-enter(r2)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00e4 }
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x00e4 }
            java.util.HashMap<java.lang.String, java.lang.Integer> r3 = r1.mPackages     // Catch:{ all -> 0x00e4 }
            r0.<init>(r3)     // Catch:{ all -> 0x00e4 }
            r3 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x00e4 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            r2 = 0
            android.util.AtomicFile r0 = r1.mFile     // Catch:{ IOException -> 0x00d2 }
            java.io.FileOutputStream r0 = r0.startWrite()     // Catch:{ IOException -> 0x00d2 }
            r2 = r0
            com.android.internal.util.FastXmlSerializer r0 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x00d2 }
            r0.<init>()     // Catch:{ IOException -> 0x00d2 }
            r4 = r0
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x00d2 }
            java.lang.String r0 = r0.name()     // Catch:{ IOException -> 0x00d2 }
            r4.setOutput(r2, r0)     // Catch:{ IOException -> 0x00d2 }
            r0 = 1
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r0)     // Catch:{ IOException -> 0x00d2 }
            r6 = 0
            r4.startDocument(r6, r5)     // Catch:{ IOException -> 0x00d2 }
            java.lang.String r5 = "http://xmlpull.org/v1/doc/features.html#indent-output"
            r4.setFeature(r5, r0)     // Catch:{ IOException -> 0x00d2 }
            java.lang.String r0 = "compat-packages"
            r4.startTag(r6, r0)     // Catch:{ IOException -> 0x00d2 }
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ IOException -> 0x00d2 }
            r5 = r0
            com.android.server.wm.ActivityTaskManagerService r0 = r1.mService     // Catch:{ IOException -> 0x00d2 }
            android.content.res.Configuration r0 = r0.getGlobalConfiguration()     // Catch:{ IOException -> 0x00d2 }
            r7 = r0
            int r0 = r7.screenLayout     // Catch:{ IOException -> 0x00d2 }
            r8 = r0
            int r0 = r7.smallestScreenWidthDp     // Catch:{ IOException -> 0x00d2 }
            r9 = r0
            java.util.Set r0 = r3.entrySet()     // Catch:{ IOException -> 0x00d2 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ IOException -> 0x00d2 }
            r10 = r0
        L_0x005b:
            boolean r0 = r10.hasNext()     // Catch:{ IOException -> 0x00d2 }
            if (r0 == 0) goto L_0x00bf
            java.lang.Object r0 = r10.next()     // Catch:{ IOException -> 0x00d2 }
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ IOException -> 0x00d2 }
            r11 = r0
            java.lang.Object r0 = r11.getKey()     // Catch:{ IOException -> 0x00d2 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ IOException -> 0x00d2 }
            r12 = r0
            java.lang.Object r0 = r11.getValue()     // Catch:{ IOException -> 0x00d2 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ IOException -> 0x00d2 }
            int r0 = r0.intValue()     // Catch:{ IOException -> 0x00d2 }
            r13 = r0
            if (r13 != 0) goto L_0x007d
            goto L_0x005b
        L_0x007d:
            r14 = 0
            r15 = 0
            android.content.pm.ApplicationInfo r0 = r5.getApplicationInfo(r12, r15, r15)     // Catch:{ RemoteException -> 0x0089, IOException -> 0x0085 }
            r14 = r0
            goto L_0x008a
        L_0x0085:
            r0 = move-exception
            r16 = r3
            goto L_0x00d5
        L_0x0089:
            r0 = move-exception
        L_0x008a:
            if (r14 != 0) goto L_0x008d
            goto L_0x005b
        L_0x008d:
            android.content.res.CompatibilityInfo r0 = new android.content.res.CompatibilityInfo     // Catch:{ IOException -> 0x00d2 }
            r0.<init>(r14, r8, r9, r15)     // Catch:{ IOException -> 0x00d2 }
            boolean r15 = r0.alwaysSupportsScreen()     // Catch:{ IOException -> 0x00d2 }
            if (r15 == 0) goto L_0x0099
            goto L_0x005b
        L_0x0099:
            boolean r15 = r0.neverSupportsScreen()     // Catch:{ IOException -> 0x00d2 }
            if (r15 == 0) goto L_0x00a0
            goto L_0x005b
        L_0x00a0:
            java.lang.String r15 = "pkg"
            r4.startTag(r6, r15)     // Catch:{ IOException -> 0x00d2 }
            java.lang.String r15 = "name"
            r4.attribute(r6, r15, r12)     // Catch:{ IOException -> 0x00d2 }
            java.lang.String r15 = "mode"
            java.lang.String r6 = java.lang.Integer.toString(r13)     // Catch:{ IOException -> 0x00d2 }
            r16 = r3
            r3 = 0
            r4.attribute(r3, r15, r6)     // Catch:{ IOException -> 0x00d0 }
            java.lang.String r6 = "pkg"
            r4.endTag(r3, r6)     // Catch:{ IOException -> 0x00d0 }
            r3 = r16
            r6 = 0
            goto L_0x005b
        L_0x00bf:
            r16 = r3
            java.lang.String r0 = "compat-packages"
            r3 = 0
            r4.endTag(r3, r0)     // Catch:{ IOException -> 0x00d0 }
            r4.endDocument()     // Catch:{ IOException -> 0x00d0 }
            android.util.AtomicFile r0 = r1.mFile     // Catch:{ IOException -> 0x00d0 }
            r0.finishWrite(r2)     // Catch:{ IOException -> 0x00d0 }
            goto L_0x00e3
        L_0x00d0:
            r0 = move-exception
            goto L_0x00d5
        L_0x00d2:
            r0 = move-exception
            r16 = r3
        L_0x00d5:
            java.lang.String r3 = "ActivityTaskManager"
            java.lang.String r4 = "Error writing compat packages"
            android.util.Slog.w(r3, r4, r0)
            if (r2 == 0) goto L_0x00e3
            android.util.AtomicFile r3 = r1.mFile
            r3.failWrite(r2)
        L_0x00e3:
            return
        L_0x00e4:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00e4 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.CompatModePackages.saveCompatModes():void");
    }
}
