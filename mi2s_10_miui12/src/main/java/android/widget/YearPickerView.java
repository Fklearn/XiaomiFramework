package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

class YearPickerView
  extends ListView
{
  private final YearAdapter mAdapter;
  private final int mChildSize;
  private OnYearSelectedListener mOnYearSelectedListener;
  private final int mViewSize;
  
  public YearPickerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842868);
  }
  
  public YearPickerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public YearPickerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayoutParams(new AbsListView.LayoutParams(-1, -2));
    paramContext = paramContext.getResources();
    this.mViewSize = paramContext.getDimensionPixelOffset(17105106);
    this.mChildSize = paramContext.getDimensionPixelOffset(17105107);
    setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        paramAnonymousInt = YearPickerView.this.mAdapter.getYearForPosition(paramAnonymousInt);
        YearPickerView.this.mAdapter.setSelection(paramAnonymousInt);
        if (YearPickerView.this.mOnYearSelectedListener != null) {
          YearPickerView.this.mOnYearSelectedListener.onYearChanged(YearPickerView.this, paramAnonymousInt);
        }
      }
    });
    this.mAdapter = new YearAdapter(getContext());
    setAdapter(this.mAdapter);
  }
  
  public int getFirstPositionOffset()
  {
    View localView = getChildAt(0);
    if (localView == null) {
      return 0;
    }
    return localView.getTop();
  }
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    if (paramAccessibilityEvent.getEventType() == 4096)
    {
      paramAccessibilityEvent.setFromIndex(0);
      paramAccessibilityEvent.setToIndex(0);
    }
  }
  
  public void setOnYearSelectedListener(OnYearSelectedListener paramOnYearSelectedListener)
  {
    this.mOnYearSelectedListener = paramOnYearSelectedListener;
  }
  
  public void setRange(Calendar paramCalendar1, Calendar paramCalendar2)
  {
    this.mAdapter.setRange(paramCalendar1, paramCalendar2);
  }
  
  public void setSelectionCentered(int paramInt)
  {
    setSelectionFromTop(paramInt, this.mViewSize / 2 - this.mChildSize / 2);
  }
  
  public void setYear(final int paramInt)
  {
    this.mAdapter.setSelection(paramInt);
    post(new Runnable()
    {
      public void run()
      {
        int i = YearPickerView.this.mAdapter.getPositionForYear(paramInt);
        if ((i >= 0) && (i < YearPickerView.this.getCount())) {
          YearPickerView.this.setSelectionCentered(i);
        }
      }
    });
  }
  
  public static abstract interface OnYearSelectedListener
  {
    public abstract void onYearChanged(YearPickerView paramYearPickerView, int paramInt);
  }
  
  private static class YearAdapter
    extends BaseAdapter
  {
    private static final int ITEM_LAYOUT = 17367366;
    private static final int ITEM_TEXT_ACTIVATED_APPEARANCE = 16974768;
    private static final int ITEM_TEXT_APPEARANCE = 16974767;
    private int mActivatedYear;
    private int mCount;
    private final LayoutInflater mInflater;
    private int mMinYear;
    
    public YearAdapter(Context paramContext)
    {
      this.mInflater = LayoutInflater.from(paramContext);
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      return this.mCount;
    }
    
    public Integer getItem(int paramInt)
    {
      return Integer.valueOf(getYearForPosition(paramInt));
    }
    
    public long getItemId(int paramInt)
    {
      return getYearForPosition(paramInt);
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public int getPositionForYear(int paramInt)
    {
      return paramInt - this.mMinYear;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool = true;
      int i;
      if (paramView == null) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0) {
        paramView = (TextView)this.mInflater.inflate(17367366, paramViewGroup, false);
      } else {
        paramView = (TextView)paramView;
      }
      int j = getYearForPosition(paramInt);
      if (this.mActivatedYear != j) {
        bool = false;
      }
      if ((i != 0) || (paramView.isActivated() != bool))
      {
        if (bool) {
          paramInt = 16974768;
        } else {
          paramInt = 16974767;
        }
        paramView.setTextAppearance(paramInt);
        paramView.setActivated(bool);
      }
      paramView.setText(Integer.toString(j));
      return paramView;
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public int getYearForPosition(int paramInt)
    {
      return this.mMinYear + paramInt;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return true;
    }
    
    public void setRange(Calendar paramCalendar1, Calendar paramCalendar2)
    {
      int i = paramCalendar1.get(1);
      int j = paramCalendar2.get(1) - i + 1;
      if ((this.mMinYear != i) || (this.mCount != j))
      {
        this.mMinYear = i;
        this.mCount = j;
        notifyDataSetInvalidated();
      }
    }
    
    public boolean setSelection(int paramInt)
    {
      if (this.mActivatedYear != paramInt)
      {
        this.mActivatedYear = paramInt;
        notifyDataSetChanged();
        return true;
      }
      return false;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/YearPickerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */