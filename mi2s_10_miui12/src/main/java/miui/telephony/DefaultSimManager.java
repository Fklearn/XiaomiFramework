package miui.telephony;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.provider.MiuiSettings.VirtualSim;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.Rlog;
import android.text.TextUtils;
import com.android.internal.telephony.CallManager;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.ITelephonyRegistry.Stub;
import com.android.internal.telephony.IccCardActivateHelper;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants.State;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.PhoneSwitcher;
import com.android.internal.telephony.RILConstants;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.dataconnection.TelephonyNetworkFactory;
import com.android.internal.telephony.uicc.IccCardStatus.CardState;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccSlot;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import miui.os.Build;
import miui.util.Network;

public class DefaultSimManager
  implements SubscriptionManager.OnSubscriptionsChangedListener, VirtualSimUtils.VirtualSimListenner
{
  private static final int EVENT_DELAYED_MODEM_RESET = 7;
  private static final int EVENT_ICC_CHANGED = 3;
  private static final int EVENT_IMSI_READY = 4;
  private static final int EVENT_INIT_DEFAULT_SLOT = 0;
  private static final int EVENT_MODEM_RESET = 6;
  private static final int EVENT_PRECISE_CALL_STATE_CHANGED = 5;
  private static final int EVENT_SYNC_DEFAULT_DATA_SLOT = 1;
  private static final int EVENT_SYNC_DEFAULT_VOICE_SLOT = 2;
  public static final int EVENT_VOLTE_TEMP_DDS = 130;
  public static final boolean IS_CUSTOMIZED_FOR_CM = "cm".equals(Network.getOperatorType());
  public static final String KEY_IS_USE_PREFRRED = "key_is_use_preferred";
  public static final String KEY_LAST_ICCID = "key_last_iccid";
  public static final String KEY_PREFERRED_DATA_SLOT = "key_preferred_data_slot";
  private static final String LAST_ICC_ID_PROPERTY = "persist.radio.iccid";
  private static String LOG_TAG = "DefaultSimManager";
  private static final boolean RELEASE = false;
  private static final boolean REQUEST = true;
  public static final String VICE_SLOT_VOLTE_DATA_ENABLED = "vice_slot_volte_data_enabled";
  private static final int WAITING_MODEM_RESET_TIME = 60000;
  private static final int WAITING_SIM_TIME = 4000;
  private static boolean mIsModemResetting = false;
  private static DefaultSimManager sInstance;
  private IccRecords[] m3gpp2IccRecords;
  private IccRecords[] m3gppIccRecords;
  private PhoneConstants.State mCallState = PhoneConstants.State.IDLE;
  private boolean mCallStateChangeRegistered = false;
  private List<DataSlotListener> mDataSlotListener = new ArrayList(1);
  private boolean mDataSlotReady = false;
  private DefaultSlotSelector mDefaultSlotSelector;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      Object localObject = (AsyncResult)paramAnonymousMessage.obj;
      int i;
      switch (paramAnonymousMessage.what)
      {
      default: 
        break;
      case 7: 
        DefaultSimManager.access$1202(false);
        break;
      case 6: 
        DefaultSimManager.access$1202(true);
        if (hasMessages(7)) {
          removeMessages(7);
        }
        Rlog.d(DefaultSimManager.LOG_TAG, "Event EVENT_MODEM_RESET Received");
        sendMessageDelayed(obtainMessage(7), 60000L);
        for (i = 0; i < DefaultSimManager.this.mSimInsertStatesDone.length; i++) {
          DefaultSimManager.this.mSimInsertStatesDone[i] = 0;
        }
        break;
      case 5: 
        DefaultSimManager.this.onCallStateChanged();
        break;
      case 4: 
        if ((((AsyncResult)localObject).userObj instanceof Integer))
        {
          i = ((Integer)((AsyncResult)localObject).userObj).intValue();
          if (((DefaultSimManager.this.m3gppIccRecords[i] == null) || (DefaultSimManager.this.m3gppIccRecords[i].getIMSI() != null)) && ((DefaultSimManager.this.m3gpp2IccRecords[i] == null) || (DefaultSimManager.this.m3gpp2IccRecords[i].getIMSI() != null))) {
            DefaultSimManager.this.notifyImsiReady(i);
          }
        }
        if (DefaultSimManager.this.needListenImsiReady())
        {
          paramAnonymousMessage = DefaultSimManager.this;
          if (!paramAnonymousMessage.isAllInfoReady(paramAnonymousMessage.mLastSubInfos))
          {
            DefaultSimManager.this.mHandler.removeMessages(0);
            DefaultSimManager.access$602(DefaultSimManager.this, null);
            DefaultSimManager.this.onSubscriptionsChanged();
          }
        }
        break;
      case 3: 
        if (((AsyncResult)localObject).result != null)
        {
          int j = ((Integer)((AsyncResult)localObject).result).intValue();
          paramAnonymousMessage = UiccController.getInstance().getUiccCard(j);
          i = DefaultSimManager.this.mSimApplicationNum[j];
          DefaultSimManager.this.updateCard(paramAnonymousMessage, j);
          if ((i != 0) && (DefaultSimManager.this.mSimApplicationNum[j] == 0))
          {
            if (PhoneDebug.VDBG)
            {
              paramAnonymousMessage = DefaultSimManager.LOG_TAG;
              localObject = new StringBuilder();
              ((StringBuilder)localObject).append("EVENT_ICC_CHANGED sim");
              ((StringBuilder)localObject).append(j);
              ((StringBuilder)localObject).append(" application is not OK");
              Rlog.d(paramAnonymousMessage, ((StringBuilder)localObject).toString());
            }
            DefaultSimManager.this.mHandler.removeMessages(0);
          }
          else if ((i == 0) && (DefaultSimManager.this.mSimApplicationNum[j] != 0))
          {
            if (PhoneDebug.VDBG)
            {
              localObject = DefaultSimManager.LOG_TAG;
              paramAnonymousMessage = new StringBuilder();
              paramAnonymousMessage.append("EVENT_ICC_CHANGED sim");
              paramAnonymousMessage.append(j);
              paramAnonymousMessage.append(" application is OK");
              Rlog.d((String)localObject, paramAnonymousMessage.toString());
            }
            DefaultSimManager.this.mHandler.removeMessages(0);
            DefaultSimManager.access$602(DefaultSimManager.this, null);
            DefaultSimManager.this.onSubscriptionsChanged();
          }
        }
        else
        {
          if (PhoneDebug.VDBG) {
            Rlog.e(DefaultSimManager.LOG_TAG, "Error: Invalid card index EVENT_ICC_CHANGED ");
          }
          return;
        }
        break;
      case 2: 
        DefaultSimManager.this.syncVoiceSubscription(SubscriptionManager.getDefault().getSubscriptionInfoListInternal());
        break;
      case 1: 
        DefaultSimManager.this.syncDataSubscription(SubscriptionManager.getDefault().getSubscriptionInfoListInternal());
        break;
      case 0: 
        DefaultSimManager.this.onInitDefaultSlot();
      }
    }
  };
  private List<ImsiListener> mImsiListener = new ArrayList(1);
  private String mInitModes;
  private boolean mIsDisableingVirtualSim = false;
  private boolean mIsUsePreferred;
  private List<SubscriptionInfo> mLastSubInfos;
  private boolean mNeedRestoreTempDdsSwitch = false;
  private PhoneConstants.State mOldCallState = PhoneConstants.State.IDLE;
  private int mOldDefaultDataSlotId;
  private int mOldDefaultDataSubId;
  private int mPreferredDataSlot;
  private int mPropertyDefautlDataSlotId = SystemProperties.getInt("persist.radio.default.data", SubscriptionManager.INVALID_SLOT_ID);
  private int mPropertyDefautlVoiceSlotId = SystemProperties.getInt("persist.radio.default.voice", SubscriptionManager.INVALID_SLOT_ID);
  private final SubscriptionController mSc = SubscriptionController.getInstance();
  private int[] mSimApplicationNum;
  private int[] mSimInsertStates;
  private boolean[] mSimInsertStatesDone;
  private final IccCardStatus.CardState[] mSimStatus;
  private boolean mViceSlotVolteDataEnabled = false;
  
  private DefaultSimManager()
  {
    Object localObject1 = PhoneFactory.getPhones();
    this.mSimStatus = new IccCardStatus.CardState[localObject1.length];
    this.mSimApplicationNum = new int[MiuiTelephony.PHONE_COUNT];
    this.m3gppIccRecords = new IccRecords[MiuiTelephony.PHONE_COUNT];
    this.m3gpp2IccRecords = new IccRecords[MiuiTelephony.PHONE_COUNT];
    this.mSimInsertStatesDone = new boolean[MiuiTelephony.PHONE_COUNT];
    this.mSimInsertStates = new int[MiuiTelephony.PHONE_COUNT];
    for (int i = 0; i < MiuiTelephony.PHONE_COUNT; i++)
    {
      this.mSimStatus[i] = IccCardStatus.CardState.CARDSTATE_ERROR;
      this.mSimApplicationNum[i] = 0;
      this.m3gppIccRecords[i] = null;
      this.m3gpp2IccRecords[i] = null;
      this.mSimInsertStatesDone[i] = false;
    }
    this.mOldDefaultDataSlotId = this.mPropertyDefautlDataSlotId;
    new TelephonyBroadcastReceiver(null).register(MiuiTelephony.sContext);
    this.mOldDefaultDataSubId = this.mSc.getDefaultDataSubId();
    UiccController.getInstance().registerForIccChanged(this.mHandler, 3, null);
    localObject1[0].mCi.registerForModemReset(this.mHandler, 6, null);
    SubscriptionManager.getDefault().addOnSubscriptionsChangedListener(this);
    this.mInitModes = Settings.Global.getString(MiuiTelephony.sContext.getContentResolver(), "preferred_network_mode");
    VirtualSimUtils.init();
    VirtualSimUtils.getInstance().addVirtualSimChangedListener(this);
    Object localObject2 = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
    this.mIsUsePreferred = ((SharedPreferences)localObject2).getBoolean("key_is_use_preferred", false);
    this.mPreferredDataSlot = ((SharedPreferences)localObject2).getInt("key_preferred_data_slot", SubscriptionManager.INVALID_SLOT_ID);
    localObject1 = SystemProperties.get("persist.radio.iccid", null);
    if (!TextUtils.isEmpty((CharSequence)localObject1))
    {
      localObject2 = ((SharedPreferences)localObject2).edit();
      ((SharedPreferences.Editor)localObject2).putString("key_last_iccid", (String)localObject1);
      ((SharedPreferences.Editor)localObject2).apply();
      SystemProperties.set("persist.radio.iccid", "");
    }
    initViceSlotVolteDataFeature();
  }
  
  private void broadcastDataSlotChanged(int paramInt)
  {
    String str = LOG_TAG;
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("broadcastDataSlotChanged old=");
    ((StringBuilder)localObject).append(this.mOldDefaultDataSlotId);
    ((StringBuilder)localObject).append(", new=");
    ((StringBuilder)localObject).append(paramInt);
    Rlog.d(str, ((StringBuilder)localObject).toString());
    localObject = new Intent("miui.intent.action.ACTION_DEFAULT_DATA_SLOT_CHANGED");
    ((Intent)localObject).putExtra("old_data_slot", this.mOldDefaultDataSlotId);
    SubscriptionManager.putSlotIdExtra((Intent)localObject, paramInt);
    MiuiTelephony.sContext.sendBroadcast((Intent)localObject);
    notifyDataSlotReady(true);
  }
  
  private void broadcastDataSlotReady(int paramInt, int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      localObject = "null";
    } else {
      localObject = simInsertStatesToString(paramArrayOfInt);
    }
    String str = LOG_TAG;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("broadcastDataSlotReady dataSlot = ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(", sim state =[");
    localStringBuilder.append((String)localObject);
    localStringBuilder.append("]");
    Rlog.d(str, localStringBuilder.toString());
    Object localObject = new Intent("miui.intent.action.ACTION_DEFAULT_DATA_SLOT_READY");
    SubscriptionManager.putSlotIdExtra((Intent)localObject, paramInt);
    ((Intent)localObject).putExtra("sim_insert_state_array", paramArrayOfInt);
    MiuiTelephony.sContext.sendBroadcast((Intent)localObject);
    notifyDataSlotReady(false);
  }
  
  public static boolean findSlotInSubinfos(int paramInt, List<SubscriptionInfo> paramList)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      if (paramInt == ((SubscriptionInfo)paramList.next()).getSlotId()) {
        return true;
      }
    }
    return false;
  }
  
  public static IccCardStatus.CardState getCardState(int paramInt)
  {
    UiccSlot localUiccSlot = UiccController.getInstance().getUiccSlotForPhone(paramInt);
    if (localUiccSlot != null)
    {
      UiccCard localUiccCard = localUiccSlot.getUiccCard();
      if (localUiccCard != null) {
        return localUiccCard.getCardState();
      }
      if (!localUiccSlot.isStateUnknown()) {
        return IccCardStatus.CardState.CARDSTATE_ABSENT;
      }
    }
    return null;
  }
  
  private int getDataSlotForVirtualSim(int paramInt, List<SubscriptionInfo> paramList)
  {
    if (VirtualSimUtils.isCloudSimEnabled()) {
      return paramInt;
    }
    int i;
    if (paramList.size() == 2)
    {
      paramList = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
      boolean bool = paramList.getBoolean("key_is_use_preferred", false);
      i = SubscriptionManager.INVALID_SLOT_ID;
      if (bool) {
        i = paramList.getInt("key_preferred_data_slot", SubscriptionManager.INVALID_SLOT_ID);
      }
      int j = VirtualSimUtils.getInstance().getVirtualSimSlotId();
      if (i != SubscriptionManager.INVALID_SLOT_ID) {
        paramInt = i;
      } else if (j != SubscriptionManager.INVALID_SLOT_ID) {
        paramInt = j;
      }
      String str = LOG_TAG;
      paramList = new StringBuilder();
      paramList.append("getDataSlotForVirtualSim ret=");
      paramList.append(paramInt);
      paramList.append(" usePreferred=");
      paramList.append(bool);
      paramList.append(" preferredSlot=");
      paramList.append(i);
      paramList.append(" virtualSLotId=");
      paramList.append(j);
      Rlog.d(str, paramList.toString());
      i = paramInt;
    }
    do
    {
      paramInt = i;
      break;
      i = paramInt;
    } while (paramList.size() != 1);
    if (VirtualSimUtils.getInstance().isVirtualSimEnabled())
    {
      if (((SubscriptionInfo)paramList.get(0)).getSlotId() == MiuiSettings.VirtualSim.getVirtualSimSlotId(MiuiTelephony.sContext))
      {
        Rlog.d(LOG_TAG, "getDataSlotForVirtualSim restore for only virtual sim inserted");
        storeValues("key_is_use_preferred", false, "key_preferred_data_slot", ((SubscriptionInfo)paramList.get(0)).getSlotId());
      }
      else
      {
        Rlog.d(LOG_TAG, "getDataSlotForVirtualSim waiting for virtual sim ready");
      }
    }
    else
    {
      Rlog.d(LOG_TAG, "getDataSlotForVirtualSim restore for virtual sim disabled");
      storeValues("key_is_use_preferred", false, "key_preferred_data_slot", SubscriptionManager.INVALID_SLOT_ID);
    }
    return paramInt;
  }
  
  public static int getDefaultDataSlotId()
  {
    DefaultSimManager localDefaultSimManager = sInstance;
    if ((localDefaultSimManager != null) && (localDefaultSimManager.mPropertyDefautlDataSlotId != SubscriptionManager.INVALID_SLOT_ID)) {
      return sInstance.mPropertyDefautlDataSlotId;
    }
    return SubscriptionManager.getDefault().getDefaultDataSlotId();
  }
  
  public static int getDefaultVoiceSlotId()
  {
    DefaultSimManager localDefaultSimManager = sInstance;
    if (localDefaultSimManager != null) {
      return localDefaultSimManager.mPropertyDefautlVoiceSlotId;
    }
    return SystemProperties.getInt("persist.radio.default.voice", SubscriptionManager.INVALID_SLOT_ID);
  }
  
  public static DefaultSimManager getInstance()
  {
    return sInstance;
  }
  
  public static boolean getIsModemResetting()
  {
    return mIsModemResetting;
  }
  
  public static PhoneAccountHandle getPhoneAccountHandle(int paramInt, Context paramContext)
  {
    paramContext = TelecomManager.from(paramContext);
    ListIterator localListIterator = paramContext.getCallCapablePhoneAccounts().listIterator();
    while (localListIterator.hasNext())
    {
      localObject = (PhoneAccountHandle)localListIterator.next();
      paramContext = ((PhoneAccountHandle)localObject).getId();
      if (TextUtils.equals(PhoneFactory.getPhone(paramInt).getFullIccSerialNumber(), paramContext)) {
        return (PhoneAccountHandle)localObject;
      }
    }
    paramContext = LOG_TAG;
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("cannot find PhoneAccountHandle for slot = ");
    ((StringBuilder)localObject).append(paramInt);
    Rlog.d(paramContext, ((StringBuilder)localObject).toString());
    return null;
  }
  
  public static int getPreferredNetworkModeFromDb(Context paramContext, int paramInt)
  {
    Object localObject;
    if (((MiuiTelephony.IS_MTK) && (!TelephonyManager.getDefault().isDualVolteSupported())) || (MiuiTelephony.IS_PINECONE))
    {
      localObject = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(paramInt);
      if (localObject != null)
      {
        ContentResolver localContentResolver = paramContext.getContentResolver();
        paramContext = new StringBuilder();
        paramContext.append("preferred_network_mode");
        paramContext.append(((SubscriptionInfo)localObject).getSubscriptionId());
        return Settings.Global.getInt(localContentResolver, paramContext.toString(), RILConstants.PREFERRED_NETWORK_MODE);
      }
      return RILConstants.PREFERRED_NETWORK_MODE;
    }
    int i = Phone.PREFERRED_NT_MODE;
    try
    {
      int j = android.telephony.TelephonyManager.getIntAtIndex(paramContext.getContentResolver(), "preferred_network_mode", paramInt);
      paramInt = j;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
      paramContext = LOG_TAG;
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getPreferredNetworkModeFromDb slot=");
      ((StringBuilder)localObject).append(paramInt);
      ((StringBuilder)localObject).append(" e=");
      ((StringBuilder)localObject).append(localSettingNotFoundException);
      Rlog.i(paramContext, ((StringBuilder)localObject).toString());
      paramInt = i;
    }
    return paramInt;
  }
  
  static void init()
  {
    sInstance = new DefaultSimManager();
  }
  
  private void initDefaultDataSlotId(int paramInt, int[] paramArrayOfInt)
  {
    this.mPropertyDefautlDataSlotId = paramInt;
    SystemProperties.set("persist.radio.default.data", String.valueOf(paramInt));
    this.mOldDefaultDataSubId = this.mSc.getDefaultDataSubId();
    int i = this.mSc.getSubIdUsingPhoneId(paramInt);
    if (this.mOldDefaultDataSubId != i)
    {
      this.mSc.setDefaultDataSubId(i);
    }
    else if (this.mOldDefaultDataSlotId != paramInt)
    {
      broadcastDataSlotChanged(paramInt);
      this.mOldDefaultDataSlotId = paramInt;
    }
    else
    {
      broadcastDataSlotReady(paramInt, paramArrayOfInt);
    }
  }
  
  private void initViceSlotVolteDataFeature()
  {
    boolean bool = SystemProperties.getBoolean("persist.vendor.radio.enable_temp_dds", false);
    if ((!MiuiTelephony.IS_MTK) && (!bool))
    {
      if (Settings.Global.getInt(MiuiTelephony.sContext.getContentResolver(), "vice_slot_volte_data_enabled", 0) != 0) {
        bool = true;
      } else {
        bool = false;
      }
      this.mViceSlotVolteDataEnabled = bool;
      if (this.mViceSlotVolteDataEnabled) {
        registerCallStateChange();
      }
      ContentObserver local2 = new ContentObserver(null)
      {
        public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
        {
          if ((paramAnonymousUri != null) && ("vice_slot_volte_data_enabled".equals(paramAnonymousUri.getLastPathSegment())))
          {
            paramAnonymousUri = DefaultSimManager.this;
            Object localObject = MiuiTelephony.sContext.getContentResolver();
            paramAnonymousBoolean = false;
            if (Settings.Global.getInt((ContentResolver)localObject, "vice_slot_volte_data_enabled", 0) != 0) {
              paramAnonymousBoolean = true;
            }
            DefaultSimManager.access$1502(paramAnonymousUri, paramAnonymousBoolean);
            if ((DefaultSimManager.this.mViceSlotVolteDataEnabled) && (!DefaultSimManager.this.mCallStateChangeRegistered)) {
              DefaultSimManager.this.registerCallStateChange();
            } else if ((!DefaultSimManager.this.mViceSlotVolteDataEnabled) && (DefaultSimManager.this.mCallStateChangeRegistered)) {
              DefaultSimManager.this.unRegisterCallStateChange();
            }
            localObject = DefaultSimManager.LOG_TAG;
            paramAnonymousUri = new StringBuilder();
            paramAnonymousUri.append("mViceSlotVolteDataEnabled : ");
            paramAnonymousUri.append(DefaultSimManager.this.mViceSlotVolteDataEnabled);
            Rlog.d((String)localObject, paramAnonymousUri.toString());
            return;
          }
        }
      };
      MiuiTelephony.sContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("vice_slot_volte_data_enabled"), false, local2);
      return;
    }
  }
  
  private boolean isAllInfoReady(List<SubscriptionInfo> paramList)
  {
    if ((paramList != null) && (!paramList.isEmpty()))
    {
      if (!isApplicationReady(paramList))
      {
        Rlog.d(LOG_TAG, "onSubscriptionsChanged return for no sim application");
        return false;
      }
      if (!isAllSubscriptionInfoCreated(paramList))
      {
        Rlog.d(LOG_TAG, "onSubscriptionsChanged return for some SubscriptionInfo is not created");
        return false;
      }
      if (!isMccMncReady(paramList))
      {
        Rlog.d(LOG_TAG, "onSubscriptionsChanged return for mcc is not ready");
        return false;
      }
      return true;
    }
    return false;
  }
  
  private boolean isAllSubscriptionInfoCreated(List<SubscriptionInfo> paramList)
  {
    int i = 0;
    int j = 0;
    while (j < MiuiTelephony.PHONE_COUNT)
    {
      int k = i;
      if (this.mSimStatus[j] == IccCardStatus.CardState.CARDSTATE_PRESENT) {
        k = i + 1;
      }
      j++;
      i = k;
    }
    boolean bool;
    if (i == paramList.size()) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isApplicationReady(List<SubscriptionInfo> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      paramList = (SubscriptionInfo)localIterator.next();
      if ((this.mSimStatus[paramList.getSlotId()] != IccCardStatus.CardState.CARDSTATE_ERROR) && (this.mSimApplicationNum[paramList.getSlotId()] < 1)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isDcOnlyVirtualSim(int paramInt)
  {
    return (MiuiSettings.VirtualSim.isVirtualSimEnabled(MiuiTelephony.sContext)) && (MiuiSettings.VirtualSim.getVirtualSimSlotId(MiuiTelephony.sContext) == paramInt) && (1 == MiuiSettings.VirtualSim.getVirtualSimType(MiuiTelephony.sContext));
  }
  
  private boolean isMccMncReady(List<SubscriptionInfo> paramList)
  {
    if ((!Build.IS_CM_CUSTOMIZATION) && (!Build.IS_INTERNATIONAL_BUILD)) {
      return true;
    }
    if ((paramList != null) && (paramList.size() >= 2))
    {
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        Object localObject = (SubscriptionInfo)paramList.next();
        if (((SubscriptionInfo)localObject).getMcc() == 0)
        {
          String str = getSimImsi(((SubscriptionInfo)localObject).getSlotId());
          if ((str == null) || (str.isEmpty()))
          {
            localObject = getRuimImsi(((SubscriptionInfo)localObject).getSlotId());
            if ((localObject == null) || (((String)localObject).isEmpty())) {}
          }
          else
          {
            continue;
          }
          Rlog.d(LOG_TAG, "isMccMncReady both cdma and gsm imsi is null");
          return false;
        }
      }
      return true;
    }
    return true;
  }
  
  private boolean isSameWithBefore(List<SubscriptionInfo> paramList)
  {
    Object localObject = this.mLastSubInfos;
    if (localObject == null) {
      return false;
    }
    int i = ((List)localObject).size();
    if (i == paramList.size())
    {
      int j = 0;
      while (j < i)
      {
        localObject = (SubscriptionInfo)this.mLastSubInfos.get(j);
        SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)paramList.get(j);
        if ((((SubscriptionInfo)localObject).getSlotId() == localSubscriptionInfo.getSlotId()) && (((SubscriptionInfo)localObject).getSubscriptionId() == localSubscriptionInfo.getSubscriptionId())) {
          j++;
        } else {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  private boolean needListenImsiReady()
  {
    boolean bool;
    if ((!Build.IS_CM_CUSTOMIZATION) && (!IS_CUSTOMIZED_FOR_CM) && (!Build.IS_INTERNATIONAL_BUILD)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void notifyPreferredDataSubIdChanged(int paramInt)
  {
    ITelephonyRegistry localITelephonyRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));
    try
    {
      String str = LOG_TAG;
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      localStringBuilder.append("notifyPreferredDataSubIdChanged to ");
      localStringBuilder.append(paramInt);
      Rlog.d(str, localStringBuilder.toString());
      localITelephonyRegistry.notifyActiveDataSubIdChanged(paramInt);
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void onCallStateChanged()
  {
    int i = getDefaultDataSlotId();
    int j;
    if (i == 0) {
      j = 1;
    } else {
      j = 0;
    }
    Object localObject1 = PhoneFactory.getPhone(j);
    this.mOldCallState = this.mCallState;
    this.mCallState = ((Phone)localObject1).getState();
    Object localObject2 = this.mOldCallState;
    PhoneConstants.State localState = this.mCallState;
    if (localObject2 == localState) {
      return;
    }
    if ((localState != PhoneConstants.State.IDLE) && (this.mOldCallState == PhoneConstants.State.IDLE))
    {
      Rlog.d(LOG_TAG, "onCallStateChanged offhook");
      if (!((Phone)localObject1).isImsRegistered()) {
        return;
      }
      Settings.Global.putInt(MiuiTelephony.sContext.getContentResolver(), "multi_sim_data_call", ((Phone)localObject1).getSubId());
      localObject2 = PhoneSwitcher.getInstance();
      if (localObject2 != null) {
        ((PhoneSwitcher)localObject2).obtainMessage(130, Integer.valueOf(j)).sendToTarget();
      }
      notifyPreferredDataSubIdChanged(((Phone)localObject1).getSubId());
      for (i = 0; i < MiuiTelephony.PHONE_COUNT; i++)
      {
        localObject1 = PhoneFactory.sTelephonyNetworkFactories[i];
        boolean bool;
        if (i == j) {
          bool = true;
        } else {
          bool = false;
        }
        ((TelephonyNetworkFactory)localObject1).applyDefaultRequests(bool);
      }
      this.mNeedRestoreTempDdsSwitch = true;
    }
    else if ((this.mCallState == PhoneConstants.State.IDLE) && (this.mOldCallState != PhoneConstants.State.IDLE))
    {
      Rlog.d(LOG_TAG, "onCallStateChanged idle");
      if (!this.mNeedRestoreTempDdsSwitch) {
        return;
      }
      int k = PhoneFactory.getPhone(i).getSubId();
      Settings.Global.putInt(MiuiTelephony.sContext.getContentResolver(), "multi_sim_data_call", k);
      localObject1 = PhoneSwitcher.getInstance();
      if (localObject1 != null) {
        ((PhoneSwitcher)localObject1).obtainMessage(130, Integer.valueOf(i)).sendToTarget();
      }
      notifyPreferredDataSubIdChanged(k);
      PhoneFactory.sTelephonyNetworkFactories[j].applyDefaultRequests(false);
      PhoneFactory.sTelephonyNetworkFactories[i].applyDefaultRequests(true);
      this.mNeedRestoreTempDdsSwitch = false;
    }
  }
  
  private void onDefaultVoiceSubscriptionChanged()
  {
    if (this.mHandler.hasMessages(0))
    {
      Rlog.d(LOG_TAG, "onDefaultVoiceSubscriptionChanged inconsistent and wait for init");
      return;
    }
    List localList = SubscriptionManager.getDefault().getSubscriptionInfoListInternal();
    if (localList.size() == MiuiTelephony.PHONE_COUNT)
    {
      syncVoiceSubscription(localList);
    }
    else
    {
      Rlog.d(LOG_TAG, "onDefaultVoiceSubscriptionChanged delay");
      this.mHandler.removeMessages(2);
      this.mHandler.sendEmptyMessageDelayed(2, 4000L);
    }
  }
  
  private void registerCallStateChange()
  {
    this.mCallStateChangeRegistered = true;
    CallManager.getInstance().registerForPreciseCallStateChanged(this.mHandler, 5, null);
  }
  
  public static void setIsModemResetting(boolean paramBoolean)
  {
    mIsModemResetting = paramBoolean;
  }
  
  public static void setNetworkModeInDb(int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder1 = new StringBuilder(128);
    localStringBuilder1.append("setNetworkModeInDb slotId=");
    localStringBuilder1.append(paramInt1);
    localStringBuilder1.append(" networkType=");
    localStringBuilder1 = localStringBuilder1.append(paramInt2);
    if (((MiuiTelephony.IS_MTK) && (TelephonyManager.getDefault().isDualVolteSupported())) || (MiuiTelephony.IS_QCOM)) {
      android.telephony.TelephonyManager.putIntAtIndex(MiuiTelephony.sContext.getContentResolver(), "preferred_network_mode", paramInt1, paramInt2);
    }
    SubscriptionInfo localSubscriptionInfo = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(paramInt1);
    if (localSubscriptionInfo != null)
    {
      ContentResolver localContentResolver = MiuiTelephony.sContext.getContentResolver();
      StringBuilder localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append("preferred_network_mode");
      localStringBuilder2.append(localSubscriptionInfo.getSubscriptionId());
      Settings.Global.putInt(localContentResolver, localStringBuilder2.toString(), paramInt2);
      localStringBuilder1.append(" sub=");
      localStringBuilder1.append(localSubscriptionInfo.getSubscriptionId());
    }
    else
    {
      localStringBuilder1.append(" sub=");
      localStringBuilder1.append("invalid");
    }
    Rlog.i(LOG_TAG, localStringBuilder1.toString());
  }
  
  private String simInsertStatesToString(int[] paramArrayOfInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      if (i != 0) {
        localStringBuilder.append(", ");
      }
      int j = paramArrayOfInt[i];
      if (j != 0)
      {
        if (j != 1)
        {
          if (j != 2)
          {
            if (j != 3)
            {
              if (j == 4) {
                localStringBuilder.append("CHANGED");
              }
            }
            else {
              localStringBuilder.append("REMOVED");
            }
          }
          else {
            localStringBuilder.append("NEW_CARD");
          }
        }
        else {
          localStringBuilder.append("NO CARD");
        }
      }
      else {
        localStringBuilder.append("NO_CHANGE");
      }
    }
    return localStringBuilder.toString();
  }
  
  public static void storeValues(String paramString1, boolean paramBoolean, String paramString2, int paramInt)
  {
    SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext).edit();
    if (paramString1 != null) {
      localEditor.putBoolean(paramString1, paramBoolean);
    }
    if (paramString2 != null) {
      localEditor.putInt(paramString2, paramInt);
    }
    String str = LOG_TAG;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("storeValues ");
    localStringBuilder.append(paramString1);
    localStringBuilder.append("=");
    localStringBuilder.append(paramBoolean);
    localStringBuilder.append(" ");
    localStringBuilder.append(paramString2);
    localStringBuilder.append("=");
    localStringBuilder.append(paramInt);
    Rlog.d(str, localStringBuilder.toString());
    localEditor.apply();
  }
  
  private void syncDataSubscription(List<SubscriptionInfo> paramList)
  {
    int i = this.mSc.getDefaultDataSubId();
    int j = this.mPropertyDefautlDataSlotId;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      paramList = (SubscriptionInfo)localIterator.next();
      if ((paramList.getSlotId() == j) && (paramList.getSubscriptionId() != i))
      {
        Rlog.d(LOG_TAG, "syncDataSubscription reset");
        this.mSc.setDefaultDataSubId(paramList.getSubscriptionId());
        return;
      }
    }
    Rlog.d(LOG_TAG, "syncDataSubscription ignore");
  }
  
  private void syncVoiceSubscription(List<SubscriptionInfo> paramList)
  {
    int i = this.mSc.getDefaultVoiceSubId();
    int j = this.mPropertyDefautlVoiceSlotId;
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)paramList.next();
      if ((localSubscriptionInfo.getSlotId() == j) && (localSubscriptionInfo.getSubscriptionId() != i))
      {
        Rlog.d(LOG_TAG, "syncVoiceSubscription reset");
        this.mSc.setDefaultVoiceSubId(localSubscriptionInfo.getSubscriptionId());
        return;
      }
    }
    Rlog.d(LOG_TAG, "syncVoiceSubscription ignore");
  }
  
  private void unRegisterCallStateChange()
  {
    this.mCallStateChangeRegistered = false;
    CallManager.getInstance().unregisterForPreciseCallStateChanged(this.mHandler);
  }
  
  private void updateCard(UiccCard paramUiccCard, int paramInt)
  {
    if (paramUiccCard == null) {
      this.mSimApplicationNum[paramInt] = 0;
    } else {
      this.mSimApplicationNum[paramInt] = paramUiccCard.getNumApplications();
    }
    Object localObject1 = getCardState(paramInt);
    Object localObject2 = this.mSimStatus;
    if (localObject1 == null) {
      localObject1 = IccCardStatus.CardState.CARDSTATE_ERROR;
    }
    localObject2[paramInt] = localObject1;
    int[] arrayOfInt = new int[2];
    int[] tmp58_56 = arrayOfInt;
    tmp58_56[0] = 1;
    int[] tmp62_58 = tmp58_56;
    tmp62_58[1] = 2;
    tmp62_58;
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      if (arrayOfInt[i] == 1) {
        localObject1 = this.m3gppIccRecords;
      } else {
        localObject1 = this.m3gpp2IccRecords;
      }
      Object localObject3 = null;
      if (paramUiccCard == null) {
        localObject2 = null;
      } else {
        localObject2 = paramUiccCard.getApplication(arrayOfInt[i]);
      }
      if (localObject2 == null) {
        localObject2 = localObject3;
      } else {
        localObject2 = ((UiccCardApplication)localObject2).getIccRecords();
      }
      if (localObject2 != localObject1[paramInt])
      {
        if (localObject1[paramInt] != null) {
          localObject1[paramInt].unregisterForImsiReady(this.mHandler);
        }
        if (localObject2 != null) {
          ((IccRecords)localObject2).registerForImsiReady(this.mHandler, 4, Integer.valueOf(paramInt));
        }
      }
      localObject1[paramInt] = localObject2;
    }
  }
  
  private int[] updateNewIccIds(List<SubscriptionInfo> paramList, boolean paramBoolean)
  {
    int[] arrayOfInt = new int[MiuiTelephony.PHONE_COUNT];
    Object localObject1 = new String[arrayOfInt.length];
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
    Object localObject2 = localSharedPreferences.getString("key_last_iccid", "");
    Object localObject3;
    if (TextUtils.isEmpty((CharSequence)localObject2))
    {
      Arrays.fill((Object[])localObject1, 0, localObject1.length, null);
    }
    else
    {
      localObject3 = ((String)localObject2).split(",");
      for (i = 0; i < localObject1.length; i++)
      {
        if (i < localObject3.length) {
          localObject2 = localObject3[i];
        } else {
          localObject2 = null;
        }
        localObject1[i] = localObject2;
        if ("<null>".equals(localObject1[i])) {
          localObject1[i] = null;
        }
      }
    }
    localObject2 = new String[arrayOfInt.length];
    Object localObject4 = paramList.iterator();
    while (((Iterator)localObject4).hasNext())
    {
      localObject3 = (SubscriptionInfo)((Iterator)localObject4).next();
      localObject2[localObject3.getSlotId()] = ((SubscriptionInfo)localObject3).getIccId();
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      if (((!paramBoolean) && (!findSlotInSubinfos(i, paramList))) || (this.mSimInsertStatesDone[i] != 0))
      {
        localObject2[i] = localObject1[i];
        arrayOfInt[i] = this.mSimInsertStates[i];
      }
      else
      {
        int j;
        if (localObject1[i] == null)
        {
          if (localObject2[i] == null) {
            j = 1;
          } else {
            j = 2;
          }
          arrayOfInt[i] = j;
        }
        else if (localObject2[i] == null)
        {
          arrayOfInt[i] = 3;
          localObject3 = this.mDefaultSlotSelector;
          if ((localObject3 != null) && (!this.mIsDisableingVirtualSim)) {
            ((DefaultSlotSelector)localObject3).onSimRemoved(i, (String[])localObject1);
          }
        }
        else
        {
          if (localObject2[i].equalsIgnoreCase(localObject1[i])) {
            j = 0;
          } else {
            j = 4;
          }
          arrayOfInt[i] = j;
        }
        if (localObject2[i] != null) {
          this.mSimInsertStatesDone[i] = true;
        }
        localObject4 = LOG_TAG;
        localObject3 = new StringBuilder();
        ((StringBuilder)localObject3).append("updateNewIccIds simInsertStates[");
        ((StringBuilder)localObject3).append(i);
        ((StringBuilder)localObject3).append("]=");
        ((StringBuilder)localObject3).append(arrayOfInt[i]);
        Rlog.d((String)localObject4, ((StringBuilder)localObject3).toString());
      }
    }
    localObject1 = new StringBuilder();
    for (i = 0; i < localObject2.length; i++)
    {
      if (i != 0) {
        ((StringBuilder)localObject1).append(',');
      }
      if (localObject2[i] == null) {
        paramList = "<null>";
      } else {
        paramList = localObject2[i];
      }
      ((StringBuilder)localObject1).append(paramList);
    }
    paramList = localSharedPreferences.edit();
    paramList.putString("key_last_iccid", ((StringBuilder)localObject1).toString());
    paramList.apply();
    return arrayOfInt;
  }
  
  public void addDataSlotListener(DataSlotListener paramDataSlotListener)
  {
    List localList = this.mDataSlotListener;
    if (paramDataSlotListener != null) {}
    try
    {
      this.mDataSlotListener.add(paramDataSlotListener);
      return;
    }
    finally {}
  }
  
  public void addImsiListener(ImsiListener paramImsiListener)
  {
    List localList = this.mImsiListener;
    if (paramImsiListener != null) {}
    try
    {
      this.mImsiListener.add(paramImsiListener);
      return;
    }
    finally {}
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramFileDescriptor = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
    Object localObject = Settings.Global.getString(MiuiTelephony.sContext.getContentResolver(), "preferred_network_mode");
    paramArrayOfString = new StringBuilder(512);
    paramArrayOfString.append("DefaultSimManager: PREFERRED_NETWORK_MODE init=");
    paramArrayOfString.append(this.mInitModes);
    paramArrayOfString.append(" current=");
    paramArrayOfString.append((String)localObject);
    paramArrayOfString.append(" mIsUsePreferred=");
    paramArrayOfString.append(this.mIsUsePreferred);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(" mPreferredDataSlot=");
    ((StringBuilder)localObject).append(this.mPreferredDataSlot);
    paramArrayOfString.append(((StringBuilder)localObject).toString());
    paramArrayOfString.append(" isUsePreferred=");
    paramArrayOfString.append(paramFileDescriptor.getBoolean("key_is_use_preferred", false));
    paramArrayOfString.append(" preferredDataSlot=");
    paramPrintWriter.println(paramFileDescriptor.getInt("key_preferred_data_slot", SubscriptionManager.INVALID_SLOT_ID));
  }
  
  public String getImsi(int paramInt)
  {
    String str = getRuimImsi(paramInt);
    if (str == null) {
      str = getSimImsi(paramInt);
    }
    return str;
  }
  
  public int getPresentCardCount()
  {
    int i = 0;
    IccCardStatus.CardState[] arrayOfCardState = this.mSimStatus;
    int j = arrayOfCardState.length;
    int k = 0;
    while (k < j)
    {
      int m = i;
      if (arrayOfCardState[k] == IccCardStatus.CardState.CARDSTATE_PRESENT) {
        m = i + 1;
      }
      k++;
      i = m;
    }
    return i;
  }
  
  public String getRuimImsi(int paramInt)
  {
    Object localObject = this.m3gpp2IccRecords;
    if (localObject[paramInt] == null) {
      localObject = null;
    } else {
      localObject = localObject[paramInt].getIMSI();
    }
    return (String)localObject;
  }
  
  public String getSimImsi(int paramInt)
  {
    Object localObject = this.m3gppIccRecords;
    if (localObject[paramInt] == null) {
      localObject = null;
    } else {
      localObject = localObject[paramInt].getIMSI();
    }
    return (String)localObject;
  }
  
  public int getSimInsertStates(int paramInt)
  {
    int[] arrayOfInt = this.mSimInsertStates;
    if (arrayOfInt == null)
    {
      Rlog.d(LOG_TAG, "mSimInsertStates is not initialized,return SIM_NO_CARD");
      return 1;
    }
    return arrayOfInt[paramInt];
  }
  
  int getSystemDefaultSlotId()
  {
    for (int i = 0; i < MiuiTelephony.PHONE_COUNT; i++) {
      if (IccCardStatus.CardState.CARDSTATE_ABSENT != this.mSimStatus[i]) {
        return i;
      }
    }
    return 0;
  }
  
  public boolean isCardPresent(int paramInt)
  {
    boolean bool;
    if (this.mSimStatus[paramInt] == IccCardStatus.CardState.CARDSTATE_PRESENT) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isDataSlotReady()
  {
    return this.mDataSlotReady;
  }
  
  public void notifyDataSlotReady(boolean paramBoolean)
  {
    synchronized (this.mDataSlotListener)
    {
      this.mDataSlotReady = true;
      Iterator localIterator = this.mDataSlotListener.iterator();
      while (localIterator.hasNext()) {
        ((DataSlotListener)localIterator.next()).onDataSlotReady(paramBoolean);
      }
      return;
    }
  }
  
  public void notifyImsiReady(int paramInt)
  {
    synchronized (this.mImsiListener)
    {
      Iterator localIterator = this.mImsiListener.iterator();
      while (localIterator.hasNext()) {
        ((ImsiListener)localIterator.next()).onImsiReady(paramInt);
      }
      return;
    }
  }
  
  void onDefaultDataSubscriptionChanged()
  {
    int i = this.mPropertyDefautlDataSlotId;
    if (i == SubscriptionManager.INVALID_SLOT_ID)
    {
      Rlog.d(LOG_TAG, "onDefaultDataSubscriptionChanged waiting for valid user data slot");
      return;
    }
    List localList = SubscriptionManager.getDefault().getSubscriptionInfoListInternal();
    int j = this.mSc.getDefaultDataSubId();
    int k = SubscriptionManager.INVALID_SLOT_ID;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)localIterator.next();
      if (localSubscriptionInfo.getSubscriptionId() == j) {
        k = localSubscriptionInfo.getSlotId();
      }
    }
    if (k == SubscriptionManager.INVALID_SLOT_ID)
    {
      Rlog.d(LOG_TAG, "onDefaultDataSubscriptionChanged waiting for valid modem data slot");
      return;
    }
    if (k == i)
    {
      if (this.mOldDefaultDataSlotId != k)
      {
        broadcastDataSlotChanged(k);
        this.mOldDefaultDataSlotId = k;
        this.mOldDefaultDataSubId = j;
      }
      else if (this.mOldDefaultDataSubId != j)
      {
        this.mOldDefaultDataSubId = j;
        broadcastDataSlotReady(k, this.mSimInsertStates);
      }
      else
      {
        Rlog.d(LOG_TAG, "onDefaultDataSubscriptionChanged ignore");
      }
    }
    else
    {
      if (this.mHandler.hasMessages(0))
      {
        Rlog.d(LOG_TAG, "onDefaultDataSubscriptionChanged inconsistent and wait for init");
        return;
      }
      if (localList.size() == MiuiTelephony.PHONE_COUNT)
      {
        syncDataSubscription(localList);
      }
      else
      {
        Rlog.d(LOG_TAG, "onDefaultDataSubscriptionChanged delay");
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageAtTime(1, 4000L);
      }
    }
  }
  
  void onInitDefaultSlot()
  {
    if (SystemProperties.getInt("sys.in_shutdown_progress", 0) == 1)
    {
      Rlog.d(LOG_TAG, "this device is being shut down, ignore set sefault data slot.");
      return;
    }
    List localList = SubscriptionManager.getDefault().getSubscriptionInfoListInternal();
    if (!isAllInfoReady(localList))
    {
      onSubscriptionsChanged();
      return;
    }
    if ((localList.size() == 1) && (!((SubscriptionInfo)localList.get(0)).isActivated()))
    {
      this.mLastSubInfos = null;
      new Thread()
      {
        public void run()
        {
          String str = DefaultSimManager.LOG_TAG;
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("single sim card,and the card is deactivated,so activate this card,data slot = ");
          localStringBuilder.append(this.val$dataSlot);
          Rlog.d(str, localStringBuilder.toString());
          IccCardActivateHelper.setIccCardActivate(this.val$dataSlot, true);
        }
      }.start();
      return;
    }
    this.mSimInsertStates = updateNewIccIds(localList, true);
    this.mIsDisableingVirtualSim = false;
    int i = this.mPropertyDefautlVoiceSlotId;
    int j = this.mPropertyDefautlDataSlotId;
    this.mOldDefaultDataSlotId = j;
    int k = getDataSlotForVirtualSim(j, localList);
    if ((SubscriptionManager.isRealSlotId(i)) && (IccCardStatus.CardState.CARDSTATE_ABSENT != this.mSimStatus[i]) && ((MiuiTelephony.PHONE_COUNT <= 1) || (!isDcOnlyVirtualSim(i))))
    {
      j = i;
      if (IccCardActivateHelper.isActivate(i)) {}
    }
    else
    {
      j = SubscriptionManager.INVALID_SLOT_ID;
    }
    i = 0;
    StringBuilder localStringBuilder = new StringBuilder(512);
    Iterator localIterator = localList.iterator();
    SubscriptionInfo localSubscriptionInfo;
    while (localIterator.hasNext())
    {
      localSubscriptionInfo = (SubscriptionInfo)localIterator.next();
      localStringBuilder.append('[');
      localStringBuilder.append(localSubscriptionInfo.getSlotId());
      localStringBuilder.append("]: sub=");
      localStringBuilder.append(localSubscriptionInfo.getSubscriptionId());
      localStringBuilder.append(", iccid=");
      if (PhoneDebug.VDBG) {
        localObject = localSubscriptionInfo.getIccId();
      } else {
        localObject = TelephonyUtils.pii(localSubscriptionInfo.getIccId());
      }
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(", mcc=");
      localStringBuilder.append(localSubscriptionInfo.getMcc());
      localStringBuilder.append(", mnc=");
      localStringBuilder.append(localSubscriptionInfo.getMnc());
      if ((i == 0) && (localSubscriptionInfo.getSlotId() != k)) {
        i = 0;
      } else {
        i = 1;
      }
    }
    if (localList.size() == 1)
    {
      i = ((SubscriptionInfo)localList.get(0)).getSlotId();
    }
    else if (i == 0)
    {
      i = getSystemDefaultSlotId();
      localStringBuilder.append(" system=");
      localStringBuilder.append(i);
    }
    else
    {
      i = k;
      if (!IccCardActivateHelper.isActivate(k))
      {
        localObject = localList.iterator();
        for (;;)
        {
          i = k;
          if (!((Iterator)localObject).hasNext()) {
            break;
          }
          localSubscriptionInfo = (SubscriptionInfo)((Iterator)localObject).next();
          if ((localSubscriptionInfo.getSlotId() != k) && (localSubscriptionInfo.isActivated()))
          {
            i = localSubscriptionInfo.getSlotId();
            localStringBuilder.append(" activated=");
            localStringBuilder.append(i);
            break;
          }
        }
      }
    }
    Object localObject = this.mDefaultSlotSelector;
    k = i;
    if (localObject != null)
    {
      int m = ((DefaultSlotSelector)localObject).getDefaultDataSlot(this.mSimInsertStates, i);
      k = i;
      if (i != m)
      {
        k = i;
        if (SubscriptionManager.isRealSlotId(m))
        {
          k = m;
          localStringBuilder.append(" selected=");
          localStringBuilder.append(k);
        }
      }
    }
    localStringBuilder.append(", vs = ");
    localStringBuilder.append(j);
    localStringBuilder.append(", ds = ");
    localStringBuilder.append(k);
    localStringBuilder.append(", current_ds = ");
    localStringBuilder.append(this.mOldDefaultDataSlotId);
    localStringBuilder.append(" simInsertStates=");
    localStringBuilder.append(simInsertStatesToString(this.mSimInsertStates));
    Rlog.d(LOG_TAG, localStringBuilder.toString());
    try
    {
      initDefaultDataSlotId(k, this.mSimInsertStates);
      setDefaultVoiceSlotId(j, MiuiTelephony.sContext.getOpPackageName());
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void onSubscriptionsChanged()
  {
    Object localObject1 = SubscriptionManager.getDefault().getSubscriptionInfoList();
    int i;
    Object localObject2;
    if (!isAllInfoReady((List)localObject1))
    {
      this.mDataSlotReady = false;
      this.mLastSubInfos = null;
      this.mHandler.removeMessages(0);
      if ((localObject1 == null) || (((List)localObject1).isEmpty())) {
        for (i = 0;; i++)
        {
          localObject1 = this.mSimInsertStatesDone;
          if (i >= localObject1.length) {
            break;
          }
          localObject1[i] = 0;
          localObject2 = LOG_TAG;
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("onSubscriptionsChanged not ready, mSimInsertStatesDone[");
          ((StringBuilder)localObject1).append(i);
          ((StringBuilder)localObject1).append("]=");
          ((StringBuilder)localObject1).append(this.mSimInsertStatesDone[i]);
          Rlog.d((String)localObject2, ((StringBuilder)localObject1).toString());
        }
      }
    }
    else
    {
      if (isSameWithBefore((List)localObject1)) {
        return;
      }
      String str;
      if (((List)localObject1).size() == 1)
      {
        Rlog.d(LOG_TAG, "onSubscriptionsChanged for one sub");
        this.mSc.notifySubInfoReady();
        for (i = 0; i < this.mSimInsertStatesDone.length; i++) {
          if (((SubscriptionInfo)((List)localObject1).get(0)).getSlotId() != i)
          {
            this.mSimInsertStatesDone[i] = false;
            str = LOG_TAG;
            localObject2 = new StringBuilder();
            ((StringBuilder)localObject2).append("onSubscriptionsChanged for one sub, mSimInsertStatesDone[");
            ((StringBuilder)localObject2).append(i);
            ((StringBuilder)localObject2).append("]=");
            ((StringBuilder)localObject2).append(this.mSimInsertStatesDone[i]);
            Rlog.d(str, ((StringBuilder)localObject2).toString());
          }
        }
        this.mLastSubInfos = ((List)localObject1);
        this.mSimInsertStates = updateNewIccIds((List)localObject1, false);
        this.mIsDisableingVirtualSim = VirtualSimUtils.getInstance().isDisablingVirtualSim();
        this.mHandler.sendEmptyMessageDelayed(0, 4000L);
      }
      else
      {
        str = LOG_TAG;
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("onSubscriptionsChanged update subSize=");
        ((StringBuilder)localObject2).append(((List)localObject1).size());
        Rlog.d(str, ((StringBuilder)localObject2).toString());
        this.mLastSubInfos = ((List)localObject1);
        this.mSimInsertStates = updateNewIccIds((List)localObject1, true);
        this.mHandler.sendEmptyMessage(0);
      }
    }
  }
  
  public void onVirtualSimPreciseStateChanged()
  {
    if (VirtualSimUtils.getInstance().isDisablingVirtualSim()) {
      storeValues("key_is_use_preferred", true, null, 0);
    }
  }
  
  public void onVirtualSimStateChanged()
  {
    if (VirtualSimUtils.getInstance().isVirtualSimEnabled()) {
      storeValues("key_is_use_preferred", false, "key_preferred_data_slot", SystemProperties.getInt("persist.radio.default.data", SubscriptionManager.INVALID_SLOT_ID));
    }
  }
  
  public void removeDataSlotListener(DataSlotListener paramDataSlotListener)
  {
    List localList = this.mDataSlotListener;
    if (paramDataSlotListener != null) {}
    try
    {
      this.mDataSlotListener.remove(paramDataSlotListener);
      return;
    }
    finally {}
  }
  
  public void removeImsiListener(ImsiListener paramImsiListener)
  {
    List localList = this.mImsiListener;
    if (paramImsiListener != null) {}
    try
    {
      this.mImsiListener.remove(paramImsiListener);
      return;
    }
    finally {}
  }
  
  boolean setDefaultDataSlotId(int paramInt, String paramString)
    throws RemoteException
  {
    String str = LOG_TAG;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<");
    localStringBuilder.append(paramString);
    localStringBuilder.append("> is trying to set default data slot to ");
    localStringBuilder.append(paramInt);
    Rlog.d(str, localStringBuilder.toString());
    MiuiTelephony.sContext.enforceCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE", null);
    if (SubscriptionManager.isRealSlotId(paramInt))
    {
      this.mPropertyDefautlDataSlotId = paramInt;
      SystemProperties.set("persist.radio.default.data", String.valueOf(paramInt));
      storeValues("key_is_use_preferred", VirtualSimUtils.getInstance().isVirtualSimEnabled(), "key_preferred_data_slot", paramInt);
      int i = this.mSc.getSubIdUsingPhoneId(paramInt);
      if (this.mSc.getDefaultDataSubId() != i)
      {
        this.mSc.setDefaultDataSubId(i);
      }
      else if (this.mOldDefaultDataSlotId != paramInt)
      {
        broadcastDataSlotChanged(paramInt);
        this.mOldDefaultDataSlotId = paramInt;
      }
      return true;
    }
    return false;
  }
  
  public void setDefaultSlotSelector(DefaultSlotSelector paramDefaultSlotSelector)
  {
    this.mDefaultSlotSelector = paramDefaultSlotSelector;
  }
  
  void setDefaultVoiceSlotId(int paramInt, String paramString)
    throws RemoteException
  {
    String str = LOG_TAG;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("<");
    localStringBuilder.append(paramString);
    localStringBuilder.append("> is trying to set default voice slot to ");
    localStringBuilder.append(paramInt);
    Rlog.d(str, localStringBuilder.toString());
    MiuiTelephony.sContext.enforceCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE", null);
    if (paramInt != SubscriptionManager.DEFAULT_SLOT_ID)
    {
      if (!SubscriptionManager.isRealSlotId(paramInt)) {
        paramInt = SubscriptionManager.INVALID_SLOT_ID;
      }
      this.mPropertyDefautlVoiceSlotId = paramInt;
      SystemProperties.set("persist.radio.default.voice", String.valueOf(this.mPropertyDefautlVoiceSlotId));
      if (this.mPropertyDefautlVoiceSlotId == SubscriptionManager.INVALID_SLOT_ID)
      {
        if (SubscriptionManager.isValidSubscriptionId(this.mSc.getDefaultVoiceSubId())) {
          this.mSc.setDefaultVoiceSubId(SubscriptionManager.INVALID_SUBSCRIPTION_ID);
        }
      }
      else
      {
        paramInt = this.mSc.getSubIdUsingPhoneId(this.mPropertyDefautlVoiceSlotId);
        if (this.mSc.getDefaultVoiceSubId() != paramInt) {
          this.mSc.setDefaultVoiceSubId(paramInt);
        }
      }
      syncPhoneAccount();
    }
  }
  
  public void syncPhoneAccount()
  {
    Object localObject1 = TelecomManager.from(MiuiTelephony.sContext);
    Object localObject2 = LOG_TAG;
    Object localObject3 = new StringBuilder();
    ((StringBuilder)localObject3).append("syncPhoneAccount with voice slot = ");
    ((StringBuilder)localObject3).append(this.mPropertyDefautlVoiceSlotId);
    Rlog.d((String)localObject2, ((StringBuilder)localObject3).toString());
    localObject3 = ((TelecomManager)localObject1).getUserSelectedOutgoingPhoneAccount();
    if (this.mPropertyDefautlVoiceSlotId == SubscriptionManager.INVALID_SLOT_ID)
    {
      if ((localObject3 != null) && (((PhoneAccountHandle)localObject3).getComponentName().getClassName().equals("com.android.services.telephony.TelephonyConnectionService")))
      {
        ((TelecomManager)localObject1).setUserSelectedOutgoingPhoneAccount(null);
        Rlog.d(LOG_TAG, "syncPhoneAccount clear default phone account");
      }
    }
    else
    {
      localObject2 = getPhoneAccountHandle(this.mPropertyDefautlVoiceSlotId, MiuiTelephony.sContext);
      if ((localObject2 != null) && ((localObject3 == null) || (!((PhoneAccountHandle)localObject3).equals(localObject2))))
      {
        ((TelecomManager)localObject1).setUserSelectedOutgoingPhoneAccount((PhoneAccountHandle)localObject2);
        localObject3 = LOG_TAG;
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("syncPhoneAccount set default phone account ");
        ((StringBuilder)localObject1).append(localObject2);
        Rlog.d((String)localObject3, ((StringBuilder)localObject1).toString());
      }
    }
  }
  
  public static abstract interface DataSlotListener
  {
    public abstract void onDataSlotReady(boolean paramBoolean);
  }
  
  public static abstract interface ImsiListener
  {
    public abstract void onImsiReady(int paramInt);
  }
  
  private class TelephonyBroadcastReceiver
    extends BroadcastReceiver
  {
    private TelephonyBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED".equals(paramIntent.getAction())) {
        DefaultSimManager.this.onDefaultDataSubscriptionChanged();
      } else if ("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED".equals(paramIntent.getAction())) {
        DefaultSimManager.this.onDefaultVoiceSubscriptionChanged();
      }
    }
    
    void register(Context paramContext)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
      localIntentFilter.addAction("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
      paramContext.registerReceiver(this, localIntentFilter);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/DefaultSimManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */