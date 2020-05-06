package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.internal.R.styleable;

public class ViewAnimator
  extends FrameLayout
{
  boolean mAnimateFirstTime = true;
  @UnsupportedAppUsage
  boolean mFirstTime = true;
  Animation mInAnimation;
  Animation mOutAnimation;
  @UnsupportedAppUsage
  int mWhichChild = 0;
  
  public ViewAnimator(Context paramContext)
  {
    super(paramContext);
    initViewAnimator(paramContext, null);
  }
  
  public ViewAnimator(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewAnimator);
    int i = localTypedArray.getResourceId(0, 0);
    if (i > 0) {
      setInAnimation(paramContext, i);
    }
    i = localTypedArray.getResourceId(1, 0);
    if (i > 0) {
      setOutAnimation(paramContext, i);
    }
    setAnimateFirstView(localTypedArray.getBoolean(2, true));
    localTypedArray.recycle();
    initViewAnimator(paramContext, paramAttributeSet);
  }
  
  private void initViewAnimator(Context paramContext, AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet == null)
    {
      this.mMeasureAllChildren = true;
      return;
    }
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.FrameLayout);
    setMeasureAllChildren(paramContext.getBoolean(0, true));
    paramContext.recycle();
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramInt, paramLayoutParams);
    if (getChildCount() == 1) {
      paramView.setVisibility(0);
    } else {
      paramView.setVisibility(8);
    }
    if (paramInt >= 0)
    {
      int i = this.mWhichChild;
      if (i >= paramInt) {
        setDisplayedChild(i + 1);
      }
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ViewAnimator.class.getName();
  }
  
  public boolean getAnimateFirstView()
  {
    return this.mAnimateFirstTime;
  }
  
  public int getBaseline()
  {
    int i;
    if (getCurrentView() != null) {
      i = getCurrentView().getBaseline();
    } else {
      i = super.getBaseline();
    }
    return i;
  }
  
  public View getCurrentView()
  {
    return getChildAt(this.mWhichChild);
  }
  
  public int getDisplayedChild()
  {
    return this.mWhichChild;
  }
  
  public Animation getInAnimation()
  {
    return this.mInAnimation;
  }
  
  public Animation getOutAnimation()
  {
    return this.mOutAnimation;
  }
  
  public void removeAllViews()
  {
    super.removeAllViews();
    this.mWhichChild = 0;
    this.mFirstTime = true;
  }
  
  public void removeView(View paramView)
  {
    int i = indexOfChild(paramView);
    if (i >= 0) {
      removeViewAt(i);
    }
  }
  
  public void removeViewAt(int paramInt)
  {
    super.removeViewAt(paramInt);
    int i = getChildCount();
    if (i == 0)
    {
      this.mWhichChild = 0;
      this.mFirstTime = true;
    }
    else
    {
      int j = this.mWhichChild;
      if (j >= i) {
        setDisplayedChild(i - 1);
      } else if (j == paramInt) {
        setDisplayedChild(j);
      }
    }
  }
  
  public void removeViewInLayout(View paramView)
  {
    removeView(paramView);
  }
  
  public void removeViews(int paramInt1, int paramInt2)
  {
    super.removeViews(paramInt1, paramInt2);
    if (getChildCount() == 0)
    {
      this.mWhichChild = 0;
      this.mFirstTime = true;
    }
    else
    {
      int i = this.mWhichChild;
      if ((i >= paramInt1) && (i < paramInt1 + paramInt2)) {
        setDisplayedChild(i);
      }
    }
  }
  
  public void removeViewsInLayout(int paramInt1, int paramInt2)
  {
    removeViews(paramInt1, paramInt2);
  }
  
  public void setAnimateFirstView(boolean paramBoolean)
  {
    this.mAnimateFirstTime = paramBoolean;
  }
  
  @RemotableViewMethod
  public void setDisplayedChild(int paramInt)
  {
    this.mWhichChild = paramInt;
    int i = getChildCount();
    int j = 1;
    if (paramInt >= i) {
      this.mWhichChild = 0;
    } else if (paramInt < 0) {
      this.mWhichChild = (getChildCount() - 1);
    }
    if (getFocusedChild() != null) {
      paramInt = j;
    } else {
      paramInt = 0;
    }
    showOnly(this.mWhichChild);
    if (paramInt != 0) {
      requestFocus(2);
    }
  }
  
  public void setInAnimation(Context paramContext, int paramInt)
  {
    setInAnimation(AnimationUtils.loadAnimation(paramContext, paramInt));
  }
  
  public void setInAnimation(Animation paramAnimation)
  {
    this.mInAnimation = paramAnimation;
  }
  
  public void setOutAnimation(Context paramContext, int paramInt)
  {
    setOutAnimation(AnimationUtils.loadAnimation(paramContext, paramInt));
  }
  
  public void setOutAnimation(Animation paramAnimation)
  {
    this.mOutAnimation = paramAnimation;
  }
  
  @RemotableViewMethod
  public void showNext()
  {
    setDisplayedChild(this.mWhichChild + 1);
  }
  
  void showOnly(int paramInt)
  {
    boolean bool;
    if ((this.mFirstTime) && (!this.mAnimateFirstTime)) {
      bool = false;
    } else {
      bool = true;
    }
    showOnly(paramInt, bool);
  }
  
  @UnsupportedAppUsage
  void showOnly(int paramInt, boolean paramBoolean)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if (j == paramInt)
      {
        if (paramBoolean)
        {
          Animation localAnimation = this.mInAnimation;
          if (localAnimation != null) {
            localView.startAnimation(localAnimation);
          }
        }
        localView.setVisibility(0);
        this.mFirstTime = false;
      }
      else
      {
        if ((paramBoolean) && (this.mOutAnimation != null) && (localView.getVisibility() == 0)) {
          localView.startAnimation(this.mOutAnimation);
        } else if (localView.getAnimation() == this.mInAnimation) {
          localView.clearAnimation();
        }
        localView.setVisibility(8);
      }
    }
  }
  
  @RemotableViewMethod
  public void showPrevious()
  {
    setDisplayedChild(this.mWhichChild - 1);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ViewAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */