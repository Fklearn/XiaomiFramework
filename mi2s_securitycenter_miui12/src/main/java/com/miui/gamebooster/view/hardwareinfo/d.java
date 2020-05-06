package com.miui.gamebooster.view.hardwareinfo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.UiThread;
import android.util.Pair;
import b.b.c.c.a.a;
import com.miui.gamebooster.globalgame.util.b;
import com.miui.gamebooster.m.C0388t;
import java.util.concurrent.atomic.AtomicBoolean;

public class d implements Handler.Callback {

    /* renamed from: a  reason: collision with root package name */
    private b f5285a;

    /* renamed from: b  reason: collision with root package name */
    private Long f5286b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Handler f5287c;

    /* renamed from: d  reason: collision with root package name */
    private AtomicBoolean f5288d = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public boolean e;

    public d(b bVar, Long l) {
        this.f5285a = bVar;
        this.f5286b = Long.valueOf(Math.max(1000, l.longValue()));
        this.f5287c = new Handler(Looper.myLooper(), this);
        this.f5287c.sendEmptyMessage(0);
        this.e = !C0388t.g();
    }

    /* access modifiers changed from: private */
    public int a(int i) {
        if (i < 0 || i > 100) {
            return 0;
        }
        return i;
    }

    @UiThread
    private void a(Message message) {
        b bVar = this.f5285a;
        if (bVar != null) {
            Object obj = message.obj;
            if (obj instanceof Pair) {
                try {
                    Pair pair = (Pair) obj;
                    bVar.a(((Integer) pair.first).intValue(), ((Integer) pair.second).intValue());
                } catch (Exception e2) {
                    b.b(e2);
                }
            }
            this.f5287c.sendEmptyMessageDelayed(0, this.f5286b.longValue());
        }
    }

    private void d() {
        if (this.f5285a != null) {
            a.a(new c(this));
        }
    }

    public void a() {
        this.f5288d.set(true);
        this.f5287c.removeCallbacksAndMessages((Object) null);
        this.f5285a = null;
    }

    public boolean b() {
        return this.f5288d.get();
    }

    public boolean c() {
        return this.e;
    }

    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            d();
            return false;
        } else if (i != 1) {
            return false;
        } else {
            a(message);
            return false;
        }
    }
}
