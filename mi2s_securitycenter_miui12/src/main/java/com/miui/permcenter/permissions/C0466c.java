package com.miui.permcenter.permissions;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidxc.recyclerview.widget.LinearLayoutManager;
import b.b.c.j.x;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import com.miui.permcenter.n;
import com.miui.permcenter.permissions.o;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.app.AlertDialog;
import miui.view.SearchActionMode;
import miuix.recyclerview.widget.RecyclerView;

/* renamed from: com.miui.permcenter.permissions.c  reason: case insensitive filesystem */
public class C0466c extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<com.miui.permcenter.a>>, o.a, View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    public static final String f6254a = "c";

    /* renamed from: b  reason: collision with root package name */
    private ArrayList<com.miui.permcenter.a> f6255b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    private ArrayList<com.miui.permcenter.a> f6256c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    private o f6257d;
    /* access modifiers changed from: private */
    public RecyclerView e;
    private View f;
    /* access modifiers changed from: private */
    public View g;
    private TextView h;
    /* access modifiers changed from: private */
    public boolean i;
    private boolean j;
    private a k;
    /* access modifiers changed from: private */
    public TextWatcher l = new C0464a(this);
    protected SearchActionMode m;
    private SearchActionMode.Callback n = new C0465b(this);

    /* renamed from: com.miui.permcenter.permissions.c$a */
    static class a extends b.b.c.i.a<ArrayList<com.miui.permcenter.a>> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<C0466c> f6258b;

        public a(C0466c cVar) {
            super(cVar.getActivity().getApplicationContext());
            this.f6258b = new WeakReference<>(cVar);
        }

        public ArrayList<com.miui.permcenter.a> loadInBackground() {
            C0466c cVar = (C0466c) this.f6258b.get();
            if (isLoadInBackgroundCanceled() || cVar == null) {
                return null;
            }
            boolean unused = cVar.i = n.b(cVar.getActivity().getApplicationContext());
            ArrayList<com.miui.permcenter.a> c2 = n.c(getContext());
            if (cVar.c() != null) {
                c2.add(cVar.c());
            }
            return c2;
        }
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        e();
        Iterator<com.miui.permcenter.a> it = this.f6255b.iterator();
        while (it.hasNext()) {
            com.miui.permcenter.a next = it.next();
            String d2 = next.d();
            if (d2.toLowerCase().indexOf(str.toLowerCase()) >= 0 || ((d2 != null && d2.toLowerCase().startsWith(str.toLowerCase())) || d2.toLowerCase().contains(str.toLowerCase()))) {
                this.f6256c.add(next);
            }
        }
        this.f6257d.a((List<com.miui.permcenter.a>) this.f6256c);
    }

    /* access modifiers changed from: private */
    public com.miui.permcenter.a c() {
        if (!x.b((Context) getActivity(), new Intent(HybirdServiceUtil.ACTION_HYBIRD_PERMISSIONS))) {
            return null;
        }
        com.miui.permcenter.a aVar = new com.miui.permcenter.a();
        aVar.b(HybirdServiceUtil.HYBIRD_PACKAGE_NAME);
        try {
            aVar.a(x.a((Context) getActivity(), getActivity().getPackageManager().getApplicationInfo(HybirdServiceUtil.HYBIRD_PACKAGE_NAME, 0)));
            return aVar;
        } catch (Exception e2) {
            Log.e(f6254a, "constructHybridPermissionInfo error", e2);
            return null;
        }
    }

    private void d() {
        if (isAdded()) {
            this.h.setHint(getResources().getQuantityString(R.plurals.find_applications, this.f6255b.size(), new Object[]{Integer.valueOf(this.f6255b.size())}));
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        if (!this.f6256c.isEmpty()) {
            this.f6256c.clear();
        }
    }

    private void f() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.permission_permission_control_closed_desc_dialog_title).setMessage(R.string.permission_permission_control_closed_desc_dialog_msg).setPositiveButton(R.string.button_text_known, (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void g() {
        View view;
        if (this.f6255b != null) {
            int i2 = 0;
            boolean z = b() && this.f6256c.isEmpty() && TextUtils.isEmpty(this.m.getSearchInput().getText().toString());
            if (!b() || z) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.f6255b);
                if (!this.f6255b.isEmpty()) {
                    this.f6257d.a((List<com.miui.permcenter.a>) arrayList);
                    view = this.f;
                    i2 = 8;
                } else {
                    this.f6257d.a((List<com.miui.permcenter.a>) arrayList);
                    view = this.f;
                }
                view.setVisibility(i2);
                d();
                return;
            }
            this.f6257d.a((List<com.miui.permcenter.a>) this.f6256c);
        }
    }

    public void a() {
        if (this.m != null) {
            this.m = null;
        }
    }

    public void a(int i2, View view, com.miui.permcenter.a aVar) {
        String e2 = aVar.e();
        if (e2.equals(HybirdServiceUtil.HYBIRD_PACKAGE_NAME)) {
            startActivity(new Intent(HybirdServiceUtil.ACTION_HYBIRD_PERMISSIONS));
            return;
        }
        Intent intent = new Intent(getActivity(), PermissionsEditorActivity.class);
        intent.putExtra(":miui:starting_window_label", x.j(getActivity(), e2));
        intent.putExtra("extra_pkgname", e2);
        startActivity(intent);
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<ArrayList<com.miui.permcenter.a>> loader, ArrayList<com.miui.permcenter.a> arrayList) {
        this.f6255b.addAll(arrayList);
        this.f6257d.a((List<com.miui.permcenter.a>) arrayList);
        if (!this.i && !this.j) {
            f();
        }
        d();
    }

    public void a(SearchActionMode.Callback callback) {
        if (getActivity() != null) {
            this.m = getActivity().startActionMode(callback);
        }
    }

    public boolean b() {
        return this.m != null;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.f6257d = new o(getActivity());
        this.f6257d.a((o.a) this);
        this.e.setAdapter(this.f6257d);
        getLoaderManager().initLoader(100, (Bundle) null, this);
    }

    public void onClick(View view) {
        if (view == this.g) {
            a(this.n);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.j = bundle != null;
    }

    public Loader<ArrayList<com.miui.permcenter.a>> onCreateLoader(int i2, Bundle bundle) {
        this.k = new a(this);
        return this.k;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.pm_fragment_apps, (ViewGroup) null);
        this.e = (RecyclerView) inflate.findViewById(R.id.apps_list);
        this.f = inflate.findViewById(R.id.empty_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.j(1);
        this.e.setLayoutManager(linearLayoutManager);
        this.g = inflate.findViewById(R.id.am_search_view);
        this.h = (TextView) this.g.findViewById(16908297);
        this.g.setOnClickListener(this);
        return inflate;
    }

    public void onDestroy() {
        super.onDestroy();
        a aVar = this.k;
        if (aVar != null) {
            aVar.cancelLoad();
        }
    }

    public void onLoaderReset(Loader<ArrayList<com.miui.permcenter.a>> loader) {
    }
}
