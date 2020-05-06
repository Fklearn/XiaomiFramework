package org.ifaa.android.manager.face;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.qualcomm.qti.seccamapi.SecCamServiceClient;
import com.android.qualcomm.qti.seccamapi.SecCamServiceClient.ClientCallback;
import com.android.qualcomm.qti.seccamapi.SecureSurface.FrameCallback;
import com.android.qualcomm.qti.seccamapi.SecureSurface.FrameInfo;
import java.util.concurrent.Semaphore;
import miui.util.FeatureParser;
import org.ifaa.android.manager.IIFAAService;
import org.ifaa.android.manager.IIFAAService.Stub;

public class IFAAFaceManagerImpl
  extends IFAAFaceManagerV2
  implements SecCamServiceClient.ClientCallback, SecureSurface.FrameCallback
{
  private static final boolean DEBUG = false;
  public static final Integer DESTINATION_MLVM;
  public static final Integer DESTINATION_QTEE;
  private static volatile IFAAFaceManagerImpl INSTANCE;
  private static final boolean IS_SUPPORT_2DFA = ;
  private static final Integer PREVIEW_CAMERAID;
  private static final Integer PREVIEW_FORMAT;
  private static final Integer PREVIEW_HEIGHT;
  private static final Integer PREVIEW_NUMOFBUFFERS;
  private static final Integer PREVIEW_SURFACE_NUMOFBUFFERS;
  private static final Integer PREVIEW_WIDTH;
  private static final Integer SECCAM_STOP_TIMEOUT;
  private static final String TAG = "IFAAFaceManagerImplV2";
  private static final int TZ_APP_BUFFER_SIZE = 8192;
  private static final String TZ_APP_NAME = "seccam2d";
  public static final int UPGRADE_ACTION_RESET = 0;
  public static final int UPGRADE_ACTION_UPDATE = 2;
  public static final int UPGRADE_ACTION_WRITE = 1;
  private static final String mClassName = "org.ifaa.android.manager.IFAAService";
  private static ServiceConnection mConn = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      try
      {
        paramAnonymousIBinder.linkToDeath(IFAAFaceManagerImpl.mDeathRecipient, 0);
      }
      catch (RemoteException paramAnonymousComponentName)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("linkToDeath fail: ");
        localStringBuilder.append(paramAnonymousComponentName);
        Slog.e("IFAAFaceManagerImplV2", localStringBuilder.toString());
      }
      IFAAFaceManagerImpl.access$002(IIFAAService.Stub.asInterface(paramAnonymousIBinder));
      if (!IFAAFaceManagerImpl.access$200()) {
        try
        {
          IFAAFaceManagerImpl.mService.faceGetCellinfo();
        }
        catch (RemoteException paramAnonymousIBinder)
        {
          paramAnonymousComponentName = new StringBuilder();
          paramAnonymousComponentName.append("call ci info fail: ");
          paramAnonymousComponentName.append(paramAnonymousIBinder);
          Slog.e("IFAAFaceManagerImplV2", paramAnonymousComponentName.toString());
        }
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      if (IFAAFaceManagerImpl.mContext != null)
      {
        Slog.i("IFAAFaceManagerImplV2", "re-bind to IFAA service");
        IFAAFaceManagerImpl.mContext.getApplicationContext().unbindService(IFAAFaceManagerImpl.mConn);
        IFAAFaceManagerImpl.access$500();
      }
    }
  };
  private static Context mContext;
  private static IBinder.DeathRecipient mDeathRecipient;
  private static final String mPackageName = "com.tencent.soter.soterserver";
  private static IIFAAService mService;
  protected int frameCallbackReturnParamsSize_ = 16;
  protected byte[] frameCallbackReturnParams_ = new byte[this.frameCallbackReturnParamsSize_];
  private Semaphore mCameraOperationLock = new Semaphore(1);
  private final ConditionVariable mSeccamStopping = new ConditionVariable(true);
  private IFAASecureCamera2API previewCamera_ = null;
  
  static
  {
    INSTANCE = null;
    Integer localInteger = Integer.valueOf(1);
    DESTINATION_QTEE = localInteger;
    DESTINATION_MLVM = Integer.valueOf(2);
    SECCAM_STOP_TIMEOUT = Integer.valueOf(600);
    PREVIEW_CAMERAID = localInteger;
    PREVIEW_WIDTH = Integer.valueOf(640);
    PREVIEW_HEIGHT = Integer.valueOf(480);
    PREVIEW_FORMAT = Integer.valueOf(35);
    PREVIEW_NUMOFBUFFERS = Integer.valueOf(4);
    PREVIEW_SURFACE_NUMOFBUFFERS = Integer.valueOf(3);
    mContext = null;
    mService = null;
    mDeathRecipient = new IBinder.DeathRecipient()
    {
      public void binderDied()
      {
        if (IFAAFaceManagerImpl.mService == null) {
          return;
        }
        Slog.d("IFAAFaceManagerImplV2", "binderDied, unlink service");
        IFAAFaceManagerImpl.mService.asBinder().unlinkToDeath(IFAAFaceManagerImpl.mDeathRecipient, 0);
      }
    };
  }
  
  private static boolean IsMediatek()
  {
    return "mediatek".equals(FeatureParser.getString("vendor"));
  }
  
  private int byte4ToInt(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte[paramInt];
    int j = paramArrayOfByte[(paramInt + 1)];
    int k = paramArrayOfByte[(paramInt + 2)];
    return (paramArrayOfByte[(paramInt + 3)] & 0xFF) << 24 | (k & 0xFF) << 16 | (j & 0xFF) << 8 | i & 0xFF;
  }
  
  private boolean closeCamera()
  {
    if (IsMediatek()) {
      return true;
    }
    this.mSeccamStopping.close();
    new SeccamStopTask(null).execute(new String[0]);
    return true;
  }
  
  public static IFAAFaceManagerV2 getInstance(Context paramContext)
  {
    Context localContext = paramContext.getApplicationContext();
    Slog.d("IFAAFaceManagerImplV2", "getInstance+++");
    if (((INSTANCE == null) || (localContext != mContext)) && (IS_SUPPORT_2DFA)) {
      try
      {
        Slog.d("IFAAFaceManagerImplV2", "A new instance is required");
        if (mContext != null)
        {
          mContext.getApplicationContext().unbindService(mConn);
          if (!IsMediatek()) {
            SecCamServiceClient.getInstance().release();
          }
        }
        paramContext = new org/ifaa/android/manager/face/IFAAFaceManagerImpl;
        paramContext.<init>();
        INSTANCE = paramContext;
        mContext = localContext;
        initService();
        if (!IsMediatek()) {
          SecCamServiceClient.getInstance().start(mContext, INSTANCE, "seccam2d", 8192, DESTINATION_MLVM.intValue());
        }
      }
      finally {}
    }
    Slog.d("IFAAFaceManagerImplV2", "getInstance---");
    return INSTANCE;
  }
  
  private static void initService()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.tencent.soter.soterserver", "org.ifaa.android.manager.IFAAService");
    if (!mContext.getApplicationContext().bindService(localIntent, mConn, 1)) {
      Slog.e("IFAAFaceManagerImplV2", "cannot bind service: org.ifaa.android.manager.IFAAService");
    }
  }
  
  private void invokeCallback(IFAAFaceManager.AuthenticatorCallback paramAuthenticatorCallback, int paramInt)
  {
    if (paramInt == 1) {
      paramAuthenticatorCallback.onAuthenticationSucceeded();
    } else {
      paramAuthenticatorCallback.onAuthenticationFailed(paramInt);
    }
  }
  
  private static boolean isSupport2dfa()
  {
    boolean bool1 = false;
    int i;
    if (FeatureParser.getInteger("ifaa_2dfa_support", 0) == 1) {
      i = 1;
    } else {
      i = 0;
    }
    boolean bool2 = "enable".equals(SystemProperties.get("ro.boot.hypvm", ""));
    boolean bool3 = bool1;
    if (i != 0)
    {
      bool3 = bool1;
      if (bool2) {
        bool3 = true;
      }
    }
    return bool3;
  }
  
  /* Error */
  private boolean openCamera()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: invokestatic 163	org/ifaa/android/manager/face/IFAAFaceManagerImpl:IsMediatek	()Z
    //   5: ifeq +5 -> 10
    //   8: iconst_1
    //   9: ireturn
    //   10: ldc 35
    //   12: ldc_w 281
    //   15: invokestatic 284	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   18: pop
    //   19: aload_0
    //   20: getfield 128	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mSeccamStopping	Landroid/os/ConditionVariable;
    //   23: getstatic 91	org/ifaa/android/manager/face/IFAAFaceManagerImpl:SECCAM_STOP_TIMEOUT	Ljava/lang/Integer;
    //   26: invokevirtual 226	java/lang/Integer:intValue	()I
    //   29: i2l
    //   30: invokevirtual 288	android/os/ConditionVariable:block	(J)Z
    //   33: ifeq +15 -> 48
    //   36: ldc 35
    //   38: ldc_w 290
    //   41: invokestatic 284	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   44: pop
    //   45: goto +12 -> 57
    //   48: ldc 35
    //   50: ldc_w 292
    //   53: invokestatic 284	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   56: pop
    //   57: aload_0
    //   58: getfield 135	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mCameraOperationLock	Ljava/util/concurrent/Semaphore;
    //   61: invokevirtual 295	java/util/concurrent/Semaphore:acquire	()V
    //   64: aload_0
    //   65: getfield 137	org/ifaa/android/manager/face/IFAAFaceManagerImpl:previewCamera_	Lorg/ifaa/android/manager/face/IFAASecureCamera2API;
    //   68: astore_2
    //   69: aload_2
    //   70: ifnull +30 -> 100
    //   73: ldc 35
    //   75: ldc_w 297
    //   78: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   81: pop
    //   82: ldc 35
    //   84: ldc_w 299
    //   87: invokestatic 284	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   90: pop
    //   91: aload_0
    //   92: getfield 135	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mCameraOperationLock	Ljava/util/concurrent/Semaphore;
    //   95: invokevirtual 300	java/util/concurrent/Semaphore:release	()V
    //   98: iconst_1
    //   99: ireturn
    //   100: new 302	org/ifaa/android/manager/face/IFAASecureCamera2API
    //   103: astore_2
    //   104: aload_2
    //   105: getstatic 105	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mContext	Landroid/content/Context;
    //   108: aload_0
    //   109: invokespecial 305	org/ifaa/android/manager/face/IFAASecureCamera2API:<init>	(Landroid/content/Context;Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;)V
    //   112: aload_0
    //   113: aload_2
    //   114: putfield 137	org/ifaa/android/manager/face/IFAAFaceManagerImpl:previewCamera_	Lorg/ifaa/android/manager/face/IFAASecureCamera2API;
    //   117: aload_0
    //   118: getfield 137	org/ifaa/android/manager/face/IFAAFaceManagerImpl:previewCamera_	Lorg/ifaa/android/manager/face/IFAASecureCamera2API;
    //   121: getstatic 93	org/ifaa/android/manager/face/IFAAFaceManagerImpl:PREVIEW_CAMERAID	Ljava/lang/Integer;
    //   124: invokevirtual 226	java/lang/Integer:intValue	()I
    //   127: aconst_null
    //   128: getstatic 95	org/ifaa/android/manager/face/IFAAFaceManagerImpl:PREVIEW_WIDTH	Ljava/lang/Integer;
    //   131: getstatic 97	org/ifaa/android/manager/face/IFAAFaceManagerImpl:PREVIEW_HEIGHT	Ljava/lang/Integer;
    //   134: getstatic 99	org/ifaa/android/manager/face/IFAAFaceManagerImpl:PREVIEW_FORMAT	Ljava/lang/Integer;
    //   137: getstatic 101	org/ifaa/android/manager/face/IFAAFaceManagerImpl:PREVIEW_NUMOFBUFFERS	Ljava/lang/Integer;
    //   140: getstatic 103	org/ifaa/android/manager/face/IFAAFaceManagerImpl:PREVIEW_SURFACE_NUMOFBUFFERS	Ljava/lang/Integer;
    //   143: invokevirtual 308	org/ifaa/android/manager/face/IFAASecureCamera2API:start	(ILandroid/view/SurfaceView;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Boolean;
    //   146: invokevirtual 313	java/lang/Boolean:booleanValue	()Z
    //   149: ifeq +5 -> 154
    //   152: iconst_1
    //   153: istore_1
    //   154: ldc 35
    //   156: ldc_w 299
    //   159: invokestatic 284	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   162: pop
    //   163: aload_0
    //   164: getfield 135	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mCameraOperationLock	Ljava/util/concurrent/Semaphore;
    //   167: invokevirtual 300	java/util/concurrent/Semaphore:release	()V
    //   170: iload_1
    //   171: ireturn
    //   172: astore_2
    //   173: goto +18 -> 191
    //   176: astore_3
    //   177: new 315	java/lang/RuntimeException
    //   180: astore_2
    //   181: aload_2
    //   182: ldc_w 317
    //   185: aload_3
    //   186: invokespecial 320	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   189: aload_2
    //   190: athrow
    //   191: aload_0
    //   192: getfield 135	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mCameraOperationLock	Ljava/util/concurrent/Semaphore;
    //   195: invokevirtual 300	java/util/concurrent/Semaphore:release	()V
    //   198: aload_2
    //   199: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	200	0	this	IFAAFaceManagerImpl
    //   1	170	1	bool	boolean
    //   68	46	2	localIFAASecureCamera2API	IFAASecureCamera2API
    //   172	1	2	localObject	Object
    //   180	19	2	localRuntimeException	RuntimeException
    //   176	10	3	localInterruptedException	InterruptedException
    // Exception table:
    //   from	to	target	type
    //   57	69	172	finally
    //   73	91	172	finally
    //   100	117	172	finally
    //   117	152	172	finally
    //   154	163	172	finally
    //   177	191	172	finally
    //   57	69	176	java/lang/InterruptedException
    //   73	91	176	java/lang/InterruptedException
    //   100	117	176	java/lang/InterruptedException
    //   117	152	176	java/lang/InterruptedException
    //   154	163	176	java/lang/InterruptedException
  }
  
  public void authenticate(int paramInt1, int paramInt2, IFAAFaceManager.AuthenticatorCallback paramAuthenticatorCallback)
  {
    authenticate(String.valueOf(paramInt1), paramInt2, paramAuthenticatorCallback);
  }
  
  /* Error */
  public void authenticate(String paramString, int paramInt, IFAAFaceManager.AuthenticatorCallback paramAuthenticatorCallback)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore 4
    //   3: aload_0
    //   4: invokespecial 332	org/ifaa/android/manager/face/IFAAFaceManagerImpl:openCamera	()Z
    //   7: pop
    //   8: getstatic 107	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mService	Lorg/ifaa/android/manager/IIFAAService;
    //   11: aload_1
    //   12: iload_2
    //   13: invokeinterface 337 3 0
    //   18: istore_2
    //   19: aload_0
    //   20: aload_3
    //   21: iload_2
    //   22: invokespecial 339	org/ifaa/android/manager/face/IFAAFaceManagerImpl:invokeCallback	(Lorg/ifaa/android/manager/face/IFAAFaceManager$AuthenticatorCallback;I)V
    //   25: aload_0
    //   26: invokespecial 341	org/ifaa/android/manager/face/IFAAFaceManagerImpl:closeCamera	()Z
    //   29: pop
    //   30: goto +51 -> 81
    //   33: astore_1
    //   34: goto +48 -> 82
    //   37: astore_1
    //   38: new 343	java/lang/StringBuilder
    //   41: astore 5
    //   43: aload 5
    //   45: invokespecial 344	java/lang/StringBuilder:<init>	()V
    //   48: aload 5
    //   50: ldc_w 346
    //   53: invokevirtual 350	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: pop
    //   57: aload 5
    //   59: aload_1
    //   60: invokevirtual 353	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   63: pop
    //   64: ldc 35
    //   66: aload 5
    //   68: invokevirtual 357	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   71: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   74: pop
    //   75: iload 4
    //   77: istore_2
    //   78: goto -59 -> 19
    //   81: return
    //   82: aload_0
    //   83: aload_3
    //   84: iconst_m1
    //   85: invokespecial 339	org/ifaa/android/manager/face/IFAAFaceManagerImpl:invokeCallback	(Lorg/ifaa/android/manager/face/IFAAFaceManager$AuthenticatorCallback;I)V
    //   88: aload_0
    //   89: invokespecial 341	org/ifaa/android/manager/face/IFAAFaceManagerImpl:closeCamera	()Z
    //   92: pop
    //   93: aload_1
    //   94: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	95	0	this	IFAAFaceManagerImpl
    //   0	95	1	paramString	String
    //   0	95	2	paramInt	int
    //   0	95	3	paramAuthenticatorCallback	IFAAFaceManager.AuthenticatorCallback
    //   1	75	4	i	int
    //   41	26	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   3	19	33	finally
    //   38	75	33	finally
    //   3	19	37	android/os/RemoteException
  }
  
  public int cancel(int paramInt)
  {
    return cancel(String.valueOf(paramInt));
  }
  
  public int cancel(String paramString)
  {
    try
    {
      closeCamera();
      int i = mService.faceCancel_v2(paramString);
      return i;
    }
    catch (RemoteException paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("cancel_v2 fail: ");
      localStringBuilder.append(paramString);
      Slog.e("IFAAFaceManagerImplV2", localStringBuilder.toString());
    }
    return 0;
  }
  
  /* Error */
  public void enroll(String paramString, int paramInt, IFAAFaceManager.AuthenticatorCallback paramAuthenticatorCallback)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: istore 4
    //   3: aload_0
    //   4: invokespecial 332	org/ifaa/android/manager/face/IFAAFaceManagerImpl:openCamera	()Z
    //   7: pop
    //   8: getstatic 107	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mService	Lorg/ifaa/android/manager/IIFAAService;
    //   11: aload_1
    //   12: iload_2
    //   13: invokeinterface 371 3 0
    //   18: istore_2
    //   19: aload_0
    //   20: aload_3
    //   21: iload_2
    //   22: invokespecial 339	org/ifaa/android/manager/face/IFAAFaceManagerImpl:invokeCallback	(Lorg/ifaa/android/manager/face/IFAAFaceManager$AuthenticatorCallback;I)V
    //   25: aload_0
    //   26: invokespecial 341	org/ifaa/android/manager/face/IFAAFaceManagerImpl:closeCamera	()Z
    //   29: pop
    //   30: goto +51 -> 81
    //   33: astore_1
    //   34: goto +48 -> 82
    //   37: astore_1
    //   38: new 343	java/lang/StringBuilder
    //   41: astore 5
    //   43: aload 5
    //   45: invokespecial 344	java/lang/StringBuilder:<init>	()V
    //   48: aload 5
    //   50: ldc_w 373
    //   53: invokevirtual 350	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: pop
    //   57: aload 5
    //   59: aload_1
    //   60: invokevirtual 353	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   63: pop
    //   64: ldc 35
    //   66: aload 5
    //   68: invokevirtual 357	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   71: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   74: pop
    //   75: iload 4
    //   77: istore_2
    //   78: goto -59 -> 19
    //   81: return
    //   82: aload_0
    //   83: aload_3
    //   84: iconst_m1
    //   85: invokespecial 339	org/ifaa/android/manager/face/IFAAFaceManagerImpl:invokeCallback	(Lorg/ifaa/android/manager/face/IFAAFaceManager$AuthenticatorCallback;I)V
    //   88: aload_0
    //   89: invokespecial 341	org/ifaa/android/manager/face/IFAAFaceManagerImpl:closeCamera	()Z
    //   92: pop
    //   93: aload_1
    //   94: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	95	0	this	IFAAFaceManagerImpl
    //   0	95	1	paramString	String
    //   0	95	2	paramInt	int
    //   0	95	3	paramAuthenticatorCallback	IFAAFaceManager.AuthenticatorCallback
    //   1	75	4	i	int
    //   41	26	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   3	19	33	finally
    //   38	75	33	finally
    //   3	19	37	android/os/RemoteException
  }
  
  public byte[] invokeCommand(Context paramContext, byte[] paramArrayOfByte)
  {
    try
    {
      paramContext = mService.faceInvokeCommand(paramArrayOfByte);
      return paramContext;
    }
    catch (RemoteException paramArrayOfByte)
    {
      paramContext = new StringBuilder();
      paramContext.append("invokeCommand fail: ");
      paramContext.append(paramArrayOfByte);
      Slog.e("IFAAFaceManagerImplV2", paramContext.toString());
    }
    return null;
  }
  
  public void onSecureFrameAvalable(SecureSurface.FrameInfo paramFrameInfo, byte[] paramArrayOfByte)
  {
    paramFrameInfo = new StringBuilder();
    paramFrameInfo.append("CameraActivity::onSecureFrameAvalable");
    paramFrameInfo.append(paramArrayOfByte.length);
    Slog.d("IFAAFaceManagerImplV2", paramFrameInfo.toString());
    if (paramArrayOfByte.length != 16) {
      return;
    }
    paramFrameInfo = new StringBuffer(paramArrayOfByte.length * 3);
    int i = paramArrayOfByte.length;
    for (int j = 0; j < i; j++) {
      paramFrameInfo.append(String.format("%02x ", new Object[] { Byte.valueOf(paramArrayOfByte[j]) }));
    }
    Slog.i("IFAAFaceManagerImplV2", paramFrameInfo.toString());
    try
    {
      if (this.mCameraOperationLock.tryAcquire())
      {
        if (this.previewCamera_ != null)
        {
          i = byte4ToInt(paramArrayOfByte, 0);
          int k = byte4ToInt(paramArrayOfByte, 4);
          j = byte4ToInt(paramArrayOfByte, 8);
          int m = byte4ToInt(paramArrayOfByte, 12);
          IFAASecureCamera2API localIFAASecureCamera2API = this.previewCamera_;
          paramArrayOfByte = new android/graphics/Point;
          paramArrayOfByte.<init>(i, k);
          paramFrameInfo = new android/graphics/Point;
          paramFrameInfo.<init>(j, m);
          localIFAASecureCamera2API.rectFromTEECallback(paramArrayOfByte, paramFrameInfo);
        }
        else
        {
          Slog.e("IFAAFaceManagerImplV2", "Secure camera already stopped.. ignoring rectFromTEECallback");
        }
        this.mCameraOperationLock.release();
      }
      else
      {
        Slog.d("IFAAFaceManagerImplV2", "onSecureFrameAvalable Semaphore not Acquired ");
      }
    }
    catch (Exception paramFrameInfo)
    {
      Slog.e("IFAAFaceManagerImplV2", "rectFromTEECallback Exception caught", paramFrameInfo);
    }
  }
  
  public void serviceConnected()
  {
    Slog.i("IFAAFaceManagerImplV2", "seccamservice serviceConnected");
  }
  
  public void serviceDisconnected()
  {
    Slog.i("IFAAFaceManagerImplV2", "seccamservice serviceDisconnected");
    if (IsMediatek()) {
      return;
    }
    SecCamServiceClient.getInstance().release();
    SecCamServiceClient.getInstance().start(mContext, INSTANCE, "seccam2d", 8192, DESTINATION_MLVM.intValue());
  }
  
  /* Error */
  public void upgrade(String paramString)
  {
    // Byte code:
    //   0: iconst_3
    //   1: istore_2
    //   2: ldc_w 448
    //   5: newarray <illegal type>
    //   7: astore_3
    //   8: aconst_null
    //   9: astore 4
    //   11: aconst_null
    //   12: astore 5
    //   14: aconst_null
    //   15: astore 6
    //   17: aconst_null
    //   18: astore 7
    //   20: aconst_null
    //   21: astore 8
    //   23: aconst_null
    //   24: astore 9
    //   26: aconst_null
    //   27: astore 10
    //   29: aconst_null
    //   30: astore 11
    //   32: aconst_null
    //   33: astore 12
    //   35: aconst_null
    //   36: astore 13
    //   38: aload_1
    //   39: ifnull +2239 -> 2278
    //   42: aload_1
    //   43: invokevirtual 451	java/lang/String:length	()I
    //   46: ifne +6 -> 52
    //   49: goto +2229 -> 2278
    //   52: new 343	java/lang/StringBuilder
    //   55: dup
    //   56: invokespecial 344	java/lang/StringBuilder:<init>	()V
    //   59: astore 14
    //   61: aload 14
    //   63: ldc_w 453
    //   66: invokevirtual 350	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: pop
    //   70: aload 14
    //   72: aload_1
    //   73: invokevirtual 350	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: pop
    //   77: ldc 35
    //   79: aload 14
    //   81: invokevirtual 357	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: invokestatic 207	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   87: pop
    //   88: iload_2
    //   89: istore 15
    //   91: aload 8
    //   93: astore 16
    //   95: aload 13
    //   97: astore 14
    //   99: aload 4
    //   101: astore 17
    //   103: aload 9
    //   105: astore 18
    //   107: aload 5
    //   109: astore 19
    //   111: aload 10
    //   113: astore 20
    //   115: aload 6
    //   117: astore 21
    //   119: aload 11
    //   121: astore 22
    //   123: aload 7
    //   125: astore 23
    //   127: aload 12
    //   129: astore 24
    //   131: getstatic 107	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mService	Lorg/ifaa/android/manager/IIFAAService;
    //   134: iconst_0
    //   135: ldc_w 270
    //   138: iconst_0
    //   139: aload_3
    //   140: iconst_0
    //   141: invokeinterface 457 6 0
    //   146: ifge +100 -> 246
    //   149: iload_2
    //   150: istore 15
    //   152: aload 8
    //   154: astore 16
    //   156: aload 13
    //   158: astore 14
    //   160: aload 4
    //   162: astore 17
    //   164: aload 9
    //   166: astore 18
    //   168: aload 5
    //   170: astore 19
    //   172: aload 10
    //   174: astore 20
    //   176: aload 6
    //   178: astore 21
    //   180: aload 11
    //   182: astore 22
    //   184: aload 7
    //   186: astore 23
    //   188: aload 12
    //   190: astore 24
    //   192: ldc 35
    //   194: ldc_w 459
    //   197: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   200: pop
    //   201: iconst_0
    //   202: ifeq +21 -> 223
    //   205: new 461	java/lang/NullPointerException
    //   208: dup
    //   209: invokespecial 462	java/lang/NullPointerException:<init>	()V
    //   212: athrow
    //   213: astore_1
    //   214: ldc 35
    //   216: ldc_w 464
    //   219: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   222: pop
    //   223: iconst_0
    //   224: ifeq +21 -> 245
    //   227: new 461	java/lang/NullPointerException
    //   230: dup
    //   231: invokespecial 462	java/lang/NullPointerException:<init>	()V
    //   234: athrow
    //   235: astore_1
    //   236: ldc 35
    //   238: ldc_w 466
    //   241: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   244: pop
    //   245: return
    //   246: iload_2
    //   247: istore 15
    //   249: aload 8
    //   251: astore 16
    //   253: aload 13
    //   255: astore 14
    //   257: aload 4
    //   259: astore 17
    //   261: aload 9
    //   263: astore 18
    //   265: aload 5
    //   267: astore 19
    //   269: aload 10
    //   271: astore 20
    //   273: aload 6
    //   275: astore 21
    //   277: aload 11
    //   279: astore 22
    //   281: aload 7
    //   283: astore 23
    //   285: aload 12
    //   287: astore 24
    //   289: new 468	java/io/FileInputStream
    //   292: astore 25
    //   294: iload_2
    //   295: istore 15
    //   297: aload 8
    //   299: astore 16
    //   301: aload 13
    //   303: astore 14
    //   305: aload 4
    //   307: astore 17
    //   309: aload 9
    //   311: astore 18
    //   313: aload 5
    //   315: astore 19
    //   317: aload 10
    //   319: astore 20
    //   321: aload 6
    //   323: astore 21
    //   325: aload 11
    //   327: astore 22
    //   329: aload 7
    //   331: astore 23
    //   333: aload 12
    //   335: astore 24
    //   337: aload 25
    //   339: aload_1
    //   340: invokespecial 470	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   343: aload 25
    //   345: astore_1
    //   346: iload_2
    //   347: istore 15
    //   349: aload 8
    //   351: astore 16
    //   353: aload_1
    //   354: astore 14
    //   356: aload 4
    //   358: astore 17
    //   360: aload_1
    //   361: astore 18
    //   363: aload 5
    //   365: astore 19
    //   367: aload_1
    //   368: astore 20
    //   370: aload 6
    //   372: astore 21
    //   374: aload_1
    //   375: astore 22
    //   377: aload 7
    //   379: astore 23
    //   381: aload_1
    //   382: astore 24
    //   384: new 472	java/util/zip/ZipInputStream
    //   387: astore 11
    //   389: iload_2
    //   390: istore 15
    //   392: aload 8
    //   394: astore 16
    //   396: aload_1
    //   397: astore 14
    //   399: aload 4
    //   401: astore 17
    //   403: aload_1
    //   404: astore 18
    //   406: aload 5
    //   408: astore 19
    //   410: aload_1
    //   411: astore 20
    //   413: aload 6
    //   415: astore 21
    //   417: aload_1
    //   418: astore 22
    //   420: aload 7
    //   422: astore 23
    //   424: aload_1
    //   425: astore 24
    //   427: aload 11
    //   429: aload_1
    //   430: invokespecial 475	java/util/zip/ZipInputStream:<init>	(Ljava/io/InputStream;)V
    //   433: aload 11
    //   435: astore 5
    //   437: iload_2
    //   438: istore 15
    //   440: aload 5
    //   442: astore 16
    //   444: aload_1
    //   445: astore 14
    //   447: aload 5
    //   449: astore 17
    //   451: aload_1
    //   452: astore 18
    //   454: aload 5
    //   456: astore 19
    //   458: aload_1
    //   459: astore 20
    //   461: aload 5
    //   463: astore 21
    //   465: aload_1
    //   466: astore 22
    //   468: aload 5
    //   470: astore 23
    //   472: aload_1
    //   473: astore 24
    //   475: aload 5
    //   477: invokevirtual 479	java/util/zip/ZipInputStream:getNextEntry	()Ljava/util/zip/ZipEntry;
    //   480: astore 7
    //   482: iconst_3
    //   483: istore 15
    //   485: aload 7
    //   487: ifnull +779 -> 1266
    //   490: aload 5
    //   492: astore 18
    //   494: aload_1
    //   495: astore 16
    //   497: aload 5
    //   499: astore 17
    //   501: aload_1
    //   502: astore 14
    //   504: aload 5
    //   506: astore 24
    //   508: aload_1
    //   509: astore 21
    //   511: aload 5
    //   513: astore 22
    //   515: aload_1
    //   516: astore 19
    //   518: aload 5
    //   520: astore 20
    //   522: aload_1
    //   523: astore 23
    //   525: aload 7
    //   527: invokevirtual 484	java/util/zip/ZipEntry:getName	()Ljava/lang/String;
    //   530: astore 4
    //   532: aload 5
    //   534: astore 18
    //   536: aload_1
    //   537: astore 16
    //   539: aload 5
    //   541: astore 17
    //   543: aload_1
    //   544: astore 14
    //   546: aload 5
    //   548: astore 24
    //   550: aload_1
    //   551: astore 21
    //   553: aload 5
    //   555: astore 22
    //   557: aload_1
    //   558: astore 19
    //   560: aload 5
    //   562: astore 20
    //   564: aload_1
    //   565: astore 23
    //   567: aload 7
    //   569: invokevirtual 488	java/util/zip/ZipEntry:getSize	()J
    //   572: lstore 26
    //   574: aload 5
    //   576: astore 18
    //   578: aload_1
    //   579: astore 16
    //   581: aload 5
    //   583: astore 17
    //   585: aload_1
    //   586: astore 14
    //   588: aload 5
    //   590: astore 24
    //   592: aload_1
    //   593: astore 21
    //   595: aload 5
    //   597: astore 22
    //   599: aload_1
    //   600: astore 19
    //   602: aload 5
    //   604: astore 20
    //   606: aload_1
    //   607: astore 23
    //   609: new 343	java/lang/StringBuilder
    //   612: astore 7
    //   614: aload 5
    //   616: astore 18
    //   618: aload_1
    //   619: astore 16
    //   621: aload 5
    //   623: astore 17
    //   625: aload_1
    //   626: astore 14
    //   628: aload 5
    //   630: astore 24
    //   632: aload_1
    //   633: astore 21
    //   635: aload 5
    //   637: astore 22
    //   639: aload_1
    //   640: astore 19
    //   642: aload 5
    //   644: astore 20
    //   646: aload_1
    //   647: astore 23
    //   649: aload 7
    //   651: invokespecial 344	java/lang/StringBuilder:<init>	()V
    //   654: aload 5
    //   656: astore 18
    //   658: aload_1
    //   659: astore 16
    //   661: aload 5
    //   663: astore 17
    //   665: aload_1
    //   666: astore 14
    //   668: aload 5
    //   670: astore 24
    //   672: aload_1
    //   673: astore 21
    //   675: aload 5
    //   677: astore 22
    //   679: aload_1
    //   680: astore 19
    //   682: aload 5
    //   684: astore 20
    //   686: aload_1
    //   687: astore 23
    //   689: aload 7
    //   691: ldc_w 490
    //   694: invokevirtual 350	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   697: pop
    //   698: aload 5
    //   700: astore 18
    //   702: aload_1
    //   703: astore 16
    //   705: aload 5
    //   707: astore 17
    //   709: aload_1
    //   710: astore 14
    //   712: aload 5
    //   714: astore 24
    //   716: aload_1
    //   717: astore 21
    //   719: aload 5
    //   721: astore 22
    //   723: aload_1
    //   724: astore 19
    //   726: aload 5
    //   728: astore 20
    //   730: aload_1
    //   731: astore 23
    //   733: aload 7
    //   735: aload 4
    //   737: invokevirtual 350	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   740: pop
    //   741: aload 5
    //   743: astore 18
    //   745: aload_1
    //   746: astore 16
    //   748: aload 5
    //   750: astore 17
    //   752: aload_1
    //   753: astore 14
    //   755: aload 5
    //   757: astore 24
    //   759: aload_1
    //   760: astore 21
    //   762: aload 5
    //   764: astore 22
    //   766: aload_1
    //   767: astore 19
    //   769: aload 5
    //   771: astore 20
    //   773: aload_1
    //   774: astore 23
    //   776: ldc 35
    //   778: aload 7
    //   780: invokevirtual 357	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   783: invokestatic 207	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   786: pop
    //   787: iconst_0
    //   788: istore 28
    //   790: aload 5
    //   792: astore 18
    //   794: aload_1
    //   795: astore 16
    //   797: aload 5
    //   799: astore 17
    //   801: aload_1
    //   802: astore 14
    //   804: aload 5
    //   806: astore 24
    //   808: aload_1
    //   809: astore 21
    //   811: aload 5
    //   813: astore 22
    //   815: aload_1
    //   816: astore 19
    //   818: aload 5
    //   820: astore 20
    //   822: aload_1
    //   823: astore 23
    //   825: aload_3
    //   826: arraylength
    //   827: istore_2
    //   828: lconst_0
    //   829: lstore 29
    //   831: lload 29
    //   833: lload 26
    //   835: lcmp
    //   836: ifge +342 -> 1178
    //   839: aload 5
    //   841: astore 18
    //   843: aload_1
    //   844: astore 16
    //   846: aload 5
    //   848: astore 17
    //   850: aload_1
    //   851: astore 14
    //   853: aload 5
    //   855: astore 24
    //   857: aload_1
    //   858: astore 21
    //   860: aload 5
    //   862: astore 22
    //   864: aload_1
    //   865: astore 19
    //   867: aload 5
    //   869: astore 20
    //   871: aload_1
    //   872: astore 23
    //   874: aload 5
    //   876: aload_3
    //   877: aload_3
    //   878: arraylength
    //   879: iload_2
    //   880: isub
    //   881: iload_2
    //   882: invokevirtual 494	java/util/zip/ZipInputStream:read	([BII)I
    //   885: istore 31
    //   887: iload 31
    //   889: ifge +50 -> 939
    //   892: aload 5
    //   894: astore 18
    //   896: aload_1
    //   897: astore 16
    //   899: aload 5
    //   901: astore 17
    //   903: aload_1
    //   904: astore 14
    //   906: aload 5
    //   908: astore 24
    //   910: aload_1
    //   911: astore 21
    //   913: aload 5
    //   915: astore 22
    //   917: aload_1
    //   918: astore 19
    //   920: aload 5
    //   922: astore 20
    //   924: aload_1
    //   925: astore 23
    //   927: ldc 35
    //   929: ldc_w 496
    //   932: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   935: pop
    //   936: goto +242 -> 1178
    //   939: iload 28
    //   941: iload 31
    //   943: iadd
    //   944: istore 32
    //   946: iload_2
    //   947: iload 31
    //   949: isub
    //   950: istore 33
    //   952: iload 31
    //   954: ifeq +70 -> 1024
    //   957: iload 32
    //   959: i2l
    //   960: lload 29
    //   962: ladd
    //   963: lload 26
    //   965: lcmp
    //   966: ifeq +58 -> 1024
    //   969: iload 32
    //   971: istore 28
    //   973: iload 33
    //   975: istore_2
    //   976: aload 5
    //   978: astore 18
    //   980: aload_1
    //   981: astore 16
    //   983: aload 5
    //   985: astore 17
    //   987: aload_1
    //   988: astore 14
    //   990: aload 5
    //   992: astore 24
    //   994: aload_1
    //   995: astore 21
    //   997: aload 5
    //   999: astore 22
    //   1001: aload_1
    //   1002: astore 19
    //   1004: aload 5
    //   1006: astore 20
    //   1008: aload_1
    //   1009: astore 23
    //   1011: iload 33
    //   1013: aload_3
    //   1014: arraylength
    //   1015: i2d
    //   1016: ldc2_w 497
    //   1019: dmul
    //   1020: d2i
    //   1021: if_icmpge -190 -> 831
    //   1024: aload 5
    //   1026: astore 18
    //   1028: aload_1
    //   1029: astore 16
    //   1031: aload 5
    //   1033: astore 17
    //   1035: aload_1
    //   1036: astore 14
    //   1038: aload 5
    //   1040: astore 24
    //   1042: aload_1
    //   1043: astore 21
    //   1045: aload 5
    //   1047: astore 22
    //   1049: aload_1
    //   1050: astore 19
    //   1052: aload 5
    //   1054: astore 20
    //   1056: aload_1
    //   1057: astore 23
    //   1059: getstatic 107	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mService	Lorg/ifaa/android/manager/IIFAAService;
    //   1062: iconst_1
    //   1063: aload 4
    //   1065: lload 29
    //   1067: l2i
    //   1068: aload_3
    //   1069: iload 32
    //   1071: invokeinterface 457 6 0
    //   1076: ifge +50 -> 1126
    //   1079: aload 5
    //   1081: astore 18
    //   1083: aload_1
    //   1084: astore 16
    //   1086: aload 5
    //   1088: astore 17
    //   1090: aload_1
    //   1091: astore 14
    //   1093: aload 5
    //   1095: astore 24
    //   1097: aload_1
    //   1098: astore 21
    //   1100: aload 5
    //   1102: astore 22
    //   1104: aload_1
    //   1105: astore 19
    //   1107: aload 5
    //   1109: astore 20
    //   1111: aload_1
    //   1112: astore 23
    //   1114: ldc 35
    //   1116: ldc_w 500
    //   1119: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1122: pop
    //   1123: goto +55 -> 1178
    //   1126: lload 29
    //   1128: iload 32
    //   1130: i2l
    //   1131: ladd
    //   1132: lstore 29
    //   1134: iconst_0
    //   1135: istore 28
    //   1137: aload 5
    //   1139: astore 18
    //   1141: aload_1
    //   1142: astore 16
    //   1144: aload 5
    //   1146: astore 17
    //   1148: aload_1
    //   1149: astore 14
    //   1151: aload 5
    //   1153: astore 24
    //   1155: aload_1
    //   1156: astore 21
    //   1158: aload 5
    //   1160: astore 22
    //   1162: aload_1
    //   1163: astore 19
    //   1165: aload 5
    //   1167: astore 20
    //   1169: aload_1
    //   1170: astore 23
    //   1172: aload_3
    //   1173: arraylength
    //   1174: istore_2
    //   1175: goto -344 -> 831
    //   1178: aload 5
    //   1180: astore 18
    //   1182: aload_1
    //   1183: astore 16
    //   1185: aload 5
    //   1187: astore 17
    //   1189: aload_1
    //   1190: astore 14
    //   1192: aload 5
    //   1194: astore 24
    //   1196: aload_1
    //   1197: astore 21
    //   1199: aload 5
    //   1201: astore 22
    //   1203: aload_1
    //   1204: astore 19
    //   1206: aload 5
    //   1208: astore 20
    //   1210: aload_1
    //   1211: astore 23
    //   1213: aload 5
    //   1215: invokevirtual 503	java/util/zip/ZipInputStream:closeEntry	()V
    //   1218: aload 5
    //   1220: astore 18
    //   1222: aload_1
    //   1223: astore 16
    //   1225: aload 5
    //   1227: astore 17
    //   1229: aload_1
    //   1230: astore 14
    //   1232: aload 5
    //   1234: astore 24
    //   1236: aload_1
    //   1237: astore 21
    //   1239: aload 5
    //   1241: astore 22
    //   1243: aload_1
    //   1244: astore 19
    //   1246: aload 5
    //   1248: astore 20
    //   1250: aload_1
    //   1251: astore 23
    //   1253: aload 5
    //   1255: invokevirtual 479	java/util/zip/ZipInputStream:getNextEntry	()Ljava/util/zip/ZipEntry;
    //   1258: astore 7
    //   1260: iinc 15 -1
    //   1263: goto -778 -> 485
    //   1266: aload 5
    //   1268: astore 18
    //   1270: aload_1
    //   1271: astore 16
    //   1273: aload 5
    //   1275: astore 17
    //   1277: aload_1
    //   1278: astore 14
    //   1280: aload 5
    //   1282: astore 24
    //   1284: aload_1
    //   1285: astore 21
    //   1287: aload 5
    //   1289: astore 22
    //   1291: aload_1
    //   1292: astore 19
    //   1294: aload 5
    //   1296: astore 20
    //   1298: aload_1
    //   1299: astore 23
    //   1301: aload 5
    //   1303: invokevirtual 504	java/util/zip/ZipInputStream:close	()V
    //   1306: aconst_null
    //   1307: astore 5
    //   1309: aconst_null
    //   1310: astore 6
    //   1312: aconst_null
    //   1313: astore 7
    //   1315: aconst_null
    //   1316: astore 4
    //   1318: aconst_null
    //   1319: astore 8
    //   1321: aload 8
    //   1323: astore 18
    //   1325: aload_1
    //   1326: astore 16
    //   1328: aload 5
    //   1330: astore 17
    //   1332: aload_1
    //   1333: astore 14
    //   1335: aload 6
    //   1337: astore 24
    //   1339: aload_1
    //   1340: astore 21
    //   1342: aload 7
    //   1344: astore 22
    //   1346: aload_1
    //   1347: astore 19
    //   1349: aload 4
    //   1351: astore 20
    //   1353: aload_1
    //   1354: astore 23
    //   1356: aload_1
    //   1357: invokevirtual 505	java/io/FileInputStream:close	()V
    //   1360: aconst_null
    //   1361: astore 12
    //   1363: aconst_null
    //   1364: astore 10
    //   1366: aconst_null
    //   1367: astore 11
    //   1369: aconst_null
    //   1370: astore_1
    //   1371: aconst_null
    //   1372: astore 13
    //   1374: iload 15
    //   1376: ifle +281 -> 1657
    //   1379: aload 8
    //   1381: astore 18
    //   1383: aload 13
    //   1385: astore 16
    //   1387: aload 5
    //   1389: astore 17
    //   1391: aload 12
    //   1393: astore 14
    //   1395: aload 6
    //   1397: astore 24
    //   1399: aload 10
    //   1401: astore 21
    //   1403: aload 7
    //   1405: astore 22
    //   1407: aload 11
    //   1409: astore 19
    //   1411: aload 4
    //   1413: astore 20
    //   1415: aload_1
    //   1416: astore 23
    //   1418: new 343	java/lang/StringBuilder
    //   1421: astore 9
    //   1423: aload 8
    //   1425: astore 18
    //   1427: aload 13
    //   1429: astore 16
    //   1431: aload 5
    //   1433: astore 17
    //   1435: aload 12
    //   1437: astore 14
    //   1439: aload 6
    //   1441: astore 24
    //   1443: aload 10
    //   1445: astore 21
    //   1447: aload 7
    //   1449: astore 22
    //   1451: aload 11
    //   1453: astore 19
    //   1455: aload 4
    //   1457: astore 20
    //   1459: aload_1
    //   1460: astore 23
    //   1462: aload 9
    //   1464: invokespecial 344	java/lang/StringBuilder:<init>	()V
    //   1467: aload 8
    //   1469: astore 18
    //   1471: aload 13
    //   1473: astore 16
    //   1475: aload 5
    //   1477: astore 17
    //   1479: aload 12
    //   1481: astore 14
    //   1483: aload 6
    //   1485: astore 24
    //   1487: aload 10
    //   1489: astore 21
    //   1491: aload 7
    //   1493: astore 22
    //   1495: aload 11
    //   1497: astore 19
    //   1499: aload 4
    //   1501: astore 20
    //   1503: aload_1
    //   1504: astore 23
    //   1506: aload 9
    //   1508: ldc_w 507
    //   1511: invokevirtual 350	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1514: pop
    //   1515: aload 8
    //   1517: astore 18
    //   1519: aload 13
    //   1521: astore 16
    //   1523: aload 5
    //   1525: astore 17
    //   1527: aload 12
    //   1529: astore 14
    //   1531: aload 6
    //   1533: astore 24
    //   1535: aload 10
    //   1537: astore 21
    //   1539: aload 7
    //   1541: astore 22
    //   1543: aload 11
    //   1545: astore 19
    //   1547: aload 4
    //   1549: astore 20
    //   1551: aload_1
    //   1552: astore 23
    //   1554: aload 9
    //   1556: iload 15
    //   1558: invokevirtual 390	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1561: pop
    //   1562: aload 8
    //   1564: astore 18
    //   1566: aload 13
    //   1568: astore 16
    //   1570: aload 5
    //   1572: astore 17
    //   1574: aload 12
    //   1576: astore 14
    //   1578: aload 6
    //   1580: astore 24
    //   1582: aload 10
    //   1584: astore 21
    //   1586: aload 7
    //   1588: astore 22
    //   1590: aload 11
    //   1592: astore 19
    //   1594: aload 4
    //   1596: astore 20
    //   1598: aload_1
    //   1599: astore 23
    //   1601: ldc 35
    //   1603: aload 9
    //   1605: invokevirtual 357	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1608: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1611: pop
    //   1612: iconst_0
    //   1613: ifeq +21 -> 1634
    //   1616: new 461	java/lang/NullPointerException
    //   1619: dup
    //   1620: invokespecial 462	java/lang/NullPointerException:<init>	()V
    //   1623: athrow
    //   1624: astore_1
    //   1625: ldc 35
    //   1627: ldc_w 464
    //   1630: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1633: pop
    //   1634: iconst_0
    //   1635: ifeq +21 -> 1656
    //   1638: new 461	java/lang/NullPointerException
    //   1641: dup
    //   1642: invokespecial 462	java/lang/NullPointerException:<init>	()V
    //   1645: athrow
    //   1646: astore_1
    //   1647: ldc 35
    //   1649: ldc_w 466
    //   1652: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1655: pop
    //   1656: return
    //   1657: aload 8
    //   1659: astore 18
    //   1661: aload 13
    //   1663: astore 16
    //   1665: aload 5
    //   1667: astore 17
    //   1669: aload 12
    //   1671: astore 14
    //   1673: aload 6
    //   1675: astore 24
    //   1677: aload 10
    //   1679: astore 21
    //   1681: aload 7
    //   1683: astore 22
    //   1685: aload 11
    //   1687: astore 19
    //   1689: aload 4
    //   1691: astore 20
    //   1693: aload_1
    //   1694: astore 23
    //   1696: getstatic 107	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mService	Lorg/ifaa/android/manager/IIFAAService;
    //   1699: iconst_2
    //   1700: ldc_w 270
    //   1703: iconst_0
    //   1704: aload_3
    //   1705: iconst_0
    //   1706: invokeinterface 457 6 0
    //   1711: ifge +51 -> 1762
    //   1714: aload 8
    //   1716: astore 18
    //   1718: aload 13
    //   1720: astore 16
    //   1722: aload 5
    //   1724: astore 17
    //   1726: aload 12
    //   1728: astore 14
    //   1730: aload 6
    //   1732: astore 24
    //   1734: aload 10
    //   1736: astore 21
    //   1738: aload 7
    //   1740: astore 22
    //   1742: aload 11
    //   1744: astore 19
    //   1746: aload 4
    //   1748: astore 20
    //   1750: aload_1
    //   1751: astore 23
    //   1753: ldc 35
    //   1755: ldc_w 509
    //   1758: invokestatic 207	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1761: pop
    //   1762: iconst_0
    //   1763: ifeq +21 -> 1784
    //   1766: new 461	java/lang/NullPointerException
    //   1769: dup
    //   1770: invokespecial 462	java/lang/NullPointerException:<init>	()V
    //   1773: athrow
    //   1774: astore_1
    //   1775: ldc 35
    //   1777: ldc_w 464
    //   1780: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1783: pop
    //   1784: iconst_0
    //   1785: ifeq +27 -> 1812
    //   1788: new 461	java/lang/NullPointerException
    //   1791: dup
    //   1792: invokespecial 462	java/lang/NullPointerException:<init>	()V
    //   1795: athrow
    //   1796: goto +16 -> 1812
    //   1799: astore_1
    //   1800: ldc 35
    //   1802: ldc_w 466
    //   1805: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1808: pop
    //   1809: goto -13 -> 1796
    //   1812: goto +379 -> 2191
    //   1815: astore_1
    //   1816: aload 16
    //   1818: astore 14
    //   1820: goto +408 -> 2228
    //   1823: astore_1
    //   1824: iload 15
    //   1826: istore_2
    //   1827: aload 14
    //   1829: astore 18
    //   1831: goto +36 -> 1867
    //   1834: astore_1
    //   1835: aload 24
    //   1837: astore 19
    //   1839: goto +177 -> 2016
    //   1842: astore_1
    //   1843: aload 22
    //   1845: astore 21
    //   1847: goto +230 -> 2077
    //   1850: astore_1
    //   1851: aload 23
    //   1853: astore 24
    //   1855: goto +283 -> 2138
    //   1858: astore_1
    //   1859: aload 16
    //   1861: astore 18
    //   1863: goto +365 -> 2228
    //   1866: astore_1
    //   1867: iload_2
    //   1868: istore 15
    //   1870: aload 17
    //   1872: astore 16
    //   1874: aload 18
    //   1876: astore 14
    //   1878: new 343	java/lang/StringBuilder
    //   1881: astore 5
    //   1883: iload_2
    //   1884: istore 15
    //   1886: aload 17
    //   1888: astore 16
    //   1890: aload 18
    //   1892: astore 14
    //   1894: aload 5
    //   1896: invokespecial 344	java/lang/StringBuilder:<init>	()V
    //   1899: iload_2
    //   1900: istore 15
    //   1902: aload 17
    //   1904: astore 16
    //   1906: aload 18
    //   1908: astore 14
    //   1910: aload 5
    //   1912: ldc_w 511
    //   1915: invokevirtual 350	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1918: pop
    //   1919: iload_2
    //   1920: istore 15
    //   1922: aload 17
    //   1924: astore 16
    //   1926: aload 18
    //   1928: astore 14
    //   1930: aload 5
    //   1932: aload_1
    //   1933: invokevirtual 353	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1936: pop
    //   1937: iload_2
    //   1938: istore 15
    //   1940: aload 17
    //   1942: astore 16
    //   1944: aload 18
    //   1946: astore 14
    //   1948: ldc 35
    //   1950: aload 5
    //   1952: invokevirtual 357	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1955: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1958: pop
    //   1959: aload 17
    //   1961: ifnull +21 -> 1982
    //   1964: aload 17
    //   1966: invokevirtual 504	java/util/zip/ZipInputStream:close	()V
    //   1969: goto +13 -> 1982
    //   1972: astore_1
    //   1973: ldc 35
    //   1975: ldc_w 464
    //   1978: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1981: pop
    //   1982: aload 18
    //   1984: ifnull +207 -> 2191
    //   1987: aload 18
    //   1989: invokevirtual 505	java/io/FileInputStream:close	()V
    //   1992: goto +199 -> 2191
    //   1995: astore_1
    //   1996: ldc 35
    //   1998: ldc_w 466
    //   2001: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2004: pop
    //   2005: goto -13 -> 1992
    //   2008: astore_1
    //   2009: aload 20
    //   2011: astore 21
    //   2013: iload_2
    //   2014: istore 15
    //   2016: aload 19
    //   2018: astore 16
    //   2020: aload 21
    //   2022: astore 14
    //   2024: ldc 35
    //   2026: ldc_w 513
    //   2029: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2032: pop
    //   2033: aload 19
    //   2035: ifnull +21 -> 2056
    //   2038: aload 19
    //   2040: invokevirtual 504	java/util/zip/ZipInputStream:close	()V
    //   2043: goto +13 -> 2056
    //   2046: astore_1
    //   2047: ldc 35
    //   2049: ldc_w 464
    //   2052: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2055: pop
    //   2056: aload 21
    //   2058: ifnull +133 -> 2191
    //   2061: aload 21
    //   2063: invokevirtual 505	java/io/FileInputStream:close	()V
    //   2066: goto -74 -> 1992
    //   2069: astore_1
    //   2070: aload 22
    //   2072: astore 19
    //   2074: iload_2
    //   2075: istore 15
    //   2077: aload 21
    //   2079: astore 16
    //   2081: aload 19
    //   2083: astore 14
    //   2085: ldc 35
    //   2087: ldc_w 515
    //   2090: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2093: pop
    //   2094: aload 21
    //   2096: ifnull +21 -> 2117
    //   2099: aload 21
    //   2101: invokevirtual 504	java/util/zip/ZipInputStream:close	()V
    //   2104: goto +13 -> 2117
    //   2107: astore_1
    //   2108: ldc 35
    //   2110: ldc_w 464
    //   2113: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2116: pop
    //   2117: aload 19
    //   2119: ifnull +72 -> 2191
    //   2122: aload 19
    //   2124: invokevirtual 505	java/io/FileInputStream:close	()V
    //   2127: goto -135 -> 1992
    //   2130: astore_1
    //   2131: aload 23
    //   2133: astore 20
    //   2135: iload_2
    //   2136: istore 15
    //   2138: aload 20
    //   2140: astore 16
    //   2142: aload 24
    //   2144: astore 14
    //   2146: ldc 35
    //   2148: ldc_w 517
    //   2151: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2154: pop
    //   2155: aload 20
    //   2157: ifnull +21 -> 2178
    //   2160: aload 20
    //   2162: invokevirtual 504	java/util/zip/ZipInputStream:close	()V
    //   2165: goto +13 -> 2178
    //   2168: astore_1
    //   2169: ldc 35
    //   2171: ldc_w 464
    //   2174: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2177: pop
    //   2178: aload 24
    //   2180: ifnull +11 -> 2191
    //   2183: aload 24
    //   2185: invokevirtual 505	java/io/FileInputStream:close	()V
    //   2188: goto -196 -> 1992
    //   2191: invokestatic 163	org/ifaa/android/manager/face/IFAAFaceManagerImpl:IsMediatek	()Z
    //   2194: ifne +33 -> 2227
    //   2197: invokestatic 218	com/android/qualcomm/qti/seccamapi/SecCamServiceClient:getInstance	()Lcom/android/qualcomm/qti/seccamapi/SecCamServiceClient;
    //   2200: invokevirtual 221	com/android/qualcomm/qti/seccamapi/SecCamServiceClient:release	()V
    //   2203: invokestatic 218	com/android/qualcomm/qti/seccamapi/SecCamServiceClient:getInstance	()Lcom/android/qualcomm/qti/seccamapi/SecCamServiceClient;
    //   2206: getstatic 105	org/ifaa/android/manager/face/IFAAFaceManagerImpl:mContext	Landroid/content/Context;
    //   2209: getstatic 79	org/ifaa/android/manager/face/IFAAFaceManagerImpl:INSTANCE	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
    //   2212: ldc 41
    //   2214: sipush 8192
    //   2217: getstatic 89	org/ifaa/android/manager/face/IFAAFaceManagerImpl:DESTINATION_MLVM	Ljava/lang/Integer;
    //   2220: invokevirtual 226	java/lang/Integer:intValue	()I
    //   2223: invokevirtual 230	com/android/qualcomm/qti/seccamapi/SecCamServiceClient:start	(Landroid/content/Context;Lcom/android/qualcomm/qti/seccamapi/SecCamServiceClient$ClientCallback;Ljava/lang/String;II)Z
    //   2226: pop
    //   2227: return
    //   2228: aload 18
    //   2230: ifnull +22 -> 2252
    //   2233: aload 18
    //   2235: invokevirtual 504	java/util/zip/ZipInputStream:close	()V
    //   2238: goto +14 -> 2252
    //   2241: astore 5
    //   2243: ldc 35
    //   2245: ldc_w 464
    //   2248: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2251: pop
    //   2252: aload 14
    //   2254: ifnull +22 -> 2276
    //   2257: aload 14
    //   2259: invokevirtual 505	java/io/FileInputStream:close	()V
    //   2262: goto +14 -> 2276
    //   2265: astore 5
    //   2267: ldc 35
    //   2269: ldc_w 466
    //   2272: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2275: pop
    //   2276: aload_1
    //   2277: athrow
    //   2278: ldc 35
    //   2280: ldc_w 519
    //   2283: invokestatic 248	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   2286: pop
    //   2287: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2288	0	this	IFAAFaceManagerImpl
    //   0	2288	1	paramString	String
    //   1	2135	2	i	int
    //   7	1698	3	arrayOfByte	byte[]
    //   9	1738	4	str	String
    //   12	1939	5	localObject1	Object
    //   2241	1	5	localIOException1	java.io.IOException
    //   2265	1	5	localIOException2	java.io.IOException
    //   15	1716	6	localObject2	Object
    //   18	1721	7	localObject3	Object
    //   21	1694	8	localObject4	Object
    //   24	1580	9	localStringBuilder	StringBuilder
    //   27	1708	10	localObject5	Object
    //   30	1713	11	localZipInputStream	java.util.zip.ZipInputStream
    //   33	1694	12	localObject6	Object
    //   36	1683	13	localObject7	Object
    //   59	2199	14	localObject8	Object
    //   89	2048	15	j	int
    //   93	2048	16	localObject9	Object
    //   101	1864	17	localObject10	Object
    //   105	2129	18	localObject11	Object
    //   109	2014	19	localObject12	Object
    //   113	2048	20	localObject13	Object
    //   117	1983	21	localObject14	Object
    //   121	1950	22	localObject15	Object
    //   125	2007	23	localObject16	Object
    //   129	2055	24	localObject17	Object
    //   292	52	25	localFileInputStream	java.io.FileInputStream
    //   572	392	26	l1	long
    //   788	348	28	k	int
    //   829	304	29	l2	long
    //   885	68	31	m	int
    //   944	185	32	n	int
    //   950	72	33	i1	int
    // Exception table:
    //   from	to	target	type
    //   205	213	213	java/io/IOException
    //   227	235	235	java/io/IOException
    //   1616	1624	1624	java/io/IOException
    //   1638	1646	1646	java/io/IOException
    //   1766	1774	1774	java/io/IOException
    //   1788	1796	1799	java/io/IOException
    //   525	532	1815	finally
    //   567	574	1815	finally
    //   609	614	1815	finally
    //   649	654	1815	finally
    //   689	698	1815	finally
    //   733	741	1815	finally
    //   776	787	1815	finally
    //   825	828	1815	finally
    //   874	887	1815	finally
    //   927	936	1815	finally
    //   1011	1024	1815	finally
    //   1059	1079	1815	finally
    //   1114	1123	1815	finally
    //   1172	1175	1815	finally
    //   1213	1218	1815	finally
    //   1253	1260	1815	finally
    //   1301	1306	1815	finally
    //   1356	1360	1815	finally
    //   1418	1423	1815	finally
    //   1462	1467	1815	finally
    //   1506	1515	1815	finally
    //   1554	1562	1815	finally
    //   1601	1612	1815	finally
    //   1696	1714	1815	finally
    //   1753	1762	1815	finally
    //   525	532	1823	android/os/RemoteException
    //   567	574	1823	android/os/RemoteException
    //   609	614	1823	android/os/RemoteException
    //   649	654	1823	android/os/RemoteException
    //   689	698	1823	android/os/RemoteException
    //   733	741	1823	android/os/RemoteException
    //   776	787	1823	android/os/RemoteException
    //   825	828	1823	android/os/RemoteException
    //   874	887	1823	android/os/RemoteException
    //   927	936	1823	android/os/RemoteException
    //   1011	1024	1823	android/os/RemoteException
    //   1059	1079	1823	android/os/RemoteException
    //   1114	1123	1823	android/os/RemoteException
    //   1172	1175	1823	android/os/RemoteException
    //   1213	1218	1823	android/os/RemoteException
    //   1253	1260	1823	android/os/RemoteException
    //   1301	1306	1823	android/os/RemoteException
    //   1356	1360	1823	android/os/RemoteException
    //   1418	1423	1823	android/os/RemoteException
    //   1462	1467	1823	android/os/RemoteException
    //   1506	1515	1823	android/os/RemoteException
    //   1554	1562	1823	android/os/RemoteException
    //   1601	1612	1823	android/os/RemoteException
    //   1696	1714	1823	android/os/RemoteException
    //   1753	1762	1823	android/os/RemoteException
    //   525	532	1834	java/lang/SecurityException
    //   567	574	1834	java/lang/SecurityException
    //   609	614	1834	java/lang/SecurityException
    //   649	654	1834	java/lang/SecurityException
    //   689	698	1834	java/lang/SecurityException
    //   733	741	1834	java/lang/SecurityException
    //   776	787	1834	java/lang/SecurityException
    //   825	828	1834	java/lang/SecurityException
    //   874	887	1834	java/lang/SecurityException
    //   927	936	1834	java/lang/SecurityException
    //   1011	1024	1834	java/lang/SecurityException
    //   1059	1079	1834	java/lang/SecurityException
    //   1114	1123	1834	java/lang/SecurityException
    //   1172	1175	1834	java/lang/SecurityException
    //   1213	1218	1834	java/lang/SecurityException
    //   1253	1260	1834	java/lang/SecurityException
    //   1301	1306	1834	java/lang/SecurityException
    //   1356	1360	1834	java/lang/SecurityException
    //   1418	1423	1834	java/lang/SecurityException
    //   1462	1467	1834	java/lang/SecurityException
    //   1506	1515	1834	java/lang/SecurityException
    //   1554	1562	1834	java/lang/SecurityException
    //   1601	1612	1834	java/lang/SecurityException
    //   1696	1714	1834	java/lang/SecurityException
    //   1753	1762	1834	java/lang/SecurityException
    //   525	532	1842	java/io/IOException
    //   567	574	1842	java/io/IOException
    //   609	614	1842	java/io/IOException
    //   649	654	1842	java/io/IOException
    //   689	698	1842	java/io/IOException
    //   733	741	1842	java/io/IOException
    //   776	787	1842	java/io/IOException
    //   825	828	1842	java/io/IOException
    //   874	887	1842	java/io/IOException
    //   927	936	1842	java/io/IOException
    //   1011	1024	1842	java/io/IOException
    //   1059	1079	1842	java/io/IOException
    //   1114	1123	1842	java/io/IOException
    //   1172	1175	1842	java/io/IOException
    //   1213	1218	1842	java/io/IOException
    //   1253	1260	1842	java/io/IOException
    //   1301	1306	1842	java/io/IOException
    //   1356	1360	1842	java/io/IOException
    //   1418	1423	1842	java/io/IOException
    //   1462	1467	1842	java/io/IOException
    //   1506	1515	1842	java/io/IOException
    //   1554	1562	1842	java/io/IOException
    //   1601	1612	1842	java/io/IOException
    //   1696	1714	1842	java/io/IOException
    //   1753	1762	1842	java/io/IOException
    //   525	532	1850	java/io/FileNotFoundException
    //   567	574	1850	java/io/FileNotFoundException
    //   609	614	1850	java/io/FileNotFoundException
    //   649	654	1850	java/io/FileNotFoundException
    //   689	698	1850	java/io/FileNotFoundException
    //   733	741	1850	java/io/FileNotFoundException
    //   776	787	1850	java/io/FileNotFoundException
    //   825	828	1850	java/io/FileNotFoundException
    //   874	887	1850	java/io/FileNotFoundException
    //   927	936	1850	java/io/FileNotFoundException
    //   1011	1024	1850	java/io/FileNotFoundException
    //   1059	1079	1850	java/io/FileNotFoundException
    //   1114	1123	1850	java/io/FileNotFoundException
    //   1172	1175	1850	java/io/FileNotFoundException
    //   1213	1218	1850	java/io/FileNotFoundException
    //   1253	1260	1850	java/io/FileNotFoundException
    //   1301	1306	1850	java/io/FileNotFoundException
    //   1356	1360	1850	java/io/FileNotFoundException
    //   1418	1423	1850	java/io/FileNotFoundException
    //   1462	1467	1850	java/io/FileNotFoundException
    //   1506	1515	1850	java/io/FileNotFoundException
    //   1554	1562	1850	java/io/FileNotFoundException
    //   1601	1612	1850	java/io/FileNotFoundException
    //   1696	1714	1850	java/io/FileNotFoundException
    //   1753	1762	1850	java/io/FileNotFoundException
    //   131	149	1858	finally
    //   192	201	1858	finally
    //   289	294	1858	finally
    //   337	343	1858	finally
    //   384	389	1858	finally
    //   427	433	1858	finally
    //   475	482	1858	finally
    //   1878	1883	1858	finally
    //   1894	1899	1858	finally
    //   1910	1919	1858	finally
    //   1930	1937	1858	finally
    //   1948	1959	1858	finally
    //   2024	2033	1858	finally
    //   2085	2094	1858	finally
    //   2146	2155	1858	finally
    //   131	149	1866	android/os/RemoteException
    //   192	201	1866	android/os/RemoteException
    //   289	294	1866	android/os/RemoteException
    //   337	343	1866	android/os/RemoteException
    //   384	389	1866	android/os/RemoteException
    //   427	433	1866	android/os/RemoteException
    //   475	482	1866	android/os/RemoteException
    //   1964	1969	1972	java/io/IOException
    //   1987	1992	1995	java/io/IOException
    //   2061	2066	1995	java/io/IOException
    //   2122	2127	1995	java/io/IOException
    //   2183	2188	1995	java/io/IOException
    //   131	149	2008	java/lang/SecurityException
    //   192	201	2008	java/lang/SecurityException
    //   289	294	2008	java/lang/SecurityException
    //   337	343	2008	java/lang/SecurityException
    //   384	389	2008	java/lang/SecurityException
    //   427	433	2008	java/lang/SecurityException
    //   475	482	2008	java/lang/SecurityException
    //   2038	2043	2046	java/io/IOException
    //   131	149	2069	java/io/IOException
    //   192	201	2069	java/io/IOException
    //   289	294	2069	java/io/IOException
    //   337	343	2069	java/io/IOException
    //   384	389	2069	java/io/IOException
    //   427	433	2069	java/io/IOException
    //   475	482	2069	java/io/IOException
    //   2099	2104	2107	java/io/IOException
    //   131	149	2130	java/io/FileNotFoundException
    //   192	201	2130	java/io/FileNotFoundException
    //   289	294	2130	java/io/FileNotFoundException
    //   337	343	2130	java/io/FileNotFoundException
    //   384	389	2130	java/io/FileNotFoundException
    //   427	433	2130	java/io/FileNotFoundException
    //   475	482	2130	java/io/FileNotFoundException
    //   2160	2165	2168	java/io/IOException
    //   2233	2238	2241	java/io/IOException
    //   2257	2262	2265	java/io/IOException
  }
  
  private class SeccamStopTask
    extends AsyncTask<String, Void, Boolean>
  {
    private SeccamStopTask() {}
    
    /* Error */
    protected Boolean doInBackground(String... paramVarArgs)
    {
      // Byte code:
      //   0: ldc 27
      //   2: ldc 28
      //   4: invokestatic 34	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   7: pop
      //   8: aload_0
      //   9: getfield 14	org/ifaa/android/manager/face/IFAAFaceManagerImpl$SeccamStopTask:this$0	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
      //   12: invokestatic 38	org/ifaa/android/manager/face/IFAAFaceManagerImpl:access$700	(Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;)Ljava/util/concurrent/Semaphore;
      //   15: invokevirtual 43	java/util/concurrent/Semaphore:acquire	()V
      //   18: ldc 27
      //   20: ldc 45
      //   22: invokestatic 48	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   25: pop
      //   26: aload_0
      //   27: getfield 14	org/ifaa/android/manager/face/IFAAFaceManagerImpl$SeccamStopTask:this$0	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
      //   30: invokestatic 52	org/ifaa/android/manager/face/IFAAFaceManagerImpl:access$800	(Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;)Lorg/ifaa/android/manager/face/IFAASecureCamera2API;
      //   33: ifnull +26 -> 59
      //   36: aload_0
      //   37: getfield 14	org/ifaa/android/manager/face/IFAAFaceManagerImpl$SeccamStopTask:this$0	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
      //   40: invokestatic 52	org/ifaa/android/manager/face/IFAAFaceManagerImpl:access$800	(Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;)Lorg/ifaa/android/manager/face/IFAASecureCamera2API;
      //   43: invokevirtual 58	org/ifaa/android/manager/face/IFAASecureCamera2API:stop	()Ljava/lang/Boolean;
      //   46: pop
      //   47: aload_0
      //   48: getfield 14	org/ifaa/android/manager/face/IFAAFaceManagerImpl$SeccamStopTask:this$0	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
      //   51: aconst_null
      //   52: invokestatic 62	org/ifaa/android/manager/face/IFAAFaceManagerImpl:access$802	(Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;Lorg/ifaa/android/manager/face/IFAASecureCamera2API;)Lorg/ifaa/android/manager/face/IFAASecureCamera2API;
      //   55: pop
      //   56: goto +11 -> 67
      //   59: ldc 27
      //   61: ldc 64
      //   63: invokestatic 67	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   66: pop
      //   67: ldc 27
      //   69: ldc 69
      //   71: invokestatic 48	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   74: pop
      //   75: aload_0
      //   76: getfield 14	org/ifaa/android/manager/face/IFAAFaceManagerImpl$SeccamStopTask:this$0	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
      //   79: invokestatic 73	org/ifaa/android/manager/face/IFAAFaceManagerImpl:access$900	(Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;)Landroid/os/ConditionVariable;
      //   82: invokevirtual 78	android/os/ConditionVariable:open	()V
      //   85: aload_0
      //   86: getfield 14	org/ifaa/android/manager/face/IFAAFaceManagerImpl$SeccamStopTask:this$0	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
      //   89: invokestatic 38	org/ifaa/android/manager/face/IFAAFaceManagerImpl:access$700	(Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;)Ljava/util/concurrent/Semaphore;
      //   92: invokevirtual 81	java/util/concurrent/Semaphore:release	()V
      //   95: getstatic 87	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
      //   98: areturn
      //   99: astore_1
      //   100: goto +17 -> 117
      //   103: astore_1
      //   104: new 89	java/lang/RuntimeException
      //   107: astore_2
      //   108: aload_2
      //   109: ldc 91
      //   111: aload_1
      //   112: invokespecial 94	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   115: aload_2
      //   116: athrow
      //   117: aload_0
      //   118: getfield 14	org/ifaa/android/manager/face/IFAAFaceManagerImpl$SeccamStopTask:this$0	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
      //   121: invokestatic 73	org/ifaa/android/manager/face/IFAAFaceManagerImpl:access$900	(Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;)Landroid/os/ConditionVariable;
      //   124: invokevirtual 78	android/os/ConditionVariable:open	()V
      //   127: aload_0
      //   128: getfield 14	org/ifaa/android/manager/face/IFAAFaceManagerImpl$SeccamStopTask:this$0	Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;
      //   131: invokestatic 38	org/ifaa/android/manager/face/IFAAFaceManagerImpl:access$700	(Lorg/ifaa/android/manager/face/IFAAFaceManagerImpl;)Ljava/util/concurrent/Semaphore;
      //   134: invokevirtual 81	java/util/concurrent/Semaphore:release	()V
      //   137: aload_1
      //   138: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	139	0	this	SeccamStopTask
      //   0	139	1	paramVarArgs	String[]
      //   107	9	2	localRuntimeException	RuntimeException
      // Exception table:
      //   from	to	target	type
      //   8	56	99	finally
      //   59	67	99	finally
      //   67	75	99	finally
      //   104	117	99	finally
      //   8	56	103	java/lang/InterruptedException
      //   59	67	103	java/lang/InterruptedException
      //   67	75	103	java/lang/InterruptedException
    }
    
    protected void onPostExecute(Boolean paramBoolean)
    {
      Slog.d("IFAAFaceManagerImplV2", "onPostExecute");
    }
    
    protected void onPreExecute()
    {
      Slog.d("IFAAFaceManagerImplV2", "onPreExecute");
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/face/IFAAFaceManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */