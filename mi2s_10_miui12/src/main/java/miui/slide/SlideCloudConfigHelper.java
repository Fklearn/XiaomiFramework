package miui.slide;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.MiuiSettings.SettingsCloudData;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miui.process.IActivityChangeListener;
import miui.process.ProcessManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SlideCloudConfigHelper
{
  private static final String CloudConfigFilePath = "/data/system/mirihi-config-cloud.json";
  private static final String ConfigFilePath = "/system/etc/mirihi-config.json";
  private static final String TAG = "SlideCloudConfigHelper";
  private static volatile SlideCloudConfigHelper sCloudConfigHelper = null;
  private IActivityChangeListener mActivityChangeListener;
  private HashMap<String, AppSlideConfig> mAppSlideConfigs = new HashMap();
  private PackageManager mPackageManager;
  PackageMonitor mPackageMonitor = new PackageMonitor()
  {
    public void onPackageAdded(String paramAnonymousString, int paramAnonymousInt)
    {
      if (SlideCloudConfigHelper.this.mSlide3rdPackageList.contains(paramAnonymousString))
      {
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append("onPackageAdded ");
        ((StringBuilder)localObject).append(paramAnonymousString);
        Slog.d("SlideCloudConfigHelper", ((StringBuilder)localObject).toString());
        try
        {
          localObject = SlideCloudConfigHelper.this.mPackageManager.getPackageInfo(paramAnonymousString, 0);
          if (localObject != null) {
            SlideCloudConfigHelper.this.parseSlideConfigFile(false, paramAnonymousString, ((PackageInfo)localObject).versionCode);
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          Slog.w("SlideCloudConfigHelper", localNameNotFoundException);
        }
        SlideCloudConfigHelper.this.registerSlideActivityChangeListener();
      }
      super.onPackageAdded(paramAnonymousString, paramAnonymousInt);
    }
    
    public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
    {
      if (SlideCloudConfigHelper.this.mSlide3rdPackageList.contains(paramAnonymousString))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("onPackageRemoved ");
        localStringBuilder.append(paramAnonymousString);
        Slog.d("SlideCloudConfigHelper", localStringBuilder.toString());
        SlideCloudConfigHelper.this.mAppSlideConfigs.remove(paramAnonymousString);
      }
      super.onPackageRemoved(paramAnonymousString, paramAnonymousInt);
    }
    
    public void onPackageUpdateFinished(String paramAnonymousString, int paramAnonymousInt)
    {
      if (SlideCloudConfigHelper.this.mSlide3rdPackageList.contains(paramAnonymousString))
      {
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append("onPackageUpdateFinished ");
        ((StringBuilder)localObject).append(paramAnonymousString);
        Slog.d("SlideCloudConfigHelper", ((StringBuilder)localObject).toString());
        try
        {
          localObject = SlideCloudConfigHelper.this.mPackageManager.getPackageInfo(paramAnonymousString, 0);
          if (localObject != null) {
            SlideCloudConfigHelper.this.parseSlideConfigFile(false, paramAnonymousString, ((PackageInfo)localObject).versionCode);
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          Slog.w("SlideCloudConfigHelper", localNameNotFoundException);
        }
      }
      super.onPackageUpdateFinished(paramAnonymousString, paramAnonymousInt);
    }
  };
  private ArrayList<String> mSlide3rdPackageList = new ArrayList();
  private ArrayList<String> mSlideSystemPackageList = new ArrayList();
  
  private SlideCloudConfigHelper()
  {
    this.mSlideSystemPackageList.add("com.android.camera");
    this.mSlideSystemPackageList.add("com.android.phone");
    this.mSlideSystemPackageList.add("com.miui.home");
    this.mSlideSystemPackageList.add("com.android.incallui");
    this.mSlideSystemPackageList.add("com.android.contacts");
    this.mSlideSystemPackageList.add("com.example.xcm.test");
  }
  
  private AppSlideConfig generateAppSlideConfigFromJson(JSONObject paramJSONObject)
  {
    if (paramJSONObject == null) {
      return null;
    }
    try
    {
      Object localObject1 = paramJSONObject.getString("pkg");
      JSONArray localJSONArray = paramJSONObject.getJSONArray("actionConfigs");
      if ((localJSONArray != null) && (localJSONArray.length() > 0))
      {
        AppSlideConfig localAppSlideConfig = new miui/slide/AppSlideConfig;
        localAppSlideConfig.<init>((String)localObject1);
        int i = 0;
        int j = 0;
        paramJSONObject = (JSONObject)localObject1;
        while (j < localJSONArray.length())
        {
          localObject1 = localJSONArray.getJSONObject(j);
          int k = ((JSONObject)localObject1).getInt("keyCode");
          String str1 = ((JSONObject)localObject1).getString("version");
          String str2 = ((JSONObject)localObject1).getString("startActivity");
          int m = ((JSONObject)localObject1).getInt("flagAction");
          int n = ((JSONObject)localObject1).getInt("flagResult");
          int i1 = ((JSONObject)localObject1).getInt("flagCondition");
          boolean bool1 = ((JSONObject)localObject1).getBoolean("condition");
          String str3 = ((JSONObject)localObject1).getString("viewID");
          String str4 = ((JSONObject)localObject1).getString("viewClassName");
          String str5 = ((JSONObject)localObject1).getString("targetActivity");
          ArrayList localArrayList = new java/util/ArrayList;
          localArrayList.<init>();
          Object localObject2 = ((JSONObject)localObject1).getString("touchEvent");
          try
          {
            boolean bool2 = TextUtils.isEmpty((CharSequence)localObject2);
            int i2;
            if (!bool2)
            {
              localObject1 = paramJSONObject;
              try
              {
                localObject2 = ((String)localObject2).split(";");
                localObject1 = paramJSONObject;
                i2 = localObject2.length;
                for (i = 0; i < i2; i++)
                {
                  localObject1 = paramJSONObject;
                  String[] arrayOfString = localObject2[i].trim().split(",");
                  localObject1 = paramJSONObject;
                  int i3 = arrayOfString.length;
                  if (i3 == 5) {
                    try
                    {
                      i3 = Integer.parseInt(arrayOfString[0]);
                      int i4 = Integer.parseInt(arrayOfString[1]);
                      int i5 = Integer.parseInt(arrayOfString[2]);
                      int i6 = Integer.parseInt(arrayOfString[3]);
                      int i7 = Integer.parseInt(arrayOfString[4]);
                      localObject1 = new miui/slide/SlideConfig$TouchEventConfig;
                      ((SlideConfig.TouchEventConfig)localObject1).<init>(i3, i4, i5, i6, i7);
                      localArrayList.add(localObject1);
                    }
                    catch (Exception paramJSONObject)
                    {
                      break label394;
                    }
                  }
                }
                i = 0;
              }
              catch (Exception paramJSONObject)
              {
                break label394;
              }
            }
            localObject1 = new miui/slide/SlideConfig;
            if (str1.isEmpty()) {
              i2 = i;
            } else {
              i2 = Integer.parseInt(str1);
            }
            ((SlideConfig)localObject1).<init>(k, i2, str2, m, n, i1, bool1, str3, str4, localArrayList, str5);
            localAppSlideConfig.addSlideConfig((SlideConfig)localObject1);
            j++;
          }
          catch (Exception paramJSONObject)
          {
            label394:
            Slog.w("SlideCloudConfigHelper", paramJSONObject);
            return null;
          }
        }
        return localAppSlideConfig;
      }
    }
    catch (JSONException paramJSONObject)
    {
      Slog.w("SlideCloudConfigHelper", paramJSONObject);
    }
    return null;
  }
  
  public static SlideCloudConfigHelper getInstance()
  {
    if (sCloudConfigHelper == null) {
      try
      {
        if (sCloudConfigHelper == null)
        {
          SlideCloudConfigHelper localSlideCloudConfigHelper = new miui/slide/SlideCloudConfigHelper;
          localSlideCloudConfigHelper.<init>();
          sCloudConfigHelper = localSlideCloudConfigHelper;
        }
      }
      finally {}
    }
    return sCloudConfigHelper;
  }
  
  /* Error */
  private StringBuilder parseFile(File paramFile)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +149 -> 150
    //   4: aload_1
    //   5: invokevirtual 220	java/io/File:exists	()Z
    //   8: ifne +6 -> 14
    //   11: goto +139 -> 150
    //   14: new 222	java/lang/StringBuilder
    //   17: dup
    //   18: invokespecial 223	java/lang/StringBuilder:<init>	()V
    //   21: astore_2
    //   22: aconst_null
    //   23: astore_3
    //   24: aconst_null
    //   25: astore 4
    //   27: aload 4
    //   29: astore 5
    //   31: aload_3
    //   32: astore 6
    //   34: new 225	java/io/BufferedReader
    //   37: astore 7
    //   39: aload 4
    //   41: astore 5
    //   43: aload_3
    //   44: astore 6
    //   46: new 227	java/io/FileReader
    //   49: astore 8
    //   51: aload 4
    //   53: astore 5
    //   55: aload_3
    //   56: astore 6
    //   58: aload 8
    //   60: aload_1
    //   61: invokespecial 230	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   64: aload 4
    //   66: astore 5
    //   68: aload_3
    //   69: astore 6
    //   71: aload 7
    //   73: aload 8
    //   75: invokespecial 233	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   78: aload 7
    //   80: astore_1
    //   81: aload_1
    //   82: astore 5
    //   84: aload_1
    //   85: astore 6
    //   87: aload_1
    //   88: invokevirtual 236	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   91: astore 4
    //   93: aload 4
    //   95: ifnull +19 -> 114
    //   98: aload_1
    //   99: astore 5
    //   101: aload_1
    //   102: astore 6
    //   104: aload_2
    //   105: aload 4
    //   107: invokevirtual 240	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: pop
    //   111: goto -30 -> 81
    //   114: aload_1
    //   115: invokestatic 246	miui/slide/SlideUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   118: aload_2
    //   119: areturn
    //   120: astore_1
    //   121: goto +22 -> 143
    //   124: astore_1
    //   125: aload 6
    //   127: astore 5
    //   129: ldc 18
    //   131: aload_1
    //   132: invokestatic 210	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
    //   135: pop
    //   136: aload 6
    //   138: invokestatic 246	miui/slide/SlideUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   141: aconst_null
    //   142: areturn
    //   143: aload 5
    //   145: invokestatic 246	miui/slide/SlideUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   148: aload_1
    //   149: athrow
    //   150: aconst_null
    //   151: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	152	0	this	SlideCloudConfigHelper
    //   0	152	1	paramFile	File
    //   21	98	2	localStringBuilder	StringBuilder
    //   23	46	3	localObject1	Object
    //   25	81	4	str	String
    //   29	115	5	localObject2	Object
    //   32	105	6	localObject3	Object
    //   37	42	7	localBufferedReader	java.io.BufferedReader
    //   49	25	8	localFileReader	java.io.FileReader
    // Exception table:
    //   from	to	target	type
    //   34	39	120	finally
    //   46	51	120	finally
    //   58	64	120	finally
    //   71	78	120	finally
    //   87	93	120	finally
    //   104	111	120	finally
    //   129	136	120	finally
    //   34	39	124	java/lang/Exception
    //   46	51	124	java/lang/Exception
    //   58	64	124	java/lang/Exception
    //   71	78	124	java/lang/Exception
    //   87	93	124	java/lang/Exception
    //   104	111	124	java/lang/Exception
  }
  
  private void parseSlideConfigFile(boolean paramBoolean, String paramString, int paramInt)
  {
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("parseSlideConfigFile updateAll = ");
    ((StringBuilder)localObject1).append(paramBoolean);
    Slog.w("SlideCloudConfigHelper", ((StringBuilder)localObject1).toString());
    localObject1 = parseFile(new File("/data/system/mirihi-config-cloud.json"));
    if (localObject1 == null)
    {
      localObject1 = parseFile(new File("/system/etc/mirihi-config.json"));
      if (localObject1 == null) {
        return;
      }
    }
    localObject1 = ((StringBuilder)localObject1).toString();
    if (TextUtils.isEmpty((CharSequence)localObject1)) {
      return;
    }
    if (paramBoolean)
    {
      this.mAppSlideConfigs.clear();
      this.mSlide3rdPackageList.clear();
    }
    try
    {
      Object localObject2 = new org/json/JSONObject;
      ((JSONObject)localObject2).<init>((String)localObject1);
      localObject1 = ((JSONObject)localObject2).getJSONArray("packages");
      for (int i = 0; i < ((JSONArray)localObject1).length(); i++)
      {
        localObject2 = generateAppSlideConfigFromJson(((JSONArray)localObject1).getJSONObject(i));
        if (localObject2 != null)
        {
          Object localObject3;
          if (paramBoolean)
          {
            this.mSlide3rdPackageList.add(((AppSlideConfig)localObject2).mPackageName);
            try
            {
              localObject3 = this.mPackageManager.getPackageInfo(((AppSlideConfig)localObject2).mPackageName, 0);
              if (localObject3 != null)
              {
                ((AppSlideConfig)localObject2).matchVersionSlideConfig(((PackageInfo)localObject3).versionCode);
                this.mAppSlideConfigs.put(((AppSlideConfig)localObject2).mPackageName, localObject2);
              }
            }
            catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
          }
          else
          {
            localObject3 = localNameNotFoundException.mPackageName;
            try
            {
              boolean bool = ((String)localObject3).equals(paramString);
              if (bool) {
                try
                {
                  localNameNotFoundException.matchVersionSlideConfig(paramInt);
                  this.mAppSlideConfigs.put(localNameNotFoundException.mPackageName, localNameNotFoundException);
                }
                catch (Exception paramString)
                {
                  break label301;
                }
              }
            }
            catch (Exception paramString)
            {
              break label301;
            }
          }
        }
      }
    }
    catch (Exception paramString)
    {
      label301:
      Slog.w("SlideCloudConfigHelper", paramString);
    }
  }
  
  private void registerDataObserver(final Context paramContext)
  {
    paramContext.getContentResolver().registerContentObserver(MiuiSettings.SettingsCloudData.getCloudDataNotifyUri(), true, new ContentObserver(BackgroundThread.getHandler())
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        Slog.w("SlideCloudConfigHelper", "SlideCloudConfigInfo onChange");
        SlideCloudConfigHelper.this.updateAppSlideCloudConfigList(paramContext);
        SlideCloudConfigHelper.this.registerSlideActivityChangeListener();
      }
    });
  }
  
  /* Error */
  private void updateAppSlideCloudConfigList(Context paramContext)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 304	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: ldc_w 327
    //   7: invokestatic 331	android/provider/MiuiSettings$SettingsCloudData:getCloudDataList	(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/util/List;
    //   10: astore_2
    //   11: aload_2
    //   12: ifnull +234 -> 246
    //   15: new 116	org/json/JSONArray
    //   18: dup
    //   19: invokespecial 332	org/json/JSONArray:<init>	()V
    //   22: astore_1
    //   23: aload_2
    //   24: invokeinterface 336 1 0
    //   29: astore_3
    //   30: aload_3
    //   31: invokeinterface 341 1 0
    //   36: ifeq +33 -> 69
    //   39: aload_3
    //   40: invokeinterface 345 1 0
    //   45: checkcast 347	android/provider/MiuiSettings$SettingsCloudData$CloudData
    //   48: invokevirtual 351	android/provider/MiuiSettings$SettingsCloudData$CloudData:json	()Lorg/json/JSONObject;
    //   51: astore_2
    //   52: aload_0
    //   53: aload_2
    //   54: invokespecial 271	miui/slide/SlideCloudConfigHelper:generateAppSlideConfigFromJson	(Lorg/json/JSONObject;)Lmiui/slide/AppSlideConfig;
    //   57: ifnull +9 -> 66
    //   60: aload_1
    //   61: aload_2
    //   62: invokevirtual 354	org/json/JSONArray:put	(Ljava/lang/Object;)Lorg/json/JSONArray;
    //   65: pop
    //   66: goto -36 -> 30
    //   69: aload_1
    //   70: invokevirtual 120	org/json/JSONArray:length	()I
    //   73: ifne +4 -> 77
    //   76: return
    //   77: new 104	org/json/JSONObject
    //   80: dup
    //   81: invokespecial 355	org/json/JSONObject:<init>	()V
    //   84: astore_2
    //   85: aload_2
    //   86: ldc_w 269
    //   89: aload_1
    //   90: invokevirtual 358	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   93: pop
    //   94: goto +11 -> 105
    //   97: astore_1
    //   98: ldc 18
    //   100: aload_1
    //   101: invokestatic 210	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
    //   104: pop
    //   105: aload_2
    //   106: invokevirtual 359	org/json/JSONObject:toString	()Ljava/lang/String;
    //   109: astore 4
    //   111: aconst_null
    //   112: astore 5
    //   114: aconst_null
    //   115: astore_3
    //   116: aload_3
    //   117: astore_2
    //   118: aload 5
    //   120: astore_1
    //   121: new 217	java/io/File
    //   124: astore 6
    //   126: aload_3
    //   127: astore_2
    //   128: aload 5
    //   130: astore_1
    //   131: aload 6
    //   133: ldc 12
    //   135: invokespecial 260	java/io/File:<init>	(Ljava/lang/String;)V
    //   138: aload_3
    //   139: astore_2
    //   140: aload 5
    //   142: astore_1
    //   143: aload 6
    //   145: ldc_w 360
    //   148: invokestatic 366	android/os/ParcelFileDescriptor:open	(Ljava/io/File;I)Landroid/os/ParcelFileDescriptor;
    //   151: astore_3
    //   152: aload_3
    //   153: astore_2
    //   154: aload_3
    //   155: astore_1
    //   156: new 368	com/android/internal/util/FastPrintWriter
    //   159: astore 6
    //   161: aload_3
    //   162: astore_2
    //   163: aload_3
    //   164: astore_1
    //   165: new 370	java/io/FileOutputStream
    //   168: astore 5
    //   170: aload_3
    //   171: astore_2
    //   172: aload_3
    //   173: astore_1
    //   174: aload 5
    //   176: aload_3
    //   177: invokevirtual 374	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   180: invokespecial 377	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   183: aload_3
    //   184: astore_2
    //   185: aload_3
    //   186: astore_1
    //   187: aload 6
    //   189: aload 5
    //   191: invokespecial 380	com/android/internal/util/FastPrintWriter:<init>	(Ljava/io/OutputStream;)V
    //   194: aload_3
    //   195: astore_2
    //   196: aload_3
    //   197: astore_1
    //   198: aload 6
    //   200: aload 4
    //   202: invokevirtual 385	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   205: aload_3
    //   206: astore_2
    //   207: aload_3
    //   208: astore_1
    //   209: aload 6
    //   211: invokevirtual 388	java/io/PrintWriter:flush	()V
    //   214: aload_3
    //   215: astore_1
    //   216: goto +17 -> 233
    //   219: astore_1
    //   220: goto +20 -> 240
    //   223: astore_3
    //   224: aload_1
    //   225: astore_2
    //   226: ldc 18
    //   228: aload_3
    //   229: invokestatic 210	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
    //   232: pop
    //   233: aload_1
    //   234: invokestatic 246	miui/slide/SlideUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   237: goto +9 -> 246
    //   240: aload_2
    //   241: invokestatic 246	miui/slide/SlideUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   244: aload_1
    //   245: athrow
    //   246: aload_0
    //   247: iconst_1
    //   248: aconst_null
    //   249: iconst_0
    //   250: invokespecial 92	miui/slide/SlideCloudConfigHelper:parseSlideConfigFile	(ZLjava/lang/String;I)V
    //   253: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	254	0	this	SlideCloudConfigHelper
    //   0	254	1	paramContext	Context
    //   10	231	2	localObject1	Object
    //   29	186	3	localObject2	Object
    //   223	6	3	localException	Exception
    //   109	92	4	str	String
    //   112	78	5	localFileOutputStream	java.io.FileOutputStream
    //   124	86	6	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   85	94	97	org/json/JSONException
    //   121	126	219	finally
    //   131	138	219	finally
    //   143	152	219	finally
    //   156	161	219	finally
    //   165	170	219	finally
    //   174	183	219	finally
    //   187	194	219	finally
    //   198	205	219	finally
    //   209	214	219	finally
    //   226	233	219	finally
    //   121	126	223	java/lang/Exception
    //   131	138	223	java/lang/Exception
    //   143	152	223	java/lang/Exception
    //   156	161	223	java/lang/Exception
    //   165	170	223	java/lang/Exception
    //   174	183	223	java/lang/Exception
    //   187	194	223	java/lang/Exception
    //   198	205	223	java/lang/Exception
    //   209	214	223	java/lang/Exception
  }
  
  public AppSlideConfig getAppSlideConfigs(String paramString)
  {
    if (this.mAppSlideConfigs.containsKey(paramString)) {
      return (AppSlideConfig)this.mAppSlideConfigs.get(paramString);
    }
    return null;
  }
  
  public void initConfig(Context paramContext)
  {
    this.mPackageManager = paramContext.getPackageManager();
    registerDataObserver(paramContext);
    parseSlideConfigFile(true, null, 0);
    initPackageMonitor(paramContext);
  }
  
  public void initPackageMonitor(Context paramContext)
  {
    this.mPackageMonitor.register(paramContext, BackgroundThread.getHandler().getLooper(), UserHandle.CURRENT, true);
  }
  
  public boolean is3rdAppProcessingActivity(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null))
    {
      if (this.mAppSlideConfigs.containsKey(paramString1)) {
        return ((AppSlideConfig)this.mAppSlideConfigs.get(paramString1)).matchStartingActivity(paramString2);
      }
      return false;
    }
    return false;
  }
  
  public boolean isMiuiAdapteringApp(String paramString)
  {
    if (paramString.equals("com.tencent.mm")) {
      return false;
    }
    if (paramString.equals("com.tencent.mobileqq")) {
      return false;
    }
    return this.mAppSlideConfigs.containsKey(paramString);
  }
  
  public void registerSlideActivityChangeListener()
  {
    if (this.mActivityChangeListener != null)
    {
      ArrayList localArrayList1 = new ArrayList(this.mAppSlideConfigs.keySet());
      localArrayList1.addAll(this.mSlideSystemPackageList);
      ArrayList localArrayList2 = new ArrayList();
      localArrayList2.add("com.android.settings.faceunlock.MiuiFaceDataInput");
      localArrayList2.add("com.android.settings.faceunlock.MiuiNormalCameraFaceInput");
      localArrayList2.add("com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput");
      localArrayList2.add("com.android.settings.faceunlock.MiuiFaceDataIntroduction");
      ProcessManager.unregisterActivityChanageListener(this.mActivityChangeListener);
      ProcessManager.registerActivityChangeListener(localArrayList1, localArrayList2, this.mActivityChangeListener);
    }
  }
  
  public void setActivityChangeListener(IActivityChangeListener paramIActivityChangeListener)
  {
    this.mActivityChangeListener = paramIActivityChangeListener;
    registerSlideActivityChangeListener();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideCloudConfigHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */