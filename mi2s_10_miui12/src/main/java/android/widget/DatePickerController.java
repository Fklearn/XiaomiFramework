package android.widget;

import java.util.Calendar;

abstract interface DatePickerController
{
  public abstract Calendar getSelectedDay();
  
  public abstract void onYearSelected(int paramInt);
  
  public abstract void registerOnDateChangedListener(OnDateChangedListener paramOnDateChangedListener);
  
  public abstract void tryVibrate();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DatePickerController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */