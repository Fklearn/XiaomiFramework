package com.google.protobuf;

import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

class LiteralByteString extends ByteString {
    protected final byte[] bytes;
    private int hash = 0;

    LiteralByteString(byte[] bytes2) {
        this.bytes = bytes2;
    }

    public byte byteAt(int index) {
        return this.bytes[index];
    }

    public int size() {
        return this.bytes.length;
    }

    public ByteString substring(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException("Beginning index: " + beginIndex + " < 0");
        } else if (endIndex <= size()) {
            int substringLength = endIndex - beginIndex;
            if (substringLength < 0) {
                throw new IndexOutOfBoundsException("Beginning index larger than ending index: " + beginIndex + ", " + endIndex);
            } else if (substringLength == 0) {
                return ByteString.EMPTY;
            } else {
                return new BoundedByteString(this.bytes, getOffsetIntoBytes() + beginIndex, substringLength);
            }
        } else {
            throw new IndexOutOfBoundsException("End index: " + endIndex + " > " + size());
        }
    }

    /* access modifiers changed from: protected */
    public void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
        System.arraycopy(this.bytes, sourceOffset, target, targetOffset, numberToCopy);
    }

    public void copyTo(ByteBuffer target) {
        target.put(this.bytes, getOffsetIntoBytes(), size());
    }

    public ByteBuffer asReadOnlyByteBuffer() {
        return ByteBuffer.wrap(this.bytes, getOffsetIntoBytes(), size()).asReadOnlyBuffer();
    }

    public List<ByteBuffer> asReadOnlyByteBufferList() {
        List<ByteBuffer> result = new ArrayList<>(1);
        result.add(asReadOnlyByteBuffer());
        return result;
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(toByteArray());
    }

    public String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(this.bytes, getOffsetIntoBytes(), size(), charsetName);
    }

    public boolean isValidUtf8() {
        int offset = getOffsetIntoBytes();
        return Utf8.isValidUtf8(this.bytes, offset, size() + offset);
    }

    /* access modifiers changed from: protected */
    public int partialIsValidUtf8(int state, int offset, int length) {
        int index = getOffsetIntoBytes() + offset;
        return Utf8.partialIsValidUtf8(state, this.bytes, index, index + length);
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ByteString) || size() != ((ByteString) other).size()) {
            return false;
        }
        if (size() == 0) {
            return true;
        }
        if (other instanceof LiteralByteString) {
            return equalsRange((LiteralByteString) other, 0, size());
        }
        if (other instanceof RopeByteString) {
            return other.equals(this);
        }
        throw new IllegalArgumentException("Has a new type of ByteString been created? Found " + other.getClass());
    }

    /* access modifiers changed from: package-private */
    public boolean equalsRange(LiteralByteString other, int offset, int length) {
        if (length > other.size()) {
            throw new IllegalArgumentException("Length too large: " + length + size());
        } else if (offset + length <= other.size()) {
            byte[] thisBytes = this.bytes;
            byte[] otherBytes = other.bytes;
            int thisLimit = getOffsetIntoBytes() + length;
            int thisIndex = getOffsetIntoBytes();
            int otherIndex = other.getOffsetIntoBytes() + offset;
            while (thisIndex < thisLimit) {
                if (thisBytes[thisIndex] != otherBytes[otherIndex]) {
                    return false;
                }
                thisIndex++;
                otherIndex++;
            }
            return true;
        } else {
            throw new IllegalArgumentException("Ran off end of other: " + offset + ", " + length + ", " + other.size());
        }
    }

    public int hashCode() {
        int h = this.hash;
        if (h == 0) {
            int size = size();
            h = partialHash(size, 0, size);
            if (h == 0) {
                h = 1;
            }
            this.hash = h;
        }
        return h;
    }

    /* access modifiers changed from: protected */
    public int peekCachedHashCode() {
        return this.hash;
    }

    /* access modifiers changed from: protected */
    public int partialHash(int h, int offset, int length) {
        byte[] thisBytes = this.bytes;
        int i = getOffsetIntoBytes() + offset;
        int limit = i + length;
        while (i < limit) {
            h = (h * 31) + thisBytes[i];
            i++;
        }
        return h;
    }

    public InputStream newInput() {
        return new ByteArrayInputStream(this.bytes, getOffsetIntoBytes(), size());
    }

    public CodedInputStream newCodedInput() {
        return CodedInputStream.newInstance(this.bytes, getOffsetIntoBytes(), size());
    }

    public ByteString.ByteIterator iterator() {
        return new LiteralByteIterator();
    }

    private class LiteralByteIterator implements ByteString.ByteIterator {
        private final int limit;
        private int position;

        private LiteralByteIterator() {
            this.position = 0;
            this.limit = LiteralByteString.this.size();
        }

        public boolean hasNext() {
            return this.position < this.limit;
        }

        public Byte next() {
            return Byte.valueOf(nextByte());
        }

        public byte nextByte() {
            try {
                byte[] bArr = LiteralByteString.this.bytes;
                int i = this.position;
                this.position = i + 1;
                return bArr[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* access modifiers changed from: protected */
    public int getTreeDepth() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public boolean isBalanced() {
        return true;
    }

    /* access modifiers changed from: protected */
    public int getOffsetIntoBytes() {
        return 0;
    }
}
