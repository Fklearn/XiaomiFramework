package b.b.p;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.systemAdSolution.changeSkin.IChangeSkinService;

class c implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f1892a;

    c(f fVar) {
        this.f1892a = fVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d("RemoteUnifiedAdService", "onServiceConnected. from startting binding service to connected,it spent " + (System.currentTimeMillis() - f.f1902d) + "ms.");
        try {
            synchronized (this.f1892a.g) {
                IChangeSkinService unused = this.f1892a.f = IChangeSkinService.Stub.asInterface(iBinder);
                this.f1892a.g.notifyAll();
            }
        } catch (Exception e) {
            Log.e("RemoteUnifiedAdService", "onServiceConnected", e);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Log.d("RemoteUnifiedAdService", "onServiceDisconnected");
        synchronized (this.f1892a.g) {
            IChangeSkinService unused = this.f1892a.f = null;
        }
    }
}
