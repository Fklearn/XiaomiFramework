package com.miui.gamebooster.xunyou;

import android.content.Context;
import com.miui.gamebooster.viewPointwidget.b;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class h implements b {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f5411a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public List<String> f5412b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Observable f5413c = new Observable();

    public static abstract class a implements Observer {
        public abstract void a(List<String> list);

        public void update(Observable observable, Object obj) {
            if (obj instanceof List) {
                a((List) obj);
            }
        }
    }

    public h(Context context) {
        this.f5411a = context.getApplicationContext();
    }

    private void c() {
        b.b.c.c.a.a.a(new g(this));
    }

    public void a() {
    }

    public void a(a aVar) {
        List<String> list = this.f5412b;
        if (list != null) {
            aVar.a(list);
        } else {
            this.f5413c.addObserver(aVar);
        }
    }

    public void b() {
        c();
    }

    public void onPause() {
    }

    public void onStop() {
        this.f5413c.deleteObservers();
    }
}
