package d.a;

import android.os.Handler;
import android.util.ArrayMap;
import d.a.d.i;
import d.a.g.C0575b;
import d.a.g.C0576c;
import d.a.g.e;
import d.a.i.f;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class d<T> {
    public static final long FLAT_ONESHOT = 1;
    private static Map<String, C0575b> sPropertyMap = new ArrayMap();
    private static AtomicInteger sTargetIds = new AtomicInteger(Integer.MAX_VALUE);
    private i mAnimTask;
    private float mDefaultMinVisible = Float.MAX_VALUE;
    private long mFlags;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    private int mId = sTargetIds.decrementAndGet();
    private Map<Object, Float> mMinVisibleChanges = new ArrayMap();
    private Map<C0575b, a> mMonitors = new ArrayMap();
    private ArrayMap<Object, Double> mValueMap = new ArrayMap<>();

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        f f8682a;

        /* renamed from: b  reason: collision with root package name */
        b f8683b;

        private a() {
            this.f8682a = new f();
            this.f8683b = new b(this);
        }
    }

    private static class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        WeakReference<d> f8684a;

        /* renamed from: b  reason: collision with root package name */
        C0575b f8685b;

        /* renamed from: c  reason: collision with root package name */
        a f8686c;

        b(a aVar) {
            this.f8686c = aVar;
        }

        /* access modifiers changed from: package-private */
        public void a(d dVar, C0575b bVar) {
            WeakReference<d> weakReference = this.f8684a;
            if (weakReference == null || weakReference.get() != dVar) {
                this.f8684a = new WeakReference<>(dVar);
            }
            this.f8685b = bVar;
            dVar.mHandler.removeCallbacks(this);
            dVar.mHandler.postDelayed(this, 500);
        }

        public void run() {
            d dVar = (d) this.f8684a.get();
            if (dVar != null) {
                if (!dVar.getAnimTask().a(this.f8685b)) {
                    dVar.setVelocity(this.f8685b, 0.0d);
                }
                this.f8686c.f8682a.a();
            }
        }
    }

    public d() {
        setMinVisibleChange(0.1f, 9, 10, 11);
        setMinVisibleChange(0.00390625f, 4, 14, 7, 8);
        setMinVisibleChange(0.002f, 2, 3);
    }

    private a getMonitor(C0575b bVar) {
        a aVar = this.mMonitors.get(bVar);
        if (aVar != null) {
            return aVar;
        }
        a aVar2 = new a();
        this.mMonitors.put(bVar, aVar2);
        return aVar2;
    }

    public boolean allowAnimRun() {
        return true;
    }

    public C0575b createProperty(String str, Class<?> cls) {
        C0575b bVar = sPropertyMap.get(str);
        if (bVar != null) {
            return bVar;
        }
        C0575b eVar = (cls == Integer.TYPE || cls == Integer.class) ? new e(str) : new d.a.g.f(str);
        sPropertyMap.put(str, eVar);
        return eVar;
    }

    public void executeOnInitialized(Runnable runnable) {
        post(runnable);
    }

    public i getAnimTask() {
        if (this.mAnimTask == null) {
            this.mAnimTask = new i(this);
        }
        return this.mAnimTask;
    }

    public float getDefaultMinVisible() {
        return 1.0f;
    }

    public int getId() {
        return this.mId;
    }

    public int getIntValue(C0576c cVar) {
        Object targetObject = getTargetObject();
        if (targetObject != null) {
            return cVar.getIntValue(targetObject);
        }
        return Integer.MAX_VALUE;
    }

    public void getLocationOnScreen(int[] iArr) {
        iArr[1] = 0;
        iArr[0] = 0;
    }

    public float getMinVisibleChange(Object obj) {
        int type;
        Float f = this.mMinVisibleChanges.get(obj);
        if (f == null && (obj instanceof C0575b) && (type = getType((C0575b) obj)) != -1) {
            f = this.mMinVisibleChanges.get(Integer.valueOf(type));
        }
        if (f != null) {
            return f.floatValue();
        }
        float f2 = this.mDefaultMinVisible;
        return f2 != Float.MAX_VALUE ? f2 : getDefaultMinVisible();
    }

    public abstract C0575b getProperty(int i);

    public abstract T getTargetObject();

    public abstract int getType(C0575b bVar);

    public float getValue(int i) {
        return getValue(getProperty(i));
    }

    public float getValue(C0575b bVar) {
        Object targetObject = getTargetObject();
        if (targetObject != null) {
            return bVar.getValue(targetObject);
        }
        return Float.MAX_VALUE;
    }

    public double getVelocity(C0575b bVar) {
        Double d2 = this.mValueMap.get(bVar);
        if (d2 != null) {
            return d2.doubleValue();
        }
        return 0.0d;
    }

    public boolean hasFlags(long j) {
        return d.a.i.a.a(this.mFlags, j);
    }

    public boolean isValid() {
        return true;
    }

    public void onFrameEnd(boolean z) {
    }

    public void post(Runnable runnable) {
        runnable.run();
    }

    public d setDefaultMinVisibleChange(float f) {
        this.mDefaultMinVisible = f;
        return this;
    }

    public void setFlags(long j) {
        this.mFlags = j;
    }

    public void setIntValue(C0576c cVar, int i) {
        Object targetObject = getTargetObject();
        if (targetObject != null && i != Integer.MAX_VALUE) {
            cVar.setIntValue(targetObject, i);
        }
    }

    public d setMinVisibleChange(float f, int... iArr) {
        for (int valueOf : iArr) {
            this.mMinVisibleChanges.put(Integer.valueOf(valueOf), Float.valueOf(f));
        }
        return this;
    }

    public d setMinVisibleChange(float f, String... strArr) {
        for (String fVar : strArr) {
            setMinVisibleChange((Object) new d.a.g.f(fVar), f);
        }
        return this;
    }

    public d setMinVisibleChange(Object obj, float f) {
        this.mMinVisibleChanges.put(obj, Float.valueOf(f));
        return this;
    }

    public void setValue(C0575b bVar, float f) {
        Object targetObject = getTargetObject();
        if (targetObject != null && f != Float.MAX_VALUE) {
            bVar.setValue(targetObject, f);
        }
    }

    public void setVelocity(C0575b bVar, double d2) {
        if (d2 != 3.4028234663852886E38d) {
            this.mValueMap.put(bVar, Double.valueOf(d2));
        }
    }

    public boolean shouldUseIntValue(C0575b bVar) {
        return bVar instanceof C0576c;
    }

    public void trackVelocity(C0575b bVar, double d2) {
        a monitor = getMonitor(bVar);
        monitor.f8682a.a(d2);
        float a2 = monitor.f8682a.a(0);
        if (a2 > 0.0f) {
            monitor.f8683b.a(this, bVar);
        }
        setVelocity(bVar, (double) a2);
    }
}
