package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.R.styleable;
import com.android.internal.widget.ViewPager;
import com.android.internal.widget.ViewPager.OnPageChangeListener;
import java.util.Locale;
import libcore.icu.LocaleData;

class DayPickerView
  extends ViewGroup
{
  private static final int[] ATTRS_TEXT_COLOR = { 16842904 };
  private static final int DEFAULT_END_YEAR = 2100;
  private static final int DEFAULT_LAYOUT = 17367136;
  private static final int DEFAULT_START_YEAR = 1900;
  private final AccessibilityManager mAccessibilityManager;
  private final DayPickerPagerAdapter mAdapter;
  private final Calendar mMaxDate = Calendar.getInstance();
  private final Calendar mMinDate = Calendar.getInstance();
  private final ImageButton mNextButton;
  private final View.OnClickListener mOnClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i;
      if (paramAnonymousView == DayPickerView.this.mPrevButton)
      {
        i = -1;
      }
      else
      {
        if (paramAnonymousView != DayPickerView.this.mNextButton) {
          return;
        }
        i = 1;
      }
      boolean bool = DayPickerView.this.mAccessibilityManager.isEnabled();
      int j = DayPickerView.this.mViewPager.getCurrentItem();
      DayPickerView.this.mViewPager.setCurrentItem(j + i, bool ^ true);
      return;
    }
  };
  private OnDaySelectedListener mOnDaySelectedListener;
  private final ViewPager.OnPageChangeListener mOnPageChangedListener = new ViewPager.OnPageChangeListener()
  {
    public void onPageScrollStateChanged(int paramAnonymousInt) {}
    
    public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2)
    {
      paramAnonymousFloat = Math.abs(0.5F - paramAnonymousFloat) * 2.0F;
      DayPickerView.this.mPrevButton.setAlpha(paramAnonymousFloat);
      DayPickerView.this.mNextButton.setAlpha(paramAnonymousFloat);
    }
    
    public void onPageSelected(int paramAnonymousInt)
    {
      DayPickerView.this.updateButtonVisibility(paramAnonymousInt);
    }
  };
  private final ImageButton mPrevButton;
  private final Calendar mSelectedDay = Calendar.getInstance();
  private Calendar mTempCalendar;
  private final ViewPager mViewPager;
  
  public DayPickerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DayPickerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843613);
  }
  
  public DayPickerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public DayPickerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mAccessibilityManager = ((AccessibilityManager)paramContext.getSystemService("accessibility"));
    Object localObject1 = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CalendarView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.CalendarView, paramAttributeSet, (TypedArray)localObject1, paramInt1, paramInt2);
    paramInt1 = ((TypedArray)localObject1).getInt(0, LocaleData.get(Locale.getDefault()).firstDayOfWeek.intValue());
    paramAttributeSet = ((TypedArray)localObject1).getString(2);
    String str = ((TypedArray)localObject1).getString(3);
    int i = ((TypedArray)localObject1).getResourceId(16, 16974788);
    int j = ((TypedArray)localObject1).getResourceId(11, 16974787);
    paramInt2 = ((TypedArray)localObject1).getResourceId(12, 16974786);
    Object localObject2 = ((TypedArray)localObject1).getColorStateList(15);
    ((TypedArray)localObject1).recycle();
    this.mAdapter = new DayPickerPagerAdapter(paramContext, 17367134, 16909149);
    this.mAdapter.setMonthTextAppearance(i);
    this.mAdapter.setDayOfWeekTextAppearance(j);
    this.mAdapter.setDayTextAppearance(paramInt2);
    this.mAdapter.setDaySelectorColor((ColorStateList)localObject2);
    paramContext = LayoutInflater.from(paramContext);
    localObject1 = (ViewGroup)paramContext.inflate(17367136, this, false);
    while (((ViewGroup)localObject1).getChildCount() > 0)
    {
      localObject2 = ((ViewGroup)localObject1).getChildAt(0);
      ((ViewGroup)localObject1).removeViewAt(0);
      addView((View)localObject2);
    }
    this.mPrevButton = ((ImageButton)findViewById(16909289));
    this.mPrevButton.setOnClickListener(this.mOnClickListener);
    this.mNextButton = ((ImageButton)findViewById(16909167));
    this.mNextButton.setOnClickListener(this.mOnClickListener);
    this.mViewPager = ((ViewPager)findViewById(16908878));
    this.mViewPager.setAdapter(this.mAdapter);
    this.mViewPager.setOnPageChangeListener(this.mOnPageChangedListener);
    if (i != 0)
    {
      localObject1 = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, i);
      paramContext = ((TypedArray)localObject1).getColorStateList(0);
      if (paramContext != null)
      {
        this.mPrevButton.setImageTintList(paramContext);
        this.mNextButton.setImageTintList(paramContext);
      }
      ((TypedArray)localObject1).recycle();
    }
    paramContext = Calendar.getInstance();
    if (!CalendarView.parseDate(paramAttributeSet, paramContext)) {
      paramContext.set(1900, 0, 1);
    }
    long l1 = paramContext.getTimeInMillis();
    if (!CalendarView.parseDate(str, paramContext)) {
      paramContext.set(2100, 11, 31);
    }
    long l2 = paramContext.getTimeInMillis();
    if (l2 >= l1)
    {
      long l3 = MathUtils.constrain(System.currentTimeMillis(), l1, l2);
      setFirstDayOfWeek(paramInt1);
      setMinDate(l1);
      setMaxDate(l2);
      setDate(l3, false);
      this.mAdapter.setOnDaySelectedListener(new DayPickerPagerAdapter.OnDaySelectedListener()
      {
        public void onDaySelected(DayPickerPagerAdapter paramAnonymousDayPickerPagerAdapter, Calendar paramAnonymousCalendar)
        {
          if (DayPickerView.this.mOnDaySelectedListener != null) {
            DayPickerView.this.mOnDaySelectedListener.onDaySelected(DayPickerView.this, paramAnonymousCalendar);
          }
        }
      });
      return;
    }
    throw new IllegalArgumentException("maxDate must be >= minDate");
  }
  
  private int getDiffMonths(Calendar paramCalendar1, Calendar paramCalendar2)
  {
    int i = paramCalendar2.get(1);
    int j = paramCalendar1.get(1);
    return paramCalendar2.get(2) - paramCalendar1.get(2) + (i - j) * 12;
  }
  
  private int getPositionFromDay(long paramLong)
  {
    int i = getDiffMonths(this.mMinDate, this.mMaxDate);
    return MathUtils.constrain(getDiffMonths(this.mMinDate, getTempCalendarForTime(paramLong)), 0, i);
  }
  
  private Calendar getTempCalendarForTime(long paramLong)
  {
    if (this.mTempCalendar == null) {
      this.mTempCalendar = Calendar.getInstance();
    }
    this.mTempCalendar.setTimeInMillis(paramLong);
    return this.mTempCalendar;
  }
  
  private void setDate(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    long l;
    if (paramLong < this.mMinDate.getTimeInMillis())
    {
      l = this.mMinDate.getTimeInMillis();
      i = 1;
    }
    else
    {
      l = paramLong;
      if (paramLong > this.mMaxDate.getTimeInMillis())
      {
        l = this.mMaxDate.getTimeInMillis();
        i = 1;
      }
    }
    getTempCalendarForTime(l);
    if ((paramBoolean2) || (i != 0)) {
      this.mSelectedDay.setTimeInMillis(l);
    }
    i = getPositionFromDay(l);
    if (i != this.mViewPager.getCurrentItem()) {
      this.mViewPager.setCurrentItem(i, paramBoolean1);
    }
    this.mAdapter.setSelectedDay(this.mTempCalendar);
  }
  
  private void updateButtonVisibility(int paramInt)
  {
    int i = 1;
    int j = 0;
    int k;
    if (paramInt > 0) {
      k = 1;
    } else {
      k = 0;
    }
    if (paramInt < this.mAdapter.getCount() - 1) {
      paramInt = i;
    } else {
      paramInt = 0;
    }
    ImageButton localImageButton = this.mPrevButton;
    if (k != 0) {
      k = 0;
    } else {
      k = 4;
    }
    localImageButton.setVisibility(k);
    localImageButton = this.mNextButton;
    if (paramInt != 0) {
      paramInt = j;
    } else {
      paramInt = 4;
    }
    localImageButton.setVisibility(paramInt);
  }
  
  public boolean getBoundsForDate(long paramLong, Rect paramRect)
  {
    if (getPositionFromDay(paramLong) != this.mViewPager.getCurrentItem()) {
      return false;
    }
    this.mTempCalendar.setTimeInMillis(paramLong);
    return this.mAdapter.getBoundsForDate(this.mTempCalendar, paramRect);
  }
  
  public long getDate()
  {
    return this.mSelectedDay.getTimeInMillis();
  }
  
  public int getDayOfWeekTextAppearance()
  {
    return this.mAdapter.getDayOfWeekTextAppearance();
  }
  
  public int getDayTextAppearance()
  {
    return this.mAdapter.getDayTextAppearance();
  }
  
  public int getFirstDayOfWeek()
  {
    return this.mAdapter.getFirstDayOfWeek();
  }
  
  public long getMaxDate()
  {
    return this.mMaxDate.getTimeInMillis();
  }
  
  public long getMinDate()
  {
    return this.mMinDate.getTimeInMillis();
  }
  
  public int getMostVisiblePosition()
  {
    return this.mViewPager.getCurrentItem();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    ImageButton localImageButton1;
    ImageButton localImageButton2;
    if (isLayoutRtl())
    {
      localImageButton1 = this.mNextButton;
      localImageButton2 = this.mPrevButton;
    }
    else
    {
      localImageButton1 = this.mPrevButton;
      localImageButton2 = this.mNextButton;
    }
    paramInt1 = paramInt3 - paramInt1;
    this.mViewPager.layout(0, 0, paramInt1, paramInt4 - paramInt2);
    SimpleMonthView localSimpleMonthView = (SimpleMonthView)this.mViewPager.getChildAt(0);
    paramInt3 = localSimpleMonthView.getMonthHeight();
    paramInt2 = localSimpleMonthView.getCellWidth();
    int i = localImageButton1.getMeasuredWidth();
    paramInt4 = localImageButton1.getMeasuredHeight();
    int j = localSimpleMonthView.getPaddingTop() + (paramInt3 - paramInt4) / 2;
    int k = localSimpleMonthView.getPaddingLeft() + (paramInt2 - i) / 2;
    localImageButton1.layout(k, j, k + i, j + paramInt4);
    i = localImageButton2.getMeasuredWidth();
    paramInt4 = localImageButton2.getMeasuredHeight();
    paramInt3 = localSimpleMonthView.getPaddingTop() + (paramInt3 - paramInt4) / 2;
    paramInt1 = paramInt1 - localSimpleMonthView.getPaddingRight() - (paramInt2 - i) / 2;
    localImageButton2.layout(paramInt1 - i, paramInt3, paramInt1, paramInt3 + paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    ViewPager localViewPager = this.mViewPager;
    measureChild(localViewPager, paramInt1, paramInt2);
    setMeasuredDimension(localViewPager.getMeasuredWidthAndState(), localViewPager.getMeasuredHeightAndState());
    paramInt1 = localViewPager.getMeasuredWidth();
    paramInt2 = localViewPager.getMeasuredHeight();
    paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE);
    paramInt2 = View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE);
    this.mPrevButton.measure(paramInt1, paramInt2);
    this.mNextButton.measure(paramInt1, paramInt2);
  }
  
  public void onRangeChanged()
  {
    this.mAdapter.setRange(this.mMinDate, this.mMaxDate);
    setDate(this.mSelectedDay.getTimeInMillis(), false, false);
    updateButtonVisibility(this.mViewPager.getCurrentItem());
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    requestLayout();
  }
  
  public void setDate(long paramLong)
  {
    setDate(paramLong, false);
  }
  
  public void setDate(long paramLong, boolean paramBoolean)
  {
    setDate(paramLong, paramBoolean, true);
  }
  
  public void setDayOfWeekTextAppearance(int paramInt)
  {
    this.mAdapter.setDayOfWeekTextAppearance(paramInt);
  }
  
  public void setDayTextAppearance(int paramInt)
  {
    this.mAdapter.setDayTextAppearance(paramInt);
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    this.mAdapter.setFirstDayOfWeek(paramInt);
  }
  
  public void setMaxDate(long paramLong)
  {
    this.mMaxDate.setTimeInMillis(paramLong);
    onRangeChanged();
  }
  
  public void setMinDate(long paramLong)
  {
    this.mMinDate.setTimeInMillis(paramLong);
    onRangeChanged();
  }
  
  public void setOnDaySelectedListener(OnDaySelectedListener paramOnDaySelectedListener)
  {
    this.mOnDaySelectedListener = paramOnDaySelectedListener;
  }
  
  public void setPosition(int paramInt)
  {
    this.mViewPager.setCurrentItem(paramInt, false);
  }
  
  public static abstract interface OnDaySelectedListener
  {
    public abstract void onDaySelected(DayPickerView paramDayPickerView, Calendar paramCalendar);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DayPickerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */