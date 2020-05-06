package com.android.server.wm;

import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.function.Predicate;

class WindowTraceBuffer {
    private static final long MAGIC_NUMBER_VALUE = 4990904633914181975L;
    private final Queue<ProtoOutputStream> mBuffer = new ArrayDeque();
    private int mBufferCapacity;
    private final Object mBufferLock = new Object();
    private int mBufferUsedSize;

    WindowTraceBuffer(int bufferCapacity) {
        this.mBufferCapacity = bufferCapacity;
        resetBuffer();
    }

    /* access modifiers changed from: package-private */
    public int getAvailableSpace() {
        return this.mBufferCapacity - this.mBufferUsedSize;
    }

    /* access modifiers changed from: package-private */
    public int size() {
        return this.mBuffer.size();
    }

    /* access modifiers changed from: package-private */
    public void setCapacity(int capacity) {
        this.mBufferCapacity = capacity;
    }

    /* access modifiers changed from: package-private */
    public void add(ProtoOutputStream proto) {
        int protoLength = proto.getRawSize();
        if (protoLength <= this.mBufferCapacity) {
            synchronized (this.mBufferLock) {
                discardOldest(protoLength);
                this.mBuffer.add(proto);
                this.mBufferUsedSize += protoLength;
                this.mBufferLock.notify();
            }
            return;
        }
        throw new IllegalStateException("Trace object too large for the buffer. Buffer size:" + this.mBufferCapacity + " Object size: " + protoLength);
    }

    /* access modifiers changed from: package-private */
    public boolean contains(byte[] other) {
        return this.mBuffer.stream().anyMatch(new Predicate(other) {
            private final /* synthetic */ byte[] f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return Arrays.equals(((ProtoOutputStream) obj).getBytes(), this.f$0);
            }
        });
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0053, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0057, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeTraceToFile(java.io.File r8) throws java.io.IOException {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mBufferLock
            monitor-enter(r0)
            r8.delete()     // Catch:{ all -> 0x0058 }
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x0058 }
            r1.<init>(r8)     // Catch:{ all -> 0x0058 }
            r2 = 1
            r3 = 0
            r8.setReadable(r2, r3)     // Catch:{ all -> 0x004c }
            android.util.proto.ProtoOutputStream r2 = new android.util.proto.ProtoOutputStream     // Catch:{ all -> 0x004c }
            r2.<init>()     // Catch:{ all -> 0x004c }
            r3 = 1125281431553(0x10600000001, double:5.559628972334E-312)
            r5 = 4990904633914181975(0x45434152544e4957, double:4.655612620390422E25)
            r2.write(r3, r5)     // Catch:{ all -> 0x004c }
            byte[] r3 = r2.getBytes()     // Catch:{ all -> 0x004c }
            r1.write(r3)     // Catch:{ all -> 0x004c }
            java.util.Queue<android.util.proto.ProtoOutputStream> r3 = r7.mBuffer     // Catch:{ all -> 0x004c }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x004c }
        L_0x002f:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x004c }
            if (r4 == 0) goto L_0x0044
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x004c }
            android.util.proto.ProtoOutputStream r4 = (android.util.proto.ProtoOutputStream) r4     // Catch:{ all -> 0x004c }
            r2 = r4
            byte[] r5 = r2.getBytes()     // Catch:{ all -> 0x004c }
            r1.write(r5)     // Catch:{ all -> 0x004c }
            goto L_0x002f
        L_0x0044:
            r1.flush()     // Catch:{ all -> 0x004c }
            r1.close()     // Catch:{ all -> 0x0058 }
            monitor-exit(r0)     // Catch:{ all -> 0x0058 }
            return
        L_0x004c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x004e }
        L_0x004e:
            r3 = move-exception
            r1.close()     // Catch:{ all -> 0x0053 }
            goto L_0x0057
        L_0x0053:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ all -> 0x0058 }
        L_0x0057:
            throw r3     // Catch:{ all -> 0x0058 }
        L_0x0058:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0058 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowTraceBuffer.writeTraceToFile(java.io.File):void");
    }

    private void discardOldest(int protoLength) {
        long availableSpace = (long) getAvailableSpace();
        while (availableSpace < ((long) protoLength)) {
            ProtoOutputStream item = this.mBuffer.poll();
            if (item != null) {
                this.mBufferUsedSize -= item.getRawSize();
                availableSpace = (long) getAvailableSpace();
            } else {
                throw new IllegalStateException("No element to discard from buffer");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void resetBuffer() {
        synchronized (this.mBufferLock) {
            this.mBuffer.clear();
            this.mBufferUsedSize = 0;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getBufferSize() {
        return this.mBufferUsedSize;
    }

    /* access modifiers changed from: package-private */
    public String getStatus() {
        String str;
        synchronized (this.mBufferLock) {
            str = "Buffer size: " + this.mBufferCapacity + " bytes\nBuffer usage: " + this.mBufferUsedSize + " bytes\nElements in the buffer: " + this.mBuffer.size();
        }
        return str;
    }
}
