package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R.styleable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import libcore.icu.LocaleData;

class TimePickerSpinnerDelegate
  extends TimePicker.AbstractTimePickerDelegate
{
  private static final boolean DEFAULT_ENABLED_STATE = true;
  private static final int HOURS_IN_HALF_DAY = 12;
  private final Button mAmPmButton;
  private final NumberPicker mAmPmSpinner;
  private final EditText mAmPmSpinnerInput;
  private final String[] mAmPmStrings;
  private final TextView mDivider;
  private char mHourFormat;
  private final NumberPicker mHourSpinner;
  private final EditText mHourSpinnerInput;
  private boolean mHourWithTwoDigit;
  private boolean mIs24HourView;
  private boolean mIsAm;
  private boolean mIsEnabled = true;
  private final NumberPicker mMinuteSpinner;
  private final EditText mMinuteSpinnerInput;
  private final Calendar mTempCalendar;
  
  public TimePickerSpinnerDelegate(TimePicker paramTimePicker, Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramTimePicker, paramContext);
    paramAttributeSet = this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TimePicker, paramInt1, paramInt2);
    paramInt1 = paramAttributeSet.getResourceId(13, 17367346);
    paramAttributeSet.recycle();
    LayoutInflater.from(this.mContext).inflate(paramInt1, this.mDelegator, true).setSaveFromParentEnabled(false);
    this.mHourSpinner = ((NumberPicker)paramTimePicker.findViewById(16909003));
    this.mHourSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
    {
      public void onValueChange(NumberPicker paramAnonymousNumberPicker, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        TimePickerSpinnerDelegate.this.updateInputState();
        if ((!TimePickerSpinnerDelegate.this.is24Hour()) && (((paramAnonymousInt1 == 11) && (paramAnonymousInt2 == 12)) || ((paramAnonymousInt1 == 12) && (paramAnonymousInt2 == 11))))
        {
          paramAnonymousNumberPicker = TimePickerSpinnerDelegate.this;
          TimePickerSpinnerDelegate.access$102(paramAnonymousNumberPicker, paramAnonymousNumberPicker.mIsAm ^ true);
          TimePickerSpinnerDelegate.this.updateAmPmControl();
        }
        TimePickerSpinnerDelegate.this.onTimeChanged();
      }
    });
    this.mHourSpinnerInput = ((EditText)this.mHourSpinner.findViewById(16909214));
    this.mHourSpinnerInput.setImeOptions(5);
    this.mDivider = ((TextView)this.mDelegator.findViewById(16908894));
    if (this.mDivider != null) {
      setDividerText();
    }
    this.mMinuteSpinner = ((NumberPicker)this.mDelegator.findViewById(16909134));
    this.mMinuteSpinner.setMinValue(0);
    this.mMinuteSpinner.setMaxValue(59);
    this.mMinuteSpinner.setOnLongPressUpdateInterval(100L);
    this.mMinuteSpinner.setFormatter(NumberPicker.getTwoDigitFormatter());
    this.mMinuteSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
    {
      public void onValueChange(NumberPicker paramAnonymousNumberPicker, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        TimePickerSpinnerDelegate.this.updateInputState();
        int i = TimePickerSpinnerDelegate.this.mMinuteSpinner.getMinValue();
        int j = TimePickerSpinnerDelegate.this.mMinuteSpinner.getMaxValue();
        if ((paramAnonymousInt1 == j) && (paramAnonymousInt2 == i))
        {
          paramAnonymousInt1 = TimePickerSpinnerDelegate.this.mHourSpinner.getValue() + 1;
          if ((!TimePickerSpinnerDelegate.this.is24Hour()) && (paramAnonymousInt1 == 12))
          {
            paramAnonymousNumberPicker = TimePickerSpinnerDelegate.this;
            TimePickerSpinnerDelegate.access$102(paramAnonymousNumberPicker, paramAnonymousNumberPicker.mIsAm ^ true);
            TimePickerSpinnerDelegate.this.updateAmPmControl();
          }
          TimePickerSpinnerDelegate.this.mHourSpinner.setValue(paramAnonymousInt1);
        }
        else if ((paramAnonymousInt1 == i) && (paramAnonymousInt2 == j))
        {
          paramAnonymousInt1 = TimePickerSpinnerDelegate.this.mHourSpinner.getValue() - 1;
          if ((!TimePickerSpinnerDelegate.this.is24Hour()) && (paramAnonymousInt1 == 11))
          {
            paramAnonymousNumberPicker = TimePickerSpinnerDelegate.this;
            TimePickerSpinnerDelegate.access$102(paramAnonymousNumberPicker, paramAnonymousNumberPicker.mIsAm ^ true);
            TimePickerSpinnerDelegate.this.updateAmPmControl();
          }
          TimePickerSpinnerDelegate.this.mHourSpinner.setValue(paramAnonymousInt1);
        }
        TimePickerSpinnerDelegate.this.onTimeChanged();
      }
    });
    this.mMinuteSpinnerInput = ((EditText)this.mMinuteSpinner.findViewById(16909214));
    this.mMinuteSpinnerInput.setImeOptions(5);
    this.mAmPmStrings = getAmPmStrings(paramContext);
    paramContext = this.mDelegator.findViewById(16908725);
    if ((paramContext instanceof Button))
    {
      this.mAmPmSpinner = null;
      this.mAmPmSpinnerInput = null;
      this.mAmPmButton = ((Button)paramContext);
      this.mAmPmButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView.requestFocus();
          paramAnonymousView = TimePickerSpinnerDelegate.this;
          TimePickerSpinnerDelegate.access$102(paramAnonymousView, paramAnonymousView.mIsAm ^ true);
          TimePickerSpinnerDelegate.this.updateAmPmControl();
          TimePickerSpinnerDelegate.this.onTimeChanged();
        }
      });
    }
    else
    {
      this.mAmPmButton = null;
      this.mAmPmSpinner = ((NumberPicker)paramContext);
      this.mAmPmSpinner.setMinValue(0);
      this.mAmPmSpinner.setMaxValue(1);
      this.mAmPmSpinner.setDisplayedValues(this.mAmPmStrings);
      this.mAmPmSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
      {
        public void onValueChange(NumberPicker paramAnonymousNumberPicker, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          TimePickerSpinnerDelegate.this.updateInputState();
          paramAnonymousNumberPicker.requestFocus();
          paramAnonymousNumberPicker = TimePickerSpinnerDelegate.this;
          TimePickerSpinnerDelegate.access$102(paramAnonymousNumberPicker, paramAnonymousNumberPicker.mIsAm ^ true);
          TimePickerSpinnerDelegate.this.updateAmPmControl();
          TimePickerSpinnerDelegate.this.onTimeChanged();
        }
      });
      this.mAmPmSpinnerInput = ((EditText)this.mAmPmSpinner.findViewById(16909214));
      this.mAmPmSpinnerInput.setImeOptions(6);
    }
    if (isAmPmAtStart())
    {
      paramTimePicker = (ViewGroup)paramTimePicker.findViewById(16909502);
      paramTimePicker.removeView(paramContext);
      paramTimePicker.addView(paramContext, 0);
      paramTimePicker = (ViewGroup.MarginLayoutParams)paramContext.getLayoutParams();
      paramInt1 = paramTimePicker.getMarginStart();
      paramInt2 = paramTimePicker.getMarginEnd();
      if (paramInt1 != paramInt2)
      {
        paramTimePicker.setMarginStart(paramInt2);
        paramTimePicker.setMarginEnd(paramInt1);
      }
    }
    getHourFormatData();
    updateHourControl();
    updateMinuteControl();
    updateAmPmControl();
    this.mTempCalendar = Calendar.getInstance(this.mLocale);
    setHour(this.mTempCalendar.get(11));
    setMinute(this.mTempCalendar.get(12));
    if (!isEnabled()) {
      setEnabled(false);
    }
    setContentDescriptions();
    if (this.mDelegator.getImportantForAccessibility() == 0) {
      this.mDelegator.setImportantForAccessibility(1);
    }
  }
  
  public static String[] getAmPmStrings(Context paramContext)
  {
    Object localObject = LocaleData.get(paramContext.getResources().getConfiguration().locale);
    if (localObject.amPm[0].length() > 4) {
      paramContext = ((LocaleData)localObject).narrowAm;
    } else {
      paramContext = localObject.amPm[0];
    }
    if (localObject.amPm[1].length() > 4) {
      localObject = ((LocaleData)localObject).narrowPm;
    } else {
      localObject = localObject.amPm[1];
    }
    return new String[] { paramContext, localObject };
  }
  
  private void getHourFormatData()
  {
    Locale localLocale = this.mLocale;
    if (this.mIs24HourView) {
      str = "Hm";
    } else {
      str = "hm";
    }
    String str = DateFormat.getBestDateTimePattern(localLocale, str);
    int i = str.length();
    this.mHourWithTwoDigit = false;
    int j = 0;
    while (j < i)
    {
      int k = str.charAt(j);
      if ((k != 72) && (k != 104) && (k != 75) && (k != 107))
      {
        j++;
      }
      else
      {
        this.mHourFormat = ((char)k);
        if ((j + 1 < i) && (k == str.charAt(j + 1))) {
          this.mHourWithTwoDigit = true;
        }
      }
    }
  }
  
  private boolean isAmPmAtStart()
  {
    return DateFormat.getBestDateTimePattern(this.mLocale, "hm").startsWith("a");
  }
  
  private void onTimeChanged()
  {
    this.mDelegator.sendAccessibilityEvent(4);
    if (this.mOnTimeChangedListener != null) {
      this.mOnTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
    }
    if (this.mAutoFillChangeListener != null) {
      this.mAutoFillChangeListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
    }
  }
  
  private void setContentDescriptions()
  {
    trySetContentDescription(this.mMinuteSpinner, 16909030, 17041238);
    trySetContentDescription(this.mMinuteSpinner, 16908881, 17041232);
    trySetContentDescription(this.mHourSpinner, 16909030, 17041237);
    trySetContentDescription(this.mHourSpinner, 16908881, 17041231);
    NumberPicker localNumberPicker = this.mAmPmSpinner;
    if (localNumberPicker != null)
    {
      trySetContentDescription(localNumberPicker, 16909030, 17041239);
      trySetContentDescription(this.mAmPmSpinner, 16908881, 17041233);
    }
  }
  
  private void setCurrentHour(int paramInt, boolean paramBoolean)
  {
    if (paramInt == getHour()) {
      return;
    }
    resetAutofilledValue();
    int i = paramInt;
    if (!is24Hour())
    {
      if (paramInt >= 12)
      {
        this.mIsAm = false;
        i = paramInt;
        if (paramInt > 12) {
          i = paramInt - 12;
        }
      }
      else
      {
        this.mIsAm = true;
        i = paramInt;
        if (paramInt == 0) {
          i = 12;
        }
      }
      updateAmPmControl();
    }
    this.mHourSpinner.setValue(i);
    if (paramBoolean) {
      onTimeChanged();
    }
  }
  
  private void setCurrentMinute(int paramInt, boolean paramBoolean)
  {
    if (paramInt == getMinute()) {
      return;
    }
    resetAutofilledValue();
    this.mMinuteSpinner.setValue(paramInt);
    if (paramBoolean) {
      onTimeChanged();
    }
  }
  
  private void setDividerText()
  {
    if (this.mIs24HourView) {
      str = "Hm";
    } else {
      str = "hm";
    }
    String str = DateFormat.getBestDateTimePattern(this.mLocale, str);
    int i = str.lastIndexOf('H');
    int j = i;
    if (i == -1) {
      j = str.lastIndexOf('h');
    }
    if (j == -1)
    {
      str = ":";
    }
    else
    {
      i = str.indexOf('m', j + 1);
      if (i == -1) {
        str = Character.toString(str.charAt(j + 1));
      } else {
        str = str.substring(j + 1, i);
      }
    }
    this.mDivider.setText(str);
  }
  
  private void trySetContentDescription(View paramView, int paramInt1, int paramInt2)
  {
    paramView = paramView.findViewById(paramInt1);
    if (paramView != null) {
      paramView.setContentDescription(this.mContext.getString(paramInt2));
    }
  }
  
  private void updateAmPmControl()
  {
    NumberPicker localNumberPicker;
    if (is24Hour())
    {
      localNumberPicker = this.mAmPmSpinner;
      if (localNumberPicker != null) {
        localNumberPicker.setVisibility(8);
      } else {
        this.mAmPmButton.setVisibility(8);
      }
    }
    else
    {
      int i = this.mIsAm ^ true;
      localNumberPicker = this.mAmPmSpinner;
      if (localNumberPicker != null)
      {
        localNumberPicker.setValue(i);
        this.mAmPmSpinner.setVisibility(0);
      }
      else
      {
        this.mAmPmButton.setText(this.mAmPmStrings[i]);
        this.mAmPmButton.setVisibility(0);
      }
    }
    this.mDelegator.sendAccessibilityEvent(4);
  }
  
  private void updateHourControl()
  {
    if (is24Hour())
    {
      if (this.mHourFormat == 'k')
      {
        this.mHourSpinner.setMinValue(1);
        this.mHourSpinner.setMaxValue(24);
      }
      else
      {
        this.mHourSpinner.setMinValue(0);
        this.mHourSpinner.setMaxValue(23);
      }
    }
    else if (this.mHourFormat == 'K')
    {
      this.mHourSpinner.setMinValue(0);
      this.mHourSpinner.setMaxValue(11);
    }
    else
    {
      this.mHourSpinner.setMinValue(1);
      this.mHourSpinner.setMaxValue(12);
    }
    NumberPicker localNumberPicker = this.mHourSpinner;
    NumberPicker.Formatter localFormatter;
    if (this.mHourWithTwoDigit) {
      localFormatter = NumberPicker.getTwoDigitFormatter();
    } else {
      localFormatter = null;
    }
    localNumberPicker.setFormatter(localFormatter);
  }
  
  private void updateInputState()
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)this.mContext.getSystemService(InputMethodManager.class);
    if (localInputMethodManager != null) {
      if (localInputMethodManager.isActive(this.mHourSpinnerInput))
      {
        this.mHourSpinnerInput.clearFocus();
        localInputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
      }
      else if (localInputMethodManager.isActive(this.mMinuteSpinnerInput))
      {
        this.mMinuteSpinnerInput.clearFocus();
        localInputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
      }
      else if (localInputMethodManager.isActive(this.mAmPmSpinnerInput))
      {
        this.mAmPmSpinnerInput.clearFocus();
        localInputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
      }
    }
  }
  
  private void updateMinuteControl()
  {
    if (is24Hour()) {
      this.mMinuteSpinnerInput.setImeOptions(6);
    } else {
      this.mMinuteSpinnerInput.setImeOptions(5);
    }
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    onPopulateAccessibilityEvent(paramAccessibilityEvent);
    return true;
  }
  
  public View getAmView()
  {
    return this.mAmPmSpinnerInput;
  }
  
  public int getBaseline()
  {
    return this.mHourSpinner.getBaseline();
  }
  
  public int getHour()
  {
    int i = this.mHourSpinner.getValue();
    if (is24Hour()) {
      return i;
    }
    if (this.mIsAm) {
      return i % 12;
    }
    return i % 12 + 12;
  }
  
  public View getHourView()
  {
    return this.mHourSpinnerInput;
  }
  
  public int getMinute()
  {
    return this.mMinuteSpinner.getValue();
  }
  
  public View getMinuteView()
  {
    return this.mMinuteSpinnerInput;
  }
  
  public View getPmView()
  {
    return this.mAmPmSpinnerInput;
  }
  
  public boolean is24Hour()
  {
    return this.mIs24HourView;
  }
  
  public boolean isEnabled()
  {
    return this.mIsEnabled;
  }
  
  public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    int i;
    if (this.mIs24HourView) {
      i = 0x1 | 0x80;
    } else {
      i = 0x1 | 0x40;
    }
    this.mTempCalendar.set(11, getHour());
    this.mTempCalendar.set(12, getMinute());
    String str = DateUtils.formatDateTime(this.mContext, this.mTempCalendar.getTimeInMillis(), i);
    paramAccessibilityEvent.getText().add(str);
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof TimePicker.AbstractTimePickerDelegate.SavedState))
    {
      paramParcelable = (TimePicker.AbstractTimePickerDelegate.SavedState)paramParcelable;
      setHour(paramParcelable.getHour());
      setMinute(paramParcelable.getMinute());
    }
  }
  
  public Parcelable onSaveInstanceState(Parcelable paramParcelable)
  {
    return new TimePicker.AbstractTimePickerDelegate.SavedState(paramParcelable, getHour(), getMinute(), is24Hour());
  }
  
  public void setDate(int paramInt1, int paramInt2)
  {
    setCurrentHour(paramInt1, false);
    setCurrentMinute(paramInt2, false);
    onTimeChanged();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mMinuteSpinner.setEnabled(paramBoolean);
    Object localObject = this.mDivider;
    if (localObject != null) {
      ((TextView)localObject).setEnabled(paramBoolean);
    }
    this.mHourSpinner.setEnabled(paramBoolean);
    localObject = this.mAmPmSpinner;
    if (localObject != null) {
      ((NumberPicker)localObject).setEnabled(paramBoolean);
    } else {
      this.mAmPmButton.setEnabled(paramBoolean);
    }
    this.mIsEnabled = paramBoolean;
  }
  
  public void setHour(int paramInt)
  {
    setCurrentHour(paramInt, true);
  }
  
  public void setIs24Hour(boolean paramBoolean)
  {
    if (this.mIs24HourView == paramBoolean) {
      return;
    }
    int i = getHour();
    this.mIs24HourView = paramBoolean;
    getHourFormatData();
    updateHourControl();
    setCurrentHour(i, false);
    updateMinuteControl();
    updateAmPmControl();
  }
  
  public void setMinute(int paramInt)
  {
    setCurrentMinute(paramInt, true);
  }
  
  public boolean validateInput()
  {
    return true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TimePickerSpinnerDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */