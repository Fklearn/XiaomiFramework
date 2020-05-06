package com.google.android.exoplayer2.source;

public class CompositeSequenceableLoader implements SequenceableLoader {
    protected final SequenceableLoader[] loaders;

    public CompositeSequenceableLoader(SequenceableLoader[] sequenceableLoaderArr) {
        this.loaders = sequenceableLoaderArr;
    }

    public boolean continueLoading(long j) {
        long j2 = j;
        boolean z = false;
        while (true) {
            long nextLoadPositionUs = getNextLoadPositionUs();
            if (nextLoadPositionUs != Long.MIN_VALUE) {
                boolean z2 = false;
                for (SequenceableLoader sequenceableLoader : this.loaders) {
                    long nextLoadPositionUs2 = sequenceableLoader.getNextLoadPositionUs();
                    boolean z3 = nextLoadPositionUs2 != Long.MIN_VALUE && nextLoadPositionUs2 <= j2;
                    if (nextLoadPositionUs2 == nextLoadPositionUs || z3) {
                        z2 |= sequenceableLoader.continueLoading(j2);
                    }
                }
                z |= z2;
                if (!z2) {
                    break;
                }
            } else {
                break;
            }
        }
        return z;
    }

    public final long getBufferedPositionUs() {
        long j = Long.MAX_VALUE;
        for (SequenceableLoader bufferedPositionUs : this.loaders) {
            long bufferedPositionUs2 = bufferedPositionUs.getBufferedPositionUs();
            if (bufferedPositionUs2 != Long.MIN_VALUE) {
                j = Math.min(j, bufferedPositionUs2);
            }
        }
        if (j == Long.MAX_VALUE) {
            return Long.MIN_VALUE;
        }
        return j;
    }

    public final long getNextLoadPositionUs() {
        long j = Long.MAX_VALUE;
        for (SequenceableLoader nextLoadPositionUs : this.loaders) {
            long nextLoadPositionUs2 = nextLoadPositionUs.getNextLoadPositionUs();
            if (nextLoadPositionUs2 != Long.MIN_VALUE) {
                j = Math.min(j, nextLoadPositionUs2);
            }
        }
        if (j == Long.MAX_VALUE) {
            return Long.MIN_VALUE;
        }
        return j;
    }

    public final void reevaluateBuffer(long j) {
        for (SequenceableLoader reevaluateBuffer : this.loaders) {
            reevaluateBuffer.reevaluateBuffer(j);
        }
    }
}
