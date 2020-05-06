package com.android.server.power.batterysaver;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import com.android.server.IoThread;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class FileUpdater {
    private static final boolean DEBUG = false;
    private static final String PROP_SKIP_WRITE = "debug.batterysaver.no_write_files";
    private static final String TAG = "BatterySaverController";
    private static final String TAG_DEFAULT_ROOT = "defaults";
    private final int MAX_RETRIES;
    private final long RETRY_INTERVAL_MS;
    private final Context mContext;
    @GuardedBy({"mLock"})
    private final ArrayMap<String, String> mDefaultValues;
    private Runnable mHandleWriteOnHandlerRunnable;
    private final Handler mHandler;
    private final Object mLock;
    @GuardedBy({"mLock"})
    private final ArrayMap<String, String> mPendingWrites;
    @GuardedBy({"mLock"})
    private int mRetries;

    public FileUpdater(Context context) {
        this(context, IoThread.get().getLooper(), 10, 5000);
    }

    @VisibleForTesting
    FileUpdater(Context context, Looper looper, int maxRetries, int retryIntervalMs) {
        this.mLock = new Object();
        this.mPendingWrites = new ArrayMap<>();
        this.mDefaultValues = new ArrayMap<>();
        this.mRetries = 0;
        this.mHandleWriteOnHandlerRunnable = new Runnable() {
            public final void run() {
                FileUpdater.this.lambda$new$0$FileUpdater();
            }
        };
        this.mContext = context;
        this.mHandler = new Handler(looper);
        this.MAX_RETRIES = maxRetries;
        this.RETRY_INTERVAL_MS = (long) retryIntervalMs;
    }

    public void systemReady(boolean runtimeRestarted) {
        synchronized (this.mLock) {
            if (!runtimeRestarted) {
                injectDefaultValuesFilename().delete();
            } else if (loadDefaultValuesLocked()) {
                Slog.d(TAG, "Default values loaded after runtime restart; writing them...");
                restoreDefault();
            }
        }
    }

    public void writeFiles(ArrayMap<String, String> fileValues) {
        synchronized (this.mLock) {
            for (int i = fileValues.size() - 1; i >= 0; i--) {
                this.mPendingWrites.put(fileValues.keyAt(i), fileValues.valueAt(i));
            }
            this.mRetries = 0;
            this.mHandler.removeCallbacks(this.mHandleWriteOnHandlerRunnable);
            this.mHandler.post(this.mHandleWriteOnHandlerRunnable);
        }
    }

    public void restoreDefault() {
        synchronized (this.mLock) {
            this.mPendingWrites.clear();
            writeFiles(this.mDefaultValues);
        }
    }

    private String getKeysString(Map<String, String> source) {
        return new ArrayList(source.keySet()).toString();
    }

    private ArrayMap<String, String> cloneMap(ArrayMap<String, String> source) {
        return new ArrayMap<>(source);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        if (r3 >= r2) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001c, code lost:
        r4 = r1.keyAt(r3);
        r5 = r1.valueAt(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002c, code lost:
        if (ensureDefaultLoaded(r4) != false) goto L_0x002f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        injectWriteToFile(r4, r5);
        removePendingWrite(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0037, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
        if (r0 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
        scheduleRetry();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
        r0 = false;
        r2 = r1.size();
        r3 = 0;
     */
    /* renamed from: handleWriteOnHandler */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$new$0$FileUpdater() {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            android.util.ArrayMap<java.lang.String, java.lang.String> r1 = r7.mPendingWrites     // Catch:{ all -> 0x0041 }
            int r1 = r1.size()     // Catch:{ all -> 0x0041 }
            if (r1 != 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            return
        L_0x000d:
            android.util.ArrayMap<java.lang.String, java.lang.String> r1 = r7.mPendingWrites     // Catch:{ all -> 0x0041 }
            android.util.ArrayMap r1 = r7.cloneMap(r1)     // Catch:{ all -> 0x0041 }
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            r0 = 0
            int r2 = r1.size()
            r3 = 0
        L_0x001a:
            if (r3 >= r2) goto L_0x003b
            java.lang.Object r4 = r1.keyAt(r3)
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Object r5 = r1.valueAt(r3)
            java.lang.String r5 = (java.lang.String) r5
            boolean r6 = r7.ensureDefaultLoaded(r4)
            if (r6 != 0) goto L_0x002f
            goto L_0x0038
        L_0x002f:
            r7.injectWriteToFile(r4, r5)     // Catch:{ IOException -> 0x0036 }
            r7.removePendingWrite(r4)     // Catch:{ IOException -> 0x0036 }
            goto L_0x0038
        L_0x0036:
            r6 = move-exception
            r0 = 1
        L_0x0038:
            int r3 = r3 + 1
            goto L_0x001a
        L_0x003b:
            if (r0 == 0) goto L_0x0040
            r7.scheduleRetry()
        L_0x0040:
            return
        L_0x0041:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.batterysaver.FileUpdater.lambda$new$0$FileUpdater():void");
    }

    private void removePendingWrite(String file) {
        synchronized (this.mLock) {
            this.mPendingWrites.remove(file);
        }
    }

    private void scheduleRetry() {
        synchronized (this.mLock) {
            if (this.mPendingWrites.size() != 0) {
                this.mRetries++;
                if (this.mRetries > this.MAX_RETRIES) {
                    doWtf("Gave up writing files: " + getKeysString(this.mPendingWrites));
                    return;
                }
                this.mHandler.removeCallbacks(this.mHandleWriteOnHandlerRunnable);
                this.mHandler.postDelayed(this.mHandleWriteOnHandlerRunnable, this.RETRY_INTERVAL_MS);
            }
        }
    }

    private boolean ensureDefaultLoaded(String file) {
        synchronized (this.mLock) {
            if (this.mDefaultValues.containsKey(file)) {
                return true;
            }
            try {
                String originalValue = injectReadFromFileTrimmed(file);
                synchronized (this.mLock) {
                    this.mDefaultValues.put(file, originalValue);
                    saveDefaultValuesLocked();
                }
                return true;
            } catch (IOException e) {
                injectWtf("Unable to read from file", e);
                removePendingWrite(file);
                return false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public String injectReadFromFileTrimmed(String file) throws IOException {
        return IoUtils.readFileAsString(file).trim();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0032, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        $closeResource(r2, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0036, code lost:
        throw r3;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void injectWriteToFile(java.lang.String r5, java.lang.String r6) throws java.io.IOException {
        /*
            r4 = this;
            boolean r0 = r4.injectShouldSkipWrite()
            java.lang.String r1 = "BatterySaverController"
            if (r0 == 0) goto L_0x0022
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Skipped writing to '"
            r0.append(r2)
            r0.append(r5)
            java.lang.String r2 = "'"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Slog.i(r1, r0)
            return
        L_0x0022:
            java.io.FileWriter r0 = new java.io.FileWriter     // Catch:{ IOException | RuntimeException -> 0x0037 }
            r0.<init>(r5)     // Catch:{ IOException | RuntimeException -> 0x0037 }
            r2 = 0
            r0.write(r6)     // Catch:{ all -> 0x0030 }
            $closeResource(r2, r0)     // Catch:{ IOException | RuntimeException -> 0x0037 }
            return
        L_0x0030:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0032 }
        L_0x0032:
            r3 = move-exception
            $closeResource(r2, r0)     // Catch:{ IOException | RuntimeException -> 0x0037 }
            throw r3     // Catch:{ IOException | RuntimeException -> 0x0037 }
        L_0x0037:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed writing '"
            r2.append(r3)
            r2.append(r6)
            java.lang.String r3 = "' to '"
            r2.append(r3)
            r2.append(r5)
            java.lang.String r3 = "': "
            r2.append(r3)
            java.lang.String r3 = r0.getMessage()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Slog.w(r1, r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.batterysaver.FileUpdater.injectWriteToFile(java.lang.String, java.lang.String):void");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    @GuardedBy({"mLock"})
    private void saveDefaultValuesLocked() {
        AtomicFile file = new AtomicFile(injectDefaultValuesFilename());
        FileOutputStream outs = null;
        try {
            file.getBaseFile().getParentFile().mkdirs();
            outs = file.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(outs, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.startTag((String) null, TAG_DEFAULT_ROOT);
            XmlUtils.writeMapXml(this.mDefaultValues, out, (XmlUtils.WriteMapCallback) null);
            out.endTag((String) null, TAG_DEFAULT_ROOT);
            out.endDocument();
            file.finishWrite(outs);
        } catch (IOException | RuntimeException | XmlPullParserException e) {
            Slog.e(TAG, "Failed to write to file " + file.getBaseFile(), e);
            file.failWrite(outs);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003d, code lost:
        android.util.Slog.e(TAG, "Invalid root tag: " + r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0052, code lost:
        if (r5 == null) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        $closeResource((java.lang.Throwable) null, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0057, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0060, code lost:
        if (r5 == null) goto L_0x008c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        $closeResource((java.lang.Throwable) null, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0068, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0069, code lost:
        if (r5 != null) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        $closeResource(r6, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x006e, code lost:
        throw r7;
     */
    @com.android.internal.annotations.GuardedBy({"mLock"})
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean loadDefaultValuesLocked() {
        /*
            r13 = this;
            java.lang.String r0 = "BatterySaverController"
            android.util.AtomicFile r1 = new android.util.AtomicFile
            java.io.File r2 = r13.injectDefaultValuesFilename()
            r1.<init>(r2)
            r2 = 0
            r3 = 0
            r4 = 1
            java.io.FileInputStream r5 = r1.openRead()     // Catch:{ FileNotFoundException -> 0x0089, IOException | RuntimeException | XmlPullParserException -> 0x006f }
            org.xmlpull.v1.XmlPullParser r6 = android.util.Xml.newPullParser()     // Catch:{ all -> 0x0066 }
            java.nio.charset.Charset r7 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x0066 }
            java.lang.String r7 = r7.name()     // Catch:{ all -> 0x0066 }
            r6.setInput(r5, r7)     // Catch:{ all -> 0x0066 }
        L_0x001f:
            int r7 = r6.next()     // Catch:{ all -> 0x0066 }
            r8 = r7
            r9 = 0
            if (r7 == r4) goto L_0x0060
            r7 = 2
            if (r8 == r7) goto L_0x002b
            goto L_0x001f
        L_0x002b:
            int r7 = r6.getDepth()     // Catch:{ all -> 0x0066 }
            java.lang.String r10 = r6.getName()     // Catch:{ all -> 0x0066 }
            java.lang.String r11 = "defaults"
            if (r7 != r4) goto L_0x0058
            boolean r11 = r11.equals(r10)     // Catch:{ all -> 0x0066 }
            if (r11 != 0) goto L_0x001f
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0066 }
            r11.<init>()     // Catch:{ all -> 0x0066 }
            java.lang.String r12 = "Invalid root tag: "
            r11.append(r12)     // Catch:{ all -> 0x0066 }
            r11.append(r10)     // Catch:{ all -> 0x0066 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0066 }
            android.util.Slog.e(r0, r11)     // Catch:{ all -> 0x0066 }
            if (r5 == 0) goto L_0x0057
            $closeResource(r9, r5)     // Catch:{ FileNotFoundException -> 0x0089, IOException | RuntimeException | XmlPullParserException -> 0x006f }
        L_0x0057:
            return r3
        L_0x0058:
            java.lang.String[] r12 = new java.lang.String[r4]     // Catch:{ all -> 0x0066 }
            android.util.ArrayMap r9 = com.android.internal.util.XmlUtils.readThisArrayMapXml(r6, r11, r12, r9)     // Catch:{ all -> 0x0066 }
            r2 = r9
            goto L_0x001f
        L_0x0060:
            if (r5 == 0) goto L_0x008b
            $closeResource(r9, r5)     // Catch:{ FileNotFoundException -> 0x0089, IOException | RuntimeException | XmlPullParserException -> 0x006f }
            goto L_0x008b
        L_0x0066:
            r6 = move-exception
            throw r6     // Catch:{ all -> 0x0068 }
        L_0x0068:
            r7 = move-exception
            if (r5 == 0) goto L_0x006e
            $closeResource(r6, r5)     // Catch:{ FileNotFoundException -> 0x0089, IOException | RuntimeException | XmlPullParserException -> 0x006f }
        L_0x006e:
            throw r7     // Catch:{ FileNotFoundException -> 0x0089, IOException | RuntimeException | XmlPullParserException -> 0x006f }
        L_0x006f:
            r5 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Failed to read file "
            r6.append(r7)
            java.io.File r7 = r1.getBaseFile()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Slog.e(r0, r6, r5)
            goto L_0x008c
        L_0x0089:
            r0 = move-exception
            r2 = 0
        L_0x008b:
        L_0x008c:
            if (r2 == 0) goto L_0x0099
            android.util.ArrayMap<java.lang.String, java.lang.String> r0 = r13.mDefaultValues
            r0.clear()
            android.util.ArrayMap<java.lang.String, java.lang.String> r0 = r13.mDefaultValues
            r0.putAll(r2)
            return r4
        L_0x0099:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.batterysaver.FileUpdater.loadDefaultValuesLocked():boolean");
    }

    private void doWtf(String message) {
        injectWtf(message, (Throwable) null);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void injectWtf(String message, Throwable e) {
        Slog.wtf(TAG, message, e);
    }

    /* access modifiers changed from: package-private */
    public File injectDefaultValuesFilename() {
        File dir = new File(Environment.getDataSystemDirectory(), "battery-saver");
        dir.mkdirs();
        return new File(dir, "default-values.xml");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean injectShouldSkipWrite() {
        return SystemProperties.getBoolean(PROP_SKIP_WRITE, false);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ArrayMap<String, String> getDefaultValuesForTest() {
        return this.mDefaultValues;
    }
}
