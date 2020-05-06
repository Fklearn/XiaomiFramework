package com.android.server.backup.encryption.storage;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class TertiaryKeysTable {
    private final BackupEncryptionDbHelper mHelper;

    TertiaryKeysTable(BackupEncryptionDbHelper helper) {
        this.mHelper = helper;
    }

    public long addKey(TertiaryKey tertiaryKey) throws EncryptionDbException {
        SQLiteDatabase db = this.mHelper.getWritableDatabaseSafe();
        ContentValues values = new ContentValues();
        values.put("secondary_key_alias", tertiaryKey.getSecondaryKeyAlias());
        values.put("package_name", tertiaryKey.getPackageName());
        values.put("wrapped_key_bytes", tertiaryKey.getWrappedKeyBytes());
        return db.replace("tertiary_keys", (String) null, values);
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0059, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x005a, code lost:
        if (r1 != null) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005c, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x005f, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.Optional<com.android.server.backup.encryption.storage.TertiaryKey> getKey(java.lang.String r12, java.lang.String r13) throws com.android.server.backup.encryption.storage.EncryptionDbException {
        /*
            r11 = this;
            com.android.server.backup.encryption.storage.BackupEncryptionDbHelper r0 = r11.mHelper
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabaseSafe()
            java.lang.String r9 = "wrapped_key_bytes"
            java.lang.String r1 = "_id"
            java.lang.String r2 = "secondary_key_alias"
            java.lang.String r3 = "package_name"
            java.lang.String[] r3 = new java.lang.String[]{r1, r2, r3, r9}
            java.lang.String r10 = "secondary_key_alias = ? AND package_name = ?"
            r1 = 2
            java.lang.String[] r5 = new java.lang.String[r1]
            r1 = 0
            r5[r1] = r12
            r1 = 1
            r5[r1] = r13
            java.lang.String r2 = "tertiary_keys"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r10
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            int r2 = r1.getCount()     // Catch:{ all -> 0x0057 }
            r4 = 0
            if (r2 != 0) goto L_0x003e
            java.util.Optional r6 = java.util.Optional.empty()     // Catch:{ all -> 0x0057 }
            $closeResource(r4, r1)
            return r6
        L_0x003e:
            r1.moveToFirst()     // Catch:{ all -> 0x0057 }
            int r6 = r1.getColumnIndexOrThrow(r9)     // Catch:{ all -> 0x0057 }
            byte[] r6 = r1.getBlob(r6)     // Catch:{ all -> 0x0057 }
            com.android.server.backup.encryption.storage.TertiaryKey r7 = new com.android.server.backup.encryption.storage.TertiaryKey     // Catch:{ all -> 0x0057 }
            r7.<init>(r12, r13, r6)     // Catch:{ all -> 0x0057 }
            java.util.Optional r7 = java.util.Optional.of(r7)     // Catch:{ all -> 0x0057 }
            $closeResource(r4, r1)
            return r7
        L_0x0057:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0059 }
        L_0x0059:
            r4 = move-exception
            if (r1 == 0) goto L_0x005f
            $closeResource(r2, r1)
        L_0x005f:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.encryption.storage.TertiaryKeysTable.getKey(java.lang.String, java.lang.String):java.util.Optional");
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

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x005f, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0060, code lost:
        if (r1 != null) goto L_0x0062;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0062, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0065, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.Map<java.lang.String, com.android.server.backup.encryption.storage.TertiaryKey> getAllKeys(java.lang.String r14) throws com.android.server.backup.encryption.storage.EncryptionDbException {
        /*
            r13 = this;
            com.android.server.backup.encryption.storage.BackupEncryptionDbHelper r0 = r13.mHelper
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabaseSafe()
            java.lang.String r9 = "wrapped_key_bytes"
            java.lang.String r10 = "package_name"
            java.lang.String r1 = "_id"
            java.lang.String r2 = "secondary_key_alias"
            java.lang.String[] r3 = new java.lang.String[]{r1, r2, r10, r9}
            java.lang.String r11 = "secondary_key_alias = ?"
            r1 = 1
            java.lang.String[] r5 = new java.lang.String[r1]
            r1 = 0
            r5[r1] = r14
            android.util.ArrayMap r1 = new android.util.ArrayMap
            r1.<init>()
            r12 = r1
            java.lang.String r2 = "tertiary_keys"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r11
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
        L_0x0032:
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x005d }
            if (r2 == 0) goto L_0x0054
            int r2 = r1.getColumnIndexOrThrow(r10)     // Catch:{ all -> 0x005d }
            java.lang.String r2 = r1.getString(r2)     // Catch:{ all -> 0x005d }
            int r4 = r1.getColumnIndexOrThrow(r9)     // Catch:{ all -> 0x005d }
            byte[] r4 = r1.getBlob(r4)     // Catch:{ all -> 0x005d }
            com.android.server.backup.encryption.storage.TertiaryKey r6 = new com.android.server.backup.encryption.storage.TertiaryKey     // Catch:{ all -> 0x005d }
            r6.<init>(r14, r2, r4)     // Catch:{ all -> 0x005d }
            r12.put(r2, r6)     // Catch:{ all -> 0x005d }
            goto L_0x0032
        L_0x0054:
            r2 = 0
            $closeResource(r2, r1)
            java.util.Map r1 = java.util.Collections.unmodifiableMap(r12)
            return r1
        L_0x005d:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x005f }
        L_0x005f:
            r4 = move-exception
            if (r1 == 0) goto L_0x0065
            $closeResource(r2, r1)
        L_0x0065:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.encryption.storage.TertiaryKeysTable.getAllKeys(java.lang.String):java.util.Map");
    }
}
