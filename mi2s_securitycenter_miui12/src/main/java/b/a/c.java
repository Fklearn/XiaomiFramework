package b.a;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Debug;
import android.os.IBinder;

public abstract class c {

    /* renamed from: a  reason: collision with root package name */
    protected final String f1303a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public final Context f1304b;

    /* renamed from: c  reason: collision with root package name */
    protected final Intent f1305c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public b f1306d;
    private String e = " unnamed";
    /* access modifiers changed from: private */
    public final ServiceConnection f = new a(this, (a) null);
    private int g = 45;
    private long h;
    private boolean i = false;
    /* access modifiers changed from: private */
    public boolean j = false;

    private class a implements ServiceConnection {
        private a() {
        }

        /* synthetic */ a(c cVar, a aVar) {
            this();
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            c.this.a(iBinder);
            new b(this).execute(new Void[0]);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            c.this.a();
        }
    }

    public interface b {
        void run();
    }

    public c(Context context, Intent intent) {
        this.f1304b = context;
        this.f1305c = intent;
        this.f1303a = getClass().getSimpleName();
        if (Debug.isDebuggerConnected()) {
            this.g <<= 2;
        }
    }

    public abstract void a();

    public abstract void a(IBinder iBinder);

    /* access modifiers changed from: protected */
    public boolean a(b bVar, String str) {
        if (!this.i) {
            this.i = true;
            this.e = str;
            this.f1306d = bVar;
            this.h = System.currentTimeMillis();
            System.currentTimeMillis();
            return this.f1304b.bindService(this.f1305c, this.f, 1);
        }
        throw new IllegalStateException("Cannot call setTask twice on the same ServiceProxy.");
    }
}
