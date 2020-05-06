package miui.graphics;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import com.android.server.wifi.hotspot2.anqp.Constants;
import java.lang.reflect.Field;

public class BitmapUtil {
    private static final int COLOR_BYTE_SIZE = 4;
    private static final String TAG = "BitmapUtil";

    public static byte[] getBuffer(Bitmap bitmap) {
        byte[] ret = null;
        if (Build.VERSION.SDK_INT >= 26) {
            int baseWidth = bitmap.getWidth();
            int baseHeight = bitmap.getHeight();
            try {
                int[] basePixels = new int[(bitmap.getByteCount() / 4)];
                bitmap.getPixels(basePixels, 0, bitmap.getRowBytes() / 4, 0, 0, baseWidth, baseHeight);
                ret = new byte[bitmap.getByteCount()];
                for (int i = 0; i < basePixels.length; i++) {
                    ret[(i * 4) + 3] = (byte) ((basePixels[i] >> 24) & Constants.BYTE_MASK);
                    ret[i * 4] = (byte) ((basePixels[i] >> 16) & Constants.BYTE_MASK);
                    ret[(i * 4) + 1] = (byte) ((basePixels[i] >> 8) & Constants.BYTE_MASK);
                    ret[(i * 4) + 2] = (byte) (basePixels[i] & Constants.BYTE_MASK);
                }
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "failed to get buffer, baseWidth = " + baseWidth + ", baseHeight = " + baseHeight, e);
            }
            Bitmap bitmap2 = bitmap;
            return ret;
        }
        try {
            Field field = Bitmap.class.getDeclaredField("mBuffer");
            field.setAccessible(true);
            try {
                return (byte[]) field.get(bitmap);
            } catch (Exception e2) {
                e = e2;
                Log.w(TAG, "get Bitmap.mBuffer failed!", e);
                return null;
            } catch (OutOfMemoryError e3) {
                e = e3;
                Log.e(TAG, "failed to get Bitmap.mBuffer", e);
                return null;
            }
        } catch (Exception e4) {
            e = e4;
            Bitmap bitmap3 = bitmap;
            Log.w(TAG, "get Bitmap.mBuffer failed!", e);
            return null;
        } catch (OutOfMemoryError e5) {
            e = e5;
            Bitmap bitmap4 = bitmap;
            Log.e(TAG, "failed to get Bitmap.mBuffer", e);
            return null;
        }
    }
}
