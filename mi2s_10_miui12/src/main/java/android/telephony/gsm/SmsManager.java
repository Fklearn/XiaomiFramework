package android.telephony.gsm;

import android.app.PendingIntent;
import android.telephony.SmsMessage;
import android.util.SeempLog;
import java.util.ArrayList;

@Deprecated
public final class SmsManager
{
  @Deprecated
  public static final int RESULT_ERROR_GENERIC_FAILURE = 1;
  @Deprecated
  public static final int RESULT_ERROR_NO_SERVICE = 4;
  @Deprecated
  public static final int RESULT_ERROR_NULL_PDU = 3;
  @Deprecated
  public static final int RESULT_ERROR_RADIO_OFF = 2;
  @Deprecated
  public static final int STATUS_ON_SIM_FREE = 0;
  @Deprecated
  public static final int STATUS_ON_SIM_READ = 1;
  @Deprecated
  public static final int STATUS_ON_SIM_SENT = 5;
  @Deprecated
  public static final int STATUS_ON_SIM_UNREAD = 3;
  @Deprecated
  public static final int STATUS_ON_SIM_UNSENT = 7;
  private static SmsManager sInstance;
  private android.telephony.SmsManager mSmsMgrProxy = android.telephony.SmsManager.getDefault();
  
  @Deprecated
  public static final SmsManager getDefault()
  {
    if (sInstance == null) {
      sInstance = new SmsManager();
    }
    return sInstance;
  }
  
  @Deprecated
  public final boolean copyMessageToSim(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    SeempLog.record(82);
    return this.mSmsMgrProxy.copyMessageToIcc(paramArrayOfByte1, paramArrayOfByte2, paramInt);
  }
  
  @Deprecated
  public final boolean deleteMessageFromSim(int paramInt)
  {
    SeempLog.record(83);
    return this.mSmsMgrProxy.deleteMessageFromIcc(paramInt);
  }
  
  @Deprecated
  public final ArrayList<String> divideMessage(String paramString)
  {
    return this.mSmsMgrProxy.divideMessage(paramString);
  }
  
  @Deprecated
  public final ArrayList<SmsMessage> getAllMessagesFromSim()
  {
    SeempLog.record(85);
    return android.telephony.SmsManager.getDefault().getAllMessagesFromIcc();
  }
  
  @Deprecated
  public final void sendDataMessage(String paramString1, String paramString2, short paramShort, byte[] paramArrayOfByte, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2)
  {
    SeempLog.record_str(73, paramString1);
    this.mSmsMgrProxy.sendDataMessage(paramString1, paramString2, paramShort, paramArrayOfByte, paramPendingIntent1, paramPendingIntent2);
  }
  
  @Deprecated
  public final void sendMultipartTextMessage(String paramString1, String paramString2, ArrayList<String> paramArrayList, ArrayList<PendingIntent> paramArrayList1, ArrayList<PendingIntent> paramArrayList2)
  {
    SeempLog.record_str(77, paramString1);
    this.mSmsMgrProxy.sendMultipartTextMessage(paramString1, paramString2, paramArrayList, paramArrayList1, paramArrayList2);
  }
  
  @Deprecated
  public final void sendTextMessage(String paramString1, String paramString2, String paramString3, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2)
  {
    SeempLog.record_str(75, paramString1);
    this.mSmsMgrProxy.sendTextMessage(paramString1, paramString2, paramString3, paramPendingIntent1, paramPendingIntent2);
  }
  
  @Deprecated
  public final boolean updateMessageOnSim(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    SeempLog.record(84);
    return this.mSmsMgrProxy.updateMessageOnIcc(paramInt1, paramInt2, paramArrayOfByte);
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/android/telephony/gsm/SmsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */