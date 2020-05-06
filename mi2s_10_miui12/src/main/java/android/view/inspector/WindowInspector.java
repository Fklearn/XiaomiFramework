package android.view.inspector;

import android.view.View;
import android.view.WindowManagerGlobal;
import java.util.List;

public final class WindowInspector
{
  public static List<View> getGlobalWindowViews()
  {
    return WindowManagerGlobal.getInstance().getWindowViews();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inspector/WindowInspector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */