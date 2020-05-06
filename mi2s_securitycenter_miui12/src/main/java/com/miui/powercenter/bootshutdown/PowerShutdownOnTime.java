package com.miui.powercenter.bootshutdown;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.powercenter.utils.u;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miui.app.ActionBar;
import miui.app.AlertDialog;
import miui.app.TimePickerDialog;
import miuix.preference.TextPreference;
import miuix.preference.s;

public class PowerShutdownOnTime extends b.b.c.c.a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public ImageView f6937a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public ImageView f6938b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f6939c;

    /* renamed from: d  reason: collision with root package name */
    View.OnClickListener f6940d = new d(this);

    public static class a extends s {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public PowerShutdownOnTime f6941a;

        /* renamed from: b  reason: collision with root package name */
        private CheckBoxPreference f6942b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public TextPreference f6943c;

        /* renamed from: d  reason: collision with root package name */
        private RepeatPreference f6944d;
        private CheckBoxPreference e;
        /* access modifiers changed from: private */
        public TextPreference f;
        private RepeatPreference g;
        /* access modifiers changed from: private */
        public int h;
        /* access modifiers changed from: private */
        public int i;
        private Preference.c j = new C0062a(this, (d) null);

        /* renamed from: com.miui.powercenter.bootshutdown.PowerShutdownOnTime$a$a  reason: collision with other inner class name */
        private static class C0062a implements Preference.c {

            /* renamed from: a  reason: collision with root package name */
            private final WeakReference<a> f6945a;

            private C0062a(a aVar) {
                this.f6945a = new WeakReference<>(aVar);
            }

            /* synthetic */ C0062a(a aVar, d dVar) {
                this(aVar);
            }

            /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
            /* JADX WARNING: type inference failed for: r3v1, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
            public boolean onPreferenceClick(Preference preference) {
                TimePickerDialog timePickerDialog;
                int i;
                int i2;
                a aVar = (a) this.f6945a.get();
                if (aVar == null) {
                    return false;
                }
                if (preference == aVar.f) {
                    timePickerDialog = new TimePickerDialog(aVar.f6941a, new h(this, aVar), 0, 0, true);
                    i = aVar.i / 60;
                    i2 = aVar.i;
                } else {
                    timePickerDialog = new TimePickerDialog(aVar.f6941a, new i(this, aVar), 0, 0, true);
                    i = aVar.h / 60;
                    i2 = aVar.h;
                }
                timePickerDialog.updateTime(i, i2 % 60);
                timePickerDialog.show();
                return false;
            }
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
        /* JADX WARNING: type inference failed for: r0v1, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
        /* access modifiers changed from: private */
        public void a() {
            a.c(this.f6941a);
            a.d(this.f6941a);
        }

        /* JADX WARNING: type inference failed for: r0v3, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
        private boolean b() {
            if (!y.m()) {
                return false;
            }
            if (a.a()) {
                return true;
            }
            a.a(this.f6941a);
            return false;
        }

        /* access modifiers changed from: private */
        public boolean c() {
            return !this.f6942b.isChecked() || !this.e.isChecked() || this.h != this.i;
        }

        /* JADX WARNING: type inference failed for: r0v5, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
        private boolean d() {
            if (!y.r()) {
                return false;
            }
            if (y.q() >= System.currentTimeMillis() || y.s() != 0) {
                return true;
            }
            a.b(this.f6941a);
            return false;
        }

        private void e() {
            this.f = (TextPreference) findPreference("time_shutdown");
            this.f6943c = (TextPreference) findPreference("time_boot");
            this.f.setOnPreferenceClickListener(this.j);
            this.f6943c.setOnPreferenceClickListener(this.j);
            this.f6942b = (CheckBoxPreference) findPreference("button_boot");
            this.e = (CheckBoxPreference) findPreference("button_shutdown");
            this.f6944d = (RepeatPreference) findPreference("repeat_boot");
            this.g = (RepeatPreference) findPreference("repeat_shutdown");
        }

        private int f() {
            return this.f6944d.d().b();
        }

        private int g() {
            return this.g.d().b();
        }

        private void h() {
            this.h = y.o();
            this.i = y.t();
        }

        /* JADX WARNING: type inference failed for: r1v7, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
        /* JADX WARNING: type inference failed for: r1v12, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
        private void i() {
            this.f6942b.setChecked(b());
            this.e.setChecked(d());
            this.f6943c.a(u.b(this.h));
            this.f.a(u.b(this.i));
            c cVar = new c(y.n());
            this.f6944d.a(cVar.a((Context) this.f6941a, true));
            this.f6944d.a(cVar);
            this.f6944d.setOnPreferenceClickListener(new f(this));
            c cVar2 = new c(y.s());
            this.g.a(cVar2.a((Context) this.f6941a, true));
            this.g.a(cVar2);
            this.g.setOnPreferenceClickListener(new g(this));
        }

        /* access modifiers changed from: private */
        public boolean j() {
            return (this.f6942b.isChecked() == y.m() && this.e.isChecked() == y.r() && this.h == y.o() && this.i == y.t() && f() == y.n() && g() == y.s()) ? false : true;
        }

        /* access modifiers changed from: private */
        public void k() {
            y.d(this.f6942b.isChecked());
            y.e(this.e.isChecked());
            y.f(this.h);
            y.h(this.i);
            y.e(f());
            y.g(g());
        }

        /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
        /* access modifiers changed from: private */
        public void l() {
            e eVar = new e(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.f6941a);
            builder.setTitle(R.string.power_customize_giveup_change);
            builder.setPositiveButton(R.string.power_dialog_ok, eVar);
            builder.setNegativeButton(R.string.power_dialog_cancel, eVar);
            builder.show();
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.f6941a = (PowerShutdownOnTime) getActivity();
            addPreferencesFromResource(R.xml.pc_power_shutdown_on_time);
            e();
            h();
            i();
        }

        public void onCreatePreferences(Bundle bundle, String str) {
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.Activity, com.miui.powercenter.bootshutdown.PowerShutdownOnTime] */
    private void l() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(getTitle());
            this.f6937a = new ImageView(this);
            this.f6937a.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.f6937a.setImageResource(miui.R.drawable.action_mode_immersion_done_light);
            actionBar.setEndView(this.f6937a);
            this.f6937a.setOnClickListener(this.f6940d);
            this.f6938b = new ImageView(this);
            this.f6938b.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.f6938b.setImageResource(miui.R.drawable.action_mode_immersion_close_light);
            actionBar.setStartView(this.f6938b);
            this.f6938b.setOnClickListener(this.f6940d);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        a aVar = new a();
        this.f6939c = aVar;
        beginTransaction.replace(16908290, aVar).commit();
        l();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4 && this.f6939c.j()) {
            this.f6939c.l();
        }
        return PowerShutdownOnTime.super.onKeyDown(i, keyEvent);
    }
}
