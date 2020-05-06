package com.google.android.exoplayer2.offline;

public interface Downloader {
    void cancel();

    void download();

    float getDownloadPercentage();

    long getDownloadedBytes();

    void remove();
}
