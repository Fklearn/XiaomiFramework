package com.miui.securityscan.shortcut;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.cleanmaster.f;
import com.miui.securitycenter.R;
import com.miui.securityscan.shortcut.e;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miuix.recyclerview.widget.RecyclerView;

public class ShortcutActivity extends b.b.c.c.a implements LoaderManager.LoaderCallbacks<List<c>> {

    /* renamed from: a  reason: collision with root package name */
    private RecyclerView f7941a;

    /* renamed from: b  reason: collision with root package name */
    private c f7942b;

    /* renamed from: c  reason: collision with root package name */
    private b f7943c;

    private static class a extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ShortcutListItemView f7944a;

        public a(View view) {
            super(view);
            this.f7944a = (ShortcutListItemView) view;
        }
    }

    private static class b extends b.b.c.i.a<List<c>> {

        /* renamed from: b  reason: collision with root package name */
        private Context f7945b;

        /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, miui.app.Activity, com.miui.securityscan.shortcut.ShortcutActivity] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public b(com.miui.securityscan.shortcut.ShortcutActivity r1) {
            /*
                r0 = this;
                r0.<init>(r1)
                android.content.Context r1 = r1.getApplicationContext()
                r0.f7945b = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.shortcut.ShortcutActivity.b.<init>(com.miui.securityscan.shortcut.ShortcutActivity):void");
        }

        public List<c> loadInBackground() {
            if (isLoadInBackgroundCanceled()) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(new c(e.a.QUICk_CLEANUP, this.f7945b));
            arrayList.add(new c(e.a.POWER_CLEANUP, this.f7945b));
            if (f.a(this.f7945b)) {
                arrayList.add(new c(e.a.CLEANMASTER, this.f7945b));
            }
            arrayList.add(new c(e.a.NETWORK_ASSISTANT, this.f7945b));
            arrayList.add(new c(e.a.ANTISPAM, this.f7945b));
            arrayList.add(new c(e.a.POWER_CENTER, this.f7945b));
            arrayList.add(new c(e.a.VIRUS_CENTER, this.f7945b));
            arrayList.add(new c(e.a.PERM_CENTER, this.f7945b));
            if (!Build.IS_INTERNATIONAL_BUILD) {
                arrayList.add(new c(e.a.NETWORK_DIAGNOSTICS, this.f7945b));
                arrayList.add(new c(e.a.LUCKY_MONEY, this.f7945b));
            }
            if (isLoadInBackgroundCanceled()) {
                return null;
            }
            return arrayList;
        }
    }

    private class c extends RecyclerView.a<a> {

        /* renamed from: a  reason: collision with root package name */
        private List<c> f7946a;

        /* renamed from: b  reason: collision with root package name */
        private Context f7947b;

        public c(Context context) {
            this.f7947b = context;
        }

        /* renamed from: a */
        public void onBindViewHolder(@NonNull a aVar, int i) {
            aVar.f7944a.a(this.f7946a.get(i));
            aVar.f7944a.setOnClickListener(new a(this, aVar));
        }

        public void a(List<c> list) {
            this.f7946a = list;
        }

        public int getItemCount() {
            List<c> list = this.f7946a;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        @NonNull
        public a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new a(LayoutInflater.from(this.f7947b).inflate(R.layout.op_shortcut_list_item_view, viewGroup, false));
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<List<c>> loader, List<c> list) {
        if (list != null) {
            this.f7942b.a(list);
            this.f7942b.notifyDataSetChanged();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, android.app.LoaderManager$LoaderCallbacks, miui.app.Activity, com.miui.securityscan.shortcut.ShortcutActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.op_activity_shortcut);
        this.f7941a = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.list_view);
        this.f7941a.setLayoutManager(new LinearLayoutManager(this));
        this.f7942b = new c(this);
        this.f7941a.setAdapter(this.f7942b);
        getLoaderManager().initLoader(160, (Bundle) null, this);
    }

    public Loader<List<c>> onCreateLoader(int i, Bundle bundle) {
        this.f7943c = new b(this);
        return this.f7943c;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        ShortcutActivity.super.onDestroy();
        b bVar = this.f7943c;
        if (bVar != null) {
            bVar.cancelLoad();
        }
    }

    public void onLoaderReset(Loader<List<c>> loader) {
    }
}
