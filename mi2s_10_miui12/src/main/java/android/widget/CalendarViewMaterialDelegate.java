package android.widget;

import android.content.Context;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.util.AttributeSet;

class CalendarViewMaterialDelegate
  extends CalendarView.AbstractCalendarViewDelegate
{
  private final DayPickerView mDayPickerView;
  private CalendarView.OnDateChangeListener mOnDateChangeListener;
  private final DayPickerView.OnDaySelectedListener mOnDaySelectedListener = new DayPickerView.OnDaySelectedListener()
  {
    public void onDaySelected(DayPickerView paramAnonymousDayPickerView, Calendar paramAnonymousCalendar)
    {
      if (CalendarViewMaterialDelegate.this.mOnDateChangeListener != null)
      {
        int i = paramAnonymousCalendar.get(1);
        int j = paramAnonymousCalendar.get(2);
        int k = paramAnonymousCalendar.get(5);
        CalendarViewMaterialDelegate.this.mOnDateChangeListener.onSelectedDayChange(CalendarViewMaterialDelegate.this.mDelegator, i, j, k);
      }
    }
  };
  
  public CalendarViewMaterialDelegate(CalendarView paramCalendarView, Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramCalendarView, paramContext);
    this.mDayPickerView = new DayPickerView(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mDayPickerView.setOnDaySelectedListener(this.mOnDaySelectedListener);
    paramCalendarView.addView(this.mDayPickerView);
  }
  
  public boolean getBoundsForDate(long paramLong, Rect paramRect)
  {
    if (this.mDayPickerView.getBoundsForDate(paramLong, paramRect))
    {
      int[] arrayOfInt1 = new int[2];
      int[] arrayOfInt2 = new int[2];
      this.mDayPickerView.getLocationOnScreen(arrayOfInt1);
      this.mDelegator.getLocationOnScreen(arrayOfInt2);
      int i = arrayOfInt1[1] - arrayOfInt2[1];
      paramRect.top += i;
      paramRect.bottom += i;
      return true;
    }
    return false;
  }
  
  public long getDate()
  {
    return this.mDayPickerView.getDate();
  }
  
  public int getDateTextAppearance()
  {
    return this.mDayPickerView.getDayTextAppearance();
  }
  
  public int getFirstDayOfWeek()
  {
    return this.mDayPickerView.getFirstDayOfWeek();
  }
  
  public long getMaxDate()
  {
    return this.mDayPickerView.getMaxDate();
  }
  
  public long getMinDate()
  {
    return this.mDayPickerView.getMinDate();
  }
  
  public int getWeekDayTextAppearance()
  {
    return this.mDayPickerView.getDayOfWeekTextAppearance();
  }
  
  public void setDate(long paramLong)
  {
    this.mDayPickerView.setDate(paramLong, true);
  }
  
  public void setDate(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mDayPickerView.setDate(paramLong, paramBoolean1);
  }
  
  public void setDateTextAppearance(int paramInt)
  {
    this.mDayPickerView.setDayTextAppearance(paramInt);
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    this.mDayPickerView.setFirstDayOfWeek(paramInt);
  }
  
  public void setMaxDate(long paramLong)
  {
    this.mDayPickerView.setMaxDate(paramLong);
  }
  
  public void setMinDate(long paramLong)
  {
    this.mDayPickerView.setMinDate(paramLong);
  }
  
  public void setOnDateChangeListener(CalendarView.OnDateChangeListener paramOnDateChangeListener)
  {
    this.mOnDateChangeListener = paramOnDateChangeListener;
  }
  
  public void setWeekDayTextAppearance(int paramInt)
  {
    this.mDayPickerView.setDayOfWeekTextAppearance(paramInt);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CalendarViewMaterialDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */