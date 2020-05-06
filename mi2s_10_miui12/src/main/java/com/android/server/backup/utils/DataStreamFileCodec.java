package com.android.server.backup.utils;

import java.io.File;

public final class DataStreamFileCodec<T> {
    private final DataStreamCodec<T> mCodec;
    private final File mFile;

    public DataStreamFileCodec(File file, DataStreamCodec<T> codec) {
        this.mFile = file;
        this.mCodec = codec;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0022, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0025, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0026, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0029, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public T deserialize() throws java.io.IOException {
        /*
            r4 = this;
            java.io.FileInputStream r0 = new java.io.FileInputStream
            java.io.File r1 = r4.mFile
            r0.<init>(r1)
            java.io.DataInputStream r1 = new java.io.DataInputStream     // Catch:{ all -> 0x0023 }
            r1.<init>(r0)     // Catch:{ all -> 0x0023 }
            com.android.server.backup.utils.DataStreamCodec<T> r2 = r4.mCodec     // Catch:{ all -> 0x001c }
            java.lang.Object r2 = r2.deserialize(r1)     // Catch:{ all -> 0x001c }
            r3 = 0
            $closeResource(r3, r1)     // Catch:{ all -> 0x0023 }
            $closeResource(r3, r0)
            return r2
        L_0x001c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x001e }
        L_0x001e:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ all -> 0x0023 }
            throw r3     // Catch:{ all -> 0x0023 }
        L_0x0023:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0025 }
        L_0x0025:
            r2 = move-exception
            $closeResource(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.utils.DataStreamFileCodec.deserialize():java.lang.Object");
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

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0029, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        $closeResource(r3, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002d, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0030, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0034, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0037, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0038, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x003b, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void serialize(T r6) throws java.io.IOException {
        /*
            r5 = this;
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            java.io.File r1 = r5.mFile
            r0.<init>(r1)
            java.io.BufferedOutputStream r1 = new java.io.BufferedOutputStream     // Catch:{ all -> 0x0035 }
            r1.<init>(r0)     // Catch:{ all -> 0x0035 }
            java.io.DataOutputStream r2 = new java.io.DataOutputStream     // Catch:{ all -> 0x002e }
            r2.<init>(r1)     // Catch:{ all -> 0x002e }
            com.android.server.backup.utils.DataStreamCodec<T> r3 = r5.mCodec     // Catch:{ all -> 0x0027 }
            r3.serialize(r6, r2)     // Catch:{ all -> 0x0027 }
            r2.flush()     // Catch:{ all -> 0x0027 }
            r3 = 0
            $closeResource(r3, r2)     // Catch:{ all -> 0x002e }
            $closeResource(r3, r1)     // Catch:{ all -> 0x0035 }
            $closeResource(r3, r0)
            return
        L_0x0027:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0029 }
        L_0x0029:
            r4 = move-exception
            $closeResource(r3, r2)     // Catch:{ all -> 0x002e }
            throw r4     // Catch:{ all -> 0x002e }
        L_0x002e:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0030 }
        L_0x0030:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ all -> 0x0035 }
            throw r3     // Catch:{ all -> 0x0035 }
        L_0x0035:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0037 }
        L_0x0037:
            r2 = move-exception
            $closeResource(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.utils.DataStreamFileCodec.serialize(java.lang.Object):void");
    }
}
