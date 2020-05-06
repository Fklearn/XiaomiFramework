package com.android.server.locksettings;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.UserInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.PersistentDataBlockManagerInternal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class LockSettingsStorage {
    private static final String BASE_ZERO_LOCK_PATTERN_FILE = "gatekeeper.gesture.key";
    private static final String CHILD_PROFILE_LOCK_FILE = "gatekeeper.profile.key";
    private static final String[] COLUMNS_FOR_PREFETCH = {"name", COLUMN_VALUE};
    private static final String[] COLUMNS_FOR_QUERY = {COLUMN_VALUE};
    private static final String COLUMN_KEY = "name";
    private static final String COLUMN_USERID = "user";
    private static final String COLUMN_VALUE = "value";
    private static final boolean DEBUG = false;
    /* access modifiers changed from: private */
    public static final Object DEFAULT = new Object();
    private static final String LEGACY_LOCK_PASSWORD_FILE = "password.key";
    private static final String LEGACY_LOCK_PATTERN_FILE = "gesture.key";
    private static final String LOCK_PASSWORD_FILE = "gatekeeper.password.key";
    private static final String LOCK_PATTERN_FILE = "gatekeeper.pattern.key";
    private static final String SYNTHETIC_PASSWORD_DIRECTORY = "spblob/";
    private static final String SYSTEM_DIRECTORY = "/system/";
    private static final String TABLE = "locksettings";
    private static final String TAG = "LockSettingsStorage";
    private final Cache mCache = new Cache();
    private final Context mContext;
    private final Object mFileWriteLock = new Object();
    private final DatabaseHelper mOpenHelper;
    private PersistentDataBlockManagerInternal mPersistentDataBlockManagerInternal;

    public interface Callback {
        void initialize(SQLiteDatabase sQLiteDatabase);
    }

    @VisibleForTesting
    public static class CredentialHash {
        static final int VERSION_GATEKEEPER = 1;
        static final int VERSION_LEGACY = 0;
        byte[] hash;
        boolean isBaseZeroPattern;
        int type;
        int version;

        private CredentialHash(byte[] hash2, int type2, int version2) {
            this(hash2, type2, version2, false);
        }

        private CredentialHash(byte[] hash2, int type2, int version2, boolean isBaseZeroPattern2) {
            if (type2 != -1) {
                if (hash2 == null) {
                    throw new RuntimeException("Empty hash for CredentialHash");
                }
            } else if (hash2 != null) {
                throw new RuntimeException("None type CredentialHash should not have hash");
            }
            this.hash = hash2;
            this.type = type2;
            this.version = version2;
            this.isBaseZeroPattern = isBaseZeroPattern2;
        }

        /* access modifiers changed from: private */
        public static CredentialHash createBaseZeroPattern(byte[] hash2) {
            return new CredentialHash(hash2, 1, 1, true);
        }

        static CredentialHash create(byte[] hash2, int type2) {
            if (type2 != -1) {
                return new CredentialHash(hash2, type2, 1);
            }
            throw new RuntimeException("Bad type for CredentialHash");
        }

        static CredentialHash createEmptyHash() {
            return new CredentialHash((byte[]) null, -1, 1);
        }

        public byte[] toBytes() {
            Preconditions.checkState(!this.isBaseZeroPattern, "base zero patterns are not serializable");
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.write(this.version);
                dos.write(this.type);
                if (this.hash == null || this.hash.length <= 0) {
                    dos.writeInt(0);
                } else {
                    dos.writeInt(this.hash.length);
                    dos.write(this.hash);
                }
                dos.close();
                return os.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static CredentialHash fromBytes(byte[] bytes) {
            try {
                DataInputStream is = new DataInputStream(new ByteArrayInputStream(bytes));
                int version2 = is.read();
                int type2 = is.read();
                int hashSize = is.readInt();
                byte[] hash2 = null;
                if (hashSize > 0) {
                    hash2 = new byte[hashSize];
                    is.readFully(hash2);
                }
                return new CredentialHash(hash2, type2, version2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public LockSettingsStorage(Context context) {
        this.mContext = context;
        this.mOpenHelper = new DatabaseHelper(context);
    }

    public void setDatabaseOnCreateCallback(Callback callback) {
        this.mOpenHelper.setCallback(callback);
    }

    public void writeKeyValue(String key, String value, int userId) {
        writeKeyValue(this.mOpenHelper.getWritableDatabase(), key, value, userId);
    }

    public void writeKeyValue(SQLiteDatabase db, String key, String value, int userId) {
        ContentValues cv = new ContentValues();
        cv.put("name", key);
        cv.put(COLUMN_USERID, Integer.valueOf(userId));
        cv.put(COLUMN_VALUE, value);
        db.beginTransaction();
        try {
            db.delete(TABLE, "name=? AND user=?", new String[]{key, Integer.toString(userId)});
            db.insert(TABLE, (String) null, cv);
            db.setTransactionSuccessful();
            this.mCache.putKeyValue(key, value, userId);
        } finally {
            db.endTransaction();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        r0 = DEFAULT;
        r3 = r12.mOpenHelper.getReadableDatabase().query(TABLE, COLUMNS_FOR_QUERY, "user=? AND name=?", new java.lang.String[]{java.lang.Integer.toString(r15), r13}, (java.lang.String) null, (java.lang.String) null, (java.lang.String) null);
        r4 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0040, code lost:
        if (r3 == null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0046, code lost:
        if (r4.moveToFirst() == false) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0048, code lost:
        r0 = r4.getString(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004c, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004f, code lost:
        r12.mCache.putKeyValueIfUnchanged(r13, r0, r15, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0056, code lost:
        if (r0 != DEFAULT) goto L_0x005a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return (java.lang.String) r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        return r14;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String readKeyValue(java.lang.String r13, java.lang.String r14, int r15) {
        /*
            r12 = this;
            com.android.server.locksettings.LockSettingsStorage$Cache r0 = r12.mCache
            monitor-enter(r0)
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r12.mCache     // Catch:{ all -> 0x005e }
            boolean r1 = r1.hasKeyValue(r13, r15)     // Catch:{ all -> 0x005e }
            if (r1 == 0) goto L_0x0013
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r12.mCache     // Catch:{ all -> 0x005e }
            java.lang.String r1 = r1.peekKeyValue(r13, r14, r15)     // Catch:{ all -> 0x005e }
            monitor-exit(r0)     // Catch:{ all -> 0x005e }
            return r1
        L_0x0013:
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r12.mCache     // Catch:{ all -> 0x005e }
            int r1 = r1.getVersion()     // Catch:{ all -> 0x005e }
            monitor-exit(r0)     // Catch:{ all -> 0x005e }
            java.lang.Object r0 = DEFAULT
            com.android.server.locksettings.LockSettingsStorage$DatabaseHelper r2 = r12.mOpenHelper
            android.database.sqlite.SQLiteDatabase r2 = r2.getReadableDatabase()
            java.lang.String[] r5 = COLUMNS_FOR_QUERY
            r3 = 2
            java.lang.String[] r7 = new java.lang.String[r3]
            java.lang.String r3 = java.lang.Integer.toString(r15)
            r11 = 0
            r7[r11] = r3
            r3 = 1
            r7[r3] = r13
            r8 = 0
            r9 = 0
            r10 = 0
            java.lang.String r4 = "locksettings"
            java.lang.String r6 = "user=? AND name=?"
            r3 = r2
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8, r9, r10)
            r4 = r3
            if (r3 == 0) goto L_0x004f
            boolean r3 = r4.moveToFirst()
            if (r3 == 0) goto L_0x004c
            java.lang.String r0 = r4.getString(r11)
        L_0x004c:
            r4.close()
        L_0x004f:
            com.android.server.locksettings.LockSettingsStorage$Cache r3 = r12.mCache
            r3.putKeyValueIfUnchanged(r13, r0, r15, r1)
            java.lang.Object r3 = DEFAULT
            if (r0 != r3) goto L_0x005a
            r3 = r14
            goto L_0x005d
        L_0x005a:
            r3 = r0
            java.lang.String r3 = (java.lang.String) r3
        L_0x005d:
            return r3
        L_0x005e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsStorage.readKeyValue(java.lang.String, java.lang.String, int):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003a, code lost:
        if (r2 == null) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0040, code lost:
        if (r3.moveToNext() == false) goto L_0x0050;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0042, code lost:
        r12.mCache.putKeyValueIfUnchanged(r3.getString(0), r3.getString(1), r13, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0050, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0053, code lost:
        readCredentialHash(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0056, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        r2 = r12.mOpenHelper.getReadableDatabase().query(TABLE, COLUMNS_FOR_PREFETCH, "user=?", new java.lang.String[]{java.lang.Integer.toString(r13)}, (java.lang.String) null, (java.lang.String) null, (java.lang.String) null);
        r3 = r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void prefetchUser(int r13) {
        /*
            r12 = this;
            com.android.server.locksettings.LockSettingsStorage$Cache r0 = r12.mCache
            monitor-enter(r0)
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r12.mCache     // Catch:{ all -> 0x0057 }
            boolean r1 = r1.isFetched(r13)     // Catch:{ all -> 0x0057 }
            if (r1 == 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x0057 }
            return
        L_0x000d:
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r12.mCache     // Catch:{ all -> 0x0057 }
            r1.setFetched(r13)     // Catch:{ all -> 0x0057 }
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r12.mCache     // Catch:{ all -> 0x0057 }
            int r1 = r1.getVersion()     // Catch:{ all -> 0x0057 }
            monitor-exit(r0)     // Catch:{ all -> 0x0057 }
            com.android.server.locksettings.LockSettingsStorage$DatabaseHelper r0 = r12.mOpenHelper
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            java.lang.String[] r4 = COLUMNS_FOR_PREFETCH
            r10 = 1
            java.lang.String[] r6 = new java.lang.String[r10]
            java.lang.String r2 = java.lang.Integer.toString(r13)
            r11 = 0
            r6[r11] = r2
            r7 = 0
            r8 = 0
            r9 = 0
            java.lang.String r3 = "locksettings"
            java.lang.String r5 = "user=?"
            r2 = r0
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7, r8, r9)
            r3 = r2
            if (r2 == 0) goto L_0x0053
        L_0x003c:
            boolean r2 = r3.moveToNext()
            if (r2 == 0) goto L_0x0050
            java.lang.String r2 = r3.getString(r11)
            java.lang.String r4 = r3.getString(r10)
            com.android.server.locksettings.LockSettingsStorage$Cache r5 = r12.mCache
            r5.putKeyValueIfUnchanged(r2, r4, r13, r1)
            goto L_0x003c
        L_0x0050:
            r3.close()
        L_0x0053:
            r12.readCredentialHash(r13)
            return
        L_0x0057:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0057 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsStorage.prefetchUser(int):void");
    }

    private CredentialHash readPasswordHashIfExists(int userId) {
        byte[] stored = readFile(getLockPasswordFilename(userId));
        if (!ArrayUtils.isEmpty(stored)) {
            return new CredentialHash(stored, 2, 1);
        }
        byte[] stored2 = readFile(getLegacyLockPasswordFilename(userId));
        if (!ArrayUtils.isEmpty(stored2)) {
            return new CredentialHash(stored2, 2, 0);
        }
        return null;
    }

    private CredentialHash readPatternHashIfExists(int userId) {
        byte[] stored = readFile(getLockPatternFilename(userId));
        if (!ArrayUtils.isEmpty(stored)) {
            return new CredentialHash(stored, 1, 1);
        }
        byte[] stored2 = readFile(getBaseZeroLockPatternFilename(userId));
        if (!ArrayUtils.isEmpty(stored2)) {
            return CredentialHash.createBaseZeroPattern(stored2);
        }
        byte[] stored3 = readFile(getLegacyLockPatternFilename(userId));
        if (!ArrayUtils.isEmpty(stored3)) {
            return new CredentialHash(stored3, 1, 0);
        }
        return null;
    }

    public CredentialHash readCredentialHash(int userId) {
        CredentialHash passwordHash = readPasswordHashIfExists(userId);
        CredentialHash patternHash = readPatternHashIfExists(userId);
        if (passwordHash == null || patternHash == null) {
            if (passwordHash != null) {
                return passwordHash;
            }
            if (patternHash != null) {
                return patternHash;
            }
            return CredentialHash.createEmptyHash();
        } else if (passwordHash.version == 1) {
            return passwordHash;
        } else {
            return patternHash;
        }
    }

    public void removeChildProfileLock(int userId) {
        try {
            deleteFile(getChildProfileLockFile(userId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeChildProfileLock(int userId, byte[] lock) {
        writeFile(getChildProfileLockFile(userId), lock);
    }

    public byte[] readChildProfileLock(int userId) {
        return readFile(getChildProfileLockFile(userId));
    }

    public boolean hasChildProfileLock(int userId) {
        return hasFile(getChildProfileLockFile(userId));
    }

    public boolean hasPassword(int userId) {
        return hasFile(getLockPasswordFilename(userId)) || hasFile(getLegacyLockPasswordFilename(userId));
    }

    public boolean hasPattern(int userId) {
        return hasFile(getLockPatternFilename(userId)) || hasFile(getBaseZeroLockPatternFilename(userId)) || hasFile(getLegacyLockPatternFilename(userId));
    }

    public boolean hasCredential(int userId) {
        return hasPassword(userId) || hasPattern(userId);
    }

    private boolean hasFile(String name) {
        byte[] contents = readFile(name);
        return contents != null && contents.length > 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        r0 = null;
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r0 = new java.io.RandomAccessFile(r8, com.android.server.wm.ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD);
        r2 = new byte[((int) r0.length())];
        r0.readFully(r2, 0, r2.length);
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003a, code lost:
        r3 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003b, code lost:
        r4 = new java.lang.StringBuilder();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0052, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0054, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        android.util.Slog.e(TAG, "Cannot read file " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006c, code lost:
        if (r0 != null) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0072, code lost:
        r3 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0073, code lost:
        r4 = new java.lang.StringBuilder();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x007f, code lost:
        if (r0 != null) goto L_0x0081;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0085, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0086, code lost:
        android.util.Slog.e(TAG, "Error closing file " + r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x009c, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] readFile(java.lang.String r8) {
        /*
            r7 = this;
            com.android.server.locksettings.LockSettingsStorage$Cache r0 = r7.mCache
            monitor-enter(r0)
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r7.mCache     // Catch:{ all -> 0x009d }
            boolean r1 = r1.hasFile(r8)     // Catch:{ all -> 0x009d }
            if (r1 == 0) goto L_0x0013
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r7.mCache     // Catch:{ all -> 0x009d }
            byte[] r1 = r1.peekFile(r8)     // Catch:{ all -> 0x009d }
            monitor-exit(r0)     // Catch:{ all -> 0x009d }
            return r1
        L_0x0013:
            com.android.server.locksettings.LockSettingsStorage$Cache r1 = r7.mCache     // Catch:{ all -> 0x009d }
            int r1 = r1.getVersion()     // Catch:{ all -> 0x009d }
            monitor-exit(r0)     // Catch:{ all -> 0x009d }
            r0 = 0
            r2 = 0
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x0054 }
            java.lang.String r4 = "r"
            r3.<init>(r8, r4)     // Catch:{ IOException -> 0x0054 }
            r0 = r3
            long r3 = r0.length()     // Catch:{ IOException -> 0x0054 }
            int r3 = (int) r3     // Catch:{ IOException -> 0x0054 }
            byte[] r3 = new byte[r3]     // Catch:{ IOException -> 0x0054 }
            r2 = r3
            r3 = 0
            int r4 = r2.length     // Catch:{ IOException -> 0x0054 }
            r0.readFully(r2, r3, r4)     // Catch:{ IOException -> 0x0054 }
            r0.close()     // Catch:{ IOException -> 0x0054 }
            r0.close()     // Catch:{ IOException -> 0x003a }
        L_0x0039:
            goto L_0x0079
        L_0x003a:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
        L_0x0040:
            java.lang.String r5 = "Error closing file "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "LockSettingsStorage"
            android.util.Slog.e(r5, r4)
            goto L_0x0039
        L_0x0052:
            r3 = move-exception
            goto L_0x007f
        L_0x0054:
            r3 = move-exception
            java.lang.String r4 = "LockSettingsStorage"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0052 }
            r5.<init>()     // Catch:{ all -> 0x0052 }
            java.lang.String r6 = "Cannot read file "
            r5.append(r6)     // Catch:{ all -> 0x0052 }
            r5.append(r3)     // Catch:{ all -> 0x0052 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0052 }
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0052 }
            if (r0 == 0) goto L_0x0079
            r0.close()     // Catch:{ IOException -> 0x0072 }
            goto L_0x0039
        L_0x0072:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            goto L_0x0040
        L_0x0079:
            com.android.server.locksettings.LockSettingsStorage$Cache r3 = r7.mCache
            r3.putFileIfUnchanged(r8, r2, r1)
            return r2
        L_0x007f:
            if (r0 == 0) goto L_0x009c
            r0.close()     // Catch:{ IOException -> 0x0085 }
            goto L_0x009c
        L_0x0085:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Error closing file "
            r5.append(r6)
            r5.append(r4)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "LockSettingsStorage"
            android.util.Slog.e(r6, r5)
        L_0x009c:
            throw r3
        L_0x009d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x009d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsStorage.readFile(java.lang.String):byte[]");
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0027, code lost:
        r3 = TAG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r4 = "Error closing file " + r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x007b, code lost:
        if (r1 != null) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0081, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        android.util.Slog.e(TAG, "Error closing file " + r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0099, code lost:
        throw r2;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:12:0x0022, B:22:0x0044] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeFile(java.lang.String r8, byte[] r9) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mFileWriteLock
            monitor-enter(r0)
            r1 = 0
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x0041 }
            java.lang.String r3 = "rws"
            r2.<init>(r8, r3)     // Catch:{ IOException -> 0x0041 }
            r1 = r2
            if (r9 == 0) goto L_0x0019
            int r2 = r9.length     // Catch:{ IOException -> 0x0041 }
            if (r2 != 0) goto L_0x0013
            goto L_0x0019
        L_0x0013:
            r2 = 0
            int r3 = r9.length     // Catch:{ IOException -> 0x0041 }
            r1.write(r9, r2, r3)     // Catch:{ IOException -> 0x0041 }
            goto L_0x001e
        L_0x0019:
            r2 = 0
            r1.setLength(r2)     // Catch:{ IOException -> 0x0041 }
        L_0x001e:
            r1.close()     // Catch:{ IOException -> 0x0041 }
            r1.close()     // Catch:{ IOException -> 0x0026 }
        L_0x0025:
            goto L_0x0074
        L_0x0026:
            r2 = move-exception
            java.lang.String r3 = "LockSettingsStorage"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x009a }
            r4.<init>()     // Catch:{ all -> 0x009a }
            java.lang.String r5 = "Error closing file "
            r4.append(r5)     // Catch:{ all -> 0x009a }
            r4.append(r2)     // Catch:{ all -> 0x009a }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x009a }
        L_0x003a:
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x009a }
            goto L_0x0074
        L_0x003f:
            r2 = move-exception
            goto L_0x007b
        L_0x0041:
            r2 = move-exception
            java.lang.String r3 = "LockSettingsStorage"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x003f }
            r4.<init>()     // Catch:{ all -> 0x003f }
            java.lang.String r5 = "Error writing to file "
            r4.append(r5)     // Catch:{ all -> 0x003f }
            r4.append(r2)     // Catch:{ all -> 0x003f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x003f }
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x003f }
            if (r1 == 0) goto L_0x0074
            r1.close()     // Catch:{ IOException -> 0x005f }
            goto L_0x0025
        L_0x005f:
            r2 = move-exception
            java.lang.String r3 = "LockSettingsStorage"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x009a }
            r4.<init>()     // Catch:{ all -> 0x009a }
            java.lang.String r5 = "Error closing file "
            r4.append(r5)     // Catch:{ all -> 0x009a }
            r4.append(r2)     // Catch:{ all -> 0x009a }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x009a }
            goto L_0x003a
        L_0x0074:
            com.android.server.locksettings.LockSettingsStorage$Cache r2 = r7.mCache     // Catch:{ all -> 0x009a }
            r2.putFile(r8, r9)     // Catch:{ all -> 0x009a }
            monitor-exit(r0)     // Catch:{ all -> 0x009a }
            return
        L_0x007b:
            if (r1 == 0) goto L_0x0098
            r1.close()     // Catch:{ IOException -> 0x0081 }
            goto L_0x0098
        L_0x0081:
            r3 = move-exception
            java.lang.String r4 = "LockSettingsStorage"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x009a }
            r5.<init>()     // Catch:{ all -> 0x009a }
            java.lang.String r6 = "Error closing file "
            r5.append(r6)     // Catch:{ all -> 0x009a }
            r5.append(r3)     // Catch:{ all -> 0x009a }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x009a }
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x009a }
        L_0x0098:
            throw r2     // Catch:{ all -> 0x009a }
        L_0x009a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x009a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsStorage.writeFile(java.lang.String, byte[]):void");
    }

    private void deleteFile(String name) {
        synchronized (this.mFileWriteLock) {
            File file = new File(name);
            if (file.exists()) {
                file.delete();
                this.mCache.putFile(name, (byte[]) null);
            }
        }
    }

    public void writeCredentialHash(CredentialHash hash, int userId) {
        byte[] patternHash = null;
        byte[] passwordHash = null;
        if (hash.type == 2) {
            passwordHash = hash.hash;
        } else if (hash.type == 1) {
            patternHash = hash.hash;
        }
        writeFile(getLockPasswordFilename(userId), passwordHash);
        writeFile(getLockPatternFilename(userId), patternHash);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public String getLockPatternFilename(int userId) {
        return getLockCredentialFilePathForUser(userId, LOCK_PATTERN_FILE);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public String getLockPasswordFilename(int userId) {
        return getLockCredentialFilePathForUser(userId, LOCK_PASSWORD_FILE);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public String getLegacyLockPatternFilename(int userId) {
        return getLockCredentialFilePathForUser(userId, LEGACY_LOCK_PATTERN_FILE);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public String getLegacyLockPasswordFilename(int userId) {
        return getLockCredentialFilePathForUser(userId, LEGACY_LOCK_PASSWORD_FILE);
    }

    private String getBaseZeroLockPatternFilename(int userId) {
        return getLockCredentialFilePathForUser(userId, BASE_ZERO_LOCK_PATTERN_FILE);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public String getChildProfileLockFile(int userId) {
        return getLockCredentialFilePathForUser(userId, CHILD_PROFILE_LOCK_FILE);
    }

    private String getLockCredentialFilePathForUser(int userId, String basename) {
        String dataSystemDirectory = Environment.getDataDirectory().getAbsolutePath() + SYSTEM_DIRECTORY;
        if (userId != 0) {
            return new File(Environment.getUserSystemDirectory(userId), basename).getAbsolutePath();
        }
        return dataSystemDirectory + basename;
    }

    public void writeSyntheticPasswordState(int userId, long handle, String name, byte[] data) {
        ensureSyntheticPasswordDirectoryForUser(userId);
        writeFile(getSynthenticPasswordStateFilePathForUser(userId, handle, name), data);
    }

    public byte[] readSyntheticPasswordState(int userId, long handle, String name) {
        return readFile(getSynthenticPasswordStateFilePathForUser(userId, handle, name));
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0027, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0030, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deleteSyntheticPasswordState(int r7, long r8, java.lang.String r10) {
        /*
            r6 = this;
            java.lang.String r0 = r6.getSynthenticPasswordStateFilePathForUser(r7, r8, r10)
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            boolean r2 = r1.exists()
            if (r2 == 0) goto L_0x005a
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0033 }
            java.lang.String r3 = "rws"
            r2.<init>(r0, r3)     // Catch:{ Exception -> 0x0033 }
            long r3 = r2.length()     // Catch:{ all -> 0x0025 }
            int r3 = (int) r3     // Catch:{ all -> 0x0025 }
            byte[] r4 = new byte[r3]     // Catch:{ all -> 0x0025 }
            r2.write(r4)     // Catch:{ all -> 0x0025 }
            r2.close()     // Catch:{ Exception -> 0x0033 }
            goto L_0x004b
        L_0x0025:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0027 }
        L_0x0027:
            r4 = move-exception
            r2.close()     // Catch:{ all -> 0x002c }
            goto L_0x0030
        L_0x002c:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ Exception -> 0x0033 }
        L_0x0030:
            throw r4     // Catch:{ Exception -> 0x0033 }
        L_0x0031:
            r2 = move-exception
            goto L_0x0056
        L_0x0033:
            r2 = move-exception
            java.lang.String r3 = "LockSettingsStorage"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0031 }
            r4.<init>()     // Catch:{ all -> 0x0031 }
            java.lang.String r5 = "Failed to zeroize "
            r4.append(r5)     // Catch:{ all -> 0x0031 }
            r4.append(r0)     // Catch:{ all -> 0x0031 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0031 }
            android.util.Slog.w(r3, r4, r2)     // Catch:{ all -> 0x0031 }
        L_0x004b:
            r1.delete()
            com.android.server.locksettings.LockSettingsStorage$Cache r2 = r6.mCache
            r3 = 0
            r2.putFile(r0, r3)
            goto L_0x005a
        L_0x0056:
            r1.delete()
            throw r2
        L_0x005a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsStorage.deleteSyntheticPasswordState(int, long, java.lang.String):void");
    }

    public Map<Integer, List<Long>> listSyntheticPasswordHandlesForAllUsers(String stateName) {
        Map<Integer, List<Long>> result = new ArrayMap<>();
        for (UserInfo user : UserManager.get(this.mContext).getUsers(false)) {
            result.put(Integer.valueOf(user.id), listSyntheticPasswordHandlesForUser(stateName, user.id));
        }
        return result;
    }

    public List<Long> listSyntheticPasswordHandlesForUser(String stateName, int userId) {
        File baseDir = getSyntheticPasswordDirectoryForUser(userId);
        List<Long> result = new ArrayList<>();
        File[] files = baseDir.listFiles();
        if (files == null) {
            return result;
        }
        for (File file : files) {
            String[] parts = file.getName().split("\\.");
            if (parts.length == 2 && parts[1].equals(stateName)) {
                try {
                    result.add(Long.valueOf(Long.parseUnsignedLong(parts[0], 16)));
                } catch (NumberFormatException e) {
                    Slog.e(TAG, "Failed to parse handle " + parts[0]);
                }
            }
        }
        return result;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public File getSyntheticPasswordDirectoryForUser(int userId) {
        return new File(Environment.getDataSystemDeDirectory(userId), SYNTHETIC_PASSWORD_DIRECTORY);
    }

    private void ensureSyntheticPasswordDirectoryForUser(int userId) {
        File baseDir = getSyntheticPasswordDirectoryForUser(userId);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public String getSynthenticPasswordStateFilePathForUser(int userId, long handle, String name) {
        return new File(getSyntheticPasswordDirectoryForUser(userId), String.format("%016x.%s", new Object[]{Long.valueOf(handle), name})).getAbsolutePath();
    }

    public void removeUser(int userId) {
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        if (((UserManager) this.mContext.getSystemService(COLUMN_USERID)).getProfileParent(userId) == null) {
            synchronized (this.mFileWriteLock) {
                String name = getLockPasswordFilename(userId);
                File file = new File(name);
                if (file.exists()) {
                    file.delete();
                    this.mCache.putFile(name, (byte[]) null);
                }
                String name2 = getLockPatternFilename(userId);
                File file2 = new File(name2);
                if (file2.exists()) {
                    file2.delete();
                    this.mCache.putFile(name2, (byte[]) null);
                }
            }
        } else {
            removeChildProfileLock(userId);
        }
        File spStateDir = getSyntheticPasswordDirectoryForUser(userId);
        try {
            db.beginTransaction();
            db.delete(TABLE, "user='" + userId + "'", (String[]) null);
            db.setTransactionSuccessful();
            this.mCache.removeUser(userId);
            this.mCache.purgePath(spStateDir.getAbsolutePath());
        } finally {
            db.endTransaction();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void closeDatabase() {
        this.mOpenHelper.close();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void clearCache() {
        this.mCache.clear();
    }

    public PersistentDataBlockManagerInternal getPersistentDataBlock() {
        if (this.mPersistentDataBlockManagerInternal == null) {
            this.mPersistentDataBlockManagerInternal = (PersistentDataBlockManagerInternal) LocalServices.getService(PersistentDataBlockManagerInternal.class);
        }
        return this.mPersistentDataBlockManagerInternal;
    }

    public void writePersistentDataBlock(int persistentType, int userId, int qualityForUi, byte[] payload) {
        PersistentDataBlockManagerInternal persistentDataBlock = getPersistentDataBlock();
        if (persistentDataBlock != null) {
            persistentDataBlock.setFrpCredentialHandle(PersistentData.toBytes(persistentType, userId, qualityForUi, payload));
        }
    }

    public PersistentData readPersistentDataBlock() {
        PersistentDataBlockManagerInternal persistentDataBlock = getPersistentDataBlock();
        if (persistentDataBlock == null) {
            return PersistentData.NONE;
        }
        try {
            return PersistentData.fromBytes(persistentDataBlock.getFrpCredentialHandle());
        } catch (IllegalStateException e) {
            Slog.e(TAG, "Error reading persistent data block", e);
            return PersistentData.NONE;
        }
    }

    public static class PersistentData {
        public static final PersistentData NONE = new PersistentData(0, ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, 0, (byte[]) null);
        public static final int TYPE_NONE = 0;
        public static final int TYPE_SP = 1;
        public static final int TYPE_SP_WEAVER = 2;
        static final byte VERSION_1 = 1;
        static final int VERSION_1_HEADER_SIZE = 10;
        final byte[] payload;
        final int qualityForUi;
        final int type;
        final int userId;

        private PersistentData(int type2, int userId2, int qualityForUi2, byte[] payload2) {
            this.type = type2;
            this.userId = userId2;
            this.qualityForUi = qualityForUi2;
            this.payload = payload2;
        }

        public static PersistentData fromBytes(byte[] frpData) {
            if (frpData == null || frpData.length == 0) {
                return NONE;
            }
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(frpData));
            try {
                byte version = is.readByte();
                if (version == 1) {
                    int userId2 = is.readInt();
                    int qualityForUi2 = is.readInt();
                    byte[] payload2 = new byte[(frpData.length - 10)];
                    System.arraycopy(frpData, 10, payload2, 0, payload2.length);
                    return new PersistentData(is.readByte() & 255, userId2, qualityForUi2, payload2);
                }
                Slog.wtf(LockSettingsStorage.TAG, "Unknown PersistentData version code: " + version);
                return NONE;
            } catch (IOException e) {
                Slog.wtf(LockSettingsStorage.TAG, "Could not parse PersistentData", e);
                return NONE;
            }
        }

        public static byte[] toBytes(int persistentType, int userId2, int qualityForUi2, byte[] payload2) {
            boolean z = false;
            if (persistentType == 0) {
                if (payload2 == null) {
                    z = true;
                }
                Preconditions.checkArgument(z, "TYPE_NONE must have empty payload");
                return null;
            }
            if (payload2 != null && payload2.length > 0) {
                z = true;
            }
            Preconditions.checkArgument(z, "empty payload must only be used with TYPE_NONE");
            ByteArrayOutputStream os = new ByteArrayOutputStream(payload2.length + 10);
            DataOutputStream dos = new DataOutputStream(os);
            try {
                dos.writeByte(1);
                dos.writeByte(persistentType);
                dos.writeInt(userId2);
                dos.writeInt(qualityForUi2);
                dos.write(payload2);
                return os.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("ByteArrayOutputStream cannot throw IOException");
            }
        }
    }

    static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "locksettings.db";
        private static final int DATABASE_VERSION = 2;
        private static final int IDLE_CONNECTION_TIMEOUT_MS = 30000;
        private static final String TAG = "LockSettingsDB";
        private Callback mCallback;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 2);
            setWriteAheadLoggingEnabled(true);
            setIdleConnectionTimeout(30000);
        }

        public void setCallback(Callback callback) {
            this.mCallback = callback;
        }

        private void createTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE locksettings (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,user INTEGER,value TEXT);");
        }

        public void onCreate(SQLiteDatabase db) {
            createTable(db);
            Callback callback = this.mCallback;
            if (callback != null) {
                callback.initialize(db);
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
            int upgradeVersion = oldVersion;
            if (upgradeVersion == 1) {
                upgradeVersion = 2;
            }
            if (upgradeVersion != 2) {
                Log.w(TAG, "Failed to upgrade database!");
            }
        }
    }

    private static class Cache {
        private final ArrayMap<CacheKey, Object> mCache;
        private final CacheKey mCacheKey;
        private int mVersion;

        private Cache() {
            this.mCache = new ArrayMap<>();
            this.mCacheKey = new CacheKey();
            this.mVersion = 0;
        }

        /* access modifiers changed from: package-private */
        public String peekKeyValue(String key, String defaultValue, int userId) {
            Object cached = peek(0, key, userId);
            return cached == LockSettingsStorage.DEFAULT ? defaultValue : (String) cached;
        }

        /* access modifiers changed from: package-private */
        public boolean hasKeyValue(String key, int userId) {
            return contains(0, key, userId);
        }

        /* access modifiers changed from: package-private */
        public void putKeyValue(String key, String value, int userId) {
            put(0, key, value, userId);
        }

        /* access modifiers changed from: package-private */
        public void putKeyValueIfUnchanged(String key, Object value, int userId, int version) {
            putIfUnchanged(0, key, value, userId, version);
        }

        /* access modifiers changed from: package-private */
        public byte[] peekFile(String fileName) {
            return copyOf((byte[]) peek(1, fileName, -1));
        }

        /* access modifiers changed from: package-private */
        public boolean hasFile(String fileName) {
            return contains(1, fileName, -1);
        }

        /* access modifiers changed from: package-private */
        public void putFile(String key, byte[] value) {
            put(1, key, copyOf(value), -1);
        }

        /* access modifiers changed from: package-private */
        public void putFileIfUnchanged(String key, byte[] value, int version) {
            putIfUnchanged(1, key, copyOf(value), -1, version);
        }

        /* access modifiers changed from: package-private */
        public void setFetched(int userId) {
            put(2, "isFetched", "true", userId);
        }

        /* access modifiers changed from: package-private */
        public boolean isFetched(int userId) {
            return contains(2, "", userId);
        }

        private synchronized void put(int type, String key, Object value, int userId) {
            this.mCache.put(new CacheKey().set(type, key, userId), value);
            this.mVersion++;
        }

        private synchronized void putIfUnchanged(int type, String key, Object value, int userId, int version) {
            if (!contains(type, key, userId) && this.mVersion == version) {
                put(type, key, value, userId);
            }
        }

        private synchronized boolean contains(int type, String key, int userId) {
            return this.mCache.containsKey(this.mCacheKey.set(type, key, userId));
        }

        private synchronized Object peek(int type, String key, int userId) {
            return this.mCache.get(this.mCacheKey.set(type, key, userId));
        }

        /* access modifiers changed from: private */
        public synchronized int getVersion() {
            return this.mVersion;
        }

        /* access modifiers changed from: package-private */
        public synchronized void removeUser(int userId) {
            for (int i = this.mCache.size() - 1; i >= 0; i--) {
                if (this.mCache.keyAt(i).userId == userId) {
                    this.mCache.removeAt(i);
                }
            }
            this.mVersion++;
        }

        private byte[] copyOf(byte[] data) {
            if (data != null) {
                return Arrays.copyOf(data, data.length);
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public synchronized void purgePath(String path) {
            for (int i = this.mCache.size() - 1; i >= 0; i--) {
                CacheKey entry = this.mCache.keyAt(i);
                if (entry.type == 1 && entry.key.startsWith(path)) {
                    this.mCache.removeAt(i);
                }
            }
            this.mVersion++;
        }

        /* access modifiers changed from: package-private */
        public synchronized void clear() {
            this.mCache.clear();
            this.mVersion++;
        }

        private static final class CacheKey {
            static final int TYPE_FETCHED = 2;
            static final int TYPE_FILE = 1;
            static final int TYPE_KEY_VALUE = 0;
            String key;
            int type;
            int userId;

            private CacheKey() {
            }

            public CacheKey set(int type2, String key2, int userId2) {
                this.type = type2;
                this.key = key2;
                this.userId = userId2;
                return this;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof CacheKey)) {
                    return false;
                }
                CacheKey o = (CacheKey) obj;
                if (this.userId == o.userId && this.type == o.type && this.key.equals(o.key)) {
                    return true;
                }
                return false;
            }

            public int hashCode() {
                return (this.key.hashCode() ^ this.userId) ^ this.type;
            }
        }
    }
}
