package com.google.protobuf;

import android.hardware.wifi.supplicant.V1_0.ISupplicantStaIfaceCallback;
import com.android.server.wifi.WifiConfigManager;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

final class Utf8 {
    private static final long ASCII_MASK_LONG = -9187201950435737472L;
    public static final int COMPLETE = 0;
    public static final int MALFORMED = -1;
    static final int MAX_BYTES_PER_CHAR = 3;
    private static final int UNSAFE_COUNT_ASCII_THRESHOLD = 16;
    /* access modifiers changed from: private */
    public static final Logger logger = Logger.getLogger(Utf8.class.getName());
    private static final Processor processor = (UnsafeProcessor.isAvailable() ? new UnsafeProcessor() : new SafeProcessor());

    public static boolean isValidUtf8(byte[] bytes) {
        return processor.isValidUtf8(bytes, 0, bytes.length);
    }

    public static boolean isValidUtf8(byte[] bytes, int index, int limit) {
        return processor.isValidUtf8(bytes, index, limit);
    }

    public static int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
        return processor.partialIsValidUtf8(state, bytes, index, limit);
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(int byte1) {
        if (byte1 > -12) {
            return -1;
        }
        return byte1;
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(int byte1, int byte2) {
        if (byte1 > -12 || byte2 > -65) {
            return -1;
        }
        return (byte2 << 8) ^ byte1;
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(int byte1, int byte2, int byte3) {
        if (byte1 > -12 || byte2 > -65 || byte3 > -65) {
            return -1;
        }
        return ((byte2 << 8) ^ byte1) ^ (byte3 << 16);
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(byte[] bytes, int index, int limit) {
        byte byte1 = bytes[index - 1];
        int i = limit - index;
        if (i == 0) {
            return incompleteStateFor(byte1);
        }
        if (i == 1) {
            return incompleteStateFor(byte1, bytes[index]);
        }
        if (i == 2) {
            return incompleteStateFor((int) byte1, (int) bytes[index], (int) bytes[index + 1]);
        }
        throw new AssertionError();
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(ByteBuffer buffer, int byte1, int index, int remaining) {
        if (remaining == 0) {
            return incompleteStateFor(byte1);
        }
        if (remaining == 1) {
            return incompleteStateFor(byte1, buffer.get(index));
        }
        if (remaining == 2) {
            return incompleteStateFor(byte1, (int) buffer.get(index), (int) buffer.get(index + 1));
        }
        throw new AssertionError();
    }

    static class UnpairedSurrogateException extends IllegalArgumentException {
        private UnpairedSurrogateException(int index, int length) {
            super("Unpaired surrogate at index " + index + " of " + length);
        }
    }

    static int encodedLength(CharSequence sequence) {
        int utf16Length = sequence.length();
        int utf8Length = utf16Length;
        int i = 0;
        while (i < utf16Length && sequence.charAt(i) < 128) {
            i++;
        }
        while (true) {
            if (i < utf16Length) {
                char c = sequence.charAt(i);
                if (c >= 2048) {
                    utf8Length += encodedLengthGeneral(sequence, i);
                    break;
                }
                utf8Length += (127 - c) >>> 31;
                i++;
            } else {
                break;
            }
        }
        if (utf8Length >= utf16Length) {
            return utf8Length;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (((long) utf8Length) + 4294967296L));
    }

    private static int encodedLengthGeneral(CharSequence sequence, int start) {
        int utf16Length = sequence.length();
        int utf8Length = 0;
        int i = start;
        while (i < utf16Length) {
            char c = sequence.charAt(i);
            if (c < 2048) {
                utf8Length += (127 - c) >>> 31;
            } else {
                utf8Length += 2;
                if (55296 <= c && c <= 57343) {
                    if (Character.codePointAt(sequence, i) >= 65536) {
                        i++;
                    } else {
                        throw new UnpairedSurrogateException(i, utf16Length);
                    }
                }
            }
            i++;
        }
        return utf8Length;
    }

    static int encode(CharSequence in, byte[] out, int offset, int length) {
        return processor.encodeUtf8(in, out, offset, length);
    }

    static boolean isValidUtf8(ByteBuffer buffer) {
        return processor.isValidUtf8(buffer, buffer.position(), buffer.remaining());
    }

    static int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
        return processor.partialIsValidUtf8(state, buffer, index, limit);
    }

    static void encodeUtf8(CharSequence in, ByteBuffer out) {
        processor.encodeUtf8(in, out);
    }

    /* access modifiers changed from: private */
    public static int estimateConsecutiveAscii(ByteBuffer buffer, int index, int limit) {
        int i = index;
        int lim = limit - 7;
        while (i < lim && (buffer.getLong(i) & ASCII_MASK_LONG) == 0) {
            i += 8;
        }
        return i - index;
    }

    static abstract class Processor {
        /* access modifiers changed from: package-private */
        public abstract int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2);

        /* access modifiers changed from: package-private */
        public abstract void encodeUtf8Direct(CharSequence charSequence, ByteBuffer byteBuffer);

        /* access modifiers changed from: package-private */
        public abstract int partialIsValidUtf8(int i, byte[] bArr, int i2, int i3);

        /* access modifiers changed from: package-private */
        public abstract int partialIsValidUtf8Direct(int i, ByteBuffer byteBuffer, int i2, int i3);

        Processor() {
        }

        /* access modifiers changed from: package-private */
        public final boolean isValidUtf8(byte[] bytes, int index, int limit) {
            return partialIsValidUtf8(0, bytes, index, limit) == 0;
        }

        /* access modifiers changed from: package-private */
        public final boolean isValidUtf8(ByteBuffer buffer, int index, int limit) {
            return partialIsValidUtf8(0, buffer, index, limit) == 0;
        }

        /* access modifiers changed from: package-private */
        public final int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
            if (buffer.hasArray()) {
                int offset = buffer.arrayOffset();
                return partialIsValidUtf8(state, buffer.array(), offset + index, offset + limit);
            } else if (buffer.isDirect() != 0) {
                return partialIsValidUtf8Direct(state, buffer, index, limit);
            } else {
                return partialIsValidUtf8Default(state, buffer, index, limit);
            }
        }

        /* access modifiers changed from: package-private */
        public final int partialIsValidUtf8Default(int state, ByteBuffer buffer, int index, int limit) {
            int index2;
            if (state == 0) {
                index2 = index;
            } else if (index >= limit) {
                return state;
            } else {
                byte byte1 = (byte) state;
                if (byte1 < -32) {
                    if (byte1 >= -62) {
                        index2 = index + 1;
                        if (buffer.get(index) > -65) {
                            int i = index2;
                        }
                    }
                    return -1;
                } else if (byte1 < -16) {
                    byte byte2 = (byte) (~(state >> 8));
                    if (byte2 == 0) {
                        int index3 = index + 1;
                        byte2 = buffer.get(index);
                        if (index3 >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                        index = index3;
                    }
                    if (byte2 <= -65 && ((byte1 != -32 || byte2 >= -96) && (byte1 != -19 || byte2 < -96))) {
                        index2 = index + 1;
                        if (buffer.get(index) > -65) {
                            int i2 = index2;
                        }
                    }
                    return -1;
                } else {
                    byte byte22 = (byte) (~(state >> 8));
                    byte byte3 = 0;
                    if (byte22 == 0) {
                        int index4 = index + 1;
                        byte22 = buffer.get(index);
                        if (index4 >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte22);
                        }
                        index = index4;
                    } else {
                        byte3 = (byte) (state >> 16);
                    }
                    if (byte3 == 0) {
                        int index5 = index + 1;
                        byte3 = buffer.get(index);
                        if (index5 >= limit) {
                            return Utf8.incompleteStateFor((int) byte1, (int) byte22, (int) byte3);
                        }
                        index = index5;
                    }
                    if (byte22 <= -65 && (((byte1 << 28) + (byte22 + 112)) >> 30) == 0 && byte3 <= -65) {
                        int index6 = index + 1;
                        if (buffer.get(index) > -65) {
                            int i3 = index6;
                        } else {
                            index2 = index6;
                        }
                    }
                    return -1;
                }
            }
            return partialIsValidUtf8(buffer, index2, limit);
        }

        private static int partialIsValidUtf8(ByteBuffer buffer, int index, int limit) {
            int index2 = index + Utf8.estimateConsecutiveAscii(buffer, index, limit);
            while (index2 < limit) {
                int index3 = index2 + 1;
                int index4 = buffer.get(index2);
                int byte1 = index4;
                if (index4 >= 0) {
                    index2 = index3;
                } else if (byte1 < -32) {
                    if (index3 >= limit) {
                        return byte1;
                    }
                    if (byte1 < -62 || buffer.get(index3) > -65) {
                        return -1;
                    }
                    index2 = index3 + 1;
                } else if (byte1 < -16) {
                    if (index3 >= limit - 1) {
                        return Utf8.incompleteStateFor(buffer, byte1, index3, limit - index3);
                    }
                    int index5 = index3 + 1;
                    byte byte2 = buffer.get(index3);
                    if (byte2 > -65 || ((byte1 == -32 && byte2 < -96) || ((byte1 == -19 && byte2 >= -96) || buffer.get(index5) > -65))) {
                        return -1;
                    }
                    index2 = index5 + 1;
                } else if (index3 >= limit - 2) {
                    return Utf8.incompleteStateFor(buffer, byte1, index3, limit - index3);
                } else {
                    int index6 = index3 + 1;
                    int byte22 = buffer.get(index3);
                    if (byte22 <= -65 && (((byte1 << 28) + (byte22 + ISupplicantStaIfaceCallback.StatusCode.FILS_AUTHENTICATION_FAILURE)) >> 30) == 0) {
                        int index7 = index6 + 1;
                        if (buffer.get(index6) <= -65) {
                            index2 = index7 + 1;
                            if (buffer.get(index7) > -65) {
                            }
                        }
                    }
                    return -1;
                }
            }
            return 0;
        }

        /* access modifiers changed from: package-private */
        public final void encodeUtf8(CharSequence in, ByteBuffer out) {
            if (out.hasArray()) {
                int offset = out.arrayOffset();
                out.position(Utf8.encode(in, out.array(), out.position() + offset, out.remaining()) - offset);
            } else if (out.isDirect()) {
                encodeUtf8Direct(in, out);
            } else {
                encodeUtf8Default(in, out);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        /* access modifiers changed from: package-private */
        public final void encodeUtf8Default(CharSequence in, ByteBuffer out) {
            int outIx;
            int inLength = in.length();
            int outIx2 = out.position();
            int inIx = 0;
            while (inIx < inLength) {
                try {
                    char charAt = in.charAt(inIx);
                    char c = charAt;
                    if (charAt >= 128) {
                        break;
                    }
                    out.put(outIx2 + inIx, (byte) c);
                    inIx++;
                } catch (IndexOutOfBoundsException e) {
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + in.charAt(inIx) + " at index " + (out.position() + Math.max(inIx, (outIx2 - out.position()) + 1)));
                }
            }
            if (inIx == inLength) {
                out.position(outIx2 + inIx);
                return;
            }
            outIx2 += inIx;
            while (inIx < inLength) {
                char c2 = in.charAt(inIx);
                if (c2 < 128) {
                    out.put(outIx2, (byte) c2);
                } else if (c2 < 2048) {
                    outIx = outIx2 + 1;
                    try {
                        out.put(outIx2, (byte) ((c2 >>> 6) | WifiConfigManager.SCAN_CACHE_ENTRIES_MAX_SIZE));
                        out.put(outIx, (byte) ((c2 & '?') | 128));
                        outIx2 = outIx;
                    } catch (IndexOutOfBoundsException e2) {
                        outIx2 = outIx;
                        throw new ArrayIndexOutOfBoundsException("Failed writing " + in.charAt(inIx) + " at index " + (out.position() + Math.max(inIx, (outIx2 - out.position()) + 1)));
                    }
                } else if (c2 < 55296 || 57343 < c2) {
                    outIx = outIx2 + 1;
                    out.put(outIx2, (byte) ((c2 >>> 12) | 224));
                    outIx2 = outIx + 1;
                    out.put(outIx, (byte) (((c2 >>> 6) & 63) | 128));
                    out.put(outIx2, (byte) ((c2 & '?') | 128));
                } else {
                    if (inIx + 1 != inLength) {
                        inIx++;
                        char charAt2 = in.charAt(inIx);
                        char low = charAt2;
                        if (Character.isSurrogatePair(c2, charAt2)) {
                            int codePoint = Character.toCodePoint(c2, low);
                            int outIx3 = outIx2 + 1;
                            try {
                                out.put(outIx2, (byte) ((codePoint >>> 18) | 240));
                                outIx2 = outIx3 + 1;
                                out.put(outIx3, (byte) (((codePoint >>> 12) & 63) | 128));
                                outIx3 = outIx2 + 1;
                                out.put(outIx2, (byte) (((codePoint >>> 6) & 63) | 128));
                                out.put(outIx3, (byte) ((codePoint & 63) | 128));
                                outIx2 = outIx3;
                            } catch (IndexOutOfBoundsException e3) {
                                outIx2 = outIx3;
                                throw new ArrayIndexOutOfBoundsException("Failed writing " + in.charAt(inIx) + " at index " + (out.position() + Math.max(inIx, (outIx2 - out.position()) + 1)));
                            }
                        }
                    }
                    throw new UnpairedSurrogateException(inIx, inLength);
                }
                inIx++;
                outIx2++;
            }
            out.position(outIx2);
        }
    }

    static final class SafeProcessor extends Processor {
        SafeProcessor() {
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r1v8, types: [byte, int] */
        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r4v10, types: [byte, int] */
        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r4v4, types: [byte, int] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int partialIsValidUtf8(int r8, byte[] r9, int r10, int r11) {
            /*
                r7 = this;
                if (r8 == 0) goto L_0x0084
                if (r10 < r11) goto L_0x0005
                return r8
            L_0x0005:
                byte r0 = (byte) r8
                r1 = -32
                r2 = -1
                r3 = -65
                if (r0 >= r1) goto L_0x0019
                r1 = -62
                if (r0 < r1) goto L_0x0018
                int r1 = r10 + 1
                byte r10 = r9[r10]
                if (r10 <= r3) goto L_0x0085
                r10 = r1
            L_0x0018:
                return r2
            L_0x0019:
                r4 = -16
                if (r0 >= r4) goto L_0x0047
                int r4 = r8 >> 8
                int r4 = ~r4
                byte r4 = (byte) r4
                if (r4 != 0) goto L_0x002f
                int r5 = r10 + 1
                byte r4 = r9[r10]
                if (r5 < r11) goto L_0x002e
                int r10 = com.google.protobuf.Utf8.incompleteStateFor(r0, r4)
                return r10
            L_0x002e:
                r10 = r5
            L_0x002f:
                if (r4 > r3) goto L_0x0046
                r5 = -96
                if (r0 != r1) goto L_0x0037
                if (r4 < r5) goto L_0x0046
            L_0x0037:
                r1 = -19
                if (r0 != r1) goto L_0x003d
                if (r4 >= r5) goto L_0x0046
            L_0x003d:
                int r1 = r10 + 1
                byte r10 = r9[r10]
                if (r10 <= r3) goto L_0x0045
                r10 = r1
                goto L_0x0046
            L_0x0045:
                goto L_0x0085
            L_0x0046:
                return r2
            L_0x0047:
                int r1 = r8 >> 8
                int r1 = ~r1
                byte r1 = (byte) r1
                r4 = 0
                if (r1 != 0) goto L_0x005b
                int r5 = r10 + 1
                byte r1 = r9[r10]
                if (r5 < r11) goto L_0x0059
                int r10 = com.google.protobuf.Utf8.incompleteStateFor(r0, r1)
                return r10
            L_0x0059:
                r10 = r5
                goto L_0x005e
            L_0x005b:
                int r5 = r8 >> 16
                byte r4 = (byte) r5
            L_0x005e:
                if (r4 != 0) goto L_0x006c
                int r5 = r10 + 1
                byte r4 = r9[r10]
                if (r5 < r11) goto L_0x006b
                int r10 = com.google.protobuf.Utf8.incompleteStateFor((int) r0, (int) r1, (int) r4)
                return r10
            L_0x006b:
                r10 = r5
            L_0x006c:
                if (r1 > r3) goto L_0x0083
                int r5 = r0 << 28
                int r6 = r1 + 112
                int r5 = r5 + r6
                int r5 = r5 >> 30
                if (r5 != 0) goto L_0x0083
                if (r4 > r3) goto L_0x0083
                int r5 = r10 + 1
                byte r10 = r9[r10]
                if (r10 <= r3) goto L_0x0081
                r10 = r5
                goto L_0x0083
            L_0x0081:
                r1 = r5
                goto L_0x0085
            L_0x0083:
                return r2
            L_0x0084:
                r1 = r10
            L_0x0085:
                int r10 = partialIsValidUtf8(r9, r1, r11)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.SafeProcessor.partialIsValidUtf8(int, byte[], int, int):int");
        }

        /* access modifiers changed from: package-private */
        public int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
            return partialIsValidUtf8Default(state, buffer, index, limit);
        }

        /* access modifiers changed from: package-private */
        public int encodeUtf8(CharSequence in, byte[] out, int offset, int length) {
            int utf16Length = in.length();
            int j = offset;
            int i = 0;
            int limit = offset + length;
            while (i < utf16Length && i + j < limit) {
                char charAt = in.charAt(i);
                char c = charAt;
                if (charAt >= 128) {
                    break;
                }
                out[j + i] = (byte) c;
                i++;
            }
            if (i == utf16Length) {
                return j + utf16Length;
            }
            int j2 = j + i;
            while (i < utf16Length) {
                char c2 = in.charAt(i);
                if (c2 < 128 && j2 < limit) {
                    out[j2] = (byte) c2;
                    j2++;
                } else if (c2 < 2048 && j2 <= limit - 2) {
                    int j3 = j2 + 1;
                    out[j2] = (byte) ((c2 >>> 6) | 960);
                    j2 = j3 + 1;
                    out[j3] = (byte) ((c2 & '?') | 128);
                } else if ((c2 < 55296 || 57343 < c2) && j2 <= limit - 3) {
                    int j4 = j2 + 1;
                    out[j2] = (byte) ((c2 >>> 12) | 480);
                    int j5 = j4 + 1;
                    out[j4] = (byte) (((c2 >>> 6) & 63) | 128);
                    out[j5] = (byte) ((c2 & '?') | 128);
                    j2 = j5 + 1;
                } else if (j2 <= limit - 4) {
                    if (i + 1 != in.length()) {
                        i++;
                        char charAt2 = in.charAt(i);
                        char low = charAt2;
                        if (Character.isSurrogatePair(c2, charAt2)) {
                            int codePoint = Character.toCodePoint(c2, low);
                            int j6 = j2 + 1;
                            out[j2] = (byte) ((codePoint >>> 18) | 240);
                            int j7 = j6 + 1;
                            out[j6] = (byte) (((codePoint >>> 12) & 63) | 128);
                            int j8 = j7 + 1;
                            out[j7] = (byte) (((codePoint >>> 6) & 63) | 128);
                            j2 = j8 + 1;
                            out[j8] = (byte) ((codePoint & 63) | 128);
                        }
                    }
                    throw new UnpairedSurrogateException(i - 1, utf16Length);
                } else if (55296 > c2 || c2 > 57343 || (i + 1 != in.length() && Character.isSurrogatePair(c2, in.charAt(i + 1)))) {
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + c2 + " at index " + j2);
                } else {
                    throw new UnpairedSurrogateException(i, utf16Length);
                }
                i++;
            }
            return j2;
        }

        /* access modifiers changed from: package-private */
        public void encodeUtf8Direct(CharSequence in, ByteBuffer out) {
            encodeUtf8Default(in, out);
        }

        private static int partialIsValidUtf8(byte[] bytes, int index, int limit) {
            while (index < limit && bytes[index] >= 0) {
                index++;
            }
            if (index >= limit) {
                return 0;
            }
            return partialIsValidUtf8NonAscii(bytes, index, limit);
        }

        private static int partialIsValidUtf8NonAscii(byte[] bytes, int index, int limit) {
            while (index < limit) {
                int index2 = index + 1;
                byte index3 = bytes[index];
                int byte1 = index3;
                if (index3 >= 0) {
                    index = index2;
                } else if (byte1 < -32) {
                    if (index2 >= limit) {
                        return byte1;
                    }
                    if (byte1 >= -62) {
                        index = index2 + 1;
                        if (bytes[index2] > -65) {
                        }
                    }
                    return -1;
                } else if (byte1 < -16) {
                    if (index2 >= limit - 1) {
                        return Utf8.incompleteStateFor(bytes, index2, limit);
                    }
                    int index4 = index2 + 1;
                    byte index5 = bytes[index2];
                    int byte2 = index5;
                    if (index5 > -65 || ((byte1 == -32 && byte2 < -96) || (byte1 == -19 && byte2 >= -96))) {
                    } else {
                        index = index4 + 1;
                        if (bytes[index4] > -65) {
                        }
                    }
                    return -1;
                } else if (index2 >= limit - 2) {
                    return Utf8.incompleteStateFor(bytes, index2, limit);
                } else {
                    int index6 = index2 + 1;
                    byte index7 = bytes[index2];
                    int byte22 = index7;
                    if (index7 <= -65 && (((byte1 << 28) + (byte22 + ISupplicantStaIfaceCallback.StatusCode.FILS_AUTHENTICATION_FAILURE)) >> 30) == 0) {
                        int index8 = index6 + 1;
                        if (bytes[index6] <= -65) {
                            index = index8 + 1;
                            if (bytes[index8] > -65) {
                            }
                        }
                    }
                    return -1;
                }
            }
            return 0;
        }
    }

    static final class UnsafeProcessor extends Processor {
        private static final int ARRAY_BASE_OFFSET = byteArrayBaseOffset();
        private static final boolean AVAILABLE = (BUFFER_ADDRESS_OFFSET != -1 && ARRAY_BASE_OFFSET % 8 == 0);
        private static final long BUFFER_ADDRESS_OFFSET = fieldOffset(field(Buffer.class, "address"));
        private static final Unsafe UNSAFE = getUnsafe();

        UnsafeProcessor() {
        }

        static boolean isAvailable() {
            return AVAILABLE;
        }

        /* access modifiers changed from: package-private */
        public int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
            long offset;
            long offset2;
            long offset3;
            int i = state;
            byte[] bArr = bytes;
            if ((index | limit | (bArr.length - limit)) >= 0) {
                int i2 = ARRAY_BASE_OFFSET;
                long offset4 = (long) (i2 + index);
                long offsetLimit = (long) (i2 + limit);
                if (i == 0) {
                    offset = offset4;
                } else if (offset4 >= offsetLimit) {
                    return i;
                } else {
                    int byte1 = (byte) i;
                    if (byte1 < -32) {
                        if (byte1 >= -62) {
                            offset = 1 + offset4;
                            if (UNSAFE.getByte(bArr, offset4) > -65) {
                                long j = offset;
                            }
                        }
                        return -1;
                    } else if (byte1 < -16) {
                        int byte2 = (byte) (~(i >> 8));
                        if (byte2 == 0) {
                            offset3 = offset4 + 1;
                            byte2 = UNSAFE.getByte(bArr, offset4);
                            if (offset3 >= offsetLimit) {
                                return Utf8.incompleteStateFor(byte1, byte2);
                            }
                        } else {
                            offset3 = offset4;
                        }
                        if (byte2 <= -65 && ((byte1 != -32 || byte2 >= -96) && (byte1 != -19 || byte2 < -96))) {
                            offset = 1 + offset3;
                            if (UNSAFE.getByte(bArr, offset3) > -65) {
                                long j2 = offset;
                            }
                        }
                        return -1;
                    } else {
                        int byte22 = (byte) (~(i >> 8));
                        int byte3 = 0;
                        if (byte22 == 0) {
                            offset2 = offset4 + 1;
                            byte22 = UNSAFE.getByte(bArr, offset4);
                            if (offset2 >= offsetLimit) {
                                return Utf8.incompleteStateFor(byte1, byte22);
                            }
                        } else {
                            byte3 = (byte) (i >> 16);
                            offset2 = offset4;
                        }
                        if (byte3 == 0) {
                            long offset5 = offset2 + 1;
                            byte3 = UNSAFE.getByte(bArr, offset2);
                            if (offset5 >= offsetLimit) {
                                return Utf8.incompleteStateFor(byte1, byte22, byte3);
                            }
                            offset2 = offset5;
                        }
                        if (byte22 <= -65 && (((byte1 << 28) + (byte22 + ISupplicantStaIfaceCallback.StatusCode.FILS_AUTHENTICATION_FAILURE)) >> 30) == 0 && byte3 <= -65) {
                            offset = 1 + offset2;
                            if (UNSAFE.getByte(bArr, offset2) > -65) {
                                long j3 = offset;
                            }
                        }
                        return -1;
                    }
                }
                return partialIsValidUtf8(bArr, offset, (int) (offsetLimit - offset));
            }
            throw new ArrayIndexOutOfBoundsException(String.format("Array length=%d, index=%d, limit=%d", new Object[]{Integer.valueOf(bArr.length), Integer.valueOf(index), Integer.valueOf(limit)}));
        }

        /* access modifiers changed from: package-private */
        public int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
            long address;
            long address2;
            long address3;
            int i = state;
            int i2 = index;
            if ((i2 | limit | (buffer.limit() - limit)) >= 0) {
                long address4 = addressOffset(buffer) + ((long) i2);
                long addressLimit = ((long) (limit - i2)) + address4;
                if (i == 0) {
                    address = address4;
                } else if (address4 >= addressLimit) {
                    return i;
                } else {
                    int byte1 = (byte) i;
                    if (byte1 < -32) {
                        if (byte1 >= -62) {
                            address = 1 + address4;
                            if (UNSAFE.getByte(address4) > -65) {
                                long j = address;
                            }
                        }
                        return -1;
                    } else if (byte1 < -16) {
                        int byte2 = (byte) (~(i >> 8));
                        if (byte2 == 0) {
                            address3 = address4 + 1;
                            byte2 = UNSAFE.getByte(address4);
                            if (address3 >= addressLimit) {
                                return Utf8.incompleteStateFor(byte1, byte2);
                            }
                        } else {
                            address3 = address4;
                        }
                        if (byte2 <= -65 && ((byte1 != -32 || byte2 >= -96) && (byte1 != -19 || byte2 < -96))) {
                            address = 1 + address3;
                            if (UNSAFE.getByte(address3) > -65) {
                                long j2 = address;
                            }
                        }
                        return -1;
                    } else {
                        int byte22 = (byte) (~(i >> 8));
                        int byte3 = 0;
                        if (byte22 == 0) {
                            address2 = address4 + 1;
                            byte22 = UNSAFE.getByte(address4);
                            if (address2 >= addressLimit) {
                                return Utf8.incompleteStateFor(byte1, byte22);
                            }
                        } else {
                            byte3 = (byte) (i >> 16);
                            address2 = address4;
                        }
                        if (byte3 == 0) {
                            long address5 = address2 + 1;
                            byte3 = UNSAFE.getByte(address2);
                            if (address5 >= addressLimit) {
                                return Utf8.incompleteStateFor(byte1, byte22, byte3);
                            }
                            address2 = address5;
                        }
                        if (byte22 <= -65 && (((byte1 << 28) + (byte22 + ISupplicantStaIfaceCallback.StatusCode.FILS_AUTHENTICATION_FAILURE)) >> 30) == 0 && byte3 <= -65) {
                            address = 1 + address2;
                            if (UNSAFE.getByte(address2) > -65) {
                                long j3 = address;
                            }
                        }
                        return -1;
                    }
                }
                return partialIsValidUtf8(address, (int) (addressLimit - address));
            }
            throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", new Object[]{Integer.valueOf(buffer.limit()), Integer.valueOf(index), Integer.valueOf(limit)}));
        }

        /* access modifiers changed from: package-private */
        public int encodeUtf8(CharSequence in, byte[] out, int offset, int length) {
            long outIx;
            char c;
            long j;
            long j2;
            long outLimit;
            String str;
            String str2;
            char c2;
            CharSequence charSequence = in;
            byte[] bArr = out;
            int i = offset;
            int i2 = length;
            long outIx2 = (long) (ARRAY_BASE_OFFSET + i);
            long outLimit2 = ((long) i2) + outIx2;
            int inLimit = in.length();
            String str3 = " at index ";
            String str4 = "Failed writing ";
            if (inLimit > i2 || bArr.length - i2 < i) {
                long j3 = outLimit2;
                throw new ArrayIndexOutOfBoundsException(str4 + charSequence.charAt(inLimit - 1) + str3 + (offset + length));
            }
            int inIx = 0;
            while (true) {
                c = 128;
                j = 1;
                if (inIx >= inLimit) {
                    break;
                }
                char charAt = charSequence.charAt(inIx);
                char c3 = charAt;
                if (charAt >= 128) {
                    break;
                }
                char c4 = c3;
                UNSAFE.putByte(bArr, outIx, (byte) c4);
                inIx++;
                outIx2 = 1 + outIx;
            }
            if (inIx == inLimit) {
                return (int) (outIx - ((long) ARRAY_BASE_OFFSET));
            }
            while (inIx < inLimit) {
                char c5 = charSequence.charAt(inIx);
                if (c5 < c && outIx < outLimit2) {
                    UNSAFE.putByte(bArr, outIx, (byte) c5);
                    str2 = str3;
                    outIx += j;
                    j2 = 1;
                    outLimit = outLimit2;
                    str = str4;
                    c2 = 128;
                } else if (c5 >= 2048 || outIx > outLimit2 - 2) {
                    if (c5 >= 55296 && 57343 >= c5) {
                        str2 = str3;
                        str = str4;
                    } else if (outIx <= outLimit2 - 3) {
                        str2 = str3;
                        str = str4;
                        long outIx3 = outIx + 1;
                        UNSAFE.putByte(bArr, outIx, (byte) ((c5 >>> 12) | 480));
                        long outIx4 = outIx3 + 1;
                        UNSAFE.putByte(bArr, outIx3, (byte) (((c5 >>> 6) & 63) | 128));
                        UNSAFE.putByte(bArr, outIx4, (byte) ((c5 & '?') | 128));
                        outLimit = outLimit2;
                        outIx = outIx4 + 1;
                        c2 = 128;
                        j2 = 1;
                    } else {
                        str2 = str3;
                        str = str4;
                    }
                    if (outIx <= outLimit2 - 4) {
                        if (inIx + 1 != inLimit) {
                            inIx++;
                            char charAt2 = charSequence.charAt(inIx);
                            char low = charAt2;
                            if (Character.isSurrogatePair(c5, charAt2)) {
                                int codePoint = Character.toCodePoint(c5, low);
                                outLimit = outLimit2;
                                long outLimit3 = outIx + 1;
                                UNSAFE.putByte(bArr, outIx, (byte) ((codePoint >>> 18) | 240));
                                long outIx5 = outLimit3 + 1;
                                UNSAFE.putByte(bArr, outLimit3, (byte) (((codePoint >>> 12) & 63) | 128));
                                long outIx6 = outIx5 + 1;
                                c2 = 128;
                                UNSAFE.putByte(bArr, outIx5, (byte) (((codePoint >>> 6) & 63) | 128));
                                j2 = 1;
                                UNSAFE.putByte(bArr, outIx6, (byte) ((codePoint & 63) | 128));
                                outIx = outIx6 + 1;
                            }
                        }
                        throw new UnpairedSurrogateException(inIx - 1, inLimit);
                    }
                    if (55296 > c5 || c5 > 57343 || (inIx + 1 != inLimit && Character.isSurrogatePair(c5, charSequence.charAt(inIx + 1)))) {
                        throw new ArrayIndexOutOfBoundsException(str + c5 + str2 + outIx);
                    }
                    throw new UnpairedSurrogateException(inIx, inLimit);
                } else {
                    long outIx7 = outIx + 1;
                    UNSAFE.putByte(bArr, outIx, (byte) ((c5 >>> 6) | 960));
                    UNSAFE.putByte(bArr, outIx7, (byte) ((c5 & '?') | 128));
                    str2 = str3;
                    outIx = outIx7 + 1;
                    j2 = 1;
                    outLimit = outLimit2;
                    str = str4;
                    c2 = 128;
                }
                inIx++;
                int i3 = offset;
                int i4 = length;
                c = c2;
                str3 = str2;
                str4 = str;
                outLimit2 = outLimit;
                j = j2;
            }
            return (int) (outIx - ((long) ARRAY_BASE_OFFSET));
        }

        /* access modifiers changed from: package-private */
        public void encodeUtf8Direct(CharSequence in, ByteBuffer out) {
            long outIx;
            char c;
            long j;
            long outIx2;
            long outLimit;
            long outIx3;
            char c2;
            CharSequence charSequence = in;
            ByteBuffer byteBuffer = out;
            long address = addressOffset(out);
            long outIx4 = ((long) out.position()) + address;
            long outLimit2 = ((long) out.limit()) + address;
            int inLimit = in.length();
            if (((long) inLimit) <= outLimit2 - outIx4) {
                int inIx = 0;
                while (true) {
                    c = 128;
                    j = 1;
                    if (inIx >= inLimit) {
                        break;
                    }
                    char charAt = charSequence.charAt(inIx);
                    char c3 = charAt;
                    if (charAt >= 128) {
                        break;
                    }
                    char c4 = c3;
                    UNSAFE.putByte(outIx, (byte) c4);
                    inIx++;
                    outIx4 = 1 + outIx;
                }
                if (inIx == inLimit) {
                    byteBuffer.position((int) (outIx - address));
                    return;
                }
                while (inIx < inLimit) {
                    char c5 = charSequence.charAt(inIx);
                    if (c5 < c && outIx < outLimit2) {
                        UNSAFE.putByte(outIx, (byte) c5);
                        outLimit = outLimit2;
                        outIx += j;
                        c2 = 128;
                        outIx2 = 1;
                        outIx3 = address;
                    } else if (c5 >= 2048 || outIx > outLimit2 - 2) {
                        outIx3 = address;
                        if ((c5 < 55296 || 57343 < c5) && outIx <= outLimit2 - 3) {
                            long outIx5 = outIx + 1;
                            UNSAFE.putByte(outIx, (byte) ((c5 >>> 12) | 480));
                            long outIx6 = outIx5 + 1;
                            UNSAFE.putByte(outIx5, (byte) (((c5 >>> 6) & 63) | 128));
                            UNSAFE.putByte(outIx6, (byte) ((c5 & '?') | 128));
                            outLimit = outLimit2;
                            outIx = outIx6 + 1;
                            c2 = 128;
                            outIx2 = 1;
                        } else if (outIx <= outLimit2 - 4) {
                            if (inIx + 1 != inLimit) {
                                inIx++;
                                char charAt2 = charSequence.charAt(inIx);
                                char low = charAt2;
                                if (Character.isSurrogatePair(c5, charAt2)) {
                                    int codePoint = Character.toCodePoint(c5, low);
                                    outLimit = outLimit2;
                                    long outLimit3 = outIx + 1;
                                    UNSAFE.putByte(outIx, (byte) ((codePoint >>> 18) | 240));
                                    long outIx7 = outLimit3 + 1;
                                    UNSAFE.putByte(outLimit3, (byte) (((codePoint >>> 12) & 63) | 128));
                                    long outIx8 = outIx7 + 1;
                                    c2 = 128;
                                    UNSAFE.putByte(outIx7, (byte) (((codePoint >>> 6) & 63) | 128));
                                    outIx2 = 1;
                                    outIx = outIx8 + 1;
                                    UNSAFE.putByte(outIx8, (byte) ((codePoint & 63) | 128));
                                }
                            }
                            throw new UnpairedSurrogateException(inIx - 1, inLimit);
                        } else {
                            if (55296 > c5 || c5 > 57343 || (inIx + 1 != inLimit && Character.isSurrogatePair(c5, charSequence.charAt(inIx + 1)))) {
                                throw new ArrayIndexOutOfBoundsException("Failed writing " + c5 + " at index " + outIx);
                            }
                            throw new UnpairedSurrogateException(inIx, inLimit);
                        }
                    } else {
                        outIx3 = address;
                        long outIx9 = outIx + 1;
                        UNSAFE.putByte(outIx, (byte) ((c5 >>> 6) | 960));
                        outIx = outIx9 + 1;
                        UNSAFE.putByte(outIx9, (byte) ((c5 & '?') | 128));
                        outLimit = outLimit2;
                        c2 = 128;
                        outIx2 = 1;
                    }
                    inIx++;
                    ByteBuffer byteBuffer2 = out;
                    c = c2;
                    address = outIx3;
                    outLimit2 = outLimit;
                    j = outIx2;
                }
                out.position((int) (outIx - address));
                return;
            }
            long j2 = outLimit2;
            ByteBuffer byteBuffer3 = byteBuffer;
            throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(inLimit - 1) + " at index " + out.limit());
        }

        private static int unsafeEstimateConsecutiveAscii(byte[] bytes, long offset, int maxChars) {
            int remaining = maxChars;
            if (remaining < 16) {
                return 0;
            }
            int unaligned = ((int) offset) & 7;
            int j = unaligned;
            while (j > 0) {
                long offset2 = 1 + offset;
                if (UNSAFE.getByte(bytes, offset) < 0) {
                    return unaligned - j;
                }
                j--;
                offset = offset2;
            }
            int remaining2 = remaining - unaligned;
            while (remaining2 >= 8 && (UNSAFE.getLong(bytes, offset) & Utf8.ASCII_MASK_LONG) == 0) {
                offset += 8;
                remaining2 -= 8;
            }
            return maxChars - remaining2;
        }

        private static int unsafeEstimateConsecutiveAscii(long address, int maxChars) {
            int remaining = maxChars;
            if (remaining < 16) {
                return 0;
            }
            int unaligned = ((int) address) & 7;
            int j = unaligned;
            while (j > 0) {
                long address2 = 1 + address;
                if (UNSAFE.getByte(address) < 0) {
                    return unaligned - j;
                }
                j--;
                address = address2;
            }
            int remaining2 = remaining - unaligned;
            while (remaining2 >= 8 && (UNSAFE.getLong(address) & Utf8.ASCII_MASK_LONG) == 0) {
                address += 8;
                remaining2 -= 8;
            }
            return maxChars - remaining2;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0073, code lost:
            return -1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x00ab, code lost:
            return -1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static int partialIsValidUtf8(byte[] r11, long r12, int r14) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r11, r12, r14)
                int r14 = r14 - r0
                long r1 = (long) r0
                long r12 = r12 + r1
            L_0x0007:
                r1 = 0
            L_0x0008:
                r2 = 1
                if (r14 <= 0) goto L_0x001c
                sun.misc.Unsafe r4 = UNSAFE
                long r5 = r12 + r2
                byte r12 = r4.getByte(r11, r12)
                r1 = r12
                if (r12 < 0) goto L_0x001b
                int r14 = r14 + -1
                r12 = r5
                goto L_0x0008
            L_0x001b:
                r12 = r5
            L_0x001c:
                if (r14 != 0) goto L_0x0020
                r2 = 0
                return r2
            L_0x0020:
                int r14 = r14 + -1
                r4 = -32
                r5 = -65
                r6 = -1
                if (r1 >= r4) goto L_0x0041
                if (r14 != 0) goto L_0x002c
                return r1
            L_0x002c:
                int r14 = r14 + -1
                r4 = -62
                if (r1 < r4) goto L_0x0040
                sun.misc.Unsafe r4 = UNSAFE
                long r2 = r2 + r12
                byte r12 = r4.getByte(r11, r12)
                if (r12 <= r5) goto L_0x003d
                r12 = r2
                goto L_0x0040
            L_0x003d:
                r12 = r2
                goto L_0x00a8
            L_0x0040:
                return r6
            L_0x0041:
                r7 = -16
                if (r1 >= r7) goto L_0x0074
                r7 = 2
                if (r14 >= r7) goto L_0x004d
                int r2 = unsafeIncompleteStateFor(r11, r1, r12, r14)
                return r2
            L_0x004d:
                int r14 = r14 + -2
                sun.misc.Unsafe r7 = UNSAFE
                long r8 = r12 + r2
                byte r12 = r7.getByte(r11, r12)
                r13 = r12
                if (r12 > r5) goto L_0x0072
                r12 = -96
                if (r1 != r4) goto L_0x0060
                if (r13 < r12) goto L_0x0072
            L_0x0060:
                r4 = -19
                if (r1 != r4) goto L_0x0066
                if (r13 >= r12) goto L_0x0072
            L_0x0066:
                sun.misc.Unsafe r12 = UNSAFE
                long r2 = r2 + r8
                byte r12 = r12.getByte(r11, r8)
                if (r12 <= r5) goto L_0x0070
                goto L_0x0073
            L_0x0070:
                r12 = r2
                goto L_0x00a8
            L_0x0072:
                r2 = r8
            L_0x0073:
                return r6
            L_0x0074:
                r4 = 3
                if (r14 >= r4) goto L_0x007c
                int r2 = unsafeIncompleteStateFor(r11, r1, r12, r14)
                return r2
            L_0x007c:
                int r14 = r14 + -3
                sun.misc.Unsafe r4 = UNSAFE
                long r7 = r12 + r2
                byte r12 = r4.getByte(r11, r12)
                r13 = r12
                if (r12 > r5) goto L_0x00ab
                int r12 = r1 << 28
                int r4 = r13 + 112
                int r12 = r12 + r4
                int r12 = r12 >> 30
                if (r12 != 0) goto L_0x00ab
                sun.misc.Unsafe r12 = UNSAFE
                long r9 = r7 + r2
                byte r12 = r12.getByte(r11, r7)
                if (r12 > r5) goto L_0x00aa
                sun.misc.Unsafe r12 = UNSAFE
                long r7 = r9 + r2
                byte r12 = r12.getByte(r11, r9)
                if (r12 <= r5) goto L_0x00a7
                goto L_0x00ab
            L_0x00a7:
                r12 = r7
            L_0x00a8:
                goto L_0x0007
            L_0x00aa:
                r7 = r9
            L_0x00ab:
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(byte[], long, int):int");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0072, code lost:
            return -1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x00a9, code lost:
            return -1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static int partialIsValidUtf8(long r11, int r13) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r11, r13)
                long r1 = (long) r0
                long r11 = r11 + r1
                int r13 = r13 - r0
            L_0x0007:
                r1 = 0
            L_0x0008:
                r2 = 1
                if (r13 <= 0) goto L_0x001c
                sun.misc.Unsafe r4 = UNSAFE
                long r5 = r11 + r2
                byte r11 = r4.getByte(r11)
                r1 = r11
                if (r11 < 0) goto L_0x001b
                int r13 = r13 + -1
                r11 = r5
                goto L_0x0008
            L_0x001b:
                r11 = r5
            L_0x001c:
                if (r13 != 0) goto L_0x0020
                r2 = 0
                return r2
            L_0x0020:
                int r13 = r13 + -1
                r4 = -32
                r5 = -65
                r6 = -1
                if (r1 >= r4) goto L_0x0041
                if (r13 != 0) goto L_0x002c
                return r1
            L_0x002c:
                int r13 = r13 + -1
                r4 = -62
                if (r1 < r4) goto L_0x0040
                sun.misc.Unsafe r4 = UNSAFE
                long r2 = r2 + r11
                byte r11 = r4.getByte(r11)
                if (r11 <= r5) goto L_0x003d
                r11 = r2
                goto L_0x0040
            L_0x003d:
                r11 = r2
                goto L_0x00a6
            L_0x0040:
                return r6
            L_0x0041:
                r7 = -16
                if (r1 >= r7) goto L_0x0073
                r7 = 2
                if (r13 >= r7) goto L_0x004d
                int r2 = unsafeIncompleteStateFor(r11, r1, r13)
                return r2
            L_0x004d:
                int r13 = r13 + -2
                sun.misc.Unsafe r7 = UNSAFE
                long r8 = r11 + r2
                byte r11 = r7.getByte(r11)
                if (r11 > r5) goto L_0x0071
                r12 = -96
                if (r1 != r4) goto L_0x005f
                if (r11 < r12) goto L_0x0071
            L_0x005f:
                r4 = -19
                if (r1 != r4) goto L_0x0065
                if (r11 >= r12) goto L_0x0071
            L_0x0065:
                sun.misc.Unsafe r12 = UNSAFE
                long r2 = r2 + r8
                byte r12 = r12.getByte(r8)
                if (r12 <= r5) goto L_0x006f
                goto L_0x0072
            L_0x006f:
                r11 = r2
                goto L_0x00a6
            L_0x0071:
                r2 = r8
            L_0x0072:
                return r6
            L_0x0073:
                r4 = 3
                if (r13 >= r4) goto L_0x007b
                int r2 = unsafeIncompleteStateFor(r11, r1, r13)
                return r2
            L_0x007b:
                int r13 = r13 + -3
                sun.misc.Unsafe r4 = UNSAFE
                long r7 = r11 + r2
                byte r11 = r4.getByte(r11)
                if (r11 > r5) goto L_0x00a9
                int r12 = r1 << 28
                int r4 = r11 + 112
                int r12 = r12 + r4
                int r12 = r12 >> 30
                if (r12 != 0) goto L_0x00a9
                sun.misc.Unsafe r12 = UNSAFE
                long r9 = r7 + r2
                byte r12 = r12.getByte(r7)
                if (r12 > r5) goto L_0x00a8
                sun.misc.Unsafe r12 = UNSAFE
                long r7 = r9 + r2
                byte r12 = r12.getByte(r9)
                if (r12 <= r5) goto L_0x00a5
                goto L_0x00a9
            L_0x00a5:
                r11 = r7
            L_0x00a6:
                goto L_0x0007
            L_0x00a8:
                r7 = r9
            L_0x00a9:
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(long, int):int");
        }

        private static int unsafeIncompleteStateFor(byte[] bytes, int byte1, long offset, int remaining) {
            if (remaining == 0) {
                return Utf8.incompleteStateFor(byte1);
            }
            if (remaining == 1) {
                return Utf8.incompleteStateFor(byte1, UNSAFE.getByte(bytes, offset));
            }
            if (remaining == 2) {
                return Utf8.incompleteStateFor(byte1, (int) UNSAFE.getByte(bytes, offset), (int) UNSAFE.getByte(bytes, 1 + offset));
            }
            throw new AssertionError();
        }

        private static int unsafeIncompleteStateFor(long address, int byte1, int remaining) {
            if (remaining == 0) {
                return Utf8.incompleteStateFor(byte1);
            }
            if (remaining == 1) {
                return Utf8.incompleteStateFor(byte1, UNSAFE.getByte(address));
            }
            if (remaining == 2) {
                return Utf8.incompleteStateFor(byte1, (int) UNSAFE.getByte(address), (int) UNSAFE.getByte(1 + address));
            }
            throw new AssertionError();
        }

        private static Field field(Class<?> clazz, String fieldName) {
            Field field;
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (Throwable th) {
                field = null;
            }
            Logger access$700 = Utf8.logger;
            Level level = Level.FINEST;
            Object[] objArr = new Object[3];
            objArr[0] = clazz.getName();
            objArr[1] = fieldName;
            objArr[2] = field != null ? "available" : "unavailable";
            access$700.log(level, "{0}.{1}: {2}", objArr);
            return field;
        }

        private static long fieldOffset(Field field) {
            Unsafe unsafe;
            if (field == null || (unsafe = UNSAFE) == null) {
                return -1;
            }
            return unsafe.objectFieldOffset(field);
        }

        private static <T> int byteArrayBaseOffset() {
            Unsafe unsafe = UNSAFE;
            if (unsafe == null) {
                return -1;
            }
            return unsafe.arrayBaseOffset(byte[].class);
        }

        private static long addressOffset(ByteBuffer buffer) {
            return UNSAFE.getLong(buffer, BUFFER_ADDRESS_OFFSET);
        }

        private static Unsafe getUnsafe() {
            Unsafe unsafe = null;
            try {
                unsafe = (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                    public Unsafe run() throws Exception {
                        Class<Unsafe> k = Unsafe.class;
                        UnsafeProcessor.checkRequiredMethods(k);
                        for (Field f : k.getDeclaredFields()) {
                            f.setAccessible(true);
                            Object x = f.get((Object) null);
                            if (k.isInstance(x)) {
                                return k.cast(x);
                            }
                        }
                        return null;
                    }
                });
            } catch (Throwable th) {
            }
            Utf8.logger.log(Level.FINEST, "sun.misc.Unsafe: {}", unsafe != null ? "available" : "unavailable");
            return unsafe;
        }

        /* access modifiers changed from: private */
        public static void checkRequiredMethods(Class<Unsafe> clazz) throws NoSuchMethodException, SecurityException {
            clazz.getMethod("arrayBaseOffset", new Class[]{Class.class});
            clazz.getMethod("getByte", new Class[]{Object.class, Long.TYPE});
            clazz.getMethod("putByte", new Class[]{Object.class, Long.TYPE, Byte.TYPE});
            clazz.getMethod("getLong", new Class[]{Object.class, Long.TYPE});
            clazz.getMethod("objectFieldOffset", new Class[]{Field.class});
            clazz.getMethod("getByte", new Class[]{Long.TYPE});
            clazz.getMethod("getLong", new Class[]{Object.class, Long.TYPE});
            clazz.getMethod("putByte", new Class[]{Long.TYPE, Byte.TYPE});
            clazz.getMethod("getLong", new Class[]{Long.TYPE});
        }
    }

    private Utf8() {
    }
}
