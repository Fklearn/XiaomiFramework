package miui.imagefilters;

import com.android.server.wifi.hotspot2.anqp.Constants;
import miui.imagefilters.IImageFilter;

public class LevelsFilter extends IImageFilter.AbstractImageFilter {
    private float mInputMax = 255.0f;
    private float mInputMiddle = 1.0f;
    private float mInputMin = 0.0f;
    private boolean mIsFilterB = true;
    private boolean mIsFilterG = true;
    private boolean mIsFilterR = true;
    private float mOutputMax = 255.0f;
    private float mOutputMin = 0.0f;

    public void setInputMin(float value) {
        this.mInputMin = ImageFilterUtils.clamp(0.0f, value, 253.0f);
    }

    public void setInputMiddle(float value) {
        this.mInputMiddle = ImageFilterUtils.clamp(1.0E-4f, value, 9.9999f);
    }

    public void setInputMax(float value) {
        this.mInputMax = ImageFilterUtils.clamp(2.0f, value, 255.0f);
    }

    public void setOutputMin(float value) {
        this.mOutputMin = ImageFilterUtils.clamp(0.0f, value, 255.0f);
    }

    public void setOutputMax(float value) {
        this.mOutputMax = ImageFilterUtils.clamp(0.0f, value, 255.0f);
    }

    public void setChannel(String channel) {
        boolean[] outRgb = new boolean[3];
        ImageFilterUtils.checkChannelParam(channel, outRgb);
        this.mIsFilterR = outRgb[0];
        this.mIsFilterG = outRgb[1];
        this.mIsFilterB = outRgb[2];
    }

    public void processData(ImageData imgData) {
        int b;
        int b2;
        ImageData imageData = imgData;
        int width = imageData.width;
        int height = imageData.height;
        int[] pixels = imageData.pixels;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int colorIndex = (y * width) + x;
                int argb = pixels[colorIndex];
                int r = (16711680 & argb) >>> 16;
                int g = (65280 & argb) >>> 8;
                int b3 = argb & Constants.BYTE_MASK;
                if (this.mIsFilterR) {
                    b = b3;
                    r = interpolate(this.mInputMin, this.mInputMiddle, this.mInputMax, this.mOutputMin, this.mOutputMax, r);
                } else {
                    b = b3;
                }
                if (this.mIsFilterG) {
                    g = interpolate(this.mInputMin, this.mInputMiddle, this.mInputMax, this.mOutputMin, this.mOutputMax, g);
                }
                if (this.mIsFilterB) {
                    b2 = interpolate(this.mInputMin, this.mInputMiddle, this.mInputMax, this.mOutputMin, this.mOutputMax, b);
                } else {
                    b2 = b;
                }
                pixels[colorIndex] = (r << 16) | (g << 8) | b2 | (-16777216 & argb);
            }
        }
    }

    private static int interpolate(float start, float gamma, float end, float outStart, float outEnd, int inputValue) {
        if (((float) inputValue) <= start) {
            return (int) outStart;
        }
        if (((float) inputValue) >= end) {
            return (int) outEnd;
        }
        if (gamma == 1.0f) {
            return (int) ((((((float) inputValue) - start) * (outEnd - outStart)) / (end - start)) + outStart);
        }
        return (int) (((outEnd - outStart) * (1.0f - ((float) Math.pow((double) (1.0f - ((((float) inputValue) - start) / (end - start))), (double) gamma)))) + outStart);
    }
}
