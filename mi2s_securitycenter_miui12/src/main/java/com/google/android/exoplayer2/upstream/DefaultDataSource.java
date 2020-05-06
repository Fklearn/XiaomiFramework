package com.google.android.exoplayer2.upstream;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;

public final class DefaultDataSource implements DataSource {
    private static final String SCHEME_ASSET = "asset";
    private static final String SCHEME_CONTENT = "content";
    private static final String SCHEME_RAW = "rawresource";
    private static final String SCHEME_RTMP = "rtmp";
    private static final String TAG = "DefaultDataSource";
    private DataSource assetDataSource;
    private final DataSource baseDataSource;
    private DataSource contentDataSource;
    private final Context context;
    private DataSource dataSchemeDataSource;
    private DataSource dataSource;
    private DataSource fileDataSource;
    private final TransferListener<? super DataSource> listener;
    private DataSource rawResourceDataSource;
    private DataSource rtmpDataSource;

    public DefaultDataSource(Context context2, TransferListener<? super DataSource> transferListener, DataSource dataSource2) {
        this.context = context2.getApplicationContext();
        this.listener = transferListener;
        Assertions.checkNotNull(dataSource2);
        this.baseDataSource = dataSource2;
    }

    public DefaultDataSource(Context context2, TransferListener<? super DataSource> transferListener, String str, int i, int i2, boolean z) {
        this(context2, transferListener, new DefaultHttpDataSource(str, (Predicate<String>) null, transferListener, i, i2, z, (HttpDataSource.RequestProperties) null));
    }

    public DefaultDataSource(Context context2, TransferListener<? super DataSource> transferListener, String str, boolean z) {
        this(context2, transferListener, str, 8000, 8000, z);
    }

    private DataSource getAssetDataSource() {
        if (this.assetDataSource == null) {
            this.assetDataSource = new AssetDataSource(this.context, this.listener);
        }
        return this.assetDataSource;
    }

    private DataSource getContentDataSource() {
        if (this.contentDataSource == null) {
            this.contentDataSource = new ContentDataSource(this.context, this.listener);
        }
        return this.contentDataSource;
    }

    private DataSource getDataSchemeDataSource() {
        if (this.dataSchemeDataSource == null) {
            this.dataSchemeDataSource = new DataSchemeDataSource();
        }
        return this.dataSchemeDataSource;
    }

    private DataSource getFileDataSource() {
        if (this.fileDataSource == null) {
            this.fileDataSource = new FileDataSource(this.listener);
        }
        return this.fileDataSource;
    }

    private DataSource getRawResourceDataSource() {
        if (this.rawResourceDataSource == null) {
            this.rawResourceDataSource = new RawResourceDataSource(this.context, this.listener);
        }
        return this.rawResourceDataSource;
    }

    private DataSource getRtmpDataSource() {
        if (this.rtmpDataSource == null) {
            try {
                this.rtmpDataSource = (DataSource) Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource").getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ClassNotFoundException unused) {
                Log.w(TAG, "Attempting to play RTMP stream without depending on the RTMP extension");
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating RTMP extension", e);
            }
            if (this.rtmpDataSource == null) {
                this.rtmpDataSource = this.baseDataSource;
            }
        }
        return this.rtmpDataSource;
    }

    public void close() {
        DataSource dataSource2 = this.dataSource;
        if (dataSource2 != null) {
            try {
                dataSource2.close();
            } finally {
                this.dataSource = null;
            }
        }
    }

    public Uri getUri() {
        DataSource dataSource2 = this.dataSource;
        if (dataSource2 == null) {
            return null;
        }
        return dataSource2.getUri();
    }

    public long open(DataSpec dataSpec) {
        DataSource dataSource2;
        Assertions.checkState(this.dataSource == null);
        String scheme = dataSpec.uri.getScheme();
        if (Util.isLocalFileUri(dataSpec.uri)) {
            if (!dataSpec.uri.getPath().startsWith("/android_asset/")) {
                dataSource2 = getFileDataSource();
                this.dataSource = dataSource2;
                return this.dataSource.open(dataSpec);
            }
        } else if (!SCHEME_ASSET.equals(scheme)) {
            dataSource2 = "content".equals(scheme) ? getContentDataSource() : SCHEME_RTMP.equals(scheme) ? getRtmpDataSource() : DataSchemeDataSource.SCHEME_DATA.equals(scheme) ? getDataSchemeDataSource() : "rawresource".equals(scheme) ? getRawResourceDataSource() : this.baseDataSource;
            this.dataSource = dataSource2;
            return this.dataSource.open(dataSpec);
        }
        dataSource2 = getAssetDataSource();
        this.dataSource = dataSource2;
        return this.dataSource.open(dataSpec);
    }

    public int read(byte[] bArr, int i, int i2) {
        return this.dataSource.read(bArr, i, i2);
    }
}
