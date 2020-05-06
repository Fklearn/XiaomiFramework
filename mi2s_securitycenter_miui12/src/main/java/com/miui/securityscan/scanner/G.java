package com.miui.securityscan.scanner;

import b.b.c.j.s;
import b.b.c.j.x;
import com.miui.securityscan.b.n;
import com.miui.securityscan.scanner.CacheCheckManager;
import com.miui.securityscan.scanner.ScoreManager;
import java.util.HashMap;
import java.util.Map;
import miui.util.Log;

class G extends CacheCheckManager.CacheScanCallbackAdapter {

    /* renamed from: a  reason: collision with root package name */
    Map<String, ScoreManager.ResultModel> f7833a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ O f7834b;

    G(O o) {
        this.f7834b = o;
    }

    public boolean a(String str, String str2, String str3, long j, boolean z) {
        if (z) {
            ScoreManager.ResultModel resultModel = this.f7833a.get(str3);
            if (resultModel == null) {
                ScoreManager.ResultModel resultModel2 = new ScoreManager.ResultModel();
                resultModel2.setPackageName(str3);
                resultModel2.setChecked(true);
                resultModel2.setMemorySize(j);
                resultModel2.setAppName(x.j(this.f7834b.f7851c, str3).toString());
                resultModel2.addInfo(str2);
                this.f7833a.put(str3, resultModel2);
            } else {
                resultModel.setMemorySize(resultModel.getMemorySize() + j);
                resultModel.addInfo(str2);
            }
        }
        s.a("cacheType : " + str + ", dirPath : " + str2 + ", pkgName : " + str3 + ", size :" + j + ", adviseDel : " + z);
        return this.f7834b.f7850b;
    }

    public void b() {
        Log.d("SecurityManager", "startScanCacheItem -------------> onStartScan");
    }

    public void c() {
        Log.d("SecurityManager", "startScanCacheItem =============> onFinishScan");
        this.f7834b.l.post(new F(this));
        this.f7834b.a((n) null);
    }
}
