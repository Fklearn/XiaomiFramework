package com.miui.gamebooster.customview.b;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class f<Item> extends RecyclerView.a<g> {

    /* renamed from: a  reason: collision with root package name */
    protected final Context f4182a;

    /* renamed from: b  reason: collision with root package name */
    private final List<Item> f4183b;

    /* renamed from: c  reason: collision with root package name */
    protected final e f4184c;

    /* renamed from: d  reason: collision with root package name */
    protected a f4185d;

    public interface a {
        boolean a(View view, g gVar, int i);

        void b(View view, g gVar, int i);
    }

    public f(Context context) {
        this(context, Collections.EMPTY_LIST);
    }

    public f(Context context, List<Item> list) {
        this.f4183b = new LinkedList();
        this.f4184c = new e();
        this.f4182a = context;
        this.f4183b.addAll(list);
    }

    private void a(ViewGroup viewGroup, g gVar, int i) {
        if (a(gVar, i)) {
            gVar.a().setOnClickListener(new b(this, gVar));
            gVar.a().setOnLongClickListener(new a(this, gVar));
        }
    }

    private boolean d() {
        return this.f4184c.a() > 0;
    }

    public final f a(int i, d<Item> dVar) {
        this.f4184c.a(i, dVar);
        return this;
    }

    public final f a(d<Item> dVar) {
        this.f4184c.a(dVar);
        return this;
    }

    public final Item a(int i) {
        return this.f4183b.get(i);
    }

    public final void a(a aVar) {
        this.f4185d = aVar;
    }

    /* renamed from: a */
    public void onBindViewHolder(g gVar, int i, List<Object> list) {
        if (list.isEmpty()) {
            onBindViewHolder(gVar, i);
        } else {
            a(gVar, this.f4183b.get(i));
        }
    }

    public /* synthetic */ void a(g gVar, View view) {
        int adapterPosition;
        if (this.f4185d != null && (adapterPosition = gVar.getAdapterPosition()) >= 0 && adapterPosition < getItemCount()) {
            this.f4185d.b(view, gVar, adapterPosition);
        }
    }

    /* access modifiers changed from: protected */
    public void a(g gVar, Item item) {
        this.f4184c.a(gVar, item, gVar.getAdapterPosition());
    }

    public final void a(Item item) {
        this.f4183b.add(item);
    }

    public final void a(@NonNull List<Item> list) {
        this.f4183b.addAll(list);
    }

    /* renamed from: a */
    public boolean onFailedToRecycleView(@NonNull g gVar) {
        return super.onFailedToRecycleView(gVar);
    }

    /* access modifiers changed from: protected */
    public boolean a(g gVar, int i) {
        return this.f4184c.a(i).a();
    }

    public final void b() {
        this.f4183b.clear();
    }

    /* renamed from: b */
    public void onViewAttachedToWindow(@NonNull g gVar) {
        super.onViewAttachedToWindow(gVar);
    }

    /* renamed from: b */
    public final void onBindViewHolder(g gVar, int i) {
        a(gVar, this.f4183b.get(i));
    }

    public /* synthetic */ boolean b(g gVar, View view) {
        int adapterPosition;
        if (this.f4185d == null || (adapterPosition = gVar.getAdapterPosition()) < 0 || adapterPosition >= getItemCount()) {
            return false;
        }
        boolean a2 = this.f4185d.a(view, gVar, adapterPosition);
        if (a2) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
        }
        return a2;
    }

    public final List<Item> c() {
        return this.f4183b;
    }

    /* renamed from: c */
    public void onViewDetachedFromWindow(@NonNull g gVar) {
        super.onViewDetachedFromWindow(gVar);
    }

    /* access modifiers changed from: protected */
    public void c(g gVar, View view) {
    }

    /* renamed from: d */
    public void onViewRecycled(@NonNull g gVar) {
        super.onViewRecycled(gVar);
    }

    public final int getItemCount() {
        return this.f4183b.size();
    }

    public final int getItemViewType(int i) {
        return !d() ? super.getItemViewType(i) : this.f4184c.a(this.f4183b.get(i), i);
    }

    public final g onCreateViewHolder(ViewGroup viewGroup, int i) {
        d a2 = this.f4184c.a(i);
        int b2 = a2.b();
        g a3 = b2 == 0 ? g.a(this.f4182a, a2.c()) : g.a(this.f4182a, viewGroup, b2, false);
        c(a3, a3.a());
        a(viewGroup, a3, i);
        return a3;
    }
}
