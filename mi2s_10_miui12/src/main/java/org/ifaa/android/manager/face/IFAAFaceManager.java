package org.ifaa.android.manager.face;

public abstract class IFAAFaceManager
{
  public static final int ERR_FACE_CANCEL = 102;
  public static final int ERR_FACE_LOCKED = 129;
  public static final int ERR_FACE_TIMEOUT = 113;
  public static final int FAIL_FACE_AUTHENTICATION = 103;
  public static final int IFAA_FACE_ERR_AUTHENTICATOR_SIGN = 2046820367;
  public static final int IFAA_FACE_ERR_BUF_TOO_SHORT = 2046820356;
  public static final int IFAA_FACE_ERR_CANCELLED = -2;
  public static final int IFAA_FACE_ERR_ERASE = 2046820364;
  public static final int IFAA_FACE_ERR_GET_CELLINFO = 2046820369;
  public static final int IFAA_FACE_ERR_GET_DEVICEID = 2046820366;
  public static final int IFAA_FACE_ERR_GET_FACEINFO = 2046820368;
  public static final int IFAA_FACE_ERR_HASH = 2046820358;
  public static final int IFAA_FACE_ERR_INVALID_PARAM = 2046820354;
  public static final int IFAA_FACE_ERR_KEY_GEN = 2046820361;
  public static final int IFAA_FACE_ERR_MTEE_BUSY = -1;
  public static final int IFAA_FACE_ERR_MTEE_CALL = 2063597568;
  public static final int IFAA_FACE_ERR_NOT_MATCH = 2046820365;
  public static final int IFAA_FACE_ERR_READ = 2046820362;
  public static final int IFAA_FACE_ERR_SIGN = 2046820359;
  public static final int IFAA_FACE_ERR_SUCCESS = 1;
  public static final int IFAA_FACE_ERR_TIMEOUT = 2046820357;
  public static final int IFAA_FACE_ERR_UNKNOWN = 2046820353;
  public static final int IFAA_FACE_ERR_UNKNOWN_CMD = 2046820355;
  public static final int IFAA_FACE_ERR_VERIFY = 2046820360;
  public static final int IFAA_FACE_ERR_WRITE = 2046820363;
  public static final int STATUS_FACE_BRIGHT = 407;
  public static final int STATUS_FACE_CAPTURE = 414;
  public static final int STATUS_FACE_DARK = 406;
  public static final int STATUS_FACE_EYE_CLOSED = 403;
  public static final int STATUS_FACE_FAR = 404;
  public static final int STATUS_FACE_HACKER = 418;
  public static final int STATUS_FACE_IMAGE_DIRTY = 419;
  public static final int STATUS_FACE_INSUFFICIENT = 402;
  public static final int STATUS_FACE_MOUTH_OCCLUSION = 409;
  public static final int STATUS_FACE_NEAR = 405;
  public static final int STATUS_FACE_NO_FACE = 415;
  public static final int STATUS_FACE_OFFSET_BOTTOM = 413;
  public static final int STATUS_FACE_OFFSET_LEFT = 410;
  public static final int STATUS_FACE_OFFSET_RIGHT = 411;
  public static final int STATUS_FACE_OFFSET_TOP = 412;
  public static final int STATUS_FACE_PARTIAL = 401;
  public static final int STATUS_FACE_QUALITY = 408;
  public static final int STATUS_FACE_TOO_FAST = 417;
  public static final int STATUS_FACE_TOO_SLOW = 416;
  public static final int SUCC_FACE_AUTHENTICATION = 100;
  
  public abstract void authenticate(int paramInt1, int paramInt2, AuthenticatorCallback paramAuthenticatorCallback);
  
  public abstract int cancel(int paramInt);
  
  public int getVersion()
  {
    return 1;
  }
  
  public static abstract class AuthenticatorCallback
  {
    public void onAuthenticationError(int paramInt) {}
    
    public void onAuthenticationFailed(int paramInt) {}
    
    public void onAuthenticationStatus(int paramInt) {}
    
    public void onAuthenticationSucceeded() {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/face/IFAAFaceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */