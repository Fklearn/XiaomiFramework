package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.ArrayMap;
import android.view.accessibility.AccessibilityNodeInfo.TouchDelegateInfo;

public class TouchDelegate
{
  public static final int ABOVE = 1;
  public static final int BELOW = 2;
  public static final int TO_LEFT = 4;
  public static final int TO_RIGHT = 8;
  private Rect mBounds;
  @UnsupportedAppUsage
  private boolean mDelegateTargeted;
  private View mDelegateView;
  private int mSlop;
  private Rect mSlopBounds;
  private AccessibilityNodeInfo.TouchDelegateInfo mTouchDelegateInfo;
  
  public TouchDelegate(Rect paramRect, View paramView)
  {
    this.mBounds = paramRect;
    this.mSlop = ViewConfiguration.get(paramView.getContext()).getScaledTouchSlop();
    this.mSlopBounds = new Rect(paramRect);
    paramRect = this.mSlopBounds;
    int i = this.mSlop;
    paramRect.inset(-i, -i);
    this.mDelegateView = paramView;
  }
  
  public AccessibilityNodeInfo.TouchDelegateInfo getTouchDelegateInfo()
  {
    if (this.mTouchDelegateInfo == null)
    {
      ArrayMap localArrayMap = new ArrayMap(1);
      Rect localRect1 = this.mBounds;
      Rect localRect2 = localRect1;
      if (localRect1 == null) {
        localRect2 = new Rect();
      }
      localArrayMap.put(new Region(localRect2), this.mDelegateView);
      this.mTouchDelegateInfo = new AccessibilityNodeInfo.TouchDelegateInfo(localArrayMap);
    }
    return this.mTouchDelegateInfo;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    boolean bool1 = false;
    int k = 1;
    int m = 1;
    boolean bool2 = false;
    int n = paramMotionEvent.getActionMasked();
    if (n != 0)
    {
      if ((n != 1) && (n != 2)) {
        if (n != 3)
        {
          if ((n != 5) && (n != 6)) {
            break label143;
          }
        }
        else
        {
          bool1 = this.mDelegateTargeted;
          this.mDelegateTargeted = false;
          break label143;
        }
      }
      boolean bool3 = this.mDelegateTargeted;
      bool1 = bool3;
      if (bool3)
      {
        k = m;
        if (!this.mSlopBounds.contains(i, j)) {
          k = 0;
        }
        bool1 = bool3;
      }
    }
    else
    {
      this.mDelegateTargeted = this.mBounds.contains(i, j);
      bool1 = this.mDelegateTargeted;
    }
    label143:
    if (bool1)
    {
      if (k != 0)
      {
        paramMotionEvent.setLocation(this.mDelegateView.getWidth() / 2, this.mDelegateView.getHeight() / 2);
      }
      else
      {
        k = this.mSlop;
        paramMotionEvent.setLocation(-(k * 2), -(k * 2));
      }
      bool2 = this.mDelegateView.dispatchTouchEvent(paramMotionEvent);
    }
    return bool2;
  }
  
  public boolean onTouchExplorationHoverEvent(MotionEvent paramMotionEvent)
  {
    if (this.mBounds == null) {
      return false;
    }
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    int k = 1;
    boolean bool1 = false;
    boolean bool2 = this.mBounds.contains(i, j);
    int m = paramMotionEvent.getActionMasked();
    if (m != 7)
    {
      if (m != 9)
      {
        if (m != 10)
        {
          m = k;
        }
        else
        {
          this.mDelegateTargeted = true;
          m = k;
        }
      }
      else
      {
        this.mDelegateTargeted = bool2;
        m = k;
      }
    }
    else if (bool2)
    {
      this.mDelegateTargeted = true;
      m = k;
    }
    else
    {
      m = k;
      if (this.mDelegateTargeted)
      {
        m = k;
        if (!this.mSlopBounds.contains(i, j)) {
          m = 0;
        }
      }
    }
    if (this.mDelegateTargeted)
    {
      if (m != 0) {
        paramMotionEvent.setLocation(this.mDelegateView.getWidth() / 2, this.mDelegateView.getHeight() / 2);
      } else {
        this.mDelegateTargeted = false;
      }
      bool1 = this.mDelegateView.dispatchHoverEvent(paramMotionEvent);
    }
    return bool1;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/TouchDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */