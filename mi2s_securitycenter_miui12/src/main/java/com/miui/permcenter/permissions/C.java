package com.miui.permcenter.permissions;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import b.b.c.j.x;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.miui.permcenter.n;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.permission.PermissionGroupInfo;
import com.miui.permission.PermissionInfo;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.util.AttributeResolver;
import miuix.preference.s;

public class C extends s implements LoaderManager.LoaderCallbacks<p>, n.c {

    /* renamed from: a  reason: collision with root package name */
    public static final String f6215a = "C";
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public String f6216b = null;

    /* renamed from: c  reason: collision with root package name */
    private boolean f6217c = true;

    /* renamed from: d  reason: collision with root package name */
    private LoaderManager f6218d;
    private b.b.c.i.a<p> e;
    private PackageInfo f;
    private boolean g;
    /* access modifiers changed from: private */
    public Map<Long, String> h = new HashMap();
    /* access modifiers changed from: private */
    public String i;

    static class a extends b.b.c.i.a<p> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<C> f6219b;

        /* renamed from: c  reason: collision with root package name */
        private String f6220c;

        a(C c2, String str) {
            super(c2.getContext().getApplicationContext());
            this.f6219b = new WeakReference<>(c2);
            this.f6220c = str;
        }

        public p loadInBackground() {
            q qVar;
            p pVar = null;
            if (isLoadInBackgroundCanceled()) {
                return null;
            }
            C c2 = (C) this.f6219b.get();
            if (c2 != null && !c2.getActivity().isFinishing() && !c2.getActivity().isDestroyed()) {
                c2.h.putAll(o.a((Context) c2.getActivity(), this.f6220c));
                pVar = new p();
                pVar.f6286b = n.b(c2.getActivity().getApplicationContext());
                HashMap<Long, Integer> b2 = n.b(c2.getActivity().getApplicationContext(), this.f6220c);
                if (!o.b((Context) c2.getActivity()) && b2 != null && b2.containsKey(32L) && b2.containsKey(Long.valueOf(PermissionManager.PERM_ID_BACKGROUND_LOCATION))) {
                    b2.put(32L, Integer.valueOf(n.a(b2.get(32L).intValue(), b2.get(Long.valueOf(PermissionManager.PERM_ID_BACKGROUND_LOCATION)).intValue())));
                }
                ArrayList<q> arrayList = new ArrayList<>();
                pVar.f6287c = b2;
                pVar.f6285a = arrayList;
                if (b2 != null) {
                    PermissionManager instance = PermissionManager.getInstance(c2.getActivity().getApplicationContext());
                    List<PermissionGroupInfo> allPermissionGroups = instance.getAllPermissionGroups(0);
                    List<PermissionInfo> allPermissions = instance.getAllPermissions(0);
                    Set<Long> keySet = b2.keySet();
                    HashMap hashMap = new HashMap();
                    for (PermissionGroupInfo next : allPermissionGroups) {
                        q qVar2 = new q();
                        qVar2.f6288a = next;
                        hashMap.put(Integer.valueOf(next.getId()), qVar2);
                        arrayList.add(qVar2);
                    }
                    for (PermissionInfo next2 : allPermissions) {
                        long id = next2.getId();
                        if (!(!keySet.contains(Long.valueOf(id)) || id == PermissionManager.PERM_ID_BACKGROUND_LOCATION || (qVar = (q) hashMap.get(Integer.valueOf(next2.getGroup()))) == null)) {
                            qVar.f6289b.add(next2);
                        }
                    }
                    ArrayList arrayList2 = new ArrayList();
                    Iterator<q> it = arrayList.iterator();
                    while (it.hasNext()) {
                        q next3 = it.next();
                        if (next3.f6289b.size() == 0) {
                            arrayList2.add(next3);
                        }
                    }
                    Iterator it2 = arrayList2.iterator();
                    while (it2.hasNext()) {
                        arrayList.remove((q) it2.next());
                    }
                }
            }
            return pVar;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x011d  */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLoadFinished(android.content.Loader<com.miui.permcenter.permissions.p> r24, com.miui.permcenter.permissions.p r25) {
        /*
            r23 = this;
            r7 = r23
            r8 = r25
            android.content.Context r9 = r23.getContext()
            boolean r10 = com.miui.permcenter.privacymanager.b.c.a(r9)
            androidx.preference.PreferenceScreen r11 = r23.getPreferenceScreen()
            int r0 = r11.c()
            r12 = 1
            r13 = 0
            if (r0 <= 0) goto L_0x001a
            r0 = r12
            goto L_0x001b
        L_0x001a:
            r0 = r13
        L_0x001b:
            r11.e()
            java.util.ArrayList<com.miui.permcenter.permissions.q> r1 = r8.f6285a
            java.util.Iterator r14 = r1.iterator()
            r1 = r13
        L_0x0025:
            boolean r2 = r14.hasNext()
            if (r2 == 0) goto L_0x01ad
            java.lang.Object r2 = r14.next()
            com.miui.permcenter.permissions.q r2 = (com.miui.permcenter.permissions.q) r2
            if (r10 != 0) goto L_0x003a
            androidx.preference.PreferenceCategory r3 = new androidx.preference.PreferenceCategory
            r3.<init>(r9)
            r15 = r3
            goto L_0x0058
        L_0x003a:
            android.content.res.Resources r3 = r9.getResources()
            r4 = 2131166785(0x7f070641, float:1.7947825E38)
            float r3 = r3.getDimension(r4)
            int r3 = (int) r3
            android.content.res.Resources r4 = r9.getResources()
            r5 = 2131166784(0x7f070640, float:1.7947823E38)
            float r4 = r4.getDimension(r5)
            int r4 = (int) r4
            com.miui.permcenter.permissions.A r5 = new com.miui.permcenter.permissions.A
            r5.<init>(r7, r9, r3, r4)
            r15 = r5
        L_0x0058:
            if (r0 == 0) goto L_0x0060
            r15.setOrder(r13)
            r16 = r13
            goto L_0x0062
        L_0x0060:
            r16 = r0
        L_0x0062:
            boolean r0 = com.miui.permcenter.privacymanager.b.c.a(r9)
            if (r0 == 0) goto L_0x0080
            com.miui.permission.PermissionGroupInfo r0 = r2.f6288a
            int r0 = r0.getId()
            r3 = 16
            if (r0 != r3) goto L_0x0074
            r0 = r12
            goto L_0x0075
        L_0x0074:
            r0 = r13
        L_0x0075:
            boolean r3 = r7.f6217c
            if (r3 == 0) goto L_0x007e
            if (r0 != 0) goto L_0x007e
            r0 = r16
            goto L_0x0025
        L_0x007e:
            r6 = r0
            goto L_0x0081
        L_0x0080:
            r6 = r13
        L_0x0081:
            com.miui.permission.PermissionGroupInfo r0 = r2.f6288a
            java.lang.String r0 = r0.getName()
            r15.setTitle((java.lang.CharSequence) r0)
            r11.b((androidx.preference.Preference) r15)
            java.util.ArrayList<com.miui.permission.PermissionInfo> r0 = r2.f6289b
            java.util.Iterator r17 = r0.iterator()
            r18 = r13
        L_0x0095:
            boolean r0 = r17.hasNext()
            if (r0 == 0) goto L_0x01a1
            java.lang.Object r0 = r17.next()
            r19 = r0
            com.miui.permission.PermissionInfo r19 = (com.miui.permission.PermissionInfo) r19
            long r2 = r19.getId()
            java.lang.String r4 = r19.getName()
            java.util.HashMap<java.lang.Long, java.lang.Integer> r0 = r8.f6287c
            java.lang.Long r5 = java.lang.Long.valueOf(r2)
            java.lang.Object r0 = r0.get(r5)
            r5 = r0
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 != 0) goto L_0x00bb
            goto L_0x0095
        L_0x00bb:
            boolean r0 = com.miui.permcenter.privacymanager.b.c.a(r9)
            if (r0 != 0) goto L_0x00c7
            com.miui.permcenter.permissions.AppPermsEditorPreference r0 = new com.miui.permcenter.permissions.AppPermsEditorPreference
            r0.<init>(r9)
            goto L_0x00db
        L_0x00c7:
            com.miui.permcenter.permissions.AppBasePermsEditorPreference r0 = com.miui.permcenter.permissions.AppBasePermsEditorPreference.a(r9, r6)
            boolean r13 = r0 instanceof com.miui.permcenter.permissions.AppSensitivePermsEditorPreference
            if (r13 == 0) goto L_0x00db
            if (r1 != 0) goto L_0x00db
            r1 = r0
            com.miui.permcenter.permissions.AppSensitivePermsEditorPreference r1 = (com.miui.permcenter.permissions.AppSensitivePermsEditorPreference) r1
            r1.a((boolean) r12)
            r13 = r0
            r20 = r12
            goto L_0x00de
        L_0x00db:
            r13 = r0
            r20 = r1
        L_0x00de:
            r13.setTitle((java.lang.CharSequence) r4)
            java.lang.String r0 = r19.getDesc()
            r13.setSummary((java.lang.CharSequence) r0)
            int r0 = r5.intValue()
            r13.a((int) r0)
            r13.a((long) r2)
            boolean r0 = r7.g
            if (r0 != 0) goto L_0x00fe
            android.content.pm.PackageInfo r0 = r7.f
            boolean r0 = com.miui.permission.RequiredPermissionsUtil.isAdaptedRequiredPermissionsOnData(r0)
            if (r0 == 0) goto L_0x014a
        L_0x00fe:
            android.content.pm.PackageInfo r0 = r7.f
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo
            java.util.List r0 = com.miui.permission.RequiredPermissionsUtil.retrieveRequiredPermissions(r0)
            if (r0 == 0) goto L_0x014a
            java.util.Map<java.lang.String, java.lang.Long> r1 = com.miui.permission.RequiredPermissionsUtil.RUNTIME_PERMISSIONS
            java.lang.Long r12 = java.lang.Long.valueOf(r2)
            boolean r1 = r1.containsValue(r12)
            if (r1 != 0) goto L_0x011d
            r15.d(r13)
            r1 = r20
            r12 = 1
            r13 = 0
            goto L_0x0095
        L_0x011d:
            java.util.Iterator r0 = r0.iterator()
        L_0x0121:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x014a
            java.lang.Object r1 = r0.next()
            java.lang.String r1 = (java.lang.String) r1
            java.util.Map<java.lang.String, java.lang.Long> r12 = com.miui.permission.RequiredPermissionsUtil.RUNTIME_PERMISSIONS
            boolean r12 = r12.containsKey(r1)
            if (r12 == 0) goto L_0x0121
            java.util.Map<java.lang.String, java.lang.Long> r12 = com.miui.permission.RequiredPermissionsUtil.RUNTIME_PERMISSIONS
            java.lang.Object r1 = r12.get(r1)
            java.lang.Long r1 = (java.lang.Long) r1
            long r21 = r1.longValue()
            int r1 = (r21 > r2 ? 1 : (r21 == r2 ? 0 : -1))
            if (r1 != 0) goto L_0x0121
            r1 = 0
            r13.setEnabled(r1)
            goto L_0x0121
        L_0x014a:
            com.miui.permcenter.permissions.B r12 = new com.miui.permcenter.permissions.B
            r0 = r12
            r1 = r23
            r21 = r6
            r6 = r19
            r0.<init>(r1, r2, r4, r5, r6)
            r13.setOnPreferenceClickListener(r12)
            boolean r0 = r8.f6286b
            if (r0 != 0) goto L_0x0161
            r0 = 0
            r13.setEnabled(r0)
        L_0x0161:
            android.content.Context r0 = r23.getContext()
            android.content.Context r0 = r0.getApplicationContext()
            java.lang.String r1 = r7.f6216b
            boolean r0 = com.miui.permcenter.compact.EnterpriseCompat.shouldGrantPermission(r0, r1)
            if (r0 == 0) goto L_0x0193
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Permission edit for package "
            r0.append(r1)
            java.lang.String r1 = r7.f6216b
            r0.append(r1)
            java.lang.String r1 = " is restricted"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "Enterprise"
            android.util.Log.d(r1, r0)
            r0 = 0
            r13.setEnabled(r0)
            goto L_0x0194
        L_0x0193:
            r0 = 0
        L_0x0194:
            r15.b((androidx.preference.Preference) r13)
            int r18 = r18 + 1
            r13 = r0
            r1 = r20
            r6 = r21
            r12 = 1
            goto L_0x0095
        L_0x01a1:
            r0 = r13
            if (r18 != 0) goto L_0x01a7
            r11.d(r15)
        L_0x01a7:
            r13 = r0
            r0 = r16
            r12 = 1
            goto L_0x0025
        L_0x01ad:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.permissions.C.onLoadFinished(android.content.Loader, com.miui.permcenter.permissions.p):void");
    }

    public void a(String str, int i2) {
        this.f6218d.getLoader(110).forceLoad();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.pm_activity_app_permissions_editor);
    }

    public Loader<p> onCreateLoader(int i2, Bundle bundle) {
        this.e = new a(this, this.f6216b);
        return this.e;
    }

    public void onCreatePreferences(Bundle bundle, String str) {
    }

    public void onDestroy() {
        super.onDestroy();
        b.b.c.i.a<p> aVar = this.e;
        if (aVar != null) {
            aVar.cancelLoad();
        }
        LoaderManager loaderManager = this.f6218d;
        if (loaderManager != null) {
            loaderManager.destroyLoader(110);
        }
    }

    public void onLoaderReset(Loader<p> loader) {
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ListView listView = (ListView) view.findViewById(16908298);
        if (listView != null) {
            listView.setClipToPadding(false);
            listView.setPadding(0, 0, 0, (int) AttributeResolver.resolveDimension(view.getContext(), miui.R.attr.preferenceScreenPaddingBottom));
        }
        this.f6216b = getActivity().getIntent().getStringExtra("extra_pkgname");
        this.f6217c = getActivity().getIntent().getBooleanExtra("extra_remove_other_settings", false);
        try {
            this.f = getActivity().getPackageManager().getPackageInfo(this.f6216b, PsExtractor.AUDIO_STREAM);
            boolean z = true;
            if ((this.f.applicationInfo.flags & 1) == 0) {
                z = false;
            }
            this.g = z;
        } catch (Exception e2) {
            Log.e(f6215a, "not found package", e2);
        }
        if (!TextUtils.isEmpty(this.f6216b) && this.f != null) {
            if (getActivity().getPackageName().equals(getActivity().getCallingPackage()) || !"com.android.cts.permissionapp".equals(this.f6216b)) {
                this.i = x.j(getActivity(), this.f6216b).toString();
                getActivity().setTitle(this.i);
                this.f6218d = getLoaderManager();
                Loader loader = this.f6218d.getLoader(110);
                this.f6218d.initLoader(110, (Bundle) null, this);
                if (Build.VERSION.SDK_INT >= 24 && bundle != null && loader != null) {
                    this.f6218d.restartLoader(110, (Bundle) null, this);
                    return;
                }
                return;
            }
            Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
            intent.putExtra("android.intent.extra.PACKAGE_NAME", this.f6216b);
            startActivity(intent);
        }
        getActivity().finish();
    }
}
