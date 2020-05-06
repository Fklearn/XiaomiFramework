package androidx.recyclerview.widget;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.C0163d;
import androidx.recyclerview.widget.RecyclerView;

class G implements C0163d.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecyclerView f1085a;

    G(RecyclerView recyclerView) {
        this.f1085a = recyclerView;
    }

    public int a() {
        return this.f1085a.getChildCount();
    }

    public View a(int i) {
        return this.f1085a.getChildAt(i);
    }

    public void a(View view) {
        RecyclerView.u h = RecyclerView.h(view);
        if (h != null) {
            h.onEnteredHiddenState(this.f1085a);
        }
    }

    public void a(View view, int i) {
        this.f1085a.addView(view, i);
        this.f1085a.a(view);
    }

    public void a(View view, int i, ViewGroup.LayoutParams layoutParams) {
        RecyclerView.u h = RecyclerView.h(view);
        if (h != null) {
            if (h.isTmpDetached() || h.shouldIgnore()) {
                h.clearTmpDetachFlag();
            } else {
                throw new IllegalArgumentException("Called attach on a child which is not detached: " + h + this.f1085a.i());
            }
        }
        this.f1085a.attachViewToParent(view, i, layoutParams);
    }

    public RecyclerView.u b(View view) {
        return RecyclerView.h(view);
    }

    public void b() {
        int a2 = a();
        for (int i = 0; i < a2; i++) {
            View a3 = a(i);
            this.f1085a.b(a3);
            a3.clearAnimation();
        }
        this.f1085a.removeAllViews();
    }

    public void b(int i) {
        RecyclerView.u h;
        View a2 = a(i);
        if (!(a2 == null || (h = RecyclerView.h(a2)) == null)) {
            if (!h.isTmpDetached() || h.shouldIgnore()) {
                h.addFlags(256);
            } else {
                throw new IllegalArgumentException("called detach on an already detached child " + h + this.f1085a.i());
            }
        }
        this.f1085a.detachViewFromParent(i);
    }

    public int c(View view) {
        return this.f1085a.indexOfChild(view);
    }

    public void c(int i) {
        View childAt = this.f1085a.getChildAt(i);
        if (childAt != null) {
            this.f1085a.b(childAt);
            childAt.clearAnimation();
        }
        this.f1085a.removeViewAt(i);
    }

    public void d(View view) {
        RecyclerView.u h = RecyclerView.h(view);
        if (h != null) {
            h.onLeftHiddenState(this.f1085a);
        }
    }
}
