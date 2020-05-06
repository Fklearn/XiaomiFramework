package miui.imagefilters;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.wifi.hotspot2.anqp.Constants;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Map;
import miui.imagefilters.IImageFilter;

public class BlendImageFilter extends IImageFilter.AbstractImageFilter {
    static final int BLEND_TYPE_COLOR = 21;
    static final int BLEND_TYPE_COLOR_BURN = 10;
    static final int BLEND_TYPE_COLOR_DODGE = 9;
    static final int BLEND_TYPE_DARKEN = 3;
    static final int BLEND_TYPE_DIFFERENCE = 5;
    static final int BLEND_TYPE_DIVIDE = 23;
    static final int BLEND_TYPE_EXCLUSION = 18;
    static final int BLEND_TYPE_HARD_LIGHT = 12;
    static final int BLEND_TYPE_HARD_MIX = 17;
    static final int BLEND_TYPE_HUE = 19;
    static final int BLEND_TYPE_LIGHTEN = 4;
    static final int BLEND_TYPE_LINEAR_BURN = 7;
    static final int BLEND_TYPE_LINEAR_DODGE = 6;
    static final int BLEND_TYPE_LINEAR_LIGHT = 15;
    static final int BLEND_TYPE_LUMINOSITY = 22;
    static final int BLEND_TYPE_MULTIPLY = 1;
    static final int BLEND_TYPE_NORMAL = 0;
    static final int BLEND_TYPE_OPACITY = 11;
    static final int BLEND_TYPE_OVERLAY = 8;
    static final int BLEND_TYPE_PIN_LIGHT = 16;
    static final int BLEND_TYPE_SATURATION = 20;
    static final int BLEND_TYPE_SCREEN = 2;
    static final int BLEND_TYPE_SOFT_LIGHT = 13;
    static final int BLEND_TYPE_SUBTRACT = 24;
    static final int BLEND_TYPE_VIVID_LIGHT = 14;
    static final String TAG = "BlendImageFilter";
    private int mBlendType = 0;
    private IImageFilter.ImageFilterGroup mInputFilters;
    private ImageData mInputImage;
    private SoftReference<Map.Entry<Integer, ImageData>> mInputImageCache;
    private boolean mIsInputImageOnTop = true;
    private PorterDuff.Mode mPorterDuffMode = PorterDuff.Mode.SRC_ATOP;
    private boolean mUseOriginalImage;

    abstract class Blender {
        public abstract int blendColor(int i, int i2);

        Blender() {
        }
    }

    abstract class BlenderPerChannel extends Blender {
        public abstract float blendChannel(float f, float f2);

        BlenderPerChannel() {
            super();
        }

        public int blendColor(int dstArgb, int srcArgb) {
            int srcR = (srcArgb >>> 16) & Constants.BYTE_MASK;
            int srcG = (srcArgb >>> 8) & Constants.BYTE_MASK;
            int srcB = srcArgb & Constants.BYTE_MASK;
            int dstR = (dstArgb >>> 16) & Constants.BYTE_MASK;
            int dstG = (dstArgb >>> 8) & Constants.BYTE_MASK;
            return (-16777216 & srcArgb) | (((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((float) dstR) / 255.0f, ((float) srcR) / 255.0f), 1.0f) * 255.0f)) << 16) | (((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((float) dstG) / 255.0f, ((float) srcG) / 255.0f), 1.0f) * 255.0f)) << 8) | ((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((float) (dstArgb & Constants.BYTE_MASK)) / 255.0f, ((float) srcB) / 255.0f), 1.0f) * 255.0f));
        }
    }

    abstract class PorterDuffBlender {
        public abstract int blendFinal(int i, int i2);

        PorterDuffBlender() {
        }
    }

    abstract class PorterDuffBlenderPerChannel extends PorterDuffBlender {
        public abstract float blendAlpha(float f, float f2);

        public abstract float blendChannel(float f, float f2, float f3, float f4);

        PorterDuffBlenderPerChannel() {
            super();
        }

        public int blendFinal(int dstArgb, int srcArgb) {
            int i = dstArgb;
            int i2 = srcArgb;
            int srcA = (i2 >>> 24) & Constants.BYTE_MASK;
            int srcR = ((i2 >>> 16) & Constants.BYTE_MASK) * srcA;
            int srcG = ((i2 >>> 8) & Constants.BYTE_MASK) * srcA;
            int srcB = (i2 & Constants.BYTE_MASK) * srcA;
            int dstA = (i >>> 24) & Constants.BYTE_MASK;
            int dstR = ((i >>> 16) & Constants.BYTE_MASK) * dstA;
            int dstG = ((i >>> 8) & Constants.BYTE_MASK) * dstA;
            int dstB = (i & Constants.BYTE_MASK) * dstA;
            float Da = ((float) dstA) / 255.0f;
            float Sa = ((float) srcA) / 255.0f;
            int resultA = (int) (ImageFilterUtils.clamp(0.0f, blendAlpha(Da, Sa), 1.0f) * 255.0f);
            return (resultA << 24) | ((resultA == 0 ? 0 : (int) ImageFilterUtils.clamp(0.0f, blendChannel((float) dstR, (float) srcR, Da, Sa) / ((float) resultA), 255.0f)) << 16) | ((resultA == 0 ? 0 : (int) ImageFilterUtils.clamp(0.0f, blendChannel((float) dstG, (float) srcG, Da, Sa) / ((float) resultA), 255.0f)) << 8) | (resultA == 0 ? 0 : (int) ImageFilterUtils.clamp(0.0f, blendChannel((float) dstB, (float) srcB, Da, Sa) / ((float) resultA), 255.0f));
        }
    }

    public void setBlendType(int blendType) {
        this.mBlendType = blendType;
    }

    public void setIsInputImageOnTop(boolean value) {
        this.mIsInputImageOnTop = value;
    }

    public void setPorterDuffMode(PorterDuff.Mode value) {
        this.mPorterDuffMode = value;
    }

    public void setBlendTypeName(String typeName) {
        if (TextUtils.isEmpty(typeName) || typeName.equalsIgnoreCase("Normal")) {
            this.mBlendType = 0;
        } else if (typeName.equalsIgnoreCase("Multiply")) {
            this.mBlendType = 1;
        } else if (typeName.equalsIgnoreCase("Screen")) {
            this.mBlendType = 2;
        } else if (typeName.equalsIgnoreCase("Darken")) {
            this.mBlendType = 3;
        } else if (typeName.equalsIgnoreCase("Lighten")) {
            this.mBlendType = 4;
        } else if (typeName.equalsIgnoreCase("Difference")) {
            this.mBlendType = 5;
        } else if (typeName.equalsIgnoreCase("LinearDodge")) {
            this.mBlendType = 6;
        } else if (typeName.equalsIgnoreCase("LinearBurn")) {
            this.mBlendType = 7;
        } else if (typeName.equalsIgnoreCase("Overlay")) {
            this.mBlendType = 8;
        } else if (typeName.equalsIgnoreCase("ColorDodge")) {
            this.mBlendType = 9;
        } else if (typeName.equalsIgnoreCase("ColorBurn")) {
            this.mBlendType = 10;
        } else if (typeName.equalsIgnoreCase("Opacity")) {
            this.mBlendType = 11;
        } else if (typeName.equalsIgnoreCase("HardLight")) {
            this.mBlendType = 12;
        } else if (typeName.equalsIgnoreCase("SoftLight")) {
            this.mBlendType = 13;
        } else if (typeName.equalsIgnoreCase("VividLight")) {
            this.mBlendType = 14;
        } else if (typeName.equalsIgnoreCase("LinearLight")) {
            this.mBlendType = 15;
        } else if (typeName.equalsIgnoreCase("PinLight")) {
            this.mBlendType = 16;
        } else if (typeName.equalsIgnoreCase("HardMix")) {
            this.mBlendType = 17;
        } else if (typeName.equalsIgnoreCase("Exclusion")) {
            this.mBlendType = 18;
        } else if (typeName.equalsIgnoreCase("Hue")) {
            this.mBlendType = 19;
        } else if (typeName.equalsIgnoreCase("Saturation")) {
            this.mBlendType = 20;
        } else if (typeName.equalsIgnoreCase("Color")) {
            this.mBlendType = 21;
        } else if (typeName.equalsIgnoreCase("Luminosity")) {
            this.mBlendType = 22;
        } else if (typeName.equalsIgnoreCase("Divide")) {
            this.mBlendType = 23;
        } else if (typeName.equalsIgnoreCase("Subtract")) {
            this.mBlendType = 24;
        } else {
            Log.d(TAG, "unknown blend type name: " + typeName);
        }
    }

    public void setInputImage(Bitmap bitmap) {
        this.mInputImage = ImageData.bitmapToImageData(bitmap);
        this.mInputImageCache = null;
    }

    public void setInputFilters(IImageFilter.ImageFilterGroup filters) {
        this.mUseOriginalImage = true;
        this.mInputFilters = filters;
    }

    public void setUseOriginalImage(boolean value) {
        this.mUseOriginalImage = value;
    }

    public void putOriginalImage(Bitmap originalBitmap) {
        if (this.mUseOriginalImage) {
            IImageFilter.ImageFilterGroup imageFilterGroup = this.mInputFilters;
            if (imageFilterGroup != null) {
                this.mInputImage = imageFilterGroup.processAll(originalBitmap);
            } else {
                this.mInputImage = ImageData.bitmapToImageData(originalBitmap);
            }
            this.mInputImageCache = null;
        }
    }

    public boolean canConcurrence() {
        if (this.mUseOriginalImage) {
            return false;
        }
        return super.canConcurrence();
    }

    public void processData(ImageData imgData) {
        Blender blendable;
        PorterDuffBlender porterDuffBlender;
        if (this.mInputImage != null && (blendable = getCurrentBlender()) != null && (porterDuffBlender = getCurrentPorterDuffBlender()) != null) {
            int width = imgData.width;
            int height = imgData.height;
            int[] pixels = imgData.pixels;
            int[] dstPixels = pixels;
            int[] srcPixels = obtainInputImageBySize(width, height).pixels;
            if (!this.mIsInputImageOnTop) {
                dstPixels = srcPixels;
                srcPixels = pixels;
            }
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int colorIndex = (y * width) + x;
                    int dstArgb = dstPixels[colorIndex];
                    pixels[colorIndex] = porterDuffBlender.blendFinal(dstArgb, blendable.blendColor(dstArgb, srcPixels[colorIndex]));
                }
            }
        }
    }

    private ImageData obtainInputImageBySize(int width, int height) {
        int widthHeight = mergeWidthHeight(width, height);
        SoftReference<Map.Entry<Integer, ImageData>> softReference = this.mInputImageCache;
        Map.Entry<Integer, ImageData> cache = softReference == null ? null : softReference.get();
        if (cache != null && cache.getKey().intValue() == widthHeight) {
            return cache.getValue();
        }
        if (mergeWidthHeight(this.mInputImage.width, this.mInputImage.height) == widthHeight) {
            return this.mInputImage;
        }
        ImageData newImageData = ImageData.bitmapToImageData(Bitmap.createScaledBitmap(ImageData.imageDataToBitmap(this.mInputImage), width, height, true));
        this.mInputImageCache = new SoftReference<>(new AbstractMap.SimpleImmutableEntry(Integer.valueOf(widthHeight), newImageData));
        return newImageData;
    }

    private int mergeWidthHeight(int width, int height) {
        if (width <= 32767 && height <= 32767) {
            return (width << 16) | height;
        }
        throw new RuntimeException("image's width or height to large:" + width + "x" + height);
    }

    private Blender getCurrentBlender() {
        switch (this.mBlendType) {
            case 0:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return b;
                    }
                };
            case 1:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return a * b;
                    }
                };
            case 2:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return 1.0f - ((1.0f - a) * (1.0f - b));
                    }
                };
            case 3:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return Math.min(a, b);
                    }
                };
            case 4:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return Math.max(a, b);
                    }
                };
            case 5:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return Math.abs(a - b);
                    }
                };
            case 6:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return b + a;
                    }
                };
            case 7:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return (b + a) - 1.0f;
                    }
                };
            case 8:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        if (a < 0.5f) {
                            return 2.0f * a * b;
                        }
                        return 1.0f - (((1.0f - a) * 2.0f) * (1.0f - b));
                    }
                };
            case 9:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return a / (1.0f - b);
                    }
                };
            case 10:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return 1.0f - ((1.0f - a) / b);
                    }
                };
            case 11:
                return new Blender() {
                    public int blendColor(int dstArgb, int srcArgb) {
                        int i = dstArgb;
                        int i2 = srcArgb;
                        int srcA = (i2 >>> 24) & Constants.BYTE_MASK;
                        int srcR = (i2 >>> 16) & Constants.BYTE_MASK;
                        int srcG = (i2 >>> 8) & Constants.BYTE_MASK;
                        int srcB = i2 & Constants.BYTE_MASK;
                        int dstR = (i >>> 16) & Constants.BYTE_MASK;
                        int dstG = (i >>> 8) & Constants.BYTE_MASK;
                        return (-16777216 & i2) | (((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((float) dstR) / 255.0f, ((float) srcR) / 255.0f, ((float) srcA) / 255.0f), 1.0f) * 255.0f)) << 16) | (((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((float) dstG) / 255.0f, ((float) srcG) / 255.0f, ((float) srcA) / 255.0f), 1.0f) * 255.0f)) << 8) | ((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((float) (i & Constants.BYTE_MASK)) / 255.0f, ((float) srcB) / 255.0f, ((float) srcA) / 255.0f), 1.0f) * 255.0f));
                    }

                    private float blendChannel(float a, float b, float alpha) {
                        return (alpha * b) + ((1.0f - alpha) * a);
                    }
                };
            case 12:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        if (b < 0.5f) {
                            return 2.0f * a * b;
                        }
                        return 1.0f - (((1.0f - a) * 2.0f) * (1.0f - b));
                    }
                };
            case 13:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        if (b < 0.5f) {
                            return (a * 2.0f * b) + (a * a * (1.0f - (2.0f * b)));
                        }
                        return (a * 2.0f * (1.0f - b)) + (((float) Math.sqrt((double) a)) * ((2.0f * b) - 1.0f));
                    }
                };
            case 14:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        if (b <= 0.5f) {
                            return 1.0f - ((1.0f - a) / (2.0f * b));
                        }
                        return a / ((1.0f - b) * 2.0f);
                    }
                };
            case 15:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return ((2.0f * b) + a) - 1.0f;
                    }
                };
            case 16:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        if (a < (b * 2.0f) - 1.0f) {
                            return (2.0f * b) - 1.0f;
                        }
                        if (a < b * 2.0f) {
                            return a;
                        }
                        return 2.0f * b;
                    }
                };
            case 17:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        if (b < 1.0f - a) {
                            return 0.0f;
                        }
                        return 1.0f;
                    }
                };
            case 18:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return (b + a) - ((2.0f * b) * a);
                    }
                };
            case 19:
                return new Blender() {
                    public int blendColor(int dstArgb, int srcArgb) {
                        float[] srcHsl = new float[3];
                        float[] dstHsl = new float[3];
                        ImageFilterUtils.RgbToHsl(srcArgb, srcHsl);
                        ImageFilterUtils.RgbToHsl(dstArgb, dstHsl);
                        return (-16777216 & srcArgb) | (16777215 & ImageFilterUtils.HslToRgb(srcHsl[0], dstHsl[1], dstHsl[2]));
                    }
                };
            case 20:
                return new Blender() {
                    public int blendColor(int dstArgb, int srcArgb) {
                        float[] srcHsl = new float[3];
                        float[] dstHsl = new float[3];
                        ImageFilterUtils.RgbToHsl(srcArgb, srcHsl);
                        ImageFilterUtils.RgbToHsl(dstArgb, dstHsl);
                        return (-16777216 & srcArgb) | (16777215 & ImageFilterUtils.HslToRgb(dstHsl[0], srcHsl[1], dstHsl[2]));
                    }
                };
            case 21:
                return new Blender() {
                    public int blendColor(int dstArgb, int srcArgb) {
                        float[] srcHsl = new float[3];
                        float[] dstHsl = new float[3];
                        ImageFilterUtils.RgbToHsl(srcArgb, srcHsl);
                        ImageFilterUtils.RgbToHsl(dstArgb, dstHsl);
                        return (-16777216 & srcArgb) | (16777215 & ImageFilterUtils.HslToRgb(srcHsl[0], srcHsl[1], dstHsl[2]));
                    }
                };
            case 22:
                return new Blender() {
                    public int blendColor(int dstArgb, int srcArgb) {
                        float[] srcHsl = new float[3];
                        float[] dstHsl = new float[3];
                        ImageFilterUtils.RgbToHsl(srcArgb, srcHsl);
                        ImageFilterUtils.RgbToHsl(dstArgb, dstHsl);
                        return (-16777216 & srcArgb) | (16777215 & ImageFilterUtils.HslToRgb(dstHsl[0], dstHsl[1], srcHsl[2]));
                    }
                };
            case 23:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return a / b;
                    }
                };
            case 24:
                return new BlenderPerChannel() {
                    public float blendChannel(float a, float b) {
                        return a - b;
                    }
                };
            default:
                Log.w(TAG, "unknown blender type:" + this.mBlendType);
                return null;
        }
    }

    /* renamed from: miui.imagefilters.BlendImageFilter$41  reason: invalid class name */
    static /* synthetic */ class AnonymousClass41 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$PorterDuff$Mode = new int[PorterDuff.Mode.values().length];

        static {
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.CLEAR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST_ATOP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST_IN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST_OUT.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST_OVER.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC_ATOP.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC_IN.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC_OUT.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC_OVER.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.XOR.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SCREEN.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.LIGHTEN.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DARKEN.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
        }
    }

    private PorterDuffBlender getCurrentPorterDuffBlender() {
        switch (AnonymousClass41.$SwitchMap$android$graphics$PorterDuff$Mode[this.mPorterDuffMode.ordinal()]) {
            case 1:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return 0.0f;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return 0.0f;
                    }
                };
            case 2:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return Da;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return Dc;
                    }
                };
            case 3:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return Sa;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return (Sa * Dc) + ((1.0f - Da) * Sc);
                    }
                };
            case 4:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return Sa * Da;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return Sa * Dc;
                    }
                };
            case 5:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return (1.0f - Sa) * Da;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return (1.0f - Sa) * Dc;
                    }
                };
            case 6:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return ((1.0f - Sa) * Da) + Sa;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return ((1.0f - Da) * Sc) + Dc;
                    }
                };
            case 7:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return Sa;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return Sc;
                    }
                };
            case 8:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return Da;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return (Sc * Da) + ((1.0f - Sa) * Dc);
                    }
                };
            case 9:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return Sa * Da;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return Sc * Da;
                    }
                };
            case 10:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return (1.0f - Da) * Sa;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return (1.0f - Da) * Sc;
                    }
                };
            case 11:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return ((1.0f - Sa) * Da) + Sa;
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return ((1.0f - Sa) * Dc) + Sc;
                    }
                };
            case 12:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return (Sa + Da) - ((2.0f * Sa) * Da);
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return ((1.0f - Da) * Sc) + ((1.0f - Sa) * Dc);
                    }
                };
            case 13:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return (Sa + Da) - (Sa * Da);
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return (Sc + Dc) - ((Sc * Dc) / 255.0f);
                    }
                };
            case 14:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return (Sa + Da) - (Sa * Da);
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return ((1.0f - Da) * Sc) + ((1.0f - Sa) * Dc) + Math.max(Sc, Dc);
                    }
                };
            case 15:
                return new PorterDuffBlenderPerChannel() {
                    public float blendAlpha(float Da, float Sa) {
                        return (Sa + Da) - (Sa * Da);
                    }

                    public float blendChannel(float Dc, float Sc, float Da, float Sa) {
                        return ((1.0f - Da) * Sc) + ((1.0f - Sa) * Dc) + Math.min(Sc, Dc);
                    }
                };
            default:
                Log.w(TAG, "unsupport porter duff mode:" + this.mPorterDuffMode);
                return null;
        }
    }
}
