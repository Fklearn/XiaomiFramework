package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.service.r;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;

public class E extends m {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public boolean f4321a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f4322b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public IFeedbackControl f4323c;

    /* renamed from: d  reason: collision with root package name */
    private int f4324d;
    private Context e;
    /* access modifiers changed from: private */
    public r f;
    private ServiceConnection g = new D(this);

    public E(Context context, r rVar) {
        this.e = context;
        this.f = rVar;
    }

    private void f() {
        IFeedbackControl iFeedbackControl = this.f4323c;
        if (iFeedbackControl != null) {
            try {
                iFeedbackControl.c(1, this.f.a());
            } catch (RemoteException e2) {
                Log.i("GameBoosterService", e2.toString());
            }
        } else {
            Intent intent = new Intent();
            intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.feedbackcontrol.FeedbackControlService");
            this.f4322b = this.e.bindService(intent, this.g, 1);
        }
    }

    private void g() {
        IFeedbackControl iFeedbackControl = this.f4323c;
        if (iFeedbackControl != null) {
            try {
                iFeedbackControl.G();
            } catch (RemoteException e2) {
                Log.i("GameBoosterService", e2.toString());
            }
            if (this.f4322b) {
                this.e.unbindService(this.g);
                this.f4322b = false;
            }
        }
    }

    public void a() {
        if (!this.f4321a) {
            return;
        }
        if (this.f4324d == 1) {
            g();
            Log.i("GameBoosterService", "mThermalMode...stop");
            return;
        }
        Log.i("GameBoosterService", "mIsPerformance...stop");
    }

    public boolean b() {
        return this.f.g() == 1;
    }

    public void c() {
        if (!this.f4321a) {
            return;
        }
        if (this.f4324d == 1) {
            f();
            Log.i("GameBoosterService", "mThermalMode...start ");
            return;
        }
        Log.i("GameBoosterService", "mIsPerformance...start ");
    }

    public void d() {
        a.a(this.e);
        this.f4321a = a.q(false);
        this.f4324d = this.f.g();
        Log.i("GameBoosterService", "initservice mThermalMode:" + this.f4324d);
    }

    public int e() {
        return 9;
    }
}
