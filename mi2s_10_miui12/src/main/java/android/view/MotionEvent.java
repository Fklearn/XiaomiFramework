package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Matrix;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.util.SparseArray;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class MotionEvent
  extends InputEvent
  implements Parcelable
{
  public static final int ACTION_BUTTON_PRESS = 11;
  public static final int ACTION_BUTTON_RELEASE = 12;
  public static final int ACTION_CANCEL = 3;
  public static final int ACTION_DOWN = 0;
  public static final int ACTION_HOVER_ENTER = 9;
  public static final int ACTION_HOVER_EXIT = 10;
  public static final int ACTION_HOVER_MOVE = 7;
  public static final int ACTION_MASK = 255;
  public static final int ACTION_MOVE = 2;
  public static final int ACTION_OUTSIDE = 4;
  @Deprecated
  public static final int ACTION_POINTER_1_DOWN = 5;
  @Deprecated
  public static final int ACTION_POINTER_1_UP = 6;
  @Deprecated
  public static final int ACTION_POINTER_2_DOWN = 261;
  @Deprecated
  public static final int ACTION_POINTER_2_UP = 262;
  @Deprecated
  public static final int ACTION_POINTER_3_DOWN = 517;
  @Deprecated
  public static final int ACTION_POINTER_3_UP = 518;
  public static final int ACTION_POINTER_DOWN = 5;
  @Deprecated
  public static final int ACTION_POINTER_ID_MASK = 65280;
  @Deprecated
  public static final int ACTION_POINTER_ID_SHIFT = 8;
  public static final int ACTION_POINTER_INDEX_MASK = 65280;
  public static final int ACTION_POINTER_INDEX_SHIFT = 8;
  public static final int ACTION_POINTER_UP = 6;
  public static final int ACTION_SCROLL = 8;
  public static final int ACTION_UP = 1;
  public static final int AXIS_BRAKE = 23;
  public static final int AXIS_DISTANCE = 24;
  public static final int AXIS_GAS = 22;
  public static final int AXIS_GENERIC_1 = 32;
  public static final int AXIS_GENERIC_10 = 41;
  public static final int AXIS_GENERIC_11 = 42;
  public static final int AXIS_GENERIC_12 = 43;
  public static final int AXIS_GENERIC_13 = 44;
  public static final int AXIS_GENERIC_14 = 45;
  public static final int AXIS_GENERIC_15 = 46;
  public static final int AXIS_GENERIC_16 = 47;
  public static final int AXIS_GENERIC_2 = 33;
  public static final int AXIS_GENERIC_3 = 34;
  public static final int AXIS_GENERIC_4 = 35;
  public static final int AXIS_GENERIC_5 = 36;
  public static final int AXIS_GENERIC_6 = 37;
  public static final int AXIS_GENERIC_7 = 38;
  public static final int AXIS_GENERIC_8 = 39;
  public static final int AXIS_GENERIC_9 = 40;
  public static final int AXIS_HAT_X = 15;
  public static final int AXIS_HAT_Y = 16;
  public static final int AXIS_HSCROLL = 10;
  public static final int AXIS_LTRIGGER = 17;
  public static final int AXIS_ORIENTATION = 8;
  public static final int AXIS_PRESSURE = 2;
  public static final int AXIS_RELATIVE_X = 27;
  public static final int AXIS_RELATIVE_Y = 28;
  public static final int AXIS_RTRIGGER = 18;
  public static final int AXIS_RUDDER = 20;
  public static final int AXIS_RX = 12;
  public static final int AXIS_RY = 13;
  public static final int AXIS_RZ = 14;
  public static final int AXIS_SCROLL = 26;
  public static final int AXIS_SIZE = 3;
  private static final SparseArray<String> AXIS_SYMBOLIC_NAMES = new SparseArray();
  public static final int AXIS_THROTTLE = 19;
  public static final int AXIS_TILT = 25;
  public static final int AXIS_TOOL_MAJOR = 6;
  public static final int AXIS_TOOL_MINOR = 7;
  public static final int AXIS_TOUCH_MAJOR = 4;
  public static final int AXIS_TOUCH_MINOR = 5;
  public static final int AXIS_VSCROLL = 9;
  public static final int AXIS_WHEEL = 21;
  public static final int AXIS_X = 0;
  public static final int AXIS_Y = 1;
  public static final int AXIS_Z = 11;
  public static final int BUTTON_BACK = 8;
  public static final int BUTTON_FORWARD = 16;
  public static final int BUTTON_PRIMARY = 1;
  public static final int BUTTON_SECONDARY = 2;
  public static final int BUTTON_STYLUS_PRIMARY = 32;
  public static final int BUTTON_STYLUS_SECONDARY = 64;
  private static final String[] BUTTON_SYMBOLIC_NAMES;
  public static final int BUTTON_TERTIARY = 4;
  public static final int CLASSIFICATION_AMBIGUOUS_GESTURE = 1;
  public static final int CLASSIFICATION_DEEP_PRESS = 2;
  public static final int CLASSIFICATION_NONE = 0;
  public static final Parcelable.Creator<MotionEvent> CREATOR = new Parcelable.Creator()
  {
    public MotionEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      paramAnonymousParcel.readInt();
      return MotionEvent.createFromParcelBody(paramAnonymousParcel);
    }
    
    public MotionEvent[] newArray(int paramAnonymousInt)
    {
      return new MotionEvent[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG_CONCISE_TOSTRING = false;
  public static final int EDGE_BOTTOM = 2;
  public static final int EDGE_LEFT = 4;
  public static final int EDGE_RIGHT = 8;
  public static final int EDGE_TOP = 1;
  public static final int FLAG_HOVER_EXIT_PENDING = 4;
  public static final int FLAG_IS_GENERATED_GESTURE = 8;
  public static final int FLAG_TAINTED = Integer.MIN_VALUE;
  public static final int FLAG_TARGET_ACCESSIBILITY_FOCUS = 1073741824;
  public static final int FLAG_WINDOW_IS_OBSCURED = 1;
  public static final int FLAG_WINDOW_IS_PARTIALLY_OBSCURED = 2;
  @UnsupportedAppUsage
  private static final int HISTORY_CURRENT = Integer.MIN_VALUE;
  public static final int INVALID_POINTER_ID = -1;
  private static final String LABEL_PREFIX = "AXIS_";
  private static final int MAX_RECYCLED = 10;
  private static final long NS_PER_MS = 1000000L;
  private static final String TAG = "MotionEvent";
  public static final int TOOL_TYPE_ERASER = 4;
  public static final int TOOL_TYPE_FINGER = 1;
  public static final int TOOL_TYPE_MOUSE = 3;
  public static final int TOOL_TYPE_STYLUS = 2;
  private static final SparseArray<String> TOOL_TYPE_SYMBOLIC_NAMES;
  public static final int TOOL_TYPE_UNKNOWN = 0;
  private static final Object gRecyclerLock;
  private static MotionEvent gRecyclerTop;
  private static int gRecyclerUsed;
  private static final Object gSharedTempLock;
  private static PointerCoords[] gSharedTempPointerCoords;
  private static int[] gSharedTempPointerIndexMap;
  private static PointerProperties[] gSharedTempPointerProperties;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private long mNativePtr;
  private MotionEvent mNext;
  
  static
  {
    SparseArray localSparseArray = AXIS_SYMBOLIC_NAMES;
    localSparseArray.append(0, "AXIS_X");
    localSparseArray.append(1, "AXIS_Y");
    localSparseArray.append(2, "AXIS_PRESSURE");
    localSparseArray.append(3, "AXIS_SIZE");
    localSparseArray.append(4, "AXIS_TOUCH_MAJOR");
    localSparseArray.append(5, "AXIS_TOUCH_MINOR");
    localSparseArray.append(6, "AXIS_TOOL_MAJOR");
    localSparseArray.append(7, "AXIS_TOOL_MINOR");
    localSparseArray.append(8, "AXIS_ORIENTATION");
    localSparseArray.append(9, "AXIS_VSCROLL");
    localSparseArray.append(10, "AXIS_HSCROLL");
    localSparseArray.append(11, "AXIS_Z");
    localSparseArray.append(12, "AXIS_RX");
    localSparseArray.append(13, "AXIS_RY");
    localSparseArray.append(14, "AXIS_RZ");
    localSparseArray.append(15, "AXIS_HAT_X");
    localSparseArray.append(16, "AXIS_HAT_Y");
    localSparseArray.append(17, "AXIS_LTRIGGER");
    localSparseArray.append(18, "AXIS_RTRIGGER");
    localSparseArray.append(19, "AXIS_THROTTLE");
    localSparseArray.append(20, "AXIS_RUDDER");
    localSparseArray.append(21, "AXIS_WHEEL");
    localSparseArray.append(22, "AXIS_GAS");
    localSparseArray.append(23, "AXIS_BRAKE");
    localSparseArray.append(24, "AXIS_DISTANCE");
    localSparseArray.append(25, "AXIS_TILT");
    localSparseArray.append(26, "AXIS_SCROLL");
    localSparseArray.append(27, "AXIS_REALTIVE_X");
    localSparseArray.append(28, "AXIS_REALTIVE_Y");
    localSparseArray.append(32, "AXIS_GENERIC_1");
    localSparseArray.append(33, "AXIS_GENERIC_2");
    localSparseArray.append(34, "AXIS_GENERIC_3");
    localSparseArray.append(35, "AXIS_GENERIC_4");
    localSparseArray.append(36, "AXIS_GENERIC_5");
    localSparseArray.append(37, "AXIS_GENERIC_6");
    localSparseArray.append(38, "AXIS_GENERIC_7");
    localSparseArray.append(39, "AXIS_GENERIC_8");
    localSparseArray.append(40, "AXIS_GENERIC_9");
    localSparseArray.append(41, "AXIS_GENERIC_10");
    localSparseArray.append(42, "AXIS_GENERIC_11");
    localSparseArray.append(43, "AXIS_GENERIC_12");
    localSparseArray.append(44, "AXIS_GENERIC_13");
    localSparseArray.append(45, "AXIS_GENERIC_14");
    localSparseArray.append(46, "AXIS_GENERIC_15");
    localSparseArray.append(47, "AXIS_GENERIC_16");
    BUTTON_SYMBOLIC_NAMES = new String[] { "BUTTON_PRIMARY", "BUTTON_SECONDARY", "BUTTON_TERTIARY", "BUTTON_BACK", "BUTTON_FORWARD", "BUTTON_STYLUS_PRIMARY", "BUTTON_STYLUS_SECONDARY", "0x00000080", "0x00000100", "0x00000200", "0x00000400", "0x00000800", "0x00001000", "0x00002000", "0x00004000", "0x00008000", "0x00010000", "0x00020000", "0x00040000", "0x00080000", "0x00100000", "0x00200000", "0x00400000", "0x00800000", "0x01000000", "0x02000000", "0x04000000", "0x08000000", "0x10000000", "0x20000000", "0x40000000", "0x80000000" };
    TOOL_TYPE_SYMBOLIC_NAMES = new SparseArray();
    localSparseArray = TOOL_TYPE_SYMBOLIC_NAMES;
    localSparseArray.append(0, "TOOL_TYPE_UNKNOWN");
    localSparseArray.append(1, "TOOL_TYPE_FINGER");
    localSparseArray.append(2, "TOOL_TYPE_STYLUS");
    localSparseArray.append(3, "TOOL_TYPE_MOUSE");
    localSparseArray.append(4, "TOOL_TYPE_ERASER");
    gRecyclerLock = new Object();
    gSharedTempLock = new Object();
  }
  
  public static String actionToString(int paramInt)
  {
    int i;
    int j;
    switch (paramInt)
    {
    case 5: 
    case 6: 
    default: 
      i = (0xFF00 & paramInt) >> 8;
      j = paramInt & 0xFF;
      break;
    case 12: 
      return "ACTION_BUTTON_RELEASE";
    case 11: 
      return "ACTION_BUTTON_PRESS";
    case 10: 
      return "ACTION_HOVER_EXIT";
    case 9: 
      return "ACTION_HOVER_ENTER";
    case 8: 
      return "ACTION_SCROLL";
    case 7: 
      return "ACTION_HOVER_MOVE";
    case 4: 
      return "ACTION_OUTSIDE";
    case 3: 
      return "ACTION_CANCEL";
    case 2: 
      return "ACTION_MOVE";
    case 1: 
      return "ACTION_UP";
    case 0: 
      return "ACTION_DOWN";
    }
    if (j != 5)
    {
      if (j != 6) {
        return Integer.toString(paramInt);
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("ACTION_POINTER_UP(");
      localStringBuilder.append(i);
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ACTION_POINTER_DOWN(");
    localStringBuilder.append(i);
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  private static <T> void appendUnless(T paramT1, StringBuilder paramStringBuilder, String paramString, T paramT2)
  {
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(paramT2);
  }
  
  public static int axisFromString(String paramString)
  {
    String str = paramString;
    int i;
    if (paramString.startsWith("AXIS_"))
    {
      str = paramString.substring("AXIS_".length());
      i = nativeAxisFromString(str);
      if (i >= 0) {
        return i;
      }
    }
    try
    {
      i = Integer.parseInt(str, 10);
      return i;
    }
    catch (NumberFormatException paramString) {}
    return -1;
  }
  
  public static String axisToString(int paramInt)
  {
    String str = nativeAxisToString(paramInt);
    if (str != null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("AXIS_");
      localStringBuilder.append(str);
      str = localStringBuilder.toString();
    }
    else
    {
      str = Integer.toString(paramInt);
    }
    return str;
  }
  
  public static String buttonStateToString(int paramInt)
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
        localObject2 = BUTTON_SYMBOLIC_NAMES[paramInt];
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
  
  private static final float clamp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    return paramFloat1;
  }
  
  public static String classificationToString(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2) {
          return "NONE";
        }
        return "DEEP_PRESS";
      }
      return "AMBIGUOUS_GESTURE";
    }
    return "NONE";
  }
  
  public static MotionEvent createFromParcelBody(Parcel paramParcel)
  {
    MotionEvent localMotionEvent = obtain();
    localMotionEvent.mNativePtr = nativeReadFromParcel(localMotionEvent.mNativePtr, paramParcel);
    return localMotionEvent;
  }
  
  private static final void ensureSharedTempPointerCapacity(int paramInt)
  {
    PointerCoords[] arrayOfPointerCoords = gSharedTempPointerCoords;
    if ((arrayOfPointerCoords == null) || (arrayOfPointerCoords.length < paramInt))
    {
      arrayOfPointerCoords = gSharedTempPointerCoords;
      int i;
      if (arrayOfPointerCoords != null) {
        i = arrayOfPointerCoords.length;
      } else {
        i = 8;
      }
      while (i < paramInt) {
        i *= 2;
      }
      gSharedTempPointerCoords = PointerCoords.createArray(i);
      gSharedTempPointerProperties = PointerProperties.createArray(i);
      gSharedTempPointerIndexMap = new int[i];
    }
  }
  
  private static native void nativeAddBatch(long paramLong1, long paramLong2, PointerCoords[] paramArrayOfPointerCoords, int paramInt);
  
  private static native int nativeAxisFromString(String paramString);
  
  private static native String nativeAxisToString(int paramInt);
  
  @CriticalNative
  private static native long nativeCopy(long paramLong1, long paramLong2, boolean paramBoolean);
  
  private static native void nativeDispose(long paramLong);
  
  @CriticalNative
  private static native int nativeFindPointerIndex(long paramLong, int paramInt);
  
  @CriticalNative
  private static native int nativeGetAction(long paramLong);
  
  @CriticalNative
  private static native int nativeGetActionButton(long paramLong);
  
  @FastNative
  private static native float nativeGetAxisValue(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  @CriticalNative
  private static native int nativeGetButtonState(long paramLong);
  
  @CriticalNative
  private static native int nativeGetClassification(long paramLong);
  
  @CriticalNative
  private static native int nativeGetDeviceId(long paramLong);
  
  @CriticalNative
  private static native int nativeGetDisplayId(long paramLong);
  
  @CriticalNative
  private static native long nativeGetDownTimeNanos(long paramLong);
  
  @CriticalNative
  private static native int nativeGetEdgeFlags(long paramLong);
  
  @FastNative
  private static native long nativeGetEventTimeNanos(long paramLong, int paramInt);
  
  @CriticalNative
  private static native int nativeGetFlags(long paramLong);
  
  @CriticalNative
  private static native int nativeGetHistorySize(long paramLong);
  
  @CriticalNative
  private static native int nativeGetMetaState(long paramLong);
  
  private static native void nativeGetPointerCoords(long paramLong, int paramInt1, int paramInt2, PointerCoords paramPointerCoords);
  
  @CriticalNative
  private static native int nativeGetPointerCount(long paramLong);
  
  @FastNative
  private static native int nativeGetPointerId(long paramLong, int paramInt);
  
  private static native void nativeGetPointerProperties(long paramLong, int paramInt, PointerProperties paramPointerProperties);
  
  @UnsupportedAppUsage
  @FastNative
  private static native float nativeGetRawAxisValue(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  @CriticalNative
  private static native int nativeGetSource(long paramLong);
  
  @FastNative
  private static native int nativeGetToolType(long paramLong, int paramInt);
  
  @CriticalNative
  private static native float nativeGetXOffset(long paramLong);
  
  @CriticalNative
  private static native float nativeGetXPrecision(long paramLong);
  
  @CriticalNative
  private static native float nativeGetYOffset(long paramLong);
  
  @CriticalNative
  private static native float nativeGetYPrecision(long paramLong);
  
  private static native long nativeInitialize(long paramLong1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, long paramLong2, long paramLong3, int paramInt10, PointerProperties[] paramArrayOfPointerProperties, PointerCoords[] paramArrayOfPointerCoords);
  
  @CriticalNative
  private static native boolean nativeIsTouchEvent(long paramLong);
  
  @CriticalNative
  private static native void nativeOffsetLocation(long paramLong, float paramFloat1, float paramFloat2);
  
  private static native long nativeReadFromParcel(long paramLong, Parcel paramParcel);
  
  @CriticalNative
  private static native void nativeScale(long paramLong, float paramFloat);
  
  @CriticalNative
  private static native void nativeSetAction(long paramLong, int paramInt);
  
  @CriticalNative
  private static native void nativeSetActionButton(long paramLong, int paramInt);
  
  @CriticalNative
  private static native void nativeSetButtonState(long paramLong, int paramInt);
  
  @CriticalNative
  private static native void nativeSetDisplayId(long paramLong, int paramInt);
  
  @CriticalNative
  private static native void nativeSetDownTimeNanos(long paramLong1, long paramLong2);
  
  @CriticalNative
  private static native void nativeSetEdgeFlags(long paramLong, int paramInt);
  
  @CriticalNative
  private static native void nativeSetFlags(long paramLong, int paramInt);
  
  @CriticalNative
  private static native void nativeSetSource(long paramLong, int paramInt);
  
  @CriticalNative
  private static native void nativeTransform(long paramLong1, long paramLong2);
  
  private static native void nativeWriteToParcel(long paramLong, Parcel paramParcel);
  
  @UnsupportedAppUsage
  private static MotionEvent obtain()
  {
    synchronized (gRecyclerLock)
    {
      MotionEvent localMotionEvent = gRecyclerTop;
      if (localMotionEvent == null)
      {
        localMotionEvent = new android/view/MotionEvent;
        localMotionEvent.<init>();
        return localMotionEvent;
      }
      gRecyclerTop = localMotionEvent.mNext;
      gRecyclerUsed -= 1;
      localMotionEvent.mNext = null;
      localMotionEvent.prepareForReuse();
      return localMotionEvent;
    }
  }
  
  public static MotionEvent obtain(long paramLong1, long paramLong2, int paramInt1, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt2, float paramFloat5, float paramFloat6, int paramInt3, int paramInt4)
  {
    return obtain(paramLong1, paramLong2, paramInt1, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt2, paramFloat5, paramFloat6, paramInt3, paramInt4, 0, 0);
  }
  
  public static MotionEvent obtain(long paramLong1, long paramLong2, int paramInt1, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt2, float paramFloat5, float paramFloat6, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    MotionEvent localMotionEvent = obtain();
    synchronized (gSharedTempLock)
    {
      ensureSharedTempPointerCapacity(1);
      PointerProperties[] arrayOfPointerProperties = gSharedTempPointerProperties;
      arrayOfPointerProperties[0].clear();
      arrayOfPointerProperties[0].id = 0;
      PointerCoords[] arrayOfPointerCoords = gSharedTempPointerCoords;
      arrayOfPointerCoords[0].clear();
      arrayOfPointerCoords[0].x = paramFloat1;
      arrayOfPointerCoords[0].y = paramFloat2;
      arrayOfPointerCoords[0].pressure = paramFloat3;
      arrayOfPointerCoords[0].size = paramFloat4;
      localMotionEvent.mNativePtr = nativeInitialize(localMotionEvent.mNativePtr, paramInt3, paramInt5, paramInt6, paramInt1, 0, paramInt4, paramInt2, 0, 0, 0.0F, 0.0F, paramFloat5, paramFloat6, paramLong1 * 1000000L, paramLong2 * 1000000L, 1, arrayOfPointerProperties, arrayOfPointerCoords);
      return localMotionEvent;
    }
  }
  
  public static MotionEvent obtain(long paramLong1, long paramLong2, int paramInt1, float paramFloat1, float paramFloat2, int paramInt2)
  {
    return obtain(paramLong1, paramLong2, paramInt1, paramFloat1, paramFloat2, 1.0F, 1.0F, paramInt2, 1.0F, 1.0F, 0, 0);
  }
  
  @Deprecated
  public static MotionEvent obtain(long paramLong1, long paramLong2, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt3, float paramFloat5, float paramFloat6, int paramInt4, int paramInt5)
  {
    return obtain(paramLong1, paramLong2, paramInt1, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt3, paramFloat5, paramFloat6, paramInt4, paramInt5);
  }
  
  @Deprecated
  public static MotionEvent obtain(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int[] paramArrayOfInt, PointerCoords[] paramArrayOfPointerCoords, int paramInt3, float paramFloat1, float paramFloat2, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    synchronized (gSharedTempLock)
    {
      ensureSharedTempPointerCapacity(paramInt2);
      PointerProperties[] arrayOfPointerProperties = gSharedTempPointerProperties;
      for (int i = 0; i < paramInt2; i++)
      {
        arrayOfPointerProperties[i].clear();
        arrayOfPointerProperties[i].id = paramArrayOfInt[i];
      }
      paramArrayOfInt = obtain(paramLong1, paramLong2, paramInt1, paramInt2, arrayOfPointerProperties, paramArrayOfPointerCoords, paramInt3, 0, paramFloat1, paramFloat2, paramInt4, paramInt5, paramInt6, paramInt7);
      return paramArrayOfInt;
    }
  }
  
  public static MotionEvent obtain(long paramLong1, long paramLong2, int paramInt1, int paramInt2, PointerProperties[] paramArrayOfPointerProperties, PointerCoords[] paramArrayOfPointerCoords, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    return obtain(paramLong1, paramLong2, paramInt1, paramInt2, paramArrayOfPointerProperties, paramArrayOfPointerCoords, paramInt3, paramInt4, paramFloat1, paramFloat2, paramInt5, paramInt6, paramInt7, 0, paramInt8);
  }
  
  public static MotionEvent obtain(long paramLong1, long paramLong2, int paramInt1, int paramInt2, PointerProperties[] paramArrayOfPointerProperties, PointerCoords[] paramArrayOfPointerCoords, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
  {
    MotionEvent localMotionEvent = obtain();
    localMotionEvent.mNativePtr = nativeInitialize(localMotionEvent.mNativePtr, paramInt5, paramInt7, paramInt8, paramInt1, paramInt9, paramInt6, paramInt3, paramInt4, 0, 0.0F, 0.0F, paramFloat1, paramFloat2, paramLong1 * 1000000L, paramLong2 * 1000000L, paramInt2, paramArrayOfPointerProperties, paramArrayOfPointerCoords);
    if (localMotionEvent.mNativePtr == 0L)
    {
      Log.e("MotionEvent", "Could not initialize MotionEvent");
      localMotionEvent.recycle();
      return null;
    }
    return localMotionEvent;
  }
  
  public static MotionEvent obtain(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent != null)
    {
      MotionEvent localMotionEvent = obtain();
      localMotionEvent.mNativePtr = nativeCopy(localMotionEvent.mNativePtr, paramMotionEvent.mNativePtr, true);
      return localMotionEvent;
    }
    throw new IllegalArgumentException("other motion event must not be null");
  }
  
  public static MotionEvent obtainNoHistory(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent != null)
    {
      MotionEvent localMotionEvent = obtain();
      localMotionEvent.mNativePtr = nativeCopy(localMotionEvent.mNativePtr, paramMotionEvent.mNativePtr, false);
      return localMotionEvent;
    }
    throw new IllegalArgumentException("other motion event must not be null");
  }
  
  public static String toolTypeToString(int paramInt)
  {
    String str = (String)TOOL_TYPE_SYMBOLIC_NAMES.get(paramInt);
    if (str == null) {
      str = Integer.toString(paramInt);
    }
    return str;
  }
  
  public final void addBatch(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt)
  {
    synchronized (gSharedTempLock)
    {
      ensureSharedTempPointerCapacity(1);
      PointerCoords[] arrayOfPointerCoords = gSharedTempPointerCoords;
      arrayOfPointerCoords[0].clear();
      arrayOfPointerCoords[0].x = paramFloat1;
      arrayOfPointerCoords[0].y = paramFloat2;
      arrayOfPointerCoords[0].pressure = paramFloat3;
      arrayOfPointerCoords[0].size = paramFloat4;
      nativeAddBatch(this.mNativePtr, 1000000L * paramLong, arrayOfPointerCoords, paramInt);
      return;
    }
  }
  
  public final void addBatch(long paramLong, PointerCoords[] paramArrayOfPointerCoords, int paramInt)
  {
    nativeAddBatch(this.mNativePtr, 1000000L * paramLong, paramArrayOfPointerCoords, paramInt);
  }
  
  @UnsupportedAppUsage
  public final boolean addBatch(MotionEvent paramMotionEvent)
  {
    int i = nativeGetAction(this.mNativePtr);
    if ((i != 2) && (i != 7)) {
      return false;
    }
    if (i != nativeGetAction(paramMotionEvent.mNativePtr)) {
      return false;
    }
    if ((nativeGetDeviceId(this.mNativePtr) == nativeGetDeviceId(paramMotionEvent.mNativePtr)) && (nativeGetSource(this.mNativePtr) == nativeGetSource(paramMotionEvent.mNativePtr)) && (nativeGetDisplayId(this.mNativePtr) == nativeGetDisplayId(paramMotionEvent.mNativePtr)) && (nativeGetFlags(this.mNativePtr) == nativeGetFlags(paramMotionEvent.mNativePtr)) && (nativeGetClassification(this.mNativePtr) == nativeGetClassification(paramMotionEvent.mNativePtr)))
    {
      int j = nativeGetPointerCount(this.mNativePtr);
      if (j != nativeGetPointerCount(paramMotionEvent.mNativePtr)) {
        return false;
      }
      synchronized (gSharedTempLock)
      {
        ensureSharedTempPointerCapacity(Math.max(j, 2));
        PointerProperties[] arrayOfPointerProperties = gSharedTempPointerProperties;
        PointerCoords[] arrayOfPointerCoords = gSharedTempPointerCoords;
        for (i = 0; i < j; i++)
        {
          nativeGetPointerProperties(this.mNativePtr, i, arrayOfPointerProperties[0]);
          nativeGetPointerProperties(paramMotionEvent.mNativePtr, i, arrayOfPointerProperties[1]);
          if (!arrayOfPointerProperties[0].equals(arrayOfPointerProperties[1])) {
            return false;
          }
        }
        int k = nativeGetMetaState(paramMotionEvent.mNativePtr);
        int m = nativeGetHistorySize(paramMotionEvent.mNativePtr);
        for (i = 0; i <= m; i++)
        {
          int n;
          if (i == m) {
            n = Integer.MIN_VALUE;
          } else {
            n = i;
          }
          for (int i1 = 0; i1 < j; i1++) {
            nativeGetPointerCoords(paramMotionEvent.mNativePtr, i1, n, arrayOfPointerCoords[i1]);
          }
          long l = nativeGetEventTimeNanos(paramMotionEvent.mNativePtr, n);
          nativeAddBatch(this.mNativePtr, l, arrayOfPointerCoords, k);
        }
        return true;
      }
    }
    return false;
  }
  
  public final void cancel()
  {
    setAction(3);
  }
  
  public final MotionEvent clampNoHistory(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    MotionEvent localMotionEvent = obtain();
    label286:
    synchronized (gSharedTempLock)
    {
      int i = nativeGetPointerCount(this.mNativePtr);
      ensureSharedTempPointerCapacity(i);
      PointerProperties[] arrayOfPointerProperties = gSharedTempPointerProperties;
      PointerCoords[] arrayOfPointerCoords1 = gSharedTempPointerCoords;
      int j = 0;
      for (;;)
      {
        if (j < i) {
          try
          {
            nativeGetPointerProperties(this.mNativePtr, j, arrayOfPointerProperties[j]);
            nativeGetPointerCoords(this.mNativePtr, j, Integer.MIN_VALUE, arrayOfPointerCoords1[j]);
            PointerCoords localPointerCoords = arrayOfPointerCoords1[j];
            float f = arrayOfPointerCoords1[j].x;
            try
            {
              localPointerCoords.x = clamp(f, paramFloat1, paramFloat3);
              localPointerCoords = arrayOfPointerCoords1[j];
              f = arrayOfPointerCoords1[j].y;
              localPointerCoords.y = clamp(f, paramFloat2, paramFloat4);
              j++;
            }
            finally {}
            break label286;
          }
          finally {}
        }
      }
      localMotionEvent.mNativePtr = nativeInitialize(localMotionEvent.mNativePtr, nativeGetDeviceId(this.mNativePtr), nativeGetSource(this.mNativePtr), nativeGetDisplayId(this.mNativePtr), nativeGetAction(this.mNativePtr), nativeGetFlags(this.mNativePtr), nativeGetEdgeFlags(this.mNativePtr), nativeGetMetaState(this.mNativePtr), nativeGetButtonState(this.mNativePtr), nativeGetClassification(this.mNativePtr), nativeGetXOffset(this.mNativePtr), nativeGetYOffset(this.mNativePtr), nativeGetXPrecision(this.mNativePtr), nativeGetYPrecision(this.mNativePtr), nativeGetDownTimeNanos(this.mNativePtr), nativeGetEventTimeNanos(this.mNativePtr, Integer.MIN_VALUE), i, arrayOfPointerProperties, arrayOfPointerCoords2);
      return localMotionEvent;
    }
  }
  
  @UnsupportedAppUsage
  public MotionEvent copy()
  {
    return obtain(this);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mNativePtr != 0L)
      {
        nativeDispose(this.mNativePtr);
        this.mNativePtr = 0L;
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public final int findPointerIndex(int paramInt)
  {
    return nativeFindPointerIndex(this.mNativePtr, paramInt);
  }
  
  public final int getAction()
  {
    return nativeGetAction(this.mNativePtr);
  }
  
  public final int getActionButton()
  {
    return nativeGetActionButton(this.mNativePtr);
  }
  
  public final int getActionIndex()
  {
    return (nativeGetAction(this.mNativePtr) & 0xFF00) >> 8;
  }
  
  public final int getActionMasked()
  {
    return nativeGetAction(this.mNativePtr) & 0xFF;
  }
  
  public final float getAxisValue(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, paramInt, 0, Integer.MIN_VALUE);
  }
  
  public final float getAxisValue(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, paramInt1, paramInt2, Integer.MIN_VALUE);
  }
  
  public final int getButtonState()
  {
    return nativeGetButtonState(this.mNativePtr);
  }
  
  public int getClassification()
  {
    return nativeGetClassification(this.mNativePtr);
  }
  
  public final int getDeviceId()
  {
    return nativeGetDeviceId(this.mNativePtr);
  }
  
  public int getDisplayId()
  {
    return nativeGetDisplayId(this.mNativePtr);
  }
  
  public final long getDownTime()
  {
    return nativeGetDownTimeNanos(this.mNativePtr) / 1000000L;
  }
  
  public final int getEdgeFlags()
  {
    return nativeGetEdgeFlags(this.mNativePtr);
  }
  
  public final long getEventTime()
  {
    return nativeGetEventTimeNanos(this.mNativePtr, Integer.MIN_VALUE) / 1000000L;
  }
  
  @UnsupportedAppUsage
  public final long getEventTimeNano()
  {
    return nativeGetEventTimeNanos(this.mNativePtr, Integer.MIN_VALUE);
  }
  
  public final int getFlags()
  {
    return nativeGetFlags(this.mNativePtr);
  }
  
  public final float getHistoricalAxisValue(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, paramInt1, 0, paramInt2);
  }
  
  public final float getHistoricalAxisValue(int paramInt1, int paramInt2, int paramInt3)
  {
    return nativeGetAxisValue(this.mNativePtr, paramInt1, paramInt2, paramInt3);
  }
  
  public final long getHistoricalEventTime(int paramInt)
  {
    return nativeGetEventTimeNanos(this.mNativePtr, paramInt) / 1000000L;
  }
  
  public final long getHistoricalEventTimeNano(int paramInt)
  {
    return nativeGetEventTimeNanos(this.mNativePtr, paramInt);
  }
  
  public final float getHistoricalOrientation(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 8, 0, paramInt);
  }
  
  public final float getHistoricalOrientation(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 8, paramInt1, paramInt2);
  }
  
  public final void getHistoricalPointerCoords(int paramInt1, int paramInt2, PointerCoords paramPointerCoords)
  {
    nativeGetPointerCoords(this.mNativePtr, paramInt1, paramInt2, paramPointerCoords);
  }
  
  public final float getHistoricalPressure(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 2, 0, paramInt);
  }
  
  public final float getHistoricalPressure(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 2, paramInt1, paramInt2);
  }
  
  public final float getHistoricalSize(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 3, 0, paramInt);
  }
  
  public final float getHistoricalSize(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 3, paramInt1, paramInt2);
  }
  
  public final float getHistoricalToolMajor(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 6, 0, paramInt);
  }
  
  public final float getHistoricalToolMajor(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 6, paramInt1, paramInt2);
  }
  
  public final float getHistoricalToolMinor(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 7, 0, paramInt);
  }
  
  public final float getHistoricalToolMinor(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 7, paramInt1, paramInt2);
  }
  
  public final float getHistoricalTouchMajor(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 4, 0, paramInt);
  }
  
  public final float getHistoricalTouchMajor(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 4, paramInt1, paramInt2);
  }
  
  public final float getHistoricalTouchMinor(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 5, 0, paramInt);
  }
  
  public final float getHistoricalTouchMinor(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 5, paramInt1, paramInt2);
  }
  
  public final float getHistoricalX(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 0, 0, paramInt);
  }
  
  public final float getHistoricalX(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 0, paramInt1, paramInt2);
  }
  
  public final float getHistoricalY(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 1, 0, paramInt);
  }
  
  public final float getHistoricalY(int paramInt1, int paramInt2)
  {
    return nativeGetAxisValue(this.mNativePtr, 1, paramInt1, paramInt2);
  }
  
  public final int getHistorySize()
  {
    return nativeGetHistorySize(this.mNativePtr);
  }
  
  public final int getMetaState()
  {
    return nativeGetMetaState(this.mNativePtr);
  }
  
  public final float getOrientation()
  {
    return nativeGetAxisValue(this.mNativePtr, 8, 0, Integer.MIN_VALUE);
  }
  
  public final float getOrientation(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 8, paramInt, Integer.MIN_VALUE);
  }
  
  public final void getPointerCoords(int paramInt, PointerCoords paramPointerCoords)
  {
    nativeGetPointerCoords(this.mNativePtr, paramInt, Integer.MIN_VALUE, paramPointerCoords);
  }
  
  public final int getPointerCount()
  {
    return nativeGetPointerCount(this.mNativePtr);
  }
  
  public final int getPointerId(int paramInt)
  {
    return nativeGetPointerId(this.mNativePtr, paramInt);
  }
  
  @UnsupportedAppUsage
  public final int getPointerIdBits()
  {
    int i = 0;
    int j = nativeGetPointerCount(this.mNativePtr);
    for (int k = 0; k < j; k++) {
      i |= 1 << nativeGetPointerId(this.mNativePtr, k);
    }
    return i;
  }
  
  public final void getPointerProperties(int paramInt, PointerProperties paramPointerProperties)
  {
    nativeGetPointerProperties(this.mNativePtr, paramInt, paramPointerProperties);
  }
  
  public final float getPressure()
  {
    return nativeGetAxisValue(this.mNativePtr, 2, 0, Integer.MIN_VALUE);
  }
  
  public final float getPressure(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 2, paramInt, Integer.MIN_VALUE);
  }
  
  public final float getRawX()
  {
    return nativeGetRawAxisValue(this.mNativePtr, 0, 0, Integer.MIN_VALUE);
  }
  
  public float getRawX(int paramInt)
  {
    return nativeGetRawAxisValue(this.mNativePtr, 0, paramInt, Integer.MIN_VALUE);
  }
  
  public final float getRawY()
  {
    return nativeGetRawAxisValue(this.mNativePtr, 1, 0, Integer.MIN_VALUE);
  }
  
  public float getRawY(int paramInt)
  {
    return nativeGetRawAxisValue(this.mNativePtr, 1, paramInt, Integer.MIN_VALUE);
  }
  
  public final float getSize()
  {
    return nativeGetAxisValue(this.mNativePtr, 3, 0, Integer.MIN_VALUE);
  }
  
  public final float getSize(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 3, paramInt, Integer.MIN_VALUE);
  }
  
  public final int getSource()
  {
    return nativeGetSource(this.mNativePtr);
  }
  
  public final float getToolMajor()
  {
    return nativeGetAxisValue(this.mNativePtr, 6, 0, Integer.MIN_VALUE);
  }
  
  public final float getToolMajor(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 6, paramInt, Integer.MIN_VALUE);
  }
  
  public final float getToolMinor()
  {
    return nativeGetAxisValue(this.mNativePtr, 7, 0, Integer.MIN_VALUE);
  }
  
  public final float getToolMinor(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 7, paramInt, Integer.MIN_VALUE);
  }
  
  public final int getToolType(int paramInt)
  {
    return nativeGetToolType(this.mNativePtr, paramInt);
  }
  
  public final float getTouchMajor()
  {
    return nativeGetAxisValue(this.mNativePtr, 4, 0, Integer.MIN_VALUE);
  }
  
  public final float getTouchMajor(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 4, paramInt, Integer.MIN_VALUE);
  }
  
  public final float getTouchMinor()
  {
    return nativeGetAxisValue(this.mNativePtr, 5, 0, Integer.MIN_VALUE);
  }
  
  public final float getTouchMinor(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 5, paramInt, Integer.MIN_VALUE);
  }
  
  public final float getX()
  {
    return nativeGetAxisValue(this.mNativePtr, 0, 0, Integer.MIN_VALUE);
  }
  
  public final float getX(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 0, paramInt, Integer.MIN_VALUE);
  }
  
  public final float getXPrecision()
  {
    return nativeGetXPrecision(this.mNativePtr);
  }
  
  public final float getY()
  {
    return nativeGetAxisValue(this.mNativePtr, 1, 0, Integer.MIN_VALUE);
  }
  
  public final float getY(int paramInt)
  {
    return nativeGetAxisValue(this.mNativePtr, 1, paramInt, Integer.MIN_VALUE);
  }
  
  public final float getYPrecision()
  {
    return nativeGetYPrecision(this.mNativePtr);
  }
  
  public final boolean isButtonPressed(int paramInt)
  {
    boolean bool = false;
    if (paramInt == 0) {
      return false;
    }
    if ((getButtonState() & paramInt) == paramInt) {
      bool = true;
    }
    return bool;
  }
  
  public final boolean isHoverExitPending()
  {
    boolean bool;
    if ((getFlags() & 0x4) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isTainted()
  {
    boolean bool;
    if ((0x80000000 & getFlags()) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isTargetAccessibilityFocus()
  {
    boolean bool;
    if ((0x40000000 & getFlags()) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public final boolean isTouchEvent()
  {
    return nativeIsTouchEvent(this.mNativePtr);
  }
  
  public final boolean isWithinBoundsNoHistory(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int i = nativeGetPointerCount(this.mNativePtr);
    int j = 0;
    while (j < i)
    {
      float f1 = nativeGetAxisValue(this.mNativePtr, 0, j, Integer.MIN_VALUE);
      float f2 = nativeGetAxisValue(this.mNativePtr, 1, j, Integer.MIN_VALUE);
      if ((f1 >= paramFloat1) && (f1 <= paramFloat3) && (f2 >= paramFloat2) && (f2 <= paramFloat4)) {
        j++;
      } else {
        return false;
      }
    }
    return true;
  }
  
  public final void offsetLocation(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 != 0.0F) || (paramFloat2 != 0.0F)) {
      nativeOffsetLocation(this.mNativePtr, paramFloat1, paramFloat2);
    }
  }
  
  public final void recycle()
  {
    super.recycle();
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
  
  @UnsupportedAppUsage
  public final void scale(float paramFloat)
  {
    if (paramFloat != 1.0F) {
      nativeScale(this.mNativePtr, paramFloat);
    }
  }
  
  public final void setAction(int paramInt)
  {
    nativeSetAction(this.mNativePtr, paramInt);
  }
  
  public final void setActionButton(int paramInt)
  {
    nativeSetActionButton(this.mNativePtr, paramInt);
  }
  
  public final void setButtonState(int paramInt)
  {
    nativeSetButtonState(this.mNativePtr, paramInt);
  }
  
  public void setDisplayId(int paramInt)
  {
    nativeSetDisplayId(this.mNativePtr, paramInt);
  }
  
  @UnsupportedAppUsage
  public final void setDownTime(long paramLong)
  {
    nativeSetDownTimeNanos(this.mNativePtr, 1000000L * paramLong);
  }
  
  public final void setEdgeFlags(int paramInt)
  {
    nativeSetEdgeFlags(this.mNativePtr, paramInt);
  }
  
  public void setHoverExitPending(boolean paramBoolean)
  {
    int i = getFlags();
    long l = this.mNativePtr;
    if (paramBoolean) {
      i |= 0x4;
    } else {
      i &= 0xFFFFFFFB;
    }
    nativeSetFlags(l, i);
  }
  
  public final void setLocation(float paramFloat1, float paramFloat2)
  {
    offsetLocation(paramFloat1 - getX(), paramFloat2 - getY());
  }
  
  public final void setSource(int paramInt)
  {
    nativeSetSource(this.mNativePtr, paramInt);
  }
  
  public final void setTainted(boolean paramBoolean)
  {
    int i = getFlags();
    long l = this.mNativePtr;
    if (paramBoolean) {
      i = 0x80000000 | i;
    } else {
      i = 0x7FFFFFFF & i;
    }
    nativeSetFlags(l, i);
  }
  
  public final void setTargetAccessibilityFocus(boolean paramBoolean)
  {
    int i = getFlags();
    long l = this.mNativePtr;
    if (paramBoolean) {
      i = 0x40000000 | i;
    } else {
      i = 0xBFFFFFFF & i;
    }
    nativeSetFlags(l, i);
  }
  
  /* Error */
  @UnsupportedAppUsage
  public final MotionEvent split(int paramInt)
  {
    // Byte code:
    //   0: invokestatic 442	android/view/MotionEvent:obtain	()Landroid/view/MotionEvent;
    //   3: astore_2
    //   4: getstatic 344	android/view/MotionEvent:gSharedTempLock	Ljava/lang/Object;
    //   7: astore_3
    //   8: aload_3
    //   9: monitorenter
    //   10: aload_2
    //   11: astore 4
    //   13: aload_3
    //   14: astore 4
    //   16: aload_0
    //   17: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   20: invokestatic 621	android/view/MotionEvent:nativeGetPointerCount	(J)I
    //   23: istore 5
    //   25: aload_2
    //   26: astore 4
    //   28: aload_3
    //   29: astore 4
    //   31: iload 5
    //   33: invokestatic 542	android/view/MotionEvent:ensureSharedTempPointerCapacity	(I)V
    //   36: aload_2
    //   37: astore 4
    //   39: aload_3
    //   40: astore 4
    //   42: getstatic 461	android/view/MotionEvent:gSharedTempPointerProperties	[Landroid/view/MotionEvent$PointerProperties;
    //   45: astore 6
    //   47: aload_2
    //   48: astore 4
    //   50: aload_3
    //   51: astore 4
    //   53: getstatic 452	android/view/MotionEvent:gSharedTempPointerCoords	[Landroid/view/MotionEvent$PointerCoords;
    //   56: astore 7
    //   58: aload_2
    //   59: astore 4
    //   61: aload_3
    //   62: astore 4
    //   64: getstatic 463	android/view/MotionEvent:gSharedTempPointerIndexMap	[I
    //   67: astore 8
    //   69: aload_2
    //   70: astore 4
    //   72: aload_3
    //   73: astore 4
    //   75: aload_0
    //   76: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   79: invokestatic 609	android/view/MotionEvent:nativeGetAction	(J)I
    //   82: istore 9
    //   84: iload 9
    //   86: sipush 255
    //   89: iand
    //   90: istore 10
    //   92: ldc 54
    //   94: iload 9
    //   96: iand
    //   97: bipush 8
    //   99: ishr
    //   100: istore 11
    //   102: iconst_0
    //   103: istore 12
    //   105: iconst_m1
    //   106: istore 13
    //   108: iconst_0
    //   109: istore 14
    //   111: iconst_1
    //   112: istore 15
    //   114: iload 12
    //   116: iload 5
    //   118: if_icmpge +101 -> 219
    //   121: aload_2
    //   122: astore 4
    //   124: aload_3
    //   125: astore 4
    //   127: aload_0
    //   128: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   131: iload 12
    //   133: aload 6
    //   135: iload 14
    //   137: aaload
    //   138: invokestatic 629	android/view/MotionEvent:nativeGetPointerProperties	(JILandroid/view/MotionEvent$PointerProperties;)V
    //   141: iload 14
    //   143: istore 16
    //   145: iload 13
    //   147: istore 15
    //   149: aload_2
    //   150: astore 4
    //   152: aload_3
    //   153: astore 4
    //   155: iconst_1
    //   156: aload 6
    //   158: iload 14
    //   160: aaload
    //   161: getfield 548	android/view/MotionEvent$PointerProperties:id	I
    //   164: ishl
    //   165: iload_1
    //   166: iand
    //   167: ifeq +31 -> 198
    //   170: iload 12
    //   172: iload 11
    //   174: if_icmpne +7 -> 181
    //   177: iload 14
    //   179: istore 13
    //   181: aload 8
    //   183: iload 14
    //   185: iload 12
    //   187: iastore
    //   188: iload 14
    //   190: iconst_1
    //   191: iadd
    //   192: istore 16
    //   194: iload 13
    //   196: istore 15
    //   198: iinc 12 1
    //   201: iload 16
    //   203: istore 14
    //   205: iload 15
    //   207: istore 13
    //   209: goto -98 -> 111
    //   212: astore_3
    //   213: aload 4
    //   215: astore_2
    //   216: goto +464 -> 680
    //   219: iload 14
    //   221: ifeq +433 -> 654
    //   224: iload 10
    //   226: iconst_5
    //   227: if_icmpeq +19 -> 246
    //   230: iload 10
    //   232: bipush 6
    //   234: if_icmpne +6 -> 240
    //   237: goto +9 -> 246
    //   240: iload 9
    //   242: istore_1
    //   243: goto +45 -> 288
    //   246: iload 13
    //   248: ifge +8 -> 256
    //   251: iconst_2
    //   252: istore_1
    //   253: goto +35 -> 288
    //   256: iload 14
    //   258: iconst_1
    //   259: if_icmpne +20 -> 279
    //   262: iload 10
    //   264: iconst_5
    //   265: if_icmpne +8 -> 273
    //   268: iconst_0
    //   269: istore_1
    //   270: goto +6 -> 276
    //   273: iload 15
    //   275: istore_1
    //   276: goto +12 -> 288
    //   279: iload 13
    //   281: bipush 8
    //   283: ishl
    //   284: iload 10
    //   286: ior
    //   287: istore_1
    //   288: aload_2
    //   289: astore 4
    //   291: aload_3
    //   292: astore 4
    //   294: aload_0
    //   295: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   298: invokestatic 637	android/view/MotionEvent:nativeGetHistorySize	(J)I
    //   301: istore 17
    //   303: iconst_0
    //   304: istore 18
    //   306: iload 9
    //   308: istore 13
    //   310: iload 10
    //   312: istore 12
    //   314: iload 11
    //   316: istore 15
    //   318: iload 14
    //   320: istore 16
    //   322: iload 18
    //   324: istore 14
    //   326: iload 5
    //   328: istore 9
    //   330: iload 14
    //   332: iload 17
    //   334: if_icmpgt +313 -> 647
    //   337: iload 14
    //   339: iload 17
    //   341: if_icmpne +10 -> 351
    //   344: ldc -88
    //   346: istore 5
    //   348: goto +7 -> 355
    //   351: iload 14
    //   353: istore 5
    //   355: iconst_0
    //   356: istore 11
    //   358: iload 11
    //   360: iload 16
    //   362: if_icmpge +34 -> 396
    //   365: aload_2
    //   366: astore 4
    //   368: aload_3
    //   369: astore 4
    //   371: aload_0
    //   372: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   375: aload 8
    //   377: iload 11
    //   379: iaload
    //   380: iload 5
    //   382: aload 7
    //   384: iload 11
    //   386: aaload
    //   387: invokestatic 639	android/view/MotionEvent:nativeGetPointerCoords	(JIILandroid/view/MotionEvent$PointerCoords;)V
    //   390: iinc 11 1
    //   393: goto -35 -> 358
    //   396: aload_2
    //   397: astore 4
    //   399: aload_3
    //   400: astore 4
    //   402: aload_0
    //   403: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   406: iload 5
    //   408: invokestatic 641	android/view/MotionEvent:nativeGetEventTimeNanos	(JI)J
    //   411: lstore 19
    //   413: iload 14
    //   415: ifne +211 -> 626
    //   418: aload_2
    //   419: astore 4
    //   421: aload_3
    //   422: astore 4
    //   424: aload_2
    //   425: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   428: lstore 21
    //   430: aload_2
    //   431: astore 4
    //   433: aload_3
    //   434: astore 4
    //   436: aload_0
    //   437: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   440: invokestatic 611	android/view/MotionEvent:nativeGetDeviceId	(J)I
    //   443: istore 23
    //   445: aload_2
    //   446: astore 4
    //   448: aload_3
    //   449: astore 4
    //   451: aload_0
    //   452: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   455: invokestatic 613	android/view/MotionEvent:nativeGetSource	(J)I
    //   458: istore 24
    //   460: aload_2
    //   461: astore 4
    //   463: aload_3
    //   464: astore 4
    //   466: aload_0
    //   467: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   470: invokestatic 615	android/view/MotionEvent:nativeGetDisplayId	(J)I
    //   473: istore 11
    //   475: aload_2
    //   476: astore 4
    //   478: aload_3
    //   479: astore 4
    //   481: aload_0
    //   482: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   485: invokestatic 617	android/view/MotionEvent:nativeGetFlags	(J)I
    //   488: istore 18
    //   490: aload_2
    //   491: astore 4
    //   493: aload_3
    //   494: astore 4
    //   496: aload_0
    //   497: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   500: invokestatic 651	android/view/MotionEvent:nativeGetEdgeFlags	(J)I
    //   503: istore 25
    //   505: aload_2
    //   506: astore 4
    //   508: aload_3
    //   509: astore 4
    //   511: aload_0
    //   512: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   515: invokestatic 635	android/view/MotionEvent:nativeGetMetaState	(J)I
    //   518: istore 5
    //   520: aload_2
    //   521: astore 4
    //   523: aload_3
    //   524: astore 4
    //   526: aload_0
    //   527: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   530: invokestatic 653	android/view/MotionEvent:nativeGetButtonState	(J)I
    //   533: istore 10
    //   535: aload_3
    //   536: astore 4
    //   538: lload 21
    //   540: iload 23
    //   542: iload 24
    //   544: iload 11
    //   546: iload_1
    //   547: iload 18
    //   549: iload 25
    //   551: iload 5
    //   553: iload 10
    //   555: aload_0
    //   556: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   559: invokestatic 619	android/view/MotionEvent:nativeGetClassification	(J)I
    //   562: aload_0
    //   563: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   566: invokestatic 655	android/view/MotionEvent:nativeGetXOffset	(J)F
    //   569: aload_0
    //   570: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   573: invokestatic 657	android/view/MotionEvent:nativeGetYOffset	(J)F
    //   576: aload_0
    //   577: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   580: invokestatic 659	android/view/MotionEvent:nativeGetXPrecision	(J)F
    //   583: aload_0
    //   584: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   587: invokestatic 661	android/view/MotionEvent:nativeGetYPrecision	(J)F
    //   590: aload_0
    //   591: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   594: invokestatic 663	android/view/MotionEvent:nativeGetDownTimeNanos	(J)J
    //   597: lload 19
    //   599: iload 16
    //   601: aload 6
    //   603: aload 7
    //   605: invokestatic 564	android/view/MotionEvent:nativeInitialize	(JIIIIIIIIIFFFFJJI[Landroid/view/MotionEvent$PointerProperties;[Landroid/view/MotionEvent$PointerCoords;)J
    //   608: lstore 21
    //   610: aload_2
    //   611: lload 21
    //   613: putfield 444	android/view/MotionEvent:mNativePtr	J
    //   616: goto +25 -> 641
    //   619: astore_3
    //   620: aload 4
    //   622: astore_2
    //   623: goto +57 -> 680
    //   626: aload_3
    //   627: astore 4
    //   629: aload_2
    //   630: getfield 444	android/view/MotionEvent:mNativePtr	J
    //   633: lload 19
    //   635: aload 7
    //   637: iconst_0
    //   638: invokestatic 605	android/view/MotionEvent:nativeAddBatch	(JJ[Landroid/view/MotionEvent$PointerCoords;I)V
    //   641: iinc 14 1
    //   644: goto -314 -> 330
    //   647: aload_3
    //   648: astore 4
    //   650: aload_3
    //   651: monitorexit
    //   652: aload_2
    //   653: areturn
    //   654: aload_3
    //   655: astore 4
    //   657: new 592	java/lang/IllegalArgumentException
    //   660: astore_2
    //   661: aload_3
    //   662: astore 4
    //   664: aload_2
    //   665: ldc_w 812
    //   668: invokespecial 595	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   671: aload_3
    //   672: astore 4
    //   674: aload_2
    //   675: athrow
    //   676: astore_3
    //   677: aload 4
    //   679: astore_2
    //   680: aload_2
    //   681: astore 4
    //   683: aload_2
    //   684: monitorexit
    //   685: aload_3
    //   686: athrow
    //   687: astore_3
    //   688: aload 4
    //   690: astore_2
    //   691: goto -11 -> 680
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	694	0	this	MotionEvent
    //   0	694	1	paramInt	int
    //   3	688	2	localObject1	Object
    //   7	146	3	localObject2	Object
    //   212	324	3	localObject3	Object
    //   619	53	3	localObject4	Object
    //   676	10	3	localObject5	Object
    //   687	1	3	localObject6	Object
    //   11	678	4	localObject7	Object
    //   23	529	5	i	int
    //   45	557	6	arrayOfPointerProperties	PointerProperties[]
    //   56	580	7	arrayOfPointerCoords	PointerCoords[]
    //   67	309	8	arrayOfInt	int[]
    //   82	247	9	j	int
    //   90	464	10	k	int
    //   100	445	11	m	int
    //   103	210	12	n	int
    //   106	203	13	i1	int
    //   109	533	14	i2	int
    //   112	205	15	i3	int
    //   143	457	16	i4	int
    //   301	41	17	i5	int
    //   304	244	18	i6	int
    //   411	223	19	l1	long
    //   428	184	21	l2	long
    //   443	98	23	i7	int
    //   458	85	24	i8	int
    //   503	47	25	i9	int
    // Exception table:
    //   from	to	target	type
    //   127	141	212	finally
    //   155	170	212	finally
    //   371	390	212	finally
    //   538	610	619	finally
    //   16	25	676	finally
    //   31	36	676	finally
    //   42	47	676	finally
    //   53	58	676	finally
    //   64	69	676	finally
    //   75	84	676	finally
    //   294	303	676	finally
    //   402	413	676	finally
    //   424	430	676	finally
    //   436	445	676	finally
    //   451	460	676	finally
    //   466	475	676	finally
    //   481	490	676	finally
    //   496	505	676	finally
    //   511	520	676	finally
    //   526	535	676	finally
    //   610	616	687	finally
    //   629	641	687	finally
    //   650	652	687	finally
    //   657	661	687	finally
    //   664	671	687	finally
    //   674	676	687	finally
    //   683	685	687	finally
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("MotionEvent { action=");
    localStringBuilder1.append(actionToString(getAction()));
    appendUnless("0", localStringBuilder1, ", actionButton=", buttonStateToString(getActionButton()));
    int i = getPointerCount();
    for (int j = 0; j < i; j++)
    {
      StringBuilder localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", id[");
      localStringBuilder2.append(j);
      localStringBuilder2.append("]=");
      appendUnless(Integer.valueOf(j), localStringBuilder1, localStringBuilder2.toString(), Integer.valueOf(getPointerId(j)));
      float f1 = getX(j);
      float f2 = getY(j);
      localStringBuilder1.append(", x[");
      localStringBuilder1.append(j);
      localStringBuilder1.append("]=");
      localStringBuilder1.append(f1);
      localStringBuilder1.append(", y[");
      localStringBuilder1.append(j);
      localStringBuilder1.append("]=");
      localStringBuilder1.append(f2);
      String str = (String)TOOL_TYPE_SYMBOLIC_NAMES.get(1);
      localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", toolType[");
      localStringBuilder2.append(j);
      localStringBuilder2.append("]=");
      appendUnless(str, localStringBuilder1, localStringBuilder2.toString(), toolTypeToString(getToolType(j)));
    }
    appendUnless("0", localStringBuilder1, ", buttonState=", buttonStateToString(getButtonState()));
    appendUnless(classificationToString(0), localStringBuilder1, ", classification=", classificationToString(getClassification()));
    appendUnless("0", localStringBuilder1, ", metaState=", KeyEvent.metaStateToString(getMetaState()));
    appendUnless("0", localStringBuilder1, ", flags=0x", Integer.toHexString(getFlags()));
    appendUnless("0", localStringBuilder1, ", edgeFlags=0x", Integer.toHexString(getEdgeFlags()));
    appendUnless(Integer.valueOf(1), localStringBuilder1, ", pointerCount=", Integer.valueOf(i));
    appendUnless(Integer.valueOf(0), localStringBuilder1, ", historySize=", Integer.valueOf(getHistorySize()));
    localStringBuilder1.append(", eventTime=");
    localStringBuilder1.append(getEventTime());
    localStringBuilder1.append(", downTime=");
    localStringBuilder1.append(getDownTime());
    localStringBuilder1.append(", deviceId=");
    localStringBuilder1.append(getDeviceId());
    localStringBuilder1.append(", source=0x");
    localStringBuilder1.append(Integer.toHexString(getSource()));
    localStringBuilder1.append(", displayId=");
    localStringBuilder1.append(getDisplayId());
    localStringBuilder1.append(" }");
    return localStringBuilder1.toString();
  }
  
  public final void transform(Matrix paramMatrix)
  {
    if (paramMatrix != null)
    {
      nativeTransform(this.mNativePtr, paramMatrix.native_instance);
      return;
    }
    throw new IllegalArgumentException("matrix must not be null");
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(1);
    nativeWriteToParcel(this.mNativePtr, paramParcel);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Classification {}
  
  public static final class PointerCoords
  {
    private static final int INITIAL_PACKED_AXIS_VALUES = 8;
    @UnsupportedAppUsage
    private long mPackedAxisBits;
    @UnsupportedAppUsage
    private float[] mPackedAxisValues;
    public float orientation;
    public float pressure;
    public float size;
    public float toolMajor;
    public float toolMinor;
    public float touchMajor;
    public float touchMinor;
    public float x;
    public float y;
    
    public PointerCoords() {}
    
    public PointerCoords(PointerCoords paramPointerCoords)
    {
      copyFrom(paramPointerCoords);
    }
    
    @UnsupportedAppUsage
    public static PointerCoords[] createArray(int paramInt)
    {
      PointerCoords[] arrayOfPointerCoords = new PointerCoords[paramInt];
      for (int i = 0; i < paramInt; i++) {
        arrayOfPointerCoords[i] = new PointerCoords();
      }
      return arrayOfPointerCoords;
    }
    
    public void clear()
    {
      this.mPackedAxisBits = 0L;
      this.x = 0.0F;
      this.y = 0.0F;
      this.pressure = 0.0F;
      this.size = 0.0F;
      this.touchMajor = 0.0F;
      this.touchMinor = 0.0F;
      this.toolMajor = 0.0F;
      this.toolMinor = 0.0F;
      this.orientation = 0.0F;
    }
    
    public void copyFrom(PointerCoords paramPointerCoords)
    {
      long l = paramPointerCoords.mPackedAxisBits;
      this.mPackedAxisBits = l;
      if (l != 0L)
      {
        float[] arrayOfFloat1 = paramPointerCoords.mPackedAxisValues;
        int i = Long.bitCount(l);
        float[] arrayOfFloat2 = this.mPackedAxisValues;
        float[] arrayOfFloat3;
        if (arrayOfFloat2 != null)
        {
          arrayOfFloat3 = arrayOfFloat2;
          if (i <= arrayOfFloat2.length) {}
        }
        else
        {
          arrayOfFloat3 = new float[arrayOfFloat1.length];
          this.mPackedAxisValues = arrayOfFloat3;
        }
        System.arraycopy(arrayOfFloat1, 0, arrayOfFloat3, 0, i);
      }
      this.x = paramPointerCoords.x;
      this.y = paramPointerCoords.y;
      this.pressure = paramPointerCoords.pressure;
      this.size = paramPointerCoords.size;
      this.touchMajor = paramPointerCoords.touchMajor;
      this.touchMinor = paramPointerCoords.touchMinor;
      this.toolMajor = paramPointerCoords.toolMajor;
      this.toolMinor = paramPointerCoords.toolMinor;
      this.orientation = paramPointerCoords.orientation;
    }
    
    public float getAxisValue(int paramInt)
    {
      long l;
      switch (paramInt)
      {
      default: 
        if ((paramInt < 0) || (paramInt > 63)) {
          break label144;
        }
        l = this.mPackedAxisBits;
        if ((l & Long.MIN_VALUE >>> paramInt) == 0L) {
          return 0.0F;
        }
        break;
      case 8: 
        return this.orientation;
      case 7: 
        return this.toolMinor;
      case 6: 
        return this.toolMajor;
      case 5: 
        return this.touchMinor;
      case 4: 
        return this.touchMajor;
      case 3: 
        return this.size;
      case 2: 
        return this.pressure;
      case 1: 
        return this.y;
      case 0: 
        return this.x;
      }
      paramInt = Long.bitCount(-1L >>> paramInt & l);
      return this.mPackedAxisValues[paramInt];
      label144:
      throw new IllegalArgumentException("Axis out of range.");
    }
    
    public void setAxisValue(int paramInt, float paramFloat)
    {
      long l1;
      long l2;
      float[] arrayOfFloat1;
      float[] arrayOfFloat2;
      switch (paramInt)
      {
      default: 
        if ((paramInt < 0) || (paramInt > 63)) {
          break label295;
        }
        l1 = this.mPackedAxisBits;
        l2 = Long.MIN_VALUE >>> paramInt;
        paramInt = Long.bitCount(-1L >>> paramInt & l1);
        arrayOfFloat1 = this.mPackedAxisValues;
        arrayOfFloat2 = arrayOfFloat1;
        if ((l1 & l2) != 0L) {
          break label289;
        }
        if (arrayOfFloat1 == null)
        {
          arrayOfFloat2 = new float[8];
          this.mPackedAxisValues = arrayOfFloat2;
        }
        break;
      case 8: 
        this.orientation = paramFloat;
        break;
      case 7: 
        this.toolMinor = paramFloat;
        break;
      case 6: 
        this.toolMajor = paramFloat;
        break;
      case 5: 
        this.touchMinor = paramFloat;
        break;
      case 4: 
        this.touchMajor = paramFloat;
        break;
      case 3: 
        this.size = paramFloat;
        break;
      case 2: 
        this.pressure = paramFloat;
        break;
      case 1: 
        this.y = paramFloat;
        break;
      case 0: 
        this.x = paramFloat;
        break;
      }
      int i = Long.bitCount(l1);
      if (i < arrayOfFloat1.length)
      {
        arrayOfFloat2 = arrayOfFloat1;
        if (paramInt != i)
        {
          System.arraycopy(arrayOfFloat1, paramInt, arrayOfFloat1, paramInt + 1, i - paramInt);
          arrayOfFloat2 = arrayOfFloat1;
        }
      }
      else
      {
        arrayOfFloat2 = new float[i * 2];
        System.arraycopy(arrayOfFloat1, 0, arrayOfFloat2, 0, paramInt);
        System.arraycopy(arrayOfFloat1, paramInt, arrayOfFloat2, paramInt + 1, i - paramInt);
        this.mPackedAxisValues = arrayOfFloat2;
      }
      this.mPackedAxisBits = (l1 | l2);
      label289:
      arrayOfFloat2[paramInt] = paramFloat;
      return;
      label295:
      throw new IllegalArgumentException("Axis out of range.");
    }
  }
  
  public static final class PointerProperties
  {
    public int id;
    public int toolType;
    
    public PointerProperties()
    {
      clear();
    }
    
    public PointerProperties(PointerProperties paramPointerProperties)
    {
      copyFrom(paramPointerProperties);
    }
    
    @UnsupportedAppUsage
    public static PointerProperties[] createArray(int paramInt)
    {
      PointerProperties[] arrayOfPointerProperties = new PointerProperties[paramInt];
      for (int i = 0; i < paramInt; i++) {
        arrayOfPointerProperties[i] = new PointerProperties();
      }
      return arrayOfPointerProperties;
    }
    
    private boolean equals(PointerProperties paramPointerProperties)
    {
      boolean bool;
      if ((paramPointerProperties != null) && (this.id == paramPointerProperties.id) && (this.toolType == paramPointerProperties.toolType)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void clear()
    {
      this.id = -1;
      this.toolType = 0;
    }
    
    public void copyFrom(PointerProperties paramPointerProperties)
    {
      this.id = paramPointerProperties.id;
      this.toolType = paramPointerProperties.toolType;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof PointerProperties)) {
        return equals((PointerProperties)paramObject);
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.id | this.toolType << 8;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/MotionEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */