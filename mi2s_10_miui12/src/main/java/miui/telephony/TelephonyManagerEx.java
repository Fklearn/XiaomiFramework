package miui.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Country;
import android.location.CountryDetector;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.provider.Settings.Global;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.Rlog;
import android.telephony.SignalStrength;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.android.internal.telecom.ITelecomService;
import com.android.internal.telecom.ITelecomService.Stub;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephony.Stub;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.ITelephonyRegistry.Stub;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.util.AppConstants;

public class TelephonyManagerEx
  extends TelephonyManager
{
  public static final int NETWORK_CLASS_2_G = 1;
  public static final int NETWORK_CLASS_3_G = 2;
  public static final int NETWORK_CLASS_4_G = 3;
  public static final int NETWORK_CLASS_UNKNOWN = 0;
  public static final int NETWORK_TYPE_GSM = 16;
  public static final int NETWORK_TYPE_TD_SCDMA = 17;
  public static final String PROPERTY_DBG_VOLTE_AVAIL_OVERRIDE = "persist.dbg.volte_avail_ovr";
  public static final String PROPERTY_DBG_VT_AVAIL_OVERRIDE = "persist.dbg.vt_avail_ovr";
  private static final String TAG = "TelephonyManager";
  private static ITelephonyRegistry sRegistry;
  
  private static String getCountryIso(Context paramContext)
  {
    Object localObject1 = null;
    Object localObject2 = (CountryDetector)paramContext.getSystemService("country_detector");
    Object localObject3 = localObject1;
    if (localObject2 != null)
    {
      localObject2 = ((CountryDetector)localObject2).detectCountry();
      localObject3 = localObject1;
      if (localObject2 != null) {
        localObject3 = ((Country)localObject2).getCountryIso();
      }
    }
    localObject1 = localObject3;
    if (localObject3 == null)
    {
      localObject1 = paramContext.getResources().getConfiguration().locale.getCountry();
      paramContext = new StringBuilder();
      paramContext.append("No CountryDetector; falling back to countryIso based on locale: ");
      paramContext.append((String)localObject1);
      Rlog.w("TelephonyManager", paramContext.toString());
    }
    return (String)localObject1;
  }
  
  public static TelephonyManagerEx getDefault()
  {
    return Holder.INSTANCE;
  }
  
  private ITelephony getITelephony()
  {
    return ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
  }
  
  private ITelecomService getTelecomService()
  {
    return ITelecomService.Stub.asInterface(ServiceManager.getService("telecom"));
  }
  
  public static boolean isLocalEmergencyNumber(Context paramContext, String paramString)
  {
    return isLocalEmergencyNumberInternal(paramContext, paramString, true);
  }
  
  private static boolean isLocalEmergencyNumberInternal(Context paramContext, String paramString, boolean paramBoolean)
  {
    String str = getCountryIso(paramContext);
    Object localObject;
    int i;
    if ("IN".equalsIgnoreCase(str))
    {
      localObject = new String[4];
      localObject[0] = "100";
      localObject[1] = "101";
      localObject[2] = "102";
      localObject[3] = "108";
      i = localObject.length;
      for (j = 0; j < i; j++) {
        if (localObject[j].equals(paramString))
        {
          paramContext = new StringBuilder();
          paramContext.append("isLocalEmergencyNumberInternal :number:");
          paramContext.append(paramString);
          paramContext.append(" is not a real IN emergency number,return false");
          Rlog.d("TelephonyManager", paramContext.toString());
          return false;
        }
      }
    }
    int k = Holder.INSTANCE.getPhoneCount();
    if (k < 2)
    {
      if (paramBoolean) {
        paramBoolean = PhoneNumberUtils.isLocalEmergencyNumber(paramContext, paramString);
      } else {
        paramBoolean = PhoneNumberUtils.isPotentialLocalEmergencyNumber(paramContext, paramString);
      }
      return paramBoolean;
    }
    if (("IT".equalsIgnoreCase(str)) || ("MM".equalsIgnoreCase(str)))
    {
      int m = 0;
      i = -1;
      j = 0;
      while (j < k)
      {
        int n = m;
        if (Holder.TELEPHONY_MANAGER.getSimState(j) == 5)
        {
          n = m + 1;
          i = j;
        }
        j++;
        m = n;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("isLocalEmergencyNumberInternal : in Italy or Myanmar,insert ");
      ((StringBuilder)localObject).append(m);
      ((StringBuilder)localObject).append(" sim card, validSlot is");
      ((StringBuilder)localObject).append(i);
      Rlog.d("TelephonyManager", ((StringBuilder)localObject).toString());
      if (m == 1)
      {
        j = Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(i);
        return ((paramBoolean) && (PhoneNumberUtils.isLocalEmergencyNumber(paramContext, j, paramString))) || ((!paramBoolean) && (PhoneNumberUtils.isPotentialLocalEmergencyNumber(paramContext, j, paramString)));
      }
    }
    for (int j = 0; j < k; j++)
    {
      i = Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(j);
      if (((paramBoolean) && (PhoneNumberUtils.isLocalEmergencyNumber(paramContext, i, paramString))) || ((!paramBoolean) && (PhoneNumberUtils.isPotentialLocalEmergencyNumber(paramContext, i, paramString)))) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isPotentialLocalEmergencyNumber(Context paramContext, String paramString)
  {
    return isLocalEmergencyNumberInternal(paramContext, paramString, false);
  }
  
  private int normalizeSlotId(int paramInt)
  {
    if (paramInt == SubscriptionManager.DEFAULT_SLOT_ID) {
      return Holder.SUBSCRIPTION_MANAGER.getDefaultSlotId();
    }
    return paramInt;
  }
  
  private int normalizeSubscriptionId(int paramInt)
  {
    if (paramInt == SubscriptionManager.DEFAULT_SUBSCRIPTION_ID) {
      return Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId();
    }
    return paramInt;
  }
  
  public void answerRingingCall()
  {
    try
    {
      getTelecomService().acceptRingingCall(Holder.CONTEXT.getOpPackageName());
    }
    catch (RemoteException localRemoteException)
    {
      Rlog.e("TelephonyManager", "Error calling ITelecomService#acceptRingingCall", localRemoteException);
    }
  }
  
  public void cancelMissedCallsNotification()
  {
    try
    {
      getTelecomService().cancelMissedCallsNotification(Holder.CONTEXT.getOpPackageName());
    }
    catch (RemoteException localRemoteException)
    {
      Rlog.e("TelephonyManager", "Error call ITelecomService#cancelMissedCallsNotification", localRemoteException);
    }
  }
  
  public boolean enableDataConnectivity()
  {
    return Holder.TELEPHONY_MANAGER.enableDataConnectivity();
  }
  
  public boolean enableDataConnectivityForSlot(int paramInt)
  {
    if ((paramInt != SubscriptionManager.DEFAULT_SLOT_ID) && (paramInt != Holder.SUBSCRIPTION_MANAGER.getDefaultDataSlotId())) {
      return false;
    }
    return Holder.TELEPHONY_MANAGER.enableDataConnectivity();
  }
  
  public boolean enableDataConnectivityForSubscription(int paramInt)
  {
    if ((paramInt != SubscriptionManager.DEFAULT_SUBSCRIPTION_ID) && (paramInt != Holder.SUBSCRIPTION_MANAGER.getDefaultDataSubscriptionId())) {
      return false;
    }
    return Holder.TELEPHONY_MANAGER.enableDataConnectivity();
  }
  
  public boolean endCall()
  {
    try
    {
      boolean bool = getTelecomService().endCall(Holder.CONTEXT.getOpPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Rlog.e("TelephonyManager", "Error calling ITelecomService#endCall", localRemoteException);
    }
    return false;
  }
  
  public List<CellInfo> getAllCellInfo()
  {
    return getAllCellInfoForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public List<CellInfo> getAllCellInfoForSlot(int paramInt)
  {
    return getAllCellInfoForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public List<CellInfo> getAllCellInfoForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getAllCellInfo();
  }
  
  public int getCallState()
  {
    return Holder.TELEPHONY_MANAGER.getCallState();
  }
  
  public int getCallStateForSlot(int paramInt)
  {
    return getCallStateForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public int getCallStateForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getCallState(normalizeSubscriptionId(paramInt));
  }
  
  public CellLocation getCellLocation()
  {
    return Holder.TELEPHONY_MANAGER.getCellLocation();
  }
  
  public CellLocation getCellLocationForSlot(int paramInt)
  {
    try
    {
      localObject = getMiuiTelephony();
      if (localObject == null) {
        return null;
      }
      Bundle localBundle = ((IMiuiTelephony)localObject).getCellLocationForSlot(paramInt, Holder.CONTEXT.getOpPackageName());
      if (localBundle.isEmpty())
      {
        Rlog.d("TelephonyManager", "getCellLocationForSlot returning null because bundle is empty");
        return null;
      }
      localObject = null;
      paramInt = android.telephony.TelephonyManager.getDefault().getCurrentPhoneTypeForSlot(paramInt);
      if (paramInt != 1)
      {
        if (paramInt == 2) {
          localObject = new CdmaCellLocation(localBundle);
        }
      }
      else {
        localObject = new GsmCellLocation(localBundle);
      }
      if (((CellLocation)localObject).isEmpty())
      {
        Rlog.d("TelephonyManager", "getCellLocationForSlot returning null because CellLocation is empty");
        return null;
      }
      return (CellLocation)localObject;
    }
    catch (Exception localException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getCellLocationForSlot returning null due to Exception ");
      ((StringBuilder)localObject).append(localException);
      Rlog.d("TelephonyManager", ((StringBuilder)localObject).toString());
    }
    return null;
  }
  
  public CellLocation getCellLocationForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getCellLocation();
  }
  
  public int getCtVolteSupportedMode()
  {
    return Holder.CT_VOLTE_SUPPORTED_MODE;
  }
  
  public int getDataActivity()
  {
    return Holder.TELEPHONY_MANAGER.getDataActivity();
  }
  
  public int getDataActivityForSlot(int paramInt)
  {
    if ((paramInt != SubscriptionManager.DEFAULT_SLOT_ID) && (paramInt != Holder.SUBSCRIPTION_MANAGER.getDefaultDataSlotId())) {
      return 0;
    }
    return Holder.TELEPHONY_MANAGER.getDataActivity();
  }
  
  public int getDataActivityForSubscription(int paramInt)
  {
    if ((paramInt != SubscriptionManager.DEFAULT_SUBSCRIPTION_ID) && (paramInt != Holder.SUBSCRIPTION_MANAGER.getDefaultDataSubscriptionId())) {
      return 0;
    }
    return Holder.TELEPHONY_MANAGER.getDataActivity();
  }
  
  public int getDataState()
  {
    return Holder.TELEPHONY_MANAGER.getDataState();
  }
  
  public int getDataStateForSlot(int paramInt)
  {
    if ((paramInt != SubscriptionManager.DEFAULT_SLOT_ID) && (paramInt != Holder.SUBSCRIPTION_MANAGER.getDefaultDataSlotId())) {
      return 0;
    }
    return Holder.TELEPHONY_MANAGER.getDataState();
  }
  
  public int getDataStateForSubscription(int paramInt)
  {
    if ((paramInt != SubscriptionManager.DEFAULT_SUBSCRIPTION_ID) && (paramInt != Holder.SUBSCRIPTION_MANAGER.getDefaultDataSubscriptionId())) {
      return 0;
    }
    return Holder.TELEPHONY_MANAGER.getDataState();
  }
  
  public String getDeviceId()
  {
    return getDeviceIdForSlot(Holder.SUBSCRIPTION_MANAGER.getDefaultSlotId());
  }
  
  public String getDeviceIdForSlot(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getDeviceId(normalizeSlotId(paramInt));
  }
  
  public String getDeviceIdForSubscription(int paramInt)
  {
    return getDeviceIdForSlot(Holder.SUBSCRIPTION_MANAGER.getSlotIdForSubscription(paramInt));
  }
  
  public List<String> getDeviceIdList()
  {
    try
    {
      Object localObject = getMiuiTelephony();
      if (localObject == null) {
        localObject = null;
      } else {
        localObject = ((IMiuiTelephony)localObject).getDeviceIdList(Holder.CONTEXT.getOpPackageName());
      }
      return (List<String>)localObject;
    }
    catch (Exception localException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("getDeviceIdList");
      localStringBuilder.append(localException);
      Rlog.e("TelephonyManager", localStringBuilder.toString());
    }
    return new ArrayList(0);
  }
  
  public String getDeviceSoftwareVersion()
  {
    return Holder.TELEPHONY_MANAGER.getDeviceSoftwareVersion();
  }
  
  public String getDeviceSoftwareVersionForSlot(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getDeviceSoftwareVersion();
  }
  
  public String getDeviceSoftwareVersionForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getDeviceSoftwareVersion();
  }
  
  public String getImei()
  {
    return getImeiForSlot(Holder.SUBSCRIPTION_MANAGER.getDefaultSlotId());
  }
  
  public String getImeiForSlot(int paramInt)
  {
    String str = null;
    try
    {
      localObject = getMiuiTelephony();
      if (localObject != null) {
        for (;;)
        {
          str = ((IMiuiTelephony)localObject).getImei(normalizeSlotId(paramInt), Holder.CONTEXT.getOpPackageName());
        }
      }
      return str;
    }
    catch (Exception localException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getImeiForSlot ");
      ((StringBuilder)localObject).append(localException);
      Rlog.e("TelephonyManager", ((StringBuilder)localObject).toString());
    }
    return null;
  }
  
  public String getImeiForSubscription(int paramInt)
  {
    return getImeiForSlot(Holder.SUBSCRIPTION_MANAGER.getSlotIdForSubscription(paramInt));
  }
  
  public List<String> getImeiList()
  {
    try
    {
      localObject = getMiuiTelephony();
      if (localObject == null) {
        localObject = null;
      } else {
        localObject = ((IMiuiTelephony)localObject).getImeiList(Holder.CONTEXT.getOpPackageName());
      }
      return (List<String>)localObject;
    }
    catch (Exception localException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getImeiList");
      ((StringBuilder)localObject).append(localException);
      Rlog.e("TelephonyManager", ((StringBuilder)localObject).toString());
    }
    return new ArrayList(0);
  }
  
  public String getLine1Number()
  {
    return getLine1NumberForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getLine1NumberForSlot(int paramInt)
  {
    return getLine1NumberForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getLine1NumberForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getLine1Number(normalizeSubscriptionId(paramInt));
  }
  
  public String getMeid()
  {
    return getMeidForSlot(Holder.SUBSCRIPTION_MANAGER.getDefaultSlotId());
  }
  
  public String getMeidForSlot(int paramInt)
  {
    Object localObject = null;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        for (;;)
        {
          localObject = localIMiuiTelephony.getMeid(normalizeSlotId(paramInt), Holder.CONTEXT.getOpPackageName());
        }
      }
      return (String)localObject;
    }
    catch (Exception localException)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getMeidForSlot ");
      ((StringBuilder)localObject).append(localException);
      Rlog.e("TelephonyManager", ((StringBuilder)localObject).toString());
    }
    return null;
  }
  
  public String getMeidForSubscription(int paramInt)
  {
    return getMeidForSlot(Holder.SUBSCRIPTION_MANAGER.getSlotIdForSubscription(paramInt));
  }
  
  public List<String> getMeidList()
  {
    try
    {
      localObject = getMiuiTelephony();
      if (localObject == null) {
        localObject = null;
      } else {
        localObject = ((IMiuiTelephony)localObject).getMeidList(Holder.CONTEXT.getOpPackageName());
      }
      return (List<String>)localObject;
    }
    catch (Exception localException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getMeidList");
      ((StringBuilder)localObject).append(localException);
      Rlog.e("TelephonyManager", ((StringBuilder)localObject).toString());
    }
    return new ArrayList(0);
  }
  
  public String getMiuiDeviceId()
  {
    Object localObject = null;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        for (;;)
        {
          localObject = localIMiuiTelephony.getDeviceId(Holder.CONTEXT.getOpPackageName());
        }
      }
      return (String)localObject;
    }
    catch (Exception localException)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getDeviceId");
      ((StringBuilder)localObject).append(localException);
      Rlog.e("TelephonyManager", ((StringBuilder)localObject).toString());
    }
    return null;
  }
  
  public int getMiuiLevel(SignalStrength paramSignalStrength)
  {
    return paramSignalStrength.getMiuiLevel();
  }
  
  public IMiuiTelephony getMiuiTelephony()
  {
    try
    {
      if (sRegistry == null) {
        try
        {
          if (sRegistry == null) {
            sRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));
          }
        }
        finally {}
      }
      if (sRegistry != null)
      {
        IMiuiTelephony localIMiuiTelephony = sRegistry.getMiuiTelephony();
        return localIMiuiTelephony;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Rlog.e("TelephonyManager", "getMiuiTelephony error", localRemoteException);
    }
    return null;
  }
  
  public String getMsisdn()
  {
    return getMsisdnForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getMsisdnForSlot(int paramInt)
  {
    return getMsisdnForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getMsisdnForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getMsisdn(normalizeSubscriptionId(paramInt));
  }
  
  public List<NeighboringCellInfo> getNeighboringCellInfo()
  {
    return Holder.TELEPHONY_MANAGER.getNeighboringCellInfo();
  }
  
  public List<NeighboringCellInfo> getNeighboringCellInfoForSlot(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getNeighboringCellInfo();
  }
  
  public List<NeighboringCellInfo> getNeighboringCellInfoForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getNeighboringCellInfo();
  }
  
  public int getNetworkClass(int paramInt)
  {
    return android.telephony.TelephonyManager.getNetworkClass(paramInt);
  }
  
  public String getNetworkCountryIso()
  {
    return getNetworkCountryIsoForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getNetworkCountryIsoForSlot(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getNetworkCountryIsoForPhone(normalizeSlotId(paramInt));
  }
  
  public String getNetworkCountryIsoForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getNetworkCountryIso(normalizeSubscriptionId(paramInt));
  }
  
  public String getNetworkOperator()
  {
    return getNetworkOperatorForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getNetworkOperatorForSlot(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getNetworkOperatorForPhone(normalizeSlotId(paramInt));
  }
  
  public String getNetworkOperatorForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getNetworkOperator(normalizeSubscriptionId(paramInt));
  }
  
  public String getNetworkOperatorName()
  {
    return getNetworkOperatorNameForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getNetworkOperatorNameForSlot(int paramInt)
  {
    return getNetworkOperatorNameForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getNetworkOperatorNameForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getNetworkOperatorName(normalizeSubscriptionId(paramInt));
  }
  
  public int getNetworkType()
  {
    return getNetworkTypeForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public int getNetworkTypeForSlot(int paramInt)
  {
    return getNetworkTypeForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public int getNetworkTypeForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getNetworkType(normalizeSubscriptionId(paramInt));
  }
  
  public String getNetworkTypeName(int paramInt)
  {
    return android.telephony.TelephonyManager.getNetworkTypeName(paramInt);
  }
  
  public int getPhoneCount()
  {
    return Holder.PHONE_COUNT;
  }
  
  public int getPhoneType()
  {
    return getPhoneTypeForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public int getPhoneTypeForSlot(int paramInt)
  {
    return getPhoneTypeForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public int getPhoneTypeForSubscription(int paramInt)
  {
    if (!isVoiceCapable()) {
      return 0;
    }
    return Holder.TELEPHONY_MANAGER.getCurrentPhoneType(normalizeSubscriptionId(paramInt));
  }
  
  public String getSimCountryIso()
  {
    return getSimCountryIsoForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getSimCountryIsoForSlot(int paramInt)
  {
    return getSimCountryIsoForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getSimCountryIsoForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getSimCountryIso(normalizeSubscriptionId(paramInt));
  }
  
  public String getSimOperator()
  {
    return getSimOperatorForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getSimOperatorForSlot(int paramInt)
  {
    return getSimOperatorForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getSimOperatorForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getSimOperator(normalizeSubscriptionId(paramInt));
  }
  
  public String getSimOperatorName()
  {
    return getSimOperatorNameForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getSimOperatorNameForSlot(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getSimOperatorNameForPhone(normalizeSlotId(paramInt));
  }
  
  public String getSimOperatorNameForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getSimOperatorName(normalizeSubscriptionId(paramInt));
  }
  
  public String getSimSerialNumber()
  {
    return getSimSerialNumberForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getSimSerialNumberForSlot(int paramInt)
  {
    return getSimSerialNumberForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getSimSerialNumberForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getSimSerialNumber(normalizeSubscriptionId(paramInt));
  }
  
  public int getSimState()
  {
    return getSimStateForSlot(Holder.SUBSCRIPTION_MANAGER.getDefaultSlotId());
  }
  
  public int getSimStateForSlot(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getSimState(normalizeSlotId(paramInt));
  }
  
  public int getSimStateForSubscription(int paramInt)
  {
    return getSimStateForSlot(Holder.SUBSCRIPTION_MANAGER.getSlotIdForSubscription(paramInt));
  }
  
  public String getSmallDeviceId()
  {
    String str = null;
    try
    {
      localObject = getMiuiTelephony();
      if (localObject != null) {
        for (;;)
        {
          str = ((IMiuiTelephony)localObject).getSmallDeviceId(Holder.CONTEXT.getOpPackageName());
        }
      }
      return str;
    }
    catch (Exception localException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getSmallDeviceId");
      ((StringBuilder)localObject).append(localException);
      Rlog.e("TelephonyManager", ((StringBuilder)localObject).toString());
    }
    return null;
  }
  
  public String getSpn(String paramString1, int paramInt, String paramString2, boolean paramBoolean)
  {
    String str = null;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony == null) {
        paramString1 = null;
      } else {
        paramString1 = localIMiuiTelephony.getSpn(paramString1, paramInt, paramString2, paramBoolean);
      }
    }
    catch (RemoteException paramString1)
    {
      Rlog.e("TelephonyManager", "getSpn error", paramString1);
      paramString1 = str;
    }
    str = paramString1;
    if (paramString1 == null)
    {
      if (((paramString2 == null) || (paramString2.length() == 0)) && (SubscriptionManager.isValidSlotId(paramInt))) {
        paramString1 = getSimOperatorNameForSlot(paramInt);
      } else {
        paramString1 = paramString2;
      }
      str = paramString1;
    }
    return str;
  }
  
  public String getSubscriberId()
  {
    return getSubscriberIdForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getSubscriberIdForSlot(int paramInt)
  {
    return getSubscriberIdForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getSubscriberIdForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getSubscriberId(normalizeSubscriptionId(paramInt));
  }
  
  public String getTelephonyProperty(int paramInt, String paramString1, String paramString2)
  {
    return android.telephony.TelephonyManager.getTelephonyProperty(normalizeSlotId(paramInt), paramString1, paramString2);
  }
  
  public String getTelephonySetting(int paramInt, ContentResolver paramContentResolver, String paramString)
  {
    paramInt = normalizeSlotId(paramInt);
    paramContentResolver = Settings.Global.getString(paramContentResolver, paramString);
    if (paramContentResolver != null)
    {
      paramContentResolver = paramContentResolver.split(",");
      if ((paramInt >= 0) && (paramInt < paramContentResolver.length) && (paramContentResolver[paramInt] != null)) {
        return paramContentResolver[paramInt];
      }
    }
    return "";
  }
  
  public String getVoiceMailAlphaTag()
  {
    return getVoiceMailAlphaTagForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getVoiceMailAlphaTagForSlot(int paramInt)
  {
    return getVoiceMailAlphaTagForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getVoiceMailAlphaTagForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getVoiceMailAlphaTag(normalizeSubscriptionId(paramInt));
  }
  
  public String getVoiceMailNumber()
  {
    return getVoiceMailNumberForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public String getVoiceMailNumberForSlot(int paramInt)
  {
    return getVoiceMailNumberForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public String getVoiceMailNumberForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getVoiceMailNumber(normalizeSubscriptionId(paramInt));
  }
  
  public int getVoiceNetworkType()
  {
    return getVoiceNetworkTypeForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public int getVoiceNetworkTypeForSlot(int paramInt)
  {
    return getVoiceNetworkTypeForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public int getVoiceNetworkTypeForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.getVoiceNetworkType(normalizeSubscriptionId(paramInt));
  }
  
  public boolean hasIccCard()
  {
    int i = getPhoneCount();
    for (int j = 0; j < i; j++) {
      if (hasIccCard(j)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean hasIccCard(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.hasIccCard(normalizeSlotId(paramInt));
  }
  
  public boolean isCmccCooperationDevice()
  {
    return Holder.IS_CMCC_COOPERATION_DEVICE;
  }
  
  public boolean isDualVolteSupported()
  {
    boolean bool;
    if ((Holder.IS_DUAL_VOLTE_SUPPORTED) && (!isCustSingleSimDevice())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isFiveGCapable()
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isFiveGCapable();
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isFiveGCapable exception", localException);
    }
    return false;
  }
  
  public boolean isGwsdSupport()
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isGwsdSupport();
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isGwsdSupport exception", localException);
    }
    return false;
  }
  
  public boolean isImsRegistered(int paramInt)
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isImsRegistered(paramInt);
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isImsRegistered exception", localException);
    }
    return false;
  }
  
  public boolean isMultiSimEnabled()
  {
    int i = getPhoneCount();
    boolean bool = true;
    if (i <= 1) {
      bool = false;
    }
    return bool;
  }
  
  public boolean isNetworkRoaming()
  {
    return isNetworkRoamingForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId());
  }
  
  public boolean isNetworkRoamingForSlot(int paramInt)
  {
    return isNetworkRoamingForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public boolean isNetworkRoamingForSubscription(int paramInt)
  {
    return Holder.TELEPHONY_MANAGER.isNetworkRoaming(normalizeSubscriptionId(paramInt));
  }
  
  public boolean isRadioOn()
  {
    int i = getPhoneCount();
    for (int j = 0; j < i; j++) {
      if (isRadioOnForSlot(j)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isRadioOnForSlot(int paramInt)
  {
    return isRadioOnForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt));
  }
  
  public boolean isRadioOnForSubscription(int paramInt)
  {
    try
    {
      boolean bool = getITelephony().isRadioOnForSubscriber(normalizeSubscriptionId(paramInt), Holder.CONTEXT.getOpPackageName());
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Rlog.e("TelephonyManager", "Error calling ITelephony#supplyPukReportResultForSubscriber", localRemoteException);
    }
    return false;
  }
  
  public boolean isSameOperator(String paramString1, String paramString2)
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isSameOperator(paramString1, paramString2);
      }
      return bool;
    }
    catch (RemoteException paramString1)
    {
      Rlog.e("TelephonyManager", "isSameOperator error", paramString1);
    }
    return false;
  }
  
  public boolean isSmsCapable()
  {
    return Holder.TELEPHONY_MANAGER.isSmsCapable();
  }
  
  public boolean isUserFiveGEnabled()
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isUserFiveGEnabled();
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isUserFiveGEnabled exception", localException);
    }
    return false;
  }
  
  public boolean isVideoTelephonyAvailable(int paramInt)
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isVideoTelephonyAvailable(paramInt);
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isVideoTelephonyAvailable exception", localException);
    }
    return false;
  }
  
  public boolean isVoiceCapable()
  {
    return Holder.TELEPHONY_MANAGER.isVoiceCapable();
  }
  
  public boolean isVolteEnabledByPlatform()
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isVolteEnabledByPlatform();
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isVolteEnabledByPlatform exception", localException);
    }
    return false;
  }
  
  public boolean isVolteEnabledByPlatform(int paramInt)
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isVolteEnabledByPlatformForSlot(paramInt);
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isVolteEnabledByPlatform exception", localException);
    }
    return false;
  }
  
  public boolean isVolteEnabledByUser()
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isVolteEnabledByUser();
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isVolteEnabledByUser exception", localException);
    }
    return false;
  }
  
  public boolean isVolteEnabledByUser(int paramInt)
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isVolteEnabledByUserForSlot(paramInt);
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isVolteEnabledByUser exception", localException);
    }
    return false;
  }
  
  public boolean isVtEnabledByPlatform()
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isVtEnabledByPlatform();
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isVtEnabledByPlatform exception", localException);
    }
    return false;
  }
  
  public boolean isVtEnabledByPlatform(int paramInt)
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isVtEnabledByPlatformForSlot(paramInt);
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isVtEnabledByPlatform exception", localException);
    }
    return false;
  }
  
  public boolean isWifiCallingAvailable(int paramInt)
  {
    boolean bool = false;
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        bool = localIMiuiTelephony.isWifiCallingAvailable(paramInt);
      }
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isWifiCallingAvailable exception", localException);
    }
    return false;
  }
  
  public void listen(PhoneStateListener paramPhoneStateListener, int paramInt)
  {
    Holder.TELEPHONY_MANAGER.listen(paramPhoneStateListener, paramInt);
  }
  
  public void listenForSlot(int paramInt1, PhoneStateListener paramPhoneStateListener, int paramInt2)
  {
    int i = Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt1);
    int j = i;
    if (Build.IS_CM_CUSTOMIZATION_TEST)
    {
      j = i;
      if (!SubscriptionManager.isValidSubscriptionId(i)) {
        j = -2 - paramInt1;
      }
    }
    listenForSubscription(j, paramPhoneStateListener, paramInt2);
  }
  
  public void listenForSubscription(int paramInt1, PhoneStateListener paramPhoneStateListener, int paramInt2)
  {
    Integer localInteger = paramPhoneStateListener.updateSubscription(Integer.valueOf(paramInt1));
    Holder.TELEPHONY_MANAGER.createForSubscriptionId(paramInt1).listen(paramPhoneStateListener, paramInt2);
    paramPhoneStateListener.updateSubscription(localInteger);
  }
  
  public String onOperatorNumericOrNameSet(int paramInt, String paramString1, String paramString2)
  {
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony == null) {
        paramString1 = paramString2;
      } else {
        paramString1 = localIMiuiTelephony.onOperatorNumericOrNameSet(paramInt, paramString1, paramString2);
      }
      return paramString1;
    }
    catch (RemoteException paramString1)
    {
      Rlog.e("TelephonyManager", "onOperatorNumericOrNameSet error", paramString1);
    }
    return paramString2;
  }
  
  public boolean putTelephonySetting(int paramInt, ContentResolver paramContentResolver, String paramString1, String paramString2)
  {
    int i = normalizeSlotId(paramInt);
    StringBuilder localStringBuilder = new StringBuilder(128);
    String[] arrayOfString = null;
    Object localObject = Settings.Global.getString(paramContentResolver, paramString1);
    if (localObject != null) {
      arrayOfString = ((String)localObject).split(",");
    }
    for (paramInt = 0; paramInt < i; paramInt++)
    {
      String str = "";
      localObject = str;
      if (arrayOfString != null)
      {
        localObject = str;
        if (paramInt < arrayOfString.length) {
          localObject = arrayOfString[paramInt];
        }
      }
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(',');
    }
    if (paramString2 == null) {
      paramString2 = "";
    }
    localStringBuilder.append(paramString2);
    if (arrayOfString != null) {
      for (paramInt = i + 1; paramInt < arrayOfString.length; paramInt++)
      {
        localStringBuilder.append(',');
        localStringBuilder.append(arrayOfString[paramInt]);
      }
    }
    return Settings.Global.putString(paramContentResolver, paramString1, localStringBuilder.toString());
  }
  
  public void setCallForwardingOption(int paramInt1, int paramInt2, int paramInt3, String paramString, ResultReceiver paramResultReceiver)
  {
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        localIMiuiTelephony.setCallForwardingOption(paramInt1, paramInt2, paramInt3, paramString, paramResultReceiver);
      }
    }
    catch (Exception paramString)
    {
      Rlog.e("TelephonyManager", "setCallForwardingOption exception", paramString);
      if (paramResultReceiver != null) {
        paramResultReceiver.send(-1, null);
      }
    }
  }
  
  public void setIccCardActivate(int paramInt, boolean paramBoolean)
  {
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        localIMiuiTelephony.setIccCardActivate(paramInt, paramBoolean);
      }
    }
    catch (RemoteException localRemoteException)
    {
      Rlog.e("TelephonyManager", "setIccCardActivate error", localRemoteException);
    }
  }
  
  public void setTelephonyProperty(int paramInt, String paramString1, String paramString2)
  {
    android.telephony.TelephonyManager.setTelephonyProperty(normalizeSlotId(paramInt), paramString1, paramString2);
  }
  
  public void setUserFiveGEnabled(boolean paramBoolean)
  {
    try
    {
      IMiuiTelephony localIMiuiTelephony = getMiuiTelephony();
      if (localIMiuiTelephony != null) {
        localIMiuiTelephony.setUserFiveGEnabled(paramBoolean);
      }
    }
    catch (Exception localException)
    {
      Rlog.e("TelephonyManager", "isUserFiveGEnabled exception", localException);
    }
  }
  
  public boolean showCallScreen()
  {
    return showCallScreenWithDialpad(false);
  }
  
  public boolean showCallScreenWithDialpad(boolean paramBoolean)
  {
    try
    {
      getTelecomService().showInCallScreen(paramBoolean, Holder.CONTEXT.getOpPackageName());
      return true;
    }
    catch (RemoteException localRemoteException)
    {
      Rlog.e("TelephonyManager", "Error calling ITelecomService#showInCallScreen", localRemoteException);
    }
    return false;
  }
  
  public void silenceRinger()
  {
    try
    {
      getTelecomService().silenceRinger(Holder.CONTEXT.getOpPackageName());
    }
    catch (RemoteException localRemoteException)
    {
      Rlog.e("TelephonyManager", "Error call ITelecomService#silenceRinger", localRemoteException);
    }
  }
  
  public int[] supplyPinReportResult(String paramString)
  {
    return supplyPinReportResultForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId(), paramString);
  }
  
  public int[] supplyPinReportResultForSlot(int paramInt, String paramString)
  {
    return supplyPinReportResultForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt), paramString);
  }
  
  public int[] supplyPinReportResultForSubscription(int paramInt, String paramString)
  {
    try
    {
      paramString = getITelephony().supplyPinReportResultForSubscriber(normalizeSubscriptionId(paramInt), paramString);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      Rlog.e("TelephonyManager", "Error calling ITelephony#supplyPinReportResultForSubscriber", paramString);
    }
    return new int[0];
  }
  
  public int[] supplyPukReportResult(String paramString1, String paramString2)
  {
    return supplyPukReportResultForSubscription(Holder.SUBSCRIPTION_MANAGER.getDefaultSubscriptionId(), paramString1, paramString2);
  }
  
  public int[] supplyPukReportResultForSlot(int paramInt, String paramString1, String paramString2)
  {
    return supplyPukReportResultForSubscription(Holder.SUBSCRIPTION_MANAGER.getSubscriptionIdForSlot(paramInt), paramString1, paramString2);
  }
  
  public int[] supplyPukReportResultForSubscription(int paramInt, String paramString1, String paramString2)
  {
    try
    {
      paramString1 = getITelephony().supplyPukReportResultForSubscriber(normalizeSubscriptionId(paramInt), paramString1, paramString2);
      return paramString1;
    }
    catch (RemoteException paramString1)
    {
      Rlog.e("TelephonyManager", "Error calling ITelephony#supplyPukReportResultForSubscriber", paramString1);
    }
    return new int[0];
  }
  
  static class Holder
  {
    static final Context CONTEXT;
    static final int CT_VOLTE_SUPPORTED_MODE = CONTEXT.getResources().getInteger(285868059);
    static final TelephonyManagerEx INSTANCE = new TelephonyManagerEx(null);
    static final boolean IS_CMCC_COOPERATION_DEVICE = CONTEXT.getResources().getBoolean(285474849);
    static final boolean IS_DUAL_VOLTE_SUPPORTED;
    static final int PHONE_COUNT;
    static final SubscriptionManager SUBSCRIPTION_MANAGER = SubscriptionManager.getDefault();
    static final android.telephony.TelephonyManager TELEPHONY_MANAGER;
    
    static
    {
      CONTEXT = AppConstants.getCurrentApplication();
      TELEPHONY_MANAGER = (android.telephony.TelephonyManager)CONTEXT.getSystemService("phone");
      PHONE_COUNT = TELEPHONY_MANAGER.getPhoneCount();
      IS_DUAL_VOLTE_SUPPORTED = CONTEXT.getResources().getBoolean(285474842);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/TelephonyManagerEx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */