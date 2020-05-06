package miui.imagefilters;

import com.android.server.wifi.hotspot2.anqp.Constants;
import miui.imagefilters.IImageFilter;

public class SpreadFilter extends IImageFilter.AbstractImageFilter {
    private boolean mIsAlphaSpread = false;
    private boolean mIsSpreadBlack = true;
    private int mRadius;

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    public void setIsSpreadBlack(boolean value) {
        this.mIsSpreadBlack = value;
    }

    public void setIsAlphaSpread(boolean value) {
        this.mIsAlphaSpread = value;
    }

    public void processData(ImageData imgData) {
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
        int i = this.mRadius;
        int radiusSquare = i * i;
        int resultA = this.mIsAlphaSpread ? 255 : 0;
        int resultRgb = 0;
        int resultLuminance = -1;
        int yCursor = -this.mRadius;
        while (true) {
            int i2 = this.mRadius;
            if (yCursor <= i2) {
                for (int xCursor = -i2; xCursor <= this.mRadius; xCursor++) {
                    if ((yCursor * yCursor) + (xCursor * xCursor) > radiusSquare) {
                        int[] iArr = pixels;
                        int i3 = width;
                        int i4 = height;
                    } else {
                        int color = getColor(pixels, x + xCursor, y + yCursor, width, height);
                        int alpha = (color >>> 24) & Constants.BYTE_MASK;
                        int lum = getLuminance(color, alpha);
                        resultLuminance = Math.max(lum, resultLuminance);
                        if (resultLuminance == lum) {
                            resultRgb = color;
                        }
                        resultA = this.mIsAlphaSpread ? Math.min(resultA, alpha) : Math.max(resultA, alpha);
                    }
                }
                int[] iArr2 = pixels;
                int i5 = width;
                int i6 = height;
                yCursor++;
            } else {
                int[] iArr3 = pixels;
                int i7 = width;
                int i8 = height;
                return (resultA << 24) | (16777215 & resultRgb);
            }
        }
    }

    private int getLuminance(int color, int alpha) {
        int lum = ImageFilterUtils.convertColorToGrayscale(color);
        if (this.mIsSpreadBlack) {
            lum = 255 - lum;
        }
        return lum * alpha;
    }

    private static final int getColor(int[] pixels, int x, int y, int width, int height) {
        return pixels[(ImageFilterUtils.clamp(0, y, height - 1) * width) + ImageFilterUtils.clamp(0, x, width - 1)];
    }
}
