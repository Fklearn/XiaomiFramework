package com.android.server.pm;

import android.content.pm.ShortcutInfo;
import android.os.SystemClock;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.Deque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ShortcutBitmapSaver {
    private static final boolean ADD_DELAY_BEFORE_SAVE_FOR_TEST = false;
    private static final boolean DEBUG = false;
    private static final long SAVE_DELAY_MS_FOR_TEST = 1000;
    private static final String TAG = "ShortcutService";
    private final long SAVE_WAIT_TIMEOUT_MS = 30000;
    private final Executor mExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    @GuardedBy({"mPendingItems"})
    private final Deque<PendingItem> mPendingItems = new LinkedBlockingDeque();
    private final Runnable mRunnable = new Runnable() {
        public final void run() {
            ShortcutBitmapSaver.this.lambda$new$1$ShortcutBitmapSaver();
        }
    };
    private final ShortcutService mService;

    private static class PendingItem {
        public final byte[] bytes;
        private final long mInstantiatedUptimeMillis;
        public final ShortcutInfo shortcut;

        private PendingItem(ShortcutInfo shortcut2, byte[] bytes2) {
            this.shortcut = shortcut2;
            this.bytes = bytes2;
            this.mInstantiatedUptimeMillis = SystemClock.uptimeMillis();
        }

        public String toString() {
            return "PendingItem{size=" + this.bytes.length + " age=" + (SystemClock.uptimeMillis() - this.mInstantiatedUptimeMillis) + "ms shortcut=" + this.shortcut.toInsecureString() + "}";
        }
    }

    public ShortcutBitmapSaver(ShortcutService service) {
        this.mService = service;
    }

    public boolean waitForAllSavesLocked() {
        CountDownLatch latch = new CountDownLatch(1);
        this.mExecutor.execute(new Runnable(latch) {
            private final /* synthetic */ CountDownLatch f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.countDown();
            }
        });
        try {
            if (latch.await(30000, TimeUnit.MILLISECONDS)) {
                return true;
            }
            this.mService.wtf("Timed out waiting on saving bitmaps.");
            return false;
        } catch (InterruptedException e) {
            Slog.w(TAG, "interrupted");
            return false;
        }
    }

    public String getBitmapPathMayWaitLocked(ShortcutInfo shortcut) {
        if (!waitForAllSavesLocked() || !shortcut.hasIconFile()) {
            return null;
        }
        return shortcut.getBitmapPath();
    }

    public void removeIcon(ShortcutInfo shortcut) {
        shortcut.setIconResourceId(0);
        shortcut.setIconResName((String) null);
        shortcut.setBitmapPath((String) null);
        shortcut.clearFlags(2572);
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0096, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x009f, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveBitmapLocked(android.content.pm.ShortcutInfo r9, int r10, android.graphics.Bitmap.CompressFormat r11, int r12) {
        /*
            r8 = this;
            android.graphics.drawable.Icon r0 = r9.getIcon()
            com.android.internal.util.Preconditions.checkNotNull(r0)
            android.graphics.Bitmap r1 = r0.getBitmap()
            if (r1 != 0) goto L_0x0024
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Missing icon: "
            r2.append(r3)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "ShortcutService"
            android.util.Log.e(r3, r2)
            return
        L_0x0024:
            android.os.StrictMode$ThreadPolicy r2 = android.os.StrictMode.getThreadPolicy()
            android.os.StrictMode$ThreadPolicy$Builder r3 = new android.os.StrictMode$ThreadPolicy$Builder     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
            r3.<init>(r2)     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
            android.os.StrictMode$ThreadPolicy$Builder r3 = r3.permitCustomSlowCalls()     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
            android.os.StrictMode$ThreadPolicy r3 = r3.build()     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
            android.os.StrictMode.setThreadPolicy(r3)     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
            com.android.server.pm.ShortcutService r3 = r8.mService     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
            android.graphics.Bitmap r3 = com.android.server.pm.ShortcutService.shrinkBitmap(r1, r10)     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
            java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00a0 }
            r5 = 65536(0x10000, float:9.18355E-41)
            r4.<init>(r5)     // Catch:{ all -> 0x00a0 }
            boolean r5 = r3.compress(r11, r12, r4)     // Catch:{ all -> 0x0094 }
            if (r5 != 0) goto L_0x0052
            java.lang.String r5 = "ShortcutService"
            java.lang.String r6 = "Unable to compress bitmap"
            android.util.Slog.wtf(r5, r6)     // Catch:{ all -> 0x0094 }
        L_0x0052:
            r4.flush()     // Catch:{ all -> 0x0094 }
            byte[] r5 = r4.toByteArray()     // Catch:{ all -> 0x0094 }
            r4.close()     // Catch:{ all -> 0x0094 }
            r4.close()     // Catch:{ all -> 0x00a0 }
            if (r3 == r1) goto L_0x0064
            r3.recycle()     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
        L_0x0064:
            android.os.StrictMode.setThreadPolicy(r2)
            r3 = 2056(0x808, float:2.881E-42)
            r9.addFlags(r3)
            int r3 = r0.getType()
            r4 = 5
            if (r3 != r4) goto L_0x0079
            r3 = 512(0x200, float:7.175E-43)
            r9.addFlags(r3)
        L_0x0079:
            com.android.server.pm.ShortcutBitmapSaver$PendingItem r3 = new com.android.server.pm.ShortcutBitmapSaver$PendingItem
            r4 = 0
            r3.<init>(r9, r5)
            r4 = r3
            java.util.Deque<com.android.server.pm.ShortcutBitmapSaver$PendingItem> r6 = r8.mPendingItems
            monitor-enter(r6)
            java.util.Deque<com.android.server.pm.ShortcutBitmapSaver$PendingItem> r3 = r8.mPendingItems     // Catch:{ all -> 0x0091 }
            r3.add(r4)     // Catch:{ all -> 0x0091 }
            monitor-exit(r6)     // Catch:{ all -> 0x0091 }
            java.util.concurrent.Executor r3 = r8.mExecutor
            java.lang.Runnable r6 = r8.mRunnable
            r3.execute(r6)
            return
        L_0x0091:
            r3 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0091 }
            throw r3
        L_0x0094:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x0096 }
        L_0x0096:
            r6 = move-exception
            r4.close()     // Catch:{ all -> 0x009b }
            goto L_0x009f
        L_0x009b:
            r7 = move-exception
            r5.addSuppressed(r7)     // Catch:{ all -> 0x00a0 }
        L_0x009f:
            throw r6     // Catch:{ all -> 0x00a0 }
        L_0x00a0:
            r4 = move-exception
            if (r3 == r1) goto L_0x00a6
            r3.recycle()     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
        L_0x00a6:
            throw r4     // Catch:{ IOException | OutOfMemoryError | RuntimeException -> 0x00aa }
        L_0x00a8:
            r3 = move-exception
            goto L_0x00b6
        L_0x00aa:
            r3 = move-exception
            java.lang.String r4 = "ShortcutService"
            java.lang.String r5 = "Unable to write bitmap to file"
            android.util.Slog.wtf(r4, r5, r3)     // Catch:{ all -> 0x00a8 }
            android.os.StrictMode.setThreadPolicy(r2)
            return
        L_0x00b6:
            android.os.StrictMode.setThreadPolicy(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ShortcutBitmapSaver.saveBitmapLocked(android.content.pm.ShortcutInfo, int, android.graphics.Bitmap$CompressFormat, int):void");
    }

    public /* synthetic */ void lambda$new$1$ShortcutBitmapSaver() {
        do {
        } while (processPendingItems());
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0016, code lost:
        if (r0.getBitmapPath() != null) goto L_0x001b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0018, code lost:
        removeIcon(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        r0.clearFlags(2048);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001e, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r0 = r3.shortcut;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0030, code lost:
        if (r0.isIconPendingSave() != false) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0038, code lost:
        if (r0.getBitmapPath() != null) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003a, code lost:
        removeIcon(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x003d, code lost:
        r0.clearFlags(2048);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0040, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0041, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        r5 = r8.mService.openIconFileForWrite(r0.getUserId(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0050, code lost:
        r2 = r5.getFile();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        r5.write(r3.bytes);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        libcore.io.IoUtils.closeQuietly(r5);
        r0.setBitmapPath(r2.getAbsolutePath());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0067, code lost:
        if (r0.getBitmapPath() != null) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0069, code lost:
        removeIcon(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x006c, code lost:
        r0.clearFlags(2048);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x006f, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0070, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        libcore.io.IoUtils.closeQuietly(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0074, code lost:
        throw r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0075, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        android.util.Slog.e(TAG, "Unable to write bitmap to file", r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0085, code lost:
        r2.delete();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x008e, code lost:
        if (r0.getBitmapPath() == null) goto L_0x0090;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0090, code lost:
        removeIcon(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0093, code lost:
        r0.clearFlags(2048);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0096, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0010, code lost:
        if (r0 == null) goto L_0x001e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean processPendingItems() {
        /*
            r8 = this;
            r0 = 0
            r1 = 2048(0x800, float:2.87E-42)
            java.util.Deque<com.android.server.pm.ShortcutBitmapSaver$PendingItem> r2 = r8.mPendingItems     // Catch:{ all -> 0x009a }
            monitor-enter(r2)     // Catch:{ all -> 0x009a }
            java.util.Deque<com.android.server.pm.ShortcutBitmapSaver$PendingItem> r3 = r8.mPendingItems     // Catch:{ all -> 0x0097 }
            int r3 = r3.size()     // Catch:{ all -> 0x0097 }
            if (r3 != 0) goto L_0x001f
            r3 = 0
            monitor-exit(r2)     // Catch:{ all -> 0x0097 }
            if (r0 == 0) goto L_0x001e
            java.lang.String r2 = r0.getBitmapPath()
            if (r2 != 0) goto L_0x001b
            r8.removeIcon(r0)
        L_0x001b:
            r0.clearFlags(r1)
        L_0x001e:
            return r3
        L_0x001f:
            java.util.Deque<com.android.server.pm.ShortcutBitmapSaver$PendingItem> r3 = r8.mPendingItems     // Catch:{ all -> 0x0097 }
            java.lang.Object r3 = r3.pop()     // Catch:{ all -> 0x0097 }
            com.android.server.pm.ShortcutBitmapSaver$PendingItem r3 = (com.android.server.pm.ShortcutBitmapSaver.PendingItem) r3     // Catch:{ all -> 0x0097 }
            monitor-exit(r2)     // Catch:{ all -> 0x0097 }
            android.content.pm.ShortcutInfo r2 = r3.shortcut     // Catch:{ all -> 0x009a }
            r0 = r2
            boolean r2 = r0.isIconPendingSave()     // Catch:{ all -> 0x009a }
            r4 = 1
            if (r2 != 0) goto L_0x0041
            java.lang.String r2 = r0.getBitmapPath()
            if (r2 != 0) goto L_0x003d
            r8.removeIcon(r0)
        L_0x003d:
            r0.clearFlags(r1)
            return r4
        L_0x0041:
            r2 = 0
            com.android.server.pm.ShortcutService r5 = r8.mService     // Catch:{ IOException | RuntimeException -> 0x0075 }
            int r6 = r0.getUserId()     // Catch:{ IOException | RuntimeException -> 0x0075 }
            com.android.server.pm.ShortcutService$FileOutputStreamWithPath r5 = r5.openIconFileForWrite(r6, r0)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.io.File r6 = r5.getFile()     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r2 = r6
            byte[] r6 = r3.bytes     // Catch:{ all -> 0x0070 }
            r5.write(r6)     // Catch:{ all -> 0x0070 }
            libcore.io.IoUtils.closeQuietly(r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.lang.String r6 = r2.getAbsolutePath()     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r0.setBitmapPath(r6)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.lang.String r2 = r0.getBitmapPath()
            if (r2 != 0) goto L_0x006c
            r8.removeIcon(r0)
        L_0x006c:
            r0.clearFlags(r1)
            return r4
        L_0x0070:
            r6 = move-exception
            libcore.io.IoUtils.closeQuietly(r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            throw r6     // Catch:{ IOException | RuntimeException -> 0x0075 }
        L_0x0075:
            r5 = move-exception
            java.lang.String r6 = "ShortcutService"
            java.lang.String r7 = "Unable to write bitmap to file"
            android.util.Slog.e(r6, r7, r5)     // Catch:{ all -> 0x009a }
            if (r2 == 0) goto L_0x0088
            boolean r6 = r2.exists()     // Catch:{ all -> 0x009a }
            if (r6 == 0) goto L_0x0088
            r2.delete()     // Catch:{ all -> 0x009a }
        L_0x0088:
            java.lang.String r6 = r0.getBitmapPath()
            if (r6 != 0) goto L_0x0093
            r8.removeIcon(r0)
        L_0x0093:
            r0.clearFlags(r1)
            return r4
        L_0x0097:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0097 }
            throw r3     // Catch:{ all -> 0x009a }
        L_0x009a:
            r2 = move-exception
            if (r0 == 0) goto L_0x00a9
            java.lang.String r3 = r0.getBitmapPath()
            if (r3 != 0) goto L_0x00a6
            r8.removeIcon(r0)
        L_0x00a6:
            r0.clearFlags(r1)
        L_0x00a9:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ShortcutBitmapSaver.processPendingItems():boolean");
    }

    public void dumpLocked(PrintWriter pw, String prefix) {
        synchronized (this.mPendingItems) {
            int N = this.mPendingItems.size();
            pw.print(prefix);
            pw.println("Pending saves: Num=" + N + " Executor=" + this.mExecutor);
            for (PendingItem item : this.mPendingItems) {
                pw.print(prefix);
                pw.print("  ");
                pw.println(item);
            }
        }
    }
}
