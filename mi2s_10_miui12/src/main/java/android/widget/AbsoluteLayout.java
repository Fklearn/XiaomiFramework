package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.android.internal.R.styleable;

@RemoteViews.RemoteView
@Deprecated
public class AbsoluteLayout
  extends ViewGroup
{
  public AbsoluteLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AbsoluteLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AbsoluteLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AbsoluteLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2, 0, 0);
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = getChildCount();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
    {
      View localView = getChildAt(paramInt1);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        paramInt4 = this.mPaddingLeft + localLayoutParams.x;
        paramInt3 = this.mPaddingTop + localLayoutParams.y;
        localView.layout(paramInt4, paramInt3, localView.getMeasuredWidth() + paramInt4, localView.getMeasuredHeight() + paramInt3);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    int j = 0;
    int k = 0;
    measureChildren(paramInt1, paramInt2);
    int m = 0;
    while (m < i)
    {
      View localView = getChildAt(m);
      int n = j;
      i1 = k;
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int i2 = localLayoutParams.x;
        i1 = localView.getMeasuredWidth();
        int i3 = localLayoutParams.y;
        n = localView.getMeasuredHeight();
        i1 = Math.max(k, i2 + i1);
        n = Math.max(j, i3 + n);
      }
      m++;
      j = n;
      k = i1;
    }
    m = this.mPaddingLeft;
    int i1 = this.mPaddingRight;
    j = Math.max(j + (this.mPaddingTop + this.mPaddingBottom), getSuggestedMinimumHeight());
    setMeasuredDimension(resolveSizeAndState(Math.max(k + (m + i1), getSuggestedMinimumWidth()), paramInt1, 0), resolveSizeAndState(j, paramInt2, 0));
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
  {
    public int x;
    public int y;
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt2);
      this.x = paramInt3;
      this.y = paramInt4;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AbsoluteLayout_Layout);
      this.x = paramContext.getDimensionPixelOffset(0, 0);
      this.y = paramContext.getDimensionPixelOffset(1, 0);
      paramContext.recycle();
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public String debug(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("Absolute.LayoutParams={width=");
      localStringBuilder.append(sizeToString(this.width));
      localStringBuilder.append(", height=");
      localStringBuilder.append(sizeToString(this.height));
      localStringBuilder.append(" x=");
      localStringBuilder.append(this.x);
      localStringBuilder.append(" y=");
      localStringBuilder.append(this.y);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AbsoluteLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */