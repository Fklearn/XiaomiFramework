package com.android.server.accounts;

import android.accounts.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.FileUtils;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.server.voiceinteraction.DatabaseHelper;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class AccountsDb implements AutoCloseable {
    private static final String ACCOUNTS_ID = "_id";
    private static final String ACCOUNTS_LAST_AUTHENTICATE_TIME_EPOCH_MILLIS = "last_password_entry_time_millis_epoch";
    private static final String ACCOUNTS_NAME = "name";
    private static final String ACCOUNTS_PASSWORD = "password";
    private static final String ACCOUNTS_PREVIOUS_NAME = "previous_name";
    private static final String ACCOUNTS_TYPE = "type";
    private static final String ACCOUNTS_TYPE_COUNT = "count(type)";
    private static final String ACCOUNT_ACCESS_GRANTS = "SELECT name, uid FROM accounts, grants WHERE accounts_id=_id";
    private static final String[] ACCOUNT_TYPE_COUNT_PROJECTION = {DatabaseHelper.SoundModelContract.KEY_TYPE, ACCOUNTS_TYPE_COUNT};
    private static final String AUTHTOKENS_ACCOUNTS_ID = "accounts_id";
    private static final String AUTHTOKENS_AUTHTOKEN = "authtoken";
    private static final String AUTHTOKENS_ID = "_id";
    private static final String AUTHTOKENS_TYPE = "type";
    static final String CE_DATABASE_NAME = "accounts_ce.db";
    private static final int CE_DATABASE_VERSION = 10;
    private static final String CE_DB_PREFIX = "ceDb.";
    private static final String CE_TABLE_ACCOUNTS = "ceDb.accounts";
    private static final String CE_TABLE_AUTHTOKENS = "ceDb.authtokens";
    private static final String CE_TABLE_EXTRAS = "ceDb.extras";
    private static final String[] COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN = {DatabaseHelper.SoundModelContract.KEY_TYPE, AUTHTOKENS_AUTHTOKEN};
    private static final String[] COLUMNS_EXTRAS_KEY_AND_VALUE = {"key", "value"};
    private static final String COUNT_OF_MATCHING_GRANTS = "SELECT COUNT(*) FROM grants, accounts WHERE accounts_id=_id AND uid=? AND auth_token_type=? AND name=? AND type=?";
    private static final String COUNT_OF_MATCHING_GRANTS_ANY_TOKEN = "SELECT COUNT(*) FROM grants, accounts WHERE accounts_id=_id AND uid=? AND name=? AND type=?";
    private static final String DATABASE_NAME = "accounts.db";
    static String DEBUG_ACTION_ACCOUNT_ADD = "action_account_add";
    static String DEBUG_ACTION_ACCOUNT_REMOVE = "action_account_remove";
    static String DEBUG_ACTION_ACCOUNT_REMOVE_DE = "action_account_remove_de";
    static String DEBUG_ACTION_ACCOUNT_RENAME = "action_account_rename";
    static String DEBUG_ACTION_AUTHENTICATOR_REMOVE = "action_authenticator_remove";
    static String DEBUG_ACTION_CALLED_ACCOUNT_ADD = "action_called_account_add";
    static String DEBUG_ACTION_CALLED_ACCOUNT_REMOVE = "action_called_account_remove";
    static String DEBUG_ACTION_CALLED_ACCOUNT_SESSION_FINISH = "action_called_account_session_finish";
    static String DEBUG_ACTION_CALLED_START_ACCOUNT_ADD = "action_called_start_account_add";
    static String DEBUG_ACTION_CLEAR_PASSWORD = "action_clear_password";
    static String DEBUG_ACTION_SET_PASSWORD = "action_set_password";
    static String DEBUG_ACTION_SYNC_DE_CE_ACCOUNTS = "action_sync_de_ce_accounts";
    /* access modifiers changed from: private */
    public static String DEBUG_TABLE_ACTION_TYPE = "action_type";
    /* access modifiers changed from: private */
    public static String DEBUG_TABLE_CALLER_UID = "caller_uid";
    /* access modifiers changed from: private */
    public static String DEBUG_TABLE_KEY = "primary_key";
    /* access modifiers changed from: private */
    public static String DEBUG_TABLE_TABLE_NAME = "table_name";
    /* access modifiers changed from: private */
    public static String DEBUG_TABLE_TIMESTAMP = SplitScreenReporter.STR_DEAL_TIME;
    static final String DE_DATABASE_NAME = "accounts_de.db";
    private static final int DE_DATABASE_VERSION = 3;
    private static final String EXTRAS_ACCOUNTS_ID = "accounts_id";
    private static final String EXTRAS_ID = "_id";
    private static final String EXTRAS_KEY = "key";
    private static final String EXTRAS_VALUE = "value";
    private static final String GRANTS_ACCOUNTS_ID = "accounts_id";
    private static final String GRANTS_AUTH_TOKEN_TYPE = "auth_token_type";
    private static final String GRANTS_GRANTEE_UID = "uid";
    static final int MAX_DEBUG_DB_SIZE = 64;
    private static final String META_KEY = "key";
    private static final String META_KEY_DELIMITER = ":";
    private static final String META_KEY_FOR_AUTHENTICATOR_UID_FOR_TYPE_PREFIX = "auth_uid_for_type:";
    private static final String META_VALUE = "value";
    private static final int PRE_N_DATABASE_VERSION = 9;
    private static final String SELECTION_ACCOUNTS_ID_BY_ACCOUNT = "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)";
    private static final String SELECTION_META_BY_AUTHENTICATOR_TYPE = "key LIKE ?";
    private static final String SHARED_ACCOUNTS_ID = "_id";
    static final String TABLE_ACCOUNTS = "accounts";
    private static final String TABLE_AUTHTOKENS = "authtokens";
    /* access modifiers changed from: private */
    public static String TABLE_DEBUG = "debug_table";
    private static final String TABLE_EXTRAS = "extras";
    private static final String TABLE_GRANTS = "grants";
    private static final String TABLE_META = "meta";
    static final String TABLE_SHARED_ACCOUNTS = "shared_accounts";
    private static final String TABLE_VISIBILITY = "visibility";
    private static final String TAG = "AccountsDb";
    private static final String VISIBILITY_ACCOUNTS_ID = "accounts_id";
    private static final String VISIBILITY_PACKAGE = "_package";
    private static final String VISIBILITY_VALUE = "value";
    private final Context mContext;
    private final DeDatabaseHelper mDeDatabase;
    private volatile long mDebugDbInsertionPoint = -1;
    private volatile SQLiteStatement mDebugStatementForLogging;
    final Object mDebugStatementLock = new Object();
    private final File mPreNDatabaseFile;

    AccountsDb(DeDatabaseHelper deDatabase, Context context, File preNDatabaseFile) {
        this.mDeDatabase = deDatabase;
        this.mContext = context;
        this.mPreNDatabaseFile = preNDatabaseFile;
    }

    private static class CeDatabaseHelper extends SQLiteOpenHelper {
        CeDatabaseHelper(Context context, String ceDatabaseName) {
            super(context, ceDatabaseName, (SQLiteDatabase.CursorFactory) null, 10);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.i(AccountsDb.TAG, "Creating CE database " + getDatabaseName());
            db.execSQL("CREATE TABLE accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, password TEXT, UNIQUE(name,type))");
            db.execSQL("CREATE TABLE authtokens (  _id INTEGER PRIMARY KEY AUTOINCREMENT,  accounts_id INTEGER NOT NULL, type TEXT NOT NULL,  authtoken TEXT,  UNIQUE (accounts_id,type))");
            db.execSQL("CREATE TABLE extras ( _id INTEGER PRIMARY KEY AUTOINCREMENT, accounts_id INTEGER, key TEXT NOT NULL, value TEXT, UNIQUE(accounts_id,key))");
            createAccountsDeletionTrigger(db);
        }

        private void createAccountsDeletionTrigger(SQLiteDatabase db) {
            db.execSQL(" CREATE TRIGGER accountsDelete DELETE ON accounts BEGIN   DELETE FROM authtokens     WHERE accounts_id=OLD._id ;   DELETE FROM extras     WHERE accounts_id=OLD._id ; END");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(AccountsDb.TAG, "Upgrade CE from version " + oldVersion + " to version " + newVersion);
            if (oldVersion == 9) {
                if (Log.isLoggable(AccountsDb.TAG, 2)) {
                    Log.v(AccountsDb.TAG, "onUpgrade upgrading to v10");
                }
                db.execSQL("DROP TABLE IF EXISTS meta");
                db.execSQL("DROP TABLE IF EXISTS shared_accounts");
                db.execSQL("DROP TRIGGER IF EXISTS accountsDelete");
                createAccountsDeletionTrigger(db);
                db.execSQL("DROP TABLE IF EXISTS grants");
                db.execSQL("DROP TABLE IF EXISTS " + AccountsDb.TABLE_DEBUG);
                oldVersion++;
            }
            if (oldVersion != newVersion) {
                Log.e(AccountsDb.TAG, "failed to upgrade version " + oldVersion + " to version " + newVersion);
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(AccountsDb.TAG, "onDowngrade: recreate accounts CE table");
            AccountsDb.resetDatabase(db);
            onCreate(db);
        }

        public void onOpen(SQLiteDatabase db) {
            if (Log.isLoggable(AccountsDb.TAG, 2)) {
                Log.v(AccountsDb.TAG, "opened database accounts_ce.db");
            }
        }

        static CeDatabaseHelper create(Context context, File preNDatabaseFile, File ceDatabaseFile) {
            boolean newDbExists = ceDatabaseFile.exists();
            if (Log.isLoggable(AccountsDb.TAG, 2)) {
                Log.v(AccountsDb.TAG, "CeDatabaseHelper.create ceDatabaseFile=" + ceDatabaseFile + " oldDbExists=" + preNDatabaseFile.exists() + " newDbExists=" + newDbExists);
            }
            boolean removeOldDb = false;
            if (!newDbExists && preNDatabaseFile.exists()) {
                removeOldDb = migratePreNDbToCe(preNDatabaseFile, ceDatabaseFile);
            }
            CeDatabaseHelper ceHelper = new CeDatabaseHelper(context, ceDatabaseFile.getPath());
            ceHelper.getWritableDatabase();
            ceHelper.close();
            if (removeOldDb) {
                Slog.i(AccountsDb.TAG, "Migration complete - removing pre-N db " + preNDatabaseFile);
                if (!SQLiteDatabase.deleteDatabase(preNDatabaseFile)) {
                    Slog.e(AccountsDb.TAG, "Cannot remove pre-N db " + preNDatabaseFile);
                }
            }
            return ceHelper;
        }

        private static boolean migratePreNDbToCe(File oldDbFile, File ceDbFile) {
            Slog.i(AccountsDb.TAG, "Moving pre-N DB " + oldDbFile + " to CE " + ceDbFile);
            try {
                FileUtils.copyFileOrThrow(oldDbFile, ceDbFile);
                return true;
            } catch (IOException e) {
                Slog.e(AccountsDb.TAG, "Cannot copy file to " + ceDbFile + " from " + oldDbFile, e);
                AccountsDb.deleteDbFileWarnIfFailed(ceDbFile);
                return false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Cursor findAuthtokenForAllAccounts(String accountType, String authToken) {
        return this.mDeDatabase.getReadableDatabaseUserIsUnlocked().rawQuery("SELECT ceDb.authtokens._id, ceDb.accounts.name, ceDb.authtokens.type FROM ceDb.accounts JOIN ceDb.authtokens ON ceDb.accounts._id = ceDb.authtokens.accounts_id WHERE ceDb.authtokens.authtoken = ? AND ceDb.accounts.type = ?", new String[]{authToken, accountType});
    }

    /* access modifiers changed from: package-private */
    public Map<String, String> findAuthTokensByAccount(Account account) {
        SQLiteDatabase db = this.mDeDatabase.getReadableDatabaseUserIsUnlocked();
        HashMap<String, String> authTokensForAccount = new HashMap<>();
        Cursor cursor = db.query(CE_TABLE_AUTHTOKENS, COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN, SELECTION_ACCOUNTS_ID_BY_ACCOUNT, new String[]{account.name, account.type}, (String) null, (String) null, (String) null);
        while (cursor.moveToNext()) {
            try {
                authTokensForAccount.put(cursor.getString(0), cursor.getString(1));
            } finally {
                cursor.close();
            }
        }
        return authTokensForAccount;
    }

    /* access modifiers changed from: package-private */
    public boolean deleteAuthtokensByAccountIdAndType(long accountId, String authtokenType) {
        if (this.mDeDatabase.getWritableDatabaseUserIsUnlocked().delete(CE_TABLE_AUTHTOKENS, "accounts_id=? AND type=?", new String[]{String.valueOf(accountId), authtokenType}) > 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean deleteAuthToken(String authTokenId) {
        return this.mDeDatabase.getWritableDatabaseUserIsUnlocked().delete(CE_TABLE_AUTHTOKENS, "_id= ?", new String[]{authTokenId}) > 0;
    }

    /* access modifiers changed from: package-private */
    public long insertAuthToken(long accountId, String authTokenType, String authToken) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabaseUserIsUnlocked();
        ContentValues values = new ContentValues();
        values.put("accounts_id", Long.valueOf(accountId));
        values.put(DatabaseHelper.SoundModelContract.KEY_TYPE, authTokenType);
        values.put(AUTHTOKENS_AUTHTOKEN, authToken);
        return db.insert(CE_TABLE_AUTHTOKENS, AUTHTOKENS_AUTHTOKEN, values);
    }

    /* access modifiers changed from: package-private */
    public int updateCeAccountPassword(long accountId, String password) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabaseUserIsUnlocked();
        ContentValues values = new ContentValues();
        values.put(ACCOUNTS_PASSWORD, password);
        return db.update(CE_TABLE_ACCOUNTS, values, "_id=?", new String[]{String.valueOf(accountId)});
    }

    /* access modifiers changed from: package-private */
    public boolean renameCeAccount(long accountId, String newName) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabaseUserIsUnlocked();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        if (db.update(CE_TABLE_ACCOUNTS, values, "_id=?", new String[]{String.valueOf(accountId)}) > 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean deleteAuthTokensByAccountId(long accountId) {
        return this.mDeDatabase.getWritableDatabaseUserIsUnlocked().delete(CE_TABLE_AUTHTOKENS, "accounts_id=?", new String[]{String.valueOf(accountId)}) > 0;
    }

    /* access modifiers changed from: package-private */
    public long findExtrasIdByAccountId(long accountId, String key) {
        SQLiteDatabase db = this.mDeDatabase.getReadableDatabaseUserIsUnlocked();
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query(CE_TABLE_EXTRAS, new String[]{"_id"}, "accounts_id=" + accountId + " AND " + "key" + "=?", new String[]{key}, (String) null, (String) null, (String) null);
        try {
            if (cursor.moveToNext()) {
                return cursor.getLong(0);
            }
            cursor.close();
            return -1;
        } finally {
            cursor.close();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateExtra(long extrasId, String value) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabaseUserIsUnlocked();
        ContentValues values = new ContentValues();
        values.put("value", value);
        if (db.update(TABLE_EXTRAS, values, "_id=?", new String[]{String.valueOf(extrasId)}) == 1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public long insertExtra(long accountId, String key, String value) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabaseUserIsUnlocked();
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("accounts_id", Long.valueOf(accountId));
        values.put("value", value);
        return db.insert(CE_TABLE_EXTRAS, "key", values);
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0041, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0042, code lost:
        if (r1 != null) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0044, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0047, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.Map<java.lang.String, java.lang.String> findUserExtrasForAccount(android.accounts.Account r13) {
        /*
            r12 = this;
            com.android.server.accounts.AccountsDb$DeDatabaseHelper r0 = r12.mDeDatabase
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabaseUserIsUnlocked()
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r9 = r1
            r1 = 2
            java.lang.String[] r5 = new java.lang.String[r1]
            java.lang.String r1 = r13.name
            r10 = 0
            r5[r10] = r1
            java.lang.String r1 = r13.type
            r11 = 1
            r5[r11] = r1
            java.lang.String[] r3 = COLUMNS_EXTRAS_KEY_AND_VALUE
            java.lang.String r2 = "ceDb.extras"
            java.lang.String r4 = "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
        L_0x0027:
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x003f }
            if (r2 == 0) goto L_0x003a
            java.lang.String r2 = r1.getString(r10)     // Catch:{ all -> 0x003f }
            java.lang.String r3 = r1.getString(r11)     // Catch:{ all -> 0x003f }
            r9.put(r2, r3)     // Catch:{ all -> 0x003f }
            goto L_0x0027
        L_0x003a:
            r2 = 0
            $closeResource(r2, r1)
            return r9
        L_0x003f:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0041 }
        L_0x0041:
            r3 = move-exception
            if (r1 == 0) goto L_0x0047
            $closeResource(r2, r1)
        L_0x0047:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.findUserExtrasForAccount(android.accounts.Account):java.util.Map");
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

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003e, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003f, code lost:
        if (r1 != null) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0041, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0044, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long findCeAccountId(android.accounts.Account r12) {
        /*
            r11 = this;
            com.android.server.accounts.AccountsDb$DeDatabaseHelper r0 = r11.mDeDatabase
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabaseUserIsUnlocked()
            java.lang.String r1 = "_id"
            java.lang.String[] r3 = new java.lang.String[]{r1}
            java.lang.String r9 = "name=? AND type=?"
            r1 = 2
            java.lang.String[] r5 = new java.lang.String[r1]
            java.lang.String r1 = r12.name
            r10 = 0
            r5[r10] = r1
            java.lang.String r1 = r12.type
            r2 = 1
            r5[r2] = r1
            java.lang.String r2 = "ceDb.accounts"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r9
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x003c }
            r4 = 0
            if (r2 == 0) goto L_0x0036
            long r6 = r1.getLong(r10)     // Catch:{ all -> 0x003c }
            $closeResource(r4, r1)
            return r6
        L_0x0036:
            r6 = -1
            $closeResource(r4, r1)
            return r6
        L_0x003c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003e }
        L_0x003e:
            r4 = move-exception
            if (r1 == 0) goto L_0x0044
            $closeResource(r2, r1)
        L_0x0044:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.findCeAccountId(android.accounts.Account):long");
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003a, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003b, code lost:
        if (r1 != null) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003d, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0040, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String findAccountPasswordByNameAndType(java.lang.String r12, java.lang.String r13) {
        /*
            r11 = this;
            com.android.server.accounts.AccountsDb$DeDatabaseHelper r0 = r11.mDeDatabase
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabaseUserIsUnlocked()
            java.lang.String r9 = "name=? AND type=?"
            r1 = 2
            java.lang.String[] r5 = new java.lang.String[r1]
            r10 = 0
            r5[r10] = r12
            r1 = 1
            r5[r1] = r13
            java.lang.String r1 = "password"
            java.lang.String[] r3 = new java.lang.String[]{r1}
            java.lang.String r2 = "ceDb.accounts"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r9
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x0038 }
            r4 = 0
            if (r2 == 0) goto L_0x0033
            java.lang.String r2 = r1.getString(r10)     // Catch:{ all -> 0x0038 }
            $closeResource(r4, r1)
            return r2
        L_0x0033:
            $closeResource(r4, r1)
            return r4
        L_0x0038:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003a }
        L_0x003a:
            r4 = move-exception
            if (r1 == 0) goto L_0x0040
            $closeResource(r2, r1)
        L_0x0040:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.findAccountPasswordByNameAndType(java.lang.String, java.lang.String):java.lang.String");
    }

    /* access modifiers changed from: package-private */
    public long insertCeAccount(Account account, String password) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabaseUserIsUnlocked();
        ContentValues values = new ContentValues();
        values.put("name", account.name);
        values.put(DatabaseHelper.SoundModelContract.KEY_TYPE, account.type);
        values.put(ACCOUNTS_PASSWORD, password);
        return db.insert(CE_TABLE_ACCOUNTS, "name", values);
    }

    static class DeDatabaseHelper extends SQLiteOpenHelper {
        /* access modifiers changed from: private */
        public volatile boolean mCeAttached;
        private final int mUserId;

        private DeDatabaseHelper(Context context, int userId, String deDatabaseName) {
            super(context, deDatabaseName, (SQLiteDatabase.CursorFactory) null, 3);
            this.mUserId = userId;
        }

        public void onCreate(SQLiteDatabase db) {
            Log.i(AccountsDb.TAG, "Creating DE database for user " + this.mUserId);
            db.execSQL("CREATE TABLE accounts ( _id INTEGER PRIMARY KEY, name TEXT NOT NULL, type TEXT NOT NULL, previous_name TEXT, last_password_entry_time_millis_epoch INTEGER DEFAULT 0, UNIQUE(name,type))");
            db.execSQL("CREATE TABLE meta ( key TEXT PRIMARY KEY NOT NULL, value TEXT)");
            createGrantsTable(db);
            createSharedAccountsTable(db);
            createAccountsDeletionTrigger(db);
            createDebugTable(db);
            createAccountsVisibilityTable(db);
            createAccountsDeletionVisibilityCleanupTrigger(db);
        }

        private void createSharedAccountsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE shared_accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, UNIQUE(name,type))");
        }

        private void createAccountsDeletionTrigger(SQLiteDatabase db) {
            db.execSQL(" CREATE TRIGGER accountsDelete DELETE ON accounts BEGIN   DELETE FROM grants     WHERE accounts_id=OLD._id ; END");
        }

        private void createGrantsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE grants (  accounts_id INTEGER NOT NULL, auth_token_type STRING NOT NULL,  uid INTEGER NOT NULL,  UNIQUE (accounts_id,auth_token_type,uid))");
        }

        private void createAccountsVisibilityTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE visibility ( accounts_id INTEGER NOT NULL, _package TEXT NOT NULL, value INTEGER, PRIMARY KEY(accounts_id,_package))");
        }

        static void createDebugTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + AccountsDb.TABLE_DEBUG + " ( " + "_id" + " INTEGER," + AccountsDb.DEBUG_TABLE_ACTION_TYPE + " TEXT NOT NULL, " + AccountsDb.DEBUG_TABLE_TIMESTAMP + " DATETIME," + AccountsDb.DEBUG_TABLE_CALLER_UID + " INTEGER NOT NULL," + AccountsDb.DEBUG_TABLE_TABLE_NAME + " TEXT NOT NULL," + AccountsDb.DEBUG_TABLE_KEY + " INTEGER PRIMARY KEY)");
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE INDEX timestamp_index ON ");
            sb.append(AccountsDb.TABLE_DEBUG);
            sb.append(" (");
            sb.append(AccountsDb.DEBUG_TABLE_TIMESTAMP);
            sb.append(")");
            db.execSQL(sb.toString());
        }

        private void createAccountsDeletionVisibilityCleanupTrigger(SQLiteDatabase db) {
            db.execSQL(" CREATE TRIGGER accountsDeleteVisibility DELETE ON accounts BEGIN   DELETE FROM visibility     WHERE accounts_id=OLD._id ; END");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(AccountsDb.TAG, "upgrade from version " + oldVersion + " to version " + newVersion);
            if (oldVersion == 1) {
                createAccountsVisibilityTable(db);
                createAccountsDeletionVisibilityCleanupTrigger(db);
                oldVersion = 3;
            }
            if (oldVersion == 2) {
                db.execSQL("DROP TRIGGER IF EXISTS accountsDeleteVisibility");
                db.execSQL("DROP TABLE IF EXISTS visibility");
                createAccountsVisibilityTable(db);
                createAccountsDeletionVisibilityCleanupTrigger(db);
                oldVersion++;
            }
            if (oldVersion != newVersion) {
                Log.e(AccountsDb.TAG, "failed to upgrade version " + oldVersion + " to version " + newVersion);
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(AccountsDb.TAG, "onDowngrade: recreate accounts DE table");
            AccountsDb.resetDatabase(db);
            onCreate(db);
        }

        public SQLiteDatabase getReadableDatabaseUserIsUnlocked() {
            if (!this.mCeAttached) {
                Log.wtf(AccountsDb.TAG, "getReadableDatabaseUserIsUnlocked called while user " + this.mUserId + " is still locked. CE database is not yet available.", new Throwable());
            }
            return super.getReadableDatabase();
        }

        public SQLiteDatabase getWritableDatabaseUserIsUnlocked() {
            if (!this.mCeAttached) {
                Log.wtf(AccountsDb.TAG, "getWritableDatabaseUserIsUnlocked called while user " + this.mUserId + " is still locked. CE database is not yet available.", new Throwable());
            }
            return super.getWritableDatabase();
        }

        public void onOpen(SQLiteDatabase db) {
            if (Log.isLoggable(AccountsDb.TAG, 2)) {
                Log.v(AccountsDb.TAG, "opened database accounts_de.db");
            }
        }

        /* access modifiers changed from: private */
        public void migratePreNDbToDe(File preNDbFile) {
            Log.i(AccountsDb.TAG, "Migrate pre-N database to DE preNDbFile=" + preNDbFile);
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("ATTACH DATABASE '" + preNDbFile.getPath() + "' AS preNDb");
            db.beginTransaction();
            db.execSQL("INSERT INTO accounts(_id,name,type, previous_name, last_password_entry_time_millis_epoch) SELECT _id,name,type, previous_name, last_password_entry_time_millis_epoch FROM preNDb.accounts");
            db.execSQL("INSERT INTO shared_accounts(_id,name,type) SELECT _id,name,type FROM preNDb.shared_accounts");
            db.execSQL("INSERT INTO " + AccountsDb.TABLE_DEBUG + "(" + "_id" + "," + AccountsDb.DEBUG_TABLE_ACTION_TYPE + "," + AccountsDb.DEBUG_TABLE_TIMESTAMP + "," + AccountsDb.DEBUG_TABLE_CALLER_UID + "," + AccountsDb.DEBUG_TABLE_TABLE_NAME + "," + AccountsDb.DEBUG_TABLE_KEY + ") SELECT " + "_id" + "," + AccountsDb.DEBUG_TABLE_ACTION_TYPE + "," + AccountsDb.DEBUG_TABLE_TIMESTAMP + "," + AccountsDb.DEBUG_TABLE_CALLER_UID + "," + AccountsDb.DEBUG_TABLE_TABLE_NAME + "," + AccountsDb.DEBUG_TABLE_KEY + " FROM preNDb." + AccountsDb.TABLE_DEBUG);
            db.execSQL("INSERT INTO grants(accounts_id,auth_token_type,uid) SELECT accounts_id,auth_token_type,uid FROM preNDb.grants");
            db.execSQL("INSERT INTO meta(key,value) SELECT key,value FROM preNDb.meta");
            db.setTransactionSuccessful();
            db.endTransaction();
            db.execSQL("DETACH DATABASE preNDb");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean deleteDeAccount(long accountId) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("_id=");
        sb.append(accountId);
        return db.delete(TABLE_ACCOUNTS, sb.toString(), (String[]) null) > 0;
    }

    /* access modifiers changed from: package-private */
    public long insertSharedAccount(Account account) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", account.name);
        values.put(DatabaseHelper.SoundModelContract.KEY_TYPE, account.type);
        return db.insert(TABLE_SHARED_ACCOUNTS, "name", values);
    }

    /* access modifiers changed from: package-private */
    public boolean deleteSharedAccount(Account account) {
        return this.mDeDatabase.getWritableDatabase().delete(TABLE_SHARED_ACCOUNTS, "name=? AND type=?", new String[]{account.name, account.type}) > 0;
    }

    /* access modifiers changed from: package-private */
    public int renameSharedAccount(Account account, String newName) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        return db.update(TABLE_SHARED_ACCOUNTS, values, "name=? AND type=?", new String[]{account.name, account.type});
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x004d A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.accounts.Account> getSharedAccounts() {
        /*
            r13 = this;
            java.lang.String r0 = "type"
            java.lang.String r1 = "name"
            com.android.server.accounts.AccountsDb$DeDatabaseHelper r2 = r13.mDeDatabase
            android.database.sqlite.SQLiteDatabase r2 = r2.getReadableDatabase()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r11 = r3
            r12 = 0
            java.lang.String r4 = "shared_accounts"
            java.lang.String[] r5 = new java.lang.String[]{r1, r0}     // Catch:{ all -> 0x0051 }
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r3 = r2
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0051 }
            r12 = r3
            if (r12 == 0) goto L_0x004b
            boolean r3 = r12.moveToFirst()     // Catch:{ all -> 0x0051 }
            if (r3 == 0) goto L_0x004b
            int r1 = r12.getColumnIndex(r1)     // Catch:{ all -> 0x0051 }
            int r0 = r12.getColumnIndex(r0)     // Catch:{ all -> 0x0051 }
        L_0x0035:
            android.accounts.Account r3 = new android.accounts.Account     // Catch:{ all -> 0x0051 }
            java.lang.String r4 = r12.getString(r1)     // Catch:{ all -> 0x0051 }
            java.lang.String r5 = r12.getString(r0)     // Catch:{ all -> 0x0051 }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x0051 }
            r11.add(r3)     // Catch:{ all -> 0x0051 }
            boolean r3 = r12.moveToNext()     // Catch:{ all -> 0x0051 }
            if (r3 != 0) goto L_0x0035
        L_0x004b:
            if (r12 == 0) goto L_0x0050
            r12.close()
        L_0x0050:
            return r11
        L_0x0051:
            r0 = move-exception
            if (r12 == 0) goto L_0x0057
            r12.close()
        L_0x0057:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.getSharedAccounts():java.util.List");
    }

    /* access modifiers changed from: package-private */
    public long findSharedAccountId(Account account) {
        SQLiteDatabase db = this.mDeDatabase.getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query(TABLE_SHARED_ACCOUNTS, new String[]{"_id"}, "name=? AND type=?", new String[]{account.name, account.type}, (String) null, (String) null, (String) null);
        try {
            if (cursor.moveToNext()) {
                return cursor.getLong(0);
            }
            cursor.close();
            return -1;
        } finally {
            cursor.close();
        }
    }

    /* access modifiers changed from: package-private */
    public long findAccountLastAuthenticatedTime(Account account) {
        return DatabaseUtils.longForQuery(this.mDeDatabase.getReadableDatabase(), "SELECT last_password_entry_time_millis_epoch FROM accounts WHERE name=? AND type=?", new String[]{account.name, account.type});
    }

    /* access modifiers changed from: package-private */
    public boolean updateAccountLastAuthenticatedTime(Account account) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNTS_LAST_AUTHENTICATE_TIME_EPOCH_MILLIS, Long.valueOf(System.currentTimeMillis()));
        if (db.update(TABLE_ACCOUNTS, values, "name=? AND type=?", new String[]{account.name, account.type}) > 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void dumpDeAccountsTable(PrintWriter pw) {
        Cursor cursor = this.mDeDatabase.getReadableDatabase().query(TABLE_ACCOUNTS, ACCOUNT_TYPE_COUNT_PROJECTION, (String) null, (String[]) null, DatabaseHelper.SoundModelContract.KEY_TYPE, (String) null, (String) null);
        while (cursor.moveToNext()) {
            try {
                pw.println(cursor.getString(0) + "," + cursor.getString(1));
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }
        cursor.close();
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003e, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003f, code lost:
        if (r1 != null) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0041, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0044, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long findDeAccountId(android.accounts.Account r12) {
        /*
            r11 = this;
            com.android.server.accounts.AccountsDb$DeDatabaseHelper r0 = r11.mDeDatabase
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            java.lang.String r1 = "_id"
            java.lang.String[] r3 = new java.lang.String[]{r1}
            java.lang.String r9 = "name=? AND type=?"
            r1 = 2
            java.lang.String[] r5 = new java.lang.String[r1]
            java.lang.String r1 = r12.name
            r10 = 0
            r5[r10] = r1
            java.lang.String r1 = r12.type
            r2 = 1
            r5[r2] = r1
            java.lang.String r2 = "accounts"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r9
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x003c }
            r4 = 0
            if (r2 == 0) goto L_0x0036
            long r6 = r1.getLong(r10)     // Catch:{ all -> 0x003c }
            $closeResource(r4, r1)
            return r6
        L_0x0036:
            r6 = -1
            $closeResource(r4, r1)
            return r6
        L_0x003c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003e }
        L_0x003e:
            r4 = move-exception
            if (r1 == 0) goto L_0x0044
            $closeResource(r2, r1)
        L_0x0044:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.findDeAccountId(android.accounts.Account):long");
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x004f, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0050, code lost:
        if (r1 != null) goto L_0x0052;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0052, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0055, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.Map<java.lang.Long, android.accounts.Account> findAllDeAccounts() {
        /*
            r10 = this;
            com.android.server.accounts.AccountsDb$DeDatabaseHelper r0 = r10.mDeDatabase
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            java.util.LinkedHashMap r1 = new java.util.LinkedHashMap
            r1.<init>()
            r9 = r1
            java.lang.String r1 = "_id"
            java.lang.String r2 = "type"
            java.lang.String r3 = "name"
            java.lang.String[] r3 = new java.lang.String[]{r1, r2, r3}
            java.lang.String r2 = "accounts"
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            java.lang.String r8 = "_id"
            r1 = r0
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
        L_0x0025:
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x004d }
            if (r2 == 0) goto L_0x0048
            r2 = 0
            long r4 = r1.getLong(r2)     // Catch:{ all -> 0x004d }
            r2 = 1
            java.lang.String r2 = r1.getString(r2)     // Catch:{ all -> 0x004d }
            r6 = 2
            java.lang.String r6 = r1.getString(r6)     // Catch:{ all -> 0x004d }
            android.accounts.Account r7 = new android.accounts.Account     // Catch:{ all -> 0x004d }
            r7.<init>(r6, r2)     // Catch:{ all -> 0x004d }
            java.lang.Long r8 = java.lang.Long.valueOf(r4)     // Catch:{ all -> 0x004d }
            r9.put(r8, r7)     // Catch:{ all -> 0x004d }
            goto L_0x0025
        L_0x0048:
            r2 = 0
            $closeResource(r2, r1)
            return r9
        L_0x004d:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x004f }
        L_0x004f:
            r4 = move-exception
            if (r1 == 0) goto L_0x0055
            $closeResource(r2, r1)
        L_0x0055:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.findAllDeAccounts():java.util.Map");
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003d, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003e, code lost:
        if (r1 != null) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0040, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0043, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String findDeAccountPreviousName(android.accounts.Account r12) {
        /*
            r11 = this;
            com.android.server.accounts.AccountsDb$DeDatabaseHelper r0 = r11.mDeDatabase
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            java.lang.String r1 = "previous_name"
            java.lang.String[] r3 = new java.lang.String[]{r1}
            java.lang.String r9 = "name=? AND type=?"
            r1 = 2
            java.lang.String[] r5 = new java.lang.String[r1]
            java.lang.String r1 = r12.name
            r10 = 0
            r5[r10] = r1
            java.lang.String r1 = r12.type
            r2 = 1
            r5[r2] = r1
            java.lang.String r2 = "accounts"
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r0
            r4 = r9
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x003b }
            r4 = 0
            if (r2 == 0) goto L_0x0037
            java.lang.String r2 = r1.getString(r10)     // Catch:{ all -> 0x003b }
            $closeResource(r4, r1)
            return r2
        L_0x0037:
            $closeResource(r4, r1)
            return r4
        L_0x003b:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003d }
        L_0x003d:
            r4 = move-exception
            if (r1 == 0) goto L_0x0043
            $closeResource(r2, r1)
        L_0x0043:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.findDeAccountPreviousName(android.accounts.Account):java.lang.String");
    }

    /* access modifiers changed from: package-private */
    public long insertDeAccount(Account account, long accountId) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_id", Long.valueOf(accountId));
        values.put("name", account.name);
        values.put(DatabaseHelper.SoundModelContract.KEY_TYPE, account.type);
        values.put(ACCOUNTS_LAST_AUTHENTICATE_TIME_EPOCH_MILLIS, Long.valueOf(System.currentTimeMillis()));
        return db.insert(TABLE_ACCOUNTS, "name", values);
    }

    /* access modifiers changed from: package-private */
    public boolean renameDeAccount(long accountId, String newName, String previousName) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        values.put(ACCOUNTS_PREVIOUS_NAME, previousName);
        if (db.update(TABLE_ACCOUNTS, values, "_id=?", new String[]{String.valueOf(accountId)}) > 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean deleteGrantsByAccountIdAuthTokenTypeAndUid(long accountId, String authTokenType, long uid) {
        if (this.mDeDatabase.getWritableDatabase().delete(TABLE_GRANTS, "accounts_id=? AND auth_token_type=? AND uid=?", new String[]{String.valueOf(accountId), authTokenType, String.valueOf(uid)}) > 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public List<Integer> findAllUidGrants() {
        SQLiteDatabase db = this.mDeDatabase.getReadableDatabase();
        List<Integer> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_GRANTS, new String[]{"uid"}, (String) null, (String[]) null, "uid", (String) null, (String) null);
        while (cursor.moveToNext()) {
            try {
                result.add(Integer.valueOf(cursor.getInt(0)));
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public long findMatchingGrantsCount(int uid, String authTokenType, Account account) {
        return DatabaseUtils.longForQuery(this.mDeDatabase.getReadableDatabase(), COUNT_OF_MATCHING_GRANTS, new String[]{String.valueOf(uid), authTokenType, account.name, account.type});
    }

    /* access modifiers changed from: package-private */
    public long findMatchingGrantsCountAnyToken(int uid, Account account) {
        return DatabaseUtils.longForQuery(this.mDeDatabase.getReadableDatabase(), COUNT_OF_MATCHING_GRANTS_ANY_TOKEN, new String[]{String.valueOf(uid), account.name, account.type});
    }

    /* access modifiers changed from: package-private */
    public long insertGrant(long accountId, String authTokenType, int uid) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("accounts_id", Long.valueOf(accountId));
        values.put(GRANTS_AUTH_TOKEN_TYPE, authTokenType);
        values.put("uid", Integer.valueOf(uid));
        return db.insert(TABLE_GRANTS, "accounts_id", values);
    }

    /* access modifiers changed from: package-private */
    public boolean deleteGrantsByUid(int uid) {
        return this.mDeDatabase.getWritableDatabase().delete(TABLE_GRANTS, "uid=?", new String[]{Integer.toString(uid)}) > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean setAccountVisibility(long accountId, String packageName, int visibility) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("accounts_id", String.valueOf(accountId));
        values.put(VISIBILITY_PACKAGE, packageName);
        values.put("value", String.valueOf(visibility));
        return db.replace(TABLE_VISIBILITY, "value", values) != -1;
    }

    /* access modifiers changed from: package-private */
    public Integer findAccountVisibility(Account account, String packageName) {
        SQLiteDatabase db = this.mDeDatabase.getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query(TABLE_VISIBILITY, new String[]{"value"}, "accounts_id=(select _id FROM accounts WHERE name=? AND type=?) AND _package=? ", new String[]{account.name, account.type, packageName}, (String) null, (String) null, (String) null);
        try {
            if (cursor.moveToNext()) {
                return Integer.valueOf(cursor.getInt(0));
            }
            cursor.close();
            return null;
        } finally {
            cursor.close();
        }
    }

    /* access modifiers changed from: package-private */
    public Integer findAccountVisibility(long accountId, String packageName) {
        SQLiteDatabase db = this.mDeDatabase.getReadableDatabase();
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query(TABLE_VISIBILITY, new String[]{"value"}, "accounts_id=? AND _package=? ", new String[]{String.valueOf(accountId), packageName}, (String) null, (String) null, (String) null);
        try {
            if (cursor.moveToNext()) {
                return Integer.valueOf(cursor.getInt(0));
            }
            cursor.close();
            return null;
        } finally {
            cursor.close();
        }
    }

    /* access modifiers changed from: package-private */
    public Account findDeAccountByAccountId(long accountId) {
        Cursor cursor = this.mDeDatabase.getReadableDatabase().query(TABLE_ACCOUNTS, new String[]{"name", DatabaseHelper.SoundModelContract.KEY_TYPE}, "_id=? ", new String[]{String.valueOf(accountId)}, (String) null, (String) null, (String) null);
        try {
            if (cursor.moveToNext()) {
                return new Account(cursor.getString(0), cursor.getString(1));
            }
            cursor.close();
            return null;
        } finally {
            cursor.close();
        }
    }

    /* access modifiers changed from: package-private */
    public Map<String, Integer> findAllVisibilityValuesForAccount(Account account) {
        SQLiteDatabase db = this.mDeDatabase.getReadableDatabase();
        Map<String, Integer> result = new HashMap<>();
        Cursor cursor = db.query(TABLE_VISIBILITY, new String[]{VISIBILITY_PACKAGE, "value"}, SELECTION_ACCOUNTS_ID_BY_ACCOUNT, new String[]{account.name, account.type}, (String) null, (String) null, (String) null);
        while (cursor.moveToNext()) {
            try {
                result.put(cursor.getString(0), Integer.valueOf(cursor.getInt(1)));
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public Map<Account, Map<String, Integer>> findAllVisibilityValues() {
        SQLiteDatabase db = this.mDeDatabase.getReadableDatabase();
        Map<Account, Map<String, Integer>> result = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT visibility._package, visibility.value, accounts.name, accounts.type FROM visibility JOIN accounts ON accounts._id = visibility.accounts_id", (String[]) null);
        while (cursor.moveToNext()) {
            try {
                String packageName = cursor.getString(0);
                Integer visibility = Integer.valueOf(cursor.getInt(1));
                Account account = new Account(cursor.getString(2), cursor.getString(3));
                Map<String, Integer> accountVisibility = result.get(account);
                if (accountVisibility == null) {
                    accountVisibility = new HashMap<>();
                    result.put(account, accountVisibility);
                }
                accountVisibility.put(packageName, visibility);
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public boolean deleteAccountVisibilityForPackage(String packageName) {
        return this.mDeDatabase.getWritableDatabase().delete(TABLE_VISIBILITY, "_package=? ", new String[]{packageName}) > 0;
    }

    /* access modifiers changed from: package-private */
    public long insertOrReplaceMetaAuthTypeAndUid(String authenticatorType, int uid) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key", META_KEY_FOR_AUTHENTICATOR_UID_FOR_TYPE_PREFIX + authenticatorType);
        values.put("value", Integer.valueOf(uid));
        return db.insertWithOnConflict(TABLE_META, (String) null, values, 5);
    }

    /* access modifiers changed from: package-private */
    public Map<String, Integer> findMetaAuthUid() {
        Cursor metaCursor = this.mDeDatabase.getReadableDatabase().query(TABLE_META, new String[]{"key", "value"}, SELECTION_META_BY_AUTHENTICATOR_TYPE, new String[]{"auth_uid_for_type:%"}, (String) null, (String) null, "key");
        Map<String, Integer> map = new LinkedHashMap<>();
        while (metaCursor.moveToNext()) {
            try {
                String type = TextUtils.split(metaCursor.getString(0), META_KEY_DELIMITER)[1];
                String uidStr = metaCursor.getString(1);
                if (!TextUtils.isEmpty(type)) {
                    if (!TextUtils.isEmpty(uidStr)) {
                        map.put(type, Integer.valueOf(Integer.parseInt(metaCursor.getString(1))));
                    }
                }
                Slog.e(TAG, "Auth type empty: " + TextUtils.isEmpty(type) + ", uid empty: " + TextUtils.isEmpty(uidStr));
            } finally {
                metaCursor.close();
            }
        }
        return map;
    }

    /* access modifiers changed from: package-private */
    public boolean deleteMetaByAuthTypeAndUid(String type, int uid) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append(META_KEY_FOR_AUTHENTICATOR_UID_FOR_TYPE_PREFIX);
        sb.append(type);
        return db.delete(TABLE_META, "key=? AND value=?", new String[]{sb.toString(), String.valueOf(uid)}) > 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0048, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0049, code lost:
        if (r2 != null) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004b, code lost:
        $closeResource(r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004e, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.util.Pair<java.lang.String, java.lang.Integer>> findAllAccountGrants() {
        /*
            r7 = this;
            com.android.server.accounts.AccountsDb$DeDatabaseHelper r0 = r7.mDeDatabase
            android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
            r1 = 0
            java.lang.String r2 = "SELECT name, uid FROM accounts, grants WHERE accounts_id=_id"
            android.database.Cursor r2 = r0.rawQuery(r2, r1)
            if (r2 == 0) goto L_0x003c
            boolean r3 = r2.moveToFirst()     // Catch:{ all -> 0x0046 }
            if (r3 != 0) goto L_0x0016
            goto L_0x003c
        L_0x0016:
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x0046 }
            r3.<init>()     // Catch:{ all -> 0x0046 }
        L_0x001b:
            r4 = 0
            java.lang.String r4 = r2.getString(r4)     // Catch:{ all -> 0x0046 }
            r5 = 1
            int r5 = r2.getInt(r5)     // Catch:{ all -> 0x0046 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0046 }
            android.util.Pair r6 = android.util.Pair.create(r4, r6)     // Catch:{ all -> 0x0046 }
            r3.add(r6)     // Catch:{ all -> 0x0046 }
            boolean r4 = r2.moveToNext()     // Catch:{ all -> 0x0046 }
            if (r4 != 0) goto L_0x001b
            $closeResource(r1, r2)
            return r3
        L_0x003c:
            java.util.List r3 = java.util.Collections.emptyList()     // Catch:{ all -> 0x0046 }
            if (r2 == 0) goto L_0x0045
            $closeResource(r1, r2)
        L_0x0045:
            return r3
        L_0x0046:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0048 }
        L_0x0048:
            r3 = move-exception
            if (r2 == 0) goto L_0x004e
            $closeResource(r1, r2)
        L_0x004e:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.findAllAccountGrants():java.util.List");
    }

    private static class PreNDatabaseHelper extends SQLiteOpenHelper {
        private final Context mContext;
        private final int mUserId;

        PreNDatabaseHelper(Context context, int userId, String preNDatabaseName) {
            super(context, preNDatabaseName, (SQLiteDatabase.CursorFactory) null, 9);
            this.mContext = context;
            this.mUserId = userId;
        }

        public void onCreate(SQLiteDatabase db) {
            throw new IllegalStateException("Legacy database cannot be created - only upgraded!");
        }

        private void createSharedAccountsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE shared_accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, UNIQUE(name,type))");
        }

        private void addLastSuccessfullAuthenticatedTimeColumn(SQLiteDatabase db) {
            db.execSQL("ALTER TABLE accounts ADD COLUMN last_password_entry_time_millis_epoch DEFAULT 0");
        }

        private void addOldAccountNameColumn(SQLiteDatabase db) {
            db.execSQL("ALTER TABLE accounts ADD COLUMN previous_name");
        }

        private void addDebugTable(SQLiteDatabase db) {
            DeDatabaseHelper.createDebugTable(db);
        }

        private void createAccountsDeletionTrigger(SQLiteDatabase db) {
            db.execSQL(" CREATE TRIGGER accountsDelete DELETE ON accounts BEGIN   DELETE FROM authtokens     WHERE accounts_id=OLD._id ;   DELETE FROM extras     WHERE accounts_id=OLD._id ;   DELETE FROM grants     WHERE accounts_id=OLD._id ; END");
        }

        private void createGrantsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE grants (  accounts_id INTEGER NOT NULL, auth_token_type STRING NOT NULL,  uid INTEGER NOT NULL,  UNIQUE (accounts_id,auth_token_type,uid))");
        }

        static long insertMetaAuthTypeAndUid(SQLiteDatabase db, String authenticatorType, int uid) {
            ContentValues values = new ContentValues();
            values.put("key", AccountsDb.META_KEY_FOR_AUTHENTICATOR_UID_FOR_TYPE_PREFIX + authenticatorType);
            values.put("value", Integer.valueOf(uid));
            return db.insert(AccountsDb.TABLE_META, (String) null, values);
        }

        private void populateMetaTableWithAuthTypeAndUID(SQLiteDatabase db, Map<String, Integer> authTypeAndUIDMap) {
            for (Map.Entry<String, Integer> entry : authTypeAndUIDMap.entrySet()) {
                insertMetaAuthTypeAndUid(db, entry.getKey(), entry.getValue().intValue());
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(AccountsDb.TAG, "upgrade from version " + oldVersion + " to version " + newVersion);
            if (oldVersion == 1) {
                oldVersion++;
            }
            if (oldVersion == 2) {
                createGrantsTable(db);
                db.execSQL("DROP TRIGGER accountsDelete");
                createAccountsDeletionTrigger(db);
                oldVersion++;
            }
            if (oldVersion == 3) {
                db.execSQL("UPDATE accounts SET type = 'com.google' WHERE type == 'com.google.GAIA'");
                oldVersion++;
            }
            if (oldVersion == 4) {
                createSharedAccountsTable(db);
                oldVersion++;
            }
            if (oldVersion == 5) {
                addOldAccountNameColumn(db);
                oldVersion++;
            }
            if (oldVersion == 6) {
                addLastSuccessfullAuthenticatedTimeColumn(db);
                oldVersion++;
            }
            if (oldVersion == 7) {
                addDebugTable(db);
                oldVersion++;
            }
            if (oldVersion == 8) {
                populateMetaTableWithAuthTypeAndUID(db, AccountManagerService.getAuthenticatorTypeAndUIDForUser(this.mContext, this.mUserId));
                oldVersion++;
            }
            if (oldVersion != newVersion) {
                Log.e(AccountsDb.TAG, "failed to upgrade version " + oldVersion + " to version " + newVersion);
            }
        }

        public void onOpen(SQLiteDatabase db) {
            if (Log.isLoggable(AccountsDb.TAG, 2)) {
                Log.v(AccountsDb.TAG, "opened database accounts.db");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public List<Account> findCeAccountsNotInDe() {
        Cursor cursor = this.mDeDatabase.getReadableDatabaseUserIsUnlocked().rawQuery("SELECT name,type FROM ceDb.accounts WHERE NOT EXISTS  (SELECT _id FROM accounts WHERE _id=ceDb.accounts._id )", (String[]) null);
        try {
            List<Account> accounts = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                accounts.add(new Account(cursor.getString(0), cursor.getString(1)));
            }
            return accounts;
        } finally {
            cursor.close();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean deleteCeAccount(long accountId) {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabaseUserIsUnlocked();
        StringBuilder sb = new StringBuilder();
        sb.append("_id=");
        sb.append(accountId);
        return db.delete(CE_TABLE_ACCOUNTS, sb.toString(), (String[]) null) > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isCeDatabaseAttached() {
        return this.mDeDatabase.mCeAttached;
    }

    /* access modifiers changed from: package-private */
    public void beginTransaction() {
        this.mDeDatabase.getWritableDatabase().beginTransaction();
    }

    /* access modifiers changed from: package-private */
    public void setTransactionSuccessful() {
        this.mDeDatabase.getWritableDatabase().setTransactionSuccessful();
    }

    /* access modifiers changed from: package-private */
    public void endTransaction() {
        this.mDeDatabase.getWritableDatabase().endTransaction();
    }

    /* access modifiers changed from: package-private */
    public void attachCeDatabase(File ceDbFile) {
        CeDatabaseHelper.create(this.mContext, this.mPreNDatabaseFile, ceDbFile);
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '" + ceDbFile.getPath() + "' AS ceDb");
        boolean unused = this.mDeDatabase.mCeAttached = true;
    }

    /* access modifiers changed from: package-private */
    public long calculateDebugTableInsertionPoint() {
        try {
            SQLiteDatabase db = this.mDeDatabase.getReadableDatabase();
            int size = (int) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_DEBUG, (String[]) null);
            if (size < 64) {
                return (long) size;
            }
            return DatabaseUtils.longForQuery(db, "SELECT " + DEBUG_TABLE_KEY + " FROM " + TABLE_DEBUG + " ORDER BY " + DEBUG_TABLE_TIMESTAMP + "," + DEBUG_TABLE_KEY + " LIMIT 1", (String[]) null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Failed to open debug table" + e);
            return -1;
        }
    }

    /* access modifiers changed from: package-private */
    public SQLiteStatement compileSqlStatementForLogging() {
        SQLiteDatabase db = this.mDeDatabase.getWritableDatabase();
        return db.compileStatement("INSERT OR REPLACE INTO " + TABLE_DEBUG + " VALUES (?,?,?,?,?,?)");
    }

    /* access modifiers changed from: package-private */
    public SQLiteStatement getStatementForLogging() {
        if (this.mDebugStatementForLogging != null) {
            return this.mDebugStatementForLogging;
        }
        try {
            this.mDebugStatementForLogging = compileSqlStatementForLogging();
            return this.mDebugStatementForLogging;
        } catch (SQLiteException e) {
            Log.e(TAG, "Failed to open debug table" + e);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void closeDebugStatement() {
        synchronized (this.mDebugStatementLock) {
            if (this.mDebugStatementForLogging != null) {
                this.mDebugStatementForLogging.close();
                this.mDebugStatementForLogging = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public long reserveDebugDbInsertionPoint() {
        if (this.mDebugDbInsertionPoint == -1) {
            this.mDebugDbInsertionPoint = calculateDebugTableInsertionPoint();
            return this.mDebugDbInsertionPoint;
        }
        this.mDebugDbInsertionPoint = (this.mDebugDbInsertionPoint + 1) % 64;
        return this.mDebugDbInsertionPoint;
    }

    /* access modifiers changed from: package-private */
    public void dumpDebugTable(PrintWriter pw) {
        Cursor cursor = this.mDeDatabase.getReadableDatabase().query(TABLE_DEBUG, (String[]) null, (String) null, (String[]) null, (String) null, (String) null, DEBUG_TABLE_TIMESTAMP);
        pw.println("AccountId, Action_Type, timestamp, UID, TableName, Key");
        pw.println("Accounts History");
        while (cursor.moveToNext()) {
            try {
                pw.println(cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3) + "," + cursor.getString(4) + "," + cursor.getString(5));
            } finally {
                cursor.close();
            }
        }
    }

    public void close() {
        this.mDeDatabase.close();
    }

    static void deleteDbFileWarnIfFailed(File dbFile) {
        if (!SQLiteDatabase.deleteDatabase(dbFile)) {
            Log.w(TAG, "Database at " + dbFile + " was not deleted successfully");
        }
    }

    public static AccountsDb create(Context context, int userId, File preNDatabaseFile, File deDatabaseFile) {
        boolean newDbExists = deDatabaseFile.exists();
        DeDatabaseHelper deDatabaseHelper = new DeDatabaseHelper(context, userId, deDatabaseFile.getPath());
        if (!newDbExists && preNDatabaseFile.exists()) {
            PreNDatabaseHelper preNDatabaseHelper = new PreNDatabaseHelper(context, userId, preNDatabaseFile.getPath());
            preNDatabaseHelper.getWritableDatabase();
            preNDatabaseHelper.close();
            deDatabaseHelper.migratePreNDbToDe(preNDatabaseFile);
        }
        return new AccountsDb(deDatabaseHelper, context, preNDatabaseFile);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0067, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0068, code lost:
        if (r1 != null) goto L_0x006a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x006a, code lost:
        $closeResource(r0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006d, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0070, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0071, code lost:
        if (r1 != null) goto L_0x0073;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0073, code lost:
        $closeResource(r0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0076, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void resetDatabase(android.database.sqlite.SQLiteDatabase r6) {
        /*
            r0 = 0
            java.lang.String r1 = "SELECT name FROM sqlite_master WHERE type ='table'"
            android.database.Cursor r1 = r6.rawQuery(r1, r0)
        L_0x0007:
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x006e }
            r3 = 0
            if (r2 == 0) goto L_0x0039
            java.lang.String r2 = r1.getString(r3)     // Catch:{ all -> 0x006e }
            java.lang.String r3 = "android_metadata"
            boolean r3 = r3.equals(r2)     // Catch:{ all -> 0x006e }
            if (r3 != 0) goto L_0x0007
            java.lang.String r3 = "sqlite_sequence"
            boolean r3 = r3.equals(r2)     // Catch:{ all -> 0x006e }
            if (r3 == 0) goto L_0x0024
            goto L_0x0007
        L_0x0024:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x006e }
            r3.<init>()     // Catch:{ all -> 0x006e }
            java.lang.String r4 = "DROP TABLE IF EXISTS "
            r3.append(r4)     // Catch:{ all -> 0x006e }
            r3.append(r2)     // Catch:{ all -> 0x006e }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x006e }
            r6.execSQL(r3)     // Catch:{ all -> 0x006e }
            goto L_0x0007
        L_0x0039:
            $closeResource(r0, r1)
            java.lang.String r1 = "SELECT name FROM sqlite_master WHERE type ='trigger'"
            android.database.Cursor r1 = r6.rawQuery(r1, r0)
        L_0x0042:
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x0065 }
            if (r2 == 0) goto L_0x0061
            java.lang.String r2 = r1.getString(r3)     // Catch:{ all -> 0x0065 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0065 }
            r4.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.String r5 = "DROP TRIGGER IF EXISTS "
            r4.append(r5)     // Catch:{ all -> 0x0065 }
            r4.append(r2)     // Catch:{ all -> 0x0065 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0065 }
            r6.execSQL(r4)     // Catch:{ all -> 0x0065 }
            goto L_0x0042
        L_0x0061:
            $closeResource(r0, r1)
            return
        L_0x0065:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0067 }
        L_0x0067:
            r2 = move-exception
            if (r1 == 0) goto L_0x006d
            $closeResource(r0, r1)
        L_0x006d:
            throw r2
        L_0x006e:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0070 }
        L_0x0070:
            r2 = move-exception
            if (r1 == 0) goto L_0x0076
            $closeResource(r0, r1)
        L_0x0076:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.accounts.AccountsDb.resetDatabase(android.database.sqlite.SQLiteDatabase):void");
    }
}
