package com.google.android.exoplayer2.source.hls.playlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class RenditionKey implements Comparable<RenditionKey> {
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_SUBTITLE = 2;
    public static final int TYPE_VARIANT = 0;
    public final int trackIndex;
    public final int type;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public RenditionKey(int i, int i2) {
        this.type = i;
        this.trackIndex = i2;
    }

    public int compareTo(@NonNull RenditionKey renditionKey) {
        int i = this.type - renditionKey.type;
        return i == 0 ? this.trackIndex - renditionKey.trackIndex : i;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || RenditionKey.class != obj.getClass()) {
            return false;
        }
        RenditionKey renditionKey = (RenditionKey) obj;
        return this.type == renditionKey.type && this.trackIndex == renditionKey.trackIndex;
    }

    public int hashCode() {
        return (this.type * 31) + this.trackIndex;
    }

    public String toString() {
        return this.type + "." + this.trackIndex;
    }
}
