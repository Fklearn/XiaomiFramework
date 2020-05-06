package com.google.android.exoplayer2.audio;

import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;

public interface AudioRendererEventListener {

    public static final class EventDispatcher {
        @Nullable
        private final Handler handler;
        /* access modifiers changed from: private */
        @Nullable
        public final AudioRendererEventListener listener;

        public EventDispatcher(@Nullable Handler handler2, @Nullable AudioRendererEventListener audioRendererEventListener) {
            Handler handler3;
            if (audioRendererEventListener != null) {
                Assertions.checkNotNull(handler2);
                handler3 = handler2;
            } else {
                handler3 = null;
            }
            this.handler = handler3;
            this.listener = audioRendererEventListener;
        }

        public void audioSessionId(final int i) {
            if (this.listener != null) {
                this.handler.post(new Runnable() {
                    public void run() {
                        EventDispatcher.this.listener.onAudioSessionId(i);
                    }
                });
            }
        }

        public void audioTrackUnderrun(int i, long j, long j2) {
            if (this.listener != null) {
                final int i2 = i;
                final long j3 = j;
                final long j4 = j2;
                this.handler.post(new Runnable() {
                    public void run() {
                        EventDispatcher.this.listener.onAudioSinkUnderrun(i2, j3, j4);
                    }
                });
            }
        }

        public void decoderInitialized(String str, long j, long j2) {
            if (this.listener != null) {
                final String str2 = str;
                final long j3 = j;
                final long j4 = j2;
                this.handler.post(new Runnable() {
                    public void run() {
                        EventDispatcher.this.listener.onAudioDecoderInitialized(str2, j3, j4);
                    }
                });
            }
        }

        public void disabled(final DecoderCounters decoderCounters) {
            if (this.listener != null) {
                this.handler.post(new Runnable() {
                    public void run() {
                        decoderCounters.ensureUpdated();
                        EventDispatcher.this.listener.onAudioDisabled(decoderCounters);
                    }
                });
            }
        }

        public void enabled(final DecoderCounters decoderCounters) {
            if (this.listener != null) {
                this.handler.post(new Runnable() {
                    public void run() {
                        EventDispatcher.this.listener.onAudioEnabled(decoderCounters);
                    }
                });
            }
        }

        public void inputFormatChanged(final Format format) {
            if (this.listener != null) {
                this.handler.post(new Runnable() {
                    public void run() {
                        EventDispatcher.this.listener.onAudioInputFormatChanged(format);
                    }
                });
            }
        }
    }

    void onAudioDecoderInitialized(String str, long j, long j2);

    void onAudioDisabled(DecoderCounters decoderCounters);

    void onAudioEnabled(DecoderCounters decoderCounters);

    void onAudioInputFormatChanged(Format format);

    void onAudioSessionId(int i);

    void onAudioSinkUnderrun(int i, long j, long j2);
}
