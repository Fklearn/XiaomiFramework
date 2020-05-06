package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R.styleable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import libcore.icu.LocaleData;

public class NumberPicker
  extends LinearLayout
{
  private static final int DEFAULT_LAYOUT_RESOURCE_ID = 17367238;
  private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300L;
  private static final char[] DIGIT_CHARACTERS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641, 1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785, 2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415, 2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543, 3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311 };
  private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
  private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
  @UnsupportedAppUsage
  private static final int SELECTOR_MIDDLE_ITEM_INDEX = 1;
  @UnsupportedAppUsage
  private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
  private static final int SIZE_UNSPECIFIED = -1;
  private static final int SNAP_SCROLL_DURATION = 300;
  private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9F;
  private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
  private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
  private static final TwoDigitFormatter sTwoDigitFormatter = new TwoDigitFormatter();
  private AccessibilityNodeProviderImpl mAccessibilityNodeProvider;
  private final Scroller mAdjustScroller;
  private BeginSoftInputOnLongPressCommand mBeginSoftInputOnLongPressCommand;
  private int mBottomSelectionDividerBottom;
  private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
  private final boolean mComputeMaxWidth;
  private int mCurrentScrollOffset;
  private final ImageButton mDecrementButton;
  private boolean mDecrementVirtualButtonPressed;
  private String[] mDisplayedValues;
  @UnsupportedAppUsage
  private final Scroller mFlingScroller;
  private Formatter mFormatter;
  private final boolean mHasSelectorWheel;
  private boolean mHideWheelUntilFocused;
  private boolean mIgnoreMoveEvents;
  private final ImageButton mIncrementButton;
  private boolean mIncrementVirtualButtonPressed;
  private int mInitialScrollOffset = Integer.MIN_VALUE;
  @UnsupportedAppUsage
  private final EditText mInputText;
  private long mLastDownEventTime;
  private float mLastDownEventY;
  private float mLastDownOrMoveEventY;
  private int mLastHandledDownDpadKeyCode = -1;
  private int mLastHoveredChildVirtualViewId;
  private long mLongPressUpdateInterval = 300L;
  private final int mMaxHeight;
  @UnsupportedAppUsage
  private int mMaxValue;
  private int mMaxWidth;
  @UnsupportedAppUsage
  private int mMaximumFlingVelocity;
  @UnsupportedAppUsage
  private final int mMinHeight;
  private int mMinValue;
  @UnsupportedAppUsage
  private final int mMinWidth;
  private int mMinimumFlingVelocity;
  private OnScrollListener mOnScrollListener;
  @UnsupportedAppUsage
  private OnValueChangeListener mOnValueChangeListener;
  private boolean mPerformClickOnTap;
  private final PressedStateHelper mPressedStateHelper;
  private int mPreviousScrollerY;
  private int mScrollState = 0;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private final Drawable mSelectionDivider;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private int mSelectionDividerHeight;
  private final int mSelectionDividersDistance;
  private int mSelectorElementHeight;
  private final SparseArray<String> mSelectorIndexToStringCache = new SparseArray();
  @UnsupportedAppUsage
  private final int[] mSelectorIndices = new int[3];
  private int mSelectorTextGapHeight;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private final Paint mSelectorWheelPaint;
  private SetSelectionCommand mSetSelectionCommand;
  private final int mSolidColor;
  @UnsupportedAppUsage
  private final int mTextSize;
  private int mTopSelectionDividerTop;
  private int mTouchSlop;
  private int mValue;
  private VelocityTracker mVelocityTracker;
  private final Drawable mVirtualButtonPressedDrawable;
  private boolean mWrapSelectorWheel;
  private boolean mWrapSelectorWheelPreferred = true;
  
  public NumberPicker(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NumberPicker(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16844068);
  }
  
  public NumberPicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public NumberPicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    Object localObject = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.NumberPicker, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.NumberPicker, paramAttributeSet, (TypedArray)localObject, paramInt1, paramInt2);
    paramInt1 = ((TypedArray)localObject).getResourceId(3, 17367238);
    boolean bool;
    if (paramInt1 != 17367238) {
      bool = true;
    } else {
      bool = false;
    }
    this.mHasSelectorWheel = bool;
    this.mHideWheelUntilFocused = ((TypedArray)localObject).getBoolean(2, false);
    this.mSolidColor = ((TypedArray)localObject).getColor(0, 0);
    paramAttributeSet = ((TypedArray)localObject).getDrawable(8);
    if (paramAttributeSet != null)
    {
      paramAttributeSet.setCallback(this);
      paramAttributeSet.setLayoutDirection(getLayoutDirection());
      if (paramAttributeSet.isStateful()) {
        paramAttributeSet.setState(getDrawableState());
      }
    }
    this.mSelectionDivider = paramAttributeSet;
    this.mSelectionDividerHeight = ((TypedArray)localObject).getDimensionPixelSize(1, (int)TypedValue.applyDimension(1, 2.0F, getResources().getDisplayMetrics()));
    this.mSelectionDividersDistance = ((TypedArray)localObject).getDimensionPixelSize(9, (int)TypedValue.applyDimension(1, 48.0F, getResources().getDisplayMetrics()));
    this.mMinHeight = ((TypedArray)localObject).getDimensionPixelSize(6, -1);
    this.mMaxHeight = ((TypedArray)localObject).getDimensionPixelSize(4, -1);
    int i = this.mMinHeight;
    if (i != -1)
    {
      paramInt2 = this.mMaxHeight;
      if ((paramInt2 != -1) && (i > paramInt2)) {
        throw new IllegalArgumentException("minHeight > maxHeight");
      }
    }
    this.mMinWidth = ((TypedArray)localObject).getDimensionPixelSize(7, -1);
    this.mMaxWidth = ((TypedArray)localObject).getDimensionPixelSize(5, -1);
    paramInt2 = this.mMinWidth;
    if (paramInt2 != -1)
    {
      i = this.mMaxWidth;
      if ((i != -1) && (paramInt2 > i)) {
        throw new IllegalArgumentException("minWidth > maxWidth");
      }
    }
    if (this.mMaxWidth == -1) {
      bool = true;
    } else {
      bool = false;
    }
    this.mComputeMaxWidth = bool;
    this.mVirtualButtonPressedDrawable = ((TypedArray)localObject).getDrawable(10);
    ((TypedArray)localObject).recycle();
    this.mPressedStateHelper = new PressedStateHelper();
    setWillNotDraw(this.mHasSelectorWheel ^ true);
    ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(paramInt1, this, true);
    localObject = new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        NumberPicker.this.hideSoftInput();
        NumberPicker.this.mInputText.clearFocus();
        if (paramAnonymousView.getId() == 16909030) {
          NumberPicker.this.changeValueByOne(true);
        } else {
          NumberPicker.this.changeValueByOne(false);
        }
      }
    };
    paramAttributeSet = new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        NumberPicker.this.hideSoftInput();
        NumberPicker.this.mInputText.clearFocus();
        if (paramAnonymousView.getId() == 16909030) {
          NumberPicker.this.postChangeCurrentByOneFromLongPress(true, 0L);
        } else {
          NumberPicker.this.postChangeCurrentByOneFromLongPress(false, 0L);
        }
        return true;
      }
    };
    if (!this.mHasSelectorWheel)
    {
      this.mIncrementButton = ((ImageButton)findViewById(16909030));
      this.mIncrementButton.setOnClickListener((View.OnClickListener)localObject);
      this.mIncrementButton.setOnLongClickListener(paramAttributeSet);
    }
    else
    {
      this.mIncrementButton = null;
    }
    if (!this.mHasSelectorWheel)
    {
      this.mDecrementButton = ((ImageButton)findViewById(16908881));
      this.mDecrementButton.setOnClickListener((View.OnClickListener)localObject);
      this.mDecrementButton.setOnLongClickListener(paramAttributeSet);
    }
    else
    {
      this.mDecrementButton = null;
    }
    this.mInputText = ((EditText)findViewById(16909214));
    this.mInputText.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          NumberPicker.this.mInputText.selectAll();
        }
        else
        {
          NumberPicker.this.mInputText.setSelection(0, 0);
          NumberPicker.this.validateInputTextView(paramAnonymousView);
        }
      }
    });
    this.mInputText.setFilters(new InputFilter[] { new InputTextFilter() });
    this.mInputText.setAccessibilityLiveRegion(1);
    this.mInputText.setRawInputType(2);
    this.mInputText.setImeOptions(6);
    paramContext = ViewConfiguration.get(paramContext);
    this.mTouchSlop = paramContext.getScaledTouchSlop();
    this.mMinimumFlingVelocity = paramContext.getScaledMinimumFlingVelocity();
    this.mMaximumFlingVelocity = (paramContext.getScaledMaximumFlingVelocity() / 8);
    this.mTextSize = ((int)this.mInputText.getTextSize());
    paramContext = new Paint();
    paramContext.setAntiAlias(true);
    paramContext.setTextAlign(Paint.Align.CENTER);
    paramContext.setTextSize(this.mTextSize);
    paramContext.setTypeface(this.mInputText.getTypeface());
    paramContext.setColor(this.mInputText.getTextColors().getColorForState(ENABLED_STATE_SET, -1));
    this.mSelectorWheelPaint = paramContext;
    this.mFlingScroller = new Scroller(getContext(), null, true);
    this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5F));
    updateInputTextView();
    if (getImportantForAccessibility() == 0) {
      setImportantForAccessibility(1);
    }
    if (getFocusable() == 16)
    {
      setFocusable(1);
      setFocusableInTouchMode(true);
    }
  }
  
  @UnsupportedAppUsage
  private void changeValueByOne(boolean paramBoolean)
  {
    if (this.mHasSelectorWheel)
    {
      hideSoftInput();
      if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
        moveToFinalScrollerPosition(this.mAdjustScroller);
      }
      this.mPreviousScrollerY = 0;
      if (paramBoolean) {
        this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, 300);
      } else {
        this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, 300);
      }
      invalidate();
    }
    else if (paramBoolean)
    {
      setValueInternal(this.mValue + 1, true);
    }
    else
    {
      setValueInternal(this.mValue - 1, true);
    }
  }
  
  private void decrementSelectorIndices(int[] paramArrayOfInt)
  {
    for (int i = paramArrayOfInt.length - 1; i > 0; i--) {
      paramArrayOfInt[i] = paramArrayOfInt[(i - 1)];
    }
    int j = paramArrayOfInt[1] - 1;
    i = j;
    if (this.mWrapSelectorWheel)
    {
      i = j;
      if (j < this.mMinValue) {
        i = this.mMaxValue;
      }
    }
    paramArrayOfInt[0] = i;
    ensureCachedScrollSelectorValue(i);
  }
  
  private void ensureCachedScrollSelectorValue(int paramInt)
  {
    SparseArray localSparseArray = this.mSelectorIndexToStringCache;
    if ((String)localSparseArray.get(paramInt) != null) {
      return;
    }
    int i = this.mMinValue;
    Object localObject;
    if ((paramInt >= i) && (paramInt <= this.mMaxValue))
    {
      localObject = this.mDisplayedValues;
      if (localObject != null) {
        localObject = localObject[(paramInt - i)];
      } else {
        localObject = formatNumber(paramInt);
      }
    }
    else
    {
      localObject = "";
    }
    localSparseArray.put(paramInt, localObject);
  }
  
  private boolean ensureScrollWheelAdjusted()
  {
    int i = this.mInitialScrollOffset - this.mCurrentScrollOffset;
    if (i != 0)
    {
      this.mPreviousScrollerY = 0;
      int j = Math.abs(i);
      int k = this.mSelectorElementHeight;
      int m = i;
      if (j > k / 2)
      {
        m = k;
        if (i > 0) {
          m = -k;
        }
        m = i + m;
      }
      this.mAdjustScroller.startScroll(0, 0, 0, m, 800);
      invalidate();
      return true;
    }
    return false;
  }
  
  private void fling(int paramInt)
  {
    this.mPreviousScrollerY = 0;
    if (paramInt > 0) {
      this.mFlingScroller.fling(0, 0, 0, paramInt, 0, 0, 0, Integer.MAX_VALUE);
    } else {
      this.mFlingScroller.fling(0, Integer.MAX_VALUE, 0, paramInt, 0, 0, 0, Integer.MAX_VALUE);
    }
    invalidate();
  }
  
  private String formatNumber(int paramInt)
  {
    Object localObject = this.mFormatter;
    if (localObject != null) {
      localObject = ((Formatter)localObject).format(paramInt);
    } else {
      localObject = formatNumberWithLocale(paramInt);
    }
    return (String)localObject;
  }
  
  private static String formatNumberWithLocale(int paramInt)
  {
    return String.format(Locale.getDefault(), "%d", new Object[] { Integer.valueOf(paramInt) });
  }
  
  private int getSelectedPos(String paramString)
  {
    int i;
    if (this.mDisplayedValues == null)
    {
      try
      {
        i = Integer.parseInt(paramString);
        return i;
      }
      catch (NumberFormatException paramString) {}
    }
    else
    {
      for (i = 0; i < this.mDisplayedValues.length; i++)
      {
        paramString = paramString.toLowerCase();
        if (this.mDisplayedValues[i].toLowerCase().startsWith(paramString)) {
          return this.mMinValue + i;
        }
      }
      try
      {
        i = Integer.parseInt(paramString);
        return i;
      }
      catch (NumberFormatException paramString) {}
    }
    return this.mMinValue;
  }
  
  @UnsupportedAppUsage
  public static final Formatter getTwoDigitFormatter()
  {
    return sTwoDigitFormatter;
  }
  
  private int getWrappedSelectorIndex(int paramInt)
  {
    int i = this.mMaxValue;
    if (paramInt > i)
    {
      j = this.mMinValue;
      return j + (paramInt - i) % (i - j) - 1;
    }
    int j = this.mMinValue;
    if (paramInt < j) {
      return i - (j - paramInt) % (i - j) + 1;
    }
    return paramInt;
  }
  
  private void hideSoftInput()
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
    if ((localInputMethodManager != null) && (localInputMethodManager.isActive(this.mInputText))) {
      localInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }
    if (this.mHasSelectorWheel) {
      this.mInputText.setVisibility(4);
    }
  }
  
  private void incrementSelectorIndices(int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramArrayOfInt.length - 1; i++) {
      paramArrayOfInt[i] = paramArrayOfInt[(i + 1)];
    }
    int j = paramArrayOfInt[(paramArrayOfInt.length - 2)] + 1;
    i = j;
    if (this.mWrapSelectorWheel)
    {
      i = j;
      if (j > this.mMaxValue) {
        i = this.mMinValue;
      }
    }
    paramArrayOfInt[(paramArrayOfInt.length - 1)] = i;
    ensureCachedScrollSelectorValue(i);
  }
  
  private void initializeFadingEdges()
  {
    setVerticalFadingEdgeEnabled(true);
    setFadingEdgeLength((this.mBottom - this.mTop - this.mTextSize) / 2);
  }
  
  private void initializeSelectorWheel()
  {
    initializeSelectorWheelIndices();
    int[] arrayOfInt = this.mSelectorIndices;
    int i = arrayOfInt.length;
    int j = this.mTextSize;
    this.mSelectorTextGapHeight = ((int)((this.mBottom - this.mTop - i * j) / arrayOfInt.length + 0.5F));
    this.mSelectorElementHeight = (this.mTextSize + this.mSelectorTextGapHeight);
    this.mInitialScrollOffset = (this.mInputText.getBaseline() + this.mInputText.getTop() - this.mSelectorElementHeight * 1);
    this.mCurrentScrollOffset = this.mInitialScrollOffset;
    updateInputTextView();
  }
  
  @UnsupportedAppUsage
  private void initializeSelectorWheelIndices()
  {
    this.mSelectorIndexToStringCache.clear();
    int[] arrayOfInt = this.mSelectorIndices;
    int i = getValue();
    for (int j = 0; j < this.mSelectorIndices.length; j++)
    {
      int k = j - 1 + i;
      int m = k;
      if (this.mWrapSelectorWheel) {
        m = getWrappedSelectorIndex(k);
      }
      arrayOfInt[j] = m;
      ensureCachedScrollSelectorValue(arrayOfInt[j]);
    }
  }
  
  private int makeMeasureSpec(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1) {
      return paramInt1;
    }
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getMode(paramInt1);
    if (j != Integer.MIN_VALUE)
    {
      if (j != 0)
      {
        if (j == 1073741824) {
          return paramInt1;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unknown measure mode: ");
        localStringBuilder.append(j);
        throw new IllegalArgumentException(localStringBuilder.toString());
      }
      return View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
    }
    return View.MeasureSpec.makeMeasureSpec(Math.min(i, paramInt2), 1073741824);
  }
  
  private boolean moveToFinalScrollerPosition(Scroller paramScroller)
  {
    paramScroller.forceFinished(true);
    int i = paramScroller.getFinalY() - paramScroller.getCurrY();
    int j = this.mCurrentScrollOffset;
    int k = this.mSelectorElementHeight;
    j = this.mInitialScrollOffset - (j + i) % k;
    if (j != 0)
    {
      int m = Math.abs(j);
      int n = this.mSelectorElementHeight;
      k = j;
      if (m > n / 2) {
        if (j > 0) {
          k = j - n;
        } else {
          k = j + n;
        }
      }
      scrollBy(0, i + k);
      return true;
    }
    return false;
  }
  
  private void notifyChange(int paramInt1, int paramInt2)
  {
    OnValueChangeListener localOnValueChangeListener = this.mOnValueChangeListener;
    if (localOnValueChangeListener != null) {
      localOnValueChangeListener.onValueChange(this, paramInt1, this.mValue);
    }
  }
  
  private void onScrollStateChange(int paramInt)
  {
    if (this.mScrollState == paramInt) {
      return;
    }
    this.mScrollState = paramInt;
    OnScrollListener localOnScrollListener = this.mOnScrollListener;
    if (localOnScrollListener != null) {
      localOnScrollListener.onScrollStateChange(this, paramInt);
    }
  }
  
  private void onScrollerFinished(Scroller paramScroller)
  {
    if (paramScroller == this.mFlingScroller)
    {
      ensureScrollWheelAdjusted();
      updateInputTextView();
      onScrollStateChange(0);
    }
    else if (this.mScrollState != 1)
    {
      updateInputTextView();
    }
  }
  
  private void postBeginSoftInputOnLongPressCommand()
  {
    BeginSoftInputOnLongPressCommand localBeginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
    if (localBeginSoftInputOnLongPressCommand == null) {
      this.mBeginSoftInputOnLongPressCommand = new BeginSoftInputOnLongPressCommand();
    } else {
      removeCallbacks(localBeginSoftInputOnLongPressCommand);
    }
    postDelayed(this.mBeginSoftInputOnLongPressCommand, ViewConfiguration.getLongPressTimeout());
  }
  
  private void postChangeCurrentByOneFromLongPress(boolean paramBoolean, long paramLong)
  {
    ChangeCurrentByOneFromLongPressCommand localChangeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
    if (localChangeCurrentByOneFromLongPressCommand == null) {
      this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
    } else {
      removeCallbacks(localChangeCurrentByOneFromLongPressCommand);
    }
    this.mChangeCurrentByOneFromLongPressCommand.setStep(paramBoolean);
    postDelayed(this.mChangeCurrentByOneFromLongPressCommand, paramLong);
  }
  
  private void postSetSelectionCommand(int paramInt1, int paramInt2)
  {
    if (this.mSetSelectionCommand == null) {
      this.mSetSelectionCommand = new SetSelectionCommand(this.mInputText);
    }
    this.mSetSelectionCommand.post(paramInt1, paramInt2);
  }
  
  private void removeAllCallbacks()
  {
    Object localObject = this.mChangeCurrentByOneFromLongPressCommand;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    localObject = this.mSetSelectionCommand;
    if (localObject != null) {
      ((SetSelectionCommand)localObject).cancel();
    }
    localObject = this.mBeginSoftInputOnLongPressCommand;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    this.mPressedStateHelper.cancel();
  }
  
  private void removeBeginSoftInputCommand()
  {
    BeginSoftInputOnLongPressCommand localBeginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
    if (localBeginSoftInputOnLongPressCommand != null) {
      removeCallbacks(localBeginSoftInputOnLongPressCommand);
    }
  }
  
  private void removeChangeCurrentByOneFromLongPress()
  {
    ChangeCurrentByOneFromLongPressCommand localChangeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
    if (localChangeCurrentByOneFromLongPressCommand != null) {
      removeCallbacks(localChangeCurrentByOneFromLongPressCommand);
    }
  }
  
  private int resolveSizeAndStateRespectingMinSize(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 != -1) {
      return resolveSizeAndState(Math.max(paramInt1, paramInt2), paramInt3, 0);
    }
    return paramInt2;
  }
  
  private void setValueInternal(int paramInt, boolean paramBoolean)
  {
    if (this.mValue == paramInt) {
      return;
    }
    if (this.mWrapSelectorWheel) {
      paramInt = getWrappedSelectorIndex(paramInt);
    } else {
      paramInt = Math.min(Math.max(paramInt, this.mMinValue), this.mMaxValue);
    }
    int i = this.mValue;
    this.mValue = paramInt;
    if (this.mScrollState != 2) {
      updateInputTextView();
    }
    if (paramBoolean) {
      notifyChange(i, paramInt);
    }
    initializeSelectorWheelIndices();
    invalidate();
  }
  
  private void showSoftInput()
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
    if (localInputMethodManager != null)
    {
      if (this.mHasSelectorWheel) {
        this.mInputText.setVisibility(0);
      }
      this.mInputText.requestFocus();
      localInputMethodManager.showSoftInput(this.mInputText, 0);
    }
  }
  
  private void tryComputeMaxWidth()
  {
    if (!this.mComputeMaxWidth) {
      return;
    }
    int i = 0;
    String[] arrayOfString = this.mDisplayedValues;
    float f1;
    int j;
    int k;
    if (arrayOfString == null)
    {
      f1 = 0.0F;
      i = 0;
      while (i <= 9)
      {
        float f2 = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
        float f3 = f1;
        if (f2 > f1) {
          f3 = f2;
        }
        i++;
        f1 = f3;
      }
      j = 0;
      i = this.mMaxValue;
      while (i > 0)
      {
        j++;
        i /= 10;
      }
      k = (int)(j * f1);
    }
    else
    {
      int m = arrayOfString.length;
      j = 0;
      for (;;)
      {
        k = i;
        if (j >= m) {
          break;
        }
        f1 = this.mSelectorWheelPaint.measureText(this.mDisplayedValues[j]);
        k = i;
        if (f1 > i) {
          k = (int)f1;
        }
        j++;
        i = k;
      }
    }
    i = k + (this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight());
    if (this.mMaxWidth != i)
    {
      j = this.mMinWidth;
      if (i > j) {
        this.mMaxWidth = i;
      } else {
        this.mMaxWidth = j;
      }
      invalidate();
    }
  }
  
  private boolean updateInputTextView()
  {
    Object localObject = this.mDisplayedValues;
    if (localObject == null) {
      localObject = formatNumber(this.mValue);
    } else {
      localObject = localObject[(this.mValue - this.mMinValue)];
    }
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      Editable localEditable = this.mInputText.getText();
      if (!((String)localObject).equals(localEditable.toString()))
      {
        this.mInputText.setText((CharSequence)localObject);
        if (AccessibilityManager.getInstance(this.mContext).isEnabled())
        {
          AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(16);
          this.mInputText.onInitializeAccessibilityEvent(localAccessibilityEvent);
          this.mInputText.onPopulateAccessibilityEvent(localAccessibilityEvent);
          localAccessibilityEvent.setFromIndex(0);
          localAccessibilityEvent.setRemovedCount(localEditable.length());
          localAccessibilityEvent.setAddedCount(((String)localObject).length());
          localAccessibilityEvent.setBeforeText(localEditable);
          localAccessibilityEvent.setSource(this, 2);
          requestSendAccessibilityEvent(this, localAccessibilityEvent);
        }
        return true;
      }
    }
    return false;
  }
  
  private void updateWrapSelectorWheel()
  {
    int i = this.mMaxValue;
    int j = this.mMinValue;
    int k = this.mSelectorIndices.length;
    boolean bool = true;
    if (i - j >= k) {
      k = 1;
    } else {
      k = 0;
    }
    if ((k == 0) || (!this.mWrapSelectorWheelPreferred)) {
      bool = false;
    }
    this.mWrapSelectorWheel = bool;
  }
  
  private void validateInputTextView(View paramView)
  {
    paramView = String.valueOf(((TextView)paramView).getText());
    if (TextUtils.isEmpty(paramView)) {
      updateInputTextView();
    } else {
      setValueInternal(getSelectedPos(paramView.toString()), true);
    }
  }
  
  public void computeScroll()
  {
    Scroller localScroller1 = this.mFlingScroller;
    Scroller localScroller2 = localScroller1;
    if (localScroller1.isFinished())
    {
      localScroller1 = this.mAdjustScroller;
      localScroller2 = localScroller1;
      if (localScroller1.isFinished()) {
        return;
      }
    }
    localScroller2.computeScrollOffset();
    int i = localScroller2.getCurrY();
    if (this.mPreviousScrollerY == 0) {
      this.mPreviousScrollerY = localScroller2.getStartY();
    }
    scrollBy(0, i - this.mPreviousScrollerY);
    this.mPreviousScrollerY = i;
    if (localScroller2.isFinished()) {
      onScrollerFinished(localScroller2);
    } else {
      invalidate();
    }
  }
  
  protected int computeVerticalScrollExtent()
  {
    return getHeight();
  }
  
  protected int computeVerticalScrollOffset()
  {
    return this.mCurrentScrollOffset;
  }
  
  protected int computeVerticalScrollRange()
  {
    return (this.mMaxValue - this.mMinValue + 1) * this.mSelectorElementHeight;
  }
  
  protected boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    if (!this.mHasSelectorWheel) {
      return super.dispatchHoverEvent(paramMotionEvent);
    }
    if (AccessibilityManager.getInstance(this.mContext).isEnabled())
    {
      int i = (int)paramMotionEvent.getY();
      if (i < this.mTopSelectionDividerTop) {
        i = 3;
      } else if (i > this.mBottomSelectionDividerBottom) {
        i = 1;
      } else {
        i = 2;
      }
      int j = paramMotionEvent.getActionMasked();
      paramMotionEvent = (AccessibilityNodeProviderImpl)getAccessibilityNodeProvider();
      if (j != 7)
      {
        if (j != 9)
        {
          if (j == 10)
          {
            paramMotionEvent.sendAccessibilityEventForVirtualView(i, 256);
            this.mLastHoveredChildVirtualViewId = -1;
          }
        }
        else
        {
          paramMotionEvent.sendAccessibilityEventForVirtualView(i, 128);
          this.mLastHoveredChildVirtualViewId = i;
          paramMotionEvent.performAction(i, 64, null);
        }
      }
      else
      {
        j = this.mLastHoveredChildVirtualViewId;
        if ((j != i) && (j != -1))
        {
          paramMotionEvent.sendAccessibilityEventForVirtualView(j, 256);
          paramMotionEvent.sendAccessibilityEventForVirtualView(i, 128);
          this.mLastHoveredChildVirtualViewId = i;
          paramMotionEvent.performAction(i, 64, null);
        }
      }
    }
    return false;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getKeyCode();
    if ((i != 19) && (i != 20))
    {
      if ((i == 23) || (i == 66)) {
        removeAllCallbacks();
      }
    }
    else if (this.mHasSelectorWheel)
    {
      int j = paramKeyEvent.getAction();
      if (j != 0)
      {
        if ((j == 1) && (this.mLastHandledDownDpadKeyCode == i))
        {
          this.mLastHandledDownDpadKeyCode = -1;
          return true;
        }
      }
      else
      {
        if (this.mWrapSelectorWheel) {
          break label128;
        }
        if (i == 20) {
          if (getValue() >= getMaxValue()) {
            break label122;
          }
        } else {
          if (getValue() > getMinValue()) {
            break label128;
          }
        }
      }
    }
    label122:
    return super.dispatchKeyEvent(paramKeyEvent);
    label128:
    requestFocus();
    this.mLastHandledDownDpadKeyCode = i;
    removeAllCallbacks();
    if (this.mFlingScroller.isFinished())
    {
      boolean bool;
      if (i == 20) {
        bool = true;
      } else {
        bool = false;
      }
      changeValueByOne(bool);
    }
    return true;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    if ((i == 1) || (i == 3)) {
      removeAllCallbacks();
    }
    return super.dispatchTouchEvent(paramMotionEvent);
  }
  
  public boolean dispatchTrackballEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    if ((i == 1) || (i == 3)) {
      removeAllCallbacks();
    }
    return super.dispatchTrackballEvent(paramMotionEvent);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mSelectionDivider;
    if ((localDrawable != null) && (localDrawable.isStateful()) && (localDrawable.setState(getDrawableState()))) {
      invalidateDrawable(localDrawable);
    }
  }
  
  public AccessibilityNodeProvider getAccessibilityNodeProvider()
  {
    if (!this.mHasSelectorWheel) {
      return super.getAccessibilityNodeProvider();
    }
    if (this.mAccessibilityNodeProvider == null) {
      this.mAccessibilityNodeProvider = new AccessibilityNodeProviderImpl();
    }
    return this.mAccessibilityNodeProvider;
  }
  
  protected float getBottomFadingEdgeStrength()
  {
    return 0.9F;
  }
  
  public CharSequence getDisplayedValueForCurrentSelection()
  {
    return (CharSequence)this.mSelectorIndexToStringCache.get(getValue());
  }
  
  public String[] getDisplayedValues()
  {
    return this.mDisplayedValues;
  }
  
  public int getMaxValue()
  {
    return this.mMaxValue;
  }
  
  public int getMinValue()
  {
    return this.mMinValue;
  }
  
  public int getSelectionDividerHeight()
  {
    return this.mSelectionDividerHeight;
  }
  
  public int getSolidColor()
  {
    return this.mSolidColor;
  }
  
  public int getTextColor()
  {
    return this.mSelectorWheelPaint.getColor();
  }
  
  public float getTextSize()
  {
    return this.mSelectorWheelPaint.getTextSize();
  }
  
  protected float getTopFadingEdgeStrength()
  {
    return 0.9F;
  }
  
  public int getValue()
  {
    return this.mValue;
  }
  
  public boolean getWrapSelectorWheel()
  {
    return this.mWrapSelectorWheel;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    Drawable localDrawable = this.mSelectionDivider;
    if (localDrawable != null) {
      localDrawable.jumpToCurrentState();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeAllCallbacks();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (!this.mHasSelectorWheel)
    {
      super.onDraw(paramCanvas);
      return;
    }
    boolean bool;
    if (this.mHideWheelUntilFocused) {
      bool = hasFocus();
    } else {
      bool = true;
    }
    float f1 = (this.mRight - this.mLeft) / 2;
    float f2 = this.mCurrentScrollOffset;
    Object localObject;
    if (bool)
    {
      localObject = this.mVirtualButtonPressedDrawable;
      if ((localObject != null) && (this.mScrollState == 0))
      {
        if (this.mDecrementVirtualButtonPressed)
        {
          ((Drawable)localObject).setState(PRESSED_STATE_SET);
          this.mVirtualButtonPressedDrawable.setBounds(0, 0, this.mRight, this.mTopSelectionDividerTop);
          this.mVirtualButtonPressedDrawable.draw(paramCanvas);
        }
        if (this.mIncrementVirtualButtonPressed)
        {
          this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
          this.mVirtualButtonPressedDrawable.setBounds(0, this.mBottomSelectionDividerBottom, this.mRight, this.mBottom);
          this.mVirtualButtonPressedDrawable.draw(paramCanvas);
        }
      }
    }
    int[] arrayOfInt = this.mSelectorIndices;
    int j;
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      j = arrayOfInt[i];
      localObject = (String)this.mSelectorIndexToStringCache.get(j);
      if (((bool) && (i != 1)) || ((i == 1) && (this.mInputText.getVisibility() != 0))) {
        paramCanvas.drawText((String)localObject, f1, f2, this.mSelectorWheelPaint);
      }
      f2 += this.mSelectorElementHeight;
    }
    if (bool)
    {
      localObject = this.mSelectionDivider;
      if (localObject != null)
      {
        j = this.mTopSelectionDividerTop;
        i = this.mSelectionDividerHeight;
        ((Drawable)localObject).setBounds(0, j, this.mRight, i + j);
        this.mSelectionDivider.draw(paramCanvas);
        j = this.mBottomSelectionDividerBottom;
        i = this.mSelectionDividerHeight;
        this.mSelectionDivider.setBounds(0, j - i, this.mRight, j);
        this.mSelectionDivider.draw(paramCanvas);
      }
    }
  }
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(NumberPicker.class.getName());
    paramAccessibilityEvent.setScrollable(true);
    paramAccessibilityEvent.setScrollY((this.mMinValue + this.mValue) * this.mSelectorElementHeight);
    paramAccessibilityEvent.setMaxScrollY((this.mMaxValue - this.mMinValue) * this.mSelectorElementHeight);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mHasSelectorWheel) && (isEnabled()))
    {
      if (paramMotionEvent.getActionMasked() != 0) {
        return false;
      }
      removeAllCallbacks();
      hideSoftInput();
      float f = paramMotionEvent.getY();
      this.mLastDownEventY = f;
      this.mLastDownOrMoveEventY = f;
      this.mLastDownEventTime = paramMotionEvent.getEventTime();
      this.mIgnoreMoveEvents = false;
      this.mPerformClickOnTap = false;
      f = this.mLastDownEventY;
      if (f < this.mTopSelectionDividerTop)
      {
        if (this.mScrollState == 0) {
          this.mPressedStateHelper.buttonPressDelayed(2);
        }
      }
      else if ((f > this.mBottomSelectionDividerBottom) && (this.mScrollState == 0)) {
        this.mPressedStateHelper.buttonPressDelayed(1);
      }
      getParent().requestDisallowInterceptTouchEvent(true);
      if (!this.mFlingScroller.isFinished())
      {
        this.mFlingScroller.forceFinished(true);
        this.mAdjustScroller.forceFinished(true);
        onScrollStateChange(0);
      }
      else if (!this.mAdjustScroller.isFinished())
      {
        this.mFlingScroller.forceFinished(true);
        this.mAdjustScroller.forceFinished(true);
      }
      else
      {
        f = this.mLastDownEventY;
        if (f < this.mTopSelectionDividerTop)
        {
          postChangeCurrentByOneFromLongPress(false, ViewConfiguration.getLongPressTimeout());
        }
        else if (f > this.mBottomSelectionDividerBottom)
        {
          postChangeCurrentByOneFromLongPress(true, ViewConfiguration.getLongPressTimeout());
        }
        else
        {
          this.mPerformClickOnTap = true;
          postBeginSoftInputOnLongPressCommand();
        }
      }
      return true;
    }
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!this.mHasSelectorWheel)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    paramInt3 = getMeasuredWidth();
    paramInt4 = getMeasuredHeight();
    paramInt1 = this.mInputText.getMeasuredWidth();
    paramInt2 = this.mInputText.getMeasuredHeight();
    paramInt3 = (paramInt3 - paramInt1) / 2;
    paramInt4 = (paramInt4 - paramInt2) / 2;
    this.mInputText.layout(paramInt3, paramInt4, paramInt3 + paramInt1, paramInt4 + paramInt2);
    if (paramBoolean)
    {
      initializeSelectorWheel();
      initializeFadingEdges();
      paramInt2 = getHeight();
      paramInt1 = this.mSelectionDividersDistance;
      paramInt3 = (paramInt2 - paramInt1) / 2;
      paramInt2 = this.mSelectionDividerHeight;
      this.mTopSelectionDividerTop = (paramInt3 - paramInt2);
      this.mBottomSelectionDividerBottom = (this.mTopSelectionDividerTop + paramInt2 * 2 + paramInt1);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (!this.mHasSelectorWheel)
    {
      super.onMeasure(paramInt1, paramInt2);
      return;
    }
    super.onMeasure(makeMeasureSpec(paramInt1, this.mMaxWidth), makeMeasureSpec(paramInt2, this.mMaxHeight));
    setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), paramInt1), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), paramInt2));
  }
  
  public void onResolveDrawables(int paramInt)
  {
    super.onResolveDrawables(paramInt);
    Drawable localDrawable = this.mSelectionDivider;
    if (localDrawable != null) {
      localDrawable.setLayoutDirection(paramInt);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((isEnabled()) && (this.mHasSelectorWheel))
    {
      if (this.mVelocityTracker == null) {
        this.mVelocityTracker = VelocityTracker.obtain();
      }
      this.mVelocityTracker.addMovement(paramMotionEvent);
      int i = paramMotionEvent.getActionMasked();
      if (i != 1)
      {
        if ((i == 2) && (!this.mIgnoreMoveEvents))
        {
          float f = paramMotionEvent.getY();
          if (this.mScrollState != 1)
          {
            if ((int)Math.abs(f - this.mLastDownEventY) > this.mTouchSlop)
            {
              removeAllCallbacks();
              onScrollStateChange(1);
            }
          }
          else
          {
            scrollBy(0, (int)(f - this.mLastDownOrMoveEventY));
            invalidate();
          }
          this.mLastDownOrMoveEventY = f;
        }
      }
      else
      {
        removeBeginSoftInputCommand();
        removeChangeCurrentByOneFromLongPress();
        this.mPressedStateHelper.cancel();
        VelocityTracker localVelocityTracker = this.mVelocityTracker;
        localVelocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
        i = (int)localVelocityTracker.getYVelocity();
        if (Math.abs(i) > this.mMinimumFlingVelocity)
        {
          fling(i);
          onScrollStateChange(2);
        }
        else
        {
          i = (int)paramMotionEvent.getY();
          int j = (int)Math.abs(i - this.mLastDownEventY);
          long l1 = paramMotionEvent.getEventTime();
          long l2 = this.mLastDownEventTime;
          if ((j <= this.mTouchSlop) && (l1 - l2 < ViewConfiguration.getTapTimeout()))
          {
            if (this.mPerformClickOnTap)
            {
              this.mPerformClickOnTap = false;
              performClick();
            }
            else
            {
              i = i / this.mSelectorElementHeight - 1;
              if (i > 0)
              {
                changeValueByOne(true);
                this.mPressedStateHelper.buttonTapped(1);
              }
              else if (i < 0)
              {
                changeValueByOne(false);
                this.mPressedStateHelper.buttonTapped(2);
              }
            }
          }
          else {
            ensureScrollWheelAdjusted();
          }
          onScrollStateChange(0);
        }
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
      }
      return true;
    }
    return false;
  }
  
  public boolean performClick()
  {
    if (!this.mHasSelectorWheel) {
      return super.performClick();
    }
    if (!super.performClick()) {
      showSoftInput();
    }
    return true;
  }
  
  public boolean performLongClick()
  {
    if (!this.mHasSelectorWheel) {
      return super.performLongClick();
    }
    if (!super.performLongClick())
    {
      showSoftInput();
      this.mIgnoreMoveEvents = true;
    }
    return true;
  }
  
  public void scrollBy(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = this.mSelectorIndices;
    paramInt1 = this.mCurrentScrollOffset;
    if ((!this.mWrapSelectorWheel) && (paramInt2 > 0) && (arrayOfInt[1] <= this.mMinValue))
    {
      this.mCurrentScrollOffset = this.mInitialScrollOffset;
      return;
    }
    if ((!this.mWrapSelectorWheel) && (paramInt2 < 0) && (arrayOfInt[1] >= this.mMaxValue))
    {
      this.mCurrentScrollOffset = this.mInitialScrollOffset;
      return;
    }
    for (this.mCurrentScrollOffset += paramInt2;; this.mCurrentScrollOffset = this.mInitialScrollOffset) {
      do
      {
        paramInt2 = this.mCurrentScrollOffset;
        if (paramInt2 - this.mInitialScrollOffset <= this.mSelectorTextGapHeight) {
          break;
        }
        this.mCurrentScrollOffset = (paramInt2 - this.mSelectorElementHeight);
        decrementSelectorIndices(arrayOfInt);
        setValueInternal(arrayOfInt[1], true);
      } while ((this.mWrapSelectorWheel) || (arrayOfInt[1] > this.mMinValue));
    }
    for (;;)
    {
      paramInt2 = this.mCurrentScrollOffset;
      if (paramInt2 - this.mInitialScrollOffset >= -this.mSelectorTextGapHeight) {
        break;
      }
      this.mCurrentScrollOffset = (paramInt2 + this.mSelectorElementHeight);
      incrementSelectorIndices(arrayOfInt);
      setValueInternal(arrayOfInt[1], true);
      if ((!this.mWrapSelectorWheel) && (arrayOfInt[1] >= this.mMaxValue)) {
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
      }
    }
    if (paramInt1 != paramInt2) {
      onScrollChanged(0, paramInt2, 0, paramInt1);
    }
  }
  
  public void setDisplayedValues(String[] paramArrayOfString)
  {
    if (this.mDisplayedValues == paramArrayOfString) {
      return;
    }
    this.mDisplayedValues = paramArrayOfString;
    if (this.mDisplayedValues != null) {
      this.mInputText.setRawInputType(524289);
    } else {
      this.mInputText.setRawInputType(2);
    }
    updateInputTextView();
    initializeSelectorWheelIndices();
    tryComputeMaxWidth();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if (!this.mHasSelectorWheel) {
      this.mIncrementButton.setEnabled(paramBoolean);
    }
    if (!this.mHasSelectorWheel) {
      this.mDecrementButton.setEnabled(paramBoolean);
    }
    this.mInputText.setEnabled(paramBoolean);
  }
  
  public void setFormatter(Formatter paramFormatter)
  {
    if (paramFormatter == this.mFormatter) {
      return;
    }
    this.mFormatter = paramFormatter;
    initializeSelectorWheelIndices();
    updateInputTextView();
  }
  
  public void setMaxValue(int paramInt)
  {
    if (this.mMaxValue == paramInt) {
      return;
    }
    if (paramInt >= 0)
    {
      this.mMaxValue = paramInt;
      paramInt = this.mMaxValue;
      if (paramInt < this.mValue) {
        this.mValue = paramInt;
      }
      updateWrapSelectorWheel();
      initializeSelectorWheelIndices();
      updateInputTextView();
      tryComputeMaxWidth();
      invalidate();
      return;
    }
    throw new IllegalArgumentException("maxValue must be >= 0");
  }
  
  public void setMinValue(int paramInt)
  {
    if (this.mMinValue == paramInt) {
      return;
    }
    if (paramInt >= 0)
    {
      this.mMinValue = paramInt;
      paramInt = this.mMinValue;
      if (paramInt > this.mValue) {
        this.mValue = paramInt;
      }
      updateWrapSelectorWheel();
      initializeSelectorWheelIndices();
      updateInputTextView();
      tryComputeMaxWidth();
      invalidate();
      return;
    }
    throw new IllegalArgumentException("minValue must be >= 0");
  }
  
  public void setOnLongPressUpdateInterval(long paramLong)
  {
    this.mLongPressUpdateInterval = paramLong;
  }
  
  public void setOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    this.mOnScrollListener = paramOnScrollListener;
  }
  
  public void setOnValueChangedListener(OnValueChangeListener paramOnValueChangeListener)
  {
    this.mOnValueChangeListener = paramOnValueChangeListener;
  }
  
  public void setSelectionDividerHeight(int paramInt)
  {
    this.mSelectionDividerHeight = paramInt;
    invalidate();
  }
  
  public void setTextColor(int paramInt)
  {
    this.mSelectorWheelPaint.setColor(paramInt);
    this.mInputText.setTextColor(paramInt);
    invalidate();
  }
  
  public void setTextSize(float paramFloat)
  {
    this.mSelectorWheelPaint.setTextSize(paramFloat);
    this.mInputText.setTextSize(0, paramFloat);
    invalidate();
  }
  
  public void setValue(int paramInt)
  {
    setValueInternal(paramInt, false);
  }
  
  public void setWrapSelectorWheel(boolean paramBoolean)
  {
    this.mWrapSelectorWheelPreferred = paramBoolean;
    updateWrapSelectorWheel();
  }
  
  class AccessibilityNodeProviderImpl
    extends AccessibilityNodeProvider
  {
    private static final int UNDEFINED = Integer.MIN_VALUE;
    private static final int VIRTUAL_VIEW_ID_DECREMENT = 3;
    private static final int VIRTUAL_VIEW_ID_INCREMENT = 1;
    private static final int VIRTUAL_VIEW_ID_INPUT = 2;
    private int mAccessibilityFocusedView = Integer.MIN_VALUE;
    private final int[] mTempArray = new int[2];
    private final Rect mTempRect = new Rect();
    
    AccessibilityNodeProviderImpl() {}
    
    private AccessibilityNodeInfo createAccessibilityNodeInfoForNumberPicker(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo = AccessibilityNodeInfo.obtain();
      localAccessibilityNodeInfo.setClassName(NumberPicker.class.getName());
      localAccessibilityNodeInfo.setPackageName(NumberPicker.this.mContext.getPackageName());
      localAccessibilityNodeInfo.setSource(NumberPicker.this);
      if (hasVirtualDecrementButton()) {
        localAccessibilityNodeInfo.addChild(NumberPicker.this, 3);
      }
      localAccessibilityNodeInfo.addChild(NumberPicker.this, 2);
      if (hasVirtualIncrementButton()) {
        localAccessibilityNodeInfo.addChild(NumberPicker.this, 1);
      }
      localAccessibilityNodeInfo.setParent((View)NumberPicker.this.getParentForAccessibility());
      localAccessibilityNodeInfo.setEnabled(NumberPicker.this.isEnabled());
      localAccessibilityNodeInfo.setScrollable(true);
      float f = NumberPicker.this.getContext().getResources().getCompatibilityInfo().applicationScale;
      Rect localRect = this.mTempRect;
      localRect.set(paramInt1, paramInt2, paramInt3, paramInt4);
      localRect.scale(f);
      localAccessibilityNodeInfo.setBoundsInParent(localRect);
      localAccessibilityNodeInfo.setVisibleToUser(NumberPicker.this.isVisibleToUser());
      int[] arrayOfInt = this.mTempArray;
      NumberPicker.this.getLocationOnScreen(arrayOfInt);
      localRect.offset(arrayOfInt[0], arrayOfInt[1]);
      localRect.scale(f);
      localAccessibilityNodeInfo.setBoundsInScreen(localRect);
      if (this.mAccessibilityFocusedView != -1) {
        localAccessibilityNodeInfo.addAction(64);
      }
      if (this.mAccessibilityFocusedView == -1) {
        localAccessibilityNodeInfo.addAction(128);
      }
      if (NumberPicker.this.isEnabled())
      {
        if ((NumberPicker.this.getWrapSelectorWheel()) || (NumberPicker.this.getValue() < NumberPicker.this.getMaxValue())) {
          localAccessibilityNodeInfo.addAction(4096);
        }
        if ((NumberPicker.this.getWrapSelectorWheel()) || (NumberPicker.this.getValue() > NumberPicker.this.getMinValue())) {
          localAccessibilityNodeInfo.addAction(8192);
        }
      }
      return localAccessibilityNodeInfo;
    }
    
    private AccessibilityNodeInfo createAccessibilityNodeInfoForVirtualButton(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo = AccessibilityNodeInfo.obtain();
      localAccessibilityNodeInfo.setClassName(Button.class.getName());
      localAccessibilityNodeInfo.setPackageName(NumberPicker.this.mContext.getPackageName());
      localAccessibilityNodeInfo.setSource(NumberPicker.this, paramInt1);
      localAccessibilityNodeInfo.setParent(NumberPicker.this);
      localAccessibilityNodeInfo.setText(paramString);
      localAccessibilityNodeInfo.setClickable(true);
      localAccessibilityNodeInfo.setLongClickable(true);
      localAccessibilityNodeInfo.setEnabled(NumberPicker.this.isEnabled());
      Rect localRect = this.mTempRect;
      localRect.set(paramInt2, paramInt3, paramInt4, paramInt5);
      localAccessibilityNodeInfo.setVisibleToUser(NumberPicker.this.isVisibleToUser(localRect));
      localAccessibilityNodeInfo.setBoundsInParent(localRect);
      paramString = this.mTempArray;
      NumberPicker.this.getLocationOnScreen(paramString);
      localRect.offset(paramString[0], paramString[1]);
      localAccessibilityNodeInfo.setBoundsInScreen(localRect);
      if (this.mAccessibilityFocusedView != paramInt1) {
        localAccessibilityNodeInfo.addAction(64);
      }
      if (this.mAccessibilityFocusedView == paramInt1) {
        localAccessibilityNodeInfo.addAction(128);
      }
      if (NumberPicker.this.isEnabled()) {
        localAccessibilityNodeInfo.addAction(16);
      }
      return localAccessibilityNodeInfo;
    }
    
    private AccessibilityNodeInfo createAccessibiltyNodeInfoForInputText(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      AccessibilityNodeInfo localAccessibilityNodeInfo = NumberPicker.this.mInputText.createAccessibilityNodeInfo();
      localAccessibilityNodeInfo.setSource(NumberPicker.this, 2);
      if (this.mAccessibilityFocusedView != 2) {
        localAccessibilityNodeInfo.addAction(64);
      }
      if (this.mAccessibilityFocusedView == 2) {
        localAccessibilityNodeInfo.addAction(128);
      }
      Rect localRect = this.mTempRect;
      localRect.set(paramInt1, paramInt2, paramInt3, paramInt4);
      localAccessibilityNodeInfo.setVisibleToUser(NumberPicker.this.isVisibleToUser(localRect));
      localAccessibilityNodeInfo.setBoundsInParent(localRect);
      int[] arrayOfInt = this.mTempArray;
      NumberPicker.this.getLocationOnScreen(arrayOfInt);
      localRect.offset(arrayOfInt[0], arrayOfInt[1]);
      localAccessibilityNodeInfo.setBoundsInScreen(localRect);
      return localAccessibilityNodeInfo;
    }
    
    private void findAccessibilityNodeInfosByTextInChild(String paramString, int paramInt, List<AccessibilityNodeInfo> paramList)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt == 3)
          {
            localObject = getVirtualDecrementButtonText();
            if ((!TextUtils.isEmpty((CharSequence)localObject)) && (((String)localObject).toString().toLowerCase().contains(paramString))) {
              paramList.add(createAccessibilityNodeInfo(3));
            }
          }
        }
        else
        {
          localObject = NumberPicker.this.mInputText.getText();
          if ((!TextUtils.isEmpty((CharSequence)localObject)) && (((CharSequence)localObject).toString().toLowerCase().contains(paramString)))
          {
            paramList.add(createAccessibilityNodeInfo(2));
            return;
          }
          localObject = NumberPicker.this.mInputText.getText();
          if ((!TextUtils.isEmpty((CharSequence)localObject)) && (((CharSequence)localObject).toString().toLowerCase().contains(paramString)))
          {
            paramList.add(createAccessibilityNodeInfo(2));
            return;
          }
        }
        return;
      }
      Object localObject = getVirtualIncrementButtonText();
      if ((!TextUtils.isEmpty((CharSequence)localObject)) && (((String)localObject).toString().toLowerCase().contains(paramString))) {
        paramList.add(createAccessibilityNodeInfo(1));
      }
    }
    
    private String getVirtualDecrementButtonText()
    {
      int i = NumberPicker.this.mValue - 1;
      int j = i;
      if (NumberPicker.this.mWrapSelectorWheel) {
        j = NumberPicker.this.getWrappedSelectorIndex(i);
      }
      if (j >= NumberPicker.this.mMinValue)
      {
        String str;
        if (NumberPicker.this.mDisplayedValues == null) {
          str = NumberPicker.this.formatNumber(j);
        } else {
          str = NumberPicker.this.mDisplayedValues[(j - NumberPicker.this.mMinValue)];
        }
        return str;
      }
      return null;
    }
    
    private String getVirtualIncrementButtonText()
    {
      int i = NumberPicker.this.mValue + 1;
      int j = i;
      if (NumberPicker.this.mWrapSelectorWheel) {
        j = NumberPicker.this.getWrappedSelectorIndex(i);
      }
      if (j <= NumberPicker.this.mMaxValue)
      {
        String str;
        if (NumberPicker.this.mDisplayedValues == null) {
          str = NumberPicker.this.formatNumber(j);
        } else {
          str = NumberPicker.this.mDisplayedValues[(j - NumberPicker.this.mMinValue)];
        }
        return str;
      }
      return null;
    }
    
    private boolean hasVirtualDecrementButton()
    {
      boolean bool;
      if ((!NumberPicker.this.getWrapSelectorWheel()) && (NumberPicker.this.getValue() <= NumberPicker.this.getMinValue())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    private boolean hasVirtualIncrementButton()
    {
      boolean bool;
      if ((!NumberPicker.this.getWrapSelectorWheel()) && (NumberPicker.this.getValue() >= NumberPicker.this.getMaxValue())) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    private void sendAccessibilityEventForVirtualButton(int paramInt1, int paramInt2, String paramString)
    {
      if (AccessibilityManager.getInstance(NumberPicker.this.mContext).isEnabled())
      {
        AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt2);
        localAccessibilityEvent.setClassName(Button.class.getName());
        localAccessibilityEvent.setPackageName(NumberPicker.this.mContext.getPackageName());
        localAccessibilityEvent.getText().add(paramString);
        localAccessibilityEvent.setEnabled(NumberPicker.this.isEnabled());
        localAccessibilityEvent.setSource(NumberPicker.this, paramInt1);
        paramString = NumberPicker.this;
        paramString.requestSendAccessibilityEvent(paramString, localAccessibilityEvent);
      }
    }
    
    private void sendAccessibilityEventForVirtualText(int paramInt)
    {
      if (AccessibilityManager.getInstance(NumberPicker.this.mContext).isEnabled())
      {
        AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt);
        NumberPicker.this.mInputText.onInitializeAccessibilityEvent(localAccessibilityEvent);
        NumberPicker.this.mInputText.onPopulateAccessibilityEvent(localAccessibilityEvent);
        localAccessibilityEvent.setSource(NumberPicker.this, 2);
        NumberPicker localNumberPicker = NumberPicker.this;
        localNumberPicker.requestSendAccessibilityEvent(localNumberPicker, localAccessibilityEvent);
      }
    }
    
    public AccessibilityNodeInfo createAccessibilityNodeInfo(int paramInt)
    {
      if (paramInt != -1)
      {
        if (paramInt != 1)
        {
          if (paramInt != 2)
          {
            if (paramInt != 3) {
              return super.createAccessibilityNodeInfo(paramInt);
            }
            return createAccessibilityNodeInfoForVirtualButton(3, getVirtualDecrementButtonText(), NumberPicker.this.mScrollX, NumberPicker.this.mScrollY, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mTopSelectionDividerTop + NumberPicker.this.mSelectionDividerHeight);
          }
          return createAccessibiltyNodeInfoForInputText(NumberPicker.this.mScrollX, NumberPicker.this.mTopSelectionDividerTop + NumberPicker.this.mSelectionDividerHeight, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mBottomSelectionDividerBottom - NumberPicker.this.mSelectionDividerHeight);
        }
        return createAccessibilityNodeInfoForVirtualButton(1, getVirtualIncrementButtonText(), NumberPicker.this.mScrollX, NumberPicker.this.mBottomSelectionDividerBottom - NumberPicker.this.mSelectionDividerHeight, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mScrollY + (NumberPicker.this.mBottom - NumberPicker.this.mTop));
      }
      return createAccessibilityNodeInfoForNumberPicker(NumberPicker.this.mScrollX, NumberPicker.this.mScrollY, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mScrollY + (NumberPicker.this.mBottom - NumberPicker.this.mTop));
    }
    
    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String paramString, int paramInt)
    {
      if (TextUtils.isEmpty(paramString)) {
        return Collections.emptyList();
      }
      String str = paramString.toLowerCase();
      ArrayList localArrayList = new ArrayList();
      if (paramInt != -1)
      {
        if ((paramInt != 1) && (paramInt != 2) && (paramInt != 3)) {
          return super.findAccessibilityNodeInfosByText(paramString, paramInt);
        }
        findAccessibilityNodeInfosByTextInChild(str, paramInt, localArrayList);
        return localArrayList;
      }
      findAccessibilityNodeInfosByTextInChild(str, 3, localArrayList);
      findAccessibilityNodeInfosByTextInChild(str, 2, localArrayList);
      findAccessibilityNodeInfosByTextInChild(str, 1, localArrayList);
      return localArrayList;
    }
    
    public boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      boolean bool = false;
      if (paramInt1 != -1)
      {
        if (paramInt1 != 1)
        {
          if (paramInt1 != 2)
          {
            if (paramInt1 == 3)
            {
              if (paramInt2 != 16)
              {
                if (paramInt2 != 64)
                {
                  if (paramInt2 != 128) {
                    return false;
                  }
                  if (this.mAccessibilityFocusedView == paramInt1)
                  {
                    this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                    sendAccessibilityEventForVirtualView(paramInt1, 65536);
                    paramBundle = NumberPicker.this;
                    paramBundle.invalidate(0, 0, paramBundle.mRight, NumberPicker.this.mTopSelectionDividerTop);
                    return true;
                  }
                  return false;
                }
                if (this.mAccessibilityFocusedView != paramInt1)
                {
                  this.mAccessibilityFocusedView = paramInt1;
                  sendAccessibilityEventForVirtualView(paramInt1, 32768);
                  paramBundle = NumberPicker.this;
                  paramBundle.invalidate(0, 0, paramBundle.mRight, NumberPicker.this.mTopSelectionDividerTop);
                  return true;
                }
                return false;
              }
              if (NumberPicker.this.isEnabled())
              {
                if (paramInt1 == 1) {
                  bool = true;
                }
                NumberPicker.this.changeValueByOne(bool);
                sendAccessibilityEventForVirtualView(paramInt1, 1);
                return true;
              }
              return false;
            }
          }
          else
          {
            if (paramInt2 != 1)
            {
              if (paramInt2 != 2)
              {
                if (paramInt2 != 16)
                {
                  if (paramInt2 != 32)
                  {
                    if (paramInt2 != 64)
                    {
                      if (paramInt2 != 128) {
                        return NumberPicker.this.mInputText.performAccessibilityAction(paramInt2, paramBundle);
                      }
                      if (this.mAccessibilityFocusedView == paramInt1)
                      {
                        this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                        sendAccessibilityEventForVirtualView(paramInt1, 65536);
                        NumberPicker.this.mInputText.invalidate();
                        return true;
                      }
                      return false;
                    }
                    if (this.mAccessibilityFocusedView != paramInt1)
                    {
                      this.mAccessibilityFocusedView = paramInt1;
                      sendAccessibilityEventForVirtualView(paramInt1, 32768);
                      NumberPicker.this.mInputText.invalidate();
                      return true;
                    }
                    return false;
                  }
                  if (NumberPicker.this.isEnabled())
                  {
                    NumberPicker.this.performLongClick();
                    return true;
                  }
                  return false;
                }
                if (NumberPicker.this.isEnabled())
                {
                  NumberPicker.this.performClick();
                  return true;
                }
                return false;
              }
              if ((NumberPicker.this.isEnabled()) && (NumberPicker.this.mInputText.isFocused()))
              {
                NumberPicker.this.mInputText.clearFocus();
                return true;
              }
              return false;
            }
            if ((NumberPicker.this.isEnabled()) && (!NumberPicker.this.mInputText.isFocused())) {
              return NumberPicker.this.mInputText.requestFocus();
            }
            return false;
          }
        }
        else
        {
          if (paramInt2 != 16)
          {
            if (paramInt2 != 64)
            {
              if (paramInt2 != 128) {
                return false;
              }
              if (this.mAccessibilityFocusedView == paramInt1)
              {
                this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                sendAccessibilityEventForVirtualView(paramInt1, 65536);
                paramBundle = NumberPicker.this;
                paramBundle.invalidate(0, paramBundle.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                return true;
              }
              return false;
            }
            if (this.mAccessibilityFocusedView != paramInt1)
            {
              this.mAccessibilityFocusedView = paramInt1;
              sendAccessibilityEventForVirtualView(paramInt1, 32768);
              paramBundle = NumberPicker.this;
              paramBundle.invalidate(0, paramBundle.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
              return true;
            }
            return false;
          }
          if (NumberPicker.this.isEnabled())
          {
            NumberPicker.this.changeValueByOne(true);
            sendAccessibilityEventForVirtualView(paramInt1, 1);
            return true;
          }
          return false;
        }
      }
      else
      {
        if (paramInt2 == 64) {
          break label729;
        }
        if (paramInt2 == 128) {
          break label704;
        }
        if (paramInt2 == 4096) {
          break label655;
        }
        if (paramInt2 == 8192) {
          break label606;
        }
      }
      return super.performAction(paramInt1, paramInt2, paramBundle);
      label606:
      if ((NumberPicker.this.isEnabled()) && ((NumberPicker.this.getWrapSelectorWheel()) || (NumberPicker.this.getValue() > NumberPicker.this.getMinValue())))
      {
        NumberPicker.this.changeValueByOne(false);
        return true;
      }
      return false;
      label655:
      if ((NumberPicker.this.isEnabled()) && ((NumberPicker.this.getWrapSelectorWheel()) || (NumberPicker.this.getValue() < NumberPicker.this.getMaxValue())))
      {
        NumberPicker.this.changeValueByOne(true);
        return true;
      }
      return false;
      label704:
      if (this.mAccessibilityFocusedView == paramInt1)
      {
        this.mAccessibilityFocusedView = Integer.MIN_VALUE;
        NumberPicker.this.clearAccessibilityFocus();
        return true;
      }
      return false;
      label729:
      if (this.mAccessibilityFocusedView != paramInt1)
      {
        this.mAccessibilityFocusedView = paramInt1;
        NumberPicker.this.requestAccessibilityFocus();
        return true;
      }
      return false;
    }
    
    public void sendAccessibilityEventForVirtualView(int paramInt1, int paramInt2)
    {
      if (paramInt1 != 1)
      {
        if (paramInt1 != 2)
        {
          if ((paramInt1 == 3) && (hasVirtualDecrementButton())) {
            sendAccessibilityEventForVirtualButton(paramInt1, paramInt2, getVirtualDecrementButtonText());
          }
        }
        else {
          sendAccessibilityEventForVirtualText(paramInt2);
        }
      }
      else if (hasVirtualIncrementButton()) {
        sendAccessibilityEventForVirtualButton(paramInt1, paramInt2, getVirtualIncrementButtonText());
      }
    }
  }
  
  class BeginSoftInputOnLongPressCommand
    implements Runnable
  {
    BeginSoftInputOnLongPressCommand() {}
    
    public void run()
    {
      NumberPicker.this.performLongClick();
    }
  }
  
  class ChangeCurrentByOneFromLongPressCommand
    implements Runnable
  {
    private boolean mIncrement;
    
    ChangeCurrentByOneFromLongPressCommand() {}
    
    private void setStep(boolean paramBoolean)
    {
      this.mIncrement = paramBoolean;
    }
    
    public void run()
    {
      NumberPicker.this.changeValueByOne(this.mIncrement);
      NumberPicker localNumberPicker = NumberPicker.this;
      localNumberPicker.postDelayed(this, localNumberPicker.mLongPressUpdateInterval);
    }
  }
  
  public static class CustomEditText
    extends EditText
  {
    public CustomEditText(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public void onEditorAction(int paramInt)
    {
      super.onEditorAction(paramInt);
      if (paramInt == 6) {
        clearFocus();
      }
    }
  }
  
  public static abstract interface Formatter
  {
    public abstract String format(int paramInt);
  }
  
  class InputTextFilter
    extends NumberKeyListener
  {
    InputTextFilter() {}
    
    public CharSequence filter(CharSequence paramCharSequence, int paramInt1, int paramInt2, Spanned paramSpanned, int paramInt3, int paramInt4)
    {
      if (NumberPicker.this.mSetSelectionCommand != null) {
        NumberPicker.this.mSetSelectionCommand.cancel();
      }
      Object localObject1 = NumberPicker.this.mDisplayedValues;
      int i = 0;
      if (localObject1 == null)
      {
        localObject2 = super.filter(paramCharSequence, paramInt1, paramInt2, paramSpanned, paramInt3, paramInt4);
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = paramCharSequence.subSequence(paramInt1, paramInt2);
        }
        paramCharSequence = new StringBuilder();
        paramCharSequence.append(String.valueOf(paramSpanned.subSequence(0, paramInt3)));
        paramCharSequence.append(localObject1);
        paramCharSequence.append(paramSpanned.subSequence(paramInt4, paramSpanned.length()));
        paramCharSequence = paramCharSequence.toString();
        if ("".equals(paramCharSequence)) {
          return paramCharSequence;
        }
        if ((NumberPicker.this.getSelectedPos(paramCharSequence) <= NumberPicker.this.mMaxValue) && (paramCharSequence.length() <= String.valueOf(NumberPicker.this.mMaxValue).length())) {
          return (CharSequence)localObject1;
        }
        return "";
      }
      paramCharSequence = String.valueOf(paramCharSequence.subSequence(paramInt1, paramInt2));
      if (TextUtils.isEmpty(paramCharSequence)) {
        return "";
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append(String.valueOf(paramSpanned.subSequence(0, paramInt3)));
      ((StringBuilder)localObject1).append(paramCharSequence);
      ((StringBuilder)localObject1).append(paramSpanned.subSequence(paramInt4, paramSpanned.length()));
      paramSpanned = ((StringBuilder)localObject1).toString();
      paramCharSequence = String.valueOf(paramSpanned).toLowerCase();
      Object localObject2 = NumberPicker.this.mDisplayedValues;
      paramInt2 = localObject2.length;
      for (paramInt1 = i; paramInt1 < paramInt2; paramInt1++)
      {
        localObject1 = localObject2[paramInt1];
        if (((String)localObject1).toLowerCase().startsWith(paramCharSequence))
        {
          NumberPicker.this.postSetSelectionCommand(paramSpanned.length(), ((String)localObject1).length());
          return ((String)localObject1).subSequence(paramInt3, ((String)localObject1).length());
        }
      }
      return "";
    }
    
    protected char[] getAcceptedChars()
    {
      return NumberPicker.DIGIT_CHARACTERS;
    }
    
    public int getInputType()
    {
      return 1;
    }
  }
  
  public static abstract interface OnScrollListener
  {
    public static final int SCROLL_STATE_FLING = 2;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
    
    public abstract void onScrollStateChange(NumberPicker paramNumberPicker, int paramInt);
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface ScrollState {}
  }
  
  public static abstract interface OnValueChangeListener
  {
    public abstract void onValueChange(NumberPicker paramNumberPicker, int paramInt1, int paramInt2);
  }
  
  class PressedStateHelper
    implements Runnable
  {
    public static final int BUTTON_DECREMENT = 2;
    public static final int BUTTON_INCREMENT = 1;
    private final int MODE_PRESS = 1;
    private final int MODE_TAPPED = 2;
    private int mManagedButton;
    private int mMode;
    
    PressedStateHelper() {}
    
    public void buttonPressDelayed(int paramInt)
    {
      cancel();
      this.mMode = 1;
      this.mManagedButton = paramInt;
      NumberPicker.this.postDelayed(this, ViewConfiguration.getTapTimeout());
    }
    
    public void buttonTapped(int paramInt)
    {
      cancel();
      this.mMode = 2;
      this.mManagedButton = paramInt;
      NumberPicker.this.post(this);
    }
    
    public void cancel()
    {
      this.mMode = 0;
      this.mManagedButton = 0;
      NumberPicker.this.removeCallbacks(this);
      NumberPicker localNumberPicker;
      if (NumberPicker.this.mIncrementVirtualButtonPressed)
      {
        NumberPicker.access$1202(NumberPicker.this, false);
        localNumberPicker = NumberPicker.this;
        localNumberPicker.invalidate(0, localNumberPicker.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
      }
      NumberPicker.access$1602(NumberPicker.this, false);
      if (NumberPicker.this.mDecrementVirtualButtonPressed)
      {
        localNumberPicker = NumberPicker.this;
        localNumberPicker.invalidate(0, 0, localNumberPicker.mRight, NumberPicker.this.mTopSelectionDividerTop);
      }
    }
    
    public void run()
    {
      int i = this.mMode;
      NumberPicker localNumberPicker;
      if (i != 1)
      {
        if (i == 2)
        {
          i = this.mManagedButton;
          if (i != 1)
          {
            if (i == 2)
            {
              if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
              }
              NumberPicker.access$1680(NumberPicker.this, 1);
              localNumberPicker = NumberPicker.this;
              localNumberPicker.invalidate(0, 0, localNumberPicker.mRight, NumberPicker.this.mTopSelectionDividerTop);
            }
          }
          else
          {
            if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
              NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
            }
            NumberPicker.access$1280(NumberPicker.this, 1);
            localNumberPicker = NumberPicker.this;
            localNumberPicker.invalidate(0, localNumberPicker.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
          }
        }
      }
      else
      {
        i = this.mManagedButton;
        if (i != 1)
        {
          if (i == 2)
          {
            NumberPicker.access$1602(NumberPicker.this, true);
            localNumberPicker = NumberPicker.this;
            localNumberPicker.invalidate(0, 0, localNumberPicker.mRight, NumberPicker.this.mTopSelectionDividerTop);
          }
        }
        else
        {
          NumberPicker.access$1202(NumberPicker.this, true);
          localNumberPicker = NumberPicker.this;
          localNumberPicker.invalidate(0, localNumberPicker.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
        }
      }
    }
  }
  
  private static class SetSelectionCommand
    implements Runnable
  {
    private final EditText mInputText;
    private boolean mPosted;
    private int mSelectionEnd;
    private int mSelectionStart;
    
    public SetSelectionCommand(EditText paramEditText)
    {
      this.mInputText = paramEditText;
    }
    
    public void cancel()
    {
      if (this.mPosted)
      {
        this.mInputText.removeCallbacks(this);
        this.mPosted = false;
      }
    }
    
    public void post(int paramInt1, int paramInt2)
    {
      this.mSelectionStart = paramInt1;
      this.mSelectionEnd = paramInt2;
      if (!this.mPosted)
      {
        this.mInputText.post(this);
        this.mPosted = true;
      }
    }
    
    public void run()
    {
      this.mPosted = false;
      this.mInputText.setSelection(this.mSelectionStart, this.mSelectionEnd);
    }
  }
  
  private static class TwoDigitFormatter
    implements NumberPicker.Formatter
  {
    final Object[] mArgs = new Object[1];
    final StringBuilder mBuilder = new StringBuilder();
    Formatter mFmt;
    char mZeroDigit;
    
    TwoDigitFormatter()
    {
      init(Locale.getDefault());
    }
    
    private Formatter createFormatter(Locale paramLocale)
    {
      return new Formatter(this.mBuilder, paramLocale);
    }
    
    private static char getZeroDigit(Locale paramLocale)
    {
      return LocaleData.get(paramLocale).zeroDigit;
    }
    
    private void init(Locale paramLocale)
    {
      this.mFmt = createFormatter(paramLocale);
      this.mZeroDigit = getZeroDigit(paramLocale);
    }
    
    public String format(int paramInt)
    {
      Object localObject = Locale.getDefault();
      if (this.mZeroDigit != getZeroDigit((Locale)localObject)) {
        init((Locale)localObject);
      }
      this.mArgs[0] = Integer.valueOf(paramInt);
      localObject = this.mBuilder;
      ((StringBuilder)localObject).delete(0, ((StringBuilder)localObject).length());
      this.mFmt.format("%02d", this.mArgs);
      return this.mFmt.toString();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/NumberPicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */