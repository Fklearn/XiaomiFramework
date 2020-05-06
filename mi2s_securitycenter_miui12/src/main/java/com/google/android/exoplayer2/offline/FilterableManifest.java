package com.google.android.exoplayer2.offline;

import java.util.List;

public interface FilterableManifest<T, K> {
    T copy(List<K> list);
}
