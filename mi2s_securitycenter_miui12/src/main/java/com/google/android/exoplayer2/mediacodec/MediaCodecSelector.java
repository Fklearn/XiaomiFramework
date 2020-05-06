package com.google.android.exoplayer2.mediacodec;

public interface MediaCodecSelector {
    public static final MediaCodecSelector DEFAULT = new MediaCodecSelector() {
        public MediaCodecInfo getDecoderInfo(String str, boolean z) {
            return MediaCodecUtil.getDecoderInfo(str, z);
        }

        public MediaCodecInfo getPassthroughDecoderInfo() {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    };

    MediaCodecInfo getDecoderInfo(String str, boolean z);

    MediaCodecInfo getPassthroughDecoderInfo();
}
