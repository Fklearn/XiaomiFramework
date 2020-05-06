package com.miui.applicationlock.c;

import android.content.Context;
import android.hardware.miuiface.IMiuiFaceManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import b.b.c.j.g;
import b.b.o.g.e;
import com.miui.gamebooster.m.C0384o;
import miui.util.ArraySet;

public class C {

    /* renamed from: a  reason: collision with root package name */
    private static C f3272a;

    /* renamed from: b  reason: collision with root package name */
    private static IMiuiFaceManager f3273b;

    /* renamed from: c  reason: collision with root package name */
    public static final ArraySet<String> f3274c = new ArraySet<>();

    /* renamed from: d  reason: collision with root package name */
    protected int f3275d;
    protected Handler e;
    protected HandlerThread f = new HandlerThread("applock_face_unlock_thread");
    /* access modifiers changed from: private */
    public long g;
    /* access modifiers changed from: private */
    public Context h;
    /* access modifiers changed from: private */
    public int i;
    /* access modifiers changed from: private */
    public boolean j = false;
    private int k;
    /* access modifiers changed from: private */
    public int l;
    /* access modifiers changed from: private */
    public boolean m = false;
    /* access modifiers changed from: private */
    public boolean n;
    /* access modifiers changed from: private */
    public CancellationSignal o = null;
    /* access modifiers changed from: private */
    public z p = null;
    /* access modifiers changed from: private */
    public Handler q = new A(this, Looper.getMainLooper());
    /* access modifiers changed from: private */
    public boolean r;
    IMiuiFaceManager.AuthenticationCallback s = new B(this);

    static {
        f3274c.add("perseus");
        f3274c.add("andromeda");
        f3274c.add("davinci");
        f3274c.add("davinciin");
        f3274c.add("raphael");
        f3274c.add("raphaelin");
        f3274c.add("lmi");
        f3274c.add("lmiin");
    }

    private C(Context context) {
        this.h = context;
        h();
    }

    public static synchronized C a(Context context) {
        C c2;
        synchronized (C.class) {
            c(context);
            if (f3272a == null) {
                f3272a = new C(context);
            }
            c2 = f3272a;
        }
        return c2;
    }

    public static boolean b(Context context) {
        try {
            Class<?> cls = Class.forName("miui.os.DeviceFeature");
            return ((Boolean) cls.getDeclaredMethod("hasPopupCameraSupport", new Class[0]).invoke(cls, new Object[0])).booleanValue();
        } catch (Exception e2) {
            Log.e("applock_face_unlock", "reflect error when get hasPopupCameraSupport state", e2);
            return false;
        }
    }

    private static void c(Context context) {
        try {
            f3273b = (IMiuiFaceManager) e.a(Class.forName("android.hardware.miuiface.MiuiFaceFactory"), IMiuiFaceManager.class, "getFaceManager", (Class<?>[]) new Class[]{Context.class, Integer.TYPE}, context, 0);
        } catch (Exception e2) {
            Log.e("applock_face_unlock", "getFaceManager exception: ", e2);
        }
    }

    /* access modifiers changed from: private */
    public void g() {
        f();
        this.o = null;
        this.q.sendEmptyMessage(1005);
    }

    private void h() {
        try {
            if (c()) {
                this.f.start();
                this.e = new Handler(this.f.getLooper());
                t.a();
            }
        } catch (Exception e2) {
            Log.e("applock_face_unlock", "initFaceUnlockUtil exception: ", e2);
        }
    }

    private boolean i() {
        return this.f3275d >= 5 || this.k >= 3;
    }

    private boolean j() {
        int i2;
        try {
            i2 = C0384o.a(this.h.getContentResolver(), "sc_status", 0, -2);
        } catch (Exception e2) {
            Log.e("applock_face_unlock", "isSlideCoverOpened exception: ", e2);
            i2 = 0;
        }
        return !"perseus".equals(Build.DEVICE) || i2 == 0;
    }

    public void a(z zVar) {
        if (i()) {
            Log.d("applock_face_unlock", "face unlock locked");
            this.q.sendEmptyMessage(1006);
            g();
            return;
        }
        this.p = zVar;
        if (this.o != null) {
            Log.d("applock_face_unlock", "start face unlock is running");
            g();
            return;
        }
        this.i = 0;
        this.m = false;
        this.j = false;
        this.n = false;
        this.r = true;
        this.g = System.currentTimeMillis();
        this.o = new CancellationSignal();
        try {
            e.a((Object) f3273b, "authenticate", (Class<?>[]) new Class[]{CancellationSignal.class, Integer.TYPE, IMiuiFaceManager.AuthenticationCallback.class, Handler.class, Integer.TYPE}, this.o, 0, this.s, this.e, 5000);
        } catch (Exception e2) {
            Log.e("applock_face_unlock", "face unlock authenticate exception: ", e2);
        }
        t.b();
        this.q.sendEmptyMessage(1001);
    }

    public void a(Runnable runnable) {
        HandlerThread handlerThread = this.f;
        if (handlerThread != null && this.e != null) {
            if (handlerThread.getThreadId() == Process.myTid()) {
                runnable.run();
            } else {
                this.e.post(runnable);
            }
        }
    }

    public boolean a() {
        int i2;
        try {
            i2 = ((Integer) e.a((Object) f3273b, Integer.TYPE, "hasEnrolledFaces", (Class<?>[]) null, new Object[0])).intValue();
        } catch (Exception e2) {
            Log.e("applock_face_unlock", "hasEnrolledFaces exception:", e2);
            i2 = 0;
        }
        return i2 > 0;
    }

    public boolean b() {
        return true;
    }

    public boolean c() {
        if (g.a(this.h) != 0 || f3273b == null || f3274c.contains(Build.DEVICE) || b(this.h)) {
            return false;
        }
        try {
            return ((Boolean) e.a((Object) f3273b, Boolean.TYPE, "isFaceFeatureSupport", (Class<?>[]) null, new Object[0])).booleanValue();
        } catch (Exception e2) {
            Log.e("applock_face_unlock", "isFaceFeatureSupport", e2);
            return false;
        }
    }

    public void d() {
        this.f3275d = 0;
        this.k = 0;
    }

    public boolean e() {
        return g.a(this.h) == 0 && j() && f3272a.c();
    }

    public void f() {
        Log.d("applock_face_unlock", "stopFaceUnlock");
        CancellationSignal cancellationSignal = this.o;
        if (cancellationSignal != null && this.r) {
            if (!cancellationSignal.isCanceled()) {
                Log.i("applock_face_unlock", "call stopFaceUnlock cancel");
                this.o.cancel();
            }
            this.r = false;
            this.o = null;
            if (this.m && !this.n) {
                this.f3275d++;
            }
            if (this.j) {
                this.k++;
            }
        }
    }
}
