package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import android.support.annotation.Nullable;

public interface DataSource {

    public interface Factory {
        DataSource createDataSource();
    }

    void close();

    @Nullable
    Uri getUri();

    long open(DataSpec dataSpec);

    int read(byte[] bArr, int i, int i2);
}
