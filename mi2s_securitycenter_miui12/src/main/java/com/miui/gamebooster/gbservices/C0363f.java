package com.miui.gamebooster.gbservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.p.f;
import com.miui.securitycenter.R;

/* renamed from: com.miui.gamebooster.gbservices.f  reason: case insensitive filesystem */
class C0363f extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AntiMsgAccessibilityService f4353a;

    C0363f(AntiMsgAccessibilityService antiMsgAccessibilityService) {
        this.f4353a = antiMsgAccessibilityService;
    }

    private void a(Context context) {
        new C0362e(this, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("miui.intent.action.gb_show_window")) {
            Intent intent2 = (Intent) intent.getParcelableExtra("passby_intent");
            if (intent2 != null) {
                this.f4353a.b(intent2);
            }
        } else if (intent.getAction().equals("action_toast_booster_success")) {
            a(context);
        } else if (intent.getAction().equals("action_toast_booster_fail")) {
            if (this.f4353a.k) {
                f.a(context, this.f4353a.m).a();
                boolean unused = this.f4353a.k = false;
            }
        } else if (intent.getAction().equals("action_toast_wonderful_moment")) {
            C0373d.e();
            f.a(context, this.f4353a.m).a(context.getString(R.string.gb_game_end_toast), new C0361d(this, context, intent), 5000);
        }
    }
}
