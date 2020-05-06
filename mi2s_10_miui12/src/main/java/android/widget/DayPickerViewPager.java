package android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import com.android.internal.widget.ViewPager;
import com.android.internal.widget.ViewPager.LayoutParams;
import java.util.ArrayList;
import java.util.function.Predicate;

class DayPickerViewPager
  extends ViewPager
{
  private final ArrayList<View> mMatchParentChildren = new ArrayList(1);
  
  public DayPickerViewPager(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DayPickerViewPager(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DayPickerViewPager(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public DayPickerViewPager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  protected <T extends View> T findViewByPredicateTraversal(Predicate<View> paramPredicate, View paramView)
  {
    if (paramPredicate.test(this)) {
      return this;
    }
    SimpleMonthView localSimpleMonthView = ((DayPickerPagerAdapter)getAdapter()).getView(getCurrent());
    View localView;
    if ((localSimpleMonthView != paramView) && (localSimpleMonthView != null))
    {
      localView = localSimpleMonthView.findViewByPredicate(paramPredicate);
      if (localView != null) {
        return localView;
      }
    }
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      localView = getChildAt(j);
      if ((localView != paramView) && (localView != localSimpleMonthView))
      {
        localView = localView.findViewByPredicate(paramPredicate);
        if (localView != null) {
          return localView;
        }
      }
    }
    return null;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    populate();
    int i = getChildCount();
    if ((View.MeasureSpec.getMode(paramInt1) == 1073741824) && (View.MeasureSpec.getMode(paramInt2) == 1073741824)) {
      j = 0;
    } else {
      j = 1;
    }
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    View localView;
    while (i1 < i)
    {
      localView = getChildAt(i1);
      i2 = k;
      int i3 = m;
      int i4 = n;
      if (localView.getVisibility() != 8)
      {
        measureChild(localView, paramInt1, paramInt2);
        localObject = (ViewPager.LayoutParams)localView.getLayoutParams();
        m = Math.max(m, localView.getMeasuredWidth());
        k = Math.max(k, localView.getMeasuredHeight());
        n = combineMeasuredStates(n, localView.getMeasuredState());
        i2 = k;
        i3 = m;
        i4 = n;
        if (j != 0) {
          if (((ViewPager.LayoutParams)localObject).width != -1)
          {
            i2 = k;
            i3 = m;
            i4 = n;
            if (((ViewPager.LayoutParams)localObject).height != -1) {}
          }
          else
          {
            this.mMatchParentChildren.add(localView);
            i4 = n;
            i3 = m;
            i2 = k;
          }
        }
      }
      i1++;
      k = i2;
      m = i3;
      n = i4;
    }
    int j = getPaddingLeft();
    i1 = getPaddingRight();
    int i2 = Math.max(k + (getPaddingTop() + getPaddingBottom()), getSuggestedMinimumHeight());
    m = Math.max(m + (j + i1), getSuggestedMinimumWidth());
    Object localObject = getForeground();
    i1 = i2;
    j = m;
    if (localObject != null)
    {
      i1 = Math.max(i2, ((Drawable)localObject).getMinimumHeight());
      j = Math.max(m, ((Drawable)localObject).getMinimumWidth());
    }
    setMeasuredDimension(resolveSizeAndState(j, paramInt1, n), resolveSizeAndState(i1, paramInt2, n << 16));
    m = this.mMatchParentChildren.size();
    if (m > 1) {
      for (n = 0; n < m; n++)
      {
        localView = (View)this.mMatchParentChildren.get(n);
        localObject = (ViewPager.LayoutParams)localView.getLayoutParams();
        if (((ViewPager.LayoutParams)localObject).width == -1) {
          j = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 1073741824);
        } else {
          j = getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight(), ((ViewPager.LayoutParams)localObject).width);
        }
        if (((ViewPager.LayoutParams)localObject).height == -1) {
          i1 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), 1073741824);
        } else {
          i1 = getChildMeasureSpec(paramInt2, getPaddingTop() + getPaddingBottom(), ((ViewPager.LayoutParams)localObject).height);
        }
        localView.measure(j, i1);
      }
    }
    this.mMatchParentChildren.clear();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DayPickerViewPager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */