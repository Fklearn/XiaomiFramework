package com.miui.appmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.appmanager.c.c;
import com.miui.appmanager.c.i;
import com.miui.appmanager.c.j;
import com.miui.appmanager.c.k;
import com.miui.appmanager.c.l;
import com.miui.appmanager.c.n;
import com.miui.appmanager.c.o;
import com.miui.appmanager.c.p;
import com.miui.appmanager.c.q;
import com.miui.appmanager.c.r;
import java.util.ArrayList;

/* renamed from: com.miui.appmanager.b  reason: case insensitive filesystem */
public class C0319b extends RecyclerView.a<l> {

    /* renamed from: a  reason: collision with root package name */
    private Context f3581a;

    /* renamed from: b  reason: collision with root package name */
    private ArrayList<k> f3582b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f3583c;

    /* renamed from: com.miui.appmanager.b$a */
    public interface a {
        void onItemClick(int i);
    }

    public C0319b(Context context) {
        this(context, new ArrayList());
    }

    public C0319b(Context context, ArrayList<k> arrayList) {
        this.f3581a = context;
        this.f3582b = arrayList;
    }

    private l a(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(this.f3581a).inflate(k.a(i), viewGroup, false);
        switch (i) {
            case 0:
                return new i.a(inflate);
            case 1:
                return new o.a(inflate);
            case 2:
                return new j.a(inflate);
            case 3:
                return new p.a(inflate);
            case 4:
                return new c.a(inflate);
            case 5:
                return new r.a(inflate);
            case 6:
                return new q.a(inflate);
            case 12:
                return new n.a(inflate);
            default:
                return new l(inflate);
        }
    }

    public k a(int i) {
        return this.f3582b.get(i);
    }

    public void a(a aVar) {
        this.f3583c = aVar;
    }

    public void a(k kVar) {
        this.f3582b.add(kVar);
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull l lVar, int i) {
        lVar.a(lVar.a(), this.f3582b.get(i), i);
        lVar.a().setOnClickListener(new C0318a(this, i));
    }

    public void a(ArrayList<k> arrayList) {
        this.f3582b.addAll(arrayList);
    }

    public void b() {
        this.f3582b.clear();
    }

    public void b(ArrayList<k> arrayList) {
        this.f3582b.clear();
        this.f3582b.addAll(arrayList);
        notifyDataSetChanged();
    }

    public ArrayList<k> c() {
        return this.f3582b;
    }

    public int getItemCount() {
        return this.f3582b.size();
    }

    public int getItemViewType(int i) {
        return this.f3582b.get(i).a();
    }

    @NonNull
    public l onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return a(viewGroup, i);
    }
}
