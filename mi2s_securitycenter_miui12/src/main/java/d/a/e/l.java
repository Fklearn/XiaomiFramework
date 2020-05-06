package d.a.e;

import d.a.g.C0575b;
import d.a.h.c;
import java.util.Collection;

public class l {

    /* renamed from: a  reason: collision with root package name */
    public C0575b f8730a;

    /* renamed from: b  reason: collision with root package name */
    public float f8731b;

    /* renamed from: c  reason: collision with root package name */
    public boolean f8732c;

    /* renamed from: d  reason: collision with root package name */
    public boolean f8733d;
    public boolean e;
    public c f;
    private Number g;

    public static l a(Collection<l> collection, String str) {
        for (l next : collection) {
            if (next.f8730a.getName().equals(str)) {
                return next;
            }
        }
        return null;
    }

    public float a() {
        return this.g.floatValue();
    }

    public <T extends Number> void a(T t) {
        this.g = t;
    }

    public int b() {
        return this.g.intValue();
    }

    public void c() {
        this.e = false;
        this.f8732c = false;
        this.f8733d = false;
    }

    public String toString() {
        return "UpdateInfo{property=" + this.f8730a + ", mValue=" + this.g + ", velocity=" + this.f8731b + ", isCompleted=" + this.f8732c + '}';
    }
}
