package b.a;

import android.os.AsyncTask;
import b.a.c;

class b extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c.a f1302a;

    b(c.a aVar) {
        this.f1302a = aVar;
    }

    /* access modifiers changed from: protected */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    /* renamed from: a */
    public java.lang.Void doInBackground(java.lang.Void... r3) {
        /*
            r2 = this;
            b.a.c$a r3 = r2.f1302a     // Catch:{ RemoteException -> 0x000b }
            b.a.c r3 = b.a.c.this     // Catch:{ RemoteException -> 0x000b }
            b.a.c$b r3 = r3.f1306d     // Catch:{ RemoteException -> 0x000b }
            r3.run()     // Catch:{ RemoteException -> 0x000b }
        L_0x000b:
            b.a.c$a r3 = r2.f1302a     // Catch:{ RuntimeException -> 0x001f }
            b.a.c r3 = b.a.c.this     // Catch:{ RuntimeException -> 0x001f }
            android.content.Context r3 = r3.f1304b     // Catch:{ RuntimeException -> 0x001f }
            b.a.c$a r0 = r2.f1302a     // Catch:{ RuntimeException -> 0x001f }
            b.a.c r0 = b.a.c.this     // Catch:{ RuntimeException -> 0x001f }
            android.content.ServiceConnection r0 = r0.f     // Catch:{ RuntimeException -> 0x001f }
            r3.unbindService(r0)     // Catch:{ RuntimeException -> 0x001f }
            goto L_0x002b
        L_0x001f:
            r3 = move-exception
            b.a.c$a r0 = r2.f1302a
            b.a.c r0 = b.a.c.this
            java.lang.String r0 = r0.f1303a
            java.lang.String r1 = "RuntimeException when trying to unbind from service"
            android.util.Log.e(r0, r1, r3)
        L_0x002b:
            b.a.c$a r3 = r2.f1302a
            b.a.c r3 = b.a.c.this
            r0 = 1
            boolean unused = r3.j = r0
            b.a.c$a r3 = r2.f1302a
            b.a.c r3 = b.a.c.this
            android.content.ServiceConnection r3 = r3.f
            monitor-enter(r3)
            b.a.c$a r0 = r2.f1302a     // Catch:{ all -> 0x004a }
            b.a.c r0 = b.a.c.this     // Catch:{ all -> 0x004a }
            android.content.ServiceConnection r0 = r0.f     // Catch:{ all -> 0x004a }
            r0.notify()     // Catch:{ all -> 0x004a }
            monitor-exit(r3)     // Catch:{ all -> 0x004a }
            r3 = 0
            return r3
        L_0x004a:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x004a }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.a.b.doInBackground(java.lang.Void[]):java.lang.Void");
    }
}
