package android.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchPanelLayout
  extends LinearLayout
{
  private Rect mTemRect = new Rect();
  
  public TouchPanelLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public TouchPanelLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public TouchPanelLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private void checkChildState(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if (localView.getVisibility() == 0)
      {
        localView.getHitRect(this.mTemRect);
        localView.setPressed(this.mTemRect.contains(paramInt1, paramInt2));
      }
    }
  }
  
  private void resetChildState(boolean paramBoolean)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((paramBoolean) && (localView.isPressed())) {
        localView.performClick();
      }
      localView.setPressed(false);
    }
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    int j = (int)paramMotionEvent.getX();
    int k = (int)paramMotionEvent.getY();
    boolean bool = false;
    if (i != 0)
    {
      if (i != 1) {
        if (i == 2) {
          break label50;
        }
      } else {
        bool = true;
      }
      resetChildState(bool);
      break label57;
    }
    label50:
    checkChildState(j, k);
    label57:
    return true;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    setFitsSystemWindows(false);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    resetChildState(false);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TouchPanelLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */