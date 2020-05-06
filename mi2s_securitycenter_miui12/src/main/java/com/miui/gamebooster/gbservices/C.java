package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.service.GameBoosterTelecomManager;
import com.miui.gamebooster.service.IGameBoosterTelecomeManager;
import com.miui.gamebooster.service.r;

public class C extends m {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4316a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f4317b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f4318c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f4319d;
    private boolean e;
    private boolean f;
    /* access modifiers changed from: private */
    public r g;
    private AudioManager h;
    /* access modifiers changed from: private */
    public IGameBoosterTelecomeManager i;
    private Runnable j = new y(this);
    private PhoneStateListener k = new A(this);
    /* access modifiers changed from: private */
    public ServiceConnection l = new B(this);

    public C(Context context, r rVar) {
        this.f4317b = context;
        this.g = rVar;
        this.h = (AudioManager) this.f4317b.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
    }

    private void a(long j2) {
        this.g.c().postDelayed(this.j, j2);
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        this.e = z;
        this.h.adjustStreamVolume(3, this.e ? -100 : 100, 0);
    }

    /* access modifiers changed from: private */
    public void f() {
        Log.i("GameBoosterService", "mHandsFree...stop ");
        if (this.f) {
            ((TelephonyManager) this.f4317b.getSystemService("phone")).listen(this.k, 0);
            this.f = false;
        }
    }

    /* access modifiers changed from: private */
    public boolean g() {
        return this.h.getStreamVolume(3) == 0;
    }

    public void a() {
        Settings.Secure.putInt(this.f4317b.getContentResolver(), "gb_handsfree", 0);
        if (this.f4316a) {
            int i2 = Build.VERSION.SDK_INT;
            a((i2 == 21 || i2 == 22) ? 3000 : 0);
            if (this.e) {
                a(false);
            }
            this.g.c().post(new z(this));
        }
    }

    public boolean b() {
        return true;
    }

    public void c() {
        if (this.f4316a) {
            Log.i("GameBoosterService", "mHandsFree...start ");
            this.g.c().removeCallbacks(this.j);
            Settings.Secure.putInt(this.f4317b.getContentResolver(), "gb_handsfree", 1);
            Intent intent = new Intent(this.f4317b, GameBoosterTelecomManager.class);
            this.f4317b.startService(intent);
            this.f4317b.bindService(intent, this.l, 0);
            this.f = true;
            ((TelephonyManager) this.f4317b.getSystemService("phone")).listen(this.k, 32);
        }
    }

    public void d() {
        this.f4316a = a.l(true);
    }

    public int e() {
        return 6;
    }
}
