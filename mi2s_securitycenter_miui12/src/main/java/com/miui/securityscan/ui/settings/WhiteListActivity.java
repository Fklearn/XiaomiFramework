package com.miui.securityscan.ui.settings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.A;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.m;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.model.ModelFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import miuix.recyclerview.widget.RecyclerView;

public class WhiteListActivity extends b.b.c.c.a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private c f8013a;

    /* renamed from: b  reason: collision with root package name */
    private RecyclerView f8014b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f8015c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Button f8016d;
    private TextView e;

    private class a extends AsyncTask<Void, Void, List<AbsModel>> {
        private a() {
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [android.content.Context, com.miui.securityscan.ui.settings.WhiteListActivity] */
        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<AbsModel> doInBackground(Void... voidArr) {
            List<GroupModel> produceManualGroupModel;
            List<String> b2 = m.b();
            if (b2 == null || b2.isEmpty() || (produceManualGroupModel = ModelFactory.produceManualGroupModel(WhiteListActivity.this)) == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (GroupModel modelList : produceManualGroupModel) {
                for (AbsModel next : modelList.getModelList()) {
                    if (b2.contains(next.getItemKey())) {
                        arrayList.add(next);
                    }
                }
            }
            return arrayList;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<AbsModel> list) {
            WhiteListActivity.this.a(list);
        }
    }

    private class b extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private Set<String> f8018a;

        public b(Set<String> set) {
            this.f8018a = set;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            for (String b2 : this.f8018a) {
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
        private LayoutInflater f8020a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public Set<String> f8021b = new HashSet();

        /* renamed from: c  reason: collision with root package name */
        private List<AbsModel> f8022c = new ArrayList();

        public c(Context context) {
            this.f8020a = LayoutInflater.from(context);
        }

        /* renamed from: a */
        public void onBindViewHolder(@NonNull d dVar, int i) {
            AbsModel absModel = this.f8022c.get(i);
            dVar.f8024a.setText(absModel.getTitle());
            dVar.f8025b.setChecked(false);
            dVar.f8025b.setTag(absModel);
            dVar.f8025b.setOnCheckedChangeListener(new d(this));
        }

        public void a(List<AbsModel> list) {
            this.f8021b.clear();
            this.f8022c.clear();
            if (list != null) {
                this.f8022c.addAll(list);
            }
        }

        public Set<String> b() {
            return this.f8021b;
        }

        public int getItemCount() {
            return this.f8022c.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        @NonNull
        public d onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new d(this.f8020a.inflate(R.layout.ma_white_list_item_view, viewGroup, false));
        }
    }

    static class d extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        TextView f8024a;

        /* renamed from: b  reason: collision with root package name */
        CheckBox f8025b;

        public d(View view) {
            super(view);
            this.f8024a = (TextView) view.findViewById(R.id.title);
            this.f8025b = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    /* access modifiers changed from: private */
    public void a(List<AbsModel> list) {
        if (list == null || list.isEmpty()) {
            this.f8016d.setVisibility(8);
            this.f8015c.setVisibility(8);
            this.f8014b.setVisibility(8);
            this.e.setVisibility(0);
        } else {
            this.e.setVisibility(8);
            this.f8016d.setVisibility(0);
            this.f8015c.setVisibility(0);
            this.f8014b.setVisibility(0);
            int color = getResources().getColor(R.color.high_light_green);
            String quantityString = getResources().getQuantityString(R.plurals.manual_white_list_header, list.size(), new Object[]{Integer.valueOf(list.size())});
            this.f8015c.setText(A.a(quantityString, color, String.valueOf(list.size())));
        }
        this.f8016d.setEnabled(true ^ this.f8013a.b().isEmpty());
        this.f8013a.a(list);
        this.f8013a.notifyDataSetChanged();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.cleanup_btn) {
            new b(this.f8013a.b()).executeOnExecutor(Executors.newFixedThreadPool(1), new Void[0]);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, com.miui.securityscan.ui.settings.WhiteListActivity, android.view.View$OnClickListener, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.m_activity_appinfo_entry);
        this.f8016d = (Button) findViewById(R.id.cleanup_btn);
        this.f8016d.setOnClickListener(this);
        this.f8016d.setText(R.string.delete_from_manual_item_white_list);
        this.e = (TextView) findViewById(R.id.empty_view);
        this.e.setText(R.string.empty_title_manual_item_white_list);
        this.f8013a = new c(this);
        this.f8014b = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.app_list);
        this.f8014b.setLayoutManager(new LinearLayoutManager(this));
        this.f8014b.setAdapter(this.f8013a);
        this.f8015c = (TextView) findViewById(R.id.header_title);
        new a().executeOnExecutor(Executors.newFixedThreadPool(1), new Void[0]);
    }
}
