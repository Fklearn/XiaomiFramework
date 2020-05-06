package com.miui.maml;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.MemoryFile;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import org.w3c.dom.Element;

public class ResourceManager {
    private static final int DEF_CACHE_SIZE = 268435456;
    private static final int DENSITY_HIGH_R = 240;
    private static final int DENSITY_XHIGH_R = 360;
    private static final int DENSITY_XXHIGH_R = 540;
    private static final int DENSITY_XXXHIGH = 640;
    private static final int DENSITY_XXXHIGH_R = 720;
    private static final String LOG_TAG = "ResourceManager";
    private static final int RESOURCE_FALLBACK_DENSITY = 480;
    private static final String RESOURCE_FALLBACK_EXTRA_FOLDER = "den480/";
    protected final LruCache<String, BitmapInfo> mBitmapsCache;
    private int mDefaultResourceDensity;
    private int mExtraResourceDensity;
    private String mExtraResourceFolder;
    /* access modifiers changed from: private */
    public final HashSet<String> mLoadingBitmaps = new HashSet<>();
    private final ResourceLoader mResourceLoader;
    private int mTargetDensity;
    protected final HashMap<String, WeakReference<BitmapInfo>> mWeakRefBitmapsCache;

    public interface AsyncLoadListener {
        void onLoadComplete(String str, BitmapInfo bitmapInfo);
    }

    public static class BitmapInfo {
        public final Bitmap mBitmap;
        public String mKey;
        public long mLastVisitTime;
        public boolean mLoading;
        public final NinePatch mNinePatch;
        public final Rect mPadding;
        public HashMap<String, WeakReference<BitmapInfo>> mWeakRefCache;

        public BitmapInfo() {
            this.mBitmap = null;
            this.mPadding = null;
            this.mNinePatch = null;
        }

        public BitmapInfo(Bitmap bitmap, Rect rect) {
            this.mBitmap = bitmap;
            this.mPadding = rect;
            if (bitmap == null || bitmap.getNinePatchChunk() == null) {
                this.mNinePatch = null;
            } else {
                this.mNinePatch = new NinePatch(bitmap, bitmap.getNinePatchChunk(), (String) null);
            }
            this.mLastVisitTime = System.currentTimeMillis();
        }

        /* access modifiers changed from: protected */
        public void finalize() {
            synchronized (this.mWeakRefCache) {
                if (this.mWeakRefCache != null) {
                    this.mWeakRefCache.remove(this.mKey);
                    this.mWeakRefCache = null;
                }
            }
            super.finalize();
        }
    }

    private class LoadBitmapAsyncTask extends AsyncTask<String, Object, BitmapInfo> {
        private AsyncLoadListener mLoadListener;
        private String mSrc;

        public LoadBitmapAsyncTask(AsyncLoadListener asyncLoadListener) {
            this.mLoadListener = asyncLoadListener;
        }

        /* access modifiers changed from: protected */
        public BitmapInfo doInBackground(String... strArr) {
            this.mSrc = strArr[0];
            String str = this.mSrc;
            if (str != null) {
                return ResourceManager.this.loadBitmap(str);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(BitmapInfo bitmapInfo) {
            synchronized (ResourceManager.this.mLoadingBitmaps) {
                this.mLoadListener.onLoadComplete(this.mSrc, bitmapInfo);
                ResourceManager.this.mLoadingBitmaps.remove(this.mSrc);
            }
        }
    }

    public ResourceManager(ResourceLoader resourceLoader) {
        this.mResourceLoader = resourceLoader;
        this.mBitmapsCache = new LruCache<String, BitmapInfo>(DEF_CACHE_SIZE) {
            /* access modifiers changed from: protected */
            public int sizeOf(String str, BitmapInfo bitmapInfo) {
                Bitmap bitmap;
                if (bitmapInfo == null || (bitmap = bitmapInfo.mBitmap) == null) {
                    return 0;
                }
                return bitmap.getAllocationByteCount();
            }
        };
        this.mWeakRefBitmapsCache = new HashMap<>();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: com.miui.maml.ResourceManager$BitmapInfo} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.miui.maml.ResourceManager.BitmapInfo getCache(java.lang.String r5) {
        /*
            r4 = this;
            android.util.LruCache<java.lang.String, com.miui.maml.ResourceManager$BitmapInfo> r0 = r4.mBitmapsCache
            monitor-enter(r0)
            android.util.LruCache<java.lang.String, com.miui.maml.ResourceManager$BitmapInfo> r1 = r4.mBitmapsCache     // Catch:{ all -> 0x0069 }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x0069 }
            com.miui.maml.ResourceManager$BitmapInfo r1 = (com.miui.maml.ResourceManager.BitmapInfo) r1     // Catch:{ all -> 0x0069 }
            monitor-exit(r0)     // Catch:{ all -> 0x0069 }
            java.util.HashMap<java.lang.String, java.lang.ref.WeakReference<com.miui.maml.ResourceManager$BitmapInfo>> r2 = r4.mWeakRefBitmapsCache
            monitor-enter(r2)
            java.util.HashMap<java.lang.String, java.lang.ref.WeakReference<com.miui.maml.ResourceManager$BitmapInfo>> r0 = r4.mWeakRefBitmapsCache     // Catch:{ all -> 0x0066 }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ all -> 0x0066 }
            java.lang.ref.WeakReference r0 = (java.lang.ref.WeakReference) r0     // Catch:{ all -> 0x0066 }
            monitor-exit(r2)     // Catch:{ all -> 0x0066 }
            if (r1 == 0) goto L_0x003a
            long r2 = java.lang.System.currentTimeMillis()
            r1.mLastVisitTime = r2
            if (r0 == 0) goto L_0x0028
            java.lang.Object r0 = r0.get()
            if (r0 != 0) goto L_0x0065
        L_0x0028:
            java.util.HashMap<java.lang.String, java.lang.ref.WeakReference<com.miui.maml.ResourceManager$BitmapInfo>> r2 = r4.mWeakRefBitmapsCache
            monitor-enter(r2)
            java.util.HashMap<java.lang.String, java.lang.ref.WeakReference<com.miui.maml.ResourceManager$BitmapInfo>> r0 = r4.mWeakRefBitmapsCache     // Catch:{ all -> 0x0037 }
            java.lang.ref.WeakReference r3 = new java.lang.ref.WeakReference     // Catch:{ all -> 0x0037 }
            r3.<init>(r1)     // Catch:{ all -> 0x0037 }
            r0.put(r5, r3)     // Catch:{ all -> 0x0037 }
            monitor-exit(r2)     // Catch:{ all -> 0x0037 }
            goto L_0x0065
        L_0x0037:
            r5 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0037 }
            throw r5
        L_0x003a:
            if (r0 == 0) goto L_0x0065
            java.lang.Object r0 = r0.get()
            r1 = r0
            com.miui.maml.ResourceManager$BitmapInfo r1 = (com.miui.maml.ResourceManager.BitmapInfo) r1
            if (r1 == 0) goto L_0x0058
            long r2 = java.lang.System.currentTimeMillis()
            r1.mLastVisitTime = r2
            android.util.LruCache<java.lang.String, com.miui.maml.ResourceManager$BitmapInfo> r0 = r4.mBitmapsCache
            monitor-enter(r0)
            android.util.LruCache<java.lang.String, com.miui.maml.ResourceManager$BitmapInfo> r2 = r4.mBitmapsCache     // Catch:{ all -> 0x0055 }
            r2.put(r5, r1)     // Catch:{ all -> 0x0055 }
            monitor-exit(r0)     // Catch:{ all -> 0x0055 }
            goto L_0x0065
        L_0x0055:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0055 }
            throw r5
        L_0x0058:
            java.util.HashMap<java.lang.String, java.lang.ref.WeakReference<com.miui.maml.ResourceManager$BitmapInfo>> r0 = r4.mWeakRefBitmapsCache
            monitor-enter(r0)
            java.util.HashMap<java.lang.String, java.lang.ref.WeakReference<com.miui.maml.ResourceManager$BitmapInfo>> r2 = r4.mWeakRefBitmapsCache     // Catch:{ all -> 0x0062 }
            r2.remove(r5)     // Catch:{ all -> 0x0062 }
            monitor-exit(r0)     // Catch:{ all -> 0x0062 }
            goto L_0x0065
        L_0x0062:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0062 }
            throw r5
        L_0x0065:
            return r1
        L_0x0066:
            r5 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0066 }
            throw r5
        L_0x0069:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0069 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ResourceManager.getCache(java.lang.String):com.miui.maml.ResourceManager$BitmapInfo");
    }

    /* access modifiers changed from: private */
    public BitmapInfo loadBitmap(String str) {
        BitmapInfo bitmapInfo;
        String str2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        boolean z = true;
        options.inScaled = true;
        options.inTargetDensity = this.mTargetDensity;
        if (this.mExtraResourceFolder != null) {
            Log.i(LOG_TAG, "try to load resource from extra resource: " + this.mExtraResourceFolder + " of " + str);
            options.inDensity = this.mExtraResourceDensity;
            if (TextUtils.isEmpty(this.mExtraResourceFolder)) {
                str2 = str;
            } else {
                str2 = this.mExtraResourceFolder + "/" + str;
            }
            bitmapInfo = this.mResourceLoader.getBitmapInfo(str2, options);
            if (bitmapInfo != null) {
                z = false;
            }
        } else {
            bitmapInfo = null;
        }
        if (bitmapInfo == null) {
            options.inDensity = this.mDefaultResourceDensity;
            bitmapInfo = this.mResourceLoader.getBitmapInfo(str, options);
        }
        if (bitmapInfo == null) {
            options.inDensity = RESOURCE_FALLBACK_DENSITY;
            bitmapInfo = this.mResourceLoader.getBitmapInfo(RESOURCE_FALLBACK_EXTRA_FOLDER + str, options);
        }
        if (bitmapInfo != null) {
            if (!z) {
                Log.i(LOG_TAG, "load image from extra resource: " + this.mExtraResourceFolder + " of " + str);
            }
            bitmapInfo.mKey = str;
            bitmapInfo.mWeakRefCache = this.mWeakRefBitmapsCache;
            bitmapInfo.mBitmap.setDensity(this.mTargetDensity);
            bitmapInfo.mLastVisitTime = System.currentTimeMillis();
            synchronized (this.mBitmapsCache) {
                this.mBitmapsCache.put(str, bitmapInfo);
            }
            synchronized (this.mWeakRefBitmapsCache) {
                this.mWeakRefBitmapsCache.put(str, new WeakReference(bitmapInfo));
            }
        } else {
            Log.e(LOG_TAG, "fail to load image: " + str);
        }
        return bitmapInfo;
    }

    public static int retranslateDensity(int i) {
        return (i <= 240 || i > DENSITY_XHIGH_R) ? (i <= DENSITY_XHIGH_R || i > DENSITY_XXHIGH_R) ? (i <= DENSITY_XXHIGH_R || i > DENSITY_XXXHIGH_R) ? i : ((int) (((double) (i - DENSITY_XXHIGH_R)) * 0.8888888888888888d)) + RESOURCE_FALLBACK_DENSITY : ((int) (((double) (i - DENSITY_XHIGH_R)) * 0.8888888888888888d)) + 320 : ((int) (((double) (i - 240)) * 0.6666666666666666d)) + 240;
    }

    public static int translateDensity(int i) {
        return (i <= 240 || i > 320) ? (i <= 320 || i > RESOURCE_FALLBACK_DENSITY) ? (i <= RESOURCE_FALLBACK_DENSITY || i > DENSITY_XXXHIGH) ? i : ((int) (((double) (i - RESOURCE_FALLBACK_DENSITY)) * 1.125d)) + DENSITY_XXHIGH_R : ((int) (((double) (i - 320)) * 1.125d)) + DENSITY_XHIGH_R : ((int) (((double) (i - 240)) * 1.5d)) + 240;
    }

    public void clear() {
        synchronized (this.mBitmapsCache) {
            this.mBitmapsCache.evictAll();
        }
    }

    public void clear(String str) {
        synchronized (this.mBitmapsCache) {
            this.mBitmapsCache.remove(str);
        }
    }

    public void finish(boolean z) {
        if (!z) {
            synchronized (this.mBitmapsCache) {
                this.mBitmapsCache.evictAll();
            }
            synchronized (this.mWeakRefBitmapsCache) {
                this.mWeakRefBitmapsCache.clear();
            }
        }
        synchronized (this.mLoadingBitmaps) {
            this.mLoadingBitmaps.clear();
        }
        this.mResourceLoader.finish();
    }

    public Bitmap getBitmap(String str) {
        BitmapInfo bitmapInfo = getBitmapInfo(str);
        if (bitmapInfo != null) {
            return bitmapInfo.mBitmap;
        }
        return null;
    }

    public BitmapInfo getBitmapInfo(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        BitmapInfo cache = getCache(str);
        if (cache != null) {
            return cache;
        }
        Log.i(LOG_TAG, "load image " + str);
        return loadBitmap(str);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004c, code lost:
        r6 = new com.miui.maml.ResourceManager.BitmapInfo();
        r6.mLoading = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0053, code lost:
        return r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.maml.ResourceManager.BitmapInfo getBitmapInfoAsync(java.lang.String r6, com.miui.maml.ResourceManager.AsyncLoadListener r7) {
        /*
            r5 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r6)
            if (r0 == 0) goto L_0x0008
            r6 = 0
            return r6
        L_0x0008:
            com.miui.maml.ResourceManager$BitmapInfo r0 = r5.getCache(r6)
            if (r0 == 0) goto L_0x000f
            return r0
        L_0x000f:
            java.util.HashSet<java.lang.String> r0 = r5.mLoadingBitmaps
            monitor-enter(r0)
            java.util.HashSet<java.lang.String> r1 = r5.mLoadingBitmaps     // Catch:{ all -> 0x0054 }
            boolean r1 = r1.contains(r6)     // Catch:{ all -> 0x0054 }
            r2 = 1
            if (r1 != 0) goto L_0x004b
            com.miui.maml.ResourceManager$BitmapInfo r1 = r5.getCache(r6)     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0023
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            return r1
        L_0x0023:
            java.util.HashSet<java.lang.String> r1 = r5.mLoadingBitmaps     // Catch:{ all -> 0x0054 }
            r1.add(r6)     // Catch:{ all -> 0x0054 }
            java.lang.String r1 = "ResourceManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0054 }
            r3.<init>()     // Catch:{ all -> 0x0054 }
            java.lang.String r4 = "load image async: "
            r3.append(r4)     // Catch:{ all -> 0x0054 }
            r3.append(r6)     // Catch:{ all -> 0x0054 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0054 }
            android.util.Log.i(r1, r3)     // Catch:{ all -> 0x0054 }
            com.miui.maml.ResourceManager$LoadBitmapAsyncTask r1 = new com.miui.maml.ResourceManager$LoadBitmapAsyncTask     // Catch:{ all -> 0x0054 }
            r1.<init>(r7)     // Catch:{ all -> 0x0054 }
            java.lang.String[] r7 = new java.lang.String[r2]     // Catch:{ all -> 0x0054 }
            r3 = 0
            r7[r3] = r6     // Catch:{ all -> 0x0054 }
            r1.execute(r7)     // Catch:{ all -> 0x0054 }
        L_0x004b:
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            com.miui.maml.ResourceManager$BitmapInfo r6 = new com.miui.maml.ResourceManager$BitmapInfo
            r6.<init>()
            r6.mLoading = r2
            return r6
        L_0x0054:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ResourceManager.getBitmapInfoAsync(java.lang.String, com.miui.maml.ResourceManager$AsyncLoadListener):com.miui.maml.ResourceManager$BitmapInfo");
    }

    public Element getConfigRoot() {
        return this.mResourceLoader.getConfigRoot();
    }

    public Drawable getDrawable(Resources resources, String str) {
        Bitmap bitmap;
        BitmapInfo bitmapInfo = getBitmapInfo(str);
        if (bitmapInfo == null || (bitmap = bitmapInfo.mBitmap) == null) {
            return null;
        }
        if (bitmapInfo.mNinePatch != null) {
            NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(resources, bitmap, bitmap.getNinePatchChunk(), bitmapInfo.mPadding, str);
            ninePatchDrawable.setTargetDensity(this.mTargetDensity);
            return ninePatchDrawable;
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
        bitmapDrawable.setTargetDensity(this.mTargetDensity);
        return bitmapDrawable;
    }

    public MemoryFile getFile(String str) {
        return this.mResourceLoader.getFile(str);
    }

    public final InputStream getInputStream(String str) {
        return this.mResourceLoader.getInputStream(str);
    }

    public final InputStream getInputStream(String str, long[] jArr) {
        return this.mResourceLoader.getInputStream(str, jArr);
    }

    public Element getManifestRoot() {
        return this.mResourceLoader.getManifestRoot();
    }

    public NinePatch getNinePatch(String str) {
        BitmapInfo bitmapInfo = getBitmapInfo(str);
        if (bitmapInfo != null) {
            return bitmapInfo.mNinePatch;
        }
        return null;
    }

    public String getPathForLanguage(String str) {
        return this.mResourceLoader.getPathForLanguage(str);
    }

    public void init() {
        this.mResourceLoader.init();
    }

    public void pause() {
    }

    public final boolean resourceExists(String str) {
        return this.mResourceLoader.resourceExists(str);
    }

    public void resume() {
    }

    public void setCacheSize(int i) {
        synchronized (this.mBitmapsCache) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.mBitmapsCache.resize(i);
            }
        }
    }

    public void setDefaultResourceDensity(int i) {
        this.mDefaultResourceDensity = i;
    }

    public void setExtraResource(String str) {
        this.mExtraResourceFolder = str;
    }

    public void setExtraResource(String str, int i) {
        this.mExtraResourceFolder = str;
        this.mExtraResourceDensity = i;
    }

    public void setExtraResourceDensity(int i) {
        this.mExtraResourceDensity = i;
    }

    public void setLocal(Locale locale) {
        if (locale != null && !locale.equals(this.mResourceLoader.getLocale())) {
            this.mResourceLoader.setLocal(locale);
            finish(false);
        }
    }

    public void setTargetDensity(int i) {
        this.mTargetDensity = i;
    }
}
