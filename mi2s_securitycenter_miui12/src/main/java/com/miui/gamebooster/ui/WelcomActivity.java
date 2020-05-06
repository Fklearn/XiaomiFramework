package com.miui.gamebooster.ui;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import b.b.c.f.a;
import b.b.c.j.g;
import com.google.android.exoplayer2.C;
import com.miui.applicationlock.c.K;
import com.miui.applicationlock.c.o;
import com.miui.earthquakewarning.Constants;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0375f;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0389u;
import com.miui.gamebooster.m.C0390v;
import com.miui.gamebooster.m.Z;
import com.miui.gamebooster.m.ba;
import com.miui.gamebooster.m.ga;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.gamebooster.service.M;
import com.miui.gamebooster.service.MiuiVpnManageServiceCallback;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;
import com.miui.securitycenter.p;
import com.xiaomi.stat.MiStat;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import miui.app.Activity;
import miui.security.SecurityManager;

public class WelcomActivity extends b.b.c.c.a {

    /* renamed from: a  reason: collision with root package name */
    private SecurityManager f5018a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public IMiuiVpnManageService f5019b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f5020c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Handler f5021d;
    /* access modifiers changed from: private */
    public boolean e;
    /* access modifiers changed from: private */
    public b f;
    /* access modifiers changed from: private */
    public M g = M.NOTINIT;
    /* access modifiers changed from: private */
    public IGameBooster h;
    private ServiceConnection i = new Sa(this);
    a.C0027a j = new Ta(this);

    private static class a extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private Context f5022a;

        public a(Context context) {
            this.f5022a = context;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            boolean z;
            if (!com.miui.gamebooster.c.a.a()) {
                com.miui.gamebooster.c.a.a(this.f5022a);
                if (com.miui.gamebooster.c.a.e() && !Z.b(this.f5022a, (String) null)) {
                    z = true;
                    return Boolean.valueOf(z);
                }
            }
            z = false;
            return Boolean.valueOf(z);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool.booleanValue()) {
                ba.a(this.f5022a, (Boolean) false);
                com.miui.gamebooster.c.a.a(this.f5022a);
                com.miui.gamebooster.c.a.I(true);
            }
        }
    }

    private static class b extends MiuiVpnManageServiceCallback {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<WelcomActivity> f5023a;

        public b(WelcomActivity welcomActivity) {
            this.f5023a = new WeakReference<>(welcomActivity);
        }

        public void onVpnStateChanged(int i, int i2, String str) {
            super.onVpnStateChanged(i, i2, str);
            Activity activity = (WelcomActivity) this.f5023a.get();
            if (activity != null) {
                try {
                    com.miui.common.persistence.b.b("gb_xiaomi_id_md5_key", new String(Base64.encode(K.d(activity).getBytes(C.UTF8_NAME), 2), C.UTF8_NAME));
                } catch (UnsupportedEncodingException e) {
                    Log.i("WelcomActivity", e.toString());
                }
                int i3 = Va.f5014a[activity.g.ordinal()];
                if (i3 != 1) {
                    if (i3 == 2) {
                        if ((i2 == 102 && String.valueOf(5).equals(str)) || String.valueOf(3).equals(str)) {
                            com.miui.common.persistence.b.b("gamebooster_xunyou_cache_expire", false);
                            com.miui.gamebooster.c.a.ea(false);
                        } else if (i2 == 102 && (String.valueOf(4).equals(str) || String.valueOf(2).equals(str) || String.valueOf(6).equals(str))) {
                            com.miui.common.persistence.b.b("gamebooster_xunyou_cache_expire", true);
                            com.miui.common.persistence.b.b("gamebooster_xunyou_cache_user_type", str);
                        }
                        if (i2 == 1003) {
                            Long a2 = ga.a(str, "yyyy-MM-dd HH:mm:ss");
                            Log.i("WelcomActivity", "timestamp：" + ga.a(str, "yyyy-MM-dd HH:mm:ss"));
                            if (a2 != null) {
                                if (activity.h != null) {
                                    activity.h.K();
                                }
                                com.miui.common.persistence.b.b("gamebooster_xunyou_cache_time", a2.longValue());
                                if (a2.longValue() > System.currentTimeMillis() && com.miui.common.persistence.b.a("gamebooster_xunyou_cache_expire", true)) {
                                    com.miui.gamebooster.c.a.ea(true);
                                }
                            }
                        }
                        C0390v.a((Context) activity).a();
                    }
                    activity.finish();
                } else {
                    String unused = activity.f5020c = activity.f5019b.getSetting("detailUrl", (String) null);
                    M unused2 = activity.g = M.INIT;
                    activity.f5021d.post(new Wa(this, activity));
                    C0390v.a((Context) activity).a(activity.j);
                }
                Log.i("WelcomActivity", "VpnType:" + i + " " + "VpnState:" + i2 + " " + "Vpndata:" + str);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, miui.app.Activity, com.miui.gamebooster.ui.WelcomActivity] */
    private void a(Boolean bool, boolean z) {
        if (!bool.booleanValue() || p.a(Constants.SECURITY_ADD_PACKAGE) < 90806) {
            String stringExtra = getIntent().getStringExtra("track_gamebooster_enter_way");
            Class cls = GameBoosterRealMainActivity.class;
            if (stringExtra != null) {
                char c2 = 65535;
                switch (stringExtra.hashCode()) {
                    case 45806647:
                        if (stringExtra.equals("00007")) {
                            c2 = 0;
                            break;
                        }
                        break;
                    case 45806648:
                        if (stringExtra.equals("00008")) {
                            c2 = 1;
                            break;
                        }
                        break;
                    case 45806649:
                        if (stringExtra.equals("00009")) {
                            c2 = 2;
                            break;
                        }
                        break;
                }
                if (c2 == 0 || c2 == 1) {
                    cls = z ? SettingsActivity.class : GameBoosterSettingActivity.class;
                } else if (c2 == 2) {
                    cls = z ? SelectGameLandActivity.class : SelectGameActivity.class;
                }
            }
            Intent intent = new Intent(this, cls);
            if ((getIntent().getFlags() & 268435456) != 0) {
                intent.putExtra("track_channel", "channel_luncher");
            }
            if (bool.booleanValue()) {
                intent.putExtra("top", true);
            } else {
                intent.putExtra("top", false);
            }
            if (stringExtra != null) {
                intent.putExtra("track_gamebooster_enter_way", stringExtra);
            }
            startActivity(intent);
            finish();
            return;
        }
        m();
    }

    private void a(String str, String str2) {
        char c2 = 0;
        new a(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        if (str.hashCode() != -195603303 || !str.equals("gamebox")) {
            c2 = 65535;
        }
        if (c2 == 0) {
            Intent intent = new Intent("miui.gamebooster.action.GAMEBOX");
            if (!TextUtils.isEmpty(str2)) {
                intent.putExtra("caller_channel", str2);
            }
            intent.addFlags(268435456);
            startActivity(intent);
        }
        finish();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.gamebooster.ui.WelcomActivity] */
    private void l() {
        Intent intent = new Intent();
        intent.setPackage("com.miui.securitycenter");
        intent.setAction("com.miui.networkassistant.vpn.MIUI_VPN_MANAGE_SERVICE");
        g.a((Context) this, intent, this.i, 1, UserHandle.OWNER);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.gamebooster.ui.WelcomActivity, android.app.Activity] */
    private void m() {
        if (!K.c(this)) {
            if (!o.a(this.f5018a, "com.xiaomi.account")) {
                o.b(this.f5018a, "com.xiaomi.account");
            }
            K.a(this, new Bundle());
            finish();
            return;
        }
        l();
    }

    private void n() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String string = extras.getString("gamebooster_entrance", " ");
            String str = "noti_gameadd";
            if (!str.equals(string)) {
                str = "noti_gameopen";
                if (!str.equals(string)) {
                    return;
                }
            }
            C0373d.q(str, MiStat.Event.CLICK);
        }
    }

    private void o() {
        getWindow().getDecorView().setSystemUiVisibility(5894);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        WelcomActivity.super.onActivityResult(i2, i3, intent);
        if (i2 != 100) {
            finish();
            return;
        }
        try {
            this.f5019b.refreshUserState();
            this.g = M.GETREFRESHTIME;
        } catch (Exception e2) {
            Log.i("WelcomActivity", e2.toString());
        }
        this.f5021d.postDelayed(new Ua(this), 500);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.gamebooster.ui.WelcomActivity] */
    public void onCreate(Bundle bundle) {
        boolean z;
        super.onCreate(bundle);
        this.f5018a = (SecurityManager) getSystemService("security");
        this.f5021d = new Handler(Looper.getMainLooper());
        o();
        if (C0375f.a()) {
            C0375f.a(this);
        } else if (C0389u.a(this)) {
            C0389u.b(this);
        } else {
            n();
            com.miui.gamebooster.c.a.a((Context) this);
            this.e = com.miui.gamebooster.c.a.b() == 0 && C0388t.s();
            if ("com.miui.gamebooster.action.ACCESS_MAINACTIVITY".equals(getIntent().getAction())) {
                String stringExtra = getIntent().getStringExtra("jump_target");
                if (stringExtra != null && this.e) {
                    a(stringExtra, getIntent().getStringExtra("caller_channel"));
                    return;
                }
            } else if ("com.miui.gamebooster.action.MI_PUSH_GAMEBOOSTER_HOT".equals(getIntent().getAction())) {
                z = true;
                a(z, this.e);
                return;
            }
            z = false;
            a(z, this.e);
            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        WelcomActivity.super.onDestroy();
        IMiuiVpnManageService iMiuiVpnManageService = this.f5019b;
        if (iMiuiVpnManageService != null) {
            try {
                iMiuiVpnManageService.unregisterCallback(this.f);
                unbindService(this.i);
            } catch (Exception e2) {
                Log.i("WelcomActivity", "MiuiVpnServiceException:" + e2.toString());
            }
        }
    }

    public void onWindowFocusChanged(boolean z) {
        WelcomActivity.super.onWindowFocusChanged(z);
        if (z) {
            o();
        }
    }
}
