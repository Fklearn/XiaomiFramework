package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.icu.util.Calendar;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View.BaseSavedState;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.autofill.AutofillValue;
import com.android.internal.R.styleable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Locale;

public class DatePicker
  extends FrameLayout
{
  private static final String LOG_TAG = DatePicker.class.getSimpleName();
  public static final int MODE_CALENDAR = 2;
  public static final int MODE_SPINNER = 1;
  @UnsupportedAppUsage
  private final DatePickerDelegate mDelegate;
  private final int mMode;
  
  public DatePicker(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DatePicker(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843612);
  }
  
  public DatePicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public DatePicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    if (getImportantForAutofill() == 0) {
      setImportantForAutofill(1);
    }
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DatePicker, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.DatePicker, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    boolean bool = localTypedArray.getBoolean(17, false);
    int i = localTypedArray.getInt(16, 1);
    int j = localTypedArray.getInt(3, 0);
    localTypedArray.recycle();
    if ((i == 2) && (bool)) {
      this.mMode = paramContext.getResources().getInteger(17694967);
    } else {
      this.mMode = i;
    }
    if (this.mMode != 2) {
      this.mDelegate = createSpinnerUIDelegate(paramContext, paramAttributeSet, paramInt1, paramInt2);
    } else {
      this.mDelegate = createCalendarUIDelegate(paramContext, paramAttributeSet, paramInt1, paramInt2);
    }
    if (j != 0) {
      setFirstDayOfWeek(j);
    }
    this.mDelegate.setAutoFillChangeListener(new _..Lambda.DatePicker.AnJPL5BrPXPJa_Oc_WUAB_HJq84(this, paramContext));
  }
  
  private DatePickerDelegate createCalendarUIDelegate(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    return new DatePickerCalendarDelegate(this, paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private DatePickerDelegate createSpinnerUIDelegate(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    return new DatePickerSpinnerDelegate(this, paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public void autofill(AutofillValue paramAutofillValue)
  {
    if (!isEnabled()) {
      return;
    }
    this.mDelegate.autofill(paramAutofillValue);
  }
  
  public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    return this.mDelegate.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
  }
  
  public void dispatchProvideAutofillStructure(ViewStructure paramViewStructure, int paramInt)
  {
    paramViewStructure.setAutofillId(getAutofillId());
    onProvideAutofillStructure(paramViewStructure, paramInt);
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchThawSelfOnly(paramSparseArray);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return DatePicker.class.getName();
  }
  
  public int getAutofillType()
  {
    int i;
    if (isEnabled()) {
      i = 4;
    } else {
      i = 0;
    }
    return i;
  }
  
  public AutofillValue getAutofillValue()
  {
    AutofillValue localAutofillValue;
    if (isEnabled()) {
      localAutofillValue = this.mDelegate.getAutofillValue();
    } else {
      localAutofillValue = null;
    }
    return localAutofillValue;
  }
  
  @Deprecated
  public CalendarView getCalendarView()
  {
    return this.mDelegate.getCalendarView();
  }
  
  @Deprecated
  public boolean getCalendarViewShown()
  {
    return this.mDelegate.getCalendarViewShown();
  }
  
  public int getDayOfMonth()
  {
    return this.mDelegate.getDayOfMonth();
  }
  
  public int getFirstDayOfWeek()
  {
    return this.mDelegate.getFirstDayOfWeek();
  }
  
  public long getMaxDate()
  {
    return this.mDelegate.getMaxDate().getTimeInMillis();
  }
  
  public long getMinDate()
  {
    return this.mDelegate.getMinDate().getTimeInMillis();
  }
  
  public int getMode()
  {
    return this.mMode;
  }
  
  public int getMonth()
  {
    return this.mDelegate.getMonth();
  }
  
  @Deprecated
  public boolean getSpinnersShown()
  {
    return this.mDelegate.getSpinnersShown();
  }
  
  public int getYear()
  {
    return this.mDelegate.getYear();
  }
  
  public void init(int paramInt1, int paramInt2, int paramInt3, OnDateChangedListener paramOnDateChangedListener)
  {
    this.mDelegate.init(paramInt1, paramInt2, paramInt3, paramOnDateChangedListener);
  }
  
  public boolean isEnabled()
  {
    return this.mDelegate.isEnabled();
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mDelegate.onConfigurationChanged(paramConfiguration);
  }
  
  public void onPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onPopulateAccessibilityEventInternal(paramAccessibilityEvent);
    this.mDelegate.onPopulateAccessibilityEvent(paramAccessibilityEvent);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (View.BaseSavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    this.mDelegate.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    Parcelable localParcelable = super.onSaveInstanceState();
    return this.mDelegate.onSaveInstanceState(localParcelable);
  }
  
  @Deprecated
  public void setCalendarViewShown(boolean paramBoolean)
  {
    this.mDelegate.setCalendarViewShown(paramBoolean);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (this.mDelegate.isEnabled() == paramBoolean) {
      return;
    }
    super.setEnabled(paramBoolean);
    this.mDelegate.setEnabled(paramBoolean);
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    if ((paramInt >= 1) && (paramInt <= 7))
    {
      this.mDelegate.setFirstDayOfWeek(paramInt);
      return;
    }
    throw new IllegalArgumentException("firstDayOfWeek must be between 1 and 7");
  }
  
  public void setMaxDate(long paramLong)
  {
    this.mDelegate.setMaxDate(paramLong);
  }
  
  public void setMinDate(long paramLong)
  {
    this.mDelegate.setMinDate(paramLong);
  }
  
  public void setOnDateChangedListener(OnDateChangedListener paramOnDateChangedListener)
  {
    this.mDelegate.setOnDateChangedListener(paramOnDateChangedListener);
  }
  
  @Deprecated
  public void setSpinnersShown(boolean paramBoolean)
  {
    this.mDelegate.setSpinnersShown(paramBoolean);
  }
  
  @UnsupportedAppUsage
  public void setValidationCallback(ValidationCallback paramValidationCallback)
  {
    this.mDelegate.setValidationCallback(paramValidationCallback);
  }
  
  public void updateDate(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mDelegate.updateDate(paramInt1, paramInt2, paramInt3);
  }
  
  static abstract class AbstractDatePickerDelegate
    implements DatePicker.DatePickerDelegate
  {
    protected DatePicker.OnDateChangedListener mAutoFillChangeListener;
    private long mAutofilledValue;
    protected Context mContext;
    protected Calendar mCurrentDate;
    protected Locale mCurrentLocale;
    protected DatePicker mDelegator;
    protected DatePicker.OnDateChangedListener mOnDateChangedListener;
    protected DatePicker.ValidationCallback mValidationCallback;
    
    public AbstractDatePickerDelegate(DatePicker paramDatePicker, Context paramContext)
    {
      this.mDelegator = paramDatePicker;
      this.mContext = paramContext;
      setCurrentLocale(Locale.getDefault());
    }
    
    public final void autofill(AutofillValue paramAutofillValue)
    {
      if ((paramAutofillValue != null) && (paramAutofillValue.isDate()))
      {
        long l = paramAutofillValue.getDateValue();
        paramAutofillValue = Calendar.getInstance(this.mCurrentLocale);
        paramAutofillValue.setTimeInMillis(l);
        updateDate(paramAutofillValue.get(1), paramAutofillValue.get(2), paramAutofillValue.get(5));
        this.mAutofilledValue = l;
        return;
      }
      String str = DatePicker.LOG_TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramAutofillValue);
      localStringBuilder.append(" could not be autofilled into ");
      localStringBuilder.append(this);
      Log.w(str, localStringBuilder.toString());
    }
    
    public final AutofillValue getAutofillValue()
    {
      long l = this.mAutofilledValue;
      if (l == 0L) {
        l = this.mCurrentDate.getTimeInMillis();
      }
      return AutofillValue.forDate(l);
    }
    
    protected String getFormattedCurrentDate()
    {
      return DateUtils.formatDateTime(this.mContext, this.mCurrentDate.getTimeInMillis(), 22);
    }
    
    protected void onLocaleChanged(Locale paramLocale) {}
    
    public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      paramAccessibilityEvent.getText().add(getFormattedCurrentDate());
    }
    
    protected void onValidationChanged(boolean paramBoolean)
    {
      DatePicker.ValidationCallback localValidationCallback = this.mValidationCallback;
      if (localValidationCallback != null) {
        localValidationCallback.onValidationChanged(paramBoolean);
      }
    }
    
    protected void resetAutofilledValue()
    {
      this.mAutofilledValue = 0L;
    }
    
    public void setAutoFillChangeListener(DatePicker.OnDateChangedListener paramOnDateChangedListener)
    {
      this.mAutoFillChangeListener = paramOnDateChangedListener;
    }
    
    protected void setCurrentLocale(Locale paramLocale)
    {
      if (!paramLocale.equals(this.mCurrentLocale))
      {
        this.mCurrentLocale = paramLocale;
        onLocaleChanged(paramLocale);
      }
    }
    
    public void setOnDateChangedListener(DatePicker.OnDateChangedListener paramOnDateChangedListener)
    {
      this.mOnDateChangedListener = paramOnDateChangedListener;
    }
    
    public void setValidationCallback(DatePicker.ValidationCallback paramValidationCallback)
    {
      this.mValidationCallback = paramValidationCallback;
    }
    
    static class SavedState
      extends View.BaseSavedState
    {
      public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
      {
        public DatePicker.AbstractDatePickerDelegate.SavedState createFromParcel(Parcel paramAnonymousParcel)
        {
          return new DatePicker.AbstractDatePickerDelegate.SavedState(paramAnonymousParcel, null);
        }
        
        public DatePicker.AbstractDatePickerDelegate.SavedState[] newArray(int paramAnonymousInt)
        {
          return new DatePicker.AbstractDatePickerDelegate.SavedState[paramAnonymousInt];
        }
      };
      private final int mCurrentView;
      private final int mListPosition;
      private final int mListPositionOffset;
      private final long mMaxDate;
      private final long mMinDate;
      private final int mSelectedDay;
      private final int mSelectedMonth;
      private final int mSelectedYear;
      
      private SavedState(Parcel paramParcel)
      {
        super();
        this.mSelectedYear = paramParcel.readInt();
        this.mSelectedMonth = paramParcel.readInt();
        this.mSelectedDay = paramParcel.readInt();
        this.mMinDate = paramParcel.readLong();
        this.mMaxDate = paramParcel.readLong();
        this.mCurrentView = paramParcel.readInt();
        this.mListPosition = paramParcel.readInt();
        this.mListPositionOffset = paramParcel.readInt();
      }
      
      public SavedState(Parcelable paramParcelable, int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2)
      {
        this(paramParcelable, paramInt1, paramInt2, paramInt3, paramLong1, paramLong2, 0, 0, 0);
      }
      
      public SavedState(Parcelable paramParcelable, int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2, int paramInt4, int paramInt5, int paramInt6)
      {
        super();
        this.mSelectedYear = paramInt1;
        this.mSelectedMonth = paramInt2;
        this.mSelectedDay = paramInt3;
        this.mMinDate = paramLong1;
        this.mMaxDate = paramLong2;
        this.mCurrentView = paramInt4;
        this.mListPosition = paramInt5;
        this.mListPositionOffset = paramInt6;
      }
      
      public int getCurrentView()
      {
        return this.mCurrentView;
      }
      
      public int getListPosition()
      {
        return this.mListPosition;
      }
      
      public int getListPositionOffset()
      {
        return this.mListPositionOffset;
      }
      
      public long getMaxDate()
      {
        return this.mMaxDate;
      }
      
      public long getMinDate()
      {
        return this.mMinDate;
      }
      
      public int getSelectedDay()
      {
        return this.mSelectedDay;
      }
      
      public int getSelectedMonth()
      {
        return this.mSelectedMonth;
      }
      
      public int getSelectedYear()
      {
        return this.mSelectedYear;
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        super.writeToParcel(paramParcel, paramInt);
        paramParcel.writeInt(this.mSelectedYear);
        paramParcel.writeInt(this.mSelectedMonth);
        paramParcel.writeInt(this.mSelectedDay);
        paramParcel.writeLong(this.mMinDate);
        paramParcel.writeLong(this.mMaxDate);
        paramParcel.writeInt(this.mCurrentView);
        paramParcel.writeInt(this.mListPosition);
        paramParcel.writeInt(this.mListPositionOffset);
      }
    }
  }
  
  static abstract interface DatePickerDelegate
  {
    public abstract void autofill(AutofillValue paramAutofillValue);
    
    public abstract boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract AutofillValue getAutofillValue();
    
    public abstract CalendarView getCalendarView();
    
    public abstract boolean getCalendarViewShown();
    
    public abstract int getDayOfMonth();
    
    public abstract int getFirstDayOfWeek();
    
    public abstract Calendar getMaxDate();
    
    public abstract Calendar getMinDate();
    
    public abstract int getMonth();
    
    public abstract boolean getSpinnersShown();
    
    public abstract int getYear();
    
    public abstract void init(int paramInt1, int paramInt2, int paramInt3, DatePicker.OnDateChangedListener paramOnDateChangedListener);
    
    public abstract boolean isEnabled();
    
    public abstract void onConfigurationChanged(Configuration paramConfiguration);
    
    public abstract void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract void onRestoreInstanceState(Parcelable paramParcelable);
    
    public abstract Parcelable onSaveInstanceState(Parcelable paramParcelable);
    
    public abstract void setAutoFillChangeListener(DatePicker.OnDateChangedListener paramOnDateChangedListener);
    
    public abstract void setCalendarViewShown(boolean paramBoolean);
    
    public abstract void setEnabled(boolean paramBoolean);
    
    public abstract void setFirstDayOfWeek(int paramInt);
    
    public abstract void setMaxDate(long paramLong);
    
    public abstract void setMinDate(long paramLong);
    
    public abstract void setOnDateChangedListener(DatePicker.OnDateChangedListener paramOnDateChangedListener);
    
    public abstract void setSpinnersShown(boolean paramBoolean);
    
    public abstract void setValidationCallback(DatePicker.ValidationCallback paramValidationCallback);
    
    public abstract void updateDate(int paramInt1, int paramInt2, int paramInt3);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface DatePickerMode {}
  
  public static abstract interface OnDateChangedListener
  {
    public abstract void onDateChanged(DatePicker paramDatePicker, int paramInt1, int paramInt2, int paramInt3);
  }
  
  public static abstract interface ValidationCallback
  {
    public abstract void onValidationChanged(boolean paramBoolean);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DatePicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */