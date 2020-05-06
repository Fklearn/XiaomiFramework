package org.ifaa.android.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Slog;
import miui.util.FeatureParser;
import org.json.JSONObject;

public class IFAAManagerImpl
  extends IFAAManagerV4
{
  private static final int CODE_GETIDLIST_CMD = 2;
  private static final int CODE_PROCESS_CMD = 1;
  private static final boolean DEBUG = false;
  private static final int IFAA_TYPE_2DFA = 32;
  private static final int IFAA_TYPE_FINGER = 1;
  private static final int IFAA_TYPE_IRIS = 2;
  private static final int IFAA_TYPE_SENSOR_FOD = 16;
  private static volatile IFAAManagerImpl INSTANCE = null;
  private static final String INTERFACE_DESCRIPTOR = "vendor.xiaomi.hardware.mlipay@1.0::IMlipayService";
  private static final String SERVICE_NAME = "vendor.xiaomi.hardware.mlipay@1.0::IMlipayService";
  private static final String TAG = "IfaaManagerImpl";
  private static ServiceConnection ifaaconn = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      IFAAManagerImpl.access$002(paramAnonymousIBinder);
      try
      {
        IFAAManagerImpl.mService.linkToDeath(IFAAManagerImpl.mDeathRecipient, 0);
      }
      catch (RemoteException paramAnonymousComponentName)
      {
        paramAnonymousIBinder = new StringBuilder();
        paramAnonymousIBinder.append("linkToDeath fail. ");
        paramAnonymousIBinder.append(paramAnonymousComponentName);
        Slog.e("IfaaManagerImpl", paramAnonymousIBinder.toString());
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      if (IFAAManagerImpl.mContext != null)
      {
        Slog.i("IfaaManagerImpl", "re-bind the service.");
        IFAAManagerImpl.access$300();
      }
    }
  };
  private static Context mContext;
  private static IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient()
  {
    public void binderDied()
    {
      if (IFAAManagerImpl.mService == null) {
        return;
      }
      Slog.d("IfaaManagerImpl", "binderDied, unlink the service.");
      IFAAManagerImpl.mService.unlinkToDeath(IFAAManagerImpl.mDeathRecipient, 0);
    }
  };
  private static final String mFingerActName = "com.android.settings.NewFingerprintActivity";
  private static final String mFingerPackName = "com.android.settings";
  private static final String mIfaaActName = "org.ifaa.android.manager.IFAAService";
  private static final String mIfaaInterfaceDesc = "org.ifaa.android.manager.IIFAAService";
  private static final String mIfaaPackName = "com.tencent.soter.soterserver";
  private static IBinder mService;
  private static final String seperate = ",";
  private String mDevModel = null;
  
  public static IFAAManagerV4 getInstance(Context paramContext)
  {
    if (INSTANCE == null) {
      try
      {
        if (INSTANCE == null)
        {
          IFAAManagerImpl localIFAAManagerImpl = new org/ifaa/android/manager/IFAAManagerImpl;
          localIFAAManagerImpl.<init>();
          INSTANCE = localIFAAManagerImpl;
          if (Build.VERSION.SDK_INT >= 28)
          {
            mContext = paramContext;
            initService();
          }
        }
      }
      finally {}
    }
    return INSTANCE;
  }
  
  private String initExtString()
  {
    Object localObject1 = "";
    Object localObject2 = new JSONObject();
    Object localObject3 = new JSONObject();
    String str1;
    String str2;
    if (Build.VERSION.SDK_INT >= 28)
    {
      str1 = SystemProperties.get("persist.vendor.sys.fp.fod.location.X_Y", "");
      str2 = SystemProperties.get("persist.vendor.sys.fp.fod.size.width_height", "");
    }
    else
    {
      str1 = SystemProperties.get("persist.sys.fp.fod.location.X_Y", "");
      str2 = SystemProperties.get("persist.sys.fp.fod.size.width_height", "");
    }
    try
    {
      if ((validateVal(str1)) && (validateVal(str2)))
      {
        String[] arrayOfString1 = str1.split(",");
        String[] arrayOfString2 = str2.split(",");
        ((JSONObject)localObject3).put("startX", Integer.parseInt(arrayOfString1[0]));
        ((JSONObject)localObject3).put("startY", Integer.parseInt(arrayOfString1[1]));
        ((JSONObject)localObject3).put("width", Integer.parseInt(arrayOfString2[0]));
        ((JSONObject)localObject3).put("height", Integer.parseInt(arrayOfString2[1]));
        ((JSONObject)localObject3).put("navConflict", true);
        ((JSONObject)localObject2).put("type", 0);
        ((JSONObject)localObject2).put("fullView", localObject3);
        localObject3 = ((JSONObject)localObject2).toString();
        localObject1 = localObject3;
      }
      else
      {
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        ((StringBuilder)localObject3).append("initExtString invalidate, xy:");
        ((StringBuilder)localObject3).append(str1);
        ((StringBuilder)localObject3).append(" wh:");
        ((StringBuilder)localObject3).append(str2);
        Slog.e("IfaaManagerImpl", ((StringBuilder)localObject3).toString());
      }
    }
    catch (Exception localException)
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("Exception , xy:");
      ((StringBuilder)localObject2).append(str1);
      ((StringBuilder)localObject2).append(" wh:");
      ((StringBuilder)localObject2).append(str2);
      Slog.e("IfaaManagerImpl", ((StringBuilder)localObject2).toString(), localException);
    }
    return (String)localObject1;
  }
  
  private static void initService()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.tencent.soter.soterserver", "org.ifaa.android.manager.IFAAService");
    if (!mContext.bindService(localIntent, ifaaconn, 1)) {
      Slog.e("IfaaManagerImpl", "cannot bind service org.ifaa.android.manager.IFAAService");
    }
  }
  
  private boolean validateVal(String paramString)
  {
    return (!"".equalsIgnoreCase(paramString)) && (paramString.contains(","));
  }
  
  public String getDeviceModel()
  {
    if (this.mDevModel == null)
    {
      localObject = FeatureParser.getString("finger_alipay_ifaa_model");
      if ((localObject != null) && (!"".equalsIgnoreCase((String)localObject)))
      {
        this.mDevModel = ((String)localObject);
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(Build.MANUFACTURER);
        ((StringBuilder)localObject).append("-");
        ((StringBuilder)localObject).append(Build.DEVICE);
        this.mDevModel = ((StringBuilder)localObject).toString();
      }
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("getDeviceModel devcieModel:");
    ((StringBuilder)localObject).append(this.mDevModel);
    Slog.i("IfaaManagerImpl", ((StringBuilder)localObject).toString());
    return this.mDevModel;
  }
  
  public int getEnabled(int paramInt)
  {
    int i = 1003;
    if (1 == paramInt) {
      i = 1000;
    }
    return i;
  }
  
  public String getExtInfo(int paramInt, String paramString)
  {
    return initExtString();
  }
  
  /* Error */
  public int[] getIDList(int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: newarray <illegal type>
    //   3: astore_2
    //   4: aload_2
    //   5: iconst_0
    //   6: iconst_0
    //   7: iastore
    //   8: aload_2
    //   9: astore_3
    //   10: iconst_1
    //   11: iload_1
    //   12: if_icmpne +218 -> 230
    //   15: bipush 10
    //   17: istore 4
    //   19: iload 4
    //   21: iconst_1
    //   22: isub
    //   23: istore 5
    //   25: aload_2
    //   26: astore_3
    //   27: iload 4
    //   29: ifle +201 -> 230
    //   32: getstatic 79	org/ifaa/android/manager/IFAAManagerImpl:mService	Landroid/os/IBinder;
    //   35: astore_3
    //   36: aload_3
    //   37: ifnull +137 -> 174
    //   40: aload_3
    //   41: invokeinterface 252 1 0
    //   46: ifeq +128 -> 174
    //   49: invokestatic 258	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   52: astore 6
    //   54: invokestatic 258	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   57: astore 7
    //   59: aload 6
    //   61: ldc 50
    //   63: invokevirtual 262	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   66: aload 6
    //   68: iload_1
    //   69: invokevirtual 266	android/os/Parcel:writeInt	(I)V
    //   72: getstatic 79	org/ifaa/android/manager/IFAAManagerImpl:mService	Landroid/os/IBinder;
    //   75: iconst_2
    //   76: aload 6
    //   78: aload 7
    //   80: iconst_0
    //   81: invokeinterface 270 5 0
    //   86: pop
    //   87: aload 7
    //   89: invokevirtual 273	android/os/Parcel:readException	()V
    //   92: aload 7
    //   94: invokevirtual 277	android/os/Parcel:createIntArray	()[I
    //   97: astore_3
    //   98: aload_3
    //   99: astore_2
    //   100: aload 6
    //   102: invokevirtual 280	android/os/Parcel:recycle	()V
    //   105: aload 7
    //   107: invokevirtual 280	android/os/Parcel:recycle	()V
    //   110: goto +45 -> 155
    //   113: astore_2
    //   114: goto +48 -> 162
    //   117: astore 8
    //   119: new 167	java/lang/StringBuilder
    //   122: astore_3
    //   123: aload_3
    //   124: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   127: aload_3
    //   128: ldc_w 282
    //   131: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: pop
    //   135: aload_3
    //   136: aload 8
    //   138: invokevirtual 285	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   141: pop
    //   142: ldc 32
    //   144: aload_3
    //   145: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: invokestatic 183	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   151: pop
    //   152: goto -52 -> 100
    //   155: iload 5
    //   157: istore 4
    //   159: goto -140 -> 19
    //   162: aload 6
    //   164: invokevirtual 280	android/os/Parcel:recycle	()V
    //   167: aload 7
    //   169: invokevirtual 280	android/os/Parcel:recycle	()V
    //   172: aload_2
    //   173: athrow
    //   174: ldc2_w 286
    //   177: invokestatic 293	java/lang/Thread:sleep	(J)V
    //   180: goto +43 -> 223
    //   183: astore_3
    //   184: new 167	java/lang/StringBuilder
    //   187: dup
    //   188: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   191: astore 6
    //   193: aload 6
    //   195: ldc_w 295
    //   198: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: pop
    //   202: aload 6
    //   204: aload_3
    //   205: invokevirtual 285	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   208: pop
    //   209: ldc 32
    //   211: aload 6
    //   213: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   216: invokestatic 183	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   219: pop
    //   220: goto -40 -> 180
    //   223: iload 5
    //   225: istore 4
    //   227: goto -208 -> 19
    //   230: aload_3
    //   231: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	IFAAManagerImpl
    //   0	232	1	paramInt	int
    //   3	97	2	localObject1	Object
    //   113	60	2	localObject2	Object
    //   9	136	3	localObject3	Object
    //   183	48	3	localInterruptedException	InterruptedException
    //   17	209	4	i	int
    //   23	201	5	j	int
    //   52	160	6	localObject4	Object
    //   57	111	7	localParcel	android.os.Parcel
    //   117	20	8	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   59	98	113	finally
    //   119	152	113	finally
    //   59	98	117	android/os/RemoteException
    //   174	180	183	java/lang/InterruptedException
  }
  
  public int getSupportBIOTypes(Context paramContext)
  {
    int i = FeatureParser.getInteger("ifaa_2dfa_support", 0);
    int j;
    if (Build.VERSION.SDK_INT >= 28)
    {
      j = SystemProperties.getInt("persist.vendor.sys.pay.ifaa", 0);
      paramContext = SystemProperties.get("persist.vendor.sys.fp.vendor", "");
    }
    else
    {
      j = SystemProperties.getInt("persist.sys.ifaa", 0);
      paramContext = SystemProperties.get("persist.sys.fp.vendor", "");
    }
    if ("none".equalsIgnoreCase(paramContext)) {
      k = j & 0x2;
    } else {
      k = j & 0x3;
    }
    int m = k;
    if ((k & 0x1) == 1)
    {
      m = k;
      if (sIsFod) {
        m = k | 0x10;
      }
    }
    int k = m;
    if (i == 1)
    {
      k = m;
      if ("enable".equals(SystemProperties.get("ro.boot.hypvm", ""))) {
        k = m | 0x20;
      }
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("getSupportBIOTypesV26:");
    localStringBuilder.append(j);
    localStringBuilder.append(" ");
    localStringBuilder.append(sIsFod);
    localStringBuilder.append(" ");
    localStringBuilder.append(paramContext);
    localStringBuilder.append(" res:");
    localStringBuilder.append(k);
    Slog.i("IfaaManagerImpl", localStringBuilder.toString());
    return k;
  }
  
  public int getVersion()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("getVersion sdk:");
    localStringBuilder.append(Build.VERSION.SDK_INT);
    localStringBuilder.append(" ifaaVer:");
    localStringBuilder.append(sIfaaVer);
    Slog.i("IfaaManagerImpl", localStringBuilder.toString());
    return sIfaaVer;
  }
  
  /* Error */
  public byte[] processCmdV2(Context paramContext, byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: new 167	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   7: astore_1
    //   8: aload_1
    //   9: ldc_w 352
    //   12: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: pop
    //   16: aload_1
    //   17: getstatic 99	android/os/Build$VERSION:SDK_INT	I
    //   20: invokevirtual 332	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   23: pop
    //   24: ldc 32
    //   26: aload_1
    //   27: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   30: invokestatic 234	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   33: pop
    //   34: getstatic 99	android/os/Build$VERSION:SDK_INT	I
    //   37: bipush 28
    //   39: if_icmplt +254 -> 293
    //   42: bipush 10
    //   44: istore_3
    //   45: iload_3
    //   46: iconst_1
    //   47: isub
    //   48: istore 4
    //   50: iload_3
    //   51: ifle +239 -> 290
    //   54: getstatic 79	org/ifaa/android/manager/IFAAManagerImpl:mService	Landroid/os/IBinder;
    //   57: astore_1
    //   58: aload_1
    //   59: ifnull +137 -> 196
    //   62: aload_1
    //   63: invokeinterface 252 1 0
    //   68: ifeq +128 -> 196
    //   71: invokestatic 258	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   74: astore 5
    //   76: invokestatic 258	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   79: astore_1
    //   80: aload 5
    //   82: ldc 50
    //   84: invokevirtual 262	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   87: aload 5
    //   89: aload_2
    //   90: invokevirtual 356	android/os/Parcel:writeByteArray	([B)V
    //   93: getstatic 79	org/ifaa/android/manager/IFAAManagerImpl:mService	Landroid/os/IBinder;
    //   96: iconst_1
    //   97: aload 5
    //   99: aload_1
    //   100: iconst_0
    //   101: invokeinterface 270 5 0
    //   106: pop
    //   107: aload_1
    //   108: invokevirtual 273	android/os/Parcel:readException	()V
    //   111: aload_1
    //   112: invokevirtual 360	android/os/Parcel:createByteArray	()[B
    //   115: astore 6
    //   117: aload 5
    //   119: invokevirtual 280	android/os/Parcel:recycle	()V
    //   122: aload_1
    //   123: invokevirtual 280	android/os/Parcel:recycle	()V
    //   126: aload 6
    //   128: areturn
    //   129: astore_2
    //   130: goto +55 -> 185
    //   133: astore 7
    //   135: new 167	java/lang/StringBuilder
    //   138: astore 6
    //   140: aload 6
    //   142: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   145: aload 6
    //   147: ldc_w 362
    //   150: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   153: pop
    //   154: aload 6
    //   156: aload 7
    //   158: invokevirtual 285	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   161: pop
    //   162: ldc 32
    //   164: aload 6
    //   166: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   169: invokestatic 183	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   172: pop
    //   173: aload 5
    //   175: invokevirtual 280	android/os/Parcel:recycle	()V
    //   178: aload_1
    //   179: invokevirtual 280	android/os/Parcel:recycle	()V
    //   182: goto +102 -> 284
    //   185: aload 5
    //   187: invokevirtual 280	android/os/Parcel:recycle	()V
    //   190: aload_1
    //   191: invokevirtual 280	android/os/Parcel:recycle	()V
    //   194: aload_2
    //   195: athrow
    //   196: new 167	java/lang/StringBuilder
    //   199: dup
    //   200: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   203: astore_1
    //   204: aload_1
    //   205: ldc_w 364
    //   208: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   211: pop
    //   212: aload_1
    //   213: iload 4
    //   215: invokevirtual 332	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   218: pop
    //   219: aload_1
    //   220: ldc_w 366
    //   223: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   226: pop
    //   227: ldc 32
    //   229: aload_1
    //   230: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   233: invokestatic 234	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   236: pop
    //   237: ldc2_w 286
    //   240: invokestatic 293	java/lang/Thread:sleep	(J)V
    //   243: goto +41 -> 284
    //   246: astore 5
    //   248: new 167	java/lang/StringBuilder
    //   251: dup
    //   252: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   255: astore_1
    //   256: aload_1
    //   257: ldc_w 368
    //   260: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: pop
    //   264: aload_1
    //   265: aload 5
    //   267: invokevirtual 285	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   270: pop
    //   271: ldc 32
    //   273: aload_1
    //   274: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   277: invokestatic 183	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   280: pop
    //   281: goto -38 -> 243
    //   284: iload 4
    //   286: istore_3
    //   287: goto -242 -> 45
    //   290: goto +203 -> 493
    //   293: new 370	android/os/HwParcel
    //   296: dup
    //   297: invokespecial 371	android/os/HwParcel:<init>	()V
    //   300: astore_1
    //   301: ldc 28
    //   303: ldc_w 373
    //   306: invokestatic 379	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   309: astore 7
    //   311: aload 7
    //   313: ifnull +128 -> 441
    //   316: new 370	android/os/HwParcel
    //   319: astore 6
    //   321: aload 6
    //   323: invokespecial 371	android/os/HwParcel:<init>	()V
    //   326: aload 6
    //   328: ldc 28
    //   330: invokevirtual 380	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   333: new 382	java/util/ArrayList
    //   336: astore 5
    //   338: aload 5
    //   340: aload_2
    //   341: invokestatic 388	android/os/HwBlob:wrapArray	([B)[Ljava/lang/Byte;
    //   344: invokestatic 394	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
    //   347: invokespecial 397	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
    //   350: aload 6
    //   352: aload 5
    //   354: invokevirtual 401	android/os/HwParcel:writeInt8Vector	(Ljava/util/ArrayList;)V
    //   357: aload 6
    //   359: aload 5
    //   361: invokevirtual 404	java/util/ArrayList:size	()I
    //   364: invokevirtual 407	android/os/HwParcel:writeInt32	(I)V
    //   367: aload 7
    //   369: iconst_1
    //   370: aload 6
    //   372: aload_1
    //   373: iconst_0
    //   374: invokeinterface 412 5 0
    //   379: aload_1
    //   380: invokevirtual 415	android/os/HwParcel:verifySuccess	()V
    //   383: aload 6
    //   385: invokevirtual 418	android/os/HwParcel:releaseTemporaryStorage	()V
    //   388: aload_1
    //   389: invokevirtual 422	android/os/HwParcel:readInt8Vector	()Ljava/util/ArrayList;
    //   392: astore_2
    //   393: aload_2
    //   394: invokevirtual 404	java/util/ArrayList:size	()I
    //   397: istore 4
    //   399: iload 4
    //   401: newarray <illegal type>
    //   403: astore 5
    //   405: iconst_0
    //   406: istore_3
    //   407: iload_3
    //   408: iload 4
    //   410: if_icmpge +24 -> 434
    //   413: aload 5
    //   415: iload_3
    //   416: aload_2
    //   417: iload_3
    //   418: invokevirtual 425	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   421: checkcast 427	java/lang/Byte
    //   424: invokevirtual 431	java/lang/Byte:byteValue	()B
    //   427: bastore
    //   428: iinc 3 1
    //   431: goto -24 -> 407
    //   434: aload_1
    //   435: invokevirtual 434	android/os/HwParcel:release	()V
    //   438: aload 5
    //   440: areturn
    //   441: aload_1
    //   442: invokevirtual 434	android/os/HwParcel:release	()V
    //   445: goto +48 -> 493
    //   448: astore_2
    //   449: goto +55 -> 504
    //   452: astore_2
    //   453: new 167	java/lang/StringBuilder
    //   456: astore 5
    //   458: aload 5
    //   460: invokespecial 168	java/lang/StringBuilder:<init>	()V
    //   463: aload 5
    //   465: ldc_w 436
    //   468: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   471: pop
    //   472: aload 5
    //   474: aload_2
    //   475: invokevirtual 285	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   478: pop
    //   479: ldc 32
    //   481: aload 5
    //   483: invokevirtual 177	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   486: invokestatic 183	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   489: pop
    //   490: goto -49 -> 441
    //   493: ldc 32
    //   495: ldc_w 438
    //   498: invokestatic 183	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   501: pop
    //   502: aconst_null
    //   503: areturn
    //   504: aload_1
    //   505: invokevirtual 434	android/os/HwParcel:release	()V
    //   508: aload_2
    //   509: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	510	0	this	IFAAManagerImpl
    //   0	510	1	paramContext	Context
    //   0	510	2	paramArrayOfByte	byte[]
    //   44	385	3	i	int
    //   48	363	4	j	int
    //   74	112	5	localParcel	android.os.Parcel
    //   246	20	5	localInterruptedException	InterruptedException
    //   336	146	5	localObject1	Object
    //   115	269	6	localObject2	Object
    //   133	24	7	localRemoteException	RemoteException
    //   309	59	7	localIHwBinder	android.os.IHwBinder
    // Exception table:
    //   from	to	target	type
    //   80	117	129	finally
    //   135	173	129	finally
    //   80	117	133	android/os/RemoteException
    //   237	243	246	java/lang/InterruptedException
    //   301	311	448	finally
    //   316	405	448	finally
    //   413	428	448	finally
    //   453	490	448	finally
    //   301	311	452	android/os/RemoteException
    //   316	405	452	android/os/RemoteException
    //   413	428	452	android/os/RemoteException
  }
  
  public void setExtInfo(int paramInt, String paramString1, String paramString2) {}
  
  public int startBIOManager(Context paramContext, int paramInt)
  {
    int i = -1;
    if (1 == paramInt)
    {
      Intent localIntent = new Intent();
      localIntent.setClassName("com.android.settings", "com.android.settings.NewFingerprintActivity");
      localIntent.setFlags(268435456);
      paramContext.startActivity(localIntent);
      i = 0;
    }
    paramContext = new StringBuilder();
    paramContext.append("startBIOManager authType:");
    paramContext.append(paramInt);
    paramContext.append(" res:");
    paramContext.append(i);
    Slog.i("IfaaManagerImpl", paramContext.toString());
    return i;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/IFAAManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */