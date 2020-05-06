package com.miui.gamebooster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import b.b.c.c.a;
import b.b.c.f.a;
import b.b.c.j.i;
import com.miui.gamebooster.m.C0390v;
import com.miui.gamebooster.m.Q;
import com.miui.gamebooster.m.fa;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.securitycenter.R;

public class SettingsActivity extends a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public IGameBooster f4998a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public IFeedbackControl f4999b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f5000c = 0;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public LocalBroadcastManager f5001d;
    private a.C0027a e = new Qa(this);
    private ServiceConnection f = new Ra(this);

    private void o() {
        if (Build.VERSION.SDK_INT >= 28 && i.e()) {
            getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new Pa(this));
        }
    }

    public IFeedbackControl l() {
        return this.f4999b;
    }

    public IGameBooster m() {
        return this.f4998a;
    }

    public int n() {
        return this.f5000c;
    }

    public void onBackPressed() {
        SettingsActivity.super.onBackPressed();
        overridePendingTransition(17432578, 17432579);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, miui.app.Activity, com.miui.gamebooster.ui.SettingsActivity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(Build.VERSION.SDK_INT == 26 ? R.style.GameLandscape2 : R.style.GameLandscape);
        super.onCreate(bundle);
        Q.a((Activity) this);
        setContentView(R.layout.activity_game_booster_settings);
        na.a((Activity) this);
        this.f5001d = LocalBroadcastManager.getInstance(this);
        C0390v.a((Context) this).a(this.e);
        Intent intent = new Intent();
        intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.feedbackcontrol.FeedbackControlService");
        bindService(intent, this.f, 1);
        fa.a(this);
        o();
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, miui.app.Activity, com.miui.gamebooster.ui.SettingsActivity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        C0390v.a((Context) this).a();
        ServiceConnection serviceConnection = this.f;
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        SettingsActivity.super.onDestroy();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [miui.app.Activity, com.miui.gamebooster.ui.SettingsActivity, android.app.Activity] */
    public void onWindowFocusChanged(boolean z) {
        SettingsActivity.super.onWindowFocusChanged(z);
        if (z) {
            na.a((Activity) this);
        }
    }
}
