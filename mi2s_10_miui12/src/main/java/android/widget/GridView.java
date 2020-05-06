package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.util.SparseBooleanArray;
import android.view.Gravity;
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
import android.view.animation.GridLayoutAnimationController.AnimationParameters;
import com.android.internal.R.styleable;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_Widget_GridView.Extension;
import com.miui.internal.variable.api.v29.Android_Widget_GridView.Interface;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RemoteViews.RemoteView
public class GridView
  extends AbsListView
{
  public static final int AUTO_FIT = -1;
  public static final int NO_STRETCH = 0;
  public static final int STRETCH_COLUMN_WIDTH = 2;
  public static final int STRETCH_SPACING = 1;
  public static final int STRETCH_SPACING_UNIFORM = 3;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=117521079L)
  private int mColumnWidth;
  private int mGravity = 8388611;
  @UnsupportedAppUsage
  private int mHorizontalSpacing = 0;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=117521080L)
  private int mNumColumns = -1;
  private View mReferenceView = null;
  private View mReferenceViewInSelectedRow = null;
  @UnsupportedAppUsage
  private int mRequestedColumnWidth;
  @UnsupportedAppUsage
  private int mRequestedHorizontalSpacing;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769395L)
  private int mRequestedNumColumns;
  private int mStretchMode = 2;
  private final Rect mTempRect = new Rect();
  @UnsupportedAppUsage
  private int mVerticalSpacing = 0;
  
  static
  {
    Android_Widget_GridView.Extension.get().bindOriginal(new Android_Widget_GridView.Interface()
    {
      public void fillGap(GridView paramAnonymousGridView, boolean paramAnonymousBoolean)
      {
        paramAnonymousGridView.originalFillGap(paramAnonymousBoolean);
      }
      
      public void layoutChildren(GridView paramAnonymousGridView)
      {
        paramAnonymousGridView.originalLayoutChildren();
      }
    });
  }
  
  public GridView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public GridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842865);
  }
  
  public GridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public GridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.GridView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.GridView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    setHorizontalSpacing(localTypedArray.getDimensionPixelOffset(1, 0));
    setVerticalSpacing(localTypedArray.getDimensionPixelOffset(2, 0));
    paramInt1 = localTypedArray.getInt(3, 2);
    if (paramInt1 >= 0) {
      setStretchMode(paramInt1);
    }
    paramInt1 = localTypedArray.getDimensionPixelOffset(4, -1);
    if (paramInt1 > 0) {
      setColumnWidth(paramInt1);
    }
    setNumColumns(localTypedArray.getInt(5, 1));
    paramInt1 = localTypedArray.getInt(0, -1);
    if (paramInt1 >= 0) {
      setGravity(paramInt1);
    }
    localTypedArray.recycle();
  }
  
  private void adjustForBottomFadingEdge(View paramView, int paramInt1, int paramInt2)
  {
    if (paramView.getBottom() > paramInt2) {
      offsetChildrenTopAndBottom(-Math.min(paramView.getTop() - paramInt1, paramView.getBottom() - paramInt2));
    }
  }
  
  private void adjustForTopFadingEdge(View paramView, int paramInt1, int paramInt2)
  {
    if (paramView.getTop() < paramInt1) {
      offsetChildrenTopAndBottom(Math.min(paramInt1 - paramView.getTop(), paramInt2 - paramView.getBottom()));
    }
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
          k = j - this.mVerticalSpacing;
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
          k = j + this.mVerticalSpacing;
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
  
  private boolean commonKey(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    if (this.mAdapter == null) {
      return false;
    }
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
    if (!bool2)
    {
      bool1 = bool2;
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
                  switch (paramInt1)
                  {
                  default: 
                    bool1 = bool2;
                    break;
                  case 22: 
                    bool1 = bool2;
                    if (!paramKeyEvent.hasNoModifiers()) {
                      break;
                    }
                    if ((!resurrectSelectionIfNeeded()) && (!arrowScroll(66))) {
                      bool1 = false;
                    } else {
                      bool1 = true;
                    }
                    break;
                  case 21: 
                    bool1 = bool2;
                    if (!paramKeyEvent.hasNoModifiers()) {
                      break;
                    }
                    if ((!resurrectSelectionIfNeeded()) && (!arrowScroll(17))) {
                      bool1 = false;
                    } else {
                      bool1 = true;
                    }
                    break;
                  case 20: 
                    if (paramKeyEvent.hasNoModifiers())
                    {
                      if ((!resurrectSelectionIfNeeded()) && (!arrowScroll(130))) {
                        bool1 = false;
                      } else {
                        bool1 = true;
                      }
                    }
                    else
                    {
                      bool1 = bool2;
                      if (paramKeyEvent.hasModifiers(2)) {
                        if ((!resurrectSelectionIfNeeded()) && (!fullScroll(130))) {
                          bool1 = false;
                        } else {
                          bool1 = true;
                        }
                      }
                    }
                    break;
                  case 19: 
                    if (paramKeyEvent.hasNoModifiers())
                    {
                      if ((!resurrectSelectionIfNeeded()) && (!arrowScroll(33))) {
                        bool1 = false;
                      } else {
                        bool1 = true;
                      }
                    }
                    else
                    {
                      bool1 = bool2;
                      if (paramKeyEvent.hasModifiers(2)) {
                        if ((!resurrectSelectionIfNeeded()) && (!fullScroll(33))) {
                          bool1 = false;
                        } else {
                          bool1 = true;
                        }
                      }
                    }
                    break;
                  }
                }
                else
                {
                  bool1 = bool2;
                  if (paramKeyEvent.hasNoModifiers()) {
                    if ((!resurrectSelectionIfNeeded()) && (!fullScroll(130))) {
                      bool1 = false;
                    } else {
                      bool1 = true;
                    }
                  }
                }
              }
              else
              {
                bool1 = bool2;
                if (paramKeyEvent.hasNoModifiers()) {
                  if ((!resurrectSelectionIfNeeded()) && (!fullScroll(33))) {
                    bool1 = false;
                  } else {
                    bool1 = true;
                  }
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
            }
            else
            {
              bool1 = bool2;
              if (paramKeyEvent.hasModifiers(2)) {
                if ((!resurrectSelectionIfNeeded()) && (!fullScroll(130))) {
                  bool1 = false;
                } else {
                  bool1 = true;
                }
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
          }
          else
          {
            bool1 = bool2;
            if (paramKeyEvent.hasModifiers(2)) {
              if ((!resurrectSelectionIfNeeded()) && (!fullScroll(33))) {
                bool1 = false;
              } else {
                bool1 = true;
              }
            }
          }
        }
        else if (paramKeyEvent.hasNoModifiers())
        {
          if ((!resurrectSelectionIfNeeded()) && (!sequenceScroll(2))) {
            bool1 = false;
          } else {
            bool1 = true;
          }
        }
        else
        {
          bool1 = bool2;
          if (paramKeyEvent.hasModifiers(1)) {
            if ((!resurrectSelectionIfNeeded()) && (!sequenceScroll(1))) {
              bool1 = false;
            } else {
              bool1 = true;
            }
          }
        }
      }
    }
    if (bool1) {
      return true;
    }
    if (sendToTextFilter(paramInt1, paramInt2, paramKeyEvent)) {
      return true;
    }
    if (i != 0)
    {
      if (i != 1)
      {
        if (i != 2) {
          return false;
        }
        return super.onKeyMultiple(paramInt1, paramInt2, paramKeyEvent);
      }
      return super.onKeyUp(paramInt1, paramKeyEvent);
    }
    return super.onKeyDown(paramInt1, paramKeyEvent);
  }
  
  private void correctTooHigh(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = this.mFirstPosition;
    int j = 1;
    if ((i + paramInt3 - 1 == this.mItemCount - 1) && (paramInt3 > 0))
    {
      paramInt3 = getChildAt(paramInt3 - 1).getBottom();
      i = this.mBottom - this.mTop - this.mListPadding.bottom - paramInt3;
      View localView = getChildAt(0);
      int k = localView.getTop();
      if ((i > 0) && ((this.mFirstPosition > 0) || (k < this.mListPadding.top)))
      {
        paramInt3 = i;
        if (this.mFirstPosition == 0) {
          paramInt3 = Math.min(i, this.mListPadding.top - k);
        }
        offsetChildrenTopAndBottom(paramInt3);
        if (this.mFirstPosition > 0)
        {
          paramInt3 = this.mFirstPosition;
          if (this.mStackFromBottom) {
            paramInt1 = j;
          }
          fillUp(paramInt3 - paramInt1, localView.getTop() - paramInt2);
          adjustViewsUpOrDown();
        }
      }
    }
  }
  
  private void correctTooLow(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.mFirstPosition == 0) && (paramInt3 > 0))
    {
      int i = getChildAt(0).getTop();
      int j = this.mListPadding.top;
      int k = this.mBottom - this.mTop - this.mListPadding.bottom;
      j = i - j;
      View localView = getChildAt(paramInt3 - 1);
      int m = localView.getBottom();
      int n = this.mFirstPosition;
      i = 1;
      n = n + paramInt3 - 1;
      if ((j > 0) && ((n < this.mItemCount - 1) || (m > k)))
      {
        paramInt3 = j;
        if (n == this.mItemCount - 1) {
          paramInt3 = Math.min(j, m - k);
        }
        offsetChildrenTopAndBottom(-paramInt3);
        if (n < this.mItemCount - 1)
        {
          if (!this.mStackFromBottom) {
            paramInt1 = i;
          }
          fillDown(paramInt1 + n, localView.getBottom() + paramInt2);
          adjustViewsUpOrDown();
        }
      }
    }
  }
  
  @UnsupportedAppUsage
  private boolean determineColumns(int paramInt)
  {
    int i = this.mRequestedHorizontalSpacing;
    int j = this.mStretchMode;
    int k = this.mRequestedColumnWidth;
    boolean bool1 = false;
    boolean bool2 = false;
    int m = this.mRequestedNumColumns;
    if (m == -1)
    {
      if (k > 0) {
        this.mNumColumns = ((paramInt + i) / (k + i));
      } else {
        this.mNumColumns = 2;
      }
    }
    else {
      this.mNumColumns = m;
    }
    if (this.mNumColumns <= 0) {
      this.mNumColumns = 1;
    }
    if (j != 0)
    {
      m = this.mNumColumns;
      paramInt = paramInt - m * k - (m - 1) * i;
      if (paramInt < 0) {
        bool2 = true;
      }
      if (j != 1)
      {
        if (j != 2)
        {
          if (j == 3)
          {
            this.mColumnWidth = k;
            j = this.mNumColumns;
            if (j > 1) {
              this.mHorizontalSpacing = (paramInt / (j + 1) + i);
            } else {
              this.mHorizontalSpacing = (i + paramInt);
            }
          }
        }
        else
        {
          this.mColumnWidth = (paramInt / this.mNumColumns + k);
          this.mHorizontalSpacing = i;
        }
      }
      else
      {
        this.mColumnWidth = k;
        j = this.mNumColumns;
        if (j > 1) {
          this.mHorizontalSpacing = (paramInt / (j - 1) + i);
        } else {
          this.mHorizontalSpacing = (i + paramInt);
        }
      }
    }
    else
    {
      this.mColumnWidth = k;
      this.mHorizontalSpacing = i;
      bool2 = bool1;
    }
    return bool2;
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
    while ((m < j) && (k < this.mItemCount))
    {
      localView1 = makeRow(k, m, true);
      if (localView1 != null) {
        localView2 = localView1;
      }
      m = this.mReferenceView.getBottom() + this.mVerticalSpacing;
      k += this.mNumColumns;
    }
    setVisibleRangeHint(this.mFirstPosition, this.mFirstPosition + getChildCount() - 1);
    return localView2;
  }
  
  private View fillFromBottom(int paramInt1, int paramInt2)
  {
    paramInt1 = Math.min(Math.max(paramInt1, this.mSelectedPosition), this.mItemCount - 1);
    paramInt1 = this.mItemCount - 1 - paramInt1;
    return fillUp(this.mItemCount - 1 - (paramInt1 - paramInt1 % this.mNumColumns), paramInt2);
  }
  
  private View fillFromSelection(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = getVerticalFadingEdgeLength();
    int j = this.mSelectedPosition;
    int k = this.mNumColumns;
    int m = this.mVerticalSpacing;
    int n = -1;
    if (!this.mStackFromBottom)
    {
      j -= j % k;
    }
    else
    {
      j = this.mItemCount - 1 - j;
      n = this.mItemCount - 1 - (j - j % k);
      j = Math.max(0, n - k + 1);
    }
    int i1 = getTopSelectionPixel(paramInt2, i, j);
    paramInt3 = getBottomSelectionPixel(paramInt3, i, k, j);
    if (this.mStackFromBottom) {
      paramInt2 = n;
    } else {
      paramInt2 = j;
    }
    View localView1 = makeRow(paramInt2, paramInt1, true);
    this.mFirstPosition = j;
    View localView2 = this.mReferenceView;
    adjustForTopFadingEdge(localView2, i1, paramInt3);
    adjustForBottomFadingEdge(localView2, i1, paramInt3);
    if (!this.mStackFromBottom)
    {
      fillUp(j - k, localView2.getTop() - m);
      adjustViewsUpOrDown();
      fillDown(j + k, localView2.getBottom() + m);
    }
    else
    {
      fillDown(n + k, localView2.getBottom() + m);
      adjustViewsUpOrDown();
      fillUp(j - 1, localView2.getTop() - m);
    }
    return localView1;
  }
  
  private View fillFromTop(int paramInt)
  {
    this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
    this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
    if (this.mFirstPosition < 0) {
      this.mFirstPosition = 0;
    }
    this.mFirstPosition -= this.mFirstPosition % this.mNumColumns;
    return fillDown(this.mFirstPosition, paramInt);
  }
  
  private View fillSelection(int paramInt1, int paramInt2)
  {
    int i = reconcileSelectedPosition();
    int j = this.mNumColumns;
    int k = this.mVerticalSpacing;
    int m = -1;
    if (!this.mStackFromBottom)
    {
      i -= i % j;
    }
    else
    {
      i = this.mItemCount - 1 - i;
      m = this.mItemCount - 1 - (i - i % j);
      i = Math.max(0, m - j + 1);
    }
    int n = getVerticalFadingEdgeLength();
    int i1 = getTopSelectionPixel(paramInt1, n, i);
    int i2;
    if (this.mStackFromBottom) {
      i2 = m;
    } else {
      i2 = i;
    }
    View localView1 = makeRow(i2, i1, true);
    this.mFirstPosition = i;
    View localView2 = this.mReferenceView;
    if (!this.mStackFromBottom)
    {
      fillDown(i + j, localView2.getBottom() + k);
      pinToBottom(paramInt2);
      fillUp(i - j, localView2.getTop() - k);
      adjustViewsUpOrDown();
    }
    else
    {
      offsetChildrenTopAndBottom(getBottomSelectionPixel(paramInt2, n, j, i) - localView2.getBottom());
      fillUp(i - 1, localView2.getTop() - k);
      pinToTop(paramInt1);
      fillDown(m + j, localView2.getBottom() + k);
      adjustViewsUpOrDown();
    }
    return localView1;
  }
  
  private View fillSpecific(int paramInt1, int paramInt2)
  {
    int i = this.mNumColumns;
    int j = -1;
    int k;
    if (!this.mStackFromBottom)
    {
      k = paramInt1 - paramInt1 % i;
      paramInt1 = j;
      j = k;
    }
    else
    {
      paramInt1 = this.mItemCount - 1 - paramInt1;
      paramInt1 = this.mItemCount - 1 - (paramInt1 - paramInt1 % i);
      j = Math.max(0, paramInt1 - i + 1);
    }
    if (this.mStackFromBottom) {
      k = paramInt1;
    } else {
      k = j;
    }
    View localView1 = makeRow(k, paramInt2, true);
    this.mFirstPosition = j;
    Object localObject1 = this.mReferenceView;
    if (localObject1 == null) {
      return null;
    }
    paramInt2 = this.mVerticalSpacing;
    Object localObject2;
    if (!this.mStackFromBottom)
    {
      localObject2 = fillUp(j - i, ((View)localObject1).getTop() - paramInt2);
      adjustViewsUpOrDown();
      localObject1 = fillDown(j + i, ((View)localObject1).getBottom() + paramInt2);
      paramInt1 = getChildCount();
      if (paramInt1 > 0) {
        correctTooHigh(i, paramInt2, paramInt1);
      }
    }
    else
    {
      View localView2 = fillDown(paramInt1 + i, ((View)localObject1).getBottom() + paramInt2);
      adjustViewsUpOrDown();
      View localView3 = fillUp(j - 1, ((View)localObject1).getTop() - paramInt2);
      paramInt1 = getChildCount();
      localObject2 = localView3;
      localObject1 = localView2;
      if (paramInt1 > 0)
      {
        correctTooLow(i, paramInt2, paramInt1);
        localObject1 = localView2;
        localObject2 = localView3;
      }
    }
    if (localView1 != null) {
      return localView1;
    }
    if (localObject2 != null) {
      return (View)localObject2;
    }
    return (View)localObject1;
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
    while ((k > i) && (j >= 0))
    {
      localView1 = makeRow(j, k, false);
      if (localView1 != null) {
        localView2 = localView1;
      }
      k = this.mReferenceView.getTop() - this.mVerticalSpacing;
      this.mFirstPosition = j;
      j -= this.mNumColumns;
    }
    if (this.mStackFromBottom) {
      this.mFirstPosition = Math.max(0, j + 1);
    }
    setVisibleRangeHint(this.mFirstPosition, this.mFirstPosition + getChildCount() - 1);
    return localView2;
  }
  
  private int getBottomSelectionPixel(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt1;
    paramInt1 = i;
    if (paramInt4 + paramInt3 - 1 < this.mItemCount - 1) {
      paramInt1 = i - paramInt2;
    }
    return paramInt1;
  }
  
  private int getTopSelectionPixel(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1;
    paramInt1 = i;
    if (paramInt3 > 0) {
      paramInt1 = i + paramInt2;
    }
    return paramInt1;
  }
  
  private boolean isCandidateSelection(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    int j = i - 1 - paramInt1;
    boolean bool1 = this.mStackFromBottom;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    boolean bool6 = false;
    boolean bool7 = false;
    int k;
    if (!bool1)
    {
      j = this.mNumColumns;
      k = paramInt1 - paramInt1 % j;
      j = Math.min(j + k - 1, i);
    }
    else
    {
      k = this.mNumColumns;
      j = i - 1 - (j - j % k);
      k = Math.max(0, j - k + 1);
    }
    if (paramInt2 != 1)
    {
      if (paramInt2 != 2)
      {
        if (paramInt2 != 17)
        {
          if (paramInt2 != 33)
          {
            if (paramInt2 != 66)
            {
              if (paramInt2 == 130)
              {
                if (k == 0) {
                  bool7 = true;
                }
                return bool7;
              }
              throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
            }
            bool7 = bool2;
            if (paramInt1 == k) {
              bool7 = true;
            }
            return bool7;
          }
          bool7 = bool3;
          if (j == i - 1) {
            bool7 = true;
          }
          return bool7;
        }
        bool7 = bool4;
        if (paramInt1 == j) {
          bool7 = true;
        }
        return bool7;
      }
      bool7 = bool5;
      if (paramInt1 == k)
      {
        bool7 = bool5;
        if (k == 0) {
          bool7 = true;
        }
      }
      return bool7;
    }
    bool7 = bool6;
    if (paramInt1 == j)
    {
      bool7 = bool6;
      if (j == i - 1) {
        bool7 = true;
      }
    }
    return bool7;
  }
  
  private View makeAndAddView(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, int paramInt4)
  {
    if (!this.mDataChanged)
    {
      localView = this.mRecycler.getActiveView(paramInt1);
      if (localView != null)
      {
        setupChild(localView, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, true, paramInt4);
        return localView;
      }
    }
    View localView = obtainView(paramInt1, this.mIsScrap);
    setupChild(localView, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, this.mIsScrap[0], paramInt4);
    return localView;
  }
  
  private View makeRow(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = this.mColumnWidth;
    int j = this.mHorizontalSpacing;
    boolean bool1 = isLayoutRtl();
    int k;
    int m;
    int n;
    if (bool1)
    {
      k = getWidth();
      m = this.mListPadding.right;
      if (this.mStretchMode == 3) {
        n = j;
      } else {
        n = 0;
      }
      m = k - m - i - n;
    }
    else
    {
      m = this.mListPadding.left;
      if (this.mStretchMode == 3) {
        n = j;
      } else {
        n = 0;
      }
      m += n;
    }
    if (!this.mStackFromBottom)
    {
      n = Math.min(paramInt1 + this.mNumColumns, this.mItemCount);
    }
    else
    {
      n = paramInt1 + 1;
      paramInt1 = Math.max(0, paramInt1 - this.mNumColumns + 1);
      i1 = this.mNumColumns;
      if (n - paramInt1 < i1)
      {
        if (bool1) {
          k = -1;
        } else {
          k = 1;
        }
        m += k * ((i1 - (n - paramInt1)) * (i + j));
      }
    }
    boolean bool2 = shouldShowSelector();
    boolean bool3 = touchModeDrawsInPressedState();
    int i1 = this.mSelectedPosition;
    if (bool1) {
      k = -1;
    } else {
      k = 1;
    }
    Object localObject1 = null;
    int i2 = paramInt1;
    View localView = null;
    while (i2 < n)
    {
      if (i2 == i1) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      if (paramBoolean) {
        i3 = -1;
      } else {
        i3 = i2 - paramInt1;
      }
      localView = makeAndAddView(i2, paramInt2, paramBoolean, m, bool1, i3);
      int i3 = m + k * i;
      m = i3;
      if (i2 < n - 1) {
        m = i3 + k * j;
      }
      Object localObject2 = localObject1;
      if (bool1) {
        if (!bool2)
        {
          localObject2 = localObject1;
          if (!bool3) {}
        }
        else
        {
          localObject2 = localView;
        }
      }
      i2++;
      localObject1 = localObject2;
    }
    this.mReferenceView = localView;
    if (localObject1 != null) {
      this.mReferenceViewInSelectedRow = this.mReferenceView;
    }
    return (View)localObject1;
  }
  
  private View moveSelection(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = getVerticalFadingEdgeLength();
    int j = this.mSelectedPosition;
    int k = this.mNumColumns;
    int m = this.mVerticalSpacing;
    int n = -1;
    boolean bool = this.mStackFromBottom;
    int i1 = 0;
    int i2 = 0;
    int i4;
    if (!bool)
    {
      i3 = j - paramInt1 - (j - paramInt1) % k;
      i4 = j - j % k;
      paramInt1 = n;
    }
    else
    {
      i4 = this.mItemCount - 1 - j;
      n = this.mItemCount - 1 - (i4 - i4 % k);
      i4 = Math.max(0, n - k + 1);
      paramInt1 = this.mItemCount - 1 - (j - paramInt1);
      i3 = Math.max(0, this.mItemCount - 1 - (paramInt1 - paramInt1 % k) - k + 1);
      paramInt1 = n;
    }
    j = i4 - i3;
    int i3 = getTopSelectionPixel(paramInt2, i, i4);
    n = getBottomSelectionPixel(paramInt3, i, k, i4);
    this.mFirstPosition = i4;
    View localView1;
    View localView2;
    if (j > 0)
    {
      localView1 = this.mReferenceViewInSelectedRow;
      if (localView1 == null) {
        paramInt2 = i2;
      } else {
        paramInt2 = localView1.getBottom();
      }
      if (this.mStackFromBottom) {
        paramInt3 = paramInt1;
      } else {
        paramInt3 = i4;
      }
      localView1 = makeRow(paramInt3, paramInt2 + m, true);
      localView2 = this.mReferenceView;
      adjustForBottomFadingEdge(localView2, i3, n);
    }
    else if (j < 0)
    {
      localView1 = this.mReferenceViewInSelectedRow;
      if (localView1 == null) {
        paramInt2 = 0;
      } else {
        paramInt2 = localView1.getTop();
      }
      if (this.mStackFromBottom) {
        paramInt3 = paramInt1;
      } else {
        paramInt3 = i4;
      }
      localView1 = makeRow(paramInt3, paramInt2 - m, false);
      localView2 = this.mReferenceView;
      adjustForTopFadingEdge(localView2, i3, n);
    }
    else
    {
      localView1 = this.mReferenceViewInSelectedRow;
      if (localView1 == null) {
        paramInt2 = i1;
      } else {
        paramInt2 = localView1.getTop();
      }
      if (this.mStackFromBottom) {
        paramInt3 = paramInt1;
      } else {
        paramInt3 = i4;
      }
      localView1 = makeRow(paramInt3, paramInt2, true);
      localView2 = this.mReferenceView;
    }
    if (!this.mStackFromBottom)
    {
      fillUp(i4 - k, localView2.getTop() - m);
      adjustViewsUpOrDown();
      fillDown(i4 + k, localView2.getBottom() + m);
    }
    else
    {
      fillDown(paramInt1 + k, localView2.getBottom() + m);
      adjustViewsUpOrDown();
      fillUp(i4 - 1, localView2.getTop() - m);
    }
    return localView1;
  }
  
  private void pinToBottom(int paramInt)
  {
    int i = getChildCount();
    if (this.mFirstPosition + i == this.mItemCount)
    {
      paramInt -= getChildAt(i - 1).getBottom();
      if (paramInt > 0) {
        offsetChildrenTopAndBottom(paramInt);
      }
    }
  }
  
  private void pinToTop(int paramInt)
  {
    if (this.mFirstPosition == 0)
    {
      paramInt -= getChildAt(0).getTop();
      if (paramInt < 0) {
        offsetChildrenTopAndBottom(paramInt);
      }
    }
  }
  
  private void setupChild(View paramView, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, boolean paramBoolean3, int paramInt4)
  {
    Trace.traceBegin(8L, "setupGridItem");
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
    int k;
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
    if (i != 0)
    {
      paramView.setSelected(paramBoolean2);
      if (paramBoolean2) {
        requestFocus();
      }
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
    if ((paramBoolean3) && (!localLayoutParams2.forceAdd))
    {
      attachViewToParent(paramView, paramInt4, localLayoutParams2);
      if ((!paramBoolean3) || (((AbsListView.LayoutParams)paramView.getLayoutParams()).scrappedFromPosition != paramInt1)) {
        paramView.jumpDrawablesToCurrentState();
      }
    }
    else
    {
      localLayoutParams2.forceAdd = false;
      addViewInLayout(paramView, paramInt4, localLayoutParams2, true);
    }
    if (j != 0)
    {
      paramInt1 = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), 0, localLayoutParams2.height);
      paramView.measure(ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(this.mColumnWidth, 1073741824), 0, localLayoutParams2.width), paramInt1);
    }
    else
    {
      cleanupLayoutState(paramView);
    }
    paramInt4 = paramView.getMeasuredWidth();
    int i = paramView.getMeasuredHeight();
    if (!paramBoolean1) {
      paramInt2 -= i;
    }
    paramInt1 = getLayoutDirection();
    paramInt1 = Gravity.getAbsoluteGravity(this.mGravity, paramInt1) & 0x7;
    if (paramInt1 != 1)
    {
      if (paramInt1 != 3)
      {
        if (paramInt1 != 5) {
          paramInt1 = paramInt3;
        } else {
          paramInt1 = paramInt3 + this.mColumnWidth - paramInt4;
        }
      }
      else {
        paramInt1 = paramInt3;
      }
    }
    else {
      paramInt1 = paramInt3 + (this.mColumnWidth - paramInt4) / 2;
    }
    if (j != 0)
    {
      paramView.layout(paramInt1, paramInt2, paramInt1 + paramInt4, paramInt2 + i);
    }
    else
    {
      paramView.offsetLeftAndRight(paramInt1 - paramView.getLeft());
      paramView.offsetTopAndBottom(paramInt2 - paramView.getTop());
    }
    if ((this.mCachingStarted) && (!paramView.isDrawingCacheEnabled())) {
      paramView.setDrawingCacheEnabled(true);
    }
    Trace.traceEnd(8L);
  }
  
  boolean arrowScroll(int paramInt)
  {
    int i = this.mSelectedPosition;
    int j = this.mNumColumns;
    boolean bool1 = false;
    int k;
    int m;
    if (!this.mStackFromBottom)
    {
      k = i / j * j;
      m = Math.min(k + j - 1, this.mItemCount - 1);
    }
    else
    {
      m = this.mItemCount;
      m = this.mItemCount - 1 - (m - 1 - i) / j * j;
      k = Math.max(0, m - j + 1);
    }
    if (paramInt != 33)
    {
      if ((paramInt == 130) && (m < this.mItemCount - 1))
      {
        this.mLayoutMode = 6;
        setSelectionInt(Math.min(i + j, this.mItemCount - 1));
        bool1 = true;
      }
    }
    else if (k > 0)
    {
      this.mLayoutMode = 6;
      setSelectionInt(Math.max(0, i - j));
      bool1 = true;
    }
    boolean bool2 = isLayoutRtl();
    boolean bool3;
    if ((i > k) && (((paramInt == 17) && (!bool2)) || ((paramInt == 66) && (bool2))))
    {
      this.mLayoutMode = 6;
      setSelectionInt(Math.max(0, i - 1));
      bool3 = true;
    }
    else
    {
      bool3 = bool1;
      if (i < m) {
        if ((paramInt != 17) || (!bool2))
        {
          bool3 = bool1;
          if (paramInt == 66)
          {
            bool3 = bool1;
            if (bool2) {}
          }
        }
        else
        {
          this.mLayoutMode = 6;
          setSelectionInt(Math.min(i + 1, this.mItemCount - 1));
          bool3 = true;
        }
      }
    }
    if (bool3)
    {
      playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
      invokeOnItemScrollListener();
    }
    if (bool3) {
      awakenScrollBars();
    }
    return bool3;
  }
  
  protected void attachLayoutAnimationParameters(View paramView, ViewGroup.LayoutParams paramLayoutParams, int paramInt1, int paramInt2)
  {
    GridLayoutAnimationController.AnimationParameters localAnimationParameters = (GridLayoutAnimationController.AnimationParameters)paramLayoutParams.layoutAnimationParameters;
    paramView = localAnimationParameters;
    if (localAnimationParameters == null)
    {
      paramView = new GridLayoutAnimationController.AnimationParameters();
      paramLayoutParams.layoutAnimationParameters = paramView;
    }
    paramView.count = paramInt2;
    paramView.index = paramInt1;
    int i = this.mNumColumns;
    paramView.columnsCount = i;
    paramView.rowsCount = (paramInt2 / i);
    if (!this.mStackFromBottom)
    {
      paramInt2 = this.mNumColumns;
      paramView.column = (paramInt1 % paramInt2);
      paramView.row = (paramInt1 / paramInt2);
    }
    else
    {
      paramInt1 = paramInt2 - 1 - paramInt1;
      paramInt2 = this.mNumColumns;
      paramView.column = (paramInt2 - 1 - paramInt1 % paramInt2);
      paramView.row = (paramView.rowsCount - 1 - paramInt1 / this.mNumColumns);
    }
  }
  
  protected int computeVerticalScrollExtent()
  {
    int i = getChildCount();
    if (i > 0)
    {
      int j = this.mNumColumns;
      int k = (i + j - 1) / j * 100;
      View localView = getChildAt(0);
      int m = localView.getTop();
      int n = localView.getHeight();
      j = k;
      if (n > 0) {
        j = k + m * 100 / n;
      }
      localView = getChildAt(i - 1);
      m = localView.getBottom();
      i = localView.getHeight();
      k = j;
      if (i > 0) {
        k = j - (m - getHeight()) * 100 / i;
      }
      return k;
    }
    return 0;
  }
  
  protected int computeVerticalScrollOffset()
  {
    if ((this.mFirstPosition >= 0) && (getChildCount() > 0))
    {
      View localView = getChildAt(0);
      int i = localView.getTop();
      int j = localView.getHeight();
      if (j > 0)
      {
        int k = this.mNumColumns;
        int m = (this.mItemCount + k - 1) / k;
        int n;
        if (isStackFromBottom()) {
          n = m * k - this.mItemCount;
        } else {
          n = 0;
        }
        return Math.max((this.mFirstPosition + n) / k * 100 - i * 100 / j + (int)(this.mScrollY / getHeight() * m * 100.0F), 0);
      }
    }
    return 0;
  }
  
  protected int computeVerticalScrollRange()
  {
    int i = this.mNumColumns;
    int j = (this.mItemCount + i - 1) / i;
    int k = Math.max(j * 100, 0);
    i = k;
    if (this.mScrollY != 0) {
      i = k + Math.abs((int)(this.mScrollY / getHeight() * j * 100.0F));
    }
    return i;
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("numColumns", getNumColumns());
  }
  
  void fillGap(boolean paramBoolean)
  {
    if (Android_Widget_GridView.Extension.get().getExtension() != null) {
      ((Android_Widget_GridView.Interface)Android_Widget_GridView.Extension.get().getExtension().asInterface()).fillGap(this, paramBoolean);
    } else {
      originalFillGap(paramBoolean);
    }
  }
  
  int findMotionRow(int paramInt)
  {
    int i = getChildCount();
    if (i > 0)
    {
      int j = this.mNumColumns;
      int k;
      if (!this.mStackFromBottom)
      {
        k = 0;
        while (k < i)
        {
          if (paramInt <= getChildAt(k).getBottom()) {
            return this.mFirstPosition + k;
          }
          k += j;
        }
      }
      else
      {
        k = i - 1;
        while (k >= 0)
        {
          if (paramInt >= getChildAt(k).getTop()) {
            return this.mFirstPosition + k;
          }
          k -= j;
        }
      }
    }
    return -1;
  }
  
  boolean fullScroll(int paramInt)
  {
    boolean bool = false;
    if (paramInt == 33)
    {
      this.mLayoutMode = 2;
      setSelectionInt(0);
      invokeOnItemScrollListener();
      bool = true;
    }
    else if (paramInt == 130)
    {
      this.mLayoutMode = 2;
      setSelectionInt(this.mItemCount - 1);
      invokeOnItemScrollListener();
      bool = true;
    }
    if (bool) {
      awakenScrollBars();
    }
    return bool;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return GridView.class.getName();
  }
  
  public ListAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public int getColumnWidth()
  {
    return this.mColumnWidth;
  }
  
  public int getGravity()
  {
    return this.mGravity;
  }
  
  public int getHorizontalSpacing()
  {
    return this.mHorizontalSpacing;
  }
  
  @ViewDebug.ExportedProperty
  public int getNumColumns()
  {
    return this.mNumColumns;
  }
  
  public int getRequestedColumnWidth()
  {
    return this.mRequestedColumnWidth;
  }
  
  public int getRequestedHorizontalSpacing()
  {
    return this.mRequestedHorizontalSpacing;
  }
  
  public int getStretchMode()
  {
    return this.mStretchMode;
  }
  
  public int getVerticalSpacing()
  {
    return this.mVerticalSpacing;
  }
  
  protected void layoutChildren()
  {
    if (Android_Widget_GridView.Extension.get().getExtension() != null) {
      ((Android_Widget_GridView.Interface)Android_Widget_GridView.Extension.get().getExtension().asInterface()).layoutChildren(this);
    } else {
      originalLayoutChildren();
    }
  }
  
  int lookForSelectablePosition(int paramInt, boolean paramBoolean)
  {
    if ((this.mAdapter != null) && (!isInTouchMode()))
    {
      if ((paramInt >= 0) && (paramInt < this.mItemCount)) {
        return paramInt;
      }
      return -1;
    }
    return -1;
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    int i = -1;
    int j = i;
    if (paramBoolean)
    {
      j = i;
      if (paramRect != null)
      {
        paramRect.offset(this.mScrollX, this.mScrollY);
        Rect localRect = this.mTempRect;
        int k = Integer.MAX_VALUE;
        int m = getChildCount();
        int n = 0;
        for (;;)
        {
          j = i;
          if (n >= m) {
            break;
          }
          if (!isCandidateSelection(n, paramInt))
          {
            j = k;
          }
          else
          {
            View localView = getChildAt(n);
            localView.getDrawingRect(localRect);
            offsetDescendantRectToMyCoords(localView, localRect);
            int i1 = getDistance(paramRect, localRect, paramInt);
            j = k;
            if (i1 < k)
            {
              j = i1;
              i = n;
            }
          }
          n++;
          k = j;
        }
      }
    }
    if (j >= 0) {
      setSelection(this.mFirstPosition + j);
    } else {
      requestLayout();
    }
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(View paramView, int paramInt, AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoForItem(paramView, paramInt, paramAccessibilityNodeInfo);
    int i = getCount();
    int j = getNumColumns();
    int k = i / j;
    if (!this.mStackFromBottom)
    {
      i = paramInt % j;
      k = paramInt / j;
    }
    else
    {
      i = i - 1 - paramInt;
      k = k - 1 - i / j;
      i = j - 1 - i % j;
    }
    paramView = (AbsListView.LayoutParams)paramView.getLayoutParams();
    boolean bool;
    if ((paramView != null) && (paramView.viewType == -2)) {
      bool = true;
    } else {
      bool = false;
    }
    paramAccessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(k, 1, i, 1, bool, isItemChecked(paramInt)));
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    int i = getNumColumns();
    int j = getCount() / i;
    paramAccessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(j, i, false, getSelectionModeForAccessibility()));
    if ((i > 0) || (j > 0)) {
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
    if (i == 0)
    {
      k = this.mColumnWidth;
      if (k > 0) {
        k = k + this.mListPadding.left + this.mListPadding.right;
      } else {
        k = this.mListPadding.left + this.mListPadding.right;
      }
      k = getVerticalScrollbarWidth() + k;
    }
    boolean bool = determineColumns(k - this.mListPadding.left - this.mListPadding.right);
    int n = 0;
    if (this.mAdapter == null) {
      i1 = 0;
    } else {
      i1 = this.mAdapter.getCount();
    }
    this.mItemCount = i1;
    int i2 = this.mItemCount;
    if (i2 > 0)
    {
      View localView = obtainView(0, this.mIsScrap);
      AbsListView.LayoutParams localLayoutParams1 = (AbsListView.LayoutParams)localView.getLayoutParams();
      AbsListView.LayoutParams localLayoutParams2 = localLayoutParams1;
      if (localLayoutParams1 == null)
      {
        localLayoutParams2 = (AbsListView.LayoutParams)generateDefaultLayoutParams();
        localView.setLayoutParams(localLayoutParams2);
      }
      localLayoutParams2.viewType = this.mAdapter.getItemViewType(0);
      localLayoutParams2.isEnabled = this.mAdapter.isEnabled(0);
      localLayoutParams2.forceAdd = true;
      paramInt2 = getChildMeasureSpec(View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 0), 0, localLayoutParams2.height);
      localView.measure(getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(this.mColumnWidth, 1073741824), 0, localLayoutParams2.width), paramInt2);
      paramInt2 = localView.getMeasuredHeight();
      combineMeasuredStates(0, localView.getMeasuredState());
      n = paramInt2;
      if (this.mRecycler.shouldRecycleViewType(localLayoutParams2.viewType))
      {
        this.mRecycler.addScrapView(localView, -1);
        n = paramInt2;
      }
    }
    paramInt2 = m;
    if (j == 0) {
      paramInt2 = this.mListPadding.top + this.mListPadding.bottom + n + getVerticalFadingEdgeLength() * 2;
    }
    int i1 = paramInt2;
    if (j == Integer.MIN_VALUE)
    {
      i1 = this.mListPadding.top + this.mListPadding.bottom;
      int i3 = this.mNumColumns;
      j = 0;
      for (;;)
      {
        m = i1;
        if (j >= i2) {
          break;
        }
        m = i1 + n;
        i1 = m;
        if (j + i3 < i2) {
          i1 = m + this.mVerticalSpacing;
        }
        if (i1 >= paramInt2)
        {
          m = paramInt2;
          break;
        }
        j += i3;
      }
      i1 = m;
    }
    paramInt2 = k;
    if (i == Integer.MIN_VALUE)
    {
      m = this.mRequestedNumColumns;
      paramInt2 = k;
      if (m != -1) {
        if (this.mColumnWidth * m + (m - 1) * this.mHorizontalSpacing + this.mListPadding.left + this.mListPadding.right <= k)
        {
          paramInt2 = k;
          if (!bool) {}
        }
        else
        {
          paramInt2 = k | 0x1000000;
        }
      }
    }
    setMeasuredDimension(paramInt2, i1);
    this.mWidthMeasureSpec = paramInt1;
  }
  
  void originalFillGap(boolean paramBoolean)
  {
    int i = this.mNumColumns;
    int j = this.mVerticalSpacing;
    int k = getChildCount();
    int m;
    if (paramBoolean)
    {
      m = 0;
      if ((this.mGroupFlags & 0x22) == 34) {
        m = getListPaddingTop();
      }
      if (k > 0) {
        m = getChildAt(k - 1).getBottom() + j;
      }
      int n = this.mFirstPosition + k;
      k = n;
      if (this.mStackFromBottom) {
        k = n + (i - 1);
      }
      fillDown(k, m);
      correctTooHigh(i, j, getChildCount());
    }
    else
    {
      m = 0;
      if ((this.mGroupFlags & 0x22) == 34) {
        m = getListPaddingBottom();
      }
      if (k > 0) {
        m = getChildAt(0).getTop() - j;
      } else {
        m = getHeight() - m;
      }
      k = this.mFirstPosition;
      if (!this.mStackFromBottom) {
        k -= i;
      } else {
        k--;
      }
      fillUp(k, m);
      correctTooLow(i, j, getChildCount());
    }
  }
  
  void originalLayoutChildren()
  {
    boolean bool1 = this.mBlockLayoutRequests;
    if (!bool1) {
      this.mBlockLayoutRequests = true;
    }
    boolean bool2 = bool1;
    try
    {
      super.layoutChildren();
      bool2 = bool1;
      invalidate();
      bool2 = bool1;
      ListAdapter localListAdapter = this.mAdapter;
      if (localListAdapter == null) {
        try
        {
          resetList();
          invokeOnItemScrollListener();
          if (!bool1) {
            this.mBlockLayoutRequests = false;
          }
          return;
        }
        finally
        {
          break label1465;
        }
      }
      bool2 = bool1;
      int i = this.mListPadding.top;
      bool2 = bool1;
      int j = this.mBottom - this.mTop - this.mListPadding.bottom;
      bool2 = bool1;
      int k = getChildCount();
      int m = 0;
      Object localObject5 = null;
      Object localObject6 = null;
      Object localObject2 = null;
      Object localObject7 = null;
      bool2 = bool1;
      int n;
      switch (this.mLayoutMode)
      {
      default: 
        bool2 = bool1;
        n = this.mSelectedPosition;
        break;
      case 6: 
        i1 = m;
        localObject8 = localObject5;
        localObject6 = localObject2;
        localObject9 = localObject7;
        if (this.mNextSelectedPosition < 0) {
          break label367;
        }
        i1 = this.mNextSelectedPosition - this.mSelectedPosition;
        localObject8 = localObject5;
        localObject6 = localObject2;
        localObject9 = localObject7;
        break;
      case 2: 
        n = this.mNextSelectedPosition - this.mFirstPosition;
        i1 = m;
        localObject8 = localObject5;
        localObject6 = localObject2;
        localObject9 = localObject7;
        if (n < 0) {
          break label367;
        }
        i1 = m;
        localObject8 = localObject5;
        localObject6 = localObject2;
        localObject9 = localObject7;
        if (n >= k) {
          break label367;
        }
        localObject9 = getChildAt(n);
        i1 = m;
        localObject8 = localObject5;
        localObject6 = localObject2;
        break;
      case 1: 
      case 3: 
      case 4: 
      case 5: 
        i1 = m;
        localObject8 = localObject5;
        localObject6 = localObject2;
        localObject9 = localObject7;
        break;
      }
      bool2 = bool1;
      int i1 = this.mFirstPosition;
      i1 = n - i1;
      localObject2 = localObject6;
      if (i1 >= 0)
      {
        localObject2 = localObject6;
        if (i1 < k) {
          localObject2 = getChildAt(i1);
        }
      }
      bool2 = bool1;
      localObject6 = getChildAt(0);
      Object localObject9 = localObject7;
      Object localObject8 = localObject2;
      i1 = m;
      label367:
      bool2 = bool1;
      boolean bool3 = this.mDataChanged;
      if (bool3) {
        handleDataChanged();
      }
      bool2 = bool1;
      m = this.mItemCount;
      if (m == 0)
      {
        resetList();
        invokeOnItemScrollListener();
        if (!bool1) {
          this.mBlockLayoutRequests = false;
        }
        return;
      }
      bool2 = bool1;
      setSelectedPositionInt(this.mNextSelectedPosition);
      localObject5 = null;
      localObject2 = null;
      m = -1;
      bool2 = bool1;
      ViewRootImpl localViewRootImpl = getViewRootImpl();
      if (localViewRootImpl != null)
      {
        localObject7 = localViewRootImpl.getAccessibilityFocusedHost();
        if (localObject7 != null)
        {
          localObject10 = getAccessibilityFocusedChild((View)localObject7);
          if (localObject10 != null)
          {
            if ((bool3) && (!((View)localObject10).hasTransientState()) && (!this.mAdapterHasStableIds))
            {
              localObject7 = null;
            }
            else
            {
              localObject2 = localObject7;
              localObject7 = localViewRootImpl.getAccessibilityFocusedVirtualView();
            }
            m = getPositionForView((View)localObject10);
            localObject5 = localObject2;
          }
          else
          {
            localObject7 = null;
          }
        }
        else
        {
          localObject7 = null;
        }
      }
      else
      {
        localObject7 = null;
      }
      bool2 = bool1;
      int i2 = this.mFirstPosition;
      bool2 = bool1;
      Object localObject10 = this.mRecycler;
      if (bool3) {
        n = 0;
      }
      for (;;)
      {
        if (n < k)
        {
          bool2 = bool1;
          localObject2 = getChildAt(n);
          bool2 = bool1;
          bool1 = bool2;
        }
        try
        {
          ((AbsListView.RecycleBin)localObject10).addScrapView((View)localObject2, i2 + n);
          n++;
        }
        finally
        {
          break label1465;
        }
      }
      bool2 = bool1;
      break label620;
      bool2 = bool1;
      bool1 = bool2;
      ((AbsListView.RecycleBin)localObject10).fillActiveViews(k, i2);
      label620:
      bool1 = bool2;
      detachAllViewsFromParent();
      bool1 = bool2;
      ((AbsListView.RecycleBin)localObject10).removeSkippedScrap();
      bool1 = bool2;
      switch (this.mLayoutMode)
      {
      default: 
        if (k != 0) {
          break label949;
        }
        bool1 = bool2;
        bool3 = this.mStackFromBottom;
        break;
      case 6: 
        bool1 = bool2;
        localObject2 = moveSelection(i1, i, j);
        break;
      case 5: 
        bool1 = bool2;
        localObject2 = fillSpecific(this.mSyncPosition, this.mSpecificTop);
        break;
      case 4: 
        bool1 = bool2;
        localObject2 = fillSpecific(this.mSelectedPosition, this.mSpecificTop);
        break;
      case 3: 
        bool1 = bool2;
        localObject2 = fillUp(this.mItemCount - 1, j);
        bool1 = bool2;
        adjustViewsUpOrDown();
        break;
      case 2: 
        if (localObject9 != null)
        {
          bool1 = bool2;
          localObject2 = fillFromSelection(((View)localObject9).getTop(), i, j);
        }
        else
        {
          bool1 = bool2;
          localObject2 = fillSelection(i, j);
        }
        break;
      case 1: 
        bool1 = bool2;
        this.mFirstPosition = 0;
        bool1 = bool2;
        localObject2 = fillFromTop(i);
        bool1 = bool2;
        adjustViewsUpOrDown();
        break;
      }
      if (!bool3)
      {
        bool1 = bool2;
        if (this.mAdapter != null)
        {
          bool1 = bool2;
          if (!isInTouchMode())
          {
            i1 = 0;
            break label866;
          }
        }
        i1 = -1;
        label866:
        bool1 = bool2;
        setSelectedPositionInt(i1);
        bool1 = bool2;
        localObject2 = fillFromTop(i);
      }
      else
      {
        bool1 = bool2;
        i1 = this.mItemCount - 1;
        bool1 = bool2;
        if (this.mAdapter != null)
        {
          bool1 = bool2;
          if (!isInTouchMode())
          {
            i = i1;
            break label927;
          }
        }
        i = -1;
        label927:
        bool1 = bool2;
        setSelectedPositionInt(i);
        bool1 = bool2;
        localObject2 = fillFromBottom(i1, j);
        break label1072;
        label949:
        bool1 = bool2;
        if (this.mSelectedPosition >= 0)
        {
          bool1 = bool2;
          if (this.mSelectedPosition < this.mItemCount)
          {
            bool1 = bool2;
            i1 = this.mSelectedPosition;
            if (localObject8 != null)
            {
              bool1 = bool2;
              i = ((View)localObject8).getTop();
            }
            bool1 = bool2;
            localObject2 = fillSpecific(i1, i);
            break label1072;
          }
        }
        bool1 = bool2;
        if (this.mFirstPosition < this.mItemCount)
        {
          bool1 = bool2;
          i1 = this.mFirstPosition;
          if (localObject6 != null)
          {
            bool1 = bool2;
            i = ((View)localObject6).getTop();
          }
          bool1 = bool2;
          localObject2 = fillSpecific(i1, i);
        }
        else
        {
          bool1 = bool2;
          localObject2 = fillSpecific(0, i);
        }
      }
      label1072:
      bool1 = bool2;
      ((AbsListView.RecycleBin)localObject10).scrapActiveViews();
      if (localObject2 != null)
      {
        bool1 = bool2;
        positionSelector(-1, (View)localObject2);
        bool1 = bool2;
        this.mSelectedTop = ((View)localObject2).getTop();
      }
      else
      {
        bool1 = bool2;
        if (this.mTouchMode > 0)
        {
          bool1 = bool2;
          if (this.mTouchMode < 3)
          {
            i = 1;
            break label1132;
          }
        }
        i = 0;
        label1132:
        if (i != 0)
        {
          bool1 = bool2;
          localObject2 = getChildAt(this.mMotionPosition - this.mFirstPosition);
          if (localObject2 != null)
          {
            bool1 = bool2;
            positionSelector(this.mMotionPosition, (View)localObject2);
          }
        }
        else
        {
          bool1 = bool2;
          if (this.mSelectedPosition != -1)
          {
            bool1 = bool2;
            localObject2 = getChildAt(this.mSelectorPosition - this.mFirstPosition);
            if (localObject2 != null)
            {
              bool1 = bool2;
              positionSelector(this.mSelectorPosition, (View)localObject2);
            }
          }
          else
          {
            bool1 = bool2;
            this.mSelectedTop = 0;
            bool1 = bool2;
            this.mSelectorRect.setEmpty();
          }
        }
      }
      if (localViewRootImpl != null)
      {
        bool1 = bool2;
        label1357:
        if (localViewRootImpl.getAccessibilityFocusedHost() == null)
        {
          if (localObject5 != null)
          {
            bool1 = bool2;
            if (((View)localObject5).isAttachedToWindow())
            {
              bool1 = bool2;
              localObject2 = ((View)localObject5).getAccessibilityNodeProvider();
              if ((localObject7 != null) && (localObject2 != null))
              {
                bool1 = bool2;
                ((AccessibilityNodeProvider)localObject2).performAction(AccessibilityNodeInfo.getVirtualDescendantId(((AccessibilityNodeInfo)localObject7).getSourceNodeId()), 64, null);
                break label1357;
              }
              bool1 = bool2;
              ((View)localObject5).requestAccessibilityFocus();
              break label1357;
            }
          }
          if (m != -1)
          {
            bool1 = bool2;
            localObject2 = getChildAt(MathUtils.constrain(m - this.mFirstPosition, 0, getChildCount() - 1));
            if (localObject2 == null) {
              break label1363;
            }
            bool1 = bool2;
            ((View)localObject2).requestAccessibilityFocus();
          }
        }
        else {}
      }
      label1363:
      bool1 = bool2;
      this.mLayoutMode = 0;
      bool1 = bool2;
      this.mDataChanged = false;
      bool1 = bool2;
      if (this.mPositionScrollAfterLayout != null)
      {
        bool1 = bool2;
        post(this.mPositionScrollAfterLayout);
        bool1 = bool2;
        this.mPositionScrollAfterLayout = null;
      }
      bool1 = bool2;
      this.mNeedSync = false;
      bool1 = bool2;
      setNextSelectedPositionInt(this.mSelectedPosition);
      bool1 = bool2;
      updateScrollIndicators();
      bool1 = bool2;
      if (this.mItemCount > 0)
      {
        bool1 = bool2;
        checkSelectionChanged();
      }
      bool1 = bool2;
      invokeOnItemScrollListener();
      if (!bool2) {
        this.mBlockLayoutRequests = false;
      }
      return;
    }
    finally
    {
      bool1 = bool2;
    }
    label1465:
    if (!bool1) {
      this.mBlockLayoutRequests = false;
    }
    throw ((Throwable)localObject4);
  }
  
  boolean pageScroll(int paramInt)
  {
    int i = -1;
    if (paramInt == 33) {
      i = Math.max(0, this.mSelectedPosition - getChildCount());
    } else if (paramInt == 130) {
      i = Math.min(this.mItemCount - 1, this.mSelectedPosition + getChildCount());
    }
    if (i >= 0)
    {
      setSelectionInt(i);
      invokeOnItemScrollListener();
      awakenScrollBars();
      return true;
    }
    return false;
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (paramInt == 16908343)
    {
      int i = getNumColumns();
      paramInt = paramBundle.getInt("android.view.accessibility.action.ARGUMENT_ROW_INT", -1);
      i = Math.min(paramInt * i, getCount() - 1);
      if (paramInt >= 0)
      {
        smoothScrollToPosition(i);
        return true;
      }
    }
    return false;
  }
  
  @UnsupportedAppUsage
  boolean sequenceScroll(int paramInt)
  {
    int i = this.mSelectedPosition;
    int j = this.mNumColumns;
    int k = this.mItemCount;
    boolean bool = this.mStackFromBottom;
    int m = 0;
    int n = 0;
    int i1;
    int i2;
    if (!bool)
    {
      i1 = i / j * j;
      i2 = Math.min(i1 + j - 1, k - 1);
    }
    else
    {
      i2 = k - 1 - (k - 1 - i) / j * j;
      i1 = Math.max(0, i2 - j + 1);
    }
    bool = false;
    j = 0;
    if (paramInt != 1)
    {
      if ((paramInt == 2) && (i < k - 1))
      {
        this.mLayoutMode = 6;
        setSelectionInt(i + 1);
        bool = true;
        j = n;
        if (i == i2) {
          j = 1;
        }
      }
    }
    else if (i > 0)
    {
      this.mLayoutMode = 6;
      setSelectionInt(i - 1);
      bool = true;
      j = m;
      if (i == i1) {
        j = 1;
      }
    }
    if (bool)
    {
      playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
      invokeOnItemScrollListener();
    }
    if (j != 0) {
      awakenScrollBars();
    }
    return bool;
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    if ((this.mAdapter != null) && (this.mDataSetObserver != null)) {
      this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
    }
    resetList();
    this.mRecycler.clear();
    this.mAdapter = paramListAdapter;
    this.mOldSelectedPosition = -1;
    this.mOldSelectedRowId = Long.MIN_VALUE;
    super.setAdapter(paramListAdapter);
    if (this.mAdapter != null)
    {
      this.mOldItemCount = this.mItemCount;
      this.mItemCount = this.mAdapter.getCount();
      this.mDataChanged = true;
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
      checkSelectionChanged();
    }
    else
    {
      checkFocus();
      checkSelectionChanged();
    }
    requestLayout();
  }
  
  public void setColumnWidth(int paramInt)
  {
    if (paramInt != this.mRequestedColumnWidth)
    {
      this.mRequestedColumnWidth = paramInt;
      requestLayoutIfNecessary();
    }
  }
  
  public void setGravity(int paramInt)
  {
    if (this.mGravity != paramInt)
    {
      this.mGravity = paramInt;
      requestLayoutIfNecessary();
    }
  }
  
  public void setHorizontalSpacing(int paramInt)
  {
    if (paramInt != this.mRequestedHorizontalSpacing)
    {
      this.mRequestedHorizontalSpacing = paramInt;
      requestLayoutIfNecessary();
    }
  }
  
  public void setNumColumns(int paramInt)
  {
    if (paramInt != this.mRequestedNumColumns)
    {
      this.mRequestedNumColumns = paramInt;
      requestLayoutIfNecessary();
    }
  }
  
  @RemotableViewMethod(asyncImpl="setRemoteViewsAdapterAsync")
  public void setRemoteViewsAdapter(Intent paramIntent)
  {
    super.setRemoteViewsAdapter(paramIntent);
  }
  
  public void setSelection(int paramInt)
  {
    if (!isInTouchMode()) {
      setNextSelectedPositionInt(paramInt);
    } else {
      this.mResurrectToPosition = paramInt;
    }
    this.mLayoutMode = 2;
    if (this.mPositionScroller != null) {
      this.mPositionScroller.stop();
    }
    requestLayout();
  }
  
  void setSelectionInt(int paramInt)
  {
    int i = this.mNextSelectedPosition;
    if (this.mPositionScroller != null) {
      this.mPositionScroller.stop();
    }
    setNextSelectedPositionInt(paramInt);
    layoutChildren();
    if (this.mStackFromBottom) {
      paramInt = this.mItemCount - 1 - this.mNextSelectedPosition;
    } else {
      paramInt = this.mNextSelectedPosition;
    }
    if (this.mStackFromBottom) {
      i = this.mItemCount - 1 - i;
    }
    int j = this.mNumColumns;
    if (paramInt / j != i / j) {
      awakenScrollBars();
    }
  }
  
  public void setStretchMode(int paramInt)
  {
    if (paramInt != this.mStretchMode)
    {
      this.mStretchMode = paramInt;
      requestLayoutIfNecessary();
    }
  }
  
  public void setVerticalSpacing(int paramInt)
  {
    if (paramInt != this.mVerticalSpacing)
    {
      this.mVerticalSpacing = paramInt;
      requestLayoutIfNecessary();
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
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface StretchMode {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/GridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */