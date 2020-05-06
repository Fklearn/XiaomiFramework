package com.miui.powercenter.autotask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.preference.Preference;
import b.b.c.c.b.l;
import b.b.c.j.A;
import com.miui.maml.data.VariableNames;
import com.miui.powercenter.autotask.AutoTask;
import com.miui.powercenter.utils.s;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miui.app.Activity;
import miui.app.AlertDialog;
import miui.app.TimePickerDialog;
import miui.widget.NumberPicker;
import miuix.preference.RadioButtonPreference;
import miuix.preference.TextPreference;

public class ChooseConditionActivity extends B {

    /* renamed from: c  reason: collision with root package name */
    private a f6694c;

    /* renamed from: d  reason: collision with root package name */
    private View.OnClickListener f6695d;

    public static class a extends l implements Preference.c, Preference.b {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public int f6696a = 1410;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public int f6697b = 20;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public AutoTask.b f6698c = new AutoTask.b(1410, 420);

        /* renamed from: d  reason: collision with root package name */
        private Activity f6699d;
        private RadioButtonPreference e;
        /* access modifiers changed from: private */
        public RadioButtonPreference f;
        private RadioButtonPreference g;
        private TextPreference h;
        private TextPreference i;
        private TextPreference j;
        private TextPreference k;
        private AutoTask l;
        private String m;
        private int n;
        private NumberPicker.OnValueChangeListener o = new c(this);

        private void a(TimePickerDialog.OnTimeSetListener onTimeSetListener, int i2, int i3) {
            new TimePickerDialog(this.f6699d, onTimeSetListener, i2, i3, true).show();
        }

        /* access modifiers changed from: private */
        public void b() {
            AutoTask autoTask;
            Integer valueOf;
            String str;
            Intent intent = new Intent();
            this.l.removeAllConditions();
            if (this.e.isChecked()) {
                autoTask = this.l;
                valueOf = Integer.valueOf(this.f6696a);
                str = "hour_minute";
            } else if (this.f.isChecked()) {
                autoTask = this.l;
                valueOf = Integer.valueOf(this.f6697b);
                str = "battery_level_down";
            } else {
                if (this.g.isChecked()) {
                    autoTask = this.l;
                    valueOf = Integer.valueOf(this.f6698c.a());
                    str = "hour_minute_duration";
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("task", this.l);
                intent.putExtra("bundle", bundle);
                getActivity().setResult(-1, intent);
            }
            autoTask.setCondition(str, valueOf);
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("task", this.l);
            intent.putExtra("bundle", bundle2);
            getActivity().setResult(-1, intent);
        }

        /* access modifiers changed from: private */
        public boolean c() {
            if (!this.g.isChecked()) {
                return true;
            }
            AutoTask.b bVar = this.f6698c;
            return bVar.f6675a != bVar.f6676b;
        }

        private boolean d() {
            if (this.e.isChecked()) {
                if (!"hour_minute".equals(this.m)) {
                    return true;
                }
                return this.n != this.f6696a;
            } else if (this.f.isChecked()) {
                if (!"battery_level_down".equals(this.m)) {
                    return true;
                }
                return this.n != this.f6697b;
            } else if (!this.g.isChecked()) {
                return false;
            } else {
                if (!"hour_minute_duration".equals(this.m)) {
                    return true;
                }
                return this.n != this.f6698c.a();
            }
        }

        /* access modifiers changed from: private */
        public void e() {
            if (d()) {
                ea.a((Context) getActivity(), (DialogInterface.OnClickListener) new I(this));
            } else {
                getActivity().finish();
            }
        }

        private void f() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.f6699d);
            NumberPicker numberPicker = new NumberPicker(this.f6699d);
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(100);
            numberPicker.setLabel("%");
            numberPicker.setOnValueChangedListener(this.o);
            numberPicker.setValue(this.f6697b);
            builder.setView(numberPicker);
            builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            builder.show();
        }

        /* access modifiers changed from: private */
        public void g() {
            this.i.a(getString(R.string.percentage, new Object[]{Integer.valueOf(this.f6697b)}));
        }

        /* access modifiers changed from: private */
        public void h() {
            AutoTask.a hourMinute = AutoTask.getHourMinute(this.f6696a);
            this.h.a(s.a(hourMinute.f6673a, hourMinute.f6674b));
        }

        /* access modifiers changed from: private */
        public void i() {
            AutoTask.a hourMinute = AutoTask.getHourMinute(this.f6698c.f6675a);
            AutoTask.a hourMinute2 = AutoTask.getHourMinute(this.f6698c.f6676b);
            this.j.a(s.a(hourMinute.f6673a, hourMinute.f6674b));
            this.k.a(s.a(hourMinute2.f6673a, hourMinute2.f6674b));
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.f6699d = getActivity();
            addPreferencesFromResource(R.xml.pc_task_condition);
            this.e = (RadioButtonPreference) findPreference("ontime");
            this.h = (TextPreference) findPreference("ontime_time");
            this.f = (RadioButtonPreference) findPreference("battery_level_down");
            this.i = (TextPreference) findPreference(VariableNames.BATTERY_LEVEL);
            this.g = (RadioButtonPreference) findPreference("time_duration");
            this.j = (TextPreference) findPreference("time_start");
            this.k = (TextPreference) findPreference("time_end");
            this.e.setOnPreferenceClickListener(this);
            this.h.setOnPreferenceClickListener(this);
            this.f.setOnPreferenceClickListener(this);
            this.i.setOnPreferenceClickListener(this);
            this.g.setOnPreferenceClickListener(this);
            this.j.setOnPreferenceClickListener(this);
            this.k.setOnPreferenceClickListener(this);
            this.l = (AutoTask) getArguments().getParcelable("task");
            this.m = ea.a(this.l);
            Integer num = (Integer) this.l.getCondition(this.m);
            if (num != null) {
                this.n = num.intValue();
            }
            if ("battery_level_down".equals(this.m)) {
                this.f.setChecked(true);
                this.f6697b = ((Integer) this.l.getCondition(this.m)).intValue();
            } else if ("hour_minute".equals(this.m)) {
                this.e.setChecked(true);
                this.f6696a = ((Integer) this.l.getCondition(this.m)).intValue();
            } else if ("hour_minute_duration".equals(this.m)) {
                this.g.setChecked(true);
                this.f6698c.a(((Integer) this.l.getCondition(this.m)).intValue());
            } else {
                this.e.setChecked(true);
            }
            h();
            g();
            i();
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            return true;
        }

        public boolean onPreferenceClick(Preference preference) {
            AutoTask.a hourMinute;
            TimePickerDialog.OnTimeSetListener h2;
            if (preference == this.h) {
                hourMinute = AutoTask.getHourMinute(this.f6696a);
                h2 = new F(this);
            } else if (preference == this.i) {
                f();
                return false;
            } else if (preference == this.j) {
                hourMinute = AutoTask.getHourMinute(this.f6698c.f6675a);
                h2 = new G(this);
            } else if (preference != this.k) {
                return false;
            } else {
                hourMinute = AutoTask.getHourMinute(this.f6698c.f6676b);
                h2 = new H(this);
            }
            a(h2, hourMinute.f6673a, hourMinute.f6674b);
            return false;
        }
    }

    private static class b implements View.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<a> f6700a;

        private b(a aVar) {
            this.f6700a = new WeakReference<>(aVar);
        }

        /* JADX WARNING: type inference failed for: r1v1, types: [com.miui.powercenter.autotask.B, android.content.Context, com.miui.powercenter.autotask.ChooseConditionActivity] */
        public void onClick(View view) {
            a aVar = (a) this.f6700a.get();
            if (aVar != null) {
                ? r1 = (ChooseConditionActivity) aVar.getActivity();
                if (view == r1.f6691b) {
                    aVar.e();
                } else if (view != r1.f6690a) {
                } else {
                    if (!aVar.c()) {
                        A.a((Context) r1, (int) R.string.prompt_input_time_illegal);
                        return;
                    }
                    aVar.b();
                    aVar.getActivity().finish();
                }
            }
        }
    }

    private static class c implements NumberPicker.OnValueChangeListener {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<a> f6701a;

        private c(a aVar) {
            this.f6701a = new WeakReference<>(aVar);
        }

        public void onValueChange(NumberPicker numberPicker, int i, int i2) {
            a aVar = (a) this.f6701a.get();
            if (aVar != null && aVar.f.isChecked()) {
                int unused = aVar.f6697b = i2;
                aVar.g();
            }
        }
    }

    /* access modifiers changed from: protected */
    public View.OnClickListener l() {
        return this.f6695d;
    }

    /* access modifiers changed from: protected */
    public String m() {
        return getResources().getString(R.string.auto_task_edit_choose_condition_title);
    }

    /* access modifiers changed from: protected */
    public void n() {
        this.f6694c.e();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        this.f6694c = new a();
        this.f6694c.setArguments(getIntent().getBundleExtra("bundle"));
        this.f6695d = new b(this.f6694c);
        super.onCreate(bundle);
        getFragmentManager().beginTransaction().replace(16908290, this.f6694c, (String) null).commit();
    }
}
