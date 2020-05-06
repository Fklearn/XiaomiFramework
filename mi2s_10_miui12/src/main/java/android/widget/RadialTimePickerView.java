package android.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.IntArray;
import android.util.Log;
import android.util.MathUtils;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.internal.R.styleable;
import com.android.internal.widget.ExploreByTouchHelper;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Locale;

public class RadialTimePickerView
  extends View
{
  private static final int AM = 0;
  private static final int ANIM_DURATION_NORMAL = 500;
  private static final int ANIM_DURATION_TOUCH = 60;
  private static final float[] COS_30;
  private static final int DEGREES_FOR_ONE_HOUR = 30;
  private static final int DEGREES_FOR_ONE_MINUTE = 6;
  public static final int HOURS = 0;
  private static final int HOURS_INNER = 2;
  private static final int HOURS_IN_CIRCLE = 12;
  private static final int[] HOURS_NUMBERS = { 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
  private static final int[] HOURS_NUMBERS_24 = { 0, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
  public static final int MINUTES = 1;
  private static final int MINUTES_IN_CIRCLE = 60;
  private static final int[] MINUTES_NUMBERS = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55 };
  private static final int MISSING_COLOR = -65281;
  private static final int NUM_POSITIONS = 12;
  private static final int PM = 1;
  private static final int SELECTOR_CIRCLE = 0;
  private static final int SELECTOR_DOT = 1;
  private static final int SELECTOR_LINE = 2;
  private static final float[] SIN_30;
  private static final int[] SNAP_PREFER_30S_MAP = new int['ũ'];
  private static final String TAG = "RadialTimePickerView";
  private final FloatProperty<RadialTimePickerView> HOURS_TO_MINUTES = new FloatProperty("hoursToMinutes")
  {
    public Float get(RadialTimePickerView paramAnonymousRadialTimePickerView)
    {
      return Float.valueOf(paramAnonymousRadialTimePickerView.mHoursToMinutes);
    }
    
    public void setValue(RadialTimePickerView paramAnonymousRadialTimePickerView, float paramAnonymousFloat)
    {
      RadialTimePickerView.access$002(paramAnonymousRadialTimePickerView, paramAnonymousFloat);
      paramAnonymousRadialTimePickerView.invalidate();
    }
  };
  private int mAmOrPm;
  private int mCenterDotRadius;
  boolean mChangedDuringTouch = false;
  private int mCircleRadius;
  private float mDisabledAlpha;
  private int mHalfwayDist;
  private final String[] mHours12Texts = new String[12];
  private float mHoursToMinutes;
  private ObjectAnimator mHoursToMinutesAnimator;
  private final String[] mInnerHours24Texts = new String[12];
  private String[] mInnerTextHours;
  private final float[] mInnerTextX = new float[12];
  private final float[] mInnerTextY = new float[12];
  private boolean mInputEnabled = true;
  private boolean mIs24HourMode;
  private boolean mIsOnInnerCircle;
  private OnValueSelectedListener mListener;
  private int mMaxDistForOuterNumber;
  private int mMinDistForInnerNumber;
  private String[] mMinutesText;
  private final String[] mMinutesTexts = new String[12];
  private final String[] mOuterHours24Texts = new String[12];
  private String[] mOuterTextHours;
  private final float[][] mOuterTextX = new float[2][12];
  private final float[][] mOuterTextY = new float[2][12];
  private final Paint[] mPaint = new Paint[2];
  private final Paint mPaintBackground = new Paint();
  private final Paint mPaintCenter = new Paint();
  private final Paint[] mPaintSelector = new Paint[3];
  private final int[] mSelectionDegrees = new int[2];
  private int mSelectorColor;
  private int mSelectorDotColor;
  private int mSelectorDotRadius;
  private final Path mSelectorPath = new Path();
  private int mSelectorRadius;
  private int mSelectorStroke;
  private boolean mShowHours;
  private final ColorStateList[] mTextColor = new ColorStateList[3];
  private final int[] mTextInset = new int[3];
  private final int[] mTextSize = new int[3];
  private final RadialPickerTouchHelper mTouchHelper;
  private final Typeface mTypeface;
  private int mXCenter;
  private int mYCenter;
  
  static
  {
    COS_30 = new float[12];
    SIN_30 = new float[12];
    preparePrefer30sMap();
    double d = 1.5707963267948966D;
    for (int i = 0; i < 12; i++)
    {
      COS_30[i] = ((float)Math.cos(d));
      SIN_30[i] = ((float)Math.sin(d));
      d += 0.5235987755982988D;
    }
  }
  
  public RadialTimePickerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RadialTimePickerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843933);
  }
  
  public RadialTimePickerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RadialTimePickerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet);
    applyAttributes(paramAttributeSet, paramInt1, paramInt2);
    paramAttributeSet = new TypedValue();
    paramContext.getTheme().resolveAttribute(16842803, paramAttributeSet, true);
    this.mDisabledAlpha = paramAttributeSet.getFloat();
    this.mTypeface = Typeface.create("sans-serif", 0);
    this.mPaint[0] = new Paint();
    this.mPaint[0].setAntiAlias(true);
    this.mPaint[0].setTextAlign(Paint.Align.CENTER);
    this.mPaint[1] = new Paint();
    this.mPaint[1].setAntiAlias(true);
    this.mPaint[1].setTextAlign(Paint.Align.CENTER);
    this.mPaintCenter.setAntiAlias(true);
    this.mPaintSelector[0] = new Paint();
    this.mPaintSelector[0].setAntiAlias(true);
    this.mPaintSelector[1] = new Paint();
    this.mPaintSelector[1].setAntiAlias(true);
    this.mPaintSelector[2] = new Paint();
    this.mPaintSelector[2].setAntiAlias(true);
    this.mPaintSelector[2].setStrokeWidth(2.0F);
    this.mPaintBackground.setAntiAlias(true);
    paramContext = getResources();
    this.mSelectorRadius = paramContext.getDimensionPixelSize(17105516);
    this.mSelectorStroke = paramContext.getDimensionPixelSize(17105517);
    this.mSelectorDotRadius = paramContext.getDimensionPixelSize(17105515);
    this.mCenterDotRadius = paramContext.getDimensionPixelSize(17105507);
    this.mTextSize[0] = paramContext.getDimensionPixelSize(17105522);
    this.mTextSize[1] = paramContext.getDimensionPixelSize(17105522);
    this.mTextSize[2] = paramContext.getDimensionPixelSize(17105521);
    this.mTextInset[0] = paramContext.getDimensionPixelSize(17105520);
    this.mTextInset[1] = paramContext.getDimensionPixelSize(17105520);
    this.mTextInset[2] = paramContext.getDimensionPixelSize(17105519);
    this.mShowHours = true;
    this.mHoursToMinutes = 0.0F;
    this.mIs24HourMode = false;
    this.mAmOrPm = 0;
    this.mTouchHelper = new RadialPickerTouchHelper();
    setAccessibilityDelegate(this.mTouchHelper);
    if (getImportantForAccessibility() == 0) {
      setImportantForAccessibility(1);
    }
    initHoursAndMinutesText();
    initData();
    paramContext = Calendar.getInstance(Locale.getDefault());
    paramInt2 = paramContext.get(11);
    paramInt1 = paramContext.get(12);
    setCurrentHourInternal(paramInt2, false, false);
    setCurrentMinuteInternal(paramInt1, false);
    setHapticFeedbackEnabled(true);
  }
  
  private void animatePicker(boolean paramBoolean, long paramLong)
  {
    float f;
    if (paramBoolean) {
      f = 0.0F;
    } else {
      f = 1.0F;
    }
    if (this.mHoursToMinutes == f)
    {
      ObjectAnimator localObjectAnimator = this.mHoursToMinutesAnimator;
      if ((localObjectAnimator != null) && (localObjectAnimator.isStarted()))
      {
        this.mHoursToMinutesAnimator.cancel();
        this.mHoursToMinutesAnimator = null;
      }
      return;
    }
    this.mHoursToMinutesAnimator = ObjectAnimator.ofFloat(this, this.HOURS_TO_MINUTES, new float[] { f });
    this.mHoursToMinutesAnimator.setAutoCancel(true);
    this.mHoursToMinutesAnimator.setDuration(paramLong);
    this.mHoursToMinutesAnimator.start();
  }
  
  private static void calculatePositions(Paint paramPaint, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    paramPaint.setTextSize(paramFloat4);
    paramFloat4 = (paramPaint.descent() + paramPaint.ascent()) / 2.0F;
    for (int i = 0; i < 12; i++)
    {
      paramArrayOfFloat1[i] = (paramFloat2 - COS_30[i] * paramFloat1);
      paramArrayOfFloat2[i] = (paramFloat3 - paramFloat4 - SIN_30[i] * paramFloat1);
    }
  }
  
  private void calculatePositionsHours()
  {
    float f = this.mCircleRadius - this.mTextInset[0];
    calculatePositions(this.mPaint[0], f, this.mXCenter, this.mYCenter, this.mTextSize[0], this.mOuterTextX[0], this.mOuterTextY[0]);
    if (this.mIs24HourMode)
    {
      int i = this.mCircleRadius;
      int j = this.mTextInset[2];
      calculatePositions(this.mPaint[0], i - j, this.mXCenter, this.mYCenter, this.mTextSize[2], this.mInnerTextX, this.mInnerTextY);
    }
  }
  
  private void calculatePositionsMinutes()
  {
    float f = this.mCircleRadius - this.mTextInset[1];
    calculatePositions(this.mPaint[1], f, this.mXCenter, this.mYCenter, this.mTextSize[1], this.mOuterTextX[1], this.mOuterTextY[1]);
  }
  
  private void drawCenter(Canvas paramCanvas, float paramFloat)
  {
    this.mPaintCenter.setAlpha((int)(255.0F * paramFloat + 0.5F));
    paramCanvas.drawCircle(this.mXCenter, this.mYCenter, this.mCenterDotRadius, this.mPaintCenter);
  }
  
  private void drawCircleBackground(Canvas paramCanvas)
  {
    paramCanvas.drawCircle(this.mXCenter, this.mYCenter, this.mCircleRadius, this.mPaintBackground);
  }
  
  private void drawHours(Canvas paramCanvas, Path paramPath, float paramFloat)
  {
    int i = (int)((1.0F - this.mHoursToMinutes) * 255.0F * paramFloat + 0.5F);
    if (i > 0)
    {
      paramCanvas.save(2);
      paramCanvas.clipPath(paramPath, Region.Op.DIFFERENCE);
      drawHoursClipped(paramCanvas, i, false);
      paramCanvas.restore();
      paramCanvas.save(2);
      paramCanvas.clipPath(paramPath, Region.Op.INTERSECT);
      drawHoursClipped(paramCanvas, i, true);
      paramCanvas.restore();
    }
  }
  
  private void drawHoursClipped(Canvas paramCanvas, int paramInt, boolean paramBoolean)
  {
    float f = this.mTextSize[0];
    Typeface localTypeface = this.mTypeface;
    Object localObject1 = this.mTextColor[0];
    Object localObject2 = this.mOuterTextHours;
    Object localObject3 = this.mOuterTextX[0];
    Object localObject4 = this.mOuterTextY[0];
    Paint localPaint = this.mPaint[0];
    boolean bool;
    if ((paramBoolean) && (!this.mIsOnInnerCircle)) {
      bool = true;
    } else {
      bool = false;
    }
    drawTextElements(paramCanvas, f, localTypeface, (ColorStateList)localObject1, (String[])localObject2, (float[])localObject3, (float[])localObject4, localPaint, paramInt, bool, this.mSelectionDegrees[0], paramBoolean);
    if (this.mIs24HourMode)
    {
      localObject3 = this.mInnerTextHours;
      if (localObject3 != null)
      {
        f = this.mTextSize[2];
        localTypeface = this.mTypeface;
        localObject4 = this.mTextColor[2];
        localObject1 = this.mInnerTextX;
        localObject2 = this.mInnerTextY;
        localPaint = this.mPaint[0];
        if ((paramBoolean) && (this.mIsOnInnerCircle)) {
          bool = true;
        } else {
          bool = false;
        }
        drawTextElements(paramCanvas, f, localTypeface, (ColorStateList)localObject4, (String[])localObject3, (float[])localObject1, (float[])localObject2, localPaint, paramInt, bool, this.mSelectionDegrees[0], paramBoolean);
      }
    }
  }
  
  private void drawMinutes(Canvas paramCanvas, Path paramPath, float paramFloat)
  {
    int i = (int)(this.mHoursToMinutes * 255.0F * paramFloat + 0.5F);
    if (i > 0)
    {
      paramCanvas.save(2);
      paramCanvas.clipPath(paramPath, Region.Op.DIFFERENCE);
      drawMinutesClipped(paramCanvas, i, false);
      paramCanvas.restore();
      paramCanvas.save(2);
      paramCanvas.clipPath(paramPath, Region.Op.INTERSECT);
      drawMinutesClipped(paramCanvas, i, true);
      paramCanvas.restore();
    }
  }
  
  private void drawMinutesClipped(Canvas paramCanvas, int paramInt, boolean paramBoolean)
  {
    drawTextElements(paramCanvas, this.mTextSize[1], this.mTypeface, this.mTextColor[1], this.mMinutesText, this.mOuterTextX[1], this.mOuterTextY[1], this.mPaint[1], paramInt, paramBoolean, this.mSelectionDegrees[1], paramBoolean);
  }
  
  private void drawSelector(Canvas paramCanvas, Path paramPath)
  {
    if (this.mIsOnInnerCircle) {
      i = 2;
    } else {
      i = 0;
    }
    int j = this.mTextInset[i];
    Object localObject = this.mSelectionDegrees;
    int k = localObject[(i % 2)];
    int i = localObject[(i % 2)];
    float f1 = 1.0F;
    if (i % 30 != 0) {
      f2 = 1.0F;
    } else {
      f2 = 0.0F;
    }
    int m = this.mTextInset[1];
    localObject = this.mSelectionDegrees;
    int n = localObject[1];
    if (localObject[1] % 30 == 0) {
      f1 = 0.0F;
    }
    i = this.mSelectorRadius;
    float f3 = this.mCircleRadius - MathUtils.lerp(j, m, this.mHoursToMinutes);
    double d1 = Math.toRadians(MathUtils.lerpDeg(k, n, this.mHoursToMinutes));
    float f4 = this.mXCenter + (float)Math.sin(d1) * f3;
    float f5 = this.mYCenter - (float)Math.cos(d1) * f3;
    localObject = this.mPaintSelector[0];
    ((Paint)localObject).setColor(this.mSelectorColor);
    paramCanvas.drawCircle(f4, f5, i, (Paint)localObject);
    if (paramPath != null)
    {
      paramPath.reset();
      paramPath.addCircle(f4, f5, i, Path.Direction.CCW);
    }
    float f2 = MathUtils.lerp(f2, f1, this.mHoursToMinutes);
    if (f2 > 0.0F)
    {
      paramPath = this.mPaintSelector[1];
      paramPath.setColor(this.mSelectorDotColor);
      paramCanvas.drawCircle(f4, f5, this.mSelectorDotRadius * f2, paramPath);
    }
    double d2 = Math.sin(d1);
    d1 = Math.cos(d1);
    f1 = f3 - i;
    i = this.mXCenter;
    m = this.mCenterDotRadius;
    j = (int)(m * d2);
    k = this.mYCenter;
    m = (int)(m * d1);
    f2 = (int)(f1 * d2) + (i + j);
    f1 = k - m - (int)(f1 * d1);
    paramPath = this.mPaintSelector[2];
    paramPath.setColor(this.mSelectorColor);
    paramPath.setStrokeWidth(this.mSelectorStroke);
    paramCanvas.drawLine(this.mXCenter, this.mYCenter, f2, f1, paramPath);
  }
  
  private void drawTextElements(Canvas paramCanvas, float paramFloat, Typeface paramTypeface, ColorStateList paramColorStateList, String[] paramArrayOfString, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, Paint paramPaint, int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    paramPaint.setTextSize(paramFloat);
    paramPaint.setTypeface(paramTypeface);
    paramFloat = paramInt2 / 30.0F;
    int i = (int)paramFloat;
    int j = (int)Math.ceil(paramFloat);
    for (paramInt2 = 0; paramInt2 < 12; paramInt2++)
    {
      int k;
      if ((i != paramInt2) && (j % 12 != paramInt2)) {
        k = 0;
      } else {
        k = 1;
      }
      if ((!paramBoolean2) || (k != 0))
      {
        if ((paramBoolean1) && (k != 0)) {
          k = 32;
        } else {
          k = 0;
        }
        k = paramColorStateList.getColorForState(StateSet.get(0x8 | k), 0);
        paramPaint.setColor(k);
        paramPaint.setAlpha(getMultipliedAlpha(k, paramInt1));
        paramCanvas.drawText(paramArrayOfString[paramInt2], paramArrayOfFloat1[paramInt2], paramArrayOfFloat2[paramInt2], paramPaint);
      }
    }
  }
  
  private int getDegreesForHour(int paramInt)
  {
    int i;
    if (this.mIs24HourMode)
    {
      i = paramInt;
      if (paramInt >= 12) {
        i = paramInt - 12;
      }
    }
    else
    {
      i = paramInt;
      if (paramInt == 12) {
        i = 0;
      }
    }
    return i * 30;
  }
  
  private int getDegreesForMinute(int paramInt)
  {
    return paramInt * 6;
  }
  
  private int getDegreesFromXY(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    int i;
    int j;
    if ((this.mIs24HourMode) && (this.mShowHours))
    {
      i = this.mMinDistForInnerNumber;
      j = this.mMaxDistForOuterNumber;
    }
    else
    {
      boolean bool = this.mShowHours;
      int k = this.mCircleRadius - this.mTextInset[(bool ^ true)];
      i = this.mSelectorRadius;
      j = k + i;
      i = k - i;
    }
    double d1 = paramFloat1 - this.mXCenter;
    double d2 = paramFloat2 - this.mYCenter;
    double d3 = Math.sqrt(d1 * d1 + d2 * d2);
    if ((d3 >= i) && ((!paramBoolean) || (d3 <= j)))
    {
      i = (int)(Math.toDegrees(Math.atan2(d2, d1) + 1.5707963267948966D) + 0.5D);
      if (i < 0) {
        return i + 360;
      }
      return i;
    }
    return -1;
  }
  
  private int getHourForDegrees(int paramInt, boolean paramBoolean)
  {
    int i = paramInt / 30 % 12;
    if (this.mIs24HourMode)
    {
      if ((!paramBoolean) && (i == 0))
      {
        paramInt = 12;
      }
      else
      {
        paramInt = i;
        if (paramBoolean)
        {
          paramInt = i;
          if (i != 0) {
            paramInt = i + 12;
          }
        }
      }
    }
    else
    {
      paramInt = i;
      if (this.mAmOrPm == 1) {
        paramInt = i + 12;
      }
    }
    return paramInt;
  }
  
  private boolean getInnerCircleForHour(int paramInt)
  {
    boolean bool;
    if ((this.mIs24HourMode) && ((paramInt == 0) || (paramInt > 12))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean getInnerCircleFromXY(float paramFloat1, float paramFloat2)
  {
    boolean bool1 = this.mIs24HourMode;
    boolean bool2 = false;
    if ((bool1) && (this.mShowHours))
    {
      double d1 = paramFloat1 - this.mXCenter;
      double d2 = paramFloat2 - this.mYCenter;
      if (Math.sqrt(d1 * d1 + d2 * d2) <= this.mHalfwayDist) {
        bool2 = true;
      }
      return bool2;
    }
    return false;
  }
  
  private int getMinuteForDegrees(int paramInt)
  {
    return paramInt / 6;
  }
  
  private int getMultipliedAlpha(int paramInt1, int paramInt2)
  {
    return (int)(Color.alpha(paramInt1) * (paramInt2 / 255.0D) + 0.5D);
  }
  
  private boolean handleTouchInput(float paramFloat1, float paramFloat2, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = getInnerCircleFromXY(paramFloat1, paramFloat2);
    int i = getDegreesFromXY(paramFloat1, paramFloat2, false);
    if (i == -1) {
      return false;
    }
    animatePicker(this.mShowHours, 60L);
    int j;
    int k;
    if (this.mShowHours)
    {
      j = snapOnly30s(i, 0) % 360;
      if ((this.mIsOnInnerCircle == bool) && (this.mSelectionDegrees[0] == j)) {
        i = 0;
      } else {
        i = 1;
      }
      this.mIsOnInnerCircle = bool;
      this.mSelectionDegrees[0] = j;
      k = 0;
      j = getCurrentHour();
    }
    else
    {
      j = snapPrefer30s(i) % 360;
      if (this.mSelectionDegrees[1] != j) {
        i = 1;
      } else {
        i = 0;
      }
      this.mSelectionDegrees[1] = j;
      k = 1;
      j = getCurrentMinute();
    }
    if ((i == 0) && (!paramBoolean1) && (!paramBoolean2)) {
      return false;
    }
    OnValueSelectedListener localOnValueSelectedListener = this.mListener;
    if (localOnValueSelectedListener != null) {
      localOnValueSelectedListener.onValueSelected(k, j, paramBoolean2);
    }
    if ((i != 0) || (paramBoolean1))
    {
      performHapticFeedback(4);
      invalidate();
    }
    return true;
  }
  
  private void initData()
  {
    if (this.mIs24HourMode)
    {
      this.mOuterTextHours = this.mOuterHours24Texts;
      this.mInnerTextHours = this.mInnerHours24Texts;
    }
    else
    {
      String[] arrayOfString = this.mHours12Texts;
      this.mOuterTextHours = arrayOfString;
      this.mInnerTextHours = arrayOfString;
    }
    this.mMinutesText = this.mMinutesTexts;
  }
  
  private void initHoursAndMinutesText()
  {
    for (int i = 0; i < 12; i++)
    {
      this.mHours12Texts[i] = String.format("%d", new Object[] { Integer.valueOf(HOURS_NUMBERS[i]) });
      this.mInnerHours24Texts[i] = String.format("%02d", new Object[] { Integer.valueOf(HOURS_NUMBERS_24[i]) });
      this.mOuterHours24Texts[i] = String.format("%d", new Object[] { Integer.valueOf(HOURS_NUMBERS[i]) });
      this.mMinutesTexts[i] = String.format("%02d", new Object[] { Integer.valueOf(MINUTES_NUMBERS[i]) });
    }
  }
  
  private static void preparePrefer30sMap()
  {
    int i = 0;
    int j = 1;
    int k = 8;
    for (int m = 0; m < 361; m++)
    {
      SNAP_PREFER_30S_MAP[m] = i;
      if (j == k)
      {
        i += 6;
        if (i == 360) {
          k = 7;
        } else if (i % 30 == 0) {
          k = 14;
        } else {
          k = 4;
        }
        j = 1;
      }
      else
      {
        j++;
      }
    }
  }
  
  private void setCurrentHourInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mSelectionDegrees[0] = (paramInt % 12 * 30);
    int i;
    if ((paramInt != 0) && (paramInt % 24 >= 12)) {
      i = 1;
    } else {
      i = 0;
    }
    boolean bool = getInnerCircleForHour(paramInt);
    if ((this.mAmOrPm != i) || (this.mIsOnInnerCircle != bool))
    {
      this.mAmOrPm = i;
      this.mIsOnInnerCircle = bool;
      initData();
      this.mTouchHelper.invalidateRoot();
    }
    invalidate();
    if (paramBoolean1)
    {
      OnValueSelectedListener localOnValueSelectedListener = this.mListener;
      if (localOnValueSelectedListener != null) {
        localOnValueSelectedListener.onValueSelected(0, paramInt, paramBoolean2);
      }
    }
  }
  
  private void setCurrentMinuteInternal(int paramInt, boolean paramBoolean)
  {
    this.mSelectionDegrees[1] = (paramInt % 60 * 6);
    invalidate();
    if (paramBoolean)
    {
      OnValueSelectedListener localOnValueSelectedListener = this.mListener;
      if (localOnValueSelectedListener != null) {
        localOnValueSelectedListener.onValueSelected(1, paramInt, false);
      }
    }
  }
  
  private void showPicker(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mShowHours == paramBoolean1) {
      return;
    }
    this.mShowHours = paramBoolean1;
    if (paramBoolean2)
    {
      animatePicker(paramBoolean1, 500L);
    }
    else
    {
      ObjectAnimator localObjectAnimator = this.mHoursToMinutesAnimator;
      if ((localObjectAnimator != null) && (localObjectAnimator.isStarted()))
      {
        this.mHoursToMinutesAnimator.cancel();
        this.mHoursToMinutesAnimator = null;
      }
      float f;
      if (paramBoolean1) {
        f = 0.0F;
      } else {
        f = 1.0F;
      }
      this.mHoursToMinutes = f;
    }
    initData();
    invalidate();
    this.mTouchHelper.invalidateRoot();
  }
  
  private static int snapOnly30s(int paramInt1, int paramInt2)
  {
    int i = paramInt1 / 30 * 30;
    int j = i + 30;
    if (paramInt2 == 1)
    {
      paramInt1 = j;
    }
    else if (paramInt2 == -1)
    {
      paramInt2 = i;
      if (paramInt1 == i) {
        paramInt2 = i - 30;
      }
      paramInt1 = paramInt2;
    }
    else if (paramInt1 - i < j - paramInt1)
    {
      paramInt1 = i;
    }
    else
    {
      paramInt1 = j;
    }
    return paramInt1;
  }
  
  private static int snapPrefer30s(int paramInt)
  {
    int[] arrayOfInt = SNAP_PREFER_30S_MAP;
    if (arrayOfInt == null) {
      return -1;
    }
    return arrayOfInt[paramInt];
  }
  
  void applyAttributes(AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    Context localContext = getContext();
    TypedArray localTypedArray = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.TimePicker, paramInt1, paramInt2);
    saveAttributeDataForStyleable(localContext, R.styleable.TimePicker, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramAttributeSet = localTypedArray.getColorStateList(3);
    ColorStateList localColorStateList = localTypedArray.getColorStateList(9);
    ColorStateList[] arrayOfColorStateList = this.mTextColor;
    if (paramAttributeSet == null) {
      paramAttributeSet = ColorStateList.valueOf(-65281);
    }
    arrayOfColorStateList[0] = paramAttributeSet;
    arrayOfColorStateList = this.mTextColor;
    if (localColorStateList == null) {
      paramAttributeSet = ColorStateList.valueOf(-65281);
    } else {
      paramAttributeSet = localColorStateList;
    }
    arrayOfColorStateList[2] = paramAttributeSet;
    paramAttributeSet = this.mTextColor;
    paramAttributeSet[1] = paramAttributeSet[0];
    paramAttributeSet = localTypedArray.getColorStateList(5);
    if (paramAttributeSet != null) {
      paramInt1 = paramAttributeSet.getColorForState(StateSet.get(40), 0);
    } else {
      paramInt1 = -65281;
    }
    this.mPaintCenter.setColor(paramInt1);
    paramAttributeSet = StateSet.get(40);
    this.mSelectorColor = paramInt1;
    this.mSelectorDotColor = this.mTextColor[0].getColorForState(paramAttributeSet, 0);
    this.mPaintBackground.setColor(localTypedArray.getColor(4, localContext.getColor(17171025)));
    localTypedArray.recycle();
  }
  
  public boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    if (this.mTouchHelper.dispatchHoverEvent(paramMotionEvent)) {
      return true;
    }
    return super.dispatchHoverEvent(paramMotionEvent);
  }
  
  public int getAmOrPm()
  {
    return this.mAmOrPm;
  }
  
  public int getCurrentHour()
  {
    return getHourForDegrees(this.mSelectionDegrees[0], this.mIsOnInnerCircle);
  }
  
  public int getCurrentItemShowing()
  {
    return this.mShowHours ^ true;
  }
  
  public int getCurrentMinute()
  {
    return getMinuteForDegrees(this.mSelectionDegrees[1]);
  }
  
  public void initialize(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (this.mIs24HourMode != paramBoolean)
    {
      this.mIs24HourMode = paramBoolean;
      initData();
    }
    setCurrentHourInternal(paramInt1, false, false);
    setCurrentMinuteInternal(paramInt2, false);
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    float f;
    if (this.mInputEnabled) {
      f = 1.0F;
    } else {
      f = this.mDisabledAlpha;
    }
    drawCircleBackground(paramCanvas);
    Path localPath = this.mSelectorPath;
    drawSelector(paramCanvas, localPath);
    drawHours(paramCanvas, localPath, f);
    drawMinutes(paramCanvas, localPath, f);
    drawCenter(paramCanvas, f);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!paramBoolean) {
      return;
    }
    this.mXCenter = (getWidth() / 2);
    this.mYCenter = (getHeight() / 2);
    this.mCircleRadius = Math.min(this.mXCenter, this.mYCenter);
    paramInt1 = this.mCircleRadius;
    int[] arrayOfInt = this.mTextInset;
    paramInt2 = arrayOfInt[2];
    paramInt3 = this.mSelectorRadius;
    this.mMinDistForInnerNumber = (paramInt1 - paramInt2 - paramInt3);
    this.mMaxDistForOuterNumber = (paramInt1 - arrayOfInt[0] + paramInt3);
    this.mHalfwayDist = (paramInt1 - (arrayOfInt[0] + arrayOfInt[2]) / 2);
    calculatePositionsHours();
    calculatePositionsMinutes();
    this.mTouchHelper.invalidateRoot();
  }
  
  public PointerIcon onResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt)
  {
    if (!isEnabled()) {
      return null;
    }
    if (getDegreesFromXY(paramMotionEvent.getX(), paramMotionEvent.getY(), false) != -1) {
      return PointerIcon.getSystemIcon(getContext(), 1002);
    }
    return super.onResolvePointerIcon(paramMotionEvent, paramInt);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mInputEnabled) {
      return true;
    }
    int i = paramMotionEvent.getActionMasked();
    if ((i == 2) || (i == 1) || (i == 0))
    {
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3;
      if (i == 0)
      {
        this.mChangedDuringTouch = false;
        bool3 = bool1;
      }
      else
      {
        bool3 = bool1;
        if (i == 1)
        {
          boolean bool4 = true;
          bool3 = bool1;
          bool2 = bool4;
          if (!this.mChangedDuringTouch)
          {
            bool3 = true;
            bool2 = bool4;
          }
        }
      }
      this.mChangedDuringTouch |= handleTouchInput(paramMotionEvent.getX(), paramMotionEvent.getY(), bool3, bool2);
    }
    return true;
  }
  
  public boolean setAmOrPm(int paramInt)
  {
    if ((this.mAmOrPm != paramInt) && (!this.mIs24HourMode))
    {
      this.mAmOrPm = paramInt;
      invalidate();
      this.mTouchHelper.invalidateRoot();
      return true;
    }
    return false;
  }
  
  public void setCurrentHour(int paramInt)
  {
    setCurrentHourInternal(paramInt, true, false);
  }
  
  public void setCurrentItemShowing(int paramInt, boolean paramBoolean)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("ClockView does not support showing item ");
        localStringBuilder.append(paramInt);
        Log.e("RadialTimePickerView", localStringBuilder.toString());
      }
      else
      {
        showMinutes(paramBoolean);
      }
    }
    else {
      showHours(paramBoolean);
    }
  }
  
  public void setCurrentMinute(int paramInt)
  {
    setCurrentMinuteInternal(paramInt, true);
  }
  
  public void setInputEnabled(boolean paramBoolean)
  {
    this.mInputEnabled = paramBoolean;
    invalidate();
  }
  
  public void setOnValueSelectedListener(OnValueSelectedListener paramOnValueSelectedListener)
  {
    this.mListener = paramOnValueSelectedListener;
  }
  
  public void showHours(boolean paramBoolean)
  {
    showPicker(true, paramBoolean);
  }
  
  public void showMinutes(boolean paramBoolean)
  {
    showPicker(false, paramBoolean);
  }
  
  static abstract interface OnValueSelectedListener
  {
    public abstract void onValueSelected(int paramInt1, int paramInt2, boolean paramBoolean);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  static @interface PickerType {}
  
  private class RadialPickerTouchHelper
    extends ExploreByTouchHelper
  {
    private final int MASK_TYPE = 15;
    private final int MASK_VALUE = 255;
    private final int MINUTE_INCREMENT = 5;
    private final int SHIFT_TYPE = 0;
    private final int SHIFT_VALUE = 8;
    private final int TYPE_HOUR = 1;
    private final int TYPE_MINUTE = 2;
    private final Rect mTempRect = new Rect();
    
    public RadialPickerTouchHelper()
    {
      super();
    }
    
    private void adjustPicker(int paramInt)
    {
      int i;
      int j;
      int k;
      int m;
      if (RadialTimePickerView.this.mShowHours)
      {
        i = 1;
        j = RadialTimePickerView.this.getCurrentHour();
        if (RadialTimePickerView.this.mIs24HourMode)
        {
          k = 0;
          m = 23;
        }
        else
        {
          j = hour24To12(j);
          k = 1;
          m = 12;
        }
      }
      else
      {
        i = 5;
        j = RadialTimePickerView.this.getCurrentMinute() / 5;
        k = 0;
        m = 55;
      }
      paramInt = MathUtils.constrain((j + paramInt) * i, k, m);
      if (RadialTimePickerView.this.mShowHours) {
        RadialTimePickerView.this.setCurrentHour(paramInt);
      } else {
        RadialTimePickerView.this.setCurrentMinute(paramInt);
      }
    }
    
    private void getBoundsForVirtualView(int paramInt, Rect paramRect)
    {
      int i = getTypeFromId(paramInt);
      paramInt = getValueFromId(paramInt);
      float f2;
      if (i == 1)
      {
        if (RadialTimePickerView.this.getInnerCircleForHour(paramInt))
        {
          f1 = RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[2];
          f2 = RadialTimePickerView.this.mSelectorRadius;
        }
        else
        {
          f1 = RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[0];
          f2 = RadialTimePickerView.this.mSelectorRadius;
        }
        f3 = RadialTimePickerView.this.getDegreesForHour(paramInt);
      }
      else if (i == 2)
      {
        f1 = RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[1];
        f3 = RadialTimePickerView.this.getDegreesForMinute(paramInt);
        f2 = RadialTimePickerView.this.mSelectorRadius;
      }
      else
      {
        f1 = 0.0F;
        f3 = 0.0F;
        f2 = 0.0F;
      }
      double d = Math.toRadians(f3);
      float f3 = RadialTimePickerView.this.mXCenter + (float)Math.sin(d) * f1;
      float f1 = RadialTimePickerView.this.mYCenter - (float)Math.cos(d) * f1;
      paramRect.set((int)(f3 - f2), (int)(f1 - f2), (int)(f3 + f2), (int)(f1 + f2));
    }
    
    private int getCircularDiff(int paramInt1, int paramInt2, int paramInt3)
    {
      paramInt1 = Math.abs(paramInt1 - paramInt2);
      if (paramInt1 > paramInt3 / 2) {
        paramInt1 = paramInt3 - paramInt1;
      }
      return paramInt1;
    }
    
    private int getTypeFromId(int paramInt)
    {
      return paramInt >>> 0 & 0xF;
    }
    
    private int getValueFromId(int paramInt)
    {
      return paramInt >>> 8 & 0xFF;
    }
    
    private CharSequence getVirtualViewDescription(int paramInt1, int paramInt2)
    {
      String str;
      if ((paramInt1 != 1) && (paramInt1 != 2)) {
        str = null;
      } else {
        str = Integer.toString(paramInt2);
      }
      return str;
    }
    
    private int getVirtualViewIdAfter(int paramInt1, int paramInt2)
    {
      int i;
      if (paramInt1 == 1)
      {
        i = paramInt2 + 1;
        if (RadialTimePickerView.this.mIs24HourMode) {
          paramInt2 = 23;
        } else {
          paramInt2 = 12;
        }
        if (i <= paramInt2) {
          return makeId(paramInt1, i);
        }
      }
      else if (paramInt1 == 2)
      {
        int j = RadialTimePickerView.this.getCurrentMinute();
        i = paramInt2 - paramInt2 % 5 + 5;
        if ((paramInt2 < j) && (i > j)) {
          return makeId(paramInt1, j);
        }
        if (i < 60) {
          return makeId(paramInt1, i);
        }
      }
      return Integer.MIN_VALUE;
    }
    
    private int hour12To24(int paramInt1, int paramInt2)
    {
      int i = paramInt1;
      if (paramInt1 == 12)
      {
        paramInt1 = i;
        if (paramInt2 == 0) {
          paramInt1 = 0;
        }
      }
      else
      {
        paramInt1 = i;
        if (paramInt2 == 1) {
          paramInt1 = i + 12;
        }
      }
      return paramInt1;
    }
    
    private int hour24To12(int paramInt)
    {
      if (paramInt == 0) {
        return 12;
      }
      if (paramInt > 12) {
        return paramInt - 12;
      }
      return paramInt;
    }
    
    private boolean isVirtualViewSelected(int paramInt1, int paramInt2)
    {
      boolean bool1 = false;
      boolean bool2 = false;
      if (paramInt1 == 1)
      {
        if (RadialTimePickerView.this.getCurrentHour() == paramInt2) {
          bool2 = true;
        }
      }
      else if (paramInt1 == 2)
      {
        bool2 = bool1;
        if (RadialTimePickerView.this.getCurrentMinute() == paramInt2) {
          bool2 = true;
        }
      }
      else
      {
        bool2 = false;
      }
      return bool2;
    }
    
    private int makeId(int paramInt1, int paramInt2)
    {
      return paramInt1 << 0 | paramInt2 << 8;
    }
    
    protected int getVirtualViewAt(float paramFloat1, float paramFloat2)
    {
      int i = RadialTimePickerView.this.getDegreesFromXY(paramFloat1, paramFloat2, true);
      int k;
      if (i != -1)
      {
        int j = RadialTimePickerView.snapOnly30s(i, 0) % 360;
        if (RadialTimePickerView.this.mShowHours)
        {
          boolean bool = RadialTimePickerView.this.getInnerCircleFromXY(paramFloat1, paramFloat2);
          k = RadialTimePickerView.this.getHourForDegrees(j, bool);
          if (!RadialTimePickerView.this.mIs24HourMode) {
            k = hour24To12(k);
          }
          k = makeId(1, k);
        }
        else
        {
          k = RadialTimePickerView.this.getCurrentMinute();
          i = RadialTimePickerView.this.getMinuteForDegrees(i);
          j = RadialTimePickerView.this.getMinuteForDegrees(j);
          if (getCircularDiff(k, i, 60) >= getCircularDiff(j, i, 60)) {
            k = j;
          }
          k = makeId(2, k);
        }
      }
      else
      {
        k = Integer.MIN_VALUE;
      }
      return k;
    }
    
    protected void getVisibleVirtualViews(IntArray paramIntArray)
    {
      int i;
      int j;
      if (RadialTimePickerView.this.mShowHours)
      {
        boolean bool = RadialTimePickerView.this.mIs24HourMode;
        if (RadialTimePickerView.this.mIs24HourMode) {
          i = 23;
        } else {
          i = 12;
        }
        for (j = bool ^ true; j <= i; j++) {
          paramIntArray.add(makeId(1, j));
        }
      }
      else
      {
        j = RadialTimePickerView.this.getCurrentMinute();
        for (i = 0; i < 60; i += 5)
        {
          paramIntArray.add(makeId(2, i));
          if ((j > i) && (j < i + 5)) {
            paramIntArray.add(makeId(2, j));
          }
        }
      }
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
    }
    
    protected boolean onPerformActionForVirtualView(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      if (paramInt2 == 16)
      {
        paramInt2 = getTypeFromId(paramInt1);
        paramInt1 = getValueFromId(paramInt1);
        if (paramInt2 == 1)
        {
          if (!RadialTimePickerView.this.mIs24HourMode) {
            paramInt1 = hour12To24(paramInt1, RadialTimePickerView.this.mAmOrPm);
          }
          RadialTimePickerView.this.setCurrentHour(paramInt1);
          return true;
        }
        if (paramInt2 == 2)
        {
          RadialTimePickerView.this.setCurrentMinute(paramInt1);
          return true;
        }
      }
      return false;
    }
    
    protected void onPopulateEventForVirtualView(int paramInt, AccessibilityEvent paramAccessibilityEvent)
    {
      paramAccessibilityEvent.setClassName(getClass().getName());
      paramAccessibilityEvent.setContentDescription(getVirtualViewDescription(getTypeFromId(paramInt), getValueFromId(paramInt)));
    }
    
    protected void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      paramAccessibilityNodeInfo.setClassName(getClass().getName());
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
      int i = getTypeFromId(paramInt);
      int j = getValueFromId(paramInt);
      paramAccessibilityNodeInfo.setContentDescription(getVirtualViewDescription(i, j));
      getBoundsForVirtualView(paramInt, this.mTempRect);
      paramAccessibilityNodeInfo.setBoundsInParent(this.mTempRect);
      paramAccessibilityNodeInfo.setSelected(isVirtualViewSelected(i, j));
      paramInt = getVirtualViewIdAfter(i, j);
      if (paramInt != Integer.MIN_VALUE) {
        paramAccessibilityNodeInfo.setTraversalBefore(RadialTimePickerView.this, paramInt);
      }
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if (super.performAccessibilityAction(paramView, paramInt, paramBundle)) {
        return true;
      }
      if (paramInt != 4096)
      {
        if (paramInt != 8192) {
          return false;
        }
        adjustPicker(-1);
        return true;
      }
      adjustPicker(1);
      return true;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RadialTimePickerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */