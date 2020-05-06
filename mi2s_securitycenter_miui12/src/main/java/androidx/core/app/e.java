package androidx.core.app;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@RestrictTo({RestrictTo.a.LIBRARY})
final class e {

    /* renamed from: a  reason: collision with root package name */
    protected static final Class<?> f689a = a();

    /* renamed from: b  reason: collision with root package name */
    protected static final Field f690b = b();

    /* renamed from: c  reason: collision with root package name */
    protected static final Field f691c = c();

    /* renamed from: d  reason: collision with root package name */
    protected static final Method f692d = b(f689a);
    protected static final Method e = a(f689a);
    protected static final Method f = c(f689a);
    private static final Handler g = new Handler(Looper.getMainLooper());

    private static final class a implements Application.ActivityLifecycleCallbacks {

        /* renamed from: a  reason: collision with root package name */
        Object f693a;

        /* renamed from: b  reason: collision with root package name */
        private Activity f694b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f695c = false;

        /* renamed from: d  reason: collision with root package name */
        private boolean f696d = false;
        private boolean e = false;

        a(@NonNull Activity activity) {
            this.f694b = activity;
        }

        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        public void onActivityDestroyed(Activity activity) {
            if (this.f694b == activity) {
                this.f694b = null;
                this.f696d = true;
            }
        }

        public void onActivityPaused(Activity activity) {
            if (this.f696d && !this.e && !this.f695c && e.a(this.f693a, activity)) {
                this.e = true;
                this.f693a = null;
            }
        }

        public void onActivityResumed(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        public void onActivityStarted(Activity activity) {
            if (this.f694b == activity) {
                this.f695c = true;
            }
        }

        public void onActivityStopped(Activity activity) {
        }
    }

    private static Class<?> a() {
        try {
            return Class.forName("android.app.ActivityThread");
        } catch (Throwable unused) {
            return null;
        }
    }

    private static Method a(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        try {
            Method declaredMethod = cls.getDeclaredMethod("performStopActivity", new Class[]{IBinder.class, Boolean.TYPE});
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (Throwable unused) {
            return null;
        }
    }

    static boolean a(@NonNull Activity activity) {
        Object obj;
        Application application;
        a aVar;
        if (Build.VERSION.SDK_INT >= 28) {
            activity.recreate();
            return true;
        } else if (d() && f == null) {
            return false;
        } else {
            if (e == null && f692d == null) {
                return false;
            }
            try {
                Object obj2 = f691c.get(activity);
                if (obj2 == null || (obj = f690b.get(activity)) == null) {
                    return false;
                }
                application = activity.getApplication();
                aVar = new a(activity);
                application.registerActivityLifecycleCallbacks(aVar);
                g.post(new b(aVar, obj2));
                if (d()) {
                    f.invoke(obj, new Object[]{obj2, null, null, 0, false, null, null, false, false});
                } else {
                    activity.recreate();
                }
                g.post(new c(application, aVar));
                return true;
            } catch (Throwable unused) {
                return false;
            }
        }
    }

    protected static boolean a(Object obj, Activity activity) {
        try {
            Object obj2 = f691c.get(activity);
            if (obj2 != obj) {
                return false;
            }
            g.postAtFrontOfQueue(new d(f690b.get(activity), obj2));
            return true;
        } catch (Throwable th) {
            Log.e("ActivityRecreator", "Exception while fetching field values", th);
            return false;
        }
    }

    private static Field b() {
        try {
            Field declaredField = Activity.class.getDeclaredField("mMainThread");
            declaredField.setAccessible(true);
            return declaredField;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static Method b(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        try {
            Method declaredMethod = cls.getDeclaredMethod("performStopActivity", new Class[]{IBinder.class, Boolean.TYPE, String.class});
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static Field c() {
        try {
            Field declaredField = Activity.class.getDeclaredField("mToken");
            declaredField.setAccessible(true);
            return declaredField;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static Method c(Class<?> cls) {
        if (d() && cls != null) {
            try {
                Method declaredMethod = cls.getDeclaredMethod("requestRelaunchActivity", new Class[]{IBinder.class, List.class, List.class, Integer.TYPE, Boolean.TYPE, Configuration.class, Configuration.class, Boolean.TYPE, Boolean.TYPE});
                declaredMethod.setAccessible(true);
                return declaredMethod;
            } catch (Throwable unused) {
            }
        }
        return null;
    }

    private static boolean d() {
        int i = Build.VERSION.SDK_INT;
        return i == 26 || i == 27;
    }
}
