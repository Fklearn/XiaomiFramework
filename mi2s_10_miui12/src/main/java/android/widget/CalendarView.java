package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R.styleable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarView
  extends FrameLayout
{
  private static final String DATE_FORMAT = "MM/dd/yyyy";
  private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
  private static final String LOG_TAG = "CalendarView";
  private static final int MODE_HOLO = 0;
  private static final int MODE_MATERIAL = 1;
  @UnsupportedAppUsage
  private final CalendarViewDelegate mDelegate;
  
  public CalendarView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CalendarView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843613);
  }
  
  public CalendarView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public CalendarView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CalendarView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.CalendarView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    int i = localTypedArray.getInt(13, 0);
    localTypedArray.recycle();
    if (i != 0)
    {
      if (i == 1) {
        this.mDelegate = new CalendarViewMaterialDelegate(this, paramContext, paramAttributeSet, paramInt1, paramInt2);
      } else {
        throw new IllegalArgumentException("invalid calendarViewMode attribute");
      }
    }
    else {
      this.mDelegate = new CalendarViewLegacyDelegate(this, paramContext, paramAttributeSet, paramInt1, paramInt2);
    }
  }
  
  public static boolean parseDate(String paramString, Calendar paramCalendar)
  {
    if ((paramString != null) && (!paramString.isEmpty())) {
      try
      {
        paramCalendar.setTime(DATE_FORMATTER.parse(paramString));
        return true;
      }
      catch (ParseException paramCalendar)
      {
        paramCalendar = new StringBuilder();
        paramCalendar.append("Date: ");
        paramCalendar.append(paramString);
        paramCalendar.append(" not in format: ");
        paramCalendar.append("MM/dd/yyyy");
        Log.w("CalendarView", paramCalendar.toString());
        return false;
      }
    }
    return false;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return CalendarView.class.getName();
  }
  
  public boolean getBoundsForDate(long paramLong, Rect paramRect)
  {
    return this.mDelegate.getBoundsForDate(paramLong, paramRect);
  }
  
  public long getDate()
  {
    return this.mDelegate.getDate();
  }
  
  public int getDateTextAppearance()
  {
    return this.mDelegate.getDateTextAppearance();
  }
  
  public int getFirstDayOfWeek()
  {
    return this.mDelegate.getFirstDayOfWeek();
  }
  
  @Deprecated
  public int getFocusedMonthDateColor()
  {
    return this.mDelegate.getFocusedMonthDateColor();
  }
  
  public long getMaxDate()
  {
    return this.mDelegate.getMaxDate();
  }
  
  public long getMinDate()
  {
    return this.mDelegate.getMinDate();
  }
  
  @Deprecated
  public Drawable getSelectedDateVerticalBar()
  {
    return this.mDelegate.getSelectedDateVerticalBar();
  }
  
  @Deprecated
  public int getSelectedWeekBackgroundColor()
  {
    return this.mDelegate.getSelectedWeekBackgroundColor();
  }
  
  @Deprecated
  public boolean getShowWeekNumber()
  {
    return this.mDelegate.getShowWeekNumber();
  }
  
  @Deprecated
  public int getShownWeekCount()
  {
    return this.mDelegate.getShownWeekCount();
  }
  
  @Deprecated
  public int getUnfocusedMonthDateColor()
  {
    return this.mDelegate.getUnfocusedMonthDateColor();
  }
  
  public int getWeekDayTextAppearance()
  {
    return this.mDelegate.getWeekDayTextAppearance();
  }
  
  @Deprecated
  public int getWeekNumberColor()
  {
    return this.mDelegate.getWeekNumberColor();
  }
  
  @Deprecated
  public int getWeekSeparatorLineColor()
  {
    return this.mDelegate.getWeekSeparatorLineColor();
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    this.mDelegate.onConfigurationChanged(paramConfiguration);
  }
  
  public void setDate(long paramLong)
  {
    this.mDelegate.setDate(paramLong);
  }
  
  public void setDate(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mDelegate.setDate(paramLong, paramBoolean1, paramBoolean2);
  }
  
  public void setDateTextAppearance(int paramInt)
  {
    this.mDelegate.setDateTextAppearance(paramInt);
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    this.mDelegate.setFirstDayOfWeek(paramInt);
  }
  
  @Deprecated
  public void setFocusedMonthDateColor(int paramInt)
  {
    this.mDelegate.setFocusedMonthDateColor(paramInt);
  }
  
  public void setMaxDate(long paramLong)
  {
    this.mDelegate.setMaxDate(paramLong);
  }
  
  public void setMinDate(long paramLong)
  {
    this.mDelegate.setMinDate(paramLong);
  }
  
  public void setOnDateChangeListener(OnDateChangeListener paramOnDateChangeListener)
  {
    this.mDelegate.setOnDateChangeListener(paramOnDateChangeListener);
  }
  
  @Deprecated
  public void setSelectedDateVerticalBar(int paramInt)
  {
    this.mDelegate.setSelectedDateVerticalBar(paramInt);
  }
  
  @Deprecated
  public void setSelectedDateVerticalBar(Drawable paramDrawable)
  {
    this.mDelegate.setSelectedDateVerticalBar(paramDrawable);
  }
  
  @Deprecated
  public void setSelectedWeekBackgroundColor(int paramInt)
  {
    this.mDelegate.setSelectedWeekBackgroundColor(paramInt);
  }
  
  @Deprecated
  public void setShowWeekNumber(boolean paramBoolean)
  {
    this.mDelegate.setShowWeekNumber(paramBoolean);
  }
  
  @Deprecated
  public void setShownWeekCount(int paramInt)
  {
    this.mDelegate.setShownWeekCount(paramInt);
  }
  
  @Deprecated
  public void setUnfocusedMonthDateColor(int paramInt)
  {
    this.mDelegate.setUnfocusedMonthDateColor(paramInt);
  }
  
  public void setWeekDayTextAppearance(int paramInt)
  {
    this.mDelegate.setWeekDayTextAppearance(paramInt);
  }
  
  @Deprecated
  public void setWeekNumberColor(int paramInt)
  {
    this.mDelegate.setWeekNumberColor(paramInt);
  }
  
  @Deprecated
  public void setWeekSeparatorLineColor(int paramInt)
  {
    this.mDelegate.setWeekSeparatorLineColor(paramInt);
  }
  
  static abstract class AbstractCalendarViewDelegate
    implements CalendarView.CalendarViewDelegate
  {
    protected static final String DEFAULT_MAX_DATE = "01/01/2100";
    protected static final String DEFAULT_MIN_DATE = "01/01/1900";
    protected Context mContext;
    protected Locale mCurrentLocale;
    protected CalendarView mDelegator;
    
    AbstractCalendarViewDelegate(CalendarView paramCalendarView, Context paramContext)
    {
      this.mDelegator = paramCalendarView;
      this.mContext = paramContext;
      setCurrentLocale(Locale.getDefault());
    }
    
    public int getFocusedMonthDateColor()
    {
      return 0;
    }
    
    public Drawable getSelectedDateVerticalBar()
    {
      return null;
    }
    
    public int getSelectedWeekBackgroundColor()
    {
      return 0;
    }
    
    public boolean getShowWeekNumber()
    {
      return false;
    }
    
    public int getShownWeekCount()
    {
      return 0;
    }
    
    public int getUnfocusedMonthDateColor()
    {
      return 0;
    }
    
    public int getWeekNumberColor()
    {
      return 0;
    }
    
    public int getWeekSeparatorLineColor()
    {
      return 0;
    }
    
    public void onConfigurationChanged(Configuration paramConfiguration) {}
    
    protected void setCurrentLocale(Locale paramLocale)
    {
      if (paramLocale.equals(this.mCurrentLocale)) {
        return;
      }
      this.mCurrentLocale = paramLocale;
    }
    
    public void setFocusedMonthDateColor(int paramInt) {}
    
    public void setSelectedDateVerticalBar(int paramInt) {}
    
    public void setSelectedDateVerticalBar(Drawable paramDrawable) {}
    
    public void setSelectedWeekBackgroundColor(int paramInt) {}
    
    public void setShowWeekNumber(boolean paramBoolean) {}
    
    public void setShownWeekCount(int paramInt) {}
    
    public void setUnfocusedMonthDateColor(int paramInt) {}
    
    public void setWeekNumberColor(int paramInt) {}
    
    public void setWeekSeparatorLineColor(int paramInt) {}
  }
  
  private static abstract interface CalendarViewDelegate
  {
    public abstract boolean getBoundsForDate(long paramLong, Rect paramRect);
    
    public abstract long getDate();
    
    public abstract int getDateTextAppearance();
    
    public abstract int getFirstDayOfWeek();
    
    public abstract int getFocusedMonthDateColor();
    
    public abstract long getMaxDate();
    
    public abstract long getMinDate();
    
    public abstract Drawable getSelectedDateVerticalBar();
    
    public abstract int getSelectedWeekBackgroundColor();
    
    public abstract boolean getShowWeekNumber();
    
    public abstract int getShownWeekCount();
    
    public abstract int getUnfocusedMonthDateColor();
    
    public abstract int getWeekDayTextAppearance();
    
    public abstract int getWeekNumberColor();
    
    public abstract int getWeekSeparatorLineColor();
    
    public abstract void onConfigurationChanged(Configuration paramConfiguration);
    
    public abstract void setDate(long paramLong);
    
    public abstract void setDate(long paramLong, boolean paramBoolean1, boolean paramBoolean2);
    
    public abstract void setDateTextAppearance(int paramInt);
    
    public abstract void setFirstDayOfWeek(int paramInt);
    
    public abstract void setFocusedMonthDateColor(int paramInt);
    
    public abstract void setMaxDate(long paramLong);
    
    public abstract void setMinDate(long paramLong);
    
    public abstract void setOnDateChangeListener(CalendarView.OnDateChangeListener paramOnDateChangeListener);
    
    public abstract void setSelectedDateVerticalBar(int paramInt);
    
    public abstract void setSelectedDateVerticalBar(Drawable paramDrawable);
    
    public abstract void setSelectedWeekBackgroundColor(int paramInt);
    
    public abstract void setShowWeekNumber(boolean paramBoolean);
    
    public abstract void setShownWeekCount(int paramInt);
    
    public abstract void setUnfocusedMonthDateColor(int paramInt);
    
    public abstract void setWeekDayTextAppearance(int paramInt);
    
    public abstract void setWeekNumberColor(int paramInt);
    
    public abstract void setWeekSeparatorLineColor(int paramInt);
  }
  
  public static abstract interface OnDateChangeListener
  {
    public abstract void onSelectedDayChange(CalendarView paramCalendarView, int paramInt1, int paramInt2, int paramInt3);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CalendarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */