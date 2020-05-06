package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.CompatibilityInfo.Translator;
import android.content.res.Configuration;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.RenderNode;
import android.graphics.RenderNode.PositionUpdateListener;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class SurfaceView
  extends View
  implements ViewRootImpl.WindowStoppedCallback, ViewRootImpl.CastProjectionCallback
{
  private static final boolean DEBUG = false;
  private static final String TAG = "SurfaceView";
  private boolean lastCastModeOpen = false;
  private boolean mAlreadySetCastMode;
  private boolean mAttachedToWindow;
  SurfaceControl mBackgroundControl;
  private boolean mBlurCurrent;
  @UnsupportedAppUsage
  final ArrayList<SurfaceHolder.Callback> mCallbacks = new ArrayList();
  final Configuration mConfiguration = new Configuration();
  float mCornerRadius;
  SurfaceControl mDeferredDestroySurfaceControl;
  boolean mDrawFinished = false;
  @UnsupportedAppUsage
  private final ViewTreeObserver.OnPreDrawListener mDrawListener = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      SurfaceView localSurfaceView = SurfaceView.this;
      boolean bool;
      if ((localSurfaceView.getWidth() > 0) && (SurfaceView.this.getHeight() > 0)) {
        bool = true;
      } else {
        bool = false;
      }
      localSurfaceView.mHaveFrame = bool;
      SurfaceView.this.updateSurface();
      return true;
    }
  };
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  boolean mDrawingStopped = true;
  @UnsupportedAppUsage
  int mFormat = -1;
  private boolean mGlobalListenersAdded;
  @UnsupportedAppUsage
  boolean mHaveFrame = false;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  boolean mIsCreating = false;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  long mLastLockTime = 0L;
  int mLastSurfaceHeight = -1;
  int mLastSurfaceWidth = -1;
  boolean mLastWindowVisibility = false;
  final int[] mLocation = new int[2];
  private int mPendingReportDraws;
  private RenderNode.PositionUpdateListener mPositionListener = new RenderNode.PositionUpdateListener()
  {
    public void positionChanged(long paramAnonymousLong, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      if (SurfaceView.this.mSurfaceControl == null) {
        return;
      }
      SurfaceView.access$002(SurfaceView.this, true);
      if ((SurfaceView.this.mRTLastReportedPosition.left == paramAnonymousInt1) && (SurfaceView.this.mRTLastReportedPosition.top == paramAnonymousInt2) && (SurfaceView.this.mRTLastReportedPosition.right == paramAnonymousInt3) && (SurfaceView.this.mRTLastReportedPosition.bottom == paramAnonymousInt4)) {
        return;
      }
      try
      {
        SurfaceView.this.mRTLastReportedPosition.set(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        SurfaceView.this.setParentSpaceRectangle(SurfaceView.this.mRTLastReportedPosition, paramAnonymousLong);
      }
      catch (Exception localException)
      {
        Log.e("SurfaceView", "Exception from repositionChild", localException);
      }
    }
    
    public void positionLost(long paramAnonymousLong)
    {
      SurfaceView.this.mRTLastReportedPosition.setEmpty();
      if (SurfaceView.this.mSurfaceControl == null) {
        return;
      }
      if (paramAnonymousLong > 0L)
      {
        ViewRootImpl localViewRootImpl = SurfaceView.this.getViewRootImpl();
        SurfaceView.this.mRtTransaction.deferTransactionUntilSurface(SurfaceView.this.mSurfaceControl, localViewRootImpl.mSurface, paramAnonymousLong);
      }
      SurfaceView.this.mRtTransaction.hide(SurfaceView.this.mSurfaceControl);
      SurfaceView.this.mRtTransaction.apply();
    }
  };
  private Rect mRTLastReportedPosition = new Rect();
  @UnsupportedAppUsage
  int mRequestedFormat = 4;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  int mRequestedHeight = -1;
  boolean mRequestedVisible = false;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  int mRequestedWidth = -1;
  Paint mRoundedViewportPaint;
  private volatile boolean mRtHandlingPositionUpdates = false;
  private SurfaceControl.Transaction mRtTransaction = new SurfaceControl.Transaction();
  final Rect mScreenRect = new Rect();
  private final ViewTreeObserver.OnScrollChangedListener mScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener()
  {
    public void onScrollChanged()
    {
      SurfaceView.this.updateSurface();
    }
  };
  int mSubLayer = -2;
  @UnsupportedAppUsage
  final Surface mSurface = new Surface();
  SurfaceControl mSurfaceControl;
  boolean mSurfaceCreated = false;
  private int mSurfaceFlags = 4;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  final Rect mSurfaceFrame = new Rect();
  int mSurfaceHeight = -1;
  @UnsupportedAppUsage
  private final SurfaceHolder mSurfaceHolder = new SurfaceHolder()
  {
    private static final String LOG_TAG = "SurfaceHolder";
    
    private Canvas internalLockCanvas(Rect paramAnonymousRect, boolean paramAnonymousBoolean)
    {
      SurfaceView.this.mSurfaceLock.lock();
      Object localObject1 = null;
      Object localObject2 = localObject1;
      if (!SurfaceView.this.mDrawingStopped)
      {
        localObject2 = localObject1;
        if (SurfaceView.this.mSurfaceControl != null)
        {
          if (paramAnonymousBoolean) {}
          try
          {
            paramAnonymousRect = SurfaceView.this.mSurface.lockHardwareCanvas();
            break label68;
            paramAnonymousRect = SurfaceView.this.mSurface.lockCanvas(paramAnonymousRect);
            label68:
            localObject2 = paramAnonymousRect;
          }
          catch (Exception paramAnonymousRect)
          {
            Log.e("SurfaceHolder", "Exception locking surface", paramAnonymousRect);
            localObject2 = localObject1;
          }
        }
      }
      if (localObject2 != null)
      {
        SurfaceView.this.mLastLockTime = SystemClock.uptimeMillis();
        return (Canvas)localObject2;
      }
      long l1 = SystemClock.uptimeMillis();
      long l2 = SurfaceView.this.mLastLockTime + 100L;
      long l3 = l1;
      if (l2 > l1)
      {
        try
        {
          Thread.sleep(l2 - l1);
        }
        catch (InterruptedException paramAnonymousRect) {}
        l3 = SystemClock.uptimeMillis();
      }
      paramAnonymousRect = SurfaceView.this;
      paramAnonymousRect.mLastLockTime = l3;
      paramAnonymousRect.mSurfaceLock.unlock();
      return null;
    }
    
    public void addCallback(SurfaceHolder.Callback paramAnonymousCallback)
    {
      synchronized (SurfaceView.this.mCallbacks)
      {
        if (!SurfaceView.this.mCallbacks.contains(paramAnonymousCallback)) {
          SurfaceView.this.mCallbacks.add(paramAnonymousCallback);
        }
        return;
      }
    }
    
    public Surface getSurface()
    {
      return SurfaceView.this.mSurface;
    }
    
    public Rect getSurfaceFrame()
    {
      return SurfaceView.this.mSurfaceFrame;
    }
    
    public boolean isCreating()
    {
      return SurfaceView.this.mIsCreating;
    }
    
    public Canvas lockCanvas()
    {
      return internalLockCanvas(null, false);
    }
    
    public Canvas lockCanvas(Rect paramAnonymousRect)
    {
      return internalLockCanvas(paramAnonymousRect, false);
    }
    
    public Canvas lockHardwareCanvas()
    {
      return internalLockCanvas(null, true);
    }
    
    public void removeCallback(SurfaceHolder.Callback paramAnonymousCallback)
    {
      synchronized (SurfaceView.this.mCallbacks)
      {
        SurfaceView.this.mCallbacks.remove(paramAnonymousCallback);
        return;
      }
    }
    
    public void setFixedSize(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if ((SurfaceView.this.mRequestedWidth != paramAnonymousInt1) || (SurfaceView.this.mRequestedHeight != paramAnonymousInt2))
      {
        SurfaceView localSurfaceView = SurfaceView.this;
        localSurfaceView.mRequestedWidth = paramAnonymousInt1;
        localSurfaceView.mRequestedHeight = paramAnonymousInt2;
        localSurfaceView.requestLayout();
      }
    }
    
    public void setFormat(int paramAnonymousInt)
    {
      int i = paramAnonymousInt;
      if (paramAnonymousInt == -1) {
        i = 4;
      }
      SurfaceView localSurfaceView = SurfaceView.this;
      localSurfaceView.mRequestedFormat = i;
      if (localSurfaceView.mSurfaceControl != null) {
        SurfaceView.this.updateSurface();
      }
    }
    
    public void setKeepScreenOn(boolean paramAnonymousBoolean)
    {
      SurfaceView.this.runOnUiThread(new _..Lambda.SurfaceView.4.wAwzCgpoBmqWbw6GlT0xJXSxjm4(this, paramAnonymousBoolean));
    }
    
    public void setSizeFromLayout()
    {
      if ((SurfaceView.this.mRequestedWidth != -1) || (SurfaceView.this.mRequestedHeight != -1))
      {
        SurfaceView localSurfaceView = SurfaceView.this;
        localSurfaceView.mRequestedHeight = -1;
        localSurfaceView.mRequestedWidth = -1;
        localSurfaceView.requestLayout();
      }
    }
    
    @Deprecated
    public void setType(int paramAnonymousInt) {}
    
    public void unlockCanvasAndPost(Canvas paramAnonymousCanvas)
    {
      SurfaceView.this.mSurface.unlockCanvasAndPost(paramAnonymousCanvas);
      SurfaceView.this.mSurfaceLock.unlock();
    }
  };
  @UnsupportedAppUsage
  final ReentrantLock mSurfaceLock = new ReentrantLock();
  SurfaceSession mSurfaceSession;
  int mSurfaceWidth = -1;
  final Rect mTmpRect = new Rect();
  private CompatibilityInfo.Translator mTranslator;
  boolean mViewVisibility = false;
  boolean mVisible = false;
  int mWindowSpaceLeft = -1;
  int mWindowSpaceTop = -1;
  boolean mWindowStopped = false;
  boolean mWindowVisibility = false;
  
  public SurfaceView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SurfaceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SurfaceView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SurfaceView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mRenderNode.addPositionUpdateListener(this.mPositionListener);
    setWillNotDraw(true);
  }
  
  private void applySurfaceTransforms(SurfaceControl paramSurfaceControl, Rect paramRect, long paramLong)
  {
    if (paramLong > 0L)
    {
      ViewRootImpl localViewRootImpl = getViewRootImpl();
      this.mRtTransaction.deferTransactionUntilSurface(paramSurfaceControl, localViewRootImpl.mSurface, paramLong);
    }
    this.mRtTransaction.setPosition(paramSurfaceControl, paramRect.left, paramRect.top);
    this.mRtTransaction.setMatrix(paramSurfaceControl, paramRect.width() / this.mSurfaceWidth, 0.0F, 0.0F, paramRect.height() / this.mSurfaceHeight);
    if (this.mViewVisibility) {
      this.mRtTransaction.show(paramSurfaceControl);
    }
  }
  
  private void clearSurfaceViewPort(Canvas paramCanvas)
  {
    if (this.mCornerRadius > 0.0F)
    {
      paramCanvas.getClipBounds(this.mTmpRect);
      float f1 = this.mTmpRect.left;
      float f2 = this.mTmpRect.top;
      float f3 = this.mTmpRect.right;
      float f4 = this.mTmpRect.bottom;
      float f5 = this.mCornerRadius;
      paramCanvas.drawRoundRect(f1, f2, f3, f4, f5, f5, this.mRoundedViewportPaint);
    }
    else
    {
      paramCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }
  }
  
  private Rect getParentSurfaceInsets()
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    if (localViewRootImpl == null) {
      return null;
    }
    return localViewRootImpl.mWindowAttributes.surfaceInsets;
  }
  
  private SurfaceHolder.Callback[] getSurfaceCallbacks()
  {
    synchronized (this.mCallbacks)
    {
      SurfaceHolder.Callback[] arrayOfCallback = new SurfaceHolder.Callback[this.mCallbacks.size()];
      this.mCallbacks.toArray(arrayOfCallback);
      return arrayOfCallback;
    }
  }
  
  private boolean isAboveParent()
  {
    boolean bool;
    if (this.mSubLayer >= 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void notifyConfirmedSurfaceView(boolean paramBoolean)
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    if (localViewRootImpl != null) {
      localViewRootImpl.notifySurfaceViewCountChange(paramBoolean);
    }
  }
  
  private void onDrawFinished()
  {
    SurfaceControl localSurfaceControl = this.mDeferredDestroySurfaceControl;
    if (localSurfaceControl != null)
    {
      localSurfaceControl.remove();
      this.mDeferredDestroySurfaceControl = null;
    }
    runOnUiThread(new _..Lambda.SurfaceView.Cs7TGTdA1lXf9qW8VOJAfEsMjdk(this));
  }
  
  private void performDrawFinished()
  {
    if (this.mPendingReportDraws > 0)
    {
      this.mDrawFinished = true;
      if (this.mAttachedToWindow)
      {
        notifyDrawFinished();
        invalidate();
      }
    }
    else
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(System.identityHashCode(this));
      localStringBuilder.append("finished drawing but no pending report draw (extra call to draw completion runnable?)");
      Log.e("SurfaceView", localStringBuilder.toString());
    }
  }
  
  private void releaseSurfaces()
  {
    SurfaceControl localSurfaceControl = this.mSurfaceControl;
    if (localSurfaceControl != null)
    {
      localSurfaceControl.remove();
      this.mSurfaceControl = null;
    }
    localSurfaceControl = this.mBackgroundControl;
    if (localSurfaceControl != null)
    {
      localSurfaceControl.remove();
      this.mBackgroundControl = null;
    }
  }
  
  private void runOnUiThread(Runnable paramRunnable)
  {
    Handler localHandler = getHandler();
    if ((localHandler != null) && (localHandler.getLooper() != Looper.myLooper())) {
      localHandler.post(paramRunnable);
    } else {
      paramRunnable.run();
    }
  }
  
  private void setParentSpaceRectangle(Rect paramRect, long paramLong)
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    applySurfaceTransforms(this.mSurfaceControl, paramRect, paramLong);
    applyChildSurfaceTransaction_renderWorker(this.mRtTransaction, localViewRootImpl.mSurface, paramLong);
    this.mRtTransaction.apply();
  }
  
  private void updateBackgroundVisibilityInTransaction(SurfaceControl paramSurfaceControl)
  {
    SurfaceControl localSurfaceControl = this.mBackgroundControl;
    if (localSurfaceControl == null) {
      return;
    }
    if ((this.mSubLayer < 0) && ((this.mSurfaceFlags & 0x400) != 0))
    {
      localSurfaceControl.show();
      this.mBackgroundControl.setRelativeLayer(paramSurfaceControl, Integer.MIN_VALUE);
    }
    else
    {
      this.mBackgroundControl.hide();
    }
  }
  
  private void updateOpaqueFlag()
  {
    if (!PixelFormat.formatHasAlpha(this.mRequestedFormat)) {
      this.mSurfaceFlags |= 0x400;
    } else {
      this.mSurfaceFlags &= 0xFBFF;
    }
  }
  
  private void updateRequestedVisibility()
  {
    boolean bool;
    if ((this.mViewVisibility) && (this.mWindowVisibility) && (!this.mWindowStopped)) {
      bool = true;
    } else {
      bool = false;
    }
    this.mRequestedVisible = bool;
  }
  
  protected void applyChildSurfaceTransaction_renderWorker(SurfaceControl.Transaction paramTransaction, Surface paramSurface, long paramLong) {}
  
  public void castModeChanged()
  {
    updateSurface();
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    if ((this.mDrawFinished) && (!isAboveParent()) && ((this.mPrivateFlags & 0x80) == 128)) {
      clearSurfaceViewPort(paramCanvas);
    }
    super.dispatchDraw(paramCanvas);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if ((this.mDrawFinished) && (!isAboveParent()) && ((this.mPrivateFlags & 0x80) == 0)) {
      clearSurfaceViewPort(paramCanvas);
    }
    super.draw(paramCanvas);
  }
  
  public boolean gatherTransparentRegion(Region paramRegion)
  {
    if ((!isAboveParent()) && (this.mDrawFinished))
    {
      boolean bool1 = true;
      boolean bool2;
      if ((this.mPrivateFlags & 0x80) == 0)
      {
        bool2 = super.gatherTransparentRegion(paramRegion);
      }
      else
      {
        bool2 = bool1;
        if (paramRegion != null)
        {
          int i = getWidth();
          int j = getHeight();
          bool2 = bool1;
          if (i > 0)
          {
            bool2 = bool1;
            if (j > 0)
            {
              getLocationInWindow(this.mLocation);
              int[] arrayOfInt = this.mLocation;
              int k = arrayOfInt[0];
              int m = arrayOfInt[1];
              paramRegion.op(k, m, k + i, m + j, Region.Op.UNION);
              bool2 = bool1;
            }
          }
        }
      }
      if (PixelFormat.formatHasAlpha(this.mRequestedFormat)) {
        bool2 = false;
      }
      return bool2;
    }
    return super.gatherTransparentRegion(paramRegion);
  }
  
  public SurfaceHolder getHolder()
  {
    return this.mSurfaceHolder;
  }
  
  public SurfaceControl getSurfaceControl()
  {
    return this.mSurfaceControl;
  }
  
  @UnsupportedAppUsage
  public boolean isFixedSize()
  {
    boolean bool;
    if ((this.mRequestedWidth == -1) && (this.mRequestedHeight == -1)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  void notifyDrawFinished()
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    if (localViewRootImpl != null) {
      localViewRootImpl.pendingDrawFinished();
    }
    this.mPendingReportDraws -= 1;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    notifyConfirmedSurfaceView(true);
    getViewRootImpl().addWindowStoppedCallback(this);
    getViewRootImpl().addCastProjectionCallback(this);
    boolean bool = false;
    this.mAlreadySetCastMode = false;
    this.mWindowStopped = false;
    if (getVisibility() == 0) {
      bool = true;
    }
    this.mViewVisibility = bool;
    updateRequestedVisibility();
    this.mAttachedToWindow = true;
    this.mParent.requestTransparentRegion(this);
    if (!this.mGlobalListenersAdded)
    {
      ViewTreeObserver localViewTreeObserver = getViewTreeObserver();
      localViewTreeObserver.addOnScrollChangedListener(this.mScrollChangedListener);
      localViewTreeObserver.addOnPreDrawListener(this.mDrawListener);
      this.mGlobalListenersAdded = true;
    }
  }
  
  protected void onDetachedFromWindow()
  {
    notifyConfirmedSurfaceView(false);
    Object localObject = getViewRootImpl();
    if (localObject != null)
    {
      ((ViewRootImpl)localObject).removeWindowStoppedCallback(this);
      ((ViewRootImpl)localObject).removeCastProjectionCallback(this);
    }
    this.mAlreadySetCastMode = false;
    this.mAttachedToWindow = false;
    if (this.mGlobalListenersAdded)
    {
      localObject = getViewTreeObserver();
      ((ViewTreeObserver)localObject).removeOnScrollChangedListener(this.mScrollChangedListener);
      ((ViewTreeObserver)localObject).removeOnPreDrawListener(this.mDrawListener);
      this.mGlobalListenersAdded = false;
    }
    while (this.mPendingReportDraws > 0) {
      notifyDrawFinished();
    }
    this.mRequestedVisible = false;
    updateSurface();
    localObject = this.mSurfaceControl;
    if (localObject != null) {
      ((SurfaceControl)localObject).remove();
    }
    this.mSurfaceControl = null;
    this.mHaveFrame = false;
    super.onDetachedFromWindow();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = this.mRequestedWidth;
    if (i >= 0) {
      paramInt1 = resolveSizeAndState(i, paramInt1, 0);
    } else {
      paramInt1 = getDefaultSize(0, paramInt1);
    }
    i = this.mRequestedHeight;
    if (i >= 0) {
      paramInt2 = resolveSizeAndState(i, paramInt2, 0);
    } else {
      paramInt2 = getDefaultSize(0, paramInt2);
    }
    setMeasuredDimension(paramInt1, paramInt2);
  }
  
  protected void onWindowVisibilityChanged(int paramInt)
  {
    super.onWindowVisibilityChanged(paramInt);
    boolean bool;
    if (paramInt == 0) {
      bool = true;
    } else {
      bool = false;
    }
    this.mWindowVisibility = bool;
    updateRequestedVisibility();
    updateSurface();
  }
  
  public void setCornerRadius(float paramFloat)
  {
    this.mCornerRadius = paramFloat;
    if ((this.mCornerRadius > 0.0F) && (this.mRoundedViewportPaint == null))
    {
      this.mRoundedViewportPaint = new Paint(1);
      this.mRoundedViewportPaint.setBlendMode(BlendMode.CLEAR);
      this.mRoundedViewportPaint.setColor(0);
    }
    invalidate();
  }
  
  @UnsupportedAppUsage
  protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = super.setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    updateSurface();
    return bool;
  }
  
  public void setResizeBackgroundColor(int paramInt)
  {
    if (this.mBackgroundControl == null) {
      return;
    }
    float f1 = Color.red(paramInt) / 255.0F;
    float f2 = Color.green(paramInt) / 255.0F;
    float f3 = Color.blue(paramInt) / 255.0F;
    SurfaceControl.openTransaction();
    try
    {
      this.mBackgroundControl.setColor(new float[] { f1, f2, f3 });
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
    }
  }
  
  public void setSecure(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mSurfaceFlags |= 0x80;
    } else {
      this.mSurfaceFlags &= 0xFF7F;
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    boolean bool1 = false;
    if (paramInt == 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mViewVisibility = bool2;
    boolean bool2 = bool1;
    if (this.mWindowVisibility)
    {
      bool2 = bool1;
      if (this.mViewVisibility)
      {
        bool2 = bool1;
        if (!this.mWindowStopped) {
          bool2 = true;
        }
      }
    }
    if (bool2 != this.mRequestedVisible)
    {
      getViewRootImpl().setProjectionModeChanged(true);
      requestLayout();
    }
    this.mRequestedVisible = bool2;
    updateSurface();
  }
  
  public void setZOrderMediaOverlay(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = -1;
    } else {
      i = -2;
    }
    this.mSubLayer = i;
  }
  
  public void setZOrderOnTop(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mSubLayer = 1;
    } else {
      this.mSubLayer = -2;
    }
  }
  
  /* Error */
  protected void updateSurface()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 180	android/view/SurfaceView:mHaveFrame	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: invokevirtual 257	android/view/SurfaceView:getViewRootImpl	()Landroid/view/ViewRootImpl;
    //   12: astore_1
    //   13: aload_1
    //   14: ifnull +2304 -> 2318
    //   17: aload_1
    //   18: getfield 260	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   21: ifnull +2297 -> 2318
    //   24: aload_1
    //   25: getfield 260	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   28: invokevirtual 655	android/view/Surface:isValid	()Z
    //   31: ifne +6 -> 37
    //   34: goto +2284 -> 2318
    //   37: aload_0
    //   38: aload_1
    //   39: getfield 657	android/view/ViewRootImpl:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   42: putfield 658	android/view/SurfaceView:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   45: aload_0
    //   46: getfield 658	android/view/SurfaceView:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   49: astore_2
    //   50: aload_2
    //   51: ifnull +11 -> 62
    //   54: aload_0
    //   55: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   58: aload_2
    //   59: invokevirtual 662	android/view/Surface:setCompatibilityTranslator	(Landroid/content/res/CompatibilityInfo$Translator;)V
    //   62: aload_0
    //   63: getfield 530	android/view/SurfaceView:mAlreadySetCastMode	Z
    //   66: ifeq +14 -> 80
    //   69: aload_1
    //   70: getfield 665	android/view/ViewRootImpl:mIsCastMode	Z
    //   73: aload_0
    //   74: getfield 204	android/view/SurfaceView:lastCastModeOpen	Z
    //   77: if_icmpeq +118 -> 195
    //   80: aload_0
    //   81: aload_1
    //   82: getfield 665	android/view/ViewRootImpl:mIsCastMode	Z
    //   85: putfield 204	android/view/SurfaceView:lastCastModeOpen	Z
    //   88: aload_0
    //   89: iconst_1
    //   90: putfield 530	android/view/SurfaceView:mAlreadySetCastMode	Z
    //   93: new 376	java/lang/StringBuilder
    //   96: dup
    //   97: invokespecial 377	java/lang/StringBuilder:<init>	()V
    //   100: astore_2
    //   101: aload_2
    //   102: ldc_w 667
    //   105: invokevirtual 392	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   108: pop
    //   109: aload_2
    //   110: aload_0
    //   111: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   114: invokevirtual 670	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   117: pop
    //   118: aload_2
    //   119: ldc_w 672
    //   122: invokevirtual 392	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: pop
    //   126: aload_2
    //   127: aload_1
    //   128: getfield 665	android/view/ViewRootImpl:mIsCastMode	Z
    //   131: invokevirtual 675	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   134: pop
    //   135: ldc 23
    //   137: aload_2
    //   138: invokevirtual 396	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   141: invokestatic 678	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   144: pop
    //   145: new 208	android/view/SurfaceControl$Transaction
    //   148: dup
    //   149: invokespecial 209	android/view/SurfaceControl$Transaction:<init>	()V
    //   152: astore_2
    //   153: aload_0
    //   154: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   157: astore_3
    //   158: aload_3
    //   159: ifnull +13 -> 172
    //   162: aload_2
    //   163: aload_3
    //   164: aload_1
    //   165: getfield 665	android/view/ViewRootImpl:mIsCastMode	Z
    //   168: invokevirtual 682	android/view/SurfaceControl$Transaction:setFlagsFromSV	(Landroid/view/SurfaceControl;Z)Landroid/view/SurfaceControl$Transaction;
    //   171: pop
    //   172: aload_0
    //   173: getfield 407	android/view/SurfaceView:mBackgroundControl	Landroid/view/SurfaceControl;
    //   176: astore_3
    //   177: aload_3
    //   178: ifnull +13 -> 191
    //   181: aload_2
    //   182: aload_3
    //   183: aload_1
    //   184: getfield 665	android/view/ViewRootImpl:mIsCastMode	Z
    //   187: invokevirtual 682	android/view/SurfaceControl$Transaction:setFlagsFromSV	(Landroid/view/SurfaceControl;Z)Landroid/view/SurfaceControl$Transaction;
    //   190: pop
    //   191: aload_2
    //   192: invokevirtual 440	android/view/SurfaceControl$Transaction:apply	()V
    //   195: aload_1
    //   196: invokevirtual 685	android/view/ViewRootImpl:getProjectionModeChanged	()Z
    //   199: ifeq +217 -> 416
    //   202: aload_0
    //   203: invokevirtual 689	android/view/SurfaceView:getResources	()Landroid/content/res/Resources;
    //   206: ifnull +47 -> 253
    //   209: aload_0
    //   210: invokevirtual 689	android/view/SurfaceView:getResources	()Landroid/content/res/Resources;
    //   213: invokevirtual 695	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   216: ifnull +37 -> 253
    //   219: aload_0
    //   220: invokevirtual 689	android/view/SurfaceView:getResources	()Landroid/content/res/Resources;
    //   223: invokevirtual 695	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   226: getfield 699	android/content/res/Configuration:windowConfiguration	Landroid/app/WindowConfiguration;
    //   229: ifnonnull +6 -> 235
    //   232: goto +21 -> 253
    //   235: aload_0
    //   236: invokevirtual 689	android/view/SurfaceView:getResources	()Landroid/content/res/Resources;
    //   239: invokevirtual 695	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   242: getfield 699	android/content/res/Configuration:windowConfiguration	Landroid/app/WindowConfiguration;
    //   245: invokevirtual 704	android/app/WindowConfiguration:getWindowingMode	()I
    //   248: istore 4
    //   250: goto +6 -> 256
    //   253: iconst_m1
    //   254: istore 4
    //   256: aload_0
    //   257: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   260: ifnull +156 -> 416
    //   263: iload 4
    //   265: iconst_5
    //   266: if_icmpne +150 -> 416
    //   269: aload_1
    //   270: iconst_0
    //   271: invokevirtual 645	android/view/ViewRootImpl:setProjectionModeChanged	(Z)V
    //   274: new 376	java/lang/StringBuilder
    //   277: dup
    //   278: invokespecial 377	java/lang/StringBuilder:<init>	()V
    //   281: astore_2
    //   282: aload_2
    //   283: ldc_w 667
    //   286: invokevirtual 392	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   289: pop
    //   290: aload_2
    //   291: aload_0
    //   292: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   295: invokevirtual 670	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   298: pop
    //   299: aload_2
    //   300: ldc_w 706
    //   303: invokevirtual 392	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   306: pop
    //   307: aload_2
    //   308: aload_1
    //   309: invokevirtual 709	android/view/ViewRootImpl:getIsProjectionMode	()Z
    //   312: invokevirtual 675	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   315: pop
    //   316: ldc 23
    //   318: aload_2
    //   319: invokevirtual 396	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   322: invokestatic 678	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   325: pop
    //   326: new 208	android/view/SurfaceControl$Transaction
    //   329: dup
    //   330: invokespecial 209	android/view/SurfaceControl$Transaction:<init>	()V
    //   333: astore_2
    //   334: aload_0
    //   335: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   338: ifnull +35 -> 373
    //   341: aload_1
    //   342: invokevirtual 709	android/view/ViewRootImpl:getIsProjectionMode	()Z
    //   345: ifeq +18 -> 363
    //   348: aload_2
    //   349: aload_0
    //   350: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   353: ldc_w 710
    //   356: invokevirtual 714	android/view/SurfaceControl$Transaction:setScreenProjection	(Landroid/view/SurfaceControl;I)Landroid/view/SurfaceControl$Transaction;
    //   359: pop
    //   360: goto +13 -> 373
    //   363: aload_2
    //   364: aload_0
    //   365: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   368: iconst_0
    //   369: invokevirtual 714	android/view/SurfaceControl$Transaction:setScreenProjection	(Landroid/view/SurfaceControl;I)Landroid/view/SurfaceControl$Transaction;
    //   372: pop
    //   373: aload_0
    //   374: getfield 407	android/view/SurfaceView:mBackgroundControl	Landroid/view/SurfaceControl;
    //   377: ifnull +35 -> 412
    //   380: aload_1
    //   381: invokevirtual 709	android/view/ViewRootImpl:getIsProjectionMode	()Z
    //   384: ifeq +18 -> 402
    //   387: aload_2
    //   388: aload_0
    //   389: getfield 407	android/view/SurfaceView:mBackgroundControl	Landroid/view/SurfaceControl;
    //   392: ldc_w 710
    //   395: invokevirtual 714	android/view/SurfaceControl$Transaction:setScreenProjection	(Landroid/view/SurfaceControl;I)Landroid/view/SurfaceControl$Transaction;
    //   398: pop
    //   399: goto +13 -> 412
    //   402: aload_2
    //   403: aload_0
    //   404: getfield 407	android/view/SurfaceView:mBackgroundControl	Landroid/view/SurfaceControl;
    //   407: iconst_0
    //   408: invokevirtual 714	android/view/SurfaceControl$Transaction:setScreenProjection	(Landroid/view/SurfaceControl;I)Landroid/view/SurfaceControl$Transaction;
    //   411: pop
    //   412: aload_2
    //   413: invokevirtual 440	android/view/SurfaceControl$Transaction:apply	()V
    //   416: aload_0
    //   417: getfield 174	android/view/SurfaceView:mRequestedWidth	I
    //   420: istore 5
    //   422: iload 5
    //   424: istore 4
    //   426: iload 5
    //   428: ifgt +9 -> 437
    //   431: aload_0
    //   432: invokevirtual 484	android/view/SurfaceView:getWidth	()I
    //   435: istore 4
    //   437: aload_0
    //   438: getfield 176	android/view/SurfaceView:mRequestedHeight	I
    //   441: istore 6
    //   443: iload 6
    //   445: istore 5
    //   447: iload 6
    //   449: ifgt +9 -> 458
    //   452: aload_0
    //   453: invokevirtual 487	android/view/SurfaceView:getHeight	()I
    //   456: istore 5
    //   458: aload_0
    //   459: getfield 196	android/view/SurfaceView:mFormat	I
    //   462: aload_0
    //   463: getfield 178	android/view/SurfaceView:mRequestedFormat	I
    //   466: if_icmpeq +9 -> 475
    //   469: iconst_1
    //   470: istore 6
    //   472: goto +6 -> 478
    //   475: iconst_0
    //   476: istore 6
    //   478: aload_0
    //   479: aload_0
    //   480: invokevirtual 689	android/view/SurfaceView:getResources	()Landroid/content/res/Resources;
    //   483: aload_0
    //   484: getfield 150	android/view/SurfaceView:mSubLayer	I
    //   487: aload_0
    //   488: invokevirtual 718	android/view/SurfaceView:getContext	()Landroid/content/Context;
    //   491: invokevirtual 723	android/content/Context:getPackageName	()Ljava/lang/String;
    //   494: invokestatic 729	android/util/MiuiMultiWindowAdapter:getSurfaceViewVisable	(Landroid/view/SurfaceView;Landroid/content/res/Resources;ILjava/lang/String;)V
    //   497: aload_0
    //   498: getfield 186	android/view/SurfaceView:mVisible	Z
    //   501: aload_0
    //   502: getfield 164	android/view/SurfaceView:mRequestedVisible	Z
    //   505: if_icmpeq +9 -> 514
    //   508: iconst_1
    //   509: istore 7
    //   511: goto +6 -> 517
    //   514: iconst_0
    //   515: istore 7
    //   517: aload_0
    //   518: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   521: ifnull +13 -> 534
    //   524: iload 6
    //   526: ifne +8 -> 534
    //   529: iload 7
    //   531: ifeq +16 -> 547
    //   534: aload_0
    //   535: getfield 164	android/view/SurfaceView:mRequestedVisible	Z
    //   538: ifeq +9 -> 547
    //   541: iconst_1
    //   542: istore 8
    //   544: goto +6 -> 550
    //   547: iconst_0
    //   548: istore 8
    //   550: aload_0
    //   551: getfield 192	android/view/SurfaceView:mSurfaceWidth	I
    //   554: iload 4
    //   556: if_icmpne +21 -> 577
    //   559: aload_0
    //   560: getfield 194	android/view/SurfaceView:mSurfaceHeight	I
    //   563: iload 5
    //   565: if_icmpeq +6 -> 571
    //   568: goto +9 -> 577
    //   571: iconst_0
    //   572: istore 9
    //   574: goto +6 -> 580
    //   577: iconst_1
    //   578: istore 9
    //   580: aload_0
    //   581: getfield 166	android/view/SurfaceView:mWindowVisibility	Z
    //   584: aload_0
    //   585: getfield 168	android/view/SurfaceView:mLastWindowVisibility	Z
    //   588: if_icmpeq +9 -> 597
    //   591: iconst_1
    //   592: istore 10
    //   594: goto +6 -> 600
    //   597: iconst_0
    //   598: istore 10
    //   600: iload 8
    //   602: ifne +276 -> 878
    //   605: iload 6
    //   607: ifne +271 -> 878
    //   610: iload 9
    //   612: ifne +266 -> 878
    //   615: iload 7
    //   617: ifne +261 -> 878
    //   620: iload 10
    //   622: ifeq +6 -> 628
    //   625: goto +253 -> 878
    //   628: aload_0
    //   629: aload_0
    //   630: getfield 122	android/view/SurfaceView:mLocation	[I
    //   633: invokevirtual 732	android/view/SurfaceView:getLocationInSurface	([I)V
    //   636: aload_0
    //   637: getfield 188	android/view/SurfaceView:mWindowSpaceLeft	I
    //   640: istore 4
    //   642: aload_0
    //   643: getfield 122	android/view/SurfaceView:mLocation	[I
    //   646: astore_2
    //   647: iload 4
    //   649: aload_2
    //   650: iconst_0
    //   651: iaload
    //   652: if_icmpne +22 -> 674
    //   655: aload_0
    //   656: getfield 190	android/view/SurfaceView:mWindowSpaceTop	I
    //   659: aload_2
    //   660: iconst_1
    //   661: iaload
    //   662: if_icmpeq +6 -> 668
    //   665: goto +9 -> 674
    //   668: iconst_0
    //   669: istore 4
    //   671: goto +6 -> 677
    //   674: iconst_1
    //   675: istore 4
    //   677: aload_0
    //   678: invokevirtual 484	android/view/SurfaceView:getWidth	()I
    //   681: aload_0
    //   682: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   685: invokevirtual 278	android/graphics/Rect:width	()I
    //   688: if_icmpne +26 -> 714
    //   691: aload_0
    //   692: invokevirtual 487	android/view/SurfaceView:getHeight	()I
    //   695: aload_0
    //   696: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   699: invokevirtual 281	android/graphics/Rect:height	()I
    //   702: if_icmpeq +6 -> 708
    //   705: goto +9 -> 714
    //   708: iconst_0
    //   709: istore 5
    //   711: goto +6 -> 717
    //   714: iconst_1
    //   715: istore 5
    //   717: iload 4
    //   719: ifne +14 -> 733
    //   722: iload 5
    //   724: ifeq +6 -> 730
    //   727: goto +6 -> 733
    //   730: goto +145 -> 875
    //   733: aload_0
    //   734: getfield 122	android/view/SurfaceView:mLocation	[I
    //   737: astore_2
    //   738: aload_0
    //   739: aload_2
    //   740: iconst_0
    //   741: iaload
    //   742: putfield 188	android/view/SurfaceView:mWindowSpaceLeft	I
    //   745: aload_0
    //   746: aload_2
    //   747: iconst_1
    //   748: iaload
    //   749: putfield 190	android/view/SurfaceView:mWindowSpaceTop	I
    //   752: aload_2
    //   753: iconst_0
    //   754: aload_0
    //   755: invokevirtual 484	android/view/SurfaceView:getWidth	()I
    //   758: iastore
    //   759: aload_0
    //   760: getfield 122	android/view/SurfaceView:mLocation	[I
    //   763: iconst_1
    //   764: aload_0
    //   765: invokevirtual 487	android/view/SurfaceView:getHeight	()I
    //   768: iastore
    //   769: aload_0
    //   770: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   773: astore_3
    //   774: aload_0
    //   775: getfield 188	android/view/SurfaceView:mWindowSpaceLeft	I
    //   778: istore 4
    //   780: aload_0
    //   781: getfield 190	android/view/SurfaceView:mWindowSpaceTop	I
    //   784: istore 5
    //   786: aload_0
    //   787: getfield 122	android/view/SurfaceView:mLocation	[I
    //   790: astore_2
    //   791: aload_3
    //   792: iload 4
    //   794: iload 5
    //   796: iload 4
    //   798: aload_2
    //   799: iconst_0
    //   800: iaload
    //   801: iadd
    //   802: aload_2
    //   803: iconst_1
    //   804: iaload
    //   805: iload 5
    //   807: iadd
    //   808: invokevirtual 736	android/graphics/Rect:set	(IIII)V
    //   811: aload_0
    //   812: getfield 658	android/view/SurfaceView:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   815: astore_2
    //   816: aload_2
    //   817: ifnull +11 -> 828
    //   820: aload_2
    //   821: aload_0
    //   822: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   825: invokevirtual 742	android/content/res/CompatibilityInfo$Translator:translateRectInAppWindowToScreen	(Landroid/graphics/Rect;)V
    //   828: aload_0
    //   829: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   832: ifnonnull +4 -> 836
    //   835: return
    //   836: aload_0
    //   837: invokevirtual 745	android/view/SurfaceView:isHardwareAccelerated	()Z
    //   840: ifeq +10 -> 850
    //   843: aload_0
    //   844: getfield 154	android/view/SurfaceView:mRtHandlingPositionUpdates	Z
    //   847: ifne +28 -> 875
    //   850: aload_0
    //   851: aload_0
    //   852: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   855: ldc2_w 746
    //   858: invokespecial 243	android/view/SurfaceView:setParentSpaceRectangle	(Landroid/graphics/Rect;J)V
    //   861: goto +14 -> 875
    //   864: astore_2
    //   865: ldc 23
    //   867: ldc_w 749
    //   870: aload_2
    //   871: invokestatic 752	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   874: pop
    //   875: goto +1442 -> 2317
    //   878: aload_0
    //   879: aload_0
    //   880: getfield 122	android/view/SurfaceView:mLocation	[I
    //   883: invokevirtual 491	android/view/SurfaceView:getLocationInWindow	([I)V
    //   886: aload_0
    //   887: getfield 164	android/view/SurfaceView:mRequestedVisible	Z
    //   890: istore 11
    //   892: aload_0
    //   893: iload 11
    //   895: putfield 186	android/view/SurfaceView:mVisible	Z
    //   898: aload_0
    //   899: aload_0
    //   900: getfield 122	android/view/SurfaceView:mLocation	[I
    //   903: iconst_0
    //   904: iaload
    //   905: putfield 188	android/view/SurfaceView:mWindowSpaceLeft	I
    //   908: aload_0
    //   909: aload_0
    //   910: getfield 122	android/view/SurfaceView:mLocation	[I
    //   913: iconst_1
    //   914: iaload
    //   915: putfield 190	android/view/SurfaceView:mWindowSpaceTop	I
    //   918: aload_0
    //   919: iload 4
    //   921: putfield 192	android/view/SurfaceView:mSurfaceWidth	I
    //   924: aload_0
    //   925: iload 5
    //   927: putfield 194	android/view/SurfaceView:mSurfaceHeight	I
    //   930: aload_0
    //   931: aload_0
    //   932: getfield 178	android/view/SurfaceView:mRequestedFormat	I
    //   935: putfield 196	android/view/SurfaceView:mFormat	I
    //   938: aload_0
    //   939: aload_0
    //   940: getfield 166	android/view/SurfaceView:mWindowVisibility	Z
    //   943: putfield 168	android/view/SurfaceView:mLastWindowVisibility	Z
    //   946: aload_0
    //   947: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   950: aload_0
    //   951: getfield 188	android/view/SurfaceView:mWindowSpaceLeft	I
    //   954: putfield 267	android/graphics/Rect:left	I
    //   957: aload_0
    //   958: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   961: aload_0
    //   962: getfield 190	android/view/SurfaceView:mWindowSpaceTop	I
    //   965: putfield 270	android/graphics/Rect:top	I
    //   968: aload_0
    //   969: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   972: aload_0
    //   973: getfield 188	android/view/SurfaceView:mWindowSpaceLeft	I
    //   976: aload_0
    //   977: invokevirtual 484	android/view/SurfaceView:getWidth	()I
    //   980: iadd
    //   981: putfield 302	android/graphics/Rect:right	I
    //   984: aload_0
    //   985: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   988: aload_0
    //   989: getfield 190	android/view/SurfaceView:mWindowSpaceTop	I
    //   992: aload_0
    //   993: invokevirtual 487	android/view/SurfaceView:getHeight	()I
    //   996: iadd
    //   997: putfield 305	android/graphics/Rect:bottom	I
    //   1000: aload_0
    //   1001: getfield 658	android/view/SurfaceView:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   1004: ifnull +14 -> 1018
    //   1007: aload_0
    //   1008: getfield 658	android/view/SurfaceView:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   1011: aload_0
    //   1012: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   1015: invokevirtual 742	android/content/res/CompatibilityInfo$Translator:translateRectInAppWindowToScreen	(Landroid/graphics/Rect;)V
    //   1018: aload_0
    //   1019: invokespecial 754	android/view/SurfaceView:getParentSurfaceInsets	()Landroid/graphics/Rect;
    //   1022: astore_2
    //   1023: aload_0
    //   1024: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   1027: aload_2
    //   1028: getfield 267	android/graphics/Rect:left	I
    //   1031: aload_2
    //   1032: getfield 270	android/graphics/Rect:top	I
    //   1035: invokevirtual 757	android/graphics/Rect:offset	(II)V
    //   1038: iload 8
    //   1040: ifeq +223 -> 1263
    //   1043: aload_1
    //   1044: aload_0
    //   1045: getfield 150	android/view/SurfaceView:mSubLayer	I
    //   1048: invokevirtual 760	android/view/ViewRootImpl:createBoundsSurface	(I)V
    //   1051: new 762	android/view/SurfaceSession
    //   1054: astore_2
    //   1055: aload_2
    //   1056: invokespecial 763	android/view/SurfaceSession:<init>	()V
    //   1059: aload_0
    //   1060: aload_2
    //   1061: putfield 765	android/view/SurfaceView:mSurfaceSession	Landroid/view/SurfaceSession;
    //   1064: aload_0
    //   1065: aload_0
    //   1066: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1069: putfield 355	android/view/SurfaceView:mDeferredDestroySurfaceControl	Landroid/view/SurfaceControl;
    //   1072: aload_0
    //   1073: invokespecial 767	android/view/SurfaceView:updateOpaqueFlag	()V
    //   1076: new 376	java/lang/StringBuilder
    //   1079: astore_2
    //   1080: aload_2
    //   1081: invokespecial 377	java/lang/StringBuilder:<init>	()V
    //   1084: aload_2
    //   1085: ldc_w 769
    //   1088: invokevirtual 392	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1091: pop
    //   1092: aload_2
    //   1093: aload_1
    //   1094: invokevirtual 773	android/view/ViewRootImpl:getTitle	()Ljava/lang/CharSequence;
    //   1097: invokeinterface 776 1 0
    //   1102: invokevirtual 392	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1105: pop
    //   1106: aload_2
    //   1107: invokevirtual 396	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1110: astore_2
    //   1111: new 778	android/view/SurfaceControl$Builder
    //   1114: astore_3
    //   1115: aload_3
    //   1116: aload_0
    //   1117: getfield 765	android/view/SurfaceView:mSurfaceSession	Landroid/view/SurfaceSession;
    //   1120: invokespecial 781	android/view/SurfaceControl$Builder:<init>	(Landroid/view/SurfaceSession;)V
    //   1123: aload_3
    //   1124: aload_2
    //   1125: invokevirtual 785	android/view/SurfaceControl$Builder:setName	(Ljava/lang/String;)Landroid/view/SurfaceControl$Builder;
    //   1128: astore_3
    //   1129: aload_0
    //   1130: getfield 206	android/view/SurfaceView:mSurfaceFlags	I
    //   1133: sipush 1024
    //   1136: iand
    //   1137: ifeq +9 -> 1146
    //   1140: iconst_1
    //   1141: istore 12
    //   1143: goto +6 -> 1149
    //   1146: iconst_0
    //   1147: istore 12
    //   1149: aload_0
    //   1150: aload_3
    //   1151: iload 12
    //   1153: invokevirtual 789	android/view/SurfaceControl$Builder:setOpaque	(Z)Landroid/view/SurfaceControl$Builder;
    //   1156: aload_0
    //   1157: getfield 192	android/view/SurfaceView:mSurfaceWidth	I
    //   1160: aload_0
    //   1161: getfield 194	android/view/SurfaceView:mSurfaceHeight	I
    //   1164: invokevirtual 793	android/view/SurfaceControl$Builder:setBufferSize	(II)Landroid/view/SurfaceControl$Builder;
    //   1167: aload_0
    //   1168: getfield 196	android/view/SurfaceView:mFormat	I
    //   1171: invokevirtual 797	android/view/SurfaceControl$Builder:setFormat	(I)Landroid/view/SurfaceControl$Builder;
    //   1174: aload_1
    //   1175: invokevirtual 799	android/view/ViewRootImpl:getSurfaceControl	()Landroid/view/SurfaceControl;
    //   1178: invokevirtual 803	android/view/SurfaceControl$Builder:setParent	(Landroid/view/SurfaceControl;)Landroid/view/SurfaceControl$Builder;
    //   1181: aload_0
    //   1182: getfield 206	android/view/SurfaceView:mSurfaceFlags	I
    //   1185: invokevirtual 806	android/view/SurfaceControl$Builder:setFlags	(I)Landroid/view/SurfaceControl$Builder;
    //   1188: invokevirtual 809	android/view/SurfaceControl$Builder:build	()Landroid/view/SurfaceControl;
    //   1191: putfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1194: new 778	android/view/SurfaceControl$Builder
    //   1197: astore 13
    //   1199: aload 13
    //   1201: aload_0
    //   1202: getfield 765	android/view/SurfaceView:mSurfaceSession	Landroid/view/SurfaceSession;
    //   1205: invokespecial 781	android/view/SurfaceControl$Builder:<init>	(Landroid/view/SurfaceSession;)V
    //   1208: new 376	java/lang/StringBuilder
    //   1211: astore_3
    //   1212: aload_3
    //   1213: invokespecial 377	java/lang/StringBuilder:<init>	()V
    //   1216: aload_3
    //   1217: ldc_w 811
    //   1220: invokevirtual 392	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1223: pop
    //   1224: aload_3
    //   1225: aload_2
    //   1226: invokevirtual 392	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1229: pop
    //   1230: aload_0
    //   1231: aload 13
    //   1233: aload_3
    //   1234: invokevirtual 396	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1237: invokevirtual 785	android/view/SurfaceControl$Builder:setName	(Ljava/lang/String;)Landroid/view/SurfaceControl$Builder;
    //   1240: iconst_1
    //   1241: invokevirtual 789	android/view/SurfaceControl$Builder:setOpaque	(Z)Landroid/view/SurfaceControl$Builder;
    //   1244: invokevirtual 815	android/view/SurfaceControl$Builder:setColorLayer	()Landroid/view/SurfaceControl$Builder;
    //   1247: aload_0
    //   1248: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1251: invokevirtual 803	android/view/SurfaceControl$Builder:setParent	(Landroid/view/SurfaceControl;)Landroid/view/SurfaceControl$Builder;
    //   1254: invokevirtual 809	android/view/SurfaceControl$Builder:build	()Landroid/view/SurfaceControl;
    //   1257: putfield 407	android/view/SurfaceView:mBackgroundControl	Landroid/view/SurfaceControl;
    //   1260: goto +11 -> 1271
    //   1263: aload_0
    //   1264: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1267: ifnonnull +4 -> 1271
    //   1270: return
    //   1271: aload_0
    //   1272: getfield 127	android/view/SurfaceView:mSurfaceLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   1275: invokevirtual 818	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   1278: iload 11
    //   1280: ifne +9 -> 1289
    //   1283: iconst_1
    //   1284: istore 12
    //   1286: goto +6 -> 1292
    //   1289: iconst_0
    //   1290: istore 12
    //   1292: aload_0
    //   1293: iload 12
    //   1295: putfield 134	android/view/SurfaceView:mDrawingStopped	Z
    //   1298: invokestatic 632	android/view/SurfaceControl:openTransaction	()V
    //   1301: aload_0
    //   1302: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1305: aload_0
    //   1306: getfield 150	android/view/SurfaceView:mSubLayer	I
    //   1309: invokevirtual 821	android/view/SurfaceControl:setLayer	(I)V
    //   1312: aload_0
    //   1313: getfield 170	android/view/SurfaceView:mViewVisibility	Z
    //   1316: istore 12
    //   1318: iload 12
    //   1320: ifeq +17 -> 1337
    //   1323: aload_0
    //   1324: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1327: invokevirtual 444	android/view/SurfaceControl:show	()V
    //   1330: goto +14 -> 1344
    //   1333: astore_2
    //   1334: goto +949 -> 2283
    //   1337: aload_0
    //   1338: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1341: invokevirtual 452	android/view/SurfaceControl:hide	()V
    //   1344: aload_0
    //   1345: aload_1
    //   1346: invokevirtual 799	android/view/ViewRootImpl:getSurfaceControl	()Landroid/view/SurfaceControl;
    //   1349: invokespecial 823	android/view/SurfaceView:updateBackgroundVisibilityInTransaction	(Landroid/view/SurfaceControl;)V
    //   1352: iload 9
    //   1354: ifne +25 -> 1379
    //   1357: iload 8
    //   1359: ifne +20 -> 1379
    //   1362: aload_0
    //   1363: getfield 154	android/view/SurfaceView:mRtHandlingPositionUpdates	Z
    //   1366: istore 12
    //   1368: iload 12
    //   1370: ifne +6 -> 1376
    //   1373: goto +6 -> 1379
    //   1376: goto +92 -> 1468
    //   1379: aload_0
    //   1380: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1383: aload_0
    //   1384: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   1387: getfield 267	android/graphics/Rect:left	I
    //   1390: i2f
    //   1391: aload_0
    //   1392: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   1395: getfield 270	android/graphics/Rect:top	I
    //   1398: i2f
    //   1399: invokevirtual 826	android/view/SurfaceControl:setPosition	(FF)V
    //   1402: aload_0
    //   1403: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1406: astore_2
    //   1407: aload_0
    //   1408: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   1411: invokevirtual 278	android/graphics/Rect:width	()I
    //   1414: i2f
    //   1415: aload_0
    //   1416: getfield 192	android/view/SurfaceView:mSurfaceWidth	I
    //   1419: i2f
    //   1420: fdiv
    //   1421: fstore 14
    //   1423: aload_0
    //   1424: getfield 141	android/view/SurfaceView:mScreenRect	Landroid/graphics/Rect;
    //   1427: invokevirtual 281	android/graphics/Rect:height	()I
    //   1430: istore 10
    //   1432: iload 10
    //   1434: i2f
    //   1435: fstore 15
    //   1437: aload_2
    //   1438: fload 14
    //   1440: fconst_0
    //   1441: fconst_0
    //   1442: fload 15
    //   1444: aload_0
    //   1445: getfield 194	android/view/SurfaceView:mSurfaceHeight	I
    //   1448: i2f
    //   1449: fdiv
    //   1450: invokevirtual 829	android/view/SurfaceControl:setMatrix	(FFFF)V
    //   1453: aload_0
    //   1454: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1457: aload_0
    //   1458: getfield 192	android/view/SurfaceView:mSurfaceWidth	I
    //   1461: aload_0
    //   1462: getfield 194	android/view/SurfaceView:mSurfaceHeight	I
    //   1465: invokevirtual 832	android/view/SurfaceControl:setWindowCrop	(II)V
    //   1468: aload_0
    //   1469: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1472: aload_0
    //   1473: getfield 293	android/view/SurfaceView:mCornerRadius	F
    //   1476: invokevirtual 834	android/view/SurfaceControl:setCornerRadius	(F)V
    //   1479: iload 9
    //   1481: ifeq +30 -> 1511
    //   1484: iload 8
    //   1486: ifne +25 -> 1511
    //   1489: aload_0
    //   1490: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1493: aload_0
    //   1494: getfield 192	android/view/SurfaceView:mSurfaceWidth	I
    //   1497: aload_0
    //   1498: getfield 194	android/view/SurfaceView:mSurfaceHeight	I
    //   1501: invokevirtual 836	android/view/SurfaceControl:setBufferSize	(II)V
    //   1504: goto +7 -> 1511
    //   1507: astore_2
    //   1508: goto +775 -> 2283
    //   1511: invokestatic 638	android/view/SurfaceControl:closeTransaction	()V
    //   1514: iload 9
    //   1516: ifne +17 -> 1533
    //   1519: iload 8
    //   1521: ifeq +6 -> 1527
    //   1524: goto +9 -> 1533
    //   1527: iconst_0
    //   1528: istore 10
    //   1530: goto +6 -> 1536
    //   1533: iconst_1
    //   1534: istore 10
    //   1536: aload_0
    //   1537: getfield 198	android/view/SurfaceView:mSurfaceFrame	Landroid/graphics/Rect;
    //   1540: iconst_0
    //   1541: putfield 267	android/graphics/Rect:left	I
    //   1544: aload_0
    //   1545: getfield 198	android/view/SurfaceView:mSurfaceFrame	Landroid/graphics/Rect;
    //   1548: iconst_0
    //   1549: putfield 270	android/graphics/Rect:top	I
    //   1552: aload_0
    //   1553: getfield 658	android/view/SurfaceView:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   1556: astore_2
    //   1557: aload_2
    //   1558: ifnonnull +32 -> 1590
    //   1561: aload_0
    //   1562: getfield 198	android/view/SurfaceView:mSurfaceFrame	Landroid/graphics/Rect;
    //   1565: aload_0
    //   1566: getfield 192	android/view/SurfaceView:mSurfaceWidth	I
    //   1569: putfield 302	android/graphics/Rect:right	I
    //   1572: aload_0
    //   1573: getfield 198	android/view/SurfaceView:mSurfaceFrame	Landroid/graphics/Rect;
    //   1576: aload_0
    //   1577: getfield 194	android/view/SurfaceView:mSurfaceHeight	I
    //   1580: putfield 305	android/graphics/Rect:bottom	I
    //   1583: goto +56 -> 1639
    //   1586: astore_2
    //   1587: goto +706 -> 2293
    //   1590: aload_0
    //   1591: getfield 658	android/view/SurfaceView:mTranslator	Landroid/content/res/CompatibilityInfo$Translator;
    //   1594: getfield 839	android/content/res/CompatibilityInfo$Translator:applicationInvertedScale	F
    //   1597: fstore 15
    //   1599: aload_0
    //   1600: getfield 198	android/view/SurfaceView:mSurfaceFrame	Landroid/graphics/Rect;
    //   1603: aload_0
    //   1604: getfield 192	android/view/SurfaceView:mSurfaceWidth	I
    //   1607: i2f
    //   1608: fload 15
    //   1610: fmul
    //   1611: ldc_w 840
    //   1614: fadd
    //   1615: f2i
    //   1616: putfield 302	android/graphics/Rect:right	I
    //   1619: aload_0
    //   1620: getfield 198	android/view/SurfaceView:mSurfaceFrame	Landroid/graphics/Rect;
    //   1623: aload_0
    //   1624: getfield 194	android/view/SurfaceView:mSurfaceHeight	I
    //   1627: i2f
    //   1628: fload 15
    //   1630: fmul
    //   1631: ldc_w 840
    //   1634: fadd
    //   1635: f2i
    //   1636: putfield 305	android/graphics/Rect:bottom	I
    //   1639: aload_0
    //   1640: getfield 198	android/view/SurfaceView:mSurfaceFrame	Landroid/graphics/Rect;
    //   1643: getfield 302	android/graphics/Rect:right	I
    //   1646: istore 16
    //   1648: aload_0
    //   1649: getfield 198	android/view/SurfaceView:mSurfaceFrame	Landroid/graphics/Rect;
    //   1652: getfield 305	android/graphics/Rect:bottom	I
    //   1655: istore 17
    //   1657: aload_0
    //   1658: getfield 200	android/view/SurfaceView:mLastSurfaceWidth	I
    //   1661: istore 18
    //   1663: iload 18
    //   1665: iload 16
    //   1667: if_icmpne +25 -> 1692
    //   1670: aload_0
    //   1671: getfield 202	android/view/SurfaceView:mLastSurfaceHeight	I
    //   1674: istore 18
    //   1676: iload 18
    //   1678: iload 17
    //   1680: if_icmpeq +6 -> 1686
    //   1683: goto +9 -> 1692
    //   1686: iconst_0
    //   1687: istore 18
    //   1689: goto +6 -> 1695
    //   1692: iconst_1
    //   1693: istore 18
    //   1695: aload_0
    //   1696: iload 16
    //   1698: putfield 200	android/view/SurfaceView:mLastSurfaceWidth	I
    //   1701: aload_0
    //   1702: iload 17
    //   1704: putfield 202	android/view/SurfaceView:mLastSurfaceHeight	I
    //   1707: aload_0
    //   1708: getfield 127	android/view/SurfaceView:mSurfaceLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   1711: invokevirtual 843	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   1714: iload 11
    //   1716: ifeq +24 -> 1740
    //   1719: aload_0
    //   1720: getfield 136	android/view/SurfaceView:mDrawFinished	Z
    //   1723: istore 12
    //   1725: iload 12
    //   1727: ifne +13 -> 1740
    //   1730: iconst_1
    //   1731: istore 16
    //   1733: goto +10 -> 1743
    //   1736: astore_2
    //   1737: goto +497 -> 2234
    //   1740: iconst_0
    //   1741: istore 16
    //   1743: aconst_null
    //   1744: astore_3
    //   1745: aload_0
    //   1746: getfield 182	android/view/SurfaceView:mSurfaceCreated	Z
    //   1749: istore 12
    //   1751: iload 12
    //   1753: ifeq +109 -> 1862
    //   1756: iload 8
    //   1758: ifne +26 -> 1784
    //   1761: iload 11
    //   1763: ifne +18 -> 1781
    //   1766: iload 7
    //   1768: ifeq +13 -> 1781
    //   1771: aload_1
    //   1772: getfield 665	android/view/ViewRootImpl:mIsCastMode	Z
    //   1775: ifne +6 -> 1781
    //   1778: goto +6 -> 1784
    //   1781: goto +81 -> 1862
    //   1784: aload_0
    //   1785: iconst_0
    //   1786: putfield 182	android/view/SurfaceView:mSurfaceCreated	Z
    //   1789: aload_0
    //   1790: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   1793: invokevirtual 655	android/view/Surface:isValid	()Z
    //   1796: ifeq +63 -> 1859
    //   1799: aload_0
    //   1800: invokespecial 845	android/view/SurfaceView:getSurfaceCallbacks	()[Landroid/view/SurfaceHolder$Callback;
    //   1803: astore_2
    //   1804: aload_2
    //   1805: arraylength
    //   1806: istore 17
    //   1808: iconst_0
    //   1809: istore 19
    //   1811: iload 19
    //   1813: iload 17
    //   1815: if_icmpge +22 -> 1837
    //   1818: aload_2
    //   1819: iload 19
    //   1821: aaload
    //   1822: aload_0
    //   1823: getfield 219	android/view/SurfaceView:mSurfaceHolder	Landroid/view/SurfaceHolder;
    //   1826: invokeinterface 849 2 0
    //   1831: iinc 19 1
    //   1834: goto -23 -> 1811
    //   1837: aload_0
    //   1838: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   1841: invokevirtual 655	android/view/Surface:isValid	()Z
    //   1844: ifeq +10 -> 1854
    //   1847: aload_0
    //   1848: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   1851: invokevirtual 852	android/view/Surface:forceScopedDisconnect	()V
    //   1854: aload_2
    //   1855: astore_3
    //   1856: goto +6 -> 1862
    //   1859: goto +3 -> 1862
    //   1862: iload 8
    //   1864: ifeq +21 -> 1885
    //   1867: aload_0
    //   1868: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   1871: aload_0
    //   1872: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1875: invokevirtual 855	android/view/Surface:copyFrom	(Landroid/view/SurfaceControl;)V
    //   1878: goto +7 -> 1885
    //   1881: astore_2
    //   1882: goto -145 -> 1737
    //   1885: iload 9
    //   1887: ifeq +29 -> 1916
    //   1890: aload_0
    //   1891: invokevirtual 718	android/view/SurfaceView:getContext	()Landroid/content/Context;
    //   1894: invokevirtual 859	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   1897: getfield 864	android/content/pm/ApplicationInfo:targetSdkVersion	I
    //   1900: bipush 26
    //   1902: if_icmpge +14 -> 1916
    //   1905: aload_0
    //   1906: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   1909: aload_0
    //   1910: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   1913: invokevirtual 867	android/view/Surface:createFrom	(Landroid/view/SurfaceControl;)V
    //   1916: iload 11
    //   1918: ifeq +282 -> 2200
    //   1921: iload 11
    //   1923: istore 12
    //   1925: aload_0
    //   1926: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   1929: invokevirtual 655	android/view/Surface:isValid	()Z
    //   1932: ifeq +268 -> 2200
    //   1935: iload 11
    //   1937: istore 12
    //   1939: aload_0
    //   1940: getfield 182	android/view/SurfaceView:mSurfaceCreated	Z
    //   1943: istore 20
    //   1945: aload_3
    //   1946: astore_2
    //   1947: iload 20
    //   1949: ifne +69 -> 2018
    //   1952: iload 8
    //   1954: ifne +10 -> 1964
    //   1957: aload_3
    //   1958: astore_2
    //   1959: iload 7
    //   1961: ifeq +57 -> 2018
    //   1964: aload_0
    //   1965: iconst_1
    //   1966: putfield 182	android/view/SurfaceView:mSurfaceCreated	Z
    //   1969: aload_0
    //   1970: iconst_1
    //   1971: putfield 152	android/view/SurfaceView:mIsCreating	Z
    //   1974: aload_3
    //   1975: astore_2
    //   1976: aload_3
    //   1977: ifnonnull +8 -> 1985
    //   1980: aload_0
    //   1981: invokespecial 845	android/view/SurfaceView:getSurfaceCallbacks	()[Landroid/view/SurfaceHolder$Callback;
    //   1984: astore_2
    //   1985: aload_2
    //   1986: arraylength
    //   1987: istore 19
    //   1989: iconst_0
    //   1990: istore 17
    //   1992: iload 17
    //   1994: iload 19
    //   1996: if_icmpge +22 -> 2018
    //   1999: aload_2
    //   2000: iload 17
    //   2002: aaload
    //   2003: aload_0
    //   2004: getfield 219	android/view/SurfaceView:mSurfaceHolder	Landroid/view/SurfaceHolder;
    //   2007: invokeinterface 870 2 0
    //   2012: iinc 17 1
    //   2015: goto -23 -> 1992
    //   2018: iload 8
    //   2020: ifne +29 -> 2049
    //   2023: iload 6
    //   2025: ifne +24 -> 2049
    //   2028: iload 9
    //   2030: ifne +19 -> 2049
    //   2033: iload 7
    //   2035: ifne +14 -> 2049
    //   2038: iload 18
    //   2040: ifeq +6 -> 2046
    //   2043: goto +6 -> 2049
    //   2046: goto +79 -> 2125
    //   2049: aload_2
    //   2050: astore_3
    //   2051: aload_2
    //   2052: ifnonnull +8 -> 2060
    //   2055: aload_0
    //   2056: invokespecial 845	android/view/SurfaceView:getSurfaceCallbacks	()[Landroid/view/SurfaceHolder$Callback;
    //   2059: astore_3
    //   2060: iload 11
    //   2062: istore 12
    //   2064: aload_3
    //   2065: arraylength
    //   2066: istore 7
    //   2068: iconst_0
    //   2069: istore 6
    //   2071: aload_3
    //   2072: astore_2
    //   2073: iload 6
    //   2075: iload 7
    //   2077: if_icmpge +48 -> 2125
    //   2080: aload_2
    //   2081: iload 6
    //   2083: aaload
    //   2084: astore 13
    //   2086: iload 11
    //   2088: istore 12
    //   2090: aload_0
    //   2091: getfield 219	android/view/SurfaceView:mSurfaceHolder	Landroid/view/SurfaceHolder;
    //   2094: astore_3
    //   2095: iload 11
    //   2097: istore 12
    //   2099: aload_0
    //   2100: getfield 196	android/view/SurfaceView:mFormat	I
    //   2103: istore 8
    //   2105: aload 13
    //   2107: aload_3
    //   2108: iload 8
    //   2110: iload 4
    //   2112: iload 5
    //   2114: invokeinterface 874 5 0
    //   2119: iinc 6 1
    //   2122: goto -49 -> 2073
    //   2125: iload 10
    //   2127: iload 16
    //   2129: ior
    //   2130: ifeq +70 -> 2200
    //   2133: aload_2
    //   2134: astore_3
    //   2135: aload_2
    //   2136: ifnonnull +8 -> 2144
    //   2139: aload_0
    //   2140: invokespecial 845	android/view/SurfaceView:getSurfaceCallbacks	()[Landroid/view/SurfaceHolder$Callback;
    //   2143: astore_3
    //   2144: aload_0
    //   2145: aload_0
    //   2146: getfield 366	android/view/SurfaceView:mPendingReportDraws	I
    //   2149: iconst_1
    //   2150: iadd
    //   2151: putfield 366	android/view/SurfaceView:mPendingReportDraws	I
    //   2154: aload_1
    //   2155: invokevirtual 877	android/view/ViewRootImpl:drawPending	()V
    //   2158: new 879	com/android/internal/view/SurfaceCallbackHelper
    //   2161: astore_2
    //   2162: new 881	android/view/_$$Lambda$SurfaceView$SyyzxOgxKwZMRgiiTGcRYbOU5JY
    //   2165: astore_1
    //   2166: aload_1
    //   2167: aload_0
    //   2168: invokespecial 882	android/view/_$$Lambda$SurfaceView$SyyzxOgxKwZMRgiiTGcRYbOU5JY:<init>	(Landroid/view/SurfaceView;)V
    //   2171: aload_2
    //   2172: aload_1
    //   2173: invokespecial 884	com/android/internal/view/SurfaceCallbackHelper:<init>	(Ljava/lang/Runnable;)V
    //   2176: aload_2
    //   2177: aload_0
    //   2178: getfield 219	android/view/SurfaceView:mSurfaceHolder	Landroid/view/SurfaceHolder;
    //   2181: aload_3
    //   2182: invokevirtual 888	com/android/internal/view/SurfaceCallbackHelper:dispatchSurfaceRedrawNeededAsync	(Landroid/view/SurfaceHolder;[Landroid/view/SurfaceHolder$Callback;)V
    //   2185: goto +15 -> 2200
    //   2188: astore_2
    //   2189: goto +45 -> 2234
    //   2192: astore_2
    //   2193: iload 12
    //   2195: istore 11
    //   2197: goto +37 -> 2234
    //   2200: aload_0
    //   2201: iconst_0
    //   2202: putfield 152	android/view/SurfaceView:mIsCreating	Z
    //   2205: aload_0
    //   2206: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   2209: ifnull +108 -> 2317
    //   2212: aload_0
    //   2213: getfield 182	android/view/SurfaceView:mSurfaceCreated	Z
    //   2216: ifne +101 -> 2317
    //   2219: aload_0
    //   2220: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   2223: invokevirtual 891	android/view/Surface:release	()V
    //   2226: aload_0
    //   2227: invokespecial 893	android/view/SurfaceView:releaseSurfaces	()V
    //   2230: goto +87 -> 2317
    //   2233: astore_2
    //   2234: aload_0
    //   2235: iconst_0
    //   2236: putfield 152	android/view/SurfaceView:mIsCreating	Z
    //   2239: aload_0
    //   2240: getfield 405	android/view/SurfaceView:mSurfaceControl	Landroid/view/SurfaceControl;
    //   2243: ifnull +21 -> 2264
    //   2246: aload_0
    //   2247: getfield 182	android/view/SurfaceView:mSurfaceCreated	Z
    //   2250: ifne +14 -> 2264
    //   2253: aload_0
    //   2254: getfield 132	android/view/SurfaceView:mSurface	Landroid/view/Surface;
    //   2257: invokevirtual 891	android/view/Surface:release	()V
    //   2260: aload_0
    //   2261: invokespecial 893	android/view/SurfaceView:releaseSurfaces	()V
    //   2264: aload_2
    //   2265: athrow
    //   2266: astore_2
    //   2267: goto +40 -> 2307
    //   2270: astore_2
    //   2271: goto +22 -> 2293
    //   2274: astore_2
    //   2275: goto +18 -> 2293
    //   2278: astore_2
    //   2279: goto +4 -> 2283
    //   2282: astore_2
    //   2283: invokestatic 638	android/view/SurfaceControl:closeTransaction	()V
    //   2286: aload_2
    //   2287: athrow
    //   2288: astore_2
    //   2289: goto +4 -> 2293
    //   2292: astore_2
    //   2293: aload_0
    //   2294: getfield 127	android/view/SurfaceView:mSurfaceLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   2297: invokevirtual 843	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   2300: aload_2
    //   2301: athrow
    //   2302: astore_2
    //   2303: goto +4 -> 2307
    //   2306: astore_2
    //   2307: ldc 23
    //   2309: ldc_w 749
    //   2312: aload_2
    //   2313: invokestatic 752	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   2316: pop
    //   2317: return
    //   2318: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2319	0	this	SurfaceView
    //   12	2161	1	localObject1	Object
    //   49	772	2	localObject2	Object
    //   864	7	2	localException1	Exception
    //   1022	204	2	localObject3	Object
    //   1333	1	2	localObject4	Object
    //   1406	32	2	localSurfaceControl	SurfaceControl
    //   1507	1	2	localObject5	Object
    //   1556	2	2	localTranslator	CompatibilityInfo.Translator
    //   1586	1	2	localObject6	Object
    //   1736	1	2	localObject7	Object
    //   1803	52	2	arrayOfCallback	SurfaceHolder.Callback[]
    //   1881	1	2	localObject8	Object
    //   1946	231	2	localObject9	Object
    //   2188	1	2	localObject10	Object
    //   2192	1	2	localObject11	Object
    //   2233	32	2	localObject12	Object
    //   2266	1	2	localException2	Exception
    //   2270	1	2	localObject13	Object
    //   2274	1	2	localObject14	Object
    //   2278	1	2	localObject15	Object
    //   2282	5	2	localObject16	Object
    //   2288	1	2	localObject17	Object
    //   2292	9	2	localObject18	Object
    //   2302	1	2	localException3	Exception
    //   2306	7	2	localException4	Exception
    //   157	2025	3	localObject19	Object
    //   248	1863	4	i	int
    //   420	1693	5	j	int
    //   441	1679	6	k	int
    //   509	1569	7	m	int
    //   542	1567	8	n	int
    //   572	1457	9	i1	int
    //   592	1538	10	i2	int
    //   890	1306	11	bool1	boolean
    //   1141	1053	12	bool2	boolean
    //   1197	909	13	localBuilder	SurfaceControl.Builder
    //   1421	18	14	f1	float
    //   1435	194	15	f2	float
    //   1646	484	16	i3	int
    //   1655	358	17	i4	int
    //   1661	378	18	i5	int
    //   1809	188	19	i6	int
    //   1943	5	20	bool3	boolean
    // Exception table:
    //   from	to	target	type
    //   850	861	864	java/lang/Exception
    //   1323	1330	1333	finally
    //   1362	1368	1333	finally
    //   1489	1504	1507	finally
    //   1561	1583	1586	finally
    //   1670	1676	1586	finally
    //   1719	1725	1736	finally
    //   1771	1778	1736	finally
    //   1784	1808	1736	finally
    //   1818	1831	1881	finally
    //   1837	1854	1881	finally
    //   1867	1878	1881	finally
    //   1890	1916	1881	finally
    //   1964	1974	1881	finally
    //   1980	1985	1881	finally
    //   1985	1989	1881	finally
    //   1999	2012	1881	finally
    //   2055	2060	1881	finally
    //   2105	2119	2188	finally
    //   2139	2144	2188	finally
    //   2144	2185	2188	finally
    //   1925	1935	2192	finally
    //   1939	1945	2192	finally
    //   2064	2068	2192	finally
    //   2090	2095	2192	finally
    //   2099	2105	2192	finally
    //   1745	1751	2233	finally
    //   1707	1714	2266	java/lang/Exception
    //   1536	1557	2270	finally
    //   1590	1639	2270	finally
    //   1639	1663	2270	finally
    //   1695	1707	2270	finally
    //   1511	1514	2274	finally
    //   1437	1468	2278	finally
    //   1468	1479	2278	finally
    //   1301	1318	2282	finally
    //   1337	1344	2282	finally
    //   1344	1352	2282	finally
    //   1379	1432	2282	finally
    //   2283	2288	2288	finally
    //   1292	1301	2292	finally
    //   2200	2230	2302	java/lang/Exception
    //   2234	2264	2302	java/lang/Exception
    //   2264	2266	2302	java/lang/Exception
    //   2293	2302	2302	java/lang/Exception
    //   886	898	2306	java/lang/Exception
    //   898	1018	2306	java/lang/Exception
    //   1018	1038	2306	java/lang/Exception
    //   1043	1140	2306	java/lang/Exception
    //   1149	1260	2306	java/lang/Exception
    //   1263	1270	2306	java/lang/Exception
    //   1271	1278	2306	java/lang/Exception
  }
  
  public void windowStopped(boolean paramBoolean)
  {
    this.mWindowStopped = paramBoolean;
    updateRequestedVisibility();
    updateSurface();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/SurfaceView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */