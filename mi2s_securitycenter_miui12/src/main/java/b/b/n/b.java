package b.b.n;

import android.util.Log;
import com.miui.systemAdSolution.common.AdInfo;
import com.miui.systemAdSolution.common.AdTrackType;
import com.miui.systemAdSolution.common.Material;
import miui.os.Build;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f1846a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f1847b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AdTrackType f1848c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ long f1849d;
    final /* synthetic */ e e;

    b(e eVar, String str, String str2, AdTrackType adTrackType, long j) {
        this.e = eVar;
        this.f1846a = str;
        this.f1847b = str2;
        this.f1848c = adTrackType;
        this.f1849d = j;
    }

    public void run() {
        Material a2;
        try {
            if (this.e.f1854b == null) {
                this.e.a();
            }
            AdInfo adInfo = (AdInfo) this.e.f1855c.get(this.f1846a);
            if (adInfo != null && (a2 = l.a(adInfo)) != null) {
                this.e.f1854b.a(Build.IS_INTERNATIONAL_BUILD ? "com.miui.securitycenter_globaladevent" : this.f1847b, this.f1848c, adInfo.getTagId(), adInfo.getId(), a2, this.f1849d);
            }
        } catch (Exception e2) {
            Log.d("LoadResource", "doTrack failed", e2);
        }
    }
}
