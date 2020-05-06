package com.miui.powercenter.autotask;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.miui.powercenter.bootshutdown.RepeatPreference;
import com.miui.securitycenter.R;
import miui.cloud.CloudPushConstants;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

/* renamed from: com.miui.powercenter.autotask.p  reason: case insensitive filesystem */
class C0487p extends C0485n {
    public C0487p(AutoTask autoTask, AutoTask autoTask2) {
        super(autoTask, autoTask2);
    }

    public void a(Bundle bundle) {
        ((C0488q) this.f6672c).addPreferencesFromResource(R.xml.pc_auto_task_edit_v12);
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

    public void b(String str) {
        if (str.equals("brightness")) {
            super.b(str);
            return;
        }
        DropDownPreference dropDownPreference = (DropDownPreference) ((C0488q) this.f6672c).findPreference(str);
        dropDownPreference.setOnPreferenceChangeListener(this);
        dropDownPreference.setTitle((CharSequence) X.a((Context) ((C0488q) this.f6672c).getActivity(), str));
        dropDownPreference.setIcon(X.a(str));
        X.a(((C0488q) this.f6672c).getContext(), this.f6671b, str, dropDownPreference);
        dropDownPreference.setVisible(true);
    }

    public void e(String str) {
        if (str.equals("brightness")) {
            super.e(str);
            return;
        }
        DropDownPreference dropDownPreference = (DropDownPreference) this.k.a((CharSequence) str);
        if (dropDownPreference != null) {
            X.a(dropDownPreference, this.f6671b, str);
            h();
        }
    }

    public void h() {
        if (b()) {
            this.f6671b.removeOperation("internet");
            DropDownPreference dropDownPreference = (DropDownPreference) this.k.a((CharSequence) "internet");
            if (dropDownPreference != null) {
                this.k.d(dropDownPreference);
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if (!X.b(key)) {
            return super.onPreferenceChange(preference, obj);
        }
        X.a(((C0488q) this.f6672c).getContext(), this.f6671b, key, obj, new C0486o(this));
        return true;
    }
}
