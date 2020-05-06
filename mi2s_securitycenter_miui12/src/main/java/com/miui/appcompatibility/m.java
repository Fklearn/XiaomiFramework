package com.miui.appcompatibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import b.b.b.t;
import b.b.c.j.z;
import com.miui.common.persistence.b;
import com.miui.permcenter.compact.MiuiSettingsCompat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import miui.provider.ExtraSettings;
import miui.security.SecurityManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class m {

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        private boolean f3095a = true;

        /* renamed from: b  reason: collision with root package name */
        private HashMap<String, Integer> f3096b = new HashMap<>();

        public a() {
            this.f3096b.put("com.google.android.packageinstaller", 2);
            this.f3096b.put("com.android.vending", 1);
        }

        public void a(String str) {
            this.f3096b.remove(str);
        }

        public void a(String str, int i) {
            this.f3096b.put(str, Integer.valueOf(i));
        }

        public void a(boolean z) {
            this.f3095a = z;
        }

        public boolean a() {
            return this.f3095a;
        }

        public boolean a(Context context, String str, boolean z) {
            Integer num;
            if (("com.android.vending".equals(str) && !ExtraSettings.Secure.getBoolean(context.getContentResolver(), "scan_virus_from_play_store", true)) || (num = this.f3096b.get(str)) == null) {
                return false;
            }
            return !z || num.intValue() == 2;
        }
    }

    private static void a(Context context, String str, String str2) {
        Log.e("GlobalInstallerScanUtil", "startGlobalPackageInstaller");
        try {
            IBinder topActivity = ((SecurityManager) context.getSystemService("security")).getTopActivity();
            Intent intent = new Intent("com.miui.intent.action.global.PACKAGE_ADDED");
            intent.putExtra("pkgname", str2);
            intent.putExtra("installerPkgName", str);
            intent.setPackage("com.miui.global.packageinstaller");
            intent.setFlags(402653184);
            context.startActivity(intent);
            c(topActivity);
        } catch (Exception e) {
            Log.e("GlobalInstallerScanUtil", e.toString());
        }
    }

    public static void a(Context context, String str, boolean z) {
        List<String> b2 = i.b(context);
        if (b2 == null || !b2.contains(str)) {
            new l(context, str, z).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    protected static void a(Context context, String str, boolean z, a aVar) {
        String str2;
        try {
            str2 = context.getPackageManager().getInstallerPackageName(str);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            str2 = null;
        }
        if (!TextUtils.isEmpty(str2)) {
            if ("com.android.vending".equals(str2)) {
                long a2 = b.a("google_protect_start", 0);
                Log.d("GlobalInstallerScanUtil", "first google time:" + a2);
                if (a2 == 0 && Calendar.getInstance().get(1) > 2014) {
                    b.b("google_protect_start", System.currentTimeMillis());
                    return;
                } else if (z.a(a2) < 1) {
                    Log.d("GlobalInstallerScanUtil", "during google protect time!");
                    return;
                }
            }
            if (aVar == null || !aVar.a() || !aVar.a(context, str2, z)) {
                t.b(context, str);
            } else {
                a(context, str2, str);
            }
        }
    }

    /* access modifiers changed from: private */
    public static a b(Context context) {
        a aVar = new a();
        ContentResolver contentResolver = context.getContentResolver();
        try {
            aVar.a(MiuiSettingsCompat.getCloudDataBoolean(contentResolver, "app_compatibility", "globalinstallscan", true));
            String cloudDataString = MiuiSettingsCompat.getCloudDataString(contentResolver, "app_compatibility", "global_install_scan_package_list", "");
            if (!TextUtils.isEmpty(cloudDataString)) {
                JSONArray jSONArray = new JSONArray(cloudDataString);
                int length = jSONArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    String optString = jSONObject.optString("packageName", "");
                    boolean optBoolean = jSONObject.optBoolean("enable", false);
                    boolean optBoolean2 = jSONObject.optBoolean("replace", false);
                    if (!TextUtils.isEmpty(optString)) {
                        if (optBoolean) {
                            aVar.a(optString, optBoolean2 ? 2 : 1);
                        } else {
                            aVar.a(optString);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aVar;
    }

    /* access modifiers changed from: private */
    public static void b(IBinder iBinder) {
        if (iBinder != null) {
            try {
                b.b.o.f.a.a.a().a(iBinder, 0, (Intent) null);
            } catch (Exception e) {
                Log.e("GlobalInstallerScanUtil", e.toString());
            }
        }
    }

    private static void c(IBinder iBinder) {
        new Handler().postDelayed(new k(iBinder), 800);
    }
}
