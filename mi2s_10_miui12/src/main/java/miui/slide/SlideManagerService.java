package miui.slide;

import android.content.Context;
import android.os.Binder;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SlideManagerService
  extends ISlideManagerService.Stub
{
  public static final String SERVICE_NAME = "miui.slide.SlideManagerService";
  private static final String TAG = "SlideManager";
  private Context mContext;
  
  public SlideManagerService(Context paramContext)
  {
    this.mContext = paramContext;
    SlideCloudConfigHelper.getInstance().initConfig(this.mContext);
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramFileDescriptor = new StringBuilder();
      paramFileDescriptor.append("Permission Denial: can't dump miui.slide.SlideManagerService from pid=");
      paramFileDescriptor.append(Binder.getCallingPid());
      paramFileDescriptor.append(", uid=");
      paramFileDescriptor.append(Binder.getCallingUid());
      paramFileDescriptor.append(" due to missing android.permission.DUMP permission");
      paramPrintWriter.println(paramFileDescriptor.toString());
      return;
    }
    SlideCoverEventManager.getInstance().dump("    ", paramPrintWriter, paramArrayOfString);
  }
  
  public AppSlideConfig getAppSlideConfig(String paramString, int paramInt)
  {
    paramString = SlideCloudConfigHelper.getInstance().getAppSlideConfigs(paramString);
    if (paramString == null) {
      paramString = new AppSlideConfig();
    }
    return paramString;
  }
  
  public int getCameraStatus()
  {
    long l = Binder.clearCallingIdentity();
    int i = 0;
    if (SlideCameraMonitor.getInstance().isFrontCameraOpening()) {
      i = 0x0 | 0x1;
    }
    int j = i;
    if (SlideCameraMonitor.getInstance().isBackCameraOpening()) {
      j = i | 0x2;
    }
    i = j;
    if (SlideCameraMonitor.getInstance().isCameraRecording()) {
      i = j | 0x4;
    }
    Binder.restoreCallingIdentity(l);
    return i;
  }
  
  public void registerSlideChangeListener(String paramString, ISlideChangeListener paramISlideChangeListener)
  {
    if ((paramString != null) && (paramString.length() != 0) && (paramISlideChangeListener != null))
    {
      SlideCoverEventManager.getInstance().registerSlideChangeListener(paramString, paramISlideChangeListener);
      return;
    }
  }
  
  public void unregisterSlideChangeListener(ISlideChangeListener paramISlideChangeListener)
  {
    if (paramISlideChangeListener == null) {
      return;
    }
    SlideCoverEventManager.getInstance().unregisterSlideChangeListener(paramISlideChangeListener);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/slide/SlideManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */