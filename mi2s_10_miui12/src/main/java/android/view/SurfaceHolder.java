package android.view;

import android.graphics.Canvas;
import android.graphics.Rect;

public abstract interface SurfaceHolder
{
  @Deprecated
  public static final int SURFACE_TYPE_GPU = 2;
  @Deprecated
  public static final int SURFACE_TYPE_HARDWARE = 1;
  @Deprecated
  public static final int SURFACE_TYPE_NORMAL = 0;
  @Deprecated
  public static final int SURFACE_TYPE_PUSH_BUFFERS = 3;
  
  public abstract void addCallback(Callback paramCallback);
  
  public abstract Surface getSurface();
  
  public abstract Rect getSurfaceFrame();
  
  public abstract boolean isCreating();
  
  public abstract Canvas lockCanvas();
  
  public abstract Canvas lockCanvas(Rect paramRect);
  
  public Canvas lockHardwareCanvas()
  {
    throw new IllegalStateException("This SurfaceHolder doesn't support lockHardwareCanvas");
  }
  
  public abstract void removeCallback(Callback paramCallback);
  
  public abstract void setFixedSize(int paramInt1, int paramInt2);
  
  public abstract void setFormat(int paramInt);
  
  public abstract void setKeepScreenOn(boolean paramBoolean);
  
  public abstract void setSizeFromLayout();
  
  @Deprecated
  public abstract void setType(int paramInt);
  
  public abstract void unlockCanvasAndPost(Canvas paramCanvas);
  
  public static class BadSurfaceTypeException
    extends RuntimeException
  {
    public BadSurfaceTypeException() {}
    
    public BadSurfaceTypeException(String paramString)
    {
      super();
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void surfaceCreated(SurfaceHolder paramSurfaceHolder);
    
    public abstract void surfaceDestroyed(SurfaceHolder paramSurfaceHolder);
  }
  
  public static abstract interface Callback2
    extends SurfaceHolder.Callback
  {
    public abstract void surfaceRedrawNeeded(SurfaceHolder paramSurfaceHolder);
    
    public void surfaceRedrawNeededAsync(SurfaceHolder paramSurfaceHolder, Runnable paramRunnable)
    {
      surfaceRedrawNeeded(paramSurfaceHolder);
      paramRunnable.run();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/SurfaceHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */