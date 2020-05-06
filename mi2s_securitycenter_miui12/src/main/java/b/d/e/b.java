package b.d.e;

import java.util.List;
import java.util.Set;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private Set<String> f2168a;

    /* renamed from: b  reason: collision with root package name */
    private List<String> f2169b;

    /* renamed from: c  reason: collision with root package name */
    private List<String> f2170c;

    /* renamed from: d  reason: collision with root package name */
    private String f2171d;
    private boolean e;
    private int[] f;
    public List<String> g;
    private String h;
    private int i;
    private boolean j = false;
    private boolean k = false;
    private boolean l = false;
    private boolean m = false;
    public List<a> n;
    private int o = 0;
    private int p = 0;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public int f2172a;

        /* renamed from: b  reason: collision with root package name */
        public String f2173b;

        public a(String str, int i) {
            this.f2173b = str;
            this.f2172a = i;
        }
    }

    public b(String str, String str2) {
        this.h = a(str);
        this.i = b.d.e.c.a.a(this.h);
        this.f2171d = h.a(str2);
    }

    private String a(String str) {
        return (str.length() <= 3 || str.indexOf("+86") != 0) ? str : str.substring(3);
    }

    public String a() {
        return this.h;
    }

    public void a(int i2) {
        this.o = i2 | this.o;
    }

    public void a(List<String> list) {
        this.f2170c = list;
    }

    public void a(Set<String> set) {
        this.f2168a = set;
    }

    public void a(int[] iArr) {
        this.f = iArr;
    }

    public int b() {
        return this.i;
    }

    public void b(int i2) {
        this.p = i2;
    }

    public void b(List<String> list) {
        this.f2169b = list;
    }

    public String c() {
        return this.f2171d;
    }

    public List<String> d() {
        return this.f2170c;
    }

    public int[] e() {
        return this.f;
    }

    public int f() {
        return this.p;
    }

    public Set<String> g() {
        return this.f2168a;
    }

    public List<String> h() {
        return this.f2169b;
    }

    public int i() {
        return this.o;
    }

    public String toString() {
        return "[" + this.e + "][" + this.h + "]:" + this.f2171d;
    }
}
