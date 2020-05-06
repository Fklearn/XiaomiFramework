package b.b.p;

import android.util.Log;
import com.miui.systemAdSolution.common.AdTrackType;
import com.miui.systemAdSolution.common.Material;
import java.util.concurrent.Callable;

class d implements Callable<Boolean> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f1893a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f1894b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AdTrackType f1895c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ String f1896d;
    final /* synthetic */ long e;
    final /* synthetic */ Material f;
    final /* synthetic */ long g;
    final /* synthetic */ f h;

    d(f fVar, String str, String str2, AdTrackType adTrackType, String str3, long j, Material material, long j2) {
        this.h = fVar;
        this.f1893a = str;
        this.f1894b = str2;
        this.f1895c = adTrackType;
        this.f1896d = str3;
        this.e = j;
        this.f = material;
        this.g = j2;
    }

    public Boolean call() {
        try {
            synchronized (this.h.g) {
                this.h.e();
                if (!this.h.d()) {
                    this.h.g.wait(1000);
                }
                if (!this.h.d()) {
                    return false;
                }
                Boolean valueOf = Boolean.valueOf(this.h.f.doTrack(this.f1893a, this.f1894b, this.f1895c, this.f1896d, this.e, this.f.getId(), this.g));
                return valueOf;
            }
        } catch (Exception e2) {
            Log.e("RemoteUnifiedAdService", "could not do track async.", e2);
            return false;
        }
    }
}
