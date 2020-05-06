package miui.theme;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.IOverlayManager.Stub;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;

public class IconCustomizerUtils
{
  public static final int ICON_LAYER_MAX_NUM = 5;
  private static final String ICON_SHAPE_OVERLAY_PACKAGE = "com.android.systemui.icon.overlay";
  private static final String LOG_TAG = "IconCustomizerUtils";
  
  private static Drawable getAdaptiveDrawable(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    if ((paramDrawable1 == null) && (paramDrawable2 == null)) {
      return null;
    }
    if ((paramDrawable1 instanceof AdaptiveIconDrawable)) {
      return paramDrawable1;
    }
    if ((paramDrawable2 instanceof LayerDrawable)) {
      return new AdaptiveIconDrawable(paramDrawable1, paramDrawable2);
    }
    return null;
  }
  
  public static Drawable getAdaptiveIconFromPackage(Context paramContext, String paramString, int paramInt, ApplicationInfo paramApplicationInfo)
  {
    return getAdaptiveDrawable(paramContext.getPackageManager().getDrawable(paramString, paramInt, paramApplicationInfo), null);
  }
  
  public static void setIconShapeOverlayEnable(boolean paramBoolean)
  {
    IOverlayManager localIOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
    if (localIOverlayManager != null) {
      try
      {
        localIOverlayManager.setEnabled("com.android.systemui.icon.overlay", paramBoolean, UserHandle.myUserId());
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    } else {
      Log.e("IconCustomizerUtils", "IOverlayManager is null");
    }
  }
  
  public static Drawable transformToAdaptiveIcon(Bitmap[] paramArrayOfBitmap)
  {
    BitmapDrawable localBitmapDrawable = null;
    Object localObject1 = null;
    Object localObject2 = localBitmapDrawable;
    Object localObject3 = localObject1;
    if (paramArrayOfBitmap != null)
    {
      localObject2 = localBitmapDrawable;
      localObject3 = localObject1;
      if (paramArrayOfBitmap[0] != null)
      {
        localBitmapDrawable = new BitmapDrawable(paramArrayOfBitmap[0]);
        localObject2 = localBitmapDrawable;
        localObject3 = localObject1;
        if (paramArrayOfBitmap.length >= 2)
        {
          localObject2 = localBitmapDrawable;
          localObject3 = localObject1;
          if (paramArrayOfBitmap[1] != null)
          {
            localObject2 = new Drawable[4];
            int i = Math.min(paramArrayOfBitmap.length - 1, localObject2.length);
            for (int j = 0; j < i; j++)
            {
              localObject3 = paramArrayOfBitmap[(j + 1)];
              if (localObject3 != null) {
                localObject2[j] = new BitmapDrawable(Resources.getSystem(), (Bitmap)localObject3);
              }
            }
            localObject3 = new LayerDrawable((Drawable[])localObject2);
            localObject2 = localBitmapDrawable;
          }
        }
      }
    }
    return getAdaptiveDrawable((Drawable)localObject2, (Drawable)localObject3);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/theme/IconCustomizerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */