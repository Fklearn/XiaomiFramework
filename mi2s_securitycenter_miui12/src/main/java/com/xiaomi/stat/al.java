package com.xiaomi.stat;

import java.lang.Thread;

public class al implements Thread.UncaughtExceptionHandler {

    /* renamed from: a  reason: collision with root package name */
    private e f8425a;

    /* renamed from: b  reason: collision with root package name */
    private Thread.UncaughtExceptionHandler f8426b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f8427c = true;

    public al(e eVar) {
        this.f8425a = eVar;
    }

    public void a() {
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (!(defaultUncaughtExceptionHandler instanceof al)) {
            this.f8426b = defaultUncaughtExceptionHandler;
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    public void a(boolean z) {
        this.f8427c = z;
    }

    public void uncaughtException(Thread thread, Throwable th) {
        if (this.f8427c) {
            this.f8425a.a(th, (String) null, false);
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.f8426b;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }
}
