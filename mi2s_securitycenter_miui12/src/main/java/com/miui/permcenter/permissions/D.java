package com.miui.permcenter.permissions;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.miui.permcenter.n;
import com.miui.permcenter.privacymanager.b.c;
import com.miui.permission.PermissionGroupInfo;
import com.miui.permission.PermissionInfo;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.util.AttributeResolver;
import miuix.preference.s;

public class D extends s implements LoaderManager.LoaderCallbacks<z> {

    /* renamed from: a  reason: collision with root package name */
    public static final String f6221a = "D";

    private static class a extends b.b.c.i.a<z> {
        public a(Context context) {
            super(context);
        }

        public z loadInBackground() {
            Context context = getContext();
            try {
                Thread.sleep(530);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PermissionManager instance = PermissionManager.getInstance(context);
            List<PermissionInfo> allPermissions = instance.getAllPermissions(1);
            ArrayList<PermissionInfo> arrayList = new ArrayList<>();
            ArrayList<PermissionInfo> arrayList2 = new ArrayList<>();
            List<PermissionGroupInfo> allPermissionGroups = instance.getAllPermissionGroups(0);
            ArrayList<q> arrayList3 = new ArrayList<>();
            HashMap hashMap = new HashMap();
            for (PermissionGroupInfo next : allPermissionGroups) {
                q qVar = new q();
                qVar.f6288a = next;
                hashMap.put(Integer.valueOf(next.getId()), qVar);
                if (next.getId() != 1) {
                    arrayList3.add(qVar);
                }
            }
            for (PermissionInfo next2 : allPermissions) {
                long id = next2.getId();
                if (id != PermissionManager.PERM_ID_BACKGROUND_LOCATION) {
                    if (n.a(Long.valueOf(id)) && !c.a(context)) {
                        arrayList2.add(next2);
                    } else if (!n.b(Long.valueOf(next2.getId())) || c.a(context)) {
                        q qVar2 = (q) hashMap.get(Integer.valueOf(next2.getGroup()));
                        if (qVar2 != null) {
                            qVar2.f6289b.add(next2);
                        }
                    } else {
                        arrayList.add(next2);
                    }
                }
            }
            z zVar = new z();
            zVar.f6306a = arrayList2;
            zVar.f6307b = arrayList;
            zVar.f6308c = arrayList3;
            return zVar;
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<z> loader, z zVar) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.e();
        PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
        preferenceCategory.setTitle((int) R.string.info_and_call);
        if (zVar.f6307b.size() > 0 || zVar.f6306a.size() > 0) {
            preferenceScreen.b((Preference) preferenceCategory);
        }
        if (zVar.f6307b.size() > 0) {
            ValuePreference valuePreference = new ValuePreference(getActivity());
            valuePreference.setTitle((int) R.string.SMS_and_MMS);
            valuePreference.setSummary((int) R.string.permission_with_SMS_and_MMS);
            valuePreference.a(true);
            Intent intent = new Intent(getActivity(), SecondPermissionAppsActivity.class);
            intent.putExtra(":miui:starting_window_label", getString(R.string.SMS_and_MMS));
            intent.putParcelableArrayListExtra("extra_permission_list", zVar.f6307b);
            intent.putExtra("extra_group_type", 1);
            valuePreference.setIntent(intent);
            preferenceCategory.b((Preference) valuePreference);
        }
        if (zVar.f6306a.size() > 0) {
            ValuePreference valuePreference2 = new ValuePreference(getActivity());
            valuePreference2.setTitle((int) R.string.call_and_contact);
            valuePreference2.setSummary((int) R.string.permission_with_call_and_contact);
            valuePreference2.a(true);
            Intent intent2 = new Intent(getActivity(), SecondPermissionAppsActivity.class);
            intent2.putExtra(":miui:starting_window_label", getString(R.string.call_and_contact));
            intent2.putParcelableArrayListExtra("extra_permission_list", zVar.f6306a);
            intent2.putExtra("extra_group_type", 2);
            valuePreference2.setIntent(intent2);
            preferenceCategory.b((Preference) valuePreference2);
        }
        Iterator<q> it = zVar.f6308c.iterator();
        while (it.hasNext()) {
            q next = it.next();
            if (next.f6289b.size() > 0) {
                PreferenceCategory preferenceCategory2 = new PreferenceCategory(getActivity());
                preferenceCategory2.setTitle((CharSequence) next.f6288a.getName());
                preferenceScreen.b((Preference) preferenceCategory2);
                Iterator<PermissionInfo> it2 = next.f6289b.iterator();
                while (it2.hasNext()) {
                    PermissionInfo next2 = it2.next();
                    ValuePreference valuePreference3 = new ValuePreference(getActivity());
                    valuePreference3.setTitle((CharSequence) next2.getName());
                    valuePreference3.setSummary((CharSequence) next2.getDesc());
                    valuePreference3.a(true);
                    int appCount = next2.getAppCount();
                    valuePreference3.a(getResources().getQuantityString(R.plurals.hints_permission_apps_count, appCount, new Object[]{Integer.valueOf(appCount)}));
                    Intent intent3 = new Intent(getActivity(), PermissionAppsEditorActivity.class);
                    intent3.putExtra(":miui:starting_window_label", next2.getName());
                    intent3.putExtra("extra_permission_id", next2.getId());
                    intent3.putExtra("extra_permission_name", next2.getName());
                    intent3.putExtra("extra_permission_flags", next2.getFlags());
                    if (c.a(getActivity())) {
                        intent3.putExtra("extra_permission_desc", next2.getDesc());
                    }
                    valuePreference3.setIntent(intent3);
                    preferenceCategory2.b((Preference) valuePreference3);
                }
            }
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.pm_fragment_permissions);
    }

    public Loader<z> onCreateLoader(int i, Bundle bundle) {
        return new a(getActivity());
    }

    public void onCreatePreferences(Bundle bundle, String str) {
    }

    public void onLoaderReset(Loader<z> loader) {
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ListView listView = (ListView) view.findViewById(16908298);
        if (listView != null) {
            listView.setClipToPadding(false);
            listView.setPadding(0, 0, 0, (int) AttributeResolver.resolveDimension(view.getContext(), miui.R.attr.preferenceScreenPaddingBottom));
        }
        Loader loader = getLoaderManager().getLoader(150);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(150, (Bundle) null, this);
        if (Build.VERSION.SDK_INT >= 24 && bundle != null && loader != null) {
            loaderManager.restartLoader(150, (Bundle) null, this);
        }
    }
}
