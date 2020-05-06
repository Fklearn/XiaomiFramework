package miuix.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import d.a.a.a;
import d.a.b;
import d.a.g.B;
import d.a.h;
import miuix.recyclerview.widget.c;

public class j extends c {
    private static View.OnAttachStateChangeListener v = new d();
    private static a w;

    static {
        a aVar = new a();
        aVar.a(0.0f);
        w = aVar;
    }

    /* access modifiers changed from: package-private */
    public void B(RecyclerView.u uVar) {
        C(uVar);
        uVar.itemView.setAlpha(0.0f);
    }

    /* access modifiers changed from: package-private */
    public void C(RecyclerView.u uVar) {
        if (uVar != null) {
            b.a(uVar.itemView).state().a(B.f8754a, B.f8755b, B.n);
            c.a(uVar.itemView);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(c.a aVar) {
        c.a aVar2 = aVar;
        RecyclerView.u uVar = aVar2.f8921a;
        View view = null;
        View view2 = uVar == null ? null : uVar.itemView;
        RecyclerView.u uVar2 = aVar2.f8922b;
        if (uVar2 != null) {
            view = uVar2.itemView;
        }
        if (view2 != null) {
            f(uVar, true);
            view2.addOnAttachStateChangeListener(v);
            b.a(view2).state().to(B.f8754a, Integer.valueOf(aVar2.e - aVar2.f8923c), B.f8755b, Integer.valueOf(aVar2.f - aVar2.f8924d), B.n, 0, w);
            view2.postDelayed(new h(this, uVar), b.a(view2).state().b(B.f8754a, Integer.valueOf(aVar2.e - aVar2.f8923c), B.f8755b, Integer.valueOf(aVar2.f - aVar2.f8924d), B.n, 0));
        }
        if (view != null) {
            f(uVar2, false);
            b.a(view).state().to(B.f8754a, 0, B.f8755b, 0, B.n, Float.valueOf(1.0f), w);
            view.postDelayed(new i(this, uVar2), b.a(view).state().b(B.f8754a, 0, B.f8755b, 0, B.n, Float.valueOf(1.0f)));
        }
    }

    /* access modifiers changed from: package-private */
    public void a(c.b bVar) {
        y(bVar.f8925a);
        RecyclerView.u uVar = bVar.f8925a;
        b.a(uVar.itemView).state().to(B.f8754a, 0, B.f8755b, 0, w);
        bVar.f8925a.itemView.postDelayed(new f(this, uVar), b.a(bVar.f8925a.itemView).state().b(B.f8754a, 0, B.f8755b, 0));
    }

    /* access modifiers changed from: package-private */
    public void b(c.a aVar) {
        float translationX = aVar.f8921a.itemView.getTranslationX();
        float translationY = aVar.f8921a.itemView.getTranslationY();
        float alpha = aVar.f8921a.itemView.getAlpha();
        C(aVar.f8921a);
        int i = (int) (((float) (aVar.e - aVar.f8923c)) - translationX);
        int i2 = (int) (((float) (aVar.f - aVar.f8924d)) - translationY);
        aVar.f8921a.itemView.setTranslationX(translationX);
        aVar.f8921a.itemView.setTranslationY(translationY);
        aVar.f8921a.itemView.setAlpha(alpha);
        RecyclerView.u uVar = aVar.f8922b;
        if (uVar != null) {
            C(uVar);
            aVar.f8922b.itemView.setTranslationX((float) (-i));
            aVar.f8922b.itemView.setTranslationY((float) (-i2));
            aVar.f8922b.itemView.setAlpha(0.0f);
        }
    }

    /* access modifiers changed from: package-private */
    public void b(c.b bVar) {
        bVar.f8925a.itemView.setTranslationX((float) (bVar.f8926b - bVar.f8928d));
        bVar.f8925a.itemView.setTranslationY((float) (bVar.f8927c - bVar.e));
    }

    /* access modifiers changed from: package-private */
    public void t(RecyclerView.u uVar) {
        w(uVar);
        h state = b.a(uVar.itemView).state();
        Float valueOf = Float.valueOf(1.0f);
        state.to(B.n, valueOf, w);
        uVar.itemView.postDelayed(new g(this, uVar), b.a(uVar.itemView).state().b(B.n, valueOf));
    }

    /* access modifiers changed from: package-private */
    public void u(RecyclerView.u uVar) {
        A(uVar);
        uVar.itemView.addOnAttachStateChangeListener(v);
        h state = b.a(uVar.itemView).state();
        Float valueOf = Float.valueOf(0.0f);
        state.to(B.n, valueOf, w);
        uVar.itemView.postDelayed(new e(this, uVar), b.a(uVar.itemView).state().b(B.n, valueOf));
    }
}
