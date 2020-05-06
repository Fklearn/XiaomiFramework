package com.xiaomi.stat;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.xiaomi.stat.d.r;

public class ah {

    /* renamed from: a  reason: collision with root package name */
    public static final int f8416a = 1;

    /* renamed from: b  reason: collision with root package name */
    private static final int f8417b = 10000;

    /* renamed from: c  reason: collision with root package name */
    private static final int f8418c = 3;

    /* renamed from: d  reason: collision with root package name */
    private Handler f8419d;
    /* access modifiers changed from: private */
    public Runnable e;
    private HandlerThread f;
    /* access modifiers changed from: private */
    public int g = 3;
    /* access modifiers changed from: private */
    public int h = 10000;
    private int i = 0;
    /* access modifiers changed from: private */
    public boolean j = false;

    class a implements Handler.Callback {

        /* renamed from: b  reason: collision with root package name */
        private Handler f8421b;

        private a() {
            this.f8421b = null;
        }

        /* access modifiers changed from: private */
        public void a(Handler handler) {
            this.f8421b = handler;
        }

        public boolean handleMessage(Message message) {
            if (message.what == 1) {
                int intValue = ((Integer) message.obj).intValue();
                if (intValue < ah.this.g) {
                    ah.this.e.run();
                    if (ah.this.j) {
                        Message obtainMessage = this.f8421b.obtainMessage(1);
                        obtainMessage.obj = Integer.valueOf(intValue + 1);
                        this.f8421b.sendMessageDelayed(obtainMessage, (long) ah.this.h);
                    }
                } else {
                    ah.this.b();
                }
            }
            return true;
        }
    }

    public ah(Runnable runnable) {
        this.e = runnable;
    }

    private void d() {
        a aVar = new a();
        this.f = new HandlerThread("".concat("_").concat(String.valueOf(r.b())));
        this.f.start();
        this.f8419d = new Handler(this.f.getLooper(), aVar);
        aVar.a(this.f8419d);
    }

    public void a() {
        Handler handler = this.f8419d;
        if (handler == null || !handler.hasMessages(1)) {
            d();
            Message obtainMessage = this.f8419d.obtainMessage(1);
            obtainMessage.obj = 0;
            this.j = true;
            this.f8419d.sendMessageDelayed(obtainMessage, (long) this.i);
        }
    }

    public void a(int i2) {
        this.i = i2;
    }

    public void b() {
        this.f8419d.removeMessages(1);
        this.f8419d.getLooper().quit();
        this.j = false;
    }

    public void b(int i2) {
        this.g = i2;
    }

    public void c(int i2) {
        this.h = i2;
    }

    public boolean c() {
        return this.j;
    }
}
