package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.CapturedViewProperty;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewHierarchyEncoder;
import android.view.ViewRootImpl;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.autofill.AutofillManager;

public abstract class AdapterView<T extends Adapter>
  extends ViewGroup
{
  public static final int INVALID_POSITION = -1;
  public static final long INVALID_ROW_ID = Long.MIN_VALUE;
  public static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = -2;
  public static final int ITEM_VIEW_TYPE_IGNORE = -1;
  static final int SYNC_FIRST_POSITION = 1;
  static final int SYNC_MAX_DURATION_MILLIS = 100;
  static final int SYNC_SELECTED_POSITION = 0;
  boolean mBlockLayoutRequests = false;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768524L)
  boolean mDataChanged;
  private boolean mDesiredFocusableInTouchModeState;
  private int mDesiredFocusableState = 16;
  private View mEmptyView;
  @ViewDebug.ExportedProperty(category="scrolling")
  @UnsupportedAppUsage
  int mFirstPosition = 0;
  boolean mInLayout = false;
  @ViewDebug.ExportedProperty(category="list")
  int mItemCount;
  private int mLayoutHeight;
  @UnsupportedAppUsage
  boolean mNeedSync = false;
  @ViewDebug.ExportedProperty(category="list")
  @UnsupportedAppUsage
  int mNextSelectedPosition = -1;
  @UnsupportedAppUsage
  long mNextSelectedRowId = Long.MIN_VALUE;
  int mOldItemCount;
  @UnsupportedAppUsage
  int mOldSelectedPosition = -1;
  long mOldSelectedRowId = Long.MIN_VALUE;
  @UnsupportedAppUsage
  OnItemClickListener mOnItemClickListener;
  OnItemLongClickListener mOnItemLongClickListener;
  @UnsupportedAppUsage
  OnItemSelectedListener mOnItemSelectedListener;
  private AdapterView<T>.SelectionNotifier mPendingSelectionNotifier;
  @ViewDebug.ExportedProperty(category="list")
  @UnsupportedAppUsage
  int mSelectedPosition = -1;
  long mSelectedRowId = Long.MIN_VALUE;
  private AdapterView<T>.SelectionNotifier mSelectionNotifier;
  int mSpecificTop;
  long mSyncHeight;
  int mSyncMode;
  @UnsupportedAppUsage
  int mSyncPosition;
  long mSyncRowId = Long.MIN_VALUE;
  
  public AdapterView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AdapterView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AdapterView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AdapterView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    if (getImportantForAccessibility() == 0) {
      setImportantForAccessibility(1);
    }
    this.mDesiredFocusableState = getFocusable();
    if (this.mDesiredFocusableState == 16) {
      super.setFocusable(0);
    }
  }
  
  private void dispatchOnItemSelected()
  {
    fireOnSelected();
    performAccessibilityActionsOnSelected();
  }
  
  private void fireOnSelected()
  {
    if (this.mOnItemSelectedListener == null) {
      return;
    }
    int i = getSelectedItemPosition();
    if (i >= 0)
    {
      View localView = getSelectedView();
      this.mOnItemSelectedListener.onItemSelected(this, localView, i, getAdapter().getItemId(i));
    }
    else
    {
      this.mOnItemSelectedListener.onNothingSelected(this);
    }
  }
  
  private boolean isScrollableForAccessibility()
  {
    Adapter localAdapter = getAdapter();
    boolean bool = false;
    if (localAdapter != null)
    {
      int i = localAdapter.getCount();
      if ((i > 0) && ((getFirstVisiblePosition() > 0) || (getLastVisiblePosition() < i - 1))) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  private void performAccessibilityActionsOnSelected()
  {
    if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
      return;
    }
    if (getSelectedItemPosition() >= 0) {
      sendAccessibilityEvent(4);
    }
  }
  
  private void updateEmptyStatus(boolean paramBoolean)
  {
    if (isInFilterMode()) {
      paramBoolean = false;
    }
    View localView;
    if (paramBoolean)
    {
      localView = this.mEmptyView;
      if (localView != null)
      {
        localView.setVisibility(0);
        setVisibility(8);
      }
      else
      {
        setVisibility(0);
      }
      if (this.mDataChanged) {
        onLayout(false, this.mLeft, this.mTop, this.mRight, this.mBottom);
      }
    }
    else
    {
      localView = this.mEmptyView;
      if (localView != null) {
        localView.setVisibility(8);
      }
      setVisibility(0);
    }
  }
  
  public void addView(View paramView)
  {
    throw new UnsupportedOperationException("addView(View) is not supported in AdapterView");
  }
  
  public void addView(View paramView, int paramInt)
  {
    throw new UnsupportedOperationException("addView(View, int) is not supported in AdapterView");
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    throw new UnsupportedOperationException("addView(View, int, LayoutParams) is not supported in AdapterView");
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    throw new UnsupportedOperationException("addView(View, LayoutParams) is not supported in AdapterView");
  }
  
  protected boolean canAnimate()
  {
    boolean bool;
    if ((super.canAnimate()) && (this.mItemCount > 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  void checkFocus()
  {
    Adapter localAdapter = getAdapter();
    boolean bool1 = true;
    int i;
    if ((localAdapter != null) && (localAdapter.getCount() != 0)) {
      i = 0;
    } else {
      i = 1;
    }
    if ((i != 0) && (!isInFilterMode())) {
      i = 0;
    } else {
      i = 1;
    }
    boolean bool2;
    if ((i != 0) && (this.mDesiredFocusableInTouchModeState)) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    super.setFocusableInTouchMode(bool2);
    if (i != 0) {
      i = this.mDesiredFocusableState;
    } else {
      i = 0;
    }
    super.setFocusable(i);
    if (this.mEmptyView != null)
    {
      bool2 = bool1;
      if (localAdapter != null) {
        if (localAdapter.isEmpty()) {
          bool2 = bool1;
        } else {
          bool2 = false;
        }
      }
      updateEmptyStatus(bool2);
    }
  }
  
  void checkSelectionChanged()
  {
    if ((this.mSelectedPosition != this.mOldSelectedPosition) || (this.mSelectedRowId != this.mOldSelectedRowId))
    {
      selectionChanged();
      this.mOldSelectedPosition = this.mSelectedPosition;
      this.mOldSelectedRowId = this.mSelectedRowId;
    }
    SelectionNotifier localSelectionNotifier = this.mPendingSelectionNotifier;
    if (localSelectionNotifier != null) {
      localSelectionNotifier.run();
    }
  }
  
  public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    View localView = getSelectedView();
    return (localView != null) && (localView.getVisibility() == 0) && (localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent));
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchThawSelfOnly(paramSparseArray);
  }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchFreezeSelfOnly(paramSparseArray);
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("scrolling:firstPosition", this.mFirstPosition);
    paramViewHierarchyEncoder.addProperty("list:nextSelectedPosition", this.mNextSelectedPosition);
    paramViewHierarchyEncoder.addProperty("list:nextSelectedRowId", (float)this.mNextSelectedRowId);
    paramViewHierarchyEncoder.addProperty("list:selectedPosition", this.mSelectedPosition);
    paramViewHierarchyEncoder.addProperty("list:itemCount", this.mItemCount);
  }
  
  int findSyncPosition()
  {
    int i = this.mItemCount;
    if (i == 0) {
      return -1;
    }
    long l1 = this.mSyncRowId;
    int j = this.mSyncPosition;
    if (l1 == Long.MIN_VALUE) {
      return -1;
    }
    j = Math.min(i - 1, Math.max(0, j));
    long l2 = SystemClock.uptimeMillis();
    int k = j;
    int m = j;
    int n = 0;
    Adapter localAdapter = getAdapter();
    if (localAdapter == null) {
      return -1;
    }
    while (SystemClock.uptimeMillis() <= l2 + 100L)
    {
      if (localAdapter.getItemId(j) == l1) {
        return j;
      }
      int i1 = 1;
      int i2;
      if (m == i - 1) {
        i2 = 1;
      } else {
        i2 = 0;
      }
      if (k != 0) {
        i1 = 0;
      }
      if ((i2 != 0) && (i1 != 0)) {
        break;
      }
      if ((i1 == 0) && ((n == 0) || (i2 != 0)))
      {
        if ((i2 != 0) || ((n == 0) && (i1 == 0)))
        {
          k--;
          j = k;
          n = 1;
        }
      }
      else
      {
        m++;
        j = m;
        n = 0;
      }
    }
    return -1;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return AdapterView.class.getName();
  }
  
  public abstract T getAdapter();
  
  @ViewDebug.CapturedViewProperty
  public int getCount()
  {
    return this.mItemCount;
  }
  
  public View getEmptyView()
  {
    return this.mEmptyView;
  }
  
  public int getFirstVisiblePosition()
  {
    return this.mFirstPosition;
  }
  
  public Object getItemAtPosition(int paramInt)
  {
    Object localObject = getAdapter();
    if ((localObject != null) && (paramInt >= 0)) {
      localObject = ((Adapter)localObject).getItem(paramInt);
    } else {
      localObject = null;
    }
    return localObject;
  }
  
  public long getItemIdAtPosition(int paramInt)
  {
    Adapter localAdapter = getAdapter();
    long l;
    if ((localAdapter != null) && (paramInt >= 0)) {
      l = localAdapter.getItemId(paramInt);
    } else {
      l = Long.MIN_VALUE;
    }
    return l;
  }
  
  public int getLastVisiblePosition()
  {
    return this.mFirstPosition + getChildCount() - 1;
  }
  
  public final OnItemClickListener getOnItemClickListener()
  {
    return this.mOnItemClickListener;
  }
  
  public final OnItemLongClickListener getOnItemLongClickListener()
  {
    return this.mOnItemLongClickListener;
  }
  
  public final OnItemSelectedListener getOnItemSelectedListener()
  {
    return this.mOnItemSelectedListener;
  }
  
  public int getPositionForView(View paramView)
  {
    try
    {
      for (;;)
      {
        View localView = (View)paramView.getParent();
        if (localView == null) {
          break;
        }
        boolean bool = localView.equals(this);
        if (bool) {
          break;
        }
        paramView = localView;
      }
      int i = getChildCount();
      for (int j = 0; j < i; j++) {
        if (getChildAt(j).equals(paramView)) {
          return this.mFirstPosition + j;
        }
      }
      return -1;
    }
    catch (ClassCastException paramView) {}
    return -1;
  }
  
  public Object getSelectedItem()
  {
    Adapter localAdapter = getAdapter();
    int i = getSelectedItemPosition();
    if ((localAdapter != null) && (localAdapter.getCount() > 0) && (i >= 0)) {
      return localAdapter.getItem(i);
    }
    return null;
  }
  
  @ViewDebug.CapturedViewProperty
  public long getSelectedItemId()
  {
    return this.mNextSelectedRowId;
  }
  
  @ViewDebug.CapturedViewProperty
  public int getSelectedItemPosition()
  {
    return this.mNextSelectedPosition;
  }
  
  public abstract View getSelectedView();
  
  void handleDataChanged()
  {
    int i = this.mItemCount;
    int j = 0;
    int k = 0;
    if (i > 0)
    {
      int m = k;
      if (this.mNeedSync)
      {
        this.mNeedSync = false;
        j = findSyncPosition();
        m = k;
        if (j >= 0)
        {
          m = k;
          if (lookForSelectablePosition(j, true) == j)
          {
            setNextSelectedPositionInt(j);
            m = 1;
          }
        }
      }
      j = m;
      if (m == 0)
      {
        k = getSelectedItemPosition();
        j = k;
        if (k >= i) {
          j = i - 1;
        }
        i = j;
        if (j < 0) {
          i = 0;
        }
        j = lookForSelectablePosition(i, true);
        k = j;
        if (j < 0) {
          k = lookForSelectablePosition(i, false);
        }
        j = m;
        if (k >= 0)
        {
          setNextSelectedPositionInt(k);
          checkSelectionChanged();
          j = 1;
        }
      }
    }
    if (j == 0)
    {
      this.mSelectedPosition = -1;
      this.mSelectedRowId = Long.MIN_VALUE;
      this.mNextSelectedPosition = -1;
      this.mNextSelectedRowId = Long.MIN_VALUE;
      this.mNeedSync = false;
      checkSelectionChanged();
    }
    notifySubtreeAccessibilityStateChangedIfNeeded();
  }
  
  boolean isInFilterMode()
  {
    return false;
  }
  
  int lookForSelectablePosition(int paramInt, boolean paramBoolean)
  {
    return paramInt;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeCallbacks(this.mSelectionNotifier);
  }
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    paramAccessibilityEvent.setScrollable(isScrollableForAccessibility());
    View localView = getSelectedView();
    if (localView != null) {
      paramAccessibilityEvent.setEnabled(localView.isEnabled());
    }
    paramAccessibilityEvent.setCurrentItemIndex(getSelectedItemPosition());
    paramAccessibilityEvent.setFromIndex(getFirstVisiblePosition());
    paramAccessibilityEvent.setToIndex(getLastVisiblePosition());
    paramAccessibilityEvent.setItemCount(getCount());
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setScrollable(isScrollableForAccessibility());
    View localView = getSelectedView();
    if (localView != null) {
      paramAccessibilityNodeInfo.setEnabled(localView.isEnabled());
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mLayoutHeight = getHeight();
  }
  
  public void onProvideAutofillStructure(ViewStructure paramViewStructure, int paramInt)
  {
    super.onProvideAutofillStructure(paramViewStructure, paramInt);
  }
  
  protected void onProvideStructure(ViewStructure paramViewStructure, int paramInt1, int paramInt2)
  {
    super.onProvideStructure(paramViewStructure, paramInt1, paramInt2);
    if (paramInt1 == 1)
    {
      Object localObject = getAdapter();
      if (localObject == null) {
        return;
      }
      localObject = ((Adapter)localObject).getAutofillOptions();
      if (localObject != null) {
        paramViewStructure.setAutofillOptions((CharSequence[])localObject);
      }
    }
  }
  
  public boolean onRequestSendAccessibilityEventInternal(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    if (super.onRequestSendAccessibilityEventInternal(paramView, paramAccessibilityEvent))
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
      onInitializeAccessibilityEvent(localAccessibilityEvent);
      paramView.dispatchPopulateAccessibilityEvent(localAccessibilityEvent);
      paramAccessibilityEvent.appendRecord(localAccessibilityEvent);
      return true;
    }
    return false;
  }
  
  public boolean performItemClick(View paramView, int paramInt, long paramLong)
  {
    boolean bool;
    if (this.mOnItemClickListener != null)
    {
      playSoundEffect(0);
      this.mOnItemClickListener.onItemClick(this, paramView, paramInt, paramLong);
      bool = true;
    }
    else
    {
      bool = false;
    }
    if (paramView != null) {
      paramView.sendAccessibilityEvent(1);
    }
    return bool;
  }
  
  void rememberSyncState()
  {
    if (getChildCount() > 0)
    {
      this.mNeedSync = true;
      this.mSyncHeight = this.mLayoutHeight;
      int i = this.mSelectedPosition;
      Object localObject;
      if (i >= 0)
      {
        localObject = getChildAt(i - this.mFirstPosition);
        this.mSyncRowId = this.mNextSelectedRowId;
        this.mSyncPosition = this.mNextSelectedPosition;
        if (localObject != null) {
          this.mSpecificTop = ((View)localObject).getTop();
        }
        this.mSyncMode = 0;
      }
      else
      {
        View localView = getChildAt(0);
        localObject = getAdapter();
        i = this.mFirstPosition;
        if ((i >= 0) && (i < ((Adapter)localObject).getCount())) {
          this.mSyncRowId = ((Adapter)localObject).getItemId(this.mFirstPosition);
        } else {
          this.mSyncRowId = -1L;
        }
        this.mSyncPosition = this.mFirstPosition;
        if (localView != null) {
          this.mSpecificTop = localView.getTop();
        }
        this.mSyncMode = 1;
      }
    }
  }
  
  public void removeAllViews()
  {
    throw new UnsupportedOperationException("removeAllViews() is not supported in AdapterView");
  }
  
  public void removeView(View paramView)
  {
    throw new UnsupportedOperationException("removeView(View) is not supported in AdapterView");
  }
  
  public void removeViewAt(int paramInt)
  {
    throw new UnsupportedOperationException("removeViewAt(int) is not supported in AdapterView");
  }
  
  @UnsupportedAppUsage
  void selectionChanged()
  {
    this.mPendingSelectionNotifier = null;
    if ((this.mOnItemSelectedListener != null) || (AccessibilityManager.getInstance(this.mContext).isEnabled())) {
      if ((!this.mInLayout) && (!this.mBlockLayoutRequests))
      {
        dispatchOnItemSelected();
      }
      else
      {
        localObject = this.mSelectionNotifier;
        if (localObject == null) {
          this.mSelectionNotifier = new SelectionNotifier(null);
        } else {
          removeCallbacks((Runnable)localObject);
        }
        post(this.mSelectionNotifier);
      }
    }
    Object localObject = (AutofillManager)this.mContext.getSystemService(AutofillManager.class);
    if (localObject != null) {
      ((AutofillManager)localObject).notifyValueChanged(this);
    }
  }
  
  public abstract void setAdapter(T paramT);
  
  @RemotableViewMethod
  public void setEmptyView(View paramView)
  {
    this.mEmptyView = paramView;
    boolean bool1 = true;
    if ((paramView != null) && (paramView.getImportantForAccessibility() == 0)) {
      paramView.setImportantForAccessibility(1);
    }
    paramView = getAdapter();
    boolean bool2 = bool1;
    if (paramView != null) {
      if (paramView.isEmpty()) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
    }
    updateEmptyStatus(bool2);
  }
  
  public void setFocusable(int paramInt)
  {
    Adapter localAdapter = getAdapter();
    int i = 0;
    int j;
    if ((localAdapter != null) && (localAdapter.getCount() != 0)) {
      j = 0;
    } else {
      j = 1;
    }
    this.mDesiredFocusableState = paramInt;
    if ((paramInt & 0x11) == 0) {
      this.mDesiredFocusableInTouchModeState = false;
    }
    if (j != 0)
    {
      j = i;
      if (!isInFilterMode()) {}
    }
    else
    {
      j = paramInt;
    }
    super.setFocusable(j);
  }
  
  public void setFocusableInTouchMode(boolean paramBoolean)
  {
    Adapter localAdapter = getAdapter();
    boolean bool1 = false;
    int i;
    if ((localAdapter != null) && (localAdapter.getCount() != 0)) {
      i = 0;
    } else {
      i = 1;
    }
    this.mDesiredFocusableInTouchModeState = paramBoolean;
    if (paramBoolean) {
      this.mDesiredFocusableState = 1;
    }
    boolean bool2 = bool1;
    if (paramBoolean) {
      if (i != 0)
      {
        bool2 = bool1;
        if (!isInFilterMode()) {}
      }
      else
      {
        bool2 = true;
      }
    }
    super.setFocusableInTouchMode(bool2);
  }
  
  @UnsupportedAppUsage
  void setNextSelectedPositionInt(int paramInt)
  {
    this.mNextSelectedPosition = paramInt;
    this.mNextSelectedRowId = getItemIdAtPosition(paramInt);
    if ((this.mNeedSync) && (this.mSyncMode == 0) && (paramInt >= 0))
    {
      this.mSyncPosition = paramInt;
      this.mSyncRowId = this.mNextSelectedRowId;
    }
  }
  
  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    throw new RuntimeException("Don't call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead");
  }
  
  public void setOnItemClickListener(OnItemClickListener paramOnItemClickListener)
  {
    this.mOnItemClickListener = paramOnItemClickListener;
  }
  
  public void setOnItemLongClickListener(OnItemLongClickListener paramOnItemLongClickListener)
  {
    if (!isLongClickable()) {
      setLongClickable(true);
    }
    this.mOnItemLongClickListener = paramOnItemLongClickListener;
  }
  
  public void setOnItemSelectedListener(OnItemSelectedListener paramOnItemSelectedListener)
  {
    this.mOnItemSelectedListener = paramOnItemSelectedListener;
  }
  
  @UnsupportedAppUsage
  void setSelectedPositionInt(int paramInt)
  {
    this.mSelectedPosition = paramInt;
    this.mSelectedRowId = getItemIdAtPosition(paramInt);
  }
  
  public abstract void setSelection(int paramInt);
  
  public static class AdapterContextMenuInfo
    implements ContextMenu.ContextMenuInfo
  {
    public long id;
    public int position;
    public View targetView;
    
    public AdapterContextMenuInfo(View paramView, int paramInt, long paramLong)
    {
      this.targetView = paramView;
      this.position = paramInt;
      this.id = paramLong;
    }
  }
  
  class AdapterDataSetObserver
    extends DataSetObserver
  {
    private Parcelable mInstanceState = null;
    
    AdapterDataSetObserver() {}
    
    public void clearSavedState()
    {
      this.mInstanceState = null;
    }
    
    public void onChanged()
    {
      AdapterView localAdapterView = AdapterView.this;
      localAdapterView.mDataChanged = true;
      localAdapterView.mOldItemCount = localAdapterView.mItemCount;
      localAdapterView = AdapterView.this;
      localAdapterView.mItemCount = localAdapterView.getAdapter().getCount();
      if ((AdapterView.this.getAdapter().hasStableIds()) && (this.mInstanceState != null) && (AdapterView.this.mOldItemCount == 0) && (AdapterView.this.mItemCount > 0))
      {
        AdapterView.this.onRestoreInstanceState(this.mInstanceState);
        this.mInstanceState = null;
      }
      else
      {
        AdapterView.this.rememberSyncState();
      }
      AdapterView.this.checkFocus();
      AdapterView.this.requestLayout();
    }
    
    public void onInvalidated()
    {
      AdapterView localAdapterView = AdapterView.this;
      localAdapterView.mDataChanged = true;
      if (localAdapterView.getAdapter().hasStableIds()) {
        this.mInstanceState = AdapterView.this.onSaveInstanceState();
      }
      localAdapterView = AdapterView.this;
      localAdapterView.mOldItemCount = localAdapterView.mItemCount;
      localAdapterView = AdapterView.this;
      localAdapterView.mItemCount = 0;
      localAdapterView.mSelectedPosition = -1;
      localAdapterView.mSelectedRowId = Long.MIN_VALUE;
      localAdapterView.mNextSelectedPosition = -1;
      localAdapterView.mNextSelectedRowId = Long.MIN_VALUE;
      localAdapterView.mNeedSync = false;
      localAdapterView.checkFocus();
      AdapterView.this.requestLayout();
    }
  }
  
  public static abstract interface OnItemClickListener
  {
    public abstract void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
  }
  
  public static abstract interface OnItemLongClickListener
  {
    public abstract boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
  }
  
  public static abstract interface OnItemSelectedListener
  {
    public abstract void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong);
    
    public abstract void onNothingSelected(AdapterView<?> paramAdapterView);
  }
  
  private class SelectionNotifier
    implements Runnable
  {
    private SelectionNotifier() {}
    
    public void run()
    {
      AdapterView.access$202(AdapterView.this, null);
      if ((AdapterView.this.mDataChanged) && (AdapterView.this.getViewRootImpl() != null) && (AdapterView.this.getViewRootImpl().isLayoutRequested()))
      {
        if (AdapterView.this.getAdapter() != null) {
          AdapterView.access$202(AdapterView.this, this);
        }
      }
      else {
        AdapterView.this.dispatchOnItemSelected();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AdapterView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */