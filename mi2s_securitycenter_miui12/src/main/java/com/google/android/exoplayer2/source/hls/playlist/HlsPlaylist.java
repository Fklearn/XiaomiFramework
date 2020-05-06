package com.google.android.exoplayer2.source.hls.playlist;

import com.google.android.exoplayer2.offline.FilterableManifest;
import java.util.Collections;
import java.util.List;

public abstract class HlsPlaylist implements FilterableManifest<HlsPlaylist, RenditionKey> {
    public final String baseUri;
    public final List<String> tags;

    protected HlsPlaylist(String str, List<String> list) {
        this.baseUri = str;
        this.tags = Collections.unmodifiableList(list);
    }
}
