package com.google.android.exoplayer2;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class DefaultRenderersFactory implements RenderersFactory {
    public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000;
    public static final int EXTENSION_RENDERER_MODE_OFF = 0;
    public static final int EXTENSION_RENDERER_MODE_ON = 1;
    public static final int EXTENSION_RENDERER_MODE_PREFER = 2;
    protected static final int MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50;
    private static final String TAG = "DefaultRenderersFactory";
    private final long allowedVideoJoiningTimeMs;
    private final Context context;
    @Nullable
    private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private final int extensionRendererMode;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ExtensionRendererMode {
    }

    public DefaultRenderersFactory(Context context2) {
        this(context2, 0);
    }

    public DefaultRenderersFactory(Context context2, int i) {
        this(context2, (DrmSessionManager<FrameworkMediaCrypto>) null, i, DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public DefaultRenderersFactory(Context context2, int i, long j) {
        this(context2, (DrmSessionManager<FrameworkMediaCrypto>) null, i, j);
    }

    @Deprecated
    public DefaultRenderersFactory(Context context2, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2) {
        this(context2, drmSessionManager2, 0);
    }

    @Deprecated
    public DefaultRenderersFactory(Context context2, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2, int i) {
        this(context2, drmSessionManager2, i, DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    @Deprecated
    public DefaultRenderersFactory(Context context2, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2, int i, long j) {
        this.context = context2;
        this.extensionRendererMode = i;
        this.allowedVideoJoiningTimeMs = j;
        this.drmSessionManager = drmSessionManager2;
    }

    /* access modifiers changed from: protected */
    public AudioProcessor[] buildAudioProcessors() {
        return new AudioProcessor[0];
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x005e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0066, code lost:
        throw new java.lang.RuntimeException("Error instantiating Opus extension", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0099, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00a1, code lost:
        throw new java.lang.RuntimeException("Error instantiating FLAC extension", r0);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x005e A[ExcHandler: Exception (r0v16 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:7:0x002f] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0099 A[ExcHandler: Exception (r0v10 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:19:0x006a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildAudioRenderers(android.content.Context r14, @android.support.annotation.Nullable com.google.android.exoplayer2.drm.DrmSessionManager<com.google.android.exoplayer2.drm.FrameworkMediaCrypto> r15, com.google.android.exoplayer2.audio.AudioProcessor[] r16, android.os.Handler r17, com.google.android.exoplayer2.audio.AudioRendererEventListener r18, int r19, java.util.ArrayList<com.google.android.exoplayer2.Renderer> r20) {
        /*
            r13 = this;
            r0 = r19
            r10 = r20
            java.lang.String r11 = "DefaultRenderersFactory"
            com.google.android.exoplayer2.audio.MediaCodecAudioRenderer r12 = new com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
            com.google.android.exoplayer2.mediacodec.MediaCodecSelector r3 = com.google.android.exoplayer2.mediacodec.MediaCodecSelector.DEFAULT
            com.google.android.exoplayer2.audio.AudioCapabilities r8 = com.google.android.exoplayer2.audio.AudioCapabilities.getCapabilities((android.content.Context) r14)
            r5 = 0
            r1 = r12
            r2 = r14
            r4 = r15
            r6 = r17
            r7 = r18
            r9 = r16
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
            r10.add(r12)
            if (r0 != 0) goto L_0x0021
            return
        L_0x0021:
            int r1 = r20.size()
            r2 = 2
            if (r0 != r2) goto L_0x002a
            int r1 = r1 + -1
        L_0x002a:
            java.lang.String r0 = "com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer"
            r3 = 0
            r4 = 3
            r5 = 1
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            java.lang.Class[] r6 = new java.lang.Class[r4]     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            java.lang.Class<android.os.Handler> r7 = android.os.Handler.class
            r6[r3] = r7     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            java.lang.Class<com.google.android.exoplayer2.audio.AudioRendererEventListener> r7 = com.google.android.exoplayer2.audio.AudioRendererEventListener.class
            r6[r5] = r7     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            java.lang.Class<com.google.android.exoplayer2.audio.AudioProcessor[]> r7 = com.google.android.exoplayer2.audio.AudioProcessor[].class
            r6[r2] = r7     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            java.lang.reflect.Constructor r0 = r0.getConstructor(r6)     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            java.lang.Object[] r6 = new java.lang.Object[r4]     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            r6[r3] = r17     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            r6[r5] = r18     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            r6[r2] = r16     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            java.lang.Object r0 = r0.newInstance(r6)     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            com.google.android.exoplayer2.Renderer r0 = (com.google.android.exoplayer2.Renderer) r0     // Catch:{ ClassNotFoundException -> 0x0067, Exception -> 0x005e }
            int r6 = r1 + 1
            r10.add(r1, r0)     // Catch:{ ClassNotFoundException -> 0x0068, Exception -> 0x005e }
            java.lang.String r0 = "Loaded LibopusAudioRenderer."
            android.util.Log.i(r11, r0)     // Catch:{ ClassNotFoundException -> 0x0068, Exception -> 0x005e }
            goto L_0x0068
        L_0x005e:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "Error instantiating Opus extension"
            r1.<init>(r2, r0)
            throw r1
        L_0x0067:
            r6 = r1
        L_0x0068:
            java.lang.String r0 = "com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            java.lang.Class[] r1 = new java.lang.Class[r4]     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            java.lang.Class<android.os.Handler> r7 = android.os.Handler.class
            r1[r3] = r7     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            java.lang.Class<com.google.android.exoplayer2.audio.AudioRendererEventListener> r7 = com.google.android.exoplayer2.audio.AudioRendererEventListener.class
            r1[r5] = r7     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            java.lang.Class<com.google.android.exoplayer2.audio.AudioProcessor[]> r7 = com.google.android.exoplayer2.audio.AudioProcessor[].class
            r1[r2] = r7     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            java.lang.reflect.Constructor r0 = r0.getConstructor(r1)     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            java.lang.Object[] r1 = new java.lang.Object[r4]     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            r1[r3] = r17     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            r1[r5] = r18     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            r1[r2] = r16     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            java.lang.Object r0 = r0.newInstance(r1)     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            com.google.android.exoplayer2.Renderer r0 = (com.google.android.exoplayer2.Renderer) r0     // Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0099 }
            int r1 = r6 + 1
            r10.add(r6, r0)     // Catch:{ ClassNotFoundException -> 0x00a3, Exception -> 0x0099 }
            java.lang.String r0 = "Loaded LibflacAudioRenderer."
            android.util.Log.i(r11, r0)     // Catch:{ ClassNotFoundException -> 0x00a3, Exception -> 0x0099 }
            goto L_0x00a3
        L_0x0099:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "Error instantiating FLAC extension"
            r1.<init>(r2, r0)
            throw r1
        L_0x00a2:
            r1 = r6
        L_0x00a3:
            java.lang.String r0 = "com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            java.lang.Class[] r6 = new java.lang.Class[r4]     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            java.lang.Class<android.os.Handler> r7 = android.os.Handler.class
            r6[r3] = r7     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            java.lang.Class<com.google.android.exoplayer2.audio.AudioRendererEventListener> r7 = com.google.android.exoplayer2.audio.AudioRendererEventListener.class
            r6[r5] = r7     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            java.lang.Class<com.google.android.exoplayer2.audio.AudioProcessor[]> r7 = com.google.android.exoplayer2.audio.AudioProcessor[].class
            r6[r2] = r7     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            java.lang.reflect.Constructor r0 = r0.getConstructor(r6)     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            r4[r3] = r17     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            r4[r5] = r18     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            r4[r2] = r16     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            java.lang.Object r0 = r0.newInstance(r4)     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            com.google.android.exoplayer2.Renderer r0 = (com.google.android.exoplayer2.Renderer) r0     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            r10.add(r1, r0)     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            java.lang.String r0 = "Loaded FfmpegAudioRenderer."
            android.util.Log.i(r11, r0)     // Catch:{ ClassNotFoundException -> 0x00db, Exception -> 0x00d2 }
            goto L_0x00db
        L_0x00d2:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "Error instantiating FFmpeg extension"
            r1.<init>(r2, r0)
            throw r1
        L_0x00db:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.DefaultRenderersFactory.buildAudioRenderers(android.content.Context, com.google.android.exoplayer2.drm.DrmSessionManager, com.google.android.exoplayer2.audio.AudioProcessor[], android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, int, java.util.ArrayList):void");
    }

    /* access modifiers changed from: protected */
    public void buildMetadataRenderers(Context context2, MetadataOutput metadataOutput, Looper looper, int i, ArrayList<Renderer> arrayList) {
        arrayList.add(new MetadataRenderer(metadataOutput, looper));
    }

    /* access modifiers changed from: protected */
    public void buildMiscellaneousRenderers(Context context2, Handler handler, int i, ArrayList<Renderer> arrayList) {
    }

    /* access modifiers changed from: protected */
    public void buildTextRenderers(Context context2, TextOutput textOutput, Looper looper, int i, ArrayList<Renderer> arrayList) {
        arrayList.add(new TextRenderer(textOutput, looper));
    }

    /* access modifiers changed from: protected */
    public void buildVideoRenderers(Context context2, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2, long j, Handler handler, VideoRendererEventListener videoRendererEventListener, int i, ArrayList<Renderer> arrayList) {
        int i2 = i;
        ArrayList<Renderer> arrayList2 = arrayList;
        arrayList2.add(new MediaCodecVideoRenderer(context2, MediaCodecSelector.DEFAULT, j, drmSessionManager2, false, handler, videoRendererEventListener, 50));
        if (i2 != 0) {
            int size = arrayList.size();
            if (i2 == 2) {
                size--;
            }
            try {
                arrayList2.add(size, (Renderer) Class.forName("com.google.android.exoplayer2.ext.vp9.LibvpxVideoRenderer").getConstructor(new Class[]{Boolean.TYPE, Long.TYPE, Handler.class, VideoRendererEventListener.class, Integer.TYPE}).newInstance(new Object[]{true, Long.valueOf(j), handler, videoRendererEventListener, 50}));
                Log.i(TAG, "Loaded LibvpxVideoRenderer.");
            } catch (ClassNotFoundException unused) {
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating VP9 extension", e);
            }
        }
    }

    public Renderer[] createRenderers(Handler handler, VideoRendererEventListener videoRendererEventListener, AudioRendererEventListener audioRendererEventListener, TextOutput textOutput, MetadataOutput metadataOutput, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager2) {
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager3 = drmSessionManager2 == null ? this.drmSessionManager : drmSessionManager2;
        ArrayList arrayList = new ArrayList();
        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager4 = drmSessionManager3;
        buildVideoRenderers(this.context, drmSessionManager4, this.allowedVideoJoiningTimeMs, handler, videoRendererEventListener, this.extensionRendererMode, arrayList);
        buildAudioRenderers(this.context, drmSessionManager4, buildAudioProcessors(), handler, audioRendererEventListener, this.extensionRendererMode, arrayList);
        ArrayList arrayList2 = arrayList;
        buildTextRenderers(this.context, textOutput, handler.getLooper(), this.extensionRendererMode, arrayList2);
        buildMetadataRenderers(this.context, metadataOutput, handler.getLooper(), this.extensionRendererMode, arrayList2);
        Handler handler2 = handler;
        buildMiscellaneousRenderers(this.context, handler, this.extensionRendererMode, arrayList);
        return (Renderer[]) arrayList.toArray(new Renderer[arrayList.size()]);
    }
}
