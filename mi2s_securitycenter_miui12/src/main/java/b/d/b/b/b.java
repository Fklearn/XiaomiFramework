package b.d.b.b;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import java.util.concurrent.FutureTask;

public abstract class b<R> implements ServiceConnection {
    private static final String TAG = "RemoteMethodInvoker";
    private final a<IBinder> future = new a<>();
    private final Context mContext;

    private static class a<V> extends FutureTask<V> {
        public a() {
            super(new a());
        }

        public void a(V v) {
            set(v);
        }
    }

    public b(Context context) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
            return;
        }
        throw new IllegalArgumentException("context can't be null");
    }

    /* access modifiers changed from: protected */
    public abstract boolean bindService(Context context, ServiceConnection serviceConnection);

    /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x0034 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public R invoke() {
        /*
            r4 = this;
            b.d.b.c.h.a()
            android.content.Context r0 = r4.mContext
            boolean r0 = r4.bindService(r0, r4)
            java.lang.String r1 = "RemoteMethodInvoker"
            r2 = 0
            if (r0 == 0) goto L_0x0047
            b.d.b.b.b$a<android.os.IBinder> r0 = r4.future     // Catch:{ InterruptedException -> 0x0034, ExecutionException -> 0x002e, RemoteException -> 0x0022 }
            java.lang.Object r0 = r0.get()     // Catch:{ InterruptedException -> 0x0034, ExecutionException -> 0x002e, RemoteException -> 0x0022 }
            android.os.IBinder r0 = (android.os.IBinder) r0     // Catch:{ InterruptedException -> 0x0034, ExecutionException -> 0x002e, RemoteException -> 0x0022 }
            java.lang.Object r0 = r4.invokeRemoteMethod(r0)     // Catch:{ InterruptedException -> 0x0034, ExecutionException -> 0x002e, RemoteException -> 0x0022 }
            android.content.Context r1 = r4.mContext
            r1.unbindService(r4)
            return r0
        L_0x0020:
            r0 = move-exception
            goto L_0x0041
        L_0x0022:
            r0 = move-exception
            java.lang.String r3 = "error while invoking service methods"
            android.util.Log.e(r1, r3, r0)     // Catch:{ all -> 0x0020 }
            android.content.Context r0 = r4.mContext
            r0.unbindService(r4)
            return r2
        L_0x002e:
            android.content.Context r0 = r4.mContext
            r0.unbindService(r4)
            return r2
        L_0x0034:
            java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0020 }
            r0.interrupt()     // Catch:{ all -> 0x0020 }
            android.content.Context r0 = r4.mContext
            r0.unbindService(r4)
            return r2
        L_0x0041:
            android.content.Context r1 = r4.mContext
            r1.unbindService(r4)
            throw r0
        L_0x0047:
            java.lang.String r0 = "Cannot bind remote service."
            android.util.Log.e(r1, r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: b.d.b.b.b.invoke():java.lang.Object");
    }

    /* access modifiers changed from: protected */
    public abstract R invokeRemoteMethod(IBinder iBinder);

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.i(TAG, "RemoteMethodInvoker connects remote service " + componentName.getShortClassName());
        this.future.a(iBinder);
    }

    public void onServiceDisconnected(ComponentName componentName) {
    }
}
