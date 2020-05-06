package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import miui.os.Environment;

public class ViewConfigurationInjector
{
  private static final SparseArray<ViewConfiguration> sConfigrations = new SparseArray(2);
  
  static ViewConfiguration get(Context paramContext)
  {
    if (Environment.isUsingMiui(paramContext))
    {
      int i = (int)(paramContext.getResources().getDisplayMetrics().density * 100.0F);
      return (ViewConfiguration)sConfigrations.get(i);
    }
    return null;
  }
  
  static int getOverFlingDistance(Context paramContext, int paramInt)
  {
    if (Environment.isUsingMiui(paramContext)) {
      return (int)(paramContext.getResources().getDimension(285606033) + 0.5F);
    }
    return paramInt;
  }
  
  static int getOverScrollDistance(Context paramContext, int paramInt)
  {
    if (Environment.isUsingMiui(paramContext)) {
      return (int)(paramContext.getResources().getDimension(285606034) + 0.5F);
    }
    return paramInt;
  }
  
  static boolean needMiuiConfiguration(Context paramContext)
  {
    return Environment.isUsingMiui(paramContext);
  }
  
  static void put(Context paramContext, ViewConfiguration paramViewConfiguration)
  {
    if (Environment.isUsingMiui(paramContext))
    {
      int i = (int)(paramContext.getResources().getDisplayMetrics().density * 100.0F);
      sConfigrations.put(i, paramViewConfiguration);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewConfigurationInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */