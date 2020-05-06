package com.miui.gamebooster.m;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;
import b.b.c.j.i;
import b.b.o.g.c;
import com.miui.analytics.AnalyticsUtil;
import com.miui.gamebooster.ui.GameBoosterMainActivity;
import com.miui.securitycenter.R;
import java.util.List;

public class Z {

    /* renamed from: a  reason: collision with root package name */
    public static final String f4466a = (i.a() + ".launcher.settings");

    /* renamed from: b  reason: collision with root package name */
    public static final String f4467b = ("content://" + f4466a + "/favorites");

    public static void a(Context context, String str) {
        try {
            Object systemService = context.getSystemService("shortcut");
            Intent intent = new Intent(context, GameBoosterMainActivity.class);
            if (a(systemService)) {
                intent.setAction("android.intent.action.VIEW");
                PersistableBundle persistableBundle = new PersistableBundle();
                persistableBundle.putBoolean("retained", true);
                c.a a2 = c.a.a("android.graphics.drawable.Icon");
                a2.b("createWithResource", new Class[]{Context.class, Integer.TYPE}, context, Integer.valueOf(R.drawable.game_booster_icon));
                Object d2 = a2.d();
                c.a a3 = c.a.a("android.content.pm.ShortcutInfo$Builder");
                a3.a(new Class[]{Context.class, String.class}, context, "shortcut_com_miui_gamebooster");
                Class[] clsArr = new Class[1];
                clsArr[0] = Class.forName("android.graphics.drawable.Icon");
                a3.a("setIcon", clsArr, d2);
                a3.e();
                a3.a("setShortLabel", new Class[]{CharSequence.class}, str);
                a3.e();
                a3.a("setIntent", new Class[]{Intent.class}, intent);
                a3.e();
                a3.a("setExtras", new Class[]{PersistableBundle.class}, persistableBundle);
                a3.e();
                a3.a("build", (Class<?>[]) null, new Object[0]);
                Object d3 = a3.d();
                c.a a4 = c.a.a(systemService);
                Class[] clsArr2 = new Class[2];
                clsArr2[0] = Class.forName("android.content.pm.ShortcutInfo");
                clsArr2[1] = IntentSender.class;
                a4.a("requestPinShortcut", clsArr2, d3, null);
                Log.i("ShortcutCompat", "requestPinShortcut");
            }
        } catch (Exception e) {
            Log.e("GameBoosterReflectUtils", "createShortcutIntent error ", e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003d, code lost:
        if (r0 == null) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003f, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0042, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0034, code lost:
        if (r0 != null) goto L_0x003f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(android.content.Context r9) {
        /*
            android.content.Intent r0 = com.miui.gamebooster.m.ba.a(r9)
            r1 = 0
            java.lang.String r0 = r0.toUri(r1)
            java.lang.String r2 = f4467b
            android.net.Uri r4 = android.net.Uri.parse(r2)
            java.lang.String r2 = "_id"
            java.lang.String[] r5 = new java.lang.String[]{r2}
            java.lang.String r6 = " intent = ? "
            r2 = 1
            java.lang.String[] r7 = new java.lang.String[r2]
            r7[r1] = r0
            r0 = 0
            android.content.ContentResolver r3 = r9.getContentResolver()     // Catch:{ Exception -> 0x0039 }
            r8 = 0
            android.database.Cursor r0 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0039 }
            if (r0 == 0) goto L_0x0034
            int r9 = r0.getCount()     // Catch:{ Exception -> 0x0039 }
            if (r9 == 0) goto L_0x0034
            if (r0 == 0) goto L_0x0033
            r0.close()
        L_0x0033:
            return r2
        L_0x0034:
            if (r0 == 0) goto L_0x0042
            goto L_0x003f
        L_0x0037:
            r9 = move-exception
            goto L_0x0043
        L_0x0039:
            r9 = move-exception
            r9.printStackTrace()     // Catch:{ all -> 0x0037 }
            if (r0 == 0) goto L_0x0042
        L_0x003f:
            r0.close()
        L_0x0042:
            return r1
        L_0x0043:
            if (r0 == 0) goto L_0x0048
            r0.close()
        L_0x0048:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.Z.a(android.content.Context):boolean");
    }

    private static boolean a(Object obj) {
        c.a a2 = c.a.a(obj);
        a2.a("isRequestPinShortcutSupported", (Class<?>[]) null, new Object[0]);
        return a2.a();
    }

    public static boolean b(Context context, String str) {
        if (Build.VERSION.SDK_INT < 26) {
            return a(context);
        }
        if (str == null) {
            str = "shortcut_com_miui_gamebooster";
        }
        if (a(context)) {
            return true;
        }
        try {
            List<Object> list = (List) c.a(context.getSystemService("shortcut"), List.class, "getPinnedShortcuts", (Class<?>[]) null, new Object[0]);
            if (list != null && !list.isEmpty()) {
                for (Object a2 : list) {
                    if (str.equals((String) c.a(a2, String.class, "getId", (Class<?>[]) null, new Object[0]))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.i("androidO_ShortcutCompat", e.toString());
            AnalyticsUtil.trackException(e);
        }
        return false;
    }

    public static boolean c(Context context, String str) {
        if (str == null) {
            str = "shortcut_com_miui_gamebooster";
        }
        try {
            List<Object> list = (List) c.a(context.getSystemService("shortcut"), List.class, "getPinnedShortcuts", (Class<?>[]) null, new Object[0]);
            if (list != null && !list.isEmpty()) {
                for (Object a2 : list) {
                    String str2 = (String) c.a(a2, String.class, "getId", (Class<?>[]) null, new Object[0]);
                    if (str2 != null && str2.equals(str)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ShortcutCompat", "isInPinnedShortcutsList error ", e);
        }
        return false;
    }
}
