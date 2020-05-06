package com.miui.gamebooster.m;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import b.b.c.j.x;
import com.miui.common.persistence.b;
import com.miui.gamebooster.c.a;
import com.miui.securitycenter.R;
import java.io.Closeable;
import miui.os.Build;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.util.HardwareInfo;
import miui.util.IOUtils;

public class D {
    public static int a(Context context, String str, int i) {
        Cursor cursor = null;
        try {
            cursor = C0391w.a(context.getApplicationContext(), str, 0, i);
            if (cursor != null && cursor.moveToFirst()) {
                int i2 = cursor.getInt(cursor.getColumnIndex("settings_hdr"));
                Log.d("queryAdvanceSettingsValue", ",   HDR = " + i2);
                IOUtils.closeQuietly(cursor);
                return i2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return -1;
    }

    private static void a(Context context, int i) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            Toast.makeText(context, context.getResources().getString(i), 0).show();
        }
    }

    public static void a(Context context, View view) {
        int i;
        SubscriptionInfo subscriptionInfoForSlot = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(0);
        SubscriptionInfo subscriptionInfoForSlot2 = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(1);
        if (C0384o.a() && subscriptionInfoForSlot != null && subscriptionInfoForSlot2 != null && subscriptionInfoForSlot.isActivated() && subscriptionInfoForSlot2.isActivated()) {
            if (a.f(false)) {
                Toast.makeText(context, context.getResources().getString(R.string.gamebox_func_switch_simcard_immersion), 0).show();
                return;
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
            int defaultDataSlotId = SubscriptionManager.getDefault().getDefaultDataSlotId();
            if (defaultDataSlotId == 0) {
                SubscriptionManager.getDefault().setDefaultDataSlotId(1);
                b(context, 2);
                i = R.drawable.gamebox_simcard_two_button;
            } else if (defaultDataSlotId == 1) {
                SubscriptionManager.getDefault().setDefaultDataSlotId(0);
                b(context, 1);
                i = R.drawable.gamebox_simcard_one_button;
            } else {
                return;
            }
            imageView.setImageResource(i);
        }
    }

    public static void a(Context context, View view, boolean z) {
        int i;
        Resources resources;
        Resources resources2;
        TextView textView;
        int i2;
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        if (a(context)) {
            if (z) {
                a(context, (int) R.string.gamebox_func_antimsg_close);
                imageView.setImageResource(R.drawable.gamebox_dnd_button);
                textView = (TextView) view.findViewById(R.id.label);
                resources2 = context.getResources();
                i2 = R.color.gamebox_func_text;
            } else {
                resources = context.getResources();
                i = R.drawable.transparent_selector;
                imageView.setBackground(resources.getDrawable(i));
                context.sendBroadcastAsUser(new Intent("com.miui.gamebooster.service.action.SWITCHANTIMSG"), UserHandle.CURRENT);
                Log.i("GameBoxFunctionUtils", "swtichAntiMsgMode");
            }
        } else if (z) {
            a(context, (int) R.string.gamebox_func_antimsg_open);
            imageView.setImageResource(R.drawable.gamebox_dnd_light_button);
            textView = (TextView) view.findViewById(R.id.label);
            resources2 = context.getResources();
            i2 = R.color.gamebox_func_text_light;
        } else {
            resources = context.getResources();
            i = R.drawable.gamebox_antimsg_openbg;
            imageView.setBackground(resources.getDrawable(i));
            context.sendBroadcastAsUser(new Intent("com.miui.gamebooster.service.action.SWITCHANTIMSG"), UserHandle.CURRENT);
            Log.i("GameBoxFunctionUtils", "swtichAntiMsgMode");
        }
        textView.setTextColor(resources2.getColor(i2));
        context.sendBroadcastAsUser(new Intent("com.miui.gamebooster.service.action.SWITCHANTIMSG"), UserHandle.CURRENT);
        Log.i("GameBoxFunctionUtils", "swtichAntiMsgMode");
    }

    public static void a(Context context, String str) {
        ResolveInfo a2 = x.a(context, str);
        if (a2 == null) {
            Toast.makeText(context, context.getString(R.string.gamebox_app_not_find), 0).show();
        } else {
            C0383n.a(context, a2.activityInfo.applicationInfo.packageName, a2.activityInfo.name, R.string.gamebox_app_not_find);
        }
    }

    public static void a(Context context, boolean z) {
        Intent intent = new Intent("com.android.phone.intent.action.DIVING_MODE");
        intent.setPackage("com.android.phone");
        intent.putExtra("diving_mode_key", z);
        context.sendBroadcast(intent);
        Log.i("GameBoxFunctionUtils", "startDivingMode" + z);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0115, code lost:
        a(r7, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x01e6, code lost:
        android.util.Log.i("GameBoxFunctionUtils", r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0048, code lost:
        com.miui.gamebooster.m.C0373d.i(r5);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(com.miui.gamebooster.p.r r5, com.miui.gamebooster.model.g r6, android.content.Context r7, android.view.View r8) {
        /*
            int[] r0 = com.miui.gamebooster.m.C.f4449a
            com.miui.gamebooster.d.d r6 = r6.c()
            int r6 = r6.ordinal()
            r6 = r0[r6]
            java.lang.String r0 = "antimsg"
            r1 = 1
            r2 = 0
            java.lang.String r3 = "GameBoxFunctionUtils"
            switch(r6) {
                case 1: goto L_0x01d5;
                case 2: goto L_0x01c3;
                case 3: goto L_0x011a;
                case 4: goto L_0x0113;
                case 5: goto L_0x0110;
                case 6: goto L_0x010d;
                case 7: goto L_0x0101;
                case 8: goto L_0x00f5;
                case 9: goto L_0x00e9;
                case 10: goto L_0x00df;
                case 11: goto L_0x00d5;
                case 12: goto L_0x00ce;
                case 13: goto L_0x00c2;
                case 14: goto L_0x00b6;
                case 15: goto L_0x00aa;
                case 16: goto L_0x009a;
                case 17: goto L_0x0065;
                case 18: goto L_0x0038;
                case 19: goto L_0x0030;
                case 20: goto L_0x0017;
                default: goto L_0x0015;
            }
        L_0x0015:
            goto L_0x01e9
        L_0x0017:
            java.lang.String r5 = "settings"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            android.content.Intent r5 = new android.content.Intent
            java.lang.String r6 = "com.miui.gamebooster.action.ACCESS_MAINACTIVITY"
            r5.<init>(r6)
            r6 = 32768(0x8000, float:4.5918E-41)
            r5.addFlags(r6)
            java.lang.String r6 = "00008"
            com.miui.gamebooster.m.C0393y.a((android.content.Context) r7, (android.content.Intent) r5, (java.lang.String) r6, (boolean) r1)
            goto L_0x01e9
        L_0x0030:
            if (r5 == 0) goto L_0x0035
            r5.a((android.content.Context) r7)
        L_0x0035:
            java.lang.String r5 = "manual_record"
            goto L_0x0048
        L_0x0038:
            boolean r5 = com.miui.gamebooster.m.ma.a()
            if (r5 == 0) goto L_0x004d
            com.miui.gamebooster.customview.W r5 = new com.miui.gamebooster.customview.W
            r5.<init>(r7)
            r5.a()
            java.lang.String r5 = "voicechanger"
        L_0x0048:
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            goto L_0x01e9
        L_0x004d:
            android.content.Intent r5 = new android.content.Intent
            java.lang.String r6 = "com.miui.gamebooster.action.XUNYOU_ALERT_ACTIVITY"
            r5.<init>(r6)
            java.lang.String r6 = "alertType"
            java.lang.String r8 = "voice_changer_permission_dialog"
            r5.putExtra(r6, r8)
            r6 = 268435456(0x10000000, float:2.5243549E-29)
            r5.addFlags(r6)
            r7.startActivity(r5)
            goto L_0x01e9
        L_0x0065:
            android.content.Context r5 = r7.getApplicationContext()
            com.miui.gamebooster.m.N r5 = com.miui.gamebooster.m.N.a((android.content.Context) r5)
            boolean r6 = r5.d()
            if (r6 == 0) goto L_0x0086
            android.content.res.Resources r5 = r7.getResources()
            r6 = 2131757103(0x7f10082f, float:1.9145132E38)
            java.lang.String r5 = r5.getString(r6)
            android.widget.Toast r5 = android.widget.Toast.makeText(r7, r5, r2)
            r5.show()
            return
        L_0x0086:
            boolean r6 = r5.b()
            if (r6 == 0) goto L_0x0093
            r6 = 2131758041(0x7f100bd9, float:1.9147035E38)
            r5.a((int) r6)
            goto L_0x0096
        L_0x0093:
            r5.f()
        L_0x0096:
            java.lang.String r5 = "MILINK"
            goto L_0x01e6
        L_0x009a:
            com.miui.gamebooster.m.z r5 = new com.miui.gamebooster.m.z
            r5.<init>(r7)
            com.miui.gamebooster.m.v r6 = com.miui.gamebooster.m.C0390v.a((android.content.Context) r7)
            r6.a((b.b.c.f.a.C0027a) r5)
            java.lang.String r5 = "DISPLAY"
            goto L_0x01e6
        L_0x00aa:
            e(r7)
            java.lang.String r5 = "hangup"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "HANGUP"
            goto L_0x01e6
        L_0x00b6:
            c(r7, r8)
            java.lang.String r5 = "immersion"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "IMMERSION"
            goto L_0x01e6
        L_0x00c2:
            a((android.content.Context) r7, (android.view.View) r8)
            java.lang.String r5 = "switch_sim"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "SIMCARD"
            goto L_0x01e6
        L_0x00ce:
            b((android.content.Context) r7, (android.view.View) r8)
            java.lang.String r5 = "WIFI"
            goto L_0x01e6
        L_0x00d5:
            a((android.content.Context) r7, (android.view.View) r8, (boolean) r1)
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r0)
            java.lang.String r5 = "DND"
            goto L_0x01e6
        L_0x00df:
            a((android.content.Context) r7, (android.view.View) r8, (boolean) r2)
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r0)
            java.lang.String r5 = "ANTIMSG"
            goto L_0x01e6
        L_0x00e9:
            c(r7)
            java.lang.String r5 = "clean"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "ONEKEYCLEAN"
            goto L_0x01e6
        L_0x00f5:
            f(r7)
            java.lang.String r5 = "screen_record"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "RECORD"
            goto L_0x01e6
        L_0x0101:
            d(r7)
            java.lang.String r5 = "screen_shot"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "QUICKSCREENSHOT"
            goto L_0x01e6
        L_0x010d:
            java.lang.String r5 = "com.whatsapp"
            goto L_0x0115
        L_0x0110:
            java.lang.String r5 = "com.vkontakte.android"
            goto L_0x0115
        L_0x0113:
            java.lang.String r5 = "com.facebook.katana"
        L_0x0115:
            a((android.content.Context) r7, (java.lang.String) r5)
            goto L_0x01e9
        L_0x011a:
            if (r5 == 0) goto L_0x0125
            com.miui.gamebooster.service.GameBoxWindowManagerService r5 = r5.e()
            java.lang.String r5 = r5.c()
            goto L_0x0127
        L_0x0125:
            java.lang.String r5 = ""
        L_0x0127:
            b.b.l.b r6 = b.b.l.b.b()
            boolean r6 = r6.a((java.lang.String) r5)
            if (r6 == 0) goto L_0x0176
            b.b.l.b r6 = b.b.l.b.b()
            java.lang.String r6 = r6.d(r5)
            r8 = 0
            boolean r0 = android.text.TextUtils.isEmpty(r6)
            if (r0 != 0) goto L_0x0144
            android.net.Uri r8 = android.net.Uri.parse(r6)
        L_0x0144:
            int r0 = b.b.c.j.e.b()
            r2 = 8
            if (r0 <= r2) goto L_0x0153
            r6 = 2131756349(0x7f10053d, float:1.9143603E38)
            com.miui.gamebooster.m.C0383n.a(r7, r8, r6)
            goto L_0x0159
        L_0x0153:
            b((android.content.Context) r7, (java.lang.String) r6)
            g(r7)
        L_0x0159:
            java.lang.String r6 = "active_info"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r6)
            com.miui.gamebooster.m.s r6 = com.miui.gamebooster.m.C0387s.b()
            java.lang.String r7 = "click"
            com.miui.gamebooster.model.ActiveTrackModel r7 = com.miui.gamebooster.m.C0387s.a((java.lang.String) r5, (java.lang.String) r7)
            r6.a((com.miui.gamebooster.model.ActiveTrackModel) r7)
            b.b.l.b r6 = b.b.l.b.b()
            r6.b(r5, r1)
            java.lang.String r5 = "QUICKACTIVE"
            goto L_0x01e6
        L_0x0176:
            java.lang.String r5 = "com.android.browser"
            boolean r6 = b.b.c.j.x.g(r7, r5)
            java.lang.String r8 = "com.mi.globalbrowser"
            boolean r0 = b.b.c.j.x.g(r7, r8)
            boolean r1 = miui.os.Build.IS_INTERNATIONAL_BUILD
            r4 = 2131756350(0x7f10053e, float:1.9143605E38)
            if (r1 == 0) goto L_0x01a0
            if (r6 != 0) goto L_0x01a0
            if (r0 != 0) goto L_0x01a0
            java.lang.String r5 = "com.android.chrome"
            boolean r6 = b.b.c.j.x.h(r7, r5)
            if (r6 == 0) goto L_0x01b0
            android.content.pm.ResolveInfo r6 = b.b.c.j.x.a((android.content.Context) r7, (java.lang.String) r5)
            if (r6 == 0) goto L_0x01bb
            android.content.pm.ActivityInfo r6 = r6.activityInfo
            java.lang.String r6 = r6.name
            goto L_0x01ac
        L_0x01a0:
            if (r0 == 0) goto L_0x01a8
            java.lang.String r5 = "com.mi.globalbrowser.Main"
            com.miui.gamebooster.m.C0383n.a(r7, r8, r5, r4)
            goto L_0x01bb
        L_0x01a8:
            if (r6 == 0) goto L_0x01b0
            java.lang.String r6 = "com.android.browser.BrowserActivity"
        L_0x01ac:
            com.miui.gamebooster.m.C0383n.a(r7, r5, r6, r4)
            goto L_0x01bb
        L_0x01b0:
            java.lang.String r5 = r7.getString(r4)
            android.widget.Toast r5 = android.widget.Toast.makeText(r7, r5, r2)
            r5.show()
        L_0x01bb:
            java.lang.String r5 = "browser"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "QUICKBROWSER"
            goto L_0x01e6
        L_0x01c3:
            r5 = 2131756375(0x7f100557, float:1.9143656E38)
            java.lang.String r6 = "com.tencent.mobileqq"
            java.lang.String r8 = "com.tencent.mobileqq.activity.SplashActivity"
            com.miui.gamebooster.m.C0383n.a(r7, r6, r8, r5)
            java.lang.String r5 = "qq"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "QUICKQQ"
            goto L_0x01e6
        L_0x01d5:
            r5 = 2131756382(0x7f10055e, float:1.914367E38)
            java.lang.String r6 = "com.tencent.mm"
            java.lang.String r8 = "com.tencent.mm.ui.LauncherUI"
            com.miui.gamebooster.m.C0383n.a(r7, r6, r8, r5)
            java.lang.String r5 = "wechat"
            com.miui.gamebooster.m.C0373d.i((java.lang.String) r5)
            java.lang.String r5 = "QUICKWEIXIN"
        L_0x01e6:
            android.util.Log.i(r3, r5)
        L_0x01e9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.D.a(com.miui.gamebooster.p.r, com.miui.gamebooster.model.g, android.content.Context, android.view.View):void");
    }

    public static boolean a(Context context) {
        if (C0388t.m()) {
            a.a(context);
            return a.b(false);
        }
        a.a(context);
        return a.c(false);
    }

    private static void b(Context context, int i) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            Toast.makeText(context, context.getResources().getString(R.string.gamebox_func_switch_simcard, new Object[]{Integer.valueOf(i)}), 0).show();
        }
    }

    public static void b(Context context, View view) {
        TextView textView;
        int i;
        Resources resources;
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        boolean a2 = C0393y.a(context);
        C0393y.a(context, !a2);
        if (a2) {
            imageView.setImageResource(R.drawable.gamebox_wifi_button);
            textView = (TextView) view.findViewById(R.id.label);
            resources = context.getResources();
            i = R.color.gamebox_func_text;
        } else {
            imageView.setImageResource(R.drawable.gamebox_wifi_light_button);
            textView = (TextView) view.findViewById(R.id.label);
            resources = context.getResources();
            i = R.color.gamebox_func_text_light;
        }
        textView.setTextColor(resources.getColor(i));
        a(context, a2 ? R.string.gamebox_func_wlan_close : R.string.gamebox_func_wlan_open);
    }

    private static void b(Context context, String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addFlags(402653184);
            intent.setData(Uri.parse(str));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("GameBoxFunctionUtils", "start activity error", e);
        }
    }

    public static boolean b(Context context) {
        return C0384o.a(context.getContentResolver(), "gb_boosting", 0, -2) == 1;
    }

    public static void c(Context context) {
        Handler handler = new Handler();
        Toast.makeText(context, context.getString(R.string.do_clean), 0).show();
        U.a(context, "com.miui.securitycenter");
        handler.postDelayed(new A((int) (Math.abs(HardwareInfo.getFreeMemory()) / 1048576), context), 1500);
    }

    public static void c(Context context, View view) {
        SubscriptionInfo subscriptionInfoForSlot = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(0);
        SubscriptionInfo subscriptionInfoForSlot2 = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(1);
        if ((subscriptionInfoForSlot != null && subscriptionInfoForSlot.isActivated()) || (subscriptionInfoForSlot2 != null && subscriptionInfoForSlot2.isActivated())) {
            if (!b.a("key_gamebooster_immersion_ok", false)) {
                Intent intent = new Intent("com.miui.gamebooster.action.GAMEBOX_ALERT_ACTIVITY");
                intent.putExtra("intent_gamebox_function_type", "intent_gamebox_func_type_immersion");
                intent.addFlags(268435456);
                context.startActivity(intent);
                return;
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
            a.a(context);
            boolean f = a.f(false);
            if (f) {
                imageView.setImageResource(R.drawable.gamebox_immersion_button);
                ((TextView) view.findViewById(R.id.label)).setTextColor(context.getResources().getColor(R.color.gamebox_func_text));
                a(context, false);
                a.a(context);
                a.E(false);
            } else {
                imageView.setImageResource(R.drawable.gamebox_immersion_light_button);
                ((TextView) view.findViewById(R.id.label)).setTextColor(context.getResources().getColor(R.color.gamebox_func_text_light));
                a(context, true);
                a.a(context);
                a.E(true);
                Toast.makeText(context, context.getResources().getString(R.string.gamebox_immerson_open), 0).show();
            }
            a(context, f ? R.string.gamebox_func_immersion_close : R.string.gamebox_func_immersion_open);
        }
    }

    public static void d(Context context) {
        new Handler().postDelayed(new B(context), 400);
    }

    public static void e(Context context) {
        String str = b.a("key_currentbooster_pkg_uid", (String) null).split(",")[0];
        if (N.a(context.getApplicationContext()).b() && C0388t.x()) {
            if (!b.a("key_gamebooster_milink_hangup_ok", false)) {
                Intent intent = new Intent("com.miui.gamebooster.action.GAMEBOX_ALERT_ACTIVITY");
                intent.putExtra("intent_gamebox_function_type", "intent_gamebox_func_type_milink_hangup");
                intent.putExtra("intent_gamebox_booster_pkg", str);
                intent.addFlags(268435456);
                context.startActivity(intent);
            } else {
                C0384o.b(context.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$Secure", "SCREEN_PROJECT_HANG_UP"), 1, 0);
            }
            Log.i("GameBoxFunctionUtils", "newHangUp");
        } else if (!b.a("key_gamebooster_hangup_ok", false)) {
            Intent intent2 = new Intent("com.miui.gamebooster.action.GAMEBOX_ALERT_ACTIVITY");
            intent2.putExtra("intent_gamebox_function_type", "intent_gamebox_func_type_hangup");
            intent2.putExtra("intent_gamebox_booster_pkg", str);
            intent2.addFlags(268435456);
            context.startActivity(intent2);
        } else if (str != null) {
            E.a(str, context);
            Log.i("GameBoxFunctionUtils", "setPackageHoldOn");
        }
    }

    public static void f(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.screenrecorder.RECORDER_SERVICE");
        intent.setPackage("com.miui.screenrecorder");
        intent.putExtra("is_start_immediately", true);
        if (C0393y.a(context, intent)) {
            context.startService(intent);
            return;
        }
        Toast.makeText(context, context.getString(R.string.screenrecord_not_find), 0).show();
        Log.i("GameBoxFunctionUtils", "startRecord_fail");
    }

    private static void g(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.miui.gamebooster.action.STOP_GAMEMODE");
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
