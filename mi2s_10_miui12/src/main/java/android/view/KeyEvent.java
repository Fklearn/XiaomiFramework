package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseIntArray;

public class KeyEvent
  extends InputEvent
  implements Parcelable
{
  public static final int ACTION_DOWN = 0;
  @Deprecated
  public static final int ACTION_MULTIPLE = 2;
  public static final int ACTION_UP = 1;
  public static final Parcelable.Creator<KeyEvent> CREATOR = new Parcelable.Creator()
  {
    public KeyEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      paramAnonymousParcel.readInt();
      return KeyEvent.createFromParcelBody(paramAnonymousParcel);
    }
    
    public KeyEvent[] newArray(int paramAnonymousInt)
    {
      return new KeyEvent[paramAnonymousInt];
    }
  };
  static final boolean DEBUG = false;
  public static final int FLAG_CANCELED = 32;
  public static final int FLAG_CANCELED_LONG_PRESS = 256;
  public static final int FLAG_EDITOR_ACTION = 16;
  public static final int FLAG_FALLBACK = 1024;
  public static final int FLAG_FROM_SYSTEM = 8;
  public static final int FLAG_KEEP_TOUCH_MODE = 4;
  public static final int FLAG_LONG_PRESS = 128;
  public static final int FLAG_PREDISPATCH = 536870912;
  public static final int FLAG_SOFT_KEYBOARD = 2;
  public static final int FLAG_START_TRACKING = 1073741824;
  public static final int FLAG_TAINTED = Integer.MIN_VALUE;
  public static final int FLAG_TRACKING = 512;
  public static final int FLAG_VIRTUAL_HARD_KEY = 64;
  @Deprecated
  public static final int FLAG_WOKE_HERE = 1;
  public static final int KEYCODE_0 = 7;
  public static final int KEYCODE_1 = 8;
  public static final int KEYCODE_11 = 227;
  public static final int KEYCODE_12 = 228;
  public static final int KEYCODE_2 = 9;
  public static final int KEYCODE_3 = 10;
  public static final int KEYCODE_3D_MODE = 206;
  public static final int KEYCODE_4 = 11;
  public static final int KEYCODE_5 = 12;
  public static final int KEYCODE_6 = 13;
  public static final int KEYCODE_7 = 14;
  public static final int KEYCODE_8 = 15;
  public static final int KEYCODE_9 = 16;
  public static final int KEYCODE_A = 29;
  public static final int KEYCODE_AI = 689;
  public static final int KEYCODE_ALL_APPS = 284;
  public static final int KEYCODE_ALT_LEFT = 57;
  public static final int KEYCODE_ALT_RIGHT = 58;
  public static final int KEYCODE_APOSTROPHE = 75;
  public static final int KEYCODE_APP_SWITCH = 187;
  public static final int KEYCODE_ASSIST = 219;
  public static final int KEYCODE_AT = 77;
  public static final int KEYCODE_AVR_INPUT = 182;
  public static final int KEYCODE_AVR_POWER = 181;
  public static final int KEYCODE_B = 30;
  public static final int KEYCODE_BACK = 4;
  public static final int KEYCODE_BACKSLASH = 73;
  public static final int KEYCODE_BOOKMARK = 174;
  public static final int KEYCODE_BREAK = 121;
  public static final int KEYCODE_BRIGHTNESS_DOWN = 220;
  public static final int KEYCODE_BRIGHTNESS_UP = 221;
  public static final int KEYCODE_BUTTON_1 = 188;
  public static final int KEYCODE_BUTTON_10 = 197;
  public static final int KEYCODE_BUTTON_11 = 198;
  public static final int KEYCODE_BUTTON_12 = 199;
  public static final int KEYCODE_BUTTON_13 = 200;
  public static final int KEYCODE_BUTTON_14 = 201;
  public static final int KEYCODE_BUTTON_15 = 202;
  public static final int KEYCODE_BUTTON_16 = 203;
  public static final int KEYCODE_BUTTON_2 = 189;
  public static final int KEYCODE_BUTTON_3 = 190;
  public static final int KEYCODE_BUTTON_4 = 191;
  public static final int KEYCODE_BUTTON_5 = 192;
  public static final int KEYCODE_BUTTON_6 = 193;
  public static final int KEYCODE_BUTTON_7 = 194;
  public static final int KEYCODE_BUTTON_8 = 195;
  public static final int KEYCODE_BUTTON_9 = 196;
  public static final int KEYCODE_BUTTON_A = 96;
  public static final int KEYCODE_BUTTON_B = 97;
  public static final int KEYCODE_BUTTON_C = 98;
  public static final int KEYCODE_BUTTON_L1 = 102;
  public static final int KEYCODE_BUTTON_L2 = 104;
  public static final int KEYCODE_BUTTON_MODE = 110;
  public static final int KEYCODE_BUTTON_R1 = 103;
  public static final int KEYCODE_BUTTON_R2 = 105;
  public static final int KEYCODE_BUTTON_SELECT = 109;
  public static final int KEYCODE_BUTTON_START = 108;
  public static final int KEYCODE_BUTTON_THUMBL = 106;
  public static final int KEYCODE_BUTTON_THUMBR = 107;
  public static final int KEYCODE_BUTTON_X = 99;
  public static final int KEYCODE_BUTTON_Y = 100;
  public static final int KEYCODE_BUTTON_Z = 101;
  public static final int KEYCODE_C = 31;
  public static final int KEYCODE_CALCULATOR = 210;
  public static final int KEYCODE_CALENDAR = 208;
  public static final int KEYCODE_CALL = 5;
  public static final int KEYCODE_CAMERA = 27;
  public static final int KEYCODE_CAPS_LOCK = 115;
  public static final int KEYCODE_CAPTIONS = 175;
  public static final int KEYCODE_CHANNEL_DOWN = 167;
  public static final int KEYCODE_CHANNEL_UP = 166;
  public static final int KEYCODE_CLEAR = 28;
  public static final int KEYCODE_COMMA = 55;
  public static final int KEYCODE_CONTACTS = 207;
  public static final int KEYCODE_COPY = 278;
  public static final int KEYCODE_CTRL_LEFT = 113;
  public static final int KEYCODE_CTRL_RIGHT = 114;
  public static final int KEYCODE_CUT = 277;
  public static final int KEYCODE_D = 32;
  public static final int KEYCODE_DEL = 67;
  public static final int KEYCODE_DPAD_CENTER = 23;
  public static final int KEYCODE_DPAD_DOWN = 20;
  public static final int KEYCODE_DPAD_DOWN_LEFT = 269;
  public static final int KEYCODE_DPAD_DOWN_RIGHT = 271;
  public static final int KEYCODE_DPAD_LEFT = 21;
  public static final int KEYCODE_DPAD_RIGHT = 22;
  public static final int KEYCODE_DPAD_UP = 19;
  public static final int KEYCODE_DPAD_UP_LEFT = 268;
  public static final int KEYCODE_DPAD_UP_RIGHT = 270;
  public static final int KEYCODE_DVR = 173;
  public static final int KEYCODE_E = 33;
  public static final int KEYCODE_EISU = 212;
  public static final int KEYCODE_ENDCALL = 6;
  public static final int KEYCODE_ENTER = 66;
  public static final int KEYCODE_ENVELOPE = 65;
  public static final int KEYCODE_EQUALS = 70;
  public static final int KEYCODE_ESCAPE = 111;
  public static final int KEYCODE_EXPLORER = 64;
  public static final int KEYCODE_F = 34;
  public static final int KEYCODE_F1 = 131;
  public static final int KEYCODE_F10 = 140;
  public static final int KEYCODE_F11 = 141;
  public static final int KEYCODE_F12 = 142;
  public static final int KEYCODE_F2 = 132;
  public static final int KEYCODE_F3 = 133;
  public static final int KEYCODE_F4 = 134;
  public static final int KEYCODE_F5 = 135;
  public static final int KEYCODE_F6 = 136;
  public static final int KEYCODE_F7 = 137;
  public static final int KEYCODE_F8 = 138;
  public static final int KEYCODE_F9 = 139;
  public static final int KEYCODE_FOCUS = 80;
  public static final int KEYCODE_FORWARD = 125;
  public static final int KEYCODE_FORWARD_DEL = 112;
  public static final int KEYCODE_FUNCTION = 119;
  public static final int KEYCODE_G = 35;
  public static final int KEYCODE_GOTO = 354;
  public static final int KEYCODE_GRAVE = 68;
  public static final int KEYCODE_GUIDE = 172;
  public static final int KEYCODE_H = 36;
  public static final int KEYCODE_HEADSETHOOK = 79;
  public static final int KEYCODE_HELP = 259;
  public static final int KEYCODE_HENKAN = 214;
  public static final int KEYCODE_HOME = 3;
  public static final int KEYCODE_I = 37;
  public static final int KEYCODE_INFO = 165;
  public static final int KEYCODE_INSERT = 124;
  public static final int KEYCODE_J = 38;
  public static final int KEYCODE_K = 39;
  public static final int KEYCODE_KANA = 218;
  public static final int KEYCODE_KATAKANA_HIRAGANA = 215;
  public static final int KEYCODE_L = 40;
  public static final int KEYCODE_LANGUAGE_SWITCH = 204;
  public static final int KEYCODE_LAST_CHANNEL = 229;
  public static final int KEYCODE_LEFT_BRACKET = 71;
  public static final int KEYCODE_M = 41;
  public static final int KEYCODE_MANNER_MODE = 205;
  public static final int KEYCODE_MEDIA_AUDIO_TRACK = 222;
  public static final int KEYCODE_MEDIA_CLOSE = 128;
  public static final int KEYCODE_MEDIA_EJECT = 129;
  public static final int KEYCODE_MEDIA_FAST_FORWARD = 90;
  public static final int KEYCODE_MEDIA_NEXT = 87;
  public static final int KEYCODE_MEDIA_PAUSE = 127;
  public static final int KEYCODE_MEDIA_PLAY = 126;
  public static final int KEYCODE_MEDIA_PLAY_PAUSE = 85;
  public static final int KEYCODE_MEDIA_PREVIOUS = 88;
  public static final int KEYCODE_MEDIA_RECORD = 130;
  public static final int KEYCODE_MEDIA_REWIND = 89;
  public static final int KEYCODE_MEDIA_SKIP_BACKWARD = 273;
  public static final int KEYCODE_MEDIA_SKIP_FORWARD = 272;
  public static final int KEYCODE_MEDIA_STEP_BACKWARD = 275;
  public static final int KEYCODE_MEDIA_STEP_FORWARD = 274;
  public static final int KEYCODE_MEDIA_STOP = 86;
  public static final int KEYCODE_MEDIA_TOP_MENU = 226;
  public static final int KEYCODE_MENU = 82;
  public static final int KEYCODE_META_LEFT = 117;
  public static final int KEYCODE_META_RIGHT = 118;
  public static final int KEYCODE_MINUS = 69;
  public static final int KEYCODE_MOVE_END = 123;
  public static final int KEYCODE_MOVE_HOME = 122;
  public static final int KEYCODE_MUHENKAN = 213;
  public static final int KEYCODE_MUSIC = 209;
  public static final int KEYCODE_MUTE = 91;
  public static final int KEYCODE_N = 42;
  public static final int KEYCODE_NAVIGATE_IN = 262;
  public static final int KEYCODE_NAVIGATE_NEXT = 261;
  public static final int KEYCODE_NAVIGATE_OUT = 263;
  public static final int KEYCODE_NAVIGATE_PREVIOUS = 260;
  public static final int KEYCODE_NOTIFICATION = 83;
  public static final int KEYCODE_NUM = 78;
  public static final int KEYCODE_NUMPAD_0 = 144;
  public static final int KEYCODE_NUMPAD_1 = 145;
  public static final int KEYCODE_NUMPAD_2 = 146;
  public static final int KEYCODE_NUMPAD_3 = 147;
  public static final int KEYCODE_NUMPAD_4 = 148;
  public static final int KEYCODE_NUMPAD_5 = 149;
  public static final int KEYCODE_NUMPAD_6 = 150;
  public static final int KEYCODE_NUMPAD_7 = 151;
  public static final int KEYCODE_NUMPAD_8 = 152;
  public static final int KEYCODE_NUMPAD_9 = 153;
  public static final int KEYCODE_NUMPAD_ADD = 157;
  public static final int KEYCODE_NUMPAD_COMMA = 159;
  public static final int KEYCODE_NUMPAD_DIVIDE = 154;
  public static final int KEYCODE_NUMPAD_DOT = 158;
  public static final int KEYCODE_NUMPAD_ENTER = 160;
  public static final int KEYCODE_NUMPAD_EQUALS = 161;
  public static final int KEYCODE_NUMPAD_LEFT_PAREN = 162;
  public static final int KEYCODE_NUMPAD_MULTIPLY = 155;
  public static final int KEYCODE_NUMPAD_RIGHT_PAREN = 163;
  public static final int KEYCODE_NUMPAD_SUBTRACT = 156;
  public static final int KEYCODE_NUM_LOCK = 143;
  public static final int KEYCODE_O = 43;
  public static final int KEYCODE_P = 44;
  public static final int KEYCODE_PAGE_DOWN = 93;
  public static final int KEYCODE_PAGE_UP = 92;
  public static final int KEYCODE_PAIRING = 225;
  public static final int KEYCODE_PASTE = 279;
  public static final int KEYCODE_PERIOD = 56;
  public static final int KEYCODE_PICTSYMBOLS = 94;
  public static final int KEYCODE_PLUS = 81;
  public static final int KEYCODE_POUND = 18;
  public static final int KEYCODE_POWER = 26;
  public static final int KEYCODE_PROFILE_SWITCH = 288;
  public static final int KEYCODE_PROG_BLUE = 186;
  public static final int KEYCODE_PROG_GREEN = 184;
  public static final int KEYCODE_PROG_RED = 183;
  public static final int KEYCODE_PROG_YELLOW = 185;
  public static final int KEYCODE_Q = 45;
  public static final int KEYCODE_R = 46;
  public static final int KEYCODE_REFRESH = 285;
  public static final int KEYCODE_RIGHT_BRACKET = 72;
  public static final int KEYCODE_RO = 217;
  public static final int KEYCODE_S = 47;
  public static final int KEYCODE_SCROLL_LOCK = 116;
  public static final int KEYCODE_SEARCH = 84;
  public static final int KEYCODE_SEMICOLON = 74;
  public static final int KEYCODE_SETTINGS = 176;
  public static final int KEYCODE_SHIFT_LEFT = 59;
  public static final int KEYCODE_SHIFT_RIGHT = 60;
  public static final int KEYCODE_SLASH = 76;
  public static final int KEYCODE_SLEEP = 223;
  public static final int KEYCODE_SLIDE_CLOSE = 701;
  public static final int KEYCODE_SLIDE_OPEN = 700;
  public static final int KEYCODE_SLIDING = 702;
  public static final int KEYCODE_SOFT_LEFT = 1;
  public static final int KEYCODE_SOFT_RIGHT = 2;
  public static final int KEYCODE_SOFT_SLEEP = 276;
  public static final int KEYCODE_SPACE = 62;
  public static final int KEYCODE_STAR = 17;
  public static final int KEYCODE_STB_INPUT = 180;
  public static final int KEYCODE_STB_POWER = 179;
  public static final int KEYCODE_STEM_1 = 265;
  public static final int KEYCODE_STEM_2 = 266;
  public static final int KEYCODE_STEM_3 = 267;
  public static final int KEYCODE_STEM_PRIMARY = 264;
  public static final int KEYCODE_SWITCH_CHARSET = 95;
  public static final int KEYCODE_SYM = 63;
  public static final int KEYCODE_SYSRQ = 120;
  public static final int KEYCODE_SYSTEM_NAVIGATION_DOWN = 281;
  public static final int KEYCODE_SYSTEM_NAVIGATION_LEFT = 282;
  public static final int KEYCODE_SYSTEM_NAVIGATION_RIGHT = 283;
  public static final int KEYCODE_SYSTEM_NAVIGATION_UP = 280;
  public static final int KEYCODE_T = 48;
  public static final int KEYCODE_TAB = 61;
  public static final int KEYCODE_THUMBS_DOWN = 287;
  public static final int KEYCODE_THUMBS_UP = 286;
  public static final int KEYCODE_TV = 170;
  public static final int KEYCODE_TV_ANTENNA_CABLE = 242;
  public static final int KEYCODE_TV_AUDIO_DESCRIPTION = 252;
  public static final int KEYCODE_TV_AUDIO_DESCRIPTION_MIX_DOWN = 254;
  public static final int KEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP = 253;
  public static final int KEYCODE_TV_CONTENTS_MENU = 256;
  public static final int KEYCODE_TV_DATA_SERVICE = 230;
  public static final int KEYCODE_TV_INPUT = 178;
  public static final int KEYCODE_TV_INPUT_COMPONENT_1 = 249;
  public static final int KEYCODE_TV_INPUT_COMPONENT_2 = 250;
  public static final int KEYCODE_TV_INPUT_COMPOSITE_1 = 247;
  public static final int KEYCODE_TV_INPUT_COMPOSITE_2 = 248;
  public static final int KEYCODE_TV_INPUT_HDMI_1 = 243;
  public static final int KEYCODE_TV_INPUT_HDMI_2 = 244;
  public static final int KEYCODE_TV_INPUT_HDMI_3 = 245;
  public static final int KEYCODE_TV_INPUT_HDMI_4 = 246;
  public static final int KEYCODE_TV_INPUT_VGA_1 = 251;
  public static final int KEYCODE_TV_MEDIA_CONTEXT_MENU = 257;
  public static final int KEYCODE_TV_NETWORK = 241;
  public static final int KEYCODE_TV_NUMBER_ENTRY = 234;
  public static final int KEYCODE_TV_POWER = 177;
  public static final int KEYCODE_TV_RADIO_SERVICE = 232;
  public static final int KEYCODE_TV_SATELLITE = 237;
  public static final int KEYCODE_TV_SATELLITE_BS = 238;
  public static final int KEYCODE_TV_SATELLITE_CS = 239;
  public static final int KEYCODE_TV_SATELLITE_SERVICE = 240;
  public static final int KEYCODE_TV_TELETEXT = 233;
  public static final int KEYCODE_TV_TERRESTRIAL_ANALOG = 235;
  public static final int KEYCODE_TV_TERRESTRIAL_DIGITAL = 236;
  public static final int KEYCODE_TV_TIMER_PROGRAMMING = 258;
  public static final int KEYCODE_TV_ZOOM_MODE = 255;
  public static final int KEYCODE_U = 49;
  public static final int KEYCODE_UNKNOWN = 0;
  public static final int KEYCODE_V = 50;
  public static final int KEYCODE_VOICE_ASSIST = 231;
  public static final int KEYCODE_VOLUME_DOWN = 25;
  public static final int KEYCODE_VOLUME_MUTE = 164;
  public static final int KEYCODE_VOLUME_UP = 24;
  public static final int KEYCODE_W = 51;
  public static final int KEYCODE_WAKEUP = 224;
  public static final int KEYCODE_WINDOW = 171;
  public static final int KEYCODE_X = 52;
  public static final int KEYCODE_Y = 53;
  public static final int KEYCODE_YEN = 216;
  public static final int KEYCODE_Z = 54;
  public static final int KEYCODE_ZENKAKU_HANKAKU = 211;
  public static final int KEYCODE_ZOOM_IN = 168;
  public static final int KEYCODE_ZOOM_OUT = 169;
  private static final String LABEL_PREFIX = "KEYCODE_";
  public static final int LAST_KEYCODE = 288;
  @Deprecated
  public static final int MAX_KEYCODE = 84;
  private static final int MAX_RECYCLED = 10;
  @UnsupportedAppUsage
  private static final int META_ALL_MASK = 7827711;
  public static final int META_ALT_LEFT_ON = 16;
  @UnsupportedAppUsage
  public static final int META_ALT_LOCKED = 512;
  public static final int META_ALT_MASK = 50;
  public static final int META_ALT_ON = 2;
  public static final int META_ALT_RIGHT_ON = 32;
  public static final int META_CAPS_LOCK_ON = 1048576;
  @UnsupportedAppUsage
  public static final int META_CAP_LOCKED = 256;
  public static final int META_CTRL_LEFT_ON = 8192;
  public static final int META_CTRL_MASK = 28672;
  public static final int META_CTRL_ON = 4096;
  public static final int META_CTRL_RIGHT_ON = 16384;
  public static final int META_FUNCTION_ON = 8;
  @UnsupportedAppUsage
  private static final int META_INVALID_MODIFIER_MASK = 7343872;
  @UnsupportedAppUsage
  private static final int META_LOCK_MASK = 7340032;
  public static final int META_META_LEFT_ON = 131072;
  public static final int META_META_MASK = 458752;
  public static final int META_META_ON = 65536;
  public static final int META_META_RIGHT_ON = 262144;
  @UnsupportedAppUsage
  private static final int META_MODIFIER_MASK = 487679;
  public static final int META_NUM_LOCK_ON = 2097152;
  public static final int META_SCROLL_LOCK_ON = 4194304;
  @UnsupportedAppUsage
  public static final int META_SELECTING = 2048;
  public static final int META_SHIFT_LEFT_ON = 64;
  public static final int META_SHIFT_MASK = 193;
  public static final int META_SHIFT_ON = 1;
  public static final int META_SHIFT_RIGHT_ON = 128;
  @UnsupportedAppUsage
  private static final String[] META_SYMBOLIC_NAMES = { "META_SHIFT_ON", "META_ALT_ON", "META_SYM_ON", "META_FUNCTION_ON", "META_ALT_LEFT_ON", "META_ALT_RIGHT_ON", "META_SHIFT_LEFT_ON", "META_SHIFT_RIGHT_ON", "META_CAP_LOCKED", "META_ALT_LOCKED", "META_SYM_LOCKED", "0x00000800", "META_CTRL_ON", "META_CTRL_LEFT_ON", "META_CTRL_RIGHT_ON", "0x00008000", "META_META_ON", "META_META_LEFT_ON", "META_META_RIGHT_ON", "0x00080000", "META_CAPS_LOCK_ON", "META_NUM_LOCK_ON", "META_SCROLL_LOCK_ON", "0x00800000", "0x01000000", "0x02000000", "0x04000000", "0x08000000", "0x10000000", "0x20000000", "0x40000000", "0x80000000" };
  @UnsupportedAppUsage
  public static final int META_SYM_LOCKED = 1024;
  public static final int META_SYM_ON = 4;
  @UnsupportedAppUsage
  private static final int META_SYNTHETIC_MASK = 3840;
  static final String TAG = "KeyEvent";
  private static final Object gRecyclerLock = new Object();
  private static KeyEvent gRecyclerTop;
  private static int gRecyclerUsed;
  @UnsupportedAppUsage
  private int mAction;
  @UnsupportedAppUsage
  private String mCharacters;
  @UnsupportedAppUsage
  private int mDeviceId;
  private int mDisplayId;
  @UnsupportedAppUsage
  private long mDownTime;
  @UnsupportedAppUsage
  private long mEventTime;
  @UnsupportedAppUsage
  private int mFlags;
  @UnsupportedAppUsage
  private int mKeyCode;
  @UnsupportedAppUsage
  private int mMetaState;
  private KeyEvent mNext;
  @UnsupportedAppUsage
  private int mRepeatCount;
  @UnsupportedAppUsage
  private int mScanCode;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private int mSource;
  
  private KeyEvent() {}
  
  public KeyEvent(int paramInt1, int paramInt2)
  {
    this.mAction = paramInt1;
    this.mKeyCode = paramInt2;
    this.mRepeatCount = 0;
    this.mDeviceId = -1;
  }
  
  public KeyEvent(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mDownTime = paramLong1;
    this.mEventTime = paramLong2;
    this.mAction = paramInt1;
    this.mKeyCode = paramInt2;
    this.mRepeatCount = paramInt3;
    this.mDeviceId = -1;
  }
  
  public KeyEvent(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mDownTime = paramLong1;
    this.mEventTime = paramLong2;
    this.mAction = paramInt1;
    this.mKeyCode = paramInt2;
    this.mRepeatCount = paramInt3;
    this.mMetaState = paramInt4;
    this.mDeviceId = -1;
  }
  
  public KeyEvent(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.mDownTime = paramLong1;
    this.mEventTime = paramLong2;
    this.mAction = paramInt1;
    this.mKeyCode = paramInt2;
    this.mRepeatCount = paramInt3;
    this.mMetaState = paramInt4;
    this.mDeviceId = paramInt5;
    this.mScanCode = paramInt6;
  }
  
  public KeyEvent(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    this.mDownTime = paramLong1;
    this.mEventTime = paramLong2;
    this.mAction = paramInt1;
    this.mKeyCode = paramInt2;
    this.mRepeatCount = paramInt3;
    this.mMetaState = paramInt4;
    this.mDeviceId = paramInt5;
    this.mScanCode = paramInt6;
    this.mFlags = paramInt7;
  }
  
  public KeyEvent(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    this.mDownTime = paramLong1;
    this.mEventTime = paramLong2;
    this.mAction = paramInt1;
    this.mKeyCode = paramInt2;
    this.mRepeatCount = paramInt3;
    this.mMetaState = paramInt4;
    this.mDeviceId = paramInt5;
    this.mScanCode = paramInt6;
    this.mFlags = paramInt7;
    this.mSource = paramInt8;
    this.mDisplayId = -1;
  }
  
  public KeyEvent(long paramLong, String paramString, int paramInt1, int paramInt2)
  {
    this.mDownTime = paramLong;
    this.mEventTime = paramLong;
    this.mCharacters = paramString;
    this.mAction = 2;
    this.mKeyCode = 0;
    this.mRepeatCount = 0;
    this.mDeviceId = paramInt1;
    this.mFlags = paramInt2;
    this.mSource = 257;
    this.mDisplayId = -1;
  }
  
  private KeyEvent(Parcel paramParcel)
  {
    this.mDeviceId = paramParcel.readInt();
    this.mSource = paramParcel.readInt();
    this.mDisplayId = paramParcel.readInt();
    this.mAction = paramParcel.readInt();
    this.mKeyCode = paramParcel.readInt();
    this.mRepeatCount = paramParcel.readInt();
    this.mMetaState = paramParcel.readInt();
    this.mScanCode = paramParcel.readInt();
    this.mFlags = paramParcel.readInt();
    this.mDownTime = paramParcel.readLong();
    this.mEventTime = paramParcel.readLong();
    this.mCharacters = paramParcel.readString();
  }
  
  public KeyEvent(KeyEvent paramKeyEvent)
  {
    this.mDownTime = paramKeyEvent.mDownTime;
    this.mEventTime = paramKeyEvent.mEventTime;
    this.mAction = paramKeyEvent.mAction;
    this.mKeyCode = paramKeyEvent.mKeyCode;
    this.mRepeatCount = paramKeyEvent.mRepeatCount;
    this.mMetaState = paramKeyEvent.mMetaState;
    this.mDeviceId = paramKeyEvent.mDeviceId;
    this.mSource = paramKeyEvent.mSource;
    this.mDisplayId = paramKeyEvent.mDisplayId;
    this.mScanCode = paramKeyEvent.mScanCode;
    this.mFlags = paramKeyEvent.mFlags;
    this.mCharacters = paramKeyEvent.mCharacters;
  }
  
  private KeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    this.mDownTime = paramKeyEvent.mDownTime;
    this.mEventTime = paramKeyEvent.mEventTime;
    this.mAction = paramInt;
    this.mKeyCode = paramKeyEvent.mKeyCode;
    this.mRepeatCount = paramKeyEvent.mRepeatCount;
    this.mMetaState = paramKeyEvent.mMetaState;
    this.mDeviceId = paramKeyEvent.mDeviceId;
    this.mSource = paramKeyEvent.mSource;
    this.mDisplayId = paramKeyEvent.mDisplayId;
    this.mScanCode = paramKeyEvent.mScanCode;
    this.mFlags = paramKeyEvent.mFlags;
  }
  
  @Deprecated
  public KeyEvent(KeyEvent paramKeyEvent, long paramLong, int paramInt)
  {
    this.mDownTime = paramKeyEvent.mDownTime;
    this.mEventTime = paramLong;
    this.mAction = paramKeyEvent.mAction;
    this.mKeyCode = paramKeyEvent.mKeyCode;
    this.mRepeatCount = paramInt;
    this.mMetaState = paramKeyEvent.mMetaState;
    this.mDeviceId = paramKeyEvent.mDeviceId;
    this.mSource = paramKeyEvent.mSource;
    this.mDisplayId = paramKeyEvent.mDisplayId;
    this.mScanCode = paramKeyEvent.mScanCode;
    this.mFlags = paramKeyEvent.mFlags;
    this.mCharacters = paramKeyEvent.mCharacters;
  }
  
  public static String actionToString(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2) {
          return Integer.toString(paramInt);
        }
        return "ACTION_MULTIPLE";
      }
      return "ACTION_UP";
    }
    return "ACTION_DOWN";
  }
  
  public static KeyEvent changeAction(KeyEvent paramKeyEvent, int paramInt)
  {
    return new KeyEvent(paramKeyEvent, paramInt);
  }
  
  public static KeyEvent changeFlags(KeyEvent paramKeyEvent, int paramInt)
  {
    paramKeyEvent = new KeyEvent(paramKeyEvent);
    paramKeyEvent.mFlags = paramInt;
    return paramKeyEvent;
  }
  
  public static KeyEvent changeTimeRepeat(KeyEvent paramKeyEvent, long paramLong, int paramInt)
  {
    return new KeyEvent(paramKeyEvent, paramLong, paramInt);
  }
  
  public static KeyEvent changeTimeRepeat(KeyEvent paramKeyEvent, long paramLong, int paramInt1, int paramInt2)
  {
    paramKeyEvent = new KeyEvent(paramKeyEvent);
    paramKeyEvent.mEventTime = paramLong;
    paramKeyEvent.mRepeatCount = paramInt1;
    paramKeyEvent.mFlags = paramInt2;
    return paramKeyEvent;
  }
  
  public static KeyEvent createFromParcelBody(Parcel paramParcel)
  {
    return new KeyEvent(paramParcel);
  }
  
  public static int getDeadChar(int paramInt1, int paramInt2)
  {
    return KeyCharacterMap.getDeadChar(paramInt1, paramInt2);
  }
  
  public static int getMaxKeyCode()
  {
    return 288;
  }
  
  public static int getModifierMetaStateMask()
  {
    return 487679;
  }
  
  public static final boolean isAltKey(int paramInt)
  {
    boolean bool;
    if ((paramInt != 57) && (paramInt != 58)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  public static final boolean isConfirmKey(int paramInt)
  {
    return (paramInt == 23) || (paramInt == 62) || (paramInt == 66) || (paramInt == 160);
  }
  
  public static final boolean isGamepadButton(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      switch (paramInt)
      {
      default: 
        return false;
      }
      break;
    }
    return true;
  }
  
  public static final boolean isMediaSessionKey(int paramInt)
  {
    if ((paramInt != 79) && (paramInt != 130) && (paramInt != 126) && (paramInt != 127)) {
      switch (paramInt)
      {
      default: 
        return false;
      }
    }
    return true;
  }
  
  public static final boolean isMetaKey(int paramInt)
  {
    boolean bool;
    if ((paramInt != 117) && (paramInt != 118)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isModifierKey(int paramInt)
  {
    if ((paramInt != 63) && (paramInt != 78) && (paramInt != 113) && (paramInt != 114)) {
      switch (paramInt)
      {
      default: 
        switch (paramInt)
        {
        default: 
          return false;
        }
        break;
      }
    }
    return true;
  }
  
  public static final boolean isSystemKey(int paramInt)
  {
    if ((paramInt != 2) && (paramInt != 3) && (paramInt != 4) && (paramInt != 5) && (paramInt != 6) && (paramInt != 79) && (paramInt != 80) && (paramInt != 82) && (paramInt != 130) && (paramInt != 164) && (paramInt != 689) && (paramInt != 126) && (paramInt != 127)) {
      switch (paramInt)
      {
      default: 
        switch (paramInt)
        {
        default: 
          switch (paramInt)
          {
          default: 
            switch (paramInt)
            {
            default: 
              return false;
            }
            break;
          }
          break;
        }
        break;
      }
    }
    return true;
  }
  
  public static final boolean isWakeKey(int paramInt)
  {
    if ((paramInt != 224) && (paramInt != 225)) {
      switch (paramInt)
      {
      default: 
        return false;
      }
    }
    return true;
  }
  
  public static int keyCodeFromString(String paramString)
  {
    try
    {
      i = Integer.parseInt(paramString);
      boolean bool = keyCodeIsValid(i);
      if (bool) {
        return i;
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
    String str = paramString;
    if (paramString.startsWith("KEYCODE_")) {
      str = paramString.substring("KEYCODE_".length());
    }
    int i = nativeKeyCodeFromString(str);
    if (keyCodeIsValid(i)) {
      return i;
    }
    return 0;
  }
  
  private static boolean keyCodeIsValid(int paramInt)
  {
    boolean bool;
    if ((paramInt >= 0) && (paramInt <= 288)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static String keyCodeToString(int paramInt)
  {
    String str = nativeKeyCodeToString(paramInt);
    if (str != null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("KEYCODE_");
      localStringBuilder.append(str);
      str = localStringBuilder.toString();
    }
    else
    {
      str = Integer.toString(paramInt);
    }
    return str;
  }
  
  private static int metaStateFilterDirectionalModifiers(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = 1;
    int j;
    if ((paramInt2 & paramInt3) != 0) {
      j = 1;
    } else {
      j = 0;
    }
    int k = paramInt4 | paramInt5;
    if ((paramInt2 & k) != 0) {
      paramInt2 = i;
    } else {
      paramInt2 = 0;
    }
    if (j != 0)
    {
      if (paramInt2 == 0) {
        return k & paramInt1;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("modifiers must not contain ");
      localStringBuilder.append(metaStateToString(paramInt3));
      localStringBuilder.append(" combined with ");
      localStringBuilder.append(metaStateToString(paramInt4));
      localStringBuilder.append(" or ");
      localStringBuilder.append(metaStateToString(paramInt5));
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    if (paramInt2 != 0) {
      return paramInt3 & paramInt1;
    }
    return paramInt1;
  }
  
  public static boolean metaStateHasModifiers(int paramInt1, int paramInt2)
  {
    if ((0x700F00 & paramInt2) == 0)
    {
      paramInt1 = normalizeMetaState(paramInt1);
      boolean bool = true;
      if (metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(paramInt1 & 0x770FF, paramInt2, 1, 64, 128), paramInt2, 2, 16, 32), paramInt2, 4096, 8192, 16384), paramInt2, 65536, 131072, 262144) != paramInt2) {
        bool = false;
      }
      return bool;
    }
    throw new IllegalArgumentException("modifiers must not contain META_CAPS_LOCK_ON, META_NUM_LOCK_ON, META_SCROLL_LOCK_ON, META_CAP_LOCKED, META_ALT_LOCKED, META_SYM_LOCKED, or META_SELECTING");
  }
  
  public static boolean metaStateHasNoModifiers(int paramInt)
  {
    boolean bool;
    if ((normalizeMetaState(paramInt) & 0x770FF) == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static String metaStateToString(int paramInt)
  {
    if (paramInt == 0) {
      return "0";
    }
    Object localObject1 = null;
    int i = 0;
    int j = paramInt;
    paramInt = i;
    while (j != 0)
    {
      if ((j & 0x1) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      j >>>= 1;
      Object localObject2 = localObject1;
      if (i != 0)
      {
        localObject2 = META_SYMBOLIC_NAMES[paramInt];
        if (localObject1 == null)
        {
          if (j == 0) {
            return (String)localObject2;
          }
          localObject2 = new StringBuilder((String)localObject2);
        }
        else
        {
          ((StringBuilder)localObject1).append('|');
          ((StringBuilder)localObject1).append((String)localObject2);
          localObject2 = localObject1;
        }
      }
      paramInt++;
      localObject1 = localObject2;
    }
    return ((StringBuilder)localObject1).toString();
  }
  
  private static native int nativeKeyCodeFromString(String paramString);
  
  private static native String nativeKeyCodeToString(int paramInt);
  
  public static int normalizeMetaState(int paramInt)
  {
    int i = paramInt;
    if ((paramInt & 0xC0) != 0) {
      i = paramInt | 0x1;
    }
    int j = i;
    if ((i & 0x30) != 0) {
      j = i | 0x2;
    }
    paramInt = j;
    if ((j & 0x6000) != 0) {
      paramInt = j | 0x1000;
    }
    j = paramInt;
    if ((0x60000 & paramInt) != 0) {
      j = paramInt | 0x10000;
    }
    i = j;
    if ((j & 0x100) != 0) {
      i = j | 0x100000;
    }
    paramInt = i;
    if ((i & 0x200) != 0) {
      paramInt = i | 0x2;
    }
    j = paramInt;
    if ((paramInt & 0x400) != 0) {
      j = paramInt | 0x4;
    }
    return 0x7770FF & j;
  }
  
  private static KeyEvent obtain()
  {
    synchronized (gRecyclerLock)
    {
      KeyEvent localKeyEvent = gRecyclerTop;
      if (localKeyEvent == null)
      {
        localKeyEvent = new android/view/KeyEvent;
        localKeyEvent.<init>();
        return localKeyEvent;
      }
      gRecyclerTop = localKeyEvent.mNext;
      gRecyclerUsed -= 1;
      localKeyEvent.mNext = null;
      localKeyEvent.prepareForReuse();
      return localKeyEvent;
    }
  }
  
  public static KeyEvent obtain(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, String paramString)
  {
    KeyEvent localKeyEvent = obtain();
    localKeyEvent.mDownTime = paramLong1;
    localKeyEvent.mEventTime = paramLong2;
    localKeyEvent.mAction = paramInt1;
    localKeyEvent.mKeyCode = paramInt2;
    localKeyEvent.mRepeatCount = paramInt3;
    localKeyEvent.mMetaState = paramInt4;
    localKeyEvent.mDeviceId = paramInt5;
    localKeyEvent.mScanCode = paramInt6;
    localKeyEvent.mFlags = paramInt7;
    localKeyEvent.mSource = paramInt8;
    localKeyEvent.mDisplayId = paramInt9;
    localKeyEvent.mCharacters = paramString;
    return localKeyEvent;
  }
  
  @UnsupportedAppUsage
  public static KeyEvent obtain(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, String paramString)
  {
    return obtain(paramLong1, paramLong2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, -1, paramString);
  }
  
  public static KeyEvent obtain(KeyEvent paramKeyEvent)
  {
    KeyEvent localKeyEvent = obtain();
    localKeyEvent.mDownTime = paramKeyEvent.mDownTime;
    localKeyEvent.mEventTime = paramKeyEvent.mEventTime;
    localKeyEvent.mAction = paramKeyEvent.mAction;
    localKeyEvent.mKeyCode = paramKeyEvent.mKeyCode;
    localKeyEvent.mRepeatCount = paramKeyEvent.mRepeatCount;
    localKeyEvent.mMetaState = paramKeyEvent.mMetaState;
    localKeyEvent.mDeviceId = paramKeyEvent.mDeviceId;
    localKeyEvent.mScanCode = paramKeyEvent.mScanCode;
    localKeyEvent.mFlags = paramKeyEvent.mFlags;
    localKeyEvent.mSource = paramKeyEvent.mSource;
    localKeyEvent.mDisplayId = paramKeyEvent.mDisplayId;
    localKeyEvent.mCharacters = paramKeyEvent.mCharacters;
    return localKeyEvent;
  }
  
  public final void cancel()
  {
    this.mFlags |= 0x20;
  }
  
  public KeyEvent copy()
  {
    return obtain(this);
  }
  
  @Deprecated
  public final boolean dispatch(Callback paramCallback)
  {
    return dispatch(paramCallback, null, null);
  }
  
  public final boolean dispatch(Callback paramCallback, DispatcherState paramDispatcherState, Object paramObject)
  {
    int i = this.mAction;
    if (i != 0)
    {
      if (i != 1)
      {
        if (i != 2) {
          return false;
        }
        int j = this.mRepeatCount;
        i = this.mKeyCode;
        if (paramCallback.onKeyMultiple(i, j, this)) {
          return true;
        }
        if (i != 0)
        {
          this.mAction = 0;
          this.mRepeatCount = 0;
          bool1 = paramCallback.onKeyDown(i, this);
          if (bool1)
          {
            this.mAction = 1;
            paramCallback.onKeyUp(i, this);
          }
          this.mAction = 2;
          this.mRepeatCount = j;
          return bool1;
        }
        return false;
      }
      if (paramDispatcherState != null) {
        paramDispatcherState.handleUpEvent(this);
      }
      return paramCallback.onKeyUp(this.mKeyCode, this);
    }
    this.mFlags &= 0xBFFFFFFF;
    boolean bool2 = paramCallback.onKeyDown(this.mKeyCode, this);
    boolean bool1 = bool2;
    if (paramDispatcherState != null) {
      if ((bool2) && (this.mRepeatCount == 0) && ((this.mFlags & 0x40000000) != 0))
      {
        paramDispatcherState.startTracking(this, paramObject);
        bool1 = bool2;
      }
      else
      {
        bool1 = bool2;
        if (isLongPress())
        {
          bool1 = bool2;
          if (paramDispatcherState.isTracking(this))
          {
            bool1 = bool2;
            try
            {
              if (paramCallback.onKeyLongPress(this.mKeyCode, this))
              {
                paramDispatcherState.performedLongPress(this);
                bool1 = true;
              }
            }
            catch (AbstractMethodError paramCallback)
            {
              bool1 = bool2;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public final int getAction()
  {
    return this.mAction;
  }
  
  @Deprecated
  public final String getCharacters()
  {
    return this.mCharacters;
  }
  
  public final int getDeviceId()
  {
    return this.mDeviceId;
  }
  
  public final int getDisplayId()
  {
    return this.mDisplayId;
  }
  
  public char getDisplayLabel()
  {
    return getKeyCharacterMap().getDisplayLabel(this.mKeyCode);
  }
  
  public final long getDownTime()
  {
    return this.mDownTime;
  }
  
  public final long getEventTime()
  {
    return this.mEventTime;
  }
  
  public final long getEventTimeNano()
  {
    return this.mEventTime * 1000000L;
  }
  
  public final int getFlags()
  {
    return this.mFlags;
  }
  
  public final KeyCharacterMap getKeyCharacterMap()
  {
    return KeyCharacterMap.load(this.mDeviceId);
  }
  
  public final int getKeyCode()
  {
    return this.mKeyCode;
  }
  
  @Deprecated
  public boolean getKeyData(KeyCharacterMap.KeyData paramKeyData)
  {
    return getKeyCharacterMap().getKeyData(this.mKeyCode, paramKeyData);
  }
  
  @Deprecated
  public final int getKeyboardDevice()
  {
    return this.mDeviceId;
  }
  
  public char getMatch(char[] paramArrayOfChar)
  {
    return getMatch(paramArrayOfChar, 0);
  }
  
  public char getMatch(char[] paramArrayOfChar, int paramInt)
  {
    return getKeyCharacterMap().getMatch(this.mKeyCode, paramArrayOfChar, paramInt);
  }
  
  public final int getMetaState()
  {
    return this.mMetaState;
  }
  
  public final int getModifiers()
  {
    return normalizeMetaState(this.mMetaState) & 0x770FF;
  }
  
  public char getNumber()
  {
    return getKeyCharacterMap().getNumber(this.mKeyCode);
  }
  
  public final int getRepeatCount()
  {
    return this.mRepeatCount;
  }
  
  public final int getScanCode()
  {
    return this.mScanCode;
  }
  
  public final int getSource()
  {
    return this.mSource;
  }
  
  public int getUnicodeChar()
  {
    return getUnicodeChar(this.mMetaState);
  }
  
  public int getUnicodeChar(int paramInt)
  {
    return getKeyCharacterMap().get(this.mKeyCode, paramInt);
  }
  
  public final boolean hasModifiers(int paramInt)
  {
    return metaStateHasModifiers(this.mMetaState, paramInt);
  }
  
  public final boolean hasNoModifiers()
  {
    return metaStateHasNoModifiers(this.mMetaState);
  }
  
  public final boolean isAltPressed()
  {
    boolean bool;
    if ((this.mMetaState & 0x2) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isCanceled()
  {
    boolean bool;
    if ((this.mFlags & 0x20) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isCapsLockOn()
  {
    boolean bool;
    if ((this.mMetaState & 0x100000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isCtrlPressed()
  {
    boolean bool;
    if ((this.mMetaState & 0x1000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @Deprecated
  @UnsupportedAppUsage
  public final boolean isDown()
  {
    boolean bool;
    if (this.mAction == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isFunctionPressed()
  {
    boolean bool;
    if ((this.mMetaState & 0x8) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isLongPress()
  {
    boolean bool;
    if ((this.mFlags & 0x80) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isMetaPressed()
  {
    boolean bool;
    if ((this.mMetaState & 0x10000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isNumLockOn()
  {
    boolean bool;
    if ((this.mMetaState & 0x200000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isPrintingKey()
  {
    return getKeyCharacterMap().isPrintingKey(this.mKeyCode);
  }
  
  public final boolean isScrollLockOn()
  {
    boolean bool;
    if ((this.mMetaState & 0x400000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isShiftPressed()
  {
    int i = this.mMetaState;
    boolean bool = true;
    if ((i & 0x1) == 0) {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isSymPressed()
  {
    boolean bool;
    if ((this.mMetaState & 0x4) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isSystem()
  {
    return isSystemKey(this.mKeyCode);
  }
  
  public final boolean isTainted()
  {
    boolean bool;
    if ((this.mFlags & 0x80000000) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isTracking()
  {
    boolean bool;
    if ((this.mFlags & 0x200) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isWakeKey()
  {
    return isWakeKey(this.mKeyCode);
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  public final void recycle()
  {
    super.recycle();
    this.mCharacters = null;
    synchronized (gRecyclerLock)
    {
      if (gRecyclerUsed < 10)
      {
        gRecyclerUsed += 1;
        this.mNext = gRecyclerTop;
        gRecyclerTop = this;
      }
      return;
    }
  }
  
  public final void recycleIfNeededAfterDispatch() {}
  
  public final void setDisplayId(int paramInt)
  {
    this.mDisplayId = paramInt;
  }
  
  public final void setFlags(int paramInt)
  {
    this.mFlags = paramInt;
  }
  
  public final void setSource(int paramInt)
  {
    this.mSource = paramInt;
  }
  
  public final void setTainted(boolean paramBoolean)
  {
    int i = this.mFlags;
    if (paramBoolean) {
      i |= 0x80000000;
    } else {
      i &= 0x7FFFFFFF;
    }
    this.mFlags = i;
  }
  
  public final void setTime(long paramLong1, long paramLong2)
  {
    this.mDownTime = paramLong1;
    this.mEventTime = paramLong2;
  }
  
  public final void startTracking()
  {
    this.mFlags |= 0x40000000;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("KeyEvent { action=");
    localStringBuilder.append(actionToString(this.mAction));
    localStringBuilder.append(", keyCode=");
    localStringBuilder.append(keyCodeToString(this.mKeyCode));
    localStringBuilder.append(", scanCode=");
    localStringBuilder.append(this.mScanCode);
    if (this.mCharacters != null)
    {
      localStringBuilder.append(", characters=\"");
      localStringBuilder.append(this.mCharacters);
      localStringBuilder.append("\"");
    }
    localStringBuilder.append(", metaState=");
    localStringBuilder.append(metaStateToString(this.mMetaState));
    localStringBuilder.append(", flags=0x");
    localStringBuilder.append(Integer.toHexString(this.mFlags));
    localStringBuilder.append(", repeatCount=");
    localStringBuilder.append(this.mRepeatCount);
    localStringBuilder.append(", eventTime=");
    localStringBuilder.append(this.mEventTime);
    localStringBuilder.append(", downTime=");
    localStringBuilder.append(this.mDownTime);
    localStringBuilder.append(", deviceId=");
    localStringBuilder.append(this.mDeviceId);
    localStringBuilder.append(", source=0x");
    localStringBuilder.append(Integer.toHexString(this.mSource));
    localStringBuilder.append(", displayId=");
    localStringBuilder.append(this.mDisplayId);
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(2);
    paramParcel.writeInt(this.mDeviceId);
    paramParcel.writeInt(this.mSource);
    paramParcel.writeInt(this.mDisplayId);
    paramParcel.writeInt(this.mAction);
    paramParcel.writeInt(this.mKeyCode);
    paramParcel.writeInt(this.mRepeatCount);
    paramParcel.writeInt(this.mMetaState);
    paramParcel.writeInt(this.mScanCode);
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeLong(this.mDownTime);
    paramParcel.writeLong(this.mEventTime);
    paramParcel.writeString(this.mCharacters);
  }
  
  public static abstract interface Callback
  {
    public abstract boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent);
    
    public abstract boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent);
    
    public abstract boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent);
    
    public abstract boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent);
  }
  
  public static class DispatcherState
  {
    SparseIntArray mActiveLongPresses = new SparseIntArray();
    int mDownKeyCode;
    Object mDownTarget;
    
    public void handleUpEvent(KeyEvent paramKeyEvent)
    {
      int i = paramKeyEvent.getKeyCode();
      int j = this.mActiveLongPresses.indexOfKey(i);
      if (j >= 0)
      {
        KeyEvent.access$076(paramKeyEvent, 288);
        this.mActiveLongPresses.removeAt(j);
      }
      if (this.mDownKeyCode == i)
      {
        KeyEvent.access$076(paramKeyEvent, 512);
        this.mDownKeyCode = 0;
        this.mDownTarget = null;
      }
    }
    
    public boolean isTracking(KeyEvent paramKeyEvent)
    {
      boolean bool;
      if (this.mDownKeyCode == paramKeyEvent.getKeyCode()) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void performedLongPress(KeyEvent paramKeyEvent)
    {
      this.mActiveLongPresses.put(paramKeyEvent.getKeyCode(), 1);
    }
    
    public void reset()
    {
      this.mDownKeyCode = 0;
      this.mDownTarget = null;
      this.mActiveLongPresses.clear();
    }
    
    public void reset(Object paramObject)
    {
      if (this.mDownTarget == paramObject)
      {
        this.mDownKeyCode = 0;
        this.mDownTarget = null;
      }
    }
    
    public void startTracking(KeyEvent paramKeyEvent, Object paramObject)
    {
      if (paramKeyEvent.getAction() == 0)
      {
        this.mDownKeyCode = paramKeyEvent.getKeyCode();
        this.mDownTarget = paramObject;
        return;
      }
      throw new IllegalArgumentException("Can only start tracking on a down event");
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/KeyEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */