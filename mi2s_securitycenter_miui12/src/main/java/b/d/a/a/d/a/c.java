package b.d.a.a.d.a;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private int f2117a = 0;

    /* renamed from: b  reason: collision with root package name */
    private b f2118b;

    /* renamed from: c  reason: collision with root package name */
    private int f2119c;

    /* renamed from: d  reason: collision with root package name */
    private int f2120d;

    public void a(int i) {
        this.f2119c = i;
    }

    public void a(b bVar) {
        this.f2118b = bVar;
    }

    public boolean a() {
        return (this.f2117a & 1) > 0;
    }

    public void b(int i) {
        this.f2120d = i;
    }

    public boolean b() {
        return (this.f2117a & 16) > 0;
    }

    public void c() {
        this.f2117a |= 1;
    }

    public void d() {
        this.f2117a |= 16;
    }

    public void e() {
        this.f2117a = 0;
    }
}
