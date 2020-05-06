package b.b.c.f;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.util.Log;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static a f1708a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1709b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Map<String, c> f1710c = Collections.synchronizedMap(new HashMap());

    /* renamed from: b.b.c.f.a$a  reason: collision with other inner class name */
    public interface C0027a {
        boolean a(IBinder iBinder);
    }

    private class b implements ServiceConnection {

        /* renamed from: a  reason: collision with root package name */
        private List<C0027a> f1711a = new ArrayList();

        /* renamed from: b  reason: collision with root package name */
        private String f1712b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public boolean f1713c;

        b(String str) {
            this.f1712b = str;
        }

        public void a(C0027a aVar) {
            this.f1711a.add(aVar);
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("BinderManager", "onServiceCCCCConnected " + componentName.toShortString());
            c cVar = (c) a.this.f1710c.get(this.f1712b);
            if (cVar != null) {
                cVar.e = iBinder;
                cVar.f1717c = true;
                synchronized (cVar.g) {
                    for (C0027a a2 : this.f1711a) {
                        a.this.a(a2, cVar, iBinder);
                    }
                    this.f1711a.clear();
                }
            }
            if (this.f1711a.size() > 0) {
                Log.d("BinderManager", "onServiceCCCCConnected set isServiceNotAvailable to false");
                this.f1713c = false;
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("BinderManager", "onServiceDDDDDisconnected " + componentName.toShortString());
            this.f1713c = true;
            c cVar = (c) a.this.f1710c.get(this.f1712b);
            if (cVar != null) {
                cVar.e = null;
                cVar.f1717c = false;
            }
        }
    }

    class c {

        /* renamed from: a  reason: collision with root package name */
        String f1715a;

        /* renamed from: b  reason: collision with root package name */
        String f1716b;

        /* renamed from: c  reason: collision with root package name */
        boolean f1717c = false;

        /* renamed from: d  reason: collision with root package name */
        int f1718d = 0;
        IBinder e;
        b f;
        Object g = new Object();

        c() {
        }
    }

    private a(Context context) {
        this.f1709b = context.getApplicationContext();
    }

    public static synchronized a a(Context context) {
        a aVar;
        synchronized (a.class) {
            if (f1708a == null) {
                f1708a = new a(context);
            }
            aVar = f1708a;
        }
        return aVar;
    }

    /* access modifiers changed from: private */
    public void a(C0027a aVar, c cVar, IBinder iBinder) {
        if (aVar != null && aVar.a(iBinder)) {
            a(cVar);
        }
    }

    private void a(c cVar) {
        if (cVar != null) {
            synchronized (cVar.g) {
                cVar.f1718d--;
                Log.d("BinderManager", "action:" + cVar.f1715a + "   bindCount : " + cVar.f1718d);
                if (cVar.f1718d == 0) {
                    try {
                        if (!(cVar.e == null || cVar.f == null)) {
                            Log.d("BinderManager", "BinderManager execute releaseService");
                            this.f1709b.unbindService(cVar.f);
                        }
                        this.f1710c.remove(cVar.f1715a);
                    } catch (IllegalArgumentException e) {
                        Log.e("BinderManager", "IllegalArgumentException:", e);
                    }
                }
            }
        }
    }

    private boolean a(Context context, Intent intent) {
        List<ResolveInfo> queryIntentServices = context.getPackageManager().queryIntentServices(intent, 4);
        return queryIntentServices != null && !queryIntentServices.isEmpty();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r2 = r2.f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(java.lang.String r2) {
        /*
            r1 = this;
            java.util.Map<java.lang.String, b.b.c.f.a$c> r0 = r1.f1710c
            java.lang.Object r2 = r0.get(r2)
            b.b.c.f.a$c r2 = (b.b.c.f.a.c) r2
            if (r2 == 0) goto L_0x0016
            b.b.c.f.a$b r2 = r2.f
            if (r2 == 0) goto L_0x0016
            boolean r2 = r2.f1713c
            if (r2 == 0) goto L_0x0016
            r2 = 1
            return r2
        L_0x0016:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.f.a.a(java.lang.String):boolean");
    }

    public boolean a(String str, String str2, C0027a aVar) {
        boolean z;
        c cVar = this.f1710c.get(str);
        if (cVar == null) {
            cVar = new c();
            cVar.f1715a = str;
            cVar.f1716b = str2;
            cVar.f = new b(str);
            this.f1710c.put(cVar.f1715a, cVar);
        }
        synchronized (cVar.g) {
            z = true;
            cVar.f1718d++;
            if (cVar.f != null) {
                boolean unused = cVar.f.f1713c = false;
            }
        }
        Log.d("BinderManager", "action:" + str + "   bindCount : " + cVar.f1718d);
        if (cVar.e != null) {
            Log.d("BinderManager", "find cached binder:" + cVar.e + " thread:" + Thread.currentThread());
            a(aVar, cVar, cVar.e);
        } else {
            synchronized (cVar.g) {
                c cVar2 = this.f1710c.get(str);
                if (cVar2 != null) {
                    if (cVar2.e != null) {
                        Log.d("BinderManager", "find cached binder in synchronized code:" + cVar2.e + " thread:" + Thread.currentThread() + "  isBindServiceSuccess:" + true);
                        a(aVar, cVar2, cVar2.e);
                        return true;
                    }
                    cVar2.f.a(aVar);
                    if (!cVar2.f1717c) {
                        Intent intent = new Intent(str);
                        intent.setPackage(cVar2.f1716b);
                        boolean bindService = this.f1709b.bindService(intent, cVar2.f, 1);
                        if (a(this.f1709b, intent) || bindService) {
                            cVar2.f1717c = true;
                        } else {
                            cVar2.f1717c = false;
                        }
                        Log.d("BinderManager", "can not  find cached binder，bind service thread:" + Thread.currentThread() + "  isBindServiceSuccess:" + bindService);
                        z = bindService;
                    }
                }
            }
        }
        return z;
    }

    public void b(String str) {
        a(this.f1710c.get(str));
    }
}
