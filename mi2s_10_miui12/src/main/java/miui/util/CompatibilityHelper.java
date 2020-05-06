package miui.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.SurfaceControl;

public class CompatibilityHelper
{
  public static boolean hasNavigationBar(int paramInt)
    throws RemoteException
  {
    return IWindowManager.Stub.asInterface(ServiceManager.getService("window")).hasNavigationBar(paramInt);
  }
  
  public static Bitmap screenshot(int paramInt1, int paramInt2)
  {
    Bitmap localBitmap1 = SurfaceControl.screenshot(new Rect(), paramInt1, paramInt2, 0);
    Bitmap localBitmap2 = localBitmap1;
    if (localBitmap1 != null)
    {
      localBitmap2 = localBitmap1.copy(Bitmap.Config.ARGB_8888, true);
      localBitmap1.recycle();
    }
    return localBitmap2;
  }
  
  public static Bitmap screenshot(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Bitmap localBitmap1 = SurfaceControl.screenshot(new Rect(), paramInt1, paramInt2, false, 0);
    Bitmap localBitmap2 = localBitmap1;
    if (localBitmap1 != null)
    {
      localBitmap2 = localBitmap1.copy(Bitmap.Config.ARGB_8888, true);
      localBitmap1.recycle();
    }
    return localBitmap2;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/CompatibilityHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */