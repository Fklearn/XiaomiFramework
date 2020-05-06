package miui.imagefilters;

import android.graphics.Bitmap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IImageFilter {
    boolean canConcurrence();

    boolean initParams(String str, List<Object> list);

    void process(ImageData imageData);

    void putOriginalImage(Bitmap bitmap);

    public static abstract class AbstractImageFilter implements IImageFilter {
        private Map<String, List<Object>> mParams;

        public abstract void processData(ImageData imageData);

        public boolean initParams(String paramName, List<Object> paramValues) {
            if (paramValues == null || paramValues.size() == 0) {
                return false;
            }
            boolean success = ImageFilterUtils.setProperty(this, paramName, paramValues.get(0));
            if (success && paramValues.size() > 1) {
                if (this.mParams == null) {
                    this.mParams = new HashMap();
                }
                this.mParams.put(paramName, paramValues);
            }
            return success;
        }

        public void process(ImageData imageData) {
            if (canConcurrence()) {
                fillRandomParams(imageData);
                processData(imageData);
                return;
            }
            synchronized (this) {
                fillRandomParams(imageData);
                processData(imageData);
            }
        }

        private void fillRandomParams(ImageData imageData) {
            Map<String, List<Object>> map = this.mParams;
            if (map != null) {
                for (Map.Entry<String, List<Object>> param : map.entrySet()) {
                    List<Object> paramValues = param.getValue();
                    ImageFilterUtils.setProperty(this, param.getKey(), paramValues.get(imageData.generalRandomNum(paramValues.size())));
                }
            }
        }

        public void putOriginalImage(Bitmap originalBitmap) {
        }

        public boolean canConcurrence() {
            Map<String, List<Object>> map = this.mParams;
            if (map == null || map.size() <= 1) {
                return true;
            }
            return false;
        }
    }

    public static class ImageFilterGroup {
        private IImageFilter[] mFilters;

        public ImageFilterGroup(IImageFilter[] filters) {
            this.mFilters = filters;
        }

        public ImageData processAll(Bitmap inputBitmap) {
            ImageData imgData = ImageData.bitmapToImageData(inputBitmap);
            for (IImageFilter filter : this.mFilters) {
                filter.putOriginalImage(inputBitmap);
                filter.process(imgData);
            }
            return imgData;
        }
    }
}
