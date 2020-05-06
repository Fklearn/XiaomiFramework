package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.res.CompatibilityInfo.Translator;
import android.graphics.Canvas;
import android.graphics.GraphicBuffer;
import android.graphics.Matrix;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.graphics.SurfaceTexture;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Surface
  implements Parcelable
{
  public static final Parcelable.Creator<Surface> CREATOR = new Parcelable.Creator()
  {
    public Surface createFromParcel(Parcel paramAnonymousParcel)
    {
      try
      {
        Surface localSurface = new android/view/Surface;
        localSurface.<init>();
        localSurface.readFromParcel(paramAnonymousParcel);
        return localSurface;
      }
      catch (Exception paramAnonymousParcel)
      {
        Log.e("Surface", "Exception creating surface from parcel", paramAnonymousParcel);
      }
      return null;
    }
    
    public Surface[] newArray(int paramAnonymousInt)
    {
      return new Surface[paramAnonymousInt];
    }
  };
  public static final int ROTATION_0 = 0;
  public static final int ROTATION_180 = 2;
  public static final int ROTATION_270 = 3;
  public static final int ROTATION_90 = 1;
  public static final int SCALING_MODE_FREEZE = 0;
  public static final int SCALING_MODE_NO_SCALE_CROP = 3;
  public static final int SCALING_MODE_SCALE_CROP = 2;
  public static final int SCALING_MODE_SCALE_TO_WINDOW = 1;
  private static final String TAG = "Surface";
  private final Canvas mCanvas = new CompatibleCanvas(null);
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private Matrix mCompatibleMatrix;
  private int mGenerationId;
  private HwuiContext mHwuiContext;
  private boolean mIsAutoRefreshEnabled;
  private boolean mIsSharedBufferModeEnabled;
  private boolean mIsSingleBuffered;
  @UnsupportedAppUsage
  final Object mLock = new Object();
  @UnsupportedAppUsage
  private long mLockedObject;
  @UnsupportedAppUsage
  private String mName;
  @UnsupportedAppUsage
  long mNativeObject;
  
  @UnsupportedAppUsage
  public Surface() {}
  
  @UnsupportedAppUsage
  private Surface(long paramLong)
  {
    synchronized (this.mLock)
    {
      setNativeObjectLocked(paramLong);
      return;
    }
  }
  
  public Surface(SurfaceTexture paramSurfaceTexture)
  {
    if (paramSurfaceTexture != null)
    {
      this.mIsSingleBuffered = paramSurfaceTexture.isSingleBuffered();
      synchronized (this.mLock)
      {
        this.mName = paramSurfaceTexture.toString();
        setNativeObjectLocked(nativeCreateFromSurfaceTexture(paramSurfaceTexture));
        return;
      }
    }
    throw new IllegalArgumentException("surfaceTexture must not be null");
  }
  
  public Surface(SurfaceControl paramSurfaceControl)
  {
    copyFrom(paramSurfaceControl);
  }
  
  private void checkNotReleasedLocked()
  {
    if (this.mNativeObject != 0L) {
      return;
    }
    throw new IllegalStateException("Surface has already been released.");
  }
  
  private static native long nHwuiCreate(long paramLong1, long paramLong2, boolean paramBoolean);
  
  private static native void nHwuiDestroy(long paramLong);
  
  private static native void nHwuiDraw(long paramLong);
  
  private static native void nHwuiSetSurface(long paramLong1, long paramLong2);
  
  private static native void nativeAllocateBuffers(long paramLong);
  
  private static native int nativeAttachAndQueueBuffer(long paramLong, GraphicBuffer paramGraphicBuffer);
  
  private static native long nativeCreateFromSurfaceControl(long paramLong);
  
  private static native long nativeCreateFromSurfaceTexture(SurfaceTexture paramSurfaceTexture)
    throws Surface.OutOfResourcesException;
  
  private static native int nativeForceScopedDisconnect(long paramLong);
  
  private static native long nativeGetFromSurfaceControl(long paramLong1, long paramLong2);
  
  private static native int nativeGetHeight(long paramLong);
  
  private static native long nativeGetNextFrameNumber(long paramLong);
  
  private static native int nativeGetWidth(long paramLong);
  
  private static native boolean nativeIsConsumerRunningBehind(long paramLong);
  
  private static native boolean nativeIsValid(long paramLong);
  
  private static native long nativeLockCanvas(long paramLong, Canvas paramCanvas, Rect paramRect)
    throws Surface.OutOfResourcesException;
  
  private static native long nativeReadFromParcel(long paramLong, Parcel paramParcel);
  
  @UnsupportedAppUsage
  private static native void nativeRelease(long paramLong);
  
  private static native int nativeSetAutoRefreshEnabled(long paramLong, boolean paramBoolean);
  
  private static native int nativeSetScalingMode(long paramLong, int paramInt);
  
  private static native int nativeSetSharedBufferModeEnabled(long paramLong, boolean paramBoolean);
  
  private static native void nativeUnlockCanvasAndPost(long paramLong, Canvas paramCanvas);
  
  private static native void nativeWriteToParcel(long paramLong, Parcel paramParcel);
  
  public static String rotationToString(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 3) {
            return Integer.toString(paramInt);
          }
          return "ROTATION_270";
        }
        return "ROTATION_180";
      }
      return "ROTATION_90";
    }
    return "ROTATION_0";
  }
  
  private void setNativeObjectLocked(long paramLong)
  {
    long l = this.mNativeObject;
    if (l != paramLong)
    {
      if ((l == 0L) && (paramLong != 0L)) {
        this.mCloseGuard.open("release");
      } else if ((this.mNativeObject != 0L) && (paramLong == 0L)) {
        this.mCloseGuard.close();
      }
      this.mNativeObject = paramLong;
      this.mGenerationId += 1;
      HwuiContext localHwuiContext = this.mHwuiContext;
      if (localHwuiContext != null) {
        localHwuiContext.updateSurface();
      }
    }
  }
  
  private void unlockSwCanvasAndPost(Canvas paramCanvas)
  {
    if (paramCanvas == this.mCanvas)
    {
      if (this.mNativeObject != this.mLockedObject)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("WARNING: Surface's mNativeObject (0x");
        localStringBuilder.append(Long.toHexString(this.mNativeObject));
        localStringBuilder.append(") != mLockedObject (0x");
        localStringBuilder.append(Long.toHexString(this.mLockedObject));
        localStringBuilder.append(")");
        Log.w("Surface", localStringBuilder.toString());
      }
      long l = this.mLockedObject;
      if (l != 0L) {
        try
        {
          nativeUnlockCanvasAndPost(l, paramCanvas);
          return;
        }
        finally
        {
          nativeRelease(this.mLockedObject);
          this.mLockedObject = 0L;
        }
      }
      throw new IllegalStateException("Surface was not locked");
    }
    throw new IllegalArgumentException("canvas object must be the same instance that was previously returned by lockCanvas");
  }
  
  public void allocateBuffers()
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      nativeAllocateBuffers(this.mNativeObject);
      return;
    }
  }
  
  public void attachAndQueueBuffer(GraphicBuffer paramGraphicBuffer)
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      if (nativeAttachAndQueueBuffer(this.mNativeObject, paramGraphicBuffer) == 0) {
        return;
      }
      paramGraphicBuffer = new java/lang/RuntimeException;
      paramGraphicBuffer.<init>("Failed to attach and queue buffer to Surface (bad object?)");
      throw paramGraphicBuffer;
    }
  }
  
  @UnsupportedAppUsage
  public void copyFrom(SurfaceControl arg1)
  {
    if (??? != null)
    {
      long l = ???.mNativeObject;
      if (l != 0L)
      {
        l = nativeGetFromSurfaceControl(this.mNativeObject, l);
        synchronized (this.mLock)
        {
          if (l == this.mNativeObject) {
            return;
          }
          if (this.mNativeObject != 0L) {
            nativeRelease(this.mNativeObject);
          }
          setNativeObjectLocked(l);
          return;
        }
      }
      throw new NullPointerException("null SurfaceControl native object. Are you using a released SurfaceControl?");
    }
    throw new IllegalArgumentException("other must not be null");
  }
  
  public void createFrom(SurfaceControl arg1)
  {
    if (??? != null)
    {
      long l = ???.mNativeObject;
      if (l != 0L)
      {
        l = nativeCreateFromSurfaceControl(l);
        synchronized (this.mLock)
        {
          if (this.mNativeObject != 0L) {
            nativeRelease(this.mNativeObject);
          }
          setNativeObjectLocked(l);
          return;
        }
      }
      throw new NullPointerException("null SurfaceControl native object. Are you using a released SurfaceControl?");
    }
    throw new IllegalArgumentException("other must not be null");
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  @UnsupportedAppUsage
  public void destroy()
  {
    release();
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mCloseGuard != null) {
        this.mCloseGuard.warnIfOpen();
      }
      release();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  void forceScopedDisconnect()
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      if (nativeForceScopedDisconnect(this.mNativeObject) == 0) {
        return;
      }
      RuntimeException localRuntimeException = new java/lang/RuntimeException;
      localRuntimeException.<init>("Failed to disconnect Surface instance (bad object?)");
      throw localRuntimeException;
    }
  }
  
  public int getGenerationId()
  {
    synchronized (this.mLock)
    {
      int i = this.mGenerationId;
      return i;
    }
  }
  
  @UnsupportedAppUsage
  public long getNextFrameNumber()
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      long l = nativeGetNextFrameNumber(this.mNativeObject);
      return l;
    }
  }
  
  public void hwuiDestroy()
  {
    HwuiContext localHwuiContext = this.mHwuiContext;
    if (localHwuiContext != null)
    {
      localHwuiContext.destroy();
      this.mHwuiContext = null;
    }
  }
  
  public boolean isAutoRefreshEnabled()
  {
    return this.mIsAutoRefreshEnabled;
  }
  
  public boolean isConsumerRunningBehind()
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      boolean bool = nativeIsConsumerRunningBehind(this.mNativeObject);
      return bool;
    }
  }
  
  public boolean isSharedBufferModeEnabled()
  {
    return this.mIsSharedBufferModeEnabled;
  }
  
  public boolean isSingleBuffered()
  {
    return this.mIsSingleBuffered;
  }
  
  public boolean isValid()
  {
    synchronized (this.mLock)
    {
      if (this.mNativeObject == 0L) {
        return false;
      }
      boolean bool = nativeIsValid(this.mNativeObject);
      return bool;
    }
  }
  
  public Canvas lockCanvas(Rect paramRect)
    throws Surface.OutOfResourcesException, IllegalArgumentException
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      if (this.mLockedObject == 0L)
      {
        this.mLockedObject = nativeLockCanvas(this.mNativeObject, this.mCanvas, paramRect);
        paramRect = this.mCanvas;
        return paramRect;
      }
      paramRect = new java/lang/IllegalArgumentException;
      paramRect.<init>("Surface was already locked");
      throw paramRect;
    }
  }
  
  public Canvas lockHardwareCanvas()
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      if (this.mHwuiContext == null)
      {
        localObject2 = new android/view/Surface$HwuiContext;
        ((HwuiContext)localObject2).<init>(this, false);
        this.mHwuiContext = ((HwuiContext)localObject2);
      }
      Object localObject2 = this.mHwuiContext.lockCanvas(nativeGetWidth(this.mNativeObject), nativeGetHeight(this.mNativeObject));
      return (Canvas)localObject2;
    }
  }
  
  public Canvas lockHardwareWideColorGamutCanvas()
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      if ((this.mHwuiContext != null) && (!this.mHwuiContext.isWideColorGamut()))
      {
        this.mHwuiContext.destroy();
        this.mHwuiContext = null;
      }
      if (this.mHwuiContext == null)
      {
        localObject2 = new android/view/Surface$HwuiContext;
        ((HwuiContext)localObject2).<init>(this, true);
        this.mHwuiContext = ((HwuiContext)localObject2);
      }
      Object localObject2 = this.mHwuiContext.lockCanvas(nativeGetWidth(this.mNativeObject), nativeGetHeight(this.mNativeObject));
      return (Canvas)localObject2;
    }
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    if (paramParcel != null) {
      synchronized (this.mLock)
      {
        this.mName = paramParcel.readString();
        boolean bool;
        if (paramParcel.readInt() != 0) {
          bool = true;
        } else {
          bool = false;
        }
        this.mIsSingleBuffered = bool;
        setNativeObjectLocked(nativeReadFromParcel(this.mNativeObject, paramParcel));
        return;
      }
    }
    throw new IllegalArgumentException("source must not be null");
  }
  
  public void release()
  {
    synchronized (this.mLock)
    {
      if (this.mNativeObject != 0L)
      {
        nativeRelease(this.mNativeObject);
        setNativeObjectLocked(0L);
      }
      if (this.mHwuiContext != null)
      {
        this.mHwuiContext.destroy();
        this.mHwuiContext = null;
      }
      return;
    }
  }
  
  public void setAutoRefreshEnabled(boolean paramBoolean)
  {
    if (this.mIsAutoRefreshEnabled != paramBoolean) {
      if (nativeSetAutoRefreshEnabled(this.mNativeObject, paramBoolean) == 0) {
        this.mIsAutoRefreshEnabled = paramBoolean;
      } else {
        throw new RuntimeException("Failed to set auto refresh on Surface (bad object?)");
      }
    }
  }
  
  void setCompatibilityTranslator(CompatibilityInfo.Translator paramTranslator)
  {
    if (paramTranslator != null)
    {
      float f = paramTranslator.applicationScale;
      this.mCompatibleMatrix = new Matrix();
      this.mCompatibleMatrix.setScale(f, f);
    }
  }
  
  void setScalingMode(int paramInt)
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      if (nativeSetScalingMode(this.mNativeObject, paramInt) == 0) {
        return;
      }
      IllegalArgumentException localIllegalArgumentException = new java/lang/IllegalArgumentException;
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      localStringBuilder.append("Invalid scaling mode: ");
      localStringBuilder.append(paramInt);
      localIllegalArgumentException.<init>(localStringBuilder.toString());
      throw localIllegalArgumentException;
    }
  }
  
  public void setSharedBufferModeEnabled(boolean paramBoolean)
  {
    if (this.mIsSharedBufferModeEnabled != paramBoolean) {
      if (nativeSetSharedBufferModeEnabled(this.mNativeObject, paramBoolean) == 0) {
        this.mIsSharedBufferModeEnabled = paramBoolean;
      } else {
        throw new RuntimeException("Failed to set shared buffer mode on Surface (bad object?)");
      }
    }
  }
  
  public String toString()
  {
    synchronized (this.mLock)
    {
      Object localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      ((StringBuilder)localObject2).append("Surface(name=");
      ((StringBuilder)localObject2).append(this.mName);
      ((StringBuilder)localObject2).append(")/@0x");
      ((StringBuilder)localObject2).append(Integer.toHexString(System.identityHashCode(this)));
      localObject2 = ((StringBuilder)localObject2).toString();
      return (String)localObject2;
    }
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public void transferFrom(Surface paramSurface)
  {
    if (paramSurface != null)
    {
      if (paramSurface != this) {
        synchronized (paramSurface.mLock)
        {
          long l = paramSurface.mNativeObject;
          paramSurface.setNativeObjectLocked(0L);
          synchronized (this.mLock)
          {
            if (this.mNativeObject != 0L) {
              nativeRelease(this.mNativeObject);
            }
            setNativeObjectLocked(l);
          }
        }
      }
      return;
    }
    throw new IllegalArgumentException("other must not be null");
  }
  
  @Deprecated
  public void unlockCanvas(Canvas paramCanvas)
  {
    throw new UnsupportedOperationException();
  }
  
  public void unlockCanvasAndPost(Canvas paramCanvas)
  {
    synchronized (this.mLock)
    {
      checkNotReleasedLocked();
      if (this.mHwuiContext != null) {
        this.mHwuiContext.unlockAndPost(paramCanvas);
      } else {
        unlockSwCanvasAndPost(paramCanvas);
      }
      return;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (paramParcel != null) {
      synchronized (this.mLock)
      {
        paramParcel.writeString(this.mName);
        int i;
        if (this.mIsSingleBuffered) {
          i = 1;
        } else {
          i = 0;
        }
        paramParcel.writeInt(i);
        nativeWriteToParcel(this.mNativeObject, paramParcel);
        if ((paramInt & 0x1) != 0) {
          release();
        }
        return;
      }
    }
    throw new IllegalArgumentException("dest must not be null");
  }
  
  private final class CompatibleCanvas
    extends Canvas
  {
    private Matrix mOrigMatrix = null;
    
    private CompatibleCanvas() {}
    
    public void getMatrix(Matrix paramMatrix)
    {
      super.getMatrix(paramMatrix);
      if (this.mOrigMatrix == null) {
        this.mOrigMatrix = new Matrix();
      }
      this.mOrigMatrix.set(paramMatrix);
    }
    
    public void setMatrix(Matrix paramMatrix)
    {
      if (Surface.this.mCompatibleMatrix != null)
      {
        Matrix localMatrix = this.mOrigMatrix;
        if ((localMatrix != null) && (!localMatrix.equals(paramMatrix)))
        {
          localMatrix = new Matrix(Surface.this.mCompatibleMatrix);
          localMatrix.preConcat(paramMatrix);
          super.setMatrix(localMatrix);
          return;
        }
      }
      super.setMatrix(paramMatrix);
    }
  }
  
  private final class HwuiContext
  {
    private RecordingCanvas mCanvas;
    private long mHwuiRenderer;
    private final boolean mIsWideColorGamut;
    private final RenderNode mRenderNode = RenderNode.create("HwuiCanvas", null);
    
    HwuiContext(boolean paramBoolean)
    {
      this.mRenderNode.setClipToBounds(false);
      this.mRenderNode.setForceDarkAllowed(false);
      this.mIsWideColorGamut = paramBoolean;
      this.mHwuiRenderer = Surface.nHwuiCreate(this.mRenderNode.mNativeRenderNode, Surface.this.mNativeObject, paramBoolean);
    }
    
    void destroy()
    {
      long l = this.mHwuiRenderer;
      if (l != 0L)
      {
        Surface.nHwuiDestroy(l);
        this.mHwuiRenderer = 0L;
      }
    }
    
    boolean isWideColorGamut()
    {
      return this.mIsWideColorGamut;
    }
    
    Canvas lockCanvas(int paramInt1, int paramInt2)
    {
      if (this.mCanvas == null)
      {
        this.mCanvas = this.mRenderNode.beginRecording(paramInt1, paramInt2);
        return this.mCanvas;
      }
      throw new IllegalStateException("Surface was already locked!");
    }
    
    void unlockAndPost(Canvas paramCanvas)
    {
      if (paramCanvas == this.mCanvas)
      {
        this.mRenderNode.endRecording();
        this.mCanvas = null;
        Surface.nHwuiDraw(this.mHwuiRenderer);
        return;
      }
      throw new IllegalArgumentException("canvas object must be the same instance that was previously returned by lockCanvas");
    }
    
    void updateSurface()
    {
      Surface.nHwuiSetSurface(this.mHwuiRenderer, Surface.this.mNativeObject);
    }
  }
  
  public static class OutOfResourcesException
    extends RuntimeException
  {
    public OutOfResourcesException() {}
    
    public OutOfResourcesException(String paramString)
    {
      super();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Rotation {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ScalingMode {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/Surface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */