package com.miui.optimizecenter.storage;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.c.a;
import com.miui.optimizecenter.storage.model.b;
import com.miui.optimizecenter.storage.view.EmptyView;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.List;

public class AppStorageListActivity extends a {

    /* renamed from: a  reason: collision with root package name */
    private RecyclerView f5681a;

    /* renamed from: b  reason: collision with root package name */
    private h f5682b;

    /* renamed from: c  reason: collision with root package name */
    private s f5683c;

    /* renamed from: d  reason: collision with root package name */
    private List<b> f5684d;
    private View e;
    private EmptyView f;

    private void a(boolean z) {
        if (z) {
            this.f.setVisibility(0);
            this.e.setVisibility(8);
            return;
        }
        this.f.setVisibility(8);
        this.e.setVisibility(0);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [b.b.c.c.a, android.content.Context, com.miui.optimizecenter.storage.AppStorageListActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_storage_app_list);
        this.f5681a = (RecyclerView) findViewById(R.id.apps);
        this.f5683c = s.a((Context) Application.d());
        this.f5684d = this.f5683c.a();
        this.f5682b = new h(this.f5684d);
        this.f5681a.setLayoutManager(new LinearLayoutManager(this));
        this.f5681a.setAdapter(this.f5682b);
        this.e = findViewById(R.id.list_container);
        this.f = (EmptyView) findViewById(R.id.empty_container);
        this.f.setHintView(R.string.empty_title_installed_apps);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AppStorageListActivity.super.onDestroy();
        EmptyView emptyView = this.f;
        if (emptyView != null) {
            emptyView.b();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        EmptyView emptyView = this.f;
        if (emptyView != null) {
            emptyView.c();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.f5683c.c()) {
            this.f5683c.g();
            this.f5682b.notifyDataSetChanged();
        }
        EmptyView emptyView = this.f;
        if (emptyView != null) {
            emptyView.d();
        }
        a(this.f5684d.size() == 0);
    }
}
