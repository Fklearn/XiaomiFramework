package com.miui.gamebooster.mutiwindow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.g;
import com.miui.gamebooster.m.C0375f;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.networkassistant.config.Constants;
import java.util.ArrayList;
import java.util.List;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private static i f4642a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static Context f4643b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Handler f4644c;

    /* renamed from: d  reason: collision with root package name */
    private b f4645d;

    private static class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private int f4646a;

        /* renamed from: b  reason: collision with root package name */
        private String f4647b;

        /* renamed from: c  reason: collision with root package name */
        private Context f4648c;

        public a(int i, String str, Context context) {
            this.f4646a = i;
            this.f4647b = str;
            this.f4648c = context.getApplicationContext();
        }

        public static List<String> a(Context context) {
            ArrayList<String> c2 = C0382m.c("gamebooster", "vtb_net_support_apps", context);
            return (c2 == null || c2.isEmpty()) ? C0382m.a("vtb_default_support_list", context.getApplicationContext()) : c2;
        }

        private void a(String str) {
            if (e.a() && f.b(i.f4643b) && a(this.f4648c).contains(str)) {
                ArrayList<String> a2 = f.a((ArrayList<String>) new ArrayList());
                ArrayList<String> a3 = com.miui.common.persistence.b.a("gb_added_games", (ArrayList<String>) new ArrayList());
                if (!a2.contains(str) && !a3.contains(str)) {
                    a2.add(str);
                    f.b(a2);
                    b("gb.action.update_video_list");
                }
            }
        }

        private void b(String str) {
            try {
                LocalBroadcastManager.getInstance(this.f4648c).sendBroadcast(new Intent(str));
            } catch (Exception unused) {
            }
        }

        public void run() {
            if (C0375f.a() || TextUtils.isEmpty(this.f4647b)) {
                return;
            }
            if (g.a(this.f4648c).a(this.f4647b, this.f4646a)) {
                Log.i("InstalledAppMonitor", "new app " + this.f4647b + " added to GameBox");
                return;
            }
            a(this.f4647b);
        }
    }

    private class b extends BroadcastReceiver {
        private b() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                if (Constants.System.ACTION_PACKAGE_ADDED.equals(action)) {
                    Uri data = intent.getData();
                    boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
                    if (data != null && !booleanExtra) {
                        int intExtra = intent.getIntExtra("android.intent.extra.UID", 0);
                        String schemeSpecificPart = data.getSchemeSpecificPart();
                        Log.i("InstalledAppMonitor", schemeSpecificPart + "onReceive add pkg=" + schemeSpecificPart);
                        if (i.this.f4644c != null) {
                            i.this.f4644c.post(new a(intExtra, schemeSpecificPart, context));
                        }
                    }
                } else if (Constants.System.ACTION_PACKAGE_REMOVED.equals(action)) {
                    Uri data2 = intent.getData();
                    boolean booleanExtra2 = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
                    if (data2 != null && !booleanExtra2) {
                        String schemeSpecificPart2 = data2.getSchemeSpecificPart();
                        Log.i("InstalledAppMonitor", schemeSpecificPart2 + "onReceive remove pkg=" + schemeSpecificPart2);
                        if (i.this.f4644c != null) {
                            i.this.f4644c.post(new c(schemeSpecificPart2, context));
                        }
                    }
                }
            }
        }
    }

    private static class c implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private String f4650a;

        /* renamed from: b  reason: collision with root package name */
        private Context f4651b;

        public c(String str, Context context) {
            this.f4650a = str;
            this.f4651b = context.getApplicationContext();
        }

        public void run() {
            if (!C0375f.a()) {
                ArrayList<String> a2 = f.a((ArrayList<String>) new ArrayList());
                if (!a2.isEmpty() && !TextUtils.isEmpty(this.f4650a) && a2.contains(this.f4650a)) {
                    a2.remove(this.f4650a);
                    f.b(a2);
                    try {
                        LocalBroadcastManager.getInstance(this.f4651b).sendBroadcast(new Intent("gb.action.update_video_list"));
                    } catch (Exception unused) {
                    }
                }
            }
        }
    }

    private i(Context context) {
        f4643b = context.getApplicationContext();
    }

    public static synchronized i a(Context context) {
        i iVar;
        synchronized (i.class) {
            if (f4642a == null) {
                f4642a = new i(context);
            }
            iVar = f4642a;
        }
        return iVar;
    }

    public void a(Handler handler) {
        this.f4644c = handler;
    }

    public void b() {
        try {
            if (!C0375f.a()) {
                this.f4645d = new b();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Constants.System.ACTION_PACKAGE_ADDED);
                intentFilter.addAction(Constants.System.ACTION_PACKAGE_REMOVED);
                intentFilter.addDataScheme("package");
                g.a(f4643b, (BroadcastReceiver) this.f4645d, UserHandle.CURRENT, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void c() {
        try {
            if (!C0375f.a()) {
                f4643b.unregisterReceiver(this.f4645d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
