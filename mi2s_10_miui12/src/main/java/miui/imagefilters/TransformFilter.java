package miui.imagefilters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.android.server.wifi.hotspot2.anqp.Constants;
import miui.imagefilters.FilterParamType;
import miui.imagefilters.IImageFilter;

public class TransformFilter extends IImageFilter.AbstractImageFilter {
    private boolean mBasedOnContent = false;
    private boolean mKeepAspectRatio = true;
    private int mMinVisibleAlpha = 10;
    private Paint mPaint = new Paint(3);
    private float[] mPointsMapping;

    @FilterParamType(FilterParamType.ParamType.ICON_SIZE)
    public void setPointsMapping(float[] value) {
        if (value == null || value.length == 8) {
            this.mPointsMapping = value;
        }
    }

    public void setBasedOnContent(boolean value) {
        this.mBasedOnContent = value;
    }

    public void setContentKeepAspectRatio(boolean value) {
        this.mKeepAspectRatio = value;
    }

    public void setMinVisibleAlpha(int value) {
        this.mMinVisibleAlpha = ImageFilterUtils.clamp(0, value, (int) Constants.BYTE_MASK);
    }

    public void processData(ImageData imgData) {
        float[] pointsMapping;
        int i;
        ImageData imageData = imgData;
        int width = imageData.width;
        int height = imageData.height;
        int[] pixels = imageData.pixels;
        float left = 0.0f;
        float top = 0.0f;
        float right = (float) width;
        float bottom = (float) height;
        float[] pointsMapping2 = this.mPointsMapping;
        if (pointsMapping2 == null) {
            pointsMapping = new float[]{0.0f, 0.0f, right, 0.0f, right, bottom, 0.0f, bottom};
        } else {
            pointsMapping = pointsMapping2;
        }
        if (this.mBasedOnContent) {
            int i2 = width;
            int i3 = height;
            int[] iArr = pixels;
            i = 8;
            left = (float) scanEdge(i2, i3, iArr, true, true);
            top = (float) scanEdge(i2, i3, iArr, false, true);
            right = (float) scanEdge(i2, i3, iArr, true, false);
            bottom = (float) scanEdge(i2, i3, iArr, false, false);
            if (left != -1.0f && left != right && top != bottom) {
                if (this.mKeepAspectRatio) {
                    float scanedWidth = right - left;
                    float scanedHeight = bottom - top;
                    if (scanedWidth / ((float) width) > scanedHeight / ((float) height)) {
                        float newHeight = (scanedWidth / ((float) width)) * ((float) height);
                        top -= (newHeight - scanedHeight) / 2.0f;
                        bottom += (newHeight - scanedHeight) / 2.0f;
                    } else {
                        float newWidth = (scanedHeight / ((float) height)) * ((float) width);
                        left -= (newWidth - scanedWidth) / 2.0f;
                        right += (newWidth - scanedWidth) / 2.0f;
                    }
                }
            } else {
                return;
            }
        } else {
            i = 8;
        }
        float[] fArr = new float[i];
        fArr[0] = left;
        fArr[1] = top;
        fArr[2] = right;
        fArr[3] = top;
        fArr[4] = right;
        fArr[5] = bottom;
        fArr[6] = left;
        fArr[7] = bottom;
        float[] mappingSrc = fArr;
        Matrix matrix = new Matrix();
        if (matrix.setPolyToPoly(mappingSrc, 0, pointsMapping, 0, 4)) {
            Bitmap currentBitmap = ImageData.imageDataToBitmap(imgData);
            Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            new Canvas(newBitmap).drawBitmap(currentBitmap, matrix, this.mPaint);
            imageData.pixels = ImageData.bitmapToImageData(newBitmap).pixels;
        }
    }

    private int scanEdge(int width, int height, int[] pixels, boolean scanX, boolean scanLeftTop) {
        int d1Count = scanX ? width : height;
        int d2Count = scanX ? height : width;
        int borderEnd = 0;
        int borderStart = scanLeftTop ? 0 : d1Count - 1;
        if (scanLeftTop) {
            borderEnd = d1Count - 1;
        }
        int d1 = borderStart;
        while (d1 != borderEnd) {
            for (int d2 = 0; d2 < d2Count; d2++) {
                if ((pixels[scanX ? (d2 * width) + d1 : (d1 * width) + d2] >>> 24) > this.mMinVisibleAlpha) {
                    return d1;
                }
            }
            if (scanLeftTop) {
                d1++;
            } else {
                d1--;
            }
        }
        return -1;
    }
}
