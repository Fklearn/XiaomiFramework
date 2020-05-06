package com.miui.gamebooster.encoder;

import android.util.Log;

public class SoundSupport {
    private static final String TAG = "SoundSupport";
    private long handle = 0;

    static {
        try {
            System.loadLibrary("jni_sound_effect");
        } catch (Error e) {
            Log.e(TAG, "SoundSupport load lib failed", e);
        }
    }

    public SoundSupport(int i, int i2) {
        this.handle = nativeNewInstance(i, i2);
    }

    public static native void nativeFlush(long j);

    public static native long nativeNewInstance(int i, int i2);

    public static native void nativePutSamples(long j, short[] sArr);

    public static native short[] nativeReceiveSamples(long j, int i);

    public static native void nativeRelease(long j);

    public static native void nativeSetMode(long j, float f);

    public static native void nativeSetStrechRatio(long j, float f);

    public void flush() {
        nativeFlush(this.handle);
    }

    public void putSamples(short[] sArr) {
        nativePutSamples(this.handle, sArr);
    }

    public short[] receiveSamples(int i) {
        return nativeReceiveSamples(this.handle, i);
    }

    public void release() {
        nativeRelease(this.handle);
        this.handle = 0;
    }

    public void setMode(float f) {
        nativeSetMode(this.handle, f);
    }

    public void setStrechRatio(float f) {
        nativeSetStrechRatio(this.handle, f);
    }
}
