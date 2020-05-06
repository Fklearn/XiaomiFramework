package com.android.server.pm;

import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ShortcutDumpFiles {
    private static final boolean DEBUG = false;
    private static final String TAG = "ShortcutService";
    private final ShortcutService mService;

    public ShortcutDumpFiles(ShortcutService service) {
        this.mService = service;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0046, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        $closeResource(r5, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004a, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean save(java.lang.String r8, java.util.function.Consumer<java.io.PrintWriter> r9) {
        /*
            r7 = this;
            java.lang.String r0 = "ShortcutService"
            r1 = 0
            com.android.server.pm.ShortcutService r2 = r7.mService     // Catch:{ IOException | RuntimeException -> 0x004b }
            java.io.File r2 = r2.getDumpPath()     // Catch:{ IOException | RuntimeException -> 0x004b }
            r2.mkdirs()     // Catch:{ IOException | RuntimeException -> 0x004b }
            boolean r3 = r2.exists()     // Catch:{ IOException | RuntimeException -> 0x004b }
            if (r3 != 0) goto L_0x0027
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException | RuntimeException -> 0x004b }
            r3.<init>()     // Catch:{ IOException | RuntimeException -> 0x004b }
            java.lang.String r4 = "Failed to create directory: "
            r3.append(r4)     // Catch:{ IOException | RuntimeException -> 0x004b }
            r3.append(r2)     // Catch:{ IOException | RuntimeException -> 0x004b }
            java.lang.String r3 = r3.toString()     // Catch:{ IOException | RuntimeException -> 0x004b }
            android.util.Slog.e(r0, r3)     // Catch:{ IOException | RuntimeException -> 0x004b }
            return r1
        L_0x0027:
            java.io.File r3 = new java.io.File     // Catch:{ IOException | RuntimeException -> 0x004b }
            r3.<init>(r2, r8)     // Catch:{ IOException | RuntimeException -> 0x004b }
            java.io.PrintWriter r4 = new java.io.PrintWriter     // Catch:{ IOException | RuntimeException -> 0x004b }
            java.io.BufferedOutputStream r5 = new java.io.BufferedOutputStream     // Catch:{ IOException | RuntimeException -> 0x004b }
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ IOException | RuntimeException -> 0x004b }
            r6.<init>(r3)     // Catch:{ IOException | RuntimeException -> 0x004b }
            r5.<init>(r6)     // Catch:{ IOException | RuntimeException -> 0x004b }
            r4.<init>(r5)     // Catch:{ IOException | RuntimeException -> 0x004b }
            r5 = 0
            r9.accept(r4)     // Catch:{ all -> 0x0044 }
            $closeResource(r5, r4)     // Catch:{ IOException | RuntimeException -> 0x004b }
            r0 = 1
            return r0
        L_0x0044:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r6 = move-exception
            $closeResource(r5, r4)     // Catch:{ IOException | RuntimeException -> 0x004b }
            throw r6     // Catch:{ IOException | RuntimeException -> 0x004b }
        L_0x004b:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Failed to create dump file: "
            r3.append(r4)
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            android.util.Slog.w(r0, r3, r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ShortcutDumpFiles.save(java.lang.String, java.util.function.Consumer):boolean");
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

    public boolean save(String filename, byte[] utf8bytes) {
        return save(filename, (Consumer<PrintWriter>) new Consumer(utf8bytes) {
            private final /* synthetic */ byte[] f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((PrintWriter) obj).println(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(this.f$0)).toString());
            }
        });
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        $closeResource((java.lang.Throwable) null, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0064, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0069, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        $closeResource(r2, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006d, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dumpAll(java.io.PrintWriter r10) {
        /*
            r9 = this;
            com.android.server.pm.ShortcutService r0 = r9.mService     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.io.File r0 = r0.getDumpPath()     // Catch:{ IOException | RuntimeException -> 0x0075 }
            com.android.server.pm.-$$Lambda$ShortcutDumpFiles$v6wMz6MRa9pgSnEDM_9bjvrLaKY r1 = com.android.server.pm.$$Lambda$ShortcutDumpFiles$v6wMz6MRa9pgSnEDM_9bjvrLaKY.INSTANCE     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.io.File[] r1 = r0.listFiles(r1)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            boolean r2 = r0.exists()     // Catch:{ IOException | RuntimeException -> 0x0075 }
            if (r2 == 0) goto L_0x006f
            boolean r2 = com.android.internal.util.ArrayUtils.isEmpty(r1)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            if (r2 == 0) goto L_0x0019
            goto L_0x006f
        L_0x0019:
            com.android.server.pm.-$$Lambda$ShortcutDumpFiles$stGgHzhh-NVWPgDSwmH2ybAWRE8 r2 = com.android.server.pm.$$Lambda$ShortcutDumpFiles$stGgHzhhNVWPgDSwmH2ybAWRE8.INSTANCE     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.util.Comparator r2 = java.util.Comparator.comparing(r2)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.util.Arrays.sort(r1, r2)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            int r2 = r1.length     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r3 = 0
        L_0x0024:
            if (r3 >= r2) goto L_0x006e
            r4 = r1[r3]     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.lang.String r5 = "*** Dumping: "
            r10.print(r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.lang.String r5 = r4.getName()     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r10.println(r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.lang.String r5 = "mtime: "
            r10.print(r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            long r5 = r4.lastModified()     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.lang.String r5 = com.android.server.pm.ShortcutService.formatTime(r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r10.println(r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ IOException | RuntimeException -> 0x0075 }
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r7.<init>(r4)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r6.<init>(r7)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r5.<init>(r6)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            r6 = 0
            r7 = r6
        L_0x0056:
            java.lang.String r8 = r5.readLine()     // Catch:{ all -> 0x0067 }
            r7 = r8
            if (r8 == 0) goto L_0x0061
            r10.println(r7)     // Catch:{ all -> 0x0067 }
            goto L_0x0056
        L_0x0061:
            $closeResource(r6, r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            int r3 = r3 + 1
            goto L_0x0024
        L_0x0067:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0069 }
        L_0x0069:
            r3 = move-exception
            $closeResource(r2, r5)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            throw r3     // Catch:{ IOException | RuntimeException -> 0x0075 }
        L_0x006e:
            goto L_0x007d
        L_0x006f:
            java.lang.String r2 = "  No dump files found."
            r10.print(r2)     // Catch:{ IOException | RuntimeException -> 0x0075 }
            return
        L_0x0075:
            r0 = move-exception
            java.lang.String r1 = "ShortcutService"
            java.lang.String r2 = "Failed to print dump files"
            android.util.Slog.w(r1, r2, r0)
        L_0x007d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.ShortcutDumpFiles.dumpAll(java.io.PrintWriter):void");
    }
}
