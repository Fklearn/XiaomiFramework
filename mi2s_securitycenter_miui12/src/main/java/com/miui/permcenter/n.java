package com.miui.permcenter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.x;
import com.miui.analytics.AnalyticsUtil;
import com.miui.permcenter.compact.AppOpsUtilsCompat;
import com.miui.permcenter.compact.EnterpriseCompat;
import com.miui.permcenter.compact.PermissionManagerCompat;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.permission.PermissionContract;
import com.miui.permission.PermissionManager;
import com.miui.permission.RequiredPermissionsUtil;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.app.AlertDialog;
import miui.util.IOUtils;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static final Set<Long> f6177a = new HashSet();

    /* renamed from: b  reason: collision with root package name */
    private static final Set<String> f6178b = new HashSet();

    public static class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        final WeakReference<Activity> f6179a;

        /* renamed from: b  reason: collision with root package name */
        final int f6180b;

        /* renamed from: c  reason: collision with root package name */
        final long f6181c;

        /* renamed from: d  reason: collision with root package name */
        final c f6182d;
        final String e;

        public a(Activity activity, String str, int i, long j, c cVar) {
            this.f6179a = new WeakReference<>(activity);
            this.e = str;
            this.f6181c = j;
            this.f6182d = cVar;
            this.f6180b = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Activity activity = (Activity) this.f6179a.get();
            if (activity != null && !activity.isDestroyed() && !activity.isFinishing()) {
                PermissionManagerCompat.setApplicationPermissionWithVirtual(PermissionManager.getInstance(activity), this.f6181c, this.f6180b, 2, this.e);
                c cVar = this.f6182d;
                if (cVar != null) {
                    cVar.a(this.e, this.f6180b);
                }
            }
        }
    }

    public static class b implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        final WeakReference<Activity> f6183a;

        /* renamed from: b  reason: collision with root package name */
        final int f6184b;

        /* renamed from: c  reason: collision with root package name */
        final boolean f6185c;

        /* renamed from: d  reason: collision with root package name */
        final long f6186d;
        final c e;
        final String f;

        public b(Activity activity, String str, int i, boolean z, long j, c cVar) {
            this.f6183a = new WeakReference<>(activity);
            this.f6184b = i;
            this.f6185c = z;
            this.f6186d = j;
            this.e = cVar;
            this.f = str;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:25:0x004f, code lost:
            if (r0 != 3) goto L_0x0056;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x002c, code lost:
            if (r0 != 1) goto L_0x0056;
         */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x005d A[SYNTHETIC, Splitter:B:30:0x005d] */
        /* JADX WARNING: Removed duplicated region for block: B:53:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onClick(android.content.DialogInterface r18, int r19) {
            /*
                r17 = this;
                r1 = r17
                r0 = r19
                java.lang.ref.WeakReference<android.app.Activity> r2 = r1.f6183a
                java.lang.Object r2 = r2.get()
                r4 = r2
                android.app.Activity r4 = (android.app.Activity) r4
                if (r4 == 0) goto L_0x00e7
                boolean r2 = r4.isFinishing()
                if (r2 != 0) goto L_0x00e7
                boolean r2 = r4.isDestroyed()
                if (r2 == 0) goto L_0x001d
                goto L_0x00e7
            L_0x001d:
                int r2 = r1.f6184b
                boolean r3 = r1.f6185c
                r5 = 32
                r7 = 29
                r8 = 2
                r9 = 3
                r10 = 1
                if (r3 == 0) goto L_0x0033
                if (r0 == 0) goto L_0x0031
                if (r0 == r10) goto L_0x002f
                goto L_0x0056
            L_0x002f:
                r2 = r10
                goto L_0x0056
            L_0x0031:
                r2 = r9
                goto L_0x0056
            L_0x0033:
                if (r0 == 0) goto L_0x003e
                if (r0 == r10) goto L_0x003c
                if (r0 == r8) goto L_0x003a
                goto L_0x003f
            L_0x003a:
                r2 = r10
                goto L_0x003f
            L_0x003c:
                r2 = r8
                goto L_0x003f
            L_0x003e:
                r2 = r9
            L_0x003f:
                int r3 = android.os.Build.VERSION.SDK_INT
                if (r3 < r7) goto L_0x0056
                long r11 = r1.f6186d
                int r3 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
                if (r3 != 0) goto L_0x0056
                if (r0 == 0) goto L_0x0031
                if (r0 == r10) goto L_0x0054
                if (r0 == r8) goto L_0x0052
                if (r0 == r9) goto L_0x002f
                goto L_0x0056
            L_0x0052:
                r2 = r8
                goto L_0x0056
            L_0x0054:
                r0 = 6
                r2 = r0
            L_0x0056:
                r18.dismiss()
                int r0 = r1.f6184b
                if (r2 == r0) goto L_0x00e7
                android.content.pm.PackageManager r0 = r4.getPackageManager()     // Catch:{ Exception -> 0x00df }
                java.lang.String r3 = r1.f     // Catch:{ Exception -> 0x00df }
                r8 = 8192(0x2000, float:1.14794E-41)
                android.content.pm.ApplicationInfo r0 = r0.getApplicationInfo(r3, r8)     // Catch:{ Exception -> 0x00df }
                if (r2 == r9) goto L_0x00a7
                long r8 = r1.f6186d     // Catch:{ Exception -> 0x00df }
                boolean r3 = com.miui.permcenter.n.a((long) r8)     // Catch:{ Exception -> 0x00df }
                if (r3 == 0) goto L_0x00a7
                if (r0 == 0) goto L_0x00a7
                int r0 = r0.targetSdkVersion     // Catch:{ Exception -> 0x00df }
                r3 = 23
                if (r0 >= r3) goto L_0x00a7
                miui.app.AlertDialog$Builder r0 = new miui.app.AlertDialog$Builder     // Catch:{ Exception -> 0x00df }
                r0.<init>(r4)     // Catch:{ Exception -> 0x00df }
                r3 = 2131757023(0x7f1007df, float:1.914497E38)
                miui.app.AlertDialog$Builder r0 = r0.setMessage(r3)     // Catch:{ Exception -> 0x00df }
                r3 = 2131755753(0x7f1002e9, float:1.9142394E38)
                r5 = 0
                miui.app.AlertDialog$Builder r0 = r0.setNegativeButton(r3, r5)     // Catch:{ Exception -> 0x00df }
                r10 = 2131757021(0x7f1007dd, float:1.9144966E38)
                com.miui.permcenter.n$a r11 = new com.miui.permcenter.n$a     // Catch:{ Exception -> 0x00df }
                java.lang.String r5 = r1.f     // Catch:{ Exception -> 0x00df }
                long r7 = r1.f6186d     // Catch:{ Exception -> 0x00df }
                com.miui.permcenter.n$c r9 = r1.e     // Catch:{ Exception -> 0x00df }
                r3 = r11
                r6 = r2
                r3.<init>(r4, r5, r6, r7, r9)     // Catch:{ Exception -> 0x00df }
                miui.app.AlertDialog$Builder r0 = r0.setPositiveButton(r10, r11)     // Catch:{ Exception -> 0x00df }
                r0.show()     // Catch:{ Exception -> 0x00df }
                goto L_0x00e7
            L_0x00a7:
                int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00df }
                r3 = 0
                if (r0 < r7) goto L_0x00c0
                long r7 = r1.f6186d     // Catch:{ Exception -> 0x00df }
                int r0 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x00c0
                com.miui.permission.PermissionManager r0 = com.miui.permission.PermissionManager.getInstance(r4)     // Catch:{ Exception -> 0x00df }
                java.lang.String[] r4 = new java.lang.String[r10]     // Catch:{ Exception -> 0x00df }
                java.lang.String r5 = r1.f     // Catch:{ Exception -> 0x00df }
                r4[r3] = r5     // Catch:{ Exception -> 0x00df }
                com.miui.permcenter.n.a((com.miui.permission.PermissionManager) r0, (int) r2, (java.lang.String[]) r4)     // Catch:{ Exception -> 0x00df }
                goto L_0x00d3
            L_0x00c0:
                com.miui.permission.PermissionManager r11 = com.miui.permission.PermissionManager.getInstance(r4)     // Catch:{ Exception -> 0x00df }
                long r12 = r1.f6186d     // Catch:{ Exception -> 0x00df }
                r15 = 2
                java.lang.String[] r0 = new java.lang.String[r10]     // Catch:{ Exception -> 0x00df }
                java.lang.String r4 = r1.f     // Catch:{ Exception -> 0x00df }
                r0[r3] = r4     // Catch:{ Exception -> 0x00df }
                r14 = r2
                r16 = r0
                com.miui.permcenter.compact.PermissionManagerCompat.setApplicationPermission(r11, r12, r14, r15, r16)     // Catch:{ Exception -> 0x00df }
            L_0x00d3:
                com.miui.permcenter.n$c r0 = r1.e     // Catch:{ Exception -> 0x00df }
                if (r0 == 0) goto L_0x00e7
                com.miui.permcenter.n$c r0 = r1.e     // Catch:{ Exception -> 0x00df }
                java.lang.String r3 = r1.f     // Catch:{ Exception -> 0x00df }
                r0.a(r3, r2)     // Catch:{ Exception -> 0x00df }
                goto L_0x00e7
            L_0x00df:
                r0 = move-exception
                java.lang.String r2 = "PermissionUtils"
                java.lang.String r3 = "getApplicationInfo"
                android.util.Log.e(r2, r3, r0)
            L_0x00e7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.n.b.onClick(android.content.DialogInterface, int):void");
        }
    }

    public interface c {
        void a(String str, int i);
    }

    static {
        f6177a.add(32L);
        f6178b.add("com.miui.huanji");
        f6178b.add("com.google.android.syncadapters.contacts");
        f6178b.add("com.miui.calculator");
        f6178b.add("com.miui.calculator2");
        f6178b.add("com.android.email");
        f6178b.add("com.miui.screenrecorder");
        f6178b.add("com.mi.liveassistant");
        f6178b.add("com.xiaomi.mifisecurity");
        f6178b.add("com.miui.virtualsim");
        f6178b.add("com.xiaomi.pass");
        f6178b.add("com.xiaomi.shop");
        f6178b.add("com.miui.smarttravel");
        f6178b.add("com.xiaomi.drivemode");
        f6178b.add("com.xiaomi.gamecenter.sdk.service");
        f6178b.add("com.miui.userguide");
        f6178b.add("com.xiaomi.jr");
        f6178b.add("com.xiaomi.mibrain.speech");
        f6178b.add("com.mi.health");
        f6178b.add("com.standardar.service");
        f6178b.add("com.miui.compass");
        f6178b.add("com.miui.notes");
        f6178b.add("com.xiaomi.gamecenter");
        f6178b.add("com.miui.cleanmaster");
        f6178b.add("com.miui.weather2");
        f6178b.add("com.xiaomi.scanner");
        f6178b.add("com.xiaomi.mimobile.noti");
        f6178b.add("com.xiaomi.vipaccount");
        f6178b.add("cn.wps.moffice_eng.xiaomi.lite");
        f6178b.add("com.duokan.reader");
        f6178b.add("com.example.testandroid");
    }

    public static int a(int i, int i2) {
        if (i == 3 && i2 == 3) {
            return 3;
        }
        if (i == 3) {
            return 6;
        }
        return i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
        if (r9 != 6) goto L_0x0034;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int a(long r7, int r9, boolean r10) {
        /*
            r0 = 0
            r1 = 3
            r2 = 2
            r3 = 1
            r4 = -1
            if (r10 == 0) goto L_0x000c
            if (r9 == r3) goto L_0x002e
            if (r9 == r1) goto L_0x0035
            goto L_0x0034
        L_0x000c:
            if (r9 == r3) goto L_0x0017
            if (r9 == r2) goto L_0x0015
            if (r9 == r1) goto L_0x0013
            goto L_0x0018
        L_0x0013:
            r4 = r0
            goto L_0x0018
        L_0x0015:
            r4 = r3
            goto L_0x0018
        L_0x0017:
            r4 = r2
        L_0x0018:
            int r10 = android.os.Build.VERSION.SDK_INT
            r5 = 29
            if (r10 < r5) goto L_0x0034
            r5 = 32
            int r7 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x0034
            if (r9 == r3) goto L_0x0032
            if (r9 == r2) goto L_0x0030
            if (r9 == r1) goto L_0x0035
            r7 = 6
            if (r9 == r7) goto L_0x002e
            goto L_0x0034
        L_0x002e:
            r0 = r3
            goto L_0x0035
        L_0x0030:
            r0 = r2
            goto L_0x0035
        L_0x0032:
            r0 = r1
            goto L_0x0035
        L_0x0034:
            r0 = r4
        L_0x0035:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.n.a(long, int, boolean):int");
    }

    public static a a(Context context, long j, String str) {
        Cursor cursor;
        String string;
        b.b.c.b.c cVar;
        String str2 = str;
        if (o.b(context)) {
            return l.a(context, j, str);
        }
        String[] strArr = {"pkgName", PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
        a aVar = null;
        try {
            String l = Long.toString(j);
            cursor = context.getContentResolver().query(PermissionContract.Active.URI, strArr, "permMask& ? != 0 and +present!= 0 and suggestBlock & ? == 0 and pkgName == ?", new String[]{l, l, str2}, (String) null);
            if (cursor != null) {
                a aVar2 = null;
                while (cursor.moveToNext()) {
                    try {
                        string = cursor.getString(0);
                        cVar = b.b.c.b.b.a(context).a(str2);
                    } catch (Exception e) {
                        Log.e("PermissionUtils", "fail getAppInfo", e);
                        cVar = null;
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(cursor);
                        throw th;
                    }
                    if (cVar != null) {
                        int calculatePermissionAction = PermissionManager.calculatePermissionAction(j, cursor.getLong(1), 0, cursor.getLong(2), cursor.getLong(3), cursor.getLong(4), 0, cursor.getLong(5), cursor.getLong(6));
                        a aVar3 = new a();
                        aVar3.b(string);
                        aVar3.d(false);
                        aVar3.a(cVar.a());
                        HashMap hashMap = new HashMap();
                        hashMap.put(Long.valueOf(j), Integer.valueOf(calculatePermissionAction));
                        aVar3.a((HashMap<Long, Integer>) hashMap);
                        aVar2 = aVar3;
                    }
                }
                aVar = aVar2;
            }
            IOUtils.closeQuietly(cursor);
            return aVar;
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static ArrayList<a> a(Context context, long j) {
        return a(context, j, false);
    }

    public static ArrayList<a> a(Context context, long j, boolean z) {
        Context context2 = context;
        if (o.b(context)) {
            return l.a(context, j, z);
        }
        List<PackageInfo> a2 = b.b.c.b.b.a(context).a();
        HashMap hashMap = new HashMap();
        for (PackageInfo next : a2) {
            if (EnterpriseCompat.shouldGrantPermission(context2, next.packageName)) {
                Log.d("Enterprise", "Permission edit for package " + next.packageName + " is restricted");
            } else {
                hashMap.put(next.packageName, next);
            }
        }
        String[] strArr = {"pkgName", PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
        ArrayList<a> arrayList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String l = Long.toString(j);
            cursor = context.getContentResolver().query(PermissionContract.Active.URI, strArr, "permMask& ? != 0 and +present!= 0 and suggestBlock & ? == 0", new String[]{l, l}, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String string = cursor.getString(0);
                    PackageInfo packageInfo = (PackageInfo) hashMap.get(string);
                    if (packageInfo != null) {
                        boolean z2 = (packageInfo.applicationInfo.flags & 1) != 0;
                        boolean z3 = B.a(packageInfo.applicationInfo.uid) <= 10000;
                        boolean isAdaptedRequiredPermissionsOnData = RequiredPermissionsUtil.isAdaptedRequiredPermissionsOnData(packageInfo);
                        if ((!z2 && !isAdaptedRequiredPermissionsOnData && !z3) || z) {
                            int calculatePermissionAction = PermissionManager.calculatePermissionAction(j, cursor.getLong(1), 0, cursor.getLong(2), cursor.getLong(3), cursor.getLong(4), 0, cursor.getLong(5), cursor.getLong(6));
                            a aVar = new a();
                            aVar.b(string);
                            aVar.d(z2);
                            aVar.a(x.a(context2, packageInfo.applicationInfo));
                            HashMap hashMap2 = new HashMap();
                            hashMap2.put(Long.valueOf(j), Integer.valueOf(calculatePermissionAction));
                            aVar.a((HashMap<Long, Integer>) hashMap2);
                            arrayList.add(aVar);
                        }
                    }
                }
                Collections.sort(arrayList, new b());
            }
            return arrayList;
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public static ArrayList<String> a(Context context, long j, boolean z, boolean z2) {
        String string = context.getString(R.string.permission_action_accept);
        String string2 = context.getString(R.string.permission_action_reject);
        ArrayList<String> arrayList = new ArrayList<>();
        if (Build.VERSION.SDK_INT < 29 || !f6177a.contains(Long.valueOf(j))) {
            arrayList.add(string);
        } else {
            arrayList.add(context.getString(R.string.permission_action_always));
            arrayList.add(context.getString(R.string.permission_action_foreground));
        }
        if (!z) {
            arrayList.add(context.getString(R.string.permission_action_prompt));
        }
        if (!z2) {
            arrayList.add(string2);
        }
        return arrayList;
    }

    public static HashMap<Long, Integer> a(Context context, String str) {
        if (o.b(context)) {
            return l.a(context, str);
        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            String[] strArr = {PermissionContract.Active.PERMISSION_MASK, PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
            Cursor query = contentResolver.query(PermissionContract.Active.URI, strArr, "pkgName =? ", new String[]{str}, (String) null);
            if (query != null) {
                try {
                    if (query.getCount() >= 0) {
                        PermissionManager instance = PermissionManager.getInstance(context);
                        if (query.moveToNext()) {
                            HashMap<Long, Integer> calculatePermissionAction = instance.calculatePermissionAction(query.getLong(0), query.getLong(1), 0, query.getLong(2), query.getLong(3), 0, query.getLong(4), 0, query.getLong(5), query.getLong(6));
                            IOUtils.closeQuietly(query);
                            return calculatePermissionAction;
                        }
                        IOUtils.closeQuietly(query);
                        return null;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    IOUtils.closeQuietly(cursor);
                    throw th;
                }
            }
            IOUtils.closeQuietly(query);
            return null;
        } catch (Throwable th2) {
            th = th2;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static void a(Activity activity, String str, long j, String str2, int i, c cVar, boolean z, boolean z2, String str3, String str4, boolean z3) {
        Activity activity2 = activity;
        long j2 = j;
        boolean z4 = z;
        if (o.b((Context) activity)) {
            l.a(activity, str, 0, j, str2, i, cVar, z, z2, str3, str4, z3);
            return;
        }
        ArrayList<String> a2 = a(activity, j2, z4, z2);
        int i2 = i;
        int a3 = a(j2, i2, z4);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(str2);
        builder.setSingleChoiceItems((CharSequence[]) a2.toArray(new String[0]), a3, new b(activity, str, i2, z, j, cVar)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    public static void a(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int applicationEnabledSetting = packageManager.getApplicationEnabledSetting("com.lbe.security.miui");
            if (applicationEnabledSetting == 2 || applicationEnabledSetting == 3 || applicationEnabledSetting == 4) {
                packageManager.setApplicationEnabledSetting("com.lbe.security.miui", 0, 0);
                Log.w("PermissionUtils", "enable lbe security");
                AnalyticsUtil.recordCountEvent("permcenter", "service_disabled", (Map<String, String>) null);
            }
        } catch (Exception e) {
            Log.e("PermissionUtils", " ApplicationEnabledSetting error ", e);
        }
        try {
            Intent intent = new Intent("com.miui.permission.Action.SecurityService");
            intent.setPackage("com.lbe.security.miui");
            context.startService(intent);
        } catch (Exception e2) {
            Log.e("PermissionUtils", "startService", e2);
        }
    }

    public static void a(Context context, long j, String... strArr) {
        PermissionManager.getInstance(context).setApplicationPermission(j, 3, strArr);
    }

    public static void a(Context context, boolean z) {
        PermissionManager.getInstance(context).setEnabled(z);
    }

    public static void a(PermissionManager permissionManager, int i, String... strArr) {
        int i2;
        int i3;
        if (i == 3) {
            i3 = 3;
        } else if (i == 1) {
            i3 = 1;
        } else if (i == 6) {
            i3 = 1;
            i2 = 3;
            PermissionManagerCompat.setApplicationPermission(permissionManager, 32, i2, 2, strArr);
            PermissionManagerCompat.setApplicationPermission(permissionManager, PermissionManager.PERM_ID_BACKGROUND_LOCATION, i3, 2, strArr);
        } else {
            i3 = 2;
        }
        i2 = i3;
        PermissionManagerCompat.setApplicationPermission(permissionManager, 32, i2, 2, strArr);
        PermissionManagerCompat.setApplicationPermission(permissionManager, PermissionManager.PERM_ID_BACKGROUND_LOCATION, i3, 2, strArr);
    }

    public static boolean a() {
        return AppOpsUtilsCompat.isXOptMode();
    }

    public static boolean a(long j) {
        return j == PermissionManager.PERM_ID_EXTERNAL_STORAGE || j == 64 || j == PermissionManager.PERM_ID_BODY_SENSORS || j == PermissionManager.PERM_ID_GET_ACCOUNTS || j == PermissionManager.PERM_ID_ADD_VOICEMAIL || j == PermissionManager.PERM_ID_USE_SIP || j == PermissionManager.PERM_ID_PROCESS_OUTGOING_CALLS;
    }

    public static boolean a(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) > 0;
    }

    public static boolean a(Long l) {
        return PermissionManager.isExistInMcallAndcontactpermissionlist(l);
    }

    public static ArrayList<a> b(Context context, long j) {
        int i;
        Cursor cursor;
        Cursor cursor2;
        ArrayList<a> arrayList;
        int i2;
        int i3;
        int i4;
        HashMap hashMap;
        int i5;
        int i6;
        int i7;
        ArrayList<a> arrayList2;
        if (o.b(context)) {
            return l.a(context, j);
        }
        List<PackageInfo> a2 = b.b.c.b.b.a(context).a();
        HashMap hashMap2 = new HashMap();
        Iterator<PackageInfo> it = a2.iterator();
        while (true) {
            i = 1;
            if (!it.hasNext()) {
                break;
            }
            PackageInfo next = it.next();
            ApplicationInfo applicationInfo = next.applicationInfo;
            if ((applicationInfo.flags & 1) == 0) {
                hashMap2.put(next.packageName, applicationInfo);
            }
        }
        int i8 = 3;
        int i9 = 4;
        int i10 = 5;
        int i11 = 6;
        String[] strArr = {"pkgName", PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
        ArrayList<a> arrayList3 = new ArrayList<>();
        try {
            String l = Long.toString(j);
            int i12 = 2;
            int i13 = 0;
            Cursor query = context.getContentResolver().query(PermissionContract.Active.URI, strArr, "permMask& ? != 0 and +present!= 0 and suggestBlock & ? == 0 ", new String[]{l, l}, (String) null);
            if (query != null) {
                while (query.moveToNext()) {
                    try {
                        String string = query.getString(i13);
                        ApplicationInfo applicationInfo2 = (ApplicationInfo) hashMap2.get(string);
                        if (applicationInfo2 != null) {
                            long j2 = query.getLong(i);
                            long j3 = query.getLong(i12);
                            long j4 = query.getLong(i8);
                            long j5 = query.getLong(i9);
                            long j6 = query.getLong(i10);
                            long j7 = query.getLong(i11);
                            if ((j5 & j) == 0 && (j6 & j) == 0 && (j7 & j) == 0) {
                                Context context2 = context;
                                cursor = query;
                                i4 = i13;
                                arrayList2 = arrayList3;
                                i3 = i12;
                                i7 = i10;
                                i2 = i11;
                                i5 = i;
                                i6 = i9;
                                hashMap = hashMap2;
                            } else {
                                String str = string;
                                ApplicationInfo applicationInfo3 = applicationInfo2;
                                cursor = query;
                                i4 = i13;
                                ArrayList<a> arrayList4 = arrayList3;
                                i3 = i12;
                                i7 = i10;
                                i2 = i11;
                                long j8 = j4;
                                i5 = i;
                                i6 = i9;
                                long j9 = j5;
                                hashMap = hashMap2;
                                try {
                                    int calculatePermissionAction = PermissionManager.calculatePermissionAction(j, j2, 0, j3, j8, j9, 0, j6, j7);
                                    a aVar = new a();
                                    aVar.b(str);
                                    aVar.a(x.a(context, applicationInfo3));
                                    HashMap hashMap3 = new HashMap();
                                    hashMap3.put(Long.valueOf(j), Integer.valueOf(calculatePermissionAction));
                                    aVar.a((HashMap<Long, Integer>) hashMap3);
                                    arrayList2 = arrayList4;
                                    arrayList2.add(aVar);
                                } catch (Throwable th) {
                                    th = th;
                                    IOUtils.closeQuietly(cursor);
                                    throw th;
                                }
                            }
                            arrayList3 = arrayList2;
                            i10 = i7;
                            i9 = i6;
                            i = i5;
                            hashMap2 = hashMap;
                            query = cursor;
                            i13 = i4;
                            i12 = i3;
                            i11 = i2;
                            i8 = 3;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = query;
                        IOUtils.closeQuietly(cursor);
                        throw th;
                    }
                }
                cursor2 = query;
                arrayList = arrayList3;
                Collections.sort(arrayList, new b());
            } else {
                cursor2 = query;
                arrayList = arrayList3;
            }
            IOUtils.closeQuietly(cursor2);
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static HashMap<Long, Integer> b(Context context, String str) {
        if (o.b(context)) {
            return l.b(context, str);
        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            String[] strArr = {PermissionContract.Active.PERMISSION_MASK, PermissionContract.Active.SUGGEST_ACCEPT, PermissionContract.Active.SUGGEST_PROMPT, PermissionContract.Active.SUGGEST_REJECT, PermissionContract.Active.SUGGEST_BLOCK, PermissionContract.Active.USER_ACCEPT, PermissionContract.Active.USER_PROMPT, PermissionContract.Active.USER_REJECT};
            Cursor query = contentResolver.query(PermissionContract.Active.URI, strArr, "pkgName =? ", new String[]{str}, (String) null);
            if (query != null) {
                try {
                    if (query.getCount() >= 0) {
                        PermissionManager instance = PermissionManager.getInstance(context);
                        if (query.moveToNext()) {
                            HashMap<Long, Integer> calculatePermissionAction = instance.calculatePermissionAction(query.getLong(0), query.getLong(1), 0, query.getLong(2), query.getLong(3), query.getLong(4), query.getLong(5), 0, query.getLong(6), query.getLong(7));
                            IOUtils.closeQuietly(query);
                            return calculatePermissionAction;
                        }
                        IOUtils.closeQuietly(query);
                        return null;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    IOUtils.closeQuietly(cursor);
                    throw th;
                }
            }
            IOUtils.closeQuietly(query);
            return null;
        } catch (Throwable th2) {
            th = th2;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static void b(Context context, long j, String... strArr) {
        PermissionManager.getInstance(context).setApplicationPermission(j, 0, strArr);
    }

    public static boolean b(Context context) {
        try {
            return PermissionManager.getInstance(context).isEnabled();
        } catch (Exception e) {
            Log.e("PermissionUtils", "isAppPermissionControlOpen Exception", e);
            return false;
        }
    }

    public static boolean b(Long l) {
        return PermissionManager.isExistInMsmsAndmmspermissionlist(l);
    }

    public static ArrayList<a> c(Context context) {
        List<PackageInfo> a2 = b.b.c.b.b.a(context).a();
        HashMap hashMap = new HashMap();
        for (PackageInfo next : a2) {
            if (EnterpriseCompat.shouldGrantPermission(context, next.packageName)) {
                Log.d("Enterprise", "Permission edit for package " + next.packageName + " is restricted");
            } else {
                ApplicationInfo applicationInfo = next.applicationInfo;
                if (((1 & applicationInfo.flags) == 0 && B.a(applicationInfo.uid) > 10000) || RequiredPermissionsUtil.isAdaptedRequiredPermissions(next)) {
                    hashMap.put(next.packageName, next);
                }
            }
        }
        String[] strArr = {"pkgName", PermissionContract.Active.PERMISSION_MASK, PermissionContract.Active.SUGGEST_BLOCK};
        ArrayList<a> arrayList = new ArrayList<>();
        Cursor cursor = null;
        try {
            PermissionManager instance = PermissionManager.getInstance(context);
            cursor = context.getContentResolver().query(PermissionContract.Active.URI, strArr, "present!= 0", (String[]) null, (String) null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    boolean z = false;
                    String string = cursor.getString(0);
                    PackageInfo packageInfo = (PackageInfo) hashMap.get(string);
                    if (packageInfo != null) {
                        int calculatePermissionCount = instance.calculatePermissionCount(cursor.getLong(1), cursor.getLong(2));
                        if (calculatePermissionCount > 0) {
                            a aVar = new a();
                            aVar.b(string);
                            aVar.a(x.a(context, packageInfo.applicationInfo));
                            aVar.a(calculatePermissionCount);
                            if ((packageInfo.applicationInfo.flags & 1) != 0) {
                                z = true;
                            }
                            aVar.d(z);
                            aVar.a(RequiredPermissionsUtil.isAdaptedRequiredPermissionsOnData(packageInfo));
                            arrayList.add(aVar);
                        }
                    }
                }
                Collections.sort(arrayList, new b());
            }
            return arrayList;
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public static void c(Context context, long j, String... strArr) {
        PermissionManager.getInstance(context).setApplicationPermission(j, 1, strArr);
    }

    public static boolean c(Context context, String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        return applicationInfo != null && a(applicationInfo);
    }

    public static void d(Context context) {
        if (context != null) {
            new m(context).execute(new Void[0]);
        }
    }

    public static boolean d(Context context, String str) {
        return f6178b.contains(str) || c(context, str) || e(context, str);
    }

    private static boolean e(Context context, String str) {
        try {
            ArrayList<String> stringArrayList = context.getContentResolver().call(Uri.parse("content://com.miui.sec.THIRD_DESKTOP"), "getListForCTAEnable", (String) null, (Bundle) null).getStringArrayList("list");
            return stringArrayList != null && stringArrayList.contains(str);
        } catch (Exception e) {
            Log.e("PermissionUtils", "get third desktop provider exception!", e);
            return false;
        }
    }
}
