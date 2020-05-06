package com.android.server.content;

import android.accounts.Account;
import android.app.job.JobParameters;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemProperties;
import android.server.am.SplitScreenReporter;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IntPair;
import com.android.server.IoThread;
import com.android.server.content.SyncManager;
import com.android.server.content.SyncStorageEngine;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;

public class SyncLogger {
    public static final int CALLING_UID_SELF = -1;
    private static final String TAG = "SyncLogger";
    private static SyncLogger sInstance;

    SyncLogger() {
    }

    public static synchronized SyncLogger getInstance() {
        SyncLogger syncLogger;
        synchronized (SyncLogger.class) {
            if (sInstance == null) {
                String flag = SystemProperties.get("debug.synclog");
                if ((Build.IS_DEBUGGABLE || SplitScreenReporter.ACTION_ENTER_SPLIT.equals(flag) || Log.isLoggable(TAG, 2)) && !"0".equals(flag)) {
                    sInstance = new RotatingFileLogger();
                } else {
                    sInstance = new SyncLogger();
                }
            }
            syncLogger = sInstance;
        }
        return syncLogger;
    }

    public void log(Object... message) {
    }

    public void purgeOldLogs() {
    }

    public String jobParametersToString(JobParameters params) {
        return "";
    }

    public void dumpAll(PrintWriter pw) {
    }

    public boolean enabled() {
        return false;
    }

    private static class RotatingFileLogger extends SyncLogger {
        private static final boolean DO_LOGCAT = Log.isLoggable(SyncLogger.TAG, 3);
        private static final SimpleDateFormat sFilenameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private static final SimpleDateFormat sTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        @GuardedBy({"mLock"})
        private final Date mCachedDate = new Date();
        @GuardedBy({"mLock"})
        private long mCurrentLogFileDayTimestamp;
        @GuardedBy({"mLock"})
        private boolean mErrorShown;
        private final MyHandler mHandler = new MyHandler(IoThread.get().getLooper());
        private final long mKeepAgeMs = TimeUnit.DAYS.toMillis(7);
        private final Object mLock = new Object();
        private final File mLogPath = new File(Environment.getDataSystemDirectory(), "syncmanager-log");
        @GuardedBy({"mLock"})
        private Writer mLogWriter;
        @GuardedBy({"mLock"})
        private final StringBuilder mStringBuilder = new StringBuilder();

        RotatingFileLogger() {
        }

        public boolean enabled() {
            return true;
        }

        private void handleException(String message, Exception e) {
            if (!this.mErrorShown) {
                Slog.e(SyncLogger.TAG, message, e);
                this.mErrorShown = true;
            }
        }

        public void log(Object... message) {
            if (message != null) {
                this.mHandler.log(System.currentTimeMillis(), message);
            }
        }

        /* access modifiers changed from: package-private */
        public void logInner(long now, Object[] message) {
            synchronized (this.mLock) {
                openLogLocked(now);
                if (this.mLogWriter != null) {
                    this.mStringBuilder.setLength(0);
                    this.mCachedDate.setTime(now);
                    this.mStringBuilder.append(sTimestampFormat.format(this.mCachedDate));
                    this.mStringBuilder.append(' ');
                    this.mStringBuilder.append(Process.myTid());
                    this.mStringBuilder.append(' ');
                    int messageStart = this.mStringBuilder.length();
                    for (Object o : message) {
                        this.mStringBuilder.append(o);
                    }
                    this.mStringBuilder.append(10);
                    try {
                        this.mLogWriter.append(this.mStringBuilder);
                        this.mLogWriter.flush();
                        if (DO_LOGCAT) {
                            Log.d(SyncLogger.TAG, this.mStringBuilder.substring(messageStart));
                        }
                    } catch (IOException e) {
                        handleException("Failed to write log", e);
                    }
                }
            }
        }

        @GuardedBy({"mLock"})
        private void openLogLocked(long now) {
            long day = now % 86400000;
            if (this.mLogWriter == null || day != this.mCurrentLogFileDayTimestamp) {
                closeCurrentLogLocked();
                this.mCurrentLogFileDayTimestamp = day;
                this.mCachedDate.setTime(now);
                File file = new File(this.mLogPath, "synclog-" + sFilenameDateFormat.format(this.mCachedDate) + ".log");
                file.getParentFile().mkdirs();
                try {
                    this.mLogWriter = new FileWriter(file, true);
                } catch (IOException e) {
                    handleException("Failed to open log file: " + file, e);
                }
            }
        }

        @GuardedBy({"mLock"})
        private void closeCurrentLogLocked() {
            IoUtils.closeQuietly(this.mLogWriter);
            this.mLogWriter = null;
        }

        public void purgeOldLogs() {
            synchronized (this.mLock) {
                FileUtils.deleteOlderFiles(this.mLogPath, 1, this.mKeepAgeMs);
            }
        }

        public String jobParametersToString(JobParameters params) {
            return SyncJobService.jobParametersToString(params);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dumpAll(java.io.PrintWriter r8) {
            /*
                r7 = this;
                java.lang.Object r0 = r7.mLock
                monitor-enter(r0)
                java.io.File r1 = r7.mLogPath     // Catch:{ all -> 0x0029 }
                java.lang.String[] r1 = r1.list()     // Catch:{ all -> 0x0029 }
                if (r1 == 0) goto L_0x0027
                int r2 = r1.length     // Catch:{ all -> 0x0029 }
                if (r2 != 0) goto L_0x000f
                goto L_0x0027
            L_0x000f:
                java.util.Arrays.sort(r1)     // Catch:{ all -> 0x0029 }
                int r2 = r1.length     // Catch:{ all -> 0x0029 }
                r3 = 0
            L_0x0014:
                if (r3 >= r2) goto L_0x0025
                r4 = r1[r3]     // Catch:{ all -> 0x0029 }
                java.io.File r5 = new java.io.File     // Catch:{ all -> 0x0029 }
                java.io.File r6 = r7.mLogPath     // Catch:{ all -> 0x0029 }
                r5.<init>(r6, r4)     // Catch:{ all -> 0x0029 }
                r7.dumpFile(r8, r5)     // Catch:{ all -> 0x0029 }
                int r3 = r3 + 1
                goto L_0x0014
            L_0x0025:
                monitor-exit(r0)     // Catch:{ all -> 0x0029 }
                return
            L_0x0027:
                monitor-exit(r0)     // Catch:{ all -> 0x0029 }
                return
            L_0x0029:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0029 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncLogger.RotatingFileLogger.dumpAll(java.io.PrintWriter):void");
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0039, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
            r1.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0042, code lost:
            throw r3;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void dumpFile(java.io.PrintWriter r6, java.io.File r7) {
            /*
                r5 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "Dumping "
                r0.append(r1)
                r0.append(r7)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "SyncLogger"
                android.util.Slog.w(r1, r0)
                r0 = 32768(0x8000, float:4.5918E-41)
                char[] r0 = new char[r0]
                java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0043 }
                java.io.FileReader r2 = new java.io.FileReader     // Catch:{ IOException -> 0x0043 }
                r2.<init>(r7)     // Catch:{ IOException -> 0x0043 }
                r1.<init>(r2)     // Catch:{ IOException -> 0x0043 }
            L_0x0025:
                int r2 = r1.read(r0)     // Catch:{ all -> 0x0037 }
                r3 = r2
                if (r2 < 0) goto L_0x0033
                if (r3 <= 0) goto L_0x0025
                r2 = 0
                r6.write(r0, r2, r3)     // Catch:{ all -> 0x0037 }
                goto L_0x0025
            L_0x0033:
                r1.close()     // Catch:{ IOException -> 0x0043 }
                goto L_0x0044
            L_0x0037:
                r2 = move-exception
                throw r2     // Catch:{ all -> 0x0039 }
            L_0x0039:
                r3 = move-exception
                r1.close()     // Catch:{ all -> 0x003e }
                goto L_0x0042
            L_0x003e:
                r4 = move-exception
                r2.addSuppressed(r4)     // Catch:{ IOException -> 0x0043 }
            L_0x0042:
                throw r3     // Catch:{ IOException -> 0x0043 }
            L_0x0043:
                r1 = move-exception
            L_0x0044:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncLogger.RotatingFileLogger.dumpFile(java.io.PrintWriter, java.io.File):void");
        }

        private class MyHandler extends Handler {
            public static final int MSG_LOG_ID = 1;

            MyHandler(Looper looper) {
                super(looper);
            }

            public void log(long now, Object[] message) {
                obtainMessage(1, IntPair.first(now), IntPair.second(now), message).sendToTarget();
            }

            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    RotatingFileLogger.this.logInner(IntPair.of(msg.arg1, msg.arg2), (Object[]) msg.obj);
                }
            }
        }
    }

    static String logSafe(Account account) {
        return account == null ? "[null]" : account.toSafeString();
    }

    static String logSafe(SyncStorageEngine.EndPoint endPoint) {
        return endPoint == null ? "[null]" : endPoint.toSafeString();
    }

    static String logSafe(SyncOperation operation) {
        return operation == null ? "[null]" : operation.toSafeString();
    }

    static String logSafe(SyncManager.ActiveSyncContext asc) {
        return asc == null ? "[null]" : asc.toSafeString();
    }
}
