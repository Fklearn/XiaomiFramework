package com.android.server.display;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.BrightnessCorrection;
import android.os.PowerManager;
import android.util.MathUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.Spline;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.display.utils.Plog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miui.os.SystemProperties;

public abstract class BrightnessMappingStrategy {
    private static final float LUX_GRAD_SMOOTHING = 0.25f;
    private static final float MAX_GRAD = 1.0f;
    /* access modifiers changed from: private */
    public static final Plog PLOG = Plog.createSystemPlog(TAG);
    private static final String TAG = "BrightnessMappingStrategy";
    /* access modifiers changed from: private */
    public static float[] sCorrectBrightnessGame;
    /* access modifiers changed from: private */
    public static float sCorrectBrightnessMaxLux;
    /* access modifiers changed from: private */
    public static float[] sCorrectBrightnessVideo;
    /* access modifiers changed from: private */
    public static Spline sGameBrightnessSpline;
    static List<String> sGameWhiteList = new ArrayList();
    /* access modifiers changed from: private */
    public static float[] sLowLuxLevels;
    /* access modifiers changed from: private */
    public static Spline sVideoBrightnessSpline;
    static List<String> sVideoWhiteList = new ArrayList();
    protected boolean mLoggingEnabled;
    public int mRotation = 0;

    public abstract void addUserDataPoint(float f, float f2);

    public abstract void clearUserDataPoints();

    public abstract float convertToNits(int i);

    public abstract void dump(PrintWriter printWriter);

    public abstract float getAutoBrightnessAdjustment();

    public abstract float getBrightness(float f, String str);

    public abstract float getBrightness(float f, String str, int i);

    public abstract BrightnessConfiguration getDefaultConfig();

    public abstract Spline getNitToBrightnessSpline();

    public abstract boolean hasUserDataPoints();

    public abstract boolean isDefaultConfig();

    public abstract boolean setAutoBrightnessAdjustment(float f);

    public abstract boolean setBrightnessConfiguration(BrightnessConfiguration brightnessConfiguration);

    public static BrightnessMappingStrategy create(Resources resources) {
        float[] nitsRange;
        int[] backlightRange;
        float[] luxLevels = getLuxLevels(resources.getIntArray(17235991));
        int[] brightnessLevelsBacklight = resources.getIntArray(17235990);
        float[] brightnessLevelsNits = getFloatArray(resources.obtainTypedArray(17235987));
        float autoBrightnessAdjustmentMaxGamma = resources.getFraction(18022400, 1, 1);
        sGameWhiteList = Arrays.asList(resources.getStringArray(17236088));
        sVideoWhiteList = Arrays.asList(resources.getStringArray(17236090));
        sLowLuxLevels = getLuxLevels(resources.getIntArray(17236100));
        sCorrectBrightnessGame = getFloatArray(resources.obtainTypedArray(17236087));
        sCorrectBrightnessVideo = getFloatArray(resources.obtainTypedArray(17236089));
        float[] fArr = sLowLuxLevels;
        sCorrectBrightnessMaxLux = fArr[fArr.length - 1];
        float maxScreenNit = Float.parseFloat(SystemProperties.get("persist.vendor.max.brightness", "0"));
        float minScreenNit = maxScreenNit / ((float) PowerManager.BRIGHTNESS_ON);
        if (maxScreenNit != 0.0f) {
            nitsRange = new float[]{minScreenNit, maxScreenNit};
            backlightRange = new int[]{1, PowerManager.BRIGHTNESS_ON};
        } else {
            nitsRange = getFloatArray(resources.obtainTypedArray(17236061));
            backlightRange = resources.getIntArray(17236060);
        }
        if (isValidMapping(nitsRange, backlightRange) && isValidMapping(luxLevels, brightnessLevelsNits)) {
            int minimumBacklight = resources.getInteger(17694891);
            int maximumBacklight = resources.getInteger(17694890);
            if (backlightRange[0] > minimumBacklight || backlightRange[backlightRange.length - 1] < maximumBacklight) {
                Slog.w(TAG, "Screen brightness mapping does not cover whole range of available backlight values, autobrightness functionality may be impaired.");
            }
            return new PhysicalMappingStrategy(new BrightnessConfiguration.Builder(luxLevels, brightnessLevelsNits).build(), nitsRange, backlightRange, autoBrightnessAdjustmentMaxGamma);
        } else if (isValidMapping(luxLevels, brightnessLevelsBacklight)) {
            return new SimpleMappingStrategy(luxLevels, brightnessLevelsBacklight, autoBrightnessAdjustmentMaxGamma);
        } else {
            return null;
        }
    }

    private static float[] getLuxLevels(int[] lux) {
        float[] levels = new float[(lux.length + 1)];
        for (int i = 0; i < lux.length; i++) {
            levels[i + 1] = (float) lux[i];
        }
        return levels;
    }

    private static float[] getFloatArray(TypedArray array) {
        int N = array.length();
        float[] vals = new float[N];
        for (int i = 0; i < N; i++) {
            vals[i] = array.getFloat(i, -1.0f);
        }
        array.recycle();
        return vals;
    }

    /* access modifiers changed from: private */
    public static boolean isValidMapping(float[] x, float[] y) {
        if (x == null || y == null || x.length == 0 || y.length == 0 || x.length != y.length) {
            return false;
        }
        int N = x.length;
        float prevX = x[0];
        float prevY = y[0];
        if (prevX < 0.0f || prevY < 0.0f || Float.isNaN(prevX) || Float.isNaN(prevY)) {
            return false;
        }
        for (int i = 1; i < N; i++) {
            if (prevX >= x[i] || prevY > y[i] || Float.isNaN(x[i]) || Float.isNaN(y[i])) {
                return false;
            }
            prevX = x[i];
            prevY = y[i];
        }
        return true;
    }

    private static boolean isValidMapping(float[] x, int[] y) {
        if (x == null || y == null || x.length == 0 || y.length == 0 || x.length != y.length) {
            return false;
        }
        int N = x.length;
        float prevX = x[0];
        int prevY = y[0];
        if (prevX < 0.0f || prevY < 0 || Float.isNaN(prevX)) {
            return false;
        }
        for (int i = 1; i < N; i++) {
            if (prevX >= x[i] || prevY > y[i] || Float.isNaN(x[i])) {
                return false;
            }
            prevX = x[i];
            prevY = y[i];
        }
        return true;
    }

    public boolean setLoggingEnabled(boolean loggingEnabled) {
        if (this.mLoggingEnabled == loggingEnabled) {
            return false;
        }
        this.mLoggingEnabled = loggingEnabled;
        return true;
    }

    public float getBrightness(float lux) {
        return getBrightness(lux, (String) null, -1);
    }

    /* access modifiers changed from: protected */
    public float normalizeAbsoluteBrightness(int brightness) {
        return ((float) MathUtils.constrain(brightness, 0, PowerManager.BRIGHTNESS_ON)) / ((float) PowerManager.BRIGHTNESS_ON);
    }

    private Pair<float[], float[]> insertControlPoint(float[] luxLevels, float[] brightnessLevels, float lux, float brightness) {
        float[] newBrightnessLevels;
        float[] newLuxLevels;
        int idx = findInsertionPoint(luxLevels, lux);
        if (idx == luxLevels.length) {
            newLuxLevels = Arrays.copyOf(luxLevels, luxLevels.length + 1);
            newBrightnessLevels = Arrays.copyOf(brightnessLevels, brightnessLevels.length + 1);
            newLuxLevels[idx] = lux;
            newBrightnessLevels[idx] = brightness;
        } else if (luxLevels[idx] == lux) {
            newLuxLevels = Arrays.copyOf(luxLevels, luxLevels.length);
            newBrightnessLevels = Arrays.copyOf(brightnessLevels, brightnessLevels.length);
            newBrightnessLevels[idx] = brightness;
        } else {
            newLuxLevels = Arrays.copyOf(luxLevels, luxLevels.length + 1);
            System.arraycopy(newLuxLevels, idx, newLuxLevels, idx + 1, luxLevels.length - idx);
            newLuxLevels[idx] = lux;
            newBrightnessLevels = Arrays.copyOf(brightnessLevels, brightnessLevels.length + 1);
            System.arraycopy(newBrightnessLevels, idx, newBrightnessLevels, idx + 1, brightnessLevels.length - idx);
            newBrightnessLevels[idx] = brightness;
        }
        AutomaticBrightnessControllerInjector.smoothNewCurve(newLuxLevels, newBrightnessLevels, idx);
        return Pair.create(newLuxLevels, newBrightnessLevels);
    }

    private int findInsertionPoint(float[] arr, float val) {
        for (int i = 0; i < arr.length; i++) {
            if (val <= arr[i]) {
                return i;
            }
        }
        return arr.length;
    }

    private void smoothCurve(float[] lux, float[] brightness, int idx) {
        if (this.mLoggingEnabled) {
            PLOG.logCurve("unsmoothed curve", lux, brightness);
        }
        float prevLux = lux[idx];
        float prevBrightness = brightness[idx];
        for (int i = idx + 1; i < lux.length; i++) {
            float currLux = lux[i];
            float currBrightness = brightness[i];
            float newBrightness = MathUtils.constrain(currBrightness, prevBrightness, permissibleRatio(currLux, prevLux) * prevBrightness);
            if (newBrightness == currBrightness) {
                break;
            }
            prevLux = currLux;
            prevBrightness = newBrightness;
            brightness[i] = newBrightness;
        }
        float prevLux2 = lux[idx];
        float prevBrightness2 = brightness[idx];
        for (int i2 = idx - 1; i2 >= 0; i2--) {
            float currLux2 = lux[i2];
            float currBrightness2 = brightness[i2];
            float newBrightness2 = MathUtils.constrain(currBrightness2, permissibleRatio(currLux2, prevLux2) * prevBrightness2, prevBrightness2);
            if (newBrightness2 == currBrightness2) {
                break;
            }
            prevLux2 = currLux2;
            prevBrightness2 = newBrightness2;
            brightness[i2] = newBrightness2;
        }
        if (this.mLoggingEnabled != 0) {
            PLOG.logCurve("smoothed curve", lux, brightness);
        }
    }

    private float permissibleRatio(float currLux, float prevLux) {
        return MathUtils.exp((MathUtils.log(currLux + LUX_GRAD_SMOOTHING) - MathUtils.log(LUX_GRAD_SMOOTHING + prevLux)) * 1.0f);
    }

    /* access modifiers changed from: protected */
    public float inferAutoBrightnessAdjustment(float maxGamma, float desiredBrightness, float currentBrightness) {
        float adjustment;
        float gamma = Float.NaN;
        if (currentBrightness <= 0.1f || currentBrightness >= 0.9f) {
            adjustment = desiredBrightness - currentBrightness;
        } else if (desiredBrightness == 0.0f) {
            adjustment = -1.0f;
        } else if (desiredBrightness == 1.0f) {
            adjustment = 1.0f;
        } else {
            gamma = MathUtils.log(desiredBrightness) / MathUtils.log(currentBrightness);
            adjustment = (-MathUtils.log(gamma)) / MathUtils.log(maxGamma);
        }
        float adjustment2 = MathUtils.constrain(adjustment, -1.0f, 1.0f);
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "inferAutoBrightnessAdjustment: " + maxGamma + "^" + (-adjustment2) + "=" + MathUtils.pow(maxGamma, -adjustment2) + " == " + gamma);
            Slog.d(TAG, "inferAutoBrightnessAdjustment: " + currentBrightness + "^" + gamma + "=" + MathUtils.pow(currentBrightness, gamma) + " == " + desiredBrightness);
        }
        return adjustment2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: float[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: float[]} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.Pair<float[], float[]> getAdjustedCurve(float[] r9, float[] r10, float r11, float r12, float r13, float r14) {
        /*
            r8 = this;
            r0 = r9
            int r1 = r10.length
            float[] r1 = java.util.Arrays.copyOf(r10, r1)
            boolean r2 = r8.mLoggingEnabled
            if (r2 == 0) goto L_0x0012
            com.android.server.display.utils.Plog r2 = PLOG
            java.lang.String r3 = "unadjusted curve"
            r2.logCurve(r3, r0, r1)
        L_0x0012:
            r2 = 1065353216(0x3f800000, float:1.0)
            r3 = -1082130432(0xffffffffbf800000, float:-1.0)
            float r13 = android.util.MathUtils.constrain(r13, r3, r2)
            float r4 = -r13
            float r4 = android.util.MathUtils.pow(r14, r4)
            boolean r5 = r8.mLoggingEnabled
            if (r5 == 0) goto L_0x0057
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "getAdjustedCurve: "
            r5.append(r6)
            r5.append(r14)
            java.lang.String r6 = "^"
            r5.append(r6)
            float r6 = -r13
            r5.append(r6)
            java.lang.String r6 = "="
            r5.append(r6)
            float r6 = -r13
            float r6 = android.util.MathUtils.pow(r14, r6)
            r5.append(r6)
            java.lang.String r6 = " == "
            r5.append(r6)
            r5.append(r4)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "BrightnessMappingStrategy"
            android.util.Slog.d(r6, r5)
        L_0x0057:
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 == 0) goto L_0x006a
            r2 = 0
        L_0x005c:
            int r5 = r1.length
            if (r2 >= r5) goto L_0x006a
            r5 = r1[r2]
            float r5 = android.util.MathUtils.pow(r5, r4)
            r1[r2] = r5
            int r2 = r2 + 1
            goto L_0x005c
        L_0x006a:
            boolean r2 = r8.mLoggingEnabled
            if (r2 == 0) goto L_0x0075
            com.android.server.display.utils.Plog r2 = PLOG
            java.lang.String r5 = "gamma adjusted curve"
            r2.logCurve(r5, r0, r1)
        L_0x0075:
            int r2 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x00a6
            android.util.Pair r2 = r8.insertControlPoint(r0, r1, r11, r12)
            java.lang.Object r3 = r2.first
            r0 = r3
            float[] r0 = (float[]) r0
            java.lang.Object r3 = r2.second
            r1 = r3
            float[] r1 = (float[]) r1
            boolean r3 = r8.mLoggingEnabled
            if (r3 == 0) goto L_0x00a6
            com.android.server.display.utils.Plog r3 = PLOG
            java.lang.String r5 = "gamma and user adjusted curve"
            r3.logCurve(r5, r0, r1)
            android.util.Pair r2 = r8.insertControlPoint(r9, r10, r11, r12)
            com.android.server.display.utils.Plog r3 = PLOG
            java.lang.Object r5 = r2.first
            float[] r5 = (float[]) r5
            java.lang.Object r6 = r2.second
            float[] r6 = (float[]) r6
            java.lang.String r7 = "user adjusted curve"
            r3.logCurve(r7, r5, r6)
        L_0x00a6:
            android.util.Pair r2 = android.util.Pair.create(r0, r1)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.BrightnessMappingStrategy.getAdjustedCurve(float[], float[], float, float, float, float):android.util.Pair");
    }

    private static class SimpleMappingStrategy extends BrightnessMappingStrategy {
        private float mAutoBrightnessAdjustment;
        private final float[] mBrightness;
        private final float[] mLux;
        private float mMaxGamma;
        private Spline mSpline;
        private float mUserBrightness;
        private float mUserLux;

        public Spline getNitToBrightnessSpline() {
            return null;
        }

        public SimpleMappingStrategy(float[] lux, int[] brightness, float maxGamma) {
            boolean z = true;
            Preconditions.checkArgument((lux.length == 0 || brightness.length == 0) ? false : true, "Lux and brightness arrays must not be empty!");
            Preconditions.checkArgument(lux.length != brightness.length ? false : z, "Lux and brightness arrays must be the same length!");
            Preconditions.checkArrayElementsInRange(lux, 0.0f, Float.MAX_VALUE, "lux");
            Preconditions.checkArrayElementsInRange(brightness, 0, Integer.MAX_VALUE, "brightness");
            int N = brightness.length;
            this.mLux = new float[N];
            this.mBrightness = new float[N];
            for (int i = 0; i < N; i++) {
                this.mLux[i] = lux[i];
                this.mBrightness[i] = normalizeAbsoluteBrightness(brightness[i]);
            }
            this.mMaxGamma = maxGamma;
            this.mAutoBrightnessAdjustment = 0.0f;
            this.mUserLux = -1.0f;
            this.mUserBrightness = -1.0f;
            if (this.mLoggingEnabled) {
                BrightnessMappingStrategy.PLOG.start("simple mapping strategy");
            }
            computeSpline();
        }

        public boolean setBrightnessConfiguration(BrightnessConfiguration config) {
            return false;
        }

        public float getBrightness(float lux, String packageName, int category) {
            return this.mSpline.interpolate(lux);
        }

        public float getBrightness(float lux, String packageName) {
            return this.mSpline.interpolate(lux);
        }

        public float getAutoBrightnessAdjustment() {
            return this.mAutoBrightnessAdjustment;
        }

        public boolean setAutoBrightnessAdjustment(float adjustment) {
            float adjustment2 = MathUtils.constrain(adjustment, -1.0f, 1.0f);
            if (adjustment2 == this.mAutoBrightnessAdjustment) {
                return false;
            }
            if (this.mLoggingEnabled) {
                Slog.d(BrightnessMappingStrategy.TAG, "setAutoBrightnessAdjustment: " + this.mAutoBrightnessAdjustment + " => " + adjustment2);
                BrightnessMappingStrategy.PLOG.start("auto-brightness adjustment");
            }
            this.mAutoBrightnessAdjustment = adjustment2;
            computeSpline();
            return true;
        }

        public float convertToNits(int backlight) {
            return -1.0f;
        }

        public void addUserDataPoint(float lux, float brightness) {
            float unadjustedBrightness = getUnadjustedBrightness(lux);
            AutomaticBrightnessControllerInjector.updateUnadjustedBrightness(lux, brightness, unadjustedBrightness);
            if (this.mLoggingEnabled) {
                Slog.d(BrightnessMappingStrategy.TAG, "addUserDataPoint: (" + lux + "," + brightness + ")");
                BrightnessMappingStrategy.PLOG.start("add user data point").logPoint("user data point", lux, brightness).logPoint("current brightness", lux, unadjustedBrightness);
            }
            float adjustment = inferAutoBrightnessAdjustment(this.mMaxGamma, brightness, unadjustedBrightness);
            if (this.mLoggingEnabled) {
                Slog.d(BrightnessMappingStrategy.TAG, "addUserDataPoint: " + this.mAutoBrightnessAdjustment + " => " + adjustment);
            }
            this.mAutoBrightnessAdjustment = adjustment;
            this.mUserLux = lux;
            this.mUserBrightness = brightness;
            computeSpline();
        }

        public void clearUserDataPoints() {
            if (this.mUserLux != -1.0f) {
                if (this.mLoggingEnabled) {
                    Slog.d(BrightnessMappingStrategy.TAG, "clearUserDataPoints: " + this.mAutoBrightnessAdjustment + " => 0");
                    BrightnessMappingStrategy.PLOG.start("clear user data points").logPoint("user data point", this.mUserLux, this.mUserBrightness);
                }
                this.mAutoBrightnessAdjustment = 0.0f;
                this.mUserLux = -1.0f;
                this.mUserBrightness = -1.0f;
                computeSpline();
            }
        }

        public boolean hasUserDataPoints() {
            return this.mUserLux != -1.0f;
        }

        public boolean isDefaultConfig() {
            return true;
        }

        public BrightnessConfiguration getDefaultConfig() {
            return null;
        }

        public void dump(PrintWriter pw) {
            pw.println("SimpleMappingStrategy");
            pw.println("  mSpline=" + this.mSpline);
            pw.println("  mMaxGamma=" + this.mMaxGamma);
            pw.println("  mAutoBrightnessAdjustment=" + this.mAutoBrightnessAdjustment);
            pw.println("  mUserLux=" + this.mUserLux);
            pw.println("  mUserBrightness=" + this.mUserBrightness);
        }

        private void computeSpline() {
            Pair<float[], float[]> curve = getAdjustedCurve(this.mLux, this.mBrightness, this.mUserLux, this.mUserBrightness, this.mAutoBrightnessAdjustment, this.mMaxGamma);
            this.mSpline = Spline.createSpline((float[]) curve.first, (float[]) curve.second);
        }

        private float getUnadjustedBrightness(float lux) {
            return Spline.createSpline(this.mLux, this.mBrightness).interpolate(lux);
        }
    }

    @VisibleForTesting
    static class PhysicalMappingStrategy extends BrightnessMappingStrategy {
        private float mAutoBrightnessAdjustment;
        private Spline mBacklightToNitsSpline;
        private Spline mBrightnessSpline;
        private BrightnessConfiguration mConfig;
        private final BrightnessConfiguration mDefaultConfig;
        private float mMaxGamma;
        private final Spline mNitsToBacklightSpline;
        private float mUserBrightness;
        private float mUserLux;

        public Spline getNitToBrightnessSpline() {
            return this.mNitsToBacklightSpline;
        }

        public PhysicalMappingStrategy(BrightnessConfiguration config, float[] nits, int[] backlight, float maxGamma) {
            boolean z = true;
            Preconditions.checkArgument((nits.length == 0 || backlight.length == 0) ? false : true, "Nits and backlight arrays must not be empty!");
            Preconditions.checkArgument(nits.length != backlight.length ? false : z, "Nits and backlight arrays must be the same length!");
            Preconditions.checkNotNull(config);
            Preconditions.checkArrayElementsInRange(nits, 0.0f, Float.MAX_VALUE, "nits");
            Preconditions.checkArrayElementsInRange(backlight, 0, PowerManager.BRIGHTNESS_ON, "backlight");
            this.mMaxGamma = maxGamma;
            this.mAutoBrightnessAdjustment = 0.0f;
            this.mUserLux = -1.0f;
            this.mUserBrightness = -1.0f;
            int N = nits.length;
            float[] normalizedBacklight = new float[N];
            for (int i = 0; i < N; i++) {
                normalizedBacklight[i] = normalizeAbsoluteBrightness(backlight[i]);
            }
            this.mNitsToBacklightSpline = Spline.createLinearSpline(nits, normalizedBacklight);
            this.mBacklightToNitsSpline = Spline.createLinearSpline(normalizedBacklight, nits);
            Spline unused = BrightnessMappingStrategy.sGameBrightnessSpline = getCorrectSpline(BrightnessMappingStrategy.sLowLuxLevels, BrightnessMappingStrategy.sCorrectBrightnessGame);
            Spline unused2 = BrightnessMappingStrategy.sVideoBrightnessSpline = getCorrectSpline(BrightnessMappingStrategy.sLowLuxLevels, BrightnessMappingStrategy.sCorrectBrightnessVideo);
            this.mDefaultConfig = config;
            if (this.mLoggingEnabled) {
                BrightnessMappingStrategy.PLOG.start("physical mapping strategy");
            }
            this.mConfig = config;
            computeSpline();
        }

        public boolean setBrightnessConfiguration(BrightnessConfiguration config) {
            if (config == null) {
                config = this.mDefaultConfig;
            }
            if (config.equals(this.mConfig)) {
                return false;
            }
            if (this.mLoggingEnabled) {
                BrightnessMappingStrategy.PLOG.start("brightness configuration");
            }
            this.mConfig = config;
            computeSpline();
            return true;
        }

        public float getBrightness(float lux, String packageName, int category) {
            float backlight = this.mNitsToBacklightSpline.interpolate(this.mBrightnessSpline.interpolate(lux));
            if (this.mUserLux == -1.0f) {
                return correctBrightness(backlight, packageName, category);
            }
            if (!this.mLoggingEnabled) {
                return backlight;
            }
            Slog.d(BrightnessMappingStrategy.TAG, "user point set, correction not applied");
            return backlight;
        }

        public float getBrightness(float lux, String packageName) {
            float backlight = this.mNitsToBacklightSpline.interpolate(this.mBrightnessSpline.interpolate(lux));
            if (this.mUserLux == -1.0f) {
                return correctBrightness(lux, backlight, packageName);
            }
            if (!this.mLoggingEnabled) {
                return backlight;
            }
            Slog.d(BrightnessMappingStrategy.TAG, "user point set, correction not applied");
            return backlight;
        }

        public float getAutoBrightnessAdjustment() {
            return this.mAutoBrightnessAdjustment;
        }

        public boolean setAutoBrightnessAdjustment(float adjustment) {
            float adjustment2 = MathUtils.constrain(adjustment, -1.0f, 1.0f);
            if (adjustment2 == this.mAutoBrightnessAdjustment) {
                return false;
            }
            if (this.mLoggingEnabled) {
                Slog.d(BrightnessMappingStrategy.TAG, "setAutoBrightnessAdjustment: " + this.mAutoBrightnessAdjustment + " => " + adjustment2);
                BrightnessMappingStrategy.PLOG.start("auto-brightness adjustment");
            }
            this.mAutoBrightnessAdjustment = adjustment2;
            computeSpline();
            return true;
        }

        public float convertToNits(int backlight) {
            return this.mBacklightToNitsSpline.interpolate(normalizeAbsoluteBrightness(backlight));
        }

        public void addUserDataPoint(float lux, float brightness) {
            float unadjustedBrightness = getUnadjustedBrightness(lux);
            AutomaticBrightnessControllerInjector.updateUnadjustedBrightness(lux, brightness, unadjustedBrightness);
            if (this.mLoggingEnabled) {
                Slog.d(BrightnessMappingStrategy.TAG, "addUserDataPoint: (" + lux + "," + brightness + ")");
                BrightnessMappingStrategy.PLOG.start("add user data point").logPoint("user data point", lux, brightness).logPoint("current brightness", lux, unadjustedBrightness);
            }
            float adjustment = inferAutoBrightnessAdjustment(this.mMaxGamma, brightness, unadjustedBrightness);
            if (this.mLoggingEnabled) {
                Slog.d(BrightnessMappingStrategy.TAG, "addUserDataPoint: " + this.mAutoBrightnessAdjustment + " => " + adjustment);
            }
            this.mAutoBrightnessAdjustment = adjustment;
            this.mUserLux = lux;
            this.mUserBrightness = brightness;
            computeSpline();
        }

        public void clearUserDataPoints() {
            if (this.mUserLux != -1.0f) {
                if (this.mLoggingEnabled) {
                    Slog.d(BrightnessMappingStrategy.TAG, "clearUserDataPoints: " + this.mAutoBrightnessAdjustment + " => 0");
                    BrightnessMappingStrategy.PLOG.start("clear user data points").logPoint("user data point", this.mUserLux, this.mUserBrightness);
                }
                this.mAutoBrightnessAdjustment = 0.0f;
                this.mUserLux = -1.0f;
                this.mUserBrightness = -1.0f;
                computeSpline();
            }
        }

        public boolean hasUserDataPoints() {
            return this.mUserLux != -1.0f;
        }

        public boolean isDefaultConfig() {
            return this.mDefaultConfig.equals(this.mConfig);
        }

        public BrightnessConfiguration getDefaultConfig() {
            return this.mDefaultConfig;
        }

        public void dump(PrintWriter pw) {
            pw.println("PhysicalMappingStrategy");
            pw.println("  mConfig=" + this.mConfig);
            pw.println("  mBrightnessSpline=" + this.mBrightnessSpline);
            pw.println("  mNitsToBacklightSpline=" + this.mNitsToBacklightSpline);
            pw.println("  mMaxGamma=" + this.mMaxGamma);
            pw.println("  mAutoBrightnessAdjustment=" + this.mAutoBrightnessAdjustment);
            pw.println("  mUserLux=" + this.mUserLux);
            pw.println("  mUserBrightness=" + this.mUserBrightness);
            pw.println("  mDefaultConfig=" + this.mDefaultConfig);
        }

        private void computeSpline() {
            Pair<float[], float[]> defaultCurve = this.mConfig.getCurve();
            float[] defaultLux = (float[]) defaultCurve.first;
            float[] defaultNits = (float[]) defaultCurve.second;
            float[] defaultBacklight = new float[defaultNits.length];
            for (int i = 0; i < defaultBacklight.length; i++) {
                defaultBacklight[i] = this.mNitsToBacklightSpline.interpolate(defaultNits[i]);
            }
            Pair<float[], float[]> curve = getAdjustedCurve(defaultLux, defaultBacklight, this.mUserLux, this.mUserBrightness, this.mAutoBrightnessAdjustment, this.mMaxGamma);
            float[] lux = (float[]) curve.first;
            float[] backlight = (float[]) curve.second;
            float[] nits = new float[backlight.length];
            for (int i2 = 0; i2 < nits.length; i2++) {
                nits[i2] = this.mBacklightToNitsSpline.interpolate(backlight[i2]);
            }
            this.mBrightnessSpline = Spline.createSpline(lux, nits);
        }

        private float getUnadjustedBrightness(float lux) {
            Pair<float[], float[]> curve = this.mConfig.getCurve();
            return this.mNitsToBacklightSpline.interpolate(Spline.createSpline((float[]) curve.first, (float[]) curve.second).interpolate(lux));
        }

        private float correctBrightness(float brightness, String packageName, int category) {
            BrightnessCorrection correction;
            BrightnessCorrection correction2;
            if (packageName != null && (correction2 = this.mConfig.getCorrectionByPackageName(packageName)) != null) {
                return correction2.apply(brightness);
            }
            if (category == -1 || (correction = this.mConfig.getCorrectionByCategory(category)) == null) {
                return brightness;
            }
            return correction.apply(brightness);
        }

        private float correctBrightness(float lux, float brightness, String packageName) {
            if (BrightnessMappingStrategy.sGameBrightnessSpline != null && sGameWhiteList.contains(packageName)) {
                return getCorrectBrightness(lux, brightness, BrightnessMappingStrategy.sGameBrightnessSpline);
            }
            if (BrightnessMappingStrategy.sVideoBrightnessSpline == null || !sVideoWhiteList.contains(packageName) || (this.mRotation != 1 && this.mRotation != 3)) {
                return brightness;
            }
            return getCorrectBrightness(lux, brightness, BrightnessMappingStrategy.sVideoBrightnessSpline);
        }

        private float getCorrectBrightness(float lux, float brightness, Spline correctSpline) {
            if (lux > BrightnessMappingStrategy.sCorrectBrightnessMaxLux) {
                return brightness;
            }
            return this.mNitsToBacklightSpline.interpolate(correctSpline.interpolate(lux));
        }

        private Spline getCorrectSpline(float[] lux, float[] brightness) {
            if (!BrightnessMappingStrategy.isValidMapping(lux, brightness)) {
                return null;
            }
            float[] nit = new float[brightness.length];
            for (int i = 0; i < brightness.length; i++) {
                nit[i] = this.mBacklightToNitsSpline.interpolate(brightness[i]);
            }
            return Spline.createSpline(lux, nit);
        }
    }
}
