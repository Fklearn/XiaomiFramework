package com.google.protobuf;

import com.google.protobuf.ByteString;
import java.util.NoSuchElementException;

class BoundedByteString extends LiteralByteString {
    private final int bytesLength;
    private final int bytesOffset;

    BoundedByteString(byte[] bytes, int offset, int length) {
        super(bytes);
        if (offset < 0) {
            throw new IllegalArgumentException("Offset too small: " + offset);
        } else if (length < 0) {
            throw new IllegalArgumentException("Length too small: " + offset);
        } else if (((long) offset) + ((long) length) <= ((long) bytes.length)) {
            this.bytesOffset = offset;
            this.bytesLength = length;
        } else {
            throw new IllegalArgumentException("Offset+Length too large: " + offset + "+" + length);
        }
    }

    public byte byteAt(int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("Index too small: " + index);
        } else if (index < size()) {
            return this.bytes[this.bytesOffset + index];
        } else {
            throw new ArrayIndexOutOfBoundsException("Index too large: " + index + ", " + size());
        }
    }

    public int size() {
        return this.bytesLength;
    }

    /* access modifiers changed from: protected */
    public int getOffsetIntoBytes() {
        return this.bytesOffset;
    }

    /* access modifiers changed from: protected */
    public void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
        System.arraycopy(this.bytes, getOffsetIntoBytes() + sourceOffset, target, targetOffset, numberToCopy);
    }

    public ByteString.ByteIterator iterator() {
        return new BoundedByteIterator();
    }

    private class BoundedByteIterator implements ByteString.ByteIterator {
        private final int limit;
        private int position;

        private BoundedByteIterator() {
            this.position = BoundedByteString.this.getOffsetIntoBytes();
            this.limit = this.position + BoundedByteString.this.size();
        }

        public boolean hasNext() {
            return this.position < this.limit;
        }

        public Byte next() {
            return Byte.valueOf(nextByte());
        }

        public byte nextByte() {
            if (this.position < this.limit) {
                byte[] bArr = BoundedByteString.this.bytes;
                int i = this.position;
                this.position = i + 1;
                return bArr[i];
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
