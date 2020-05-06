package com.miui.gamebooster.m;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.service.GameBoosterService;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.securitycenter.R;

public class G {
    public static void a(boolean z) {
        a.M(z);
    }

    public static void a(boolean z, @Nullable Activity activity) {
        if (a(activity)) {
            if (z) {
                ba.a((Context) activity, (Boolean) true);
            } else {
                ba.a((Context) activity, (String) null);
            }
        }
    }

    public static void a(boolean z, @Nullable Activity activity, @Nullable IGameBooster iGameBooster) {
        if (a(activity)) {
            if (!z) {
                Toast.makeText(activity, R.string.already_close_gamebooster, 0).show();
            } else {
                activity.startService(new Intent(activity, GameBoosterService.class));
            }
            a.L(z);
            if (iGameBooster != null) {
                try {
                    iGameBooster.A();
                } catch (RemoteException e) {
                    Log.e("GlobalSettingsHelper", e.toString());
                }
            }
        }
    }

    private static boolean a(@Nullable Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }
}
