package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.icu.util.Calendar;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R.styleable;
import java.util.Locale;

class DatePickerCalendarDelegate
  extends DatePicker.AbstractDatePickerDelegate
{
  private static final int ANIMATION_DURATION = 300;
  private static final int[] ATTRS_DISABLED_ALPHA = { 16842803 };
  private static final int[] ATTRS_TEXT_COLOR = { 16842904 };
  private static final int DEFAULT_END_YEAR = 2100;
  private static final int DEFAULT_START_YEAR = 1900;
  private static final int UNINITIALIZED = -1;
  private static final int USE_LOCALE = 0;
  private static final int VIEW_MONTH_DAY = 0;
  private static final int VIEW_YEAR = 1;
  private ViewAnimator mAnimator;
  private ViewGroup mContainer;
  private int mCurrentView = -1;
  private DayPickerView mDayPickerView;
  private int mFirstDayOfWeek = 0;
  private TextView mHeaderMonthDay;
  private TextView mHeaderYear;
  private final Calendar mMaxDate;
  private final Calendar mMinDate;
  private DateFormat mMonthDayFormat;
  private final DayPickerView.OnDaySelectedListener mOnDaySelectedListener = new DayPickerView.OnDaySelectedListener()
  {
    public void onDaySelected(DayPickerView paramAnonymousDayPickerView, Calendar paramAnonymousCalendar)
    {
      DatePickerCalendarDelegate.this.mCurrentDate.setTimeInMillis(paramAnonymousCalendar.getTimeInMillis());
      DatePickerCalendarDelegate.this.onDateChanged(true, true);
    }
  };
  private final View.OnClickListener mOnHeaderClickListener = new _..Lambda.DatePickerCalendarDelegate.GuCiuXPsIV2EU6oKGRXrsGY_DHM(this);
  private final YearPickerView.OnYearSelectedListener mOnYearSelectedListener = new YearPickerView.OnYearSelectedListener()
  {
    public void onYearChanged(YearPickerView paramAnonymousYearPickerView, int paramAnonymousInt)
    {
      int i = DatePickerCalendarDelegate.this.mCurrentDate.get(5);
      int j = DatePickerCalendarDelegate.getDaysInMonth(DatePickerCalendarDelegate.this.mCurrentDate.get(2), paramAnonymousInt);
      if (i > j) {
        DatePickerCalendarDelegate.this.mCurrentDate.set(5, j);
      }
      DatePickerCalendarDelegate.this.mCurrentDate.set(1, paramAnonymousInt);
      DatePickerCalendarDelegate.this.onDateChanged(true, true);
      DatePickerCalendarDelegate.this.setCurrentView(0);
      DatePickerCalendarDelegate.this.mHeaderYear.requestFocus();
    }
  };
  private String mSelectDay;
  private String mSelectYear;
  private final Calendar mTempDate;
  private DateFormat mYearFormat;
  private YearPickerView mYearPickerView;
  
  public DatePickerCalendarDelegate(DatePicker paramDatePicker, Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramDatePicker, paramContext);
    paramDatePicker = this.mCurrentLocale;
    this.mCurrentDate = Calendar.getInstance(paramDatePicker);
    this.mTempDate = Calendar.getInstance(paramDatePicker);
    this.mMinDate = Calendar.getInstance(paramDatePicker);
    this.mMaxDate = Calendar.getInstance(paramDatePicker);
    this.mMinDate.set(1900, 0, 1);
    this.mMaxDate.set(2100, 11, 31);
    Resources localResources = this.mDelegator.getResources();
    paramAttributeSet = this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DatePicker, paramInt1, paramInt2);
    this.mContainer = ((ViewGroup)((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(paramAttributeSet.getResourceId(19, 17367133), this.mDelegator, false));
    this.mContainer.setSaveFromParentEnabled(false);
    this.mDelegator.addView(this.mContainer);
    ViewGroup localViewGroup = (ViewGroup)this.mContainer.findViewById(16908871);
    this.mHeaderYear = ((TextView)localViewGroup.findViewById(16908873));
    this.mHeaderYear.setOnClickListener(this.mOnHeaderClickListener);
    this.mHeaderMonthDay = ((TextView)localViewGroup.findViewById(16908872));
    this.mHeaderMonthDay.setOnClickListener(this.mOnHeaderClickListener);
    paramDatePicker = null;
    paramInt1 = paramAttributeSet.getResourceId(10, 0);
    if (paramInt1 != 0)
    {
      paramContext = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, paramInt1);
      paramDatePicker = applyLegacyColorFixes(paramContext.getColorStateList(0));
      paramContext.recycle();
    }
    paramContext = paramDatePicker;
    if (paramDatePicker == null) {
      paramContext = paramAttributeSet.getColorStateList(18);
    }
    if (paramContext != null)
    {
      this.mHeaderYear.setTextColor(paramContext);
      this.mHeaderMonthDay.setTextColor(paramContext);
    }
    if (paramAttributeSet.hasValueOrEmpty(0)) {
      localViewGroup.setBackground(paramAttributeSet.getDrawable(0));
    }
    paramAttributeSet.recycle();
    this.mAnimator = ((ViewAnimator)this.mContainer.findViewById(16908730));
    this.mDayPickerView = ((DayPickerView)this.mAnimator.findViewById(16908870));
    this.mDayPickerView.setFirstDayOfWeek(this.mFirstDayOfWeek);
    this.mDayPickerView.setMinDate(this.mMinDate.getTimeInMillis());
    this.mDayPickerView.setMaxDate(this.mMaxDate.getTimeInMillis());
    this.mDayPickerView.setDate(this.mCurrentDate.getTimeInMillis());
    this.mDayPickerView.setOnDaySelectedListener(this.mOnDaySelectedListener);
    this.mYearPickerView = ((YearPickerView)this.mAnimator.findViewById(16908874));
    this.mYearPickerView.setRange(this.mMinDate, this.mMaxDate);
    this.mYearPickerView.setYear(this.mCurrentDate.get(1));
    this.mYearPickerView.setOnYearSelectedListener(this.mOnYearSelectedListener);
    this.mSelectDay = localResources.getString(17041075);
    this.mSelectYear = localResources.getString(17041081);
    onLocaleChanged(this.mCurrentLocale);
    setCurrentView(0);
  }
  
  private ColorStateList applyLegacyColorFixes(ColorStateList paramColorStateList)
  {
    if ((paramColorStateList != null) && (!paramColorStateList.hasState(16843518)))
    {
      int i;
      int j;
      if (paramColorStateList.hasState(16842913))
      {
        i = paramColorStateList.getColorForState(StateSet.get(10), 0);
        j = paramColorStateList.getColorForState(StateSet.get(8), 0);
      }
      else
      {
        i = paramColorStateList.getDefaultColor();
        j = multiplyAlphaComponent(i, this.mContext.obtainStyledAttributes(ATTRS_DISABLED_ALPHA).getFloat(0, 0.3F));
      }
      if ((i != 0) && (j != 0)) {
        return new ColorStateList(new int[][] { { 16843518 }, new int[0] }, new int[] { i, j });
      }
      return null;
    }
    return paramColorStateList;
  }
  
  public static int getDaysInMonth(int paramInt1, int paramInt2)
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
  
  private int multiplyAlphaComponent(int paramInt, float paramFloat)
  {
    return (int)((paramInt >> 24 & 0xFF) * paramFloat + 0.5F) << 24 | 0xFFFFFF & paramInt;
  }
  
  private void onCurrentDateChanged(boolean paramBoolean)
  {
    if (this.mHeaderYear == null) {
      return;
    }
    String str = this.mYearFormat.format(this.mCurrentDate.getTime());
    this.mHeaderYear.setText(str);
    str = this.mMonthDayFormat.format(this.mCurrentDate.getTime());
    this.mHeaderMonthDay.setText(str);
    if (paramBoolean) {
      this.mAnimator.announceForAccessibility(getFormattedCurrentDate());
    }
  }
  
  private void onDateChanged(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = this.mCurrentDate.get(1);
    if ((paramBoolean2) && ((this.mOnDateChangedListener != null) || (this.mAutoFillChangeListener != null)))
    {
      int j = this.mCurrentDate.get(2);
      int k = this.mCurrentDate.get(5);
      if (this.mOnDateChangedListener != null) {
        this.mOnDateChangedListener.onDateChanged(this.mDelegator, i, j, k);
      }
      if (this.mAutoFillChangeListener != null) {
        this.mAutoFillChangeListener.onDateChanged(this.mDelegator, i, j, k);
      }
    }
    this.mDayPickerView.setDate(this.mCurrentDate.getTimeInMillis());
    this.mYearPickerView.setYear(i);
    onCurrentDateChanged(paramBoolean1);
    if (paramBoolean1) {
      tryVibrate();
    }
  }
  
  private void setCurrentView(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt == 1)
      {
        int i = this.mCurrentDate.get(1);
        this.mYearPickerView.setYear(i);
        this.mYearPickerView.post(new _..Lambda.DatePickerCalendarDelegate._6rynvAYPe1gU9xVgvSm4VMsr2M(this));
        if (this.mCurrentView != paramInt)
        {
          this.mHeaderMonthDay.setActivated(false);
          this.mHeaderYear.setActivated(true);
          this.mAnimator.setDisplayedChild(1);
          this.mCurrentView = paramInt;
        }
        this.mAnimator.announceForAccessibility(this.mSelectYear);
      }
    }
    else
    {
      this.mDayPickerView.setDate(this.mCurrentDate.getTimeInMillis());
      if (this.mCurrentView != paramInt)
      {
        this.mHeaderMonthDay.setActivated(true);
        this.mHeaderYear.setActivated(false);
        this.mAnimator.setDisplayedChild(0);
        this.mCurrentView = paramInt;
      }
      this.mAnimator.announceForAccessibility(this.mSelectDay);
    }
  }
  
  private void setDate(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mCurrentDate.set(1, paramInt1);
    this.mCurrentDate.set(2, paramInt2);
    this.mCurrentDate.set(5, paramInt3);
    resetAutofilledValue();
  }
  
  private void tryVibrate()
  {
    this.mDelegator.performHapticFeedback(5);
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    onPopulateAccessibilityEvent(paramAccessibilityEvent);
    return true;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return DatePicker.class.getName();
  }
  
  public CalendarView getCalendarView()
  {
    throw new UnsupportedOperationException("Not supported by calendar-mode DatePicker");
  }
  
  public boolean getCalendarViewShown()
  {
    return false;
  }
  
  public int getDayOfMonth()
  {
    return this.mCurrentDate.get(5);
  }
  
  public int getFirstDayOfWeek()
  {
    int i = this.mFirstDayOfWeek;
    if (i != 0) {
      return i;
    }
    return this.mCurrentDate.getFirstDayOfWeek();
  }
  
  public Calendar getMaxDate()
  {
    return this.mMaxDate;
  }
  
  public Calendar getMinDate()
  {
    return this.mMinDate;
  }
  
  public int getMonth()
  {
    return this.mCurrentDate.get(2);
  }
  
  public boolean getSpinnersShown()
  {
    return false;
  }
  
  public int getYear()
  {
    return this.mCurrentDate.get(1);
  }
  
  public void init(int paramInt1, int paramInt2, int paramInt3, DatePicker.OnDateChangedListener paramOnDateChangedListener)
  {
    setDate(paramInt1, paramInt2, paramInt3);
    onDateChanged(false, false);
    this.mOnDateChangedListener = paramOnDateChangedListener;
  }
  
  public boolean isEnabled()
  {
    return this.mContainer.isEnabled();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    setCurrentLocale(paramConfiguration.locale);
  }
  
  protected void onLocaleChanged(Locale paramLocale)
  {
    if (this.mHeaderYear == null) {
      return;
    }
    this.mMonthDayFormat = DateFormat.getInstanceForSkeleton("EMMMd", paramLocale);
    this.mMonthDayFormat.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
    this.mYearFormat = DateFormat.getInstanceForSkeleton("y", paramLocale);
    onCurrentDateChanged(false);
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof DatePicker.AbstractDatePickerDelegate.SavedState))
    {
      paramParcelable = (DatePicker.AbstractDatePickerDelegate.SavedState)paramParcelable;
      this.mCurrentDate.set(paramParcelable.getSelectedYear(), paramParcelable.getSelectedMonth(), paramParcelable.getSelectedDay());
      this.mMinDate.setTimeInMillis(paramParcelable.getMinDate());
      this.mMaxDate.setTimeInMillis(paramParcelable.getMaxDate());
      onCurrentDateChanged(false);
      int i = paramParcelable.getCurrentView();
      setCurrentView(i);
      int j = paramParcelable.getListPosition();
      if (j != -1) {
        if (i == 0)
        {
          this.mDayPickerView.setPosition(j);
        }
        else if (i == 1)
        {
          i = paramParcelable.getListPositionOffset();
          this.mYearPickerView.setSelectionFromTop(j, i);
        }
      }
    }
  }
  
  public Parcelable onSaveInstanceState(Parcelable paramParcelable)
  {
    int i = this.mCurrentDate.get(1);
    int j = this.mCurrentDate.get(2);
    int k = this.mCurrentDate.get(5);
    int m = this.mCurrentView;
    int n;
    if (m == 0)
    {
      m = this.mDayPickerView.getMostVisiblePosition();
      n = -1;
    }
    else if (m == 1)
    {
      m = this.mYearPickerView.getFirstVisiblePosition();
      n = this.mYearPickerView.getFirstPositionOffset();
    }
    else
    {
      m = -1;
      n = -1;
    }
    return new DatePicker.AbstractDatePickerDelegate.SavedState(paramParcelable, i, j, k, this.mMinDate.getTimeInMillis(), this.mMaxDate.getTimeInMillis(), this.mCurrentView, m, n);
  }
  
  public void setCalendarViewShown(boolean paramBoolean) {}
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mContainer.setEnabled(paramBoolean);
    this.mDayPickerView.setEnabled(paramBoolean);
    this.mYearPickerView.setEnabled(paramBoolean);
    this.mHeaderYear.setEnabled(paramBoolean);
    this.mHeaderMonthDay.setEnabled(paramBoolean);
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    this.mFirstDayOfWeek = paramInt;
    this.mDayPickerView.setFirstDayOfWeek(paramInt);
  }
  
  public void setMaxDate(long paramLong)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if ((this.mTempDate.get(1) == this.mMaxDate.get(1)) && (this.mTempDate.get(6) == this.mMaxDate.get(6))) {
      return;
    }
    if (this.mCurrentDate.after(this.mTempDate))
    {
      this.mCurrentDate.setTimeInMillis(paramLong);
      onDateChanged(false, true);
    }
    this.mMaxDate.setTimeInMillis(paramLong);
    this.mDayPickerView.setMaxDate(paramLong);
    this.mYearPickerView.setRange(this.mMinDate, this.mMaxDate);
  }
  
  public void setMinDate(long paramLong)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if ((this.mTempDate.get(1) == this.mMinDate.get(1)) && (this.mTempDate.get(6) == this.mMinDate.get(6))) {
      return;
    }
    if (this.mCurrentDate.before(this.mTempDate))
    {
      this.mCurrentDate.setTimeInMillis(paramLong);
      onDateChanged(false, true);
    }
    this.mMinDate.setTimeInMillis(paramLong);
    this.mDayPickerView.setMinDate(paramLong);
    this.mYearPickerView.setRange(this.mMinDate, this.mMaxDate);
  }
  
  public void setSpinnersShown(boolean paramBoolean) {}
  
  public void updateDate(int paramInt1, int paramInt2, int paramInt3)
  {
    setDate(paramInt1, paramInt2, paramInt3);
    onDateChanged(false, true);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DatePickerCalendarDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */