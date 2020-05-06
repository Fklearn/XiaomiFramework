package com.android.server.voiceinteraction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.soundtrigger.SoundTrigger;
import android.text.TextUtils;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_SOUND_MODEL = "CREATE TABLE sound_model(model_uuid TEXT,vendor_uuid TEXT,keyphrase_id INTEGER,type INTEGER,data BLOB,recognition_modes INTEGER,locale TEXT,hint_text TEXT,users TEXT,PRIMARY KEY (keyphrase_id,locale,users))";
    static final boolean DBG = false;
    private static final String NAME = "sound_model.db";
    static final String TAG = "SoundModelDBHelper";
    private static final int VERSION = 6;

    public interface SoundModelContract {
        public static final String KEY_DATA = "data";
        public static final String KEY_HINT_TEXT = "hint_text";
        public static final String KEY_KEYPHRASE_ID = "keyphrase_id";
        public static final String KEY_LOCALE = "locale";
        public static final String KEY_MODEL_UUID = "model_uuid";
        public static final String KEY_RECOGNITION_MODES = "recognition_modes";
        public static final String KEY_TYPE = "type";
        public static final String KEY_USERS = "users";
        public static final String KEY_VENDOR_UUID = "vendor_uuid";
        public static final String TABLE = "sound_model";
    }

    public DatabaseHelper(Context context) {
        super(context, NAME, (SQLiteDatabase.CursorFactory) null, 6);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SOUND_MODEL);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS sound_model");
            onCreate(db);
        } else if (oldVersion == 4) {
            Slog.d(TAG, "Adding vendor UUID column");
            db.execSQL("ALTER TABLE sound_model ADD COLUMN vendor_uuid TEXT");
            oldVersion++;
        }
        if (oldVersion == 5) {
            Cursor c = db.rawQuery("SELECT * FROM sound_model", (String[]) null);
            List<SoundModelRecord> old_records = new ArrayList<>();
            try {
                if (c.moveToFirst()) {
                    do {
                        old_records.add(new SoundModelRecord(5, c));
                    } while (c.moveToNext());
                }
            } catch (Exception e) {
                Slog.e(TAG, "Failed to extract V5 record", e);
            } catch (Throwable th) {
                c.close();
                throw th;
            }
            c.close();
            db.execSQL("DROP TABLE IF EXISTS sound_model");
            onCreate(db);
            for (SoundModelRecord record : old_records) {
                if (record.ifViolatesV6PrimaryKeyIsFirstOfAnyDuplicates(old_records)) {
                    try {
                        long return_value = record.writeToDatabase(6, db);
                        if (return_value == -1) {
                            Slog.e(TAG, "Database write failed " + record.modelUuid + ": " + return_value);
                        }
                    } catch (Exception e2) {
                        Slog.e(TAG, "Failed to update V6 record " + record.modelUuid, e2);
                    }
                }
            }
            int oldVersion2 = oldVersion + 1;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00a5, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean updateKeyphraseSoundModel(android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel r10) {
        /*
            r9 = this;
            monitor-enter(r9)
            android.database.sqlite.SQLiteDatabase r0 = r9.getWritableDatabase()     // Catch:{ all -> 0x00a6 }
            android.content.ContentValues r1 = new android.content.ContentValues     // Catch:{ all -> 0x00a6 }
            r1.<init>()     // Catch:{ all -> 0x00a6 }
            java.lang.String r2 = "model_uuid"
            java.util.UUID r3 = r10.uuid     // Catch:{ all -> 0x00a6 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r3)     // Catch:{ all -> 0x00a6 }
            java.util.UUID r2 = r10.vendorUuid     // Catch:{ all -> 0x00a6 }
            if (r2 == 0) goto L_0x0026
            java.lang.String r2 = "vendor_uuid"
            java.util.UUID r3 = r10.vendorUuid     // Catch:{ all -> 0x00a6 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r3)     // Catch:{ all -> 0x00a6 }
        L_0x0026:
            java.lang.String r2 = "type"
            r3 = 0
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r4)     // Catch:{ all -> 0x00a6 }
            java.lang.String r2 = "data"
            byte[] r4 = r10.data     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r4)     // Catch:{ all -> 0x00a6 }
            android.hardware.soundtrigger.SoundTrigger$Keyphrase[] r2 = r10.keyphrases     // Catch:{ all -> 0x00a6 }
            if (r2 == 0) goto L_0x00a4
            android.hardware.soundtrigger.SoundTrigger$Keyphrase[] r2 = r10.keyphrases     // Catch:{ all -> 0x00a6 }
            int r2 = r2.length     // Catch:{ all -> 0x00a6 }
            r4 = 1
            if (r2 != r4) goto L_0x00a4
            java.lang.String r2 = "keyphrase_id"
            android.hardware.soundtrigger.SoundTrigger$Keyphrase[] r5 = r10.keyphrases     // Catch:{ all -> 0x00a6 }
            r5 = r5[r3]     // Catch:{ all -> 0x00a6 }
            int r5 = r5.id     // Catch:{ all -> 0x00a6 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r5)     // Catch:{ all -> 0x00a6 }
            java.lang.String r2 = "recognition_modes"
            android.hardware.soundtrigger.SoundTrigger$Keyphrase[] r5 = r10.keyphrases     // Catch:{ all -> 0x00a6 }
            r5 = r5[r3]     // Catch:{ all -> 0x00a6 }
            int r5 = r5.recognitionModes     // Catch:{ all -> 0x00a6 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r5)     // Catch:{ all -> 0x00a6 }
            java.lang.String r2 = "users"
            android.hardware.soundtrigger.SoundTrigger$Keyphrase[] r5 = r10.keyphrases     // Catch:{ all -> 0x00a6 }
            r5 = r5[r3]     // Catch:{ all -> 0x00a6 }
            int[] r5 = r5.users     // Catch:{ all -> 0x00a6 }
            java.lang.String r5 = getCommaSeparatedString(r5)     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r5)     // Catch:{ all -> 0x00a6 }
            java.lang.String r2 = "locale"
            android.hardware.soundtrigger.SoundTrigger$Keyphrase[] r5 = r10.keyphrases     // Catch:{ all -> 0x00a6 }
            r5 = r5[r3]     // Catch:{ all -> 0x00a6 }
            java.lang.String r5 = r5.locale     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r5)     // Catch:{ all -> 0x00a6 }
            java.lang.String r2 = "hint_text"
            android.hardware.soundtrigger.SoundTrigger$Keyphrase[] r5 = r10.keyphrases     // Catch:{ all -> 0x00a6 }
            r5 = r5[r3]     // Catch:{ all -> 0x00a6 }
            java.lang.String r5 = r5.text     // Catch:{ all -> 0x00a6 }
            r1.put(r2, r5)     // Catch:{ all -> 0x00a6 }
            java.lang.String r2 = "sound_model"
            r5 = 0
            r6 = 5
            long r5 = r0.insertWithOnConflict(r2, r5, r1, r6)     // Catch:{ all -> 0x009f }
            r7 = -1
            int r2 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x009a
            r3 = r4
        L_0x009a:
            r0.close()     // Catch:{ all -> 0x00a6 }
            monitor-exit(r9)     // Catch:{ all -> 0x00a6 }
            return r3
        L_0x009f:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x00a6 }
            throw r2     // Catch:{ all -> 0x00a6 }
        L_0x00a4:
            monitor-exit(r9)     // Catch:{ all -> 0x00a6 }
            return r3
        L_0x00a6:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x00a6 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.voiceinteraction.DatabaseHelper.updateKeyphraseSoundModel(android.hardware.soundtrigger.SoundTrigger$KeyphraseSoundModel):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public boolean deleteKeyphraseSoundModel(int keyphraseId, int userHandle, String bcp47Locale) {
        String bcp47Locale2 = Locale.forLanguageTag(bcp47Locale).toLanguageTag();
        synchronized (this) {
            SoundTrigger.KeyphraseSoundModel soundModel = getKeyphraseSoundModel(keyphraseId, userHandle, bcp47Locale2);
            boolean z = false;
            if (soundModel == null) {
                return false;
            }
            SQLiteDatabase db = getWritableDatabase();
            try {
                if (db.delete(SoundModelContract.TABLE, "model_uuid='" + soundModel.uuid.toString() + "'", (String[]) null) != 0) {
                    z = true;
                }
                return z;
            } finally {
                db.close();
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 21 */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0110, code lost:
        r20 = r0;
        r0 = r8;
        r2 = new android.hardware.soundtrigger.SoundTrigger.Keyphrase[]{new android.hardware.soundtrigger.SoundTrigger.Keyphrase(r22, r5, r6, r7, r4)};
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0126, code lost:
        if (r15 == null) goto L_0x012d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0128, code lost:
        r3 = java.util.UUID.fromString(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x012d, code lost:
        r4 = new android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel(java.util.UUID.fromString(r13), r3, r0, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        r11.close();
        r10.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x013e, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel getKeyphraseSoundModel(int r22, int r23, java.lang.String r24) {
        /*
            r21 = this;
            java.util.Locale r0 = java.util.Locale.forLanguageTag(r24)
            java.lang.String r1 = r0.toLanguageTag()
            monitor-enter(r21)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0162 }
            r0.<init>()     // Catch:{ all -> 0x0162 }
            java.lang.String r2 = "SELECT  * FROM sound_model WHERE keyphrase_id= '"
            r0.append(r2)     // Catch:{ all -> 0x0162 }
            r2 = r22
            r0.append(r2)     // Catch:{ all -> 0x0162 }
            java.lang.String r3 = "' AND "
            r0.append(r3)     // Catch:{ all -> 0x0162 }
            java.lang.String r3 = "locale"
            r0.append(r3)     // Catch:{ all -> 0x0162 }
            java.lang.String r3 = "='"
            r0.append(r3)     // Catch:{ all -> 0x0162 }
            r0.append(r1)     // Catch:{ all -> 0x0162 }
            java.lang.String r3 = "'"
            r0.append(r3)     // Catch:{ all -> 0x0162 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0162 }
            r9 = r0
            android.database.sqlite.SQLiteDatabase r0 = r21.getReadableDatabase()     // Catch:{ all -> 0x0162 }
            r10 = r0
            r0 = 0
            android.database.Cursor r3 = r10.rawQuery(r9, r0)     // Catch:{ all -> 0x0162 }
            r11 = r3
            boolean r3 = r11.moveToFirst()     // Catch:{ all -> 0x0156 }
            if (r3 == 0) goto L_0x013f
        L_0x0046:
            java.lang.String r3 = "type"
            int r3 = r11.getColumnIndex(r3)     // Catch:{ all -> 0x0156 }
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x0156 }
            r12 = r3
            if (r12 == 0) goto L_0x005a
            r18 = r1
            r1 = r23
            goto L_0x0102
        L_0x005a:
            java.lang.String r3 = "model_uuid"
            int r3 = r11.getColumnIndex(r3)     // Catch:{ all -> 0x0156 }
            java.lang.String r3 = r11.getString(r3)     // Catch:{ all -> 0x0156 }
            r13 = r3
            if (r13 != 0) goto L_0x007c
            java.lang.String r3 = "SoundModelDBHelper"
            java.lang.String r4 = "Ignoring SoundModel since it doesn't specify an ID"
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0075 }
            r18 = r1
            r1 = r23
            goto L_0x0102
        L_0x0075:
            r0 = move-exception
            r18 = r1
            r1 = r23
            goto L_0x015b
        L_0x007c:
            r3 = 0
            java.lang.String r4 = "vendor_uuid"
            int r4 = r11.getColumnIndex(r4)     // Catch:{ all -> 0x0156 }
            r14 = r4
            r4 = -1
            if (r14 == r4) goto L_0x008f
            java.lang.String r4 = r11.getString(r14)     // Catch:{ all -> 0x0075 }
            r3 = r4
            r15 = r3
            goto L_0x0090
        L_0x008f:
            r15 = r3
        L_0x0090:
            java.lang.String r3 = "data"
            int r3 = r11.getColumnIndex(r3)     // Catch:{ all -> 0x0156 }
            byte[] r3 = r11.getBlob(r3)     // Catch:{ all -> 0x0156 }
            r8 = r3
            java.lang.String r3 = "recognition_modes"
            int r3 = r11.getColumnIndex(r3)     // Catch:{ all -> 0x0156 }
            int r5 = r11.getInt(r3)     // Catch:{ all -> 0x0156 }
            java.lang.String r3 = "users"
            int r3 = r11.getColumnIndex(r3)     // Catch:{ all -> 0x0156 }
            java.lang.String r3 = r11.getString(r3)     // Catch:{ all -> 0x0156 }
            int[] r3 = getArrayForCommaSeparatedString(r3)     // Catch:{ all -> 0x0156 }
            r4 = r3
            java.lang.String r3 = "locale"
            int r3 = r11.getColumnIndex(r3)     // Catch:{ all -> 0x0156 }
            java.lang.String r6 = r11.getString(r3)     // Catch:{ all -> 0x0156 }
            java.lang.String r3 = "hint_text"
            int r3 = r11.getColumnIndex(r3)     // Catch:{ all -> 0x0156 }
            java.lang.String r7 = r11.getString(r3)     // Catch:{ all -> 0x0156 }
            if (r4 != 0) goto L_0x00da
            java.lang.String r3 = "SoundModelDBHelper"
            java.lang.String r0 = "Ignoring SoundModel since it doesn't specify users"
            android.util.Slog.w(r3, r0)     // Catch:{ all -> 0x0075 }
            r18 = r1
            r1 = r23
            goto L_0x0102
        L_0x00da:
            r0 = 0
            int r3 = r4.length     // Catch:{ all -> 0x0156 }
            r16 = 0
            r17 = r0
            r0 = r16
        L_0x00e2:
            if (r0 >= r3) goto L_0x00f9
            r18 = r4[r0]     // Catch:{ all -> 0x0156 }
            r19 = r18
            r18 = r1
            r2 = r19
            r1 = r23
            if (r1 != r2) goto L_0x00f2
            r0 = 1
            goto L_0x00ff
        L_0x00f2:
            int r0 = r0 + 1
            r2 = r22
            r1 = r18
            goto L_0x00e2
        L_0x00f9:
            r18 = r1
            r1 = r23
            r0 = r17
        L_0x00ff:
            if (r0 != 0) goto L_0x0110
        L_0x0102:
            boolean r0 = r11.moveToNext()     // Catch:{ all -> 0x0154 }
            if (r0 != 0) goto L_0x0109
            goto L_0x0143
        L_0x0109:
            r2 = r22
            r1 = r18
            r0 = 0
            goto L_0x0046
        L_0x0110:
            r2 = 1
            android.hardware.soundtrigger.SoundTrigger$Keyphrase[] r2 = new android.hardware.soundtrigger.SoundTrigger.Keyphrase[r2]     // Catch:{ all -> 0x0154 }
            android.hardware.soundtrigger.SoundTrigger$Keyphrase r17 = new android.hardware.soundtrigger.SoundTrigger$Keyphrase     // Catch:{ all -> 0x0154 }
            r3 = r17
            r19 = r4
            r4 = r22
            r20 = r0
            r0 = r8
            r8 = r19
            r3.<init>(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0154 }
            r2[r16] = r17     // Catch:{ all -> 0x0154 }
            r3 = 0
            if (r15 == 0) goto L_0x012d
            java.util.UUID r4 = java.util.UUID.fromString(r15)     // Catch:{ all -> 0x0154 }
            r3 = r4
        L_0x012d:
            android.hardware.soundtrigger.SoundTrigger$KeyphraseSoundModel r4 = new android.hardware.soundtrigger.SoundTrigger$KeyphraseSoundModel     // Catch:{ all -> 0x0154 }
            java.util.UUID r8 = java.util.UUID.fromString(r13)     // Catch:{ all -> 0x0154 }
            r4.<init>(r8, r3, r0, r2)     // Catch:{ all -> 0x0154 }
            r11.close()     // Catch:{ all -> 0x0169 }
            r10.close()     // Catch:{ all -> 0x0169 }
            monitor-exit(r21)     // Catch:{ all -> 0x0169 }
            return r4
        L_0x013f:
            r18 = r1
            r1 = r23
        L_0x0143:
            java.lang.String r0 = "SoundModelDBHelper"
            java.lang.String r2 = "No SoundModel available for the given keyphrase"
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x0154 }
            r11.close()     // Catch:{ all -> 0x0169 }
            r10.close()     // Catch:{ all -> 0x0169 }
            monitor-exit(r21)     // Catch:{ all -> 0x0169 }
            r0 = 0
            return r0
        L_0x0154:
            r0 = move-exception
            goto L_0x015b
        L_0x0156:
            r0 = move-exception
            r18 = r1
            r1 = r23
        L_0x015b:
            r11.close()     // Catch:{ all -> 0x0169 }
            r10.close()     // Catch:{ all -> 0x0169 }
            throw r0     // Catch:{ all -> 0x0169 }
        L_0x0162:
            r0 = move-exception
            r18 = r1
            r1 = r23
        L_0x0167:
            monitor-exit(r21)     // Catch:{ all -> 0x0169 }
            throw r0
        L_0x0169:
            r0 = move-exception
            goto L_0x0167
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.voiceinteraction.DatabaseHelper.getKeyphraseSoundModel(int, int, java.lang.String):android.hardware.soundtrigger.SoundTrigger$KeyphraseSoundModel");
    }

    private static String getCommaSeparatedString(int[] users) {
        if (users == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < users.length; i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append(users[i]);
        }
        return sb.toString();
    }

    private static int[] getArrayForCommaSeparatedString(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        String[] usersStr = text.split(",");
        int[] users = new int[usersStr.length];
        for (int i = 0; i < usersStr.length; i++) {
            users[i] = Integer.parseInt(usersStr[i]);
        }
        return users;
    }

    private static class SoundModelRecord {
        public final byte[] data;
        public final String hintText;
        public final int keyphraseId;
        public final String locale;
        public final String modelUuid;
        public final int recognitionModes;
        public final int type;
        public final String users;
        public final String vendorUuid;

        public SoundModelRecord(int version, Cursor c) {
            this.modelUuid = c.getString(c.getColumnIndex("model_uuid"));
            if (version >= 5) {
                this.vendorUuid = c.getString(c.getColumnIndex("vendor_uuid"));
            } else {
                this.vendorUuid = null;
            }
            this.keyphraseId = c.getInt(c.getColumnIndex(SoundModelContract.KEY_KEYPHRASE_ID));
            this.type = c.getInt(c.getColumnIndex(SoundModelContract.KEY_TYPE));
            this.data = c.getBlob(c.getColumnIndex("data"));
            this.recognitionModes = c.getInt(c.getColumnIndex(SoundModelContract.KEY_RECOGNITION_MODES));
            this.locale = c.getString(c.getColumnIndex(SoundModelContract.KEY_LOCALE));
            this.hintText = c.getString(c.getColumnIndex(SoundModelContract.KEY_HINT_TEXT));
            this.users = c.getString(c.getColumnIndex(SoundModelContract.KEY_USERS));
        }

        private boolean V6PrimaryKeyMatches(SoundModelRecord record) {
            return this.keyphraseId == record.keyphraseId && stringComparisonHelper(this.locale, record.locale) && stringComparisonHelper(this.users, record.users);
        }

        public boolean ifViolatesV6PrimaryKeyIsFirstOfAnyDuplicates(List<SoundModelRecord> records) {
            for (SoundModelRecord record : records) {
                if (this != record && V6PrimaryKeyMatches(record) && !Arrays.equals(this.data, record.data)) {
                    return false;
                }
            }
            for (SoundModelRecord record2 : records) {
                if (V6PrimaryKeyMatches(record2)) {
                    if (this == record2) {
                        return true;
                    }
                    return false;
                }
            }
            return true;
        }

        public long writeToDatabase(int version, SQLiteDatabase db) {
            ContentValues values = new ContentValues();
            values.put("model_uuid", this.modelUuid);
            if (version >= 5) {
                values.put("vendor_uuid", this.vendorUuid);
            }
            values.put(SoundModelContract.KEY_KEYPHRASE_ID, Integer.valueOf(this.keyphraseId));
            values.put(SoundModelContract.KEY_TYPE, Integer.valueOf(this.type));
            values.put("data", this.data);
            values.put(SoundModelContract.KEY_RECOGNITION_MODES, Integer.valueOf(this.recognitionModes));
            values.put(SoundModelContract.KEY_LOCALE, this.locale);
            values.put(SoundModelContract.KEY_HINT_TEXT, this.hintText);
            values.put(SoundModelContract.KEY_USERS, this.users);
            return db.insertWithOnConflict(SoundModelContract.TABLE, (String) null, values, 5);
        }

        private static boolean stringComparisonHelper(String a, String b) {
            if (a != null) {
                return a.equals(b);
            }
            return a == b;
        }
    }
}
