package com.miui.maml.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.miui.maml.RenderThread;
import com.miui.maml.RendererCore;
import com.miui.maml.ResourceLoader;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.ScreenElementRootFactory;
import java.util.HashMap;

public class RendererCoreCache implements RendererCore.OnReleaseListener {
    private static final String LOG_TAG = "RendererCoreCache";
    public static final int TIME_DAY = 86400000;
    public static final int TIME_HOUR = 3600000;
    public static final int TIME_MIN = 60000;
    private HashMap<Object, RendererCoreInfo> mCaches;
    private Handler mHandler;

    protected class CheckCacheRunnable implements Runnable {
        private Object mKey;

        public CheckCacheRunnable(Object obj) {
            this.mKey = obj;
        }

        public void run() {
            RendererCoreCache.this.checkCache(this.mKey);
        }
    }

    public interface OnCreateRootCallback {
        void onCreateRoot(ScreenElementRoot screenElementRoot);
    }

    public static class RendererCoreInfo {
        public long accessTime = Long.MAX_VALUE;
        public long cacheTime;
        public CheckCacheRunnable checkCache;
        public RendererCore r;

        public RendererCoreInfo(RendererCore rendererCore) {
            this.r = rendererCore;
        }
    }

    public RendererCoreCache() {
        this.mCaches = new HashMap<>();
        this.mHandler = new Handler();
    }

    public RendererCoreCache(Handler handler) {
        this.mCaches = new HashMap<>();
        this.mHandler = handler;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a8, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void checkCache(java.lang.Object r8) {
        /*
            r7 = this;
            monitor-enter(r7)
            java.lang.String r0 = "RendererCoreCache"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a9 }
            r1.<init>()     // Catch:{ all -> 0x00a9 }
            java.lang.String r2 = "checkCache: "
            r1.append(r2)     // Catch:{ all -> 0x00a9 }
            r1.append(r8)     // Catch:{ all -> 0x00a9 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00a9 }
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x00a9 }
            java.util.HashMap<java.lang.Object, com.miui.maml.util.RendererCoreCache$RendererCoreInfo> r0 = r7.mCaches     // Catch:{ all -> 0x00a9 }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ all -> 0x00a9 }
            com.miui.maml.util.RendererCoreCache$RendererCoreInfo r0 = (com.miui.maml.util.RendererCoreCache.RendererCoreInfo) r0     // Catch:{ all -> 0x00a9 }
            if (r0 != 0) goto L_0x0039
            java.lang.String r0 = "RendererCoreCache"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a9 }
            r1.<init>()     // Catch:{ all -> 0x00a9 }
            java.lang.String r2 = "checkCache: the key does not exist, "
            r1.append(r2)     // Catch:{ all -> 0x00a9 }
            r1.append(r8)     // Catch:{ all -> 0x00a9 }
            java.lang.String r8 = r1.toString()     // Catch:{ all -> 0x00a9 }
            android.util.Log.d(r0, r8)     // Catch:{ all -> 0x00a9 }
            monitor-exit(r7)
            return
        L_0x0039:
            long r1 = r0.accessTime     // Catch:{ all -> 0x00a9 }
            r3 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 != 0) goto L_0x0046
            monitor-exit(r7)
            return
        L_0x0046:
            long r1 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00a9 }
            long r3 = r0.accessTime     // Catch:{ all -> 0x00a9 }
            long r1 = r1 - r3
            long r3 = r0.cacheTime     // Catch:{ all -> 0x00a9 }
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x006f
            java.util.HashMap<java.lang.Object, com.miui.maml.util.RendererCoreCache$RendererCoreInfo> r0 = r7.mCaches     // Catch:{ all -> 0x00a9 }
            r0.remove(r8)     // Catch:{ all -> 0x00a9 }
            java.lang.String r0 = "RendererCoreCache"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a9 }
            r1.<init>()     // Catch:{ all -> 0x00a9 }
            java.lang.String r2 = "checkCache removed: "
            r1.append(r2)     // Catch:{ all -> 0x00a9 }
            r1.append(r8)     // Catch:{ all -> 0x00a9 }
            java.lang.String r8 = r1.toString()     // Catch:{ all -> 0x00a9 }
            android.util.Log.d(r0, r8)     // Catch:{ all -> 0x00a9 }
            goto L_0x00a7
        L_0x006f:
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x007c
            long r1 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00a9 }
            r0.accessTime = r1     // Catch:{ all -> 0x00a9 }
            r1 = r3
        L_0x007c:
            android.os.Handler r3 = r7.mHandler     // Catch:{ all -> 0x00a9 }
            com.miui.maml.util.RendererCoreCache$CheckCacheRunnable r4 = r0.checkCache     // Catch:{ all -> 0x00a9 }
            long r5 = r0.cacheTime     // Catch:{ all -> 0x00a9 }
            long r5 = r5 - r1
            r3.postDelayed(r4, r5)     // Catch:{ all -> 0x00a9 }
            java.lang.String r3 = "RendererCoreCache"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a9 }
            r4.<init>()     // Catch:{ all -> 0x00a9 }
            java.lang.String r5 = "checkCache resheduled: "
            r4.append(r5)     // Catch:{ all -> 0x00a9 }
            r4.append(r8)     // Catch:{ all -> 0x00a9 }
            java.lang.String r8 = " after "
            r4.append(r8)     // Catch:{ all -> 0x00a9 }
            long r5 = r0.cacheTime     // Catch:{ all -> 0x00a9 }
            long r5 = r5 - r1
            r4.append(r5)     // Catch:{ all -> 0x00a9 }
            java.lang.String r8 = r4.toString()     // Catch:{ all -> 0x00a9 }
            android.util.Log.d(r3, r8)     // Catch:{ all -> 0x00a9 }
        L_0x00a7:
            monitor-exit(r7)
            return
        L_0x00a9:
            r8 = move-exception
            monitor-exit(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.RendererCoreCache.checkCache(java.lang.Object):void");
    }

    private RendererCoreInfo get(Object obj, Context context, long j, ResourceLoader resourceLoader, String str, OnCreateRootCallback onCreateRootCallback) {
        RendererCoreInfo rendererCoreInfo = get(obj, j);
        if (rendererCoreInfo != null) {
            return rendererCoreInfo;
        }
        ScreenElementRoot create = resourceLoader != null ? ScreenElementRootFactory.create(new ScreenElementRootFactory.Parameter(context, resourceLoader)) : ScreenElementRootFactory.create(new ScreenElementRootFactory.Parameter(context, str));
        RendererCore rendererCore = null;
        if (create == null) {
            Log.e(LOG_TAG, "fail to get RendererCoreInfo" + obj);
            return null;
        }
        if (onCreateRootCallback != null) {
            onCreateRootCallback.onCreateRoot(create);
        }
        create.setDefaultFramerate(0.0f);
        if (create.load()) {
            rendererCore = new RendererCore(create, RenderThread.globalThread(true));
        }
        RendererCoreInfo rendererCoreInfo2 = new RendererCoreInfo(rendererCore);
        rendererCoreInfo2.accessTime = Long.MAX_VALUE;
        rendererCoreInfo2.cacheTime = j;
        if (rendererCore != null) {
            rendererCore.setOnReleaseListener(this);
            rendererCoreInfo2.checkCache = new CheckCacheRunnable(obj);
        }
        this.mCaches.put(obj, rendererCoreInfo2);
        return rendererCoreInfo2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0045, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean OnRendererCoreReleased(com.miui.maml.RendererCore r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            java.lang.String r0 = "RendererCoreCache"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0048 }
            r1.<init>()     // Catch:{ all -> 0x0048 }
            java.lang.String r2 = "OnRendererCoreReleased: "
            r1.append(r2)     // Catch:{ all -> 0x0048 }
            r1.append(r6)     // Catch:{ all -> 0x0048 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0048 }
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x0048 }
            java.util.HashMap<java.lang.Object, com.miui.maml.util.RendererCoreCache$RendererCoreInfo> r0 = r5.mCaches     // Catch:{ all -> 0x0048 }
            java.util.Set r0 = r0.keySet()     // Catch:{ all -> 0x0048 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0048 }
        L_0x0021:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x0048 }
            r2 = 0
            if (r1 == 0) goto L_0x0046
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x0048 }
            java.util.HashMap<java.lang.Object, com.miui.maml.util.RendererCoreCache$RendererCoreInfo> r3 = r5.mCaches     // Catch:{ all -> 0x0048 }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x0048 }
            com.miui.maml.util.RendererCoreCache$RendererCoreInfo r3 = (com.miui.maml.util.RendererCoreCache.RendererCoreInfo) r3     // Catch:{ all -> 0x0048 }
            com.miui.maml.RendererCore r4 = r3.r     // Catch:{ all -> 0x0048 }
            if (r4 != r6) goto L_0x0021
            r5.release(r1)     // Catch:{ all -> 0x0048 }
            long r0 = r3.cacheTime     // Catch:{ all -> 0x0048 }
            r3 = 0
            int r6 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r6 != 0) goto L_0x0044
            r2 = 1
        L_0x0044:
            monitor-exit(r5)
            return r2
        L_0x0046:
            monitor-exit(r5)
            return r2
        L_0x0048:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.RendererCoreCache.OnRendererCoreReleased(com.miui.maml.RendererCore):boolean");
    }

    public synchronized void clear() {
        this.mCaches.clear();
    }

    public synchronized RendererCoreInfo get(Object obj, long j) {
        RendererCoreInfo rendererCoreInfo = this.mCaches.get(obj);
        if (rendererCoreInfo == null) {
            return null;
        }
        rendererCoreInfo.accessTime = Long.MAX_VALUE;
        rendererCoreInfo.cacheTime = j;
        this.mHandler.removeCallbacks(rendererCoreInfo.checkCache);
        return rendererCoreInfo;
    }

    public synchronized RendererCoreInfo get(Object obj, Context context, long j, ResourceLoader resourceLoader, OnCreateRootCallback onCreateRootCallback) {
        return get(obj, context, j, resourceLoader, (String) null, onCreateRootCallback);
    }

    public synchronized RendererCoreInfo get(Object obj, Context context, long j, String str, OnCreateRootCallback onCreateRootCallback) {
        return get(obj, context, j, (ResourceLoader) null, str, onCreateRootCallback);
    }

    public synchronized void release(Object obj) {
        Log.d(LOG_TAG, "release: " + obj);
        RendererCoreInfo rendererCoreInfo = this.mCaches.get(obj);
        if (rendererCoreInfo != null) {
            rendererCoreInfo.accessTime = System.currentTimeMillis();
            if (rendererCoreInfo.cacheTime == 0) {
                this.mCaches.remove(obj);
                Log.d(LOG_TAG, "removed: " + obj);
            } else {
                Log.d(LOG_TAG, "scheduled release: " + obj + " after " + rendererCoreInfo.cacheTime);
                this.mHandler.removeCallbacks(rendererCoreInfo.checkCache);
                this.mHandler.postDelayed(rendererCoreInfo.checkCache, rendererCoreInfo.cacheTime);
            }
        }
    }
}
