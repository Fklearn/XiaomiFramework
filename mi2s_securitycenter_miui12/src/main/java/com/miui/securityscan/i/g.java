package com.miui.securityscan.i;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import b.b.c.j.n;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.q;
import com.miui.securityscan.scanner.ScoreManager;
import miui.app.AlertDialog;

public class g {
    public static String a() {
        Application d2 = Application.d();
        return n.d(d2, h.b((Context) d2), 0);
    }

    public static String a(long j) {
        return n.d(Application.d(), j, 0);
    }

    public static AlertDialog a(Activity activity, String str, String str2, String str3, String str4, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickListener2) {
        try {
            AlertDialog.Builder message = new AlertDialog.Builder(activity).setCancelable(false).setTitle(str).setMessage(str2);
            message.setPositiveButton(str3, onClickListener);
            message.setNegativeButton(str4, onClickListener2);
            return message.show();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean b() {
        return h.b((Context) Application.d()) > 0;
    }

    public static boolean c() {
        q.a a2 = q.a((Context) Application.d());
        return (((float) a2.f7741b) * 1.0f) / ((float) a2.f7740a) < 0.2f;
    }

    public static boolean d() {
        return ScoreManager.e().j() < 75;
    }
}
