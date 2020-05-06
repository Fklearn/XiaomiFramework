package org.mipay.android.manager;

import android.content.Context;

public abstract interface IMipayManager
{
  public abstract boolean contains(String paramString);
  
  public abstract int generateKeyPair(String paramString1, String paramString2);
  
  public abstract String getFpIds();
  
  public abstract int getSupportBIOTypes(Context paramContext);
  
  public abstract int getVersion();
  
  public abstract int removeAllKey();
  
  public abstract byte[] sign();
  
  public abstract int signInit(String paramString1, String paramString2);
  
  public abstract int signUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/mipay/android/manager/IMipayManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */