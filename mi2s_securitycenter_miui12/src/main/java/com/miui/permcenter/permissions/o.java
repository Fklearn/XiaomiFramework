package com.miui.permcenter.permissions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.r;
import b.b.c.j.x;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class o extends RecyclerView.a<b> {

    /* renamed from: a  reason: collision with root package name */
    private Context f6279a;

    /* renamed from: b  reason: collision with root package name */
    private List<com.miui.permcenter.a> f6280b = new ArrayList();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f6281c;

    public interface a {
        void a(int i, View view, com.miui.permcenter.a aVar);
    }

    public static class b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ImageView f6282a;

        /* renamed from: b  reason: collision with root package name */
        TextView f6283b;

        /* renamed from: c  reason: collision with root package name */
        TextView f6284c;

        public b(@NonNull View view) {
            super(view);
            this.f6282a = (ImageView) view.findViewById(R.id.icon);
            this.f6283b = (TextView) view.findViewById(R.id.title);
            this.f6284c = (TextView) view.findViewById(R.id.summary);
        }
    }

    public o(Context context) {
        this.f6279a = context;
    }

    public void a(a aVar) {
        this.f6281c = aVar;
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull b bVar, int i) {
        TextView textView;
        String str;
        com.miui.permcenter.a aVar = this.f6280b.get(i);
        r.a("pkg_icon://".concat(aVar.e()), bVar.f6282a, r.f);
        bVar.f6283b.setText(x.j(this.f6279a, aVar.e()));
        bVar.itemView.setOnClickListener(new n(this, i, aVar));
        if (aVar.e().equals(HybirdServiceUtil.HYBIRD_PACKAGE_NAME)) {
            textView = bVar.f6284c;
            str = this.f6279a.getString(R.string.manage_hybrid_permissions);
        } else if (aVar.h() || aVar.g()) {
            textView = bVar.f6284c;
            str = "";
        } else {
            int a2 = aVar.a();
            textView = bVar.f6284c;
            str = this.f6279a.getResources().getQuantityString(R.plurals.hints_apps_perm_count, a2, new Object[]{Integer.valueOf(a2)});
        }
        textView.setText(str);
    }

    public void a(List<com.miui.permcenter.a> list) {
        this.f6280b.clear();
        this.f6280b.addAll(list);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.f6280b.size();
    }

    @NonNull
    public b onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new b(LayoutInflater.from(this.f6279a).inflate(R.layout.pm_apps_list_item_view, viewGroup, false));
    }
}
