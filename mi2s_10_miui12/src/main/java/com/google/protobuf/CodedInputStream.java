package com.google.protobuf;

import com.android.server.wifi.ScoringParams;
import com.google.protobuf.MessageLite;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.bluetooth.ble.MiServiceData;
import miui.telephony.phonenumber.Prefix;

public final class CodedInputStream {
    private static final int BUFFER_SIZE = 4096;
    private static final int DEFAULT_RECURSION_LIMIT = 100;
    private static final int DEFAULT_SIZE_LIMIT = 67108864;
    /* access modifiers changed from: private */
    public final byte[] buffer;
    private final boolean bufferIsImmutable;
    /* access modifiers changed from: private */
    public int bufferPos;
    private int bufferSize;
    private int bufferSizeAfterLimit;
    private int currentLimit = ScoringParams.Values.MAX_EXPID;
    private boolean enableAliasing = false;
    private final InputStream input;
    private int lastTag;
    private int recursionDepth;
    private int recursionLimit = 100;
    private RefillCallback refillCallback = null;
    private int sizeLimit = 67108864;
    private int totalBytesRetired;

    private interface RefillCallback {
        void onRefill();
    }

    public static CodedInputStream newInstance(InputStream input2) {
        return new CodedInputStream(input2, 4096);
    }

    static CodedInputStream newInstance(InputStream input2, int bufferSize2) {
        return new CodedInputStream(input2, bufferSize2);
    }

    public static CodedInputStream newInstance(byte[] buf) {
        return newInstance(buf, 0, buf.length);
    }

    public static CodedInputStream newInstance(byte[] buf, int off, int len) {
        return newInstance(buf, off, len, false);
    }

    static CodedInputStream newInstance(byte[] buf, int off, int len, boolean bufferIsImmutable2) {
        CodedInputStream result = new CodedInputStream(buf, off, len, bufferIsImmutable2);
        try {
            result.pushLimit(len);
            return result;
        } catch (InvalidProtocolBufferException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static CodedInputStream newInstance(ByteBuffer buf) {
        if (buf.hasArray()) {
            return newInstance(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining());
        }
        ByteBuffer temp = buf.duplicate();
        byte[] buffer2 = new byte[temp.remaining()];
        temp.get(buffer2);
        return newInstance(buffer2);
    }

    public int readTag() throws IOException {
        if (isAtEnd()) {
            this.lastTag = 0;
            return 0;
        }
        this.lastTag = readRawVarint32();
        if (WireFormat.getTagFieldNumber(this.lastTag) != 0) {
            return this.lastTag;
        }
        throw InvalidProtocolBufferException.invalidTag();
    }

    public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
        if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
        }
    }

    public int getLastTag() {
        return this.lastTag;
    }

    public boolean skipField(int tag) throws IOException {
        int tagWireType = WireFormat.getTagWireType(tag);
        if (tagWireType == 0) {
            skipRawVarint();
            return true;
        } else if (tagWireType == 1) {
            skipRawBytes(8);
            return true;
        } else if (tagWireType == 2) {
            skipRawBytes(readRawVarint32());
            return true;
        } else if (tagWireType == 3) {
            skipMessage();
            checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
            return true;
        } else if (tagWireType == 4) {
            return false;
        } else {
            if (tagWireType == 5) {
                skipRawBytes(4);
                return true;
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }
    }

    public boolean skipField(int tag, CodedOutputStream output) throws IOException {
        int tagWireType = WireFormat.getTagWireType(tag);
        if (tagWireType == 0) {
            long value = readInt64();
            output.writeRawVarint32(tag);
            output.writeUInt64NoTag(value);
            return true;
        } else if (tagWireType == 1) {
            long value2 = readRawLittleEndian64();
            output.writeRawVarint32(tag);
            output.writeFixed64NoTag(value2);
            return true;
        } else if (tagWireType == 2) {
            ByteString value3 = readBytes();
            output.writeRawVarint32(tag);
            output.writeBytesNoTag(value3);
            return true;
        } else if (tagWireType == 3) {
            output.writeRawVarint32(tag);
            skipMessage(output);
            int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
            checkLastTagWas(endtag);
            output.writeRawVarint32(endtag);
            return true;
        } else if (tagWireType == 4) {
            return false;
        } else {
            if (tagWireType == 5) {
                int value4 = readRawLittleEndian32();
                output.writeRawVarint32(tag);
                output.writeFixed32NoTag(value4);
                return true;
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }
    }

    /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    public void skipMessage() throws java.io.IOException {
        /*
            r2 = this;
        L_0x0000:
            int r0 = r2.readTag()
            if (r0 == 0) goto L_0x000e
            boolean r1 = r2.skipField(r0)
            if (r1 != 0) goto L_0x000d
            goto L_0x000e
        L_0x000d:
            goto L_0x0000
        L_0x000e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.CodedInputStream.skipMessage():void");
    }

    /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    public void skipMessage(com.google.protobuf.CodedOutputStream r3) throws java.io.IOException {
        /*
            r2 = this;
        L_0x0000:
            int r0 = r2.readTag()
            if (r0 == 0) goto L_0x000e
            boolean r1 = r2.skipField(r0, r3)
            if (r1 != 0) goto L_0x000d
            goto L_0x000e
        L_0x000d:
            goto L_0x0000
        L_0x000e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.CodedInputStream.skipMessage(com.google.protobuf.CodedOutputStream):void");
    }

    private class SkippedDataSink implements RefillCallback {
        private ByteArrayOutputStream byteArrayStream;
        private int lastPos = CodedInputStream.this.bufferPos;

        private SkippedDataSink() {
        }

        public void onRefill() {
            if (this.byteArrayStream == null) {
                this.byteArrayStream = new ByteArrayOutputStream();
            }
            this.byteArrayStream.write(CodedInputStream.this.buffer, this.lastPos, CodedInputStream.this.bufferPos - this.lastPos);
            this.lastPos = 0;
        }

        /* access modifiers changed from: package-private */
        public ByteBuffer getSkippedData() {
            ByteArrayOutputStream byteArrayOutputStream = this.byteArrayStream;
            if (byteArrayOutputStream == null) {
                return ByteBuffer.wrap(CodedInputStream.this.buffer, this.lastPos, CodedInputStream.this.bufferPos - this.lastPos);
            }
            byteArrayOutputStream.write(CodedInputStream.this.buffer, this.lastPos, CodedInputStream.this.bufferPos);
            return ByteBuffer.wrap(this.byteArrayStream.toByteArray());
        }
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readRawLittleEndian64());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readRawLittleEndian32());
    }

    public long readUInt64() throws IOException {
        return readRawVarint64();
    }

    public long readInt64() throws IOException {
        return readRawVarint64();
    }

    public int readInt32() throws IOException {
        return readRawVarint32();
    }

    public long readFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public boolean readBool() throws IOException {
        return readRawVarint64() != 0;
    }

    public String readString() throws IOException {
        int size = readRawVarint32();
        int i = this.bufferSize;
        int i2 = this.bufferPos;
        if (size <= i - i2 && size > 0) {
            String result = new String(this.buffer, i2, size, Internal.UTF_8);
            this.bufferPos += size;
            return result;
        } else if (size == 0) {
            return Prefix.EMPTY;
        } else {
            if (size > this.bufferSize) {
                return new String(readRawBytesSlowPath(size), Internal.UTF_8);
            }
            refillBuffer(size);
            String result2 = new String(this.buffer, this.bufferPos, size, Internal.UTF_8);
            this.bufferPos += size;
            return result2;
        }
    }

    public String readStringRequireUtf8() throws IOException {
        int pos;
        byte[] bytes;
        int size = readRawVarint32();
        int oldPos = this.bufferPos;
        if (size <= this.bufferSize - oldPos && size > 0) {
            bytes = this.buffer;
            this.bufferPos = oldPos + size;
            pos = oldPos;
        } else if (size == 0) {
            return Prefix.EMPTY;
        } else {
            if (size <= this.bufferSize) {
                refillBuffer(size);
                bytes = this.buffer;
                pos = 0;
                this.bufferPos = 0 + size;
            } else {
                bytes = readRawBytesSlowPath(size);
                pos = 0;
            }
        }
        if (Utf8.isValidUtf8(bytes, pos, pos + size)) {
            return new String(bytes, pos, size, Internal.UTF_8);
        }
        throw InvalidProtocolBufferException.invalidUtf8();
    }

    public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
        int i = this.recursionDepth;
        if (i < this.recursionLimit) {
            this.recursionDepth = i + 1;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
            return;
        }
        throw InvalidProtocolBufferException.recursionLimitExceeded();
    }

    public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
        int i = this.recursionDepth;
        if (i < this.recursionLimit) {
            this.recursionDepth = i + 1;
            T result = (MessageLite) parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            this.recursionDepth--;
            return result;
        }
        throw InvalidProtocolBufferException.recursionLimitExceeded();
    }

    @Deprecated
    public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
        readGroup(fieldNumber, builder, (ExtensionRegistryLite) null);
    }

    public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
        int length = readRawVarint32();
        if (this.recursionDepth < this.recursionLimit) {
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            builder.mergeFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            popLimit(oldLimit);
            return;
        }
        throw InvalidProtocolBufferException.recursionLimitExceeded();
    }

    public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
        int length = readRawVarint32();
        if (this.recursionDepth < this.recursionLimit) {
            int oldLimit = pushLimit(length);
            this.recursionDepth++;
            T result = (MessageLite) parser.parsePartialFrom(this, extensionRegistry);
            checkLastTagWas(0);
            this.recursionDepth--;
            popLimit(oldLimit);
            return result;
        }
        throw InvalidProtocolBufferException.recursionLimitExceeded();
    }

    public ByteString readBytes() throws IOException {
        ByteString result;
        int size = readRawVarint32();
        int i = this.bufferSize;
        int i2 = this.bufferPos;
        if (size <= i - i2 && size > 0) {
            if (!this.bufferIsImmutable || !this.enableAliasing) {
                result = ByteString.copyFrom(this.buffer, this.bufferPos, size);
            } else {
                result = ByteString.wrap(this.buffer, i2, size);
            }
            this.bufferPos += size;
            return result;
        } else if (size == 0) {
            return ByteString.EMPTY;
        } else {
            return ByteString.wrap(readRawBytesSlowPath(size));
        }
    }

    public byte[] readByteArray() throws IOException {
        int size = readRawVarint32();
        int i = this.bufferSize;
        int i2 = this.bufferPos;
        if (size > i - i2 || size <= 0) {
            return readRawBytesSlowPath(size);
        }
        byte[] result = Arrays.copyOfRange(this.buffer, i2, i2 + size);
        this.bufferPos += size;
        return result;
    }

    public ByteBuffer readByteBuffer() throws IOException {
        ByteBuffer result;
        int size = readRawVarint32();
        int i = this.bufferSize;
        int i2 = this.bufferPos;
        if (size <= i - i2 && size > 0) {
            if (this.input != null || this.bufferIsImmutable || !this.enableAliasing) {
                byte[] bArr = this.buffer;
                int i3 = this.bufferPos;
                result = ByteBuffer.wrap(Arrays.copyOfRange(bArr, i3, i3 + size));
            } else {
                result = ByteBuffer.wrap(this.buffer, i2, size).slice();
            }
            this.bufferPos += size;
            return result;
        } else if (size == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
        } else {
            return ByteBuffer.wrap(readRawBytesSlowPath(size));
        }
    }

    public int readUInt32() throws IOException {
        return readRawVarint32();
    }

    public int readEnum() throws IOException {
        return readRawVarint32();
    }

    public int readSFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public long readSFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readSInt32() throws IOException {
        return decodeZigZag32(readRawVarint32());
    }

    public long readSInt64() throws IOException {
        return decodeZigZag64(readRawVarint64());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0071, code lost:
        if (r2[r1] < 0) goto L_0x0074;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int readRawVarint32() throws java.io.IOException {
        /*
            r5 = this;
            int r0 = r5.bufferPos
            int r1 = r5.bufferSize
            if (r1 != r0) goto L_0x0008
            goto L_0x0074
        L_0x0008:
            byte[] r2 = r5.buffer
            int r3 = r0 + 1
            byte r0 = r2[r0]
            r4 = r0
            if (r0 < 0) goto L_0x0014
            r5.bufferPos = r3
            return r4
        L_0x0014:
            int r1 = r1 - r3
            r0 = 9
            if (r1 >= r0) goto L_0x001a
            goto L_0x0074
        L_0x001a:
            int r0 = r3 + 1
            byte r1 = r2[r3]
            int r1 = r1 << 7
            r1 = r1 ^ r4
            r3 = r1
            if (r1 >= 0) goto L_0x0029
            r1 = r3 ^ -128(0xffffffffffffff80, float:NaN)
            r3 = r1
            r1 = r0
            goto L_0x007b
        L_0x0029:
            int r1 = r0 + 1
            byte r0 = r2[r0]
            int r0 = r0 << 14
            r0 = r0 ^ r3
            r3 = r0
            if (r0 < 0) goto L_0x0037
            r0 = r3 ^ 16256(0x3f80, float:2.278E-41)
            r3 = r0
            goto L_0x007b
        L_0x0037:
            int r0 = r1 + 1
            byte r1 = r2[r1]
            int r1 = r1 << 21
            r1 = r1 ^ r3
            r3 = r1
            if (r1 >= 0) goto L_0x0048
            r1 = -2080896(0xffffffffffe03f80, float:NaN)
            r1 = r1 ^ r3
            r3 = r1
            r1 = r0
            goto L_0x007b
        L_0x0048:
            int r1 = r0 + 1
            byte r0 = r2[r0]
            int r4 = r0 << 28
            r3 = r3 ^ r4
            r4 = 266354560(0xfe03f80, float:2.2112565E-29)
            r3 = r3 ^ r4
            if (r0 >= 0) goto L_0x007b
            int r4 = r1 + 1
            byte r1 = r2[r1]
            if (r1 >= 0) goto L_0x007a
            int r1 = r4 + 1
            byte r4 = r2[r4]
            if (r4 >= 0) goto L_0x007b
            int r4 = r1 + 1
            byte r1 = r2[r1]
            if (r1 >= 0) goto L_0x007a
            int r1 = r4 + 1
            byte r4 = r2[r4]
            if (r4 >= 0) goto L_0x007b
            int r4 = r1 + 1
            byte r1 = r2[r1]
            if (r1 >= 0) goto L_0x007a
        L_0x0074:
            long r0 = r5.readRawVarint64SlowPath()
            int r0 = (int) r0
            return r0
        L_0x007a:
            r1 = r4
        L_0x007b:
            r5.bufferPos = r1
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.CodedInputStream.readRawVarint32():int");
    }

    private void skipRawVarint() throws IOException {
        if (this.bufferSize - this.bufferPos >= 10) {
            byte[] buffer2 = this.buffer;
            int pos = this.bufferPos;
            int i = 0;
            while (i < 10) {
                int pos2 = pos + 1;
                if (buffer2[pos] >= 0) {
                    this.bufferPos = pos2;
                    return;
                } else {
                    i++;
                    pos = pos2;
                }
            }
        }
        skipRawVarintSlowPath();
    }

    private void skipRawVarintSlowPath() throws IOException {
        int i = 0;
        while (i < 10) {
            if (readRawByte() < 0) {
                i++;
            } else {
                return;
            }
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }

    static int readRawVarint32(InputStream input2) throws IOException {
        int firstByte = input2.read();
        if (firstByte != -1) {
            return readRawVarint32(firstByte, input2);
        }
        throw InvalidProtocolBufferException.truncatedMessage();
    }

    public static int readRawVarint32(int firstByte, InputStream input2) throws IOException {
        if ((firstByte & 128) == 0) {
            return firstByte;
        }
        int result = firstByte & 127;
        int offset = 7;
        while (offset < 32) {
            int b = input2.read();
            if (b != -1) {
                result |= (b & 127) << offset;
                if ((b & 128) == 0) {
                    return result;
                }
                offset += 7;
            } else {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }
        while (offset < 64) {
            int b2 = input2.read();
            if (b2 == -1) {
                throw InvalidProtocolBufferException.truncatedMessage();
            } else if ((b2 & 128) == 0) {
                return result;
            } else {
                offset += 7;
            }
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00bd, code lost:
        if (((long) r2[r1]) < 0) goto L_0x00c0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long readRawVarint64() throws java.io.IOException {
        /*
            r10 = this;
            int r0 = r10.bufferPos
            int r1 = r10.bufferSize
            if (r1 != r0) goto L_0x0008
            goto L_0x00c0
        L_0x0008:
            byte[] r2 = r10.buffer
            int r3 = r0 + 1
            byte r0 = r2[r0]
            r4 = r0
            if (r0 < 0) goto L_0x0015
            r10.bufferPos = r3
            long r0 = (long) r4
            return r0
        L_0x0015:
            int r1 = r1 - r3
            r0 = 9
            if (r1 >= r0) goto L_0x001c
            goto L_0x00c0
        L_0x001c:
            int r0 = r3 + 1
            byte r1 = r2[r3]
            int r1 = r1 << 7
            r1 = r1 ^ r4
            r3 = r1
            if (r1 >= 0) goto L_0x002b
            r1 = r3 ^ -128(0xffffffffffffff80, float:NaN)
            long r4 = (long) r1
            goto L_0x00c6
        L_0x002b:
            int r1 = r0 + 1
            byte r0 = r2[r0]
            int r0 = r0 << 14
            r0 = r0 ^ r3
            r3 = r0
            if (r0 < 0) goto L_0x003b
            r0 = r3 ^ 16256(0x3f80, float:2.278E-41)
            long r4 = (long) r0
            r0 = r1
            goto L_0x00c6
        L_0x003b:
            int r0 = r1 + 1
            byte r1 = r2[r1]
            int r1 = r1 << 21
            r1 = r1 ^ r3
            r3 = r1
            if (r1 >= 0) goto L_0x004c
            r1 = -2080896(0xffffffffffe03f80, float:NaN)
            r1 = r1 ^ r3
            long r4 = (long) r1
            goto L_0x00c6
        L_0x004c:
            long r4 = (long) r3
            int r1 = r0 + 1
            byte r0 = r2[r0]
            long r6 = (long) r0
            r0 = 28
            long r6 = r6 << r0
            long r4 = r4 ^ r6
            r6 = r4
            r8 = 0
            int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r0 < 0) goto L_0x0063
            r4 = 266354560(0xfe03f80, double:1.315966377E-315)
            long r4 = r4 ^ r6
            r0 = r1
            goto L_0x00c6
        L_0x0063:
            int r0 = r1 + 1
            byte r1 = r2[r1]
            long r4 = (long) r1
            r1 = 35
            long r4 = r4 << r1
            long r4 = r4 ^ r6
            r6 = r4
            int r1 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r1 >= 0) goto L_0x0078
            r4 = -34093383808(0xfffffff80fe03f80, double:NaN)
            long r4 = r4 ^ r6
            goto L_0x00c6
        L_0x0078:
            int r1 = r0 + 1
            byte r0 = r2[r0]
            long r4 = (long) r0
            r0 = 42
            long r4 = r4 << r0
            long r4 = r4 ^ r6
            r6 = r4
            int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r0 < 0) goto L_0x008e
            r4 = 4363953127296(0x3f80fe03f80, double:2.1560793202584E-311)
            long r4 = r4 ^ r6
            r0 = r1
            goto L_0x00c6
        L_0x008e:
            int r0 = r1 + 1
            byte r1 = r2[r1]
            long r4 = (long) r1
            r1 = 49
            long r4 = r4 << r1
            long r4 = r4 ^ r6
            r6 = r4
            int r1 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r1 >= 0) goto L_0x00a3
            r4 = -558586000294016(0xfffe03f80fe03f80, double:NaN)
            long r4 = r4 ^ r6
            goto L_0x00c6
        L_0x00a3:
            int r1 = r0 + 1
            byte r0 = r2[r0]
            long r4 = (long) r0
            r0 = 56
            long r4 = r4 << r0
            long r4 = r4 ^ r6
            r6 = 71499008037633920(0xfe03f80fe03f80, double:6.838959413692434E-304)
            long r4 = r4 ^ r6
            int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r0 >= 0) goto L_0x00c5
            int r0 = r1 + 1
            byte r1 = r2[r1]
            long r6 = (long) r1
            int r1 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r1 >= 0) goto L_0x00c6
        L_0x00c0:
            long r0 = r10.readRawVarint64SlowPath()
            return r0
        L_0x00c5:
            r0 = r1
        L_0x00c6:
            r10.bufferPos = r0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.CodedInputStream.readRawVarint64():long");
    }

    /* access modifiers changed from: package-private */
    public long readRawVarint64SlowPath() throws IOException {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            byte b = readRawByte();
            result |= ((long) (b & Byte.MAX_VALUE)) << shift;
            if ((b & 128) == 0) {
                return result;
            }
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }

    public int readRawLittleEndian32() throws IOException {
        int pos = this.bufferPos;
        if (this.bufferSize - pos < 4) {
            refillBuffer(4);
            pos = this.bufferPos;
        }
        byte[] buffer2 = this.buffer;
        this.bufferPos = pos + 4;
        return (buffer2[pos] & 255) | ((buffer2[pos + 1] & 255) << 8) | ((buffer2[pos + 2] & 255) << 16) | ((buffer2[pos + 3] & 255) << MiServiceData.CAPABILITY_IO);
    }

    public long readRawLittleEndian64() throws IOException {
        int pos = this.bufferPos;
        if (this.bufferSize - pos < 8) {
            refillBuffer(8);
            pos = this.bufferPos;
        }
        byte[] buffer2 = this.buffer;
        this.bufferPos = pos + 8;
        return (((long) buffer2[pos]) & 255) | ((((long) buffer2[pos + 1]) & 255) << 8) | ((((long) buffer2[pos + 2]) & 255) << 16) | ((((long) buffer2[pos + 3]) & 255) << 24) | ((((long) buffer2[pos + 4]) & 255) << 32) | ((((long) buffer2[pos + 5]) & 255) << 40) | ((((long) buffer2[pos + 6]) & 255) << 48) | ((((long) buffer2[pos + 7]) & 255) << 56);
    }

    public static int decodeZigZag32(int n) {
        return (n >>> 1) ^ (-(n & 1));
    }

    public static long decodeZigZag64(long n) {
        return (n >>> 1) ^ (-(1 & n));
    }

    private CodedInputStream(byte[] buffer2, int off, int len, boolean bufferIsImmutable2) {
        this.buffer = buffer2;
        this.bufferSize = off + len;
        this.bufferPos = off;
        this.totalBytesRetired = -off;
        this.input = null;
        this.bufferIsImmutable = bufferIsImmutable2;
    }

    private CodedInputStream(InputStream input2, int bufferSize2) {
        this.buffer = new byte[bufferSize2];
        this.bufferPos = 0;
        this.totalBytesRetired = 0;
        this.input = input2;
        this.bufferIsImmutable = false;
    }

    public void enableAliasing(boolean enabled) {
        this.enableAliasing = enabled;
    }

    public int setRecursionLimit(int limit) {
        if (limit >= 0) {
            int oldLimit = this.recursionLimit;
            this.recursionLimit = limit;
            return oldLimit;
        }
        throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
    }

    public int setSizeLimit(int limit) {
        if (limit >= 0) {
            int oldLimit = this.sizeLimit;
            this.sizeLimit = limit;
            return oldLimit;
        }
        throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
    }

    public void resetSizeCounter() {
        this.totalBytesRetired = -this.bufferPos;
    }

    public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
        if (byteLimit >= 0) {
            int byteLimit2 = byteLimit + this.totalBytesRetired + this.bufferPos;
            int oldLimit = this.currentLimit;
            if (byteLimit2 <= oldLimit) {
                this.currentLimit = byteLimit2;
                recomputeBufferSizeAfterLimit();
                return oldLimit;
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        throw InvalidProtocolBufferException.negativeSize();
    }

    private void recomputeBufferSizeAfterLimit() {
        this.bufferSize += this.bufferSizeAfterLimit;
        int i = this.totalBytesRetired;
        int i2 = this.bufferSize;
        int bufferEnd = i + i2;
        int i3 = this.currentLimit;
        if (bufferEnd > i3) {
            this.bufferSizeAfterLimit = bufferEnd - i3;
            this.bufferSize = i2 - this.bufferSizeAfterLimit;
            return;
        }
        this.bufferSizeAfterLimit = 0;
    }

    public void popLimit(int oldLimit) {
        this.currentLimit = oldLimit;
        recomputeBufferSizeAfterLimit();
    }

    public int getBytesUntilLimit() {
        int i = this.currentLimit;
        if (i == Integer.MAX_VALUE) {
            return -1;
        }
        return i - (this.totalBytesRetired + this.bufferPos);
    }

    public boolean isAtEnd() throws IOException {
        return this.bufferPos == this.bufferSize && !tryRefillBuffer(1);
    }

    public int getTotalBytesRead() {
        return this.totalBytesRetired + this.bufferPos;
    }

    private void refillBuffer(int n) throws IOException {
        if (!tryRefillBuffer(n)) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
    }

    private boolean tryRefillBuffer(int n) throws IOException {
        int i = this.bufferPos;
        if (i + n <= this.bufferSize) {
            throw new IllegalStateException("refillBuffer() called when " + n + " bytes were already available in buffer");
        } else if (this.totalBytesRetired + i + n > this.currentLimit) {
            return false;
        } else {
            RefillCallback refillCallback2 = this.refillCallback;
            if (refillCallback2 != null) {
                refillCallback2.onRefill();
            }
            if (this.input != null) {
                int pos = this.bufferPos;
                if (pos > 0) {
                    int i2 = this.bufferSize;
                    if (i2 > pos) {
                        byte[] bArr = this.buffer;
                        System.arraycopy(bArr, pos, bArr, 0, i2 - pos);
                    }
                    this.totalBytesRetired += pos;
                    this.bufferSize -= pos;
                    this.bufferPos = 0;
                }
                InputStream inputStream = this.input;
                byte[] bArr2 = this.buffer;
                int i3 = this.bufferSize;
                int bytesRead = inputStream.read(bArr2, i3, bArr2.length - i3);
                if (bytesRead == 0 || bytesRead < -1 || bytesRead > this.buffer.length) {
                    throw new IllegalStateException("InputStream#read(byte[]) returned invalid result: " + bytesRead + "\nThe InputStream implementation is buggy.");
                } else if (bytesRead > 0) {
                    this.bufferSize += bytesRead;
                    if ((this.totalBytesRetired + n) - this.sizeLimit <= 0) {
                        recomputeBufferSizeAfterLimit();
                        if (this.bufferSize >= n) {
                            return true;
                        }
                        return tryRefillBuffer(n);
                    }
                    throw InvalidProtocolBufferException.sizeLimitExceeded();
                }
            }
            return false;
        }
    }

    public byte readRawByte() throws IOException {
        if (this.bufferPos == this.bufferSize) {
            refillBuffer(1);
        }
        byte[] bArr = this.buffer;
        int i = this.bufferPos;
        this.bufferPos = i + 1;
        return bArr[i];
    }

    public byte[] readRawBytes(int size) throws IOException {
        int pos = this.bufferPos;
        if (size > this.bufferSize - pos || size <= 0) {
            return readRawBytesSlowPath(size);
        }
        this.bufferPos = pos + size;
        return Arrays.copyOfRange(this.buffer, pos, pos + size);
    }

    private byte[] readRawBytesSlowPath(int size) throws IOException {
        if (size > 0) {
            int i = this.totalBytesRetired;
            int i2 = this.bufferPos;
            int currentMessageSize = i + i2 + size;
            if (currentMessageSize <= this.sizeLimit) {
                int i3 = this.currentLimit;
                if (currentMessageSize <= i3) {
                    InputStream inputStream = this.input;
                    if (inputStream != null) {
                        int originalBufferPos = this.bufferPos;
                        int i4 = this.bufferSize;
                        int bufferedBytes = i4 - i2;
                        this.totalBytesRetired = i + i4;
                        this.bufferPos = 0;
                        this.bufferSize = 0;
                        int sizeLeft = size - bufferedBytes;
                        if (sizeLeft < 4096 || sizeLeft <= inputStream.available()) {
                            byte[] bytes = new byte[size];
                            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
                            int pos = bufferedBytes;
                            while (pos < bytes.length) {
                                int n = this.input.read(bytes, pos, size - pos);
                                if (n != -1) {
                                    this.totalBytesRetired += n;
                                    pos += n;
                                } else {
                                    throw InvalidProtocolBufferException.truncatedMessage();
                                }
                            }
                            return bytes;
                        }
                        List<byte[]> chunks = new ArrayList<>();
                        while (sizeLeft > 0) {
                            byte[] chunk = new byte[Math.min(sizeLeft, 4096)];
                            int pos2 = 0;
                            while (pos2 < chunk.length) {
                                int n2 = this.input.read(chunk, pos2, chunk.length - pos2);
                                if (n2 != -1) {
                                    this.totalBytesRetired += n2;
                                    pos2 += n2;
                                } else {
                                    throw InvalidProtocolBufferException.truncatedMessage();
                                }
                            }
                            sizeLeft -= chunk.length;
                            chunks.add(chunk);
                        }
                        byte[] bytes2 = new byte[size];
                        System.arraycopy(this.buffer, originalBufferPos, bytes2, 0, bufferedBytes);
                        int pos3 = bufferedBytes;
                        for (byte[] chunk2 : chunks) {
                            System.arraycopy(chunk2, 0, bytes2, pos3, chunk2.length);
                            pos3 += chunk2.length;
                        }
                        return bytes2;
                    }
                    throw InvalidProtocolBufferException.truncatedMessage();
                }
                skipRawBytes((i3 - i) - i2);
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            throw InvalidProtocolBufferException.sizeLimitExceeded();
        } else if (size == 0) {
            return Internal.EMPTY_BYTE_ARRAY;
        } else {
            throw InvalidProtocolBufferException.negativeSize();
        }
    }

    public void skipRawBytes(int size) throws IOException {
        int i = this.bufferSize;
        int i2 = this.bufferPos;
        if (size > i - i2 || size < 0) {
            skipRawBytesSlowPath(size);
        } else {
            this.bufferPos = i2 + size;
        }
    }

    private void skipRawBytesSlowPath(int size) throws IOException {
        if (size >= 0) {
            int i = this.totalBytesRetired;
            int i2 = this.bufferPos;
            int i3 = i + i2 + size;
            int i4 = this.currentLimit;
            if (i3 <= i4) {
                int i5 = this.bufferSize;
                int pos = i5 - i2;
                this.bufferPos = i5;
                refillBuffer(1);
                while (true) {
                    int i6 = size - pos;
                    int i7 = this.bufferSize;
                    if (i6 > i7) {
                        pos += i7;
                        this.bufferPos = i7;
                        refillBuffer(1);
                    } else {
                        this.bufferPos = size - pos;
                        return;
                    }
                }
            } else {
                skipRawBytes((i4 - i) - i2);
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        } else {
            throw InvalidProtocolBufferException.negativeSize();
        }
    }
}
