package miui.security;

public class SecurityInjectManager
{
  public static native void blockSelfNetwork(boolean paramBoolean);
  
  public static native boolean hookFunctions(long paramLong, int paramInt);
  
  public static native boolean isNetworkBlocked();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/security/SecurityInjectManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */