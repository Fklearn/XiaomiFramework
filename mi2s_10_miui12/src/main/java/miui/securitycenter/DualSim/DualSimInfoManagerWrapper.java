package miui.securitycenter.DualSim;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.telephony.SubscriptionManager.OnSubscriptionsChangedListener;

public class DualSimInfoManagerWrapper
{
  private static ArrayList<ISimInfoChangeWrapperListener> mListeners = new ArrayList();
  private static SimInfoChangeImpl mSimInfoChangeImpl = new SimInfoChangeImpl(null);
  
  private static void broadcastSubscriptionsChanged()
  {
    synchronized (mListeners)
    {
      Iterator localIterator = mListeners.iterator();
      while (localIterator.hasNext()) {
        ((ISimInfoChangeWrapperListener)localIterator.next()).onSubscriptionsChanged();
      }
      return;
    }
  }
  
  public static List<Map<String, String>> getSimInfoList(Context paramContext)
  {
    try
    {
      Object localObject = SubscriptionManager.getDefault().getSubscriptionInfoList();
      if ((localObject != null) && (((List)localObject).size() != 0))
      {
        if (((List)localObject).size() > 0)
        {
          paramContext = new miui/securitycenter/DualSim/DualSimInfoManagerWrapper$SubscriptionInfoComparable;
          paramContext.<init>(null);
          Collections.sort((List)localObject, paramContext);
        }
        paramContext = new java/util/ArrayList;
        paramContext.<init>();
        Iterator localIterator = ((List)localObject).iterator();
        while (localIterator.hasNext())
        {
          SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)localIterator.next();
          if (localSubscriptionInfo.isActivated())
          {
            localObject = new java/util/HashMap;
            ((HashMap)localObject).<init>();
            ((Map)localObject).put("simId", String.valueOf(localSubscriptionInfo.getSubscriptionId()));
            ((Map)localObject).put("slotNum", String.valueOf(localSubscriptionInfo.getSlotId()));
            ((Map)localObject).put("simName", localSubscriptionInfo.getDisplayName().toString());
            ((Map)localObject).put("iccId", localSubscriptionInfo.getIccId());
            paramContext.add(localObject);
          }
        }
        return paramContext;
      }
      return null;
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  private static void registerChangeListener(SimInfoChangeImpl paramSimInfoChangeImpl)
  {
    SubscriptionManager.getDefault().addOnSubscriptionsChangedListener(paramSimInfoChangeImpl);
  }
  
  public static void registerSimInfoChangeListener(Context paramContext, ISimInfoChangeWrapperListener paramISimInfoChangeWrapperListener)
  {
    paramContext = mListeners;
    if (paramISimInfoChangeWrapperListener != null) {}
    try
    {
      if (!mListeners.contains(paramISimInfoChangeWrapperListener))
      {
        mListeners.add(paramISimInfoChangeWrapperListener);
        registerChangeListener(mSimInfoChangeImpl);
      }
      return;
    }
    finally {}
  }
  
  private static void unRegisterChangeListener(SimInfoChangeImpl paramSimInfoChangeImpl)
  {
    SubscriptionManager.getDefault().removeOnSubscriptionsChangedListener(paramSimInfoChangeImpl);
  }
  
  public static void unRegisterSimInfoChangeListener(Context paramContext, ISimInfoChangeWrapperListener paramISimInfoChangeWrapperListener)
  {
    paramContext = mListeners;
    if (paramISimInfoChangeWrapperListener != null) {}
    try
    {
      if (mListeners.contains(paramISimInfoChangeWrapperListener))
      {
        mListeners.remove(paramISimInfoChangeWrapperListener);
        unRegisterChangeListener(mSimInfoChangeImpl);
      }
      return;
    }
    finally {}
  }
  
  public static abstract interface ISimInfoChangeWrapperListener
  {
    public abstract void onSubscriptionsChanged();
  }
  
  private static class SimInfoChangeImpl
    implements SubscriptionManager.OnSubscriptionsChangedListener
  {
    public void onSubscriptionsChanged() {}
  }
  
  private static final class SubscriptionInfoComparable
    implements Comparator<SubscriptionInfo>
  {
    public int compare(SubscriptionInfo paramSubscriptionInfo1, SubscriptionInfo paramSubscriptionInfo2)
    {
      return paramSubscriptionInfo1.getSlotId() - paramSubscriptionInfo2.getSlotId();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/DualSim/DualSimInfoManagerWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */