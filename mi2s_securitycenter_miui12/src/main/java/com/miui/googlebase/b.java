package com.miui.googlebase;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.d;
import com.miui.permcenter.compact.MiuiSettingsCompat;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final String[] f5438a = {"com.google.android.gsf", "com.google.android.gsf.login", "com.google.android.gms", "com.google.android.partnersetup"};

    /* renamed from: b  reason: collision with root package name */
    private static Method f5439b = null;

    /* renamed from: c  reason: collision with root package name */
    private static HashSet<String> f5440c = new HashSet<>();

    /* renamed from: d  reason: collision with root package name */
    private final String f5441d;
    private String e = null;
    private Context f;

    static {
        f5440c.add("com.google.android.gm");
        f5440c.add("com.google.android.apps.magazines");
        f5440c.add("com.google.android.apps.books");
        f5440c.add("com.google.android.apps.plus");
        f5440c.add("com.google.android.videos");
        f5440c.add("com.google.android.apps.docs");
        f5440c.add("com.android.vending");
        f5440c.add("com.google.android.youtube");
        f5440c.add("com.google.android.play.games");
        f5440c.add("com.google.android.apps.photos");
        f5440c.add("com.google.android.talk");
        f5440c.add("com.google.android.music");
        f5440c.add("com.google.android.apps.maps");
    }

    public b(Context context, String str) {
        this.f = context;
        this.f5441d = str;
        if (f5439b == null) {
            try {
                f5439b = Class.forName("android.content.pm.PackageParser").getMethod("parsePackageLite", new Class[]{File.class, Integer.TYPE});
            } catch (Exception e2) {
                Log.e("GoogleBaseApp", "ParseApk exception!", e2);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: android.content.res.AssetManager} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: android.content.res.AssetManager} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: android.content.res.AssetManager} */
    /* JADX WARNING: type inference failed for: r2v3 */
    /* JADX WARNING: type inference failed for: r2v4, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r2v5 */
    /* JADX WARNING: type inference failed for: r2v7 */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String a(android.content.Context r9, android.content.pm.ApplicationInfo r10, java.io.File r11) {
        /*
            r8 = this;
            java.lang.String r0 = "GoogleBaseApp"
            java.lang.String r11 = r11.getAbsolutePath()
            android.content.res.Resources r1 = r9.getResources()
            r2 = 0
            android.content.res.AssetManager r9 = r9.getAssets()     // Catch:{ Exception -> 0x0055 }
            java.lang.String r3 = "addAssetPath"
            r4 = 1
            java.lang.Class[] r5 = new java.lang.Class[r4]     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            java.lang.Class<java.lang.String> r6 = java.lang.String.class
            r7 = 0
            r5[r7] = r6     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            r4[r7] = r11     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            b.b.o.g.d.a((java.lang.String) r0, (java.lang.Object) r9, (java.lang.String) r3, (java.lang.Class<?>[]) r5, (java.lang.Object[]) r4)     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            android.content.res.Resources r11 = new android.content.res.Resources     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            android.util.DisplayMetrics r3 = r1.getDisplayMetrics()     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            android.content.res.Configuration r1 = r1.getConfiguration()     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            r11.<init>(r9, r3, r1)     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            int r1 = r10.labelRes     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            if (r1 == 0) goto L_0x0037
            int r1 = r10.labelRes     // Catch:{ NotFoundException -> 0x0037 }
            java.lang.CharSequence r2 = r11.getText(r1)     // Catch:{ NotFoundException -> 0x0037 }
        L_0x0037:
            if (r2 != 0) goto L_0x0043
            java.lang.CharSequence r11 = r10.nonLocalizedLabel     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            if (r11 == 0) goto L_0x0040
            java.lang.CharSequence r10 = r10.nonLocalizedLabel     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            goto L_0x0042
        L_0x0040:
            java.lang.String r10 = r10.packageName     // Catch:{ Exception -> 0x004f, all -> 0x004d }
        L_0x0042:
            r2 = r10
        L_0x0043:
            java.lang.String r10 = r2.toString()     // Catch:{ Exception -> 0x004f, all -> 0x004d }
            if (r9 == 0) goto L_0x004c
            r9.close()
        L_0x004c:
            return r10
        L_0x004d:
            r10 = move-exception
            goto L_0x0063
        L_0x004f:
            r10 = move-exception
            r2 = r9
            goto L_0x0056
        L_0x0052:
            r10 = move-exception
            r9 = r2
            goto L_0x0063
        L_0x0055:
            r10 = move-exception
        L_0x0056:
            java.lang.String r9 = "getAppNameInternal Error!"
            android.util.Log.e(r0, r9, r10)     // Catch:{ all -> 0x0052 }
            if (r2 == 0) goto L_0x0060
            r2.close()
        L_0x0060:
            java.lang.String r9 = "Google Application"
            return r9
        L_0x0063:
            if (r9 == 0) goto L_0x0068
            r9.close()
        L_0x0068:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.googlebase.b.a(android.content.Context, android.content.pm.ApplicationInfo, java.io.File):java.lang.String");
    }

    public static boolean a(String str) {
        if (str != null) {
            for (String equals : f5438a) {
                if (str.equals(equals)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean i() {
        Log.d("GoogleBaseApp", "pre-install app: parseapk1");
        if (this.f5441d == null) {
            return false;
        }
        Log.d("GoogleBaseApp", "pre-install app: parseapk2");
        if (this.e == null && f5439b != null) {
            Object obj = null;
            try {
                obj = Build.VERSION.SDK_INT <= 19 ? f5439b.invoke((Object) null, new Object[]{this.f5441d, 0}) : f5439b.invoke((Object) null, new Object[]{new File(this.f5441d), 0});
            } catch (Exception e2) {
                Log.w("GoogleBaseApp", "Failed to parse package at " + this.f5441d + ": " + e2);
            }
            this.e = obj != null ? (String) d.a("GoogleBaseApp", obj, "packageName") : this.f5441d;
        }
        Log.d("GoogleBaseApp", "actual packageName is " + this.e);
        return this.e != null;
    }

    public boolean a() {
        List<Object> cloudDataList = MiuiSettingsCompat.getCloudDataList(this.f.getContentResolver(), "gms_install");
        if (!(cloudDataList == null || cloudDataList.size() == 0)) {
            try {
                for (Object obj : cloudDataList) {
                    String obj2 = obj.toString();
                    if (!TextUtils.isEmpty(obj2)) {
                        JSONArray optJSONArray = new JSONObject(obj2).optJSONArray("black");
                        for (int i = 0; i < optJSONArray.length(); i++) {
                            Log.i("GoogleBaseApp", optJSONArray.getString(i));
                            if (!TextUtils.isEmpty(optJSONArray.getString(i)) && optJSONArray.getString(i).equals(Build.DEVICE)) {
                                return true;
                            }
                        }
                        continue;
                    }
                }
            } catch (Exception e2) {
                Log.e("GoogleBaseApp", "checkblacklist exception!", e2);
            }
        }
        return false;
    }

    public String b() {
        return this.f5441d;
    }

    public String c() {
        ApplicationInfo h = h();
        File file = new File(b());
        if (h != null) {
            return a(this.f, h, file);
        }
        return null;
    }

    public String d() {
        if (this.e == null) {
            i();
        }
        return this.e;
    }

    public boolean e() {
        for (int length = f5438a.length - 1; length >= 0; length--) {
            Log.d("GoogleBaseApp", "pre-install app: " + this.e);
            if (!com.miui.googlebase.b.d.a(this.f, f5438a[length])) {
                return false;
            }
        }
        return true;
    }

    public boolean f() {
        i();
        String str = this.e;
        if (str != null) {
            return str.equals("com.android.vending");
        }
        return false;
    }

    public boolean g() {
        Log.d("GoogleBaseApp", "pre-install app: isinstalled");
        return com.miui.googlebase.b.d.a(this.f, "com.android.vending");
    }

    public ApplicationInfo h() {
        PackageInfo packageArchiveInfo;
        if (this.f5441d == null || (packageArchiveInfo = this.f.getPackageManager().getPackageArchiveInfo(this.f5441d, 0)) == null) {
            return null;
        }
        return packageArchiveInfo.applicationInfo;
    }
}
