package miui.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.telephony.Rlog;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.util.AppConstants;

public class SubscriptionManagerEx
  extends SubscriptionManager
{
  public static final String ACTION_DEFAULT_DATA_SLOT_CHANGED = "miui.intent.action.ACTION_DEFAULT_DATA_SLOT_CHANGED";
  public static final String ACTION_DEFAULT_DATA_SLOT_READY = "miui.intent.action.ACTION_DEFAULT_DATA_SLOT_READY";
  private static final String ACTION_UICC_MANUAL_PROVISION_STATUS_CHANGED = "org.codeaurora.intent.action.ACTION_UICC_MANUAL_PROVISION_STATUS_CHANGED";
  static final String DEFAULT_DATA_SLOT_PROPERTY = "persist.radio.default.data";
  static final String DEFAULT_VOICE_SLOT_PROPERTY = "persist.radio.default.voice";
  public static final String KEY_OLD_DATA_SLOT = "old_data_slot";
  public static final String KEY_SIM_INSERT_STATE_ARRAY = "sim_insert_state_array";
  public static final int SIM_CHANGED = 4;
  public static final int SIM_NEW_CARD = 2;
  public static final int SIM_NO_CARD = 1;
  public static final int SIM_NO_CHANGE = 0;
  public static final int SIM_REMOVED = 3;
  private static final Comparator<android.telephony.SubscriptionInfo> SUBSCRIPTION_INFO_COMPARATOR = _..Lambda.SubscriptionManagerEx.nWnxymqBl7xu3TtQYhcHLhZZdf0.INSTANCE;
  private AtomicBoolean mReceiverRegistered = new AtomicBoolean(false);
  private BroadcastReceiver mSubscriptionChangedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("org.codeaurora.intent.action.ACTION_UICC_MANUAL_PROVISION_STATUS_CHANGED".equals(paramAnonymousIntent.getAction())) {
        SubscriptionManagerEx.this.onSubscriptionInfoChanged();
      }
    }
  };
  private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener;
  
  public static SubscriptionManagerEx getDefault()
  {
    return Holder.INSTANCE;
  }
  
  private IMiuiTelephony getMiuiTelephony()
  {
    return TelephonyManagerEx.getDefault().getMiuiTelephony();
  }
  
  private void initSubscriptionListener(boolean paramBoolean)
  {
    if (this.mSubscriptionListener != null) {
      return;
    }
    if ((paramBoolean) && (Looper.myLooper() != Looper.getMainLooper()))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("initSubscriptionListener failed for pkg=");
      localStringBuilder.append(Holder.CONTEXT.getOpPackageName());
      localStringBuilder.append(" threadName=");
      localStringBuilder.append(Thread.currentThread().getName());
      Rlog.i("SubMgr", localStringBuilder.toString());
      return;
    }
    this.mSubscriptionListener = new SubscriptionManager.OnSubscriptionsChangedListener()
    {
      public void onSubscriptionsChanged()
      {
        SubscriptionManagerEx.this.onSubscriptionInfoChanged();
      }
    };
    android.telephony.SubscriptionManager.from(Holder.CONTEXT).addOnSubscriptionsChangedListener(this.mSubscriptionListener);
  }
  
  protected void addOnSubscriptionsChangedListenerInternal()
  {
    if (this.mReceiverRegistered.compareAndSet(false, true))
    {
      initSubscriptionListener(true);
      if (this.mSubscriptionListener == null) {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
          public void run()
          {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("initSubscriptionListener in main Thread for pkg=");
            localStringBuilder.append(SubscriptionManagerEx.Holder.CONTEXT.getOpPackageName());
            Rlog.i("SubMgr", localStringBuilder.toString());
            SubscriptionManagerEx.this.initSubscriptionListener(false);
          }
        });
      }
      if ("qcom".equals(Build.HARDWARE))
      {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("org.codeaurora.intent.action.ACTION_UICC_MANUAL_PROVISION_STATUS_CHANGED");
        Holder.CONTEXT.registerReceiver(this.mSubscriptionChangedReceiver, localIntentFilter);
      }
    }
  }
  
  protected List<SubscriptionInfo> getAllSubscriptionInfoListInternal()
  {
    return SubscriptionInfoImpl.from(Holder.SUBSCRIPTION_MANAGER.getAllSubscriptionInfoList());
  }
  
  public int getDefaultDataSlotId()
  {
    int i = SystemProperties.getInt("persist.radio.default.data", INVALID_SLOT_ID);
    if (i == INVALID_SLOT_ID) {
      i = getDefaultSlotIdInternal();
    }
    return i;
  }
  
  public int getDefaultDataSubscriptionId()
  {
    int i = android.telephony.SubscriptionManager.getDefaultDataSubscriptionId();
    int j = i;
    if (i == INVALID_SUBSCRIPTION_ID) {
      j = getSubscriptionIdForSlot(getDefaultSlotIdInternal());
    }
    return j;
  }
  
  public SubscriptionInfo getDefaultDataSubscriptionInfo()
  {
    return SubscriptionInfoImpl.from(Holder.SUBSCRIPTION_MANAGER.getDefaultDataSubscriptionInfo());
  }
  
  protected int getDefaultSlotIdInternal()
  {
    try
    {
      int i = getMiuiTelephony().getSystemDefaultSlotId();
      return i;
    }
    catch (Exception localException) {}
    return 0;
  }
  
  public int getDefaultSmsSubscriptionId()
  {
    int i = android.telephony.SubscriptionManager.getDefaultSmsSubscriptionId();
    if (!isValidSubscriptionId(i)) {
      i = INVALID_SUBSCRIPTION_ID;
    }
    return i;
  }
  
  public SubscriptionInfo getDefaultSmsSubscriptionInfo()
  {
    return SubscriptionInfoImpl.from(Holder.SUBSCRIPTION_MANAGER.getDefaultSmsSubscriptionInfo());
  }
  
  public int getDefaultVoiceSlotId()
  {
    return SystemProperties.getInt("persist.radio.default.voice", INVALID_SLOT_ID);
  }
  
  public int getDefaultVoiceSubscriptionId()
  {
    int i = android.telephony.SubscriptionManager.getDefaultVoiceSubscriptionId();
    if (!isValidSubscriptionId(i)) {
      i = INVALID_SUBSCRIPTION_ID;
    }
    return i;
  }
  
  public SubscriptionInfo getDefaultVoiceSubscriptionInfo()
  {
    return SubscriptionInfoImpl.from(Holder.SUBSCRIPTION_MANAGER.getDefaultVoiceSubscriptionInfo());
  }
  
  protected int getSlotId(int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      Iterator localIterator = getSubscriptionInfoList().iterator();
      while (localIterator.hasNext())
      {
        SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)localIterator.next();
        if (localSubscriptionInfo.getSubscriptionId() == paramInt)
        {
          paramInt = localSubscriptionInfo.getSlotId();
          return paramInt;
        }
      }
      return android.telephony.SubscriptionManager.getPhoneId(paramInt);
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public int getSubscriptionIdForSlot(int paramInt)
  {
    if (!isValidSlotId(paramInt)) {
      return INVALID_SUBSCRIPTION_ID;
    }
    if (paramInt == DEFAULT_SLOT_ID) {
      return DEFAULT_SUBSCRIPTION_ID;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      Iterator localIterator = getSubscriptionInfoList().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (SubscriptionInfo)localIterator.next();
        if (((SubscriptionInfo)localObject1).getSlotId() == paramInt)
        {
          paramInt = ((SubscriptionInfo)localObject1).getSubscriptionId();
          return paramInt;
        }
      }
      Binder.restoreCallingIdentity(l);
      Object localObject1 = android.telephony.SubscriptionManager.getSubId(paramInt);
      if ((localObject1 != null) && (localObject1.length > 0)) {
        paramInt = localObject1[0];
      } else {
        paramInt = INVALID_SUBSCRIPTION_ID;
      }
      return paramInt;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  protected List<SubscriptionInfo> getSubscriptionInfoListInternal()
  {
    List localList = Holder.SUBSCRIPTION_MANAGER.getActiveSubscriptionInfoList();
    if (localList != null) {
      localList.sort(SUBSCRIPTION_INFO_COMPARATOR);
    }
    return SubscriptionInfoImpl.from(localList);
  }
  
  protected void removeOnSubscriptionsChangedListenerInternal()
  {
    this.mReceiverRegistered.set(false);
    if (this.mSubscriptionListener != null)
    {
      android.telephony.SubscriptionManager.from(Holder.CONTEXT).removeOnSubscriptionsChangedListener(this.mSubscriptionListener);
      this.mSubscriptionListener = null;
    }
    if ((this.mSubscriptionChangedReceiver != null) && ("qcom".equals(Build.HARDWARE))) {
      try
      {
        Holder.CONTEXT.unregisterReceiver(this.mSubscriptionChangedReceiver);
      }
      catch (Exception localException)
      {
        Rlog.i("SubMgr", "unregister SubscriptionChangedReceiver error!!!");
      }
    }
  }
  
  public void setDefaultDataSlotId(int paramInt)
  {
    if ((isValidSlotId(paramInt)) && (paramInt != DEFAULT_SLOT_ID))
    {
      try
      {
        getMiuiTelephony().setDefaultDataSlotId(paramInt, Holder.CONTEXT.getOpPackageName());
      }
      catch (Exception localException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Failed to set default data slot id ");
        localStringBuilder.append(paramInt);
        localStringBuilder.append(" - ");
        Rlog.e("SubMgr", localStringBuilder.toString(), localException);
      }
      return;
    }
  }
  
  public void setDefaultSmsSubscriptionId(int paramInt)
  {
    if (!isValidSubscriptionId(paramInt)) {
      paramInt = INVALID_SUBSCRIPTION_ID;
    }
    if ((paramInt != DEFAULT_SUBSCRIPTION_ID) && (paramInt != getDefaultSmsSubscriptionId()))
    {
      Holder.SUBSCRIPTION_MANAGER.setDefaultSmsSubId(paramInt);
      return;
    }
  }
  
  public void setDefaultVoiceSlotId(int paramInt)
  {
    if (paramInt == DEFAULT_SLOT_ID) {
      return;
    }
    try
    {
      localObject = getMiuiTelephony();
      int i;
      if (isValidSlotId(paramInt)) {
        i = paramInt;
      } else {
        i = INVALID_SLOT_ID;
      }
      ((IMiuiTelephony)localObject).setDefaultVoiceSlotId(i, Holder.CONTEXT.getOpPackageName());
    }
    catch (Exception localException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Failed to set default voice slot id ");
      ((StringBuilder)localObject).append(paramInt);
      ((StringBuilder)localObject).append(" - ");
      Rlog.e("SubMgr", ((StringBuilder)localObject).toString(), localException);
    }
  }
  
  public int setDisplayNameForSlot(String paramString, int paramInt)
  {
    if (!isValidSlotId(paramInt)) {
      return 0;
    }
    if (paramInt == DEFAULT_SLOT_ID) {
      return setDisplayNameForSubscription(paramString, getSubscriptionIdForSlot(getDefaultSlotId()));
    }
    return setDisplayNameForSubscription(paramString, getSubscriptionIdForSlot(paramInt));
  }
  
  public int setDisplayNameForSubscription(String paramString, int paramInt)
  {
    if (!isValidSubscriptionId(paramInt)) {
      return 0;
    }
    if (paramInt == DEFAULT_SUBSCRIPTION_ID) {
      return setDisplayNameForSlot(paramString, getDefaultSlotId());
    }
    return Holder.SUBSCRIPTION_MANAGER.setDisplayName(paramString, paramInt, 2);
  }
  
  public int setDisplayNumberForSlot(String paramString, int paramInt)
  {
    if (!isValidSlotId(paramInt)) {
      return 0;
    }
    if (paramInt == DEFAULT_SLOT_ID) {
      return setDisplayNameForSubscription(paramString, getSubscriptionIdForSlot(getDefaultSlotId()));
    }
    return setDisplayNameForSubscription(paramString, getSubscriptionIdForSlot(paramInt));
  }
  
  public int setDisplayNumberForSubscription(String paramString, int paramInt)
  {
    if (!isValidSubscriptionId(paramInt)) {
      return 0;
    }
    if (paramInt == DEFAULT_SUBSCRIPTION_ID) {
      return setDisplayNumberForSlot(paramString, getDefaultSlotId());
    }
    return Holder.SUBSCRIPTION_MANAGER.setDisplayNumber(paramString, paramInt);
  }
  
  static class ConstantsDefiner
  {
    private static final String PHONE_ID = "phone_id";
    private static final String SLOT_ID = "slot_id";
    private static final String SUBSCRIPTION_ID = "subscription_id";
    
    static int getDefaultPhoneIdConstant()
    {
      return Integer.MAX_VALUE;
    }
    
    static int getDefaultSlotIdConstant()
    {
      return Integer.MAX_VALUE;
    }
    
    static int getDefaultSubscriptionIdConstant()
    {
      return Integer.MAX_VALUE;
    }
    
    static int getInvalidPhoneIdConstant()
    {
      return -1;
    }
    
    static int getInvalidSlotIdConstant()
    {
      return -1;
    }
    
    static int getInvalidSubscriptionIdConstant()
    {
      return -1;
    }
    
    static String getPhoneKeyConstant()
    {
      return "phone_id";
    }
    
    static String getSlotKeyConstant()
    {
      return "slot_id";
    }
    
    static String getSubscriptionKeyConstant()
    {
      return "subscription_id";
    }
  }
  
  static class Holder
  {
    static final Context CONTEXT = ;
    static final SubscriptionManagerEx INSTANCE = new SubscriptionManagerEx(null);
    static final android.telephony.SubscriptionManager SUBSCRIPTION_MANAGER = android.telephony.SubscriptionManager.from(CONTEXT);
  }
  
  static class SubscriptionInfoImpl
    extends SubscriptionInfo
  {
    int mSlotId;
    private final android.telephony.SubscriptionInfo mSubInfo;
    private final int mSubscriptionId;
    
    private SubscriptionInfoImpl(android.telephony.SubscriptionInfo paramSubscriptionInfo)
    {
      int i;
      if (SubscriptionManager.isValidSubscriptionId(paramSubscriptionInfo.getSubscriptionId())) {
        i = paramSubscriptionInfo.getSubscriptionId();
      } else {
        i = SubscriptionManager.INVALID_SUBSCRIPTION_ID;
      }
      this.mSubscriptionId = i;
      if (SubscriptionManager.isValidSlotId(paramSubscriptionInfo.getSimSlotIndex())) {
        i = paramSubscriptionInfo.getSimSlotIndex();
      } else {
        i = SubscriptionManager.INVALID_SLOT_ID;
      }
      this.mSlotId = i;
      this.mSubInfo = paramSubscriptionInfo;
    }
    
    public static List<SubscriptionInfo> from(List<android.telephony.SubscriptionInfo> paramList)
    {
      if (paramList == null) {
        return new ArrayList();
      }
      ArrayList localArrayList = new ArrayList();
      for (int i = 0; i < paramList.size(); i++) {
        localArrayList.add(i, from((android.telephony.SubscriptionInfo)paramList.get(i)));
      }
      return localArrayList;
    }
    
    public static SubscriptionInfo from(android.telephony.SubscriptionInfo paramSubscriptionInfo)
    {
      if (paramSubscriptionInfo == null) {
        paramSubscriptionInfo = null;
      } else {
        paramSubscriptionInfo = new SubscriptionInfoImpl(paramSubscriptionInfo);
      }
      return paramSubscriptionInfo;
    }
    
    private String getDefaultDisplayName()
    {
      Object localObject1 = "";
      Object localObject2 = localObject1;
      Object localObject3;
      if (this.mSlotId != SubscriptionManager.INVALID_SLOT_ID)
      {
        try
        {
          localObject2 = TelephonyManagerEx.getDefault().getMiuiTelephony().getSpn(TelephonyManager.getDefault().getSimOperatorForSlot(this.mSlotId), this.mSlotId, TelephonyManager.getDefault().getSimOperatorNameForSlot(this.mSlotId), true);
          localObject1 = localObject2;
        }
        catch (Exception localException) {}
        localObject3 = localObject1;
        if (TextUtils.isEmpty((CharSequence)localObject1)) {
          localObject3 = SubscriptionManagerEx.Holder.CONTEXT.getString(286130530, new Object[] { Integer.valueOf(this.mSlotId + 1) });
        }
      }
      return (String)localObject3;
    }
    
    public CharSequence getDisplayName()
    {
      Object localObject;
      if (this.mSubInfo.getNameSource() == 2) {
        localObject = this.mSubInfo.getDisplayName();
      } else {
        localObject = getDefaultDisplayName();
      }
      return (CharSequence)localObject;
    }
    
    public String getDisplayNumber()
    {
      return this.mSubInfo.getNumber();
    }
    
    public String getIccId()
    {
      return this.mSubInfo.getIccId();
    }
    
    public int getMcc()
    {
      return this.mSubInfo.getMcc();
    }
    
    public int getMnc()
    {
      return this.mSubInfo.getMnc();
    }
    
    public int getPhoneId()
    {
      return this.mSlotId;
    }
    
    public int getSlotId()
    {
      return this.mSlotId;
    }
    
    public int getSubscriptionId()
    {
      return this.mSubscriptionId;
    }
    
    public boolean isActivated()
    {
      if (this.mSlotId != SubscriptionManager.INVALID_SLOT_ID)
      {
        if (!TelephonyManager.getDefault().isMultiSimEnabled()) {
          return true;
        }
        try
        {
          boolean bool = TelephonyManagerEx.getDefault().getMiuiTelephony().isIccCardActivate(this.mSlotId);
          return bool;
        }
        catch (Exception localException) {}
      }
      return true;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/SubscriptionManagerEx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */