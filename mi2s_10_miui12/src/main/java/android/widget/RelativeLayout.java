package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ResourceId;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Pools.SynchronizedPool;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import com.android.internal.R.styleable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

@RemoteViews.RemoteView
public class RelativeLayout
  extends ViewGroup
{
  public static final int ABOVE = 2;
  public static final int ALIGN_BASELINE = 4;
  public static final int ALIGN_BOTTOM = 8;
  public static final int ALIGN_END = 19;
  public static final int ALIGN_LEFT = 5;
  public static final int ALIGN_PARENT_BOTTOM = 12;
  public static final int ALIGN_PARENT_END = 21;
  public static final int ALIGN_PARENT_LEFT = 9;
  public static final int ALIGN_PARENT_RIGHT = 11;
  public static final int ALIGN_PARENT_START = 20;
  public static final int ALIGN_PARENT_TOP = 10;
  public static final int ALIGN_RIGHT = 7;
  public static final int ALIGN_START = 18;
  public static final int ALIGN_TOP = 6;
  public static final int BELOW = 3;
  public static final int CENTER_HORIZONTAL = 14;
  public static final int CENTER_IN_PARENT = 13;
  public static final int CENTER_VERTICAL = 15;
  private static final int DEFAULT_WIDTH = 65536;
  public static final int END_OF = 17;
  public static final int LEFT_OF = 0;
  public static final int RIGHT_OF = 1;
  private static final int[] RULES_HORIZONTAL = { 0, 1, 5, 7, 16, 17, 18, 19 };
  private static final int[] RULES_VERTICAL = { 2, 3, 4, 6, 8 };
  public static final int START_OF = 16;
  public static final int TRUE = -1;
  private static final int VALUE_NOT_SET = Integer.MIN_VALUE;
  private static final int VERB_COUNT = 22;
  private boolean mAllowBrokenMeasureSpecs = false;
  private View mBaselineView = null;
  private final Rect mContentBounds = new Rect();
  private boolean mDirtyHierarchy;
  private final DependencyGraph mGraph = new DependencyGraph(null);
  @UnsupportedAppUsage(maxTargetSdk=28)
  private int mGravity = 8388659;
  private int mIgnoreGravity;
  private boolean mMeasureVerticalWithPaddingMargin = false;
  private final Rect mSelfBounds = new Rect();
  private View[] mSortedHorizontalChildren;
  private View[] mSortedVerticalChildren;
  private SortedSet<View> mTopToBottomLeftToRightSet = null;
  
  public RelativeLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RelativeLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public RelativeLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RelativeLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    initFromAttributes(paramContext, paramAttributeSet, paramInt1, paramInt2);
    queryCompatibilityModes(paramContext);
  }
  
  private void applyHorizontalSizeRules(LayoutParams paramLayoutParams, int paramInt, int[] paramArrayOfInt)
  {
    LayoutParams.access$102(paramLayoutParams, Integer.MIN_VALUE);
    LayoutParams.access$202(paramLayoutParams, Integer.MIN_VALUE);
    LayoutParams localLayoutParams = getRelatedViewParams(paramArrayOfInt, 0);
    if (localLayoutParams != null) {
      LayoutParams.access$202(paramLayoutParams, localLayoutParams.mLeft - (localLayoutParams.leftMargin + paramLayoutParams.rightMargin));
    } else if ((paramLayoutParams.alignWithParent) && (paramArrayOfInt[0] != 0) && (paramInt >= 0)) {
      LayoutParams.access$202(paramLayoutParams, paramInt - this.mPaddingRight - paramLayoutParams.rightMargin);
    }
    localLayoutParams = getRelatedViewParams(paramArrayOfInt, 1);
    if (localLayoutParams != null) {
      LayoutParams.access$102(paramLayoutParams, localLayoutParams.mRight + (localLayoutParams.rightMargin + paramLayoutParams.leftMargin));
    } else if ((paramLayoutParams.alignWithParent) && (paramArrayOfInt[1] != 0)) {
      LayoutParams.access$102(paramLayoutParams, this.mPaddingLeft + paramLayoutParams.leftMargin);
    }
    localLayoutParams = getRelatedViewParams(paramArrayOfInt, 5);
    if (localLayoutParams != null) {
      LayoutParams.access$102(paramLayoutParams, localLayoutParams.mLeft + paramLayoutParams.leftMargin);
    } else if ((paramLayoutParams.alignWithParent) && (paramArrayOfInt[5] != 0)) {
      LayoutParams.access$102(paramLayoutParams, this.mPaddingLeft + paramLayoutParams.leftMargin);
    }
    localLayoutParams = getRelatedViewParams(paramArrayOfInt, 7);
    if (localLayoutParams != null) {
      LayoutParams.access$202(paramLayoutParams, localLayoutParams.mRight - paramLayoutParams.rightMargin);
    } else if ((paramLayoutParams.alignWithParent) && (paramArrayOfInt[7] != 0) && (paramInt >= 0)) {
      LayoutParams.access$202(paramLayoutParams, paramInt - this.mPaddingRight - paramLayoutParams.rightMargin);
    }
    if (paramArrayOfInt[9] != 0) {
      LayoutParams.access$102(paramLayoutParams, this.mPaddingLeft + paramLayoutParams.leftMargin);
    }
    if ((paramArrayOfInt[11] != 0) && (paramInt >= 0)) {
      LayoutParams.access$202(paramLayoutParams, paramInt - this.mPaddingRight - paramLayoutParams.rightMargin);
    }
  }
  
  private void applyVerticalSizeRules(LayoutParams paramLayoutParams, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = paramLayoutParams.getRules();
    int i = getRelatedViewBaselineOffset(arrayOfInt);
    if (i != -1)
    {
      paramInt1 = i;
      if (paramInt2 != -1) {
        paramInt1 = i - paramInt2;
      }
      LayoutParams.access$402(paramLayoutParams, paramInt1);
      LayoutParams.access$302(paramLayoutParams, Integer.MIN_VALUE);
      return;
    }
    LayoutParams.access$402(paramLayoutParams, Integer.MIN_VALUE);
    LayoutParams.access$302(paramLayoutParams, Integer.MIN_VALUE);
    LayoutParams localLayoutParams = getRelatedViewParams(arrayOfInt, 2);
    if (localLayoutParams != null) {
      LayoutParams.access$302(paramLayoutParams, localLayoutParams.mTop - (localLayoutParams.topMargin + paramLayoutParams.bottomMargin));
    } else if ((paramLayoutParams.alignWithParent) && (arrayOfInt[2] != 0) && (paramInt1 >= 0)) {
      LayoutParams.access$302(paramLayoutParams, paramInt1 - this.mPaddingBottom - paramLayoutParams.bottomMargin);
    }
    localLayoutParams = getRelatedViewParams(arrayOfInt, 3);
    if (localLayoutParams != null) {
      LayoutParams.access$402(paramLayoutParams, localLayoutParams.mBottom + (localLayoutParams.bottomMargin + paramLayoutParams.topMargin));
    } else if ((paramLayoutParams.alignWithParent) && (arrayOfInt[3] != 0)) {
      LayoutParams.access$402(paramLayoutParams, this.mPaddingTop + paramLayoutParams.topMargin);
    }
    localLayoutParams = getRelatedViewParams(arrayOfInt, 6);
    if (localLayoutParams != null) {
      LayoutParams.access$402(paramLayoutParams, localLayoutParams.mTop + paramLayoutParams.topMargin);
    } else if ((paramLayoutParams.alignWithParent) && (arrayOfInt[6] != 0)) {
      LayoutParams.access$402(paramLayoutParams, this.mPaddingTop + paramLayoutParams.topMargin);
    }
    localLayoutParams = getRelatedViewParams(arrayOfInt, 8);
    if (localLayoutParams != null) {
      LayoutParams.access$302(paramLayoutParams, localLayoutParams.mBottom - paramLayoutParams.bottomMargin);
    } else if ((paramLayoutParams.alignWithParent) && (arrayOfInt[8] != 0) && (paramInt1 >= 0)) {
      LayoutParams.access$302(paramLayoutParams, paramInt1 - this.mPaddingBottom - paramLayoutParams.bottomMargin);
    }
    if (arrayOfInt[10] != 0) {
      LayoutParams.access$402(paramLayoutParams, this.mPaddingTop + paramLayoutParams.topMargin);
    }
    if ((arrayOfInt[12] != 0) && (paramInt1 >= 0)) {
      LayoutParams.access$302(paramLayoutParams, paramInt1 - this.mPaddingBottom - paramLayoutParams.bottomMargin);
    }
  }
  
  private static void centerHorizontal(View paramView, LayoutParams paramLayoutParams, int paramInt)
  {
    int i = paramView.getMeasuredWidth();
    paramInt = (paramInt - i) / 2;
    LayoutParams.access$102(paramLayoutParams, paramInt);
    LayoutParams.access$202(paramLayoutParams, paramInt + i);
  }
  
  private static void centerVertical(View paramView, LayoutParams paramLayoutParams, int paramInt)
  {
    int i = paramView.getMeasuredHeight();
    paramInt = (paramInt - i) / 2;
    LayoutParams.access$402(paramLayoutParams, paramInt);
    LayoutParams.access$302(paramLayoutParams, paramInt + i);
  }
  
  private int compareLayoutPosition(LayoutParams paramLayoutParams1, LayoutParams paramLayoutParams2)
  {
    int i = paramLayoutParams1.mTop - paramLayoutParams2.mTop;
    if (i != 0) {
      return i;
    }
    return paramLayoutParams1.mLeft - paramLayoutParams2.mLeft;
  }
  
  private int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    int i = 0;
    int j = 0;
    int k;
    if (paramInt8 < 0) {
      k = 1;
    } else {
      k = 0;
    }
    if ((k != 0) && (!this.mAllowBrokenMeasureSpecs))
    {
      if ((paramInt1 != Integer.MIN_VALUE) && (paramInt2 != Integer.MIN_VALUE))
      {
        paramInt2 = Math.max(0, paramInt2 - paramInt1);
        paramInt1 = 1073741824;
      }
      else if (paramInt3 >= 0)
      {
        paramInt2 = paramInt3;
        paramInt1 = 1073741824;
      }
      else
      {
        paramInt2 = 0;
        paramInt1 = 0;
      }
      return View.MeasureSpec.makeMeasureSpec(paramInt2, paramInt1);
    }
    int m = paramInt1;
    int n = paramInt2;
    int i1 = m;
    if (m == Integer.MIN_VALUE) {
      i1 = paramInt6 + paramInt4;
    }
    paramInt4 = n;
    if (n == Integer.MIN_VALUE) {
      paramInt4 = paramInt8 - paramInt7 - paramInt5;
    }
    paramInt5 = paramInt4 - i1;
    paramInt4 = 1073741824;
    if ((paramInt1 != Integer.MIN_VALUE) && (paramInt2 != Integer.MIN_VALUE))
    {
      paramInt1 = paramInt4;
      if (k != 0) {
        paramInt1 = 0;
      }
      paramInt3 = Math.max(0, paramInt5);
      paramInt2 = paramInt1;
      paramInt1 = paramInt3;
    }
    else if (paramInt3 >= 0)
    {
      paramInt2 = 1073741824;
      if (paramInt5 >= 0) {
        paramInt1 = Math.min(paramInt5, paramInt3);
      } else {
        paramInt1 = paramInt3;
      }
    }
    else if (paramInt3 == -1)
    {
      paramInt1 = paramInt4;
      if (k != 0) {
        paramInt1 = 0;
      }
      paramInt3 = Math.max(0, paramInt5);
      paramInt2 = paramInt1;
      paramInt1 = paramInt3;
    }
    else
    {
      paramInt2 = i;
      paramInt1 = j;
      if (paramInt3 == -2) {
        if (paramInt5 >= 0)
        {
          paramInt2 = Integer.MIN_VALUE;
          paramInt1 = paramInt5;
        }
        else
        {
          paramInt2 = 0;
          paramInt1 = 0;
        }
      }
    }
    return View.MeasureSpec.makeMeasureSpec(paramInt1, paramInt2);
  }
  
  private View getRelatedView(int[] paramArrayOfInt, int paramInt)
  {
    int i = paramArrayOfInt[paramInt];
    if (i != 0)
    {
      paramArrayOfInt = (RelativeLayout.DependencyGraph.Node)this.mGraph.mKeyNodes.get(i);
      if (paramArrayOfInt == null) {
        return null;
      }
      paramArrayOfInt = paramArrayOfInt.view;
      while (paramArrayOfInt.getVisibility() == 8)
      {
        Object localObject = ((LayoutParams)paramArrayOfInt.getLayoutParams()).getRules(paramArrayOfInt.getLayoutDirection());
        localObject = (RelativeLayout.DependencyGraph.Node)this.mGraph.mKeyNodes.get(localObject[paramInt]);
        if ((localObject != null) && (paramArrayOfInt != ((RelativeLayout.DependencyGraph.Node)localObject).view)) {
          paramArrayOfInt = ((RelativeLayout.DependencyGraph.Node)localObject).view;
        } else {
          return null;
        }
      }
      return paramArrayOfInt;
    }
    return null;
  }
  
  private int getRelatedViewBaselineOffset(int[] paramArrayOfInt)
  {
    paramArrayOfInt = getRelatedView(paramArrayOfInt, 4);
    if (paramArrayOfInt != null)
    {
      int i = paramArrayOfInt.getBaseline();
      if ((i != -1) && ((paramArrayOfInt.getLayoutParams() instanceof LayoutParams))) {
        return ((LayoutParams)paramArrayOfInt.getLayoutParams()).mTop + i;
      }
    }
    return -1;
  }
  
  private LayoutParams getRelatedViewParams(int[] paramArrayOfInt, int paramInt)
  {
    paramArrayOfInt = getRelatedView(paramArrayOfInt, paramInt);
    if ((paramArrayOfInt != null) && ((paramArrayOfInt.getLayoutParams() instanceof LayoutParams))) {
      return (LayoutParams)paramArrayOfInt.getLayoutParams();
    }
    return null;
  }
  
  private void initFromAttributes(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RelativeLayout, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.RelativeLayout, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mIgnoreGravity = localTypedArray.getResourceId(1, -1);
    this.mGravity = localTypedArray.getInt(0, this.mGravity);
    localTypedArray.recycle();
  }
  
  private void measureChild(View paramView, LayoutParams paramLayoutParams, int paramInt1, int paramInt2)
  {
    paramView.measure(getChildMeasureSpec(paramLayoutParams.mLeft, paramLayoutParams.mRight, paramLayoutParams.width, paramLayoutParams.leftMargin, paramLayoutParams.rightMargin, this.mPaddingLeft, this.mPaddingRight, paramInt1), getChildMeasureSpec(paramLayoutParams.mTop, paramLayoutParams.mBottom, paramLayoutParams.height, paramLayoutParams.topMargin, paramLayoutParams.bottomMargin, this.mPaddingTop, this.mPaddingBottom, paramInt2));
  }
  
  private void measureChildHorizontal(View paramView, LayoutParams paramLayoutParams, int paramInt1, int paramInt2)
  {
    int i = getChildMeasureSpec(paramLayoutParams.mLeft, paramLayoutParams.mRight, paramLayoutParams.width, paramLayoutParams.leftMargin, paramLayoutParams.rightMargin, this.mPaddingLeft, this.mPaddingRight, paramInt1);
    if ((paramInt2 < 0) && (!this.mAllowBrokenMeasureSpecs))
    {
      if (paramLayoutParams.height >= 0) {
        paramInt1 = View.MeasureSpec.makeMeasureSpec(paramLayoutParams.height, 1073741824);
      } else {
        paramInt1 = View.MeasureSpec.makeMeasureSpec(0, 0);
      }
    }
    else
    {
      if (this.mMeasureVerticalWithPaddingMargin) {
        paramInt1 = Math.max(0, paramInt2 - this.mPaddingTop - this.mPaddingBottom - paramLayoutParams.topMargin - paramLayoutParams.bottomMargin);
      } else {
        paramInt1 = Math.max(0, paramInt2);
      }
      if (paramLayoutParams.height == -1) {
        paramInt2 = 1073741824;
      } else {
        paramInt2 = Integer.MIN_VALUE;
      }
      paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1, paramInt2);
    }
    paramView.measure(i, paramInt1);
  }
  
  private void positionAtEdge(View paramView, LayoutParams paramLayoutParams, int paramInt)
  {
    if (isLayoutRtl())
    {
      LayoutParams.access$202(paramLayoutParams, paramInt - this.mPaddingRight - paramLayoutParams.rightMargin);
      LayoutParams.access$102(paramLayoutParams, paramLayoutParams.mRight - paramView.getMeasuredWidth());
    }
    else
    {
      LayoutParams.access$102(paramLayoutParams, this.mPaddingLeft + paramLayoutParams.leftMargin);
      LayoutParams.access$202(paramLayoutParams, paramLayoutParams.mLeft + paramView.getMeasuredWidth());
    }
  }
  
  private boolean positionChildHorizontal(View paramView, LayoutParams paramLayoutParams, int paramInt, boolean paramBoolean)
  {
    int[] arrayOfInt = paramLayoutParams.getRules(getLayoutDirection());
    int i = paramLayoutParams.mLeft;
    boolean bool = true;
    if ((i == Integer.MIN_VALUE) && (paramLayoutParams.mRight != Integer.MIN_VALUE)) {
      LayoutParams.access$102(paramLayoutParams, paramLayoutParams.mRight - paramView.getMeasuredWidth());
    } else if ((paramLayoutParams.mLeft != Integer.MIN_VALUE) && (paramLayoutParams.mRight == Integer.MIN_VALUE)) {
      LayoutParams.access$202(paramLayoutParams, paramLayoutParams.mLeft + paramView.getMeasuredWidth());
    } else if ((paramLayoutParams.mLeft == Integer.MIN_VALUE) && (paramLayoutParams.mRight == Integer.MIN_VALUE)) {
      if ((arrayOfInt[13] == 0) && (arrayOfInt[14] == 0))
      {
        positionAtEdge(paramView, paramLayoutParams, paramInt);
      }
      else
      {
        if (!paramBoolean) {
          centerHorizontal(paramView, paramLayoutParams, paramInt);
        } else {
          positionAtEdge(paramView, paramLayoutParams, paramInt);
        }
        return true;
      }
    }
    if (arrayOfInt[21] != 0) {
      paramBoolean = bool;
    } else {
      paramBoolean = false;
    }
    return paramBoolean;
  }
  
  private boolean positionChildVertical(View paramView, LayoutParams paramLayoutParams, int paramInt, boolean paramBoolean)
  {
    int[] arrayOfInt = paramLayoutParams.getRules();
    int i = paramLayoutParams.mTop;
    boolean bool = true;
    if ((i == Integer.MIN_VALUE) && (paramLayoutParams.mBottom != Integer.MIN_VALUE)) {
      LayoutParams.access$402(paramLayoutParams, paramLayoutParams.mBottom - paramView.getMeasuredHeight());
    } else if ((paramLayoutParams.mTop != Integer.MIN_VALUE) && (paramLayoutParams.mBottom == Integer.MIN_VALUE)) {
      LayoutParams.access$302(paramLayoutParams, paramLayoutParams.mTop + paramView.getMeasuredHeight());
    } else if ((paramLayoutParams.mTop == Integer.MIN_VALUE) && (paramLayoutParams.mBottom == Integer.MIN_VALUE)) {
      if ((arrayOfInt[13] == 0) && (arrayOfInt[15] == 0))
      {
        LayoutParams.access$402(paramLayoutParams, this.mPaddingTop + paramLayoutParams.topMargin);
        LayoutParams.access$302(paramLayoutParams, paramLayoutParams.mTop + paramView.getMeasuredHeight());
      }
      else
      {
        if (!paramBoolean)
        {
          centerVertical(paramView, paramLayoutParams, paramInt);
        }
        else
        {
          LayoutParams.access$402(paramLayoutParams, this.mPaddingTop + paramLayoutParams.topMargin);
          LayoutParams.access$302(paramLayoutParams, paramLayoutParams.mTop + paramView.getMeasuredHeight());
        }
        return true;
      }
    }
    if (arrayOfInt[12] != 0) {
      paramBoolean = bool;
    } else {
      paramBoolean = false;
    }
    return paramBoolean;
  }
  
  private void queryCompatibilityModes(Context paramContext)
  {
    int i = paramContext.getApplicationInfo().targetSdkVersion;
    boolean bool1 = true;
    boolean bool2;
    if (i <= 17) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mAllowBrokenMeasureSpecs = bool2;
    if (i >= 18) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    this.mMeasureVerticalWithPaddingMargin = bool2;
  }
  
  private void sortChildren()
  {
    int i = getChildCount();
    Object localObject = this.mSortedVerticalChildren;
    if ((localObject == null) || (localObject.length != i)) {
      this.mSortedVerticalChildren = new View[i];
    }
    localObject = this.mSortedHorizontalChildren;
    if ((localObject == null) || (localObject.length != i)) {
      this.mSortedHorizontalChildren = new View[i];
    }
    localObject = this.mGraph;
    ((DependencyGraph)localObject).clear();
    for (int j = 0; j < i; j++) {
      ((DependencyGraph)localObject).add(getChildAt(j));
    }
    ((DependencyGraph)localObject).getSortedViews(this.mSortedVerticalChildren, RULES_VERTICAL);
    ((DependencyGraph)localObject).getSortedViews(this.mSortedHorizontalChildren, RULES_HORIZONTAL);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    if (this.mTopToBottomLeftToRightSet == null) {
      this.mTopToBottomLeftToRightSet = new TreeSet(new TopToBottomLeftToRightComparator(null));
    }
    int i = 0;
    int j = getChildCount();
    while (i < j)
    {
      this.mTopToBottomLeftToRightSet.add(getChildAt(i));
      i++;
    }
    Iterator localIterator = this.mTopToBottomLeftToRightSet.iterator();
    while (localIterator.hasNext())
    {
      View localView = (View)localIterator.next();
      if ((localView.getVisibility() == 0) && (localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent)))
      {
        this.mTopToBottomLeftToRightSet.clear();
        return true;
      }
    }
    this.mTopToBottomLeftToRightSet.clear();
    return false;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
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
    return RelativeLayout.class.getName();
  }
  
  public int getBaseline()
  {
    View localView = this.mBaselineView;
    int i;
    if (localView != null) {
      i = localView.getBaseline();
    } else {
      i = super.getBaseline();
    }
    return i;
  }
  
  public int getGravity()
  {
    return this.mGravity;
  }
  
  public int getIgnoreGravity()
  {
    return this.mIgnoreGravity;
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
        localView.layout(localLayoutParams.mLeft, localLayoutParams.mTop, localLayoutParams.mRight, localLayoutParams.mBottom);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mDirtyHierarchy)
    {
      this.mDirtyHierarchy = false;
      sortChildren();
    }
    int i = -1;
    int j = -1;
    int k = 0;
    int m = 0;
    int n = View.MeasureSpec.getMode(paramInt1);
    int i1 = View.MeasureSpec.getMode(paramInt2);
    int i2 = View.MeasureSpec.getSize(paramInt1);
    int i3 = View.MeasureSpec.getSize(paramInt2);
    if (n != 0) {
      i = i2;
    }
    if (i1 != 0) {
      j = i3;
    }
    if (n == 1073741824) {
      k = i;
    }
    if (i1 == 1073741824) {
      m = j;
    }
    Object localObject1 = null;
    i3 = this.mGravity & 0x800007;
    int i4;
    if ((i3 != 8388611) && (i3 != 0)) {
      i4 = 1;
    } else {
      i4 = 0;
    }
    i3 = this.mGravity & 0x70;
    int i5;
    if ((i3 != 48) && (i3 != 0)) {
      i5 = 1;
    } else {
      i5 = 0;
    }
    int i6 = 0;
    int i7 = 0;
    if (i4 == 0)
    {
      localObject2 = localObject1;
      if (i5 == 0) {}
    }
    else
    {
      i3 = this.mIgnoreGravity;
      localObject2 = localObject1;
      if (i3 != -1) {
        localObject2 = findViewById(i3);
      }
    }
    boolean bool1;
    if (n != 1073741824) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    boolean bool2;
    if (i1 != 1073741824) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    int i8 = getLayoutDirection();
    if (isLayoutRtl())
    {
      i3 = i;
      if (i == -1) {
        i3 = 65536;
      }
    }
    else
    {
      i3 = i;
    }
    localObject1 = this.mSortedHorizontalChildren;
    n = localObject1.length;
    i2 = 0;
    i = i1;
    i1 = n;
    while (i2 < i1)
    {
      localObject3 = localObject1[i2];
      n = i6;
      if (((View)localObject3).getVisibility() != 8)
      {
        localObject4 = (LayoutParams)((View)localObject3).getLayoutParams();
        applyHorizontalSizeRules((LayoutParams)localObject4, i3, ((LayoutParams)localObject4).getRules(i8));
        measureChildHorizontal((View)localObject3, (LayoutParams)localObject4, i3, j);
        n = i6;
        if (positionChildHorizontal((View)localObject3, (LayoutParams)localObject4, i3, bool1)) {
          n = 1;
        }
      }
      i2++;
      i6 = n;
    }
    Object localObject3 = this.mSortedVerticalChildren;
    int i9 = localObject3.length;
    int i10 = getContext().getApplicationInfo().targetSdkVersion;
    int i11 = 0;
    int i12 = Integer.MIN_VALUE;
    n = Integer.MIN_VALUE;
    i = i8;
    i2 = Integer.MAX_VALUE;
    int i13 = Integer.MAX_VALUE;
    i1 = k;
    i8 = n;
    n = i7;
    k = m;
    i7 = i9;
    i9 = j;
    m = i13;
    while (i11 < i7)
    {
      localObject4 = localObject3[i11];
      int i14;
      int i15;
      int i16;
      int i17;
      int i18;
      if (((View)localObject4).getVisibility() != 8)
      {
        localObject1 = (LayoutParams)((View)localObject4).getLayoutParams();
        applyVerticalSizeRules((LayoutParams)localObject1, i9, ((View)localObject4).getBaseline());
        measureChild((View)localObject4, (LayoutParams)localObject1, i3, i9);
        if (positionChildVertical((View)localObject4, (LayoutParams)localObject1, i9, bool2)) {
          n = 1;
        }
        if (bool1)
        {
          if (isLayoutRtl())
          {
            if (i10 < 19) {
              j = Math.max(i1, i3 - ((LayoutParams)localObject1).mLeft);
            } else {
              j = Math.max(i1, i3 - ((LayoutParams)localObject1).mLeft + ((LayoutParams)localObject1).leftMargin);
            }
          }
          else if (i10 < 19) {
            j = Math.max(i1, ((LayoutParams)localObject1).mRight);
          } else {
            j = Math.max(i1, ((LayoutParams)localObject1).mRight + ((LayoutParams)localObject1).rightMargin);
          }
        }
        else {
          j = i1;
        }
        i1 = k;
        if (bool2) {
          if (i10 < 19) {
            i1 = Math.max(k, ((LayoutParams)localObject1).mBottom);
          } else {
            i1 = Math.max(k, ((LayoutParams)localObject1).mBottom + ((LayoutParams)localObject1).bottomMargin);
          }
        }
        if (localObject4 == localObject2)
        {
          k = m;
          i13 = i2;
          if (i5 == 0) {}
        }
        else
        {
          i13 = Math.min(i2, ((LayoutParams)localObject1).mLeft - ((LayoutParams)localObject1).leftMargin);
          k = Math.min(m, ((LayoutParams)localObject1).mTop - ((LayoutParams)localObject1).topMargin);
        }
        if (localObject4 == localObject2)
        {
          m = k;
          i14 = j;
          i15 = i1;
          i2 = i13;
          i16 = n;
          i17 = i12;
          i18 = i8;
          if (i4 == 0) {}
        }
        else
        {
          i17 = Math.max(i12, ((LayoutParams)localObject1).mRight + ((LayoutParams)localObject1).rightMargin);
          i18 = Math.max(i8, ((LayoutParams)localObject1).mBottom + ((LayoutParams)localObject1).bottomMargin);
          m = k;
          i14 = j;
          i15 = i1;
          i2 = i13;
          i16 = n;
        }
      }
      else
      {
        i18 = i8;
        i17 = i12;
        i16 = n;
        i15 = k;
        i14 = i1;
      }
      i11++;
      i1 = i14;
      k = i15;
      n = i16;
      i12 = i17;
      i8 = i18;
    }
    j = i7;
    localObject1 = null;
    Object localObject4 = null;
    i7 = 0;
    Object localObject6;
    Object localObject8;
    while (i7 < j)
    {
      Object localObject5 = localObject3[i7];
      localObject6 = localObject4;
      localObject7 = localObject1;
      if (((View)localObject5).getVisibility() != 8)
      {
        localObject8 = (LayoutParams)((View)localObject5).getLayoutParams();
        if ((localObject4 != null) && (localObject1 != null))
        {
          localObject6 = localObject4;
          localObject7 = localObject1;
          if (compareLayoutPosition((LayoutParams)localObject8, (LayoutParams)localObject1) >= 0) {}
        }
        else
        {
          localObject6 = localObject5;
          localObject7 = localObject8;
        }
      }
      i7++;
      localObject4 = localObject6;
      localObject1 = localObject7;
    }
    this.mBaselineView = ((View)localObject4);
    if (bool1)
    {
      i7 = i1 + this.mPaddingRight;
      i1 = i7;
      if (this.mLayoutParams != null)
      {
        i1 = i7;
        if (this.mLayoutParams.width >= 0) {
          i1 = Math.max(i7, this.mLayoutParams.width);
        }
      }
      i1 = resolveSize(Math.max(i1, getSuggestedMinimumWidth()), paramInt1);
      if (i6 != 0)
      {
        for (paramInt1 = 0; paramInt1 < j; paramInt1++)
        {
          localObject6 = localObject3[paramInt1];
          if (((View)localObject6).getVisibility() != 8)
          {
            localObject7 = (LayoutParams)((View)localObject6).getLayoutParams();
            localObject8 = ((LayoutParams)localObject7).getRules(i);
            if ((localObject8[13] == 0) && (localObject8[14] == 0))
            {
              if (localObject8[11] != 0)
              {
                i7 = ((View)localObject6).getMeasuredWidth();
                LayoutParams.access$102((LayoutParams)localObject7, i1 - this.mPaddingRight - i7);
                LayoutParams.access$202((LayoutParams)localObject7, ((LayoutParams)localObject7).mLeft + i7);
              }
            }
            else {
              centerHorizontal((View)localObject6, (LayoutParams)localObject7, i1);
            }
          }
        }
        paramInt1 = i;
      }
      else
      {
        paramInt1 = i;
      }
    }
    else
    {
      paramInt1 = i;
    }
    if (bool2)
    {
      k += this.mPaddingBottom;
      i = k;
      if (this.mLayoutParams != null)
      {
        i = k;
        if (this.mLayoutParams.height >= 0) {
          i = Math.max(k, this.mLayoutParams.height);
        }
      }
      k = resolveSize(Math.max(i, getSuggestedMinimumHeight()), paramInt2);
      if (n != 0) {
        for (paramInt2 = 0; paramInt2 < j; paramInt2++)
        {
          localObject7 = localObject3[paramInt2];
          if (((View)localObject7).getVisibility() != 8)
          {
            localObject4 = (LayoutParams)((View)localObject7).getLayoutParams();
            localObject1 = ((LayoutParams)localObject4).getRules(paramInt1);
            if ((localObject1[13] == 0) && (localObject1[15] == 0))
            {
              if (localObject1[12] != 0)
              {
                i = ((View)localObject7).getMeasuredHeight();
                LayoutParams.access$402((LayoutParams)localObject4, k - this.mPaddingBottom - i);
                LayoutParams.access$302((LayoutParams)localObject4, ((LayoutParams)localObject4).mTop + i);
              }
            }
            else {
              centerVertical((View)localObject7, (LayoutParams)localObject4, k);
            }
          }
        }
      } else {}
    }
    if ((i4 == 0) && (i5 == 0)) {
      break label1620;
    }
    localObject4 = this.mSelfBounds;
    ((Rect)localObject4).set(this.mPaddingLeft, this.mPaddingTop, i1 - this.mPaddingRight, k - this.mPaddingBottom);
    Object localObject7 = this.mContentBounds;
    Gravity.apply(this.mGravity, i12 - i2, i8 - m, (Rect)localObject4, (Rect)localObject7, paramInt1);
    paramInt2 = ((Rect)localObject7).left - i2;
    i = ((Rect)localObject7).top - m;
    if ((paramInt2 == 0) && (i == 0)) {
      break label1620;
    }
    paramInt1 = 0;
    localObject1 = localObject2;
    Object localObject2 = localObject7;
    while (paramInt1 < j)
    {
      localObject7 = localObject3[paramInt1];
      if ((((View)localObject7).getVisibility() != 8) && (localObject7 != localObject1))
      {
        localObject7 = (LayoutParams)((View)localObject7).getLayoutParams();
        if (i4 != 0)
        {
          LayoutParams.access$112((LayoutParams)localObject7, paramInt2);
          LayoutParams.access$212((LayoutParams)localObject7, paramInt2);
        }
        if (i5 != 0)
        {
          LayoutParams.access$412((LayoutParams)localObject7, i);
          LayoutParams.access$312((LayoutParams)localObject7, i);
        }
      }
      paramInt1++;
    }
    label1620:
    if (isLayoutRtl())
    {
      paramInt2 = i3 - i1;
      for (paramInt1 = 0; paramInt1 < j; paramInt1++)
      {
        localObject2 = localObject3[paramInt1];
        if (((View)localObject2).getVisibility() != 8)
        {
          localObject2 = (LayoutParams)((View)localObject2).getLayoutParams();
          LayoutParams.access$120((LayoutParams)localObject2, paramInt2);
          LayoutParams.access$220((LayoutParams)localObject2, paramInt2);
        }
      }
    }
    setMeasuredDimension(i1, k);
  }
  
  public void requestLayout()
  {
    super.requestLayout();
    this.mDirtyHierarchy = true;
  }
  
  @RemotableViewMethod
  public void setGravity(int paramInt)
  {
    if (this.mGravity != paramInt)
    {
      int i = paramInt;
      if ((0x800007 & paramInt) == 0) {
        i = paramInt | 0x800003;
      }
      paramInt = i;
      if ((i & 0x70) == 0) {
        paramInt = i | 0x30;
      }
      this.mGravity = paramInt;
      requestLayout();
    }
  }
  
  @RemotableViewMethod
  public void setHorizontalGravity(int paramInt)
  {
    paramInt &= 0x800007;
    int i = this.mGravity;
    if ((0x800007 & i) != paramInt)
    {
      this.mGravity = (0xFF7FFFF8 & i | paramInt);
      requestLayout();
    }
  }
  
  @RemotableViewMethod
  public void setIgnoreGravity(int paramInt)
  {
    this.mIgnoreGravity = paramInt;
  }
  
  @RemotableViewMethod
  public void setVerticalGravity(int paramInt)
  {
    int i = paramInt & 0x70;
    paramInt = this.mGravity;
    if ((paramInt & 0x70) != i)
    {
      this.mGravity = (paramInt & 0xFFFFFF8F | i);
      requestLayout();
    }
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  private static class DependencyGraph
  {
    private SparseArray<Node> mKeyNodes = new SparseArray();
    private ArrayList<Node> mNodes = new ArrayList();
    private ArrayDeque<Node> mRoots = new ArrayDeque();
    
    private ArrayDeque<Node> findRoots(int[] paramArrayOfInt)
    {
      Object localObject1 = this.mKeyNodes;
      ArrayList localArrayList = this.mNodes;
      int i = localArrayList.size();
      Object localObject2;
      for (int j = 0; j < i; j++)
      {
        localObject2 = (Node)localArrayList.get(j);
        ((Node)localObject2).dependents.clear();
        ((Node)localObject2).dependencies.clear();
      }
      for (j = 0; j < i; j++)
      {
        Node localNode1 = (Node)localArrayList.get(j);
        localObject2 = RelativeLayout.LayoutParams.access$700((RelativeLayout.LayoutParams)localNode1.view.getLayoutParams());
        int k = paramArrayOfInt.length;
        for (int m = 0; m < k; m++)
        {
          int n = localObject2[paramArrayOfInt[m]];
          if ((n > 0) || (ResourceId.isValid(n)))
          {
            Node localNode2 = (Node)((SparseArray)localObject1).get(n);
            if ((localNode2 != null) && (localNode2 != localNode1))
            {
              localNode2.dependents.put(localNode1, this);
              localNode1.dependencies.put(n, localNode2);
            }
          }
        }
      }
      paramArrayOfInt = this.mRoots;
      paramArrayOfInt.clear();
      for (j = 0; j < i; j++)
      {
        localObject1 = (Node)localArrayList.get(j);
        if (((Node)localObject1).dependencies.size() == 0) {
          paramArrayOfInt.addLast(localObject1);
        }
      }
      return paramArrayOfInt;
    }
    
    void add(View paramView)
    {
      int i = paramView.getId();
      paramView = Node.acquire(paramView);
      if (i != -1) {
        this.mKeyNodes.put(i, paramView);
      }
      this.mNodes.add(paramView);
    }
    
    void clear()
    {
      ArrayList localArrayList = this.mNodes;
      int i = localArrayList.size();
      for (int j = 0; j < i; j++) {
        ((Node)localArrayList.get(j)).release();
      }
      localArrayList.clear();
      this.mKeyNodes.clear();
      this.mRoots.clear();
    }
    
    void getSortedViews(View[] paramArrayOfView, int... paramVarArgs)
    {
      paramVarArgs = findRoots(paramVarArgs);
      for (int i = 0;; i++)
      {
        Object localObject1 = (Node)paramVarArgs.pollLast();
        if (localObject1 == null) {
          break;
        }
        Object localObject2 = ((Node)localObject1).view;
        int j = ((View)localObject2).getId();
        paramArrayOfView[i] = localObject2;
        ArrayMap localArrayMap = ((Node)localObject1).dependents;
        int k = localArrayMap.size();
        for (int m = 0; m < k; m++)
        {
          localObject2 = (Node)localArrayMap.keyAt(m);
          localObject1 = ((Node)localObject2).dependencies;
          ((SparseArray)localObject1).remove(j);
          if (((SparseArray)localObject1).size() == 0) {
            paramVarArgs.add(localObject2);
          }
        }
      }
      if (i >= paramArrayOfView.length) {
        return;
      }
      throw new IllegalStateException("Circular dependencies cannot exist in RelativeLayout");
    }
    
    static class Node
    {
      private static final int POOL_LIMIT = 100;
      private static final Pools.SynchronizedPool<Node> sPool = new Pools.SynchronizedPool(100);
      final SparseArray<Node> dependencies = new SparseArray();
      final ArrayMap<Node, RelativeLayout.DependencyGraph> dependents = new ArrayMap();
      View view;
      
      static Node acquire(View paramView)
      {
        Node localNode1 = (Node)sPool.acquire();
        Node localNode2 = localNode1;
        if (localNode1 == null) {
          localNode2 = new Node();
        }
        localNode2.view = paramView;
        return localNode2;
      }
      
      void release()
      {
        this.view = null;
        this.dependents.clear();
        this.dependencies.clear();
        sPool.release(this);
      }
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    @ViewDebug.ExportedProperty(category="layout")
    public boolean alignWithParent;
    @UnsupportedAppUsage
    private int mBottom;
    private int[] mInitialRules = new int[22];
    private boolean mIsRtlCompatibilityMode = false;
    @UnsupportedAppUsage
    private int mLeft;
    private boolean mNeedsLayoutResolution;
    @UnsupportedAppUsage
    private int mRight;
    @ViewDebug.ExportedProperty(category="layout", indexMapping={@android.view.ViewDebug.IntToString(from=2, to="above"), @android.view.ViewDebug.IntToString(from=4, to="alignBaseline"), @android.view.ViewDebug.IntToString(from=8, to="alignBottom"), @android.view.ViewDebug.IntToString(from=5, to="alignLeft"), @android.view.ViewDebug.IntToString(from=12, to="alignParentBottom"), @android.view.ViewDebug.IntToString(from=9, to="alignParentLeft"), @android.view.ViewDebug.IntToString(from=11, to="alignParentRight"), @android.view.ViewDebug.IntToString(from=10, to="alignParentTop"), @android.view.ViewDebug.IntToString(from=7, to="alignRight"), @android.view.ViewDebug.IntToString(from=6, to="alignTop"), @android.view.ViewDebug.IntToString(from=3, to="below"), @android.view.ViewDebug.IntToString(from=14, to="centerHorizontal"), @android.view.ViewDebug.IntToString(from=13, to="center"), @android.view.ViewDebug.IntToString(from=15, to="centerVertical"), @android.view.ViewDebug.IntToString(from=0, to="leftOf"), @android.view.ViewDebug.IntToString(from=1, to="rightOf"), @android.view.ViewDebug.IntToString(from=18, to="alignStart"), @android.view.ViewDebug.IntToString(from=19, to="alignEnd"), @android.view.ViewDebug.IntToString(from=20, to="alignParentStart"), @android.view.ViewDebug.IntToString(from=21, to="alignParentEnd"), @android.view.ViewDebug.IntToString(from=16, to="startOf"), @android.view.ViewDebug.IntToString(from=17, to="endOf")}, mapping={@android.view.ViewDebug.IntToString(from=-1, to="true"), @android.view.ViewDebug.IntToString(from=0, to="false/NO_ID")}, resolveId=true)
    private int[] mRules = new int[22];
    private boolean mRulesChanged = false;
    @UnsupportedAppUsage
    private int mTop;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RelativeLayout_Layout);
      boolean bool;
      if ((paramContext.getApplicationInfo().targetSdkVersion >= 17) && (paramContext.getApplicationInfo().hasRtlSupport())) {
        bool = false;
      } else {
        bool = true;
      }
      this.mIsRtlCompatibilityMode = bool;
      paramContext = this.mRules;
      int[] arrayOfInt = this.mInitialRules;
      int i = paramAttributeSet.getIndexCount();
      for (int j = 0; j < i; j++)
      {
        int k = paramAttributeSet.getIndex(j);
        int m = -1;
        switch (k)
        {
        default: 
          break;
        case 22: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[21] = m;
          break;
        case 21: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[20] = m;
          break;
        case 20: 
          paramContext[19] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 19: 
          paramContext[18] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 18: 
          paramContext[17] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 17: 
          paramContext[16] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 16: 
          this.alignWithParent = paramAttributeSet.getBoolean(k, false);
          break;
        case 15: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[15] = m;
          break;
        case 14: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[14] = m;
          break;
        case 13: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[13] = m;
          break;
        case 12: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[12] = m;
          break;
        case 11: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[11] = m;
          break;
        case 10: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[10] = m;
          break;
        case 9: 
          if (!paramAttributeSet.getBoolean(k, false)) {
            m = 0;
          }
          paramContext[9] = m;
          break;
        case 8: 
          paramContext[8] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 7: 
          paramContext[7] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 6: 
          paramContext[6] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 5: 
          paramContext[5] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 4: 
          paramContext[4] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 3: 
          paramContext[3] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 2: 
          paramContext[2] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 1: 
          paramContext[1] = paramAttributeSet.getResourceId(k, 0);
          break;
        case 0: 
          paramContext[0] = paramAttributeSet.getResourceId(k, 0);
        }
      }
      this.mRulesChanged = true;
      System.arraycopy(paramContext, 0, arrayOfInt, 0, 22);
      paramAttributeSet.recycle();
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
      this.mIsRtlCompatibilityMode = paramLayoutParams.mIsRtlCompatibilityMode;
      this.mRulesChanged = paramLayoutParams.mRulesChanged;
      this.alignWithParent = paramLayoutParams.alignWithParent;
      System.arraycopy(paramLayoutParams.mRules, 0, this.mRules, 0, 22);
      System.arraycopy(paramLayoutParams.mInitialRules, 0, this.mInitialRules, 0, 22);
    }
    
    private boolean hasRelativeRules()
    {
      int[] arrayOfInt = this.mInitialRules;
      boolean bool;
      if ((arrayOfInt[16] == 0) && (arrayOfInt[17] == 0) && (arrayOfInt[18] == 0) && (arrayOfInt[19] == 0) && (arrayOfInt[20] == 0) && (arrayOfInt[21] == 0)) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    private boolean isRelativeRule(int paramInt)
    {
      boolean bool;
      if ((paramInt != 16) && (paramInt != 17) && (paramInt != 18) && (paramInt != 19) && (paramInt != 20) && (paramInt != 21)) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    private void resolveRules(int paramInt)
    {
      int i = 1;
      if (paramInt == 1) {
        paramInt = 1;
      } else {
        paramInt = 0;
      }
      System.arraycopy(this.mInitialRules, 0, this.mRules, 0, 22);
      boolean bool = this.mIsRtlCompatibilityMode;
      int j = 11;
      int[] arrayOfInt1;
      if (bool)
      {
        arrayOfInt1 = this.mRules;
        if (arrayOfInt1[18] != 0)
        {
          if (arrayOfInt1[5] == 0) {
            arrayOfInt1[5] = arrayOfInt1[18];
          }
          this.mRules[18] = 0;
        }
        arrayOfInt1 = this.mRules;
        if (arrayOfInt1[19] != 0)
        {
          if (arrayOfInt1[7] == 0) {
            arrayOfInt1[7] = arrayOfInt1[19];
          }
          this.mRules[19] = 0;
        }
        arrayOfInt1 = this.mRules;
        if (arrayOfInt1[16] != 0)
        {
          if (arrayOfInt1[0] == 0) {
            arrayOfInt1[0] = arrayOfInt1[16];
          }
          this.mRules[16] = 0;
        }
        arrayOfInt1 = this.mRules;
        if (arrayOfInt1[17] != 0)
        {
          if (arrayOfInt1[1] == 0) {
            arrayOfInt1[1] = arrayOfInt1[17];
          }
          this.mRules[17] = 0;
        }
        arrayOfInt1 = this.mRules;
        if (arrayOfInt1[20] != 0)
        {
          if (arrayOfInt1[9] == 0) {
            arrayOfInt1[9] = arrayOfInt1[20];
          }
          this.mRules[20] = 0;
        }
        arrayOfInt1 = this.mRules;
        if (arrayOfInt1[21] != 0)
        {
          if (arrayOfInt1[11] == 0) {
            arrayOfInt1[11] = arrayOfInt1[21];
          }
          this.mRules[21] = 0;
        }
      }
      else
      {
        arrayOfInt1 = this.mRules;
        if ((arrayOfInt1[18] != 0) || (arrayOfInt1[19] != 0))
        {
          arrayOfInt1 = this.mRules;
          if ((arrayOfInt1[5] != 0) || (arrayOfInt1[7] != 0))
          {
            arrayOfInt1 = this.mRules;
            arrayOfInt1[5] = 0;
            arrayOfInt1[7] = 0;
          }
        }
        arrayOfInt1 = this.mRules;
        int k;
        if (arrayOfInt1[18] != 0)
        {
          if (paramInt != 0) {
            k = 7;
          } else {
            k = 5;
          }
          arrayOfInt2 = this.mRules;
          arrayOfInt1[k] = arrayOfInt2[18];
          arrayOfInt2[18] = 0;
        }
        arrayOfInt1 = this.mRules;
        if (arrayOfInt1[19] != 0)
        {
          if (paramInt != 0) {
            k = 5;
          } else {
            k = 7;
          }
          arrayOfInt2 = this.mRules;
          arrayOfInt1[k] = arrayOfInt2[19];
          arrayOfInt2[19] = 0;
        }
        arrayOfInt1 = this.mRules;
        if ((arrayOfInt1[16] != 0) || (arrayOfInt1[17] != 0))
        {
          arrayOfInt1 = this.mRules;
          if ((arrayOfInt1[0] != 0) || (arrayOfInt1[1] != 0))
          {
            arrayOfInt1 = this.mRules;
            arrayOfInt1[0] = 0;
            arrayOfInt1[1] = 0;
          }
        }
        int[] arrayOfInt2 = this.mRules;
        if (arrayOfInt2[16] != 0)
        {
          if (paramInt != 0) {
            k = 1;
          } else {
            k = 0;
          }
          arrayOfInt1 = this.mRules;
          arrayOfInt2[k] = arrayOfInt1[16];
          arrayOfInt1[16] = 0;
        }
        arrayOfInt1 = this.mRules;
        if (arrayOfInt1[17] != 0)
        {
          k = i;
          if (paramInt != 0) {
            k = 0;
          }
          arrayOfInt2 = this.mRules;
          arrayOfInt1[k] = arrayOfInt2[17];
          arrayOfInt2[17] = 0;
        }
        arrayOfInt1 = this.mRules;
        if ((arrayOfInt1[20] != 0) || (arrayOfInt1[21] != 0))
        {
          arrayOfInt1 = this.mRules;
          if ((arrayOfInt1[9] != 0) || (arrayOfInt1[11] != 0))
          {
            arrayOfInt1 = this.mRules;
            arrayOfInt1[9] = 0;
            arrayOfInt1[11] = 0;
          }
        }
        arrayOfInt2 = this.mRules;
        if (arrayOfInt2[20] != 0)
        {
          if (paramInt != 0) {
            k = 11;
          } else {
            k = 9;
          }
          arrayOfInt1 = this.mRules;
          arrayOfInt2[k] = arrayOfInt1[20];
          arrayOfInt1[20] = 0;
        }
        arrayOfInt2 = this.mRules;
        if (arrayOfInt2[21] != 0)
        {
          k = j;
          if (paramInt != 0) {
            k = 9;
          }
          arrayOfInt1 = this.mRules;
          arrayOfInt2[k] = arrayOfInt1[21];
          arrayOfInt1[21] = 0;
        }
      }
      this.mRulesChanged = false;
      this.mNeedsLayoutResolution = false;
    }
    
    private boolean shouldResolveLayoutDirection(int paramInt)
    {
      boolean bool;
      if (((!this.mNeedsLayoutResolution) && (!hasRelativeRules())) || ((!this.mRulesChanged) && (paramInt == getLayoutDirection()))) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public void addRule(int paramInt)
    {
      addRule(paramInt, -1);
    }
    
    public void addRule(int paramInt1, int paramInt2)
    {
      if ((!this.mNeedsLayoutResolution) && (isRelativeRule(paramInt1)) && (this.mInitialRules[paramInt1] != 0) && (paramInt2 == 0)) {
        this.mNeedsLayoutResolution = true;
      }
      this.mRules[paramInt1] = paramInt2;
      this.mInitialRules[paramInt1] = paramInt2;
      this.mRulesChanged = true;
    }
    
    public String debug(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("ViewGroup.LayoutParams={ width=");
      localStringBuilder.append(sizeToString(this.width));
      localStringBuilder.append(", height=");
      localStringBuilder.append(sizeToString(this.height));
      localStringBuilder.append(" }");
      return localStringBuilder.toString();
    }
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("layout:alignWithParent", this.alignWithParent);
    }
    
    public int getRule(int paramInt)
    {
      return this.mRules[paramInt];
    }
    
    public int[] getRules()
    {
      return this.mRules;
    }
    
    public int[] getRules(int paramInt)
    {
      resolveLayoutDirection(paramInt);
      return this.mRules;
    }
    
    public void removeRule(int paramInt)
    {
      addRule(paramInt, 0);
    }
    
    public void resolveLayoutDirection(int paramInt)
    {
      if (shouldResolveLayoutDirection(paramInt)) {
        resolveRules(paramInt);
      }
      super.resolveLayoutDirection(paramInt);
    }
    
    public static final class InspectionCompanion
      implements InspectionCompanion<RelativeLayout.LayoutParams>
    {
      private int mAboveId;
      private int mAlignBaselineId;
      private int mAlignBottomId;
      private int mAlignEndId;
      private int mAlignLeftId;
      private int mAlignParentBottomId;
      private int mAlignParentEndId;
      private int mAlignParentLeftId;
      private int mAlignParentRightId;
      private int mAlignParentStartId;
      private int mAlignParentTopId;
      private int mAlignRightId;
      private int mAlignStartId;
      private int mAlignTopId;
      private int mAlignWithParentIfMissingId;
      private int mBelowId;
      private int mCenterHorizontalId;
      private int mCenterInParentId;
      private int mCenterVerticalId;
      private boolean mPropertiesMapped;
      private int mToEndOfId;
      private int mToLeftOfId;
      private int mToRightOfId;
      private int mToStartOfId;
      
      public void mapProperties(PropertyMapper paramPropertyMapper)
      {
        this.mPropertiesMapped = true;
        this.mAboveId = paramPropertyMapper.mapResourceId("layout_above", 16843140);
        this.mAlignBaselineId = paramPropertyMapper.mapResourceId("layout_alignBaseline", 16843142);
        this.mAlignBottomId = paramPropertyMapper.mapResourceId("layout_alignBottom", 16843146);
        this.mAlignEndId = paramPropertyMapper.mapResourceId("layout_alignEnd", 16843706);
        this.mAlignLeftId = paramPropertyMapper.mapResourceId("layout_alignLeft", 16843143);
        this.mAlignParentBottomId = paramPropertyMapper.mapBoolean("layout_alignParentBottom", 16843150);
        this.mAlignParentEndId = paramPropertyMapper.mapBoolean("layout_alignParentEnd", 16843708);
        this.mAlignParentLeftId = paramPropertyMapper.mapBoolean("layout_alignParentLeft", 16843147);
        this.mAlignParentRightId = paramPropertyMapper.mapBoolean("layout_alignParentRight", 16843149);
        this.mAlignParentStartId = paramPropertyMapper.mapBoolean("layout_alignParentStart", 16843707);
        this.mAlignParentTopId = paramPropertyMapper.mapBoolean("layout_alignParentTop", 16843148);
        this.mAlignRightId = paramPropertyMapper.mapResourceId("layout_alignRight", 16843145);
        this.mAlignStartId = paramPropertyMapper.mapResourceId("layout_alignStart", 16843705);
        this.mAlignTopId = paramPropertyMapper.mapResourceId("layout_alignTop", 16843144);
        this.mAlignWithParentIfMissingId = paramPropertyMapper.mapBoolean("layout_alignWithParentIfMissing", 16843154);
        this.mBelowId = paramPropertyMapper.mapResourceId("layout_below", 16843141);
        this.mCenterHorizontalId = paramPropertyMapper.mapBoolean("layout_centerHorizontal", 16843152);
        this.mCenterInParentId = paramPropertyMapper.mapBoolean("layout_centerInParent", 16843151);
        this.mCenterVerticalId = paramPropertyMapper.mapBoolean("layout_centerVertical", 16843153);
        this.mToEndOfId = paramPropertyMapper.mapResourceId("layout_toEndOf", 16843704);
        this.mToLeftOfId = paramPropertyMapper.mapResourceId("layout_toLeftOf", 16843138);
        this.mToRightOfId = paramPropertyMapper.mapResourceId("layout_toRightOf", 16843139);
        this.mToStartOfId = paramPropertyMapper.mapResourceId("layout_toStartOf", 16843703);
      }
      
      public void readProperties(RelativeLayout.LayoutParams paramLayoutParams, PropertyReader paramPropertyReader)
      {
        if (this.mPropertiesMapped)
        {
          int[] arrayOfInt = paramLayoutParams.getRules();
          paramPropertyReader.readResourceId(this.mAboveId, arrayOfInt[2]);
          paramPropertyReader.readResourceId(this.mAlignBaselineId, arrayOfInt[4]);
          paramPropertyReader.readResourceId(this.mAlignBottomId, arrayOfInt[8]);
          paramPropertyReader.readResourceId(this.mAlignEndId, arrayOfInt[19]);
          paramPropertyReader.readResourceId(this.mAlignLeftId, arrayOfInt[5]);
          int i = this.mAlignParentBottomId;
          boolean bool;
          if (arrayOfInt[12] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          i = this.mAlignParentEndId;
          if (arrayOfInt[21] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          i = this.mAlignParentLeftId;
          if (arrayOfInt[9] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          i = this.mAlignParentRightId;
          if (arrayOfInt[11] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          i = this.mAlignParentStartId;
          if (arrayOfInt[20] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          i = this.mAlignParentTopId;
          if (arrayOfInt[10] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          paramPropertyReader.readResourceId(this.mAlignRightId, arrayOfInt[7]);
          paramPropertyReader.readResourceId(this.mAlignStartId, arrayOfInt[18]);
          paramPropertyReader.readResourceId(this.mAlignTopId, arrayOfInt[6]);
          paramPropertyReader.readBoolean(this.mAlignWithParentIfMissingId, paramLayoutParams.alignWithParent);
          paramPropertyReader.readResourceId(this.mBelowId, arrayOfInt[3]);
          i = this.mCenterHorizontalId;
          if (arrayOfInt[14] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          i = this.mCenterInParentId;
          if (arrayOfInt[13] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          i = this.mCenterVerticalId;
          if (arrayOfInt[15] == -1) {
            bool = true;
          } else {
            bool = false;
          }
          paramPropertyReader.readBoolean(i, bool);
          paramPropertyReader.readResourceId(this.mToEndOfId, arrayOfInt[17]);
          paramPropertyReader.readResourceId(this.mToLeftOfId, arrayOfInt[0]);
          paramPropertyReader.readResourceId(this.mToRightOfId, arrayOfInt[1]);
          paramPropertyReader.readResourceId(this.mToStartOfId, arrayOfInt[16]);
          return;
        }
        throw new InspectionCompanion.UninitializedPropertyMapException();
      }
    }
  }
  
  private class TopToBottomLeftToRightComparator
    implements Comparator<View>
  {
    private TopToBottomLeftToRightComparator() {}
    
    public int compare(View paramView1, View paramView2)
    {
      int i = paramView1.getTop() - paramView2.getTop();
      if (i != 0) {
        return i;
      }
      i = paramView1.getLeft() - paramView2.getLeft();
      if (i != 0) {
        return i;
      }
      i = paramView1.getHeight() - paramView2.getHeight();
      if (i != 0) {
        return i;
      }
      i = paramView1.getWidth() - paramView2.getWidth();
      if (i != 0) {
        return i;
      }
      return 0;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RelativeLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */