package miui.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MiuiSettings.System;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miui.os.Build;
import miui.os.SystemProperties;

public class HapticFeedbackUtil
{
  public static final String EFFECT_KEY_CALCULATOR = "calculator";
  public static final String EFFECT_KEY_CLOCK_PICKER = "clock_picker";
  public static final String EFFECT_KEY_CLOCK_SECOND = "clock_second";
  public static final String EFFECT_KEY_COMPASS_CALIBRATION = "compass_calibration";
  public static final String EFFECT_KEY_COMPASS_NORTH = "compass_north";
  public static final String EFFECT_KEY_FLICK = "flick";
  public static final String EFFECT_KEY_FLICK_LIGHT = "flick_light";
  public static final String EFFECT_KEY_HOLD = "hold";
  public static final String EFFECT_KEY_HOME_DROP_FINISH = "home_drop_finish";
  public static final String EFFECT_KEY_HOME_PICKUP_START = "home_pickup_start";
  public static final String EFFECT_KEY_LONG_PRESS = "long_press";
  public static final String EFFECT_KEY_MESH_HEAVY = "mesh_heavy";
  public static final String EFFECT_KEY_MESH_LIGHT = "mesh_light";
  public static final String EFFECT_KEY_MESH_NORMAL = "mesh_normal";
  public static final String EFFECT_KEY_PICKUP = "pickup";
  public static final String EFFECT_KEY_POPUP_LIGHT = "popup_light";
  public static final String EFFECT_KEY_POPUP_NORMAL = "popup_normal";
  public static final String EFFECT_KEY_RECORDER_DELETE = "recorder_delete";
  public static final String EFFECT_KEY_RECORDER_FINISH = "recorder_finish";
  public static final String EFFECT_KEY_RECORDER_LIST = "recorder_list";
  public static final String EFFECT_KEY_RECORDER_PAUSE = "recorder_pause";
  public static final String EFFECT_KEY_RECORDER_PLAY = "recorder_play";
  public static final String EFFECT_KEY_RECORDER_RECORD = "recorder_record";
  public static final String EFFECT_KEY_RECORDER_RECORD_PAUSE = "recorder_record_pause";
  public static final String EFFECT_KEY_RECORDER_REWIND = "recorder_rewind";
  public static final String EFFECT_KEY_RECORDER_SLIDER = "recorder_slider";
  public static final String EFFECT_KEY_RECORDER_STOP = "recorder_stop";
  public static final String EFFECT_KEY_SCREEN_BUTTON_RECENT_TASK = "screen_button_recent_task";
  public static final String EFFECT_KEY_SCREEN_BUTTON_VOICE_ASSIST = "screen_button_voice_assist";
  public static final String EFFECT_KEY_SCROLL_EDGE = "scroll_edge";
  public static final String EFFECT_KEY_SWITCH = "switch";
  public static final String EFFECT_KEY_TAP_LIGHT = "tap_light";
  public static final String EFFECT_KEY_TAP_NORMAL = "tap_normal";
  public static final String EFFECT_KEY_TORCH_OFF = "torch_off";
  public static final String EFFECT_KEY_TORCH_ON = "torch_on";
  public static final String EFFECT_KEY_TRIGGER_DRAWER = "trigger_drawer";
  public static final String EFFECT_KEY_VIRTUAL_KEY_DOWN = "virtual_key_down";
  public static final String EFFECT_KEY_VIRTUAL_KEY_LONGPRESS = "virtual_key_longpress";
  public static final String EFFECT_KEY_VIRTUAL_KEY_TAP = "virtual_key_tap";
  public static final String EFFECT_KEY_VIRTUAL_KEY_UP = "virtual_key_up";
  private static final int EFFECT_STRENGTH_DEFAULT = -100;
  private static final int EFFECT_STRENGTH_STRONG = 2;
  private static final SparseArray<String> ID_TO_KEY;
  public static final boolean IS_IMMERSION_ENABLED = false;
  private static final String KEY_VIBRATE_EX_ENABLED = "ro.haptic.vibrate_ex.enabled";
  private static final float[] LEVEL_FACTOR;
  private static final String[] LEVEL_SUFFIX = { ".weak", ".normal", ".strong" };
  private static final HashMap<String, String> PROPERTY_KEY;
  private static final List<String> PROPERTY_MOTOR_KEY;
  private static final String TAG = "HapticFeedbackUtil";
  private static final int VIRTUAL_RELEASED = 2;
  private static final HashMap<String, Integer> sPatternId;
  private static final HashMap<String, long[]> sPatterns = new HashMap();
  private final Context mContext;
  private boolean mIsSupportLinearMotorVibrate;
  private boolean mIsSupportZLinearMotorVibrate;
  private int mLevel;
  private SettingsObserver mSettingsObserver;
  private Vibrator mVibrator;
  
  static
  {
    LEVEL_FACTOR = new float[] { 0.5F, 1.0F, 1.5F };
    ID_TO_KEY = new SparseArray();
    ID_TO_KEY.put(1, "virtual_key_down");
    ID_TO_KEY.put(0, "virtual_key_longpress");
    ID_TO_KEY.put(3, "virtual_key_tap");
    ID_TO_KEY.put(2, "virtual_key_up");
    ID_TO_KEY.put(268435456, "tap_normal");
    ID_TO_KEY.put(268435457, "tap_light");
    ID_TO_KEY.put(268435458, "flick");
    ID_TO_KEY.put(268435469, "flick_light");
    ID_TO_KEY.put(268435459, "switch");
    ID_TO_KEY.put(268435460, "mesh_heavy");
    ID_TO_KEY.put(268435461, "mesh_normal");
    ID_TO_KEY.put(268435462, "mesh_light");
    ID_TO_KEY.put(268435463, "long_press");
    ID_TO_KEY.put(268435464, "popup_normal");
    ID_TO_KEY.put(268435465, "popup_light");
    ID_TO_KEY.put(268435466, "pickup");
    ID_TO_KEY.put(268435467, "scroll_edge");
    ID_TO_KEY.put(268435468, "trigger_drawer");
    ID_TO_KEY.put(268435470, "hold");
    sPatternId = new HashMap();
    PROPERTY_KEY = new HashMap();
    PROPERTY_KEY.put("virtual_key_down", "sys.haptic.down");
    PROPERTY_KEY.put("virtual_key_longpress", "sys.haptic.long.press");
    PROPERTY_KEY.put("virtual_key_tap", "sys.haptic.tap.normal");
    PROPERTY_KEY.put("virtual_key_up", "sys.haptic.up");
    PROPERTY_KEY.put("tap_normal", "sys.haptic.tap.normal");
    PROPERTY_KEY.put("tap_light", "sys.haptic.tap.light");
    PROPERTY_KEY.put("flick", "sys.haptic.flick");
    PROPERTY_KEY.put("flick_light", "sys.haptic.flick.light");
    PROPERTY_KEY.put("switch", "sys.haptic.switch");
    PROPERTY_KEY.put("mesh_heavy", "sys.haptic.mesh.heavy");
    PROPERTY_KEY.put("mesh_normal", "sys.haptic.mesh.normal");
    PROPERTY_KEY.put("mesh_light", "sys.haptic.mesh.light");
    PROPERTY_KEY.put("long_press", "sys.haptic.long.press");
    PROPERTY_KEY.put("pickup", "sys.haptic.pickup");
    PROPERTY_KEY.put("scroll_edge", "sys.haptic.scroll.edge");
    PROPERTY_KEY.put("popup_normal", "sys.haptic.popup.normal");
    PROPERTY_KEY.put("popup_light", "sys.haptic.popup.light");
    PROPERTY_KEY.put("trigger_drawer", "sys.haptic.trigger.drawer");
    PROPERTY_KEY.put("hold", "sys.haptic.hold");
    PROPERTY_MOTOR_KEY = new ArrayList();
    PROPERTY_MOTOR_KEY.add("tap_normal");
    PROPERTY_MOTOR_KEY.add("tap_light");
    PROPERTY_MOTOR_KEY.add("flick");
    PROPERTY_MOTOR_KEY.add("flick_light");
    PROPERTY_MOTOR_KEY.add("switch");
    PROPERTY_MOTOR_KEY.add("mesh_heavy");
    PROPERTY_MOTOR_KEY.add("mesh_normal");
    PROPERTY_MOTOR_KEY.add("mesh_light");
    PROPERTY_MOTOR_KEY.add("long_press");
    PROPERTY_MOTOR_KEY.add("popup_normal");
    PROPERTY_MOTOR_KEY.add("popup_light");
    PROPERTY_MOTOR_KEY.add("trigger_drawer");
    PROPERTY_MOTOR_KEY.add("hold");
    PROPERTY_MOTOR_KEY.add("virtual_key_down");
    PROPERTY_MOTOR_KEY.add("virtual_key_tap");
    PROPERTY_MOTOR_KEY.add("virtual_key_longpress");
    PROPERTY_MOTOR_KEY.add("scroll_edge");
  }
  
  public HapticFeedbackUtil(Context paramContext, boolean paramBoolean)
  {
    Object localObject1 = Resources.getSystem();
    int i = ((Resources)localObject1).getIdentifier("config_longPressVibePattern", "array", "android");
    int j = ((Resources)localObject1).getIdentifier("config_virtualKeyVibePattern", "array", "android");
    int k = ((Resources)localObject1).getIdentifier("config_keyboardTapVibePattern", "array", "android");
    sPatternId.put("compass_north", Integer.valueOf(i));
    sPatternId.put("home_pickup_start", Integer.valueOf(i));
    Object localObject2 = sPatternId;
    localObject1 = Integer.valueOf(285343789);
    ((HashMap)localObject2).put("recorder_delete", localObject1);
    sPatternId.put("recorder_finish", localObject1);
    sPatternId.put("recorder_list", localObject1);
    localObject1 = sPatternId;
    localObject2 = Integer.valueOf(285343788);
    ((HashMap)localObject1).put("recorder_pause", localObject2);
    sPatternId.put("recorder_play", localObject2);
    sPatternId.put("recorder_record", localObject2);
    sPatternId.put("recorder_record_pause", localObject2);
    sPatternId.put("recorder_rewind", Integer.valueOf(285343790));
    sPatternId.put("recorder_slider", Integer.valueOf(285343791));
    sPatternId.put("recorder_stop", localObject2);
    sPatternId.put("screen_button_recent_task", Integer.valueOf(i));
    sPatternId.put("screen_button_voice_assist", Integer.valueOf(i));
    sPatternId.put("torch_off", Integer.valueOf(285343792));
    sPatternId.put("torch_on", Integer.valueOf(285343793));
    sPatternId.put("virtual_key_longpress", Integer.valueOf(i));
    sPatternId.put("virtual_key_down", Integer.valueOf(j));
    sPatternId.put("virtual_key_tap", Integer.valueOf(k));
    sPatternId.put("virtual_key_up", Integer.valueOf(285343794));
    if (!Build.IS_MIUI) {
      j = SystemProperties.getInt("ro.haptic.default_level", 1);
    } else {
      j = MiuiSettings.System.HAPTIC_FEEDBACK_LEVEL_DEFAULT;
    }
    this.mLevel = j;
    this.mIsSupportLinearMotorVibrate = false;
    this.mIsSupportZLinearMotorVibrate = false;
    this.mContext = paramContext;
    this.mVibrator = ((Vibrator)this.mContext.getSystemService("vibrator"));
    if (!Build.IS_MIUI) {
      return;
    }
    if (paramBoolean)
    {
      updateSettings();
    }
    else
    {
      this.mSettingsObserver = new SettingsObserver(new Handler());
      this.mSettingsObserver.observe();
    }
    this.mIsSupportLinearMotorVibrate = isSupportLinearMotorVibrate();
    this.mIsSupportZLinearMotorVibrate = isSupportZLinearMotorVibrate();
  }
  
  private VibrationEffect getHapticFeedback(int paramInt)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (isSupportedEffect(paramInt))
    {
      localObject2 = (String)ID_TO_KEY.get(paramInt);
      if (!sPatterns.containsKey(localObject2)) {
        sPatterns.put(localObject2, loadPattern((String)localObject2));
      }
      localObject2 = (long[])sPatterns.get(localObject2);
      if ((localObject2 != null) && (localObject2.length > 0))
      {
        localObject2 = VibrateUtils.getVibrationEffect((int)localObject2[0]);
      }
      else
      {
        Log.w("HapticFeedbackUtil", "vibrate: null or empty pattern");
        localObject2 = localObject1;
      }
    }
    return (VibrationEffect)localObject2;
  }
  
  private long[] getLongIntArray(Resources paramResources, int paramInt)
  {
    paramResources = paramResources.getIntArray(paramInt);
    if (paramResources == null) {
      return null;
    }
    long[] arrayOfLong = new long[paramResources.length];
    for (paramInt = 0; paramInt < paramResources.length; paramInt++) {
      arrayOfLong[paramInt] = ((paramResources[paramInt] * LEVEL_FACTOR[this.mLevel]));
    }
    return arrayOfLong;
  }
  
  private boolean isHapticsDisable()
  {
    boolean bool;
    if ((Build.IS_MIUI) && (MiuiSettings.System.isHapticFeedbackDisabled(this.mContext))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean isSupportLinearMotorVibrate()
  {
    return "linear".equals(SystemProperties.get("sys.haptic.motor"));
  }
  
  public static boolean isSupportLinearMotorVibrate(int paramInt)
  {
    if (isSupportLinearMotorVibrate())
    {
      String str = (String)ID_TO_KEY.get(paramInt);
      if ((PROPERTY_MOTOR_KEY.contains(str)) && (!TextUtils.isEmpty(SystemProperties.get((String)PROPERTY_KEY.get(str))))) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isSupportMotorEffect(String paramString)
  {
    boolean bool;
    if ((this.mIsSupportLinearMotorVibrate) && (!TextUtils.isEmpty(paramString)) && (PROPERTY_MOTOR_KEY.contains(paramString))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private static boolean isSupportZLinearMotorVibrate()
  {
    return "zlinear".equals(SystemProperties.get("sys.haptic.motor"));
  }
  
  private long[] loadPattern(String paramString)
  {
    Object localObject1 = null;
    String str = (String)PROPERTY_KEY.get(paramString);
    if (str != null)
    {
      localObject2 = stringToLongArray(SystemProperties.get(str));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append(str);
        ((StringBuilder)localObject1).append(LEVEL_SUFFIX[this.mLevel]);
        localObject1 = stringToLongArray(SystemProperties.get(((StringBuilder)localObject1).toString()));
      }
    }
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      localObject2 = localObject1;
      if (sPatternId.containsKey(paramString))
      {
        int i = ((Integer)sPatternId.get(paramString)).intValue();
        int j;
        try
        {
          j = ResourceMapper.resolveReference(this.mContext.getResources(), i);
        }
        catch (Exception paramString)
        {
          j = i;
        }
        localObject2 = getLongIntArray(this.mContext.getResources(), j);
      }
    }
    return (long[])localObject2;
  }
  
  private long[] stringToLongArray(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    String[] arrayOfString = paramString.split(",");
    int i = arrayOfString.length;
    paramString = new long[i];
    for (int j = 0; j < i; j++) {
      paramString[j] = Long.parseLong(arrayOfString[j].trim());
    }
    return paramString;
  }
  
  public VibrationEffect convertToMiuiHapticFeedback(int paramInt)
  {
    if (paramInt != 4) {
      return null;
    }
    return getHapticFeedback(268435461);
  }
  
  public boolean isSupportedEffect(int paramInt)
  {
    boolean bool;
    if ((paramInt > 3) && (!isSupportMotorEffect((String)ID_TO_KEY.get(paramInt)))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean performExtHapticFeedback(int paramInt)
  {
    if ((this.mIsSupportLinearMotorVibrate) && (!isHapticsDisable())) {
      return VibrateUtils.vibrateExt(this.mVibrator, paramInt);
    }
    return false;
  }
  
  public boolean performExtHapticFeedback(Uri paramUri)
  {
    if ((this.mIsSupportLinearMotorVibrate) && (!isHapticsDisable())) {
      return VibrateUtils.vibrateExt(this.mVibrator, paramUri, this.mContext);
    }
    return false;
  }
  
  public boolean performHapticFeedback(int paramInt, boolean paramBoolean)
  {
    return performHapticFeedback((String)ID_TO_KEY.get(paramInt), paramBoolean);
  }
  
  public boolean performHapticFeedback(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    return performHapticFeedback((String)ID_TO_KEY.get(paramInt1), paramBoolean, paramInt2);
  }
  
  public boolean performHapticFeedback(String paramString, boolean paramBoolean)
  {
    if (TextUtils.isEmpty(paramString))
    {
      Log.w("HapticFeedbackUtil", "fail to get key");
      return false;
    }
    if (!sPatterns.containsKey(paramString)) {
      sPatterns.put(paramString, loadPattern(paramString));
    }
    long[] arrayOfLong = (long[])sPatterns.get(paramString);
    if ((arrayOfLong != null) && (arrayOfLong.length != 0))
    {
      boolean bool = isSupportMotorEffect(paramString);
      int i = 2;
      if (bool)
      {
        if (arrayOfLong.length < 2)
        {
          Log.w("HapticFeedbackUtil", "fail to read strength id");
          return false;
        }
        return performHapticFeedback(paramString, paramBoolean, (int)arrayOfLong[1]);
      }
      if ((!Build.DEVICE.equals("andromeda")) && (!this.mIsSupportLinearMotorVibrate)) {
        i = -100;
      }
      return performHapticFeedback(paramString, paramBoolean, i);
    }
    Log.w("HapticFeedbackUtil", "vibrate: null or empty pattern");
    return false;
  }
  
  public boolean performHapticFeedback(String paramString, boolean paramBoolean, int paramInt)
  {
    boolean bool1 = this.mVibrator.hasVibrator();
    boolean bool2 = false;
    if (!bool1) {
      return false;
    }
    if ((!paramBoolean) && (isHapticsDisable())) {
      return false;
    }
    if (!sPatterns.containsKey(paramString)) {
      sPatterns.put(paramString, loadPattern(paramString));
    }
    paramString = (long[])sPatterns.get(paramString);
    if ((paramString != null) && (paramString.length != 0))
    {
      Vibrator localVibrator = this.mVibrator;
      if (!this.mIsSupportLinearMotorVibrate)
      {
        paramBoolean = bool2;
        if (!this.mIsSupportZLinearMotorVibrate) {}
      }
      else
      {
        paramBoolean = true;
      }
      VibrateUtils.vibrate(localVibrator, paramBoolean, paramString, paramInt, -100);
      return true;
    }
    Log.w("HapticFeedbackUtil", "vibrate: null or empty pattern");
    return false;
  }
  
  public void release()
  {
    SettingsObserver localSettingsObserver = this.mSettingsObserver;
    if (localSettingsObserver != null) {
      localSettingsObserver.unobserve();
    }
  }
  
  public void stop() {}
  
  public void updateImmersionSettings(boolean paramBoolean) {}
  
  public void updateSettings()
  {
    this.mLevel = MiuiSettings.System.getHapticFeedbackLevel(this.mContext);
    this.mLevel = Math.min(2, Math.max(0, this.mLevel));
    sPatterns.clear();
  }
  
  private class SettingsObserver
    extends ContentObserver
  {
    SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    void observe()
    {
      ContentResolver localContentResolver = HapticFeedbackUtil.this.mContext.getContentResolver();
      localContentResolver.registerContentObserver(Settings.System.getUriFor("haptic_feedback_level"), false, this);
      HapticFeedbackUtil.this.updateSettings();
    }
    
    public void onChange(boolean paramBoolean)
    {
      HapticFeedbackUtil.this.updateSettings();
    }
    
    void unobserve()
    {
      HapticFeedbackUtil.this.mContext.getContentResolver().unregisterContentObserver(this);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/HapticFeedbackUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */