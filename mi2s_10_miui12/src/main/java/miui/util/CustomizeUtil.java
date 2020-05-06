package miui.util;

import android.app.AppGlobals;
import android.content.pm.IPackageManager;
import android.graphics.Point;
import android.os.SystemProperties;
import android.view.Display;
import android.view.DisplayInfo;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import miui.os.Build;
import miui.os.MiuiInit;

public class CustomizeUtil
{
  public static final String ADJUST = "adjust";
  public static final String ANDROID_MAX_ASPECT = "android.max_aspect";
  private static final String CUST_VARIANT = "cust_variant";
  private static final File CUST_VARIANT_FILE;
  private static final File DATA_NONCUSTOMIZED_APP_DIR;
  public static final String ENABLE_CONFIG = "enable_config";
  public static final int EXTRA_PRIVATE_FLAG_SPECIAL_MODE = 128;
  public static final boolean HAS_NOTCH;
  public static final float MAX_ASPECT_RATIO = 3.0F;
  private static final File MIUI_APP_DIR;
  private static final File MIUI_CUSTOMIZED_APP_DIR;
  private static final File MIUI_CUSTOMIZED_CUST_DIR;
  private static final File MIUI_CUSTOMIZED_DATA_DIR = new File("/data/miui/");
  private static final File MIUI_CUST_DIR;
  private static final File MIUI_CUST_PROP_DIR;
  public static final String NEED_ADJUST = "need_adjust";
  public static final String NOTCH_CONFIG = "notch.config";
  public static final String PACKAGE = "pkg";
  private static final File PRODUCT_NONCUSTOMIZED_APP_DIR;
  public static final float RESTRICT_ASPECT_RATIO;
  private static final File SYSTEM_NONCUSTOMIZED_APP_DIR;
  public static final int TYPE_DEFAULT = 0;
  public static final int TYPE_METADATA = 1;
  public static final int TYPE_OTHER = 5;
  public static final int TYPE_RESIZEABLE = 2;
  public static final int TYPE_RESTRICT = 4;
  public static final int TYPE_SUGGEST = 3;
  public static final String UPDATE_SPECIAL_MODE = "upate_specail_mode";
  private static final File VENDOR_NONCUSTOMIZED_APP_DIR;
  private static String sCustVariant;
  private static ArrayList<String> sForceLayoutHideNavigationPkgs;
  private static ArrayList<String> sNeedCompatNotchPkgs;
  
  static
  {
    MIUI_CUSTOMIZED_CUST_DIR = new File("/cust/");
    MIUI_CUST_DIR = new File(getMiuiCustomizedDir(), "cust");
    MIUI_CUST_PROP_DIR = new File("/system/cust");
    DATA_NONCUSTOMIZED_APP_DIR = new File("/data/miui/app/noncustomized");
    SYSTEM_NONCUSTOMIZED_APP_DIR = new File("/system/data-app/");
    VENDOR_NONCUSTOMIZED_APP_DIR = new File("/vendor/data-app/");
    PRODUCT_NONCUSTOMIZED_APP_DIR = new File("/product/data-app/");
    MIUI_APP_DIR = new File(getMiuiCustomizedDir(), "app");
    MIUI_CUSTOMIZED_APP_DIR = new File(getMiuiAppDir(), "customized");
    CUST_VARIANT_FILE = getMiuiCustVariantFile();
    sCustVariant = "";
    float f;
    if ("lithium".equals(Build.DEVICE)) {
      f = 1.7777778F;
    } else {
      f = 1.833F;
    }
    RESTRICT_ASPECT_RATIO = f;
    HAS_NOTCH = "1".equals(SystemProperties.get("ro.miui.notch", "0"));
    sForceLayoutHideNavigationPkgs = new ArrayList();
    sNeedCompatNotchPkgs = new ArrayList();
    sForceLayoutHideNavigationPkgs.add("android");
    sForceLayoutHideNavigationPkgs.add("com.android.systemui");
    sForceLayoutHideNavigationPkgs.add("com.android.keyguard");
    sForceLayoutHideNavigationPkgs.add("com.miui.aod");
    sNeedCompatNotchPkgs.add("com.tencent.tmgp.sgame");
    sNeedCompatNotchPkgs.add("com.tencent.tmgp.sgamece");
    sNeedCompatNotchPkgs.add("com.tencent.tmgp.pubgmhd");
    sNeedCompatNotchPkgs.add("com.tencent.tmgp.pubgmhdce");
    sNeedCompatNotchPkgs.add("com.tencent.tmgp.speedmobile");
    sNeedCompatNotchPkgs.add("com.tencent.tmgp.speedmobileEx");
    sNeedCompatNotchPkgs.add("com.tencent.tmgp.cf");
    sNeedCompatNotchPkgs.add("com.tencent.tmgp.pubgm");
    sNeedCompatNotchPkgs.add("com.netease.hyxd.mi");
    sNeedCompatNotchPkgs.add("com.netease.hyxd");
    sNeedCompatNotchPkgs.add("com.netease.dwrg.mi");
    sNeedCompatNotchPkgs.add("com.netease.dwrg");
    sNeedCompatNotchPkgs.add("com.netease.mrzh.mi");
    sNeedCompatNotchPkgs.add("com.netease.mrzh");
    sNeedCompatNotchPkgs.add("com.netease.h48");
    sNeedCompatNotchPkgs.add("com.netease.h48.mi");
  }
  
  public static DisplayInfo adjustDisplay(DisplayInfo paramDisplayInfo, int paramInt, String paramString)
  {
    DisplayInfo localDisplayInfo1 = paramDisplayInfo;
    DisplayInfo localDisplayInfo2 = localDisplayInfo1;
    if (paramInt != 1000)
    {
      localDisplayInfo2 = localDisplayInfo1;
      if (paramInt != 0)
      {
        localDisplayInfo3 = localDisplayInfo1;
        localDisplayInfo2 = localDisplayInfo1;
        try
        {
          if (MiuiInit.isRestrictAspect(paramString))
          {
            localDisplayInfo3 = localDisplayInfo1;
            localDisplayInfo2 = new android/view/DisplayInfo;
            localDisplayInfo3 = localDisplayInfo1;
            localDisplayInfo2.<init>(paramDisplayInfo);
            localDisplayInfo3 = localDisplayInfo2;
            if (localDisplayInfo2.logicalWidth < localDisplayInfo2.logicalHeight)
            {
              localDisplayInfo3 = localDisplayInfo2;
              paramInt = (int)(localDisplayInfo2.logicalWidth * RESTRICT_ASPECT_RATIO + 0.5F);
              localDisplayInfo3 = localDisplayInfo2;
              localDisplayInfo2.logicalHeight = Math.min(localDisplayInfo2.logicalHeight, paramInt);
              localDisplayInfo3 = localDisplayInfo2;
              paramInt = (int)(localDisplayInfo2.appWidth * RESTRICT_ASPECT_RATIO + 0.5F);
              localDisplayInfo3 = localDisplayInfo2;
              localDisplayInfo2.appHeight = Math.min(localDisplayInfo2.appHeight, paramInt);
            }
            else
            {
              localDisplayInfo3 = localDisplayInfo2;
              paramInt = (int)(localDisplayInfo2.logicalHeight * RESTRICT_ASPECT_RATIO + 0.5F);
              localDisplayInfo3 = localDisplayInfo2;
              localDisplayInfo2.logicalWidth = Math.min(localDisplayInfo2.logicalWidth, paramInt);
              localDisplayInfo3 = localDisplayInfo2;
              paramInt = (int)(localDisplayInfo2.appHeight * RESTRICT_ASPECT_RATIO + 0.5F);
              localDisplayInfo3 = localDisplayInfo2;
              localDisplayInfo2.appWidth = Math.min(localDisplayInfo2.appWidth, paramInt);
            }
          }
        }
        catch (Exception paramDisplayInfo)
        {
          Log.w("CustomizeUtil", "ajsustDisplay failed.", paramDisplayInfo);
          break label237;
        }
      }
    }
    DisplayInfo localDisplayInfo3 = localDisplayInfo2;
    label237:
    return localDisplayInfo3;
  }
  
  public static boolean forceLayoutHideNavigation(String paramString)
  {
    return sForceLayoutHideNavigationPkgs.contains(paramString);
  }
  
  private static String getCallingUidPackage(int paramInt)
  {
    if (paramInt > 0) {
      try
      {
        Object localObject = AppGlobals.getPackageManager().getPackagesForUid(paramInt);
        if ((localObject != null) && (localObject.length > 0))
        {
          localObject = localObject[0];
          return (String)localObject;
        }
      }
      catch (Exception localException)
      {
        Log.w("CustomizeUtil", "getCallingUidPackage failed.", localException);
      }
    }
    return null;
  }
  
  public static File getMiuiAppDir()
  {
    return MIUI_APP_DIR;
  }
  
  public static File getMiuiCustDir()
  {
    return MIUI_CUST_DIR;
  }
  
  public static File getMiuiCustPropDir()
  {
    return MIUI_CUST_PROP_DIR;
  }
  
  public static File getMiuiCustVariantDir()
  {
    return getMiuiCustVariantDir(false);
  }
  
  /* Error */
  public static File getMiuiCustVariantDir(boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 295	miui/os/Build:IS_GLOBAL_BUILD	Z
    //   3: ifeq +39 -> 42
    //   6: getstatic 134	miui/util/CustomizeUtil:sCustVariant	Ljava/lang/String;
    //   9: invokestatic 301	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   12: ifne +30 -> 42
    //   15: iload_0
    //   16: ifeq +10 -> 26
    //   19: invokestatic 303	miui/util/CustomizeUtil:getMiuiCustPropDir	()Ljava/io/File;
    //   22: astore_1
    //   23: goto +7 -> 30
    //   26: invokestatic 305	miui/util/CustomizeUtil:getMiuiCustDir	()Ljava/io/File;
    //   29: astore_1
    //   30: new 71	java/io/File
    //   33: dup
    //   34: aload_1
    //   35: getstatic 134	miui/util/CustomizeUtil:sCustVariant	Ljava/lang/String;
    //   38: invokespecial 92	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   41: areturn
    //   42: getstatic 130	miui/util/CustomizeUtil:CUST_VARIANT_FILE	Ljava/io/File;
    //   45: invokevirtual 309	java/io/File:exists	()Z
    //   48: ifeq +640 -> 688
    //   51: aconst_null
    //   52: astore_2
    //   53: aconst_null
    //   54: astore_3
    //   55: aconst_null
    //   56: astore 4
    //   58: aconst_null
    //   59: astore 5
    //   61: aconst_null
    //   62: astore 6
    //   64: aconst_null
    //   65: astore 7
    //   67: aload 4
    //   69: astore_1
    //   70: aload 7
    //   72: astore 8
    //   74: aload_2
    //   75: astore 9
    //   77: aload 5
    //   79: astore 10
    //   81: aload_3
    //   82: astore 11
    //   84: aload 6
    //   86: astore 12
    //   88: new 311	java/io/FileReader
    //   91: astore 13
    //   93: aload 4
    //   95: astore_1
    //   96: aload 7
    //   98: astore 8
    //   100: aload_2
    //   101: astore 9
    //   103: aload 5
    //   105: astore 10
    //   107: aload_3
    //   108: astore 11
    //   110: aload 6
    //   112: astore 12
    //   114: aload 13
    //   116: getstatic 130	miui/util/CustomizeUtil:CUST_VARIANT_FILE	Ljava/io/File;
    //   119: invokespecial 314	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   122: aload 13
    //   124: astore 4
    //   126: aload 4
    //   128: astore_1
    //   129: aload 7
    //   131: astore 8
    //   133: aload 4
    //   135: astore 9
    //   137: aload 5
    //   139: astore 10
    //   141: aload 4
    //   143: astore 11
    //   145: aload 6
    //   147: astore 12
    //   149: new 316	java/io/BufferedReader
    //   152: astore 13
    //   154: aload 4
    //   156: astore_1
    //   157: aload 7
    //   159: astore 8
    //   161: aload 4
    //   163: astore 9
    //   165: aload 5
    //   167: astore 10
    //   169: aload 4
    //   171: astore 11
    //   173: aload 6
    //   175: astore 12
    //   177: aload 13
    //   179: aload 4
    //   181: invokespecial 319	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   184: aload 13
    //   186: astore 7
    //   188: aload 4
    //   190: astore_1
    //   191: aload 7
    //   193: astore 8
    //   195: aload 4
    //   197: astore 9
    //   199: aload 7
    //   201: astore 10
    //   203: aload 4
    //   205: astore 11
    //   207: aload 7
    //   209: astore 12
    //   211: aload 7
    //   213: invokevirtual 323	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   216: astore 5
    //   218: aload 5
    //   220: ifnull +195 -> 415
    //   223: aload 4
    //   225: astore_1
    //   226: aload 7
    //   228: astore 8
    //   230: aload 4
    //   232: astore 9
    //   234: aload 7
    //   236: astore 10
    //   238: aload 4
    //   240: astore 11
    //   242: aload 7
    //   244: astore 12
    //   246: aload 5
    //   248: invokevirtual 326	java/lang/String:trim	()Ljava/lang/String;
    //   251: ldc_w 328
    //   254: ldc -124
    //   256: invokevirtual 332	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   259: astore 6
    //   261: iload_0
    //   262: ifeq +34 -> 296
    //   265: aload 4
    //   267: astore_1
    //   268: aload 7
    //   270: astore 8
    //   272: aload 4
    //   274: astore 9
    //   276: aload 7
    //   278: astore 10
    //   280: aload 4
    //   282: astore 11
    //   284: aload 7
    //   286: astore 12
    //   288: invokestatic 303	miui/util/CustomizeUtil:getMiuiCustPropDir	()Ljava/io/File;
    //   291: astore 5
    //   293: goto +31 -> 324
    //   296: aload 4
    //   298: astore_1
    //   299: aload 7
    //   301: astore 8
    //   303: aload 4
    //   305: astore 9
    //   307: aload 7
    //   309: astore 10
    //   311: aload 4
    //   313: astore 11
    //   315: aload 7
    //   317: astore 12
    //   319: invokestatic 305	miui/util/CustomizeUtil:getMiuiCustDir	()Ljava/io/File;
    //   322: astore 5
    //   324: aload 4
    //   326: astore_1
    //   327: aload 7
    //   329: astore 8
    //   331: aload 4
    //   333: astore 9
    //   335: aload 7
    //   337: astore 10
    //   339: aload 4
    //   341: astore 11
    //   343: aload 7
    //   345: astore 12
    //   347: new 71	java/io/File
    //   350: dup
    //   351: aload 5
    //   353: aload 6
    //   355: invokespecial 92	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   358: astore 5
    //   360: aload 4
    //   362: invokevirtual 335	java/io/FileReader:close	()V
    //   365: aload 7
    //   367: invokevirtual 336	java/io/BufferedReader:close	()V
    //   370: goto +42 -> 412
    //   373: astore_1
    //   374: new 338	java/lang/StringBuilder
    //   377: dup
    //   378: invokespecial 339	java/lang/StringBuilder:<init>	()V
    //   381: astore 8
    //   383: aload 8
    //   385: ldc_w 341
    //   388: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   391: pop
    //   392: aload 8
    //   394: aload_1
    //   395: invokevirtual 348	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   398: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   401: pop
    //   402: ldc -2
    //   404: aload 8
    //   406: invokevirtual 351	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   409: invokestatic 355	miui/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   412: aload 5
    //   414: areturn
    //   415: aload 4
    //   417: invokevirtual 335	java/io/FileReader:close	()V
    //   420: aload 7
    //   422: invokevirtual 336	java/io/BufferedReader:close	()V
    //   425: goto +40 -> 465
    //   428: astore 8
    //   430: new 338	java/lang/StringBuilder
    //   433: dup
    //   434: invokespecial 339	java/lang/StringBuilder:<init>	()V
    //   437: astore_1
    //   438: aload_1
    //   439: ldc_w 341
    //   442: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   445: pop
    //   446: aload_1
    //   447: aload 8
    //   449: invokevirtual 348	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   452: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   455: pop
    //   456: ldc -2
    //   458: aload_1
    //   459: invokevirtual 351	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   462: invokestatic 355	miui/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   465: aconst_null
    //   466: areturn
    //   467: astore 4
    //   469: goto +147 -> 616
    //   472: astore 4
    //   474: aload 9
    //   476: astore_1
    //   477: aload 10
    //   479: astore 8
    //   481: aload 4
    //   483: invokevirtual 358	java/io/IOException:printStackTrace	()V
    //   486: aload 9
    //   488: ifnull +15 -> 503
    //   491: aload 9
    //   493: invokevirtual 335	java/io/FileReader:close	()V
    //   496: goto +7 -> 503
    //   499: astore_1
    //   500: goto +16 -> 516
    //   503: aload 10
    //   505: ifnull +108 -> 613
    //   508: aload 10
    //   510: invokevirtual 336	java/io/BufferedReader:close	()V
    //   513: goto +100 -> 613
    //   516: new 338	java/lang/StringBuilder
    //   519: dup
    //   520: invokespecial 339	java/lang/StringBuilder:<init>	()V
    //   523: astore 8
    //   525: goto +56 -> 581
    //   528: astore 4
    //   530: aload 11
    //   532: astore_1
    //   533: aload 12
    //   535: astore 8
    //   537: aload 4
    //   539: invokevirtual 359	java/io/FileNotFoundException:printStackTrace	()V
    //   542: aload 11
    //   544: ifnull +15 -> 559
    //   547: aload 11
    //   549: invokevirtual 335	java/io/FileReader:close	()V
    //   552: goto +7 -> 559
    //   555: astore_1
    //   556: goto +16 -> 572
    //   559: aload 12
    //   561: ifnull +52 -> 613
    //   564: aload 12
    //   566: invokevirtual 336	java/io/BufferedReader:close	()V
    //   569: goto +44 -> 613
    //   572: new 338	java/lang/StringBuilder
    //   575: dup
    //   576: invokespecial 339	java/lang/StringBuilder:<init>	()V
    //   579: astore 8
    //   581: aload 8
    //   583: ldc_w 341
    //   586: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   589: pop
    //   590: aload 8
    //   592: aload_1
    //   593: invokevirtual 348	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   596: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   599: pop
    //   600: ldc -2
    //   602: aload 8
    //   604: invokevirtual 351	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   607: invokestatic 355	miui/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   610: goto +78 -> 688
    //   613: goto +75 -> 688
    //   616: aload_1
    //   617: ifnull +14 -> 631
    //   620: aload_1
    //   621: invokevirtual 335	java/io/FileReader:close	()V
    //   624: goto +7 -> 631
    //   627: astore_1
    //   628: goto +16 -> 644
    //   631: aload 8
    //   633: ifnull +52 -> 685
    //   636: aload 8
    //   638: invokevirtual 336	java/io/BufferedReader:close	()V
    //   641: goto +44 -> 685
    //   644: new 338	java/lang/StringBuilder
    //   647: dup
    //   648: invokespecial 339	java/lang/StringBuilder:<init>	()V
    //   651: astore 8
    //   653: aload 8
    //   655: ldc_w 341
    //   658: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   661: pop
    //   662: aload 8
    //   664: aload_1
    //   665: invokevirtual 348	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   668: invokevirtual 345	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   671: pop
    //   672: ldc -2
    //   674: aload 8
    //   676: invokevirtual 351	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   679: invokestatic 355	miui/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   682: goto +3 -> 685
    //   685: aload 4
    //   687: athrow
    //   688: aconst_null
    //   689: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	690	0	paramBoolean	boolean
    //   22	305	1	localObject1	Object
    //   373	22	1	localException1	Exception
    //   437	40	1	localObject2	Object
    //   499	1	1	localException2	Exception
    //   532	1	1	localObject3	Object
    //   555	66	1	localException3	Exception
    //   627	38	1	localException4	Exception
    //   52	49	2	localObject4	Object
    //   54	54	3	localObject5	Object
    //   56	360	4	localObject6	Object
    //   467	1	4	localObject7	Object
    //   472	10	4	localIOException	java.io.IOException
    //   528	158	4	localFileNotFoundException	java.io.FileNotFoundException
    //   59	354	5	localObject8	Object
    //   62	292	6	str	String
    //   65	356	7	localObject9	Object
    //   72	333	8	localObject10	Object
    //   428	20	8	localException5	Exception
    //   479	196	8	localObject11	Object
    //   75	417	9	localObject12	Object
    //   79	430	10	localObject13	Object
    //   82	466	11	localObject14	Object
    //   86	479	12	localObject15	Object
    //   91	94	13	localObject16	Object
    // Exception table:
    //   from	to	target	type
    //   360	365	373	java/lang/Exception
    //   365	370	373	java/lang/Exception
    //   415	420	428	java/lang/Exception
    //   420	425	428	java/lang/Exception
    //   88	93	467	finally
    //   114	122	467	finally
    //   149	154	467	finally
    //   177	184	467	finally
    //   211	218	467	finally
    //   246	261	467	finally
    //   288	293	467	finally
    //   319	324	467	finally
    //   347	360	467	finally
    //   481	486	467	finally
    //   537	542	467	finally
    //   88	93	472	java/io/IOException
    //   114	122	472	java/io/IOException
    //   149	154	472	java/io/IOException
    //   177	184	472	java/io/IOException
    //   211	218	472	java/io/IOException
    //   246	261	472	java/io/IOException
    //   288	293	472	java/io/IOException
    //   319	324	472	java/io/IOException
    //   347	360	472	java/io/IOException
    //   491	496	499	java/lang/Exception
    //   508	513	499	java/lang/Exception
    //   88	93	528	java/io/FileNotFoundException
    //   114	122	528	java/io/FileNotFoundException
    //   149	154	528	java/io/FileNotFoundException
    //   177	184	528	java/io/FileNotFoundException
    //   211	218	528	java/io/FileNotFoundException
    //   246	261	528	java/io/FileNotFoundException
    //   288	293	528	java/io/FileNotFoundException
    //   319	324	528	java/io/FileNotFoundException
    //   347	360	528	java/io/FileNotFoundException
    //   547	552	555	java/lang/Exception
    //   564	569	555	java/lang/Exception
    //   620	624	627	java/lang/Exception
    //   636	641	627	java/lang/Exception
  }
  
  public static File getMiuiCustVariantFile()
  {
    if ((Build.HAS_CUST_PARTITION) && (!Build.IS_GLOBAL_BUILD)) {
      return new File(MIUI_CUSTOMIZED_CUST_DIR, "cust_variant");
    }
    return new File(MIUI_CUSTOMIZED_DATA_DIR, "cust_variant");
  }
  
  public static File getMiuiCustomizedAppDir()
  {
    return MIUI_CUSTOMIZED_APP_DIR;
  }
  
  public static File getMiuiCustomizedDir()
  {
    if (Build.HAS_CUST_PARTITION) {
      return MIUI_CUSTOMIZED_CUST_DIR;
    }
    return MIUI_CUSTOMIZED_DATA_DIR;
  }
  
  public static File getMiuiNoCustomizedAppDir()
  {
    if (Build.HAS_CUST_PARTITION) {
      return SYSTEM_NONCUSTOMIZED_APP_DIR;
    }
    return DATA_NONCUSTOMIZED_APP_DIR;
  }
  
  public static File getMiuiProductNoCustomizedAppDir()
  {
    if (Build.HAS_CUST_PARTITION) {
      return PRODUCT_NONCUSTOMIZED_APP_DIR;
    }
    return DATA_NONCUSTOMIZED_APP_DIR;
  }
  
  public static File getMiuiVendorNoCustomizedAppDir()
  {
    if (Build.HAS_CUST_PARTITION) {
      return VENDOR_NONCUSTOMIZED_APP_DIR;
    }
    return DATA_NONCUSTOMIZED_APP_DIR;
  }
  
  public static void getRealSize(Display paramDisplay, Point paramPoint)
  {
    try
    {
      Display.class.getDeclaredMethod("getRealSize", new Class[] { Point.class, Boolean.TYPE }).invoke(paramDisplay, new Object[] { paramPoint, Boolean.valueOf(true) });
    }
    catch (Exception localException)
    {
      Log.w("CustomizeUtil", "no getRealSize hack method");
      paramDisplay.getRealSize(paramPoint);
    }
  }
  
  public static boolean isRestrict(float paramFloat)
  {
    boolean bool;
    if ((paramFloat > 0.0F) && (paramFloat < 3.0F)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean needChangeSize()
  {
    return false;
  }
  
  public static boolean needCompatNotch(String paramString)
  {
    boolean bool;
    if ((HAS_NOTCH) && (sNeedCompatNotchPkgs.contains(paramString))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static void setMiuiCustVariatDir(String paramString)
  {
    sCustVariant = paramString;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/CustomizeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */