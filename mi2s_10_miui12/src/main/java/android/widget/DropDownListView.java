package android.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import com.android.internal.widget.AutoScrollHelper.AbsListViewAutoScroller;

public class DropDownListView
  extends ListView
{
  private boolean mDrawsInPressedState;
  private boolean mHijackFocus;
  private boolean mListSelectionHidden;
  private ResolveHoverRunnable mResolveHoverRunnable;
  private AutoScrollHelper.AbsListViewAutoScroller mScrollHelper;
  
  public DropDownListView(Context paramContext, boolean paramBoolean)
  {
    this(paramContext, paramBoolean, 16842861);
  }
  
  public DropDownListView(Context paramContext, boolean paramBoolean, int paramInt)
  {
    super(paramContext, null, paramInt);
    this.mHijackFocus = paramBoolean;
    setCacheColorHint(0);
  }
  
  private void clearPressedItem()
  {
    this.mDrawsInPressedState = false;
    setPressed(false);
    updateSelectorState();
    View localView = getChildAt(this.mMotionPosition - this.mFirstPosition);
    if (localView != null) {
      localView.setPressed(false);
    }
  }
  
  private void setPressedItem(View paramView, int paramInt, float paramFloat1, float paramFloat2)
  {
    this.mDrawsInPressedState = true;
    drawableHotspotChanged(paramFloat1, paramFloat2);
    if (!isPressed()) {
      setPressed(true);
    }
    if (this.mDataChanged) {
      layoutChildren();
    }
    View localView = getChildAt(this.mMotionPosition - this.mFirstPosition);
    if ((localView != null) && (localView != paramView) && (localView.isPressed())) {
      localView.setPressed(false);
    }
    this.mMotionPosition = paramInt;
    paramView.drawableHotspotChanged(paramFloat1 - paramView.getLeft(), paramFloat2 - paramView.getTop());
    if (!paramView.isPressed()) {
      paramView.setPressed(true);
    }
    setSelectedPositionInt(paramInt);
    positionSelectorLikeTouch(paramInt, paramView, paramFloat1, paramFloat2);
    refreshDrawableState();
  }
  
  protected void drawableStateChanged()
  {
    if (this.mResolveHoverRunnable == null) {
      super.drawableStateChanged();
    }
  }
  
  public boolean hasFocus()
  {
    boolean bool;
    if ((!this.mHijackFocus) && (!super.hasFocus())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasWindowFocus()
  {
    boolean bool;
    if ((!this.mHijackFocus) && (!super.hasWindowFocus())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isFocused()
  {
    boolean bool;
    if ((!this.mHijackFocus) && (!super.isFocused())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isInTouchMode()
  {
    boolean bool;
    if (((this.mHijackFocus) && (this.mListSelectionHidden)) || (super.isInTouchMode())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  View obtainView(int paramInt, boolean[] paramArrayOfBoolean)
  {
    paramArrayOfBoolean = super.obtainView(paramInt, paramArrayOfBoolean);
    if ((paramArrayOfBoolean instanceof TextView)) {
      ((TextView)paramArrayOfBoolean).setHorizontallyScrolling(true);
    }
    return paramArrayOfBoolean;
  }
  
  public boolean onForwardedEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    int i = 0;
    int j = paramMotionEvent.getActionMasked();
    if (j != 1)
    {
      if (j != 2)
      {
        if (j != 3)
        {
          bool2 = bool1;
          paramInt = i;
          break label172;
        }
        bool2 = false;
        paramInt = i;
        break label172;
      }
    }
    else {
      bool2 = false;
    }
    int k = paramMotionEvent.findPointerIndex(paramInt);
    if (k < 0)
    {
      bool2 = false;
      paramInt = i;
    }
    else
    {
      paramInt = (int)paramMotionEvent.getX(k);
      int m = (int)paramMotionEvent.getY(k);
      k = pointToPosition(paramInt, m);
      if (k == -1)
      {
        paramInt = 1;
      }
      else
      {
        View localView = getChildAt(k - getFirstVisiblePosition());
        setPressedItem(localView, k, paramInt, m);
        bool1 = true;
        bool2 = bool1;
        paramInt = i;
        if (j == 1)
        {
          performItemClick(localView, k, getItemIdAtPosition(k));
          paramInt = i;
          bool2 = bool1;
        }
      }
    }
    label172:
    if ((!bool2) || (paramInt != 0)) {
      clearPressedItem();
    }
    if (bool2)
    {
      if (this.mScrollHelper == null) {
        this.mScrollHelper = new AutoScrollHelper.AbsListViewAutoScroller(this);
      }
      this.mScrollHelper.setEnabled(true);
      this.mScrollHelper.onTouch(this, paramMotionEvent);
    }
    else
    {
      paramMotionEvent = this.mScrollHelper;
      if (paramMotionEvent != null) {
        paramMotionEvent.setEnabled(false);
      }
    }
    return bool2;
  }
  
  public boolean onHoverEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    if ((i == 10) && (this.mResolveHoverRunnable == null))
    {
      this.mResolveHoverRunnable = new ResolveHoverRunnable(null);
      this.mResolveHoverRunnable.post();
    }
    boolean bool = super.onHoverEvent(paramMotionEvent);
    if ((i != 9) && (i != 7))
    {
      if (!super.shouldShowSelector())
      {
        setSelectedPositionInt(-1);
        setNextSelectedPositionInt(-1);
      }
    }
    else
    {
      i = pointToPosition((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
      if ((i != -1) && (i != this.mSelectedPosition))
      {
        paramMotionEvent = getChildAt(i - getFirstVisiblePosition());
        if (paramMotionEvent.isEnabled())
        {
          requestFocus();
          positionSelector(i, paramMotionEvent);
          setSelectedPositionInt(i);
          setNextSelectedPositionInt(i);
        }
        updateSelectorState();
      }
    }
    return bool;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    ResolveHoverRunnable localResolveHoverRunnable = this.mResolveHoverRunnable;
    if (localResolveHoverRunnable != null) {
      localResolveHoverRunnable.cancel();
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setListSelectionHidden(boolean paramBoolean)
  {
    this.mListSelectionHidden = paramBoolean;
  }
  
  boolean shouldShowSelector()
  {
    boolean bool;
    if ((!isHovered()) && (!super.shouldShowSelector())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  boolean touchModeDrawsInPressedState()
  {
    boolean bool;
    if ((!this.mDrawsInPressedState) && (!super.touchModeDrawsInPressedState())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private class ResolveHoverRunnable
    implements Runnable
  {
    private ResolveHoverRunnable() {}
    
    public void cancel()
    {
      DropDownListView.access$102(DropDownListView.this, null);
      DropDownListView.this.removeCallbacks(this);
    }
    
    public void post()
    {
      DropDownListView.this.post(this);
    }
    
    public void run()
    {
      DropDownListView.access$102(DropDownListView.this, null);
      DropDownListView.this.drawableStateChanged();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DropDownListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */