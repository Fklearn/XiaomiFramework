package androidx.preference;

import androidx.preference.z;
import androidx.recyclerview.widget.C0174o;
import java.util.List;

class v extends C0174o.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f1058a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ List f1059b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ z.d f1060c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ x f1061d;

    v(x xVar, List list, List list2, z.d dVar) {
        this.f1061d = xVar;
        this.f1058a = list;
        this.f1059b = list2;
        this.f1060c = dVar;
    }

    public int a() {
        return this.f1059b.size();
    }

    public boolean a(int i, int i2) {
        return this.f1060c.a((Preference) this.f1058a.get(i), (Preference) this.f1059b.get(i2));
    }

    public int b() {
        return this.f1058a.size();
    }

    public boolean b(int i, int i2) {
        return this.f1060c.b((Preference) this.f1058a.get(i), (Preference) this.f1059b.get(i2));
    }
}
