package com.android.server.storage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.RuntimeInit;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.EventLogTags;
import com.android.server.SystemService;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.InstructionSets;
import com.android.server.utils.PriorityDump;
import dalvik.system.VMRuntime;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DeviceStorageMonitorService extends SystemService {
    private static final long BOOT_IMAGE_STORAGE_REQUIREMENT = 262144000;
    private static final long DEFAULT_CHECK_INTERVAL = 60000;
    private static final long DEFAULT_LOG_DELTA_BYTES = 67108864;
    public static final String EXTRA_SEQUENCE = "seq";
    private static final long GB = 1000000000;
    private static final long KB = 1000;
    private static final long MB = 1000000;
    private static final int MSG_CHECK = 1;
    static final int OPTION_FORCE_UPDATE = 1;
    static final String SERVICE = "devicestoragemonitor";
    private static final String TAG = "DeviceStorageMonitorService";
    private static final String TV_NOTIFICATION_CHANNEL_ID = "devicestoragemonitor.tv";
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.USER_PRESENT".equals(action) && DeviceStorageMonitorService.this.mLevel >= 2) {
                DeviceStorageMonitorService.this.check();
                if (DeviceStorageMonitorService.this.mLevel == 3) {
                    DeviceStorageMonitorService.this.doMemLowAction();
                }
            } else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
                DeviceStorageMonitorService.this.check();
            }
        }
    };
    private CacheFileDeletedObserver mCacheFileDeletedObserver;
    private volatile int mForceLevel = -1;
    private long mFreeMem = 0;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private final HandlerThread mHandlerThread = new HandlerThread(TAG, 10);
    /* access modifiers changed from: private */
    public int mLevel = 0;
    private final DeviceStorageMonitorInternal mLocalService = new DeviceStorageMonitorInternal() {
        public void checkMemory() {
            DeviceStorageMonitorService.this.mHandler.removeMessages(1);
            DeviceStorageMonitorService.this.mHandler.obtainMessage(1).sendToTarget();
        }

        public boolean isMemoryLow() {
            return Environment.getDataDirectory().getUsableSpace() < getMemoryLowThreshold();
        }

        public long getMemoryLowThreshold() {
            return ((StorageManager) DeviceStorageMonitorService.this.getContext().getSystemService(StorageManager.class)).getStorageLowBytes(Environment.getDataDirectory());
        }
    };
    private NotificationManager mNotifManager;
    private final Binder mRemoteService = new Binder() {
        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(DeviceStorageMonitorService.this.getContext(), DeviceStorageMonitorService.TAG, pw)) {
                DeviceStorageMonitorService.this.dumpImpl(fd, pw, args);
            }
        }

        public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) {
            new Shell().exec(this, in, out, err, args, callback, resultReceiver);
        }
    };
    private final AtomicInteger mSeq = new AtomicInteger(1);
    private final ArrayMap<UUID, State> mStates = new ArrayMap<>();

    private static class State {
        private static final int LEVEL_FULL = 3;
        private static final int LEVEL_LOW = 2;
        private static final int LEVEL_MEDIUM = 1;
        private static final int LEVEL_NORMAL = 0;
        private static final int LEVEL_UNKNOWN = -1;
        public long lastUsableBytes;
        public int level;

        private State() {
            this.level = 0;
            this.lastUsableBytes = JobStatus.NO_LATEST_RUNTIME;
        }

        /* access modifiers changed from: private */
        public static boolean isEntering(int level2, int oldLevel, int newLevel) {
            return newLevel >= level2 && (oldLevel < level2 || oldLevel == -1);
        }

        /* access modifiers changed from: private */
        public static boolean isMiuiEntering(int level2, int oldLevel, int newLevel) {
            return newLevel >= level2 && (oldLevel != newLevel || oldLevel == -1);
        }

        /* access modifiers changed from: private */
        public static boolean isLeaving(int level2, int oldLevel, int newLevel) {
            return newLevel < level2 && (oldLevel >= level2 || oldLevel == -1);
        }

        /* access modifiers changed from: private */
        public static String levelToString(int level2) {
            if (level2 == -1) {
                return "UNKNOWN";
            }
            if (level2 == 0) {
                return PriorityDump.PRIORITY_ARG_NORMAL;
            }
            if (level2 == 1) {
                return "MEDIUM";
            }
            if (level2 == 2) {
                return "LOW";
            }
            if (level2 != 3) {
                return Integer.toString(level2);
            }
            return "FULL";
        }
    }

    /* access modifiers changed from: private */
    public void doMemLowAction() {
        try {
            if (this.mLevel > 1) {
                Slog.i(TAG, "low memory, getMemLevel free memory is " + this.mFreeMem + " level is " + State.levelToString(this.mLevel));
                Intent memDangerousLowIntent = new Intent("com.miui.securitycenter.action.START_LOW_MEMORY_CLEAN");
                memDangerousLowIntent.putExtra("memory", this.mFreeMem);
                memDangerousLowIntent.putExtra("level", this.mLevel);
                memDangerousLowIntent.addFlags(268435456);
                getContext().startActivity(memDangerousLowIntent);
            }
        } catch (Exception e) {
            Slog.i(TAG, "low memory, Exception is " + e);
        }
    }

    private void cancelMemLowAction() {
        try {
            SystemProperties.set("sys.is_mem_low_retried", "false");
            this.mLevel = 0;
            SystemProperties.set("sys.is_mem_low_level", Integer.toString(this.mLevel));
            Slog.i(TAG, "enable the statusbar,recents and home key.");
        } catch (Exception e) {
            Slog.e(TAG, "SystemProperties set, Exception is " + e);
        }
    }

    public void onBootPhase(int phase) {
        super.onBootPhase(phase);
        if (phase == 1000) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.USER_PRESENT");
            filter.addAction("android.intent.action.BOOT_COMPLETED");
            getContext().registerReceiver(this.mBroadcastReceiver, filter);
        }
    }

    private State findOrCreateState(UUID uuid) {
        State state = this.mStates.get(uuid);
        if (state != null) {
            return state;
        }
        State state2 = new State();
        this.mStates.put(uuid, state2);
        return state2;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x009b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void check() {
        /*
            r32 = this;
            r1 = r32
            android.content.Context r0 = r32.getContext()
            java.lang.Class<android.os.storage.StorageManager> r2 = android.os.storage.StorageManager.class
            java.lang.Object r0 = r0.getSystemService(r2)
            r2 = r0
            android.os.storage.StorageManager r2 = (android.os.storage.StorageManager) r2
            java.util.concurrent.atomic.AtomicInteger r0 = r1.mSeq
            int r3 = r0.get()
            java.util.List r0 = r2.getWritablePrivateVolumes()
            java.util.Iterator r4 = r0.iterator()
        L_0x001d:
            boolean r0 = r4.hasNext()
            if (r0 == 0) goto L_0x013c
            java.lang.Object r0 = r4.next()
            r5 = r0
            android.os.storage.VolumeInfo r5 = (android.os.storage.VolumeInfo) r5
            java.io.File r6 = r5.getPath()
            java.io.File r0 = android.os.Environment.getDataDirectory()
            boolean r0 = r0.equals(r6)
            if (r0 != 0) goto L_0x0039
            goto L_0x001d
        L_0x0039:
            r7 = 1000000000(0x3b9aca00, double:4.94065646E-315)
            r9 = 500000000(0x1dcd6500, double:2.47032823E-315)
            r11 = 100000000(0x5f5e100, double:4.94065646E-316)
            long r13 = r6.getUsableSpace()
            r15 = 750000000(0x2cb41780, double:3.705492344E-315)
            int r0 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            java.lang.String r13 = "DeviceStorageMonitorService"
            if (r0 >= 0) goto L_0x0071
            java.lang.String r0 = "package"
            android.os.IBinder r0 = android.os.ServiceManager.getService(r0)
            r14 = r0
            com.android.server.pm.PackageManagerService r14 = (com.android.server.pm.PackageManagerService) r14
            java.lang.String r0 = r5.getFsUuid()     // Catch:{ IOException -> 0x006a }
            r15 = 0
            r18 = r7
            r7 = 1000000000(0x3b9aca00, double:4.94065646E-315)
            r14.freeStorage(r0, r7, r15)     // Catch:{ IOException -> 0x0068 }
            goto L_0x0073
        L_0x0068:
            r0 = move-exception
            goto L_0x006d
        L_0x006a:
            r0 = move-exception
            r18 = r7
        L_0x006d:
            android.util.Slog.w(r13, r0)
            goto L_0x0073
        L_0x0071:
            r18 = r7
        L_0x0073:
            java.lang.String r0 = r5.getFsUuid()
            java.util.UUID r7 = android.os.storage.StorageManager.convert(r0)
            com.android.server.storage.DeviceStorageMonitorService$State r8 = r1.findOrCreateState(r7)
            long r14 = r6.getTotalSpace()
            r27 = r9
            long r9 = r6.getUsableSpace()
            int r0 = r8.level
            r20 = r0
            int r0 = r1.mForceLevel
            r29 = r2
            r2 = -1
            if (r0 == r2) goto L_0x009b
            r0 = -1
            int r2 = r1.mForceLevel
            r16 = r4
            r4 = r0
            goto L_0x00e7
        L_0x009b:
            r21 = 100000000(0x5f5e100, double:4.94065646E-316)
            int r0 = (r9 > r21 ? 1 : (r9 == r21 ? 0 : -1))
            if (r0 > 0) goto L_0x00a9
            r0 = 3
            r2 = r0
            r16 = r4
            r4 = r20
            goto L_0x00e7
        L_0x00a9:
            r21 = 500000000(0x1dcd6500, double:2.47032823E-315)
            int r0 = (r9 > r21 ? 1 : (r9 == r21 ? 0 : -1))
            if (r0 > 0) goto L_0x00b7
            r0 = 2
            r2 = r0
            r16 = r4
            r4 = r20
            goto L_0x00e7
        L_0x00b7:
            r16 = 1000000000(0x3b9aca00, double:4.94065646E-315)
            int r0 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r0 > 0) goto L_0x00c5
            r0 = 1
            r2 = r0
            r16 = r4
            r4 = r20
            goto L_0x00e7
        L_0x00c5:
            java.util.UUID r0 = android.os.storage.StorageManager.UUID_DEFAULT
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00e1
            boolean r0 = isBootImageOnDisk()
            if (r0 != 0) goto L_0x00e1
            r16 = 262144000(0xfa00000, double:1.295163447E-315)
            int r0 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r0 >= 0) goto L_0x00e1
            r0 = 2
            r2 = r0
            r16 = r4
            r4 = r20
            goto L_0x00e7
        L_0x00e1:
            r0 = 0
            r2 = r0
            r16 = r4
            r4 = r20
        L_0x00e7:
            r1.mFreeMem = r9
            r1.mLevel = r2
            r30 = r11
            long r11 = r8.lastUsableBytes
            long r11 = r11 - r9
            long r11 = java.lang.Math.abs(r11)
            r20 = 67108864(0x4000000, double:3.31561842E-316)
            int r0 = (r11 > r20 ? 1 : (r11 == r20 ? 0 : -1))
            if (r0 > 0) goto L_0x00fd
            if (r4 == r2) goto L_0x012e
        L_0x00fd:
            java.lang.String r0 = "sys.is_mem_low_level"
            java.lang.String r11 = java.lang.Integer.toString(r2)     // Catch:{ Exception -> 0x0108 }
            android.os.SystemProperties.set(r0, r11)     // Catch:{ Exception -> 0x0108 }
            goto L_0x011d
        L_0x0108:
            r0 = move-exception
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "SystemProperties set, Exception is "
            r11.append(r12)
            r11.append(r0)
            java.lang.String r11 = r11.toString()
            android.util.Slog.e(r13, r11)
        L_0x011d:
            java.lang.String r20 = r7.toString()
            r21 = r4
            r22 = r2
            r23 = r9
            r25 = r14
            com.android.server.EventLogTags.writeStorageState(r20, r21, r22, r23, r25)
            r8.lastUsableBytes = r9
        L_0x012e:
            r1.updateNotifications(r5, r4, r2)
            r1.updateBroadcasts(r5, r4, r2, r3)
            r8.level = r2
            r4 = r16
            r2 = r29
            goto L_0x001d
        L_0x013c:
            r29 = r2
            android.os.Handler r0 = r1.mHandler
            r2 = 1
            boolean r0 = r0.hasMessages(r2)
            if (r0 != 0) goto L_0x0153
            android.os.Handler r0 = r1.mHandler
            android.os.Message r2 = r0.obtainMessage(r2)
            r4 = 60000(0xea60, double:2.9644E-319)
            r0.sendMessageDelayed(r2, r4)
        L_0x0153:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.storage.DeviceStorageMonitorService.check():void");
    }

    public DeviceStorageMonitorService(Context context) {
        super(context);
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    DeviceStorageMonitorService.this.check();
                }
            }
        };
    }

    private static boolean isBootImageOnDisk() {
        for (String instructionSet : InstructionSets.getAllDexCodeInstructionSets()) {
            if (!VMRuntime.isBootClassPathOnDisk(instructionSet)) {
                return false;
            }
        }
        return true;
    }

    public void onStart() {
        Context context = getContext();
        this.mNotifManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        this.mCacheFileDeletedObserver = new CacheFileDeletedObserver();
        this.mCacheFileDeletedObserver.startWatching();
        if (context.getPackageManager().hasSystemFeature("android.software.leanback")) {
            this.mNotifManager.createNotificationChannel(new NotificationChannel(TV_NOTIFICATION_CHANNEL_ID, context.getString(17039898), 4));
        }
        publishBinderService(SERVICE, this.mRemoteService);
        publishLocalService(DeviceStorageMonitorInternal.class, this.mLocalService);
    }

    class Shell extends ShellCommand {
        Shell() {
        }

        public int onCommand(String cmd) {
            return DeviceStorageMonitorService.this.onShellCommand(this, cmd);
        }

        public void onHelp() {
            DeviceStorageMonitorService.dumpHelp(getOutPrintWriter());
        }
    }

    /* access modifiers changed from: package-private */
    public int parseOptions(Shell shell) {
        int opts = 0;
        while (true) {
            String nextOption = shell.getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                return opts;
            }
            if ("-f".equals(opt)) {
                opts |= 1;
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onShellCommand(com.android.server.storage.DeviceStorageMonitorService.Shell r9, java.lang.String r10) {
        /*
            r8 = this;
            if (r10 != 0) goto L_0x0007
            int r0 = r9.handleDefaultCommands(r10)
            return r0
        L_0x0007:
            java.io.PrintWriter r0 = r9.getOutPrintWriter()
            int r1 = r10.hashCode()
            r2 = 108404047(0x6761d4f, float:4.628899E-35)
            r3 = -1
            r4 = 0
            r5 = 2
            r6 = 1
            if (r1 == r2) goto L_0x0037
            r2 = 1526871410(0x5b023572, float:3.6650511E16)
            if (r1 == r2) goto L_0x002d
            r2 = 1692300408(0x64de7478, float:3.2828557E22)
            if (r1 == r2) goto L_0x0023
        L_0x0022:
            goto L_0x0042
        L_0x0023:
            java.lang.String r1 = "force-not-low"
            boolean r1 = r10.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = r6
            goto L_0x0043
        L_0x002d:
            java.lang.String r1 = "force-low"
            boolean r1 = r10.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = r4
            goto L_0x0043
        L_0x0037:
            java.lang.String r1 = "reset"
            boolean r1 = r10.equals(r1)
            if (r1 == 0) goto L_0x0022
            r1 = r5
            goto L_0x0043
        L_0x0042:
            r1 = r3
        L_0x0043:
            r2 = 0
            java.lang.String r7 = "android.permission.DEVICE_POWER"
            if (r1 == 0) goto L_0x00a3
            if (r1 == r6) goto L_0x007a
            if (r1 == r5) goto L_0x0051
            int r1 = r9.handleDefaultCommands(r10)
            return r1
        L_0x0051:
            int r1 = r8.parseOptions(r9)
            android.content.Context r5 = r8.getContext()
            r5.enforceCallingOrSelfPermission(r7, r2)
            r8.mForceLevel = r3
            java.util.concurrent.atomic.AtomicInteger r2 = r8.mSeq
            int r2 = r2.incrementAndGet()
            r3 = r1 & 1
            if (r3 == 0) goto L_0x0079
            android.os.Handler r3 = r8.mHandler
            r3.removeMessages(r6)
            android.os.Handler r3 = r8.mHandler
            android.os.Message r3 = r3.obtainMessage(r6)
            r3.sendToTarget()
            r0.println(r2)
        L_0x0079:
            goto L_0x00cc
        L_0x007a:
            int r1 = r8.parseOptions(r9)
            android.content.Context r3 = r8.getContext()
            r3.enforceCallingOrSelfPermission(r7, r2)
            r8.mForceLevel = r4
            java.util.concurrent.atomic.AtomicInteger r2 = r8.mSeq
            int r2 = r2.incrementAndGet()
            r3 = r1 & 1
            if (r3 == 0) goto L_0x00a2
            android.os.Handler r3 = r8.mHandler
            r3.removeMessages(r6)
            android.os.Handler r3 = r8.mHandler
            android.os.Message r3 = r3.obtainMessage(r6)
            r3.sendToTarget()
            r0.println(r2)
        L_0x00a2:
            goto L_0x00cc
        L_0x00a3:
            int r1 = r8.parseOptions(r9)
            android.content.Context r3 = r8.getContext()
            r3.enforceCallingOrSelfPermission(r7, r2)
            r8.mForceLevel = r5
            java.util.concurrent.atomic.AtomicInteger r2 = r8.mSeq
            int r2 = r2.incrementAndGet()
            r3 = r1 & 1
            if (r3 == 0) goto L_0x00cb
            android.os.Handler r3 = r8.mHandler
            r3.removeMessages(r6)
            android.os.Handler r3 = r8.mHandler
            android.os.Message r3 = r3.obtainMessage(r6)
            r3.sendToTarget()
            r0.println(r2)
        L_0x00cb:
        L_0x00cc:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.storage.DeviceStorageMonitorService.onShellCommand(com.android.server.storage.DeviceStorageMonitorService$Shell, java.lang.String):int");
    }

    static void dumpHelp(PrintWriter pw) {
        pw.println("Device storage monitor service (devicestoragemonitor) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("  force-low [-f]");
        pw.println("    Force storage to be low, freezing storage state.");
        pw.println("    -f: force a storage change broadcast be sent, prints new sequence.");
        pw.println("  force-not-low [-f]");
        pw.println("    Force storage to not be low, freezing storage state.");
        pw.println("    -f: force a storage change broadcast be sent, prints new sequence.");
        pw.println("  reset [-f]");
        pw.println("    Unfreeze storage state, returning to current real values.");
        pw.println("    -f: force a storage change broadcast be sent, prints new sequence.");
    }

    /* access modifiers changed from: package-private */
    public void dumpImpl(FileDescriptor fd, PrintWriter _pw, String[] args) {
        IndentingPrintWriter pw = new IndentingPrintWriter(_pw, "  ");
        if (args == null || args.length == 0 || "-a".equals(args[0])) {
            pw.println("Known volumes:");
            pw.increaseIndent();
            for (int i = 0; i < this.mStates.size(); i++) {
                UUID uuid = this.mStates.keyAt(i);
                State state = this.mStates.valueAt(i);
                if (StorageManager.UUID_DEFAULT.equals(uuid)) {
                    pw.println("Default:");
                } else {
                    pw.println(uuid + ":");
                }
                pw.increaseIndent();
                pw.printPair("level", State.levelToString(state.level));
                pw.printPair("lastUsableBytes", Long.valueOf(state.lastUsableBytes));
                pw.println();
                pw.decreaseIndent();
            }
            pw.decreaseIndent();
            pw.println();
            pw.printPair("mSeq", Integer.valueOf(this.mSeq.get()));
            pw.printPair("mForceState", State.levelToString(this.mForceLevel));
            pw.println();
            pw.println();
            return;
        }
        new Shell().exec(this.mRemoteService, (FileDescriptor) null, fd, (FileDescriptor) null, args, (ShellCallback) null, new ResultReceiver((Handler) null));
    }

    private void updateNotifications(VolumeInfo vol, int oldLevel, int newLevel) {
        CharSequence details;
        int i = oldLevel;
        int i2 = newLevel;
        Context context = getContext();
        UUID uuid = StorageManager.convert(vol.getFsUuid());
        if (State.isMiuiEntering(1, i, i2)) {
            Intent lowMemIntent = new Intent("com.miui.securitycenter.LunchCleanMaster");
            lowMemIntent.putExtra("android.os.storage.extra.UUID", uuid);
            lowMemIntent.putExtra("level", this.mLevel);
            lowMemIntent.putExtra("memory", this.mFreeMem);
            doMemLowAction();
            lowMemIntent.addFlags(268435456);
            CharSequence title = context.getText(17040361);
            int i3 = 17040359;
            if (StorageManager.UUID_DEFAULT.equals(uuid)) {
                if (!isBootImageOnDisk()) {
                    i3 = 17040360;
                }
                details = context.getText(i3);
            } else {
                details = context.getText(17040359);
            }
            Notification notification = new Notification.Builder(context, SystemNotificationChannels.ALERTS).setSmallIcon(17303561).setTicker(title).setColor(context.getColor(17170460)).setContentTitle(title).setContentText(details).setContentIntent(PendingIntent.getActivityAsUser(context, 0, lowMemIntent, 134217728, (Bundle) null, UserHandle.CURRENT)).setStyle(new Notification.BigTextStyle().bigText(details)).setVisibility(1).setCategory("sys").extend(new Notification.TvExtender().setChannelId(TV_NOTIFICATION_CHANNEL_ID)).build();
            if (i2 > 1) {
                notification.flags |= 2;
                notification.priority = 2;
            }
            this.mNotifManager.notifyAsUser(uuid.toString(), 23, notification, UserHandle.ALL);
        } else if (State.isLeaving(1, i, i2)) {
            cancelMemLowAction();
            this.mNotifManager.cancelAsUser(uuid.toString(), 23, UserHandle.ALL);
        }
    }

    private void updateBroadcasts(VolumeInfo vol, int oldLevel, int newLevel, int seq) {
        if (Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, vol.getFsUuid())) {
            Intent lowIntent = new Intent("android.intent.action.DEVICE_STORAGE_LOW").addFlags(85983232).putExtra(EXTRA_SEQUENCE, seq);
            Intent notLowIntent = new Intent("android.intent.action.DEVICE_STORAGE_OK").addFlags(85983232).putExtra(EXTRA_SEQUENCE, seq);
            if (State.isEntering(2, oldLevel, newLevel)) {
                getContext().sendStickyBroadcastAsUser(lowIntent, UserHandle.ALL);
            } else if (State.isLeaving(2, oldLevel, newLevel)) {
                getContext().removeStickyBroadcastAsUser(lowIntent, UserHandle.ALL);
                getContext().sendBroadcastAsUser(notLowIntent, UserHandle.ALL);
            }
            Intent fullIntent = new Intent("android.intent.action.DEVICE_STORAGE_FULL").addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE).putExtra(EXTRA_SEQUENCE, seq);
            Intent notFullIntent = new Intent("android.intent.action.DEVICE_STORAGE_NOT_FULL").addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE).putExtra(EXTRA_SEQUENCE, seq);
            if (State.isEntering(3, oldLevel, newLevel)) {
                RuntimeInit.removeFileForLowMem();
                getContext().sendStickyBroadcastAsUser(fullIntent, UserHandle.ALL);
            } else if (State.isLeaving(3, oldLevel, newLevel)) {
                getContext().removeStickyBroadcastAsUser(fullIntent, UserHandle.ALL);
                getContext().sendBroadcastAsUser(notFullIntent, UserHandle.ALL);
            }
        }
    }

    private static class CacheFileDeletedObserver extends FileObserver {
        public CacheFileDeletedObserver() {
            super(Environment.getDownloadCacheDirectory().getAbsolutePath(), 512);
        }

        public void onEvent(int event, String path) {
            EventLogTags.writeCacheFileDeleted(path);
        }
    }
}
