package com.miui.antispam.db;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import b.b.a.a;
import b.b.a.c;
import b.b.a.e.c;
import b.b.a.e.n;
import b.b.c.i.e;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.antispam.policy.SmartSmsFilterPolicy;
import com.miui.antispam.policy.a.g;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.networkassistant.dual.Sim;
import java.util.ArrayList;
import miui.accounts.ExtraAccountManager;
import miui.cloud.common.XSimChangeNotification;
import miui.provider.ExtraTelephony;

public class AntiSpamProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static final UriMatcher f2335a = new UriMatcher(-1);

    /* renamed from: b  reason: collision with root package name */
    private c f2336b;

    /* renamed from: c  reason: collision with root package name */
    private c f2337c;

    static {
        f2335a.addURI("antispam", "sms_judge", 9);
        f2335a.addURI("antispam", "call_judge", 10);
        f2335a.addURI("antispam", "url_judge", 13);
        f2335a.addURI("antispam", "service_num_judge", 14);
        f2335a.addURI("antispam", "call_transfer_intercept_judge", 21);
        f2335a.addURI("antispam", "phone_list", 1);
        f2335a.addURI("antispam", "phone_list/*", 2);
        f2335a.addURI("antispam", "log", 3);
        f2335a.addURI("antispam", "log/*", 4);
        f2335a.addURI("antispam", "log_sms", 5);
        f2335a.addURI("antispam", "logconversation", 6);
        f2335a.addURI("antispam", "keyword", 7);
        f2335a.addURI("antispam", "keyword/*", 8);
        f2335a.addURI("antispam", "mode", 15);
        f2335a.addURI("antispam", "mode/*", 16);
        f2335a.addURI("antispam", "sim", 17);
        f2335a.addURI("antispam", "sim/*", 18);
        f2335a.addURI("antispam", "unsynced_count", 11);
        f2335a.addURI("antispam", "synced_count", 12);
        f2335a.addURI("antispam", "allow_repeat", 22);
        f2335a.addURI("antispam", "account", 36);
        f2335a.addURI("antispam", "account/*", 37);
        f2335a.addURI("antispam", "markednumber", 30);
        f2335a.addURI("antispam", "markednumber/*", 31);
        f2335a.addURI("antispam", "cloudantispam", 32);
        f2335a.addURI("antispam", "cloudantispam/*", 33);
        f2335a.addURI("antispam", "category", 34);
        f2335a.addURI("antispam", "category/*", 35);
        f2335a.addURI("antispam", "report_sms", 19);
        f2335a.addURI("antispam", "report_sms_pending", 20);
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0052  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.database.Cursor a(android.database.sqlite.SQLiteDatabase r21, android.database.sqlite.SQLiteQueryBuilder r22, java.lang.String[] r23, java.lang.String r24, java.lang.String[] r25, java.lang.String r26) {
        /*
            r20 = this;
            r7 = r23
            r8 = r24
            r9 = r25
            java.lang.String r10 = "data1"
            java.lang.String r11 = "reason"
            java.lang.String r0 = "number"
            java.lang.String[] r14 = new java.lang.String[]{r0, r11, r10}
            android.content.Context r0 = r20.getContext()
            android.content.ContentResolver r1 = r0.getContentResolver()
            android.net.Uri r2 = miui.provider.ExtraTelephony.MmsSms.BLOCKED_CONVERSATION_CONTENT_URI
            r4 = 0
            r5 = 0
            r3 = r23
            r6 = r26
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6)
            r0 = 0
            r2 = 0
            java.lang.String r3 = "type = 2"
            if (r8 == 0) goto L_0x0046
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r3 = " AND "
            r4.append(r3)
            r4.append(r8)
            java.lang.String r3 = r4.toString()
            if (r9 == 0) goto L_0x0046
            int r4 = r9.length
            if (r4 <= 0) goto L_0x0046
            r4 = r9[r2]
            goto L_0x0047
        L_0x0046:
            r4 = r0
        L_0x0047:
            r15 = r3
            r3 = 1
            if (r8 == 0) goto L_0x0052
            java.lang.String[] r5 = new java.lang.String[r3]
            r5[r2] = r4
            r16 = r5
            goto L_0x0054
        L_0x0052:
            r16 = r0
        L_0x0054:
            r17 = 0
            r18 = 0
            r19 = 0
            r12 = r22
            r13 = r21
            android.database.Cursor r5 = r12.query(r13, r14, r15, r16, r17, r18, r19)
            if (r1 == 0) goto L_0x0153
            if (r5 != 0) goto L_0x0068
            goto L_0x0153
        L_0x0068:
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
        L_0x006d:
            boolean r0 = r5.moveToNext()     // Catch:{ Exception -> 0x0094 }
            if (r0 == 0) goto L_0x008d
            java.lang.String r0 = r5.getString(r2)     // Catch:{ Exception -> 0x0094 }
            java.util.AbstractMap$SimpleEntry r12 = new java.util.AbstractMap$SimpleEntry     // Catch:{ Exception -> 0x0094 }
            int r13 = r5.getInt(r3)     // Catch:{ Exception -> 0x0094 }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x0094 }
            r14 = 2
            java.lang.String r14 = r5.getString(r14)     // Catch:{ Exception -> 0x0094 }
            r12.<init>(r13, r14)     // Catch:{ Exception -> 0x0094 }
            r6.put(r0, r12)     // Catch:{ Exception -> 0x0094 }
            goto L_0x006d
        L_0x008d:
            r5.close()
            goto L_0x0099
        L_0x0091:
            r0 = move-exception
            goto L_0x014f
        L_0x0094:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x0091 }
            goto L_0x008d
        L_0x0099:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r5 = "_id"
            r0.add(r5)
            r5 = r3
        L_0x00a4:
            int r12 = r7.length
            if (r5 >= r12) goto L_0x00af
            r12 = r7[r5]
            r0.add(r12)
            int r5 = r5 + 1
            goto L_0x00a4
        L_0x00af:
            r0.add(r10)
            r0.add(r11)
            android.database.MatrixCursor r5 = new android.database.MatrixCursor
            java.lang.String[] r7 = new java.lang.String[r2]
            java.lang.Object[] r0 = r0.toArray(r7)
            java.lang.String[] r0 = (java.lang.String[]) r0
            r5.<init>(r0)
        L_0x00c2:
            boolean r0 = r1.moveToNext()     // Catch:{ NumberFormatException -> 0x0145 }
            if (r0 == 0) goto L_0x013f
            if (r9 == 0) goto L_0x00db
            int r0 = r9.length     // Catch:{ NumberFormatException -> 0x0145 }
            if (r0 <= r3) goto L_0x00db
            int r0 = r1.getPosition()     // Catch:{ NumberFormatException -> 0x0145 }
            r7 = r9[r3]     // Catch:{ NumberFormatException -> 0x0145 }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x0145 }
            int r7 = r7 - r3
            if (r0 <= r7) goto L_0x00db
            goto L_0x013f
        L_0x00db:
            java.lang.String r0 = "address"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ NumberFormatException -> 0x0145 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ NumberFormatException -> 0x0145 }
            java.lang.String r0 = b.b.a.e.n.f(r0)     // Catch:{ NumberFormatException -> 0x0145 }
            java.lang.Object r0 = r6.remove(r0)     // Catch:{ NumberFormatException -> 0x0145 }
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ NumberFormatException -> 0x0145 }
            r7 = 4
            if (r0 != 0) goto L_0x00fc
            if (r8 == 0) goto L_0x00fc
            if (r4 == 0) goto L_0x00c2
            int r10 = java.lang.Integer.parseInt(r4)     // Catch:{ NumberFormatException -> 0x0145 }
            if (r10 != r7) goto L_0x00c2
        L_0x00fc:
            android.database.MatrixCursor$RowBuilder r10 = r5.newRow()     // Catch:{ NumberFormatException -> 0x0145 }
            r11 = r2
        L_0x0101:
            int r12 = r1.getColumnCount()     // Catch:{ NumberFormatException -> 0x0145 }
            if (r11 >= r12) goto L_0x0111
            java.lang.String r12 = r1.getString(r11)     // Catch:{ NumberFormatException -> 0x0145 }
            r10.add(r12)     // Catch:{ NumberFormatException -> 0x0145 }
            int r11 = r11 + 1
            goto L_0x0101
        L_0x0111:
            java.lang.String r11 = ""
            if (r0 == 0) goto L_0x0129
            java.lang.Object r12 = r0.getKey()     // Catch:{ NumberFormatException -> 0x0145 }
            java.lang.Integer r12 = (java.lang.Integer) r12     // Catch:{ NumberFormatException -> 0x0145 }
            java.lang.Object r0 = r0.getValue()     // Catch:{ NumberFormatException -> 0x0145 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ NumberFormatException -> 0x0145 }
            if (r0 != 0) goto L_0x0124
            goto L_0x0125
        L_0x0124:
            r11 = r0
        L_0x0125:
            r10.add(r11)     // Catch:{ NumberFormatException -> 0x0145 }
            goto L_0x0130
        L_0x0129:
            r10.add(r11)     // Catch:{ NumberFormatException -> 0x0145 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r7)     // Catch:{ NumberFormatException -> 0x0145 }
        L_0x0130:
            if (r12 != 0) goto L_0x0133
            goto L_0x0137
        L_0x0133:
            int r7 = r12.intValue()     // Catch:{ NumberFormatException -> 0x0145 }
        L_0x0137:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r7)     // Catch:{ NumberFormatException -> 0x0145 }
            r10.add(r0)     // Catch:{ NumberFormatException -> 0x0145 }
            goto L_0x00c2
        L_0x013f:
            r1.close()
            goto L_0x014a
        L_0x0143:
            r0 = move-exception
            goto L_0x014b
        L_0x0145:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x0143 }
            goto L_0x013f
        L_0x014a:
            return r5
        L_0x014b:
            r1.close()
            throw r0
        L_0x014f:
            r5.close()
            throw r0
        L_0x0153:
            miui.util.IOUtils.closeQuietly(r1)
            miui.util.IOUtils.closeQuietly(r5)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.db.AntiSpamProvider.a(android.database.sqlite.SQLiteDatabase, android.database.sqlite.SQLiteQueryBuilder, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    private Uri a(SQLiteDatabase sQLiteDatabase, ContentValues contentValues, String str, int i, int i2, Integer num) {
        int i3 = 1;
        if (num != null && !b.b.a.e.c.b(getContext(), num.intValue())) {
            b.b.a.e.c.a(getContext(), num.intValue(), true);
        }
        Uri uri = null;
        if (!n.a(getContext(), str, i, i2, num == null ? 1 : num.intValue())) {
            contentValues.put(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, num);
            long insert = sQLiteDatabase.insert("phone_list", (String) null, contentValues);
            if (insert > 0) {
                c cVar = this.f2337c;
                if (num != null) {
                    i3 = num.intValue();
                }
                cVar.a(str, i, i2, i3);
                Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(getContext());
                if (xiaomiAccount != null && !contentValues.containsKey("record_id")) {
                    ContentResolver.requestSync(xiaomiAccount, "antispam", new Bundle());
                }
                Uri withAppendedId = ContentUris.withAppendedId(c.b.f1420a, insert);
                getContext().getContentResolver().notifyChange(c.b.f1420a, (ContentObserver) null, false);
                uri = withAppendedId;
            }
            if (a.f1308a) {
                Log.i("AntiSpamProvider", "insert URI_PHONELIST : number = " + str);
            }
        }
        return uri;
    }

    private Uri a(SQLiteDatabase sQLiteDatabase, ContentValues contentValues, String str, Integer num, Integer num2) {
        Uri uri;
        int i = 1;
        if (num2 != null && !b.b.a.e.c.b(getContext(), num2.intValue())) {
            b.b.a.e.c.a(getContext(), num2.intValue(), true);
        }
        contentValues.put(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, num2);
        long insert = sQLiteDatabase.insert("keyword", (String) null, contentValues);
        if (insert > 0) {
            b.b.a.c cVar = this.f2337c;
            int intValue = num == null ? 1 : num.intValue();
            if (num2 != null) {
                i = num2.intValue();
            }
            cVar.a(str, intValue, i);
            uri = ContentUris.withAppendedId(ExtraTelephony.Keyword.CONTENT_URI, insert);
            getContext().getContentResolver().notifyChange(ExtraTelephony.Keyword.CONTENT_URI, (ContentObserver) null, false);
        } else {
            uri = null;
        }
        if (a.f1308a) {
            Log.i("AntiSpamProvider", "insert URI_KEYWORD : data = " + str);
        }
        return uri;
    }

    private String a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String normalizeNumber = ExtraTelephony.normalizeNumber(str);
        int length = normalizeNumber.length();
        if (length < 7) {
            return "substr(number, -7, 7)" + "='" + normalizeNumber + "'";
        }
        String substring = normalizeNumber.substring(length - 7);
        return "substr(number, -7, 7)" + "='" + substring + "' AND PHONE_NUMBERS_EQUAL(number, '" + normalizeNumber + "', 0)";
    }

    private boolean a() {
        String str;
        try {
            str = getCallingPackage();
        } catch (Exception e) {
            Log.d("AntiSpamProvider", e.toString());
            str = null;
        }
        return "com.android.mms".equals(str);
    }

    /* JADX INFO: finally extract failed */
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> arrayList) {
        SQLiteDatabase writableDatabase = this.f2336b.getWritableDatabase();
        if (writableDatabase.enableWriteAheadLogging()) {
            writableDatabase.beginTransactionNonExclusive();
        } else {
            writableDatabase.beginTransaction();
        }
        try {
            ContentProviderResult[] applyBatch = super.applyBatch(arrayList);
            writableDatabase.setTransactionSuccessful();
            writableDatabase.endTransaction();
            return applyBatch;
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            writableDatabase.endTransaction();
            return null;
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
    }

    @Nullable
    public Bundle call(@NonNull String str, @Nullable String str2, @Nullable Bundle bundle) {
        if ("getBlockKeyword".equals(str)) {
            SQLiteDatabase readableDatabase = this.f2336b.getReadableDatabase();
            SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
            sQLiteQueryBuilder.setTables("fwlog");
            Cursor query = sQLiteQueryBuilder.query(readableDatabase, new String[]{"data1"}, "number = ? AND type = 2", new String[]{str2}, (String) null, (String) null, (String) null);
            if (query != null && query.moveToFirst()) {
                String string = query.getString(0);
                query.close();
                Bundle bundle2 = new Bundle();
                bundle2.putString("blockKeyword", string);
                return bundle2;
            }
        } else if ("setMmsBlockType".equals(str) || "getMmsBlockType".equals(str)) {
            if (bundle == null) {
                return null;
            }
            int i = bundle.getInt(Sim.SIM_ID, 1);
            if ("setMmsBlockType".equals(str)) {
                d.b(getContext(), "mms_mode", i, bundle.getInt("blockType", 1));
            } else {
                Bundle bundle3 = new Bundle();
                bundle3.putInt("blockType", d.a(getContext(), "mms_mode", i, 2));
                return bundle3;
            }
        } else if ("initSmsEngine".equals(str)) {
            ((SmartSmsFilterPolicy) this.f2337c.a(g.SMART_SMS_FILTER_POLICY)).initSmsEngine(true);
        }
        return super.call(str, str2, bundle);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0092, code lost:
        if (r12 > 0) goto L_0x0094;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a3, code lost:
        if (r12 > 0) goto L_0x0094;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00c8, code lost:
        if (r12 > 0) goto L_0x00ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00ff, code lost:
        if (r12 > 0) goto L_0x00ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01a8, code lost:
        if (r12 > 0) goto L_0x0173;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int delete(android.net.Uri r12, java.lang.String r13, java.lang.String[] r14) {
        /*
            r11 = this;
            com.miui.antispam.db.c r0 = r11.f2336b
            android.database.sqlite.SQLiteDatabase r0 = r0.getWritableDatabase()
            android.content.UriMatcher r1 = f2335a
            int r1 = r1.match(r12)
            r2 = 3
            java.lang.String r3 = "phone_list"
            java.lang.String r4 = "AntiSpamProvider"
            r5 = 2
            r6 = 1
            r7 = 0
            r8 = 0
            if (r1 == r6) goto L_0x017f
            java.lang.String r9 = "_id = ? "
            if (r1 == r5) goto L_0x013b
            java.lang.String r3 = "fwlog"
            if (r1 == r2) goto L_0x011c
            r10 = 4
            if (r1 == r10) goto L_0x0102
            r3 = 7
            java.lang.String r10 = "keyword"
            if (r1 == r3) goto L_0x00d6
            r2 = 8
            if (r1 == r2) goto L_0x00a6
            r2 = 15
            java.lang.String r3 = "mode"
            if (r1 == r2) goto L_0x009f
            r2 = 16
            if (r1 == r2) goto L_0x0086
            r2 = 18
            if (r1 == r2) goto L_0x006b
            r2 = 20
            if (r1 != r2) goto L_0x0054
            java.lang.String r12 = "reportSmsPending"
            int r12 = r0.delete(r12, r13, r14)
            if (r12 <= 0) goto L_0x01ab
            android.content.Context r13 = r11.getContext()
            android.content.ContentResolver r13 = r13.getContentResolver()
            android.net.Uri r14 = b.b.a.a.b.f1310a
        L_0x004f:
            r13.notifyChange(r14, r7, r8)
            goto L_0x01ab
        L_0x0054:
            java.lang.UnsupportedOperationException r13 = new java.lang.UnsupportedOperationException
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r0 = "Cannot delete that URL: "
            r14.append(r0)
            r14.append(r12)
            java.lang.String r12 = r14.toString()
            r13.<init>(r12)
            throw r13
        L_0x006b:
            java.lang.String[] r13 = new java.lang.String[r6]
            java.lang.String r12 = r12.getLastPathSegment()
            r13[r8] = r12
            java.lang.String r12 = "sim"
            int r12 = r0.delete(r12, r9, r13)
            if (r12 <= 0) goto L_0x01ab
            android.content.Context r13 = r11.getContext()
            android.content.ContentResolver r13 = r13.getContentResolver()
            android.net.Uri r14 = miui.provider.ExtraTelephony.AntiSpamSim.CONTENT_URI
            goto L_0x004f
        L_0x0086:
            java.lang.String[] r13 = new java.lang.String[r6]
            java.lang.String r12 = r12.getLastPathSegment()
            r13[r8] = r12
            int r12 = r0.delete(r3, r9, r13)
            if (r12 <= 0) goto L_0x01ab
        L_0x0094:
            android.content.Context r13 = r11.getContext()
            android.content.ContentResolver r13 = r13.getContentResolver()
            android.net.Uri r14 = miui.provider.ExtraTelephony.AntiSpamMode.CONTENT_URI
            goto L_0x004f
        L_0x009f:
            int r12 = r0.delete(r3, r13, r14)
            if (r12 <= 0) goto L_0x01ab
            goto L_0x0094
        L_0x00a6:
            boolean r13 = b.b.a.a.f1308a
            if (r13 == 0) goto L_0x00af
            java.lang.String r13 = "delete URI_KEYWORD_ID "
            android.util.Log.i(r4, r13)
        L_0x00af:
            b.b.a.c r13 = r11.f2337c
            java.lang.String[] r14 = new java.lang.String[r6]
            java.lang.String r1 = r12.getLastPathSegment()
            r14[r8] = r1
            r13.a((android.database.sqlite.SQLiteDatabase) r0, (java.lang.String) r10, (java.lang.String) r9, (java.lang.String[]) r14)
            java.lang.String[] r13 = new java.lang.String[r6]
            java.lang.String r12 = r12.getLastPathSegment()
            r13[r8] = r12
            int r12 = r0.delete(r10, r9, r13)
            if (r12 <= 0) goto L_0x01ab
        L_0x00ca:
            android.content.Context r13 = r11.getContext()
            android.content.ContentResolver r13 = r13.getContentResolver()
            android.net.Uri r14 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI
            goto L_0x004f
        L_0x00d6:
            boolean r12 = b.b.a.a.f1308a
            if (r12 == 0) goto L_0x00df
            java.lang.String r12 = "delete URI_KEYWORD "
            android.util.Log.i(r4, r12)
        L_0x00df:
            boolean r12 = r11.a()
            if (r12 == 0) goto L_0x00f5
            int r12 = r14.length
            if (r12 != r2) goto L_0x00f5
            java.lang.String[] r12 = new java.lang.String[r5]
            r13 = r14[r8]
            r12[r8] = r13
            r13 = r14[r5]
            r12[r6] = r13
            java.lang.String r13 = "data = ? AND type = ?"
            goto L_0x00f6
        L_0x00f5:
            r12 = r14
        L_0x00f6:
            b.b.a.c r14 = r11.f2337c
            r14.a((android.database.sqlite.SQLiteDatabase) r0, (java.lang.String) r10, (java.lang.String) r13, (java.lang.String[]) r12)
            int r12 = r0.delete(r10, r13, r12)
            if (r12 <= 0) goto L_0x01ab
            goto L_0x00ca
        L_0x0102:
            java.lang.String[] r13 = new java.lang.String[r6]
            java.lang.String r12 = r12.getLastPathSegment()
            r13[r8] = r12
            int r12 = r0.delete(r3, r9, r13)
            if (r12 <= 0) goto L_0x01ab
            android.content.Context r13 = r11.getContext()
            android.content.ContentResolver r13 = r13.getContentResolver()
            android.net.Uri r14 = b.b.a.e.c.a.f1417a
            goto L_0x004f
        L_0x011c:
            int r12 = r0.delete(r3, r13, r14)
            if (r12 <= 0) goto L_0x01ab
            android.content.Context r13 = r11.getContext()
            android.content.ContentResolver r13 = r13.getContentResolver()
            android.net.Uri r14 = b.b.a.e.c.a.f1417a
            r13.notifyChange(r14, r7, r8)
            android.content.Context r13 = r11.getContext()
            android.content.ContentResolver r13 = r13.getContentResolver()
            android.net.Uri r14 = b.b.a.e.c.a.f1418b
            goto L_0x004f
        L_0x013b:
            boolean r13 = b.b.a.a.f1308a
            if (r13 == 0) goto L_0x0144
            java.lang.String r13 = "delete URI_PHONELIST_ID "
            android.util.Log.i(r4, r13)
        L_0x0144:
            b.b.a.c r13 = r11.f2337c
            java.lang.String[] r14 = new java.lang.String[r6]
            java.lang.String r1 = r12.getLastPathSegment()
            r14[r8] = r1
            r13.c(r0, r3, r9, r14)
            java.lang.String[] r13 = new java.lang.String[r6]
            java.lang.String r12 = r12.getLastPathSegment()
            r13[r8] = r12
            int r12 = r0.delete(r3, r9, r13)
            if (r12 <= 0) goto L_0x01ab
            android.content.Context r13 = r11.getContext()
            android.accounts.Account r13 = miui.accounts.ExtraAccountManager.getXiaomiAccount(r13)
            if (r13 == 0) goto L_0x0173
            android.os.Bundle r14 = new android.os.Bundle
            r14.<init>()
            java.lang.String r0 = "antispam"
            android.content.ContentResolver.requestSync(r13, r0, r14)
        L_0x0173:
            android.content.Context r13 = r11.getContext()
            android.content.ContentResolver r13 = r13.getContentResolver()
            android.net.Uri r14 = b.b.a.e.c.b.f1420a
            goto L_0x004f
        L_0x017f:
            boolean r12 = b.b.a.a.f1308a
            if (r12 == 0) goto L_0x0188
            java.lang.String r12 = "delete URI_PHONELIST "
            android.util.Log.i(r4, r12)
        L_0x0188:
            boolean r12 = r11.a()
            if (r12 == 0) goto L_0x019e
            int r12 = r14.length
            if (r12 != r2) goto L_0x019e
            java.lang.String[] r12 = new java.lang.String[r5]
            r13 = r14[r8]
            r12[r8] = r13
            r13 = r14[r5]
            r12[r6] = r13
            java.lang.String r13 = "number = ? AND type = ?"
            goto L_0x019f
        L_0x019e:
            r12 = r14
        L_0x019f:
            b.b.a.c r14 = r11.f2337c
            r14.c(r0, r3, r13, r12)
            int r12 = r0.delete(r3, r13, r12)
            if (r12 <= 0) goto L_0x01ab
            goto L_0x0173
        L_0x01ab:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.db.AntiSpamProvider.delete(android.net.Uri, java.lang.String, java.lang.String[]):int");
    }

    public String getType(Uri uri) {
        int match = f2335a.match(uri);
        if (match == 1) {
            return "vnd.android.cursor.dir/antispam-phone_list";
        }
        if (match == 2) {
            return "vnd.android.cursor.item/antispam-phone_list";
        }
        if (match == 3) {
            return "vnd.android.cursor.dir/antispam-log";
        }
        if (match == 4) {
            return "vnd.android.cursor.item/antispam-log";
        }
        if (match == 7) {
            return "vnd.android.cursor.dir/antispam-keyword";
        }
        if (match == 8) {
            return "vnd.android.cursor.item/antispam-keyword";
        }
        if (match == 15) {
            return "vnd.android.cursor.dir/antispam-mode";
        }
        if (match == 16) {
            return "vnd.android.cursor.item/antispam-mode";
        }
        switch (match) {
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
                return "*/*";
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        ContentResolver contentResolver;
        Uri uri2;
        Uri uri3;
        SQLiteDatabase writableDatabase = this.f2336b.getWritableDatabase();
        int match = f2335a.match(uri);
        Uri uri4 = null;
        if (match != 1) {
            if (match == 3) {
                contentValues.put("number", ExtraTelephony.normalizeNumber(contentValues.getAsString("number")));
                long insert = writableDatabase.insert("fwlog", (String) null, contentValues);
                if (insert > 0) {
                    uri2 = ContentUris.withAppendedId(c.a.f1417a, insert);
                    getContext().getContentResolver().notifyChange(c.a.f1417a, (ContentObserver) null, false);
                    contentResolver = getContext().getContentResolver();
                    uri3 = c.a.f1418b;
                }
            } else if (match == 7) {
                String asString = contentValues.getAsString(DataSchemeDataSource.SCHEME_DATA);
                Integer asInteger = contentValues.getAsInteger("type");
                Integer asInteger2 = contentValues.getAsInteger(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID);
                if (a()) {
                    SQLiteDatabase sQLiteDatabase = writableDatabase;
                    ContentValues contentValues2 = contentValues;
                    String str = asString;
                    Integer num = asInteger;
                    a(sQLiteDatabase, contentValues2, str, num, 1);
                    a(sQLiteDatabase, contentValues2, str, num, 2);
                } else {
                    uri4 = a(writableDatabase, contentValues, asString, asInteger, asInteger2);
                }
            } else if (match == 15) {
                long insert2 = writableDatabase.insert("mode", (String) null, contentValues);
                if (insert2 > 0) {
                    uri2 = ContentUris.withAppendedId(ExtraTelephony.AntiSpamMode.CONTENT_URI, insert2);
                    contentResolver = getContext().getContentResolver();
                    uri3 = ExtraTelephony.AntiSpamMode.CONTENT_URI;
                }
            } else if (match == 17) {
                long insert3 = writableDatabase.insert("sim", (String) null, contentValues);
                if (insert3 > 0) {
                    uri2 = ContentUris.withAppendedId(ExtraTelephony.AntiSpamSim.CONTENT_URI, insert3);
                    contentResolver = getContext().getContentResolver();
                    uri3 = ExtraTelephony.AntiSpamSim.CONTENT_URI;
                }
            } else if (match == 19) {
                long insert4 = writableDatabase.insert("reportSms", (String) null, contentValues);
                if (insert4 > 0) {
                    uri2 = ContentUris.withAppendedId(a.C0020a.f1309a, insert4);
                    contentResolver = getContext().getContentResolver();
                    uri3 = a.C0020a.f1309a;
                }
            } else if (match == 20) {
                long insert5 = writableDatabase.insert("reportSmsPending", (String) null, contentValues);
                if (insert5 > 0) {
                    uri2 = ContentUris.withAppendedId(a.b.f1310a, insert5);
                    contentResolver = getContext().getContentResolver();
                    uri3 = a.b.f1310a;
                }
            } else {
                throw new UnsupportedOperationException("Cannot insert that URL: " + uri);
            }
            contentResolver.notifyChange(uri3, (ContentObserver) null, false);
            return uri2;
        }
        String f = n.f(contentValues.getAsString("number"));
        if (TextUtils.isEmpty(f)) {
            return null;
        }
        contentValues.put("number", f);
        contentValues.put("display_number", f);
        Integer asInteger3 = contentValues.getAsInteger(AdvancedSlider.STATE);
        Integer asInteger4 = contentValues.getAsInteger("type");
        Integer asInteger5 = contentValues.getAsInteger(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID);
        if (a()) {
            SQLiteDatabase sQLiteDatabase2 = writableDatabase;
            ContentValues contentValues3 = contentValues;
            String str2 = f;
            a(sQLiteDatabase2, contentValues3, str2, asInteger3.intValue(), asInteger4.intValue(), (Integer) 1);
            a(sQLiteDatabase2, contentValues3, str2, asInteger3.intValue(), asInteger4.intValue(), (Integer) 2);
        } else {
            uri4 = a(writableDatabase, contentValues, f, asInteger3.intValue(), asInteger4.intValue(), asInteger5);
        }
        return uri4;
    }

    public boolean onCreate() {
        e.b(getContext());
        this.f2336b = e.a(getContext());
        this.f2337c = b.b.a.c.a(getContext());
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0084, code lost:
        r8.setTables(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x009d, code lost:
        r6.append("_id='");
        r6.append(r18.getLastPathSegment());
        r6.append("'");
        r6 = r6.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00c7, code lost:
        r6 = a(r18.getLastPathSegment());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00cf, code lost:
        r8.appendWhere(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00d6, code lost:
        r13 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00d7, code lost:
        if (r1 != 5) goto L_0x00f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00d9, code lost:
        r0 = a(r9, r8, r19, r20, r21, r22);
        r1 = getContext().getContentResolver();
        r2 = b.b.a.e.c.C0022c.f1424b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00f3, code lost:
        r0.setNotificationUri(r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00fd, code lost:
        if (r1 != 11) goto L_0x012d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ff, code lost:
        r12 = new java.lang.String[]{java.lang.String.valueOf(3), com.miui.activityutil.o.f2310b, "2", com.miui.activityutil.o.f2312d};
        r13 = null;
        r14 = null;
        r15 = null;
        r11 = "sync_dirty <> ? AND type IN (?,?,?)";
        r10 = new java.lang.String[]{"count(*)"};
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x011e, code lost:
        r0 = r8.query(r9, r10, r11, r12, r13, r14, r15);
        r1 = getContext().getContentResolver();
        r2 = b.b.a.e.c.C0022c.f1423a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x012f, code lost:
        if (r1 != 12) goto L_0x0144;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0131, code lost:
        r10 = new java.lang.String[]{"count(*)"};
        r12 = new java.lang.String[]{java.lang.String.valueOf(3)};
        r13 = null;
        r14 = null;
        r15 = null;
        r11 = "sync_dirty = ?";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0148, code lost:
        if (a() == false) goto L_0x015f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x014a, code lost:
        if (r1 != 1) goto L_0x015f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x014e, code lost:
        if (r5.length != 3) goto L_0x015f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0150, code lost:
        r12 = new java.lang.String[]{r5[0], r5[2]};
        r11 = "number = ? AND type = ?";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x015f, code lost:
        r11 = r20;
        r12 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0162, code lost:
        r1 = r8.query(r9, r19, r11, r12, r13, (java.lang.String) null, r22);
        r1.setNotificationUri(getContext().getContentResolver(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:?, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.database.Cursor query(android.net.Uri r18, java.lang.String[] r19, java.lang.String r20, java.lang.String[] r21, java.lang.String r22) {
        /*
            r17 = this;
            r7 = r17
            r0 = r18
            r5 = r21
            com.miui.antispam.db.c r1 = r7.f2336b
            android.database.sqlite.SQLiteDatabase r9 = r1.getReadableDatabase()
            android.database.sqlite.SQLiteQueryBuilder r8 = new android.database.sqlite.SQLiteQueryBuilder
            r8.<init>()
            android.content.UriMatcher r1 = f2335a
            int r1 = r1.match(r0)
            r2 = 5
            r3 = 4
            r4 = 3
            if (r1 == r4) goto L_0x0023
            if (r1 == r3) goto L_0x0023
            r6 = 6
            if (r1 == r6) goto L_0x0023
            if (r1 != r2) goto L_0x002e
        L_0x0023:
            android.content.Context r6 = r17.getContext()
            android.database.Cursor r6 = android.provider.SystemSettings.Secure.checkPrivacyAndReturnCursor(r6)
            if (r6 == 0) goto L_0x002e
            return r6
        L_0x002e:
            java.lang.String r6 = "sim"
            java.lang.String r10 = "mode"
            java.lang.String r11 = "'"
            java.lang.String r12 = "_id='"
            java.lang.String r13 = "keyword"
            java.lang.String r15 = "fwlog"
            java.lang.String r14 = "phone_list"
            r4 = 1
            r16 = 0
            switch(r1) {
                case 1: goto L_0x00d3;
                case 2: goto L_0x00c4;
                case 3: goto L_0x00c0;
                case 4: goto L_0x00bc;
                case 5: goto L_0x00c0;
                case 6: goto L_0x00b3;
                case 7: goto L_0x00af;
                case 8: goto L_0x0095;
                case 9: goto L_0x0042;
                case 10: goto L_0x0042;
                case 11: goto L_0x00d3;
                case 12: goto L_0x00d3;
                case 13: goto L_0x0042;
                case 14: goto L_0x0042;
                case 15: goto L_0x0091;
                case 16: goto L_0x0088;
                case 17: goto L_0x0084;
                case 18: goto L_0x0084;
                case 19: goto L_0x0082;
                case 20: goto L_0x007f;
                case 21: goto L_0x0042;
                case 22: goto L_0x005a;
                default: goto L_0x0042;
            }
        L_0x0042:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unknown query URI: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            java.lang.String r1 = "AntiSpamProvider"
            android.util.Log.e(r1, r0)
            r0 = 0
            return r0
        L_0x005a:
            r0 = r5[r16]
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            int r0 = r0.intValue()
            java.lang.String r1 = "allow_repeat"
            java.lang.String[] r1 = new java.lang.String[]{r1}
            android.database.MatrixCursor r2 = new android.database.MatrixCursor
            r2.<init>(r1)
            java.lang.Integer[] r1 = new java.lang.Integer[r4]
            boolean r0 = com.miui.antispam.db.d.c((int) r0)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r1[r16] = r0
            r2.addRow(r1)
            return r2
        L_0x007f:
            java.lang.String r6 = "reportSmsPending"
            goto L_0x0084
        L_0x0082:
            java.lang.String r6 = "reportSms"
        L_0x0084:
            r8.setTables(r6)
            goto L_0x00d6
        L_0x0088:
            r8.setTables(r10)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            goto L_0x009d
        L_0x0091:
            r8.setTables(r10)
            goto L_0x00d6
        L_0x0095:
            r8.setTables(r13)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
        L_0x009d:
            r6.append(r12)
            java.lang.String r10 = r18.getLastPathSegment()
            r6.append(r10)
            r6.append(r11)
            java.lang.String r6 = r6.toString()
            goto L_0x00cf
        L_0x00af:
            r8.setTables(r13)
            goto L_0x00d6
        L_0x00b3:
            java.lang.String r6 = "(select * from fwlog order by date ASC)"
            r8.setTables(r6)
            java.lang.String r6 = "number"
            r13 = r6
            goto L_0x00d7
        L_0x00bc:
            r8.setTables(r15)
            goto L_0x00c7
        L_0x00c0:
            r8.setTables(r15)
            goto L_0x00d6
        L_0x00c4:
            r8.setTables(r14)
        L_0x00c7:
            java.lang.String r6 = r18.getLastPathSegment()
            java.lang.String r6 = r7.a(r6)
        L_0x00cf:
            r8.appendWhere(r6)
            goto L_0x00d6
        L_0x00d3:
            r8.setTables(r14)
        L_0x00d6:
            r13 = 0
        L_0x00d7:
            if (r1 != r2) goto L_0x00f8
            r0 = r17
            r1 = r9
            r2 = r8
            r3 = r19
            r4 = r20
            r5 = r21
            r6 = r22
            android.database.Cursor r0 = r0.a((android.database.sqlite.SQLiteDatabase) r1, (android.database.sqlite.SQLiteQueryBuilder) r2, (java.lang.String[]) r3, (java.lang.String) r4, (java.lang.String[]) r5, (java.lang.String) r6)
            android.content.Context r1 = r17.getContext()
            android.content.ContentResolver r1 = r1.getContentResolver()
            android.net.Uri r2 = b.b.a.e.c.C0022c.f1424b
        L_0x00f3:
            r0.setNotificationUri(r1, r2)
            goto L_0x0177
        L_0x00f8:
            r2 = 11
            java.lang.String r6 = "count(*)"
            r10 = 2
            if (r1 != r2) goto L_0x012d
            java.lang.String[] r0 = new java.lang.String[]{r6}
            java.lang.String[] r12 = new java.lang.String[r3]
            r1 = 3
            java.lang.String r2 = java.lang.String.valueOf(r1)
            r12[r16] = r2
            java.lang.String r2 = "1"
            r12[r4] = r2
            java.lang.String r2 = "2"
            r12[r10] = r2
            java.lang.String r2 = "3"
            r12[r1] = r2
            r13 = 0
            r14 = 0
            r15 = 0
            java.lang.String r11 = "sync_dirty <> ? AND type IN (?,?,?)"
            r10 = r0
        L_0x011e:
            android.database.Cursor r0 = r8.query(r9, r10, r11, r12, r13, r14, r15)
            android.content.Context r1 = r17.getContext()
            android.content.ContentResolver r1 = r1.getContentResolver()
            android.net.Uri r2 = b.b.a.e.c.C0022c.f1423a
            goto L_0x00f3
        L_0x012d:
            r2 = 12
            if (r1 != r2) goto L_0x0144
            java.lang.String[] r10 = new java.lang.String[]{r6}
            java.lang.String[] r12 = new java.lang.String[r4]
            r0 = 3
            java.lang.String r0 = java.lang.String.valueOf(r0)
            r12[r16] = r0
            r13 = 0
            r14 = 0
            r15 = 0
            java.lang.String r11 = "sync_dirty = ?"
            goto L_0x011e
        L_0x0144:
            boolean r2 = r17.a()
            if (r2 == 0) goto L_0x015f
            if (r1 != r4) goto L_0x015f
            int r1 = r5.length
            r2 = 3
            if (r1 != r2) goto L_0x015f
            java.lang.String[] r1 = new java.lang.String[r10]
            r2 = r5[r16]
            r1[r16] = r2
            r2 = r5[r10]
            r1[r4] = r2
            java.lang.String r2 = "number = ? AND type = ?"
            r12 = r1
            r11 = r2
            goto L_0x0162
        L_0x015f:
            r11 = r20
            r12 = r5
        L_0x0162:
            r14 = 0
            r10 = r19
            r15 = r22
            android.database.Cursor r1 = r8.query(r9, r10, r11, r12, r13, r14, r15)
            android.content.Context r2 = r17.getContext()
            android.content.ContentResolver r2 = r2.getContentResolver()
            r1.setNotificationUri(r2, r0)
            r0 = r1
        L_0x0177:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.db.AntiSpamProvider.query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int update(android.net.Uri r27, android.content.ContentValues r28, java.lang.String r29, java.lang.String[] r30) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r30
            com.miui.antispam.db.c r5 = r0.f2336b
            android.database.sqlite.SQLiteDatabase r5 = r5.getWritableDatabase()
            android.content.UriMatcher r6 = f2335a
            int r6 = r6.match(r1)
            java.lang.String r7 = "_id = ? AND sync_dirty <> ? "
            java.lang.String r8 = "AntiSpamProvider"
            java.lang.String r9 = "phone_list"
            r10 = 2
            r12 = 1
            r13 = 0
            if (r6 == r12) goto L_0x02ca
            java.lang.String r14 = " with a where clause"
            java.lang.String r15 = "Cannot update URL "
            java.lang.String r11 = "_id = ? "
            if (r6 == r10) goto L_0x024b
            java.lang.String r7 = "fwlog"
            r9 = 3
            if (r6 == r9) goto L_0x0244
            r9 = 4
            if (r6 == r9) goto L_0x0200
            r7 = 13
            if (r6 == r7) goto L_0x01ed
            r7 = 14
            if (r6 == r7) goto L_0x01dd
            r7 = 16
            if (r6 == r7) goto L_0x01a6
            r7 = 17
            if (r6 == r7) goto L_0x018e
            r7 = 21
            if (r6 == r7) goto L_0x0175
            java.lang.String r7 = "keyword"
            switch(r6) {
                case 7: goto L_0x013d;
                case 8: goto L_0x00e4;
                case 9: goto L_0x00a3;
                case 10: goto L_0x0061;
                default: goto L_0x004a;
            }
        L_0x004a:
            java.lang.UnsupportedOperationException r2 = new java.lang.UnsupportedOperationException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Cannot update that URL: "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            r2.<init>(r1)
            throw r2
        L_0x0061:
            if (r4 == 0) goto L_0x01fd
            int r1 = r4.length
            r2 = 5
            if (r1 != r2) goto L_0x01fd
            r19 = r4[r13]
            r1 = r4[r12]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            int r1 = r1.intValue()
            int r1 = r1 + r12
            int r20 = b.b.a.e.n.a((int) r1)
            r1 = r4[r10]
            java.lang.String r2 = "is_forward_call"
            boolean r23 = r2.equals(r1)
            r1 = 3
            r1 = r4[r1]
            java.lang.String r2 = "is_repeated_normal_call"
            boolean r24 = r2.equals(r1)
            r1 = r4[r9]
            java.lang.String r2 = "is_repeated_blocked_call"
            boolean r25 = r2.equals(r1)
            com.miui.antispam.policy.a.e r1 = new com.miui.antispam.policy.a.e
            r21 = 2
            r22 = 0
            r18 = r1
            r18.<init>(r19, r20, r21, r22, r23, r24, r25)
            b.b.a.c r2 = r0.f2337c
            int r1 = r2.a((com.miui.antispam.policy.a.e) r1)
            return r1
        L_0x00a3:
            boolean r1 = b.b.c.j.i.f()
            if (r1 != 0) goto L_0x01fd
            android.content.Context r1 = r26.getContext()
            boolean r1 = b.b.a.e.n.c((android.content.Context) r1)
            if (r1 == 0) goto L_0x01fd
            if (r4 == 0) goto L_0x01fd
            int r1 = r4.length
            r2 = 3
            if (r1 != r2) goto L_0x01fd
            r15 = r4[r13]
            r18 = r4[r12]
            r1 = r4[r10]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            int r1 = r1.intValue()
            android.content.Context r2 = r26.getContext()
            int r16 = b.b.a.e.n.b((android.content.Context) r2, (int) r1)
            com.miui.antispam.policy.a.e r1 = new com.miui.antispam.policy.a.e
            r17 = 1
            r19 = 0
            r20 = 0
            r21 = 0
            r14 = r1
            r14.<init>(r15, r16, r17, r18, r19, r20, r21)
            b.b.a.c r2 = r0.f2337c
            int r1 = r2.b(r1)
            return r1
        L_0x00e4:
            boolean r6 = b.b.a.a.f1308a
            if (r6 == 0) goto L_0x00ed
            java.lang.String r6 = "update URI_KEYWORD_ID "
            android.util.Log.i(r8, r6)
        L_0x00ed:
            if (r3 != 0) goto L_0x0125
            if (r4 != 0) goto L_0x0125
            b.b.a.c r3 = r0.f2337c
            java.lang.String[] r4 = new java.lang.String[r12]
            java.lang.String r6 = r27.getLastPathSegment()
            r4[r13] = r6
            r3.a((android.database.sqlite.SQLiteDatabase) r5, (java.lang.String) r7, (java.lang.String) r11, (java.lang.String[]) r4)
            java.lang.String[] r3 = new java.lang.String[r12]
            java.lang.String r4 = r27.getLastPathSegment()
            r3[r13] = r4
            int r2 = r5.update(r7, r2, r11, r3)
            if (r2 <= 0) goto L_0x0300
            b.b.a.c r3 = r0.f2337c
            java.lang.String[] r4 = new java.lang.String[r12]
            java.lang.String r1 = r27.getLastPathSegment()
            r4[r13] = r1
            r3.b(r5, r7, r11, r4)
            android.content.Context r1 = r26.getContext()
            android.content.ContentResolver r1 = r1.getContentResolver()
            android.net.Uri r3 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI
            goto L_0x02fc
        L_0x0125:
            java.lang.UnsupportedOperationException r2 = new java.lang.UnsupportedOperationException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            r3.append(r1)
            r3.append(r14)
            java.lang.String r1 = r3.toString()
            r2.<init>(r1)
            throw r2
        L_0x013d:
            boolean r1 = b.b.a.a.f1308a
            if (r1 == 0) goto L_0x0146
            java.lang.String r1 = "update URI_KEYWORD "
            android.util.Log.i(r8, r1)
        L_0x0146:
            b.b.a.c r1 = r0.f2337c
            int r1 = r1.a((android.database.sqlite.SQLiteDatabase) r5, (java.lang.String) r7, (java.lang.String) r3, (java.lang.String[]) r4)
            int r2 = r5.update(r7, r2, r3, r4)
            if (r2 <= 0) goto L_0x0300
            b.b.a.c r3 = r0.f2337c
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "_id = "
            r4.append(r6)
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            r4 = 0
            r3.b(r5, r7, r1, r4)
            android.content.Context r1 = r26.getContext()
            android.content.ContentResolver r1 = r1.getContentResolver()
            android.net.Uri r3 = miui.provider.ExtraTelephony.Keyword.CONTENT_URI
            goto L_0x02fd
        L_0x0175:
            if (r4 == 0) goto L_0x01fd
            int r1 = r4.length
            if (r1 != r12) goto L_0x01fd
            r1 = r4[r13]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            int r1 = r1.intValue()
            int r1 = r1 + r12
            int r1 = b.b.a.e.n.a((int) r1)
            boolean r1 = com.miui.antispam.db.d.b((int) r1)
            return r1
        L_0x018e:
            java.lang.String r1 = "sim"
            int r1 = r5.update(r1, r2, r3, r4)
            if (r1 <= 0) goto L_0x0301
            android.content.Context r2 = r26.getContext()
            android.content.ContentResolver r2 = r2.getContentResolver()
            android.net.Uri r3 = miui.provider.ExtraTelephony.AntiSpamSim.CONTENT_URI
        L_0x01a0:
            r4 = 0
        L_0x01a1:
            r2.notifyChange(r3, r4, r13)
            goto L_0x0301
        L_0x01a6:
            if (r3 != 0) goto L_0x01c5
            if (r4 != 0) goto L_0x01c5
            java.lang.String[] r3 = new java.lang.String[r12]
            java.lang.String r1 = r27.getLastPathSegment()
            r3[r13] = r1
            java.lang.String r1 = "mode"
            int r1 = r5.update(r1, r2, r11, r3)
            if (r1 <= 0) goto L_0x0301
            android.content.Context r2 = r26.getContext()
            android.content.ContentResolver r2 = r2.getContentResolver()
            android.net.Uri r3 = miui.provider.ExtraTelephony.AntiSpamMode.CONTENT_URI
            goto L_0x01a0
        L_0x01c5:
            java.lang.UnsupportedOperationException r2 = new java.lang.UnsupportedOperationException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            r3.append(r1)
            r3.append(r14)
            java.lang.String r1 = r3.toString()
            r2.<init>(r1)
            throw r2
        L_0x01dd:
            if (r4 == 0) goto L_0x01fd
            int r1 = r4.length
            if (r1 != r12) goto L_0x01fd
            r1 = r4[r13]
            android.content.Context r2 = r26.getContext()
            boolean r1 = b.b.a.e.p.a(r2, r1)
            return r1
        L_0x01ed:
            if (r4 == 0) goto L_0x01fd
            int r1 = r4.length
            if (r1 != r10) goto L_0x01fd
            r1 = r4[r13]
            r2 = r4[r12]
            b.b.a.c r3 = r0.f2337c
            int r1 = r3.a(r1, r2)
            return r1
        L_0x01fd:
            r1 = r13
            goto L_0x0301
        L_0x0200:
            if (r3 != 0) goto L_0x022c
            if (r4 != 0) goto L_0x022c
            java.lang.String[] r3 = new java.lang.String[r12]
            java.lang.String r1 = r27.getLastPathSegment()
            r3[r13] = r1
            int r1 = r5.update(r7, r2, r11, r3)
            if (r1 <= 0) goto L_0x0301
        L_0x0212:
            android.content.Context r2 = r26.getContext()
            android.content.ContentResolver r2 = r2.getContentResolver()
            android.net.Uri r3 = b.b.a.e.c.a.f1417a
            r4 = 0
            r2.notifyChange(r3, r4, r13)
            android.content.Context r2 = r26.getContext()
            android.content.ContentResolver r2 = r2.getContentResolver()
            android.net.Uri r3 = b.b.a.e.c.a.f1418b
            goto L_0x01a1
        L_0x022c:
            java.lang.UnsupportedOperationException r2 = new java.lang.UnsupportedOperationException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            r3.append(r1)
            r3.append(r14)
            java.lang.String r1 = r3.toString()
            r2.<init>(r1)
            throw r2
        L_0x0244:
            int r1 = r5.update(r7, r2, r3, r4)
            if (r1 <= 0) goto L_0x0301
            goto L_0x0212
        L_0x024b:
            boolean r6 = b.b.a.a.f1308a
            if (r6 == 0) goto L_0x0254
            java.lang.String r6 = "update URI_PHONELIST_ID "
            android.util.Log.i(r8, r6)
        L_0x0254:
            if (r3 != 0) goto L_0x02b2
            if (r4 != 0) goto L_0x02b2
            b.b.a.c r3 = r0.f2337c
            java.lang.String[] r4 = new java.lang.String[r12]
            java.lang.String r6 = r27.getLastPathSegment()
            r4[r13] = r6
            r3.c(r5, r9, r11, r4)
            java.lang.String[] r3 = new java.lang.String[r12]
            java.lang.String r4 = r27.getLastPathSegment()
            r3[r13] = r4
            int r3 = r5.update(r9, r2, r11, r3)
            if (r3 <= 0) goto L_0x02b0
            b.b.a.c r4 = r0.f2337c
            java.lang.String[] r6 = new java.lang.String[r10]
            java.lang.String r1 = r27.getLastPathSegment()
            r6[r13] = r1
            java.lang.String r1 = java.lang.String.valueOf(r12)
            r6[r12] = r1
            r4.d(r5, r9, r7, r6)
            android.content.Context r1 = r26.getContext()
            android.accounts.Account r1 = miui.accounts.ExtraAccountManager.getXiaomiAccount(r1)
            if (r1 == 0) goto L_0x02a2
            java.lang.String r4 = "e_tag"
            boolean r2 = r2.containsKey(r4)
            if (r2 != 0) goto L_0x02a2
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            java.lang.String r4 = "antispam"
            android.content.ContentResolver.requestSync(r1, r4, r2)
        L_0x02a2:
            android.content.Context r1 = r26.getContext()
            android.content.ContentResolver r1 = r1.getContentResolver()
            android.net.Uri r2 = b.b.a.e.c.b.f1420a
            r4 = 0
            r1.notifyChange(r2, r4, r13)
        L_0x02b0:
            r1 = r3
            goto L_0x0301
        L_0x02b2:
            java.lang.UnsupportedOperationException r2 = new java.lang.UnsupportedOperationException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            r3.append(r1)
            r3.append(r14)
            java.lang.String r1 = r3.toString()
            r2.<init>(r1)
            throw r2
        L_0x02ca:
            boolean r1 = b.b.a.a.f1308a
            if (r1 == 0) goto L_0x02d3
            java.lang.String r1 = "update URI_PHONELIST "
            android.util.Log.i(r8, r1)
        L_0x02d3:
            b.b.a.c r1 = r0.f2337c
            int r1 = r1.c(r5, r9, r3, r4)
            int r2 = r5.update(r9, r2, r3, r4)
            if (r2 <= 0) goto L_0x0300
            b.b.a.c r3 = r0.f2337c
            java.lang.String[] r4 = new java.lang.String[r10]
            java.lang.String r1 = java.lang.String.valueOf(r1)
            r4[r13] = r1
            java.lang.String r1 = java.lang.String.valueOf(r12)
            r4[r12] = r1
            r3.d(r5, r9, r7, r4)
            android.content.Context r1 = r26.getContext()
            android.content.ContentResolver r1 = r1.getContentResolver()
            android.net.Uri r3 = b.b.a.e.c.b.f1420a
        L_0x02fc:
            r4 = 0
        L_0x02fd:
            r1.notifyChange(r3, r4, r13)
        L_0x0300:
            r1 = r2
        L_0x0301:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.db.AntiSpamProvider.update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[]):int");
    }
}
