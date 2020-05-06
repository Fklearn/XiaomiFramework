package com.miui.securityscan.scanner;

import android.content.pm.PackageInfo;
import b.b.c.j.x;
import com.miui.securityscan.b.d;
import com.miui.securityscan.scanner.C0564k;
import com.miui.securityscan.scanner.O;
import java.util.List;
import miui.util.Log;

class E extends C0564k.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7828a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ O.e f7829b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ d f7830c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ O f7831d;

    E(O o, boolean z, O.e eVar, d dVar) {
        this.f7831d = o;
        this.f7828a = z;
        this.f7829b = eVar;
        this.f7830c = dVar;
    }

    public void a(List<com.miui.securitycenter.memory.d> list) {
        Log.d("SecurityManager", "startScanMemoryItem =============> onFinishScan");
        this.f7831d.l.post(new D(this, list));
    }

    public void b() {
        Log.d("SecurityManager", "startScanMemoryItem -------------> onStartScan");
        if (this.f7828a) {
            try {
                List<PackageInfo> a2 = this.f7831d.i.a();
                int i = 0;
                while (i < a2.size()) {
                    String charSequence = x.j(this.f7831d.f7851c, a2.get(i).packageName).toString();
                    i++;
                    this.f7831d.m.a(v.PREDICT_MEMORY, new C0558e(i, a2.size(), charSequence));
                }
            } catch (InterruptedException e) {
                O.e eVar = this.f7829b;
                if (eVar != null) {
                    eVar.a();
                }
                Log.e("SecurityManager", "startScanMemoryItem onStartScan() callback InterruptedException", e);
            }
        }
    }

    public boolean e() {
        return this.f7831d.f7850b;
    }
}
