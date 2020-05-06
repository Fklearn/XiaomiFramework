package com.google.android.exoplayer2.text.dvb;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.util.SparseArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.Util;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DvbParser {
    private static final int DATA_TYPE_24_TABLE_DATA = 32;
    private static final int DATA_TYPE_28_TABLE_DATA = 33;
    private static final int DATA_TYPE_2BP_CODE_STRING = 16;
    private static final int DATA_TYPE_48_TABLE_DATA = 34;
    private static final int DATA_TYPE_4BP_CODE_STRING = 17;
    private static final int DATA_TYPE_8BP_CODE_STRING = 18;
    private static final int DATA_TYPE_END_LINE = 240;
    private static final int OBJECT_CODING_PIXELS = 0;
    private static final int OBJECT_CODING_STRING = 1;
    private static final int PAGE_STATE_NORMAL = 0;
    private static final int REGION_DEPTH_4_BIT = 2;
    private static final int REGION_DEPTH_8_BIT = 3;
    private static final int SEGMENT_TYPE_CLUT_DEFINITION = 18;
    private static final int SEGMENT_TYPE_DISPLAY_DEFINITION = 20;
    private static final int SEGMENT_TYPE_OBJECT_DATA = 19;
    private static final int SEGMENT_TYPE_PAGE_COMPOSITION = 16;
    private static final int SEGMENT_TYPE_REGION_COMPOSITION = 17;
    private static final String TAG = "DvbParser";
    private static final byte[] defaultMap2To4 = {0, 7, 8, 15};
    private static final byte[] defaultMap2To8 = {0, 119, -120, -1};
    private static final byte[] defaultMap4To8 = {0, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1};
    private Bitmap bitmap;
    private final Canvas canvas;
    private final ClutDefinition defaultClutDefinition;
    private final DisplayDefinition defaultDisplayDefinition;
    private final Paint defaultPaint = new Paint();
    private final Paint fillRegionPaint;
    private final SubtitleService subtitleService;

    private static final class ClutDefinition {
        public final int[] clutEntries2Bit;
        public final int[] clutEntries4Bit;
        public final int[] clutEntries8Bit;
        public final int id;

        public ClutDefinition(int i, int[] iArr, int[] iArr2, int[] iArr3) {
            this.id = i;
            this.clutEntries2Bit = iArr;
            this.clutEntries4Bit = iArr2;
            this.clutEntries8Bit = iArr3;
        }
    }

    private static final class DisplayDefinition {
        public final int height;
        public final int horizontalPositionMaximum;
        public final int horizontalPositionMinimum;
        public final int verticalPositionMaximum;
        public final int verticalPositionMinimum;
        public final int width;

        public DisplayDefinition(int i, int i2, int i3, int i4, int i5, int i6) {
            this.width = i;
            this.height = i2;
            this.horizontalPositionMinimum = i3;
            this.horizontalPositionMaximum = i4;
            this.verticalPositionMinimum = i5;
            this.verticalPositionMaximum = i6;
        }
    }

    private static final class ObjectData {
        public final byte[] bottomFieldData;
        public final int id;
        public final boolean nonModifyingColorFlag;
        public final byte[] topFieldData;

        public ObjectData(int i, boolean z, byte[] bArr, byte[] bArr2) {
            this.id = i;
            this.nonModifyingColorFlag = z;
            this.topFieldData = bArr;
            this.bottomFieldData = bArr2;
        }
    }

    private static final class PageComposition {
        public final SparseArray<PageRegion> regions;
        public final int state;
        public final int timeOutSecs;
        public final int version;

        public PageComposition(int i, int i2, int i3, SparseArray<PageRegion> sparseArray) {
            this.timeOutSecs = i;
            this.version = i2;
            this.state = i3;
            this.regions = sparseArray;
        }
    }

    private static final class PageRegion {
        public final int horizontalAddress;
        public final int verticalAddress;

        public PageRegion(int i, int i2) {
            this.horizontalAddress = i;
            this.verticalAddress = i2;
        }
    }

    private static final class RegionComposition {
        public final int clutId;
        public final int depth;
        public final boolean fillFlag;
        public final int height;
        public final int id;
        public final int levelOfCompatibility;
        public final int pixelCode2Bit;
        public final int pixelCode4Bit;
        public final int pixelCode8Bit;
        public final SparseArray<RegionObject> regionObjects;
        public final int width;

        public RegionComposition(int i, boolean z, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, SparseArray<RegionObject> sparseArray) {
            this.id = i;
            this.fillFlag = z;
            this.width = i2;
            this.height = i3;
            this.levelOfCompatibility = i4;
            this.depth = i5;
            this.clutId = i6;
            this.pixelCode8Bit = i7;
            this.pixelCode4Bit = i8;
            this.pixelCode2Bit = i9;
            this.regionObjects = sparseArray;
        }

        public void mergeFrom(RegionComposition regionComposition) {
            if (regionComposition != null) {
                SparseArray<RegionObject> sparseArray = regionComposition.regionObjects;
                for (int i = 0; i < sparseArray.size(); i++) {
                    this.regionObjects.put(sparseArray.keyAt(i), sparseArray.valueAt(i));
                }
            }
        }
    }

    private static final class RegionObject {
        public final int backgroundPixelCode;
        public final int foregroundPixelCode;
        public final int horizontalPosition;
        public final int provider;
        public final int type;
        public final int verticalPosition;

        public RegionObject(int i, int i2, int i3, int i4, int i5, int i6) {
            this.type = i;
            this.provider = i2;
            this.horizontalPosition = i3;
            this.verticalPosition = i4;
            this.foregroundPixelCode = i5;
            this.backgroundPixelCode = i6;
        }
    }

    private static final class SubtitleService {
        public final SparseArray<ClutDefinition> ancillaryCluts = new SparseArray<>();
        public final SparseArray<ObjectData> ancillaryObjects = new SparseArray<>();
        public final int ancillaryPageId;
        public final SparseArray<ClutDefinition> cluts = new SparseArray<>();
        public DisplayDefinition displayDefinition;
        public final SparseArray<ObjectData> objects = new SparseArray<>();
        public PageComposition pageComposition;
        public final SparseArray<RegionComposition> regions = new SparseArray<>();
        public final int subtitlePageId;

        public SubtitleService(int i, int i2) {
            this.subtitlePageId = i;
            this.ancillaryPageId = i2;
        }

        public void reset() {
            this.regions.clear();
            this.cluts.clear();
            this.objects.clear();
            this.ancillaryCluts.clear();
            this.ancillaryObjects.clear();
            this.displayDefinition = null;
            this.pageComposition = null;
        }
    }

    public DvbParser(int i, int i2) {
        this.defaultPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.defaultPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        this.defaultPaint.setPathEffect((PathEffect) null);
        this.fillRegionPaint = new Paint();
        this.fillRegionPaint.setStyle(Paint.Style.FILL);
        this.fillRegionPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        this.fillRegionPaint.setPathEffect((PathEffect) null);
        this.canvas = new Canvas();
        this.defaultDisplayDefinition = new DisplayDefinition(719, 575, 0, 719, 0, 575);
        this.defaultClutDefinition = new ClutDefinition(0, generateDefault2BitClutEntries(), generateDefault4BitClutEntries(), generateDefault8BitClutEntries());
        this.subtitleService = new SubtitleService(i, i2);
    }

    private static byte[] buildClutMapTable(int i, int i2, ParsableBitArray parsableBitArray) {
        byte[] bArr = new byte[i];
        for (int i3 = 0; i3 < i; i3++) {
            bArr[i3] = (byte) parsableBitArray.readBits(i2);
        }
        return bArr;
    }

    private static int[] generateDefault2BitClutEntries() {
        return new int[]{0, -1, RoundedDrawable.DEFAULT_BORDER_COLOR, -8421505};
    }

    private static int[] generateDefault4BitClutEntries() {
        int[] iArr = new int[16];
        iArr[0] = 0;
        for (int i = 1; i < iArr.length; i++) {
            if (i < 8) {
                iArr[i] = getColor(255, (i & 1) != 0 ? 255 : 0, (i & 2) != 0 ? 255 : 0, (i & 4) != 0 ? 255 : 0);
            } else {
                int i2 = 127;
                int i3 = (i & 1) != 0 ? 127 : 0;
                int i4 = (i & 2) != 0 ? 127 : 0;
                if ((i & 4) == 0) {
                    i2 = 0;
                }
                iArr[i] = getColor(255, i3, i4, i2);
            }
        }
        return iArr;
    }

    private static int[] generateDefault8BitClutEntries() {
        int[] iArr = new int[256];
        iArr[0] = 0;
        for (int i = 0; i < iArr.length; i++) {
            int i2 = 255;
            if (i < 8) {
                int i3 = (i & 1) != 0 ? 255 : 0;
                int i4 = (i & 2) != 0 ? 255 : 0;
                if ((i & 4) == 0) {
                    i2 = 0;
                }
                iArr[i] = getColor(63, i3, i4, i2);
            } else {
                int i5 = i & 136;
                int i6 = 170;
                int i7 = 85;
                if (i5 == 0) {
                    int i8 = ((i & 1) != 0 ? 85 : 0) + ((i & 16) != 0 ? 170 : 0);
                    int i9 = ((i & 2) != 0 ? 85 : 0) + ((i & 32) != 0 ? 170 : 0);
                    if ((i & 4) == 0) {
                        i7 = 0;
                    }
                    if ((i & 64) == 0) {
                        i6 = 0;
                    }
                    iArr[i] = getColor(255, i8, i9, i7 + i6);
                } else if (i5 != 8) {
                    int i10 = 43;
                    if (i5 == 128) {
                        int i11 = ((i & 1) != 0 ? 43 : 0) + 127 + ((i & 16) != 0 ? 85 : 0);
                        int i12 = ((i & 2) != 0 ? 43 : 0) + 127 + ((i & 32) != 0 ? 85 : 0);
                        if ((i & 4) == 0) {
                            i10 = 0;
                        }
                        int i13 = i10 + 127;
                        if ((i & 64) == 0) {
                            i7 = 0;
                        }
                        iArr[i] = getColor(255, i11, i12, i13 + i7);
                    } else if (i5 == 136) {
                        int i14 = ((i & 1) != 0 ? 43 : 0) + ((i & 16) != 0 ? 85 : 0);
                        int i15 = ((i & 2) != 0 ? 43 : 0) + ((i & 32) != 0 ? 85 : 0);
                        if ((i & 4) == 0) {
                            i10 = 0;
                        }
                        if ((i & 64) == 0) {
                            i7 = 0;
                        }
                        iArr[i] = getColor(255, i14, i15, i10 + i7);
                    }
                } else {
                    int i16 = ((i & 1) != 0 ? 85 : 0) + ((i & 16) != 0 ? 170 : 0);
                    int i17 = ((i & 2) != 0 ? 85 : 0) + ((i & 32) != 0 ? 170 : 0);
                    if ((i & 4) == 0) {
                        i7 = 0;
                    }
                    if ((i & 64) == 0) {
                        i6 = 0;
                    }
                    iArr[i] = getColor(127, i16, i17, i7 + i6);
                }
            }
        }
        return iArr;
    }

    private static int getColor(int i, int i2, int i3, int i4) {
        return (i << 24) | (i2 << 16) | (i3 << 8) | i4;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int paint2BitPixelCodeString(com.google.android.exoplayer2.util.ParsableBitArray r13, int[] r14, byte[] r15, int r16, int r17, android.graphics.Paint r18, android.graphics.Canvas r19) {
        /*
            r0 = r13
            r1 = r17
            r8 = r18
            r9 = 0
            r10 = r16
            r2 = r9
        L_0x0009:
            r3 = 2
            int r4 = r13.readBits(r3)
            r5 = 1
            if (r4 == 0) goto L_0x0015
            r12 = r2
            r3 = r4
            r11 = r5
            goto L_0x0058
        L_0x0015:
            boolean r4 = r13.readBit()
            r6 = 3
            if (r4 == 0) goto L_0x0028
            int r4 = r13.readBits(r6)
            int r4 = r4 + r6
        L_0x0021:
            int r3 = r13.readBits(r3)
            r12 = r2
            r11 = r4
            goto L_0x0058
        L_0x0028:
            boolean r4 = r13.readBit()
            if (r4 == 0) goto L_0x0032
            r12 = r2
            r11 = r5
        L_0x0030:
            r3 = r9
            goto L_0x0058
        L_0x0032:
            int r4 = r13.readBits(r3)
            if (r4 == 0) goto L_0x0056
            if (r4 == r5) goto L_0x0053
            if (r4 == r3) goto L_0x004b
            if (r4 == r6) goto L_0x0042
            r12 = r2
        L_0x003f:
            r3 = r9
            r11 = r3
            goto L_0x0058
        L_0x0042:
            r4 = 8
            int r4 = r13.readBits(r4)
            int r4 = r4 + 29
            goto L_0x0021
        L_0x004b:
            r4 = 4
            int r4 = r13.readBits(r4)
            int r4 = r4 + 12
            goto L_0x0021
        L_0x0053:
            r12 = r2
            r11 = r3
            goto L_0x0030
        L_0x0056:
            r12 = r5
            goto L_0x003f
        L_0x0058:
            if (r11 == 0) goto L_0x0076
            if (r8 == 0) goto L_0x0076
            if (r15 == 0) goto L_0x0060
            byte r3 = r15[r3]
        L_0x0060:
            r2 = r14[r3]
            r8.setColor(r2)
            float r3 = (float) r10
            float r4 = (float) r1
            int r2 = r10 + r11
            float r6 = (float) r2
            int r2 = r1 + 1
            float r7 = (float) r2
            r2 = r19
            r5 = r6
            r6 = r7
            r7 = r18
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x0076:
            int r10 = r10 + r11
            if (r12 == 0) goto L_0x007a
            return r10
        L_0x007a:
            r2 = r12
            goto L_0x0009
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.dvb.DvbParser.paint2BitPixelCodeString(com.google.android.exoplayer2.util.ParsableBitArray, int[], byte[], int, int, android.graphics.Paint, android.graphics.Canvas):int");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int paint4BitPixelCodeString(com.google.android.exoplayer2.util.ParsableBitArray r13, int[] r14, byte[] r15, int r16, int r17, android.graphics.Paint r18, android.graphics.Canvas r19) {
        /*
            r0 = r13
            r1 = r17
            r8 = r18
            r9 = 0
            r10 = r16
            r2 = r9
        L_0x0009:
            r3 = 4
            int r4 = r13.readBits(r3)
            r5 = 2
            r6 = 1
            if (r4 == 0) goto L_0x0017
            r12 = r2
            r3 = r4
            r11 = r6
            goto L_0x0064
        L_0x0017:
            boolean r4 = r13.readBit()
            r7 = 3
            if (r4 != 0) goto L_0x002c
            int r3 = r13.readBits(r7)
            if (r3 == 0) goto L_0x002a
            int r3 = r3 + 2
            r12 = r2
            r11 = r3
        L_0x0028:
            r3 = r9
            goto L_0x0064
        L_0x002a:
            r12 = r6
            goto L_0x004b
        L_0x002c:
            boolean r4 = r13.readBit()
            if (r4 != 0) goto L_0x003e
            int r4 = r13.readBits(r5)
            int r4 = r4 + r3
        L_0x0037:
            int r3 = r13.readBits(r3)
            r12 = r2
            r11 = r4
            goto L_0x0064
        L_0x003e:
            int r4 = r13.readBits(r5)
            if (r4 == 0) goto L_0x0061
            if (r4 == r6) goto L_0x005e
            if (r4 == r5) goto L_0x0057
            if (r4 == r7) goto L_0x004e
            r12 = r2
        L_0x004b:
            r3 = r9
            r11 = r3
            goto L_0x0064
        L_0x004e:
            r4 = 8
            int r4 = r13.readBits(r4)
            int r4 = r4 + 25
            goto L_0x0037
        L_0x0057:
            int r4 = r13.readBits(r3)
            int r4 = r4 + 9
            goto L_0x0037
        L_0x005e:
            r12 = r2
            r11 = r5
            goto L_0x0028
        L_0x0061:
            r12 = r2
            r11 = r6
            goto L_0x0028
        L_0x0064:
            if (r11 == 0) goto L_0x0080
            if (r8 == 0) goto L_0x0080
            if (r15 == 0) goto L_0x006c
            byte r3 = r15[r3]
        L_0x006c:
            r2 = r14[r3]
            r8.setColor(r2)
            float r3 = (float) r10
            float r4 = (float) r1
            int r2 = r10 + r11
            float r5 = (float) r2
            int r2 = r1 + 1
            float r6 = (float) r2
            r2 = r19
            r7 = r18
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x0080:
            int r10 = r10 + r11
            if (r12 == 0) goto L_0x0084
            return r10
        L_0x0084:
            r2 = r12
            goto L_0x0009
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.dvb.DvbParser.paint4BitPixelCodeString(com.google.android.exoplayer2.util.ParsableBitArray, int[], byte[], int, int, android.graphics.Paint, android.graphics.Canvas):int");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int paint8BitPixelCodeString(com.google.android.exoplayer2.util.ParsableBitArray r13, int[] r14, byte[] r15, int r16, int r17, android.graphics.Paint r18, android.graphics.Canvas r19) {
        /*
            r0 = r13
            r1 = r17
            r8 = r18
            r9 = 0
            r10 = r16
            r2 = r9
        L_0x0009:
            r3 = 8
            int r4 = r13.readBits(r3)
            r5 = 1
            if (r4 == 0) goto L_0x0016
            r12 = r2
            r3 = r4
            r11 = r5
            goto L_0x0035
        L_0x0016:
            boolean r4 = r13.readBit()
            r6 = 7
            if (r4 != 0) goto L_0x002b
            int r3 = r13.readBits(r6)
            if (r3 == 0) goto L_0x0027
            r12 = r2
            r11 = r3
            r3 = r9
            goto L_0x0035
        L_0x0027:
            r12 = r5
            r3 = r9
            r11 = r3
            goto L_0x0035
        L_0x002b:
            int r4 = r13.readBits(r6)
            int r3 = r13.readBits(r3)
            r12 = r2
            r11 = r4
        L_0x0035:
            if (r11 == 0) goto L_0x0053
            if (r8 == 0) goto L_0x0053
            if (r15 == 0) goto L_0x003d
            byte r3 = r15[r3]
        L_0x003d:
            r2 = r14[r3]
            r8.setColor(r2)
            float r3 = (float) r10
            float r4 = (float) r1
            int r2 = r10 + r11
            float r6 = (float) r2
            int r2 = r1 + 1
            float r7 = (float) r2
            r2 = r19
            r5 = r6
            r6 = r7
            r7 = r18
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x0053:
            int r10 = r10 + r11
            if (r12 == 0) goto L_0x0057
            return r10
        L_0x0057:
            r2 = r12
            goto L_0x0009
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.dvb.DvbParser.paint8BitPixelCodeString(com.google.android.exoplayer2.util.ParsableBitArray, int[], byte[], int, int, android.graphics.Paint, android.graphics.Canvas):int");
    }

    private static void paintPixelDataSubBlock(byte[] bArr, int[] iArr, int i, int i2, int i3, Paint paint, Canvas canvas2) {
        byte[] bArr2;
        int paint2BitPixelCodeString;
        byte[] bArr3;
        byte[] bArr4;
        int i4 = i;
        byte[] bArr5 = bArr;
        ParsableBitArray parsableBitArray = new ParsableBitArray(bArr);
        int i5 = i2;
        int i6 = i3;
        byte[] bArr6 = null;
        byte[] bArr7 = null;
        while (parsableBitArray.bitsLeft() != 0) {
            int readBits = parsableBitArray.readBits(8);
            if (readBits != 240) {
                switch (readBits) {
                    case 16:
                        if (i4 != 3) {
                            if (i4 != 2) {
                                bArr2 = null;
                                paint2BitPixelCodeString = paint2BitPixelCodeString(parsableBitArray, iArr, bArr2, i5, i6, paint, canvas2);
                                break;
                            } else {
                                bArr3 = bArr7 == null ? defaultMap2To4 : bArr7;
                            }
                        } else {
                            bArr3 = bArr6 == null ? defaultMap2To8 : bArr6;
                        }
                        bArr2 = bArr3;
                        paint2BitPixelCodeString = paint2BitPixelCodeString(parsableBitArray, iArr, bArr2, i5, i6, paint, canvas2);
                    case 17:
                        paint2BitPixelCodeString = paint4BitPixelCodeString(parsableBitArray, iArr, i4 == 3 ? defaultMap4To8 : null, i5, i6, paint, canvas2);
                        break;
                    case 18:
                        paint2BitPixelCodeString = paint8BitPixelCodeString(parsableBitArray, iArr, (byte[]) null, i5, i6, paint, canvas2);
                        break;
                    default:
                        switch (readBits) {
                            case 32:
                                bArr7 = buildClutMapTable(4, 4, parsableBitArray);
                                continue;
                            case 33:
                                bArr4 = buildClutMapTable(4, 8, parsableBitArray);
                                break;
                            case 34:
                                bArr4 = buildClutMapTable(16, 8, parsableBitArray);
                                break;
                            default:
                                continue;
                        }
                        bArr6 = bArr4;
                        break;
                }
                parsableBitArray.byteAlign();
                i5 = paint2BitPixelCodeString;
            } else {
                i6 += 2;
                i5 = i2;
            }
        }
    }

    private static void paintPixelDataSubBlocks(ObjectData objectData, ClutDefinition clutDefinition, int i, int i2, int i3, Paint paint, Canvas canvas2) {
        int[] iArr = i == 3 ? clutDefinition.clutEntries8Bit : i == 2 ? clutDefinition.clutEntries4Bit : clutDefinition.clutEntries2Bit;
        int i4 = i;
        int i5 = i2;
        Paint paint2 = paint;
        Canvas canvas3 = canvas2;
        paintPixelDataSubBlock(objectData.topFieldData, iArr, i4, i5, i3, paint2, canvas3);
        paintPixelDataSubBlock(objectData.bottomFieldData, iArr, i4, i5, i3 + 1, paint2, canvas3);
    }

    private static ClutDefinition parseClutDefinition(ParsableBitArray parsableBitArray, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        ParsableBitArray parsableBitArray2 = parsableBitArray;
        int i7 = 8;
        int readBits = parsableBitArray2.readBits(8);
        parsableBitArray2.skipBits(8);
        int i8 = 2;
        int i9 = i - 2;
        int[] generateDefault2BitClutEntries = generateDefault2BitClutEntries();
        int[] generateDefault4BitClutEntries = generateDefault4BitClutEntries();
        int[] generateDefault8BitClutEntries = generateDefault8BitClutEntries();
        while (i9 > 0) {
            int readBits2 = parsableBitArray2.readBits(i7);
            int readBits3 = parsableBitArray2.readBits(i7);
            int i10 = i9 - 2;
            int[] iArr = (readBits3 & 128) != 0 ? generateDefault2BitClutEntries : (readBits3 & 64) != 0 ? generateDefault4BitClutEntries : generateDefault8BitClutEntries;
            if ((readBits3 & 1) != 0) {
                i5 = parsableBitArray2.readBits(i7);
                i4 = parsableBitArray2.readBits(i7);
                i3 = parsableBitArray2.readBits(i7);
                i2 = parsableBitArray2.readBits(i7);
                i6 = i10 - 4;
            } else {
                i3 = parsableBitArray2.readBits(4) << 4;
                i6 = i10 - 2;
                int readBits4 = parsableBitArray2.readBits(4) << 4;
                i2 = parsableBitArray2.readBits(i8) << 6;
                i5 = parsableBitArray2.readBits(6) << i8;
                i4 = readBits4;
            }
            if (i5 == 0) {
                i2 = 255;
                i4 = 0;
                i3 = 0;
            }
            double d2 = (double) i5;
            double d3 = (double) (i4 - 128);
            double d4 = (double) (i3 - 128);
            iArr[readBits2] = getColor((byte) (255 - (i2 & 255)), Util.constrainValue((int) (d2 + (1.402d * d3)), 0, 255), Util.constrainValue((int) ((d2 - (0.34414d * d4)) - (d3 * 0.71414d)), 0, 255), Util.constrainValue((int) (d2 + (d4 * 1.772d)), 0, 255));
            i9 = i6;
            readBits = readBits;
            i7 = 8;
            i8 = 2;
        }
        return new ClutDefinition(readBits, generateDefault2BitClutEntries, generateDefault4BitClutEntries, generateDefault8BitClutEntries);
    }

    private static DisplayDefinition parseDisplayDefinition(ParsableBitArray parsableBitArray) {
        int i;
        int i2;
        int i3;
        int i4;
        parsableBitArray.skipBits(4);
        boolean readBit = parsableBitArray.readBit();
        parsableBitArray.skipBits(3);
        int readBits = parsableBitArray.readBits(16);
        int readBits2 = parsableBitArray.readBits(16);
        if (readBit) {
            int readBits3 = parsableBitArray.readBits(16);
            int readBits4 = parsableBitArray.readBits(16);
            int readBits5 = parsableBitArray.readBits(16);
            i = parsableBitArray.readBits(16);
            i3 = readBits4;
            i2 = readBits5;
            i4 = readBits3;
        } else {
            i4 = 0;
            i2 = 0;
            i3 = readBits;
            i = readBits2;
        }
        return new DisplayDefinition(readBits, readBits2, i4, i3, i2, i);
    }

    private static ObjectData parseObjectData(ParsableBitArray parsableBitArray) {
        byte[] bArr;
        int readBits = parsableBitArray.readBits(16);
        parsableBitArray.skipBits(4);
        int readBits2 = parsableBitArray.readBits(2);
        boolean readBit = parsableBitArray.readBit();
        parsableBitArray.skipBits(1);
        byte[] bArr2 = null;
        if (readBits2 == 1) {
            parsableBitArray.skipBits(parsableBitArray.readBits(8) * 16);
        } else if (readBits2 == 0) {
            int readBits3 = parsableBitArray.readBits(16);
            int readBits4 = parsableBitArray.readBits(16);
            if (readBits3 > 0) {
                bArr2 = new byte[readBits3];
                parsableBitArray.readBytes(bArr2, 0, readBits3);
            }
            if (readBits4 > 0) {
                bArr = new byte[readBits4];
                parsableBitArray.readBytes(bArr, 0, readBits4);
                return new ObjectData(readBits, readBit, bArr2, bArr);
            }
        }
        bArr = bArr2;
        return new ObjectData(readBits, readBit, bArr2, bArr);
    }

    private static PageComposition parsePageComposition(ParsableBitArray parsableBitArray, int i) {
        int readBits = parsableBitArray.readBits(8);
        int readBits2 = parsableBitArray.readBits(4);
        int readBits3 = parsableBitArray.readBits(2);
        parsableBitArray.skipBits(2);
        int i2 = i - 2;
        SparseArray sparseArray = new SparseArray();
        while (i2 > 0) {
            int readBits4 = parsableBitArray.readBits(8);
            parsableBitArray.skipBits(8);
            i2 -= 6;
            sparseArray.put(readBits4, new PageRegion(parsableBitArray.readBits(16), parsableBitArray.readBits(16)));
        }
        return new PageComposition(readBits, readBits2, readBits3, sparseArray);
    }

    private static RegionComposition parseRegionComposition(ParsableBitArray parsableBitArray, int i) {
        int i2;
        int i3;
        ParsableBitArray parsableBitArray2 = parsableBitArray;
        int readBits = parsableBitArray2.readBits(8);
        parsableBitArray2.skipBits(4);
        boolean readBit = parsableBitArray.readBit();
        parsableBitArray2.skipBits(3);
        int i4 = 16;
        int readBits2 = parsableBitArray2.readBits(16);
        int readBits3 = parsableBitArray2.readBits(16);
        int readBits4 = parsableBitArray2.readBits(3);
        int readBits5 = parsableBitArray2.readBits(3);
        int i5 = 2;
        parsableBitArray2.skipBits(2);
        int readBits6 = parsableBitArray2.readBits(8);
        int readBits7 = parsableBitArray2.readBits(8);
        int readBits8 = parsableBitArray2.readBits(4);
        int readBits9 = parsableBitArray2.readBits(2);
        parsableBitArray2.skipBits(2);
        int i6 = i - 10;
        SparseArray sparseArray = new SparseArray();
        while (i6 > 0) {
            int readBits10 = parsableBitArray2.readBits(i4);
            int readBits11 = parsableBitArray2.readBits(i5);
            int readBits12 = parsableBitArray2.readBits(i5);
            int readBits13 = parsableBitArray2.readBits(12);
            int i7 = readBits9;
            parsableBitArray2.skipBits(4);
            int readBits14 = parsableBitArray2.readBits(12);
            i6 -= 6;
            if (readBits11 == 1 || readBits11 == 2) {
                i6 -= 2;
                i3 = parsableBitArray2.readBits(8);
                i2 = parsableBitArray2.readBits(8);
            } else {
                i3 = 0;
                i2 = 0;
            }
            sparseArray.put(readBits10, new RegionObject(readBits11, readBits12, readBits13, readBits14, i3, i2));
            readBits9 = i7;
            i5 = 2;
            i4 = 16;
        }
        return new RegionComposition(readBits, readBit, readBits2, readBits3, readBits4, readBits5, readBits6, readBits7, readBits8, readBits9, sparseArray);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0061, code lost:
        r7.put(r1, r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void parseSubtitlingSegment(com.google.android.exoplayer2.util.ParsableBitArray r6, com.google.android.exoplayer2.text.dvb.DvbParser.SubtitleService r7) {
        /*
            r0 = 8
            int r0 = r6.readBits(r0)
            r1 = 16
            int r2 = r6.readBits(r1)
            int r1 = r6.readBits(r1)
            int r3 = r6.getBytePosition()
            int r3 = r3 + r1
            int r4 = r1 * 8
            int r5 = r6.bitsLeft()
            if (r4 <= r5) goto L_0x002c
            java.lang.String r7 = "DvbParser"
            java.lang.String r0 = "Data field length exceeds limit"
            android.util.Log.w(r7, r0)
            int r7 = r6.bitsLeft()
            r6.skipBits(r7)
            return
        L_0x002c:
            switch(r0) {
                case 16: goto L_0x0095;
                case 17: goto L_0x0070;
                case 18: goto L_0x0055;
                case 19: goto L_0x003d;
                case 20: goto L_0x0031;
                default: goto L_0x002f;
            }
        L_0x002f:
            goto L_0x00bf
        L_0x0031:
            int r0 = r7.subtitlePageId
            if (r2 != r0) goto L_0x00bf
            com.google.android.exoplayer2.text.dvb.DvbParser$DisplayDefinition r0 = parseDisplayDefinition(r6)
            r7.displayDefinition = r0
            goto L_0x00bf
        L_0x003d:
            int r0 = r7.subtitlePageId
            if (r2 != r0) goto L_0x004a
            com.google.android.exoplayer2.text.dvb.DvbParser$ObjectData r0 = parseObjectData(r6)
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$ObjectData> r7 = r7.objects
        L_0x0047:
            int r1 = r0.id
            goto L_0x0061
        L_0x004a:
            int r0 = r7.ancillaryPageId
            if (r2 != r0) goto L_0x00bf
            com.google.android.exoplayer2.text.dvb.DvbParser$ObjectData r0 = parseObjectData(r6)
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$ObjectData> r7 = r7.ancillaryObjects
            goto L_0x0047
        L_0x0055:
            int r0 = r7.subtitlePageId
            if (r2 != r0) goto L_0x0065
            com.google.android.exoplayer2.text.dvb.DvbParser$ClutDefinition r0 = parseClutDefinition(r6, r1)
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$ClutDefinition> r7 = r7.cluts
        L_0x005f:
            int r1 = r0.id
        L_0x0061:
            r7.put(r1, r0)
            goto L_0x00bf
        L_0x0065:
            int r0 = r7.ancillaryPageId
            if (r2 != r0) goto L_0x00bf
            com.google.android.exoplayer2.text.dvb.DvbParser$ClutDefinition r0 = parseClutDefinition(r6, r1)
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$ClutDefinition> r7 = r7.ancillaryCluts
            goto L_0x005f
        L_0x0070:
            com.google.android.exoplayer2.text.dvb.DvbParser$PageComposition r0 = r7.pageComposition
            int r4 = r7.subtitlePageId
            if (r2 != r4) goto L_0x00bf
            if (r0 == 0) goto L_0x00bf
            com.google.android.exoplayer2.text.dvb.DvbParser$RegionComposition r1 = parseRegionComposition(r6, r1)
            int r0 = r0.state
            if (r0 != 0) goto L_0x008d
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$RegionComposition> r0 = r7.regions
            int r2 = r1.id
            java.lang.Object r0 = r0.get(r2)
            com.google.android.exoplayer2.text.dvb.DvbParser$RegionComposition r0 = (com.google.android.exoplayer2.text.dvb.DvbParser.RegionComposition) r0
            r1.mergeFrom(r0)
        L_0x008d:
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$RegionComposition> r7 = r7.regions
            int r0 = r1.id
            r7.put(r0, r1)
            goto L_0x00bf
        L_0x0095:
            int r0 = r7.subtitlePageId
            if (r2 != r0) goto L_0x00bf
            com.google.android.exoplayer2.text.dvb.DvbParser$PageComposition r0 = r7.pageComposition
            com.google.android.exoplayer2.text.dvb.DvbParser$PageComposition r1 = parsePageComposition(r6, r1)
            int r2 = r1.state
            if (r2 == 0) goto L_0x00b5
            r7.pageComposition = r1
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$RegionComposition> r0 = r7.regions
            r0.clear()
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$ClutDefinition> r0 = r7.cluts
            r0.clear()
            android.util.SparseArray<com.google.android.exoplayer2.text.dvb.DvbParser$ObjectData> r7 = r7.objects
            r7.clear()
            goto L_0x00bf
        L_0x00b5:
            if (r0 == 0) goto L_0x00bf
            int r0 = r0.version
            int r2 = r1.version
            if (r0 == r2) goto L_0x00bf
            r7.pageComposition = r1
        L_0x00bf:
            int r7 = r6.getBytePosition()
            int r3 = r3 - r7
            r6.skipBytes(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.dvb.DvbParser.parseSubtitlingSegment(com.google.android.exoplayer2.util.ParsableBitArray, com.google.android.exoplayer2.text.dvb.DvbParser$SubtitleService):void");
    }

    public List<Cue> decode(byte[] bArr, int i) {
        int i2;
        SparseArray<RegionObject> sparseArray;
        ParsableBitArray parsableBitArray = new ParsableBitArray(bArr, i);
        while (parsableBitArray.bitsLeft() >= 48 && parsableBitArray.readBits(8) == 15) {
            parseSubtitlingSegment(parsableBitArray, this.subtitleService);
        }
        SubtitleService subtitleService2 = this.subtitleService;
        if (subtitleService2.pageComposition == null) {
            return Collections.emptyList();
        }
        DisplayDefinition displayDefinition = subtitleService2.displayDefinition;
        if (displayDefinition == null) {
            displayDefinition = this.defaultDisplayDefinition;
        }
        Bitmap bitmap2 = this.bitmap;
        if (!(bitmap2 != null && displayDefinition.width + 1 == bitmap2.getWidth() && displayDefinition.height + 1 == this.bitmap.getHeight())) {
            this.bitmap = Bitmap.createBitmap(displayDefinition.width + 1, displayDefinition.height + 1, Bitmap.Config.ARGB_8888);
            this.canvas.setBitmap(this.bitmap);
        }
        ArrayList arrayList = new ArrayList();
        SparseArray<PageRegion> sparseArray2 = this.subtitleService.pageComposition.regions;
        for (int i3 = 0; i3 < sparseArray2.size(); i3++) {
            PageRegion valueAt = sparseArray2.valueAt(i3);
            RegionComposition regionComposition = this.subtitleService.regions.get(sparseArray2.keyAt(i3));
            int i4 = valueAt.horizontalAddress + displayDefinition.horizontalPositionMinimum;
            int i5 = valueAt.verticalAddress + displayDefinition.verticalPositionMinimum;
            float f = (float) i4;
            float f2 = (float) i5;
            float f3 = f2;
            float f4 = f;
            this.canvas.clipRect(f, f2, (float) Math.min(regionComposition.width + i4, displayDefinition.horizontalPositionMaximum), (float) Math.min(regionComposition.height + i5, displayDefinition.verticalPositionMaximum), Region.Op.REPLACE);
            ClutDefinition clutDefinition = this.subtitleService.cluts.get(regionComposition.clutId);
            if (clutDefinition == null && (clutDefinition = this.subtitleService.ancillaryCluts.get(regionComposition.clutId)) == null) {
                clutDefinition = this.defaultClutDefinition;
            }
            SparseArray<RegionObject> sparseArray3 = regionComposition.regionObjects;
            int i6 = 0;
            while (i6 < sparseArray3.size()) {
                int keyAt = sparseArray3.keyAt(i6);
                RegionObject valueAt2 = sparseArray3.valueAt(i6);
                ObjectData objectData = this.subtitleService.objects.get(keyAt);
                ObjectData objectData2 = objectData == null ? this.subtitleService.ancillaryObjects.get(keyAt) : objectData;
                if (objectData2 != null) {
                    i2 = i6;
                    sparseArray = sparseArray3;
                    paintPixelDataSubBlocks(objectData2, clutDefinition, regionComposition.depth, valueAt2.horizontalPosition + i4, i5 + valueAt2.verticalPosition, objectData2.nonModifyingColorFlag ? null : this.defaultPaint, this.canvas);
                } else {
                    i2 = i6;
                    sparseArray = sparseArray3;
                }
                i6 = i2 + 1;
                sparseArray3 = sparseArray;
            }
            if (regionComposition.fillFlag) {
                int i7 = regionComposition.depth;
                this.fillRegionPaint.setColor(i7 == 3 ? clutDefinition.clutEntries8Bit[regionComposition.pixelCode8Bit] : i7 == 2 ? clutDefinition.clutEntries4Bit[regionComposition.pixelCode4Bit] : clutDefinition.clutEntries2Bit[regionComposition.pixelCode2Bit]);
                this.canvas.drawRect(f4, f3, (float) (regionComposition.width + i4), (float) (regionComposition.height + i5), this.fillRegionPaint);
            }
            Bitmap createBitmap = Bitmap.createBitmap(this.bitmap, i4, i5, regionComposition.width, regionComposition.height);
            int i8 = displayDefinition.width;
            int i9 = displayDefinition.height;
            arrayList.add(new Cue(createBitmap, f4 / ((float) i8), 0, f3 / ((float) i9), 0, ((float) regionComposition.width) / ((float) i8), ((float) regionComposition.height) / ((float) i9)));
            this.canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        return arrayList;
    }

    public void reset() {
        this.subtitleService.reset();
    }
}
