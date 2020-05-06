package com.miui.privacyapps.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.r;
import b.b.k.c;
import b.b.k.d;
import com.miui.gamebooster.view.QRSlidingButton;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class p extends RecyclerView.a<b> {

    /* renamed from: a  reason: collision with root package name */
    private Context f7417a;

    /* renamed from: b  reason: collision with root package name */
    private List<c> f7418b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private ArrayList<d> f7419c = new ArrayList<>();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public a f7420d;

    public interface a {
        void a(int i, c cVar);
    }

    public static class b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        ImageView f7421a;

        /* renamed from: b  reason: collision with root package name */
        TextView f7422b;

        /* renamed from: c  reason: collision with root package name */
        TextView f7423c;

        /* renamed from: d  reason: collision with root package name */
        QRSlidingButton f7424d;

        public b(View view) {
            super(view);
            this.f7421a = (ImageView) view.findViewById(R.id.icon);
            this.f7422b = (TextView) view.findViewById(R.id.title);
            this.f7423c = (TextView) view.findViewById(R.id.procIsRunning);
            this.f7424d = (QRSlidingButton) view.findViewById(R.id.sliding_button);
        }
    }

    public p(Context context) {
        this.f7417a = context;
    }

    public void a(a aVar) {
        this.f7420d = aVar;
    }

    /* renamed from: a */
    public void onBindViewHolder(b bVar, int i) {
        String str;
        String str2;
        c cVar = this.f7418b.get(i);
        if (cVar.e() == 999) {
            str2 = cVar.c();
            str = "pkg_icon_xspace://";
        } else {
            str2 = cVar.c();
            str = "pkg_icon://";
        }
        r.a(str.concat(str2), bVar.f7421a, r.f);
        bVar.f7422b.setText(cVar.b());
        bVar.f7423c.setVisibility(8);
        bVar.f7424d.setTag(cVar);
        bVar.f7424d.setChecked(cVar.a());
        bVar.itemView.setOnClickListener(new o(this, i, cVar));
    }

    public void a(ArrayList<d> arrayList) {
        this.f7419c.clear();
        this.f7419c.addAll(arrayList);
        this.f7418b.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            this.f7418b.addAll(arrayList.get(i).c());
        }
        notifyDataSetChanged();
    }

    public int getItemCount() {
        List<c> list = this.f7418b;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public b onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new b(LayoutInflater.from(this.f7417a).inflate(R.layout.pa_list_item_view, viewGroup, false));
    }
}
