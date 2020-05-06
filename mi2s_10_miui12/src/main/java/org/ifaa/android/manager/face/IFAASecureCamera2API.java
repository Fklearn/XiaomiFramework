package org.ifaa.android.manager.face;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.OutputConfiguration;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import com.android.qualcomm.qti.seccamapi.SecureCamera2Surface;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class IFAASecureCamera2API
{
  private static final int SESSION_REGULAR = 0;
  private static final int SESSION_SECCAM = 33014;
  private static final String TAG = "IFAASecureCamera2API";
  private static boolean is_2dfa = true;
  private static Context mContext = null;
  private final int DEFAULT_REGION_WEIGHT = 30;
  private Handler backgroundHandler_;
  private HandlerThread backgroundThread_;
  protected CameraDevice cameraDevice_ = null;
  private Integer cameraId_;
  protected Semaphore cameraOpenCloseSemaphore_ = new Semaphore(1);
  protected CountDownLatch cameraStartedLatch_ = new CountDownLatch(1);
  protected boolean cameraStarted_ = false;
  protected CountDownLatch cameraStopedLatch_ = new CountDownLatch(1);
  protected CaptureRequest.Builder captureBuilder_ = null;
  protected CameraCaptureSession.CaptureCallback captureCallback_ = null;
  protected CountDownLatch captureSessionClosedLatch_ = new CountDownLatch(1);
  protected CameraCaptureSession captureSession_ = null;
  private Integer format_ = Integer.valueOf(35);
  private Integer height_ = Integer.valueOf(720);
  private IFAAFaceManagerImpl ifaafacemgrimpl_ = null;
  protected final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback()
  {
    public void onClosed(CameraDevice paramAnonymousCameraDevice)
    {
      paramAnonymousCameraDevice = new StringBuilder();
      paramAnonymousCameraDevice.append("SecureCamera2API::CameraDevice.StateCallback::onClosed - Camera #");
      paramAnonymousCameraDevice.append(IFAASecureCamera2API.this.cameraId_);
      Log.d("IFAASecureCamera2API", paramAnonymousCameraDevice.toString());
      paramAnonymousCameraDevice = IFAASecureCamera2API.this;
      paramAnonymousCameraDevice.cameraDevice_ = null;
      paramAnonymousCameraDevice.cameraStartedLatch_.countDown();
      IFAASecureCamera2API.this.cameraStopedLatch_.countDown();
    }
    
    public void onDisconnected(CameraDevice paramAnonymousCameraDevice)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SecureCamera2API::CameraDevice.StateCallback::onDisconnected - Camera #");
      localStringBuilder.append(IFAASecureCamera2API.this.cameraId_);
      Log.d("IFAASecureCamera2API", localStringBuilder.toString());
      paramAnonymousCameraDevice.close();
    }
    
    public void onError(CameraDevice paramAnonymousCameraDevice, int paramAnonymousInt)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SecureCamera2API::CameraDevice.StateCallback::onError - Camera #");
      localStringBuilder.append(IFAASecureCamera2API.this.cameraId_);
      localStringBuilder.append(" - Error: ");
      localStringBuilder.append(paramAnonymousInt);
      Log.d("IFAASecureCamera2API", localStringBuilder.toString());
      paramAnonymousCameraDevice.close();
    }
    
    public void onOpened(CameraDevice paramAnonymousCameraDevice)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("SecureCamera2API::CameraDevice.StateCallback::onOpened - Camera #");
      ((StringBuilder)localObject).append(IFAASecureCamera2API.this.cameraId_);
      Log.d("IFAASecureCamera2API", ((StringBuilder)localObject).toString());
      localObject = IFAASecureCamera2API.this;
      ((IFAASecureCamera2API)localObject).cameraDevice_ = paramAnonymousCameraDevice;
      ((IFAASecureCamera2API)localObject).createCameraSessionCommon();
    }
  };
  private Integer numOfBuffers_ = Integer.valueOf(2);
  private Integer previewRotation_ = Integer.valueOf(0);
  private Integer previewSurfaceNumOfBuffers_ = Integer.valueOf(3);
  private SurfaceView previewSurfaceView_ = null;
  protected SecureCamera2Surface secureCamera2Surface_ = null;
  protected CountDownLatch surfaceReadyLatch_ = new CountDownLatch(1);
  private Integer width_ = Integer.valueOf(1280);
  
  public IFAASecureCamera2API(Context paramContext, IFAAFaceManagerImpl paramIFAAFaceManagerImpl)
  {
    mContext = paramContext;
    this.ifaafacemgrimpl_ = paramIFAAFaceManagerImpl;
  }
  
  protected void createCameraSessionCommon()
  {
    try
    {
      this.secureCamera2Surface_ = null;
      localObject1 = new com/android/qualcomm/qti/seccamapi/SecureCamera2Surface;
      ((SecureCamera2Surface)localObject1).<init>(this.cameraId_.intValue(), this.width_.intValue(), this.height_.intValue(), this.format_.intValue(), this.numOfBuffers_.intValue());
      this.secureCamera2Surface_ = ((SecureCamera2Surface)localObject1);
      localObject1 = new java/lang/StringBuilder;
      ((StringBuilder)localObject1).<init>();
      ((StringBuilder)localObject1).append("createCameraSessionCommon - numOfBuffers_ = ");
      ((StringBuilder)localObject1).append(this.numOfBuffers_);
      Log.d("IFAASecureCamera2API", ((StringBuilder)localObject1).toString());
      this.secureCamera2Surface_.enableFrameCallback(this.ifaafacemgrimpl_, this.ifaafacemgrimpl_.frameCallbackReturnParamsSize_);
      Object localObject2 = this.secureCamera2Surface_.getCaptureSurface();
      this.captureBuilder_ = this.cameraDevice_.createCaptureRequest(1);
      this.captureBuilder_.addTarget((Surface)localObject2);
      localObject1 = new java/util/ArrayList;
      ((ArrayList)localObject1).<init>();
      Object localObject3 = new android/hardware/camera2/params/OutputConfiguration;
      ((OutputConfiguration)localObject3).<init>((Surface)localObject2);
      ((List)localObject1).add(localObject3);
      localObject3 = this.cameraDevice_;
      localObject2 = new org/ifaa/android/manager/face/IFAASecureCamera2API$2;
      ((2)localObject2).<init>(this);
      ((CameraDevice)localObject3).createCustomCaptureSession(null, (List)localObject1, 33014, (CameraCaptureSession.StateCallback)localObject2, null);
    }
    catch (CameraAccessException localCameraAccessException)
    {
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("SecureCamera2API::createCameraSession ERROR: ");
      ((StringBuilder)localObject1).append(localCameraAccessException);
      Log.d("IFAASecureCamera2API", ((StringBuilder)localObject1).toString());
    }
  }
  
  protected Rect getActivityArray()
  {
    try
    {
      localObject = (Rect)((CameraManager)mContext.getSystemService("camera")).getCameraCharacteristics(Integer.toString(this.cameraId_.intValue())).get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
      if (localObject == null) {
        localObject = new Rect(0, 0, 0, 0);
      }
      if ((((Rect)localObject).width() != 0) && (((Rect)localObject).height() != 0)) {
        return (Rect)localObject;
      }
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      localStringBuilder.append("Face AE: error active array size:");
      localStringBuilder.append(((Rect)localObject).width());
      localStringBuilder.append("x");
      localStringBuilder.append(((Rect)localObject).height());
      Log.d("IFAASecureCamera2API", localStringBuilder.toString());
      return null;
    }
    catch (CameraAccessException localCameraAccessException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("getActivityArray - Camera - ERROR: ");
      ((StringBuilder)localObject).append(localCameraAccessException);
      Log.d("IFAASecureCamera2API", ((StringBuilder)localObject).toString());
    }
    return null;
  }
  
  protected Handler getBackgroundHandler()
  {
    return this.backgroundHandler_;
  }
  
  public int getCameraRotation(int paramInt)
  {
    int i = 0;
    try
    {
      paramInt = ((Integer)((CameraManager)mContext.getSystemService("camera")).getCameraCharacteristics(Integer.toString(paramInt)).get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue() % 360;
      if (paramInt != 0)
      {
        if (paramInt != 90)
        {
          if (paramInt != 180)
          {
            if (paramInt != 270) {
              paramInt = 0;
            } else {
              paramInt = 5;
            }
          }
          else {
            paramInt = 2;
          }
        }
        else {
          paramInt = 6;
        }
      }
      else {
        paramInt = 1;
      }
    }
    catch (CameraAccessException localCameraAccessException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SecureCamera2API::getCameraRotation - ERROR: ");
      localStringBuilder.append(localCameraAccessException);
      Log.d("IFAASecureCamera2API", localStringBuilder.toString());
      paramInt = i;
    }
    return paramInt;
  }
  
  protected Boolean isSupportAERegion()
  {
    try
    {
      localObject = (Integer)((CameraManager)mContext.getSystemService("camera")).getCameraCharacteristics(Integer.toString(this.cameraId_.intValue())).get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
      int i;
      if (localObject != null) {
        i = ((Integer)localObject).intValue();
      } else {
        i = 0;
      }
      if (i <= 0)
      {
        Log.d("IFAASecureCamera2API", "Face AE: don't have AE region capability.");
      }
      else
      {
        localObject = Boolean.TRUE;
        return (Boolean)localObject;
      }
    }
    catch (CameraAccessException localCameraAccessException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("isSupportAERegion - Camera - ERROR: ");
      ((StringBuilder)localObject).append(localCameraAccessException);
      Log.d("IFAASecureCamera2API", ((StringBuilder)localObject).toString());
    }
    return Boolean.FALSE;
  }
  
  public void rectFromTEECallback(Point paramPoint1, Point paramPoint2)
  {
    if ((this.captureSession_ != null) && (isSupportAERegion() != Boolean.FALSE) && ((paramPoint1.x != 0) || (paramPoint1.y != 0) || (paramPoint2.x != 0) || (paramPoint2.y != 0)))
    {
      Rect localRect = getActivityArray();
      if (localRect == null) {
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Face AE: getting rect topLeft:");
      localStringBuilder.append(paramPoint1.x);
      localStringBuilder.append(",");
      localStringBuilder.append(paramPoint1.y);
      localStringBuilder.append(", rightBottom:");
      localStringBuilder.append(paramPoint2.x);
      localStringBuilder.append(",");
      localStringBuilder.append(paramPoint2.y);
      Log.d("IFAASecureCamera2API", localStringBuilder.toString());
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Face AE: arraySize:");
      localStringBuilder.append(localRect.width());
      localStringBuilder.append("x");
      localStringBuilder.append(localRect.height());
      Log.d("IFAASecureCamera2API", localStringBuilder.toString());
      float f1 = localRect.width() / 640;
      float f2 = localRect.height() / 480;
      int i = (int)(Math.abs(paramPoint2.y - paramPoint1.y) * f2);
      int j = (int)(Math.abs(paramPoint2.x - paramPoint1.x) * f1);
      paramPoint1.y = paramPoint1.x;
      paramPoint1.x = (640 - paramPoint2.y);
      paramPoint1.x = ((int)(paramPoint1.x * f2));
      paramPoint1.y = ((int)(paramPoint1.y * f1));
      if (paramPoint1.x < 0) {
        paramPoint1.x = 0;
      }
      if (paramPoint1.y < 0) {
        paramPoint1.y = 0;
      }
      if (paramPoint1.x + i > localRect.width() - 1) {
        i = localRect.width() - paramPoint1.x - 1;
      }
      int k = j;
      if (paramPoint1.y + j > localRect.height() - 1) {
        k = localRect.height() - paramPoint1.y - 1;
      }
      paramPoint2 = new StringBuilder();
      paramPoint2.append("Face AE: metering region topLeft=");
      paramPoint2.append(paramPoint1.x);
      paramPoint2.append("x");
      paramPoint2.append(paramPoint1.y);
      paramPoint2.append(" widthxheight=");
      paramPoint2.append(i);
      paramPoint2.append("x");
      paramPoint2.append(k);
      Log.d("IFAASecureCamera2API", paramPoint2.toString());
      paramPoint1 = new MeteringRectangle(paramPoint1.x, paramPoint1.y, i, k, 30);
      this.captureBuilder_.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[] { paramPoint1 });
      try
      {
        this.captureSession_.setRepeatingRequest(this.captureBuilder_.build(), this.captureCallback_, getBackgroundHandler());
      }
      catch (CameraAccessException paramPoint1)
      {
        paramPoint2 = new StringBuilder();
        paramPoint2.append("SecureCamera2API::CameraCaptureSession.StateCallback:onConfigured - Camera #");
        paramPoint2.append(this.cameraId_);
        paramPoint2.append(" - setRepeatingRequest for face AE failed: ");
        paramPoint2.append(paramPoint1);
        Log.d("IFAASecureCamera2API", paramPoint2.toString());
      }
      return;
    }
  }
  
  public Boolean start(int paramInt, SurfaceView paramSurfaceView, Integer paramInteger1, Integer paramInteger2, Integer paramInteger3, Integer paramInteger4, Integer paramInteger5)
  {
    this.previewSurfaceView_ = paramSurfaceView;
    this.previewSurfaceNumOfBuffers_ = paramInteger5;
    paramSurfaceView = new StringBuilder();
    paramSurfaceView.append("SecureCamera2API::start - previewSurfaceView_ = ");
    paramSurfaceView.append(this.previewSurfaceView_);
    Log.d("IFAASecureCamera2API", paramSurfaceView.toString());
    return start(Integer.valueOf(paramInt), paramInteger1, paramInteger2, paramInteger3, paramInteger4);
  }
  
  /* Error */
  public Boolean start(Integer paramInteger1, Integer paramInteger2, Integer paramInteger3, Integer paramInteger4, Integer paramInteger5)
  {
    // Byte code:
    //   0: new 148	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   7: astore 6
    //   9: aload 6
    //   11: ldc_w 365
    //   14: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: pop
    //   18: aload 6
    //   20: aload_0
    //   21: getfield 134	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraId_	Ljava/lang/Integer;
    //   24: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   27: pop
    //   28: ldc 19
    //   30: aload 6
    //   32: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   35: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   38: pop
    //   39: getstatic 291	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   42: astore 6
    //   44: aload_0
    //   45: aload_1
    //   46: putfield 134	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraId_	Ljava/lang/Integer;
    //   49: aload_0
    //   50: aload_2
    //   51: putfield 115	org/ifaa/android/manager/face/IFAASecureCamera2API:width_	Ljava/lang/Integer;
    //   54: aload_0
    //   55: aload_3
    //   56: putfield 117	org/ifaa/android/manager/face/IFAASecureCamera2API:height_	Ljava/lang/Integer;
    //   59: aload_0
    //   60: aload 4
    //   62: putfield 119	org/ifaa/android/manager/face/IFAASecureCamera2API:format_	Ljava/lang/Integer;
    //   65: aload_0
    //   66: aload 5
    //   68: putfield 113	org/ifaa/android/manager/face/IFAASecureCamera2API:numOfBuffers_	Ljava/lang/Integer;
    //   71: aload_0
    //   72: invokevirtual 368	org/ifaa/android/manager/face/IFAASecureCamera2API:startBackgroundThread	()V
    //   75: aload 6
    //   77: astore_2
    //   78: aload 6
    //   80: astore_3
    //   81: getstatic 66	org/ifaa/android/manager/face/IFAASecureCamera2API:mContext	Landroid/content/Context;
    //   84: ldc -39
    //   86: invokevirtual 223	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   89: checkcast 225	android/hardware/camera2/CameraManager
    //   92: astore_1
    //   93: aload_0
    //   94: getfield 101	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraOpenCloseSemaphore_	Ljava/util/concurrent/Semaphore;
    //   97: ldc2_w 369
    //   100: getstatic 376	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   103: invokevirtual 380	java/util/concurrent/Semaphore:tryAcquire	(JLjava/util/concurrent/TimeUnit;)Z
    //   106: ifne +18 -> 124
    //   109: ldc 19
    //   111: ldc_w 382
    //   114: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   117: pop
    //   118: aload 6
    //   120: astore_1
    //   121: goto +120 -> 241
    //   124: new 85	java/util/concurrent/CountDownLatch
    //   127: astore_2
    //   128: aload_2
    //   129: iconst_1
    //   130: invokespecial 88	java/util/concurrent/CountDownLatch:<init>	(I)V
    //   133: aload_0
    //   134: aload_2
    //   135: putfield 92	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraStartedLatch_	Ljava/util/concurrent/CountDownLatch;
    //   138: aload_1
    //   139: aload_0
    //   140: getfield 134	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraId_	Ljava/lang/Integer;
    //   143: invokevirtual 143	java/lang/Integer:intValue	()I
    //   146: invokestatic 228	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   149: aload_0
    //   150: getfield 130	org/ifaa/android/manager/face/IFAASecureCamera2API:mStateCallback	Landroid/hardware/camera2/CameraDevice$StateCallback;
    //   153: aload_0
    //   154: invokevirtual 340	org/ifaa/android/manager/face/IFAASecureCamera2API:getBackgroundHandler	()Landroid/os/Handler;
    //   157: invokevirtual 386	android/hardware/camera2/CameraManager:openCamera	(Ljava/lang/String;Landroid/hardware/camera2/CameraDevice$StateCallback;Landroid/os/Handler;)V
    //   160: aload_0
    //   161: getfield 92	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraStartedLatch_	Ljava/util/concurrent/CountDownLatch;
    //   164: ldc2_w 387
    //   167: getstatic 376	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   170: invokevirtual 391	java/util/concurrent/CountDownLatch:await	(JLjava/util/concurrent/TimeUnit;)Z
    //   173: ifne +18 -> 191
    //   176: ldc 19
    //   178: ldc_w 393
    //   181: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   184: pop
    //   185: aload 6
    //   187: astore_1
    //   188: goto +53 -> 241
    //   191: aload_0
    //   192: getfield 77	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraDevice_	Landroid/hardware/camera2/CameraDevice;
    //   195: ifnonnull +18 -> 213
    //   198: ldc 19
    //   200: ldc_w 395
    //   203: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   206: pop
    //   207: aload 6
    //   209: astore_1
    //   210: goto +31 -> 241
    //   213: ldc 19
    //   215: ldc_w 397
    //   218: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   221: pop
    //   222: invokestatic 403	com/android/qualcomm/qti/seccamapi/SecCamServiceClient:getInstance	()Lcom/android/qualcomm/qti/seccamapi/SecCamServiceClient;
    //   225: sipush 2003
    //   228: aconst_null
    //   229: invokevirtual 407	com/android/qualcomm/qti/seccamapi/SecCamServiceClient:dispatchVendorCommand	(ILandroid/os/Bundle;)V
    //   232: aload_0
    //   233: iconst_1
    //   234: putfield 105	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraStarted_	Z
    //   237: getstatic 286	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   240: astore_1
    //   241: aload_1
    //   242: astore_2
    //   243: aload_1
    //   244: astore_3
    //   245: aload_0
    //   246: getfield 101	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraOpenCloseSemaphore_	Ljava/util/concurrent/Semaphore;
    //   249: invokevirtual 410	java/util/concurrent/Semaphore:release	()V
    //   252: goto +142 -> 394
    //   255: astore_1
    //   256: goto +44 -> 300
    //   259: astore_1
    //   260: ldc 19
    //   262: ldc_w 412
    //   265: aload_1
    //   266: invokestatic 415	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   269: pop
    //   270: new 359	java/lang/RuntimeException
    //   273: astore_1
    //   274: aload_1
    //   275: invokespecial 416	java/lang/RuntimeException:<init>	()V
    //   278: aload_1
    //   279: athrow
    //   280: astore_1
    //   281: ldc 19
    //   283: ldc_w 418
    //   286: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   289: pop
    //   290: new 359	java/lang/RuntimeException
    //   293: astore_1
    //   294: aload_1
    //   295: invokespecial 416	java/lang/RuntimeException:<init>	()V
    //   298: aload_1
    //   299: athrow
    //   300: aload 6
    //   302: astore_2
    //   303: aload 6
    //   305: astore_3
    //   306: aload_0
    //   307: getfield 101	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraOpenCloseSemaphore_	Ljava/util/concurrent/Semaphore;
    //   310: invokevirtual 410	java/util/concurrent/Semaphore:release	()V
    //   313: aload 6
    //   315: astore_2
    //   316: aload 6
    //   318: astore_3
    //   319: aload_1
    //   320: athrow
    //   321: astore_3
    //   322: new 148	java/lang/StringBuilder
    //   325: dup
    //   326: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   329: astore_1
    //   330: aload_1
    //   331: ldc_w 420
    //   334: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   337: pop
    //   338: aload_1
    //   339: aload_3
    //   340: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   343: pop
    //   344: ldc 19
    //   346: aload_1
    //   347: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   350: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   353: pop
    //   354: aload_2
    //   355: astore_1
    //   356: goto +38 -> 394
    //   359: astore_2
    //   360: new 148	java/lang/StringBuilder
    //   363: dup
    //   364: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   367: astore_1
    //   368: aload_1
    //   369: ldc_w 422
    //   372: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   375: pop
    //   376: aload_1
    //   377: aload_2
    //   378: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   381: pop
    //   382: ldc 19
    //   384: aload_1
    //   385: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   388: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   391: pop
    //   392: aload_3
    //   393: astore_1
    //   394: new 148	java/lang/StringBuilder
    //   397: dup
    //   398: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   401: astore_2
    //   402: aload_2
    //   403: ldc_w 424
    //   406: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   409: pop
    //   410: aload_2
    //   411: aload_0
    //   412: getfield 134	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraId_	Ljava/lang/Integer;
    //   415: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   418: pop
    //   419: aload_2
    //   420: ldc_w 426
    //   423: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   426: pop
    //   427: ldc 19
    //   429: aload_2
    //   430: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   433: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   436: pop
    //   437: aload_1
    //   438: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	439	0	this	IFAASecureCamera2API
    //   0	439	1	paramInteger1	Integer
    //   0	439	2	paramInteger2	Integer
    //   0	439	3	paramInteger3	Integer
    //   0	439	4	paramInteger4	Integer
    //   0	439	5	paramInteger5	Integer
    //   7	310	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   93	118	255	finally
    //   124	185	255	finally
    //   191	207	255	finally
    //   213	241	255	finally
    //   260	280	255	finally
    //   281	300	255	finally
    //   93	118	259	java/lang/InterruptedException
    //   124	185	259	java/lang/InterruptedException
    //   191	207	259	java/lang/InterruptedException
    //   213	241	259	java/lang/InterruptedException
    //   93	118	280	android/hardware/camera2/CameraAccessException
    //   124	185	280	android/hardware/camera2/CameraAccessException
    //   191	207	280	android/hardware/camera2/CameraAccessException
    //   213	241	280	android/hardware/camera2/CameraAccessException
    //   81	93	321	java/lang/Exception
    //   245	252	321	java/lang/Exception
    //   306	313	321	java/lang/Exception
    //   319	321	321	java/lang/Exception
    //   81	93	359	java/lang/RuntimeException
    //   245	252	359	java/lang/RuntimeException
    //   306	313	359	java/lang/RuntimeException
    //   319	321	359	java/lang/RuntimeException
  }
  
  protected void startBackgroundThread()
  {
    this.backgroundThread_ = new HandlerThread("CameraBackground");
    this.backgroundThread_.start();
    this.backgroundHandler_ = new Handler(this.backgroundThread_.getLooper());
  }
  
  /* Error */
  public Boolean stop()
  {
    // Byte code:
    //   0: getstatic 291	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   3: astore_1
    //   4: new 148	java/lang/StringBuilder
    //   7: dup
    //   8: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   11: astore_2
    //   12: aload_2
    //   13: ldc_w 449
    //   16: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: pop
    //   20: aload_2
    //   21: aload_0
    //   22: getfield 134	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraId_	Ljava/lang/Integer;
    //   25: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   28: pop
    //   29: ldc 19
    //   31: aload_2
    //   32: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   35: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   38: pop
    //   39: invokestatic 403	com/android/qualcomm/qti/seccamapi/SecCamServiceClient:getInstance	()Lcom/android/qualcomm/qti/seccamapi/SecCamServiceClient;
    //   42: sipush 2004
    //   45: aconst_null
    //   46: invokevirtual 407	com/android/qualcomm/qti/seccamapi/SecCamServiceClient:dispatchVendorCommand	(ILandroid/os/Bundle;)V
    //   49: aload_0
    //   50: getfield 101	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraOpenCloseSemaphore_	Ljava/util/concurrent/Semaphore;
    //   53: ldc2_w 369
    //   56: getstatic 376	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   59: invokevirtual 380	java/util/concurrent/Semaphore:tryAcquire	(JLjava/util/concurrent/TimeUnit;)Z
    //   62: ifne +12 -> 74
    //   65: ldc 19
    //   67: ldc_w 451
    //   70: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   73: pop
    //   74: aload_0
    //   75: getfield 79	org/ifaa/android/manager/face/IFAASecureCamera2API:captureSession_	Landroid/hardware/camera2/CameraCaptureSession;
    //   78: ifnull +97 -> 175
    //   81: new 148	java/lang/StringBuilder
    //   84: astore_2
    //   85: aload_2
    //   86: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   89: aload_2
    //   90: ldc_w 453
    //   93: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: aload_2
    //   98: aload_0
    //   99: getfield 134	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraId_	Ljava/lang/Integer;
    //   102: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   105: pop
    //   106: aload_2
    //   107: ldc_w 455
    //   110: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: pop
    //   114: ldc 19
    //   116: aload_2
    //   117: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   120: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   123: pop
    //   124: new 85	java/util/concurrent/CountDownLatch
    //   127: astore_2
    //   128: aload_2
    //   129: iconst_1
    //   130: invokespecial 88	java/util/concurrent/CountDownLatch:<init>	(I)V
    //   133: aload_0
    //   134: aload_2
    //   135: putfield 96	org/ifaa/android/manager/face/IFAASecureCamera2API:captureSessionClosedLatch_	Ljava/util/concurrent/CountDownLatch;
    //   138: aload_0
    //   139: getfield 79	org/ifaa/android/manager/face/IFAASecureCamera2API:captureSession_	Landroid/hardware/camera2/CameraCaptureSession;
    //   142: invokevirtual 458	android/hardware/camera2/CameraCaptureSession:close	()V
    //   145: aload_0
    //   146: getfield 96	org/ifaa/android/manager/face/IFAASecureCamera2API:captureSessionClosedLatch_	Ljava/util/concurrent/CountDownLatch;
    //   149: ldc2_w 459
    //   152: getstatic 376	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   155: invokevirtual 391	java/util/concurrent/CountDownLatch:await	(JLjava/util/concurrent/TimeUnit;)Z
    //   158: ifne +12 -> 170
    //   161: ldc 19
    //   163: ldc_w 462
    //   166: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   169: pop
    //   170: aload_0
    //   171: aconst_null
    //   172: putfield 79	org/ifaa/android/manager/face/IFAASecureCamera2API:captureSession_	Landroid/hardware/camera2/CameraCaptureSession;
    //   175: aload_0
    //   176: getfield 77	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraDevice_	Landroid/hardware/camera2/CameraDevice;
    //   179: ifnull +63 -> 242
    //   182: ldc 19
    //   184: ldc_w 464
    //   187: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   190: pop
    //   191: new 85	java/util/concurrent/CountDownLatch
    //   194: astore_2
    //   195: aload_2
    //   196: iconst_1
    //   197: invokespecial 88	java/util/concurrent/CountDownLatch:<init>	(I)V
    //   200: aload_0
    //   201: aload_2
    //   202: putfield 94	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraStopedLatch_	Ljava/util/concurrent/CountDownLatch;
    //   205: aload_0
    //   206: getfield 77	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraDevice_	Landroid/hardware/camera2/CameraDevice;
    //   209: invokevirtual 465	android/hardware/camera2/CameraDevice:close	()V
    //   212: aload_0
    //   213: getfield 94	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraStopedLatch_	Ljava/util/concurrent/CountDownLatch;
    //   216: ldc2_w 459
    //   219: getstatic 376	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   222: invokevirtual 391	java/util/concurrent/CountDownLatch:await	(JLjava/util/concurrent/TimeUnit;)Z
    //   225: ifne +12 -> 237
    //   228: ldc 19
    //   230: ldc_w 467
    //   233: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   236: pop
    //   237: aload_0
    //   238: aconst_null
    //   239: putfield 77	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraDevice_	Landroid/hardware/camera2/CameraDevice;
    //   242: getstatic 286	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   245: astore_2
    //   246: aload_2
    //   247: astore_1
    //   248: goto +18 -> 266
    //   251: astore_1
    //   252: goto +104 -> 356
    //   255: astore_2
    //   256: ldc 19
    //   258: ldc_w 469
    //   261: aload_2
    //   262: invokestatic 415	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   265: pop
    //   266: aload_0
    //   267: iconst_0
    //   268: putfield 105	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraStarted_	Z
    //   271: aload_0
    //   272: invokevirtual 472	org/ifaa/android/manager/face/IFAASecureCamera2API:stopBackgroundThread	()V
    //   275: aload_0
    //   276: getfield 101	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraOpenCloseSemaphore_	Ljava/util/concurrent/Semaphore;
    //   279: invokevirtual 410	java/util/concurrent/Semaphore:release	()V
    //   282: aload_0
    //   283: getfield 103	org/ifaa/android/manager/face/IFAASecureCamera2API:secureCamera2Surface_	Lcom/android/qualcomm/qti/seccamapi/SecureCamera2Surface;
    //   286: ifnull +25 -> 311
    //   289: ldc 19
    //   291: ldc_w 474
    //   294: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   297: pop
    //   298: aload_0
    //   299: getfield 103	org/ifaa/android/manager/face/IFAASecureCamera2API:secureCamera2Surface_	Lcom/android/qualcomm/qti/seccamapi/SecureCamera2Surface;
    //   302: invokevirtual 477	com/android/qualcomm/qti/seccamapi/SecureCamera2Surface:release	()Z
    //   305: pop
    //   306: aload_0
    //   307: aconst_null
    //   308: putfield 103	org/ifaa/android/manager/face/IFAASecureCamera2API:secureCamera2Surface_	Lcom/android/qualcomm/qti/seccamapi/SecureCamera2Surface;
    //   311: new 148	java/lang/StringBuilder
    //   314: dup
    //   315: invokespecial 149	java/lang/StringBuilder:<init>	()V
    //   318: astore_2
    //   319: aload_2
    //   320: ldc_w 479
    //   323: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: pop
    //   327: aload_2
    //   328: aload_0
    //   329: getfield 134	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraId_	Ljava/lang/Integer;
    //   332: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   335: pop
    //   336: aload_2
    //   337: ldc_w 481
    //   340: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   343: pop
    //   344: ldc 19
    //   346: aload_2
    //   347: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   350: invokestatic 168	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   353: pop
    //   354: aload_1
    //   355: areturn
    //   356: aload_0
    //   357: iconst_0
    //   358: putfield 105	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraStarted_	Z
    //   361: aload_0
    //   362: invokevirtual 472	org/ifaa/android/manager/face/IFAASecureCamera2API:stopBackgroundThread	()V
    //   365: aload_0
    //   366: getfield 101	org/ifaa/android/manager/face/IFAASecureCamera2API:cameraOpenCloseSemaphore_	Ljava/util/concurrent/Semaphore;
    //   369: invokevirtual 410	java/util/concurrent/Semaphore:release	()V
    //   372: aload_1
    //   373: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	374	0	this	IFAASecureCamera2API
    //   3	245	1	localObject1	Object
    //   251	122	1	localBoolean	Boolean
    //   11	236	2	localObject2	Object
    //   255	7	2	localInterruptedException	InterruptedException
    //   318	29	2	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   49	74	251	finally
    //   74	170	251	finally
    //   170	175	251	finally
    //   175	237	251	finally
    //   237	242	251	finally
    //   242	246	251	finally
    //   256	266	251	finally
    //   49	74	255	java/lang/InterruptedException
    //   74	170	255	java/lang/InterruptedException
    //   170	175	255	java/lang/InterruptedException
    //   175	237	255	java/lang/InterruptedException
    //   237	242	255	java/lang/InterruptedException
    //   242	246	255	java/lang/InterruptedException
  }
  
  protected void stopBackgroundThread()
  {
    HandlerThread localHandlerThread = this.backgroundThread_;
    if (localHandlerThread != null)
    {
      localHandlerThread.quitSafely();
      try
      {
        this.backgroundThread_.join(2000L);
        this.backgroundThread_ = null;
        this.backgroundHandler_ = null;
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/ifaa/android/manager/face/IFAASecureCamera2API.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */