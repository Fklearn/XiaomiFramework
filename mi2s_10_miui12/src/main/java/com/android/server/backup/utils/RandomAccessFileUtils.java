package com.android.server.backup.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public final class RandomAccessFileUtils {
    private static RandomAccessFile getRandomAccessFile(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, "rwd");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000e, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x000f, code lost:
        if (r0 != null) goto L_0x0011;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0014, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void writeBoolean(java.io.File r3, boolean r4) {
        /*
            java.io.RandomAccessFile r0 = getRandomAccessFile(r3)     // Catch:{ IOException -> 0x0015 }
            r1 = 0
            r0.writeBoolean(r4)     // Catch:{ all -> 0x000c }
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0015 }
            goto L_0x0030
        L_0x000c:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x000e }
        L_0x000e:
            r2 = move-exception
            if (r0 == 0) goto L_0x0014
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0015 }
        L_0x0014:
            throw r2     // Catch:{ IOException -> 0x0015 }
        L_0x0015:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Error writing file:"
            r1.append(r2)
            java.lang.String r2 = r3.getAbsolutePath()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BackupManagerService"
            android.util.Slog.w(r2, r1, r0)
        L_0x0030:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.utils.RandomAccessFileUtils.writeBoolean(java.io.File, boolean):void");
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

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x000f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0010, code lost:
        if (r0 != null) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0015, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean readBoolean(java.io.File r3, boolean r4) {
        /*
            java.io.RandomAccessFile r0 = getRandomAccessFile(r3)     // Catch:{ IOException -> 0x0016 }
            r1 = 0
            boolean r2 = r0.readBoolean()     // Catch:{ all -> 0x000d }
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0016 }
            return r2
        L_0x000d:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x000f }
        L_0x000f:
            r2 = move-exception
            if (r0 == 0) goto L_0x0015
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0016 }
        L_0x0015:
            throw r2     // Catch:{ IOException -> 0x0016 }
        L_0x0016:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Error reading file:"
            r1.append(r2)
            java.lang.String r2 = r3.getAbsolutePath()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BackupManagerService"
            android.util.Slog.w(r2, r1, r0)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.utils.RandomAccessFileUtils.readBoolean(java.io.File, boolean):boolean");
    }
}
