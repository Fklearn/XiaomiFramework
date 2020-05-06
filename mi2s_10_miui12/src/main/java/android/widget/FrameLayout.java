package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R.styleable;
import java.util.ArrayList;

@RemoteViews.RemoteView
public class FrameLayout
  extends ViewGroup
{
  private static final int DEFAULT_CHILD_GRAVITY = 8388659;
  @ViewDebug.ExportedProperty(category="padding")
  @UnsupportedAppUsage
  private int mForegroundPaddingBottom = 0;
  @ViewDebug.ExportedProperty(category="padding")
  @UnsupportedAppUsage
  private int mForegroundPaddingLeft = 0;
  @ViewDebug.ExportedProperty(category="padding")
  @UnsupportedAppUsage
  private int mForegroundPaddingRight = 0;
  @ViewDebug.ExportedProperty(category="padding")
  @UnsupportedAppUsage
  private int mForegroundPaddingTop = 0;
  private final ArrayList<View> mMatchParentChildren = new ArrayList(1);
  @ViewDebug.ExportedProperty(category="measurement")
  @UnsupportedAppUsage
  boolean mMeasureAllChildren = false;
  
  public FrameLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public FrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public FrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public FrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.FrameLayout, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.FrameLayout, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    if (localTypedArray.getBoolean(0, false)) {
      setMeasureAllChildren(true);
    }
    localTypedArray.recycle();
  }
  
  private int getPaddingBottomWithForeground()
  {
    int i;
    if (isForegroundInsidePadding()) {
      i = Math.max(this.mPaddingBottom, this.mForegroundPaddingBottom);
    } else {
      i = this.mPaddingBottom + this.mForegroundPaddingBottom;
    }
    return i;
  }
  
  private int getPaddingTopWithForeground()
  {
    int i;
    if (isForegroundInsidePadding()) {
      i = Math.max(this.mPaddingTop, this.mForegroundPaddingTop);
    } else {
      i = this.mPaddingTop + this.mForegroundPaddingTop;
    }
    return i;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("measurement:measureAllChildren", this.mMeasureAllChildren);
    paramViewHierarchyEncoder.addProperty("padding:foregroundPaddingLeft", this.mForegroundPaddingLeft);
    paramViewHierarchyEncoder.addProperty("padding:foregroundPaddingTop", this.mForegroundPaddingTop);
    paramViewHierarchyEncoder.addProperty("padding:foregroundPaddingRight", this.mForegroundPaddingRight);
    paramViewHierarchyEncoder.addProperty("padding:foregroundPaddingBottom", this.mForegroundPaddingBottom);
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (sPreserveMarginParamsInLayoutParamConversion)
    {
      if ((paramLayoutParams instanceof LayoutParams)) {
        return new LayoutParams((LayoutParams)paramLayoutParams);
      }
      if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
        return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
      }
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return FrameLayout.class.getName();
  }
  
  @Deprecated
  public boolean getConsiderGoneChildrenWhenMeasuring()
  {
    return getMeasureAllChildren();
  }
  
  public boolean getMeasureAllChildren()
  {
    return this.mMeasureAllChildren;
  }
  
  int getPaddingLeftWithForeground()
  {
    int i;
    if (isForegroundInsidePadding()) {
      i = Math.max(this.mPaddingLeft, this.mForegroundPaddingLeft);
    } else {
      i = this.mPaddingLeft + this.mForegroundPaddingLeft;
    }
    return i;
  }
  
  int getPaddingRightWithForeground()
  {
    int i;
    if (isForegroundInsidePadding()) {
      i = Math.max(this.mPaddingRight, this.mForegroundPaddingRight);
    } else {
      i = this.mPaddingRight + this.mForegroundPaddingRight;
    }
    return i;
  }
  
  void layoutChildren(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    int i = getChildCount();
    int j = getPaddingLeftWithForeground();
    paramInt3 = paramInt3 - paramInt1 - getPaddingRightWithForeground();
    int k = getPaddingTopWithForeground();
    int m = paramInt4 - paramInt2 - getPaddingBottomWithForeground();
    int n = 0;
    paramInt4 = j;
    while (n < i)
    {
      View localView = getChildAt(n);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        j = localView.getMeasuredWidth();
        int i1 = localView.getMeasuredHeight();
        paramInt2 = localLayoutParams.gravity;
        paramInt1 = paramInt2;
        if (paramInt2 == -1) {
          paramInt1 = 8388659;
        }
        paramInt2 = Gravity.getAbsoluteGravity(paramInt1, getLayoutDirection());
        paramInt1 &= 0x70;
        paramInt2 &= 0x7;
        if (paramInt2 != 1)
        {
          if ((paramInt2 == 5) && (!paramBoolean)) {
            paramInt2 = paramInt3 - j - localLayoutParams.rightMargin;
          } else {
            paramInt2 = localLayoutParams.leftMargin + paramInt4;
          }
        }
        else {
          paramInt2 = (paramInt3 - paramInt4 - j) / 2 + paramInt4 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
        }
        if (paramInt1 != 16)
        {
          if (paramInt1 != 48)
          {
            if (paramInt1 != 80) {
              paramInt1 = localLayoutParams.topMargin + k;
            } else {
              paramInt1 = m - i1 - localLayoutParams.bottomMargin;
            }
          }
          else {
            paramInt1 = k + localLayoutParams.topMargin;
          }
        }
        else {
          paramInt1 = (m - k - i1) / 2 + k + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
        }
        localView.layout(paramInt2, paramInt1, paramInt2 + j, paramInt1 + i1);
      }
      n++;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    layoutChildren(paramInt1, paramInt2, paramInt3, paramInt4, false);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    if ((View.MeasureSpec.getMode(paramInt1) == 1073741824) && (View.MeasureSpec.getMode(paramInt2) == 1073741824)) {
      j = 0;
    } else {
      j = 1;
    }
    this.mMatchParentChildren.clear();
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    Object localObject2;
    while (i1 < i)
    {
      localObject1 = getChildAt(i1);
      int i2;
      int i3;
      int i4;
      if (!this.mMeasureAllChildren)
      {
        i2 = n;
        i3 = k;
        i4 = m;
        if (((View)localObject1).getVisibility() == 8) {}
      }
      else
      {
        measureChildWithMargins((View)localObject1, paramInt1, 0, paramInt2, 0);
        localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
        i4 = Math.max(m, ((View)localObject1).getMeasuredWidth() + ((LayoutParams)localObject2).leftMargin + ((LayoutParams)localObject2).rightMargin);
        i3 = Math.max(k, ((View)localObject1).getMeasuredHeight() + ((LayoutParams)localObject2).topMargin + ((LayoutParams)localObject2).bottomMargin);
        i2 = combineMeasuredStates(n, ((View)localObject1).getMeasuredState());
        if ((j != 0) && ((((LayoutParams)localObject2).width == -1) || (((LayoutParams)localObject2).height == -1))) {
          this.mMatchParentChildren.add(localObject1);
        }
      }
      i1++;
      n = i2;
      k = i3;
      m = i4;
    }
    int j = getPaddingLeftWithForeground();
    i1 = getPaddingRightWithForeground();
    k = Math.max(k + (getPaddingTopWithForeground() + getPaddingBottomWithForeground()), getSuggestedMinimumHeight());
    m = Math.max(m + (j + i1), getSuggestedMinimumWidth());
    Object localObject1 = getForeground();
    i1 = k;
    j = m;
    if (localObject1 != null)
    {
      i1 = Math.max(k, ((Drawable)localObject1).getMinimumHeight());
      j = Math.max(m, ((Drawable)localObject1).getMinimumWidth());
    }
    setMeasuredDimension(resolveSizeAndState(j, paramInt1, n), resolveSizeAndState(i1, paramInt2, n << 16));
    m = this.mMatchParentChildren.size();
    if (m > 1) {
      for (j = 0; j < m; j++)
      {
        localObject2 = (View)this.mMatchParentChildren.get(j);
        localObject1 = (ViewGroup.MarginLayoutParams)((View)localObject2).getLayoutParams();
        if (((ViewGroup.MarginLayoutParams)localObject1).width == -1) {
          n = View.MeasureSpec.makeMeasureSpec(Math.max(0, getMeasuredWidth() - getPaddingLeftWithForeground() - getPaddingRightWithForeground() - ((ViewGroup.MarginLayoutParams)localObject1).leftMargin - ((ViewGroup.MarginLayoutParams)localObject1).rightMargin), 1073741824);
        } else {
          n = getChildMeasureSpec(paramInt1, getPaddingLeftWithForeground() + getPaddingRightWithForeground() + ((ViewGroup.MarginLayoutParams)localObject1).leftMargin + ((ViewGroup.MarginLayoutParams)localObject1).rightMargin, ((ViewGroup.MarginLayoutParams)localObject1).width);
        }
        if (((ViewGroup.MarginLayoutParams)localObject1).height == -1) {
          i1 = View.MeasureSpec.makeMeasureSpec(Math.max(0, getMeasuredHeight() - getPaddingTopWithForeground() - getPaddingBottomWithForeground() - ((ViewGroup.MarginLayoutParams)localObject1).topMargin - ((ViewGroup.MarginLayoutParams)localObject1).bottomMargin), 1073741824);
        } else {
          i1 = getChildMeasureSpec(paramInt2, getPaddingTopWithForeground() + getPaddingBottomWithForeground() + ((ViewGroup.MarginLayoutParams)localObject1).topMargin + ((ViewGroup.MarginLayoutParams)localObject1).bottomMargin, ((ViewGroup.MarginLayoutParams)localObject1).height);
        }
        ((View)localObject2).measure(n, i1);
      }
    }
  }
  
  @RemotableViewMethod
  public void setForegroundGravity(int paramInt)
  {
    if (getForegroundGravity() != paramInt)
    {
      super.setForegroundGravity(paramInt);
      Drawable localDrawable = getForeground();
      if ((getForegroundGravity() == 119) && (localDrawable != null))
      {
        Rect localRect = new Rect();
        if (localDrawable.getPadding(localRect))
        {
          this.mForegroundPaddingLeft = localRect.left;
          this.mForegroundPaddingTop = localRect.top;
          this.mForegroundPaddingRight = localRect.right;
          this.mForegroundPaddingBottom = localRect.bottom;
        }
      }
      else
      {
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
      }
      requestLayout();
    }
  }
  
  @RemotableViewMethod
  public void setMeasureAllChildren(boolean paramBoolean)
  {
    this.mMeasureAllChildren = paramBoolean;
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public static final int UNSPECIFIED_GRAVITY = -1;
    public int gravity = -1;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2);
      this.gravity = paramInt3;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.FrameLayout_Layout);
      this.gravity = paramContext.getInt(0, -1);
      paramContext.recycle();
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.gravity = paramLayoutParams.gravity;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/FrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */