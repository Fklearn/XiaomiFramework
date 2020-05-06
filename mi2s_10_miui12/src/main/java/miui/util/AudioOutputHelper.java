package miui.util;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Process;
import android.os.RemoteException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AudioOutputHelper
{
  private static TrackCollector COLLECTOR;
  private static final TrackCollector COLLECTOR_COMPAT = new CompatCollector();
  private static final TrackCollector COLLECTOR_LP;
  private static final String DEFAULT_TEMP_FILE = "audio_flinger_%d_%d_%d.dump";
  private static final String TAG = AudioOutputHelper.class.getName();
  static final Result UNHANDLED = new Result(false, null);
  
  static
  {
    COLLECTOR = null;
    COLLECTOR_LP = new LPCollector();
  }
  
  private static String collectSessions(BufferedReader paramBufferedReader, Map<Integer, Integer> paramMap)
    throws NumberFormatException, IOException
  {
    String str;
    for (;;)
    {
      str = paramBufferedReader.readLine();
      if (str == null) {
        break;
      }
      Matcher localMatcher = DUMP_TAG.SESSIONS_CONTENT_FINDER.matcher(str);
      if (!localMatcher.find()) {
        break;
      }
      paramMap.put(Integer.valueOf(Integer.valueOf(localMatcher.group(1)).intValue()), Integer.valueOf(Integer.valueOf(localMatcher.group(2)).intValue()));
    }
    return str;
  }
  
  public static List<ActivityManager.RunningAppProcessInfo> getActiveClientProcessList(List<ActivityManager.RunningAppProcessInfo> paramList, Context paramContext, boolean paramBoolean)
  {
    if (paramList == null) {
      return null;
    }
    Object localObject1 = parseAudioFlingerDump(paramContext);
    if (localObject1 == null) {
      return null;
    }
    paramContext = new ArrayList();
    localObject1 = ((List)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (AudioOutputClient)((Iterator)localObject1).next();
      if (((AudioOutputClient)localObject2).mActive)
      {
        int i = ((AudioOutputClient)localObject2).mProcessId;
        localObject2 = paramList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)((Iterator)localObject2).next();
          if (localRunningAppProcessInfo.pid == i) {
            paramContext.add(localRunningAppProcessInfo);
          }
        }
      }
    }
    if (paramBoolean) {
      paramContext.addAll(getMainProcessNames(paramContext, paramList));
    }
    return paramContext;
  }
  
  public static List<String> getActiveReceiverNameList(Context paramContext)
  {
    try
    {
      Object localObject1 = new android/content/Intent;
      ((Intent)localObject1).<init>("android.intent.action.MEDIA_BUTTON");
      localObject1 = AppGlobals.getPackageManager().queryIntentReceivers((Intent)localObject1, null, 0, 0);
      if ((localObject1 != null) && (((ParceledListSlice)localObject1).getList().size() != 0))
      {
        Object localObject2 = getActiveClientProcessList(ActivityManagerNative.getDefault().getRunningAppProcesses(), paramContext, true);
        if ((localObject2 != null) && (!((List)localObject2).isEmpty()))
        {
          paramContext = new java/util/ArrayList;
          paramContext.<init>();
          Iterator localIterator1 = ((List)localObject2).iterator();
          while (localIterator1.hasNext())
          {
            localObject2 = (ActivityManager.RunningAppProcessInfo)localIterator1.next();
            Iterator localIterator2 = ((ParceledListSlice)localObject1).getList().iterator();
            while (localIterator2.hasNext())
            {
              ResolveInfo localResolveInfo = (ResolveInfo)localIterator2.next();
              if ((localResolveInfo.activityInfo != null) && (((ActivityManager.RunningAppProcessInfo)localObject2).processName.equals(localResolveInfo.activityInfo.processName)))
              {
                paramContext.add(((ActivityManager.RunningAppProcessInfo)localObject2).processName);
                break;
              }
            }
          }
          return paramContext;
        }
        return null;
      }
      return null;
    }
    catch (RemoteException paramContext) {}
    return null;
  }
  
  private static List<ActivityManager.RunningAppProcessInfo> getMainProcessNames(List<ActivityManager.RunningAppProcessInfo> paramList1, List<ActivityManager.RunningAppProcessInfo> paramList2)
  {
    ArrayList localArrayList = new ArrayList();
    paramList1 = paramList1.iterator();
    while (paramList1.hasNext())
    {
      Object localObject = (ActivityManager.RunningAppProcessInfo)paramList1.next();
      int i = ((ActivityManager.RunningAppProcessInfo)localObject).processName.indexOf(":");
      if (i > 0)
      {
        String str = ((ActivityManager.RunningAppProcessInfo)localObject).processName.substring(0, i);
        localObject = paramList2.iterator();
        while (((Iterator)localObject).hasNext())
        {
          ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)((Iterator)localObject).next();
          if (str.equals(localRunningAppProcessInfo.processName)) {
            localArrayList.add(localRunningAppProcessInfo);
          }
        }
      }
    }
    return localArrayList;
  }
  
  public static boolean hasActiveReceivers(Context paramContext)
  {
    paramContext = getActiveReceiverNameList(paramContext);
    boolean bool;
    if ((paramContext != null) && (!paramContext.isEmpty())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private static boolean isStandBy(String paramString)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    try
    {
      int i = Integer.valueOf(paramString).intValue();
      if (i != 0) {
        bool2 = true;
      }
      return bool2;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      if (!Boolean.valueOf(paramString).booleanValue())
      {
        bool2 = bool1;
        if (!"yes".equals(paramString)) {}
      }
      else
      {
        bool2 = true;
      }
    }
    return bool2;
  }
  
  public static List<AudioOutputClient> parseAudioFlingerDump(Context paramContext)
  {
    Object localObject = null;
    try
    {
      paramContext = paramContext.getFilesDir();
    }
    catch (Exception paramContext)
    {
      paramContext = (Context)localObject;
    }
    localObject = paramContext;
    if (paramContext == null) {
      localObject = new File("/cache");
    }
    int i = Process.myPid();
    long l = Thread.currentThread().getId();
    int j = 0 + 1;
    paramContext = new File((File)localObject, String.format("audio_flinger_%d_%d_%d.dump", new Object[] { Integer.valueOf(i), Long.valueOf(l), Integer.valueOf(0) }));
    while (paramContext.exists())
    {
      paramContext = new File((File)localObject, String.format("audio_flinger_%d_%d_%d.dump", new Object[] { Integer.valueOf(i), Long.valueOf(l), Integer.valueOf(j) }));
      j++;
    }
    localObject = parseAudioFlingerDumpInternal(paramContext);
    paramContext.delete();
    return (List<AudioOutputClient>)localObject;
  }
  
  /* Error */
  private static List<AudioOutputClient> parseAudioFlingerDumpInternal(File paramFile)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore_2
    //   4: aconst_null
    //   5: astore_3
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore 5
    //   12: aload 5
    //   14: astore 6
    //   16: aload_1
    //   17: astore 7
    //   19: aload_2
    //   20: astore 8
    //   22: aload_3
    //   23: astore 9
    //   25: aload 4
    //   27: astore 10
    //   29: new 311	java/io/FileOutputStream
    //   32: astore 11
    //   34: aload 5
    //   36: astore 6
    //   38: aload_1
    //   39: astore 7
    //   41: aload_2
    //   42: astore 8
    //   44: aload_3
    //   45: astore 9
    //   47: aload 4
    //   49: astore 10
    //   51: aload 11
    //   53: aload_0
    //   54: invokespecial 314	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   57: aload 11
    //   59: astore 5
    //   61: aload 5
    //   63: astore 6
    //   65: aload 5
    //   67: astore 7
    //   69: aload 5
    //   71: astore 8
    //   73: aload 5
    //   75: astore 9
    //   77: aload 5
    //   79: astore 10
    //   81: ldc_w 316
    //   84: invokestatic 322	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   87: aload 5
    //   89: invokevirtual 326	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
    //   92: aconst_null
    //   93: invokeinterface 332 3 0
    //   98: aload 5
    //   100: invokevirtual 335	java/io/FileOutputStream:close	()V
    //   103: goto +136 -> 239
    //   106: astore 6
    //   108: goto -5 -> 103
    //   111: astore_0
    //   112: goto +524 -> 636
    //   115: astore 5
    //   117: aload 7
    //   119: astore 6
    //   121: getstatic 43	miui/util/AudioOutputHelper:TAG	Ljava/lang/String;
    //   124: aload 5
    //   126: invokevirtual 338	java/lang/Exception:toString	()Ljava/lang/String;
    //   129: invokestatic 344	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   132: pop
    //   133: aload 7
    //   135: ifnull +104 -> 239
    //   138: aload 7
    //   140: invokevirtual 335	java/io/FileOutputStream:close	()V
    //   143: goto -40 -> 103
    //   146: astore 5
    //   148: aload 8
    //   150: astore 6
    //   152: getstatic 43	miui/util/AudioOutputHelper:TAG	Ljava/lang/String;
    //   155: aload 5
    //   157: invokevirtual 345	java/io/IOException:toString	()Ljava/lang/String;
    //   160: invokestatic 344	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   163: pop
    //   164: aload 8
    //   166: ifnull +73 -> 239
    //   169: aload 8
    //   171: invokevirtual 335	java/io/FileOutputStream:close	()V
    //   174: goto -71 -> 103
    //   177: astore 5
    //   179: aload 9
    //   181: astore 6
    //   183: getstatic 43	miui/util/AudioOutputHelper:TAG	Ljava/lang/String;
    //   186: aload 5
    //   188: invokevirtual 346	android/os/RemoteException:toString	()Ljava/lang/String;
    //   191: invokestatic 344	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   194: pop
    //   195: aload 9
    //   197: ifnull +42 -> 239
    //   200: aload 9
    //   202: invokevirtual 335	java/io/FileOutputStream:close	()V
    //   205: goto -102 -> 103
    //   208: astore 5
    //   210: aload 10
    //   212: astore 6
    //   214: getstatic 43	miui/util/AudioOutputHelper:TAG	Ljava/lang/String;
    //   217: aload 5
    //   219: invokevirtual 347	java/io/FileNotFoundException:toString	()Ljava/lang/String;
    //   222: invokestatic 344	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   225: pop
    //   226: aload 10
    //   228: ifnull +11 -> 239
    //   231: aload 10
    //   233: invokevirtual 335	java/io/FileOutputStream:close	()V
    //   236: goto -133 -> 103
    //   239: aconst_null
    //   240: astore 5
    //   242: new 349	java/io/FileInputStream
    //   245: astore 6
    //   247: aload 6
    //   249: aload_0
    //   250: invokespecial 350	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   253: aload 6
    //   255: astore 5
    //   257: goto +4 -> 261
    //   260: astore_0
    //   261: aload 5
    //   263: ifnonnull +5 -> 268
    //   266: aconst_null
    //   267: areturn
    //   268: new 68	java/io/BufferedReader
    //   271: dup
    //   272: new 352	java/io/InputStreamReader
    //   275: dup
    //   276: aload 5
    //   278: invokespecial 355	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   281: invokespecial 358	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   284: astore 11
    //   286: new 121	java/util/ArrayList
    //   289: astore 9
    //   291: aload 9
    //   293: invokespecial 122	java/util/ArrayList:<init>	()V
    //   296: aconst_null
    //   297: astore 8
    //   299: iconst_0
    //   300: istore 12
    //   302: aconst_null
    //   303: astore_0
    //   304: aload_0
    //   305: ifnull +12 -> 317
    //   308: aconst_null
    //   309: astore 6
    //   311: aload_0
    //   312: astore 10
    //   314: goto +43 -> 357
    //   317: aload 11
    //   319: invokevirtual 71	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   322: astore 7
    //   324: aload 7
    //   326: astore 10
    //   328: aload_0
    //   329: astore 6
    //   331: aload 7
    //   333: ifnonnull +24 -> 357
    //   336: aload 11
    //   338: invokevirtual 359	java/io/BufferedReader:close	()V
    //   341: goto +4 -> 345
    //   344: astore_0
    //   345: aload 5
    //   347: invokevirtual 362	java/io/InputStream:close	()V
    //   350: goto +4 -> 354
    //   353: astore_0
    //   354: aload 9
    //   356: areturn
    //   357: getstatic 365	miui/util/AudioOutputHelper$DUMP_TAG:SESSIONS_HEAD_FINDER	Ljava/util/regex/Pattern;
    //   360: aload 10
    //   362: invokevirtual 81	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
    //   365: invokevirtual 368	java/util/regex/Matcher:matches	()Z
    //   368: ifeq +36 -> 404
    //   371: aload 8
    //   373: astore_0
    //   374: aload 8
    //   376: ifnonnull +11 -> 387
    //   379: new 370	java/util/HashMap
    //   382: astore_0
    //   383: aload_0
    //   384: invokespecial 371	java/util/HashMap:<init>	()V
    //   387: aload 11
    //   389: aload_0
    //   390: invokestatic 373	miui/util/AudioOutputHelper:collectSessions	(Ljava/io/BufferedReader;Ljava/util/Map;)Ljava/lang/String;
    //   393: astore 6
    //   395: aload_0
    //   396: astore 8
    //   398: aload 6
    //   400: astore_0
    //   401: goto -97 -> 304
    //   404: getstatic 376	miui/util/AudioOutputHelper$DUMP_TAG:STANDBY_FINDER	Ljava/util/regex/Pattern;
    //   407: aload 10
    //   409: invokevirtual 81	java/util/regex/Pattern:matcher	(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;
    //   412: astore_0
    //   413: aload_0
    //   414: invokevirtual 87	java/util/regex/Matcher:find	()Z
    //   417: ifeq +22 -> 439
    //   420: aload_0
    //   421: iconst_1
    //   422: invokevirtual 91	java/util/regex/Matcher:group	(I)Ljava/lang/String;
    //   425: invokevirtual 379	java/lang/String:trim	()Ljava/lang/String;
    //   428: invokestatic 381	miui/util/AudioOutputHelper:isStandBy	(Ljava/lang/String;)Z
    //   431: istore 12
    //   433: aload 6
    //   435: astore_0
    //   436: goto -132 -> 304
    //   439: aload 6
    //   441: astore_0
    //   442: iload 12
    //   444: ifne +131 -> 575
    //   447: getstatic 45	miui/util/AudioOutputHelper:COLLECTOR	Lmiui/util/AudioOutputHelper$TrackCollector;
    //   450: astore_0
    //   451: aload_0
    //   452: ifnull +24 -> 476
    //   455: aload_0
    //   456: aload 11
    //   458: aload 10
    //   460: aload 9
    //   462: aload 8
    //   464: invokeinterface 385 5 0
    //   469: getfield 388	miui/util/AudioOutputHelper$Result:mSkipped	Ljava/lang/String;
    //   472: astore_0
    //   473: goto +102 -> 575
    //   476: getstatic 50	miui/util/AudioOutputHelper:COLLECTOR_LP	Lmiui/util/AudioOutputHelper$TrackCollector;
    //   479: aload 11
    //   481: aload 10
    //   483: aload 9
    //   485: aload 8
    //   487: invokeinterface 385 5 0
    //   492: astore_0
    //   493: aload_0
    //   494: getfield 391	miui/util/AudioOutputHelper$Result:mHandled	Z
    //   497: ifeq +27 -> 524
    //   500: aload_0
    //   501: getfield 388	miui/util/AudioOutputHelper$Result:mSkipped	Ljava/lang/String;
    //   504: astore_0
    //   505: getstatic 50	miui/util/AudioOutputHelper:COLLECTOR_LP	Lmiui/util/AudioOutputHelper$TrackCollector;
    //   508: putstatic 45	miui/util/AudioOutputHelper:COLLECTOR	Lmiui/util/AudioOutputHelper$TrackCollector;
    //   511: getstatic 43	miui/util/AudioOutputHelper:TAG	Ljava/lang/String;
    //   514: ldc_w 393
    //   517: invokestatic 396	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   520: pop
    //   521: goto +54 -> 575
    //   524: getstatic 53	miui/util/AudioOutputHelper:COLLECTOR_COMPAT	Lmiui/util/AudioOutputHelper$TrackCollector;
    //   527: aload 11
    //   529: aload 10
    //   531: aload 9
    //   533: aload 8
    //   535: invokeinterface 385 5 0
    //   540: astore 10
    //   542: aload 6
    //   544: astore_0
    //   545: aload 10
    //   547: getfield 391	miui/util/AudioOutputHelper$Result:mHandled	Z
    //   550: ifeq +25 -> 575
    //   553: aload 10
    //   555: getfield 388	miui/util/AudioOutputHelper$Result:mSkipped	Ljava/lang/String;
    //   558: astore_0
    //   559: getstatic 53	miui/util/AudioOutputHelper:COLLECTOR_COMPAT	Lmiui/util/AudioOutputHelper$TrackCollector;
    //   562: putstatic 45	miui/util/AudioOutputHelper:COLLECTOR	Lmiui/util/AudioOutputHelper$TrackCollector;
    //   565: getstatic 43	miui/util/AudioOutputHelper:TAG	Ljava/lang/String;
    //   568: ldc_w 398
    //   571: invokestatic 396	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   574: pop
    //   575: goto -271 -> 304
    //   578: astore_0
    //   579: goto +35 -> 614
    //   582: astore_0
    //   583: getstatic 43	miui/util/AudioOutputHelper:TAG	Ljava/lang/String;
    //   586: aload_0
    //   587: invokevirtual 338	java/lang/Exception:toString	()Ljava/lang/String;
    //   590: invokestatic 344	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   593: pop
    //   594: aload 11
    //   596: invokevirtual 359	java/io/BufferedReader:close	()V
    //   599: goto +4 -> 603
    //   602: astore_0
    //   603: aload 5
    //   605: invokevirtual 362	java/io/InputStream:close	()V
    //   608: goto +4 -> 612
    //   611: astore_0
    //   612: aconst_null
    //   613: areturn
    //   614: aload 11
    //   616: invokevirtual 359	java/io/BufferedReader:close	()V
    //   619: goto +5 -> 624
    //   622: astore 6
    //   624: aload 5
    //   626: invokevirtual 362	java/io/InputStream:close	()V
    //   629: goto +5 -> 634
    //   632: astore 6
    //   634: aload_0
    //   635: athrow
    //   636: aload 6
    //   638: ifnull +13 -> 651
    //   641: aload 6
    //   643: invokevirtual 335	java/io/FileOutputStream:close	()V
    //   646: goto +5 -> 651
    //   649: astore 6
    //   651: aload_0
    //   652: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	653	0	paramFile	File
    //   1	38	1	localObject1	Object
    //   3	39	2	localObject2	Object
    //   5	40	3	localObject3	Object
    //   7	41	4	localObject4	Object
    //   10	89	5	localObject5	Object
    //   115	10	5	localException	Exception
    //   146	10	5	localIOException1	IOException
    //   177	10	5	localRemoteException	RemoteException
    //   208	10	5	localFileNotFoundException	java.io.FileNotFoundException
    //   240	385	5	localObject6	Object
    //   14	50	6	localObject7	Object
    //   106	1	6	localIOException2	IOException
    //   119	424	6	localObject8	Object
    //   622	1	6	localIOException3	IOException
    //   632	10	6	localIOException4	IOException
    //   649	1	6	localIOException5	IOException
    //   17	315	7	localObject9	Object
    //   20	514	8	localObject10	Object
    //   23	509	9	localObject11	Object
    //   27	527	10	localObject12	Object
    //   32	583	11	localObject13	Object
    //   300	143	12	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   98	103	106	java/io/IOException
    //   138	143	106	java/io/IOException
    //   169	174	106	java/io/IOException
    //   200	205	106	java/io/IOException
    //   231	236	106	java/io/IOException
    //   29	34	111	finally
    //   51	57	111	finally
    //   81	98	111	finally
    //   121	133	111	finally
    //   152	164	111	finally
    //   183	195	111	finally
    //   214	226	111	finally
    //   29	34	115	java/lang/Exception
    //   51	57	115	java/lang/Exception
    //   81	98	115	java/lang/Exception
    //   29	34	146	java/io/IOException
    //   51	57	146	java/io/IOException
    //   81	98	146	java/io/IOException
    //   29	34	177	android/os/RemoteException
    //   51	57	177	android/os/RemoteException
    //   81	98	177	android/os/RemoteException
    //   29	34	208	java/io/FileNotFoundException
    //   51	57	208	java/io/FileNotFoundException
    //   81	98	208	java/io/FileNotFoundException
    //   242	253	260	java/io/FileNotFoundException
    //   336	341	344	java/io/IOException
    //   345	350	353	java/io/IOException
    //   286	296	578	finally
    //   317	324	578	finally
    //   357	371	578	finally
    //   379	387	578	finally
    //   387	395	578	finally
    //   404	433	578	finally
    //   447	451	578	finally
    //   455	473	578	finally
    //   476	521	578	finally
    //   524	542	578	finally
    //   545	575	578	finally
    //   583	594	578	finally
    //   286	296	582	java/lang/Exception
    //   317	324	582	java/lang/Exception
    //   357	371	582	java/lang/Exception
    //   379	387	582	java/lang/Exception
    //   387	395	582	java/lang/Exception
    //   404	433	582	java/lang/Exception
    //   447	451	582	java/lang/Exception
    //   455	473	582	java/lang/Exception
    //   476	521	582	java/lang/Exception
    //   524	542	582	java/lang/Exception
    //   545	575	582	java/lang/Exception
    //   594	599	602	java/io/IOException
    //   603	608	611	java/io/IOException
    //   614	619	622	java/io/IOException
    //   624	629	632	java/io/IOException
    //   641	646	649	java/io/IOException
  }
  
  public static class AudioOutputClient
  {
    public boolean mActive;
    public final int mProcessId;
    public final int mSessionId;
    public final int mStreamType;
    
    public AudioOutputClient(int paramInt1, int paramInt2, int paramInt3)
    {
      this(paramInt1, paramInt2, paramInt3, false);
    }
    
    public AudioOutputClient(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      this.mSessionId = paramInt1;
      this.mProcessId = paramInt2;
      this.mStreamType = paramInt3;
      this.mActive = paramBoolean;
    }
  }
  
  static final class CompatCollector
    implements AudioOutputHelper.TrackCollector
  {
    public static final Pattern ACTIVE_TRACKS_FINDER_COMPAT = Pattern.compile("^Output thread 0x[\\w]+ active tracks");
    public static final Pattern TRACKS_FINDER_COMPAT = Pattern.compile("^Output thread 0x[\\w]+ tracks");
    public static final Pattern TRACK_CONTENT_FINDER_COMPAT = Pattern.compile("^(\\s|F)+\\d+\\s+\\d+\\s+(\\d+)\\s+\\d+\\s+\\w+\\s+(\\d+)\\s.+");
    public static final int TRACK_SESSION_GRP_IDX = 3;
    public static final int TRACK_STREAM_TYPE_GRP_IDX = 2;
    
    private String collectTracks(BufferedReader paramBufferedReader, List<AudioOutputHelper.AudioOutputClient> paramList, Map<Integer, Integer> paramMap, boolean paramBoolean)
      throws NumberFormatException, IOException
    {
      Object localObject1;
      for (;;)
      {
        localObject1 = paramBufferedReader.readLine();
        if (localObject1 == null) {
          break;
        }
        Object localObject2 = TRACK_CONTENT_FINDER_COMPAT.matcher((CharSequence)localObject1);
        if (!((Matcher)localObject2).find()) {
          break;
        }
        int i = Integer.valueOf(((Matcher)localObject2).group(3)).intValue();
        localObject1 = (Integer)paramMap.get(Integer.valueOf(i));
        if (localObject1 != null)
        {
          int j = ((Integer)localObject1).intValue();
          int k = Integer.valueOf(((Matcher)localObject2).group(2)).intValue();
          int m = 0;
          int n = 0;
          if (paramBoolean)
          {
            localObject2 = paramList.iterator();
            for (;;)
            {
              m = n;
              if (!((Iterator)localObject2).hasNext()) {
                break;
              }
              localObject1 = (AudioOutputHelper.AudioOutputClient)((Iterator)localObject2).next();
              if (((AudioOutputHelper.AudioOutputClient)localObject1).mSessionId == i)
              {
                ((AudioOutputHelper.AudioOutputClient)localObject1).mActive = paramBoolean;
                n = 1;
              }
            }
          }
          if (m == 0) {
            paramList.add(new AudioOutputHelper.AudioOutputClient(i, j, k, paramBoolean));
          }
        }
      }
      return (String)localObject1;
    }
    
    public AudioOutputHelper.Result collectTracks(BufferedReader paramBufferedReader, String paramString, List<AudioOutputHelper.AudioOutputClient> paramList, Map<Integer, Integer> paramMap)
      throws IOException
    {
      if (TRACKS_FINDER_COMPAT.matcher(paramString).find())
      {
        paramBufferedReader.readLine();
        return new AudioOutputHelper.Result(true, collectTracks(paramBufferedReader, paramList, paramMap, false));
      }
      if (ACTIVE_TRACKS_FINDER_COMPAT.matcher(paramString).find())
      {
        paramBufferedReader.readLine();
        return new AudioOutputHelper.Result(true, collectTracks(paramBufferedReader, paramList, paramMap, true));
      }
      return AudioOutputHelper.UNHANDLED;
    }
  }
  
  private static class DUMP_TAG
  {
    public static final int PID_GRP_IDX = 2;
    public static final Pattern SESSIONS_CONTENT_FINDER = Pattern.compile("^\\s+(\\d+)\\s+(\\d+)\\s+\\d+$");
    public static final Pattern SESSIONS_HEAD_FINDER = Pattern.compile("^[\\s]+session[\\s]+pid[\\s]+(cnt|count)");
    public static final int SESSION_GRP_IDX = 1;
    public static final Pattern STANDBY_FINDER = Pattern.compile("^[\\s]*[s|S]tandby: (\\w+)");
    public static final int STANDBY_GRP_IDX = 1;
  }
  
  static final class LPCollector
    implements AudioOutputHelper.TrackCollector
  {
    public static final Pattern ACTIVE_TRACKS_FINDER = Pattern.compile("^[\\s]+[\\d]+[\\s]+Tracks of which [\\d]+ are active");
    public static final int TRACK_ACTIVE_IDX = 2;
    public static final Pattern TRACK_CONTENT_FINDER = Pattern.compile("^(\\s|F)+\\d+\\s+(\\w+)\\s+\\d+\\s+(\\d+)\\s+\\d+\\s+\\d+\\s+(\\d+)\\s.+");
    public static final int TRACK_SESSION_GRP_IDX = 4;
    public static final int TRACK_STREAM_TYPE_GRP_IDX = 3;
    
    private String collectTracks(BufferedReader paramBufferedReader, List<AudioOutputHelper.AudioOutputClient> paramList, Map<Integer, Integer> paramMap)
      throws NumberFormatException, IOException
    {
      Object localObject;
      for (;;)
      {
        localObject = paramBufferedReader.readLine();
        if (localObject == null) {
          break;
        }
        Matcher localMatcher = TRACK_CONTENT_FINDER.matcher((CharSequence)localObject);
        if (!localMatcher.find()) {
          break;
        }
        int i = Integer.valueOf(localMatcher.group(4)).intValue();
        localObject = (Integer)paramMap.get(Integer.valueOf(i));
        if (localObject != null) {
          paramList.add(new AudioOutputHelper.AudioOutputClient(i, ((Integer)localObject).intValue(), Integer.valueOf(localMatcher.group(3)).intValue(), "yes".equals(localMatcher.group(2))));
        }
      }
      return (String)localObject;
    }
    
    public AudioOutputHelper.Result collectTracks(BufferedReader paramBufferedReader, String paramString, List<AudioOutputHelper.AudioOutputClient> paramList, Map<Integer, Integer> paramMap)
      throws IOException
    {
      if (ACTIVE_TRACKS_FINDER.matcher(paramString).find())
      {
        paramBufferedReader.readLine();
        return new AudioOutputHelper.Result(true, collectTracks(paramBufferedReader, paramList, paramMap));
      }
      return AudioOutputHelper.UNHANDLED;
    }
  }
  
  static final class Result
  {
    public final boolean mHandled;
    public final String mSkipped;
    
    public Result(boolean paramBoolean, String paramString)
    {
      this.mHandled = paramBoolean;
      this.mSkipped = paramString;
    }
  }
  
  static abstract interface TrackCollector
  {
    public abstract AudioOutputHelper.Result collectTracks(BufferedReader paramBufferedReader, String paramString, List<AudioOutputHelper.AudioOutputClient> paramList, Map<Integer, Integer> paramMap)
      throws IOException;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/AudioOutputHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */