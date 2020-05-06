package miui.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.net.URL;
import org.json.JSONObject;

public class Network
{
  public static final String CMWAP_GATEWAY = "10.0.0.172";
  public static final String CMWAP_HEADER_HOST_KEY = "X-Online-Host";
  public static final int CONNECTION_TIMEOUT = 10000;
  private static final String LogTag = Network.class.getSimpleName();
  public static final String OPERATOR_TYPE_FILE_NAME = "ot";
  public static final int READ_TIMEOUT = 15000;
  public static final String RESPONSE_BODY = "RESPONSE_BODY";
  public static final String RESPONSE_CODE = "RESPONSE_CODE";
  
  public static JSONObject doHttpPostWithResponseStatus(Context paramContext, String paramString1, String paramString2)
  {
    if (paramContext != null)
    {
      if ((paramString1 != null) && (paramString1.trim().length() != 0)) {
        return new JSONObject();
      }
      throw new IllegalArgumentException("url");
    }
    throw new IllegalArgumentException("context");
  }
  
  public static String getCMWapUrl(URL paramURL)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramURL.getProtocol());
    localStringBuilder.append("://");
    localStringBuilder.append("10.0.0.172");
    localStringBuilder.append(paramURL.getPath());
    if (!TextUtils.isEmpty(paramURL.getQuery()))
    {
      localStringBuilder.append("?");
      localStringBuilder.append(paramURL.getQuery());
    }
    return localStringBuilder.toString();
  }
  
  /* Error */
  public static String getOperatorType()
  {
    // Byte code:
    //   0: ldc 99
    //   2: astore_0
    //   3: aconst_null
    //   4: astore_1
    //   5: aconst_null
    //   6: astore_2
    //   7: aload_2
    //   8: astore_3
    //   9: aload_1
    //   10: astore 4
    //   12: new 101	java/io/File
    //   15: astore 5
    //   17: aload_2
    //   18: astore_3
    //   19: aload_1
    //   20: astore 4
    //   22: aload 5
    //   24: invokestatic 107	miui/os/Environment:getMiuiDataDirectory	()Ljava/io/File;
    //   27: ldc 18
    //   29: invokespecial 110	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   32: aload_2
    //   33: astore_3
    //   34: aload_1
    //   35: astore 4
    //   37: new 112	java/io/BufferedReader
    //   40: astore 6
    //   42: aload_2
    //   43: astore_3
    //   44: aload_1
    //   45: astore 4
    //   47: new 114	java/io/FileReader
    //   50: astore 7
    //   52: aload_2
    //   53: astore_3
    //   54: aload_1
    //   55: astore 4
    //   57: aload 7
    //   59: aload 5
    //   61: invokespecial 117	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   64: aload_2
    //   65: astore_3
    //   66: aload_1
    //   67: astore 4
    //   69: aload 6
    //   71: aload 7
    //   73: invokespecial 120	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   76: aload 6
    //   78: astore_2
    //   79: aload_2
    //   80: astore_3
    //   81: aload_2
    //   82: astore 4
    //   84: aload_2
    //   85: invokevirtual 123	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   88: astore 6
    //   90: aload 6
    //   92: astore_0
    //   93: aload_0
    //   94: astore_3
    //   95: aload_2
    //   96: invokevirtual 126	java/io/BufferedReader:close	()V
    //   99: aload_0
    //   100: astore_2
    //   101: goto +42 -> 143
    //   104: astore_0
    //   105: aload_3
    //   106: astore_0
    //   107: goto -8 -> 99
    //   110: astore_0
    //   111: aload_3
    //   112: ifnull +11 -> 123
    //   115: aload_3
    //   116: invokevirtual 126	java/io/BufferedReader:close	()V
    //   119: goto +4 -> 123
    //   122: astore_2
    //   123: aload_0
    //   124: athrow
    //   125: astore_2
    //   126: aload_0
    //   127: astore_2
    //   128: aload 4
    //   130: ifnull +13 -> 143
    //   133: aload_0
    //   134: astore_3
    //   135: aload 4
    //   137: invokevirtual 126	java/io/BufferedReader:close	()V
    //   140: goto -41 -> 99
    //   143: aload_2
    //   144: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   2	98	0	localObject1	Object
    //   104	1	0	localIOException1	java.io.IOException
    //   106	1	0	localObject2	Object
    //   110	24	0	localObject3	Object
    //   4	63	1	localObject4	Object
    //   6	95	2	localObject5	Object
    //   122	1	2	localIOException2	java.io.IOException
    //   125	1	2	localIOException3	java.io.IOException
    //   127	17	2	localObject6	Object
    //   8	127	3	localObject7	Object
    //   10	126	4	localObject8	Object
    //   15	45	5	localFile	java.io.File
    //   40	51	6	localObject9	Object
    //   50	22	7	localFileReader	java.io.FileReader
    // Exception table:
    //   from	to	target	type
    //   95	99	104	java/io/IOException
    //   135	140	104	java/io/IOException
    //   12	17	110	finally
    //   22	32	110	finally
    //   37	42	110	finally
    //   47	52	110	finally
    //   57	64	110	finally
    //   69	76	110	finally
    //   84	90	110	finally
    //   115	119	122	java/io/IOException
    //   12	17	125	java/io/IOException
    //   22	32	125	java/io/IOException
    //   37	42	125	java/io/IOException
    //   47	52	125	java/io/IOException
    //   57	64	125	java/io/IOException
    //   69	76	125	java/io/IOException
    //   84	90	125	java/io/IOException
  }
  
  public static boolean isCmwap(Context paramContext)
  {
    if (!"CN".equalsIgnoreCase(((TelephonyManager)paramContext.getSystemService("phone")).getSimCountryIso())) {
      return false;
    }
    paramContext = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    if (paramContext == null) {
      return false;
    }
    paramContext = paramContext.getExtraInfo();
    if ((!TextUtils.isEmpty(paramContext)) && (paramContext.length() >= 3) && (!paramContext.contains("ctwap"))) {
      return paramContext.regionMatches(true, paramContext.length() - 3, "wap", 0, 3);
    }
    return false;
  }
  
  public static boolean isNetworkConnected(Context paramContext)
  {
    if (paramContext != null)
    {
      paramContext = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
      if (paramContext != null) {
        return paramContext.isAvailable();
      }
    }
    return false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/Network.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */