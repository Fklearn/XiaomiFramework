package com.google.android.exoplayer2.upstream.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C;
import java.io.File;

public class CacheSpan implements Comparable<CacheSpan> {
    @Nullable
    public final File file;
    public final boolean isCached;
    public final String key;
    public final long lastAccessTimestamp;
    public final long length;
    public final long position;

    public CacheSpan(String str, long j, long j2) {
        this(str, j, j2, C.TIME_UNSET, (File) null);
    }

    public CacheSpan(String str, long j, long j2, long j3, @Nullable File file2) {
        this.key = str;
        this.position = j;
        this.length = j2;
        this.isCached = file2 != null;
        this.file = file2;
        this.lastAccessTimestamp = j3;
    }

    public int compareTo(@NonNull CacheSpan cacheSpan) {
        if (!this.key.equals(cacheSpan.key)) {
            return this.key.compareTo(cacheSpan.key);
        }
        int i = ((this.position - cacheSpan.position) > 0 ? 1 : ((this.position - cacheSpan.position) == 0 ? 0 : -1));
        if (i == 0) {
            return 0;
        }
        return i < 0 ? -1 : 1;
    }

    public boolean isHoleSpan() {
        return !this.isCached;
    }

    public boolean isOpenEnded() {
        return this.length == -1;
    }
}
