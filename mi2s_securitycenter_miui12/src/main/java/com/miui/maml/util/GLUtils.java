package com.miui.maml.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class GLUtils {
    public static Buffer buildBuffer(byte[] bArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(bArr.length);
        allocateDirect.order(ByteOrder.nativeOrder());
        allocateDirect.put(bArr);
        allocateDirect.position(0);
        return allocateDirect;
    }

    public static Buffer buildBuffer(char[] cArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(cArr.length);
        allocateDirect.order(ByteOrder.nativeOrder());
        CharBuffer asCharBuffer = allocateDirect.asCharBuffer();
        asCharBuffer.put(cArr);
        asCharBuffer.position(0);
        return asCharBuffer;
    }

    public static Buffer buildBuffer(double[] dArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(dArr.length * 8);
        allocateDirect.order(ByteOrder.nativeOrder());
        DoubleBuffer asDoubleBuffer = allocateDirect.asDoubleBuffer();
        asDoubleBuffer.put(dArr);
        asDoubleBuffer.position(0);
        return asDoubleBuffer;
    }

    public static Buffer buildBuffer(float[] fArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        asFloatBuffer.put(fArr);
        asFloatBuffer.position(0);
        return asFloatBuffer;
    }

    public static Buffer buildBuffer(int[] iArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(iArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        IntBuffer asIntBuffer = allocateDirect.asIntBuffer();
        asIntBuffer.put(iArr);
        asIntBuffer.position(0);
        return asIntBuffer;
    }

    public static Buffer buildBuffer(long[] jArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(jArr.length * 8);
        allocateDirect.order(ByteOrder.nativeOrder());
        LongBuffer asLongBuffer = allocateDirect.asLongBuffer();
        asLongBuffer.put(jArr);
        asLongBuffer.position(0);
        return asLongBuffer;
    }

    public static Buffer buildBuffer(short[] sArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(sArr.length * 2);
        allocateDirect.order(ByteOrder.nativeOrder());
        ShortBuffer asShortBuffer = allocateDirect.asShortBuffer();
        asShortBuffer.put(sArr);
        asShortBuffer.position(0);
        return asShortBuffer;
    }
}
