package miui.cloud.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.SystemClock;
import com.miui.activityutil.h;
import com.miui.gamebooster.m.W;
import com.miui.permcenter.permissions.D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import miui.cloud.common.XLogger;
import miui.cloud.os.MultiuserUtils;

public class SwitchFileLogSender implements XLogger.LogSender {
    private static final String LOG_SUFFIX = ".log";
    private static final String PACKAGE_LOG_PATH = "MIUI/debug_log/%s";
    private final Context mAppContext;
    private long mFailOpenTime;
    private boolean mInit;
    private int mMaxFileCount;
    private int mMaxFileSizeInByte;
    private FileObserver mMonitor;
    private FileOutputStream mOutputStream;
    private XLogger.LogSender mParentLogSender;
    private PathProvider mPathProvider;
    private boolean mShutdown;
    private long mSizeUsed;

    public interface PathProvider {
        File getPath(Context context);
    }

    public SwitchFileLogSender(Context context, final String str, int i, int i2, XLogger.LogSender logSender) {
        this(context, (PathProvider) new PathProvider() {
            public File getPath(Context context) {
                return new File(Environment.getExternalStorageDirectory(), String.format(SwitchFileLogSender.PACKAGE_LOG_PATH, new Object[]{str}));
            }
        }, i, i2, logSender);
    }

    public SwitchFileLogSender(Context context, PathProvider pathProvider, int i, int i2, XLogger.LogSender logSender) {
        if (context.getApplicationContext() != context) {
            throw new IllegalArgumentException("appContext is not the application context. ");
        } else if (pathProvider == null) {
            throw new IllegalArgumentException("pathProvider should not be null.");
        } else if (i <= 0) {
            throw new IllegalArgumentException("maxFileSizeInByte should >0. ");
        } else if (i2 > 1) {
            this.mParentLogSender = logSender;
            this.mMaxFileSizeInByte = i;
            this.mMaxFileCount = i2;
            this.mPathProvider = pathProvider;
            this.mAppContext = context;
            this.mInit = false;
        } else {
            throw new IllegalArgumentException("maxFileCount should >1. ");
        }
    }

    private void closeLogFileLocked() {
        this.mMonitor.stopWatching();
        this.mMonitor = null;
        IOUtil.closeQuietly(this.mOutputStream);
        this.mOutputStream = null;
        this.mSizeUsed = 0;
        this.mFailOpenTime = 0;
    }

    private void init() {
        if (!this.mInit) {
            this.mInit = true;
            registerShutdownListener(this.mAppContext);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0049, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onBaseDirChanged() {
        /*
            r8 = this;
            monitor-enter(r8)
            java.io.FileOutputStream r0 = r8.mOutputStream     // Catch:{ all -> 0x004a }
            if (r0 != 0) goto L_0x0007
            monitor-exit(r8)
            return
        L_0x0007:
            r0 = 0
            java.io.FileOutputStream r1 = r8.mOutputStream     // Catch:{ IOException -> 0x000f }
            java.io.FileDescriptor r1 = r1.getFD()     // Catch:{ IOException -> 0x000f }
            goto L_0x0010
        L_0x000f:
            r1 = r0
        L_0x0010:
            if (r1 != 0) goto L_0x0014
            monitor-exit(r8)
            return
        L_0x0014:
            r2 = 0
            android.system.StructStat r0 = android.system.Os.fstat(r1)     // Catch:{ ErrnoException -> 0x001b }
            r1 = r2
            goto L_0x001e
        L_0x001b:
            r1 = move-exception
            int r1 = r1.errno     // Catch:{ all -> 0x004a }
        L_0x001e:
            r3 = 1
            if (r0 == 0) goto L_0x002a
            long r4 = r0.st_nlink     // Catch:{ all -> 0x004a }
            r6 = 0
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 > 0) goto L_0x002a
            r2 = r3
        L_0x002a:
            int r0 = android.system.OsConstants.ENOENT     // Catch:{ all -> 0x004a }
            if (r1 != r0) goto L_0x002f
            r2 = r3
        L_0x002f:
            if (r2 == 0) goto L_0x0048
            miui.cloud.common.XLogger$LogSender r0 = r8.mParentLogSender     // Catch:{ all -> 0x004a }
            if (r0 == 0) goto L_0x0045
            miui.cloud.common.XLogger$LogSender r0 = r8.mParentLogSender     // Catch:{ all -> 0x004a }
            r1 = 5
            java.lang.Class r2 = r8.getClass()     // Catch:{ all -> 0x004a }
            java.lang.String r2 = r2.getName()     // Catch:{ all -> 0x004a }
            java.lang.String r3 = "File unlinked. "
            r0.sendLog(r1, r2, r3)     // Catch:{ all -> 0x004a }
        L_0x0045:
            r8.closeLogFileLocked()     // Catch:{ all -> 0x004a }
        L_0x0048:
            monitor-exit(r8)
            return
        L_0x004a:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.cloud.common.SwitchFileLogSender.onBaseDirChanged():void");
    }

    /* access modifiers changed from: private */
    public synchronized void onShutDown() {
        sendLogLocked(6, getClass().getName(), "========================== shut down ========================== ");
        this.mShutdown = true;
        if (this.mOutputStream != null) {
            closeLogFileLocked();
        }
    }

    private void prepareLogFileLocked() {
        File file;
        boolean z;
        File file2;
        if (this.mOutputStream == null) {
            if (this.mFailOpenTime <= 0 || Math.abs(SystemClock.elapsedRealtime() - this.mFailOpenTime) >= 180000) {
                try {
                    File path = this.mPathProvider.getPath(this.mAppContext);
                    if (path == null) {
                        if (this.mParentLogSender != null) {
                            this.mParentLogSender.sendLog(6, getClass().getName(), "Failed to get base log path. Abort. ");
                        }
                        if (this.mOutputStream == null) {
                            this.mFailOpenTime = SystemClock.elapsedRealtime();
                        } else {
                            this.mFailOpenTime = 0;
                        }
                    } else {
                        File file3 = new File(path, "" + MultiuserUtils.myUserId());
                        if (!file3.isDirectory()) {
                            file3.mkdirs();
                        }
                        if (!file3.isDirectory()) {
                            if (this.mParentLogSender != null) {
                                this.mParentLogSender.sendLog(6, getClass().getName(), String.format("Failed to create folder %s. ", new Object[]{file3.getAbsolutePath()}));
                            }
                            if (this.mOutputStream == null) {
                                this.mFailOpenTime = SystemClock.elapsedRealtime();
                            } else {
                                this.mFailOpenTime = 0;
                            }
                        } else {
                            String externalStorageState = Environment.getExternalStorageState(file3);
                            if (h.f2289a.equals(externalStorageState) || "mounted".equals(externalStorageState)) {
                                int i = 0;
                                while (true) {
                                    file = null;
                                    if (i >= this.mMaxFileCount) {
                                        break;
                                    }
                                    file = new File(file3, i + LOG_SUFFIX);
                                    if (!file.exists()) {
                                        break;
                                    } else if (file.length() < ((long) this.mMaxFileSizeInByte)) {
                                        break;
                                    } else {
                                        i++;
                                    }
                                }
                                if (file == null) {
                                    long lastModified = new File(file3, "0.log").lastModified();
                                    long j = 0;
                                    for (int i2 = 1; i2 < this.mMaxFileCount; i2++) {
                                        long lastModified2 = new File(file3, i2 + LOG_SUFFIX).lastModified();
                                        if (lastModified2 < lastModified) {
                                            j = (long) i2;
                                            lastModified = lastModified2;
                                        }
                                    }
                                    file2 = new File(file3, j + LOG_SUFFIX);
                                    z = false;
                                } else {
                                    file2 = file;
                                    z = true;
                                }
                                this.mOutputStream = new FileOutputStream(file2, z);
                                this.mSizeUsed = z ? file2.length() : 0;
                                this.mMonitor = new FileObserver(file3.getPath(), 1536) {
                                    public void onEvent(int i, String str) {
                                        SwitchFileLogSender.this.onBaseDirChanged();
                                    }
                                };
                                this.mMonitor.startWatching();
                                if (this.mOutputStream == null) {
                                    this.mFailOpenTime = SystemClock.elapsedRealtime();
                                } else {
                                    this.mFailOpenTime = 0;
                                }
                            } else {
                                if (this.mParentLogSender != null) {
                                    this.mParentLogSender.sendLog(6, getClass().getName(), "Storage not mounted. ");
                                }
                                if (this.mOutputStream == null) {
                                    this.mFailOpenTime = SystemClock.elapsedRealtime();
                                } else {
                                    this.mFailOpenTime = 0;
                                }
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    if (this.mParentLogSender != null) {
                        this.mParentLogSender.sendLog(6, getClass().getName(), String.format("Failed to switch to file %s, error: %s. ", new Object[]{file2.getAbsolutePath(), e}));
                    }
                    if (this.mOutputStream == null) {
                        this.mFailOpenTime = SystemClock.elapsedRealtime();
                    } else {
                        this.mFailOpenTime = 0;
                    }
                } catch (Throwable th) {
                    if (this.mOutputStream == null) {
                        this.mFailOpenTime = SystemClock.elapsedRealtime();
                    } else {
                        this.mFailOpenTime = 0;
                    }
                    throw th;
                }
            } else {
                XLogger.LogSender logSender = this.mParentLogSender;
                if (logSender != null) {
                    logSender.sendLog(6, SwitchFileLogSender.class.getName(), "Failed to open log file recently. Abort. ");
                }
            }
        }
    }

    private void registerShutdownListener(Context context) {
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                SwitchFileLogSender.this.onShutDown();
            }
        }, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"), (String) null, (Handler) null);
    }

    private void sendLogLocked(int i, String str, String str2) {
        init();
        XLogger.LogSender logSender = this.mParentLogSender;
        if (logSender != null) {
            logSender.sendLog(i, str, str2);
        }
        if (onFilterLogByLevelLocked(i)) {
            if (this.mShutdown) {
                XLogger.LogSender logSender2 = this.mParentLogSender;
                if (logSender2 != null) {
                    logSender2.sendLog(6, SwitchFileLogSender.class.getName(), "Shutdown state. Skip outputing. ");
                    return;
                }
                return;
            }
            prepareLogFileLocked();
            if (this.mOutputStream == null) {
                XLogger.LogSender logSender3 = this.mParentLogSender;
                if (logSender3 != null) {
                    logSender3.sendLog(6, SwitchFileLogSender.class.getName(), "Null output stream. Skip outputing. ");
                    return;
                }
                return;
            }
            byte[] bytes = String.format("LV:%s, TM: %s, TAG: %s, MSG: %s\n", new Object[]{onGetLevelTagLocked(i), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), str, str2}).getBytes();
            this.mSizeUsed += (long) bytes.length;
            try {
                this.mOutputStream.write(bytes);
                this.mOutputStream.flush();
            } catch (IOException e) {
                XLogger.LogSender logSender4 = this.mParentLogSender;
                if (logSender4 != null) {
                    logSender4.sendLog(6, SwitchFileLogSender.class.getName(), String.format("Failed to output log, IOException: %s", new Object[]{e}));
                }
            }
            if (this.mSizeUsed >= ((long) this.mMaxFileSizeInByte)) {
                closeLogFileLocked();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onFilterLogByLevelLocked(int i) {
        return true;
    }

    /* access modifiers changed from: protected */
    public String onGetLevelTagLocked(int i) {
        switch (i) {
            case 2:
                return "V";
            case 3:
                return D.f6221a;
            case 4:
                return "I";
            case 5:
                return W.f4461a;
            case 6:
                return "E";
            case 7:
                return "A";
            default:
                return String.valueOf(i);
        }
    }

    public synchronized void sendLog(int i, String str, String str2) {
        sendLogLocked(i, str, str2);
    }
}
