package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import java.util.Calendar;

public class TrafficDetailView extends View {
    public static final int DAY_TYPE = 1;
    private static final int DEFAULT_TEXT_SIZE = 12;
    public static final int HOUR_TYPE = 0;
    private static final int LINE = 5;
    private static final int PLAS_PERCENT = 2;
    private static final int PLAS_SPACE_PERCENT = 1;
    private static final int PLAS_TOTAL = 3;
    private static final String TAG = "TrafficDetailView";
    private static final int TOP_MARGIN = 12;
    private static final int X_AXIS_MARGIN = 3;
    private static final int X_AXIS_TEXT_Y_OFFSET = 2;
    private static final int Y_AXIS_MARGIN = 3;
    private boolean invalid;
    private ChartDragListener mChartDragListener;
    private Paint mDashPaint;
    private long[] mData;
    private float mDensity;
    private String mEndTimeTxt;
    private Paint mFillPaint;
    private Paint mHighLightPaint;
    private boolean mIsDragging;
    private int[] mLocation;
    private long mMaxValue;
    private int mMonthMaxDay;
    private float mPlasWidth;
    private float[] mPoints;
    private int mScaledTouchSlop;
    private String mStartTimeTxt;
    private int mTextColor;
    private float mTextHeight;
    private Paint mTextPaint;
    private int mTextSize;
    private float mTopMargin;
    private int mTouch = -1;
    private float mTouchDownX;
    private float mTouchDownY;
    private int mType;
    private float mXAxisMargin;
    private float mXTextMaxWidth;
    private String[] mXValueTexts;
    private long[] mXValues;
    private float mYAxisMargin;

    public interface ChartDragListener {
        void onDragEnd();

        void onDragMove(float f, float f2, int i);

        void onDragStart(float f, float f2, int i);
    }

    public TrafficDetailView(Context context) {
        super(context);
        init(context);
    }

    public TrafficDetailView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public TrafficDetailView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void dividerX() {
        if (this.mXValues == null) {
            this.mXValueTexts = new String[6];
            this.mXValues = new long[6];
        }
        long[] jArr = this.mXValues;
        long j = this.mMaxValue;
        jArr[5] = j;
        jArr[4] = (4 * j) / 5;
        jArr[3] = (3 * j) / 5;
        jArr[2] = (2 * j) / 5;
        jArr[1] = j / 5;
        jArr[0] = 0;
        long formatMaxBytes = FormatBytesUtil.formatMaxBytes(j);
        this.mXTextMaxWidth = this.mDensity * 38.0f;
        for (int i = 0; i < 6; i++) {
            this.mXValueTexts[i] = FormatBytesUtil.formatUniteUnit(getContext(), this.mXValues[i], formatMaxBytes);
            float measureText = this.mTextPaint.measureText(this.mXValueTexts[i]);
            if (measureText > this.mXTextMaxWidth) {
                this.mXTextMaxWidth = measureText;
            }
        }
    }

    private static long getMaxValue(long[] jArr) {
        if (jArr == null) {
            return 0;
        }
        int length = jArr.length;
        long j = jArr[0];
        for (int i = 1; i < length; i++) {
            if (jArr[i] > j) {
                j = jArr[i];
            }
        }
        return j;
    }

    private void init(Context context) {
        this.mMonthMaxDay = DateUtil.getActualMaxDayOfMonth();
        this.mTextPaint = new Paint(1);
        setTextColor(1711276032);
        setTextSize(12);
        this.mDashPaint = new Paint();
        this.mDashPaint.setStyle(Paint.Style.FILL);
        this.mDashPaint.setColor(1727987712);
        this.mDashPaint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f, 5.0f, 5.0f}, 1.0f));
        this.mFillPaint = new Paint();
        this.mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mFillPaint.setColor(-5185281);
        this.mHighLightPaint = new Paint();
        this.mHighLightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mHighLightPaint.setColor(-11551745);
        this.mDensity = getResources().getDisplayMetrics().density;
        this.mTopMargin = this.mDensity * 12.0f;
        this.mPoints = new float[31];
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        float f = this.mDensity;
        this.mYAxisMargin = f * 3.0f;
        this.mXAxisMargin = f * 3.0f;
    }

    private int rectContains(float f, float f2) {
        int length = this.mPoints.length;
        for (int i = 0; i < length; i++) {
            float f3 = f - this.mPoints[i];
            if (f3 >= 0.0f && f3 <= this.mPlasWidth) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Paint paint;
        float f;
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        float f2 = this.mXTextMaxWidth + this.mYAxisMargin;
        float f3 = ((((float) height) - this.mTextHeight) - this.mXAxisMargin) - 2.0f;
        float f4 = (float) width;
        canvas.drawLine(f2, f3, f4, f3, this.mDashPaint);
        if (this.mData != null) {
            if (this.mType == 0) {
                canvas2.drawText("0:00", f2, (float) (height - 2), this.mTextPaint);
            } else {
                canvas2.drawText(this.mStartTimeTxt, f2, (float) (height - 2), this.mTextPaint);
            }
            float f5 = (f3 - this.mTopMargin) / 5.0f;
            Paint.Align textAlign = this.mTextPaint.getTextAlign();
            this.mTextPaint.setTextAlign(Paint.Align.RIGHT);
            float f6 = f3;
            int i = 0;
            while (i < 5) {
                canvas2.drawText(this.mXValueTexts[i], f2 - this.mYAxisMargin, (this.mTextHeight / 2.0f) + f6, this.mTextPaint);
                float f7 = f6 - f5;
                canvas.drawLine(f2, f7, f4, f7, this.mDashPaint);
                i++;
                f6 = f7;
            }
            canvas2.drawText(this.mXValueTexts[5], f2 - this.mYAxisMargin, f6 + (this.mTextHeight / 2.0f), this.mTextPaint);
            this.mTextPaint.setTextAlign(textAlign);
            int i2 = this.mType == 0 ? 24 : this.mMonthMaxDay;
            float f8 = ((float) ((((double) width) * 1.0d) - ((double) f2))) / ((float) (i2 * 3));
            float f9 = f3 - this.mTopMargin;
            int length = this.mData.length;
            float f10 = f8 * 3.0f;
            this.mPlasWidth = f8 * 2.0f;
            float f11 = f2;
            int i3 = 0;
            while (i3 < length) {
                long j = this.mData[i3];
                if (j > 0) {
                    float f12 = (((float) j) * f9) / ((float) this.mMaxValue);
                    if (f12 <= 1.0f) {
                        f12 = 1.0f;
                    }
                    float f13 = f3 - f12;
                    if (i3 == this.mTouch) {
                        f = f11 + this.mPlasWidth;
                        paint = this.mHighLightPaint;
                    } else {
                        f = f11 + this.mPlasWidth;
                        paint = this.mFillPaint;
                    }
                    canvas.drawRect(f11, f13, f, f3, paint);
                    if (this.invalid) {
                        this.mPoints[i3] = f11;
                    }
                } else if (this.invalid) {
                    this.mPoints[i3] = f11;
                }
                f11 += f10;
                i3++;
            }
            Paint.Align textAlign2 = this.mTextPaint.getTextAlign();
            this.mTextPaint.setTextAlign(Paint.Align.RIGHT);
            if (this.mType == 0) {
                canvas2.drawText("24:00", f4, (float) (height - 2), this.mTextPaint);
            } else {
                if (f11 > f4) {
                    f11 = f4;
                }
                canvas2.drawText(this.mEndTimeTxt, f11, (float) (height - 2), this.mTextPaint);
            }
            this.mTextPaint.setTextAlign(textAlign2);
            if (this.invalid) {
                for (int i4 = i3; i4 < i2; i4++) {
                    this.mPoints[i3] = f11;
                    f11 += f10;
                }
                this.invalid = false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onStartTrackingTouch() {
        this.mIsDragging = true;
    }

    /* access modifiers changed from: package-private */
    public void onStopTrackingTouch() {
        this.mIsDragging = false;
        this.mTouch = -1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0042, code lost:
        if (r0 != 3) goto L_0x0168;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            int[] r0 = r7.mLocation
            r1 = 2
            if (r0 != 0) goto L_0x000e
            int[] r0 = new int[r1]
            r7.mLocation = r0
            int[] r0 = r7.mLocation
            r7.getLocationOnScreen(r0)
        L_0x000e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "X : "
            r0.append(r2)
            int[] r2 = r7.mLocation
            r3 = 0
            r2 = r2[r3]
            r0.append(r2)
            java.lang.String r2 = " ,Y :"
            r0.append(r2)
            int[] r2 = r7.mLocation
            r4 = 1
            r2 = r2[r4]
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "TrafficDetailView"
            android.util.Log.i(r2, r0)
            int r0 = r8.getAction()
            r5 = -1
            if (r0 == 0) goto L_0x00e7
            if (r0 == r4) goto L_0x00d8
            r6 = 3
            if (r0 == r1) goto L_0x0046
            if (r0 == r6) goto L_0x00d8
            goto L_0x0168
        L_0x0046:
            java.lang.String r0 = "MotionEvent.ACTION_MOVE"
            android.util.Log.i(r2, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "getX "
            r0.append(r1)
            float r1 = r8.getX()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r2, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "getY "
            r0.append(r1)
            float r1 = r8.getY()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r2, r0)
            float r0 = r8.getX()
            int[] r1 = r7.mLocation
            r1 = r1[r3]
            float r1 = (float) r1
            float r0 = r0 - r1
            float r8 = r8.getY()
            int[] r1 = r7.mLocation
            r1 = r1[r4]
            float r1 = (float) r1
            float r8 = r8 - r1
            boolean r1 = r7.mIsDragging
            if (r1 == 0) goto L_0x00ae
            int r8 = r7.rectContains(r0, r8)
            int r1 = r7.mTouch
            if (r8 == r1) goto L_0x0168
            if (r8 == r5) goto L_0x0168
            r7.mTouch = r8
            com.miui.networkassistant.ui.view.TrafficDetailView$ChartDragListener r8 = r7.mChartDragListener
            if (r8 == 0) goto L_0x00e2
            int[] r1 = r7.mLocation
            r1 = r1[r4]
            float r1 = (float) r1
            int r2 = r7.mTouch
            r8.onDragMove(r0, r1, r2)
            goto L_0x00e2
        L_0x00ae:
            float r1 = r7.mTouchDownX
            float r1 = r0 - r1
            float r1 = java.lang.Math.abs(r1)
            int r2 = r7.mScaledTouchSlop
            int r2 = r2 / r6
            float r2 = (float) r2
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x0168
            r7.onStartTrackingTouch()
            int r8 = r7.rectContains(r0, r8)
            r7.mTouch = r8
            int r8 = r7.mTouch
            if (r8 == r5) goto L_0x0168
            com.miui.networkassistant.ui.view.TrafficDetailView$ChartDragListener r1 = r7.mChartDragListener
            if (r1 == 0) goto L_0x00e2
            int[] r2 = r7.mLocation
            r2 = r2[r4]
            float r2 = (float) r2
            r1.onDragStart(r0, r2, r8)
            goto L_0x00e2
        L_0x00d8:
            r7.onStopTrackingTouch()
            com.miui.networkassistant.ui.view.TrafficDetailView$ChartDragListener r8 = r7.mChartDragListener
            if (r8 == 0) goto L_0x00e2
            r8.onDragEnd()
        L_0x00e2:
            r7.invalidate()
            goto L_0x0168
        L_0x00e7:
            java.lang.String r0 = "MotionEvent.ACTION_DOWN"
            android.util.Log.i(r2, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "getRawX "
            r0.append(r1)
            float r1 = r8.getRawX()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r2, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "getRawY "
            r0.append(r1)
            float r1 = r8.getRawY()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r2, r0)
            float r0 = r8.getRawX()
            int[] r1 = r7.mLocation
            r1 = r1[r3]
            float r1 = (float) r1
            float r0 = r0 - r1
            r7.mTouchDownX = r0
            float r8 = r8.getRawY()
            int[] r0 = r7.mLocation
            r0 = r0[r4]
            float r0 = (float) r0
            float r8 = r8 - r0
            r7.mTouchDownY = r8
            float r8 = r7.mTouchDownX
            float r0 = r7.mTouchDownY
            int r8 = r7.rectContains(r8, r0)
            r7.mTouch = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "onTouchEvent mTouch "
            r8.append(r0)
            int r0 = r7.mTouch
            r8.append(r0)
            java.lang.String r8 = r8.toString()
            android.util.Log.d(r2, r8)
            int r8 = r7.mTouch
            if (r8 == r5) goto L_0x0168
            com.miui.networkassistant.ui.view.TrafficDetailView$ChartDragListener r0 = r7.mChartDragListener
            if (r0 == 0) goto L_0x00e2
            float r1 = r7.mTouchDownX
            int[] r2 = r7.mLocation
            r2 = r2[r4]
            float r2 = (float) r2
            r0.onDragStart(r1, r2, r8)
            goto L_0x00e2
        L_0x0168:
            android.view.ViewParent r8 = r7.getParent()
            boolean r0 = r7.mIsDragging
            r8.requestDisallowInterceptTouchEvent(r0)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.view.TrafficDetailView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setChartDragListener(ChartDragListener chartDragListener) {
        this.mChartDragListener = chartDragListener;
    }

    public void setData(long[] jArr, int i) {
        this.mData = jArr;
        this.mType = i;
        this.mMaxValue = getMaxValue(this.mData) + 500;
        dividerX();
        this.invalid = true;
        invalidate();
    }

    public void setDurations(long j, long j2) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        this.mStartTimeTxt = String.format("%d-%d", new Object[]{Integer.valueOf(instance.get(2) + 1), Integer.valueOf(instance.get(5))});
        instance.setTimeInMillis(j2);
        this.mEndTimeTxt = String.format("%d-%d", new Object[]{Integer.valueOf(instance.get(2) + 1), Integer.valueOf(instance.get(5))});
        invalidate();
    }

    public void setTextColor(int i) {
        if (i != this.mTextColor) {
            this.mTextColor = i;
            this.mTextPaint.setColor(this.mTextColor);
        }
    }

    public void setTextSize(int i) {
        if (this.mTextSize != i) {
            this.mTextSize = i;
            Context context = getContext();
            this.mTextPaint.setTextSize((float) ((int) TypedValue.applyDimension(2, (float) i, (context == null ? Resources.getSystem() : context.getResources()).getDisplayMetrics())));
            Paint.FontMetricsInt fontMetricsInt = this.mTextPaint.getFontMetricsInt();
            this.mTextHeight = (float) (Math.abs(fontMetricsInt.ascent) - (fontMetricsInt.ascent - fontMetricsInt.top));
        }
    }
}
