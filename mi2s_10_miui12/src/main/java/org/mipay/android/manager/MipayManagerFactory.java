package org.mipay.android.manager;

import android.content.Context;

public class MipayManagerFactory
{
  public static IMipayManager getMipayManager(Context paramContext, int paramInt)
  {
    return MipayManagerImpl.getInstance(paramContext);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/mipay/android/manager/MipayManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */