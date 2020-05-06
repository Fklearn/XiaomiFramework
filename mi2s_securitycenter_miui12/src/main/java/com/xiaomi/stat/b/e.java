package com.xiaomi.stat.b;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class e {

    private static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public static final ExecutorService f8442a = Executors.newCachedThreadPool();

        private a() {
        }
    }

    private e() {
    }

    public static ExecutorService a() {
        return a.f8442a;
    }
}
