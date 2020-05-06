package com.miui.gamebooster.globalgame.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.o.g.e;
import com.miui.earthquakewarning.Constants;
import com.miui.gamebooster.m.W;
import com.miui.gamebooster.model.f;
import com.miui.gamebooster.viewPointwidget.b;
import com.miui.gamebooster.viewPointwidget.c;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.networkassistant.utils.TypefaceHelper;
import com.miui.securityscan.c.a;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utils {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f4413a = a.f7625a;

    /* renamed from: b  reason: collision with root package name */
    private static final long f4414b = TimeUnit.DAYS.toMillis(7);

    /* renamed from: c  reason: collision with root package name */
    private static Boolean f4415c = null;

    /* renamed from: d  reason: collision with root package name */
    private static Boolean f4416d = null;
    private static final long e = TimeUnit.DAYS.toMillis(1);
    public static final boolean f = (Build.VERSION.SDK_INT >= 21);
    private static String g;

    public static class Network {

        public @interface Type {
            public static final int MOBILE = 2;
            public static final int MOBILE2G = 3;
            public static final int MOBILE3G = 4;
            public static final int MOBILE4G = 5;
            public static final int UNKNOWN = 0;
            public static final int WIFI = 1;
        }

        public static NetworkInfo a(Context context) {
            try {
                return ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            } catch (Exception unused) {
                return null;
            }
        }

        public static String a() {
            return b(a.a()) == 1 ? W.f4461a : "M";
        }

        @Type
        public static int b(Context context) {
            NetworkInfo a2 = a(context);
            if (a2 == null) {
                return 0;
            }
            try {
                int type = a2.getType();
                if (type != 0) {
                    return type != 1 ? 0 : 1;
                }
                return 2;
            } catch (Exception unused) {
                return 0;
            }
        }
    }

    public static float a(String str) {
        return a.c().getFloat(str, 0.0f);
    }

    public static int a(View view) {
        if (view != null && (view.getParent() instanceof ViewGroup)) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (view.equals(viewGroup.getChildAt(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static String a() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    @SuppressLint({"PrivateApi"})
    private static String a(String str, String str2) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getMethod("get", new Class[]{String.class, String.class}).invoke(cls, new Object[]{str, str2});
        } catch (Exception unused) {
            return str2;
        }
    }

    private static void a(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        if (activityManager != null) {
            activityManager.killBackgroundProcesses(Constants.SECURITY_ADD_PACKAGE);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x000f */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(android.content.Context r4, java.lang.String r5) {
        /*
            java.lang.String r0 = "android.intent.action.VIEW"
            android.content.Intent r1 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x000f }
            android.net.Uri r2 = android.net.Uri.parse(r5)     // Catch:{ ActivityNotFoundException -> 0x000f }
            r1.<init>(r0, r2)     // Catch:{ ActivityNotFoundException -> 0x000f }
            r4.startActivity(r1)     // Catch:{ ActivityNotFoundException -> 0x000f }
            goto L_0x0046
        L_0x000f:
            java.lang.String r5 = e((java.lang.String) r5)     // Catch:{ ActivityNotFoundException -> 0x0031 }
            android.content.Intent r1 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x0031 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ ActivityNotFoundException -> 0x0031 }
            r2.<init>()     // Catch:{ ActivityNotFoundException -> 0x0031 }
            java.lang.String r3 = "market://details?id="
            r2.append(r3)     // Catch:{ ActivityNotFoundException -> 0x0031 }
            r2.append(r5)     // Catch:{ ActivityNotFoundException -> 0x0031 }
            java.lang.String r5 = r2.toString()     // Catch:{ ActivityNotFoundException -> 0x0031 }
            android.net.Uri r5 = android.net.Uri.parse(r5)     // Catch:{ ActivityNotFoundException -> 0x0031 }
            r1.<init>(r0, r5)     // Catch:{ ActivityNotFoundException -> 0x0031 }
            r4.startActivity(r1)     // Catch:{ ActivityNotFoundException -> 0x0031 }
            goto L_0x0046
        L_0x0031:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r0 = "fail::"
            r5.append(r0)
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            com.miui.gamebooster.globalgame.util.b.b(r4)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.globalgame.util.Utils.a(android.content.Context, java.lang.String):void");
    }

    public static void a(Context context, String str, String str2) {
        a(context);
        try {
            Intent intent = new Intent("miui.intent.action.AD_TRANS");
            intent.setData(Uri.parse("miadtrans://openweb?mifb=mi" + str + "&title=" + str2));
            intent.addFlags(67108864);
            intent.addFlags(32768);
            intent.addFlags(268435456);
            context.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Intent intent2 = new Intent("miui.intent.action.CLEAN_MASTER_SECURITY_WEB_VIEW");
            intent2.putExtra(MijiaAlertModel.KEY_URL, str);
            intent2.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, str2);
            intent2.addFlags(67108864);
            intent2.addFlags(32768);
            intent2.addFlags(268435456);
            context.startActivity(intent2);
        }
    }

    public static void a(Context context, View... viewArr) {
        if (context != null && viewArr != null && viewArr.length != 0) {
            for (TextView textView : viewArr) {
                if (textView instanceof TextView) {
                    textView.setTypeface(TypefaceHelper.getRobotoBoldCondensed(context));
                }
            }
        }
    }

    public static void a(View view, int i, int i2) {
        if (view != null && view.getLayoutParams() != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = i;
            layoutParams.height = i2;
        }
    }

    public static void a(View view, Runnable runnable) {
        a(view, runnable, false);
    }

    public static void a(View view, Runnable runnable, boolean z) {
        if (view != null && runnable != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new f(z, view, runnable));
        }
    }

    public static void a(@NonNull ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ImageView) {
                a((ImageView) childAt);
            } else if (childAt instanceof ViewGroup) {
                a((ViewGroup) childAt);
            }
        }
    }

    public static void a(@Nullable ViewStub viewStub, @Nullable c... cVarArr) {
        if (viewStub != null && cVarArr != null && cVarArr.length != 0) {
            for (c cVar : cVarArr) {
                if (cVar != null) {
                    cVar.a(viewStub);
                }
            }
        }
    }

    public static void a(@NonNull ImageView imageView) {
        imageView.setImageBitmap((Bitmap) null);
        imageView.setImageDrawable((Drawable) null);
    }

    public static void a(@Nullable Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void a(@Nullable Runnable runnable, View... viewArr) {
        e eVar = runnable == null ? null : new e(runnable);
        for (View view : viewArr) {
            if (view != null) {
                view.setOnClickListener(eVar);
            }
        }
    }

    public static void a(String str, float f2) {
        a.c().edit().putFloat(str, f2).apply();
    }

    public static void a(boolean z, View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.setVisibility(z ? 0 : 4);
            }
        }
    }

    public static void a(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.setVisibility(8);
            }
        }
    }

    public static void a(@Nullable b... bVarArr) {
        if (bVarArr != null && bVarArr.length != 0) {
            for (b bVar : bVarArr) {
                if (bVar != null) {
                    bVar.onPause();
                }
            }
        }
    }

    public static void a(@Nullable c... cVarArr) {
        if (cVarArr != null && cVarArr.length != 0) {
            for (c cVar : cVarArr) {
                if (cVar != null) {
                    cVar.onDestroy();
                }
            }
        }
    }

    public static void a(Object... objArr) {
        if (objArr != null && objArr.length != 0) {
            for (f fVar : objArr) {
                if (fVar instanceof f) {
                    fVar.a();
                }
            }
        }
    }

    public static <A extends Activity> boolean a(A a2) {
        return a2 == null || a2.isDestroyed() || a2.isFinishing();
    }

    public static <F extends Fragment> boolean a(F f2) {
        return f2 == null || f2.getActivity() == null || f2.getActivity().isFinishing();
    }

    public static <E> boolean a(Collection<E> collection) {
        return collection == null || collection.size() == 0;
    }

    public static <K, V> boolean a(Map<K, V> map) {
        return map == null || map.size() == 0;
    }

    public static String b() {
        return "com.miui.securitycenter";
    }

    public static void b(View view) {
        if (view != null && (view.getParent() instanceof ViewGroup)) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static void b(View view, Runnable runnable) {
        if (view != null && runnable != null) {
            if (view.getWidth() != 0) {
                runnable.run();
            } else {
                a(view, runnable);
            }
        }
    }

    public static void b(String str) {
        a.c().edit().putString("gbg_key_focused_game_pkg_name_0x0a", str).apply();
    }

    public static void b(boolean z, View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.setClickable(z);
            }
        }
    }

    public static void b(View... viewArr) {
        if (viewArr != null && viewArr.length != 0) {
            for (View view : viewArr) {
                if (view != null) {
                    view.setScaleX(-view.getScaleX());
                }
            }
        }
    }

    public static void b(@Nullable b... bVarArr) {
        if (bVarArr != null && bVarArr.length != 0) {
            for (b bVar : bVarArr) {
                if (bVar != null) {
                    bVar.a();
                }
            }
        }
    }

    public static <E> boolean b(Collection<E> collection) {
        return (collection == null || collection.size() == 0) ? false : true;
    }

    public static String c() {
        return f4413a ? "dev" : "stable";
    }

    public static void c(@Nullable Collection<b> collection) {
        if (!a(collection)) {
            for (b next : collection) {
                if (next != null) {
                    next.onPause();
                }
            }
        }
    }

    public static void c(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.setVisibility(0);
            }
        }
    }

    public static void c(@Nullable b... bVarArr) {
        if (bVarArr != null && bVarArr.length != 0) {
            for (b bVar : bVarArr) {
                if (bVar != null) {
                    bVar.b();
                }
            }
        }
    }

    public static boolean c(String str) {
        return !TextUtils.isEmpty(str) && (str.startsWith("https://play.google.com/store/apps/details?id=") || str.contains("market://details?id="));
    }

    public static String d() {
        return a.c().getString("gbg_key_focused_game_pkg_name_0x0a", "");
    }

    private static void d(String str) {
        if (!a.c().contains("gbg_key_user_uuid_0x01")) {
            a.c().edit().putString("gbg_key_user_uuid_0x01", str).apply();
        }
    }

    public static void d(@Nullable Collection<b> collection) {
        if (!a(collection)) {
            for (b next : collection) {
                if (next != null) {
                    next.a();
                }
            }
        }
    }

    public static void d(@Nullable b... bVarArr) {
        if (bVarArr != null && bVarArr.length != 0) {
            for (b bVar : bVarArr) {
                if (bVar != null) {
                    bVar.onStop();
                }
            }
        }
    }

    public static String e() {
        return Locale.getDefault().getLanguage();
    }

    private static String e(String str) {
        return (!str.contains("id=") || str.length() <= 3) ? "" : str.substring(str.indexOf("id=") + 3);
    }

    public static void e(@Nullable Collection<b> collection) {
        if (!a(collection)) {
            for (b next : collection) {
                if (next != null) {
                    next.b();
                }
            }
        }
    }

    public static String f() {
        String a2 = a("ro.miui.region", "");
        return TextUtils.isEmpty(a2) ? Locale.getDefault().getCountry() : a2;
    }

    public static void f(@Nullable Collection<b> collection) {
        if (!a(collection)) {
            for (b next : collection) {
                if (next != null) {
                    next.onStop();
                }
            }
        }
    }

    public static String g() {
        if (g == null) {
            g = a("ro.miui.ui.version.name", "");
        }
        String str = g;
        return str == null ? "" : str;
    }

    public static String h() {
        return Build.MODEL;
    }

    public static Locale i() {
        return Build.VERSION.SDK_INT >= 24 ? Resources.getSystem().getConfiguration().getLocales().get(0) : Resources.getSystem().getConfiguration().locale;
    }

    public static String j() {
        String str;
        String t = t();
        if (!TextUtils.isEmpty(t) || !t.isEmpty()) {
            return t;
        }
        if (Build.VERSION.SDK_INT < 28) {
            str = Build.SERIAL;
        } else {
            try {
                str = (String) e.a(Class.forName("android.os.Build"), String.class, "getSerial", (Class<?>[]) new Class[0], new Object[0]);
            } catch (Exception unused) {
                str = "";
            }
        }
        String uuid = new UUID((long) ("unique_gbg" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10)).hashCode(), (long) str.hashCode()).toString();
        d(uuid);
        b.a((Object) "UUID: $uuid");
        return uuid;
    }

    public static String k() {
        return "1.0";
    }

    public static String l() {
        return "1.0";
    }

    public static boolean m() {
        return System.currentTimeMillis() - a.c().getLong("gbg_key_game_feed_last_cached_time_0x09", 0) > e;
    }

    public static boolean n() {
        Boolean bool = f4415c;
        if (bool != null) {
            return bool.booleanValue();
        }
        f4415c = Boolean.valueOf(a.c().getBoolean("gbg_key_user_is_first_0x02", true));
        return f4415c.booleanValue();
    }

    public static boolean o() {
        Boolean bool = f4416d;
        if (bool != null) {
            return bool.booleanValue();
        }
        boolean z = true;
        if (a.c().contains("gbg_key_user_is_new_0x03")) {
            f4416d = Boolean.valueOf(a.c().getBoolean("gbg_key_user_is_new_0x03", true));
        } else {
            if (System.currentTimeMillis() - a.c().getLong("gbg_key_user_first_open_time_0x04", System.currentTimeMillis()) >= f4414b) {
                z = false;
            }
            f4416d = Boolean.valueOf(z);
            if (!f4416d.booleanValue()) {
                a.c().edit().putBoolean("gbg_key_user_is_new_0x03", false).apply();
            }
        }
        return f4416d.booleanValue();
    }

    public static void p() {
        if (!a.c().contains("gbg_key_user_is_first_0x02")) {
            a.c().edit().putBoolean("gbg_key_user_is_first_0x02", false).apply();
            a.c().edit().putLong("gbg_key_user_first_open_time_0x04", System.currentTimeMillis()).apply();
        }
    }

    public static void q() {
        f4415c = null;
        f4416d = null;
    }

    public static void r() {
        a.c().edit().remove("gbg_key_game_feed_last_cached_time_0x09").apply();
    }

    public static void s() {
        a.c().edit().putLong("gbg_key_game_feed_last_cached_time_0x09", System.currentTimeMillis()).apply();
    }

    private static String t() {
        return a.c().getString("gbg_key_user_uuid_0x01", "");
    }
}
