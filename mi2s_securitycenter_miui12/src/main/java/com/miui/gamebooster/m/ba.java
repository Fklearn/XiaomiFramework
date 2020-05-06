package com.miui.gamebooster.m;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import b.b.c.j.i;
import com.miui.gamebooster.ui.WelcomActivity;
import com.miui.securitycenter.R;

public class ba {

    /* renamed from: a  reason: collision with root package name */
    public static final String f4474a = (i.a() + ".launcher.settings");

    /* renamed from: b  reason: collision with root package name */
    public static final String f4475b = ("content://" + f4474a + "/favorites");

    public static Intent a(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setComponent(new ComponentName(context, WelcomActivity.class));
        return intent;
    }

    public static void a(Context context, Boolean bool) {
        if (!C0389u.a()) {
            new aa(bool, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public static void a(Context context, String str) {
        boolean c2 = Build.VERSION.SDK_INT >= 26 ? Z.c(context, str) : false;
        boolean a2 = a(context, false);
        if (c2) {
            Log.d("ShortcutHelper", "uninstall pinnedList shortcut : ");
            Intent intent = new Intent("com.miui.home.launcher.action.UNINSTALL_SHORTCUT");
            if (str == null) {
                str = "shortcut_com_miui_gamebooster";
            }
            intent.putExtra("shortcut_id", str);
            intent.setPackage(i.a());
            context.sendBroadcast(intent);
        }
        if (a2) {
            Log.d("ShortcutHelper", "uninstall database shortcut : ");
            context.sendBroadcast(b(context, "com.miui.home.launcher.action.UNINSTALL_SHORTCUT", "com.miui.securitycenter:string/game_booster"));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(android.content.Context r9, boolean r10) {
        /*
            android.content.Intent r0 = a(r9)
            r1 = 0
            java.lang.String r0 = r0.toUri(r1)
            java.lang.String r2 = f4475b
            android.net.Uri r4 = android.net.Uri.parse(r2)
            java.lang.String r2 = "_id"
            java.lang.String[] r5 = new java.lang.String[]{r2}
            java.lang.String r6 = " intent = ? "
            r2 = 1
            java.lang.String[] r7 = new java.lang.String[r2]
            r7[r1] = r0
            r0 = 0
            android.content.ContentResolver r3 = r9.getContentResolver()     // Catch:{ Exception -> 0x003b, all -> 0x0038 }
            r8 = 0
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x003b, all -> 0x0038 }
            if (r3 == 0) goto L_0x0034
            int r4 = r3.getCount()     // Catch:{ Exception -> 0x0032 }
            if (r4 == 0) goto L_0x0034
            miui.util.IOUtils.closeQuietly(r3)
            return r2
        L_0x0032:
            r4 = move-exception
            goto L_0x003d
        L_0x0034:
            miui.util.IOUtils.closeQuietly(r3)
            goto L_0x0047
        L_0x0038:
            r9 = move-exception
            r3 = r0
            goto L_0x0052
        L_0x003b:
            r4 = move-exception
            r3 = r0
        L_0x003d:
            java.lang.String r5 = "ShortcutHelper"
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0051 }
            android.util.Log.e(r5, r4)     // Catch:{ all -> 0x0051 }
            goto L_0x0034
        L_0x0047:
            if (r10 == 0) goto L_0x0050
            boolean r9 = com.miui.gamebooster.m.Z.c(r9, r0)
            if (r9 == 0) goto L_0x0050
            r1 = r2
        L_0x0050:
            return r1
        L_0x0051:
            r9 = move-exception
        L_0x0052:
            miui.util.IOUtils.closeQuietly(r3)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.ba.a(android.content.Context, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public static Intent b(Context context, String str, String str2) {
        Intent intent = new Intent(str);
        intent.putExtra("android.intent.extra.shortcut.NAME", str2);
        intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(context, R.drawable.game_booster_icon));
        intent.putExtra("duplicate", false);
        intent.putExtra("retained", true);
        intent.putExtra("android.intent.extra.shortcut.INTENT", a(context));
        return intent;
    }

    public static void b(Context context) {
        if (C0388t.s() && Z.b(context, "shortcut_com_miui_gamebooster_gamebox")) {
            a(context, "shortcut_com_miui_gamebooster_gamebox");
            a(context, (Boolean) false);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0037, code lost:
        if (r0 != null) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0040, code lost:
        if (r0 == null) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0042, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0045, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean c(android.content.Context r9) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 23
            if (r0 < r1) goto L_0x0009
            java.lang.String r0 = "#Intent;component=com.miui.securityadd/com.miui.gamebooster.ui.WelcomActivity;end"
            goto L_0x000b
        L_0x0009:
            java.lang.String r0 = "#Intent;action=android.intent.action.VIEW;component=com.miui.securityadd/com.miui.gamebooster.ui.WelcomActivity;end"
        L_0x000b:
            java.lang.String r1 = f4475b
            android.net.Uri r3 = android.net.Uri.parse(r1)
            java.lang.String r1 = "_id"
            java.lang.String[] r4 = new java.lang.String[]{r1}
            java.lang.String r5 = " intent = ? "
            r1 = 1
            java.lang.String[] r6 = new java.lang.String[r1]
            r8 = 0
            r6[r8] = r0
            r0 = 0
            android.content.ContentResolver r2 = r9.getContentResolver()     // Catch:{ Exception -> 0x003c }
            r7 = 0
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x003c }
            if (r0 == 0) goto L_0x0037
            int r9 = r0.getCount()     // Catch:{ Exception -> 0x003c }
            if (r9 == 0) goto L_0x0037
            if (r0 == 0) goto L_0x0036
            r0.close()
        L_0x0036:
            return r1
        L_0x0037:
            if (r0 == 0) goto L_0x0045
            goto L_0x0042
        L_0x003a:
            r9 = move-exception
            goto L_0x0046
        L_0x003c:
            r9 = move-exception
            r9.printStackTrace()     // Catch:{ all -> 0x003a }
            if (r0 == 0) goto L_0x0045
        L_0x0042:
            r0.close()
        L_0x0045:
            return r8
        L_0x0046:
            if (r0 == 0) goto L_0x004b
            r0.close()
        L_0x004b:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.ba.c(android.content.Context):boolean");
    }
}
