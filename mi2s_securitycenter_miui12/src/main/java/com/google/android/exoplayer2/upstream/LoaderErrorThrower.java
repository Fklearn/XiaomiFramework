package com.google.android.exoplayer2.upstream;

public interface LoaderErrorThrower {

    public static final class Dummy implements LoaderErrorThrower {
        public void maybeThrowError() {
        }

        public void maybeThrowError(int i) {
        }
    }

    void maybeThrowError();

    void maybeThrowError(int i);
}
