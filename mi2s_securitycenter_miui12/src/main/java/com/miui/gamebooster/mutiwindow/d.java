package com.miui.gamebooster.mutiwindow;

import android.content.Context;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import b.b.c.j.B;
import com.miui.gamebooster.mutiwindow.l;
import java.util.ArrayList;
import java.util.List;
import miui.process.ForegroundInfo;

public class d implements l.a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public boolean f4626a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public boolean f4627b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public List<String> f4628c = new ArrayList();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public final Object f4629d = new Object();
    /* access modifiers changed from: private */
    public Context e;
    private volatile boolean f;
    /* access modifiers changed from: private */
    public volatile boolean g;
    private volatile boolean h;
    private Handler i;
    private ContentObserver j = new b(this, new Handler());

    private class a extends ContentObserver {
        public a(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            d dVar = d.this;
            boolean unused = dVar.f4626a = f.c(dVar.e);
            synchronized (d.this.f4629d) {
                if (!d.this.f4626a && d.this.f4627b && !d.this.g) {
                    d.this.b(false);
                }
            }
        }
    }

    public d(Context context, Handler handler) {
        this.e = context;
        this.i = handler;
        a();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0053, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(miui.process.ForegroundInfo r6) {
        /*
            r5 = this;
            java.lang.String r0 = r6.mForegroundPackageName
            java.lang.String r1 = "com.lbe.security.miui"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x000b
            return
        L_0x000b:
            java.lang.Object r0 = r5.f4629d
            monitor-enter(r0)
            boolean r1 = r5.f4626a     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0052
            java.util.List<java.lang.String> r1 = r5.f4628c     // Catch:{ all -> 0x0054 }
            int r1 = r1.size()     // Catch:{ all -> 0x0054 }
            r2 = 1
            r3 = 0
            if (r1 <= 0) goto L_0x0028
            java.util.List<java.lang.String> r1 = r5.f4628c     // Catch:{ all -> 0x0054 }
            java.lang.String r4 = r6.mForegroundPackageName     // Catch:{ all -> 0x0054 }
            boolean r1 = r1.contains(r4)     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0028
            r1 = r2
            goto L_0x0029
        L_0x0028:
            r1 = r3
        L_0x0029:
            r5.h = r1     // Catch:{ all -> 0x0054 }
            boolean r1 = r5.h     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0033
            r5.b((boolean) r2)     // Catch:{ all -> 0x0054 }
            goto L_0x0052
        L_0x0033:
            boolean r1 = r5.f4627b     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0052
            boolean r1 = r5.g     // Catch:{ all -> 0x0054 }
            if (r1 != 0) goto L_0x0052
            java.util.List<java.lang.String> r1 = com.miui.gamebooster.ui.QuickReplySettingsActivity.f4966a     // Catch:{ all -> 0x0054 }
            java.lang.String r2 = r6.mForegroundPackageName     // Catch:{ all -> 0x0054 }
            boolean r1 = r1.contains(r2)     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x004f
            java.lang.String r6 = r6.mForegroundPackageName     // Catch:{ all -> 0x0054 }
            boolean r6 = com.miui.gamebooster.m.C0383n.a(r6)     // Catch:{ all -> 0x0054 }
            if (r6 == 0) goto L_0x004f
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            return
        L_0x004f:
            r5.b((boolean) r3)     // Catch:{ all -> 0x0054 }
        L_0x0052:
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            return
        L_0x0054:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.mutiwindow.d.a(miui.process.ForegroundInfo):void");
    }

    /* access modifiers changed from: private */
    public void b(boolean z) {
        if (z) {
            Log.i("FreeformWindowHandler", "enter QuickReply mode");
            this.f4627b = true;
            f.e(this.e);
            return;
        }
        this.f4627b = false;
        Log.i("FreeformWindowHandler", "quit QuickReply mode");
        f.a(this.e);
        f.b();
    }

    private void c() {
        this.f4628c.clear();
        new c(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void a() {
        if (f.d()) {
            this.j = new a(this.i);
            this.f4626a = f.c(this.e);
            c();
            this.e.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("quick_reply_enable"), true, this.j);
            this.f = true;
        }
    }

    public void a(List<String> list) {
        synchronized (this.f4629d) {
            this.f4628c.clear();
            this.f4628c.addAll(list);
        }
    }

    public void a(boolean z) {
        synchronized (this.f4629d) {
            Log.i("FreeformWindowHandler", "setGameBoosterMode: open=" + z);
            this.g = z;
            if (!this.h || z) {
                b(z);
            }
        }
    }

    public void b() {
        if (this.f) {
            this.e.getContentResolver().unregisterContentObserver(this.j);
            this.f = false;
        }
    }

    public j getId() {
        return j.MULTI_WINDOW;
    }

    public boolean onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
        int c2 = B.c(foregroundInfo.mForegroundUid);
        int j2 = B.j();
        if ((c2 == 999 && j2 == 10) || (c2 != 999 && j2 != c2)) {
            return false;
        }
        a(foregroundInfo);
        return false;
    }
}
