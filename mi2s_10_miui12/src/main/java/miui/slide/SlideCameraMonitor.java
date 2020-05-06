package miui.slide;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraManager.AvailabilityCallback;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Slog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SlideCameraMonitor
{
  private static String TAG = "SlideCameraMonitor";
  private static final int VIRTUAL_CAMERA_BOUNDARY = 100;
  private static volatile SlideCameraMonitor sInstance;
  private CameraManager.AvailabilityCallback mAvailabilityCallback = new CameraManager.AvailabilityCallback()
  {
    public void onCameraAvailable(String paramAnonymousString)
    {
      super.onCameraAvailable(paramAnonymousString);
      int i = Integer.valueOf(paramAnonymousString).intValue();
      if (i >= 100) {
        return;
      }
      String str = SlideCameraMonitor.TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("onCameraAvailable:");
      localStringBuilder.append(paramAnonymousString);
      Slog.d(str, localStringBuilder.toString());
      SlideCameraMonitor.this.mOpeningCameraID.remove(Integer.valueOf(i));
      if (SlideCameraMonitor.this.mCameraOpenListener != null) {
        SlideCameraMonitor.this.mCameraOpenListener.onCameraClose(i);
      }
    }
    
    public void onCameraUnavailable(String paramAnonymousString)
    {
      super.onCameraUnavailable(paramAnonymousString);
      int i = Integer.valueOf(paramAnonymousString).intValue();
      if (i >= 100) {
        return;
      }
      String str = SlideCameraMonitor.TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("onCameraUnavailable:");
      localStringBuilder.append(paramAnonymousString);
      Slog.d(str, localStringBuilder.toString());
      SlideCameraMonitor.this.mOpeningCameraID.add(Integer.valueOf(i));
      if (SlideCameraMonitor.this.mCameraOpenListener != null) {
        SlideCameraMonitor.this.mCameraOpenListener.onCameraOpen(i);
      }
    }
  };
  private List<Integer> mBackCameraID = new ArrayList();
  private CameraManager mCameraManager;
  private CameraOpenListener mCameraOpenListener;
  private Context mContext;
  private List<Integer> mFrontCameraID = new ArrayList();
  private Handler mHandler;
  private Set mOpeningCameraID = new HashSet();
  
  public static SlideCameraMonitor getInstance()
  {
    if (sInstance == null) {
      try
      {
        if (sInstance == null)
        {
          SlideCameraMonitor localSlideCameraMonitor = new miui/slide/SlideCameraMonitor;
          localSlideCameraMonitor.<init>();
          sInstance = localSlideCameraMonitor;
        }
      }
      finally {}
    }
    return sInstance;
  }
  
  private void initCameraId()
  {
    try
    {
      String[] arrayOfString = this.mCameraManager.getCameraIdList();
      if ((arrayOfString != null) && (arrayOfString.length > 0))
      {
        int i = arrayOfString.length;
        for (int j = 0; j < i; j++)
        {
          String str = arrayOfString[j];
          int k = Integer.valueOf(str).intValue();
          if (k < 100)
          {
            int m = ((Integer)this.mCameraManager.getCameraCharacteristics(str).get(CameraCharacteristics.LENS_FACING)).intValue();
            if (m == 0) {
              this.mFrontCameraID.add(Integer.valueOf(k));
            } else if (m == 1) {
              this.mBackCameraID.add(Integer.valueOf(k));
            }
          }
        }
      }
    }
    catch (Exception localException)
    {
      Slog.d(TAG, "Can't initCameraId");
    }
  }
  
  public List<Integer> getBackCameraID()
  {
    return this.mBackCameraID;
  }
  
  public List<Integer> getFrontCameraID()
  {
    return this.mFrontCameraID;
  }
  
  public void init(Context paramContext, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mHandler = new H(paramLooper);
    this.mCameraManager = ((CameraManager)this.mContext.getSystemService("camera"));
    this.mCameraManager.registerAvailabilityCallback(this.mAvailabilityCallback, this.mHandler);
    initCameraId();
  }
  
  public boolean isBackCameraOpening()
  {
    Object localObject = this.mBackCameraID;
    if ((localObject != null) && (((List)localObject).size() != 0))
    {
      localObject = this.mBackCameraID.iterator();
      while (((Iterator)localObject).hasNext())
      {
        int i = ((Integer)((Iterator)localObject).next()).intValue();
        if (this.mOpeningCameraID.contains(Integer.valueOf(i))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean isCameraOpening()
  {
    boolean bool;
    if ((!isFrontCameraOpening()) && (!isBackCameraOpening())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isCameraRecording()
  {
    List localList = ((AudioManager)this.mContext.getSystemService("audio")).getActiveRecordingConfigurations();
    if ((localList != null) && (localList.size() > 0))
    {
      Slog.d(TAG, "recording");
      return true;
    }
    return false;
  }
  
  public boolean isFrontCameraOpening()
  {
    Object localObject = this.mFrontCameraID;
    if ((localObject != null) && (((List)localObject).size() != 0))
    {
      localObject = this.mFrontCameraID.iterator();
      while (((Iterator)localObject).hasNext())
      {
        int i = ((Integer)((Iterator)localObject).next()).intValue();
        if (this.mOpeningCameraID.contains(Integer.valueOf(i))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void setCameraOpenListener(CameraOpenListener paramCameraOpenListener)
  {
    this.mCameraOpenListener = paramCameraOpenListener;
  }
  
  public static abstract interface CameraOpenListener
  {
    public abstract void onCameraClose(int paramInt);
    
    public abstract void onCameraOpen(int paramInt);
  }
  
  private class H
    extends Handler
  {
    public H(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      super.handleMessage(paramMessage);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideCameraMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */