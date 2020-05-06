package com.miui.permcenter.permissions;

import android.app.LoaderManager;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import b.b.c.c.c;
import com.miui.activityutil.o;
import com.miui.permcenter.n;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppPermissionsUseActivity extends c implements LoaderManager.LoaderCallbacks<r>, n.c {

    /* renamed from: a  reason: collision with root package name */
    private static final String f6203a = "AppPermissionsUseActivity";
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f6204b;

    /* renamed from: c  reason: collision with root package name */
    private String f6205c = null;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public List<String> f6206d;
    /* access modifiers changed from: private */
    public List<String> e;
    private List<String> f;
    protected HashMap<String, t> g = new HashMap<>();

    private void a() {
        this.g.put("android.permission-group.CONTACTS", new t(getString(R.string.app_permission_use_group_contact), getString(R.string.app_permission_use_group_contact_des)));
        this.g.put("android.permission-group.PHONE", new t(getString(R.string.app_permission_use_group_phone), getString(R.string.app_permission_use_group_phone_des)));
        this.g.put("android.permission-group.CALENDAR", new t(getString(R.string.app_permission_use_group_calendar), getString(R.string.app_permission_use_group_calendar_des)));
        this.g.put("android.permission-group.CAMERA", new t(getString(R.string.app_permission_use_group_camera), getString(R.string.app_permission_use_group_camera_des)));
        this.g.put("android.permission-group.SENSORS", new t(getString(R.string.app_permission_use_group_sensor), getString(R.string.app_permission_use_group_sensor_des)));
        this.g.put("android.permission-group.LOCATION", new t(getString(R.string.app_permission_use_group_location), getString(R.string.app_permission_use_group_location_des)));
        this.g.put("android.permission-group.STORAGE", new t(getString(R.string.app_permission_use_group_storage), getString(R.string.app_permission_use_group_storage_des)));
        this.g.put("android.permission-group.MICROPHONE", new t(getString(R.string.app_permission_use_group_mic), getString(R.string.app_permission_use_group_mic_des)));
        this.g.put("android.permission-group.SMS", new t(getString(R.string.app_permission_use_group_sms), getString(R.string.app_permission_use_group_sms_des)));
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [android.content.Context, com.miui.permcenter.permissions.AppPermissionsUseActivity, miui.preference.PreferenceActivity] */
    /* renamed from: a */
    public void onLoadFinished(Loader<r> loader, r rVar) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        boolean z = preferenceScreen.getPreferenceCount() > 0;
        preferenceScreen.removeAll();
        if (!rVar.f6290a.isEmpty()) {
            PreferenceCategory preferenceCategory = new PreferenceCategory(this);
            if (z) {
                preferenceCategory.setOrder(0);
                z = false;
            }
            preferenceCategory.setTitle(R.string.app_permission_use_neccesary);
            preferenceScreen.addPreference(preferenceCategory);
            for (s next : rVar.f6290a) {
                l lVar = new l(this);
                lVar.setTitle(next.f6293b.f6294a);
                lVar.setSummary(next.f6293b.f6295b);
                preferenceCategory.addPreference(lVar);
            }
        }
        if (!rVar.f6291b.isEmpty() && rVar.f6291b.size() > 0) {
            PreferenceCategory preferenceCategory2 = new PreferenceCategory(this);
            if (z) {
                preferenceCategory2.setOrder(0);
                z = false;
            }
            preferenceCategory2.setTitle(R.string.app_permission_use_no_neccesary);
            preferenceScreen.addPreference(preferenceCategory2);
            for (s next2 : rVar.f6291b) {
                l lVar2 = new l(this);
                lVar2.setTitle(next2.f6293b.f6294a);
                lVar2.setSummary(next2.f6293b.f6295b);
                preferenceCategory2.addPreference(lVar2);
            }
        }
        PreferenceCategory preferenceCategory3 = new PreferenceCategory(this);
        if (z) {
            preferenceCategory3.setOrder(0);
        }
        preferenceScreen.addPreference(preferenceCategory3);
        try {
            FrameLayout frameLayout = (FrameLayout) getWindow().getDecorView().findViewById(16908290);
            View childAt = frameLayout.getChildAt(0);
            frameLayout.removeView(childAt);
            RelativeLayout relativeLayout = new RelativeLayout(this);
            frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(-1, -1));
            relativeLayout.addView(childAt, new RelativeLayout.LayoutParams(-1, -1));
            frameLayout.requestLayout();
            frameLayout.invalidate();
            relativeLayout.postDelayed(new k(this, relativeLayout), 10);
        } catch (Exception e2) {
            Log.e(f6203a, "error:" + e2.toString());
        }
    }

    public void a(String str, int i) {
        getLoaderManager().getLoader(904).forceLoad();
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            return true;
        }
        return AppPermissionsUseActivity.super.dispatchKeyEvent(keyEvent);
    }

    public void finish() {
        setResult(2308);
        AppPermissionsUseActivity.super.finish();
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, com.miui.permcenter.permissions.AppPermissionsUseActivity, android.app.LoaderManager$LoaderCallbacks, miui.preference.PreferenceActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        String str;
        String str2;
        String str3;
        List<String> list;
        String str4;
        HashMap<String, t> hashMap;
        t tVar;
        AppPermissionsUseActivity.super.onCreate(bundle);
        getWindow().addFlags(524288);
        getWindow().addFlags(StatusBarManager.DISABLE_HOME);
        this.f6204b = this;
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
        a();
        addPreferencesFromResource(R.xml.pm_activity_app_permissions_editor);
        this.f6205c = getIntent().getStringExtra("extra_pkgname");
        this.f = getIntent().getStringArrayListExtra("extra_main_permission_groups");
        this.f6206d = new ArrayList();
        this.e = new ArrayList();
        int i = 0;
        while (i < this.f.size()) {
            String[] split = this.f.get(i).split("@");
            if (split == null) {
                str = f6203a;
                str2 = "extra_main_permission_groups data format error:null";
            } else {
                int length = split.length;
                if (length == 4 || length == 2) {
                    if (length == 4) {
                        String str5 = split[2];
                        if (!TextUtils.isEmpty(str5)) {
                            if (str5.equalsIgnoreCase("null")) {
                                if (this.g.containsKey(split[0])) {
                                    tVar = new t(new String(this.g.get(split[0]).f6294a), split[3]);
                                    hashMap = this.g;
                                    str4 = split[0];
                                }
                            } else if (this.g.containsKey(split[0])) {
                                tVar = new t(split[2], split[3]);
                                hashMap = this.g;
                                str4 = split[0];
                            }
                            hashMap.put(str4, tVar);
                        }
                    }
                    String str6 = split[0];
                    if (this.g.containsKey(str6)) {
                        if (split[1].equals(o.f2310b)) {
                            list = this.f6206d;
                            str3 = new String(str6);
                        } else {
                            list = this.e;
                            str3 = new String(str6);
                        }
                        list.add(str3);
                    }
                    i++;
                } else {
                    str = f6203a;
                    str2 = "extra_main_permission_groups data format error:len=" + length;
                }
            }
            Log.d(str, str2);
            finish();
            return;
        }
        if (TextUtils.isEmpty(this.f6205c)) {
            finish();
            return;
        }
        getActionBar().setTitle(getString(R.string.app_permission_use_title));
        Loader loader = getLoaderManager().getLoader(904);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(904, (Bundle) null, this);
        if (Build.VERSION.SDK_INT >= 24 && bundle != null && loader != null) {
            loaderManager.restartLoader(904, (Bundle) null, this);
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.permcenter.permissions.AppPermissionsUseActivity] */
    public Loader<r> onCreateLoader(int i, Bundle bundle) {
        return new C0471h(this, this);
    }

    public void onLoaderReset(Loader<r> loader) {
    }
}
