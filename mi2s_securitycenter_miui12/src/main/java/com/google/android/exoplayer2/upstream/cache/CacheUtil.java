package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import java.io.EOFException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CacheUtil {
    public static final int DEFAULT_BUFFER_SIZE_BYTES = 131072;

    public static class CachingCounters {
        public volatile long alreadyCachedBytes;
        public volatile long contentLength = -1;
        public volatile long newlyCachedBytes;

        public long totalCachedBytes() {
            return this.alreadyCachedBytes + this.newlyCachedBytes;
        }
    }

    private CacheUtil() {
    }

    public static void cache(DataSpec dataSpec, Cache cache, DataSource dataSource, @Nullable CachingCounters cachingCounters, @Nullable AtomicBoolean atomicBoolean) {
        cache(dataSpec, cache, new CacheDataSource(cache, dataSource), new byte[131072], (PriorityTaskManager) null, 0, cachingCounters, atomicBoolean, false);
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheDataSource cacheDataSource, byte[] bArr, PriorityTaskManager priorityTaskManager, int i, @Nullable CachingCounters cachingCounters, @Nullable AtomicBoolean atomicBoolean, boolean z) {
        long j;
        DataSpec dataSpec2 = dataSpec;
        Cache cache2 = cache;
        CachingCounters cachingCounters2 = cachingCounters;
        Assertions.checkNotNull(cacheDataSource);
        Assertions.checkNotNull(bArr);
        if (cachingCounters2 != null) {
            getCached(dataSpec2, cache2, cachingCounters2);
        } else {
            cachingCounters2 = new CachingCounters();
        }
        CachingCounters cachingCounters3 = cachingCounters2;
        String key = getKey(dataSpec);
        long j2 = dataSpec2.absoluteStreamPosition;
        long j3 = dataSpec2.length;
        if (j3 == -1) {
            j3 = cache2.getContentLength(key);
        }
        long j4 = j2;
        long j5 = j3;
        while (j5 != 0) {
            if (atomicBoolean == null || !atomicBoolean.get()) {
                int i2 = (j5 > -1 ? 1 : (j5 == -1 ? 0 : -1));
                long cachedLength = cache.getCachedLength(key, j4, i2 != 0 ? j5 : Long.MAX_VALUE);
                if (cachedLength > 0) {
                    j = cachedLength;
                } else {
                    long j6 = -cachedLength;
                    j = j6;
                    if (readAndDiscard(dataSpec, j4, j6, cacheDataSource, bArr, priorityTaskManager, i, cachingCounters3) < j) {
                        if (z && i2 != 0) {
                            throw new EOFException();
                        }
                        return;
                    }
                }
                j4 += j;
                if (i2 == 0) {
                    j = 0;
                }
                j5 -= j;
            } else {
                throw new InterruptedException();
            }
        }
    }

    public static String generateKey(Uri uri) {
        return uri.toString();
    }

    public static void getCached(DataSpec dataSpec, Cache cache, CachingCounters cachingCounters) {
        DataSpec dataSpec2 = dataSpec;
        CachingCounters cachingCounters2 = cachingCounters;
        String key = getKey(dataSpec);
        long j = dataSpec2.absoluteStreamPosition;
        long j2 = dataSpec2.length;
        if (j2 != -1) {
            Cache cache2 = cache;
        } else {
            j2 = cache.getContentLength(key);
        }
        cachingCounters2.contentLength = j2;
        cachingCounters2.alreadyCachedBytes = 0;
        cachingCounters2.newlyCachedBytes = 0;
        long j3 = j;
        long j4 = j2;
        while (j4 != 0) {
            int i = (j4 > -1 ? 1 : (j4 == -1 ? 0 : -1));
            long cachedLength = cache.getCachedLength(key, j3, i != 0 ? j4 : Long.MAX_VALUE);
            if (cachedLength > 0) {
                cachingCounters2.alreadyCachedBytes += cachedLength;
            } else {
                cachedLength = -cachedLength;
                if (cachedLength == Long.MAX_VALUE) {
                    return;
                }
            }
            j3 += cachedLength;
            if (i == 0) {
                cachedLength = 0;
            }
            j4 -= cachedLength;
        }
    }

    public static String getKey(DataSpec dataSpec) {
        String str = dataSpec.key;
        return str != null ? str : generateKey(dataSpec.uri);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x008d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x008e, code lost:
        com.google.android.exoplayer2.util.Util.closeQuietly(r21);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0091, code lost:
        throw r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x008d A[ExcHandler: all (r0v1 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:3:0x000d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long readAndDiscard(com.google.android.exoplayer2.upstream.DataSpec r16, long r17, long r19, com.google.android.exoplayer2.upstream.DataSource r21, byte[] r22, com.google.android.exoplayer2.util.PriorityTaskManager r23, int r24, com.google.android.exoplayer2.upstream.cache.CacheUtil.CachingCounters r25) {
        /*
            r1 = r21
            r0 = r22
            r2 = r25
            r3 = r16
        L_0x0008:
            if (r23 == 0) goto L_0x000d
            r23.proceed(r24)
        L_0x000d:
            boolean r4 = java.lang.Thread.interrupted()     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            if (r4 != 0) goto L_0x0087
            com.google.android.exoplayer2.upstream.DataSpec r4 = new com.google.android.exoplayer2.upstream.DataSpec     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            android.net.Uri r6 = r3.uri     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            byte[] r7 = r3.postBody     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            long r8 = r3.position     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            long r8 = r8 + r17
            long r10 = r3.absoluteStreamPosition     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            long r10 = r8 - r10
            r12 = -1
            java.lang.String r14 = r3.key     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            int r5 = r3.flags     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            r15 = r5 | 2
            r5 = r4
            r8 = r17
            r5.<init>(r6, r7, r8, r10, r12, r14, r15)     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            long r5 = r1.open(r4)     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            long r7 = r2.contentLength     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            r9 = -1
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 != 0) goto L_0x0044
            int r3 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r3 == 0) goto L_0x0044
            long r7 = r4.absoluteStreamPosition     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            long r7 = r7 + r5
            r2.contentLength = r7     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
        L_0x0044:
            r5 = 0
        L_0x0046:
            int r3 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r3 == 0) goto L_0x0081
            boolean r3 = java.lang.Thread.interrupted()     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            if (r3 != 0) goto L_0x007b
            r3 = 0
            int r7 = (r19 > r9 ? 1 : (r19 == r9 ? 0 : -1))
            if (r7 == 0) goto L_0x005f
            int r7 = r0.length     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            long r7 = (long) r7     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            long r11 = r19 - r5
            long r7 = java.lang.Math.min(r7, r11)     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            int r7 = (int) r7     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            goto L_0x0060
        L_0x005f:
            int r7 = r0.length     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
        L_0x0060:
            int r3 = r1.read(r0, r3, r7)     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            r7 = -1
            if (r3 != r7) goto L_0x0073
            long r7 = r2.contentLength     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            int r3 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r3 != 0) goto L_0x0081
            long r7 = r4.absoluteStreamPosition     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            long r7 = r7 + r5
            r2.contentLength = r7     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            goto L_0x0081
        L_0x0073:
            long r7 = (long) r3     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            long r5 = r5 + r7
            long r11 = r2.newlyCachedBytes     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            long r11 = r11 + r7
            r2.newlyCachedBytes = r11     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            goto L_0x0046
        L_0x007b:
            java.lang.InterruptedException r3 = new java.lang.InterruptedException     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            r3.<init>()     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
            throw r3     // Catch:{ PriorityTooLowException -> 0x0085, all -> 0x008d }
        L_0x0081:
            com.google.android.exoplayer2.util.Util.closeQuietly((com.google.android.exoplayer2.upstream.DataSource) r21)
            return r5
        L_0x0085:
            r3 = r4
            goto L_0x0092
        L_0x0087:
            java.lang.InterruptedException r4 = new java.lang.InterruptedException     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            r4.<init>()     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
            throw r4     // Catch:{ PriorityTooLowException -> 0x0092, all -> 0x008d }
        L_0x008d:
            r0 = move-exception
            com.google.android.exoplayer2.util.Util.closeQuietly((com.google.android.exoplayer2.upstream.DataSource) r21)
            throw r0
        L_0x0092:
            com.google.android.exoplayer2.util.Util.closeQuietly((com.google.android.exoplayer2.upstream.DataSource) r21)
            goto L_0x0008
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.CacheUtil.readAndDiscard(com.google.android.exoplayer2.upstream.DataSpec, long, long, com.google.android.exoplayer2.upstream.DataSource, byte[], com.google.android.exoplayer2.util.PriorityTaskManager, int, com.google.android.exoplayer2.upstream.cache.CacheUtil$CachingCounters):long");
    }

    public static void remove(Cache cache, String str) {
        for (CacheSpan removeSpan : cache.getCachedSpans(str)) {
            try {
                cache.removeSpan(removeSpan);
            } catch (Cache.CacheException unused) {
            }
        }
    }
}
