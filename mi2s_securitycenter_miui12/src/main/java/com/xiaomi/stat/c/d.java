package com.xiaomi.stat.c;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.xiaomi.stat.b.e;
import java.util.Map;

final class d implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String[] f8475a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f8476b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Map f8477c;

    d(String[] strArr, String str, Map map) {
        this.f8475a = strArr;
        this.f8476b = str;
        this.f8477c = map;
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x000b */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindingDied(android.content.ComponentName r2) {
        /*
            r1 = this;
            java.lang.Class<com.xiaomi.stat.c.i> r2 = com.xiaomi.stat.c.i.class
            monitor-enter(r2)
            java.lang.Class<com.xiaomi.stat.c.i> r0 = com.xiaomi.stat.c.i.class
            r0.notify()     // Catch:{ Exception -> 0x000b }
            goto L_0x000b
        L_0x0009:
            r0 = move-exception
            goto L_0x000d
        L_0x000b:
            monitor-exit(r2)     // Catch:{ all -> 0x0009 }
            return
        L_0x000d:
            monitor-exit(r2)     // Catch:{ all -> 0x0009 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.c.d.onBindingDied(android.content.ComponentName):void");
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        e.a().execute(new e(this, iBinder));
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0021 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onServiceDisconnected(android.content.ComponentName r3) {
        /*
            r2 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onServiceDisconnected "
            r0.append(r1)
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            java.lang.String r0 = "UploadMode"
            com.xiaomi.stat.d.k.b(r0, r3)
            java.lang.Class<com.xiaomi.stat.c.i> r3 = com.xiaomi.stat.c.i.class
            monitor-enter(r3)
            java.lang.Class<com.xiaomi.stat.c.i> r0 = com.xiaomi.stat.c.i.class
            r0.notify()     // Catch:{ Exception -> 0x0021 }
            goto L_0x0021
        L_0x001f:
            r0 = move-exception
            goto L_0x0023
        L_0x0021:
            monitor-exit(r3)     // Catch:{ all -> 0x001f }
            return
        L_0x0023:
            monitor-exit(r3)     // Catch:{ all -> 0x001f }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.c.d.onServiceDisconnected(android.content.ComponentName):void");
    }
}
