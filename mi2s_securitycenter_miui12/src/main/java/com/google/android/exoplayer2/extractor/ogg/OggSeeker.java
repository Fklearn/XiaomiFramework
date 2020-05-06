package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;

interface OggSeeker {
    SeekMap createSeekMap();

    long read(ExtractorInput extractorInput);

    long startSeek(long j);
}
