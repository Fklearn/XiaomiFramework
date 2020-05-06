package com.miui.appmanager.b;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.b.b;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.miui.appmanager.C0322e;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.c;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import miuix.preference.TextPreference;
import miuix.preference.s;
import org.json.JSONObject;

public class a extends s implements LoaderManager.LoaderCallbacks<d> {

    /* renamed from: a  reason: collision with root package name */
    private PreferenceCategory f3584a;

    /* renamed from: b  reason: collision with root package name */
    private TextPreference f3585b;

    /* renamed from: c  reason: collision with root package name */
    private TextPreference f3586c;

    /* renamed from: d  reason: collision with root package name */
    private TextPreference f3587d;
    private PackageInfo e;
    private d f;
    /* access modifiers changed from: private */
    public String g;
    private String h;
    private String[] i;
    private String[] j;
    private int k;
    private C0041a l;

    /* renamed from: com.miui.appmanager.b.a$a  reason: collision with other inner class name */
    private static class C0041a extends b.b.c.i.a<d> {

        /* renamed from: b  reason: collision with root package name */
        private Context f3588b;

        /* renamed from: c  reason: collision with root package name */
        private WeakReference<a> f3589c;

        public C0041a(Context context, a aVar) {
            super(context);
            this.f3588b = context;
            this.f3589c = new WeakReference<>(aVar);
        }

        public d loadInBackground() {
            a aVar = (a) this.f3589c.get();
            if (aVar == null) {
                return null;
            }
            Context context = this.f3588b;
            return aVar.a(context, C0322e.b(context, aVar.g));
        }
    }

    /* access modifiers changed from: private */
    public d a(Context context, JSONObject jSONObject) {
        d dVar = new d();
        b a2 = b.a(context);
        if (jSONObject != null) {
            String optString = jSONObject.optString("installer_pkg_name");
            String optString2 = jSONObject.optString("update_pkg_name");
            if (!TextUtils.isEmpty(optString)) {
                try {
                    dVar.f3610a = a2.a(optString).a();
                } catch (PackageManager.NameNotFoundException e2) {
                    Log.e("AMAppInformationFragment", "Exception getLabel", e2);
                    dVar.f3610a = optString;
                }
            }
            if (!TextUtils.isEmpty(optString2)) {
                try {
                    dVar.f3611b = a2.a(optString2).a();
                } catch (PackageManager.NameNotFoundException e3) {
                    Log.e("AMAppInformationFragment", "Exception getLabel", e3);
                    dVar.f3611b = optString2;
                }
            }
        } else {
            String a3 = C0322e.a(context, this.g);
            if (!TextUtils.isEmpty(a3)) {
                try {
                    dVar.f3611b = a2.a(a3).a();
                } catch (PackageManager.NameNotFoundException e4) {
                    Log.e("AMAppInformationFragment", "Exception getLabel", e4);
                    dVar.f3611b = a3;
                }
            }
        }
        long b2 = b();
        long j2 = this.e.firstInstallTime;
        if (j2 > b2) {
            dVar.f3612c = j2;
        }
        long j3 = this.e.lastUpdateTime;
        if (j3 > b2) {
            dVar.f3613d = j3;
        }
        return dVar;
    }

    private void a() {
        long j2;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        this.i = getResources().getStringArray(R.array.key_app_info);
        this.j = getResources().getStringArray(R.array.title_app_info);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        int i2 = 0;
        while (true) {
            String[] strArr = this.i;
            if (i2 < strArr.length) {
                if (!"install_source".equals(strArr[i2]) || (str3 = this.f.f3610a) == null) {
                    if ("install_time".equals(this.i[i2])) {
                        j2 = this.f.f3612c;
                        if (j2 != 0) {
                            str = this.i[i2];
                            str2 = this.j[i2];
                            a(str, str2, simpleDateFormat.format(Long.valueOf(j2)));
                            i2++;
                        }
                    }
                    if (!"update_source".equals(this.i[i2]) || (str3 = this.f.f3611b) == null) {
                        if ("update_time".equals(this.i[i2])) {
                            j2 = this.f.f3613d;
                            if (j2 != 0) {
                                str = this.i[i2];
                                str2 = this.j[i2];
                                a(str, str2, simpleDateFormat.format(Long.valueOf(j2)));
                            }
                        }
                        i2++;
                    } else {
                        str4 = this.i[i2];
                        str5 = this.j[i2];
                    }
                } else {
                    str4 = this.i[i2];
                    str5 = this.j[i2];
                }
                a(str4, str5, str3);
                i2++;
            } else {
                return;
            }
        }
    }

    private void a(String str, String str2, String str3) {
        TextPreference textPreference = new TextPreference(getPreferenceManager().a());
        textPreference.setKey(str);
        textPreference.setTitle((CharSequence) str2);
        textPreference.a(str3);
        this.f3584a.b((Preference) textPreference);
    }

    private long b() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse("2012/01/01 00:00:00");
        } catch (ParseException e2) {
            Log.e("AMAppInformationFragment", "getDefaultTime error", e2);
        }
        return date.getTime();
    }

    private void finish() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<d> loader, d dVar) {
        if (dVar != null) {
            this.f = dVar;
            a();
        }
    }

    public Loader<d> onCreateLoader(int i2, Bundle bundle) {
        Activity activity = getActivity();
        if (activity != null) {
            this.l = new C0041a(activity.getApplicationContext(), this);
        }
        return this.l;
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R.xml.app_manager_app_infomation);
        Bundle arguments = getArguments();
        this.g = arguments.getString("am_app_pkgname");
        this.h = arguments.getString("am_app_label");
        this.k = arguments.getInt("am_app_uid", -1);
        this.e = b.b.o.b.a.a.a(this.g, 0, UserHandle.getUserId(this.k));
        if (this.e == null) {
            finish();
            return;
        }
        this.f = new d();
        this.f3584a = (PreferenceCategory) findPreference("category_app_infomation");
        this.f3585b = (TextPreference) findPreference("am_info_pkgname");
        if ("com.miui.dmregservice".equals(this.g)) {
            this.f3584a.d(this.f3585b);
        } else {
            this.f3585b.a(this.g);
        }
        this.f3586c = (TextPreference) findPreference("am_info_label");
        this.f3586c.a(this.h);
        this.f3587d = (TextPreference) findPreference("am_info_version");
        this.f3587d.a(this.e.versionName);
        Loader loader = getLoaderManager().getLoader(TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(TsExtractor.TS_STREAM_TYPE_HDMV_DTS, (Bundle) null, this);
        if (Build.VERSION.SDK_INT >= 24 && bundle != null && loader != null) {
            loaderManager.restartLoader(TsExtractor.TS_STREAM_TYPE_HDMV_DTS, (Bundle) null, this);
        }
    }

    public void onLoaderReset(Loader<d> loader) {
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        Activity activity = getActivity();
        if (preference.getKey().equals("am_info_pkgname") && activity != null) {
            ((ClipboardManager) activity.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("pkgName", this.g));
            c.a(activity.getApplicationContext(), (int) R.string.app_manager_copy_pkg_to_clip);
        }
        return super.onPreferenceTreeClick(preference);
    }
}
