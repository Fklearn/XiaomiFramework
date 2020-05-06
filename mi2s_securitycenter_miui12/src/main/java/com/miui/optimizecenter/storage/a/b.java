package com.miui.optimizecenter.storage.a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.r;
import b.b.i.b.g;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.text.ExtraTextUtils;

public class b extends RecyclerView.a<C0056b> implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private List<c> f5698a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private a f5699b;

    public interface a {
        void a(c cVar);
    }

    /* renamed from: com.miui.optimizecenter.storage.a.b$b  reason: collision with other inner class name */
    public static class C0056b extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        public TextView f5700a;

        /* renamed from: b  reason: collision with root package name */
        public TextView f5701b;

        /* renamed from: c  reason: collision with root package name */
        public ImageView f5702c;

        /* renamed from: d  reason: collision with root package name */
        public TextView f5703d;
        public TextView e;
        public ImageView f;
        public ProgressBar g;
        public View h;

        public C0056b(@NonNull View view) {
            super(view);
            this.f5700a = (TextView) view.findViewById(R.id.title);
            this.f5701b = (TextView) view.findViewById(R.id.summary);
            this.f5702c = (ImageView) view.findViewById(R.id.app_icon);
            this.f5703d = (TextView) view.findViewById(R.id.app_version);
            this.e = (TextView) view.findViewById(R.id.app_name);
            this.f = (ImageView) view.findViewById(R.id.arrow);
            this.g = (ProgressBar) view.findViewById(R.id.progress);
            this.h = view.findViewById(R.id.status_panel);
        }
    }

    public b(List<c> list) {
        this.f5698a = list;
    }

    public void a(a aVar) {
        this.f5699b = aVar;
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull C0056b bVar, int i) {
        c cVar = this.f5698a.get(i);
        Application d2 = Application.d();
        switch (a.f5697a[cVar.b().ordinal()]) {
            case 2:
                r.a(cVar.a(), bVar.f5702c, r.f);
                g.a(bVar.e, cVar.e());
                g.a(bVar.f5703d, cVar.d());
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                g.a(bVar.f5700a, cVar.f());
                g.a(bVar.f5701b, ExtraTextUtils.formatFileSize(d2, cVar.c()));
                g.a(bVar.h, 8);
                break;
            case 7:
            case 8:
            case 9:
            case 10:
                g.a(bVar.f5700a, cVar.f());
                break;
            case 11:
                g.a(bVar.f5700a, cVar.e());
                break;
        }
        g.a(bVar.h, 0);
        g.a((View) bVar.g, 8);
        g.a((View) bVar.f, 0);
        switch (a.f5697a[cVar.b().ordinal()]) {
            case 7:
            case 10:
            case 11:
                g.a(bVar.itemView, (View.OnClickListener) this);
                break;
            case 8:
            case 9:
                if (cVar.c() < 0) {
                    g.b(bVar.itemView);
                    g.a(bVar.itemView, false);
                    break;
                } else {
                    g.a(bVar.itemView, (View.OnClickListener) this);
                    g.a(bVar.itemView, true);
                    break;
                }
            default:
                g.b(bVar.itemView);
                break;
        }
        bVar.itemView.setTag(cVar);
    }

    public int getItemCount() {
        return this.f5698a.size();
    }

    public int getItemViewType(int i) {
        return this.f5698a.get(i).b().b();
    }

    public void onClick(View view) {
        a aVar = this.f5699b;
        if (aVar != null) {
            aVar.a((c) view.getTag());
        }
    }

    @NonNull
    public C0056b onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        int i2;
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 0) {
            i2 = R.layout.storage_app_detail_list_header;
        } else if (i == 1) {
            i2 = R.layout.storage_app_detail_list_item;
        } else if (i != 2) {
            view = null;
            return new C0056b(view);
        } else {
            i2 = R.layout.storage_app_detail_list_item_line;
        }
        view = from.inflate(i2, viewGroup, false);
        return new C0056b(view);
    }
}
