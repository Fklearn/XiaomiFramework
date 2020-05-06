package com.android.server.backup;

final class UserBackupManagerFilePersistedSettings {
    private static final String BACKUP_ENABLE_FILE = "backup_enabled";

    UserBackupManagerFilePersistedSettings() {
    }

    static boolean readBackupEnableState(int userId) {
        return readBackupEnableState(UserBackupManagerFiles.getBaseStateDir(userId));
    }

    static void writeBackupEnableState(int userId, boolean enable) {
        writeBackupEnableState(UserBackupManagerFiles.getBaseStateDir(userId), enable);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0025, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        $closeResource(r4, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0029, code lost:
        throw r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean readBackupEnableState(java.io.File r7) {
        /*
            java.io.File r0 = new java.io.File
            java.lang.String r1 = "backup_enabled"
            r0.<init>(r7, r1)
            boolean r1 = r0.exists()
            r2 = 0
            java.lang.String r3 = "BackupManagerService"
            if (r1 == 0) goto L_0x0031
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ IOException -> 0x002a }
            r1.<init>(r0)     // Catch:{ IOException -> 0x002a }
            r4 = 0
            int r5 = r1.read()     // Catch:{ all -> 0x0023 }
            if (r5 == 0) goto L_0x001e
            r6 = 1
            goto L_0x001f
        L_0x001e:
            r6 = r2
        L_0x001f:
            $closeResource(r4, r1)     // Catch:{ IOException -> 0x002a }
            return r6
        L_0x0023:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0025 }
        L_0x0025:
            r5 = move-exception
            $closeResource(r4, r1)     // Catch:{ IOException -> 0x002a }
            throw r5     // Catch:{ IOException -> 0x002a }
        L_0x002a:
            r1 = move-exception
            java.lang.String r4 = "Cannot read enable state; assuming disabled"
            android.util.Slog.e(r3, r4)
            goto L_0x0037
        L_0x0031:
            java.lang.String r1 = "isBackupEnabled() => false due to absent settings file"
            android.util.Slog.i(r3, r1)
        L_0x0037:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerFilePersistedSettings.readBackupEnableState(java.io.File):boolean");
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

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        $closeResource(r3, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002c, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void writeBackupEnableState(java.io.File r5, boolean r6) {
        /*
            java.io.File r0 = new java.io.File
            java.lang.String r1 = "backup_enabled"
            r0.<init>(r5, r1)
            java.io.File r1 = new java.io.File
            java.lang.String r2 = "backup_enabled-stage"
            r1.<init>(r5, r2)
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ IOException | RuntimeException -> 0x002d }
            r2.<init>(r1)     // Catch:{ IOException | RuntimeException -> 0x002d }
            r3 = 0
            if (r6 == 0) goto L_0x0018
            r4 = 1
            goto L_0x0019
        L_0x0018:
            r4 = 0
        L_0x0019:
            r2.write(r4)     // Catch:{ all -> 0x0026 }
            r2.close()     // Catch:{ all -> 0x0026 }
            r1.renameTo(r0)     // Catch:{ all -> 0x0026 }
            $closeResource(r3, r2)     // Catch:{ IOException | RuntimeException -> 0x002d }
            goto L_0x004e
        L_0x0026:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0028 }
        L_0x0028:
            r4 = move-exception
            $closeResource(r3, r2)     // Catch:{ IOException | RuntimeException -> 0x002d }
            throw r4     // Catch:{ IOException | RuntimeException -> 0x002d }
        L_0x002d:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unable to record backup enable state; reverting to disabled: "
            r3.append(r4)
            java.lang.String r4 = r2.getMessage()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "BackupManagerService"
            android.util.Slog.e(r4, r3)
            r0.delete()
            r1.delete()
        L_0x004e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerFilePersistedSettings.writeBackupEnableState(java.io.File, boolean):void");
    }
}
