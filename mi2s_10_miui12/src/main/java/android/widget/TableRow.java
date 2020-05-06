package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R.styleable;

public class TableRow
  extends LinearLayout
{
  private ChildrenTracker mChildrenTracker;
  private SparseIntArray mColumnToChildIndex;
  private int[] mColumnWidths;
  private int[] mConstrainedColumnWidths;
  private int mNumColumns = 0;
  
  public TableRow(Context paramContext)
  {
    super(paramContext);
    initTableRow();
  }
  
  public TableRow(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initTableRow();
  }
  
  private void initTableRow()
  {
    ViewGroup.OnHierarchyChangeListener localOnHierarchyChangeListener = this.mOnHierarchyChangeListener;
    this.mChildrenTracker = new ChildrenTracker(null);
    if (localOnHierarchyChangeListener != null) {
      this.mChildrenTracker.setOnHierarchyChangeListener(localOnHierarchyChangeListener);
    }
    super.setOnHierarchyChangeListener(this.mChildrenTracker);
  }
  
  private void mapIndexAndColumns()
  {
    if (this.mColumnToChildIndex == null)
    {
      int i = 0;
      int j = getChildCount();
      this.mColumnToChildIndex = new SparseIntArray();
      SparseIntArray localSparseIntArray = this.mColumnToChildIndex;
      int k = 0;
      while (k < j)
      {
        LayoutParams localLayoutParams = (LayoutParams)getChildAt(k).getLayoutParams();
        int m = i;
        if (localLayoutParams.column >= i) {
          m = localLayoutParams.column;
        }
        i = 0;
        while (i < localLayoutParams.span)
        {
          localSparseIntArray.put(m, k);
          i++;
          m++;
        }
        k++;
        i = m;
      }
      this.mNumColumns = i;
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  protected LinearLayout.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams();
  }
  
  protected LinearLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return TableRow.class.getName();
  }
  
  int getChildrenSkipCount(View paramView, int paramInt)
  {
    return ((LayoutParams)paramView.getLayoutParams()).span - 1;
  }
  
  int[] getColumnsWidths(int paramInt1, int paramInt2)
  {
    int i = getVirtualChildCount();
    int[] arrayOfInt = this.mColumnWidths;
    if ((arrayOfInt == null) || (i != arrayOfInt.length)) {
      this.mColumnWidths = new int[i];
    }
    arrayOfInt = this.mColumnWidths;
    for (int j = 0; j < i; j++)
    {
      View localView = getVirtualChildAt(j);
      if ((localView != null) && (localView.getVisibility() != 8))
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.span == 1)
        {
          int k = localLayoutParams.width;
          if (k != -2)
          {
            if (k != -1) {
              k = View.MeasureSpec.makeMeasureSpec(localLayoutParams.width, 1073741824);
            } else {
              k = View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 0);
            }
          }
          else {
            k = getChildMeasureSpec(paramInt1, 0, -2);
          }
          localView.measure(k, k);
          arrayOfInt[j] = (localView.getMeasuredWidth() + localLayoutParams.leftMargin + localLayoutParams.rightMargin);
        }
        else
        {
          arrayOfInt[j] = 0;
        }
      }
      else
      {
        arrayOfInt[j] = 0;
      }
    }
    return arrayOfInt;
  }
  
  int getLocationOffset(View paramView)
  {
    return ((LayoutParams)paramView.getLayoutParams()).mOffset[0];
  }
  
  int getNextLocationOffset(View paramView)
  {
    return ((LayoutParams)paramView.getLayoutParams()).mOffset[1];
  }
  
  public View getVirtualChildAt(int paramInt)
  {
    if (this.mColumnToChildIndex == null) {
      mapIndexAndColumns();
    }
    paramInt = this.mColumnToChildIndex.get(paramInt, -1);
    if (paramInt != -1) {
      return getChildAt(paramInt);
    }
    return null;
  }
  
  public int getVirtualChildCount()
  {
    if (this.mColumnToChildIndex == null) {
      mapIndexAndColumns();
    }
    return this.mNumColumns;
  }
  
  void measureChildBeforeLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (this.mConstrainedColumnWidths != null)
    {
      LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
      int i = 1073741824;
      paramInt2 = 0;
      int j = localLayoutParams.span;
      int[] arrayOfInt = this.mConstrainedColumnWidths;
      for (paramInt3 = 0; paramInt3 < j; paramInt3++) {
        paramInt2 += arrayOfInt[(paramInt1 + paramInt3)];
      }
      paramInt3 = localLayoutParams.gravity;
      boolean bool = Gravity.isHorizontal(paramInt3);
      paramInt1 = i;
      if (bool) {
        paramInt1 = Integer.MIN_VALUE;
      }
      paramView.measure(View.MeasureSpec.makeMeasureSpec(Math.max(0, paramInt2 - localLayoutParams.leftMargin - localLayoutParams.rightMargin), paramInt1), getChildMeasureSpec(paramInt4, this.mPaddingTop + this.mPaddingBottom + localLayoutParams.topMargin + localLayoutParams.bottomMargin + paramInt5, localLayoutParams.height));
      if (bool)
      {
        paramInt1 = paramView.getMeasuredWidth();
        localLayoutParams.mOffset[1] = (paramInt2 - paramInt1);
        paramInt1 = Gravity.getAbsoluteGravity(paramInt3, getLayoutDirection()) & 0x7;
        if (paramInt1 != 1)
        {
          if (paramInt1 != 3)
          {
            if (paramInt1 != 5) {
              return;
            }
            localLayoutParams.mOffset[0] = localLayoutParams.mOffset[1];
          }
        }
        else {
          localLayoutParams.mOffset[0] = (localLayoutParams.mOffset[1] / 2);
        }
      }
      else
      {
        paramView = localLayoutParams.mOffset;
        localLayoutParams.mOffset[1] = 0;
        paramView[0] = 0;
      }
    }
    else
    {
      super.measureChildBeforeLayout(paramView, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
  }
  
  int measureNullChild(int paramInt)
  {
    return this.mConstrainedColumnWidths[paramInt];
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    layoutHorizontal(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    measureHorizontal(paramInt1, paramInt2);
  }
  
  void setColumnCollapsed(int paramInt, boolean paramBoolean)
  {
    View localView = getVirtualChildAt(paramInt);
    if (localView != null)
    {
      if (paramBoolean) {
        paramInt = 8;
      } else {
        paramInt = 0;
      }
      localView.setVisibility(paramInt);
    }
  }
  
  void setColumnsWidthConstraints(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length >= getVirtualChildCount()))
    {
      this.mConstrainedColumnWidths = paramArrayOfInt;
      return;
    }
    throw new IllegalArgumentException("columnWidths should be >= getVirtualChildCount()");
  }
  
  public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener paramOnHierarchyChangeListener)
  {
    this.mChildrenTracker.setOnHierarchyChangeListener(paramOnHierarchyChangeListener);
  }
  
  private class ChildrenTracker
    implements ViewGroup.OnHierarchyChangeListener
  {
    private ViewGroup.OnHierarchyChangeListener listener;
    
    private ChildrenTracker() {}
    
    private void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener paramOnHierarchyChangeListener)
    {
      this.listener = paramOnHierarchyChangeListener;
    }
    
    public void onChildViewAdded(View paramView1, View paramView2)
    {
      TableRow.access$302(TableRow.this, null);
      ViewGroup.OnHierarchyChangeListener localOnHierarchyChangeListener = this.listener;
      if (localOnHierarchyChangeListener != null) {
        localOnHierarchyChangeListener.onChildViewAdded(paramView1, paramView2);
      }
    }
    
    public void onChildViewRemoved(View paramView1, View paramView2)
    {
      TableRow.access$302(TableRow.this, null);
      ViewGroup.OnHierarchyChangeListener localOnHierarchyChangeListener = this.listener;
      if (localOnHierarchyChangeListener != null) {
        localOnHierarchyChangeListener.onChildViewRemoved(paramView1, paramView2);
      }
    }
  }
  
  public static class LayoutParams
    extends LinearLayout.LayoutParams
  {
    private static final int LOCATION = 0;
    private static final int LOCATION_NEXT = 1;
    @ViewDebug.ExportedProperty(category="layout")
    public int column;
    private int[] mOffset = new int[2];
    @ViewDebug.ExportedProperty(category="layout")
    public int span;
    
    public LayoutParams()
    {
      super(-2);
      this.column = -1;
      this.span = 1;
    }
    
    public LayoutParams(int paramInt)
    {
      this();
      this.column = paramInt;
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.column = -1;
      this.span = 1;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2, paramFloat);
      this.column = -1;
      this.span = 1;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TableRow_Cell);
      this.column = paramContext.getInt(0, -1);
      this.span = paramContext.getInt(1, 1);
      if (this.span <= 1) {
        this.span = 1;
      }
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
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("layout:column", this.column);
      paramViewHierarchyEncoder.addProperty("layout:span", this.span);
    }
    
    protected void setBaseAttributes(TypedArray paramTypedArray, int paramInt1, int paramInt2)
    {
      if (paramTypedArray.hasValue(paramInt1)) {
        this.width = paramTypedArray.getLayoutDimension(paramInt1, "layout_width");
      } else {
        this.width = -1;
      }
      if (paramTypedArray.hasValue(paramInt2)) {
        this.height = paramTypedArray.getLayoutDimension(paramInt2, "layout_height");
      } else {
        this.height = -2;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TableRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */