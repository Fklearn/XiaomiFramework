package com.miui.optimizecenter.storage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.optimizecenter.storage.model.b;
import com.miui.securitycenter.R;
import java.util.List;

public class h extends RecyclerView.a<a> implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private List<b> f5739a;

    public class a extends RecyclerView.u {

        /* renamed from: a  reason: collision with root package name */
        int f5740a;

        /* renamed from: b  reason: collision with root package name */
        public ImageView f5741b;

        /* renamed from: c  reason: collision with root package name */
        public TextView f5742c;

        /* renamed from: d  reason: collision with root package name */
        public TextView f5743d;
        public ImageView e;

        public a(@NonNull View view) {
            super(view);
            this.f5741b = (ImageView) view.findViewById(R.id.app_icon);
            this.f5742c = (TextView) view.findViewById(R.id.app_name);
            this.f5743d = (TextView) view.findViewById(R.id.app_desc);
            this.e = (ImageView) view.findViewById(R.id.arrow);
            view.setTag(this);
        }
    }

    public h(List<b> list) {
        this.f5739a = list;
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull a aVar, int i) {
        aVar.f5740a = i;
        b bVar = this.f5739a.get(i);
        if (bVar != null) {
            bVar.a(aVar.itemView);
        }
        aVar.itemView.setOnClickListener(this);
    }

    public int getItemCount() {
        List<b> list = this.f5739a;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void onClick(View view) {
        if (view.getTag() instanceof a) {
            this.f5739a.get(((a) view.getTag()).f5740a).b(view);
        }
    }

    @NonNull
    public a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new a(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.storage_app_list_item, viewGroup, false));
    }
}
