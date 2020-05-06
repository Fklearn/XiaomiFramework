package miui.imagefilters;

import com.android.server.wifi.hotspot2.anqp.Constants;
import java.lang.reflect.Array;
import miui.imagefilters.IImageFilter;

public class EdgesFilter extends IImageFilter.AbstractImageFilter {
    public void processData(ImageData imgData) {
        ImageData imageData = imgData;
        int width = imageData.width;
        int height = imageData.height;
        int[] pixels = imageData.pixels;
        float[] hsl = new float[3];
        int[][] luminance = (int[][]) Array.newInstance(int.class, new int[]{width, height});
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                luminance[x][y] = ImageFilterUtils.convertColorToGrayscale(pixels[(y * width) + x]);
            }
        }
        for (int y2 = 1; y2 < height - 1; y2++) {
            for (int x2 = 1; x2 < width - 1; x2++) {
                int colorIndex = (y2 * width) + x2;
                int magnitude = Constants.BYTE_MASK - ImageFilterUtils.clamp(0, Math.abs((((((-luminance[x2 - 1][y2 - 1]) + luminance[x2 - 1][(y2 - 1) + 2]) - (luminance[(x2 - 1) + 1][y2 - 1] * 2)) + (luminance[(x2 - 1) + 1][(y2 - 1) + 2] * 2)) - luminance[(x2 - 1) + 2][y2 - 1]) + luminance[(x2 - 1) + 2][(y2 - 1) + 2]) + Math.abs(((((luminance[x2 - 1][y2 - 1] + (luminance[x2 - 1][(y2 - 1) + 1] * 2)) + luminance[x2 - 1][(y2 - 1) + 2]) - luminance[(x2 - 1) + 2][y2 - 1]) - (luminance[(x2 - 1) + 2][(y2 - 1) + 1] * 2)) - luminance[(x2 - 1) + 2][(y2 - 1) + 2]), (int) Constants.BYTE_MASK);
                ImageFilterUtils.RgbToHsl(pixels[colorIndex], hsl);
                hsl[2] = ((float) magnitude) / 255.0f;
                pixels[colorIndex] = (16777215 & ImageFilterUtils.HslToRgb(hsl)) | (pixels[colorIndex] & -16777216);
            }
        }
    }
}
