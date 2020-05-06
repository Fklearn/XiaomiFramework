package android.view;

import android.annotation.UnsupportedAppUsage;
import android.app.AppGlobals;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.SparseArray;

public class ViewConfiguration
{
  private static final int A11Y_SHORTCUT_KEY_TIMEOUT = 3000;
  private static final int A11Y_SHORTCUT_KEY_TIMEOUT_AFTER_CONFIRMATION = 1000;
  private static final long ACTION_MODE_HIDE_DURATION_DEFAULT = 2000L;
  private static final float AMBIGUOUS_GESTURE_MULTIPLIER = 2.0F;
  private static final int DEFAULT_LONG_PRESS_TIMEOUT = 500;
  private static final int DEFAULT_MULTI_PRESS_TIMEOUT = 300;
  private static final int DOUBLE_TAP_MIN_TIME = 40;
  private static final int DOUBLE_TAP_SLOP = 100;
  private static final int DOUBLE_TAP_TIMEOUT = 300;
  private static final int DOUBLE_TAP_TOUCH_SLOP = 8;
  private static final int EDGE_SLOP = 12;
  private static final int FADING_EDGE_LENGTH = 12;
  private static final int GLOBAL_ACTIONS_KEY_TIMEOUT = 500;
  private static final int HAS_PERMANENT_MENU_KEY_AUTODETECT = 0;
  private static final int HAS_PERMANENT_MENU_KEY_FALSE = 2;
  private static final int HAS_PERMANENT_MENU_KEY_TRUE = 1;
  private static final float HORIZONTAL_SCROLL_FACTOR = 64.0F;
  private static final int HOVER_TAP_SLOP = 20;
  private static final int HOVER_TAP_TIMEOUT = 150;
  private static final int HOVER_TOOLTIP_HIDE_SHORT_TIMEOUT = 3000;
  private static final int HOVER_TOOLTIP_HIDE_TIMEOUT = 15000;
  private static final int HOVER_TOOLTIP_SHOW_TIMEOUT = 500;
  private static final int JUMP_TAP_TIMEOUT = 500;
  private static final int KEY_REPEAT_DELAY = 50;
  private static final int LONG_PRESS_TOOLTIP_HIDE_TIMEOUT = 1500;
  @Deprecated
  private static final int MAXIMUM_DRAWING_CACHE_SIZE = 1536000;
  private static final int MAXIMUM_FLING_VELOCITY = 8000;
  private static final int MINIMUM_FLING_VELOCITY = 50;
  private static final int MIN_SCROLLBAR_TOUCH_TARGET = 48;
  private static final int OVERFLING_DISTANCE = 6;
  private static final int OVERSCROLL_DISTANCE = 0;
  private static final int PAGING_TOUCH_SLOP = 16;
  private static final int PRESSED_STATE_DURATION = 64;
  private static final int PRESSED_STATE_DURATION_LISTVIEW = 8;
  private static final int SCREENSHOT_CHORD_KEY_TIMEOUT = 500;
  private static final int SCROLL_BAR_DEFAULT_DELAY = 300;
  private static final int SCROLL_BAR_FADE_DURATION = 250;
  private static final int SCROLL_BAR_SIZE = 4;
  @UnsupportedAppUsage
  private static final float SCROLL_FRICTION = 0.015F;
  private static final long SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS = 100L;
  private static final int TAP_TIMEOUT = 100;
  private static final int TOUCH_SLOP = 8;
  private static final float VERTICAL_SCROLL_FACTOR = 64.0F;
  private static final int WINDOW_TOUCH_SLOP = 16;
  private static final int ZOOM_CONTROLS_TIMEOUT = 3000;
  @UnsupportedAppUsage
  static final SparseArray<ViewConfiguration> sConfigurations = new SparseArray(2);
  private final boolean mConstructedWithContext;
  private final int mDoubleTapSlop;
  private final int mDoubleTapTouchSlop;
  private final int mEdgeSlop;
  private final int mFadingEdgeLength;
  @UnsupportedAppUsage
  private final boolean mFadingMarqueeEnabled;
  private final long mGlobalActionsKeyTimeout;
  private final float mHorizontalScrollFactor;
  private final int mHoverSlop;
  private final int mMaximumDrawingCacheSize;
  private final int mMaximumFlingVelocity;
  private final int mMinScalingSpan;
  private final int mMinScrollbarTouchTarget;
  private final int mMinimumFlingVelocity;
  private final int mOverflingDistance;
  private final int mOverscrollDistance;
  private final int mPagingTouchSlop;
  private final long mScreenshotChordKeyTimeout;
  private final int mScrollbarSize;
  private final boolean mShowMenuShortcutsWhenKeyboardPresent;
  private final int mTouchSlop;
  private final float mVerticalScrollFactor;
  private final int mWindowTouchSlop;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768915L)
  private boolean sHasPermanentMenuKey;
  @UnsupportedAppUsage
  private boolean sHasPermanentMenuKeySet;
  
  @Deprecated
  public ViewConfiguration()
  {
    this.mConstructedWithContext = false;
    this.mEdgeSlop = 12;
    this.mFadingEdgeLength = 12;
    this.mMinimumFlingVelocity = 50;
    this.mMaximumFlingVelocity = 8000;
    this.mScrollbarSize = 4;
    this.mTouchSlop = 8;
    this.mHoverSlop = 4;
    this.mMinScrollbarTouchTarget = 48;
    this.mDoubleTapTouchSlop = 8;
    this.mPagingTouchSlop = 16;
    this.mDoubleTapSlop = 100;
    this.mWindowTouchSlop = 16;
    this.mMaximumDrawingCacheSize = 1536000;
    this.mOverscrollDistance = 0;
    this.mOverflingDistance = 6;
    this.mFadingMarqueeEnabled = true;
    this.mGlobalActionsKeyTimeout = 500L;
    this.mHorizontalScrollFactor = 64.0F;
    this.mVerticalScrollFactor = 64.0F;
    this.mShowMenuShortcutsWhenKeyboardPresent = false;
    this.mScreenshotChordKeyTimeout = 500L;
    this.mMinScalingSpan = 0;
  }
  
  private ViewConfiguration(Context paramContext)
  {
    this.mConstructedWithContext = true;
    Resources localResources = paramContext.getResources();
    Object localObject1 = localResources.getDisplayMetrics();
    Object localObject2 = localResources.getConfiguration();
    float f = ((DisplayMetrics)localObject1).density;
    if (((Configuration)localObject2).isLayoutSizeAtLeast(4)) {
      f = 1.5F * f;
    }
    this.mEdgeSlop = ((int)(f * 12.0F + 0.5F));
    this.mFadingEdgeLength = ((int)(12.0F * f + 0.5F));
    this.mScrollbarSize = localResources.getDimensionPixelSize(17105076);
    this.mDoubleTapSlop = ((int)(100.0F * f + 0.5F));
    this.mWindowTouchSlop = ((int)(16.0F * f + 0.5F));
    localObject2 = ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay();
    localObject1 = new Point();
    ((Display)localObject2).getRealSize((Point)localObject1);
    this.mMaximumDrawingCacheSize = (((Point)localObject1).x * 4 * ((Point)localObject1).y);
    this.mOverscrollDistance = ((int)(0.0F * f + 0.5F));
    this.mOverflingDistance = ((int)(6.0F * f + 0.5F));
    if (!this.sHasPermanentMenuKeySet)
    {
      i = localResources.getInteger(17694875);
      if (i != 1)
      {
        if (i != 2)
        {
          localObject2 = WindowManagerGlobal.getWindowManagerService();
          try
          {
            boolean bool;
            if (!((IWindowManager)localObject2).hasNavigationBar(paramContext.getDisplayId())) {
              bool = true;
            } else {
              bool = false;
            }
            this.sHasPermanentMenuKey = bool;
            this.sHasPermanentMenuKeySet = true;
          }
          catch (RemoteException paramContext)
          {
            this.sHasPermanentMenuKey = false;
          }
        }
        else
        {
          this.sHasPermanentMenuKey = false;
          this.sHasPermanentMenuKeySet = true;
        }
      }
      else
      {
        this.sHasPermanentMenuKey = true;
        this.sHasPermanentMenuKeySet = true;
      }
    }
    this.mFadingMarqueeEnabled = localResources.getBoolean(17891558);
    this.mTouchSlop = localResources.getDimensionPixelSize(17105079);
    this.mHoverSlop = localResources.getDimensionPixelSize(17105078);
    this.mMinScrollbarTouchTarget = localResources.getDimensionPixelSize(17105065);
    int i = this.mTouchSlop;
    this.mPagingTouchSlop = (i * 2);
    this.mDoubleTapTouchSlop = i;
    this.mMinimumFlingVelocity = localResources.getDimensionPixelSize(17105081);
    this.mMaximumFlingVelocity = localResources.getDimensionPixelSize(17105080);
    this.mGlobalActionsKeyTimeout = localResources.getInteger(17694812);
    this.mHorizontalScrollFactor = localResources.getDimensionPixelSize(17105060);
    this.mVerticalScrollFactor = localResources.getDimensionPixelSize(17105077);
    this.mShowMenuShortcutsWhenKeyboardPresent = localResources.getBoolean(17891517);
    this.mMinScalingSpan = localResources.getDimensionPixelSize(17105063);
    this.mScreenshotChordKeyTimeout = localResources.getInteger(17694893);
  }
  
  public static ViewConfiguration get(Context paramContext)
  {
    int i = (int)(paramContext.getResources().getDisplayMetrics().density * 100.0F);
    ViewConfiguration localViewConfiguration1 = (ViewConfiguration)sConfigurations.get(i);
    ViewConfiguration localViewConfiguration2 = localViewConfiguration1;
    if (localViewConfiguration1 == null)
    {
      localViewConfiguration2 = new ViewConfiguration(paramContext);
      sConfigurations.put(i, localViewConfiguration2);
    }
    return localViewConfiguration2;
  }
  
  public static float getAmbiguousGestureMultiplier()
  {
    return 2.0F;
  }
  
  public static long getDefaultActionModeHideDuration()
  {
    return 2000L;
  }
  
  @UnsupportedAppUsage
  public static int getDoubleTapMinTime()
  {
    return 40;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public static int getDoubleTapSlop()
  {
    return 100;
  }
  
  public static int getDoubleTapTimeout()
  {
    return 300;
  }
  
  @Deprecated
  public static int getEdgeSlop()
  {
    return 12;
  }
  
  @Deprecated
  public static int getFadingEdgeLength()
  {
    return 12;
  }
  
  @Deprecated
  public static long getGlobalActionKeyTimeout()
  {
    return 500L;
  }
  
  @UnsupportedAppUsage
  public static int getHoverTapSlop()
  {
    return 20;
  }
  
  public static int getHoverTapTimeout()
  {
    return 150;
  }
  
  public static int getHoverTooltipHideShortTimeout()
  {
    return 3000;
  }
  
  public static int getHoverTooltipHideTimeout()
  {
    return 15000;
  }
  
  public static int getHoverTooltipShowTimeout()
  {
    return 500;
  }
  
  public static int getJumpTapTimeout()
  {
    return 500;
  }
  
  public static int getKeyRepeatDelay()
  {
    return 50;
  }
  
  public static int getKeyRepeatTimeout()
  {
    return getLongPressTimeout();
  }
  
  public static int getLongPressTimeout()
  {
    return AppGlobals.getIntCoreSetting("long_press_timeout", 500);
  }
  
  public static int getLongPressTooltipHideTimeout()
  {
    return 1500;
  }
  
  @Deprecated
  public static int getMaximumDrawingCacheSize()
  {
    return 1536000;
  }
  
  @Deprecated
  public static int getMaximumFlingVelocity()
  {
    return 8000;
  }
  
  @Deprecated
  public static int getMinimumFlingVelocity()
  {
    return 50;
  }
  
  public static int getMultiPressTimeout()
  {
    return AppGlobals.getIntCoreSetting("multi_press_timeout", 300);
  }
  
  public static int getPressedStateDuration()
  {
    return 64;
  }
  
  public static int getPressedStateDurationForListview()
  {
    return 8;
  }
  
  public static int getScrollBarFadeDuration()
  {
    return 250;
  }
  
  @Deprecated
  public static int getScrollBarSize()
  {
    return 4;
  }
  
  public static int getScrollDefaultDelay()
  {
    return 300;
  }
  
  public static float getScrollFriction()
  {
    return 0.015F;
  }
  
  public static long getSendRecurringAccessibilityEventsInterval()
  {
    return 100L;
  }
  
  public static int getTapTimeout()
  {
    return 100;
  }
  
  @Deprecated
  public static int getTouchSlop()
  {
    return 8;
  }
  
  @Deprecated
  public static int getWindowTouchSlop()
  {
    return 16;
  }
  
  public static long getZoomControlsTimeout()
  {
    return 3000L;
  }
  
  public long getAccessibilityShortcutKeyTimeout()
  {
    return 3000L;
  }
  
  public long getAccessibilityShortcutKeyTimeoutAfterConfirmation()
  {
    return 1000L;
  }
  
  public long getDeviceGlobalActionKeyTimeout()
  {
    return this.mGlobalActionsKeyTimeout;
  }
  
  public int getScaledDoubleTapSlop()
  {
    return this.mDoubleTapSlop;
  }
  
  @UnsupportedAppUsage
  public int getScaledDoubleTapTouchSlop()
  {
    return this.mDoubleTapTouchSlop;
  }
  
  public int getScaledEdgeSlop()
  {
    return this.mEdgeSlop;
  }
  
  public int getScaledFadingEdgeLength()
  {
    return this.mFadingEdgeLength;
  }
  
  public float getScaledHorizontalScrollFactor()
  {
    return this.mHorizontalScrollFactor;
  }
  
  public int getScaledHoverSlop()
  {
    return this.mHoverSlop;
  }
  
  public int getScaledMaximumDrawingCacheSize()
  {
    return this.mMaximumDrawingCacheSize;
  }
  
  public int getScaledMaximumFlingVelocity()
  {
    return this.mMaximumFlingVelocity;
  }
  
  public int getScaledMinScrollbarTouchTarget()
  {
    return this.mMinScrollbarTouchTarget;
  }
  
  public int getScaledMinimumFlingVelocity()
  {
    return this.mMinimumFlingVelocity;
  }
  
  public int getScaledMinimumScalingSpan()
  {
    if (this.mConstructedWithContext) {
      return this.mMinScalingSpan;
    }
    throw new IllegalStateException("Min scaling span cannot be determined when this method is called on a ViewConfiguration that was instantiated using a constructor with no Context parameter");
  }
  
  public int getScaledOverflingDistance()
  {
    return this.mOverflingDistance;
  }
  
  public int getScaledOverscrollDistance()
  {
    return this.mOverscrollDistance;
  }
  
  public int getScaledPagingTouchSlop()
  {
    return this.mPagingTouchSlop;
  }
  
  public int getScaledScrollBarSize()
  {
    return this.mScrollbarSize;
  }
  
  public int getScaledScrollFactor()
  {
    return (int)this.mVerticalScrollFactor;
  }
  
  public int getScaledTouchSlop()
  {
    return this.mTouchSlop;
  }
  
  public float getScaledVerticalScrollFactor()
  {
    return this.mVerticalScrollFactor;
  }
  
  public int getScaledWindowTouchSlop()
  {
    return this.mWindowTouchSlop;
  }
  
  public long getScreenshotChordKeyTimeout()
  {
    return this.mScreenshotChordKeyTimeout;
  }
  
  public boolean hasPermanentMenuKey()
  {
    return this.sHasPermanentMenuKey;
  }
  
  @UnsupportedAppUsage
  public boolean isFadingMarqueeEnabled()
  {
    return this.mFadingMarqueeEnabled;
  }
  
  public boolean shouldShowMenuShortcutsWhenKeyboardPresent()
  {
    return this.mShowMenuShortcutsWhenKeyboardPresent;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */