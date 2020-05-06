package b.b.b.d;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import b.b.b.p;
import b.b.c.h.f;
import b.b.c.j.x;
import b.b.c.j.y;
import b.b.o.g.d;
import com.miui.antivirus.model.a;
import com.miui.antivirus.model.e;
import com.miui.antivirus.service.GuardService;
import com.miui.common.persistence.b;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.permcenter.compact.MiuiSettingsCompat;
import com.xiaomi.stat.MiStat;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1537a = (Build.IS_INTERNATIONAL_BUILD ? "safepay_target_package_in" : "safepay_target_package");

    /* renamed from: b  reason: collision with root package name */
    private static final String f1538b = (Build.IS_INTERNATIONAL_BUILD ? "safepay_target_activity_in" : "safepay_target_activity");

    /* renamed from: c  reason: collision with root package name */
    private static final String f1539c = (Build.IS_INTERNATIONAL_BUILD ? "safepay_imm_whitelist_in" : "safepay_imm_whitelist");

    /* renamed from: d  reason: collision with root package name */
    private static ArrayList<String> f1540d;
    private static ArrayList<String> e;
    private static ArrayList<String> f;

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0048 A[Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }, LOOP:0: B:19:0x0042->B:21:0x0048, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized android.os.Bundle a(android.content.Context r4, java.lang.String r5, android.os.Bundle r6) {
        /*
            java.lang.Class<b.b.b.d.n> r0 = b.b.b.d.n.class
            monitor-enter(r0)
            if (r6 != 0) goto L_0x000a
            android.os.Bundle r6 = new android.os.Bundle     // Catch:{ all -> 0x0082 }
            r6.<init>()     // Catch:{ all -> 0x0082 }
        L_0x000a:
            n(r4)     // Catch:{ all -> 0x0082 }
            r1 = 0
            java.lang.String r2 = "file_target_pkg"
            boolean r2 = r2.equals(r5)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            if (r2 == 0) goto L_0x001d
            java.lang.String r5 = f1537a     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
        L_0x0018:
            java.io.FileInputStream r1 = r4.openFileInput(r5)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            goto L_0x0033
        L_0x001d:
            java.lang.String r2 = "file_target_activity"
            boolean r2 = r2.equals(r5)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            if (r2 == 0) goto L_0x0028
            java.lang.String r5 = f1538b     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            goto L_0x0018
        L_0x0028:
            java.lang.String r2 = "file_imm_whitelist"
            boolean r5 = r2.equals(r5)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            if (r5 == 0) goto L_0x0033
            java.lang.String r5 = f1539c     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            goto L_0x0018
        L_0x0033:
            org.json.JSONArray r4 = new org.json.JSONArray     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            java.lang.String r5 = miui.util.IOUtils.toString(r1)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            r4.<init>(r5)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            r5.<init>()     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            r2 = 0
        L_0x0042:
            int r3 = r4.length()     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            if (r2 >= r3) goto L_0x0056
            java.lang.Object r3 = r4.get(r2)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            java.lang.String r3 = r3.toString()     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            r5.add(r3)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
            int r2 = r2 + 1
            goto L_0x0042
        L_0x0056:
            java.lang.String r4 = "file_string_result"
            r6.putStringArrayList(r4, r5)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x006a, JSONException -> 0x0061 }
        L_0x005b:
            miui.util.IOUtils.closeQuietly(r1)     // Catch:{ all -> 0x0082 }
            goto L_0x007c
        L_0x005f:
            r4 = move-exception
            goto L_0x007e
        L_0x0061:
            r4 = move-exception
            java.lang.String r5 = "SafePay-Utils"
            java.lang.String r2 = "JSONException when openContextFile : "
            android.util.Log.e(r5, r2, r4)     // Catch:{ all -> 0x005f }
            goto L_0x005b
        L_0x006a:
            r4 = move-exception
            java.lang.String r5 = "SafePay-Utils"
            java.lang.String r2 = "IOException when openContextFile : "
            android.util.Log.e(r5, r2, r4)     // Catch:{ all -> 0x005f }
            goto L_0x005b
        L_0x0073:
            r4 = move-exception
            java.lang.String r5 = "SafePay-Utils"
            java.lang.String r2 = "FileNotFoundException when openContextFile : "
            android.util.Log.e(r5, r2, r4)     // Catch:{ all -> 0x005f }
            goto L_0x005b
        L_0x007c:
            monitor-exit(r0)
            return r6
        L_0x007e:
            miui.util.IOUtils.closeQuietly(r1)     // Catch:{ all -> 0x0082 }
            throw r4     // Catch:{ all -> 0x0082 }
        L_0x0082:
            r4 = move-exception
            monitor-exit(r0)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.b.d.n.a(android.content.Context, java.lang.String, android.os.Bundle):android.os.Bundle");
    }

    public static String a(Context context, String str) {
        HashMap<String, List<String>> j = j(context);
        for (String next : j.keySet()) {
            if (j.get(next).contains(str)) {
                return next;
            }
        }
        return null;
    }

    public static String a(String str) {
        String str2 = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            try {
                str2 = ExtraTextUtils.toHexReadable(DigestUtils.get(fileInputStream, "MD5"));
            } catch (IOException e2) {
                Log.e("SafePay-Utils", "exception when get File Md5 ", e2);
            } catch (Throwable th) {
                IOUtils.closeQuietly(fileInputStream);
                throw th;
            }
            IOUtils.closeQuietly(fileInputStream);
            return str2;
        } catch (FileNotFoundException e3) {
            Log.e("SafePay-Utils", e3.toString());
            return null;
        }
    }

    public static ArrayList<String> a() {
        String a2 = b.a("key_scan_white_list", "");
        ArrayList<String> arrayList = new ArrayList<>();
        if (TextUtils.isEmpty(a2)) {
            return arrayList;
        }
        try {
            JSONArray jSONArray = new JSONArray(a2);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.getString(i));
            }
        } catch (Exception e2) {
            Log.e("SafePay-Utils", "Exception when getScanWhiteList! ", e2);
        }
        return arrayList;
    }

    public static ArrayList<String> a(Context context) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Iterator<String> it = b(context, "file_imm_whitelist").iterator();
            while (it.hasNext()) {
                arrayList.add(it.next());
            }
        } catch (Exception e2) {
            Log.e("SafePay-Utils", "Exception when getImmException !", e2);
        }
        return arrayList;
    }

    public static void a(Context context, int i, String str, Bundle bundle) {
        d.a("SafePay-Utils", context.getSystemService("statusbar"), "setStatus", (Class<?>[]) new Class[]{Integer.TYPE, String.class, Bundle.class}, Integer.valueOf(i), str, bundle);
    }

    private static Bundle b(Context context, String str, Bundle bundle) {
        return context.getContentResolver().call(Uri.parse("content://com.miui.securitycenter.provider"), "readContextFile", str, bundle);
    }

    private static ArrayList<String> b(Context context, String str) {
        if ("file_target_pkg".equals(str)) {
            if (f1540d == null) {
                f1540d = b(context, str, new Bundle()).getStringArrayList("file_string_result");
            }
            return f1540d;
        } else if ("file_target_activity".equals(str)) {
            if (e == null) {
                e = b(context, str, new Bundle()).getStringArrayList("file_string_result");
            }
            return e;
        } else if (!"file_imm_whitelist".equals(str)) {
            return new ArrayList<>();
        } else {
            if (f == null) {
                f = b(context, str, new Bundle()).getStringArrayList("file_string_result");
            }
            return f;
        }
    }

    public static List<a> b(Context context) {
        ArrayList arrayList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();
        Iterator it = ((ArrayList) b.b.c.b.b.a(context).a()).iterator();
        while (it.hasNext()) {
            PackageInfo packageInfo = (PackageInfo) it.next();
            e eVar = new e();
            eVar.f(packageInfo.packageName);
            eVar.b(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            arrayList.add(eVar);
        }
        return arrayList;
    }

    public static boolean b() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        return ("hi".equals(language) || "en".equals(language)) && "IN".equals(locale.getCountry());
    }

    public static ArrayList<String> c(Context context) {
        ArrayList<String> b2 = p.b(context);
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator it = ((ArrayList) b.b.c.b.b.a(context).a()).iterator();
        while (it.hasNext()) {
            PackageInfo packageInfo = (PackageInfo) it.next();
            if (b2.contains(packageInfo.packageName)) {
                arrayList.add(packageInfo.packageName);
            }
        }
        return arrayList;
    }

    private static void c(Context context, String str) {
        InputStream inputStream;
        PrintWriter printWriter = null;
        try {
            String cloudDataString = MiuiSettingsCompat.getCloudDataString(context.getContentResolver(), str, MiStat.Param.CONTENT, (String) null);
            if (TextUtils.isEmpty(cloudDataString)) {
                InputStream open = context.getAssets().open(str);
                try {
                    inputStream = open;
                    cloudDataString = IOUtils.toString(open);
                } catch (Throwable th) {
                    th = th;
                    inputStream = open;
                    IOUtils.closeQuietly(printWriter);
                    IOUtils.closeQuietly(inputStream);
                    throw th;
                }
            } else {
                inputStream = null;
            }
            try {
                PrintWriter printWriter2 = new PrintWriter(context.openFileOutput(str, 0));
                try {
                    printWriter2.write(cloudDataString);
                    IOUtils.closeQuietly(printWriter2);
                    IOUtils.closeQuietly(inputStream);
                } catch (Throwable th2) {
                    th = th2;
                    printWriter = printWriter2;
                    IOUtils.closeQuietly(printWriter);
                    IOUtils.closeQuietly(inputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                IOUtils.closeQuietly(printWriter);
                IOUtils.closeQuietly(inputStream);
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            inputStream = null;
            IOUtils.closeQuietly(printWriter);
            IOUtils.closeQuietly(inputStream);
            throw th;
        }
    }

    public static boolean c() {
        return b.b.c.j.e.b() >= 10;
    }

    public static int d(Context context) {
        ArrayList<String> b2 = p.b(context);
        Iterator it = ((ArrayList) b.b.c.b.b.a(context).a()).iterator();
        int i = 0;
        while (it.hasNext()) {
            if (b2.contains(((PackageInfo) it.next()).packageName)) {
                i++;
            }
        }
        return i;
    }

    public static boolean d() {
        return y.a("ro.miui.google.csp", false);
    }

    public static ArrayList<String> e(Context context) {
        String str;
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Iterator<String> it = b(context, "file_target_activity").iterator();
            while (it.hasNext()) {
                JSONObject jSONObject = new JSONObject(it.next());
                if (f(context).contains(jSONObject.getString("package"))) {
                    JSONArray jSONArray = jSONObject.getJSONArray("activity");
                    for (int i = 0; i < jSONArray.length(); i++) {
                        arrayList.add(jSONArray.getString(i));
                    }
                }
            }
        } catch (JSONException e2) {
            e = e2;
            str = "JSONException when get DefaultActivity !";
            Log.e("SafePay-Utils", str, e);
            return arrayList;
        } catch (Exception e3) {
            e = e3;
            str = "Exception  when get DefaultActivity !";
            Log.e("SafePay-Utils", str, e);
            return arrayList;
        }
        return arrayList;
    }

    public static ArrayList<String> f(Context context) {
        String str;
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Iterator<String> it = b(context, "file_target_pkg").iterator();
            while (it.hasNext()) {
                JSONObject jSONObject = new JSONObject(it.next());
                if (jSONObject.getInt(AdvancedSlider.STATE) == 1) {
                    arrayList.add(jSONObject.getString("package"));
                }
            }
        } catch (JSONException e2) {
            e = e2;
            str = "JSONException when get DefaultPkgs !";
            Log.e("SafePay-Utils", str, e);
            return arrayList;
        } catch (Exception e3) {
            e = e3;
            str = "Exception when get DefaultPkgs !";
            Log.e("SafePay-Utils", str, e);
            return arrayList;
        }
        return arrayList;
    }

    public static ArrayList<String> g(Context context) {
        String str;
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Iterator<String> it = b(context, "file_target_activity").iterator();
            while (it.hasNext()) {
                JSONObject jSONObject = new JSONObject(it.next());
                if (jSONObject.getInt("type") == 0) {
                    arrayList.add(jSONObject.getString("package"));
                }
            }
        } catch (JSONException e2) {
            e = e2;
            str = "JSONException when get optional pkgs !";
            Log.e("SafePay-Utils", str, e);
            return arrayList;
        } catch (Exception e3) {
            e = e3;
            str = "Exception  when get optional pkgs !";
            Log.e("SafePay-Utils", str, e);
            return arrayList;
        }
        return arrayList;
    }

    public static ArrayList<String> h(Context context) {
        String str;
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Iterator<String> it = b(context, "file_target_pkg").iterator();
            while (it.hasNext()) {
                arrayList.add(new JSONObject(it.next()).getString("package"));
            }
        } catch (JSONException e2) {
            e = e2;
            str = "JSONException when get TargetPkgs !";
            Log.e("SafePay-Utils", str, e);
            return arrayList;
        } catch (Exception e3) {
            e = e3;
            str = "Exception  when get TargetPkgs !";
            Log.e("SafePay-Utils", str, e);
            return arrayList;
        }
        return arrayList;
    }

    public static int i(Context context) {
        Iterator<String> it = p.g().iterator();
        int i = 0;
        while (it.hasNext()) {
            if (x.h(context, it.next())) {
                i++;
            }
        }
        return i;
    }

    public static HashMap<String, List<String>> j(Context context) {
        String str;
        HashMap<String, List<String>> hashMap = new HashMap<>();
        try {
            Iterator<String> it = b(context, "file_target_activity").iterator();
            while (it.hasNext()) {
                JSONObject jSONObject = new JSONObject(it.next());
                JSONArray jSONArray = jSONObject.getJSONArray("activity");
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.getString(i));
                }
                hashMap.put(jSONObject.getString("package"), arrayList);
            }
        } catch (JSONException e2) {
            e = e2;
            str = "JSONException when get TargetActivityMap !";
            Log.e("SafePay-Utils", str, e);
            return hashMap;
        } catch (Exception e3) {
            e = e3;
            str = "Exception when get TargetActivityMap !";
            Log.e("SafePay-Utils", str, e);
            return hashMap;
        }
        return hashMap;
    }

    public static boolean k(Context context) {
        Point point = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealSize(point);
        return point.y <= 1920;
    }

    public static boolean l(Context context) {
        return Build.VERSION.SDK_INT >= 24 && !o(context);
    }

    public static void m(Context context) {
        if (f.l(context)) {
            Intent intent = new Intent(context, GuardService.class);
            intent.setAction("action_start_wifi_scan_task");
            context.startService(intent);
        }
    }

    private static void n(Context context) {
        if (System.currentTimeMillis() - p.c() > 86400000 || System.currentTimeMillis() < p.c()) {
            try {
                c(context, f1537a);
                c(context, f1538b);
                c(context, f1539c);
                p.a(System.currentTimeMillis());
            } catch (Exception e2) {
                Log.i("SafePay-Utils", "exception when update cloud data : ", e2);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
        r3 = (android.os.UserManager) r3.getSystemService("user");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean o(android.content.Context r3) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 24
            if (r0 < r2) goto L_0x0018
            java.lang.String r0 = "user"
            java.lang.Object r3 = r3.getSystemService(r0)
            android.os.UserManager r3 = (android.os.UserManager) r3
            if (r3 == 0) goto L_0x0018
            boolean r3 = r3.isUserUnlocked()
            if (r3 == 0) goto L_0x0018
            r1 = 1
        L_0x0018:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.b.d.n.o(android.content.Context):boolean");
    }
}
