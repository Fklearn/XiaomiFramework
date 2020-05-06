package com.miui.antivirus.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.b.d.n;
import b.b.b.p;
import b.b.c.j.A;
import b.b.c.j.r;
import com.miui.antivirus.model.e;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import miuix.recyclerview.widget.RecyclerView;

public class SignExceptionActivity extends b.b.c.c.a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private b f2695a;

    /* renamed from: b  reason: collision with root package name */
    private RecyclerView f2696b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f2697c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Button f2698d;
    private TextView e;

    private static class a extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        View f2699a;

        /* renamed from: b  reason: collision with root package name */
        ImageView f2700b;

        /* renamed from: c  reason: collision with root package name */
        TextView f2701c;

        /* renamed from: d  reason: collision with root package name */
        CheckBox f2702d;

        private a(@NonNull View view) {
            super(view);
            this.f2699a = view;
            this.f2700b = (ImageView) view.findViewById(R.id.icon);
            this.f2701c = (TextView) view.findViewById(R.id.title);
            this.f2702d = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    private class b extends RecyclerView.a<a> {

        /* renamed from: a  reason: collision with root package name */
        private LayoutInflater f2703a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public Set<String> f2704b = new HashSet();

        /* renamed from: c  reason: collision with root package name */
        private List<com.miui.antivirus.model.a> f2705c = new ArrayList();

        public b(Context context) {
            this.f2703a = LayoutInflater.from(context);
        }

        /* renamed from: a */
        public void onBindViewHolder(@NonNull a aVar, int i) {
            e eVar = (e) this.f2705c.get(i);
            r.a("pkg_icon://" + eVar.m(), aVar.f2700b, r.f);
            aVar.f2701c.setText(eVar.h());
            aVar.f2702d.setTag(eVar);
            aVar.f2702d.setChecked(false);
            aVar.f2702d.setOnCheckedChangeListener(new H(this));
            aVar.f2699a.setOnClickListener(new I(this, aVar));
        }

        public void a(List<com.miui.antivirus.model.a> list) {
            this.f2704b.clear();
            this.f2705c.clear();
            if (list != null) {
                this.f2705c.addAll(list);
            }
        }

        public Set<String> b() {
            return this.f2704b;
        }

        public int getItemCount() {
            return this.f2705c.size();
        }

        @NonNull
        public a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new a(this.f2703a.inflate(R.layout.sp_sign_whitelist_item_view, viewGroup, false));
        }
    }

    private void a(List<com.miui.antivirus.model.a> list) {
        if (list == null || list.isEmpty()) {
            this.f2698d.setVisibility(8);
            this.f2697c.setVisibility(8);
            this.e.setVisibility(0);
        } else {
            this.f2698d.setVisibility(0);
            this.f2697c.setVisibility(0);
            int color = getResources().getColor(R.color.high_light_green);
            String quantityString = getResources().getQuantityString(R.plurals.manual_white_list_header, list.size(), new Object[]{Integer.valueOf(list.size())});
            this.f2697c.setText(A.a(quantityString, color, String.valueOf(list.size())));
            this.e.setVisibility(8);
        }
        this.f2698d.setEnabled(true ^ this.f2695a.b().isEmpty());
        this.f2695a.a(list);
        this.f2695a.notifyDataSetChanged();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.SignExceptionActivity] */
    private void a(Set<String> set) {
        ArrayList arrayList = new ArrayList(p.g());
        arrayList.removeAll(set);
        p.c((ArrayList<String>) arrayList);
        l();
        Toast.makeText(this, R.string.sp_toast_removed_from_sign_white_list, 0).show();
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, com.miui.antivirus.activity.SignExceptionActivity] */
    private void l() {
        List<com.miui.antivirus.model.a> b2;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(p.g());
        if (!arrayList2.isEmpty() && (b2 = n.b(this)) != null) {
            for (com.miui.antivirus.model.a next : b2) {
                if (arrayList2.contains(((e) next).m())) {
                    arrayList.add(next);
                }
            }
        }
        a((List<com.miui.antivirus.model.a>) arrayList);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.cleanup_btn) {
            a(this.f2695a.b());
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [b.b.c.c.a, android.content.Context, android.view.View$OnClickListener, com.miui.antivirus.activity.SignExceptionActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.sp_sign_whitelist);
        this.f2698d = (Button) findViewById(R.id.cleanup_btn);
        this.f2698d.setOnClickListener(this);
        this.f2698d.setText(R.string.button_text_delete_from_virus_white_list);
        this.e = (TextView) findViewById(R.id.empty_view);
        this.e.setText(R.string.sp_empty_title_sign_exception);
        this.f2695a = new b(this);
        this.f2696b = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.app_list);
        this.f2696b.setLayoutManager(new LinearLayoutManager(this));
        this.f2696b.setAdapter(this.f2695a);
        this.f2697c = (TextView) findViewById(R.id.header_title);
        l();
    }
}
