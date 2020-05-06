package com.miui.antispam.service.a;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f2407a;

    c(d dVar) {
        this.f2407a = dVar;
    }

    public void run() {
        if (!Thread.currentThread().isInterrupted()) {
            this.f2407a.c();
            if (!Thread.currentThread().isInterrupted()) {
                this.f2407a.b();
                Thread.currentThread().isInterrupted();
            }
        }
    }
}
