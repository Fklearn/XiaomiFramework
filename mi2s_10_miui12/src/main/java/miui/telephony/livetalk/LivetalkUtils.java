package miui.telephony.livetalk;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.System;
import android.util.Pair;
import miui.telephony.TelephonyManager;
import org.json.JSONArray;
import org.json.JSONException;

public class LivetalkUtils
{
  public static final String DAIL_MODE = "dial_mode";
  public static final int DIAL_MODE_GENERAL = 0;
  public static final int DIAL_MODE_LIVETALK = 1;
  public static final String ENABLE_LIVETALK_SUMMARY_CN = "enable_livetalk_summary_cn";
  public static final String ENABLE_LIVETALK_SUMMARY_EN = "enable_livetalk_summary_en";
  public static final String ENABLE_LIVETALK_TITLE_CN = "enable_livetalk_title_cn";
  public static final String ENABLE_LIVETALK_TITLE_EN = "enable_livetalk_title_en";
  public static final String FROM_VIEW = "fromView";
  public static final String INTENT_ACCEPT_BACK_CALL = "com.miui.livetalk_ACCEPT_BACK_CALL";
  private static final String INTENT_MY_LIVETALK = "com.miui.livetalk.MY_LIVETALK_VIEW";
  private static final String INTENT_PURCHASE_ACTION = "com.miui.livetalk.PURCHASE_VIEW";
  public static final String INTENT_RECORD_CALL_BACK_INFO = "com.miui.livetalk_RECORD_CALLBACK_INFO";
  private static final String INTENT_WELCOME_ACTION = "com.miui.livetalk.WELCOME_VIEW";
  public static final String IS_LIVETALK_DIAL = "isLivetalk";
  public static final int IS_NEED_PROMPT = 1;
  public static final int LIVETALK_AVAILABLE = 1;
  public static final String LIVETALK_AVAILABLE_STATUS = "livetalk_available_status";
  public static final String LIVETALK_DIAL_RANGE = "livetalk_dial_range";
  public static final int LIVETALK_DIAL_RANGE_DEMOSTIC = 1;
  public static final int LIVETALK_DIAL_RANGE_INTERNATIONAL = 2;
  public static final int LIVETALK_DIAL_RANGE_WHOLE = 0;
  public static final String LIVETALK_ENABLED = "livetalk_enabled";
  public static final String LIVETALK_INTERNAL_DIAL_AVAIABLE = "internal_dial_avaiable";
  public static final String LIVETALK_INTERNAL_DIAL_ENABLE = "internal_dial_enable";
  public static final String LIVETALK_INTERNATIONAL_DIAL_AVAIABLE = "international_dial_avaiable";
  public static final String LIVETALK_INTERNATIONAL_DIAL_ENABLE = "international_dial_enable";
  public static final int LIVETALK_NOT_AVAILABLE = 0;
  public static int LIVETALK_NUMBER_POOL_VERSION = 0;
  public static final String LIVETALK_RECENT_COUNTRY_REMAIN_MINS = "recent_country_remain_mins";
  public static final String LIVETALK_REMAIN_MINUTES = "livetalk_remain_minutes";
  public static final String LIVETALK_SERVICE_NAME = "com.miui.livetalk.service.LivetalkService";
  public static final String LIVETALK_SERVICE_STATUS = "livetalk_service_status";
  public static final String LIVETALK_SWITCH_STATE = "livetalk_switch_state";
  public static final String LIVETALK_USE_CURRENT_MI_ACCOUNT = "livetalk_use_current_account";
  public static final int LIVETALK_WITH_170 = 2;
  private static final String META_DATA_SUPPORT_LIVETALK = "support_livetalk";
  public static final int MY_LIVETALK_FROM_CONTACTS = 202;
  public static final int MY_LIVETALK_FROM_NOTIFICATION = 200;
  public static final int MY_LIVETALK_FROM_SETTING = 201;
  public static final String NEED_PROMPT = "need_prompt";
  public static final int NOT_NEED_PROMPT = 0;
  public static final String ONLY_REGULAR_CALL = "only_regular_call";
  public static final String PARAM_NUMBER = "number";
  public static final int PURCHASE_FROM_DIALPAGE = 2;
  public static final int PURCHASE_FROM_INTERNATIONAL = 8;
  public static final int PURCHASE_FROM_NOTIFICATION = 5;
  public static final int PURCHASE_FROM_SAFE_CENTER_CLEANER = 7;
  public static final int PURCHASE_FROM_SAFE_CENTER_OPTIMIZE = 6;
  public static final int PURCHASE_FROM_SETTING = 4;
  public static final int PURCHASE_FROM_SMS = 1;
  public static final int PURCHASE_FROM_YELLOWPAGE = 3;
  public static final String SAFE_CENTER_CLEANER_SUMMARY = "safe_center_cleaner_summary";
  public static final String SAFE_CENTER_CLEANER_TITLE = "safe_center_cleaner_title";
  public static final String SAFE_CENTER_OPTIMIZE_SUMMARY_CN = "safe_center_optimize_summary_cn";
  public static final String SAFE_CENTER_OPTIMIZE_SUMMARY_EN = "safe_center_optimize_summary_en";
  public static final String SAFE_CENTER_OPTIMIZE_TITLE_CN = "safe_center_optimize_title_cn";
  public static final String SAFE_CENTER_OPTIMIZE_TITLE_EN = "safe_center_optimize_title_en";
  public static final String SIM_CARD_ACTIVATED_STATE = "sim_card_activated_status";
  public static final String SIM_CARD_NUMBER = "sim_card_number";
  private static final String TAG = "LivetalkUtils";
  public static final String USER_CONFIG_COMPLETED = "user_config_completed";
  public static final int WELCOME_FROM_PURCHASE = 102;
  public static final int WELCOME_FROM_SETTING = 101;
  private static String[] sCallBackNumbers;
  
  public static void addPrompt(Context paramContext)
  {
    Settings.System.putInt(paramContext.getContentResolver(), "need_prompt", 1);
  }
  
  public static boolean getInternalDialAvaiable(Context paramContext)
  {
    return false;
  }
  
  public static boolean getInternationalDialAvaiable(Context paramContext)
  {
    return false;
  }
  
  public static int getInternationalRemainMins(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "recent_country_remain_mins", 0);
  }
  
  public static Pair<String, String> getLivetalkCleanerInfo(Context paramContext)
  {
    return null;
  }
  
  public static int getLivetalkDialRange(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "livetalk_dial_range", 0);
  }
  
  public static Pair<String, String> getLivetalkInfo(Context paramContext)
  {
    return null;
  }
  
  public static Intent getLivetalkIntentWithParam(int paramInt)
  {
    Intent localIntent = new Intent();
    localIntent.setAction("com.miui.livetalk.MY_LIVETALK_VIEW");
    localIntent.putExtra("fromView", paramInt);
    return localIntent;
  }
  
  public static Pair<String, String> getLivetalkOptimizeInfo(Context paramContext)
  {
    return null;
  }
  
  public static int getLivetalkServiceStatus(Context paramContext)
  {
    return 0;
  }
  
  public static int getLivetalkStatus(Context paramContext)
  {
    return 0;
  }
  
  public static Pair<String, String> getLivetalkinfoForKK(ContentResolver paramContentResolver, Context paramContext)
  {
    return null;
  }
  
  public static Intent getPurchaseIntentWithParam(int paramInt)
  {
    Intent localIntent = new Intent();
    localIntent.setAction("com.miui.livetalk.PURCHASE_VIEW");
    localIntent.putExtra("fromView", paramInt);
    return localIntent;
  }
  
  public static int getRemainMins(Context paramContext)
  {
    return Settings.System.getInt(paramContext.getContentResolver(), "livetalk_remain_minutes", 0);
  }
  
  public static int[] getSimActivatedState(Context paramContext)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    int i = TelephonyManager.getDefault().getPhoneCount();
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++)
    {
      paramContext = new StringBuilder();
      paramContext.append("sim_card_activated_status");
      paramContext.append(j);
      arrayOfInt[j] = Settings.System.getInt(localContentResolver, paramContext.toString(), 0);
    }
    return arrayOfInt;
  }
  
  public static String[] getSimNumber(Context paramContext)
  {
    paramContext = paramContext.getContentResolver();
    int i = TelephonyManager.getDefault().getPhoneCount();
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("sim_card_number");
      localStringBuilder.append(j);
      arrayOfString[j] = Settings.System.getString(paramContext, localStringBuilder.toString());
    }
    return arrayOfString;
  }
  
  public static Intent getWelComeIntentWithParam(int paramInt)
  {
    Intent localIntent = new Intent();
    localIntent.setAction("com.miui.livetalk.WELCOME_VIEW");
    localIntent.putExtra("fromView", paramInt);
    return localIntent;
  }
  
  public static boolean isInternalDialEnable(Context paramContext)
  {
    return false;
  }
  
  public static boolean isInternationalDialEnable(Context paramContext)
  {
    return false;
  }
  
  public static boolean isLiveTalkCallbackNumber(String paramString)
  {
    return false;
  }
  
  public static boolean isLivetalkEnabled(Context paramContext)
  {
    paramContext = paramContext.getContentResolver();
    boolean bool = false;
    if (Settings.System.getInt(paramContext, "livetalk_enabled", 0) == 1) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isLivetalkSwitchOn(Context paramContext)
  {
    paramContext = paramContext.getContentResolver();
    boolean bool = true;
    if (Settings.System.getInt(paramContext, "livetalk_switch_state", 1) != 1) {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isLivetalkUseCurrentAccount(Context paramContext)
  {
    paramContext = paramContext.getContentResolver();
    boolean bool = false;
    if (Settings.System.getInt(paramContext, "livetalk_use_current_account", 0) == 1) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isPrompt(Context paramContext)
  {
    return false;
  }
  
  public static boolean isShowInSafeCenter(Context paramContext)
  {
    return false;
  }
  
  public static void removePrompt(Context paramContext)
  {
    Settings.System.putInt(paramContext.getContentResolver(), "need_prompt", 0);
  }
  
  public static void setInternalDialEnable(Context paramContext, boolean paramBoolean)
  {
    paramContext = paramContext.getContentResolver();
    Settings.System.putInt(paramContext, "internal_dial_enable", paramBoolean);
  }
  
  public static void setInternationalDialEnable(Context paramContext, boolean paramBoolean)
  {
    paramContext = paramContext.getContentResolver();
    Settings.System.putInt(paramContext, "international_dial_enable", paramBoolean);
  }
  
  private static boolean supportLivetalk(Context paramContext)
  {
    return false;
  }
  
  public static void updateLivetalkCallBackNumber(JSONArray paramJSONArray)
  {
    if (paramJSONArray == null) {
      return;
    }
    sCallBackNumbers = new String[paramJSONArray.length()];
    int i = 0;
    try
    {
      while (i < paramJSONArray.length())
      {
        sCallBackNumbers[i] = paramJSONArray.getString(i);
        i++;
      }
    }
    catch (JSONException paramJSONArray)
    {
      paramJSONArray.printStackTrace();
    }
  }
  
  public static void updateLivetalkCallBackNumber(JSONArray paramJSONArray, int paramInt)
    throws JSONException
  {
    if (paramJSONArray == null) {
      return;
    }
    int i = paramJSONArray.length();
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++) {
      arrayOfString[j] = paramJSONArray.getString(j);
    }
    sCallBackNumbers = arrayOfString;
    LIVETALK_NUMBER_POOL_VERSION = paramInt;
  }
  
  /* Error */
  public static boolean updateLivetalkCallBackNumber(android.database.Cursor paramCursor)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnull +111 -> 112
    //   4: aload_0
    //   5: invokeinterface 297 1 0
    //   10: ifne +6 -> 16
    //   13: goto +99 -> 112
    //   16: aload_0
    //   17: invokeinterface 297 1 0
    //   22: anewarray 251	java/lang/String
    //   25: putstatic 281	miui/telephony/livetalk/LivetalkUtils:sCallBackNumbers	[Ljava/lang/String;
    //   28: aload_0
    //   29: invokeinterface 301 1 0
    //   34: pop
    //   35: iconst_0
    //   36: istore_1
    //   37: iload_1
    //   38: aload_0
    //   39: invokeinterface 297 1 0
    //   44: if_icmpge +35 -> 79
    //   47: getstatic 281	miui/telephony/livetalk/LivetalkUtils:sCallBackNumbers	[Ljava/lang/String;
    //   50: iload_1
    //   51: aload_0
    //   52: aload_0
    //   53: ldc 113
    //   55: invokeinterface 305 2 0
    //   60: invokeinterface 306 2 0
    //   65: aastore
    //   66: aload_0
    //   67: invokeinterface 309 1 0
    //   72: pop
    //   73: iinc 1 1
    //   76: goto -39 -> 37
    //   79: aload_0
    //   80: invokeinterface 312 1 0
    //   85: iconst_1
    //   86: ireturn
    //   87: astore_2
    //   88: goto +16 -> 104
    //   91: astore_2
    //   92: aload_2
    //   93: invokevirtual 313	java/lang/Exception:printStackTrace	()V
    //   96: aload_0
    //   97: invokeinterface 312 1 0
    //   102: iconst_0
    //   103: ireturn
    //   104: aload_0
    //   105: invokeinterface 312 1 0
    //   110: aload_2
    //   111: athrow
    //   112: iconst_0
    //   113: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	114	0	paramCursor	android.database.Cursor
    //   36	38	1	i	int
    //   87	1	2	localObject	Object
    //   91	20	2	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   16	35	87	finally
    //   37	73	87	finally
    //   92	96	87	finally
    //   16	35	91	java/lang/Exception
    //   37	73	91	java/lang/Exception
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/telephony/livetalk/LivetalkUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */