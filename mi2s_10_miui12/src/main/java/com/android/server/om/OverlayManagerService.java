package com.android.server.om;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.IApplicationThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManagerInternal;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import com.android.server.FgThread;
import com.android.server.IoThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.om.OverlayManagerService;
import com.android.server.om.OverlayManagerServiceImpl;
import com.android.server.pm.Installer;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.pm.UserManagerService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParserException;

public final class OverlayManagerService extends SystemService {
    static final boolean DEBUG = false;
    private static final String DEFAULT_OVERLAYS_PROP = "ro.boot.vendor.overlay.theme";
    static final String TAG = "OverlayManager";
    /* access modifiers changed from: private */
    public final OverlayManagerServiceImpl mImpl;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final PackageManagerHelper mPackageManager;
    private final AtomicBoolean mPersistSettingsScheduled = new AtomicBoolean(false);
    private final IBinder mService = new IOverlayManager.Stub() {
        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public Map<String, List<OverlayInfo>> getAllOverlays(int userId) throws RemoteException {
            Map<String, List<OverlayInfo>> overlaysForUser;
            try {
                Trace.traceBegin(67108864, "OMS#getAllOverlays " + userId);
                int userId2 = handleIncomingUser(userId, "getAllOverlays");
                synchronized (OverlayManagerService.this.mLock) {
                    overlaysForUser = OverlayManagerService.this.mImpl.getOverlaysForUser(userId2);
                }
                Trace.traceEnd(67108864);
                return overlaysForUser;
            } catch (Throwable th) {
                Trace.traceEnd(67108864);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public List<OverlayInfo> getOverlayInfosForTarget(String targetPackageName, int userId) throws RemoteException {
            List<OverlayInfo> overlayInfosForTarget;
            try {
                Trace.traceBegin(67108864, "OMS#getOverlayInfosForTarget " + targetPackageName);
                int userId2 = handleIncomingUser(userId, "getOverlayInfosForTarget");
                if (targetPackageName == null) {
                    List<OverlayInfo> emptyList = Collections.emptyList();
                    Trace.traceEnd(67108864);
                    return emptyList;
                }
                synchronized (OverlayManagerService.this.mLock) {
                    overlayInfosForTarget = OverlayManagerService.this.mImpl.getOverlayInfosForTarget(targetPackageName, userId2);
                }
                Trace.traceEnd(67108864);
                return overlayInfosForTarget;
            } catch (Throwable th) {
                Trace.traceEnd(67108864);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public OverlayInfo getOverlayInfo(String packageName, int userId) throws RemoteException {
            OverlayInfo overlayInfo;
            try {
                Trace.traceBegin(67108864, "OMS#getOverlayInfo " + packageName);
                int userId2 = handleIncomingUser(userId, "getOverlayInfo");
                if (packageName == null) {
                    Trace.traceEnd(67108864);
                    return null;
                }
                synchronized (OverlayManagerService.this.mLock) {
                    overlayInfo = OverlayManagerService.this.mImpl.getOverlayInfo(packageName, userId2);
                }
                Trace.traceEnd(67108864);
                return overlayInfo;
            } catch (Throwable th) {
                Trace.traceEnd(67108864);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public boolean setEnabled(String packageName, boolean enable, int userId) throws RemoteException {
            boolean enabled;
            try {
                Trace.traceBegin(67108864, "OMS#setEnabled " + packageName + " " + enable);
                enforceChangeOverlayPackagesPermission("setEnabled");
                int userId2 = handleIncomingUser(userId, "setEnabled");
                if (packageName == null) {
                    return false;
                }
                long ident = Binder.clearCallingIdentity();
                try {
                    synchronized (OverlayManagerService.this.mLock) {
                        enabled = OverlayManagerService.this.mImpl.setEnabled(packageName, enable, userId2);
                    }
                    Binder.restoreCallingIdentity(ident);
                    Trace.traceEnd(67108864);
                    return enabled;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            } finally {
                Trace.traceEnd(67108864);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public boolean setEnabledExclusive(String packageName, boolean enable, int userId) throws RemoteException {
            boolean enabledExclusive;
            try {
                Trace.traceBegin(67108864, "OMS#setEnabledExclusive " + packageName + " " + enable);
                enforceChangeOverlayPackagesPermission("setEnabledExclusive");
                int userId2 = handleIncomingUser(userId, "setEnabledExclusive");
                if (packageName != null) {
                    if (enable) {
                        long ident = Binder.clearCallingIdentity();
                        try {
                            synchronized (OverlayManagerService.this.mLock) {
                                enabledExclusive = OverlayManagerService.this.mImpl.setEnabledExclusive(packageName, false, userId2);
                            }
                            Binder.restoreCallingIdentity(ident);
                            Trace.traceEnd(67108864);
                            return enabledExclusive;
                        } catch (Throwable th) {
                            Binder.restoreCallingIdentity(ident);
                            throw th;
                        }
                    }
                }
                return false;
            } finally {
                Trace.traceEnd(67108864);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public boolean setEnabledExclusiveInCategory(String packageName, int userId) throws RemoteException {
            boolean enabledExclusive;
            try {
                Trace.traceBegin(67108864, "OMS#setEnabledExclusiveInCategory " + packageName);
                enforceChangeOverlayPackagesPermission("setEnabledExclusiveInCategory");
                int userId2 = handleIncomingUser(userId, "setEnabledExclusiveInCategory");
                if (packageName == null) {
                    return false;
                }
                long ident = Binder.clearCallingIdentity();
                try {
                    synchronized (OverlayManagerService.this.mLock) {
                        enabledExclusive = OverlayManagerService.this.mImpl.setEnabledExclusive(packageName, true, userId2);
                    }
                    Binder.restoreCallingIdentity(ident);
                    Trace.traceEnd(67108864);
                    return enabledExclusive;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            } finally {
                Trace.traceEnd(67108864);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public boolean setPriority(String packageName, String parentPackageName, int userId) throws RemoteException {
            boolean priority;
            try {
                Trace.traceBegin(67108864, "OMS#setPriority " + packageName + " " + parentPackageName);
                enforceChangeOverlayPackagesPermission("setPriority");
                int userId2 = handleIncomingUser(userId, "setPriority");
                if (packageName != null) {
                    if (parentPackageName != null) {
                        long ident = Binder.clearCallingIdentity();
                        try {
                            synchronized (OverlayManagerService.this.mLock) {
                                priority = OverlayManagerService.this.mImpl.setPriority(packageName, parentPackageName, userId2);
                            }
                            Binder.restoreCallingIdentity(ident);
                            Trace.traceEnd(67108864);
                            return priority;
                        } catch (Throwable th) {
                            Binder.restoreCallingIdentity(ident);
                            throw th;
                        }
                    }
                }
                return false;
            } finally {
                Trace.traceEnd(67108864);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public boolean setHighestPriority(String packageName, int userId) throws RemoteException {
            boolean highestPriority;
            try {
                Trace.traceBegin(67108864, "OMS#setHighestPriority " + packageName);
                enforceChangeOverlayPackagesPermission("setHighestPriority");
                int userId2 = handleIncomingUser(userId, "setHighestPriority");
                if (packageName == null) {
                    return false;
                }
                long ident = Binder.clearCallingIdentity();
                try {
                    synchronized (OverlayManagerService.this.mLock) {
                        highestPriority = OverlayManagerService.this.mImpl.setHighestPriority(packageName, userId2);
                    }
                    Binder.restoreCallingIdentity(ident);
                    Trace.traceEnd(67108864);
                    return highestPriority;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            } finally {
                Trace.traceEnd(67108864);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public boolean setLowestPriority(String packageName, int userId) throws RemoteException {
            boolean lowestPriority;
            try {
                Trace.traceBegin(67108864, "OMS#setLowestPriority " + packageName);
                enforceChangeOverlayPackagesPermission("setLowestPriority");
                int userId2 = handleIncomingUser(userId, "setLowestPriority");
                if (packageName == null) {
                    return false;
                }
                long ident = Binder.clearCallingIdentity();
                try {
                    synchronized (OverlayManagerService.this.mLock) {
                        lowestPriority = OverlayManagerService.this.mImpl.setLowestPriority(packageName, userId2);
                    }
                    Binder.restoreCallingIdentity(ident);
                    Trace.traceEnd(67108864);
                    return lowestPriority;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            } finally {
                Trace.traceEnd(67108864);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public String[] getDefaultOverlayPackages() throws RemoteException {
            String[] defaultOverlayPackages;
            try {
                Trace.traceBegin(67108864, "OMS#getDefaultOverlayPackages");
                OverlayManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.MODIFY_THEME_OVERLAY", (String) null);
                long ident = Binder.clearCallingIdentity();
                try {
                    synchronized (OverlayManagerService.this.mLock) {
                        defaultOverlayPackages = OverlayManagerService.this.mImpl.getDefaultOverlayPackages();
                    }
                    Binder.restoreCallingIdentity(ident);
                    return defaultOverlayPackages;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            } finally {
                Trace.traceEnd(67108864);
            }
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
            /*
                r8 = this;
                com.android.server.om.OverlayManagerShellCommand r0 = new com.android.server.om.OverlayManagerShellCommand
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.om.OverlayManagerService.AnonymousClass1.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d6, code lost:
            if (r2.equals("packagename") != false) goto L_0x0133;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dump(java.io.FileDescriptor r8, java.io.PrintWriter r9, java.lang.String[] r10) {
            /*
                r7 = this;
                com.android.server.om.DumpState r0 = new com.android.server.om.DumpState
                r0.<init>()
                int r1 = android.os.Binder.getCallingUid()
                int r1 = android.os.UserHandle.getUserId(r1)
                r0.setUserId(r1)
                r1 = 0
            L_0x0011:
                int r2 = r10.length
                r3 = 0
                r4 = 1
                if (r1 >= r2) goto L_0x00b4
                r2 = r10[r1]
                if (r2 == 0) goto L_0x00b4
                int r5 = r2.length()
                if (r5 <= 0) goto L_0x00b4
                char r5 = r2.charAt(r3)
                r6 = 45
                if (r5 == r6) goto L_0x002a
                goto L_0x00b4
            L_0x002a:
                int r1 = r1 + 1
                java.lang.String r3 = "-h"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x0058
                java.lang.String r3 = "dump [-h] [--verbose] [--user USER_ID] [[FIELD] PACKAGE]"
                r9.println(r3)
                java.lang.String r3 = "  Print debugging information about the overlay manager."
                r9.println(r3)
                java.lang.String r3 = "  With optional parameter PACKAGE, limit output to the specified"
                r9.println(r3)
                java.lang.String r3 = "  package. With optional parameter FIELD, limit output to"
                r9.println(r3)
                java.lang.String r3 = "  the value of that SettingsItem field. Field names are"
                r9.println(r3)
                java.lang.String r3 = "  case insensitive and out.println the m prefix can be omitted,"
                r9.println(r3)
                java.lang.String r3 = "  so the following are equivalent: mState, mstate, State, state."
                r9.println(r3)
                return
            L_0x0058:
                java.lang.String r3 = "--user"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x008d
                int r1 = r1 + 1
                int r3 = r10.length
                if (r1 < r3) goto L_0x006b
                java.lang.String r3 = "Error: user missing argument"
                r9.println(r3)
                return
            L_0x006b:
                r3 = r10[r1]     // Catch:{ NumberFormatException -> 0x0075 }
                int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x0075 }
                r0.setUserId(r3)     // Catch:{ NumberFormatException -> 0x0075 }
                goto L_0x00b2
            L_0x0075:
                r3 = move-exception
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "Error: user argument is not a number: "
                r4.append(r5)
                r5 = r10[r1]
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                r9.println(r4)
                return
            L_0x008d:
                java.lang.String r3 = "--verbose"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x0099
                r0.setVerbose(r4)
                goto L_0x00b2
            L_0x0099:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Unknown argument: "
                r3.append(r4)
                r3.append(r2)
                java.lang.String r4 = "; use -h for help"
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                r9.println(r3)
            L_0x00b2:
                goto L_0x0011
            L_0x00b4:
                int r2 = r10.length
                if (r1 >= r2) goto L_0x013d
                r2 = r10[r1]
                int r1 = r1 + 1
                r5 = -1
                int r6 = r2.hashCode()
                switch(r6) {
                    case -1750736508: goto L_0x0127;
                    case -1248283232: goto L_0x011c;
                    case -1165461084: goto L_0x0110;
                    case -836029914: goto L_0x0105;
                    case 50511102: goto L_0x00fa;
                    case 109757585: goto L_0x00ef;
                    case 440941271: goto L_0x00e4;
                    case 697685016: goto L_0x00d9;
                    case 909712337: goto L_0x00cf;
                    case 1693907299: goto L_0x00c5;
                    default: goto L_0x00c3;
                }
            L_0x00c3:
                goto L_0x0132
            L_0x00c5:
                java.lang.String r3 = "basecodepath"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = 4
                goto L_0x0133
            L_0x00cf:
                java.lang.String r4 = "packagename"
                boolean r4 = r2.equals(r4)
                if (r4 == 0) goto L_0x00c3
                goto L_0x0133
            L_0x00d9:
                java.lang.String r3 = "isstatic"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = 7
                goto L_0x0133
            L_0x00e4:
                java.lang.String r3 = "isenabled"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = 6
                goto L_0x0133
            L_0x00ef:
                java.lang.String r3 = "state"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = 5
                goto L_0x0133
            L_0x00fa:
                java.lang.String r3 = "category"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = 9
                goto L_0x0133
            L_0x0105:
                java.lang.String r3 = "userid"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = r4
                goto L_0x0133
            L_0x0110:
                java.lang.String r3 = "priority"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = 8
                goto L_0x0133
            L_0x011c:
                java.lang.String r3 = "targetpackagename"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = 2
                goto L_0x0133
            L_0x0127:
                java.lang.String r3 = "targetoverlayablename"
                boolean r3 = r2.equals(r3)
                if (r3 == 0) goto L_0x00c3
                r3 = 3
                goto L_0x0133
            L_0x0132:
                r3 = r5
            L_0x0133:
                switch(r3) {
                    case 0: goto L_0x013a;
                    case 1: goto L_0x013a;
                    case 2: goto L_0x013a;
                    case 3: goto L_0x013a;
                    case 4: goto L_0x013a;
                    case 5: goto L_0x013a;
                    case 6: goto L_0x013a;
                    case 7: goto L_0x013a;
                    case 8: goto L_0x013a;
                    case 9: goto L_0x013a;
                    default: goto L_0x0136;
                }
            L_0x0136:
                r0.setPackageName(r2)
                goto L_0x013d
            L_0x013a:
                r0.setField(r2)
            L_0x013d:
                java.lang.String r2 = r0.getPackageName()
                if (r2 != 0) goto L_0x014d
                int r2 = r10.length
                if (r1 >= r2) goto L_0x014d
                r2 = r10[r1]
                r0.setPackageName(r2)
                int r1 = r1 + 1
            L_0x014d:
                java.lang.String r2 = "dump"
                r7.enforceDumpPermission(r2)
                com.android.server.om.OverlayManagerService r2 = com.android.server.om.OverlayManagerService.this
                java.lang.Object r2 = r2.mLock
                monitor-enter(r2)
                com.android.server.om.OverlayManagerService r3 = com.android.server.om.OverlayManagerService.this     // Catch:{ all -> 0x0173 }
                com.android.server.om.OverlayManagerServiceImpl r3 = r3.mImpl     // Catch:{ all -> 0x0173 }
                r3.dump(r9, r0)     // Catch:{ all -> 0x0173 }
                java.lang.String r3 = r0.getPackageName()     // Catch:{ all -> 0x0173 }
                if (r3 != 0) goto L_0x0171
                com.android.server.om.OverlayManagerService r3 = com.android.server.om.OverlayManagerService.this     // Catch:{ all -> 0x0173 }
                com.android.server.om.OverlayManagerService$PackageManagerHelper r3 = r3.mPackageManager     // Catch:{ all -> 0x0173 }
                r3.dump(r9, r0)     // Catch:{ all -> 0x0173 }
            L_0x0171:
                monitor-exit(r2)     // Catch:{ all -> 0x0173 }
                return
            L_0x0173:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0173 }
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.om.OverlayManagerService.AnonymousClass1.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
        }

        private int handleIncomingUser(int userId, String message) {
            return ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, message, (String) null);
        }

        private void enforceChangeOverlayPackagesPermission(String message) {
            OverlayManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.CHANGE_OVERLAY_PACKAGES", message);
        }

        private void enforceDumpPermission(String message) {
            OverlayManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.DUMP", message);
        }
    };
    private final OverlayManagerSettings mSettings;
    private final AtomicFile mSettingsFile;
    /* access modifiers changed from: private */
    public final UserManagerService mUserManager;

    /* JADX WARNING: type inference failed for: r0v2, types: [com.android.server.om.OverlayManagerService$1, android.os.IBinder] */
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public OverlayManagerService(Context context, Installer installer) {
        super(context);
        try {
            Trace.traceBegin(67108864, "OMS#OverlayManagerService");
            this.mSettingsFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), "overlays.xml"), "overlays");
            this.mPackageManager = new PackageManagerHelper();
            this.mUserManager = UserManagerService.getInstance();
            try {
                IdmapManager im = new IdmapManager(installer, this.mPackageManager);
                this.mSettings = new OverlayManagerSettings();
                this.mImpl = new OverlayManagerServiceImpl(this.mPackageManager, im, this.mSettings, getDefaultOverlayPackages(), new OverlayChangeListener());
                IntentFilter packageFilter = new IntentFilter();
                packageFilter.addAction("android.intent.action.PACKAGE_ADDED");
                packageFilter.addAction("android.intent.action.PACKAGE_CHANGED");
                packageFilter.addAction("android.intent.action.PACKAGE_REMOVED");
                packageFilter.addDataScheme(Settings.ATTR_PACKAGE);
                getContext().registerReceiverAsUser(new PackageReceiver(), UserHandle.ALL, packageFilter, (String) null, (Handler) null);
                IntentFilter userFilter = new IntentFilter();
                userFilter.addAction("android.intent.action.USER_ADDED");
                userFilter.addAction("android.intent.action.USER_REMOVED");
                getContext().registerReceiverAsUser(new UserReceiver(), UserHandle.ALL, userFilter, (String) null, (Handler) null);
                restoreSettings();
                initIfNeeded();
                onStartUser(0);
                publishBinderService("overlay", this.mService);
                publishLocalService(OverlayManagerService.class, this);
                Trace.traceEnd(67108864);
            } catch (Throwable th) {
                th = th;
                Trace.traceEnd(67108864);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            Installer installer2 = installer;
            Trace.traceEnd(67108864);
            throw th;
        }
    }

    public void onStart() {
    }

    private void initIfNeeded() {
        List<UserInfo> users = ((UserManager) getContext().getSystemService(UserManager.class)).getUsers(true);
        synchronized (this.mLock) {
            int userCount = users.size();
            for (int i = 0; i < userCount; i++) {
                UserInfo userInfo = users.get(i);
                if (!userInfo.supportsSwitchTo() && userInfo.id != 0) {
                    updateOverlayPaths(users.get(i).id, this.mImpl.updateOverlaysForUser(users.get(i).id));
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void onStartUser(int newUserId) {
        try {
            Trace.traceBegin(67108864, "OMS#onStartUser " + newUserId);
            synchronized (this.mLock) {
                updateAssets(newUserId, this.mImpl.updateOverlaysForUser(newUserId));
            }
            schedulePersistSettings();
            Trace.traceEnd(67108864);
        } catch (Throwable th) {
            Trace.traceEnd(67108864);
            throw th;
        }
    }

    private static String[] getDefaultOverlayPackages() {
        String str = SystemProperties.get(DEFAULT_OVERLAYS_PROP);
        if (TextUtils.isEmpty(str)) {
            return EmptyArray.STRING;
        }
        ArraySet<String> defaultPackages = new ArraySet<>();
        for (String packageName : str.split(";")) {
            if (!TextUtils.isEmpty(packageName)) {
                defaultPackages.add(packageName);
            }
        }
        return (String[]) defaultPackages.toArray(new String[defaultPackages.size()]);
    }

    private final class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:19:0x005f, code lost:
            if (r0.equals("android.intent.action.PACKAGE_ADDED") == false) goto L_0x0076;
         */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x0079  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x008c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r13, android.content.Intent r14) {
            /*
                r12 = this;
                java.lang.String r0 = r14.getAction()
                java.lang.String r1 = "OverlayManager"
                if (r0 != 0) goto L_0x000e
                java.lang.String r2 = "Cannot handle package broadcast with null action"
                android.util.Slog.e(r1, r2)
                return
            L_0x000e:
                android.net.Uri r2 = r14.getData()
                if (r2 != 0) goto L_0x001a
                java.lang.String r3 = "Cannot handle package broadcast with null data"
                android.util.Slog.e(r1, r3)
                return
            L_0x001a:
                java.lang.String r1 = r2.getSchemeSpecificPart()
                r3 = 0
                java.lang.String r4 = "android.intent.extra.REPLACING"
                boolean r4 = r14.getBooleanExtra(r4, r3)
                r5 = -10000(0xffffffffffffd8f0, float:NaN)
                java.lang.String r6 = "android.intent.extra.UID"
                int r6 = r14.getIntExtra(r6, r5)
                r7 = 1
                if (r6 != r5) goto L_0x003b
                com.android.server.om.OverlayManagerService r5 = com.android.server.om.OverlayManagerService.this
                com.android.server.pm.UserManagerService r5 = r5.mUserManager
                int[] r5 = r5.getUserIds()
                goto L_0x0043
            L_0x003b:
                int[] r5 = new int[r7]
                int r8 = android.os.UserHandle.getUserId(r6)
                r5[r3] = r8
            L_0x0043:
                r8 = -1
                int r9 = r0.hashCode()
                r10 = 172491798(0xa480416, float:9.630418E-33)
                r11 = 2
                if (r9 == r10) goto L_0x006c
                r10 = 525384130(0x1f50b9c2, float:4.419937E-20)
                if (r9 == r10) goto L_0x0062
                r10 = 1544582882(0x5c1076e2, float:1.62652439E17)
                if (r9 == r10) goto L_0x0059
            L_0x0058:
                goto L_0x0076
            L_0x0059:
                java.lang.String r9 = "android.intent.action.PACKAGE_ADDED"
                boolean r9 = r0.equals(r9)
                if (r9 == 0) goto L_0x0058
                goto L_0x0077
            L_0x0062:
                java.lang.String r3 = "android.intent.action.PACKAGE_REMOVED"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0058
                r3 = r11
                goto L_0x0077
            L_0x006c:
                java.lang.String r3 = "android.intent.action.PACKAGE_CHANGED"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0058
                r3 = r7
                goto L_0x0077
            L_0x0076:
                r3 = r8
            L_0x0077:
                if (r3 == 0) goto L_0x008c
                if (r3 == r7) goto L_0x0088
                if (r3 == r11) goto L_0x007e
                goto L_0x0096
            L_0x007e:
                if (r4 == 0) goto L_0x0084
                r12.onPackageReplacing(r1, r5)
                goto L_0x0096
            L_0x0084:
                r12.onPackageRemoved(r1, r5)
                goto L_0x0096
            L_0x0088:
                r12.onPackageChanged(r1, r5)
                goto L_0x0096
            L_0x008c:
                if (r4 == 0) goto L_0x0092
                r12.onPackageReplaced(r1, r5)
                goto L_0x0096
            L_0x0092:
                r12.onPackageAdded(r1, r5)
            L_0x0096:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.om.OverlayManagerService.PackageReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        private void onPackageAdded(String packageName, int[] userIds) {
            try {
                Trace.traceBegin(67108864, "OMS#onPackageAdded " + packageName);
                int length = userIds.length;
                for (int i = 0; i < length; i++) {
                    int userId = userIds[i];
                    synchronized (OverlayManagerService.this.mLock) {
                        PackageInfo pi = OverlayManagerService.this.mPackageManager.getPackageInfo(packageName, userId, false);
                        if (pi != null && !pi.applicationInfo.isInstantApp()) {
                            OverlayManagerService.this.mPackageManager.cachePackageInfo(packageName, userId, pi);
                            if (pi.isOverlayPackage()) {
                                OverlayManagerService.this.mImpl.onOverlayPackageAdded(packageName, userId);
                            } else {
                                OverlayManagerService.this.mImpl.onTargetPackageAdded(packageName, userId);
                            }
                        }
                    }
                }
                Trace.traceEnd(67108864);
            } catch (Throwable th) {
                Trace.traceEnd(67108864);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        private void onPackageChanged(String packageName, int[] userIds) {
            try {
                Trace.traceBegin(67108864, "OMS#onPackageChanged " + packageName);
                int length = userIds.length;
                for (int i = 0; i < length; i++) {
                    int userId = userIds[i];
                    synchronized (OverlayManagerService.this.mLock) {
                        PackageInfo pi = OverlayManagerService.this.mPackageManager.getPackageInfo(packageName, userId, false);
                        if (pi != null && pi.applicationInfo.isInstantApp()) {
                            OverlayManagerService.this.mPackageManager.cachePackageInfo(packageName, userId, pi);
                            if (pi.isOverlayPackage()) {
                                OverlayManagerService.this.mImpl.onOverlayPackageChanged(packageName, userId);
                            } else {
                                OverlayManagerService.this.mImpl.onTargetPackageChanged(packageName, userId);
                            }
                        }
                    }
                }
                Trace.traceEnd(67108864);
            } catch (Throwable th) {
                Trace.traceEnd(67108864);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        private void onPackageReplacing(String packageName, int[] userIds) {
            try {
                Trace.traceBegin(67108864, "OMS#onPackageReplacing " + packageName);
                for (int userId : userIds) {
                    synchronized (OverlayManagerService.this.mLock) {
                        OverlayManagerService.this.mPackageManager.forgetPackageInfo(packageName, userId);
                        if (OverlayManagerService.this.mImpl.getOverlayInfo(packageName, userId) != null) {
                            OverlayManagerService.this.mImpl.onOverlayPackageReplacing(packageName, userId);
                        }
                    }
                }
                Trace.traceEnd(67108864);
            } catch (Throwable th) {
                Trace.traceEnd(67108864);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        private void onPackageReplaced(String packageName, int[] userIds) {
            try {
                Trace.traceBegin(67108864, "OMS#onPackageReplaced " + packageName);
                int length = userIds.length;
                for (int i = 0; i < length; i++) {
                    int userId = userIds[i];
                    synchronized (OverlayManagerService.this.mLock) {
                        PackageInfo pi = OverlayManagerService.this.mPackageManager.getPackageInfo(packageName, userId, false);
                        if (pi != null && !pi.applicationInfo.isInstantApp()) {
                            OverlayManagerService.this.mPackageManager.cachePackageInfo(packageName, userId, pi);
                            if (pi.isOverlayPackage()) {
                                OverlayManagerService.this.mImpl.onOverlayPackageReplaced(packageName, userId);
                            } else {
                                OverlayManagerService.this.mImpl.onTargetPackageReplaced(packageName, userId);
                            }
                        }
                    }
                }
                Trace.traceEnd(67108864);
            } catch (Throwable th) {
                Trace.traceEnd(67108864);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        private void onPackageRemoved(String packageName, int[] userIds) {
            try {
                Trace.traceBegin(67108864, "OMS#onPackageRemoved " + packageName);
                for (int userId : userIds) {
                    synchronized (OverlayManagerService.this.mLock) {
                        OverlayManagerService.this.mPackageManager.forgetPackageInfo(packageName, userId);
                        if (OverlayManagerService.this.mImpl.getOverlayInfo(packageName, userId) != null) {
                            OverlayManagerService.this.mImpl.onOverlayPackageRemoved(packageName, userId);
                        } else {
                            OverlayManagerService.this.mImpl.onTargetPackageRemoved(packageName, userId);
                        }
                    }
                }
                Trace.traceEnd(67108864);
            } catch (Throwable th) {
                Trace.traceEnd(67108864);
                throw th;
            }
        }
    }

    private final class UserReceiver extends BroadcastReceiver {
        private UserReceiver() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x0036  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0067  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r7, android.content.Intent r8) {
            /*
                r6 = this;
                r0 = -10000(0xffffffffffffd8f0, float:NaN)
                java.lang.String r1 = "android.intent.extra.user_handle"
                int r1 = r8.getIntExtra(r1, r0)
                java.lang.String r2 = r8.getAction()
                int r3 = r2.hashCode()
                r4 = -2061058799(0xffffffff8526bd11, float:-7.8400085E-36)
                r5 = 1
                if (r3 == r4) goto L_0x0026
                r4 = 1121780209(0x42dd01f1, float:110.50379)
                if (r3 == r4) goto L_0x001c
            L_0x001b:
                goto L_0x0030
            L_0x001c:
                java.lang.String r3 = "android.intent.action.USER_ADDED"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x001b
                r2 = 0
                goto L_0x0031
            L_0x0026:
                java.lang.String r3 = "android.intent.action.USER_REMOVED"
                boolean r2 = r2.equals(r3)
                if (r2 == 0) goto L_0x001b
                r2 = r5
                goto L_0x0031
            L_0x0030:
                r2 = -1
            L_0x0031:
                r3 = 67108864(0x4000000, double:3.31561842E-316)
                if (r2 == 0) goto L_0x0067
                if (r2 == r5) goto L_0x0039
                goto L_0x0092
            L_0x0039:
                if (r1 == r0) goto L_0x0092
                java.lang.String r0 = "OMS ACTION_USER_REMOVED"
                android.os.Trace.traceBegin(r3, r0)     // Catch:{ all -> 0x0062 }
                com.android.server.om.OverlayManagerService r0 = com.android.server.om.OverlayManagerService.this     // Catch:{ all -> 0x0062 }
                java.lang.Object r0 = r0.mLock     // Catch:{ all -> 0x0062 }
                monitor-enter(r0)     // Catch:{ all -> 0x0062 }
                com.android.server.om.OverlayManagerService r2 = com.android.server.om.OverlayManagerService.this     // Catch:{ all -> 0x005f }
                com.android.server.om.OverlayManagerServiceImpl r2 = r2.mImpl     // Catch:{ all -> 0x005f }
                r2.onUserRemoved(r1)     // Catch:{ all -> 0x005f }
                com.android.server.om.OverlayManagerService r2 = com.android.server.om.OverlayManagerService.this     // Catch:{ all -> 0x005f }
                com.android.server.om.OverlayManagerService$PackageManagerHelper r2 = r2.mPackageManager     // Catch:{ all -> 0x005f }
                r2.forgetAllPackageInfos(r1)     // Catch:{ all -> 0x005f }
                monitor-exit(r0)     // Catch:{ all -> 0x005f }
                android.os.Trace.traceEnd(r3)
                goto L_0x0092
            L_0x005f:
                r2 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x005f }
                throw r2     // Catch:{ all -> 0x0062 }
            L_0x0062:
                r0 = move-exception
                android.os.Trace.traceEnd(r3)
                throw r0
            L_0x0067:
                if (r1 == r0) goto L_0x0092
                java.lang.String r0 = "OMS ACTION_USER_ADDED"
                android.os.Trace.traceBegin(r3, r0)     // Catch:{ all -> 0x008d }
                com.android.server.om.OverlayManagerService r0 = com.android.server.om.OverlayManagerService.this     // Catch:{ all -> 0x008d }
                java.lang.Object r0 = r0.mLock     // Catch:{ all -> 0x008d }
                monitor-enter(r0)     // Catch:{ all -> 0x008d }
                com.android.server.om.OverlayManagerService r2 = com.android.server.om.OverlayManagerService.this     // Catch:{ all -> 0x008a }
                com.android.server.om.OverlayManagerServiceImpl r2 = r2.mImpl     // Catch:{ all -> 0x008a }
                java.util.ArrayList r2 = r2.updateOverlaysForUser(r1)     // Catch:{ all -> 0x008a }
                monitor-exit(r0)     // Catch:{ all -> 0x008a }
                com.android.server.om.OverlayManagerService r0 = com.android.server.om.OverlayManagerService.this     // Catch:{ all -> 0x008d }
                r0.updateOverlayPaths(r1, r2)     // Catch:{ all -> 0x008d }
                android.os.Trace.traceEnd(r3)
                goto L_0x0092
            L_0x008a:
                r2 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x008a }
                throw r2     // Catch:{ all -> 0x008d }
            L_0x008d:
                r0 = move-exception
                android.os.Trace.traceEnd(r3)
                throw r0
            L_0x0092:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.om.OverlayManagerService.UserReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    private final class OverlayChangeListener implements OverlayManagerServiceImpl.OverlayChangeListener {
        private OverlayChangeListener() {
        }

        public void onOverlaysChanged(String targetPackageName, int userId) {
            OverlayManagerService.this.schedulePersistSettings();
            FgThread.getHandler().post(new Runnable(targetPackageName, userId) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    OverlayManagerService.OverlayChangeListener.this.lambda$onOverlaysChanged$0$OverlayManagerService$OverlayChangeListener(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$onOverlaysChanged$0$OverlayManagerService$OverlayChangeListener(String targetPackageName, int userId) {
            String str = targetPackageName;
            for (int targetUserId : (str == null || !str.equals(PackageManagerService.PLATFORM_PACKAGE_NAME)) ? new int[]{userId} : OverlayManagerService.this.mUserManager.getUserIds()) {
                OverlayManagerService.this.updateAssets(targetUserId, str);
                Intent intent = new Intent("android.intent.action.OVERLAY_CHANGED", Uri.fromParts(Settings.ATTR_PACKAGE, str, (String) null));
                intent.setFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
                try {
                    Intent intent2 = intent;
                    try {
                        ActivityManager.getService().broadcastIntent((IApplicationThread) null, intent, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, (String[]) null, -1, (Bundle) null, false, false, targetUserId);
                    } catch (RemoteException e) {
                    }
                } catch (RemoteException e2) {
                    Intent intent3 = intent;
                    int i = targetUserId;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* access modifiers changed from: private */
    public void updateOverlayPaths(int userId, List<String> targetPackageNames) {
        try {
            Trace.traceBegin(67108864, "OMS#updateOverlayPaths " + targetPackageNames);
            PackageManagerInternal pm = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            if (targetPackageNames.contains(PackageManagerService.PLATFORM_PACKAGE_NAME)) {
                targetPackageNames = pm.getTargetPackageNames(userId);
            }
            Map<String, List<String>> pendingChanges = new ArrayMap<>(targetPackageNames.size());
            synchronized (this.mLock) {
                List<String> frameworkOverlays = this.mImpl.getEnabledOverlayPackageNames(PackageManagerService.PLATFORM_PACKAGE_NAME, userId);
                int n = targetPackageNames.size();
                for (int i = 0; i < n; i++) {
                    String targetPackageName = targetPackageNames.get(i);
                    List<String> list = new ArrayList<>();
                    if (!PackageManagerService.PLATFORM_PACKAGE_NAME.equals(targetPackageName)) {
                        list.addAll(frameworkOverlays);
                    }
                    list.addAll(this.mImpl.getEnabledOverlayPackageNames(targetPackageName, userId));
                    pendingChanges.put(targetPackageName, list);
                }
            }
            int n2 = targetPackageNames.size();
            for (int i2 = 0; i2 < n2; i2++) {
                String targetPackageName2 = targetPackageNames.get(i2);
                if (!pm.setEnabledOverlayPackages(userId, targetPackageName2, pendingChanges.get(targetPackageName2))) {
                    Slog.e(TAG, String.format("Failed to change enabled overlays for %s user %d", new Object[]{targetPackageName2, Integer.valueOf(userId)}));
                }
            }
            Trace.traceEnd(67108864);
        } catch (Throwable pm2) {
            Trace.traceEnd(67108864);
            throw pm2;
        }
    }

    /* access modifiers changed from: private */
    public void updateAssets(int userId, String targetPackageName) {
        updateAssets(userId, (List<String>) Collections.singletonList(targetPackageName));
    }

    private void updateAssets(int userId, List<String> targetPackageNames) {
        updateOverlayPaths(userId, targetPackageNames);
        try {
            ActivityManager.getService().scheduleApplicationInfoChanged(targetPackageNames, userId);
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: private */
    public void schedulePersistSettings() {
        if (!this.mPersistSettingsScheduled.getAndSet(true)) {
            IoThread.getHandler().post(new Runnable() {
                public final void run() {
                    OverlayManagerService.this.lambda$schedulePersistSettings$0$OverlayManagerService();
                }
            });
        }
    }

    public /* synthetic */ void lambda$schedulePersistSettings$0$OverlayManagerService() {
        this.mPersistSettingsScheduled.set(false);
        synchronized (this.mLock) {
            FileOutputStream stream = null;
            try {
                stream = this.mSettingsFile.startWrite();
                this.mSettings.persist(stream);
                this.mSettingsFile.finishWrite(stream);
            } catch (IOException | XmlPullParserException e) {
                this.mSettingsFile.failWrite(stream);
                Slog.e(TAG, "failed to persist overlay state", e);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0073, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0074, code lost:
        if (r3 != null) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x007e, code lost:
        throw r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void restoreSettings() {
        /*
            r11 = this;
            r0 = 67108864(0x4000000, double:3.31561842E-316)
            java.lang.String r2 = "OMS#restoreSettings"
            android.os.Trace.traceBegin(r0, r2)     // Catch:{ all -> 0x0090 }
            java.lang.Object r2 = r11.mLock     // Catch:{ all -> 0x0090 }
            monitor-enter(r2)     // Catch:{ all -> 0x0090 }
            android.util.AtomicFile r3 = r11.mSettingsFile     // Catch:{ all -> 0x008d }
            java.io.File r3 = r3.getBaseFile()     // Catch:{ all -> 0x008d }
            boolean r3 = r3.exists()     // Catch:{ all -> 0x008d }
            if (r3 != 0) goto L_0x001c
            monitor-exit(r2)     // Catch:{ all -> 0x008d }
            android.os.Trace.traceEnd(r0)
            return
        L_0x001c:
            android.util.AtomicFile r3 = r11.mSettingsFile     // Catch:{ IOException | XmlPullParserException -> 0x007f }
            java.io.FileInputStream r3 = r3.openRead()     // Catch:{ IOException | XmlPullParserException -> 0x007f }
            com.android.server.om.OverlayManagerSettings r4 = r11.mSettings     // Catch:{ all -> 0x0071 }
            r4.restore(r3)     // Catch:{ all -> 0x0071 }
            com.android.server.pm.UserManagerService r4 = r11.mUserManager     // Catch:{ all -> 0x0071 }
            r5 = 1
            java.util.List r4 = r4.getUsers(r5)     // Catch:{ all -> 0x0071 }
            int r5 = r4.size()     // Catch:{ all -> 0x0071 }
            int[] r5 = new int[r5]     // Catch:{ all -> 0x0071 }
            r6 = 0
            r7 = r6
        L_0x0036:
            int r8 = r4.size()     // Catch:{ all -> 0x0071 }
            if (r7 >= r8) goto L_0x004f
            java.lang.Object r8 = r4.get(r7)     // Catch:{ all -> 0x0071 }
            android.content.pm.UserInfo r8 = (android.content.pm.UserInfo) r8     // Catch:{ all -> 0x0071 }
            android.os.UserHandle r8 = r8.getUserHandle()     // Catch:{ all -> 0x0071 }
            int r8 = r8.getIdentifier()     // Catch:{ all -> 0x0071 }
            r5[r7] = r8     // Catch:{ all -> 0x0071 }
            int r7 = r7 + 1
            goto L_0x0036
        L_0x004f:
            java.util.Arrays.sort(r5)     // Catch:{ all -> 0x0071 }
            com.android.server.om.OverlayManagerSettings r7 = r11.mSettings     // Catch:{ all -> 0x0071 }
            int[] r7 = r7.getUsers()     // Catch:{ all -> 0x0071 }
            int r8 = r7.length     // Catch:{ all -> 0x0071 }
        L_0x0059:
            if (r6 >= r8) goto L_0x006b
            r9 = r7[r6]     // Catch:{ all -> 0x0071 }
            int r10 = java.util.Arrays.binarySearch(r5, r9)     // Catch:{ all -> 0x0071 }
            if (r10 >= 0) goto L_0x0068
            com.android.server.om.OverlayManagerSettings r10 = r11.mSettings     // Catch:{ all -> 0x0071 }
            r10.removeUser(r9)     // Catch:{ all -> 0x0071 }
        L_0x0068:
            int r6 = r6 + 1
            goto L_0x0059
        L_0x006b:
            if (r3 == 0) goto L_0x0070
            r3.close()     // Catch:{ IOException | XmlPullParserException -> 0x007f }
        L_0x0070:
            goto L_0x0087
        L_0x0071:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0073 }
        L_0x0073:
            r5 = move-exception
            if (r3 == 0) goto L_0x007e
            r3.close()     // Catch:{ all -> 0x007a }
            goto L_0x007e
        L_0x007a:
            r6 = move-exception
            r4.addSuppressed(r6)     // Catch:{ IOException | XmlPullParserException -> 0x007f }
        L_0x007e:
            throw r5     // Catch:{ IOException | XmlPullParserException -> 0x007f }
        L_0x007f:
            r3 = move-exception
            java.lang.String r4 = "OverlayManager"
            java.lang.String r5 = "failed to restore overlay state"
            android.util.Slog.e(r4, r5, r3)     // Catch:{ all -> 0x008d }
        L_0x0087:
            monitor-exit(r2)     // Catch:{ all -> 0x008d }
            android.os.Trace.traceEnd(r0)
            return
        L_0x008d:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x008d }
            throw r3     // Catch:{ all -> 0x0090 }
        L_0x0090:
            r2 = move-exception
            android.os.Trace.traceEnd(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.om.OverlayManagerService.restoreSettings():void");
    }

    private static final class PackageManagerHelper implements OverlayManagerServiceImpl.PackageManagerHelper {
        private static final String TAB1 = "    ";
        private static final String TAB2 = "        ";
        private final SparseArray<HashMap<String, PackageInfo>> mCache = new SparseArray<>();
        private final IPackageManager mPackageManager = AppGlobals.getPackageManager();
        private final PackageManagerInternal mPackageManagerInternal = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class));

        PackageManagerHelper() {
        }

        public PackageInfo getPackageInfo(String packageName, int userId, boolean useCache) {
            PackageInfo cachedPi;
            if (useCache && (cachedPi = getCachedPackageInfo(packageName, userId)) != null) {
                return cachedPi;
            }
            try {
                PackageInfo pi = this.mPackageManager.getPackageInfo(packageName, 0, userId);
                if (useCache && pi != null) {
                    cachePackageInfo(packageName, userId, pi);
                }
                return pi;
            } catch (RemoteException e) {
                return null;
            }
        }

        public PackageInfo getPackageInfo(String packageName, int userId) {
            return getPackageInfo(packageName, userId, true);
        }

        public boolean signaturesMatching(String packageName1, String packageName2, int userId) {
            try {
                return this.mPackageManager.checkSignatures(packageName1, packageName2) == 0;
            } catch (RemoteException e) {
                return false;
            }
        }

        public List<PackageInfo> getOverlayPackages(int userId) {
            return this.mPackageManagerInternal.getOverlayPackages(userId);
        }

        public PackageInfo getCachedPackageInfo(String packageName, int userId) {
            HashMap<String, PackageInfo> map = this.mCache.get(userId);
            if (map == null) {
                return null;
            }
            return map.get(packageName);
        }

        public void cachePackageInfo(String packageName, int userId, PackageInfo pi) {
            HashMap<String, PackageInfo> map = this.mCache.get(userId);
            if (map == null) {
                map = new HashMap<>();
                this.mCache.put(userId, map);
            }
            map.put(packageName, pi);
        }

        public void forgetPackageInfo(String packageName, int userId) {
            HashMap<String, PackageInfo> map = this.mCache.get(userId);
            if (map != null) {
                map.remove(packageName);
                if (map.isEmpty()) {
                    this.mCache.delete(userId);
                }
            }
        }

        public void forgetAllPackageInfos(int userId) {
            this.mCache.delete(userId);
        }

        public void dump(PrintWriter pw, DumpState dumpState) {
            pw.println("PackageInfo cache");
            if (!dumpState.isVerbose()) {
                int count = 0;
                int n = this.mCache.size();
                for (int i = 0; i < n; i++) {
                    count += this.mCache.get(this.mCache.keyAt(i)).size();
                }
                pw.println(TAB1 + count + " package(s)");
            } else if (this.mCache.size() == 0) {
                pw.println("    <empty>");
            } else {
                int n2 = this.mCache.size();
                for (int i2 = 0; i2 < n2; i2++) {
                    int userId = this.mCache.keyAt(i2);
                    pw.println("    User " + userId);
                    for (Map.Entry<String, PackageInfo> entry : this.mCache.get(userId).entrySet()) {
                        pw.println(TAB2 + entry.getKey() + ": " + entry.getValue());
                    }
                }
            }
        }
    }
}
