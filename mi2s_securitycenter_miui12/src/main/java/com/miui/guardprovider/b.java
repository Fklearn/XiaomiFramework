package com.miui.guardprovider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import java.util.ArrayList;
import java.util.List;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static b f5470a;

    /* renamed from: b  reason: collision with root package name */
    private Context f5471b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f5472c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f5473d = false;
    /* access modifiers changed from: private */
    public IAntiVirusServer e;
    private c f;

    public interface a {
        void a(IAntiVirusServer iAntiVirusServer);
    }

    /* renamed from: com.miui.guardprovider.b$b  reason: collision with other inner class name */
    public interface C0050b {
        void a();
    }

    private class c implements ServiceConnection {

        /* renamed from: a  reason: collision with root package name */
        private List<a> f5474a;

        /* renamed from: b  reason: collision with root package name */
        private List<C0050b> f5475b;

        private c() {
            this.f5474a = new ArrayList();
            this.f5475b = new ArrayList();
        }

        public void a(a aVar) {
            this.f5474a.add(aVar);
        }

        public void a(C0050b bVar) {
            this.f5475b.add(bVar);
        }

        public void b(C0050b bVar) {
            this.f5475b.remove(bVar);
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (b.this) {
                IAntiVirusServer unused = b.this.e = IAntiVirusServer.Stub.a(iBinder);
                for (a next : this.f5474a) {
                    if (next != null) {
                        next.a(b.this.e);
                    }
                }
                this.f5474a.clear();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (b.this) {
                IAntiVirusServer unused = b.this.e = null;
                boolean unused2 = b.this.f5473d = false;
                int unused3 = b.this.f5472c = 0;
                for (C0050b next : this.f5475b) {
                    if (next != null) {
                        next.a();
                    }
                }
                this.f5475b.clear();
            }
        }
    }

    private b(Context context) {
        this.f5471b = context.getApplicationContext();
        this.e = null;
        this.f5472c = 0;
        this.f = new c();
    }

    public static synchronized b a(Context context) {
        b bVar;
        synchronized (b.class) {
            if (f5470a == null) {
                f5470a = new b(context);
            }
            bVar = f5470a;
        }
        return bVar;
    }

    private void b() {
        Intent intent = new Intent("com.miui.guardprovider.action.antivirusservice");
        intent.setPackage("com.miui.guardprovider");
        this.f5471b.bindService(intent, this.f, 1);
    }

    private void c() {
        this.e = null;
        this.f5473d = false;
        this.f5471b.unbindService(this.f);
    }

    public void a() {
        synchronized (this) {
            if (this.e != null) {
                this.f5472c--;
                Log.i("GpBinderManager", "unbindCount : " + this.f5472c);
                if (this.f5472c == 0) {
                    c();
                }
            }
        }
    }

    public void a(a aVar) {
        this.f5472c++;
        Log.i("GpBinderManager", "bindCount : " + this.f5472c);
        b(aVar);
    }

    public void a(C0050b bVar) {
        synchronized (this) {
            this.f.a(bVar);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(com.miui.guardprovider.b.a r2) {
        /*
            r1 = this;
            com.miui.guardprovider.aidl.IAntiVirusServer r0 = r1.e
            if (r0 == 0) goto L_0x000a
            if (r2 == 0) goto L_0x0024
            r2.a(r0)
            goto L_0x0024
        L_0x000a:
            monitor-enter(r1)
            com.miui.guardprovider.aidl.IAntiVirusServer r0 = r1.e     // Catch:{ all -> 0x0025 }
            if (r0 == 0) goto L_0x0014
            r1.b((com.miui.guardprovider.b.a) r2)     // Catch:{ all -> 0x0025 }
            monitor-exit(r1)     // Catch:{ all -> 0x0025 }
            return
        L_0x0014:
            com.miui.guardprovider.b$c r0 = r1.f     // Catch:{ all -> 0x0025 }
            r0.a((com.miui.guardprovider.b.a) r2)     // Catch:{ all -> 0x0025 }
            boolean r2 = r1.f5473d     // Catch:{ all -> 0x0025 }
            if (r2 != 0) goto L_0x0023
            r1.b()     // Catch:{ all -> 0x0025 }
            r2 = 1
            r1.f5473d = r2     // Catch:{ all -> 0x0025 }
        L_0x0023:
            monitor-exit(r1)     // Catch:{ all -> 0x0025 }
        L_0x0024:
            return
        L_0x0025:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0025 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.guardprovider.b.b(com.miui.guardprovider.b$a):void");
    }

    public void b(C0050b bVar) {
        synchronized (this) {
            this.f.b(bVar);
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        super.finalize();
        c();
    }
}
