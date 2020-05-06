package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ViewSwitcher
  extends ViewAnimator
{
  ViewFactory mFactory;
  
  public ViewSwitcher(Context paramContext)
  {
    super(paramContext);
  }
  
  public ViewSwitcher(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private View obtainView()
  {
    View localView = this.mFactory.makeView();
    FrameLayout.LayoutParams localLayoutParams1 = (FrameLayout.LayoutParams)localView.getLayoutParams();
    FrameLayout.LayoutParams localLayoutParams2 = localLayoutParams1;
    if (localLayoutParams1 == null) {
      localLayoutParams2 = new FrameLayout.LayoutParams(-1, -2);
    }
    addView(localView, localLayoutParams2);
    return localView;
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (getChildCount() < 2)
    {
      super.addView(paramView, paramInt, paramLayoutParams);
      return;
    }
    throw new IllegalStateException("Can't add more than 2 views to a ViewSwitcher");
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ViewSwitcher.class.getName();
  }
  
  public View getNextView()
  {
    int i;
    if (this.mWhichChild == 0) {
      i = 1;
    } else {
      i = 0;
    }
    return getChildAt(i);
  }
  
  public void reset()
  {
    this.mFirstTime = true;
    View localView = getChildAt(0);
    if (localView != null) {
      localView.setVisibility(8);
    }
    localView = getChildAt(1);
    if (localView != null) {
      localView.setVisibility(8);
    }
  }
  
  public void setFactory(ViewFactory paramViewFactory)
  {
    this.mFactory = paramViewFactory;
    obtainView();
    obtainView();
  }
  
  public static abstract interface ViewFactory
  {
    public abstract View makeView();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ViewSwitcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */