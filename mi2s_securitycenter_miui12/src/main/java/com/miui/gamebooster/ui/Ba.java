package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import b.b.c.c.b.d;
import b.b.c.j.g;
import com.miui.applicationlock.c.K;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.view.q;
import com.miui.gamebooster.view.r;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.gamebooster.widget.ValueSettingItemView;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miui.app.AlertDialog;

public class Ba extends d implements q, View.OnClickListener, CheckBoxSettingItemView.a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public ValueSettingItemView f4854a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public CheckBoxSettingItemView f4855b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public CheckBoxSettingItemView f4856c;

    /* renamed from: d  reason: collision with root package name */
    private ValueSettingItemView f4857d;
    /* access modifiers changed from: private */
    public View e;
    /* access modifiers changed from: private */
    public CheckBoxSettingItemView f;
    /* access modifiers changed from: private */
    public CheckBoxSettingItemView g;
    /* access modifiers changed from: private */
    public int h;
    /* access modifiers changed from: private */
    public IFeedbackControl i;
    private r j;
    /* access modifiers changed from: private */
    public IMiuiVpnManageService k;
    /* access modifiers changed from: private */
    public Boolean l = false;
    private a m;
    private LocalBroadcastManager n;
    private BroadcastReceiver o;
    private boolean p;
    private ServiceConnection q = new C0461ya(this);

    private static class a extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<Ba> f4858a;

        /* renamed from: b  reason: collision with root package name */
        private IFeedbackControl f4859b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f4860c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f4861d;
        private boolean e;

        private a(Ba ba) {
            this.f4860c = false;
            this.f4861d = false;
            this.e = false;
            this.f4858a = new WeakReference<>(ba);
        }

        /* synthetic */ a(Ba ba, C0459xa xaVar) {
            this(ba);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            Ba ba = (Ba) this.f4858a.get();
            if (ba == null || ba.isDetached() || isCancelled()) {
                return false;
            }
            if (1 == ba.h) {
                this.f4861d = com.miui.gamebooster.c.a.q(false);
            } else if (2 == ba.h) {
                try {
                    SettingsActivity settingsActivity = (SettingsActivity) ba.getActivity();
                    if (settingsActivity != null) {
                        this.f4859b = settingsActivity.l();
                        if (this.f4859b != null) {
                            this.f4861d = this.f4859b.p();
                        }
                    }
                } catch (RemoteException e2) {
                    Log.i("PerformanceSettingsFrag", e2.toString());
                }
            }
            this.f4860c = com.miui.gamebooster.c.a.e(true);
            this.e = com.miui.gamebooster.c.a.o(true);
            return true;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            Ba ba = (Ba) this.f4858a.get();
            if (ba != null && !ba.isDetached() && !isCancelled()) {
                IFeedbackControl unused = ba.i = this.f4859b;
                ba.f4855b.a(this.f4861d, false, false);
                if (!C0388t.n()) {
                    ba.f4856c.a(this.f4860c, false, false);
                }
                if (ba.f4854a.getVisibility() == 0) {
                    ba.f4854a.setValue(ba.getResources().getString(this.f4860c ? R.string.start : R.string.function_close));
                }
                if (ba.e.getVisibility() == 0) {
                    ba.f.a(this.e, false, false);
                    ba.g.setEnabled(this.e);
                }
            }
        }
    }

    private void f() {
        if (!com.miui.gamebooster.d.a.a() || !K.c(this.mAppContext) || !com.miui.gamebooster.c.a.y(false)) {
            this.e.setVisibility(8);
        } else {
            if (!this.p) {
                Intent intent = new Intent();
                intent.setPackage("com.miui.securitycenter");
                intent.setAction("com.miui.networkassistant.vpn.MIUI_VPN_MANAGE_SERVICE");
                this.p = g.a((Context) this.mActivity, intent, this.q, 1, UserHandle.OWNER);
            }
            this.e.setVisibility(0);
        }
        this.m = new a(this, (C0459xa) null);
        this.m.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void g() {
        if (this.o == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("gb_thermal_supported_action");
            this.o = new C0459xa(this);
            this.n = LocalBroadcastManager.getInstance(this.mActivity);
            this.n.registerReceiver(this.o, intentFilter);
        }
    }

    public void a(r rVar) {
        this.j = rVar;
    }

    public void e() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.thermal_tips_title).setMessage(R.string.thermal_tips_message).setPositiveButton(17039370, new Aa(this)).setNegativeButton(17039360, new C0463za(this)).setCancelable(false).create().show();
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.f4854a = (ValueSettingItemView) findViewById(R.id.performanceEnhanceSettingItem);
        this.f4854a.setOnClickListener(this);
        this.f4855b = (CheckBoxSettingItemView) findViewById(R.id.performanceOptimizationSettingItem);
        this.f4855b.setOnCheckedChangeListener(this);
        this.f4856c = (CheckBoxSettingItemView) findViewById(R.id.networkSettingItem);
        this.f4856c.setOnCheckedChangeListener(this);
        this.f4857d = (ValueSettingItemView) findViewById(R.id.memorySettingItem);
        this.f4857d.setOnClickListener(this);
        this.e = findViewById(R.id.xunyouSettingCategory);
        this.f = (CheckBoxSettingItemView) findViewById(R.id.xunyouSettingItem);
        this.f.setOnCheckedChangeListener(this);
        this.g = (CheckBoxSettingItemView) findViewById(R.id.xyWifiSettingItem);
        this.g.setOnCheckedChangeListener(this);
        com.miui.gamebooster.c.a.a((Context) this.mActivity);
        this.h = ((SettingsActivity) this.mActivity).n();
        if (this.h == 0) {
            this.f4855b.setVisibility(8);
        }
        (C0388t.n() ? this.f4856c : this.f4854a).setVisibility(8);
        g();
    }

    public void onCheckedChanged(View view, boolean z) {
        Activity activity;
        if (view == this.f4856c) {
            com.miui.gamebooster.c.a.D(z);
        } else if (view == this.f4855b) {
            if (!z || 1 != this.h) {
                int i2 = this.h;
                if (1 == i2) {
                    com.miui.gamebooster.c.a.W(z);
                } else if (2 == i2) {
                    try {
                        this.i = ((SettingsActivity) this.mActivity).l();
                        if (this.i != null) {
                            this.i.b(z);
                        }
                    } catch (Exception e2) {
                        Log.i("PerformanceSettingsFrag", e2.toString());
                    }
                }
            } else {
                e();
            }
        } else if (view == this.f) {
            com.miui.gamebooster.c.a.T(z);
            this.g.setEnabled(z);
        } else if (view == this.g) {
            try {
                this.k.setSettingEx("xunyou", "xunyou_wifi_accel_switch", String.valueOf(z));
                com.miui.gamebooster.c.a.U(z);
            } catch (Exception e3) {
                Log.i("PerformanceSettingsFrag", e3.toString());
            }
            if (z && (activity = this.mActivity) != null) {
                b.b.o.f.c.a.a((Context) activity).a(true);
            }
        }
    }

    public void onClick(View view) {
        r rVar;
        r rVar2;
        if (view == this.f4857d && (rVar2 = this.j) != null) {
            rVar2.a(new WhiteListFragment());
        } else if (view == this.f4854a && (rVar = this.j) != null) {
            rVar.a(new CompetitionDetailFragment());
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_performance_settings;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onDestroy() {
        BroadcastReceiver broadcastReceiver;
        ServiceConnection serviceConnection;
        Activity activity;
        super.onDestroy();
        a aVar = this.m;
        if (aVar != null) {
            aVar.cancel(true);
        }
        if (!(!this.p || (serviceConnection = this.q) == null || (activity = this.mActivity) == null)) {
            activity.unbindService(serviceConnection);
        }
        LocalBroadcastManager localBroadcastManager = this.n;
        if (localBroadcastManager != null && (broadcastReceiver = this.o) != null) {
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
        }
    }

    public void onResume() {
        super.onResume();
        f();
    }
}
