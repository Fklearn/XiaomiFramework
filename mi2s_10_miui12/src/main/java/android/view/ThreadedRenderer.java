package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.FrameInfo;
import android.graphics.HardwareRenderer;
import android.graphics.HardwareRenderer.FrameDrawingCallback;
import android.graphics.HardwareRenderer.FrameRenderRequest;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.os.SystemProperties;
import android.os.Trace;
import android.view.animation.AnimationUtils;
import com.android.internal.R.styleable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public final class ThreadedRenderer
  extends HardwareRenderer
{
  public static final String DEBUG_DIRTY_REGIONS_PROPERTY = "debug.hwui.show_dirty_regions";
  public static final String DEBUG_FORCE_DARK = "debug.hwui.force_dark";
  public static final String DEBUG_FPS_DIVISOR = "debug.hwui.fps_divisor";
  public static final String DEBUG_OVERDRAW_PROPERTY = "debug.hwui.overdraw";
  public static final String DEBUG_SHOW_LAYERS_UPDATES_PROPERTY = "debug.hwui.show_layers_updates";
  public static final String DEBUG_SHOW_NON_RECTANGULAR_CLIP_PROPERTY = "debug.hwui.show_non_rect_clip";
  public static int EGL_CONTEXT_PRIORITY_HIGH_IMG = 12545;
  public static int EGL_CONTEXT_PRIORITY_LOW_IMG = 0;
  public static int EGL_CONTEXT_PRIORITY_MEDIUM_IMG = 12546;
  public static final String OVERDRAW_PROPERTY_SHOW = "show";
  static final String PRINT_CONFIG_PROPERTY = "debug.hwui.print_config";
  static final String PROFILE_MAXFRAMES_PROPERTY = "debug.hwui.profile.maxframes";
  public static final String PROFILE_PROPERTY = "debug.hwui.profile";
  public static final String PROFILE_PROPERTY_VISUALIZE_BARS = "visual_bars";
  private static final String[] VISUALIZERS = { "visual_bars" };
  public static boolean sRendererDisabled;
  private static Boolean sSupportsOpenGL;
  public static boolean sSystemRendererDisabled;
  public static boolean sTrimForeground;
  private boolean mEnabled;
  private boolean mHasInsets;
  private int mHeight;
  private boolean mInitialized = false;
  private int mInsetLeft;
  private int mInsetTop;
  private final float mLightRadius;
  private final float mLightY;
  private final float mLightZ;
  private HardwareRenderer.FrameDrawingCallback mNextRtFrameCallback;
  private boolean mRequested = true;
  private boolean mRootNodeNeedsUpdate;
  private int mSurfaceHeight;
  private int mSurfaceWidth;
  private int mWidth;
  
  static
  {
    EGL_CONTEXT_PRIORITY_LOW_IMG = 12547;
    isAvailable();
    sRendererDisabled = false;
    sSystemRendererDisabled = false;
    sTrimForeground = false;
  }
  
  ThreadedRenderer(Context paramContext, boolean paramBoolean, String paramString)
  {
    setName(paramString);
    setOpaque(paramBoolean ^ true);
    paramContext = paramContext.obtainStyledAttributes(null, R.styleable.Lighting, 0, 0);
    this.mLightY = paramContext.getDimension(3, 0.0F);
    this.mLightZ = paramContext.getDimension(4, 0.0F);
    this.mLightRadius = paramContext.getDimension(2, 0.0F);
    float f1 = paramContext.getFloat(0, 0.0F);
    float f2 = paramContext.getFloat(1, 0.0F);
    paramContext.recycle();
    setLightSourceAlpha(f1, f2);
  }
  
  public static ThreadedRenderer create(Context paramContext, boolean paramBoolean, String paramString)
  {
    ThreadedRenderer localThreadedRenderer = null;
    if (isAvailable()) {
      localThreadedRenderer = new ThreadedRenderer(paramContext, paramBoolean, paramString);
    }
    return localThreadedRenderer;
  }
  
  private static void destroyResources(View paramView)
  {
    paramView.destroyHardwareResources();
  }
  
  public static void disable(boolean paramBoolean)
  {
    sRendererDisabled = true;
    if (paramBoolean) {
      sSystemRendererDisabled = true;
    }
  }
  
  public static void enableForegroundTrimming()
  {
    sTrimForeground = true;
  }
  
  public static boolean isAvailable()
  {
    Boolean localBoolean = sSupportsOpenGL;
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    boolean bool = false;
    if (SystemProperties.getInt("ro.kernel.qemu", 0) == 0)
    {
      sSupportsOpenGL = Boolean.valueOf(true);
      return true;
    }
    int i = SystemProperties.getInt("qemu.gles", -1);
    if (i == -1) {
      return false;
    }
    if (i > 0) {
      bool = true;
    }
    sSupportsOpenGL = Boolean.valueOf(bool);
    return sSupportsOpenGL.booleanValue();
  }
  
  private void updateEnabledState(Surface paramSurface)
  {
    if ((paramSurface != null) && (paramSurface.isValid())) {
      setEnabled(this.mInitialized);
    } else {
      setEnabled(false);
    }
  }
  
  private void updateRootDisplayList(View paramView, DrawCallbacks paramDrawCallbacks)
  {
    Trace.traceBegin(8L, "Record View#draw()");
    updateViewTreeDisplayList(paramView);
    Object localObject = this.mNextRtFrameCallback;
    this.mNextRtFrameCallback = null;
    if (localObject != null) {
      setFrameCallback((HardwareRenderer.FrameDrawingCallback)localObject);
    }
    if ((this.mRootNodeNeedsUpdate) || (!this.mRootNode.hasDisplayList())) {
      localObject = this.mRootNode.beginRecording(this.mSurfaceWidth, this.mSurfaceHeight);
    }
    try
    {
      int i = ((RecordingCanvas)localObject).save();
      ((RecordingCanvas)localObject).translate(this.mInsetLeft, this.mInsetTop);
      paramDrawCallbacks.onPreDraw((RecordingCanvas)localObject);
      ((RecordingCanvas)localObject).enableZ();
      ((RecordingCanvas)localObject).drawRenderNode(paramView.updateDisplayListIfDirty());
      ((RecordingCanvas)localObject).disableZ();
      paramDrawCallbacks.onPostDraw((RecordingCanvas)localObject);
      ((RecordingCanvas)localObject).restoreToCount(i);
      this.mRootNodeNeedsUpdate = false;
      this.mRootNode.endRecording();
      Trace.traceEnd(8L);
      return;
    }
    finally
    {
      this.mRootNode.endRecording();
    }
  }
  
  private void updateViewTreeDisplayList(View paramView)
  {
    paramView.mPrivateFlags |= 0x20;
    boolean bool;
    if ((paramView.mPrivateFlags & 0x80000000) == Integer.MIN_VALUE) {
      bool = true;
    } else {
      bool = false;
    }
    paramView.mRecreateDisplayList = bool;
    paramView.mPrivateFlags &= 0x7FFFFFFF;
    paramView.updateDisplayListIfDirty();
    paramView.mRecreateDisplayList = false;
  }
  
  Picture captureRenderingCommands()
  {
    return null;
  }
  
  public void destroy()
  {
    this.mInitialized = false;
    updateEnabledState(null);
    super.destroy();
  }
  
  void destroyHardwareResources(View paramView)
  {
    destroyResources(paramView);
    clearContent();
  }
  
  void draw(View paramView, View.AttachInfo paramAttachInfo, DrawCallbacks paramDrawCallbacks)
  {
    Choreographer localChoreographer = paramAttachInfo.mViewRootImpl.mChoreographer;
    localChoreographer.mFrameInfo.markDrawStart();
    updateRootDisplayList(paramView, paramDrawCallbacks);
    if (paramAttachInfo.mPendingAnimatingRenderNodes != null)
    {
      int i = paramAttachInfo.mPendingAnimatingRenderNodes.size();
      for (j = 0; j < i; j++) {
        registerAnimatingRenderNode((RenderNode)paramAttachInfo.mPendingAnimatingRenderNodes.get(j));
      }
      paramAttachInfo.mPendingAnimatingRenderNodes.clear();
      paramAttachInfo.mPendingAnimatingRenderNodes = null;
    }
    int j = syncAndDrawFrame(localChoreographer.mFrameInfo);
    if ((j & 0x2) != 0)
    {
      setEnabled(false);
      paramAttachInfo.mViewRootImpl.mSurface.release();
      paramAttachInfo.mViewRootImpl.invalidate();
    }
    if ((j & 0x1) != 0) {
      paramAttachInfo.mViewRootImpl.invalidate();
    }
  }
  
  void dumpGfxInfo(PrintWriter paramPrintWriter, FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
  {
    paramPrintWriter.flush();
    int i;
    if ((paramArrayOfString != null) && (paramArrayOfString.length != 0)) {
      i = 0;
    } else {
      i = 1;
    }
    int j = 0;
    for (int k = i; j < paramArrayOfString.length; k = i)
    {
      paramPrintWriter = paramArrayOfString[j];
      i = -1;
      int m = paramPrintWriter.hashCode();
      if (m != -252053678) {
        if (m != 1492)
        {
          break label77;
          break label77;
          break label76;
          if (m != 108404047) {
            break label125;
          }
        }
      }
      label76:
      label77:
      while (!paramPrintWriter.equals("framestats"))
      {
        do
        {
          while (!paramPrintWriter.equals("reset")) {}
          i = 1;
          break;
        } while (!paramPrintWriter.equals("-a"));
        i = 2;
        break;
      }
      i = 0;
      label125:
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2) {
            i = k;
          } else {
            i = 1;
          }
        }
        else {
          i = k | 0x2;
        }
      }
      else {
        i = k | 0x1;
      }
      j++;
    }
    dumpProfileInfo(paramFileDescriptor, k);
  }
  
  int getHeight()
  {
    return this.mHeight;
  }
  
  public int getInsetLeft()
  {
    return this.mInsetLeft;
  }
  
  public int getInsetTop()
  {
    return this.mInsetTop;
  }
  
  public RenderNode getRootNode()
  {
    return this.mRootNode;
  }
  
  int getWidth()
  {
    return this.mWidth;
  }
  
  boolean initialize(Surface paramSurface)
    throws Surface.OutOfResourcesException
  {
    boolean bool = this.mInitialized;
    this.mInitialized = true;
    updateEnabledState(paramSurface);
    setSurface(paramSurface);
    return bool ^ true;
  }
  
  boolean initializeIfNeeded(int paramInt1, int paramInt2, View.AttachInfo paramAttachInfo, Surface paramSurface, Rect paramRect)
    throws Surface.OutOfResourcesException
  {
    if ((isRequested()) && (!isEnabled()) && (initialize(paramSurface)))
    {
      setup(paramInt1, paramInt2, paramAttachInfo, paramRect);
      return true;
    }
    return false;
  }
  
  void invalidateRoot()
  {
    this.mRootNodeNeedsUpdate = true;
  }
  
  boolean isEnabled()
  {
    return this.mEnabled;
  }
  
  boolean isRequested()
  {
    return this.mRequested;
  }
  
  public boolean loadSystemProperties()
  {
    boolean bool = super.loadSystemProperties();
    if (bool) {
      invalidateRoot();
    }
    return bool;
  }
  
  void registerRtFrameCallback(HardwareRenderer.FrameDrawingCallback paramFrameDrawingCallback)
  {
    this.mNextRtFrameCallback = paramFrameDrawingCallback;
  }
  
  void setEnabled(boolean paramBoolean)
  {
    this.mEnabled = paramBoolean;
  }
  
  void setLightCenter(View.AttachInfo paramAttachInfo)
  {
    Point localPoint = paramAttachInfo.mPoint;
    paramAttachInfo.mDisplay.getRealSize(localPoint);
    setLightSourceGeometry(localPoint.x / 2.0F - paramAttachInfo.mWindowLeft, this.mLightY - paramAttachInfo.mWindowTop, this.mLightZ, this.mLightRadius);
  }
  
  void setRequested(boolean paramBoolean)
  {
    this.mRequested = paramBoolean;
  }
  
  public void setSurface(Surface paramSurface)
  {
    if ((paramSurface != null) && (paramSurface.isValid())) {
      super.setSurface(paramSurface);
    } else {
      super.setSurface(null);
    }
  }
  
  void setup(int paramInt1, int paramInt2, View.AttachInfo paramAttachInfo, Rect paramRect)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    if ((paramRect != null) && ((paramRect.left != 0) || (paramRect.right != 0) || (paramRect.top != 0) || (paramRect.bottom != 0)))
    {
      this.mHasInsets = true;
      this.mInsetLeft = paramRect.left;
      this.mInsetTop = paramRect.top;
      this.mSurfaceWidth = (this.mInsetLeft + paramInt1 + paramRect.right);
      this.mSurfaceHeight = (this.mInsetTop + paramInt2 + paramRect.bottom);
      setOpaque(false);
    }
    else
    {
      this.mHasInsets = false;
      this.mInsetLeft = 0;
      this.mInsetTop = 0;
      this.mSurfaceWidth = paramInt1;
      this.mSurfaceHeight = paramInt2;
    }
    this.mRootNode.setLeftTopRightBottom(-this.mInsetLeft, -this.mInsetTop, this.mSurfaceWidth, this.mSurfaceHeight);
    setLightCenter(paramAttachInfo);
  }
  
  void updateSurface(Surface paramSurface)
    throws Surface.OutOfResourcesException
  {
    updateEnabledState(paramSurface);
    setSurface(paramSurface);
  }
  
  static abstract interface DrawCallbacks
  {
    public abstract void onPostDraw(RecordingCanvas paramRecordingCanvas);
    
    public abstract void onPreDraw(RecordingCanvas paramRecordingCanvas);
  }
  
  public static class SimpleRenderer
    extends HardwareRenderer
  {
    private final float mLightRadius;
    private final float mLightY;
    private final float mLightZ;
    
    public SimpleRenderer(Context paramContext, String paramString, Surface paramSurface)
    {
      setName(paramString);
      setOpaque(false);
      setSurface(paramSurface);
      paramContext = paramContext.obtainStyledAttributes(null, R.styleable.Lighting, 0, 0);
      this.mLightY = paramContext.getDimension(3, 0.0F);
      this.mLightZ = paramContext.getDimension(4, 0.0F);
      this.mLightRadius = paramContext.getDimension(2, 0.0F);
      float f1 = paramContext.getFloat(0, 0.0F);
      float f2 = paramContext.getFloat(1, 0.0F);
      paramContext.recycle();
      setLightSourceAlpha(f1, f2);
    }
    
    public void draw(HardwareRenderer.FrameDrawingCallback paramFrameDrawingCallback)
    {
      long l = AnimationUtils.currentAnimationTimeMillis();
      if (paramFrameDrawingCallback != null) {
        setFrameCallback(paramFrameDrawingCallback);
      }
      createRenderRequest().setVsyncTime(l * 1000000L).syncAndDraw();
    }
    
    public RenderNode getRootNode()
    {
      return this.mRootNode;
    }
    
    public void setLightCenter(Display paramDisplay, int paramInt1, int paramInt2)
    {
      Point localPoint = new Point();
      paramDisplay.getRealSize(localPoint);
      setLightSourceGeometry(localPoint.x / 2.0F - paramInt1, this.mLightY - paramInt2, this.mLightZ, this.mLightRadius);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ThreadedRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */