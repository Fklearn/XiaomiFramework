package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.upstream.DataSource;

public final class FileDataSourceFactory implements DataSource.Factory {
    private final TransferListener<? super FileDataSource> listener;

    public FileDataSourceFactory() {
        this((TransferListener<? super FileDataSource>) null);
    }

    public FileDataSourceFactory(TransferListener<? super FileDataSource> transferListener) {
        this.listener = transferListener;
    }

    public DataSource createDataSource() {
        return new FileDataSource(this.listener);
    }
}
