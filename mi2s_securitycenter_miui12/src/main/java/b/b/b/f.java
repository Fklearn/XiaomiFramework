package b.b.b;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.miui.antivirus.model.DangerousInfo;

class f extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f1545a;

    f(g gVar) {
        this.f1545a = gVar;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String stringExtra = intent.getStringExtra("android.intent.extra.PACKAGE_NAME");
            synchronized (this.f1545a.f1549d) {
                DangerousInfo dangerousInfo = (DangerousInfo) this.f1545a.f1548c.get(stringExtra);
                if (dangerousInfo != null) {
                    this.f1545a.a(dangerousInfo);
                }
            }
        }
    }
}
