package miui.imagefilters;

import com.android.server.wifi.hotspot2.anqp.Constants;
import miui.imagefilters.IImageFilter;

public class ColorMatrixFilter extends IImageFilter.AbstractImageFilter {
    private float[] mColorMatrix;

    public void setColorMatrix(float[] colorMatrix) {
        this.mColorMatrix = colorMatrix;
    }

    public void processData(ImageData imgData) {
        ImageData imageData = imgData;
        float[] fArr = this.mColorMatrix;
        if (fArr != null && fArr.length == 20) {
            int width = imageData.width;
            int height = imageData.height;
            int[] pixels = imageData.pixels;
            float[] fArr2 = this.mColorMatrix;
            float a = fArr2[0];
            float b = fArr2[1];
            float c = fArr2[2];
            float d = fArr2[3];
            float e = fArr2[4];
            float f = fArr2[5];
            float g = fArr2[6];
            float h = fArr2[7];
            float i = fArr2[8];
            float j = fArr2[9];
            float k = fArr2[10];
            float l = fArr2[11];
            float m = fArr2[12];
            float n = fArr2[13];
            float o = fArr2[14];
            float p = fArr2[15];
            float q = fArr2[16];
            float r = fArr2[17];
            float s = fArr2[18];
            float t = fArr2[19];
            int y = 0;
            while (y <= height - 1) {
                int newA = 0;
                while (newA <= width - 1) {
                    int colorIndex = (y * width) + newA;
                    int width2 = width;
                    int color = pixels[colorIndex];
                    int height2 = height;
                    int y2 = y;
                    int R = (color >>> 16) & Constants.BYTE_MASK;
                    int x = newA;
                    int G = (color >>> 8) & Constants.BYTE_MASK;
                    int B = color & Constants.BYTE_MASK;
                    int colorIndex2 = colorIndex;
                    int i2 = color;
                    int A = (color >>> 24) & Constants.BYTE_MASK;
                    float a2 = a;
                    int i3 = G;
                    pixels[colorIndex2] = (ImageFilterUtils.clamp(0, (int) (((((((float) R) * p) + (((float) G) * q)) + (((float) B) * r)) + (((float) A) * s)) + t), (int) Constants.BYTE_MASK) << 24) | (ImageFilterUtils.clamp(0, (int) (((((((float) R) * a) + (((float) G) * b)) + (((float) B) * c)) + (((float) A) * d)) + e), (int) Constants.BYTE_MASK) << 16) | (ImageFilterUtils.clamp(0, (int) (((((((float) R) * f) + (((float) G) * g)) + (((float) B) * h)) + (((float) A) * i)) + j), (int) Constants.BYTE_MASK) << 8) | ImageFilterUtils.clamp(0, (int) ((((float) R) * k) + (((float) G) * l) + (((float) B) * m) + (((float) A) * n) + o), (int) Constants.BYTE_MASK);
                    newA = x + 1;
                    ImageData imageData2 = imgData;
                    width = width2;
                    height = height2;
                    y = y2;
                    a = a2;
                    b = b;
                    c = c;
                    d = d;
                }
                int i4 = newA;
                int i5 = width;
                int i6 = height;
                float f2 = a;
                float f3 = b;
                float f4 = c;
                y++;
                ImageData imageData3 = imgData;
                d = d;
            }
        }
    }
}
