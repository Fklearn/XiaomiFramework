package miui.imagefilters;

import android.graphics.Bitmap;
import com.android.server.wifi.ScoringParams;

public class ImageData {
    private int[] backPixels;
    int height;
    private int mHashCode = 0;
    int[] pixels;
    int width;

    public ImageData(int width2, int height2) {
        this.width = width2;
        this.height = height2;
        this.pixels = new int[(width2 * height2)];
    }

    public int generalRandomNum(int maxNum) {
        int tempCode = this.mHashCode;
        if (tempCode == 0) {
            int xStep = this.width / 8;
            int yStep = this.height / 8;
            for (int i = 1; i < 8; i++) {
                tempCode ^= this.pixels[((i * yStep) * this.width) + (i * xStep)];
            }
            if (tempCode == 0) {
                tempCode = maxNum;
            }
            tempCode &= ScoringParams.Values.MAX_EXPID;
        }
        this.mHashCode = tempCode;
        return tempCode % maxNum;
    }

    public int[] getBackPixels() {
        int[] iArr = this.backPixels;
        if (iArr == null || iArr.length != this.pixels.length) {
            this.backPixels = new int[this.pixels.length];
        }
        return this.backPixels;
    }

    public void swapPixels() {
        int[] temp = this.pixels;
        this.pixels = this.backPixels;
        this.backPixels = temp;
    }

    public static ImageData bitmapToImageData(Bitmap bitmap) {
        ImageData imgData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
        int[] iArr = imgData.pixels;
        int i = imgData.width;
        bitmap.getPixels(iArr, 0, i, 0, 0, i, imgData.height);
        imgData.generalRandomNum(100);
        return imgData;
    }

    public static Bitmap imageDataToBitmap(ImageData imgData) {
        return Bitmap.createBitmap(imgData.pixels, imgData.width, imgData.height, Bitmap.Config.ARGB_8888);
    }
}
