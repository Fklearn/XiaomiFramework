package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

public class TextureView
  extends View
{
  private static final String LOG_TAG = "TextureView";
  private Canvas mCanvas;
  private boolean mHadSurface;
  @UnsupportedAppUsage
  private TextureLayer mLayer;
  private SurfaceTextureListener mListener;
  private final Object[] mLock = new Object[0];
  private final Matrix mMatrix = new Matrix();
  private boolean mMatrixChanged;
  @UnsupportedAppUsage
  private long mNativeWindow;
  private final Object[] mNativeWindowLock = new Object[0];
  @UnsupportedAppUsage
  private boolean mOpaque = true;
  private int mSaveCount;
  @UnsupportedAppUsage
  private SurfaceTexture mSurface;
  private boolean mUpdateLayer;
  @UnsupportedAppUsage
  private final SurfaceTexture.OnFrameAvailableListener mUpdateListener = new SurfaceTexture.OnFrameAvailableListener()
  {
    public void onFrameAvailable(SurfaceTexture paramAnonymousSurfaceTexture)
    {
      TextureView.this.updateLayer();
      TextureView.this.invalidate();
    }
  };
  @UnsupportedAppUsage
  private boolean mUpdateSurface;
  
  public TextureView(Context paramContext)
  {
    super(paramContext);
  }
  
  public TextureView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public TextureView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public TextureView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  private void applyTransformMatrix()
  {
    if (this.mMatrixChanged)
    {
      TextureLayer localTextureLayer = this.mLayer;
      if (localTextureLayer != null)
      {
        localTextureLayer.setTransform(this.mMatrix);
        this.mMatrixChanged = false;
      }
    }
  }
  
  private void applyUpdate()
  {
    if (this.mLayer == null) {
      return;
    }
    synchronized (this.mLock)
    {
      if (this.mUpdateLayer)
      {
        this.mUpdateLayer = false;
        this.mLayer.prepare(getWidth(), getHeight(), this.mOpaque);
        this.mLayer.updateSurfaceTexture();
        ??? = this.mListener;
        if (??? != null) {
          ((SurfaceTextureListener)???).onSurfaceTextureUpdated(this.mSurface);
        }
        return;
      }
      return;
    }
  }
  
  @UnsupportedAppUsage
  private void destroyHardwareLayer()
  {
    TextureLayer localTextureLayer = this.mLayer;
    if (localTextureLayer != null)
    {
      localTextureLayer.detachSurfaceTexture();
      this.mLayer.destroy();
      this.mLayer = null;
      this.mMatrixChanged = true;
    }
  }
  
  @UnsupportedAppUsage
  private native void nCreateNativeWindow(SurfaceTexture paramSurfaceTexture);
  
  @UnsupportedAppUsage
  private native void nDestroyNativeWindow();
  
  private static native boolean nLockCanvas(long paramLong, Canvas paramCanvas, Rect paramRect);
  
  private static native void nUnlockCanvasAndPost(long paramLong, Canvas paramCanvas);
  
  private void releaseSurfaceTexture()
  {
    ??? = this.mSurface;
    if (??? != null)
    {
      boolean bool = true;
      SurfaceTextureListener localSurfaceTextureListener = this.mListener;
      if (localSurfaceTextureListener != null) {
        bool = localSurfaceTextureListener.onSurfaceTextureDestroyed((SurfaceTexture)???);
      }
      synchronized (this.mNativeWindowLock)
      {
        nDestroyNativeWindow();
        if (bool) {
          this.mSurface.release();
        }
        this.mSurface = null;
        this.mHadSurface = true;
      }
    }
  }
  
  private void updateLayer()
  {
    synchronized (this.mLock)
    {
      this.mUpdateLayer = true;
      return;
    }
  }
  
  private void updateLayerAndInvalidate()
  {
    synchronized (this.mLock)
    {
      this.mUpdateLayer = true;
      invalidate();
      return;
    }
  }
  
  public void buildLayer() {}
  
  @UnsupportedAppUsage
  protected void destroyHardwareResources()
  {
    super.destroyHardwareResources();
    destroyHardwareLayer();
  }
  
  public final void draw(Canvas paramCanvas)
  {
    this.mPrivateFlags = (this.mPrivateFlags & 0xFFDFFFFF | 0x20);
    if (paramCanvas.isHardwareAccelerated())
    {
      RecordingCanvas localRecordingCanvas = (RecordingCanvas)paramCanvas;
      paramCanvas = getTextureLayer();
      if (paramCanvas != null)
      {
        applyUpdate();
        applyTransformMatrix();
        this.mLayer.setLayerPaint(this.mLayerPaint);
        localRecordingCanvas.drawTextureLayer(paramCanvas);
      }
    }
  }
  
  public Bitmap getBitmap()
  {
    return getBitmap(getWidth(), getHeight());
  }
  
  public Bitmap getBitmap(int paramInt1, int paramInt2)
  {
    if ((isAvailable()) && (paramInt1 > 0) && (paramInt2 > 0)) {
      return getBitmap(Bitmap.createBitmap(getResources().getDisplayMetrics(), paramInt1, paramInt2, Bitmap.Config.ARGB_8888));
    }
    return null;
  }
  
  public Bitmap getBitmap(Bitmap paramBitmap)
  {
    if ((paramBitmap != null) && (isAvailable()))
    {
      applyUpdate();
      applyTransformMatrix();
      if ((this.mLayer == null) && (this.mUpdateSurface)) {
        getTextureLayer();
      }
      TextureLayer localTextureLayer = this.mLayer;
      if (localTextureLayer != null) {
        localTextureLayer.copyInto(paramBitmap);
      }
    }
    return paramBitmap;
  }
  
  public int getLayerType()
  {
    return 2;
  }
  
  public SurfaceTexture getSurfaceTexture()
  {
    return this.mSurface;
  }
  
  public SurfaceTextureListener getSurfaceTextureListener()
  {
    return this.mListener;
  }
  
  TextureLayer getTextureLayer()
  {
    if (this.mLayer == null) {
      if ((this.mAttachInfo != null) && (this.mAttachInfo.mThreadedRenderer != null))
      {
        this.mLayer = this.mAttachInfo.mThreadedRenderer.createTextureLayer();
        int i;
        if (this.mSurface == null) {
          i = 1;
        } else {
          i = 0;
        }
        if (i != 0)
        {
          this.mSurface = new SurfaceTexture(false);
          nCreateNativeWindow(this.mSurface);
        }
        this.mLayer.setSurfaceTexture(this.mSurface);
        this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
        this.mSurface.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
        SurfaceTextureListener localSurfaceTextureListener = this.mListener;
        if ((localSurfaceTextureListener != null) && (i != 0)) {
          localSurfaceTextureListener.onSurfaceTextureAvailable(this.mSurface, getWidth(), getHeight());
        }
        this.mLayer.setLayerPaint(this.mLayerPaint);
      }
      else
      {
        return null;
      }
    }
    if (this.mUpdateSurface)
    {
      this.mUpdateSurface = false;
      updateLayer();
      this.mMatrixChanged = true;
      this.mLayer.setSurfaceTexture(this.mSurface);
      this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
    }
    return this.mLayer;
  }
  
  public Matrix getTransform(Matrix paramMatrix)
  {
    Matrix localMatrix = paramMatrix;
    if (paramMatrix == null) {
      localMatrix = new Matrix();
    }
    localMatrix.set(this.mMatrix);
    return localMatrix;
  }
  
  public boolean isAvailable()
  {
    boolean bool;
    if (this.mSurface != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isOpaque()
  {
    return this.mOpaque;
  }
  
  public Canvas lockCanvas()
  {
    return lockCanvas(null);
  }
  
  public Canvas lockCanvas(Rect paramRect)
  {
    if (!isAvailable()) {
      return null;
    }
    if (this.mCanvas == null) {
      this.mCanvas = new Canvas();
    }
    synchronized (this.mNativeWindowLock)
    {
      if (!nLockCanvas(this.mNativeWindow, this.mCanvas, paramRect)) {
        return null;
      }
      this.mSaveCount = this.mCanvas.save();
      return this.mCanvas;
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!isHardwareAccelerated()) {
      Log.w("TextureView", "A TextureView or a subclass can only be used with hardware acceleration enabled.");
    }
    if (this.mHadSurface)
    {
      invalidate(true);
      this.mHadSurface = false;
    }
  }
  
  @UnsupportedAppUsage
  protected void onDetachedFromWindowInternal()
  {
    destroyHardwareLayer();
    releaseSurfaceTexture();
    super.onDetachedFromWindowInternal();
  }
  
  protected final void onDraw(Canvas paramCanvas) {}
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    Object localObject = this.mSurface;
    if (localObject != null)
    {
      ((SurfaceTexture)localObject).setDefaultBufferSize(getWidth(), getHeight());
      updateLayer();
      localObject = this.mListener;
      if (localObject != null) {
        ((SurfaceTextureListener)localObject).onSurfaceTextureSizeChanged(this.mSurface, getWidth(), getHeight());
      }
    }
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    paramView = this.mSurface;
    if (paramView != null) {
      if (paramInt == 0)
      {
        if (this.mLayer != null) {
          paramView.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
        }
        updateLayerAndInvalidate();
      }
      else
      {
        paramView.setOnFrameAvailableListener(null);
      }
    }
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    if ((paramDrawable != null) && (!sTextureViewIgnoresDrawableSetters)) {
      throw new UnsupportedOperationException("TextureView doesn't support displaying a background drawable");
    }
  }
  
  public void setForeground(Drawable paramDrawable)
  {
    if ((paramDrawable != null) && (!sTextureViewIgnoresDrawableSetters)) {
      throw new UnsupportedOperationException("TextureView doesn't support displaying a foreground drawable");
    }
  }
  
  public void setLayerPaint(Paint paramPaint)
  {
    if (paramPaint != this.mLayerPaint)
    {
      this.mLayerPaint = paramPaint;
      invalidate();
    }
  }
  
  public void setLayerType(int paramInt, Paint paramPaint)
  {
    setLayerPaint(paramPaint);
  }
  
  public void setOpaque(boolean paramBoolean)
  {
    if (paramBoolean != this.mOpaque)
    {
      this.mOpaque = paramBoolean;
      if (this.mLayer != null) {
        updateLayerAndInvalidate();
      }
    }
  }
  
  public void setSurfaceTexture(SurfaceTexture paramSurfaceTexture)
  {
    if (paramSurfaceTexture != null)
    {
      if (paramSurfaceTexture != this.mSurface)
      {
        if (!paramSurfaceTexture.isReleased())
        {
          if (this.mSurface != null)
          {
            nDestroyNativeWindow();
            this.mSurface.release();
          }
          this.mSurface = paramSurfaceTexture;
          nCreateNativeWindow(this.mSurface);
          if (((this.mViewFlags & 0xC) == 0) && (this.mLayer != null)) {
            this.mSurface.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
          }
          this.mUpdateSurface = true;
          invalidateParentIfNeeded();
          return;
        }
        throw new IllegalArgumentException("Cannot setSurfaceTexture to a released SurfaceTexture");
      }
      throw new IllegalArgumentException("Trying to setSurfaceTexture to the same SurfaceTexture that's already set.");
    }
    throw new NullPointerException("surfaceTexture must not be null");
  }
  
  public void setSurfaceTextureListener(SurfaceTextureListener paramSurfaceTextureListener)
  {
    this.mListener = paramSurfaceTextureListener;
  }
  
  public void setTransform(Matrix paramMatrix)
  {
    this.mMatrix.set(paramMatrix);
    this.mMatrixChanged = true;
    invalidateParentIfNeeded();
  }
  
  public void unlockCanvasAndPost(Canvas arg1)
  {
    Canvas localCanvas = this.mCanvas;
    if ((localCanvas != null) && (??? == localCanvas))
    {
      ???.restoreToCount(this.mSaveCount);
      this.mSaveCount = 0;
      synchronized (this.mNativeWindowLock)
      {
        nUnlockCanvasAndPost(this.mNativeWindow, this.mCanvas);
      }
    }
  }
  
  public static abstract interface SurfaceTextureListener
  {
    public abstract void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2);
    
    public abstract boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture);
    
    public abstract void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2);
    
    public abstract void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/TextureView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */