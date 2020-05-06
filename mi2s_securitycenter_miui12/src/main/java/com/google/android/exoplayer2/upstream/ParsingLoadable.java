package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.util.Util;
import java.io.Closeable;
import java.io.InputStream;

public final class ParsingLoadable<T> implements Loader.Loadable {
    private volatile long bytesLoaded;
    private final DataSource dataSource;
    public final DataSpec dataSpec;
    private final Parser<? extends T> parser;
    private volatile T result;
    public final int type;

    public interface Parser<T> {
        T parse(Uri uri, InputStream inputStream);
    }

    public ParsingLoadable(DataSource dataSource2, Uri uri, int i, Parser<? extends T> parser2) {
        this(dataSource2, new DataSpec(uri, 3), i, parser2);
    }

    public ParsingLoadable(DataSource dataSource2, DataSpec dataSpec2, int i, Parser<? extends T> parser2) {
        this.dataSource = dataSource2;
        this.dataSpec = dataSpec2;
        this.type = i;
        this.parser = parser2;
    }

    public static <T> T load(DataSource dataSource2, Parser<? extends T> parser2, Uri uri) {
        ParsingLoadable parsingLoadable = new ParsingLoadable(dataSource2, uri, 0, parser2);
        parsingLoadable.load();
        return parsingLoadable.getResult();
    }

    public long bytesLoaded() {
        return this.bytesLoaded;
    }

    public final void cancelLoad() {
    }

    public final T getResult() {
        return this.result;
    }

    public final void load() {
        DataSourceInputStream dataSourceInputStream = new DataSourceInputStream(this.dataSource, this.dataSpec);
        try {
            dataSourceInputStream.open();
            this.result = this.parser.parse(this.dataSource.getUri(), dataSourceInputStream);
        } finally {
            this.bytesLoaded = dataSourceInputStream.bytesRead();
            Util.closeQuietly((Closeable) dataSourceInputStream);
        }
    }
}
