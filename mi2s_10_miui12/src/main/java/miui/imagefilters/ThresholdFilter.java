package miui.imagefilters;

import com.android.server.wifi.hotspot2.anqp.Constants;
import miui.imagefilters.IImageFilter;

public class ThresholdFilter extends IImageFilter.AbstractImageFilter {
    private int mThresholdLevel = 128;
    private boolean mUniform;

    public void setThresholdLevel(int value) {
        this.mThresholdLevel = value;
    }

    public void setUniform(boolean value) {
        this.mUniform = value;
    }

    public void processData(ImageData imgData) {
        int width = imgData.width;
        int height = imgData.height;
        int[] pixels = imgData.pixels;
        int thresholdLevel = 0;
        if (!this.mUniform) {
            thresholdLevel = this.mThresholdLevel;
        } else {
            int visiblePixelCount = pixels.length;
            int[] perGrayscaleCount = new int[256];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = pixels[(y * width) + x];
                    if (((argb >>> 24) & Constants.BYTE_MASK) < 10) {
                        visiblePixelCount--;
                    } else {
                        int grayscale = ImageFilterUtils.convertColorToGrayscale(argb);
                        perGrayscaleCount[grayscale] = perGrayscaleCount[grayscale] + 1;
                    }
                }
            }
            int highThresholdCount = (this.mThresholdLevel * visiblePixelCount) / Constants.BYTE_MASK;
            int grayscaleStep = 0;
            int i = 0;
            while (true) {
                if (i >= 256) {
                    break;
                }
                grayscaleStep += perGrayscaleCount[i];
                if (grayscaleStep >= highThresholdCount) {
                    thresholdLevel = i;
                    break;
                }
                i++;
            }
        }
        for (int y2 = 0; y2 < height; y2++) {
            for (int x2 = 0; x2 < width; x2++) {
                int colorIndex = (y2 * width) + x2;
                int argb2 = pixels[colorIndex];
                if (ImageFilterUtils.convertColorToGrayscale(argb2) >= thresholdLevel) {
                    pixels[colorIndex] = (-16777216 & argb2) | 16777215;
                } else {
                    pixels[colorIndex] = -16777216 & argb2;
                }
            }
        }
    }
}
