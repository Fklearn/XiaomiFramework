package miui.telephony;

import android.app.ActivityThread;
import android.app.Application;
import android.content.ContentResolver;
import android.os.SystemProperties;
import android.provider.Settings.System;
import android.telephony.Rlog;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.uicc.IccRecords;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;

public class ServiceProviderUtils
{
  private static final SparseArray<ServiceProvider> ALL_SERVICE_PROVIDER;
  private static final String CTS_CARRIER_NAME_OVERRIDE = "carrier_a";
  private static final boolean DEBUG;
  private static final String LOG_TAG = "ServiceProvider";
  private static final String MIUI_SPN_OVERRIDE_PATH = "etc/miui-spn-conf.xml";
  private static final int MIUI_SPN_OVERRIDE_VERSION = 1;
  private static final String SETTING_PREFIX = "MOBILE_OPERATOR_NAME_";
  
  static
  {
    boolean bool = false;
    if (SystemProperties.getInt("ro.debuggable", 0) == 1) {
      bool = true;
    }
    DEBUG = bool;
    ALL_SERVICE_PROVIDER = new SparseArray();
    loadFromXml();
  }
  
  public static String get(String paramString, int paramInt)
  {
    return get(paramString, paramInt, null, true);
  }
  
  public static String get(String paramString1, int paramInt, String paramString2)
  {
    return get(paramString1, paramInt, paramString2, true);
  }
  
  public static String get(String paramString1, int paramInt, String paramString2, boolean paramBoolean)
  {
    Object localObject1 = null;
    int i = paramInt;
    Object localObject2 = localObject1;
    int j = i;
    Object localObject3;
    if (paramString1 != null)
    {
      localObject2 = localObject1;
      j = i;
      if (paramString1.length() > 0)
      {
        localObject1 = getFromSettings(paramString1);
        int k;
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          k = i;
          if (((String)localObject1).length() != 0) {}
        }
        else
        {
          if ((noNeedOverride(paramInt)) && (!TextUtils.isEmpty(paramString2))) {
            return paramString2;
          }
          k = i;
          if (i == SubscriptionManager.DEFAULT_SLOT_ID) {
            k = SubscriptionManager.getDefault().getDefaultSlotId();
          }
          try
          {
            IServiceProvider localIServiceProvider = getServiceProvider(Integer.parseInt(paramString1), k, paramString2);
            localObject2 = localObject1;
            if (localIServiceProvider != null)
            {
              localObject2 = localIServiceProvider.getName();
              if ((!paramBoolean) && (((Name)localObject2).shortAlpha != null) && (((Name)localObject2).shortAlpha.length() != 0)) {
                localObject2 = ((Name)localObject2).shortAlpha;
              } else {
                localObject2 = ((Name)localObject2).longAlpha;
              }
            }
          }
          catch (Exception localException)
          {
            localObject3 = new StringBuilder();
            ((StringBuilder)localObject3).append("invalid numeric=");
            ((StringBuilder)localObject3).append(paramString1);
            Rlog.w("ServiceProvider", ((StringBuilder)localObject3).toString());
            localObject3 = localObject1;
          }
        }
        if ((localObject3 != null) && (((String)localObject3).length() != 0))
        {
          j = k;
          if (!"carrier_a".equals(paramString2)) {}
        }
        else
        {
          localObject3 = paramString2;
          j = k;
        }
      }
    }
    if (DEBUG)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("numeric=");
      ((StringBuilder)localObject1).append(paramString1);
      ((StringBuilder)localObject1).append(", slotId=");
      ((StringBuilder)localObject1).append(j);
      ((StringBuilder)localObject1).append(", spn= ");
      ((StringBuilder)localObject1).append(paramString2);
      ((StringBuilder)localObject1).append(" => ");
      ((StringBuilder)localObject1).append((String)localObject3);
      Rlog.d("ServiceProvider", ((StringBuilder)localObject1).toString());
    }
    return (String)localObject3;
  }
  
  public static String get(String paramString, int paramInt, boolean paramBoolean)
  {
    return get(paramString, paramInt, null, paramBoolean);
  }
  
  public static String get(String paramString1, String paramString2)
  {
    return get(paramString1, -1, paramString2, true);
  }
  
  public static String get(String paramString1, String paramString2, boolean paramBoolean)
  {
    return get(paramString1, -1, paramString2, paramBoolean);
  }
  
  private static String getFromSettings(String paramString)
  {
    ContentResolver localContentResolver = ActivityThread.currentApplication().getContentResolver();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("MOBILE_OPERATOR_NAME_");
    localStringBuilder.append(paramString);
    return Settings.System.getString(localContentResolver, localStringBuilder.toString());
  }
  
  private static IServiceProvider getServiceProvider(int paramInt1, int paramInt2, String paramString)
  {
    ServiceProvider localServiceProvider = (ServiceProvider)ALL_SERVICE_PROVIDER.get(paramInt1);
    Object localObject1 = localServiceProvider;
    Object localObject2 = localObject1;
    if (localServiceProvider != null) {
      if (!SubscriptionManager.isRealSlotId(paramInt2))
      {
        localObject2 = localObject1;
        if (TextUtils.isEmpty(paramString)) {}
      }
      else
      {
        Iterator localIterator = localServiceProvider.getAllVirtualServiceProvider().iterator();
        for (;;)
        {
          localObject2 = localObject1;
          if (!localIterator.hasNext()) {
            break;
          }
          localObject2 = (VirtualServiceProvider)localIterator.next();
          if (((VirtualServiceProvider)localObject2).match(paramInt2, paramString, paramInt1)) {
            break;
          }
        }
        if (!localServiceProvider.match(paramInt2, paramString, paramInt1)) {
          localObject2 = null;
        }
      }
    }
    if (DEBUG)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("getServiceProvider numeric=");
      ((StringBuilder)localObject1).append(paramInt1);
      ((StringBuilder)localObject1).append(", slotId=");
      ((StringBuilder)localObject1).append(paramInt2);
      ((StringBuilder)localObject1).append(", spn= ");
      ((StringBuilder)localObject1).append(paramString);
      ((StringBuilder)localObject1).append(" => ");
      ((StringBuilder)localObject1).append(localObject2);
      Rlog.d("ServiceProvider", ((StringBuilder)localObject1).toString());
    }
    return (IServiceProvider)localObject2;
  }
  
  public static boolean isAPT52505Sim(int paramInt)
  {
    Phone localPhone = PhoneFactory.getPhone(paramInt);
    boolean bool = false;
    if (localPhone == null) {
      return false;
    }
    if (!localPhone.getIccRecordsLoaded()) {
      return false;
    }
    String str = localPhone.getIccCard().getIccRecords().getGid1();
    if (str != null)
    {
      str = str.trim();
      if (("52505".equals(localPhone.getOperatorNumeric())) && (str.startsWith("0A"))) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public static boolean isChinaMobile(int paramInt1, int paramInt2)
  {
    boolean bool;
    if (paramInt1 == 460) {
      bool = isSameServiceProvider(46000, paramInt2 + 46000);
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isChinaMobile(String paramString)
  {
    if ((paramString != null) && (paramString.length() >= 5)) {
      try
      {
        boolean bool = isSameServiceProvider(46000, Integer.parseInt(paramString.substring(0, 5)));
        return bool;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("invalid numeric=");
        localStringBuilder.append(paramString);
        Rlog.e("ServiceProvider", localStringBuilder.toString());
      }
    }
    return false;
  }
  
  public static boolean isChinaTelecom(String paramString)
  {
    if ((paramString != null) && (paramString.length() >= 5)) {
      try
      {
        boolean bool = isSameServiceProvider(46003, Integer.parseInt(paramString.substring(0, 5)));
        return bool;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("invalid numeric=");
        localStringBuilder.append(paramString);
        Rlog.e("ServiceProvider", localStringBuilder.toString());
      }
    }
    return false;
  }
  
  public static boolean isChinaTelecomSim(int paramInt1, int paramInt2)
  {
    boolean bool;
    if (paramInt1 == 460) {
      bool = isSameServiceProvider(46003, 46000 + paramInt2);
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isChinaUnicom(String paramString)
  {
    if ((paramString != null) && (paramString.length() >= 5)) {
      try
      {
        boolean bool = isSameServiceProvider(46001, Integer.parseInt(paramString.substring(0, 5)));
        return bool;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("invalid numeric=");
        localStringBuilder.append(paramString);
        Rlog.e("ServiceProvider", localStringBuilder.toString());
      }
    }
    return false;
  }
  
  public static boolean isChinaUnicomSim(int paramInt1, int paramInt2)
  {
    boolean bool;
    if (paramInt1 == 460) {
      bool = isSameServiceProvider(46001, 46000 + paramInt2);
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isChineseServiceProvider(String paramString)
  {
    boolean bool;
    if ((paramString != null) && (paramString.length() == 5) && (paramString.startsWith("460"))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isEITSim(int paramInt)
  {
    Object localObject = PhoneFactory.getPhone(paramInt);
    boolean bool = false;
    if (localObject == null) {
      return false;
    }
    String str = ((Phone)localObject).getGroupIdLevel1();
    if (!TextUtils.isEmpty(str))
    {
      str = str.trim();
      localObject = ((Phone)localObject).getOperatorNumeric();
      if ((("20826".equals(localObject)) || ("20601".equals(localObject)) || ("20801".equals(localObject))) && ("4E524A31".equals(str))) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public static boolean isInRange(String paramString1, String paramString2)
  {
    if ((!TextUtils.isEmpty(paramString1)) && (!TextUtils.isEmpty(paramString2)))
    {
      paramString1 = paramString1.split("~");
      return (paramString1.length == 2) && (paramString2.compareTo(paramString1[0]) >= 0) && (paramString2.compareTo(paramString1[1]) <= 0);
    }
    return false;
  }
  
  public static boolean isMacauTelecom(String paramString)
  {
    if ((paramString != null) && (paramString.length() >= 5)) {
      try
      {
        boolean bool = isSameServiceProvider(45502, Integer.parseInt(paramString.substring(0, 5)));
        return bool;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("invalid numeric=");
        localStringBuilder.append(paramString);
        Rlog.e("ServiceProvider", localStringBuilder.toString());
      }
    }
    return false;
  }
  
  public static boolean isSameServiceProvider(int paramInt1, int paramInt2)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    boolean bool3 = true;
    if (paramInt1 == paramInt2) {
      return true;
    }
    IServiceProvider localIServiceProvider1 = getServiceProvider(paramInt1, -1, null);
    IServiceProvider localIServiceProvider2 = getServiceProvider(paramInt2, -1, null);
    if ((localIServiceProvider1 != null) && (localIServiceProvider2 != null))
    {
      if (localIServiceProvider1.getBaseNumeric() != localIServiceProvider2.getBaseNumeric()) {
        bool3 = false;
      }
      return bool3;
    }
    if (localIServiceProvider1 != null)
    {
      if (localIServiceProvider1.getBaseNumeric() == paramInt2) {
        bool3 = bool1;
      } else {
        bool3 = false;
      }
      return bool3;
    }
    if (localIServiceProvider2 != null)
    {
      if (localIServiceProvider2.getBaseNumeric() == paramInt1) {
        bool3 = bool2;
      } else {
        bool3 = false;
      }
      return bool3;
    }
    return false;
  }
  
  public static boolean isSameServiceProvider(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null) && (paramString1.length() == paramString2.length()))
    {
      if (paramString1.equals(paramString2)) {
        return true;
      }
      if (!paramString1.startsWith(paramString2.substring(0, 3))) {
        return false;
      }
      return isSameServiceProvider(Integer.parseInt(paramString1), Integer.parseInt(paramString2));
    }
    return false;
  }
  
  public static boolean isVirtualServiceProvider(String paramString1, int paramInt, String paramString2)
  {
    if ((paramString1 != null) && (paramString1.length() > 0)) {
      try
      {
        int i = Integer.parseInt(paramString1);
        paramString1 = (ServiceProvider)ALL_SERVICE_PROVIDER.get(i);
        if ((paramString1 != null) && ((SubscriptionManager.isRealSlotId(paramInt)) || (!TextUtils.isEmpty(paramString2))))
        {
          paramString1 = paramString1.getAllVirtualServiceProvider().iterator();
          while (paramString1.hasNext()) {
            if (((VirtualServiceProvider)paramString1.next()).match(paramInt, paramString2, i)) {
              return true;
            }
          }
          return false;
        }
        return false;
      }
      catch (NumberFormatException paramString2)
      {
        paramString2 = new StringBuilder();
        paramString2.append("invalid numeric=");
        paramString2.append(paramString1);
        Rlog.e("ServiceProvider", paramString2.toString());
        return false;
      }
    }
    return false;
  }
  
  /* Error */
  private static void loadFromXml()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_0
    //   2: aconst_null
    //   3: astore_1
    //   4: invokestatic 341	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   7: astore_2
    //   8: aload_2
    //   9: astore_1
    //   10: aload_2
    //   11: astore_0
    //   12: new 343	java/io/FileReader
    //   15: astore_3
    //   16: aload_2
    //   17: astore_1
    //   18: aload_2
    //   19: astore_0
    //   20: new 345	java/io/File
    //   23: astore 4
    //   25: aload_2
    //   26: astore_1
    //   27: aload_2
    //   28: astore_0
    //   29: aload 4
    //   31: invokestatic 351	miui/os/Environment:getRootDirectory	()Ljava/io/File;
    //   34: ldc 53
    //   36: invokespecial 354	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   39: aload_2
    //   40: astore_1
    //   41: aload_2
    //   42: astore_0
    //   43: aload_3
    //   44: aload 4
    //   46: invokespecial 357	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   49: aload_2
    //   50: astore_1
    //   51: aload_2
    //   52: astore_0
    //   53: aload_2
    //   54: aload_3
    //   55: invokeinterface 363 2 0
    //   60: aload_2
    //   61: astore_1
    //   62: aload_2
    //   63: astore_0
    //   64: aload_2
    //   65: ldc_w 365
    //   68: invokestatic 371	com/android/internal/util/XmlUtils:beginDocument	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   71: aload_2
    //   72: astore_1
    //   73: aload_2
    //   74: astore_0
    //   75: aload_2
    //   76: aconst_null
    //   77: ldc_w 373
    //   80: invokeinterface 376 3 0
    //   85: invokestatic 131	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   88: istore 5
    //   90: aload_2
    //   91: astore_1
    //   92: aload_2
    //   93: astore_0
    //   94: new 147	java/lang/StringBuilder
    //   97: astore 4
    //   99: aload_2
    //   100: astore_1
    //   101: aload_2
    //   102: astore_0
    //   103: aload 4
    //   105: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   108: aload_2
    //   109: astore_1
    //   110: aload_2
    //   111: astore_0
    //   112: aload 4
    //   114: ldc_w 378
    //   117: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: pop
    //   121: aload_2
    //   122: astore_1
    //   123: aload_2
    //   124: astore_0
    //   125: aload 4
    //   127: iload 5
    //   129: invokevirtual 175	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   132: pop
    //   133: aload_2
    //   134: astore_1
    //   135: aload_2
    //   136: astore_0
    //   137: ldc 50
    //   139: aload 4
    //   141: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   144: invokestatic 381	android/telephony/Rlog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   147: pop
    //   148: iload 5
    //   150: iconst_1
    //   151: if_icmpeq +139 -> 290
    //   154: aload_2
    //   155: astore_1
    //   156: aload_2
    //   157: astore_0
    //   158: new 147	java/lang/StringBuilder
    //   161: astore 4
    //   163: aload_2
    //   164: astore_1
    //   165: aload_2
    //   166: astore_0
    //   167: aload 4
    //   169: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   172: aload_2
    //   173: astore_1
    //   174: aload_2
    //   175: astore_0
    //   176: aload 4
    //   178: ldc_w 383
    //   181: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: pop
    //   185: aload_2
    //   186: astore_1
    //   187: aload_2
    //   188: astore_0
    //   189: aload 4
    //   191: iload 5
    //   193: invokevirtual 175	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   196: pop
    //   197: aload_2
    //   198: astore_1
    //   199: aload_2
    //   200: astore_0
    //   201: aload 4
    //   203: ldc_w 385
    //   206: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: pop
    //   210: aload_2
    //   211: astore_1
    //   212: aload_2
    //   213: astore_0
    //   214: aload 4
    //   216: iconst_1
    //   217: invokevirtual 175	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   220: pop
    //   221: aload_2
    //   222: astore_1
    //   223: aload_2
    //   224: astore_0
    //   225: ldc 50
    //   227: aload 4
    //   229: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   232: invokestatic 164	android/telephony/Rlog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   235: pop
    //   236: aload_2
    //   237: instanceof 387
    //   240: ifeq +49 -> 289
    //   243: aload_2
    //   244: checkcast 387	java/io/Closeable
    //   247: invokeinterface 390 1 0
    //   252: goto +37 -> 289
    //   255: astore_1
    //   256: new 147	java/lang/StringBuilder
    //   259: dup
    //   260: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   263: astore_0
    //   264: aload_0
    //   265: ldc_w 392
    //   268: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   271: pop
    //   272: aload_0
    //   273: aload_1
    //   274: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   277: pop
    //   278: ldc 50
    //   280: aload_0
    //   281: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   284: aload_1
    //   285: invokestatic 395	android/telephony/Rlog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   288: pop
    //   289: return
    //   290: aload_2
    //   291: astore_1
    //   292: aload_2
    //   293: astore_0
    //   294: aload_2
    //   295: invokestatic 399	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   298: aload_2
    //   299: astore_1
    //   300: aload_2
    //   301: astore_0
    //   302: aload_2
    //   303: invokeinterface 402 1 0
    //   308: iconst_1
    //   309: if_icmpeq +41 -> 350
    //   312: aload_2
    //   313: astore_1
    //   314: aload_2
    //   315: astore_0
    //   316: ldc_w 404
    //   319: aload_2
    //   320: invokeinterface 406 1 0
    //   325: invokevirtual 168	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   328: ifeq +11 -> 339
    //   331: aload_2
    //   332: astore_1
    //   333: aload_2
    //   334: astore_0
    //   335: aload_2
    //   336: invokestatic 409	miui/telephony/ServiceProviderUtils:loadSpn	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   339: aload_2
    //   340: astore_1
    //   341: aload_2
    //   342: astore_0
    //   343: aload_2
    //   344: invokestatic 399	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   347: goto -49 -> 298
    //   350: aload_2
    //   351: instanceof 387
    //   354: ifeq +134 -> 488
    //   357: aload_2
    //   358: checkcast 387	java/io/Closeable
    //   361: invokeinterface 390 1 0
    //   366: goto +37 -> 403
    //   369: astore_0
    //   370: new 147	java/lang/StringBuilder
    //   373: dup
    //   374: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   377: astore_1
    //   378: aload_1
    //   379: ldc_w 392
    //   382: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   385: pop
    //   386: aload_1
    //   387: aload_0
    //   388: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   391: pop
    //   392: ldc 50
    //   394: aload_1
    //   395: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   398: aload_0
    //   399: invokestatic 395	android/telephony/Rlog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   402: pop
    //   403: goto +85 -> 488
    //   406: astore_0
    //   407: goto +82 -> 489
    //   410: astore 4
    //   412: aload_0
    //   413: astore_1
    //   414: new 147	java/lang/StringBuilder
    //   417: astore_2
    //   418: aload_0
    //   419: astore_1
    //   420: aload_2
    //   421: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   424: aload_0
    //   425: astore_1
    //   426: aload_2
    //   427: ldc_w 411
    //   430: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   433: pop
    //   434: aload_0
    //   435: astore_1
    //   436: aload_2
    //   437: aload 4
    //   439: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   442: pop
    //   443: aload_0
    //   444: astore_1
    //   445: ldc 50
    //   447: aload_2
    //   448: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   451: aload 4
    //   453: invokestatic 395	android/telephony/Rlog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   456: pop
    //   457: aload_0
    //   458: instanceof 387
    //   461: ifeq +27 -> 488
    //   464: aload_0
    //   465: checkcast 387	java/io/Closeable
    //   468: invokeinterface 390 1 0
    //   473: goto -70 -> 403
    //   476: astore_0
    //   477: new 147	java/lang/StringBuilder
    //   480: dup
    //   481: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   484: astore_1
    //   485: goto -107 -> 378
    //   488: return
    //   489: aload_1
    //   490: instanceof 387
    //   493: ifeq +49 -> 542
    //   496: aload_1
    //   497: checkcast 387	java/io/Closeable
    //   500: invokeinterface 390 1 0
    //   505: goto +37 -> 542
    //   508: astore_2
    //   509: new 147	java/lang/StringBuilder
    //   512: dup
    //   513: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   516: astore_1
    //   517: aload_1
    //   518: ldc_w 392
    //   521: invokevirtual 154	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   524: pop
    //   525: aload_1
    //   526: aload_2
    //   527: invokevirtual 238	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   530: pop
    //   531: ldc 50
    //   533: aload_1
    //   534: invokevirtual 158	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   537: aload_2
    //   538: invokestatic 395	android/telephony/Rlog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   541: pop
    //   542: aload_0
    //   543: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   1	342	0	localObject1	Object
    //   369	30	0	localIOException1	java.io.IOException
    //   406	59	0	localObject2	Object
    //   476	67	0	localIOException2	java.io.IOException
    //   3	220	1	localObject3	Object
    //   255	30	1	localIOException3	java.io.IOException
    //   291	243	1	localObject4	Object
    //   7	441	2	localObject5	Object
    //   508	30	2	localIOException4	java.io.IOException
    //   15	40	3	localFileReader	java.io.FileReader
    //   23	205	4	localObject6	Object
    //   410	42	4	localException	Exception
    //   88	104	5	i	int
    // Exception table:
    //   from	to	target	type
    //   243	252	255	java/io/IOException
    //   357	366	369	java/io/IOException
    //   4	8	406	finally
    //   12	16	406	finally
    //   20	25	406	finally
    //   29	39	406	finally
    //   43	49	406	finally
    //   53	60	406	finally
    //   64	71	406	finally
    //   75	90	406	finally
    //   94	99	406	finally
    //   103	108	406	finally
    //   112	121	406	finally
    //   125	133	406	finally
    //   137	148	406	finally
    //   158	163	406	finally
    //   167	172	406	finally
    //   176	185	406	finally
    //   189	197	406	finally
    //   201	210	406	finally
    //   214	221	406	finally
    //   225	236	406	finally
    //   294	298	406	finally
    //   302	312	406	finally
    //   316	331	406	finally
    //   335	339	406	finally
    //   343	347	406	finally
    //   414	418	406	finally
    //   420	424	406	finally
    //   426	434	406	finally
    //   436	443	406	finally
    //   445	457	406	finally
    //   4	8	410	java/lang/Exception
    //   12	16	410	java/lang/Exception
    //   20	25	410	java/lang/Exception
    //   29	39	410	java/lang/Exception
    //   43	49	410	java/lang/Exception
    //   53	60	410	java/lang/Exception
    //   64	71	410	java/lang/Exception
    //   75	90	410	java/lang/Exception
    //   94	99	410	java/lang/Exception
    //   103	108	410	java/lang/Exception
    //   112	121	410	java/lang/Exception
    //   125	133	410	java/lang/Exception
    //   137	148	410	java/lang/Exception
    //   158	163	410	java/lang/Exception
    //   167	172	410	java/lang/Exception
    //   176	185	410	java/lang/Exception
    //   189	197	410	java/lang/Exception
    //   201	210	410	java/lang/Exception
    //   214	221	410	java/lang/Exception
    //   225	236	410	java/lang/Exception
    //   294	298	410	java/lang/Exception
    //   302	312	410	java/lang/Exception
    //   316	331	410	java/lang/Exception
    //   335	339	410	java/lang/Exception
    //   343	347	410	java/lang/Exception
    //   464	473	476	java/io/IOException
    //   496	505	508	java/io/IOException
  }
  
  private static void loadSpn(XmlPullParser paramXmlPullParser)
  {
    Object localObject1 = null;
    Object localObject2 = paramXmlPullParser.getAttributeValue(null, "numeric");
    if ((localObject2 != null) && (((String)localObject2).length() != 0))
    {
      int[] arrayOfInt = readNumerics((String)localObject2);
      HashMap localHashMap = new HashMap();
      Object localObject3 = new Name(null);
      int i = 0;
      int j = paramXmlPullParser.getAttributeCount();
      while (i < j)
      {
        localObject4 = paramXmlPullParser.getAttributeName(i);
        if ("alpha".equals(localObject4))
        {
          ((Name)localObject3).longAlpha = paramXmlPullParser.getAttributeValue(i);
        }
        else if ("short".equals(localObject4))
        {
          ((Name)localObject3).shortAlpha = paramXmlPullParser.getAttributeValue(i);
        }
        else
        {
          String str;
          if (((String)localObject4).startsWith("alpha-"))
          {
            str = ((String)localObject4).substring("alpha-".length());
            localObject5 = (Name)localHashMap.get(str);
            localObject4 = localObject5;
            if (localObject5 == null)
            {
              localObject4 = new Name(null);
              localHashMap.put(str, localObject4);
            }
            ((Name)localObject4).longAlpha = paramXmlPullParser.getAttributeValue(i);
          }
          else if (((String)localObject4).startsWith("short-"))
          {
            str = ((String)localObject4).substring("short-".length());
            localObject5 = (Name)localHashMap.get(str);
            localObject4 = localObject5;
            if (localObject5 == null)
            {
              localObject4 = new Name(null);
              localHashMap.put(str, localObject4);
            }
            ((Name)localObject4).shortAlpha = paramXmlPullParser.getAttributeValue(i);
          }
        }
        i++;
      }
      if (((Name)localObject3).longAlpha == null) {
        return;
      }
      localHashMap.put("", localObject3);
      Object localObject5 = readConditions(paramXmlPullParser.getAttributeValue(null, "condition"), localHashMap);
      int k;
      if (!"true".equals(paramXmlPullParser.getAttributeValue(null, "mvno")))
      {
        paramXmlPullParser = new ServiceProvider(arrayOfInt, localHashMap, (Condition[])localObject5);
        j = arrayOfInt.length;
        for (i = 0; i < j; i++)
        {
          k = arrayOfInt[i];
          ALL_SERVICE_PROVIDER.put(k, paramXmlPullParser);
        }
        if (DEBUG)
        {
          localObject4 = new StringBuilder();
          ((StringBuilder)localObject4).append("Loaded spn: ");
          ((StringBuilder)localObject4).append(paramXmlPullParser);
          Rlog.d("ServiceProvider", ((StringBuilder)localObject4).toString());
        }
        return;
      }
      localObject3 = new HashSet();
      j = arrayOfInt.length;
      i = 0;
      Object localObject4 = localObject2;
      paramXmlPullParser = (XmlPullParser)localObject1;
      while (i < j)
      {
        k = arrayOfInt[i];
        localObject1 = (ServiceProvider)ALL_SERVICE_PROVIDER.get(k);
        if (localObject1 == null)
        {
          localObject1 = new HashMap();
          ((Map)localObject1).put("", new Name(paramXmlPullParser));
          paramXmlPullParser = null;
          localObject1 = new ServiceProvider(new int[] { k }, (Map)localObject1, null);
          ALL_SERVICE_PROVIDER.put(k, localObject1);
        }
        if (!((HashSet)localObject3).contains(localObject1))
        {
          ((HashSet)localObject3).add(localObject1);
          localObject2 = new VirtualServiceProvider((ServiceProvider)localObject1, localHashMap, (Condition[])localObject5);
          ((ServiceProvider)localObject1).addMvno((VirtualServiceProvider)localObject2);
          if (DEBUG)
          {
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("Loaded virtual spn: ");
            ((StringBuilder)localObject1).append(localObject2);
            Rlog.d("ServiceProvider", ((StringBuilder)localObject1).toString());
          }
        }
        i++;
      }
      return;
    }
  }
  
  private static boolean noNeedOverride(int paramInt)
  {
    boolean bool = false;
    if (isEITSim(paramInt)) {
      bool = true;
    }
    return bool;
  }
  
  public static String onOperatorNumericOrNameSet(int paramInt, String paramString1, String paramString2)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int i = 0;
    String str = paramString1;
    if ("gsm.sim.operator.alpha".equals(str))
    {
      paramString1 = paramString2;
      localObject2 = android.telephony.TelephonyManager.getTelephonyProperty(paramInt, "gsm.sim.operator.numeric", null);
      setTelephonyProperty(paramInt, "gsm.sim.operator.orig.alpha", paramString2);
      localObject1 = str;
    }
    else if ("gsm.sim.operator.numeric".equals(str))
    {
      localObject2 = paramString2;
      paramString1 = android.telephony.TelephonyManager.getTelephonyProperty(paramInt, "gsm.sim.operator.orig.alpha", null);
      i = 1;
      localObject1 = "gsm.sim.operator.alpha";
    }
    else if ("gsm.operator.alpha".equals(str))
    {
      paramString1 = paramString2;
      localObject2 = android.telephony.TelephonyManager.getTelephonyProperty(paramInt, "gsm.operator.numeric", null);
      setTelephonyProperty(paramInt, "gsm.operator.orig.alpha", paramString2);
      localObject1 = str;
    }
    else
    {
      paramString1 = (String)localObject1;
      localObject1 = str;
      if ("gsm.operator.numeric".equals(str))
      {
        localObject2 = paramString2;
        paramString1 = android.telephony.TelephonyManager.getTelephonyProperty(paramInt, "gsm.operator.orig.alpha", null);
        i = 1;
        localObject1 = "gsm.operator.alpha";
      }
    }
    if (localObject2 == null)
    {
      if (i != 0) {
        setTelephonyProperty(paramInt, (String)localObject1, paramString1);
      }
      return paramString2;
    }
    str = get((String)localObject2, paramInt, paramString1, true);
    if (str != null)
    {
      localObject2 = str;
      if (str.length() != 0) {}
    }
    else
    {
      localObject2 = paramString1;
    }
    if (i != 0)
    {
      setTelephonyProperty(paramInt, (String)localObject1, (String)localObject2);
      return paramString2;
    }
    return (String)localObject2;
  }
  
  private static Condition[] readConditions(String paramString, Map<String, Name> paramMap)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    Object localObject1 = new HashMap();
    Object localObject2;
    Object localObject3;
    for (localObject2 : paramString.split(";"))
    {
      localObject3 = ((String)localObject2).split(":");
      if (localObject3.length == 2)
      {
        localObject2 = localObject3[0].trim();
        localObject3 = localObject3[1].trim();
        if ((((String)localObject2).length() > 0) && (((String)localObject3).length() > 0) && (((String)localObject3).charAt(0) != ',')) {
          ((Map)localObject1).put(localObject2, ((String)localObject3).split(","));
        }
      }
      else if ("alpha".equals(((String)localObject2).trim()))
      {
        ((Map)localObject1).put("alpha", new String[0]);
      }
      else if ("numericSpn".equals(((String)localObject2).trim()))
      {
        ((Map)localObject1).put("numericSpn", new String[0]);
      }
    }
    paramString = new ArrayList();
    localObject1 = ((Map)localObject1).entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      if ("alpha".equals(((Map.Entry)localObject2).getKey()))
      {
        localObject3 = new HashSet();
        localObject2 = (String[])((Map.Entry)localObject2).getValue();
        ??? = localObject2.length;
        for (??? = 0; ??? < ???; ???++) {
          ((HashSet)localObject3).add(localObject2[???]);
        }
        Iterator localIterator = paramMap.values().iterator();
        while (localIterator.hasNext())
        {
          localObject2 = (Name)localIterator.next();
          ((HashSet)localObject3).add(((Name)localObject2).longAlpha);
          ((HashSet)localObject3).add(((Name)localObject2).shortAlpha);
        }
        paramString.add(new SpnCondition((String[])((HashSet)localObject3).toArray(new String[((HashSet)localObject3).size()])));
      }
      else if ("imsi".equals(((Map.Entry)localObject2).getKey()))
      {
        paramString.add(new ImsiCondition((String[])((Map.Entry)localObject2).getValue()));
      }
      else if ("gid1".equals(((Map.Entry)localObject2).getKey()))
      {
        paramString.add(new Gid1Condition((String[])((Map.Entry)localObject2).getValue()));
      }
      else if ("numericSpn".equals(((Map.Entry)localObject2).getKey()))
      {
        paramString.add(new NumericSpnCondition(null));
      }
      else if ("pnn".equals(((Map.Entry)localObject2).getKey()))
      {
        paramString.add(new PnnCondition((String[])((Map.Entry)localObject2).getValue()));
      }
    }
    return (Condition[])paramString.toArray(new Condition[paramString.size()]);
  }
  
  private static int[] readNumerics(String paramString)
  {
    Object localObject = paramString.split(",");
    paramString = new SparseIntArray();
    int i = localObject.length;
    for (int j = 0; j < i; j++)
    {
      int k = Integer.parseInt(localObject[j]);
      paramString.put(k, k);
    }
    localObject = new int[paramString.size()];
    for (j = 0; j < localObject.length; j++) {
      localObject[j] = paramString.keyAt(j);
    }
    return (int[])localObject;
  }
  
  private static void setTelephonyProperty(int paramInt, String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    String[] arrayOfString = null;
    Object localObject = SystemProperties.get(paramString1);
    if (localObject != null) {
      arrayOfString = ((String)localObject).split(",");
    }
    if (!SubscriptionManager.isValidSlotId(paramInt)) {
      return;
    }
    for (int i = 0; i < paramInt; i++)
    {
      String str = "";
      localObject = str;
      if (arrayOfString != null)
      {
        localObject = str;
        if (i < arrayOfString.length) {
          localObject = arrayOfString[i];
        }
      }
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(",");
    }
    if (paramString2 == null) {
      paramString2 = "";
    }
    localStringBuilder.append(paramString2);
    if (arrayOfString != null)
    {
      paramInt++;
      while (paramInt < arrayOfString.length)
      {
        localStringBuilder.append(',');
        localStringBuilder.append(arrayOfString[paramInt]);
        paramInt++;
      }
    }
    if ((paramString1.length() <= Integer.MAX_VALUE) && (localStringBuilder.length() <= 91))
    {
      SystemProperties.set(paramString1, localStringBuilder.toString());
      return;
    }
  }
  
  private static abstract class Condition
  {
    public boolean execute(int paramInt1, String paramString, int paramInt2)
    {
      return false;
    }
  }
  
  private static class Gid1Condition
    extends ServiceProviderUtils.Condition
  {
    private final String[] mGid1s;
    
    public Gid1Condition(String[] paramArrayOfString)
    {
      super();
      HashSet localHashSet = new HashSet(paramArrayOfString.length);
      for (int i = 0; i < paramArrayOfString.length; i++) {
        if (paramArrayOfString[i] != null) {
          localHashSet.add(paramArrayOfString[i].trim());
        }
      }
      this.mGid1s = ((String[])localHashSet.toArray(new String[localHashSet.size()]));
    }
    
    public boolean execute(int paramInt1, String paramString, int paramInt2)
    {
      paramString = PhoneFactory.getPhone(paramInt1);
      if ((paramString != null) && (paramString.getIccRecords() != null))
      {
        if (!paramString.getIccRecords().getRecordsLoaded()) {
          return false;
        }
        paramString = paramString.getIccRecords().getGid1();
        if (paramString != null)
        {
          paramString = paramString.trim();
          for (String str : this.mGid1s) {
            if ((paramString.length() >= str.length()) && (paramString.substring(0, str.length()).equalsIgnoreCase(str))) {
              return true;
            }
          }
        }
        return false;
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("CONDITION: when gid1=");
      localStringBuilder.append(TextUtils.join(",", this.mGid1s));
      return localStringBuilder.toString();
    }
  }
  
  private static abstract interface IServiceProvider
  {
    public abstract int getBaseNumeric();
    
    public abstract ServiceProviderUtils.Name getName();
    
    public abstract int[] getNumerics();
    
    public abstract boolean match(int paramInt1, String paramString, int paramInt2);
  }
  
  private static class ImsiCondition
    extends ServiceProviderUtils.Condition
  {
    private final String[] mImsis;
    
    public ImsiCondition(String[] paramArrayOfString)
    {
      super();
      HashSet localHashSet = new HashSet(paramArrayOfString.length);
      for (int i = 0; i < paramArrayOfString.length; i++) {
        if (paramArrayOfString[i] != null) {
          localHashSet.add(paramArrayOfString[i].trim());
        }
      }
      this.mImsis = ((String[])localHashSet.toArray(new String[localHashSet.size()]));
    }
    
    public boolean execute(int paramInt1, String paramString, int paramInt2)
    {
      paramString = PhoneFactory.getPhone(paramInt1);
      if ((paramString != null) && (paramString.getIccRecords() != null))
      {
        if (!paramString.getIccRecords().getRecordsLoaded()) {
          return false;
        }
        String str = paramString.getIccRecords().getIMSI();
        if (str == null) {
          return false;
        }
        paramInt2 = str.length();
        String[] arrayOfString = this.mImsis;
        int i = arrayOfString.length;
        paramInt1 = 0;
        while (paramInt1 < i)
        {
          paramString = arrayOfString[paramInt1];
          if (ServiceProviderUtils.isInRange(paramString, str))
          {
            Rlog.d("ServiceProvider", "matched range imsi.");
            return true;
          }
          if (paramInt2 != paramString.length())
          {
            paramInt1++;
          }
          else
          {
            for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
            {
              i = paramString.charAt(paramInt1);
              if ((i != 120) && (i != 88) && (i != str.charAt(paramInt1))) {
                return false;
              }
            }
            return true;
          }
        }
        return false;
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("CONDITION: when imsi~=");
      localStringBuilder.append(TextUtils.join(",", this.mImsis));
      return localStringBuilder.toString();
    }
  }
  
  private static class Name
  {
    public String longAlpha;
    public String shortAlpha;
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("longAlpha=[");
      localStringBuilder.append(this.longAlpha);
      localStringBuilder.append("], shortAlpha=[");
      localStringBuilder.append(this.shortAlpha);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  private static class NumericSpnCondition
    extends ServiceProviderUtils.Condition
  {
    private NumericSpnCondition()
    {
      super();
    }
    
    public boolean execute(int paramInt1, String paramString, int paramInt2)
    {
      if (TextUtils.isEmpty(paramString)) {
        return true;
      }
      try
      {
        paramInt1 = Integer.parseInt(paramString.trim().replaceAll(" ", ""));
        if (paramInt2 == paramInt1) {
          return true;
        }
      }
      catch (Exception paramString)
      {
        Rlog.w("ServiceProvider", "Exception in execute NumericSpnCondition", paramString);
      }
      return false;
    }
    
    public String toString()
    {
      return "CONDITION: when plmn is numeric";
    }
  }
  
  private static class PnnCondition
    extends ServiceProviderUtils.Condition
  {
    private final String[] mPnns;
    
    public PnnCondition(String[] paramArrayOfString)
    {
      super();
      HashSet localHashSet = new HashSet(paramArrayOfString.length);
      for (int i = 0; i < paramArrayOfString.length; i++) {
        if (paramArrayOfString[i] != null) {
          localHashSet.add(paramArrayOfString[i].trim());
        }
      }
      this.mPnns = ((String[])localHashSet.toArray(new String[localHashSet.size()]));
    }
    
    public boolean execute(int paramInt1, String paramString, int paramInt2)
    {
      paramString = PhoneFactory.getPhone(paramInt1);
      if ((paramString != null) && (paramString.getIccRecords() != null))
      {
        if (!paramString.getIccRecords().getRecordsLoaded()) {
          return false;
        }
        paramString = paramString.getIccRecords().getPnnHomeName();
        if (!TextUtils.isEmpty(paramString))
        {
          paramString = paramString.trim();
          String[] arrayOfString = this.mPnns;
          paramInt2 = arrayOfString.length;
          for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++) {
            if (arrayOfString[paramInt1].equals(paramString)) {
              return true;
            }
          }
        }
        return false;
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("CONDITION: when pnn=");
      localStringBuilder.append(TextUtils.join(",", this.mPnns));
      return localStringBuilder.toString();
    }
  }
  
  private static class ServiceProvider
    extends ServiceProviderUtils.ServiceProviderBase
  {
    private ArrayList<ServiceProviderUtils.VirtualServiceProvider> mvnos;
    private final int[] numerics;
    
    public ServiceProvider(int[] paramArrayOfInt, Map<String, ServiceProviderUtils.Name> paramMap, ServiceProviderUtils.Condition[] paramArrayOfCondition)
    {
      super(paramArrayOfCondition);
      if ((paramArrayOfInt != null) && (paramArrayOfInt.length != 0))
      {
        paramMap = new SparseIntArray();
        int i = paramArrayOfInt.length;
        for (int j = 0; j < i; j++)
        {
          int k = paramArrayOfInt[j];
          paramMap.put(k, k);
        }
        this.numerics = new int[paramMap.size()];
        for (j = 0; j < paramArrayOfInt.length; j++) {
          this.numerics[j] = paramMap.keyAt(j);
        }
        this.mvnos = new ArrayList();
        return;
      }
      throw new IllegalArgumentException("spn should have at least one numeric");
    }
    
    public void addMvno(ServiceProviderUtils.VirtualServiceProvider paramVirtualServiceProvider)
    {
      this.mvnos.add(paramVirtualServiceProvider);
    }
    
    public Collection<ServiceProviderUtils.VirtualServiceProvider> getAllVirtualServiceProvider()
    {
      return this.mvnos;
    }
    
    public int getBaseNumeric()
    {
      return this.numerics[0];
    }
    
    public int[] getNumerics()
    {
      return this.numerics;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("numeric=[");
      for (int i = 0; i < this.numerics.length; i++)
      {
        if (i != 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(this.numerics[i]);
      }
      localStringBuilder.append("], ");
      localStringBuilder.append(super.toString());
      return localStringBuilder.toString();
    }
  }
  
  private static abstract class ServiceProviderBase
    implements ServiceProviderUtils.IServiceProvider
  {
    private final ServiceProviderUtils.Condition[] conditions;
    private Map<String, ServiceProviderUtils.Name> names = new HashMap();
    
    public ServiceProviderBase(Map<String, ServiceProviderUtils.Name> paramMap, ServiceProviderUtils.Condition[] paramArrayOfCondition)
    {
      if ((paramMap != null) && (paramMap.size() != 0) && (paramMap.get("") != null))
      {
        this.names = paramMap;
        this.conditions = paramArrayOfCondition;
        return;
      }
      throw new miui.reflect.IllegalArgumentException("spn should have base name");
    }
    
    public ServiceProviderUtils.Name getName()
    {
      Object localObject1 = null;
      Object localObject2 = Locale.getDefault().toString();
      while ((localObject2 != null) && (localObject1 == null))
      {
        if (ServiceProviderUtils.DEBUG)
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("getName: locale=");
          ((StringBuilder)localObject1).append((String)localObject2);
          Rlog.d("ServiceProvider", ((StringBuilder)localObject1).toString());
        }
        ServiceProviderUtils.Name localName = (ServiceProviderUtils.Name)this.names.get(localObject2);
        int i = ((String)localObject2).lastIndexOf('_');
        if (i >= 0) {
          localObject1 = ((String)localObject2).substring(0, i);
        } else {
          localObject1 = null;
        }
        localObject2 = localObject1;
        localObject1 = localName;
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = (ServiceProviderUtils.Name)this.names.get("");
      }
      return (ServiceProviderUtils.Name)localObject2;
    }
    
    public boolean match(int paramInt1, String paramString, int paramInt2)
    {
      ServiceProviderUtils.Condition[] arrayOfCondition = this.conditions;
      if (arrayOfCondition == null) {
        return true;
      }
      int i = arrayOfCondition.length;
      for (int j = 0; j < i; j++) {
        if (arrayOfCondition[j].execute(paramInt1, paramString, paramInt2)) {
          return true;
        }
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(((ServiceProviderUtils.Name)this.names.get("")).toString());
      int i;
      if (this.names.size() > 1)
      {
        localStringBuilder.append(", {");
        i = 1;
        Iterator localIterator = this.names.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (((String)localEntry.getKey()).length() != 0)
          {
            if (i == 0) {
              localStringBuilder.append(", ");
            }
            i = 0;
            localStringBuilder.append('"');
            localStringBuilder.append((String)localEntry.getKey());
            localStringBuilder.append("\"=>\"");
            localStringBuilder.append(localEntry.getValue());
            localStringBuilder.append('"');
          }
        }
        localStringBuilder.append('}');
      }
      if (this.conditions != null)
      {
        localStringBuilder.append(", condition=[");
        for (i = 0; i < this.conditions.length; i++)
        {
          if (i != 0) {
            localStringBuilder.append(", ");
          }
          localStringBuilder.append(this.conditions[i]);
        }
        localStringBuilder.append("]");
      }
      return localStringBuilder.toString();
    }
  }
  
  private static class SpnCondition
    extends ServiceProviderUtils.Condition
  {
    private final String[] mSpns;
    
    public SpnCondition(String[] paramArrayOfString)
    {
      super();
      HashSet localHashSet = new HashSet(paramArrayOfString.length);
      for (int i = 0; i < paramArrayOfString.length; i++) {
        if (paramArrayOfString[i] != null) {
          localHashSet.add(paramArrayOfString[i].trim());
        }
      }
      this.mSpns = ((String[])localHashSet.toArray(new String[localHashSet.size()]));
    }
    
    public boolean execute(int paramInt1, String paramString, int paramInt2)
    {
      Object localObject = paramString;
      if (TextUtils.isEmpty(paramString))
      {
        localObject = paramString;
        if (SubscriptionManager.isRealSlotId(paramInt1)) {
          localObject = TelephonyManager.getDefault().getSimOperatorNameForSlot(paramInt1);
        }
      }
      if (!TextUtils.isEmpty((CharSequence)localObject))
      {
        paramString = ((String)localObject).trim();
        localObject = this.mSpns;
        paramInt2 = localObject.length;
        for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++) {
          if (localObject[paramInt1].equals(paramString)) {
            return true;
          }
        }
      }
      return false;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("CONDITION: when spn=");
      localStringBuilder.append(TextUtils.join(",", this.mSpns));
      return localStringBuilder.toString();
    }
  }
  
  private static class VirtualServiceProvider
    extends ServiceProviderUtils.ServiceProviderBase
  {
    private final ServiceProviderUtils.ServiceProvider provider;
    
    public VirtualServiceProvider(ServiceProviderUtils.ServiceProvider paramServiceProvider, Map<String, ServiceProviderUtils.Name> paramMap, ServiceProviderUtils.Condition[] paramArrayOfCondition)
    {
      super(paramArrayOfCondition);
      this.provider = paramServiceProvider;
    }
    
    public int getBaseNumeric()
    {
      return this.provider.getBaseNumeric();
    }
    
    public int[] getNumerics()
    {
      return this.provider.getNumerics();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("numeric=[");
      int[] arrayOfInt = this.provider.getNumerics();
      for (int i = 0; i < arrayOfInt.length; i++)
      {
        if (i != 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(arrayOfInt[i]);
      }
      localStringBuilder.append("], ");
      localStringBuilder.append(super.toString());
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/ServiceProviderUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */