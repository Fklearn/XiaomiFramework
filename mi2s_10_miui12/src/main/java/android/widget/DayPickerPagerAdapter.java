package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.widget.PagerAdapter;

class DayPickerPagerAdapter
  extends PagerAdapter
{
  private static final int MONTHS_IN_YEAR = 12;
  private ColorStateList mCalendarTextColor;
  private final int mCalendarViewId;
  private int mCount;
  private ColorStateList mDayHighlightColor;
  private int mDayOfWeekTextAppearance;
  private ColorStateList mDaySelectorColor;
  private int mDayTextAppearance;
  private int mFirstDayOfWeek;
  private final LayoutInflater mInflater;
  private final SparseArray<ViewHolder> mItems = new SparseArray();
  private final int mLayoutResId;
  private final Calendar mMaxDate = Calendar.getInstance();
  private final Calendar mMinDate = Calendar.getInstance();
  private int mMonthTextAppearance;
  private final SimpleMonthView.OnDayClickListener mOnDayClickListener = new SimpleMonthView.OnDayClickListener()
  {
    public void onDayClick(SimpleMonthView paramAnonymousSimpleMonthView, Calendar paramAnonymousCalendar)
    {
      if (paramAnonymousCalendar != null)
      {
        DayPickerPagerAdapter.this.setSelectedDay(paramAnonymousCalendar);
        if (DayPickerPagerAdapter.this.mOnDaySelectedListener != null) {
          DayPickerPagerAdapter.this.mOnDaySelectedListener.onDaySelected(DayPickerPagerAdapter.this, paramAnonymousCalendar);
        }
      }
    }
  };
  private OnDaySelectedListener mOnDaySelectedListener;
  private Calendar mSelectedDay = null;
  
  public DayPickerPagerAdapter(Context paramContext, int paramInt1, int paramInt2)
  {
    this.mInflater = LayoutInflater.from(paramContext);
    this.mLayoutResId = paramInt1;
    this.mCalendarViewId = paramInt2;
    paramContext = paramContext.obtainStyledAttributes(new int[] { 16843820 });
    this.mDayHighlightColor = paramContext.getColorStateList(0);
    paramContext.recycle();
  }
  
  private int getMonthForPosition(int paramInt)
  {
    return (this.mMinDate.get(2) + paramInt) % 12;
  }
  
  private int getPositionForDay(Calendar paramCalendar)
  {
    if (paramCalendar == null) {
      return -1;
    }
    return (paramCalendar.get(1) - this.mMinDate.get(1)) * 12 + (paramCalendar.get(2) - this.mMinDate.get(2));
  }
  
  private int getYearForPosition(int paramInt)
  {
    paramInt = (this.mMinDate.get(2) + paramInt) / 12;
    return this.mMinDate.get(1) + paramInt;
  }
  
  public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
  {
    paramViewGroup.removeView(((ViewHolder)paramObject).container);
    this.mItems.remove(paramInt);
  }
  
  public boolean getBoundsForDate(Calendar paramCalendar, Rect paramRect)
  {
    int i = getPositionForDay(paramCalendar);
    ViewHolder localViewHolder = (ViewHolder)this.mItems.get(i, null);
    if (localViewHolder == null) {
      return false;
    }
    i = paramCalendar.get(5);
    return localViewHolder.calendar.getBoundsForDay(i, paramRect);
  }
  
  public int getCount()
  {
    return this.mCount;
  }
  
  int getDayOfWeekTextAppearance()
  {
    return this.mDayOfWeekTextAppearance;
  }
  
  int getDayTextAppearance()
  {
    return this.mDayTextAppearance;
  }
  
  public int getFirstDayOfWeek()
  {
    return this.mFirstDayOfWeek;
  }
  
  public int getItemPosition(Object paramObject)
  {
    return ((ViewHolder)paramObject).position;
  }
  
  public CharSequence getPageTitle(int paramInt)
  {
    SimpleMonthView localSimpleMonthView = ((ViewHolder)this.mItems.get(paramInt)).calendar;
    if (localSimpleMonthView != null) {
      return localSimpleMonthView.getMonthYearLabel();
    }
    return null;
  }
  
  SimpleMonthView getView(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    return ((ViewHolder)paramObject).calendar;
  }
  
  public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
  {
    View localView = this.mInflater.inflate(this.mLayoutResId, paramViewGroup, false);
    Object localObject1 = (SimpleMonthView)localView.findViewById(this.mCalendarViewId);
    ((SimpleMonthView)localObject1).setOnDayClickListener(this.mOnDayClickListener);
    ((SimpleMonthView)localObject1).setMonthTextAppearance(this.mMonthTextAppearance);
    ((SimpleMonthView)localObject1).setDayOfWeekTextAppearance(this.mDayOfWeekTextAppearance);
    ((SimpleMonthView)localObject1).setDayTextAppearance(this.mDayTextAppearance);
    Object localObject2 = this.mDaySelectorColor;
    if (localObject2 != null) {
      ((SimpleMonthView)localObject1).setDaySelectorColor((ColorStateList)localObject2);
    }
    localObject2 = this.mDayHighlightColor;
    if (localObject2 != null) {
      ((SimpleMonthView)localObject1).setDayHighlightColor((ColorStateList)localObject2);
    }
    localObject2 = this.mCalendarTextColor;
    if (localObject2 != null)
    {
      ((SimpleMonthView)localObject1).setMonthTextColor((ColorStateList)localObject2);
      ((SimpleMonthView)localObject1).setDayOfWeekTextColor(this.mCalendarTextColor);
      ((SimpleMonthView)localObject1).setDayTextColor(this.mCalendarTextColor);
    }
    int i = getMonthForPosition(paramInt);
    int j = getYearForPosition(paramInt);
    localObject2 = this.mSelectedDay;
    int k;
    if ((localObject2 != null) && (((Calendar)localObject2).get(2) == i) && (this.mSelectedDay.get(1) == j)) {
      k = this.mSelectedDay.get(5);
    } else {
      k = -1;
    }
    int m;
    if ((this.mMinDate.get(2) == i) && (this.mMinDate.get(1) == j)) {
      m = this.mMinDate.get(5);
    } else {
      m = 1;
    }
    int n;
    if ((this.mMaxDate.get(2) == i) && (this.mMaxDate.get(1) == j)) {
      n = this.mMaxDate.get(5);
    } else {
      n = 31;
    }
    ((SimpleMonthView)localObject1).setMonthParams(k, i, j, this.mFirstDayOfWeek, m, n);
    localObject1 = new ViewHolder(paramInt, localView, (SimpleMonthView)localObject1);
    this.mItems.put(paramInt, localObject1);
    paramViewGroup.addView(localView);
    return localObject1;
  }
  
  public boolean isViewFromObject(View paramView, Object paramObject)
  {
    boolean bool;
    if (paramView == ((ViewHolder)paramObject).container) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  void setCalendarTextColor(ColorStateList paramColorStateList)
  {
    this.mCalendarTextColor = paramColorStateList;
    notifyDataSetChanged();
  }
  
  void setDayOfWeekTextAppearance(int paramInt)
  {
    this.mDayOfWeekTextAppearance = paramInt;
    notifyDataSetChanged();
  }
  
  void setDaySelectorColor(ColorStateList paramColorStateList)
  {
    this.mDaySelectorColor = paramColorStateList;
    notifyDataSetChanged();
  }
  
  void setDayTextAppearance(int paramInt)
  {
    this.mDayTextAppearance = paramInt;
    notifyDataSetChanged();
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    this.mFirstDayOfWeek = paramInt;
    int i = this.mItems.size();
    for (int j = 0; j < i; j++) {
      ((ViewHolder)this.mItems.valueAt(j)).calendar.setFirstDayOfWeek(paramInt);
    }
  }
  
  void setMonthTextAppearance(int paramInt)
  {
    this.mMonthTextAppearance = paramInt;
    notifyDataSetChanged();
  }
  
  public void setOnDaySelectedListener(OnDaySelectedListener paramOnDaySelectedListener)
  {
    this.mOnDaySelectedListener = paramOnDaySelectedListener;
  }
  
  public void setRange(Calendar paramCalendar1, Calendar paramCalendar2)
  {
    this.mMinDate.setTimeInMillis(paramCalendar1.getTimeInMillis());
    this.mMaxDate.setTimeInMillis(paramCalendar2.getTimeInMillis());
    this.mCount = ((this.mMaxDate.get(1) - this.mMinDate.get(1)) * 12 + (this.mMaxDate.get(2) - this.mMinDate.get(2)) + 1);
    notifyDataSetChanged();
  }
  
  public void setSelectedDay(Calendar paramCalendar)
  {
    int i = getPositionForDay(this.mSelectedDay);
    int j = getPositionForDay(paramCalendar);
    ViewHolder localViewHolder;
    if ((i != j) && (i >= 0))
    {
      localViewHolder = (ViewHolder)this.mItems.get(i, null);
      if (localViewHolder != null) {
        localViewHolder.calendar.setSelectedDay(-1);
      }
    }
    if (j >= 0)
    {
      localViewHolder = (ViewHolder)this.mItems.get(j, null);
      if (localViewHolder != null)
      {
        j = paramCalendar.get(5);
        localViewHolder.calendar.setSelectedDay(j);
      }
    }
    this.mSelectedDay = paramCalendar;
  }
  
  public static abstract interface OnDaySelectedListener
  {
    public abstract void onDaySelected(DayPickerPagerAdapter paramDayPickerPagerAdapter, Calendar paramCalendar);
  }
  
  private static class ViewHolder
  {
    public final SimpleMonthView calendar;
    public final View container;
    public final int position;
    
    public ViewHolder(int paramInt, View paramView, SimpleMonthView paramSimpleMonthView)
    {
      this.position = paramInt;
      this.container = paramView;
      this.calendar = paramSimpleMonthView;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DayPickerPagerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */