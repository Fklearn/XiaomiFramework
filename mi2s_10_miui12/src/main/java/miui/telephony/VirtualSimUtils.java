package miui.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MiuiSettings.VirtualSim;
import android.provider.Settings.Global;
import android.telephony.Rlog;
import android.text.TextUtils;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccController;
import java.util.ArrayList;
import java.util.Iterator;
import miui.provider.ExtraSettings.Secure;

public class VirtualSimUtils
{
  public static final String CLOUD_SIM_IMSI_KEY = "cloudsim_sim_imsi";
  public static final String KEY_CARRIER_NAME = "carrierName";
  public static final String KEY_SETTINGS_ENTRANCE = "setting_entrance";
  public static final String METHOD_GET_CARRIER_NAME = "getCarrierName";
  public static final String METHOD_GET_SETTINGS_ENTRANCE_INTENT = "getSettingsEntranceIntent";
  private static final String TAG = "VirtualSimUtils";
  public static final String VIRTUAL_SIM_CONTENT = "content://com.miui.virtualsim.provider.virtualsimInfo";
  private static String sCloudSimImsi;
  private static VirtualSimUtils sInstance;
  private static boolean sIsCloudSimEnabled;
  private boolean mIsVirtualSimEnabled = MiuiSettings.VirtualSim.isVirtualSimEnabled(MiuiTelephony.sContext);
  private ArrayList<VirtualSimListenner> mListeners = new ArrayList(2);
  private int mVirtualSimStatus = MiuiSettings.VirtualSim.getVirtualSimStatus(MiuiTelephony.sContext);
  
  private VirtualSimUtils()
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("init mIsVirtualSimEnabled=");
    ((StringBuilder)localObject).append(this.mIsVirtualSimEnabled);
    ((StringBuilder)localObject).append(" mVirtualSimStatus=");
    ((StringBuilder)localObject).append(this.mVirtualSimStatus);
    Rlog.d("VirtualSimUtils", ((StringBuilder)localObject).toString());
    localObject = new ContentObserver(null)
    {
      public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
      {
        VirtualSimUtils.access$002(VirtualSimUtils.this, MiuiSettings.VirtualSim.isVirtualSimEnabled(MiuiTelephony.sContext));
        VirtualSimUtils.access$102(VirtualSimUtils.this, MiuiSettings.VirtualSim.getVirtualSimStatus(MiuiTelephony.sContext));
        if (paramAnonymousUri.toString().contains("virtual_sim_imsi"))
        {
          paramAnonymousUri = new StringBuilder();
          paramAnonymousUri.append("onVirtualSimStateChanged mIsVirtualSimEnabled=");
          paramAnonymousUri.append(VirtualSimUtils.this.mIsVirtualSimEnabled);
          Rlog.d("VirtualSimUtils", paramAnonymousUri.toString());
          paramAnonymousUri = VirtualSimUtils.this.mListeners.iterator();
          while (paramAnonymousUri.hasNext()) {
            ((VirtualSimUtils.VirtualSimListenner)paramAnonymousUri.next()).onVirtualSimStateChanged();
          }
        }
        else if (paramAnonymousUri.toString().contains("virtual_sim_status"))
        {
          paramAnonymousUri = new StringBuilder();
          paramAnonymousUri.append("onVirtualSimPreciseStateChanged mVirtualSimStatus=");
          paramAnonymousUri.append(VirtualSimUtils.this.mVirtualSimStatus);
          Rlog.d("VirtualSimUtils", paramAnonymousUri.toString());
          paramAnonymousUri = VirtualSimUtils.this.mListeners.iterator();
          while (paramAnonymousUri.hasNext()) {
            ((VirtualSimUtils.VirtualSimListenner)paramAnonymousUri.next()).onVirtualSimPreciseStateChanged();
          }
        }
      }
    };
    MiuiTelephony.sContext.getContentResolver().registerContentObserver(ExtraSettings.Secure.getUriFor("virtual_sim_imsi"), false, (ContentObserver)localObject);
    MiuiTelephony.sContext.getContentResolver().registerContentObserver(ExtraSettings.Secure.getUriFor("virtual_sim_status"), false, (ContentObserver)localObject);
    sCloudSimImsi = Settings.Global.getString(MiuiTelephony.sContext.getContentResolver(), "cloudsim_sim_imsi");
    sIsCloudSimEnabled = TextUtils.isEmpty(sCloudSimImsi) ^ true;
    localObject = new ContentObserver(null)
    {
      public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
      {
        VirtualSimUtils.access$302(Settings.Global.getString(MiuiTelephony.sContext.getContentResolver(), "cloudsim_sim_imsi"));
        VirtualSimUtils.access$402(TextUtils.isEmpty(VirtualSimUtils.sCloudSimImsi) ^ true);
        paramAnonymousUri = new StringBuilder();
        paramAnonymousUri.append("mIsCloudSimEnabled");
        paramAnonymousUri.append(VirtualSimUtils.sIsCloudSimEnabled);
        Rlog.d("VirtualSimUtils", paramAnonymousUri.toString());
      }
    };
    MiuiTelephony.sContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("cloudsim_sim_imsi"), false, (ContentObserver)localObject);
  }
  
  public static String getCloudSimImsi()
  {
    String str = sCloudSimImsi;
    if (str == null) {
      str = "";
    }
    return str;
  }
  
  public static VirtualSimUtils getInstance()
  {
    return sInstance;
  }
  
  public static String getVirtualSimCarrierName(Context paramContext)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    try
    {
      paramContext = paramContext.getContentResolver().call(Uri.parse("content://com.miui.virtualsim.provider.virtualsimInfo"), "getCarrierName", null, null);
    }
    catch (Exception localException)
    {
      paramContext = new StringBuilder();
      paramContext.append("getVirtualSimCarrierName e");
      paramContext.append(localException);
      Rlog.d("VirtualSimUtils", paramContext.toString());
      paramContext = (Context)localObject1;
    }
    if (paramContext == null) {
      paramContext = (Context)localObject2;
    } else {
      paramContext = paramContext.getString("carrierName");
    }
    return paramContext;
  }
  
  public static Intent getVirtualSimIntent(Context paramContext)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    try
    {
      paramContext = paramContext.getContentResolver().call(Uri.parse("content://com.miui.virtualsim.provider.virtualsimInfo"), "getSettingsEntranceIntent", null, null);
    }
    catch (Exception paramContext)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("getVirtualSimIntent ");
      localStringBuilder.append(paramContext);
      Rlog.e("VirtualSimUtils", localStringBuilder.toString());
      paramContext = (Context)localObject1;
    }
    if (paramContext == null) {
      paramContext = (Context)localObject2;
    } else {
      paramContext = (Intent)paramContext.getParcelable("setting_entrance");
    }
    return paramContext;
  }
  
  public static int getVirtualSimSlot(Context paramContext, boolean paramBoolean, int paramInt)
  {
    if (MiuiSettings.VirtualSim.isVirtualSimEnabled(paramContext))
    {
      if ((paramBoolean) && (paramInt != MiuiSettings.VirtualSim.getVirtualSimType(paramContext))) {
        return SubscriptionManager.INVALID_SLOT_ID;
      }
      String str = MiuiSettings.VirtualSim.getVirtualSimIccId(paramContext);
      if ((str != null) && (!str.isEmpty()))
      {
        paramInt = MiuiSettings.VirtualSim.getVirtualSimSlotId(paramContext);
        paramContext = UiccController.getInstance().getUiccCard(paramInt);
        if ((paramContext != null) && (str.equalsIgnoreCase(paramContext.getIccId()))) {
          return paramInt;
        }
      }
      else
      {
        return SubscriptionManager.INVALID_SLOT_ID;
      }
    }
    return SubscriptionManager.INVALID_SLOT_ID;
  }
  
  static void init()
  {
    if (sInstance == null) {
      sInstance = new VirtualSimUtils();
    }
  }
  
  public static boolean isCloudSimEnabled()
  {
    return sIsCloudSimEnabled;
  }
  
  public static boolean isDcOnlyVirtualSim(Context paramContext)
  {
    boolean bool1 = MiuiSettings.VirtualSim.isVirtualSimEnabled(paramContext);
    boolean bool2 = true;
    if ((!bool1) || (MiuiSettings.VirtualSim.getVirtualSimType(paramContext) != 1)) {
      bool2 = false;
    }
    return bool2;
  }
  
  public static boolean isMiSimEnabled(Context paramContext, int paramInt)
  {
    boolean bool1 = MiuiSettings.VirtualSim.isMiSimEnabled(paramContext);
    boolean bool2 = true;
    if ((!bool1) || (paramInt != getVirtualSimSlot(paramContext, false, 1))) {
      bool2 = false;
    }
    return bool2;
  }
  
  public static boolean isValidApnForMiSim(Context paramContext, int paramInt, String paramString)
  {
    if ((isMiSimEnabled(paramContext, paramInt)) && (ServiceProviderUtils.isChinaUnicom(MiuiSettings.VirtualSim.getVirtualSimImsi(paramContext)))) {
      return "3gnet".equals(paramString);
    }
    return true;
  }
  
  public static boolean isVirtualSim(Context paramContext, int paramInt)
  {
    boolean bool;
    if ((MiuiSettings.VirtualSim.isVirtualSimEnabled(paramContext)) && (paramInt == MiuiSettings.VirtualSim.getVirtualSimSlotId(paramContext))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void addVirtualSimChangedListener(VirtualSimListenner paramVirtualSimListenner)
  {
    synchronized (this.mListeners)
    {
      this.mListeners.add(paramVirtualSimListenner);
      return;
    }
  }
  
  public int getVirtualSimSlotId()
  {
    if (this.mIsVirtualSimEnabled) {
      return MiuiSettings.VirtualSim.getVirtualSimSlotId(MiuiTelephony.sContext);
    }
    return SubscriptionManager.INVALID_SLOT_ID;
  }
  
  public boolean isDisablingVirtualSim()
  {
    int i = this.mVirtualSimStatus;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    return bool;
  }
  
  public boolean isEnablingVirtualSim()
  {
    boolean bool;
    if (this.mVirtualSimStatus == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isVirtualSimEnabled()
  {
    return this.mIsVirtualSimEnabled;
  }
  
  public void removeVirtualSimChangedListener(VirtualSimListenner paramVirtualSimListenner)
  {
    synchronized (this.mListeners)
    {
      this.mListeners.remove(paramVirtualSimListenner);
      return;
    }
  }
  
  public static abstract interface VirtualSimListenner
  {
    public abstract void onVirtualSimPreciseStateChanged();
    
    public abstract void onVirtualSimStateChanged();
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/VirtualSimUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */