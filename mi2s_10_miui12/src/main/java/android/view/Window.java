package android.view;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R.styleable;
import java.util.Collections;
import java.util.List;

public abstract class Window
{
  public static final int DECOR_CAPTION_SHADE_AUTO = 0;
  public static final int DECOR_CAPTION_SHADE_DARK = 2;
  public static final int DECOR_CAPTION_SHADE_LIGHT = 1;
  @Deprecated
  protected static final int DEFAULT_FEATURES = 65;
  public static final int FEATURE_ACTION_BAR = 8;
  public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
  public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
  public static final int FEATURE_ACTIVITY_TRANSITIONS = 13;
  public static final int FEATURE_CONTENT_TRANSITIONS = 12;
  public static final int FEATURE_CONTEXT_MENU = 6;
  public static final int FEATURE_CUSTOM_TITLE = 7;
  @Deprecated
  public static final int FEATURE_INDETERMINATE_PROGRESS = 5;
  public static final int FEATURE_LEFT_ICON = 3;
  @UnsupportedAppUsage
  public static final int FEATURE_MAX = 13;
  public static final int FEATURE_NO_TITLE = 1;
  public static final int FEATURE_OPTIONS_PANEL = 0;
  @Deprecated
  public static final int FEATURE_PROGRESS = 2;
  public static final int FEATURE_RIGHT_ICON = 4;
  public static final int FEATURE_SWIPE_TO_DISMISS = 11;
  public static final int ID_ANDROID_CONTENT = 16908290;
  public static final String NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME = "android:navigation:background";
  @Deprecated
  public static final int PROGRESS_END = 10000;
  @Deprecated
  public static final int PROGRESS_INDETERMINATE_OFF = -4;
  @Deprecated
  public static final int PROGRESS_INDETERMINATE_ON = -3;
  @Deprecated
  public static final int PROGRESS_SECONDARY_END = 30000;
  @Deprecated
  public static final int PROGRESS_SECONDARY_START = 20000;
  @Deprecated
  public static final int PROGRESS_START = 0;
  @Deprecated
  public static final int PROGRESS_VISIBILITY_OFF = -2;
  @Deprecated
  public static final int PROGRESS_VISIBILITY_ON = -1;
  public static final String STATUS_BAR_BACKGROUND_TRANSITION_NAME = "android:status:background";
  private Window mActiveChild;
  @UnsupportedAppUsage
  private String mAppName;
  @UnsupportedAppUsage
  private IBinder mAppToken;
  @UnsupportedAppUsage
  private Callback mCallback;
  private boolean mCloseOnSwipeEnabled = false;
  private boolean mCloseOnTouchOutside = false;
  private Window mContainer;
  @UnsupportedAppUsage
  private final Context mContext;
  private int mDefaultWindowFormat = -1;
  @UnsupportedAppUsage
  private boolean mDestroyed;
  @UnsupportedAppUsage
  private int mFeatures;
  private int mForcedWindowFlags = 0;
  @UnsupportedAppUsage
  private boolean mHardwareAccelerated;
  private boolean mHasChildren = false;
  private boolean mHasSoftInputMode = false;
  private boolean mHaveDimAmount = false;
  private boolean mHaveWindowFormat = false;
  private boolean mIsActive = false;
  @UnsupportedAppUsage
  private int mLocalFeatures;
  private OnRestrictedCaptionAreaChangedListener mOnRestrictedCaptionAreaChangedListener;
  private OnWindowDismissedCallback mOnWindowDismissedCallback;
  private OnWindowSwipeDismissedCallback mOnWindowSwipeDismissedCallback;
  private boolean mOverlayWithDecorCaptionEnabled = false;
  private Rect mRestrictedCaptionAreaRect;
  private boolean mSetCloseOnTouchOutside = false;
  @UnsupportedAppUsage
  private final WindowManager.LayoutParams mWindowAttributes = new WindowManager.LayoutParams();
  private WindowControllerCallback mWindowControllerCallback;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private WindowManager mWindowManager;
  @UnsupportedAppUsage
  private TypedArray mWindowStyle;
  
  public Window(Context paramContext)
  {
    this.mContext = paramContext;
    int i = getDefaultFeatures(paramContext);
    this.mLocalFeatures = i;
    this.mFeatures = i;
  }
  
  public static int getDefaultFeatures(Context paramContext)
  {
    int i = 0;
    paramContext = paramContext.getResources();
    if (paramContext.getBoolean(17891401)) {
      i = 0x0 | 0x1;
    }
    int j = i;
    if (paramContext.getBoolean(17891400)) {
      j = i | 0x40;
    }
    return j;
  }
  
  private boolean isOutOfBounds(Context paramContext, MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    int k = ViewConfiguration.get(paramContext).getScaledWindowTouchSlop();
    paramContext = getDecorView();
    boolean bool;
    if ((i >= -k) && (j >= -k) && (i <= paramContext.getWidth() + k) && (j <= paramContext.getHeight() + k)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void setPrivateFlags(int paramInt1, int paramInt2)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.privateFlags = (localLayoutParams.privateFlags & paramInt2 | paramInt1 & paramInt2);
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public abstract void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams);
  
  public void addExtraFlags(int paramInt)
  {
    setExtraFlags(paramInt, paramInt);
  }
  
  public void addFlags(int paramInt)
  {
    setFlags(paramInt, paramInt);
  }
  
  public final void addOnFrameMetricsAvailableListener(OnFrameMetricsAvailableListener paramOnFrameMetricsAvailableListener, Handler paramHandler)
  {
    View localView = getDecorView();
    if (localView != null)
    {
      if (paramOnFrameMetricsAvailableListener != null)
      {
        localView.addFrameMetricsListener(this, paramOnFrameMetricsAvailableListener, paramHandler);
        return;
      }
      throw new NullPointerException("listener cannot be null");
    }
    throw new IllegalStateException("can't observe a Window without an attached view");
  }
  
  @UnsupportedAppUsage
  public void addPrivateFlags(int paramInt)
  {
    setPrivateFlags(paramInt, paramInt);
  }
  
  @SystemApi
  public void addSystemFlags(int paramInt)
  {
    addPrivateFlags(paramInt);
  }
  
  void adjustLayoutParamsForSubWindow(WindowManager.LayoutParams paramLayoutParams)
  {
    CharSequence localCharSequence = paramLayoutParams.getTitle();
    Object localObject;
    if ((paramLayoutParams.type >= 1000) && (paramLayoutParams.type <= 1999))
    {
      if (paramLayoutParams.token == null)
      {
        localObject = peekDecorView();
        if (localObject != null) {
          paramLayoutParams.token = ((View)localObject).getWindowToken();
        }
      }
      if ((localCharSequence == null) || (localCharSequence.length() == 0))
      {
        localObject = new StringBuilder(32);
        if (paramLayoutParams.type == 1001) {
          ((StringBuilder)localObject).append("Media");
        } else if (paramLayoutParams.type == 1004) {
          ((StringBuilder)localObject).append("MediaOvr");
        } else if (paramLayoutParams.type == 1000) {
          ((StringBuilder)localObject).append("Panel");
        } else if (paramLayoutParams.type == 1002) {
          ((StringBuilder)localObject).append("SubPanel");
        } else if (paramLayoutParams.type == 1005) {
          ((StringBuilder)localObject).append("AboveSubPanel");
        } else if (paramLayoutParams.type == 1003) {
          ((StringBuilder)localObject).append("AtchDlg");
        } else {
          ((StringBuilder)localObject).append(paramLayoutParams.type);
        }
        if (this.mAppName != null)
        {
          ((StringBuilder)localObject).append(":");
          ((StringBuilder)localObject).append(this.mAppName);
        }
        paramLayoutParams.setTitle((CharSequence)localObject);
      }
    }
    else if ((paramLayoutParams.type >= 2000) && (paramLayoutParams.type <= 2999))
    {
      if ((localCharSequence == null) || (localCharSequence.length() == 0))
      {
        localObject = new StringBuilder(32);
        ((StringBuilder)localObject).append("Sys");
        ((StringBuilder)localObject).append(paramLayoutParams.type);
        if (this.mAppName != null)
        {
          ((StringBuilder)localObject).append(":");
          ((StringBuilder)localObject).append(this.mAppName);
        }
        paramLayoutParams.setTitle((CharSequence)localObject);
      }
    }
    else
    {
      if (paramLayoutParams.token == null)
      {
        localObject = this.mContainer;
        if (localObject == null) {
          localObject = this.mAppToken;
        } else {
          localObject = ((Window)localObject).mAppToken;
        }
        paramLayoutParams.token = ((IBinder)localObject);
      }
      if ((localCharSequence == null) || (localCharSequence.length() == 0))
      {
        localObject = this.mAppName;
        if (localObject != null) {
          paramLayoutParams.setTitle((CharSequence)localObject);
        }
      }
    }
    if (paramLayoutParams.packageName == null) {
      paramLayoutParams.packageName = this.mContext.getPackageName();
    }
    if ((this.mHardwareAccelerated) || ((this.mWindowAttributes.flags & 0x1000000) != 0)) {
      paramLayoutParams.flags |= 0x1000000;
    }
  }
  
  @UnsupportedAppUsage
  public abstract void alwaysReadCloseOnTouchAttr();
  
  public abstract void clearContentView();
  
  public void clearExtraFlags(int paramInt)
  {
    setExtraFlags(0, paramInt);
  }
  
  public void clearFlags(int paramInt)
  {
    setFlags(0, paramInt);
  }
  
  public abstract void closeAllPanels();
  
  public abstract void closePanel(int paramInt);
  
  public final void destroy()
  {
    this.mDestroyed = true;
  }
  
  public final void dispatchOnWindowDismissed(boolean paramBoolean1, boolean paramBoolean2)
  {
    OnWindowDismissedCallback localOnWindowDismissedCallback = this.mOnWindowDismissedCallback;
    if (localOnWindowDismissedCallback != null) {
      localOnWindowDismissedCallback.onWindowDismissed(paramBoolean1, paramBoolean2);
    }
  }
  
  public final void dispatchOnWindowSwipeDismissed()
  {
    OnWindowSwipeDismissedCallback localOnWindowSwipeDismissedCallback = this.mOnWindowSwipeDismissedCallback;
    if (localOnWindowSwipeDismissedCallback != null) {
      localOnWindowSwipeDismissedCallback.onWindowSwipeDismissed();
    }
  }
  
  protected void dispatchWindowAttributesChanged(WindowManager.LayoutParams paramLayoutParams)
  {
    Callback localCallback = this.mCallback;
    if (localCallback != null) {
      localCallback.onWindowAttributesChanged(paramLayoutParams);
    }
  }
  
  public <T extends View> T findViewById(int paramInt)
  {
    return getDecorView().findViewById(paramInt);
  }
  
  public boolean getAllowEnterTransitionOverlap()
  {
    return true;
  }
  
  public boolean getAllowReturnTransitionOverlap()
  {
    return true;
  }
  
  public final WindowManager.LayoutParams getAttributes()
  {
    return this.mWindowAttributes;
  }
  
  public final Callback getCallback()
  {
    return this.mCallback;
  }
  
  public int getColorMode()
  {
    return getAttributes().getColorMode();
  }
  
  public final Window getContainer()
  {
    return this.mContainer;
  }
  
  public Scene getContentScene()
  {
    return null;
  }
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  public abstract View getCurrentFocus();
  
  public abstract View getDecorView();
  
  public float getElevation()
  {
    return 0.0F;
  }
  
  public Transition getEnterTransition()
  {
    return null;
  }
  
  public Transition getExitTransition()
  {
    return null;
  }
  
  protected final int getFeatures()
  {
    return this.mFeatures;
  }
  
  protected final int getForcedWindowFlags()
  {
    return this.mForcedWindowFlags;
  }
  
  public abstract WindowInsetsController getInsetsController();
  
  public abstract LayoutInflater getLayoutInflater();
  
  protected final int getLocalFeatures()
  {
    return this.mLocalFeatures;
  }
  
  public MediaController getMediaController()
  {
    return null;
  }
  
  public abstract int getNavigationBarColor();
  
  public int getNavigationBarDividerColor()
  {
    return 0;
  }
  
  public Transition getReenterTransition()
  {
    return null;
  }
  
  public Transition getReturnTransition()
  {
    return null;
  }
  
  public Transition getSharedElementEnterTransition()
  {
    return null;
  }
  
  public Transition getSharedElementExitTransition()
  {
    return null;
  }
  
  public Transition getSharedElementReenterTransition()
  {
    return null;
  }
  
  public Transition getSharedElementReturnTransition()
  {
    return null;
  }
  
  public boolean getSharedElementsUseOverlay()
  {
    return true;
  }
  
  public abstract int getStatusBarColor();
  
  public List<Rect> getSystemGestureExclusionRects()
  {
    return Collections.emptyList();
  }
  
  public long getTransitionBackgroundFadeDuration()
  {
    return 0L;
  }
  
  public TransitionManager getTransitionManager()
  {
    return null;
  }
  
  public abstract int getVolumeControlStream();
  
  public final WindowControllerCallback getWindowControllerCallback()
  {
    return this.mWindowControllerCallback;
  }
  
  public WindowManager getWindowManager()
  {
    return this.mWindowManager;
  }
  
  public final TypedArray getWindowStyle()
  {
    try
    {
      if (this.mWindowStyle == null) {
        this.mWindowStyle = this.mContext.obtainStyledAttributes(R.styleable.Window);
      }
      TypedArray localTypedArray = this.mWindowStyle;
      return localTypedArray;
    }
    finally {}
  }
  
  public final boolean hasChildren()
  {
    return this.mHasChildren;
  }
  
  public boolean hasFeature(int paramInt)
  {
    int i = getFeatures();
    boolean bool = true;
    if ((i & 1 << paramInt) == 0) {
      bool = false;
    }
    return bool;
  }
  
  protected final boolean hasSoftInputMode()
  {
    return this.mHasSoftInputMode;
  }
  
  protected boolean haveDimAmount()
  {
    return this.mHaveDimAmount;
  }
  
  public void injectInputEvent(InputEvent paramInputEvent) {}
  
  public abstract void invalidatePanelMenu(int paramInt);
  
  public final boolean isActive()
  {
    return this.mIsActive;
  }
  
  public boolean isCloseOnSwipeEnabled()
  {
    return this.mCloseOnSwipeEnabled;
  }
  
  @UnsupportedAppUsage
  public final boolean isDestroyed()
  {
    return this.mDestroyed;
  }
  
  public abstract boolean isFloating();
  
  public boolean isNavigationBarContrastEnforced()
  {
    return false;
  }
  
  public boolean isOverlayWithDecorCaptionEnabled()
  {
    return this.mOverlayWithDecorCaptionEnabled;
  }
  
  public abstract boolean isShortcutKey(int paramInt, KeyEvent paramKeyEvent);
  
  public boolean isStatusBarContrastEnforced()
  {
    return false;
  }
  
  public boolean isWideColorGamut()
  {
    int i = getColorMode();
    boolean bool = true;
    if ((i != 1) || (!getContext().getResources().getConfiguration().isScreenWideColorGamut())) {
      bool = false;
    }
    return bool;
  }
  
  public final void makeActive()
  {
    Window localWindow = this.mContainer;
    if (localWindow != null)
    {
      localWindow = localWindow.mActiveChild;
      if (localWindow != null) {
        localWindow.mIsActive = false;
      }
      this.mContainer.mActiveChild = this;
    }
    this.mIsActive = true;
    onActive();
  }
  
  public void notifyRestrictedCaptionAreaCallback(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mOnRestrictedCaptionAreaChangedListener != null)
    {
      this.mRestrictedCaptionAreaRect.set(paramInt1, paramInt2, paramInt3, paramInt4);
      this.mOnRestrictedCaptionAreaChangedListener.onRestrictedCaptionAreaChanged(this.mRestrictedCaptionAreaRect);
    }
  }
  
  protected abstract void onActive();
  
  public abstract void onConfigurationChanged(Configuration paramConfiguration);
  
  public abstract void onMultiWindowModeChanged();
  
  public abstract void onPictureInPictureModeChanged(boolean paramBoolean);
  
  public abstract void openPanel(int paramInt, KeyEvent paramKeyEvent);
  
  public abstract View peekDecorView();
  
  public abstract boolean performContextMenuIdentifierAction(int paramInt1, int paramInt2);
  
  public abstract boolean performPanelIdentifierAction(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract boolean performPanelShortcut(int paramInt1, int paramInt2, KeyEvent paramKeyEvent, int paramInt3);
  
  protected void removeFeature(int paramInt)
  {
    paramInt = 1 << paramInt;
    this.mFeatures &= paramInt;
    int i = this.mLocalFeatures;
    Window localWindow = this.mContainer;
    if (localWindow != null) {
      paramInt = localWindow.mFeatures & paramInt;
    }
    this.mLocalFeatures = (i & paramInt);
  }
  
  public final void removeOnFrameMetricsAvailableListener(OnFrameMetricsAvailableListener paramOnFrameMetricsAvailableListener)
  {
    if (getDecorView() != null) {
      getDecorView().removeFrameMetricsListener(paramOnFrameMetricsAvailableListener);
    }
  }
  
  public abstract void reportActivityRelaunched();
  
  public boolean requestFeature(int paramInt)
  {
    boolean bool = true;
    int i = 1 << paramInt;
    this.mFeatures |= i;
    int j = this.mLocalFeatures;
    Window localWindow = this.mContainer;
    if (localWindow != null) {
      paramInt = localWindow.mFeatures & i;
    } else {
      paramInt = i;
    }
    this.mLocalFeatures = (j | paramInt);
    if ((this.mFeatures & i) == 0) {
      bool = false;
    }
    return bool;
  }
  
  public final <T extends View> T requireViewById(int paramInt)
  {
    View localView = findViewById(paramInt);
    if (localView != null) {
      return localView;
    }
    throw new IllegalArgumentException("ID does not reference a View inside this Window");
  }
  
  public abstract void restoreHierarchyState(Bundle paramBundle);
  
  public abstract Bundle saveHierarchyState();
  
  public void setAllowEnterTransitionOverlap(boolean paramBoolean) {}
  
  public void setAllowReturnTransitionOverlap(boolean paramBoolean) {}
  
  public void setAttributes(WindowManager.LayoutParams paramLayoutParams)
  {
    this.mWindowAttributes.copyFrom(paramLayoutParams);
    dispatchWindowAttributesChanged(this.mWindowAttributes);
  }
  
  public abstract void setBackgroundDrawable(Drawable paramDrawable);
  
  public void setBackgroundDrawableResource(int paramInt)
  {
    setBackgroundDrawable(this.mContext.getDrawable(paramInt));
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  public abstract void setChildDrawable(int paramInt, Drawable paramDrawable);
  
  public abstract void setChildInt(int paramInt1, int paramInt2);
  
  public void setClipToOutline(boolean paramBoolean) {}
  
  public void setCloseOnSwipeEnabled(boolean paramBoolean)
  {
    this.mCloseOnSwipeEnabled = paramBoolean;
  }
  
  @UnsupportedAppUsage
  public void setCloseOnTouchOutside(boolean paramBoolean)
  {
    this.mCloseOnTouchOutside = paramBoolean;
    this.mSetCloseOnTouchOutside = true;
  }
  
  @UnsupportedAppUsage
  public void setCloseOnTouchOutsideIfNotSet(boolean paramBoolean)
  {
    if (!this.mSetCloseOnTouchOutside)
    {
      this.mCloseOnTouchOutside = paramBoolean;
      this.mSetCloseOnTouchOutside = true;
    }
  }
  
  public void setColorMode(int paramInt)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.setColorMode(paramInt);
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public void setContainer(Window paramWindow)
  {
    this.mContainer = paramWindow;
    if (paramWindow != null)
    {
      this.mFeatures |= 0x2;
      this.mLocalFeatures |= 0x2;
      paramWindow.mHasChildren = true;
    }
  }
  
  public abstract void setContentView(int paramInt);
  
  public abstract void setContentView(View paramView);
  
  public abstract void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams);
  
  public abstract void setDecorCaptionShade(int paramInt);
  
  public void setDefaultIcon(int paramInt) {}
  
  public void setDefaultLogo(int paramInt) {}
  
  protected void setDefaultWindowFormat(int paramInt)
  {
    this.mDefaultWindowFormat = paramInt;
    if (!this.mHaveWindowFormat)
    {
      WindowManager.LayoutParams localLayoutParams = getAttributes();
      localLayoutParams.format = paramInt;
      dispatchWindowAttributesChanged(localLayoutParams);
    }
  }
  
  public void setDimAmount(float paramFloat)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.dimAmount = paramFloat;
    this.mHaveDimAmount = true;
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public void setElevation(float paramFloat) {}
  
  public void setEnterTransition(Transition paramTransition) {}
  
  public void setExitTransition(Transition paramTransition) {}
  
  public void setExtraFlags(int paramInt1, int paramInt2)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.extraFlags = (localLayoutParams.extraFlags & paramInt2 | paramInt1 & paramInt2);
    Callback localCallback = this.mCallback;
    if (localCallback != null) {
      localCallback.onWindowAttributesChanged(localLayoutParams);
    }
  }
  
  public abstract void setFeatureDrawable(int paramInt, Drawable paramDrawable);
  
  public abstract void setFeatureDrawableAlpha(int paramInt1, int paramInt2);
  
  public abstract void setFeatureDrawableResource(int paramInt1, int paramInt2);
  
  public abstract void setFeatureDrawableUri(int paramInt, Uri paramUri);
  
  public abstract void setFeatureInt(int paramInt1, int paramInt2);
  
  public void setFlags(int paramInt1, int paramInt2)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.flags = (localLayoutParams.flags & paramInt2 | paramInt1 & paramInt2);
    this.mForcedWindowFlags |= paramInt2;
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public void setFormat(int paramInt)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    if (paramInt != 0)
    {
      localLayoutParams.format = paramInt;
      this.mHaveWindowFormat = true;
    }
    else
    {
      localLayoutParams.format = this.mDefaultWindowFormat;
      this.mHaveWindowFormat = false;
    }
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public void setGravity(int paramInt)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.gravity = paramInt;
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public void setIcon(int paramInt) {}
  
  public void setLayout(int paramInt1, int paramInt2)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.width = paramInt1;
    localLayoutParams.height = paramInt2;
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public void setLocalFocus(boolean paramBoolean1, boolean paramBoolean2) {}
  
  public void setLogo(int paramInt) {}
  
  public void setMediaController(MediaController paramMediaController) {}
  
  public abstract void setNavigationBarColor(int paramInt);
  
  public void setNavigationBarContrastEnforced(boolean paramBoolean) {}
  
  public void setNavigationBarDividerColor(int paramInt) {}
  
  @UnsupportedAppUsage
  protected void setNeedsMenuKey(int paramInt)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.needsMenuKey = paramInt;
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public final void setOnWindowDismissedCallback(OnWindowDismissedCallback paramOnWindowDismissedCallback)
  {
    this.mOnWindowDismissedCallback = paramOnWindowDismissedCallback;
  }
  
  public final void setOnWindowSwipeDismissedCallback(OnWindowSwipeDismissedCallback paramOnWindowSwipeDismissedCallback)
  {
    this.mOnWindowSwipeDismissedCallback = paramOnWindowSwipeDismissedCallback;
  }
  
  public void setOverlayWithDecorCaptionEnabled(boolean paramBoolean)
  {
    this.mOverlayWithDecorCaptionEnabled = paramBoolean;
  }
  
  public void setReenterTransition(Transition paramTransition) {}
  
  public abstract void setResizingCaptionDrawable(Drawable paramDrawable);
  
  public final void setRestrictedCaptionAreaListener(OnRestrictedCaptionAreaChangedListener paramOnRestrictedCaptionAreaChangedListener)
  {
    this.mOnRestrictedCaptionAreaChangedListener = paramOnRestrictedCaptionAreaChangedListener;
    if (paramOnRestrictedCaptionAreaChangedListener != null) {
      paramOnRestrictedCaptionAreaChangedListener = new Rect();
    } else {
      paramOnRestrictedCaptionAreaChangedListener = null;
    }
    this.mRestrictedCaptionAreaRect = paramOnRestrictedCaptionAreaChangedListener;
  }
  
  public void setReturnTransition(Transition paramTransition) {}
  
  public void setSharedElementEnterTransition(Transition paramTransition) {}
  
  public void setSharedElementExitTransition(Transition paramTransition) {}
  
  public void setSharedElementReenterTransition(Transition paramTransition) {}
  
  public void setSharedElementReturnTransition(Transition paramTransition) {}
  
  public void setSharedElementsUseOverlay(boolean paramBoolean) {}
  
  public void setSoftInputMode(int paramInt)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    if (paramInt != 0)
    {
      localLayoutParams.softInputMode = paramInt;
      this.mHasSoftInputMode = true;
    }
    else
    {
      this.mHasSoftInputMode = false;
    }
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public abstract void setStatusBarColor(int paramInt);
  
  public void setStatusBarContrastEnforced(boolean paramBoolean) {}
  
  public void setSustainedPerformanceMode(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 262144;
    } else {
      i = 0;
    }
    setPrivateFlags(i, 262144);
  }
  
  public void setSystemGestureExclusionRects(List<Rect> paramList)
  {
    throw new UnsupportedOperationException("window does not support gesture exclusion rects");
  }
  
  public void setTheme(int paramInt) {}
  
  public abstract void setTitle(CharSequence paramCharSequence);
  
  @Deprecated
  public abstract void setTitleColor(int paramInt);
  
  public void setTransitionBackgroundFadeDuration(long paramLong) {}
  
  public void setTransitionManager(TransitionManager paramTransitionManager)
  {
    throw new UnsupportedOperationException();
  }
  
  public void setType(int paramInt)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.type = paramInt;
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public void setUiOptions(int paramInt) {}
  
  public void setUiOptions(int paramInt1, int paramInt2) {}
  
  public abstract void setVolumeControlStream(int paramInt);
  
  public void setWindowAnimations(int paramInt)
  {
    WindowManager.LayoutParams localLayoutParams = getAttributes();
    localLayoutParams.windowAnimations = paramInt;
    dispatchWindowAttributesChanged(localLayoutParams);
  }
  
  public final void setWindowControllerCallback(WindowControllerCallback paramWindowControllerCallback)
  {
    this.mWindowControllerCallback = paramWindowControllerCallback;
  }
  
  public void setWindowManager(WindowManager paramWindowManager, IBinder paramIBinder, String paramString)
  {
    setWindowManager(paramWindowManager, paramIBinder, paramString, false);
  }
  
  public void setWindowManager(WindowManager paramWindowManager, IBinder paramIBinder, String paramString, boolean paramBoolean)
  {
    this.mAppToken = paramIBinder;
    this.mAppName = paramString;
    this.mHardwareAccelerated = paramBoolean;
    paramIBinder = paramWindowManager;
    if (paramWindowManager == null) {
      paramIBinder = (WindowManager)this.mContext.getSystemService("window");
    }
    this.mWindowManager = ((WindowManagerImpl)paramIBinder).createLocalWindowManager(this);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  public boolean shouldCloseOnTouch(Context paramContext, MotionEvent paramMotionEvent)
  {
    int i;
    if (((paramMotionEvent.getAction() == 1) && (isOutOfBounds(paramContext, paramMotionEvent))) || (paramMotionEvent.getAction() == 4)) {
      i = 1;
    } else {
      i = 0;
    }
    return (this.mCloseOnTouchOutside) && (peekDecorView() != null) && (i != 0);
  }
  
  public abstract boolean superDispatchGenericMotionEvent(MotionEvent paramMotionEvent);
  
  public abstract boolean superDispatchKeyEvent(KeyEvent paramKeyEvent);
  
  public abstract boolean superDispatchKeyShortcutEvent(KeyEvent paramKeyEvent);
  
  public abstract boolean superDispatchTouchEvent(MotionEvent paramMotionEvent);
  
  public abstract boolean superDispatchTrackballEvent(MotionEvent paramMotionEvent);
  
  public abstract void takeInputQueue(InputQueue.Callback paramCallback);
  
  public abstract void takeKeyEvents(boolean paramBoolean);
  
  public abstract void takeSurface(SurfaceHolder.Callback2 paramCallback2);
  
  public abstract void togglePanel(int paramInt, KeyEvent paramKeyEvent);
  
  public static abstract interface Callback
  {
    public abstract boolean dispatchGenericMotionEvent(MotionEvent paramMotionEvent);
    
    public abstract boolean dispatchKeyEvent(KeyEvent paramKeyEvent);
    
    public abstract boolean dispatchKeyShortcutEvent(KeyEvent paramKeyEvent);
    
    public abstract boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract boolean dispatchTouchEvent(MotionEvent paramMotionEvent);
    
    public abstract boolean dispatchTrackballEvent(MotionEvent paramMotionEvent);
    
    public abstract void onActionModeFinished(ActionMode paramActionMode);
    
    public abstract void onActionModeStarted(ActionMode paramActionMode);
    
    public abstract void onAttachedToWindow();
    
    public abstract void onContentChanged();
    
    public abstract boolean onCreatePanelMenu(int paramInt, Menu paramMenu);
    
    public abstract View onCreatePanelView(int paramInt);
    
    public abstract void onDetachedFromWindow();
    
    public abstract boolean onMenuItemSelected(int paramInt, MenuItem paramMenuItem);
    
    public abstract boolean onMenuOpened(int paramInt, Menu paramMenu);
    
    public abstract void onPanelClosed(int paramInt, Menu paramMenu);
    
    public void onPointerCaptureChanged(boolean paramBoolean) {}
    
    public abstract boolean onPreparePanel(int paramInt, View paramView, Menu paramMenu);
    
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> paramList, Menu paramMenu, int paramInt) {}
    
    public abstract boolean onSearchRequested();
    
    public abstract boolean onSearchRequested(SearchEvent paramSearchEvent);
    
    public abstract void onWindowAttributesChanged(WindowManager.LayoutParams paramLayoutParams);
    
    public abstract void onWindowFocusChanged(boolean paramBoolean);
    
    public abstract ActionMode onWindowStartingActionMode(ActionMode.Callback paramCallback);
    
    public abstract ActionMode onWindowStartingActionMode(ActionMode.Callback paramCallback, int paramInt);
  }
  
  public static abstract interface OnFrameMetricsAvailableListener
  {
    public abstract void onFrameMetricsAvailable(Window paramWindow, FrameMetrics paramFrameMetrics, int paramInt);
  }
  
  public static abstract interface OnRestrictedCaptionAreaChangedListener
  {
    public abstract void onRestrictedCaptionAreaChanged(Rect paramRect);
  }
  
  public static abstract interface OnWindowDismissedCallback
  {
    public abstract void onWindowDismissed(boolean paramBoolean1, boolean paramBoolean2);
  }
  
  public static abstract interface OnWindowSwipeDismissedCallback
  {
    public abstract void onWindowSwipeDismissed();
  }
  
  public static abstract interface WindowControllerCallback
  {
    public abstract void enterPictureInPictureModeIfPossible();
    
    public abstract boolean isTaskRoot();
    
    public abstract void toggleFreeformWindowingMode()
      throws RemoteException;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/Window.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */