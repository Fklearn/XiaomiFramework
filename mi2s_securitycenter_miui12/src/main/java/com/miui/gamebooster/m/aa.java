package com.miui.gamebooster.m;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;

class aa extends AsyncTask<Void, Void, Boolean> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Boolean f4472a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f4473b;

    aa(Boolean bool, Context context) {
        this.f4472a = bool;
        this.f4473b = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Boolean doInBackground(Void... voidArr) {
        return Boolean.valueOf(this.f4472a.booleanValue() || !Z.b(this.f4473b, (String) null));
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        if (!bool.booleanValue()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            Log.i("ShortcutHelper", "createSwitchUserShortcut");
            Z.a(this.f4473b, "com.miui.securitycenter:string/game_booster");
            Context context = this.f4473b;
            Toast.makeText(context, context.getResources().getString(R.string.create_gamebooster_destop), 0).show();
            return;
        }
        Context context2 = this.f4473b;
        context2.sendBroadcast(ba.b(context2, Constants.System.ACTION_INSTALL_SHORTCUT, "com.miui.securitycenter:string/game_booster"));
    }
}
