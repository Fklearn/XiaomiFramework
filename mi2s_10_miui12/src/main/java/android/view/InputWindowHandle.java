package android.view;

import android.graphics.Region;
import android.os.IBinder;
import java.lang.ref.WeakReference;

public final class InputWindowHandle
{
  public boolean canReceiveKeys;
  public final IWindow clientWindow;
  public long dispatchingTimeoutNanos;
  public int displayId;
  public int frameBottom;
  public int frameLeft;
  public int frameRight;
  public int frameTop;
  public boolean hasFocus;
  public boolean hasWallpaper;
  public final InputApplicationHandle inputApplicationHandle;
  public int inputFeatures;
  public int layer;
  public int layoutParamsFlags;
  public int layoutParamsType;
  public String name;
  public int ownerPid;
  public int ownerUid;
  public boolean paused;
  public int portalToDisplayId = -1;
  private long ptr;
  public boolean replaceTouchableRegionWithCrop;
  public float scaleFactor;
  public int surfaceInset;
  public IBinder token;
  public final Region touchableRegion = new Region();
  public WeakReference<IBinder> touchableRegionCropHandle = new WeakReference(null);
  public boolean visible;
  
  public InputWindowHandle(InputApplicationHandle paramInputApplicationHandle, IWindow paramIWindow, int paramInt)
  {
    this.inputApplicationHandle = paramInputApplicationHandle;
    this.clientWindow = paramIWindow;
    this.displayId = paramInt;
  }
  
  private native void nativeDispose();
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDispose();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void replaceTouchableRegionWithCrop(SurfaceControl paramSurfaceControl)
  {
    setTouchableRegionCrop(paramSurfaceControl);
    this.replaceTouchableRegionWithCrop = true;
  }
  
  public void setTouchableRegionCrop(SurfaceControl paramSurfaceControl)
  {
    if (paramSurfaceControl != null) {
      this.touchableRegionCropHandle = new WeakReference(paramSurfaceControl.getHandle());
    }
  }
  
  public String toString()
  {
    Object localObject = this.name;
    if (localObject == null) {
      localObject = "";
    }
    localObject = new StringBuilder((String)localObject);
    ((StringBuilder)localObject).append(", layer=");
    ((StringBuilder)localObject).append(this.layer);
    ((StringBuilder)localObject).append(", frame=[");
    ((StringBuilder)localObject).append(this.frameLeft);
    ((StringBuilder)localObject).append(",");
    ((StringBuilder)localObject).append(this.frameTop);
    ((StringBuilder)localObject).append(",");
    ((StringBuilder)localObject).append(this.frameRight);
    ((StringBuilder)localObject).append(",");
    ((StringBuilder)localObject).append(this.frameBottom);
    ((StringBuilder)localObject).append("]");
    ((StringBuilder)localObject).append(", touchableRegion=");
    ((StringBuilder)localObject).append(this.touchableRegion);
    ((StringBuilder)localObject).append(", visible=");
    ((StringBuilder)localObject).append(this.visible);
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputWindowHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */