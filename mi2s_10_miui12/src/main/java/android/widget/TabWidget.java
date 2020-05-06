package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R.styleable;

public class TabWidget
  extends LinearLayout
  implements View.OnFocusChangeListener
{
  private final Rect mBounds = new Rect();
  @UnsupportedAppUsage
  private boolean mDrawBottomStrips = true;
  private int[] mImposedTabWidths;
  private int mImposedTabsHeight = -1;
  private Drawable mLeftStrip;
  private Drawable mRightStrip;
  @UnsupportedAppUsage
  private int mSelectedTab = -1;
  private OnTabSelectionChanged mSelectionChangedListener;
  private boolean mStripMoved;
  
  public TabWidget(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TabWidget(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842883);
  }
  
  public TabWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TabWidget(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TabWidget, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.TabWidget, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mDrawBottomStrips = localTypedArray.getBoolean(3, this.mDrawBottomStrips);
    if (paramContext.getApplicationInfo().targetSdkVersion <= 4) {
      paramInt1 = 1;
    } else {
      paramInt1 = 0;
    }
    if (localTypedArray.hasValueOrEmpty(1)) {
      this.mLeftStrip = localTypedArray.getDrawable(1);
    } else if (paramInt1 != 0) {
      this.mLeftStrip = paramContext.getDrawable(17303710);
    } else {
      this.mLeftStrip = paramContext.getDrawable(17303709);
    }
    if (localTypedArray.hasValueOrEmpty(2)) {
      this.mRightStrip = localTypedArray.getDrawable(2);
    } else if (paramInt1 != 0) {
      this.mRightStrip = paramContext.getDrawable(17303712);
    } else {
      this.mRightStrip = paramContext.getDrawable(17303711);
    }
    localTypedArray.recycle();
    setChildrenDrawingOrderEnabled(true);
  }
  
  public void addView(View paramView)
  {
    if (paramView.getLayoutParams() == null)
    {
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(0, -1, 1.0F);
      localLayoutParams.setMargins(0, 0, 0, 0);
      paramView.setLayoutParams(localLayoutParams);
    }
    paramView.setFocusable(true);
    paramView.setClickable(true);
    if (paramView.getPointerIcon() == null) {
      paramView.setPointerIcon(PointerIcon.getSystemIcon(getContext(), 1002));
    }
    super.addView(paramView);
    paramView.setOnClickListener(new TabClickListener(getTabCount() - 1, null));
  }
  
  public void childDrawableStateChanged(View paramView)
  {
    if ((getTabCount() > 0) && (paramView == getChildTabViewAt(this.mSelectedTab))) {
      invalidate();
    }
    super.childDrawableStateChanged(paramView);
  }
  
  public void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
    if (getTabCount() == 0) {
      return;
    }
    if (!this.mDrawBottomStrips) {
      return;
    }
    View localView = getChildTabViewAt(this.mSelectedTab);
    Drawable localDrawable1 = this.mLeftStrip;
    Drawable localDrawable2 = this.mRightStrip;
    if (localDrawable1 != null) {
      localDrawable1.setState(localView.getDrawableState());
    }
    if (localDrawable2 != null) {
      localDrawable2.setState(localView.getDrawableState());
    }
    if (this.mStripMoved)
    {
      Rect localRect = this.mBounds;
      localRect.left = localView.getLeft();
      localRect.right = localView.getRight();
      int i = getHeight();
      if (localDrawable1 != null) {
        localDrawable1.setBounds(Math.min(0, localRect.left - localDrawable1.getIntrinsicWidth()), i - localDrawable1.getIntrinsicHeight(), localRect.left, i);
      }
      if (localDrawable2 != null) {
        localDrawable2.setBounds(localRect.right, i - localDrawable2.getIntrinsicHeight(), Math.max(getWidth(), localRect.right + localDrawable2.getIntrinsicWidth()), i);
      }
      this.mStripMoved = false;
    }
    if (localDrawable1 != null) {
      localDrawable1.draw(paramCanvas);
    }
    if (localDrawable2 != null) {
      localDrawable2.draw(paramCanvas);
    }
  }
  
  public void focusCurrentTab(int paramInt)
  {
    int i = this.mSelectedTab;
    setCurrentTab(paramInt);
    if (i != paramInt) {
      getChildTabViewAt(paramInt).requestFocus();
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return TabWidget.class.getName();
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    int i = this.mSelectedTab;
    if (i == -1) {
      return paramInt2;
    }
    if (paramInt2 == paramInt1 - 1) {
      return i;
    }
    if (paramInt2 >= i) {
      return paramInt2 + 1;
    }
    return paramInt2;
  }
  
  public View getChildTabViewAt(int paramInt)
  {
    return getChildAt(paramInt);
  }
  
  public Drawable getLeftStripDrawable()
  {
    return this.mLeftStrip;
  }
  
  public Drawable getRightStripDrawable()
  {
    return this.mRightStrip;
  }
  
  public int getTabCount()
  {
    return getChildCount();
  }
  
  public boolean isStripEnabled()
  {
    return this.mDrawBottomStrips;
  }
  
  void measureChildBeforeLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = paramInt2;
    int j = paramInt4;
    if (!isMeasureWithLargestChildEnabled())
    {
      i = paramInt2;
      j = paramInt4;
      if (this.mImposedTabsHeight >= 0)
      {
        i = View.MeasureSpec.makeMeasureSpec(this.mImposedTabWidths[paramInt1] + paramInt3, 1073741824);
        j = View.MeasureSpec.makeMeasureSpec(this.mImposedTabsHeight, 1073741824);
      }
    }
    super.measureChildBeforeLayout(paramView, paramInt1, i, paramInt3, j, paramInt5);
  }
  
  void measureHorizontal(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) == 0)
    {
      super.measureHorizontal(paramInt1, paramInt2);
      return;
    }
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.makeSafeMeasureSpec(i, 0);
    this.mImposedTabsHeight = -1;
    super.measureHorizontal(j, paramInt2);
    int k = getMeasuredWidth() - i;
    if (k > 0)
    {
      int m = getChildCount();
      j = 0;
      for (i = 0; i < m; i++) {
        if (getChildAt(i).getVisibility() != 8) {
          j++;
        }
      }
      if (j > 0)
      {
        Object localObject = this.mImposedTabWidths;
        if ((localObject == null) || (localObject.length != m)) {
          this.mImposedTabWidths = new int[m];
        }
        for (i = 0; i < m; i++)
        {
          localObject = getChildAt(i);
          if (((View)localObject).getVisibility() != 8)
          {
            int n = ((View)localObject).getMeasuredWidth();
            int i1 = Math.max(0, n - k / j);
            this.mImposedTabWidths[i] = i1;
            k -= n - i1;
            j--;
            this.mImposedTabsHeight = Math.max(this.mImposedTabsHeight, ((View)localObject).getMeasuredHeight());
          }
        }
      }
    }
    super.measureHorizontal(paramInt1, paramInt2);
  }
  
  public void onFocusChange(View paramView, boolean paramBoolean) {}
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    paramAccessibilityEvent.setItemCount(getTabCount());
    paramAccessibilityEvent.setCurrentItemIndex(this.mSelectedTab);
  }
  
  public PointerIcon onResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt)
  {
    if (!isEnabled()) {
      return null;
    }
    return super.onResolvePointerIcon(paramMotionEvent, paramInt);
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mStripMoved = true;
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void removeAllViews()
  {
    super.removeAllViews();
    this.mSelectedTab = -1;
  }
  
  public void setCurrentTab(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < getTabCount()))
    {
      int i = this.mSelectedTab;
      if (paramInt != i)
      {
        if (i != -1) {
          getChildTabViewAt(i).setSelected(false);
        }
        this.mSelectedTab = paramInt;
        getChildTabViewAt(this.mSelectedTab).setSelected(true);
        this.mStripMoved = true;
        return;
      }
    }
  }
  
  public void setDividerDrawable(int paramInt)
  {
    setDividerDrawable(this.mContext.getDrawable(paramInt));
  }
  
  public void setDividerDrawable(Drawable paramDrawable)
  {
    super.setDividerDrawable(paramDrawable);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    int i = getTabCount();
    for (int j = 0; j < i; j++) {
      getChildTabViewAt(j).setEnabled(paramBoolean);
    }
  }
  
  public void setLeftStripDrawable(int paramInt)
  {
    setLeftStripDrawable(this.mContext.getDrawable(paramInt));
  }
  
  public void setLeftStripDrawable(Drawable paramDrawable)
  {
    this.mLeftStrip = paramDrawable;
    requestLayout();
    invalidate();
  }
  
  public void setRightStripDrawable(int paramInt)
  {
    setRightStripDrawable(this.mContext.getDrawable(paramInt));
  }
  
  public void setRightStripDrawable(Drawable paramDrawable)
  {
    this.mRightStrip = paramDrawable;
    requestLayout();
    invalidate();
  }
  
  public void setStripEnabled(boolean paramBoolean)
  {
    this.mDrawBottomStrips = paramBoolean;
    invalidate();
  }
  
  @UnsupportedAppUsage
  void setTabSelectionListener(OnTabSelectionChanged paramOnTabSelectionChanged)
  {
    this.mSelectionChangedListener = paramOnTabSelectionChanged;
  }
  
  static abstract interface OnTabSelectionChanged
  {
    public abstract void onTabSelectionChanged(int paramInt, boolean paramBoolean);
  }
  
  private class TabClickListener
    implements View.OnClickListener
  {
    private final int mTabIndex;
    
    private TabClickListener(int paramInt)
    {
      this.mTabIndex = paramInt;
    }
    
    public void onClick(View paramView)
    {
      TabWidget.this.mSelectionChangedListener.onTabSelectionChanged(this.mTabIndex, true);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TabWidget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */