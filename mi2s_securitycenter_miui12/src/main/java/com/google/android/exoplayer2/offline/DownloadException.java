package com.google.android.exoplayer2.offline;

import java.io.IOException;

public final class DownloadException extends IOException {
    public DownloadException(String str) {
        super(str);
    }

    public DownloadException(Throwable th) {
        super(th);
    }
}
