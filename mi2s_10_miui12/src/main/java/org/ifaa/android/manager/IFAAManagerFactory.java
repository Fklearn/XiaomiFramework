package org.ifaa.android.manager;

import android.content.Context;

public class IFAAManagerFactory
{
  public static IFAAManager getIFAAManager(Context paramContext, int paramInt)
  {
    return IFAAManagerImpl.getInstance(paramContext);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/IFAAManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */