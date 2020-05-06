package com.android.server;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ShellCommand;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.IDropBoxManagerService;
import com.android.internal.util.ObjectUtils;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.PackageManagerService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public final class DropBoxManagerService extends SystemService {
    private static final int DEFAULT_AGE_SECONDS = 259200;
    private static final int DEFAULT_MAX_FILES = 1000;
    private static final int DEFAULT_MAX_FILES_LOWRAM = 300;
    private static final int DEFAULT_QUOTA_KB = 5120;
    private static final int DEFAULT_QUOTA_PERCENT = 10;
    private static final int DEFAULT_RESERVE_PERCENT = 10;
    private static final boolean PROFILE_DUMP = false;
    private static final int QUOTA_RESCAN_MILLIS = 5000;
    private static final String TAG = "DropBoxManagerService";
    private FileList mAllFiles;
    private int mBlockSize;
    /* access modifiers changed from: private */
    public volatile boolean mBooted;
    private int mCachedQuotaBlocks;
    /* access modifiers changed from: private */
    public long mCachedQuotaUptimeMillis;
    private final ContentResolver mContentResolver;
    private final File mDropBoxDir;
    private ArrayMap<String, FileList> mFilesByTag;
    private final DropBoxManagerBroadcastHandler mHandler;
    /* access modifiers changed from: private */
    public long mLowPriorityRateLimitPeriod;
    private ArraySet<String> mLowPriorityTags;
    private int mMaxFiles;
    /* access modifiers changed from: private */
    public final BroadcastReceiver mReceiver;
    private StatFs mStatFs;
    private final IDropBoxManagerService.Stub mStub;

    private class ShellCmd extends ShellCommand {
        private ShellCmd() {
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int onCommand(java.lang.String r8) {
            /*
                r7 = this;
                if (r8 != 0) goto L_0x0007
                int r0 = r7.handleDefaultCommands(r8)
                return r0
            L_0x0007:
                java.io.PrintWriter r0 = r7.getOutPrintWriter()
                r1 = -1
                r2 = 0
                int r3 = r8.hashCode()     // Catch:{ Exception -> 0x0078 }
                r4 = 3
                r5 = 2
                r6 = 1
                switch(r3) {
                    case -1412652367: goto L_0x0038;
                    case -529247831: goto L_0x002e;
                    case -444925274: goto L_0x0023;
                    case 1936917209: goto L_0x0018;
                    default: goto L_0x0017;
                }     // Catch:{ Exception -> 0x0078 }
            L_0x0017:
                goto L_0x0042
            L_0x0018:
                java.lang.String r3 = "set-rate-limit"
                boolean r3 = r8.equals(r3)     // Catch:{ Exception -> 0x0078 }
                if (r3 == 0) goto L_0x0017
                r1 = r2
                goto L_0x0042
            L_0x0023:
                java.lang.String r3 = "remove-low-priority"
                boolean r3 = r8.equals(r3)     // Catch:{ Exception -> 0x0078 }
                if (r3 == 0) goto L_0x0017
                r1 = r5
                goto L_0x0042
            L_0x002e:
                java.lang.String r3 = "add-low-priority"
                boolean r3 = r8.equals(r3)     // Catch:{ Exception -> 0x0078 }
                if (r3 == 0) goto L_0x0017
                r1 = r6
                goto L_0x0042
            L_0x0038:
                java.lang.String r3 = "restore-defaults"
                boolean r3 = r8.equals(r3)     // Catch:{ Exception -> 0x0078 }
                if (r3 == 0) goto L_0x0017
                r1 = r4
            L_0x0042:
                if (r1 == 0) goto L_0x0069
                if (r1 == r6) goto L_0x005f
                if (r1 == r5) goto L_0x0055
                if (r1 == r4) goto L_0x004f
                int r1 = r7.handleDefaultCommands(r8)     // Catch:{ Exception -> 0x0078 }
                return r1
            L_0x004f:
                com.android.server.DropBoxManagerService r1 = com.android.server.DropBoxManagerService.this     // Catch:{ Exception -> 0x0078 }
                r1.restoreDefaults()     // Catch:{ Exception -> 0x0078 }
                goto L_0x0077
            L_0x0055:
                java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0078 }
                com.android.server.DropBoxManagerService r3 = com.android.server.DropBoxManagerService.this     // Catch:{ Exception -> 0x0078 }
                r3.removeLowPriorityTag(r1)     // Catch:{ Exception -> 0x0078 }
                goto L_0x0077
            L_0x005f:
                java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0078 }
                com.android.server.DropBoxManagerService r3 = com.android.server.DropBoxManagerService.this     // Catch:{ Exception -> 0x0078 }
                r3.addLowPriorityTag(r1)     // Catch:{ Exception -> 0x0078 }
                goto L_0x0077
            L_0x0069:
                java.lang.String r1 = r7.getNextArgRequired()     // Catch:{ Exception -> 0x0078 }
                long r3 = java.lang.Long.parseLong(r1)     // Catch:{ Exception -> 0x0078 }
                com.android.server.DropBoxManagerService r1 = com.android.server.DropBoxManagerService.this     // Catch:{ Exception -> 0x0078 }
                r1.setLowPriorityRateLimit(r3)     // Catch:{ Exception -> 0x0078 }
            L_0x0077:
                goto L_0x007c
            L_0x0078:
                r1 = move-exception
                r0.println(r1)
            L_0x007c:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.DropBoxManagerService.ShellCmd.onCommand(java.lang.String):int");
        }

        public void onHelp() {
            PrintWriter pw = getOutPrintWriter();
            pw.println("Dropbox manager service commands:");
            pw.println("  help");
            pw.println("    Print this help text.");
            pw.println("  set-rate-limit PERIOD");
            pw.println("    Sets low priority broadcast rate limit period to PERIOD ms");
            pw.println("  add-low-priority TAG");
            pw.println("    Add TAG to dropbox low priority list");
            pw.println("  remove-low-priority TAG");
            pw.println("    Remove TAG from dropbox low priority list");
            pw.println("  restore-defaults");
            pw.println("    restore dropbox settings to defaults");
        }
    }

    private class DropBoxManagerBroadcastHandler extends Handler {
        static final int MSG_SEND_BROADCAST = 1;
        static final int MSG_SEND_DEFERRED_BROADCAST = 2;
        @GuardedBy({"mLock"})
        private final ArrayMap<String, Intent> mDeferredMap = new ArrayMap<>();
        private final Object mLock = new Object();

        DropBoxManagerBroadcastHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            Intent deferredIntent;
            int i = msg.what;
            if (i == 1) {
                prepareAndSendBroadcast((Intent) msg.obj);
            } else if (i == 2) {
                synchronized (this.mLock) {
                    deferredIntent = this.mDeferredMap.remove((String) msg.obj);
                }
                if (deferredIntent != null) {
                    prepareAndSendBroadcast(deferredIntent);
                }
            }
        }

        private void prepareAndSendBroadcast(Intent intent) {
            if (!DropBoxManagerService.this.mBooted) {
                intent.addFlags(1073741824);
            }
            DropBoxManagerService.this.getContext().sendBroadcastAsUser(intent, UserHandle.SYSTEM, "android.permission.READ_LOGS");
        }

        private Intent createIntent(String tag, long time) {
            Intent dropboxIntent = new Intent("android.intent.action.DROPBOX_ENTRY_ADDED");
            dropboxIntent.putExtra("tag", tag);
            dropboxIntent.putExtra(SplitScreenReporter.STR_DEAL_TIME, time);
            return dropboxIntent;
        }

        public void sendBroadcast(String tag, long time) {
            sendMessage(obtainMessage(1, createIntent(tag, time)));
        }

        public void maybeDeferBroadcast(String tag, long time) {
            synchronized (this.mLock) {
                Intent intent = this.mDeferredMap.get(tag);
                if (intent == null) {
                    this.mDeferredMap.put(tag, createIntent(tag, time));
                    sendMessageDelayed(obtainMessage(2, tag), DropBoxManagerService.this.mLowPriorityRateLimitPeriod);
                    return;
                }
                intent.putExtra(SplitScreenReporter.STR_DEAL_TIME, time);
                intent.putExtra("android.os.extra.DROPPED_COUNT", intent.getIntExtra("android.os.extra.DROPPED_COUNT", 0) + 1);
            }
        }
    }

    public DropBoxManagerService(Context context) {
        this(context, new File("/data/system/dropbox"), FgThread.get().getLooper());
    }

    @VisibleForTesting
    public DropBoxManagerService(Context context, File path, Looper looper) {
        super(context);
        this.mAllFiles = null;
        this.mFilesByTag = null;
        this.mLowPriorityRateLimitPeriod = 0;
        this.mLowPriorityTags = null;
        this.mStatFs = null;
        this.mBlockSize = 0;
        this.mCachedQuotaBlocks = 0;
        this.mCachedQuotaUptimeMillis = 0;
        this.mBooted = false;
        this.mMaxFiles = -1;
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                long unused = DropBoxManagerService.this.mCachedQuotaUptimeMillis = 0;
                new Thread() {
                    public void run() {
                        try {
                            DropBoxManagerService.this.init();
                            long unused = DropBoxManagerService.this.trimToFit();
                        } catch (IOException e) {
                            Slog.e(DropBoxManagerService.TAG, "Can't init", e);
                        }
                    }
                }.start();
            }
        };
        this.mStub = new IDropBoxManagerService.Stub() {
            public void add(DropBoxManager.Entry entry) {
                DropBoxManagerService.this.add(entry);
            }

            public boolean isTagEnabled(String tag) {
                return DropBoxManagerService.this.isTagEnabled(tag);
            }

            public DropBoxManager.Entry getNextEntry(String tag, long millis, String callingPackage) {
                return DropBoxManagerService.this.getNextEntry(tag, millis, callingPackage);
            }

            public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
                DropBoxManagerService.this.dump(fd, pw, args);
            }

            /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
                /*
                    r8 = this;
                    com.android.server.DropBoxManagerService$ShellCmd r0 = new com.android.server.DropBoxManagerService$ShellCmd
                    com.android.server.DropBoxManagerService r1 = com.android.server.DropBoxManagerService.this
                    r2 = 0
                    r0.<init>()
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.DropBoxManagerService.AnonymousClass2.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
            }
        };
        this.mDropBoxDir = path;
        this.mContentResolver = getContext().getContentResolver();
        this.mHandler = new DropBoxManagerBroadcastHandler(looper);
    }

    public void onStart() {
        publishBinderService("dropbox", this.mStub);
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.DEVICE_STORAGE_LOW");
            getContext().registerReceiver(this.mReceiver, filter);
            this.mContentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, new ContentObserver(new Handler()) {
                public void onChange(boolean selfChange) {
                    DropBoxManagerService.this.mReceiver.onReceive(DropBoxManagerService.this.getContext(), (Intent) null);
                }
            });
            getLowPriorityResourceConfigs();
        } else if (phase == 1000) {
            this.mBooted = true;
        }
    }

    public IDropBoxManagerService getServiceStub() {
        return this.mStub;
    }

    /* Debug info: failed to restart local var, previous not found, register: 24 */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01be  */
    /* JADX WARNING: Removed duplicated region for block: B:87:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void add(android.os.DropBoxManager.Entry r25) {
        /*
            r24 = this;
            r1 = r24
            java.lang.String r2 = "DropBoxManagerService"
            r3 = 0
            r4 = 0
            r5 = 0
            java.lang.String r6 = r25.getTag()
            int r0 = r25.getFlags()     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            r7.<init>()     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.lang.String r8 = "add tag="
            r7.append(r8)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            r7.append(r6)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.lang.String r8 = " isTagEnabled="
            r7.append(r8)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            boolean r8 = r1.isTagEnabled(r6)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            r7.append(r8)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.lang.String r8 = " flags=0x"
            r7.append(r8)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.lang.String r8 = java.lang.Integer.toHexString(r0)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            r7.append(r8)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            android.util.Slog.i(r2, r7)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            r7 = r0 & 1
            if (r7 != 0) goto L_0x0179
            r24.init()     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            boolean r7 = r1.isTagEnabled(r6)     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            if (r7 != 0) goto L_0x0057
            libcore.io.IoUtils.closeQuietly(r5)
            libcore.io.IoUtils.closeQuietly(r4)
            r25.close()
            if (r3 == 0) goto L_0x0056
            r3.delete()
        L_0x0056:
            return
        L_0x0057:
            long r7 = r24.trimToFit()     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            int r11 = r1.mBlockSize     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            byte[] r11 = new byte[r11]     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.io.InputStream r12 = r25.getInputStream()     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            r4 = r12
            r12 = 0
            r13 = r12
        L_0x006a:
            int r14 = r11.length     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            if (r13 >= r14) goto L_0x007b
            int r14 = r11.length     // Catch:{ IOException -> 0x0078 }
            int r14 = r14 - r13
            int r14 = r4.read(r11, r13, r14)     // Catch:{ IOException -> 0x0078 }
            if (r14 > 0) goto L_0x0076
            goto L_0x007b
        L_0x0076:
            int r13 = r13 + r14
            goto L_0x006a
        L_0x0078:
            r0 = move-exception
            goto L_0x0190
        L_0x007b:
            java.io.File r14 = new java.io.File     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.io.File r15 = r1.mDropBoxDir     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            r12.<init>()     // Catch:{ IOException -> 0x018d, all -> 0x0189 }
            r17 = r3
            java.lang.String r3 = "drop"
            r12.append(r3)     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            java.lang.Thread r3 = java.lang.Thread.currentThread()     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            r18 = r7
            long r7 = r3.getId()     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            r12.append(r7)     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            java.lang.String r3 = ".tmp"
            r12.append(r3)     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            java.lang.String r3 = r12.toString()     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            r14.<init>(r15, r3)     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            r3 = r14
            int r7 = r1.mBlockSize     // Catch:{ IOException -> 0x0078 }
            r8 = 4096(0x1000, float:5.74E-42)
            if (r7 <= r8) goto L_0x00ad
            r7 = 4096(0x1000, float:5.74E-42)
        L_0x00ad:
            r8 = 512(0x200, float:7.175E-43)
            if (r7 >= r8) goto L_0x00b3
            r7 = 512(0x200, float:7.175E-43)
        L_0x00b3:
            java.io.FileOutputStream r8 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0078 }
            r8.<init>(r3)     // Catch:{ IOException -> 0x0078 }
            java.io.BufferedOutputStream r12 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x0078 }
            r12.<init>(r8, r7)     // Catch:{ IOException -> 0x0078 }
            r5 = r12
            int r12 = r11.length     // Catch:{ IOException -> 0x0078 }
            if (r13 != r12) goto L_0x00cd
            r12 = r0 & 4
            if (r12 != 0) goto L_0x00cd
            java.util.zip.GZIPOutputStream r12 = new java.util.zip.GZIPOutputStream     // Catch:{ IOException -> 0x0078 }
            r12.<init>(r5)     // Catch:{ IOException -> 0x0078 }
            r5 = r12
            r0 = r0 | 4
        L_0x00cd:
            r12 = 0
            r5.write(r11, r12, r13)     // Catch:{ IOException -> 0x0078 }
            long r14 = java.lang.System.currentTimeMillis()     // Catch:{ IOException -> 0x0078 }
            long r16 = r14 - r9
            r20 = 30000(0x7530, double:1.4822E-319)
            int r16 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1))
            if (r16 <= 0) goto L_0x00e9
            long r16 = r24.trimToFit()     // Catch:{ IOException -> 0x0078 }
            r9 = r14
            r22 = r9
            r9 = r16
            r16 = r22
            goto L_0x00ed
        L_0x00e9:
            r16 = r9
            r9 = r18
        L_0x00ed:
            int r18 = r4.read(r11)     // Catch:{ IOException -> 0x0078 }
            r13 = r18
            if (r13 > 0) goto L_0x00fd
            android.os.FileUtils.sync(r8)     // Catch:{ IOException -> 0x0078 }
            r5.close()     // Catch:{ IOException -> 0x0078 }
            r5 = 0
            goto L_0x0100
        L_0x00fd:
            r5.flush()     // Catch:{ IOException -> 0x0078 }
        L_0x0100:
            long r18 = r3.length()     // Catch:{ IOException -> 0x0078 }
            int r20 = (r18 > r9 ? 1 : (r18 == r9 ? 0 : -1))
            if (r20 <= 0) goto L_0x013e
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0078 }
            r12.<init>()     // Catch:{ IOException -> 0x0078 }
            r20 = r7
            java.lang.String r7 = "Dropping: "
            r12.append(r7)     // Catch:{ IOException -> 0x0078 }
            r12.append(r6)     // Catch:{ IOException -> 0x0078 }
            java.lang.String r7 = " ("
            r12.append(r7)     // Catch:{ IOException -> 0x0078 }
            r21 = r8
            long r7 = r3.length()     // Catch:{ IOException -> 0x0078 }
            r12.append(r7)     // Catch:{ IOException -> 0x0078 }
            java.lang.String r7 = " > "
            r12.append(r7)     // Catch:{ IOException -> 0x0078 }
            r12.append(r9)     // Catch:{ IOException -> 0x0078 }
            java.lang.String r7 = " bytes)"
            r12.append(r7)     // Catch:{ IOException -> 0x0078 }
            java.lang.String r7 = r12.toString()     // Catch:{ IOException -> 0x0078 }
            android.util.Slog.w(r2, r7)     // Catch:{ IOException -> 0x0078 }
            r3.delete()     // Catch:{ IOException -> 0x0078 }
            r3 = 0
            goto L_0x0144
        L_0x013e:
            r20 = r7
            r21 = r8
            if (r13 > 0) goto L_0x016f
        L_0x0144:
            long r7 = r1.createEntry(r3, r6, r0)     // Catch:{ IOException -> 0x0078 }
            r3 = 0
            android.util.ArraySet<java.lang.String> r12 = r1.mLowPriorityTags     // Catch:{ IOException -> 0x0078 }
            if (r12 == 0) goto L_0x015b
            android.util.ArraySet<java.lang.String> r12 = r1.mLowPriorityTags     // Catch:{ IOException -> 0x0078 }
            boolean r12 = r12.contains(r6)     // Catch:{ IOException -> 0x0078 }
            if (r12 == 0) goto L_0x015b
            com.android.server.DropBoxManagerService$DropBoxManagerBroadcastHandler r12 = r1.mHandler     // Catch:{ IOException -> 0x0078 }
            r12.maybeDeferBroadcast(r6, r7)     // Catch:{ IOException -> 0x0078 }
            goto L_0x0160
        L_0x015b:
            com.android.server.DropBoxManagerService$DropBoxManagerBroadcastHandler r12 = r1.mHandler     // Catch:{ IOException -> 0x0078 }
            r12.sendBroadcast(r6, r7)     // Catch:{ IOException -> 0x0078 }
        L_0x0160:
            libcore.io.IoUtils.closeQuietly(r5)
            libcore.io.IoUtils.closeQuietly(r4)
            r25.close()
            if (r3 == 0) goto L_0x01b1
        L_0x016b:
            r3.delete()
            goto L_0x01b1
        L_0x016f:
            r18 = r9
            r9 = r16
            r7 = r20
            r8 = r21
            goto L_0x00cd
        L_0x0179:
            r17 = r3
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            r3.<init>()     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
            throw r3     // Catch:{ IOException -> 0x0185, all -> 0x0181 }
        L_0x0181:
            r0 = move-exception
            r3 = r17
            goto L_0x01b3
        L_0x0185:
            r0 = move-exception
            r3 = r17
            goto L_0x0190
        L_0x0189:
            r0 = move-exception
            r17 = r3
            goto L_0x01b3
        L_0x018d:
            r0 = move-exception
            r17 = r3
        L_0x0190:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b2 }
            r7.<init>()     // Catch:{ all -> 0x01b2 }
            java.lang.String r8 = "Can't write: "
            r7.append(r8)     // Catch:{ all -> 0x01b2 }
            r7.append(r6)     // Catch:{ all -> 0x01b2 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x01b2 }
            android.util.Slog.e(r2, r7, r0)     // Catch:{ all -> 0x01b2 }
            libcore.io.IoUtils.closeQuietly(r5)
            libcore.io.IoUtils.closeQuietly(r4)
            r25.close()
            if (r3 == 0) goto L_0x01b1
            goto L_0x016b
        L_0x01b1:
            return
        L_0x01b2:
            r0 = move-exception
        L_0x01b3:
            libcore.io.IoUtils.closeQuietly(r5)
            libcore.io.IoUtils.closeQuietly(r4)
            r25.close()
            if (r3 == 0) goto L_0x01c1
            r3.delete()
        L_0x01c1:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DropBoxManagerService.add(android.os.DropBoxManager$Entry):void");
    }

    public boolean isTagEnabled(String tag) {
        long token = Binder.clearCallingIdentity();
        try {
            ContentResolver contentResolver = this.mContentResolver;
            return !"disabled".equals(Settings.Global.getString(contentResolver, "dropbox:" + tag));
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private boolean checkPermission(int callingUid, String callingPackage) {
        getContext().enforceCallingOrSelfPermission("android.permission.READ_LOGS", TAG);
        int noteOp = ((AppOpsManager) getContext().getSystemService(AppOpsManager.class)).noteOp(43, callingUid, callingPackage);
        if (noteOp == 0) {
            return true;
        }
        if (noteOp != 3) {
            return false;
        }
        getContext().enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", TAG);
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 15 */
    public synchronized DropBoxManager.Entry getNextEntry(String tag, long millis, String callingPackage) {
        String str = tag;
        synchronized (this) {
            if (!checkPermission(Binder.getCallingUid(), callingPackage)) {
                return null;
            }
            try {
                init();
                FileList list = str == null ? this.mAllFiles : this.mFilesByTag.get(str);
                if (list == null) {
                    return null;
                }
                for (EntryFile entry : list.contents.tailSet(new EntryFile(millis + 1))) {
                    if (entry.tag != null) {
                        if ((entry.flags & 1) != 0) {
                            DropBoxManager.Entry entry2 = new DropBoxManager.Entry(entry.tag, entry.timestampMillis);
                            return entry2;
                        }
                        File file = entry.getFile(this.mDropBoxDir);
                        try {
                            DropBoxManager.Entry entry3 = new DropBoxManager.Entry(entry.tag, entry.timestampMillis, file, entry.flags);
                            return entry3;
                        } catch (IOException e) {
                            Slog.wtf(TAG, "Can't read: " + file, e);
                        }
                    }
                }
                return null;
            } catch (IOException e2) {
                Slog.e(TAG, "Can't init", e2);
                return null;
            }
        }
    }

    /* access modifiers changed from: private */
    public synchronized void setLowPriorityRateLimit(long period) {
        this.mLowPriorityRateLimitPeriod = period;
    }

    /* access modifiers changed from: private */
    public synchronized void addLowPriorityTag(String tag) {
        this.mLowPriorityTags.add(tag);
    }

    /* access modifiers changed from: private */
    public synchronized void removeLowPriorityTag(String tag) {
        this.mLowPriorityTags.remove(tag);
    }

    /* access modifiers changed from: private */
    public synchronized void restoreDefaults() {
        getLowPriorityResourceConfigs();
    }

    /* JADX WARNING: Removed duplicated region for block: B:193:0x0386 A[SYNTHETIC, Splitter:B:193:0x0386] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x038b A[SYNTHETIC, Splitter:B:196:0x038b] */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0399 A[SYNTHETIC, Splitter:B:202:0x0399] */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x039e A[SYNTHETIC, Splitter:B:205:0x039e] */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x03ac A[Catch:{ IOException -> 0x03e7 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void dump(java.io.FileDescriptor r31, java.io.PrintWriter r32, java.lang.String[] r33) {
        /*
            r30 = this;
            r1 = r30
            r2 = r32
            r3 = r33
            monitor-enter(r30)
            android.content.Context r0 = r30.getContext()     // Catch:{ all -> 0x0407 }
            java.lang.String r4 = "DropBoxManagerService"
            boolean r0 = com.android.internal.util.DumpUtils.checkDumpAndUsageStatsPermission(r0, r4, r2)     // Catch:{ all -> 0x0407 }
            if (r0 != 0) goto L_0x0015
            monitor-exit(r30)
            return
        L_0x0015:
            r30.init()     // Catch:{ IOException -> 0x03e7 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0407 }
            r0.<init>()     // Catch:{ all -> 0x0407 }
            r4 = r0
            r0 = 0
            r5 = 0
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0407 }
            r6.<init>()     // Catch:{ all -> 0x0407 }
            r7 = 0
            r8 = r5
            r5 = r0
        L_0x0029:
            if (r3 == 0) goto L_0x00b7
            int r0 = r3.length     // Catch:{ all -> 0x0407 }
            if (r7 >= r0) goto L_0x00b7
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            java.lang.String r9 = "-p"
            boolean r0 = r0.equals(r9)     // Catch:{ all -> 0x0407 }
            if (r0 != 0) goto L_0x00b1
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            java.lang.String r9 = "--print"
            boolean r0 = r0.equals(r9)     // Catch:{ all -> 0x0407 }
            if (r0 == 0) goto L_0x0044
            goto L_0x00b1
        L_0x0044:
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            java.lang.String r9 = "-f"
            boolean r0 = r0.equals(r9)     // Catch:{ all -> 0x0407 }
            if (r0 != 0) goto L_0x00ae
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            java.lang.String r9 = "--file"
            boolean r0 = r0.equals(r9)     // Catch:{ all -> 0x0407 }
            if (r0 == 0) goto L_0x0059
            goto L_0x00ae
        L_0x0059:
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            java.lang.String r9 = "-h"
            boolean r0 = r0.equals(r9)     // Catch:{ all -> 0x0407 }
            if (r0 != 0) goto L_0x008e
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            java.lang.String r9 = "--help"
            boolean r0 = r0.equals(r9)     // Catch:{ all -> 0x0407 }
            if (r0 == 0) goto L_0x006e
            goto L_0x008e
        L_0x006e:
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            java.lang.String r9 = "-"
            boolean r0 = r0.startsWith(r9)     // Catch:{ all -> 0x0407 }
            if (r0 == 0) goto L_0x0088
            java.lang.String r0 = "Unknown argument: "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            goto L_0x00b3
        L_0x0088:
            r0 = r3[r7]     // Catch:{ all -> 0x0407 }
            r6.add(r0)     // Catch:{ all -> 0x0407 }
            goto L_0x00b3
        L_0x008e:
            java.lang.String r0 = "Dropbox (dropbox) dump options:"
            r2.println(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "  [-h|--help] [-p|--print] [-f|--file] [timestamp]"
            r2.println(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "    -h|--help: print this help"
            r2.println(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "    -p|--print: print full contents of each entry"
            r2.println(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "    -f|--file: print path of each entry's file"
            r2.println(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "  [timestamp] optionally filters to only those entries."
            r2.println(r0)     // Catch:{ all -> 0x0407 }
            monitor-exit(r30)
            return
        L_0x00ae:
            r0 = 1
            r8 = r0
            goto L_0x00b3
        L_0x00b1:
            r0 = 1
            r5 = r0
        L_0x00b3:
            int r7 = r7 + 1
            goto L_0x0029
        L_0x00b7:
            java.lang.String r0 = "Drop box contents: "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            com.android.server.DropBoxManagerService$FileList r0 = r1.mAllFiles     // Catch:{ all -> 0x0407 }
            java.util.TreeSet<com.android.server.DropBoxManagerService$EntryFile> r0 = r0.contents     // Catch:{ all -> 0x0407 }
            int r0 = r0.size()     // Catch:{ all -> 0x0407 }
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = " entries\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "Max entries: "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            int r0 = r1.mMaxFiles     // Catch:{ all -> 0x0407 }
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "Low priority rate limit period: "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            long r9 = r1.mLowPriorityRateLimitPeriod     // Catch:{ all -> 0x0407 }
            r4.append(r9)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = " ms\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "Low priority tags: "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            android.util.ArraySet<java.lang.String> r0 = r1.mLowPriorityTags     // Catch:{ all -> 0x0407 }
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            boolean r0 = r6.isEmpty()     // Catch:{ all -> 0x0407 }
            if (r0 != 0) goto L_0x0122
            java.lang.String r0 = "Searching for:"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.util.Iterator r0 = r6.iterator()     // Catch:{ all -> 0x0407 }
        L_0x0108:
            boolean r7 = r0.hasNext()     // Catch:{ all -> 0x0407 }
            if (r7 == 0) goto L_0x011d
            java.lang.Object r7 = r0.next()     // Catch:{ all -> 0x0407 }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x0407 }
            java.lang.String r9 = " "
            r4.append(r9)     // Catch:{ all -> 0x0407 }
            r4.append(r7)     // Catch:{ all -> 0x0407 }
            goto L_0x0108
        L_0x011d:
            java.lang.String r0 = "\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
        L_0x0122:
            r0 = 0
            int r7 = r6.size()     // Catch:{ all -> 0x0407 }
            android.text.format.Time r9 = new android.text.format.Time     // Catch:{ all -> 0x0407 }
            r9.<init>()     // Catch:{ all -> 0x0407 }
            java.lang.String r10 = "\n"
            r4.append(r10)     // Catch:{ all -> 0x0407 }
            com.android.server.DropBoxManagerService$FileList r10 = r1.mAllFiles     // Catch:{ all -> 0x0407 }
            java.util.TreeSet<com.android.server.DropBoxManagerService$EntryFile> r10 = r10.contents     // Catch:{ all -> 0x0407 }
            java.util.Iterator r10 = r10.iterator()     // Catch:{ all -> 0x0407 }
        L_0x0139:
            boolean r11 = r10.hasNext()     // Catch:{ all -> 0x0407 }
            if (r11 == 0) goto L_0x03bf
            java.lang.Object r11 = r10.next()     // Catch:{ all -> 0x0407 }
            com.android.server.DropBoxManagerService$EntryFile r11 = (com.android.server.DropBoxManagerService.EntryFile) r11     // Catch:{ all -> 0x0407 }
            long r12 = r11.timestampMillis     // Catch:{ all -> 0x0407 }
            r9.set(r12)     // Catch:{ all -> 0x0407 }
            java.lang.String r12 = "%Y-%m-%d %H:%M:%S"
            java.lang.String r12 = r9.format(r12)     // Catch:{ all -> 0x0407 }
            r13 = 1
            r14 = 0
        L_0x0152:
            r16 = 1
            if (r14 >= r7) goto L_0x017f
            if (r13 == 0) goto L_0x017f
            java.lang.Object r17 = r6.get(r14)     // Catch:{ all -> 0x0407 }
            java.lang.String r17 = (java.lang.String) r17     // Catch:{ all -> 0x0407 }
            r18 = r17
            r15 = r18
            boolean r18 = r12.contains(r15)     // Catch:{ all -> 0x0407 }
            if (r18 != 0) goto L_0x0176
            r18 = r6
            java.lang.String r6 = r11.tag     // Catch:{ all -> 0x0407 }
            boolean r6 = r15.equals(r6)     // Catch:{ all -> 0x0407 }
            if (r6 == 0) goto L_0x0173
            goto L_0x0178
        L_0x0173:
            r16 = 0
            goto L_0x0178
        L_0x0176:
            r18 = r6
        L_0x0178:
            r13 = r16
            int r14 = r14 + 1
            r6 = r18
            goto L_0x0152
        L_0x017f:
            r18 = r6
            if (r13 != 0) goto L_0x0186
            r6 = r18
            goto L_0x0139
        L_0x0186:
            int r6 = r0 + 1
            if (r5 == 0) goto L_0x018f
            java.lang.String r0 = "========================================\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
        L_0x018f:
            r4.append(r12)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = " "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = r11.tag     // Catch:{ all -> 0x0407 }
            if (r0 != 0) goto L_0x019e
            java.lang.String r0 = "(no tag)"
            goto L_0x01a0
        L_0x019e:
            java.lang.String r0 = r11.tag     // Catch:{ all -> 0x0407 }
        L_0x01a0:
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.io.File r0 = r1.mDropBoxDir     // Catch:{ all -> 0x0407 }
            java.io.File r0 = r11.getFile(r0)     // Catch:{ all -> 0x0407 }
            r14 = r0
            if (r14 != 0) goto L_0x01bc
            java.lang.String r0 = " (no file)\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            r25 = r6
            r15 = r7
            r27 = r8
            r29 = r9
            r16 = r10
            goto L_0x03b2
        L_0x01bc:
            int r0 = r11.flags     // Catch:{ all -> 0x0407 }
            r0 = r0 & 1
            if (r0 == 0) goto L_0x01d2
            java.lang.String r0 = " (contents lost)\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            r25 = r6
            r15 = r7
            r27 = r8
            r29 = r9
            r16 = r10
            goto L_0x03b2
        L_0x01d2:
            java.lang.String r0 = " ("
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            int r0 = r11.flags     // Catch:{ all -> 0x0407 }
            r0 = r0 & 4
            if (r0 == 0) goto L_0x01e2
            java.lang.String r0 = "compressed "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
        L_0x01e2:
            int r0 = r11.flags     // Catch:{ all -> 0x0407 }
            r0 = r0 & 2
            if (r0 == 0) goto L_0x01ec
            java.lang.String r0 = "text"
            goto L_0x01ee
        L_0x01ec:
            java.lang.String r0 = "data"
        L_0x01ee:
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = ", "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            r25 = r6
            r15 = r7
            long r6 = r14.length()     // Catch:{ all -> 0x0407 }
            r4.append(r6)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = " bytes)\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            if (r8 != 0) goto L_0x020f
            if (r5 == 0) goto L_0x0222
            int r0 = r11.flags     // Catch:{ all -> 0x0407 }
            r0 = r0 & 2
            if (r0 != 0) goto L_0x0222
        L_0x020f:
            if (r5 != 0) goto L_0x0216
            java.lang.String r0 = "    "
            r4.append(r0)     // Catch:{ all -> 0x0407 }
        L_0x0216:
            java.lang.String r0 = r14.getPath()     // Catch:{ all -> 0x0407 }
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r0 = "\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
        L_0x0222:
            int r0 = r11.flags     // Catch:{ all -> 0x0407 }
            r0 = r0 & 2
            if (r0 == 0) goto L_0x03a4
            if (r5 != 0) goto L_0x0235
            if (r8 != 0) goto L_0x022d
            goto L_0x0235
        L_0x022d:
            r27 = r8
            r29 = r9
            r16 = r10
            goto L_0x03aa
        L_0x0235:
            r6 = 0
            r7 = 0
            android.os.DropBoxManager$Entry r0 = new android.os.DropBoxManager$Entry     // Catch:{ IOException -> 0x0351, all -> 0x0346 }
            r26 = r6
            java.lang.String r6 = r11.tag     // Catch:{ IOException -> 0x033c, all -> 0x0333 }
            r28 = r7
            r27 = r8
            long r7 = r11.timestampMillis     // Catch:{ IOException -> 0x032b, all -> 0x0325 }
            r29 = r9
            int r9 = r11.flags     // Catch:{ IOException -> 0x031f, all -> 0x031b }
            r19 = r0
            r20 = r6
            r21 = r7
            r23 = r14
            r24 = r9
            r19.<init>(r20, r21, r23, r24)     // Catch:{ IOException -> 0x031f, all -> 0x031b }
            r6 = r0
            if (r5 == 0) goto L_0x02c4
            java.io.InputStreamReader r7 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            java.io.InputStream r8 = r6.getInputStream()     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            r7.<init>(r8)     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            r8 = 4096(0x1000, float:5.74E-42)
            char[] r8 = new char[r8]     // Catch:{ IOException -> 0x02bf, all -> 0x02b5 }
            r9 = 0
            r19 = r9
        L_0x0267:
            int r9 = r7.read(r8)     // Catch:{ IOException -> 0x02bf, all -> 0x02b5 }
            if (r9 > 0) goto L_0x027c
            if (r19 != 0) goto L_0x0278
            java.lang.String r0 = "\n"
            r4.append(r0)     // Catch:{ IOException -> 0x0275 }
            goto L_0x0278
        L_0x0275:
            r0 = move-exception
            goto L_0x035a
        L_0x0278:
            r28 = r7
            goto L_0x02ff
        L_0x027c:
            r0 = 0
            r4.append(r8, r0, r9)     // Catch:{ IOException -> 0x02bf, all -> 0x02b5 }
            int r0 = r9 + -1
            char r0 = r8[r0]     // Catch:{ IOException -> 0x02bf, all -> 0x02b5 }
            r21 = r7
            r7 = 10
            if (r0 != r7) goto L_0x028d
            r0 = r16
            goto L_0x028e
        L_0x028d:
            r0 = 0
        L_0x028e:
            r19 = r0
            int r0 = r4.length()     // Catch:{ IOException -> 0x02b0, all -> 0x02a8 }
            r7 = 65536(0x10000, float:9.18355E-41)
            if (r0 <= r7) goto L_0x02a4
            java.lang.String r0 = r4.toString()     // Catch:{ IOException -> 0x02b0, all -> 0x02a8 }
            r2.write(r0)     // Catch:{ IOException -> 0x02b0, all -> 0x02a8 }
            r0 = 0
            r4.setLength(r0)     // Catch:{ IOException -> 0x02b0, all -> 0x02a8 }
            goto L_0x02a5
        L_0x02a4:
            r0 = 0
        L_0x02a5:
            r7 = r21
            goto L_0x0267
        L_0x02a8:
            r0 = move-exception
            r26 = r6
            r28 = r21
            r6 = r0
            goto L_0x0397
        L_0x02b0:
            r0 = move-exception
            r7 = r21
            goto L_0x035a
        L_0x02b5:
            r0 = move-exception
            r21 = r7
            r26 = r6
            r28 = r21
            r6 = r0
            goto L_0x0397
        L_0x02bf:
            r0 = move-exception
            r21 = r7
            goto L_0x035a
        L_0x02c4:
            r0 = 0
            r7 = 70
            java.lang.String r8 = r6.getText(r7)     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            java.lang.String r9 = "    "
            r4.append(r9)     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            if (r8 != 0) goto L_0x02da
            java.lang.String r0 = "[null]"
            r4.append(r0)     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            r16 = r8
            goto L_0x02fa
        L_0x02da:
            int r9 = r8.length()     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            if (r9 != r7) goto L_0x02e2
            r0 = r16
        L_0x02e2:
            java.lang.String r7 = r8.trim()     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            r9 = 47
            r16 = r8
            r8 = 10
            java.lang.String r7 = r7.replace(r8, r9)     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            r4.append(r7)     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
            if (r0 == 0) goto L_0x02fa
            java.lang.String r7 = " ..."
            r4.append(r7)     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
        L_0x02fa:
            java.lang.String r0 = "\n"
            r4.append(r0)     // Catch:{ IOException -> 0x0317, all -> 0x0311 }
        L_0x02ff:
            r6.close()     // Catch:{ all -> 0x0407 }
            if (r28 == 0) goto L_0x030d
            r28.close()     // Catch:{ IOException -> 0x030b }
        L_0x0307:
            r16 = r10
            goto L_0x03aa
        L_0x030b:
            r0 = move-exception
            goto L_0x0307
        L_0x030d:
            r16 = r10
            goto L_0x03aa
        L_0x0311:
            r0 = move-exception
            r26 = r6
            r6 = r0
            goto L_0x0397
        L_0x0317:
            r0 = move-exception
            r7 = r28
            goto L_0x035a
        L_0x031b:
            r0 = move-exception
            r6 = r0
            goto L_0x0397
        L_0x031f:
            r0 = move-exception
            r6 = r26
            r7 = r28
            goto L_0x035a
        L_0x0325:
            r0 = move-exception
            r29 = r9
            r6 = r0
            goto L_0x0397
        L_0x032b:
            r0 = move-exception
            r29 = r9
            r6 = r26
            r7 = r28
            goto L_0x035a
        L_0x0333:
            r0 = move-exception
            r28 = r7
            r27 = r8
            r29 = r9
            r6 = r0
            goto L_0x0397
        L_0x033c:
            r0 = move-exception
            r28 = r7
            r27 = r8
            r29 = r9
            r6 = r26
            goto L_0x035a
        L_0x0346:
            r0 = move-exception
            r26 = r6
            r28 = r7
            r27 = r8
            r29 = r9
            r6 = r0
            goto L_0x0397
        L_0x0351:
            r0 = move-exception
            r26 = r6
            r28 = r7
            r27 = r8
            r29 = r9
        L_0x035a:
            java.lang.String r8 = "*** "
            r4.append(r8)     // Catch:{ all -> 0x0391 }
            java.lang.String r8 = r0.toString()     // Catch:{ all -> 0x0391 }
            r4.append(r8)     // Catch:{ all -> 0x0391 }
            java.lang.String r8 = "\n"
            r4.append(r8)     // Catch:{ all -> 0x0391 }
            java.lang.String r8 = "DropBoxManagerService"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0391 }
            r9.<init>()     // Catch:{ all -> 0x0391 }
            r16 = r10
            java.lang.String r10 = "Can't read: "
            r9.append(r10)     // Catch:{ all -> 0x0391 }
            r9.append(r14)     // Catch:{ all -> 0x0391 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0391 }
            android.util.Slog.e(r8, r9, r0)     // Catch:{ all -> 0x0391 }
            if (r6 == 0) goto L_0x0389
            r6.close()     // Catch:{ all -> 0x0407 }
        L_0x0389:
            if (r7 == 0) goto L_0x03aa
            r7.close()     // Catch:{ IOException -> 0x038f }
        L_0x038e:
            goto L_0x03aa
        L_0x038f:
            r0 = move-exception
            goto L_0x038e
        L_0x0391:
            r0 = move-exception
            r26 = r6
            r28 = r7
            r6 = r0
        L_0x0397:
            if (r26 == 0) goto L_0x039c
            r26.close()     // Catch:{ all -> 0x0407 }
        L_0x039c:
            if (r28 == 0) goto L_0x03a3
            r28.close()     // Catch:{ IOException -> 0x03a2 }
            goto L_0x03a3
        L_0x03a2:
            r0 = move-exception
        L_0x03a3:
            throw r6     // Catch:{ all -> 0x0407 }
        L_0x03a4:
            r27 = r8
            r29 = r9
            r16 = r10
        L_0x03aa:
            if (r5 == 0) goto L_0x03b1
            java.lang.String r0 = "\n"
            r4.append(r0)     // Catch:{ all -> 0x0407 }
        L_0x03b1:
        L_0x03b2:
            r7 = r15
            r10 = r16
            r6 = r18
            r0 = r25
            r8 = r27
            r9 = r29
            goto L_0x0139
        L_0x03bf:
            r18 = r6
            r15 = r7
            r27 = r8
            r29 = r9
            if (r0 != 0) goto L_0x03cd
            java.lang.String r6 = "(No entries found.)\n"
            r4.append(r6)     // Catch:{ all -> 0x0407 }
        L_0x03cd:
            if (r3 == 0) goto L_0x03d2
            int r6 = r3.length     // Catch:{ all -> 0x0407 }
            if (r6 != 0) goto L_0x03de
        L_0x03d2:
            if (r5 != 0) goto L_0x03d9
            java.lang.String r6 = "\n"
            r4.append(r6)     // Catch:{ all -> 0x0407 }
        L_0x03d9:
            java.lang.String r6 = "Usage: dumpsys dropbox [--print|--file] [YYYY-mm-dd] [HH:MM:SS] [tag]\n"
            r4.append(r6)     // Catch:{ all -> 0x0407 }
        L_0x03de:
            java.lang.String r6 = r4.toString()     // Catch:{ all -> 0x0407 }
            r2.write(r6)     // Catch:{ all -> 0x0407 }
            monitor-exit(r30)
            return
        L_0x03e7:
            r0 = move-exception
            r4 = r0
            r0 = r4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0407 }
            r4.<init>()     // Catch:{ all -> 0x0407 }
            java.lang.String r5 = "Can't initialize: "
            r4.append(r5)     // Catch:{ all -> 0x0407 }
            r4.append(r0)     // Catch:{ all -> 0x0407 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0407 }
            r2.println(r4)     // Catch:{ all -> 0x0407 }
            java.lang.String r4 = "DropBoxManagerService"
            java.lang.String r5 = "Can't init"
            android.util.Slog.e(r4, r5, r0)     // Catch:{ all -> 0x0407 }
            monitor-exit(r30)
            return
        L_0x0407:
            r0 = move-exception
            monitor-exit(r30)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DropBoxManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    private static final class FileList implements Comparable<FileList> {
        public int blocks;
        public final TreeSet<EntryFile> contents;

        private FileList() {
            this.blocks = 0;
            this.contents = new TreeSet<>();
        }

        public final int compareTo(FileList o) {
            int i = this.blocks;
            int i2 = o.blocks;
            if (i != i2) {
                return i2 - i;
            }
            if (this == o) {
                return 0;
            }
            if (hashCode() < o.hashCode()) {
                return -1;
            }
            if (hashCode() > o.hashCode()) {
                return 1;
            }
            return 0;
        }
    }

    @VisibleForTesting
    static final class EntryFile implements Comparable<EntryFile> {
        public final int blocks;
        public final int flags;
        public final String tag;
        public final long timestampMillis;

        public final int compareTo(EntryFile o) {
            int comp = Long.compare(this.timestampMillis, o.timestampMillis);
            if (comp != 0) {
                return comp;
            }
            int comp2 = ObjectUtils.compare(this.tag, o.tag);
            if (comp2 != 0) {
                return comp2;
            }
            int comp3 = Integer.compare(this.flags, o.flags);
            if (comp3 != 0) {
                return comp3;
            }
            return Integer.compare(hashCode(), o.hashCode());
        }

        public EntryFile(File temp, File dir, String tag2, long timestampMillis2, int flags2, int blockSize) throws IOException {
            if ((flags2 & 1) == 0) {
                this.tag = TextUtils.safeIntern(tag2);
                this.timestampMillis = timestampMillis2;
                this.flags = flags2;
                File file = getFile(dir);
                if (temp.renameTo(file)) {
                    this.blocks = (int) (((file.length() + ((long) blockSize)) - 1) / ((long) blockSize));
                    return;
                }
                throw new IOException("Can't rename " + temp + " to " + file);
            }
            throw new IllegalArgumentException();
        }

        public EntryFile(File dir, String tag2, long timestampMillis2) throws IOException {
            this.tag = TextUtils.safeIntern(tag2);
            this.timestampMillis = timestampMillis2;
            this.flags = 1;
            this.blocks = 0;
            new FileOutputStream(getFile(dir)).close();
        }

        public EntryFile(File file, int blockSize) {
            boolean parseFailure = false;
            String name = file.getName();
            int flags2 = 0;
            String tag2 = null;
            long millis = 0;
            int at = name.lastIndexOf(64);
            if (at < 0) {
                parseFailure = true;
            } else {
                tag2 = Uri.decode(name.substring(0, at));
                if (name.endsWith(PackageManagerService.COMPRESSED_EXTENSION)) {
                    flags2 = 0 | 4;
                    name = name.substring(0, name.length() - 3);
                }
                if (name.endsWith(".lost")) {
                    flags2 |= 1;
                    name = name.substring(at + 1, name.length() - 5);
                } else if (name.endsWith(".txt")) {
                    flags2 |= 2;
                    name = name.substring(at + 1, name.length() - 4);
                } else if (name.endsWith(".dat")) {
                    name = name.substring(at + 1, name.length() - 4);
                } else {
                    parseFailure = true;
                }
                if (!parseFailure) {
                    try {
                        millis = Long.parseLong(name);
                    } catch (NumberFormatException e) {
                        parseFailure = true;
                    }
                }
            }
            if (parseFailure) {
                Slog.wtf(DropBoxManagerService.TAG, "Invalid filename: " + file);
                file.delete();
                this.tag = null;
                this.flags = 1;
                this.timestampMillis = 0;
                this.blocks = 0;
                return;
            }
            this.blocks = (int) (((file.length() + ((long) blockSize)) - 1) / ((long) blockSize));
            this.tag = TextUtils.safeIntern(tag2);
            this.flags = flags2;
            this.timestampMillis = millis;
        }

        public EntryFile(long millis) {
            this.tag = null;
            this.timestampMillis = millis;
            this.flags = 1;
            this.blocks = 0;
        }

        public boolean hasFile() {
            return this.tag != null;
        }

        private String getExtension() {
            if ((this.flags & 1) != 0) {
                return ".lost";
            }
            StringBuilder sb = new StringBuilder();
            sb.append((this.flags & 2) != 0 ? ".txt" : ".dat");
            sb.append((this.flags & 4) != 0 ? PackageManagerService.COMPRESSED_EXTENSION : "");
            return sb.toString();
        }

        public String getFilename() {
            if (!hasFile()) {
                return null;
            }
            return Uri.encode(this.tag) + "@" + this.timestampMillis + getExtension();
        }

        public File getFile(File dir) {
            if (hasFile()) {
                return new File(dir, getFilename());
            }
            return null;
        }

        public void deleteFile(File dir) {
            if (hasFile()) {
                getFile(dir).delete();
            }
        }
    }

    /* access modifiers changed from: private */
    public synchronized void init() throws IOException {
        if (this.mStatFs == null) {
            if (!this.mDropBoxDir.isDirectory()) {
                if (!this.mDropBoxDir.mkdirs()) {
                    throw new IOException("Can't mkdir: " + this.mDropBoxDir);
                }
            }
            try {
                this.mStatFs = new StatFs(this.mDropBoxDir.getPath());
                this.mBlockSize = this.mStatFs.getBlockSize();
            } catch (IllegalArgumentException e) {
                throw new IOException("Can't statfs: " + this.mDropBoxDir);
            }
        }
        if (this.mAllFiles == null) {
            File[] files = this.mDropBoxDir.listFiles();
            if (files != null) {
                this.mAllFiles = new FileList();
                this.mFilesByTag = new ArrayMap<>();
                for (File file : files) {
                    if (file.getName().endsWith(".tmp")) {
                        Slog.i(TAG, "Cleaning temp file: " + file);
                        file.delete();
                    } else {
                        EntryFile entry = new EntryFile(file, this.mBlockSize);
                        if (entry.hasFile()) {
                            enrollEntry(entry);
                        }
                    }
                }
            } else {
                throw new IOException("Can't list files: " + this.mDropBoxDir);
            }
        }
    }

    private synchronized void enrollEntry(EntryFile entry) {
        this.mAllFiles.contents.add(entry);
        this.mAllFiles.blocks += entry.blocks;
        if (entry.hasFile() && entry.blocks > 0) {
            FileList tagFiles = this.mFilesByTag.get(entry.tag);
            if (tagFiles == null) {
                tagFiles = new FileList();
                this.mFilesByTag.put(TextUtils.safeIntern(entry.tag), tagFiles);
            }
            tagFiles.contents.add(entry);
            tagFiles.blocks += entry.blocks;
        }
    }

    private synchronized long createEntry(File temp, String tag, int flags) throws IOException {
        long t;
        SortedSet<EntryFile> tail;
        long j;
        synchronized (this) {
            long t2 = System.currentTimeMillis();
            SortedSet<EntryFile> tail2 = this.mAllFiles.contents.tailSet(new EntryFile(JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY + t2));
            EntryFile[] future = null;
            if (!tail2.isEmpty()) {
                future = (EntryFile[]) tail2.toArray(new EntryFile[tail2.size()]);
                tail2.clear();
            }
            long j2 = 1;
            if (!this.mAllFiles.contents.isEmpty()) {
                t2 = Math.max(t2, this.mAllFiles.contents.last().timestampMillis + 1);
            }
            if (future != null) {
                int length = future.length;
                int i = 0;
                t = t2;
                while (i < length) {
                    EntryFile late = future[i];
                    this.mAllFiles.blocks -= late.blocks;
                    FileList tagFiles = this.mFilesByTag.get(late.tag);
                    if (tagFiles != null && tagFiles.contents.remove(late)) {
                        tagFiles.blocks -= late.blocks;
                    }
                    if ((late.flags & 1) == 0) {
                        tail = tail2;
                        EntryFile entryFile = r9;
                        EntryFile entryFile2 = new EntryFile(late.getFile(this.mDropBoxDir), this.mDropBoxDir, late.tag, t, late.flags, this.mBlockSize);
                        enrollEntry(entryFile);
                        t += j2;
                        j = 1;
                    } else {
                        tail = tail2;
                        j = 1;
                        enrollEntry(new EntryFile(this.mDropBoxDir, late.tag, t));
                        t++;
                    }
                    i++;
                    j2 = j;
                    tail2 = tail;
                }
            } else {
                t = t2;
            }
            if (temp == null) {
                enrollEntry(new EntryFile(this.mDropBoxDir, tag, t));
            } else {
                String str = tag;
                enrollEntry(new EntryFile(temp, this.mDropBoxDir, tag, t, flags, this.mBlockSize));
            }
        }
        return t;
    }

    /* access modifiers changed from: private */
    public synchronized long trimToFit() throws IOException {
        long j;
        long cutoffMillis;
        int ageSeconds;
        synchronized (this) {
            int ageSeconds2 = Settings.Global.getInt(this.mContentResolver, "dropbox_age_seconds", DEFAULT_AGE_SECONDS);
            this.mMaxFiles = Settings.Global.getInt(this.mContentResolver, "dropbox_max_files", ActivityManager.isLowRamDeviceStatic() ? 300 : 1000);
            long cutoffMillis2 = System.currentTimeMillis() - ((long) (ageSeconds2 * 1000));
            while (true) {
                if (this.mAllFiles.contents.isEmpty()) {
                    break;
                }
                EntryFile entry = this.mAllFiles.contents.first();
                if (entry.timestampMillis > cutoffMillis2 && this.mAllFiles.contents.size() < this.mMaxFiles) {
                    break;
                }
                FileList tag = this.mFilesByTag.get(entry.tag);
                if (tag != null && tag.contents.remove(entry)) {
                    tag.blocks -= entry.blocks;
                }
                if (this.mAllFiles.contents.remove(entry)) {
                    this.mAllFiles.blocks -= entry.blocks;
                }
                entry.deleteFile(this.mDropBoxDir);
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            if (uptimeMillis > this.mCachedQuotaUptimeMillis + 5000) {
                int quotaPercent = Settings.Global.getInt(this.mContentResolver, "dropbox_quota_percent", 10);
                int reservePercent = Settings.Global.getInt(this.mContentResolver, "dropbox_reserve_percent", 10);
                int quotaKb = Settings.Global.getInt(this.mContentResolver, "dropbox_quota_kb", DEFAULT_QUOTA_KB);
                try {
                    this.mStatFs.restat(this.mDropBoxDir.getPath());
                    this.mCachedQuotaBlocks = Math.min((quotaKb * 1024) / this.mBlockSize, Math.max(0, ((this.mStatFs.getAvailableBlocks() - ((this.mStatFs.getBlockCount() * reservePercent) / 100)) * quotaPercent) / 100));
                    this.mCachedQuotaUptimeMillis = uptimeMillis;
                } catch (IllegalArgumentException e) {
                    throw new IOException("Can't restat: " + this.mDropBoxDir);
                }
            }
            if (this.mAllFiles.blocks > this.mCachedQuotaBlocks) {
                int unsqueezed = this.mAllFiles.blocks;
                int squeezed = 0;
                TreeSet<FileList> tags = new TreeSet<>(this.mFilesByTag.values());
                Iterator<FileList> it = tags.iterator();
                int unsqueezed2 = unsqueezed;
                while (true) {
                    if (it.hasNext() == 0) {
                        break;
                    }
                    FileList tag2 = it.next();
                    if (squeezed > 0 && tag2.blocks <= (this.mCachedQuotaBlocks - unsqueezed2) / squeezed) {
                        break;
                    }
                    unsqueezed2 -= tag2.blocks;
                    squeezed++;
                }
                int tagQuota = (this.mCachedQuotaBlocks - unsqueezed2) / squeezed;
                Iterator<FileList> it2 = tags.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        long j2 = cutoffMillis2;
                        break;
                    }
                    FileList tag3 = it2.next();
                    if (this.mAllFiles.blocks < this.mCachedQuotaBlocks) {
                        int i = ageSeconds2;
                        long j3 = cutoffMillis2;
                        break;
                    }
                    while (tag3.blocks > tagQuota && !tag3.contents.isEmpty()) {
                        EntryFile entry2 = tag3.contents.first();
                        if (tag3.contents.remove(entry2)) {
                            tag3.blocks -= entry2.blocks;
                        }
                        if (this.mAllFiles.contents.remove(entry2)) {
                            this.mAllFiles.blocks -= entry2.blocks;
                        }
                        try {
                            entry2.deleteFile(this.mDropBoxDir);
                            ageSeconds = ageSeconds2;
                            cutoffMillis = cutoffMillis2;
                            try {
                                enrollEntry(new EntryFile(this.mDropBoxDir, entry2.tag, entry2.timestampMillis));
                            } catch (IOException e2) {
                                e = e2;
                            }
                        } catch (IOException e3) {
                            e = e3;
                            ageSeconds = ageSeconds2;
                            cutoffMillis = cutoffMillis2;
                            Slog.e(TAG, "Can't write tombstone file", e);
                            ageSeconds2 = ageSeconds;
                            cutoffMillis2 = cutoffMillis;
                        }
                        ageSeconds2 = ageSeconds;
                        cutoffMillis2 = cutoffMillis;
                    }
                    ageSeconds2 = ageSeconds2;
                    cutoffMillis2 = cutoffMillis2;
                }
            } else {
                long j4 = cutoffMillis2;
            }
            j = (long) (this.mCachedQuotaBlocks * this.mBlockSize);
        }
        return j;
    }

    private void getLowPriorityResourceConfigs() {
        this.mLowPriorityRateLimitPeriod = (long) Resources.getSystem().getInteger(17694805);
        if (size == 0) {
            this.mLowPriorityTags = null;
            return;
        }
        this.mLowPriorityTags = new ArraySet<>(size);
        for (String add : Resources.getSystem().getStringArray(17236020)) {
            this.mLowPriorityTags.add(add);
        }
    }
}
