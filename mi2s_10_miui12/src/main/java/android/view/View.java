package android.view;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.UnsupportedAppUsage;
import android.app.Activity;
import android.app.WindowConfiguration;
import android.content.AutofillOptions;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Interpolator;
import android.graphics.Interpolator.Result;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.RenderNode;
import android.graphics.RenderNode.PositionUpdateListener;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.sysprop.DisplayProperties;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatProperty;
import android.util.Log;
import android.util.LongArray;
import android.util.LongSparseLongArray;
import android.util.MiuiMultiWindowAdapter;
import android.util.Pools.SynchronizedPool;
import android.util.Property;
import android.util.SeempLog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.util.StatsLog;
import android.util.SuperNotCalledException;
import android.util.TypedValue;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityEventSource;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeIdManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo.TouchDelegateInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.contentcapture.ContentCaptureManager;
import android.view.contentcapture.ContentCaptureSession;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ScrollBarDrawable;
import com.android.internal.R.styleable;
import com.android.internal.view.TooltipPopup;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.widget.ScrollBarUtils;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import com.miui.internal.contentcatcher.IInterceptor;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_View_View.Extension;
import com.miui.internal.variable.api.v29.Android_View_View.Interface;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import miui.contentcatcher.InterceptorProxy;

public class View
  implements Drawable.Callback, KeyEvent.Callback, AccessibilityEventSource
{
  public static final int ACCESSIBILITY_CURSOR_POSITION_UNDEFINED = -1;
  public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
  static final int ACCESSIBILITY_LIVE_REGION_DEFAULT = 0;
  public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
  public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
  static final int ALL_RTL_PROPERTIES_RESOLVED = 1610678816;
  public static final Property<View, Float> ALPHA;
  public static final int AUTOFILL_FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 1;
  private static final int[] AUTOFILL_HIGHLIGHT_ATTR;
  public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE = "creditCardExpirationDate";
  public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY = "creditCardExpirationDay";
  public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH = "creditCardExpirationMonth";
  public static final String AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR = "creditCardExpirationYear";
  public static final String AUTOFILL_HINT_CREDIT_CARD_NUMBER = "creditCardNumber";
  public static final String AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE = "creditCardSecurityCode";
  public static final String AUTOFILL_HINT_EMAIL_ADDRESS = "emailAddress";
  public static final String AUTOFILL_HINT_NAME = "name";
  public static final String AUTOFILL_HINT_PASSWORD = "password";
  public static final String AUTOFILL_HINT_PHONE = "phone";
  public static final String AUTOFILL_HINT_POSTAL_ADDRESS = "postalAddress";
  public static final String AUTOFILL_HINT_POSTAL_CODE = "postalCode";
  public static final String AUTOFILL_HINT_USERNAME = "username";
  private static final String AUTOFILL_LOG_TAG = "View.Autofill";
  public static final int AUTOFILL_TYPE_DATE = 4;
  public static final int AUTOFILL_TYPE_LIST = 3;
  public static final int AUTOFILL_TYPE_NONE = 0;
  public static final int AUTOFILL_TYPE_TEXT = 1;
  public static final int AUTOFILL_TYPE_TOGGLE = 2;
  static final int CLICKABLE = 16384;
  private static final String CONTENT_CAPTURE_LOG_TAG = "View.ContentCapture";
  static final int CONTEXT_CLICKABLE = 8388608;
  @UnsupportedAppUsage
  private static final boolean DBG = false;
  private static final boolean DEBUG_CONTENT_CAPTURE = false;
  static final int DEBUG_CORNERS_COLOR;
  static final int DEBUG_CORNERS_SIZE_DIP = 8;
  public static boolean DEBUG_DRAW = false;
  static final int DISABLED = 32;
  public static final int DRAG_FLAG_GLOBAL = 256;
  public static final int DRAG_FLAG_GLOBAL_PERSISTABLE_URI_PERMISSION = 64;
  public static final int DRAG_FLAG_GLOBAL_PREFIX_URI_PERMISSION = 128;
  public static final int DRAG_FLAG_GLOBAL_URI_READ = 1;
  public static final int DRAG_FLAG_GLOBAL_URI_WRITE = 2;
  public static final int DRAG_FLAG_OPAQUE = 512;
  static final int DRAG_MASK = 3;
  static final int DRAWING_CACHE_ENABLED = 32768;
  @Deprecated
  public static final int DRAWING_CACHE_QUALITY_AUTO = 0;
  private static final int[] DRAWING_CACHE_QUALITY_FLAGS;
  @Deprecated
  public static final int DRAWING_CACHE_QUALITY_HIGH = 1048576;
  @Deprecated
  public static final int DRAWING_CACHE_QUALITY_LOW = 524288;
  static final int DRAWING_CACHE_QUALITY_MASK = 1572864;
  static final int DRAW_MASK = 128;
  static final int DUPLICATE_PARENT_STATE = 4194304;
  protected static final int[] EMPTY_STATE_SET;
  static final int ENABLED = 0;
  protected static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET;
  protected static final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] ENABLED_FOCUSED_STATE_SET;
  protected static final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
  static final int ENABLED_MASK = 32;
  protected static final int[] ENABLED_SELECTED_STATE_SET;
  protected static final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] ENABLED_STATE_SET;
  protected static final int[] ENABLED_WINDOW_FOCUSED_STATE_SET;
  static final int FADING_EDGE_HORIZONTAL = 4096;
  static final int FADING_EDGE_MASK = 12288;
  static final int FADING_EDGE_NONE = 0;
  static final int FADING_EDGE_VERTICAL = 8192;
  static final int FILTER_TOUCHES_WHEN_OBSCURED = 1024;
  public static final int FIND_VIEWS_WITH_ACCESSIBILITY_NODE_PROVIDERS = 4;
  public static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 2;
  public static final int FIND_VIEWS_WITH_TEXT = 1;
  private static final int FITS_SYSTEM_WINDOWS = 2;
  public static final int FOCUSABLE = 1;
  public static final int FOCUSABLES_ALL = 0;
  public static final int FOCUSABLES_TOUCH_MODE = 1;
  public static final int FOCUSABLE_AUTO = 16;
  static final int FOCUSABLE_IN_TOUCH_MODE = 262144;
  private static final int FOCUSABLE_MASK = 17;
  protected static final int[] FOCUSED_SELECTED_STATE_SET;
  protected static final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] FOCUSED_STATE_SET;
  protected static final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET;
  public static final int FOCUS_BACKWARD = 1;
  public static final int FOCUS_DOWN = 130;
  public static final int FOCUS_FORWARD = 2;
  public static final int FOCUS_LEFT = 17;
  public static final int FOCUS_RIGHT = 66;
  public static final int FOCUS_UP = 33;
  public static final HashMap<String, String> FORCE_DARK_WHITE_LIST;
  public static final int GONE = 8;
  public static final int HAPTIC_FEEDBACK_ENABLED = 268435456;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
  static final int IMPORTANT_FOR_ACCESSIBILITY_DEFAULT = 0;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
  public static final int IMPORTANT_FOR_AUTOFILL_AUTO = 0;
  public static final int IMPORTANT_FOR_AUTOFILL_NO = 2;
  public static final int IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS = 8;
  public static final int IMPORTANT_FOR_AUTOFILL_YES = 1;
  public static final int IMPORTANT_FOR_AUTOFILL_YES_EXCLUDE_DESCENDANTS = 4;
  public static final int INVISIBLE = 4;
  public static final int KEEP_SCREEN_ON = 67108864;
  public static final int LAST_APP_AUTOFILL_ID = 1073741823;
  public static final int LAYER_TYPE_HARDWARE = 2;
  public static final int LAYER_TYPE_NONE = 0;
  public static final int LAYER_TYPE_SOFTWARE = 1;
  private static final int LAYOUT_DIRECTION_DEFAULT = 2;
  private static final int[] LAYOUT_DIRECTION_FLAGS;
  public static final int LAYOUT_DIRECTION_INHERIT = 2;
  public static final int LAYOUT_DIRECTION_LOCALE = 3;
  public static final int LAYOUT_DIRECTION_LTR = 0;
  static final int LAYOUT_DIRECTION_RESOLVED_DEFAULT = 0;
  public static final int LAYOUT_DIRECTION_RTL = 1;
  public static final int LAYOUT_DIRECTION_UNDEFINED = -1;
  static final int LONG_CLICKABLE = 2097152;
  public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
  public static final int MEASURED_SIZE_MASK = 16777215;
  public static final int MEASURED_STATE_MASK = -16777216;
  public static final int MEASURED_STATE_TOO_SMALL = 16777216;
  @UnsupportedAppUsage
  public static final int NAVIGATION_BAR_TRANSIENT = 134217728;
  public static final int NAVIGATION_BAR_TRANSLUCENT = Integer.MIN_VALUE;
  public static final int NAVIGATION_BAR_TRANSPARENT = 32768;
  public static final int NAVIGATION_BAR_UNHIDE = 536870912;
  public static final int NOT_FOCUSABLE = 0;
  public static final int NO_ID = -1;
  static final int OPTIONAL_FITS_SYSTEM_WINDOWS = 2048;
  public static final int OVER_SCROLL_ALWAYS = 0;
  public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
  public static final int OVER_SCROLL_NEVER = 2;
  static final int PARENT_SAVE_DISABLED = 536870912;
  static final int PARENT_SAVE_DISABLED_MASK = 536870912;
  static final int PFLAG2_ACCESSIBILITY_FOCUSED = 67108864;
  static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_MASK = 25165824;
  static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_SHIFT = 23;
  static final int PFLAG2_DRAG_CAN_ACCEPT = 1;
  static final int PFLAG2_DRAG_HOVERED = 2;
  static final int PFLAG2_DRAWABLE_RESOLVED = 1073741824;
  static final int PFLAG2_HAS_TRANSIENT_STATE = Integer.MIN_VALUE;
  static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK = 7340032;
  static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT = 20;
  static final int PFLAG2_LAYOUT_DIRECTION_MASK = 12;
  static final int PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT = 2;
  static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED = 32;
  static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_MASK = 48;
  static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_RTL = 16;
  static final int PFLAG2_PADDING_RESOLVED = 536870912;
  static final int PFLAG2_SUBTREE_ACCESSIBILITY_STATE_CHANGED = 134217728;
  private static final int[] PFLAG2_TEXT_ALIGNMENT_FLAGS;
  static final int PFLAG2_TEXT_ALIGNMENT_MASK = 57344;
  static final int PFLAG2_TEXT_ALIGNMENT_MASK_SHIFT = 13;
  static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED = 65536;
  private static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_DEFAULT = 131072;
  static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK = 917504;
  static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT = 17;
  private static final int[] PFLAG2_TEXT_DIRECTION_FLAGS;
  static final int PFLAG2_TEXT_DIRECTION_MASK = 448;
  static final int PFLAG2_TEXT_DIRECTION_MASK_SHIFT = 6;
  static final int PFLAG2_TEXT_DIRECTION_RESOLVED = 512;
  static final int PFLAG2_TEXT_DIRECTION_RESOLVED_DEFAULT = 1024;
  static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK = 7168;
  static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT = 10;
  static final int PFLAG2_VIEW_QUICK_REJECTED = 268435456;
  private static final int PFLAG3_ACCESSIBILITY_HEADING = Integer.MIN_VALUE;
  private static final int PFLAG3_AGGREGATED_VISIBLE = 536870912;
  static final int PFLAG3_APPLYING_INSETS = 32;
  static final int PFLAG3_ASSIST_BLOCKED = 16384;
  private static final int PFLAG3_AUTOFILLID_EXPLICITLY_SET = 1073741824;
  static final int PFLAG3_CALLED_SUPER = 16;
  private static final int PFLAG3_CLUSTER = 32768;
  private static final int PFLAG3_FINGER_DOWN = 131072;
  static final int PFLAG3_FITTING_SYSTEM_WINDOWS = 64;
  private static final int PFLAG3_FOCUSED_BY_DEFAULT = 262144;
  private static final int PFLAG3_HAS_OVERLAPPING_RENDERING_FORCED = 16777216;
  static final int PFLAG3_IMPORTANT_FOR_AUTOFILL_MASK = 7864320;
  static final int PFLAG3_IMPORTANT_FOR_AUTOFILL_SHIFT = 19;
  private static final int PFLAG3_IS_AUTOFILLED = 65536;
  static final int PFLAG3_IS_LAID_OUT = 4;
  static final int PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT = 8;
  static final int PFLAG3_NESTED_SCROLLING_ENABLED = 128;
  static final int PFLAG3_NOTIFY_AUTOFILL_ENTER_ON_LAYOUT = 134217728;
  private static final int PFLAG3_NO_REVEAL_ON_FOCUS = 67108864;
  private static final int PFLAG3_OVERLAPPING_RENDERING_FORCED_VALUE = 8388608;
  private static final int PFLAG3_SCREEN_READER_FOCUSABLE = 268435456;
  static final int PFLAG3_SCROLL_INDICATOR_BOTTOM = 512;
  static final int PFLAG3_SCROLL_INDICATOR_END = 8192;
  static final int PFLAG3_SCROLL_INDICATOR_LEFT = 1024;
  static final int PFLAG3_SCROLL_INDICATOR_RIGHT = 2048;
  static final int PFLAG3_SCROLL_INDICATOR_START = 4096;
  static final int PFLAG3_SCROLL_INDICATOR_TOP = 256;
  static final int PFLAG3_TEMPORARY_DETACH = 33554432;
  static final int PFLAG3_VIEW_IS_ANIMATING_ALPHA = 2;
  static final int PFLAG3_VIEW_IS_ANIMATING_TRANSFORM = 1;
  static final int PFLAG_ACTIVATED = 1073741824;
  static final int PFLAG_ALPHA_SET = 262144;
  static final int PFLAG_ANIMATION_STARTED = 65536;
  private static final int PFLAG_AWAKEN_SCROLL_BARS_ON_ATTACH = 134217728;
  static final int PFLAG_CANCEL_NEXT_UP_EVENT = 67108864;
  static final int PFLAG_DIRTY = 2097152;
  static final int PFLAG_DIRTY_MASK = 2097152;
  static final int PFLAG_DRAWABLE_STATE_DIRTY = 1024;
  static final int PFLAG_DRAWING_CACHE_VALID = 32768;
  static final int PFLAG_DRAWN = 32;
  static final int PFLAG_DRAW_ANIMATION = 64;
  static final int PFLAG_FOCUSED = 2;
  static final int PFLAG_FORCE_LAYOUT = 4096;
  static final int PFLAG_HAS_BOUNDS = 16;
  private static final int PFLAG_HOVERED = 268435456;
  static final int PFLAG_INVALIDATED = Integer.MIN_VALUE;
  static final int PFLAG_IS_ROOT_NAMESPACE = 8;
  static final int PFLAG_LAYOUT_REQUIRED = 8192;
  static final int PFLAG_MEASURED_DIMENSION_SET = 2048;
  private static final int PFLAG_NOTIFY_AUTOFILL_MANAGER_ON_CLICK = 536870912;
  static final int PFLAG_OPAQUE_BACKGROUND = 8388608;
  static final int PFLAG_OPAQUE_MASK = 25165824;
  static final int PFLAG_OPAQUE_SCROLLBARS = 16777216;
  private static final int PFLAG_PREPRESSED = 33554432;
  private static final int PFLAG_PRESSED = 16384;
  static final int PFLAG_REQUEST_TRANSPARENT_REGIONS = 512;
  private static final int PFLAG_SAVE_STATE_CALLED = 131072;
  static final int PFLAG_SCROLL_CONTAINER = 524288;
  static final int PFLAG_SCROLL_CONTAINER_ADDED = 1048576;
  static final int PFLAG_SELECTED = 4;
  static final int PFLAG_SKIP_DRAW = 128;
  static final int PFLAG_WANTS_FOCUS = 1;
  private static final int POPULATING_ACCESSIBILITY_EVENT_TYPES = 172479;
  protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET;
  protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_ENABLED_SELECTED_STATE_SET;
  protected static final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_ENABLED_STATE_SET;
  protected static final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET;
  protected static final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_SELECTED_STATE_SET;
  protected static final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET;
  protected static final int[] PRESSED_STATE_SET;
  protected static final int[] PRESSED_WINDOW_FOCUSED_STATE_SET;
  private static final int PROVIDER_BACKGROUND = 0;
  private static final int PROVIDER_BOUNDS = 2;
  private static final int PROVIDER_NONE = 1;
  private static final int PROVIDER_PADDED_BOUNDS = 3;
  public static final int PUBLIC_STATUS_BAR_VISIBILITY_MASK = 16375;
  public static final Property<View, Float> ROTATION;
  public static final Property<View, Float> ROTATION_X;
  public static final Property<View, Float> ROTATION_Y;
  static final int SAVE_DISABLED = 65536;
  static final int SAVE_DISABLED_MASK = 65536;
  public static final Property<View, Float> SCALE_X;
  public static final Property<View, Float> SCALE_Y;
  public static final int SCREEN_STATE_OFF = 0;
  public static final int SCREEN_STATE_ON = 1;
  static final int SCROLLBARS_HORIZONTAL = 256;
  static final int SCROLLBARS_INSET_MASK = 16777216;
  public static final int SCROLLBARS_INSIDE_INSET = 16777216;
  public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
  static final int SCROLLBARS_MASK = 768;
  static final int SCROLLBARS_NONE = 0;
  public static final int SCROLLBARS_OUTSIDE_INSET = 50331648;
  static final int SCROLLBARS_OUTSIDE_MASK = 33554432;
  public static final int SCROLLBARS_OUTSIDE_OVERLAY = 33554432;
  static final int SCROLLBARS_STYLE_MASK = 50331648;
  static final int SCROLLBARS_VERTICAL = 512;
  public static final int SCROLLBAR_POSITION_DEFAULT = 0;
  public static final int SCROLLBAR_POSITION_LEFT = 1;
  public static final int SCROLLBAR_POSITION_RIGHT = 2;
  public static final int SCROLL_AXIS_HORIZONTAL = 1;
  public static final int SCROLL_AXIS_NONE = 0;
  public static final int SCROLL_AXIS_VERTICAL = 2;
  static final int SCROLL_INDICATORS_NONE = 0;
  static final int SCROLL_INDICATORS_PFLAG3_MASK = 16128;
  static final int SCROLL_INDICATORS_TO_PFLAGS3_LSHIFT = 8;
  public static final int SCROLL_INDICATOR_BOTTOM = 2;
  public static final int SCROLL_INDICATOR_END = 32;
  public static final int SCROLL_INDICATOR_LEFT = 4;
  public static final int SCROLL_INDICATOR_RIGHT = 8;
  public static final int SCROLL_INDICATOR_START = 16;
  public static final int SCROLL_INDICATOR_TOP = 1;
  protected static final int[] SELECTED_STATE_SET;
  protected static final int[] SELECTED_WINDOW_FOCUSED_STATE_SET;
  public static final int SOUND_EFFECTS_ENABLED = 134217728;
  @UnsupportedAppUsage
  public static final int STATUS_BAR_DISABLE_BACK = 4194304;
  public static final int STATUS_BAR_DISABLE_CLOCK = 8388608;
  @UnsupportedAppUsage
  public static final int STATUS_BAR_DISABLE_EXPAND = 65536;
  @UnsupportedAppUsage
  public static final int STATUS_BAR_DISABLE_HOME = 2097152;
  public static final int STATUS_BAR_DISABLE_NOTIFICATION_ALERTS = 262144;
  public static final int STATUS_BAR_DISABLE_NOTIFICATION_ICONS = 131072;
  public static final int STATUS_BAR_DISABLE_NOTIFICATION_TICKER = 524288;
  @UnsupportedAppUsage
  public static final int STATUS_BAR_DISABLE_RECENT = 16777216;
  public static final int STATUS_BAR_DISABLE_SEARCH = 33554432;
  public static final int STATUS_BAR_DISABLE_SYSTEM_INFO = 1048576;
  @Deprecated
  public static final int STATUS_BAR_HIDDEN = 1;
  public static final int STATUS_BAR_TRANSIENT = 67108864;
  public static final int STATUS_BAR_TRANSLUCENT = 1073741824;
  public static final int STATUS_BAR_TRANSPARENT = 8;
  public static final int STATUS_BAR_UNHIDE = 268435456;
  @Deprecated
  public static final int STATUS_BAR_VISIBLE = 0;
  public static final int SYSTEM_UI_CLEARABLE_FLAGS = 7;
  public static final int SYSTEM_UI_FLAG_FULLSCREEN = 4;
  public static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;
  public static final int SYSTEM_UI_FLAG_IMMERSIVE = 2048;
  public static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 4096;
  public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 1024;
  public static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 512;
  public static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 256;
  public static final int SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR = 16;
  public static final int SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 8192;
  public static final int SYSTEM_UI_FLAG_LOW_PROFILE = 1;
  public static final int SYSTEM_UI_FLAG_VISIBLE = 0;
  public static final int SYSTEM_UI_LAYOUT_FLAGS = 1536;
  private static final int SYSTEM_UI_RESERVED_LEGACY1 = 16384;
  private static final int SYSTEM_UI_RESERVED_LEGACY2 = 65536;
  public static final int SYSTEM_UI_TRANSPARENT = 32776;
  public static final int TEXT_ALIGNMENT_CENTER = 4;
  private static final int TEXT_ALIGNMENT_DEFAULT = 1;
  public static final int TEXT_ALIGNMENT_GRAVITY = 1;
  public static final int TEXT_ALIGNMENT_INHERIT = 0;
  static final int TEXT_ALIGNMENT_RESOLVED_DEFAULT = 1;
  public static final int TEXT_ALIGNMENT_TEXT_END = 3;
  public static final int TEXT_ALIGNMENT_TEXT_START = 2;
  public static final int TEXT_ALIGNMENT_VIEW_END = 6;
  public static final int TEXT_ALIGNMENT_VIEW_START = 5;
  public static final int TEXT_DIRECTION_ANY_RTL = 2;
  private static final int TEXT_DIRECTION_DEFAULT = 0;
  public static final int TEXT_DIRECTION_FIRST_STRONG = 1;
  public static final int TEXT_DIRECTION_FIRST_STRONG_LTR = 6;
  public static final int TEXT_DIRECTION_FIRST_STRONG_RTL = 7;
  public static final int TEXT_DIRECTION_INHERIT = 0;
  public static final int TEXT_DIRECTION_LOCALE = 5;
  public static final int TEXT_DIRECTION_LTR = 3;
  static final int TEXT_DIRECTION_RESOLVED_DEFAULT = 1;
  public static final int TEXT_DIRECTION_RTL = 4;
  static final int TOOLTIP = 1073741824;
  public static final Property<View, Float> TRANSLATION_X;
  public static final Property<View, Float> TRANSLATION_Y;
  public static final Property<View, Float> TRANSLATION_Z;
  private static final int UNDEFINED_PADDING = Integer.MIN_VALUE;
  protected static final String VIEW_LOG_TAG = "View";
  protected static final int VIEW_STRUCTURE_FOR_ASSIST = 0;
  protected static final int VIEW_STRUCTURE_FOR_AUTOFILL = 1;
  protected static final int VIEW_STRUCTURE_FOR_CONTENT_CAPTURE = 2;
  private static final int[] VISIBILITY_FLAGS;
  static final int VISIBILITY_MASK = 12;
  public static final int VISIBLE = 0;
  static final int WILL_NOT_CACHE_DRAWING = 131072;
  static final int WILL_NOT_DRAW = 128;
  protected static final int[] WINDOW_FOCUSED_STATE_SET;
  public static final Property<View, Float> X;
  public static final Property<View, Float> Y;
  public static final Property<View, Float> Z;
  private static SparseArray<String> mAttributeMap;
  private static boolean sAcceptZeroSizeDragShadow;
  private static boolean sAlwaysAssignFocus;
  private static boolean sAlwaysRemeasureExactly;
  private static boolean sAutoFocusableOffUIThreadWontNotifyParents;
  static boolean sBrokenInsetsDispatch;
  protected static boolean sBrokenWindowBackground;
  private static boolean sCanFocusZeroSized;
  static boolean sCascadedDragDrop;
  private static boolean sCompatibilityDone;
  private static Paint sDebugPaint;
  public static boolean sDebugViewAttributes = false;
  public static String sDebugViewAttributesApplicationPackage;
  @UnsupportedAppUsage
  private static int sForceUseForceDark = -1;
  static boolean sHasFocusableExcludeAutoFocusable;
  private static boolean sIgnoreMeasureCache;
  private static int sNextAccessibilityViewId;
  private static final AtomicInteger sNextGeneratedId;
  protected static boolean sPreserveMarginParamsInLayoutParamConversion;
  static boolean sTextureViewIgnoresDrawableSetters;
  static final ThreadLocal<Rect> sThreadLocal;
  private static boolean sThrowOnInvalidFloatProperties;
  private static boolean sUseBrokenMakeMeasureSpec;
  private static boolean sUseDefaultFocusHighlight;
  static boolean sUseZeroUnspecifiedMeasureSpec;
  private int mAccessibilityCursorPosition;
  @UnsupportedAppUsage
  AccessibilityDelegate mAccessibilityDelegate;
  private CharSequence mAccessibilityPaneTitle;
  private int mAccessibilityTraversalAfterId;
  private int mAccessibilityTraversalBeforeId;
  @UnsupportedAppUsage
  private int mAccessibilityViewId;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private ViewPropertyAnimator mAnimator;
  @UnsupportedAppUsage(maxTargetSdk=28)
  AttachInfo mAttachInfo;
  protected WeakReference<Activity> mAttachedActivity;
  private SparseArray<int[]> mAttributeResolutionStacks;
  private SparseIntArray mAttributeSourceResId;
  @ViewDebug.ExportedProperty(category="attributes", hasAdjacentMapping=true)
  public String[] mAttributes;
  private String[] mAutofillHints;
  private AutofillId mAutofillId;
  private int mAutofillViewId;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="bg_")
  @UnsupportedAppUsage
  private Drawable mBackground;
  private RenderNode mBackgroundRenderNode;
  @UnsupportedAppUsage
  private int mBackgroundResource;
  private boolean mBackgroundSizeChanged;
  private TintInfo mBackgroundTint;
  @ViewDebug.ExportedProperty(category="layout")
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected int mBottom;
  private ContentCaptureSession mCachedContentCaptureSession;
  @UnsupportedAppUsage
  public boolean mCachingFailed;
  @ViewDebug.ExportedProperty(category="drawing")
  Rect mClipBounds;
  private ContentCaptureSession mContentCaptureSession;
  private CharSequence mContentDescription;
  @ViewDebug.ExportedProperty(deepExport=true)
  @UnsupportedAppUsage
  protected Context mContext;
  protected Animation mCurrentAnimation;
  private Drawable mDefaultFocusHighlight;
  private Drawable mDefaultFocusHighlightCache;
  boolean mDefaultFocusHighlightEnabled;
  private boolean mDefaultFocusHighlightSizeChanged;
  private int[] mDrawableState;
  @UnsupportedAppUsage
  private Bitmap mDrawingCache;
  private int mDrawingCacheBackgroundColor;
  private int mExplicitStyle;
  protected volatile boolean mFirst;
  private ViewTreeObserver mFloatingTreeObserver;
  private boolean mForceDark = true;
  @ViewDebug.ExportedProperty(deepExport=true, prefix="fg_")
  private ForegroundInfo mForegroundInfo;
  private ArrayList<FrameMetricsObserver> mFrameMetricsObservers;
  GhostView mGhostView;
  boolean mHapticEnabledExplicitly;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private boolean mHasPerformedLongPress;
  private boolean mHoveringTouchDelegate;
  @ViewDebug.ExportedProperty(resolveId=true)
  int mID;
  private boolean mIgnoreNextUpEvent;
  private boolean mInContextButtonPress;
  protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
  protected boolean mIsWebView;
  @UnsupportedAppUsage
  private SparseArray<Object> mKeyedTags;
  private int mLabelForId;
  private boolean mLastIsOpaque;
  Paint mLayerPaint;
  @ViewDebug.ExportedProperty(category="drawing", mapping={@ViewDebug.IntToString(from=0, to="NONE"), @ViewDebug.IntToString(from=1, to="SOFTWARE"), @ViewDebug.IntToString(from=2, to="HARDWARE")})
  int mLayerType;
  private Insets mLayoutInsets;
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected ViewGroup.LayoutParams mLayoutParams;
  @ViewDebug.ExportedProperty(category="layout")
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected int mLeft;
  private boolean mLeftPaddingDefined;
  @UnsupportedAppUsage
  ListenerInfo mListenerInfo;
  private float mLongClickX;
  private float mLongClickY;
  private MatchIdPredicate mMatchIdPredicate;
  private MatchLabelForPredicate mMatchLabelForPredicate;
  private LongSparseLongArray mMeasureCache;
  @ViewDebug.ExportedProperty(category="measurement")
  @UnsupportedAppUsage
  int mMeasuredHeight;
  @ViewDebug.ExportedProperty(category="measurement")
  @UnsupportedAppUsage
  int mMeasuredWidth;
  @ViewDebug.ExportedProperty(category="measurement")
  @UnsupportedAppUsage(maxTargetSdk=28)
  private int mMinHeight;
  @ViewDebug.ExportedProperty(category="measurement")
  @UnsupportedAppUsage(maxTargetSdk=28)
  private int mMinWidth;
  private ViewParent mNestedScrollingParent;
  int mNextClusterForwardId;
  private int mNextFocusDownId;
  int mNextFocusForwardId;
  private int mNextFocusLeftId;
  private int mNextFocusRightId;
  private int mNextFocusUpId;
  int mOldHeightMeasureSpec;
  int mOldWidthMeasureSpec;
  ViewOutlineProvider mOutlineProvider;
  private int mOverScrollMode;
  ViewOverlay mOverlay;
  @ViewDebug.ExportedProperty(category="padding")
  @UnsupportedAppUsage
  protected int mPaddingBottom;
  @ViewDebug.ExportedProperty(category="padding")
  @UnsupportedAppUsage
  protected int mPaddingLeft;
  @ViewDebug.ExportedProperty(category="padding")
  @UnsupportedAppUsage
  protected int mPaddingRight;
  @ViewDebug.ExportedProperty(category="padding")
  @UnsupportedAppUsage
  protected int mPaddingTop;
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected ViewParent mParent;
  private CheckForLongPress mPendingCheckForLongPress;
  @UnsupportedAppUsage
  private CheckForTap mPendingCheckForTap;
  private PerformClick mPerformClick;
  private PointerIcon mPointerIcon;
  @ViewDebug.ExportedProperty(flagMapping={@ViewDebug.FlagToString(equals=4096, mask=4096, name="FORCE_LAYOUT"), @ViewDebug.FlagToString(equals=8192, mask=8192, name="LAYOUT_REQUIRED"), @ViewDebug.FlagToString(equals=32768, mask=32768, name="DRAWING_CACHE_INVALID", outputIf=false), @ViewDebug.FlagToString(equals=32, mask=32, name="DRAWN", outputIf=true), @ViewDebug.FlagToString(equals=32, mask=32, name="NOT_DRAWN", outputIf=false), @ViewDebug.FlagToString(equals=2097152, mask=2097152, name="DIRTY")}, formatToHexString=true)
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769414L)
  public int mPrivateFlags;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768943L)
  int mPrivateFlags2;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=129147060L)
  int mPrivateFlags3;
  @UnsupportedAppUsage
  boolean mRecreateDisplayList;
  @UnsupportedAppUsage
  final RenderNode mRenderNode;
  @UnsupportedAppUsage
  private final Resources mResources;
  @ViewDebug.ExportedProperty(category="layout")
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected int mRight;
  private boolean mRightPaddingDefined;
  private RoundScrollbarRenderer mRoundScrollbarRenderer;
  private HandlerActionQueue mRunQueue;
  @UnsupportedAppUsage
  private ScrollabilityCache mScrollCache;
  private Drawable mScrollIndicatorDrawable;
  @ViewDebug.ExportedProperty(category="scrolling")
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected int mScrollX;
  @ViewDebug.ExportedProperty(category="scrolling")
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected int mScrollY;
  private SendViewScrolledAccessibilityEvent mSendViewScrolledAccessibilityEvent;
  private boolean mSendingHoverAccessibilityEvents;
  private int mSourceLayoutId;
  @UnsupportedAppUsage
  String mStartActivityRequestWho;
  private StateListAnimator mStateListAnimator;
  @ViewDebug.ExportedProperty(flagMapping={@ViewDebug.FlagToString(equals=1, mask=1, name="LOW_PROFILE"), @ViewDebug.FlagToString(equals=2, mask=2, name="HIDE_NAVIGATION"), @ViewDebug.FlagToString(equals=4, mask=4, name="FULLSCREEN"), @ViewDebug.FlagToString(equals=256, mask=256, name="LAYOUT_STABLE"), @ViewDebug.FlagToString(equals=512, mask=512, name="LAYOUT_HIDE_NAVIGATION"), @ViewDebug.FlagToString(equals=1024, mask=1024, name="LAYOUT_FULLSCREEN"), @ViewDebug.FlagToString(equals=2048, mask=2048, name="IMMERSIVE"), @ViewDebug.FlagToString(equals=4096, mask=4096, name="IMMERSIVE_STICKY"), @ViewDebug.FlagToString(equals=8192, mask=8192, name="LIGHT_STATUS_BAR"), @ViewDebug.FlagToString(equals=16, mask=16, name="LIGHT_NAVIGATION_BAR"), @ViewDebug.FlagToString(equals=65536, mask=65536, name="STATUS_BAR_DISABLE_EXPAND"), @ViewDebug.FlagToString(equals=131072, mask=131072, name="STATUS_BAR_DISABLE_NOTIFICATION_ICONS"), @ViewDebug.FlagToString(equals=262144, mask=262144, name="STATUS_BAR_DISABLE_NOTIFICATION_ALERTS"), @ViewDebug.FlagToString(equals=524288, mask=524288, name="STATUS_BAR_DISABLE_NOTIFICATION_TICKER"), @ViewDebug.FlagToString(equals=1048576, mask=1048576, name="STATUS_BAR_DISABLE_SYSTEM_INFO"), @ViewDebug.FlagToString(equals=2097152, mask=2097152, name="STATUS_BAR_DISABLE_HOME"), @ViewDebug.FlagToString(equals=4194304, mask=4194304, name="STATUS_BAR_DISABLE_BACK"), @ViewDebug.FlagToString(equals=8388608, mask=8388608, name="STATUS_BAR_DISABLE_CLOCK"), @ViewDebug.FlagToString(equals=16777216, mask=16777216, name="STATUS_BAR_DISABLE_RECENT"), @ViewDebug.FlagToString(equals=33554432, mask=33554432, name="STATUS_BAR_DISABLE_SEARCH"), @ViewDebug.FlagToString(equals=67108864, mask=67108864, name="STATUS_BAR_TRANSIENT"), @ViewDebug.FlagToString(equals=134217728, mask=134217728, name="NAVIGATION_BAR_TRANSIENT"), @ViewDebug.FlagToString(equals=268435456, mask=268435456, name="STATUS_BAR_UNHIDE"), @ViewDebug.FlagToString(equals=536870912, mask=536870912, name="NAVIGATION_BAR_UNHIDE"), @ViewDebug.FlagToString(equals=1073741824, mask=1073741824, name="STATUS_BAR_TRANSLUCENT"), @ViewDebug.FlagToString(equals=Integer.MIN_VALUE, mask=Integer.MIN_VALUE, name="NAVIGATION_BAR_TRANSLUCENT"), @ViewDebug.FlagToString(equals=32768, mask=32768, name="NAVIGATION_BAR_TRANSPARENT"), @ViewDebug.FlagToString(equals=8, mask=8, name="STATUS_BAR_TRANSPARENT")}, formatToHexString=true)
  int mSystemUiVisibility;
  @UnsupportedAppUsage
  protected Object mTag;
  private int[] mTempNestedScrollConsumed;
  TooltipInfo mTooltipInfo;
  @ViewDebug.ExportedProperty(category="layout")
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected int mTop;
  private TouchDelegate mTouchDelegate;
  private int mTouchSlop;
  @UnsupportedAppUsage
  public TransformationInfo mTransformationInfo;
  int mTransientStateCount;
  private String mTransitionName;
  @UnsupportedAppUsage
  private Bitmap mUnscaledDrawingCache;
  private UnsetPressedState mUnsetPressedState;
  @ViewDebug.ExportedProperty(category="padding")
  protected int mUserPaddingBottom;
  @ViewDebug.ExportedProperty(category="padding")
  int mUserPaddingEnd;
  @ViewDebug.ExportedProperty(category="padding")
  protected int mUserPaddingLeft;
  int mUserPaddingLeftInitial;
  @ViewDebug.ExportedProperty(category="padding")
  protected int mUserPaddingRight;
  int mUserPaddingRightInitial;
  @ViewDebug.ExportedProperty(category="padding")
  int mUserPaddingStart;
  private float mVerticalScrollFactor;
  @UnsupportedAppUsage
  private int mVerticalScrollbarPosition;
  @ViewDebug.ExportedProperty(formatToHexString=true)
  @UnsupportedAppUsage(maxTargetSdk=28)
  int mViewFlags;
  private Handler mVisibilityChangeForAutofillHandler;
  int mWindowAttachCount;
  
  static
  {
    AUTOFILL_HIGHLIGHT_ATTR = new int[] { 16844136 };
    sCompatibilityDone = false;
    sUseBrokenMakeMeasureSpec = false;
    sUseZeroUnspecifiedMeasureSpec = false;
    sIgnoreMeasureCache = false;
    sAlwaysRemeasureExactly = false;
    sTextureViewIgnoresDrawableSetters = false;
    VISIBILITY_FLAGS = new int[] { 0, 4, 8 };
    DRAWING_CACHE_QUALITY_FLAGS = new int[] { 0, 524288, 1048576 };
    FORCE_DARK_WHITE_LIST = new HashMap();
    FORCE_DARK_WHITE_LIST.put("com.duokan.reader", "");
    FORCE_DARK_WHITE_LIST.put("com.xiaomi.vipaccount", "");
    FORCE_DARK_WHITE_LIST.put("com.mfashiongallery.emag", "");
    FORCE_DARK_WHITE_LIST.put("com.xiaomi.smarthome", "");
    FORCE_DARK_WHITE_LIST.put("com.wali.live", "");
    FORCE_DARK_WHITE_LIST.put("com.xiaomi.gamecenter", "");
    FORCE_DARK_WHITE_LIST.put("com.duokan.phone.remotecontroller", "");
    FORCE_DARK_WHITE_LIST.put("com.xiaomi.channel", "");
    FORCE_DARK_WHITE_LIST.put("com.youku.phone", "");
    FORCE_DARK_WHITE_LIST.put("com.baidu.input_mi", "");
    FORCE_DARK_WHITE_LIST.put("com.miui.fm", "");
    FORCE_DARK_WHITE_LIST.put("com.iflytek.inputmethod.miui", "");
    FORCE_DARK_WHITE_LIST.put("com.sohu.inputmethod.sogou.xiaomi", "");
    FORCE_DARK_WHITE_LIST.put("android.uirendering.cts", "");
    FORCE_DARK_WHITE_LIST.put("com.miui.weather2", "");
    EMPTY_STATE_SET = StateSet.get(0);
    WINDOW_FOCUSED_STATE_SET = StateSet.get(1);
    SELECTED_STATE_SET = StateSet.get(2);
    SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(3);
    FOCUSED_STATE_SET = StateSet.get(4);
    FOCUSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(5);
    FOCUSED_SELECTED_STATE_SET = StateSet.get(6);
    FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(7);
    ENABLED_STATE_SET = StateSet.get(8);
    ENABLED_WINDOW_FOCUSED_STATE_SET = StateSet.get(9);
    ENABLED_SELECTED_STATE_SET = StateSet.get(10);
    ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(11);
    ENABLED_FOCUSED_STATE_SET = StateSet.get(12);
    ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(13);
    ENABLED_FOCUSED_SELECTED_STATE_SET = StateSet.get(14);
    ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(15);
    PRESSED_STATE_SET = StateSet.get(16);
    PRESSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(17);
    PRESSED_SELECTED_STATE_SET = StateSet.get(18);
    PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(19);
    PRESSED_FOCUSED_STATE_SET = StateSet.get(20);
    PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(21);
    PRESSED_FOCUSED_SELECTED_STATE_SET = StateSet.get(22);
    PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(23);
    PRESSED_ENABLED_STATE_SET = StateSet.get(24);
    PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = StateSet.get(25);
    PRESSED_ENABLED_SELECTED_STATE_SET = StateSet.get(26);
    PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(27);
    PRESSED_ENABLED_FOCUSED_STATE_SET = StateSet.get(28);
    PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = StateSet.get(29);
    PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = StateSet.get(30);
    PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = StateSet.get(31);
    DEBUG_CORNERS_COLOR = Color.rgb(63, 127, 255);
    sThreadLocal = new ThreadLocal();
    LAYOUT_DIRECTION_FLAGS = new int[] { 0, 1, 2, 3 };
    PFLAG2_TEXT_DIRECTION_FLAGS = new int[] { 0, 64, 128, 192, 256, 320, 384, 448 };
    PFLAG2_TEXT_ALIGNMENT_FLAGS = new int[] { 0, 8192, 16384, 24576, 32768, 40960, 49152 };
    sNextGeneratedId = new AtomicInteger(1);
    ALPHA = new FloatProperty("alpha")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getAlpha());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setAlpha(paramAnonymousFloat);
      }
    };
    TRANSLATION_X = new FloatProperty("translationX")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getTranslationX());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setTranslationX(paramAnonymousFloat);
      }
    };
    TRANSLATION_Y = new FloatProperty("translationY")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getTranslationY());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setTranslationY(paramAnonymousFloat);
      }
    };
    TRANSLATION_Z = new FloatProperty("translationZ")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getTranslationZ());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setTranslationZ(paramAnonymousFloat);
      }
    };
    X = new FloatProperty("x")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getX());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setX(paramAnonymousFloat);
      }
    };
    Y = new FloatProperty("y")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getY());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setY(paramAnonymousFloat);
      }
    };
    Z = new FloatProperty("z")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getZ());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setZ(paramAnonymousFloat);
      }
    };
    ROTATION = new FloatProperty("rotation")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getRotation());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setRotation(paramAnonymousFloat);
      }
    };
    ROTATION_X = new FloatProperty("rotationX")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getRotationX());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setRotationX(paramAnonymousFloat);
      }
    };
    ROTATION_Y = new FloatProperty("rotationY")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getRotationY());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setRotationY(paramAnonymousFloat);
      }
    };
    SCALE_X = new FloatProperty("scaleX")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getScaleX());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setScaleX(paramAnonymousFloat);
      }
    };
    SCALE_Y = new FloatProperty("scaleY")
    {
      public Float get(View paramAnonymousView)
      {
        return Float.valueOf(paramAnonymousView.getScaleY());
      }
      
      public void setValue(View paramAnonymousView, float paramAnonymousFloat)
      {
        paramAnonymousView.setScaleY(paramAnonymousFloat);
      }
    };
    Android_View_View.Extension.get().bindOriginal(new Android_View_View.Interface()
    {
      public void init(View paramAnonymousView, Context paramAnonymousContext, AttributeSet paramAnonymousAttributeSet, int paramAnonymousInt1, int paramAnonymousInt2) {}
      
      public int[] onCreateDrawableState(View paramAnonymousView, int paramAnonymousInt)
      {
        return paramAnonymousView.originalOnCreateDrawableState(paramAnonymousInt);
      }
      
      public void refreshDrawableState(View paramAnonymousView)
      {
        paramAnonymousView.originalRefreshDrawableState();
      }
    });
  }
  
  @UnsupportedAppUsage
  View()
  {
    this.mCurrentAnimation = null;
    this.mRecreateDisplayList = false;
    this.mID = -1;
    this.mAutofillViewId = -1;
    this.mAccessibilityViewId = -1;
    this.mAccessibilityCursorPosition = -1;
    this.mTag = null;
    this.mTransientStateCount = 0;
    this.mClipBounds = null;
    this.mPaddingLeft = 0;
    this.mPaddingRight = 0;
    this.mLabelForId = -1;
    this.mAccessibilityTraversalBeforeId = -1;
    this.mAccessibilityTraversalAfterId = -1;
    this.mLeftPaddingDefined = false;
    this.mRightPaddingDefined = false;
    this.mOldWidthMeasureSpec = Integer.MIN_VALUE;
    this.mOldHeightMeasureSpec = Integer.MIN_VALUE;
    this.mLongClickX = NaN.0F;
    this.mLongClickY = NaN.0F;
    this.mDrawableState = null;
    this.mOutlineProvider = ViewOutlineProvider.BACKGROUND;
    this.mNextFocusLeftId = -1;
    this.mNextFocusRightId = -1;
    this.mNextFocusUpId = -1;
    this.mNextFocusDownId = -1;
    this.mNextFocusForwardId = -1;
    this.mNextClusterForwardId = -1;
    this.mDefaultFocusHighlightEnabled = true;
    this.mPendingCheckForTap = null;
    this.mTouchDelegate = null;
    this.mHoveringTouchDelegate = false;
    this.mDrawingCacheBackgroundColor = 0;
    this.mAnimator = null;
    this.mLayerType = 0;
    InputEventConsistencyVerifier localInputEventConsistencyVerifier;
    if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
      localInputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, 0);
    } else {
      localInputEventConsistencyVerifier = null;
    }
    this.mInputEventConsistencyVerifier = localInputEventConsistencyVerifier;
    this.mSourceLayoutId = 0;
    this.mFirst = true;
    this.mIsWebView = false;
    this.mResources = null;
    this.mRenderNode = RenderNode.create(getClass().getName(), new ViewAnimationHostBridge(this));
  }
  
  public View(Context paramContext)
  {
    Object localObject1 = null;
    this.mCurrentAnimation = null;
    boolean bool1 = false;
    this.mRecreateDisplayList = false;
    this.mID = -1;
    this.mAutofillViewId = -1;
    this.mAccessibilityViewId = -1;
    this.mAccessibilityCursorPosition = -1;
    this.mTag = null;
    this.mTransientStateCount = 0;
    this.mClipBounds = null;
    this.mPaddingLeft = 0;
    this.mPaddingRight = 0;
    this.mLabelForId = -1;
    this.mAccessibilityTraversalBeforeId = -1;
    this.mAccessibilityTraversalAfterId = -1;
    this.mLeftPaddingDefined = false;
    this.mRightPaddingDefined = false;
    this.mOldWidthMeasureSpec = Integer.MIN_VALUE;
    this.mOldHeightMeasureSpec = Integer.MIN_VALUE;
    this.mLongClickX = NaN.0F;
    this.mLongClickY = NaN.0F;
    this.mDrawableState = null;
    this.mOutlineProvider = ViewOutlineProvider.BACKGROUND;
    this.mNextFocusLeftId = -1;
    this.mNextFocusRightId = -1;
    this.mNextFocusUpId = -1;
    this.mNextFocusDownId = -1;
    this.mNextFocusForwardId = -1;
    this.mNextClusterForwardId = -1;
    this.mDefaultFocusHighlightEnabled = true;
    this.mPendingCheckForTap = null;
    this.mTouchDelegate = null;
    this.mHoveringTouchDelegate = false;
    this.mDrawingCacheBackgroundColor = 0;
    this.mAnimator = null;
    this.mLayerType = 0;
    if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
      localObject2 = new InputEventConsistencyVerifier(this, 0);
    } else {
      localObject2 = null;
    }
    this.mInputEventConsistencyVerifier = ((InputEventConsistencyVerifier)localObject2);
    this.mSourceLayoutId = 0;
    this.mFirst = true;
    this.mIsWebView = false;
    this.mContext = paramContext;
    initForcedUseForceDark(paramContext);
    Object localObject2 = localObject1;
    if (paramContext != null) {
      localObject2 = paramContext.getResources();
    }
    this.mResources = ((Resources)localObject2);
    this.mViewFlags = 402653200;
    this.mPrivateFlags2 = 140296;
    this.mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    setOverScrollMode(1);
    this.mUserPaddingStart = Integer.MIN_VALUE;
    this.mUserPaddingEnd = Integer.MIN_VALUE;
    this.mRenderNode = RenderNode.create(getClass().getName(), new ViewAnimationHostBridge(this));
    if ((!sCompatibilityDone) && (paramContext != null))
    {
      int i = paramContext.getApplicationInfo().targetSdkVersion;
      if (i <= 17) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sUseBrokenMakeMeasureSpec = bool2;
      if (i < 19) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sIgnoreMeasureCache = bool2;
      if (i < 23) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Canvas.sCompatibilityRestore = bool2;
      if (i < 26) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Canvas.sCompatibilitySetBitmap = bool2;
      Canvas.setCompatibilityVersion(i);
      if (i < 23) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sUseZeroUnspecifiedMeasureSpec = bool2;
      if (i <= 23) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sAlwaysRemeasureExactly = bool2;
      if (i <= 23) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sTextureViewIgnoresDrawableSetters = bool2;
      if (i >= 24) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sPreserveMarginParamsInLayoutParamConversion = bool2;
      if (i < 24) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sCascadedDragDrop = bool2;
      if (i < 26) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sHasFocusableExcludeAutoFocusable = bool2;
      if (i < 26) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sAutoFocusableOffUIThreadWontNotifyParents = bool2;
      sUseDefaultFocusHighlight = paramContext.getResources().getBoolean(17891562);
      if (i >= 28) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sThrowOnInvalidFloatProperties = bool2;
      if (i < 28) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sCanFocusZeroSized = bool2;
      if (i < 28) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sAlwaysAssignFocus = bool2;
      if (i < 28) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      sAcceptZeroSizeDragShadow = bool2;
      if ((ViewRootImpl.sNewInsetsMode == 2) && (i >= 29)) {
        bool2 = false;
      } else {
        bool2 = true;
      }
      sBrokenInsetsDispatch = bool2;
      boolean bool2 = bool1;
      if (i < 29) {
        bool2 = true;
      }
      sBrokenWindowBackground = bool2;
      sCompatibilityDone = true;
    }
    this.mIsWebView = InterceptorProxy.checkAndInitWebView(this, Looper.myLooper());
  }
  
  public View(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public View(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public View(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this(paramContext);
    this.mSourceLayoutId = Resources.getAttributeSetSourceResId(paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.View, paramInt1, paramInt2);
    retrieveExplicitStyle(paramContext.getTheme(), paramAttributeSet);
    saveAttributeDataForStyleable(paramContext, R.styleable.View, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    if (sDebugViewAttributes) {
      saveAttributeData(paramAttributeSet, localTypedArray);
    }
    int i = Integer.MIN_VALUE;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = this.mOverScrollMode;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = paramContext.getApplicationInfo().targetSdkVersion;
    int i6 = localTypedArray.getIndexCount();
    int i7 = 0x0 | 0x10;
    int i8 = 0x0 | 0x10;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    float f6 = 0.0F;
    float f7 = 0.0F;
    float f8 = 1.0F;
    float f9 = 1.0F;
    int i9 = -1;
    int i10 = -1;
    int i11 = -1;
    int i12 = 0;
    int i13 = 0;
    int i14 = 0;
    int i15 = -1;
    int i16 = -1;
    int i17 = -1;
    int i18 = Integer.MIN_VALUE;
    Drawable localDrawable = null;
    boolean bool1 = false;
    boolean bool2 = false;
    int i19 = -1;
    int i20;
    while (i12 < i6)
    {
      i20 = localTypedArray.getIndex(i12);
      Object localObject2;
      if (i20 != 109)
      {
        switch (i20)
        {
        default: 
          switch (i20)
          {
          default: 
            switch (i20)
            {
            default: 
              break;
            case 102: 
              this.mRenderNode.setForceDarkAllowed(localTypedArray.getBoolean(i20, true));
              break;
            case 101: 
              setOutlineAmbientShadowColor(localTypedArray.getColor(i20, -16777216));
              break;
            case 100: 
              setOutlineSpotShadowColor(localTypedArray.getColor(i20, -16777216));
              break;
            case 99: 
              setAccessibilityHeading(localTypedArray.getBoolean(i20, false));
              break;
            case 98: 
              if (localTypedArray.peekValue(i20) != null)
              {
                setAccessibilityPaneTitle(localTypedArray.getString(i20));
                break label3383;
              }
              break;
            case 97: 
              if (localTypedArray.peekValue(i20) != null)
              {
                setScreenReaderFocusable(localTypedArray.getBoolean(i20, false));
                break label3383;
              }
              break;
            case 96: 
              if (localTypedArray.peekValue(i20) != null)
              {
                setDefaultFocusHighlightEnabled(localTypedArray.getBoolean(i20, true));
                break label3383;
              }
              break;
            case 95: 
              if (localTypedArray.peekValue(i20) != null)
              {
                setImportantForAutofill(localTypedArray.getInt(i20, 0));
                break label3383;
              }
              break;
            case 94: 
              if (localTypedArray.peekValue(i20) != null)
              {
                Object localObject1 = null;
                i21 = localTypedArray.getType(i20);
                localObject2 = null;
                if (i21 == 1)
                {
                  i21 = localTypedArray.getResourceId(i20, 0);
                  try
                  {
                    CharSequence[] arrayOfCharSequence = localTypedArray.getTextArray(i20);
                    localObject2 = localObject1;
                  }
                  catch (Resources.NotFoundException localNotFoundException)
                  {
                    localObject1 = getResources().getString(i21);
                    localObject3 = localObject2;
                    localObject2 = localObject1;
                  }
                  localObject1 = localObject2;
                  localObject2 = localObject3;
                }
                else
                {
                  localObject1 = localTypedArray.getString(i20);
                }
                if (localObject2 == null) {
                  if (localObject1 != null) {
                    localObject2 = ((String)localObject1).split(",");
                  } else {
                    throw new IllegalArgumentException("Could not resolve autofillHints");
                  }
                }
                Object localObject3 = new String[localObject2.length];
                i21 = localObject2.length;
                for (i20 = 0; i20 < i21; i20++) {
                  localObject3[i20] = localObject2[i20].toString().trim();
                }
                setAutofillHints((String[])localObject3);
                break label3383;
              }
              break;
            case 93: 
              if (localTypedArray.peekValue(i20) == null) {
                break label3383;
              }
              setFocusedByDefault(localTypedArray.getBoolean(i20, true));
              break;
            case 92: 
              this.mNextClusterForwardId = localTypedArray.getResourceId(i20, -1);
              break;
            case 91: 
              if (localTypedArray.peekValue(i20) == null) {
                break label3383;
              }
              setKeyboardNavigationCluster(localTypedArray.getBoolean(i20, true));
              break;
            case 90: 
              i11 = localTypedArray.getDimensionPixelSize(i20, -1);
              i21 = i8;
              i20 = i7;
              i22 = k;
              break;
            case 89: 
              i10 = localTypedArray.getDimensionPixelSize(i20, -1);
              this.mUserPaddingLeftInitial = i10;
              this.mUserPaddingRightInitial = i10;
              bool2 = true;
              bool1 = true;
              i21 = i8;
              i20 = i7;
              i22 = k;
              break;
            case 88: 
              setTooltipText(localTypedArray.getText(i20));
              break;
            case 87: 
              if (localTypedArray.peekValue(i20) == null) {
                break label3383;
              }
              forceHasOverlappingRendering(localTypedArray.getBoolean(i20, true));
              break;
            case 86: 
              i21 = localTypedArray.getResourceId(i20, 0);
              if (i21 != 0)
              {
                setPointerIcon(PointerIcon.load(paramContext.getResources(), i21));
              }
              else
              {
                i20 = localTypedArray.getInt(i20, 1);
                if (i20 != 1) {
                  setPointerIcon(PointerIcon.getSystemIcon(paramContext, i20));
                }
              }
              break;
            case 85: 
              if (!localTypedArray.getBoolean(i20, false)) {
                break label3383;
              }
              i20 = 0x800000 | i7;
              i21 = 0x800000 | i8;
              i22 = k;
              break;
            case 84: 
              i20 = localTypedArray.getInt(i20, 0) << 8 & 0x3F00;
              if (i20 == 0) {
                break label3383;
              }
              this.mPrivateFlags3 |= i20;
              i2 = 1;
              i21 = i8;
              i20 = i7;
              i22 = k;
              break;
            case 83: 
              setAccessibilityTraversalAfter(localTypedArray.getResourceId(i20, -1));
              break;
            case 82: 
              setAccessibilityTraversalBefore(localTypedArray.getResourceId(i20, -1));
              break;
            case 81: 
              setOutlineProviderFromAttribute(localTypedArray.getInt(81, 0));
              break;
            case 80: 
              if ((i5 < 23) && (!(this instanceof FrameLayout))) {
                break label3383;
              }
              setForegroundTintBlendMode(Drawable.parseBlendMode(localTypedArray.getInt(i20, -1), null));
              break;
            case 79: 
              if ((i5 < 23) && (!(this instanceof FrameLayout))) {
                break label3383;
              }
              setForegroundTintList(localTypedArray.getColorStateList(i20));
              break;
            case 78: 
              if (this.mBackgroundTint == null) {
                this.mBackgroundTint = new TintInfo();
              }
              this.mBackgroundTint.mBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(78, -1), null);
              this.mBackgroundTint.mHasTintMode = true;
              break;
            case 77: 
              if (this.mBackgroundTint == null) {
                this.mBackgroundTint = new TintInfo();
              }
              this.mBackgroundTint.mTintList = localTypedArray.getColorStateList(77);
              this.mBackgroundTint.mHasTintList = true;
              break;
            case 76: 
              setStateListAnimator(AnimatorInflater.loadStateListAnimator(paramContext, localTypedArray.getResourceId(i20, 0)));
              break;
            case 75: 
              f4 = localTypedArray.getDimension(i20, 0.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 74: 
              setNestedScrollingEnabled(localTypedArray.getBoolean(i20, false));
              break;
            case 73: 
              setTransitionName(localTypedArray.getString(i20));
              break;
            case 72: 
              f3 = localTypedArray.getDimension(i20, 0.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 71: 
              setAccessibilityLiveRegion(localTypedArray.getInt(i20, 0));
              break;
            case 70: 
              setLabelFor(localTypedArray.getResourceId(i20, -1));
              break;
            case 69: 
              i = localTypedArray.getDimensionPixelSize(i20, Integer.MIN_VALUE);
              if (i != Integer.MIN_VALUE) {
                i20 = 1;
              } else {
                i20 = 0;
              }
              i4 = i20;
              i21 = i8;
              i20 = i7;
              i22 = k;
              break;
            case 68: 
              i18 = localTypedArray.getDimensionPixelSize(i20, Integer.MIN_VALUE);
              if (i18 != Integer.MIN_VALUE) {
                i20 = 1;
              } else {
                i20 = 0;
              }
              i3 = i20;
              i21 = i8;
              i20 = i7;
              i22 = k;
              break;
            case 67: 
              this.mPrivateFlags2 &= 0xFFFFFFC3;
              i20 = localTypedArray.getInt(i20, -1);
              if (i20 != -1) {
                i20 = LAYOUT_DIRECTION_FLAGS[i20];
              } else {
                i20 = 2;
              }
              this.mPrivateFlags2 |= i20 << 2;
              break;
            case 66: 
              this.mPrivateFlags2 &= 0xFFFF1FFF;
              i20 = localTypedArray.getInt(i20, 1);
              this.mPrivateFlags2 |= PFLAG2_TEXT_ALIGNMENT_FLAGS[i20];
              break;
            case 65: 
              this.mPrivateFlags2 &= 0xFE3F;
              i20 = localTypedArray.getInt(i20, -1);
              if (i20 == -1) {
                break label3383;
              }
              this.mPrivateFlags2 |= PFLAG2_TEXT_DIRECTION_FLAGS[i20];
              break;
            case 64: 
              setImportantForAccessibility(localTypedArray.getInt(i20, 0));
              break;
            case 63: 
              break;
            case 62: 
              setLayerType(localTypedArray.getInt(i20, 0), null);
              break;
            case 61: 
              this.mNextFocusForwardId = localTypedArray.getResourceId(i20, -1);
              break;
            case 60: 
              this.mVerticalScrollbarPosition = localTypedArray.getInt(i20, 0);
              break;
            case 59: 
              f7 = localTypedArray.getFloat(i20, 0.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 58: 
              f6 = localTypedArray.getFloat(i20, 0.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 57: 
              f5 = localTypedArray.getFloat(i20, 0.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 56: 
              f9 = localTypedArray.getFloat(i20, 1.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 55: 
              f8 = localTypedArray.getFloat(i20, 1.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 54: 
              f2 = localTypedArray.getDimension(i20, 0.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 53: 
              f1 = localTypedArray.getDimension(i20, 0.0F);
              i22 = 1;
              i21 = i8;
              i20 = i7;
              break;
            case 52: 
              setPivotY(localTypedArray.getDimension(i20, 0.0F));
              break;
            case 51: 
              setPivotX(localTypedArray.getDimension(i20, 0.0F));
              break;
            case 50: 
              setAlpha(localTypedArray.getFloat(i20, 1.0F));
              break;
            case 49: 
              if (!localTypedArray.getBoolean(i20, false)) {
                break label3383;
              }
              i20 = i7 | 0x400;
              i21 = i8 | 0x400;
              i22 = k;
              break;
            case 48: 
              n = localTypedArray.getInt(i20, 1);
              i21 = i8;
              i20 = i7;
              i22 = k;
            }
            break;
          case 44: 
            setContentDescription(localTypedArray.getString(i20));
            break;
          case 43: 
            if (!paramContext.isRestricted())
            {
              localObject2 = localTypedArray.getString(i20);
              if (localObject2 == null) {
                break label3383;
              }
              setOnClickListener(new DeclaredOnClickListener(this, (String)localObject2));
              break label3383;
            }
            throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
          case 42: 
            this.mHapticEnabledExplicitly = localTypedArray.getBoolean(i20, false);
            if (localTypedArray.getBoolean(i20, true)) {
              break label3383;
            }
            i20 = 0xEFFFFFFF & i7;
            i21 = 0x10000000 | i8;
            i22 = k;
            break;
          case 41: 
            if (localTypedArray.getBoolean(i20, false)) {
              setScrollContainer(true);
            }
            i13 = 1;
            i21 = i8;
            i20 = i7;
            i22 = k;
            break;
          case 40: 
            if (!localTypedArray.getBoolean(i20, false)) {
              break label3383;
            }
            i20 = 0x4000000 | i7;
            i21 = 0x4000000 | i8;
            i22 = k;
            break;
          case 39: 
            if (localTypedArray.getBoolean(i20, true)) {
              break label3383;
            }
            i20 = 0xF7FFFFFF & i7;
            i21 = 0x8000000 | i8;
            i22 = k;
            break;
          case 38: 
            if ((i5 < 23) && (!(this instanceof FrameLayout))) {
              break label3383;
            }
            setForegroundGravity(localTypedArray.getInt(i20, 0));
            break;
          case 37: 
            this.mMinHeight = localTypedArray.getDimensionPixelSize(i20, 0);
            break;
          case 36: 
            this.mMinWidth = localTypedArray.getDimensionPixelSize(i20, 0);
            break;
          case 35: 
            if ((i5 < 23) && (!(this instanceof FrameLayout))) {
              break label3383;
            }
            setForeground(localTypedArray.getDrawable(i20));
            break;
          case 34: 
            if (!localTypedArray.getBoolean(i20, false)) {
              break label3383;
            }
            i20 = 0x400000 | i7;
            i21 = 0x400000 | i8;
            i22 = k;
            break;
          case 33: 
            i20 = localTypedArray.getInt(i20, 0);
            if (i20 == 0) {
              break label3383;
            }
            i20 = DRAWING_CACHE_QUALITY_FLAGS[i20] | i7;
            i21 = 0x180000 | i8;
            i22 = k;
            break;
          case 32: 
            if (localTypedArray.getBoolean(i20, true)) {
              break label3383;
            }
            i21 = 0x10000 | i8;
            i20 = i7 | 0x10000;
            i22 = k;
            break;
          case 31: 
            if (!localTypedArray.getBoolean(i20, false)) {
              break label3383;
            }
            i21 = 0x200000 | i8;
            i20 = i7 | 0x200000;
            i22 = k;
            break;
          case 30: 
            if (!localTypedArray.getBoolean(i20, false)) {
              break label3383;
            }
            i20 = i7 | 0x4000;
            i21 = i8 | 0x4000;
            i22 = k;
            break;
          case 29: 
            this.mNextFocusDownId = localTypedArray.getResourceId(i20, -1);
            break;
          case 28: 
            this.mNextFocusUpId = localTypedArray.getResourceId(i20, -1);
            break;
          case 27: 
            this.mNextFocusRightId = localTypedArray.getResourceId(i20, -1);
            break;
          case 26: 
            this.mNextFocusLeftId = localTypedArray.getResourceId(i20, -1);
          }
          break;
        case 24: 
          if (i5 >= 14) {
            break label3383;
          }
          i20 = localTypedArray.getInt(i20, 0);
          if (i20 == 0) {
            break label3383;
          }
          initializeFadingEdgeInternal(localTypedArray);
          i20 = i7 | i20;
          i21 = i8 | 0x3000;
          i22 = k;
          break;
        case 23: 
          i20 = localTypedArray.getInt(i20, 0);
          if (i20 == 0) {
            break label3383;
          }
          i20 = i7 | i20;
          i21 = i8 | 0x300;
          i1 = 1;
          i22 = k;
          break;
        case 22: 
          if (!localTypedArray.getBoolean(i20, false)) {
            break label3383;
          }
          i20 = i7 | 0x2;
          i21 = i8 | 0x2;
          i22 = k;
          break;
        case 21: 
          i20 = localTypedArray.getInt(i20, 0);
          if (i20 == 0) {
            break label3383;
          }
          i20 = VISIBILITY_FLAGS[i20] | i7;
          i21 = i8 | 0xC;
          i22 = k;
          break;
        case 20: 
          if (!localTypedArray.getBoolean(i20, false)) {
            break label3383;
          }
          i20 = i7 & 0xFFFFFFEF | 0x40001;
          i21 = 0x40011 | i8;
          i22 = k;
          break;
        case 19: 
          i20 = i7 & 0xFFFFFFEE | getFocusableAttribute(localTypedArray);
          if ((i20 & 0x10) == 0)
          {
            i21 = i8 | 0x11;
            i22 = k;
          }
          else
          {
            i21 = i8;
            i22 = k;
          }
          break;
        case 18: 
          i17 = localTypedArray.getDimensionPixelSize(i20, -1);
          i21 = i8;
          i20 = i7;
          i22 = k;
          break;
        case 17: 
          i16 = localTypedArray.getDimensionPixelSize(i20, -1);
          this.mUserPaddingRightInitial = i16;
          bool2 = true;
          i21 = i8;
          i20 = i7;
          i22 = k;
          break;
        case 16: 
          i15 = localTypedArray.getDimensionPixelSize(i20, -1);
          i21 = i8;
          i20 = i7;
          i22 = k;
          break;
        case 15: 
          i9 = localTypedArray.getDimensionPixelSize(i20, -1);
          this.mUserPaddingLeftInitial = i9;
          bool1 = true;
          i21 = i8;
          i20 = i7;
          i22 = k;
          break;
        case 14: 
          i19 = localTypedArray.getDimensionPixelSize(i20, -1);
          this.mUserPaddingLeftInitial = i19;
          this.mUserPaddingRightInitial = i19;
          bool2 = true;
          bool1 = true;
          i21 = i8;
          i20 = i7;
          i22 = k;
          break;
        case 13: 
          localDrawable = localTypedArray.getDrawable(i20);
          i21 = i8;
          i20 = i7;
          i22 = k;
          break;
        case 12: 
          j = localTypedArray.getDimensionPixelOffset(i20, 0);
          i21 = i8;
          i20 = i7;
          i22 = k;
          break;
        case 11: 
          i14 = localTypedArray.getDimensionPixelOffset(i20, 0);
          i21 = i8;
          i20 = i7;
          i22 = k;
          break;
        case 10: 
          this.mTag = localTypedArray.getText(i20);
          break;
        case 9: 
          this.mID = localTypedArray.getResourceId(i20, -1);
          break;
        }
        i20 = localTypedArray.getInt(i20, 0);
        if (i20 != 0)
        {
          m = i20;
          i21 = 0x3000000 | i8;
          i20 = i20 & 0x3000000 | i7;
          i22 = k;
        }
        else
        {
          m = i20;
          i21 = i8;
          i20 = i7;
          i22 = k;
        }
      }
      else
      {
        if ((i5 >= 23) || ((this instanceof FrameLayout)))
        {
          if (this.mForegroundInfo == null) {
            this.mForegroundInfo = new ForegroundInfo(null);
          }
          localObject2 = this.mForegroundInfo;
          ForegroundInfo.access$102((ForegroundInfo)localObject2, localTypedArray.getBoolean(i20, ((ForegroundInfo)localObject2).mInsidePadding));
        }
        label3383:
        i22 = k;
        i20 = i7;
        i21 = i8;
      }
      i12++;
      i8 = i21;
      i7 = i20;
      k = i22;
    }
    ForceDarkHelper.getInstance().updateForceDarkForView(this);
    setOverScrollMode(n);
    this.mUserPaddingStart = i18;
    this.mUserPaddingEnd = i;
    if (localDrawable != null) {
      setBackground(localDrawable);
    }
    this.mLeftPaddingDefined = bool1;
    this.mRightPaddingDefined = bool2;
    if (i19 >= 0)
    {
      i22 = i19;
      i20 = i19;
      i10 = i19;
      this.mUserPaddingLeftInitial = i19;
      this.mUserPaddingRightInitial = i19;
      i21 = i19;
      i19 = i10;
    }
    else
    {
      if (i10 >= 0)
      {
        i19 = i10;
        i20 = i10;
        this.mUserPaddingLeftInitial = i20;
        this.mUserPaddingRightInitial = i20;
        i22 = i10;
      }
      else
      {
        i22 = i9;
        i19 = i16;
      }
      if (i11 >= 0)
      {
        i20 = i11;
        i21 = i19;
        i19 = i11;
      }
      else
      {
        i21 = i19;
        i19 = i17;
        i20 = i15;
      }
    }
    if (isRtlCompatibilityMode())
    {
      i10 = i22;
      if (!this.mLeftPaddingDefined)
      {
        i10 = i22;
        if (i3 != 0) {
          i10 = i18;
        }
      }
      if (i10 >= 0) {
        i22 = i10;
      } else {
        i22 = this.mUserPaddingLeftInitial;
      }
      this.mUserPaddingLeftInitial = i22;
      i22 = i21;
      if (!this.mRightPaddingDefined)
      {
        i22 = i21;
        if (i4 != 0) {
          i22 = i;
        }
      }
      if (i22 < 0) {
        i22 = this.mUserPaddingRightInitial;
      }
      this.mUserPaddingRightInitial = i22;
      i22 = i10;
    }
    else
    {
      if ((i3 == 0) && (i4 == 0)) {
        i10 = 0;
      } else {
        i10 = 1;
      }
      if ((this.mLeftPaddingDefined) && (i10 == 0)) {
        this.mUserPaddingLeftInitial = i22;
      }
      if ((this.mRightPaddingDefined) && (i10 == 0)) {
        this.mUserPaddingRightInitial = i21;
      }
    }
    int i21 = this.mUserPaddingLeftInitial;
    if (i20 < 0) {
      i20 = this.mPaddingTop;
    }
    int i22 = this.mUserPaddingRightInitial;
    if (i19 < 0) {
      i19 = this.mPaddingBottom;
    }
    internalSetPadding(i21, i20, i22, i19);
    if (i8 != 0) {
      setFlags(i7, i8);
    }
    if (i1 != 0) {
      initializeScrollbarsInternal(localTypedArray);
    }
    if (i2 != 0) {
      initializeScrollIndicatorsInternal();
    }
    localTypedArray.recycle();
    if (m != 0) {
      recomputePadding();
    }
    if ((i14 == 0) && (j == 0)) {
      break label3860;
    }
    scrollTo(i14, j);
    label3860:
    if (k != 0)
    {
      setTranslationX(f1);
      setTranslationY(f2);
      setTranslationZ(f3);
      setElevation(f4);
      setRotation(f5);
      setRotationX(f6);
      setRotationY(f7);
      setScaleX(f8);
      setScaleY(f9);
    }
    if ((i13 == 0) && ((i7 & 0x200) != 0)) {
      setScrollContainer(true);
    }
    computeOpaqueFlags();
    if (Android_View_View.Extension.get().getExtension() != null) {
      ((Android_View_View.Interface)Android_View_View.Extension.get().getExtension().asInterface()).init(this, paramContext, paramAttributeSet, paramInt1, paramInt2);
    }
  }
  
  private void applyBackgroundTint()
  {
    if ((this.mBackground != null) && (this.mBackgroundTint != null))
    {
      TintInfo localTintInfo = this.mBackgroundTint;
      if ((localTintInfo.mHasTintList) || (localTintInfo.mHasTintMode))
      {
        this.mBackground = this.mBackground.mutate();
        if (localTintInfo.mHasTintList) {
          this.mBackground.setTintList(localTintInfo.mTintList);
        }
        if (localTintInfo.mHasTintMode) {
          this.mBackground.setTintBlendMode(localTintInfo.mBlendMode);
        }
        if (this.mBackground.isStateful()) {
          this.mBackground.setState(getDrawableState());
        }
      }
    }
  }
  
  private void applyForegroundTint()
  {
    Object localObject = this.mForegroundInfo;
    if ((localObject != null) && (((ForegroundInfo)localObject).mDrawable != null) && (this.mForegroundInfo.mTintInfo != null))
    {
      localObject = this.mForegroundInfo.mTintInfo;
      if ((((TintInfo)localObject).mHasTintList) || (((TintInfo)localObject).mHasTintMode))
      {
        ForegroundInfo localForegroundInfo = this.mForegroundInfo;
        ForegroundInfo.access$1402(localForegroundInfo, localForegroundInfo.mDrawable.mutate());
        if (((TintInfo)localObject).mHasTintList) {
          this.mForegroundInfo.mDrawable.setTintList(((TintInfo)localObject).mTintList);
        }
        if (((TintInfo)localObject).mHasTintMode) {
          this.mForegroundInfo.mDrawable.setTintBlendMode(((TintInfo)localObject).mBlendMode);
        }
        if (this.mForegroundInfo.mDrawable.isStateful()) {
          this.mForegroundInfo.mDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private boolean applyLegacyAnimation(ViewGroup paramViewGroup, long paramLong, Animation paramAnimation, boolean paramBoolean)
  {
    int i = paramViewGroup.mGroupFlags;
    if (!paramAnimation.isInitialized())
    {
      paramAnimation.initialize(this.mRight - this.mLeft, this.mBottom - this.mTop, paramViewGroup.getWidth(), paramViewGroup.getHeight());
      paramAnimation.initializeInvalidateRegion(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
      localObject = this.mAttachInfo;
      if (localObject != null) {
        paramAnimation.setListenerHandler(((AttachInfo)localObject).mHandler);
      }
      onAnimationStart();
    }
    Object localObject = paramViewGroup.getChildTransformation();
    boolean bool = paramAnimation.getTransformation(paramLong, (Transformation)localObject, 1.0F);
    if ((paramBoolean) && (this.mAttachInfo.mApplicationScale != 1.0F))
    {
      if (paramViewGroup.mInvalidationTransformation == null) {
        paramViewGroup.mInvalidationTransformation = new Transformation();
      }
      localObject = paramViewGroup.mInvalidationTransformation;
      paramAnimation.getTransformation(paramLong, (Transformation)localObject, 1.0F);
    }
    if (bool) {
      if (!paramAnimation.willChangeBounds())
      {
        if ((i & 0x90) == 128)
        {
          paramViewGroup.mGroupFlags |= 0x4;
        }
        else if ((i & 0x4) == 0)
        {
          paramViewGroup.mPrivateFlags |= 0x40;
          paramViewGroup.invalidate(this.mLeft, this.mTop, this.mRight, this.mBottom);
        }
      }
      else
      {
        if (paramViewGroup.mInvalidateRegion == null) {
          paramViewGroup.mInvalidateRegion = new RectF();
        }
        RectF localRectF = paramViewGroup.mInvalidateRegion;
        paramAnimation.getInvalidateRegion(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop, localRectF, (Transformation)localObject);
        paramViewGroup.mPrivateFlags |= 0x40;
        i = this.mLeft + (int)localRectF.left;
        int j = this.mTop + (int)localRectF.top;
        paramViewGroup.invalidate(i, j, (int)(localRectF.width() + 0.5F) + i, (int)(localRectF.height() + 0.5F) + j);
      }
    }
    return bool;
  }
  
  private void buildDrawingCacheImpl(boolean paramBoolean)
  {
    this.mCachingFailed = false;
    int i = this.mRight - this.mLeft;
    int j = this.mBottom - this.mTop;
    AttachInfo localAttachInfo = this.mAttachInfo;
    int k;
    if ((localAttachInfo != null) && (localAttachInfo.mScalingRequired)) {
      k = 1;
    } else {
      k = 0;
    }
    int m = i;
    int n = j;
    if (paramBoolean)
    {
      m = i;
      n = j;
      if (k != 0)
      {
        m = (int)(i * localAttachInfo.mApplicationScale + 0.5F);
        n = (int)(j * localAttachInfo.mApplicationScale + 0.5F);
      }
    }
    int i1 = this.mDrawingCacheBackgroundColor;
    if ((i1 == 0) && (!isOpaque())) {
      j = 0;
    } else {
      j = 1;
    }
    if ((localAttachInfo != null) && (localAttachInfo.mUse32BitDrawingCache)) {
      i = 1;
    } else {
      i = 0;
    }
    int i2;
    if ((j != 0) && (i == 0)) {
      i2 = 2;
    } else {
      i2 = 4;
    }
    long l1 = m * n * i2;
    long l2 = ViewConfiguration.get(this.mContext).getScaledMaximumDrawingCacheSize();
    if ((m > 0) && (n > 0) && (l1 <= l2))
    {
      i2 = 1;
      Bitmap localBitmap;
      if (paramBoolean) {
        localBitmap = this.mDrawingCache;
      } else {
        localBitmap = this.mUnscaledDrawingCache;
      }
      Object localObject2;
      if ((localBitmap != null) && (localBitmap.getWidth() == m))
      {
        localObject2 = localBitmap;
        if (localBitmap.getHeight() == n) {}
      }
      else
      {
        if (j == 0)
        {
          i2 = this.mViewFlags;
          localObject2 = Bitmap.Config.ARGB_8888;
        }
        else if (i != 0)
        {
          localObject2 = Bitmap.Config.ARGB_8888;
        }
        else
        {
          localObject2 = Bitmap.Config.RGB_565;
        }
        if (localBitmap != null) {
          localBitmap.recycle();
        }
      }
      try
      {
        localObject2 = Bitmap.createBitmap(this.mResources.getDisplayMetrics(), m, n, (Bitmap.Config)localObject2);
        ((Bitmap)localObject2).setDensity(getResources().getDisplayMetrics().densityDpi);
        if (paramBoolean) {
          try
          {
            this.mDrawingCache = ((Bitmap)localObject2);
          }
          catch (OutOfMemoryError localOutOfMemoryError1)
          {
            break label681;
          }
        } else {
          this.mUnscaledDrawingCache = ((Bitmap)localObject2);
        }
        if ((j != 0) && (i != 0)) {
          ((Bitmap)localObject2).setHasAlpha(false);
        }
        n = 0;
        if (i1 != 0) {
          n = 1;
        }
        i2 = n;
        Object localObject1;
        if (localAttachInfo != null)
        {
          Canvas localCanvas = localAttachInfo.mCanvas;
          localObject1 = localCanvas;
          if (localCanvas == null) {
            localObject1 = new Canvas();
          }
          ((Canvas)localObject1).setBitmap((Bitmap)localObject2);
          localAttachInfo.mCanvas = null;
        }
        else
        {
          localObject1 = new Canvas((Bitmap)localObject2);
        }
        if (i2 != 0) {
          ((Bitmap)localObject2).eraseColor(i1);
        }
        computeScroll();
        n = ((Canvas)localObject1).save();
        if ((paramBoolean) && (k != 0))
        {
          float f = localAttachInfo.mApplicationScale;
          ((Canvas)localObject1).scale(f, f);
        }
        ((Canvas)localObject1).translate(-this.mScrollX, -this.mScrollY);
        this.mPrivateFlags |= 0x20;
        localObject2 = this.mAttachInfo;
        if ((localObject2 == null) || (!((AttachInfo)localObject2).mHardwareAccelerated) || (this.mLayerType != 0)) {
          this.mPrivateFlags |= 0x8000;
        }
        k = this.mPrivateFlags;
        if ((k & 0x80) == 128)
        {
          this.mPrivateFlags = (k & 0xFFDFFFFF);
          dispatchDraw((Canvas)localObject1);
          drawAutofilledHighlight((Canvas)localObject1);
          localObject2 = this.mOverlay;
          if ((localObject2 != null) && (!((ViewOverlay)localObject2).isEmpty())) {
            this.mOverlay.getOverlayView().draw((Canvas)localObject1);
          }
        }
        else
        {
          draw((Canvas)localObject1);
        }
        ((Canvas)localObject1).restoreToCount(n);
        ((Canvas)localObject1).setBitmap(null);
        if (localAttachInfo != null) {
          localAttachInfo.mCanvas = ((Canvas)localObject1);
        }
        return;
      }
      catch (OutOfMemoryError localOutOfMemoryError2)
      {
        label681:
        if (paramBoolean) {
          this.mDrawingCache = null;
        } else {
          this.mUnscaledDrawingCache = null;
        }
        this.mCachingFailed = true;
        return;
      }
    }
    if ((m > 0) && (n > 0))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(getClass().getSimpleName());
      localStringBuilder.append(" not displayed because it is too large to fit into a software layer (or drawing cache), needs ");
      localStringBuilder.append(l1);
      localStringBuilder.append(" bytes, only ");
      localStringBuilder.append(l2);
      localStringBuilder.append(" available");
      Log.w("View", localStringBuilder.toString());
    }
    destroyDrawingCache();
    this.mCachingFailed = true;
  }
  
  private boolean canTakeFocus()
  {
    int i = this.mViewFlags;
    boolean bool = true;
    if (((i & 0xC) != 0) || ((i & 0x1) != 1) || ((i & 0x20) != 0) || ((sCanFocusZeroSized) || (!isLayoutValid()) || (!hasSize()))) {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  private void cancel(SendViewScrolledAccessibilityEvent paramSendViewScrolledAccessibilityEvent)
  {
    if ((paramSendViewScrolledAccessibilityEvent != null) && (paramSendViewScrolledAccessibilityEvent.mIsPending))
    {
      removeCallbacks(paramSendViewScrolledAccessibilityEvent);
      paramSendViewScrolledAccessibilityEvent.reset();
      return;
    }
  }
  
  private void checkForLongClick(long paramLong, float paramFloat1, float paramFloat2, int paramInt)
  {
    int i = this.mViewFlags;
    if (((i & 0x200000) == 2097152) || ((i & 0x40000000) == 1073741824))
    {
      this.mHasPerformedLongPress = false;
      if (this.mPendingCheckForLongPress == null) {
        this.mPendingCheckForLongPress = new CheckForLongPress(null);
      }
      this.mPendingCheckForLongPress.setAnchor(paramFloat1, paramFloat2);
      this.mPendingCheckForLongPress.rememberWindowAttachCount();
      this.mPendingCheckForLongPress.rememberPressedState();
      this.mPendingCheckForLongPress.setClassification(paramInt);
      postDelayed(this.mPendingCheckForLongPress, paramLong);
    }
  }
  
  private void cleanupDraw()
  {
    resetDisplayList();
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      localAttachInfo.mViewRootImpl.cancelInvalidate(this);
    }
  }
  
  public static int combineMeasuredStates(int paramInt1, int paramInt2)
  {
    return paramInt1 | paramInt2;
  }
  
  private final void debugDrawFocus(Canvas paramCanvas)
  {
    if (isFocused())
    {
      int i = dipsToPixels(8);
      int j = this.mScrollX;
      int k = this.mRight + j - this.mLeft;
      int m = this.mScrollY;
      int n = this.mBottom + m - this.mTop;
      Paint localPaint = getDebugPaint();
      localPaint.setColor(DEBUG_CORNERS_COLOR);
      localPaint.setStyle(Paint.Style.FILL);
      paramCanvas.drawRect(j, m, j + i, m + i, localPaint);
      paramCanvas.drawRect(k - i, m, k, m + i, localPaint);
      paramCanvas.drawRect(j, n - i, j + i, n, localPaint);
      paramCanvas.drawRect(k - i, n - i, k, n, localPaint);
      localPaint.setStyle(Paint.Style.STROKE);
      paramCanvas.drawLine(j, m, k, n, localPaint);
      paramCanvas.drawLine(j, n, k, m, localPaint);
    }
  }
  
  protected static String debugIndent(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder((paramInt * 2 + 3) * 2);
    for (int i = 0; i < paramInt * 2 + 3; i++)
    {
      localStringBuilder.append(' ');
      localStringBuilder.append(' ');
    }
    return localStringBuilder.toString();
  }
  
  private boolean dispatchGenericMotionEventInternal(MotionEvent paramMotionEvent)
  {
    Object localObject = this.mListenerInfo;
    if ((localObject != null) && (((ListenerInfo)localObject).mOnGenericMotionListener != null) && ((this.mViewFlags & 0x20) == 0) && (((ListenerInfo)localObject).mOnGenericMotionListener.onGenericMotion(this, paramMotionEvent))) {
      return true;
    }
    if (onGenericMotionEvent(paramMotionEvent)) {
      return true;
    }
    int i = paramMotionEvent.getActionButton();
    int j = paramMotionEvent.getActionMasked();
    if (j != 11)
    {
      if ((j == 12) && (this.mInContextButtonPress) && ((i == 32) || (i == 2)))
      {
        this.mInContextButtonPress = false;
        this.mIgnoreNextUpEvent = true;
      }
    }
    else if ((isContextClickable()) && (!this.mInContextButtonPress) && (!this.mHasPerformedLongPress) && ((i == 32) || (i == 2)) && (performContextClick(paramMotionEvent.getX(), paramMotionEvent.getY())))
    {
      this.mInContextButtonPress = true;
      setPressed(true, paramMotionEvent.getX(), paramMotionEvent.getY());
      removeTapCallback();
      removeLongPressCallback();
      return true;
    }
    localObject = this.mInputEventConsistencyVerifier;
    if (localObject != null) {
      ((InputEventConsistencyVerifier)localObject).onUnhandledEvent(paramMotionEvent, 0);
    }
    return false;
  }
  
  private void dispatchProvideStructure(ViewStructure paramViewStructure, int paramInt1, int paramInt2)
  {
    if (paramInt1 == 1)
    {
      paramViewStructure.setAutofillId(getAutofillId());
      onProvideAutofillStructure(paramViewStructure, paramInt2);
      onProvideAutofillVirtualStructure(paramViewStructure, paramInt2);
    }
    else if (!isAssistBlocked())
    {
      onProvideStructure(paramViewStructure);
      onProvideVirtualStructure(paramViewStructure);
    }
    else
    {
      paramViewStructure.setClassName(getAccessibilityClassName().toString());
      paramViewStructure.setAssistBlocked(true);
    }
  }
  
  private boolean dispatchTouchExplorationHoverEvent(MotionEvent paramMotionEvent)
  {
    Object localObject = AccessibilityManager.getInstance(this.mContext);
    if ((((AccessibilityManager)localObject).isEnabled()) && (((AccessibilityManager)localObject).isTouchExplorationEnabled()))
    {
      boolean bool1 = this.mHoveringTouchDelegate;
      int i = paramMotionEvent.getActionMasked();
      int j = 0;
      boolean bool2 = false;
      boolean bool3 = false;
      localObject = this.mTouchDelegate.getTouchDelegateInfo();
      for (int k = 0; k < ((AccessibilityNodeInfo.TouchDelegateInfo)localObject).getRegionCount(); k++) {
        if (((AccessibilityNodeInfo.TouchDelegateInfo)localObject).getRegionAt(k).contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) {
          j = 1;
        }
      }
      if (!bool1)
      {
        if (((i == 9) || (i == 7)) && (!pointInHoveredChild(paramMotionEvent)) && (j != 0)) {
          this.mHoveringTouchDelegate = true;
        }
      }
      else if ((i == 10) || ((i == 7) && ((pointInHoveredChild(paramMotionEvent)) || (j == 0)))) {
        this.mHoveringTouchDelegate = false;
      }
      boolean bool4;
      if (i != 7)
      {
        if (i != 9)
        {
          if (i != 10)
          {
            bool4 = bool2;
          }
          else
          {
            bool4 = bool2;
            if (bool1)
            {
              this.mTouchDelegate.onTouchExplorationHoverEvent(paramMotionEvent);
              bool4 = bool2;
            }
          }
        }
        else
        {
          bool4 = bool2;
          if (!bool1)
          {
            bool4 = bool2;
            if (this.mHoveringTouchDelegate) {
              bool4 = this.mTouchDelegate.onTouchExplorationHoverEvent(paramMotionEvent);
            }
          }
        }
      }
      else if ((bool1) && (this.mHoveringTouchDelegate))
      {
        bool4 = this.mTouchDelegate.onTouchExplorationHoverEvent(paramMotionEvent);
      }
      else
      {
        if ((!bool1) && (this.mHoveringTouchDelegate))
        {
          if (paramMotionEvent.getHistorySize() != 0) {
            paramMotionEvent = MotionEvent.obtainNoHistory(paramMotionEvent);
          }
          paramMotionEvent.setAction(9);
          bool4 = this.mTouchDelegate.onTouchExplorationHoverEvent(paramMotionEvent);
          paramMotionEvent.setAction(i);
          bool4 |= this.mTouchDelegate.onTouchExplorationHoverEvent(paramMotionEvent);
        }
        for (;;)
        {
          break;
          bool4 = bool3;
          if (bool1)
          {
            bool4 = bool3;
            if (!this.mHoveringTouchDelegate)
            {
              bool4 = paramMotionEvent.isHoverExitPending();
              paramMotionEvent.setHoverExitPending(true);
              this.mTouchDelegate.onTouchExplorationHoverEvent(paramMotionEvent);
              if (paramMotionEvent.getHistorySize() != 0) {
                paramMotionEvent = MotionEvent.obtainNoHistory(paramMotionEvent);
              }
              paramMotionEvent.setHoverExitPending(bool4);
              paramMotionEvent.setAction(10);
              this.mTouchDelegate.onTouchExplorationHoverEvent(paramMotionEvent);
              bool4 = bool2;
            }
          }
        }
      }
      return bool4;
    }
    return false;
  }
  
  private void drawAutofilledHighlight(Canvas paramCanvas)
  {
    if (isAutofilled())
    {
      Drawable localDrawable = getAutofilledDrawable();
      if (localDrawable != null)
      {
        localDrawable.setBounds(0, 0, getWidth(), getHeight());
        localDrawable.draw(paramCanvas);
      }
    }
  }
  
  @UnsupportedAppUsage
  private void drawBackground(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mBackground;
    if (localDrawable == null) {
      return;
    }
    setBackgroundBounds();
    if (paramCanvas.isHardwareAccelerated())
    {
      Object localObject = this.mAttachInfo;
      if ((localObject != null) && (((AttachInfo)localObject).mThreadedRenderer != null))
      {
        this.mBackgroundRenderNode = getDrawableRenderNode(localDrawable, this.mBackgroundRenderNode);
        localObject = this.mBackgroundRenderNode;
        if ((localObject != null) && (((RenderNode)localObject).hasDisplayList()))
        {
          setBackgroundRenderNodeProperties((RenderNode)localObject);
          ((RecordingCanvas)paramCanvas).drawRenderNode((RenderNode)localObject);
          return;
        }
      }
    }
    int i = this.mScrollX;
    int j = this.mScrollY;
    if ((i | j) == 0)
    {
      localDrawable.draw(paramCanvas);
    }
    else
    {
      paramCanvas.translate(i, j);
      localDrawable.draw(paramCanvas);
      paramCanvas.translate(-i, -j);
    }
  }
  
  private void drawDefaultFocusHighlight(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mDefaultFocusHighlight;
    if (localDrawable != null)
    {
      if (this.mDefaultFocusHighlightSizeChanged)
      {
        this.mDefaultFocusHighlightSizeChanged = false;
        int i = this.mScrollX;
        int j = this.mRight;
        int k = this.mLeft;
        int m = this.mScrollY;
        localDrawable.setBounds(i, m, j + i - k, this.mBottom + m - this.mTop);
      }
      this.mDefaultFocusHighlight.draw(paramCanvas);
    }
  }
  
  private static void dumpFlag(HashMap<String, String> paramHashMap, String paramString, int paramInt)
  {
    String str1 = String.format("%32s", new Object[] { Integer.toBinaryString(paramInt) }).replace('0', ' ');
    paramInt = paramString.indexOf('_');
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramInt > 0) {
      str2 = paramString.substring(0, paramInt);
    } else {
      str2 = paramString;
    }
    localStringBuilder.append(str2);
    localStringBuilder.append(str1);
    localStringBuilder.append(paramString);
    String str2 = localStringBuilder.toString();
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(str1);
    localStringBuilder.append(" ");
    localStringBuilder.append(paramString);
    paramHashMap.put(str2, localStringBuilder.toString());
  }
  
  private static void dumpFlags()
  {
    HashMap localHashMap = Maps.newHashMap();
    try
    {
      for (Object localObject2 : View.class.getDeclaredFields())
      {
        int k = ((Field)localObject2).getModifiers();
        if ((Modifier.isStatic(k)) && (Modifier.isFinal(k))) {
          if (((Field)localObject2).getType().equals(Integer.TYPE))
          {
            k = ((Field)localObject2).getInt(null);
            dumpFlag(localHashMap, ((Field)localObject2).getName(), k);
          }
          else if (((Field)localObject2).getType().equals(int[].class))
          {
            int[] arrayOfInt = (int[])((Field)localObject2).get(null);
            for (k = 0; k < arrayOfInt.length; k++)
            {
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localStringBuilder.append(((Field)localObject2).getName());
              localStringBuilder.append("[");
              localStringBuilder.append(k);
              localStringBuilder.append("]");
              dumpFlag(localHashMap, localStringBuilder.toString(), arrayOfInt[k]);
            }
          }
        }
      }
      ??? = Lists.newArrayList();
      ((ArrayList)???).addAll(localHashMap.keySet());
      Collections.sort((List)???);
      ??? = ((ArrayList)???).iterator();
      while (((Iterator)???).hasNext()) {
        Log.d("View", (String)localHashMap.get((String)((Iterator)???).next()));
      }
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException(localIllegalAccessException);
    }
  }
  
  private View findAccessibilityFocusHost(boolean paramBoolean)
  {
    if (isAccessibilityFocusedViewOrHost()) {
      return this;
    }
    if (paramBoolean)
    {
      Object localObject = getViewRootImpl();
      if (localObject != null)
      {
        localObject = ((ViewRootImpl)localObject).getAccessibilityFocusedHost();
        if ((localObject != null) && (ViewRootImpl.isViewDescendantOf((View)localObject, this))) {
          return (View)localObject;
        }
      }
    }
    return null;
  }
  
  private FrameMetricsObserver findFrameMetricsObserver(Window.OnFrameMetricsAvailableListener paramOnFrameMetricsAvailableListener)
  {
    if (this.mFrameMetricsObservers != null) {
      for (int i = 0; i < this.mFrameMetricsObservers.size(); i++)
      {
        FrameMetricsObserver localFrameMetricsObserver = (FrameMetricsObserver)this.mFrameMetricsObservers.get(i);
        if (localFrameMetricsObserver.mListener == paramOnFrameMetricsAvailableListener) {
          return localFrameMetricsObserver;
        }
      }
    }
    return null;
  }
  
  private View findLabelForView(View paramView, int paramInt)
  {
    if (this.mMatchLabelForPredicate == null) {
      this.mMatchLabelForPredicate = new MatchLabelForPredicate(null);
    }
    MatchLabelForPredicate.access$1002(this.mMatchLabelForPredicate, paramInt);
    return findViewByPredicateInsideOut(paramView, this.mMatchLabelForPredicate);
  }
  
  private View findViewInsideOutShouldExist(View paramView, int paramInt)
  {
    if (this.mMatchIdPredicate == null) {
      this.mMatchIdPredicate = new MatchIdPredicate(null);
    }
    Object localObject = this.mMatchIdPredicate;
    ((MatchIdPredicate)localObject).mId = paramInt;
    localObject = paramView.findViewByPredicateInsideOut(this, (Predicate)localObject);
    if (localObject == null)
    {
      paramView = new StringBuilder();
      paramView.append("couldn't find view with id ");
      paramView.append(paramInt);
      Log.w("View", paramView.toString());
    }
    return (View)localObject;
  }
  
  private boolean fitSystemWindowsInt(Rect paramRect)
  {
    if ((this.mViewFlags & 0x2) == 2)
    {
      this.mUserPaddingStart = Integer.MIN_VALUE;
      this.mUserPaddingEnd = Integer.MIN_VALUE;
      Rect localRect1 = (Rect)sThreadLocal.get();
      Rect localRect2 = localRect1;
      if (localRect1 == null)
      {
        localRect2 = new Rect();
        sThreadLocal.set(localRect2);
      }
      boolean bool = computeFitSystemWindows(paramRect, localRect2);
      this.mUserPaddingLeftInitial = localRect2.left;
      this.mUserPaddingRightInitial = localRect2.right;
      internalSetPadding(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom);
      return bool;
    }
    return false;
  }
  
  public static int generateViewId()
  {
    for (;;)
    {
      int i = sNextGeneratedId.get();
      int j = i + 1;
      int k = j;
      if (j > 16777215) {
        k = 1;
      }
      if (sNextGeneratedId.compareAndSet(i, k)) {
        return i;
      }
    }
  }
  
  private ContentCaptureSession getAndCacheContentCaptureSession()
  {
    Object localObject = this.mContentCaptureSession;
    if (localObject != null) {
      return (ContentCaptureSession)localObject;
    }
    localObject = null;
    ViewParent localViewParent = this.mParent;
    if ((localViewParent instanceof View)) {
      localObject = ((View)localViewParent).getContentCaptureSession();
    }
    if (localObject == null)
    {
      localObject = (ContentCaptureManager)this.mContext.getSystemService(ContentCaptureManager.class);
      if (localObject == null) {
        localObject = null;
      } else {
        localObject = ((ContentCaptureManager)localObject).getMainContentCaptureSession();
      }
      return (ContentCaptureSession)localObject;
    }
    return (ContentCaptureSession)localObject;
  }
  
  private static SparseArray<String> getAttributeMap()
  {
    if (mAttributeMap == null) {
      mAttributeMap = new SparseArray();
    }
    return mAttributeMap;
  }
  
  private AutofillManager getAutofillManager()
  {
    return (AutofillManager)this.mContext.getSystemService(AutofillManager.class);
  }
  
  private Drawable getAutofilledDrawable()
  {
    Object localObject = this.mAttachInfo;
    if (localObject == null) {
      return null;
    }
    if (((AttachInfo)localObject).mAutofilledDrawable == null)
    {
      localObject = getRootView().getContext();
      TypedArray localTypedArray = ((Context)localObject).getTheme().obtainStyledAttributes(AUTOFILL_HIGHLIGHT_ATTR);
      int i = localTypedArray.getResourceId(0, 0);
      this.mAttachInfo.mAutofilledDrawable = ((Context)localObject).getDrawable(i);
      localTypedArray.recycle();
    }
    return this.mAttachInfo.mAutofilledDrawable;
  }
  
  static Paint getDebugPaint()
  {
    if (sDebugPaint == null)
    {
      sDebugPaint = new Paint();
      sDebugPaint.setAntiAlias(false);
    }
    return sDebugPaint;
  }
  
  private Drawable getDefaultFocusHighlightDrawable()
  {
    if (this.mDefaultFocusHighlightCache == null)
    {
      Object localObject = this.mContext;
      if (localObject != null)
      {
        localObject = ((Context)localObject).obtainStyledAttributes(new int[] { 16843534 });
        this.mDefaultFocusHighlightCache = ((TypedArray)localObject).getDrawable(0);
        ((TypedArray)localObject).recycle();
      }
    }
    return this.mDefaultFocusHighlightCache;
  }
  
  public static int getDefaultSize(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    int j = MeasureSpec.getMode(paramInt2);
    paramInt2 = MeasureSpec.getSize(paramInt2);
    if (j != Integer.MIN_VALUE) {
      if (j != 0)
      {
        if (j != 1073741824) {
          return i;
        }
      }
      else {
        return paramInt1;
      }
    }
    paramInt1 = paramInt2;
    return paramInt1;
  }
  
  private RenderNode getDrawableRenderNode(Drawable paramDrawable, RenderNode paramRenderNode)
  {
    RenderNode localRenderNode = paramRenderNode;
    if (paramRenderNode == null)
    {
      localRenderNode = RenderNode.create(paramDrawable.getClass().getName(), new ViewAnimationHostBridge(this));
      localRenderNode.setUsageHint(1);
    }
    Rect localRect = paramDrawable.getBounds();
    paramRenderNode = localRenderNode.beginRecording(localRect.width(), localRect.height());
    paramRenderNode.translate(-localRect.left, -localRect.top);
    try
    {
      paramDrawable.draw(paramRenderNode);
      localRenderNode.endRecording();
      localRenderNode.setLeftTopRightBottom(localRect.left, localRect.top, localRect.right, localRect.bottom);
      localRenderNode.setProjectBackwards(paramDrawable.isProjected());
      localRenderNode.setProjectionReceiver(true);
      localRenderNode.setClipToBounds(false);
      return localRenderNode;
    }
    finally
    {
      localRenderNode.endRecording();
    }
  }
  
  private float getFinalAlpha()
  {
    TransformationInfo localTransformationInfo = this.mTransformationInfo;
    if (localTransformationInfo != null) {
      return localTransformationInfo.mAlpha * this.mTransformationInfo.mTransitionAlpha;
    }
    return 1.0F;
  }
  
  private int getFocusableAttribute(TypedArray paramTypedArray)
  {
    TypedValue localTypedValue = new TypedValue();
    if (paramTypedArray.getValue(19, localTypedValue))
    {
      if (localTypedValue.type == 18)
      {
        int i;
        if (localTypedValue.data == 0) {
          i = 0;
        } else {
          i = 1;
        }
        return i;
      }
      return localTypedValue.data;
    }
    return 16;
  }
  
  private void getHorizontalScrollBarBounds(Rect paramRect1, Rect paramRect2)
  {
    if (paramRect1 == null) {
      paramRect1 = paramRect2;
    }
    if (paramRect1 == null) {
      return;
    }
    int i = this.mViewFlags;
    int j = 0;
    if ((i & 0x2000000) == 0) {
      i = -1;
    } else {
      i = 0;
    }
    int k;
    if ((isVerticalScrollBarEnabled()) && (!isVerticalScrollBarHidden())) {
      k = 1;
    } else {
      k = 0;
    }
    int m = getHorizontalScrollbarHeight();
    if (k != 0) {
      k = getVerticalScrollbarWidth();
    } else {
      k = j;
    }
    int n = this.mRight;
    int i1 = this.mLeft;
    j = this.mBottom - this.mTop;
    paramRect1.top = (this.mScrollY + j - m - (this.mUserPaddingBottom & i));
    int i2 = this.mScrollX;
    paramRect1.left = ((this.mPaddingLeft & i) + i2);
    paramRect1.right = (i2 + (n - i1) - (this.mUserPaddingRight & i) - k);
    paramRect1.bottom = (paramRect1.top + m);
    if (paramRect2 == null) {
      return;
    }
    if (paramRect2 != paramRect1) {
      paramRect2.set(paramRect1);
    }
    i = this.mScrollCache.scrollBarMinTouchTarget;
    if (paramRect2.height() < i)
    {
      k = (i - paramRect2.height()) / 2;
      paramRect2.bottom = Math.min(paramRect2.bottom + k, this.mScrollY + j);
      paramRect2.top = (paramRect2.bottom - i);
    }
    if (paramRect2.width() < i)
    {
      k = (i - paramRect2.width()) / 2;
      paramRect2.left -= k;
      paramRect2.right = (paramRect2.left + i);
    }
  }
  
  private View getProjectionReceiver()
  {
    for (ViewParent localViewParent = getParent(); (localViewParent != null) && ((localViewParent instanceof View)); localViewParent = localViewParent.getParent())
    {
      View localView = (View)localViewParent;
      if (localView.isProjectionReceiver()) {
        return localView;
      }
    }
    return null;
  }
  
  private void getRoundVerticalScrollBarBounds(Rect paramRect)
  {
    int i = this.mRight;
    int j = this.mLeft;
    int k = this.mBottom;
    int m = this.mTop;
    paramRect.left = this.mScrollX;
    paramRect.top = this.mScrollY;
    paramRect.right = (paramRect.left + (i - j));
    paramRect.bottom = (this.mScrollY + (k - m));
  }
  
  private HandlerActionQueue getRunQueue()
  {
    if (this.mRunQueue == null) {
      this.mRunQueue = new HandlerActionQueue();
    }
    return this.mRunQueue;
  }
  
  @UnsupportedAppUsage
  private ScrollabilityCache getScrollCache()
  {
    initScrollCache();
    return this.mScrollCache;
  }
  
  private void getStraightVerticalScrollBarBounds(Rect paramRect1, Rect paramRect2)
  {
    if (paramRect1 == null) {
      paramRect1 = paramRect2;
    }
    if (paramRect1 == null) {
      return;
    }
    if ((this.mViewFlags & 0x2000000) == 0) {
      i = -1;
    } else {
      i = 0;
    }
    int j = getVerticalScrollbarWidth();
    int k = this.mVerticalScrollbarPosition;
    int m = k;
    if (k == 0) {
      if (isLayoutRtl()) {
        m = 1;
      } else {
        m = 2;
      }
    }
    k = this.mRight - this.mLeft;
    int n = this.mBottom;
    int i1 = this.mTop;
    if (m != 1) {
      paramRect1.left = (this.mScrollX + k - j - (this.mUserPaddingRight & i));
    } else {
      paramRect1.left = (this.mScrollX + (this.mUserPaddingLeft & i));
    }
    paramRect1.top = (this.mScrollY + (this.mPaddingTop & i));
    paramRect1.right = (paramRect1.left + j);
    paramRect1.bottom = (this.mScrollY + (n - i1) - (this.mUserPaddingBottom & i));
    if (paramRect2 == null) {
      return;
    }
    if (paramRect2 != paramRect1) {
      paramRect2.set(paramRect1);
    }
    int i = this.mScrollCache.scrollBarMinTouchTarget;
    if (paramRect2.width() < i)
    {
      j = (i - paramRect2.width()) / 2;
      if (m == 2)
      {
        paramRect2.right = Math.min(paramRect2.right + j, this.mScrollX + k);
        paramRect2.left = (paramRect2.right - i);
      }
      else
      {
        paramRect2.left = Math.max(paramRect2.left + j, this.mScrollX);
        paramRect2.right = (paramRect2.left + i);
      }
    }
    if (paramRect2.height() < i)
    {
      m = (i - paramRect2.height()) / 2;
      paramRect2.top -= m;
      paramRect2.bottom = (paramRect2.top + i);
    }
  }
  
  private void getVerticalScrollBarBounds(Rect paramRect1, Rect paramRect2)
  {
    if (this.mRoundScrollbarRenderer == null)
    {
      getStraightVerticalScrollBarBounds(paramRect1, paramRect2);
    }
    else
    {
      if (paramRect1 == null) {
        paramRect1 = paramRect2;
      }
      getRoundVerticalScrollBarBounds(paramRect1);
    }
  }
  
  private void handleTooltipUp()
  {
    TooltipInfo localTooltipInfo = this.mTooltipInfo;
    if ((localTooltipInfo != null) && (localTooltipInfo.mTooltipPopup != null))
    {
      removeCallbacks(this.mTooltipInfo.mHideTooltipRunnable);
      postDelayed(this.mTooltipInfo.mHideTooltipRunnable, ViewConfiguration.getLongPressTooltipHideTimeout());
      return;
    }
  }
  
  private boolean hasAncestorThatBlocksDescendantFocus()
  {
    boolean bool = isFocusableInTouchMode();
    Object localObject = this.mParent;
    while ((localObject instanceof ViewGroup))
    {
      localObject = (ViewGroup)localObject;
      if ((((ViewGroup)localObject).getDescendantFocusability() != 393216) && ((bool) || (!((ViewGroup)localObject).shouldBlockFocusForTouchscreen()))) {
        localObject = ((ViewGroup)localObject).getParent();
      } else {
        return true;
      }
    }
    return false;
  }
  
  private boolean hasListenersForAccessibility()
  {
    ListenerInfo localListenerInfo = getListenerInfo();
    boolean bool;
    if ((this.mTouchDelegate == null) && (localListenerInfo.mOnKeyListener == null) && (localListenerInfo.mOnTouchListener == null) && (localListenerInfo.mOnGenericMotionListener == null) && (localListenerInfo.mOnHoverListener == null) && (localListenerInfo.mOnDragListener == null)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private boolean hasParentWantsFocus()
  {
    for (Object localObject = this.mParent; (localObject instanceof ViewGroup); localObject = ((ViewGroup)localObject).mParent)
    {
      localObject = (ViewGroup)localObject;
      if ((((ViewGroup)localObject).mPrivateFlags & 0x1) != 0) {
        return true;
      }
    }
    return false;
  }
  
  private boolean hasPendingLongPressCallback()
  {
    if (this.mPendingCheckForLongPress == null) {
      return false;
    }
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo == null) {
      return false;
    }
    return localAttachInfo.mHandler.hasCallbacks(this.mPendingCheckForLongPress);
  }
  
  @UnsupportedAppUsage
  private boolean hasRtlSupport()
  {
    return this.mContext.getApplicationInfo().hasRtlSupport();
  }
  
  private boolean hasSize()
  {
    boolean bool;
    if ((this.mBottom > this.mTop) && (this.mRight > this.mLeft)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static View inflate(Context paramContext, int paramInt, ViewGroup paramViewGroup)
  {
    return LayoutInflater.from(paramContext).inflate(paramInt, paramViewGroup);
  }
  
  public static void initForcedUseForceDark(Context paramContext)
  {
    if ((sForceUseForceDark == -1) && (paramContext != null))
    {
      int i;
      if ((paramContext.getApplicationInfo().flags & 0x1) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      String str1 = paramContext.getApplicationInfo().packageName;
      if (i == 0)
      {
        String str2 = "";
        try
        {
          paramContext = paramContext.getPackageManager().getPackageInfo(str1, 0).versionName;
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          paramContext.printStackTrace();
          paramContext = str2;
        }
        catch (RuntimeException paramContext)
        {
          for (;;)
          {
            paramContext.printStackTrace();
            paramContext = str2;
          }
        }
        str2 = (String)FORCE_DARK_WHITE_LIST.get(str1);
        if ((str2 != null) && (str2.compareTo(paramContext) <= 0)) {
          sForceUseForceDark = 0;
        } else {
          sForceUseForceDark = 1;
        }
      }
      else
      {
        sForceUseForceDark = 0;
      }
      paramContext = new StringBuilder();
      paramContext.append(str1);
      paramContext.append(" initForcedUseForceDark: ");
      paramContext.append(sForceUseForceDark);
      Log.d("View", paramContext.toString());
    }
  }
  
  private void initScrollCache()
  {
    if (this.mScrollCache == null) {
      this.mScrollCache = new ScrollabilityCache(ViewConfiguration.get(this.mContext), this);
    }
  }
  
  private boolean initialAwakenScrollBars()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    boolean bool = true;
    if ((localScrollabilityCache == null) || (!awakenScrollBars(localScrollabilityCache.scrollBarDefaultDelayBeforeFade * 4, true))) {
      bool = false;
    }
    return bool;
  }
  
  private void initializeScrollBarDrawable()
  {
    initScrollCache();
    if (this.mScrollCache.scrollBar == null)
    {
      this.mScrollCache.scrollBar = new ScrollBarDrawable();
      this.mScrollCache.scrollBar.setState(getDrawableState());
      this.mScrollCache.scrollBar.setCallback(this);
    }
  }
  
  private void initializeScrollIndicatorsInternal()
  {
    if (this.mScrollIndicatorDrawable == null) {
      this.mScrollIndicatorDrawable = this.mContext.getDrawable(17303439);
    }
  }
  
  private boolean isAccessibilityPane()
  {
    boolean bool;
    if (this.mAccessibilityPaneTitle != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isAutofillable()
  {
    int i = getAutofillType();
    boolean bool = false;
    if (i == 0) {
      return false;
    }
    if (!isImportantForAutofill())
    {
      Object localObject = this.mContext.getAutofillOptions();
      if ((localObject != null) && (((AutofillOptions)localObject).isAugmentedAutofillEnabled(this.mContext)))
      {
        localObject = getAutofillManager();
        if (localObject == null) {
          return false;
        }
        ((AutofillManager)localObject).notifyViewEnteredForAugmentedAutofill(this);
      }
      else
      {
        return false;
      }
    }
    if (getAutofillViewId() > 1073741823) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isDefaultFocusHighlightEnabled()
  {
    return sUseDefaultFocusHighlight;
  }
  
  public static boolean isForceUseForceDark()
  {
    int i = sForceUseForceDark;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isForceUseForceDark(Context paramContext)
  {
    if (sForceUseForceDark == -1) {
      initForcedUseForceDark(paramContext);
    }
    int i = sForceUseForceDark;
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    return bool;
  }
  
  private boolean isHoverable()
  {
    int i = this.mViewFlags;
    boolean bool = false;
    if ((i & 0x20) == 32) {
      return false;
    }
    if (((i & 0x4000) == 16384) || ((i & 0x200000) == 2097152) || ((i & 0x800000) == 8388608)) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isLayoutModeOptical(Object paramObject)
  {
    boolean bool;
    if (((paramObject instanceof ViewGroup)) && (((ViewGroup)paramObject).isLayoutModeOptical())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isOnHorizontalScrollbarThumb(float paramFloat1, float paramFloat2)
  {
    if ((this.mScrollCache != null) && (isHorizontalScrollBarEnabled()))
    {
      int i = computeHorizontalScrollRange();
      int j = computeHorizontalScrollExtent();
      if (i > j)
      {
        paramFloat1 += getScrollX();
        paramFloat2 += getScrollY();
        Rect localRect1 = this.mScrollCache.mScrollBarBounds;
        Rect localRect2 = this.mScrollCache.mScrollBarTouchBounds;
        getHorizontalScrollBarBounds(localRect1, localRect2);
        int k = computeHorizontalScrollOffset();
        int m = ScrollBarUtils.getThumbLength(localRect1.width(), localRect1.height(), j, i);
        k = ScrollBarUtils.getThumbOffset(localRect1.width(), m, j, i, k);
        j = localRect1.left + k;
        k = Math.max(this.mScrollCache.scrollBarMinTouchTarget - m, 0) / 2;
        if ((paramFloat1 >= j - k) && (paramFloat1 <= j + m + k) && (paramFloat2 >= localRect2.top) && (paramFloat2 <= localRect2.bottom)) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
  
  private boolean isOnVerticalScrollbarThumb(float paramFloat1, float paramFloat2)
  {
    if ((this.mScrollCache != null) && (isVerticalScrollBarEnabled()) && (!isVerticalScrollBarHidden()))
    {
      int i = computeVerticalScrollRange();
      int j = computeVerticalScrollExtent();
      if (i > j)
      {
        paramFloat1 += getScrollX();
        paramFloat2 += getScrollY();
        Rect localRect1 = this.mScrollCache.mScrollBarBounds;
        Rect localRect2 = this.mScrollCache.mScrollBarTouchBounds;
        getVerticalScrollBarBounds(localRect1, localRect2);
        int k = computeVerticalScrollOffset();
        int m = ScrollBarUtils.getThumbLength(localRect1.height(), localRect1.width(), j, i);
        i = ScrollBarUtils.getThumbOffset(localRect1.height(), m, j, i, k);
        k = localRect1.top + i;
        i = Math.max(this.mScrollCache.scrollBarMinTouchTarget - m, 0) / 2;
        if ((paramFloat1 >= localRect2.left) && (paramFloat1 <= localRect2.right) && (paramFloat2 >= k - i) && (paramFloat2 <= k + m + i)) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
  
  private boolean isProjectionReceiver()
  {
    boolean bool;
    if (this.mBackground != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isRtlCompatibilityMode()
  {
    boolean bool;
    if ((getContext().getApplicationInfo().targetSdkVersion >= 17) && (hasRtlSupport())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private static boolean isViewIdGenerated(int paramInt)
  {
    boolean bool;
    if (((0xFF000000 & paramInt) == 0) && ((0xFFFFFF & paramInt) != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected static int[] mergeDrawableStates(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    for (int i = paramArrayOfInt1.length - 1; (i >= 0) && (paramArrayOfInt1[i] == 0); i--) {}
    System.arraycopy(paramArrayOfInt2, 0, paramArrayOfInt1, i + 1, paramArrayOfInt2.length);
    return paramArrayOfInt1;
  }
  
  private boolean needRtlPropertiesResolution()
  {
    boolean bool;
    if ((this.mPrivateFlags2 & 0x60010220) != 1610678816) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  /* Error */
  private void notifyAutofillManagerOnClick()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 2078	android/view/View:mPrivateFlags	I
    //   4: ldc_w 417
    //   7: iand
    //   8: ifeq +41 -> 49
    //   11: aload_0
    //   12: invokespecial 2970	android/view/View:getAutofillManager	()Landroid/view/autofill/AutofillManager;
    //   15: aload_0
    //   16: invokevirtual 3052	android/view/autofill/AutofillManager:notifyViewClicked	(Landroid/view/View;)V
    //   19: aload_0
    //   20: ldc_w 3053
    //   23: aload_0
    //   24: getfield 2078	android/view/View:mPrivateFlags	I
    //   27: iand
    //   28: putfield 2078	android/view/View:mPrivateFlags	I
    //   31: goto +18 -> 49
    //   34: astore_1
    //   35: aload_0
    //   36: ldc_w 3053
    //   39: aload_0
    //   40: getfield 2078	android/view/View:mPrivateFlags	I
    //   43: iand
    //   44: putfield 2078	android/view/View:mPrivateFlags	I
    //   47: aload_1
    //   48: athrow
    //   49: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	View
    //   34	14	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	19	34	finally
  }
  
  private void notifyFocusChangeToInputMethodManager(boolean paramBoolean)
  {
    InputMethodManager localInputMethodManager = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
    if (localInputMethodManager == null) {
      return;
    }
    if (paramBoolean) {
      localInputMethodManager.focusIn(this);
    } else {
      localInputMethodManager.focusOut(this);
    }
  }
  
  private boolean notifyWebView(View paramView, boolean paramBoolean)
  {
    if (paramView != null)
    {
      paramView = paramView.getAttachedActivityInstance();
      if (paramView != null) {
        paramView = paramView.getInterceptor();
      } else {
        paramView = null;
      }
      if (paramView != null)
      {
        paramView.notifyWebView(this, paramBoolean);
        return true;
      }
    }
    return false;
  }
  
  private static int numViewsForAccessibility(View paramView)
  {
    if (paramView != null)
    {
      if (paramView.includeForAccessibility()) {
        return 1;
      }
      if ((paramView instanceof ViewGroup)) {
        return ((ViewGroup)paramView).getNumChildrenForAccessibility();
      }
    }
    return 0;
  }
  
  private void onDrawScrollIndicators(Canvas paramCanvas)
  {
    if ((this.mPrivateFlags3 & 0x3F00) == 0) {
      return;
    }
    Drawable localDrawable = this.mScrollIndicatorDrawable;
    if (localDrawable == null) {
      return;
    }
    int i = localDrawable.getIntrinsicHeight();
    int j = localDrawable.getIntrinsicWidth();
    Rect localRect = this.mAttachInfo.mTmpInvalRect;
    getScrollIndicatorBounds(localRect);
    if (((this.mPrivateFlags3 & 0x100) != 0) && (canScrollVertically(-1)))
    {
      localDrawable.setBounds(localRect.left, localRect.top, localRect.right, localRect.top + i);
      localDrawable.draw(paramCanvas);
    }
    if (((this.mPrivateFlags3 & 0x200) != 0) && (canScrollVertically(1)))
    {
      localDrawable.setBounds(localRect.left, localRect.bottom - i, localRect.right, localRect.bottom);
      localDrawable.draw(paramCanvas);
    }
    int k;
    if (getLayoutDirection() == 1)
    {
      i = 8192;
      k = 4096;
    }
    else
    {
      i = 4096;
      k = 8192;
    }
    if (((this.mPrivateFlags3 & (i | 0x400)) != 0) && (canScrollHorizontally(-1)))
    {
      localDrawable.setBounds(localRect.left, localRect.top, localRect.left + j, localRect.bottom);
      localDrawable.draw(paramCanvas);
    }
    if (((this.mPrivateFlags3 & (k | 0x800)) != 0) && (canScrollHorizontally(1)))
    {
      localDrawable.setBounds(localRect.right - j, localRect.top, localRect.right, localRect.bottom);
      localDrawable.draw(paramCanvas);
    }
  }
  
  private void onProvideVirtualStructureCompat(ViewStructure paramViewStructure, boolean paramBoolean)
  {
    AccessibilityNodeProvider localAccessibilityNodeProvider = getAccessibilityNodeProvider();
    if (localAccessibilityNodeProvider != null)
    {
      if ((paramBoolean) && (Log.isLoggable("View.Autofill", 2)))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("onProvideVirtualStructureCompat() for ");
        ((StringBuilder)localObject).append(this);
        Log.v("View.Autofill", ((StringBuilder)localObject).toString());
      }
      Object localObject = createAccessibilityNodeInfo();
      paramViewStructure.setChildCount(1);
      populateVirtualStructure(paramViewStructure.newChild(0), localAccessibilityNodeProvider, (AccessibilityNodeInfo)localObject, paramBoolean);
      ((AccessibilityNodeInfo)localObject).recycle();
    }
  }
  
  private boolean performClickInternal()
  {
    notifyAutofillManagerOnClick();
    return performClick();
  }
  
  private boolean performLongClickInternal(float paramFloat1, float paramFloat2)
  {
    sendAccessibilityEvent(2);
    boolean bool1 = false;
    ListenerInfo localListenerInfo = this.mListenerInfo;
    boolean bool2 = bool1;
    if (localListenerInfo != null)
    {
      bool2 = bool1;
      if (localListenerInfo.mOnLongClickListener != null) {
        bool2 = localListenerInfo.mOnLongClickListener.onLongClick(this);
      }
    }
    bool1 = bool2;
    if (!bool2)
    {
      int i;
      if ((!Float.isNaN(paramFloat1)) && (!Float.isNaN(paramFloat2))) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0) {
        bool1 = showContextMenu(paramFloat1, paramFloat2);
      } else {
        bool1 = showContextMenu();
      }
    }
    bool2 = bool1;
    if ((this.mViewFlags & 0x40000000) == 1073741824)
    {
      bool2 = bool1;
      if (!bool1) {
        bool2 = showLongClickTooltip((int)paramFloat1, (int)paramFloat2);
      }
    }
    if (bool2) {
      performHapticFeedback(0);
    }
    return bool2;
  }
  
  private void populateAccessibilityNodeInfoDrawingOrderInParent(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    if ((this.mPrivateFlags & 0x10) == 0)
    {
      paramAccessibilityNodeInfo.setDrawingOrder(0);
      return;
    }
    int i = 1;
    View localView = this;
    ViewParent localViewParent1 = getParentForAccessibility();
    int j;
    for (;;)
    {
      j = i;
      if (localView == localViewParent1) {
        break;
      }
      ViewParent localViewParent2 = localView.getParent();
      if (!(localViewParent2 instanceof ViewGroup))
      {
        j = 0;
        break;
      }
      ViewGroup localViewGroup = (ViewGroup)localViewParent2;
      int k = localViewGroup.getChildCount();
      int m = i;
      if (k > 1)
      {
        ArrayList localArrayList = localViewGroup.buildOrderedChildList();
        int n;
        if (localArrayList != null)
        {
          n = localArrayList.indexOf(localView);
          for (j = 0; j < n; j++) {
            i += numViewsForAccessibility((View)localArrayList.get(j));
          }
          m = i;
        }
        else
        {
          j = localViewGroup.indexOfChild(localView);
          boolean bool = localViewGroup.isChildrenDrawingOrderEnabled();
          if ((j >= 0) && (bool)) {
            j = localViewGroup.getChildDrawingOrder(k, j);
          }
          int i1;
          if (bool) {
            i1 = k;
          } else {
            i1 = j;
          }
          m = i;
          if (j != 0)
          {
            n = 0;
            for (;;)
            {
              m = i;
              if (n >= i1) {
                break;
              }
              int i2;
              if (bool) {
                i2 = localViewGroup.getChildDrawingOrder(k, n);
              } else {
                i2 = n;
              }
              m = i;
              if (i2 < j) {
                m = i + numViewsForAccessibility(localViewGroup.getChildAt(n));
              }
              n++;
              i = m;
            }
          }
        }
      }
      localView = (View)localViewParent2;
      i = m;
    }
    paramAccessibilityNodeInfo.setDrawingOrder(j);
  }
  
  private void populateVirtualStructure(ViewStructure paramViewStructure, AccessibilityNodeProvider paramAccessibilityNodeProvider, AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
  {
    int i = AccessibilityNodeInfo.getVirtualDescendantId(paramAccessibilityNodeInfo.getSourceNodeId());
    Object localObject1 = paramAccessibilityNodeInfo.getViewIdResourceName();
    Object localObject2 = null;
    paramViewStructure.setId(i, null, null, (String)localObject1);
    localObject1 = paramViewStructure.getTempRect();
    paramAccessibilityNodeInfo.getBoundsInParent((Rect)localObject1);
    paramViewStructure.setDimens(((Rect)localObject1).left, ((Rect)localObject1).top, 0, 0, ((Rect)localObject1).width(), ((Rect)localObject1).height());
    paramViewStructure.setVisibility(0);
    paramViewStructure.setEnabled(paramAccessibilityNodeInfo.isEnabled());
    if (paramAccessibilityNodeInfo.isClickable()) {
      paramViewStructure.setClickable(true);
    }
    if (paramAccessibilityNodeInfo.isFocusable()) {
      paramViewStructure.setFocusable(true);
    }
    if (paramAccessibilityNodeInfo.isFocused()) {
      paramViewStructure.setFocused(true);
    }
    if (paramAccessibilityNodeInfo.isAccessibilityFocused()) {
      paramViewStructure.setAccessibilityFocused(true);
    }
    if (paramAccessibilityNodeInfo.isSelected()) {
      paramViewStructure.setSelected(true);
    }
    if (paramAccessibilityNodeInfo.isLongClickable()) {
      paramViewStructure.setLongClickable(true);
    }
    if (paramAccessibilityNodeInfo.isCheckable())
    {
      paramViewStructure.setCheckable(true);
      if (paramAccessibilityNodeInfo.isChecked()) {
        paramViewStructure.setChecked(true);
      }
    }
    if (paramAccessibilityNodeInfo.isContextClickable()) {
      paramViewStructure.setContextClickable(true);
    }
    if (paramBoolean) {
      paramViewStructure.setAutofillId(new AutofillId(getAutofillId(), AccessibilityNodeInfo.getVirtualDescendantId(paramAccessibilityNodeInfo.getSourceNodeId())));
    }
    localObject1 = paramAccessibilityNodeInfo.getClassName();
    if (localObject1 != null) {
      localObject2 = ((CharSequence)localObject1).toString();
    }
    paramViewStructure.setClassName((String)localObject2);
    paramViewStructure.setContentDescription(paramAccessibilityNodeInfo.getContentDescription());
    if (paramBoolean)
    {
      i = paramAccessibilityNodeInfo.getMaxTextLength();
      if (i != -1) {
        paramViewStructure.setMaxTextLength(i);
      }
      paramViewStructure.setHint(paramAccessibilityNodeInfo.getHintText());
    }
    localObject2 = paramAccessibilityNodeInfo.getText();
    if ((localObject2 == null) && (paramAccessibilityNodeInfo.getError() == null)) {
      i = 0;
    } else {
      i = 1;
    }
    if (i != 0) {
      paramViewStructure.setText((CharSequence)localObject2, paramAccessibilityNodeInfo.getTextSelectionStart(), paramAccessibilityNodeInfo.getTextSelectionEnd());
    }
    if (paramBoolean) {
      if (paramAccessibilityNodeInfo.isEditable())
      {
        paramViewStructure.setDataIsSensitive(true);
        if (i != 0)
        {
          paramViewStructure.setAutofillType(1);
          paramViewStructure.setAutofillValue(AutofillValue.forText((CharSequence)localObject2));
        }
        j = paramAccessibilityNodeInfo.getInputType();
        i = j;
        if (j == 0)
        {
          i = j;
          if (paramAccessibilityNodeInfo.isPassword()) {
            i = 129;
          }
        }
        paramViewStructure.setInputType(i);
      }
      else
      {
        paramViewStructure.setDataIsSensitive(false);
      }
    }
    int j = paramAccessibilityNodeInfo.getChildCount();
    if (j > 0)
    {
      paramViewStructure.setChildCount(j);
      for (i = 0; i < j; i++) {
        if (AccessibilityNodeInfo.getVirtualDescendantId(paramAccessibilityNodeInfo.getChildNodeIds().get(i)) == -1)
        {
          Log.e("View", "Virtual view pointing to its host. Ignoring");
        }
        else
        {
          localObject2 = paramAccessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(paramAccessibilityNodeInfo.getChildId(i)));
          populateVirtualStructure(paramViewStructure.newChild(i), paramAccessibilityNodeProvider, (AccessibilityNodeInfo)localObject2, paramBoolean);
          ((AccessibilityNodeInfo)localObject2).recycle();
        }
      }
    }
  }
  
  private void postSendViewScrolledAccessibilityEventCallback(int paramInt1, int paramInt2)
  {
    if (this.mSendViewScrolledAccessibilityEvent == null) {
      this.mSendViewScrolledAccessibilityEvent = new SendViewScrolledAccessibilityEvent(null);
    }
    this.mSendViewScrolledAccessibilityEvent.post(paramInt1, paramInt2);
  }
  
  private static String printFlags(int paramInt)
  {
    Object localObject1 = "";
    int i = 0;
    if ((paramInt & 0x1) == 1)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("");
      ((StringBuilder)localObject1).append("TAKES_FOCUS");
      localObject1 = ((StringBuilder)localObject1).toString();
      i = 0 + 1;
    }
    paramInt &= 0xC;
    Object localObject2;
    if (paramInt != 4)
    {
      if (paramInt == 8)
      {
        localObject2 = localObject1;
        if (i > 0)
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append((String)localObject1);
          ((StringBuilder)localObject2).append(" ");
          localObject2 = ((StringBuilder)localObject2).toString();
        }
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append((String)localObject2);
        ((StringBuilder)localObject1).append("GONE");
        localObject1 = ((StringBuilder)localObject1).toString();
      }
    }
    else
    {
      localObject2 = localObject1;
      if (i > 0)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append((String)localObject1);
        ((StringBuilder)localObject2).append(" ");
        localObject2 = ((StringBuilder)localObject2).toString();
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append("INVISIBLE");
      localObject1 = ((StringBuilder)localObject1).toString();
    }
    return (String)localObject1;
  }
  
  private static String printPrivateFlags(int paramInt)
  {
    Object localObject1 = "";
    int i = 0;
    if ((paramInt & 0x1) == 1)
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("");
      ((StringBuilder)localObject2).append("WANTS_FOCUS");
      localObject1 = ((StringBuilder)localObject2).toString();
      i = 0 + 1;
    }
    Object localObject2 = localObject1;
    int j = i;
    if ((paramInt & 0x2) == 2)
    {
      localObject2 = localObject1;
      if (i > 0)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append((String)localObject1);
        ((StringBuilder)localObject2).append(" ");
        localObject2 = ((StringBuilder)localObject2).toString();
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append("FOCUSED");
      localObject2 = ((StringBuilder)localObject1).toString();
      j = i + 1;
    }
    localObject1 = localObject2;
    i = j;
    if ((paramInt & 0x4) == 4)
    {
      localObject1 = localObject2;
      if (j > 0)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append((String)localObject2);
        ((StringBuilder)localObject1).append(" ");
        localObject1 = ((StringBuilder)localObject1).toString();
      }
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append((String)localObject1);
      ((StringBuilder)localObject2).append("SELECTED");
      localObject1 = ((StringBuilder)localObject2).toString();
      i = j + 1;
    }
    Object localObject3 = localObject1;
    j = i;
    if ((paramInt & 0x8) == 8)
    {
      localObject2 = localObject1;
      if (i > 0)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append((String)localObject1);
        ((StringBuilder)localObject2).append(" ");
        localObject2 = ((StringBuilder)localObject2).toString();
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append("IS_ROOT_NAMESPACE");
      localObject3 = ((StringBuilder)localObject1).toString();
      j = i + 1;
    }
    localObject2 = localObject3;
    i = j;
    if ((paramInt & 0x10) == 16)
    {
      localObject2 = localObject3;
      if (j > 0)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append((String)localObject3);
        ((StringBuilder)localObject2).append(" ");
        localObject2 = ((StringBuilder)localObject2).toString();
      }
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append("HAS_BOUNDS");
      localObject2 = ((StringBuilder)localObject1).toString();
      i = j + 1;
    }
    localObject1 = localObject2;
    if ((paramInt & 0x20) == 32)
    {
      localObject1 = localObject2;
      if (i > 0)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append((String)localObject2);
        ((StringBuilder)localObject1).append(" ");
        localObject1 = ((StringBuilder)localObject1).toString();
      }
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append((String)localObject1);
      ((StringBuilder)localObject2).append("DRAWN");
      localObject1 = ((StringBuilder)localObject2).toString();
    }
    return (String)localObject1;
  }
  
  private void rebuildOutline()
  {
    Object localObject = this.mAttachInfo;
    if (localObject == null) {
      return;
    }
    if (this.mOutlineProvider == null)
    {
      this.mRenderNode.setOutline(null);
    }
    else
    {
      localObject = ((AttachInfo)localObject).mTmpOutline;
      ((Outline)localObject).setEmpty();
      ((Outline)localObject).setAlpha(1.0F);
      this.mOutlineProvider.getOutline(this, (Outline)localObject);
      this.mRenderNode.setOutline((Outline)localObject);
    }
  }
  
  private void recordGestureClassification(int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    StatsLog.write(177, getClass().getName(), paramInt);
  }
  
  private void registerPendingFrameMetricsObservers()
  {
    if (this.mFrameMetricsObservers != null)
    {
      ThreadedRenderer localThreadedRenderer = getThreadedRenderer();
      if (localThreadedRenderer != null)
      {
        Iterator localIterator = this.mFrameMetricsObservers.iterator();
        while (localIterator.hasNext()) {
          localThreadedRenderer.addFrameMetricsObserver((FrameMetricsObserver)localIterator.next());
        }
      }
      else
      {
        Log.w("View", "View not hardware-accelerated. Unable to observe frame stats");
      }
    }
  }
  
  private void removeLongPressCallback()
  {
    CheckForLongPress localCheckForLongPress = this.mPendingCheckForLongPress;
    if (localCheckForLongPress != null) {
      removeCallbacks(localCheckForLongPress);
    }
  }
  
  @UnsupportedAppUsage
  private void removePerformClickCallback()
  {
    PerformClick localPerformClick = this.mPerformClick;
    if (localPerformClick != null) {
      removeCallbacks(localPerformClick);
    }
  }
  
  private void removeTapCallback()
  {
    CheckForTap localCheckForTap = this.mPendingCheckForTap;
    if (localCheckForTap != null)
    {
      this.mPrivateFlags &= 0xFDFFFFFF;
      removeCallbacks(localCheckForTap);
    }
  }
  
  private void removeUnsetPressCallback()
  {
    if (((this.mPrivateFlags & 0x4000) != 0) && (this.mUnsetPressedState != null))
    {
      setPressed(false);
      removeCallbacks(this.mUnsetPressedState);
    }
  }
  
  private boolean requestFocusNoSearch(int paramInt, Rect paramRect)
  {
    if (!canTakeFocus()) {
      return false;
    }
    if ((isInTouchMode()) && (262144 != (this.mViewFlags & 0x40000))) {
      return false;
    }
    if (hasAncestorThatBlocksDescendantFocus()) {
      return false;
    }
    if (!isLayoutValid()) {
      this.mPrivateFlags |= 0x1;
    } else {
      clearParentsWantFocus();
    }
    handleFocusGainInternal(paramInt, paramRect);
    return true;
  }
  
  @UnsupportedAppUsage
  private void resetDisplayList()
  {
    this.mRenderNode.discardDisplayList();
    RenderNode localRenderNode = this.mBackgroundRenderNode;
    if (localRenderNode != null) {
      localRenderNode.discardDisplayList();
    }
  }
  
  private void resetPressedState()
  {
    if ((this.mViewFlags & 0x20) == 32) {
      return;
    }
    if (isPressed())
    {
      setPressed(false);
      if (!this.mHasPerformedLongPress) {
        removeLongPressCallback();
      }
    }
  }
  
  public static int resolveSize(int paramInt1, int paramInt2)
  {
    return resolveSizeAndState(paramInt1, paramInt2, 0) & 0xFFFFFF;
  }
  
  public static int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = MeasureSpec.getMode(paramInt2);
    paramInt2 = MeasureSpec.getSize(paramInt2);
    if (i != Integer.MIN_VALUE)
    {
      if (i == 1073741824) {
        paramInt1 = paramInt2;
      }
    }
    else if (paramInt2 < paramInt1) {
      paramInt1 = 0x1000000 | paramInt2;
    }
    return 0xFF000000 & paramInt3 | paramInt1;
  }
  
  private void retrieveExplicitStyle(Resources.Theme paramTheme, AttributeSet paramAttributeSet)
  {
    if (!sDebugViewAttributes) {
      return;
    }
    this.mExplicitStyle = paramTheme.getExplicitStyle(paramAttributeSet);
  }
  
  private static float sanitizeFloatPropertyValue(float paramFloat, String paramString)
  {
    return sanitizeFloatPropertyValue(paramFloat, paramString, -3.4028235E38F, Float.MAX_VALUE);
  }
  
  private static float sanitizeFloatPropertyValue(float paramFloat1, String paramString, float paramFloat2, float paramFloat3)
  {
    if ((paramFloat1 >= paramFloat2) && (paramFloat1 <= paramFloat3)) {
      return paramFloat1;
    }
    if ((paramFloat1 >= paramFloat2) && (paramFloat1 != Float.NEGATIVE_INFINITY))
    {
      if ((paramFloat1 <= paramFloat3) && (paramFloat1 != Float.POSITIVE_INFINITY))
      {
        if (Float.isNaN(paramFloat1))
        {
          if (!sThrowOnInvalidFloatProperties) {
            return 0.0F;
          }
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Cannot set '");
          localStringBuilder.append(paramString);
          localStringBuilder.append("' to Float.NaN");
          throw new IllegalArgumentException(localStringBuilder.toString());
        }
        paramString = new StringBuilder();
        paramString.append("How do you get here?? ");
        paramString.append(paramFloat1);
        throw new IllegalStateException(paramString.toString());
      }
      if (!sThrowOnInvalidFloatProperties) {
        return paramFloat3;
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Cannot set '");
      localStringBuilder.append(paramString);
      localStringBuilder.append("' to ");
      localStringBuilder.append(paramFloat1);
      localStringBuilder.append(", the value must be <= ");
      localStringBuilder.append(paramFloat3);
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    if (!sThrowOnInvalidFloatProperties) {
      return paramFloat2;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Cannot set '");
    localStringBuilder.append(paramString);
    localStringBuilder.append("' to ");
    localStringBuilder.append(paramFloat1);
    localStringBuilder.append(", the value must be >= ");
    localStringBuilder.append(paramFloat2);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  private void saveAttributeData(AttributeSet paramAttributeSet, TypedArray paramTypedArray)
  {
    if (paramAttributeSet == null) {
      i = 0;
    } else {
      i = paramAttributeSet.getAttributeCount();
    }
    int j = paramTypedArray.getIndexCount();
    String[] arrayOfString = new String[(i + j) * 2];
    int k = 0;
    for (int m = 0; m < i; m++)
    {
      arrayOfString[k] = paramAttributeSet.getAttributeName(m);
      arrayOfString[(k + 1)] = paramAttributeSet.getAttributeValue(m);
      k += 2;
    }
    Resources localResources = paramTypedArray.getResources();
    SparseArray localSparseArray = getAttributeMap();
    m = 0;
    int i = k;
    for (k = m; k < j; k++)
    {
      m = paramTypedArray.getIndex(k);
      if (paramTypedArray.hasValueOrEmpty(m))
      {
        int n = paramTypedArray.getResourceId(m, 0);
        if (n != 0)
        {
          String str = (String)localSparseArray.get(n);
          paramAttributeSet = str;
          if (str == null)
          {
            try
            {
              paramAttributeSet = localResources.getResourceName(n);
            }
            catch (Resources.NotFoundException paramAttributeSet)
            {
              paramAttributeSet = new StringBuilder();
              paramAttributeSet.append("0x");
              paramAttributeSet.append(Integer.toHexString(n));
              paramAttributeSet = paramAttributeSet.toString();
            }
            localSparseArray.put(n, paramAttributeSet);
          }
          arrayOfString[i] = paramAttributeSet;
          arrayOfString[(i + 1)] = paramTypedArray.getString(m);
          i += 2;
        }
      }
    }
    paramAttributeSet = new String[i];
    System.arraycopy(arrayOfString, 0, paramAttributeSet, 0, i);
    this.mAttributes = paramAttributeSet;
  }
  
  private void sendAccessibilityHoverEvent(int paramInt)
  {
    for (Object localObject = this;; localObject = (View)localObject)
    {
      if (((View)localObject).includeForAccessibility())
      {
        ((View)localObject).sendAccessibilityEvent(paramInt);
        return;
      }
      localObject = ((View)localObject).getParent();
      if (!(localObject instanceof View)) {
        break;
      }
    }
  }
  
  private void sendViewTextTraversedAtGranularityEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mParent == null) {
      return;
    }
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(131072);
    onInitializeAccessibilityEvent(localAccessibilityEvent);
    onPopulateAccessibilityEvent(localAccessibilityEvent);
    localAccessibilityEvent.setFromIndex(paramInt3);
    localAccessibilityEvent.setToIndex(paramInt4);
    localAccessibilityEvent.setAction(paramInt1);
    localAccessibilityEvent.setMovementGranularity(paramInt2);
    this.mParent.requestSendAccessibilityEvent(this, localAccessibilityEvent);
  }
  
  private void setBackgroundRenderNodeProperties(RenderNode paramRenderNode)
  {
    paramRenderNode.setTranslationX(this.mScrollX);
    paramRenderNode.setTranslationY(this.mScrollY);
  }
  
  private void setDefaultFocusHighlight(Drawable paramDrawable)
  {
    this.mDefaultFocusHighlight = paramDrawable;
    boolean bool = true;
    this.mDefaultFocusHighlightSizeChanged = true;
    if (paramDrawable != null)
    {
      int i = this.mPrivateFlags;
      if ((i & 0x80) != 0) {
        this.mPrivateFlags = (i & 0xFF7F);
      }
      paramDrawable.setLayoutDirection(getLayoutDirection());
      if (paramDrawable.isStateful()) {
        paramDrawable.setState(getDrawableState());
      }
      if (isAttachedToWindow())
      {
        if ((getWindowVisibility() != 0) || (!isShown())) {
          bool = false;
        }
        paramDrawable.setVisible(bool, false);
      }
      paramDrawable.setCallback(this);
    }
    else if (((this.mViewFlags & 0x80) != 0) && (this.mBackground == null))
    {
      paramDrawable = this.mForegroundInfo;
      if ((paramDrawable == null) || (paramDrawable.mDrawable == null)) {
        this.mPrivateFlags |= 0x80;
      }
    }
    invalidate();
  }
  
  private void setFocusedInCluster(View paramView)
  {
    if ((this instanceof ViewGroup)) {
      ((ViewGroup)this).mFocusedInCluster = null;
    }
    if (paramView == this) {
      return;
    }
    ViewParent localViewParent = this.mParent;
    View localView = this;
    while ((localViewParent instanceof ViewGroup))
    {
      ((ViewGroup)localViewParent).mFocusedInCluster = localView;
      if (localViewParent == paramView) {
        break;
      }
      localView = (View)localViewParent;
      localViewParent = localViewParent.getParent();
    }
  }
  
  private void setKeyedTag(int paramInt, Object paramObject)
  {
    if (this.mKeyedTags == null) {
      this.mKeyedTags = new SparseArray(2);
    }
    this.mKeyedTags.put(paramInt, paramObject);
  }
  
  private void setMeasuredDimensionRaw(int paramInt1, int paramInt2)
  {
    this.mMeasuredWidth = paramInt1;
    this.mMeasuredHeight = paramInt2;
    this.mPrivateFlags |= 0x800;
  }
  
  private boolean setOpticalFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Object localObject = this.mParent;
    if ((localObject instanceof View)) {
      localObject = ((View)localObject).getOpticalInsets();
    } else {
      localObject = Insets.NONE;
    }
    Insets localInsets = getOpticalInsets();
    return setFrame(((Insets)localObject).left + paramInt1 - localInsets.left, ((Insets)localObject).top + paramInt2 - localInsets.top, ((Insets)localObject).left + paramInt3 + localInsets.right, ((Insets)localObject).top + paramInt4 + localInsets.bottom);
  }
  
  private void setOutlineProviderFromAttribute(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt == 3) {
            setOutlineProvider(ViewOutlineProvider.PADDED_BOUNDS);
          }
        }
        else {
          setOutlineProvider(ViewOutlineProvider.BOUNDS);
        }
      }
      else {
        setOutlineProvider(null);
      }
    }
    else {
      setOutlineProvider(ViewOutlineProvider.BACKGROUND);
    }
  }
  
  private void setPressed(boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    if (paramBoolean) {
      drawableHotspotChanged(paramFloat1, paramFloat2);
    }
    setPressed(paramBoolean);
  }
  
  private boolean showHoverTooltip()
  {
    return showTooltip(this.mTooltipInfo.mAnchorX, this.mTooltipInfo.mAnchorY, false);
  }
  
  private boolean showLongClickTooltip(int paramInt1, int paramInt2)
  {
    removeCallbacks(this.mTooltipInfo.mShowTooltipRunnable);
    removeCallbacks(this.mTooltipInfo.mHideTooltipRunnable);
    return showTooltip(paramInt1, paramInt2, true);
  }
  
  private boolean showTooltip(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((this.mAttachInfo != null) && (this.mTooltipInfo != null))
    {
      if ((paramBoolean) && ((this.mViewFlags & 0x20) != 0)) {
        return false;
      }
      if (TextUtils.isEmpty(this.mTooltipInfo.mTooltipText)) {
        return false;
      }
      hideTooltip();
      TooltipInfo localTooltipInfo = this.mTooltipInfo;
      localTooltipInfo.mTooltipFromLongClick = paramBoolean;
      localTooltipInfo.mTooltipPopup = new TooltipPopup(getContext());
      if ((this.mPrivateFlags3 & 0x20000) == 131072) {
        paramBoolean = true;
      } else {
        paramBoolean = false;
      }
      this.mTooltipInfo.mTooltipPopup.show(this, paramInt1, paramInt2, paramBoolean, this.mTooltipInfo.mTooltipText);
      this.mAttachInfo.mTooltipHost = this;
      notifyViewAccessibilityStateChangedIfNeeded(0);
      return true;
    }
    return false;
  }
  
  private void sizeChange(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    Object localObject = this.mOverlay;
    if (localObject != null)
    {
      ((ViewOverlay)localObject).getOverlayView().setRight(paramInt1);
      this.mOverlay.getOverlayView().setBottom(paramInt2);
    }
    if ((!sCanFocusZeroSized) && (isLayoutValid()))
    {
      localObject = this.mParent;
      if ((!(localObject instanceof ViewGroup)) || (!((ViewGroup)localObject).isLayoutSuppressed())) {
        if ((paramInt1 > 0) && (paramInt2 > 0))
        {
          if (((paramInt3 <= 0) || (paramInt4 <= 0)) && (this.mParent != null) && (canTakeFocus())) {
            this.mParent.focusableViewAvailable(this);
          }
        }
        else
        {
          if (hasFocus())
          {
            clearFocus();
            localObject = this.mParent;
            if ((localObject instanceof ViewGroup)) {
              ((ViewGroup)localObject).clearFocusedInCluster();
            }
          }
          clearAccessibilityFocus();
        }
      }
    }
    rebuildOutline();
  }
  
  private boolean skipInvalidate()
  {
    if (((this.mViewFlags & 0xC) != 0) && (this.mCurrentAnimation == null))
    {
      ViewParent localViewParent = this.mParent;
      if ((!(localViewParent instanceof ViewGroup)) || (!((ViewGroup)localViewParent).isViewTransitioning(this))) {
        return true;
      }
    }
    boolean bool = false;
    return bool;
  }
  
  private void switchDefaultFocusHighlight()
  {
    if (isFocused())
    {
      Drawable localDrawable = this.mBackground;
      Object localObject = this.mForegroundInfo;
      if (localObject == null) {
        localObject = null;
      } else {
        localObject = ((ForegroundInfo)localObject).mDrawable;
      }
      boolean bool = isDefaultFocusHighlightNeeded(localDrawable, (Drawable)localObject);
      int i;
      if (this.mDefaultFocusHighlight != null) {
        i = 1;
      } else {
        i = 0;
      }
      if ((bool) && (i == 0)) {
        setDefaultFocusHighlight(getDefaultFocusHighlightDrawable());
      } else if ((!bool) && (i != 0)) {
        setDefaultFocusHighlight(null);
      }
    }
  }
  
  private boolean traverseAtGranularity(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    CharSequence localCharSequence = getIterableTextForAccessibility();
    if ((localCharSequence != null) && (localCharSequence.length() != 0))
    {
      Object localObject = getIteratorForGranularity(paramInt);
      if (localObject == null) {
        return false;
      }
      int i = getAccessibilitySelectionEnd();
      int j = i;
      if (i == -1) {
        if (paramBoolean1) {
          j = 0;
        } else {
          j = localCharSequence.length();
        }
      }
      if (paramBoolean1) {
        localObject = ((AccessibilityIterators.TextSegmentIterator)localObject).following(j);
      } else {
        localObject = ((AccessibilityIterators.TextSegmentIterator)localObject).preceding(j);
      }
      if (localObject == null) {
        return false;
      }
      i = localObject[0];
      int k = localObject[1];
      int m;
      if ((paramBoolean2) && (isAccessibilitySelectionExtendable()))
      {
        m = getAccessibilitySelectionStart();
        j = m;
        if (m == -1) {
          if (paramBoolean1) {
            j = i;
          } else {
            j = k;
          }
        }
        if (paramBoolean1) {
          m = k;
        } else {
          m = i;
        }
      }
      else
      {
        if (paramBoolean1) {
          j = k;
        } else {
          j = i;
        }
        m = j;
      }
      setAccessibilitySelection(j, m);
      if (paramBoolean1) {
        j = 256;
      } else {
        j = 512;
      }
      sendViewTextTraversedAtGranularityEvent(j, paramInt, i, k);
      return true;
    }
    return false;
  }
  
  private void updateFocusedInCluster(View paramView, int paramInt)
  {
    if (paramView != null)
    {
      View localView = paramView.findKeyboardNavigationCluster();
      if (localView != findKeyboardNavigationCluster())
      {
        paramView.setFocusedInCluster(localView);
        if (!(paramView.mParent instanceof ViewGroup)) {
          return;
        }
        if ((paramInt != 2) && (paramInt != 1))
        {
          if (((paramView instanceof ViewGroup)) && (((ViewGroup)paramView).getDescendantFocusability() == 262144) && (ViewRootImpl.isViewDescendantOf(this, paramView))) {
            ((ViewGroup)paramView.mParent).clearFocusedInCluster(paramView);
          }
        }
        else {
          ((ViewGroup)paramView.mParent).clearFocusedInCluster(paramView);
        }
      }
    }
  }
  
  private void updatePflags3AndNotifyA11yIfChanged(int paramInt, boolean paramBoolean)
  {
    int i = this.mPrivateFlags3;
    if (paramBoolean) {
      paramInt = i | paramInt;
    } else {
      paramInt = i & paramInt;
    }
    if (paramInt != this.mPrivateFlags3)
    {
      this.mPrivateFlags3 = paramInt;
      notifyViewAccessibilityStateChangedIfNeeded(0);
    }
  }
  
  public void addChildrenForAccessibility(ArrayList<View> paramArrayList) {}
  
  public void addExtraDataToAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo, String paramString, Bundle paramBundle) {}
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt)
  {
    addFocusables(paramArrayList, paramInt, isInTouchMode());
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if (paramArrayList == null) {
      return;
    }
    if (!canTakeFocus()) {
      return;
    }
    if (((paramInt2 & 0x1) == 1) && (!isFocusableInTouchMode())) {
      return;
    }
    paramArrayList.add(this);
  }
  
  public void addFrameMetricsListener(Window paramWindow, Window.OnFrameMetricsAvailableListener paramOnFrameMetricsAvailableListener, Handler paramHandler)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null)
    {
      if (localAttachInfo.mThreadedRenderer != null)
      {
        if (this.mFrameMetricsObservers == null) {
          this.mFrameMetricsObservers = new ArrayList();
        }
        paramWindow = new FrameMetricsObserver(paramWindow, paramHandler.getLooper(), paramOnFrameMetricsAvailableListener);
        this.mFrameMetricsObservers.add(paramWindow);
        this.mAttachInfo.mThreadedRenderer.addFrameMetricsObserver(paramWindow);
      }
      else
      {
        Log.w("View", "View not hardware-accelerated. Unable to observe frame stats");
      }
    }
    else
    {
      if (this.mFrameMetricsObservers == null) {
        this.mFrameMetricsObservers = new ArrayList();
      }
      paramWindow = new FrameMetricsObserver(paramWindow, paramHandler.getLooper(), paramOnFrameMetricsAvailableListener);
      this.mFrameMetricsObservers.add(paramWindow);
    }
  }
  
  public void addKeyboardNavigationClusters(Collection<View> paramCollection, int paramInt)
  {
    if (!isKeyboardNavigationCluster()) {
      return;
    }
    if (!hasFocusable()) {
      return;
    }
    paramCollection.add(this);
  }
  
  public void addOnAttachStateChangeListener(OnAttachStateChangeListener paramOnAttachStateChangeListener)
  {
    ListenerInfo localListenerInfo = getListenerInfo();
    if (localListenerInfo.mOnAttachStateChangeListeners == null) {
      ListenerInfo.access$302(localListenerInfo, new CopyOnWriteArrayList());
    }
    localListenerInfo.mOnAttachStateChangeListeners.add(paramOnAttachStateChangeListener);
  }
  
  public void addOnLayoutChangeListener(OnLayoutChangeListener paramOnLayoutChangeListener)
  {
    ListenerInfo localListenerInfo = getListenerInfo();
    if (localListenerInfo.mOnLayoutChangeListeners == null) {
      ListenerInfo.access$202(localListenerInfo, new ArrayList());
    }
    if (!localListenerInfo.mOnLayoutChangeListeners.contains(paramOnLayoutChangeListener)) {
      localListenerInfo.mOnLayoutChangeListeners.add(paramOnLayoutChangeListener);
    }
  }
  
  public void addOnUnhandledKeyEventListener(OnUnhandledKeyEventListener paramOnUnhandledKeyEventListener)
  {
    ArrayList localArrayList1 = getListenerInfo().mUnhandledKeyListeners;
    ArrayList localArrayList2 = localArrayList1;
    if (localArrayList1 == null)
    {
      localArrayList2 = new ArrayList();
      ListenerInfo.access$4102(getListenerInfo(), localArrayList2);
    }
    localArrayList2.add(paramOnUnhandledKeyEventListener);
    if (localArrayList2.size() == 1)
    {
      paramOnUnhandledKeyEventListener = this.mParent;
      if ((paramOnUnhandledKeyEventListener instanceof ViewGroup)) {
        ((ViewGroup)paramOnUnhandledKeyEventListener).incrementChildUnhandledKeyListeners();
      }
    }
  }
  
  public void addTouchables(ArrayList<View> paramArrayList)
  {
    int i = this.mViewFlags;
    if ((((i & 0x4000) == 16384) || ((i & 0x200000) == 2097152) || ((i & 0x800000) == 8388608)) && ((i & 0x20) == 0)) {
      paramArrayList.add(this);
    }
  }
  
  public ViewPropertyAnimator animate()
  {
    if (this.mAnimator == null) {
      this.mAnimator = new ViewPropertyAnimator(this);
    }
    return this.mAnimator;
  }
  
  public void announceForAccessibility(CharSequence paramCharSequence)
  {
    if ((AccessibilityManager.getInstance(this.mContext).isEnabled()) && (this.mParent != null))
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(16384);
      onInitializeAccessibilityEvent(localAccessibilityEvent);
      localAccessibilityEvent.getText().add(paramCharSequence);
      localAccessibilityEvent.setContentDescription(null);
      this.mParent.requestSendAccessibilityEvent(this, localAccessibilityEvent);
    }
  }
  
  @UnsupportedAppUsage
  public void applyDrawableToTransparentRegion(Drawable paramDrawable, Region paramRegion)
  {
    Region localRegion = paramDrawable.getTransparentRegion();
    paramDrawable = paramDrawable.getBounds();
    AttachInfo localAttachInfo = this.mAttachInfo;
    if ((localRegion != null) && (localAttachInfo != null))
    {
      int i = getRight() - getLeft();
      int j = getBottom() - getTop();
      if (paramDrawable.left > 0) {
        localRegion.op(0, 0, paramDrawable.left, j, Region.Op.UNION);
      }
      if (paramDrawable.right < i) {
        localRegion.op(paramDrawable.right, 0, i, j, Region.Op.UNION);
      }
      if (paramDrawable.top > 0) {
        localRegion.op(0, 0, i, paramDrawable.top, Region.Op.UNION);
      }
      if (paramDrawable.bottom < j) {
        localRegion.op(0, paramDrawable.bottom, i, j, Region.Op.UNION);
      }
      paramDrawable = localAttachInfo.mTransparentLocation;
      getLocationInWindow(paramDrawable);
      localRegion.translate(paramDrawable[0], paramDrawable[1]);
      paramRegion.op(localRegion, Region.Op.INTERSECT);
    }
    else
    {
      paramRegion.op(paramDrawable, Region.Op.DIFFERENCE);
    }
  }
  
  boolean areDrawablesResolved()
  {
    boolean bool;
    if ((this.mPrivateFlags2 & 0x40000000) == 1073741824) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  void assignParent(ViewParent paramViewParent)
  {
    if (this.mParent == null)
    {
      this.mParent = paramViewParent;
    }
    else
    {
      if (paramViewParent != null) {
        break label25;
      }
      this.mParent = null;
    }
    return;
    label25:
    paramViewParent = new StringBuilder();
    paramViewParent.append("view ");
    paramViewParent.append(this);
    paramViewParent.append(" being added, but it already has a parent");
    throw new RuntimeException(paramViewParent.toString());
  }
  
  public void autofill(SparseArray<AutofillValue> paramSparseArray)
  {
    if (!this.mContext.isAutofillCompatibilityEnabled()) {
      return;
    }
    AccessibilityNodeProvider localAccessibilityNodeProvider = getAccessibilityNodeProvider();
    if (localAccessibilityNodeProvider == null) {
      return;
    }
    int i = paramSparseArray.size();
    for (int j = 0; j < i; j++)
    {
      Object localObject = (AutofillValue)paramSparseArray.valueAt(j);
      if (((AutofillValue)localObject).isText())
      {
        int k = paramSparseArray.keyAt(j);
        CharSequence localCharSequence = ((AutofillValue)localObject).getTextValue();
        localObject = new Bundle();
        ((Bundle)localObject).putCharSequence("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE", localCharSequence);
        localAccessibilityNodeProvider.performAction(k, 2097152, (Bundle)localObject);
      }
    }
  }
  
  public void autofill(AutofillValue paramAutofillValue) {}
  
  protected boolean awakenScrollBars()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    boolean bool = true;
    if ((localScrollabilityCache == null) || (!awakenScrollBars(localScrollabilityCache.scrollBarDefaultDelayBeforeFade, true))) {
      bool = false;
    }
    return bool;
  }
  
  protected boolean awakenScrollBars(int paramInt)
  {
    return awakenScrollBars(paramInt, true);
  }
  
  protected boolean awakenScrollBars(int paramInt, boolean paramBoolean)
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    if ((localScrollabilityCache != null) && (localScrollabilityCache.fadeScrollBars))
    {
      if (localScrollabilityCache.scrollBar == null)
      {
        localScrollabilityCache.scrollBar = new ScrollBarDrawable();
        localScrollabilityCache.scrollBar.setState(getDrawableState());
        localScrollabilityCache.scrollBar.setCallback(this);
      }
      if ((!isHorizontalScrollBarEnabled()) && (!isVerticalScrollBarEnabled())) {
        return false;
      }
      if (paramBoolean) {
        postInvalidateOnAnimation();
      }
      int i = paramInt;
      if (localScrollabilityCache.state == 0) {
        i = Math.max(750, paramInt);
      }
      long l = AnimationUtils.currentAnimationTimeMillis() + i;
      localScrollabilityCache.fadeStartTime = l;
      localScrollabilityCache.state = 1;
      AttachInfo localAttachInfo = this.mAttachInfo;
      if (localAttachInfo != null)
      {
        localAttachInfo.mHandler.removeCallbacks(localScrollabilityCache);
        this.mAttachInfo.mHandler.postAtTime(localScrollabilityCache, l);
      }
      return true;
    }
    return false;
  }
  
  public void bringToFront()
  {
    ViewParent localViewParent = this.mParent;
    if (localViewParent != null) {
      localViewParent.bringChildToFront(this);
    }
  }
  
  @Deprecated
  public void buildDrawingCache()
  {
    buildDrawingCache(false);
  }
  
  @Deprecated
  public void buildDrawingCache(boolean paramBoolean)
  {
    if (((this.mPrivateFlags & 0x8000) == 0) || (paramBoolean ? this.mDrawingCache == null : this.mUnscaledDrawingCache == null)) {
      if (Trace.isTagEnabled(8L))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("buildDrawingCache/SW Layer for ");
        localStringBuilder.append(getClass().getSimpleName());
        Trace.traceBegin(8L, localStringBuilder.toString());
      }
    }
    try
    {
      buildDrawingCacheImpl(paramBoolean);
      return;
    }
    finally
    {
      Trace.traceEnd(8L);
    }
  }
  
  public void buildLayer()
  {
    if (this.mLayerType == 0) {
      return;
    }
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null)
    {
      if ((getWidth() != 0) && (getHeight() != 0))
      {
        int i = this.mLayerType;
        if (i != 1)
        {
          if (i == 2)
          {
            updateDisplayListIfDirty();
            if ((localAttachInfo.mThreadedRenderer != null) && (this.mRenderNode.hasDisplayList())) {
              localAttachInfo.mThreadedRenderer.buildLayer(this.mRenderNode);
            }
          }
        }
        else {
          buildDrawingCache(true);
        }
        return;
      }
      return;
    }
    throw new IllegalStateException("This view must be attached to a window first");
  }
  
  final boolean callDragEventHandler(DragEvent paramDragEvent)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    boolean bool;
    if ((localListenerInfo != null) && (localListenerInfo.mOnDragListener != null) && ((this.mViewFlags & 0x20) == 0) && (localListenerInfo.mOnDragListener.onDrag(this, paramDragEvent))) {
      bool = true;
    } else {
      bool = onDragEvent(paramDragEvent);
    }
    int i = paramDragEvent.mAction;
    if (i != 4)
    {
      if (i != 5)
      {
        if (i == 6)
        {
          this.mPrivateFlags2 &= 0xFFFFFFFD;
          refreshDrawableState();
        }
      }
      else
      {
        this.mPrivateFlags2 |= 0x2;
        refreshDrawableState();
      }
    }
    else
    {
      this.mPrivateFlags2 &= 0xFFFFFFFC;
      refreshDrawableState();
    }
    return bool;
  }
  
  public boolean callOnClick()
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mOnClickListener != null))
    {
      localListenerInfo.mOnClickListener.onClick(this);
      return true;
    }
    return false;
  }
  
  boolean canAcceptDrag()
  {
    int i = this.mPrivateFlags2;
    boolean bool = true;
    if ((i & 0x1) == 0) {
      bool = false;
    }
    return bool;
  }
  
  public boolean canHaveDisplayList()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    boolean bool;
    if ((localAttachInfo != null) && (localAttachInfo.mThreadedRenderer != null)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean canNotifyAutofillEnterExitEvent()
  {
    boolean bool;
    if ((isAutofillable()) && (isAttachedToWindow())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected boolean canReceivePointerEvents()
  {
    boolean bool;
    if (((this.mViewFlags & 0xC) != 0) && (getAnimation() == null)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean canResolveLayoutDirection()
  {
    if (getRawLayoutDirection() != 2) {
      return true;
    }
    ViewParent localViewParent = this.mParent;
    if (localViewParent != null) {
      try
      {
        boolean bool = localViewParent.canResolveLayoutDirection();
        return bool;
      }
      catch (AbstractMethodError localAbstractMethodError)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.mParent.getClass().getSimpleName());
        localStringBuilder.append(" does not fully implement ViewParent");
        Log.e("View", localStringBuilder.toString(), localAbstractMethodError);
      }
    }
    return false;
  }
  
  public boolean canResolveTextAlignment()
  {
    if (getRawTextAlignment() != 0) {
      return true;
    }
    Object localObject = this.mParent;
    if (localObject != null) {
      try
      {
        boolean bool = ((ViewParent)localObject).canResolveTextAlignment();
        return bool;
      }
      catch (AbstractMethodError localAbstractMethodError)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(this.mParent.getClass().getSimpleName());
        ((StringBuilder)localObject).append(" does not fully implement ViewParent");
        Log.e("View", ((StringBuilder)localObject).toString(), localAbstractMethodError);
      }
    }
    return false;
  }
  
  public boolean canResolveTextDirection()
  {
    if (getRawTextDirection() != 0) {
      return true;
    }
    ViewParent localViewParent = this.mParent;
    if (localViewParent != null) {
      try
      {
        boolean bool = localViewParent.canResolveTextDirection();
        return bool;
      }
      catch (AbstractMethodError localAbstractMethodError)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(this.mParent.getClass().getSimpleName());
        localStringBuilder.append(" does not fully implement ViewParent");
        Log.e("View", localStringBuilder.toString(), localAbstractMethodError);
      }
    }
    return false;
  }
  
  public boolean canScrollHorizontally(int paramInt)
  {
    int i = computeHorizontalScrollOffset();
    int j = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
    boolean bool1 = false;
    boolean bool2 = false;
    if (j == 0) {
      return false;
    }
    if (paramInt < 0)
    {
      if (i > 0) {
        bool2 = true;
      }
      return bool2;
    }
    bool2 = bool1;
    if (i < j - 1) {
      bool2 = true;
    }
    return bool2;
  }
  
  public boolean canScrollVertically(int paramInt)
  {
    int i = computeVerticalScrollOffset();
    int j = computeVerticalScrollRange() - computeVerticalScrollExtent();
    boolean bool1 = false;
    boolean bool2 = false;
    if (j == 0) {
      return false;
    }
    if (paramInt < 0)
    {
      if (i > 0) {
        bool2 = true;
      }
      return bool2;
    }
    bool2 = bool1;
    if (i < j - 1) {
      bool2 = true;
    }
    return bool2;
  }
  
  public final void cancelDragAndDrop()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo == null)
    {
      Log.w("View", "cancelDragAndDrop called on a detached view.");
      return;
    }
    if (localAttachInfo.mDragToken != null)
    {
      try
      {
        this.mAttachInfo.mSession.cancelDragAndDrop(this.mAttachInfo.mDragToken, false);
      }
      catch (Exception localException)
      {
        Log.e("View", "Unable to cancel drag", localException);
      }
      this.mAttachInfo.mDragToken = null;
    }
    else
    {
      Log.e("View", "No active drag to cancel");
    }
  }
  
  public void cancelLongPress()
  {
    removeLongPressCallback();
    removeTapCallback();
  }
  
  public final void cancelPendingInputEvents()
  {
    dispatchCancelPendingInputEvents();
  }
  
  public void captureTransitioningViews(List<View> paramList)
  {
    if (getVisibility() == 0) {
      paramList.add(this);
    }
  }
  
  public boolean checkInputConnectionProxy(View paramView)
  {
    return false;
  }
  
  @UnsupportedAppUsage
  public void clearAccessibilityFocus()
  {
    clearAccessibilityFocusNoCallbacks(0);
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    if (localViewRootImpl != null)
    {
      View localView = localViewRootImpl.getAccessibilityFocusedHost();
      if ((localView != null) && (ViewRootImpl.isViewDescendantOf(localView, this))) {
        localViewRootImpl.setAccessibilityFocus(null, null);
      }
    }
  }
  
  void clearAccessibilityFocusNoCallbacks(int paramInt)
  {
    int i = this.mPrivateFlags2;
    if ((0x4000000 & i) != 0)
    {
      this.mPrivateFlags2 = (i & 0xFBFFFFFF);
      invalidate();
      if (AccessibilityManager.getInstance(this.mContext).isEnabled())
      {
        AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(65536);
        localAccessibilityEvent.setAction(paramInt);
        AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
        if (localAccessibilityDelegate != null) {
          localAccessibilityDelegate.sendAccessibilityEventUnchecked(this, localAccessibilityEvent);
        } else {
          sendAccessibilityEventUnchecked(localAccessibilityEvent);
        }
      }
    }
  }
  
  public void clearAnimation()
  {
    Animation localAnimation = this.mCurrentAnimation;
    if (localAnimation != null) {
      localAnimation.detach();
    }
    this.mCurrentAnimation = null;
    invalidateParentIfNeeded();
  }
  
  public void clearFocus()
  {
    boolean bool;
    if ((!sAlwaysAssignFocus) && (isInTouchMode())) {
      bool = false;
    } else {
      bool = true;
    }
    clearFocusInternal(null, true, bool);
  }
  
  void clearFocusInternal(View paramView, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = this.mPrivateFlags;
    if ((i & 0x2) != 0)
    {
      this.mPrivateFlags = (i & 0xFFFFFFFD);
      clearParentsWantFocus();
      if (paramBoolean1)
      {
        paramView = this.mParent;
        if (paramView != null) {
          paramView.clearChildFocus(this);
        }
      }
      onFocusChanged(false, 0, null);
      refreshDrawableState();
      if ((paramBoolean1) && ((!paramBoolean2) || (!rootViewRequestFocus()))) {
        notifyGlobalFocusCleared(this);
      }
    }
  }
  
  void clearParentsWantFocus()
  {
    ViewParent localViewParent = this.mParent;
    if ((localViewParent instanceof View))
    {
      View localView = (View)localViewParent;
      localView.mPrivateFlags &= 0xFFFFFFFE;
      ((View)localViewParent).clearParentsWantFocus();
    }
  }
  
  int combineVisibility(int paramInt1, int paramInt2)
  {
    return Math.max(paramInt1, paramInt2);
  }
  
  @Deprecated
  @UnsupportedAppUsage
  protected boolean computeFitSystemWindows(Rect paramRect1, Rect paramRect2)
  {
    paramRect2 = computeSystemWindowInsets(new WindowInsets(paramRect1), paramRect2);
    paramRect1.set(paramRect2.getSystemWindowInsetsAsRect());
    return paramRect2.isSystemWindowInsetsConsumed();
  }
  
  protected int computeHorizontalScrollExtent()
  {
    return getWidth();
  }
  
  protected int computeHorizontalScrollOffset()
  {
    return this.mScrollX;
  }
  
  protected int computeHorizontalScrollRange()
  {
    return getWidth();
  }
  
  @UnsupportedAppUsage
  protected void computeOpaqueFlags()
  {
    Drawable localDrawable = this.mBackground;
    if ((localDrawable != null) && (localDrawable.getOpacity() == -1)) {
      this.mPrivateFlags |= 0x800000;
    } else {
      this.mPrivateFlags &= 0xFF7FFFFF;
    }
    int i = this.mViewFlags;
    if ((((i & 0x200) != 0) || ((i & 0x100) != 0)) && ((i & 0x3000000) != 0) && ((0x3000000 & i) != 33554432)) {
      this.mPrivateFlags &= 0xFEFFFFFF;
    } else {
      this.mPrivateFlags |= 0x1000000;
    }
  }
  
  Insets computeOpticalInsets()
  {
    Object localObject = this.mBackground;
    if (localObject == null) {
      localObject = Insets.NONE;
    } else {
      localObject = ((Drawable)localObject).getOpticalInsets();
    }
    return (Insets)localObject;
  }
  
  public void computeScroll() {}
  
  public WindowInsets computeSystemWindowInsets(WindowInsets paramWindowInsets, Rect paramRect)
  {
    if ((this.mViewFlags & 0x800) != 0)
    {
      AttachInfo localAttachInfo = this.mAttachInfo;
      if ((localAttachInfo != null) && (((localAttachInfo.mSystemUiVisibility & 0x600) != 0) || (this.mAttachInfo.mOverscanRequested)))
      {
        paramRect.set(this.mAttachInfo.mOverscanInsets);
        return paramWindowInsets.inset(paramRect);
      }
    }
    paramRect.set(paramWindowInsets.getSystemWindowInsetsAsRect());
    return paramWindowInsets.consumeSystemWindowInsets().inset(paramRect);
  }
  
  protected int computeVerticalScrollExtent()
  {
    return getHeight();
  }
  
  protected int computeVerticalScrollOffset()
  {
    return this.mScrollY;
  }
  
  protected int computeVerticalScrollRange()
  {
    return getHeight();
  }
  
  public AccessibilityNodeInfo createAccessibilityNodeInfo()
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      return localAccessibilityDelegate.createAccessibilityNodeInfo(this);
    }
    return createAccessibilityNodeInfoInternal();
  }
  
  public AccessibilityNodeInfo createAccessibilityNodeInfoInternal()
  {
    Object localObject = getAccessibilityNodeProvider();
    if (localObject != null) {
      return ((AccessibilityNodeProvider)localObject).createAccessibilityNodeInfo(-1);
    }
    localObject = AccessibilityNodeInfo.obtain(this);
    onInitializeAccessibilityNodeInfo((AccessibilityNodeInfo)localObject);
    return (AccessibilityNodeInfo)localObject;
  }
  
  public void createContextMenu(ContextMenu paramContextMenu)
  {
    Object localObject = getContextMenuInfo();
    ((MenuBuilder)paramContextMenu).setCurrentMenuInfo((ContextMenu.ContextMenuInfo)localObject);
    onCreateContextMenu(paramContextMenu);
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mOnCreateContextMenuListener != null)) {
      localListenerInfo.mOnCreateContextMenuListener.onCreateContextMenu(paramContextMenu, this, (ContextMenu.ContextMenuInfo)localObject);
    }
    ((MenuBuilder)paramContextMenu).setCurrentMenuInfo(null);
    localObject = this.mParent;
    if (localObject != null) {
      ((ViewParent)localObject).createContextMenu(paramContextMenu);
    }
  }
  
  @UnsupportedAppUsage
  public Bitmap createSnapshot(ViewDebug.CanvasProvider paramCanvasProvider, boolean paramBoolean)
  {
    int i = this.mRight;
    int j = this.mLeft;
    int k = this.mBottom;
    int m = this.mTop;
    AttachInfo localAttachInfo = this.mAttachInfo;
    float f;
    if (localAttachInfo != null) {
      f = localAttachInfo.mApplicationScale;
    } else {
      f = 1.0F;
    }
    i = (int)((i - j) * f + 0.5F);
    m = (int)((k - m) * f + 0.5F);
    Object localObject1 = null;
    Canvas localCanvas1 = null;
    k = 1;
    if (i <= 0) {
      i = 1;
    }
    if (m > 0) {
      k = m;
    }
    Object localObject2 = localObject1;
    try
    {
      Canvas localCanvas2 = paramCanvasProvider.getCanvas(this, i, k);
      if (localAttachInfo != null)
      {
        localObject2 = localObject1;
        localCanvas1 = localAttachInfo.mCanvas;
        localObject2 = localCanvas1;
        localAttachInfo.mCanvas = null;
      }
      localObject2 = localCanvas1;
      computeScroll();
      localObject2 = localCanvas1;
      i = localCanvas2.save();
      localObject2 = localCanvas1;
      localCanvas2.scale(f, f);
      localObject2 = localCanvas1;
      localCanvas2.translate(-this.mScrollX, -this.mScrollY);
      localObject2 = localCanvas1;
      k = this.mPrivateFlags;
      localObject2 = localCanvas1;
      this.mPrivateFlags &= 0xFFDFFFFF;
      localObject2 = localCanvas1;
      if ((this.mPrivateFlags & 0x80) == 128)
      {
        localObject2 = localCanvas1;
        dispatchDraw(localCanvas2);
        localObject2 = localCanvas1;
        drawAutofilledHighlight(localCanvas2);
        localObject2 = localCanvas1;
        if (this.mOverlay != null)
        {
          localObject2 = localCanvas1;
          if (!this.mOverlay.isEmpty())
          {
            localObject2 = localCanvas1;
            this.mOverlay.getOverlayView().draw(localCanvas2);
          }
        }
      }
      else
      {
        localObject2 = localCanvas1;
        draw(localCanvas2);
      }
      localObject2 = localCanvas1;
      this.mPrivateFlags = k;
      localObject2 = localCanvas1;
      localCanvas2.restoreToCount(i);
      localObject2 = localCanvas1;
      paramCanvasProvider = paramCanvasProvider.createBitmap();
      if (localCanvas1 != null) {
        localAttachInfo.mCanvas = localCanvas1;
      }
      return paramCanvasProvider;
    }
    finally
    {
      if (localObject2 != null) {
        localAttachInfo.mCanvas = ((Canvas)localObject2);
      }
    }
  }
  
  protected void damageInParent()
  {
    ViewParent localViewParent = this.mParent;
    if ((localViewParent != null) && (this.mAttachInfo != null)) {
      localViewParent.onDescendantInvalidated(this, this);
    }
  }
  
  @UnsupportedAppUsage
  public void debug()
  {
    debug(0);
  }
  
  @UnsupportedAppUsage
  protected void debug(int paramInt)
  {
    Object localObject1 = debugIndent(paramInt - 1);
    Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append("+ ");
    ((StringBuilder)localObject2).append(this);
    localObject2 = ((StringBuilder)localObject2).toString();
    int i = getId();
    localObject1 = localObject2;
    if (i != -1)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append(" (id=");
      ((StringBuilder)localObject1).append(i);
      ((StringBuilder)localObject1).append(")");
      localObject1 = ((StringBuilder)localObject1).toString();
    }
    Object localObject3 = getTag();
    localObject2 = localObject1;
    if (localObject3 != null)
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append((String)localObject1);
      ((StringBuilder)localObject2).append(" (tag=");
      ((StringBuilder)localObject2).append(localObject3);
      ((StringBuilder)localObject2).append(")");
      localObject2 = ((StringBuilder)localObject2).toString();
    }
    Log.d("View", (String)localObject2);
    if ((this.mPrivateFlags & 0x2) != 0)
    {
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append(debugIndent(paramInt));
      ((StringBuilder)localObject1).append(" FOCUSED");
      Log.d("View", ((StringBuilder)localObject1).toString());
    }
    localObject2 = debugIndent(paramInt);
    localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append((String)localObject2);
    ((StringBuilder)localObject1).append("frame={");
    ((StringBuilder)localObject1).append(this.mLeft);
    ((StringBuilder)localObject1).append(", ");
    ((StringBuilder)localObject1).append(this.mTop);
    ((StringBuilder)localObject1).append(", ");
    ((StringBuilder)localObject1).append(this.mRight);
    ((StringBuilder)localObject1).append(", ");
    ((StringBuilder)localObject1).append(this.mBottom);
    ((StringBuilder)localObject1).append("} scroll={");
    ((StringBuilder)localObject1).append(this.mScrollX);
    ((StringBuilder)localObject1).append(", ");
    ((StringBuilder)localObject1).append(this.mScrollY);
    ((StringBuilder)localObject1).append("} ");
    Log.d("View", ((StringBuilder)localObject1).toString());
    if ((this.mPaddingLeft != 0) || (this.mPaddingTop != 0) || (this.mPaddingRight != 0) || (this.mPaddingBottom != 0))
    {
      localObject2 = debugIndent(paramInt);
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append((String)localObject2);
      ((StringBuilder)localObject1).append("padding={");
      ((StringBuilder)localObject1).append(this.mPaddingLeft);
      ((StringBuilder)localObject1).append(", ");
      ((StringBuilder)localObject1).append(this.mPaddingTop);
      ((StringBuilder)localObject1).append(", ");
      ((StringBuilder)localObject1).append(this.mPaddingRight);
      ((StringBuilder)localObject1).append(", ");
      ((StringBuilder)localObject1).append(this.mPaddingBottom);
      ((StringBuilder)localObject1).append("}");
      Log.d("View", ((StringBuilder)localObject1).toString());
    }
    localObject1 = debugIndent(paramInt);
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append("mMeasureWidth=");
    ((StringBuilder)localObject2).append(this.mMeasuredWidth);
    ((StringBuilder)localObject2).append(" mMeasureHeight=");
    ((StringBuilder)localObject2).append(this.mMeasuredHeight);
    Log.d("View", ((StringBuilder)localObject2).toString());
    localObject1 = debugIndent(paramInt);
    localObject2 = this.mLayoutParams;
    if (localObject2 == null)
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append((String)localObject1);
      ((StringBuilder)localObject2).append("BAD! no layout params");
      localObject1 = ((StringBuilder)localObject2).toString();
    }
    else
    {
      localObject1 = ((ViewGroup.LayoutParams)localObject2).debug((String)localObject1);
    }
    Log.d("View", (String)localObject1);
    localObject2 = debugIndent(paramInt);
    localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append((String)localObject2);
    ((StringBuilder)localObject1).append("flags={");
    localObject1 = ((StringBuilder)localObject1).toString();
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append(printFlags(this.mViewFlags));
    localObject1 = ((StringBuilder)localObject2).toString();
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append("}");
    Log.d("View", ((StringBuilder)localObject2).toString());
    localObject2 = debugIndent(paramInt);
    localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append((String)localObject2);
    ((StringBuilder)localObject1).append("privateFlags={");
    localObject1 = ((StringBuilder)localObject1).toString();
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append(printPrivateFlags(this.mPrivateFlags));
    localObject1 = ((StringBuilder)localObject2).toString();
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append((String)localObject1);
    ((StringBuilder)localObject2).append("}");
    Log.d("View", ((StringBuilder)localObject2).toString());
  }
  
  final boolean debugDraw()
  {
    if (!DEBUG_DRAW)
    {
      AttachInfo localAttachInfo = this.mAttachInfo;
      if ((localAttachInfo == null) || (!localAttachInfo.mDebugLayout)) {
        return false;
      }
    }
    boolean bool = true;
    return bool;
  }
  
  @Deprecated
  public void destroyDrawingCache()
  {
    Bitmap localBitmap = this.mDrawingCache;
    if (localBitmap != null)
    {
      localBitmap.recycle();
      this.mDrawingCache = null;
    }
    localBitmap = this.mUnscaledDrawingCache;
    if (localBitmap != null)
    {
      localBitmap.recycle();
      this.mUnscaledDrawingCache = null;
    }
  }
  
  @UnsupportedAppUsage
  protected void destroyHardwareResources()
  {
    Object localObject = this.mOverlay;
    if (localObject != null) {
      ((ViewOverlay)localObject).getOverlayView().destroyHardwareResources();
    }
    localObject = this.mGhostView;
    if (localObject != null) {
      ((GhostView)localObject).destroyHardwareResources();
    }
  }
  
  final int dipsToPixels(int paramInt)
  {
    float f = getContext().getResources().getDisplayMetrics().density;
    return (int)(paramInt * f + 0.5F);
  }
  
  public boolean dispatchActivityResult(String paramString, int paramInt1, int paramInt2, Intent paramIntent)
  {
    String str = this.mStartActivityRequestWho;
    if ((str != null) && (str.equals(paramString)))
    {
      onActivityResult(paramInt1, paramInt2, paramIntent);
      this.mStartActivityRequestWho = null;
      return true;
    }
    return false;
  }
  
  /* Error */
  public WindowInsets dispatchApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: getfield 1582	android/view/View:mPrivateFlags3	I
    //   5: bipush 32
    //   7: ior
    //   8: putfield 1582	android/view/View:mPrivateFlags3	I
    //   11: aload_0
    //   12: getfield 2233	android/view/View:mListenerInfo	Landroid/view/View$ListenerInfo;
    //   15: ifnull +41 -> 56
    //   18: aload_0
    //   19: getfield 2233	android/view/View:mListenerInfo	Landroid/view/View$ListenerInfo;
    //   22: getfield 4304	android/view/View$ListenerInfo:mOnApplyWindowInsetsListener	Landroid/view/View$OnApplyWindowInsetsListener;
    //   25: ifnull +31 -> 56
    //   28: aload_0
    //   29: getfield 2233	android/view/View:mListenerInfo	Landroid/view/View$ListenerInfo;
    //   32: getfield 4304	android/view/View$ListenerInfo:mOnApplyWindowInsetsListener	Landroid/view/View$OnApplyWindowInsetsListener;
    //   35: aload_0
    //   36: aload_1
    //   37: invokeinterface 4308 3 0
    //   42: astore_1
    //   43: aload_0
    //   44: aload_0
    //   45: getfield 1582	android/view/View:mPrivateFlags3	I
    //   48: bipush -33
    //   50: iand
    //   51: putfield 1582	android/view/View:mPrivateFlags3	I
    //   54: aload_1
    //   55: areturn
    //   56: aload_0
    //   57: aload_1
    //   58: invokevirtual 4310	android/view/View:onApplyWindowInsets	(Landroid/view/WindowInsets;)Landroid/view/WindowInsets;
    //   61: astore_1
    //   62: aload_0
    //   63: aload_0
    //   64: getfield 1582	android/view/View:mPrivateFlags3	I
    //   67: bipush -33
    //   69: iand
    //   70: putfield 1582	android/view/View:mPrivateFlags3	I
    //   73: aload_1
    //   74: areturn
    //   75: astore_1
    //   76: aload_0
    //   77: aload_0
    //   78: getfield 1582	android/view/View:mPrivateFlags3	I
    //   81: bipush -33
    //   83: iand
    //   84: putfield 1582	android/view/View:mPrivateFlags3	I
    //   87: aload_1
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	View
    //   0	89	1	paramWindowInsets	WindowInsets
    // Exception table:
    //   from	to	target	type
    //   0	43	75	finally
    //   56	62	75	finally
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  void dispatchAttachedToWindow(AttachInfo paramAttachInfo, int paramInt)
  {
    this.mAttachInfo = paramAttachInfo;
    notifyConfirmedWebView(true);
    Object localObject1 = this.mOverlay;
    if (localObject1 != null) {
      ((ViewOverlay)localObject1).getOverlayView().dispatchAttachedToWindow(paramAttachInfo, paramInt);
    }
    this.mWindowAttachCount += 1;
    this.mPrivateFlags |= 0x400;
    Object localObject2 = this.mFloatingTreeObserver;
    localObject1 = null;
    if (localObject2 != null)
    {
      paramAttachInfo.mTreeObserver.merge(this.mFloatingTreeObserver);
      this.mFloatingTreeObserver = null;
    }
    registerPendingFrameMetricsObservers();
    if ((this.mPrivateFlags & 0x80000) != 0)
    {
      this.mAttachInfo.mScrollContainers.add(this);
      this.mPrivateFlags |= 0x100000;
    }
    localObject2 = this.mRunQueue;
    if (localObject2 != null)
    {
      ((HandlerActionQueue)localObject2).executeActions(paramAttachInfo.mHandler);
      this.mRunQueue = null;
    }
    performCollectViewAttributes(this.mAttachInfo, paramInt);
    onAttachedToWindow();
    localObject2 = this.mListenerInfo;
    if (localObject2 != null) {
      localObject1 = ((ListenerInfo)localObject2).mOnAttachStateChangeListeners;
    }
    if ((localObject1 != null) && (((CopyOnWriteArrayList)localObject1).size() > 0))
    {
      localObject1 = ((CopyOnWriteArrayList)localObject1).iterator();
      while (((Iterator)localObject1).hasNext()) {
        ((OnAttachStateChangeListener)((Iterator)localObject1).next()).onViewAttachedToWindow(this);
      }
    }
    int i = paramAttachInfo.mWindowVisibility;
    if (i != 8)
    {
      onWindowVisibilityChanged(i);
      if (isShown())
      {
        boolean bool;
        if (i == 0) {
          bool = true;
        } else {
          bool = false;
        }
        onVisibilityAggregated(bool);
      }
    }
    onVisibilityChanged(this, paramInt);
    if ((this.mPrivateFlags & 0x400) != 0) {
      refreshDrawableState();
    }
    needGlobalAttributesUpdate(false);
    notifyEnterOrExitForAutoFillIfNeeded(true);
  }
  
  void dispatchCancelPendingInputEvents()
  {
    this.mPrivateFlags3 &= 0xFFFFFFEF;
    onCancelPendingInputEvents();
    if ((this.mPrivateFlags3 & 0x10) == 16) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("View ");
    localStringBuilder.append(getClass().getSimpleName());
    localStringBuilder.append(" did not call through to super.onCancelPendingInputEvents()");
    throw new SuperNotCalledException(localStringBuilder.toString());
  }
  
  public boolean dispatchCapturedPointerEvent(MotionEvent paramMotionEvent)
  {
    if (!hasPointerCapture()) {
      return false;
    }
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mOnCapturedPointerListener != null) && (localListenerInfo.mOnCapturedPointerListener.onCapturedPointer(this, paramMotionEvent))) {
      return true;
    }
    return onCapturedPointerEvent(paramMotionEvent);
  }
  
  void dispatchCollectViewAttributes(AttachInfo paramAttachInfo, int paramInt)
  {
    performCollectViewAttributes(paramAttachInfo, paramInt);
  }
  
  public void dispatchConfigurationChanged(Configuration paramConfiguration)
  {
    onConfigurationChanged(paramConfiguration);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  void dispatchDetachedFromWindow()
  {
    notifyConfirmedWebView(false);
    Object localObject = this.mAttachInfo;
    if ((localObject != null) && (((AttachInfo)localObject).mWindowVisibility != 8))
    {
      onWindowVisibilityChanged(8);
      if (isShown()) {
        onVisibilityAggregated(false);
      }
    }
    onDetachedFromWindow();
    onDetachedFromWindowInternal();
    localObject = (InputMethodManager)getContext().getSystemService(InputMethodManager.class);
    if (localObject != null) {
      ((InputMethodManager)localObject).onViewDetachedFromWindow(this);
    }
    localObject = this.mListenerInfo;
    if (localObject != null) {
      localObject = ((ListenerInfo)localObject).mOnAttachStateChangeListeners;
    } else {
      localObject = null;
    }
    if ((localObject != null) && (((CopyOnWriteArrayList)localObject).size() > 0))
    {
      localObject = ((CopyOnWriteArrayList)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((OnAttachStateChangeListener)((Iterator)localObject).next()).onViewDetachedFromWindow(this);
      }
    }
    if ((this.mPrivateFlags & 0x100000) != 0)
    {
      this.mAttachInfo.mScrollContainers.remove(this);
      this.mPrivateFlags &= 0xFFEFFFFF;
    }
    this.mAttachInfo = null;
    localObject = this.mOverlay;
    if (localObject != null) {
      ((ViewOverlay)localObject).getOverlayView().dispatchDetachedFromWindow();
    }
    notifyEnterOrExitForAutoFillIfNeeded(false);
  }
  
  public void dispatchDisplayHint(int paramInt)
  {
    onDisplayHint(paramInt);
  }
  
  boolean dispatchDragEnterExitInPreN(DragEvent paramDragEvent)
  {
    return callDragEventHandler(paramDragEvent);
  }
  
  public boolean dispatchDragEvent(DragEvent paramDragEvent)
  {
    paramDragEvent.mEventHandlerWasCalled = true;
    if ((paramDragEvent.mAction == 2) || (paramDragEvent.mAction == 3)) {
      getViewRootImpl().setDragFocus(this, paramDragEvent);
    }
    return callDragEventHandler(paramDragEvent);
  }
  
  protected void dispatchDraw(Canvas paramCanvas) {}
  
  public void dispatchDrawableHotspotChanged(float paramFloat1, float paramFloat2) {}
  
  public void dispatchFinishTemporaryDetach()
  {
    this.mPrivateFlags3 &= 0xFDFFFFFF;
    onFinishTemporaryDetach();
    if ((hasWindowFocus()) && (hasFocus())) {
      notifyFocusChangeToInputMethodManager(true);
    }
    notifyEnterOrExitForAutoFillIfNeeded(true);
  }
  
  protected boolean dispatchGenericFocusedEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean dispatchGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    InputEventConsistencyVerifier localInputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
    if (localInputEventConsistencyVerifier != null) {
      localInputEventConsistencyVerifier.onGenericMotionEvent(paramMotionEvent, 0);
    }
    if ((paramMotionEvent.getSource() & 0x2) != 0)
    {
      int i = paramMotionEvent.getAction();
      if ((i != 9) && (i != 7) && (i != 10))
      {
        if (dispatchGenericPointerEvent(paramMotionEvent)) {
          return true;
        }
      }
      else if (dispatchHoverEvent(paramMotionEvent)) {
        return true;
      }
    }
    else if (dispatchGenericFocusedEvent(paramMotionEvent))
    {
      return true;
    }
    if (dispatchGenericMotionEventInternal(paramMotionEvent)) {
      return true;
    }
    localInputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
    if (localInputEventConsistencyVerifier != null) {
      localInputEventConsistencyVerifier.onUnhandledEvent(paramMotionEvent, 0);
    }
    return false;
  }
  
  protected boolean dispatchGenericPointerEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  protected void dispatchGetDisplayList() {}
  
  protected boolean dispatchHoverEvent(MotionEvent paramMotionEvent)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mOnHoverListener != null) && ((this.mViewFlags & 0x20) == 0) && (localListenerInfo.mOnHoverListener.onHover(this, paramMotionEvent))) {
      return true;
    }
    return onHoverEvent(paramMotionEvent);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    Object localObject = this.mInputEventConsistencyVerifier;
    if (localObject != null) {
      ((InputEventConsistencyVerifier)localObject).onKeyEvent(paramKeyEvent, 0);
    }
    localObject = this.mListenerInfo;
    if ((localObject != null) && (((ListenerInfo)localObject).mOnKeyListener != null) && ((this.mViewFlags & 0x20) == 0) && (((ListenerInfo)localObject).mOnKeyListener.onKey(this, paramKeyEvent.getKeyCode(), paramKeyEvent))) {
      return true;
    }
    localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mKeyDispatchState;
    } else {
      localObject = null;
    }
    if (paramKeyEvent.dispatch(this, (KeyEvent.DispatcherState)localObject, this)) {
      return true;
    }
    localObject = this.mInputEventConsistencyVerifier;
    if (localObject != null) {
      ((InputEventConsistencyVerifier)localObject).onUnhandledEvent(paramKeyEvent, 0);
    }
    return false;
  }
  
  public boolean dispatchKeyEventPreIme(KeyEvent paramKeyEvent)
  {
    return onKeyPreIme(paramKeyEvent.getKeyCode(), paramKeyEvent);
  }
  
  public boolean dispatchKeyShortcutEvent(KeyEvent paramKeyEvent)
  {
    return onKeyShortcut(paramKeyEvent.getKeyCode(), paramKeyEvent);
  }
  
  void dispatchMovedToDisplay(Display paramDisplay, Configuration paramConfiguration)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    localAttachInfo.mDisplay = paramDisplay;
    localAttachInfo.mDisplayState = paramDisplay.getState();
    onMovedToDisplay(paramDisplay.getDisplayId(), paramConfiguration);
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (isNestedScrollingEnabled())
    {
      ViewParent localViewParent = this.mNestedScrollingParent;
      if (localViewParent != null) {
        return localViewParent.onNestedFling(this, paramFloat1, paramFloat2, paramBoolean);
      }
    }
    return false;
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    if (isNestedScrollingEnabled())
    {
      ViewParent localViewParent = this.mNestedScrollingParent;
      if (localViewParent != null) {
        return localViewParent.onNestedPreFling(this, paramFloat1, paramFloat2);
      }
    }
    return false;
  }
  
  public boolean dispatchNestedPrePerformAccessibilityAction(int paramInt, Bundle paramBundle)
  {
    for (ViewParent localViewParent = getParent(); localViewParent != null; localViewParent = localViewParent.getParent()) {
      if (localViewParent.onNestedPrePerformAccessibilityAction(this, paramInt, paramBundle)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if ((isNestedScrollingEnabled()) && (this.mNestedScrollingParent != null))
    {
      boolean bool1 = true;
      if ((paramInt1 == 0) && (paramInt2 == 0))
      {
        if (paramArrayOfInt2 != null)
        {
          paramArrayOfInt2[0] = 0;
          paramArrayOfInt2[1] = 0;
        }
      }
      else
      {
        int i = 0;
        int j = 0;
        if (paramArrayOfInt2 != null)
        {
          getLocationInWindow(paramArrayOfInt2);
          i = paramArrayOfInt2[0];
          j = paramArrayOfInt2[1];
        }
        int[] arrayOfInt = paramArrayOfInt1;
        if (paramArrayOfInt1 == null)
        {
          if (this.mTempNestedScrollConsumed == null) {
            this.mTempNestedScrollConsumed = new int[2];
          }
          arrayOfInt = this.mTempNestedScrollConsumed;
        }
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 0;
        this.mNestedScrollingParent.onNestedPreScroll(this, paramInt1, paramInt2, arrayOfInt);
        if (paramArrayOfInt2 != null)
        {
          getLocationInWindow(paramArrayOfInt2);
          paramArrayOfInt2[0] -= i;
          paramArrayOfInt2[1] -= j;
        }
        boolean bool2 = bool1;
        if (arrayOfInt[0] == 0) {
          if (arrayOfInt[1] != 0) {
            bool2 = bool1;
          } else {
            bool2 = false;
          }
        }
        return bool2;
      }
    }
    return false;
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    if ((isNestedScrollingEnabled()) && (this.mNestedScrollingParent != null)) {
      if ((paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == 0) && (paramInt4 == 0))
      {
        if (paramArrayOfInt != null)
        {
          paramArrayOfInt[0] = 0;
          paramArrayOfInt[1] = 0;
        }
      }
      else
      {
        int i = 0;
        int j = 0;
        if (paramArrayOfInt != null)
        {
          getLocationInWindow(paramArrayOfInt);
          i = paramArrayOfInt[0];
          j = paramArrayOfInt[1];
        }
        this.mNestedScrollingParent.onNestedScroll(this, paramInt1, paramInt2, paramInt3, paramInt4);
        if (paramArrayOfInt != null)
        {
          getLocationInWindow(paramArrayOfInt);
          paramArrayOfInt[0] -= i;
          paramArrayOfInt[1] -= j;
        }
        return true;
      }
    }
    return false;
  }
  
  public void dispatchPointerCaptureChanged(boolean paramBoolean)
  {
    onPointerCaptureChange(paramBoolean);
  }
  
  @UnsupportedAppUsage
  public final boolean dispatchPointerEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.isTouchEvent())
    {
      dispatchTouchEventToContentCatcher(paramMotionEvent);
      return dispatchTouchEvent(paramMotionEvent);
    }
    return dispatchGenericMotionEvent(paramMotionEvent);
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      return localAccessibilityDelegate.dispatchPopulateAccessibilityEvent(this, paramAccessibilityEvent);
    }
    return dispatchPopulateAccessibilityEventInternal(paramAccessibilityEvent);
  }
  
  public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    onPopulateAccessibilityEvent(paramAccessibilityEvent);
    return false;
  }
  
  public void dispatchProvideAutofillStructure(ViewStructure paramViewStructure, int paramInt)
  {
    dispatchProvideStructure(paramViewStructure, 1, paramInt);
  }
  
  public void dispatchProvideStructure(ViewStructure paramViewStructure)
  {
    dispatchProvideStructure(paramViewStructure, 0, 0);
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    int i = this.mID;
    if (i != -1)
    {
      paramSparseArray = (Parcelable)paramSparseArray.get(i);
      if (paramSparseArray != null)
      {
        this.mPrivateFlags &= 0xFFFDFFFF;
        onRestoreInstanceState(paramSparseArray);
        if ((this.mPrivateFlags & 0x20000) == 0) {
          throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
        }
      }
    }
  }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    if ((this.mID != -1) && ((this.mViewFlags & 0x10000) == 0))
    {
      this.mPrivateFlags &= 0xFFFDFFFF;
      Parcelable localParcelable = onSaveInstanceState();
      if ((this.mPrivateFlags & 0x20000) != 0)
      {
        if (localParcelable != null) {
          paramSparseArray.put(this.mID, localParcelable);
        }
      }
      else {
        throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
      }
    }
  }
  
  void dispatchScreenStateChanged(int paramInt)
  {
    onScreenStateChanged(paramInt);
  }
  
  protected void dispatchSetActivated(boolean paramBoolean) {}
  
  protected void dispatchSetPressed(boolean paramBoolean) {}
  
  protected void dispatchSetSelected(boolean paramBoolean) {}
  
  public void dispatchStartTemporaryDetach()
  {
    this.mPrivateFlags3 |= 0x2000000;
    notifyEnterOrExitForAutoFillIfNeeded(false);
    onStartTemporaryDetach();
  }
  
  public void dispatchSystemUiVisibilityChanged(int paramInt)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mOnSystemUiVisibilityChangeListener != null)) {
      localListenerInfo.mOnSystemUiVisibilityChangeListener.onSystemUiVisibilityChange(paramInt & 0x3FF7);
    }
  }
  
  boolean dispatchTooltipHoverEvent(MotionEvent paramMotionEvent)
  {
    if (this.mTooltipInfo == null) {
      return false;
    }
    int i = paramMotionEvent.getAction();
    if (i != 7)
    {
      if (i == 10)
      {
        this.mTooltipInfo.clearAnchorPos();
        if (!this.mTooltipInfo.mTooltipFromLongClick) {
          hideTooltip();
        }
      }
    }
    else {
      if ((this.mViewFlags & 0x40000000) == 1073741824) {
        break label69;
      }
    }
    return false;
    label69:
    if ((!this.mTooltipInfo.mTooltipFromLongClick) && (this.mTooltipInfo.updateAnchorPos(paramMotionEvent)))
    {
      if (this.mTooltipInfo.mTooltipPopup == null)
      {
        removeCallbacks(this.mTooltipInfo.mShowTooltipRunnable);
        postDelayed(this.mTooltipInfo.mShowTooltipRunnable, ViewConfiguration.getHoverTooltipShowTimeout());
      }
      if ((getWindowSystemUiVisibility() & 0x1) == 1) {
        i = ViewConfiguration.getHoverTooltipHideShortTimeout();
      } else {
        i = ViewConfiguration.getHoverTooltipHideTimeout();
      }
      removeCallbacks(this.mTooltipInfo.mHideTooltipRunnable);
      postDelayed(this.mTooltipInfo.mHideTooltipRunnable, i);
    }
    return true;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.isTargetAccessibilityFocus())
    {
      if (!isAccessibilityFocusedViewOrHost()) {
        return false;
      }
      paramMotionEvent.setTargetAccessibilityFocus(false);
    }
    boolean bool1 = false;
    boolean bool2 = false;
    Object localObject = this.mInputEventConsistencyVerifier;
    if (localObject != null) {
      ((InputEventConsistencyVerifier)localObject).onTouchEvent(paramMotionEvent, 0);
    }
    int i = paramMotionEvent.getActionMasked();
    if (i == 0)
    {
      SeempLog.record(3);
      stopNestedScroll();
    }
    if (onFilterTouchEventForSecurity(paramMotionEvent))
    {
      bool1 = bool2;
      if ((this.mViewFlags & 0x20) == 0)
      {
        bool1 = bool2;
        if (handleScrollBarDragging(paramMotionEvent)) {
          bool1 = true;
        }
      }
      localObject = this.mListenerInfo;
      bool2 = bool1;
      if (localObject != null)
      {
        bool2 = bool1;
        if (((ListenerInfo)localObject).mOnTouchListener != null)
        {
          bool2 = bool1;
          if ((this.mViewFlags & 0x20) == 0)
          {
            bool2 = bool1;
            if (((ListenerInfo)localObject).mOnTouchListener.onTouch(this, paramMotionEvent)) {
              bool2 = true;
            }
          }
        }
      }
      bool1 = bool2;
      if (!bool2)
      {
        bool1 = bool2;
        if (onTouchEvent(paramMotionEvent)) {
          bool1 = true;
        }
      }
    }
    if (!bool1)
    {
      localObject = this.mInputEventConsistencyVerifier;
      if (localObject != null) {
        ((InputEventConsistencyVerifier)localObject).onUnhandledEvent(paramMotionEvent, 0);
      }
    }
    if ((i == 1) || (i == 3) || ((i == 0) && (!bool1))) {
      stopNestedScroll();
    }
    return bool1;
  }
  
  public final void dispatchTouchEventToContentCatcher(MotionEvent paramMotionEvent)
  {
    Activity localActivity = getAttachedActivityInstance();
    if (localActivity != null)
    {
      IInterceptor localIInterceptor = localActivity.getInterceptor();
      if (localIInterceptor != null) {
        localIInterceptor.dispatchTouchEvent(paramMotionEvent, this, localActivity);
      }
    }
  }
  
  public boolean dispatchTrackballEvent(MotionEvent paramMotionEvent)
  {
    InputEventConsistencyVerifier localInputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
    if (localInputEventConsistencyVerifier != null) {
      localInputEventConsistencyVerifier.onTrackballEvent(paramMotionEvent, 0);
    }
    return onTrackballEvent(paramMotionEvent);
  }
  
  View dispatchUnhandledKeyEvent(KeyEvent paramKeyEvent)
  {
    if (onUnhandledKeyEvent(paramKeyEvent)) {
      return this;
    }
    return null;
  }
  
  public boolean dispatchUnhandledMove(View paramView, int paramInt)
  {
    return false;
  }
  
  boolean dispatchVisibilityAggregated(boolean paramBoolean)
  {
    int i = getVisibility();
    boolean bool = true;
    if (i == 0) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i != 0) || (!paramBoolean)) {
      onVisibilityAggregated(paramBoolean);
    }
    if ((i != 0) && (paramBoolean)) {
      paramBoolean = bool;
    } else {
      paramBoolean = false;
    }
    return paramBoolean;
  }
  
  protected void dispatchVisibilityChanged(View paramView, int paramInt)
  {
    onVisibilityChanged(paramView, paramInt);
  }
  
  public void dispatchWindowFocusChanged(boolean paramBoolean)
  {
    onWindowFocusChanged(paramBoolean);
  }
  
  void dispatchWindowInsetsAnimationFinished(WindowInsetsAnimationListener.InsetsAnimation paramInsetsAnimation)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mWindowInsetsAnimationListener != null)) {
      this.mListenerInfo.mWindowInsetsAnimationListener.onFinished(paramInsetsAnimation);
    }
  }
  
  WindowInsets dispatchWindowInsetsAnimationProgress(WindowInsets paramWindowInsets)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mWindowInsetsAnimationListener != null)) {
      return this.mListenerInfo.mWindowInsetsAnimationListener.onProgress(paramWindowInsets);
    }
    return paramWindowInsets;
  }
  
  void dispatchWindowInsetsAnimationStarted(WindowInsetsAnimationListener.InsetsAnimation paramInsetsAnimation)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mWindowInsetsAnimationListener != null)) {
      this.mListenerInfo.mWindowInsetsAnimationListener.onStarted(paramInsetsAnimation);
    }
  }
  
  public void dispatchWindowSystemUiVisiblityChanged(int paramInt)
  {
    onWindowSystemUiVisibilityChanged(paramInt);
  }
  
  public void dispatchWindowVisibilityChanged(int paramInt)
  {
    onWindowVisibilityChanged(paramInt);
  }
  
  public void draw(Canvas paramCanvas)
  {
    ForceDarkHelper.getInstance().updateForceDarkForCanvas(this.mForceDark, paramCanvas);
    this.mPrivateFlags = (0xFFDFFFFF & this.mPrivateFlags | 0x20);
    drawBackground(paramCanvas);
    int i = this.mViewFlags;
    if ((i & 0x1000) != 0) {
      j = 1;
    } else {
      j = 0;
    }
    if ((i & 0x2000) != 0) {
      k = 1;
    } else {
      k = 0;
    }
    if ((k == 0) && (j == 0))
    {
      onDraw(paramCanvas);
      dispatchDraw(paramCanvas);
      drawAutofilledHighlight(paramCanvas);
      localObject1 = this.mOverlay;
      if ((localObject1 != null) && (!((ViewOverlay)localObject1).isEmpty())) {
        this.mOverlay.getOverlayView().dispatchDraw(paramCanvas);
      }
      onDrawForeground(paramCanvas);
      drawDefaultFocusHighlight(paramCanvas);
      if (debugDraw()) {
        debugDrawFocus(paramCanvas);
      }
      ForceDarkHelper.getInstance().updateForceDarkForRenderNode(this.mRenderNode, paramCanvas);
      return;
    }
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    i = this.mPaddingLeft;
    boolean bool = isPaddingOffsetRequired();
    if (bool) {
      i += getLeftPaddingOffset();
    }
    int m = this.mScrollX + i;
    int n = this.mRight;
    int i1 = 0;
    n = n + m - this.mLeft - this.mPaddingRight - i;
    int i2 = this.mScrollY + getFadeTop(bool);
    int i3 = i2 + getFadeHeight(bool);
    if (bool)
    {
      i4 = getRightPaddingOffset();
      i = getBottomPaddingOffset();
      n += i4;
      i3 += i;
    }
    Object localObject2 = this.mScrollCache;
    int i5 = 0;
    float f4 = ((ScrollabilityCache)localObject2).fadingEdgeLength;
    int i6 = 0;
    int i4 = (int)f4;
    if (k != 0)
    {
      i = i4;
      if (i2 + i4 > i3 - i4) {
        i = (i3 - i2) / 2;
      }
    }
    else
    {
      i = i4;
    }
    float f5 = 0.0F;
    int i7 = 0;
    if ((j != 0) && (m + i > n - i)) {
      i4 = (n - m) / 2;
    } else {
      i4 = i;
    }
    if (k != 0)
    {
      f5 = Math.max(0.0F, Math.min(1.0F, getTopFadingEdgeStrength()));
      if (f5 * f4 > 1.0F) {
        i = 1;
      } else {
        i = 0;
      }
      i1 = i;
      f1 = Math.max(0.0F, Math.min(1.0F, getBottomFadingEdgeStrength()));
      if (f1 * f4 > 1.0F) {
        i = 1;
      } else {
        i = 0;
      }
      i5 = i;
    }
    if (j != 0)
    {
      f2 = Math.max(0.0F, Math.min(1.0F, getLeftFadingEdgeStrength()));
      if (f2 * f4 > 1.0F) {
        i = 1;
      } else {
        i = 0;
      }
      f3 = Math.max(0.0F, Math.min(1.0F, getRightFadingEdgeStrength()));
      if (f3 * f4 > 1.0F) {
        j = 1;
      } else {
        j = 0;
      }
      i6 = i;
      i7 = j;
    }
    int i8 = paramCanvas.getSaveCount();
    int k = -1;
    i = -1;
    int j = -1;
    int i9 = getSolidColor();
    int i10;
    if (i9 == 0)
    {
      if (i1 != 0) {
        k = paramCanvas.saveUnclippedLayer(m, i2, n, i2 + i4);
      }
      if (i5 != 0) {
        i = paramCanvas.saveUnclippedLayer(m, i3 - i4, n, i3);
      }
      if (i6 != 0) {
        j = paramCanvas.saveUnclippedLayer(m, i2, m + i4, i3);
      }
      int i11;
      if (i7 != 0)
      {
        i10 = paramCanvas.saveUnclippedLayer(n - i4, i2, n, i3);
        i11 = k;
        k = j;
        j = i11;
      }
      else
      {
        i10 = j;
        i11 = -1;
        j = k;
        k = i10;
        i10 = i11;
      }
    }
    else
    {
      ((ScrollabilityCache)localObject2).setFadeColor(i9);
      i = -1;
      k = -1;
      i10 = -1;
      j = -1;
    }
    onDraw(paramCanvas);
    dispatchDraw(paramCanvas);
    Paint localPaint = ((ScrollabilityCache)localObject2).paint;
    Object localObject1 = ((ScrollabilityCache)localObject2).matrix;
    localObject2 = ((ScrollabilityCache)localObject2).shader;
    if (i7 != 0)
    {
      ((Matrix)localObject1).setScale(1.0F, f4 * f3);
      ((Matrix)localObject1).postRotate(90.0F);
      ((Matrix)localObject1).postTranslate(n, i2);
      ((Shader)localObject2).setLocalMatrix((Matrix)localObject1);
      localPaint.setShader((Shader)localObject2);
      if (i9 == 0) {
        paramCanvas.restoreUnclippedLayer(i10, localPaint);
      } else {
        paramCanvas.drawRect(n - i4, i2, n, i3, localPaint);
      }
    }
    if (i6 != 0)
    {
      ((Matrix)localObject1).setScale(1.0F, f4 * f2);
      ((Matrix)localObject1).postRotate(-90.0F);
      ((Matrix)localObject1).postTranslate(m, i2);
      ((Shader)localObject2).setLocalMatrix((Matrix)localObject1);
      localPaint.setShader((Shader)localObject2);
      if (i9 == 0) {
        paramCanvas.restoreUnclippedLayer(k, localPaint);
      } else {
        paramCanvas.drawRect(m, i2, m + i4, i3, localPaint);
      }
    }
    if (i5 != 0)
    {
      ((Matrix)localObject1).setScale(1.0F, f4 * f1);
      ((Matrix)localObject1).postRotate(180.0F);
      ((Matrix)localObject1).postTranslate(m, i3);
      ((Shader)localObject2).setLocalMatrix((Matrix)localObject1);
      localPaint.setShader((Shader)localObject2);
      if (i9 == 0) {
        paramCanvas.restoreUnclippedLayer(i, localPaint);
      } else {
        paramCanvas.drawRect(m, i3 - i4, n, i3, localPaint);
      }
    }
    if (i1 != 0)
    {
      ((Matrix)localObject1).setScale(1.0F, f4 * f5);
      ((Matrix)localObject1).postTranslate(m, i2);
      ((Shader)localObject2).setLocalMatrix((Matrix)localObject1);
      localPaint.setShader((Shader)localObject2);
      if (i9 == 0) {
        paramCanvas.restoreUnclippedLayer(j, localPaint);
      } else {
        paramCanvas.drawRect(m, i2, n, i2 + i4, localPaint);
      }
    }
    paramCanvas.restoreToCount(i8);
    drawAutofilledHighlight(paramCanvas);
    localObject1 = this.mOverlay;
    if ((localObject1 != null) && (!((ViewOverlay)localObject1).isEmpty())) {
      this.mOverlay.getOverlayView().dispatchDraw(paramCanvas);
    }
    onDrawForeground(paramCanvas);
    if (debugDraw()) {
      debugDrawFocus(paramCanvas);
    }
    ForceDarkHelper.getInstance().updateForceDarkForRenderNode(this.mRenderNode, paramCanvas);
  }
  
  boolean draw(Canvas paramCanvas, ViewGroup paramViewGroup, long paramLong)
  {
    boolean bool1 = paramCanvas.isHardwareAccelerated();
    Object localObject1 = this.mAttachInfo;
    if ((localObject1 != null) && (((AttachInfo)localObject1).mHardwareAccelerated) && (bool1)) {
      i = 1;
    } else {
      i = 0;
    }
    int j = i;
    boolean bool2 = false;
    boolean bool3 = hasIdentityMatrix();
    int k = paramViewGroup.mGroupFlags;
    if ((k & 0x100) != 0)
    {
      paramViewGroup.getChildTransformation().clear();
      paramViewGroup.mGroupFlags &= 0xFEFF;
    }
    localObject1 = null;
    boolean bool4 = false;
    Object localObject2 = this.mAttachInfo;
    boolean bool5;
    if ((localObject2 != null) && (((AttachInfo)localObject2).mScalingRequired)) {
      bool5 = true;
    } else {
      bool5 = false;
    }
    Animation localAnimation = getAnimation();
    boolean bool6;
    boolean bool7;
    Object localObject4;
    if (localAnimation != null)
    {
      bool6 = applyLegacyAnimation(paramViewGroup, paramLong, localAnimation, bool5);
      bool7 = localAnimation.willChangeTransformationMatrix();
      if (bool7) {
        this.mPrivateFlags3 |= 0x1;
      }
      localObject2 = paramViewGroup.getChildTransformation();
    }
    else
    {
      i = this.mPrivateFlags3;
      Object localObject3 = null;
      if ((i & 0x1) != 0)
      {
        this.mRenderNode.setAnimationMatrix(null);
        this.mPrivateFlags3 &= 0xFFFFFFFE;
      }
      bool6 = bool2;
      localObject2 = localObject1;
      bool7 = bool4;
      if (j == 0)
      {
        bool6 = bool2;
        localObject2 = localObject1;
        bool7 = bool4;
        if ((k & 0x800) != 0)
        {
          localObject4 = paramViewGroup.getChildTransformation();
          bool6 = bool2;
          localObject2 = localObject1;
          bool7 = bool4;
          if (paramViewGroup.getChildStaticTransformation(this, (Transformation)localObject4))
          {
            i = ((Transformation)localObject4).getTransformationType();
            localObject1 = localObject3;
            if (i != 0) {
              localObject1 = localObject4;
            }
            if ((i & 0x2) != 0) {
              bool6 = true;
            } else {
              bool6 = false;
            }
            bool7 = bool6;
            localObject2 = localObject1;
            bool6 = bool2;
          }
        }
      }
    }
    int m = bool7 | bool3 ^ true;
    this.mPrivateFlags |= 0x20;
    if ((m == 0) && ((k & 0x801) == 1) && (paramCanvas.quickReject(this.mLeft, this.mTop, this.mRight, this.mBottom, Canvas.EdgeType.BW)) && ((this.mPrivateFlags & 0x40) == 0))
    {
      this.mPrivateFlags2 |= 0x10000000;
      return bool6;
    }
    this.mPrivateFlags2 &= 0xEFFFFFFF;
    if (bool1)
    {
      if ((this.mPrivateFlags & 0x80000000) != 0) {
        bool7 = true;
      } else {
        bool7 = false;
      }
      this.mRecreateDisplayList = bool7;
      this.mPrivateFlags &= 0x7FFFFFFF;
    }
    int n = getLayerType();
    int i1;
    if ((n != 1) && (j != 0))
    {
      localObject1 = null;
      i1 = n;
    }
    else
    {
      i = n;
      if (n != 0)
      {
        i = 1;
        buildDrawingCache(true);
      }
      localObject1 = getDrawingCache(true);
      i1 = i;
    }
    if (j != 0)
    {
      localObject4 = updateDisplayListIfDirty();
      if (!((RenderNode)localObject4).hasDisplayList())
      {
        j = 0;
        localObject4 = null;
      }
    }
    else
    {
      localObject4 = null;
    }
    int i2;
    if (j == 0)
    {
      computeScroll();
      i2 = this.mScrollX;
      n = this.mScrollY;
    }
    else
    {
      i2 = 0;
      n = 0;
    }
    int i3;
    if ((localObject1 != null) && (j == 0)) {
      i3 = 1;
    } else {
      i3 = 0;
    }
    int i4;
    if ((localObject1 == null) && (j == 0)) {
      i4 = 1;
    } else {
      i4 = 0;
    }
    int i = -1;
    if ((j == 0) || (localObject2 != null)) {
      i = paramCanvas.save();
    }
    float f1;
    if (i4 != 0)
    {
      paramCanvas.translate(this.mLeft - i2, this.mTop - n);
    }
    else
    {
      if (j == 0) {
        paramCanvas.translate(this.mLeft, this.mTop);
      }
      if (bool5)
      {
        if (j != 0) {
          i = paramCanvas.save();
        }
        f1 = 1.0F / this.mAttachInfo.mApplicationScale;
        paramCanvas.scale(f1, f1);
      }
    }
    if (j != 0) {
      f1 = 1.0F;
    } else {
      f1 = getAlpha() * getTransitionAlpha();
    }
    if ((localObject2 == null) && (f1 >= 1.0F) && (hasIdentityMatrix()) && ((this.mPrivateFlags3 & 0x2) == 0))
    {
      if ((this.mPrivateFlags & 0x40000) == 262144)
      {
        onSetAlpha(255);
        this.mPrivateFlags &= 0xFFFBFFFF;
      }
    }
    else
    {
      if ((localObject2 == null) && (bool3)) {
        break label1026;
      }
      int i5 = 0;
      int i6;
      if (i4 != 0)
      {
        i5 = -i2;
        i6 = -n;
      }
      else
      {
        i6 = 0;
      }
      float f3;
      if (localObject2 != null)
      {
        if (m != 0)
        {
          if (j != 0)
          {
            ((RenderNode)localObject4).setAnimationMatrix(((Transformation)localObject2).getMatrix());
          }
          else
          {
            paramCanvas.translate(-i5, -i6);
            paramCanvas.concat(((Transformation)localObject2).getMatrix());
            paramCanvas.translate(i5, i6);
          }
          paramViewGroup.mGroupFlags |= 0x100;
        }
        float f2 = ((Transformation)localObject2).getAlpha();
        f3 = f1;
        if (f2 < 1.0F)
        {
          f3 = f1 * f2;
          paramViewGroup.mGroupFlags |= 0x100;
        }
      }
      else
      {
        f3 = f1;
      }
      if ((!bool3) && (j == 0))
      {
        paramCanvas.translate(-i5, -i6);
        paramCanvas.concat(getMatrix());
        paramCanvas.translate(i5, i6);
      }
      f1 = f3;
      label1026:
      if ((f1 >= 1.0F) && ((this.mPrivateFlags3 & 0x2) == 0)) {
        break label1195;
      }
      if (f1 < 1.0F) {
        this.mPrivateFlags3 |= 0x2;
      } else {
        this.mPrivateFlags3 &= 0xFFFFFFFD;
      }
      paramViewGroup.mGroupFlags |= 0x100;
      if (i3 == 0)
      {
        i6 = (int)(f1 * 255.0F);
        if (!onSetAlpha(i6))
        {
          if (j != 0) {
            ((RenderNode)localObject4).setAlpha(getAlpha() * f1 * getTransitionAlpha());
          } else if (i1 == 0) {
            paramCanvas.saveLayerAlpha(i2, n, getWidth() + i2, n + getHeight(), i6);
          }
        }
        else {
          this.mPrivateFlags |= 0x40000;
        }
      }
    }
    label1195:
    if (j == 0)
    {
      if (((k & 0x1) != 0) && (localObject1 == null)) {
        if (i4 != 0) {
          paramCanvas.clipRect(i2, n, i2 + getWidth(), n + getHeight());
        } else if ((bool5) && (localObject1 != null)) {
          paramCanvas.clipRect(0, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight());
        } else {
          paramCanvas.clipRect(0, 0, getWidth(), getHeight());
        }
      }
      localObject2 = this.mClipBounds;
      if (localObject2 != null) {
        paramCanvas.clipRect((Rect)localObject2);
      }
    }
    if (i3 == 0)
    {
      if (j != 0)
      {
        this.mPrivateFlags = (0xFFDFFFFF & this.mPrivateFlags);
        ((RecordingCanvas)paramCanvas).drawRenderNode((RenderNode)localObject4);
      }
      else
      {
        n = this.mPrivateFlags;
        if ((n & 0x80) == 128)
        {
          this.mPrivateFlags = (0xFFDFFFFF & n);
          dispatchDraw(paramCanvas);
        }
        else
        {
          draw(paramCanvas);
        }
      }
    }
    else if (localObject1 != null)
    {
      this.mPrivateFlags = (0xFFDFFFFF & this.mPrivateFlags);
      if (i1 != 0)
      {
        localObject2 = this.mLayerPaint;
        if (localObject2 != null)
        {
          n = ((Paint)localObject2).getAlpha();
          if (f1 < 1.0F) {
            this.mLayerPaint.setAlpha((int)(n * f1));
          }
          paramCanvas.drawBitmap((Bitmap)localObject1, 0.0F, 0.0F, this.mLayerPaint);
          if (f1 >= 1.0F) {
            break label1540;
          }
          this.mLayerPaint.setAlpha(n);
          break label1540;
        }
      }
      localObject4 = paramViewGroup.mCachePaint;
      localObject2 = localObject4;
      if (localObject4 == null)
      {
        localObject2 = new Paint();
        ((Paint)localObject2).setDither(false);
        paramViewGroup.mCachePaint = ((Paint)localObject2);
      }
      ((Paint)localObject2).setAlpha((int)(f1 * 255.0F));
      paramCanvas.drawBitmap((Bitmap)localObject1, 0.0F, 0.0F, (Paint)localObject2);
    }
    label1540:
    if (i >= 0) {
      paramCanvas.restoreToCount(i);
    }
    if ((localAnimation != null) && (!bool6))
    {
      if ((!bool1) && (!localAnimation.getFillAfter())) {
        onSetAlpha(255);
      }
      paramViewGroup.finishAnimatingView(this, localAnimation);
    }
    if ((bool6) && (bool1) && (localAnimation.hasAlpha()) && ((this.mPrivateFlags & 0x40000) == 262144)) {
      invalidate(true);
    }
    this.mRecreateDisplayList = false;
    return bool6;
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    Object localObject = this.mBackground;
    if (localObject != null) {
      ((Drawable)localObject).setHotspot(paramFloat1, paramFloat2);
    }
    localObject = this.mDefaultFocusHighlight;
    if (localObject != null) {
      ((Drawable)localObject).setHotspot(paramFloat1, paramFloat2);
    }
    localObject = this.mForegroundInfo;
    if ((localObject != null) && (((ForegroundInfo)localObject).mDrawable != null)) {
      this.mForegroundInfo.mDrawable.setHotspot(paramFloat1, paramFloat2);
    }
    dispatchDrawableHotspotChanged(paramFloat1, paramFloat2);
  }
  
  protected void drawableStateChanged()
  {
    int[] arrayOfInt = getDrawableState();
    boolean bool1 = false;
    Object localObject = this.mBackground;
    boolean bool2 = bool1;
    if (localObject != null)
    {
      bool2 = bool1;
      if (((Drawable)localObject).isStateful()) {
        bool2 = false | ((Drawable)localObject).setState(arrayOfInt);
      }
    }
    localObject = this.mDefaultFocusHighlight;
    bool1 = bool2;
    if (localObject != null)
    {
      bool1 = bool2;
      if (((Drawable)localObject).isStateful()) {
        bool1 = bool2 | ((Drawable)localObject).setState(arrayOfInt);
      }
    }
    localObject = this.mForegroundInfo;
    if (localObject != null) {
      localObject = ((ForegroundInfo)localObject).mDrawable;
    } else {
      localObject = null;
    }
    bool2 = bool1;
    if (localObject != null)
    {
      bool2 = bool1;
      if (((Drawable)localObject).isStateful()) {
        bool2 = bool1 | ((Drawable)localObject).setState(arrayOfInt);
      }
    }
    localObject = this.mScrollCache;
    bool1 = bool2;
    if (localObject != null)
    {
      localObject = ((ScrollabilityCache)localObject).scrollBar;
      bool1 = bool2;
      if (localObject != null)
      {
        bool1 = bool2;
        if (((Drawable)localObject).isStateful())
        {
          if ((((Drawable)localObject).setState(arrayOfInt)) && (this.mScrollCache.state != 0)) {
            bool1 = true;
          } else {
            bool1 = false;
          }
          bool1 = bool2 | bool1;
        }
      }
    }
    localObject = this.mStateListAnimator;
    if (localObject != null) {
      ((StateListAnimator)localObject).setState(arrayOfInt);
    }
    if (bool1) {
      invalidate();
    }
  }
  
  public void encode(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    paramViewHierarchyEncoder.beginObject(this);
    encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.endObject();
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    Object localObject = ViewDebug.resolveId(getContext(), this.mID);
    if ((localObject instanceof String)) {
      paramViewHierarchyEncoder.addProperty("id", (String)localObject);
    } else {
      paramViewHierarchyEncoder.addProperty("id", this.mID);
    }
    localObject = this.mTransformationInfo;
    float f;
    if (localObject != null) {
      f = ((TransformationInfo)localObject).mAlpha;
    } else {
      f = 0.0F;
    }
    paramViewHierarchyEncoder.addProperty("misc:transformation.alpha", f);
    paramViewHierarchyEncoder.addProperty("misc:transitionName", getTransitionName());
    paramViewHierarchyEncoder.addProperty("layout:left", this.mLeft);
    paramViewHierarchyEncoder.addProperty("layout:right", this.mRight);
    paramViewHierarchyEncoder.addProperty("layout:top", this.mTop);
    paramViewHierarchyEncoder.addProperty("layout:bottom", this.mBottom);
    paramViewHierarchyEncoder.addProperty("layout:width", getWidth());
    paramViewHierarchyEncoder.addProperty("layout:height", getHeight());
    paramViewHierarchyEncoder.addProperty("layout:layoutDirection", getLayoutDirection());
    paramViewHierarchyEncoder.addProperty("layout:layoutRtl", isLayoutRtl());
    paramViewHierarchyEncoder.addProperty("layout:hasTransientState", hasTransientState());
    paramViewHierarchyEncoder.addProperty("layout:baseline", getBaseline());
    localObject = getLayoutParams();
    if (localObject != null)
    {
      paramViewHierarchyEncoder.addPropertyKey("layoutParams");
      ((ViewGroup.LayoutParams)localObject).encode(paramViewHierarchyEncoder);
    }
    paramViewHierarchyEncoder.addProperty("scrolling:scrollX", this.mScrollX);
    paramViewHierarchyEncoder.addProperty("scrolling:scrollY", this.mScrollY);
    paramViewHierarchyEncoder.addProperty("padding:paddingLeft", this.mPaddingLeft);
    paramViewHierarchyEncoder.addProperty("padding:paddingRight", this.mPaddingRight);
    paramViewHierarchyEncoder.addProperty("padding:paddingTop", this.mPaddingTop);
    paramViewHierarchyEncoder.addProperty("padding:paddingBottom", this.mPaddingBottom);
    paramViewHierarchyEncoder.addProperty("padding:userPaddingRight", this.mUserPaddingRight);
    paramViewHierarchyEncoder.addProperty("padding:userPaddingLeft", this.mUserPaddingLeft);
    paramViewHierarchyEncoder.addProperty("padding:userPaddingBottom", this.mUserPaddingBottom);
    paramViewHierarchyEncoder.addProperty("padding:userPaddingStart", this.mUserPaddingStart);
    paramViewHierarchyEncoder.addProperty("padding:userPaddingEnd", this.mUserPaddingEnd);
    paramViewHierarchyEncoder.addProperty("measurement:minHeight", this.mMinHeight);
    paramViewHierarchyEncoder.addProperty("measurement:minWidth", this.mMinWidth);
    paramViewHierarchyEncoder.addProperty("measurement:measuredWidth", this.mMeasuredWidth);
    paramViewHierarchyEncoder.addProperty("measurement:measuredHeight", this.mMeasuredHeight);
    paramViewHierarchyEncoder.addProperty("drawing:elevation", getElevation());
    paramViewHierarchyEncoder.addProperty("drawing:translationX", getTranslationX());
    paramViewHierarchyEncoder.addProperty("drawing:translationY", getTranslationY());
    paramViewHierarchyEncoder.addProperty("drawing:translationZ", getTranslationZ());
    paramViewHierarchyEncoder.addProperty("drawing:rotation", getRotation());
    paramViewHierarchyEncoder.addProperty("drawing:rotationX", getRotationX());
    paramViewHierarchyEncoder.addProperty("drawing:rotationY", getRotationY());
    paramViewHierarchyEncoder.addProperty("drawing:scaleX", getScaleX());
    paramViewHierarchyEncoder.addProperty("drawing:scaleY", getScaleY());
    paramViewHierarchyEncoder.addProperty("drawing:pivotX", getPivotX());
    paramViewHierarchyEncoder.addProperty("drawing:pivotY", getPivotY());
    localObject = this.mClipBounds;
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = ((Rect)localObject).toString();
    }
    paramViewHierarchyEncoder.addProperty("drawing:clipBounds", (String)localObject);
    paramViewHierarchyEncoder.addProperty("drawing:opaque", isOpaque());
    paramViewHierarchyEncoder.addProperty("drawing:alpha", getAlpha());
    paramViewHierarchyEncoder.addProperty("drawing:transitionAlpha", getTransitionAlpha());
    paramViewHierarchyEncoder.addProperty("drawing:shadow", hasShadow());
    paramViewHierarchyEncoder.addProperty("drawing:solidColor", getSolidColor());
    paramViewHierarchyEncoder.addProperty("drawing:layerType", this.mLayerType);
    paramViewHierarchyEncoder.addProperty("drawing:willNotDraw", willNotDraw());
    paramViewHierarchyEncoder.addProperty("drawing:hardwareAccelerated", isHardwareAccelerated());
    paramViewHierarchyEncoder.addProperty("drawing:willNotCacheDrawing", willNotCacheDrawing());
    paramViewHierarchyEncoder.addProperty("drawing:drawingCacheEnabled", isDrawingCacheEnabled());
    paramViewHierarchyEncoder.addProperty("drawing:overlappingRendering", hasOverlappingRendering());
    paramViewHierarchyEncoder.addProperty("drawing:outlineAmbientShadowColor", getOutlineAmbientShadowColor());
    paramViewHierarchyEncoder.addProperty("drawing:outlineSpotShadowColor", getOutlineSpotShadowColor());
    paramViewHierarchyEncoder.addProperty("focus:hasFocus", hasFocus());
    paramViewHierarchyEncoder.addProperty("focus:isFocused", isFocused());
    paramViewHierarchyEncoder.addProperty("focus:focusable", getFocusable());
    paramViewHierarchyEncoder.addProperty("focus:isFocusable", isFocusable());
    paramViewHierarchyEncoder.addProperty("focus:isFocusableInTouchMode", isFocusableInTouchMode());
    paramViewHierarchyEncoder.addProperty("misc:clickable", isClickable());
    paramViewHierarchyEncoder.addProperty("misc:pressed", isPressed());
    paramViewHierarchyEncoder.addProperty("misc:selected", isSelected());
    paramViewHierarchyEncoder.addProperty("misc:touchMode", isInTouchMode());
    paramViewHierarchyEncoder.addProperty("misc:hovered", isHovered());
    paramViewHierarchyEncoder.addProperty("misc:activated", isActivated());
    paramViewHierarchyEncoder.addProperty("misc:visibility", getVisibility());
    paramViewHierarchyEncoder.addProperty("misc:fitsSystemWindows", getFitsSystemWindows());
    paramViewHierarchyEncoder.addProperty("misc:filterTouchesWhenObscured", getFilterTouchesWhenObscured());
    paramViewHierarchyEncoder.addProperty("misc:enabled", isEnabled());
    paramViewHierarchyEncoder.addProperty("misc:soundEffectsEnabled", isSoundEffectsEnabled());
    paramViewHierarchyEncoder.addProperty("misc:hapticFeedbackEnabled", isHapticFeedbackEnabled());
    localObject = getContext().getTheme();
    if (localObject != null)
    {
      paramViewHierarchyEncoder.addPropertyKey("theme");
      ((Resources.Theme)localObject).encode(paramViewHierarchyEncoder);
    }
    localObject = this.mAttributes;
    int i;
    if (localObject != null) {
      i = localObject.length;
    } else {
      i = 0;
    }
    paramViewHierarchyEncoder.addProperty("meta:__attrCount__", i / 2);
    for (int j = 0; j < i; j += 2)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("meta:__attr__");
      ((StringBuilder)localObject).append(this.mAttributes[j]);
      paramViewHierarchyEncoder.addProperty(((StringBuilder)localObject).toString(), this.mAttributes[(j + 1)]);
    }
    paramViewHierarchyEncoder.addProperty("misc:scrollBarStyle", getScrollBarStyle());
    paramViewHierarchyEncoder.addProperty("text:textDirection", getTextDirection());
    paramViewHierarchyEncoder.addProperty("text:textAlignment", getTextAlignment());
    localObject = getContentDescription();
    if (localObject == null) {
      localObject = "";
    } else {
      localObject = ((CharSequence)localObject).toString();
    }
    paramViewHierarchyEncoder.addProperty("accessibility:contentDescription", (String)localObject);
    paramViewHierarchyEncoder.addProperty("accessibility:labelFor", getLabelFor());
    paramViewHierarchyEncoder.addProperty("accessibility:importantForAccessibility", getImportantForAccessibility());
  }
  
  @UnsupportedAppUsage
  void ensureTransformationInfo()
  {
    if (this.mTransformationInfo == null) {
      this.mTransformationInfo = new TransformationInfo();
    }
  }
  
  public View findFocus()
  {
    View localView;
    if ((this.mPrivateFlags & 0x2) != 0) {
      localView = this;
    } else {
      localView = null;
    }
    return localView;
  }
  
  View findKeyboardNavigationCluster()
  {
    Object localObject = this.mParent;
    if ((localObject instanceof View))
    {
      localObject = ((View)localObject).findKeyboardNavigationCluster();
      if (localObject != null) {
        return (View)localObject;
      }
      if (isKeyboardNavigationCluster()) {
        return this;
      }
    }
    return null;
  }
  
  public void findNamedViews(Map<String, View> paramMap)
  {
    if ((getVisibility() == 0) || (this.mGhostView != null))
    {
      String str = getTransitionName();
      if (str != null) {
        paramMap.put(str, this);
      }
    }
  }
  
  View findUserSetNextFocus(View paramView, int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 17)
        {
          if (paramInt != 33)
          {
            if (paramInt != 66)
            {
              if (paramInt != 130) {
                return null;
              }
              paramInt = this.mNextFocusDownId;
              if (paramInt == -1) {
                return null;
              }
              return findViewInsideOutShouldExist(paramView, paramInt);
            }
            paramInt = this.mNextFocusRightId;
            if (paramInt == -1) {
              return null;
            }
            return findViewInsideOutShouldExist(paramView, paramInt);
          }
          paramInt = this.mNextFocusUpId;
          if (paramInt == -1) {
            return null;
          }
          return findViewInsideOutShouldExist(paramView, paramInt);
        }
        paramInt = this.mNextFocusLeftId;
        if (paramInt == -1) {
          return null;
        }
        return findViewInsideOutShouldExist(paramView, paramInt);
      }
      paramInt = this.mNextFocusForwardId;
      if (paramInt == -1) {
        return null;
      }
      return findViewInsideOutShouldExist(paramView, paramInt);
    }
    if (this.mID == -1) {
      return null;
    }
    paramView.findViewByPredicateInsideOut(this, new Predicate()
    {
      public boolean test(View paramAnonymousView)
      {
        boolean bool;
        if (paramAnonymousView.mNextFocusForwardId == this.val$id) {
          bool = true;
        } else {
          bool = false;
        }
        return bool;
      }
    });
  }
  
  View findUserSetNextKeyboardNavigationCluster(View paramView, int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2) {
        return null;
      }
      paramInt = this.mNextClusterForwardId;
      if (paramInt == -1) {
        return null;
      }
      return findViewInsideOutShouldExist(paramView, paramInt);
    }
    if (this.mID == -1) {
      return null;
    }
    return paramView.findViewByPredicateInsideOut(this, new _..Lambda.View.7kZ4TXHKswReUMQB8098MEBcx_U(this.mID));
  }
  
  public <T extends View> T findViewByAccessibilityIdTraversal(int paramInt)
  {
    if (getAccessibilityViewId() == paramInt) {
      return this;
    }
    return null;
  }
  
  public <T extends View> T findViewByAutofillIdTraversal(int paramInt)
  {
    if (getAutofillViewId() == paramInt) {
      return this;
    }
    return null;
  }
  
  public final <T extends View> T findViewById(int paramInt)
  {
    if (paramInt == -1) {
      return null;
    }
    return findViewTraversal(paramInt);
  }
  
  public final <T extends View> T findViewByPredicate(Predicate<View> paramPredicate)
  {
    return findViewByPredicateTraversal(paramPredicate, null);
  }
  
  public final <T extends View> T findViewByPredicateInsideOut(View paramView, Predicate<View> paramPredicate)
  {
    Object localObject = null;
    for (;;)
    {
      localObject = paramView.findViewByPredicateTraversal(paramPredicate, (View)localObject);
      if ((localObject != null) || (paramView == this)) {
        return localObject;
      }
      localObject = paramView.getParent();
      if ((localObject == null) || (!(localObject instanceof View))) {
        break;
      }
      View localView = (View)localObject;
      localObject = paramView;
      paramView = localView;
    }
    return null;
    return (T)localObject;
  }
  
  protected <T extends View> T findViewByPredicateTraversal(Predicate<View> paramPredicate, View paramView)
  {
    if (paramPredicate.test(this)) {
      return this;
    }
    return null;
  }
  
  protected <T extends View> T findViewTraversal(int paramInt)
  {
    if (paramInt == this.mID) {
      return this;
    }
    return null;
  }
  
  public final <T extends View> T findViewWithTag(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    return findViewWithTagTraversal(paramObject);
  }
  
  protected <T extends View> T findViewWithTagTraversal(Object paramObject)
  {
    if ((paramObject != null) && (paramObject.equals(this.mTag))) {
      return this;
    }
    return null;
  }
  
  public void findViewsWithText(ArrayList<View> paramArrayList, CharSequence paramCharSequence, int paramInt)
  {
    if (getAccessibilityNodeProvider() != null)
    {
      if ((paramInt & 0x4) != 0) {
        paramArrayList.add(this);
      }
    }
    else if (((paramInt & 0x2) != 0) && (paramCharSequence != null) && (paramCharSequence.length() > 0))
    {
      CharSequence localCharSequence = this.mContentDescription;
      if ((localCharSequence != null) && (localCharSequence.length() > 0))
      {
        paramCharSequence = paramCharSequence.toString().toLowerCase();
        if (this.mContentDescription.toString().toLowerCase().contains(paramCharSequence)) {
          paramArrayList.add(this);
        }
      }
    }
  }
  
  public void finishMovingTask()
  {
    try
    {
      this.mAttachInfo.mSession.finishMovingTask(this.mAttachInfo.mWindow);
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("View", "Unable to finish moving", localRemoteException);
    }
  }
  
  /* Error */
  @Deprecated
  protected boolean fitSystemWindows(Rect paramRect)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 1582	android/view/View:mPrivateFlags3	I
    //   4: istore_2
    //   5: iload_2
    //   6: bipush 32
    //   8: iand
    //   9: ifne +64 -> 73
    //   12: aload_1
    //   13: ifnonnull +5 -> 18
    //   16: iconst_0
    //   17: ireturn
    //   18: aload_0
    //   19: iload_2
    //   20: bipush 64
    //   22: ior
    //   23: putfield 1582	android/view/View:mPrivateFlags3	I
    //   26: new 4134	android/view/WindowInsets
    //   29: astore_3
    //   30: aload_3
    //   31: aload_1
    //   32: invokespecial 4136	android/view/WindowInsets:<init>	(Landroid/graphics/Rect;)V
    //   35: aload_0
    //   36: aload_3
    //   37: invokevirtual 5319	android/view/View:dispatchApplyWindowInsets	(Landroid/view/WindowInsets;)Landroid/view/WindowInsets;
    //   40: invokevirtual 5322	android/view/WindowInsets:isConsumed	()Z
    //   43: istore 4
    //   45: aload_0
    //   46: aload_0
    //   47: getfield 1582	android/view/View:mPrivateFlags3	I
    //   50: bipush -65
    //   52: iand
    //   53: putfield 1582	android/view/View:mPrivateFlags3	I
    //   56: iload 4
    //   58: ireturn
    //   59: astore_1
    //   60: aload_0
    //   61: aload_0
    //   62: getfield 1582	android/view/View:mPrivateFlags3	I
    //   65: bipush -65
    //   67: iand
    //   68: putfield 1582	android/view/View:mPrivateFlags3	I
    //   71: aload_1
    //   72: athrow
    //   73: aload_0
    //   74: aload_1
    //   75: invokespecial 5324	android/view/View:fitSystemWindowsInt	(Landroid/graphics/Rect;)Z
    //   78: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	79	0	this	View
    //   0	79	1	paramRect	Rect
    //   4	19	2	i	int
    //   29	8	3	localWindowInsets	WindowInsets
    //   43	14	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   18	45	59	finally
  }
  
  @UnsupportedAppUsage
  public boolean fitsSystemWindows()
  {
    return getFitsSystemWindows();
  }
  
  public View focusSearch(int paramInt)
  {
    ViewParent localViewParent = this.mParent;
    if (localViewParent != null) {
      return localViewParent.focusSearch(this, paramInt);
    }
    return null;
  }
  
  public void forceHasOverlappingRendering(boolean paramBoolean)
  {
    this.mPrivateFlags3 |= 0x1000000;
    if (paramBoolean) {
      this.mPrivateFlags3 |= 0x800000;
    } else {
      this.mPrivateFlags3 &= 0xFF7FFFFF;
    }
  }
  
  public void forceLayout()
  {
    LongSparseLongArray localLongSparseLongArray = this.mMeasureCache;
    if (localLongSparseLongArray != null) {
      localLongSparseLongArray.clear();
    }
    this.mPrivateFlags |= 0x1000;
    this.mPrivateFlags |= 0x80000000;
  }
  
  @UnsupportedAppUsage
  public boolean gatherTransparentRegion(Region paramRegion)
  {
    Object localObject = this.mAttachInfo;
    if ((paramRegion != null) && (localObject != null)) {
      if ((this.mPrivateFlags & 0x80) == 0)
      {
        localObject = ((AttachInfo)localObject).mTransparentLocation;
        getLocationInWindow((int[])localObject);
        int i;
        if (getZ() > 0.0F) {
          i = (int)getZ();
        } else {
          i = 0;
        }
        paramRegion.op(localObject[0] - i, localObject[1] - i, localObject[0] + this.mRight - this.mLeft + i, localObject[1] + this.mBottom - this.mTop + i * 3, Region.Op.DIFFERENCE);
      }
      else
      {
        localObject = this.mBackground;
        if ((localObject != null) && (((Drawable)localObject).getOpacity() != -2)) {
          applyDrawableToTransparentRegion(this.mBackground, paramRegion);
        }
        localObject = this.mForegroundInfo;
        if ((localObject != null) && (((ForegroundInfo)localObject).mDrawable != null) && (this.mForegroundInfo.mDrawable.getOpacity() != -2)) {
          applyDrawableToTransparentRegion(this.mForegroundInfo.mDrawable, paramRegion);
        }
        localObject = this.mDefaultFocusHighlight;
        if ((localObject != null) && (((Drawable)localObject).getOpacity() != -2)) {
          applyDrawableToTransparentRegion(this.mDefaultFocusHighlight, paramRegion);
        }
      }
    }
    return true;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return View.class.getName();
  }
  
  public AccessibilityDelegate getAccessibilityDelegate()
  {
    return this.mAccessibilityDelegate;
  }
  
  public int getAccessibilityLiveRegion()
  {
    return (this.mPrivateFlags2 & 0x1800000) >> 23;
  }
  
  public AccessibilityNodeProvider getAccessibilityNodeProvider()
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      return localAccessibilityDelegate.getAccessibilityNodeProvider(this);
    }
    return null;
  }
  
  public CharSequence getAccessibilityPaneTitle()
  {
    return this.mAccessibilityPaneTitle;
  }
  
  public int getAccessibilitySelectionEnd()
  {
    return getAccessibilitySelectionStart();
  }
  
  public int getAccessibilitySelectionStart()
  {
    return this.mAccessibilityCursorPosition;
  }
  
  public int getAccessibilityTraversalAfter()
  {
    return this.mAccessibilityTraversalAfterId;
  }
  
  public int getAccessibilityTraversalBefore()
  {
    return this.mAccessibilityTraversalBeforeId;
  }
  
  @UnsupportedAppUsage
  public int getAccessibilityViewId()
  {
    if (this.mAccessibilityViewId == -1)
    {
      int i = sNextAccessibilityViewId;
      sNextAccessibilityViewId = i + 1;
      this.mAccessibilityViewId = i;
    }
    return this.mAccessibilityViewId;
  }
  
  public int getAccessibilityWindowId()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    int i;
    if (localAttachInfo != null) {
      i = localAttachInfo.mAccessibilityWindowId;
    } else {
      i = -1;
    }
    return i;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getAlpha()
  {
    TransformationInfo localTransformationInfo = this.mTransformationInfo;
    float f;
    if (localTransformationInfo != null) {
      f = localTransformationInfo.mAlpha;
    } else {
      f = 1.0F;
    }
    return f;
  }
  
  public Animation getAnimation()
  {
    return this.mCurrentAnimation;
  }
  
  public Matrix getAnimationMatrix()
  {
    return this.mRenderNode.getAnimationMatrix();
  }
  
  public IBinder getApplicationWindowToken()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null)
    {
      IBinder localIBinder1 = localAttachInfo.mPanelParentWindowToken;
      IBinder localIBinder2 = localIBinder1;
      if (localIBinder1 == null) {
        localIBinder2 = localAttachInfo.mWindowToken;
      }
      return localIBinder2;
    }
    return null;
  }
  
  protected Activity getAttachedActivity()
  {
    Context localContext1 = getContext();
    Context localContext2;
    for (;;)
    {
      localContext2 = localContext1;
      if (((localContext2 instanceof Activity)) || (!(localContext2 instanceof ContextWrapper))) {
        break;
      }
      localContext1 = ((ContextWrapper)localContext2).getBaseContext();
      if (localContext2 == localContext1) {
        return null;
      }
    }
    if ((localContext2 instanceof Activity)) {
      return (Activity)localContext2;
    }
    return null;
  }
  
  public Activity getAttachedActivityInstance()
  {
    if (this.mFirst) {
      try
      {
        if (this.mFirst)
        {
          Activity localActivity = getAttachedActivity();
          WeakReference localWeakReference = new java/lang/ref/WeakReference;
          localWeakReference.<init>(localActivity);
          this.mAttachedActivity = localWeakReference;
          this.mFirst = false;
        }
      }
      finally {}
    }
    Object localObject2 = this.mAttachedActivity;
    if (localObject2 == null) {
      localObject2 = null;
    } else {
      localObject2 = (Activity)((WeakReference)localObject2).get();
    }
    return (Activity)localObject2;
  }
  
  public int[] getAttributeResolutionStack(int paramInt)
  {
    if (sDebugViewAttributes)
    {
      Object localObject = this.mAttributeResolutionStacks;
      if ((localObject != null) && (((SparseArray)localObject).get(paramInt) != null))
      {
        int[] arrayOfInt = (int[])this.mAttributeResolutionStacks.get(paramInt);
        int i = arrayOfInt.length;
        paramInt = i;
        if (this.mSourceLayoutId != 0) {
          paramInt = i + 1;
        }
        i = 0;
        localObject = new int[paramInt];
        int j = this.mSourceLayoutId;
        paramInt = i;
        if (j != 0)
        {
          localObject[0] = j;
          paramInt = 0 + 1;
        }
        for (i = 0; i < arrayOfInt.length; i++)
        {
          localObject[paramInt] = arrayOfInt[i];
          paramInt++;
        }
        return (int[])localObject;
      }
    }
    return new int[0];
  }
  
  public Map<Integer, Integer> getAttributeSourceResourceMap()
  {
    HashMap localHashMap = new HashMap();
    if ((sDebugViewAttributes) && (this.mAttributeSourceResId != null))
    {
      for (int i = 0; i < this.mAttributeSourceResId.size(); i++) {
        localHashMap.put(Integer.valueOf(this.mAttributeSourceResId.keyAt(i)), Integer.valueOf(this.mAttributeSourceResId.valueAt(i)));
      }
      return localHashMap;
    }
    return localHashMap;
  }
  
  @ViewDebug.ExportedProperty
  public String[] getAutofillHints()
  {
    return this.mAutofillHints;
  }
  
  public final AutofillId getAutofillId()
  {
    if (this.mAutofillId == null) {
      this.mAutofillId = new AutofillId(getAutofillViewId());
    }
    return this.mAutofillId;
  }
  
  public int getAutofillType()
  {
    return 0;
  }
  
  public AutofillValue getAutofillValue()
  {
    return null;
  }
  
  public int getAutofillViewId()
  {
    if (this.mAutofillViewId == -1) {
      this.mAutofillViewId = this.mContext.getNextAutofillId();
    }
    return this.mAutofillViewId;
  }
  
  public Drawable getBackground()
  {
    return this.mBackground;
  }
  
  public BlendMode getBackgroundTintBlendMode()
  {
    Object localObject = this.mBackgroundTint;
    if (localObject != null) {
      localObject = ((TintInfo)localObject).mBlendMode;
    } else {
      localObject = null;
    }
    return (BlendMode)localObject;
  }
  
  public ColorStateList getBackgroundTintList()
  {
    Object localObject = this.mBackgroundTint;
    if (localObject != null) {
      localObject = ((TintInfo)localObject).mTintList;
    } else {
      localObject = null;
    }
    return (ColorStateList)localObject;
  }
  
  public PorterDuff.Mode getBackgroundTintMode()
  {
    Object localObject = this.mBackgroundTint;
    if ((localObject != null) && (((TintInfo)localObject).mBlendMode != null)) {
      localObject = BlendMode.blendModeToPorterDuffMode(this.mBackgroundTint.mBlendMode);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  @ViewDebug.ExportedProperty(category="layout")
  public int getBaseline()
  {
    return -1;
  }
  
  @ViewDebug.CapturedViewProperty
  public final int getBottom()
  {
    return this.mBottom;
  }
  
  protected float getBottomFadingEdgeStrength()
  {
    float f;
    if (computeVerticalScrollOffset() + computeVerticalScrollExtent() < computeVerticalScrollRange()) {
      f = 1.0F;
    } else {
      f = 0.0F;
    }
    return f;
  }
  
  protected int getBottomPaddingOffset()
  {
    return 0;
  }
  
  @UnsupportedAppUsage
  public void getBoundsOnScreen(Rect paramRect)
  {
    getBoundsOnScreen(paramRect, false);
  }
  
  @UnsupportedAppUsage
  public void getBoundsOnScreen(Rect paramRect, boolean paramBoolean)
  {
    Object localObject = this.mAttachInfo;
    if (localObject == null) {
      return;
    }
    localObject = ((AttachInfo)localObject).mTmpTransformRect;
    ((RectF)localObject).set(0.0F, 0.0F, this.mRight - this.mLeft, this.mBottom - this.mTop);
    mapRectFromViewToScreenCoords((RectF)localObject, paramBoolean);
    paramRect.set(Math.round(((RectF)localObject).left), Math.round(((RectF)localObject).top), Math.round(((RectF)localObject).right), Math.round(((RectF)localObject).bottom));
  }
  
  public float getCameraDistance()
  {
    float f = this.mResources.getDisplayMetrics().densityDpi;
    return this.mRenderNode.getCameraDistance() * f;
  }
  
  public Rect getClipBounds()
  {
    Rect localRect = this.mClipBounds;
    if (localRect != null) {
      localRect = new Rect(localRect);
    } else {
      localRect = null;
    }
    return localRect;
  }
  
  public boolean getClipBounds(Rect paramRect)
  {
    Rect localRect = this.mClipBounds;
    if (localRect != null)
    {
      paramRect.set(localRect);
      return true;
    }
    return false;
  }
  
  public final boolean getClipToOutline()
  {
    return this.mRenderNode.getClipToOutline();
  }
  
  public final ContentCaptureSession getContentCaptureSession()
  {
    ContentCaptureSession localContentCaptureSession = this.mCachedContentCaptureSession;
    if (localContentCaptureSession != null) {
      return localContentCaptureSession;
    }
    this.mCachedContentCaptureSession = getAndCacheContentCaptureSession();
    return this.mCachedContentCaptureSession;
  }
  
  @ViewDebug.ExportedProperty(category="accessibility")
  public CharSequence getContentDescription()
  {
    return this.mContentDescription;
  }
  
  @ViewDebug.CapturedViewProperty
  public final Context getContext()
  {
    return this.mContext;
  }
  
  protected ContextMenu.ContextMenuInfo getContextMenuInfo()
  {
    return null;
  }
  
  @ViewDebug.ExportedProperty(category="focus")
  public final boolean getDefaultFocusHighlightEnabled()
  {
    return this.mDefaultFocusHighlightEnabled;
  }
  
  public Display getDisplay()
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mDisplay;
    } else {
      localObject = null;
    }
    return (Display)localObject;
  }
  
  public final int[] getDrawableState()
  {
    int[] arrayOfInt = this.mDrawableState;
    if ((arrayOfInt != null) && ((this.mPrivateFlags & 0x400) == 0)) {
      return arrayOfInt;
    }
    this.mDrawableState = onCreateDrawableState(0);
    this.mPrivateFlags &= 0xFBFF;
    return this.mDrawableState;
  }
  
  @Deprecated
  public Bitmap getDrawingCache()
  {
    return getDrawingCache(false);
  }
  
  @Deprecated
  public Bitmap getDrawingCache(boolean paramBoolean)
  {
    int i = this.mViewFlags;
    if ((i & 0x20000) == 131072) {
      return null;
    }
    if ((i & 0x8000) == 32768) {
      buildDrawingCache(paramBoolean);
    }
    Bitmap localBitmap;
    if (paramBoolean) {
      localBitmap = this.mDrawingCache;
    } else {
      localBitmap = this.mUnscaledDrawingCache;
    }
    return localBitmap;
  }
  
  @Deprecated
  public int getDrawingCacheBackgroundColor()
  {
    return this.mDrawingCacheBackgroundColor;
  }
  
  @Deprecated
  public int getDrawingCacheQuality()
  {
    return this.mViewFlags & 0x180000;
  }
  
  public void getDrawingRect(Rect paramRect)
  {
    int i = this.mScrollX;
    paramRect.left = i;
    int j = this.mScrollY;
    paramRect.top = j;
    paramRect.right = (i + (this.mRight - this.mLeft));
    paramRect.bottom = (j + (this.mBottom - this.mTop));
  }
  
  public long getDrawingTime()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    long l;
    if (localAttachInfo != null) {
      l = localAttachInfo.mDrawingTime;
    } else {
      l = 0L;
    }
    return l;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getElevation()
  {
    return this.mRenderNode.getElevation();
  }
  
  public int getExplicitStyle()
  {
    if (!sDebugViewAttributes) {
      return 0;
    }
    return this.mExplicitStyle;
  }
  
  protected int getFadeHeight(boolean paramBoolean)
  {
    int i = this.mPaddingTop;
    int j = i;
    if (paramBoolean) {
      j = i + getTopPaddingOffset();
    }
    return this.mBottom - this.mTop - this.mPaddingBottom - j;
  }
  
  protected int getFadeTop(boolean paramBoolean)
  {
    int i = this.mPaddingTop;
    int j = i;
    if (paramBoolean) {
      j = i + getTopPaddingOffset();
    }
    return j;
  }
  
  public int getFadingEdge()
  {
    return this.mViewFlags & 0x3000;
  }
  
  public int getFadingEdgeLength()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    if ((localScrollabilityCache != null) && ((this.mViewFlags & 0x3000) != 0)) {
      return localScrollabilityCache.fadingEdgeLength;
    }
    return 0;
  }
  
  @ViewDebug.ExportedProperty
  public boolean getFilterTouchesWhenObscured()
  {
    boolean bool;
    if ((this.mViewFlags & 0x400) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty
  public boolean getFitsSystemWindows()
  {
    boolean bool;
    if ((this.mViewFlags & 0x2) == 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="focus", mapping={@ViewDebug.IntToString(from=0, to="NOT_FOCUSABLE"), @ViewDebug.IntToString(from=1, to="FOCUSABLE"), @ViewDebug.IntToString(from=16, to="FOCUSABLE_AUTO")})
  public int getFocusable()
  {
    int i = this.mViewFlags;
    if ((i & 0x10) > 0) {
      i = 16;
    } else {
      i &= 0x1;
    }
    return i;
  }
  
  public ArrayList<View> getFocusables(int paramInt)
  {
    ArrayList localArrayList = new ArrayList(24);
    addFocusables(localArrayList, paramInt);
    return localArrayList;
  }
  
  public void getFocusedRect(Rect paramRect)
  {
    getDrawingRect(paramRect);
  }
  
  public Drawable getForeground()
  {
    Object localObject = this.mForegroundInfo;
    if (localObject != null) {
      localObject = ((ForegroundInfo)localObject).mDrawable;
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  public int getForegroundGravity()
  {
    ForegroundInfo localForegroundInfo = this.mForegroundInfo;
    int i;
    if (localForegroundInfo != null) {
      i = localForegroundInfo.mGravity;
    } else {
      i = 8388659;
    }
    return i;
  }
  
  public BlendMode getForegroundTintBlendMode()
  {
    Object localObject = this.mForegroundInfo;
    if ((localObject != null) && (((ForegroundInfo)localObject).mTintInfo != null)) {
      localObject = this.mForegroundInfo.mTintInfo.mBlendMode;
    } else {
      localObject = null;
    }
    return (BlendMode)localObject;
  }
  
  public ColorStateList getForegroundTintList()
  {
    Object localObject = this.mForegroundInfo;
    if ((localObject != null) && (((ForegroundInfo)localObject).mTintInfo != null)) {
      localObject = this.mForegroundInfo.mTintInfo.mTintList;
    } else {
      localObject = null;
    }
    return (ColorStateList)localObject;
  }
  
  public PorterDuff.Mode getForegroundTintMode()
  {
    Object localObject = this.mForegroundInfo;
    if ((localObject != null) && (((ForegroundInfo)localObject).mTintInfo != null)) {
      localObject = this.mForegroundInfo.mTintInfo.mBlendMode;
    } else {
      localObject = null;
    }
    if (localObject != null) {
      return BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    }
    return null;
  }
  
  public final boolean getGlobalVisibleRect(Rect paramRect)
  {
    return getGlobalVisibleRect(paramRect, null);
  }
  
  public boolean getGlobalVisibleRect(Rect paramRect, Point paramPoint)
  {
    int i = this.mRight - this.mLeft;
    int j = this.mBottom - this.mTop;
    boolean bool = false;
    if ((i > 0) && (j > 0))
    {
      paramRect.set(0, 0, i, j);
      if (paramPoint != null) {
        paramPoint.set(-this.mScrollX, -this.mScrollY);
      }
      ViewParent localViewParent = this.mParent;
      if ((localViewParent == null) || (localViewParent.getChildVisibleRect(this, paramRect, paramPoint))) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public Handler getHandler()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      return localAttachInfo.mHandler;
    }
    return null;
  }
  
  public final boolean getHasOverlappingRendering()
  {
    int i = this.mPrivateFlags3;
    boolean bool;
    if ((0x1000000 & i) != 0)
    {
      if ((i & 0x800000) != 0) {
        bool = true;
      } else {
        bool = false;
      }
    }
    else {
      bool = hasOverlappingRendering();
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="layout")
  public final int getHeight()
  {
    return this.mBottom - this.mTop;
  }
  
  public void getHitRect(Rect paramRect)
  {
    if (!hasIdentityMatrix())
    {
      Object localObject = this.mAttachInfo;
      if (localObject != null)
      {
        localObject = ((AttachInfo)localObject).mTmpTransformRect;
        ((RectF)localObject).set(0.0F, 0.0F, getWidth(), getHeight());
        getMatrix().mapRect((RectF)localObject);
        paramRect.set((int)((RectF)localObject).left + this.mLeft, (int)((RectF)localObject).top + this.mTop, (int)((RectF)localObject).right + this.mLeft, (int)((RectF)localObject).bottom + this.mTop);
        return;
      }
    }
    paramRect.set(this.mLeft, this.mTop, this.mRight, this.mBottom);
  }
  
  public int getHorizontalFadingEdgeLength()
  {
    if (isHorizontalFadingEdgeEnabled())
    {
      ScrollabilityCache localScrollabilityCache = this.mScrollCache;
      if (localScrollabilityCache != null) {
        return localScrollabilityCache.fadingEdgeLength;
      }
    }
    return 0;
  }
  
  @UnsupportedAppUsage
  protected float getHorizontalScrollFactor()
  {
    return getVerticalScrollFactor();
  }
  
  protected int getHorizontalScrollbarHeight()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    if (localScrollabilityCache != null)
    {
      ScrollBarDrawable localScrollBarDrawable = localScrollabilityCache.scrollBar;
      if (localScrollBarDrawable != null)
      {
        int i = localScrollBarDrawable.getSize(false);
        int j = i;
        if (i <= 0) {
          j = localScrollabilityCache.scrollBarSize;
        }
        return j;
      }
      return 0;
    }
    return 0;
  }
  
  public Drawable getHorizontalScrollbarThumbDrawable()
  {
    Object localObject = this.mScrollCache;
    if (localObject != null) {
      localObject = ((ScrollabilityCache)localObject).scrollBar.getHorizontalThumbDrawable();
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  public Drawable getHorizontalScrollbarTrackDrawable()
  {
    Object localObject = this.mScrollCache;
    if (localObject != null) {
      localObject = ((ScrollabilityCache)localObject).scrollBar.getHorizontalTrackDrawable();
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  public void getHotspotBounds(Rect paramRect)
  {
    Drawable localDrawable = getBackground();
    if (localDrawable != null) {
      localDrawable.getHotspotBounds(paramRect);
    } else {
      getBoundsOnScreen(paramRect);
    }
  }
  
  @ViewDebug.CapturedViewProperty
  public int getId()
  {
    return this.mID;
  }
  
  @ViewDebug.ExportedProperty(category="accessibility", mapping={@ViewDebug.IntToString(from=0, to="auto"), @ViewDebug.IntToString(from=1, to="yes"), @ViewDebug.IntToString(from=2, to="no"), @ViewDebug.IntToString(from=4, to="noHideDescendants")})
  public int getImportantForAccessibility()
  {
    return (this.mPrivateFlags2 & 0x700000) >> 20;
  }
  
  @ViewDebug.ExportedProperty(mapping={@ViewDebug.IntToString(from=0, to="auto"), @ViewDebug.IntToString(from=1, to="yes"), @ViewDebug.IntToString(from=2, to="no"), @ViewDebug.IntToString(from=4, to="yesExcludeDescendants"), @ViewDebug.IntToString(from=8, to="noExcludeDescendants")})
  public int getImportantForAutofill()
  {
    return (this.mPrivateFlags3 & 0x780000) >> 19;
  }
  
  @UnsupportedAppUsage
  public final Matrix getInverseMatrix()
  {
    ensureTransformationInfo();
    if (this.mTransformationInfo.mInverseMatrix == null) {
      TransformationInfo.access$2202(this.mTransformationInfo, new Matrix());
    }
    Matrix localMatrix = this.mTransformationInfo.mInverseMatrix;
    this.mRenderNode.getInverseMatrix(localMatrix);
    return localMatrix;
  }
  
  @UnsupportedAppUsage
  public CharSequence getIterableTextForAccessibility()
  {
    return getContentDescription();
  }
  
  @UnsupportedAppUsage
  public AccessibilityIterators.TextSegmentIterator getIteratorForGranularity(int paramInt)
  {
    Object localObject1;
    Object localObject2;
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt == 8)
        {
          localObject1 = getIterableTextForAccessibility();
          if ((localObject1 != null) && (((CharSequence)localObject1).length() > 0))
          {
            localObject2 = AccessibilityIterators.ParagraphTextSegmentIterator.getInstance();
            ((AccessibilityIterators.ParagraphTextSegmentIterator)localObject2).initialize(((CharSequence)localObject1).toString());
            return (AccessibilityIterators.TextSegmentIterator)localObject2;
          }
        }
      }
      else
      {
        localObject1 = getIterableTextForAccessibility();
        if ((localObject1 != null) && (((CharSequence)localObject1).length() > 0))
        {
          localObject2 = AccessibilityIterators.WordTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
          ((AccessibilityIterators.WordTextSegmentIterator)localObject2).initialize(((CharSequence)localObject1).toString());
          return (AccessibilityIterators.TextSegmentIterator)localObject2;
        }
      }
    }
    else
    {
      localObject2 = getIterableTextForAccessibility();
      if ((localObject2 != null) && (((CharSequence)localObject2).length() > 0))
      {
        localObject1 = AccessibilityIterators.CharacterTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
        ((AccessibilityIterators.CharacterTextSegmentIterator)localObject1).initialize(((CharSequence)localObject2).toString());
        return (AccessibilityIterators.TextSegmentIterator)localObject1;
      }
    }
    return null;
  }
  
  public boolean getKeepScreenOn()
  {
    boolean bool;
    if ((this.mViewFlags & 0x4000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public KeyEvent.DispatcherState getKeyDispatcherState()
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mKeyDispatchState;
    } else {
      localObject = null;
    }
    return (KeyEvent.DispatcherState)localObject;
  }
  
  @ViewDebug.ExportedProperty(category="accessibility")
  public int getLabelFor()
  {
    return this.mLabelForId;
  }
  
  public int getLayerType()
  {
    return this.mLayerType;
  }
  
  @ViewDebug.ExportedProperty(category="layout", mapping={@ViewDebug.IntToString(from=0, to="RESOLVED_DIRECTION_LTR"), @ViewDebug.IntToString(from=1, to="RESOLVED_DIRECTION_RTL")})
  public int getLayoutDirection()
  {
    int i = getContext().getApplicationInfo().targetSdkVersion;
    int j = 0;
    if (i < 17)
    {
      this.mPrivateFlags2 |= 0x20;
      return 0;
    }
    if ((this.mPrivateFlags2 & 0x10) == 16) {
      j = 1;
    }
    return j;
  }
  
  @ViewDebug.ExportedProperty(deepExport=true, prefix="layout_")
  public ViewGroup.LayoutParams getLayoutParams()
  {
    return this.mLayoutParams;
  }
  
  @ViewDebug.CapturedViewProperty
  public final int getLeft()
  {
    return this.mLeft;
  }
  
  protected float getLeftFadingEdgeStrength()
  {
    float f;
    if (computeHorizontalScrollOffset() > 0) {
      f = 1.0F;
    } else {
      f = 0.0F;
    }
    return f;
  }
  
  protected int getLeftPaddingOffset()
  {
    return 0;
  }
  
  @UnsupportedAppUsage
  ListenerInfo getListenerInfo()
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if (localListenerInfo != null) {
      return localListenerInfo;
    }
    this.mListenerInfo = new ListenerInfo();
    return this.mListenerInfo;
  }
  
  public final boolean getLocalVisibleRect(Rect paramRect)
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mPoint;
    } else {
      localObject = new Point();
    }
    if (getGlobalVisibleRect(paramRect, (Point)localObject))
    {
      paramRect.offset(-((Point)localObject).x, -((Point)localObject).y);
      return true;
    }
    return false;
  }
  
  public void getLocationInSurface(int[] paramArrayOfInt)
  {
    getLocationInWindow(paramArrayOfInt);
    AttachInfo localAttachInfo = this.mAttachInfo;
    if ((localAttachInfo != null) && (localAttachInfo.mViewRootImpl != null))
    {
      paramArrayOfInt[0] += this.mAttachInfo.mViewRootImpl.mWindowAttributes.surfaceInsets.left;
      paramArrayOfInt[1] += this.mAttachInfo.mViewRootImpl.mWindowAttributes.surfaceInsets.top;
    }
  }
  
  public void getLocationInWindow(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length >= 2))
    {
      paramArrayOfInt[0] = 0;
      paramArrayOfInt[1] = 0;
      transformFromViewToWindowSpace(paramArrayOfInt);
      return;
    }
    throw new IllegalArgumentException("outLocation must be an array of two integers");
  }
  
  public void getLocationOnScreen(int[] paramArrayOfInt)
  {
    getLocationInWindow(paramArrayOfInt);
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null)
    {
      Resources localResources = this.mResources;
      if ((localResources != null) && (localResources.getConfiguration().windowConfiguration.getWindowingMode() != 5))
      {
        paramArrayOfInt[0] += localAttachInfo.mWindowLeft;
        paramArrayOfInt[1] += localAttachInfo.mWindowTop;
      }
    }
  }
  
  @ViewDebug.ExportedProperty(category="layout", indexMapping={@ViewDebug.IntToString(from=0, to="x"), @ViewDebug.IntToString(from=1, to="y")})
  @UnsupportedAppUsage
  public int[] getLocationOnScreen()
  {
    int[] arrayOfInt = new int[2];
    getLocationOnScreen(arrayOfInt);
    return arrayOfInt;
  }
  
  public Matrix getMatrix()
  {
    ensureTransformationInfo();
    Matrix localMatrix = this.mTransformationInfo.mMatrix;
    this.mRenderNode.getMatrix(localMatrix);
    return localMatrix;
  }
  
  public final int getMeasuredHeight()
  {
    return this.mMeasuredHeight & 0xFFFFFF;
  }
  
  @ViewDebug.ExportedProperty(category="measurement", flagMapping={@ViewDebug.FlagToString(equals=16777216, mask=-16777216, name="MEASURED_STATE_TOO_SMALL")})
  public final int getMeasuredHeightAndState()
  {
    return this.mMeasuredHeight;
  }
  
  public final int getMeasuredState()
  {
    return this.mMeasuredWidth & 0xFF000000 | this.mMeasuredHeight >> 16 & 0xFF00;
  }
  
  public final int getMeasuredWidth()
  {
    return this.mMeasuredWidth & 0xFFFFFF;
  }
  
  @ViewDebug.ExportedProperty(category="measurement", flagMapping={@ViewDebug.FlagToString(equals=16777216, mask=-16777216, name="MEASURED_STATE_TOO_SMALL")})
  public final int getMeasuredWidthAndState()
  {
    return this.mMeasuredWidth;
  }
  
  public int getMinimumHeight()
  {
    return this.mMinHeight;
  }
  
  public int getMinimumWidth()
  {
    return this.mMinWidth;
  }
  
  public int getNextClusterForwardId()
  {
    return this.mNextClusterForwardId;
  }
  
  public int getNextFocusDownId()
  {
    return this.mNextFocusDownId;
  }
  
  public int getNextFocusForwardId()
  {
    return this.mNextFocusForwardId;
  }
  
  public int getNextFocusLeftId()
  {
    return this.mNextFocusLeftId;
  }
  
  public int getNextFocusRightId()
  {
    return this.mNextFocusRightId;
  }
  
  public int getNextFocusUpId()
  {
    return this.mNextFocusUpId;
  }
  
  public OnFocusChangeListener getOnFocusChangeListener()
  {
    Object localObject = this.mListenerInfo;
    if (localObject != null) {
      localObject = ((ListenerInfo)localObject).mOnFocusChangeListener;
    } else {
      localObject = null;
    }
    return (OnFocusChangeListener)localObject;
  }
  
  public Insets getOpticalInsets()
  {
    if (this.mLayoutInsets == null) {
      this.mLayoutInsets = computeOpticalInsets();
    }
    return this.mLayoutInsets;
  }
  
  public int getOutlineAmbientShadowColor()
  {
    return this.mRenderNode.getAmbientShadowColor();
  }
  
  public ViewOutlineProvider getOutlineProvider()
  {
    return this.mOutlineProvider;
  }
  
  public int getOutlineSpotShadowColor()
  {
    return this.mRenderNode.getSpotShadowColor();
  }
  
  public void getOutsets(Rect paramRect)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      paramRect.set(localAttachInfo.mOutsets);
    } else {
      paramRect.setEmpty();
    }
  }
  
  public int getOverScrollMode()
  {
    return this.mOverScrollMode;
  }
  
  public ViewOverlay getOverlay()
  {
    if (this.mOverlay == null) {
      this.mOverlay = new ViewOverlay(this.mContext, this);
    }
    return this.mOverlay;
  }
  
  public int getPaddingBottom()
  {
    return this.mPaddingBottom;
  }
  
  public int getPaddingEnd()
  {
    if (!isPaddingResolved()) {
      resolvePadding();
    }
    int i;
    if (getLayoutDirection() == 1) {
      i = this.mPaddingLeft;
    } else {
      i = this.mPaddingRight;
    }
    return i;
  }
  
  public int getPaddingLeft()
  {
    if (!isPaddingResolved()) {
      resolvePadding();
    }
    return this.mPaddingLeft;
  }
  
  public int getPaddingRight()
  {
    if (!isPaddingResolved()) {
      resolvePadding();
    }
    return this.mPaddingRight;
  }
  
  public int getPaddingStart()
  {
    if (!isPaddingResolved()) {
      resolvePadding();
    }
    int i;
    if (getLayoutDirection() == 1) {
      i = this.mPaddingRight;
    } else {
      i = this.mPaddingLeft;
    }
    return i;
  }
  
  public int getPaddingTop()
  {
    return this.mPaddingTop;
  }
  
  public final ViewParent getParent()
  {
    return this.mParent;
  }
  
  public ViewParent getParentForAccessibility()
  {
    ViewParent localViewParent = this.mParent;
    if ((localViewParent instanceof View))
    {
      if (((View)localViewParent).includeForAccessibility()) {
        return this.mParent;
      }
      return this.mParent.getParentForAccessibility();
    }
    return null;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getPivotX()
  {
    return this.mRenderNode.getPivotX();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getPivotY()
  {
    return this.mRenderNode.getPivotY();
  }
  
  public PointerIcon getPointerIcon()
  {
    return this.mPointerIcon;
  }
  
  @ViewDebug.ExportedProperty(category="layout", mapping={@ViewDebug.IntToString(from=0, to="LTR"), @ViewDebug.IntToString(from=1, to="RTL"), @ViewDebug.IntToString(from=2, to="INHERIT"), @ViewDebug.IntToString(from=3, to="LOCALE")})
  public int getRawLayoutDirection()
  {
    return (this.mPrivateFlags2 & 0xC) >> 2;
  }
  
  @ViewDebug.ExportedProperty(category="text", mapping={@ViewDebug.IntToString(from=0, to="INHERIT"), @ViewDebug.IntToString(from=1, to="GRAVITY"), @ViewDebug.IntToString(from=2, to="TEXT_START"), @ViewDebug.IntToString(from=3, to="TEXT_END"), @ViewDebug.IntToString(from=4, to="CENTER"), @ViewDebug.IntToString(from=5, to="VIEW_START"), @ViewDebug.IntToString(from=6, to="VIEW_END")})
  @UnsupportedAppUsage
  public int getRawTextAlignment()
  {
    return (this.mPrivateFlags2 & 0xE000) >> 13;
  }
  
  @ViewDebug.ExportedProperty(category="text", mapping={@ViewDebug.IntToString(from=0, to="INHERIT"), @ViewDebug.IntToString(from=1, to="FIRST_STRONG"), @ViewDebug.IntToString(from=2, to="ANY_RTL"), @ViewDebug.IntToString(from=3, to="LTR"), @ViewDebug.IntToString(from=4, to="RTL"), @ViewDebug.IntToString(from=5, to="LOCALE"), @ViewDebug.IntToString(from=6, to="FIRST_STRONG_LTR"), @ViewDebug.IntToString(from=7, to="FIRST_STRONG_RTL")})
  @UnsupportedAppUsage
  public int getRawTextDirection()
  {
    return (this.mPrivateFlags2 & 0x1C0) >> 6;
  }
  
  @UnsupportedAppUsage
  public RenderNode getRenderNode()
  {
    return this.mRenderNode;
  }
  
  public Resources getResources()
  {
    return this.mResources;
  }
  
  public final boolean getRevealOnFocusHint()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x4000000) == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.CapturedViewProperty
  public final int getRight()
  {
    return this.mRight;
  }
  
  protected float getRightFadingEdgeStrength()
  {
    float f;
    if (computeHorizontalScrollOffset() + computeHorizontalScrollExtent() < computeHorizontalScrollRange()) {
      f = 1.0F;
    } else {
      f = 0.0F;
    }
    return f;
  }
  
  protected int getRightPaddingOffset()
  {
    return 0;
  }
  
  public View getRootView()
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null)
    {
      localObject = ((AttachInfo)localObject).mRootView;
      if (localObject != null) {
        return (View)localObject;
      }
    }
    ViewParent localViewParent;
    for (localObject = this;; localObject = (View)localViewParent)
    {
      localViewParent = ((View)localObject).mParent;
      if ((localViewParent == null) || (!(localViewParent instanceof View))) {
        break;
      }
    }
    return (View)localObject;
  }
  
  public WindowInsets getRootWindowInsets()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      return localAttachInfo.mViewRootImpl.getWindowInsets(false);
    }
    return null;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getRotation()
  {
    return this.mRenderNode.getRotationZ();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getRotationX()
  {
    return this.mRenderNode.getRotationX();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getRotationY()
  {
    return this.mRenderNode.getRotationY();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getScaleX()
  {
    return this.mRenderNode.getScaleX();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getScaleY()
  {
    return this.mRenderNode.getScaleY();
  }
  
  public int getScrollBarDefaultDelayBeforeFade()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    int i;
    if (localScrollabilityCache == null) {
      i = ViewConfiguration.getScrollDefaultDelay();
    } else {
      i = localScrollabilityCache.scrollBarDefaultDelayBeforeFade;
    }
    return i;
  }
  
  public int getScrollBarFadeDuration()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    int i;
    if (localScrollabilityCache == null) {
      i = ViewConfiguration.getScrollBarFadeDuration();
    } else {
      i = localScrollabilityCache.scrollBarFadeDuration;
    }
    return i;
  }
  
  public int getScrollBarSize()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    int i;
    if (localScrollabilityCache == null) {
      i = ViewConfiguration.get(this.mContext).getScaledScrollBarSize();
    } else {
      i = localScrollabilityCache.scrollBarSize;
    }
    return i;
  }
  
  @ViewDebug.ExportedProperty(mapping={@ViewDebug.IntToString(from=0, to="INSIDE_OVERLAY"), @ViewDebug.IntToString(from=16777216, to="INSIDE_INSET"), @ViewDebug.IntToString(from=33554432, to="OUTSIDE_OVERLAY"), @ViewDebug.IntToString(from=50331648, to="OUTSIDE_INSET")})
  public int getScrollBarStyle()
  {
    return this.mViewFlags & 0x3000000;
  }
  
  void getScrollIndicatorBounds(Rect paramRect)
  {
    int i = this.mScrollX;
    paramRect.left = i;
    paramRect.right = (i + this.mRight - this.mLeft);
    i = this.mScrollY;
    paramRect.top = i;
    paramRect.bottom = (i + this.mBottom - this.mTop);
  }
  
  public int getScrollIndicators()
  {
    return (this.mPrivateFlags3 & 0x3F00) >>> 8;
  }
  
  public final int getScrollX()
  {
    return this.mScrollX;
  }
  
  public final int getScrollY()
  {
    return this.mScrollY;
  }
  
  View getSelfOrParentImportantForA11y()
  {
    if (isImportantForAccessibility()) {
      return this;
    }
    ViewParent localViewParent = getParentForAccessibility();
    if ((localViewParent instanceof View)) {
      return (View)localViewParent;
    }
    return null;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public int getSolidColor()
  {
    return 0;
  }
  
  public int getSourceLayoutResId()
  {
    return this.mSourceLayoutId;
  }
  
  public StateListAnimator getStateListAnimator()
  {
    return this.mStateListAnimator;
  }
  
  protected int getSuggestedMinimumHeight()
  {
    Drawable localDrawable = this.mBackground;
    int i;
    if (localDrawable == null) {
      i = this.mMinHeight;
    } else {
      i = Math.max(this.mMinHeight, localDrawable.getMinimumHeight());
    }
    return i;
  }
  
  protected int getSuggestedMinimumWidth()
  {
    Drawable localDrawable = this.mBackground;
    int i;
    if (localDrawable == null) {
      i = this.mMinWidth;
    } else {
      i = Math.max(this.mMinWidth, localDrawable.getMinimumWidth());
    }
    return i;
  }
  
  public List<Rect> getSystemGestureExclusionRects()
  {
    Object localObject = this.mListenerInfo;
    if (localObject != null)
    {
      localObject = ((ListenerInfo)localObject).mSystemGestureExclusionRects;
      if (localObject != null) {
        return (List<Rect>)localObject;
      }
    }
    return Collections.emptyList();
  }
  
  public int getSystemUiVisibility()
  {
    return this.mSystemUiVisibility;
  }
  
  @ViewDebug.ExportedProperty
  public Object getTag()
  {
    return this.mTag;
  }
  
  public Object getTag(int paramInt)
  {
    SparseArray localSparseArray = this.mKeyedTags;
    if (localSparseArray != null) {
      return localSparseArray.get(paramInt);
    }
    return null;
  }
  
  @ViewDebug.ExportedProperty(category="text", mapping={@ViewDebug.IntToString(from=0, to="INHERIT"), @ViewDebug.IntToString(from=1, to="GRAVITY"), @ViewDebug.IntToString(from=2, to="TEXT_START"), @ViewDebug.IntToString(from=3, to="TEXT_END"), @ViewDebug.IntToString(from=4, to="CENTER"), @ViewDebug.IntToString(from=5, to="VIEW_START"), @ViewDebug.IntToString(from=6, to="VIEW_END")})
  public int getTextAlignment()
  {
    return (this.mPrivateFlags2 & 0xE0000) >> 17;
  }
  
  @ViewDebug.ExportedProperty(category="text", mapping={@ViewDebug.IntToString(from=0, to="INHERIT"), @ViewDebug.IntToString(from=1, to="FIRST_STRONG"), @ViewDebug.IntToString(from=2, to="ANY_RTL"), @ViewDebug.IntToString(from=3, to="LTR"), @ViewDebug.IntToString(from=4, to="RTL"), @ViewDebug.IntToString(from=5, to="LOCALE"), @ViewDebug.IntToString(from=6, to="FIRST_STRONG_LTR"), @ViewDebug.IntToString(from=7, to="FIRST_STRONG_RTL")})
  public int getTextDirection()
  {
    return (this.mPrivateFlags2 & 0x1C00) >> 10;
  }
  
  @UnsupportedAppUsage
  public ThreadedRenderer getThreadedRenderer()
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mThreadedRenderer;
    } else {
      localObject = null;
    }
    return (ThreadedRenderer)localObject;
  }
  
  public CharSequence getTooltip()
  {
    return getTooltipText();
  }
  
  public CharSequence getTooltipText()
  {
    Object localObject = this.mTooltipInfo;
    if (localObject != null) {
      localObject = ((TooltipInfo)localObject).mTooltipText;
    } else {
      localObject = null;
    }
    return (CharSequence)localObject;
  }
  
  public View getTooltipView()
  {
    TooltipInfo localTooltipInfo = this.mTooltipInfo;
    if ((localTooltipInfo != null) && (localTooltipInfo.mTooltipPopup != null)) {
      return this.mTooltipInfo.mTooltipPopup.getContentView();
    }
    return null;
  }
  
  @ViewDebug.CapturedViewProperty
  public final int getTop()
  {
    return this.mTop;
  }
  
  protected float getTopFadingEdgeStrength()
  {
    float f;
    if (computeVerticalScrollOffset() > 0) {
      f = 1.0F;
    } else {
      f = 0.0F;
    }
    return f;
  }
  
  protected int getTopPaddingOffset()
  {
    return 0;
  }
  
  public TouchDelegate getTouchDelegate()
  {
    return this.mTouchDelegate;
  }
  
  public ArrayList<View> getTouchables()
  {
    ArrayList localArrayList = new ArrayList();
    addTouchables(localArrayList);
    return localArrayList;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getTransitionAlpha()
  {
    TransformationInfo localTransformationInfo = this.mTransformationInfo;
    float f;
    if (localTransformationInfo != null) {
      f = localTransformationInfo.mTransitionAlpha;
    } else {
      f = 1.0F;
    }
    return f;
  }
  
  @ViewDebug.ExportedProperty
  public String getTransitionName()
  {
    return this.mTransitionName;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getTranslationX()
  {
    return this.mRenderNode.getTranslationX();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getTranslationY()
  {
    return this.mRenderNode.getTranslationY();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getTranslationZ()
  {
    return this.mRenderNode.getTranslationZ();
  }
  
  public long getUniqueDrawingId()
  {
    return this.mRenderNode.getUniqueId();
  }
  
  public int getVerticalFadingEdgeLength()
  {
    if (isVerticalFadingEdgeEnabled())
    {
      ScrollabilityCache localScrollabilityCache = this.mScrollCache;
      if (localScrollabilityCache != null) {
        return localScrollabilityCache.fadingEdgeLength;
      }
    }
    return 0;
  }
  
  @UnsupportedAppUsage
  protected float getVerticalScrollFactor()
  {
    if (this.mVerticalScrollFactor == 0.0F)
    {
      TypedValue localTypedValue = new TypedValue();
      if (this.mContext.getTheme().resolveAttribute(16842829, localTypedValue, true)) {
        this.mVerticalScrollFactor = localTypedValue.getDimension(this.mContext.getResources().getDisplayMetrics());
      } else {
        throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
      }
    }
    return this.mVerticalScrollFactor;
  }
  
  public int getVerticalScrollbarPosition()
  {
    return this.mVerticalScrollbarPosition;
  }
  
  public Drawable getVerticalScrollbarThumbDrawable()
  {
    Object localObject = this.mScrollCache;
    if (localObject != null) {
      localObject = ((ScrollabilityCache)localObject).scrollBar.getVerticalThumbDrawable();
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  public Drawable getVerticalScrollbarTrackDrawable()
  {
    Object localObject = this.mScrollCache;
    if (localObject != null) {
      localObject = ((ScrollabilityCache)localObject).scrollBar.getVerticalTrackDrawable();
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  public int getVerticalScrollbarWidth()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    if (localScrollabilityCache != null)
    {
      ScrollBarDrawable localScrollBarDrawable = localScrollabilityCache.scrollBar;
      if (localScrollBarDrawable != null)
      {
        int i = localScrollBarDrawable.getSize(true);
        int j = i;
        if (i <= 0) {
          j = localScrollabilityCache.scrollBarSize;
        }
        return j;
      }
      return 0;
    }
    return 0;
  }
  
  @UnsupportedAppUsage
  public ViewRootImpl getViewRootImpl()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      return localAttachInfo.mViewRootImpl;
    }
    return null;
  }
  
  public ViewTreeObserver getViewTreeObserver()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      return localAttachInfo.mTreeObserver;
    }
    if (this.mFloatingTreeObserver == null) {
      this.mFloatingTreeObserver = new ViewTreeObserver(this.mContext);
    }
    return this.mFloatingTreeObserver;
  }
  
  @ViewDebug.ExportedProperty(mapping={@ViewDebug.IntToString(from=0, to="VISIBLE"), @ViewDebug.IntToString(from=4, to="INVISIBLE"), @ViewDebug.IntToString(from=8, to="GONE")})
  public int getVisibility()
  {
    return this.mViewFlags & 0xC;
  }
  
  @ViewDebug.ExportedProperty(category="layout")
  public final int getWidth()
  {
    return this.mRight - this.mLeft;
  }
  
  protected IWindow getWindow()
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mWindow;
    } else {
      localObject = null;
    }
    return (IWindow)localObject;
  }
  
  protected int getWindowAttachCount()
  {
    return this.mWindowAttachCount;
  }
  
  @UnsupportedAppUsage
  public void getWindowDisplayFrame(Rect paramRect)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      try
      {
        localAttachInfo.mSession.getDisplayFrame(this.mAttachInfo.mWindow, paramRect);
        return;
      }
      catch (RemoteException paramRect)
      {
        return;
      }
    }
    DisplayManagerGlobal.getInstance().getRealDisplay(0).getRectSize(paramRect);
  }
  
  public WindowId getWindowId()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo == null) {
      return null;
    }
    if (localAttachInfo.mWindowId == null) {
      try
      {
        localAttachInfo.mIWindowId = localAttachInfo.mSession.getWindowId(localAttachInfo.mWindowToken);
        if (localAttachInfo.mIWindowId != null)
        {
          WindowId localWindowId = new android/view/WindowId;
          localWindowId.<init>(localAttachInfo.mIWindowId);
          localAttachInfo.mWindowId = localWindowId;
        }
      }
      catch (RemoteException localRemoteException) {}
    }
    return localAttachInfo.mWindowId;
  }
  
  public WindowInsetsController getWindowInsetsController()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      return localAttachInfo.mViewRootImpl.getInsetsController();
    }
    return null;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  IWindowSession getWindowSession()
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mSession;
    } else {
      localObject = null;
    }
    return (IWindowSession)localObject;
  }
  
  public int getWindowSystemUiVisibility()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    int i;
    if (localAttachInfo != null) {
      i = localAttachInfo.mSystemUiVisibility;
    } else {
      i = 0;
    }
    return i;
  }
  
  public IBinder getWindowToken()
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mWindowToken;
    } else {
      localObject = null;
    }
    return (IBinder)localObject;
  }
  
  public int getWindowVisibility()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    int i;
    if (localAttachInfo != null) {
      i = localAttachInfo.mWindowVisibility;
    } else {
      i = 8;
    }
    return i;
  }
  
  public void getWindowVisibleDisplayFrame(Rect paramRect)
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      try
      {
        ((AttachInfo)localObject).mSession.getDisplayFrame(this.mAttachInfo.mWindow, paramRect);
        localObject = this.mResources;
        if ((localObject != null) && (((Resources)localObject).getConfiguration().windowConfiguration.getWindowingMode() == 5))
        {
          MiuiMultiWindowAdapter.getWindowVisibleDisplayFrame(this.mContext, paramRect);
          return;
        }
        localObject = this.mAttachInfo.mVisibleInsets;
        paramRect.left += ((Rect)localObject).left;
        paramRect.top += ((Rect)localObject).top;
        paramRect.right -= ((Rect)localObject).right;
        paramRect.bottom -= ((Rect)localObject).bottom;
        return;
      }
      catch (RemoteException paramRect)
      {
        return;
      }
    }
    DisplayManagerGlobal.getInstance().getRealDisplay(0).getRectSize(paramRect);
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getX()
  {
    return this.mLeft + getTranslationX();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getY()
  {
    return this.mTop + getTranslationY();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public float getZ()
  {
    return getElevation() + getTranslationZ();
  }
  
  void handleFocusGainInternal(int paramInt, Rect paramRect)
  {
    int i = this.mPrivateFlags;
    if ((i & 0x2) == 0)
    {
      this.mPrivateFlags = (i | 0x2);
      View localView;
      if (this.mAttachInfo != null) {
        localView = getRootView().findFocus();
      } else {
        localView = null;
      }
      Object localObject = this.mParent;
      if (localObject != null)
      {
        ((ViewParent)localObject).requestChildFocus(this, this);
        updateFocusedInCluster(localView, paramInt);
      }
      localObject = this.mAttachInfo;
      if (localObject != null) {
        ((AttachInfo)localObject).mTreeObserver.dispatchOnGlobalFocusChange(localView, this);
      }
      onFocusChanged(true, paramInt, paramRect);
      refreshDrawableState();
    }
  }
  
  protected boolean handleScrollBarDragging(MotionEvent paramMotionEvent)
  {
    if (this.mScrollCache == null) {
      return false;
    }
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    int i = paramMotionEvent.getAction();
    if (((this.mScrollCache.mScrollBarDraggingState != 0) || (i == 0)) && (paramMotionEvent.isFromSource(8194)) && (paramMotionEvent.isButtonPressed(1)))
    {
      if (i != 0)
      {
        if (i != 2) {
          break label545;
        }
        if (this.mScrollCache.mScrollBarDraggingState == 0) {
          return false;
        }
        int j;
        int k;
        int m;
        float f3;
        if (this.mScrollCache.mScrollBarDraggingState == 1)
        {
          paramMotionEvent = this.mScrollCache.mScrollBarBounds;
          getVerticalScrollBarBounds(paramMotionEvent, null);
          j = computeVerticalScrollRange();
          k = computeVerticalScrollOffset();
          i = computeVerticalScrollExtent();
          m = ScrollBarUtils.getThumbLength(paramMotionEvent.height(), paramMotionEvent.width(), i, j);
          k = ScrollBarUtils.getThumbOffset(paramMotionEvent.height(), m, i, j, k);
          f3 = this.mScrollCache.mScrollBarDraggingPos;
          f1 = paramMotionEvent.height() - m;
          f3 = Math.min(Math.max(k + (f2 - f3), 0.0F), f1);
          m = getHeight();
          if ((Math.round(f3) != k) && (f1 > 0.0F) && (m > 0) && (i > 0))
          {
            i = Math.round((j - i) / (i / m) * (f3 / f1));
            if (i != getScrollY())
            {
              this.mScrollCache.mScrollBarDraggingPos = f2;
              setScrollY(i);
            }
          }
          return true;
        }
        if (this.mScrollCache.mScrollBarDraggingState == 2)
        {
          paramMotionEvent = this.mScrollCache.mScrollBarBounds;
          getHorizontalScrollBarBounds(paramMotionEvent, null);
          j = computeHorizontalScrollRange();
          k = computeHorizontalScrollOffset();
          i = computeHorizontalScrollExtent();
          m = ScrollBarUtils.getThumbLength(paramMotionEvent.width(), paramMotionEvent.height(), i, j);
          k = ScrollBarUtils.getThumbOffset(paramMotionEvent.width(), m, i, j, k);
          f3 = this.mScrollCache.mScrollBarDraggingPos;
          f2 = paramMotionEvent.width() - m;
          f3 = Math.min(Math.max(k + (f1 - f3), 0.0F), f2);
          m = getWidth();
          if ((Math.round(f3) != k) && (f2 > 0.0F) && (m > 0) && (i > 0))
          {
            i = Math.round((j - i) / (i / m) * (f3 / f2));
            if (i != getScrollX())
            {
              this.mScrollCache.mScrollBarDraggingPos = f1;
              setScrollX(i);
            }
          }
          return true;
        }
      }
      if (this.mScrollCache.state == 0) {
        return false;
      }
      if (isOnVerticalScrollbarThumb(f1, f2))
      {
        paramMotionEvent = this.mScrollCache;
        paramMotionEvent.mScrollBarDraggingState = 1;
        paramMotionEvent.mScrollBarDraggingPos = f2;
        return true;
      }
      if (isOnHorizontalScrollbarThumb(f1, f2))
      {
        paramMotionEvent = this.mScrollCache;
        paramMotionEvent.mScrollBarDraggingState = 2;
        paramMotionEvent.mScrollBarDraggingPos = f1;
        return true;
      }
      label545:
      this.mScrollCache.mScrollBarDraggingState = 0;
      return false;
    }
    this.mScrollCache.mScrollBarDraggingState = 0;
    return false;
  }
  
  void handleTooltipKey(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getAction();
    if (i != 0)
    {
      if (i == 1) {
        handleTooltipUp();
      }
    }
    else if (paramKeyEvent.getRepeatCount() == 0) {
      hideTooltip();
    }
  }
  
  boolean hasDefaultFocus()
  {
    return isFocusedByDefault();
  }
  
  public boolean hasExplicitFocusable()
  {
    return hasFocusable(false, true);
  }
  
  @ViewDebug.ExportedProperty(category="focus")
  public boolean hasFocus()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x2) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean hasFocusable()
  {
    return hasFocusable(sHasFocusableExcludeAutoFocusable ^ true, false);
  }
  
  boolean hasFocusable(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!isFocusableInTouchMode()) {
      for (ViewParent localViewParent = this.mParent; (localViewParent instanceof ViewGroup); localViewParent = localViewParent.getParent()) {
        if (((ViewGroup)localViewParent).shouldBlockFocusForTouchscreen()) {
          return false;
        }
      }
    }
    int i = this.mViewFlags;
    if (((i & 0xC) == 0) && ((i & 0x20) == 0)) {
      return ((paramBoolean1) || (getFocusable() != 16)) && (isFocusable());
    }
    return false;
  }
  
  protected boolean hasHoveredChild()
  {
    return false;
  }
  
  @UnsupportedAppUsage
  public final boolean hasIdentityMatrix()
  {
    return this.mRenderNode.hasIdentityMatrix();
  }
  
  public boolean hasNestedScrollingParent()
  {
    boolean bool;
    if (this.mNestedScrollingParent != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean hasOnClickListeners()
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    boolean bool;
    if ((localListenerInfo != null) && (localListenerInfo.mOnClickListener != null)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected boolean hasOpaqueScrollbars()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x1000000) == 16777216) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public boolean hasOverlappingRendering()
  {
    return true;
  }
  
  public boolean hasPointerCapture()
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    if (localViewRootImpl == null) {
      return false;
    }
    return localViewRootImpl.hasPointerCapture();
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public boolean hasShadow()
  {
    return this.mRenderNode.hasShadow();
  }
  
  @ViewDebug.ExportedProperty(category="layout")
  public boolean hasTransientState()
  {
    boolean bool;
    if ((this.mPrivateFlags2 & 0x80000000) == Integer.MIN_VALUE) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  boolean hasUnhandledKeyListener()
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    boolean bool;
    if ((localListenerInfo != null) && (localListenerInfo.mUnhandledKeyListeners != null) && (!this.mListenerInfo.mUnhandledKeyListeners.isEmpty())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean hasWindowFocus()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    boolean bool;
    if ((localAttachInfo != null) && (localAttachInfo.mHasWindowFocus)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  void hideTooltip()
  {
    Object localObject = this.mTooltipInfo;
    if (localObject == null) {
      return;
    }
    removeCallbacks(((TooltipInfo)localObject).mShowTooltipRunnable);
    if (this.mTooltipInfo.mTooltipPopup == null) {
      return;
    }
    this.mTooltipInfo.mTooltipPopup.hide();
    localObject = this.mTooltipInfo;
    ((TooltipInfo)localObject).mTooltipPopup = null;
    ((TooltipInfo)localObject).mTooltipFromLongClick = false;
    ((TooltipInfo)localObject).clearAnchorPos();
    localObject = this.mAttachInfo;
    if (localObject != null) {
      ((AttachInfo)localObject).mTooltipHost = null;
    }
    notifyViewAccessibilityStateChangedIfNeeded(0);
  }
  
  @UnsupportedAppUsage
  public boolean includeForAccessibility()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    boolean bool = false;
    if (localAttachInfo != null)
    {
      if (((localAttachInfo.mAccessibilityFetchFlags & 0x8) != 0) || (isImportantForAccessibility())) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  protected void initializeFadingEdge(TypedArray paramTypedArray)
  {
    paramTypedArray = this.mContext.obtainStyledAttributes(R.styleable.View);
    initializeFadingEdgeInternal(paramTypedArray);
    paramTypedArray.recycle();
  }
  
  protected void initializeFadingEdgeInternal(TypedArray paramTypedArray)
  {
    initScrollCache();
    this.mScrollCache.fadingEdgeLength = paramTypedArray.getDimensionPixelSize(25, ViewConfiguration.get(this.mContext).getScaledFadingEdgeLength());
  }
  
  protected void initializeScrollbars(TypedArray paramTypedArray)
  {
    paramTypedArray = this.mContext.obtainStyledAttributes(R.styleable.View);
    initializeScrollbarsInternal(paramTypedArray);
    paramTypedArray.recycle();
  }
  
  @UnsupportedAppUsage
  protected void initializeScrollbarsInternal(TypedArray paramTypedArray)
  {
    initScrollCache();
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    if (localScrollabilityCache.scrollBar == null)
    {
      localScrollabilityCache.scrollBar = new ScrollBarDrawable();
      localScrollabilityCache.scrollBar.setState(getDrawableState());
      localScrollabilityCache.scrollBar.setCallback(this);
    }
    boolean bool = paramTypedArray.getBoolean(47, true);
    if (!bool) {
      localScrollabilityCache.state = 1;
    }
    localScrollabilityCache.fadeScrollBars = bool;
    localScrollabilityCache.scrollBarFadeDuration = paramTypedArray.getInt(45, ViewConfiguration.getScrollBarFadeDuration());
    localScrollabilityCache.scrollBarDefaultDelayBeforeFade = paramTypedArray.getInt(46, ViewConfiguration.getScrollDefaultDelay());
    localScrollabilityCache.scrollBarSize = paramTypedArray.getDimensionPixelSize(1, ViewConfiguration.get(this.mContext).getScaledScrollBarSize());
    Drawable localDrawable1 = paramTypedArray.getDrawable(4);
    localScrollabilityCache.scrollBar.setHorizontalTrackDrawable(localDrawable1);
    localDrawable1 = paramTypedArray.getDrawable(2);
    if (localDrawable1 != null) {
      localScrollabilityCache.scrollBar.setHorizontalThumbDrawable(localDrawable1);
    }
    if (paramTypedArray.getBoolean(6, false)) {
      localScrollabilityCache.scrollBar.setAlwaysDrawHorizontalTrack(true);
    }
    Drawable localDrawable2 = paramTypedArray.getDrawable(5);
    localScrollabilityCache.scrollBar.setVerticalTrackDrawable(localDrawable2);
    localDrawable1 = paramTypedArray.getDrawable(3);
    if (localDrawable1 != null) {
      localScrollabilityCache.scrollBar.setVerticalThumbDrawable(localDrawable1);
    }
    if (paramTypedArray.getBoolean(7, false)) {
      localScrollabilityCache.scrollBar.setAlwaysDrawVerticalTrack(true);
    }
    int i = getLayoutDirection();
    if (localDrawable2 != null) {
      localDrawable2.setLayoutDirection(i);
    }
    if (localDrawable1 != null) {
      localDrawable1.setLayoutDirection(i);
    }
    resolvePadding();
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768420L)
  protected void internalSetPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mUserPaddingLeft = paramInt1;
    this.mUserPaddingRight = paramInt3;
    this.mUserPaddingBottom = paramInt4;
    int i = this.mViewFlags;
    int j = 0;
    int k = paramInt1;
    int m = paramInt3;
    int n = paramInt4;
    if ((i & 0x300) != 0)
    {
      int i1 = 0;
      int i2 = paramInt1;
      int i3 = paramInt3;
      if ((i & 0x200) != 0)
      {
        if ((i & 0x1000000) == 0) {
          i2 = 0;
        } else {
          i2 = getVerticalScrollbarWidth();
        }
        i3 = this.mVerticalScrollbarPosition;
        if (i3 != 0)
        {
          if (i3 != 1)
          {
            if (i3 != 2)
            {
              i2 = paramInt1;
              i3 = paramInt3;
            }
            else
            {
              i3 = paramInt3 + i2;
              i2 = paramInt1;
            }
          }
          else
          {
            i2 = paramInt1 + i2;
            i3 = paramInt3;
          }
        }
        else if (isLayoutRtl())
        {
          i2 = paramInt1 + i2;
          i3 = paramInt3;
        }
        else
        {
          i3 = paramInt3 + i2;
          i2 = paramInt1;
        }
      }
      k = i2;
      m = i3;
      n = paramInt4;
      if ((i & 0x100) != 0)
      {
        if ((i & 0x1000000) == 0) {
          paramInt1 = i1;
        } else {
          paramInt1 = getHorizontalScrollbarHeight();
        }
        n = paramInt4 + paramInt1;
        m = i3;
        k = i2;
      }
    }
    paramInt1 = j;
    if (this.mPaddingLeft != k)
    {
      paramInt1 = 1;
      this.mPaddingLeft = k;
    }
    if (this.mPaddingTop != paramInt2)
    {
      paramInt1 = 1;
      this.mPaddingTop = paramInt2;
    }
    if (this.mPaddingRight != m)
    {
      paramInt1 = 1;
      this.mPaddingRight = m;
    }
    if (this.mPaddingBottom != n)
    {
      paramInt1 = 1;
      this.mPaddingBottom = n;
    }
    if (paramInt1 != 0)
    {
      requestLayout();
      invalidateOutline();
    }
  }
  
  public void invalidate()
  {
    invalidate(true);
  }
  
  @Deprecated
  public void invalidate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mScrollX;
    int j = this.mScrollY;
    invalidateInternal(paramInt1 - i, paramInt2 - j, paramInt3 - i, paramInt4 - j, true, false);
  }
  
  @Deprecated
  public void invalidate(Rect paramRect)
  {
    int i = this.mScrollX;
    int j = this.mScrollY;
    invalidateInternal(paramRect.left - i, paramRect.top - j, paramRect.right - i, paramRect.bottom - j, true, false);
  }
  
  @UnsupportedAppUsage
  public void invalidate(boolean paramBoolean)
  {
    invalidateInternal(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop, paramBoolean, true);
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (verifyDrawable(paramDrawable))
    {
      paramDrawable = paramDrawable.getDirtyBounds();
      int i = this.mScrollX;
      int j = this.mScrollY;
      invalidate(paramDrawable.left + i, paramDrawable.top + j, paramDrawable.right + i, paramDrawable.bottom + j);
      rebuildOutline();
    }
  }
  
  void invalidateInheritedLayoutMode(int paramInt) {}
  
  void invalidateInternal(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject1 = this.mGhostView;
    if (localObject1 != null)
    {
      ((GhostView)localObject1).invalidate(true);
      return;
    }
    if (skipInvalidate()) {
      return;
    }
    this.mCachedContentCaptureSession = null;
    int i = this.mPrivateFlags;
    if (((i & 0x30) == 48) || ((paramBoolean1) && ((i & 0x8000) == 32768)) || ((this.mPrivateFlags & 0x80000000) != Integer.MIN_VALUE) || ((paramBoolean2) && (isOpaque() != this.mLastIsOpaque)))
    {
      if (paramBoolean2)
      {
        this.mLastIsOpaque = isOpaque();
        this.mPrivateFlags &= 0xFFFFFFDF;
      }
      this.mPrivateFlags |= 0x200000;
      if (paramBoolean1)
      {
        this.mPrivateFlags |= 0x80000000;
        this.mPrivateFlags &= 0xFFFF7FFF;
      }
      Object localObject2 = this.mAttachInfo;
      localObject1 = this.mParent;
      if ((localObject1 != null) && (localObject2 != null) && (paramInt1 < paramInt3) && (paramInt2 < paramInt4))
      {
        localObject2 = ((AttachInfo)localObject2).mTmpInvalRect;
        ((Rect)localObject2).set(paramInt1, paramInt2, paramInt3, paramInt4);
        ((ViewParent)localObject1).invalidateChild(this, (Rect)localObject2);
      }
      localObject1 = this.mBackground;
      if ((localObject1 != null) && (((Drawable)localObject1).isProjected()))
      {
        localObject1 = getProjectionReceiver();
        if (localObject1 != null) {
          ((View)localObject1).damageInParent();
        }
      }
    }
  }
  
  public void invalidateOutline()
  {
    rebuildOutline();
    notifySubtreeAccessibilityStateChangedIfNeeded();
    invalidateViewProperty(false, false);
  }
  
  @UnsupportedAppUsage
  protected void invalidateParentCaches()
  {
    Object localObject = this.mParent;
    if ((localObject instanceof View))
    {
      localObject = (View)localObject;
      ((View)localObject).mPrivateFlags |= 0x80000000;
    }
  }
  
  @UnsupportedAppUsage
  protected void invalidateParentIfNeeded()
  {
    if (isHardwareAccelerated())
    {
      ViewParent localViewParent = this.mParent;
      if ((localViewParent instanceof View)) {
        ((View)localViewParent).invalidate(true);
      }
    }
  }
  
  protected void invalidateParentIfNeededAndWasQuickRejected()
  {
    if ((this.mPrivateFlags2 & 0x10000000) != 0) {
      invalidateParentIfNeeded();
    }
  }
  
  @UnsupportedAppUsage
  void invalidateViewProperty(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((isHardwareAccelerated()) && (this.mRenderNode.hasDisplayList()) && ((this.mPrivateFlags & 0x40) == 0))
    {
      damageInParent();
    }
    else
    {
      if (paramBoolean1) {
        invalidateParentCaches();
      }
      if (paramBoolean2) {
        this.mPrivateFlags |= 0x20;
      }
      invalidate(false);
    }
  }
  
  public boolean isAccessibilityFocused()
  {
    boolean bool;
    if ((this.mPrivateFlags2 & 0x4000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  boolean isAccessibilityFocusedViewOrHost()
  {
    boolean bool;
    if ((!isAccessibilityFocused()) && ((getViewRootImpl() == null) || (getViewRootImpl().getAccessibilityFocusedHost() != this))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isAccessibilityHeading()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x80000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isAccessibilitySelectionExtendable()
  {
    return false;
  }
  
  public boolean isActionableForAccessibility()
  {
    boolean bool;
    if ((!isClickable()) && (!isLongClickable()) && (!isFocusable())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isActivated()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x40000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isAssistBlocked()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x4000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isAttachedToWindow()
  {
    boolean bool;
    if (this.mAttachInfo != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isAutofilled()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x10000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isClickable()
  {
    boolean bool;
    if ((this.mViewFlags & 0x4000) == 16384) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isContextClickable()
  {
    boolean bool;
    if ((this.mViewFlags & 0x800000) == 8388608) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isDefaultFocusHighlightNeeded(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    boolean bool = false;
    int i;
    if (((paramDrawable1 != null) && (paramDrawable1.isStateful()) && (paramDrawable1.hasFocusStateSpecified())) || ((paramDrawable2 != null) && (paramDrawable2.isStateful()) && (paramDrawable2.hasFocusStateSpecified()))) {
      i = 0;
    } else {
      i = 1;
    }
    if ((!isInTouchMode()) && (getDefaultFocusHighlightEnabled()) && (i != 0) && (isAttachedToWindow()) && (sUseDefaultFocusHighlight)) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isDirty()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x200000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  boolean isDraggingScrollBar()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    boolean bool;
    if ((localScrollabilityCache != null) && (localScrollabilityCache.mScrollBarDraggingState != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  @Deprecated
  public boolean isDrawingCacheEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x8000) == 32768) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isDuplicateParentStateEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x400000) == 4194304) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x20) == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="focus")
  public final boolean isFocusable()
  {
    int i = this.mViewFlags;
    boolean bool = true;
    if (1 != (i & 0x1)) {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="focus")
  public final boolean isFocusableInTouchMode()
  {
    boolean bool;
    if (262144 == (this.mViewFlags & 0x40000)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="focus")
  public boolean isFocused()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x2) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="focus")
  public final boolean isFocusedByDefault()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x40000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public boolean isForceDarkAllowed()
  {
    return this.mRenderNode.isForceDarkAllowed();
  }
  
  public boolean isForegroundInsidePadding()
  {
    ForegroundInfo localForegroundInfo = this.mForegroundInfo;
    boolean bool;
    if (localForegroundInfo != null) {
      bool = localForegroundInfo.mInsidePadding;
    } else {
      bool = true;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isHapticFeedbackEnabled()
  {
    boolean bool;
    if (268435456 == (this.mViewFlags & 0x10000000)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public boolean isHardwareAccelerated()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    boolean bool;
    if ((localAttachInfo != null) && (localAttachInfo.mHardwareAccelerated)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isHorizontalFadingEdgeEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x1000) == 4096) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isHorizontalScrollBarEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x100) == 256) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isHovered()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x10000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isImportantForAccessibility()
  {
    int i = (this.mPrivateFlags2 & 0x700000) >> 20;
    boolean bool = false;
    if ((i != 2) && (i != 4))
    {
      for (ViewParent localViewParent = this.mParent; (localViewParent instanceof View); localViewParent = localViewParent.getParent()) {
        if (((View)localViewParent).getImportantForAccessibility() == 4) {
          return false;
        }
      }
      if ((i == 1) || (isActionableForAccessibility()) || (hasListenersForAccessibility()) || (getAccessibilityNodeProvider() != null) || (getAccessibilityLiveRegion() != 0) || (isAccessibilityPane())) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public final boolean isImportantForAutofill()
  {
    Object localObject1 = this.mParent;
    Object localObject2;
    while ((localObject1 instanceof View))
    {
      i = ((View)localObject1).getImportantForAutofill();
      if ((i != 8) && (i != 4))
      {
        localObject1 = ((ViewParent)localObject1).getParent();
      }
      else
      {
        if (Log.isLoggable("View.Autofill", 2))
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("View (");
          ((StringBuilder)localObject2).append(this);
          ((StringBuilder)localObject2).append(") is not important for autofill because parent ");
          ((StringBuilder)localObject2).append(localObject1);
          ((StringBuilder)localObject2).append("'s importance is ");
          ((StringBuilder)localObject2).append(i);
          Log.v("View.Autofill", ((StringBuilder)localObject2).toString());
        }
        return false;
      }
    }
    int i = getImportantForAutofill();
    if ((i != 4) && (i != 1))
    {
      if ((i != 8) && (i != 2))
      {
        if (i != 0)
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("invalid autofill importance (");
          ((StringBuilder)localObject1).append(i);
          ((StringBuilder)localObject1).append(" on view ");
          ((StringBuilder)localObject1).append(this);
          Log.w("View.Autofill", ((StringBuilder)localObject1).toString());
          return false;
        }
        i = this.mID;
        if ((i != -1) && (!isViewIdGenerated(i)))
        {
          Object localObject3 = getResources();
          localObject1 = null;
          Object localObject4 = null;
          try
          {
            localObject2 = ((Resources)localObject3).getResourceEntryName(i);
            localObject1 = localObject2;
            localObject3 = ((Resources)localObject3).getResourcePackageName(i);
            localObject4 = localObject3;
            localObject1 = localObject2;
          }
          catch (Resources.NotFoundException localNotFoundException) {}
          if ((localObject1 != null) && (localObject4 != null) && (((String)localObject4).equals(this.mContext.getPackageName()))) {
            return true;
          }
        }
        return getAutofillHints() != null;
      }
      if (Log.isLoggable("View.Autofill", 2))
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("View (");
        ((StringBuilder)localObject1).append(this);
        ((StringBuilder)localObject1).append(") is not important for autofill because its importance is ");
        ((StringBuilder)localObject1).append(i);
        Log.v("View.Autofill", ((StringBuilder)localObject1).toString());
      }
      return false;
    }
    return true;
  }
  
  public boolean isInEditMode()
  {
    return false;
  }
  
  public boolean isInLayout()
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    boolean bool;
    if ((localViewRootImpl != null) && (localViewRootImpl.isInLayout())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  public boolean isInScrollingContainer()
  {
    for (ViewParent localViewParent = getParent(); (localViewParent != null) && ((localViewParent instanceof ViewGroup)); localViewParent = localViewParent.getParent()) {
      if (((ViewGroup)localViewParent).shouldDelayChildPressedState()) {
        return true;
      }
    }
    return false;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isInTouchMode()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      return localAttachInfo.mInTouchMode;
    }
    return ViewRootImpl.isInTouchMode();
  }
  
  @ViewDebug.ExportedProperty(category="focus")
  public final boolean isKeyboardNavigationCluster()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x8000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isLaidOut()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x4) == 4) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isLayoutDirectionInherited()
  {
    boolean bool;
    if (getRawLayoutDirection() == 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isLayoutDirectionResolved()
  {
    boolean bool;
    if ((this.mPrivateFlags2 & 0x20) == 32) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isLayoutRequested()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x1000) == 4096) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="layout")
  @UnsupportedAppUsage
  public boolean isLayoutRtl()
  {
    int i = getLayoutDirection();
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    return bool;
  }
  
  boolean isLayoutValid()
  {
    boolean bool;
    if ((isLaidOut()) && ((this.mPrivateFlags & 0x1000) == 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isLongClickable()
  {
    boolean bool;
    if ((this.mViewFlags & 0x200000) == 2097152) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isNestedScrollingEnabled()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x80) == 128) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  boolean isOnScrollbar(float paramFloat1, float paramFloat2)
  {
    if (this.mScrollCache == null) {
      return false;
    }
    paramFloat1 += getScrollX();
    paramFloat2 += getScrollY();
    int i;
    if (computeVerticalScrollRange() > computeVerticalScrollExtent()) {
      i = 1;
    } else {
      i = 0;
    }
    Rect localRect;
    if ((isVerticalScrollBarEnabled()) && (!isVerticalScrollBarHidden()) && (i != 0))
    {
      localRect = this.mScrollCache.mScrollBarTouchBounds;
      getVerticalScrollBarBounds(null, localRect);
      if (localRect.contains((int)paramFloat1, (int)paramFloat2)) {
        return true;
      }
    }
    if (computeHorizontalScrollRange() > computeHorizontalScrollExtent()) {
      i = 1;
    } else {
      i = 0;
    }
    if ((isHorizontalScrollBarEnabled()) && (i != 0))
    {
      localRect = this.mScrollCache.mScrollBarTouchBounds;
      getHorizontalScrollBarBounds(null, localRect);
      if (localRect.contains((int)paramFloat1, (int)paramFloat2)) {
        return true;
      }
    }
    return false;
  }
  
  @UnsupportedAppUsage
  boolean isOnScrollbarThumb(float paramFloat1, float paramFloat2)
  {
    boolean bool;
    if ((!isOnVerticalScrollbarThumb(paramFloat1, paramFloat2)) && (!isOnHorizontalScrollbarThumb(paramFloat1, paramFloat2))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public boolean isOpaque()
  {
    boolean bool;
    if (((this.mPrivateFlags & 0x1800000) == 25165824) && (getFinalAlpha() >= 1.0F)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected boolean isPaddingOffsetRequired()
  {
    return false;
  }
  
  public boolean isPaddingRelative()
  {
    boolean bool;
    if ((this.mUserPaddingStart == Integer.MIN_VALUE) && (this.mUserPaddingEnd == Integer.MIN_VALUE)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  boolean isPaddingResolved()
  {
    boolean bool;
    if ((this.mPrivateFlags2 & 0x20000000) == 536870912) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isPivotSet()
  {
    return this.mRenderNode.isPivotExplicitlySet();
  }
  
  @ViewDebug.ExportedProperty
  public boolean isPressed()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x4000) == 16384) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  public boolean isRootNamespace()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x8) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isSaveEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x10000) != 65536) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isSaveFromParentEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x20000000) != 536870912) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isScreenReaderFocusable()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x10000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isScrollContainer()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x100000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isScrollbarFadingEnabled()
  {
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    boolean bool;
    if ((localScrollabilityCache != null) && (localScrollabilityCache.fadeScrollBars)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isSelected()
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x4) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isShown()
  {
    for (Object localObject = this;; localObject = (View)localObject)
    {
      if ((((View)localObject).mViewFlags & 0xC) != 0) {
        return false;
      }
      localObject = ((View)localObject).mParent;
      if (localObject == null) {
        return false;
      }
      if (!(localObject instanceof View)) {
        return true;
      }
    }
  }
  
  @ViewDebug.ExportedProperty
  public boolean isSoundEffectsEnabled()
  {
    boolean bool;
    if (134217728 == (this.mViewFlags & 0x8000000)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isTemporarilyDetached()
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x2000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isTextAlignmentInherited()
  {
    boolean bool;
    if (getRawTextAlignment() == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isTextAlignmentResolved()
  {
    boolean bool;
    if ((this.mPrivateFlags2 & 0x10000) == 65536) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isTextDirectionInherited()
  {
    boolean bool;
    if (getRawTextDirection() == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isTextDirectionResolved()
  {
    boolean bool;
    if ((this.mPrivateFlags2 & 0x200) == 512) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isVerticalFadingEdgeEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x2000) == 8192) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isVerticalScrollBarEnabled()
  {
    boolean bool;
    if ((this.mViewFlags & 0x200) == 512) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected boolean isVerticalScrollBarHidden()
  {
    return false;
  }
  
  @UnsupportedAppUsage
  public boolean isVisibleToUser()
  {
    return isVisibleToUser(null);
  }
  
  @UnsupportedAppUsage
  protected boolean isVisibleToUser(Rect paramRect)
  {
    Object localObject = this.mAttachInfo;
    if (localObject != null)
    {
      if (((AttachInfo)localObject).mWindowVisibility != 0) {
        return false;
      }
      localObject = this;
      while ((localObject instanceof View))
      {
        localObject = (View)localObject;
        if ((((View)localObject).getAlpha() > 0.0F) && (((View)localObject).getTransitionAlpha() > 0.0F) && (((View)localObject).getVisibility() == 0)) {
          localObject = ((View)localObject).mParent;
        } else {
          return false;
        }
      }
      localObject = this.mAttachInfo.mTmpInvalRect;
      Point localPoint = this.mAttachInfo.mPoint;
      if (!getGlobalVisibleRect((Rect)localObject, localPoint)) {
        return false;
      }
      if (paramRect != null)
      {
        ((Rect)localObject).offset(-localPoint.x, -localPoint.y);
        return paramRect.intersect((Rect)localObject);
      }
      return true;
    }
    return false;
  }
  
  public boolean isVisibleToUserForAutofill(int paramInt)
  {
    if (this.mContext.isAutofillCompatibilityEnabled())
    {
      Object localObject = getAccessibilityNodeProvider();
      if (localObject != null)
      {
        localObject = ((AccessibilityNodeProvider)localObject).createAccessibilityNodeInfo(paramInt);
        if (localObject != null) {
          return ((AccessibilityNodeInfo)localObject).isVisibleToUser();
        }
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("isVisibleToUserForAutofill(");
        ((StringBuilder)localObject).append(paramInt);
        ((StringBuilder)localObject).append("): no provider");
        Log.w("View", ((StringBuilder)localObject).toString());
      }
      return false;
    }
    return true;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    Object localObject = this.mBackground;
    if (localObject != null) {
      ((Drawable)localObject).jumpToCurrentState();
    }
    localObject = this.mStateListAnimator;
    if (localObject != null) {
      ((StateListAnimator)localObject).jumpToCurrentState();
    }
    localObject = this.mDefaultFocusHighlight;
    if (localObject != null) {
      ((Drawable)localObject).jumpToCurrentState();
    }
    localObject = this.mForegroundInfo;
    if ((localObject != null) && (((ForegroundInfo)localObject).mDrawable != null)) {
      this.mForegroundInfo.mDrawable.jumpToCurrentState();
    }
  }
  
  public View keyboardNavigationClusterSearch(View paramView, int paramInt)
  {
    if (isKeyboardNavigationCluster()) {
      paramView = this;
    }
    if (isRootNamespace()) {
      return FocusFinder.getInstance().findNextKeyboardNavigationCluster(this, paramView, paramInt);
    }
    ViewParent localViewParent = this.mParent;
    if (localViewParent != null) {
      return localViewParent.keyboardNavigationClusterSearch(paramView, paramInt);
    }
    return null;
  }
  
  public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.mPrivateFlags3 & 0x8) != 0)
    {
      onMeasure(this.mOldWidthMeasureSpec, this.mOldHeightMeasureSpec);
      this.mPrivateFlags3 &= 0xFFFFFFF7;
    }
    int i = this.mLeft;
    int j = this.mTop;
    int k = this.mBottom;
    int m = this.mRight;
    if (isLayoutModeOptical(this.mParent)) {
      bool = setOpticalFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      bool = setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    View localView = null;
    Object localObject;
    if ((!bool) && ((this.mPrivateFlags & 0x2000) != 8192))
    {
      localView = null;
    }
    else
    {
      onLayout(bool, paramInt1, paramInt2, paramInt3, paramInt4);
      if (shouldDrawRoundScrollbar())
      {
        if (this.mRoundScrollbarRenderer == null) {
          this.mRoundScrollbarRenderer = new RoundScrollbarRenderer(this);
        }
      }
      else {
        this.mRoundScrollbarRenderer = null;
      }
      this.mPrivateFlags &= 0xDFFF;
      localObject = this.mListenerInfo;
      if ((localObject != null) && (((ListenerInfo)localObject).mOnLayoutChangeListeners != null))
      {
        ArrayList localArrayList = (ArrayList)((ListenerInfo)localObject).mOnLayoutChangeListeners.clone();
        int n = localArrayList.size();
        for (int i1 = 0; i1 < n; i1++) {
          ((OnLayoutChangeListener)localArrayList.get(i1)).onLayoutChange(this, paramInt1, paramInt2, paramInt3, paramInt4, i, j, m, k);
        }
      }
      else
      {
        localView = null;
      }
    }
    boolean bool = isLayoutValid();
    this.mPrivateFlags &= 0xEFFF;
    this.mPrivateFlags3 |= 0x4;
    if ((!bool) && (isFocused()))
    {
      this.mPrivateFlags &= 0xFFFFFFFE;
      if (canTakeFocus())
      {
        clearParentsWantFocus();
      }
      else if ((getViewRootImpl() != null) && (getViewRootImpl().isInLayout()))
      {
        if (!hasParentWantsFocus()) {
          clearFocusInternal(localView, true, false);
        }
      }
      else
      {
        clearFocusInternal(localView, true, false);
        clearParentsWantFocus();
      }
    }
    else
    {
      paramInt1 = this.mPrivateFlags;
      if ((paramInt1 & 0x1) != 0)
      {
        this.mPrivateFlags = (paramInt1 & 0xFFFFFFFE);
        localObject = findFocus();
        if ((localObject != null) && (!restoreDefaultFocus()) && (!hasParentWantsFocus())) {
          ((View)localObject).clearFocusInternal(localView, true, false);
        }
      }
    }
    paramInt1 = this.mPrivateFlags3;
    if ((0x8000000 & paramInt1) != 0)
    {
      this.mPrivateFlags3 = (paramInt1 & 0xF7FFFFFF);
      notifyEnterOrExitForAutoFillIfNeeded(true);
    }
  }
  
  @UnsupportedAppUsage
  public void makeOptionalFitsSystemWindows()
  {
    setFlags(2048, 2048);
  }
  
  public void mapRectFromViewToScreenCoords(RectF paramRectF, boolean paramBoolean)
  {
    if (!hasIdentityMatrix()) {
      getMatrix().mapRect(paramRectF);
    }
    paramRectF.offset(this.mLeft, this.mTop);
    for (Object localObject = this.mParent; (localObject instanceof View); localObject = ((View)localObject).mParent)
    {
      localObject = (View)localObject;
      paramRectF.offset(-((View)localObject).mScrollX, -((View)localObject).mScrollY);
      if (paramBoolean)
      {
        paramRectF.left = Math.max(paramRectF.left, 0.0F);
        paramRectF.top = Math.max(paramRectF.top, 0.0F);
        paramRectF.right = Math.min(paramRectF.right, ((View)localObject).getWidth());
        paramRectF.bottom = Math.min(paramRectF.bottom, ((View)localObject).getHeight());
      }
      if (!((View)localObject).hasIdentityMatrix()) {
        ((View)localObject).getMatrix().mapRect(paramRectF);
      }
      paramRectF.offset(((View)localObject).mLeft, ((View)localObject).mTop);
    }
    if ((localObject instanceof ViewRootImpl)) {
      paramRectF.offset(0.0F, -((ViewRootImpl)localObject).mCurScrollY);
    }
    paramRectF.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
  }
  
  public final void measure(int paramInt1, int paramInt2)
  {
    boolean bool = isLayoutModeOptical(this);
    int j;
    if (bool != isLayoutModeOptical(this.mParent))
    {
      localObject = getOpticalInsets();
      i = ((Insets)localObject).left + ((Insets)localObject).right;
      j = ((Insets)localObject).top + ((Insets)localObject).bottom;
      if (bool) {
        i = -i;
      }
      i = MeasureSpec.adjust(paramInt1, i);
      if (bool) {
        paramInt1 = -j;
      } else {
        paramInt1 = j;
      }
      paramInt2 = MeasureSpec.adjust(paramInt2, paramInt1);
      paramInt1 = i;
    }
    long l1 = paramInt1 << 32 | paramInt2 & 0xFFFFFFFF;
    if (this.mMeasureCache == null) {
      this.mMeasureCache = new LongSparseLongArray(2);
    }
    int i = this.mPrivateFlags;
    int k = 1;
    if ((i & 0x1000) == 4096) {
      i = 1;
    } else {
      i = 0;
    }
    if ((paramInt1 == this.mOldWidthMeasureSpec) && (paramInt2 == this.mOldHeightMeasureSpec)) {
      j = 0;
    } else {
      j = 1;
    }
    int m;
    if ((MeasureSpec.getMode(paramInt1) == 1073741824) && (MeasureSpec.getMode(paramInt2) == 1073741824)) {
      m = 1;
    } else {
      m = 0;
    }
    int n;
    if ((getMeasuredWidth() == MeasureSpec.getSize(paramInt1)) && (getMeasuredHeight() == MeasureSpec.getSize(paramInt2))) {
      n = 1;
    } else {
      n = 0;
    }
    if ((j != 0) && ((sAlwaysRemeasureExactly) || (m == 0) || (n == 0))) {
      j = k;
    } else {
      j = 0;
    }
    if ((i == 0) && (j == 0)) {
      break label428;
    }
    this.mPrivateFlags &= 0xF7FF;
    resolveRtlPropertiesIfNeeded();
    if (i != 0) {
      i = -1;
    } else {
      i = this.mMeasureCache.indexOfKey(l1);
    }
    long l2;
    if ((i >= 0) && (!sIgnoreMeasureCache))
    {
      l2 = this.mMeasureCache.valueAt(i);
      setMeasuredDimensionRaw((int)(l2 >> 32), (int)l2);
      this.mPrivateFlags3 |= 0x8;
    }
    else
    {
      onMeasure(paramInt1, paramInt2);
      this.mPrivateFlags3 &= 0xFFFFFFF7;
    }
    i = this.mPrivateFlags;
    if ((i & 0x800) == 2048)
    {
      this.mPrivateFlags = (i | 0x2000);
      label428:
      this.mOldWidthMeasureSpec = paramInt1;
      this.mOldHeightMeasureSpec = paramInt2;
      localObject = this.mMeasureCache;
      l2 = this.mMeasuredWidth;
      ((LongSparseLongArray)localObject).put(l1, this.mMeasuredHeight & 0xFFFFFFFF | l2 << 32);
      return;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("View with id ");
    ((StringBuilder)localObject).append(getId());
    ((StringBuilder)localObject).append(": ");
    ((StringBuilder)localObject).append(getClass().getName());
    ((StringBuilder)localObject).append("#onMeasure() did not set the measured dimension by calling setMeasuredDimension()");
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  void needGlobalAttributesUpdate(boolean paramBoolean)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if ((localAttachInfo != null) && (!localAttachInfo.mRecomputeGlobalAttributes) && ((paramBoolean) || (localAttachInfo.mKeepScreenOn) || (localAttachInfo.mSystemUiVisibility != 0) || (localAttachInfo.mHasSystemUiListeners))) {
      localAttachInfo.mRecomputeGlobalAttributes = true;
    }
  }
  
  public void notifyConfirmedWebView(boolean paramBoolean)
  {
    if (this.mIsWebView)
    {
      Object localObject = this.mAttachInfo;
      if ((localObject != null) && (((AttachInfo)localObject).mRootView != null))
      {
        localObject = this.mAttachInfo.mRootView;
        try
        {
          if (!notifyWebView((View)localObject, paramBoolean)) {
            Log.w("ContentCatcher", "Failed to notify a WebView");
          }
        }
        catch (Exception localException)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("View.notifyConfirmedWebView-Exception: ");
          localStringBuilder.append(localException);
          Log.e("ContentCatcher", localStringBuilder.toString());
        }
      }
    }
  }
  
  public void notifyEnterOrExitForAutoFillIfNeeded(boolean paramBoolean)
  {
    if (canNotifyAutofillEnterExitEvent())
    {
      AutofillManager localAutofillManager = getAutofillManager();
      if (localAutofillManager != null) {
        if ((paramBoolean) && (isFocused()))
        {
          if (!isLaidOut()) {
            this.mPrivateFlags3 |= 0x8000000;
          } else if (isVisibleToUser()) {
            localAutofillManager.notifyViewEntered(this);
          }
        }
        else if ((!paramBoolean) && (!isFocused())) {
          localAutofillManager.notifyViewExited(this);
        }
      }
    }
  }
  
  void notifyGlobalFocusCleared(View paramView)
  {
    if (paramView != null)
    {
      AttachInfo localAttachInfo = this.mAttachInfo;
      if (localAttachInfo != null) {
        localAttachInfo.mTreeObserver.dispatchOnGlobalFocusChange(paramView, null);
      }
    }
  }
  
  @UnsupportedAppUsage
  public void notifySubtreeAccessibilityStateChangedIfNeeded()
  {
    if ((AccessibilityManager.getInstance(this.mContext).isEnabled()) && (this.mAttachInfo != null))
    {
      int i = this.mPrivateFlags2;
      if ((i & 0x8000000) == 0)
      {
        this.mPrivateFlags2 = (i | 0x8000000);
        Object localObject = this.mParent;
        if (localObject != null) {
          try
          {
            ((ViewParent)localObject).notifySubtreeAccessibilityStateChanged(this, this, 1);
          }
          catch (AbstractMethodError localAbstractMethodError)
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append(this.mParent.getClass().getSimpleName());
            ((StringBuilder)localObject).append(" does not fully implement ViewParent");
            Log.e("View", ((StringBuilder)localObject).toString(), localAbstractMethodError);
          }
        }
      }
      return;
    }
  }
  
  @UnsupportedAppUsage
  public void notifyViewAccessibilityStateChangedIfNeeded(int paramInt)
  {
    if ((AccessibilityManager.getInstance(this.mContext).isEnabled()) && (this.mAttachInfo != null))
    {
      if ((paramInt != 1) && (isAccessibilityPane()) && ((getVisibility() == 0) || (paramInt == 32)))
      {
        Object localObject1 = AccessibilityEvent.obtain();
        onInitializeAccessibilityEvent((AccessibilityEvent)localObject1);
        ((AccessibilityEvent)localObject1).setEventType(32);
        ((AccessibilityEvent)localObject1).setContentChangeTypes(paramInt);
        ((AccessibilityEvent)localObject1).setSource(this);
        onPopulateAccessibilityEvent((AccessibilityEvent)localObject1);
        ViewParent localViewParent = this.mParent;
        if (localViewParent != null) {
          try
          {
            localViewParent.requestSendAccessibilityEvent(this, (AccessibilityEvent)localObject1);
          }
          catch (AbstractMethodError localAbstractMethodError2)
          {
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append(this.mParent.getClass().getSimpleName());
            ((StringBuilder)localObject1).append(" does not fully implement ViewParent");
            Log.e("View", ((StringBuilder)localObject1).toString(), localAbstractMethodError2);
          }
        }
        return;
      }
      Object localObject2;
      if (getAccessibilityLiveRegion() != 0)
      {
        localObject2 = AccessibilityEvent.obtain();
        ((AccessibilityEvent)localObject2).setEventType(2048);
        ((AccessibilityEvent)localObject2).setContentChangeTypes(paramInt);
        sendAccessibilityEventUnchecked((AccessibilityEvent)localObject2);
      }
      else
      {
        localObject2 = this.mParent;
        if (localObject2 != null) {
          try
          {
            ((ViewParent)localObject2).notifySubtreeAccessibilityStateChanged(this, this, paramInt);
          }
          catch (AbstractMethodError localAbstractMethodError1)
          {
            localObject2 = new StringBuilder();
            ((StringBuilder)localObject2).append(this.mParent.getClass().getSimpleName());
            ((StringBuilder)localObject2).append(" does not fully implement ViewParent");
            Log.e("View", ((StringBuilder)localObject2).toString(), localAbstractMethodError1);
          }
        }
      }
      return;
    }
  }
  
  public void offsetLeftAndRight(int paramInt)
  {
    if (paramInt != 0)
    {
      boolean bool = hasIdentityMatrix();
      if (bool)
      {
        if (isHardwareAccelerated())
        {
          invalidateViewProperty(false, false);
        }
        else
        {
          ViewParent localViewParent = this.mParent;
          if (localViewParent != null)
          {
            Object localObject = this.mAttachInfo;
            if (localObject != null)
            {
              localObject = ((AttachInfo)localObject).mTmpInvalRect;
              int i;
              int j;
              if (paramInt < 0)
              {
                i = this.mLeft + paramInt;
                j = this.mRight;
              }
              else
              {
                i = this.mLeft;
                j = this.mRight + paramInt;
              }
              ((Rect)localObject).set(0, 0, j - i, this.mBottom - this.mTop);
              localViewParent.invalidateChild(this, (Rect)localObject);
            }
          }
        }
      }
      else {
        invalidateViewProperty(false, false);
      }
      this.mLeft += paramInt;
      this.mRight += paramInt;
      this.mRenderNode.offsetLeftAndRight(paramInt);
      if (isHardwareAccelerated())
      {
        invalidateViewProperty(false, false);
        invalidateParentIfNeededAndWasQuickRejected();
      }
      else
      {
        if (!bool) {
          invalidateViewProperty(false, true);
        }
        invalidateParentIfNeeded();
      }
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void offsetTopAndBottom(int paramInt)
  {
    if (paramInt != 0)
    {
      boolean bool = hasIdentityMatrix();
      if (bool)
      {
        if (isHardwareAccelerated())
        {
          invalidateViewProperty(false, false);
        }
        else
        {
          ViewParent localViewParent = this.mParent;
          if (localViewParent != null)
          {
            Object localObject = this.mAttachInfo;
            if (localObject != null)
            {
              localObject = ((AttachInfo)localObject).mTmpInvalRect;
              int i;
              int j;
              int k;
              if (paramInt < 0)
              {
                i = this.mTop + paramInt;
                j = this.mBottom;
                k = paramInt;
              }
              else
              {
                i = this.mTop;
                j = this.mBottom + paramInt;
                k = 0;
              }
              ((Rect)localObject).set(0, k, this.mRight - this.mLeft, j - i);
              localViewParent.invalidateChild(this, (Rect)localObject);
            }
          }
        }
      }
      else {
        invalidateViewProperty(false, false);
      }
      this.mTop += paramInt;
      this.mBottom += paramInt;
      this.mRenderNode.offsetTopAndBottom(paramInt);
      if (isHardwareAccelerated())
      {
        invalidateViewProperty(false, false);
        invalidateParentIfNeededAndWasQuickRejected();
      }
      else
      {
        if (!bool) {
          invalidateViewProperty(false, true);
        }
        invalidateParentIfNeeded();
      }
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {}
  
  protected void onAnimationEnd()
  {
    this.mPrivateFlags &= 0xFFFEFFFF;
  }
  
  protected void onAnimationStart()
  {
    this.mPrivateFlags |= 0x10000;
  }
  
  public WindowInsets onApplyWindowInsets(WindowInsets paramWindowInsets)
  {
    if ((this.mPrivateFlags3 & 0x40) == 0)
    {
      if (fitSystemWindows(paramWindowInsets.getSystemWindowInsetsAsRect())) {
        return paramWindowInsets.consumeSystemWindowInsets();
      }
    }
    else if (fitSystemWindowsInt(paramWindowInsets.getSystemWindowInsetsAsRect())) {
      return paramWindowInsets.consumeSystemWindowInsets();
    }
    return paramWindowInsets;
  }
  
  protected void onAttachedToWindow()
  {
    if ((this.mPrivateFlags & 0x200) != 0) {
      this.mParent.requestTransparentRegion(this);
    }
    this.mPrivateFlags3 &= 0xFFFFFFFB;
    jumpDrawablesToCurrentState();
    AccessibilityNodeIdManager.getInstance().registerViewWithId(this, getAccessibilityViewId());
    resetSubtreeAccessibilityStateChanged();
    rebuildOutline();
    if (isFocused()) {
      notifyFocusChangeToInputMethodManager(true);
    }
  }
  
  public void onCancelPendingInputEvents()
  {
    removePerformClickCallback();
    cancelLongPress();
    this.mPrivateFlags3 |= 0x10;
  }
  
  public boolean onCapturedPointerEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onCheckIsTextEditor()
  {
    return false;
  }
  
  @UnsupportedAppUsage
  public void onCloseSystemDialogs(String paramString) {}
  
  protected void onConfigurationChanged(Configuration paramConfiguration) {}
  
  protected void onCreateContextMenu(ContextMenu paramContextMenu) {}
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    if (Android_View_View.Extension.get().getExtension() != null) {
      return ((Android_View_View.Interface)Android_View_View.Extension.get().getExtension().asInterface()).onCreateDrawableState(this, paramInt);
    }
    return originalOnCreateDrawableState(paramInt);
  }
  
  public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo)
  {
    return null;
  }
  
  protected void onDetachedFromWindow() {}
  
  @UnsupportedAppUsage
  protected void onDetachedFromWindowInternal()
  {
    this.mPrivateFlags &= 0xFBFFFFFF;
    this.mPrivateFlags3 &= 0xFFFFFFFB;
    this.mPrivateFlags3 &= 0xFDFFFFFF;
    removeUnsetPressCallback();
    removeLongPressCallback();
    removePerformClickCallback();
    cancel(this.mSendViewScrolledAccessibilityEvent);
    stopNestedScroll();
    jumpDrawablesToCurrentState();
    destroyDrawingCache();
    cleanupDraw();
    this.mCurrentAnimation = null;
    if ((this.mViewFlags & 0x40000000) == 1073741824) {
      hideTooltip();
    }
    AccessibilityNodeIdManager.getInstance().unregisterViewWithId(getAccessibilityViewId());
  }
  
  protected void onDisplayHint(int paramInt) {}
  
  public boolean onDragEvent(DragEvent paramDragEvent)
  {
    return false;
  }
  
  protected void onDraw(Canvas paramCanvas) {}
  
  public void onDrawForeground(Canvas paramCanvas)
  {
    onDrawScrollIndicators(paramCanvas);
    onDrawScrollBars(paramCanvas);
    Object localObject = this.mForegroundInfo;
    if (localObject != null) {
      localObject = ((ForegroundInfo)localObject).mDrawable;
    } else {
      localObject = null;
    }
    if (localObject != null)
    {
      if (this.mForegroundInfo.mBoundsChanged)
      {
        ForegroundInfo.access$2002(this.mForegroundInfo, false);
        Rect localRect1 = this.mForegroundInfo.mSelfBounds;
        Rect localRect2 = this.mForegroundInfo.mOverlayBounds;
        if (this.mForegroundInfo.mInsidePadding) {
          localRect1.set(0, 0, getWidth(), getHeight());
        } else {
          localRect1.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        }
        int i = getLayoutDirection();
        Gravity.apply(this.mForegroundInfo.mGravity, ((Drawable)localObject).getIntrinsicWidth(), ((Drawable)localObject).getIntrinsicHeight(), localRect1, localRect2, i);
        ((Drawable)localObject).setBounds(localRect2);
      }
      ((Drawable)localObject).draw(paramCanvas);
    }
  }
  
  @UnsupportedAppUsage
  protected void onDrawHorizontalScrollBar(Canvas paramCanvas, Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramDrawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    paramDrawable.draw(paramCanvas);
  }
  
  protected final void onDrawScrollBars(Canvas paramCanvas)
  {
    Object localObject1 = this.mScrollCache;
    if (localObject1 != null)
    {
      int i = ((ScrollabilityCache)localObject1).state;
      if (i == 0) {
        return;
      }
      Object localObject2;
      if (i == 2)
      {
        if (((ScrollabilityCache)localObject1).interpolatorValues == null) {
          ((ScrollabilityCache)localObject1).interpolatorValues = new float[1];
        }
        localObject2 = ((ScrollabilityCache)localObject1).interpolatorValues;
        if (((ScrollabilityCache)localObject1).scrollBarInterpolator.timeToValues((float[])localObject2) == Interpolator.Result.FREEZE_END) {
          ((ScrollabilityCache)localObject1).state = 0;
        } else {
          ((ScrollabilityCache)localObject1).scrollBar.mutate().setAlpha(Math.round(localObject2[0]));
        }
        i = 1;
      }
      else
      {
        ((ScrollabilityCache)localObject1).scrollBar.mutate().setAlpha(255);
        i = 0;
      }
      boolean bool = isHorizontalScrollBarEnabled();
      int j;
      if ((isVerticalScrollBarEnabled()) && (!isVerticalScrollBarHidden())) {
        j = 1;
      } else {
        j = 0;
      }
      if (this.mRoundScrollbarRenderer != null)
      {
        if (j != 0)
        {
          localObject2 = ((ScrollabilityCache)localObject1).mScrollBarBounds;
          getVerticalScrollBarBounds((Rect)localObject2, null);
          this.mRoundScrollbarRenderer.drawRoundScrollbars(paramCanvas, ((ScrollabilityCache)localObject1).scrollBar.getAlpha() / 255.0F, (Rect)localObject2);
          if (i != 0) {
            invalidate();
          }
        }
      }
      else if ((j != 0) || (bool))
      {
        localObject2 = ((ScrollabilityCache)localObject1).scrollBar;
        if (bool)
        {
          ((ScrollBarDrawable)localObject2).setParameters(computeHorizontalScrollRange(), computeHorizontalScrollOffset(), computeHorizontalScrollExtent(), false);
          Rect localRect = ((ScrollabilityCache)localObject1).mScrollBarBounds;
          getHorizontalScrollBarBounds(localRect, null);
          onDrawHorizontalScrollBar(paramCanvas, (Drawable)localObject2, localRect.left, localRect.top, localRect.right, localRect.bottom);
          if (i != 0) {
            invalidate(localRect);
          }
        }
        if (j != 0)
        {
          ((ScrollBarDrawable)localObject2).setParameters(computeVerticalScrollRange(), computeVerticalScrollOffset(), computeVerticalScrollExtent(), true);
          localObject1 = ((ScrollabilityCache)localObject1).mScrollBarBounds;
          getVerticalScrollBarBounds((Rect)localObject1, null);
          onDrawVerticalScrollBar(paramCanvas, (Drawable)localObject2, ((Rect)localObject1).left, ((Rect)localObject1).top, ((Rect)localObject1).right, ((Rect)localObject1).bottom);
          if (i != 0) {
            invalidate((Rect)localObject1);
          }
        }
      }
    }
  }
  
  @UnsupportedAppUsage
  protected void onDrawVerticalScrollBar(Canvas paramCanvas, Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramDrawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    paramDrawable.draw(paramCanvas);
  }
  
  public boolean onFilterTouchEventForSecurity(MotionEvent paramMotionEvent)
  {
    return ((this.mViewFlags & 0x400) == 0) || ((paramMotionEvent.getFlags() & 0x1) == 0);
  }
  
  protected void onFinishInflate() {}
  
  public void onFinishTemporaryDetach() {}
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    if (paramBoolean) {
      sendAccessibilityEvent(8);
    } else {
      notifyViewAccessibilityStateChangedIfNeeded(0);
    }
    switchDefaultFocusHighlight();
    if (!paramBoolean)
    {
      if (isPressed()) {
        setPressed(false);
      }
      paramRect = this.mAttachInfo;
      if ((paramRect != null) && (paramRect.mHasWindowFocus)) {
        notifyFocusChangeToInputMethodManager(false);
      }
      onFocusLost();
    }
    else
    {
      paramRect = this.mAttachInfo;
      if ((paramRect != null) && (paramRect.mHasWindowFocus)) {
        notifyFocusChangeToInputMethodManager(true);
      }
    }
    invalidate(true);
    paramRect = this.mListenerInfo;
    if ((paramRect != null) && (paramRect.mOnFocusChangeListener != null)) {
      paramRect.mOnFocusChangeListener.onFocusChange(this, paramBoolean);
    }
    paramRect = this.mAttachInfo;
    if (paramRect != null) {
      paramRect.mKeyDispatchState.reset(this);
    }
    notifyEnterOrExitForAutoFillIfNeeded(paramBoolean);
  }
  
  @UnsupportedAppUsage
  protected void onFocusLost()
  {
    resetPressedState();
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public void onHoverChanged(boolean paramBoolean) {}
  
  public boolean onHoverEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mTouchDelegate != null) && (dispatchTouchExplorationHoverEvent(paramMotionEvent))) {
      return true;
    }
    int i = paramMotionEvent.getActionMasked();
    if (!this.mSendingHoverAccessibilityEvents)
    {
      if (((i == 9) || (i == 7)) && (!hasHoveredChild()) && (pointInView(paramMotionEvent.getX(), paramMotionEvent.getY())))
      {
        sendAccessibilityHoverEvent(128);
        this.mSendingHoverAccessibilityEvents = true;
      }
    }
    else if ((i == 10) || ((i == 7) && (!pointInView(paramMotionEvent.getX(), paramMotionEvent.getY()))))
    {
      this.mSendingHoverAccessibilityEvents = false;
      sendAccessibilityHoverEvent(256);
    }
    if (((i == 9) || (i == 7)) && (paramMotionEvent.isFromSource(8194)) && (isOnScrollbar(paramMotionEvent.getX(), paramMotionEvent.getY()))) {
      awakenScrollBars();
    }
    if ((!isHoverable()) && (!isHovered())) {
      return false;
    }
    if (i != 9)
    {
      if (i == 10) {
        setHovered(false);
      }
    }
    else {
      setHovered(true);
    }
    dispatchGenericMotionEventInternal(paramMotionEvent);
    return true;
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      localAccessibilityDelegate.onInitializeAccessibilityEvent(this, paramAccessibilityEvent);
    } else {
      onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    }
  }
  
  @UnsupportedAppUsage
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    paramAccessibilityEvent.setSource(this);
    paramAccessibilityEvent.setClassName(getAccessibilityClassName());
    paramAccessibilityEvent.setPackageName(getContext().getPackageName());
    paramAccessibilityEvent.setEnabled(isEnabled());
    paramAccessibilityEvent.setContentDescription(this.mContentDescription);
    int i = paramAccessibilityEvent.getEventType();
    Object localObject;
    if (i != 8)
    {
      if (i == 8192)
      {
        localObject = getIterableTextForAccessibility();
        if ((localObject != null) && (((CharSequence)localObject).length() > 0))
        {
          paramAccessibilityEvent.setFromIndex(getAccessibilitySelectionStart());
          paramAccessibilityEvent.setToIndex(getAccessibilitySelectionEnd());
          paramAccessibilityEvent.setItemCount(((CharSequence)localObject).length());
        }
      }
    }
    else
    {
      localObject = this.mAttachInfo;
      if (localObject != null) {
        localObject = ((AttachInfo)localObject).mTempArrayList;
      } else {
        localObject = new ArrayList();
      }
      getRootView().addFocusables((ArrayList)localObject, 2, 0);
      paramAccessibilityEvent.setItemCount(((ArrayList)localObject).size());
      paramAccessibilityEvent.setCurrentItemIndex(((ArrayList)localObject).indexOf(this));
      if (this.mAttachInfo != null) {
        ((ArrayList)localObject).clear();
      }
    }
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      localAccessibilityDelegate.onInitializeAccessibilityNodeInfo(this, paramAccessibilityNodeInfo);
    } else {
      onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    }
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    Object localObject1 = this.mAttachInfo;
    if (localObject1 == null) {
      return;
    }
    localObject1 = ((AttachInfo)localObject1).mTmpInvalRect;
    getDrawingRect((Rect)localObject1);
    paramAccessibilityNodeInfo.setBoundsInParent((Rect)localObject1);
    getBoundsOnScreen((Rect)localObject1, true);
    paramAccessibilityNodeInfo.setBoundsInScreen((Rect)localObject1);
    localObject1 = getParentForAccessibility();
    if ((localObject1 instanceof View)) {
      paramAccessibilityNodeInfo.setParent((View)localObject1);
    }
    View localView;
    if (this.mID != -1)
    {
      localView = getRootView();
      localObject1 = localView;
      if (localView == null) {
        localObject1 = this;
      }
      localObject1 = ((View)localObject1).findLabelForView(this, this.mID);
      if (localObject1 != null) {
        paramAccessibilityNodeInfo.setLabeledBy((View)localObject1);
      }
      if (((this.mAttachInfo.mAccessibilityFetchFlags & 0x10) != 0) && (Resources.resourceHasPackage(this.mID))) {
        try
        {
          paramAccessibilityNodeInfo.setViewIdResourceName(getResources().getResourceName(this.mID));
        }
        catch (Resources.NotFoundException localNotFoundException) {}
      }
    }
    if (this.mLabelForId != -1)
    {
      localView = getRootView();
      localObject2 = localView;
      if (localView == null) {
        localObject2 = this;
      }
      localObject2 = ((View)localObject2).findViewInsideOutShouldExist(this, this.mLabelForId);
      if (localObject2 != null) {
        paramAccessibilityNodeInfo.setLabelFor((View)localObject2);
      }
    }
    if (this.mAccessibilityTraversalBeforeId != -1)
    {
      localView = getRootView();
      localObject2 = localView;
      if (localView == null) {
        localObject2 = this;
      }
      localObject2 = ((View)localObject2).findViewInsideOutShouldExist(this, this.mAccessibilityTraversalBeforeId);
      if ((localObject2 != null) && (((View)localObject2).includeForAccessibility())) {
        paramAccessibilityNodeInfo.setTraversalBefore((View)localObject2);
      }
    }
    if (this.mAccessibilityTraversalAfterId != -1)
    {
      localView = getRootView();
      localObject2 = localView;
      if (localView == null) {
        localObject2 = this;
      }
      localObject2 = ((View)localObject2).findViewInsideOutShouldExist(this, this.mAccessibilityTraversalAfterId);
      if ((localObject2 != null) && (((View)localObject2).includeForAccessibility())) {
        paramAccessibilityNodeInfo.setTraversalAfter((View)localObject2);
      }
    }
    paramAccessibilityNodeInfo.setVisibleToUser(isVisibleToUser());
    paramAccessibilityNodeInfo.setImportantForAccessibility(isImportantForAccessibility());
    paramAccessibilityNodeInfo.setPackageName(this.mContext.getPackageName());
    paramAccessibilityNodeInfo.setClassName(getAccessibilityClassName());
    paramAccessibilityNodeInfo.setContentDescription(getContentDescription());
    paramAccessibilityNodeInfo.setEnabled(isEnabled());
    paramAccessibilityNodeInfo.setClickable(isClickable());
    paramAccessibilityNodeInfo.setFocusable(isFocusable());
    paramAccessibilityNodeInfo.setScreenReaderFocusable(isScreenReaderFocusable());
    paramAccessibilityNodeInfo.setFocused(isFocused());
    paramAccessibilityNodeInfo.setAccessibilityFocused(isAccessibilityFocused());
    paramAccessibilityNodeInfo.setSelected(isSelected());
    paramAccessibilityNodeInfo.setLongClickable(isLongClickable());
    paramAccessibilityNodeInfo.setContextClickable(isContextClickable());
    paramAccessibilityNodeInfo.setLiveRegion(getAccessibilityLiveRegion());
    Object localObject2 = this.mTooltipInfo;
    if ((localObject2 != null) && (((TooltipInfo)localObject2).mTooltipText != null))
    {
      paramAccessibilityNodeInfo.setTooltipText(this.mTooltipInfo.mTooltipText);
      if (this.mTooltipInfo.mTooltipPopup == null) {
        localObject2 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_TOOLTIP;
      } else {
        localObject2 = AccessibilityNodeInfo.AccessibilityAction.ACTION_HIDE_TOOLTIP;
      }
      paramAccessibilityNodeInfo.addAction((AccessibilityNodeInfo.AccessibilityAction)localObject2);
    }
    paramAccessibilityNodeInfo.addAction(4);
    paramAccessibilityNodeInfo.addAction(8);
    if (isFocusable()) {
      if (isFocused()) {
        paramAccessibilityNodeInfo.addAction(2);
      } else {
        paramAccessibilityNodeInfo.addAction(1);
      }
    }
    if (!isAccessibilityFocused()) {
      paramAccessibilityNodeInfo.addAction(64);
    } else {
      paramAccessibilityNodeInfo.addAction(128);
    }
    if ((isClickable()) && (isEnabled())) {
      paramAccessibilityNodeInfo.addAction(16);
    }
    if ((isLongClickable()) && (isEnabled())) {
      paramAccessibilityNodeInfo.addAction(32);
    }
    if ((isContextClickable()) && (isEnabled())) {
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CONTEXT_CLICK);
    }
    localObject2 = getIterableTextForAccessibility();
    if ((localObject2 != null) && (((CharSequence)localObject2).length() > 0))
    {
      paramAccessibilityNodeInfo.setTextSelection(getAccessibilitySelectionStart(), getAccessibilitySelectionEnd());
      paramAccessibilityNodeInfo.addAction(131072);
      paramAccessibilityNodeInfo.addAction(256);
      paramAccessibilityNodeInfo.addAction(512);
      paramAccessibilityNodeInfo.setMovementGranularities(11);
    }
    paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN);
    populateAccessibilityNodeInfoDrawingOrderInParent(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setPaneTitle(this.mAccessibilityPaneTitle);
    paramAccessibilityNodeInfo.setHeading(isAccessibilityHeading());
    localObject2 = this.mTouchDelegate;
    if (localObject2 != null) {
      paramAccessibilityNodeInfo.setTouchDelegateInfo(((TouchDelegate)localObject2).getTouchDelegateInfo());
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    SeempLog.record(4);
    if (KeyEvent.isConfirmKey(paramInt))
    {
      if ((this.mViewFlags & 0x20) == 32) {
        return true;
      }
      if (paramKeyEvent.getRepeatCount() == 0)
      {
        paramInt = this.mViewFlags;
        if (((paramInt & 0x4000) != 16384) && ((paramInt & 0x200000) != 2097152)) {
          paramInt = 0;
        } else {
          paramInt = 1;
        }
        if ((paramInt != 0) || ((this.mViewFlags & 0x40000000) == 1073741824))
        {
          float f1 = getWidth() / 2.0F;
          float f2 = getHeight() / 2.0F;
          if (paramInt != 0) {
            setPressed(true, f1, f2);
          }
          checkForLongClick(ViewConfiguration.getLongPressTimeout(), f1, f2, 0);
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent)
  {
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    SeempLog.record(5);
    if (KeyEvent.isConfirmKey(paramInt))
    {
      paramInt = this.mViewFlags;
      if ((paramInt & 0x20) == 32) {
        return true;
      }
      if (((paramInt & 0x4000) == 16384) && (isPressed()))
      {
        setPressed(false);
        if (!this.mHasPerformedLongPress)
        {
          removeLongPressCallback();
          if (!paramKeyEvent.isCanceled()) {
            return performClickInternal();
          }
        }
      }
    }
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), paramInt1), getDefaultSize(getSuggestedMinimumHeight(), paramInt2));
  }
  
  public void onMovedToDisplay(int paramInt, Configuration paramConfiguration) {}
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {}
  
  public void onPointerCaptureChange(boolean paramBoolean) {}
  
  public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      localAccessibilityDelegate.onPopulateAccessibilityEvent(this, paramAccessibilityEvent);
    } else {
      onPopulateAccessibilityEventInternal(paramAccessibilityEvent);
    }
  }
  
  public void onPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    if ((paramAccessibilityEvent.getEventType() == 32) && (isAccessibilityPane())) {
      paramAccessibilityEvent.getText().add(getAccessibilityPaneTitle());
    }
  }
  
  public void onProvideAutofillStructure(ViewStructure paramViewStructure, int paramInt)
  {
    onProvideStructure(paramViewStructure, 1, paramInt);
  }
  
  public void onProvideAutofillVirtualStructure(ViewStructure paramViewStructure, int paramInt)
  {
    if (this.mContext.isAutofillCompatibilityEnabled()) {
      onProvideVirtualStructureCompat(paramViewStructure, true);
    }
  }
  
  public void onProvideStructure(ViewStructure paramViewStructure)
  {
    onProvideStructure(paramViewStructure, 0, 0);
  }
  
  protected void onProvideStructure(ViewStructure paramViewStructure, int paramInt1, int paramInt2)
  {
    int i = this.mID;
    Object localObject1;
    Object localObject2;
    if ((i != -1) && (!isViewIdGenerated(i)))
    {
      String str2;
      try
      {
        localObject1 = getResources();
        String str1 = ((Resources)localObject1).getResourceEntryName(i);
        str2 = ((Resources)localObject1).getResourceTypeName(i);
        localObject1 = ((Resources)localObject1).getResourcePackageName(i);
      }
      catch (Resources.NotFoundException localNotFoundException)
      {
        str2 = null;
        localObject2 = null;
        localObject1 = null;
      }
      paramViewStructure.setId(i, (String)localObject1, str2, (String)localObject2);
    }
    else
    {
      paramViewStructure.setId(i, null, null, null);
    }
    if ((paramInt1 == 1) || (paramInt1 == 2))
    {
      i = getAutofillType();
      if (i != 0)
      {
        paramViewStructure.setAutofillType(i);
        paramViewStructure.setAutofillHints(getAutofillHints());
        paramViewStructure.setAutofillValue(getAutofillValue());
      }
      paramViewStructure.setImportantForAutofill(getImportantForAutofill());
    }
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = j;
    i = m;
    if (paramInt1 == 1)
    {
      i1 = j;
      i = m;
      if ((paramInt2 & 0x1) == 0)
      {
        localObject2 = null;
        localObject1 = getParent();
        j = k;
        paramInt2 = n;
        if ((localObject1 instanceof View))
        {
          localObject2 = (View)localObject1;
          paramInt2 = n;
          j = k;
        }
        for (;;)
        {
          i1 = j;
          i = paramInt2;
          if (localObject2 == null) {
            break label298;
          }
          i1 = j;
          i = paramInt2;
          if (((View)localObject2).isImportantForAutofill()) {
            break label298;
          }
          j += ((View)localObject2).mLeft;
          paramInt2 += ((View)localObject2).mTop;
          localObject2 = ((View)localObject2).getParent();
          if (!(localObject2 instanceof View)) {
            break;
          }
          localObject2 = (View)localObject2;
        }
        i = paramInt2;
        paramInt2 = j;
        break label301;
      }
    }
    label298:
    paramInt2 = i1;
    label301:
    j = this.mLeft;
    i1 = this.mTop;
    paramViewStructure.setDimens(paramInt2 + j, i + i1, this.mScrollX, this.mScrollY, this.mRight - j, this.mBottom - i1);
    if (paramInt1 == 0)
    {
      if (!hasIdentityMatrix()) {
        paramViewStructure.setTransformation(getMatrix());
      }
      paramViewStructure.setElevation(getZ());
    }
    paramViewStructure.setVisibility(getVisibility());
    paramViewStructure.setEnabled(isEnabled());
    if (isClickable()) {
      paramViewStructure.setClickable(true);
    }
    if (isFocusable()) {
      paramViewStructure.setFocusable(true);
    }
    if (isFocused()) {
      paramViewStructure.setFocused(true);
    }
    if (isAccessibilityFocused()) {
      paramViewStructure.setAccessibilityFocused(true);
    }
    if (isSelected()) {
      paramViewStructure.setSelected(true);
    }
    if (isActivated()) {
      paramViewStructure.setActivated(true);
    }
    if (isLongClickable()) {
      paramViewStructure.setLongClickable(true);
    }
    if ((this instanceof Checkable))
    {
      paramViewStructure.setCheckable(true);
      if (((Checkable)this).isChecked()) {
        paramViewStructure.setChecked(true);
      }
    }
    if (isOpaque()) {
      paramViewStructure.setOpaque(true);
    }
    if (isContextClickable()) {
      paramViewStructure.setContextClickable(true);
    }
    paramViewStructure.setClassName(getAccessibilityClassName().toString());
    paramViewStructure.setContentDescription(getContentDescription());
  }
  
  public void onProvideVirtualStructure(ViewStructure paramViewStructure)
  {
    onProvideVirtualStructureCompat(paramViewStructure, false);
  }
  
  public void onResolveDrawables(int paramInt) {}
  
  public PointerIcon onResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt)
  {
    float f1 = paramMotionEvent.getX(paramInt);
    float f2 = paramMotionEvent.getY(paramInt);
    if ((!isDraggingScrollBar()) && (!isOnScrollbarThumb(f1, f2))) {
      return this.mPointerIcon;
    }
    return PointerIcon.getSystemIcon(this.mContext, 1000);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    this.mPrivateFlags |= 0x20000;
    Object localObject;
    if ((paramParcelable != null) && (!(paramParcelable instanceof AbsSavedState)))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Wrong state class, expecting View State but received ");
      ((StringBuilder)localObject).append(paramParcelable.getClass().toString());
      ((StringBuilder)localObject).append(" instead. This usually happens when two views of different type have the same id in the same hierarchy. This view's id is ");
      ((StringBuilder)localObject).append(ViewDebug.resolveId(this.mContext, getId()));
      ((StringBuilder)localObject).append(". Make sure other views do not use the same id.");
      throw new IllegalArgumentException(((StringBuilder)localObject).toString());
    }
    if ((paramParcelable != null) && ((paramParcelable instanceof BaseSavedState)))
    {
      localObject = (BaseSavedState)paramParcelable;
      if ((((BaseSavedState)localObject).mSavedData & 0x1) != 0) {
        this.mStartActivityRequestWho = ((BaseSavedState)localObject).mStartActivityRequestWhoSaved;
      }
      if ((((BaseSavedState)localObject).mSavedData & 0x2) != 0) {
        setAutofilled(((BaseSavedState)localObject).mIsAutofilled);
      }
      if ((((BaseSavedState)localObject).mSavedData & 0x4) != 0)
      {
        paramParcelable = (BaseSavedState)paramParcelable;
        paramParcelable.mSavedData &= 0xFFFFFFFB;
        if ((this.mPrivateFlags3 & 0x40000000) != 0)
        {
          if (Log.isLoggable("View.Autofill", 3))
          {
            paramParcelable = new StringBuilder();
            paramParcelable.append("onRestoreInstanceState(): not setting autofillId to ");
            paramParcelable.append(((BaseSavedState)localObject).mAutofillViewId);
            paramParcelable.append(" because view explicitly set it to ");
            paramParcelable.append(this.mAutofillId);
            Log.d("View.Autofill", paramParcelable.toString());
          }
        }
        else
        {
          this.mAutofillViewId = ((BaseSavedState)localObject).mAutofillViewId;
          this.mAutofillId = null;
        }
      }
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt) {}
  
  protected Parcelable onSaveInstanceState()
  {
    this.mPrivateFlags |= 0x20000;
    if ((this.mStartActivityRequestWho == null) && (!isAutofilled()) && (this.mAutofillViewId <= 1073741823)) {
      return BaseSavedState.EMPTY_STATE;
    }
    BaseSavedState localBaseSavedState = new BaseSavedState(AbsSavedState.EMPTY_STATE);
    if (this.mStartActivityRequestWho != null) {
      localBaseSavedState.mSavedData |= 0x1;
    }
    if (isAutofilled()) {
      localBaseSavedState.mSavedData |= 0x2;
    }
    if (this.mAutofillViewId > 1073741823) {
      localBaseSavedState.mSavedData |= 0x4;
    }
    localBaseSavedState.mStartActivityRequestWhoSaved = this.mStartActivityRequestWho;
    localBaseSavedState.mIsAutofilled = isAutofilled();
    localBaseSavedState.mAutofillViewId = this.mAutofillViewId;
    return localBaseSavedState;
  }
  
  public void onScreenStateChanged(int paramInt) {}
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    notifySubtreeAccessibilityStateChangedIfNeeded();
    if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
      postSendViewScrolledAccessibilityEventCallback(paramInt1 - paramInt3, paramInt2 - paramInt4);
    }
    this.mBackgroundSizeChanged = true;
    this.mDefaultFocusHighlightSizeChanged = true;
    Object localObject = this.mForegroundInfo;
    if (localObject != null) {
      ForegroundInfo.access$2002((ForegroundInfo)localObject, true);
    }
    localObject = this.mAttachInfo;
    if (localObject != null) {
      ((AttachInfo)localObject).mViewScrollChanged = true;
    }
    localObject = this.mListenerInfo;
    if ((localObject != null) && (((ListenerInfo)localObject).mOnScrollChangeListener != null)) {
      this.mListenerInfo.mOnScrollChangeListener.onScrollChange(this, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected boolean onSetAlpha(int paramInt)
  {
    return false;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void onStartTemporaryDetach()
  {
    removeUnsetPressCallback();
    this.mPrivateFlags |= 0x4000000;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    SeempLog.record(3);
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    int i = this.mViewFlags;
    int j = paramMotionEvent.getAction();
    int k = 0;
    boolean bool1;
    if (((i & 0x4000) != 16384) && ((i & 0x200000) != 2097152) && ((i & 0x800000) != 8388608)) {
      bool1 = false;
    } else {
      bool1 = true;
    }
    if ((i & 0x20) == 32)
    {
      if ((j == 1) && ((this.mPrivateFlags & 0x4000) != 0)) {
        setPressed(false);
      }
      this.mPrivateFlags3 &= 0xFFFDFFFF;
      return bool1;
    }
    TouchDelegate localTouchDelegate = this.mTouchDelegate;
    if ((localTouchDelegate != null) && (localTouchDelegate.onTouchEvent(paramMotionEvent))) {
      return true;
    }
    if ((!bool1) && ((i & 0x40000000) != 1073741824)) {
      return false;
    }
    if (j != 0)
    {
      if (j != 1)
      {
        if (j != 2)
        {
          if (j == 3)
          {
            if (bool1) {
              setPressed(false);
            }
            removeTapCallback();
            removeLongPressCallback();
            this.mInContextButtonPress = false;
            this.mHasPerformedLongPress = false;
            this.mIgnoreNextUpEvent = false;
            this.mPrivateFlags3 &= 0xFFFDFFFF;
          }
        }
        else
        {
          if (bool1) {
            drawableHotspotChanged(f1, f2);
          }
          int m = paramMotionEvent.getClassification();
          if (m == 1) {
            j = 1;
          } else {
            j = 0;
          }
          i = this.mTouchSlop;
          if ((j != 0) && (hasPendingLongPressCallback()))
          {
            float f3 = ViewConfiguration.getAmbiguousGestureMultiplier();
            if (!pointInView(f1, f2, i))
            {
              removeLongPressCallback();
              checkForLongClick((ViewConfiguration.getLongPressTimeout() * f3) - (paramMotionEvent.getEventTime() - paramMotionEvent.getDownTime()), f1, f2, 3);
            }
            j = (int)(i * f3);
          }
          else
          {
            j = i;
          }
          if (!pointInView(f1, f2, j))
          {
            removeTapCallback();
            removeLongPressCallback();
            if ((this.mPrivateFlags & 0x4000) != 0) {
              setPressed(false);
            }
            this.mPrivateFlags3 &= 0xFFFDFFFF;
          }
          j = k;
          if (m == 2) {
            j = 1;
          }
          if ((j != 0) && (hasPendingLongPressCallback()))
          {
            removeLongPressCallback();
            checkForLongClick(0L, f1, f2, 4);
          }
        }
      }
      else
      {
        this.mPrivateFlags3 &= 0xFFFDFFFF;
        if ((i & 0x40000000) == 1073741824) {
          handleTooltipUp();
        }
        if (!bool1)
        {
          removeTapCallback();
          removeLongPressCallback();
          this.mInContextButtonPress = false;
          this.mHasPerformedLongPress = false;
          this.mIgnoreNextUpEvent = false;
        }
        else
        {
          if ((this.mPrivateFlags & 0x2000000) != 0) {
            j = 1;
          } else {
            j = 0;
          }
          if (((this.mPrivateFlags & 0x4000) != 0) || (j != 0))
          {
            boolean bool2 = false;
            bool1 = bool2;
            if (isFocusable())
            {
              bool1 = bool2;
              if (isFocusableInTouchMode())
              {
                bool1 = bool2;
                if (!isFocused()) {
                  bool1 = requestFocus();
                }
              }
            }
            if (j != 0) {
              setPressed(true, f1, f2);
            }
            if ((!this.mHasPerformedLongPress) && (!this.mIgnoreNextUpEvent))
            {
              removeLongPressCallback();
              if (!bool1)
              {
                if (this.mPerformClick == null) {
                  this.mPerformClick = new PerformClick(null);
                }
                if (!post(this.mPerformClick)) {
                  performClickInternal();
                }
              }
            }
            if (this.mUnsetPressedState == null) {
              this.mUnsetPressedState = new UnsetPressedState(null);
            }
            if (j != 0) {
              postDelayed(this.mUnsetPressedState, ViewConfiguration.getPressedStateDuration());
            } else if (!post(this.mUnsetPressedState)) {
              this.mUnsetPressedState.run();
            }
            removeTapCallback();
          }
          this.mIgnoreNextUpEvent = false;
        }
      }
    }
    else
    {
      if (paramMotionEvent.getSource() == 4098) {
        this.mPrivateFlags3 |= 0x20000;
      }
      this.mHasPerformedLongPress = false;
      if (!bool1) {
        checkForLongClick(ViewConfiguration.getLongPressTimeout(), f1, f2, 3);
      } else if (!performButtonActionOnTouchDown(paramMotionEvent)) {
        if (isInScrollingContainer())
        {
          this.mPrivateFlags |= 0x2000000;
          if (this.mPendingCheckForTap == null) {
            this.mPendingCheckForTap = new CheckForTap(null);
          }
          this.mPendingCheckForTap.x = paramMotionEvent.getX();
          this.mPendingCheckForTap.y = paramMotionEvent.getY();
          postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
        }
        else
        {
          setPressed(true, f1, f2);
          checkForLongClick(ViewConfiguration.getLongPressTimeout(), f1, f2, 3);
          performHapticFeedback(1, 4);
        }
      }
    }
    return true;
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  boolean onUnhandledKeyEvent(KeyEvent paramKeyEvent)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mUnhandledKeyListeners != null)) {
      for (int i = this.mListenerInfo.mUnhandledKeyListeners.size() - 1; i >= 0; i--) {
        if (((OnUnhandledKeyEventListener)this.mListenerInfo.mUnhandledKeyListeners.get(i)).onUnhandledKeyEvent(this, paramKeyEvent)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void onVisibilityAggregated(boolean paramBoolean)
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x20000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    int i;
    if (paramBoolean) {
      i = 0x20000000 | this.mPrivateFlags3;
    } else {
      i = this.mPrivateFlags3 & 0xDFFFFFFF;
    }
    this.mPrivateFlags3 = i;
    if ((paramBoolean) && (this.mAttachInfo != null)) {
      initialAwakenScrollBars();
    }
    Object localObject = this.mBackground;
    if ((localObject != null) && (paramBoolean != ((Drawable)localObject).isVisible())) {
      ((Drawable)localObject).setVisible(paramBoolean, false);
    }
    localObject = this.mDefaultFocusHighlight;
    if ((localObject != null) && (paramBoolean != ((Drawable)localObject).isVisible())) {
      ((Drawable)localObject).setVisible(paramBoolean, false);
    }
    localObject = this.mForegroundInfo;
    if (localObject != null) {
      localObject = ((ForegroundInfo)localObject).mDrawable;
    } else {
      localObject = null;
    }
    if ((localObject != null) && (paramBoolean != ((Drawable)localObject).isVisible())) {
      ((Drawable)localObject).setVisible(paramBoolean, false);
    }
    if (isAutofillable())
    {
      AutofillManager localAutofillManager = getAutofillManager();
      if ((localAutofillManager != null) && (getAutofillViewId() > 1073741823))
      {
        localObject = this.mVisibilityChangeForAutofillHandler;
        if (localObject != null) {
          ((Handler)localObject).removeMessages(0);
        }
        if (paramBoolean)
        {
          localAutofillManager.notifyViewVisibilityChanged(this, true);
        }
        else
        {
          if (this.mVisibilityChangeForAutofillHandler == null) {
            this.mVisibilityChangeForAutofillHandler = new VisibilityChangeForAutofillHandler(localAutofillManager, this, null);
          }
          this.mVisibilityChangeForAutofillHandler.obtainMessage(0, this).sendToTarget();
        }
      }
    }
    if ((isAccessibilityPane()) && (paramBoolean != bool))
    {
      if (paramBoolean) {
        i = 16;
      } else {
        i = 32;
      }
      notifyViewAccessibilityStateChangedIfNeeded(i);
    }
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt) {}
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      if (isPressed()) {
        setPressed(false);
      }
      this.mPrivateFlags3 &= 0xFFFDFFFF;
      if ((this.mPrivateFlags & 0x2) != 0) {
        notifyFocusChangeToInputMethodManager(false);
      }
      removeLongPressCallback();
      removeTapCallback();
      onFocusLost();
    }
    else if ((this.mPrivateFlags & 0x2) != 0)
    {
      notifyFocusChangeToInputMethodManager(true);
    }
    refreshDrawableState();
  }
  
  public void onWindowSystemUiVisibilityChanged(int paramInt) {}
  
  protected void onWindowVisibilityChanged(int paramInt)
  {
    if (paramInt == 0) {
      initialAwakenScrollBars();
    }
  }
  
  int[] originalOnCreateDrawableState(int paramInt)
  {
    if ((this.mViewFlags & 0x400000) == 4194304)
    {
      localObject = this.mParent;
      if ((localObject instanceof View)) {
        return ((View)localObject).onCreateDrawableState(paramInt);
      }
    }
    int i = this.mPrivateFlags;
    int j = 0;
    if ((i & 0x4000) != 0) {
      j = 0x0 | 0x10;
    }
    int k = j;
    if ((this.mViewFlags & 0x20) == 0) {
      k = j | 0x8;
    }
    j = k;
    if (isFocused()) {
      j = k | 0x4;
    }
    k = j;
    if ((i & 0x4) != 0) {
      k = j | 0x2;
    }
    j = k;
    if (hasWindowFocus()) {
      j = k | 0x1;
    }
    k = j;
    if ((0x40000000 & i) != 0) {
      k = j | 0x20;
    }
    Object localObject = this.mAttachInfo;
    j = k;
    if (localObject != null)
    {
      j = k;
      if (((AttachInfo)localObject).mHardwareAccelerationRequested)
      {
        j = k;
        if (ThreadedRenderer.isAvailable()) {
          j = k | 0x40;
        }
      }
    }
    k = j;
    if ((0x10000000 & i) != 0) {
      k = j | 0x80;
    }
    i = this.mPrivateFlags2;
    j = k;
    if ((i & 0x1) != 0) {
      j = k | 0x100;
    }
    k = j;
    if ((i & 0x2) != 0) {
      k = j | 0x200;
    }
    int[] arrayOfInt = StateSet.get(k);
    if (paramInt == 0) {
      return arrayOfInt;
    }
    if (arrayOfInt != null)
    {
      localObject = new int[arrayOfInt.length + paramInt];
      System.arraycopy(arrayOfInt, 0, localObject, 0, arrayOfInt.length);
    }
    else
    {
      localObject = new int[paramInt];
    }
    return (int[])localObject;
  }
  
  void originalRefreshDrawableState()
  {
    this.mPrivateFlags |= 0x400;
    drawableStateChanged();
    ViewParent localViewParent = this.mParent;
    if (localViewParent != null) {
      localViewParent.childDrawableStateChanged(this);
    }
  }
  
  public void outputDirtyFlags(String paramString, boolean paramBoolean, int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append(this);
    localStringBuilder.append("             DIRTY(");
    localStringBuilder.append(this.mPrivateFlags & 0x200000);
    localStringBuilder.append(") DRAWN(");
    localStringBuilder.append(this.mPrivateFlags & 0x20);
    localStringBuilder.append(") CACHE_VALID(");
    localStringBuilder.append(this.mPrivateFlags & 0x8000);
    localStringBuilder.append(") INVALIDATED(");
    localStringBuilder.append(this.mPrivateFlags & 0x80000000);
    localStringBuilder.append(")");
    Log.d("View", localStringBuilder.toString());
    if (paramBoolean) {
      this.mPrivateFlags &= paramInt;
    }
    if ((this instanceof ViewGroup))
    {
      ViewGroup localViewGroup = (ViewGroup)this;
      int i = localViewGroup.getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = localViewGroup.getChildAt(j);
        localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramString);
        localStringBuilder.append("  ");
        localView.outputDirtyFlags(localStringBuilder.toString(), paramBoolean, paramInt);
      }
    }
  }
  
  protected boolean overScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean)
  {
    int i = this.mOverScrollMode;
    int j;
    if (computeHorizontalScrollRange() > computeHorizontalScrollExtent()) {
      j = 1;
    } else {
      j = 0;
    }
    int k;
    if (computeVerticalScrollRange() > computeVerticalScrollExtent()) {
      k = 1;
    } else {
      k = 0;
    }
    if ((i != 0) && ((i != 1) || (j == 0))) {
      j = 0;
    } else {
      j = 1;
    }
    if ((i != 0) && ((i != 1) || (k == 0))) {
      k = 0;
    } else {
      k = 1;
    }
    paramInt3 += paramInt1;
    if (j == 0) {
      paramInt1 = 0;
    } else {
      paramInt1 = paramInt7;
    }
    paramInt4 += paramInt2;
    if (k == 0) {
      paramInt2 = 0;
    } else {
      paramInt2 = paramInt8;
    }
    paramInt7 = -paramInt1;
    paramInt1 += paramInt5;
    paramInt5 = -paramInt2;
    paramInt2 += paramInt6;
    if (paramInt3 > paramInt1)
    {
      paramBoolean = true;
    }
    else if (paramInt3 < paramInt7)
    {
      paramInt1 = paramInt7;
      paramBoolean = true;
    }
    else
    {
      paramBoolean = false;
      paramInt1 = paramInt3;
    }
    boolean bool;
    if (paramInt4 > paramInt2)
    {
      bool = true;
    }
    else if (paramInt4 < paramInt5)
    {
      paramInt2 = paramInt5;
      bool = true;
    }
    else
    {
      bool = false;
      paramInt2 = paramInt4;
    }
    onOverScrolled(paramInt1, paramInt2, paramBoolean, bool);
    if ((!paramBoolean) && (!bool)) {
      paramBoolean = false;
    } else {
      paramBoolean = true;
    }
    return paramBoolean;
  }
  
  public boolean performAccessibilityAction(int paramInt, Bundle paramBundle)
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      return localAccessibilityDelegate.performAccessibilityAction(this, paramInt, paramBundle);
    }
    return performAccessibilityActionInternal(paramInt, paramBundle);
  }
  
  @UnsupportedAppUsage
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if ((isNestedScrollingEnabled()) && ((paramInt == 8192) || (paramInt == 4096) || (paramInt == 16908344) || (paramInt == 16908345) || (paramInt == 16908346) || (paramInt == 16908347)) && (dispatchNestedPrePerformAccessibilityAction(paramInt, paramBundle))) {
      return true;
    }
    switch (paramInt)
    {
    default: 
      break;
    case 16908357: 
      paramBundle = this.mTooltipInfo;
      if ((paramBundle != null) && (paramBundle.mTooltipPopup != null))
      {
        hideTooltip();
        return true;
      }
      return false;
    case 16908356: 
      paramBundle = this.mTooltipInfo;
      if ((paramBundle != null) && (paramBundle.mTooltipPopup != null)) {
        return false;
      }
      return showLongClickTooltip(0, 0);
    case 16908348: 
      if (isContextClickable())
      {
        performContextClick();
        return true;
      }
      break;
    case 16908342: 
      paramBundle = this.mAttachInfo;
      if (paramBundle != null)
      {
        paramBundle = paramBundle.mTmpInvalRect;
        getDrawingRect(paramBundle);
        return requestRectangleOnScreen(paramBundle, true);
      }
      break;
    case 131072: 
      if (getIterableTextForAccessibility() == null) {
        return false;
      }
      int i = -1;
      if (paramBundle != null) {
        paramInt = paramBundle.getInt("ACTION_ARGUMENT_SELECTION_START_INT", -1);
      } else {
        paramInt = -1;
      }
      if (paramBundle != null) {
        i = paramBundle.getInt("ACTION_ARGUMENT_SELECTION_END_INT", -1);
      }
      if (((getAccessibilitySelectionStart() != paramInt) || (getAccessibilitySelectionEnd() != i)) && (paramInt == i))
      {
        setAccessibilitySelection(paramInt, i);
        notifyViewAccessibilityStateChangedIfNeeded(0);
        return true;
      }
      break;
    case 512: 
      if (paramBundle != null) {
        return traverseAtGranularity(paramBundle.getInt("ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT"), false, paramBundle.getBoolean("ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN"));
      }
      break;
    case 256: 
      if (paramBundle != null) {
        return traverseAtGranularity(paramBundle.getInt("ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT"), true, paramBundle.getBoolean("ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN"));
      }
      break;
    case 128: 
      if (isAccessibilityFocused())
      {
        clearAccessibilityFocus();
        return true;
      }
      break;
    case 64: 
      if (!isAccessibilityFocused()) {
        return requestAccessibilityFocus();
      }
      break;
    case 32: 
      if (isLongClickable())
      {
        performLongClick();
        return true;
      }
      break;
    case 16: 
      if (isClickable())
      {
        performClickInternal();
        return true;
      }
      break;
    case 8: 
      if (isSelected())
      {
        setSelected(false);
        return isSelected() ^ true;
      }
      break;
    case 4: 
      if (!isSelected())
      {
        setSelected(true);
        return isSelected();
      }
      break;
    case 2: 
      if (hasFocus())
      {
        clearFocus();
        return isFocused() ^ true;
      }
      break;
    case 1: 
      if (!hasFocus())
      {
        getViewRootImpl().ensureTouchMode(false);
        return requestFocus();
      }
      break;
    }
    return false;
  }
  
  protected boolean performButtonActionOnTouchDown(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.isFromSource(8194)) && ((paramMotionEvent.getButtonState() & 0x2) != 0))
    {
      showContextMenu(paramMotionEvent.getX(), paramMotionEvent.getY());
      this.mPrivateFlags |= 0x4000000;
      return true;
    }
    return false;
  }
  
  public boolean performClick()
  {
    notifyAutofillManagerOnClick();
    ListenerInfo localListenerInfo = this.mListenerInfo;
    boolean bool;
    if ((localListenerInfo != null) && (localListenerInfo.mOnClickListener != null))
    {
      playSoundEffect(0);
      localListenerInfo.mOnClickListener.onClick(this);
      bool = true;
    }
    else
    {
      bool = false;
    }
    sendAccessibilityEvent(1);
    notifyEnterOrExitForAutoFillIfNeeded(true);
    return bool;
  }
  
  void performCollectViewAttributes(AttachInfo paramAttachInfo, int paramInt)
  {
    if ((paramInt & 0xC) == 0)
    {
      if ((this.mViewFlags & 0x4000000) == 67108864) {
        paramAttachInfo.mKeepScreenOn = true;
      }
      paramAttachInfo.mSystemUiVisibility |= this.mSystemUiVisibility;
      ListenerInfo localListenerInfo = this.mListenerInfo;
      if ((localListenerInfo != null) && (localListenerInfo.mOnSystemUiVisibilityChangeListener != null)) {
        paramAttachInfo.mHasSystemUiListeners = true;
      }
    }
  }
  
  public boolean performContextClick()
  {
    sendAccessibilityEvent(8388608);
    boolean bool1 = false;
    ListenerInfo localListenerInfo = this.mListenerInfo;
    boolean bool2 = bool1;
    if (localListenerInfo != null)
    {
      bool2 = bool1;
      if (localListenerInfo.mOnContextClickListener != null) {
        bool2 = localListenerInfo.mOnContextClickListener.onContextClick(this);
      }
    }
    if (bool2) {
      performHapticFeedback(6);
    }
    return bool2;
  }
  
  public boolean performContextClick(float paramFloat1, float paramFloat2)
  {
    return performContextClick();
  }
  
  public boolean performHapticFeedback(int paramInt)
  {
    return performHapticFeedback(paramInt, 0);
  }
  
  public boolean performHapticFeedback(int paramInt1, int paramInt2)
  {
    Object localObject = this.mAttachInfo;
    boolean bool = false;
    if (localObject == null) {
      return false;
    }
    if (((paramInt2 & 0x4) != 0) && (!this.mHapticEnabledExplicitly)) {
      return false;
    }
    if (((paramInt2 & 0x1) == 0) && (!isHapticFeedbackEnabled())) {
      return false;
    }
    localObject = this.mAttachInfo.mRootCallbacks;
    if ((paramInt2 & 0x2) != 0) {
      bool = true;
    }
    return ((View.AttachInfo.Callbacks)localObject).performHapticFeedback(paramInt1, bool);
  }
  
  public boolean performLongClick()
  {
    return performLongClickInternal(this.mLongClickX, this.mLongClickY);
  }
  
  public boolean performLongClick(float paramFloat1, float paramFloat2)
  {
    this.mLongClickX = paramFloat1;
    this.mLongClickY = paramFloat2;
    boolean bool = performLongClick();
    this.mLongClickX = NaN.0F;
    this.mLongClickY = NaN.0F;
    return bool;
  }
  
  public void playSoundEffect(int paramInt)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if ((localAttachInfo != null) && (localAttachInfo.mRootCallbacks != null) && (isSoundEffectsEnabled()))
    {
      this.mAttachInfo.mRootCallbacks.playSoundEffect(paramInt);
      return;
    }
  }
  
  protected boolean pointInHoveredChild(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  final boolean pointInView(float paramFloat1, float paramFloat2)
  {
    return pointInView(paramFloat1, paramFloat2, 0.0F);
  }
  
  @UnsupportedAppUsage
  public boolean pointInView(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    boolean bool;
    if ((paramFloat1 >= -paramFloat3) && (paramFloat2 >= -paramFloat3) && (paramFloat1 < this.mRight - this.mLeft + paramFloat3) && (paramFloat2 < this.mBottom - this.mTop + paramFloat3)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean post(Runnable paramRunnable)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      return localAttachInfo.mHandler.post(paramRunnable);
    }
    getRunQueue().post(paramRunnable);
    return true;
  }
  
  public boolean postDelayed(Runnable paramRunnable, long paramLong)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      return localAttachInfo.mHandler.postDelayed(paramRunnable, paramLong);
    }
    getRunQueue().postDelayed(paramRunnable, paramLong);
    return true;
  }
  
  public void postInvalidate()
  {
    postInvalidateDelayed(0L);
  }
  
  public void postInvalidate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    postInvalidateDelayed(0L, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void postInvalidateDelayed(long paramLong)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      localAttachInfo.mViewRootImpl.dispatchInvalidateDelayed(this, paramLong);
    }
  }
  
  public void postInvalidateDelayed(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null)
    {
      View.AttachInfo.InvalidateInfo localInvalidateInfo = View.AttachInfo.InvalidateInfo.obtain();
      localInvalidateInfo.target = this;
      localInvalidateInfo.left = paramInt1;
      localInvalidateInfo.top = paramInt2;
      localInvalidateInfo.right = paramInt3;
      localInvalidateInfo.bottom = paramInt4;
      localAttachInfo.mViewRootImpl.dispatchInvalidateRectDelayed(localInvalidateInfo, paramLong);
    }
  }
  
  public void postInvalidateOnAnimation()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      localAttachInfo.mViewRootImpl.dispatchInvalidateOnAnimation(this);
    }
  }
  
  public void postInvalidateOnAnimation(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null)
    {
      View.AttachInfo.InvalidateInfo localInvalidateInfo = View.AttachInfo.InvalidateInfo.obtain();
      localInvalidateInfo.target = this;
      localInvalidateInfo.left = paramInt1;
      localInvalidateInfo.top = paramInt2;
      localInvalidateInfo.right = paramInt3;
      localInvalidateInfo.bottom = paramInt4;
      localAttachInfo.mViewRootImpl.dispatchInvalidateRectOnAnimation(localInvalidateInfo);
    }
  }
  
  public void postOnAnimation(Runnable paramRunnable)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      localAttachInfo.mViewRootImpl.mChoreographer.postCallback(1, paramRunnable, null);
    } else {
      getRunQueue().post(paramRunnable);
    }
  }
  
  public void postOnAnimationDelayed(Runnable paramRunnable, long paramLong)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      localAttachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(1, paramRunnable, null, paramLong);
    } else {
      getRunQueue().postDelayed(paramRunnable, paramLong);
    }
  }
  
  void postUpdateSystemGestureExclusionRects()
  {
    Handler localHandler = getHandler();
    if (localHandler != null) {
      localHandler.postAtFrontOfQueue(new _..Lambda.WlJa6OPA72p3gYtA3nVKC7Z1tGY(this));
    }
  }
  
  @UnsupportedAppUsage
  protected void recomputePadding()
  {
    internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
  }
  
  public void refreshDrawableState()
  {
    if (Android_View_View.Extension.get().getExtension() != null) {
      ((Android_View_View.Interface)Android_View_View.Extension.get().getExtension().asInterface()).refreshDrawableState(this);
    } else {
      originalRefreshDrawableState();
    }
  }
  
  public void releasePointerCapture()
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    if (localViewRootImpl != null) {
      localViewRootImpl.requestPointerCapture(false);
    }
  }
  
  public boolean removeCallbacks(Runnable paramRunnable)
  {
    if (paramRunnable != null)
    {
      AttachInfo localAttachInfo = this.mAttachInfo;
      if (localAttachInfo != null)
      {
        localAttachInfo.mHandler.removeCallbacks(paramRunnable);
        localAttachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, paramRunnable, null);
      }
      getRunQueue().removeCallbacks(paramRunnable);
    }
    return true;
  }
  
  public void removeFrameMetricsListener(Window.OnFrameMetricsAvailableListener paramOnFrameMetricsAvailableListener)
  {
    ThreadedRenderer localThreadedRenderer = getThreadedRenderer();
    FrameMetricsObserver localFrameMetricsObserver = findFrameMetricsObserver(paramOnFrameMetricsAvailableListener);
    if (localFrameMetricsObserver != null)
    {
      paramOnFrameMetricsAvailableListener = this.mFrameMetricsObservers;
      if (paramOnFrameMetricsAvailableListener != null)
      {
        paramOnFrameMetricsAvailableListener.remove(localFrameMetricsObserver);
        if (localThreadedRenderer != null) {
          localThreadedRenderer.removeFrameMetricsObserver(localFrameMetricsObserver);
        }
      }
      return;
    }
    throw new IllegalArgumentException("attempt to remove OnFrameMetricsAvailableListener that was never added");
  }
  
  public void removeOnAttachStateChangeListener(OnAttachStateChangeListener paramOnAttachStateChangeListener)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mOnAttachStateChangeListeners != null))
    {
      localListenerInfo.mOnAttachStateChangeListeners.remove(paramOnAttachStateChangeListener);
      return;
    }
  }
  
  public void removeOnLayoutChangeListener(OnLayoutChangeListener paramOnLayoutChangeListener)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mOnLayoutChangeListeners != null))
    {
      localListenerInfo.mOnLayoutChangeListeners.remove(paramOnLayoutChangeListener);
      return;
    }
  }
  
  public void removeOnUnhandledKeyEventListener(OnUnhandledKeyEventListener paramOnUnhandledKeyEventListener)
  {
    ListenerInfo localListenerInfo = this.mListenerInfo;
    if ((localListenerInfo != null) && (localListenerInfo.mUnhandledKeyListeners != null) && (!this.mListenerInfo.mUnhandledKeyListeners.isEmpty()))
    {
      this.mListenerInfo.mUnhandledKeyListeners.remove(paramOnUnhandledKeyEventListener);
      if (this.mListenerInfo.mUnhandledKeyListeners.isEmpty())
      {
        ListenerInfo.access$4102(this.mListenerInfo, null);
        paramOnUnhandledKeyEventListener = this.mParent;
        if ((paramOnUnhandledKeyEventListener instanceof ViewGroup)) {
          ((ViewGroup)paramOnUnhandledKeyEventListener).decrementChildUnhandledKeyListeners();
        }
      }
    }
  }
  
  @UnsupportedAppUsage
  public boolean requestAccessibilityFocus()
  {
    Object localObject = AccessibilityManager.getInstance(this.mContext);
    if ((((AccessibilityManager)localObject).isEnabled()) && (((AccessibilityManager)localObject).isTouchExplorationEnabled()))
    {
      if ((this.mViewFlags & 0xC) != 0) {
        return false;
      }
      int i = this.mPrivateFlags2;
      if ((i & 0x4000000) == 0)
      {
        this.mPrivateFlags2 = (i | 0x4000000);
        localObject = getViewRootImpl();
        if (localObject != null) {
          ((ViewRootImpl)localObject).setAccessibilityFocus(this, null);
        }
        invalidate();
        sendAccessibilityEvent(32768);
        return true;
      }
      return false;
    }
    return false;
  }
  
  public void requestApplyInsets()
  {
    requestFitSystemWindows();
  }
  
  @Deprecated
  public void requestFitSystemWindows()
  {
    ViewParent localViewParent = this.mParent;
    if (localViewParent != null) {
      localViewParent.requestFitSystemWindows();
    }
  }
  
  public final boolean requestFocus()
  {
    return requestFocus(130);
  }
  
  public final boolean requestFocus(int paramInt)
  {
    return requestFocus(paramInt, null);
  }
  
  public boolean requestFocus(int paramInt, Rect paramRect)
  {
    return requestFocusNoSearch(paramInt, paramRect);
  }
  
  public final boolean requestFocusFromTouch()
  {
    if (isInTouchMode())
    {
      ViewRootImpl localViewRootImpl = getViewRootImpl();
      if (localViewRootImpl != null) {
        localViewRootImpl.ensureTouchMode(false);
      }
    }
    return requestFocus(130);
  }
  
  public void requestKeyboardShortcuts(List<KeyboardShortcutGroup> paramList, int paramInt) {}
  
  public void requestLayout()
  {
    Object localObject = this.mMeasureCache;
    if (localObject != null) {
      ((LongSparseLongArray)localObject).clear();
    }
    localObject = this.mAttachInfo;
    if ((localObject != null) && (((AttachInfo)localObject).mViewRequestingLayout == null))
    {
      localObject = getViewRootImpl();
      if ((localObject != null) && (((ViewRootImpl)localObject).isInLayout()) && (!((ViewRootImpl)localObject).requestLayoutDuringLayout(this))) {
        return;
      }
      this.mAttachInfo.mViewRequestingLayout = this;
    }
    this.mPrivateFlags |= 0x1000;
    this.mPrivateFlags |= 0x80000000;
    localObject = this.mParent;
    if ((localObject != null) && (!((ViewParent)localObject).isLayoutRequested())) {
      this.mParent.requestLayout();
    }
    localObject = this.mAttachInfo;
    if ((localObject != null) && (((AttachInfo)localObject).mViewRequestingLayout == this)) {
      this.mAttachInfo.mViewRequestingLayout = null;
    }
  }
  
  public void requestPointerCapture()
  {
    ViewRootImpl localViewRootImpl = getViewRootImpl();
    if (localViewRootImpl != null) {
      localViewRootImpl.requestPointerCapture(true);
    }
  }
  
  public boolean requestRectangleOnScreen(Rect paramRect)
  {
    return requestRectangleOnScreen(paramRect, false);
  }
  
  public boolean requestRectangleOnScreen(Rect paramRect, boolean paramBoolean)
  {
    if (this.mParent == null) {
      return false;
    }
    View localView = this;
    Object localObject = this.mAttachInfo;
    if (localObject != null) {
      localObject = ((AttachInfo)localObject).mTmpTransformRect;
    } else {
      localObject = new RectF();
    }
    ((RectF)localObject).set(paramRect);
    ViewParent localViewParent = this.mParent;
    boolean bool1 = false;
    boolean bool2;
    for (;;)
    {
      bool2 = bool1;
      if (localViewParent == null) {
        break;
      }
      paramRect.set((int)((RectF)localObject).left, (int)((RectF)localObject).top, (int)((RectF)localObject).right, (int)((RectF)localObject).bottom);
      bool1 |= localViewParent.requestChildRectangleOnScreen(localView, paramRect, paramBoolean);
      if (!(localViewParent instanceof View))
      {
        bool2 = bool1;
        break;
      }
      ((RectF)localObject).offset(localView.mLeft - localView.getScrollX(), localView.mTop - localView.getScrollY());
      localView = (View)localViewParent;
      localViewParent = localView.getParent();
    }
    return bool2;
  }
  
  public final void requestUnbufferedDispatch(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if ((this.mAttachInfo != null) && ((i == 0) || (i == 2)) && (paramMotionEvent.isTouchEvent()))
    {
      this.mAttachInfo.mUnbufferedDispatchRequested = true;
      return;
    }
  }
  
  public final <T extends View> T requireViewById(int paramInt)
  {
    View localView = findViewById(paramInt);
    if (localView != null) {
      return localView;
    }
    throw new IllegalArgumentException("ID does not reference a View inside this View");
  }
  
  @UnsupportedAppUsage
  public void resetPaddingToInitialValues()
  {
    if (isRtlCompatibilityMode())
    {
      this.mPaddingLeft = this.mUserPaddingLeftInitial;
      this.mPaddingRight = this.mUserPaddingRightInitial;
      return;
    }
    int i;
    if (isLayoutRtl())
    {
      i = this.mUserPaddingEnd;
      if (i < 0) {
        i = this.mUserPaddingLeftInitial;
      }
      this.mPaddingLeft = i;
      i = this.mUserPaddingStart;
      if (i < 0) {
        i = this.mUserPaddingRightInitial;
      }
      this.mPaddingRight = i;
    }
    else
    {
      i = this.mUserPaddingStart;
      if (i < 0) {
        i = this.mUserPaddingLeftInitial;
      }
      this.mPaddingLeft = i;
      i = this.mUserPaddingEnd;
      if (i < 0) {
        i = this.mUserPaddingRightInitial;
      }
      this.mPaddingRight = i;
    }
  }
  
  public void resetPivot()
  {
    if (this.mRenderNode.resetPivot()) {
      invalidateViewProperty(false, false);
    }
  }
  
  protected void resetResolvedDrawables()
  {
    resetResolvedDrawablesInternal();
  }
  
  void resetResolvedDrawablesInternal()
  {
    this.mPrivateFlags2 &= 0xBFFFFFFF;
  }
  
  public void resetResolvedLayoutDirection()
  {
    this.mPrivateFlags2 &= 0xFFFFFFCF;
  }
  
  public void resetResolvedPadding()
  {
    resetResolvedPaddingInternal();
  }
  
  void resetResolvedPaddingInternal()
  {
    this.mPrivateFlags2 &= 0xDFFFFFFF;
  }
  
  public void resetResolvedTextAlignment()
  {
    this.mPrivateFlags2 &= 0xFFF0FFFF;
    this.mPrivateFlags2 |= 0x20000;
  }
  
  public void resetResolvedTextDirection()
  {
    this.mPrivateFlags2 &= 0xE1FF;
    this.mPrivateFlags2 |= 0x400;
  }
  
  public void resetRtlProperties()
  {
    resetResolvedLayoutDirection();
    resetResolvedTextDirection();
    resetResolvedTextAlignment();
    resetResolvedPadding();
    resetResolvedDrawables();
  }
  
  void resetSubtreeAccessibilityStateChanged()
  {
    this.mPrivateFlags2 &= 0xF7FFFFFF;
  }
  
  protected void resolveDrawables()
  {
    if ((!isLayoutDirectionResolved()) && (getRawLayoutDirection() == 2)) {
      return;
    }
    int i;
    if (isLayoutDirectionResolved()) {
      i = getLayoutDirection();
    } else {
      i = getRawLayoutDirection();
    }
    Object localObject = this.mBackground;
    if (localObject != null) {
      ((Drawable)localObject).setLayoutDirection(i);
    }
    localObject = this.mForegroundInfo;
    if ((localObject != null) && (((ForegroundInfo)localObject).mDrawable != null)) {
      this.mForegroundInfo.mDrawable.setLayoutDirection(i);
    }
    localObject = this.mDefaultFocusHighlight;
    if (localObject != null) {
      ((Drawable)localObject).setLayoutDirection(i);
    }
    this.mPrivateFlags2 |= 0x40000000;
    onResolveDrawables(i);
  }
  
  public boolean resolveLayoutDirection()
  {
    this.mPrivateFlags2 &= 0xFFFFFFCF;
    if (hasRtlSupport())
    {
      int i = this.mPrivateFlags2;
      int j = (i & 0xC) >> 2;
      if (j != 1)
      {
        if (j != 2)
        {
          if ((j == 3) && (1 == TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()))) {
            this.mPrivateFlags2 |= 0x10;
          }
        }
        else
        {
          if (!canResolveLayoutDirection()) {
            return false;
          }
          try
          {
            if (!this.mParent.isLayoutDirectionResolved()) {
              return false;
            }
            if (this.mParent.getLayoutDirection() == 1) {
              this.mPrivateFlags2 |= 0x10;
            }
          }
          catch (AbstractMethodError localAbstractMethodError)
          {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(this.mParent.getClass().getSimpleName());
            localStringBuilder.append(" does not fully implement ViewParent");
            Log.e("View", localStringBuilder.toString(), localAbstractMethodError);
          }
        }
      }
      else {
        this.mPrivateFlags2 = (i | 0x10);
      }
    }
    this.mPrivateFlags2 |= 0x20;
    return true;
  }
  
  public void resolveLayoutParams()
  {
    ViewGroup.LayoutParams localLayoutParams = this.mLayoutParams;
    if (localLayoutParams != null) {
      localLayoutParams.resolveLayoutDirection(getLayoutDirection());
    }
  }
  
  @UnsupportedAppUsage
  public void resolvePadding()
  {
    int i = getLayoutDirection();
    if (!isRtlCompatibilityMode())
    {
      if ((this.mBackground != null) && ((!this.mLeftPaddingDefined) || (!this.mRightPaddingDefined)))
      {
        Rect localRect1 = (Rect)sThreadLocal.get();
        Rect localRect2 = localRect1;
        if (localRect1 == null)
        {
          localRect2 = new Rect();
          sThreadLocal.set(localRect2);
        }
        this.mBackground.getPadding(localRect2);
        if (!this.mLeftPaddingDefined) {
          this.mUserPaddingLeftInitial = localRect2.left;
        }
        if (!this.mRightPaddingDefined) {
          this.mUserPaddingRightInitial = localRect2.right;
        }
      }
      if (i != 1)
      {
        j = this.mUserPaddingStart;
        if (j != Integer.MIN_VALUE) {
          this.mUserPaddingLeft = j;
        } else {
          this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
        }
        j = this.mUserPaddingEnd;
        if (j != Integer.MIN_VALUE) {
          this.mUserPaddingRight = j;
        } else {
          this.mUserPaddingRight = this.mUserPaddingRightInitial;
        }
      }
      else
      {
        j = this.mUserPaddingStart;
        if (j != Integer.MIN_VALUE) {
          this.mUserPaddingRight = j;
        } else {
          this.mUserPaddingRight = this.mUserPaddingRightInitial;
        }
        j = this.mUserPaddingEnd;
        if (j != Integer.MIN_VALUE) {
          this.mUserPaddingLeft = j;
        } else {
          this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
        }
      }
      int j = this.mUserPaddingBottom;
      if (j < 0) {
        j = this.mPaddingBottom;
      }
      this.mUserPaddingBottom = j;
    }
    internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
    onRtlPropertiesChanged(i);
    this.mPrivateFlags2 |= 0x20000000;
  }
  
  public boolean resolveRtlPropertiesIfNeeded()
  {
    if (!needRtlPropertiesResolution()) {
      return false;
    }
    if (!isLayoutDirectionResolved())
    {
      resolveLayoutDirection();
      resolveLayoutParams();
    }
    if (!isTextDirectionResolved()) {
      resolveTextDirection();
    }
    if (!isTextAlignmentResolved()) {
      resolveTextAlignment();
    }
    if (!areDrawablesResolved()) {
      resolveDrawables();
    }
    if (!isPaddingResolved()) {
      resolvePadding();
    }
    onRtlPropertiesChanged(getLayoutDirection());
    return true;
  }
  
  public boolean resolveTextAlignment()
  {
    this.mPrivateFlags2 &= 0xFFF0FFFF;
    if (hasRtlSupport())
    {
      int i = getRawTextAlignment();
      switch (i)
      {
      default: 
        this.mPrivateFlags2 |= 0x20000;
        break;
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
        this.mPrivateFlags2 |= i << 17;
        break;
      case 0: 
        if (!canResolveTextAlignment())
        {
          this.mPrivateFlags2 |= 0x20000;
          return false;
        }
        try
        {
          if (!this.mParent.isTextAlignmentResolved())
          {
            this.mPrivateFlags2 = (0x20000 | this.mPrivateFlags2);
            return false;
          }
          try
          {
            i = this.mParent.getTextAlignment();
          }
          catch (AbstractMethodError localAbstractMethodError1)
          {
            localStringBuilder = new StringBuilder();
            localStringBuilder.append(this.mParent.getClass().getSimpleName());
            localStringBuilder.append(" does not fully implement ViewParent");
            Log.e("View", localStringBuilder.toString(), localAbstractMethodError1);
            i = 1;
          }
          switch (i)
          {
          default: 
            this.mPrivateFlags2 |= 0x20000;
            break;
          case 1: 
          case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
            this.mPrivateFlags2 |= i << 17;
          }
        }
        catch (AbstractMethodError localAbstractMethodError2)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append(this.mParent.getClass().getSimpleName());
          localStringBuilder.append(" does not fully implement ViewParent");
          Log.e("View", localStringBuilder.toString(), localAbstractMethodError2);
          this.mPrivateFlags2 |= 0x30000;
          return true;
        }
      }
    }
    else
    {
      this.mPrivateFlags2 |= 0x20000;
    }
    this.mPrivateFlags2 |= 0x10000;
    return true;
  }
  
  public boolean resolveTextDirection()
  {
    this.mPrivateFlags2 &= 0xE1FF;
    if (hasRtlSupport())
    {
      int i = getRawTextDirection();
      switch (i)
      {
      default: 
        this.mPrivateFlags2 |= 0x400;
        break;
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
        this.mPrivateFlags2 |= i << 10;
        break;
      case 0: 
        if (!canResolveTextDirection())
        {
          this.mPrivateFlags2 |= 0x400;
          return false;
        }
        try
        {
          if (!this.mParent.isTextDirectionResolved())
          {
            this.mPrivateFlags2 |= 0x400;
            return false;
          }
          try
          {
            i = this.mParent.getTextDirection();
          }
          catch (AbstractMethodError localAbstractMethodError1)
          {
            StringBuilder localStringBuilder2 = new StringBuilder();
            localStringBuilder2.append(this.mParent.getClass().getSimpleName());
            localStringBuilder2.append(" does not fully implement ViewParent");
            Log.e("View", localStringBuilder2.toString(), localAbstractMethodError1);
            i = 3;
          }
          switch (i)
          {
          default: 
            this.mPrivateFlags2 |= 0x400;
            break;
          case 1: 
          case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
          case 7: 
            this.mPrivateFlags2 |= i << 10;
          }
        }
        catch (AbstractMethodError localAbstractMethodError2)
        {
          StringBuilder localStringBuilder1 = new StringBuilder();
          localStringBuilder1.append(this.mParent.getClass().getSimpleName());
          localStringBuilder1.append(" does not fully implement ViewParent");
          Log.e("View", localStringBuilder1.toString(), localAbstractMethodError2);
          this.mPrivateFlags2 |= 0x600;
          return true;
        }
      }
    }
    else
    {
      this.mPrivateFlags2 |= 0x400;
    }
    this.mPrivateFlags2 |= 0x200;
    return true;
  }
  
  public boolean restoreDefaultFocus()
  {
    return requestFocus(130);
  }
  
  public boolean restoreFocusInCluster(int paramInt)
  {
    if (restoreDefaultFocus()) {
      return true;
    }
    return requestFocus(paramInt);
  }
  
  public boolean restoreFocusNotInCluster()
  {
    return requestFocus(130);
  }
  
  public void restoreHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchRestoreInstanceState(paramSparseArray);
  }
  
  boolean rootViewRequestFocus()
  {
    View localView = getRootView();
    boolean bool;
    if ((localView != null) && (localView.requestFocus())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final void saveAttributeDataForStyleable(Context paramContext, int[] paramArrayOfInt, AttributeSet paramAttributeSet, TypedArray paramTypedArray, int paramInt1, int paramInt2)
  {
    if (!sDebugViewAttributes) {
      return;
    }
    paramContext = paramContext.getTheme().getAttributeResolutionStack(paramInt1, paramInt2, this.mExplicitStyle);
    if (this.mAttributeResolutionStacks == null) {
      this.mAttributeResolutionStacks = new SparseArray();
    }
    if (this.mAttributeSourceResId == null) {
      this.mAttributeSourceResId = new SparseIntArray();
    }
    paramInt2 = paramTypedArray.getIndexCount();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
    {
      int i = paramTypedArray.getIndex(paramInt1);
      this.mAttributeSourceResId.append(paramArrayOfInt[i], paramTypedArray.getSourceResourceId(i, 0));
      this.mAttributeResolutionStacks.append(paramArrayOfInt[i], paramContext);
    }
  }
  
  public void saveHierarchyState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchSaveInstanceState(paramSparseArray);
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    if ((verifyDrawable(paramDrawable)) && (paramRunnable != null))
    {
      paramLong -= SystemClock.uptimeMillis();
      AttachInfo localAttachInfo = this.mAttachInfo;
      if (localAttachInfo != null) {
        localAttachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(1, paramRunnable, paramDrawable, Choreographer.subtractFrameDelay(paramLong));
      } else {
        getRunQueue().postDelayed(paramRunnable, paramLong);
      }
    }
  }
  
  public void scrollBy(int paramInt1, int paramInt2)
  {
    scrollTo(this.mScrollX + paramInt1, this.mScrollY + paramInt2);
  }
  
  public void scrollTo(int paramInt1, int paramInt2)
  {
    if ((this.mScrollX != paramInt1) || (this.mScrollY != paramInt2))
    {
      int i = this.mScrollX;
      int j = this.mScrollY;
      this.mScrollX = paramInt1;
      this.mScrollY = paramInt2;
      invalidateParentCaches();
      onScrollChanged(this.mScrollX, this.mScrollY, i, j);
      if (!awakenScrollBars()) {
        postInvalidateOnAnimation();
      }
    }
  }
  
  public void sendAccessibilityEvent(int paramInt)
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      localAccessibilityDelegate.sendAccessibilityEvent(this, paramInt);
    } else {
      sendAccessibilityEventInternal(paramInt);
    }
  }
  
  public void sendAccessibilityEventInternal(int paramInt)
  {
    if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
      sendAccessibilityEventUnchecked(AccessibilityEvent.obtain(paramInt));
    }
  }
  
  public void sendAccessibilityEventUnchecked(AccessibilityEvent paramAccessibilityEvent)
  {
    AccessibilityDelegate localAccessibilityDelegate = this.mAccessibilityDelegate;
    if (localAccessibilityDelegate != null) {
      localAccessibilityDelegate.sendAccessibilityEventUnchecked(this, paramAccessibilityEvent);
    } else {
      sendAccessibilityEventUncheckedInternal(paramAccessibilityEvent);
    }
  }
  
  public void sendAccessibilityEventUncheckedInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    int i = paramAccessibilityEvent.getEventType();
    int j = 1;
    if (i == 32) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i != 0) && ((0x20 & paramAccessibilityEvent.getContentChangeTypes()) != 0)) {
      i = j;
    } else {
      i = 0;
    }
    if ((!isShown()) && (i == 0)) {
      return;
    }
    onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if ((paramAccessibilityEvent.getEventType() & 0x2A1BF) != 0) {
      dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
    }
    if (getParent() != null) {
      getParent().requestSendAccessibilityEvent(this, paramAccessibilityEvent);
    }
  }
  
  public void setAccessibilityDelegate(AccessibilityDelegate paramAccessibilityDelegate)
  {
    this.mAccessibilityDelegate = paramAccessibilityDelegate;
  }
  
  public void setAccessibilityHeading(boolean paramBoolean)
  {
    updatePflags3AndNotifyA11yIfChanged(Integer.MIN_VALUE, paramBoolean);
  }
  
  public void setAccessibilityLiveRegion(int paramInt)
  {
    if (paramInt != getAccessibilityLiveRegion())
    {
      this.mPrivateFlags2 &= 0xFE7FFFFF;
      this.mPrivateFlags2 |= paramInt << 23 & 0x1800000;
      notifyViewAccessibilityStateChangedIfNeeded(0);
    }
  }
  
  public void setAccessibilityPaneTitle(CharSequence paramCharSequence)
  {
    if (!TextUtils.equals(paramCharSequence, this.mAccessibilityPaneTitle))
    {
      this.mAccessibilityPaneTitle = paramCharSequence;
      notifyViewAccessibilityStateChangedIfNeeded(8);
    }
  }
  
  public void setAccessibilitySelection(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == paramInt2) && (paramInt2 == this.mAccessibilityCursorPosition)) {
      return;
    }
    if ((paramInt1 >= 0) && (paramInt1 == paramInt2) && (paramInt2 <= getIterableTextForAccessibility().length())) {
      this.mAccessibilityCursorPosition = paramInt1;
    } else {
      this.mAccessibilityCursorPosition = -1;
    }
    sendAccessibilityEvent(8192);
  }
  
  @RemotableViewMethod
  public void setAccessibilityTraversalAfter(int paramInt)
  {
    if (this.mAccessibilityTraversalAfterId == paramInt) {
      return;
    }
    this.mAccessibilityTraversalAfterId = paramInt;
    notifyViewAccessibilityStateChangedIfNeeded(0);
  }
  
  @RemotableViewMethod
  public void setAccessibilityTraversalBefore(int paramInt)
  {
    if (this.mAccessibilityTraversalBeforeId == paramInt) {
      return;
    }
    this.mAccessibilityTraversalBeforeId = paramInt;
    notifyViewAccessibilityStateChangedIfNeeded(0);
  }
  
  public void setActivated(boolean paramBoolean)
  {
    int i = this.mPrivateFlags;
    int j = 1073741824;
    boolean bool;
    if ((i & 0x40000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    if (bool != paramBoolean)
    {
      i = this.mPrivateFlags;
      if (!paramBoolean) {
        j = 0;
      }
      this.mPrivateFlags = (i & 0xBFFFFFFF | j);
      invalidate(true);
      refreshDrawableState();
      dispatchSetActivated(paramBoolean);
    }
  }
  
  public void setAlpha(float paramFloat)
  {
    ensureTransformationInfo();
    if (this.mTransformationInfo.mAlpha != paramFloat)
    {
      setAlphaInternal(paramFloat);
      if (onSetAlpha((int)(255.0F * paramFloat)))
      {
        this.mPrivateFlags |= 0x40000;
        invalidateParentCaches();
        invalidate(true);
      }
      else
      {
        this.mPrivateFlags &= 0xFFFBFFFF;
        invalidateViewProperty(true, false);
        this.mRenderNode.setAlpha(getFinalAlpha());
      }
    }
  }
  
  void setAlphaInternal(float paramFloat)
  {
    float f = this.mTransformationInfo.mAlpha;
    TransformationInfo.access$2302(this.mTransformationInfo, paramFloat);
    int i = 1;
    int j;
    if (paramFloat == 0.0F) {
      j = 1;
    } else {
      j = 0;
    }
    if (f != 0.0F) {
      i = 0;
    }
    if ((j ^ i) != 0) {
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768435L)
  boolean setAlphaNoInvalidation(float paramFloat)
  {
    ensureTransformationInfo();
    if (this.mTransformationInfo.mAlpha != paramFloat)
    {
      setAlphaInternal(paramFloat);
      if (onSetAlpha((int)(255.0F * paramFloat)))
      {
        this.mPrivateFlags |= 0x40000;
        return true;
      }
      this.mPrivateFlags &= 0xFFFBFFFF;
      this.mRenderNode.setAlpha(getFinalAlpha());
    }
    return false;
  }
  
  public void setAnimation(Animation paramAnimation)
  {
    this.mCurrentAnimation = paramAnimation;
    if (paramAnimation != null)
    {
      AttachInfo localAttachInfo = this.mAttachInfo;
      if ((localAttachInfo != null) && (localAttachInfo.mDisplayState == 1) && (paramAnimation.getStartTime() == -1L)) {
        paramAnimation.setStartTime(AnimationUtils.currentAnimationTimeMillis());
      }
      paramAnimation.reset();
    }
  }
  
  public void setAnimationMatrix(Matrix paramMatrix)
  {
    invalidateViewProperty(true, false);
    this.mRenderNode.setAnimationMatrix(paramMatrix);
    invalidateViewProperty(false, true);
    invalidateParentIfNeededAndWasQuickRejected();
  }
  
  @UnsupportedAppUsage
  public void setAssistBlocked(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mPrivateFlags3 |= 0x4000;
    } else {
      this.mPrivateFlags3 &= 0xBFFF;
    }
  }
  
  public void setAutofillHints(String... paramVarArgs)
  {
    if ((paramVarArgs != null) && (paramVarArgs.length != 0)) {
      this.mAutofillHints = paramVarArgs;
    } else {
      this.mAutofillHints = null;
    }
  }
  
  public void setAutofillId(AutofillId paramAutofillId)
  {
    if (Log.isLoggable("View.Autofill", 2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("setAutofill(): from ");
      localStringBuilder.append(this.mAutofillId);
      localStringBuilder.append(" to ");
      localStringBuilder.append(paramAutofillId);
      Log.v("View.Autofill", localStringBuilder.toString());
    }
    if (!isAttachedToWindow())
    {
      if ((paramAutofillId != null) && (!paramAutofillId.isNonVirtual())) {
        throw new IllegalStateException("Cannot set autofill id assigned to virtual views");
      }
      if ((paramAutofillId == null) && ((this.mPrivateFlags3 & 0x40000000) == 0)) {
        return;
      }
      this.mAutofillId = paramAutofillId;
      if (paramAutofillId != null)
      {
        this.mAutofillViewId = paramAutofillId.getViewId();
        this.mPrivateFlags3 = (0x40000000 | this.mPrivateFlags3);
      }
      else
      {
        this.mAutofillViewId = -1;
        this.mPrivateFlags3 &= 0xBFFFFFFF;
      }
      return;
    }
    throw new IllegalStateException("Cannot set autofill id when view is attached");
  }
  
  public void setAutofilled(boolean paramBoolean)
  {
    int i;
    if (paramBoolean != isAutofilled()) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0)
    {
      if (paramBoolean) {
        this.mPrivateFlags3 |= 0x10000;
      } else {
        this.mPrivateFlags3 &= 0xFFFEFFFF;
      }
      invalidate();
    }
  }
  
  public void setBackground(Drawable paramDrawable)
  {
    setBackgroundDrawable(paramDrawable);
  }
  
  void setBackgroundBounds()
  {
    if (this.mBackgroundSizeChanged)
    {
      Drawable localDrawable = this.mBackground;
      if (localDrawable != null)
      {
        localDrawable.setBounds(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
        this.mBackgroundSizeChanged = false;
        rebuildOutline();
      }
    }
  }
  
  @RemotableViewMethod
  public void setBackgroundColor(int paramInt)
  {
    Drawable localDrawable = this.mBackground;
    if ((localDrawable instanceof ColorDrawable))
    {
      ((ColorDrawable)localDrawable.mutate()).setColor(paramInt);
      computeOpaqueFlags();
      this.mBackgroundResource = 0;
    }
    else
    {
      setBackground(new ColorDrawable(paramInt));
    }
  }
  
  @Deprecated
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    computeOpaqueFlags();
    Object localObject = this.mBackground;
    if (paramDrawable == localObject) {
      return;
    }
    int i = 0;
    this.mBackgroundResource = 0;
    if (localObject != null)
    {
      if (isAttachedToWindow()) {
        this.mBackground.setVisible(false, false);
      }
      this.mBackground.setCallback(null);
      unscheduleDrawable(this.mBackground);
    }
    if (paramDrawable != null)
    {
      Rect localRect = (Rect)sThreadLocal.get();
      localObject = localRect;
      if (localRect == null)
      {
        localObject = new Rect();
        sThreadLocal.set(localObject);
      }
      resetResolvedDrawablesInternal();
      paramDrawable.setLayoutDirection(getLayoutDirection());
      if (paramDrawable.getPadding((Rect)localObject))
      {
        resetResolvedPaddingInternal();
        if (paramDrawable.getLayoutDirection() != 1)
        {
          this.mUserPaddingLeftInitial = ((Rect)localObject).left;
          this.mUserPaddingRightInitial = ((Rect)localObject).right;
          internalSetPadding(((Rect)localObject).left, ((Rect)localObject).top, ((Rect)localObject).right, ((Rect)localObject).bottom);
        }
        else
        {
          this.mUserPaddingLeftInitial = ((Rect)localObject).right;
          this.mUserPaddingRightInitial = ((Rect)localObject).left;
          internalSetPadding(((Rect)localObject).right, ((Rect)localObject).top, ((Rect)localObject).left, ((Rect)localObject).bottom);
        }
        this.mLeftPaddingDefined = false;
        this.mRightPaddingDefined = false;
      }
      localObject = this.mBackground;
      if ((localObject == null) || (((Drawable)localObject).getMinimumHeight() != paramDrawable.getMinimumHeight()) || (this.mBackground.getMinimumWidth() != paramDrawable.getMinimumWidth())) {
        i = 1;
      }
      this.mBackground = paramDrawable;
      if (paramDrawable.isStateful()) {
        paramDrawable.setState(getDrawableState());
      }
      if (isAttachedToWindow())
      {
        boolean bool;
        if ((getWindowVisibility() == 0) && (isShown())) {
          bool = true;
        } else {
          bool = false;
        }
        paramDrawable.setVisible(bool, false);
      }
      applyBackgroundTint();
      paramDrawable.setCallback(this);
      int j = this.mPrivateFlags;
      if ((j & 0x80) != 0)
      {
        this.mPrivateFlags = (j & 0xFF7F);
        i = 1;
      }
    }
    else
    {
      this.mBackground = null;
      if (((this.mViewFlags & 0x80) != 0) && (this.mDefaultFocusHighlight == null))
      {
        paramDrawable = this.mForegroundInfo;
        if ((paramDrawable == null) || (paramDrawable.mDrawable == null)) {
          this.mPrivateFlags |= 0x80;
        }
      }
      i = 1;
    }
    computeOpaqueFlags();
    if (i != 0) {
      requestLayout();
    }
    this.mBackgroundSizeChanged = true;
    invalidate(true);
    invalidateOutline();
  }
  
  @RemotableViewMethod
  public void setBackgroundResource(int paramInt)
  {
    if ((paramInt != 0) && (paramInt == this.mBackgroundResource)) {
      return;
    }
    Drawable localDrawable = null;
    if (paramInt != 0) {
      localDrawable = this.mContext.getDrawable(paramInt);
    }
    setBackground(localDrawable);
    this.mBackgroundResource = paramInt;
  }
  
  public void setBackgroundTintBlendMode(BlendMode paramBlendMode)
  {
    if (this.mBackgroundTint == null) {
      this.mBackgroundTint = new TintInfo();
    }
    TintInfo localTintInfo = this.mBackgroundTint;
    localTintInfo.mBlendMode = paramBlendMode;
    localTintInfo.mHasTintMode = true;
    applyBackgroundTint();
  }
  
  public void setBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (this.mBackgroundTint == null) {
      this.mBackgroundTint = new TintInfo();
    }
    TintInfo localTintInfo = this.mBackgroundTint;
    localTintInfo.mTintList = paramColorStateList;
    localTintInfo.mHasTintList = true;
    applyBackgroundTint();
  }
  
  public void setBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    BlendMode localBlendMode = null;
    if (paramMode != null) {
      localBlendMode = BlendMode.fromValue(paramMode.nativeInt);
    }
    setBackgroundTintBlendMode(localBlendMode);
  }
  
  public final void setBottom(int paramInt)
  {
    if (paramInt != this.mBottom)
    {
      boolean bool = hasIdentityMatrix();
      if (bool)
      {
        if (this.mAttachInfo != null)
        {
          if (paramInt < this.mBottom) {
            i = this.mBottom;
          } else {
            i = paramInt;
          }
          invalidate(0, 0, this.mRight - this.mLeft, i - this.mTop);
        }
      }
      else {
        invalidate(true);
      }
      int j = this.mRight - this.mLeft;
      int k = this.mBottom;
      int i = this.mTop;
      this.mBottom = paramInt;
      this.mRenderNode.setBottom(this.mBottom);
      sizeChange(j, this.mBottom - this.mTop, j, k - i);
      if (!bool)
      {
        this.mPrivateFlags |= 0x20;
        invalidate(true);
      }
      this.mBackgroundSizeChanged = true;
      this.mDefaultFocusHighlightSizeChanged = true;
      ForegroundInfo localForegroundInfo = this.mForegroundInfo;
      if (localForegroundInfo != null) {
        ForegroundInfo.access$2002(localForegroundInfo, true);
      }
      invalidateParentIfNeeded();
      if ((this.mPrivateFlags2 & 0x10000000) == 268435456) {
        invalidateParentIfNeeded();
      }
    }
  }
  
  public void setCameraDistance(float paramFloat)
  {
    float f = this.mResources.getDisplayMetrics().densityDpi;
    invalidateViewProperty(true, false);
    this.mRenderNode.setCameraDistance(Math.abs(paramFloat) / f);
    invalidateViewProperty(false, false);
    invalidateParentIfNeededAndWasQuickRejected();
  }
  
  public void setClickable(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 16384;
    } else {
      i = 0;
    }
    setFlags(i, 16384);
  }
  
  public void setClipBounds(Rect paramRect)
  {
    Rect localRect = this.mClipBounds;
    if ((paramRect != localRect) && ((paramRect == null) || (!paramRect.equals(localRect))))
    {
      if (paramRect != null)
      {
        localRect = this.mClipBounds;
        if (localRect == null) {
          this.mClipBounds = new Rect(paramRect);
        } else {
          localRect.set(paramRect);
        }
      }
      else
      {
        this.mClipBounds = null;
      }
      this.mRenderNode.setClipRect(this.mClipBounds);
      invalidateViewProperty(false, false);
      return;
    }
  }
  
  public void setClipToOutline(boolean paramBoolean)
  {
    damageInParent();
    if (getClipToOutline() != paramBoolean) {
      this.mRenderNode.setClipToOutline(paramBoolean);
    }
  }
  
  public void setContentCaptureSession(ContentCaptureSession paramContentCaptureSession)
  {
    this.mContentCaptureSession = paramContentCaptureSession;
  }
  
  @RemotableViewMethod
  public void setContentDescription(CharSequence paramCharSequence)
  {
    CharSequence localCharSequence = this.mContentDescription;
    if (localCharSequence == null)
    {
      if (paramCharSequence != null) {}
    }
    else if (localCharSequence.equals(paramCharSequence)) {
      return;
    }
    this.mContentDescription = paramCharSequence;
    int i;
    if ((paramCharSequence != null) && (paramCharSequence.length() > 0)) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i != 0) && (getImportantForAccessibility() == 0))
    {
      setImportantForAccessibility(1);
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
    else
    {
      notifyViewAccessibilityStateChangedIfNeeded(4);
    }
  }
  
  public void setContextClickable(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 8388608;
    } else {
      i = 0;
    }
    setFlags(i, 8388608);
  }
  
  public void setDefaultFocusHighlightEnabled(boolean paramBoolean)
  {
    this.mDefaultFocusHighlightEnabled = paramBoolean;
  }
  
  @UnsupportedAppUsage
  public void setDisabledSystemUiVisibility(int paramInt)
  {
    Object localObject = this.mAttachInfo;
    if ((localObject != null) && (((AttachInfo)localObject).mDisabledSystemUiVisibility != paramInt))
    {
      this.mAttachInfo.mDisabledSystemUiVisibility = paramInt;
      localObject = this.mParent;
      if (localObject != null) {
        ((ViewParent)localObject).recomputeViewAttributes(this);
      }
    }
  }
  
  void setDisplayListProperties(RenderNode paramRenderNode)
  {
    if (paramRenderNode != null)
    {
      paramRenderNode.setHasOverlappingRendering(getHasOverlappingRendering());
      Object localObject = this.mParent;
      boolean bool;
      if (((localObject instanceof ViewGroup)) && (((ViewGroup)localObject).getClipChildren())) {
        bool = true;
      } else {
        bool = false;
      }
      paramRenderNode.setClipToBounds(bool);
      float f1 = 1.0F;
      localObject = this.mParent;
      float f2 = f1;
      if ((localObject instanceof ViewGroup))
      {
        f2 = f1;
        if ((((ViewGroup)localObject).mGroupFlags & 0x800) != 0)
        {
          localObject = (ViewGroup)this.mParent;
          Transformation localTransformation = ((ViewGroup)localObject).getChildTransformation();
          f2 = f1;
          if (((ViewGroup)localObject).getChildStaticTransformation(this, localTransformation))
          {
            int i = localTransformation.getTransformationType();
            f2 = f1;
            if (i != 0)
            {
              if ((i & 0x1) != 0) {
                f1 = localTransformation.getAlpha();
              }
              f2 = f1;
              if ((i & 0x2) != 0)
              {
                paramRenderNode.setStaticMatrix(localTransformation.getMatrix());
                f2 = f1;
              }
            }
          }
        }
      }
      if (this.mTransformationInfo != null)
      {
        f2 *= getFinalAlpha();
        f1 = f2;
        if (f2 < 1.0F)
        {
          f1 = f2;
          if (onSetAlpha((int)(255.0F * f2))) {
            f1 = 1.0F;
          }
        }
        paramRenderNode.setAlpha(f1);
      }
      else if (f2 < 1.0F)
      {
        paramRenderNode.setAlpha(f2);
      }
    }
  }
  
  @Deprecated
  public void setDrawingCacheBackgroundColor(int paramInt)
  {
    if (paramInt != this.mDrawingCacheBackgroundColor)
    {
      this.mDrawingCacheBackgroundColor = paramInt;
      this.mPrivateFlags &= 0xFFFF7FFF;
    }
  }
  
  @Deprecated
  public void setDrawingCacheEnabled(boolean paramBoolean)
  {
    int i = 0;
    this.mCachingFailed = false;
    if (paramBoolean) {
      i = 32768;
    }
    setFlags(i, 32768);
  }
  
  @Deprecated
  public void setDrawingCacheQuality(int paramInt)
  {
    setFlags(paramInt, 1572864);
  }
  
  public void setDuplicateParentStateEnabled(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 4194304;
    } else {
      i = 0;
    }
    setFlags(i, 4194304);
  }
  
  public void setElevation(float paramFloat)
  {
    if (paramFloat != getElevation())
    {
      paramFloat = sanitizeFloatPropertyValue(paramFloat, "elevation");
      invalidateViewProperty(true, false);
      this.mRenderNode.setElevation(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
    }
  }
  
  @RemotableViewMethod
  public void setEnabled(boolean paramBoolean)
  {
    if (paramBoolean == isEnabled()) {
      return;
    }
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 32;
    }
    setFlags(i, 32);
    refreshDrawableState();
    invalidate(true);
    if (!paramBoolean) {
      cancelPendingInputEvents();
    }
  }
  
  public void setFadingEdgeLength(int paramInt)
  {
    initScrollCache();
    this.mScrollCache.fadingEdgeLength = paramInt;
  }
  
  public void setFilterTouchesWhenObscured(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 1024;
    } else {
      i = 0;
    }
    setFlags(i, 1024);
  }
  
  public void setFitsSystemWindows(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 2;
    } else {
      i = 0;
    }
    setFlags(i, 2);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  void setFlags(int paramInt1, int paramInt2)
  {
    boolean bool1 = AccessibilityManager.getInstance(this.mContext).isEnabled();
    int i;
    if ((bool1) && (includeForAccessibility())) {
      i = 1;
    } else {
      i = 0;
    }
    int j = this.mViewFlags;
    this.mViewFlags = (this.mViewFlags & paramInt2 | paramInt1 & paramInt2);
    int k = this.mViewFlags;
    int m = k ^ j;
    if (m == 0) {
      return;
    }
    int n = this.mPrivateFlags;
    boolean bool2 = false;
    int i1 = 0;
    paramInt2 = m;
    int i2 = i1;
    if ((k & 0x10) != 0)
    {
      paramInt2 = m;
      i2 = i1;
      if ((m & 0x4011) != 0)
      {
        if ((k & 0x4000) != 0) {
          paramInt2 = 1;
        } else {
          paramInt2 = 0;
        }
        this.mViewFlags = (this.mViewFlags & 0xFFFFFFFE | paramInt2);
        i2 = j & 0x1 ^ paramInt2 & 0x1;
        paramInt2 = m & 0xFFFFFFFE | i2;
      }
    }
    boolean bool3 = bool2;
    Object localObject;
    if ((paramInt2 & 0x1) != 0)
    {
      bool3 = bool2;
      if ((n & 0x10) != 0) {
        if (((j & 0x1) == 1) && ((n & 0x2) != 0))
        {
          clearFocus();
          localObject = this.mParent;
          bool3 = bool2;
          if ((localObject instanceof ViewGroup))
          {
            ((ViewGroup)localObject).clearFocusedInCluster();
            bool3 = bool2;
          }
        }
        else
        {
          bool3 = bool2;
          if ((j & 0x1) == 0)
          {
            bool3 = bool2;
            if ((n & 0x2) == 0)
            {
              bool3 = bool2;
              if (this.mParent != null)
              {
                localObject = getViewRootImpl();
                if ((sAutoFocusableOffUIThreadWontNotifyParents) && (i2 != 0) && (localObject != null))
                {
                  bool3 = bool2;
                  if (((ViewRootImpl)localObject).mThread != Thread.currentThread()) {}
                }
                else
                {
                  bool3 = canTakeFocus();
                }
              }
            }
          }
        }
      }
    }
    paramInt1 &= 0xC;
    bool2 = bool3;
    if (paramInt1 == 0)
    {
      bool2 = bool3;
      if ((paramInt2 & 0xC) != 0)
      {
        this.mPrivateFlags |= 0x20;
        invalidate(true);
        needGlobalAttributesUpdate(true);
        bool2 = hasSize();
      }
    }
    bool3 = bool2;
    if ((paramInt2 & 0x20) != 0) {
      if ((this.mViewFlags & 0x20) == 0)
      {
        bool3 = canTakeFocus();
      }
      else
      {
        bool3 = bool2;
        if (isFocused())
        {
          clearFocus();
          bool3 = bool2;
        }
      }
    }
    if (bool3)
    {
      localObject = this.mParent;
      if (localObject != null) {
        ((ViewParent)localObject).focusableViewAvailable(this);
      }
    }
    if ((paramInt2 & 0x8) != 0)
    {
      needGlobalAttributesUpdate(false);
      requestLayout();
      if ((this.mViewFlags & 0xC) == 8)
      {
        if (hasFocus())
        {
          clearFocus();
          localObject = this.mParent;
          if ((localObject instanceof ViewGroup)) {
            ((ViewGroup)localObject).clearFocusedInCluster();
          }
        }
        clearAccessibilityFocus();
        destroyDrawingCache();
        localObject = this.mParent;
        if ((localObject instanceof View)) {
          ((View)localObject).invalidate(true);
        }
        this.mPrivateFlags |= 0x20;
      }
      localObject = this.mAttachInfo;
      if (localObject != null) {
        ((AttachInfo)localObject).mViewVisibilityChanged = true;
      }
    }
    if ((paramInt2 & 0x4) != 0)
    {
      needGlobalAttributesUpdate(false);
      this.mPrivateFlags |= 0x20;
      if (((this.mViewFlags & 0xC) == 4) && (getRootView() != this))
      {
        if (hasFocus())
        {
          clearFocus();
          localObject = this.mParent;
          if ((localObject instanceof ViewGroup)) {
            ((ViewGroup)localObject).clearFocusedInCluster();
          }
        }
        clearAccessibilityFocus();
      }
      localObject = this.mAttachInfo;
      if (localObject != null) {
        ((AttachInfo)localObject).mViewVisibilityChanged = true;
      }
    }
    if ((paramInt2 & 0xC) != 0)
    {
      if ((paramInt1 != 0) && (this.mAttachInfo != null)) {
        cleanupDraw();
      }
      localObject = this.mParent;
      if ((localObject instanceof ViewGroup))
      {
        localObject = (ViewGroup)localObject;
        ((ViewGroup)localObject).onChildVisibilityChanged(this, paramInt2 & 0xC, paramInt1);
        ((ViewGroup)localObject).invalidate(true);
      }
      else if (localObject != null)
      {
        ((ViewParent)localObject).invalidateChild(this, null);
      }
      if (this.mAttachInfo != null)
      {
        dispatchVisibilityChanged(this, paramInt1);
        if ((this.mParent != null) && (getWindowVisibility() == 0))
        {
          localObject = this.mParent;
          if ((!(localObject instanceof ViewGroup)) || (((ViewGroup)localObject).isShown()))
          {
            if (paramInt1 == 0) {
              bool3 = true;
            } else {
              bool3 = false;
            }
            dispatchVisibilityAggregated(bool3);
          }
        }
        notifySubtreeAccessibilityStateChangedIfNeeded();
      }
    }
    if ((0x20000 & paramInt2) != 0) {
      destroyDrawingCache();
    }
    if ((0x8000 & paramInt2) != 0)
    {
      destroyDrawingCache();
      this.mPrivateFlags &= 0xFFFF7FFF;
      invalidateParentCaches();
    }
    if ((0x180000 & paramInt2) != 0)
    {
      destroyDrawingCache();
      this.mPrivateFlags &= 0xFFFF7FFF;
    }
    if ((paramInt2 & 0x80) != 0)
    {
      if ((this.mViewFlags & 0x80) != 0)
      {
        if ((this.mBackground == null) && (this.mDefaultFocusHighlight == null))
        {
          localObject = this.mForegroundInfo;
          if ((localObject == null) || (((ForegroundInfo)localObject).mDrawable == null))
          {
            this.mPrivateFlags |= 0x80;
            break label967;
          }
        }
        this.mPrivateFlags &= 0xFF7F;
      }
      else
      {
        this.mPrivateFlags &= 0xFF7F;
      }
      label967:
      requestLayout();
      invalidate(true);
    }
    if (((0x4000000 & paramInt2) != 0) && (this.mParent != null))
    {
      localObject = this.mAttachInfo;
      if ((localObject != null) && (!((AttachInfo)localObject).mRecomputeGlobalAttributes)) {
        this.mParent.recomputeViewAttributes(this);
      }
    }
    if (bool1)
    {
      paramInt1 = paramInt2;
      if (isAccessibilityPane()) {
        paramInt1 = paramInt2 & 0xFFFFFFF3;
      }
      if (((paramInt1 & 0x1) == 0) && ((paramInt1 & 0xC) == 0) && ((paramInt1 & 0x4000) == 0) && ((0x200000 & paramInt1) == 0) && ((0x800000 & paramInt1) == 0))
      {
        if ((paramInt1 & 0x20) != 0) {
          notifyViewAccessibilityStateChangedIfNeeded(0);
        }
      }
      else if (i != includeForAccessibility()) {
        notifySubtreeAccessibilityStateChangedIfNeeded();
      } else {
        notifyViewAccessibilityStateChangedIfNeeded(0);
      }
    }
  }
  
  public void setFocusable(int paramInt)
  {
    if ((paramInt & 0x11) == 0) {
      setFlags(0, 262144);
    }
    setFlags(paramInt, 17);
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    setFocusable(paramBoolean);
  }
  
  public void setFocusableInTouchMode(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 262144;
    } else {
      i = 0;
    }
    setFlags(i, 262144);
    if (paramBoolean) {
      setFlags(1, 17);
    }
  }
  
  public void setFocusedByDefault(boolean paramBoolean)
  {
    boolean bool;
    if ((this.mPrivateFlags3 & 0x40000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    if (paramBoolean == bool) {
      return;
    }
    if (paramBoolean) {
      this.mPrivateFlags3 |= 0x40000;
    } else {
      this.mPrivateFlags3 &= 0xFFFBFFFF;
    }
    ViewParent localViewParent = this.mParent;
    if ((localViewParent instanceof ViewGroup)) {
      if (paramBoolean) {
        ((ViewGroup)localViewParent).setDefaultFocus(this);
      } else {
        ((ViewGroup)localViewParent).clearDefaultFocus(this);
      }
    }
  }
  
  public final void setFocusedInCluster()
  {
    setFocusedInCluster(findKeyboardNavigationCluster());
  }
  
  public void setForceDarkAllowed(boolean paramBoolean)
  {
    if (paramBoolean != this.mForceDark)
    {
      this.mForceDark = paramBoolean;
      this.mRenderNode.setForceDarkAllowed(paramBoolean);
      invalidate();
    }
  }
  
  public void setForeground(Drawable paramDrawable)
  {
    if (this.mForegroundInfo == null)
    {
      if (paramDrawable == null) {
        return;
      }
      this.mForegroundInfo = new ForegroundInfo(null);
    }
    if (paramDrawable == this.mForegroundInfo.mDrawable) {
      return;
    }
    if (this.mForegroundInfo.mDrawable != null)
    {
      if (isAttachedToWindow()) {
        this.mForegroundInfo.mDrawable.setVisible(false, false);
      }
      this.mForegroundInfo.mDrawable.setCallback(null);
      unscheduleDrawable(this.mForegroundInfo.mDrawable);
    }
    ForegroundInfo.access$1402(this.mForegroundInfo, paramDrawable);
    ForegroundInfo localForegroundInfo = this.mForegroundInfo;
    boolean bool = true;
    ForegroundInfo.access$2002(localForegroundInfo, true);
    if (paramDrawable != null)
    {
      int i = this.mPrivateFlags;
      if ((i & 0x80) != 0) {
        this.mPrivateFlags = (i & 0xFF7F);
      }
      paramDrawable.setLayoutDirection(getLayoutDirection());
      if (paramDrawable.isStateful()) {
        paramDrawable.setState(getDrawableState());
      }
      applyForegroundTint();
      if (isAttachedToWindow())
      {
        if ((getWindowVisibility() != 0) || (!isShown())) {
          bool = false;
        }
        paramDrawable.setVisible(bool, false);
      }
      paramDrawable.setCallback(this);
    }
    else if (((this.mViewFlags & 0x80) != 0) && (this.mBackground == null) && (this.mDefaultFocusHighlight == null))
    {
      this.mPrivateFlags |= 0x80;
    }
    requestLayout();
    invalidate();
  }
  
  public void setForegroundGravity(int paramInt)
  {
    if (this.mForegroundInfo == null) {
      this.mForegroundInfo = new ForegroundInfo(null);
    }
    if (this.mForegroundInfo.mGravity != paramInt)
    {
      int i = paramInt;
      if ((0x800007 & paramInt) == 0) {
        i = paramInt | 0x800003;
      }
      paramInt = i;
      if ((i & 0x70) == 0) {
        paramInt = i | 0x30;
      }
      ForegroundInfo.access$2502(this.mForegroundInfo, paramInt);
      requestLayout();
    }
  }
  
  public void setForegroundTintBlendMode(BlendMode paramBlendMode)
  {
    if (this.mForegroundInfo == null) {
      this.mForegroundInfo = new ForegroundInfo(null);
    }
    if (this.mForegroundInfo.mTintInfo == null) {
      ForegroundInfo.access$2602(this.mForegroundInfo, new TintInfo());
    }
    this.mForegroundInfo.mTintInfo.mBlendMode = paramBlendMode;
    this.mForegroundInfo.mTintInfo.mHasTintMode = true;
    applyForegroundTint();
  }
  
  public void setForegroundTintList(ColorStateList paramColorStateList)
  {
    if (this.mForegroundInfo == null) {
      this.mForegroundInfo = new ForegroundInfo(null);
    }
    if (this.mForegroundInfo.mTintInfo == null) {
      ForegroundInfo.access$2602(this.mForegroundInfo, new TintInfo());
    }
    this.mForegroundInfo.mTintInfo.mTintList = paramColorStateList;
    this.mForegroundInfo.mTintInfo.mHasTintList = true;
    applyForegroundTint();
  }
  
  public void setForegroundTintMode(PorterDuff.Mode paramMode)
  {
    BlendMode localBlendMode = null;
    if (paramMode != null) {
      localBlendMode = BlendMode.fromValue(paramMode.nativeInt);
    }
    setForegroundTintBlendMode(localBlendMode);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool1 = false;
    if ((this.mLeft != paramInt1) || (this.mRight != paramInt3) || (this.mTop != paramInt2) || (this.mBottom != paramInt4))
    {
      boolean bool2 = true;
      int i = this.mPrivateFlags;
      int j = this.mRight - this.mLeft;
      int k = this.mBottom - this.mTop;
      int m = paramInt3 - paramInt1;
      int n = paramInt4 - paramInt2;
      if ((m == j) && (n == k)) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      invalidate(bool1);
      this.mLeft = paramInt1;
      this.mTop = paramInt2;
      this.mRight = paramInt3;
      this.mBottom = paramInt4;
      this.mRenderNode.setLeftTopRightBottom(this.mLeft, this.mTop, this.mRight, this.mBottom);
      this.mPrivateFlags |= 0x10;
      if (bool1) {
        sizeChange(m, n, j, k);
      }
      if (((this.mViewFlags & 0xC) == 0) || (this.mGhostView != null))
      {
        this.mPrivateFlags |= 0x20;
        invalidate(bool1);
        invalidateParentCaches();
      }
      this.mPrivateFlags |= i & 0x20;
      this.mBackgroundSizeChanged = true;
      this.mDefaultFocusHighlightSizeChanged = true;
      ForegroundInfo localForegroundInfo = this.mForegroundInfo;
      if (localForegroundInfo != null) {
        ForegroundInfo.access$2002(localForegroundInfo, true);
      }
      notifySubtreeAccessibilityStateChangedIfNeeded();
      bool1 = bool2;
    }
    return bool1;
  }
  
  public void setHapticFeedbackEnabled(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 268435456;
    } else {
      i = 0;
    }
    setFlags(i, 268435456);
    this.mHapticEnabledExplicitly = paramBoolean;
  }
  
  public void setHasTransientState(boolean paramBoolean)
  {
    boolean bool = hasTransientState();
    if (paramBoolean) {
      i = this.mTransientStateCount + 1;
    } else {
      i = this.mTransientStateCount - 1;
    }
    this.mTransientStateCount = i;
    int j = this.mTransientStateCount;
    int i = 0;
    if (j < 0)
    {
      this.mTransientStateCount = 0;
      Log.e("View", "hasTransientState decremented below 0: unmatched pair of setHasTransientState calls");
    }
    else if (((paramBoolean) && (j == 1)) || ((!paramBoolean) && (this.mTransientStateCount == 0)))
    {
      j = this.mPrivateFlags2;
      if (paramBoolean) {
        i = Integer.MIN_VALUE;
      }
      this.mPrivateFlags2 = (j & 0x7FFFFFFF | i);
      paramBoolean = hasTransientState();
      ViewParent localViewParent = this.mParent;
      if ((localViewParent != null) && (paramBoolean != bool)) {
        try
        {
          localViewParent.childHasTransientStateChanged(this, paramBoolean);
        }
        catch (AbstractMethodError localAbstractMethodError)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append(this.mParent.getClass().getSimpleName());
          localStringBuilder.append(" does not fully implement ViewParent");
          Log.e("View", localStringBuilder.toString(), localAbstractMethodError);
        }
      }
    }
  }
  
  public void setHorizontalFadingEdgeEnabled(boolean paramBoolean)
  {
    if (isHorizontalFadingEdgeEnabled() != paramBoolean)
    {
      if (paramBoolean) {
        initScrollCache();
      }
      this.mViewFlags ^= 0x1000;
    }
  }
  
  public void setHorizontalScrollBarEnabled(boolean paramBoolean)
  {
    if (isHorizontalScrollBarEnabled() != paramBoolean)
    {
      this.mViewFlags ^= 0x100;
      computeOpaqueFlags();
      resolvePadding();
    }
  }
  
  public void setHorizontalScrollbarThumbDrawable(Drawable paramDrawable)
  {
    initializeScrollBarDrawable();
    this.mScrollCache.scrollBar.setHorizontalThumbDrawable(paramDrawable);
  }
  
  public void setHorizontalScrollbarTrackDrawable(Drawable paramDrawable)
  {
    initializeScrollBarDrawable();
    this.mScrollCache.scrollBar.setHorizontalTrackDrawable(paramDrawable);
  }
  
  public void setHovered(boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = this.mPrivateFlags;
      if ((i & 0x10000000) == 0)
      {
        this.mPrivateFlags = (0x10000000 | i);
        refreshDrawableState();
        onHoverChanged(true);
      }
    }
    else
    {
      i = this.mPrivateFlags;
      if ((0x10000000 & i) != 0)
      {
        this.mPrivateFlags = (0xEFFFFFFF & i);
        refreshDrawableState();
        onHoverChanged(false);
      }
    }
  }
  
  public void setId(int paramInt)
  {
    this.mID = paramInt;
    if ((this.mID == -1) && (this.mLabelForId != -1)) {
      this.mID = generateViewId();
    }
  }
  
  public void setImportantForAccessibility(int paramInt)
  {
    int i = getImportantForAccessibility();
    if (paramInt != i)
    {
      boolean bool1 = true;
      boolean bool2;
      if (paramInt == 4) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      if ((paramInt == 2) || (bool2))
      {
        View localView = findAccessibilityFocusHost(bool2);
        if (localView != null) {
          localView.clearAccessibilityFocus();
        }
      }
      if ((i != 0) && (paramInt != 0)) {
        i = 0;
      } else {
        i = 1;
      }
      if ((i != 0) && (includeForAccessibility())) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      this.mPrivateFlags2 &= 0xFF8FFFFF;
      this.mPrivateFlags2 |= paramInt << 20 & 0x700000;
      if ((i != 0) && (bool2 == includeForAccessibility())) {
        notifyViewAccessibilityStateChangedIfNeeded(0);
      } else {
        notifySubtreeAccessibilityStateChangedIfNeeded();
      }
    }
  }
  
  public void setImportantForAutofill(int paramInt)
  {
    this.mPrivateFlags3 &= 0xFF87FFFF;
    this.mPrivateFlags3 |= paramInt << 19 & 0x780000;
  }
  
  public void setIsRootNamespace(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mPrivateFlags |= 0x8;
    } else {
      this.mPrivateFlags &= 0xFFFFFFF7;
    }
  }
  
  public void setKeepScreenOn(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 67108864;
    } else {
      i = 0;
    }
    setFlags(i, 67108864);
  }
  
  public void setKeyboardNavigationCluster(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mPrivateFlags3 |= 0x8000;
    } else {
      this.mPrivateFlags3 &= 0xFFFF7FFF;
    }
  }
  
  @RemotableViewMethod
  public void setLabelFor(int paramInt)
  {
    if (this.mLabelForId == paramInt) {
      return;
    }
    this.mLabelForId = paramInt;
    if ((this.mLabelForId != -1) && (this.mID == -1)) {
      this.mID = generateViewId();
    }
    notifyViewAccessibilityStateChangedIfNeeded(0);
  }
  
  public void setLayerPaint(Paint paramPaint)
  {
    int i = getLayerType();
    if (i != 0)
    {
      this.mLayerPaint = paramPaint;
      if (i == 2)
      {
        if (this.mRenderNode.setLayerPaint(paramPaint)) {
          invalidateViewProperty(false, false);
        }
      }
      else {
        invalidate();
      }
    }
  }
  
  public void setLayerType(int paramInt, Paint paramPaint)
  {
    if ((paramInt >= 0) && (paramInt <= 2))
    {
      if (!this.mRenderNode.setLayerType(paramInt))
      {
        setLayerPaint(paramPaint);
        return;
      }
      if (paramInt != 1) {
        destroyDrawingCache();
      }
      this.mLayerType = paramInt;
      if (this.mLayerType == 0) {
        paramPaint = null;
      }
      this.mLayerPaint = paramPaint;
      this.mRenderNode.setLayerPaint(this.mLayerPaint);
      invalidateParentCaches();
      invalidate(true);
      return;
    }
    throw new IllegalArgumentException("Layer type can only be one of: LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE");
  }
  
  @RemotableViewMethod
  public void setLayoutDirection(int paramInt)
  {
    if (getRawLayoutDirection() != paramInt)
    {
      this.mPrivateFlags2 &= 0xFFFFFFF3;
      resetRtlProperties();
      this.mPrivateFlags2 |= paramInt << 2 & 0xC;
      resolveRtlPropertiesIfNeeded();
      requestLayout();
      invalidate(true);
    }
  }
  
  public void setLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams != null)
    {
      this.mLayoutParams = paramLayoutParams;
      resolveLayoutParams();
      ViewParent localViewParent = this.mParent;
      if ((localViewParent instanceof ViewGroup)) {
        ((ViewGroup)localViewParent).onSetLayoutParams(this, paramLayoutParams);
      }
      requestLayout();
      return;
    }
    throw new NullPointerException("Layout parameters cannot be null");
  }
  
  public final void setLeft(int paramInt)
  {
    if (paramInt != this.mLeft)
    {
      boolean bool = hasIdentityMatrix();
      if (bool)
      {
        if (this.mAttachInfo != null)
        {
          i = this.mLeft;
          if (paramInt < i)
          {
            j = paramInt;
            i = paramInt - i;
          }
          else
          {
            j = this.mLeft;
            i = 0;
          }
          invalidate(i, 0, this.mRight - j, this.mBottom - this.mTop);
        }
      }
      else {
        invalidate(true);
      }
      int i = this.mRight;
      int k = this.mLeft;
      int j = this.mBottom - this.mTop;
      this.mLeft = paramInt;
      this.mRenderNode.setLeft(paramInt);
      sizeChange(this.mRight - this.mLeft, j, i - k, j);
      if (!bool)
      {
        this.mPrivateFlags |= 0x20;
        invalidate(true);
      }
      this.mBackgroundSizeChanged = true;
      this.mDefaultFocusHighlightSizeChanged = true;
      ForegroundInfo localForegroundInfo = this.mForegroundInfo;
      if (localForegroundInfo != null) {
        ForegroundInfo.access$2002(localForegroundInfo, true);
      }
      invalidateParentIfNeeded();
      if ((this.mPrivateFlags2 & 0x10000000) == 268435456) {
        invalidateParentIfNeeded();
      }
    }
  }
  
  public final void setLeftTopRightBottom(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setLongClickable(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 2097152;
    } else {
      i = 0;
    }
    setFlags(i, 2097152);
  }
  
  protected final void setMeasuredDimension(int paramInt1, int paramInt2)
  {
    boolean bool = isLayoutModeOptical(this);
    int i = paramInt1;
    int j = paramInt2;
    if (bool != isLayoutModeOptical(this.mParent))
    {
      Insets localInsets = getOpticalInsets();
      j = localInsets.left + localInsets.right;
      int k = localInsets.top + localInsets.bottom;
      if (!bool) {
        j = -j;
      }
      i = paramInt1 + j;
      if (bool) {
        paramInt1 = k;
      } else {
        paramInt1 = -k;
      }
      j = paramInt2 + paramInt1;
    }
    setMeasuredDimensionRaw(i, j);
  }
  
  @RemotableViewMethod
  public void setMinimumHeight(int paramInt)
  {
    this.mMinHeight = paramInt;
    requestLayout();
  }
  
  public void setMinimumWidth(int paramInt)
  {
    this.mMinWidth = paramInt;
    requestLayout();
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mPrivateFlags3 |= 0x80;
    }
    else
    {
      stopNestedScroll();
      this.mPrivateFlags3 &= 0xFF7F;
    }
  }
  
  public void setNextClusterForwardId(int paramInt)
  {
    this.mNextClusterForwardId = paramInt;
  }
  
  public void setNextFocusDownId(int paramInt)
  {
    this.mNextFocusDownId = paramInt;
  }
  
  public void setNextFocusForwardId(int paramInt)
  {
    this.mNextFocusForwardId = paramInt;
  }
  
  public void setNextFocusLeftId(int paramInt)
  {
    this.mNextFocusLeftId = paramInt;
  }
  
  public void setNextFocusRightId(int paramInt)
  {
    this.mNextFocusRightId = paramInt;
  }
  
  public void setNextFocusUpId(int paramInt)
  {
    this.mNextFocusUpId = paramInt;
  }
  
  public void setNotifyAutofillManagerOnClick(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mPrivateFlags |= 0x20000000;
    } else {
      this.mPrivateFlags &= 0xDFFFFFFF;
    }
  }
  
  public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
  {
    getListenerInfo().mOnApplyWindowInsetsListener = paramOnApplyWindowInsetsListener;
  }
  
  public void setOnCapturedPointerListener(OnCapturedPointerListener paramOnCapturedPointerListener)
  {
    getListenerInfo().mOnCapturedPointerListener = paramOnCapturedPointerListener;
  }
  
  public void setOnClickListener(OnClickListener paramOnClickListener)
  {
    if (!isClickable()) {
      setClickable(true);
    }
    getListenerInfo().mOnClickListener = paramOnClickListener;
  }
  
  public void setOnContextClickListener(OnContextClickListener paramOnContextClickListener)
  {
    if (!isContextClickable()) {
      setContextClickable(true);
    }
    getListenerInfo().mOnContextClickListener = paramOnContextClickListener;
  }
  
  public void setOnCreateContextMenuListener(OnCreateContextMenuListener paramOnCreateContextMenuListener)
  {
    if (!isLongClickable()) {
      setLongClickable(true);
    }
    getListenerInfo().mOnCreateContextMenuListener = paramOnCreateContextMenuListener;
  }
  
  public void setOnDragListener(OnDragListener paramOnDragListener)
  {
    ListenerInfo.access$802(getListenerInfo(), paramOnDragListener);
  }
  
  public void setOnFocusChangeListener(OnFocusChangeListener paramOnFocusChangeListener)
  {
    getListenerInfo().mOnFocusChangeListener = paramOnFocusChangeListener;
  }
  
  public void setOnGenericMotionListener(OnGenericMotionListener paramOnGenericMotionListener)
  {
    ListenerInfo.access$602(getListenerInfo(), paramOnGenericMotionListener);
  }
  
  public void setOnHoverListener(OnHoverListener paramOnHoverListener)
  {
    ListenerInfo.access$702(getListenerInfo(), paramOnHoverListener);
  }
  
  public void setOnKeyListener(OnKeyListener paramOnKeyListener)
  {
    ListenerInfo.access$402(getListenerInfo(), paramOnKeyListener);
  }
  
  public void setOnLongClickListener(OnLongClickListener paramOnLongClickListener)
  {
    if (!isLongClickable()) {
      setLongClickable(true);
    }
    getListenerInfo().mOnLongClickListener = paramOnLongClickListener;
  }
  
  public void setOnScrollChangeListener(OnScrollChangeListener paramOnScrollChangeListener)
  {
    getListenerInfo().mOnScrollChangeListener = paramOnScrollChangeListener;
  }
  
  public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener paramOnSystemUiVisibilityChangeListener)
  {
    ListenerInfo.access$1602(getListenerInfo(), paramOnSystemUiVisibilityChangeListener);
    if (this.mParent != null)
    {
      paramOnSystemUiVisibilityChangeListener = this.mAttachInfo;
      if ((paramOnSystemUiVisibilityChangeListener != null) && (!paramOnSystemUiVisibilityChangeListener.mRecomputeGlobalAttributes)) {
        this.mParent.recomputeViewAttributes(this);
      }
    }
  }
  
  public void setOnTouchListener(OnTouchListener paramOnTouchListener)
  {
    ListenerInfo.access$502(getListenerInfo(), paramOnTouchListener);
  }
  
  public void setOpticalInsets(Insets paramInsets)
  {
    this.mLayoutInsets = paramInsets;
  }
  
  public void setOutlineAmbientShadowColor(int paramInt)
  {
    if (this.mRenderNode.setAmbientShadowColor(paramInt)) {
      invalidateViewProperty(true, true);
    }
  }
  
  public void setOutlineProvider(ViewOutlineProvider paramViewOutlineProvider)
  {
    this.mOutlineProvider = paramViewOutlineProvider;
    invalidateOutline();
  }
  
  public void setOutlineSpotShadowColor(int paramInt)
  {
    if (this.mRenderNode.setSpotShadowColor(paramInt)) {
      invalidateViewProperty(true, true);
    }
  }
  
  public void setOverScrollMode(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid overscroll mode ");
      localStringBuilder.append(paramInt);
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    this.mOverScrollMode = paramInt;
  }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    resetResolvedPaddingInternal();
    this.mUserPaddingStart = Integer.MIN_VALUE;
    this.mUserPaddingEnd = Integer.MIN_VALUE;
    this.mUserPaddingLeftInitial = paramInt1;
    this.mUserPaddingRightInitial = paramInt3;
    this.mLeftPaddingDefined = true;
    this.mRightPaddingDefined = true;
    internalSetPadding(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setPaddingRelative(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    resetResolvedPaddingInternal();
    this.mUserPaddingStart = paramInt1;
    this.mUserPaddingEnd = paramInt3;
    this.mLeftPaddingDefined = true;
    this.mRightPaddingDefined = true;
    if (getLayoutDirection() != 1)
    {
      this.mUserPaddingLeftInitial = paramInt1;
      this.mUserPaddingRightInitial = paramInt3;
      internalSetPadding(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    else
    {
      this.mUserPaddingLeftInitial = paramInt3;
      this.mUserPaddingRightInitial = paramInt1;
      internalSetPadding(paramInt3, paramInt2, paramInt1, paramInt4);
    }
  }
  
  public void setPivotX(float paramFloat)
  {
    if ((!this.mRenderNode.isPivotExplicitlySet()) || (paramFloat != getPivotX()))
    {
      invalidateViewProperty(true, false);
      this.mRenderNode.setPivotX(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
    }
  }
  
  public void setPivotY(float paramFloat)
  {
    if ((!this.mRenderNode.isPivotExplicitlySet()) || (paramFloat != getPivotY()))
    {
      invalidateViewProperty(true, false);
      this.mRenderNode.setPivotY(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
    }
  }
  
  public void setPointerIcon(PointerIcon paramPointerIcon)
  {
    this.mPointerIcon = paramPointerIcon;
    paramPointerIcon = this.mAttachInfo;
    if ((paramPointerIcon != null) && (!paramPointerIcon.mHandlingPointerEvent))
    {
      try
      {
        this.mAttachInfo.mSession.updatePointerIcon(this.mAttachInfo.mWindow);
      }
      catch (RemoteException paramPointerIcon) {}
      return;
    }
  }
  
  public void setPressed(boolean paramBoolean)
  {
    int i = this.mPrivateFlags;
    int j = 1;
    boolean bool;
    if ((i & 0x4000) == 16384) {
      bool = true;
    } else {
      bool = false;
    }
    if (paramBoolean == bool) {
      j = 0;
    }
    if (paramBoolean) {
      this.mPrivateFlags = (0x4000 | this.mPrivateFlags);
    } else {
      this.mPrivateFlags &= 0xBFFF;
    }
    if (j != 0) {
      refreshDrawableState();
    }
    dispatchSetPressed(paramBoolean);
  }
  
  public void setRevealClip(boolean paramBoolean, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.mRenderNode.setRevealClip(paramBoolean, paramFloat1, paramFloat2, paramFloat3);
    invalidateViewProperty(false, false);
  }
  
  public final void setRevealOnFocusHint(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mPrivateFlags3 &= 0xFBFFFFFF;
    } else {
      this.mPrivateFlags3 |= 0x4000000;
    }
  }
  
  public final void setRight(int paramInt)
  {
    if (paramInt != this.mRight)
    {
      boolean bool = hasIdentityMatrix();
      if (bool)
      {
        if (this.mAttachInfo != null)
        {
          if (paramInt < this.mRight) {
            i = this.mRight;
          } else {
            i = paramInt;
          }
          invalidate(0, 0, i - this.mLeft, this.mBottom - this.mTop);
        }
      }
      else {
        invalidate(true);
      }
      int j = this.mRight;
      int k = this.mLeft;
      int i = this.mBottom - this.mTop;
      this.mRight = paramInt;
      this.mRenderNode.setRight(this.mRight);
      sizeChange(this.mRight - this.mLeft, i, j - k, i);
      if (!bool)
      {
        this.mPrivateFlags |= 0x20;
        invalidate(true);
      }
      this.mBackgroundSizeChanged = true;
      this.mDefaultFocusHighlightSizeChanged = true;
      ForegroundInfo localForegroundInfo = this.mForegroundInfo;
      if (localForegroundInfo != null) {
        ForegroundInfo.access$2002(localForegroundInfo, true);
      }
      invalidateParentIfNeeded();
      if ((this.mPrivateFlags2 & 0x10000000) == 268435456) {
        invalidateParentIfNeeded();
      }
    }
  }
  
  public void setRotation(float paramFloat)
  {
    if (paramFloat != getRotation())
    {
      invalidateViewProperty(true, false);
      this.mRenderNode.setRotationZ(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void setRotationX(float paramFloat)
  {
    if (paramFloat != getRotationX())
    {
      invalidateViewProperty(true, false);
      this.mRenderNode.setRotationX(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void setRotationY(float paramFloat)
  {
    if (paramFloat != getRotationY())
    {
      invalidateViewProperty(true, false);
      this.mRenderNode.setRotationY(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void setSaveEnabled(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 65536;
    }
    setFlags(i, 65536);
  }
  
  public void setSaveFromParentEnabled(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 536870912;
    }
    setFlags(i, 536870912);
  }
  
  public void setScaleX(float paramFloat)
  {
    if (paramFloat != getScaleX())
    {
      paramFloat = sanitizeFloatPropertyValue(paramFloat, "scaleX");
      invalidateViewProperty(true, false);
      this.mRenderNode.setScaleX(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void setScaleY(float paramFloat)
  {
    if (paramFloat != getScaleY())
    {
      paramFloat = sanitizeFloatPropertyValue(paramFloat, "scaleY");
      invalidateViewProperty(true, false);
      this.mRenderNode.setScaleY(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void setScreenReaderFocusable(boolean paramBoolean)
  {
    updatePflags3AndNotifyA11yIfChanged(268435456, paramBoolean);
  }
  
  public void setScrollBarDefaultDelayBeforeFade(int paramInt)
  {
    getScrollCache().scrollBarDefaultDelayBeforeFade = paramInt;
  }
  
  public void setScrollBarFadeDuration(int paramInt)
  {
    getScrollCache().scrollBarFadeDuration = paramInt;
  }
  
  public void setScrollBarSize(int paramInt)
  {
    getScrollCache().scrollBarSize = paramInt;
  }
  
  public void setScrollBarStyle(int paramInt)
  {
    int i = this.mViewFlags;
    if (paramInt != (i & 0x3000000))
    {
      this.mViewFlags = (i & 0xFCFFFFFF | 0x3000000 & paramInt);
      computeOpaqueFlags();
      resolvePadding();
    }
  }
  
  public void setScrollContainer(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      AttachInfo localAttachInfo = this.mAttachInfo;
      if ((localAttachInfo != null) && ((this.mPrivateFlags & 0x100000) == 0))
      {
        localAttachInfo.mScrollContainers.add(this);
        this.mPrivateFlags = (0x100000 | this.mPrivateFlags);
      }
      this.mPrivateFlags |= 0x80000;
    }
    else
    {
      if ((0x100000 & this.mPrivateFlags) != 0) {
        this.mAttachInfo.mScrollContainers.remove(this);
      }
      this.mPrivateFlags &= 0xFFE7FFFF;
    }
  }
  
  public void setScrollIndicators(int paramInt)
  {
    setScrollIndicators(paramInt, 63);
  }
  
  public void setScrollIndicators(int paramInt1, int paramInt2)
  {
    int i = paramInt2 << 8 & 0x3F00;
    paramInt1 = paramInt1 << 8 & i;
    paramInt2 = this.mPrivateFlags3;
    i = i & paramInt2 | paramInt1;
    if (paramInt2 != i)
    {
      this.mPrivateFlags3 = i;
      if (paramInt1 != 0) {
        initializeScrollIndicatorsInternal();
      }
      invalidate();
    }
  }
  
  public void setScrollX(int paramInt)
  {
    scrollTo(paramInt, this.mScrollY);
  }
  
  public void setScrollY(int paramInt)
  {
    scrollTo(this.mScrollX, paramInt);
  }
  
  public void setScrollbarFadingEnabled(boolean paramBoolean)
  {
    initScrollCache();
    ScrollabilityCache localScrollabilityCache = this.mScrollCache;
    localScrollabilityCache.fadeScrollBars = paramBoolean;
    if (paramBoolean) {
      localScrollabilityCache.state = 0;
    } else {
      localScrollabilityCache.state = 1;
    }
  }
  
  public void setSelected(boolean paramBoolean)
  {
    boolean bool;
    if ((this.mPrivateFlags & 0x4) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    if (bool != paramBoolean)
    {
      int i = this.mPrivateFlags;
      int j;
      if (paramBoolean) {
        j = 4;
      } else {
        j = 0;
      }
      this.mPrivateFlags = (i & 0xFFFFFFFB | j);
      if (!paramBoolean) {
        resetPressedState();
      }
      invalidate(true);
      refreshDrawableState();
      dispatchSetSelected(paramBoolean);
      if (paramBoolean) {
        sendAccessibilityEvent(4);
      } else {
        notifyViewAccessibilityStateChangedIfNeeded(0);
      }
    }
  }
  
  public void setSoundEffectsEnabled(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 134217728;
    } else {
      i = 0;
    }
    setFlags(i, 134217728);
  }
  
  public void setStateListAnimator(StateListAnimator paramStateListAnimator)
  {
    StateListAnimator localStateListAnimator = this.mStateListAnimator;
    if (localStateListAnimator == paramStateListAnimator) {
      return;
    }
    if (localStateListAnimator != null) {
      localStateListAnimator.setTarget(null);
    }
    this.mStateListAnimator = paramStateListAnimator;
    if (paramStateListAnimator != null)
    {
      paramStateListAnimator.setTarget(this);
      if (isAttachedToWindow()) {
        paramStateListAnimator.setState(getDrawableState());
      }
    }
  }
  
  public void setSystemGestureExclusionRects(List<Rect> paramList)
  {
    if ((paramList.isEmpty()) && (this.mListenerInfo == null)) {
      return;
    }
    ListenerInfo localListenerInfo = getListenerInfo();
    if (paramList.isEmpty())
    {
      ListenerInfo.access$1202(localListenerInfo, null);
      if (localListenerInfo.mPositionUpdateListener != null) {
        this.mRenderNode.removePositionUpdateListener(localListenerInfo.mPositionUpdateListener);
      }
    }
    else
    {
      ListenerInfo.access$1202(localListenerInfo, paramList);
      if (localListenerInfo.mPositionUpdateListener == null)
      {
        localListenerInfo.mPositionUpdateListener = new RenderNode.PositionUpdateListener()
        {
          public void positionChanged(long paramAnonymousLong, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
          {
            View.this.postUpdateSystemGestureExclusionRects();
          }
          
          public void positionLost(long paramAnonymousLong)
          {
            View.this.postUpdateSystemGestureExclusionRects();
          }
        };
        this.mRenderNode.addPositionUpdateListener(localListenerInfo.mPositionUpdateListener);
      }
    }
    postUpdateSystemGestureExclusionRects();
  }
  
  public void setSystemUiVisibility(int paramInt)
  {
    if (paramInt != this.mSystemUiVisibility)
    {
      this.mSystemUiVisibility = paramInt;
      if (this.mParent != null)
      {
        AttachInfo localAttachInfo = this.mAttachInfo;
        if ((localAttachInfo != null) && (!localAttachInfo.mRecomputeGlobalAttributes)) {
          this.mParent.recomputeViewAttributes(this);
        }
      }
    }
  }
  
  public void setTag(int paramInt, Object paramObject)
  {
    if (paramInt >>> 24 >= 2)
    {
      setKeyedTag(paramInt, paramObject);
      return;
    }
    throw new IllegalArgumentException("The key must be an application-specific resource id.");
  }
  
  public void setTag(Object paramObject)
  {
    this.mTag = paramObject;
  }
  
  @UnsupportedAppUsage
  public void setTagInternal(int paramInt, Object paramObject)
  {
    if (paramInt >>> 24 == 1)
    {
      setKeyedTag(paramInt, paramObject);
      return;
    }
    throw new IllegalArgumentException("The key must be a framework-specific resource id.");
  }
  
  public void setTextAlignment(int paramInt)
  {
    if (paramInt != getRawTextAlignment())
    {
      this.mPrivateFlags2 &= 0xFFFF1FFF;
      resetResolvedTextAlignment();
      this.mPrivateFlags2 |= paramInt << 13 & 0xE000;
      resolveTextAlignment();
      onRtlPropertiesChanged(getLayoutDirection());
      requestLayout();
      invalidate(true);
    }
  }
  
  public void setTextDirection(int paramInt)
  {
    if (getRawTextDirection() != paramInt)
    {
      this.mPrivateFlags2 &= 0xFE3F;
      resetResolvedTextDirection();
      this.mPrivateFlags2 |= paramInt << 6 & 0x1C0;
      resolveTextDirection();
      onRtlPropertiesChanged(getLayoutDirection());
      requestLayout();
      invalidate(true);
    }
  }
  
  @UnsupportedAppUsage
  public void setTooltip(CharSequence paramCharSequence)
  {
    setTooltipText(paramCharSequence);
  }
  
  public void setTooltipText(CharSequence paramCharSequence)
  {
    if (TextUtils.isEmpty(paramCharSequence))
    {
      setFlags(0, 1073741824);
      hideTooltip();
      this.mTooltipInfo = null;
    }
    else
    {
      setFlags(1073741824, 1073741824);
      if (this.mTooltipInfo == null)
      {
        this.mTooltipInfo = new TooltipInfo(null);
        TooltipInfo localTooltipInfo = this.mTooltipInfo;
        localTooltipInfo.mShowTooltipRunnable = new _..Lambda.View.llq76MkPXP4bNcb9oJt_msw0fnQ(this);
        localTooltipInfo.mHideTooltipRunnable = new _..Lambda.QI1s392qW8l6mC24bcy9050SkuY(this);
        localTooltipInfo.mHoverSlop = ViewConfiguration.get(this.mContext).getScaledHoverSlop();
        this.mTooltipInfo.clearAnchorPos();
      }
      this.mTooltipInfo.mTooltipText = paramCharSequence;
    }
  }
  
  public final void setTop(int paramInt)
  {
    if (paramInt != this.mTop)
    {
      boolean bool = hasIdentityMatrix();
      if (bool)
      {
        if (this.mAttachInfo != null)
        {
          i = this.mTop;
          if (paramInt < i)
          {
            j = paramInt;
            i = paramInt - i;
          }
          else
          {
            j = this.mTop;
            i = 0;
          }
          invalidate(0, i, this.mRight - this.mLeft, this.mBottom - j);
        }
      }
      else {
        invalidate(true);
      }
      int i = this.mRight - this.mLeft;
      int k = this.mBottom;
      int j = this.mTop;
      this.mTop = paramInt;
      this.mRenderNode.setTop(this.mTop);
      sizeChange(i, this.mBottom - this.mTop, i, k - j);
      if (!bool)
      {
        this.mPrivateFlags |= 0x20;
        invalidate(true);
      }
      this.mBackgroundSizeChanged = true;
      this.mDefaultFocusHighlightSizeChanged = true;
      ForegroundInfo localForegroundInfo = this.mForegroundInfo;
      if (localForegroundInfo != null) {
        ForegroundInfo.access$2002(localForegroundInfo, true);
      }
      invalidateParentIfNeeded();
      if ((this.mPrivateFlags2 & 0x10000000) == 268435456) {
        invalidateParentIfNeeded();
      }
    }
  }
  
  public void setTouchDelegate(TouchDelegate paramTouchDelegate)
  {
    this.mTouchDelegate = paramTouchDelegate;
  }
  
  public void setTransitionAlpha(float paramFloat)
  {
    ensureTransformationInfo();
    if (this.mTransformationInfo.mTransitionAlpha != paramFloat)
    {
      this.mTransformationInfo.mTransitionAlpha = paramFloat;
      this.mPrivateFlags &= 0xFFFBFFFF;
      invalidateViewProperty(true, false);
      this.mRenderNode.setAlpha(getFinalAlpha());
    }
  }
  
  public final void setTransitionName(String paramString)
  {
    this.mTransitionName = paramString;
  }
  
  public void setTransitionVisibility(int paramInt)
  {
    this.mViewFlags = (this.mViewFlags & 0xFFFFFFF3 | paramInt);
  }
  
  public void setTranslationX(float paramFloat)
  {
    if (paramFloat != getTranslationX())
    {
      invalidateViewProperty(true, false);
      this.mRenderNode.setTranslationX(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void setTranslationY(float paramFloat)
  {
    if (paramFloat != getTranslationY())
    {
      invalidateViewProperty(true, false);
      this.mRenderNode.setTranslationY(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
      notifySubtreeAccessibilityStateChangedIfNeeded();
    }
  }
  
  public void setTranslationZ(float paramFloat)
  {
    if (paramFloat != getTranslationZ())
    {
      paramFloat = sanitizeFloatPropertyValue(paramFloat, "translationZ");
      invalidateViewProperty(true, false);
      this.mRenderNode.setTranslationZ(paramFloat);
      invalidateViewProperty(false, true);
      invalidateParentIfNeededAndWasQuickRejected();
    }
  }
  
  public void setVerticalFadingEdgeEnabled(boolean paramBoolean)
  {
    if (isVerticalFadingEdgeEnabled() != paramBoolean)
    {
      if (paramBoolean) {
        initScrollCache();
      }
      this.mViewFlags ^= 0x2000;
    }
  }
  
  public void setVerticalScrollBarEnabled(boolean paramBoolean)
  {
    if (isVerticalScrollBarEnabled() != paramBoolean)
    {
      this.mViewFlags ^= 0x200;
      computeOpaqueFlags();
      resolvePadding();
    }
  }
  
  public void setVerticalScrollbarPosition(int paramInt)
  {
    if (this.mVerticalScrollbarPosition != paramInt)
    {
      this.mVerticalScrollbarPosition = paramInt;
      computeOpaqueFlags();
      resolvePadding();
    }
  }
  
  public void setVerticalScrollbarThumbDrawable(Drawable paramDrawable)
  {
    initializeScrollBarDrawable();
    this.mScrollCache.scrollBar.setVerticalThumbDrawable(paramDrawable);
  }
  
  public void setVerticalScrollbarTrackDrawable(Drawable paramDrawable)
  {
    initializeScrollBarDrawable();
    this.mScrollCache.scrollBar.setVerticalTrackDrawable(paramDrawable);
  }
  
  @RemotableViewMethod
  public void setVisibility(int paramInt)
  {
    if (MiuiMultiWindowAdapter.isPrevent(this, paramInt, this.mContext.getPackageName(), this.mResources.getConfiguration().windowConfiguration.getWindowingMode())) {
      return;
    }
    setFlags(paramInt, 12);
  }
  
  @Deprecated
  public void setWillNotCacheDrawing(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 131072;
    } else {
      i = 0;
    }
    setFlags(i, 131072);
  }
  
  public void setWillNotDraw(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 128;
    } else {
      i = 0;
    }
    setFlags(i, 128);
  }
  
  public void setWindowInsetsAnimationListener(WindowInsetsAnimationListener paramWindowInsetsAnimationListener)
  {
    ListenerInfo.access$1102(getListenerInfo(), paramWindowInsetsAnimationListener);
  }
  
  public void setX(float paramFloat)
  {
    setTranslationX(paramFloat - this.mLeft);
  }
  
  public void setY(float paramFloat)
  {
    setTranslationY(paramFloat - this.mTop);
  }
  
  public void setZ(float paramFloat)
  {
    setTranslationZ(paramFloat - getElevation());
  }
  
  boolean shouldDrawRoundScrollbar()
  {
    boolean bool1 = this.mResources.getConfiguration().isScreenRound();
    boolean bool2 = false;
    if ((bool1) && (this.mAttachInfo != null))
    {
      View localView = getRootView();
      WindowInsets localWindowInsets = getRootWindowInsets();
      int i = getHeight();
      int j = getWidth();
      int k = localView.getHeight();
      int m = localView.getWidth();
      if ((i == k) && (j == m))
      {
        getLocationInWindow(this.mAttachInfo.mTmpLocation);
        if ((this.mAttachInfo.mTmpLocation[0] == localWindowInsets.getStableInsetLeft()) && (this.mAttachInfo.mTmpLocation[1] == localWindowInsets.getStableInsetTop())) {
          bool2 = true;
        }
        return bool2;
      }
      return false;
    }
    return false;
  }
  
  public boolean showContextMenu()
  {
    return getParent().showContextMenuForChild(this);
  }
  
  public boolean showContextMenu(float paramFloat1, float paramFloat2)
  {
    return getParent().showContextMenuForChild(this, paramFloat1, paramFloat2);
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback)
  {
    return startActionMode(paramCallback, 0);
  }
  
  public ActionMode startActionMode(ActionMode.Callback paramCallback, int paramInt)
  {
    ViewParent localViewParent = getParent();
    if (localViewParent == null) {
      return null;
    }
    try
    {
      ActionMode localActionMode = localViewParent.startActionModeForChild(this, paramCallback, paramInt);
      return localActionMode;
    }
    catch (AbstractMethodError localAbstractMethodError) {}
    return localViewParent.startActionModeForChild(this, paramCallback);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("@android:view:");
    localStringBuilder.append(System.identityHashCode(this));
    this.mStartActivityRequestWho = localStringBuilder.toString();
    getContext().startActivityForResult(this.mStartActivityRequestWho, paramIntent, paramInt, null);
  }
  
  public void startAnimation(Animation paramAnimation)
  {
    paramAnimation.setStartTime(-1L);
    setAnimation(paramAnimation);
    invalidateParentCaches();
    invalidate(true);
  }
  
  @Deprecated
  public final boolean startDrag(ClipData paramClipData, DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt)
  {
    return startDragAndDrop(paramClipData, paramDragShadowBuilder, paramObject, paramInt);
  }
  
  /* Error */
  public final boolean startDragAndDrop(ClipData paramClipData, DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   4: astore 5
    //   6: aload 5
    //   8: ifnonnull +15 -> 23
    //   11: ldc_w 666
    //   14: ldc_w 7584
    //   17: invokestatic 2133	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   20: pop
    //   21: iconst_0
    //   22: ireturn
    //   23: aload 5
    //   25: getfield 2184	android/view/View$AttachInfo:mViewRootImpl	Landroid/view/ViewRootImpl;
    //   28: getfield 7588	android/view/ViewRootImpl:mSurface	Landroid/view/Surface;
    //   31: invokevirtual 7593	android/view/Surface:isValid	()Z
    //   34: ifne +15 -> 49
    //   37: ldc_w 666
    //   40: ldc_w 7595
    //   43: invokestatic 2133	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   46: pop
    //   47: iconst_0
    //   48: ireturn
    //   49: aload_1
    //   50: ifnull +30 -> 80
    //   53: iload 4
    //   55: sipush 256
    //   58: iand
    //   59: ifeq +9 -> 68
    //   62: iconst_1
    //   63: istore 6
    //   65: goto +6 -> 71
    //   68: iconst_0
    //   69: istore 6
    //   71: aload_1
    //   72: iload 6
    //   74: invokevirtual 7600	android/content/ClipData:prepareToLeaveProcess	(Z)V
    //   77: goto +3 -> 80
    //   80: new 5508	android/graphics/Point
    //   83: dup
    //   84: invokespecial 5613	android/graphics/Point:<init>	()V
    //   87: astore 7
    //   89: new 5508	android/graphics/Point
    //   92: dup
    //   93: invokespecial 5613	android/graphics/Point:<init>	()V
    //   96: astore 8
    //   98: aload_2
    //   99: aload 7
    //   101: aload 8
    //   103: invokevirtual 7604	android/view/View$DragShadowBuilder:onProvideShadowMetrics	(Landroid/graphics/Point;Landroid/graphics/Point;)V
    //   106: aload 7
    //   108: getfield 5615	android/graphics/Point:x	I
    //   111: iflt +497 -> 608
    //   114: aload 7
    //   116: getfield 5617	android/graphics/Point:y	I
    //   119: iflt +489 -> 608
    //   122: aload 8
    //   124: getfield 5615	android/graphics/Point:x	I
    //   127: iflt +481 -> 608
    //   130: aload 8
    //   132: getfield 5617	android/graphics/Point:y	I
    //   135: iflt +473 -> 608
    //   138: aload 7
    //   140: getfield 5615	android/graphics/Point:x	I
    //   143: ifeq +11 -> 154
    //   146: aload 7
    //   148: getfield 5617	android/graphics/Point:y	I
    //   151: ifne +21 -> 172
    //   154: getstatic 1390	android/view/View:sAcceptZeroSizeDragShadow	Z
    //   157: ifeq +440 -> 597
    //   160: aload 7
    //   162: iconst_1
    //   163: putfield 5615	android/graphics/Point:x	I
    //   166: aload 7
    //   168: iconst_1
    //   169: putfield 5617	android/graphics/Point:y	I
    //   172: aload_0
    //   173: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   176: getfield 2184	android/view/View$AttachInfo:mViewRootImpl	Landroid/view/ViewRootImpl;
    //   179: astore 9
    //   181: new 7606	android/view/SurfaceSession
    //   184: dup
    //   185: invokespecial 7607	android/view/SurfaceSession:<init>	()V
    //   188: astore 10
    //   190: new 7609	android/view/SurfaceControl$Builder
    //   193: dup
    //   194: aload 10
    //   196: invokespecial 7612	android/view/SurfaceControl$Builder:<init>	(Landroid/view/SurfaceSession;)V
    //   199: ldc_w 7614
    //   202: invokevirtual 7618	android/view/SurfaceControl$Builder:setName	(Ljava/lang/String;)Landroid/view/SurfaceControl$Builder;
    //   205: aload 9
    //   207: invokevirtual 7622	android/view/ViewRootImpl:getSurfaceControl	()Landroid/view/SurfaceControl;
    //   210: invokevirtual 7625	android/view/SurfaceControl$Builder:setParent	(Landroid/view/SurfaceControl;)Landroid/view/SurfaceControl$Builder;
    //   213: aload 7
    //   215: getfield 5615	android/graphics/Point:x	I
    //   218: aload 7
    //   220: getfield 5617	android/graphics/Point:y	I
    //   223: invokevirtual 7629	android/view/SurfaceControl$Builder:setBufferSize	(II)Landroid/view/SurfaceControl$Builder;
    //   226: bipush -3
    //   228: invokevirtual 7633	android/view/SurfaceControl$Builder:setFormat	(I)Landroid/view/SurfaceControl$Builder;
    //   231: invokevirtual 7636	android/view/SurfaceControl$Builder:build	()Landroid/view/SurfaceControl;
    //   234: astore 11
    //   236: new 7590	android/view/Surface
    //   239: dup
    //   240: invokespecial 7637	android/view/Surface:<init>	()V
    //   243: astore 5
    //   245: aload 5
    //   247: aload 11
    //   249: invokevirtual 7641	android/view/Surface:copyFrom	(Landroid/view/SurfaceControl;)V
    //   252: aconst_null
    //   253: astore 12
    //   255: aconst_null
    //   256: astore 13
    //   258: aconst_null
    //   259: astore 14
    //   261: aload 5
    //   263: aconst_null
    //   264: invokevirtual 7645	android/view/Surface:lockCanvas	(Landroid/graphics/Rect;)Landroid/graphics/Canvas;
    //   267: astore 15
    //   269: aload 15
    //   271: iconst_0
    //   272: getstatic 7649	android/graphics/PorterDuff$Mode:CLEAR	Landroid/graphics/PorterDuff$Mode;
    //   275: invokevirtual 7653	android/graphics/Canvas:drawColor	(ILandroid/graphics/PorterDuff$Mode;)V
    //   278: aload_2
    //   279: aload 15
    //   281: invokevirtual 7656	android/view/View$DragShadowBuilder:onDrawShadow	(Landroid/graphics/Canvas;)V
    //   284: aload 5
    //   286: aload 15
    //   288: invokevirtual 7659	android/view/Surface:unlockCanvasAndPost	(Landroid/graphics/Canvas;)V
    //   291: aload 9
    //   293: aload 7
    //   295: invokevirtual 7663	android/view/ViewRootImpl:getLastTouchPoint	(Landroid/graphics/Point;)V
    //   298: aload_0
    //   299: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   302: getfield 4071	android/view/View$AttachInfo:mSession	Landroid/view/IWindowSession;
    //   305: astore_2
    //   306: aload_0
    //   307: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   310: getfield 5311	android/view/View$AttachInfo:mWindow	Landroid/view/IWindow;
    //   313: astore 15
    //   315: aload 9
    //   317: invokevirtual 7666	android/view/ViewRootImpl:getLastTouchSource	()I
    //   320: istore 16
    //   322: aload 7
    //   324: getfield 5615	android/graphics/Point:x	I
    //   327: i2f
    //   328: fstore 17
    //   330: aload 7
    //   332: getfield 5617	android/graphics/Point:y	I
    //   335: i2f
    //   336: fstore 18
    //   338: aload 8
    //   340: getfield 5615	android/graphics/Point:x	I
    //   343: i2f
    //   344: fstore 19
    //   346: aload 8
    //   348: getfield 5617	android/graphics/Point:y	I
    //   351: istore 20
    //   353: iload 20
    //   355: i2f
    //   356: fstore 21
    //   358: aload 5
    //   360: astore 12
    //   362: aload_2
    //   363: aload 15
    //   365: iload 4
    //   367: aload 11
    //   369: iload 16
    //   371: fload 17
    //   373: fload 18
    //   375: fload 19
    //   377: fload 21
    //   379: aload_1
    //   380: invokeinterface 7670 10 0
    //   385: astore_2
    //   386: aload_2
    //   387: ifnull +67 -> 454
    //   390: aload_0
    //   391: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   394: getfield 7673	android/view/View$AttachInfo:mDragSurface	Landroid/view/Surface;
    //   397: ifnull +13 -> 410
    //   400: aload_0
    //   401: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   404: getfield 7673	android/view/View$AttachInfo:mDragSurface	Landroid/view/Surface;
    //   407: invokevirtual 7676	android/view/Surface:release	()V
    //   410: aload_0
    //   411: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   414: aload 12
    //   416: putfield 7673	android/view/View$AttachInfo:mDragSurface	Landroid/view/Surface;
    //   419: aload_0
    //   420: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   423: aload_2
    //   424: putfield 4067	android/view/View$AttachInfo:mDragToken	Landroid/os/IBinder;
    //   427: aload 9
    //   429: aload_3
    //   430: invokevirtual 7679	android/view/ViewRootImpl:setLocalDragState	(Ljava/lang/Object;)V
    //   433: goto +21 -> 454
    //   436: astore_1
    //   437: goto +8 -> 445
    //   440: astore_1
    //   441: goto +8 -> 449
    //   444: astore_1
    //   445: goto +131 -> 576
    //   448: astore_1
    //   449: aload_2
    //   450: astore_3
    //   451: goto +86 -> 537
    //   454: aload_2
    //   455: ifnull +9 -> 464
    //   458: iconst_1
    //   459: istore 6
    //   461: goto +6 -> 467
    //   464: iconst_0
    //   465: istore 6
    //   467: aload_2
    //   468: ifnonnull +8 -> 476
    //   471: aload 12
    //   473: invokevirtual 7682	android/view/Surface:destroy	()V
    //   476: aload 10
    //   478: invokevirtual 7685	android/view/SurfaceSession:kill	()V
    //   481: aload 12
    //   483: invokevirtual 7682	android/view/Surface:destroy	()V
    //   486: iload 6
    //   488: ireturn
    //   489: astore_1
    //   490: aload 13
    //   492: astore_2
    //   493: goto +83 -> 576
    //   496: astore_1
    //   497: aload 14
    //   499: astore_3
    //   500: goto +37 -> 537
    //   503: astore_1
    //   504: aload 12
    //   506: astore_2
    //   507: aload 5
    //   509: aload 15
    //   511: invokevirtual 7659	android/view/Surface:unlockCanvasAndPost	(Landroid/graphics/Canvas;)V
    //   514: aload 12
    //   516: astore_2
    //   517: aload_1
    //   518: athrow
    //   519: astore_1
    //   520: aload 14
    //   522: astore_3
    //   523: goto +14 -> 537
    //   526: astore_1
    //   527: aload 13
    //   529: astore_2
    //   530: goto +46 -> 576
    //   533: astore_1
    //   534: aload 14
    //   536: astore_3
    //   537: aload 5
    //   539: astore 14
    //   541: aload_3
    //   542: astore_2
    //   543: ldc_w 666
    //   546: ldc_w 7687
    //   549: aload_1
    //   550: invokestatic 4046	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   553: pop
    //   554: aload_3
    //   555: ifnonnull +8 -> 563
    //   558: aload 14
    //   560: invokevirtual 7682	android/view/Surface:destroy	()V
    //   563: aload 10
    //   565: invokevirtual 7685	android/view/SurfaceSession:kill	()V
    //   568: aload 14
    //   570: invokevirtual 7682	android/view/Surface:destroy	()V
    //   573: iconst_0
    //   574: ireturn
    //   575: astore_1
    //   576: aload_2
    //   577: ifnonnull +8 -> 585
    //   580: aload 5
    //   582: invokevirtual 7682	android/view/Surface:destroy	()V
    //   585: aload 10
    //   587: invokevirtual 7685	android/view/SurfaceSession:kill	()V
    //   590: aload 5
    //   592: invokevirtual 7682	android/view/Surface:destroy	()V
    //   595: aload_1
    //   596: athrow
    //   597: new 1692	java/lang/IllegalStateException
    //   600: dup
    //   601: ldc_w 7689
    //   604: invokespecial 1695	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   607: athrow
    //   608: new 1692	java/lang/IllegalStateException
    //   611: dup
    //   612: ldc_w 7691
    //   615: invokespecial 1695	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   618: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	619	0	this	View
    //   0	619	1	paramClipData	ClipData
    //   0	619	2	paramDragShadowBuilder	DragShadowBuilder
    //   0	619	3	paramObject	Object
    //   0	619	4	paramInt	int
    //   4	587	5	localObject1	Object
    //   63	424	6	bool	boolean
    //   87	244	7	localPoint1	Point
    //   96	251	8	localPoint2	Point
    //   179	249	9	localViewRootImpl	ViewRootImpl
    //   188	398	10	localSurfaceSession	SurfaceSession
    //   234	134	11	localSurfaceControl	SurfaceControl
    //   253	262	12	localObject2	Object
    //   256	272	13	localObject3	Object
    //   259	310	14	localObject4	Object
    //   267	243	15	localObject5	Object
    //   320	50	16	i	int
    //   328	44	17	f1	float
    //   336	38	18	f2	float
    //   344	32	19	f3	float
    //   351	3	20	j	int
    //   356	22	21	f4	float
    // Exception table:
    //   from	to	target	type
    //   427	433	436	finally
    //   427	433	440	java/lang/Exception
    //   390	410	444	finally
    //   410	427	444	finally
    //   390	410	448	java/lang/Exception
    //   410	427	448	java/lang/Exception
    //   362	386	489	finally
    //   362	386	496	java/lang/Exception
    //   269	284	503	finally
    //   507	514	519	java/lang/Exception
    //   517	519	519	java/lang/Exception
    //   261	269	526	finally
    //   284	291	526	finally
    //   291	353	526	finally
    //   261	269	533	java/lang/Exception
    //   284	291	533	java/lang/Exception
    //   291	353	533	java/lang/Exception
    //   507	514	575	finally
    //   517	519	575	finally
    //   543	554	575	finally
  }
  
  public final boolean startMovingTask(float paramFloat1, float paramFloat2)
  {
    try
    {
      boolean bool = this.mAttachInfo.mSession.startMovingTask(this.mAttachInfo.mWindow, paramFloat1, paramFloat2);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("View", "Unable to start moving", localRemoteException);
    }
    return false;
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    if (hasNestedScrollingParent()) {
      return true;
    }
    if (isNestedScrollingEnabled())
    {
      ViewParent localViewParent = getParent();
      View localView = this;
      while (localViewParent != null)
      {
        try
        {
          if (localViewParent.onStartNestedScroll(localView, this, paramInt))
          {
            this.mNestedScrollingParent = localViewParent;
            localViewParent.onNestedScrollAccepted(localView, this, paramInt);
            return true;
          }
        }
        catch (AbstractMethodError localAbstractMethodError)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("ViewParent ");
          localStringBuilder.append(localViewParent);
          localStringBuilder.append(" does not implement interface method onStartNestedScroll");
          Log.e("View", localStringBuilder.toString(), localAbstractMethodError);
        }
        if ((localViewParent instanceof View)) {
          localView = (View)localViewParent;
        }
        localViewParent = localViewParent.getParent();
      }
    }
    return false;
  }
  
  public void stopNestedScroll()
  {
    ViewParent localViewParent = this.mNestedScrollingParent;
    if (localViewParent != null)
    {
      localViewParent.onStopNestedScroll(this);
      this.mNestedScrollingParent = null;
    }
  }
  
  @UnsupportedAppUsage
  public boolean toGlobalMotionEvent(MotionEvent paramMotionEvent)
  {
    Object localObject = this.mAttachInfo;
    if (localObject == null) {
      return false;
    }
    localObject = ((AttachInfo)localObject).mTmpMatrix;
    ((Matrix)localObject).set(Matrix.IDENTITY_MATRIX);
    transformMatrixToGlobal((Matrix)localObject);
    paramMotionEvent.transform((Matrix)localObject);
    return true;
  }
  
  @UnsupportedAppUsage
  public boolean toLocalMotionEvent(MotionEvent paramMotionEvent)
  {
    Object localObject = this.mAttachInfo;
    if (localObject == null) {
      return false;
    }
    localObject = ((AttachInfo)localObject).mTmpMatrix;
    ((Matrix)localObject).set(Matrix.IDENTITY_MATRIX);
    transformMatrixToLocal((Matrix)localObject);
    paramMotionEvent.transform((Matrix)localObject);
    return true;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append(getClass().getName());
    localStringBuilder.append('{');
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(' ');
    int i = this.mViewFlags & 0xC;
    int j = 73;
    int k = 86;
    int m = 46;
    if (i != 0)
    {
      if (i != 4)
      {
        if (i != 8) {
          localStringBuilder.append('.');
        } else {
          localStringBuilder.append('G');
        }
      }
      else {
        localStringBuilder.append('I');
      }
    }
    else {
      localStringBuilder.append('V');
    }
    int n = this.mViewFlags;
    i = 70;
    if ((n & 0x1) == 1)
    {
      n = 70;
      i1 = n;
    }
    else
    {
      n = 46;
      i1 = n;
    }
    localStringBuilder.append(i1);
    if ((this.mViewFlags & 0x20) == 0)
    {
      n = 69;
      i1 = n;
    }
    else
    {
      n = 46;
      i1 = n;
    }
    localStringBuilder.append(i1);
    if ((this.mViewFlags & 0x80) == 128)
    {
      n = 46;
      i1 = n;
    }
    else
    {
      n = 68;
      i1 = n;
    }
    localStringBuilder.append(i1);
    int i2 = this.mViewFlags;
    n = 72;
    if ((i2 & 0x100) != 0)
    {
      i2 = 72;
      i1 = i2;
    }
    else
    {
      i2 = 46;
      i1 = i2;
    }
    localStringBuilder.append(i1);
    if ((this.mViewFlags & 0x200) != 0)
    {
      i1 = k;
    }
    else
    {
      k = 46;
      i1 = k;
    }
    localStringBuilder.append(i1);
    if ((this.mViewFlags & 0x4000) != 0)
    {
      k = 67;
      i1 = k;
    }
    else
    {
      k = 46;
      i1 = k;
    }
    localStringBuilder.append(i1);
    if ((this.mViewFlags & 0x200000) != 0)
    {
      k = 76;
      i1 = k;
    }
    else
    {
      k = 46;
      i1 = k;
    }
    localStringBuilder.append(i1);
    if ((this.mViewFlags & 0x800000) != 0)
    {
      k = 88;
      i1 = k;
    }
    else
    {
      k = 46;
      i1 = k;
    }
    localStringBuilder.append(i1);
    localStringBuilder.append(' ');
    if ((this.mPrivateFlags & 0x8) != 0)
    {
      k = 82;
      i1 = k;
    }
    else
    {
      k = 46;
      i1 = k;
    }
    localStringBuilder.append(i1);
    if ((this.mPrivateFlags & 0x2) != 0)
    {
      i1 = i;
    }
    else
    {
      i = 46;
      i1 = i;
    }
    localStringBuilder.append(i1);
    if ((this.mPrivateFlags & 0x4) != 0)
    {
      i = 83;
      i1 = i;
    }
    else
    {
      i = 46;
      i1 = i;
    }
    localStringBuilder.append(i1);
    i = this.mPrivateFlags;
    if ((0x2000000 & i) != 0)
    {
      localStringBuilder.append('p');
    }
    else
    {
      if ((i & 0x4000) != 0)
      {
        i = 80;
        i1 = i;
      }
      else
      {
        i = 46;
        i1 = i;
      }
      localStringBuilder.append(i1);
    }
    if ((this.mPrivateFlags & 0x10000000) != 0)
    {
      i1 = n;
    }
    else
    {
      i = 46;
      i1 = i;
    }
    localStringBuilder.append(i1);
    if ((this.mPrivateFlags & 0x40000000) != 0)
    {
      i = 65;
      i1 = i;
    }
    else
    {
      i = 46;
      i1 = i;
    }
    localStringBuilder.append(i1);
    if ((this.mPrivateFlags & 0x80000000) != 0)
    {
      i1 = j;
    }
    else
    {
      j = 46;
      i1 = j;
    }
    localStringBuilder.append(i1);
    int i1 = m;
    if ((this.mPrivateFlags & 0x200000) != 0)
    {
      m = 68;
      i1 = m;
    }
    localStringBuilder.append(i1);
    localStringBuilder.append(' ');
    localStringBuilder.append(this.mLeft);
    localStringBuilder.append(',');
    localStringBuilder.append(this.mTop);
    localStringBuilder.append('-');
    localStringBuilder.append(this.mRight);
    localStringBuilder.append(',');
    localStringBuilder.append(this.mBottom);
    m = getId();
    if (m != -1)
    {
      localStringBuilder.append(" #");
      localStringBuilder.append(Integer.toHexString(m));
      Object localObject = this.mResources;
      if ((m > 0) && (Resources.resourceHasPackage(m)) && (localObject != null))
      {
        j = 0xFF000000 & m;
        String str2;
        if (j != 16777216)
        {
          if (j != 2130706432) {
            try
            {
              String str1 = ((Resources)localObject).getResourcePackageName(m);
            }
            catch (Resources.NotFoundException localNotFoundException)
            {
              break label939;
            }
          } else {
            str2 = "app";
          }
        }
        else {
          str2 = "android";
        }
        String str3 = ((Resources)localObject).getResourceTypeName(m);
        localObject = ((Resources)localObject).getResourceEntryName(m);
        localStringBuilder.append(" ");
        localStringBuilder.append(str2);
        localStringBuilder.append(":");
        localStringBuilder.append(str3);
        localStringBuilder.append("/");
        localStringBuilder.append((String)localObject);
      }
    }
    label939:
    if (this.mAutofillId != null)
    {
      localStringBuilder.append(" aid=");
      localStringBuilder.append(this.mAutofillId);
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void transformFromViewToWindowSpace(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length >= 2))
    {
      Object localObject = this.mAttachInfo;
      if (localObject == null)
      {
        paramArrayOfInt[1] = 0;
        paramArrayOfInt[0] = 0;
        return;
      }
      float[] arrayOfFloat = ((AttachInfo)localObject).mTmpTransformLocation;
      arrayOfFloat[0] = paramArrayOfInt[0];
      arrayOfFloat[1] = paramArrayOfInt[1];
      if (!hasIdentityMatrix()) {
        getMatrix().mapPoints(arrayOfFloat);
      }
      arrayOfFloat[0] += this.mLeft;
      arrayOfFloat[1] += this.mTop;
      for (localObject = this.mParent; (localObject instanceof View); localObject = ((View)localObject).mParent)
      {
        localObject = (View)localObject;
        arrayOfFloat[0] -= ((View)localObject).mScrollX;
        arrayOfFloat[1] -= ((View)localObject).mScrollY;
        if (!((View)localObject).hasIdentityMatrix()) {
          ((View)localObject).getMatrix().mapPoints(arrayOfFloat);
        }
        arrayOfFloat[0] += ((View)localObject).mLeft;
        arrayOfFloat[1] += ((View)localObject).mTop;
      }
      if ((localObject instanceof ViewRootImpl))
      {
        localObject = (ViewRootImpl)localObject;
        arrayOfFloat[1] -= ((ViewRootImpl)localObject).mCurScrollY;
      }
      paramArrayOfInt[0] = Math.round(arrayOfFloat[0]);
      paramArrayOfInt[1] = Math.round(arrayOfFloat[1]);
      return;
    }
    throw new IllegalArgumentException("inOutLocation must be an array of two integers");
  }
  
  public void transformMatrixToGlobal(Matrix paramMatrix)
  {
    Object localObject = this.mParent;
    if ((localObject instanceof View))
    {
      localObject = (View)localObject;
      ((View)localObject).transformMatrixToGlobal(paramMatrix);
      paramMatrix.preTranslate(-((View)localObject).mScrollX, -((View)localObject).mScrollY);
    }
    else if ((localObject instanceof ViewRootImpl))
    {
      localObject = (ViewRootImpl)localObject;
      ((ViewRootImpl)localObject).transformMatrixToGlobal(paramMatrix);
      paramMatrix.preTranslate(0.0F, -((ViewRootImpl)localObject).mCurScrollY);
    }
    paramMatrix.preTranslate(this.mLeft, this.mTop);
    if (!hasIdentityMatrix()) {
      paramMatrix.preConcat(getMatrix());
    }
  }
  
  public void transformMatrixToLocal(Matrix paramMatrix)
  {
    Object localObject = this.mParent;
    if ((localObject instanceof View))
    {
      localObject = (View)localObject;
      ((View)localObject).transformMatrixToLocal(paramMatrix);
      paramMatrix.postTranslate(((View)localObject).mScrollX, ((View)localObject).mScrollY);
    }
    else if ((localObject instanceof ViewRootImpl))
    {
      localObject = (ViewRootImpl)localObject;
      ((ViewRootImpl)localObject).transformMatrixToLocal(paramMatrix);
      paramMatrix.postTranslate(0.0F, ((ViewRootImpl)localObject).mCurScrollY);
    }
    paramMatrix.postTranslate(-this.mLeft, -this.mTop);
    if (!hasIdentityMatrix()) {
      paramMatrix.postConcat(getInverseMatrix());
    }
  }
  
  void unFocus(View paramView)
  {
    clearFocusInternal(paramView, false, false);
  }
  
  public void unscheduleDrawable(Drawable paramDrawable)
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if ((localAttachInfo != null) && (paramDrawable != null)) {
      localAttachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, null, paramDrawable);
    }
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    if ((verifyDrawable(paramDrawable)) && (paramRunnable != null))
    {
      AttachInfo localAttachInfo = this.mAttachInfo;
      if (localAttachInfo != null) {
        localAttachInfo.mViewRootImpl.mChoreographer.removeCallbacks(1, paramRunnable, paramDrawable);
      }
      getRunQueue().removeCallbacks(paramRunnable);
    }
  }
  
  @UnsupportedAppUsage
  public RenderNode updateDisplayListIfDirty()
  {
    RenderNode localRenderNode = this.mRenderNode;
    if (!canHaveDisplayList()) {
      return localRenderNode;
    }
    ForceDarkHelper.getInstance().injectViewWhenUpdateDisplayListIfDirty(this);
    RecordingCanvas localRecordingCanvas;
    if (((this.mPrivateFlags & 0x8000) != 0) && (localRenderNode.hasDisplayList()) && (!this.mRecreateDisplayList))
    {
      this.mPrivateFlags |= 0x8020;
      this.mPrivateFlags &= 0xFFDFFFFF;
    }
    else
    {
      if ((localRenderNode.hasDisplayList()) && (!this.mRecreateDisplayList))
      {
        this.mPrivateFlags |= 0x8020;
        this.mPrivateFlags &= 0xFFDFFFFF;
        dispatchGetDisplayList();
        return localRenderNode;
      }
      this.mRecreateDisplayList = true;
      int i = this.mRight;
      int j = this.mLeft;
      int k = this.mBottom;
      int m = this.mTop;
      int n = getLayerType();
      localRecordingCanvas = localRenderNode.beginRecording(i - j, k - m);
      if (n != 1) {}
    }
    try
    {
      buildDrawingCache(true);
      Bitmap localBitmap = getDrawingCache(true);
      if (localBitmap != null) {
        localRecordingCanvas.drawBitmap(localBitmap, 0.0F, 0.0F, this.mLayerPaint);
      }
      break label328;
      computeScroll();
      localRecordingCanvas.translate(-this.mScrollX, -this.mScrollY);
      this.mPrivateFlags |= 0x8020;
      this.mPrivateFlags &= 0xFFDFFFFF;
      if ((this.mPrivateFlags & 0x80) == 128)
      {
        dispatchDraw(localRecordingCanvas);
        drawAutofilledHighlight(localRecordingCanvas);
        if ((this.mOverlay != null) && (!this.mOverlay.isEmpty())) {
          this.mOverlay.getOverlayView().draw(localRecordingCanvas);
        }
        if (debugDraw()) {
          debugDrawFocus(localRecordingCanvas);
        }
      }
      else
      {
        draw(localRecordingCanvas);
      }
      label328:
      return localRenderNode;
    }
    finally
    {
      localRenderNode.endRecording();
      setDisplayListProperties(localRenderNode);
    }
  }
  
  /* Error */
  public final void updateDragShadow(DragShadowBuilder paramDragShadowBuilder)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   4: astore_2
    //   5: aload_2
    //   6: ifnonnull +14 -> 20
    //   9: ldc_w 666
    //   12: ldc_w 7785
    //   15: invokestatic 2133	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   18: pop
    //   19: return
    //   20: aload_2
    //   21: getfield 4067	android/view/View$AttachInfo:mDragToken	Landroid/os/IBinder;
    //   24: ifnull +71 -> 95
    //   27: aload_0
    //   28: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   31: getfield 7673	android/view/View$AttachInfo:mDragSurface	Landroid/view/Surface;
    //   34: aconst_null
    //   35: invokevirtual 7645	android/view/Surface:lockCanvas	(Landroid/graphics/Rect;)Landroid/graphics/Canvas;
    //   38: astore_2
    //   39: aload_2
    //   40: iconst_0
    //   41: getstatic 7649	android/graphics/PorterDuff$Mode:CLEAR	Landroid/graphics/PorterDuff$Mode;
    //   44: invokevirtual 7653	android/graphics/Canvas:drawColor	(ILandroid/graphics/PorterDuff$Mode;)V
    //   47: aload_1
    //   48: aload_2
    //   49: invokevirtual 7656	android/view/View$DragShadowBuilder:onDrawShadow	(Landroid/graphics/Canvas;)V
    //   52: aload_0
    //   53: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   56: getfield 7673	android/view/View$AttachInfo:mDragSurface	Landroid/view/Surface;
    //   59: aload_2
    //   60: invokevirtual 7659	android/view/Surface:unlockCanvasAndPost	(Landroid/graphics/Canvas;)V
    //   63: goto +29 -> 92
    //   66: astore_1
    //   67: aload_0
    //   68: getfield 1931	android/view/View:mAttachInfo	Landroid/view/View$AttachInfo;
    //   71: getfield 7673	android/view/View$AttachInfo:mDragSurface	Landroid/view/Surface;
    //   74: aload_2
    //   75: invokevirtual 7659	android/view/Surface:unlockCanvasAndPost	(Landroid/graphics/Canvas;)V
    //   78: aload_1
    //   79: athrow
    //   80: astore_1
    //   81: ldc_w 666
    //   84: ldc_w 7787
    //   87: aload_1
    //   88: invokestatic 4046	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   91: pop
    //   92: goto +13 -> 105
    //   95: ldc_w 666
    //   98: ldc_w 7789
    //   101: invokestatic 3374	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   104: pop
    //   105: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	View
    //   0	106	1	paramDragShadowBuilder	DragShadowBuilder
    //   4	71	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   39	52	66	finally
    //   27	39	80	java/lang/Exception
    //   52	63	80	java/lang/Exception
    //   67	80	80	java/lang/Exception
  }
  
  boolean updateLocalSystemUiVisibility(int paramInt1, int paramInt2)
  {
    int i = this.mSystemUiVisibility;
    paramInt1 = paramInt2 & i | paramInt1 & paramInt2;
    if (paramInt1 != i)
    {
      setSystemUiVisibility(paramInt1);
      return true;
    }
    return false;
  }
  
  void updateSystemGestureExclusionRects()
  {
    AttachInfo localAttachInfo = this.mAttachInfo;
    if (localAttachInfo != null) {
      localAttachInfo.mViewRootImpl.updateSystemGestureExclusionRectsForView(this);
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if (paramDrawable != this.mBackground)
    {
      ForegroundInfo localForegroundInfo = this.mForegroundInfo;
      if (((localForegroundInfo == null) || (localForegroundInfo.mDrawable != paramDrawable)) && (this.mDefaultFocusHighlight != paramDrawable)) {
        return false;
      }
    }
    boolean bool = true;
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  @Deprecated
  public boolean willNotCacheDrawing()
  {
    boolean bool;
    if ((this.mViewFlags & 0x20000) == 131072) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="drawing")
  public boolean willNotDraw()
  {
    boolean bool;
    if ((this.mViewFlags & 0x80) == 128) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static class AccessibilityDelegate
  {
    public void addExtraDataToAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo, String paramString, Bundle paramBundle)
    {
      paramView.addExtraDataToAccessibilityNodeInfo(paramAccessibilityNodeInfo, paramString, paramBundle);
    }
    
    @UnsupportedAppUsage
    public AccessibilityNodeInfo createAccessibilityNodeInfo(View paramView)
    {
      return paramView.createAccessibilityNodeInfoInternal();
    }
    
    public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      return paramView.dispatchPopulateAccessibilityEventInternal(paramAccessibilityEvent);
    }
    
    public AccessibilityNodeProvider getAccessibilityNodeProvider(View paramView)
    {
      return null;
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      paramView.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      paramView.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    }
    
    public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      paramView.onPopulateAccessibilityEventInternal(paramAccessibilityEvent);
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      return paramViewGroup.onRequestSendAccessibilityEventInternal(paramView, paramAccessibilityEvent);
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      return paramView.performAccessibilityActionInternal(paramInt, paramBundle);
    }
    
    public void sendAccessibilityEvent(View paramView, int paramInt)
    {
      paramView.sendAccessibilityEventInternal(paramInt);
    }
    
    public void sendAccessibilityEventUnchecked(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      paramView.sendAccessibilityEventUncheckedInternal(paramAccessibilityEvent);
    }
  }
  
  static final class AttachInfo
  {
    int mAccessibilityFetchFlags;
    Drawable mAccessibilityFocusDrawable;
    int mAccessibilityWindowId = -1;
    boolean mAlwaysConsumeSystemBars;
    @UnsupportedAppUsage
    float mApplicationScale;
    Drawable mAutofilledDrawable;
    Canvas mCanvas;
    @UnsupportedAppUsage
    final Rect mContentInsets = new Rect();
    boolean mDebugLayout = ((Boolean)DisplayProperties.debug_layout().orElse(Boolean.valueOf(false))).booleanValue();
    int mDisabledSystemUiVisibility;
    Display mDisplay;
    final DisplayCutout.ParcelableWrapper mDisplayCutout = new DisplayCutout.ParcelableWrapper(DisplayCutout.NO_CUTOUT);
    @UnsupportedAppUsage
    int mDisplayState = 0;
    public Surface mDragSurface;
    IBinder mDragToken;
    @UnsupportedAppUsage
    long mDrawingTime;
    boolean mForceReportNewAttributes;
    @UnsupportedAppUsage
    final ViewTreeObserver.InternalInsetsInfo mGivenInternalInsets = new ViewTreeObserver.InternalInsetsInfo();
    int mGlobalSystemUiVisibility = -1;
    @UnsupportedAppUsage
    final Handler mHandler;
    boolean mHandlingPointerEvent;
    boolean mHardwareAccelerated;
    boolean mHardwareAccelerationRequested;
    boolean mHasNonEmptyGivenInternalInsets;
    boolean mHasSystemUiListeners;
    @UnsupportedAppUsage
    boolean mHasWindowFocus;
    IWindowId mIWindowId;
    @UnsupportedAppUsage
    boolean mInTouchMode;
    final int[] mInvalidateChildLocation = new int[2];
    @UnsupportedAppUsage
    boolean mKeepScreenOn;
    @UnsupportedAppUsage
    final KeyEvent.DispatcherState mKeyDispatchState = new KeyEvent.DispatcherState();
    boolean mNeedsUpdateLightCenter;
    final Rect mOutsets = new Rect();
    final Rect mOverscanInsets = new Rect();
    boolean mOverscanRequested;
    IBinder mPanelParentWindowToken;
    List<RenderNode> mPendingAnimatingRenderNodes;
    final Point mPoint = new Point();
    @UnsupportedAppUsage
    boolean mRecomputeGlobalAttributes;
    final Callbacks mRootCallbacks;
    View mRootView;
    @UnsupportedAppUsage
    boolean mScalingRequired;
    @UnsupportedAppUsage
    final ArrayList<View> mScrollContainers = new ArrayList();
    @UnsupportedAppUsage
    final IWindowSession mSession;
    @UnsupportedAppUsage
    final Rect mStableInsets = new Rect();
    int mSystemUiVisibility;
    final ArrayList<View> mTempArrayList = new ArrayList(24);
    ThreadedRenderer mThreadedRenderer;
    final Rect mTmpInvalRect = new Rect();
    final int[] mTmpLocation = new int[2];
    final Matrix mTmpMatrix = new Matrix();
    final Outline mTmpOutline = new Outline();
    final List<RectF> mTmpRectList = new ArrayList();
    final float[] mTmpTransformLocation = new float[2];
    final RectF mTmpTransformRect = new RectF();
    final RectF mTmpTransformRect1 = new RectF();
    final Transformation mTmpTransformation = new Transformation();
    View mTooltipHost;
    final int[] mTransparentLocation = new int[2];
    @UnsupportedAppUsage
    final ViewTreeObserver mTreeObserver;
    boolean mUnbufferedDispatchRequested;
    boolean mUse32BitDrawingCache;
    View mViewRequestingLayout;
    final ViewRootImpl mViewRootImpl;
    @UnsupportedAppUsage
    boolean mViewScrollChanged;
    @UnsupportedAppUsage
    boolean mViewVisibilityChanged;
    @UnsupportedAppUsage
    final Rect mVisibleInsets = new Rect();
    @UnsupportedAppUsage
    final IWindow mWindow;
    WindowId mWindowId;
    int mWindowLeft;
    final IBinder mWindowToken;
    int mWindowTop;
    int mWindowVisibility;
    
    AttachInfo(IWindowSession paramIWindowSession, IWindow paramIWindow, Display paramDisplay, ViewRootImpl paramViewRootImpl, Handler paramHandler, Callbacks paramCallbacks, Context paramContext)
    {
      this.mSession = paramIWindowSession;
      this.mWindow = paramIWindow;
      this.mWindowToken = paramIWindow.asBinder();
      this.mDisplay = paramDisplay;
      this.mViewRootImpl = paramViewRootImpl;
      this.mHandler = paramHandler;
      this.mRootCallbacks = paramCallbacks;
      this.mTreeObserver = new ViewTreeObserver(paramContext);
    }
    
    static abstract interface Callbacks
    {
      public abstract boolean performHapticFeedback(int paramInt, boolean paramBoolean);
      
      public abstract void playSoundEffect(int paramInt);
    }
    
    static class InvalidateInfo
    {
      private static final int POOL_LIMIT = 10;
      private static final Pools.SynchronizedPool<InvalidateInfo> sPool = new Pools.SynchronizedPool(10);
      @UnsupportedAppUsage
      int bottom;
      @UnsupportedAppUsage
      int left;
      @UnsupportedAppUsage
      int right;
      @UnsupportedAppUsage
      View target;
      @UnsupportedAppUsage
      int top;
      
      public static InvalidateInfo obtain()
      {
        InvalidateInfo localInvalidateInfo = (InvalidateInfo)sPool.acquire();
        if (localInvalidateInfo == null) {
          localInvalidateInfo = new InvalidateInfo();
        }
        return localInvalidateInfo;
      }
      
      public void recycle()
      {
        this.target = null;
        sPool.release(this);
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface AutofillFlags {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface AutofillImportance {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface AutofillType {}
  
  public static class BaseSavedState
    extends AbsSavedState
  {
    static final int AUTOFILL_ID = 4;
    public static final Parcelable.Creator<BaseSavedState> CREATOR = new Parcelable.ClassLoaderCreator()
    {
      public View.BaseSavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new View.BaseSavedState(paramAnonymousParcel);
      }
      
      public View.BaseSavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new View.BaseSavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public View.BaseSavedState[] newArray(int paramAnonymousInt)
      {
        return new View.BaseSavedState[paramAnonymousInt];
      }
    };
    static final int IS_AUTOFILLED = 2;
    static final int START_ACTIVITY_REQUESTED_WHO_SAVED = 1;
    int mAutofillViewId;
    boolean mIsAutofilled;
    int mSavedData;
    String mStartActivityRequestWhoSaved;
    
    public BaseSavedState(Parcel paramParcel)
    {
      this(paramParcel, null);
    }
    
    public BaseSavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      this.mSavedData = paramParcel.readInt();
      this.mStartActivityRequestWhoSaved = paramParcel.readString();
      this.mIsAutofilled = paramParcel.readBoolean();
      this.mAutofillViewId = paramParcel.readInt();
    }
    
    public BaseSavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.mSavedData);
      paramParcel.writeString(this.mStartActivityRequestWhoSaved);
      paramParcel.writeBoolean(this.mIsAutofilled);
      paramParcel.writeInt(this.mAutofillViewId);
    }
  }
  
  private final class CheckForLongPress
    implements Runnable
  {
    private int mClassification;
    private boolean mOriginalPressedState;
    private int mOriginalWindowAttachCount;
    private float mX;
    private float mY;
    
    private CheckForLongPress() {}
    
    public void rememberPressedState()
    {
      this.mOriginalPressedState = View.this.isPressed();
    }
    
    public void rememberWindowAttachCount()
    {
      this.mOriginalWindowAttachCount = View.this.mWindowAttachCount;
    }
    
    public void run()
    {
      if ((this.mOriginalPressedState == View.this.isPressed()) && (View.this.mParent != null) && (this.mOriginalWindowAttachCount == View.this.mWindowAttachCount))
      {
        View.this.recordGestureClassification(this.mClassification);
        if (View.this.performLongClick(this.mX, this.mY)) {
          View.access$3202(View.this, true);
        }
      }
    }
    
    public void setAnchor(float paramFloat1, float paramFloat2)
    {
      this.mX = paramFloat1;
      this.mY = paramFloat2;
    }
    
    public void setClassification(int paramInt)
    {
      this.mClassification = paramInt;
    }
  }
  
  private final class CheckForTap
    implements Runnable
  {
    public float x;
    public float y;
    
    private CheckForTap() {}
    
    public void run()
    {
      View localView = View.this;
      localView.mPrivateFlags &= 0xFDFFFFFF;
      View.this.setPressed(true, this.x, this.y);
      long l = ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout();
      View.this.checkForLongClick(l, this.x, this.y, 3);
    }
  }
  
  private static class DeclaredOnClickListener
    implements View.OnClickListener
  {
    private final View mHostView;
    private final String mMethodName;
    private Context mResolvedContext;
    private Method mResolvedMethod;
    
    public DeclaredOnClickListener(View paramView, String paramString)
    {
      this.mHostView = paramView;
      this.mMethodName = paramString;
    }
    
    private void resolveMethod(Context paramContext, String paramString)
    {
      while (paramContext != null)
      {
        try
        {
          if (!paramContext.isRestricted())
          {
            paramString = paramContext.getClass().getMethod(this.mMethodName, new Class[] { View.class });
            if (paramString != null)
            {
              this.mResolvedMethod = paramString;
              this.mResolvedContext = paramContext;
              return;
            }
          }
        }
        catch (NoSuchMethodException paramString) {}
        if ((paramContext instanceof ContextWrapper)) {
          paramContext = ((ContextWrapper)paramContext).getBaseContext();
        } else {
          paramContext = null;
        }
      }
      int i = this.mHostView.getId();
      if (i == -1)
      {
        paramContext = "";
      }
      else
      {
        paramContext = new StringBuilder();
        paramContext.append(" with id '");
        paramContext.append(this.mHostView.getContext().getResources().getResourceEntryName(i));
        paramContext.append("'");
        paramContext = paramContext.toString();
      }
      paramString = new StringBuilder();
      paramString.append("Could not find method ");
      paramString.append(this.mMethodName);
      paramString.append("(View) in a parent or ancestor Context for android:onClick attribute defined on view ");
      paramString.append(this.mHostView.getClass());
      paramString.append(paramContext);
      throw new IllegalStateException(paramString.toString());
    }
    
    public void onClick(View paramView)
    {
      if (this.mResolvedMethod == null) {
        resolveMethod(this.mHostView.getContext(), this.mMethodName);
      }
      try
      {
        this.mResolvedMethod.invoke(this.mResolvedContext, new Object[] { paramView });
        return;
      }
      catch (InvocationTargetException paramView)
      {
        throw new IllegalStateException("Could not execute method for android:onClick", paramView);
      }
      catch (IllegalAccessException paramView)
      {
        throw new IllegalStateException("Could not execute non-public method for android:onClick", paramView);
      }
    }
  }
  
  public static class DragShadowBuilder
  {
    @UnsupportedAppUsage
    private final WeakReference<View> mView;
    
    public DragShadowBuilder()
    {
      this.mView = new WeakReference(null);
    }
    
    public DragShadowBuilder(View paramView)
    {
      this.mView = new WeakReference(paramView);
    }
    
    public final View getView()
    {
      return (View)this.mView.get();
    }
    
    public void onDrawShadow(Canvas paramCanvas)
    {
      View localView = (View)this.mView.get();
      if (localView != null) {
        localView.draw(paramCanvas);
      } else {
        Log.e("View", "Asked to draw drag shadow but no view");
      }
    }
    
    public void onProvideShadowMetrics(Point paramPoint1, Point paramPoint2)
    {
      View localView = (View)this.mView.get();
      if (localView != null)
      {
        paramPoint1.set(localView.getWidth(), localView.getHeight());
        paramPoint2.set(paramPoint1.x / 2, paramPoint1.y / 2);
      }
      else
      {
        Log.e("View", "Asked for drag thumb metrics but no view");
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface DrawingCacheQuality {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface FindViewFlags {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface FocusDirection {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface FocusRealDirection {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Focusable {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface FocusableMode {}
  
  private static class ForegroundInfo
  {
    private boolean mBoundsChanged = true;
    private Drawable mDrawable;
    private int mGravity = 119;
    private boolean mInsidePadding = true;
    private final Rect mOverlayBounds = new Rect();
    private final Rect mSelfBounds = new Rect();
    private View.TintInfo mTintInfo;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface LayerType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface LayoutDir {}
  
  static class ListenerInfo
  {
    View.OnApplyWindowInsetsListener mOnApplyWindowInsetsListener;
    private CopyOnWriteArrayList<View.OnAttachStateChangeListener> mOnAttachStateChangeListeners;
    View.OnCapturedPointerListener mOnCapturedPointerListener;
    @UnsupportedAppUsage
    public View.OnClickListener mOnClickListener;
    protected View.OnContextClickListener mOnContextClickListener;
    @UnsupportedAppUsage
    protected View.OnCreateContextMenuListener mOnCreateContextMenuListener;
    @UnsupportedAppUsage
    private View.OnDragListener mOnDragListener;
    @UnsupportedAppUsage
    protected View.OnFocusChangeListener mOnFocusChangeListener;
    @UnsupportedAppUsage
    private View.OnGenericMotionListener mOnGenericMotionListener;
    @UnsupportedAppUsage
    private View.OnHoverListener mOnHoverListener;
    @UnsupportedAppUsage
    private View.OnKeyListener mOnKeyListener;
    private ArrayList<View.OnLayoutChangeListener> mOnLayoutChangeListeners;
    @UnsupportedAppUsage
    protected View.OnLongClickListener mOnLongClickListener;
    protected View.OnScrollChangeListener mOnScrollChangeListener;
    private View.OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;
    @UnsupportedAppUsage
    private View.OnTouchListener mOnTouchListener;
    public RenderNode.PositionUpdateListener mPositionUpdateListener;
    private List<Rect> mSystemGestureExclusionRects;
    private ArrayList<View.OnUnhandledKeyEventListener> mUnhandledKeyListeners;
    private WindowInsetsAnimationListener mWindowInsetsAnimationListener;
  }
  
  private static class MatchIdPredicate
    implements Predicate<View>
  {
    public int mId;
    
    public boolean test(View paramView)
    {
      boolean bool;
      if (paramView.mID == this.mId) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  private static class MatchLabelForPredicate
    implements Predicate<View>
  {
    private int mLabeledId;
    
    public boolean test(View paramView)
    {
      boolean bool;
      if (paramView.mLabelForId == this.mLabeledId) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
  
  public static class MeasureSpec
  {
    public static final int AT_MOST = Integer.MIN_VALUE;
    public static final int EXACTLY = 1073741824;
    private static final int MODE_MASK = -1073741824;
    private static final int MODE_SHIFT = 30;
    public static final int UNSPECIFIED = 0;
    
    static int adjust(int paramInt1, int paramInt2)
    {
      int i = getMode(paramInt1);
      int j = getSize(paramInt1);
      if (i == 0) {
        return makeMeasureSpec(j, 0);
      }
      int k = j + paramInt2;
      j = k;
      if (k < 0)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("MeasureSpec.adjust: new size would be negative! (");
        localStringBuilder.append(k);
        localStringBuilder.append(") spec: ");
        localStringBuilder.append(toString(paramInt1));
        localStringBuilder.append(" delta: ");
        localStringBuilder.append(paramInt2);
        Log.e("View", localStringBuilder.toString());
        j = 0;
      }
      return makeMeasureSpec(j, i);
    }
    
    public static int getMode(int paramInt)
    {
      return 0xC0000000 & paramInt;
    }
    
    public static int getSize(int paramInt)
    {
      return 0x3FFFFFFF & paramInt;
    }
    
    public static int makeMeasureSpec(int paramInt1, int paramInt2)
    {
      if (View.sUseBrokenMakeMeasureSpec) {
        return paramInt1 + paramInt2;
      }
      return 0x3FFFFFFF & paramInt1 | 0xC0000000 & paramInt2;
    }
    
    @UnsupportedAppUsage
    public static int makeSafeMeasureSpec(int paramInt1, int paramInt2)
    {
      if ((View.sUseZeroUnspecifiedMeasureSpec) && (paramInt2 == 0)) {
        return 0;
      }
      return makeMeasureSpec(paramInt1, paramInt2);
    }
    
    public static String toString(int paramInt)
    {
      int i = getMode(paramInt);
      paramInt = getSize(paramInt);
      StringBuilder localStringBuilder = new StringBuilder("MeasureSpec: ");
      if (i == 0)
      {
        localStringBuilder.append("UNSPECIFIED ");
      }
      else if (i == 1073741824)
      {
        localStringBuilder.append("EXACTLY ");
      }
      else if (i == Integer.MIN_VALUE)
      {
        localStringBuilder.append("AT_MOST ");
      }
      else
      {
        localStringBuilder.append(i);
        localStringBuilder.append(" ");
      }
      localStringBuilder.append(paramInt);
      return localStringBuilder.toString();
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface MeasureSpecMode {}
  }
  
  public static abstract interface OnApplyWindowInsetsListener
  {
    public abstract WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets);
  }
  
  public static abstract interface OnAttachStateChangeListener
  {
    public abstract void onViewAttachedToWindow(View paramView);
    
    public abstract void onViewDetachedFromWindow(View paramView);
  }
  
  public static abstract interface OnCapturedPointerListener
  {
    public abstract boolean onCapturedPointer(View paramView, MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnClickListener
  {
    public abstract void onClick(View paramView);
  }
  
  public static abstract interface OnContextClickListener
  {
    public abstract boolean onContextClick(View paramView);
  }
  
  public static abstract interface OnCreateContextMenuListener
  {
    public abstract void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo);
  }
  
  public static abstract interface OnDragListener
  {
    public abstract boolean onDrag(View paramView, DragEvent paramDragEvent);
  }
  
  public static abstract interface OnFocusChangeListener
  {
    public abstract void onFocusChange(View paramView, boolean paramBoolean);
  }
  
  public static abstract interface OnGenericMotionListener
  {
    public abstract boolean onGenericMotion(View paramView, MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnHoverListener
  {
    public abstract boolean onHover(View paramView, MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnKeyListener
  {
    public abstract boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent);
  }
  
  public static abstract interface OnLayoutChangeListener
  {
    public abstract void onLayoutChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8);
  }
  
  public static abstract interface OnLongClickListener
  {
    public abstract boolean onLongClick(View paramView);
  }
  
  public static abstract interface OnScrollChangeListener
  {
    public abstract void onScrollChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }
  
  public static abstract interface OnSystemUiVisibilityChangeListener
  {
    public abstract void onSystemUiVisibilityChange(int paramInt);
  }
  
  public static abstract interface OnTouchListener
  {
    public abstract boolean onTouch(View paramView, MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnUnhandledKeyEventListener
  {
    public abstract boolean onUnhandledKeyEvent(View paramView, KeyEvent paramKeyEvent);
  }
  
  private final class PerformClick
    implements Runnable
  {
    private PerformClick() {}
    
    public void run()
    {
      View.this.recordGestureClassification(1);
      View.this.performClickInternal();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ResolvedLayoutDir {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ScrollBarStyle {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ScrollIndicators {}
  
  private static class ScrollabilityCache
    implements Runnable
  {
    public static final int DRAGGING_HORIZONTAL_SCROLL_BAR = 2;
    public static final int DRAGGING_VERTICAL_SCROLL_BAR = 1;
    public static final int FADING = 2;
    public static final int NOT_DRAGGING = 0;
    public static final int OFF = 0;
    public static final int ON = 1;
    private static final float[] OPAQUE = { 255.0F };
    private static final float[] TRANSPARENT = { 0.0F };
    public boolean fadeScrollBars;
    public long fadeStartTime;
    public int fadingEdgeLength;
    @UnsupportedAppUsage
    public View host;
    public float[] interpolatorValues;
    private int mLastColor;
    public final Rect mScrollBarBounds = new Rect();
    public float mScrollBarDraggingPos = 0.0F;
    public int mScrollBarDraggingState = 0;
    public final Rect mScrollBarTouchBounds = new Rect();
    public final Matrix matrix;
    public final Paint paint;
    @UnsupportedAppUsage
    public ScrollBarDrawable scrollBar;
    public int scrollBarDefaultDelayBeforeFade;
    public int scrollBarFadeDuration;
    public final Interpolator scrollBarInterpolator = new Interpolator(1, 2);
    public int scrollBarMinTouchTarget;
    public int scrollBarSize;
    public Shader shader;
    @UnsupportedAppUsage
    public int state = 0;
    
    public ScrollabilityCache(ViewConfiguration paramViewConfiguration, View paramView)
    {
      this.fadingEdgeLength = paramViewConfiguration.getScaledFadingEdgeLength();
      this.scrollBarSize = paramViewConfiguration.getScaledScrollBarSize();
      this.scrollBarMinTouchTarget = paramViewConfiguration.getScaledMinScrollbarTouchTarget();
      this.scrollBarDefaultDelayBeforeFade = ViewConfiguration.getScrollDefaultDelay();
      this.scrollBarFadeDuration = ViewConfiguration.getScrollBarFadeDuration();
      this.paint = new Paint();
      this.matrix = new Matrix();
      this.shader = new LinearGradient(0.0F, 0.0F, 0.0F, 1.0F, -16777216, 0, Shader.TileMode.CLAMP);
      this.paint.setShader(this.shader);
      this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
      this.host = paramView;
    }
    
    public void run()
    {
      long l = AnimationUtils.currentAnimationTimeMillis();
      if (l >= this.fadeStartTime)
      {
        int i = (int)l;
        Interpolator localInterpolator = this.scrollBarInterpolator;
        localInterpolator.setKeyFrame(0, i, OPAQUE);
        localInterpolator.setKeyFrame(0 + 1, i + this.scrollBarFadeDuration, TRANSPARENT);
        this.state = 2;
        this.host.invalidate(true);
      }
    }
    
    public void setFadeColor(int paramInt)
    {
      if (paramInt != this.mLastColor)
      {
        this.mLastColor = paramInt;
        if (paramInt != 0)
        {
          this.shader = new LinearGradient(0.0F, 0.0F, 0.0F, 1.0F, paramInt | 0xFF000000, paramInt & 0xFFFFFF, Shader.TileMode.CLAMP);
          this.paint.setShader(this.shader);
          this.paint.setXfermode(null);
        }
        else
        {
          this.shader = new LinearGradient(0.0F, 0.0F, 0.0F, 1.0F, -16777216, 0, Shader.TileMode.CLAMP);
          this.paint.setShader(this.shader);
          this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }
      }
    }
  }
  
  private class SendViewScrolledAccessibilityEvent
    implements Runnable
  {
    public int mDeltaX;
    public int mDeltaY;
    public volatile boolean mIsPending;
    
    private SendViewScrolledAccessibilityEvent() {}
    
    private void reset()
    {
      this.mIsPending = false;
      this.mDeltaX = 0;
      this.mDeltaY = 0;
    }
    
    public void post(int paramInt1, int paramInt2)
    {
      this.mDeltaX += paramInt1;
      this.mDeltaY += paramInt2;
      if (!this.mIsPending)
      {
        this.mIsPending = true;
        View.this.postDelayed(this, ViewConfiguration.getSendRecurringAccessibilityEventsInterval());
      }
    }
    
    public void run()
    {
      if (AccessibilityManager.getInstance(View.this.mContext).isEnabled())
      {
        AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(4096);
        localAccessibilityEvent.setScrollDeltaX(this.mDeltaX);
        localAccessibilityEvent.setScrollDeltaY(this.mDeltaY);
        View.this.sendAccessibilityEventUnchecked(localAccessibilityEvent);
      }
      reset();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface TextAlignment {}
  
  static class TintInfo
  {
    BlendMode mBlendMode;
    boolean mHasTintList;
    boolean mHasTintMode;
    ColorStateList mTintList;
  }
  
  private static class TooltipInfo
  {
    int mAnchorX;
    int mAnchorY;
    Runnable mHideTooltipRunnable;
    int mHoverSlop;
    Runnable mShowTooltipRunnable;
    boolean mTooltipFromLongClick;
    TooltipPopup mTooltipPopup;
    CharSequence mTooltipText;
    
    private void clearAnchorPos()
    {
      this.mAnchorX = Integer.MAX_VALUE;
      this.mAnchorY = Integer.MAX_VALUE;
    }
    
    private boolean updateAnchorPos(MotionEvent paramMotionEvent)
    {
      int i = (int)paramMotionEvent.getX();
      int j = (int)paramMotionEvent.getY();
      if ((Math.abs(i - this.mAnchorX) <= this.mHoverSlop) && (Math.abs(j - this.mAnchorY) <= this.mHoverSlop)) {
        return false;
      }
      this.mAnchorX = i;
      this.mAnchorY = j;
      return true;
    }
  }
  
  static class TransformationInfo
  {
    @ViewDebug.ExportedProperty
    private float mAlpha = 1.0F;
    private Matrix mInverseMatrix;
    private final Matrix mMatrix = new Matrix();
    float mTransitionAlpha = 1.0F;
  }
  
  private final class UnsetPressedState
    implements Runnable
  {
    private UnsetPressedState() {}
    
    public void run()
    {
      View.this.setPressed(false);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ViewStructureType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Visibility {}
  
  private static class VisibilityChangeForAutofillHandler
    extends Handler
  {
    private final AutofillManager mAfm;
    private final View mView;
    
    private VisibilityChangeForAutofillHandler(AutofillManager paramAutofillManager, View paramView)
    {
      this.mAfm = paramAutofillManager;
      this.mView = paramView;
    }
    
    public void handleMessage(Message paramMessage)
    {
      paramMessage = this.mAfm;
      View localView = this.mView;
      paramMessage.notifyViewVisibilityChanged(localView, localView.isShown());
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/View.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */