package com.miui.gamebooster.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.e;
import com.miui.gamebooster.view.IncomingCallFloatBall;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.securitycenter.R;

public class GameBoosterTelecomManager extends Service {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Handler f4763a = new Handler();
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public boolean f4764b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public b f4765c = null;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public PhoneStateListener f4766d = new v(this);
    private a e;

    private class a extends BroadcastReceiver {
        private a() {
        }

        /* synthetic */ a(GameBoosterTelecomManager gameBoosterTelecomManager, u uVar) {
            this();
        }

        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra(AdvancedSlider.STATE, 0);
            String stringExtra = intent.getStringExtra("incomingNumber");
            if (GameBoosterTelecomManager.this.f4766d != null) {
                GameBoosterTelecomManager.this.f4766d.onCallStateChanged(intExtra, stringExtra);
            }
        }
    }

    private static class b implements IncomingCallFloatBall.a {

        /* renamed from: a  reason: collision with root package name */
        private Handler f4768a = new w(this);
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public String f4769b;

        /* renamed from: c  reason: collision with root package name */
        private String f4770c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public Context f4771d;
        /* access modifiers changed from: private */
        public long e;
        /* access modifiers changed from: private */
        public IncomingCallFloatBall f;
        /* access modifiers changed from: private */
        public boolean g = false;
        /* access modifiers changed from: private */
        public boolean h = false;

        public b(Context context, String str) {
            this.f4771d = context;
            this.f4769b = str;
        }

        public String a(long j) {
            Resources resources = this.f4771d.getResources();
            if (j < 0) {
                return resources.getQuantityString(R.plurals.game_booster_incoming_call_duration_p1, 0, new Object[]{"--"}) + resources.getQuantityString(R.plurals.game_booster_incoming_call_duration_p2, 0, new Object[]{"--"});
            }
            long j2 = j / 1000;
            int i = (int) (j2 / 60);
            int i2 = (int) (j2 % 60);
            return resources.getQuantityString(R.plurals.game_booster_incoming_call_duration_p1, i, new Object[]{String.valueOf(i)}) + resources.getQuantityString(R.plurals.game_booster_incoming_call_duration_p2, i2, new Object[]{String.format("%02d", new Object[]{Integer.valueOf(i2)})});
        }

        public void a() {
            try {
                e.a((Object) (TelecomManager) this.f4771d.getSystemService("telecom"), "endCall", (Class<?>[]) null, new Object[0]);
            } catch (Exception e2) {
                Log.i("GameBoosterReflectUtils", e2.toString());
            }
        }

        public void a(String str) {
            this.f4770c = str;
        }

        public void b() {
            if (this.h) {
                this.h = false;
                this.f.a();
                this.f4768a.removeMessages(1);
            }
        }

        public void b(long j) {
            this.e = j;
        }

        public void c() {
            new x(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }

        public void d() {
            if (!this.h) {
                this.f = IncomingCallFloatBall.a(this.f4771d);
                this.f.setCallerName(TextUtils.isEmpty(this.f4770c) ? this.f4769b : this.f4770c);
                this.f.setCallDuration(a(SystemClock.uptimeMillis() - this.e));
                this.f.setOnHangUpClickListener(this);
                this.f.d();
                this.f4768a.sendEmptyMessageDelayed(1, 500);
                this.h = true;
            }
        }
    }

    public void a() {
        Log.d("GameBoosterTeleManager", "onEnterGameBoosterMode");
        this.f4764b = true;
        ((TelephonyManager) getSystemService("phone")).listen(this.f4766d, 32);
        if (com.miui.gamebooster.c.b.f4100a) {
            this.e = new a(this, (u) null);
            registerReceiver(this.e, new IntentFilter("com.miui.gamebooster.service.DEBUG_INCOMING_CALL"));
        }
    }

    public void b() {
        Log.d("GameBoosterTeleManager", "onQuitGameBoosterMode");
        this.f4764b = false;
        b bVar = this.f4765c;
        if (bVar != null) {
            bVar.b();
            this.f4765c = null;
        }
    }

    public IBinder onBind(Intent intent) {
        Log.d("GameBoosterTeleManager", "onBind");
        return new u(this);
    }

    public void onCreate() {
        super.onCreate();
        Log.d("GameBoosterTeleManager", "onCreate");
    }

    public void onDestroy() {
        a aVar;
        super.onDestroy();
        Log.d("GameBoosterTeleManager", "onDestroy");
        ((TelephonyManager) getSystemService("phone")).listen(this.f4766d, 0);
        if (com.miui.gamebooster.c.b.f4100a && (aVar = this.e) != null) {
            unregisterReceiver(aVar);
        }
        b bVar = this.f4765c;
        if (bVar != null) {
            bVar.b();
            this.f4765c = null;
        }
    }
}
