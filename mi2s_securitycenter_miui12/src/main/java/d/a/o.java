package d.a;

import android.os.Handler;
import android.os.Looper;
import d.a.g.C0574a;
import d.a.g.C0575b;
import d.a.g.C0576c;
import d.a.g.h;

public class o extends d {

    /* renamed from: a  reason: collision with root package name */
    static i f8801a = new n();

    /* renamed from: b  reason: collision with root package name */
    private h f8802b;
    private Handler mHandler;

    public o() {
        this((Object) null);
    }

    private o(Object obj) {
        this.f8802b = new h(obj == null ? Integer.valueOf(getId()) : obj);
        Looper myLooper = Looper.myLooper();
        if (myLooper != Looper.getMainLooper()) {
            this.mHandler = new Handler(myLooper);
        }
    }

    /* synthetic */ o(Object obj, n nVar) {
        this(obj);
    }

    public float getDefaultMinVisible() {
        return 0.002f;
    }

    public int getIntValue(C0576c cVar) {
        Integer num = (Integer) this.f8802b.a(cVar.getName(), Integer.TYPE);
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    public float getMinVisibleChange(Object obj) {
        if (!(obj instanceof C0576c) || (obj instanceof C0574a)) {
            return super.getMinVisibleChange(obj);
        }
        return 1.0f;
    }

    public C0575b getProperty(int i) {
        return null;
    }

    public Object getTargetObject() {
        return this.f8802b;
    }

    public int getType(C0575b bVar) {
        return -1;
    }

    public float getValue(C0575b bVar) {
        Float f = (Float) this.f8802b.a(bVar.getName(), Float.TYPE);
        if (f == null) {
            return 0.0f;
        }
        return f.floatValue();
    }

    public boolean isValid() {
        return this.f8802b.a();
    }

    public void post(Runnable runnable) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public void setIntValue(C0576c cVar, int i) {
        this.f8802b.a(cVar.getName(), Integer.TYPE, Integer.valueOf(i));
    }

    public void setValue(C0575b bVar, float f) {
        this.f8802b.a(bVar.getName(), Float.TYPE, Float.valueOf(f));
    }
}
