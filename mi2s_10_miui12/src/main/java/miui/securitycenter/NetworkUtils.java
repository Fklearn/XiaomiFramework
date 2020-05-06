package miui.securitycenter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.IConnectivityManager;
import android.net.IConnectivityManager.Stub;
import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.system.Os;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.net.VpnConfig;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetworkUtils
{
  private static final String TAG = "OverLayUtil";
  private static INetworkManagementService mNMService;
  private static ClassLoader mSystemServiceClassLoader;
  
  public static NetworkStats getAdjustedNetworkStatsTethering()
  {
    NetworkStats localNetworkStats1 = null;
    Object localObject = localNetworkStats1;
    NetworkStats localNetworkStats2;
    try
    {
      if (mNMService == null)
      {
        localObject = localNetworkStats1;
        mNMService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
      }
      localObject = localNetworkStats1;
      localNetworkStats1 = mNMService.getNetworkStatsTethering(1);
      if (localNetworkStats1 != null)
      {
        localObject = localNetworkStats1;
        int i = localNetworkStats1.size();
        if (i > 0) {
          try
          {
            if (mSystemServiceClassLoader == null)
            {
              String str = Os.getenv("SYSTEMSERVERCLASSPATH");
              if (str != null)
              {
                localObject = new dalvik/system/PathClassLoader;
                ((PathClassLoader)localObject).<init>(str, ClassLoader.getSystemClassLoader());
                mSystemServiceClassLoader = (ClassLoader)localObject;
              }
              else
              {
                mSystemServiceClassLoader = Thread.currentThread().getContextClassLoader();
              }
            }
            Class.forName("com.android.server.NetPluginDelegate", false, mSystemServiceClassLoader).getMethod("getTetherStats", new Class[] { NetworkStats.class, NetworkStats.class, NetworkStats.class }).invoke(null, new Object[] { localNetworkStats1, null, null });
          }
          catch (Exception localException2)
          {
            localObject = localNetworkStats1;
            Log.e("OverLayUtil", "an exception occurred!!", localException2);
          }
          catch (ClassNotFoundException localClassNotFoundException) {}
        }
      }
      localNetworkStats2 = localNetworkStats1;
    }
    catch (Exception localException1)
    {
      Log.e("OverLayUtil", "an exception occurred!!", localException1);
    }
    return localNetworkStats2;
  }
  
  public static String getMobileIface(Context paramContext)
  {
    Object localObject = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
    paramContext = null;
    try
    {
      localObject = ((IConnectivityManager)localObject).getLinkPropertiesForType(0);
      paramContext = (Context)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    if (paramContext != null) {
      return paramContext.getInterfaceName();
    }
    return "";
  }
  
  public static ArrayList<Map<String, String>> getNetworkStatsTethering()
  {
    Object localObject = null;
    NetworkStats localNetworkStats = getAdjustedNetworkStatsTethering();
    NetworkStats.Entry localEntry = null;
    if (localNetworkStats != null)
    {
      ArrayList localArrayList = new ArrayList();
      for (int i = 0;; i++)
      {
        localObject = localArrayList;
        if (i >= localNetworkStats.size()) {
          break;
        }
        localEntry = localNetworkStats.getValues(i, localEntry);
        localObject = new HashMap();
        ((Map)localObject).put("uid", String.valueOf(localEntry.uid));
        ((Map)localObject).put("iface", localEntry.iface);
        ((Map)localObject).put("rxBytes", String.valueOf(localEntry.rxBytes));
        ((Map)localObject).put("txBytes", String.valueOf(localEntry.txBytes));
        ((Map)localObject).put("tag", String.valueOf(localEntry.tag));
        localArrayList.add(localObject);
      }
    }
    return (ArrayList<Map<String, String>>)localObject;
  }
  
  public static boolean isVpnConnected()
  {
    boolean bool = false;
    try
    {
      VpnConfig localVpnConfig = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity")).getVpnConfig(UserHandle.myUserId());
      if (localVpnConfig != null) {
        bool = true;
      }
      return bool;
    }
    catch (Exception localException)
    {
      Log.e("OverLayUtil", "isVpnConnected", localException);
    }
    return false;
  }
  
  public static void saveWifiConfiguration(Context paramContext, InetAddress paramInetAddress, WifiConfiguration paramWifiConfiguration)
    throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException
  {
    if ((paramContext != null) && (paramInetAddress != null) && (paramWifiConfiguration != null))
    {
      WifiManager localWifiManager = (WifiManager)paramContext.getSystemService("wifi");
      if (localWifiManager == null) {
        return;
      }
      int i = 0;
      Object localObject;
      if (paramWifiConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC)
      {
        localObject = paramWifiConfiguration.getStaticIpConfiguration();
        paramContext = null;
        if (((StaticIpConfiguration)localObject).dnsServers.size() > 0) {
          paramContext = (InetAddress)((StaticIpConfiguration)localObject).dnsServers.get(0);
        }
        ((StaticIpConfiguration)localObject).dnsServers.clear();
        ((StaticIpConfiguration)localObject).dnsServers.add(paramInetAddress);
        if (paramContext != null) {
          ((StaticIpConfiguration)localObject).dnsServers.add(paramContext);
        }
        i = 1;
      }
      else if (paramWifiConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.DHCP)
      {
        paramContext = new StaticIpConfiguration();
        localObject = localWifiManager.getDhcpInfo();
        if (localObject != null)
        {
          paramContext.ipAddress = new LinkAddress(android.net.NetworkUtils.intToInetAddress(((DhcpInfo)localObject).ipAddress), android.net.NetworkUtils.netmaskIntToPrefixLength(((DhcpInfo)localObject).netmask));
          paramContext.gateway = android.net.NetworkUtils.intToInetAddress(((DhcpInfo)localObject).gateway);
          paramContext.dnsServers.add(paramInetAddress);
          try
          {
            paramContext.dnsServers.add(android.net.NetworkUtils.intToInetAddress(((DhcpInfo)localObject).dns1));
          }
          catch (Exception paramInetAddress)
          {
            paramContext.dnsServers.add(android.net.NetworkUtils.numericToInetAddress("8.8.8.8"));
          }
          paramWifiConfiguration.setIpConfiguration(new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, paramContext, null));
        }
        i = 1;
      }
      if (i != 0) {
        localWifiManager.save(paramWifiConfiguration, new WifiManager.ActionListener()
        {
          public void onFailure(int paramAnonymousInt)
          {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Failure to save wifi configuration! reason=");
            localStringBuilder.append(paramAnonymousInt);
            Log.i("OverLayUtil", localStringBuilder.toString());
          }
          
          public void onSuccess()
          {
            Log.i("OverLayUtil", "save  wifi configuration success!");
          }
        });
      }
      return;
    }
    Log.i("OverLayUtil", "saveWifiConfiguration:  invalidate parameter!");
  }
  
  public static void setMobileDataState(Context paramContext, boolean paramBoolean)
  {
    ((TelephonyManager)paramContext.getSystemService("phone")).setDataEnabled(paramBoolean);
  }
  
  public static void vpnPrepareAndAuthorize(String paramString)
  {
    IConnectivityManager localIConnectivityManager = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
    try
    {
      int i = UserHandle.myUserId();
      if (localIConnectivityManager.prepareVpn(null, paramString, i)) {
        localIConnectivityManager.setVpnPackageAuthorization(paramString, i, true);
      }
    }
    catch (RemoteException paramString)
    {
      Log.e("OverLayUtil", "prepareAndAuthorize", paramString);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/NetworkUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */