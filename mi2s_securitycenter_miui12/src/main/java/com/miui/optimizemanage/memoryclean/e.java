package com.miui.optimizemanage.memoryclean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.x;
import com.miui.gamebooster.view.QRSlidingButton;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class e extends RecyclerView.a<c> {

    /* renamed from: a  reason: collision with root package name */
    private List<c> f5959a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private Context f5960b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public b f5961c;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        int f5962a;

        /* renamed from: b  reason: collision with root package name */
        List<c> f5963b;
    }

    public interface b {
        void a(int i, c cVar);
    }

    public static class c extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ImageView f5964a;

        /* renamed from: b  reason: collision with root package name */
        TextView f5965b;

        /* renamed from: c  reason: collision with root package name */
        TextView f5966c;

        /* renamed from: d  reason: collision with root package name */
        QRSlidingButton f5967d;
        View itemView;

        public c(View view) {
            super(view);
            this.itemView = view;
            this.f5964a = (ImageView) view.findViewById(R.id.icon);
            this.f5965b = (TextView) view.findViewById(R.id.title);
            this.f5966c = (TextView) view.findViewById(R.id.info);
            this.f5967d = (QRSlidingButton) view.findViewById(R.id.sliding_button);
        }
    }

    public e(Context context) {
        this.f5960b = context;
    }

    public void a(b bVar) {
        this.f5961c = bVar;
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull c cVar, int i) {
        c cVar2 = this.f5959a.get(i);
        cVar.f5965b.setText(x.j(this.f5960b, cVar2.f5953b));
        com.miui.optimizemanage.d.e.a(cVar.f5964a, cVar2.f5953b, cVar2.f5952a);
        cVar.f5967d.setTag(cVar2);
        cVar.f5967d.setChecked(cVar2.f5954c);
        cVar.itemView.setOnClickListener(new d(this, cVar, cVar2, i));
    }

    public void a(List<a> list) {
        this.f5959a.clear();
        for (int i = 0; i < list.size(); i++) {
            this.f5959a.addAll(list.get(i).f5963b);
        }
    }

    public int getItemCount() {
        List<c> list = this.f5959a;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @NonNull
    public c onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new c(LayoutInflater.from(this.f5960b).inflate(R.layout.om_list_item_lock_app, viewGroup, false));
    }
}
