package org.mipay.android.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.HwBinder;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IHwBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Slog;

public class MipayManagerImpl
  implements IMipayManager
{
  private static int CODE_CONTAINS;
  private static int CODE_GEN_KEY_PAIR;
  private static int CODE_GET_FP_IDS;
  private static int CODE_RM_ALL_KEY;
  private static int CODE_SIGN;
  private static int CODE_SIGN_INIT;
  private static int CODE_SIGN_UPDATE;
  private static boolean DEBUG;
  private static volatile MipayManagerImpl INSTANCE = null;
  private static String INTERFACE_DESCRIPTOR;
  private static int MIPAY_TYPE_FINGER;
  private static int MIPAY_TYPE_IRIS;
  private static int MIPAY_VERISON_1;
  private static String SERVICE_NAME;
  private static String TAG = "MipayManagerImpl";
  private static Context mContext;
  private static IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient()
  {
    public void binderDied()
    {
      if (MipayManagerImpl.mService == null) {
        return;
      }
      Slog.i(MipayManagerImpl.TAG, "binderDied, unlink the service.");
      MipayManagerImpl.mService.unlinkToDeath(MipayManagerImpl.mDeathRecipient, 0);
    }
  };
  private static String mPackName;
  private static IBinder mService;
  private static String mTidaActName;
  private static String mTidaInterfaceDesc;
  private static ServiceConnection mipayconn = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Object localObject;
      if (MipayManagerImpl.DEBUG)
      {
        String str = MipayManagerImpl.TAG;
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("onServiceConnected name = ");
        ((StringBuilder)localObject).append(paramAnonymousComponentName);
        Slog.i(str, ((StringBuilder)localObject).toString());
      }
      MipayManagerImpl.access$602(paramAnonymousIBinder);
      try
      {
        MipayManagerImpl.mService.linkToDeath(MipayManagerImpl.mDeathRecipient, 0);
      }
      catch (RemoteException paramAnonymousComponentName)
      {
        localObject = MipayManagerImpl.TAG;
        paramAnonymousIBinder = new StringBuilder();
        paramAnonymousIBinder.append("linkToDeath fail. ");
        paramAnonymousIBinder.append(paramAnonymousComponentName);
        Slog.e((String)localObject, paramAnonymousIBinder.toString());
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      if (MipayManagerImpl.DEBUG)
      {
        String str = MipayManagerImpl.TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("onServiceDisconnected name = ");
        localStringBuilder.append(paramAnonymousComponentName);
        Slog.i(str, localStringBuilder.toString());
      }
      if (MipayManagerImpl.mContext != null)
      {
        Slog.i(MipayManagerImpl.TAG, "re-bind the service.");
        MipayManagerImpl.access$800();
      }
    }
  };
  private IHwBinder mHwService;
  
  static
  {
    DEBUG = false;
    MIPAY_VERISON_1 = 1;
    MIPAY_TYPE_FINGER = 1;
    MIPAY_TYPE_IRIS = 2;
    CODE_CONTAINS = 1;
    CODE_GEN_KEY_PAIR = 2;
    CODE_SIGN_INIT = 3;
    CODE_SIGN_UPDATE = 4;
    CODE_SIGN = 5;
    CODE_RM_ALL_KEY = 6;
    CODE_GET_FP_IDS = 7;
    SERVICE_NAME = "vendor.xiaomi.hardware.tidaservice@1.0::ITidaService";
    INTERFACE_DESCRIPTOR = "vendor.xiaomi.hardware.tidaservice@1.0::ITidaService";
    mPackName = "com.tencent.soter.soterserver";
    mTidaActName = "org.mipay.android.manager.MipayService";
    mTidaInterfaceDesc = "org.mipay.android.manager.IMipayService";
  }
  
  private static void bindTidaService()
  {
    Thread local1 = new Thread("TidaThread")
    {
      public void run()
      {
        Object localObject = new Intent();
        ((Intent)localObject).setClassName(MipayManagerImpl.mPackName, MipayManagerImpl.mTidaActName);
        if (!MipayManagerImpl.mContext.bindService((Intent)localObject, MipayManagerImpl.mipayconn, 1))
        {
          String str = MipayManagerImpl.TAG;
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("cannot bind service ");
          ((StringBuilder)localObject).append(MipayManagerImpl.mTidaActName);
          Slog.e(str, ((StringBuilder)localObject).toString());
        }
        if (MipayManagerImpl.DEBUG) {
          Slog.i(MipayManagerImpl.TAG, "Tida client calling joinThreadPool");
        }
        Binder.joinThreadPool();
      }
    };
    local1.setDaemon(true);
    local1.start();
  }
  
  private static int connectService(int paramInt, Parcel paramParcel1, Parcel paramParcel2)
  {
    int i = 10;
    int j = -1;
    int m;
    for (;;)
    {
      int k = i - 1;
      m = j;
      if (i <= 0) {
        break;
      }
      IBinder localIBinder = mService;
      Object localObject2;
      if ((localIBinder != null) && (localIBinder.pingBinder()))
      {
        try
        {
          mService.transact(paramInt, paramParcel1, paramParcel2, 0);
          m = 0;
        }
        catch (RemoteException localRemoteException)
        {
          String str = TAG;
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("connectService transact failed. ");
          ((StringBuilder)localObject2).append(localRemoteException);
          Slog.e(str, ((StringBuilder)localObject2).toString());
          break label208;
        }
      }
      else
      {
        Object localObject1 = TAG;
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("connectService waiting mipayService, remain: ");
        ((StringBuilder)localObject2).append(k);
        ((StringBuilder)localObject2).append(" time(s)");
        Slog.i((String)localObject1, ((StringBuilder)localObject2).toString());
        try
        {
          Thread.sleep(30L);
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;)
          {
            localObject2 = TAG;
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("connectService InterruptedException while waiting: ");
            ((StringBuilder)localObject1).append(localInterruptedException);
            Slog.e((String)localObject2, ((StringBuilder)localObject1).toString());
          }
        }
      }
      label208:
      i = k;
    }
    return m;
  }
  
  public static IMipayManager getInstance(Context paramContext)
  {
    if (INSTANCE == null) {
      try
      {
        if (INSTANCE == null)
        {
          MipayManagerImpl localMipayManagerImpl = new org/mipay/android/manager/MipayManagerImpl;
          localMipayManagerImpl.<init>();
          INSTANCE = localMipayManagerImpl;
          if (Build.VERSION.SDK_INT >= 28)
          {
            mContext = paramContext;
            bindTidaService();
          }
        }
      }
      finally {}
    }
    return INSTANCE;
  }
  
  private void initService()
    throws RemoteException
  {
    if (this.mHwService == null) {
      this.mHwService = HwBinder.getService(SERVICE_NAME, "default");
    }
  }
  
  /* Error */
  private int signUpdate(String paramString)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_2
    //   2: getstatic 202	android/os/Build$VERSION:SDK_INT	I
    //   5: bipush 28
    //   7: if_icmplt +98 -> 105
    //   10: invokestatic 222	android/os/Binder:getCallingUid	()I
    //   13: istore_3
    //   14: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   17: astore 4
    //   19: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   22: astore 5
    //   24: aload 4
    //   26: getstatic 90	org/mipay/android/manager/MipayManagerImpl:mTidaInterfaceDesc	Ljava/lang/String;
    //   29: invokevirtual 231	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   32: aload 4
    //   34: iload_3
    //   35: invokevirtual 235	android/os/Parcel:writeInt	(I)V
    //   38: aload 4
    //   40: aload_1
    //   41: invokevirtual 238	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   44: getstatic 66	org/mipay/android/manager/MipayManagerImpl:CODE_SIGN_UPDATE	I
    //   47: aload 4
    //   49: aload 5
    //   51: invokestatic 240	org/mipay/android/manager/MipayManagerImpl:connectService	(ILandroid/os/Parcel;Landroid/os/Parcel;)I
    //   54: istore_3
    //   55: iload_3
    //   56: ifeq +25 -> 81
    //   59: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   62: ldc -14
    //   64: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   67: pop
    //   68: aload 4
    //   70: invokevirtual 245	android/os/Parcel:recycle	()V
    //   73: aload 5
    //   75: invokevirtual 245	android/os/Parcel:recycle	()V
    //   78: goto +24 -> 102
    //   81: aload 5
    //   83: invokevirtual 248	android/os/Parcel:readException	()V
    //   86: aload 5
    //   88: invokevirtual 251	android/os/Parcel:readInt	()I
    //   91: istore_3
    //   92: aload 4
    //   94: invokevirtual 245	android/os/Parcel:recycle	()V
    //   97: aload 5
    //   99: invokevirtual 245	android/os/Parcel:recycle	()V
    //   102: goto +142 -> 244
    //   105: new 253	android/os/HwParcel
    //   108: dup
    //   109: invokespecial 254	android/os/HwParcel:<init>	()V
    //   112: astore 4
    //   114: aload_0
    //   115: invokespecial 256	org/mipay/android/manager/MipayManagerImpl:initService	()V
    //   118: iload_2
    //   119: istore_3
    //   120: aload_0
    //   121: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   124: ifnull +60 -> 184
    //   127: new 253	android/os/HwParcel
    //   130: astore 5
    //   132: aload 5
    //   134: invokespecial 254	android/os/HwParcel:<init>	()V
    //   137: aload 5
    //   139: getstatic 78	org/mipay/android/manager/MipayManagerImpl:INTERFACE_DESCRIPTOR	Ljava/lang/String;
    //   142: invokevirtual 257	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   145: aload 5
    //   147: aload_1
    //   148: invokevirtual 258	android/os/HwParcel:writeString	(Ljava/lang/String;)V
    //   151: aload_0
    //   152: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   155: getstatic 66	org/mipay/android/manager/MipayManagerImpl:CODE_SIGN_UPDATE	I
    //   158: aload 5
    //   160: aload 4
    //   162: iconst_0
    //   163: invokeinterface 263 5 0
    //   168: aload 4
    //   170: invokevirtual 266	android/os/HwParcel:verifySuccess	()V
    //   173: aload 5
    //   175: invokevirtual 269	android/os/HwParcel:releaseTemporaryStorage	()V
    //   178: aload 4
    //   180: invokevirtual 272	android/os/HwParcel:readInt32	()I
    //   183: istore_3
    //   184: aload 4
    //   186: invokevirtual 275	android/os/HwParcel:release	()V
    //   189: goto +55 -> 244
    //   192: astore_1
    //   193: goto +98 -> 291
    //   196: astore 5
    //   198: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   201: astore_1
    //   202: new 157	java/lang/StringBuilder
    //   205: astore 6
    //   207: aload 6
    //   209: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   212: aload 6
    //   214: ldc_w 277
    //   217: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: pop
    //   221: aload 6
    //   223: aload 5
    //   225: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   228: pop
    //   229: aload_1
    //   230: aload 6
    //   232: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   235: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   238: pop
    //   239: iload_2
    //   240: istore_3
    //   241: goto -57 -> 184
    //   244: getstatic 52	org/mipay/android/manager/MipayManagerImpl:DEBUG	Z
    //   247: ifeq +42 -> 289
    //   250: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   253: astore_1
    //   254: new 157	java/lang/StringBuilder
    //   257: dup
    //   258: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   261: astore 4
    //   263: aload 4
    //   265: ldc_w 279
    //   268: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   271: pop
    //   272: aload 4
    //   274: iload_3
    //   275: invokevirtual 181	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   278: pop
    //   279: aload_1
    //   280: aload 4
    //   282: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   285: invokestatic 186	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   288: pop
    //   289: iload_3
    //   290: ireturn
    //   291: aload 4
    //   293: invokevirtual 275	android/os/HwParcel:release	()V
    //   296: aload_1
    //   297: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	298	0	this	MipayManagerImpl
    //   0	298	1	paramString	String
    //   1	239	2	i	int
    //   13	277	3	j	int
    //   17	275	4	localObject1	Object
    //   22	152	5	localObject2	Object
    //   196	28	5	localRemoteException	RemoteException
    //   205	26	6	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   114	118	192	finally
    //   120	184	192	finally
    //   198	239	192	finally
    //   114	118	196	android/os/RemoteException
    //   120	184	196	android/os/RemoteException
  }
  
  /* Error */
  public boolean contains(String paramString)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: iconst_0
    //   3: istore_3
    //   4: iconst_0
    //   5: istore 4
    //   7: getstatic 202	android/os/Build$VERSION:SDK_INT	I
    //   10: bipush 28
    //   12: if_icmplt +99 -> 111
    //   15: invokestatic 222	android/os/Binder:getCallingUid	()I
    //   18: istore 5
    //   20: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   23: astore 6
    //   25: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   28: astore 7
    //   30: aload 6
    //   32: getstatic 90	org/mipay/android/manager/MipayManagerImpl:mTidaInterfaceDesc	Ljava/lang/String;
    //   35: invokevirtual 231	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   38: aload 6
    //   40: iload 5
    //   42: invokevirtual 235	android/os/Parcel:writeInt	(I)V
    //   45: aload 6
    //   47: aload_1
    //   48: invokevirtual 238	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   51: getstatic 60	org/mipay/android/manager/MipayManagerImpl:CODE_CONTAINS	I
    //   54: aload 6
    //   56: aload 7
    //   58: invokestatic 240	org/mipay/android/manager/MipayManagerImpl:connectService	(ILandroid/os/Parcel;Landroid/os/Parcel;)I
    //   61: ifeq +25 -> 86
    //   64: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   67: ldc -14
    //   69: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   72: pop
    //   73: aload 6
    //   75: invokevirtual 245	android/os/Parcel:recycle	()V
    //   78: aload 7
    //   80: invokevirtual 245	android/os/Parcel:recycle	()V
    //   83: goto +25 -> 108
    //   86: aload 7
    //   88: invokevirtual 248	android/os/Parcel:readException	()V
    //   91: aload 7
    //   93: invokevirtual 284	android/os/Parcel:readBoolean	()Z
    //   96: istore 4
    //   98: aload 6
    //   100: invokevirtual 245	android/os/Parcel:recycle	()V
    //   103: aload 7
    //   105: invokevirtual 245	android/os/Parcel:recycle	()V
    //   108: goto +147 -> 255
    //   111: new 253	android/os/HwParcel
    //   114: dup
    //   115: invokespecial 254	android/os/HwParcel:<init>	()V
    //   118: astore 6
    //   120: aload_0
    //   121: invokespecial 256	org/mipay/android/manager/MipayManagerImpl:initService	()V
    //   124: iload_2
    //   125: istore 4
    //   127: aload_0
    //   128: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   131: ifnull +61 -> 192
    //   134: new 253	android/os/HwParcel
    //   137: astore 7
    //   139: aload 7
    //   141: invokespecial 254	android/os/HwParcel:<init>	()V
    //   144: aload 7
    //   146: getstatic 78	org/mipay/android/manager/MipayManagerImpl:INTERFACE_DESCRIPTOR	Ljava/lang/String;
    //   149: invokevirtual 257	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   152: aload 7
    //   154: aload_1
    //   155: invokevirtual 258	android/os/HwParcel:writeString	(Ljava/lang/String;)V
    //   158: aload_0
    //   159: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   162: getstatic 60	org/mipay/android/manager/MipayManagerImpl:CODE_CONTAINS	I
    //   165: aload 7
    //   167: aload 6
    //   169: iconst_0
    //   170: invokeinterface 263 5 0
    //   175: aload 6
    //   177: invokevirtual 266	android/os/HwParcel:verifySuccess	()V
    //   180: aload 7
    //   182: invokevirtual 269	android/os/HwParcel:releaseTemporaryStorage	()V
    //   185: aload 6
    //   187: invokevirtual 287	android/os/HwParcel:readBool	()Z
    //   190: istore 4
    //   192: aload 6
    //   194: invokevirtual 275	android/os/HwParcel:release	()V
    //   197: goto +58 -> 255
    //   200: astore_1
    //   201: goto +121 -> 322
    //   204: astore 8
    //   206: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   209: astore 9
    //   211: new 157	java/lang/StringBuilder
    //   214: astore 7
    //   216: aload 7
    //   218: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   221: aload 7
    //   223: ldc_w 277
    //   226: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   229: pop
    //   230: aload 7
    //   232: aload 8
    //   234: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   237: pop
    //   238: aload 9
    //   240: aload 7
    //   242: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   245: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   248: pop
    //   249: iload_3
    //   250: istore 4
    //   252: goto -60 -> 192
    //   255: getstatic 52	org/mipay/android/manager/MipayManagerImpl:DEBUG	Z
    //   258: ifeq +61 -> 319
    //   261: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   264: astore 7
    //   266: new 157	java/lang/StringBuilder
    //   269: dup
    //   270: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   273: astore 6
    //   275: aload 6
    //   277: ldc_w 289
    //   280: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   283: pop
    //   284: aload 6
    //   286: aload_1
    //   287: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   290: pop
    //   291: aload 6
    //   293: ldc_w 291
    //   296: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   299: pop
    //   300: aload 6
    //   302: iload 4
    //   304: invokevirtual 294	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   307: pop
    //   308: aload 7
    //   310: aload 6
    //   312: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   315: invokestatic 186	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   318: pop
    //   319: iload 4
    //   321: ireturn
    //   322: aload 6
    //   324: invokevirtual 275	android/os/HwParcel:release	()V
    //   327: aload_1
    //   328: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	329	0	this	MipayManagerImpl
    //   0	329	1	paramString	String
    //   1	124	2	bool1	boolean
    //   3	247	3	bool2	boolean
    //   5	315	4	bool3	boolean
    //   18	23	5	i	int
    //   23	300	6	localObject1	Object
    //   28	281	7	localObject2	Object
    //   204	29	8	localRemoteException	RemoteException
    //   209	30	9	str	String
    // Exception table:
    //   from	to	target	type
    //   120	124	200	finally
    //   127	192	200	finally
    //   206	249	200	finally
    //   120	124	204	android/os/RemoteException
    //   127	192	204	android/os/RemoteException
  }
  
  /* Error */
  public int generateKeyPair(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_3
    //   2: getstatic 202	android/os/Build$VERSION:SDK_INT	I
    //   5: bipush 28
    //   7: if_icmplt +109 -> 116
    //   10: invokestatic 222	android/os/Binder:getCallingUid	()I
    //   13: istore 4
    //   15: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   18: astore 5
    //   20: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   23: astore 6
    //   25: aload 5
    //   27: getstatic 90	org/mipay/android/manager/MipayManagerImpl:mTidaInterfaceDesc	Ljava/lang/String;
    //   30: invokevirtual 231	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   33: aload 5
    //   35: iload 4
    //   37: invokevirtual 235	android/os/Parcel:writeInt	(I)V
    //   40: aload 5
    //   42: aload_1
    //   43: invokevirtual 238	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   46: aload 5
    //   48: aload_2
    //   49: invokevirtual 238	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   52: getstatic 62	org/mipay/android/manager/MipayManagerImpl:CODE_GEN_KEY_PAIR	I
    //   55: aload 5
    //   57: aload 6
    //   59: invokestatic 240	org/mipay/android/manager/MipayManagerImpl:connectService	(ILandroid/os/Parcel;Landroid/os/Parcel;)I
    //   62: istore 4
    //   64: iload 4
    //   66: ifeq +25 -> 91
    //   69: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   72: ldc -14
    //   74: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   77: pop
    //   78: aload 5
    //   80: invokevirtual 245	android/os/Parcel:recycle	()V
    //   83: aload 6
    //   85: invokevirtual 245	android/os/Parcel:recycle	()V
    //   88: goto +25 -> 113
    //   91: aload 6
    //   93: invokevirtual 248	android/os/Parcel:readException	()V
    //   96: aload 6
    //   98: invokevirtual 251	android/os/Parcel:readInt	()I
    //   101: istore 4
    //   103: aload 5
    //   105: invokevirtual 245	android/os/Parcel:recycle	()V
    //   108: aload 6
    //   110: invokevirtual 245	android/os/Parcel:recycle	()V
    //   113: goto +153 -> 266
    //   116: new 253	android/os/HwParcel
    //   119: dup
    //   120: invokespecial 254	android/os/HwParcel:<init>	()V
    //   123: astore 6
    //   125: aload_0
    //   126: invokespecial 256	org/mipay/android/manager/MipayManagerImpl:initService	()V
    //   129: iload_3
    //   130: istore 4
    //   132: aload_0
    //   133: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   136: ifnull +67 -> 203
    //   139: new 253	android/os/HwParcel
    //   142: astore 5
    //   144: aload 5
    //   146: invokespecial 254	android/os/HwParcel:<init>	()V
    //   149: aload 5
    //   151: getstatic 78	org/mipay/android/manager/MipayManagerImpl:INTERFACE_DESCRIPTOR	Ljava/lang/String;
    //   154: invokevirtual 257	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   157: aload 5
    //   159: aload_1
    //   160: invokevirtual 258	android/os/HwParcel:writeString	(Ljava/lang/String;)V
    //   163: aload 5
    //   165: aload_2
    //   166: invokevirtual 258	android/os/HwParcel:writeString	(Ljava/lang/String;)V
    //   169: aload_0
    //   170: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   173: getstatic 62	org/mipay/android/manager/MipayManagerImpl:CODE_GEN_KEY_PAIR	I
    //   176: aload 5
    //   178: aload 6
    //   180: iconst_0
    //   181: invokeinterface 263 5 0
    //   186: aload 6
    //   188: invokevirtual 266	android/os/HwParcel:verifySuccess	()V
    //   191: aload 5
    //   193: invokevirtual 269	android/os/HwParcel:releaseTemporaryStorage	()V
    //   196: aload 6
    //   198: invokevirtual 272	android/os/HwParcel:readInt32	()I
    //   201: istore 4
    //   203: aload 6
    //   205: invokevirtual 275	android/os/HwParcel:release	()V
    //   208: goto +58 -> 266
    //   211: astore_1
    //   212: goto +137 -> 349
    //   215: astore 7
    //   217: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   220: astore 5
    //   222: new 157	java/lang/StringBuilder
    //   225: astore 8
    //   227: aload 8
    //   229: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   232: aload 8
    //   234: ldc_w 277
    //   237: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   240: pop
    //   241: aload 8
    //   243: aload 7
    //   245: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   248: pop
    //   249: aload 5
    //   251: aload 8
    //   253: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   256: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   259: pop
    //   260: iload_3
    //   261: istore 4
    //   263: goto -60 -> 203
    //   266: getstatic 52	org/mipay/android/manager/MipayManagerImpl:DEBUG	Z
    //   269: ifeq +77 -> 346
    //   272: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   275: astore 5
    //   277: new 157	java/lang/StringBuilder
    //   280: dup
    //   281: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   284: astore 6
    //   286: aload 6
    //   288: ldc_w 297
    //   291: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   294: pop
    //   295: aload 6
    //   297: aload_1
    //   298: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   301: pop
    //   302: aload 6
    //   304: ldc_w 299
    //   307: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   310: pop
    //   311: aload 6
    //   313: aload_2
    //   314: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   317: pop
    //   318: aload 6
    //   320: ldc_w 291
    //   323: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: pop
    //   327: aload 6
    //   329: iload 4
    //   331: invokevirtual 181	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   334: pop
    //   335: aload 5
    //   337: aload 6
    //   339: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   342: invokestatic 186	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   345: pop
    //   346: iload 4
    //   348: ireturn
    //   349: aload 6
    //   351: invokevirtual 275	android/os/HwParcel:release	()V
    //   354: aload_1
    //   355: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	356	0	this	MipayManagerImpl
    //   0	356	1	paramString1	String
    //   0	356	2	paramString2	String
    //   1	260	3	i	int
    //   13	334	4	j	int
    //   18	318	5	localObject1	Object
    //   23	327	6	localObject2	Object
    //   215	29	7	localRemoteException	RemoteException
    //   225	27	8	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   125	129	211	finally
    //   132	203	211	finally
    //   217	260	211	finally
    //   125	129	215	android/os/RemoteException
    //   132	203	215	android/os/RemoteException
  }
  
  /* Error */
  public String getFpIds()
  {
    // Byte code:
    //   0: ldc_w 302
    //   3: astore_1
    //   4: getstatic 202	android/os/Build$VERSION:SDK_INT	I
    //   7: bipush 28
    //   9: if_icmplt +69 -> 78
    //   12: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   15: astore_2
    //   16: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   19: astore_3
    //   20: aload_2
    //   21: getstatic 90	org/mipay/android/manager/MipayManagerImpl:mTidaInterfaceDesc	Ljava/lang/String;
    //   24: invokevirtual 231	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   27: getstatic 72	org/mipay/android/manager/MipayManagerImpl:CODE_GET_FP_IDS	I
    //   30: aload_2
    //   31: aload_3
    //   32: invokestatic 240	org/mipay/android/manager/MipayManagerImpl:connectService	(ILandroid/os/Parcel;Landroid/os/Parcel;)I
    //   35: ifeq +23 -> 58
    //   38: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   41: ldc -14
    //   43: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   46: pop
    //   47: aload_2
    //   48: invokevirtual 245	android/os/Parcel:recycle	()V
    //   51: aload_3
    //   52: invokevirtual 245	android/os/Parcel:recycle	()V
    //   55: goto +20 -> 75
    //   58: aload_3
    //   59: invokevirtual 248	android/os/Parcel:readException	()V
    //   62: aload_3
    //   63: invokevirtual 305	android/os/Parcel:readString	()Ljava/lang/String;
    //   66: astore_1
    //   67: aload_2
    //   68: invokevirtual 245	android/os/Parcel:recycle	()V
    //   71: aload_3
    //   72: invokevirtual 245	android/os/Parcel:recycle	()V
    //   75: goto +126 -> 201
    //   78: new 253	android/os/HwParcel
    //   81: dup
    //   82: invokespecial 254	android/os/HwParcel:<init>	()V
    //   85: astore_3
    //   86: aload_0
    //   87: invokespecial 256	org/mipay/android/manager/MipayManagerImpl:initService	()V
    //   90: aload_1
    //   91: astore_2
    //   92: aload_0
    //   93: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   96: ifnull +46 -> 142
    //   99: new 253	android/os/HwParcel
    //   102: astore_2
    //   103: aload_2
    //   104: invokespecial 254	android/os/HwParcel:<init>	()V
    //   107: aload_2
    //   108: getstatic 78	org/mipay/android/manager/MipayManagerImpl:INTERFACE_DESCRIPTOR	Ljava/lang/String;
    //   111: invokevirtual 257	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   114: aload_0
    //   115: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   118: getstatic 72	org/mipay/android/manager/MipayManagerImpl:CODE_GET_FP_IDS	I
    //   121: aload_2
    //   122: aload_3
    //   123: iconst_0
    //   124: invokeinterface 263 5 0
    //   129: aload_3
    //   130: invokevirtual 266	android/os/HwParcel:verifySuccess	()V
    //   133: aload_2
    //   134: invokevirtual 269	android/os/HwParcel:releaseTemporaryStorage	()V
    //   137: aload_3
    //   138: invokevirtual 306	android/os/HwParcel:readString	()Ljava/lang/String;
    //   141: astore_2
    //   142: aload_2
    //   143: astore_1
    //   144: aload_3
    //   145: invokevirtual 275	android/os/HwParcel:release	()V
    //   148: goto +53 -> 201
    //   151: astore_1
    //   152: goto +92 -> 244
    //   155: astore_2
    //   156: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   159: astore 4
    //   161: new 157	java/lang/StringBuilder
    //   164: astore 5
    //   166: aload 5
    //   168: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   171: aload 5
    //   173: ldc_w 277
    //   176: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: pop
    //   180: aload 5
    //   182: aload_2
    //   183: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   186: pop
    //   187: aload 4
    //   189: aload 5
    //   191: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   194: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   197: pop
    //   198: goto -54 -> 144
    //   201: getstatic 52	org/mipay/android/manager/MipayManagerImpl:DEBUG	Z
    //   204: ifeq +38 -> 242
    //   207: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   210: astore_3
    //   211: new 157	java/lang/StringBuilder
    //   214: dup
    //   215: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   218: astore_2
    //   219: aload_2
    //   220: ldc_w 308
    //   223: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   226: pop
    //   227: aload_2
    //   228: aload_1
    //   229: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   232: pop
    //   233: aload_3
    //   234: aload_2
    //   235: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   238: invokestatic 186	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   241: pop
    //   242: aload_1
    //   243: areturn
    //   244: aload_3
    //   245: invokevirtual 275	android/os/HwParcel:release	()V
    //   248: aload_1
    //   249: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	250	0	this	MipayManagerImpl
    //   3	141	1	localObject1	Object
    //   151	98	1	str1	String
    //   15	128	2	localObject2	Object
    //   155	28	2	localRemoteException	RemoteException
    //   218	17	2	localStringBuilder1	StringBuilder
    //   19	226	3	localObject3	Object
    //   159	29	4	str2	String
    //   164	26	5	localStringBuilder2	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   86	90	151	finally
    //   92	142	151	finally
    //   156	198	151	finally
    //   86	90	155	android/os/RemoteException
    //   92	142	155	android/os/RemoteException
  }
  
  public int getSupportBIOTypes(Context paramContext)
  {
    if (DEBUG)
    {
      String str = TAG;
      paramContext = new StringBuilder();
      paramContext.append("getSupportBIOTypes :");
      paramContext.append(MIPAY_TYPE_FINGER);
      Slog.i(str, paramContext.toString());
    }
    return MIPAY_TYPE_FINGER;
  }
  
  public int getVersion()
  {
    if (DEBUG)
    {
      String str = TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("getVersion :");
      localStringBuilder.append(MIPAY_VERISON_1);
      Slog.i(str, localStringBuilder.toString());
    }
    return MIPAY_VERISON_1;
  }
  
  /* Error */
  public int removeAllKey()
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_1
    //   2: getstatic 202	android/os/Build$VERSION:SDK_INT	I
    //   5: bipush 28
    //   7: if_icmplt +86 -> 93
    //   10: invokestatic 222	android/os/Binder:getCallingUid	()I
    //   13: istore_2
    //   14: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   17: astore_3
    //   18: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   21: astore 4
    //   23: aload_3
    //   24: getstatic 90	org/mipay/android/manager/MipayManagerImpl:mTidaInterfaceDesc	Ljava/lang/String;
    //   27: invokevirtual 231	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   30: aload_3
    //   31: iload_2
    //   32: invokevirtual 235	android/os/Parcel:writeInt	(I)V
    //   35: getstatic 70	org/mipay/android/manager/MipayManagerImpl:CODE_RM_ALL_KEY	I
    //   38: aload_3
    //   39: aload 4
    //   41: invokestatic 240	org/mipay/android/manager/MipayManagerImpl:connectService	(ILandroid/os/Parcel;Landroid/os/Parcel;)I
    //   44: istore_2
    //   45: iload_2
    //   46: ifeq +24 -> 70
    //   49: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   52: ldc -14
    //   54: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   57: pop
    //   58: aload_3
    //   59: invokevirtual 245	android/os/Parcel:recycle	()V
    //   62: aload 4
    //   64: invokevirtual 245	android/os/Parcel:recycle	()V
    //   67: goto +23 -> 90
    //   70: aload 4
    //   72: invokevirtual 248	android/os/Parcel:readException	()V
    //   75: aload 4
    //   77: invokevirtual 251	android/os/Parcel:readInt	()I
    //   80: istore_2
    //   81: aload_3
    //   82: invokevirtual 245	android/os/Parcel:recycle	()V
    //   85: aload 4
    //   87: invokevirtual 245	android/os/Parcel:recycle	()V
    //   90: goto +128 -> 218
    //   93: new 253	android/os/HwParcel
    //   96: dup
    //   97: invokespecial 254	android/os/HwParcel:<init>	()V
    //   100: astore 4
    //   102: aload_0
    //   103: invokespecial 256	org/mipay/android/manager/MipayManagerImpl:initService	()V
    //   106: iload_1
    //   107: istore_2
    //   108: aload_0
    //   109: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   112: ifnull +49 -> 161
    //   115: new 253	android/os/HwParcel
    //   118: astore_3
    //   119: aload_3
    //   120: invokespecial 254	android/os/HwParcel:<init>	()V
    //   123: aload_3
    //   124: getstatic 78	org/mipay/android/manager/MipayManagerImpl:INTERFACE_DESCRIPTOR	Ljava/lang/String;
    //   127: invokevirtual 257	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   130: aload_0
    //   131: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   134: getstatic 70	org/mipay/android/manager/MipayManagerImpl:CODE_RM_ALL_KEY	I
    //   137: aload_3
    //   138: aload 4
    //   140: iconst_0
    //   141: invokeinterface 263 5 0
    //   146: aload 4
    //   148: invokevirtual 266	android/os/HwParcel:verifySuccess	()V
    //   151: aload_3
    //   152: invokevirtual 269	android/os/HwParcel:releaseTemporaryStorage	()V
    //   155: aload 4
    //   157: invokevirtual 272	android/os/HwParcel:readInt32	()I
    //   160: istore_2
    //   161: aload 4
    //   163: invokevirtual 275	android/os/HwParcel:release	()V
    //   166: goto +52 -> 218
    //   169: astore_3
    //   170: goto +95 -> 265
    //   173: astore 5
    //   175: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   178: astore 6
    //   180: new 157	java/lang/StringBuilder
    //   183: astore_3
    //   184: aload_3
    //   185: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   188: aload_3
    //   189: ldc_w 277
    //   192: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   195: pop
    //   196: aload_3
    //   197: aload 5
    //   199: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   202: pop
    //   203: aload 6
    //   205: aload_3
    //   206: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   209: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   212: pop
    //   213: iload_1
    //   214: istore_2
    //   215: goto -54 -> 161
    //   218: getstatic 52	org/mipay/android/manager/MipayManagerImpl:DEBUG	Z
    //   221: ifeq +42 -> 263
    //   224: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   227: astore_3
    //   228: new 157	java/lang/StringBuilder
    //   231: dup
    //   232: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   235: astore 4
    //   237: aload 4
    //   239: ldc_w 318
    //   242: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: pop
    //   246: aload 4
    //   248: iload_2
    //   249: invokevirtual 181	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   252: pop
    //   253: aload_3
    //   254: aload 4
    //   256: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   259: invokestatic 186	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   262: pop
    //   263: iload_2
    //   264: ireturn
    //   265: aload 4
    //   267: invokevirtual 275	android/os/HwParcel:release	()V
    //   270: aload_3
    //   271: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	272	0	this	MipayManagerImpl
    //   1	213	1	i	int
    //   13	251	2	j	int
    //   17	135	3	localObject1	Object
    //   169	1	3	localObject2	Object
    //   183	88	3	localObject3	Object
    //   21	245	4	localObject4	Object
    //   173	25	5	localRemoteException	RemoteException
    //   178	26	6	str	String
    // Exception table:
    //   from	to	target	type
    //   102	106	169	finally
    //   108	161	169	finally
    //   175	213	169	finally
    //   102	106	173	android/os/RemoteException
    //   108	161	173	android/os/RemoteException
  }
  
  /* Error */
  public byte[] sign()
  {
    // Byte code:
    //   0: getstatic 202	android/os/Build$VERSION:SDK_INT	I
    //   3: bipush 28
    //   5: if_icmplt +72 -> 77
    //   8: iconst_0
    //   9: newarray <illegal type>
    //   11: astore_1
    //   12: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   15: astore_2
    //   16: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   19: astore_3
    //   20: aload_2
    //   21: getstatic 90	org/mipay/android/manager/MipayManagerImpl:mTidaInterfaceDesc	Ljava/lang/String;
    //   24: invokevirtual 231	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   27: getstatic 68	org/mipay/android/manager/MipayManagerImpl:CODE_SIGN	I
    //   30: aload_2
    //   31: aload_3
    //   32: invokestatic 240	org/mipay/android/manager/MipayManagerImpl:connectService	(ILandroid/os/Parcel;Landroid/os/Parcel;)I
    //   35: ifeq +23 -> 58
    //   38: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   41: ldc -14
    //   43: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   46: pop
    //   47: aload_2
    //   48: invokevirtual 245	android/os/Parcel:recycle	()V
    //   51: aload_3
    //   52: invokevirtual 245	android/os/Parcel:recycle	()V
    //   55: goto +20 -> 75
    //   58: aload_3
    //   59: invokevirtual 248	android/os/Parcel:readException	()V
    //   62: aload_3
    //   63: invokevirtual 323	android/os/Parcel:createByteArray	()[B
    //   66: astore_1
    //   67: aload_2
    //   68: invokevirtual 245	android/os/Parcel:recycle	()V
    //   71: aload_3
    //   72: invokevirtual 245	android/os/Parcel:recycle	()V
    //   75: aload_1
    //   76: areturn
    //   77: new 253	android/os/HwParcel
    //   80: dup
    //   81: invokespecial 254	android/os/HwParcel:<init>	()V
    //   84: astore_1
    //   85: aload_0
    //   86: invokespecial 256	org/mipay/android/manager/MipayManagerImpl:initService	()V
    //   89: aload_0
    //   90: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   93: ifnull +95 -> 188
    //   96: new 253	android/os/HwParcel
    //   99: astore_3
    //   100: aload_3
    //   101: invokespecial 254	android/os/HwParcel:<init>	()V
    //   104: aload_3
    //   105: getstatic 78	org/mipay/android/manager/MipayManagerImpl:INTERFACE_DESCRIPTOR	Ljava/lang/String;
    //   108: invokevirtual 257	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   111: aload_0
    //   112: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   115: getstatic 68	org/mipay/android/manager/MipayManagerImpl:CODE_SIGN	I
    //   118: aload_3
    //   119: aload_1
    //   120: iconst_0
    //   121: invokeinterface 263 5 0
    //   126: aload_1
    //   127: invokevirtual 266	android/os/HwParcel:verifySuccess	()V
    //   130: aload_3
    //   131: invokevirtual 269	android/os/HwParcel:releaseTemporaryStorage	()V
    //   134: aload_1
    //   135: invokevirtual 327	android/os/HwParcel:readInt8Vector	()Ljava/util/ArrayList;
    //   138: astore_3
    //   139: aload_3
    //   140: invokevirtual 332	java/util/ArrayList:size	()I
    //   143: istore 4
    //   145: iload 4
    //   147: newarray <illegal type>
    //   149: astore_2
    //   150: iconst_0
    //   151: istore 5
    //   153: iload 5
    //   155: iload 4
    //   157: if_icmpge +25 -> 182
    //   160: aload_2
    //   161: iload 5
    //   163: aload_3
    //   164: iload 5
    //   166: invokevirtual 336	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   169: checkcast 338	java/lang/Byte
    //   172: invokevirtual 342	java/lang/Byte:byteValue	()B
    //   175: bastore
    //   176: iinc 5 1
    //   179: goto -26 -> 153
    //   182: aload_1
    //   183: invokevirtual 275	android/os/HwParcel:release	()V
    //   186: aload_2
    //   187: areturn
    //   188: aload_1
    //   189: invokevirtual 275	android/os/HwParcel:release	()V
    //   192: goto +48 -> 240
    //   195: astore_3
    //   196: goto +56 -> 252
    //   199: astore_3
    //   200: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   203: astore 6
    //   205: new 157	java/lang/StringBuilder
    //   208: astore_2
    //   209: aload_2
    //   210: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   213: aload_2
    //   214: ldc_w 277
    //   217: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: pop
    //   221: aload_2
    //   222: aload_3
    //   223: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   226: pop
    //   227: aload 6
    //   229: aload_2
    //   230: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   233: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   236: pop
    //   237: goto -49 -> 188
    //   240: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   243: ldc_w 344
    //   246: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   249: pop
    //   250: aconst_null
    //   251: areturn
    //   252: aload_1
    //   253: invokevirtual 275	android/os/HwParcel:release	()V
    //   256: aload_3
    //   257: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	258	0	this	MipayManagerImpl
    //   11	242	1	localObject1	Object
    //   15	215	2	localObject2	Object
    //   19	145	3	localObject3	Object
    //   195	1	3	localObject4	Object
    //   199	58	3	localRemoteException	RemoteException
    //   143	15	4	i	int
    //   151	26	5	j	int
    //   203	25	6	str	String
    // Exception table:
    //   from	to	target	type
    //   85	150	195	finally
    //   160	176	195	finally
    //   200	237	195	finally
    //   85	150	199	android/os/RemoteException
    //   160	176	199	android/os/RemoteException
  }
  
  /* Error */
  public int signInit(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore_3
    //   2: getstatic 202	android/os/Build$VERSION:SDK_INT	I
    //   5: bipush 28
    //   7: if_icmplt +109 -> 116
    //   10: invokestatic 222	android/os/Binder:getCallingUid	()I
    //   13: istore 4
    //   15: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   18: astore 5
    //   20: invokestatic 228	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   23: astore 6
    //   25: aload 5
    //   27: getstatic 90	org/mipay/android/manager/MipayManagerImpl:mTidaInterfaceDesc	Ljava/lang/String;
    //   30: invokevirtual 231	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   33: aload 5
    //   35: iload 4
    //   37: invokevirtual 235	android/os/Parcel:writeInt	(I)V
    //   40: aload 5
    //   42: aload_1
    //   43: invokevirtual 238	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   46: aload 5
    //   48: aload_2
    //   49: invokevirtual 238	android/os/Parcel:writeString	(Ljava/lang/String;)V
    //   52: getstatic 64	org/mipay/android/manager/MipayManagerImpl:CODE_SIGN_INIT	I
    //   55: aload 5
    //   57: aload 6
    //   59: invokestatic 240	org/mipay/android/manager/MipayManagerImpl:connectService	(ILandroid/os/Parcel;Landroid/os/Parcel;)I
    //   62: istore 4
    //   64: iload 4
    //   66: ifeq +25 -> 91
    //   69: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   72: ldc -14
    //   74: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   77: pop
    //   78: aload 5
    //   80: invokevirtual 245	android/os/Parcel:recycle	()V
    //   83: aload 6
    //   85: invokevirtual 245	android/os/Parcel:recycle	()V
    //   88: goto +25 -> 113
    //   91: aload 6
    //   93: invokevirtual 248	android/os/Parcel:readException	()V
    //   96: aload 6
    //   98: invokevirtual 251	android/os/Parcel:readInt	()I
    //   101: istore 4
    //   103: aload 5
    //   105: invokevirtual 245	android/os/Parcel:recycle	()V
    //   108: aload 6
    //   110: invokevirtual 245	android/os/Parcel:recycle	()V
    //   113: goto +153 -> 266
    //   116: new 253	android/os/HwParcel
    //   119: dup
    //   120: invokespecial 254	android/os/HwParcel:<init>	()V
    //   123: astore 6
    //   125: aload_0
    //   126: invokespecial 256	org/mipay/android/manager/MipayManagerImpl:initService	()V
    //   129: iload_3
    //   130: istore 4
    //   132: aload_0
    //   133: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   136: ifnull +67 -> 203
    //   139: new 253	android/os/HwParcel
    //   142: astore 5
    //   144: aload 5
    //   146: invokespecial 254	android/os/HwParcel:<init>	()V
    //   149: aload 5
    //   151: getstatic 78	org/mipay/android/manager/MipayManagerImpl:INTERFACE_DESCRIPTOR	Ljava/lang/String;
    //   154: invokevirtual 257	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   157: aload 5
    //   159: aload_1
    //   160: invokevirtual 258	android/os/HwParcel:writeString	(Ljava/lang/String;)V
    //   163: aload 5
    //   165: aload_2
    //   166: invokevirtual 258	android/os/HwParcel:writeString	(Ljava/lang/String;)V
    //   169: aload_0
    //   170: getfield 205	org/mipay/android/manager/MipayManagerImpl:mHwService	Landroid/os/IHwBinder;
    //   173: getstatic 64	org/mipay/android/manager/MipayManagerImpl:CODE_SIGN_INIT	I
    //   176: aload 5
    //   178: aload 6
    //   180: iconst_0
    //   181: invokeinterface 263 5 0
    //   186: aload 6
    //   188: invokevirtual 266	android/os/HwParcel:verifySuccess	()V
    //   191: aload 5
    //   193: invokevirtual 269	android/os/HwParcel:releaseTemporaryStorage	()V
    //   196: aload 6
    //   198: invokevirtual 272	android/os/HwParcel:readInt32	()I
    //   201: istore 4
    //   203: aload 6
    //   205: invokevirtual 275	android/os/HwParcel:release	()V
    //   208: goto +58 -> 266
    //   211: astore_1
    //   212: goto +137 -> 349
    //   215: astore 5
    //   217: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   220: astore 7
    //   222: new 157	java/lang/StringBuilder
    //   225: astore 8
    //   227: aload 8
    //   229: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   232: aload 8
    //   234: ldc_w 277
    //   237: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   240: pop
    //   241: aload 8
    //   243: aload 5
    //   245: invokevirtual 167	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   248: pop
    //   249: aload 7
    //   251: aload 8
    //   253: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   256: invokestatic 176	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   259: pop
    //   260: iload_3
    //   261: istore 4
    //   263: goto -60 -> 203
    //   266: getstatic 52	org/mipay/android/manager/MipayManagerImpl:DEBUG	Z
    //   269: ifeq +77 -> 346
    //   272: getstatic 50	org/mipay/android/manager/MipayManagerImpl:TAG	Ljava/lang/String;
    //   275: astore 6
    //   277: new 157	java/lang/StringBuilder
    //   280: dup
    //   281: invokespecial 158	java/lang/StringBuilder:<init>	()V
    //   284: astore 5
    //   286: aload 5
    //   288: ldc_w 347
    //   291: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   294: pop
    //   295: aload 5
    //   297: aload_1
    //   298: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   301: pop
    //   302: aload 5
    //   304: ldc_w 299
    //   307: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   310: pop
    //   311: aload 5
    //   313: aload_2
    //   314: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   317: pop
    //   318: aload 5
    //   320: ldc_w 291
    //   323: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: pop
    //   327: aload 5
    //   329: iload 4
    //   331: invokevirtual 181	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   334: pop
    //   335: aload 6
    //   337: aload 5
    //   339: invokevirtual 170	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   342: invokestatic 186	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   345: pop
    //   346: iload 4
    //   348: ireturn
    //   349: aload 6
    //   351: invokevirtual 275	android/os/HwParcel:release	()V
    //   354: aload_1
    //   355: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	356	0	this	MipayManagerImpl
    //   0	356	1	paramString1	String
    //   0	356	2	paramString2	String
    //   1	260	3	i	int
    //   13	334	4	j	int
    //   18	174	5	localObject1	Object
    //   215	29	5	localRemoteException	RemoteException
    //   284	54	5	localStringBuilder1	StringBuilder
    //   23	327	6	localObject2	Object
    //   220	30	7	str	String
    //   225	27	8	localStringBuilder2	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   125	129	211	finally
    //   132	203	211	finally
    //   217	260	211	finally
    //   125	129	215	android/os/RemoteException
    //   132	203	215	android/os/RemoteException
  }
  
  public int signUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return signUpdate(new String(paramArrayOfByte, paramInt1, paramInt2));
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/mipay/android/manager/MipayManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */