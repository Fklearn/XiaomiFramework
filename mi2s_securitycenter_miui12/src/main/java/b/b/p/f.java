package b.b.p;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import b.b.n.l;
import com.miui.msa.util.MsaUtils;
import com.miui.systemAdSolution.changeSkin.IChangeSkinService;
import com.miui.systemAdSolution.common.AdInfo;
import com.miui.systemAdSolution.common.Material;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static final ExecutorService f1899a = Executors.newSingleThreadExecutor();

    /* renamed from: b  reason: collision with root package name */
    private static final Set<Long> f1900b = new ConcurrentSkipListSet();

    /* renamed from: c  reason: collision with root package name */
    private static volatile f f1901c = null;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public static long f1902d;
    /* access modifiers changed from: private */
    public Context e;
    /* access modifiers changed from: private */
    public IChangeSkinService f = null;
    /* access modifiers changed from: private */
    public Object g = new Object();
    private SharedPreferences h;
    /* access modifiers changed from: private */
    public Map<String, c> i = new ConcurrentHashMap();
    private ServiceConnection j = new c(this);

    abstract class a<P> implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private P f1903a;

        protected a(P p) {
            this.f1903a = p;
        }

        /* access modifiers changed from: package-private */
        public abstract String a(IChangeSkinService iChangeSkinService, P p);

        /* access modifiers changed from: package-private */
        public abstract void a(String str);

        public void run() {
            IChangeSkinService b2;
            P p;
            String str = null;
            try {
                synchronized (f.this.g) {
                    f.this.e();
                    if (!f.this.d()) {
                        f.this.g.wait(1000);
                        if (f.this.d()) {
                            b2 = f.this.f;
                            p = this.f1903a;
                        }
                    } else {
                        b2 = f.this.f;
                        p = this.f1903a;
                    }
                    str = a(b2, p);
                }
                new Handler(f.this.e.getMainLooper()).post(new e(this, str));
            } catch (Exception e) {
                Log.e("RemoteUnifiedAdService", "colud not invoke the remote method.", e);
            }
        }
    }

    private class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private String f1905a;

        private b(String str) {
            this.f1905a = str;
        }

        /* synthetic */ b(f fVar, String str, c cVar) {
            this(str);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
            r0 = b.b.p.f.a(r1);
            r2 = (b.b.p.f.c) b.b.p.f.f(r6.f1906b).get(r6.f1905a);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0085, code lost:
            if (r2 == null) goto L_0x008b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0087, code lost:
            r4 = r2.a(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x008b, code lost:
            if (r4 == false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x008d, code lost:
            b.b.n.l.a("skin_description", "skinAdDescription_" + r6.f1905a + ".txt", r1, b.b.p.f.e(r6.f1906b));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r6 = this;
                java.lang.String r0 = "RemoteUnifiedAdService"
                java.lang.String r1 = "start getting skin info from ad sdk."
                android.util.Log.d(r0, r1)
                java.lang.String r0 = r6.f1905a     // Catch:{ Exception -> 0x00b4 }
                boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x00b4 }
                if (r0 == 0) goto L_0x0017
                java.lang.String r0 = "RemoteUnifiedAdService"
                java.lang.String r1 = "tag id is null."
                android.util.Log.e(r0, r1)     // Catch:{ Exception -> 0x00b4 }
                return
            L_0x0017:
                b.b.p.f r0 = b.b.p.f.this     // Catch:{ Exception -> 0x00b4 }
                java.lang.Object r0 = r0.g     // Catch:{ Exception -> 0x00b4 }
                monitor-enter(r0)     // Catch:{ Exception -> 0x00b4 }
                b.b.p.f r1 = b.b.p.f.this     // Catch:{ all -> 0x00b1 }
                r1.e()     // Catch:{ all -> 0x00b1 }
                b.b.p.f r1 = b.b.p.f.this     // Catch:{ all -> 0x00b1 }
                boolean r1 = r1.d()     // Catch:{ all -> 0x00b1 }
                if (r1 != 0) goto L_0x0036
                b.b.p.f r1 = b.b.p.f.this     // Catch:{ all -> 0x00b1 }
                java.lang.Object r1 = r1.g     // Catch:{ all -> 0x00b1 }
                r2 = 1000(0x3e8, double:4.94E-321)
                r1.wait(r2)     // Catch:{ all -> 0x00b1 }
            L_0x0036:
                b.b.p.f r1 = b.b.p.f.this     // Catch:{ all -> 0x00b1 }
                boolean r1 = r1.d()     // Catch:{ all -> 0x00b1 }
                if (r1 != 0) goto L_0x0040
                monitor-exit(r0)     // Catch:{ all -> 0x00b1 }
                return
            L_0x0040:
                b.b.p.f r1 = b.b.p.f.this     // Catch:{ all -> 0x00b1 }
                com.miui.systemAdSolution.changeSkin.IChangeSkinService r1 = r1.f     // Catch:{ all -> 0x00b1 }
                java.lang.String r2 = r6.f1905a     // Catch:{ all -> 0x00b1 }
                b.b.p.f r3 = b.b.p.f.this     // Catch:{ all -> 0x00b1 }
                android.content.Context r3 = r3.e     // Catch:{ all -> 0x00b1 }
                java.lang.String r3 = r3.getPackageName()     // Catch:{ all -> 0x00b1 }
                java.lang.String r1 = r1.getSkinInfoByTagId(r2, r3)     // Catch:{ all -> 0x00b1 }
                java.lang.String r2 = "RemoteUnifiedAdService"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b1 }
                r3.<init>()     // Catch:{ all -> 0x00b1 }
                java.lang.String r4 = "Result is null:"
                r3.append(r4)     // Catch:{ all -> 0x00b1 }
                r4 = 1
                if (r1 != 0) goto L_0x0067
                r5 = r4
                goto L_0x0068
            L_0x0067:
                r5 = 0
            L_0x0068:
                r3.append(r5)     // Catch:{ all -> 0x00b1 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00b1 }
                android.util.Log.d(r2, r3)     // Catch:{ all -> 0x00b1 }
                monitor-exit(r0)     // Catch:{ all -> 0x00b1 }
                java.util.List r0 = b.b.p.f.c((java.lang.String) r1)     // Catch:{ Exception -> 0x00b4 }
                b.b.p.f r2 = b.b.p.f.this     // Catch:{ Exception -> 0x00b4 }
                java.util.Map r2 = r2.i     // Catch:{ Exception -> 0x00b4 }
                java.lang.String r3 = r6.f1905a     // Catch:{ Exception -> 0x00b4 }
                java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x00b4 }
                b.b.p.f$c r2 = (b.b.p.f.c) r2     // Catch:{ Exception -> 0x00b4 }
                if (r2 == 0) goto L_0x008b
                boolean r4 = r2.a(r0)     // Catch:{ Exception -> 0x00b4 }
            L_0x008b:
                if (r4 == 0) goto L_0x00d3
                java.lang.String r0 = "skin_description"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b4 }
                r2.<init>()     // Catch:{ Exception -> 0x00b4 }
                java.lang.String r3 = "skinAdDescription_"
                r2.append(r3)     // Catch:{ Exception -> 0x00b4 }
                java.lang.String r3 = r6.f1905a     // Catch:{ Exception -> 0x00b4 }
                r2.append(r3)     // Catch:{ Exception -> 0x00b4 }
                java.lang.String r3 = ".txt"
                r2.append(r3)     // Catch:{ Exception -> 0x00b4 }
                java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00b4 }
                b.b.p.f r3 = b.b.p.f.this     // Catch:{ Exception -> 0x00b4 }
                android.content.Context r3 = r3.e     // Catch:{ Exception -> 0x00b4 }
                b.b.n.l.a((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r1, (android.content.Context) r3)     // Catch:{ Exception -> 0x00b4 }
                goto L_0x00d3
            L_0x00b1:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00b1 }
                throw r1     // Catch:{ Exception -> 0x00b4 }
            L_0x00b4:
                r0 = move-exception
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "could not do track. thread is "
                r1.append(r2)
                java.lang.Thread r2 = java.lang.Thread.currentThread()
                java.lang.String r2 = r2.getName()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                java.lang.String r2 = "RemoteUnifiedAdService"
                android.util.Log.e(r2, r1, r0)
            L_0x00d3:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: b.b.p.f.b.run():void");
        }
    }

    public interface c {
        boolean a(List<AdInfo> list);
    }

    private f(Context context) {
        if (context != null) {
            this.e = context.getApplicationContext();
            if (this.e == null) {
                this.e = context;
            }
            this.h = this.e.getSharedPreferences("unified_ad_list", 0);
            c();
            return;
        }
        throw new IllegalArgumentException("the context is null");
    }

    public static synchronized f a(Context context) {
        f fVar;
        synchronized (f.class) {
            if (f1901c == null) {
                f1901c = new f(context);
            }
            fVar = f1901c;
        }
        return fVar;
    }

    public static AdInfo a(Context context, String str) {
        AdInfo adInfo;
        Material material;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String a2 = l.a("skin_description", "skinAdDescription_" + str + ".txt", context);
        if (TextUtils.isEmpty(a2)) {
            Log.i("RemoteUnifiedAdService", "there is no unified ad now.");
            return null;
        }
        try {
            List<AdInfo> c2 = c(a2);
            if (c2 != null) {
                if (!c2.isEmpty()) {
                    Iterator<AdInfo> it = c2.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            adInfo = null;
                            break;
                        }
                        adInfo = it.next();
                        if (adInfo != null) {
                            if (!adInfo.isInvalid()) {
                                if (!adInfo.isInvalid()) {
                                    break;
                                }
                            }
                        }
                    }
                    if (adInfo == null) {
                        Log.i("RemoteUnifiedAdService", "could not get skin info by ad id. no fitted ad info. maybe all ads are invalid.");
                        return null;
                    }
                    List<Material> materials = adInfo.getMaterials();
                    if (materials != null && !materials.isEmpty() && (material = materials.get(0)) != null && material.isMaterialViewLogLevel()) {
                        f1900b.add(new Long(adInfo.getId()));
                    }
                    return adInfo;
                }
            }
            Log.i("RemoteUnifiedAdService", "colud not caver the cached str to the adInfo list.");
            return null;
        } catch (Exception e2) {
            Log.e("RemoteUnifiedAdService", "could not get the ad from local.", e2);
            return null;
        }
    }

    private static Intent b(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.ad.CHANGE_SKIN");
        intent.setPackage(MsaUtils.getMsaPackageName(context));
        return intent;
    }

    /* access modifiers changed from: private */
    public static List<AdInfo> c(String str) {
        AdInfo deserialize;
        ArrayList arrayList = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        JSONArray jSONArray = new JSONArray(str);
        int length = jSONArray.length();
        if (length > 0) {
            arrayList = new ArrayList();
            for (int i2 = 0; i2 < length; i2++) {
                String optString = jSONArray.optString(i2);
                if (!TextUtils.isEmpty(optString) && (deserialize = AdInfo.deserialize(optString)) != null) {
                    arrayList.add(deserialize);
                }
            }
        }
        return arrayList;
    }

    private void c() {
        if (c(this.e)) {
            try {
                this.e.bindService(b(this.e), this.j, 1);
                f1902d = System.currentTimeMillis();
                Log.d("RemoteUnifiedAdService", "start bind service " + f1902d);
            } catch (Exception e2) {
                Log.e("RemoteUnifiedAdService", "could not bind the service.", e2);
            }
        }
    }

    private static boolean c(Context context) {
        List<ResolveInfo> queryIntentServices;
        if (context != null) {
            try {
                if (!(context.getPackageManager() == null || (queryIntentServices = context.getPackageManager().queryIntentServices(b(context), 0)) == null || queryIntentServices.size() <= 0)) {
                    Log.d("RemoteUnifiedAdService", "find the ad service in systemAdSolution.");
                    return true;
                }
            } catch (Exception e2) {
                Log.e("RemoteUnifiedAdService", "some exceptions occur when judge if there is the system ad app.", e2);
            }
        }
        Log.e("RemoteUnifiedAdService", "there is no a systemAdSolution app.");
        return false;
    }

    /* access modifiers changed from: private */
    public boolean d() {
        return c(this.e) && this.f != null;
    }

    /* access modifiers changed from: private */
    public void e() {
        if (this.f == null) {
            c();
        }
    }

    /* access modifiers changed from: package-private */
    public <P> void a(a<P> aVar) {
        if (aVar != null) {
            try {
                f1899a.execute(aVar);
            } catch (Exception e2) {
                Log.e("RemoteUnifiedAdService", "exec some command failed.", e2);
            }
        }
    }

    public void a(String str, c cVar) {
        if (!TextUtils.isEmpty(str) && cVar != null) {
            this.i.put(str, cVar);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ba, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00cc, code lost:
        throw new java.util.concurrent.TimeoutException("do track is time out(more than 1000 second.)");
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[ExcHandler: TimeoutException (unused java.util.concurrent.TimeoutException), SYNTHETIC, Splitter:B:18:0x0044] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(java.lang.String r17, com.miui.systemAdSolution.common.AdTrackType r18, java.lang.String r19, long r20, com.miui.systemAdSolution.common.Material r22, long r23) {
        /*
            r16 = this;
            r12 = r16
            r13 = r20
            android.content.Context r0 = r12.e
            r15 = 0
            java.lang.String r10 = "RemoteUnifiedAdService"
            if (r0 == 0) goto L_0x00cd
            boolean r0 = android.text.TextUtils.isEmpty(r17)
            if (r0 != 0) goto L_0x00cd
            boolean r0 = android.text.TextUtils.isEmpty(r19)
            if (r0 != 0) goto L_0x00cd
            if (r18 == 0) goto L_0x00cd
            r0 = 0
            int r0 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x00cd
            if (r22 != 0) goto L_0x0023
            goto L_0x00cd
        L_0x0023:
            com.miui.systemAdSolution.common.AdTrackType$Type r0 = r18.getValue()
            com.miui.systemAdSolution.common.AdTrackType$Type r1 = com.miui.systemAdSolution.common.AdTrackType.Type.TRACK_VIEW
            if (r0 != r1) goto L_0x0044
            boolean r0 = r22.isMaterialViewLogLevel()
            if (r0 == 0) goto L_0x0044
            java.util.Set<java.lang.Long> r0 = f1900b
            java.lang.Long r1 = new java.lang.Long
            r1.<init>(r13)
            boolean r0 = r0.remove(r1)
            if (r0 != 0) goto L_0x0044
            java.lang.String r0 = "could not track. becasue the method getAdInfoFromLocal is not called before this tracking."
            android.util.Log.i(r10, r0)
            return r15
        L_0x0044:
            android.content.Context r0 = r12.e     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00bc }
            java.lang.String r3 = r0.getPackageName()     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00bc }
            java.util.concurrent.FutureTask r0 = new java.util.concurrent.FutureTask     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00bc }
            b.b.p.d r11 = new b.b.p.d     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00bc }
            r1 = r11
            r2 = r16
            r4 = r17
            r5 = r18
            r6 = r19
            r7 = r20
            r9 = r22
            r12 = r10
            r15 = r11
            r10 = r23
            r1.<init>(r2, r3, r4, r5, r6, r7, r9, r10)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            r0.<init>(r15)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.util.concurrent.ExecutorService r1 = f1899a     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            r1.execute(r0)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            r1 = 1000(0x3e8, double:4.94E-321)
            java.util.concurrent.TimeUnit r3 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.lang.Object r0 = r0.get(r1, r3)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            r1.<init>()     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.lang.String r2 = "do track! the ad info id is "
            r1.append(r2)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            r1.append(r13)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.lang.String r2 = ", the material id is "
            r1.append(r2)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            long r2 = r22.getId()     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            r1.append(r2)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.lang.String r2 = ", the resource id is "
            r1.append(r2)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            r2 = r23
            r1.append(r2)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.lang.String r2 = ", the level is "
            r1.append(r2)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            boolean r2 = r22.isMaterialViewLogLevel()     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            if (r2 == 0) goto L_0x00a5
            java.lang.String r2 = "material leve"
            goto L_0x00a7
        L_0x00a5:
            java.lang.String r2 = "resource level."
        L_0x00a7:
            r1.append(r2)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            java.lang.String r1 = r1.toString()     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            android.util.Log.i(r12, r1)     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
            if (r0 != 0) goto L_0x00b5
            r15 = 0
            goto L_0x00b9
        L_0x00b5:
            boolean r15 = r0.booleanValue()     // Catch:{ TimeoutException -> 0x00c5, Exception -> 0x00ba }
        L_0x00b9:
            return r15
        L_0x00ba:
            r0 = move-exception
            goto L_0x00be
        L_0x00bc:
            r0 = move-exception
            r12 = r10
        L_0x00be:
            java.lang.String r1 = "colud not do track."
            android.util.Log.e(r12, r1, r0)
        L_0x00c3:
            r1 = 0
            return r1
        L_0x00c5:
            java.util.concurrent.TimeoutException r0 = new java.util.concurrent.TimeoutException
            java.lang.String r1 = "do track is time out(more than 1000 second.)"
            r0.<init>(r1)
            throw r0
        L_0x00cd:
            r12 = r10
            java.lang.String r0 = "the params are invalid."
            android.util.Log.e(r12, r0)
            goto L_0x00c3
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.p.f.a(java.lang.String, com.miui.systemAdSolution.common.AdTrackType, java.lang.String, long, com.miui.systemAdSolution.common.Material, long):boolean");
    }

    /* access modifiers changed from: package-private */
    public void b() {
        if (c(this.e)) {
            this.e.unbindService(this.j);
        }
    }

    public void b(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                f1899a.execute(new b(this, str, (c) null));
            } catch (Exception e2) {
                Log.e("RemoteUnifiedAdService", "colud not get skin info. becuase some exceptions occur.", e2);
            }
        }
    }
}
