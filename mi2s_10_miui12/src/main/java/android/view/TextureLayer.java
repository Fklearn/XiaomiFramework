package android.view;

import android.graphics.Bitmap;
import android.graphics.HardwareRenderer;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import com.android.internal.util.VirtualRefBasePtr;

public final class TextureLayer
{
  private VirtualRefBasePtr mFinalizer;
  private HardwareRenderer mRenderer;
  
  private TextureLayer(HardwareRenderer paramHardwareRenderer, long paramLong)
  {
    if ((paramHardwareRenderer != null) && (paramLong != 0L))
    {
      this.mRenderer = paramHardwareRenderer;
      this.mFinalizer = new VirtualRefBasePtr(paramLong);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Either hardware renderer: ");
    localStringBuilder.append(paramHardwareRenderer);
    localStringBuilder.append(" or deferredUpdater: ");
    localStringBuilder.append(paramLong);
    localStringBuilder.append(" is invalid");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public static TextureLayer adoptTextureLayer(HardwareRenderer paramHardwareRenderer, long paramLong)
  {
    return new TextureLayer(paramHardwareRenderer, paramLong);
  }
  
  private static native boolean nPrepare(long paramLong, int paramInt1, int paramInt2, boolean paramBoolean);
  
  private static native void nSetLayerPaint(long paramLong1, long paramLong2);
  
  private static native void nSetSurfaceTexture(long paramLong, SurfaceTexture paramSurfaceTexture);
  
  private static native void nSetTransform(long paramLong1, long paramLong2);
  
  private static native void nUpdateSurfaceTexture(long paramLong);
  
  public boolean copyInto(Bitmap paramBitmap)
  {
    return this.mRenderer.copyLayerInto(this, paramBitmap);
  }
  
  public void destroy()
  {
    if (!isValid()) {
      return;
    }
    this.mRenderer.onLayerDestroyed(this);
    this.mRenderer = null;
    this.mFinalizer.release();
    this.mFinalizer = null;
  }
  
  public void detachSurfaceTexture()
  {
    this.mRenderer.detachSurfaceTexture(this.mFinalizer.get());
  }
  
  public long getDeferredLayerUpdater()
  {
    return this.mFinalizer.get();
  }
  
  public long getLayerHandle()
  {
    return this.mFinalizer.get();
  }
  
  public boolean isValid()
  {
    VirtualRefBasePtr localVirtualRefBasePtr = this.mFinalizer;
    boolean bool;
    if ((localVirtualRefBasePtr != null) && (localVirtualRefBasePtr.get() != 0L)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean prepare(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return nPrepare(this.mFinalizer.get(), paramInt1, paramInt2, paramBoolean);
  }
  
  public void setLayerPaint(Paint paramPaint)
  {
    long l1 = this.mFinalizer.get();
    long l2;
    if (paramPaint != null) {
      l2 = paramPaint.getNativeInstance();
    } else {
      l2 = 0L;
    }
    nSetLayerPaint(l1, l2);
    this.mRenderer.pushLayerUpdate(this);
  }
  
  public void setSurfaceTexture(SurfaceTexture paramSurfaceTexture)
  {
    nSetSurfaceTexture(this.mFinalizer.get(), paramSurfaceTexture);
    this.mRenderer.pushLayerUpdate(this);
  }
  
  public void setTransform(Matrix paramMatrix)
  {
    nSetTransform(this.mFinalizer.get(), paramMatrix.native_instance);
    this.mRenderer.pushLayerUpdate(this);
  }
  
  public void updateSurfaceTexture()
  {
    nUpdateSurfaceTexture(this.mFinalizer.get());
    this.mRenderer.pushLayerUpdate(this);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/TextureLayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */