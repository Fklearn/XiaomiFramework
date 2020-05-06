package com.miui.networkassistant.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.securitycenter.R;
import java.util.Calendar;

public class AppDetailTrafficView extends View {
    public static final int DAY_TYPE = 1;
    private static final int DEFAULT_TEXT_SIZE = 12;
    public static final int HOUR_TYPE = 0;
    private static final int LINE_X_HOUR = 24;
    private static final int LINE_Y = 6;
    private static final int PLAS_PERCENT = 4;
    private static final int PLAS_SPACE_PERCENT = 3;
    private static final int PLAS_TOTAL = 7;
    private static final String TAG = "AppDetailTrafficView";
    private static final String TEXT_END = "24:00";
    private static final String TEXT_START = "0:00";
    private static final int TOP_MARGIN = 12;
    private static final int X_AXIS_MARGIN = 15;
    private static final int X_AXIS_TEXT_Y_OFFSET = 2;
    private static final int Y_AXIS_MARGIN = 18;
    private float height;
    float lastX;
    private ChartDragListener mChartDragListener;
    private Paint mDashPaint;
    private long[] mData;
    private float[] mDataHeight;
    private float mDensity;
    private String mEndTimeTxt;
    private Paint mFillPaint;
    private int mFocus = -1;
    private Paint mHighLightPaint;
    private long mMaxValue;
    private float mRoundRectSize;
    private String mStartTimeTxt;
    private int mTextColor;
    private float mTextHeight;
    private Paint mTextPaint;
    private int mTextSize;
    private float mTextWidth;
    private float mTopMargin;
    private int mType;
    private float mXAxisMargin;
    private float mXTextMaxWidth;
    private String[] mXValueTexts;
    private long[] mXValues;
    private float mYAxisMargin;
    private float plasWidth;
    private float stepWidth;
    private float textY;
    private float validHeight;
    private float width;
    private float x;
    private int xLineSize = 24;
    private float xStep;
    private float y;
    private float yStep;

    public interface ChartDragListener {
        void onDragEnd();

        void onDragStart(float f, float f2, int i);
    }

    public AppDetailTrafficView(Context context) {
        super(context);
        init();
    }

    public AppDetailTrafficView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public AppDetailTrafficView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void checkFocus(float f, float f2, float f3, float f4) {
        float f5 = f - this.x;
        int i = 0;
        while (i < this.xLineSize) {
            float f6 = (float) i;
            float f7 = this.stepWidth;
            if (f5 <= f6 * f7 || f5 >= ((float) (i + 1)) * f7) {
                i++;
            } else if (this.mFocus != i) {
                this.mFocus = i;
                invalidate();
                ChartDragListener chartDragListener = this.mChartDragListener;
                if (chartDragListener != null) {
                    float f8 = this.x;
                    float f9 = this.stepWidth;
                    chartDragListener.onDragStart(f8 + (f6 * f9) + f9 + (this.mType == 0 ? 0.0f : this.plasWidth / 2.0f), ((f4 - f2) + this.height) - this.mDataHeight[i], i);
                    return;
                }
                return;
            } else {
                return;
            }
        }
        invalidate();
    }

    private void dividerX() {
        if (this.mXValues == null) {
            this.mXValueTexts = new String[7];
            this.mXValues = new long[7];
        }
        int i = 0;
        while (true) {
            long[] jArr = this.mXValues;
            if (i >= jArr.length) {
                break;
            }
            jArr[i] = (((long) i) * this.mMaxValue) / ((long) (jArr.length - 1));
            i++;
        }
        this.mXTextMaxWidth = this.mDensity * 38.0f;
        for (int i2 = 0; i2 < this.mXValueTexts.length; i2++) {
            long j = this.mMaxValue;
            this.mXValueTexts[i2] = String.format("%.01f", new Object[]{Float.valueOf(((((float) (((long) i2) * j)) * 1.0f) / ((float) FormatBytesUtil.formatMaxBytes(j))) / ((float) (this.mXValues.length - 1)))});
            float measureText = this.mTextPaint.measureText(this.mXValueTexts[i2]);
            if (measureText > this.mXTextMaxWidth) {
                this.mXTextMaxWidth = measureText;
            }
        }
    }

    private void drawAxes(Canvas canvas) {
        float f = this.x;
        float f2 = this.y;
        canvas.drawLine(f, f2, this.width, f2, this.mDashPaint);
        if (this.mType == 0) {
            canvas.drawText(TEXT_START, this.x, this.textY, this.mTextPaint);
            canvas.drawText(TEXT_END, this.width, this.textY, this.mTextPaint);
        } else if (this.mData != null) {
            canvas.drawText(this.mStartTimeTxt, this.x, this.textY, this.mTextPaint);
            canvas.drawText(this.mEndTimeTxt, this.x + (((float) (this.mData.length - 1)) * this.stepWidth), this.textY, this.mTextPaint);
        }
        for (int i = 0; i <= 6; i++) {
            float f3 = this.y - (((float) i) * this.yStep);
            if (i != 0) {
                canvas.drawLine(this.x, f3, this.width, f3, this.mDashPaint);
            }
            String[] strArr = this.mXValueTexts;
            if (strArr != null) {
                canvas.drawText(strArr[i], this.x - this.mYAxisMargin, (f3 + (this.mTextHeight / 2.0f)) - (this.mDensity * 2.0f), this.mTextPaint);
            }
        }
    }

    private void drawBarChart(Canvas canvas) {
        if (this.mData != null) {
            int i = 0;
            while (true) {
                long[] jArr = this.mData;
                if (i < jArr.length) {
                    if (jArr[i] > 0) {
                        float[] fArr = this.mDataHeight;
                        fArr[i] = (((float) jArr[i]) * this.validHeight) / ((float) this.mMaxValue);
                        float f = 1.0f;
                        if (fArr[i] > 1.0f) {
                            f = fArr[i];
                        }
                        fArr[i] = f;
                        float f2 = this.x + (((float) i) * this.stepWidth);
                        int i2 = this.mFocus;
                        Paint paint = (i == i2 || i2 == -1) ? this.mFillPaint : this.mHighLightPaint;
                        float f3 = this.y;
                        float f4 = f3 - this.mDataHeight[i];
                        float f5 = f2 + this.plasWidth;
                        float f6 = this.mRoundRectSize;
                        float f7 = f2;
                        canvas.drawRoundRect(f7, f4, f5, f3, f6, f6, paint);
                        float f8 = this.y;
                        canvas.drawRect(f7, f8 - this.mRoundRectSize, f2 + this.plasWidth, f8, paint);
                    }
                    i++;
                } else {
                    return;
                }
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

    private void init() {
        this.mTextPaint = new Paint(1);
        setTextColor(getResources().getColor(R.color.na_traffic_dotted_line));
        setTextSize(12);
        this.mTextPaint.setTextAlign(Paint.Align.RIGHT);
        this.mTextWidth = this.mTextPaint.measureText(TEXT_START);
        this.mDashPaint = new Paint();
        this.mDashPaint.setStyle(Paint.Style.STROKE);
        this.mDashPaint.setColor(getResources().getColor(R.color.na_traffic_dotted_line));
        this.mDashPaint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f, 5.0f, 5.0f}, 1.0f));
        this.mFillPaint = new Paint();
        this.mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mFillPaint.setColor(-16737793);
        this.mFillPaint.setAntiAlias(true);
        this.mHighLightPaint = new Paint();
        this.mHighLightPaint.setAntiAlias(true);
        this.mHighLightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mHighLightPaint.setColor(855677439);
        this.mDensity = getResources().getDisplayMetrics().density;
        float f = this.mDensity;
        this.mTopMargin = 12.0f * f;
        this.mRoundRectSize = 2.0f * f;
        this.mYAxisMargin = 18.0f * f;
        this.mXAxisMargin = f * 15.0f;
    }

    private void initValue() {
        this.x = this.mXTextMaxWidth + this.mYAxisMargin;
        float f = this.height;
        this.y = ((f - this.mTextHeight) - this.mXAxisMargin) - 2.0f;
        this.textY = f - 2.0f;
        float f2 = this.y;
        float f3 = this.mTopMargin;
        this.yStep = (f2 - f3) / 6.0f;
        this.xStep = (this.width - this.x) / ((float) (this.xLineSize * 7));
        this.validHeight = f2 - f3;
        float f4 = this.xStep;
        this.stepWidth = 7.0f * f4;
        this.plasWidth = f4 * 4.0f;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAxes(canvas);
        drawBarChart(canvas);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.width = (float) getMeasuredWidth();
        this.height = (float) getMeasuredHeight();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000d, code lost:
        if (r0 != 3) goto L_0x0051;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            int r0 = r5.getAction()
            r1 = 1
            if (r0 == 0) goto L_0x0038
            if (r0 == r1) goto L_0x002d
            r2 = 2
            if (r0 == r2) goto L_0x0010
            r5 = 3
            if (r0 == r5) goto L_0x002d
            goto L_0x0051
        L_0x0010:
            float r0 = r4.lastX
            float r2 = r5.getX()
            float r0 = r0 - r2
            float r0 = java.lang.Math.abs(r0)
            r2 = 1092616192(0x41200000, float:10.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0051
            android.view.ViewParent r0 = r4.getParent()
            android.view.ViewParent r0 = r0.getParent()
            r0.requestDisallowInterceptTouchEvent(r1)
            goto L_0x003e
        L_0x002d:
            r5 = -1
            r4.mFocus = r5
            com.miui.networkassistant.ui.view.AppDetailTrafficView$ChartDragListener r5 = r4.mChartDragListener
            if (r5 == 0) goto L_0x0051
            r5.onDragEnd()
            goto L_0x0051
        L_0x0038:
            float r0 = r5.getX()
            r4.lastX = r0
        L_0x003e:
            float r0 = r5.getX()
            float r2 = r5.getY()
            float r3 = r5.getRawX()
            float r5 = r5.getRawY()
            r4.checkFocus(r0, r2, r3, r5)
        L_0x0051:
            r4.invalidate()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.view.AppDetailTrafficView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setChartDragListener(ChartDragListener chartDragListener) {
        this.mChartDragListener = chartDragListener;
    }

    public void setData(long[] jArr, int i) {
        if (jArr == null) {
            Log.e(TAG, "AppDetailTrafficView.java: data is null");
            return;
        }
        this.mData = jArr;
        this.mDataHeight = new float[jArr.length];
        this.mType = i;
        this.xLineSize = this.mType == 0 ? 24 : jArr.length;
        this.mMaxValue = getMaxValue(this.mData);
        dividerX();
        initValue();
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
