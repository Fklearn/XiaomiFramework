package com.miui.powercenter.autotask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.miui.powercenter.autotask.TextEditPreference;
import com.miui.powercenter.autotask.X;
import com.miui.powercenter.bootshutdown.RepeatPreference;
import com.miui.powercenter.bootshutdown.c;
import com.miui.securitycenter.R;
import java.util.List;
import miui.cloud.CloudPushConstants;
import miuix.preference.TextPreference;

/* renamed from: com.miui.powercenter.autotask.n  reason: case insensitive filesystem */
class C0485n extends A<C0488q> implements Preference.b, Preference.c {

    /* renamed from: d  reason: collision with root package name */
    public TextEditPreference f6761d;
    public TextPreference e;
    public TextPreference f;
    public TextPreference g;
    public RepeatPreference h;
    public CheckBoxPreference i;
    public PreferenceCategory j;
    public PreferenceCategory k;

    public C0485n(AutoTask autoTask, AutoTask autoTask2) {
        super(autoTask, autoTask2);
        this.f6670a = autoTask;
        this.f6671b = autoTask2;
    }

    public void a() {
        this.i.setTitle((int) R.string.auto_task_condition_restore_task_when_charged);
        this.i.setKey("key_restore_when_charged");
        this.i.setIcon((int) R.drawable.battery_selector);
        this.i.setChecked(this.f6671b.getRestoreLevel() != 0);
        this.i.setOnPreferenceChangeListener(this);
        this.i.setVisible(true);
    }

    public void a(int i2, int i3, Intent intent) {
        AutoTask autoTask;
        if (i2 == 1 && i3 == -1) {
            AutoTask autoTask2 = (AutoTask) intent.getBundleExtra("bundle").getParcelable("task");
            if (autoTask2 != null) {
                this.f6671b.removeAllConditions();
                for (String next : autoTask2.getConditionNames()) {
                    this.f6671b.setCondition(next, autoTask2.getCondition(next));
                }
                this.f6761d.a(ea.a((Context) ((C0488q) this.f6672c).getActivity(), this.f6671b));
                g();
            }
        } else if (i2 == 2 && i3 == -1 && (autoTask = (AutoTask) intent.getBundleExtra("bundle").getParcelable("task")) != null) {
            List<String> operationNames = autoTask.getOperationNames();
            this.f6671b.removeAllOperations();
            for (String next2 : operationNames) {
                this.f6671b.setOperation(next2, autoTask.getOperation(next2));
            }
            i();
        }
    }

    public void a(Bundle bundle) {
        ((C0488q) this.f6672c).addPreferencesFromResource(R.xml.pc_auto_task_edit);
        this.j = (PreferenceCategory) ((C0488q) this.f6672c).findPreference("conditions_category");
        this.k = (PreferenceCategory) ((C0488q) this.f6672c).findPreference("operations_category");
        this.j.setTitle((CharSequence) ((C0488q) this.f6672c).getActivity().getString(R.string.auto_task_edit_step1) + " " + ((C0488q) this.f6672c).getActivity().getString(R.string.auto_task_edit_choose_condition_title));
        this.k.setTitle((CharSequence) ((C0488q) this.f6672c).getActivity().getString(R.string.auto_task_edit_step2) + " " + ((C0488q) this.f6672c).getActivity().getString(R.string.auto_task_edit_choose_operation_title));
        this.f6761d = (TextEditPreference) ((C0488q) this.f6672c).findPreference(CloudPushConstants.XML_NAME);
        this.e = (TextPreference) ((C0488q) this.f6672c).findPreference("add_new_condition");
        this.e.setOnPreferenceClickListener(this);
        this.f = (TextPreference) ((C0488q) this.f6672c).findPreference("condition_custom");
        this.f.setOnPreferenceClickListener(this);
        this.g = (TextPreference) ((C0488q) this.f6672c).findPreference("operation_custom_view");
        this.g.setOnPreferenceClickListener(this);
        this.h = (RepeatPreference) ((C0488q) this.f6672c).findPreference("condition_custom_repeat");
        this.h.setOnPreferenceClickListener(this);
        this.i = (CheckBoxPreference) ((C0488q) this.f6672c).findPreference("key_restore_when_charged");
    }

    public void a(String str) {
        int i2;
        TextPreference textPreference;
        this.f.setKey(str);
        if ("battery_level_down".equals(str) || "battery_level_up".equals(str)) {
            textPreference = this.f;
            i2 = R.drawable.battery_selector;
        } else if ("hour_minute".equals(str)) {
            textPreference = this.f;
            i2 = R.drawable.on_time_task_selector;
        } else if ("hour_minute_duration".equals(str)) {
            textPreference = this.f;
            i2 = R.drawable.time_duration_task_selector;
        } else {
            return;
        }
        textPreference.setIcon(i2);
        Integer num = (Integer) this.f6671b.getCondition(str);
        if (num != null) {
            this.f.setTitle((CharSequence) ea.a((Context) ((C0488q) this.f6672c).getActivity(), str, (Object) num));
            this.f.setVisible(true);
        }
    }

    public void b(Bundle bundle) {
        ((C0488q) this.f6672c).getActivity().getWindow().setSoftInputMode(32);
        this.f6761d.a(ea.a((Context) ((C0488q) this.f6672c).getActivity(), this.f6671b));
        g();
        i();
        this.f6761d.a((TextEditPreference.a) new C0481j(this));
    }

    public void b(String str) {
        TextPreference textPreference = (TextPreference) ((C0488q) this.f6672c).findPreference(str);
        textPreference.setOnPreferenceClickListener(this);
        textPreference.setTitle((CharSequence) X.a((Context) ((C0488q) this.f6672c).getActivity(), str));
        textPreference.setIcon(X.a(str));
        X.a(textPreference, this.f6671b, str);
        textPreference.setVisible(true);
    }

    public boolean b() {
        if (this.f6671b.hasOperation("airplane_mode")) {
            return ((Integer) this.f6671b.getOperation("airplane_mode")).intValue() == 1;
        }
        return false;
    }

    public void c() {
        X.a((Context) ((C0488q) this.f6672c).getActivity(), this.f6671b, (X.a) new C0484m(this));
    }

    public void c(String str) {
        RepeatPreference repeatPreference;
        int i2;
        this.h.setTitle((int) R.string.power_center_repeat);
        this.h.setKey("key_repeat_type");
        c cVar = new c(this.f6671b.getRepeatType());
        this.h.a(cVar.a((Context) ((C0488q) this.f6672c).getActivity(), true));
        this.h.a(cVar);
        this.h.setOnPreferenceChangeListener(this);
        if ("hour_minute".equals(str)) {
            repeatPreference = this.h;
            i2 = R.drawable.on_time_task_selector;
        } else {
            if ("hour_minute_duration".equals(str)) {
                repeatPreference = this.h;
                i2 = R.drawable.time_duration_task_selector;
            }
            this.h.setVisible(true);
        }
        repeatPreference.setIcon(i2);
        this.h.setVisible(true);
    }

    public void d() {
        Intent intent = new Intent(((C0488q) this.f6672c).getActivity(), ChooseConditionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", this.f6671b);
        intent.putExtra("bundle", bundle);
        ((C0488q) this.f6672c).startActivityForResult(intent, 1, (Bundle) null);
    }

    public void d(String str) {
        X.a((Context) ((C0488q) this.f6672c).getActivity(), this.f6671b, str, (X.a) new C0482k(this));
    }

    public void e() {
        Intent intent = new Intent(((C0488q) this.f6672c).getActivity(), OperationEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", this.f6671b);
        intent.putExtra("bundle", bundle);
        ((C0488q) this.f6672c).startActivityForResult(intent, 2);
    }

    public void e(String str) {
        TextPreference textPreference = (TextPreference) this.k.a((CharSequence) str);
        if (textPreference != null) {
            X.a(textPreference, this.f6671b, str);
            h();
        }
    }

    public void f() {
        X.b(((C0488q) this.f6672c).getActivity(), this.f6671b, new C0483l(this));
    }

    public void g() {
        String a2 = ea.a(this.f6671b);
        if (!TextUtils.isEmpty(a2)) {
            this.e.setVisible(false);
            this.f.setVisible(false);
            this.h.setVisible(false);
            this.i.setVisible(false);
            a(a2);
            if ("hour_minute".equals(a2) || "hour_minute_duration".equals(a2)) {
                c(a2);
            } else if ("battery_level_down".equals(a2) || "battery_level_up".equals(a2)) {
                a();
            }
        }
    }

    public void h() {
        if (b()) {
            this.f6671b.removeOperation("internet");
            TextPreference textPreference = (TextPreference) this.k.a((CharSequence) "internet");
            if (textPreference != null) {
                this.k.d(textPreference);
            }
        }
    }

    public void i() {
        if (b()) {
            this.f6671b.removeOperation("internet");
        }
        List<String> operationNames = this.f6671b.getOperationNames();
        if (!operationNames.isEmpty()) {
            for (String b2 : operationNames) {
                b(b2);
            }
        }
        this.g.setOnPreferenceClickListener(this);
        this.g.setKey("key_add_new_operations");
        this.g.setTitle((int) R.string.auto_task_edit_choose_new_operation);
        this.g.setSummary((int) R.string.auto_task_edit_operation_summary);
        this.g.setIcon((int) R.drawable.ic_button_add);
        this.g.setVisible(true);
        this.k.b((Preference) this.g);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("key_repeat_type".equals(preference.getKey())) {
            this.f6671b.setRepeatType(((c) obj).b());
            return true;
        } else if (!"key_restore_when_charged".equals(preference.getKey())) {
            return false;
        } else {
            if (((Boolean) obj).booleanValue()) {
                this.f6671b.setRestoreLevel(1);
            } else {
                this.f6671b.setRestoreLevel(0);
            }
            return true;
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (!"add_new_condition".equals(key)) {
            if ("key_add_new_operations".equals(key)) {
                e();
                return false;
            } else if (!"battery_level_down".equals(key) && !"battery_level_up".equals(key) && !"hour_minute".equals(key) && !"hour_minute_duration".equals(key)) {
                if (X.b(key)) {
                    d(key);
                    return false;
                } else if ("auto_clean_memory".equals(key)) {
                    f();
                    return false;
                } else if (!"brightness".equals(key)) {
                    return false;
                } else {
                    c();
                    return false;
                }
            }
        }
        d();
        return false;
    }
}
