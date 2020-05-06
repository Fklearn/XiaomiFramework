package com.miui.gamebooster.m;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import b.b.c.d;
import b.b.c.h.j;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import com.miui.gamebooster.d.a;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.luckymoney.config.AppConstants;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.k;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: com.miui.gamebooster.m.y  reason: case insensitive filesystem */
public class C0393y {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4528a = (Build.IS_INTERNATIONAL_BUILD ? "https://adv.sec.intl.miui.com/game/fast_reply" : "https://adv.sec.miui.com/game/fast_reply");

    /* renamed from: b  reason: collision with root package name */
    private static final Object f4529b = new Object();

    /* renamed from: c  reason: collision with root package name */
    public static ArrayList<String> f4530c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    public static ArrayList<String> f4531d = new ArrayList<>();

    public static String a(String str) {
        if (str == null || !str.contains("/")) {
            return null;
        }
        return str.split("/")[0];
    }

    public static ArrayList<C0398d> a(Context context, PackageManager packageManager, List<ApplicationInfo> list) {
        ArrayList<C0398d> arrayList = new ArrayList<>();
        if (list.size() != 0 && h.i()) {
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                arrayList2.add(list.get(i).packageName);
            }
            String join = TextUtils.join(",", arrayList2);
            HashMap hashMap = new HashMap();
            hashMap.put("pkgs", join);
            try {
                String a2 = k.a((Map<String, String>) hashMap, a.f4249d, new j("gamebooster_loadgamelistfromnet"));
                if (!TextUtils.isEmpty(a2)) {
                    JSONArray optJSONArray = new JSONObject(a2).optJSONArray("result");
                    for (int i2 = 0; i2 < list.size(); i2++) {
                        if (optJSONArray.optInt(i2) == 1) {
                            ApplicationInfo applicationInfo = list.get(i2);
                            String a3 = x.a(context, applicationInfo);
                            C0391w.a(context, a3, applicationInfo.packageName, applicationInfo.uid, 0);
                            arrayList.add(new C0398d(applicationInfo, true, a3, applicationInfo.loadIcon(packageManager)));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public static List<ApplicationInfo> a(PackageManager packageManager, List<ApplicationInfo> list) {
        list.addAll(packageManager.getInstalledApplications(0));
        if (B.j() == 0) {
            List<ApplicationInfo> list2 = null;
            try {
                list2 = d.a(0, 999);
            } catch (Exception e) {
                Log.i("GameBoosterUtils", e.toString());
            }
            if (list2 != null) {
                list.addAll(list2);
            }
        }
        return list;
    }

    public static void a(Activity activity, String str, String str2, int i) {
        Intent intent = new Intent("miui.intent.action.GAMEBOOSTER_SECURITY_WEB_VIEW_LAND");
        intent.putExtra(MijiaAlertModel.KEY_URL, str);
        intent.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, str2);
        intent.setFlags(32768);
        intent.addFlags(536870912);
        activity.startActivityForResult(intent, i);
    }

    public static void a(Context context, Intent intent, String str, boolean z) {
        if (z) {
            try {
                intent.addFlags(268435456);
            } catch (Exception e) {
                Log.e("GameBoosterUtils", "viewActionActivity", e);
                return;
            }
        }
        if ("com.miui.gamebooster.action.ACCESS_MAINACTIVITY".equals(intent.getAction())) {
            intent.putExtra("track_gamebooster_enter_way", str);
        }
        context.startActivity(intent);
    }

    public static void a(Context context, IGameBooster iGameBooster, String str, UserHandle userHandle) {
        if (str == null || str.length() == 0) {
            Log.d("GameBooster", "package name must not null");
        } else {
            new Handler().postDelayed(new C0392x(context, str, userHandle), 50);
        }
    }

    public static void a(Context context, String str, UserHandle userHandle) {
        try {
            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
            launchIntentForPackage.putExtra("android.intent.extra.auth_to_call_xspace", "true");
            e.a((Object) context, "startActivityAsUser", (Class<?>) Context.class, (Class<?>[]) new Class[]{Intent.class, UserHandle.class}, launchIntentForPackage, userHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(Context context, String str, String str2) {
        Intent intent = new Intent("miui.intent.action.GAMEBOOSTER_SECURITY_WEB_VIEW");
        intent.putExtra(MijiaAlertModel.KEY_URL, str);
        intent.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, str2);
        g.b(context, intent, UserHandle.CURRENT);
    }

    public static void a(Context context, String str, boolean z) {
        if (z) {
            String b2 = b(context, str);
            b.b("key_power_save_config", b2);
            if (!UserConfigure.BG_CONTROL_NO_RESTRICT.equals(b2)) {
                b(context, str, UserConfigure.BG_CONTROL_NO_RESTRICT);
                return;
            }
            return;
        }
        String a2 = b.a("key_power_save_config", (String) null);
        if (a2 != null && !UserConfigure.BG_CONTROL_NO_RESTRICT.equals(a2)) {
            b(context, str, a2);
            b.b("key_power_save_config", (String) null);
        }
    }

    public static void a(Context context, boolean z) {
        ((WifiManager) context.getSystemService("wifi")).setWifiEnabled(z);
    }

    public static void a(View view, boolean z) {
        try {
            Log.i("GameBoosterUtils", "setForceDarkEnable");
            e.b(view, "setForceDarkAllowed", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("GameBoosterUtils", "reflect error when setForceDark", e);
        }
    }

    public static boolean a(Context context) {
        return ((WifiManager) context.getSystemService("wifi")).isWifiEnabled();
    }

    public static boolean a(Context context, Intent intent) {
        return context.getPackageManager().queryIntentServices(intent, 4).size() > 0;
    }

    public static boolean a(Context context, String str) {
        return (str == null || str.length() == 0 || x.c(context, str) == null) ? false : true;
    }

    public static boolean a(String str, Context context) {
        if (!C0388t.m()) {
            return str.equals(AppConstants.Package.PACKAGE_NAME_QQ) || str.equals(AppConstants.Package.PACKAGE_NAME_MM);
        }
        if (f4530c.isEmpty()) {
            b(context);
        }
        return f4530c.contains(str);
    }

    public static boolean a(List<?> list) {
        return list == null || list.size() <= 0;
    }

    public static String b(Context context, String str) {
        int i;
        try {
            i = g.a(context);
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            i = 0;
        }
        String[] strArr = {str, Integer.toString(i)};
        String str2 = "";
        Cursor query = context.getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://com.miui.powerkeeper.configure"), UserConfigure.TABLE), (String[]) null, "pkgName = ? AND userId = ?", strArr, (String) null);
        if (query != null) {
            if (query.moveToFirst()) {
                str2 = query.getString(query.getColumnIndex(UserConfigure.Columns.BG_CONTROL));
            }
            query.close();
        }
        return str2;
    }

    public static void b(Activity activity, String str, String str2, int i) {
        Intent intent = new Intent("miui.intent.action.GAMEBOOSTER_SECURITY_WEB_VIEW");
        intent.putExtra(MijiaAlertModel.KEY_URL, str);
        intent.putExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, str2);
        activity.startActivityForResult(intent, i);
    }

    public static void b(Context context) {
        ArrayList<String> c2 = C0382m.c("gamebooster", "freeformlist", context);
        if (!c2.isEmpty()) {
            f4530c.addAll(c2);
            return;
        }
        f4530c.add(AppConstants.Package.PACKAGE_NAME_QQ);
        f4530c.add(AppConstants.Package.PACKAGE_NAME_MM);
        f4530c.add("com.sina.weibo");
        f4530c.add("com.alibaba.android.rimet");
        f4530c.add("com.tencent.androidqqmail");
        f4530c.add("com.qzone");
        f4530c.add("com.tencent.wework");
        f4530c.add("com.sina.weibolite");
        f4530c.add("com.miui.miuibbs");
        f4530c.add("com.immomo.momo");
        f4530c.add("com.tencent.news");
        f4530c.add("com.zhihu.android");
        f4530c.add("com.tencent.reading");
        f4530c.add("com.netease.newsreader.activity");
        f4530c.add("com.cashtoutiao");
        f4530c.add("com.dianping.v1");
        f4530c.add("com.qq.reader");
        f4530c.add("com.tencent.mtt");
        f4530c.add("com.baidu.searchbox");
        f4530c.add("com.UCMobile");
        f4530c.add("com.baidu.tieba");
        f4530c.add("com.tencent.gamehelper.smoba");
        f4530c.add(AppConstants.Package.PACKAGE_NAME_ALIPAY);
        f4530c.add("com.taobao.taobao");
        f4530c.add("com.jingdong.app.mall");
        f4530c.add("com.sankuai.meituan");
        f4530c.add("com.tencent.qqmusic");
        f4530c.add("com.android.settings");
        f4530c.add("com.android.mms");
        f4530c.add("com.android.browser");
        f4530c.add("com.mi.globalbrowser");
        f4530c.add("com.miui.notes");
    }

    public static void b(Context context, String str, String str2) {
        int i;
        try {
            i = g.a(context);
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
            i = 0;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(UserConfigure.Columns.USER_ID, i);
        bundle.putString("pkgName", str);
        bundle.putString(UserConfigure.Columns.BG_CONTROL, str2);
        try {
            context.getContentResolver().call(Uri.withAppendedPath(Uri.parse("content://com.miui.powerkeeper.configure"), UserConfigure.TABLE), UserConfigure.METHOD_UPDATE, (String) null, bundle);
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        }
    }

    public static boolean c(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "diving_mode", 0) == 1;
    }

    public static boolean d(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String a2 = C0374e.a(context, "top_200_games.json");
        if (a2 == null || a2.length() <= 0) {
            return false;
        }
        for (ApplicationInfo next : packageManager.getInstalledApplications(8192)) {
            if (x.a(next) && a2.contains(next.packageName)) {
                return true;
            }
        }
        return false;
    }
}
