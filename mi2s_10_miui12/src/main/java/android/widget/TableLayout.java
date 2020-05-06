package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import com.android.internal.R.styleable;
import java.util.regex.Pattern;

public class TableLayout
  extends LinearLayout
{
  private SparseBooleanArray mCollapsedColumns;
  private boolean mInitialized;
  private int[] mMaxWidths;
  private PassThroughHierarchyChangeListener mPassThroughListener;
  private boolean mShrinkAllColumns;
  private SparseBooleanArray mShrinkableColumns;
  private boolean mStretchAllColumns;
  private SparseBooleanArray mStretchableColumns;
  
  public TableLayout(Context paramContext)
  {
    super(paramContext);
    initTableLayout();
  }
  
  public TableLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TableLayout);
    paramAttributeSet = paramContext.getString(0);
    if (paramAttributeSet != null) {
      if (paramAttributeSet.charAt(0) == '*') {
        this.mStretchAllColumns = true;
      } else {
        this.mStretchableColumns = parseColumns(paramAttributeSet);
      }
    }
    paramAttributeSet = paramContext.getString(1);
    if (paramAttributeSet != null) {
      if (paramAttributeSet.charAt(0) == '*') {
        this.mShrinkAllColumns = true;
      } else {
        this.mShrinkableColumns = parseColumns(paramAttributeSet);
      }
    }
    paramAttributeSet = paramContext.getString(2);
    if (paramAttributeSet != null) {
      this.mCollapsedColumns = parseColumns(paramAttributeSet);
    }
    paramContext.recycle();
    initTableLayout();
  }
  
  private void findLargestCells(int paramInt1, int paramInt2)
  {
    int i = 1;
    int j = getChildCount();
    for (int k = 0; k < j; k++)
    {
      Object localObject = getChildAt(k);
      if ((((View)localObject).getVisibility() != 8) && ((localObject instanceof TableRow)))
      {
        localObject = (TableRow)localObject;
        ((TableRow)localObject).getLayoutParams().height = -2;
        localObject = ((TableRow)localObject).getColumnsWidths(paramInt1, paramInt2);
        int m = localObject.length;
        int[] arrayOfInt;
        if (i != 0)
        {
          arrayOfInt = this.mMaxWidths;
          if ((arrayOfInt == null) || (arrayOfInt.length != m)) {
            this.mMaxWidths = new int[m];
          }
          System.arraycopy(localObject, 0, this.mMaxWidths, 0, m);
          i = 0;
        }
        else
        {
          int n = this.mMaxWidths.length;
          int i1 = m - n;
          if (i1 > 0)
          {
            arrayOfInt = this.mMaxWidths;
            this.mMaxWidths = new int[m];
            System.arraycopy(arrayOfInt, 0, this.mMaxWidths, 0, arrayOfInt.length);
            System.arraycopy(localObject, arrayOfInt.length, this.mMaxWidths, arrayOfInt.length, i1);
          }
          arrayOfInt = this.mMaxWidths;
          n = Math.min(n, m);
          for (i1 = 0; i1 < n; i1++) {
            arrayOfInt[i1] = Math.max(arrayOfInt[i1], localObject[i1]);
          }
        }
      }
    }
  }
  
  private void initTableLayout()
  {
    if (this.mCollapsedColumns == null) {
      this.mCollapsedColumns = new SparseBooleanArray();
    }
    if (this.mStretchableColumns == null) {
      this.mStretchableColumns = new SparseBooleanArray();
    }
    if (this.mShrinkableColumns == null) {
      this.mShrinkableColumns = new SparseBooleanArray();
    }
    setOrientation(1);
    this.mPassThroughListener = new PassThroughHierarchyChangeListener(null);
    super.setOnHierarchyChangeListener(this.mPassThroughListener);
    this.mInitialized = true;
  }
  
  private void mutateColumnsWidth(SparseBooleanArray paramSparseBooleanArray, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    int i = 0;
    int[] arrayOfInt = this.mMaxWidths;
    int j = arrayOfInt.length;
    int k;
    if (paramBoolean) {
      k = j;
    } else {
      k = paramSparseBooleanArray.size();
    }
    int m = (paramInt1 - paramInt2) / k;
    paramInt2 = getChildCount();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
    {
      View localView = getChildAt(paramInt1);
      if ((localView instanceof TableRow)) {
        localView.forceLayout();
      }
    }
    if (!paramBoolean)
    {
      paramInt1 = 0;
      for (paramInt2 = i; paramInt1 < k; paramInt2 = i)
      {
        int n = paramSparseBooleanArray.keyAt(paramInt1);
        i = paramInt2;
        if (paramSparseBooleanArray.valueAt(paramInt1)) {
          if (n < j)
          {
            arrayOfInt[n] += m;
            i = paramInt2;
          }
          else
          {
            i = paramInt2 + 1;
          }
        }
        paramInt1++;
      }
      if ((paramInt2 > 0) && (paramInt2 < k))
      {
        paramInt2 = paramInt2 * m / (k - paramInt2);
        for (paramInt1 = 0; paramInt1 < k; paramInt1++)
        {
          i = paramSparseBooleanArray.keyAt(paramInt1);
          if ((paramSparseBooleanArray.valueAt(paramInt1)) && (i < j)) {
            if (paramInt2 > arrayOfInt[i]) {
              arrayOfInt[i] = 0;
            } else {
              arrayOfInt[i] += paramInt2;
            }
          }
        }
      }
      return;
    }
    for (paramInt1 = 0; paramInt1 < k; paramInt1++) {
      arrayOfInt[paramInt1] += m;
    }
  }
  
  private static SparseBooleanArray parseColumns(String paramString)
  {
    SparseBooleanArray localSparseBooleanArray = new SparseBooleanArray();
    for (String str : Pattern.compile("\\s*,\\s*").split(paramString)) {
      try
      {
        int k = Integer.parseInt(str);
        if (k >= 0) {
          localSparseBooleanArray.put(k, true);
        }
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return localSparseBooleanArray;
  }
  
  private void requestRowsLayout()
  {
    if (this.mInitialized)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++) {
        getChildAt(j).requestLayout();
      }
    }
  }
  
  private void shrinkAndStretchColumns(int paramInt)
  {
    int[] arrayOfInt = this.mMaxWidths;
    if (arrayOfInt == null) {
      return;
    }
    int i = 0;
    int j = arrayOfInt.length;
    for (int k = 0; k < j; k++) {
      i += arrayOfInt[k];
    }
    paramInt = View.MeasureSpec.getSize(paramInt) - this.mPaddingLeft - this.mPaddingRight;
    if ((i > paramInt) && ((this.mShrinkAllColumns) || (this.mShrinkableColumns.size() > 0))) {
      mutateColumnsWidth(this.mShrinkableColumns, this.mShrinkAllColumns, paramInt, i);
    } else if ((i < paramInt) && ((this.mStretchAllColumns) || (this.mStretchableColumns.size() > 0))) {
      mutateColumnsWidth(this.mStretchableColumns, this.mStretchAllColumns, paramInt, i);
    }
  }
  
  private void trackCollapsedColumns(View paramView)
  {
    if ((paramView instanceof TableRow))
    {
      TableRow localTableRow = (TableRow)paramView;
      paramView = this.mCollapsedColumns;
      int i = paramView.size();
      for (int j = 0; j < i; j++)
      {
        int k = paramView.keyAt(j);
        boolean bool = paramView.valueAt(j);
        if (bool) {
          localTableRow.setColumnCollapsed(k, bool);
        }
      }
    }
  }
  
  public void addView(View paramView)
  {
    super.addView(paramView);
    requestRowsLayout();
  }
  
  public void addView(View paramView, int paramInt)
  {
    super.addView(paramView, paramInt);
    requestRowsLayout();
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramInt, paramLayoutParams);
    requestRowsLayout();
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramLayoutParams);
    requestRowsLayout();
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
    return TableLayout.class.getName();
  }
  
  public boolean isColumnCollapsed(int paramInt)
  {
    return this.mCollapsedColumns.get(paramInt);
  }
  
  public boolean isColumnShrinkable(int paramInt)
  {
    boolean bool;
    if ((!this.mShrinkAllColumns) && (!this.mShrinkableColumns.get(paramInt))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isColumnStretchable(int paramInt)
  {
    boolean bool;
    if ((!this.mStretchAllColumns) && (!this.mStretchableColumns.get(paramInt))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isShrinkAllColumns()
  {
    return this.mShrinkAllColumns;
  }
  
  public boolean isStretchAllColumns()
  {
    return this.mStretchAllColumns;
  }
  
  void measureChildBeforeLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if ((paramView instanceof TableRow)) {
      ((TableRow)paramView).setColumnsWidthConstraints(this.mMaxWidths);
    }
    super.measureChildBeforeLayout(paramView, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  void measureVertical(int paramInt1, int paramInt2)
  {
    findLargestCells(paramInt1, paramInt2);
    shrinkAndStretchColumns(paramInt1);
    super.measureVertical(paramInt1, paramInt2);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    layoutVertical(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    measureVertical(paramInt1, paramInt2);
  }
  
  public void requestLayout()
  {
    if (this.mInitialized)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++) {
        getChildAt(j).forceLayout();
      }
    }
    super.requestLayout();
  }
  
  public void setColumnCollapsed(int paramInt, boolean paramBoolean)
  {
    this.mCollapsedColumns.put(paramInt, paramBoolean);
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((localView instanceof TableRow)) {
        ((TableRow)localView).setColumnCollapsed(paramInt, paramBoolean);
      }
    }
    requestRowsLayout();
  }
  
  public void setColumnShrinkable(int paramInt, boolean paramBoolean)
  {
    this.mShrinkableColumns.put(paramInt, paramBoolean);
    requestRowsLayout();
  }
  
  public void setColumnStretchable(int paramInt, boolean paramBoolean)
  {
    this.mStretchableColumns.put(paramInt, paramBoolean);
    requestRowsLayout();
  }
  
  public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener paramOnHierarchyChangeListener)
  {
    PassThroughHierarchyChangeListener.access$102(this.mPassThroughListener, paramOnHierarchyChangeListener);
  }
  
  public void setShrinkAllColumns(boolean paramBoolean)
  {
    this.mShrinkAllColumns = paramBoolean;
  }
  
  public void setStretchAllColumns(boolean paramBoolean)
  {
    this.mStretchAllColumns = paramBoolean;
  }
  
  public static class LayoutParams
    extends LinearLayout.LayoutParams
  {
    public LayoutParams()
    {
      super(-2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2, paramFloat);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
      this.width = -1;
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
      this.width = -1;
      if ((paramMarginLayoutParams instanceof LayoutParams)) {
        this.weight = ((LayoutParams)paramMarginLayoutParams).weight;
      }
    }
    
    protected void setBaseAttributes(TypedArray paramTypedArray, int paramInt1, int paramInt2)
    {
      this.width = -1;
      if (paramTypedArray.hasValue(paramInt2)) {
        this.height = paramTypedArray.getLayoutDimension(paramInt2, "layout_height");
      } else {
        this.height = -2;
      }
    }
  }
  
  private class PassThroughHierarchyChangeListener
    implements ViewGroup.OnHierarchyChangeListener
  {
    private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;
    
    private PassThroughHierarchyChangeListener() {}
    
    public void onChildViewAdded(View paramView1, View paramView2)
    {
      TableLayout.this.trackCollapsedColumns(paramView2);
      ViewGroup.OnHierarchyChangeListener localOnHierarchyChangeListener = this.mOnHierarchyChangeListener;
      if (localOnHierarchyChangeListener != null) {
        localOnHierarchyChangeListener.onChildViewAdded(paramView1, paramView2);
      }
    }
    
    public void onChildViewRemoved(View paramView1, View paramView2)
    {
      ViewGroup.OnHierarchyChangeListener localOnHierarchyChangeListener = this.mOnHierarchyChangeListener;
      if (localOnHierarchyChangeListener != null) {
        localOnHierarchyChangeListener.onChildViewRemoved(paramView1, paramView2);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TableLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */