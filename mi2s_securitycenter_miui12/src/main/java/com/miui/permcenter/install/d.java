package com.miui.permcenter.install;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.v;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.activityutil.o;
import com.miui.common.persistence.b;
import com.miui.permcenter.compact.AppOpsUtilsCompat;
import com.miui.permcenter.compact.MiuiNotificationCompat;
import com.miui.permcenter.compact.SystemPropertiesCompat;
import com.miui.securitycenter.R;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.util.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static d f6143a;

    /* renamed from: b  reason: collision with root package name */
    private Context f6144b;

    /* renamed from: c  reason: collision with root package name */
    private HashMap<String, String> f6145c = new HashMap<>();

    /* renamed from: d  reason: collision with root package name */
    private Handler f6146d = new c(this, Looper.getMainLooper());

    private d(Context context) {
        this.f6144b = context;
        this.f6145c.put("com.android.mmitest", "3082034b30820233a00302010202045e8fee07300d06092a864886f70d01010b05003055310b300906035504061302636e310b300906035504081302677a310b300906035504071302737a310c300a060355040a1303697774310c300a060355040b13036977743110300e0603550403130779616f66616e673020170d3136303430383038333633325a180f32303731303131303038333633325a3055310b300906035504061302636e310b300906035504081302677a310b300906035504071302737a310c300a060355040a1303697774310c300a060355040b13036977743110300e0603550403130779616f66616e6730820122300d06092a864886f70d01010105000382010f003082010a0282010100ad3fe15f61a1de4136745c45c8e4e26c45d5a92b7c2d564e25fc0d40c596030bf89c6ef626d0b38b550580d2cfec2ad3c3b316eae25f5169d3f1b9d3d12b7de47b08e927b5d471152e04ceb91f0438baecd80c02b39b00924aa90d80f6494fb444629e4de9b967af0c8bdd8e40b33c2d193eaddf44ce75554839f7394fccb77ab11b3244a24cde33e78f6d0addc81c192f2c7ae2c98033a3af821aff2a58677afbf2461c3442be0b3c210150a908c2e9ce72f7e88b09108c912472cea8b4e9bba0d76aee0b51619c484d16879b3e33ea1db03ba73a76edde8c795541db1567534863cb5e80376341d3a571e8c359cfdc2a2f32b23d9164ddec791fda9873b4b90203010001a321301f301d0603551d0e04160414e9f8c5713fd795eac08934f3bfd8f48ab8c701e6300d06092a864886f70d01010b050003820101005426a0e5629985dca9e32e105884fbe859fe4ea62ee4855c0681398a6d82876577ed17355ceea94bf0e9bf6e9afc0bf7f0f291b5a0ffa97353ade5a209c727eaa7d3775a72296dd78079f4026ce7d1ede3107c9eee79d7e9d32a908e889fecd45e4ded74666d8109f5ba23a987b60ffc66c841f45521d99d8c8c73036171e72e6637ed353d88a6f3522e476362a050bc0c6f12099b968577040ca49708153385731f3fe619b3436f3ccfcf7a5625afb6a57331191aa47fcb9c82ec166798b8b218a6dde056e60d2c24d09fff802cefd0a3d7a6353dc5d431b852fdc92a08a713be295b1ce2febecf10e5d1401f345e06939f0d594a81172a0478d4244af0b82f");
    }

    public static synchronized d a(Context context) {
        d dVar;
        synchronized (d.class) {
            if (f6143a == null) {
                f6143a = new d(context.getApplicationContext());
            }
            dVar = f6143a;
        }
        return dVar;
    }

    private h a(String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        try {
            return new h(str, new JSONObject(str2));
        } catch (JSONException e) {
            Log.e("AdbInstallManager", "parsePackageInfo", e);
            return null;
        }
    }

    private void a(String str, Drawable drawable) {
        Bitmap bitmap;
        File c2 = c(str);
        if ((drawable instanceof BitmapDrawable) && (bitmap = ((BitmapDrawable) drawable).getBitmap()) != null) {
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(c2);
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream2);
                    IOUtils.closeQuietly(fileOutputStream2);
                } catch (Exception e) {
                    e = e;
                    fileOutputStream = fileOutputStream2;
                    try {
                        Log.e("AdbInstallManager", "addIcon", e);
                        IOUtils.closeQuietly(fileOutputStream);
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(fileOutputStream);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    fileOutputStream = fileOutputStream2;
                    IOUtils.closeQuietly(fileOutputStream);
                    throw th;
                }
            } catch (Exception e2) {
                e = e2;
                Log.e("AdbInstallManager", "addIcon", e);
                IOUtils.closeQuietly(fileOutputStream);
            }
        }
    }

    private Notification.Builder h(String str) {
        return v.a(this.f6144b, "com.miui.securitycenter").setDefaults(32).setWhen(System.currentTimeMillis()).setAutoCancel(true).setSmallIcon(R.drawable.ic_license_manage_small_icon).setContentIntent(PendingIntent.getActivity(this.f6144b, 0, new Intent(this.f6144b, PackageManagerActivity.class), 0)).setDefaults(1).setContentTitle(this.f6144b.getString(R.string.app_install_intercept)).setContentText(this.f6144b.getString(R.string.pc_to_phone_install, new Object[]{str})).setPriority(1).setSound(Uri.EMPTY, (AudioAttributes) null);
    }

    public static boolean h() {
        return l() || AppOpsUtilsCompat.isXOptMode();
    }

    private void i(String str) {
        c(str).delete();
    }

    /* access modifiers changed from: private */
    public void j() {
        ((NotificationManager) this.f6144b.getSystemService("notification")).cancel(100);
    }

    private SharedPreferences k() {
        return this.f6144b.getSharedPreferences("adb_install_packages", 0);
    }

    private static boolean l() {
        return SystemPropertiesCompat.getInt("ro.debuggable", 0) == 1;
    }

    public void a() {
        b.b("permcenter_install_reject_count", -1);
    }

    public void a(h hVar) {
        int a2 = b.a("permcenter_install_reject_count", 0);
        b.b("permcenter_install_last_name", hVar.b());
        b.b("permcenter_install_reject_count", a2 + 1);
    }

    public void a(h hVar, Drawable drawable) {
        SharedPreferences k = k();
        String d2 = hVar.d();
        String c2 = hVar.c();
        k.edit().putString(c2, d2).commit();
        if (drawable != null) {
            a(c2, drawable);
        }
    }

    public void a(String str) {
        if (!b.a("perm_adb_install_notify", false)) {
            b.b("perm_adb_install_notify", true);
            Notification build = v.a(this.f6144b, "com.miui.securitycenter").setDefaults(32).setWhen(System.currentTimeMillis()).setAutoCancel(true).setSmallIcon(R.drawable.ic_license_manage_small_icon).setDefaults(1).setPriority(1).setContentTitle(this.f6144b.getString(R.string.adb_install_reject_notiy, new Object[]{str})).setContentText(this.f6144b.getString(R.string.adb_install_reject_notiy_desc)).setSound(Uri.EMPTY, (AudioAttributes) null).build();
            MiuiNotificationCompat.setEnableFloat(true);
            MiuiNotificationCompat.setFloatTime(5000);
            NotificationManager notificationManager = (NotificationManager) this.f6144b.getSystemService("notification");
            notificationManager.cancel(100);
            v.a(notificationManager, "com.miui.securitycenter", this.f6144b.getResources().getString(R.string.notify_channel_name_security), 5);
            notificationManager.notify(100, build);
            this.f6146d.sendEmptyMessageDelayed(10, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }
    }

    public void a(String str, int i) {
        h d2 = d(str);
        if (d2 != null) {
            d2.a(i);
            a(d2, (Drawable) null);
        }
    }

    public void a(boolean z) {
        b.b("security_adb_install_enable", z);
        SystemPropertiesCompat.set("persist.security.adbinstall", z ? o.f2310b : o.f2309a);
    }

    public boolean a(PackageInfo packageInfo) {
        String str = this.f6145c.get(packageInfo.packageName);
        Signature[] signatureArr = packageInfo.signatures;
        return str != null && signatureArr != null && signatureArr.length > 0 && str.equals(signatureArr[0].toCharsString());
    }

    public List<h> b() {
        h a2;
        Map<String, ?> all = k().getAll();
        ArrayList arrayList = new ArrayList();
        for (Map.Entry next : all.entrySet()) {
            String str = (String) next.getKey();
            if (!(str == null || (a2 = a(str, (String) next.getValue())) == null)) {
                arrayList.add(a2);
            }
        }
        return arrayList;
    }

    public void b(boolean z) {
        b.b("permcenter_install_intercept_enabled", z);
    }

    public boolean b(String str) {
        return this.f6145c.containsKey(str);
    }

    public int c() {
        return b.a("permcenter_install_reject_count", -1);
    }

    public File c(String str) {
        File file = new File(this.f6144b.getFilesDir(), "installIcon");
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, str);
    }

    public void c(boolean z) {
        b.b("perm_install_debug_package", z);
    }

    public h d(String str) {
        return a(str, k().getString(str, (String) null));
    }

    public String d() {
        return b.a("permcenter_install_last_name", "");
    }

    public void e(String str) {
        k().edit().remove(str).commit();
        i(str);
    }

    public boolean e() {
        if (SystemPropertiesCompat.getInt("ro.debuggable", 0) == 1 && (SystemPropertiesCompat.getInt("ro.secureboot.devicelock", 0) == 0 || "unlocked".equals(SystemPropertiesCompat.getString("ro.secureboot.lockstate", "")))) {
            return true;
        }
        return b.a("security_adb_install_enable", (Build.VERSION.SDK_INT < 26 || (!AppOpsUtilsCompat.isXOptMode() && !miui.os.Build.IS_INTERNATIONAL_BUILD)) ? false : SystemPropertiesCompat.getBoolean("persist.security.adbinstall", false), 0);
    }

    public void f(String str) {
        Notification build = h(str).build();
        NotificationManager notificationManager = (NotificationManager) this.f6144b.getSystemService("notification");
        notificationManager.cancel(100);
        v.a(notificationManager, "com.miui.securitycenter", this.f6144b.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.notify(100, build);
        this.f6146d.removeMessages(10);
    }

    public boolean f() {
        return b.a("permcenter_install_intercept_enabled", !h());
    }

    public void g(String str) {
        Notification build = h(str).build();
        MiuiNotificationCompat.setEnableFloat(true);
        MiuiNotificationCompat.setFloatTime(5000);
        NotificationManager notificationManager = (NotificationManager) this.f6144b.getSystemService("notification");
        notificationManager.cancel(100);
        v.a(notificationManager, "com.miui.securitycenter", this.f6144b.getResources().getString(R.string.notify_channel_name_security), 5);
        notificationManager.notify(100, build);
        this.f6146d.removeMessages(10);
        this.f6146d.sendEmptyMessageDelayed(10, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public boolean g() {
        return b.a("perm_install_debug_package", false, 0);
    }

    public void i() {
        LocalBroadcastManager.getInstance(this.f6144b).sendBroadcast(new Intent("com.miui.permcenter.install.action_data_change"));
    }
}
