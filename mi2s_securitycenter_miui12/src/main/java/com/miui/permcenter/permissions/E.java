package com.miui.permcenter.permissions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.miui.permission.PermissionInfo;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.preference.s;

public class E extends s {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.pm_fragment_permissions);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        int intExtra = getActivity().getIntent().getIntExtra("extra_group_type", -1);
        ArrayList parcelableArrayListExtra = getActivity().getIntent().getParcelableArrayListExtra("extra_permission_list");
        if (intExtra == -1 || parcelableArrayListExtra == null) {
            getActivity().finish();
        }
        if (intExtra == 1) {
            getActivity().setTitle(R.string.SMS_and_MMS);
        } else if (intExtra == 2) {
            getActivity().setTitle(R.string.call_and_contact);
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.e();
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        if (intExtra == 1) {
            preferenceCategory.setTitle((int) R.string.SMS_and_MMS);
        } else if (intExtra == 2) {
            preferenceCategory.setTitle((int) R.string.call_and_contact);
        }
        preferenceScreen.b((Preference) preferenceCategory);
        if (parcelableArrayListExtra != null && !parcelableArrayListExtra.isEmpty()) {
            Iterator it = parcelableArrayListExtra.iterator();
            while (it.hasNext()) {
                PermissionInfo permissionInfo = (PermissionInfo) it.next();
                ValuePreference valuePreference = new ValuePreference(getContext());
                valuePreference.a(true);
                valuePreference.setTitle((CharSequence) permissionInfo.getName());
                valuePreference.setSummary((CharSequence) permissionInfo.getDesc());
                int appCount = permissionInfo.getAppCount();
                valuePreference.a(getResources().getQuantityString(R.plurals.hints_permission_apps_count, appCount, new Object[]{Integer.valueOf(appCount)}));
                Intent intent = new Intent(getActivity(), PermissionAppsEditorActivity.class);
                intent.putExtra(":miui:starting_window_label", permissionInfo.getName());
                intent.putExtra("extra_permission_id", permissionInfo.getId());
                intent.putExtra("extra_permission_name", permissionInfo.getName());
                intent.putExtra("extra_permission_flags", permissionInfo.getFlags());
                valuePreference.setIntent(intent);
                preferenceCategory.b((Preference) valuePreference);
            }
        }
    }
}
