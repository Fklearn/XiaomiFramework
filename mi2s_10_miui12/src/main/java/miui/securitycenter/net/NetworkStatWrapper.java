package miui.securitycenter.net;

import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.TrafficStats;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.ServiceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import miui.securitycenter.NetworkUtils;

public class NetworkStatWrapper
{
  private static INetworkManagementService mNMService;
  private static NetworkStats mPreSnapshot = null;
  private static ArrayList<Map<String, String>> mStatsInfo = new ArrayList();
  
  private static NetworkStats getNetworkStatsDetail()
  {
    try
    {
      if (mNMService == null) {
        mNMService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
      }
      NetworkStats localNetworkStats = mNMService.getNetworkStatsDetail();
      return localNetworkStats;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public static long getRxBytes(String paramString)
  {
    return TrafficStats.getRxBytes(paramString);
  }
  
  public static ArrayList<Map<String, String>> getStatsInfo()
  {
    try
    {
      Object localObject = getNetworkStatsDetail();
      if (localObject == null) {
        return mStatsInfo;
      }
      NetworkStats localNetworkStats = NetworkUtils.getAdjustedNetworkStatsTethering();
      if ((localNetworkStats != null) && (localNetworkStats.size() > 0)) {
        ((NetworkStats)localObject).combineAllValues(localNetworkStats);
      }
      if (mPreSnapshot == null)
      {
        mPreSnapshot = (NetworkStats)localObject;
      }
      else
      {
        mStatsInfo.clear();
        localNetworkStats = NetworkStats.subtract((NetworkStats)localObject, mPreSnapshot, null, null);
        mPreSnapshot = (NetworkStats)localObject;
        localObject = null;
        if (localNetworkStats != null) {
          for (int i = 0; i < localNetworkStats.size(); i++)
          {
            localObject = localNetworkStats.getValues(i, (NetworkStats.Entry)localObject);
            HashMap localHashMap = new java/util/HashMap;
            localHashMap.<init>();
            localHashMap.put("uid", String.valueOf(((NetworkStats.Entry)localObject).uid));
            localHashMap.put("iface", ((NetworkStats.Entry)localObject).iface);
            localHashMap.put("rxBytes", String.valueOf(((NetworkStats.Entry)localObject).rxBytes));
            localHashMap.put("txBytes", String.valueOf(((NetworkStats.Entry)localObject).txBytes));
            localHashMap.put("tag", String.valueOf(((NetworkStats.Entry)localObject).tag));
            mStatsInfo.add(localHashMap);
          }
        }
      }
    }
    catch (Exception localException) {}
    return mStatsInfo;
  }
  
  public static long getTxBytes(String paramString)
  {
    return TrafficStats.getTxBytes(paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/net/NetworkStatWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */