package org.ifaa.android.manager.face;

import android.content.Context;

public abstract class IFAAFaceManagerV2
  extends IFAAFaceManager
{
  public abstract void authenticate(String paramString, int paramInt, IFAAFaceManager.AuthenticatorCallback paramAuthenticatorCallback);
  
  public abstract int cancel(String paramString);
  
  public abstract void enroll(String paramString, int paramInt, IFAAFaceManager.AuthenticatorCallback paramAuthenticatorCallback);
  
  public int getVersion()
  {
    return 2;
  }
  
  public abstract byte[] invokeCommand(Context paramContext, byte[] paramArrayOfByte);
  
  public abstract void upgrade(String paramString);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/face/IFAAFaceManagerV2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */