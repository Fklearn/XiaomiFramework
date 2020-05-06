package b.b.a.d.a;

import android.text.TextUtils;
import android.util.Pair;
import b.b.a.d.a.l;
import b.b.a.e.i;

class f implements i.d {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l.a f1345a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ h f1346b;

    f(h hVar, l.a aVar) {
        this.f1346b = hVar;
        this.f1345a = aVar;
    }

    public void a(String str, Pair<String, String> pair) {
        if (pair != null && !TextUtils.isEmpty((CharSequence) pair.first) && str.equals(this.f1345a.f1362a.getTag())) {
            this.f1345a.f1362a.setText((CharSequence) pair.first);
        }
    }
}
