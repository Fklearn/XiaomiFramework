package com.android.server.locksettings.recoverablekeystore.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.locksettings.recoverablekeystore.TestOnlyInsecureCertificateHelper;
import com.android.server.locksettings.recoverablekeystore.WrappedKey;
import com.android.server.net.watchlist.WatchlistLoggingHandler;
import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.IntConsumer;

public class RecoverableKeyStoreDb {
    private static final String CERT_PATH_ENCODING = "PkiPath";
    private static final int IDLE_TIMEOUT_SECONDS = 30;
    private static final int LAST_SYNCED_AT_UNSYNCED = -1;
    private static final String TAG = "RecoverableKeyStoreDb";
    private final RecoverableKeyStoreDbHelper mKeyStoreDbHelper;
    private final TestOnlyInsecureCertificateHelper mTestOnlyInsecureCertificateHelper = new TestOnlyInsecureCertificateHelper();

    public static RecoverableKeyStoreDb newInstance(Context context) {
        RecoverableKeyStoreDbHelper helper = new RecoverableKeyStoreDbHelper(context);
        helper.setWriteAheadLoggingEnabled(true);
        helper.setIdleConnectionTimeout(30);
        return new RecoverableKeyStoreDb(helper);
    }

    private RecoverableKeyStoreDb(RecoverableKeyStoreDbHelper keyStoreDbHelper) {
        this.mKeyStoreDbHelper = keyStoreDbHelper;
    }

    public long insertKey(int userId, int uid, String alias, WrappedKey wrappedKey) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", Integer.valueOf(userId));
        values.put(WatchlistLoggingHandler.WatchlistEventKeys.UID, Integer.valueOf(uid));
        values.put("alias", alias);
        values.put("nonce", wrappedKey.getNonce());
        values.put("wrapped_key", wrappedKey.getKeyMaterial());
        values.put("last_synced_at", -1);
        values.put("platform_key_generation_id", Integer.valueOf(wrappedKey.getPlatformKeyGenerationId()));
        values.put("recovery_status", Integer.valueOf(wrappedKey.getRecoveryStatus()));
        byte[] keyMetadata = wrappedKey.getKeyMetadata();
        if (keyMetadata == null) {
            values.putNull("key_metadata");
        } else {
            values.put("key_metadata", keyMetadata);
        }
        return db.replace("keys", (String) null, values);
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    public WrappedKey getKey(int uid, String alias) {
        Throwable th;
        byte[] keyMetadata;
        SQLiteDatabase db = this.mKeyStoreDbHelper.getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query("keys", new String[]{"_id", "nonce", "wrapped_key", "platform_key_generation_id", "recovery_status", "key_metadata"}, "uid = ? AND alias = ?", new String[]{Integer.toString(uid), alias}, (String) null, (String) null, (String) null);
        try {
            int count = cursor.getCount();
            if (count == 0) {
                $closeResource((Throwable) null, cursor);
                return null;
            } else if (count > 1) {
                Log.wtf(TAG, String.format(Locale.US, "%d WrappedKey entries found for uid=%d alias='%s'. Should only ever be 0 or 1.", new Object[]{Integer.valueOf(count), Integer.valueOf(uid), alias}));
                $closeResource((Throwable) null, cursor);
                return null;
            } else {
                cursor.moveToFirst();
                byte[] nonce = cursor.getBlob(cursor.getColumnIndexOrThrow("nonce"));
                byte[] keyMaterial = cursor.getBlob(cursor.getColumnIndexOrThrow("wrapped_key"));
                int generationId = cursor.getInt(cursor.getColumnIndexOrThrow("platform_key_generation_id"));
                int recoveryStatus = cursor.getInt(cursor.getColumnIndexOrThrow("recovery_status"));
                int metadataIdx = cursor.getColumnIndexOrThrow("key_metadata");
                if (cursor.isNull(metadataIdx)) {
                    keyMetadata = null;
                } else {
                    keyMetadata = cursor.getBlob(metadataIdx);
                }
                WrappedKey wrappedKey = new WrappedKey(nonce, keyMaterial, keyMetadata, generationId, recoveryStatus);
                $closeResource((Throwable) null, cursor);
                return wrappedKey;
            }
        } catch (Throwable th2) {
            Throwable th3 = th2;
            if (cursor != null) {
                $closeResource(th, cursor);
            }
            throw th3;
        }
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

    public boolean removeKey(int uid, String alias) {
        if (this.mKeyStoreDbHelper.getWritableDatabase().delete("keys", "uid = ? AND alias = ?", new String[]{Integer.toString(uid), alias}) > 0) {
            return true;
        }
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0059, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x005a, code lost:
        if (r1 != null) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x005c, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005f, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.Map<java.lang.String, java.lang.Integer> getStatusForAllKeys(int r13) {
        /*
            r12 = this;
            com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDbHelper r0 = r12.mKeyStoreDbHelper
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            java.lang.String r9 = "recovery_status"
            java.lang.String r10 = "alias"
            java.lang.String r1 = "_id"
            java.lang.String[] r3 = new java.lang.String[]{r1, r10, r9}
            java.lang.String r11 = "uid = ?"
            r1 = 1
            java.lang.String[] r5 = new java.lang.String[r1]
            java.lang.String r1 = java.lang.Integer.toString(r13)
            r2 = 0
            r5[r2] = r1
            java.lang.String r2 = "keys"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r11
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ all -> 0x0057 }
            r2.<init>()     // Catch:{ all -> 0x0057 }
        L_0x0030:
            boolean r4 = r1.moveToNext()     // Catch:{ all -> 0x0057 }
            if (r4 == 0) goto L_0x0051
            int r4 = r1.getColumnIndexOrThrow(r10)     // Catch:{ all -> 0x0057 }
            java.lang.String r4 = r1.getString(r4)     // Catch:{ all -> 0x0057 }
            int r6 = r1.getColumnIndexOrThrow(r9)     // Catch:{ all -> 0x0057 }
            int r6 = r1.getInt(r6)     // Catch:{ all -> 0x0057 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0057 }
            r2.put(r4, r7)     // Catch:{ all -> 0x0057 }
            goto L_0x0030
        L_0x0051:
            r4 = 0
            $closeResource(r4, r1)
            return r2
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb.getStatusForAllKeys(int):java.util.Map");
    }

    public int setRecoveryStatus(int uid, String alias, int status) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recovery_status", Integer.valueOf(status));
        return db.update("keys", values, "uid = ? AND alias = ?", new String[]{String.valueOf(uid), alias});
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    public Map<String, WrappedKey> getAllKeys(int userId, int recoveryAgentUid, int platformKeyGenerationId) {
        Throwable th;
        byte[] keyMetadata;
        SQLiteDatabase db = this.mKeyStoreDbHelper.getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query("keys", new String[]{"_id", "nonce", "wrapped_key", "alias", "recovery_status", "key_metadata"}, "user_id = ? AND uid = ? AND platform_key_generation_id = ?", new String[]{Integer.toString(userId), Integer.toString(recoveryAgentUid), Integer.toString(platformKeyGenerationId)}, (String) null, (String) null, (String) null);
        try {
            HashMap<String, WrappedKey> keys = new HashMap<>();
            while (cursor.moveToNext()) {
                byte[] nonce = cursor.getBlob(cursor.getColumnIndexOrThrow("nonce"));
                byte[] keyMaterial = cursor.getBlob(cursor.getColumnIndexOrThrow("wrapped_key"));
                String alias = cursor.getString(cursor.getColumnIndexOrThrow("alias"));
                int recoveryStatus = cursor.getInt(cursor.getColumnIndexOrThrow("recovery_status"));
                int metadataIdx = cursor.getColumnIndexOrThrow("key_metadata");
                if (cursor.isNull(metadataIdx)) {
                    keyMetadata = null;
                } else {
                    keyMetadata = cursor.getBlob(metadataIdx);
                }
                keys.put(alias, new WrappedKey(nonce, keyMaterial, keyMetadata, platformKeyGenerationId, recoveryStatus));
            }
            $closeResource((Throwable) null, cursor);
            return keys;
        } catch (Throwable th2) {
            Throwable th3 = th2;
            if (cursor != null) {
                $closeResource(th, cursor);
            }
            throw th3;
        }
    }

    public long setPlatformKeyGenerationId(int userId, int generationId) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", Integer.valueOf(userId));
        values.put("platform_key_generation_id", Integer.valueOf(generationId));
        String[] selectionArguments = {String.valueOf(userId)};
        ensureUserMetadataEntryExists(userId);
        invalidateKeysForUser(userId);
        return (long) db.update("user_metadata", values, "user_id = ?", selectionArguments);
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0053, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0054, code lost:
        if (r1 != null) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0056, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0059, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.Map<java.lang.Integer, java.lang.Long> getUserSerialNumbers() {
        /*
            r13 = this;
            com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDbHelper r0 = r13.mKeyStoreDbHelper
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            java.lang.String r9 = "user_serial_number"
            java.lang.String r10 = "user_id"
            java.lang.String[] r3 = new java.lang.String[]{r10, r9}
            r11 = 0
            r1 = 0
            java.lang.String[] r12 = new java.lang.String[r1]
            java.lang.String r2 = "user_metadata"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r11
            r5 = r12
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            android.util.ArrayMap r2 = new android.util.ArrayMap     // Catch:{ all -> 0x0051 }
            r2.<init>()     // Catch:{ all -> 0x0051 }
        L_0x0027:
            boolean r4 = r1.moveToNext()     // Catch:{ all -> 0x0051 }
            if (r4 == 0) goto L_0x004b
            int r4 = r1.getColumnIndexOrThrow(r10)     // Catch:{ all -> 0x0051 }
            int r4 = r1.getInt(r4)     // Catch:{ all -> 0x0051 }
            int r5 = r1.getColumnIndexOrThrow(r9)     // Catch:{ all -> 0x0051 }
            long r5 = r1.getLong(r5)     // Catch:{ all -> 0x0051 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0051 }
            java.lang.Long r8 = java.lang.Long.valueOf(r5)     // Catch:{ all -> 0x0051 }
            r2.put(r7, r8)     // Catch:{ all -> 0x0051 }
            goto L_0x0027
        L_0x004b:
            r4 = 0
            $closeResource(r4, r1)
            return r2
        L_0x0051:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0053 }
        L_0x0053:
            r4 = move-exception
            if (r1 == 0) goto L_0x0059
            $closeResource(r2, r1)
        L_0x0059:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb.getUserSerialNumbers():java.util.Map");
    }

    public long setUserSerialNumber(int userId, long serialNumber) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", Integer.valueOf(userId));
        values.put("user_serial_number", Long.valueOf(serialNumber));
        String[] selectionArguments = {String.valueOf(userId)};
        ensureUserMetadataEntryExists(userId);
        return (long) db.update("user_metadata", values, "user_id = ?", selectionArguments);
    }

    public void invalidateKeysForUser(int userId) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recovery_status", 3);
        db.update("keys", values, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public void invalidateKeysForUserIdOnCustomScreenLock(int userId) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recovery_status", 3);
        db.update("keys", values, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0045, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0046, code lost:
        if (r1 != null) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0048, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004b, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getPlatformKeyGenerationId(int r12) {
        /*
            r11 = this;
            com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDbHelper r0 = r11.mKeyStoreDbHelper
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            java.lang.String r9 = "platform_key_generation_id"
            java.lang.String[] r3 = new java.lang.String[]{r9}
            java.lang.String r10 = "user_id = ?"
            r1 = 1
            java.lang.String[] r5 = new java.lang.String[r1]
            java.lang.String r1 = java.lang.Integer.toString(r12)
            r2 = 0
            r5[r2] = r1
            java.lang.String r2 = "user_metadata"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r10
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            int r2 = r1.getCount()     // Catch:{ all -> 0x0043 }
            r4 = 0
            if (r2 != 0) goto L_0x0033
            r2 = -1
            $closeResource(r4, r1)
            return r2
        L_0x0033:
            r1.moveToFirst()     // Catch:{ all -> 0x0043 }
            int r2 = r1.getColumnIndexOrThrow(r9)     // Catch:{ all -> 0x0043 }
            int r2 = r1.getInt(r2)     // Catch:{ all -> 0x0043 }
            $closeResource(r4, r1)
            return r2
        L_0x0043:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0045 }
        L_0x0045:
            r4 = move-exception
            if (r1 == 0) goto L_0x004b
            $closeResource(r2, r1)
        L_0x004b:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb.getPlatformKeyGenerationId(int):int");
    }

    public long setRecoveryServicePublicKey(int userId, int uid, PublicKey publicKey) {
        return setBytes(userId, uid, "public_key", publicKey.getEncoded());
    }

    public Long getRecoveryServiceCertSerial(int userId, int uid, String rootAlias) {
        return getLong(userId, uid, rootAlias, "cert_serial");
    }

    public long setRecoveryServiceCertSerial(int userId, int uid, String rootAlias, long serial) {
        return setLong(userId, uid, rootAlias, "cert_serial", serial);
    }

    public CertPath getRecoveryServiceCertPath(int userId, int uid, String rootAlias) {
        byte[] bytes = getBytes(userId, uid, rootAlias, "cert_path");
        if (bytes == null) {
            return null;
        }
        try {
            return decodeCertPath(bytes);
        } catch (CertificateException e) {
            Log.wtf(TAG, String.format(Locale.US, "Recovery service CertPath entry cannot be decoded for userId=%d uid=%d.", new Object[]{Integer.valueOf(userId), Integer.valueOf(uid)}), e);
            return null;
        }
    }

    public long setRecoveryServiceCertPath(int userId, int uid, String rootAlias, CertPath certPath) throws CertificateEncodingException {
        if (certPath.getCertificates().size() != 0) {
            return setBytes(userId, uid, rootAlias, "cert_path", certPath.getEncoded(CERT_PATH_ENCODING));
        }
        throw new CertificateEncodingException("No certificate contained in the cert path.");
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0050, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0051, code lost:
        if (r1 != null) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0053, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0056, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<java.lang.Integer> getRecoveryAgents(int r12) {
        /*
            r11 = this;
            com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDbHelper r0 = r11.mKeyStoreDbHelper
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            java.lang.String r9 = "uid"
            java.lang.String[] r3 = new java.lang.String[]{r9}
            java.lang.String r10 = "user_id = ?"
            r1 = 1
            java.lang.String[] r5 = new java.lang.String[r1]
            java.lang.String r1 = java.lang.Integer.toString(r12)
            r2 = 0
            r5[r2] = r1
            java.lang.String r2 = "recovery_service_metadata"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r10
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            int r2 = r1.getCount()     // Catch:{ all -> 0x004e }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x004e }
            r4.<init>(r2)     // Catch:{ all -> 0x004e }
        L_0x0030:
            boolean r6 = r1.moveToNext()     // Catch:{ all -> 0x004e }
            if (r6 == 0) goto L_0x0048
            int r6 = r1.getColumnIndexOrThrow(r9)     // Catch:{ all -> 0x004e }
            int r6 = r1.getInt(r6)     // Catch:{ all -> 0x004e }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x004e }
            r4.add(r7)     // Catch:{ all -> 0x004e }
            goto L_0x0030
        L_0x0048:
            r6 = 0
            $closeResource(r6, r1)
            return r4
        L_0x004e:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0050 }
        L_0x0050:
            r4 = move-exception
            if (r1 == 0) goto L_0x0056
            $closeResource(r2, r1)
        L_0x0056:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb.getRecoveryAgents(int):java.util.List");
    }

    public PublicKey getRecoveryServicePublicKey(int userId, int uid) {
        byte[] keyBytes = getBytes(userId, uid, "public_key");
        if (keyBytes == null) {
            return null;
        }
        try {
            return decodeX509Key(keyBytes);
        } catch (InvalidKeySpecException e) {
            Log.wtf(TAG, String.format(Locale.US, "Recovery service public key entry cannot be decoded for userId=%d uid=%d.", new Object[]{Integer.valueOf(userId), Integer.valueOf(uid)}));
            return null;
        }
    }

    public long setRecoverySecretTypes(int userId, int uid, int[] secretTypes) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        StringJoiner joiner = new StringJoiner(",");
        Arrays.stream(secretTypes).forEach(new IntConsumer(joiner) {
            private final /* synthetic */ StringJoiner f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(int i) {
                this.f$0.add(Integer.toString(i));
            }
        });
        values.put("secret_types", joiner.toString());
        ensureRecoveryServiceMetadataEntryExists(userId, uid);
        return (long) db.update("recovery_service_metadata", values, "user_id = ? AND uid = ?", new String[]{String.valueOf(userId), String.valueOf(uid)});
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    public int[] getRecoverySecretTypes(int userId, int uid) {
        Cursor cursor = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"_id", "user_id", WatchlistLoggingHandler.WatchlistEventKeys.UID, "secret_types"}, "user_id = ? AND uid = ?", new String[]{Integer.toString(userId), Integer.toString(uid)}, (String) null, (String) null, (String) null);
        try {
            int count = cursor.getCount();
            if (count == 0) {
                int[] iArr = new int[0];
                $closeResource((Throwable) null, cursor);
                return iArr;
            } else if (count > 1) {
                Log.wtf(TAG, String.format(Locale.US, "%d deviceId entries found for userId=%d uid=%d. Should only ever be 0 or 1.", new Object[]{Integer.valueOf(count), Integer.valueOf(userId), Integer.valueOf(uid)}));
                int[] iArr2 = new int[0];
                $closeResource((Throwable) null, cursor);
                return iArr2;
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndexOrThrow("secret_types");
                if (cursor.isNull(idx)) {
                    int[] iArr3 = new int[0];
                    $closeResource((Throwable) null, cursor);
                    return iArr3;
                }
                String csv = cursor.getString(idx);
                if (TextUtils.isEmpty(csv)) {
                    int[] iArr4 = new int[0];
                    $closeResource((Throwable) null, cursor);
                    return iArr4;
                }
                String[] types = csv.split(",");
                int[] result = new int[types.length];
                for (int i = 0; i < types.length; i++) {
                    result[i] = Integer.parseInt(types[i]);
                }
                $closeResource((Throwable) null, cursor);
                return result;
            }
        } catch (NumberFormatException e) {
            Log.wtf(TAG, "String format error " + e);
        } catch (Throwable th) {
            Throwable th2 = th;
            try {
                throw th2;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                if (cursor != null) {
                    $closeResource(th2, cursor);
                }
                throw th4;
            }
        }
    }

    public long setActiveRootOfTrust(int userId, int uid, String rootAlias) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active_root_of_trust", rootAlias);
        ensureRecoveryServiceMetadataEntryExists(userId, uid);
        return (long) db.update("recovery_service_metadata", values, "user_id = ? AND uid = ?", new String[]{String.valueOf(userId), String.valueOf(uid)});
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    public String getActiveRootOfTrust(int userId, int uid) {
        Throwable th;
        Cursor cursor = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"_id", "user_id", WatchlistLoggingHandler.WatchlistEventKeys.UID, "active_root_of_trust"}, "user_id = ? AND uid = ?", new String[]{Integer.toString(userId), Integer.toString(uid)}, (String) null, (String) null, (String) null);
        try {
            int count = cursor.getCount();
            if (count == 0) {
                $closeResource((Throwable) null, cursor);
                return null;
            } else if (count > 1) {
                Log.wtf(TAG, String.format(Locale.US, "%d deviceId entries found for userId=%d uid=%d. Should only ever be 0 or 1.", new Object[]{Integer.valueOf(count), Integer.valueOf(userId), Integer.valueOf(uid)}));
                $closeResource((Throwable) null, cursor);
                return null;
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndexOrThrow("active_root_of_trust");
                if (cursor.isNull(idx)) {
                    $closeResource((Throwable) null, cursor);
                    return null;
                }
                String result = cursor.getString(idx);
                if (TextUtils.isEmpty(result)) {
                    $closeResource((Throwable) null, cursor);
                    return null;
                }
                $closeResource((Throwable) null, cursor);
                return result;
            }
        } catch (Throwable th2) {
            Throwable th3 = th2;
            if (cursor != null) {
                $closeResource(th, cursor);
            }
            throw th3;
        }
    }

    public long setCounterId(int userId, int uid, long counterId) {
        return setLong(userId, uid, "counter_id", counterId);
    }

    public Long getCounterId(int userId, int uid) {
        return getLong(userId, uid, "counter_id");
    }

    public long setServerParams(int userId, int uid, byte[] serverParams) {
        return setBytes(userId, uid, "server_params", serverParams);
    }

    public byte[] getServerParams(int userId, int uid) {
        return getBytes(userId, uid, "server_params");
    }

    public long setSnapshotVersion(int userId, int uid, long snapshotVersion) {
        return setLong(userId, uid, "snapshot_version", snapshotVersion);
    }

    public Long getSnapshotVersion(int userId, int uid) {
        return getLong(userId, uid, "snapshot_version");
    }

    public long setShouldCreateSnapshot(int userId, int uid, boolean pending) {
        return setLong(userId, uid, "should_create_snapshot", pending ? 1 : 0);
    }

    public boolean getShouldCreateSnapshot(int userId, int uid) {
        Long res = getLong(userId, uid, "should_create_snapshot");
        return (res == null || res.longValue() == 0) ? false : true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    private Long getLong(int userId, int uid, String key) {
        Throwable th;
        String str = key;
        Cursor cursor = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"_id", "user_id", WatchlistLoggingHandler.WatchlistEventKeys.UID, str}, "user_id = ? AND uid = ?", new String[]{Integer.toString(userId), Integer.toString(uid)}, (String) null, (String) null, (String) null);
        try {
            int count = cursor.getCount();
            if (count == 0) {
                $closeResource((Throwable) null, cursor);
                return null;
            } else if (count > 1) {
                Log.wtf(TAG, String.format(Locale.US, "%d entries found for userId=%d uid=%d. Should only ever be 0 or 1.", new Object[]{Integer.valueOf(count), Integer.valueOf(userId), Integer.valueOf(uid)}));
                $closeResource((Throwable) null, cursor);
                return null;
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndexOrThrow(str);
                if (cursor.isNull(idx)) {
                    $closeResource((Throwable) null, cursor);
                    return null;
                }
                Long valueOf = Long.valueOf(cursor.getLong(idx));
                $closeResource((Throwable) null, cursor);
                return valueOf;
            }
        } catch (Throwable th2) {
            Throwable th3 = th2;
            if (cursor != null) {
                $closeResource(th, cursor);
            }
            throw th3;
        }
    }

    private long setLong(int userId, int uid, String key, long value) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key, Long.valueOf(value));
        String[] selectionArguments = {Integer.toString(userId), Integer.toString(uid)};
        ensureRecoveryServiceMetadataEntryExists(userId, uid);
        return (long) db.update("recovery_service_metadata", values, "user_id = ? AND uid = ?", selectionArguments);
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    private byte[] getBytes(int userId, int uid, String key) {
        Throwable th;
        String str = key;
        Cursor cursor = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"_id", "user_id", WatchlistLoggingHandler.WatchlistEventKeys.UID, str}, "user_id = ? AND uid = ?", new String[]{Integer.toString(userId), Integer.toString(uid)}, (String) null, (String) null, (String) null);
        try {
            int count = cursor.getCount();
            if (count == 0) {
                $closeResource((Throwable) null, cursor);
                return null;
            } else if (count > 1) {
                Log.wtf(TAG, String.format(Locale.US, "%d entries found for userId=%d uid=%d. Should only ever be 0 or 1.", new Object[]{Integer.valueOf(count), Integer.valueOf(userId), Integer.valueOf(uid)}));
                $closeResource((Throwable) null, cursor);
                return null;
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndexOrThrow(str);
                if (cursor.isNull(idx)) {
                    $closeResource((Throwable) null, cursor);
                    return null;
                }
                byte[] blob = cursor.getBlob(idx);
                $closeResource((Throwable) null, cursor);
                return blob;
            }
        } catch (Throwable th2) {
            Throwable th3 = th2;
            if (cursor != null) {
                $closeResource(th, cursor);
            }
            throw th3;
        }
    }

    private long setBytes(int userId, int uid, String key, byte[] value) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key, value);
        String[] selectionArguments = {Integer.toString(userId), Integer.toString(uid)};
        ensureRecoveryServiceMetadataEntryExists(userId, uid);
        return (long) db.update("recovery_service_metadata", values, "user_id = ? AND uid = ?", selectionArguments);
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    private byte[] getBytes(int userId, int uid, String rootAlias, String key) {
        Throwable th;
        String str = key;
        String rootAlias2 = this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(rootAlias);
        Cursor cursor = this.mKeyStoreDbHelper.getReadableDatabase().query("root_of_trust", new String[]{"_id", "user_id", WatchlistLoggingHandler.WatchlistEventKeys.UID, "root_alias", str}, "user_id = ? AND uid = ? AND root_alias = ?", new String[]{Integer.toString(userId), Integer.toString(uid), rootAlias2}, (String) null, (String) null, (String) null);
        try {
            int count = cursor.getCount();
            if (count == 0) {
                $closeResource((Throwable) null, cursor);
                return null;
            } else if (count > 1) {
                Log.wtf(TAG, String.format(Locale.US, "%d entries found for userId=%d uid=%d. Should only ever be 0 or 1.", new Object[]{Integer.valueOf(count), Integer.valueOf(userId), Integer.valueOf(uid)}));
                $closeResource((Throwable) null, cursor);
                return null;
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndexOrThrow(str);
                if (cursor.isNull(idx)) {
                    $closeResource((Throwable) null, cursor);
                    return null;
                }
                byte[] blob = cursor.getBlob(idx);
                $closeResource((Throwable) null, cursor);
                return blob;
            }
        } catch (Throwable th2) {
            Throwable th3 = th2;
            if (cursor != null) {
                $closeResource(th, cursor);
            }
            throw th3;
        }
    }

    private long setBytes(int userId, int uid, String rootAlias, String key, byte[] value) {
        String rootAlias2 = this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(rootAlias);
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key, value);
        String[] selectionArguments = {Integer.toString(userId), Integer.toString(uid), rootAlias2};
        ensureRootOfTrustEntryExists(userId, uid, rootAlias2);
        return (long) db.update("root_of_trust", values, "user_id = ? AND uid = ? AND root_alias = ?", selectionArguments);
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    private Long getLong(int userId, int uid, String rootAlias, String key) {
        Throwable th;
        String str = key;
        String rootAlias2 = this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(rootAlias);
        Cursor cursor = this.mKeyStoreDbHelper.getReadableDatabase().query("root_of_trust", new String[]{"_id", "user_id", WatchlistLoggingHandler.WatchlistEventKeys.UID, "root_alias", str}, "user_id = ? AND uid = ? AND root_alias = ?", new String[]{Integer.toString(userId), Integer.toString(uid), rootAlias2}, (String) null, (String) null, (String) null);
        try {
            int count = cursor.getCount();
            if (count == 0) {
                $closeResource((Throwable) null, cursor);
                return null;
            } else if (count > 1) {
                Log.wtf(TAG, String.format(Locale.US, "%d entries found for userId=%d uid=%d. Should only ever be 0 or 1.", new Object[]{Integer.valueOf(count), Integer.valueOf(userId), Integer.valueOf(uid)}));
                $closeResource((Throwable) null, cursor);
                return null;
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndexOrThrow(str);
                if (cursor.isNull(idx)) {
                    $closeResource((Throwable) null, cursor);
                    return null;
                }
                Long valueOf = Long.valueOf(cursor.getLong(idx));
                $closeResource((Throwable) null, cursor);
                return valueOf;
            }
        } catch (Throwable th2) {
            Throwable th3 = th2;
            if (cursor != null) {
                $closeResource(th, cursor);
            }
            throw th3;
        }
    }

    private long setLong(int userId, int uid, String rootAlias, String key, long value) {
        String rootAlias2 = this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(rootAlias);
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key, Long.valueOf(value));
        String[] selectionArguments = {Integer.toString(userId), Integer.toString(uid), rootAlias2};
        ensureRootOfTrustEntryExists(userId, uid, rootAlias2);
        return (long) db.update("root_of_trust", values, "user_id = ? AND uid = ? AND root_alias = ?", selectionArguments);
    }

    public void removeUserFromAllTables(int userId) {
        removeUserFromKeysTable(userId);
        removeUserFromUserMetadataTable(userId);
        removeUserFromRecoveryServiceMetadataTable(userId);
        removeUserFromRootOfTrustTable(userId);
    }

    private boolean removeUserFromKeysTable(int userId) {
        if (this.mKeyStoreDbHelper.getWritableDatabase().delete("keys", "user_id = ?", new String[]{Integer.toString(userId)}) > 0) {
            return true;
        }
        return false;
    }

    private boolean removeUserFromUserMetadataTable(int userId) {
        if (this.mKeyStoreDbHelper.getWritableDatabase().delete("user_metadata", "user_id = ?", new String[]{Integer.toString(userId)}) > 0) {
            return true;
        }
        return false;
    }

    private boolean removeUserFromRecoveryServiceMetadataTable(int userId) {
        if (this.mKeyStoreDbHelper.getWritableDatabase().delete("recovery_service_metadata", "user_id = ?", new String[]{Integer.toString(userId)}) > 0) {
            return true;
        }
        return false;
    }

    private boolean removeUserFromRootOfTrustTable(int userId) {
        if (this.mKeyStoreDbHelper.getWritableDatabase().delete("root_of_trust", "user_id = ?", new String[]{Integer.toString(userId)}) > 0) {
            return true;
        }
        return false;
    }

    private void ensureRecoveryServiceMetadataEntryExists(int userId, int uid) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", Integer.valueOf(userId));
        values.put(WatchlistLoggingHandler.WatchlistEventKeys.UID, Integer.valueOf(uid));
        db.insertWithOnConflict("recovery_service_metadata", (String) null, values, 4);
    }

    private void ensureRootOfTrustEntryExists(int userId, int uid, String rootAlias) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", Integer.valueOf(userId));
        values.put(WatchlistLoggingHandler.WatchlistEventKeys.UID, Integer.valueOf(uid));
        values.put("root_alias", rootAlias);
        db.insertWithOnConflict("root_of_trust", (String) null, values, 4);
    }

    private void ensureUserMetadataEntryExists(int userId) {
        SQLiteDatabase db = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", Integer.valueOf(userId));
        db.insertWithOnConflict("user_metadata", (String) null, values, 4);
    }

    public void close() {
        this.mKeyStoreDbHelper.close();
    }

    private static PublicKey decodeX509Key(byte[] keyBytes) throws InvalidKeySpecException {
        try {
            return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static CertPath decodeCertPath(byte[] bytes) throws CertificateException {
        try {
            return CertificateFactory.getInstance("X.509").generateCertPath(new ByteArrayInputStream(bytes), CERT_PATH_ENCODING);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }
}
