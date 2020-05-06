package miui.telephony;

import android.app.AppOpsManager;
import android.app.MobileDataUtils;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.telephony.CellLocation;
import android.telephony.Rlog;
import com.android.internal.os.BackgroundThread;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.ITelephonyRegistry.Stub;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.IccCardActivateHelper;
import com.android.internal.telephony.MiuiIccPhoneBookInterfaceManager;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.UiccController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import miui.os.Build;
import miui.os.SystemProperties;
import miui.util.FeatureParser;

public class MiuiTelephony
  extends MiuiTelephonyBase
{
  private static final int EVENT_SET_CALL_FORWARD_DONE = 1;
  public static final boolean IS_MTK;
  public static final boolean IS_PINECONE = "pinecone".equals(FeatureParser.getString("vendor"));
  public static final boolean IS_QCOM;
  private static String LOG_TAG = "MiuiTelephony";
  public static final int PHONE_COUNT = TelephonyManager.getDefault().getPhoneCount();
  static Context sContext;
  private static MiuiTelephony sInstance;
  AppOpsManager mAppOps = (AppOpsManager)sContext.getSystemService(AppOpsManager.class);
  private MiuiTelephonyHandler mHandler;
  private ImeiMeidSource mImeiMeidSource;
  private UiccController mUiccController;
  
  static
  {
    IS_QCOM = "qcom".equals(FeatureParser.getString("vendor"));
    boolean bool;
    if ((!"mediatek".equals(FeatureParser.getString("vendor"))) && (!"atom".equals(Build.DEVICE)) && (!"pre_bomb".equals(Build.DEVICE)) && (!"bomb".equals(Build.DEVICE))) {
      bool = false;
    } else {
      bool = true;
    }
    IS_MTK = bool;
  }
  
  private MiuiTelephony()
  {
    DefaultSimManager.init();
    MiuiAisSimLockManager.init(sContext);
    this.mImeiMeidSource = new ImeiMeidSource();
    this.mHandler = new MiuiTelephonyHandler(BackgroundThread.get().getLooper());
    this.mUiccController = UiccController.getInstance();
  }
  
  /* Error */
  public static boolean checkIfCallerIsSelfOrForegroundUser()
  {
    // Byte code:
    //   0: invokestatic 149	android/os/Binder:getCallingUid	()I
    //   3: istore_0
    //   4: invokestatic 154	android/os/Process:myUid	()I
    //   7: istore_1
    //   8: iconst_1
    //   9: istore_2
    //   10: iload_0
    //   11: iload_1
    //   12: if_icmpne +8 -> 20
    //   15: iconst_1
    //   16: istore_3
    //   17: goto +5 -> 22
    //   20: iconst_0
    //   21: istore_3
    //   22: iload_3
    //   23: istore 4
    //   25: iload_3
    //   26: ifne +99 -> 125
    //   29: invokestatic 159	android/os/UserHandle:getCallingUserId	()I
    //   32: istore_0
    //   33: invokestatic 163	android/os/Binder:clearCallingIdentity	()J
    //   36: lstore 5
    //   38: invokestatic 168	android/app/ActivityManager:getCurrentUser	()I
    //   41: istore_1
    //   42: iload_1
    //   43: iload_0
    //   44: if_icmpne +8 -> 52
    //   47: iload_2
    //   48: istore_3
    //   49: goto +5 -> 54
    //   52: iconst_0
    //   53: istore_3
    //   54: lload 5
    //   56: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   59: iload_3
    //   60: istore 4
    //   62: goto +63 -> 125
    //   65: astore 7
    //   67: goto +50 -> 117
    //   70: astore 8
    //   72: getstatic 35	miui/telephony/MiuiTelephony:LOG_TAG	Ljava/lang/String;
    //   75: astore 7
    //   77: new 174	java/lang/StringBuilder
    //   80: astore 9
    //   82: aload 9
    //   84: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   87: aload 9
    //   89: ldc -79
    //   91: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   94: pop
    //   95: aload 9
    //   97: aload 8
    //   99: invokevirtual 184	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   102: pop
    //   103: aload 7
    //   105: aload 9
    //   107: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   110: invokestatic 193	android/telephony/Rlog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   113: pop
    //   114: goto -60 -> 54
    //   117: lload 5
    //   119: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   122: aload 7
    //   124: athrow
    //   125: getstatic 35	miui/telephony/MiuiTelephony:LOG_TAG	Ljava/lang/String;
    //   128: astore 9
    //   130: new 174	java/lang/StringBuilder
    //   133: dup
    //   134: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   137: astore 7
    //   139: aload 7
    //   141: ldc -61
    //   143: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: pop
    //   147: aload 7
    //   149: iload 4
    //   151: invokevirtual 198	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   154: pop
    //   155: aload 9
    //   157: aload 7
    //   159: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   162: invokestatic 201	android/telephony/Rlog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   165: pop
    //   166: iload 4
    //   168: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	42	0	i	int
    //   7	38	1	j	int
    //   9	39	2	bool1	boolean
    //   16	44	3	bool2	boolean
    //   23	144	4	bool3	boolean
    //   36	82	5	l	long
    //   65	1	7	localObject1	Object
    //   75	83	7	localObject2	Object
    //   70	28	8	localException	Exception
    //   80	76	9	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   38	42	65	finally
    //   72	114	65	finally
    //   38	42	70	java/lang/Exception
  }
  
  public static MiuiTelephony getInstance()
  {
    return sInstance;
  }
  
  public static int getPhoneCount()
  {
    return PHONE_COUNT;
  }
  
  public static void init(Context paramContext)
  {
    sContext = paramContext;
    sInstance = new MiuiTelephony();
    try
    {
      ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry")).setMiuiTelephony(sInstance);
    }
    catch (RemoteException paramContext)
    {
      Rlog.e(LOG_TAG, "setMiuiTelephony error", paramContext);
    }
  }
  
  private boolean isUtNotAllowedAsDataDisabled(Phone paramPhone)
  {
    if ((paramPhone != null) && (paramPhone.getIccCard() != null) && (paramPhone.getIccCard().getIccRecords() != null)) {
      return (ServiceProviderUtils.isChineseServiceProvider(paramPhone.getIccCard().getIccRecords().getOperatorNumeric())) && ((paramPhone.isImsRegistered()) || (paramPhone.isUtEnabled())) && (!MobileDataUtils.getInstance().isMobileEnable(sContext));
    }
    return false;
  }
  
  public boolean canReadPhoneState(String paramString1, String paramString2)
  {
    boolean bool = true;
    try
    {
      sContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", paramString2);
      return true;
    }
    catch (SecurityException localSecurityException)
    {
      sContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", paramString2);
      if (this.mAppOps.noteOp(51, Binder.getCallingUid(), paramString1) != 0) {
        bool = false;
      }
    }
    return bool;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.println("MiuiTelephony:");
    this.mImeiMeidSource.dump(paramPrintWriter);
    DefaultSimManager.getInstance().dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    for (Phone localPhone : PhoneFactory.getPhones())
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Phone[");
      localStringBuilder.append(localPhone.getPhoneId());
      localStringBuilder.append("]:");
      paramPrintWriter.println(localStringBuilder.toString());
      if (localPhone.getMiuiIccPhoneBookInterfaceManager() != null) {
        localPhone.getMiuiIccPhoneBookInterfaceManager().dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      }
    }
  }
  
  public void enforceModifyPermission()
  {
    if (TelephonyManager.checkCallingOrSelfPermissionGranted(Binder.getCallingUid())) {
      return;
    }
    sContext.enforceCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE", null);
  }
  
  public Bundle getCellLocationForSlot(int paramInt, String paramString)
  {
    try
    {
      sContext.enforceCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION", null);
    }
    catch (SecurityException localSecurityException)
    {
      sContext.enforceCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION", "getCellLocationForSlot");
    }
    if (this.mAppOps.noteOp(0, Binder.getCallingUid(), paramString) != 0) {
      return null;
    }
    if (checkIfCallerIsSelfOrForegroundUser())
    {
      paramString = PhoneFactory.getPhone(paramInt);
      if (paramString != null)
      {
        Bundle localBundle = new Bundle();
        paramString.getCellLocation().fillInNotifierBundle(localBundle);
        return localBundle;
      }
    }
    return null;
  }
  
  public String getDeviceId(String paramString)
  {
    return this.mImeiMeidSource.getDeviceId(paramString);
  }
  
  public List<String> getDeviceIdList(String paramString)
  {
    return this.mImeiMeidSource.getDeviceIdList(paramString);
  }
  
  public String getImei(int paramInt)
  {
    return this.mImeiMeidSource.getImei(paramInt);
  }
  
  public String getImei(int paramInt, String paramString)
  {
    return this.mImeiMeidSource.getImei(paramInt, paramString);
  }
  
  public List<String> getImeiList(String paramString)
  {
    return this.mImeiMeidSource.getImeiList(paramString);
  }
  
  public String getMeid(int paramInt)
  {
    return this.mImeiMeidSource.getMeid(paramInt);
  }
  
  public String getMeid(int paramInt, String paramString)
  {
    return this.mImeiMeidSource.getMeid(paramInt, paramString);
  }
  
  public List<String> getMeidList(String paramString)
  {
    return this.mImeiMeidSource.getMeidList(paramString);
  }
  
  public String getSmallDeviceId(String paramString)
  {
    return this.mImeiMeidSource.getSmallDeviceId(paramString);
  }
  
  public String getSpn(String paramString1, int paramInt, String paramString2, boolean paramBoolean)
  {
    IccRecords localIccRecords = this.mUiccController.getIccRecords(paramInt, 2);
    String str = paramString1;
    if (localIccRecords != null) {
      if (!ServiceProviderUtils.isChinaTelecom(localIccRecords.getOperatorNumeric()))
      {
        str = paramString1;
        if (!ServiceProviderUtils.isMacauTelecom(localIccRecords.getOperatorNumeric())) {}
      }
      else
      {
        str = localIccRecords.getOperatorNumeric();
      }
    }
    return ServiceProviderUtils.get(str, paramInt, paramString2, paramBoolean);
  }
  
  public int getSystemDefaultSlotId()
  {
    return DefaultSimManager.getInstance().getSystemDefaultSlotId();
  }
  
  public boolean isFiveGCapable()
  {
    return ReflectBuilderUtil.ReflAgent.getFiveGManagerClass(sContext).callStatic("isFiveGCapable", null, new Object[0]).booleanResult();
  }
  
  public boolean isGwsdSupport()
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (SystemProperties.getInt("ro.vendor.mtk_gwsd_support", 0) == 1)
    {
      bool2 = bool1;
      if (!Build.IS_INTERNATIONAL_BUILD) {
        bool2 = true;
      }
    }
    return bool2;
  }
  
  public boolean isIccCardActivate(int paramInt)
  {
    return IccCardActivateHelper.isActivate(paramInt);
  }
  
  public boolean isImsRegistered(int paramInt)
  {
    boolean bool1 = false;
    try
    {
      localObject = PhoneFactory.getPhone(paramInt);
      if (localObject != null)
      {
        boolean bool2 = bool1;
        if (((Phone)localObject).isImsRegistered())
        {
          boolean bool3 = ((Phone)localObject).isVolteEnabled();
          bool2 = bool1;
          if (bool3) {
            bool2 = true;
          }
        }
        return bool2;
      }
    }
    catch (Exception localException)
    {
      Object localObject = LOG_TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("isImsRegistered exception=");
      localStringBuilder.append(localException);
      Rlog.d((String)localObject, localStringBuilder.toString());
    }
    return false;
  }
  
  public boolean isSameOperator(String paramString1, String paramString2)
  {
    return ServiceProviderUtils.isSameServiceProvider(paramString1, paramString2);
  }
  
  public boolean isUserFiveGEnabled()
  {
    return ReflectBuilderUtil.ReflAgent.getFiveGManagerClass(sContext).callStatic("getInstance", null, new Object[0]).setResultToSelf().call("isUserFiveGEnabled", null, null).booleanResult();
  }
  
  public boolean isVideoTelephonyAvailable(int paramInt)
  {
    try
    {
      Phone localPhone = PhoneFactory.getPhone(paramInt);
      if (localPhone != null)
      {
        boolean bool = localPhone.isVideoEnabled();
        return bool;
      }
    }
    catch (Exception localException)
    {
      String str = LOG_TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("isVideoTelephonyAvailable exception=");
      localStringBuilder.append(localException);
      Rlog.d(str, localStringBuilder.toString());
    }
    return false;
  }
  
  public boolean isVolteEnabledByPlatform()
  {
    try
    {
      if (TelephonyManager.getDefault().isDualVolteSupported())
      {
        for (i = 0; i < PHONE_COUNT; i++) {
          if (isVolteEnabledByPlatformForSlot(i)) {
            return true;
          }
        }
        return false;
      }
      int i = DefaultSimManager.getDefaultDataSlotId();
      if (!SubscriptionManager.isRealSlotId(i)) {
        return false;
      }
      boolean bool = isVolteEnabledByPlatformForSlot(i);
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.d(LOG_TAG, "isVolteEnabledByPlatform exception", localException);
    }
    return false;
  }
  
  /* Error */
  public boolean isVolteEnabledByPlatformForSlot(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 163	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_2
    //   4: getstatic 90	miui/telephony/MiuiTelephony:sContext	Landroid/content/Context;
    //   7: iload_1
    //   8: invokestatic 492	com/android/ims/ImsManager:getInstance	(Landroid/content/Context;I)Lcom/android/ims/ImsManager;
    //   11: invokevirtual 494	com/android/ims/ImsManager:isVolteEnabledByPlatform	()Z
    //   14: istore 4
    //   16: lload_2
    //   17: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   20: iload 4
    //   22: ireturn
    //   23: astore 5
    //   25: goto +23 -> 48
    //   28: astore 5
    //   30: getstatic 35	miui/telephony/MiuiTelephony:LOG_TAG	Ljava/lang/String;
    //   33: ldc_w 496
    //   36: aload 5
    //   38: invokestatic 487	android/telephony/Rlog:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   41: pop
    //   42: lload_2
    //   43: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   46: iconst_0
    //   47: ireturn
    //   48: lload_2
    //   49: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   52: aload 5
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	MiuiTelephony
    //   0	55	1	paramInt	int
    //   3	46	2	l	long
    //   14	7	4	bool	boolean
    //   23	1	5	localObject	Object
    //   28	25	5	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   4	16	23	finally
    //   30	42	23	finally
    //   4	16	28	java/lang/Exception
  }
  
  public boolean isVolteEnabledByUser()
  {
    try
    {
      if (TelephonyManager.getDefault().isDualVolteSupported())
      {
        for (i = 0; i < PHONE_COUNT; i++) {
          if (isVolteEnabledByUserForSlot(i)) {
            return true;
          }
        }
        return false;
      }
      int i = DefaultSimManager.getDefaultDataSlotId();
      if (!SubscriptionManager.isRealSlotId(i)) {
        return false;
      }
      boolean bool = isVolteEnabledByUserForSlot(i);
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.d(LOG_TAG, "isVolteEnabledByUser exception", localException);
    }
    return false;
  }
  
  /* Error */
  public boolean isVolteEnabledByUserForSlot(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 163	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_2
    //   4: getstatic 90	miui/telephony/MiuiTelephony:sContext	Landroid/content/Context;
    //   7: iload_1
    //   8: invokestatic 492	com/android/ims/ImsManager:getInstance	(Landroid/content/Context;I)Lcom/android/ims/ImsManager;
    //   11: invokevirtual 505	com/android/ims/ImsManager:isEnhanced4gLteModeSettingEnabledByUser	()Z
    //   14: istore 4
    //   16: lload_2
    //   17: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   20: iload 4
    //   22: ireturn
    //   23: astore 5
    //   25: goto +23 -> 48
    //   28: astore 5
    //   30: getstatic 35	miui/telephony/MiuiTelephony:LOG_TAG	Ljava/lang/String;
    //   33: ldc_w 507
    //   36: aload 5
    //   38: invokestatic 487	android/telephony/Rlog:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   41: pop
    //   42: lload_2
    //   43: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   46: iconst_0
    //   47: ireturn
    //   48: lload_2
    //   49: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   52: aload 5
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	MiuiTelephony
    //   0	55	1	paramInt	int
    //   3	46	2	l	long
    //   14	7	4	bool	boolean
    //   23	1	5	localObject	Object
    //   28	25	5	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   4	16	23	finally
    //   30	42	23	finally
    //   4	16	28	java/lang/Exception
  }
  
  public boolean isVtEnabledByPlatform()
  {
    try
    {
      if (TelephonyManager.getDefault().isDualVolteSupported())
      {
        for (i = 0; i < PHONE_COUNT; i++) {
          if (isVtEnabledByPlatformForSlot(i)) {
            return true;
          }
        }
        return false;
      }
      int i = DefaultSimManager.getDefaultDataSlotId();
      if (!SubscriptionManager.isRealSlotId(i)) {
        return false;
      }
      boolean bool = isVtEnabledByPlatformForSlot(i);
      return bool;
    }
    catch (Exception localException)
    {
      Rlog.d(LOG_TAG, "isVtEnabledByPlatform exception", localException);
    }
    return false;
  }
  
  /* Error */
  public boolean isVtEnabledByPlatformForSlot(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 163	android/os/Binder:clearCallingIdentity	()J
    //   3: lstore_2
    //   4: getstatic 90	miui/telephony/MiuiTelephony:sContext	Landroid/content/Context;
    //   7: iload_1
    //   8: invokestatic 492	com/android/ims/ImsManager:getInstance	(Landroid/content/Context;I)Lcom/android/ims/ImsManager;
    //   11: invokevirtual 515	com/android/ims/ImsManager:isVtEnabledByPlatform	()Z
    //   14: istore 4
    //   16: lload_2
    //   17: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   20: iload 4
    //   22: ireturn
    //   23: astore 5
    //   25: goto +23 -> 48
    //   28: astore 5
    //   30: getstatic 35	miui/telephony/MiuiTelephony:LOG_TAG	Ljava/lang/String;
    //   33: ldc_w 517
    //   36: aload 5
    //   38: invokestatic 487	android/telephony/Rlog:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   41: pop
    //   42: lload_2
    //   43: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   46: iconst_0
    //   47: ireturn
    //   48: lload_2
    //   49: invokestatic 172	android/os/Binder:restoreCallingIdentity	(J)V
    //   52: aload 5
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	MiuiTelephony
    //   0	55	1	paramInt	int
    //   3	46	2	l	long
    //   14	7	4	bool	boolean
    //   23	1	5	localObject	Object
    //   28	25	5	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   4	16	23	finally
    //   30	42	23	finally
    //   4	16	28	java/lang/Exception
  }
  
  public boolean isWifiCallingAvailable(int paramInt)
  {
    try
    {
      localObject = PhoneFactory.getPhone(paramInt);
      if (localObject != null)
      {
        boolean bool = ((Phone)localObject).isWifiCallingEnabled();
        return bool;
      }
    }
    catch (Exception localException)
    {
      String str = LOG_TAG;
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("isWifiCallingAvailable exception=");
      ((StringBuilder)localObject).append(localException);
      Rlog.d(str, ((StringBuilder)localObject).toString());
    }
    return false;
  }
  
  public String onOperatorNumericOrNameSet(int paramInt, String paramString1, String paramString2)
  {
    return ServiceProviderUtils.onOperatorNumericOrNameSet(paramInt, paramString1, paramString2);
  }
  
  public void setCallForwardingOption(int paramInt1, int paramInt2, int paramInt3, String paramString, ResultReceiver paramResultReceiver)
  {
    enforceModifyPermission();
    Phone localPhone = PhoneFactory.getPhone(paramInt1);
    if (isUtNotAllowedAsDataDisabled(localPhone))
    {
      paramResultReceiver.send(-3, null);
      return;
    }
    if ((localPhone.getPhoneType() == 2) && (!localPhone.isUtEnabled()))
    {
      paramResultReceiver.send(-2, null);
      return;
    }
    localPhone.setCallForwardingOption(paramInt2, paramInt3, paramString, 0, this.mHandler.obtainMessage(1, paramResultReceiver));
  }
  
  public boolean setDefaultDataSlotId(int paramInt, String paramString)
    throws RemoteException
  {
    return DefaultSimManager.getInstance().setDefaultDataSlotId(paramInt, paramString);
  }
  
  public void setDefaultVoiceSlotId(int paramInt, String paramString)
    throws RemoteException
  {
    DefaultSimManager.getInstance().setDefaultVoiceSlotId(paramInt, paramString);
  }
  
  public void setIccCardActivate(int paramInt, boolean paramBoolean)
  {
    IccCardActivateHelper.setIccCardActivate(paramInt, paramBoolean);
  }
  
  public void setUserFiveGEnabled(boolean paramBoolean)
  {
    ReflectBuilderUtil.ReflAgent.getFiveGManagerClass(sContext).callStatic("getInstance", null, new Object[0]).setResultToSelf().call("setUserFiveGEnabled", new Class[] { Boolean.TYPE }, new Object[] { Boolean.valueOf(paramBoolean) });
  }
  
  private class MiuiTelephonyHandler
    extends Handler
  {
    public MiuiTelephonyHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject = MiuiTelephony.LOG_TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Received message:");
      localStringBuilder.append(paramMessage.what);
      Rlog.d((String)localObject, localStringBuilder.toString());
      if (paramMessage.what == 1)
      {
        if (!(paramMessage.obj instanceof AsyncResult)) {
          return;
        }
        paramMessage = (AsyncResult)paramMessage.obj;
        localObject = (ResultReceiver)paramMessage.userObj;
        int i;
        if (paramMessage.exception != null) {
          i = -1;
        } else {
          i = 0;
        }
        if (localObject != null) {
          ((ResultReceiver)localObject).send(i, null);
        }
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/MiuiTelephony.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */