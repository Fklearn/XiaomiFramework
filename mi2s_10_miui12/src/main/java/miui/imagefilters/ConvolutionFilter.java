package miui.imagefilters;

import com.android.server.wifi.hotspot2.anqp.Constants;
import miui.imagefilters.IImageFilter;

public class ConvolutionFilter extends IImageFilter.AbstractImageFilter {
    private int mBias;
    private int mDivisor;
    private boolean mIsFilterA = true;
    private boolean mIsFilterB = true;
    private boolean mIsFilterG = true;
    private boolean mIsFilterR = true;
    private boolean mIsParamsFormated;
    private int[] mMatrix;
    private int mMatrixSideLength;
    private int mRepeatCount;

    public void setMatrix(int[] matrix) {
        this.mMatrix = matrix;
        this.mIsParamsFormated = false;
    }

    public void setRepeatCount(int count) {
        this.mRepeatCount = count;
    }

    public void setDivisor(int divisor) {
        this.mDivisor = divisor;
        this.mIsParamsFormated = false;
    }

    public void setBias(int bias) {
        this.mBias = bias;
    }

    public void setChannel(String channel) {
        boolean[] outRgba = new boolean[4];
        ImageFilterUtils.checkChannelParam(channel, outRgba);
        this.mIsFilterR = outRgba[0];
        this.mIsFilterG = outRgba[1];
        this.mIsFilterB = outRgba[2];
        this.mIsFilterA = outRgba[3];
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x006d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void formatParams() {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.mIsParamsFormated     // Catch:{ all -> 0x006e }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r4)
            return
        L_0x0007:
            r0 = 1
            r4.mIsParamsFormated = r0     // Catch:{ all -> 0x006e }
            int[] r1 = r4.mMatrix     // Catch:{ all -> 0x006e }
            if (r1 == 0) goto L_0x006c
            int[] r1 = r4.mMatrix     // Catch:{ all -> 0x006e }
            int r1 = r1.length     // Catch:{ all -> 0x006e }
            r2 = 9
            if (r1 != r2) goto L_0x0019
            r1 = 3
            r4.mMatrixSideLength = r1     // Catch:{ all -> 0x006e }
            goto L_0x003f
        L_0x0019:
            int[] r1 = r4.mMatrix     // Catch:{ all -> 0x006e }
            int r1 = r1.length     // Catch:{ all -> 0x006e }
            r2 = 25
            if (r1 != r2) goto L_0x0024
            r1 = 5
            r4.mMatrixSideLength = r1     // Catch:{ all -> 0x006e }
            goto L_0x003f
        L_0x0024:
            int[] r1 = r4.mMatrix     // Catch:{ all -> 0x006e }
            int r1 = r1.length     // Catch:{ all -> 0x006e }
            double r1 = (double) r1     // Catch:{ all -> 0x006e }
            double r1 = java.lang.Math.sqrt(r1)     // Catch:{ all -> 0x006e }
            int r1 = (int) r1     // Catch:{ all -> 0x006e }
            r4.mMatrixSideLength = r1     // Catch:{ all -> 0x006e }
            int r1 = r4.mMatrixSideLength     // Catch:{ all -> 0x006e }
            int r2 = r4.mMatrixSideLength     // Catch:{ all -> 0x006e }
            int r1 = r1 * r2
            int[] r2 = r4.mMatrix     // Catch:{ all -> 0x006e }
            int r2 = r2.length     // Catch:{ all -> 0x006e }
            if (r1 != r2) goto L_0x0064
            int r1 = r4.mMatrixSideLength     // Catch:{ all -> 0x006e }
            int r1 = r1 % 2
            if (r1 != r0) goto L_0x005c
        L_0x003f:
            int r1 = r4.mDivisor     // Catch:{ all -> 0x006e }
            if (r1 != 0) goto L_0x006c
            r1 = 0
        L_0x0044:
            int[] r2 = r4.mMatrix     // Catch:{ all -> 0x006e }
            int r2 = r2.length     // Catch:{ all -> 0x006e }
            if (r1 >= r2) goto L_0x0055
            int r2 = r4.mDivisor     // Catch:{ all -> 0x006e }
            int[] r3 = r4.mMatrix     // Catch:{ all -> 0x006e }
            r3 = r3[r1]     // Catch:{ all -> 0x006e }
            int r2 = r2 + r3
            r4.mDivisor = r2     // Catch:{ all -> 0x006e }
            int r1 = r1 + 1
            goto L_0x0044
        L_0x0055:
            int r1 = r4.mDivisor     // Catch:{ all -> 0x006e }
            if (r1 != 0) goto L_0x006c
            r4.mDivisor = r0     // Catch:{ all -> 0x006e }
            goto L_0x006c
        L_0x005c:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ all -> 0x006e }
            java.lang.String r1 = "matrixX and matrixY must be odd."
            r0.<init>(r1)     // Catch:{ all -> 0x006e }
            throw r0     // Catch:{ all -> 0x006e }
        L_0x0064:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ all -> 0x006e }
            java.lang.String r1 = "matrix must be a square matrix."
            r0.<init>(r1)     // Catch:{ all -> 0x006e }
            throw r0     // Catch:{ all -> 0x006e }
        L_0x006c:
            monitor-exit(r4)
            return
        L_0x006e:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.imagefilters.ConvolutionFilter.formatParams():void");
    }

    public void processData(ImageData imgData) {
        if (!this.mIsParamsFormated) {
            formatParams();
        }
        if (this.mMatrix != null) {
            int repeatCount = this.mRepeatCount;
            if (repeatCount <= 1) {
                repeatCount = 1;
            }
            for (int i = 0; i < repeatCount; i++) {
                processOnce(imgData);
            }
        }
    }

    private void processOnce(ImageData imgData) {
        int width = imgData.width;
        int height = imgData.height;
        int[] pixels = imgData.pixels;
        int[] backPixels = imgData.getBackPixels();
        for (int y = 0; y <= height - 1; y++) {
            for (int x = 0; x <= width - 1; x++) {
                backPixels[(y * width) + x] = processPerPixel(pixels, x, y, width, height);
            }
        }
        imgData.swapPixels();
    }

    private int processPerPixel(int[] pixels, int x, int y, int width, int height) {
        int newB;
        int newA;
        int totalR = 0;
        int totalG = 0;
        int totalB = 0;
        int totalA = 0;
        int radius = (this.mMatrixSideLength - 1) / 2;
        int i = 0;
        for (int yCursor = -radius; yCursor <= radius; yCursor++) {
            for (int xCursor = -radius; xCursor <= radius; xCursor++) {
                int matrixEle = this.mMatrix[i];
                int color = getColor(pixels, x + xCursor, y + yCursor, width, height);
                if (this.mIsFilterR) {
                    totalR += ((color >>> 16) & Constants.BYTE_MASK) * matrixEle;
                }
                if (this.mIsFilterG) {
                    totalG += ((color >>> 8) & Constants.BYTE_MASK) * matrixEle;
                }
                if (this.mIsFilterB) {
                    totalB += (color & Constants.BYTE_MASK) * matrixEle;
                }
                if (this.mIsFilterA) {
                    totalA += ((color >>> 24) & Constants.BYTE_MASK) * matrixEle;
                }
                i++;
            }
            int[] iArr = pixels;
            int i2 = width;
            int i3 = height;
        }
        int[] iArr2 = pixels;
        int i4 = width;
        int i5 = height;
        int color2 = getColor(pixels, x, y, width, height);
        int newR = this.mIsFilterR ? ImageFilterUtils.clamp(0, (totalR / this.mDivisor) + this.mBias, (int) Constants.BYTE_MASK) : (color2 >>> 16) & Constants.BYTE_MASK;
        int newG = this.mIsFilterG ? ImageFilterUtils.clamp(0, (totalG / this.mDivisor) + this.mBias, (int) Constants.BYTE_MASK) : (color2 >>> 8) & Constants.BYTE_MASK;
        if (this.mIsFilterB) {
            int i6 = totalR;
            newB = ImageFilterUtils.clamp(0, (totalB / this.mDivisor) + this.mBias, (int) Constants.BYTE_MASK);
        } else {
            newB = color2 & Constants.BYTE_MASK;
        }
        if (this.mIsFilterA) {
            int i7 = totalG;
            newA = ImageFilterUtils.clamp(0, (totalA / this.mDivisor) + this.mBias, (int) Constants.BYTE_MASK);
        } else {
            newA = (color2 >>> 24) & Constants.BYTE_MASK;
        }
        return (newA << 24) | (newR << 16) | (newG << 8) | newB;
    }

    private static final int getColor(int[] pixels, int x, int y, int width, int height) {
        return pixels[(ImageFilterUtils.clamp(0, y, height - 1) * width) + ImageFilterUtils.clamp(0, x, width - 1)];
    }
}
