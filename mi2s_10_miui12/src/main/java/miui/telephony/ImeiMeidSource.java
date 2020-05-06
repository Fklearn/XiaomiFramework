package miui.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.telephony.Rlog;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.TelephonyPermissions;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import miui.os.Build;

public class ImeiMeidSource
{
  private static final int DELAY_TIME = 2000;
  private static final String DEVICE_ID_KEY = "device_id_key";
  private static final int EVENT_GET_DEVICE_IDENTITY_DONE_BASE = 300;
  private static final int EVENT_GET_DEVICE_IDENTITY_RETRY_BASE = 500;
  private static final int EVENT_GET_IMEI_DONE_BASE = 200;
  private static final int EVENT_GET_IMEI_RETRY_BASE = 400;
  private static final int EVENT_RADIO_AVAILABLE_BASE = 100;
  private static final String IMEI_KEY_PREFIX = "key_imei_slot";
  private static String LOG_TAG = "ImeiMeidSource";
  private static final String MEID_KEY_PREFIX = "key_meid_slot";
  private static final int RETRY_MAX_TIMES = 10;
  private static final int SLOT_1 = 0;
  private static final int SLOT_2 = 1;
  private static final String SMALL_DEVICE_ID_KEY = "small_device_id_key";
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      Object localObject = (AsyncResult)paramAnonymousMessage.obj;
      int i = paramAnonymousMessage.what;
      if ((i != 100) && (i != 101))
      {
        int j;
        if ((i != 200) && (i != 201))
        {
          if ((i != 300) && (i != 301))
          {
            if ((i != 400) && (i != 401))
            {
              if ((i == 500) || (i == 501))
              {
                i = paramAnonymousMessage.what - 500;
                if (ImeiMeidSource.this.mReadMeidException[i] == null) {
                  ImeiMeidSource.this.mReadMeidException[i] = new Throwable();
                }
                ImeiMeidSource.getCommandsInterface(PhoneFactory.getPhone(i)).getDeviceIdentity(obtainMessage(i + 300, paramAnonymousMessage.arg1, 0));
              }
            }
            else
            {
              i = paramAnonymousMessage.what - 400;
              if (ImeiMeidSource.this.mReadImeiException[i] == null) {
                ImeiMeidSource.this.mReadImeiException[i] = new Throwable();
              }
              ImeiMeidSource.getCommandsInterface(PhoneFactory.getPhone(i)).getIMEI(obtainMessage(i + 200, paramAnonymousMessage.arg1, 0));
            }
          }
          else
          {
            j = paramAnonymousMessage.what - 300;
            ImeiMeidSource.this.mReadMeidException[j] = ((AsyncResult)localObject).exception;
            if (((AsyncResult)localObject).exception != null)
            {
              ImeiMeidSource.this.mMeids[j] = null;
              i = paramAnonymousMessage.arg1;
              if (i <= 10)
              {
                ImeiMeidSource.this.mHandler.removeMessages(j + 500);
                paramAnonymousMessage = obtainMessage(j + 500, i + 1, 0);
                ImeiMeidSource.this.mHandler.sendMessageDelayed(paramAnonymousMessage, 2000L);
              }
            }
            else
            {
              paramAnonymousMessage = (String[])((AsyncResult)localObject).result;
              if (!TextUtils.isEmpty(paramAnonymousMessage[0]))
              {
                ImeiMeidSource.this.mImeis[j] = paramAnonymousMessage[0];
                ImeiMeidSource.this.mReadImeiException[j] = ((AsyncResult)localObject).exception;
              }
              ImeiMeidSource.this.mMeids[j] = paramAnonymousMessage[3];
              if ((ImeiMeidSource.this.mMeids[j] != null) && (ImeiMeidSource.this.mMeids[j].matches("^0*$")))
              {
                if ((!TelephonyManager.isCustSingleSimDevice()) || (j != 1))
                {
                  paramAnonymousMessage = ImeiMeidSource.LOG_TAG;
                  localObject = new StringBuilder();
                  ((StringBuilder)localObject).append("invalid meid=");
                  ((StringBuilder)localObject).append(ImeiMeidSource.this.mMeids[j]);
                  ((StringBuilder)localObject).append(" slot=");
                  ((StringBuilder)localObject).append(j);
                  Rlog.d(paramAnonymousMessage, ((StringBuilder)localObject).toString());
                }
                ImeiMeidSource.this.mMeids[j] = null;
              }
              if ((!TelephonyManager.isCustSingleSimDevice()) || (j != 1))
              {
                paramAnonymousMessage = ImeiMeidSource.LOG_TAG;
                localObject = new StringBuilder();
                ((StringBuilder)localObject).append("slot=");
                ((StringBuilder)localObject).append(j);
                ((StringBuilder)localObject).append(" imei=");
                ((StringBuilder)localObject).append(PhoneNumberUtils.toLogSafePhoneNumber(ImeiMeidSource.this.mImeis[j], 3));
                ((StringBuilder)localObject).append(" meid=");
                ((StringBuilder)localObject).append(PhoneNumberUtils.toLogSafePhoneNumber(ImeiMeidSource.this.mMeids[j], 3));
                Rlog.d(paramAnonymousMessage, ((StringBuilder)localObject).toString());
              }
              ImeiMeidSource.this.onDeviceIdLoaded();
            }
          }
        }
        else
        {
          j = paramAnonymousMessage.what - 200;
          ImeiMeidSource.this.mReadImeiException[j] = ((AsyncResult)localObject).exception;
          if ((((AsyncResult)localObject).exception == null) && (((AsyncResult)localObject).result != null) && (!TextUtils.isEmpty((String)((AsyncResult)localObject).result)))
          {
            ImeiMeidSource.this.mImeis[j] = ((String)((AsyncResult)localObject).result);
            if ((!TelephonyManager.isCustSingleSimDevice()) || (j != 1))
            {
              paramAnonymousMessage = ImeiMeidSource.LOG_TAG;
              localObject = new StringBuilder();
              ((StringBuilder)localObject).append("slot=");
              ((StringBuilder)localObject).append(j);
              ((StringBuilder)localObject).append(" imei=");
              ((StringBuilder)localObject).append(PhoneNumberUtils.toLogSafePhoneNumber(ImeiMeidSource.this.mImeis[j], 3));
              Rlog.d(paramAnonymousMessage, ((StringBuilder)localObject).toString());
            }
            ImeiMeidSource.this.onDeviceIdLoaded();
          }
          else
          {
            ImeiMeidSource.this.mImeis[j] = null;
            i = paramAnonymousMessage.arg1;
            if (i <= 10)
            {
              ImeiMeidSource.this.mHandler.removeMessages(j + 400);
              paramAnonymousMessage = obtainMessage(j + 400, i + 1, 0);
              ImeiMeidSource.this.mHandler.sendMessageDelayed(paramAnonymousMessage, 2000L);
            }
          }
        }
      }
      else
      {
        i = paramAnonymousMessage.what - 100;
        ImeiMeidSource.this.mImeis[i] = null;
        ImeiMeidSource.this.mMeids[i] = null;
        if (ImeiMeidSource.this.mReadImeiException[i] == null) {
          ImeiMeidSource.this.mReadImeiException[i] = new Throwable("init");
        }
        if (ImeiMeidSource.this.mReadMeidException[i] == null) {
          ImeiMeidSource.this.mReadMeidException[i] = new Throwable("init");
        }
        ImeiMeidSource.getCommandsInterface(PhoneFactory.getPhone(i)).getDeviceIdentity(obtainMessage(i + 300, 0, 0));
      }
    }
  };
  private String[] mImeis;
  private String[] mMeids;
  private Throwable[] mReadImeiException;
  private Throwable[] mReadMeidException;
  
  ImeiMeidSource()
  {
    Object localObject = PhoneFactory.getPhones();
    this.mImeis = new String[localObject.length];
    this.mMeids = new String[localObject.length];
    this.mReadImeiException = new Throwable[localObject.length];
    this.mReadMeidException = new Throwable[localObject.length];
    for (int i = 0; i < localObject.length; i++)
    {
      this.mReadImeiException[i] = new Throwable("init");
      this.mReadMeidException[i] = new Throwable("init");
      this.mImeis[i] = null;
      this.mMeids[i] = null;
      getCommandsInterface(localObject[i]).registerForAvailable(this.mHandler, i + 100, null);
    }
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
    if ((TextUtils.isEmpty(localSharedPreferences.getString("device_id_key", ""))) && (!TextUtils.isEmpty(localSharedPreferences.getString("key_imei_slot0", ""))))
    {
      localObject = localSharedPreferences.edit();
      ((SharedPreferences.Editor)localObject).putString("device_id_key", localSharedPreferences.getString("key_imei_slot0", ""));
      if (!((SharedPreferences.Editor)localObject).commit()) {
        Rlog.d(LOG_TAG, "failed to commit preference when init");
      }
    }
  }
  
  public static CommandsInterface getCommandsInterface(Phone paramPhone)
  {
    return paramPhone.mCi;
  }
  
  public static boolean isImeiMeidKey(String paramString)
  {
    boolean bool;
    if ((!TextUtils.isEmpty(paramString)) && ((paramString.contains("key_imei_slot")) || (paramString.contains("key_meid_slot")) || (paramString.contains("small_device_id_key")) || (paramString.contains("device_id_key")))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void onDeviceIdLoaded()
  {
    for (int i = 0;; i++)
    {
      localObject1 = this.mImeis;
      if (i >= localObject1.length) {
        break label41;
      }
      if ((this.mReadImeiException[i] != null) || (TextUtils.isEmpty(localObject1[i]))) {
        break;
      }
    }
    return;
    label41:
    Object localObject1 = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
    SharedPreferences.Editor localEditor = ((SharedPreferences)localObject1).edit();
    String str;
    for (i = 0;; i++)
    {
      int j = this.mImeis.length;
      str = "";
      if (i >= j) {
        break;
      }
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("key_imei_slot");
      ((StringBuilder)localObject2).append(i);
      localEditor.putString(((StringBuilder)localObject2).toString(), this.mImeis[i]);
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("ro.ril.miui.imei");
      ((StringBuilder)localObject2).append(i);
      if ((TextUtils.isEmpty(SystemProperties.get(((StringBuilder)localObject2).toString()))) && ((!TelephonyManager.isCustSingleSimDevice()) || (i != 1)))
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("ro.ril.miui.imei");
        ((StringBuilder)localObject2).append(i);
        SystemProperties.set(((StringBuilder)localObject2).toString(), this.mImeis[i]);
      }
      if (!TextUtils.isEmpty(this.mMeids[i]))
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("key_meid_slot");
        ((StringBuilder)localObject2).append(i);
        localEditor.putString(((StringBuilder)localObject2).toString(), this.mMeids[i]);
      }
      else
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("key_meid_slot");
        ((StringBuilder)localObject2).append(i);
        if ((!TextUtils.isEmpty(((SharedPreferences)localObject1).getString(((StringBuilder)localObject2).toString(), ""))) && (Build.IS_INTERNATIONAL_BUILD) && (("lmi".equals(Build.DEVICE)) || ("lmipro".equals(Build.DEVICE)) || ("monet".equals(Build.DEVICE))))
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("key_meid_slot");
          ((StringBuilder)localObject2).append(i);
          localEditor.putString(((StringBuilder)localObject2).toString(), "");
        }
      }
    }
    Object localObject2 = ((SharedPreferences)localObject1).getString("device_id_key", "");
    localObject1 = ((SharedPreferences)localObject1).getString("small_device_id_key", "");
    if ((TextUtils.isEmpty((CharSequence)localObject2)) || (TextUtils.isEmpty((CharSequence)localObject1)))
    {
      String[] arrayOfString = this.mImeis;
      localObject2 = arrayOfString[0];
      localObject1 = arrayOfString[0];
      i = 0;
      if (arrayOfString.length == 2) {
        if (arrayOfString[0].compareTo(arrayOfString[1]) < 0)
        {
          arrayOfString = this.mImeis;
          localObject2 = arrayOfString[1];
          i = 1;
          if (arrayOfString[0].matches("^0*$")) {
            localObject1 = this.mImeis[1];
          }
        }
        else if (!this.mImeis[1].matches("^0*$"))
        {
          localObject1 = this.mImeis[1];
        }
      }
      if (((String)localObject1).matches("^0*$")) {
        localObject1 = str;
      }
      localEditor.putString("small_device_id_key", (String)localObject1);
      localEditor.putString("device_id_key", (String)localObject2);
      sendDeviceIdReadyBroadcast((String)localObject2, i);
    }
    if (!localEditor.commit()) {
      Rlog.d(LOG_TAG, "failed to commit preference");
    }
  }
  
  private void sendDeviceIdReadyBroadcast(String paramString, int paramInt)
  {
    Intent localIntent = new Intent("android.intent.action.DEVICE_ID_READY");
    localIntent.putExtra("device_id", paramString);
    SubscriptionManager.putPhoneIdAndSubIdExtra(localIntent, paramInt);
    MiuiTelephony.sContext.sendBroadcast(localIntent, "android.permission.READ_PHONE_STATE");
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    StringBuilder localStringBuilder = new StringBuilder(256);
    Object localObject = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext).getString("device_id_key", "");
    localStringBuilder.append(" deviceId=");
    localStringBuilder.append(PhoneNumberUtils.toLogSafePhoneNumber((String)localObject, 3));
    localObject = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext).getString("small_device_id_key", "");
    localStringBuilder.append(" smallDeviceId=");
    localStringBuilder.append(PhoneNumberUtils.toLogSafePhoneNumber((String)localObject, 3));
    int i;
    if (TelephonyManager.isCustSingleSimDevice()) {
      i = 1;
    } else {
      i = this.mImeis.length;
    }
    for (int j = 0; j < i; j++)
    {
      localStringBuilder.append(" slot");
      localStringBuilder.append(j);
      localStringBuilder.append(" imei=");
      localStringBuilder.append(PhoneNumberUtils.toLogSafePhoneNumber(this.mImeis[j], 3));
      localStringBuilder.append(" mReadImeiException=");
      localStringBuilder.append(this.mReadImeiException[j]);
      localStringBuilder.append(" meid=");
      localStringBuilder.append(PhoneNumberUtils.toLogSafePhoneNumber(this.mMeids[j], 3));
      localStringBuilder.append(" mReadMeidException=");
      localStringBuilder.append(this.mReadMeidException[j]);
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("ImeiMeidSource: ");
    ((StringBuilder)localObject).append(localStringBuilder.toString());
    paramPrintWriter.println(((StringBuilder)localObject).toString());
  }
  
  public String getDeviceId(String paramString)
  {
    if (!TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(MiuiTelephony.sContext, paramString, "getDeviceId")) {
      return null;
    }
    return PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext).getString("device_id_key", "");
  }
  
  public List<String> getDeviceIdList(String paramString)
  {
    if (!TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(MiuiTelephony.sContext, paramString, "getDeviceIdList")) {
      return new ArrayList(0);
    }
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
    paramString = new ArrayList(this.mImeis.length * 2);
    for (int i = 0; i < this.mImeis.length; i++)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("key_imei_slot");
      ((StringBuilder)localObject).append(i);
      localObject = localSharedPreferences.getString(((StringBuilder)localObject).toString(), "");
      if ((!TextUtils.isEmpty((CharSequence)localObject)) && (!paramString.contains(localObject))) {
        paramString.add(localObject);
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("key_meid_slot");
      ((StringBuilder)localObject).append(i);
      localObject = localSharedPreferences.getString(((StringBuilder)localObject).toString(), "");
      if ((!TextUtils.isEmpty((CharSequence)localObject)) && (!paramString.contains(localObject))) {
        paramString.add(localObject);
      }
    }
    return paramString;
  }
  
  public String getImei(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.mImeis.length)) {
      break label21;
    }
    paramInt = 0;
    label21:
    if (TextUtils.isEmpty(this.mImeis[paramInt]))
    {
      SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("key_imei_slot");
      localStringBuilder.append(paramInt);
      return localSharedPreferences.getString(localStringBuilder.toString(), "");
    }
    return this.mImeis[paramInt];
  }
  
  public String getImei(int paramInt, String paramString)
  {
    Phone localPhone = PhoneFactory.getPhone(paramInt);
    if (localPhone == null) {
      return null;
    }
    int i = localPhone.getSubId();
    if (!TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(MiuiTelephony.sContext, i, paramString, "getImei")) {
      return null;
    }
    return getImei(paramInt);
  }
  
  List<String> getImeiList(String paramString)
  {
    if (!TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(MiuiTelephony.sContext, paramString, "getSortedImeiList")) {
      return new ArrayList(0);
    }
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
    paramString = new ArrayList(MiuiTelephony.PHONE_COUNT);
    for (int i = 0; i < MiuiTelephony.PHONE_COUNT; i++)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("key_imei_slot");
      ((StringBuilder)localObject).append(i);
      localObject = localSharedPreferences.getString(((StringBuilder)localObject).toString(), "");
      if ((localObject != null) && (!((String)localObject).isEmpty())) {
        paramString.add(localObject);
      }
    }
    if (paramString.size() > 1) {
      Collections.sort(paramString);
    }
    return paramString;
  }
  
  public String getMeid(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.mMeids.length)) {
      break label21;
    }
    paramInt = 0;
    label21:
    if (TextUtils.isEmpty(this.mMeids[paramInt]))
    {
      SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("key_meid_slot");
      localStringBuilder.append(paramInt);
      return localSharedPreferences.getString(localStringBuilder.toString(), "");
    }
    return this.mMeids[paramInt];
  }
  
  public String getMeid(int paramInt, String paramString)
  {
    Phone localPhone = PhoneFactory.getPhone(paramInt);
    if (localPhone == null) {
      return null;
    }
    int i = localPhone.getSubId();
    if (!TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(MiuiTelephony.sContext, i, paramString, "getMeid")) {
      return null;
    }
    return getMeid(paramInt);
  }
  
  List<String> getMeidList(String paramString)
  {
    if ((!Build.IS_INTERNATIONAL_BUILD) && (("cmi".equals(Build.DEVICE)) || ("umi".equals(Build.DEVICE)) || ("picasso".equals(Build.DEVICE))) && ((paramString.equals("com.android.contacts")) || (paramString.equals("com.android.settings")))) {
      return new ArrayList(0);
    }
    if (!TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(MiuiTelephony.sContext, paramString, "getSortedMeidList")) {
      return new ArrayList(0);
    }
    paramString = PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext);
    ArrayList localArrayList = new ArrayList(MiuiTelephony.PHONE_COUNT);
    for (int i = 0; i < MiuiTelephony.PHONE_COUNT; i++)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("key_meid_slot");
      ((StringBuilder)localObject).append(i);
      localObject = paramString.getString(((StringBuilder)localObject).toString(), "");
      if ((localObject != null) && (!((String)localObject).isEmpty()) && (!localArrayList.contains(localObject))) {
        localArrayList.add(localObject);
      }
    }
    if (localArrayList.size() > 1) {
      Collections.sort(localArrayList);
    }
    return localArrayList;
  }
  
  public String getSmallDeviceId(String paramString)
  {
    if (!TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(MiuiTelephony.sContext, paramString, "getSmallDeviceId")) {
      return null;
    }
    return PreferenceManager.getDefaultSharedPreferences(MiuiTelephony.sContext).getString("small_device_id_key", "");
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/ImeiMeidSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */