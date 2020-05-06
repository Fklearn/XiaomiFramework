package com.miui.firstaidkit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.A;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.m;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.ModelFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import miuix.recyclerview.widget.RecyclerView;

public class FirstAidKitWhiteListActivity extends b.b.c.c.a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private c f3884a;

    /* renamed from: b  reason: collision with root package name */
    private RecyclerView f3885b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f3886c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Button f3887d;
    private TextView e;
    private View f;

    private class a extends AsyncTask<Void, Void, List<AbsModel>> {
        private a() {
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [android.content.Context, com.miui.firstaidkit.FirstAidKitWhiteListActivity] */
        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<AbsModel> doInBackground(Void... voidArr) {
            List<AbsModel> produceFirstAidKitGroupModel;
            List<String> b2 = m.b();
            if (b2 == null || b2.isEmpty() || (produceFirstAidKitGroupModel = ModelFactory.produceFirstAidKitGroupModel(FirstAidKitWhiteListActivity.this)) == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (AbsModel next : produceFirstAidKitGroupModel) {
                if (b2.contains(next.getItemKey())) {
                    arrayList.add(next);
                }
            }
            return arrayList;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<AbsModel> list) {
            FirstAidKitWhiteListActivity.this.a(list);
        }
    }

    private class b extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Set<String> f3889a;

        public b(Set<String> set) {
            this.f3889a = set;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            for (String b2 : this.f3889a) {
                m.b(b2);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            new a().executeOnExecutor(Executors.newFixedThreadPool(1), new Void[0]);
        }
    }

    private class c extends RecyclerView.a<d> {

        /* renamed from: a  reason: collision with root package name */
        private LayoutInflater f3891a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public Set<String> f3892b = new HashSet();

        /* renamed from: c  reason: collision with root package name */
        private List<AbsModel> f3893c = new ArrayList();

        public c(Context context) {
            this.f3891a = LayoutInflater.from(context);
        }

        /* renamed from: a */
        public void onBindViewHolder(@NonNull d dVar, int i) {
            AbsModel absModel = this.f3893c.get(i);
            dVar.f3895a.setText(absModel.getTitle());
            dVar.f3896b.setChecked(false);
            dVar.f3896b.setTag(absModel);
            dVar.f3896b.setOnCheckedChangeListener(new q(this));
        }

        public void a(List<AbsModel> list) {
            this.f3892b.clear();
            this.f3893c.clear();
            if (list != null) {
                this.f3893c.addAll(list);
            }
        }

        public Set<String> b() {
            return this.f3892b;
        }

        public int getItemCount() {
            return this.f3893c.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        @NonNull
        public d onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new d(this.f3891a.inflate(R.layout.ma_white_list_item_view, viewGroup, false));
        }
    }

    static class d extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        TextView f3895a;

        /* renamed from: b  reason: collision with root package name */
        CheckBox f3896b;

        public d(View view) {
            super(view);
            this.f3895a = (TextView) view.findViewById(R.id.title);
            this.f3896b = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    /* access modifiers changed from: private */
    public void a(List<AbsModel> list) {
        if (list == null || list.isEmpty()) {
            this.f3887d.setVisibility(8);
            this.f3886c.setVisibility(8);
            this.f.setVisibility(8);
            this.f3885b.setVisibility(8);
            this.e.setVisibility(0);
        } else {
            this.e.setVisibility(8);
            this.f3887d.setVisibility(0);
            this.f3886c.setVisibility(0);
            this.f.setVisibility(0);
            this.f3885b.setVisibility(0);
            int color = getResources().getColor(R.color.high_light_green);
            int size = list.size();
            String quantityString = getResources().getQuantityString(R.plurals.manual_white_list_header, size, new Object[]{Integer.valueOf(size)});
            this.f3886c.setText(A.a(quantityString, color, String.valueOf(list.size())));
        }
        this.f3887d.setEnabled(true ^ this.f3884a.b().isEmpty());
        this.f3884a.a(list);
        this.f3884a.notifyDataSetChanged();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.cleanup_btn) {
            new b(this.f3884a.b()).executeOnExecutor(Executors.newFixedThreadPool(1), new Void[0]);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, android.view.View$OnClickListener, com.miui.firstaidkit.FirstAidKitWhiteListActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.m_activity_appinfo_entry);
        this.f3887d = (Button) findViewById(R.id.cleanup_btn);
        this.f3887d.setOnClickListener(this);
        this.f3887d.setText(R.string.delete_from_manual_item_white_list);
        this.e = (TextView) findViewById(R.id.empty_view);
        this.e.setText(R.string.empty_title_manual_item_white_list);
        this.f3884a = new c(this);
        this.f3885b = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.app_list);
        this.f3885b.setLayoutManager(new LinearLayoutManager(this));
        this.f3885b.setAdapter(this.f3884a);
        this.f3886c = (TextView) findViewById(R.id.header_title);
        this.f = findViewById(R.id.divider);
        new a().executeOnExecutor(Executors.newFixedThreadPool(1), new Void[0]);
    }
}
