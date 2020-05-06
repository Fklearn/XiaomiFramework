package androidx.core.app;

import android.util.Log;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Object f687a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Object f688b;

    d(Object obj, Object obj2) {
        this.f687a = obj;
        this.f688b = obj2;
    }

    public void run() {
        try {
            if (e.f692d != null) {
                e.f692d.invoke(this.f687a, new Object[]{this.f688b, false, "AppCompat recreation"});
                return;
            }
            e.e.invoke(this.f687a, new Object[]{this.f688b, false});
        } catch (RuntimeException e) {
            if (e.getClass() == RuntimeException.class && e.getMessage() != null && e.getMessage().startsWith("Unable to stop")) {
                throw e;
            }
        } catch (Throwable th) {
            Log.e("ActivityRecreator", "Exception while invoking performStopActivity", th);
        }
    }
}
