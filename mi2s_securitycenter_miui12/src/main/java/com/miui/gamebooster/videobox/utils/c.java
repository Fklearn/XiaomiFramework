package com.miui.gamebooster.videobox.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.E;
import com.miui.gamebooster.m.N;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.securitycenter.R;

public class c {
    public static void a(Context context) {
        new Handler().postDelayed(new b(context), 400);
    }

    public static boolean b(Context context) {
        N a2 = N.a(context);
        if (a2.d()) {
            return false;
        }
        if (a2.b()) {
            a2.a((int) R.string.vtb_stop_milink_connect);
            return true;
        }
        a2.f();
        return true;
    }

    public static void c(Context context) {
        String str = b.a("key_currentbooster_pkg_uid", (String) null).split(",")[0];
        if (N.a(context.getApplicationContext()).b() && C0388t.x()) {
            if (!f.j()) {
                Intent intent = new Intent("com.miui.gamebooster.action.GAMEBOX_ALERT_ACTIVITY");
                intent.putExtra("intent_gamebox_function_type", "intent_videobox_func_type_milink_hangup");
                intent.putExtra("intent_gamebox_booster_pkg", str);
                intent.addFlags(268435456);
                context.startActivity(intent);
            } else {
                C0384o.b(context.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$Secure", "SCREEN_PROJECT_HANG_UP"), 1, 0);
            }
            Log.i("FunctionUtils", "newHangUp");
        } else if (!f.i()) {
            Intent intent2 = new Intent("com.miui.gamebooster.action.GAMEBOX_ALERT_ACTIVITY");
            intent2.putExtra("intent_gamebox_function_type", "intent_videobox_func_type_hangup");
            intent2.putExtra("intent_gamebox_booster_pkg", str);
            intent2.addFlags(268435456);
            context.startActivity(intent2);
        } else if (str != null) {
            E.a(str, context);
            f.d(true);
            Log.i("FunctionUtils", "setPackageHoldOn");
        }
    }

    public static void d(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.screenrecorder.RECORDER_SERVICE");
        intent.setPackage("com.miui.screenrecorder");
        intent.putExtra("is_start_immediately", true);
        if (C0393y.a(context, intent)) {
            context.startService(intent);
            return;
        }
        Toast.makeText(context, context.getString(R.string.screenrecord_not_find), 0).show();
        Log.i("FunctionUtils", "startRecord_fail");
    }
}
