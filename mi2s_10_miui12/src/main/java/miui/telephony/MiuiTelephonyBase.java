package miui.telephony;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.telephony.Rlog;
import java.util.ArrayList;
import java.util.List;

public abstract class MiuiTelephonyBase
  extends IMiuiTelephony.Stub
{
  private static String LOG_TAG = "MiuiTelephonyBase";
  
  public Bundle getCellLocationForSlot(int paramInt, String paramString)
  {
    Rlog.d(LOG_TAG, "unexpected getCellLocation method call");
    return null;
  }
  
  public String getDeviceId(String paramString)
  {
    return null;
  }
  
  public List<String> getDeviceIdList(String paramString)
  {
    Rlog.d(LOG_TAG, "unexpected getDeviceIdList method call");
    return new ArrayList(0);
  }
  
  public String getImei(int paramInt, String paramString)
  {
    return null;
  }
  
  public List<String> getImeiList(String paramString)
  {
    return new ArrayList(0);
  }
  
  public String getMeid(int paramInt, String paramString)
  {
    return null;
  }
  
  public List<String> getMeidList(String paramString)
  {
    return new ArrayList(0);
  }
  
  public String getSmallDeviceId(String paramString)
  {
    return null;
  }
  
  public boolean isFiveGCapable()
  {
    return false;
  }
  
  public boolean isGwsdSupport()
  {
    return false;
  }
  
  public boolean isImsRegistered(int paramInt)
  {
    return false;
  }
  
  public boolean isUserFiveGEnabled()
  {
    return false;
  }
  
  public boolean isVideoTelephonyAvailable(int paramInt)
  {
    return false;
  }
  
  public boolean isVolteEnabledByPlatform()
  {
    return false;
  }
  
  public boolean isVolteEnabledByPlatformForSlot(int paramInt)
  {
    return false;
  }
  
  public boolean isVolteEnabledByUser()
  {
    return false;
  }
  
  public boolean isVolteEnabledByUserForSlot(int paramInt)
  {
    return false;
  }
  
  public boolean isVtEnabledByPlatform()
  {
    return false;
  }
  
  public boolean isVtEnabledByPlatformForSlot(int paramInt)
  {
    return false;
  }
  
  public boolean isWifiCallingAvailable(int paramInt)
  {
    return false;
  }
  
  public void setCallForwardingOption(int paramInt1, int paramInt2, int paramInt3, String paramString, ResultReceiver paramResultReceiver)
  {
    Rlog.d(LOG_TAG, "unexpected setCallForwardingOption method call");
  }
  
  public void setIccCardActivate(int paramInt, boolean paramBoolean) {}
  
  public void setUserFiveGEnabled(boolean paramBoolean) {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/MiuiTelephonyBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */