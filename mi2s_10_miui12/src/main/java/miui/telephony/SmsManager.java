package miui.telephony;

import android.app.PendingIntent;
import android.telephony.SmsMessage;
import java.util.ArrayList;

public class SmsManager
{
  private static final int INVALID_SIM_SLOT_INDEX = -1;
  private int mSlotId = -1;
  
  private SmsManager() {}
  
  private SmsManager(int paramInt)
  {
    if (SubscriptionManager.isValidSlotId(paramInt)) {
      this.mSlotId = paramInt;
    }
  }
  
  public static SmsManager getDefault()
  {
    return Holder.sDefaultInstance;
  }
  
  public static SmsManager getDefault(int paramInt)
  {
    if (SubscriptionManager.isRealSlotId(paramInt)) {
      return Holder.sInstance[paramInt];
    }
    return getDefault();
  }
  
  private android.telephony.SmsManager getSmsManager()
  {
    if (this.mSlotId == -1) {
      return android.telephony.SmsManager.getDefault();
    }
    return android.telephony.SmsManager.getSmsManagerForSubscriptionId(SubscriptionManager.getDefault().getSubscriptionIdForSlot(this.mSlotId));
  }
  
  public boolean copyMessageToIcc(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    return getSmsManager().copyMessageToIcc(paramArrayOfByte1, paramArrayOfByte2, paramInt);
  }
  
  public boolean deleteMessageFromIcc(int paramInt)
  {
    return getSmsManager().deleteMessageFromIcc(paramInt);
  }
  
  public ArrayList<String> divideMessage(String paramString)
  {
    return getSmsManager().divideMessage(paramString);
  }
  
  public ArrayList<SmsMessage> getAllMessagesFromIcc()
  {
    return getSmsManager().getAllMessagesFromIcc();
  }
  
  public void sendMultipartTextMessage(String paramString1, String paramString2, ArrayList<String> paramArrayList, ArrayList<PendingIntent> paramArrayList1, ArrayList<PendingIntent> paramArrayList2)
  {
    getSmsManager().sendMultipartTextMessage(paramString1, paramString2, paramArrayList, paramArrayList1, paramArrayList2);
  }
  
  public void sendMultipartTextMessage(String paramString1, String paramString2, ArrayList<String> paramArrayList, ArrayList<PendingIntent> paramArrayList1, ArrayList<PendingIntent> paramArrayList2, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    getSmsManager().sendMultipartTextMessage(paramString1, paramString2, paramArrayList, paramArrayList1, paramArrayList2);
  }
  
  public void sendTextMessage(String paramString1, String paramString2, String paramString3, PendingIntent paramPendingIntent1, PendingIntent paramPendingIntent2)
  {
    getSmsManager().sendTextMessage(paramString1, paramString2, paramString3, paramPendingIntent1, paramPendingIntent2);
  }
  
  static class Holder
  {
    private static SmsManager sDefaultInstance;
    private static SmsManager[] sInstance = new SmsManager[TelephonyManager.getDefault().getPhoneCount()];
    
    static
    {
      sDefaultInstance = new SmsManager(null);
      for (int i = 0;; i++)
      {
        SmsManager[] arrayOfSmsManager = sInstance;
        if (i >= arrayOfSmsManager.length) {
          break;
        }
        arrayOfSmsManager[i] = new SmsManager(i, null);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/SmsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */