package com.miui.antivirus.model;

import java.util.ArrayList;
import java.util.Iterator;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private String f2769a;

    /* renamed from: b  reason: collision with root package name */
    private a f2770b;

    /* renamed from: c  reason: collision with root package name */
    private ArrayList<b> f2771c;

    public enum a {
        ENABLED,
        DISABLED
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public String f2775a;

        /* renamed from: b  reason: collision with root package name */
        public boolean f2776b;

        public b(String str, boolean z) {
            this.f2775a = str;
            this.f2776b = z;
        }
    }

    public String a() {
        return this.f2769a;
    }

    public void a(a aVar) {
        this.f2770b = aVar;
    }

    public void a(String str) {
        this.f2769a = str;
    }

    public void a(String str, boolean z) {
        Iterator<b> it = this.f2771c.iterator();
        while (it.hasNext()) {
            b next = it.next();
            if (next.f2775a.equals(str)) {
                next.f2776b = z;
                return;
            }
        }
    }

    public void a(ArrayList<b> arrayList) {
        this.f2771c = arrayList;
    }

    public a b() {
        return this.f2770b;
    }

    public ArrayList<b> c() {
        return this.f2771c;
    }
}
