package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.util.SparseBooleanArray;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.RemotableViewMethod;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewHierarchyEncoder;
import android.view.ViewRootImpl;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import com.android.internal.R.styleable;
import com.google.android.collect.Lists;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_Widget_ListView.Extension;
import com.miui.internal.variable.api.v29.Android_Widget_ListView.Interface;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@RemoteViews.RemoteView
public class ListView
  extends AbsListView
{
  private static final float MAX_SCROLL_FACTOR = 0.33F;
  private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;
  static final int NO_POSITION = -1;
  static final String TAG = "ListView";
  @UnsupportedAppUsage
  private boolean mAreAllItemsSelectable = true;
  private final ArrowScrollFocusResult mArrowScrollFocusResult = new ArrowScrollFocusResult(null);
  @UnsupportedAppUsage
  Drawable mDivider;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  int mDividerHeight;
  private boolean mDividerIsOpaque;
  private Paint mDividerPaint;
  private FocusSelector mFocusSelector;
  private boolean mFooterDividersEnabled;
  @UnsupportedAppUsage
  ArrayList<FixedViewInfo> mFooterViewInfos = Lists.newArrayList();
  private boolean mHeaderDividersEnabled;
  @UnsupportedAppUsage
  ArrayList<FixedViewInfo> mHeaderViewInfos = Lists.newArrayList();
  private boolean mIsCacheColorOpaque;
  private boolean mItemsCanFocus = false;
  Drawable mOverScrollFooter;
  Drawable mOverScrollHeader;
  private final Rect mTempRect = new Rect();
  
  static
  {
    Android_Widget_ListView.Extension.get().bindOriginal(new Android_Widget_ListView.Interface()
    {
      public void fillGap(ListView paramAnonymousListView, boolean paramAnonymousBoolean)
      {
        paramAnonymousListView.originalFillGap(paramAnonymousBoolean);
      }
      
      public void init(ListView paramAnonymousListView, Context paramAnonymousContext, AttributeSet paramAnonymousAttributeSet, int paramAnonymousInt1, int paramAnonymousInt2) {}
      
      public void layoutChildren(ListView paramAnonymousListView)
      {
        paramAnonymousListView.originalLayoutChildren();
      }
    });
  }
  
  public ListView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842868);
  }
  
  public ListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.ListView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    Object localObject = localTypedArray.getTextArray(0);
    if (localObject != null) {
      setAdapter(new ArrayAdapter(paramContext, 17367043, (Object[])localObject));
    }
    localObject = localTypedArray.getDrawable(1);
    if (localObject != null) {
      setDivider((Drawable)localObject);
    }
    localObject = localTypedArray.getDrawable(5);
    if (localObject != null) {
      setOverscrollHeader((Drawable)localObject);
    }
    localObject = localTypedArray.getDrawable(6);
    if (localObject != null) {
      setOverscrollFooter((Drawable)localObject);
    }
    if (localTypedArray.hasValueOrEmpty(2))
    {
      int i = localTypedArray.getDimensionPixelSize(2, 0);
      if (i != 0) {
        setDividerHeight(i);
      }
    }
    this.mHeaderDividersEnabled = localTypedArray.getBoolean(3, true);
    this.mFooterDividersEnabled = localTypedArray.getBoolean(4, true);
    localTypedArray.recycle();
    if (Android_Widget_ListView.Extension.get().getExtension() != null) {
      ((Android_Widget_ListView.Interface)Android_Widget_ListView.Extension.get().getExtension().asInterface()).init(this, paramContext, paramAttributeSet, paramInt1, paramInt2);
    }
  }
  
  private View addViewAbove(View paramView, int paramInt)
  {
    paramInt--;
    View localView = obtainView(paramInt, this.mIsScrap);
    setupChild(localView, paramInt, paramView.getTop() - this.mDividerHeight, false, this.mListPadding.left, false, this.mIsScrap[0]);
    return localView;
  }
  
  private View addViewBelow(View paramView, int paramInt)
  {
    paramInt++;
    View localView = obtainView(paramInt, this.mIsScrap);
    setupChild(localView, paramInt, paramView.getBottom() + this.mDividerHeight, true, this.mListPadding.left, false, this.mIsScrap[0]);
    return localView;
  }
  
  private void adjustViewsUpOrDown()
  {
    int i = getChildCount();
    if (i > 0)
    {
      int j;
      int k;
      if (!this.mStackFromBottom)
      {
        j = getChildAt(0).getTop() - this.mListPadding.top;
        k = j;
        if (this.mFirstPosition != 0) {
          k = j - this.mDividerHeight;
        }
        j = k;
        if (k < 0) {
          j = 0;
        }
      }
      else
      {
        j = getChildAt(i - 1).getBottom() - (getHeight() - this.mListPadding.bottom);
        k = j;
        if (this.mFirstPosition + i < this.mItemCount) {
          k = j + this.mDividerHeight;
        }
        j = k;
        if (k > 0) {
          j = 0;
        }
      }
      if (j != 0) {
        offsetChildrenTopAndBottom(-j);
      }
    }
  }
  
  private int amountToScroll(int paramInt1, int paramInt2)
  {
    int i = getHeight() - this.mListPadding.bottom;
    int j = this.mListPadding.top;
    int k = getChildCount();
    if (paramInt1 == 130)
    {
      m = k - 1;
      paramInt1 = k;
      if (paramInt2 != -1) {
        m = paramInt2 - this.mFirstPosition;
      }
      for (paramInt1 = k; paramInt1 <= m; paramInt1++) {
        addViewBelow(getChildAt(paramInt1 - 1), this.mFirstPosition + paramInt1 - 1);
      }
      int n = this.mFirstPosition;
      localView = getChildAt(m);
      j = i;
      k = j;
      if (n + m < this.mItemCount - 1) {
        k = j - getArrowScrollPreviewLength();
      }
      if (localView.getBottom() <= k) {
        return 0;
      }
      if ((paramInt2 != -1) && (k - localView.getTop() >= getMaxScrollAmount())) {
        return 0;
      }
      m = localView.getBottom() - k;
      paramInt2 = m;
      if (this.mFirstPosition + paramInt1 == this.mItemCount) {
        paramInt2 = Math.min(m, getChildAt(paramInt1 - 1).getBottom() - i);
      }
      return Math.min(paramInt2, getMaxScrollAmount());
    }
    paramInt1 = 0;
    if (paramInt2 != -1) {}
    for (paramInt1 = paramInt2 - this.mFirstPosition; paramInt1 < 0; paramInt1 = paramInt2 - this.mFirstPosition)
    {
      addViewAbove(getChildAt(0), this.mFirstPosition);
      this.mFirstPosition -= 1;
    }
    i = this.mFirstPosition;
    View localView = getChildAt(paramInt1);
    k = j;
    int m = k;
    if (i + paramInt1 > 0) {
      m = k + getArrowScrollPreviewLength();
    }
    if (localView.getTop() >= m) {
      return 0;
    }
    if ((paramInt2 != -1) && (localView.getBottom() - m >= getMaxScrollAmount())) {
      return 0;
    }
    paramInt2 = m - localView.getTop();
    paramInt1 = paramInt2;
    if (this.mFirstPosition == 0) {
      paramInt1 = Math.min(paramInt2, j - getChildAt(0).getTop());
    }
    return Math.min(paramInt1, getMaxScrollAmount());
  }
  
  private int amountToScrollToNewFocus(int paramInt1, View paramView, int paramInt2)
  {
    int i = 0;
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    if (paramInt1 == 33)
    {
      paramInt1 = i;
      if (this.mTempRect.top < this.mListPadding.top)
      {
        i = this.mListPadding.top - this.mTempRect.top;
        paramInt1 = i;
        if (paramInt2 > 0) {
          paramInt1 = i + getArrowScrollPreviewLength();
        }
      }
    }
    else
    {
      int j = getHeight() - this.mListPadding.bottom;
      paramInt1 = i;
      if (this.mTempRect.bottom > j)
      {
        i = this.mTempRect.bottom - j;
        paramInt1 = i;
        if (paramInt2 < this.mItemCount - 1) {
          paramInt1 = i + getArrowScrollPreviewLength();
        }
      }
    }
    return paramInt1;
  }
  
  private ArrowScrollFocusResult arrowScrollFocused(int paramInt)
  {
    View localView = getSelectedView();
    int i;
    int j;
    int k;
    if ((localView != null) && (localView.hasFocus()))
    {
      localView = localView.findFocus();
      localView = FocusFinder.getInstance().findNextFocus(this, localView, paramInt);
    }
    else
    {
      i = 1;
      j = 1;
      if (paramInt == 130)
      {
        if (this.mFirstPosition <= 0) {
          j = 0;
        }
        i = this.mListPadding.top;
        if (j != 0) {
          j = getArrowScrollPreviewLength();
        } else {
          j = 0;
        }
        j = i + j;
        if ((localView != null) && (localView.getTop() > j)) {
          j = localView.getTop();
        }
        this.mTempRect.set(0, j, 0, j);
      }
      else
      {
        if (this.mFirstPosition + getChildCount() - 1 < this.mItemCount) {
          j = i;
        } else {
          j = 0;
        }
        k = getHeight();
        i = this.mListPadding.bottom;
        if (j != 0) {
          j = getArrowScrollPreviewLength();
        } else {
          j = 0;
        }
        j = k - i - j;
        if ((localView != null) && (localView.getBottom() < j)) {
          j = localView.getBottom();
        }
        this.mTempRect.set(0, j, 0, j);
      }
      localView = FocusFinder.getInstance().findNextFocusFromRect(this, this.mTempRect, paramInt);
    }
    if (localView != null)
    {
      j = positionOfNewFocus(localView);
      if ((this.mSelectedPosition != -1) && (j != this.mSelectedPosition))
      {
        i = lookForSelectablePositionOnScreen(paramInt);
        if ((i != -1) && (((paramInt == 130) && (i < j)) || ((paramInt == 33) && (i > j)))) {
          return null;
        }
      }
      k = amountToScrollToNewFocus(paramInt, localView, j);
      i = getMaxScrollAmount();
      if (k < i)
      {
        localView.requestFocus(paramInt);
        this.mArrowScrollFocusResult.populate(j, k);
        return this.mArrowScrollFocusResult;
      }
      if (distanceToView(localView) < i)
      {
        localView.requestFocus(paramInt);
        this.mArrowScrollFocusResult.populate(j, i);
        return this.mArrowScrollFocusResult;
      }
    }
    return null;
  }
  
  private boolean arrowScrollImpl(int paramInt)
  {
    if (getChildCount() <= 0) {
      return false;
    }
    View localView1 = getSelectedView();
    int i = this.mSelectedPosition;
    int j = nextSelectedPositionForDirection(localView1, i, paramInt);
    int k = amountToScroll(paramInt, j);
    if (this.mItemsCanFocus) {
      localObject = arrowScrollFocused(paramInt);
    } else {
      localObject = null;
    }
    if (localObject != null)
    {
      j = ((ArrowScrollFocusResult)localObject).getSelectedPosition();
      k = ((ArrowScrollFocusResult)localObject).getAmountToScroll();
    }
    int m;
    if (localObject != null) {
      m = 1;
    } else {
      m = 0;
    }
    View localView2 = localView1;
    if (j != -1)
    {
      boolean bool;
      if (localObject != null) {
        bool = true;
      } else {
        bool = false;
      }
      handleNewSelectionChange(localView1, paramInt, j, bool);
      setSelectedPositionInt(j);
      setNextSelectedPositionInt(j);
      localView2 = getSelectedView();
      i = j;
      if ((this.mItemsCanFocus) && (localObject == null))
      {
        localView1 = getFocusedChild();
        if (localView1 != null) {
          localView1.clearFocus();
        }
      }
      m = 1;
      checkSelectionChanged();
    }
    if (k > 0)
    {
      if (paramInt == 33) {
        paramInt = k;
      } else {
        paramInt = -k;
      }
      scrollListItemsBy(paramInt);
      m = 1;
    }
    if ((this.mItemsCanFocus) && (localObject == null) && (localView2 != null) && (localView2.hasFocus()))
    {
      localObject = localView2.findFocus();
      if ((localObject != null) && ((!isViewAncestorOf((View)localObject, this)) || (distanceToView((View)localObject) > 0))) {
        ((View)localObject).clearFocus();
      }
    }
    Object localObject = localView2;
    if (j == -1)
    {
      localObject = localView2;
      if (localView2 != null)
      {
        localObject = localView2;
        if (!isViewAncestorOf(localView2, this))
        {
          localObject = null;
          hideSelector();
          this.mResurrectToPosition = -1;
        }
      }
    }
    if (m != 0)
    {
      if (localObject != null)
      {
        positionSelectorLikeFocus(i, (View)localObject);
        this.mSelectedTop = ((View)localObject).getTop();
      }
      if (!awakenScrollBars()) {
        invalidate();
      }
      invokeOnItemScrollListener();
      return true;
    }
    return false;
  }
  
  private void clearRecycledState(ArrayList<FixedViewInfo> paramArrayList)
  {
    if (paramArrayList != null)
    {
      int i = paramArrayList.size();
      for (int j = 0; j < i; j++)
      {
        ViewGroup.LayoutParams localLayoutParams = ((FixedViewInfo)paramArrayList.get(j)).view.getLayoutParams();
        if (checkLayoutParams(localLayoutParams)) {
          ((AbsListView.LayoutParams)localLayoutParams).recycledHeaderFooter = false;
        }
      }
    }
  }
  
  private boolean commonKey(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    if ((this.mAdapter != null) && (isAttachedToWindow()))
    {
      if (this.mDataChanged) {
        layoutChildren();
      }
      boolean bool1 = false;
      int i = paramKeyEvent.getAction();
      boolean bool2 = bool1;
      if (KeyEvent.isConfirmKey(paramInt1))
      {
        bool2 = bool1;
        if (paramKeyEvent.hasNoModifiers())
        {
          bool2 = bool1;
          if (i != 1)
          {
            bool1 = resurrectSelectionIfNeeded();
            bool2 = bool1;
            if (!bool1)
            {
              bool2 = bool1;
              if (paramKeyEvent.getRepeatCount() == 0)
              {
                bool2 = bool1;
                if (getChildCount() > 0)
                {
                  keyPressed();
                  bool2 = true;
                }
              }
            }
          }
        }
      }
      bool1 = bool2;
      int j = paramInt2;
      if (!bool2)
      {
        bool1 = bool2;
        j = paramInt2;
        if (i != 1) {
          if (paramInt1 != 61)
          {
            if (paramInt1 != 92)
            {
              if (paramInt1 != 93)
              {
                if (paramInt1 != 122)
                {
                  if (paramInt1 != 123)
                  {
                    int k;
                    switch (paramInt1)
                    {
                    default: 
                      bool1 = bool2;
                      j = paramInt2;
                      break;
                    case 22: 
                      bool1 = bool2;
                      j = paramInt2;
                      if (!paramKeyEvent.hasNoModifiers()) {
                        break;
                      }
                      bool1 = handleHorizontalFocusWithinListItem(66);
                      j = paramInt2;
                      break;
                    case 21: 
                      bool1 = bool2;
                      j = paramInt2;
                      if (!paramKeyEvent.hasNoModifiers()) {
                        break;
                      }
                      bool1 = handleHorizontalFocusWithinListItem(17);
                      j = paramInt2;
                      break;
                    case 20: 
                      if (paramKeyEvent.hasNoModifiers())
                      {
                        bool2 = resurrectSelectionIfNeeded();
                        bool1 = bool2;
                        j = paramInt2;
                        if (!bool2) {
                          for (j = paramInt2;; j = k)
                          {
                            k = j - 1;
                            bool1 = bool2;
                            paramInt2 = k;
                            if (j <= 0) {
                              break;
                            }
                            bool1 = bool2;
                            paramInt2 = k;
                            if (!arrowScroll(130)) {
                              break;
                            }
                            bool2 = true;
                          }
                        }
                      }
                      else
                      {
                        bool1 = bool2;
                        j = paramInt2;
                        if (paramKeyEvent.hasModifiers(2))
                        {
                          if ((!resurrectSelectionIfNeeded()) && (!fullScroll(130))) {
                            bool1 = false;
                          } else {
                            bool1 = true;
                          }
                          j = paramInt2;
                        }
                      }
                      break;
                    case 19: 
                      if (paramKeyEvent.hasNoModifiers())
                      {
                        bool2 = resurrectSelectionIfNeeded();
                        bool1 = bool2;
                        j = paramInt2;
                        if (!bool2)
                        {
                          for (k = paramInt2;; k = j)
                          {
                            j = k - 1;
                            bool1 = bool2;
                            paramInt2 = j;
                            if (k <= 0) {
                              break;
                            }
                            bool1 = bool2;
                            paramInt2 = j;
                            if (!arrowScroll(33)) {
                              break;
                            }
                            bool2 = true;
                          }
                          j = paramInt2;
                        }
                      }
                      else
                      {
                        bool1 = bool2;
                        j = paramInt2;
                        if (paramKeyEvent.hasModifiers(2))
                        {
                          if ((!resurrectSelectionIfNeeded()) && (!fullScroll(33))) {
                            bool1 = false;
                          } else {
                            bool1 = true;
                          }
                          j = paramInt2;
                        }
                      }
                      break;
                    }
                  }
                  else
                  {
                    bool1 = bool2;
                    j = paramInt2;
                    if (paramKeyEvent.hasNoModifiers())
                    {
                      if ((!resurrectSelectionIfNeeded()) && (!fullScroll(130))) {
                        bool1 = false;
                      } else {
                        bool1 = true;
                      }
                      j = paramInt2;
                    }
                  }
                }
                else
                {
                  bool1 = bool2;
                  j = paramInt2;
                  if (paramKeyEvent.hasNoModifiers())
                  {
                    if ((!resurrectSelectionIfNeeded()) && (!fullScroll(33))) {
                      bool1 = false;
                    } else {
                      bool1 = true;
                    }
                    j = paramInt2;
                  }
                }
              }
              else if (paramKeyEvent.hasNoModifiers())
              {
                if ((!resurrectSelectionIfNeeded()) && (!pageScroll(130))) {
                  bool1 = false;
                } else {
                  bool1 = true;
                }
                j = paramInt2;
              }
              else
              {
                bool1 = bool2;
                j = paramInt2;
                if (paramKeyEvent.hasModifiers(2))
                {
                  if ((!resurrectSelectionIfNeeded()) && (!fullScroll(130))) {
                    bool1 = false;
                  } else {
                    bool1 = true;
                  }
                  j = paramInt2;
                }
              }
            }
            else if (paramKeyEvent.hasNoModifiers())
            {
              if ((!resurrectSelectionIfNeeded()) && (!pageScroll(33))) {
                bool1 = false;
              } else {
                bool1 = true;
              }
              j = paramInt2;
            }
            else
            {
              bool1 = bool2;
              j = paramInt2;
              if (paramKeyEvent.hasModifiers(2))
              {
                if ((!resurrectSelectionIfNeeded()) && (!fullScroll(33))) {
                  bool1 = false;
                } else {
                  bool1 = true;
                }
                j = paramInt2;
              }
            }
          }
          else if (paramKeyEvent.hasNoModifiers())
          {
            if ((!resurrectSelectionIfNeeded()) && (!arrowScroll(130))) {
              bool1 = false;
            } else {
              bool1 = true;
            }
            j = paramInt2;
          }
          else
          {
            bool1 = bool2;
            j = paramInt2;
            if (paramKeyEvent.hasModifiers(1))
            {
              if ((!resurrectSelectionIfNeeded()) && (!arrowScroll(33))) {
                bool1 = false;
              } else {
                bool1 = true;
              }
              j = paramInt2;
            }
          }
        }
      }
      if (bool1) {
        return true;
      }
      if (sendToTextFilter(paramInt1, j, paramKeyEvent)) {
        return true;
      }
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2) {
            return false;
          }
          return super.onKeyMultiple(paramInt1, j, paramKeyEvent);
        }
        return super.onKeyUp(paramInt1, paramKeyEvent);
      }
      return super.onKeyDown(paramInt1, paramKeyEvent);
    }
    return false;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private void correctTooHigh(int paramInt)
  {
    if ((this.mFirstPosition + paramInt - 1 == this.mItemCount - 1) && (paramInt > 0))
    {
      paramInt = getChildAt(paramInt - 1).getBottom();
      int i = this.mBottom - this.mTop - this.mListPadding.bottom - paramInt;
      View localView = getChildAt(0);
      int j = localView.getTop();
      if ((i > 0) && ((this.mFirstPosition > 0) || (j < this.mListPadding.top)))
      {
        paramInt = i;
        if (this.mFirstPosition == 0) {
          paramInt = Math.min(i, this.mListPadding.top - j);
        }
        offsetChildrenTopAndBottom(paramInt);
        if (this.mFirstPosition > 0)
        {
          fillUp(this.mFirstPosition - 1, localView.getTop() - this.mDividerHeight);
          adjustViewsUpOrDown();
        }
      }
    }
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private void correctTooLow(int paramInt)
  {
    if ((this.mFirstPosition == 0) && (paramInt > 0))
    {
      int i = getChildAt(0).getTop();
      int j = this.mListPadding.top;
      int k = this.mBottom - this.mTop - this.mListPadding.bottom;
      i -= j;
      View localView = getChildAt(paramInt - 1);
      j = localView.getBottom();
      int m = this.mFirstPosition + paramInt - 1;
      if (i > 0) {
        if ((m >= this.mItemCount - 1) && (j <= k))
        {
          if (m == this.mItemCount - 1) {
            adjustViewsUpOrDown();
          }
        }
        else
        {
          paramInt = i;
          if (m == this.mItemCount - 1) {
            paramInt = Math.min(i, j - k);
          }
          offsetChildrenTopAndBottom(-paramInt);
          if (m < this.mItemCount - 1)
          {
            fillDown(m + 1, localView.getBottom() + this.mDividerHeight);
            adjustViewsUpOrDown();
          }
        }
      }
    }
  }
  
  private int distanceToView(View paramView)
  {
    int i = 0;
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    int j = this.mBottom - this.mTop - this.mListPadding.bottom;
    if (this.mTempRect.bottom < this.mListPadding.top) {
      i = this.mListPadding.top - this.mTempRect.bottom;
    } else if (this.mTempRect.top > j) {
      i = this.mTempRect.top - j;
    }
    return i;
  }
  
  private void fillAboveAndBelow(View paramView, int paramInt)
  {
    int i = this.mDividerHeight;
    if (!this.mStackFromBottom)
    {
      fillUp(paramInt - 1, paramView.getTop() - i);
      adjustViewsUpOrDown();
      fillDown(paramInt + 1, paramView.getBottom() + i);
    }
    else
    {
      fillDown(paramInt + 1, paramView.getBottom() + i);
      adjustViewsUpOrDown();
      fillUp(paramInt - 1, paramView.getTop() - i);
    }
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  private View fillDown(int paramInt1, int paramInt2)
  {
    View localView1 = null;
    int i = this.mBottom - this.mTop;
    View localView2 = localView1;
    int j = i;
    int k = paramInt1;
    int m = paramInt2;
    if ((this.mGroupFlags & 0x22) == 34)
    {
      j = i - this.mListPadding.bottom;
      m = paramInt2;
      k = paramInt1;
      localView2 = localView1;
    }
    for (;;)
    {
      boolean bool = true;
      if ((m >= j) || (k >= this.mItemCount)) {
        break;
      }
      if (k != this.mSelectedPosition) {
        bool = false;
      }
      localView1 = makeAndAddView(k, m, true, this.mListPadding.left, bool);
      m = localView1.getBottom() + this.mDividerHeight;
      if (bool) {
        localView2 = localView1;
      }
      k++;
    }
    setVisibleRangeHint(this.mFirstPosition, this.mFirstPosition + getChildCount() - 1);
    return localView2;
  }
  
  private View fillFromMiddle(int paramInt1, int paramInt2)
  {
    paramInt2 -= paramInt1;
    int i = reconcileSelectedPosition();
    View localView = makeAndAddView(i, paramInt1, true, this.mListPadding.left, true);
    this.mFirstPosition = i;
    paramInt1 = localView.getMeasuredHeight();
    if (paramInt1 <= paramInt2) {
      localView.offsetTopAndBottom((paramInt2 - paramInt1) / 2);
    }
    fillAboveAndBelow(localView, i);
    if (!this.mStackFromBottom) {
      correctTooHigh(getChildCount());
    } else {
      correctTooLow(getChildCount());
    }
    return localView;
  }
  
  private View fillFromSelection(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = getVerticalFadingEdgeLength();
    int j = this.mSelectedPosition;
    paramInt2 = getTopSelectionPixel(paramInt2, i, j);
    paramInt3 = getBottomSelectionPixel(paramInt3, i, j);
    View localView = makeAndAddView(j, paramInt1, true, this.mListPadding.left, true);
    if (localView.getBottom() > paramInt3) {
      localView.offsetTopAndBottom(-Math.min(localView.getTop() - paramInt2, localView.getBottom() - paramInt3));
    } else if (localView.getTop() < paramInt2) {
      localView.offsetTopAndBottom(Math.min(paramInt2 - localView.getTop(), paramInt3 - localView.getBottom()));
    }
    fillAboveAndBelow(localView, j);
    if (!this.mStackFromBottom) {
      correctTooHigh(getChildCount());
    } else {
      correctTooLow(getChildCount());
    }
    return localView;
  }
  
  private View fillFromTop(int paramInt)
  {
    this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
    this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
    if (this.mFirstPosition < 0) {
      this.mFirstPosition = 0;
    }
    return fillDown(this.mFirstPosition, paramInt);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  private View fillSpecific(int paramInt1, int paramInt2)
  {
    boolean bool;
    if (paramInt1 == this.mSelectedPosition) {
      bool = true;
    } else {
      bool = false;
    }
    View localView1 = makeAndAddView(paramInt1, paramInt2, true, this.mListPadding.left, bool);
    this.mFirstPosition = paramInt1;
    paramInt2 = this.mDividerHeight;
    Object localObject1;
    Object localObject2;
    if (!this.mStackFromBottom)
    {
      localObject1 = fillUp(paramInt1 - 1, localView1.getTop() - paramInt2);
      adjustViewsUpOrDown();
      localObject2 = fillDown(paramInt1 + 1, localView1.getBottom() + paramInt2);
      paramInt1 = getChildCount();
      if (paramInt1 > 0) {
        correctTooHigh(paramInt1);
      }
    }
    else
    {
      View localView2 = fillDown(paramInt1 + 1, localView1.getBottom() + paramInt2);
      adjustViewsUpOrDown();
      View localView3 = fillUp(paramInt1 - 1, localView1.getTop() - paramInt2);
      paramInt1 = getChildCount();
      localObject1 = localView3;
      localObject2 = localView2;
      if (paramInt1 > 0)
      {
        correctTooLow(paramInt1);
        localObject2 = localView2;
        localObject1 = localView3;
      }
    }
    if (bool) {
      return localView1;
    }
    if (localObject1 != null) {
      return (View)localObject1;
    }
    return (View)localObject2;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  private View fillUp(int paramInt1, int paramInt2)
  {
    View localView1 = null;
    int i = 0;
    View localView2 = localView1;
    int j = paramInt1;
    int k = paramInt2;
    if ((this.mGroupFlags & 0x22) == 34)
    {
      i = this.mListPadding.top;
      k = paramInt2;
      j = paramInt1;
      localView2 = localView1;
    }
    for (;;)
    {
      boolean bool = true;
      if ((k <= i) || (j < 0)) {
        break;
      }
      if (j != this.mSelectedPosition) {
        bool = false;
      }
      localView1 = makeAndAddView(j, k, false, this.mListPadding.left, bool);
      k = localView1.getTop() - this.mDividerHeight;
      if (bool) {
        localView2 = localView1;
      }
      j--;
    }
    this.mFirstPosition = (j + 1);
    setVisibleRangeHint(this.mFirstPosition, this.mFirstPosition + getChildCount() - 1);
    return localView2;
  }
  
  private int getArrowScrollPreviewLength()
  {
    return Math.max(2, getVerticalFadingEdgeLength());
  }
  
  private int getBottomSelectionPixel(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1;
    if (paramInt3 != this.mItemCount - 1) {
      i = paramInt1 - paramInt2;
    }
    return i;
  }
  
  private int getTopSelectionPixel(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1;
    if (paramInt3 > 0) {
      i = paramInt1 + paramInt2;
    }
    return i;
  }
  
  private boolean handleHorizontalFocusWithinListItem(int paramInt)
  {
    if ((paramInt != 17) && (paramInt != 66)) {
      throw new IllegalArgumentException("direction must be one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}");
    }
    int i = getChildCount();
    if ((this.mItemsCanFocus) && (i > 0) && (this.mSelectedPosition != -1))
    {
      Object localObject = getSelectedView();
      if ((localObject != null) && (((View)localObject).hasFocus()) && ((localObject instanceof ViewGroup)))
      {
        View localView1 = ((View)localObject).findFocus();
        View localView2 = FocusFinder.getInstance().findNextFocus((ViewGroup)localObject, localView1, paramInt);
        if (localView2 != null)
        {
          localObject = this.mTempRect;
          if (localView1 != null)
          {
            localView1.getFocusedRect((Rect)localObject);
            offsetDescendantRectToMyCoords(localView1, (Rect)localObject);
            offsetRectIntoDescendantCoords(localView2, (Rect)localObject);
          }
          else
          {
            localObject = null;
          }
          if (localView2.requestFocus(paramInt, (Rect)localObject)) {
            return true;
          }
        }
        localObject = FocusFinder.getInstance().findNextFocus((ViewGroup)getRootView(), localView1, paramInt);
        if (localObject != null) {
          return isViewAncestorOf((View)localObject, this);
        }
      }
    }
    return false;
  }
  
  private void handleNewSelectionChange(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramInt2 != -1)
    {
      int i = 0;
      int j = this.mSelectedPosition - this.mFirstPosition;
      paramInt2 -= this.mFirstPosition;
      View localView2;
      if (paramInt1 == 33)
      {
        View localView1 = getChildAt(paramInt2);
        localView2 = paramView;
        paramInt1 = 1;
        paramView = localView1;
      }
      else
      {
        paramInt1 = j;
        localView2 = getChildAt(paramInt2);
        j = paramInt2;
        paramInt2 = paramInt1;
        paramInt1 = i;
      }
      i = getChildCount();
      boolean bool1 = true;
      if (paramView != null)
      {
        boolean bool2;
        if ((!paramBoolean) && (paramInt1 != 0)) {
          bool2 = true;
        } else {
          bool2 = false;
        }
        paramView.setSelected(bool2);
        measureAndAdjustDown(paramView, paramInt2, i);
      }
      if (localView2 != null)
      {
        if ((!paramBoolean) && (paramInt1 == 0)) {
          paramBoolean = bool1;
        } else {
          paramBoolean = false;
        }
        localView2.setSelected(paramBoolean);
        measureAndAdjustDown(localView2, j, i);
      }
      return;
    }
    throw new IllegalArgumentException("newSelectedPosition needs to be valid");
  }
  
  @UnsupportedAppUsage
  private boolean isDirectChildHeaderOrFooter(View paramView)
  {
    ArrayList localArrayList = this.mHeaderViewInfos;
    int i = localArrayList.size();
    for (int j = 0; j < i; j++) {
      if (paramView == ((FixedViewInfo)localArrayList.get(j)).view) {
        return true;
      }
    }
    localArrayList = this.mFooterViewInfos;
    i = localArrayList.size();
    for (j = 0; j < i; j++) {
      if (paramView == ((FixedViewInfo)localArrayList.get(j)).view) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isViewAncestorOf(View paramView1, View paramView2)
  {
    boolean bool = true;
    if (paramView1 == paramView2) {
      return true;
    }
    paramView1 = paramView1.getParent();
    if ((!(paramView1 instanceof ViewGroup)) || (!isViewAncestorOf((View)paramView1, paramView2))) {
      bool = false;
    }
    return bool;
  }
  
  private int lookForSelectablePositionOnScreen(int paramInt)
  {
    int i = this.mFirstPosition;
    int j;
    ListAdapter localListAdapter;
    if (paramInt == 130)
    {
      if (this.mSelectedPosition != -1) {
        j = this.mSelectedPosition + 1;
      } else {
        j = i;
      }
      if (j >= this.mAdapter.getCount()) {
        return -1;
      }
      paramInt = j;
      if (j < i) {
        paramInt = i;
      }
      j = getLastVisiblePosition();
      localListAdapter = getAdapter();
      while (paramInt <= j)
      {
        if ((localListAdapter.isEnabled(paramInt)) && (getChildAt(paramInt - i).getVisibility() == 0)) {
          return paramInt;
        }
        paramInt++;
      }
    }
    else
    {
      int k = getChildCount() + i - 1;
      if (this.mSelectedPosition != -1) {
        paramInt = this.mSelectedPosition - 1;
      } else {
        paramInt = getChildCount() + i - 1;
      }
      if ((paramInt < 0) || (paramInt >= this.mAdapter.getCount())) {
        break label222;
      }
      j = paramInt;
      if (paramInt > k) {
        j = k;
      }
      localListAdapter = getAdapter();
      for (paramInt = j; paramInt >= i; paramInt--) {
        if ((localListAdapter.isEnabled(paramInt)) && (getChildAt(paramInt - i).getVisibility() == 0)) {
          return paramInt;
        }
      }
    }
    return -1;
    label222:
    return -1;
  }
  
  @UnsupportedAppUsage
  private View makeAndAddView(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2)
  {
    if (!this.mDataChanged)
    {
      localView = this.mRecycler.getActiveView(paramInt1);
      if (localView != null)
      {
        setupChild(localView, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, true);
        return localView;
      }
    }
    View localView = obtainView(paramInt1, this.mIsScrap);
    setupChild(localView, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, this.mIsScrap[0]);
    return localView;
  }
  
  private void measureAndAdjustDown(View paramView, int paramInt1, int paramInt2)
  {
    int i = paramView.getHeight();
    measureItem(paramView);
    if (paramView.getMeasuredHeight() != i)
    {
      relayoutMeasuredItem(paramView);
      int j = paramView.getMeasuredHeight();
      paramInt1++;
      while (paramInt1 < paramInt2)
      {
        getChildAt(paramInt1).offsetTopAndBottom(j - i);
        paramInt1++;
      }
    }
  }
  
  private void measureItem(View paramView)
  {
    ViewGroup.LayoutParams localLayoutParams1 = paramView.getLayoutParams();
    ViewGroup.LayoutParams localLayoutParams2 = localLayoutParams1;
    if (localLayoutParams1 == null) {
      localLayoutParams2 = new ViewGroup.LayoutParams(-1, -2);
    }
    int i = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, localLayoutParams2.width);
    int j = localLayoutParams2.height;
    if (j > 0) {
      j = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
    } else {
      j = View.MeasureSpec.makeSafeMeasureSpec(getMeasuredHeight(), 0);
    }
    paramView.measure(i, j);
  }
  
  private void measureScrapChild(View paramView, int paramInt1, int paramInt2, int paramInt3)
  {
    AbsListView.LayoutParams localLayoutParams1 = (AbsListView.LayoutParams)paramView.getLayoutParams();
    AbsListView.LayoutParams localLayoutParams2 = localLayoutParams1;
    if (localLayoutParams1 == null)
    {
      localLayoutParams2 = (AbsListView.LayoutParams)generateDefaultLayoutParams();
      paramView.setLayoutParams(localLayoutParams2);
    }
    localLayoutParams2.viewType = this.mAdapter.getItemViewType(paramInt1);
    localLayoutParams2.isEnabled = this.mAdapter.isEnabled(paramInt1);
    localLayoutParams2.forceAdd = true;
    paramInt2 = ViewGroup.getChildMeasureSpec(paramInt2, this.mListPadding.left + this.mListPadding.right, localLayoutParams2.width);
    paramInt1 = localLayoutParams2.height;
    if (paramInt1 > 0) {
      paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824);
    } else {
      paramInt1 = View.MeasureSpec.makeSafeMeasureSpec(paramInt3, 0);
    }
    paramView.measure(paramInt2, paramInt1);
    paramView.forceLayout();
  }
  
  private View moveSelection(View paramView1, View paramView2, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = getVerticalFadingEdgeLength();
    int j = this.mSelectedPosition;
    int k = getTopSelectionPixel(paramInt2, i, j);
    i = getBottomSelectionPixel(paramInt2, i, j);
    int m;
    if (paramInt1 > 0)
    {
      paramView2 = makeAndAddView(j - 1, paramView1.getTop(), true, this.mListPadding.left, false);
      paramInt1 = this.mDividerHeight;
      paramView1 = makeAndAddView(j, paramView2.getBottom() + paramInt1, true, this.mListPadding.left, true);
      if (paramView1.getBottom() > i)
      {
        j = paramView1.getTop();
        m = paramView1.getBottom();
        paramInt2 = (paramInt3 - paramInt2) / 2;
        paramInt2 = Math.min(Math.min(j - k, m - i), paramInt2);
        paramView2.offsetTopAndBottom(-paramInt2);
        paramView1.offsetTopAndBottom(-paramInt2);
      }
      if (!this.mStackFromBottom)
      {
        fillUp(this.mSelectedPosition - 2, paramView1.getTop() - paramInt1);
        adjustViewsUpOrDown();
        fillDown(this.mSelectedPosition + 1, paramView1.getBottom() + paramInt1);
      }
      else
      {
        fillDown(this.mSelectedPosition + 1, paramView1.getBottom() + paramInt1);
        adjustViewsUpOrDown();
        fillUp(this.mSelectedPosition - 2, paramView1.getTop() - paramInt1);
      }
    }
    else if (paramInt1 < 0)
    {
      if (paramView2 != null) {
        paramView1 = makeAndAddView(j, paramView2.getTop(), true, this.mListPadding.left, true);
      } else {
        paramView1 = makeAndAddView(j, paramView1.getTop(), false, this.mListPadding.left, true);
      }
      if (paramView1.getTop() < k)
      {
        m = paramView1.getTop();
        paramInt1 = paramView1.getBottom();
        paramInt2 = (paramInt3 - paramInt2) / 2;
        paramView1.offsetTopAndBottom(Math.min(Math.min(k - m, i - paramInt1), paramInt2));
      }
      fillAboveAndBelow(paramView1, j);
    }
    else
    {
      paramInt1 = paramView1.getTop();
      paramView1 = makeAndAddView(j, paramInt1, true, this.mListPadding.left, true);
      if ((paramInt1 < paramInt2) && (paramView1.getBottom() < paramInt2 + 20)) {
        paramView1.offsetTopAndBottom(paramInt2 - paramView1.getTop());
      }
      fillAboveAndBelow(paramView1, j);
    }
    return paramView1;
  }
  
  private final int nextSelectedPositionForDirection(View paramView, int paramInt1, int paramInt2)
  {
    boolean bool = true;
    int i;
    if (paramInt2 == 130)
    {
      i = getHeight();
      int j = this.mListPadding.bottom;
      if ((paramView != null) && (paramView.getBottom() <= i - j))
      {
        if ((paramInt1 != -1) && (paramInt1 >= this.mFirstPosition)) {
          paramInt1++;
        } else {
          paramInt1 = this.mFirstPosition;
        }
      }
      else {
        return -1;
      }
    }
    else
    {
      i = this.mListPadding.top;
      if ((paramView == null) || (paramView.getTop() < i)) {
        break label168;
      }
      i = this.mFirstPosition + getChildCount() - 1;
      if ((paramInt1 != -1) && (paramInt1 <= i)) {
        paramInt1--;
      } else {
        paramInt1 = i;
      }
    }
    if ((paramInt1 >= 0) && (paramInt1 < this.mAdapter.getCount()))
    {
      if (paramInt2 != 130) {
        bool = false;
      }
      return lookForSelectablePosition(paramInt1, bool);
    }
    return -1;
    label168:
    return -1;
  }
  
  private int positionOfNewFocus(View paramView)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      if (isViewAncestorOf(paramView, getChildAt(j))) {
        return this.mFirstPosition + j;
      }
    }
    throw new IllegalArgumentException("newFocus is not a child of any of the children of the list!");
  }
  
  private void relayoutMeasuredItem(View paramView)
  {
    int i = paramView.getMeasuredWidth();
    int j = paramView.getMeasuredHeight();
    int k = this.mListPadding.left;
    int m = paramView.getTop();
    paramView.layout(k, m, k + i, m + j);
  }
  
  private void removeFixedViewInfo(View paramView, ArrayList<FixedViewInfo> paramArrayList)
  {
    int i = paramArrayList.size();
    for (int j = 0; j < i; j++) {
      if (((FixedViewInfo)paramArrayList.get(j)).view == paramView)
      {
        paramArrayList.remove(j);
        break;
      }
    }
  }
  
  private void removeUnusedFixedViews(List<FixedViewInfo> paramList)
  {
    if (paramList == null) {
      return;
    }
    for (int i = paramList.size() - 1; i >= 0; i--)
    {
      View localView = ((FixedViewInfo)paramList.get(i)).view;
      AbsListView.LayoutParams localLayoutParams = (AbsListView.LayoutParams)localView.getLayoutParams();
      if ((localView.getParent() == null) && (localLayoutParams != null) && (localLayoutParams.recycledHeaderFooter))
      {
        removeDetachedView(localView, false);
        localLayoutParams.recycledHeaderFooter = false;
      }
    }
  }
  
  @UnsupportedAppUsage
  private void scrollListItemsBy(int paramInt)
  {
    offsetChildrenTopAndBottom(paramInt);
    int i = getHeight() - this.mListPadding.bottom;
    int j = this.mListPadding.top;
    AbsListView.RecycleBin localRecycleBin = this.mRecycler;
    View localView;
    if (paramInt < 0)
    {
      paramInt = getChildCount();
      localView = getChildAt(paramInt - 1);
      while (localView.getBottom() < i)
      {
        int k = this.mFirstPosition + paramInt - 1;
        if (k >= this.mItemCount - 1) {
          break;
        }
        localView = addViewBelow(localView, k);
        paramInt++;
      }
      if (localView.getBottom() < i) {
        offsetChildrenTopAndBottom(i - localView.getBottom());
      }
      localView = getChildAt(0);
      while (localView.getBottom() < j)
      {
        if (localRecycleBin.shouldRecycleViewType(((AbsListView.LayoutParams)localView.getLayoutParams()).viewType)) {
          localRecycleBin.addScrapView(localView, this.mFirstPosition);
        }
        detachViewFromParent(localView);
        localView = getChildAt(0);
        this.mFirstPosition += 1;
      }
    }
    else
    {
      localView = getChildAt(0);
      while ((localView.getTop() > j) && (this.mFirstPosition > 0))
      {
        localView = addViewAbove(localView, this.mFirstPosition);
        this.mFirstPosition -= 1;
      }
      if (localView.getTop() > j) {
        offsetChildrenTopAndBottom(j - localView.getTop());
      }
      paramInt = getChildCount() - 1;
      for (localView = getChildAt(paramInt); localView.getTop() > i; localView = getChildAt(paramInt))
      {
        if (localRecycleBin.shouldRecycleViewType(((AbsListView.LayoutParams)localView.getLayoutParams()).viewType)) {
          localRecycleBin.addScrapView(localView, this.mFirstPosition + paramInt);
        }
        detachViewFromParent(localView);
        paramInt--;
      }
    }
    localRecycleBin.fullyDetachScrapViews();
    removeUnusedFixedViews(this.mHeaderViewInfos);
    removeUnusedFixedViews(this.mFooterViewInfos);
  }
  
  private void setupChild(View paramView, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, boolean paramBoolean3)
  {
    Trace.traceBegin(8L, "setupListItem");
    if ((paramBoolean2) && (shouldShowSelector())) {
      paramBoolean2 = true;
    } else {
      paramBoolean2 = false;
    }
    if (paramBoolean2 != paramView.isSelected()) {
      i = 1;
    } else {
      i = 0;
    }
    int j = this.mTouchMode;
    boolean bool;
    if ((j > 0) && (j < 3) && (this.mMotionPosition == paramInt1)) {
      bool = true;
    } else {
      bool = false;
    }
    if (bool != paramView.isPressed()) {
      k = 1;
    } else {
      k = 0;
    }
    if ((paramBoolean3) && (i == 0) && (!paramView.isLayoutRequested())) {
      j = 0;
    } else {
      j = 1;
    }
    AbsListView.LayoutParams localLayoutParams1 = (AbsListView.LayoutParams)paramView.getLayoutParams();
    AbsListView.LayoutParams localLayoutParams2 = localLayoutParams1;
    if (localLayoutParams1 == null) {
      localLayoutParams2 = (AbsListView.LayoutParams)generateDefaultLayoutParams();
    }
    localLayoutParams2.viewType = this.mAdapter.getItemViewType(paramInt1);
    localLayoutParams2.isEnabled = this.mAdapter.isEnabled(paramInt1);
    if (i != 0) {
      paramView.setSelected(paramBoolean2);
    }
    if (k != 0) {
      paramView.setPressed(bool);
    }
    if ((this.mChoiceMode != 0) && (this.mCheckStates != null)) {
      if ((paramView instanceof Checkable)) {
        ((Checkable)paramView).setChecked(this.mCheckStates.get(paramInt1));
      } else if (getContext().getApplicationInfo().targetSdkVersion >= 11) {
        paramView.setActivated(this.mCheckStates.get(paramInt1));
      }
    }
    int i = -1;
    if (((paramBoolean3) && (!localLayoutParams2.forceAdd)) || ((localLayoutParams2.recycledHeaderFooter) && (localLayoutParams2.viewType == -2)))
    {
      if (!paramBoolean1) {
        i = 0;
      }
      attachViewToParent(paramView, i, localLayoutParams2);
      if ((paramBoolean3) && (((AbsListView.LayoutParams)paramView.getLayoutParams()).scrappedFromPosition != paramInt1)) {
        paramView.jumpDrawablesToCurrentState();
      }
    }
    else
    {
      localLayoutParams2.forceAdd = false;
      if (localLayoutParams2.viewType == -2) {
        localLayoutParams2.recycledHeaderFooter = true;
      }
      if (!paramBoolean1) {
        i = 0;
      }
      addViewInLayout(paramView, i, localLayoutParams2, true);
      paramView.resolveRtlPropertiesIfNeeded();
    }
    if (j != 0)
    {
      i = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, localLayoutParams2.width);
      paramInt1 = localLayoutParams2.height;
      if (paramInt1 > 0) {
        paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824);
      } else {
        paramInt1 = View.MeasureSpec.makeSafeMeasureSpec(getMeasuredHeight(), 0);
      }
      paramView.measure(i, paramInt1);
    }
    else
    {
      cleanupLayoutState(paramView);
    }
    int k = paramView.getMeasuredWidth();
    i = paramView.getMeasuredHeight();
    if (paramBoolean1) {
      paramInt1 = paramInt2;
    } else {
      paramInt1 = paramInt2 - i;
    }
    if (j != 0)
    {
      paramView.layout(paramInt3, paramInt1, paramInt3 + k, paramInt1 + i);
    }
    else
    {
      paramView.offsetLeftAndRight(paramInt3 - paramView.getLeft());
      paramView.offsetTopAndBottom(paramInt1 - paramView.getTop());
    }
    if ((this.mCachingStarted) && (!paramView.isDrawingCacheEnabled())) {
      paramView.setDrawingCacheEnabled(true);
    }
    Trace.traceEnd(8L);
  }
  
  private boolean shouldAdjustHeightForDivider(int paramInt)
  {
    int i = this.mDividerHeight;
    Object localObject = this.mOverScrollHeader;
    Drawable localDrawable = this.mOverScrollFooter;
    int j;
    if (localObject != null) {
      j = 1;
    } else {
      j = 0;
    }
    int k;
    if (localDrawable != null) {
      k = 1;
    } else {
      k = 0;
    }
    if ((i > 0) && (this.mDivider != null)) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0)
    {
      if ((isOpaque()) && (!super.isOpaque())) {
        i = 1;
      } else {
        i = 0;
      }
      int m = this.mItemCount;
      int n = getHeaderViewsCount();
      int i1 = m - this.mFooterViewInfos.size();
      int i2;
      if (paramInt < n) {
        i2 = 1;
      } else {
        i2 = 0;
      }
      int i3;
      if (paramInt >= i1) {
        i3 = 1;
      } else {
        i3 = 0;
      }
      boolean bool1 = this.mHeaderDividersEnabled;
      boolean bool2 = this.mFooterDividersEnabled;
      if ((!bool1) && (i2 != 0)) {}
      for (;;)
      {
        break;
        if ((bool2) || (i3 == 0))
        {
          localObject = this.mAdapter;
          if (!this.mStackFromBottom)
          {
            if (paramInt == m - 1) {
              j = 1;
            } else {
              j = 0;
            }
            if ((k == 0) || (j == 0))
            {
              k = paramInt + 1;
              if (((ListAdapter)localObject).isEnabled(paramInt))
              {
                if ((!bool1) && ((i2 != 0) || (k < n))) {
                  break label296;
                }
                if ((j == 0) && ((!((ListAdapter)localObject).isEnabled(k)) || ((bool2) || ((i3 != 0) || (k >= i1))))) {
                  break label296;
                }
                return true;
              }
              label296:
              if (i != 0) {
                return true;
              }
            }
          }
          else
          {
            if (j != 0) {
              k = 1;
            } else {
              k = 0;
            }
            if (paramInt == k) {
              k = 1;
            } else {
              k = 0;
            }
            if (k == 0)
            {
              j = paramInt - 1;
              if (((ListAdapter)localObject).isEnabled(paramInt))
              {
                if ((!bool1) && ((i2 != 0) || (j < n))) {
                  break label418;
                }
                if ((k == 0) && ((!((ListAdapter)localObject).isEnabled(j)) || ((bool2) || ((i3 != 0) || (j >= i1))))) {
                  break label418;
                }
                return true;
              }
              label418:
              if (i != 0) {
                return true;
              }
            }
            else {}
          }
        }
      }
    }
    return false;
  }
  
  private boolean showingBottomFadingEdge()
  {
    int i = getChildCount();
    int j = getChildAt(i - 1).getBottom();
    int k = this.mFirstPosition;
    boolean bool1 = true;
    int m = this.mScrollY;
    int n = getHeight();
    int i1 = this.mListPadding.bottom;
    boolean bool2 = bool1;
    if (k + i - 1 >= this.mItemCount - 1) {
      if (j < m + n - i1) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
    }
    return bool2;
  }
  
  private boolean showingTopFadingEdge()
  {
    int i = this.mScrollY;
    int j = this.mListPadding.top;
    int k = this.mFirstPosition;
    boolean bool = false;
    if ((k > 0) || (getChildAt(0).getTop() > i + j)) {
      bool = true;
    }
    return bool;
  }
  
  public void addFooterView(View paramView)
  {
    addFooterView(paramView, null, true);
  }
  
  public void addFooterView(View paramView, Object paramObject, boolean paramBoolean)
  {
    if ((paramView.getParent() != null) && (paramView.getParent() != this) && (Log.isLoggable("ListView", 5))) {
      Log.w("ListView", "The specified child already has a parent. You must call removeView() on the child's parent first.");
    }
    FixedViewInfo localFixedViewInfo = new FixedViewInfo();
    localFixedViewInfo.view = paramView;
    localFixedViewInfo.data = paramObject;
    localFixedViewInfo.isSelectable = paramBoolean;
    this.mFooterViewInfos.add(localFixedViewInfo);
    this.mAreAllItemsSelectable &= paramBoolean;
    if (this.mAdapter != null)
    {
      if (!(this.mAdapter instanceof HeaderViewListAdapter)) {
        wrapHeaderListAdapterInternal();
      }
      if (this.mDataSetObserver != null) {
        this.mDataSetObserver.onChanged();
      }
    }
  }
  
  public void addHeaderView(View paramView)
  {
    addHeaderView(paramView, null, true);
  }
  
  public void addHeaderView(View paramView, Object paramObject, boolean paramBoolean)
  {
    if ((paramView.getParent() != null) && (paramView.getParent() != this) && (Log.isLoggable("ListView", 5))) {
      Log.w("ListView", "The specified child already has a parent. You must call removeView() on the child's parent first.");
    }
    FixedViewInfo localFixedViewInfo = new FixedViewInfo();
    localFixedViewInfo.view = paramView;
    localFixedViewInfo.data = paramObject;
    localFixedViewInfo.isSelectable = paramBoolean;
    this.mHeaderViewInfos.add(localFixedViewInfo);
    this.mAreAllItemsSelectable &= paramBoolean;
    if (this.mAdapter != null)
    {
      if (!(this.mAdapter instanceof HeaderViewListAdapter)) {
        wrapHeaderListAdapterInternal();
      }
      if (this.mDataSetObserver != null) {
        this.mDataSetObserver.onChanged();
      }
    }
  }
  
  public boolean areFooterDividersEnabled()
  {
    return this.mFooterDividersEnabled;
  }
  
  public boolean areHeaderDividersEnabled()
  {
    return this.mHeaderDividersEnabled;
  }
  
  @UnsupportedAppUsage
  boolean arrowScroll(int paramInt)
  {
    try
    {
      this.mInLayout = true;
      boolean bool = arrowScrollImpl(paramInt);
      if (bool) {
        playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
      }
      return bool;
    }
    finally
    {
      this.mInLayout = false;
    }
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
  
  protected void dispatchDataSetObserverOnChangedInternal()
  {
    if (this.mDataSetObserver != null) {
      this.mDataSetObserver.onChanged();
    }
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    if (this.mCachingStarted) {
      this.mCachingActive = true;
    }
    int i = this.mDividerHeight;
    Object localObject1 = this.mOverScrollHeader;
    Drawable localDrawable = this.mOverScrollFooter;
    int j;
    if (localObject1 != null) {
      j = 1;
    } else {
      j = 0;
    }
    int k;
    if (localDrawable != null) {
      k = 1;
    } else {
      k = 0;
    }
    int m;
    if ((i > 0) && (this.mDivider != null)) {
      m = 1;
    } else {
      m = 0;
    }
    if ((m == 0) && (j == 0) && (k == 0)) {
      break label1236;
    }
    Rect localRect = this.mTempRect;
    localRect.left = this.mPaddingLeft;
    localRect.right = (this.mRight - this.mLeft - this.mPaddingRight);
    int n = getChildCount();
    int i1 = getHeaderViewsCount();
    int i2 = this.mItemCount;
    int i3 = i2 - this.mFooterViewInfos.size();
    boolean bool1 = this.mHeaderDividersEnabled;
    boolean bool2 = this.mFooterDividersEnabled;
    int i4 = this.mFirstPosition;
    boolean bool3 = this.mAreAllItemsSelectable;
    ListAdapter localListAdapter = this.mAdapter;
    int i5;
    if ((isOpaque()) && (!super.isOpaque())) {
      i5 = 1;
    } else {
      i5 = 0;
    }
    if (i5 != 0) {
      if ((this.mDividerPaint == null) && (this.mIsCacheColorOpaque))
      {
        this.mDividerPaint = new Paint();
        this.mDividerPaint.setColor(getCacheColorHint());
      }
      else {}
    }
    Paint localPaint = this.mDividerPaint;
    int i6 = this.mGroupFlags;
    int i7;
    if ((i6 & 0x22) == 34)
    {
      i6 = this.mListPadding.top;
      i7 = this.mListPadding.bottom;
    }
    else
    {
      i7 = 0;
      i6 = 0;
    }
    int i8 = this.mBottom - this.mTop - i7 + this.mScrollY;
    int i9;
    Object localObject2;
    int i10;
    int i11;
    int i12;
    int i13;
    if (!this.mStackFromBottom)
    {
      i6 = this.mScrollY;
      if ((n > 0) && (i6 < 0)) {
        if (j != 0)
        {
          localRect.bottom = 0;
          localRect.top = i6;
          drawOverscrollHeader(paramCanvas, (Drawable)localObject1, localRect);
        }
        else if (m != 0)
        {
          localRect.bottom = 0;
          localRect.top = (-i);
          drawDivider(paramCanvas, localRect, -1);
        }
      }
      i7 = 0;
      i9 = 0;
      i6 = i8;
      localObject2 = localObject1;
      while (i7 < n)
      {
        i10 = i4 + i7;
        if (i10 < i1) {
          i11 = 1;
        } else {
          i11 = 0;
        }
        if (i10 >= i3) {
          i12 = 1;
        } else {
          i12 = 0;
        }
        if (((!bool1) && (i11 != 0)) || ((!bool2) && (i12 != 0))) {
          break label716;
        }
        i8 = getChildAt(i7).getBottom();
        if (i7 == n - 1) {
          i9 = 1;
        } else {
          i9 = 0;
        }
        if ((m != 0) && (i8 < i6))
        {
          if ((k != 0) && (i9 != 0))
          {
            i9 = i8;
          }
          else
          {
            i13 = i10 + 1;
            localObject1 = localListAdapter;
            if (((ListAdapter)localObject1).isEnabled(i10))
            {
              if ((!bool1) && ((i11 != 0) || (i13 < i1))) {
                break label669;
              }
              if ((i9 == 0) && ((!((ListAdapter)localObject1).isEnabled(i13)) || ((bool2) || ((i12 != 0) || (i13 >= i3))))) {
                break label669;
              }
              localRect.top = i8;
              localRect.bottom = (i8 + i);
              drawDivider(paramCanvas, localRect, i7);
              i9 = i8;
              break label716;
            }
            label669:
            if (i5 != 0)
            {
              localRect.top = i8;
              localRect.bottom = (i8 + i);
              paramCanvas.drawRect(localRect, localPaint);
              i9 = i8;
            }
            else
            {
              i9 = i8;
            }
          }
        }
        else {
          i9 = i8;
        }
        label716:
        i7++;
      }
      j = this.mBottom + this.mScrollY;
      if (k != 0) {
        if ((i4 + n == i2) && (j > i9))
        {
          localRect.top = i9;
          localRect.bottom = j;
          drawOverscrollFooter(paramCanvas, localDrawable, localRect);
        }
        else {}
      }
    }
    else
    {
      localObject2 = localDrawable;
      i10 = this.mScrollY;
      if ((n > 0) && (j != 0))
      {
        localRect.top = i10;
        localRect.bottom = getChildAt(0).getTop();
        drawOverscrollHeader(paramCanvas, (Drawable)localObject1, localRect);
      }
      if (j != 0) {
        j = 1;
      } else {
        j = 0;
      }
      i9 = j;
      i7 = j;
      j = i6;
      i6 = i9;
      i9 = i4;
      while (i7 < n)
      {
        i13 = i9 + i7;
        if (i13 < i1) {
          i11 = 1;
        } else {
          i11 = 0;
        }
        if (i13 >= i3) {
          i12 = 1;
        } else {
          i12 = 0;
        }
        if ((!bool1) && (i11 != 0)) {}
        for (;;)
        {
          break;
          if ((bool2) || (i12 == 0))
          {
            int i14 = getChildAt(i7).getTop();
            if (m != 0) {
              if (i14 > j)
              {
                i4 = j;
                if (i7 == i6) {
                  j = 1;
                } else {
                  j = 0;
                }
                int i15 = i13 - 1;
                if (localListAdapter.isEnabled(i13))
                {
                  if ((!bool1) && ((i11 != 0) || (i15 < i1))) {
                    break label1102;
                  }
                  if ((j == 0) && ((!localListAdapter.isEnabled(i15)) || ((bool2) || ((i12 != 0) || (i15 >= i3))))) {
                    break label1102;
                  }
                  localRect.top = (i14 - i);
                  localRect.bottom = i14;
                  drawDivider(paramCanvas, localRect, i7 - 1);
                  j = i4;
                  break;
                }
                label1102:
                j = i4;
                if (i5 != 0)
                {
                  localRect.top = (i14 - i);
                  localRect.bottom = i14;
                  paramCanvas.drawRect(localRect, localPaint);
                  j = i4;
                }
              }
              else {}
            }
          }
        }
        i7++;
      }
      if ((n > 0) && (i10 > 0)) {
        if (k != 0)
        {
          j = this.mBottom;
          localRect.top = j;
          localRect.bottom = (j + i10);
          drawOverscrollFooter(paramCanvas, (Drawable)localObject2, localRect);
        }
        else if (m != 0)
        {
          localRect.top = i8;
          localRect.bottom = (i8 + i);
          drawDivider(paramCanvas, localRect, -1);
        }
        else {}
      }
    }
    label1236:
    super.dispatchDraw(paramCanvas);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool1 = super.dispatchKeyEvent(paramKeyEvent);
    boolean bool2 = bool1;
    if (!bool1)
    {
      bool2 = bool1;
      if (getFocusedChild() != null)
      {
        bool2 = bool1;
        if (paramKeyEvent.getAction() == 0) {
          bool2 = onKeyDown(paramKeyEvent.getKeyCode(), paramKeyEvent);
        }
      }
    }
    return bool2;
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    if ((this.mCachingActive) && (paramView.mCachingFailed)) {
      this.mCachingActive = false;
    }
    return bool;
  }
  
  void drawDivider(Canvas paramCanvas, Rect paramRect, int paramInt)
  {
    Drawable localDrawable = this.mDivider;
    localDrawable.setBounds(paramRect);
    localDrawable.draw(paramCanvas);
  }
  
  void drawOverscrollFooter(Canvas paramCanvas, Drawable paramDrawable, Rect paramRect)
  {
    int i = paramDrawable.getMinimumHeight();
    paramCanvas.save();
    paramCanvas.clipRect(paramRect);
    if (paramRect.bottom - paramRect.top < i) {
      paramRect.bottom = (paramRect.top + i);
    }
    paramDrawable.setBounds(paramRect);
    paramDrawable.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  void drawOverscrollHeader(Canvas paramCanvas, Drawable paramDrawable, Rect paramRect)
  {
    int i = paramDrawable.getMinimumHeight();
    paramCanvas.save();
    paramCanvas.clipRect(paramRect);
    if (paramRect.bottom - paramRect.top < i) {
      paramRect.top = (paramRect.bottom - i);
    }
    paramDrawable.setBounds(paramRect);
    paramDrawable.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("recycleOnMeasure", recycleOnMeasure());
  }
  
  void fillGap(boolean paramBoolean)
  {
    if (Android_Widget_ListView.Extension.get().getExtension() != null) {
      ((Android_Widget_ListView.Interface)Android_Widget_ListView.Extension.get().getExtension().asInterface()).fillGap(this, paramBoolean);
    } else {
      originalFillGap(paramBoolean);
    }
  }
  
  int findMotionRow(int paramInt)
  {
    int i = getChildCount();
    if (i > 0)
    {
      int j;
      if (!this.mStackFromBottom) {
        for (j = 0; j < i; j++) {
          if (paramInt <= getChildAt(j).getBottom()) {
            return this.mFirstPosition + j;
          }
        }
      } else {
        for (j = i - 1; j >= 0; j--) {
          if (paramInt >= getChildAt(j).getTop()) {
            return this.mFirstPosition + j;
          }
        }
      }
    }
    return -1;
  }
  
  View findViewByPredicateInHeadersOrFooters(ArrayList<FixedViewInfo> paramArrayList, Predicate<View> paramPredicate, View paramView)
  {
    if (paramArrayList != null)
    {
      int i = paramArrayList.size();
      for (int j = 0; j < i; j++)
      {
        View localView = ((FixedViewInfo)paramArrayList.get(j)).view;
        if ((localView != paramView) && (!localView.isRootNamespace()))
        {
          localView = localView.findViewByPredicate(paramPredicate);
          if (localView != null) {
            return localView;
          }
        }
      }
    }
    return null;
  }
  
  protected <T extends View> T findViewByPredicateTraversal(Predicate<View> paramPredicate, View paramView)
  {
    View localView = super.findViewByPredicateTraversal(paramPredicate, paramView);
    Object localObject = localView;
    if (localView == null)
    {
      localObject = findViewByPredicateInHeadersOrFooters(this.mHeaderViewInfos, paramPredicate, paramView);
      if (localObject != null) {
        return (T)localObject;
      }
      paramPredicate = findViewByPredicateInHeadersOrFooters(this.mFooterViewInfos, paramPredicate, paramView);
      localObject = paramPredicate;
      if (paramPredicate != null) {
        return paramPredicate;
      }
    }
    return (T)localObject;
  }
  
  View findViewInHeadersOrFooters(ArrayList<FixedViewInfo> paramArrayList, int paramInt)
  {
    if (paramArrayList != null)
    {
      int i = paramArrayList.size();
      for (int j = 0; j < i; j++)
      {
        View localView = ((FixedViewInfo)paramArrayList.get(j)).view;
        if (!localView.isRootNamespace())
        {
          localView = localView.findViewById(paramInt);
          if (localView != null) {
            return localView;
          }
        }
      }
    }
    return null;
  }
  
  protected <T extends View> T findViewTraversal(int paramInt)
  {
    View localView1 = super.findViewTraversal(paramInt);
    View localView2 = localView1;
    if (localView1 == null)
    {
      localView2 = findViewInHeadersOrFooters(this.mHeaderViewInfos, paramInt);
      if (localView2 != null) {
        return localView2;
      }
      localView1 = findViewInHeadersOrFooters(this.mFooterViewInfos, paramInt);
      localView2 = localView1;
      if (localView1 != null) {
        return localView1;
      }
    }
    return localView2;
  }
  
  View findViewWithTagInHeadersOrFooters(ArrayList<FixedViewInfo> paramArrayList, Object paramObject)
  {
    if (paramArrayList != null)
    {
      int i = paramArrayList.size();
      for (int j = 0; j < i; j++)
      {
        View localView = ((FixedViewInfo)paramArrayList.get(j)).view;
        if (!localView.isRootNamespace())
        {
          localView = localView.findViewWithTag(paramObject);
          if (localView != null) {
            return localView;
          }
        }
      }
    }
    return null;
  }
  
  protected <T extends View> T findViewWithTagTraversal(Object paramObject)
  {
    View localView = super.findViewWithTagTraversal(paramObject);
    Object localObject = localView;
    if (localView == null)
    {
      localObject = findViewWithTagInHeadersOrFooters(this.mHeaderViewInfos, paramObject);
      if (localObject != null) {
        return (T)localObject;
      }
      paramObject = findViewWithTagInHeadersOrFooters(this.mFooterViewInfos, paramObject);
      localObject = paramObject;
      if (paramObject != null) {
        return (T)paramObject;
      }
    }
    return (T)localObject;
  }
  
  boolean fullScroll(int paramInt)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramInt == 33)
    {
      bool2 = bool1;
      if (this.mSelectedPosition != 0)
      {
        paramInt = lookForSelectablePositionAfter(this.mSelectedPosition, 0, true);
        if (paramInt >= 0)
        {
          this.mLayoutMode = 1;
          setSelectionInt(paramInt);
          invokeOnItemScrollListener();
        }
        bool2 = true;
      }
    }
    else
    {
      bool2 = bool1;
      if (paramInt == 130)
      {
        paramInt = this.mItemCount - 1;
        bool2 = bool1;
        if (this.mSelectedPosition < paramInt)
        {
          paramInt = lookForSelectablePositionAfter(this.mSelectedPosition, paramInt, false);
          if (paramInt >= 0)
          {
            this.mLayoutMode = 3;
            setSelectionInt(paramInt);
            invokeOnItemScrollListener();
          }
          bool2 = true;
        }
      }
    }
    if ((bool2) && (!awakenScrollBars()))
    {
      awakenScrollBars();
      invalidate();
    }
    return bool2;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ListView.class.getName();
  }
  
  public ListAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  @Deprecated
  public long[] getCheckItemIds()
  {
    if ((this.mAdapter != null) && (this.mAdapter.hasStableIds())) {
      return getCheckedItemIds();
    }
    if ((this.mChoiceMode != 0) && (this.mCheckStates != null) && (this.mAdapter != null))
    {
      SparseBooleanArray localSparseBooleanArray = this.mCheckStates;
      int i = localSparseBooleanArray.size();
      long[] arrayOfLong = new long[i];
      Object localObject = this.mAdapter;
      int j = 0;
      int k = 0;
      while (k < i)
      {
        int m = j;
        if (localSparseBooleanArray.valueAt(k))
        {
          arrayOfLong[j] = ((ListAdapter)localObject).getItemId(localSparseBooleanArray.keyAt(k));
          m = j + 1;
        }
        k++;
        j = m;
      }
      if (j == i) {
        return arrayOfLong;
      }
      localObject = new long[j];
      System.arraycopy(arrayOfLong, 0, localObject, 0, j);
      return (long[])localObject;
    }
    return new long[0];
  }
  
  public Drawable getDivider()
  {
    return this.mDivider;
  }
  
  public int getDividerHeight()
  {
    return this.mDividerHeight;
  }
  
  public int getFooterViewsCount()
  {
    return this.mFooterViewInfos.size();
  }
  
  public int getHeaderViewsCount()
  {
    return this.mHeaderViewInfos.size();
  }
  
  @UnsupportedAppUsage
  int getHeightForPosition(int paramInt)
  {
    int i = super.getHeightForPosition(paramInt);
    if (shouldAdjustHeightForDivider(paramInt)) {
      return this.mDividerHeight + i;
    }
    return i;
  }
  
  public boolean getItemsCanFocus()
  {
    return this.mItemsCanFocus;
  }
  
  public int getMaxScrollAmount()
  {
    return (int)((this.mBottom - this.mTop) * 0.33F);
  }
  
  public Drawable getOverscrollFooter()
  {
    return this.mOverScrollFooter;
  }
  
  public Drawable getOverscrollHeader()
  {
    return this.mOverScrollHeader;
  }
  
  public boolean isOpaque()
  {
    boolean bool;
    if (((this.mCachingActive) && (this.mIsCacheColorOpaque) && (this.mDividerIsOpaque) && (hasOpaqueScrollbars())) || (super.isOpaque())) {
      bool = true;
    } else {
      bool = false;
    }
    if (bool)
    {
      int i;
      if (this.mListPadding != null) {
        i = this.mListPadding.top;
      } else {
        i = this.mPaddingTop;
      }
      View localView = getChildAt(0);
      if ((localView != null) && (localView.getTop() <= i))
      {
        int j = getHeight();
        if (this.mListPadding != null) {
          i = this.mListPadding.bottom;
        } else {
          i = this.mPaddingBottom;
        }
        localView = getChildAt(getChildCount() - 1);
        if ((localView == null) || (localView.getBottom() < j - i)) {
          return false;
        }
      }
      else
      {
        return false;
      }
    }
    return bool;
  }
  
  protected void layoutChildren()
  {
    if (Android_Widget_ListView.Extension.get().getExtension() != null) {
      ((Android_Widget_ListView.Interface)Android_Widget_ListView.Extension.get().getExtension().asInterface()).layoutChildren(this);
    } else {
      originalLayoutChildren();
    }
  }
  
  @UnsupportedAppUsage
  int lookForSelectablePosition(int paramInt, boolean paramBoolean)
  {
    ListAdapter localListAdapter = this.mAdapter;
    if ((localListAdapter != null) && (!isInTouchMode()))
    {
      int i = localListAdapter.getCount();
      int j = paramInt;
      if (!this.mAreAllItemsSelectable)
      {
        if (paramBoolean) {
          for (paramInt = Math.max(0, paramInt);; paramInt++)
          {
            j = paramInt;
            if (paramInt >= i) {
              break;
            }
            j = paramInt;
            if (localListAdapter.isEnabled(paramInt)) {
              break;
            }
          }
        }
        for (paramInt = Math.min(paramInt, i - 1);; paramInt--)
        {
          j = paramInt;
          if (paramInt < 0) {
            break;
          }
          j = paramInt;
          if (localListAdapter.isEnabled(paramInt)) {
            break;
          }
        }
      }
      if ((j >= 0) && (j < i)) {
        return j;
      }
      return -1;
    }
    return -1;
  }
  
  int lookForSelectablePositionAfter(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    ListAdapter localListAdapter = this.mAdapter;
    if ((localListAdapter != null) && (!isInTouchMode()))
    {
      int i = lookForSelectablePosition(paramInt2, paramBoolean);
      if (i != -1) {
        return i;
      }
      int j = localListAdapter.getCount();
      i = MathUtils.constrain(paramInt1, -1, j - 1);
      if (paramBoolean)
      {
        for (paramInt1 = Math.min(paramInt2 - 1, j - 1); (paramInt1 > i) && (!localListAdapter.isEnabled(paramInt1)); paramInt1--) {}
        paramInt2 = paramInt1;
        if (paramInt1 <= i) {
          return -1;
        }
      }
      else
      {
        for (paramInt1 = Math.max(0, paramInt2 + 1); (paramInt1 < i) && (!localListAdapter.isEnabled(paramInt1)); paramInt1++) {}
        paramInt2 = paramInt1;
        if (paramInt1 >= i) {
          return -1;
        }
      }
      return paramInt2;
    }
    return -1;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  final int measureHeightOfChildren(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    Object localObject = this.mAdapter;
    if (localObject == null) {
      return this.mListPadding.top + this.mListPadding.bottom;
    }
    int i = this.mListPadding.top + this.mListPadding.bottom;
    int j = this.mDividerHeight;
    int k = 0;
    int m;
    if (paramInt3 == -1) {
      m = ((ListAdapter)localObject).getCount() - 1;
    } else {
      m = paramInt3;
    }
    AbsListView.RecycleBin localRecycleBin = this.mRecycler;
    boolean bool = recycleOnMeasure();
    boolean[] arrayOfBoolean = this.mIsScrap;
    int n = paramInt2;
    paramInt2 = k;
    paramInt3 = i;
    while (n <= m)
    {
      localObject = obtainView(n, arrayOfBoolean);
      measureScrapChild((View)localObject, n, paramInt1, paramInt4);
      i = paramInt3;
      if (n > 0) {
        i = paramInt3 + j;
      }
      if ((bool) && (localRecycleBin.shouldRecycleViewType(((AbsListView.LayoutParams)((View)localObject).getLayoutParams()).viewType))) {
        localRecycleBin.addScrapView((View)localObject, -1);
      }
      paramInt3 = i + ((View)localObject).getMeasuredHeight();
      if (paramInt3 >= paramInt4)
      {
        if ((paramInt5 >= 0) && (n > paramInt5) && (paramInt2 > 0) && (paramInt3 != paramInt4)) {
          paramInt1 = paramInt2;
        } else {
          paramInt1 = paramInt4;
        }
        return paramInt1;
      }
      i = paramInt2;
      if (paramInt5 >= 0)
      {
        i = paramInt2;
        if (n >= paramInt5) {
          i = paramInt3;
        }
      }
      n++;
      paramInt2 = i;
    }
    return paramInt3;
  }
  
  protected void onDetachedFromWindow()
  {
    FocusSelector localFocusSelector = this.mFocusSelector;
    if (localFocusSelector != null)
    {
      removeCallbacks(localFocusSelector);
      this.mFocusSelector = null;
    }
    super.onDetachedFromWindow();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    int i = getChildCount();
    if (i > 0)
    {
      for (int j = 0; j < i; j++) {
        addHeaderView(getChildAt(j));
      }
      removeAllViews();
    }
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    ListAdapter localListAdapter = this.mAdapter;
    int i = -1;
    int j = 0;
    int k = 0;
    int m = i;
    int n = j;
    if (localListAdapter != null)
    {
      m = i;
      n = j;
      if (paramBoolean)
      {
        m = i;
        n = j;
        if (paramRect != null)
        {
          paramRect.offset(this.mScrollX, this.mScrollY);
          if (localListAdapter.getCount() < getChildCount() + this.mFirstPosition)
          {
            this.mLayoutMode = 0;
            layoutChildren();
          }
          Rect localRect = this.mTempRect;
          int i1 = Integer.MAX_VALUE;
          int i2 = getChildCount();
          int i3 = this.mFirstPosition;
          j = 0;
          for (;;)
          {
            m = i;
            n = k;
            if (j >= i2) {
              break;
            }
            if (!localListAdapter.isEnabled(i3 + j))
            {
              n = i1;
            }
            else
            {
              View localView = getChildAt(j);
              localView.getDrawingRect(localRect);
              offsetDescendantRectToMyCoords(localView, localRect);
              m = getDistance(paramRect, localRect, paramInt);
              n = i1;
              if (m < i1)
              {
                n = m;
                i = j;
                k = localView.getTop();
              }
            }
            j++;
            i1 = n;
          }
        }
      }
    }
    if (m >= 0) {
      setSelectionFromTop(this.mFirstPosition + m, n);
    } else {
      requestLayout();
    }
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(View paramView, int paramInt, AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoForItem(paramView, paramInt, paramAccessibilityNodeInfo);
    paramView = (AbsListView.LayoutParams)paramView.getLayoutParams();
    boolean bool;
    if ((paramView != null) && (paramView.viewType == -2)) {
      bool = true;
    } else {
      bool = false;
    }
    paramAccessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(paramInt, 1, 0, 1, bool, isItemChecked(paramInt)));
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    int i = getCount();
    paramAccessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(i, 1, false, getSelectionModeForAccessibility()));
    if (i > 0) {
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION);
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return commonKey(paramInt, 1, paramKeyEvent);
  }
  
  public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    return commonKey(paramInt1, paramInt2, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return commonKey(paramInt, 1, paramKeyEvent);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getMode(paramInt2);
    int k = View.MeasureSpec.getSize(paramInt1);
    int m = View.MeasureSpec.getSize(paramInt2);
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    if (this.mAdapter == null) {
      paramInt2 = 0;
    } else {
      paramInt2 = this.mAdapter.getCount();
    }
    this.mItemCount = paramInt2;
    int i3 = n;
    paramInt2 = i1;
    int i4 = i2;
    if (this.mItemCount > 0) {
      if (i != 0)
      {
        i3 = n;
        paramInt2 = i1;
        i4 = i2;
        if (j != 0) {}
      }
      else
      {
        View localView = obtainView(0, this.mIsScrap);
        measureScrapChild(localView, 0, paramInt1, m);
        i2 = localView.getMeasuredWidth();
        i1 = localView.getMeasuredHeight();
        n = combineMeasuredStates(0, localView.getMeasuredState());
        i3 = i2;
        paramInt2 = i1;
        i4 = n;
        if (recycleOnMeasure())
        {
          i3 = i2;
          paramInt2 = i1;
          i4 = n;
          if (this.mRecycler.shouldRecycleViewType(((AbsListView.LayoutParams)localView.getLayoutParams()).viewType))
          {
            this.mRecycler.addScrapView(localView, 0);
            i4 = n;
            paramInt2 = i1;
            i3 = i2;
          }
        }
      }
    }
    if (i == 0) {
      i4 = this.mListPadding.left + this.mListPadding.right + i3 + getVerticalScrollbarWidth();
    } else {
      i4 = 0xFF000000 & i4 | k;
    }
    if (j == 0) {
      paramInt2 = this.mListPadding.top + this.mListPadding.bottom + paramInt2 + getVerticalFadingEdgeLength() * 2;
    } else {
      paramInt2 = m;
    }
    i3 = paramInt2;
    if (j == Integer.MIN_VALUE) {
      i3 = measureHeightOfChildren(paramInt1, 0, -1, paramInt2, -1);
    }
    setMeasuredDimension(i4, i3);
    this.mWidthMeasureSpec = paramInt1;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (getChildCount() > 0)
    {
      View localView = getFocusedChild();
      if (localView != null)
      {
        int i = this.mFirstPosition;
        int j = indexOfChild(localView);
        int k = Math.max(0, localView.getBottom() - (paramInt2 - this.mPaddingTop));
        int m = localView.getTop();
        if (this.mFocusSelector == null) {
          this.mFocusSelector = new FocusSelector(null);
        }
        post(this.mFocusSelector.setupForSetSelection(i + j, m - k));
      }
    }
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void originalFillGap(boolean paramBoolean)
  {
    int i = getChildCount();
    int j;
    if (paramBoolean)
    {
      j = 0;
      if ((this.mGroupFlags & 0x22) == 34) {
        j = getListPaddingTop();
      }
      if (i > 0) {
        j = getChildAt(i - 1).getBottom() + this.mDividerHeight;
      }
      fillDown(this.mFirstPosition + i, j);
      correctTooHigh(getChildCount());
    }
    else
    {
      j = 0;
      if ((this.mGroupFlags & 0x22) == 34) {
        j = getListPaddingBottom();
      }
      if (i > 0) {
        j = getChildAt(0).getTop() - this.mDividerHeight;
      } else {
        j = getHeight() - j;
      }
      fillUp(this.mFirstPosition - 1, j);
      correctTooLow(getChildCount());
    }
  }
  
  void originalLayoutChildren()
  {
    boolean bool1 = this.mBlockLayoutRequests;
    if (bool1) {
      return;
    }
    this.mBlockLayoutRequests = true;
    try
    {
      super.layoutChildren();
      invalidate();
      if (this.mAdapter == null)
      {
        resetList();
        invokeOnItemScrollListener();
        return;
      }
      int i = this.mListPadding.top;
      int j = this.mBottom - this.mTop - this.mListPadding.bottom;
      int k = getChildCount();
      int m = 0;
      Object localObject2 = null;
      int n = this.mLayoutMode;
      if (n != 1) {
        if (n != 2)
        {
          if ((n != 3) && (n != 4) && (n != 5))
          {
            n = this.mSelectedPosition - this.mFirstPosition;
            localObject1 = localObject2;
            if (n >= 0)
            {
              localObject1 = localObject2;
              if (n < k) {
                localObject1 = getChildAt(n);
              }
            }
            localObject2 = getChildAt(0);
            if (this.mNextSelectedPosition >= 0) {
              m = this.mNextSelectedPosition - this.mSelectedPosition;
            }
            localView1 = getChildAt(n + m);
            break label278;
          }
        }
        else
        {
          m = this.mNextSelectedPosition - this.mFirstPosition;
          if ((m >= 0) && (m < k))
          {
            localView1 = getChildAt(m);
            m = 0;
            localObject1 = null;
            localObject2 = null;
            break label278;
          }
          m = 0;
          localObject1 = null;
          localObject2 = null;
          localView1 = null;
          break label278;
        }
      }
      m = 0;
      localObject1 = null;
      localObject2 = null;
      View localView1 = null;
      label278:
      boolean bool2 = this.mDataChanged;
      if (bool2) {
        handleDataChanged();
      }
      if (this.mItemCount == 0)
      {
        resetList();
        invokeOnItemScrollListener();
        return;
      }
      if (this.mItemCount == this.mAdapter.getCount())
      {
        setSelectedPositionInt(this.mNextSelectedPosition);
        Object localObject4 = null;
        Object localObject5 = null;
        ViewRootImpl localViewRootImpl = getViewRootImpl();
        View localView2;
        if (localViewRootImpl != null)
        {
          localObject6 = localViewRootImpl.getAccessibilityFocusedHost();
          if (localObject6 != null)
          {
            localView2 = getAccessibilityFocusedChild((View)localObject6);
            if (localView2 != null)
            {
              if ((bool2) && (!isDirectChildHeaderOrFooter(localView2)))
              {
                localObject7 = localObject4;
                localObject8 = localObject5;
                if (localView2.hasTransientState())
                {
                  localObject7 = localObject4;
                  localObject8 = localObject5;
                  if (!this.mAdapterHasStableIds) {}
                }
              }
              else
              {
                localObject8 = localObject6;
                localObject7 = localViewRootImpl.getAccessibilityFocusedVirtualView();
              }
              n = getPositionForView(localView2);
              localObject4 = localObject8;
              localObject6 = localObject7;
              break label481;
            }
          }
        }
        Object localObject6 = null;
        localObject4 = null;
        n = -1;
        label481:
        Object localObject7 = null;
        Object localObject8 = null;
        localObject5 = getFocusedChild();
        if (localObject5 != null)
        {
          if ((!bool2) || (isDirectChildHeaderOrFooter((View)localObject5)) || (((View)localObject5).hasTransientState()) || (this.mAdapterHasStableIds))
          {
            localView2 = findFocus();
            localObject7 = localObject5;
            localObject8 = localView2;
            if (localView2 != null)
            {
              localView2.dispatchStartTemporaryDetach();
              localObject8 = localView2;
              localObject7 = localObject5;
            }
          }
          requestFocus();
        }
        else
        {
          localObject7 = null;
          localObject8 = null;
        }
        int i1 = this.mFirstPosition;
        localObject5 = this.mRecycler;
        int i2;
        if (bool2) {
          for (i2 = 0; i2 < k; i2++) {
            ((AbsListView.RecycleBin)localObject5).addScrapView(getChildAt(i2), i1 + i2);
          }
        } else {
          ((AbsListView.RecycleBin)localObject5).fillActiveViews(k, i1);
        }
        detachAllViewsFromParent();
        ((AbsListView.RecycleBin)localObject5).removeSkippedScrap();
        switch (this.mLayoutMode)
        {
        default: 
          if (k != 0) {
            break label909;
          }
          bool2 = this.mStackFromBottom;
          break;
        case 6: 
          localObject1 = moveSelection((View)localObject1, localView1, m, i, j);
          break;
        case 5: 
          localObject1 = fillSpecific(this.mSyncPosition, this.mSpecificTop);
          break;
        case 4: 
          m = reconcileSelectedPosition();
          localObject1 = fillSpecific(m, this.mSpecificTop);
          if ((localObject1 == null) && (this.mFocusSelector != null))
          {
            localObject2 = this.mFocusSelector.setupFocusIfValid(m);
            if (localObject2 != null) {
              post((Runnable)localObject2);
            }
          }
          break;
        case 3: 
          localObject1 = fillUp(this.mItemCount - 1, j);
          adjustViewsUpOrDown();
          break;
        case 2: 
          if (localView1 != null) {
            localObject1 = fillFromSelection(localView1.getTop(), i, j);
          } else {
            localObject1 = fillFromMiddle(i, j);
          }
          break;
        case 1: 
          this.mFirstPosition = 0;
          localObject1 = fillFromTop(i);
          adjustViewsUpOrDown();
          break;
        }
        if (!bool2)
        {
          setSelectedPositionInt(lookForSelectablePosition(0, true));
          localObject1 = fillFromTop(i);
        }
        else
        {
          setSelectedPositionInt(lookForSelectablePosition(this.mItemCount - 1, false));
          localObject1 = fillUp(this.mItemCount - 1, j);
          break label1010;
          label909:
          if ((this.mSelectedPosition >= 0) && (this.mSelectedPosition < this.mItemCount))
          {
            i2 = this.mSelectedPosition;
            if (localObject1 == null) {
              m = i;
            } else {
              m = ((View)localObject1).getTop();
            }
            localObject1 = fillSpecific(i2, m);
          }
          else if (this.mFirstPosition < this.mItemCount)
          {
            m = this.mFirstPosition;
            if (localObject2 != null) {
              i = ((View)localObject2).getTop();
            }
            localObject1 = fillSpecific(m, i);
          }
          else
          {
            localObject1 = fillSpecific(0, i);
          }
        }
        label1010:
        ((AbsListView.RecycleBin)localObject5).scrapActiveViews();
        removeUnusedFixedViews(this.mHeaderViewInfos);
        removeUnusedFixedViews(this.mFooterViewInfos);
        if (localObject1 != null)
        {
          if ((this.mItemsCanFocus) && (hasFocus()) && (!((View)localObject1).hasFocus()))
          {
            if (((localObject1 == localObject7) && (localObject8 != null) && (((View)localObject8).requestFocus())) || (((View)localObject1).requestFocus())) {
              m = 1;
            } else {
              m = 0;
            }
            if (m == 0)
            {
              localObject2 = getFocusedChild();
              if (localObject2 != null) {
                ((View)localObject2).clearFocus();
              }
              positionSelector(-1, (View)localObject1);
            }
            else
            {
              ((View)localObject1).setSelected(false);
              this.mSelectorRect.setEmpty();
            }
          }
          else
          {
            positionSelector(-1, (View)localObject1);
          }
          this.mSelectedTop = ((View)localObject1).getTop();
        }
        else
        {
          if ((this.mTouchMode != 1) && (this.mTouchMode != 2)) {
            m = 0;
          } else {
            m = 1;
          }
          if (m != 0)
          {
            localObject1 = getChildAt(this.mMotionPosition - this.mFirstPosition);
            if (localObject1 != null) {
              positionSelector(this.mMotionPosition, (View)localObject1);
            }
          }
          else if (this.mSelectorPosition != -1)
          {
            localObject1 = getChildAt(this.mSelectorPosition - this.mFirstPosition);
            if (localObject1 != null) {
              positionSelector(this.mSelectorPosition, (View)localObject1);
            }
          }
          else
          {
            this.mSelectedTop = 0;
            this.mSelectorRect.setEmpty();
          }
          if ((hasFocus()) && (localObject8 != null)) {
            ((View)localObject8).requestFocus();
          }
        }
        if (localViewRootImpl != null) {
          if (localViewRootImpl.getAccessibilityFocusedHost() == null)
          {
            if ((localObject4 != null) && (((View)localObject4).isAttachedToWindow()))
            {
              localObject1 = ((View)localObject4).getAccessibilityNodeProvider();
              if ((localObject6 != null) && (localObject1 != null)) {
                ((AccessibilityNodeProvider)localObject1).performAction(AccessibilityNodeInfo.getVirtualDescendantId(((AccessibilityNodeInfo)localObject6).getSourceNodeId()), 64, null);
              } else {
                ((View)localObject4).requestAccessibilityFocus();
              }
            }
            else if (n != -1)
            {
              localObject1 = getChildAt(MathUtils.constrain(n - this.mFirstPosition, 0, getChildCount() - 1));
              if (localObject1 != null) {
                ((View)localObject1).requestAccessibilityFocus();
              }
            }
          }
          else {}
        }
        if ((localObject8 != null) && (((View)localObject8).getWindowToken() != null)) {
          ((View)localObject8).dispatchFinishTemporaryDetach();
        }
        this.mLayoutMode = 0;
        this.mDataChanged = false;
        if (this.mPositionScrollAfterLayout != null)
        {
          post(this.mPositionScrollAfterLayout);
          this.mPositionScrollAfterLayout = null;
        }
        this.mNeedSync = false;
        setNextSelectedPositionInt(this.mSelectedPosition);
        updateScrollIndicators();
        if (this.mItemCount > 0) {
          checkSelectionChanged();
        }
        invokeOnItemScrollListener();
        return;
      }
      localObject2 = new java/lang/IllegalStateException;
      localObject1 = new java/lang/StringBuilder;
      ((StringBuilder)localObject1).<init>();
      ((StringBuilder)localObject1).append("The content of the adapter has changed but ListView did not receive a notification. Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. Make sure your adapter calls notifyDataSetChanged() when its content changes. [in ListView(");
      ((StringBuilder)localObject1).append(getId());
      ((StringBuilder)localObject1).append(", ");
      ((StringBuilder)localObject1).append(getClass());
      ((StringBuilder)localObject1).append(") with Adapter(");
      ((StringBuilder)localObject1).append(this.mAdapter.getClass());
      ((StringBuilder)localObject1).append(")]");
      ((IllegalStateException)localObject2).<init>(((StringBuilder)localObject1).toString());
      throw ((Throwable)localObject2);
    }
    finally
    {
      Object localObject1 = this.mFocusSelector;
      if (localObject1 != null) {
        ((FocusSelector)localObject1).onLayoutComplete();
      }
      if (!bool1) {
        this.mBlockLayoutRequests = false;
      }
    }
  }
  
  boolean pageScroll(int paramInt)
  {
    boolean bool;
    if (paramInt == 33)
    {
      paramInt = Math.max(0, this.mSelectedPosition - getChildCount() - 1);
      bool = false;
    }
    else
    {
      if (paramInt != 130) {
        break label157;
      }
      paramInt = Math.min(this.mItemCount - 1, this.mSelectedPosition + getChildCount() - 1);
      bool = true;
    }
    if (paramInt >= 0)
    {
      paramInt = lookForSelectablePositionAfter(this.mSelectedPosition, paramInt, bool);
      if (paramInt >= 0)
      {
        this.mLayoutMode = 4;
        this.mSpecificTop = (this.mPaddingTop + getVerticalFadingEdgeLength());
        if ((bool) && (paramInt > this.mItemCount - getChildCount())) {
          this.mLayoutMode = 3;
        }
        if ((!bool) && (paramInt < getChildCount())) {
          this.mLayoutMode = 1;
        }
        setSelectionInt(paramInt);
        invokeOnItemScrollListener();
        if (!awakenScrollBars()) {
          invalidate();
        }
        return true;
      }
    }
    return false;
    label157:
    return false;
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (paramInt == 16908343)
    {
      paramInt = paramBundle.getInt("android.view.accessibility.action.ARGUMENT_ROW_INT", -1);
      int i = Math.min(paramInt, getCount() - 1);
      if (paramInt >= 0)
      {
        smoothScrollToPosition(i);
        return true;
      }
    }
    return false;
  }
  
  @ViewDebug.ExportedProperty(category="list")
  protected boolean recycleOnMeasure()
  {
    return true;
  }
  
  public boolean removeFooterView(View paramView)
  {
    if (this.mFooterViewInfos.size() > 0)
    {
      boolean bool1 = false;
      boolean bool2 = bool1;
      if (this.mAdapter != null)
      {
        bool2 = bool1;
        if (((HeaderViewListAdapter)this.mAdapter).removeFooter(paramView))
        {
          if (this.mDataSetObserver != null) {
            this.mDataSetObserver.onChanged();
          }
          bool2 = true;
        }
      }
      removeFixedViewInfo(paramView, this.mFooterViewInfos);
      return bool2;
    }
    return false;
  }
  
  public boolean removeHeaderView(View paramView)
  {
    if (this.mHeaderViewInfos.size() > 0)
    {
      boolean bool1 = false;
      boolean bool2 = bool1;
      if (this.mAdapter != null)
      {
        bool2 = bool1;
        if (((HeaderViewListAdapter)this.mAdapter).removeHeader(paramView))
        {
          if (this.mDataSetObserver != null) {
            this.mDataSetObserver.onChanged();
          }
          bool2 = true;
        }
      }
      removeFixedViewInfo(paramView, this.mHeaderViewInfos);
      return bool2;
    }
    return false;
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    int i = paramRect.top;
    paramRect.offset(paramView.getLeft(), paramView.getTop());
    paramRect.offset(-paramView.getScrollX(), -paramView.getScrollY());
    int j = getHeight();
    int k = getScrollY();
    int m = k + j;
    int n = getVerticalFadingEdgeLength();
    int i1 = k;
    if (showingTopFadingEdge()) {
      if (this.mSelectedPosition <= 0)
      {
        i1 = k;
        if (i <= n) {}
      }
      else
      {
        i1 = k + n;
      }
    }
    i = getChildAt(getChildCount() - 1).getBottom();
    boolean bool = showingBottomFadingEdge();
    paramBoolean = true;
    k = m;
    if (bool) {
      if (this.mSelectedPosition >= this.mItemCount - 1)
      {
        k = m;
        if (paramRect.bottom >= i - n) {}
      }
      else
      {
        k = m - n;
      }
    }
    n = 0;
    if ((paramRect.bottom > k) && (paramRect.top > i1))
    {
      if (paramRect.height() > j) {
        m = 0 + (paramRect.top - i1);
      } else {
        m = 0 + (paramRect.bottom - k);
      }
      m = Math.min(m, i - k);
    }
    for (;;)
    {
      break;
      m = n;
      if (paramRect.top < i1)
      {
        m = n;
        if (paramRect.bottom < k)
        {
          if (paramRect.height() > j) {
            m = 0 - (k - paramRect.bottom);
          } else {
            m = 0 - (i1 - paramRect.top);
          }
          m = Math.max(m, getChildAt(0).getTop() - i1);
        }
      }
    }
    if (m == 0) {
      paramBoolean = false;
    }
    if (paramBoolean)
    {
      scrollListItemsBy(-m);
      positionSelector(-1, paramView);
      this.mSelectedTop = paramView.getTop();
      invalidate();
    }
    return paramBoolean;
  }
  
  void resetList()
  {
    clearRecycledState(this.mHeaderViewInfos);
    clearRecycledState(this.mFooterViewInfos);
    super.resetList();
    this.mLayoutMode = 0;
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    if ((this.mAdapter != null) && (this.mDataSetObserver != null)) {
      this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
    }
    resetList();
    this.mRecycler.clear();
    if ((this.mHeaderViewInfos.size() <= 0) && (this.mFooterViewInfos.size() <= 0)) {
      this.mAdapter = paramListAdapter;
    } else {
      this.mAdapter = wrapHeaderListAdapterInternal(this.mHeaderViewInfos, this.mFooterViewInfos, paramListAdapter);
    }
    this.mOldSelectedPosition = -1;
    this.mOldSelectedRowId = Long.MIN_VALUE;
    super.setAdapter(paramListAdapter);
    if (this.mAdapter != null)
    {
      this.mAreAllItemsSelectable = this.mAdapter.areAllItemsEnabled();
      this.mOldItemCount = this.mItemCount;
      this.mItemCount = this.mAdapter.getCount();
      checkFocus();
      this.mDataSetObserver = new AbsListView.AdapterDataSetObserver(this);
      this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
      this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());
      int i;
      if (this.mStackFromBottom) {
        i = lookForSelectablePosition(this.mItemCount - 1, false);
      } else {
        i = lookForSelectablePosition(0, true);
      }
      setSelectedPositionInt(i);
      setNextSelectedPositionInt(i);
      if (this.mItemCount == 0) {
        checkSelectionChanged();
      }
    }
    else
    {
      this.mAreAllItemsSelectable = true;
      checkFocus();
      checkSelectionChanged();
    }
    requestLayout();
  }
  
  public void setCacheColorHint(int paramInt)
  {
    boolean bool;
    if (paramInt >>> 24 == 255) {
      bool = true;
    } else {
      bool = false;
    }
    this.mIsCacheColorOpaque = bool;
    if (bool)
    {
      if (this.mDividerPaint == null) {
        this.mDividerPaint = new Paint();
      }
      this.mDividerPaint.setColor(paramInt);
    }
    super.setCacheColorHint(paramInt);
  }
  
  public void setDivider(Drawable paramDrawable)
  {
    boolean bool = false;
    if (paramDrawable != null) {
      this.mDividerHeight = paramDrawable.getIntrinsicHeight();
    } else {
      this.mDividerHeight = 0;
    }
    this.mDivider = paramDrawable;
    if ((paramDrawable == null) || (paramDrawable.getOpacity() == -1)) {
      bool = true;
    }
    this.mDividerIsOpaque = bool;
    requestLayout();
    invalidate();
  }
  
  public void setDividerHeight(int paramInt)
  {
    this.mDividerHeight = paramInt;
    requestLayout();
    invalidate();
  }
  
  public void setFooterDividersEnabled(boolean paramBoolean)
  {
    this.mFooterDividersEnabled = paramBoolean;
    invalidate();
  }
  
  public void setHeaderDividersEnabled(boolean paramBoolean)
  {
    this.mHeaderDividersEnabled = paramBoolean;
    invalidate();
  }
  
  public void setItemsCanFocus(boolean paramBoolean)
  {
    this.mItemsCanFocus = paramBoolean;
    if (!paramBoolean) {
      setDescendantFocusability(393216);
    }
  }
  
  public void setOverscrollFooter(Drawable paramDrawable)
  {
    this.mOverScrollFooter = paramDrawable;
    invalidate();
  }
  
  public void setOverscrollHeader(Drawable paramDrawable)
  {
    this.mOverScrollHeader = paramDrawable;
    if (this.mScrollY < 0) {
      invalidate();
    }
  }
  
  @RemotableViewMethod(asyncImpl="setRemoteViewsAdapterAsync")
  public void setRemoteViewsAdapter(Intent paramIntent)
  {
    super.setRemoteViewsAdapter(paramIntent);
  }
  
  public void setSelection(int paramInt)
  {
    setSelectionFromTop(paramInt, 0);
  }
  
  public void setSelectionAfterHeaderView()
  {
    int i = getHeaderViewsCount();
    if (i > 0)
    {
      this.mNextSelectedPosition = 0;
      return;
    }
    if (this.mAdapter != null)
    {
      setSelection(i);
    }
    else
    {
      this.mNextSelectedPosition = i;
      this.mLayoutMode = 2;
    }
  }
  
  @UnsupportedAppUsage
  void setSelectionInt(int paramInt)
  {
    setNextSelectedPositionInt(paramInt);
    int i = 0;
    int j = this.mSelectedPosition;
    int k = i;
    if (j >= 0) {
      if (paramInt == j - 1)
      {
        k = 1;
      }
      else
      {
        k = i;
        if (paramInt == j + 1) {
          k = 1;
        }
      }
    }
    if (this.mPositionScroller != null) {
      this.mPositionScroller.stop();
    }
    layoutChildren();
    if (k != 0) {
      awakenScrollBars();
    }
  }
  
  @RemotableViewMethod
  public void smoothScrollByOffset(int paramInt)
  {
    super.smoothScrollByOffset(paramInt);
  }
  
  @RemotableViewMethod
  public void smoothScrollToPosition(int paramInt)
  {
    super.smoothScrollToPosition(paramInt);
  }
  
  @UnsupportedAppUsage
  boolean trackMotionScroll(int paramInt1, int paramInt2)
  {
    boolean bool = super.trackMotionScroll(paramInt1, paramInt2);
    removeUnusedFixedViews(this.mHeaderViewInfos);
    removeUnusedFixedViews(this.mFooterViewInfos);
    return bool;
  }
  
  protected HeaderViewListAdapter wrapHeaderListAdapterInternal(ArrayList<FixedViewInfo> paramArrayList1, ArrayList<FixedViewInfo> paramArrayList2, ListAdapter paramListAdapter)
  {
    return new HeaderViewListAdapter(paramArrayList1, paramArrayList2, paramListAdapter);
  }
  
  protected void wrapHeaderListAdapterInternal()
  {
    this.mAdapter = wrapHeaderListAdapterInternal(this.mHeaderViewInfos, this.mFooterViewInfos, this.mAdapter);
  }
  
  private static class ArrowScrollFocusResult
  {
    private int mAmountToScroll;
    private int mSelectedPosition;
    
    public int getAmountToScroll()
    {
      return this.mAmountToScroll;
    }
    
    public int getSelectedPosition()
    {
      return this.mSelectedPosition;
    }
    
    void populate(int paramInt1, int paramInt2)
    {
      this.mSelectedPosition = paramInt1;
      this.mAmountToScroll = paramInt2;
    }
  }
  
  public class FixedViewInfo
  {
    public Object data;
    public boolean isSelectable;
    public View view;
    
    public FixedViewInfo() {}
  }
  
  private class FocusSelector
    implements Runnable
  {
    private static final int STATE_REQUEST_FOCUS = 3;
    private static final int STATE_SET_SELECTION = 1;
    private static final int STATE_WAIT_FOR_LAYOUT = 2;
    private int mAction;
    private int mPosition;
    private int mPositionTop;
    
    private FocusSelector() {}
    
    void onLayoutComplete()
    {
      if (this.mAction == 2) {
        this.mAction = -1;
      }
    }
    
    public void run()
    {
      int i = this.mAction;
      if (i == 1)
      {
        ListView.this.setSelectionFromTop(this.mPosition, this.mPositionTop);
        this.mAction = 2;
      }
      else if (i == 3)
      {
        i = this.mPosition;
        int j = ListView.this.mFirstPosition;
        View localView = ListView.this.getChildAt(i - j);
        if (localView != null) {
          localView.requestFocus();
        }
        this.mAction = -1;
      }
    }
    
    Runnable setupFocusIfValid(int paramInt)
    {
      if ((this.mAction == 2) && (paramInt == this.mPosition))
      {
        this.mAction = 3;
        return this;
      }
      return null;
    }
    
    FocusSelector setupForSetSelection(int paramInt1, int paramInt2)
    {
      this.mPosition = paramInt1;
      this.mPositionTop = paramInt2;
      this.mAction = 1;
      return this;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */