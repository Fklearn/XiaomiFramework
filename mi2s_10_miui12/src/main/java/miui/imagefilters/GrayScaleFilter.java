package miui.imagefilters;

import android.graphics.Color;
import com.android.server.wifi.hotspot2.anqp.Constants;
import miui.imagefilters.IImageFilter;

public class GrayScaleFilter extends IImageFilter.AbstractImageFilter {
    private int mBlackColor = -16777216;
    private int mWhiteColor = -1;

    public void setBlackColor(String strColor) {
        this.mBlackColor = Color.parseColor(strColor);
    }

    public void setWhiteColor(String strColor) {
        this.mWhiteColor = Color.parseColor(strColor);
    }

    public void processData(ImageData imgData) {
        ImageData imageData = imgData;
        int width = imageData.width;
        int height = imageData.height;
        int[] pixels = imageData.pixels;
        int i = this.mBlackColor;
        int aBlack = i >>> 24;
        int i2 = this.mWhiteColor;
        int aWhite = i2 >>> 24;
        char c = 255;
        int rBlack = (i >>> 16) & Constants.BYTE_MASK;
        int rWhite = (i2 >>> 16) & Constants.BYTE_MASK;
        int gBlack = (i >>> 8) & Constants.BYTE_MASK;
        int gWhite = (i2 >>> 8) & Constants.BYTE_MASK;
        int bBlack = i & Constants.BYTE_MASK;
        int bWhite = i2 & Constants.BYTE_MASK;
        int x = 0;
        while (x < width) {
            int y = 0;
            while (y < height) {
                int colorIndex = (y * width) + x;
                int argb = pixels[colorIndex];
                int luminance = ImageFilterUtils.convertColorToGrayscale(argb);
                pixels[colorIndex] = (((ImageFilterUtils.interpolate(0, Constants.BYTE_MASK, aBlack, aWhite, luminance) * (argb >>> 24)) / Constants.BYTE_MASK) << 24) | (ImageFilterUtils.interpolate(0, Constants.BYTE_MASK, rBlack, rWhite, luminance) << 16) | (ImageFilterUtils.interpolate(0, Constants.BYTE_MASK, gBlack, gWhite, luminance) << 8) | ImageFilterUtils.interpolate(0, Constants.BYTE_MASK, bBlack, bWhite, luminance);
                y++;
                c = 255;
                width = width;
                ImageData imageData2 = imgData;
            }
            char c2 = c;
            x++;
            ImageData imageData3 = imgData;
        }
    }
}
