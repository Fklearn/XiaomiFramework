package b.d.a.a.a;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static Integer f2088a;

    /* renamed from: b  reason: collision with root package name */
    private static final Map<a, Boolean> f2089b = new b();

    /* renamed from: c  reason: collision with root package name */
    public static String f2090c = null;

    /* renamed from: d  reason: collision with root package name */
    private static Map<a, b.d.a.a.a.a.a> f2091d;
    /* access modifiers changed from: private */
    public static Map<a, b.d.a.a.a.a.b> e;
    /* access modifiers changed from: private */
    public static Map<a, Double> f;
    private static volatile boolean g = false;

    static class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        a f2092a;

        /* renamed from: b  reason: collision with root package name */
        double f2093b;

        /* renamed from: c  reason: collision with root package name */
        Map<a, b.d.a.a.a.a.b> f2094c;

        public a(a aVar, double d2, Map<a, b.d.a.a.a.a.b> map) {
            this.f2092a = aVar;
            this.f2093b = d2;
            this.f2094c = map;
        }

        public void run() {
            try {
                c.e.put(this.f2092a, b.d.a.a.a.a.a.c.a(this.f2092a));
                c.f.put(this.f2092a, Double.valueOf(this.f2093b));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        a f2095a;

        /* renamed from: b  reason: collision with root package name */
        Map<a, b.d.a.a.a.a.a> f2096b;

        public b(a aVar, Map<a, b.d.a.a.a.a.a> map) {
            this.f2095a = aVar;
            this.f2096b = map;
        }

        public void run() {
            try {
                this.f2096b.put(this.f2095a, b.d.a.a.a.a.a.a.a(this.f2095a));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Boolean a(String str, String str2) {
        if (g) {
            Boolean bool = f2089b.get(b(str, str2));
            return bool == null ? Boolean.FALSE : bool;
        }
        throw new RuntimeException("please call method init and wait initialize success!");
    }

    public static synchronized boolean a(String str) {
        synchronized (c.class) {
            if (g) {
                return true;
            }
            if (!str.endsWith("/")) {
                str = str + "/";
            }
            f2090c = str;
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f2090c + "version")));
                f2088a = Integer.valueOf(Integer.parseInt(bufferedReader.readLine()));
                bufferedReader.close();
                f2091d = new HashMap(1);
                e = new ConcurrentHashMap(0, 0.9f);
                f = new ConcurrentHashMap(0, 0.9f);
                new b(a.OTHER, f2091d).run();
                new a(a.AIRPLANE, 0.999999d, e).run();
                new a(a.TRAIN, 0.999999999d, e).run();
                new a(a.HOTEL, 0.99999999d, e).run();
                new a(a.BUS, 0.99999d, e).run();
                new a(a.BANK_BILL, 0.99999999d, e).run();
                new a(a.LOAN_CREDIT_BILL, 0.9999999d, e).run();
                new a(a.TELECOM_OPERATOR, 0.99999d, e).run();
                new a(a.SHUI_DIAN_MEI, 0.999999d, e).run();
                new a(a.EXPRESS, 0.999999d, e).run();
                b.d.a.a.c.b.b();
                g = true;
                return true;
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            } catch (Exception e3) {
                throw new RuntimeException(e3);
            }
        }
    }

    public static a b(String str, String str2) {
        if (g) {
            String replaceAll = str2.replaceAll("( )+", " ");
            a a2 = b.d.a.a.c.b.a(str, replaceAll);
            if (!a.UNKNOWN.equals(a2)) {
                return a2;
            }
            if (!b.d.a.a.b.a.a.f2098a.matcher(replaceAll).find()) {
                return a.OTHER;
            }
            b.d.a.a.b.c a3 = f2091d.get(a.OTHER).a(replaceAll);
            for (a aVar : a.values()) {
                if (!aVar.equals(a.UNKNOWN) && !aVar.equals(a.OTHER) && e.get(aVar).a(a3)[1] > f.get(aVar).doubleValue()) {
                    return aVar;
                }
            }
            return a.OTHER;
        }
        throw new RuntimeException("please call method init and wait initialize success!");
    }

    public static boolean c() {
        e = null;
        f2091d = null;
        f = null;
        b.d.a.a.a.a.a.a.a();
        b.d.a.a.a.a.a.c.a();
        b.d.a.a.c.b.a();
        g = false;
        return true;
    }
}
