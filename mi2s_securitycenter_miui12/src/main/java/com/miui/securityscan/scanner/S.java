package com.miui.securityscan.scanner;

import android.util.Log;
import com.miui.securityscan.b.f;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.scanner.O;
import java.util.List;

class S implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f7865a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ O.c f7866b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ f f7867c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ U f7868d;

    S(U u, List list, O.c cVar, f fVar) {
        this.f7868d = u;
        this.f7865a = list;
        this.f7866b = cVar;
        this.f7867c = fVar;
    }

    public void run() {
        Log.d("SystemCheckManager", "SystemCheckManager startOptimize run()");
        for (GroupModel groupModel : this.f7865a) {
            O.c cVar = this.f7866b;
            if (cVar != null) {
                cVar.a(groupModel);
            }
        }
        this.f7867c.a();
    }
}
