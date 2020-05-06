package com.miui.optimizecenter.storage.b;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import com.miui.securitycenter.R;
import java.util.List;

public class b extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    /* renamed from: a  reason: collision with root package name */
    private PackageManager f5717a;

    /* renamed from: b  reason: collision with root package name */
    private Context f5718b;

    private void a() {
        List<ResolveInfo> queryBroadcastReceivers = this.f5717a.queryBroadcastReceivers(new Intent("miui.intent.action.PRIORITY_STORAGE"), 640);
        if (queryBroadcastReceivers != null) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            for (ResolveInfo next : queryBroadcastReceivers) {
                ListPreference listPreference = new ListPreference(getActivity());
                ComponentName componentName = new ComponentName(next.activityInfo.packageName, next.activityInfo.name);
                String charSequence = next.loadLabel(this.f5717a).toString();
                listPreference.setKey(componentName.flattenToString());
                listPreference.setEntries(R.array.priority_storage_entries);
                listPreference.setEntryValues(R.array.priority_storage_value);
                int componentEnabledSetting = this.f5717a.getComponentEnabledSetting(componentName);
                boolean z = true;
                int i = (componentEnabledSetting == 1 || (componentEnabledSetting == 0 && next.activityInfo.metaData != null && next.activityInfo.metaData.getBoolean("miui.intent.extra.SET_PRIORITY_DEFAULT"))) ? 1 : 0;
                listPreference.setValueIndex(i);
                listPreference.setSummary(listPreference.getEntries()[i]);
                listPreference.setPersistent(false);
                listPreference.setTitle(charSequence);
                listPreference.setDialogTitle(getString(R.string.priority_storage_app_settings, new Object[]{charSequence}));
                if (!(next.activityInfo.metaData == null || next.activityInfo.metaData.getInt("miui.intent.extra.PRIORITY_STORAGE_KILL_APP") == 0)) {
                    z = false;
                }
                Intent intent = new Intent();
                intent.putExtra("extra_kill_app", z);
                listPreference.setIntent(intent);
                listPreference.setOnPreferenceChangeListener(this);
                listPreference.setOnPreferenceClickListener(this);
                preferenceScreen.addPreference(listPreference);
            }
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.f5718b = context;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.f5717a = this.f5718b.getPackageManager();
        addPreferencesFromResource(R.xml.priority_storage);
        a();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        int intValue = Integer.valueOf((String) obj).intValue();
        ComponentName unflattenFromString = ComponentName.unflattenFromString(preference.getKey());
        ListPreference listPreference = (ListPreference) preference;
        this.f5717a.setComponentEnabledSetting(unflattenFromString, intValue == 1 ? 1 : 2, listPreference.getIntent().getBooleanExtra("extra_kill_app", true) ^ true ? 1 : 0);
        listPreference.setSummary(listPreference.getEntries()[intValue]);
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        return true;
    }
}
