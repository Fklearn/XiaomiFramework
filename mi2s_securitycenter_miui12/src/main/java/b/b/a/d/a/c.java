package b.b.a.d.a;

import android.text.TextUtils;
import android.util.Pair;
import b.b.a.d.a.e;
import b.b.a.e.i;

class c implements i.d {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e.b f1332a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e f1333b;

    c(e eVar, e.b bVar) {
        this.f1333b = eVar;
        this.f1332a = bVar;
    }

    public void a(String str, Pair<String, String> pair) {
        if (pair != null && str.equals(this.f1332a.f1341a.getTag())) {
            if (!TextUtils.isEmpty((CharSequence) pair.first)) {
                this.f1332a.f1341a.setText((CharSequence) pair.first);
                this.f1332a.f1344d.setVisibility(0);
                this.f1332a.f1344d.setText(str);
            }
            if (!TextUtils.isEmpty((CharSequence) pair.second)) {
                this.f1332a.f1342b.setText((CharSequence) pair.second);
            }
        }
    }
}
