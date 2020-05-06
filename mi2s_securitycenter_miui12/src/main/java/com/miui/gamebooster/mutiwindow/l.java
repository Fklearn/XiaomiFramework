package com.miui.gamebooster.mutiwindow;

import android.util.Log;
import b.b.o.g.e;
import java.util.concurrent.ConcurrentHashMap;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;

public class l {

    /* renamed from: a  reason: collision with root package name */
    private static l f4657a;

    /* renamed from: b  reason: collision with root package name */
    private volatile boolean f4658b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ConcurrentHashMap<j, a> f4659c = new ConcurrentHashMap<>();

    /* renamed from: d  reason: collision with root package name */
    private IForegroundInfoListener.Stub f4660d = new k(this);

    public interface a {
        j getId();

        boolean onForegroundInfoChanged(ForegroundInfo foregroundInfo);
    }

    private l() {
    }

    public static synchronized l a() {
        l lVar;
        synchronized (l.class) {
            if (f4657a == null) {
                f4657a = new l();
            }
            lVar = f4657a;
        }
        return lVar;
    }

    private void d() {
        try {
            if (!this.f4658b) {
                e.a(Class.forName("miui.process.ProcessManager"), "registerForegroundInfoListener", (Class<?>[]) new Class[]{IForegroundInfoListener.class}, this.f4660d);
                Log.i("ProcessMonitor", "registerWhetStoneSuccess");
                this.f4658b = true;
            }
        } catch (Exception e) {
            Log.e("ProcessMonitor", e.toString());
        }
    }

    private void e() {
        try {
            if (this.f4658b) {
                this.f4658b = false;
                e.a(Class.forName("miui.process.ProcessManager"), "unregisterForegroundInfoListener", (Class<?>[]) new Class[]{IForegroundInfoListener.class}, this.f4660d);
                Log.i("ProcessMonitor", "unRegisterForegroundInfoListener");
            }
        } catch (Exception e) {
            Log.e("ProcessMonitor", e.toString());
        }
    }

    public void a(a aVar) {
        if (aVar != null) {
            this.f4659c.put(aVar.getId(), aVar);
        }
    }

    public void b() {
        d();
    }

    public void b(a aVar) {
        if (aVar != null) {
            this.f4659c.remove(aVar.getId());
        }
    }

    public void c() {
        e();
    }
}
