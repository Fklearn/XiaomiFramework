package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.text.DisplayContext;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.IntArray;
import android.util.MathUtils;
import android.util.StateSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.internal.R.styleable;
import com.android.internal.widget.ExploreByTouchHelper;
import java.text.NumberFormat;
import java.util.Locale;
import libcore.icu.LocaleData;

class SimpleMonthView
  extends View
{
  private static final int DAYS_IN_WEEK = 7;
  private static final int DEFAULT_SELECTED_DAY = -1;
  private static final int DEFAULT_WEEK_START = 1;
  private static final int MAX_WEEKS_IN_MONTH = 6;
  private static final String MONTH_YEAR_FORMAT = "MMMMy";
  private static final int SELECTED_HIGHLIGHT_ALPHA = 176;
  private int mActivatedDay = -1;
  private final Calendar mCalendar;
  private int mCellWidth;
  private final NumberFormat mDayFormatter;
  private int mDayHeight;
  private final Paint mDayHighlightPaint = new Paint();
  private final Paint mDayHighlightSelectorPaint = new Paint();
  private int mDayOfWeekHeight;
  private final String[] mDayOfWeekLabels = new String[7];
  private final TextPaint mDayOfWeekPaint = new TextPaint();
  private int mDayOfWeekStart;
  private final TextPaint mDayPaint = new TextPaint();
  private final Paint mDaySelectorPaint = new Paint();
  private int mDaySelectorRadius;
  private ColorStateList mDayTextColor;
  private int mDaysInMonth;
  private final int mDesiredCellWidth;
  private final int mDesiredDayHeight;
  private final int mDesiredDayOfWeekHeight;
  private final int mDesiredDaySelectorRadius;
  private final int mDesiredMonthHeight;
  private int mEnabledDayEnd = 31;
  private int mEnabledDayStart = 1;
  private int mHighlightedDay = -1;
  private boolean mIsTouchHighlighted = false;
  private final Locale mLocale;
  private int mMonth;
  private int mMonthHeight;
  private final TextPaint mMonthPaint = new TextPaint();
  private String mMonthYearLabel;
  private OnDayClickListener mOnDayClickListener;
  private int mPaddedHeight;
  private int mPaddedWidth;
  private int mPreviouslyHighlightedDay = -1;
  private int mToday = -1;
  private final MonthViewTouchHelper mTouchHelper;
  private int mWeekStart = 1;
  private int mYear;
  
  public SimpleMonthView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SimpleMonthView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843612);
  }
  
  public SimpleMonthView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SimpleMonthView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = paramContext.getResources();
    this.mDesiredMonthHeight = paramContext.getDimensionPixelSize(17105094);
    this.mDesiredDayOfWeekHeight = paramContext.getDimensionPixelSize(17105089);
    this.mDesiredDayHeight = paramContext.getDimensionPixelSize(17105088);
    this.mDesiredCellWidth = paramContext.getDimensionPixelSize(17105093);
    this.mDesiredDaySelectorRadius = paramContext.getDimensionPixelSize(17105091);
    this.mTouchHelper = new MonthViewTouchHelper(this);
    setAccessibilityDelegate(this.mTouchHelper);
    setImportantForAccessibility(1);
    this.mLocale = paramContext.getConfiguration().locale;
    this.mCalendar = Calendar.getInstance(this.mLocale);
    this.mDayFormatter = NumberFormat.getIntegerInstance(this.mLocale);
    updateMonthYearLabel();
    updateDayOfWeekLabels();
    initPaints(paramContext);
  }
  
  private ColorStateList applyTextAppearance(Paint paramPaint, int paramInt)
  {
    TypedArray localTypedArray = this.mContext.obtainStyledAttributes(null, R.styleable.TextAppearance, 0, paramInt);
    Object localObject = localTypedArray.getString(12);
    if (localObject != null) {
      paramPaint.setTypeface(Typeface.create((String)localObject, 0));
    }
    paramPaint.setTextSize(localTypedArray.getDimensionPixelSize(0, (int)paramPaint.getTextSize()));
    localObject = localTypedArray.getColorStateList(3);
    if (localObject != null) {
      paramPaint.setColor(((ColorStateList)localObject).getColorForState(ENABLED_STATE_SET, 0));
    }
    localTypedArray.recycle();
    return (ColorStateList)localObject;
  }
  
  private void drawDays(Canvas paramCanvas)
  {
    TextPaint localTextPaint = this.mDayPaint;
    int i = this.mMonthHeight + this.mDayOfWeekHeight;
    int j = this.mDayHeight;
    int k = this.mCellWidth;
    float f1 = (localTextPaint.ascent() + localTextPaint.descent()) / 2.0F;
    int m = j / 2 + i;
    int n = 1;
    int i3;
    for (int i1 = findDayOffset(); n <= this.mDaysInMonth; i1 = i3)
    {
      int i2 = k * i1 + k / 2;
      if (isLayoutRtl()) {
        i2 = this.mPaddedWidth - i2;
      }
      i3 = 0;
      boolean bool = isDayEnabled(n);
      if (bool) {
        i3 = 0x0 | 0x8;
      }
      int i4 = this.mActivatedDay;
      int i5 = 1;
      if (i4 == n) {
        i4 = 1;
      } else {
        i4 = 0;
      }
      int i6;
      if (this.mHighlightedDay == n) {
        i6 = 1;
      } else {
        i6 = 0;
      }
      Object localObject;
      float f2;
      float f3;
      if (i4 != 0)
      {
        if (i6 != 0) {
          localObject = this.mDayHighlightSelectorPaint;
        } else {
          localObject = this.mDaySelectorPaint;
        }
        f2 = i2;
        f3 = m;
        i3 |= 0x20;
        paramCanvas.drawCircle(f2, f3, this.mDaySelectorRadius, (Paint)localObject);
      }
      else if (i6 != 0)
      {
        i3 |= 0x10;
        if (bool)
        {
          f2 = i2;
          f3 = m;
          float f4 = this.mDaySelectorRadius;
          paramCanvas.drawCircle(f2, f3, f4, this.mDayHighlightPaint);
        }
        else {}
      }
      if (this.mToday == n) {
        i6 = i5;
      } else {
        i6 = 0;
      }
      if ((i6 != 0) && (i4 == 0))
      {
        i3 = this.mDaySelectorPaint.getColor();
      }
      else
      {
        localObject = StateSet.get(i3);
        i3 = this.mDayTextColor.getColorForState((int[])localObject, 0);
      }
      localTextPaint.setColor(i3);
      paramCanvas.drawText(this.mDayFormatter.format(n), i2, m - f1, localTextPaint);
      i2 = i1 + 1;
      i1 = m;
      i3 = i2;
      if (i2 == 7)
      {
        i3 = 0;
        i1 = m + j;
      }
      n++;
      m = i1;
    }
  }
  
  private void drawDaysOfWeek(Canvas paramCanvas)
  {
    TextPaint localTextPaint = this.mDayOfWeekPaint;
    int i = this.mMonthHeight;
    int j = this.mDayOfWeekHeight;
    int k = this.mCellWidth;
    float f = (localTextPaint.ascent() + localTextPaint.descent()) / 2.0F;
    int m = j / 2;
    for (j = 0; j < 7; j++)
    {
      int n = k * j + k / 2;
      if (isLayoutRtl()) {
        n = this.mPaddedWidth - n;
      }
      paramCanvas.drawText(this.mDayOfWeekLabels[j], n, m + i - f, localTextPaint);
    }
  }
  
  private void drawMonth(Canvas paramCanvas)
  {
    float f1 = this.mPaddedWidth / 2.0F;
    float f2 = this.mMonthPaint.ascent();
    float f3 = this.mMonthPaint.descent();
    f3 = (this.mMonthHeight - (f2 + f3)) / 2.0F;
    paramCanvas.drawText(this.mMonthYearLabel, f1, f3, this.mMonthPaint);
  }
  
  private void ensureFocusedDay()
  {
    if (this.mHighlightedDay != -1) {
      return;
    }
    int i = this.mPreviouslyHighlightedDay;
    if (i != -1)
    {
      this.mHighlightedDay = i;
      return;
    }
    i = this.mActivatedDay;
    if (i != -1)
    {
      this.mHighlightedDay = i;
      return;
    }
    this.mHighlightedDay = 1;
  }
  
  private int findClosestColumn(Rect paramRect)
  {
    if (paramRect == null) {
      return 3;
    }
    if (this.mCellWidth == 0) {
      return 0;
    }
    int i = MathUtils.constrain((paramRect.centerX() - this.mPaddingLeft) / this.mCellWidth, 0, 6);
    if (isLayoutRtl()) {
      i = 7 - i - 1;
    }
    return i;
  }
  
  private int findClosestRow(Rect paramRect)
  {
    if (paramRect == null) {
      return 3;
    }
    if (this.mDayHeight == 0) {
      return 0;
    }
    int i = paramRect.centerY();
    paramRect = this.mDayPaint;
    int j = this.mMonthHeight;
    int k = this.mDayOfWeekHeight;
    int m = this.mDayHeight;
    float f = (paramRect.ascent() + paramRect.descent()) / 2.0F;
    int n = m / 2;
    j = Math.round((int)(i - (n + (j + k) - f)) / m);
    i = findDayOffset() + this.mDaysInMonth;
    k = i / 7;
    if (i % 7 == 0) {
      i = 1;
    } else {
      i = 0;
    }
    return MathUtils.constrain(j, 0, k - i);
  }
  
  private int findDayOffset()
  {
    int i = this.mDayOfWeekStart;
    int j = this.mWeekStart;
    int k = i - j;
    if (i < j) {
      return k + 7;
    }
    return k;
  }
  
  private int getDayAtLocation(int paramInt1, int paramInt2)
  {
    paramInt1 -= getPaddingLeft();
    if ((paramInt1 >= 0) && (paramInt1 < this.mPaddedWidth))
    {
      int i = this.mMonthHeight + this.mDayOfWeekHeight;
      paramInt2 -= getPaddingTop();
      if ((paramInt2 >= i) && (paramInt2 < this.mPaddedHeight))
      {
        if (isLayoutRtl()) {
          paramInt1 = this.mPaddedWidth - paramInt1;
        }
        paramInt1 = (paramInt2 - i) / this.mDayHeight * 7 + paramInt1 * 7 / this.mPaddedWidth + 1 - findDayOffset();
        if (!isValidDayOfMonth(paramInt1)) {
          return -1;
        }
        return paramInt1;
      }
      return -1;
    }
    return -1;
  }
  
  private static int getDaysInMonth(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      throw new IllegalArgumentException("Invalid Month");
    case 3: 
    case 5: 
    case 8: 
    case 10: 
      return 30;
    case 1: 
      if (paramInt2 % 4 == 0) {
        paramInt1 = 29;
      } else {
        paramInt1 = 28;
      }
      return paramInt1;
    }
    return 31;
  }
  
  private void initPaints(Resources paramResources)
  {
    String str1 = paramResources.getString(17039858);
    String str2 = paramResources.getString(17039848);
    String str3 = paramResources.getString(17039849);
    int i = paramResources.getDimensionPixelSize(17105095);
    int j = paramResources.getDimensionPixelSize(17105090);
    int k = paramResources.getDimensionPixelSize(17105092);
    this.mMonthPaint.setAntiAlias(true);
    this.mMonthPaint.setTextSize(i);
    this.mMonthPaint.setTypeface(Typeface.create(str1, 0));
    this.mMonthPaint.setTextAlign(Paint.Align.CENTER);
    this.mMonthPaint.setStyle(Paint.Style.FILL);
    this.mDayOfWeekPaint.setAntiAlias(true);
    this.mDayOfWeekPaint.setTextSize(j);
    this.mDayOfWeekPaint.setTypeface(Typeface.create(str2, 0));
    this.mDayOfWeekPaint.setTextAlign(Paint.Align.CENTER);
    this.mDayOfWeekPaint.setStyle(Paint.Style.FILL);
    this.mDaySelectorPaint.setAntiAlias(true);
    this.mDaySelectorPaint.setStyle(Paint.Style.FILL);
    this.mDayHighlightPaint.setAntiAlias(true);
    this.mDayHighlightPaint.setStyle(Paint.Style.FILL);
    this.mDayHighlightSelectorPaint.setAntiAlias(true);
    this.mDayHighlightSelectorPaint.setStyle(Paint.Style.FILL);
    this.mDayPaint.setAntiAlias(true);
    this.mDayPaint.setTextSize(k);
    this.mDayPaint.setTypeface(Typeface.create(str3, 0));
    this.mDayPaint.setTextAlign(Paint.Align.CENTER);
    this.mDayPaint.setStyle(Paint.Style.FILL);
  }
  
  private boolean isDayEnabled(int paramInt)
  {
    boolean bool;
    if ((paramInt >= this.mEnabledDayStart) && (paramInt <= this.mEnabledDayEnd)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isFirstDayOfWeek(int paramInt)
  {
    int i = findDayOffset();
    boolean bool = true;
    if ((i + paramInt - 1) % 7 != 0) {
      bool = false;
    }
    return bool;
  }
  
  private boolean isLastDayOfWeek(int paramInt)
  {
    boolean bool;
    if ((findDayOffset() + paramInt) % 7 == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isValidDayOfMonth(int paramInt)
  {
    boolean bool = true;
    if ((paramInt < 1) || (paramInt > this.mDaysInMonth)) {
      bool = false;
    }
    return bool;
  }
  
  private static boolean isValidDayOfWeek(int paramInt)
  {
    boolean bool = true;
    if ((paramInt < 1) || (paramInt > 7)) {
      bool = false;
    }
    return bool;
  }
  
  private static boolean isValidMonth(int paramInt)
  {
    boolean bool;
    if ((paramInt >= 0) && (paramInt <= 11)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean moveOneDay(boolean paramBoolean)
  {
    ensureFocusedDay();
    boolean bool = false;
    int i;
    if (paramBoolean)
    {
      paramBoolean = bool;
      if (!isLastDayOfWeek(this.mHighlightedDay))
      {
        i = this.mHighlightedDay;
        paramBoolean = bool;
        if (i < this.mDaysInMonth)
        {
          this.mHighlightedDay = (i + 1);
          paramBoolean = true;
        }
      }
    }
    else
    {
      paramBoolean = bool;
      if (!isFirstDayOfWeek(this.mHighlightedDay))
      {
        i = this.mHighlightedDay;
        paramBoolean = bool;
        if (i > 1)
        {
          this.mHighlightedDay = (i - 1);
          paramBoolean = true;
        }
      }
    }
    return paramBoolean;
  }
  
  private boolean onDayClicked(int paramInt)
  {
    if ((isValidDayOfMonth(paramInt)) && (isDayEnabled(paramInt)))
    {
      if (this.mOnDayClickListener != null)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(this.mYear, this.mMonth, paramInt);
        this.mOnDayClickListener.onDayClick(this, localCalendar);
      }
      this.mTouchHelper.sendEventForVirtualView(paramInt, 1);
      return true;
    }
    return false;
  }
  
  private boolean sameDay(int paramInt, Calendar paramCalendar)
  {
    int i = this.mYear;
    boolean bool = true;
    if ((i != paramCalendar.get(1)) || (this.mMonth != paramCalendar.get(2)) || (paramInt != paramCalendar.get(5))) {
      bool = false;
    }
    return bool;
  }
  
  private void updateDayOfWeekLabels()
  {
    String[] arrayOfString = LocaleData.get(this.mLocale).tinyWeekdayNames;
    for (int i = 0; i < 7; i++) {
      this.mDayOfWeekLabels[i] = arrayOfString[((this.mWeekStart + i - 1) % 7 + 1)];
    }
  }
  
  private void updateMonthYearLabel()
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(DateFormat.getBestDateTimePattern(this.mLocale, "MMMMy"), this.mLocale);
    localSimpleDateFormat.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
    this.mMonthYearLabel = localSimpleDateFormat.format(this.mCalendar.getTime());
  }
  
  public boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if ((!this.mTouchHelper.dispatchHoverEvent(paramMotionEvent)) && (!super.dispatchHoverEvent(paramMotionEvent))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean getBoundsForDay(int paramInt, Rect paramRect)
  {
    if (!isValidDayOfMonth(paramInt)) {
      return false;
    }
    int i = paramInt - 1 + findDayOffset();
    paramInt = i % 7;
    int j = this.mCellWidth;
    if (isLayoutRtl()) {
      paramInt = getWidth() - getPaddingRight() - (paramInt + 1) * j;
    } else {
      paramInt = getPaddingLeft() + paramInt * j;
    }
    int k = i / 7;
    i = this.mDayHeight;
    int m = this.mMonthHeight;
    int n = this.mDayOfWeekHeight;
    n = getPaddingTop() + (m + n) + k * i;
    paramRect.set(paramInt, n, paramInt + j, n + i);
    return true;
  }
  
  public int getCellWidth()
  {
    return this.mCellWidth;
  }
  
  public void getFocusedRect(Rect paramRect)
  {
    int i = this.mHighlightedDay;
    if (i > 0) {
      getBoundsForDay(i, paramRect);
    } else {
      super.getFocusedRect(paramRect);
    }
  }
  
  public int getMonthHeight()
  {
    return this.mMonthHeight;
  }
  
  public String getMonthYearLabel()
  {
    return this.mMonthYearLabel;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = getPaddingLeft();
    int j = getPaddingTop();
    paramCanvas.translate(i, j);
    drawMonth(paramCanvas);
    drawDaysOfWeek(paramCanvas);
    drawDays(paramCanvas);
    paramCanvas.translate(-i, -j);
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    if (paramBoolean)
    {
      int i = findDayOffset();
      int j = 1;
      if (paramInt != 17)
      {
        int k;
        if (paramInt != 33)
        {
          if (paramInt != 66)
          {
            if (paramInt == 130)
            {
              j = findClosestColumn(paramRect) - i + 1;
              if (j < 1) {
                j += 7;
              }
              this.mHighlightedDay = j;
            }
          }
          else
          {
            k = findClosestRow(paramRect);
            if (k != 0) {
              j = 1 + (k * 7 - i);
            }
            this.mHighlightedDay = j;
          }
        }
        else
        {
          j = findClosestColumn(paramRect);
          k = this.mDaysInMonth;
          j = j - i + (i + k) / 7 * 7 + 1;
          if (j > k) {
            j -= 7;
          }
          this.mHighlightedDay = j;
        }
      }
      else
      {
        j = findClosestRow(paramRect);
        this.mHighlightedDay = Math.min(this.mDaysInMonth, (j + 1) * 7 - i);
      }
      ensureFocusedDay();
      invalidate();
    }
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
  }
  
  protected void onFocusLost()
  {
    if (!this.mIsTouchHighlighted)
    {
      this.mPreviouslyHighlightedDay = this.mHighlightedDay;
      this.mHighlightedDay = -1;
      invalidate();
    }
    super.onFocusLost();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool1 = false;
    int i = paramKeyEvent.getKeyCode();
    boolean bool2;
    if (i != 61)
    {
      if (i != 66)
      {
        switch (i)
        {
        default: 
          bool2 = bool1;
          break;
        case 22: 
          bool2 = bool1;
          if (!paramKeyEvent.hasNoModifiers()) {
            break;
          }
          bool2 = moveOneDay(isLayoutRtl() ^ true);
          break;
        case 21: 
          bool2 = bool1;
          if (!paramKeyEvent.hasNoModifiers()) {
            break;
          }
          bool2 = moveOneDay(isLayoutRtl());
          break;
        case 20: 
          bool2 = bool1;
          if (!paramKeyEvent.hasNoModifiers()) {
            break;
          }
          ensureFocusedDay();
          i = this.mHighlightedDay;
          bool2 = bool1;
          if (i > this.mDaysInMonth - 7) {
            break;
          }
          this.mHighlightedDay = (i + 7);
          bool2 = true;
          break;
        case 19: 
          bool2 = bool1;
          if (!paramKeyEvent.hasNoModifiers()) {
            break;
          }
          ensureFocusedDay();
          i = this.mHighlightedDay;
          bool2 = bool1;
          if (i <= 7) {
            break;
          }
          this.mHighlightedDay = (i - 7);
          bool2 = true;
          break;
        }
      }
      else
      {
        i = this.mHighlightedDay;
        bool2 = bool1;
        if (i != -1)
        {
          onDayClicked(i);
          return true;
        }
      }
    }
    else
    {
      i = 0;
      if (paramKeyEvent.hasNoModifiers()) {
        i = 2;
      } else if (paramKeyEvent.hasModifiers(1)) {
        i = 1;
      }
      bool2 = bool1;
      if (i != 0)
      {
        ViewParent localViewParent = getParent();
        Object localObject = this;
        View localView;
        do
        {
          localView = ((View)localObject).focusSearch(i);
          if ((localView == null) || (localView == this)) {
            break;
          }
          localObject = localView;
        } while (localView.getParent() == localViewParent);
        bool2 = bool1;
        if (localView != null)
        {
          localView.requestFocus();
          return true;
        }
      }
    }
    if (bool2)
    {
      invalidate();
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!paramBoolean) {
      return;
    }
    int i = getPaddingLeft();
    int j = getPaddingTop();
    int k = getPaddingRight();
    int m = getPaddingBottom();
    paramInt1 = paramInt3 - paramInt1 - k - i;
    paramInt2 = paramInt4 - paramInt2 - m - j;
    if ((paramInt1 != this.mPaddedWidth) && (paramInt2 != this.mPaddedHeight))
    {
      this.mPaddedWidth = paramInt1;
      this.mPaddedHeight = paramInt2;
      paramInt1 = getMeasuredHeight();
      float f = paramInt2 / (paramInt1 - j - m);
      paramInt1 = (int)(this.mDesiredMonthHeight * f);
      paramInt2 = this.mPaddedWidth / 7;
      this.mMonthHeight = paramInt1;
      this.mDayOfWeekHeight = ((int)(this.mDesiredDayOfWeekHeight * f));
      this.mDayHeight = ((int)(this.mDesiredDayHeight * f));
      this.mCellWidth = paramInt2;
      paramInt1 = paramInt2 / 2;
      paramInt3 = Math.min(i, k);
      paramInt2 = this.mDayHeight / 2;
      this.mDaySelectorRadius = Math.min(this.mDesiredDaySelectorRadius, Math.min(paramInt1 + paramInt3, paramInt2 + m));
      this.mTouchHelper.invalidateRoot();
      return;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = this.mDesiredDayHeight;
    int j = this.mDesiredDayOfWeekHeight;
    int k = this.mDesiredMonthHeight;
    int m = getPaddingTop();
    int n = getPaddingBottom();
    setMeasuredDimension(resolveSize(this.mDesiredCellWidth * 7 + getPaddingStart() + getPaddingEnd(), paramInt1), resolveSize(i * 6 + j + k + m + n, paramInt2));
  }
  
  public PointerIcon onResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt)
  {
    if (!isEnabled()) {
      return null;
    }
    if (getDayAtLocation((int)(paramMotionEvent.getX() + 0.5F), (int)(paramMotionEvent.getY() + 0.5F)) >= 0) {
      return PointerIcon.getSystemIcon(getContext(), 1002);
    }
    return super.onResolvePointerIcon(paramMotionEvent, paramInt);
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    requestLayout();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = (int)(paramMotionEvent.getX() + 0.5F);
    int j = (int)(paramMotionEvent.getY() + 0.5F);
    int k = paramMotionEvent.getAction();
    if (k != 0)
    {
      if (k != 1)
      {
        if (k == 2) {
          break label80;
        }
        if (k != 3) {
          break label125;
        }
      }
      else
      {
        onDayClicked(getDayAtLocation(i, j));
      }
      this.mHighlightedDay = -1;
      this.mIsTouchHighlighted = false;
      invalidate();
      break label125;
    }
    label80:
    i = getDayAtLocation(i, j);
    this.mIsTouchHighlighted = true;
    if (this.mHighlightedDay != i)
    {
      this.mHighlightedDay = i;
      this.mPreviouslyHighlightedDay = i;
      invalidate();
    }
    if ((k == 0) && (i < 0)) {
      return false;
    }
    label125:
    return true;
  }
  
  void setDayHighlightColor(ColorStateList paramColorStateList)
  {
    int i = paramColorStateList.getColorForState(StateSet.get(24), 0);
    this.mDayHighlightPaint.setColor(i);
    invalidate();
  }
  
  public void setDayOfWeekTextAppearance(int paramInt)
  {
    applyTextAppearance(this.mDayOfWeekPaint, paramInt);
    invalidate();
  }
  
  void setDayOfWeekTextColor(ColorStateList paramColorStateList)
  {
    int i = paramColorStateList.getColorForState(ENABLED_STATE_SET, 0);
    this.mDayOfWeekPaint.setColor(i);
    invalidate();
  }
  
  void setDaySelectorColor(ColorStateList paramColorStateList)
  {
    int i = paramColorStateList.getColorForState(StateSet.get(40), 0);
    this.mDaySelectorPaint.setColor(i);
    this.mDayHighlightSelectorPaint.setColor(i);
    this.mDayHighlightSelectorPaint.setAlpha(176);
    invalidate();
  }
  
  public void setDayTextAppearance(int paramInt)
  {
    ColorStateList localColorStateList = applyTextAppearance(this.mDayPaint, paramInt);
    if (localColorStateList != null) {
      this.mDayTextColor = localColorStateList;
    }
    invalidate();
  }
  
  void setDayTextColor(ColorStateList paramColorStateList)
  {
    this.mDayTextColor = paramColorStateList;
    invalidate();
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    if (isValidDayOfWeek(paramInt)) {
      this.mWeekStart = paramInt;
    } else {
      this.mWeekStart = this.mCalendar.getFirstDayOfWeek();
    }
    updateDayOfWeekLabels();
    this.mTouchHelper.invalidateRoot();
    invalidate();
  }
  
  void setMonthParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.mActivatedDay = paramInt1;
    if (isValidMonth(paramInt2)) {
      this.mMonth = paramInt2;
    }
    this.mYear = paramInt3;
    this.mCalendar.set(2, this.mMonth);
    this.mCalendar.set(1, this.mYear);
    this.mCalendar.set(5, 1);
    this.mDayOfWeekStart = this.mCalendar.get(7);
    if (isValidDayOfWeek(paramInt4)) {
      this.mWeekStart = paramInt4;
    } else {
      this.mWeekStart = this.mCalendar.getFirstDayOfWeek();
    }
    Calendar localCalendar = Calendar.getInstance();
    this.mToday = -1;
    this.mDaysInMonth = getDaysInMonth(this.mMonth, this.mYear);
    for (paramInt1 = 0;; paramInt1++)
    {
      paramInt2 = this.mDaysInMonth;
      if (paramInt1 >= paramInt2) {
        break;
      }
      paramInt2 = paramInt1 + 1;
      if (sameDay(paramInt2, localCalendar)) {
        this.mToday = paramInt2;
      }
    }
    this.mEnabledDayStart = MathUtils.constrain(paramInt5, 1, paramInt2);
    this.mEnabledDayEnd = MathUtils.constrain(paramInt6, this.mEnabledDayStart, this.mDaysInMonth);
    updateMonthYearLabel();
    updateDayOfWeekLabels();
    this.mTouchHelper.invalidateRoot();
    invalidate();
  }
  
  public void setMonthTextAppearance(int paramInt)
  {
    applyTextAppearance(this.mMonthPaint, paramInt);
    invalidate();
  }
  
  void setMonthTextColor(ColorStateList paramColorStateList)
  {
    int i = paramColorStateList.getColorForState(ENABLED_STATE_SET, 0);
    this.mMonthPaint.setColor(i);
    invalidate();
  }
  
  public void setOnDayClickListener(OnDayClickListener paramOnDayClickListener)
  {
    this.mOnDayClickListener = paramOnDayClickListener;
  }
  
  public void setSelectedDay(int paramInt)
  {
    this.mActivatedDay = paramInt;
    this.mTouchHelper.invalidateRoot();
    invalidate();
  }
  
  private class MonthViewTouchHelper
    extends ExploreByTouchHelper
  {
    private static final String DATE_FORMAT = "dd MMMM yyyy";
    private final Calendar mTempCalendar = Calendar.getInstance();
    private final Rect mTempRect = new Rect();
    
    public MonthViewTouchHelper(View paramView)
    {
      super();
    }
    
    private CharSequence getDayDescription(int paramInt)
    {
      if (SimpleMonthView.this.isValidDayOfMonth(paramInt))
      {
        this.mTempCalendar.set(SimpleMonthView.this.mYear, SimpleMonthView.this.mMonth, paramInt);
        return DateFormat.format("dd MMMM yyyy", this.mTempCalendar.getTimeInMillis());
      }
      return "";
    }
    
    private CharSequence getDayText(int paramInt)
    {
      if (SimpleMonthView.this.isValidDayOfMonth(paramInt)) {
        return SimpleMonthView.this.mDayFormatter.format(paramInt);
      }
      return null;
    }
    
    protected int getVirtualViewAt(float paramFloat1, float paramFloat2)
    {
      int i = SimpleMonthView.this.getDayAtLocation((int)(paramFloat1 + 0.5F), (int)(0.5F + paramFloat2));
      if (i != -1) {
        return i;
      }
      return Integer.MIN_VALUE;
    }
    
    protected void getVisibleVirtualViews(IntArray paramIntArray)
    {
      for (int i = 1; i <= SimpleMonthView.this.mDaysInMonth; i++) {
        paramIntArray.add(i);
      }
    }
    
    protected boolean onPerformActionForVirtualView(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      if (paramInt2 != 16) {
        return false;
      }
      return SimpleMonthView.this.onDayClicked(paramInt1);
    }
    
    protected void onPopulateEventForVirtualView(int paramInt, AccessibilityEvent paramAccessibilityEvent)
    {
      paramAccessibilityEvent.setContentDescription(getDayDescription(paramInt));
    }
    
    protected void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      if (!SimpleMonthView.this.getBoundsForDay(paramInt, this.mTempRect))
      {
        this.mTempRect.setEmpty();
        paramAccessibilityNodeInfo.setContentDescription("");
        paramAccessibilityNodeInfo.setBoundsInParent(this.mTempRect);
        paramAccessibilityNodeInfo.setVisibleToUser(false);
        return;
      }
      paramAccessibilityNodeInfo.setText(getDayText(paramInt));
      paramAccessibilityNodeInfo.setContentDescription(getDayDescription(paramInt));
      paramAccessibilityNodeInfo.setBoundsInParent(this.mTempRect);
      boolean bool = SimpleMonthView.this.isDayEnabled(paramInt);
      if (bool) {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
      }
      paramAccessibilityNodeInfo.setEnabled(bool);
      if (paramInt == SimpleMonthView.this.mActivatedDay) {
        paramAccessibilityNodeInfo.setChecked(true);
      }
    }
  }
  
  public static abstract interface OnDayClickListener
  {
    public abstract void onDayClick(SimpleMonthView paramSimpleMonthView, Calendar paramCalendar);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SimpleMonthView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */