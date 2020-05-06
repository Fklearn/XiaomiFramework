package b.b.a.d.a;

import android.text.TextUtils;
import android.util.Pair;
import b.b.a.d.a.l;
import b.b.a.e.i;

class m implements i.d {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l.a f1366a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ o f1367b;

    m(o oVar, l.a aVar) {
        this.f1367b = oVar;
        this.f1366a = aVar;
    }

    public void a(String str, Pair<String, String> pair) {
        if (pair != null && !TextUtils.isEmpty((CharSequence) pair.first) && str.equals(this.f1366a.f1362a.getTag())) {
            this.f1366a.f1362a.setText((CharSequence) pair.first);
        }
    }
}
