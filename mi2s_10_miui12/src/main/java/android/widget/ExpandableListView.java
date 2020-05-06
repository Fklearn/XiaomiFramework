package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.BaseSavedState;
import com.android.internal.R.styleable;
import java.util.ArrayList;

public class ExpandableListView
  extends ListView
{
  public static final int CHILD_INDICATOR_INHERIT = -1;
  private static final int[] CHILD_LAST_STATE_SET = { 16842918 };
  private static final int[] EMPTY_STATE_SET = new int[0];
  private static final int[] GROUP_EMPTY_STATE_SET;
  private static final int[] GROUP_EXPANDED_EMPTY_STATE_SET;
  private static final int[] GROUP_EXPANDED_STATE_SET = { 16842920 };
  @UnsupportedAppUsage
  private static final int[][] GROUP_STATE_SETS;
  private static final int INDICATOR_UNDEFINED = -2;
  private static final long PACKED_POSITION_INT_MASK_CHILD = -1L;
  private static final long PACKED_POSITION_INT_MASK_GROUP = 2147483647L;
  private static final long PACKED_POSITION_MASK_CHILD = 4294967295L;
  private static final long PACKED_POSITION_MASK_GROUP = 9223372032559808512L;
  private static final long PACKED_POSITION_MASK_TYPE = Long.MIN_VALUE;
  private static final long PACKED_POSITION_SHIFT_GROUP = 32L;
  private static final long PACKED_POSITION_SHIFT_TYPE = 63L;
  public static final int PACKED_POSITION_TYPE_CHILD = 1;
  public static final int PACKED_POSITION_TYPE_GROUP = 0;
  public static final int PACKED_POSITION_TYPE_NULL = 2;
  public static final long PACKED_POSITION_VALUE_NULL = 4294967295L;
  private ExpandableListAdapter mAdapter;
  @UnsupportedAppUsage
  private Drawable mChildDivider;
  private Drawable mChildIndicator;
  private int mChildIndicatorEnd;
  private int mChildIndicatorLeft;
  private int mChildIndicatorRight;
  private int mChildIndicatorStart;
  @UnsupportedAppUsage
  private ExpandableListConnector mConnector;
  @UnsupportedAppUsage
  private Drawable mGroupIndicator;
  private int mIndicatorEnd;
  @UnsupportedAppUsage
  private int mIndicatorLeft;
  private final Rect mIndicatorRect = new Rect();
  @UnsupportedAppUsage
  private int mIndicatorRight;
  private int mIndicatorStart;
  @UnsupportedAppUsage
  private OnChildClickListener mOnChildClickListener;
  @UnsupportedAppUsage
  private OnGroupClickListener mOnGroupClickListener;
  @UnsupportedAppUsage
  private OnGroupCollapseListener mOnGroupCollapseListener;
  @UnsupportedAppUsage
  private OnGroupExpandListener mOnGroupExpandListener;
  
  static
  {
    GROUP_EMPTY_STATE_SET = new int[] { 16842921 };
    GROUP_EXPANDED_EMPTY_STATE_SET = new int[] { 16842920, 16842921 };
    GROUP_STATE_SETS = new int[][] { EMPTY_STATE_SET, GROUP_EXPANDED_STATE_SET, GROUP_EMPTY_STATE_SET, GROUP_EXPANDED_EMPTY_STATE_SET };
  }
  
  public ExpandableListView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ExpandableListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842863);
  }
  
  public ExpandableListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ExpandableListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ExpandableListView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.ExpandableListView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mGroupIndicator = localTypedArray.getDrawable(0);
    this.mChildIndicator = localTypedArray.getDrawable(1);
    this.mIndicatorLeft = localTypedArray.getDimensionPixelSize(2, 0);
    this.mIndicatorRight = localTypedArray.getDimensionPixelSize(3, 0);
    if (this.mIndicatorRight == 0)
    {
      paramContext = this.mGroupIndicator;
      if (paramContext != null) {
        this.mIndicatorRight = (this.mIndicatorLeft + paramContext.getIntrinsicWidth());
      }
    }
    this.mChildIndicatorLeft = localTypedArray.getDimensionPixelSize(4, -1);
    this.mChildIndicatorRight = localTypedArray.getDimensionPixelSize(5, -1);
    this.mChildDivider = localTypedArray.getDrawable(6);
    if (!isRtlCompatibilityMode())
    {
      this.mIndicatorStart = localTypedArray.getDimensionPixelSize(7, -2);
      this.mIndicatorEnd = localTypedArray.getDimensionPixelSize(8, -2);
      this.mChildIndicatorStart = localTypedArray.getDimensionPixelSize(9, -1);
      this.mChildIndicatorEnd = localTypedArray.getDimensionPixelSize(10, -1);
    }
    localTypedArray.recycle();
  }
  
  private int getAbsoluteFlatPosition(int paramInt)
  {
    return getHeaderViewsCount() + paramInt;
  }
  
  private long getChildOrGroupId(ExpandableListPosition paramExpandableListPosition)
  {
    if (paramExpandableListPosition.type == 1) {
      return this.mAdapter.getChildId(paramExpandableListPosition.groupPos, paramExpandableListPosition.childPos);
    }
    return this.mAdapter.getGroupId(paramExpandableListPosition.groupPos);
  }
  
  private int getFlatPositionForConnector(int paramInt)
  {
    return paramInt - getHeaderViewsCount();
  }
  
  private Drawable getIndicator(ExpandableListConnector.PositionMetadata paramPositionMetadata)
  {
    int i = paramPositionMetadata.position.type;
    int j = 2;
    Drawable localDrawable1;
    Drawable localDrawable2;
    if (i == 2)
    {
      localDrawable1 = this.mGroupIndicator;
      localDrawable2 = localDrawable1;
      if (localDrawable1 != null)
      {
        localDrawable2 = localDrawable1;
        if (localDrawable1.isStateful())
        {
          if ((paramPositionMetadata.groupMetadata != null) && (paramPositionMetadata.groupMetadata.lastChildFlPos != paramPositionMetadata.groupMetadata.flPos)) {
            i = 0;
          } else {
            i = 1;
          }
          int k = paramPositionMetadata.isExpanded();
          if (i != 0) {
            i = j;
          } else {
            i = 0;
          }
          localDrawable1.setState(GROUP_STATE_SETS[(i | k)]);
          localDrawable2 = localDrawable1;
        }
      }
    }
    else
    {
      localDrawable1 = this.mChildIndicator;
      localDrawable2 = localDrawable1;
      if (localDrawable1 != null)
      {
        localDrawable2 = localDrawable1;
        if (localDrawable1.isStateful())
        {
          if (paramPositionMetadata.position.flatListPos == paramPositionMetadata.groupMetadata.lastChildFlPos) {
            paramPositionMetadata = CHILD_LAST_STATE_SET;
          } else {
            paramPositionMetadata = EMPTY_STATE_SET;
          }
          localDrawable1.setState(paramPositionMetadata);
          localDrawable2 = localDrawable1;
        }
      }
    }
    return localDrawable2;
  }
  
  public static int getPackedPositionChild(long paramLong)
  {
    if (paramLong == 4294967295L) {
      return -1;
    }
    if ((paramLong & 0x8000000000000000) != Long.MIN_VALUE) {
      return -1;
    }
    return (int)(0xFFFFFFFF & paramLong);
  }
  
  public static long getPackedPositionForChild(int paramInt1, int paramInt2)
  {
    return (paramInt1 & 0x7FFFFFFF) << 32 | 0x8000000000000000 | paramInt2 & 0xFFFFFFFFFFFFFFFF;
  }
  
  public static long getPackedPositionForGroup(int paramInt)
  {
    return (paramInt & 0x7FFFFFFF) << 32;
  }
  
  public static int getPackedPositionGroup(long paramLong)
  {
    if (paramLong == 4294967295L) {
      return -1;
    }
    return (int)((0x7FFFFFFF00000000 & paramLong) >> 32);
  }
  
  public static int getPackedPositionType(long paramLong)
  {
    if (paramLong == 4294967295L) {
      return 2;
    }
    int i;
    if ((paramLong & 0x8000000000000000) == Long.MIN_VALUE) {
      i = 1;
    } else {
      i = 0;
    }
    return i;
  }
  
  private boolean hasRtlSupport()
  {
    return this.mContext.getApplicationInfo().hasRtlSupport();
  }
  
  private boolean isHeaderOrFooterPosition(int paramInt)
  {
    int i = this.mItemCount;
    int j = getFooterViewsCount();
    boolean bool;
    if ((paramInt >= getHeaderViewsCount()) && (paramInt < i - j)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private boolean isRtlCompatibilityMode()
  {
    boolean bool;
    if ((this.mContext.getApplicationInfo().targetSdkVersion >= 17) && (hasRtlSupport())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void resolveChildIndicator()
  {
    int i;
    if (isLayoutRtl())
    {
      i = this.mChildIndicatorStart;
      if (i >= -1) {
        this.mChildIndicatorRight = i;
      }
      i = this.mChildIndicatorEnd;
      if (i >= -1) {
        this.mChildIndicatorLeft = i;
      }
    }
    else
    {
      i = this.mChildIndicatorStart;
      if (i >= -1) {
        this.mChildIndicatorLeft = i;
      }
      i = this.mChildIndicatorEnd;
      if (i >= -1) {
        this.mChildIndicatorRight = i;
      }
    }
  }
  
  private void resolveIndicator()
  {
    int i;
    if (isLayoutRtl())
    {
      i = this.mIndicatorStart;
      if (i >= 0) {
        this.mIndicatorRight = i;
      }
      i = this.mIndicatorEnd;
      if (i >= 0) {
        this.mIndicatorLeft = i;
      }
    }
    else
    {
      i = this.mIndicatorStart;
      if (i >= 0) {
        this.mIndicatorLeft = i;
      }
      i = this.mIndicatorEnd;
      if (i >= 0) {
        this.mIndicatorRight = i;
      }
    }
    if (this.mIndicatorRight == 0)
    {
      Drawable localDrawable = this.mGroupIndicator;
      if (localDrawable != null) {
        this.mIndicatorRight = (this.mIndicatorLeft + localDrawable.getIntrinsicWidth());
      }
    }
  }
  
  public boolean collapseGroup(int paramInt)
  {
    boolean bool = this.mConnector.collapseGroup(paramInt);
    OnGroupCollapseListener localOnGroupCollapseListener = this.mOnGroupCollapseListener;
    if (localOnGroupCollapseListener != null) {
      localOnGroupCollapseListener.onGroupCollapse(paramInt);
    }
    return bool;
  }
  
  ContextMenu.ContextMenuInfo createContextMenuInfo(View paramView, int paramInt, long paramLong)
  {
    if (isHeaderOrFooterPosition(paramInt)) {
      return new AdapterView.AdapterContextMenuInfo(paramView, paramInt, paramLong);
    }
    paramInt = getFlatPositionForConnector(paramInt);
    ExpandableListConnector.PositionMetadata localPositionMetadata = this.mConnector.getUnflattenedPos(paramInt);
    ExpandableListPosition localExpandableListPosition = localPositionMetadata.position;
    long l = getChildOrGroupId(localExpandableListPosition);
    paramLong = localExpandableListPosition.getPackedPosition();
    localPositionMetadata.recycle();
    return new ExpandableListContextMenuInfo(paramView, paramLong, l);
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
    if ((this.mChildIndicator == null) && (this.mGroupIndicator == null)) {
      return;
    }
    int i = 0;
    int j;
    if ((this.mGroupFlags & 0x22) == 34) {
      j = 1;
    } else {
      j = 0;
    }
    if (j != 0)
    {
      i = paramCanvas.save();
      k = this.mScrollX;
      m = this.mScrollY;
      paramCanvas.clipRect(this.mPaddingLeft + k, this.mPaddingTop + m, this.mRight + k - this.mLeft - this.mPaddingRight, this.mBottom + m - this.mTop - this.mPaddingBottom);
    }
    int n = getHeaderViewsCount();
    int i1 = this.mItemCount - getFooterViewsCount() - n - 1;
    int i2 = this.mBottom;
    int k = -4;
    Rect localRect = this.mIndicatorRect;
    int i3 = getChildCount();
    int i4 = 0;
    for (int m = this.mFirstPosition - n; i4 < i3; m++)
    {
      if (m >= 0)
      {
        if (m > i1) {
          break;
        }
        Object localObject = getChildAt(i4);
        int i5 = ((View)localObject).getTop();
        int i6 = ((View)localObject).getBottom();
        if ((i6 >= 0) && (i5 <= i2))
        {
          localObject = this.mConnector.getUnflattenedPos(m);
          boolean bool = isLayoutRtl();
          int i7 = getWidth();
          if (((ExpandableListConnector.PositionMetadata)localObject).position.type != k)
          {
            if (((ExpandableListConnector.PositionMetadata)localObject).position.type == 1)
            {
              k = this.mChildIndicatorLeft;
              if (k == -1) {
                k = this.mIndicatorLeft;
              }
              localRect.left = k;
              k = this.mChildIndicatorRight;
              if (k == -1) {
                k = this.mIndicatorRight;
              }
              localRect.right = k;
            }
            else
            {
              localRect.left = this.mIndicatorLeft;
              localRect.right = this.mIndicatorRight;
            }
            if (bool)
            {
              k = localRect.left;
              localRect.left = (i7 - localRect.right);
              localRect.right = (i7 - k);
              localRect.left -= this.mPaddingRight;
              localRect.right -= this.mPaddingRight;
            }
            else
            {
              localRect.left += this.mPaddingLeft;
              localRect.right += this.mPaddingLeft;
            }
            k = ((ExpandableListConnector.PositionMetadata)localObject).position.type;
          }
          if (localRect.left != localRect.right)
          {
            if (this.mStackFromBottom)
            {
              localRect.top = i5;
              localRect.bottom = i6;
            }
            else
            {
              localRect.top = i5;
              localRect.bottom = i6;
            }
            Drawable localDrawable = getIndicator((ExpandableListConnector.PositionMetadata)localObject);
            if (localDrawable != null)
            {
              localDrawable.setBounds(localRect);
              localDrawable.draw(paramCanvas);
            }
          }
          ((ExpandableListConnector.PositionMetadata)localObject).recycle();
        }
      }
      i4++;
    }
    if (j != 0) {
      paramCanvas.restoreToCount(i);
    }
  }
  
  void drawDivider(Canvas paramCanvas, Rect paramRect, int paramInt)
  {
    paramInt = this.mFirstPosition + paramInt;
    if (paramInt >= 0)
    {
      int i = getFlatPositionForConnector(paramInt);
      ExpandableListConnector.PositionMetadata localPositionMetadata = this.mConnector.getUnflattenedPos(i);
      if ((localPositionMetadata.position.type != 1) && ((!localPositionMetadata.isExpanded()) || (localPositionMetadata.groupMetadata.lastChildFlPos == localPositionMetadata.groupMetadata.flPos)))
      {
        localPositionMetadata.recycle();
      }
      else
      {
        Drawable localDrawable = this.mChildDivider;
        localDrawable.setBounds(paramRect);
        localDrawable.draw(paramCanvas);
        localPositionMetadata.recycle();
        return;
      }
    }
    super.drawDivider(paramCanvas, paramRect, paramInt);
  }
  
  public boolean expandGroup(int paramInt)
  {
    return expandGroup(paramInt, false);
  }
  
  public boolean expandGroup(int paramInt, boolean paramBoolean)
  {
    Object localObject = ExpandableListPosition.obtain(2, paramInt, -1, -1);
    ExpandableListConnector.PositionMetadata localPositionMetadata = this.mConnector.getFlattenedPos((ExpandableListPosition)localObject);
    ((ExpandableListPosition)localObject).recycle();
    boolean bool = this.mConnector.expandGroup(localPositionMetadata);
    localObject = this.mOnGroupExpandListener;
    if (localObject != null) {
      ((OnGroupExpandListener)localObject).onGroupExpand(paramInt);
    }
    if (paramBoolean)
    {
      int i = localPositionMetadata.position.flatListPos;
      i = getHeaderViewsCount() + i;
      smoothScrollToPosition(this.mAdapter.getChildrenCount(paramInt) + i, i);
    }
    localPositionMetadata.recycle();
    return bool;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ExpandableListView.class.getName();
  }
  
  public ListAdapter getAdapter()
  {
    return super.getAdapter();
  }
  
  public ExpandableListAdapter getExpandableListAdapter()
  {
    return this.mAdapter;
  }
  
  public long getExpandableListPosition(int paramInt)
  {
    if (isHeaderOrFooterPosition(paramInt)) {
      return 4294967295L;
    }
    paramInt = getFlatPositionForConnector(paramInt);
    ExpandableListConnector.PositionMetadata localPositionMetadata = this.mConnector.getUnflattenedPos(paramInt);
    long l = localPositionMetadata.position.getPackedPosition();
    localPositionMetadata.recycle();
    return l;
  }
  
  public int getFlatListPosition(long paramLong)
  {
    ExpandableListPosition localExpandableListPosition = ExpandableListPosition.obtainPosition(paramLong);
    ExpandableListConnector.PositionMetadata localPositionMetadata = this.mConnector.getFlattenedPos(localExpandableListPosition);
    localExpandableListPosition.recycle();
    int i = localPositionMetadata.position.flatListPos;
    localPositionMetadata.recycle();
    return getAbsoluteFlatPosition(i);
  }
  
  public long getSelectedId()
  {
    long l = getSelectedPosition();
    if (l == 4294967295L) {
      return -1L;
    }
    int i = getPackedPositionGroup(l);
    if (getPackedPositionType(l) == 0) {
      return this.mAdapter.getGroupId(i);
    }
    return this.mAdapter.getChildId(i, getPackedPositionChild(l));
  }
  
  public long getSelectedPosition()
  {
    return getExpandableListPosition(getSelectedItemPosition());
  }
  
  boolean handleItemClick(View paramView, int paramInt, long paramLong)
  {
    ExpandableListConnector.PositionMetadata localPositionMetadata = this.mConnector.getUnflattenedPos(paramInt);
    paramLong = getChildOrGroupId(localPositionMetadata.position);
    boolean bool;
    if (localPositionMetadata.position.type == 2)
    {
      OnGroupClickListener localOnGroupClickListener = this.mOnGroupClickListener;
      if ((localOnGroupClickListener != null) && (localOnGroupClickListener.onGroupClick(this, paramView, localPositionMetadata.position.groupPos, paramLong)))
      {
        localPositionMetadata.recycle();
        return true;
      }
      if (localPositionMetadata.isExpanded())
      {
        this.mConnector.collapseGroup(localPositionMetadata);
        playSoundEffect(0);
        paramView = this.mOnGroupCollapseListener;
        if (paramView != null) {
          paramView.onGroupCollapse(localPositionMetadata.position.groupPos);
        }
      }
      else
      {
        this.mConnector.expandGroup(localPositionMetadata);
        playSoundEffect(0);
        paramView = this.mOnGroupExpandListener;
        if (paramView != null) {
          paramView.onGroupExpand(localPositionMetadata.position.groupPos);
        }
        paramInt = localPositionMetadata.position.groupPos;
        int i = localPositionMetadata.position.flatListPos;
        i = getHeaderViewsCount() + i;
        smoothScrollToPosition(this.mAdapter.getChildrenCount(paramInt) + i, i);
      }
      bool = true;
    }
    else
    {
      if (this.mOnChildClickListener != null)
      {
        playSoundEffect(0);
        return this.mOnChildClickListener.onChildClick(this, paramView, localPositionMetadata.position.groupPos, localPositionMetadata.position.childPos, paramLong);
      }
      bool = false;
    }
    localPositionMetadata.recycle();
    return bool;
  }
  
  public boolean isGroupExpanded(int paramInt)
  {
    return this.mConnector.isGroupExpanded(paramInt);
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if ((this.mConnector != null) && (paramParcelable.expandedGroupMetadataList != null)) {
      this.mConnector.setExpandedGroupMetadataList(paramParcelable.expandedGroupMetadataList);
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    resolveIndicator();
    resolveChildIndicator();
  }
  
  public Parcelable onSaveInstanceState()
  {
    Parcelable localParcelable = super.onSaveInstanceState();
    Object localObject = this.mConnector;
    if (localObject != null) {
      localObject = ((ExpandableListConnector)localObject).getExpandedGroupMetadataList();
    } else {
      localObject = null;
    }
    return new SavedState(localParcelable, (ArrayList)localObject);
  }
  
  public boolean performItemClick(View paramView, int paramInt, long paramLong)
  {
    if (isHeaderOrFooterPosition(paramInt)) {
      return super.performItemClick(paramView, paramInt, paramLong);
    }
    return handleItemClick(paramView, getFlatPositionForConnector(paramInt), paramLong);
  }
  
  public void setAdapter(ExpandableListAdapter paramExpandableListAdapter)
  {
    this.mAdapter = paramExpandableListAdapter;
    if (paramExpandableListAdapter != null) {
      this.mConnector = new ExpandableListConnector(paramExpandableListAdapter);
    } else {
      this.mConnector = null;
    }
    super.setAdapter(this.mConnector);
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    throw new RuntimeException("For ExpandableListView, use setAdapter(ExpandableListAdapter) instead of setAdapter(ListAdapter)");
  }
  
  public void setChildDivider(Drawable paramDrawable)
  {
    this.mChildDivider = paramDrawable;
  }
  
  public void setChildIndicator(Drawable paramDrawable)
  {
    this.mChildIndicator = paramDrawable;
  }
  
  public void setChildIndicatorBounds(int paramInt1, int paramInt2)
  {
    this.mChildIndicatorLeft = paramInt1;
    this.mChildIndicatorRight = paramInt2;
    resolveChildIndicator();
  }
  
  public void setChildIndicatorBoundsRelative(int paramInt1, int paramInt2)
  {
    this.mChildIndicatorStart = paramInt1;
    this.mChildIndicatorEnd = paramInt2;
    resolveChildIndicator();
  }
  
  public void setGroupIndicator(Drawable paramDrawable)
  {
    this.mGroupIndicator = paramDrawable;
    if (this.mIndicatorRight == 0)
    {
      paramDrawable = this.mGroupIndicator;
      if (paramDrawable != null) {
        this.mIndicatorRight = (this.mIndicatorLeft + paramDrawable.getIntrinsicWidth());
      }
    }
  }
  
  public void setIndicatorBounds(int paramInt1, int paramInt2)
  {
    this.mIndicatorLeft = paramInt1;
    this.mIndicatorRight = paramInt2;
    resolveIndicator();
  }
  
  public void setIndicatorBoundsRelative(int paramInt1, int paramInt2)
  {
    this.mIndicatorStart = paramInt1;
    this.mIndicatorEnd = paramInt2;
    resolveIndicator();
  }
  
  public void setOnChildClickListener(OnChildClickListener paramOnChildClickListener)
  {
    this.mOnChildClickListener = paramOnChildClickListener;
  }
  
  public void setOnGroupClickListener(OnGroupClickListener paramOnGroupClickListener)
  {
    this.mOnGroupClickListener = paramOnGroupClickListener;
  }
  
  public void setOnGroupCollapseListener(OnGroupCollapseListener paramOnGroupCollapseListener)
  {
    this.mOnGroupCollapseListener = paramOnGroupCollapseListener;
  }
  
  public void setOnGroupExpandListener(OnGroupExpandListener paramOnGroupExpandListener)
  {
    this.mOnGroupExpandListener = paramOnGroupExpandListener;
  }
  
  public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    super.setOnItemClickListener(paramOnItemClickListener);
  }
  
  public boolean setSelectedChild(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    ExpandableListPosition localExpandableListPosition = ExpandableListPosition.obtainChildPosition(paramInt1, paramInt2);
    ExpandableListConnector.PositionMetadata localPositionMetadata1 = this.mConnector.getFlattenedPos(localExpandableListPosition);
    ExpandableListConnector.PositionMetadata localPositionMetadata2 = localPositionMetadata1;
    if (localPositionMetadata1 == null)
    {
      if (!paramBoolean) {
        return false;
      }
      expandGroup(paramInt1);
      localPositionMetadata2 = this.mConnector.getFlattenedPos(localExpandableListPosition);
      if (localPositionMetadata2 == null) {
        throw new IllegalStateException("Could not find child");
      }
    }
    super.setSelection(getAbsoluteFlatPosition(localPositionMetadata2.position.flatListPos));
    localExpandableListPosition.recycle();
    localPositionMetadata2.recycle();
    return true;
  }
  
  public void setSelectedGroup(int paramInt)
  {
    ExpandableListPosition localExpandableListPosition = ExpandableListPosition.obtainGroupPosition(paramInt);
    ExpandableListConnector.PositionMetadata localPositionMetadata = this.mConnector.getFlattenedPos(localExpandableListPosition);
    localExpandableListPosition.recycle();
    super.setSelection(getAbsoluteFlatPosition(localPositionMetadata.position.flatListPos));
    localPositionMetadata.recycle();
  }
  
  public static class ExpandableListContextMenuInfo
    implements ContextMenu.ContextMenuInfo
  {
    public long id;
    public long packedPosition;
    public View targetView;
    
    public ExpandableListContextMenuInfo(View paramView, long paramLong1, long paramLong2)
    {
      this.targetView = paramView;
      this.packedPosition = paramLong1;
      this.id = paramLong2;
    }
  }
  
  public static abstract interface OnChildClickListener
  {
    public abstract boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong);
  }
  
  public static abstract interface OnGroupClickListener
  {
    public abstract boolean onGroupClick(ExpandableListView paramExpandableListView, View paramView, int paramInt, long paramLong);
  }
  
  public static abstract interface OnGroupCollapseListener
  {
    public abstract void onGroupCollapse(int paramInt);
  }
  
  public static abstract interface OnGroupExpandListener
  {
    public abstract void onGroupExpand(int paramInt);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ExpandableListView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ExpandableListView.SavedState(paramAnonymousParcel, null);
      }
      
      public ExpandableListView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ExpandableListView.SavedState[paramAnonymousInt];
      }
    };
    ArrayList<ExpandableListConnector.GroupMetadata> expandedGroupMetadataList;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.expandedGroupMetadataList = new ArrayList();
      paramParcel.readList(this.expandedGroupMetadataList, ExpandableListConnector.class.getClassLoader());
    }
    
    SavedState(Parcelable paramParcelable, ArrayList<ExpandableListConnector.GroupMetadata> paramArrayList)
    {
      super();
      this.expandedGroupMetadataList = paramArrayList;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeList(this.expandedGroupMetadataList);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ExpandableListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */