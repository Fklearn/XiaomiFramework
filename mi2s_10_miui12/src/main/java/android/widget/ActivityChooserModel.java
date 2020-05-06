package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.os.AsyncTask;
import android.os.Process;
import android.text.TextUtils;
import com.android.internal.content.PackageMonitor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityChooserModel
  extends DataSetObservable
{
  private static final String ATTRIBUTE_ACTIVITY = "activity";
  private static final String ATTRIBUTE_TIME = "time";
  private static final String ATTRIBUTE_WEIGHT = "weight";
  private static final boolean DEBUG = false;
  private static final int DEFAULT_ACTIVITY_INFLATION = 5;
  private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0F;
  public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
  public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
  private static final String HISTORY_FILE_EXTENSION = ".xml";
  private static final int INVALID_INDEX = -1;
  private static final String LOG_TAG = ActivityChooserModel.class.getSimpleName();
  private static final String TAG_HISTORICAL_RECORD = "historical-record";
  private static final String TAG_HISTORICAL_RECORDS = "historical-records";
  private static final Map<String, ActivityChooserModel> sDataModelRegistry = new HashMap();
  private static final Object sRegistryLock = new Object();
  private final List<ActivityResolveInfo> mActivities = new ArrayList();
  private OnChooseActivityListener mActivityChoserModelPolicy;
  private ActivitySorter mActivitySorter = new DefaultSorter(null);
  private boolean mCanReadHistoricalData = true;
  private final Context mContext;
  private final List<HistoricalRecord> mHistoricalRecords = new ArrayList();
  private boolean mHistoricalRecordsChanged = true;
  private final String mHistoryFileName;
  private int mHistoryMaxSize = 50;
  private final Object mInstanceLock = new Object();
  private Intent mIntent;
  private final PackageMonitor mPackageMonitor = new DataModelPackageMonitor(null);
  private boolean mReadShareHistoryCalled = false;
  private boolean mReloadActivities = false;
  
  private ActivityChooserModel(Context paramContext, String paramString)
  {
    this.mContext = paramContext.getApplicationContext();
    if ((!TextUtils.isEmpty(paramString)) && (!paramString.endsWith(".xml")))
    {
      paramContext = new StringBuilder();
      paramContext.append(paramString);
      paramContext.append(".xml");
      this.mHistoryFileName = paramContext.toString();
    }
    else
    {
      this.mHistoryFileName = paramString;
    }
    this.mPackageMonitor.register(this.mContext, null, true);
  }
  
  private boolean addHisoricalRecord(HistoricalRecord paramHistoricalRecord)
  {
    boolean bool = this.mHistoricalRecords.add(paramHistoricalRecord);
    if (bool)
    {
      this.mHistoricalRecordsChanged = true;
      pruneExcessiveHistoricalRecordsIfNeeded();
      persistHistoricalDataIfNeeded();
      sortActivitiesIfNeeded();
      notifyChanged();
    }
    return bool;
  }
  
  private void ensureConsistentState()
  {
    boolean bool1 = loadActivitiesIfNeeded();
    boolean bool2 = readHistoricalDataIfNeeded();
    pruneExcessiveHistoricalRecordsIfNeeded();
    if ((bool1 | bool2))
    {
      sortActivitiesIfNeeded();
      notifyChanged();
    }
  }
  
  @UnsupportedAppUsage
  public static ActivityChooserModel get(Context paramContext, String paramString)
  {
    synchronized (sRegistryLock)
    {
      ActivityChooserModel localActivityChooserModel1 = (ActivityChooserModel)sDataModelRegistry.get(paramString);
      ActivityChooserModel localActivityChooserModel2 = localActivityChooserModel1;
      if (localActivityChooserModel1 == null)
      {
        localActivityChooserModel2 = new android/widget/ActivityChooserModel;
        localActivityChooserModel2.<init>(paramContext, paramString);
        sDataModelRegistry.put(paramString, localActivityChooserModel2);
      }
      return localActivityChooserModel2;
    }
  }
  
  private boolean loadActivitiesIfNeeded()
  {
    if ((this.mReloadActivities) && (this.mIntent != null))
    {
      this.mReloadActivities = false;
      this.mActivities.clear();
      List localList = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
      int i = localList.size();
      for (int j = 0; j < i; j++)
      {
        ResolveInfo localResolveInfo = (ResolveInfo)localList.get(j);
        ActivityInfo localActivityInfo = localResolveInfo.activityInfo;
        if (ActivityManager.checkComponentPermission(localActivityInfo.permission, Process.myUid(), localActivityInfo.applicationInfo.uid, localActivityInfo.exported) == 0) {
          this.mActivities.add(new ActivityResolveInfo(localResolveInfo));
        }
      }
      return true;
    }
    return false;
  }
  
  private void persistHistoricalDataIfNeeded()
  {
    if (this.mReadShareHistoryCalled)
    {
      if (!this.mHistoricalRecordsChanged) {
        return;
      }
      this.mHistoricalRecordsChanged = false;
      if (!TextUtils.isEmpty(this.mHistoryFileName)) {
        new PersistHistoryAsyncTask(null).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Object[] { new ArrayList(this.mHistoricalRecords), this.mHistoryFileName });
      }
      return;
    }
    throw new IllegalStateException("No preceding call to #readHistoricalData");
  }
  
  private void pruneExcessiveHistoricalRecordsIfNeeded()
  {
    int i = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
    if (i <= 0) {
      return;
    }
    this.mHistoricalRecordsChanged = true;
    for (int j = 0; j < i; j++) {
      HistoricalRecord localHistoricalRecord = (HistoricalRecord)this.mHistoricalRecords.remove(0);
    }
  }
  
  private boolean readHistoricalDataIfNeeded()
  {
    if ((this.mCanReadHistoricalData) && (this.mHistoricalRecordsChanged) && (!TextUtils.isEmpty(this.mHistoryFileName)))
    {
      this.mCanReadHistoricalData = false;
      this.mReadShareHistoryCalled = true;
      readHistoricalDataImpl();
      return true;
    }
    return false;
  }
  
  /* Error */
  private void readHistoricalDataImpl()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 153	android/widget/ActivityChooserModel:mContext	Landroid/content/Context;
    //   4: aload_0
    //   5: getfield 177	android/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
    //   8: invokevirtual 330	android/content/Context:openFileInput	(Ljava/lang/String;)Ljava/io/FileInputStream;
    //   11: astore_1
    //   12: invokestatic 336	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   15: astore_2
    //   16: aload_2
    //   17: aload_1
    //   18: getstatic 342	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   21: invokevirtual 347	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   24: invokeinterface 353 3 0
    //   29: iconst_0
    //   30: istore_3
    //   31: iload_3
    //   32: iconst_1
    //   33: if_icmpeq +18 -> 51
    //   36: iload_3
    //   37: iconst_2
    //   38: if_icmpeq +13 -> 51
    //   41: aload_2
    //   42: invokeinterface 356 1 0
    //   47: istore_3
    //   48: goto -17 -> 31
    //   51: ldc 66
    //   53: aload_2
    //   54: invokeinterface 359 1 0
    //   59: invokevirtual 362	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   62: ifeq +150 -> 212
    //   65: aload_0
    //   66: getfield 127	android/widget/ActivityChooserModel:mHistoricalRecords	Ljava/util/List;
    //   69: astore 4
    //   71: aload 4
    //   73: invokeinterface 239 1 0
    //   78: aload_2
    //   79: invokeinterface 356 1 0
    //   84: istore_3
    //   85: iload_3
    //   86: iconst_1
    //   87: if_icmpne +14 -> 101
    //   90: aload_1
    //   91: ifnull +259 -> 350
    //   94: aload_1
    //   95: invokevirtual 367	java/io/FileInputStream:close	()V
    //   98: goto +245 -> 343
    //   101: iload_3
    //   102: iconst_3
    //   103: if_icmpeq -25 -> 78
    //   106: iload_3
    //   107: iconst_4
    //   108: if_icmpne +6 -> 114
    //   111: goto -33 -> 78
    //   114: ldc 63
    //   116: aload_2
    //   117: invokeinterface 359 1 0
    //   122: invokevirtual 362	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   125: ifeq +71 -> 196
    //   128: aload_2
    //   129: aconst_null
    //   130: ldc 34
    //   132: invokeinterface 371 3 0
    //   137: astore 5
    //   139: aload_2
    //   140: aconst_null
    //   141: ldc 37
    //   143: invokeinterface 371 3 0
    //   148: invokestatic 377	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   151: lstore 6
    //   153: aload_2
    //   154: aconst_null
    //   155: ldc 40
    //   157: invokeinterface 371 3 0
    //   162: invokestatic 383	java/lang/Float:parseFloat	(Ljava/lang/String;)F
    //   165: fstore 8
    //   167: new 23	android/widget/ActivityChooserModel$HistoricalRecord
    //   170: astore 9
    //   172: aload 9
    //   174: aload 5
    //   176: lload 6
    //   178: fload 8
    //   180: invokespecial 386	android/widget/ActivityChooserModel$HistoricalRecord:<init>	(Ljava/lang/String;JF)V
    //   183: aload 4
    //   185: aload 9
    //   187: invokeinterface 199 2 0
    //   192: pop
    //   193: goto -115 -> 78
    //   196: new 324	org/xmlpull/v1/XmlPullParserException
    //   199: astore 4
    //   201: aload 4
    //   203: ldc_w 388
    //   206: invokespecial 389	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   209: aload 4
    //   211: athrow
    //   212: new 324	org/xmlpull/v1/XmlPullParserException
    //   215: astore 4
    //   217: aload 4
    //   219: ldc_w 391
    //   222: invokespecial 389	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   225: aload 4
    //   227: athrow
    //   228: astore 4
    //   230: goto +121 -> 351
    //   233: astore 9
    //   235: getstatic 103	android/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
    //   238: astore_2
    //   239: new 167	java/lang/StringBuilder
    //   242: astore 4
    //   244: aload 4
    //   246: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   249: aload 4
    //   251: ldc_w 393
    //   254: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   257: pop
    //   258: aload 4
    //   260: aload_0
    //   261: getfield 177	android/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
    //   264: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: pop
    //   268: aload_2
    //   269: aload 4
    //   271: invokevirtual 175	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   274: aload 9
    //   276: invokestatic 399	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   279: pop
    //   280: aload_1
    //   281: ifnull +69 -> 350
    //   284: aload_1
    //   285: invokevirtual 367	java/io/FileInputStream:close	()V
    //   288: goto +55 -> 343
    //   291: astore 9
    //   293: getstatic 103	android/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
    //   296: astore 4
    //   298: new 167	java/lang/StringBuilder
    //   301: astore_2
    //   302: aload_2
    //   303: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   306: aload_2
    //   307: ldc_w 393
    //   310: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   313: pop
    //   314: aload_2
    //   315: aload_0
    //   316: getfield 177	android/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
    //   319: invokevirtual 172	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   322: pop
    //   323: aload 4
    //   325: aload_2
    //   326: invokevirtual 175	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   329: aload 9
    //   331: invokestatic 399	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   334: pop
    //   335: aload_1
    //   336: ifnull +14 -> 350
    //   339: aload_1
    //   340: invokevirtual 367	java/io/FileInputStream:close	()V
    //   343: goto +7 -> 350
    //   346: astore_1
    //   347: goto -4 -> 343
    //   350: return
    //   351: aload_1
    //   352: ifnull +11 -> 363
    //   355: aload_1
    //   356: invokevirtual 367	java/io/FileInputStream:close	()V
    //   359: goto +4 -> 363
    //   362: astore_1
    //   363: aload 4
    //   365: athrow
    //   366: astore_1
    //   367: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	368	0	this	ActivityChooserModel
    //   11	329	1	localFileInputStream	java.io.FileInputStream
    //   346	10	1	localIOException1	java.io.IOException
    //   362	1	1	localIOException2	java.io.IOException
    //   366	1	1	localFileNotFoundException	java.io.FileNotFoundException
    //   15	311	2	localObject1	Object
    //   30	79	3	i	int
    //   69	157	4	localObject2	Object
    //   228	1	4	localObject3	Object
    //   242	122	4	localObject4	Object
    //   137	38	5	str	String
    //   151	26	6	l	long
    //   165	14	8	f	float
    //   170	16	9	localHistoricalRecord	HistoricalRecord
    //   233	42	9	localIOException3	java.io.IOException
    //   291	39	9	localXmlPullParserException	org.xmlpull.v1.XmlPullParserException
    // Exception table:
    //   from	to	target	type
    //   12	29	228	finally
    //   41	48	228	finally
    //   51	78	228	finally
    //   78	85	228	finally
    //   114	193	228	finally
    //   196	212	228	finally
    //   212	228	228	finally
    //   235	280	228	finally
    //   293	335	228	finally
    //   12	29	233	java/io/IOException
    //   41	48	233	java/io/IOException
    //   51	78	233	java/io/IOException
    //   78	85	233	java/io/IOException
    //   114	193	233	java/io/IOException
    //   196	212	233	java/io/IOException
    //   212	228	233	java/io/IOException
    //   12	29	291	org/xmlpull/v1/XmlPullParserException
    //   41	48	291	org/xmlpull/v1/XmlPullParserException
    //   51	78	291	org/xmlpull/v1/XmlPullParserException
    //   78	85	291	org/xmlpull/v1/XmlPullParserException
    //   114	193	291	org/xmlpull/v1/XmlPullParserException
    //   196	212	291	org/xmlpull/v1/XmlPullParserException
    //   212	228	291	org/xmlpull/v1/XmlPullParserException
    //   94	98	346	java/io/IOException
    //   284	288	346	java/io/IOException
    //   339	343	346	java/io/IOException
    //   355	359	362	java/io/IOException
    //   0	12	366	java/io/FileNotFoundException
  }
  
  private boolean sortActivitiesIfNeeded()
  {
    if ((this.mActivitySorter != null) && (this.mIntent != null) && (!this.mActivities.isEmpty()) && (!this.mHistoricalRecords.isEmpty()))
    {
      this.mActivitySorter.sort(this.mIntent, this.mActivities, Collections.unmodifiableList(this.mHistoricalRecords));
      return true;
    }
    return false;
  }
  
  @UnsupportedAppUsage
  public Intent chooseActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mIntent == null) {
        return null;
      }
      ensureConsistentState();
      Object localObject2 = (ActivityResolveInfo)this.mActivities.get(paramInt);
      ComponentName localComponentName = new android/content/ComponentName;
      localComponentName.<init>(((ActivityResolveInfo)localObject2).resolveInfo.activityInfo.packageName, ((ActivityResolveInfo)localObject2).resolveInfo.activityInfo.name);
      localObject2 = new android/content/Intent;
      ((Intent)localObject2).<init>(this.mIntent);
      ((Intent)localObject2).setComponent(localComponentName);
      if (this.mActivityChoserModelPolicy != null)
      {
        localObject4 = new android/content/Intent;
        ((Intent)localObject4).<init>((Intent)localObject2);
        if (this.mActivityChoserModelPolicy.onChooseActivity(this, (Intent)localObject4)) {
          return null;
        }
      }
      Object localObject4 = new android/widget/ActivityChooserModel$HistoricalRecord;
      ((HistoricalRecord)localObject4).<init>(localComponentName, System.currentTimeMillis(), 1.0F);
      addHisoricalRecord((HistoricalRecord)localObject4);
      return (Intent)localObject2;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    super.finalize();
    this.mPackageMonitor.unregister();
  }
  
  @UnsupportedAppUsage
  public ResolveInfo getActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivities.get(paramInt)).resolveInfo;
      return localResolveInfo;
    }
  }
  
  @UnsupportedAppUsage
  public int getActivityCount()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      int i = this.mActivities.size();
      return i;
    }
  }
  
  public int getActivityIndex(ResolveInfo paramResolveInfo)
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      List localList = this.mActivities;
      int i = localList.size();
      for (int j = 0; j < i; j++) {
        if (((ActivityResolveInfo)localList.get(j)).resolveInfo == paramResolveInfo) {
          return j;
        }
      }
      return -1;
    }
  }
  
  public ResolveInfo getDefaultActivity()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      if (!this.mActivities.isEmpty())
      {
        ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivities.get(0)).resolveInfo;
        return localResolveInfo;
      }
      return null;
    }
  }
  
  public int getHistoryMaxSize()
  {
    synchronized (this.mInstanceLock)
    {
      int i = this.mHistoryMaxSize;
      return i;
    }
  }
  
  public int getHistorySize()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      int i = this.mHistoricalRecords.size();
      return i;
    }
  }
  
  public Intent getIntent()
  {
    synchronized (this.mInstanceLock)
    {
      Intent localIntent = this.mIntent;
      return localIntent;
    }
  }
  
  public void setActivitySorter(ActivitySorter paramActivitySorter)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mActivitySorter == paramActivitySorter) {
        return;
      }
      this.mActivitySorter = paramActivitySorter;
      if (sortActivitiesIfNeeded()) {
        notifyChanged();
      }
      return;
    }
  }
  
  public void setDefaultActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      Object localObject2 = (ActivityResolveInfo)this.mActivities.get(paramInt);
      Object localObject3 = (ActivityResolveInfo)this.mActivities.get(0);
      float f;
      if (localObject3 != null) {
        f = ((ActivityResolveInfo)localObject3).weight - ((ActivityResolveInfo)localObject2).weight + 5.0F;
      } else {
        f = 1.0F;
      }
      localObject3 = new android/content/ComponentName;
      ((ComponentName)localObject3).<init>(((ActivityResolveInfo)localObject2).resolveInfo.activityInfo.packageName, ((ActivityResolveInfo)localObject2).resolveInfo.activityInfo.name);
      localObject2 = new android/widget/ActivityChooserModel$HistoricalRecord;
      ((HistoricalRecord)localObject2).<init>((ComponentName)localObject3, System.currentTimeMillis(), f);
      addHisoricalRecord((HistoricalRecord)localObject2);
      return;
    }
  }
  
  public void setHistoryMaxSize(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mHistoryMaxSize == paramInt) {
        return;
      }
      this.mHistoryMaxSize = paramInt;
      pruneExcessiveHistoricalRecordsIfNeeded();
      if (sortActivitiesIfNeeded()) {
        notifyChanged();
      }
      return;
    }
  }
  
  @UnsupportedAppUsage
  public void setIntent(Intent paramIntent)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mIntent == paramIntent) {
        return;
      }
      this.mIntent = paramIntent;
      this.mReloadActivities = true;
      ensureConsistentState();
      return;
    }
  }
  
  @UnsupportedAppUsage
  public void setOnChooseActivityListener(OnChooseActivityListener paramOnChooseActivityListener)
  {
    synchronized (this.mInstanceLock)
    {
      this.mActivityChoserModelPolicy = paramOnChooseActivityListener;
      return;
    }
  }
  
  public static abstract interface ActivityChooserModelClient
  {
    public abstract void setActivityChooserModel(ActivityChooserModel paramActivityChooserModel);
  }
  
  public final class ActivityResolveInfo
    implements Comparable<ActivityResolveInfo>
  {
    public final ResolveInfo resolveInfo;
    public float weight;
    
    public ActivityResolveInfo(ResolveInfo paramResolveInfo)
    {
      this.resolveInfo = paramResolveInfo;
    }
    
    public int compareTo(ActivityResolveInfo paramActivityResolveInfo)
    {
      return Float.floatToIntBits(paramActivityResolveInfo.weight) - Float.floatToIntBits(this.weight);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (ActivityResolveInfo)paramObject;
      return Float.floatToIntBits(this.weight) == Float.floatToIntBits(((ActivityResolveInfo)paramObject).weight);
    }
    
    public int hashCode()
    {
      return Float.floatToIntBits(this.weight) + 31;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("resolveInfo:");
      localStringBuilder.append(this.resolveInfo.toString());
      localStringBuilder.append("; weight:");
      localStringBuilder.append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface ActivitySorter
  {
    public abstract void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1);
  }
  
  private final class DataModelPackageMonitor
    extends PackageMonitor
  {
    private DataModelPackageMonitor() {}
    
    public void onSomePackagesChanged()
    {
      ActivityChooserModel.access$702(ActivityChooserModel.this, true);
    }
  }
  
  private final class DefaultSorter
    implements ActivityChooserModel.ActivitySorter
  {
    private static final float WEIGHT_DECAY_COEFFICIENT = 0.95F;
    private final Map<ComponentName, ActivityChooserModel.ActivityResolveInfo> mPackageNameToActivityMap = new HashMap();
    
    private DefaultSorter() {}
    
    public void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1)
    {
      paramIntent = this.mPackageNameToActivityMap;
      paramIntent.clear();
      int i = paramList.size();
      Object localObject;
      for (int j = 0; j < i; j++)
      {
        localObject = (ActivityChooserModel.ActivityResolveInfo)paramList.get(j);
        ((ActivityChooserModel.ActivityResolveInfo)localObject).weight = 0.0F;
        paramIntent.put(new ComponentName(((ActivityChooserModel.ActivityResolveInfo)localObject).resolveInfo.activityInfo.packageName, ((ActivityChooserModel.ActivityResolveInfo)localObject).resolveInfo.activityInfo.name), localObject);
      }
      j = paramList1.size();
      float f1 = 1.0F;
      j--;
      while (j >= 0)
      {
        localObject = (ActivityChooserModel.HistoricalRecord)paramList1.get(j);
        ActivityChooserModel.ActivityResolveInfo localActivityResolveInfo = (ActivityChooserModel.ActivityResolveInfo)paramIntent.get(((ActivityChooserModel.HistoricalRecord)localObject).activity);
        float f2 = f1;
        if (localActivityResolveInfo != null)
        {
          localActivityResolveInfo.weight += ((ActivityChooserModel.HistoricalRecord)localObject).weight * f1;
          f2 = f1 * 0.95F;
        }
        j--;
        f1 = f2;
      }
      Collections.sort(paramList);
    }
  }
  
  public static final class HistoricalRecord
  {
    public final ComponentName activity;
    public final long time;
    public final float weight;
    
    public HistoricalRecord(ComponentName paramComponentName, long paramLong, float paramFloat)
    {
      this.activity = paramComponentName;
      this.time = paramLong;
      this.weight = paramFloat;
    }
    
    public HistoricalRecord(String paramString, long paramLong, float paramFloat)
    {
      this(ComponentName.unflattenFromString(paramString), paramLong, paramFloat);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (HistoricalRecord)paramObject;
      ComponentName localComponentName = this.activity;
      if (localComponentName == null)
      {
        if (((HistoricalRecord)paramObject).activity != null) {
          return false;
        }
      }
      else if (!localComponentName.equals(((HistoricalRecord)paramObject).activity)) {
        return false;
      }
      if (this.time != ((HistoricalRecord)paramObject).time) {
        return false;
      }
      return Float.floatToIntBits(this.weight) == Float.floatToIntBits(((HistoricalRecord)paramObject).weight);
    }
    
    public int hashCode()
    {
      ComponentName localComponentName = this.activity;
      int i;
      if (localComponentName == null) {
        i = 0;
      } else {
        i = localComponentName.hashCode();
      }
      long l = this.time;
      return ((1 * 31 + i) * 31 + (int)(l ^ l >>> 32)) * 31 + Float.floatToIntBits(this.weight);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("; activity:");
      localStringBuilder.append(this.activity);
      localStringBuilder.append("; time:");
      localStringBuilder.append(this.time);
      localStringBuilder.append("; weight:");
      localStringBuilder.append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  public static abstract interface OnChooseActivityListener
  {
    public abstract boolean onChooseActivity(ActivityChooserModel paramActivityChooserModel, Intent paramIntent);
  }
  
  private final class PersistHistoryAsyncTask
    extends AsyncTask<Object, Void, Void>
  {
    private PersistHistoryAsyncTask() {}
    
    /* Error */
    public Void doInBackground(Object... paramVarArgs)
    {
      // Byte code:
      //   0: aload_1
      //   1: iconst_0
      //   2: aaload
      //   3: checkcast 36	java/util/List
      //   6: astore_2
      //   7: aload_1
      //   8: iconst_1
      //   9: aaload
      //   10: checkcast 38	java/lang/String
      //   13: astore_1
      //   14: aload_0
      //   15: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   18: invokestatic 42	android/widget/ActivityChooserModel:access$300	(Landroid/widget/ActivityChooserModel;)Landroid/content/Context;
      //   21: aload_1
      //   22: iconst_0
      //   23: invokevirtual 48	android/content/Context:openFileOutput	(Ljava/lang/String;I)Ljava/io/FileOutputStream;
      //   26: astore_3
      //   27: invokestatic 54	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
      //   30: astore 4
      //   32: aload_2
      //   33: astore 5
      //   35: aload_2
      //   36: astore 5
      //   38: aload_2
      //   39: astore 5
      //   41: aload_2
      //   42: astore 5
      //   44: aload 4
      //   46: aload_3
      //   47: aconst_null
      //   48: invokeinterface 60 3 0
      //   53: aload_2
      //   54: astore 5
      //   56: aload_2
      //   57: astore 5
      //   59: aload_2
      //   60: astore 5
      //   62: aload_2
      //   63: astore 5
      //   65: aload 4
      //   67: getstatic 66	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
      //   70: invokevirtual 72	java/nio/charset/Charset:name	()Ljava/lang/String;
      //   73: iconst_1
      //   74: invokestatic 78	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   77: invokeinterface 82 3 0
      //   82: aload_2
      //   83: astore 5
      //   85: aload_2
      //   86: astore 5
      //   88: aload_2
      //   89: astore 5
      //   91: aload_2
      //   92: astore 5
      //   94: aload 4
      //   96: aconst_null
      //   97: ldc 84
      //   99: invokeinterface 88 3 0
      //   104: pop
      //   105: aload_2
      //   106: astore 5
      //   108: aload_2
      //   109: astore 5
      //   111: aload_2
      //   112: astore 5
      //   114: aload_2
      //   115: astore 5
      //   117: aload_2
      //   118: invokeinterface 92 1 0
      //   123: istore 6
      //   125: iconst_0
      //   126: istore 7
      //   128: aload_2
      //   129: astore_1
      //   130: iload 7
      //   132: iload 6
      //   134: if_icmpge +132 -> 266
      //   137: aload_1
      //   138: astore 5
      //   140: aload_1
      //   141: astore 5
      //   143: aload_1
      //   144: astore 5
      //   146: aload_1
      //   147: astore 5
      //   149: aload_1
      //   150: iconst_0
      //   151: invokeinterface 96 2 0
      //   156: checkcast 98	android/widget/ActivityChooserModel$HistoricalRecord
      //   159: astore_2
      //   160: aload_1
      //   161: astore 5
      //   163: aload_1
      //   164: astore 5
      //   166: aload_1
      //   167: astore 5
      //   169: aload_1
      //   170: astore 5
      //   172: aload 4
      //   174: aconst_null
      //   175: ldc 100
      //   177: invokeinterface 88 3 0
      //   182: pop
      //   183: aload_1
      //   184: astore 5
      //   186: aload_1
      //   187: astore 5
      //   189: aload_1
      //   190: astore 5
      //   192: aload_1
      //   193: astore 5
      //   195: aload 4
      //   197: aconst_null
      //   198: ldc 102
      //   200: aload_2
      //   201: getfield 105	android/widget/ActivityChooserModel$HistoricalRecord:activity	Landroid/content/ComponentName;
      //   204: invokevirtual 110	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   207: invokeinterface 114 4 0
      //   212: pop
      //   213: aload 4
      //   215: aconst_null
      //   216: ldc 116
      //   218: aload_2
      //   219: getfield 119	android/widget/ActivityChooserModel$HistoricalRecord:time	J
      //   222: invokestatic 122	java/lang/String:valueOf	(J)Ljava/lang/String;
      //   225: invokeinterface 114 4 0
      //   230: pop
      //   231: aload 4
      //   233: aconst_null
      //   234: ldc 124
      //   236: aload_2
      //   237: getfield 127	android/widget/ActivityChooserModel$HistoricalRecord:weight	F
      //   240: invokestatic 130	java/lang/String:valueOf	(F)Ljava/lang/String;
      //   243: invokeinterface 114 4 0
      //   248: pop
      //   249: aload 4
      //   251: aconst_null
      //   252: ldc 100
      //   254: invokeinterface 133 3 0
      //   259: pop
      //   260: iinc 7 1
      //   263: goto -133 -> 130
      //   266: aload 4
      //   268: aconst_null
      //   269: ldc 84
      //   271: invokeinterface 133 3 0
      //   276: pop
      //   277: aload 4
      //   279: invokeinterface 136 1 0
      //   284: aload_0
      //   285: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   288: iconst_1
      //   289: invokestatic 140	android/widget/ActivityChooserModel:access$602	(Landroid/widget/ActivityChooserModel;Z)Z
      //   292: pop
      //   293: aload_3
      //   294: ifnull +231 -> 525
      //   297: aload_3
      //   298: invokevirtual 145	java/io/FileOutputStream:close	()V
      //   301: goto +217 -> 518
      //   304: astore_1
      //   305: goto +16 -> 321
      //   308: astore_1
      //   309: goto +79 -> 388
      //   312: astore_1
      //   313: goto +142 -> 455
      //   316: astore_1
      //   317: goto +211 -> 528
      //   320: astore_1
      //   321: invokestatic 148	android/widget/ActivityChooserModel:access$400	()Ljava/lang/String;
      //   324: astore_2
      //   325: new 150	java/lang/StringBuilder
      //   328: astore 5
      //   330: aload 5
      //   332: invokespecial 151	java/lang/StringBuilder:<init>	()V
      //   335: aload 5
      //   337: ldc -103
      //   339: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   342: pop
      //   343: aload 5
      //   345: aload_0
      //   346: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   349: invokestatic 161	android/widget/ActivityChooserModel:access$500	(Landroid/widget/ActivityChooserModel;)Ljava/lang/String;
      //   352: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   355: pop
      //   356: aload_2
      //   357: aload 5
      //   359: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   362: aload_1
      //   363: invokestatic 170	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   366: pop
      //   367: aload_0
      //   368: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   371: iconst_1
      //   372: invokestatic 140	android/widget/ActivityChooserModel:access$602	(Landroid/widget/ActivityChooserModel;Z)Z
      //   375: pop
      //   376: aload_3
      //   377: ifnull +148 -> 525
      //   380: aload_3
      //   381: invokevirtual 145	java/io/FileOutputStream:close	()V
      //   384: goto +134 -> 518
      //   387: astore_1
      //   388: invokestatic 148	android/widget/ActivityChooserModel:access$400	()Ljava/lang/String;
      //   391: astore_2
      //   392: new 150	java/lang/StringBuilder
      //   395: astore 5
      //   397: aload 5
      //   399: invokespecial 151	java/lang/StringBuilder:<init>	()V
      //   402: aload 5
      //   404: ldc -103
      //   406: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   409: pop
      //   410: aload 5
      //   412: aload_0
      //   413: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   416: invokestatic 161	android/widget/ActivityChooserModel:access$500	(Landroid/widget/ActivityChooserModel;)Ljava/lang/String;
      //   419: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   422: pop
      //   423: aload_2
      //   424: aload 5
      //   426: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   429: aload_1
      //   430: invokestatic 170	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   433: pop
      //   434: aload_0
      //   435: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   438: iconst_1
      //   439: invokestatic 140	android/widget/ActivityChooserModel:access$602	(Landroid/widget/ActivityChooserModel;Z)Z
      //   442: pop
      //   443: aload_3
      //   444: ifnull +81 -> 525
      //   447: aload_3
      //   448: invokevirtual 145	java/io/FileOutputStream:close	()V
      //   451: goto +67 -> 518
      //   454: astore_1
      //   455: invokestatic 148	android/widget/ActivityChooserModel:access$400	()Ljava/lang/String;
      //   458: astore_2
      //   459: new 150	java/lang/StringBuilder
      //   462: astore 5
      //   464: aload 5
      //   466: invokespecial 151	java/lang/StringBuilder:<init>	()V
      //   469: aload 5
      //   471: ldc -103
      //   473: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   476: pop
      //   477: aload 5
      //   479: aload_0
      //   480: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   483: invokestatic 161	android/widget/ActivityChooserModel:access$500	(Landroid/widget/ActivityChooserModel;)Ljava/lang/String;
      //   486: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   489: pop
      //   490: aload_2
      //   491: aload 5
      //   493: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   496: aload_1
      //   497: invokestatic 170	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   500: pop
      //   501: aload_0
      //   502: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   505: iconst_1
      //   506: invokestatic 140	android/widget/ActivityChooserModel:access$602	(Landroid/widget/ActivityChooserModel;Z)Z
      //   509: pop
      //   510: aload_3
      //   511: ifnull +14 -> 525
      //   514: aload_3
      //   515: invokevirtual 145	java/io/FileOutputStream:close	()V
      //   518: goto +7 -> 525
      //   521: astore_1
      //   522: goto -4 -> 518
      //   525: aconst_null
      //   526: areturn
      //   527: astore_1
      //   528: aload_0
      //   529: getfield 14	android/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/widget/ActivityChooserModel;
      //   532: iconst_1
      //   533: invokestatic 140	android/widget/ActivityChooserModel:access$602	(Landroid/widget/ActivityChooserModel;Z)Z
      //   536: pop
      //   537: aload_3
      //   538: ifnull +11 -> 549
      //   541: aload_3
      //   542: invokevirtual 145	java/io/FileOutputStream:close	()V
      //   545: goto +4 -> 549
      //   548: astore_2
      //   549: aload_1
      //   550: athrow
      //   551: astore 5
      //   553: invokestatic 148	android/widget/ActivityChooserModel:access$400	()Ljava/lang/String;
      //   556: astore_3
      //   557: new 150	java/lang/StringBuilder
      //   560: dup
      //   561: invokespecial 151	java/lang/StringBuilder:<init>	()V
      //   564: astore_2
      //   565: aload_2
      //   566: ldc -103
      //   568: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   571: pop
      //   572: aload_2
      //   573: aload_1
      //   574: invokevirtual 157	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   577: pop
      //   578: aload_3
      //   579: aload_2
      //   580: invokevirtual 164	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   583: aload 5
      //   585: invokestatic 170	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   588: pop
      //   589: aconst_null
      //   590: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	591	0	this	PersistHistoryAsyncTask
      //   0	591	1	paramVarArgs	Object[]
      //   6	485	2	localObject1	Object
      //   548	1	2	localIOException	java.io.IOException
      //   564	16	2	localStringBuilder	StringBuilder
      //   26	553	3	localObject2	Object
      //   30	248	4	localXmlSerializer	org.xmlpull.v1.XmlSerializer
      //   33	459	5	localObject3	Object
      //   551	33	5	localFileNotFoundException	java.io.FileNotFoundException
      //   123	12	6	i	int
      //   126	135	7	j	int
      // Exception table:
      //   from	to	target	type
      //   213	260	304	java/io/IOException
      //   266	284	304	java/io/IOException
      //   213	260	308	java/lang/IllegalStateException
      //   266	284	308	java/lang/IllegalStateException
      //   213	260	312	java/lang/IllegalArgumentException
      //   266	284	312	java/lang/IllegalArgumentException
      //   44	53	316	finally
      //   65	82	316	finally
      //   94	105	316	finally
      //   117	125	316	finally
      //   149	160	316	finally
      //   172	183	316	finally
      //   195	213	316	finally
      //   44	53	320	java/io/IOException
      //   65	82	320	java/io/IOException
      //   94	105	320	java/io/IOException
      //   117	125	320	java/io/IOException
      //   149	160	320	java/io/IOException
      //   172	183	320	java/io/IOException
      //   195	213	320	java/io/IOException
      //   44	53	387	java/lang/IllegalStateException
      //   65	82	387	java/lang/IllegalStateException
      //   94	105	387	java/lang/IllegalStateException
      //   117	125	387	java/lang/IllegalStateException
      //   149	160	387	java/lang/IllegalStateException
      //   172	183	387	java/lang/IllegalStateException
      //   195	213	387	java/lang/IllegalStateException
      //   44	53	454	java/lang/IllegalArgumentException
      //   65	82	454	java/lang/IllegalArgumentException
      //   94	105	454	java/lang/IllegalArgumentException
      //   117	125	454	java/lang/IllegalArgumentException
      //   149	160	454	java/lang/IllegalArgumentException
      //   172	183	454	java/lang/IllegalArgumentException
      //   195	213	454	java/lang/IllegalArgumentException
      //   297	301	521	java/io/IOException
      //   380	384	521	java/io/IOException
      //   447	451	521	java/io/IOException
      //   514	518	521	java/io/IOException
      //   213	260	527	finally
      //   266	284	527	finally
      //   321	367	527	finally
      //   388	434	527	finally
      //   455	501	527	finally
      //   541	545	548	java/io/IOException
      //   14	27	551	java/io/FileNotFoundException
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ActivityChooserModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */