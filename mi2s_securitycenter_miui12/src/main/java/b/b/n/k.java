package b.b.n;

import android.content.Context;
import android.util.Log;
import b.b.p.f;
import com.miui.systemAdSolution.common.AdInfo;
import com.miui.systemAdSolution.common.AdTrackType;
import com.miui.systemAdSolution.common.Material;
import miui.os.Build;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f1868a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f1869b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AdTrackType f1870c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ AdInfo f1871d;
    final /* synthetic */ Material e;
    final /* synthetic */ long f;

    k(Context context, String str, AdTrackType adTrackType, AdInfo adInfo, Material material, long j) {
        this.f1868a = context;
        this.f1869b = str;
        this.f1870c = adTrackType;
        this.f1871d = adInfo;
        this.e = material;
        this.f = j;
    }

    public void run() {
        try {
            f.a(this.f1868a).a(Build.IS_INTERNATIONAL_BUILD ? "com.miui.securitycenter_globaladevent" : this.f1869b, this.f1870c, this.f1871d.getTagId(), this.f1871d.getId(), this.e, this.f);
        } catch (Exception e2) {
            Log.d("RemoteUnifiedAdService", "doTrack failed", e2);
        }
    }
}
