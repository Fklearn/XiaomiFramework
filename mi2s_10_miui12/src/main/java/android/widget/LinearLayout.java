package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RemoteViews.RemoteView
public class LinearLayout
  extends ViewGroup
{
  public static final int HORIZONTAL = 0;
  @UnsupportedAppUsage
  private static final int INDEX_BOTTOM = 2;
  private static final int INDEX_CENTER_VERTICAL = 0;
  private static final int INDEX_FILL = 3;
  @UnsupportedAppUsage
  private static final int INDEX_TOP = 1;
  public static final int SHOW_DIVIDER_BEGINNING = 1;
  public static final int SHOW_DIVIDER_END = 4;
  public static final int SHOW_DIVIDER_MIDDLE = 2;
  public static final int SHOW_DIVIDER_NONE = 0;
  public static final int VERTICAL = 1;
  private static final int VERTICAL_GRAVITY_COUNT = 4;
  private static boolean sCompatibilityDone = false;
  private static boolean sRemeasureWeightedChildren = true;
  private final boolean mAllowInconsistentMeasurement;
  @ViewDebug.ExportedProperty(category="layout")
  private boolean mBaselineAligned;
  @ViewDebug.ExportedProperty(category="layout")
  private int mBaselineAlignedChildIndex;
  @ViewDebug.ExportedProperty(category="measurement")
  private int mBaselineChildTop;
  @UnsupportedAppUsage
  private Drawable mDivider;
  private int mDividerHeight;
  private int mDividerPadding;
  private int mDividerWidth;
  @ViewDebug.ExportedProperty(category="measurement", flagMapping={@android.view.ViewDebug.FlagToString(equals=-1, mask=-1, name="NONE"), @android.view.ViewDebug.FlagToString(equals=0, mask=0, name="NONE"), @android.view.ViewDebug.FlagToString(equals=48, mask=48, name="TOP"), @android.view.ViewDebug.FlagToString(equals=80, mask=80, name="BOTTOM"), @android.view.ViewDebug.FlagToString(equals=3, mask=3, name="LEFT"), @android.view.ViewDebug.FlagToString(equals=5, mask=5, name="RIGHT"), @android.view.ViewDebug.FlagToString(equals=8388611, mask=8388611, name="START"), @android.view.ViewDebug.FlagToString(equals=8388613, mask=8388613, name="END"), @android.view.ViewDebug.FlagToString(equals=16, mask=16, name="CENTER_VERTICAL"), @android.view.ViewDebug.FlagToString(equals=112, mask=112, name="FILL_VERTICAL"), @android.view.ViewDebug.FlagToString(equals=1, mask=1, name="CENTER_HORIZONTAL"), @android.view.ViewDebug.FlagToString(equals=7, mask=7, name="FILL_HORIZONTAL"), @android.view.ViewDebug.FlagToString(equals=17, mask=17, name="CENTER"), @android.view.ViewDebug.FlagToString(equals=119, mask=119, name="FILL"), @android.view.ViewDebug.FlagToString(equals=8388608, mask=8388608, name="RELATIVE")}, formatToHexString=true)
  @UnsupportedAppUsage(maxTargetSdk=28)
  private int mGravity;
  private int mLayoutDirection;
  @UnsupportedAppUsage
  private int[] mMaxAscent;
  @UnsupportedAppUsage
  private int[] mMaxDescent;
  @ViewDebug.ExportedProperty(category="measurement")
  private int mOrientation;
  private int mShowDividers;
  @ViewDebug.ExportedProperty(category="measurement")
  @UnsupportedAppUsage
  private int mTotalLength;
  @ViewDebug.ExportedProperty(category="layout")
  @UnsupportedAppUsage
  private boolean mUseLargestChild;
  @ViewDebug.ExportedProperty(category="layout")
  private float mWeightSum;
  
  public LinearLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public LinearLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public LinearLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public LinearLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    boolean bool1 = true;
    this.mBaselineAligned = true;
    this.mBaselineAlignedChildIndex = -1;
    this.mBaselineChildTop = 0;
    this.mGravity = 8388659;
    this.mLayoutDirection = -1;
    if ((!sCompatibilityDone) && (paramContext != null))
    {
      if (paramContext.getApplicationInfo().targetSdkVersion >= 28) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sRemeasureWeightedChildren = bool2;
      sCompatibilityDone = true;
    }
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.LinearLayout, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.LinearLayout, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramInt1 = localTypedArray.getInt(1, -1);
    if (paramInt1 >= 0) {
      setOrientation(paramInt1);
    }
    paramInt1 = localTypedArray.getInt(0, -1);
    if (paramInt1 >= 0) {
      setGravity(paramInt1);
    }
    boolean bool2 = localTypedArray.getBoolean(2, true);
    if (!bool2) {
      setBaselineAligned(bool2);
    }
    this.mWeightSum = localTypedArray.getFloat(4, -1.0F);
    this.mBaselineAlignedChildIndex = localTypedArray.getInt(3, -1);
    this.mUseLargestChild = localTypedArray.getBoolean(6, false);
    this.mShowDividers = localTypedArray.getInt(7, 0);
    this.mDividerPadding = localTypedArray.getDimensionPixelSize(8, 0);
    setDividerDrawable(localTypedArray.getDrawable(5));
    if (paramContext.getApplicationInfo().targetSdkVersion <= 23) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    this.mAllowInconsistentMeasurement = bool2;
    localTypedArray.recycle();
  }
  
  private boolean allViewsAreGoneBefore(int paramInt)
  {
    
    while (paramInt >= 0)
    {
      View localView = getVirtualChildAt(paramInt);
      if ((localView != null) && (localView.getVisibility() != 8)) {
        return false;
      }
      paramInt--;
    }
    return true;
  }
  
  private void forceUniformHeight(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
    for (int j = 0; j < paramInt1; j++)
    {
      View localView = getVirtualChildAt(j);
      if ((localView != null) && (localView.getVisibility() != 8))
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.height == -1)
        {
          int k = localLayoutParams.width;
          localLayoutParams.width = localView.getMeasuredWidth();
          measureChildWithMargins(localView, paramInt2, 0, i, 0);
          localLayoutParams.width = k;
        }
      }
    }
  }
  
  private void forceUniformWidth(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
    for (int j = 0; j < paramInt1; j++)
    {
      View localView = getVirtualChildAt(j);
      if ((localView != null) && (localView.getVisibility() != 8))
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.width == -1)
        {
          int k = localLayoutParams.height;
          localLayoutParams.height = localView.getMeasuredHeight();
          measureChildWithMargins(localView, i, 0, paramInt2, 0);
          localLayoutParams.height = k;
        }
      }
    }
  }
  
  private View getLastNonGoneChild()
  {
    for (int i = getVirtualChildCount() - 1; i >= 0; i--)
    {
      View localView = getVirtualChildAt(i);
      if ((localView != null) && (localView.getVisibility() != 8)) {
        return localView;
      }
    }
    return null;
  }
  
  private boolean isShowingDividers()
  {
    boolean bool;
    if ((this.mShowDividers != 0) && (this.mDivider != null)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void setChildFrame(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramView.layout(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  void drawDividersHorizontal(Canvas paramCanvas)
  {
    int i = getVirtualChildCount();
    boolean bool = isLayoutRtl();
    Object localObject1;
    Object localObject2;
    for (int j = 0; j < i; j++)
    {
      localObject1 = getVirtualChildAt(j);
      if ((localObject1 != null) && (((View)localObject1).getVisibility() != 8) && (hasDividerBeforeChildAt(j)))
      {
        localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
        int k;
        if (bool) {
          k = ((View)localObject1).getRight() + ((LayoutParams)localObject2).rightMargin;
        } else {
          k = ((View)localObject1).getLeft() - ((LayoutParams)localObject2).leftMargin - this.mDividerWidth;
        }
        drawVerticalDivider(paramCanvas, k);
      }
    }
    if (hasDividerBeforeChildAt(i))
    {
      localObject2 = getLastNonGoneChild();
      if (localObject2 == null)
      {
        if (bool) {
          j = getPaddingLeft();
        } else {
          j = getWidth() - getPaddingRight() - this.mDividerWidth;
        }
      }
      else
      {
        localObject1 = (LayoutParams)((View)localObject2).getLayoutParams();
        if (bool) {
          j = ((View)localObject2).getLeft() - ((LayoutParams)localObject1).leftMargin - this.mDividerWidth;
        } else {
          j = ((View)localObject2).getRight() + ((LayoutParams)localObject1).rightMargin;
        }
      }
      drawVerticalDivider(paramCanvas, j);
    }
  }
  
  void drawDividersVertical(Canvas paramCanvas)
  {
    int i = getVirtualChildCount();
    Object localObject1;
    Object localObject2;
    for (int j = 0; j < i; j++)
    {
      localObject1 = getVirtualChildAt(j);
      if ((localObject1 != null) && (((View)localObject1).getVisibility() != 8) && (hasDividerBeforeChildAt(j)))
      {
        localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
        drawHorizontalDivider(paramCanvas, ((View)localObject1).getTop() - ((LayoutParams)localObject2).topMargin - this.mDividerHeight);
      }
    }
    if (hasDividerBeforeChildAt(i))
    {
      localObject2 = getLastNonGoneChild();
      if (localObject2 == null)
      {
        j = getHeight() - getPaddingBottom() - this.mDividerHeight;
      }
      else
      {
        localObject1 = (LayoutParams)((View)localObject2).getLayoutParams();
        j = ((View)localObject2).getBottom() + ((LayoutParams)localObject1).bottomMargin;
      }
      drawHorizontalDivider(paramCanvas, j);
    }
  }
  
  void drawHorizontalDivider(Canvas paramCanvas, int paramInt)
  {
    this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, paramInt, getWidth() - getPaddingRight() - this.mDividerPadding, this.mDividerHeight + paramInt);
    this.mDivider.draw(paramCanvas);
  }
  
  void drawVerticalDivider(Canvas paramCanvas, int paramInt)
  {
    this.mDivider.setBounds(paramInt, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + paramInt, getHeight() - getPaddingBottom() - this.mDividerPadding);
    this.mDivider.draw(paramCanvas);
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("layout:baselineAligned", this.mBaselineAligned);
    paramViewHierarchyEncoder.addProperty("layout:baselineAlignedChildIndex", this.mBaselineAlignedChildIndex);
    paramViewHierarchyEncoder.addProperty("measurement:baselineChildTop", this.mBaselineChildTop);
    paramViewHierarchyEncoder.addProperty("measurement:orientation", this.mOrientation);
    paramViewHierarchyEncoder.addProperty("measurement:gravity", this.mGravity);
    paramViewHierarchyEncoder.addProperty("measurement:totalLength", this.mTotalLength);
    paramViewHierarchyEncoder.addProperty("layout:totalLength", this.mTotalLength);
    paramViewHierarchyEncoder.addProperty("layout:useLargestChild", this.mUseLargestChild);
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    int i = this.mOrientation;
    if (i == 0) {
      return new LayoutParams(-2, -2);
    }
    if (i == 1) {
      return new LayoutParams(-1, -2);
    }
    return null;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
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
  
  public CharSequence getAccessibilityClassName()
  {
    return LinearLayout.class.getName();
  }
  
  public int getBaseline()
  {
    if (this.mBaselineAlignedChildIndex < 0) {
      return super.getBaseline();
    }
    int i = getChildCount();
    int j = this.mBaselineAlignedChildIndex;
    if (i > j)
    {
      View localView = getChildAt(j);
      int k = localView.getBaseline();
      if (k == -1)
      {
        if (this.mBaselineAlignedChildIndex == 0) {
          return -1;
        }
        throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
      }
      i = this.mBaselineChildTop;
      j = i;
      if (this.mOrientation == 1)
      {
        int m = this.mGravity & 0x70;
        j = i;
        if (m != 48) {
          if (m != 16)
          {
            if (m != 80) {
              j = i;
            } else {
              j = this.mBottom - this.mTop - this.mPaddingBottom - this.mTotalLength;
            }
          }
          else {
            j = i + (this.mBottom - this.mTop - this.mPaddingTop - this.mPaddingBottom - this.mTotalLength) / 2;
          }
        }
      }
      return ((LayoutParams)localView.getLayoutParams()).topMargin + j + k;
    }
    throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
  }
  
  public int getBaselineAlignedChildIndex()
  {
    return this.mBaselineAlignedChildIndex;
  }
  
  int getChildrenSkipCount(View paramView, int paramInt)
  {
    return 0;
  }
  
  public Drawable getDividerDrawable()
  {
    return this.mDivider;
  }
  
  public int getDividerPadding()
  {
    return this.mDividerPadding;
  }
  
  public int getDividerWidth()
  {
    return this.mDividerWidth;
  }
  
  public int getGravity()
  {
    return this.mGravity;
  }
  
  int getLocationOffset(View paramView)
  {
    return 0;
  }
  
  int getNextLocationOffset(View paramView)
  {
    return 0;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public int getShowDividers()
  {
    return this.mShowDividers;
  }
  
  View getVirtualChildAt(int paramInt)
  {
    return getChildAt(paramInt);
  }
  
  int getVirtualChildCount()
  {
    return getChildCount();
  }
  
  public float getWeightSum()
  {
    return this.mWeightSum;
  }
  
  protected boolean hasDividerBeforeChildAt(int paramInt)
  {
    int i = getVirtualChildCount();
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    if (paramInt == i)
    {
      if ((this.mShowDividers & 0x4) != 0) {
        bool3 = true;
      }
      return bool3;
    }
    if (allViewsAreGoneBefore(paramInt))
    {
      bool3 = bool1;
      if ((this.mShowDividers & 0x1) != 0) {
        bool3 = true;
      }
      return bool3;
    }
    bool3 = bool2;
    if ((this.mShowDividers & 0x2) != 0) {
      bool3 = true;
    }
    return bool3;
  }
  
  public boolean isBaselineAligned()
  {
    return this.mBaselineAligned;
  }
  
  public boolean isMeasureWithLargestChildEnabled()
  {
    return this.mUseLargestChild;
  }
  
  void layoutHorizontal(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool1 = isLayoutRtl();
    int i = this.mPaddingTop;
    int j = paramInt4 - paramInt2;
    int k = this.mPaddingBottom;
    int m = this.mPaddingBottom;
    int n = getVirtualChildCount();
    int i1 = this.mGravity;
    boolean bool2 = this.mBaselineAligned;
    int[] arrayOfInt1 = this.mMaxAscent;
    int[] arrayOfInt2 = this.mMaxDescent;
    int i2 = getLayoutDirection();
    paramInt2 = Gravity.getAbsoluteGravity(i1 & 0x800007, i2);
    if (paramInt2 != 1)
    {
      if (paramInt2 != 5) {
        paramInt1 = this.mPaddingLeft;
      } else {
        paramInt1 = this.mPaddingLeft + paramInt3 - paramInt1 - this.mTotalLength;
      }
    }
    else {
      paramInt1 = this.mPaddingLeft + (paramInt3 - paramInt1 - this.mTotalLength) / 2;
    }
    int i3;
    int i4;
    if (bool1)
    {
      i3 = n - 1;
      i4 = -1;
    }
    else
    {
      i3 = 0;
      i4 = 1;
    }
    paramInt2 = 0;
    int i5 = j;
    paramInt3 = i;
    paramInt4 = paramInt1;
    while (paramInt2 < n)
    {
      int i6 = i3 + i4 * paramInt2;
      View localView = getVirtualChildAt(i6);
      if (localView == null)
      {
        paramInt4 += measureNullChild(i6);
      }
      else if (localView.getVisibility() != 8)
      {
        int i7 = localView.getMeasuredWidth();
        int i8 = localView.getMeasuredHeight();
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if ((bool2) && (localLayoutParams.height != -1)) {
          paramInt1 = localView.getBaseline();
        } else {
          paramInt1 = -1;
        }
        int i9 = localLayoutParams.gravity;
        if (i9 < 0) {
          i9 = i1 & 0x70;
        }
        i9 &= 0x70;
        if (i9 != 16)
        {
          if (i9 != 48)
          {
            if (i9 != 80)
            {
              paramInt1 = paramInt3;
            }
            else
            {
              i9 = j - k - i8 - localLayoutParams.bottomMargin;
              if (paramInt1 != -1)
              {
                int i10 = localView.getMeasuredHeight();
                paramInt1 = i9 - (arrayOfInt2[2] - (i10 - paramInt1));
              }
              else
              {
                paramInt1 = i9;
              }
            }
          }
          else
          {
            i9 = localLayoutParams.topMargin + paramInt3;
            if (paramInt1 != -1) {
              paramInt1 = i9 + (arrayOfInt1[1] - paramInt1);
            } else {
              paramInt1 = i9;
            }
          }
        }
        else {
          paramInt1 = (j - i - m - i8) / 2 + paramInt3 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
        }
        i9 = paramInt4;
        if (hasDividerBeforeChildAt(i6)) {
          i9 = paramInt4 + this.mDividerWidth;
        }
        paramInt4 = i9 + localLayoutParams.leftMargin;
        setChildFrame(localView, paramInt4 + getLocationOffset(localView), paramInt1, i7, i8);
        paramInt1 = localLayoutParams.rightMargin;
        i9 = getNextLocationOffset(localView);
        paramInt2 += getChildrenSkipCount(localView, i6);
        paramInt4 += i7 + paramInt1 + i9;
      }
      paramInt2++;
    }
  }
  
  void layoutVertical(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mPaddingLeft;
    int j = paramInt3 - paramInt1;
    int k = this.mPaddingRight;
    int m = this.mPaddingRight;
    int n = getVirtualChildCount();
    int i1 = this.mGravity;
    paramInt1 = i1 & 0x70;
    if (paramInt1 != 16)
    {
      if (paramInt1 != 80) {
        paramInt1 = this.mPaddingTop;
      } else {
        paramInt1 = this.mPaddingTop + paramInt4 - paramInt2 - this.mTotalLength;
      }
    }
    else {
      paramInt1 = this.mPaddingTop + (paramInt4 - paramInt2 - this.mTotalLength) / 2;
    }
    paramInt2 = 0;
    for (paramInt3 = i;; paramInt3 = paramInt4)
    {
      paramInt4 = paramInt3;
      if (paramInt2 >= n) {
        break;
      }
      View localView = getVirtualChildAt(paramInt2);
      if (localView == null)
      {
        paramInt1 += measureNullChild(paramInt2);
      }
      else if (localView.getVisibility() != 8)
      {
        int i2 = localView.getMeasuredWidth();
        int i3 = localView.getMeasuredHeight();
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        paramInt3 = localLayoutParams.gravity;
        if (paramInt3 < 0) {
          paramInt3 = i1 & 0x800007;
        }
        paramInt3 = Gravity.getAbsoluteGravity(paramInt3, getLayoutDirection()) & 0x7;
        if (paramInt3 != 1)
        {
          if (paramInt3 != 5) {
            paramInt3 = localLayoutParams.leftMargin + paramInt4;
          } else {
            paramInt3 = j - k - i2 - localLayoutParams.rightMargin;
          }
        }
        else {
          paramInt3 = (j - i - m - i2) / 2 + paramInt4 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
        }
        int i4 = paramInt1;
        if (hasDividerBeforeChildAt(paramInt2)) {
          i4 = paramInt1 + this.mDividerHeight;
        }
        paramInt1 = i4 + localLayoutParams.topMargin;
        setChildFrame(localView, paramInt3, paramInt1 + getLocationOffset(localView), i2, i3);
        paramInt3 = localLayoutParams.bottomMargin;
        i4 = getNextLocationOffset(localView);
        paramInt2 += getChildrenSkipCount(localView, paramInt2);
        paramInt1 += i3 + paramInt3 + i4;
      }
      paramInt2++;
    }
  }
  
  void measureChildBeforeLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    measureChildWithMargins(paramView, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  void measureHorizontal(int paramInt1, int paramInt2)
  {
    this.mTotalLength = 0;
    int i = 0;
    int j = getVirtualChildCount();
    int k = View.MeasureSpec.getMode(paramInt1);
    int m = View.MeasureSpec.getMode(paramInt2);
    if ((this.mMaxAscent == null) || (this.mMaxDescent == null))
    {
      this.mMaxAscent = new int[4];
      this.mMaxDescent = new int[4];
    }
    int[] arrayOfInt1 = this.mMaxAscent;
    int[] arrayOfInt2 = this.mMaxDescent;
    arrayOfInt1[3] = -1;
    arrayOfInt1[2] = -1;
    arrayOfInt1[1] = -1;
    arrayOfInt1[0] = -1;
    arrayOfInt2[3] = -1;
    arrayOfInt2[2] = -1;
    arrayOfInt2[1] = -1;
    arrayOfInt2[0] = -1;
    boolean bool1 = this.mBaselineAligned;
    int n = 0;
    boolean bool2 = this.mUseLargestChild;
    int i1;
    if (k == 1073741824) {
      i1 = 1;
    } else {
      i1 = 0;
    }
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    float f1 = 0.0F;
    int i7 = 0;
    int i8 = 1;
    int i9 = Integer.MIN_VALUE;
    int i10 = 0;
    Object localObject1;
    Object localObject2;
    int i14;
    while (i7 < j)
    {
      View localView = getVirtualChildAt(i7);
      if (localView == null)
      {
        this.mTotalLength += measureNullChild(i7);
        i11 = i4;
        i4 = i9;
        i9 = i11;
      }
      else if (localView.getVisibility() == 8)
      {
        i7 += getChildrenSkipCount(localView, i7);
        i11 = i4;
        i4 = i9;
        i9 = i11;
      }
      else
      {
        int i12 = i3 + 1;
        if (hasDividerBeforeChildAt(i7)) {
          this.mTotalLength += this.mDividerWidth;
        }
        localObject1 = (LayoutParams)localView.getLayoutParams();
        f1 += ((LayoutParams)localObject1).weight;
        if ((((LayoutParams)localObject1).width == 0) && (((LayoutParams)localObject1).weight > 0.0F)) {
          i3 = 1;
        } else {
          i3 = 0;
        }
        if ((k == 1073741824) && (i3 != 0))
        {
          if (i1 != 0)
          {
            this.mTotalLength += ((LayoutParams)localObject1).leftMargin + ((LayoutParams)localObject1).rightMargin;
          }
          else
          {
            i3 = this.mTotalLength;
            this.mTotalLength = Math.max(i3, ((LayoutParams)localObject1).leftMargin + i3 + ((LayoutParams)localObject1).rightMargin);
          }
          if (bool1)
          {
            i3 = View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 0);
            localView.measure(i3, View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 0));
          }
          else
          {
            n = 1;
          }
        }
        else
        {
          if (i3 != 0) {
            ((LayoutParams)localObject1).width = -2;
          }
          if (f1 == 0.0F) {
            i11 = this.mTotalLength;
          } else {
            i11 = 0;
          }
          measureChildBeforeLayout(localView, i7, paramInt1, i11, paramInt2, 0);
          i11 = localView.getMeasuredWidth();
          if (i3 != 0)
          {
            ((LayoutParams)localObject1).width = 0;
            i2 += i11;
          }
          localObject2 = localObject1;
          if (i1 != 0)
          {
            this.mTotalLength += ((LayoutParams)localObject2).leftMargin + i11 + ((LayoutParams)localObject2).rightMargin + getNextLocationOffset(localView);
          }
          else
          {
            i3 = this.mTotalLength;
            this.mTotalLength = Math.max(i3, i3 + i11 + ((LayoutParams)localObject2).leftMargin + ((LayoutParams)localObject2).rightMargin + getNextLocationOffset(localView));
          }
          if (bool2) {
            i9 = Math.max(i11, i9);
          }
        }
        i3 = i;
        i11 = i5;
        int i13 = i7;
        i5 = 0;
        i7 = i5;
        i = i10;
        if (m != 1073741824)
        {
          i7 = i5;
          i = i10;
          if (((LayoutParams)localObject1).height == -1)
          {
            i = 1;
            i7 = 1;
          }
        }
        i14 = ((LayoutParams)localObject1).topMargin + ((LayoutParams)localObject1).bottomMargin;
        i10 = localView.getMeasuredHeight() + i14;
        int i15 = combineMeasuredStates(i4, localView.getMeasuredState());
        if (bool1)
        {
          i4 = localView.getBaseline();
          if (i4 != -1)
          {
            if (((LayoutParams)localObject1).gravity < 0) {
              i5 = this.mGravity;
            } else {
              i5 = ((LayoutParams)localObject1).gravity;
            }
            i5 = ((i5 & 0x70) >> 4 & 0xFFFFFFFE) >> 1;
            arrayOfInt1[i5] = Math.max(arrayOfInt1[i5], i4);
            arrayOfInt2[i5] = Math.max(arrayOfInt2[i5], i10 - i4);
          }
          else {}
        }
        i4 = i14;
        i14 = Math.max(i6, i10);
        if ((i8 != 0) && (((LayoutParams)localObject1).height == -1)) {
          i5 = 1;
        } else {
          i5 = 0;
        }
        if (((LayoutParams)localObject1).weight > 0.0F)
        {
          if (i7 == 0) {
            i4 = i10;
          }
          i4 = Math.max(i11, i4);
          i6 = i3;
        }
        else
        {
          if (i7 == 0) {
            i4 = i10;
          }
          i6 = Math.max(i3, i4);
          i4 = i11;
        }
        i7 = getChildrenSkipCount(localView, i13);
        i8 = i5;
        i5 = i9;
        i11 = i14;
        i14 = i6;
        int i16 = i4;
        i9 = i15;
        i7 = i13 + i7;
        i3 = i12;
        i10 = i;
        i4 = i5;
        i6 = i11;
        i = i14;
        i5 = i16;
      }
      i7++;
      i11 = i9;
      i9 = i4;
      i4 = i11;
    }
    i7 = i9;
    if ((i3 > 0) && (hasDividerBeforeChildAt(j))) {
      this.mTotalLength += this.mDividerWidth;
    }
    if ((arrayOfInt1[1] == -1) && (arrayOfInt1[0] == -1) && (arrayOfInt1[2] == -1) && (arrayOfInt1[3] == -1)) {
      break label1095;
    }
    i6 = Math.max(i6, Math.max(arrayOfInt1[3], Math.max(arrayOfInt1[0], Math.max(arrayOfInt1[1], arrayOfInt1[2]))) + Math.max(arrayOfInt2[3], Math.max(arrayOfInt2[0], Math.max(arrayOfInt2[1], arrayOfInt2[2]))));
    label1095:
    if (bool2)
    {
      i9 = k;
      if ((i9 != Integer.MIN_VALUE) && (i9 != 0))
      {
        i9 = i6;
      }
      else
      {
        this.mTotalLength = 0;
        for (i9 = 0; i9 < j; i9++)
        {
          localObject2 = getVirtualChildAt(i9);
          if (localObject2 == null)
          {
            this.mTotalLength += measureNullChild(i9);
          }
          else if (((View)localObject2).getVisibility() == 8)
          {
            i9 += getChildrenSkipCount((View)localObject2, i9);
          }
          else
          {
            localObject1 = (LayoutParams)((View)localObject2).getLayoutParams();
            if (i1 != 0)
            {
              i11 = this.mTotalLength;
              i3 = ((LayoutParams)localObject1).leftMargin;
              this.mTotalLength = (i11 + (i3 + i7 + ((LayoutParams)localObject1).rightMargin + getNextLocationOffset((View)localObject2)));
            }
            else
            {
              i3 = this.mTotalLength;
              this.mTotalLength = Math.max(i3, i3 + i7 + ((LayoutParams)localObject1).leftMargin + ((LayoutParams)localObject1).rightMargin + getNextLocationOffset((View)localObject2));
            }
          }
        }
        i9 = i6;
      }
    }
    else
    {
      i9 = i6;
    }
    this.mTotalLength += this.mPaddingLeft + this.mPaddingRight;
    i6 = resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), paramInt1, 0);
    i3 = i6 & 0xFFFFFF;
    int i11 = this.mTotalLength;
    if (this.mAllowInconsistentMeasurement) {
      i2 = 0;
    }
    i2 = i3 - i11 + i2;
    if ((n == 0) && (((!sRemeasureWeightedChildren) && (i2 == 0)) || (f1 <= 0.0F)))
    {
      i = Math.max(i, i5);
      if ((bool2) && (k != 1073741824))
      {
        k = 0;
        i5 = i3;
        while (k < j)
        {
          localObject1 = getVirtualChildAt(k);
          if ((localObject1 != null) && (((View)localObject1).getVisibility() != 8)) {
            if (((LayoutParams)((View)localObject1).getLayoutParams()).weight > 0.0F) {
              ((View)localObject1).measure(View.MeasureSpec.makeMeasureSpec(i7, 1073741824), View.MeasureSpec.makeMeasureSpec(((View)localObject1).getMeasuredHeight(), 1073741824));
            } else {}
          }
          k++;
        }
      }
      i5 = i4;
      i4 = j;
      i2 = i;
    }
    else
    {
      float f2 = this.mWeightSum;
      if (f2 > 0.0F) {
        f1 = f2;
      }
      arrayOfInt1[3] = -1;
      arrayOfInt1[2] = -1;
      arrayOfInt1[1] = -1;
      arrayOfInt1[0] = -1;
      arrayOfInt2[3] = -1;
      arrayOfInt2[2] = -1;
      arrayOfInt2[1] = -1;
      arrayOfInt2[0] = -1;
      i5 = -1;
      this.mTotalLength = 0;
      n = 0;
      i9 = i4;
      i3 = i2;
      i4 = i;
      i = j;
      i2 = i6;
      j = n;
      n = k;
      i6 = i5;
      i5 = i7;
      k = i3;
      while (j < i)
      {
        localObject1 = getVirtualChildAt(j);
        if ((localObject1 != null) && (((View)localObject1).getVisibility() != 8))
        {
          localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
          f2 = ((LayoutParams)localObject2).weight;
          if (f2 > 0.0F)
          {
            i3 = (int)(k * f2 / f1);
            if ((this.mUseLargestChild) && (n != 1073741824)) {
              i7 = i5;
            } else if ((((LayoutParams)localObject2).width == 0) && ((!this.mAllowInconsistentMeasurement) || (n == 1073741824))) {
              i7 = i3;
            } else {
              i7 = ((View)localObject1).getMeasuredWidth() + i3;
            }
            ((View)localObject1).measure(View.MeasureSpec.makeMeasureSpec(Math.max(0, i7), 1073741824), getChildMeasureSpec(paramInt2, this.mPaddingTop + this.mPaddingBottom + ((LayoutParams)localObject2).topMargin + ((LayoutParams)localObject2).bottomMargin, ((LayoutParams)localObject2).height));
            i9 = combineMeasuredStates(i9, ((View)localObject1).getMeasuredState() & 0xFF000000);
            k -= i3;
            f1 -= f2;
          }
          if (i1 != 0)
          {
            this.mTotalLength += ((View)localObject1).getMeasuredWidth() + ((LayoutParams)localObject2).leftMargin + ((LayoutParams)localObject2).rightMargin + getNextLocationOffset((View)localObject1);
          }
          else
          {
            i7 = this.mTotalLength;
            this.mTotalLength = Math.max(i7, ((View)localObject1).getMeasuredWidth() + i7 + ((LayoutParams)localObject2).leftMargin + ((LayoutParams)localObject2).rightMargin + getNextLocationOffset((View)localObject1));
          }
          if ((m != 1073741824) && (((LayoutParams)localObject2).height == -1)) {
            i7 = 1;
          } else {
            i7 = 0;
          }
          i14 = ((LayoutParams)localObject2).topMargin + ((LayoutParams)localObject2).bottomMargin;
          i11 = ((View)localObject1).getMeasuredHeight() + i14;
          i3 = Math.max(i6, i11);
          if (i7 != 0) {
            i6 = i14;
          } else {
            i6 = i11;
          }
          i7 = Math.max(i4, i6);
          if ((i8 != 0) && (((LayoutParams)localObject2).height == -1)) {
            i4 = 1;
          } else {
            i4 = 0;
          }
          if (bool1)
          {
            i8 = ((View)localObject1).getBaseline();
            if (i8 != -1)
            {
              if (((LayoutParams)localObject2).gravity < 0) {
                i6 = this.mGravity;
              } else {
                i6 = ((LayoutParams)localObject2).gravity;
              }
              i6 = ((i6 & 0x70) >> 4 & 0xFFFFFFFE) >> 1;
              arrayOfInt1[i6] = Math.max(arrayOfInt1[i6], i8);
              arrayOfInt2[i6] = Math.max(arrayOfInt2[i6], i11 - i8);
            }
            else {}
          }
          i8 = i4;
          i4 = i7;
          i6 = i3;
        }
        j++;
      }
      i5 = i;
      this.mTotalLength += this.mPaddingLeft + this.mPaddingRight;
      if ((arrayOfInt1[1] == -1) && (arrayOfInt1[0] == -1) && (arrayOfInt1[2] == -1))
      {
        i = i6;
        if (arrayOfInt1[3] == -1) {}
      }
      else
      {
        i = Math.max(i6, Math.max(arrayOfInt1[3], Math.max(arrayOfInt1[0], Math.max(arrayOfInt1[1], arrayOfInt1[2]))) + Math.max(arrayOfInt2[3], Math.max(arrayOfInt2[0], Math.max(arrayOfInt2[1], arrayOfInt2[2]))));
      }
      k = i4;
      i6 = i2;
      i4 = i5;
      i2 = k;
      i5 = i9;
      i9 = i;
    }
    i = i9;
    if (i8 == 0)
    {
      i = i9;
      if (m != 1073741824) {
        i = i2;
      }
    }
    setMeasuredDimension(i6 | 0xFF000000 & i5, resolveSizeAndState(Math.max(i + (this.mPaddingTop + this.mPaddingBottom), getSuggestedMinimumHeight()), paramInt2, i5 << 16));
    if (i10 != 0) {
      forceUniformHeight(i4, paramInt1);
    }
  }
  
  int measureNullChild(int paramInt)
  {
    return 0;
  }
  
  void measureVertical(int paramInt1, int paramInt2)
  {
    this.mTotalLength = 0;
    int i = 0;
    float f1 = 0.0F;
    int j = getVirtualChildCount();
    int k = View.MeasureSpec.getMode(paramInt1);
    int m = View.MeasureSpec.getMode(paramInt2);
    int n = this.mBaselineAlignedChildIndex;
    boolean bool = this.mUseLargestChild;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    int i7 = Integer.MIN_VALUE;
    int i8 = 0;
    int i9 = 0;
    int i10 = 1;
    Object localObject1;
    Object localObject2;
    while (i9 < j)
    {
      localObject1 = getVirtualChildAt(i9);
      if (localObject1 == null)
      {
        this.mTotalLength += measureNullChild(i9);
      }
      else if (((View)localObject1).getVisibility() == 8)
      {
        i9 += getChildrenSkipCount((View)localObject1, i9);
      }
      else
      {
        int i11 = i2 + 1;
        if (hasDividerBeforeChildAt(i9)) {
          this.mTotalLength += this.mDividerHeight;
        }
        localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
        f1 += ((LayoutParams)localObject2).weight;
        if ((((LayoutParams)localObject2).height == 0) && (((LayoutParams)localObject2).weight > 0.0F)) {
          i12 = 1;
        } else {
          i12 = 0;
        }
        if ((m == 1073741824) && (i12 != 0))
        {
          i8 = this.mTotalLength;
          this.mTotalLength = Math.max(i8, ((LayoutParams)localObject2).topMargin + i8 + ((LayoutParams)localObject2).bottomMargin);
          i8 = 1;
        }
        else
        {
          if (i12 != 0) {
            ((LayoutParams)localObject2).height = -2;
          }
          if (f1 == 0.0F) {
            i2 = this.mTotalLength;
          } else {
            i2 = 0;
          }
          Object localObject3 = localObject2;
          measureChildBeforeLayout((View)localObject1, i9, paramInt1, 0, paramInt2, i2);
          i13 = ((View)localObject1).getMeasuredHeight();
          i2 = i1;
          if (i12 != 0)
          {
            ((LayoutParams)localObject3).height = 0;
            i2 = i1 + i13;
          }
          i1 = this.mTotalLength;
          this.mTotalLength = Math.max(i1, i1 + i13 + ((LayoutParams)localObject3).topMargin + ((LayoutParams)localObject3).bottomMargin + getNextLocationOffset((View)localObject1));
          if (bool)
          {
            i7 = Math.max(i13, i7);
            i1 = i2;
          }
          else
          {
            i1 = i2;
          }
        }
        i2 = i6;
        if ((n >= 0) && (n == i9 + 1)) {
          this.mBaselineChildTop = this.mTotalLength;
        }
        if ((i9 < n) && (((LayoutParams)localObject2).weight > 0.0F)) {
          throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
        }
        i12 = 0;
        if ((k != 1073741824) && (((LayoutParams)localObject2).width == -1))
        {
          i3 = 1;
          i12 = 1;
        }
        i13 = ((LayoutParams)localObject2).leftMargin + ((LayoutParams)localObject2).rightMargin;
        int i14 = ((View)localObject1).getMeasuredWidth() + i13;
        i4 = Math.max(i4, i14);
        int i15 = combineMeasuredStates(i5, ((View)localObject1).getMeasuredState());
        if ((i10 != 0) && (((LayoutParams)localObject2).width == -1)) {
          i6 = 1;
        } else {
          i6 = 0;
        }
        if (((LayoutParams)localObject2).weight > 0.0F)
        {
          if (i12 != 0) {
            i5 = i13;
          } else {
            i5 = i14;
          }
          i10 = Math.max(i, i5);
          i5 = i2;
        }
        else
        {
          i10 = i;
          if (i12 == 0) {
            i13 = i14;
          }
          i5 = Math.max(i2, i13);
        }
        i9 += getChildrenSkipCount((View)localObject1, i9);
        i12 = i15;
        i = i10;
        i10 = i6;
        i2 = i11;
        i6 = i5;
        i5 = i12;
      }
      i9++;
    }
    if ((i2 > 0) && (hasDividerBeforeChildAt(j))) {
      this.mTotalLength += this.mDividerHeight;
    }
    if (bool)
    {
      i9 = m;
      if ((i9 != Integer.MIN_VALUE) && (i9 != 0)) {
        break label846;
      }
      this.mTotalLength = 0;
      for (i9 = 0; i9 < j; i9++)
      {
        localObject1 = getVirtualChildAt(i9);
        if (localObject1 == null)
        {
          this.mTotalLength += measureNullChild(i9);
        }
        else if (((View)localObject1).getVisibility() == 8)
        {
          i9 += getChildrenSkipCount((View)localObject1, i9);
        }
        else
        {
          localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
          i2 = this.mTotalLength;
          this.mTotalLength = Math.max(i2, i2 + i7 + ((LayoutParams)localObject2).topMargin + ((LayoutParams)localObject2).bottomMargin + getNextLocationOffset((View)localObject1));
        }
      }
    }
    label846:
    i2 = m;
    this.mTotalLength += this.mPaddingTop + this.mPaddingBottom;
    int i13 = resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), paramInt2, 0);
    m = i13 & 0xFFFFFF;
    i9 = this.mTotalLength;
    if (this.mAllowInconsistentMeasurement) {
      i1 = 0;
    }
    int i12 = m - i9 + i1;
    if ((i8 == 0) && (((!sRemeasureWeightedChildren) && (i12 == 0)) || (f1 <= 0.0F)))
    {
      i6 = Math.max(i6, i);
      if ((bool) && (i2 != 1073741824)) {
        for (i1 = 0; i1 < j; i1++)
        {
          localObject2 = getVirtualChildAt(i1);
          if ((localObject2 != null) && (((View)localObject2).getVisibility() != 8)) {
            if (((LayoutParams)((View)localObject2).getLayoutParams()).weight > 0.0F) {
              ((View)localObject2).measure(View.MeasureSpec.makeMeasureSpec(((View)localObject2).getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(i7, 1073741824));
            } else {}
          }
        }
      }
      i1 = i5;
      i5 = i6;
      i6 = j;
    }
    else
    {
      float f2 = this.mWeightSum;
      if (f2 > 0.0F) {
        f1 = f2;
      }
      this.mTotalLength = 0;
      i9 = 0;
      i1 = i6;
      m = i5;
      i = n;
      i6 = i12;
      i5 = i1;
      i1 = m;
      while (i9 < j)
      {
        localObject2 = getVirtualChildAt(i9);
        if ((localObject2 != null) && (((View)localObject2).getVisibility() != 8))
        {
          localObject1 = (LayoutParams)((View)localObject2).getLayoutParams();
          f2 = ((LayoutParams)localObject1).weight;
          if (f2 > 0.0F)
          {
            i8 = (int)(i6 * f2 / f1);
            if ((this.mUseLargestChild) && (i2 != 1073741824)) {
              m = i7;
            } else if ((((LayoutParams)localObject1).height == 0) && ((!this.mAllowInconsistentMeasurement) || (i2 == 1073741824))) {
              m = i8;
            } else {
              m = ((View)localObject2).getMeasuredHeight() + i8;
            }
            m = View.MeasureSpec.makeMeasureSpec(Math.max(0, m), 1073741824);
            ((View)localObject2).measure(getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight + ((LayoutParams)localObject1).leftMargin + ((LayoutParams)localObject1).rightMargin, ((LayoutParams)localObject1).width), m);
            i1 = combineMeasuredStates(i1, ((View)localObject2).getMeasuredState() & 0xFF00);
            f1 -= f2;
            i6 -= i8;
          }
          i12 = ((LayoutParams)localObject1).leftMargin + ((LayoutParams)localObject1).rightMargin;
          i8 = ((View)localObject2).getMeasuredWidth() + i12;
          i4 = Math.max(i4, i8);
          if ((k != 1073741824) && (((LayoutParams)localObject1).width == -1)) {
            m = 1;
          } else {
            m = 0;
          }
          if (m != 0) {
            m = i12;
          } else {
            m = i8;
          }
          m = Math.max(i5, m);
          if ((i10 != 0) && (((LayoutParams)localObject1).width == -1)) {
            i5 = 1;
          } else {
            i5 = 0;
          }
          i10 = this.mTotalLength;
          this.mTotalLength = Math.max(i10, i10 + ((View)localObject2).getMeasuredHeight() + ((LayoutParams)localObject1).topMargin + ((LayoutParams)localObject1).bottomMargin + getNextLocationOffset((View)localObject2));
          i10 = i5;
          i5 = m;
        }
        i9++;
      }
      i6 = j;
      this.mTotalLength += this.mPaddingTop + this.mPaddingBottom;
    }
    i7 = i4;
    if (i10 == 0)
    {
      i7 = i4;
      if (k != 1073741824) {
        i7 = i5;
      }
    }
    setMeasuredDimension(resolveSizeAndState(Math.max(i7 + (this.mPaddingLeft + this.mPaddingRight), getSuggestedMinimumWidth()), paramInt1, i1), i13);
    if (i3 != 0) {
      forceUniformWidth(i6, paramInt2);
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.mDivider == null) {
      return;
    }
    if (this.mOrientation == 1) {
      drawDividersVertical(paramCanvas);
    } else {
      drawDividersHorizontal(paramCanvas);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mOrientation == 1) {
      layoutVertical(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      layoutHorizontal(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mOrientation == 1) {
      measureVertical(paramInt1, paramInt2);
    } else {
      measureHorizontal(paramInt1, paramInt2);
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    if (paramInt != this.mLayoutDirection)
    {
      this.mLayoutDirection = paramInt;
      if (this.mOrientation == 0) {
        requestLayout();
      }
    }
  }
  
  @RemotableViewMethod
  public void setBaselineAligned(boolean paramBoolean)
  {
    this.mBaselineAligned = paramBoolean;
  }
  
  @RemotableViewMethod
  public void setBaselineAlignedChildIndex(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < getChildCount()))
    {
      this.mBaselineAlignedChildIndex = paramInt;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("base aligned child index out of range (0, ");
    localStringBuilder.append(getChildCount());
    localStringBuilder.append(")");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public void setDividerDrawable(Drawable paramDrawable)
  {
    if (paramDrawable == this.mDivider) {
      return;
    }
    this.mDivider = paramDrawable;
    if (paramDrawable != null)
    {
      this.mDividerWidth = paramDrawable.getIntrinsicWidth();
      this.mDividerHeight = paramDrawable.getIntrinsicHeight();
    }
    else
    {
      this.mDividerWidth = 0;
      this.mDividerHeight = 0;
    }
    setWillNotDraw(isShowingDividers() ^ true);
    requestLayout();
  }
  
  public void setDividerPadding(int paramInt)
  {
    if (paramInt == this.mDividerPadding) {
      return;
    }
    this.mDividerPadding = paramInt;
    if (isShowingDividers())
    {
      requestLayout();
      invalidate();
    }
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
  public void setMeasureWithLargestChildEnabled(boolean paramBoolean)
  {
    this.mUseLargestChild = paramBoolean;
  }
  
  public void setOrientation(int paramInt)
  {
    if (this.mOrientation != paramInt)
    {
      this.mOrientation = paramInt;
      requestLayout();
    }
  }
  
  public void setShowDividers(int paramInt)
  {
    if (paramInt == this.mShowDividers) {
      return;
    }
    this.mShowDividers = paramInt;
    setWillNotDraw(isShowingDividers() ^ true);
    requestLayout();
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
  
  @RemotableViewMethod
  public void setWeightSum(float paramFloat)
  {
    this.mWeightSum = Math.max(0.0F, paramFloat);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface DividerMode {}
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    @ViewDebug.ExportedProperty(category="layout", mapping={@android.view.ViewDebug.IntToString(from=-1, to="NONE"), @android.view.ViewDebug.IntToString(from=0, to="NONE"), @android.view.ViewDebug.IntToString(from=48, to="TOP"), @android.view.ViewDebug.IntToString(from=80, to="BOTTOM"), @android.view.ViewDebug.IntToString(from=3, to="LEFT"), @android.view.ViewDebug.IntToString(from=5, to="RIGHT"), @android.view.ViewDebug.IntToString(from=8388611, to="START"), @android.view.ViewDebug.IntToString(from=8388613, to="END"), @android.view.ViewDebug.IntToString(from=16, to="CENTER_VERTICAL"), @android.view.ViewDebug.IntToString(from=112, to="FILL_VERTICAL"), @android.view.ViewDebug.IntToString(from=1, to="CENTER_HORIZONTAL"), @android.view.ViewDebug.IntToString(from=7, to="FILL_HORIZONTAL"), @android.view.ViewDebug.IntToString(from=17, to="CENTER"), @android.view.ViewDebug.IntToString(from=119, to="FILL")})
    public int gravity = -1;
    @ViewDebug.ExportedProperty(category="layout")
    public float weight;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.weight = 0.0F;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2);
      this.weight = paramFloat;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.LinearLayout_Layout);
      this.weight = paramContext.getFloat(3, 0.0F);
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
      this.weight = paramLayoutParams.weight;
      this.gravity = paramLayoutParams.gravity;
    }
    
    public String debug(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("LinearLayout.LayoutParams={width=");
      localStringBuilder.append(sizeToString(this.width));
      localStringBuilder.append(", height=");
      localStringBuilder.append(sizeToString(this.height));
      localStringBuilder.append(" weight=");
      localStringBuilder.append(this.weight);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    @UnsupportedAppUsage
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("layout:weight", this.weight);
      paramViewHierarchyEncoder.addProperty("layout:gravity", this.gravity);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface OrientationMode {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/LinearLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */