package miui.imagefilters;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ImageFilterBuilder {
    private static final String TAG = "ImageFilterBuilder";
    private String mFilterName;
    private boolean mIgnoreWhenNotSupported = false;
    private List<ParamData> mParams = new ArrayList();

    public static class NoSupportException extends Exception {
        public NoSupportException(String msg) {
            super(msg);
        }
    }

    private static class ParamData {
        boolean mIgnoreWhenNotSupported;
        String mParamName;
        List<Object> mParamValues;

        private ParamData() {
            this.mIgnoreWhenNotSupported = false;
        }
    }

    public void setIgnoreWhenNotSupported(boolean isAllowIgnore) {
        this.mIgnoreWhenNotSupported = isAllowIgnore;
    }

    public void setFilterName(String filterName) {
        this.mFilterName = filterName;
    }

    public void addParam(final String paramName, final List<Object> paramValues, final boolean allowIgnore) {
        this.mParams.add(new ParamData() {
            {
                this.mParamName = paramName;
                this.mParamValues = paramValues;
                this.mIgnoreWhenNotSupported = allowIgnore;
            }
        });
    }

    public IImageFilter createImageFilter() throws NoSupportException {
        IImageFilter filter;
        if ("Hsl".equalsIgnoreCase(this.mFilterName)) {
            filter = new HslWrapFilter();
        } else if ("Edges".equalsIgnoreCase(this.mFilterName)) {
            filter = new EdgesFilter();
        } else if ("Levels".equalsIgnoreCase(this.mFilterName)) {
            filter = new LevelsFilter();
        } else if ("GrayScale".equalsIgnoreCase(this.mFilterName)) {
            filter = new GrayScaleFilter();
        } else if ("BlendImage".equalsIgnoreCase(this.mFilterName)) {
            filter = new BlendImageFilter();
        } else if ("ColorMatrix".equalsIgnoreCase(this.mFilterName)) {
            filter = new ColorMatrixFilter();
        } else if ("Convolution".equalsIgnoreCase(this.mFilterName)) {
            filter = new ConvolutionFilter();
        } else if ("Threshold".equalsIgnoreCase(this.mFilterName)) {
            filter = new ThresholdFilter();
        } else if ("Spread".equalsIgnoreCase(this.mFilterName)) {
            filter = new SpreadFilter();
        } else if ("Transform".equalsIgnoreCase(this.mFilterName)) {
            filter = new TransformFilter();
        } else {
            Log.w(TAG, "unknown filter:" + this.mFilterName);
            if (this.mIgnoreWhenNotSupported) {
                return null;
            }
            throw new NoSupportException("filter:" + this.mFilterName + " not support.");
        }
        for (ParamData param : this.mParams) {
            if (!filter.initParams(param.mParamName, param.mParamValues) && !param.mIgnoreWhenNotSupported) {
                if (this.mIgnoreWhenNotSupported) {
                    return null;
                }
                throw new NoSupportException("param:" + param.mParamName + " not support.");
            }
        }
        return filter;
    }
}
