package org.ifaa.android.manager;

public abstract class IFAAManagerV3
  extends IFAAManagerV2
{
  public static final String KEY_FINGERPRINT_FULLVIEW = "org.ifaa.ext.key.CUSTOM_VIEW";
  public static final String KEY_GET_SENSOR_LOCATION = "org.ifaa.ext.key.GET_SENSOR_LOCATION";
  public static final String VALUE_FINGERPRINT_DISABLE = "disable";
  public static final String VLAUE_FINGERPRINT_ENABLE = "enable";
  
  public abstract String getExtInfo(int paramInt, String paramString);
  
  public abstract void setExtInfo(int paramInt, String paramString1, String paramString2);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/IFAAManagerV3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */