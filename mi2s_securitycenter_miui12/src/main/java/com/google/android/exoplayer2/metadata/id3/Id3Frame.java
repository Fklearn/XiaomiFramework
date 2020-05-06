package com.google.android.exoplayer2.metadata.id3;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;

public abstract class Id3Frame implements Metadata.Entry {
    public final String id;

    public Id3Frame(String str) {
        Assertions.checkNotNull(str);
        this.id = str;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return this.id;
    }
}
