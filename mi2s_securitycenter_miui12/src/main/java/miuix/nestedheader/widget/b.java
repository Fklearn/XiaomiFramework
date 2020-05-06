package miuix.nestedheader.widget;

import d.a.e.k;
import d.a.e.l;
import java.util.Collection;

class b extends k {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ NestedHeaderLayout f8870a;

    b(NestedHeaderLayout nestedHeaderLayout) {
        this.f8870a = nestedHeaderLayout;
    }

    public void onComplete(Object obj) {
        boolean unused = this.f8870a.P = false;
    }

    public void onUpdate(Object obj, Collection<l> collection) {
        l a2 = l.a(collection, "targe");
        if (a2 != null && !this.f8870a.Q) {
            this.f8870a.e(a2.b());
        }
    }
}
