package android.widget;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager.LayoutParams;

class WindowLayoutParamsUtil
{
  static WindowManager.LayoutParams getWindowLayoutParams(View paramView)
  {
    while (paramView != null)
    {
      ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
      if ((localLayoutParams instanceof WindowManager.LayoutParams)) {
        return (WindowManager.LayoutParams)localLayoutParams;
      }
      paramView = (View)paramView.getParent();
    }
    return null;
  }
  
  static boolean isInSystemWindow(View paramView)
  {
    paramView = getWindowLayoutParams(paramView);
    boolean bool;
    if ((paramView != null) && (paramView.type >= 2000)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/WindowLayoutParamsUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */