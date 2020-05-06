package com.miui.earthquakewarning.ui;

import android.os.Bundle;
import b.b.c.c.a;
import miui.os.Build;

public class EarthquakeWarningMainActivity extends a {
    public static final int REQUEST_CODE_CONTACT_PICK = 1000;
    public static final int REQUEST_CODE_GOOGLE_CONTACT_PICK = 1001;
    public static final int REQUEST_CODE_GUIDE = 1002;
    public static final int RESULT_CODE_GUIDE_AGREE = 1003;
    public static final int RESULT_CODE_GUIDE_REJECT = 1004;
    private static final String TAG = "EarthquakeWarningMain";
    private EarthquakeWarningMainFragment mFragment;

    /* JADX WARNING: Code restructure failed: missing block: B:60:0x015c, code lost:
        if (r4 != null) goto L_0x00ce;
     */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0164  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0169  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int r13, int r14, android.content.Intent r15) {
        /*
            r12 = this;
            com.miui.earthquakewarning.ui.EarthquakeWarningMainActivity.super.onActivityResult(r13, r14, r15)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.lang.String r2 = "display_name"
            java.lang.String r3 = "data1"
            r4 = 0
            r5 = 1000(0x3e8, float:1.401E-42)
            if (r13 != r5) goto L_0x00df
            if (r15 != 0) goto L_0x0019
            return
        L_0x0019:
            java.lang.String r13 = "com.android.contacts.extra.PHONE_URIS"
            android.os.Parcelable[] r13 = r15.getParcelableArrayExtra(r13)
            if (r13 == 0) goto L_0x00de
            int r14 = r13.length
            if (r14 != 0) goto L_0x0026
            goto L_0x00de
        L_0x0026:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            int r15 = r13.length
            r5 = 0
            r6 = r5
        L_0x002e:
            if (r6 >= r15) goto L_0x0078
            r7 = r13[r6]
            android.net.Uri r7 = (android.net.Uri) r7
            java.lang.String r8 = r7.getScheme()
            java.lang.String r9 = "content"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x0053
            int r8 = r14.length()
            if (r8 <= 0) goto L_0x004b
            r8 = 44
            r14.append(r8)
        L_0x004b:
            java.lang.String r7 = r7.getLastPathSegment()
            r14.append(r7)
            goto L_0x0075
        L_0x0053:
            java.lang.String r8 = r7.getScheme()
            java.lang.String r9 = "tel"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x0075
            java.lang.String r8 = r7.getSchemeSpecificPart()
            java.lang.String r8 = r8.trim()
            r0.add(r8)
            java.lang.String r7 = r7.getSchemeSpecificPart()
            java.lang.String r7 = r7.trim()
            r1.add(r7)
        L_0x0075:
            int r6 = r6 + 1
            goto L_0x002e
        L_0x0078:
            int r13 = r14.length()
            if (r13 <= 0) goto L_0x00a8
            android.content.ContentResolver r6 = r12.getContentResolver()
            android.net.Uri r7 = android.provider.ContactsContract.Data.CONTENT_URI
            java.lang.String[] r8 = new java.lang.String[]{r3, r2}
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r15 = "_id IN ("
            r13.append(r15)
            java.lang.String r14 = r14.toString()
            r13.append(r14)
            java.lang.String r14 = ")"
            r13.append(r14)
            java.lang.String r9 = r13.toString()
            r10 = 0
            r11 = 0
            android.database.Cursor r4 = r6.query(r7, r8, r9, r10, r11)
        L_0x00a8:
            if (r4 == 0) goto L_0x00d7
        L_0x00aa:
            boolean r13 = r4.moveToNext()     // Catch:{ all -> 0x00d2 }
            if (r13 == 0) goto L_0x00ce
            java.lang.String r13 = r4.getString(r5)     // Catch:{ all -> 0x00d2 }
            if (r13 == 0) goto L_0x00aa
            java.lang.String r13 = r4.getString(r5)     // Catch:{ all -> 0x00d2 }
            java.lang.String r13 = r13.trim()     // Catch:{ all -> 0x00d2 }
            r0.add(r13)     // Catch:{ all -> 0x00d2 }
            r13 = 1
            java.lang.String r13 = r4.getString(r13)     // Catch:{ all -> 0x00d2 }
            java.lang.String r13 = r13.trim()     // Catch:{ all -> 0x00d2 }
            r1.add(r13)     // Catch:{ all -> 0x00d2 }
            goto L_0x00aa
        L_0x00ce:
            r4.close()
            goto L_0x00d7
        L_0x00d2:
            r13 = move-exception
            r4.close()
            throw r13
        L_0x00d7:
            com.miui.earthquakewarning.ui.EarthquakeWarningMainFragment r13 = r12.mFragment
            r13.setContacts(r1, r0)
            goto L_0x0180
        L_0x00de:
            return
        L_0x00df:
            r5 = 1001(0x3e9, float:1.403E-42)
            if (r13 != r5) goto L_0x016d
            if (r15 != 0) goto L_0x00e6
            return
        L_0x00e6:
            android.net.Uri r7 = r15.getData()
            if (r7 != 0) goto L_0x00ed
            return
        L_0x00ed:
            android.content.ContentResolver r6 = r12.getContentResolver()     // Catch:{ all -> 0x0160 }
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            android.database.Cursor r13 = r6.query(r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0160 }
            if (r13 == 0) goto L_0x0157
        L_0x00fb:
            boolean r14 = r13.moveToNext()     // Catch:{ all -> 0x0155 }
            if (r14 == 0) goto L_0x0157
            java.lang.String r14 = "_id"
            int r14 = r13.getColumnIndex(r14)     // Catch:{ all -> 0x0155 }
            java.lang.String r14 = r13.getString(r14)     // Catch:{ all -> 0x0155 }
            boolean r15 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x0155 }
            if (r15 != 0) goto L_0x00fb
            android.content.ContentResolver r5 = r12.getContentResolver()     // Catch:{ all -> 0x0155 }
            android.net.Uri r6 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI     // Catch:{ all -> 0x0155 }
            r7 = 0
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0155 }
            r15.<init>()     // Catch:{ all -> 0x0155 }
            java.lang.String r8 = "contact_id = "
            r15.append(r8)     // Catch:{ all -> 0x0155 }
            r15.append(r14)     // Catch:{ all -> 0x0155 }
            java.lang.String r8 = r15.toString()     // Catch:{ all -> 0x0155 }
            r9 = 0
            r10 = 0
            android.database.Cursor r4 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0155 }
            if (r4 == 0) goto L_0x00fb
        L_0x0131:
            boolean r14 = r4.moveToNext()     // Catch:{ all -> 0x0155 }
            if (r14 == 0) goto L_0x00fb
            int r14 = r0.size()     // Catch:{ all -> 0x0155 }
            r15 = 3
            if (r14 >= r15) goto L_0x00fb
            int r14 = r4.getColumnIndex(r3)     // Catch:{ all -> 0x0155 }
            java.lang.String r14 = r4.getString(r14)     // Catch:{ all -> 0x0155 }
            r0.add(r14)     // Catch:{ all -> 0x0155 }
            int r14 = r4.getColumnIndex(r2)     // Catch:{ all -> 0x0155 }
            java.lang.String r14 = r4.getString(r14)     // Catch:{ all -> 0x0155 }
            r1.add(r14)     // Catch:{ all -> 0x0155 }
            goto L_0x0131
        L_0x0155:
            r14 = move-exception
            goto L_0x0162
        L_0x0157:
            if (r13 == 0) goto L_0x015c
            r13.close()
        L_0x015c:
            if (r4 == 0) goto L_0x00d7
            goto L_0x00ce
        L_0x0160:
            r14 = move-exception
            r13 = r4
        L_0x0162:
            if (r13 == 0) goto L_0x0167
            r13.close()
        L_0x0167:
            if (r4 == 0) goto L_0x016c
            r4.close()
        L_0x016c:
            throw r14
        L_0x016d:
            r15 = 1002(0x3ea, float:1.404E-42)
            if (r13 != r15) goto L_0x0180
            r13 = 1003(0x3eb, float:1.406E-42)
            if (r14 != r13) goto L_0x017b
            com.miui.earthquakewarning.ui.EarthquakeWarningMainFragment r13 = r12.mFragment
            r13.agreeResult()
            goto L_0x0180
        L_0x017b:
            com.miui.earthquakewarning.ui.EarthquakeWarningMainFragment r13 = r12.mFragment
            r13.disAgreeResult()
        L_0x0180:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.earthquakewarning.ui.EarthquakeWarningMainActivity.onActivityResult(int, int, android.content.Intent):void");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.IS_INTERNATIONAL_BUILD) {
            finish();
            return;
        }
        this.mFragment = new EarthquakeWarningMainFragment();
        getFragmentManager().beginTransaction().replace(16908290, this.mFragment).commit();
    }
}
