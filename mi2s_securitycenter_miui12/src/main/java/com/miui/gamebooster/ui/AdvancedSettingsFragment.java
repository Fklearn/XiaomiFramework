package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.c.b.d;
import b.b.o.g.e;
import com.miui.gamebooster.a.a.a.b;
import com.miui.gamebooster.customview.b.f;
import com.miui.gamebooster.customview.b.g;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.view.q;
import com.miui.gamebooster.view.r;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miuix.recyclerview.widget.RecyclerView;

public class AdvancedSettingsFragment extends d implements LoaderManager.LoaderCallbacks<ArrayList<C0398d>>, f.a, q {

    /* renamed from: a  reason: collision with root package name */
    private RecyclerView f4849a;

    /* renamed from: b  reason: collision with root package name */
    private f<C0398d> f4850b;

    /* renamed from: c  reason: collision with root package name */
    private r f4851c;

    static class a extends b.b.c.i.a<ArrayList<C0398d>> {

        /* renamed from: b  reason: collision with root package name */
        private PackageManager f4852b;

        private a(Context context) {
            super(context);
            this.f4852b = getContext().getPackageManager();
        }

        private ApplicationInfo a(String str, int i) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            try {
                return (ApplicationInfo) e.a(e.a(Class.forName("android.app.AppGlobals"), "getPackageManager", (Class<?>[]) null, new Object[0]), ApplicationInfo.class, "getApplicationInfo", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, 8192, Integer.valueOf(i));
            } catch (Exception unused) {
                return null;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:19:0x006a, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
            com.miui.gamebooster.m.C0391w.a(r1, (java.lang.String) null, -1, true, 0);
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x0070 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.util.ArrayList<com.miui.gamebooster.model.C0398d> loadInBackground() {
            /*
                r12 = this;
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                android.content.Context r1 = r12.getContext()
                if (r1 != 0) goto L_0x000c
                return r0
            L_0x000c:
                r2 = -1
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r4 = 0
                r5 = 1
                r6 = 0
                android.database.Cursor r7 = com.miui.gamebooster.m.C0391w.a((android.content.Context) r1, (int) r6)     // Catch:{ Exception -> 0x006f, all -> 0x006c }
                r7.moveToFirst()     // Catch:{ Exception -> 0x0070 }
            L_0x001c:
                boolean r8 = r7.isAfterLast()     // Catch:{ Exception -> 0x0070 }
                if (r8 != 0) goto L_0x0073
                java.lang.String r8 = "package_name"
                int r8 = r7.getColumnIndex(r8)     // Catch:{ Exception -> 0x0070 }
                java.lang.String r4 = r7.getString(r8)     // Catch:{ Exception -> 0x0070 }
                java.lang.String r8 = "package_uid"
                int r8 = r7.getColumnIndex(r8)     // Catch:{ Exception -> 0x0070 }
                int r2 = r7.getInt(r8)     // Catch:{ Exception -> 0x0070 }
                android.content.pm.ApplicationInfo r8 = r12.a(r4, r2)     // Catch:{ Exception -> 0x0070 }
                if (r8 == 0) goto L_0x0063
                android.content.pm.PackageManager r9 = r12.f4852b     // Catch:{ Exception -> 0x0070 }
                java.lang.String r10 = r8.packageName     // Catch:{ Exception -> 0x0070 }
                android.content.Intent r9 = r9.getLaunchIntentForPackage(r10)     // Catch:{ Exception -> 0x0070 }
                if (r9 == 0) goto L_0x0063
                int r9 = r8.flags     // Catch:{ Exception -> 0x0070 }
                r10 = 8388608(0x800000, float:1.17549435E-38)
                r9 = r9 & r10
                if (r9 == 0) goto L_0x0063
                r3.add(r8)     // Catch:{ Exception -> 0x0070 }
                java.lang.String r9 = b.b.c.j.x.a((android.content.Context) r1, (android.content.pm.ApplicationInfo) r8)     // Catch:{ Exception -> 0x0070 }
                com.miui.gamebooster.model.d r10 = new com.miui.gamebooster.model.d     // Catch:{ Exception -> 0x0070 }
                android.content.pm.PackageManager r11 = r12.f4852b     // Catch:{ Exception -> 0x0070 }
                android.graphics.drawable.Drawable r11 = r8.loadIcon(r11)     // Catch:{ Exception -> 0x0070 }
                r10.<init>(r8, r5, r9, r11)     // Catch:{ Exception -> 0x0070 }
                r0.add(r10)     // Catch:{ Exception -> 0x0070 }
                goto L_0x0066
            L_0x0063:
                com.miui.gamebooster.m.C0391w.a((android.content.Context) r1, (java.lang.String) r4, (int) r2, (boolean) r5, (int) r6)     // Catch:{ Exception -> 0x0070 }
            L_0x0066:
                r7.moveToNext()     // Catch:{ Exception -> 0x0070 }
                goto L_0x001c
            L_0x006a:
                r0 = move-exception
                goto L_0x0077
            L_0x006c:
                r0 = move-exception
                r7 = r4
                goto L_0x0077
            L_0x006f:
                r7 = r4
            L_0x0070:
                com.miui.gamebooster.m.C0391w.a((android.content.Context) r1, (java.lang.String) r4, (int) r2, (boolean) r5, (int) r6)     // Catch:{ all -> 0x006a }
            L_0x0073:
                miui.util.IOUtils.closeQuietly(r7)
                return r0
            L_0x0077:
                miui.util.IOUtils.closeQuietly(r7)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.ui.AdvancedSettingsFragment.a.loadInBackground():java.util.ArrayList");
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<ArrayList<C0398d>> loader, ArrayList<C0398d> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.f4850b.a(arrayList);
            this.f4850b.notifyDataSetChanged();
        }
    }

    public void a(r rVar) {
        this.f4851c = rVar;
    }

    public boolean a(View view, g gVar, int i) {
        return false;
    }

    public void b(View view, g gVar, int i) {
        List<C0398d> c2;
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed() && (c2 = this.f4850b.c()) != null && c2.size() > i) {
            C0398d dVar = c2.get(i);
            String charSequence = dVar.d().toString();
            String str = dVar.b().packageName;
            int i2 = dVar.b().uid;
            if (this.f4851c != null) {
                this.f4851c.a(AdvancedSettingsDetailFragment.a(charSequence, str, i2));
                return;
            }
            Intent intent = new Intent(activity, AdvanceSettingsDetailActivity.class);
            intent.putExtra("label", charSequence);
            intent.putExtra("pkg", str);
            intent.putExtra("pkg_uid", i2);
            Log.d("AdvancedSettingsFrag", "pkg_uid = " + intent.getIntExtra("pkg_uid", -1234));
            startActivity(intent);
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        Activity activity = getActivity();
        this.f4849a = (RecyclerView) findViewById(R.id.listview);
        this.f4849a.setLayoutManager(new LinearLayoutManager(activity));
        this.f4850b = new f<>(activity);
        this.f4850b.a((f.a) this);
        this.f4849a.setAdapter(this.f4850b);
        boolean z = this.mActivity.getRequestedOrientation() == 6;
        if (z) {
            this.f4850b.a(1, new b());
            this.f4850b.a(new C0398d((ApplicationInfo) null, false, (CharSequence) null, (Drawable) null));
        } else {
            this.f4849a.setSpringEnabled(false);
        }
        this.f4850b.a(2, new com.miui.gamebooster.a.a.a.a(z));
        getLoaderManager().initLoader(112, (Bundle) null, this);
    }

    public Loader<ArrayList<C0398d>> onCreateLoader(int i, Bundle bundle) {
        return new a(this.mAppContext);
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_advanced_settings;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    public void onLoaderReset(Loader<ArrayList<C0398d>> loader) {
    }
}
