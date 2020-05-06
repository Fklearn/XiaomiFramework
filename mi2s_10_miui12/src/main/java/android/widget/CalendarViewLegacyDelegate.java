package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import com.android.internal.R.styleable;
import java.util.Locale;
import libcore.icu.LocaleData;

class CalendarViewLegacyDelegate
  extends CalendarView.AbstractCalendarViewDelegate
{
  private static final int ADJUSTMENT_SCROLL_DURATION = 500;
  private static final int DAYS_PER_WEEK = 7;
  private static final int DEFAULT_DATE_TEXT_SIZE = 14;
  private static final int DEFAULT_SHOWN_WEEK_COUNT = 6;
  private static final boolean DEFAULT_SHOW_WEEK_NUMBER = true;
  private static final int DEFAULT_WEEK_DAY_TEXT_APPEARANCE_RES_ID = -1;
  private static final int GOTO_SCROLL_DURATION = 1000;
  private static final long MILLIS_IN_DAY = 86400000L;
  private static final long MILLIS_IN_WEEK = 604800000L;
  private static final int SCROLL_CHANGE_DELAY = 40;
  private static final int SCROLL_HYST_WEEKS = 2;
  private static final int UNSCALED_BOTTOM_BUFFER = 20;
  private static final int UNSCALED_LIST_SCROLL_TOP_OFFSET = 2;
  private static final int UNSCALED_SELECTED_DATE_VERTICAL_BAR_WIDTH = 6;
  private static final int UNSCALED_WEEK_MIN_VISIBLE_HEIGHT = 12;
  private static final int UNSCALED_WEEK_SEPARATOR_LINE_WIDTH = 1;
  private WeeksAdapter mAdapter;
  private int mBottomBuffer = 20;
  private int mCurrentMonthDisplayed = -1;
  private int mCurrentScrollState = 0;
  private int mDateTextAppearanceResId;
  private int mDateTextSize;
  private ViewGroup mDayNamesHeader;
  private String[] mDayNamesLong;
  private String[] mDayNamesShort;
  private int mDaysPerWeek = 7;
  private Calendar mFirstDayOfMonth;
  private int mFirstDayOfWeek;
  private int mFocusedMonthDateColor;
  private float mFriction = 0.05F;
  private boolean mIsScrollingUp = false;
  private int mListScrollTopOffset = 2;
  private ListView mListView;
  private Calendar mMaxDate;
  private Calendar mMinDate;
  private TextView mMonthName;
  private CalendarView.OnDateChangeListener mOnDateChangeListener;
  private long mPreviousScrollPosition;
  private int mPreviousScrollState = 0;
  private ScrollStateRunnable mScrollStateChangedRunnable = new ScrollStateRunnable(null);
  private Drawable mSelectedDateVerticalBar;
  private final int mSelectedDateVerticalBarWidth;
  private int mSelectedWeekBackgroundColor;
  private boolean mShowWeekNumber;
  private int mShownWeekCount;
  private Calendar mTempDate;
  private int mUnfocusedMonthDateColor;
  private float mVelocityScale = 0.333F;
  private int mWeekDayTextAppearanceResId;
  private int mWeekMinVisibleHeight = 12;
  private int mWeekNumberColor;
  private int mWeekSeparatorLineColor;
  private final int mWeekSeparatorLineWidth;
  
  CalendarViewLegacyDelegate(CalendarView paramCalendarView, Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramCalendarView, paramContext);
    paramCalendarView = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CalendarView, paramInt1, paramInt2);
    this.mShowWeekNumber = paramCalendarView.getBoolean(1, true);
    this.mFirstDayOfWeek = paramCalendarView.getInt(0, LocaleData.get(Locale.getDefault()).firstDayOfWeek.intValue());
    if (!CalendarView.parseDate(paramCalendarView.getString(2), this.mMinDate)) {
      CalendarView.parseDate("01/01/1900", this.mMinDate);
    }
    if (!CalendarView.parseDate(paramCalendarView.getString(3), this.mMaxDate)) {
      CalendarView.parseDate("01/01/2100", this.mMaxDate);
    }
    if (!this.mMaxDate.before(this.mMinDate))
    {
      this.mShownWeekCount = paramCalendarView.getInt(4, 6);
      this.mSelectedWeekBackgroundColor = paramCalendarView.getColor(5, 0);
      this.mFocusedMonthDateColor = paramCalendarView.getColor(6, 0);
      this.mUnfocusedMonthDateColor = paramCalendarView.getColor(7, 0);
      this.mWeekSeparatorLineColor = paramCalendarView.getColor(9, 0);
      this.mWeekNumberColor = paramCalendarView.getColor(8, 0);
      this.mSelectedDateVerticalBar = paramCalendarView.getDrawable(10);
      this.mDateTextAppearanceResId = paramCalendarView.getResourceId(12, 16973894);
      updateDateTextSize();
      this.mWeekDayTextAppearanceResId = paramCalendarView.getResourceId(11, -1);
      paramCalendarView.recycle();
      paramCalendarView = this.mDelegator.getResources().getDisplayMetrics();
      this.mWeekMinVisibleHeight = ((int)TypedValue.applyDimension(1, 12.0F, paramCalendarView));
      this.mListScrollTopOffset = ((int)TypedValue.applyDimension(1, 2.0F, paramCalendarView));
      this.mBottomBuffer = ((int)TypedValue.applyDimension(1, 20.0F, paramCalendarView));
      this.mSelectedDateVerticalBarWidth = ((int)TypedValue.applyDimension(1, 6.0F, paramCalendarView));
      this.mWeekSeparatorLineWidth = ((int)TypedValue.applyDimension(1, 1.0F, paramCalendarView));
      paramCalendarView = ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(17367106, null, false);
      this.mDelegator.addView(paramCalendarView);
      this.mListView = ((ListView)this.mDelegator.findViewById(16908298));
      this.mDayNamesHeader = ((ViewGroup)paramCalendarView.findViewById(16908877));
      this.mMonthName = ((TextView)paramCalendarView.findViewById(16909148));
      setUpHeader();
      setUpListView();
      setUpAdapter();
      this.mTempDate.setTimeInMillis(System.currentTimeMillis());
      if (this.mTempDate.before(this.mMinDate)) {
        goTo(this.mMinDate, false, true, true);
      } else if (this.mMaxDate.before(this.mTempDate)) {
        goTo(this.mMaxDate, false, true, true);
      } else {
        goTo(this.mTempDate, false, true, true);
      }
      this.mDelegator.invalidate();
      return;
    }
    throw new IllegalArgumentException("Max date cannot be before min date.");
  }
  
  private static Calendar getCalendarForLocale(Calendar paramCalendar, Locale paramLocale)
  {
    if (paramCalendar == null) {
      return Calendar.getInstance(paramLocale);
    }
    long l = paramCalendar.getTimeInMillis();
    paramCalendar = Calendar.getInstance(paramLocale);
    paramCalendar.setTimeInMillis(l);
    return paramCalendar;
  }
  
  private int getWeeksSinceMinDate(Calendar paramCalendar)
  {
    if (!paramCalendar.before(this.mMinDate)) {
      return (int)((paramCalendar.getTimeInMillis() + paramCalendar.getTimeZone().getOffset(paramCalendar.getTimeInMillis()) - (this.mMinDate.getTimeInMillis() + this.mMinDate.getTimeZone().getOffset(this.mMinDate.getTimeInMillis())) + (this.mMinDate.get(7) - this.mFirstDayOfWeek) * 86400000L) / 604800000L);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("fromDate: ");
    localStringBuilder.append(this.mMinDate.getTime());
    localStringBuilder.append(" does not precede toDate: ");
    localStringBuilder.append(paramCalendar.getTime());
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  private void goTo(Calendar paramCalendar, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((!paramCalendar.before(this.mMinDate)) && (!paramCalendar.after(this.mMaxDate)))
    {
      int i = this.mListView.getFirstVisiblePosition();
      View localView = this.mListView.getChildAt(0);
      int j = i;
      if (localView != null)
      {
        j = i;
        if (localView.getTop() < 0) {
          j = i + 1;
        }
      }
      int k = this.mShownWeekCount + j - 1;
      i = k;
      if (localView != null)
      {
        i = k;
        if (localView.getTop() > this.mBottomBuffer) {
          i = k - 1;
        }
      }
      if (paramBoolean2) {
        this.mAdapter.setSelectedDay(paramCalendar);
      }
      k = getWeeksSinceMinDate(paramCalendar);
      if ((k >= j) && (k <= i) && (!paramBoolean3))
      {
        if (paramBoolean2) {
          setMonthDisplayed(paramCalendar);
        }
      }
      else
      {
        this.mFirstDayOfMonth.setTimeInMillis(paramCalendar.getTimeInMillis());
        this.mFirstDayOfMonth.set(5, 1);
        setMonthDisplayed(this.mFirstDayOfMonth);
        if (this.mFirstDayOfMonth.before(this.mMinDate)) {
          j = 0;
        } else {
          j = getWeeksSinceMinDate(this.mFirstDayOfMonth);
        }
        this.mPreviousScrollState = 2;
        if (paramBoolean1)
        {
          this.mListView.smoothScrollToPositionFromTop(j, this.mListScrollTopOffset, 1000);
        }
        else
        {
          this.mListView.setSelectionFromTop(j, this.mListScrollTopOffset);
          onScrollStateChanged(this.mListView, 0);
        }
      }
      return;
    }
    throw new IllegalArgumentException("timeInMillis must be between the values of getMinDate() and getMaxDate()");
  }
  
  private void invalidateAllWeekViews()
  {
    int i = this.mListView.getChildCount();
    for (int j = 0; j < i; j++) {
      this.mListView.getChildAt(j).invalidate();
    }
  }
  
  private static boolean isSameDate(Calendar paramCalendar1, Calendar paramCalendar2)
  {
    int i = paramCalendar1.get(6);
    int j = paramCalendar2.get(6);
    boolean bool = true;
    if ((i != j) || (paramCalendar1.get(1) != paramCalendar2.get(1))) {
      bool = false;
    }
    return bool;
  }
  
  private void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt1 = 0;
    WeekView localWeekView = (WeekView)paramAbsListView.getChildAt(0);
    if (localWeekView == null) {
      return;
    }
    long l1 = paramAbsListView.getFirstVisiblePosition() * localWeekView.getHeight() - localWeekView.getBottom();
    long l2 = this.mPreviousScrollPosition;
    if (l1 < l2)
    {
      this.mIsScrollingUp = true;
    }
    else
    {
      if (l1 <= l2) {
        return;
      }
      this.mIsScrollingUp = false;
    }
    if (localWeekView.getBottom() < this.mWeekMinVisibleHeight) {
      paramInt1 = 1;
    }
    if (this.mIsScrollingUp) {
      localWeekView = (WeekView)paramAbsListView.getChildAt(paramInt1 + 2);
    } else if (paramInt1 != 0) {
      localWeekView = (WeekView)paramAbsListView.getChildAt(paramInt1);
    }
    if (localWeekView != null)
    {
      if (this.mIsScrollingUp) {
        paramInt1 = localWeekView.getMonthOfFirstWeekDay();
      } else {
        paramInt1 = localWeekView.getMonthOfLastWeekDay();
      }
      if ((this.mCurrentMonthDisplayed == 11) && (paramInt1 == 0)) {
        paramInt1 = 1;
      } else if ((this.mCurrentMonthDisplayed == 0) && (paramInt1 == 11)) {
        paramInt1 = -1;
      } else {
        paramInt1 -= this.mCurrentMonthDisplayed;
      }
      if (((!this.mIsScrollingUp) && (paramInt1 > 0)) || ((this.mIsScrollingUp) && (paramInt1 < 0)))
      {
        paramAbsListView = localWeekView.getFirstDay();
        if (this.mIsScrollingUp) {
          paramAbsListView.add(5, -7);
        } else {
          paramAbsListView.add(5, 7);
        }
        setMonthDisplayed(paramAbsListView);
      }
    }
    this.mPreviousScrollPosition = l1;
    this.mPreviousScrollState = this.mCurrentScrollState;
    return;
  }
  
  private void onScrollStateChanged(AbsListView paramAbsListView, int paramInt)
  {
    this.mScrollStateChangedRunnable.doScrollStateChange(paramAbsListView, paramInt);
  }
  
  private void setMonthDisplayed(Calendar paramCalendar)
  {
    this.mCurrentMonthDisplayed = paramCalendar.get(2);
    this.mAdapter.setFocusMonth(this.mCurrentMonthDisplayed);
    long l = paramCalendar.getTimeInMillis();
    paramCalendar = DateUtils.formatDateRange(this.mContext, l, l, 52);
    this.mMonthName.setText(paramCalendar);
    this.mMonthName.invalidate();
  }
  
  private void setUpAdapter()
  {
    if (this.mAdapter == null)
    {
      this.mAdapter = new WeeksAdapter(this.mContext);
      this.mAdapter.registerDataSetObserver(new DataSetObserver()
      {
        public void onChanged()
        {
          if (CalendarViewLegacyDelegate.this.mOnDateChangeListener != null)
          {
            Calendar localCalendar = CalendarViewLegacyDelegate.this.mAdapter.getSelectedDay();
            CalendarViewLegacyDelegate.this.mOnDateChangeListener.onSelectedDayChange(CalendarViewLegacyDelegate.this.mDelegator, localCalendar.get(1), localCalendar.get(2), localCalendar.get(5));
          }
        }
      });
      this.mListView.setAdapter(this.mAdapter);
    }
    this.mAdapter.notifyDataSetChanged();
  }
  
  private void setUpHeader()
  {
    int i = this.mDaysPerWeek;
    this.mDayNamesShort = new String[i];
    this.mDayNamesLong = new String[i];
    int j = this.mFirstDayOfWeek;
    int k = this.mFirstDayOfWeek;
    while (j < k + i)
    {
      if (j > 7) {
        m = j - 7;
      } else {
        m = j;
      }
      this.mDayNamesShort[(j - this.mFirstDayOfWeek)] = DateUtils.getDayOfWeekString(m, 50);
      this.mDayNamesLong[(j - this.mFirstDayOfWeek)] = DateUtils.getDayOfWeekString(m, 10);
      j++;
    }
    TextView localTextView = (TextView)this.mDayNamesHeader.getChildAt(0);
    if (this.mShowWeekNumber) {
      localTextView.setVisibility(0);
    } else {
      localTextView.setVisibility(8);
    }
    j = 1;
    int m = this.mDayNamesHeader.getChildCount();
    while (j < m)
    {
      localTextView = (TextView)this.mDayNamesHeader.getChildAt(j);
      i = this.mWeekDayTextAppearanceResId;
      if (i > -1) {
        localTextView.setTextAppearance(i);
      }
      if (j < this.mDaysPerWeek + 1)
      {
        localTextView.setText(this.mDayNamesShort[(j - 1)]);
        localTextView.setContentDescription(this.mDayNamesLong[(j - 1)]);
        localTextView.setVisibility(0);
      }
      else
      {
        localTextView.setVisibility(8);
      }
      j++;
    }
    this.mDayNamesHeader.invalidate();
  }
  
  private void setUpListView()
  {
    this.mListView.setDivider(null);
    this.mListView.setItemsCanFocus(true);
    this.mListView.setVerticalScrollBarEnabled(false);
    this.mListView.setOnScrollListener(new AbsListView.OnScrollListener()
    {
      public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        CalendarViewLegacyDelegate.this.onScroll(paramAnonymousAbsListView, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
      }
      
      public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
      {
        CalendarViewLegacyDelegate.this.onScrollStateChanged(paramAnonymousAbsListView, paramAnonymousInt);
      }
    });
    this.mListView.setFriction(this.mFriction);
    this.mListView.setVelocityScale(this.mVelocityScale);
  }
  
  private void updateDateTextSize()
  {
    TypedArray localTypedArray = this.mDelegator.getContext().obtainStyledAttributes(this.mDateTextAppearanceResId, R.styleable.TextAppearance);
    this.mDateTextSize = localTypedArray.getDimensionPixelSize(0, 14);
    localTypedArray.recycle();
  }
  
  public boolean getBoundsForDate(long paramLong, Rect paramRect)
  {
    Object localObject = Calendar.getInstance();
    ((Calendar)localObject).setTimeInMillis(paramLong);
    int i = this.mListView.getCount();
    for (int j = 0; j < i; j++)
    {
      WeekView localWeekView = (WeekView)this.mListView.getChildAt(j);
      if (localWeekView.getBoundsForDate((Calendar)localObject, paramRect))
      {
        localObject = new int[2];
        int[] arrayOfInt = new int[2];
        localWeekView.getLocationOnScreen((int[])localObject);
        this.mDelegator.getLocationOnScreen(arrayOfInt);
        j = localObject[1] - arrayOfInt[1];
        paramRect.top += j;
        paramRect.bottom += j;
        return true;
      }
    }
    return false;
  }
  
  public long getDate()
  {
    return this.mAdapter.mSelectedDate.getTimeInMillis();
  }
  
  public int getDateTextAppearance()
  {
    return this.mDateTextAppearanceResId;
  }
  
  public int getFirstDayOfWeek()
  {
    return this.mFirstDayOfWeek;
  }
  
  public int getFocusedMonthDateColor()
  {
    return this.mFocusedMonthDateColor;
  }
  
  public long getMaxDate()
  {
    return this.mMaxDate.getTimeInMillis();
  }
  
  public long getMinDate()
  {
    return this.mMinDate.getTimeInMillis();
  }
  
  public Drawable getSelectedDateVerticalBar()
  {
    return this.mSelectedDateVerticalBar;
  }
  
  public int getSelectedWeekBackgroundColor()
  {
    return this.mSelectedWeekBackgroundColor;
  }
  
  public boolean getShowWeekNumber()
  {
    return this.mShowWeekNumber;
  }
  
  public int getShownWeekCount()
  {
    return this.mShownWeekCount;
  }
  
  public int getUnfocusedMonthDateColor()
  {
    return this.mUnfocusedMonthDateColor;
  }
  
  public int getWeekDayTextAppearance()
  {
    return this.mWeekDayTextAppearanceResId;
  }
  
  public int getWeekNumberColor()
  {
    return this.mWeekNumberColor;
  }
  
  public int getWeekSeparatorLineColor()
  {
    return this.mWeekSeparatorLineColor;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    setCurrentLocale(paramConfiguration.locale);
  }
  
  protected void setCurrentLocale(Locale paramLocale)
  {
    super.setCurrentLocale(paramLocale);
    this.mTempDate = getCalendarForLocale(this.mTempDate, paramLocale);
    this.mFirstDayOfMonth = getCalendarForLocale(this.mFirstDayOfMonth, paramLocale);
    this.mMinDate = getCalendarForLocale(this.mMinDate, paramLocale);
    this.mMaxDate = getCalendarForLocale(this.mMaxDate, paramLocale);
  }
  
  public void setDate(long paramLong)
  {
    setDate(paramLong, false, false);
  }
  
  public void setDate(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if (isSameDate(this.mTempDate, this.mAdapter.mSelectedDate)) {
      return;
    }
    goTo(this.mTempDate, paramBoolean1, true, paramBoolean2);
  }
  
  public void setDateTextAppearance(int paramInt)
  {
    if (this.mDateTextAppearanceResId != paramInt)
    {
      this.mDateTextAppearanceResId = paramInt;
      updateDateTextSize();
      invalidateAllWeekViews();
    }
  }
  
  public void setFirstDayOfWeek(int paramInt)
  {
    if (this.mFirstDayOfWeek == paramInt) {
      return;
    }
    this.mFirstDayOfWeek = paramInt;
    this.mAdapter.init();
    this.mAdapter.notifyDataSetChanged();
    setUpHeader();
  }
  
  public void setFocusedMonthDateColor(int paramInt)
  {
    if (this.mFocusedMonthDateColor != paramInt)
    {
      this.mFocusedMonthDateColor = paramInt;
      int i = this.mListView.getChildCount();
      for (paramInt = 0; paramInt < i; paramInt++)
      {
        WeekView localWeekView = (WeekView)this.mListView.getChildAt(paramInt);
        if (localWeekView.mHasFocusedDay) {
          localWeekView.invalidate();
        }
      }
    }
  }
  
  public void setMaxDate(long paramLong)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if (isSameDate(this.mTempDate, this.mMaxDate)) {
      return;
    }
    this.mMaxDate.setTimeInMillis(paramLong);
    this.mAdapter.init();
    Calendar localCalendar = this.mAdapter.mSelectedDate;
    if (localCalendar.after(this.mMaxDate)) {
      setDate(this.mMaxDate.getTimeInMillis());
    } else {
      goTo(localCalendar, false, true, false);
    }
  }
  
  public void setMinDate(long paramLong)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if (isSameDate(this.mTempDate, this.mMinDate)) {
      return;
    }
    this.mMinDate.setTimeInMillis(paramLong);
    Calendar localCalendar = this.mAdapter.mSelectedDate;
    if (localCalendar.before(this.mMinDate)) {
      this.mAdapter.setSelectedDay(this.mMinDate);
    }
    this.mAdapter.init();
    if (localCalendar.before(this.mMinDate)) {
      setDate(this.mTempDate.getTimeInMillis());
    } else {
      goTo(localCalendar, false, true, false);
    }
  }
  
  public void setOnDateChangeListener(CalendarView.OnDateChangeListener paramOnDateChangeListener)
  {
    this.mOnDateChangeListener = paramOnDateChangeListener;
  }
  
  public void setSelectedDateVerticalBar(int paramInt)
  {
    setSelectedDateVerticalBar(this.mDelegator.getContext().getDrawable(paramInt));
  }
  
  public void setSelectedDateVerticalBar(Drawable paramDrawable)
  {
    if (this.mSelectedDateVerticalBar != paramDrawable)
    {
      this.mSelectedDateVerticalBar = paramDrawable;
      int i = this.mListView.getChildCount();
      for (int j = 0; j < i; j++)
      {
        paramDrawable = (WeekView)this.mListView.getChildAt(j);
        if (paramDrawable.mHasSelectedDay) {
          paramDrawable.invalidate();
        }
      }
    }
  }
  
  public void setSelectedWeekBackgroundColor(int paramInt)
  {
    if (this.mSelectedWeekBackgroundColor != paramInt)
    {
      this.mSelectedWeekBackgroundColor = paramInt;
      int i = this.mListView.getChildCount();
      for (paramInt = 0; paramInt < i; paramInt++)
      {
        WeekView localWeekView = (WeekView)this.mListView.getChildAt(paramInt);
        if (localWeekView.mHasSelectedDay) {
          localWeekView.invalidate();
        }
      }
    }
  }
  
  public void setShowWeekNumber(boolean paramBoolean)
  {
    if (this.mShowWeekNumber == paramBoolean) {
      return;
    }
    this.mShowWeekNumber = paramBoolean;
    this.mAdapter.notifyDataSetChanged();
    setUpHeader();
  }
  
  public void setShownWeekCount(int paramInt)
  {
    if (this.mShownWeekCount != paramInt)
    {
      this.mShownWeekCount = paramInt;
      this.mDelegator.invalidate();
    }
  }
  
  public void setUnfocusedMonthDateColor(int paramInt)
  {
    if (this.mUnfocusedMonthDateColor != paramInt)
    {
      this.mUnfocusedMonthDateColor = paramInt;
      int i = this.mListView.getChildCount();
      for (paramInt = 0; paramInt < i; paramInt++)
      {
        WeekView localWeekView = (WeekView)this.mListView.getChildAt(paramInt);
        if (localWeekView.mHasUnfocusedDay) {
          localWeekView.invalidate();
        }
      }
    }
  }
  
  public void setWeekDayTextAppearance(int paramInt)
  {
    if (this.mWeekDayTextAppearanceResId != paramInt)
    {
      this.mWeekDayTextAppearanceResId = paramInt;
      setUpHeader();
    }
  }
  
  public void setWeekNumberColor(int paramInt)
  {
    if (this.mWeekNumberColor != paramInt)
    {
      this.mWeekNumberColor = paramInt;
      if (this.mShowWeekNumber) {
        invalidateAllWeekViews();
      }
    }
  }
  
  public void setWeekSeparatorLineColor(int paramInt)
  {
    if (this.mWeekSeparatorLineColor != paramInt)
    {
      this.mWeekSeparatorLineColor = paramInt;
      invalidateAllWeekViews();
    }
  }
  
  private class ScrollStateRunnable
    implements Runnable
  {
    private int mNewState;
    private AbsListView mView;
    
    private ScrollStateRunnable() {}
    
    public void doScrollStateChange(AbsListView paramAbsListView, int paramInt)
    {
      this.mView = paramAbsListView;
      this.mNewState = paramInt;
      CalendarViewLegacyDelegate.this.mDelegator.removeCallbacks(this);
      CalendarViewLegacyDelegate.this.mDelegator.postDelayed(this, 40L);
    }
    
    public void run()
    {
      CalendarViewLegacyDelegate.access$1002(CalendarViewLegacyDelegate.this, this.mNewState);
      if ((this.mNewState == 0) && (CalendarViewLegacyDelegate.this.mPreviousScrollState != 0))
      {
        View localView = this.mView.getChildAt(0);
        if (localView == null) {
          return;
        }
        int i = localView.getBottom() - CalendarViewLegacyDelegate.this.mListScrollTopOffset;
        if (i > CalendarViewLegacyDelegate.this.mListScrollTopOffset) {
          if (CalendarViewLegacyDelegate.this.mIsScrollingUp) {
            this.mView.smoothScrollBy(i - localView.getHeight(), 500);
          } else {
            this.mView.smoothScrollBy(i, 500);
          }
        }
      }
      CalendarViewLegacyDelegate.access$1102(CalendarViewLegacyDelegate.this, this.mNewState);
    }
  }
  
  private class WeekView
    extends View
  {
    private String[] mDayNumbers;
    private final Paint mDrawPaint = new Paint();
    private Calendar mFirstDay;
    private boolean[] mFocusDay;
    private boolean mHasFocusedDay;
    private boolean mHasSelectedDay = false;
    private boolean mHasUnfocusedDay;
    private int mHeight;
    private int mLastWeekDayMonth = -1;
    private final Paint mMonthNumDrawPaint = new Paint();
    private int mMonthOfFirstWeekDay = -1;
    private int mNumCells;
    private int mSelectedDay = -1;
    private int mSelectedLeft = -1;
    private int mSelectedRight = -1;
    private final Rect mTempRect = new Rect();
    private int mWeek = -1;
    private int mWidth;
    
    public WeekView(Context paramContext)
    {
      super();
      initializePaints();
    }
    
    private void drawBackground(Canvas paramCanvas)
    {
      if (!this.mHasSelectedDay) {
        return;
      }
      this.mDrawPaint.setColor(CalendarViewLegacyDelegate.this.mSelectedWeekBackgroundColor);
      this.mTempRect.top = CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth;
      this.mTempRect.bottom = this.mHeight;
      boolean bool = isLayoutRtl();
      int i = 0;
      Rect localRect;
      if (bool)
      {
        localRect = this.mTempRect;
        localRect.left = 0;
        localRect.right = (this.mSelectedLeft - 2);
      }
      else
      {
        localRect = this.mTempRect;
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
          i = this.mWidth / this.mNumCells;
        }
        localRect.left = i;
        this.mTempRect.right = (this.mSelectedLeft - 2);
      }
      paramCanvas.drawRect(this.mTempRect, this.mDrawPaint);
      if (bool)
      {
        localRect = this.mTempRect;
        localRect.left = (this.mSelectedRight + 3);
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber)
        {
          i = this.mWidth;
          i -= i / this.mNumCells;
        }
        else
        {
          i = this.mWidth;
        }
        localRect.right = i;
      }
      else
      {
        localRect = this.mTempRect;
        localRect.left = (this.mSelectedRight + 3);
        localRect.right = this.mWidth;
      }
      paramCanvas.drawRect(this.mTempRect, this.mDrawPaint);
    }
    
    private void drawSelectedDateVerticalBars(Canvas paramCanvas)
    {
      if (!this.mHasSelectedDay) {
        return;
      }
      CalendarViewLegacyDelegate.this.mSelectedDateVerticalBar.setBounds(this.mSelectedLeft - CalendarViewLegacyDelegate.this.mSelectedDateVerticalBarWidth / 2, CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth, this.mSelectedLeft + CalendarViewLegacyDelegate.this.mSelectedDateVerticalBarWidth / 2, this.mHeight);
      CalendarViewLegacyDelegate.this.mSelectedDateVerticalBar.draw(paramCanvas);
      CalendarViewLegacyDelegate.this.mSelectedDateVerticalBar.setBounds(this.mSelectedRight - CalendarViewLegacyDelegate.this.mSelectedDateVerticalBarWidth / 2, CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth, this.mSelectedRight + CalendarViewLegacyDelegate.this.mSelectedDateVerticalBarWidth / 2, this.mHeight);
      CalendarViewLegacyDelegate.this.mSelectedDateVerticalBar.draw(paramCanvas);
    }
    
    private void drawWeekNumbersAndDates(Canvas paramCanvas)
    {
      float f = this.mDrawPaint.getTextSize();
      int i = (int)((this.mHeight + f) / 2.0F) - CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth;
      int j = this.mNumCells;
      int k = j * 2;
      this.mDrawPaint.setTextAlign(Paint.Align.CENTER);
      this.mDrawPaint.setTextSize(CalendarViewLegacyDelegate.this.mDateTextSize);
      int m = 0;
      int n = 0;
      Paint localPaint;
      if (isLayoutRtl())
      {
        while (n < j - 1)
        {
          localPaint = this.mMonthNumDrawPaint;
          if (this.mFocusDay[n] != 0) {
            m = CalendarViewLegacyDelegate.this.mFocusedMonthDateColor;
          } else {
            m = CalendarViewLegacyDelegate.this.mUnfocusedMonthDateColor;
          }
          localPaint.setColor(m);
          m = (n * 2 + 1) * this.mWidth / k;
          paramCanvas.drawText(this.mDayNumbers[(j - 1 - n)], m, i, this.mMonthNumDrawPaint);
          n++;
        }
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber)
        {
          this.mDrawPaint.setColor(CalendarViewLegacyDelegate.this.mWeekNumberColor);
          n = this.mWidth;
          m = n / k;
          paramCanvas.drawText(this.mDayNumbers[0], n - m, i, this.mDrawPaint);
        }
      }
      else
      {
        n = m;
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber)
        {
          this.mDrawPaint.setColor(CalendarViewLegacyDelegate.this.mWeekNumberColor);
          n = this.mWidth / k;
          paramCanvas.drawText(this.mDayNumbers[0], n, i, this.mDrawPaint);
        }
        for (n = 0 + 1; n < j; n++)
        {
          localPaint = this.mMonthNumDrawPaint;
          if (this.mFocusDay[n] != 0) {
            m = CalendarViewLegacyDelegate.this.mFocusedMonthDateColor;
          } else {
            m = CalendarViewLegacyDelegate.this.mUnfocusedMonthDateColor;
          }
          localPaint.setColor(m);
          m = (n * 2 + 1) * this.mWidth / k;
          paramCanvas.drawText(this.mDayNumbers[n], m, i, this.mMonthNumDrawPaint);
        }
      }
    }
    
    private void drawWeekSeparators(Canvas paramCanvas)
    {
      int i = CalendarViewLegacyDelegate.this.mListView.getFirstVisiblePosition();
      int j = i;
      if (CalendarViewLegacyDelegate.this.mListView.getChildAt(0).getTop() < 0) {
        j = i + 1;
      }
      if (j == this.mWeek) {
        return;
      }
      this.mDrawPaint.setColor(CalendarViewLegacyDelegate.this.mWeekSeparatorLineColor);
      this.mDrawPaint.setStrokeWidth(CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth);
      float f1;
      float f2;
      if (isLayoutRtl())
      {
        f1 = 0.0F;
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber)
        {
          j = this.mWidth;
          j -= j / this.mNumCells;
        }
        else
        {
          j = this.mWidth;
        }
        f2 = j;
      }
      else
      {
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
          f1 = this.mWidth / this.mNumCells;
        } else {
          f1 = 0.0F;
        }
        f2 = this.mWidth;
      }
      paramCanvas.drawLine(f1, 0.0F, f2, 0.0F, this.mDrawPaint);
    }
    
    private void initializePaints()
    {
      this.mDrawPaint.setFakeBoldText(false);
      this.mDrawPaint.setAntiAlias(true);
      this.mDrawPaint.setStyle(Paint.Style.FILL);
      this.mMonthNumDrawPaint.setFakeBoldText(true);
      this.mMonthNumDrawPaint.setAntiAlias(true);
      this.mMonthNumDrawPaint.setStyle(Paint.Style.FILL);
      this.mMonthNumDrawPaint.setTextAlign(Paint.Align.CENTER);
      this.mMonthNumDrawPaint.setTextSize(CalendarViewLegacyDelegate.this.mDateTextSize);
    }
    
    private void updateSelectionPositions()
    {
      if (this.mHasSelectedDay)
      {
        boolean bool = isLayoutRtl();
        int i = this.mSelectedDay - CalendarViewLegacyDelegate.this.mFirstDayOfWeek;
        int j = i;
        if (i < 0) {
          j = i + 7;
        }
        i = j;
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber)
        {
          i = j;
          if (!bool) {
            i = j + 1;
          }
        }
        if (bool) {
          this.mSelectedLeft = ((CalendarViewLegacyDelegate.this.mDaysPerWeek - 1 - i) * this.mWidth / this.mNumCells);
        } else {
          this.mSelectedLeft = (this.mWidth * i / this.mNumCells);
        }
        this.mSelectedRight = (this.mSelectedLeft + this.mWidth / this.mNumCells);
      }
    }
    
    public boolean getBoundsForDate(Calendar paramCalendar, Rect paramRect)
    {
      Calendar localCalendar = Calendar.getInstance();
      localCalendar.setTime(this.mFirstDay.getTime());
      for (int i = 0; i < CalendarViewLegacyDelegate.this.mDaysPerWeek; i++)
      {
        if ((paramCalendar.get(1) == localCalendar.get(1)) && (paramCalendar.get(2) == localCalendar.get(2)) && (paramCalendar.get(5) == localCalendar.get(5)))
        {
          int j = this.mWidth / this.mNumCells;
          if (isLayoutRtl())
          {
            if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
              i = this.mNumCells - i - 2;
            } else {
              i = this.mNumCells - i - 1;
            }
            paramRect.left = (i * j);
          }
          else
          {
            if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
              i++;
            }
            paramRect.left = (i * j);
          }
          paramRect.top = 0;
          paramRect.right = (paramRect.left + j);
          paramRect.bottom = getHeight();
          return true;
        }
        localCalendar.add(5, 1);
      }
      return false;
    }
    
    public boolean getDayFromLocation(float paramFloat, Calendar paramCalendar)
    {
      boolean bool = isLayoutRtl();
      int i;
      int j;
      int k;
      if (bool)
      {
        i = 0;
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber)
        {
          j = this.mWidth;
          j -= j / this.mNumCells;
        }
        else
        {
          j = this.mWidth;
        }
        k = j;
      }
      else
      {
        if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
          j = this.mWidth / this.mNumCells;
        } else {
          j = 0;
        }
        k = this.mWidth;
        i = j;
      }
      if ((paramFloat >= i) && (paramFloat <= k))
      {
        i = (int)((paramFloat - i) * CalendarViewLegacyDelegate.this.mDaysPerWeek / (k - i));
        j = i;
        if (bool) {
          j = CalendarViewLegacyDelegate.this.mDaysPerWeek - 1 - i;
        }
        paramCalendar.setTimeInMillis(this.mFirstDay.getTimeInMillis());
        paramCalendar.add(5, j);
        return true;
      }
      paramCalendar.clear();
      return false;
    }
    
    public Calendar getFirstDay()
    {
      return this.mFirstDay;
    }
    
    public int getMonthOfFirstWeekDay()
    {
      return this.mMonthOfFirstWeekDay;
    }
    
    public int getMonthOfLastWeekDay()
    {
      return this.mLastWeekDayMonth;
    }
    
    public void init(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mSelectedDay = paramInt2;
      boolean bool;
      if (this.mSelectedDay != -1) {
        bool = true;
      } else {
        bool = false;
      }
      this.mHasSelectedDay = bool;
      if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
        paramInt2 = CalendarViewLegacyDelegate.this.mDaysPerWeek + 1;
      } else {
        paramInt2 = CalendarViewLegacyDelegate.this.mDaysPerWeek;
      }
      this.mNumCells = paramInt2;
      this.mWeek = paramInt1;
      CalendarViewLegacyDelegate.this.mTempDate.setTimeInMillis(CalendarViewLegacyDelegate.this.mMinDate.getTimeInMillis());
      CalendarViewLegacyDelegate.this.mTempDate.add(3, this.mWeek);
      CalendarViewLegacyDelegate.this.mTempDate.setFirstDayOfWeek(CalendarViewLegacyDelegate.this.mFirstDayOfWeek);
      paramInt1 = this.mNumCells;
      this.mDayNumbers = new String[paramInt1];
      this.mFocusDay = new boolean[paramInt1];
      paramInt1 = 0;
      if (CalendarViewLegacyDelegate.this.mShowWeekNumber)
      {
        this.mDayNumbers[0] = String.format(Locale.getDefault(), "%d", new Object[] { Integer.valueOf(CalendarViewLegacyDelegate.this.mTempDate.get(3)) });
        paramInt1 = 0 + 1;
      }
      paramInt2 = CalendarViewLegacyDelegate.this.mFirstDayOfWeek;
      int i = CalendarViewLegacyDelegate.this.mTempDate.get(7);
      CalendarViewLegacyDelegate.this.mTempDate.add(5, paramInt2 - i);
      this.mFirstDay = ((Calendar)CalendarViewLegacyDelegate.this.mTempDate.clone());
      this.mMonthOfFirstWeekDay = CalendarViewLegacyDelegate.this.mTempDate.get(2);
      this.mHasUnfocusedDay = true;
      while (paramInt1 < this.mNumCells)
      {
        if (CalendarViewLegacyDelegate.this.mTempDate.get(2) == paramInt3) {
          bool = true;
        } else {
          bool = false;
        }
        this.mFocusDay[paramInt1] = bool;
        this.mHasFocusedDay |= bool;
        int j = this.mHasUnfocusedDay;
        if (!bool) {
          paramInt2 = 1;
        } else {
          paramInt2 = 0;
        }
        this.mHasUnfocusedDay = (j & paramInt2);
        if ((!CalendarViewLegacyDelegate.this.mTempDate.before(CalendarViewLegacyDelegate.this.mMinDate)) && (!CalendarViewLegacyDelegate.this.mTempDate.after(CalendarViewLegacyDelegate.this.mMaxDate))) {
          this.mDayNumbers[paramInt1] = String.format(Locale.getDefault(), "%d", new Object[] { Integer.valueOf(CalendarViewLegacyDelegate.this.mTempDate.get(5)) });
        } else {
          this.mDayNumbers[paramInt1] = "";
        }
        CalendarViewLegacyDelegate.this.mTempDate.add(5, 1);
        paramInt1++;
      }
      if (CalendarViewLegacyDelegate.this.mTempDate.get(5) == 1) {
        CalendarViewLegacyDelegate.this.mTempDate.add(5, -1);
      }
      this.mLastWeekDayMonth = CalendarViewLegacyDelegate.this.mTempDate.get(2);
      updateSelectionPositions();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      drawBackground(paramCanvas);
      drawWeekNumbersAndDates(paramCanvas);
      drawWeekSeparators(paramCanvas);
      drawSelectedDateVerticalBars(paramCanvas);
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      this.mHeight = ((CalendarViewLegacyDelegate.this.mListView.getHeight() - CalendarViewLegacyDelegate.this.mListView.getPaddingTop() - CalendarViewLegacyDelegate.this.mListView.getPaddingBottom()) / CalendarViewLegacyDelegate.this.mShownWeekCount);
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), this.mHeight);
    }
    
    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mWidth = paramInt1;
      updateSelectionPositions();
    }
  }
  
  private class WeeksAdapter
    extends BaseAdapter
    implements View.OnTouchListener
  {
    private int mFocusedMonth;
    private GestureDetector mGestureDetector;
    private final Calendar mSelectedDate = Calendar.getInstance();
    private int mSelectedWeek;
    private int mTotalWeekCount;
    
    public WeeksAdapter(Context paramContext)
    {
      CalendarViewLegacyDelegate.this.mContext = paramContext;
      this.mGestureDetector = new GestureDetector(CalendarViewLegacyDelegate.this.mContext, new CalendarGestureListener());
      init();
    }
    
    private void init()
    {
      this.mSelectedWeek = CalendarViewLegacyDelegate.this.getWeeksSinceMinDate(this.mSelectedDate);
      CalendarViewLegacyDelegate localCalendarViewLegacyDelegate = CalendarViewLegacyDelegate.this;
      this.mTotalWeekCount = localCalendarViewLegacyDelegate.getWeeksSinceMinDate(localCalendarViewLegacyDelegate.mMaxDate);
      if ((CalendarViewLegacyDelegate.this.mMinDate.get(7) != CalendarViewLegacyDelegate.this.mFirstDayOfWeek) || (CalendarViewLegacyDelegate.this.mMaxDate.get(7) != CalendarViewLegacyDelegate.this.mFirstDayOfWeek)) {
        this.mTotalWeekCount += 1;
      }
      notifyDataSetChanged();
    }
    
    private void onDateTapped(Calendar paramCalendar)
    {
      setSelectedDay(paramCalendar);
      CalendarViewLegacyDelegate.this.setMonthDisplayed(paramCalendar);
    }
    
    public int getCount()
    {
      return this.mTotalWeekCount;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public Calendar getSelectedDay()
    {
      return this.mSelectedDate;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView != null)
      {
        paramView = (CalendarViewLegacyDelegate.WeekView)paramView;
      }
      else
      {
        paramView = CalendarViewLegacyDelegate.this;
        paramView = new CalendarViewLegacyDelegate.WeekView(paramView, paramView.mContext);
        paramView.setLayoutParams(new AbsListView.LayoutParams(-2, -2));
        paramView.setClickable(true);
        paramView.setOnTouchListener(this);
      }
      int i;
      if (this.mSelectedWeek == paramInt) {
        i = this.mSelectedDate.get(7);
      } else {
        i = -1;
      }
      paramView.init(paramInt, i, this.mFocusedMonth);
      return paramView;
    }
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
      if ((CalendarViewLegacyDelegate.this.mListView.isEnabled()) && (this.mGestureDetector.onTouchEvent(paramMotionEvent)))
      {
        if (!((CalendarViewLegacyDelegate.WeekView)paramView).getDayFromLocation(paramMotionEvent.getX(), CalendarViewLegacyDelegate.this.mTempDate)) {
          return true;
        }
        if ((!CalendarViewLegacyDelegate.this.mTempDate.before(CalendarViewLegacyDelegate.this.mMinDate)) && (!CalendarViewLegacyDelegate.this.mTempDate.after(CalendarViewLegacyDelegate.this.mMaxDate)))
        {
          onDateTapped(CalendarViewLegacyDelegate.this.mTempDate);
          return true;
        }
        return true;
      }
      return false;
    }
    
    public void setFocusMonth(int paramInt)
    {
      if (this.mFocusedMonth == paramInt) {
        return;
      }
      this.mFocusedMonth = paramInt;
      notifyDataSetChanged();
    }
    
    public void setSelectedDay(Calendar paramCalendar)
    {
      if ((paramCalendar.get(6) == this.mSelectedDate.get(6)) && (paramCalendar.get(1) == this.mSelectedDate.get(1))) {
        return;
      }
      this.mSelectedDate.setTimeInMillis(paramCalendar.getTimeInMillis());
      this.mSelectedWeek = CalendarViewLegacyDelegate.this.getWeeksSinceMinDate(this.mSelectedDate);
      this.mFocusedMonth = this.mSelectedDate.get(2);
      notifyDataSetChanged();
    }
    
    class CalendarGestureListener
      extends GestureDetector.SimpleOnGestureListener
    {
      CalendarGestureListener() {}
      
      public boolean onSingleTapUp(MotionEvent paramMotionEvent)
      {
        return true;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CalendarViewLegacyDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */