package org.ifaa.android.manager.face;

import android.content.Context;

public class IFAAFaceManagerFactory
{
  public static IFAAFaceManager getIFAAFaceManager(Context paramContext)
  {
    return IFAAFaceManagerImpl.getInstance(paramContext);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/face/IFAAFaceManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */