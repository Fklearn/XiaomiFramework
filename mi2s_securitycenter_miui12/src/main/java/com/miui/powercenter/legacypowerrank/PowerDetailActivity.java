package com.miui.powercenter.legacypowerrank;

import android.app.ActivityManager;
import android.app.StatusBarManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.c.b.l;
import b.b.c.j.C;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.powercenter.utils.j;
import com.miui.powercenter.utils.s;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.io.Serializable;
import java.util.Locale;
import miui.app.AlertDialog;
import miui.content.res.IconCustomizer;
import miui.os.Build;
import miuix.preference.TextPreference;

public class PowerDetailActivity extends b.b.c.c.a {

    public static class a extends l {

        /* renamed from: a  reason: collision with root package name */
        private int f7077a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public int f7078b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public String[] f7079c;

        /* renamed from: d  reason: collision with root package name */
        private DevicePolicyManager f7080d;
        /* access modifiers changed from: private */
        public PowerUsageDetailsTitlePreference e;
        private PreferenceCategory f;
        private PreferenceCategory g;
        /* access modifiers changed from: private */
        public MenuItem h;
        /* access modifiers changed from: private */
        public MenuItem i;
        /* access modifiers changed from: private */
        public MenuItem j;
        /* access modifiers changed from: private */
        public boolean k = false;
        private boolean l = true;
        private final BroadcastReceiver m = new c(this);
        /* access modifiers changed from: private */
        public Handler mHandler;

        /* renamed from: com.miui.powercenter.legacypowerrank.PowerDetailActivity$a$a  reason: collision with other inner class name */
        private class C0064a extends AsyncTask<Void, Void, Drawable> {

            /* renamed from: a  reason: collision with root package name */
            private String f7081a;

            /* renamed from: b  reason: collision with root package name */
            private int f7082b;

            /* renamed from: c  reason: collision with root package name */
            private int f7083c;

            public C0064a(String str, int i, int i2) {
                this.f7081a = str;
                this.f7082b = i;
                this.f7083c = i2;
            }

            /* access modifiers changed from: protected */
            /* renamed from: a */
            public Drawable doInBackground(Void... voidArr) {
                Drawable drawable = null;
                if (!TextUtils.isEmpty(this.f7081a)) {
                    try {
                        PackageManager packageManager = a.this.getContext().getPackageManager();
                        ApplicationInfo applicationInfo = packageManager.getPackageInfo(this.f7081a, 0).applicationInfo;
                        if (applicationInfo != null) {
                            drawable = applicationInfo.loadIcon(packageManager);
                        }
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                    if (drawable != null && C.b(j.a(this.f7083c))) {
                        drawable = C.a(Application.d(), drawable, this.f7083c);
                    }
                } else if (this.f7082b != 0) {
                    drawable = a.this.getContext().getResources().getDrawable(this.f7082b);
                }
                return drawable == null ? a.this.getContext().getPackageManager().getDefaultActivityIcon() : IconCustomizer.generateIconStyleDrawable(drawable);
            }

            /* access modifiers changed from: protected */
            /* renamed from: a */
            public void onPostExecute(Drawable drawable) {
                a.this.e.setIcon(drawable);
            }
        }

        private void a(Context context, String[] strArr, int i2, BroadcastReceiver broadcastReceiver) {
            String[] strArr2 = this.f7079c;
            if (strArr2 != null && strArr2.length != 0) {
                try {
                    Intent intent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", strArr[0], (String) null));
                    intent.putExtra(Constants.System.EXTRA_USER_HANDLE, (Serializable) e.a(Class.forName("miui.securitycenter.utils.SecurityCenterHelper"), Integer.TYPE, "getUserId", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i2)));
                    intent.putExtra("android.intent.extra.PACKAGES", strArr);
                    context.sendOrderedBroadcastAsUser(intent, (UserHandle) e.a(Class.forName("miui.securitycenter.utils.SecurityCenterHelper"), UserHandle.class, "getUserAll", (Class<?>[]) null, new Object[0]), (String) null, broadcastReceiver, (Handler) null, 0, (String) null, (Bundle) null);
                } catch (Exception e2) {
                    Log.d("PowerDetailActivity", "sendQueryPackageIntent exception: ", e2);
                }
            }
        }

        /* access modifiers changed from: private */
        public void a(Object obj, int i2, int i3, int i4) {
            b.b.o.b.a.a.a(obj, this.f7079c[0], i2, new f(this), i3, i4);
        }

        private void b() {
            boolean z;
            if (this.h != null) {
                if (this.f7079c != null && this.f7078b >= 10000) {
                    int i2 = 0;
                    while (i2 < this.f7079c.length) {
                        try {
                            z = ((Boolean) e.a(Class.forName("miui.securitycenter.utils.SecurityCenterHelper"), Boolean.TYPE, "packageHasActiveAdmins", (Class<?>[]) new Class[]{DevicePolicyManager.class, String.class}, this.f7080d, this.f7079c[i2])).booleanValue();
                        } catch (Exception e2) {
                            Log.d("PowerDetailActivity", "checkForceStop exception ", e2);
                            z = false;
                        }
                        if (!z) {
                            i2++;
                        }
                    }
                    int i3 = 0;
                    while (i3 < this.f7079c.length) {
                        try {
                            if ((getContext().getPackageManager().getApplicationInfo(this.f7079c[i3], 0).flags & StatusBarManager.DISABLE_HOME) == 0) {
                                this.h.setEnabled(true);
                                return;
                            }
                            i3++;
                        } catch (PackageManager.NameNotFoundException unused) {
                        }
                    }
                    this.h.setEnabled(this.k);
                    if (b.b.c.j.e.c(getContext(), this.f7079c[0], j.a(this.f7078b))) {
                        Log.d("Enterprise", "Package " + this.f7079c[0] + " should keep alive");
                        this.h.setEnabled(false);
                        return;
                    }
                    return;
                }
                this.h.setEnabled(false);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:13:0x002a  */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x002d  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0036  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x0038  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0048  */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00ab  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x00ca  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x00c6 A[EDGE_INSN: B:59:0x00c6->B:51:0x00c6 ?: BREAK  , SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:64:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void c() {
            /*
                r7 = this;
                android.view.MenuItem r0 = r7.i
                if (r0 == 0) goto L_0x0136
                android.view.MenuItem r0 = r7.h
                if (r0 == 0) goto L_0x0136
                android.view.MenuItem r0 = r7.j
                if (r0 != 0) goto L_0x000e
                goto L_0x0136
            L_0x000e:
                android.content.Context r0 = r7.getContext()
                android.content.pm.PackageManager r0 = r0.getPackageManager()
                int r1 = r7.f7078b
                java.lang.String[] r1 = r0.getPackagesForUid(r1)
                r2 = 0
                r3 = 0
                if (r1 == 0) goto L_0x0027
                r1 = r1[r3]     // Catch:{ NameNotFoundException -> 0x0027 }
                android.content.pm.PackageInfo r0 = r0.getPackageInfo(r1, r3)     // Catch:{ NameNotFoundException -> 0x0027 }
                goto L_0x0028
            L_0x0027:
                r0 = r2
            L_0x0028:
                if (r0 == 0) goto L_0x002d
                android.content.pm.ApplicationInfo r0 = r0.applicationInfo
                goto L_0x002e
            L_0x002d:
                r0 = r2
            L_0x002e:
                r1 = 1
                if (r0 == 0) goto L_0x0038
                int r4 = r0.flags
                r4 = r4 & r1
                if (r4 == 0) goto L_0x0038
                r4 = r1
                goto L_0x0039
            L_0x0038:
                r4 = r3
            L_0x0039:
                android.view.MenuItem r5 = r7.h
                r5.setEnabled(r3)
                boolean r5 = r7.e()
                if (r5 == 0) goto L_0x007f
                java.lang.String[] r5 = r7.f7079c
                if (r5 == 0) goto L_0x007f
                android.view.MenuItem r6 = r7.j
                int r5 = r5.length
                if (r5 != r1) goto L_0x004f
                r5 = r1
                goto L_0x0050
            L_0x004f:
                r5 = r3
            L_0x0050:
                r6.setEnabled(r5)
                boolean r5 = r7.f()
                if (r5 == 0) goto L_0x005f
                android.view.MenuItem r4 = r7.i
                r4.setVisible(r3)
                goto L_0x0089
            L_0x005f:
                android.view.MenuItem r5 = r7.i
                r4 = r4 ^ r1
                r5.setEnabled(r4)
                java.lang.String[] r4 = r7.f7079c
                r4 = r4[r3]
                boolean r4 = b.b.f.a.a((java.lang.String) r4)
                if (r4 == 0) goto L_0x0089
                android.content.Context r4 = r7.getContext()
                boolean r4 = b.b.f.a.b(r4)
                if (r4 == 0) goto L_0x0089
                android.view.MenuItem r4 = r7.i
                r4.setEnabled(r1)
                goto L_0x0089
            L_0x007f:
                android.view.MenuItem r4 = r7.j
                r4.setEnabled(r3)
                android.view.MenuItem r4 = r7.i
                r4.setEnabled(r3)
            L_0x0089:
                boolean r4 = r7.e()
                if (r4 == 0) goto L_0x00c6
                if (r0 == 0) goto L_0x00c6
                android.content.Context r4 = r7.getContext()
                java.lang.String r5 = "activity"
                java.lang.Object r4 = r4.getSystemService(r5)
                android.app.ActivityManager r4 = (android.app.ActivityManager) r4
                java.util.List r4 = r4.getRunningAppProcesses()
                java.util.Iterator r4 = r4.iterator()
            L_0x00a5:
                boolean r5 = r4.hasNext()
                if (r5 == 0) goto L_0x00c6
                java.lang.Object r5 = r4.next()
                android.app.ActivityManager$RunningAppProcessInfo r5 = (android.app.ActivityManager.RunningAppProcessInfo) r5
                java.lang.String[] r5 = r5.pkgList
                if (r5 == 0) goto L_0x00b8
                r5 = r5[r3]
                goto L_0x00b9
            L_0x00b8:
                r5 = r2
            L_0x00b9:
                java.lang.String r6 = r0.packageName
                boolean r5 = android.text.TextUtils.equals(r5, r6)
                if (r5 == 0) goto L_0x00a5
                android.view.MenuItem r0 = r7.h
                r0.setEnabled(r1)
            L_0x00c6:
                java.lang.String[] r0 = r7.f7079c
                if (r0 == 0) goto L_0x0136
                android.content.Context r0 = r7.getContext()
                java.lang.String[] r1 = r7.f7079c
                r1 = r1[r3]
                int r2 = r7.f7078b
                int r2 = com.miui.powercenter.utils.j.a(r2)
                boolean r0 = b.b.c.j.e.c(r0, r1, r2)
                java.lang.String r1 = "Package "
                java.lang.String r2 = "Enterprise"
                if (r0 == 0) goto L_0x0102
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r1)
                java.lang.String[] r4 = r7.f7079c
                r4 = r4[r3]
                r0.append(r4)
                java.lang.String r4 = " should keep alive"
                r0.append(r4)
                java.lang.String r0 = r0.toString()
                android.util.Log.d(r2, r0)
                android.view.MenuItem r0 = r7.h
                r0.setEnabled(r3)
            L_0x0102:
                android.content.Context r0 = r7.getContext()
                java.lang.String[] r4 = r7.f7079c
                r4 = r4[r3]
                int r5 = r7.f7078b
                int r5 = com.miui.powercenter.utils.j.a(r5)
                boolean r0 = b.b.c.j.e.b(r0, r4, r5)
                if (r0 == 0) goto L_0x0136
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r1)
                java.lang.String[] r1 = r7.f7079c
                r1 = r1[r3]
                r0.append(r1)
                java.lang.String r1 = " is protected from delete"
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                android.util.Log.d(r2, r0)
                android.view.MenuItem r0 = r7.i
                r0.setEnabled(r3)
            L_0x0136:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.legacypowerrank.PowerDetailActivity.a.c():void");
        }

        private String d() {
            return b.a(getContext(), this.f7077a);
        }

        private boolean e() {
            return this.f7077a == 6;
        }

        private boolean f() {
            String[] strArr = this.f7079c;
            if (strArr.length <= 0) {
                return false;
            }
            return (Build.IS_INTERNATIONAL_BUILD && ("com.facemoji.lite.xiaomi".equals(strArr[0]) || "com.kikaoem.xiaomi.qisiemoji.inputmethod".equals(this.f7079c[0]))) || "com.miui.android.fashiongallery".equals(this.f7079c[0]);
        }

        private void g() {
            if (this.f7079c != null) {
                ActivityManager activityManager = (ActivityManager) getContext().getSystemService("activity");
                int i2 = 0;
                while (true) {
                    String[] strArr = this.f7079c;
                    if (i2 < strArr.length) {
                        x.a(activityManager, strArr[i2]);
                        i2++;
                    } else {
                        return;
                    }
                }
            }
        }

        private void h() {
            if (!C.b(j.a(this.f7078b))) {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", this.f7079c[0], (String) null));
                startActivity(intent);
                return;
            }
            Intent intent2 = new Intent();
            intent2.setClassName("com.android.settings", "com.android.settings.applications.InstalledAppDetailsTop");
            intent2.putExtra("package", this.f7079c[0]);
            intent2.putExtra("is_xspace_app", true);
            x.c(getContext(), intent2);
        }

        private void i() {
            new AlertDialog.Builder(getContext()).setTitle(R.string.uninstall_app_dialog_title).setMessage(R.string.uninstall_app_dialog_msg).setPositiveButton(17039370, new d(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
        }

        private void j() {
            c();
            if (e()) {
                b();
            }
        }

        public void onCreate(Bundle bundle) {
            String str;
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.pc_power_usage_details);
            this.f7080d = (DevicePolicyManager) getContext().getSystemService("device_policy");
            this.mHandler = new Handler();
            this.e = (PowerUsageDetailsTitlePreference) findPreference("preference_key_power_usage_details_title");
            this.f = (PreferenceCategory) findPreference("category_key_power_usage_details");
            this.g = (PreferenceCategory) findPreference("category_key_power_usage_packages");
            Intent intent = getActivity().getIntent();
            this.e.setTitle((CharSequence) intent.getStringExtra(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME));
            float floatExtra = intent.getFloatExtra("percent", 1.0f);
            this.e.a(Math.round(floatExtra));
            this.e.a((CharSequence) getString(R.string.percent_formatted_text, new Object[]{String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(floatExtra)})}));
            this.f7077a = intent.getIntExtra("drainType", 0);
            this.e.setSummary((CharSequence) d());
            this.f7078b = intent.getIntExtra(MijiaAlertModel.KEY_UID, 0);
            this.l = intent.getBooleanExtra("showMenus", true);
            new C0064a(intent.getStringExtra("iconPackage"), intent.getIntExtra("iconId", 0), this.f7078b).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            int[] intArrayExtra = intent.getIntArrayExtra("types");
            double[] doubleArrayExtra = intent.getDoubleArrayExtra("values");
            if (!(intArrayExtra == null || doubleArrayExtra == null)) {
                for (int i2 = 0; i2 < intArrayExtra.length; i2++) {
                    if (doubleArrayExtra[i2] > 0.0d) {
                        String string = getString(intArrayExtra[i2]);
                        switch (intArrayExtra[i2]) {
                            case R.string.usage_type_data_recv /*2131758480*/:
                            case R.string.usage_type_data_send /*2131758481*/:
                                str = s.a(getContext(), doubleArrayExtra[i2]);
                                break;
                            case R.string.usage_type_no_coverage /*2131758483*/:
                                str = String.format(Locale.getDefault(), "%d%%", new Object[]{Integer.valueOf((int) Math.floor(doubleArrayExtra[i2]))});
                                break;
                            default:
                                str = s.b(getContext(), doubleArrayExtra[i2]);
                                break;
                        }
                        TextPreference textPreference = new TextPreference(getPreferenceManager().a());
                        textPreference.setTitle((CharSequence) string);
                        textPreference.a(str);
                        this.f.b((Preference) textPreference);
                    }
                }
            }
            if (this.f.c() == 0) {
                this.f.setTitle((CharSequence) null);
                getPreferenceScreen().d(this.f);
            }
            int i3 = this.f7078b;
            if (i3 >= 1 && i3 != 1000) {
                PackageManager packageManager = getContext().getPackageManager();
                this.f7079c = packageManager.getPackagesForUid(this.f7078b);
                String[] strArr = this.f7079c;
                if (strArr != null && strArr.length >= 2) {
                    int i4 = 0;
                    while (true) {
                        String[] strArr2 = this.f7079c;
                        if (i4 < strArr2.length) {
                            try {
                                CharSequence loadLabel = packageManager.getApplicationInfo(strArr2[i4], 0).loadLabel(packageManager);
                                TextPreference textPreference2 = new TextPreference(getPreferenceManager().a());
                                textPreference2.setTitle(loadLabel);
                                this.g.b((Preference) textPreference2);
                            } catch (PackageManager.NameNotFoundException unused) {
                            }
                            i4++;
                        }
                    }
                }
            }
            if (this.g.c() == 0) {
                this.g.setTitle((CharSequence) null);
                getPreferenceScreen().d(this.g);
            }
            a(getContext(), this.f7079c, this.f7078b, this.m);
        }

        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
            super.onCreateOptionsMenu(menu, menuInflater);
            if (this.l) {
                menuInflater.inflate(R.menu.pc_power_usage_details_menus, menu);
                this.h = menu.findItem(R.id.item_force_stop);
                this.i = menu.findItem(R.id.item_uninstall);
                this.j = menu.findItem(R.id.item_details);
            }
        }

        public boolean onOptionsItemSelected(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.item_details) {
                h();
                return true;
            } else if (itemId == R.id.item_force_stop) {
                g();
                this.h.setEnabled(false);
                return true;
            } else if (itemId != R.id.item_uninstall) {
                return super.onOptionsItemSelected(menuItem);
            } else {
                i();
                return true;
            }
        }

        public void onPrepareOptionsMenu(Menu menu) {
            j();
        }

        public void onResume() {
            super.onResume();
            j();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        a aVar = new a();
        aVar.setHasOptionsMenu(true);
        getFragmentManager().beginTransaction().replace(16908290, aVar, (String) null).commit();
    }
}
