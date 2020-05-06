package android.view;

import android.content.Context;

public class ViewGroupOverlay
  extends ViewOverlay
{
  ViewGroupOverlay(Context paramContext, View paramView)
  {
    super(paramContext, paramView);
  }
  
  public void add(View paramView)
  {
    this.mOverlayViewGroup.add(paramView);
  }
  
  public void remove(View paramView)
  {
    this.mOverlayViewGroup.remove(paramView);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewGroupOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */