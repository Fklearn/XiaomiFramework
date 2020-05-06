package com.android.server.display;

import android.content.Context;
import android.util.Slog;
import com.android.server.FeatureInjector;
import java.util.Arrays;

public class AmbientLightFlickBuffer {
    private static final int BRIGHTNESS_FLICK = 2;
    private static final int BRIGHTNESS_NO_FLICK = 1;
    private static final int FLICK_BRIGHTNESS_THRESHOLD = 300;
    private static final int FLICK_TIME_THRESHOLD = 4000;
    private static final String TAG = "AmbientLightFlickBuffer";
    private int[] mBrightness;
    private int mCapacity = 4;
    private Context mContext;
    private float[] mLux;
    private int mNext = 0;
    private int mReturnBrightness;
    private long[] mTime;
    private int mTimeThreshold;

    AmbientLightFlickBuffer(Context context) {
        int i = this.mCapacity;
        this.mBrightness = new int[i];
        this.mTime = new long[i];
        this.mLux = new float[i];
        this.mTimeThreshold = FLICK_TIME_THRESHOLD;
        this.mContext = context;
    }

    public void clear() {
        int i = this.mCapacity;
        this.mBrightness = new int[i];
        this.mTime = new long[i];
        this.mLux = new float[i];
        this.mNext = 0;
    }

    private void updateBrightnessList(int brightness, long time, float lux) {
        int i = this.mNext;
        if (i < this.mCapacity) {
            this.mBrightness[i] = brightness;
            this.mTime[i] = time;
            this.mLux[i] = lux;
            this.mNext = i + 1;
            this.mReturnBrightness = brightness;
            return;
        }
        int i2 = 0;
        while (true) {
            int i3 = this.mCapacity;
            if (i2 < i3 - 1) {
                int tmp = i2 + 1;
                int[] iArr = this.mBrightness;
                iArr[i2] = iArr[tmp];
                long[] jArr = this.mTime;
                jArr[i2] = jArr[tmp];
                float[] fArr = this.mLux;
                fArr[i2] = fArr[tmp];
                i2++;
            } else {
                this.mBrightness[i3 - 1] = brightness;
                this.mTime[i3 - 1] = time;
                this.mLux[i3 - 1] = lux;
                return;
            }
        }
    }

    private int getBrightness(int brightneeType, int brightness) {
        if (brightneeType == 2) {
            String fgAppName = FeatureInjector.getForegroundAppPackageName(this.mContext);
            String message = "flick brightness : " + Arrays.toString(this.mBrightness) + ", lux : " + Arrays.toString(this.mLux);
            FeatureInjector.onBrightnessFeature(fgAppName, fgAppName, message, message, FeatureInjector.BRIGHTNESS_FLICK_FEATURES);
            Slog.d(TAG, message + " time is :" + Arrays.toString(this.mTime));
        } else {
            this.mReturnBrightness = brightness;
        }
        return this.mReturnBrightness;
    }

    private int checkThreshold() {
        int brightnessType = 1;
        int i = 1;
        while (i < this.mCapacity) {
            int[] iArr = this.mBrightness;
            if (Math.abs(iArr[i] - iArr[i - 1]) > 300) {
                long[] jArr = this.mTime;
                if (Math.abs(jArr[i] - jArr[i - 1]) < ((long) this.mTimeThreshold)) {
                    brightnessType = 2;
                    i++;
                }
            }
            clear();
            return 1;
        }
        return brightnessType;
    }

    private int getBrightnssType() {
        int[] iArr = this.mBrightness;
        boolean lastComparativeData1 = iArr[1] >= iArr[0];
        int[] iArr2 = this.mBrightness;
        boolean lastComparativeData2 = iArr2[1] >= iArr2[2];
        for (int i = 1; i < this.mCapacity; i += 2) {
            int[] iArr3 = this.mBrightness;
            boolean comparativeData = iArr3[i] >= iArr3[i + -1];
            if (comparativeData != lastComparativeData1) {
                return 1;
            }
            lastComparativeData1 = comparativeData;
            int next = i + 1 > this.mCapacity - 1 ? 0 : i + 1;
            int[] iArr4 = this.mBrightness;
            boolean comparativeData2 = iArr4[i] >= iArr4[next];
            if (comparativeData2 != lastComparativeData2) {
                return 1;
            }
            lastComparativeData2 = comparativeData2;
        }
        if (lastComparativeData1 != lastComparativeData2) {
            return 1;
        }
        return 2;
    }

    public int getCurrentBrightness(int brightness, long time, float lux) {
        updateBrightnessList(brightness, time, lux);
        int brightnessType = getBrightnssType();
        if (brightnessType == 2) {
            brightnessType = checkThreshold();
        }
        return getBrightness(brightnessType, brightness);
    }
}
