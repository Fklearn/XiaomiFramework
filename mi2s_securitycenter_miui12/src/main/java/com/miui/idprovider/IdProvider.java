package com.miui.idprovider;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.y;
import com.miui.idprovider.b.b;
import com.miui.networkassistant.config.Constants;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import miui.util.IOUtils;

public class IdProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static List<String> f5603a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private static Set<String> f5604b = new HashSet();

    /* renamed from: c  reason: collision with root package name */
    private static final UriMatcher f5605c = new UriMatcher(-1);
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f5606d;
    private boolean e = false;
    private final Object f = new Object();
    private final Object g = new Object();
    private final Object h = new Object();
    private String i;
    private String j;
    /* access modifiers changed from: private */
    public b k;
    private AppOpsManager l;

    private class a extends BroadcastReceiver {
        private a() {
        }

        /* synthetic */ a(IdProvider idProvider, a aVar) {
            this();
        }

        private String a(Intent intent) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getSchemeSpecificPart();
            }
            return null;
        }

        public void onReceive(Context context, Intent intent) {
            String a2 = a(intent);
            SQLiteDatabase writableDatabase = IdProvider.this.k.getWritableDatabase();
            SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
            sQLiteQueryBuilder.setTables("AAID");
            sQLiteQueryBuilder.appendWhere("package_name='" + a2 + "'");
            Cursor query = sQLiteQueryBuilder.query(writableDatabase, (String[]) null, (String) null, (String[]) null, (String) null, (String) null, (String) null);
            if (query != null) {
                try {
                    if (query.getCount() > 0) {
                        writableDatabase.delete("AAID", "package_name = ?", new String[]{a2});
                    }
                } catch (Exception e) {
                    Log.e("IdProvider", "Package removed query exception!", e);
                } catch (Throwable th) {
                    IOUtils.closeQuietly(query);
                    throw th;
                }
                IOUtils.closeQuietly(query);
            }
        }
    }

    static {
        f5603a.add("com.android.settings");
        f5603a.add("com.miui.securitycenter");
        f5604b.add(Constants.System.ANDROID_PACKAGE_NAME);
        f5604b.add("com.miui.securitycenter");
        f5604b.add("com.android.settings");
        f5604b.add("com.xiaomi.finddevice");
        f5604b.add("com.miui.greenguard");
        f5604b.add("com.miui.securitycore");
        f5605c.addURI("com.miui.idprovider", "udid", 1);
        f5605c.addURI("com.miui.idprovider", "oaid", 2);
        f5605c.addURI("com.miui.idprovider", "vaid", 3);
        f5605c.addURI("com.miui.idprovider", "aaid", 4);
    }

    private String a() {
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getCallingPackage(), 64);
            byte[] bArr = new byte[32];
            String a2 = com.miui.common.persistence.b.a("VAID_" + UserHandle.getUserId(Binder.getCallingUid()), (String) null);
            if (TextUtils.isEmpty(a2)) {
                new SecureRandom().nextBytes(bArr);
                com.miui.common.persistence.b.b("VAID_" + UserHandle.getUserId(Binder.getCallingUid()), com.miui.idprovider.c.a.a(bArr));
            } else {
                bArr = com.miui.idprovider.c.a.a(a2);
            }
            if (bArr == null || !(bArr.length == 16 || bArr.length == 32)) {
                throw new IllegalStateException("User key invalid");
            }
            try {
                Mac instance = Mac.getInstance("HmacSHA256");
                instance.init(new SecretKeySpec(bArr, instance.getAlgorithm()));
                int i2 = 0;
                while (true) {
                    Signature[] signatureArr = packageInfo.signatures;
                    if (i2 >= signatureArr.length) {
                        return com.miui.idprovider.c.a.a(instance.doFinal()).substring(0, 16).toLowerCase(Locale.US);
                    }
                    byte[] byteArray = signatureArr[i2].toByteArray();
                    instance.update(ByteBuffer.allocate(4).putInt(byteArray.length).array(), 0, 4);
                    instance.update(byteArray);
                    i2++;
                }
            } catch (NoSuchAlgorithmException e2) {
                throw new IllegalStateException("HmacSHA256 is not available", e2);
            } catch (InvalidKeyException e3) {
                throw new IllegalStateException("Key is corrupted", e3);
            }
        } catch (Exception e4) {
            Log.e("IdProvider", "get callingPkg exception", e4);
            return "";
        }
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        return null;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        if (!f5603a.contains(getCallingPackage())) {
            return 0;
        }
        this.k.getWritableDatabase().delete("AAID", (String) null, (String[]) null);
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        this.k = com.miui.idprovider.b.a.a(getContext());
        this.l = (AppOpsManager) getContext().getSystemService("appops");
        getContext().registerReceiver(new a(this, (a) null), new IntentFilter(Constants.System.ACTION_PACKAGE_REMOVED));
        this.f5606d = Settings.Secure.getInt(getContext().getContentResolver(), "allow_oaid_used", 1) == 1;
        getContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("allow_oaid_used"), false, new a(this, new Handler()));
        try {
            if (Integer.parseInt(y.a("ro.miui.ui.version.name", "V0").substring(1)) >= 12) {
                this.e = true;
            }
        } catch (Exception e2) {
            Log.e("IdProvider", "get miuiVersion Exception!", e2);
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0174, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.database.Cursor query(android.net.Uri r17, java.lang.String[] r18, java.lang.String r19, java.lang.String[] r20, java.lang.String r21) {
        /*
            r16 = this;
            r1 = r16
            com.miui.idprovider.b.b r0 = r1.k
            android.database.sqlite.SQLiteDatabase r0 = r0.getWritableDatabase()
            android.database.sqlite.SQLiteQueryBuilder r2 = new android.database.sqlite.SQLiteQueryBuilder
            r2.<init>()
            java.lang.StringBuffer r10 = new java.lang.StringBuffer
            r10.<init>()
            android.content.UriMatcher r3 = f5605c
            r4 = r17
            int r3 = r3.match(r4)
            r11 = 0
            r12 = 1
            if (r3 == r12) goto L_0x022c
            r4 = 2
            if (r3 == r4) goto L_0x018b
            r5 = 3
            if (r3 == r5) goto L_0x0108
            r6 = 4
            if (r3 == r6) goto L_0x0029
            goto L_0x0272
        L_0x0029:
            java.lang.String r14 = r16.getCallingPackage()
            boolean r3 = r1.e     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            if (r3 != 0) goto L_0x007e
            java.lang.String r3 = "IdProvider"
            android.app.AppOpsManager r6 = r1.l     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.String r7 = "checkOpNoThrow"
            java.lang.Class[] r8 = new java.lang.Class[r5]     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Class r9 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r8[r11] = r9     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Class r9 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r8[r12] = r9     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Class<java.lang.String> r9 = java.lang.String.class
            r8[r4] = r9     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Class<android.app.AppOpsManager> r9 = android.app.AppOpsManager.class
            java.lang.String r15 = "OP_GET_ANONYMOUS_ID"
            java.lang.Class r13 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Object r9 = b.b.o.g.e.a((java.lang.Class<?>) r9, (java.lang.String) r15, r13)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r5[r11] = r9     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            android.content.Context r9 = r16.getContext()     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            android.content.pm.PackageManager r9 = r9.getPackageManager()     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r13 = 64
            android.content.pm.PackageInfo r9 = r9.getPackageInfo(r14, r13)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            android.content.pm.ApplicationInfo r9 = r9.applicationInfo     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            int r9 = r9.uid     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r5[r12] = r9     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r5[r4] = r14     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Object r3 = b.b.o.g.d.a((java.lang.String) r3, (java.lang.Object) r6, (java.lang.String) r7, (java.lang.Class<?>[]) r8, (java.lang.Object[]) r5)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            if (r3 == 0) goto L_0x007e
            r3 = 0
            miui.util.IOUtils.closeQuietly(r3)
            return r3
        L_0x007e:
            java.lang.String r3 = "AAID"
            r2.setTables(r3)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r3.<init>()     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.String r4 = "package_name='"
            r3.append(r4)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r3.append(r14)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.String r4 = "'"
            r3.append(r4)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r2.appendWhere(r3)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            java.lang.Object r13 = r1.h     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            monitor-enter(r13)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r3 = r0
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x00e6 }
            if (r2 == 0) goto L_0x00c0
            boolean r3 = r2.moveToNext()     // Catch:{ all -> 0x00f0 }
            if (r3 == 0) goto L_0x00c0
            java.lang.String r0 = "aaid"
            int r0 = r2.getColumnIndex(r0)     // Catch:{ all -> 0x00f0 }
            java.lang.String r0 = r2.getString(r0)     // Catch:{ all -> 0x00f0 }
            r10.append(r0)     // Catch:{ all -> 0x00f0 }
            goto L_0x00e0
        L_0x00c0:
            java.util.UUID r3 = java.util.UUID.randomUUID()     // Catch:{ all -> 0x00f0 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00f0 }
            android.content.ContentValues r4 = new android.content.ContentValues     // Catch:{ all -> 0x00f0 }
            r4.<init>()     // Catch:{ all -> 0x00f0 }
            java.lang.String r5 = "aaid"
            r4.put(r5, r3)     // Catch:{ all -> 0x00f0 }
            java.lang.String r5 = "package_name"
            r4.put(r5, r14)     // Catch:{ all -> 0x00f0 }
            java.lang.String r5 = "AAID"
            r6 = 0
            r0.insert(r5, r6, r4)     // Catch:{ all -> 0x00f0 }
            r10.append(r3)     // Catch:{ all -> 0x00f0 }
        L_0x00e0:
            monitor-exit(r13)     // Catch:{ all -> 0x00f0 }
        L_0x00e1:
            miui.util.IOUtils.closeQuietly(r2)
            goto L_0x0272
        L_0x00e6:
            r0 = move-exception
            r2 = 0
        L_0x00e8:
            monitor-exit(r13)     // Catch:{ all -> 0x00f0 }
            throw r0     // Catch:{ Exception -> 0x00ed, all -> 0x00ea }
        L_0x00ea:
            r0 = move-exception
            r13 = r2
            goto L_0x0104
        L_0x00ed:
            r0 = move-exception
            r13 = r2
            goto L_0x00f7
        L_0x00f0:
            r0 = move-exception
            goto L_0x00e8
        L_0x00f2:
            r0 = move-exception
            r13 = 0
            goto L_0x0104
        L_0x00f5:
            r0 = move-exception
            r13 = 0
        L_0x00f7:
            java.lang.String r2 = "IdProvider"
            java.lang.String r3 = "Cursor err in aaid query: "
            android.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x0103 }
        L_0x00fe:
            miui.util.IOUtils.closeQuietly(r13)
            goto L_0x0272
        L_0x0103:
            r0 = move-exception
        L_0x0104:
            miui.util.IOUtils.closeQuietly(r13)
            throw r0
        L_0x0108:
            java.lang.String r13 = r16.getCallingPackage()
            java.lang.String r3 = "VAID"
            r2.setTables(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "package_name='"
            r3.append(r4)
            r3.append(r13)
            java.lang.String r4 = "'"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.appendWhere(r3)
            java.lang.Object r14 = r1.g     // Catch:{ Exception -> 0x017b, all -> 0x0178 }
            monitor-enter(r14)     // Catch:{ Exception -> 0x017b, all -> 0x0178 }
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r3 = r0
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0170 }
            if (r2 == 0) goto L_0x014e
            boolean r3 = r2.moveToNext()     // Catch:{ all -> 0x016d }
            if (r3 == 0) goto L_0x014e
            java.lang.String r0 = "vaid"
            int r0 = r2.getColumnIndex(r0)     // Catch:{ all -> 0x016d }
            java.lang.String r0 = r2.getString(r0)     // Catch:{ all -> 0x016d }
            r10.append(r0)     // Catch:{ all -> 0x016d }
            goto L_0x016a
        L_0x014e:
            java.lang.String r3 = r16.a()     // Catch:{ all -> 0x016d }
            android.content.ContentValues r4 = new android.content.ContentValues     // Catch:{ all -> 0x016d }
            r4.<init>()     // Catch:{ all -> 0x016d }
            java.lang.String r5 = "vaid"
            r4.put(r5, r3)     // Catch:{ all -> 0x016d }
            java.lang.String r5 = "package_name"
            r4.put(r5, r13)     // Catch:{ all -> 0x016d }
            java.lang.String r5 = "VAID"
            r6 = 0
            r0.insert(r5, r6, r4)     // Catch:{ all -> 0x016d }
            r10.append(r3)     // Catch:{ all -> 0x016d }
        L_0x016a:
            monitor-exit(r14)     // Catch:{ all -> 0x016d }
            goto L_0x00e1
        L_0x016d:
            r0 = move-exception
            r13 = r2
            goto L_0x0172
        L_0x0170:
            r0 = move-exception
            r13 = 0
        L_0x0172:
            monitor-exit(r14)     // Catch:{ all -> 0x0176 }
            throw r0     // Catch:{ Exception -> 0x0174 }
        L_0x0174:
            r0 = move-exception
            goto L_0x017d
        L_0x0176:
            r0 = move-exception
            goto L_0x0172
        L_0x0178:
            r0 = move-exception
            r13 = 0
            goto L_0x0187
        L_0x017b:
            r0 = move-exception
            r13 = 0
        L_0x017d:
            java.lang.String r2 = "IdProvider"
            java.lang.String r3 = "Cursor err in vaid query: "
            android.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x0186 }
            goto L_0x00fe
        L_0x0186:
            r0 = move-exception
        L_0x0187:
            miui.util.IOUtils.closeQuietly(r13)
            throw r0
        L_0x018b:
            boolean r0 = r1.f5606d
            if (r0 == 0) goto L_0x0272
            java.lang.Object r2 = r1.f
            monitor-enter(r2)
            java.lang.String r0 = r1.j     // Catch:{ all -> 0x0229 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0229 }
            if (r0 == 0) goto L_0x0222
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0229 }
            r0.<init>()     // Catch:{ all -> 0x0229 }
            java.lang.String r3 = "OAID_"
            r0.append(r3)     // Catch:{ all -> 0x0229 }
            int r3 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0229 }
            int r3 = android.os.UserHandle.getUserId(r3)     // Catch:{ all -> 0x0229 }
            r0.append(r3)     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0229 }
            r3 = 0
            java.lang.String r0 = com.miui.common.persistence.b.a((java.lang.String) r0, (java.lang.String) r3)     // Catch:{ all -> 0x0229 }
            r1.j = r0     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = r1.j     // Catch:{ all -> 0x0229 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0229 }
            if (r0 == 0) goto L_0x0222
            java.security.SecureRandom r0 = new java.security.SecureRandom     // Catch:{ all -> 0x0229 }
            r0.<init>()     // Catch:{ all -> 0x0229 }
            long r5 = r0.nextLong()     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = java.lang.Long.toHexString(r5)     // Catch:{ all -> 0x0229 }
            r1.j = r0     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = r1.j     // Catch:{ all -> 0x0229 }
            int r0 = r0.length()     // Catch:{ all -> 0x0229 }
            r3 = 16
            if (r0 >= r3) goto L_0x0204
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0229 }
            r5.<init>()     // Catch:{ all -> 0x0229 }
            java.lang.String r6 = "%0"
            r5.append(r6)     // Catch:{ all -> 0x0229 }
            int r3 = r3 - r0
            r5.append(r3)     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = "d%s"
            r5.append(r0)     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0229 }
            java.lang.Object[] r3 = new java.lang.Object[r4]     // Catch:{ all -> 0x0229 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x0229 }
            r3[r11] = r4     // Catch:{ all -> 0x0229 }
            java.lang.String r4 = r1.j     // Catch:{ all -> 0x0229 }
            r3[r12] = r4     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = java.lang.String.format(r0, r3)     // Catch:{ all -> 0x0229 }
            r1.j = r0     // Catch:{ all -> 0x0229 }
        L_0x0204:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0229 }
            r0.<init>()     // Catch:{ all -> 0x0229 }
            java.lang.String r3 = "OAID_"
            r0.append(r3)     // Catch:{ all -> 0x0229 }
            int r3 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0229 }
            int r3 = android.os.UserHandle.getUserId(r3)     // Catch:{ all -> 0x0229 }
            r0.append(r3)     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0229 }
            java.lang.String r3 = r1.j     // Catch:{ all -> 0x0229 }
            com.miui.common.persistence.b.b((java.lang.String) r0, (java.lang.String) r3)     // Catch:{ all -> 0x0229 }
        L_0x0222:
            java.lang.String r0 = r1.j     // Catch:{ all -> 0x0229 }
            r10.append(r0)     // Catch:{ all -> 0x0229 }
            monitor-exit(r2)     // Catch:{ all -> 0x0229 }
            goto L_0x0272
        L_0x0229:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0229 }
            throw r0
        L_0x022c:
            java.lang.String r0 = r16.getCallingPackage()
            java.util.Set<java.lang.String> r2 = f5604b
            boolean r0 = r2.contains(r0)
            if (r0 != 0) goto L_0x023a
            r2 = 0
            return r2
        L_0x023a:
            java.lang.String r0 = r1.i
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x026d
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
            java.lang.String r2 = com.miui.securitycenter.utils.d.c()
            r0.append(r2)
            java.lang.String r2 = com.miui.idprovider.c.b.a((int) r11)
            r0.append(r2)
            java.lang.String r2 = com.miui.idprovider.c.b.a((int) r12)
            r0.append(r2)
            java.lang.String r2 = com.miui.idprovider.c.b.a()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r0 = com.miui.idprovider.c.c.a((java.lang.String) r0)
            r1.i = r0
        L_0x026d:
            java.lang.String r0 = r1.i
            r10.append(r0)
        L_0x0272:
            android.database.MatrixCursor r0 = new android.database.MatrixCursor
            java.lang.String r2 = "uniform_id"
            java.lang.String[] r2 = new java.lang.String[]{r2}
            r0.<init>(r2)
            java.lang.String[] r2 = new java.lang.String[r12]
            java.lang.String r3 = r10.toString()
            r2[r11] = r3
            r0.addRow(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.idprovider.IdProvider.query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (f5605c.match(uri) != 2 || !f5603a.contains(getCallingPackage())) {
            return 0;
        }
        this.j = null;
        com.miui.common.persistence.b.b("OAID_" + UserHandle.getUserId(Binder.getCallingUid()), (String) null);
        getContext().sendBroadcast(new Intent("miui.intent.action.oaid_changed"), "miui.securitycenter.permission.ANALYTICS");
        return 0;
    }
}
