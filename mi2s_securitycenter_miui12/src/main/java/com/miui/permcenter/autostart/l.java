package com.miui.permcenter.autostart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.r;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.widget.SlidingButton;

public class l extends RecyclerView.a<b> {

    /* renamed from: a  reason: collision with root package name */
    private Context f6086a;

    /* renamed from: b  reason: collision with root package name */
    private List<com.miui.permcenter.a> f6087b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private long f6088c;

    /* renamed from: d  reason: collision with root package name */
    private CompoundButton.OnCheckedChangeListener f6089d;
    /* access modifiers changed from: private */
    public a e;

    public interface a {
        void a(int i, View view, com.miui.permcenter.a aVar);
    }

    public static class b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ImageView f6090a;

        /* renamed from: b  reason: collision with root package name */
        TextView f6091b;

        /* renamed from: c  reason: collision with root package name */
        TextView f6092c;

        /* renamed from: d  reason: collision with root package name */
        SlidingButton f6093d;

        public b(@NonNull View view, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            super(view);
            this.f6090a = (ImageView) view.findViewById(R.id.icon);
            this.f6091b = (TextView) view.findViewById(R.id.title);
            this.f6092c = (TextView) view.findViewById(R.id.procIsRunning);
            this.f6093d = view.findViewById(R.id.sliding_button);
            this.f6093d.setOnPerformCheckedChangeListener(onCheckedChangeListener);
        }
    }

    public l(Context context, long j) {
        this.f6086a = context;
        this.f6088c = j;
    }

    public void a(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.f6089d = onCheckedChangeListener;
    }

    public void a(a aVar) {
        this.e = aVar;
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull b bVar, int i) {
        com.miui.permcenter.a aVar = this.f6087b.get(i);
        if (this.e != null) {
            bVar.itemView.setOnClickListener(new k(this, i, aVar));
        }
        boolean z = true;
        bVar.itemView.setClickable(true);
        r.a("pkg_icon://".concat(aVar.e()), bVar.f6090a, r.f);
        bVar.f6091b.setText(aVar.d());
        bVar.f6092c.setVisibility(aVar.c() ? 0 : 8);
        bVar.f6093d.setTag(aVar);
        SlidingButton slidingButton = bVar.f6093d;
        if (aVar.f().get(Long.valueOf(this.f6088c)).intValue() != 3 && !aVar.b()) {
            z = false;
        }
        slidingButton.setChecked(z);
        bVar.itemView.setTag(bVar);
    }

    public void a(List<i> list) {
        this.f6087b.clear();
        for (i c2 : list) {
            this.f6087b.addAll(c2.c());
        }
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.f6087b.size();
    }

    @NonNull
    public b onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new b(LayoutInflater.from(this.f6086a).inflate(R.layout.pm_auto_start_list_item_view, viewGroup, false), this.f6089d);
    }
}
