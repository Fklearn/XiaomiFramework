package com.miui.maml;

import android.util.Log;
import com.miui.maml.ResourceManager;
import java.util.ArrayList;
import java.util.Iterator;

public class LifecycleResourceManager extends ResourceManager {
    private static final String LOG_TAG = "LifecycleResourceManager";
    public static final int TIME_DAY = 86400000;
    public static final int TIME_HOUR = 3600000;
    private static long mLastCheckCacheTime;
    private long mCheckTime;
    private long mInactiveTime;

    public LifecycleResourceManager(ResourceLoader resourceLoader, long j, long j2) {
        super(resourceLoader);
        this.mInactiveTime = j;
        this.mCheckTime = j2;
    }

    public void checkCache() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mLastCheckCacheTime >= this.mCheckTime) {
            Log.d(LOG_TAG, "begin check cache... ");
            ArrayList arrayList = new ArrayList();
            synchronized (this.mBitmapsCache) {
                for (String next : this.mBitmapsCache.snapshot().keySet()) {
                    ResourceManager.BitmapInfo bitmapInfo = this.mBitmapsCache.get(next);
                    if (bitmapInfo != null && currentTimeMillis - bitmapInfo.mLastVisitTime > this.mInactiveTime) {
                        arrayList.add(next);
                    }
                }
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    String str = (String) it.next();
                    Log.d(LOG_TAG, "remove cache: " + str);
                    this.mBitmapsCache.remove(str);
                }
            }
            mLastCheckCacheTime = currentTimeMillis;
        }
    }

    public void finish(boolean z) {
        if (z) {
            checkCache();
        }
        super.finish(z);
    }

    public void pause() {
        checkCache();
    }
}
