package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.HardwareRenderer.FrameDrawingCallback;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceControl.Builder;
import android.view.SurfaceHolder;
import android.view.SurfaceSession;
import android.view.SurfaceView;
import android.view.ThreadedRenderer.SimpleRenderer;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.WindowInsets;
import android.view.WindowManager.LayoutParams;
import com.android.internal.R.styleable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Magnifier
{
  private static final int NONEXISTENT_PREVIOUS_CONFIG_VALUE = -1;
  public static final int SOURCE_BOUND_MAX_IN_SURFACE = 0;
  public static final int SOURCE_BOUND_MAX_VISIBLE = 1;
  private static final String TAG = "Magnifier";
  private static final HandlerThread sPixelCopyHandlerThread = new HandlerThread("magnifier pixel copy result handler");
  private int mBottomContentBound;
  private Callback mCallback;
  private final Point mClampedCenterZoomCoords = new Point();
  private final boolean mClippingEnabled;
  private SurfaceInfo mContentCopySurface;
  private final int mDefaultHorizontalSourceToMagnifierOffset;
  private final int mDefaultVerticalSourceToMagnifierOffset;
  private final Object mDestroyLock = new Object();
  private boolean mDirtyState;
  private int mLeftContentBound;
  private final Object mLock = new Object();
  private final Drawable mOverlay;
  private SurfaceInfo mParentSurface;
  private final Rect mPixelCopyRequestRect = new Rect();
  private final PointF mPrevShowSourceCoords = new PointF(-1.0F, -1.0F);
  private final PointF mPrevShowWindowCoords = new PointF(-1.0F, -1.0F);
  private final Point mPrevStartCoordsInSurface = new Point(-1, -1);
  private int mRightContentBound;
  private int mSourceHeight;
  private int mSourceWidth;
  private int mTopContentBound;
  private final View mView;
  private final int[] mViewCoordinatesInSurface;
  private InternalPopupWindow mWindow;
  private final Point mWindowCoords = new Point();
  private final float mWindowCornerRadius;
  private final float mWindowElevation;
  private final int mWindowHeight;
  private final int mWindowWidth;
  private float mZoom;
  
  static
  {
    sPixelCopyHandlerThread.start();
  }
  
  @Deprecated
  public Magnifier(View paramView)
  {
    this(createBuilderWithOldMagnifierDefaults(paramView));
  }
  
  private Magnifier(Builder paramBuilder)
  {
    this.mView = paramBuilder.mView;
    this.mWindowWidth = paramBuilder.mWidth;
    this.mWindowHeight = paramBuilder.mHeight;
    this.mZoom = paramBuilder.mZoom;
    this.mSourceWidth = Math.round(this.mWindowWidth / this.mZoom);
    this.mSourceHeight = Math.round(this.mWindowHeight / this.mZoom);
    this.mWindowElevation = paramBuilder.mElevation;
    this.mWindowCornerRadius = paramBuilder.mCornerRadius;
    this.mOverlay = paramBuilder.mOverlay;
    this.mDefaultHorizontalSourceToMagnifierOffset = paramBuilder.mHorizontalDefaultSourceToMagnifierOffset;
    this.mDefaultVerticalSourceToMagnifierOffset = paramBuilder.mVerticalDefaultSourceToMagnifierOffset;
    this.mClippingEnabled = paramBuilder.mClippingEnabled;
    this.mLeftContentBound = paramBuilder.mLeftContentBound;
    this.mTopContentBound = paramBuilder.mTopContentBound;
    this.mRightContentBound = paramBuilder.mRightContentBound;
    this.mBottomContentBound = paramBuilder.mBottomContentBound;
    this.mViewCoordinatesInSurface = new int[2];
  }
  
  static Builder createBuilderWithOldMagnifierDefaults(View paramView)
  {
    Builder localBuilder = new Builder(paramView);
    paramView = paramView.getContext();
    TypedArray localTypedArray = paramView.obtainStyledAttributes(null, R.styleable.Magnifier, 17956981, 0);
    Builder.access$002(localBuilder, localTypedArray.getDimensionPixelSize(5, 0));
    Builder.access$102(localBuilder, localTypedArray.getDimensionPixelSize(2, 0));
    Builder.access$202(localBuilder, localTypedArray.getDimension(1, 0.0F));
    Builder.access$302(localBuilder, getDeviceDefaultDialogCornerRadius(paramView));
    Builder.access$402(localBuilder, localTypedArray.getFloat(6, 0.0F));
    Builder.access$502(localBuilder, localTypedArray.getDimensionPixelSize(3, 0));
    Builder.access$602(localBuilder, localTypedArray.getDimensionPixelSize(4, 0));
    Builder.access$702(localBuilder, new ColorDrawable(localTypedArray.getColor(0, 0)));
    localTypedArray.recycle();
    Builder.access$802(localBuilder, true);
    Builder.access$902(localBuilder, 1);
    Builder.access$1002(localBuilder, 0);
    Builder.access$1102(localBuilder, 1);
    Builder.access$1202(localBuilder, 0);
    return localBuilder;
  }
  
  private Point getCurrentClampedWindowCoordinates()
  {
    if (!this.mClippingEnabled) {
      return new Point(this.mWindowCoords);
    }
    Object localObject;
    if (this.mParentSurface.mIsMainWindowSurface)
    {
      localObject = this.mView.getRootWindowInsets().getSystemWindowInsets();
      localObject = new Rect(((Insets)localObject).left + this.mParentSurface.mInsets.left, ((Insets)localObject).top + this.mParentSurface.mInsets.top, this.mParentSurface.mWidth - ((Insets)localObject).right - this.mParentSurface.mInsets.right, this.mParentSurface.mHeight - ((Insets)localObject).bottom - this.mParentSurface.mInsets.bottom);
    }
    else
    {
      localObject = new Rect(0, 0, this.mParentSurface.mWidth, this.mParentSurface.mHeight);
    }
    return new Point(Math.max(((Rect)localObject).left, Math.min(((Rect)localObject).right - this.mWindowWidth, this.mWindowCoords.x)), Math.max(((Rect)localObject).top, Math.min(((Rect)localObject).bottom - this.mWindowHeight, this.mWindowCoords.y)));
  }
  
  private static float getDeviceDefaultDialogCornerRadius(Context paramContext)
  {
    paramContext = new ContextThemeWrapper(paramContext, 16974120).obtainStyledAttributes(new int[] { 16844145 });
    float f = paramContext.getDimension(0, 0.0F);
    paramContext.recycle();
    return f;
  }
  
  public static PointF getMagnifierDefaultSize()
  {
    Resources localResources = Resources.getSystem();
    float f = localResources.getDisplayMetrics().density;
    PointF localPointF = new PointF();
    localPointF.x = (localResources.getDimension(17105121) / f);
    localPointF.y = (localResources.getDimension(17105118) / f);
    return localPointF;
  }
  
  private void obtainContentCoordinates(float paramFloat1, float paramFloat2)
  {
    Object localObject1 = this.mViewCoordinatesInSurface;
    int i = localObject1[0];
    int j = localObject1[1];
    this.mView.getLocationInSurface((int[])localObject1);
    localObject1 = this.mViewCoordinatesInSurface;
    if ((localObject1[0] != i) || (localObject1[1] != j)) {
      this.mDirtyState = true;
    }
    if ((this.mView instanceof SurfaceView))
    {
      i = Math.round(paramFloat1);
      j = Math.round(paramFloat2);
    }
    else
    {
      i = Math.round(paramFloat1 + this.mViewCoordinatesInSurface[0]);
      j = Math.round(paramFloat2 + this.mViewCoordinatesInSurface[1]);
    }
    Rect[] arrayOfRect = new Rect[2];
    arrayOfRect[0] = new Rect(0, 0, this.mContentCopySurface.mWidth, this.mContentCopySurface.mHeight);
    localObject1 = new Rect();
    this.mView.getGlobalVisibleRect((Rect)localObject1);
    Object localObject2;
    if (this.mView.getViewRootImpl() != null)
    {
      localObject2 = this.mView.getViewRootImpl().mWindowAttributes.surfaceInsets;
      ((Rect)localObject1).offset(((Rect)localObject2).left, ((Rect)localObject2).top);
    }
    if ((this.mView instanceof SurfaceView))
    {
      localObject2 = this.mViewCoordinatesInSurface;
      ((Rect)localObject1).offset(-localObject2[0], -localObject2[1]);
    }
    arrayOfRect[1] = localObject1;
    int k = Integer.MIN_VALUE;
    for (int m = this.mLeftContentBound; m >= 0; m--) {
      k = Math.max(k, arrayOfRect[m].left);
    }
    m = Integer.MIN_VALUE;
    for (int n = this.mTopContentBound; n >= 0; n--) {
      m = Math.max(m, arrayOfRect[n].top);
    }
    n = Integer.MAX_VALUE;
    for (int i1 = this.mRightContentBound; i1 >= 0; i1--) {
      n = Math.min(n, arrayOfRect[i1].right);
    }
    i1 = Integer.MAX_VALUE;
    for (int i2 = this.mBottomContentBound; i2 >= 0; i2--) {
      i1 = Math.min(i1, arrayOfRect[i2].bottom);
    }
    i2 = Math.min(k, this.mContentCopySurface.mWidth - this.mSourceWidth);
    k = Math.min(m, this.mContentCopySurface.mHeight - this.mSourceHeight);
    if ((i2 < 0) || (k < 0)) {
      Log.e("Magnifier", "Magnifier's content is copied from a surface smaller thanthe content requested size. The magnifier will be dismissed.");
    }
    n = Math.max(n, this.mSourceWidth + i2);
    m = Math.max(i1, this.mSourceHeight + k);
    localObject1 = this.mClampedCenterZoomCoords;
    i1 = this.mSourceWidth;
    ((Point)localObject1).x = Math.max(i1 / 2 + i2, Math.min(i, n - i1 / 2));
    localObject1 = this.mClampedCenterZoomCoords;
    i = this.mSourceHeight;
    ((Point)localObject1).y = Math.max(i / 2 + k, Math.min(j, m - i / 2));
  }
  
  private void obtainSurfaces()
  {
    Object localObject1 = SurfaceInfo.NULL;
    Object localObject2 = localObject1;
    if (this.mView.getViewRootImpl() != null)
    {
      localObject3 = this.mView.getViewRootImpl();
      localObject4 = ((ViewRootImpl)localObject3).mSurface;
      localObject2 = localObject1;
      if (localObject4 != null)
      {
        localObject2 = localObject1;
        if (((Surface)localObject4).isValid())
        {
          localObject2 = ((ViewRootImpl)localObject3).mWindowAttributes.surfaceInsets;
          int i = ((ViewRootImpl)localObject3).getWidth();
          int j = ((Rect)localObject2).left;
          int k = ((Rect)localObject2).right;
          int m = ((ViewRootImpl)localObject3).getHeight();
          int n = ((Rect)localObject2).top;
          int i1 = ((Rect)localObject2).bottom;
          localObject2 = new SurfaceInfo(((ViewRootImpl)localObject3).getSurfaceControl(), (Surface)localObject4, i + j + k, m + n + i1, (Rect)localObject2, true);
        }
      }
    }
    Object localObject4 = SurfaceInfo.NULL;
    Object localObject3 = this.mView;
    localObject1 = localObject4;
    if ((localObject3 instanceof SurfaceView))
    {
      SurfaceControl localSurfaceControl = ((SurfaceView)localObject3).getSurfaceControl();
      SurfaceHolder localSurfaceHolder = ((SurfaceView)this.mView).getHolder();
      localObject3 = localSurfaceHolder.getSurface();
      localObject1 = localObject4;
      if (localSurfaceControl != null)
      {
        localObject1 = localObject4;
        if (localSurfaceControl.isValid())
        {
          localObject1 = localSurfaceHolder.getSurfaceFrame();
          localObject1 = new SurfaceInfo(localSurfaceControl, (Surface)localObject3, ((Rect)localObject1).right, ((Rect)localObject1).bottom, new Rect(), false);
        }
      }
    }
    if (localObject2 != SurfaceInfo.NULL) {
      localObject4 = localObject2;
    } else {
      localObject4 = localObject1;
    }
    this.mParentSurface = ((SurfaceInfo)localObject4);
    if ((this.mView instanceof SurfaceView)) {
      localObject2 = localObject1;
    }
    this.mContentCopySurface = ((SurfaceInfo)localObject2);
  }
  
  private void obtainWindowCoordinates(float paramFloat1, float paramFloat2)
  {
    int i;
    int j;
    if ((this.mView instanceof SurfaceView))
    {
      i = Math.round(paramFloat1);
      j = Math.round(paramFloat2);
    }
    else
    {
      i = Math.round(this.mViewCoordinatesInSurface[0] + paramFloat1);
      j = Math.round(this.mViewCoordinatesInSurface[1] + paramFloat2);
    }
    Point localPoint = this.mWindowCoords;
    localPoint.x = (i - this.mWindowWidth / 2);
    localPoint.y = (j - this.mWindowHeight / 2);
    if (this.mParentSurface != this.mContentCopySurface)
    {
      localPoint.x += this.mViewCoordinatesInSurface[0];
      localPoint = this.mWindowCoords;
      localPoint.y += this.mViewCoordinatesInSurface[1];
    }
  }
  
  private void onPixelCopyFailed()
  {
    Log.e("Magnifier", "Magnifier failed to copy content from the view Surface. It will be dismissed.");
    Handler.getMain().postAtFrontOfQueue(new _..Lambda.Magnifier.esRj9C7NyDvOX8eqqqLKuB6jpTw(this));
  }
  
  private void performPixelCopy(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((this.mContentCopySurface.mSurface != null) && (this.mContentCopySurface.mSurface.isValid()))
    {
      Point localPoint = getCurrentClampedWindowCoordinates();
      this.mPixelCopyRequestRect.set(paramInt1, paramInt2, this.mSourceWidth + paramInt1, this.mSourceHeight + paramInt2);
      InternalPopupWindow localInternalPopupWindow = this.mWindow;
      Object localObject = Bitmap.createBitmap(this.mSourceWidth, this.mSourceHeight, Bitmap.Config.ARGB_8888);
      PixelCopy.request(this.mContentCopySurface.mSurface, this.mPixelCopyRequestRect, (Bitmap)localObject, new _..Lambda.Magnifier.K0um0QSTAb4wXwua60CgJIIwGaI(this, localInternalPopupWindow, paramBoolean, localPoint, (Bitmap)localObject), sPixelCopyHandlerThread.getThreadHandler());
      localObject = this.mPrevStartCoordsInSurface;
      ((Point)localObject).x = paramInt1;
      ((Point)localObject).y = paramInt2;
      this.mDirtyState = false;
      return;
    }
    onPixelCopyFailed();
  }
  
  public void dismiss()
  {
    if (this.mWindow != null) {
      synchronized (this.mLock)
      {
        this.mWindow.destroy();
        this.mWindow = null;
        ??? = this.mPrevShowSourceCoords;
        ((PointF)???).x = -1.0F;
        ((PointF)???).y = -1.0F;
        ??? = this.mPrevShowWindowCoords;
        ((PointF)???).x = -1.0F;
        ((PointF)???).y = -1.0F;
        ??? = this.mPrevStartCoordsInSurface;
        ((Point)???).x = -1;
        ((Point)???).y = -1;
      }
    }
  }
  
  public Bitmap getContent()
  {
    ??? = this.mWindow;
    if (??? == null) {
      return null;
    }
    synchronized (((InternalPopupWindow)???).mLock)
    {
      Bitmap localBitmap = this.mWindow.mCurrentContent;
      return localBitmap;
    }
  }
  
  public float getCornerRadius()
  {
    return this.mWindowCornerRadius;
  }
  
  public int getDefaultHorizontalSourceToMagnifierOffset()
  {
    return this.mDefaultHorizontalSourceToMagnifierOffset;
  }
  
  public int getDefaultVerticalSourceToMagnifierOffset()
  {
    return this.mDefaultVerticalSourceToMagnifierOffset;
  }
  
  public float getElevation()
  {
    return this.mWindowElevation;
  }
  
  public int getHeight()
  {
    return this.mWindowHeight;
  }
  
  public Bitmap getOriginalContent()
  {
    ??? = this.mWindow;
    if (??? == null) {
      return null;
    }
    synchronized (((InternalPopupWindow)???).mLock)
    {
      Bitmap localBitmap = Bitmap.createBitmap(this.mWindow.mBitmap);
      return localBitmap;
    }
  }
  
  public Drawable getOverlay()
  {
    return this.mOverlay;
  }
  
  public Point getPosition()
  {
    if (this.mWindow == null) {
      return null;
    }
    Point localPoint = getCurrentClampedWindowCoordinates();
    localPoint.offset(-this.mParentSurface.mInsets.left, -this.mParentSurface.mInsets.top);
    return new Point(localPoint);
  }
  
  public int getSourceHeight()
  {
    return this.mSourceHeight;
  }
  
  public Point getSourcePosition()
  {
    if (this.mWindow == null) {
      return null;
    }
    Point localPoint = new Point(this.mPixelCopyRequestRect.left, this.mPixelCopyRequestRect.top);
    localPoint.offset(-this.mContentCopySurface.mInsets.left, -this.mContentCopySurface.mInsets.top);
    return new Point(localPoint);
  }
  
  public int getSourceWidth()
  {
    return this.mSourceWidth;
  }
  
  public int getWidth()
  {
    return this.mWindowWidth;
  }
  
  public float getZoom()
  {
    return this.mZoom;
  }
  
  public boolean isClippingEnabled()
  {
    return this.mClippingEnabled;
  }
  
  public void setOnOperationCompleteCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
    InternalPopupWindow localInternalPopupWindow = this.mWindow;
    if (localInternalPopupWindow != null) {
      InternalPopupWindow.access$2102(localInternalPopupWindow, paramCallback);
    }
  }
  
  public void setZoom(float paramFloat)
  {
    Preconditions.checkArgumentPositive(paramFloat, "Zoom should be positive");
    this.mZoom = paramFloat;
    this.mSourceWidth = Math.round(this.mWindowWidth / this.mZoom);
    this.mSourceHeight = Math.round(this.mWindowHeight / this.mZoom);
    this.mDirtyState = true;
  }
  
  public void show(float paramFloat1, float paramFloat2)
  {
    show(paramFloat1, paramFloat2, this.mDefaultHorizontalSourceToMagnifierOffset + paramFloat1, this.mDefaultVerticalSourceToMagnifierOffset + paramFloat2);
  }
  
  public void show(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    obtainSurfaces();
    obtainContentCoordinates(paramFloat1, paramFloat2);
    obtainWindowCoordinates(paramFloat3, paramFloat4);
    int i = this.mClampedCenterZoomCoords.x;
    int j = this.mSourceWidth / 2;
    int k = this.mClampedCenterZoomCoords.y;
    int m = this.mSourceHeight / 2;
    Object localObject2;
    if ((paramFloat1 == this.mPrevShowSourceCoords.x) && (paramFloat2 == this.mPrevShowSourceCoords.y) && (!this.mDirtyState))
    {
      if ((paramFloat3 != this.mPrevShowWindowCoords.x) || (paramFloat4 != this.mPrevShowWindowCoords.y))
      {
        ??? = getCurrentClampedWindowCoordinates();
        localObject2 = this.mWindow;
        sPixelCopyHandlerThread.getThreadHandler().post(new _..Lambda.Magnifier.sEUKNU2_gseoDMBt_HOs_JGAfZ8(this, (InternalPopupWindow)localObject2, (Point)???));
      }
    }
    else
    {
      if (this.mWindow == null) {
        synchronized (this.mLock)
        {
          InternalPopupWindow localInternalPopupWindow = new android/widget/Magnifier$InternalPopupWindow;
          Context localContext = this.mView.getContext();
          Display localDisplay = this.mView.getDisplay();
          SurfaceControl localSurfaceControl = this.mParentSurface.mSurfaceControl;
          int n = this.mWindowWidth;
          int i1 = this.mWindowHeight;
          float f1 = this.mWindowElevation;
          float f2 = this.mWindowCornerRadius;
          if (this.mOverlay != null) {
            localObject2 = this.mOverlay;
          } else {
            localObject2 = new ColorDrawable(0);
          }
          localInternalPopupWindow.<init>(localContext, localDisplay, localSurfaceControl, n, i1, f1, f2, (Drawable)localObject2, Handler.getMain(), this.mLock, this.mCallback);
          this.mWindow = localInternalPopupWindow;
        }
      }
      performPixelCopy(i - j, k - m, true);
    }
    PointF localPointF = this.mPrevShowSourceCoords;
    localPointF.x = paramFloat1;
    localPointF.y = paramFloat2;
    localPointF = this.mPrevShowWindowCoords;
    localPointF.x = paramFloat3;
    localPointF.y = paramFloat4;
  }
  
  public void update()
  {
    if (this.mWindow != null)
    {
      obtainSurfaces();
      if (!this.mDirtyState) {
        performPixelCopy(this.mPrevStartCoordsInSurface.x, this.mPrevStartCoordsInSurface.y, false);
      } else {
        show(this.mPrevShowSourceCoords.x, this.mPrevShowSourceCoords.y, this.mPrevShowWindowCoords.x, this.mPrevShowWindowCoords.y);
      }
    }
  }
  
  public static final class Builder
  {
    private int mBottomContentBound;
    private boolean mClippingEnabled;
    private float mCornerRadius;
    private float mElevation;
    private int mHeight;
    private int mHorizontalDefaultSourceToMagnifierOffset;
    private int mLeftContentBound;
    private Drawable mOverlay;
    private int mRightContentBound;
    private int mTopContentBound;
    private int mVerticalDefaultSourceToMagnifierOffset;
    private View mView;
    private int mWidth;
    private float mZoom;
    
    public Builder(View paramView)
    {
      this.mView = ((View)Preconditions.checkNotNull(paramView));
      applyDefaults();
    }
    
    private void applyDefaults()
    {
      Resources localResources = this.mView.getContext().getResources();
      this.mWidth = localResources.getDimensionPixelSize(17105121);
      this.mHeight = localResources.getDimensionPixelSize(17105118);
      this.mElevation = localResources.getDimension(17105117);
      this.mCornerRadius = localResources.getDimension(17105116);
      this.mZoom = localResources.getFloat(17105122);
      this.mHorizontalDefaultSourceToMagnifierOffset = localResources.getDimensionPixelSize(17105119);
      this.mVerticalDefaultSourceToMagnifierOffset = localResources.getDimensionPixelSize(17105120);
      this.mOverlay = new ColorDrawable(localResources.getColor(17170751, null));
      this.mClippingEnabled = true;
      this.mLeftContentBound = 1;
      this.mTopContentBound = 1;
      this.mRightContentBound = 1;
      this.mBottomContentBound = 1;
    }
    
    public Magnifier build()
    {
      return new Magnifier(this, null);
    }
    
    public Builder setClippingEnabled(boolean paramBoolean)
    {
      this.mClippingEnabled = paramBoolean;
      return this;
    }
    
    public Builder setCornerRadius(float paramFloat)
    {
      Preconditions.checkArgumentNonNegative(paramFloat, "Corner radius should be non-negative");
      this.mCornerRadius = paramFloat;
      return this;
    }
    
    public Builder setDefaultSourceToMagnifierOffset(int paramInt1, int paramInt2)
    {
      this.mHorizontalDefaultSourceToMagnifierOffset = paramInt1;
      this.mVerticalDefaultSourceToMagnifierOffset = paramInt2;
      return this;
    }
    
    public Builder setElevation(float paramFloat)
    {
      Preconditions.checkArgumentNonNegative(paramFloat, "Elevation should be non-negative");
      this.mElevation = paramFloat;
      return this;
    }
    
    public Builder setInitialZoom(float paramFloat)
    {
      Preconditions.checkArgumentPositive(paramFloat, "Zoom should be positive");
      this.mZoom = paramFloat;
      return this;
    }
    
    public Builder setOverlay(Drawable paramDrawable)
    {
      this.mOverlay = paramDrawable;
      return this;
    }
    
    public Builder setSize(int paramInt1, int paramInt2)
    {
      Preconditions.checkArgumentPositive(paramInt1, "Width should be positive");
      Preconditions.checkArgumentPositive(paramInt2, "Height should be positive");
      this.mWidth = paramInt1;
      this.mHeight = paramInt2;
      return this;
    }
    
    public Builder setSourceBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mLeftContentBound = paramInt1;
      this.mTopContentBound = paramInt2;
      this.mRightContentBound = paramInt3;
      this.mBottomContentBound = paramInt4;
      return this;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onOperationComplete();
  }
  
  private static class InternalPopupWindow
  {
    private static final int SURFACE_Z = 5;
    private Bitmap mBitmap;
    private final RenderNode mBitmapRenderNode;
    private Magnifier.Callback mCallback;
    private final int mContentHeight;
    private final int mContentWidth;
    private Bitmap mCurrentContent;
    private final Display mDisplay;
    private boolean mFirstDraw = true;
    private boolean mFrameDrawScheduled;
    private final Handler mHandler;
    private int mLastDrawContentPositionX;
    private int mLastDrawContentPositionY;
    private final Object mLock;
    private final Runnable mMagnifierUpdater;
    private final int mOffsetX;
    private final int mOffsetY;
    private final Drawable mOverlay;
    private final RenderNode mOverlayRenderNode;
    private boolean mPendingWindowPositionUpdate;
    private final ThreadedRenderer.SimpleRenderer mRenderer;
    private final Surface mSurface;
    private final SurfaceControl mSurfaceControl;
    private final int mSurfaceHeight;
    private final SurfaceSession mSurfaceSession;
    private final int mSurfaceWidth;
    private int mWindowPositionX;
    private int mWindowPositionY;
    
    InternalPopupWindow(Context paramContext, Display paramDisplay, SurfaceControl paramSurfaceControl, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, Drawable paramDrawable, Handler paramHandler, Object paramObject, Magnifier.Callback paramCallback)
    {
      this.mDisplay = paramDisplay;
      this.mOverlay = paramDrawable;
      this.mLock = paramObject;
      this.mCallback = paramCallback;
      this.mContentWidth = paramInt1;
      this.mContentHeight = paramInt2;
      this.mOffsetX = ((int)(paramFloat1 * 1.05F));
      this.mOffsetY = ((int)(1.05F * paramFloat1));
      this.mSurfaceWidth = (this.mContentWidth + this.mOffsetX * 2);
      this.mSurfaceHeight = (this.mContentHeight + this.mOffsetY * 2);
      this.mSurfaceSession = new SurfaceSession();
      this.mSurfaceControl = new SurfaceControl.Builder(this.mSurfaceSession).setFormat(-3).setBufferSize(this.mSurfaceWidth, this.mSurfaceHeight).setName("magnifier surface").setFlags(4).setParent(paramSurfaceControl).build();
      this.mSurface = new Surface();
      this.mSurface.copyFrom(this.mSurfaceControl);
      this.mRenderer = new ThreadedRenderer.SimpleRenderer(paramContext, "magnifier renderer", this.mSurface);
      this.mBitmapRenderNode = createRenderNodeForBitmap("magnifier content", paramFloat1, paramFloat2);
      this.mOverlayRenderNode = createRenderNodeForOverlay("magnifier overlay", paramFloat2);
      setupOverlay();
      paramContext = this.mRenderer.getRootNode().beginRecording(paramInt1, paramInt2);
      try
      {
        paramContext.insertReorderBarrier();
        paramContext.drawRenderNode(this.mBitmapRenderNode);
        paramContext.insertInorderBarrier();
        paramContext.drawRenderNode(this.mOverlayRenderNode);
        paramContext.insertInorderBarrier();
        this.mRenderer.getRootNode().endRecording();
        if (this.mCallback != null)
        {
          this.mCurrentContent = Bitmap.createBitmap(this.mContentWidth, this.mContentHeight, Bitmap.Config.ARGB_8888);
          updateCurrentContentForTesting();
        }
        this.mHandler = paramHandler;
        this.mMagnifierUpdater = new _..Lambda.Magnifier.InternalPopupWindow.t9Cn2sIi2LBUhAVikvRPKKoAwIU(this);
        this.mFrameDrawScheduled = false;
        return;
      }
      finally
      {
        this.mRenderer.getRootNode().endRecording();
      }
    }
    
    private RenderNode createRenderNodeForBitmap(String paramString, float paramFloat1, float paramFloat2)
    {
      paramString = RenderNode.create(paramString, null);
      int i = this.mOffsetX;
      int j = this.mOffsetY;
      paramString.setLeftTopRightBottom(i, j, this.mContentWidth + i, this.mContentHeight + j);
      paramString.setElevation(paramFloat1);
      Object localObject1 = new Outline();
      ((Outline)localObject1).setRoundRect(0, 0, this.mContentWidth, this.mContentHeight, paramFloat2);
      ((Outline)localObject1).setAlpha(1.0F);
      paramString.setOutline((Outline)localObject1);
      paramString.setClipToOutline(true);
      localObject1 = paramString.beginRecording(this.mContentWidth, this.mContentHeight);
      try
      {
        ((RecordingCanvas)localObject1).drawColor(-16711936);
        return paramString;
      }
      finally
      {
        paramString.endRecording();
      }
    }
    
    private RenderNode createRenderNodeForOverlay(String paramString, float paramFloat)
    {
      RenderNode localRenderNode = RenderNode.create(paramString, null);
      int i = this.mOffsetX;
      int j = this.mOffsetY;
      localRenderNode.setLeftTopRightBottom(i, j, this.mContentWidth + i, this.mContentHeight + j);
      paramString = new Outline();
      paramString.setRoundRect(0, 0, this.mContentWidth, this.mContentHeight, paramFloat);
      paramString.setAlpha(1.0F);
      localRenderNode.setOutline(paramString);
      localRenderNode.setClipToOutline(true);
      return localRenderNode;
    }
    
    private void doDraw()
    {
      synchronized (this.mLock)
      {
        if (!this.mSurface.isValid()) {
          return;
        }
        Object localObject2 = this.mBitmapRenderNode.beginRecording(this.mContentWidth, this.mContentHeight);
        try
        {
          Rect localRect1 = new android/graphics/Rect;
          localRect1.<init>(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
          Rect localRect2 = new android/graphics/Rect;
          localRect2.<init>(0, 0, this.mContentWidth, this.mContentHeight);
          Paint localPaint = new android/graphics/Paint;
          localPaint.<init>();
          localPaint.setFilterBitmap(true);
          ((RecordingCanvas)localObject2).drawBitmap(this.mBitmap, localRect1, localRect2, localPaint);
          this.mBitmapRenderNode.endRecording();
          if ((!this.mPendingWindowPositionUpdate) && (!this.mFirstDraw))
          {
            localObject2 = null;
          }
          else
          {
            boolean bool1 = this.mFirstDraw;
            this.mFirstDraw = false;
            boolean bool2 = this.mPendingWindowPositionUpdate;
            this.mPendingWindowPositionUpdate = false;
            int i = this.mWindowPositionX;
            int j = this.mWindowPositionY;
            localObject2 = new android/widget/_$$Lambda$Magnifier$InternalPopupWindow$qfjMrDJVvOQUv9_kKVdpLzbaJ_A;
            ((_..Lambda.Magnifier.InternalPopupWindow.qfjMrDJVvOQUv9_kKVdpLzbaJ_A)localObject2).<init>(this, bool2, i, j, bool1);
            this.mRenderer.setLightCenter(this.mDisplay, i, j);
          }
          this.mLastDrawContentPositionX = (this.mWindowPositionX + this.mOffsetX);
          this.mLastDrawContentPositionY = (this.mWindowPositionY + this.mOffsetY);
          this.mFrameDrawScheduled = false;
          this.mRenderer.draw((HardwareRenderer.FrameDrawingCallback)localObject2);
          if (this.mCallback != null)
          {
            updateCurrentContentForTesting();
            this.mCallback.onOperationComplete();
          }
          return;
        }
        finally
        {
          this.mBitmapRenderNode.endRecording();
        }
      }
    }
    
    private void drawOverlay()
    {
      RecordingCanvas localRecordingCanvas = this.mOverlayRenderNode.beginRecording(this.mContentWidth, this.mContentHeight);
      try
      {
        this.mOverlay.setBounds(0, 0, this.mContentWidth, this.mContentHeight);
        this.mOverlay.draw(localRecordingCanvas);
        return;
      }
      finally
      {
        this.mOverlayRenderNode.endRecording();
      }
    }
    
    private void requestUpdate()
    {
      if (this.mFrameDrawScheduled) {
        return;
      }
      Message localMessage = Message.obtain(this.mHandler, this.mMagnifierUpdater);
      localMessage.setAsynchronous(true);
      localMessage.sendToTarget();
      this.mFrameDrawScheduled = true;
    }
    
    private void setupOverlay()
    {
      drawOverlay();
      this.mOverlay.setCallback(new Drawable.Callback()
      {
        public void invalidateDrawable(Drawable paramAnonymousDrawable)
        {
          Magnifier.InternalPopupWindow.this.drawOverlay();
          if (Magnifier.InternalPopupWindow.this.mCallback != null) {
            Magnifier.InternalPopupWindow.this.updateCurrentContentForTesting();
          }
        }
        
        public void scheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable, long paramAnonymousLong)
        {
          Handler.getMain().postAtTime(paramAnonymousRunnable, paramAnonymousDrawable, paramAnonymousLong);
        }
        
        public void unscheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable)
        {
          Handler.getMain().removeCallbacks(paramAnonymousRunnable, paramAnonymousDrawable);
        }
      });
    }
    
    private void updateCurrentContentForTesting()
    {
      Canvas localCanvas = new Canvas(this.mCurrentContent);
      Rect localRect = new Rect(0, 0, this.mContentWidth, this.mContentHeight);
      Object localObject = this.mBitmap;
      if ((localObject != null) && (!((Bitmap)localObject).isRecycled()))
      {
        localObject = new Rect(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
        localCanvas.drawBitmap(this.mBitmap, (Rect)localObject, localRect, null);
      }
      this.mOverlay.setBounds(localRect);
      this.mOverlay.draw(localCanvas);
    }
    
    public void destroy()
    {
      this.mRenderer.destroy();
      this.mSurface.destroy();
      this.mSurfaceControl.remove();
      this.mSurfaceSession.kill();
      this.mHandler.removeCallbacks(this.mMagnifierUpdater);
      Bitmap localBitmap = this.mBitmap;
      if (localBitmap != null) {
        localBitmap.recycle();
      }
    }
    
    public void setContentPositionForNextDraw(int paramInt1, int paramInt2)
    {
      this.mWindowPositionX = (paramInt1 - this.mOffsetX);
      this.mWindowPositionY = (paramInt2 - this.mOffsetY);
      this.mPendingWindowPositionUpdate = true;
      requestUpdate();
    }
    
    public void updateContent(Bitmap paramBitmap)
    {
      Bitmap localBitmap = this.mBitmap;
      if (localBitmap != null) {
        localBitmap.recycle();
      }
      this.mBitmap = paramBitmap;
      requestUpdate();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface SourceBound {}
  
  private static class SurfaceInfo
  {
    public static final SurfaceInfo NULL = new SurfaceInfo(null, null, 0, 0, null, false);
    private int mHeight;
    private Rect mInsets;
    private boolean mIsMainWindowSurface;
    private Surface mSurface;
    private SurfaceControl mSurfaceControl;
    private int mWidth;
    
    SurfaceInfo(SurfaceControl paramSurfaceControl, Surface paramSurface, int paramInt1, int paramInt2, Rect paramRect, boolean paramBoolean)
    {
      this.mSurfaceControl = paramSurfaceControl;
      this.mSurface = paramSurface;
      this.mWidth = paramInt1;
      this.mHeight = paramInt2;
      this.mInsets = paramRect;
      this.mIsMainWindowSurface = paramBoolean;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Magnifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */