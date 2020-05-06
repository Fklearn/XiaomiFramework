package com.google.android.exoplayer2.text.pgs;

import android.graphics.Bitmap;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public final class PgsDecoder extends SimpleSubtitleDecoder {
    private static final byte INFLATE_HEADER = 120;
    private static final int SECTION_TYPE_BITMAP_PICTURE = 21;
    private static final int SECTION_TYPE_END = 128;
    private static final int SECTION_TYPE_IDENTIFIER = 22;
    private static final int SECTION_TYPE_PALETTE = 20;
    private final ParsableByteArray buffer = new ParsableByteArray();
    private final CueBuilder cueBuilder = new CueBuilder();
    private byte[] inflatedData;
    private int inflatedDataSize;
    private Inflater inflater;

    private static final class CueBuilder {
        private final ParsableByteArray bitmapData = new ParsableByteArray();
        private int bitmapHeight;
        private int bitmapWidth;
        private int bitmapX;
        private int bitmapY;
        private final int[] colors = new int[256];
        private boolean colorsSet;
        private int planeHeight;
        private int planeWidth;

        /* access modifiers changed from: private */
        public void parseBitmapSection(ParsableByteArray parsableByteArray, int i) {
            int readUnsignedInt24;
            if (i >= 4) {
                parsableByteArray.skipBytes(3);
                int i2 = i - 4;
                if ((parsableByteArray.readUnsignedByte() & PgsDecoder.SECTION_TYPE_END) != 0) {
                    if (i2 >= 7 && (readUnsignedInt24 = parsableByteArray.readUnsignedInt24()) >= 4) {
                        this.bitmapWidth = parsableByteArray.readUnsignedShort();
                        this.bitmapHeight = parsableByteArray.readUnsignedShort();
                        this.bitmapData.reset(readUnsignedInt24 - 4);
                        i2 -= 7;
                    } else {
                        return;
                    }
                }
                int position = this.bitmapData.getPosition();
                int limit = this.bitmapData.limit();
                if (position < limit && i2 > 0) {
                    int min = Math.min(i2, limit - position);
                    parsableByteArray.readBytes(this.bitmapData.data, position, min);
                    this.bitmapData.setPosition(position + min);
                }
            }
        }

        /* access modifiers changed from: private */
        public void parseIdentifierSection(ParsableByteArray parsableByteArray, int i) {
            if (i >= 19) {
                this.planeWidth = parsableByteArray.readUnsignedShort();
                this.planeHeight = parsableByteArray.readUnsignedShort();
                parsableByteArray.skipBytes(11);
                this.bitmapX = parsableByteArray.readUnsignedShort();
                this.bitmapY = parsableByteArray.readUnsignedShort();
            }
        }

        /* access modifiers changed from: private */
        public void parsePaletteSection(ParsableByteArray parsableByteArray, int i) {
            if (i % 5 == 2) {
                parsableByteArray.skipBytes(2);
                Arrays.fill(this.colors, 0);
                int i2 = i / 5;
                int i3 = 0;
                while (i3 < i2) {
                    int readUnsignedByte = parsableByteArray.readUnsignedByte();
                    int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
                    int readUnsignedByte3 = parsableByteArray.readUnsignedByte();
                    int readUnsignedByte4 = parsableByteArray.readUnsignedByte();
                    int readUnsignedByte5 = parsableByteArray.readUnsignedByte();
                    double d2 = (double) readUnsignedByte2;
                    double d3 = (double) (readUnsignedByte3 - 128);
                    int i4 = i3;
                    double d4 = (double) (readUnsignedByte4 - 128);
                    this.colors[readUnsignedByte] = Util.constrainValue((int) (d2 + (d4 * 1.772d)), 0, 255) | (Util.constrainValue((int) ((d2 - (0.34414d * d4)) - (d3 * 0.71414d)), 0, 255) << 8) | (readUnsignedByte5 << 24) | (Util.constrainValue((int) ((1.402d * d3) + d2), 0, 255) << 16);
                    i3 = i4 + 1;
                }
                this.colorsSet = true;
            }
        }

        public Cue build() {
            int readUnsignedByte;
            if (this.planeWidth == 0 || this.planeHeight == 0 || this.bitmapWidth == 0 || this.bitmapHeight == 0 || this.bitmapData.limit() == 0 || this.bitmapData.getPosition() != this.bitmapData.limit() || !this.colorsSet) {
                return null;
            }
            this.bitmapData.setPosition(0);
            int[] iArr = new int[(this.bitmapWidth * this.bitmapHeight)];
            int i = 0;
            while (i < iArr.length) {
                int readUnsignedByte2 = this.bitmapData.readUnsignedByte();
                if (readUnsignedByte2 != 0) {
                    readUnsignedByte = i + 1;
                    iArr[i] = this.colors[readUnsignedByte2];
                } else {
                    int readUnsignedByte3 = this.bitmapData.readUnsignedByte();
                    if (readUnsignedByte3 != 0) {
                        readUnsignedByte = ((readUnsignedByte3 & 64) == 0 ? readUnsignedByte3 & 63 : ((readUnsignedByte3 & 63) << 8) | this.bitmapData.readUnsignedByte()) + i;
                        Arrays.fill(iArr, i, readUnsignedByte, (readUnsignedByte3 & PgsDecoder.SECTION_TYPE_END) == 0 ? 0 : this.colors[this.bitmapData.readUnsignedByte()]);
                    }
                }
                i = readUnsignedByte;
            }
            Bitmap createBitmap = Bitmap.createBitmap(iArr, this.bitmapWidth, this.bitmapHeight, Bitmap.Config.ARGB_8888);
            int i2 = this.planeWidth;
            float f = ((float) this.bitmapX) / ((float) i2);
            int i3 = this.planeHeight;
            return new Cue(createBitmap, f, 0, ((float) this.bitmapY) / ((float) i3), 0, ((float) this.bitmapWidth) / ((float) i2), ((float) this.bitmapHeight) / ((float) i3));
        }

        public void reset() {
            this.planeWidth = 0;
            this.planeHeight = 0;
            this.bitmapX = 0;
            this.bitmapY = 0;
            this.bitmapWidth = 0;
            this.bitmapHeight = 0;
            this.bitmapData.reset(0);
            this.colorsSet = false;
        }
    }

    public PgsDecoder() {
        super("PgsDecoder");
    }

    private boolean maybeInflateData(byte[] bArr, int i) {
        if (i != 0 && bArr[0] == 120) {
            if (this.inflater == null) {
                this.inflater = new Inflater();
                this.inflatedData = new byte[i];
            }
            this.inflatedDataSize = 0;
            this.inflater.setInput(bArr, 0, i);
            while (!this.inflater.finished() && !this.inflater.needsDictionary() && !this.inflater.needsInput()) {
                try {
                    if (this.inflatedDataSize == this.inflatedData.length) {
                        this.inflatedData = Arrays.copyOf(this.inflatedData, this.inflatedData.length * 2);
                    }
                    this.inflatedDataSize += this.inflater.inflate(this.inflatedData, this.inflatedDataSize, this.inflatedData.length - this.inflatedDataSize);
                } catch (DataFormatException unused) {
                } finally {
                    this.inflater.reset();
                }
            }
            return this.inflater.finished();
        }
        return false;
    }

    private static Cue readNextSection(ParsableByteArray parsableByteArray, CueBuilder cueBuilder2) {
        int limit = parsableByteArray.limit();
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int position = parsableByteArray.getPosition() + readUnsignedShort;
        Cue cue = null;
        if (position > limit) {
            parsableByteArray.setPosition(limit);
            return null;
        }
        if (readUnsignedByte != SECTION_TYPE_END) {
            switch (readUnsignedByte) {
                case 20:
                    cueBuilder2.parsePaletteSection(parsableByteArray, readUnsignedShort);
                    break;
                case 21:
                    cueBuilder2.parseBitmapSection(parsableByteArray, readUnsignedShort);
                    break;
                case 22:
                    cueBuilder2.parseIdentifierSection(parsableByteArray, readUnsignedShort);
                    break;
            }
        } else {
            cue = cueBuilder2.build();
            cueBuilder2.reset();
        }
        parsableByteArray.setPosition(position);
        return cue;
    }

    /* access modifiers changed from: protected */
    public Subtitle decode(byte[] bArr, int i, boolean z) {
        if (maybeInflateData(bArr, i)) {
            this.buffer.reset(this.inflatedData, this.inflatedDataSize);
        } else {
            this.buffer.reset(bArr, i);
        }
        this.cueBuilder.reset();
        ArrayList arrayList = new ArrayList();
        while (this.buffer.bytesLeft() >= 3) {
            Cue readNextSection = readNextSection(this.buffer, this.cueBuilder);
            if (readNextSection != null) {
                arrayList.add(readNextSection);
            }
        }
        return new PgsSubtitle(Collections.unmodifiableList(arrayList));
    }
}
