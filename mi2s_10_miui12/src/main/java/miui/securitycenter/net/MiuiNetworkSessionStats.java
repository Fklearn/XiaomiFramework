package miui.securitycenter.net;

import android.net.INetworkStatsService;
import android.net.INetworkStatsService.Stub;
import android.net.INetworkStatsSession;
import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.net.NetworkTemplate;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.SparseArray;
import java.util.HashMap;
import java.util.Map;

public class MiuiNetworkSessionStats
{
  private INetworkStatsService mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
  private INetworkStatsSession mStatsSession;
  
  private SparseArray<Map<String, Long>> buildNetworkStatsMap(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
  {
    NetworkStats.Entry localEntry1 = null;
    try
    {
      if ((this.mStatsSession != null) && (paramNetworkTemplate != null)) {
        paramNetworkTemplate = getSummaryForAllUid(paramNetworkTemplate, paramLong1, paramLong2, false);
      } else {
        return null;
      }
    }
    catch (Exception paramNetworkTemplate)
    {
      paramNetworkTemplate.printStackTrace();
      paramNetworkTemplate = localEntry1;
      if ((paramNetworkTemplate != null) && (paramNetworkTemplate.size() != 0))
      {
        SparseArray localSparseArray = new SparseArray(255);
        localEntry1 = new NetworkStats.Entry();
        int i = paramNetworkTemplate.size();
        for (int j = 0; j < i; j++)
        {
          NetworkStats.Entry localEntry2 = paramNetworkTemplate.getValues(j, localEntry1);
          if (localEntry2 != null)
          {
            int k = localEntry2.uid;
            Map localMap1 = (Map)localSparseArray.get(k);
            Map localMap2 = localMap1;
            if (localMap1 == null)
            {
              localMap2 = buildStatsMap();
              localSparseArray.put(k, localMap2);
            }
            if (localEntry2.set == 1)
            {
              localMap2.put("txForegroundBytes", Long.valueOf(((Long)localMap2.get("txForegroundBytes")).longValue() + localEntry2.txBytes));
              localMap2.put("rxForegroundBytes", Long.valueOf(((Long)localMap2.get("rxForegroundBytes")).longValue() + localEntry2.rxBytes));
            }
            localMap2.put("txBytes", Long.valueOf(((Long)localMap2.get("txBytes")).longValue() + localEntry2.txBytes));
            localMap2.put("rxBytes", Long.valueOf(((Long)localMap2.get("rxBytes")).longValue() + localEntry2.rxBytes));
          }
        }
        return localSparseArray;
      }
    }
    return null;
  }
  
  private Map<String, Long> buildStatsMap()
  {
    HashMap localHashMap = new HashMap();
    Long localLong = Long.valueOf(0L);
    localHashMap.put("rxForegroundBytes", localLong);
    localHashMap.put("txForegroundBytes", localLong);
    localHashMap.put("txBytes", localLong);
    localHashMap.put("rxBytes", localLong);
    return localHashMap;
  }
  
  private NetworkTemplate buildTemplateMobileAll(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    return NetworkTemplate.buildTemplateMobileAll(paramString);
  }
  
  private NetworkTemplate buildTemplateWifiWildcard()
  {
    return NetworkTemplate.buildTemplateWifiWildcard();
  }
  
  private NetworkStatsHistory getHistoryForUid(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException
  {
    return this.mStatsSession.getHistoryForUid(paramNetworkTemplate, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private long[] getHistoryStats(NetworkStatsHistory paramNetworkStatsHistory, long paramLong1, long paramLong2)
  {
    long[] arrayOfLong = new long[2];
    NetworkStatsHistory.Entry localEntry = new NetworkStatsHistory.Entry();
    if (paramNetworkStatsHistory != null)
    {
      paramNetworkStatsHistory = paramNetworkStatsHistory.getValues(paramLong1, paramLong2, localEntry);
      if (paramNetworkStatsHistory.rxBytes >= 0L) {
        arrayOfLong[0] = paramNetworkStatsHistory.rxBytes;
      } else {
        arrayOfLong[0] = 0L;
      }
      if (paramNetworkStatsHistory.txBytes >= 0L) {
        arrayOfLong[1] = paramNetworkStatsHistory.txBytes;
      } else {
        arrayOfLong[1] = 0L;
      }
    }
    return arrayOfLong;
  }
  
  private long getNetworkTotalBytes(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
    throws RemoteException
  {
    return this.mStatsSession.getSummaryForNetwork(paramNetworkTemplate, paramLong1, paramLong2).getTotalBytes();
  }
  
  private NetworkStats getSummaryForAllUid(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2, boolean paramBoolean)
    throws RemoteException
  {
    return this.mStatsSession.getSummaryForAllUid(paramNetworkTemplate, paramLong1, paramLong2, paramBoolean);
  }
  
  public void closeSession()
  {
    INetworkStatsSession localINetworkStatsSession = this.mStatsSession;
    if (localINetworkStatsSession != null) {
      try
      {
        localINetworkStatsSession.close();
      }
      catch (Exception localException) {}catch (RuntimeException localRuntimeException)
      {
        throw localRuntimeException;
      }
    }
  }
  
  public void forceUpdate()
  {
    try
    {
      this.mStatsService.forceUpdate();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException(localRemoteException);
    }
  }
  
  public long[] getMobileHistoryForUid(String paramString, int paramInt, long paramLong1, long paramLong2)
  {
    long[] arrayOfLong = new long[2];
    try
    {
      paramString = buildTemplateMobileAll(paramString);
      if (paramString == null) {
        return arrayOfLong;
      }
      paramString = getHistoryStats(getHistoryForUid(paramString, paramInt, -1, 0, 10), paramLong1, paramLong2);
    }
    catch (RemoteException paramString)
    {
      paramString.printStackTrace();
      paramString = arrayOfLong;
    }
    return paramString;
  }
  
  public SparseArray<Map<String, Long>> getMobileSummaryForAllUid(String paramString, long paramLong1, long paramLong2)
  {
    return buildNetworkStatsMap(buildTemplateMobileAll(paramString), paramLong1, paramLong2);
  }
  
  public long getNetworkMobileTotalBytes(String paramString, long paramLong1, long paramLong2)
  {
    long l1 = 0L;
    long l2;
    try
    {
      paramString = buildTemplateMobileAll(paramString);
      l2 = l1;
      if (paramString != null) {
        l2 = getNetworkTotalBytes(paramString, paramLong1, paramLong2);
      }
    }
    catch (RemoteException paramString)
    {
      paramString.printStackTrace();
      l2 = l1;
    }
    return l2;
  }
  
  public long getNetworkWifiTotalBytes(long paramLong1, long paramLong2)
  {
    long l1 = 0L;
    long l2;
    try
    {
      NetworkTemplate localNetworkTemplate = buildTemplateWifiWildcard();
      l2 = l1;
      if (localNetworkTemplate != null) {
        l2 = getNetworkTotalBytes(localNetworkTemplate, paramLong1, paramLong2);
      }
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
      l2 = l1;
    }
    return l2;
  }
  
  public long[] getWifiHistoryForUid(int paramInt, long paramLong1, long paramLong2)
  {
    Object localObject1 = new long[2];
    try
    {
      Object localObject2 = buildTemplateWifiWildcard();
      if (localObject2 == null) {
        return (long[])localObject1;
      }
      localObject2 = getHistoryStats(getHistoryForUid((NetworkTemplate)localObject2, paramInt, -1, 0, 10), paramLong1, paramLong2);
      localObject1 = localObject2;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException.printStackTrace();
    }
    return (long[])localObject1;
  }
  
  public SparseArray<Map<String, Long>> getWifiSummaryForAllUid(long paramLong1, long paramLong2)
  {
    return buildNetworkStatsMap(buildTemplateWifiWildcard(), paramLong1, paramLong2);
  }
  
  public void openSession()
  {
    try
    {
      this.mStatsSession = this.mStatsService.openSession();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException(localRemoteException);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/net/MiuiNetworkSessionStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */