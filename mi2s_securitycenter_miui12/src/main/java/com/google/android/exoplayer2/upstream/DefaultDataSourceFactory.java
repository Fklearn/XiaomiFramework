package com.google.android.exoplayer2.upstream;

import android.content.Context;
import com.google.android.exoplayer2.upstream.DataSource;

public final class DefaultDataSourceFactory implements DataSource.Factory {
    private final DataSource.Factory baseDataSourceFactory;
    private final Context context;
    private final TransferListener<? super DataSource> listener;

    public DefaultDataSourceFactory(Context context2, TransferListener<? super DataSource> transferListener, DataSource.Factory factory) {
        this.context = context2.getApplicationContext();
        this.listener = transferListener;
        this.baseDataSourceFactory = factory;
    }

    public DefaultDataSourceFactory(Context context2, String str) {
        this(context2, str, (TransferListener<? super DataSource>) null);
    }

    public DefaultDataSourceFactory(Context context2, String str, TransferListener<? super DataSource> transferListener) {
        this(context2, transferListener, (DataSource.Factory) new DefaultHttpDataSourceFactory(str, transferListener));
    }

    public DefaultDataSource createDataSource() {
        return new DefaultDataSource(this.context, this.listener, this.baseDataSourceFactory.createDataSource());
    }
}
