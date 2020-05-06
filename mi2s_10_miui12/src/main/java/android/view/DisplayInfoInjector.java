package android.view;

import android.app.AppGlobals;
import android.content.pm.IPackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import java.util.ArrayList;
import miui.os.MiuiInit;
import miui.util.CustomizeUtil;

public class DisplayInfoInjector
{
  private static final ArrayList<String> NOTCH_BLACK_LIST = new ArrayList() {};
  private static final ArrayList<String> SCALE_BLACK_LIST = new ArrayList() {};
  private static String mAppName;
  private static int mNotchConfig;
  
  static int adjustHeightIfNeeded(Configuration paramConfiguration, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    int i;
    if ((paramConfiguration == null) || (paramConfiguration.orientation != 1))
    {
      i = paramInt1;
      if (paramInt2 >= paramInt3) {}
    }
    else
    {
      i = paramInt1;
      if (paramInt1 == paramInt3)
      {
        i = paramInt1;
        if (CustomizeUtil.HAS_NOTCH)
        {
          i = paramInt1;
          if (SystemProperties.getBoolean("persist.sys.miui_optimization", true ^ "1".equals(SystemProperties.get("ro.miui.cts"))))
          {
            paramInt2 = Process.myUid();
            if (paramInt2 < 10000) {
              return paramInt1;
            }
            if (mAppName == null)
            {
              mAppName = getAppName(paramInt2);
              mNotchConfig = MiuiInit.getNotchConfig(mAppName);
            }
            i = paramInt1;
            if ((mNotchConfig & 0x100) == 0)
            {
              i = paramInt1;
              if (NOTCH_BLACK_LIST.contains(mAppName)) {
                i = paramInt1 - Resources.getSystem().getDimensionPixelSize(17105467);
              }
            }
          }
        }
      }
    }
    return i;
  }
  
  static int adjustHeightIfNeededCurve(Configuration paramConfiguration, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString)
  {
    if ((paramConfiguration == null) || (paramConfiguration.orientation != 2))
    {
      paramInt5 = paramInt1;
      if (paramInt2 <= paramInt3) {}
    }
    else
    {
      paramInt5 = paramInt1;
      if (paramInt1 == paramInt3)
      {
        paramInt2 = Process.myUid();
        if (paramInt2 < 10000) {
          return paramInt1;
        }
        if (mAppName == null) {
          mAppName = getAppName(paramInt2);
        }
        if (SCALE_BLACK_LIST.contains(mAppName)) {
          paramInt5 = paramInt1;
        } else {
          paramInt5 = paramInt4;
        }
      }
    }
    return paramInt5;
  }
  
  static int adjustWidthIfNeeded(Configuration paramConfiguration, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    int i;
    if ((paramConfiguration == null) || (paramConfiguration.orientation != 2))
    {
      i = paramInt1;
      if (paramInt2 <= paramInt3) {}
    }
    else
    {
      i = paramInt1;
      if (paramInt1 == paramInt2)
      {
        i = paramInt1;
        if (CustomizeUtil.HAS_NOTCH)
        {
          i = paramInt1;
          if (SystemProperties.getBoolean("persist.sys.miui_optimization", "1".equals(SystemProperties.get("ro.miui.cts")) ^ true))
          {
            paramInt2 = Process.myUid();
            if (paramInt2 < 10000) {
              return paramInt1;
            }
            if (mAppName == null)
            {
              mAppName = getAppName(paramInt2);
              mNotchConfig = MiuiInit.getNotchConfig(mAppName);
            }
            i = paramInt1;
            if ((mNotchConfig & 0x100) == 0) {
              i = paramInt1 - Resources.getSystem().getDimensionPixelSize(17105467);
            }
          }
        }
      }
    }
    return i;
  }
  
  static int adjustWidthIfNeededCurve(Configuration paramConfiguration, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString)
  {
    if ((paramConfiguration == null) || (paramConfiguration.orientation != 1))
    {
      paramInt5 = paramInt1;
      if (paramInt2 >= paramInt3) {}
    }
    else
    {
      paramInt5 = paramInt1;
      if (paramInt1 == paramInt2)
      {
        paramInt2 = Process.myUid();
        if (paramInt2 < 10000) {
          return paramInt1;
        }
        if (mAppName == null) {
          mAppName = getAppName(paramInt2);
        }
        if (SCALE_BLACK_LIST.contains(mAppName)) {
          paramInt5 = paramInt1;
        } else {
          paramInt5 = paramInt4;
        }
      }
    }
    return paramInt5;
  }
  
  static String getAppName(int paramInt)
  {
    String str1 = "";
    if (paramInt < 10000) {
      return "";
    }
    Object localObject = null;
    try
    {
      String[] arrayOfString = AppGlobals.getPackageManager().getPackagesForUid(paramInt);
      localObject = arrayOfString;
    }
    catch (RemoteException localRemoteException) {}
    String str2 = str1;
    if (localObject != null)
    {
      str2 = str1;
      if (localObject.length > 0) {
        str2 = localObject[0];
      }
    }
    return str2;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/DisplayInfoInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */