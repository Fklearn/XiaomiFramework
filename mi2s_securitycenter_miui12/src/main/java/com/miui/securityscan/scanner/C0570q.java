package com.miui.securityscan.scanner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* renamed from: com.miui.securityscan.scanner.q  reason: case insensitive filesystem */
public class C0570q {

    /* renamed from: a  reason: collision with root package name */
    private static C0570q f7916a;

    /* renamed from: b  reason: collision with root package name */
    private Map<a, Map<String, C0569p>> f7917b = new ConcurrentHashMap();

    /* renamed from: com.miui.securityscan.scanner.q$a */
    public enum a {
        SYSTEM,
        CLEANUP,
        SECURITY
    }

    private C0570q() {
        for (a put : a.values()) {
            this.f7917b.put(put, new ConcurrentHashMap());
        }
    }

    public static synchronized C0570q b() {
        C0570q qVar;
        synchronized (C0570q.class) {
            if (f7916a == null) {
                f7916a = new C0570q();
            }
            qVar = f7916a;
        }
        return qVar;
    }

    public Map<String, C0569p> a(a aVar) {
        return this.f7917b.get(aVar);
    }

    public void a() {
        for (a aVar : a.values()) {
            this.f7917b.get(aVar).clear();
        }
    }

    public void a(a aVar, String str, C0569p pVar) {
        this.f7917b.get(aVar).put(str, pVar);
    }
}
