package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.icu.util.Calendar;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R.styleable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import libcore.icu.ICU;

class DatePickerSpinnerDelegate
  extends DatePicker.AbstractDatePickerDelegate
{
  private static final String DATE_FORMAT = "MM/dd/yyyy";
  private static final boolean DEFAULT_CALENDAR_VIEW_SHOWN = true;
  private static final boolean DEFAULT_ENABLED_STATE = true;
  private static final int DEFAULT_END_YEAR = 2100;
  private static final boolean DEFAULT_SPINNERS_SHOWN = true;
  private static final int DEFAULT_START_YEAR = 1900;
  private final CalendarView mCalendarView;
  private final java.text.DateFormat mDateFormat = new SimpleDateFormat("MM/dd/yyyy");
  private final NumberPicker mDaySpinner;
  private final EditText mDaySpinnerInput;
  private boolean mIsEnabled = true;
  private Calendar mMaxDate;
  private Calendar mMinDate;
  private final NumberPicker mMonthSpinner;
  private final EditText mMonthSpinnerInput;
  private int mNumberOfMonths;
  private String[] mShortMonths;
  private final LinearLayout mSpinners;
  private Calendar mTempDate;
  private final NumberPicker mYearSpinner;
  private final EditText mYearSpinnerInput;
  
  DatePickerSpinnerDelegate(DatePicker paramDatePicker, Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramDatePicker, paramContext);
    this.mDelegator = paramDatePicker;
    this.mContext = paramContext;
    setCurrentLocale(Locale.getDefault());
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DatePicker, paramInt1, paramInt2);
    boolean bool1 = localTypedArray.getBoolean(6, true);
    boolean bool2 = localTypedArray.getBoolean(7, true);
    paramInt2 = localTypedArray.getInt(1, 1900);
    int i = localTypedArray.getInt(2, 2100);
    paramAttributeSet = localTypedArray.getString(4);
    paramDatePicker = localTypedArray.getString(5);
    paramInt1 = localTypedArray.getResourceId(20, 17367131);
    localTypedArray.recycle();
    ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(paramInt1, this.mDelegator, true).setSaveFromParentEnabled(false);
    paramContext = new NumberPicker.OnValueChangeListener()
    {
      public void onValueChange(NumberPicker paramAnonymousNumberPicker, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        DatePickerSpinnerDelegate.this.updateInputState();
        DatePickerSpinnerDelegate.this.mTempDate.setTimeInMillis(DatePickerSpinnerDelegate.this.mCurrentDate.getTimeInMillis());
        if (paramAnonymousNumberPicker == DatePickerSpinnerDelegate.this.mDaySpinner)
        {
          int i = DatePickerSpinnerDelegate.this.mTempDate.getActualMaximum(5);
          if ((paramAnonymousInt1 == i) && (paramAnonymousInt2 == 1)) {
            DatePickerSpinnerDelegate.this.mTempDate.add(5, 1);
          } else if ((paramAnonymousInt1 == 1) && (paramAnonymousInt2 == i)) {
            DatePickerSpinnerDelegate.this.mTempDate.add(5, -1);
          } else {
            DatePickerSpinnerDelegate.this.mTempDate.add(5, paramAnonymousInt2 - paramAnonymousInt1);
          }
        }
        else if (paramAnonymousNumberPicker == DatePickerSpinnerDelegate.this.mMonthSpinner)
        {
          if ((paramAnonymousInt1 == 11) && (paramAnonymousInt2 == 0)) {
            DatePickerSpinnerDelegate.this.mTempDate.add(2, 1);
          } else if ((paramAnonymousInt1 == 0) && (paramAnonymousInt2 == 11)) {
            DatePickerSpinnerDelegate.this.mTempDate.add(2, -1);
          } else {
            DatePickerSpinnerDelegate.this.mTempDate.add(2, paramAnonymousInt2 - paramAnonymousInt1);
          }
        }
        else
        {
          if (paramAnonymousNumberPicker != DatePickerSpinnerDelegate.this.mYearSpinner) {
            break label282;
          }
          DatePickerSpinnerDelegate.this.mTempDate.set(1, paramAnonymousInt2);
        }
        paramAnonymousNumberPicker = DatePickerSpinnerDelegate.this;
        paramAnonymousNumberPicker.setDate(paramAnonymousNumberPicker.mTempDate.get(1), DatePickerSpinnerDelegate.this.mTempDate.get(2), DatePickerSpinnerDelegate.this.mTempDate.get(5));
        DatePickerSpinnerDelegate.this.updateSpinners();
        DatePickerSpinnerDelegate.this.updateCalendarView();
        DatePickerSpinnerDelegate.this.notifyDateChanged();
        return;
        label282:
        throw new IllegalArgumentException();
      }
    };
    this.mSpinners = ((LinearLayout)this.mDelegator.findViewById(16909268));
    this.mCalendarView = ((CalendarView)this.mDelegator.findViewById(16908800));
    this.mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
    {
      public void onSelectedDayChange(CalendarView paramAnonymousCalendarView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        DatePickerSpinnerDelegate.this.setDate(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
        DatePickerSpinnerDelegate.this.updateSpinners();
        DatePickerSpinnerDelegate.this.notifyDateChanged();
      }
    });
    this.mDaySpinner = ((NumberPicker)this.mDelegator.findViewById(16908876));
    this.mDaySpinner.setFormatter(NumberPicker.getTwoDigitFormatter());
    this.mDaySpinner.setOnLongPressUpdateInterval(100L);
    this.mDaySpinner.setOnValueChangedListener(paramContext);
    this.mDaySpinnerInput = ((EditText)this.mDaySpinner.findViewById(16909214));
    this.mMonthSpinner = ((NumberPicker)this.mDelegator.findViewById(16909147));
    this.mMonthSpinner.setMinValue(0);
    this.mMonthSpinner.setMaxValue(this.mNumberOfMonths - 1);
    this.mMonthSpinner.setDisplayedValues(this.mShortMonths);
    this.mMonthSpinner.setOnLongPressUpdateInterval(200L);
    this.mMonthSpinner.setOnValueChangedListener(paramContext);
    this.mMonthSpinnerInput = ((EditText)this.mMonthSpinner.findViewById(16909214));
    this.mYearSpinner = ((NumberPicker)this.mDelegator.findViewById(16909616));
    this.mYearSpinner.setOnLongPressUpdateInterval(100L);
    this.mYearSpinner.setOnValueChangedListener(paramContext);
    this.mYearSpinnerInput = ((EditText)this.mYearSpinner.findViewById(16909214));
    if ((!bool1) && (!bool2))
    {
      setSpinnersShown(true);
    }
    else
    {
      setSpinnersShown(bool1);
      setCalendarViewShown(bool2);
    }
    this.mTempDate.clear();
    if (!TextUtils.isEmpty(paramAttributeSet))
    {
      if (!parseDate(paramAttributeSet, this.mTempDate)) {
        this.mTempDate.set(paramInt2, 0, 1);
      }
    }
    else {
      this.mTempDate.set(paramInt2, 0, 1);
    }
    setMinDate(this.mTempDate.getTimeInMillis());
    this.mTempDate.clear();
    if (!TextUtils.isEmpty(paramDatePicker))
    {
      if (!parseDate(paramDatePicker, this.mTempDate)) {
        this.mTempDate.set(i, 11, 31);
      }
    }
    else {
      this.mTempDate.set(i, 11, 31);
    }
    setMaxDate(this.mTempDate.getTimeInMillis());
    this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
    init(this.mCurrentDate.get(1), this.mCurrentDate.get(2), this.mCurrentDate.get(5), null);
    reorderSpinners();
    setContentDescriptions();
    if (this.mDelegator.getImportantForAccessibility() == 0) {
      this.mDelegator.setImportantForAccessibility(1);
    }
  }
  
  private Calendar getCalendarForLocale(Calendar paramCalendar, Locale paramLocale)
  {
    if (paramCalendar == null) {
      return Calendar.getInstance(paramLocale);
    }
    long l = paramCalendar.getTimeInMillis();
    paramCalendar = Calendar.getInstance(paramLocale);
    paramCalendar.setTimeInMillis(l);
    return paramCalendar;
  }
  
  private boolean isNewDate(int paramInt1, int paramInt2, int paramInt3)
  {
    Calendar localCalendar = this.mCurrentDate;
    boolean bool = true;
    if ((localCalendar.get(1) == paramInt1) && (this.mCurrentDate.get(2) == paramInt2) && (this.mCurrentDate.get(5) == paramInt3)) {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  private void notifyDateChanged()
  {
    this.mDelegator.sendAccessibilityEvent(4);
    if (this.mOnDateChangedListener != null) {
      this.mOnDateChangedListener.onDateChanged(this.mDelegator, getYear(), getMonth(), getDayOfMonth());
    }
    if (this.mAutoFillChangeListener != null) {
      this.mAutoFillChangeListener.onDateChanged(this.mDelegator, getYear(), getMonth(), getDayOfMonth());
    }
  }
  
  private boolean parseDate(String paramString, Calendar paramCalendar)
  {
    try
    {
      paramCalendar.setTime(this.mDateFormat.parse(paramString));
      return true;
    }
    catch (ParseException paramString)
    {
      paramString.printStackTrace();
    }
    return false;
  }
  
  private void reorderSpinners()
  {
    this.mSpinners.removeAllViews();
    for (int k : ICU.getDateFormatOrder(android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyyMMMdd"))) {
      if (k != 77)
      {
        if (k != 100)
        {
          if (k == 121)
          {
            this.mSpinners.addView(this.mYearSpinner);
            setImeOptions(this.mYearSpinner, ???, ???);
          }
          else
          {
            throw new IllegalArgumentException(Arrays.toString(???));
          }
        }
        else
        {
          this.mSpinners.addView(this.mDaySpinner);
          setImeOptions(this.mDaySpinner, ???, ???);
        }
      }
      else
      {
        this.mSpinners.addView(this.mMonthSpinner);
        setImeOptions(this.mMonthSpinner, ???, ???);
      }
    }
  }
  
  private void setContentDescriptions()
  {
    trySetContentDescription(this.mDaySpinner, 16909030, 17039854);
    trySetContentDescription(this.mDaySpinner, 16908881, 17039850);
    trySetContentDescription(this.mMonthSpinner, 16909030, 17039855);
    trySetContentDescription(this.mMonthSpinner, 16908881, 17039851);
    trySetContentDescription(this.mYearSpinner, 16909030, 17039856);
    trySetContentDescription(this.mYearSpinner, 16908881, 17039852);
  }
  
  @UnsupportedAppUsage
  private void setDate(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mCurrentDate.set(paramInt1, paramInt2, paramInt3);
    resetAutofilledValue();
    if (this.mCurrentDate.before(this.mMinDate)) {
      this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
    } else if (this.mCurrentDate.after(this.mMaxDate)) {
      this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
    }
  }
  
  private void setImeOptions(NumberPicker paramNumberPicker, int paramInt1, int paramInt2)
  {
    if (paramInt2 < paramInt1 - 1) {
      paramInt1 = 5;
    } else {
      paramInt1 = 6;
    }
    ((TextView)paramNumberPicker.findViewById(16909214)).setImeOptions(paramInt1);
  }
  
  private void trySetContentDescription(View paramView, int paramInt1, int paramInt2)
  {
    paramView = paramView.findViewById(paramInt1);
    if (paramView != null) {
      paramView.setContentDescription(this.mContext.getString(paramInt2));
    }
  }
  
  @UnsupportedAppUsage
  private void updateCalendarView()
  {
    this.mCalendarView.setDate(this.mCurrentDate.getTimeInMillis(), false, false);
  }
  
  @UnsupportedAppUsage
  private void updateInputState()
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)this.mContext.getSystemService(InputMethodManager.class);
    if (localInputMethodManager != null) {
      if (localInputMethodManager.isActive(this.mYearSpinnerInput))
      {
        this.mYearSpinnerInput.clearFocus();
        localInputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
      }
      else if (localInputMethodManager.isActive(this.mMonthSpinnerInput))
      {
        this.mMonthSpinnerInput.clearFocus();
        localInputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
      }
      else if (localInputMethodManager.isActive(this.mDaySpinnerInput))
      {
        this.mDaySpinnerInput.clearFocus();
        localInputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
      }
    }
  }
  
  @UnsupportedAppUsage
  private void updateSpinners()
  {
    if (this.mCurrentDate.equals(this.mMinDate))
    {
      this.mDaySpinner.setMinValue(this.mCurrentDate.get(5));
      this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
      this.mDaySpinner.setWrapSelectorWheel(false);
      this.mMonthSpinner.setDisplayedValues(null);
      this.mMonthSpinner.setMinValue(this.mCurrentDate.get(2));
      this.mMonthSpinner.setMaxValue(this.mCurrentDate.getActualMaximum(2));
      this.mMonthSpinner.setWrapSelectorWheel(false);
    }
    else if (this.mCurrentDate.equals(this.mMaxDate))
    {
      this.mDaySpinner.setMinValue(this.mCurrentDate.getActualMinimum(5));
      this.mDaySpinner.setMaxValue(this.mCurrentDate.get(5));
      this.mDaySpinner.setWrapSelectorWheel(false);
      this.mMonthSpinner.setDisplayedValues(null);
      this.mMonthSpinner.setMinValue(this.mCurrentDate.getActualMinimum(2));
      this.mMonthSpinner.setMaxValue(this.mCurrentDate.get(2));
      this.mMonthSpinner.setWrapSelectorWheel(false);
    }
    else
    {
      this.mDaySpinner.setMinValue(1);
      this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
      this.mDaySpinner.setWrapSelectorWheel(true);
      this.mMonthSpinner.setDisplayedValues(null);
      this.mMonthSpinner.setMinValue(0);
      this.mMonthSpinner.setMaxValue(11);
      this.mMonthSpinner.setWrapSelectorWheel(true);
    }
    String[] arrayOfString = (String[])Arrays.copyOfRange(this.mShortMonths, this.mMonthSpinner.getMinValue(), this.mMonthSpinner.getMaxValue() + 1);
    this.mMonthSpinner.setDisplayedValues(arrayOfString);
    this.mYearSpinner.setMinValue(this.mMinDate.get(1));
    this.mYearSpinner.setMaxValue(this.mMaxDate.get(1));
    this.mYearSpinner.setWrapSelectorWheel(false);
    this.mYearSpinner.setValue(this.mCurrentDate.get(1));
    this.mMonthSpinner.setValue(this.mCurrentDate.get(2));
    this.mDaySpinner.setValue(this.mCurrentDate.get(5));
    if (usingNumericMonths()) {
      this.mMonthSpinnerInput.setRawInputType(2);
    }
  }
  
  private boolean usingNumericMonths()
  {
    return Character.isDigit(this.mShortMonths[0].charAt(0));
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    onPopulateAccessibilityEvent(paramAccessibilityEvent);
    return true;
  }
  
  public CalendarView getCalendarView()
  {
    return this.mCalendarView;
  }
  
  public boolean getCalendarViewShown()
  {
    boolean bool;
    if (this.mCalendarView.getVisibility() == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public int getDayOfMonth()
  {
    return this.mCurrentDate.get(5);
  }
  
  public int getFirstDayOfWeek()
  {
    return this.mCalendarView.getFirstDayOfWeek();
  }
  
  public Calendar getMaxDate()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(this.mCalendarView.getMaxDate());
    return localCalendar;
  }
  
  public Calendar getMinDate()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(this.mCalendarView.getMinDate());
    return localCalendar;
  }
  
  public int getMonth()
  {
    return this.mCurrentDate.get(2);
  }
  
  public boolean getSpinnersShown()
  {
    return this.mSpinners.isShown();
  }
  
  public int getYear()
  {
    return this.mCurrentDate.get(1);
  }
  
  public void init(int paramInt1, int paramInt2, int paramInt3, DatePicker.OnDateChangedListener paramOnDateChangedListener)
  {
    setDate(paramInt1, paramInt2, paramInt3);
    updateSpinners();
    updateCalendarView();
    this.mOnDateChangedListener = paramOnDateChangedListener;
  }
  
  public boolean isEnabled()
  {
    return this.mIsEnabled;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    setCurrentLocale(paramConfiguration.locale);
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof DatePicker.AbstractDatePickerDelegate.SavedState))
    {
      paramParcelable = (DatePicker.AbstractDatePickerDelegate.SavedState)paramParcelable;
      setDate(paramParcelable.getSelectedYear(), paramParcelable.getSelectedMonth(), paramParcelable.getSelectedDay());
      updateSpinners();
      updateCalendarView();
    }
  }
  
  public Parcelable onSaveInstanceState(Parcelable paramParcelable)
  {
    return new DatePicker.AbstractDatePickerDelegate.SavedState(paramParcelable, getYear(), getMonth(), getDayOfMonth(), getMinDate().getTimeInMillis(), getMaxDate().getTimeInMillis());
  }
  
  public void setCalendarViewShown(boolean paramBoolean)
  {
    CalendarView localCalendarView = this.mCalendarView;
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 8;
    }
    localCalendarView.setVisibility(i);
  }
  
  protected void setCurrentLocale(Locale paramLocale)
  {
    super.setCurrentLocale(paramLocale);
    this.mTempDate = getCalendarForLocale(this.mTempDate, paramLocale);
    this.mMinDate = getCalendarForLocale(this.mMinDate, paramLocale);
    this.mMaxDate = getCalendarForLocale(this.mMaxDate, paramLocale);
    this.mCurrentDate = getCalendarForLocale(this.mCurrentDate, paramLocale);
    this.mNumberOfMonths = (this.mTempDate.getActualMaximum(2) + 1);
    this.mShortMonths = new DateFormatSymbols().getShortMonths();
    if (usingNumericMonths())
    {
      this.mShortMonths = new String[this.mNumberOfMonths];
      for (int i = 0; i < this.mNumberOfMonths; i++) {
        this.mShortMonths[i] = String.format("%d", new Object[] { Integer.valueOf(i + 1) });
      }
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mDaySpinner.setEnabled(paramBoolean);
    this.mMonthSpinner.setEnabled(paramBoolean);
    this.mYearSpinner.setEnabled(paramBoolean);
    this.mCalendarView.setEnabled(paramBoolean);
    this.mIsEnabled = paramBoolean;
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    this.mCalendarView.setFirstDayOfWeek(paramInt);
  }
  
  public void setMaxDate(long paramLong)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if ((this.mTempDate.get(1) == this.mMaxDate.get(1)) && (this.mTempDate.get(6) == this.mMaxDate.get(6))) {
      return;
    }
    this.mMaxDate.setTimeInMillis(paramLong);
    this.mCalendarView.setMaxDate(paramLong);
    if (this.mCurrentDate.after(this.mMaxDate))
    {
      this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
      updateCalendarView();
    }
    updateSpinners();
  }
  
  public void setMinDate(long paramLong)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if ((this.mTempDate.get(1) == this.mMinDate.get(1)) && (this.mTempDate.get(6) == this.mMinDate.get(6))) {
      return;
    }
    this.mMinDate.setTimeInMillis(paramLong);
    this.mCalendarView.setMinDate(paramLong);
    if (this.mCurrentDate.before(this.mMinDate))
    {
      this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
      updateCalendarView();
    }
    updateSpinners();
  }
  
  public void setSpinnersShown(boolean paramBoolean)
  {
    LinearLayout localLinearLayout = this.mSpinners;
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 8;
    }
    localLinearLayout.setVisibility(i);
  }
  
  public void updateDate(int paramInt1, int paramInt2, int paramInt3)
  {
    if (!isNewDate(paramInt1, paramInt2, paramInt3)) {
      return;
    }
    setDate(paramInt1, paramInt2, paramInt3);
    updateSpinners();
    updateCalendarView();
    notifyDateChanged();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DatePickerSpinnerDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */