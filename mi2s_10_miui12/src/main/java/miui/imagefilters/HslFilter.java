package miui.imagefilters;

import miui.imagefilters.IImageFilter;

public class HslFilter extends IImageFilter.AbstractImageFilter {
    private float mHueAdjust = Float.NaN;
    private float mHueModify = Float.NaN;
    private float mLightnessAdjust = Float.NaN;
    private float mLightnessModify = Float.NaN;
    private float mSaturationAdjust = Float.NaN;
    private float mSaturationModify = Float.NaN;
    public boolean useHsv;

    public void setHueModify(float hue) {
        this.mHueModify = ImageFilterUtils.clamp(0.0f, hue, 359.9999f);
    }

    public void setSaturationModify(float saturation) {
        this.mSaturationModify = ImageFilterUtils.clamp(0.0f, saturation / 100.0f, 1.0f);
    }

    public void setLightnessModify(float lightness) {
        this.mLightnessModify = ImageFilterUtils.clamp(0.0f, lightness / 100.0f, 1.0f);
    }

    public void setHueAdjust(float hue) {
        this.mHueAdjust = ImageFilterUtils.clamp(-180.0f, hue, 180.0f);
    }

    public void setSaturationAdjust(float saturation) {
        this.mSaturationAdjust = ImageFilterUtils.clamp(-1.0f, saturation / 100.0f, 1.0f);
    }

    public void setLightnessAdjust(float lightness) {
        this.mLightnessAdjust = ImageFilterUtils.clamp(-1.0f, lightness / 100.0f, 1.0f);
    }

    public void processData(ImageData imgData) {
        int newRgb;
        float f;
        float s;
        ImageData imageData = imgData;
        int width = imageData.width;
        int height = imageData.height;
        int[] pixels = imageData.pixels;
        float[] triple = new float[3];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int colorIndex = (y * width) + x;
                int rgb = pixels[colorIndex];
                if (this.useHsv) {
                    ImageFilterUtils.RgbToHsv(rgb, triple);
                } else {
                    ImageFilterUtils.RgbToHsl(rgb, triple);
                }
                float h = triple[0];
                float s2 = triple[1];
                float l = triple[2];
                if (!Float.isNaN(this.mHueModify)) {
                    h = this.mHueModify;
                } else if (!Float.isNaN(this.mHueAdjust)) {
                    h += this.mHueAdjust;
                    if (h >= 360.0f) {
                        h -= 360.0f;
                    } else if (h < 0.0f) {
                        h += 360.0f;
                    }
                }
                if (!Float.isNaN(this.mSaturationModify)) {
                    s2 = this.mSaturationModify;
                } else if (!Float.isNaN(this.mSaturationAdjust)) {
                    float f2 = this.mSaturationAdjust;
                    if (f2 <= 0.0f) {
                        s = s2 * (f2 + 1.0f);
                        f = 0.0f;
                    } else {
                        float multipleFactor = Math.min(1.0f, f2 * 2.0f);
                        float additionFactor = (this.mSaturationAdjust - 0.5f) * 2.0f;
                        s = s2 * (multipleFactor + 1.0f);
                        f = 0.0f;
                        if (additionFactor > 0.0f) {
                            s += additionFactor;
                        }
                    }
                    s2 = ImageFilterUtils.clamp(f, s, 1.0f);
                }
                if (!Float.isNaN(this.mLightnessModify)) {
                    l = this.mLightnessModify;
                } else if (!Float.isNaN(this.mLightnessAdjust)) {
                    float f3 = this.mLightnessAdjust;
                    if (f3 <= 0.0f) {
                        l *= f3 + 1.0f;
                    } else {
                        l = 1.0f - ((1.0f - l) * (1.0f - f3));
                    }
                }
                if (this.useHsv) {
                    newRgb = ImageFilterUtils.HsvToRgb(h, s2, l);
                } else {
                    newRgb = ImageFilterUtils.HslToRgb(h, s2, l);
                }
                pixels[colorIndex] = (16777215 & newRgb) | (pixels[colorIndex] & -16777216);
            }
        }
    }
}
