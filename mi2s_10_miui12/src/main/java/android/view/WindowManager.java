package android.view;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.app.WindowConfiguration;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Objects;

public abstract interface WindowManager
  extends ViewManager
{
  public static final int DOCKED_BOTTOM = 4;
  public static final int DOCKED_INVALID = -1;
  public static final int DOCKED_LEFT = 1;
  public static final int DOCKED_RIGHT = 3;
  public static final int DOCKED_TOP = 2;
  public static final String INPUT_CONSUMER_NAVIGATION = "nav_input_consumer";
  public static final String INPUT_CONSUMER_PIP = "pip_input_consumer";
  public static final String INPUT_CONSUMER_RECENTS_ANIMATION = "recents_animation_input_consumer";
  public static final String INPUT_CONSUMER_WALLPAPER = "wallpaper_input_consumer";
  public static final String PARCEL_KEY_SHORTCUTS_ARRAY = "shortcuts_array";
  public static final int REMOVE_CONTENT_MODE_DESTROY = 2;
  public static final int REMOVE_CONTENT_MODE_MOVE_TO_PRIMARY = 1;
  public static final int REMOVE_CONTENT_MODE_UNDEFINED = 0;
  public static final int TAKE_SCREENSHOT_FULLSCREEN = 1;
  public static final int TAKE_SCREENSHOT_SELECTED_REGION = 2;
  public static final int TRANSIT_ACTIVITY_CLOSE = 7;
  public static final int TRANSIT_ACTIVITY_OPEN = 6;
  public static final int TRANSIT_ACTIVITY_RELAUNCH = 18;
  public static final int TRANSIT_CRASHING_ACTIVITY_CLOSE = 26;
  public static final int TRANSIT_DOCK_TASK_FROM_RECENTS = 19;
  public static final int TRANSIT_FLAG_KEYGUARD_GOING_AWAY_NO_ANIMATION = 2;
  public static final int TRANSIT_FLAG_KEYGUARD_GOING_AWAY_TO_SHADE = 1;
  public static final int TRANSIT_FLAG_KEYGUARD_GOING_AWAY_WITH_WALLPAPER = 4;
  public static final int TRANSIT_KEYGUARD_GOING_AWAY = 20;
  public static final int TRANSIT_KEYGUARD_GOING_AWAY_ON_WALLPAPER = 21;
  public static final int TRANSIT_KEYGUARD_OCCLUDE = 22;
  public static final int TRANSIT_KEYGUARD_UNOCCLUDE = 23;
  public static final int TRANSIT_NONE = 0;
  public static final int TRANSIT_TASK_CHANGE_WINDOWING_MODE = 27;
  public static final int TRANSIT_TASK_CLOSE = 9;
  public static final int TRANSIT_TASK_IN_PLACE = 17;
  public static final int TRANSIT_TASK_OPEN = 8;
  public static final int TRANSIT_TASK_OPEN_BEHIND = 16;
  public static final int TRANSIT_TASK_TO_BACK = 11;
  public static final int TRANSIT_TASK_TO_FRONT = 10;
  public static final int TRANSIT_TRANSLUCENT_ACTIVITY_CLOSE = 25;
  public static final int TRANSIT_TRANSLUCENT_ACTIVITY_OPEN = 24;
  public static final int TRANSIT_UNSET = -1;
  public static final int TRANSIT_WALLPAPER_CLOSE = 12;
  public static final int TRANSIT_WALLPAPER_INTRA_CLOSE = 15;
  public static final int TRANSIT_WALLPAPER_INTRA_OPEN = 14;
  public static final int TRANSIT_WALLPAPER_OPEN = 13;
  
  @SystemApi
  public abstract Region getCurrentImeTouchRegion();
  
  public abstract Display getDefaultDisplay();
  
  public abstract void removeViewImmediate(View paramView);
  
  public abstract void requestAppKeyboardShortcuts(KeyboardShortcutsReceiver paramKeyboardShortcutsReceiver, int paramInt);
  
  public void setShouldShowIme(int paramInt, boolean paramBoolean) {}
  
  public void setShouldShowSystemDecors(int paramInt, boolean paramBoolean) {}
  
  public void setShouldShowWithInsecureKeyguard(int paramInt, boolean paramBoolean) {}
  
  public boolean shouldShowIme(int paramInt)
  {
    return false;
  }
  
  public boolean shouldShowSystemDecors(int paramInt)
  {
    return false;
  }
  
  public static class BadTokenException
    extends RuntimeException
  {
    public BadTokenException() {}
    
    public BadTokenException(String paramString)
    {
      super();
    }
  }
  
  public static class InvalidDisplayException
    extends RuntimeException
  {
    public InvalidDisplayException() {}
    
    public InvalidDisplayException(String paramString)
    {
      super();
    }
  }
  
  public static abstract interface KeyboardShortcutsReceiver
  {
    public abstract void onKeyboardShortcutsReceived(List<KeyboardShortcutGroup> paramList);
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
    implements Parcelable
  {
    public static final int ACCESSIBILITY_ANCHOR_CHANGED = 16777216;
    public static final int ACCESSIBILITY_TITLE_CHANGED = 33554432;
    public static final int ALPHA_CHANGED = 128;
    public static final int ANIMATION_CHANGED = 16;
    public static final float BLUR_BLACK_UNSET = -1.0F;
    public static final int BLUR_CROP_CHANGED = 536870912;
    public static final int BLUR_MODE_BLACK_SATURATION = 0;
    public static final int BLUR_MODE_COLOR_BURN = 3;
    public static final int BLUR_MODE_COLOR_DODGE = 1;
    public static final int BLUR_MODE_WHITE_SATURATION = 2;
    public static final int BLUR_RATIO_CHANGED = 1073741824;
    public static final float BRIGHTNESS_OVERRIDE_FULL = 1.0F;
    public static final float BRIGHTNESS_OVERRIDE_NONE = -1.0F;
    public static final float BRIGHTNESS_OVERRIDE_OFF = 0.0F;
    public static final int BUTTON_BRIGHTNESS_CHANGED = 8192;
    public static final int COLOR_MODE_CHANGED = 67108864;
    public static final Parcelable.Creator<LayoutParams> CREATOR = new Parcelable.Creator()
    {
      public WindowManager.LayoutParams createFromParcel(Parcel paramAnonymousParcel)
      {
        return new WindowManager.LayoutParams(paramAnonymousParcel);
      }
      
      public WindowManager.LayoutParams[] newArray(int paramAnonymousInt)
      {
        return new WindowManager.LayoutParams[paramAnonymousInt];
      }
    };
    public static final int DIM_AMOUNT_CHANGED = 32;
    public static final int EVERYTHING_CHANGED = -1;
    public static final int FIRST_APPLICATION_WINDOW = 1;
    public static final int FIRST_SUB_WINDOW = 1000;
    public static final int FIRST_SYSTEM_WINDOW = 2000;
    public static final int FLAGS_CHANGED = 4;
    public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON = 1;
    public static final int FLAG_ALT_FOCUSABLE_IM = 131072;
    @Deprecated
    public static final int FLAG_BLUR_BEHIND = 4;
    public static final int FLAG_DIM_BEHIND = 2;
    @Deprecated
    public static final int FLAG_DISMISS_KEYGUARD = 4194304;
    @Deprecated
    public static final int FLAG_DITHER = 4096;
    public static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = Integer.MIN_VALUE;
    public static final int FLAG_FORCE_NOT_FULLSCREEN = 2048;
    public static final int FLAG_FULLSCREEN = 1024;
    public static final int FLAG_HARDWARE_ACCELERATED = 16777216;
    public static final int FLAG_IGNORE_CHEEK_PRESSES = 32768;
    public static final int FLAG_KEEP_SCREEN_ON = 128;
    public static final int FLAG_LAYOUT_ATTACHED_IN_DECOR = 1073741824;
    public static final int FLAG_LAYOUT_INSET_DECOR = 65536;
    public static final int FLAG_LAYOUT_IN_OVERSCAN = 33554432;
    public static final int FLAG_LAYOUT_IN_SCREEN = 256;
    public static final int FLAG_LAYOUT_NO_LIMITS = 512;
    public static final int FLAG_LOCAL_FOCUS_MODE = 268435456;
    public static final int FLAG_NOT_FOCUSABLE = 8;
    public static final int FLAG_NOT_TOUCHABLE = 16;
    public static final int FLAG_NOT_TOUCH_MODAL = 32;
    public static final int FLAG_SCALED = 16384;
    public static final int FLAG_SECURE = 8192;
    public static final int FLAG_SHOW_WALLPAPER = 1048576;
    @Deprecated
    public static final int FLAG_SHOW_WHEN_LOCKED = 524288;
    @UnsupportedAppUsage
    public static final int FLAG_SLIPPERY = 536870912;
    public static final int FLAG_SPLIT_TOUCH = 8388608;
    @Deprecated
    public static final int FLAG_TOUCHABLE_WHEN_WAKING = 64;
    public static final int FLAG_TRANSLUCENT_NAVIGATION = 134217728;
    public static final int FLAG_TRANSLUCENT_STATUS = 67108864;
    @Deprecated
    public static final int FLAG_TURN_SCREEN_ON = 2097152;
    public static final int FLAG_WATCH_OUTSIDE_TOUCH = 262144;
    public static final int FORMAT_CHANGED = 8;
    public static final int INPUT_FEATURES_CHANGED = 65536;
    public static final int INPUT_FEATURE_DISABLE_POINTER_GESTURES = 1;
    @UnsupportedAppUsage
    public static final int INPUT_FEATURE_DISABLE_USER_ACTIVITY = 4;
    public static final int INPUT_FEATURE_NO_INPUT_CHANNEL = 2;
    public static final int INVALID_WINDOW_TYPE = -1;
    public static final int LAST_APPLICATION_WINDOW = 99;
    public static final int LAST_SUB_WINDOW = 1999;
    public static final int LAST_SYSTEM_WINDOW = 2999;
    public static final int LAYOUT_CHANGED = 1;
    @Deprecated
    public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS = 1;
    public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT = 0;
    public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER = 2;
    public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES = 1;
    public static final int MEMORY_TYPE_CHANGED = 256;
    @Deprecated
    public static final int MEMORY_TYPE_GPU = 2;
    @Deprecated
    public static final int MEMORY_TYPE_HARDWARE = 1;
    @Deprecated
    public static final int MEMORY_TYPE_NORMAL = 0;
    @Deprecated
    public static final int MEMORY_TYPE_PUSH_BUFFERS = 3;
    public static final int NEEDS_MENU_KEY_CHANGED = 4194304;
    @UnsupportedAppUsage
    public static final int NEEDS_MENU_SET_FALSE = 2;
    @UnsupportedAppUsage
    public static final int NEEDS_MENU_SET_TRUE = 1;
    public static final int NEEDS_MENU_UNSET = 0;
    public static final int PREFERRED_DISPLAY_MODE_ID = 8388608;
    public static final int PREFERRED_REFRESH_RATE_CHANGED = 2097152;
    public static final int PRIVATE_FLAGS_CHANGED = 131072;
    public static final int PRIVATE_FLAG_COLOR_SPACE_AGNOSTIC = 16777216;
    public static final int PRIVATE_FLAG_COMPATIBLE_WINDOW = 128;
    public static final int PRIVATE_FLAG_DISABLE_WALLPAPER_TOUCH_EVENTS = 2048;
    public static final int PRIVATE_FLAG_ENABLE_SURFACE_BLUR = 8;
    public static final int PRIVATE_FLAG_FAKE_HARDWARE_ACCELERATED = 1;
    public static final int PRIVATE_FLAG_FORCE_DECOR_VIEW_VISIBILITY = 16384;
    public static final int PRIVATE_FLAG_FORCE_DRAW_BAR_BACKGROUNDS = 131072;
    public static final int PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED = 2;
    public static final int PRIVATE_FLAG_FORCE_STATUS_BAR_VISIBLE_TRANSPARENT = 4096;
    public static final int PRIVATE_FLAG_INHERIT_TRANSLUCENT_DECOR = 512;
    public static final int PRIVATE_FLAG_IS_ROUNDED_CORNERS_OVERLAY = 1048576;
    public static final int PRIVATE_FLAG_IS_SCREEN_DECOR = 4194304;
    public static final int PRIVATE_FLAG_KEYGUARD = 1024;
    public static final int PRIVATE_FLAG_LAYOUT_CHILD_WINDOW_IN_PARENT_FRAME = 65536;
    public static final int PRIVATE_FLAG_NO_MOVE_ANIMATION = 64;
    public static final int PRIVATE_FLAG_PRESERVE_GEOMETRY = 8192;
    @UnsupportedAppUsage
    public static final int PRIVATE_FLAG_SHOW_FOR_ALL_USERS = 16;
    public static final int PRIVATE_FLAG_STATUS_FORCE_SHOW_NAVIGATION = 8388608;
    public static final int PRIVATE_FLAG_SUSTAINED_PERFORMANCE_MODE = 262144;
    public static final int PRIVATE_FLAG_SYSTEM_ERROR = 256;
    public static final int PRIVATE_FLAG_WANTS_OFFSET_NOTIFICATIONS = 4;
    public static final int PRIVATE_FLAG_WILL_NOT_REPLACE_ON_RELAUNCH = 32768;
    public static final int ROTATION_ANIMATION_CHANGED = 4096;
    public static final int ROTATION_ANIMATION_CROSSFADE = 1;
    public static final int ROTATION_ANIMATION_JUMPCUT = 2;
    public static final int ROTATION_ANIMATION_ROTATE = 0;
    public static final int ROTATION_ANIMATION_SEAMLESS = 3;
    public static final int ROTATION_ANIMATION_UNSPECIFIED = -1;
    public static final int SCREEN_BRIGHTNESS_CHANGED = 2048;
    public static final int SCREEN_ORIENTATION_CHANGED = 1024;
    public static final int SOFT_INPUT_ADJUST_NOTHING = 48;
    public static final int SOFT_INPUT_ADJUST_PAN = 32;
    public static final int SOFT_INPUT_ADJUST_RESIZE = 16;
    public static final int SOFT_INPUT_ADJUST_UNSPECIFIED = 0;
    public static final int SOFT_INPUT_IS_FORWARD_NAVIGATION = 256;
    public static final int SOFT_INPUT_MASK_ADJUST = 240;
    public static final int SOFT_INPUT_MASK_STATE = 15;
    public static final int SOFT_INPUT_MODE_CHANGED = 512;
    public static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3;
    public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
    public static final int SOFT_INPUT_STATE_HIDDEN = 2;
    public static final int SOFT_INPUT_STATE_UNCHANGED = 1;
    public static final int SOFT_INPUT_STATE_UNSPECIFIED = 0;
    public static final int SOFT_INPUT_STATE_VISIBLE = 4;
    public static final int SURFACE_INSETS_CHANGED = 1048576;
    @SystemApi
    public static final int SYSTEM_FLAG_HIDE_NON_SYSTEM_OVERLAY_WINDOWS = 524288;
    public static final int SYSTEM_UI_LISTENER_CHANGED = 32768;
    public static final int SYSTEM_UI_VISIBILITY_CHANGED = 16384;
    public static final int TITLE_CHANGED = 64;
    public static final int TRANSLUCENT_FLAGS_CHANGED = 524288;
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 2032;
    public static final int TYPE_APPLICATION = 2;
    public static final int TYPE_APPLICATION_ABOVE_SUB_PANEL = 1005;
    public static final int TYPE_APPLICATION_ATTACHED_DIALOG = 1003;
    public static final int TYPE_APPLICATION_MEDIA = 1001;
    @UnsupportedAppUsage
    public static final int TYPE_APPLICATION_MEDIA_OVERLAY = 1004;
    public static final int TYPE_APPLICATION_OVERLAY = 2038;
    public static final int TYPE_APPLICATION_PANEL = 1000;
    public static final int TYPE_APPLICATION_STARTING = 3;
    public static final int TYPE_APPLICATION_SUB_PANEL = 1002;
    public static final int TYPE_BASE_APPLICATION = 1;
    public static final int TYPE_BOOT_PROGRESS = 2021;
    public static final int TYPE_CHANGED = 2;
    @UnsupportedAppUsage
    public static final int TYPE_DISPLAY_OVERLAY = 2026;
    public static final int TYPE_DOCK_DIVIDER = 2034;
    public static final int TYPE_DRAG = 2016;
    public static final int TYPE_DRAWN_APPLICATION = 4;
    public static final int TYPE_DREAM = 2023;
    public static final int TYPE_INPUT_CONSUMER = 2022;
    public static final int TYPE_INPUT_METHOD = 2011;
    public static final int TYPE_INPUT_METHOD_DIALOG = 2012;
    public static final int TYPE_KEYGUARD = 2004;
    public static final int TYPE_KEYGUARD_DIALOG = 2009;
    public static final int TYPE_MAGNIFICATION_OVERLAY = 2027;
    public static final int TYPE_NAVIGATION_BAR = 2019;
    public static final int TYPE_NAVIGATION_BAR_PANEL = 2024;
    @Deprecated
    public static final int TYPE_PHONE = 2002;
    public static final int TYPE_POINTER = 2018;
    public static final int TYPE_PRESENTATION = 2037;
    @Deprecated
    public static final int TYPE_PRIORITY_PHONE = 2007;
    public static final int TYPE_PRIVATE_PRESENTATION = 2030;
    public static final int TYPE_QS_DIALOG = 2035;
    public static final int TYPE_SCREENSHOT = 2036;
    public static final int TYPE_SEARCH_BAR = 2001;
    @UnsupportedAppUsage
    public static final int TYPE_SECURE_SYSTEM_OVERLAY = 2015;
    public static final int TYPE_STATUS_BAR = 2000;
    public static final int TYPE_STATUS_BAR_PANEL = 2014;
    public static final int TYPE_STATUS_BAR_SUB_PANEL = 2017;
    @Deprecated
    public static final int TYPE_SYSTEM_ALERT = 2003;
    public static final int TYPE_SYSTEM_DIALOG = 2008;
    @Deprecated
    public static final int TYPE_SYSTEM_ERROR = 2010;
    @Deprecated
    public static final int TYPE_SYSTEM_OVERLAY = 2006;
    @Deprecated
    public static final int TYPE_TOAST = 2005;
    public static final int TYPE_VOICE_INTERACTION = 2031;
    public static final int TYPE_VOICE_INTERACTION_STARTING = 2033;
    public static final int TYPE_VOLUME_OVERLAY = 2020;
    public static final int TYPE_WALLPAPER = 2013;
    public static final int USER_ACTIVITY_TIMEOUT_CHANGED = 262144;
    public long accessibilityIdOfAnchor;
    public CharSequence accessibilityTitle;
    public float alpha;
    public final Rect blurAbsoluteCrop;
    public float blurBlack;
    public int blurMode;
    public float blurRatio = 1.0F;
    public final Rect blurRelativeCrop;
    public float buttonBrightness;
    public float dimAmount;
    public int extraFlags;
    @ViewDebug.ExportedProperty(flagMapping={@ViewDebug.FlagToString(equals=1, mask=1, name="ALLOW_LOCK_WHILE_SCREEN_ON"), @ViewDebug.FlagToString(equals=2, mask=2, name="DIM_BEHIND"), @ViewDebug.FlagToString(equals=4, mask=4, name="BLUR_BEHIND"), @ViewDebug.FlagToString(equals=8, mask=8, name="NOT_FOCUSABLE"), @ViewDebug.FlagToString(equals=16, mask=16, name="NOT_TOUCHABLE"), @ViewDebug.FlagToString(equals=32, mask=32, name="NOT_TOUCH_MODAL"), @ViewDebug.FlagToString(equals=64, mask=64, name="TOUCHABLE_WHEN_WAKING"), @ViewDebug.FlagToString(equals=128, mask=128, name="KEEP_SCREEN_ON"), @ViewDebug.FlagToString(equals=256, mask=256, name="LAYOUT_IN_SCREEN"), @ViewDebug.FlagToString(equals=512, mask=512, name="LAYOUT_NO_LIMITS"), @ViewDebug.FlagToString(equals=1024, mask=1024, name="FULLSCREEN"), @ViewDebug.FlagToString(equals=2048, mask=2048, name="FORCE_NOT_FULLSCREEN"), @ViewDebug.FlagToString(equals=4096, mask=4096, name="DITHER"), @ViewDebug.FlagToString(equals=8192, mask=8192, name="SECURE"), @ViewDebug.FlagToString(equals=16384, mask=16384, name="SCALED"), @ViewDebug.FlagToString(equals=32768, mask=32768, name="IGNORE_CHEEK_PRESSES"), @ViewDebug.FlagToString(equals=65536, mask=65536, name="LAYOUT_INSET_DECOR"), @ViewDebug.FlagToString(equals=131072, mask=131072, name="ALT_FOCUSABLE_IM"), @ViewDebug.FlagToString(equals=262144, mask=262144, name="WATCH_OUTSIDE_TOUCH"), @ViewDebug.FlagToString(equals=524288, mask=524288, name="SHOW_WHEN_LOCKED"), @ViewDebug.FlagToString(equals=1048576, mask=1048576, name="SHOW_WALLPAPER"), @ViewDebug.FlagToString(equals=2097152, mask=2097152, name="TURN_SCREEN_ON"), @ViewDebug.FlagToString(equals=4194304, mask=4194304, name="DISMISS_KEYGUARD"), @ViewDebug.FlagToString(equals=8388608, mask=8388608, name="SPLIT_TOUCH"), @ViewDebug.FlagToString(equals=16777216, mask=16777216, name="HARDWARE_ACCELERATED"), @ViewDebug.FlagToString(equals=33554432, mask=33554432, name="LOCAL_FOCUS_MODE"), @ViewDebug.FlagToString(equals=67108864, mask=67108864, name="TRANSLUCENT_STATUS"), @ViewDebug.FlagToString(equals=134217728, mask=134217728, name="TRANSLUCENT_NAVIGATION"), @ViewDebug.FlagToString(equals=268435456, mask=268435456, name="LOCAL_FOCUS_MODE"), @ViewDebug.FlagToString(equals=536870912, mask=536870912, name="FLAG_SLIPPERY"), @ViewDebug.FlagToString(equals=1073741824, mask=1073741824, name="FLAG_LAYOUT_ATTACHED_IN_DECOR"), @ViewDebug.FlagToString(equals=Integer.MIN_VALUE, mask=Integer.MIN_VALUE, name="DRAWS_SYSTEM_BAR_BACKGROUNDS")}, formatToHexString=true)
    public int flags;
    public int format;
    public int gravity;
    public boolean hasManualSurfaceInsets;
    @UnsupportedAppUsage
    public boolean hasSystemUiListeners;
    @UnsupportedAppUsage
    public long hideTimeoutMilliseconds;
    public float horizontalMargin;
    @ViewDebug.ExportedProperty
    public float horizontalWeight;
    @UnsupportedAppUsage
    public int inputFeatures;
    public int layoutInDisplayCutoutMode;
    private int mColorMode;
    private int[] mCompatibilityParamsBackup;
    private CharSequence mTitle;
    @Deprecated
    public int memoryType;
    @UnsupportedAppUsage
    public int needsMenuKey;
    public String packageName;
    public int preferredDisplayModeId;
    @Deprecated
    public float preferredRefreshRate;
    public boolean preservePreviousSurfaceInsets;
    @ViewDebug.ExportedProperty(flagMapping={@ViewDebug.FlagToString(equals=1, mask=1, name="FAKE_HARDWARE_ACCELERATED"), @ViewDebug.FlagToString(equals=2, mask=2, name="FORCE_HARDWARE_ACCELERATED"), @ViewDebug.FlagToString(equals=4, mask=4, name="WANTS_OFFSET_NOTIFICATIONS"), @ViewDebug.FlagToString(equals=16, mask=16, name="SHOW_FOR_ALL_USERS"), @ViewDebug.FlagToString(equals=64, mask=64, name="NO_MOVE_ANIMATION"), @ViewDebug.FlagToString(equals=128, mask=128, name="COMPATIBLE_WINDOW"), @ViewDebug.FlagToString(equals=256, mask=256, name="SYSTEM_ERROR"), @ViewDebug.FlagToString(equals=512, mask=512, name="INHERIT_TRANSLUCENT_DECOR"), @ViewDebug.FlagToString(equals=1024, mask=1024, name="KEYGUARD"), @ViewDebug.FlagToString(equals=2048, mask=2048, name="DISABLE_WALLPAPER_TOUCH_EVENTS"), @ViewDebug.FlagToString(equals=4096, mask=4096, name="FORCE_STATUS_BAR_VISIBLE_TRANSPARENT"), @ViewDebug.FlagToString(equals=8192, mask=8192, name="PRESERVE_GEOMETRY"), @ViewDebug.FlagToString(equals=16384, mask=16384, name="FORCE_DECOR_VIEW_VISIBILITY"), @ViewDebug.FlagToString(equals=32768, mask=32768, name="WILL_NOT_REPLACE_ON_RELAUNCH"), @ViewDebug.FlagToString(equals=65536, mask=65536, name="LAYOUT_CHILD_WINDOW_IN_PARENT_FRAME"), @ViewDebug.FlagToString(equals=131072, mask=131072, name="FORCE_DRAW_STATUS_BAR_BACKGROUND"), @ViewDebug.FlagToString(equals=262144, mask=262144, name="SUSTAINED_PERFORMANCE_MODE"), @ViewDebug.FlagToString(equals=524288, mask=524288, name="HIDE_NON_SYSTEM_OVERLAY_WINDOWS"), @ViewDebug.FlagToString(equals=1048576, mask=1048576, name="IS_ROUNDED_CORNERS_OVERLAY"), @ViewDebug.FlagToString(equals=4194304, mask=4194304, name="IS_SCREEN_DECOR"), @ViewDebug.FlagToString(equals=8388608, mask=8388608, name="STATUS_FORCE_SHOW_NAVIGATION"), @ViewDebug.FlagToString(equals=16777216, mask=16777216, name="COLOR_SPACE_AGNOSTIC")})
    public int privateFlags;
    public int rotationAnimation;
    public float screenBrightness;
    public int screenOrientation;
    public int softInputMode;
    @UnsupportedAppUsage
    public int subtreeSystemUiVisibility;
    public final Rect surfaceInsets;
    public int systemUiVisibility;
    public IBinder token;
    @ViewDebug.ExportedProperty(mapping={@ViewDebug.IntToString(from=1, to="BASE_APPLICATION"), @ViewDebug.IntToString(from=2, to="APPLICATION"), @ViewDebug.IntToString(from=3, to="APPLICATION_STARTING"), @ViewDebug.IntToString(from=4, to="DRAWN_APPLICATION"), @ViewDebug.IntToString(from=1000, to="APPLICATION_PANEL"), @ViewDebug.IntToString(from=1001, to="APPLICATION_MEDIA"), @ViewDebug.IntToString(from=1002, to="APPLICATION_SUB_PANEL"), @ViewDebug.IntToString(from=1005, to="APPLICATION_ABOVE_SUB_PANEL"), @ViewDebug.IntToString(from=1003, to="APPLICATION_ATTACHED_DIALOG"), @ViewDebug.IntToString(from=1004, to="APPLICATION_MEDIA_OVERLAY"), @ViewDebug.IntToString(from=2000, to="STATUS_BAR"), @ViewDebug.IntToString(from=2001, to="SEARCH_BAR"), @ViewDebug.IntToString(from=2002, to="PHONE"), @ViewDebug.IntToString(from=2003, to="SYSTEM_ALERT"), @ViewDebug.IntToString(from=2005, to="TOAST"), @ViewDebug.IntToString(from=2006, to="SYSTEM_OVERLAY"), @ViewDebug.IntToString(from=2007, to="PRIORITY_PHONE"), @ViewDebug.IntToString(from=2008, to="SYSTEM_DIALOG"), @ViewDebug.IntToString(from=2009, to="KEYGUARD_DIALOG"), @ViewDebug.IntToString(from=2010, to="SYSTEM_ERROR"), @ViewDebug.IntToString(from=2011, to="INPUT_METHOD"), @ViewDebug.IntToString(from=2012, to="INPUT_METHOD_DIALOG"), @ViewDebug.IntToString(from=2013, to="WALLPAPER"), @ViewDebug.IntToString(from=2014, to="STATUS_BAR_PANEL"), @ViewDebug.IntToString(from=2015, to="SECURE_SYSTEM_OVERLAY"), @ViewDebug.IntToString(from=2016, to="DRAG"), @ViewDebug.IntToString(from=2017, to="STATUS_BAR_SUB_PANEL"), @ViewDebug.IntToString(from=2018, to="POINTER"), @ViewDebug.IntToString(from=2019, to="NAVIGATION_BAR"), @ViewDebug.IntToString(from=2020, to="VOLUME_OVERLAY"), @ViewDebug.IntToString(from=2021, to="BOOT_PROGRESS"), @ViewDebug.IntToString(from=2022, to="INPUT_CONSUMER"), @ViewDebug.IntToString(from=2023, to="DREAM"), @ViewDebug.IntToString(from=2024, to="NAVIGATION_BAR_PANEL"), @ViewDebug.IntToString(from=2026, to="DISPLAY_OVERLAY"), @ViewDebug.IntToString(from=2027, to="MAGNIFICATION_OVERLAY"), @ViewDebug.IntToString(from=2037, to="PRESENTATION"), @ViewDebug.IntToString(from=2030, to="PRIVATE_PRESENTATION"), @ViewDebug.IntToString(from=2031, to="VOICE_INTERACTION"), @ViewDebug.IntToString(from=2033, to="VOICE_INTERACTION_STARTING"), @ViewDebug.IntToString(from=2034, to="DOCK_DIVIDER"), @ViewDebug.IntToString(from=2035, to="QS_DIALOG"), @ViewDebug.IntToString(from=2036, to="SCREENSHOT"), @ViewDebug.IntToString(from=2038, to="APPLICATION_OVERLAY")})
    public int type;
    @UnsupportedAppUsage
    public long userActivityTimeout;
    public float verticalMargin;
    @ViewDebug.ExportedProperty
    public float verticalWeight;
    public int windowAnimations;
    @ViewDebug.ExportedProperty
    public int x;
    @ViewDebug.ExportedProperty
    public int y;
    
    public LayoutParams()
    {
      super(-1);
      this.blurAbsoluteCrop = new Rect(0, 0, -1, -1);
      this.blurRelativeCrop = new Rect(0, 0, 0, 0);
      this.blurBlack = -1.0F;
      this.blurMode = 0;
      this.needsMenuKey = 0;
      this.surfaceInsets = new Rect();
      this.preservePreviousSurfaceInsets = true;
      this.alpha = 1.0F;
      this.dimAmount = 1.0F;
      this.screenBrightness = -1.0F;
      this.buttonBrightness = -1.0F;
      this.rotationAnimation = 0;
      this.token = null;
      this.packageName = null;
      this.screenOrientation = -1;
      this.layoutInDisplayCutoutMode = 0;
      this.userActivityTimeout = -1L;
      this.accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
      this.hideTimeoutMilliseconds = -1L;
      this.mColorMode = 0;
      this.mCompatibilityParamsBackup = null;
      this.mTitle = null;
      this.type = 2;
      this.format = -1;
    }
    
    public LayoutParams(int paramInt)
    {
      super(-1);
      this.blurAbsoluteCrop = new Rect(0, 0, -1, -1);
      this.blurRelativeCrop = new Rect(0, 0, 0, 0);
      this.blurBlack = -1.0F;
      this.blurMode = 0;
      this.needsMenuKey = 0;
      this.surfaceInsets = new Rect();
      this.preservePreviousSurfaceInsets = true;
      this.alpha = 1.0F;
      this.dimAmount = 1.0F;
      this.screenBrightness = -1.0F;
      this.buttonBrightness = -1.0F;
      this.rotationAnimation = 0;
      this.token = null;
      this.packageName = null;
      this.screenOrientation = -1;
      this.layoutInDisplayCutoutMode = 0;
      this.userActivityTimeout = -1L;
      this.accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
      this.hideTimeoutMilliseconds = -1L;
      this.mColorMode = 0;
      this.mCompatibilityParamsBackup = null;
      this.mTitle = null;
      this.type = paramInt;
      this.format = -1;
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(-1);
      this.blurAbsoluteCrop = new Rect(0, 0, -1, -1);
      this.blurRelativeCrop = new Rect(0, 0, 0, 0);
      this.blurBlack = -1.0F;
      this.blurMode = 0;
      this.needsMenuKey = 0;
      this.surfaceInsets = new Rect();
      this.preservePreviousSurfaceInsets = true;
      this.alpha = 1.0F;
      this.dimAmount = 1.0F;
      this.screenBrightness = -1.0F;
      this.buttonBrightness = -1.0F;
      this.rotationAnimation = 0;
      this.token = null;
      this.packageName = null;
      this.screenOrientation = -1;
      this.layoutInDisplayCutoutMode = 0;
      this.userActivityTimeout = -1L;
      this.accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
      this.hideTimeoutMilliseconds = -1L;
      this.mColorMode = 0;
      this.mCompatibilityParamsBackup = null;
      this.mTitle = null;
      this.type = paramInt1;
      this.flags = paramInt2;
      this.format = -1;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(-1);
      this.blurAbsoluteCrop = new Rect(0, 0, -1, -1);
      this.blurRelativeCrop = new Rect(0, 0, 0, 0);
      this.blurBlack = -1.0F;
      this.blurMode = 0;
      this.needsMenuKey = 0;
      this.surfaceInsets = new Rect();
      this.preservePreviousSurfaceInsets = true;
      this.alpha = 1.0F;
      this.dimAmount = 1.0F;
      this.screenBrightness = -1.0F;
      this.buttonBrightness = -1.0F;
      this.rotationAnimation = 0;
      this.token = null;
      this.packageName = null;
      this.screenOrientation = -1;
      this.layoutInDisplayCutoutMode = 0;
      this.userActivityTimeout = -1L;
      this.accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
      this.hideTimeoutMilliseconds = -1L;
      this.mColorMode = 0;
      this.mCompatibilityParamsBackup = null;
      this.mTitle = null;
      this.type = paramInt1;
      this.flags = paramInt2;
      this.format = paramInt3;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      super(paramInt2);
      this.blurAbsoluteCrop = new Rect(0, 0, -1, -1);
      this.blurRelativeCrop = new Rect(0, 0, 0, 0);
      this.blurBlack = -1.0F;
      this.blurMode = 0;
      this.needsMenuKey = 0;
      this.surfaceInsets = new Rect();
      this.preservePreviousSurfaceInsets = true;
      this.alpha = 1.0F;
      this.dimAmount = 1.0F;
      this.screenBrightness = -1.0F;
      this.buttonBrightness = -1.0F;
      this.rotationAnimation = 0;
      this.token = null;
      this.packageName = null;
      this.screenOrientation = -1;
      this.layoutInDisplayCutoutMode = 0;
      this.userActivityTimeout = -1L;
      this.accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
      this.hideTimeoutMilliseconds = -1L;
      this.mColorMode = 0;
      this.mCompatibilityParamsBackup = null;
      this.mTitle = null;
      this.type = paramInt3;
      this.flags = paramInt4;
      this.format = paramInt5;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      super(paramInt2);
      this.blurAbsoluteCrop = new Rect(0, 0, -1, -1);
      this.blurRelativeCrop = new Rect(0, 0, 0, 0);
      this.blurBlack = -1.0F;
      this.blurMode = 0;
      this.needsMenuKey = 0;
      this.surfaceInsets = new Rect();
      this.preservePreviousSurfaceInsets = true;
      this.alpha = 1.0F;
      this.dimAmount = 1.0F;
      this.screenBrightness = -1.0F;
      this.buttonBrightness = -1.0F;
      this.rotationAnimation = 0;
      this.token = null;
      this.packageName = null;
      this.screenOrientation = -1;
      this.layoutInDisplayCutoutMode = 0;
      this.userActivityTimeout = -1L;
      this.accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
      this.hideTimeoutMilliseconds = -1L;
      this.mColorMode = 0;
      this.mCompatibilityParamsBackup = null;
      this.mTitle = null;
      this.x = paramInt3;
      this.y = paramInt4;
      this.type = paramInt5;
      this.flags = paramInt6;
      this.format = paramInt7;
    }
    
    public LayoutParams(Parcel paramParcel)
    {
      boolean bool1 = false;
      this.blurAbsoluteCrop = new Rect(0, 0, -1, -1);
      this.blurRelativeCrop = new Rect(0, 0, 0, 0);
      this.blurBlack = -1.0F;
      this.blurMode = 0;
      this.needsMenuKey = 0;
      this.surfaceInsets = new Rect();
      this.preservePreviousSurfaceInsets = true;
      this.alpha = 1.0F;
      this.dimAmount = 1.0F;
      this.screenBrightness = -1.0F;
      this.buttonBrightness = -1.0F;
      this.rotationAnimation = 0;
      this.token = null;
      this.packageName = null;
      this.screenOrientation = -1;
      this.layoutInDisplayCutoutMode = 0;
      this.userActivityTimeout = -1L;
      this.accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
      this.hideTimeoutMilliseconds = -1L;
      this.mColorMode = 0;
      this.mCompatibilityParamsBackup = null;
      this.mTitle = null;
      this.width = paramParcel.readInt();
      this.height = paramParcel.readInt();
      this.x = paramParcel.readInt();
      this.y = paramParcel.readInt();
      this.type = paramParcel.readInt();
      this.flags = paramParcel.readInt();
      this.extraFlags = paramParcel.readInt();
      this.blurRatio = paramParcel.readFloat();
      this.blurMode = paramParcel.readInt();
      this.blurAbsoluteCrop.left = paramParcel.readInt();
      this.blurAbsoluteCrop.top = paramParcel.readInt();
      this.blurAbsoluteCrop.right = paramParcel.readInt();
      this.blurAbsoluteCrop.bottom = paramParcel.readInt();
      this.blurRelativeCrop.left = paramParcel.readInt();
      this.blurRelativeCrop.top = paramParcel.readInt();
      this.blurRelativeCrop.right = paramParcel.readInt();
      this.blurRelativeCrop.bottom = paramParcel.readInt();
      this.privateFlags = paramParcel.readInt();
      this.softInputMode = paramParcel.readInt();
      this.layoutInDisplayCutoutMode = paramParcel.readInt();
      this.gravity = paramParcel.readInt();
      this.horizontalMargin = paramParcel.readFloat();
      this.verticalMargin = paramParcel.readFloat();
      this.format = paramParcel.readInt();
      this.windowAnimations = paramParcel.readInt();
      this.alpha = paramParcel.readFloat();
      this.dimAmount = paramParcel.readFloat();
      this.screenBrightness = paramParcel.readFloat();
      this.buttonBrightness = paramParcel.readFloat();
      this.rotationAnimation = paramParcel.readInt();
      this.token = paramParcel.readStrongBinder();
      this.packageName = paramParcel.readString();
      this.mTitle = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.screenOrientation = paramParcel.readInt();
      this.preferredRefreshRate = paramParcel.readFloat();
      this.preferredDisplayModeId = paramParcel.readInt();
      this.systemUiVisibility = paramParcel.readInt();
      this.subtreeSystemUiVisibility = paramParcel.readInt();
      if (paramParcel.readInt() != 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      this.hasSystemUiListeners = bool2;
      this.inputFeatures = paramParcel.readInt();
      this.userActivityTimeout = paramParcel.readLong();
      this.surfaceInsets.left = paramParcel.readInt();
      this.surfaceInsets.top = paramParcel.readInt();
      this.surfaceInsets.right = paramParcel.readInt();
      this.surfaceInsets.bottom = paramParcel.readInt();
      if (paramParcel.readInt() != 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      this.hasManualSurfaceInsets = bool2;
      boolean bool2 = bool1;
      if (paramParcel.readInt() != 0) {
        bool2 = true;
      }
      this.preservePreviousSurfaceInsets = bool2;
      this.needsMenuKey = paramParcel.readInt();
      this.accessibilityIdOfAnchor = paramParcel.readLong();
      this.accessibilityTitle = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.mColorMode = paramParcel.readInt();
      this.hideTimeoutMilliseconds = paramParcel.readLong();
    }
    
    private static String inputFeatureToString(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 4) {
            return Integer.toString(paramInt);
          }
          return "DISABLE_USER_ACTIVITY";
        }
        return "NO_INPUT_CHANNEL";
      }
      return "DISABLE_POINTER_GESTURES";
    }
    
    public static boolean isSystemAlertWindowType(int paramInt)
    {
      return (paramInt == 2002) || (paramInt == 2003) || (paramInt == 2006) || (paramInt == 2007) || (paramInt == 2010) || (paramInt == 2038);
    }
    
    private static String layoutInDisplayCutoutModeToString(int paramInt)
    {
      if (paramInt != 0)
      {
        if (paramInt != 1)
        {
          if (paramInt != 2)
          {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("unknown(");
            localStringBuilder.append(paramInt);
            localStringBuilder.append(")");
            return localStringBuilder.toString();
          }
          return "never";
        }
        return "always";
      }
      return "default";
    }
    
    public static boolean mayUseInputMethod(int paramInt)
    {
      paramInt &= 0x20008;
      return (paramInt == 0) || (paramInt == 131080);
    }
    
    private static String rotationAnimationToString(int paramInt)
    {
      if (paramInt != -1)
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
              return "SEAMLESS";
            }
            return "JUMPCUT";
          }
          return "CROSSFADE";
        }
        return "ROTATE";
      }
      return "UNSPECIFIED";
    }
    
    private static String softInputModeToString(int paramInt)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = paramInt & 0xF;
      if (i != 0)
      {
        localStringBuilder.append("state=");
        if (i != 1)
        {
          if (i != 2)
          {
            if (i != 3)
            {
              if (i != 4)
              {
                if (i != 5) {
                  localStringBuilder.append(i);
                } else {
                  localStringBuilder.append("always_visible");
                }
              }
              else {
                localStringBuilder.append("visible");
              }
            }
            else {
              localStringBuilder.append("always_hidden");
            }
          }
          else {
            localStringBuilder.append("hidden");
          }
        }
        else {
          localStringBuilder.append("unchanged");
        }
        localStringBuilder.append(' ');
      }
      i = paramInt & 0xF0;
      if (i != 0)
      {
        localStringBuilder.append("adjust=");
        if (i != 16)
        {
          if (i != 32)
          {
            if (i != 48) {
              localStringBuilder.append(i);
            } else {
              localStringBuilder.append("nothing");
            }
          }
          else {
            localStringBuilder.append("pan");
          }
        }
        else {
          localStringBuilder.append("resize");
        }
        localStringBuilder.append(' ');
      }
      if ((paramInt & 0x100) != 0)
      {
        localStringBuilder.append("forwardNavigation");
        localStringBuilder.append(' ');
      }
      localStringBuilder.deleteCharAt(localStringBuilder.length() - 1);
      return localStringBuilder.toString();
    }
    
    @UnsupportedAppUsage
    void backup()
    {
      int[] arrayOfInt1 = this.mCompatibilityParamsBackup;
      int[] arrayOfInt2 = arrayOfInt1;
      if (arrayOfInt1 == null)
      {
        arrayOfInt2 = new int[4];
        this.mCompatibilityParamsBackup = arrayOfInt2;
      }
      arrayOfInt2[0] = this.x;
      arrayOfInt2[1] = this.y;
      arrayOfInt2[2] = this.width;
      arrayOfInt2[3] = this.height;
    }
    
    public final int copyFrom(LayoutParams paramLayoutParams)
    {
      int i = 0;
      if (this.width != paramLayoutParams.width)
      {
        this.width = paramLayoutParams.width;
        i = 0x0 | 0x1;
      }
      int j = i;
      if (this.height != paramLayoutParams.height)
      {
        this.height = paramLayoutParams.height;
        j = i | 0x1;
      }
      int k = this.x;
      int m = paramLayoutParams.x;
      i = j;
      if (k != m)
      {
        this.x = m;
        i = j | 0x1;
      }
      m = this.y;
      k = paramLayoutParams.y;
      j = i;
      if (m != k)
      {
        this.y = k;
        j = i | 0x1;
      }
      float f1 = this.horizontalWeight;
      float f2 = paramLayoutParams.horizontalWeight;
      i = j;
      if (f1 != f2)
      {
        this.horizontalWeight = f2;
        i = j | 0x1;
      }
      f1 = this.verticalWeight;
      f2 = paramLayoutParams.verticalWeight;
      j = i;
      if (f1 != f2)
      {
        this.verticalWeight = f2;
        j = i | 0x1;
      }
      f2 = this.horizontalMargin;
      f1 = paramLayoutParams.horizontalMargin;
      i = j;
      if (f2 != f1)
      {
        this.horizontalMargin = f1;
        i = j | 0x1;
      }
      f2 = this.verticalMargin;
      f1 = paramLayoutParams.verticalMargin;
      j = i;
      if (f2 != f1)
      {
        this.verticalMargin = f1;
        j = i | 0x1;
      }
      k = this.type;
      m = paramLayoutParams.type;
      i = j;
      if (k != m)
      {
        this.type = m;
        i = j | 0x2;
      }
      k = this.flags;
      m = paramLayoutParams.flags;
      j = i;
      if (k != m)
      {
        j = i;
        if ((0xC000000 & (k ^ m)) != 0) {
          j = i | 0x80000;
        }
        this.flags = paramLayoutParams.flags;
        j |= 0x4;
      }
      k = this.extraFlags;
      m = paramLayoutParams.extraFlags;
      i = j;
      if (k != m)
      {
        this.extraFlags = m;
        i = j | 0x4;
      }
      f1 = this.blurRatio;
      f2 = paramLayoutParams.blurRatio;
      j = i;
      if (f1 != f2)
      {
        this.blurRatio = f2;
        j = i | 0x40000000;
      }
      m = this.blurMode;
      k = paramLayoutParams.blurMode;
      i = j;
      if (m != k)
      {
        this.blurMode = k;
        i = j | 0x1;
      }
      m = i;
      if (!this.blurAbsoluteCrop.equals(paramLayoutParams.blurAbsoluteCrop))
      {
        this.surfaceInsets.set(paramLayoutParams.surfaceInsets);
        m = i | 0x20000000;
      }
      j = m;
      if (!this.blurRelativeCrop.equals(paramLayoutParams.blurRelativeCrop))
      {
        this.surfaceInsets.set(paramLayoutParams.surfaceInsets);
        j = m | 0x20000000;
      }
      m = this.privateFlags;
      k = paramLayoutParams.privateFlags;
      i = j;
      if (m != k)
      {
        this.privateFlags = k;
        i = j | 0x20000;
      }
      k = this.softInputMode;
      m = paramLayoutParams.softInputMode;
      j = i;
      if (k != m)
      {
        this.softInputMode = m;
        j = i | 0x200;
      }
      m = this.layoutInDisplayCutoutMode;
      k = paramLayoutParams.layoutInDisplayCutoutMode;
      i = j;
      if (m != k)
      {
        this.layoutInDisplayCutoutMode = k;
        i = j | 0x1;
      }
      m = this.gravity;
      k = paramLayoutParams.gravity;
      j = i;
      if (m != k)
      {
        this.gravity = k;
        j = i | 0x1;
      }
      k = this.format;
      m = paramLayoutParams.format;
      i = j;
      if (k != m)
      {
        this.format = m;
        i = j | 0x8;
      }
      k = this.windowAnimations;
      m = paramLayoutParams.windowAnimations;
      j = i;
      if (k != m)
      {
        this.windowAnimations = m;
        j = i | 0x10;
      }
      if (this.token == null) {
        this.token = paramLayoutParams.token;
      }
      if (this.packageName == null) {
        this.packageName = paramLayoutParams.packageName;
      }
      i = j;
      CharSequence localCharSequence;
      if (!Objects.equals(this.mTitle, paramLayoutParams.mTitle))
      {
        localCharSequence = paramLayoutParams.mTitle;
        i = j;
        if (localCharSequence != null)
        {
          this.mTitle = localCharSequence;
          i = j | 0x40;
        }
      }
      f1 = this.alpha;
      f2 = paramLayoutParams.alpha;
      j = i;
      if (f1 != f2)
      {
        this.alpha = f2;
        j = i | 0x80;
      }
      f2 = this.dimAmount;
      f1 = paramLayoutParams.dimAmount;
      i = j;
      if (f2 != f1)
      {
        this.dimAmount = f1;
        i = j | 0x20;
      }
      f1 = this.screenBrightness;
      f2 = paramLayoutParams.screenBrightness;
      m = i;
      if (f1 != f2)
      {
        this.screenBrightness = f2;
        m = i | 0x800;
      }
      f1 = this.buttonBrightness;
      f2 = paramLayoutParams.buttonBrightness;
      j = m;
      if (f1 != f2)
      {
        this.buttonBrightness = f2;
        j = m | 0x2000;
      }
      m = this.rotationAnimation;
      k = paramLayoutParams.rotationAnimation;
      i = j;
      if (m != k)
      {
        this.rotationAnimation = k;
        i = j | 0x1000;
      }
      k = this.screenOrientation;
      m = paramLayoutParams.screenOrientation;
      j = i;
      if (k != m)
      {
        this.screenOrientation = m;
        j = i | 0x400;
      }
      f1 = this.preferredRefreshRate;
      f2 = paramLayoutParams.preferredRefreshRate;
      i = j;
      if (f1 != f2)
      {
        this.preferredRefreshRate = f2;
        i = j | 0x200000;
      }
      k = this.preferredDisplayModeId;
      m = paramLayoutParams.preferredDisplayModeId;
      j = i;
      if (k != m)
      {
        this.preferredDisplayModeId = m;
        j = i | 0x800000;
      }
      if (this.systemUiVisibility == paramLayoutParams.systemUiVisibility)
      {
        m = j;
        if (this.subtreeSystemUiVisibility == paramLayoutParams.subtreeSystemUiVisibility) {}
      }
      else
      {
        this.systemUiVisibility = paramLayoutParams.systemUiVisibility;
        this.subtreeSystemUiVisibility = paramLayoutParams.subtreeSystemUiVisibility;
        m = j | 0x4000;
      }
      boolean bool1 = this.hasSystemUiListeners;
      boolean bool2 = paramLayoutParams.hasSystemUiListeners;
      i = m;
      if (bool1 != bool2)
      {
        this.hasSystemUiListeners = bool2;
        i = m | 0x8000;
      }
      k = this.inputFeatures;
      m = paramLayoutParams.inputFeatures;
      j = i;
      if (k != m)
      {
        this.inputFeatures = m;
        j = i | 0x10000;
      }
      long l1 = this.userActivityTimeout;
      long l2 = paramLayoutParams.userActivityTimeout;
      i = j;
      if (l1 != l2)
      {
        this.userActivityTimeout = l2;
        i = j | 0x40000;
      }
      m = i;
      if (!this.surfaceInsets.equals(paramLayoutParams.surfaceInsets))
      {
        this.surfaceInsets.set(paramLayoutParams.surfaceInsets);
        m = i | 0x100000;
      }
      bool2 = this.hasManualSurfaceInsets;
      bool1 = paramLayoutParams.hasManualSurfaceInsets;
      j = m;
      if (bool2 != bool1)
      {
        this.hasManualSurfaceInsets = bool1;
        j = m | 0x100000;
      }
      bool1 = this.preservePreviousSurfaceInsets;
      bool2 = paramLayoutParams.preservePreviousSurfaceInsets;
      i = j;
      if (bool1 != bool2)
      {
        this.preservePreviousSurfaceInsets = bool2;
        i = j | 0x100000;
      }
      m = this.needsMenuKey;
      k = paramLayoutParams.needsMenuKey;
      j = i;
      if (m != k)
      {
        this.needsMenuKey = k;
        j = i | 0x400000;
      }
      l1 = this.accessibilityIdOfAnchor;
      l2 = paramLayoutParams.accessibilityIdOfAnchor;
      m = j;
      if (l1 != l2)
      {
        this.accessibilityIdOfAnchor = l2;
        m = j | 0x1000000;
      }
      i = m;
      if (!Objects.equals(this.accessibilityTitle, paramLayoutParams.accessibilityTitle))
      {
        localCharSequence = paramLayoutParams.accessibilityTitle;
        i = m;
        if (localCharSequence != null)
        {
          this.accessibilityTitle = localCharSequence;
          i = m | 0x2000000;
        }
      }
      m = this.mColorMode;
      k = paramLayoutParams.mColorMode;
      j = i;
      if (m != k)
      {
        this.mColorMode = k;
        j = i | 0x4000000;
      }
      this.hideTimeoutMilliseconds = paramLayoutParams.hideTimeoutMilliseconds;
      return j;
    }
    
    public String debug(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("Contents of ");
      localStringBuilder.append(this);
      localStringBuilder.append(":");
      Log.d("Debug", localStringBuilder.toString());
      Log.d("Debug", super.debug(""));
      Log.d("Debug", "");
      paramString = new StringBuilder();
      paramString.append("WindowManager.LayoutParams={title=");
      paramString.append(this.mTitle);
      paramString.append("}");
      Log.d("Debug", paramString.toString());
      return "";
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void dumpDimensions(StringBuilder paramStringBuilder)
    {
      paramStringBuilder.append('(');
      paramStringBuilder.append(this.x);
      paramStringBuilder.append(',');
      paramStringBuilder.append(this.y);
      paramStringBuilder.append(")(");
      int i = this.width;
      String str1 = "wrap";
      String str2;
      if (i == -1) {
        str2 = "fill";
      } else if (this.width == -2) {
        str2 = "wrap";
      } else {
        str2 = String.valueOf(this.width);
      }
      paramStringBuilder.append(str2);
      paramStringBuilder.append('x');
      if (this.height == -1) {
        str2 = "fill";
      } else if (this.height == -2) {
        str2 = str1;
      } else {
        str2 = String.valueOf(this.height);
      }
      paramStringBuilder.append(str2);
      paramStringBuilder.append(")");
    }
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("x", this.x);
      paramViewHierarchyEncoder.addProperty("y", this.y);
      paramViewHierarchyEncoder.addProperty("horizontalWeight", this.horizontalWeight);
      paramViewHierarchyEncoder.addProperty("verticalWeight", this.verticalWeight);
      paramViewHierarchyEncoder.addProperty("type", this.type);
      paramViewHierarchyEncoder.addProperty("flags", this.flags);
    }
    
    public int getColorMode()
    {
      return this.mColorMode;
    }
    
    public final CharSequence getTitle()
    {
      Object localObject = this.mTitle;
      if (localObject == null) {
        localObject = "";
      }
      return (CharSequence)localObject;
    }
    
    @SystemApi
    public final long getUserActivityTimeout()
    {
      return this.userActivityTimeout;
    }
    
    public boolean isFullscreen()
    {
      boolean bool;
      if ((this.x == 0) && (this.y == 0) && (this.width == -1) && (this.height == -1)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    @UnsupportedAppUsage
    void restore()
    {
      int[] arrayOfInt = this.mCompatibilityParamsBackup;
      if (arrayOfInt != null)
      {
        this.x = arrayOfInt[0];
        this.y = arrayOfInt[1];
        this.width = arrayOfInt[2];
        this.height = arrayOfInt[3];
      }
    }
    
    public void scale(float paramFloat)
    {
      this.x = ((int)(this.x * paramFloat + 0.5F));
      this.y = ((int)(this.y * paramFloat + 0.5F));
      if (this.width > 0) {
        this.width = ((int)(this.width * paramFloat + 0.5F));
      }
      if (this.height > 0) {
        this.height = ((int)(this.height * paramFloat + 0.5F));
      }
    }
    
    public void setColorMode(int paramInt)
    {
      this.mColorMode = paramInt;
    }
    
    public final void setSurfaceInsets(View paramView, boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = (int)Math.ceil(paramView.getZ() * 2.0F);
      int j = 0;
      try
      {
        int k = paramView.getResources().getConfiguration().windowConfiguration.getWindowingMode();
        j = k;
      }
      catch (Exception paramView)
      {
        paramView.printStackTrace();
      }
      if ((i != 0) && (j != 5))
      {
        paramView = this.surfaceInsets;
        paramView.set(Math.max(i, paramView.left), Math.max(i, this.surfaceInsets.top), Math.max(i, this.surfaceInsets.right), Math.max(i, this.surfaceInsets.bottom));
      }
      else
      {
        this.surfaceInsets.set(0, 0, 0, 0);
      }
      this.hasManualSurfaceInsets = paramBoolean1;
      this.preservePreviousSurfaceInsets = paramBoolean2;
    }
    
    public final void setTitle(CharSequence paramCharSequence)
    {
      Object localObject = paramCharSequence;
      if (paramCharSequence == null) {
        localObject = "";
      }
      this.mTitle = TextUtils.stringOrSpannedString((CharSequence)localObject);
    }
    
    @SystemApi
    public final void setUserActivityTimeout(long paramLong)
    {
      this.userActivityTimeout = paramLong;
    }
    
    public String toString()
    {
      return toString("");
    }
    
    public String toString(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder(256);
      localStringBuilder.append('{');
      dumpDimensions(localStringBuilder);
      if (this.horizontalMargin != 0.0F)
      {
        localStringBuilder.append(" hm=");
        localStringBuilder.append(this.horizontalMargin);
      }
      if (this.verticalMargin != 0.0F)
      {
        localStringBuilder.append(" vm=");
        localStringBuilder.append(this.verticalMargin);
      }
      if (this.gravity != 0)
      {
        localStringBuilder.append(" gr=");
        localStringBuilder.append(Gravity.toString(this.gravity));
      }
      if (this.softInputMode != 0)
      {
        localStringBuilder.append(" sim={");
        localStringBuilder.append(softInputModeToString(this.softInputMode));
        localStringBuilder.append('}');
      }
      if (this.layoutInDisplayCutoutMode != 0)
      {
        localStringBuilder.append(" layoutInDisplayCutoutMode=");
        localStringBuilder.append(layoutInDisplayCutoutModeToString(this.layoutInDisplayCutoutMode));
      }
      localStringBuilder.append(" blurRatio=");
      localStringBuilder.append(this.blurRatio);
      localStringBuilder.append(" blurMode=");
      localStringBuilder.append(this.blurMode);
      localStringBuilder.append(" ty=");
      localStringBuilder.append(ViewDebug.intToString(LayoutParams.class, "type", this.type));
      if (this.format != -1)
      {
        localStringBuilder.append(" fmt=");
        localStringBuilder.append(PixelFormat.formatToString(this.format));
      }
      if (this.windowAnimations != 0)
      {
        localStringBuilder.append(" wanim=0x");
        localStringBuilder.append(Integer.toHexString(this.windowAnimations));
      }
      if (this.screenOrientation != -1)
      {
        localStringBuilder.append(" or=");
        localStringBuilder.append(ActivityInfo.screenOrientationToString(this.screenOrientation));
      }
      if (this.alpha != 1.0F)
      {
        localStringBuilder.append(" alpha=");
        localStringBuilder.append(this.alpha);
      }
      if (this.screenBrightness != -1.0F)
      {
        localStringBuilder.append(" sbrt=");
        localStringBuilder.append(this.screenBrightness);
      }
      if (this.buttonBrightness != -1.0F)
      {
        localStringBuilder.append(" bbrt=");
        localStringBuilder.append(this.buttonBrightness);
      }
      if (this.rotationAnimation != 0)
      {
        localStringBuilder.append(" rotAnim=");
        localStringBuilder.append(rotationAnimationToString(this.rotationAnimation));
      }
      if (this.preferredRefreshRate != 0.0F)
      {
        localStringBuilder.append(" preferredRefreshRate=");
        localStringBuilder.append(this.preferredRefreshRate);
      }
      if (this.preferredDisplayModeId != 0)
      {
        localStringBuilder.append(" preferredDisplayMode=");
        localStringBuilder.append(this.preferredDisplayModeId);
      }
      if (this.hasSystemUiListeners)
      {
        localStringBuilder.append(" sysuil=");
        localStringBuilder.append(this.hasSystemUiListeners);
      }
      if (this.inputFeatures != 0)
      {
        localStringBuilder.append(" if=");
        localStringBuilder.append(inputFeatureToString(this.inputFeatures));
      }
      if (this.userActivityTimeout >= 0L)
      {
        localStringBuilder.append(" userActivityTimeout=");
        localStringBuilder.append(this.userActivityTimeout);
      }
      if ((this.surfaceInsets.left != 0) || (this.surfaceInsets.top != 0) || (this.surfaceInsets.right != 0) || (this.surfaceInsets.bottom != 0) || (this.hasManualSurfaceInsets) || (!this.preservePreviousSurfaceInsets))
      {
        localStringBuilder.append(" surfaceInsets=");
        localStringBuilder.append(this.surfaceInsets);
        if (this.hasManualSurfaceInsets) {
          localStringBuilder.append(" (manual)");
        }
        if (!this.preservePreviousSurfaceInsets) {
          localStringBuilder.append(" (!preservePreviousSurfaceInsets)");
        }
      }
      if (this.needsMenuKey == 1) {
        localStringBuilder.append(" needsMenuKey");
      }
      if (this.mColorMode != 0)
      {
        localStringBuilder.append(" colorMode=");
        localStringBuilder.append(ActivityInfo.colorModeToString(this.mColorMode));
      }
      localStringBuilder.append(System.lineSeparator());
      localStringBuilder.append(paramString);
      localStringBuilder.append("  fl=");
      localStringBuilder.append(ViewDebug.flagsToString(LayoutParams.class, "flags", this.flags));
      if (this.privateFlags != 0)
      {
        localStringBuilder.append(System.lineSeparator());
        localStringBuilder.append(paramString);
        localStringBuilder.append("  pfl=");
        localStringBuilder.append(ViewDebug.flagsToString(LayoutParams.class, "privateFlags", this.privateFlags));
      }
      if (this.systemUiVisibility != 0)
      {
        localStringBuilder.append(System.lineSeparator());
        localStringBuilder.append(paramString);
        localStringBuilder.append("  sysui=");
        localStringBuilder.append(ViewDebug.flagsToString(View.class, "mSystemUiVisibility", this.systemUiVisibility));
      }
      if (this.subtreeSystemUiVisibility != 0)
      {
        localStringBuilder.append(System.lineSeparator());
        localStringBuilder.append(paramString);
        localStringBuilder.append("  vsysui=");
        localStringBuilder.append(ViewDebug.flagsToString(View.class, "mSystemUiVisibility", this.subtreeSystemUiVisibility));
      }
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.width);
      paramParcel.writeInt(this.height);
      paramParcel.writeInt(this.x);
      paramParcel.writeInt(this.y);
      paramParcel.writeInt(this.type);
      paramParcel.writeInt(this.flags);
      paramParcel.writeInt(this.extraFlags);
      paramParcel.writeFloat(this.blurRatio);
      paramParcel.writeInt(this.blurMode);
      paramParcel.writeInt(this.blurAbsoluteCrop.left);
      paramParcel.writeInt(this.blurAbsoluteCrop.top);
      paramParcel.writeInt(this.blurAbsoluteCrop.right);
      paramParcel.writeInt(this.blurAbsoluteCrop.bottom);
      paramParcel.writeInt(this.blurRelativeCrop.left);
      paramParcel.writeInt(this.blurRelativeCrop.top);
      paramParcel.writeInt(this.blurRelativeCrop.right);
      paramParcel.writeInt(this.blurRelativeCrop.bottom);
      paramParcel.writeInt(this.privateFlags);
      paramParcel.writeInt(this.softInputMode);
      paramParcel.writeInt(this.layoutInDisplayCutoutMode);
      paramParcel.writeInt(this.gravity);
      paramParcel.writeFloat(this.horizontalMargin);
      paramParcel.writeFloat(this.verticalMargin);
      paramParcel.writeInt(this.format);
      paramParcel.writeInt(this.windowAnimations);
      paramParcel.writeFloat(this.alpha);
      paramParcel.writeFloat(this.dimAmount);
      paramParcel.writeFloat(this.screenBrightness);
      paramParcel.writeFloat(this.buttonBrightness);
      paramParcel.writeInt(this.rotationAnimation);
      paramParcel.writeStrongBinder(this.token);
      paramParcel.writeString(this.packageName);
      TextUtils.writeToParcel(this.mTitle, paramParcel, paramInt);
      paramParcel.writeInt(this.screenOrientation);
      paramParcel.writeFloat(this.preferredRefreshRate);
      paramParcel.writeInt(this.preferredDisplayModeId);
      paramParcel.writeInt(this.systemUiVisibility);
      paramParcel.writeInt(this.subtreeSystemUiVisibility);
      paramParcel.writeInt(this.hasSystemUiListeners);
      paramParcel.writeInt(this.inputFeatures);
      paramParcel.writeLong(this.userActivityTimeout);
      paramParcel.writeInt(this.surfaceInsets.left);
      paramParcel.writeInt(this.surfaceInsets.top);
      paramParcel.writeInt(this.surfaceInsets.right);
      paramParcel.writeInt(this.surfaceInsets.bottom);
      paramParcel.writeInt(this.hasManualSurfaceInsets);
      paramParcel.writeInt(this.preservePreviousSurfaceInsets);
      paramParcel.writeInt(this.needsMenuKey);
      paramParcel.writeLong(this.accessibilityIdOfAnchor);
      TextUtils.writeToParcel(this.accessibilityTitle, paramParcel, paramInt);
      paramParcel.writeInt(this.mColorMode);
      paramParcel.writeLong(this.hideTimeoutMilliseconds);
    }
    
    public void writeToProto(ProtoOutputStream paramProtoOutputStream, long paramLong)
    {
      paramLong = paramProtoOutputStream.start(paramLong);
      paramProtoOutputStream.write(1120986464257L, this.type);
      paramProtoOutputStream.write(1120986464258L, this.x);
      paramProtoOutputStream.write(1120986464259L, this.y);
      paramProtoOutputStream.write(1120986464260L, this.width);
      paramProtoOutputStream.write(1120986464261L, this.height);
      paramProtoOutputStream.write(1108101562374L, this.horizontalMargin);
      paramProtoOutputStream.write(1108101562375L, this.verticalMargin);
      paramProtoOutputStream.write(1120986464264L, this.gravity);
      paramProtoOutputStream.write(1120986464265L, this.softInputMode);
      paramProtoOutputStream.write(1159641169930L, this.format);
      paramProtoOutputStream.write(1120986464267L, this.windowAnimations);
      paramProtoOutputStream.write(1108101562380L, this.alpha);
      paramProtoOutputStream.write(1108101562381L, this.screenBrightness);
      paramProtoOutputStream.write(1108101562382L, this.buttonBrightness);
      paramProtoOutputStream.write(1159641169935L, this.rotationAnimation);
      paramProtoOutputStream.write(1108101562384L, this.preferredRefreshRate);
      paramProtoOutputStream.write(1120986464273L, this.preferredDisplayModeId);
      paramProtoOutputStream.write(1133871366162L, this.hasSystemUiListeners);
      paramProtoOutputStream.write(1155346202643L, this.inputFeatures);
      paramProtoOutputStream.write(1112396529684L, this.userActivityTimeout);
      paramProtoOutputStream.write(1159641169942L, this.needsMenuKey);
      paramProtoOutputStream.write(1159641169943L, this.mColorMode);
      paramProtoOutputStream.write(1155346202648L, this.flags);
      paramProtoOutputStream.write(1155346202650L, this.privateFlags);
      paramProtoOutputStream.write(1155346202651L, this.systemUiVisibility);
      paramProtoOutputStream.write(1155346202652L, this.subtreeSystemUiVisibility);
      paramProtoOutputStream.end(paramLong);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    static @interface LayoutInDisplayCutoutMode {}
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface SoftInputModeFlags {}
    
    @SystemApi
    @Retention(RetentionPolicy.SOURCE)
    public static @interface SystemFlags {}
  }
  
  public static @interface RemoveContentMode {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface TransitionFlags {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface TransitionType {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */