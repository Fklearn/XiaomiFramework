package android.webkit;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.app.AppGlobals;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArraySet;
import android.util.Log;
import java.io.File;

@SystemApi
public final class WebViewFactory
{
  private static final String CHROMIUM_WEBVIEW_FACTORY = "com.android.webview.chromium.WebViewChromiumFactoryProviderForQ";
  private static final String CHROMIUM_WEBVIEW_FACTORY_METHOD = "create";
  private static final boolean DEBUG = false;
  public static final int LIBLOAD_ADDRESS_SPACE_NOT_RESERVED = 2;
  public static final int LIBLOAD_FAILED_JNI_CALL = 7;
  public static final int LIBLOAD_FAILED_LISTING_WEBVIEW_PACKAGES = 4;
  public static final int LIBLOAD_FAILED_TO_FIND_NAMESPACE = 10;
  public static final int LIBLOAD_FAILED_TO_LOAD_LIBRARY = 6;
  public static final int LIBLOAD_FAILED_TO_OPEN_RELRO_FILE = 5;
  public static final int LIBLOAD_FAILED_WAITING_FOR_RELRO = 3;
  public static final int LIBLOAD_FAILED_WAITING_FOR_WEBVIEW_REASON_UNKNOWN = 8;
  public static final int LIBLOAD_SUCCESS = 0;
  public static final int LIBLOAD_WRONG_PACKAGE_NAME = 1;
  private static final String LOGTAG = "WebViewFactory";
  private static String WEBVIEW_UPDATE_SERVICE_NAME = "webviewupdate";
  private static String sDataDirectorySuffix;
  @UnsupportedAppUsage
  private static PackageInfo sPackageInfo;
  @UnsupportedAppUsage
  private static WebViewFactoryProvider sProviderInstance;
  private static final Object sProviderLock = new Object();
  private static boolean sWebViewDisabled;
  private static Boolean sWebViewSupported;
  
  static void disableWebView()
  {
    synchronized (sProviderLock)
    {
      if (sProviderInstance == null)
      {
        sWebViewDisabled = true;
        return;
      }
      IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
      localIllegalStateException.<init>("Can't disable WebView: WebView already initialized");
      throw localIllegalStateException;
    }
  }
  
  static String getDataDirectorySuffix()
  {
    synchronized (sProviderLock)
    {
      String str = sDataDirectorySuffix;
      return str;
    }
  }
  
  public static PackageInfo getLoadedPackageInfo()
  {
    synchronized (sProviderLock)
    {
      PackageInfo localPackageInfo = sPackageInfo;
      return localPackageInfo;
    }
  }
  
  /* Error */
  @UnsupportedAppUsage
  static WebViewFactoryProvider getProvider()
  {
    // Byte code:
    //   0: getstatic 60	android/webkit/WebViewFactory:sProviderLock	Ljava/lang/Object;
    //   3: astore_0
    //   4: aload_0
    //   5: monitorenter
    //   6: getstatic 68	android/webkit/WebViewFactory:sProviderInstance	Landroid/webkit/WebViewFactoryProvider;
    //   9: ifnull +11 -> 20
    //   12: getstatic 68	android/webkit/WebViewFactory:sProviderInstance	Landroid/webkit/WebViewFactoryProvider;
    //   15: astore_1
    //   16: aload_0
    //   17: monitorexit
    //   18: aload_1
    //   19: areturn
    //   20: invokestatic 95	android/os/Process:myUid	()I
    //   23: istore_2
    //   24: iload_2
    //   25: ifeq +198 -> 223
    //   28: iload_2
    //   29: sipush 1000
    //   32: if_icmpeq +191 -> 223
    //   35: iload_2
    //   36: sipush 1001
    //   39: if_icmpeq +184 -> 223
    //   42: iload_2
    //   43: sipush 1027
    //   46: if_icmpeq +177 -> 223
    //   49: iload_2
    //   50: sipush 1002
    //   53: if_icmpeq +170 -> 223
    //   56: invokestatic 99	android/webkit/WebViewFactory:isWebViewSupported	()Z
    //   59: ifeq +154 -> 213
    //   62: getstatic 70	android/webkit/WebViewFactory:sWebViewDisabled	Z
    //   65: ifne +136 -> 201
    //   68: ldc2_w 100
    //   71: ldc 103
    //   73: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   76: invokestatic 113	android/webkit/WebViewFactory:getProviderClass	()Ljava/lang/Class;
    //   79: astore_3
    //   80: aconst_null
    //   81: astore_1
    //   82: aload_3
    //   83: ldc 15
    //   85: iconst_1
    //   86: anewarray 115	java/lang/Class
    //   89: dup
    //   90: iconst_0
    //   91: ldc 117
    //   93: aastore
    //   94: invokevirtual 121	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   97: astore_3
    //   98: aload_3
    //   99: astore_1
    //   100: goto +4 -> 104
    //   103: astore_3
    //   104: ldc2_w 100
    //   107: ldc 123
    //   109: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   112: new 117	android/webkit/WebViewDelegate
    //   115: astore_3
    //   116: aload_3
    //   117: invokespecial 124	android/webkit/WebViewDelegate:<init>	()V
    //   120: aload_1
    //   121: aconst_null
    //   122: iconst_1
    //   123: anewarray 4	java/lang/Object
    //   126: dup
    //   127: iconst_0
    //   128: aload_3
    //   129: aastore
    //   130: invokevirtual 130	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   133: checkcast 132	android/webkit/WebViewFactoryProvider
    //   136: putstatic 68	android/webkit/WebViewFactory:sProviderInstance	Landroid/webkit/WebViewFactoryProvider;
    //   139: getstatic 68	android/webkit/WebViewFactory:sProviderInstance	Landroid/webkit/WebViewFactoryProvider;
    //   142: astore_1
    //   143: ldc2_w 100
    //   146: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   149: ldc2_w 100
    //   152: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   155: aload_0
    //   156: monitorexit
    //   157: aload_1
    //   158: areturn
    //   159: astore_1
    //   160: goto +24 -> 184
    //   163: astore_3
    //   164: ldc 41
    //   166: ldc -118
    //   168: aload_3
    //   169: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   172: pop
    //   173: new 146	android/util/AndroidRuntimeException
    //   176: astore_1
    //   177: aload_1
    //   178: aload_3
    //   179: invokespecial 149	android/util/AndroidRuntimeException:<init>	(Ljava/lang/Exception;)V
    //   182: aload_1
    //   183: athrow
    //   184: ldc2_w 100
    //   187: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   190: aload_1
    //   191: athrow
    //   192: astore_1
    //   193: ldc2_w 100
    //   196: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   199: aload_1
    //   200: athrow
    //   201: new 72	java/lang/IllegalStateException
    //   204: astore_1
    //   205: aload_1
    //   206: ldc -105
    //   208: invokespecial 77	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   211: aload_1
    //   212: athrow
    //   213: new 153	java/lang/UnsupportedOperationException
    //   216: astore_1
    //   217: aload_1
    //   218: invokespecial 154	java/lang/UnsupportedOperationException:<init>	()V
    //   221: aload_1
    //   222: athrow
    //   223: new 153	java/lang/UnsupportedOperationException
    //   226: astore_1
    //   227: aload_1
    //   228: ldc -100
    //   230: invokespecial 157	java/lang/UnsupportedOperationException:<init>	(Ljava/lang/String;)V
    //   233: aload_1
    //   234: athrow
    //   235: astore_1
    //   236: aload_0
    //   237: monitorexit
    //   238: aload_1
    //   239: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	234	0	localObject1	Object
    //   15	143	1	localObject2	Object
    //   159	1	1	localObject3	Object
    //   176	15	1	localAndroidRuntimeException	android.util.AndroidRuntimeException
    //   192	8	1	localObject4	Object
    //   204	30	1	localObject5	Object
    //   235	4	1	localObject6	Object
    //   23	31	2	i	int
    //   79	20	3	localObject7	Object
    //   103	1	3	localException1	Exception
    //   115	14	3	localWebViewDelegate	WebViewDelegate
    //   163	16	3	localException2	Exception
    // Exception table:
    //   from	to	target	type
    //   82	98	103	java/lang/Exception
    //   112	143	159	finally
    //   164	184	159	finally
    //   112	143	163	java/lang/Exception
    //   76	80	192	finally
    //   82	98	192	finally
    //   104	112	192	finally
    //   143	149	192	finally
    //   184	192	192	finally
    //   6	18	235	finally
    //   20	24	235	finally
    //   56	76	235	finally
    //   149	157	235	finally
    //   193	201	235	finally
    //   201	213	235	finally
    //   213	223	235	finally
    //   223	235	235	finally
    //   236	238	235	finally
  }
  
  /* Error */
  @UnsupportedAppUsage
  private static Class<WebViewFactoryProvider> getProviderClass()
  {
    // Byte code:
    //   0: invokestatic 166	android/app/AppGlobals:getInitialApplication	()Landroid/app/Application;
    //   3: astore_0
    //   4: ldc2_w 100
    //   7: ldc -88
    //   9: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   12: invokestatic 172	android/webkit/WebViewFactory:getWebViewContextAndSetProvider	()Landroid/content/Context;
    //   15: astore_1
    //   16: ldc2_w 100
    //   19: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   22: new 174	java/lang/StringBuilder
    //   25: astore_2
    //   26: aload_2
    //   27: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   30: aload_2
    //   31: ldc -79
    //   33: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   36: pop
    //   37: aload_2
    //   38: getstatic 85	android/webkit/WebViewFactory:sPackageInfo	Landroid/content/pm/PackageInfo;
    //   41: getfield 186	android/content/pm/PackageInfo:packageName	Ljava/lang/String;
    //   44: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: pop
    //   48: aload_2
    //   49: ldc -68
    //   51: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: pop
    //   55: aload_2
    //   56: getstatic 85	android/webkit/WebViewFactory:sPackageInfo	Landroid/content/pm/PackageInfo;
    //   59: getfield 191	android/content/pm/PackageInfo:versionName	Ljava/lang/String;
    //   62: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   65: pop
    //   66: aload_2
    //   67: ldc -63
    //   69: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   72: pop
    //   73: aload_2
    //   74: getstatic 85	android/webkit/WebViewFactory:sPackageInfo	Landroid/content/pm/PackageInfo;
    //   77: invokevirtual 197	android/content/pm/PackageInfo:getLongVersionCode	()J
    //   80: invokevirtual 200	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   83: pop
    //   84: aload_2
    //   85: ldc -54
    //   87: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: pop
    //   91: ldc 41
    //   93: aload_2
    //   94: invokevirtual 205	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   97: invokestatic 209	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   100: pop
    //   101: ldc2_w 100
    //   104: ldc -45
    //   106: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   109: aload_1
    //   110: invokevirtual 217	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   113: invokevirtual 223	android/content/pm/ApplicationInfo:getAllApkPaths	()[Ljava/lang/String;
    //   116: astore_3
    //   117: aload_3
    //   118: arraylength
    //   119: istore 4
    //   121: iconst_0
    //   122: istore 5
    //   124: iload 5
    //   126: iload 4
    //   128: if_icmpge +23 -> 151
    //   131: aload_3
    //   132: iload 5
    //   134: aaload
    //   135: astore_2
    //   136: aload_0
    //   137: invokevirtual 229	android/app/Application:getAssets	()Landroid/content/res/AssetManager;
    //   140: aload_2
    //   141: invokevirtual 235	android/content/res/AssetManager:addAssetPathAsSharedLibrary	(Ljava/lang/String;)I
    //   144: pop
    //   145: iinc 5 1
    //   148: goto -24 -> 124
    //   151: aload_1
    //   152: invokevirtual 239	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   155: astore_1
    //   156: ldc2_w 100
    //   159: ldc -15
    //   161: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   164: aload_1
    //   165: getstatic 85	android/webkit/WebViewFactory:sPackageInfo	Landroid/content/pm/PackageInfo;
    //   168: getfield 245	android/content/pm/PackageInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   171: invokestatic 249	android/webkit/WebViewFactory:getWebViewLibrary	(Landroid/content/pm/ApplicationInfo;)Ljava/lang/String;
    //   174: invokestatic 255	android/webkit/WebViewLibraryLoader:loadNativeLibrary	(Ljava/lang/ClassLoader;Ljava/lang/String;)I
    //   177: pop
    //   178: ldc2_w 100
    //   181: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   184: ldc2_w 100
    //   187: ldc_w 257
    //   190: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   193: aload_1
    //   194: invokestatic 261	android/webkit/WebViewFactory:getWebViewProviderClass	(Ljava/lang/ClassLoader;)Ljava/lang/Class;
    //   197: astore_1
    //   198: ldc2_w 100
    //   201: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   204: ldc2_w 100
    //   207: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   210: aload_1
    //   211: areturn
    //   212: astore_1
    //   213: ldc2_w 100
    //   216: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   219: aload_1
    //   220: athrow
    //   221: astore_1
    //   222: goto +25 -> 247
    //   225: astore_0
    //   226: ldc 41
    //   228: ldc_w 263
    //   231: aload_0
    //   232: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   235: pop
    //   236: new 146	android/util/AndroidRuntimeException
    //   239: astore_1
    //   240: aload_1
    //   241: aload_0
    //   242: invokespecial 149	android/util/AndroidRuntimeException:<init>	(Ljava/lang/Exception;)V
    //   245: aload_1
    //   246: athrow
    //   247: ldc2_w 100
    //   250: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   253: aload_1
    //   254: athrow
    //   255: astore_1
    //   256: ldc2_w 100
    //   259: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   262: aload_1
    //   263: athrow
    //   264: astore_1
    //   265: ldc 41
    //   267: ldc_w 265
    //   270: aload_1
    //   271: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   274: pop
    //   275: new 146	android/util/AndroidRuntimeException
    //   278: dup
    //   279: aload_1
    //   280: invokespecial 149	android/util/AndroidRuntimeException:<init>	(Ljava/lang/Exception;)V
    //   283: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	134	0	localApplication	Application
    //   225	17	0	localClassNotFoundException	ClassNotFoundException
    //   15	196	1	localObject1	Object
    //   212	8	1	localObject2	Object
    //   221	1	1	localObject3	Object
    //   239	15	1	localAndroidRuntimeException	android.util.AndroidRuntimeException
    //   255	8	1	localObject4	Object
    //   264	16	1	localMissingWebViewPackageException	MissingWebViewPackageException
    //   25	116	2	localStringBuilder	StringBuilder
    //   116	16	3	arrayOfString	String[]
    //   119	10	4	i	int
    //   122	24	5	j	int
    // Exception table:
    //   from	to	target	type
    //   193	198	212	finally
    //   109	121	221	finally
    //   136	145	221	finally
    //   151	193	221	finally
    //   198	204	221	finally
    //   213	221	221	finally
    //   226	247	221	finally
    //   109	121	225	java/lang/ClassNotFoundException
    //   136	145	225	java/lang/ClassNotFoundException
    //   151	193	225	java/lang/ClassNotFoundException
    //   198	204	225	java/lang/ClassNotFoundException
    //   213	221	225	java/lang/ClassNotFoundException
    //   12	16	255	finally
    //   4	12	264	android/webkit/WebViewFactory$MissingWebViewPackageException
    //   16	22	264	android/webkit/WebViewFactory$MissingWebViewPackageException
    //   22	109	264	android/webkit/WebViewFactory$MissingWebViewPackageException
    //   204	210	264	android/webkit/WebViewFactory$MissingWebViewPackageException
    //   247	255	264	android/webkit/WebViewFactory$MissingWebViewPackageException
    //   256	264	264	android/webkit/WebViewFactory$MissingWebViewPackageException
  }
  
  @UnsupportedAppUsage
  public static IWebViewUpdateService getUpdateService()
  {
    if (isWebViewSupported()) {
      return getUpdateServiceUnchecked();
    }
    return null;
  }
  
  static IWebViewUpdateService getUpdateServiceUnchecked()
  {
    return IWebViewUpdateService.Stub.asInterface(ServiceManager.getService(WEBVIEW_UPDATE_SERVICE_NAME));
  }
  
  /* Error */
  @UnsupportedAppUsage
  private static android.content.Context getWebViewContextAndSetProvider()
    throws WebViewFactory.MissingWebViewPackageException
  {
    // Byte code:
    //   0: invokestatic 166	android/app/AppGlobals:getInitialApplication	()Landroid/app/Application;
    //   3: astore_0
    //   4: ldc2_w 100
    //   7: ldc_w 290
    //   10: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   13: invokestatic 292	android/webkit/WebViewFactory:getUpdateService	()Landroid/webkit/IWebViewUpdateService;
    //   16: invokeinterface 298 1 0
    //   21: astore_1
    //   22: ldc2_w 100
    //   25: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   28: aload_1
    //   29: getfield 303	android/webkit/WebViewProviderResponse:status	I
    //   32: ifeq +56 -> 88
    //   35: aload_1
    //   36: getfield 303	android/webkit/WebViewProviderResponse:status	I
    //   39: iconst_3
    //   40: if_icmpne +6 -> 46
    //   43: goto +45 -> 88
    //   46: new 6	android/webkit/WebViewFactory$MissingWebViewPackageException
    //   49: astore_0
    //   50: new 174	java/lang/StringBuilder
    //   53: astore_2
    //   54: aload_2
    //   55: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   58: aload_2
    //   59: ldc_w 305
    //   62: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   65: pop
    //   66: aload_2
    //   67: aload_1
    //   68: getfield 303	android/webkit/WebViewProviderResponse:status	I
    //   71: invokestatic 309	android/webkit/WebViewFactory:getWebViewPreparationErrorReason	(I)Ljava/lang/String;
    //   74: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: pop
    //   78: aload_0
    //   79: aload_2
    //   80: invokevirtual 205	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   83: invokespecial 310	android/webkit/WebViewFactory$MissingWebViewPackageException:<init>	(Ljava/lang/String;)V
    //   86: aload_0
    //   87: athrow
    //   88: ldc2_w 100
    //   91: ldc_w 312
    //   94: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   97: invokestatic 317	android/app/ActivityManager:getService	()Landroid/app/IActivityManager;
    //   100: aload_1
    //   101: getfield 320	android/webkit/WebViewProviderResponse:packageInfo	Landroid/content/pm/PackageInfo;
    //   104: getfield 186	android/content/pm/PackageInfo:packageName	Ljava/lang/String;
    //   107: invokeinterface 325 2 0
    //   112: ldc2_w 100
    //   115: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   118: aload_0
    //   119: invokevirtual 329	android/app/Application:getPackageManager	()Landroid/content/pm/PackageManager;
    //   122: astore_2
    //   123: ldc2_w 100
    //   126: ldc_w 331
    //   129: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   132: aload_2
    //   133: aload_1
    //   134: getfield 320	android/webkit/WebViewProviderResponse:packageInfo	Landroid/content/pm/PackageInfo;
    //   137: getfield 186	android/content/pm/PackageInfo:packageName	Ljava/lang/String;
    //   140: ldc_w 332
    //   143: invokevirtual 338	android/content/pm/PackageManager:getPackageInfo	(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
    //   146: astore_2
    //   147: ldc2_w 100
    //   150: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   153: aload_1
    //   154: getfield 320	android/webkit/WebViewProviderResponse:packageInfo	Landroid/content/pm/PackageInfo;
    //   157: aload_2
    //   158: invokestatic 342	android/webkit/WebViewFactory:verifyPackageInfo	(Landroid/content/pm/PackageInfo;Landroid/content/pm/PackageInfo;)V
    //   161: aload_2
    //   162: getfield 245	android/content/pm/PackageInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   165: astore_1
    //   166: ldc2_w 100
    //   169: ldc_w 344
    //   172: invokestatic 109	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   175: aload_0
    //   176: aload_1
    //   177: iconst_3
    //   178: invokevirtual 348	android/app/Application:createApplicationContext	(Landroid/content/pm/ApplicationInfo;I)Landroid/content/Context;
    //   181: astore_1
    //   182: aload_2
    //   183: putstatic 85	android/webkit/WebViewFactory:sPackageInfo	Landroid/content/pm/PackageInfo;
    //   186: ldc2_w 100
    //   189: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   192: aload_1
    //   193: areturn
    //   194: astore_1
    //   195: ldc2_w 100
    //   198: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   201: aload_1
    //   202: athrow
    //   203: astore_1
    //   204: ldc2_w 100
    //   207: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   210: aload_1
    //   211: athrow
    //   212: astore_1
    //   213: ldc2_w 100
    //   216: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   219: aload_1
    //   220: athrow
    //   221: astore_1
    //   222: ldc2_w 100
    //   225: invokestatic 136	android/os/Trace:traceEnd	(J)V
    //   228: aload_1
    //   229: athrow
    //   230: astore_1
    //   231: new 174	java/lang/StringBuilder
    //   234: dup
    //   235: invokespecial 175	java/lang/StringBuilder:<init>	()V
    //   238: astore_0
    //   239: aload_0
    //   240: ldc_w 305
    //   243: invokevirtual 181	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: pop
    //   247: aload_0
    //   248: aload_1
    //   249: invokevirtual 351	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   252: pop
    //   253: new 6	android/webkit/WebViewFactory$MissingWebViewPackageException
    //   256: dup
    //   257: aload_0
    //   258: invokevirtual 205	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   261: invokespecial 310	android/webkit/WebViewFactory$MissingWebViewPackageException:<init>	(Ljava/lang/String;)V
    //   264: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	255	0	localObject1	Object
    //   21	172	1	localObject2	Object
    //   194	8	1	localObject3	Object
    //   203	8	1	localObject4	Object
    //   212	8	1	localObject5	Object
    //   221	8	1	localObject6	Object
    //   230	19	1	localRemoteException	RemoteException
    //   53	130	2	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   175	186	194	finally
    //   132	147	203	finally
    //   97	112	212	finally
    //   13	22	221	finally
    //   4	13	230	android/os/RemoteException
    //   4	13	230	android/content/pm/PackageManager$NameNotFoundException
    //   22	28	230	android/os/RemoteException
    //   22	28	230	android/content/pm/PackageManager$NameNotFoundException
    //   28	43	230	android/os/RemoteException
    //   28	43	230	android/content/pm/PackageManager$NameNotFoundException
    //   46	88	230	android/os/RemoteException
    //   46	88	230	android/content/pm/PackageManager$NameNotFoundException
    //   88	97	230	android/os/RemoteException
    //   88	97	230	android/content/pm/PackageManager$NameNotFoundException
    //   112	118	230	android/os/RemoteException
    //   112	118	230	android/content/pm/PackageManager$NameNotFoundException
    //   118	132	230	android/os/RemoteException
    //   118	132	230	android/content/pm/PackageManager$NameNotFoundException
    //   147	153	230	android/os/RemoteException
    //   147	153	230	android/content/pm/PackageManager$NameNotFoundException
    //   153	175	230	android/os/RemoteException
    //   153	175	230	android/content/pm/PackageManager$NameNotFoundException
    //   186	192	230	android/os/RemoteException
    //   186	192	230	android/content/pm/PackageManager$NameNotFoundException
    //   195	203	230	android/os/RemoteException
    //   195	203	230	android/content/pm/PackageManager$NameNotFoundException
    //   204	212	230	android/os/RemoteException
    //   204	212	230	android/content/pm/PackageManager$NameNotFoundException
    //   213	221	230	android/os/RemoteException
    //   213	221	230	android/content/pm/PackageManager$NameNotFoundException
    //   222	230	230	android/os/RemoteException
    //   222	230	230	android/content/pm/PackageManager$NameNotFoundException
  }
  
  public static String getWebViewLibrary(ApplicationInfo paramApplicationInfo)
  {
    if (paramApplicationInfo.metaData != null) {
      return paramApplicationInfo.metaData.getString("com.android.webview.WebViewLibrary");
    }
    return null;
  }
  
  private static String getWebViewPreparationErrorReason(int paramInt)
  {
    if (paramInt != 3)
    {
      if (paramInt != 4)
      {
        if (paramInt != 8) {
          return "Unknown";
        }
        return "Crashed for unknown reason";
      }
      return "No WebView installed";
    }
    return "Time out waiting for Relro files being created";
  }
  
  public static Class<WebViewFactoryProvider> getWebViewProviderClass(ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    return Class.forName("com.android.webview.chromium.WebViewChromiumFactoryProviderForQ", true, paramClassLoader);
  }
  
  private static boolean isWebViewSupported()
  {
    if (sWebViewSupported == null) {
      sWebViewSupported = Boolean.valueOf(AppGlobals.getInitialApplication().getPackageManager().hasSystemFeature("android.software.webview"));
    }
    return sWebViewSupported.booleanValue();
  }
  
  public static int loadWebViewNativeLibraryFromPackage(String paramString, ClassLoader paramClassLoader)
  {
    if (!isWebViewSupported()) {
      return 1;
    }
    try
    {
      WebViewProviderResponse localWebViewProviderResponse = getUpdateService().waitForAndGetProvider();
      if ((localWebViewProviderResponse.status != 0) && (localWebViewProviderResponse.status != 3)) {
        return localWebViewProviderResponse.status;
      }
      if (!localWebViewProviderResponse.packageInfo.packageName.equals(paramString)) {
        return 1;
      }
      Object localObject = AppGlobals.getInitialApplication().getPackageManager();
      try
      {
        localObject = getWebViewLibrary(((PackageManager)localObject).getPackageInfo(paramString, 268435584).applicationInfo);
        int i = WebViewLibraryLoader.loadNativeLibrary(paramClassLoader, (String)localObject);
        if (i == 0) {
          return localWebViewProviderResponse.status;
        }
        return i;
      }
      catch (PackageManager.NameNotFoundException paramClassLoader)
      {
        paramClassLoader = new StringBuilder();
        paramClassLoader.append("Couldn't find package ");
        paramClassLoader.append(paramString);
        Log.e("WebViewFactory", paramClassLoader.toString());
        return 1;
      }
      return 8;
    }
    catch (RemoteException paramString)
    {
      Log.e("WebViewFactory", "error waiting for relro creation", paramString);
    }
  }
  
  /* Error */
  public static int onWebViewProviderChanged(PackageInfo paramPackageInfo)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: invokestatic 414	android/webkit/WebViewLibraryLoader:prepareNativeLibraries	(Landroid/content/pm/PackageInfo;)I
    //   6: istore_2
    //   7: iload_2
    //   8: istore_1
    //   9: goto +14 -> 23
    //   12: astore_3
    //   13: ldc 41
    //   15: ldc_w 416
    //   18: aload_3
    //   19: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   22: pop
    //   23: aload_0
    //   24: invokestatic 421	android/webkit/WebViewZygote:onWebViewProviderChanged	(Landroid/content/pm/PackageInfo;)V
    //   27: iload_1
    //   28: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	29	0	paramPackageInfo	PackageInfo
    //   1	27	1	i	int
    //   6	2	2	j	int
    //   12	7	3	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   2	7	12	finally
  }
  
  /* Error */
  public static void prepareWebViewInZygote()
  {
    // Byte code:
    //   0: invokestatic 425	android/webkit/WebViewLibraryLoader:reserveAddressSpaceInZygote	()V
    //   3: goto +14 -> 17
    //   6: astore_0
    //   7: ldc 41
    //   9: ldc_w 427
    //   12: aload_0
    //   13: invokestatic 144	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   16: pop
    //   17: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   6	7	0	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   0	3	6	finally
  }
  
  static void setDataDirectorySuffix(String paramString)
  {
    synchronized (sProviderLock)
    {
      if (sProviderInstance == null)
      {
        if (paramString.indexOf(File.separatorChar) < 0)
        {
          sDataDirectorySuffix = paramString;
          return;
        }
        IllegalArgumentException localIllegalArgumentException = new java/lang/IllegalArgumentException;
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localStringBuilder.append("Suffix ");
        localStringBuilder.append(paramString);
        localStringBuilder.append(" contains a path separator");
        localIllegalArgumentException.<init>(localStringBuilder.toString());
        throw localIllegalArgumentException;
      }
      paramString = new java/lang/IllegalStateException;
      paramString.<init>("Can't set data directory suffix: WebView already initialized");
      throw paramString;
    }
  }
  
  private static boolean signaturesEquals(Signature[] paramArrayOfSignature1, Signature[] paramArrayOfSignature2)
  {
    int i = 0;
    boolean bool = false;
    if (paramArrayOfSignature1 == null)
    {
      if (paramArrayOfSignature2 == null) {
        bool = true;
      }
      return bool;
    }
    if (paramArrayOfSignature2 == null) {
      return false;
    }
    ArraySet localArraySet = new ArraySet();
    int j = paramArrayOfSignature1.length;
    for (int k = 0; k < j; k++) {
      localArraySet.add(paramArrayOfSignature1[k]);
    }
    paramArrayOfSignature1 = new ArraySet();
    j = paramArrayOfSignature2.length;
    for (k = i; k < j; k++) {
      paramArrayOfSignature1.add(paramArrayOfSignature2[k]);
    }
    return localArraySet.equals(paramArrayOfSignature1);
  }
  
  private static void verifyPackageInfo(PackageInfo paramPackageInfo1, PackageInfo paramPackageInfo2)
    throws WebViewFactory.MissingWebViewPackageException
  {
    if (paramPackageInfo1.packageName.equals(paramPackageInfo2.packageName))
    {
      if (paramPackageInfo1.getLongVersionCode() <= paramPackageInfo2.getLongVersionCode())
      {
        if (getWebViewLibrary(paramPackageInfo2.applicationInfo) != null)
        {
          if (signaturesEquals(paramPackageInfo1.signatures, paramPackageInfo2.signatures)) {
            return;
          }
          throw new MissingWebViewPackageException("Failed to verify WebView provider, signature mismatch");
        }
        paramPackageInfo1 = new StringBuilder();
        paramPackageInfo1.append("Tried to load an invalid WebView provider: ");
        paramPackageInfo1.append(paramPackageInfo2.packageName);
        throw new MissingWebViewPackageException(paramPackageInfo1.toString());
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Failed to verify WebView provider, version code is lower than expected: ");
      localStringBuilder.append(paramPackageInfo1.getLongVersionCode());
      localStringBuilder.append(" actual: ");
      localStringBuilder.append(paramPackageInfo2.getLongVersionCode());
      throw new MissingWebViewPackageException(localStringBuilder.toString());
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Failed to verify WebView provider, packageName mismatch, expected: ");
    localStringBuilder.append(paramPackageInfo1.packageName);
    localStringBuilder.append(" actual: ");
    localStringBuilder.append(paramPackageInfo2.packageName);
    throw new MissingWebViewPackageException(localStringBuilder.toString());
  }
  
  static class MissingWebViewPackageException
    extends Exception
  {
    public MissingWebViewPackageException(Exception paramException)
    {
      super();
    }
    
    public MissingWebViewPackageException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */