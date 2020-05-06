package miui.securitycenter.utils;

import android.content.Context;
import android.content.Intent;
import android.net.IConnectivityManager;
import android.net.IConnectivityManager.Stub;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class MiAssistantUtil
{
  private static final String EXTRA_LINK_PROPERTIES = "linkProperties";
  private static final String EXTRA_NETWORK_INFO = "networkinfo";
  private static final String INTERFACE_USBNET0 = "usbnet0";
  private static final String TAG = "MiAssistantManager";
  private static final String USB_SHARE_NET_STATE_CHANGE = "miui.intent.action.USB_SHARE_NET_STATE_CHANGE";
  
  public static String getActiveInterfaceName()
  {
    Object localObject1 = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
    Object localObject2 = null;
    try
    {
      localObject1 = ((IConnectivityManager)localObject1).getActiveLinkProperties();
      localObject2 = localObject1;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    if (localObject2 != null) {
      return ((LinkProperties)localObject2).getInterfaceName();
    }
    Log.e("MiAssistantManager", "activeLink is null");
    return "null";
  }
  
  public static void usbnet0Down(Context paramContext)
  {
    Intent localIntent = new Intent("miui.intent.action.USB_SHARE_NET_STATE_CHANGE");
    NetworkInfo localNetworkInfo = new NetworkInfo(9, 0, "ETHERNET", "");
    localNetworkInfo.setIsAvailable(false);
    localNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, null, null);
    localIntent.putExtra("networkinfo", localNetworkInfo);
    localIntent.putExtra("linkProperties", new LinkProperties());
    paramContext.sendBroadcast(localIntent);
    paramContext = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
    try
    {
      paramContext.setInterfaceDown("usbnet0");
    }
    catch (Exception paramContext)
    {
      Log.w("MiAssistantManager", "disable usbnet0 error");
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/utils/MiAssistantUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */