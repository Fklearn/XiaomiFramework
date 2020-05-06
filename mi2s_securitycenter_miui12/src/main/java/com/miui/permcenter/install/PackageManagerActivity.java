package com.miui.permcenter.install;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidxc.recyclerview.widget.LinearLayoutManager;
import b.b.c.j.r;
import com.miui.common.stickydecoration.b.c;
import com.miui.common.stickydecoration.f;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.widget.SlidingButton;
import miuix.recyclerview.widget.RecyclerView;

public class PackageManagerActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<g>, CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private RecyclerView f6128a;

    /* renamed from: b  reason: collision with root package name */
    private a f6129b;

    /* renamed from: c  reason: collision with root package name */
    private View f6130c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public View f6131d;
    private TextView e;
    private RecyclerView.f f;
    private ArrayList<String> g = new ArrayList<>();
    private BroadcastReceiver h = new i(this);

    public static class a extends RecyclerView.a<C0058a> {

        /* renamed from: a  reason: collision with root package name */
        private Context f6132a;

        /* renamed from: b  reason: collision with root package name */
        private g f6133b = new g();

        /* renamed from: c  reason: collision with root package name */
        private d f6134c;

        /* renamed from: d  reason: collision with root package name */
        private CompoundButton.OnCheckedChangeListener f6135d;

        /* renamed from: com.miui.permcenter.install.PackageManagerActivity$a$a  reason: collision with other inner class name */
        static class C0058a extends RecyclerView.u {

            /* renamed from: a  reason: collision with root package name */
            ImageView f6136a;

            /* renamed from: b  reason: collision with root package name */
            TextView f6137b;

            /* renamed from: c  reason: collision with root package name */
            TextView f6138c;

            /* renamed from: d  reason: collision with root package name */
            SlidingButton f6139d;

            public C0058a(@NonNull View view, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
                super(view);
                this.f6136a = (ImageView) view.findViewById(R.id.icon);
                this.f6137b = (TextView) view.findViewById(R.id.title);
                this.f6138c = (TextView) view.findViewById(R.id.procIsRunning);
                this.f6139d = view.findViewById(R.id.sliding_button);
                this.f6139d.setOnPerformCheckedChangeListener(onCheckedChangeListener);
            }
        }

        public a(Context context) {
            this.f6132a = context;
            this.f6134c = d.a(context);
        }

        public void a(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            this.f6135d = onCheckedChangeListener;
        }

        /* renamed from: a */
        public void onBindViewHolder(@NonNull C0058a aVar, int i) {
            h hVar = this.f6133b.a().get(i);
            r.a("file://".concat(this.f6134c.c(hVar.c()).getAbsolutePath()), aVar.f6136a, r.f, 17301651);
            boolean z = true;
            aVar.itemView.setClickable(true);
            aVar.f6137b.setText(hVar.b());
            aVar.f6139d.setTag(hVar);
            aVar.f6138c.setVisibility(8);
            SlidingButton slidingButton = aVar.f6139d;
            if (hVar.a() != 0) {
                z = false;
            }
            slidingButton.setChecked(z);
        }

        public void a(g gVar) {
            if (this.f6133b.a() != null) {
                this.f6133b.a().clear();
            }
            this.f6133b = gVar;
            notifyDataSetChanged();
        }

        public int getItemCount() {
            if (this.f6133b.a() != null) {
                return this.f6133b.a().size();
            }
            return 0;
        }

        @NonNull
        public C0058a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new C0058a(LayoutInflater.from(this.f6132a).inflate(R.layout.pm_auto_start_list_item_view, viewGroup, false), this.f6135d);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.permcenter.install.PackageManagerActivity] */
    private void a(CompoundButton compoundButton, boolean z) {
        String c2 = ((h) compoundButton.getTag()).c();
        if (z) {
            this.g.remove(c2);
        } else {
            this.g.add(c2);
        }
        d.a((Context) this).a(c2, z ^ true ? 1 : 0);
    }

    private void a(g gVar) {
        if (gVar.a() != null) {
            List<h> a2 = gVar.a();
            this.f6128a.b(this.f);
            SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < a2.size(); i++) {
                sparseArray.put(i, a2.get(i).b());
            }
            this.f = f.a.a((c) new m(this, sparseArray)).a();
            this.f6128a.a(this.f);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, android.widget.CompoundButton$OnCheckedChangeListener, com.miui.permcenter.install.PackageManagerActivity] */
    private void l() {
        this.f6131d = findViewById(R.id.message_layout);
        this.e = (TextView) findViewById(R.id.message);
        findViewById(R.id.close).setOnClickListener(new j(this));
        this.f6128a = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.j(1);
        this.f6128a.setLayoutManager(linearLayoutManager);
        this.f6130c = findViewById(R.id.empty_view);
        this.f6129b = new a(this);
        this.f6128a.setAdapter(this.f6129b);
        this.f6129b.a((CompoundButton.OnCheckedChangeListener) this);
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.install.PackageManagerActivity] */
    /* access modifiers changed from: private */
    public void m() {
        String str;
        d a2 = d.a((Context) this);
        int c2 = a2.c();
        String d2 = a2.d();
        if (c2 > 0 && !TextUtils.isEmpty(d2)) {
            a2.a();
            this.f6131d.setVisibility(0);
            if (c2 > 1) {
                str = getString(R.string.recently_reject_message, new Object[]{d2, Integer.valueOf(c2)});
            } else {
                str = getString(R.string.recently_reject_message_one, new Object[]{d2});
            }
            this.e.setText(str);
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<g> loader, g gVar) {
        if (gVar != null) {
            if (gVar.a() != null && gVar.a().size() > 0) {
                this.f6130c.setVisibility(8);
            }
            this.f6129b.a(gVar);
            a(gVar);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.install.PackageManagerActivity] */
    public void finish() {
        PackageManagerActivity.super.finish();
        d a2 = d.a((Context) this);
        Iterator<String> it = this.g.iterator();
        while (it.hasNext()) {
            String next = it.next();
            a2.e(next);
            com.miui.permcenter.a.a.a(next);
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        a(compoundButton, z);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, android.app.LoaderManager$LoaderCallbacks, miui.app.Activity, com.miui.permcenter.install.PackageManagerActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        ArrayList<String> stringArrayList;
        super.onCreate(bundle);
        setContentView(R.layout.pm_activity_package_manager);
        l();
        getLoaderManager().initLoader(50, (Bundle) null, this);
        if (!(bundle == null || (stringArrayList = bundle.getStringArrayList("packages")) == null || stringArrayList.size() <= 0)) {
            this.g = stringArrayList;
        }
        m();
        LocalBroadcastManager.getInstance(this).registerReceiver(this.h, new IntentFilter("com.miui.permcenter.install.action_data_change"));
    }

    public Loader<g> onCreateLoader(int i, Bundle bundle) {
        return new l(this, getApplicationContext());
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.permcenter.install.PackageManagerActivity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        PackageManagerActivity.super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.h);
    }

    public void onLoaderReset(Loader<g> loader) {
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        PackageManagerActivity.super.onSaveInstanceState(bundle);
        bundle.putStringArrayList("packages", this.g);
    }
}
