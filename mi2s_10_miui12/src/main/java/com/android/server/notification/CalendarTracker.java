package com.android.server.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.ArraySet;
import android.util.Log;
import java.io.PrintWriter;

public class CalendarTracker {
    private static final String[] ATTENDEE_PROJECTION = {"event_id", "attendeeEmail", "attendeeStatus"};
    private static final String ATTENDEE_SELECTION = "event_id = ? AND attendeeEmail = ?";
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("ConditionProviders", 3);
    private static final boolean DEBUG_ATTENDEES = false;
    private static final int EVENT_CHECK_LOOKAHEAD = 86400000;
    private static final String INSTANCE_ORDER_BY = "begin ASC";
    private static final String[] INSTANCE_PROJECTION = {"begin", "end", "title", "visible", "event_id", "calendar_displayName", "ownerAccount", "calendar_id", "availability"};
    private static final String TAG = "ConditionProviders.CT";
    /* access modifiers changed from: private */
    public Callback mCallback;
    private final ContentObserver mObserver = new ContentObserver((Handler) null) {
        public void onChange(boolean selfChange, Uri u) {
            if (CalendarTracker.DEBUG) {
                Log.d(CalendarTracker.TAG, "onChange selfChange=" + selfChange + " uri=" + u + " u=" + CalendarTracker.this.mUserContext.getUserId());
            }
            CalendarTracker.this.mCallback.onChanged();
        }

        public void onChange(boolean selfChange) {
            if (CalendarTracker.DEBUG) {
                Log.d(CalendarTracker.TAG, "onChange selfChange=" + selfChange);
            }
        }
    };
    private boolean mRegistered;
    private final Context mSystemContext;
    /* access modifiers changed from: private */
    public final Context mUserContext;

    public interface Callback {
        void onChanged();
    }

    public static class CheckEventResult {
        public boolean inEvent;
        public long recheckAt;
    }

    public CalendarTracker(Context systemContext, Context userContext) {
        this.mSystemContext = systemContext;
        this.mUserContext = userContext;
    }

    public void setCallback(Callback callback) {
        if (this.mCallback != callback) {
            this.mCallback = callback;
            setRegistered(this.mCallback != null);
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("mCallback=");
        pw.println(this.mCallback);
        pw.print(prefix);
        pw.print("mRegistered=");
        pw.println(this.mRegistered);
        pw.print(prefix);
        pw.print("u=");
        pw.println(this.mUserContext.getUserId());
    }

    private ArraySet<Long> getCalendarsWithAccess() {
        long start = System.currentTimeMillis();
        ArraySet<Long> rt = new ArraySet<>();
        Cursor cursor = null;
        try {
            cursor = this.mUserContext.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, new String[]{"_id"}, "calendar_access_level >= 500 AND sync_events = 1", (String[]) null, (String) null);
            while (cursor != null && cursor.moveToNext()) {
                rt.add(Long.valueOf(cursor.getLong(0)));
            }
            if (DEBUG) {
                Log.d(TAG, "getCalendarsWithAccess took " + (System.currentTimeMillis() - start));
            }
            return rt;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0142, code lost:
        if (java.util.Objects.equals(r2.calName, r9) == false) goto L_0x014b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x01c4, code lost:
        if (r18 != null) goto L_0x01c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x01c6, code lost:
        r18.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01df, code lost:
        if (r18 != null) goto L_0x01c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x01e2, code lost:
        return r11;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x01e6  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0154 A[Catch:{ Exception -> 0x0146 }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x015a A[ADDED_TO_REGION, Catch:{ Exception -> 0x0146 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.server.notification.CalendarTracker.CheckEventResult checkEvent(android.service.notification.ZenModeConfig.EventInfo r35, long r36) {
        /*
            r34 = this;
            r1 = r34
            r2 = r35
            r3 = r36
            java.lang.String r5 = "ConditionProviders.CT"
            android.net.Uri r0 = android.provider.CalendarContract.Instances.CONTENT_URI
            android.net.Uri$Builder r6 = r0.buildUpon()
            android.content.ContentUris.appendId(r6, r3)
            r7 = 86400000(0x5265c00, double:4.2687272E-316)
            long r9 = r3 + r7
            android.content.ContentUris.appendId(r6, r9)
            android.net.Uri r9 = r6.build()
            android.content.Context r0 = r1.mUserContext
            android.content.ContentResolver r11 = r0.getContentResolver()
            java.lang.String[] r13 = INSTANCE_PROJECTION
            r14 = 0
            r15 = 0
            java.lang.String r16 = "begin ASC"
            r12 = r9
            android.database.Cursor r10 = r11.query(r12, r13, r14, r15, r16)
            com.android.server.notification.CalendarTracker$CheckEventResult r0 = new com.android.server.notification.CalendarTracker$CheckEventResult
            r0.<init>()
            r11 = r0
            long r7 = r7 + r3
            r11.recheckAt = r7
            android.util.ArraySet r0 = r34.getCalendarsWithAccess()     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
        L_0x003b:
            if (r10 == 0) goto L_0x01bc
            boolean r7 = r10.moveToNext()     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            if (r7 == 0) goto L_0x01bc
            r7 = 0
            long r12 = r10.getLong(r7)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r8 = 1
            long r14 = r10.getLong(r8)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r7 = 2
            java.lang.String r17 = r10.getString(r7)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r7 = 3
            int r8 = r10.getInt(r7)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r7 = 1
            if (r8 != r7) goto L_0x005c
            r7 = 1
            goto L_0x005d
        L_0x005c:
            r7 = 0
        L_0x005d:
            r8 = 4
            int r21 = r10.getInt(r8)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r22 = r21
            r8 = 5
            java.lang.String r23 = r10.getString(r8)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r24 = r23
            r8 = 6
            java.lang.String r25 = r10.getString(r8)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r26 = r25
            r8 = 7
            long r27 = r10.getLong(r8)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r8 = 8
            int r29 = r10.getInt(r8)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            r30 = r29
            java.lang.Long r8 = java.lang.Long.valueOf(r27)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            boolean r8 = r0.contains(r8)     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            boolean r31 = DEBUG     // Catch:{ Exception -> 0x01d2, all -> 0x01ca }
            if (r31 == 0) goto L_0x0104
            r31 = r0
            java.lang.String r0 = "title=%s time=%s-%s vis=%s availability=%s eventId=%s name=%s owner=%s calId=%s canAccessCal=%s"
            r32 = r6
            r6 = 10
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x00fd, all -> 0x00f6 }
            r16 = 0
            r6[r16] = r17     // Catch:{ Exception -> 0x00fd, all -> 0x00f6 }
            r33 = r9
            java.util.Date r9 = new java.util.Date     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r9.<init>(r12)     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r19 = 1
            r6[r19] = r9     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            java.util.Date r9 = new java.util.Date     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r9.<init>(r14)     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r18 = 2
            r6[r18] = r9     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r7)     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r18 = 3
            r6[r18] = r9     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            java.lang.String r9 = availabilityToString(r30)     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r18 = 4
            r6[r18] = r9     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r22)     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r18 = 5
            r6[r18] = r9     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r9 = r24
            r18 = 6
            r6[r18] = r9     // Catch:{ Exception -> 0x00f1, all -> 0x00ec }
            r18 = r10
            r10 = r26
            r20 = 7
            r6[r20] = r10     // Catch:{ Exception -> 0x0146 }
            java.lang.Long r20 = java.lang.Long.valueOf(r27)     // Catch:{ Exception -> 0x0146 }
            r21 = 8
            r6[r21] = r20     // Catch:{ Exception -> 0x0146 }
            r20 = 9
            java.lang.Boolean r21 = java.lang.Boolean.valueOf(r8)     // Catch:{ Exception -> 0x0146 }
            r6[r20] = r21     // Catch:{ Exception -> 0x0146 }
            java.lang.String r0 = java.lang.String.format(r0, r6)     // Catch:{ Exception -> 0x0146 }
            android.util.Log.d(r5, r0)     // Catch:{ Exception -> 0x0146 }
            goto L_0x0112
        L_0x00ec:
            r0 = move-exception
            r18 = r10
            goto L_0x01e4
        L_0x00f1:
            r0 = move-exception
            r18 = r10
            goto L_0x01d9
        L_0x00f6:
            r0 = move-exception
            r33 = r9
            r18 = r10
            goto L_0x01e4
        L_0x00fd:
            r0 = move-exception
            r33 = r9
            r18 = r10
            goto L_0x01d9
        L_0x0104:
            r31 = r0
            r32 = r6
            r33 = r9
            r18 = r10
            r9 = r24
            r10 = r26
            r16 = 0
        L_0x0112:
            int r0 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r0 < 0) goto L_0x011c
            int r0 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x011c
            r0 = 1
            goto L_0x011e
        L_0x011c:
            r0 = r16
        L_0x011e:
            if (r7 == 0) goto L_0x0149
            if (r8 == 0) goto L_0x0149
            java.lang.String r6 = r2.calName     // Catch:{ Exception -> 0x0146 }
            if (r6 != 0) goto L_0x012e
            java.lang.Long r6 = r2.calendarId     // Catch:{ Exception -> 0x0146 }
            if (r6 == 0) goto L_0x012b
            goto L_0x012e
        L_0x012b:
            r20 = r7
            goto L_0x0144
        L_0x012e:
            java.lang.Long r6 = r2.calendarId     // Catch:{ Exception -> 0x0146 }
            r20 = r7
            java.lang.Long r7 = java.lang.Long.valueOf(r27)     // Catch:{ Exception -> 0x0146 }
            boolean r6 = java.util.Objects.equals(r6, r7)     // Catch:{ Exception -> 0x0146 }
            if (r6 != 0) goto L_0x0144
            java.lang.String r6 = r2.calName     // Catch:{ Exception -> 0x0146 }
            boolean r6 = java.util.Objects.equals(r6, r9)     // Catch:{ Exception -> 0x0146 }
            if (r6 == 0) goto L_0x014b
        L_0x0144:
            r6 = 1
            goto L_0x014d
        L_0x0146:
            r0 = move-exception
            goto L_0x01d9
        L_0x0149:
            r20 = r7
        L_0x014b:
            r6 = r16
        L_0x014d:
            r21 = r8
            r7 = r30
            r8 = 1
            if (r7 == r8) goto L_0x0156
            r16 = 1
        L_0x0156:
            r8 = r16
            if (r6 == 0) goto L_0x01aa
            if (r8 == 0) goto L_0x01aa
            boolean r16 = DEBUG     // Catch:{ Exception -> 0x0146 }
            if (r16 == 0) goto L_0x0168
            r16 = r6
            java.lang.String r6 = "  MEETS CALENDAR & AVAILABILITY"
            android.util.Log.d(r5, r6)     // Catch:{ Exception -> 0x0146 }
            goto L_0x016a
        L_0x0168:
            r16 = r6
        L_0x016a:
            r6 = r22
            boolean r22 = r1.meetsAttendee(r2, r6, r10)     // Catch:{ Exception -> 0x0146 }
            if (r22 == 0) goto L_0x01a7
            boolean r23 = DEBUG     // Catch:{ Exception -> 0x0146 }
            if (r23 == 0) goto L_0x017b
            java.lang.String r1 = "    MEETS ATTENDEE"
            android.util.Log.d(r5, r1)     // Catch:{ Exception -> 0x0146 }
        L_0x017b:
            if (r0 == 0) goto L_0x0189
            boolean r1 = DEBUG     // Catch:{ Exception -> 0x0146 }
            if (r1 == 0) goto L_0x0186
            java.lang.String r1 = "      MEETS TIME"
            android.util.Log.d(r5, r1)     // Catch:{ Exception -> 0x0146 }
        L_0x0186:
            r1 = 1
            r11.inEvent = r1     // Catch:{ Exception -> 0x0146 }
        L_0x0189:
            int r1 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x0198
            r19 = r0
            long r0 = r11.recheckAt     // Catch:{ Exception -> 0x0146 }
            int r0 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x019a
            r11.recheckAt = r12     // Catch:{ Exception -> 0x0146 }
            goto L_0x01b0
        L_0x0198:
            r19 = r0
        L_0x019a:
            int r0 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x01b0
            long r0 = r11.recheckAt     // Catch:{ Exception -> 0x0146 }
            int r0 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x01b0
            r11.recheckAt = r14     // Catch:{ Exception -> 0x0146 }
            goto L_0x01b0
        L_0x01a7:
            r19 = r0
            goto L_0x01b0
        L_0x01aa:
            r19 = r0
            r16 = r6
            r6 = r22
        L_0x01b0:
            r1 = r34
            r10 = r18
            r0 = r31
            r6 = r32
            r9 = r33
            goto L_0x003b
        L_0x01bc:
            r31 = r0
            r32 = r6
            r33 = r9
            r18 = r10
            if (r18 == 0) goto L_0x01e2
        L_0x01c6:
            r18.close()
            goto L_0x01e2
        L_0x01ca:
            r0 = move-exception
            r32 = r6
            r33 = r9
            r18 = r10
            goto L_0x01e4
        L_0x01d2:
            r0 = move-exception
            r32 = r6
            r33 = r9
            r18 = r10
        L_0x01d9:
            java.lang.String r1 = "error reading calendar"
            android.util.Slog.w(r5, r1, r0)     // Catch:{ all -> 0x01e3 }
            if (r18 == 0) goto L_0x01e2
            goto L_0x01c6
        L_0x01e2:
            return r11
        L_0x01e3:
            r0 = move-exception
        L_0x01e4:
            if (r18 == 0) goto L_0x01e9
            r18.close()
        L_0x01e9:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.CalendarTracker.checkEvent(android.service.notification.ZenModeConfig$EventInfo, long):com.android.server.notification.CalendarTracker$CheckEventResult");
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0130  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean meetsAttendee(android.service.notification.ZenModeConfig.EventInfo r22, int r23, java.lang.String r24) {
        /*
            r21 = this;
            r1 = r24
            long r2 = java.lang.System.currentTimeMillis()
            java.lang.String r10 = "event_id = ? AND attendeeEmail = ?"
            r0 = 2
            java.lang.String[] r8 = new java.lang.String[r0]
            java.lang.String r4 = java.lang.Integer.toString(r23)
            r11 = 0
            r8[r11] = r4
            r12 = 1
            r8[r12] = r1
            r13 = r21
            android.content.Context r4 = r13.mUserContext
            android.content.ContentResolver r4 = r4.getContentResolver()
            android.net.Uri r5 = android.provider.CalendarContract.Attendees.CONTENT_URI
            java.lang.String[] r6 = ATTENDEE_PROJECTION
            r9 = 0
            r7 = r10
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)
            java.lang.String r5 = "meetsAttendee took "
            java.lang.String r6 = "ConditionProviders.CT"
            if (r4 == 0) goto L_0x00f4
            int r7 = r4.getCount()     // Catch:{ all -> 0x00ec }
            if (r7 != 0) goto L_0x003c
            r17 = r8
            r20 = r10
            r8 = r23
            goto L_0x00fa
        L_0x003c:
            r7 = 0
        L_0x003d:
            boolean r9 = r4.moveToNext()     // Catch:{ all -> 0x00ec }
            if (r9 == 0) goto L_0x00c5
            long r14 = r4.getLong(r11)     // Catch:{ all -> 0x00ec }
            java.lang.String r9 = r4.getString(r12)     // Catch:{ all -> 0x00ec }
            int r16 = r4.getInt(r0)     // Catch:{ all -> 0x00ec }
            r17 = r16
            r12 = r22
            int r11 = r12.reply     // Catch:{ all -> 0x00ec }
            r0 = r17
            boolean r11 = meetsReply(r11, r0)     // Catch:{ all -> 0x00ec }
            boolean r17 = DEBUG     // Catch:{ all -> 0x00ec }
            if (r17 == 0) goto L_0x009e
            r17 = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
            r8.<init>()     // Catch:{ all -> 0x0097 }
            r20 = r10
            java.lang.String r10 = ""
            r8.append(r10)     // Catch:{ all -> 0x0092 }
            java.lang.String r10 = "status=%s, meetsReply=%s"
            r12 = 2
            java.lang.Object[] r13 = new java.lang.Object[r12]     // Catch:{ all -> 0x0092 }
            java.lang.String r19 = attendeeStatusToString(r0)     // Catch:{ all -> 0x0092 }
            r18 = 0
            r13[r18] = r19     // Catch:{ all -> 0x0092 }
            java.lang.Boolean r19 = java.lang.Boolean.valueOf(r11)     // Catch:{ all -> 0x0092 }
            r16 = 1
            r13[r16] = r19     // Catch:{ all -> 0x0092 }
            java.lang.String r10 = java.lang.String.format(r10, r13)     // Catch:{ all -> 0x0092 }
            r8.append(r10)     // Catch:{ all -> 0x0092 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0092 }
            android.util.Log.d(r6, r8)     // Catch:{ all -> 0x0092 }
            goto L_0x00a5
        L_0x0092:
            r0 = move-exception
            r8 = r23
            goto L_0x0127
        L_0x0097:
            r0 = move-exception
            r20 = r10
            r8 = r23
            goto L_0x0127
        L_0x009e:
            r17 = r8
            r20 = r10
            r12 = 2
            r18 = 0
        L_0x00a5:
            r8 = r23
            long r12 = (long) r8
            int r10 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r10 != 0) goto L_0x00b6
            boolean r10 = java.util.Objects.equals(r9, r1)     // Catch:{ all -> 0x0126 }
            if (r10 == 0) goto L_0x00b6
            if (r11 == 0) goto L_0x00b6
            r10 = 1
            goto L_0x00b8
        L_0x00b6:
            r10 = r18
        L_0x00b8:
            r7 = r7 | r10
            r13 = r21
            r8 = r17
            r11 = r18
            r10 = r20
            r0 = 2
            r12 = 1
            goto L_0x003d
        L_0x00c5:
            r17 = r8
            r20 = r10
            r8 = r23
            r4.close()
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x00eb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r5)
            long r9 = java.lang.System.currentTimeMillis()
            long r9 = r9 - r2
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r6, r0)
        L_0x00eb:
            return r7
        L_0x00ec:
            r0 = move-exception
            r17 = r8
            r20 = r10
            r8 = r23
            goto L_0x0127
        L_0x00f4:
            r17 = r8
            r20 = r10
            r8 = r23
        L_0x00fa:
            boolean r0 = DEBUG     // Catch:{ all -> 0x0126 }
            if (r0 == 0) goto L_0x0103
            java.lang.String r0 = "No attendees found"
            android.util.Log.d(r6, r0)     // Catch:{ all -> 0x0126 }
        L_0x0103:
            if (r4 == 0) goto L_0x0109
            r4.close()
        L_0x0109:
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x0124
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r5)
            long r9 = java.lang.System.currentTimeMillis()
            long r9 = r9 - r2
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r6, r0)
        L_0x0124:
            r0 = 1
            return r0
        L_0x0126:
            r0 = move-exception
        L_0x0127:
            if (r4 == 0) goto L_0x012c
            r4.close()
        L_0x012c:
            boolean r7 = DEBUG
            if (r7 == 0) goto L_0x0147
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r5)
            long r9 = java.lang.System.currentTimeMillis()
            long r9 = r9 - r2
            r7.append(r9)
            java.lang.String r5 = r7.toString()
            android.util.Log.d(r6, r5)
        L_0x0147:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.CalendarTracker.meetsAttendee(android.service.notification.ZenModeConfig$EventInfo, int, java.lang.String):boolean");
    }

    private void setRegistered(boolean registered) {
        if (this.mRegistered != registered) {
            ContentResolver cr = this.mSystemContext.getContentResolver();
            int userId = this.mUserContext.getUserId();
            if (this.mRegistered) {
                if (DEBUG) {
                    Log.d(TAG, "unregister content observer u=" + userId);
                }
                cr.unregisterContentObserver(this.mObserver);
            }
            this.mRegistered = registered;
            if (DEBUG) {
                Log.d(TAG, "mRegistered = " + registered + " u=" + userId);
            }
            if (this.mRegistered) {
                if (DEBUG) {
                    Log.d(TAG, "register content observer u=" + userId);
                }
                cr.registerContentObserver(CalendarContract.Instances.CONTENT_URI, true, this.mObserver, userId);
                cr.registerContentObserver(CalendarContract.Events.CONTENT_URI, true, this.mObserver, userId);
                cr.registerContentObserver(CalendarContract.Calendars.CONTENT_URI, true, this.mObserver, userId);
            }
        }
    }

    private static String attendeeStatusToString(int status) {
        if (status == 0) {
            return "ATTENDEE_STATUS_NONE";
        }
        if (status == 1) {
            return "ATTENDEE_STATUS_ACCEPTED";
        }
        if (status == 2) {
            return "ATTENDEE_STATUS_DECLINED";
        }
        if (status == 3) {
            return "ATTENDEE_STATUS_INVITED";
        }
        if (status == 4) {
            return "ATTENDEE_STATUS_TENTATIVE";
        }
        return "ATTENDEE_STATUS_UNKNOWN_" + status;
    }

    private static String availabilityToString(int availability) {
        if (availability == 0) {
            return "AVAILABILITY_BUSY";
        }
        if (availability == 1) {
            return "AVAILABILITY_FREE";
        }
        if (availability == 2) {
            return "AVAILABILITY_TENTATIVE";
        }
        return "AVAILABILITY_UNKNOWN_" + availability;
    }

    private static boolean meetsReply(int reply, int attendeeStatus) {
        if (reply != 0) {
            if (reply != 1) {
                if (reply == 2 && attendeeStatus == 1) {
                    return true;
                }
                return false;
            } else if (attendeeStatus == 1 || attendeeStatus == 4) {
                return true;
            } else {
                return false;
            }
        } else if (attendeeStatus != 2) {
            return true;
        } else {
            return false;
        }
    }
}
