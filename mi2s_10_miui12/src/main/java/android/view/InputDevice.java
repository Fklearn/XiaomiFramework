package android.view;

import android.annotation.UnsupportedAppUsage;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.os.NullVibrator;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Vibrator;
import java.util.ArrayList;
import java.util.List;

public final class InputDevice
  implements Parcelable
{
  public static final Parcelable.Creator<InputDevice> CREATOR = new Parcelable.Creator()
  {
    public InputDevice createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InputDevice(paramAnonymousParcel, null);
    }
    
    public InputDevice[] newArray(int paramAnonymousInt)
    {
      return new InputDevice[paramAnonymousInt];
    }
  };
  public static final int KEYBOARD_TYPE_ALPHABETIC = 2;
  public static final int KEYBOARD_TYPE_NONE = 0;
  public static final int KEYBOARD_TYPE_NON_ALPHABETIC = 1;
  private static final int MAX_RANGES = 1000;
  @Deprecated
  public static final int MOTION_RANGE_ORIENTATION = 8;
  @Deprecated
  public static final int MOTION_RANGE_PRESSURE = 2;
  @Deprecated
  public static final int MOTION_RANGE_SIZE = 3;
  @Deprecated
  public static final int MOTION_RANGE_TOOL_MAJOR = 6;
  @Deprecated
  public static final int MOTION_RANGE_TOOL_MINOR = 7;
  @Deprecated
  public static final int MOTION_RANGE_TOUCH_MAJOR = 4;
  @Deprecated
  public static final int MOTION_RANGE_TOUCH_MINOR = 5;
  @Deprecated
  public static final int MOTION_RANGE_X = 0;
  @Deprecated
  public static final int MOTION_RANGE_Y = 1;
  public static final int SOURCE_ANY = -256;
  public static final int SOURCE_BLUETOOTH_STYLUS = 49154;
  public static final int SOURCE_CLASS_BUTTON = 1;
  public static final int SOURCE_CLASS_JOYSTICK = 16;
  public static final int SOURCE_CLASS_MASK = 255;
  public static final int SOURCE_CLASS_NONE = 0;
  public static final int SOURCE_CLASS_POINTER = 2;
  public static final int SOURCE_CLASS_POSITION = 8;
  public static final int SOURCE_CLASS_TRACKBALL = 4;
  public static final int SOURCE_DPAD = 513;
  public static final int SOURCE_GAMEPAD = 1025;
  public static final int SOURCE_HDMI = 33554433;
  public static final int SOURCE_JOYSTICK = 16777232;
  public static final int SOURCE_KEYBOARD = 257;
  public static final int SOURCE_MOUSE = 8194;
  public static final int SOURCE_MOUSE_RELATIVE = 131076;
  public static final int SOURCE_ROTARY_ENCODER = 4194304;
  public static final int SOURCE_STYLUS = 16386;
  public static final int SOURCE_TOUCHPAD = 1048584;
  public static final int SOURCE_TOUCHSCREEN = 4098;
  public static final int SOURCE_TOUCH_NAVIGATION = 2097152;
  public static final int SOURCE_TRACKBALL = 65540;
  public static final int SOURCE_UNKNOWN = 0;
  private final int mControllerNumber;
  private final String mDescriptor;
  private final int mGeneration;
  private final boolean mHasButtonUnderPad;
  private final boolean mHasMicrophone;
  private final boolean mHasVibrator;
  private final int mId;
  private final InputDeviceIdentifier mIdentifier;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private final boolean mIsExternal;
  private final KeyCharacterMap mKeyCharacterMap;
  private final int mKeyboardType;
  private final ArrayList<MotionRange> mMotionRanges = new ArrayList();
  private final String mName;
  private final int mProductId;
  private final int mSources;
  private final int mVendorId;
  private Vibrator mVibrator;
  
  @UnsupportedAppUsage
  private InputDevice(int paramInt1, int paramInt2, int paramInt3, String paramString1, int paramInt4, int paramInt5, String paramString2, boolean paramBoolean1, int paramInt6, int paramInt7, KeyCharacterMap paramKeyCharacterMap, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    this.mId = paramInt1;
    this.mGeneration = paramInt2;
    this.mControllerNumber = paramInt3;
    this.mName = paramString1;
    this.mVendorId = paramInt4;
    this.mProductId = paramInt5;
    this.mDescriptor = paramString2;
    this.mIsExternal = paramBoolean1;
    this.mSources = paramInt6;
    this.mKeyboardType = paramInt7;
    this.mKeyCharacterMap = paramKeyCharacterMap;
    this.mHasVibrator = paramBoolean2;
    this.mHasMicrophone = paramBoolean3;
    this.mHasButtonUnderPad = paramBoolean4;
    this.mIdentifier = new InputDeviceIdentifier(paramString2, paramInt4, paramInt5);
  }
  
  private InputDevice(Parcel paramParcel)
  {
    this.mId = paramParcel.readInt();
    this.mGeneration = paramParcel.readInt();
    this.mControllerNumber = paramParcel.readInt();
    this.mName = paramParcel.readString();
    this.mVendorId = paramParcel.readInt();
    this.mProductId = paramParcel.readInt();
    this.mDescriptor = paramParcel.readString();
    int i = paramParcel.readInt();
    boolean bool1 = false;
    if (i != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mIsExternal = bool2;
    this.mSources = paramParcel.readInt();
    this.mKeyboardType = paramParcel.readInt();
    this.mKeyCharacterMap = ((KeyCharacterMap)KeyCharacterMap.CREATOR.createFromParcel(paramParcel));
    if (paramParcel.readInt() != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mHasVibrator = bool2;
    if (paramParcel.readInt() != 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mHasMicrophone = bool2;
    boolean bool2 = bool1;
    if (paramParcel.readInt() != 0) {
      bool2 = true;
    }
    this.mHasButtonUnderPad = bool2;
    this.mIdentifier = new InputDeviceIdentifier(this.mDescriptor, this.mVendorId, this.mProductId);
    int j = paramParcel.readInt();
    i = j;
    if (j > 1000) {
      i = 1000;
    }
    for (j = 0; j < i; j++) {
      addMotionRange(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readFloat(), paramParcel.readFloat(), paramParcel.readFloat(), paramParcel.readFloat(), paramParcel.readFloat());
    }
  }
  
  @UnsupportedAppUsage
  private void addMotionRange(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    this.mMotionRanges.add(new MotionRange(paramInt1, paramInt2, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, null));
  }
  
  private void appendSourceDescriptionIfApplicable(StringBuilder paramStringBuilder, int paramInt, String paramString)
  {
    if ((this.mSources & paramInt) == paramInt)
    {
      paramStringBuilder.append(" ");
      paramStringBuilder.append(paramString);
    }
  }
  
  public static InputDevice getDevice(int paramInt)
  {
    return InputManager.getInstance().getInputDevice(paramInt);
  }
  
  public static int[] getDeviceIds()
  {
    return InputManager.getInstance().getInputDeviceIds();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void disable()
  {
    InputManager.getInstance().disableInputDevice(this.mId);
  }
  
  public void enable()
  {
    InputManager.getInstance().enableInputDevice(this.mId);
  }
  
  public int getControllerNumber()
  {
    return this.mControllerNumber;
  }
  
  public String getDescriptor()
  {
    return this.mDescriptor;
  }
  
  public int getGeneration()
  {
    return this.mGeneration;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public InputDeviceIdentifier getIdentifier()
  {
    return this.mIdentifier;
  }
  
  public KeyCharacterMap getKeyCharacterMap()
  {
    return this.mKeyCharacterMap;
  }
  
  public int getKeyboardType()
  {
    return this.mKeyboardType;
  }
  
  public MotionRange getMotionRange(int paramInt)
  {
    int i = this.mMotionRanges.size();
    for (int j = 0; j < i; j++)
    {
      MotionRange localMotionRange = (MotionRange)this.mMotionRanges.get(j);
      if (localMotionRange.mAxis == paramInt) {
        return localMotionRange;
      }
    }
    return null;
  }
  
  public MotionRange getMotionRange(int paramInt1, int paramInt2)
  {
    int i = this.mMotionRanges.size();
    for (int j = 0; j < i; j++)
    {
      MotionRange localMotionRange = (MotionRange)this.mMotionRanges.get(j);
      if ((localMotionRange.mAxis == paramInt1) && (localMotionRange.mSource == paramInt2)) {
        return localMotionRange;
      }
    }
    return null;
  }
  
  public List<MotionRange> getMotionRanges()
  {
    return this.mMotionRanges;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getProductId()
  {
    return this.mProductId;
  }
  
  public int getSources()
  {
    return this.mSources;
  }
  
  public int getVendorId()
  {
    return this.mVendorId;
  }
  
  public Vibrator getVibrator()
  {
    synchronized (this.mMotionRanges)
    {
      if (this.mVibrator == null) {
        if (this.mHasVibrator) {
          this.mVibrator = InputManager.getInstance().getInputDeviceVibrator(this.mId);
        } else {
          this.mVibrator = NullVibrator.getInstance();
        }
      }
      Vibrator localVibrator = this.mVibrator;
      return localVibrator;
    }
  }
  
  public boolean hasButtonUnderPad()
  {
    return this.mHasButtonUnderPad;
  }
  
  public boolean[] hasKeys(int... paramVarArgs)
  {
    return InputManager.getInstance().deviceHasKeys(this.mId, paramVarArgs);
  }
  
  public boolean hasMicrophone()
  {
    return this.mHasMicrophone;
  }
  
  public boolean isEnabled()
  {
    return InputManager.getInstance().isInputDeviceEnabled(this.mId);
  }
  
  public boolean isExternal()
  {
    return this.mIsExternal;
  }
  
  public boolean isFullKeyboard()
  {
    boolean bool;
    if (((this.mSources & 0x101) == 257) && (this.mKeyboardType == 2)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isVirtual()
  {
    boolean bool;
    if (this.mId < 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void setCustomPointerIcon(PointerIcon paramPointerIcon)
  {
    InputManager.getInstance().setCustomPointerIcon(paramPointerIcon);
  }
  
  public void setPointerType(int paramInt)
  {
    InputManager.getInstance().setPointerIconType(paramInt);
  }
  
  public boolean supportsSource(int paramInt)
  {
    boolean bool;
    if ((this.mSources & paramInt) == paramInt) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Input Device ");
    localStringBuilder.append(this.mId);
    localStringBuilder.append(": ");
    localStringBuilder.append(this.mName);
    localStringBuilder.append("\n");
    localStringBuilder.append("  Descriptor: ");
    localStringBuilder.append(this.mDescriptor);
    localStringBuilder.append("\n");
    localStringBuilder.append("  Generation: ");
    localStringBuilder.append(this.mGeneration);
    localStringBuilder.append("\n");
    localStringBuilder.append("  Location: ");
    Object localObject;
    if (this.mIsExternal) {
      localObject = "external";
    } else {
      localObject = "built-in";
    }
    localStringBuilder.append((String)localObject);
    localStringBuilder.append("\n");
    localStringBuilder.append("  Keyboard Type: ");
    int i = this.mKeyboardType;
    if (i != 0)
    {
      if (i != 1)
      {
        if (i == 2) {
          localStringBuilder.append("alphabetic");
        }
      }
      else {
        localStringBuilder.append("non-alphabetic");
      }
    }
    else {
      localStringBuilder.append("none");
    }
    localStringBuilder.append("\n");
    localStringBuilder.append("  Has Vibrator: ");
    localStringBuilder.append(this.mHasVibrator);
    localStringBuilder.append("\n");
    localStringBuilder.append("  Has mic: ");
    localStringBuilder.append(this.mHasMicrophone);
    localStringBuilder.append("\n");
    localStringBuilder.append("  Sources: 0x");
    localStringBuilder.append(Integer.toHexString(this.mSources));
    localStringBuilder.append(" (");
    appendSourceDescriptionIfApplicable(localStringBuilder, 257, "keyboard");
    appendSourceDescriptionIfApplicable(localStringBuilder, 513, "dpad");
    appendSourceDescriptionIfApplicable(localStringBuilder, 4098, "touchscreen");
    appendSourceDescriptionIfApplicable(localStringBuilder, 8194, "mouse");
    appendSourceDescriptionIfApplicable(localStringBuilder, 16386, "stylus");
    appendSourceDescriptionIfApplicable(localStringBuilder, 65540, "trackball");
    appendSourceDescriptionIfApplicable(localStringBuilder, 131076, "mouse_relative");
    appendSourceDescriptionIfApplicable(localStringBuilder, 1048584, "touchpad");
    appendSourceDescriptionIfApplicable(localStringBuilder, 16777232, "joystick");
    appendSourceDescriptionIfApplicable(localStringBuilder, 1025, "gamepad");
    localStringBuilder.append(" )\n");
    int j = this.mMotionRanges.size();
    for (i = 0; i < j; i++)
    {
      localObject = (MotionRange)this.mMotionRanges.get(i);
      localStringBuilder.append("    ");
      localStringBuilder.append(MotionEvent.axisToString(((MotionRange)localObject).mAxis));
      localStringBuilder.append(": source=0x");
      localStringBuilder.append(Integer.toHexString(((MotionRange)localObject).mSource));
      localStringBuilder.append(" min=");
      localStringBuilder.append(((MotionRange)localObject).mMin);
      localStringBuilder.append(" max=");
      localStringBuilder.append(((MotionRange)localObject).mMax);
      localStringBuilder.append(" flat=");
      localStringBuilder.append(((MotionRange)localObject).mFlat);
      localStringBuilder.append(" fuzz=");
      localStringBuilder.append(((MotionRange)localObject).mFuzz);
      localStringBuilder.append(" resolution=");
      localStringBuilder.append(((MotionRange)localObject).mResolution);
      localStringBuilder.append("\n");
    }
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mId);
    paramParcel.writeInt(this.mGeneration);
    paramParcel.writeInt(this.mControllerNumber);
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mVendorId);
    paramParcel.writeInt(this.mProductId);
    paramParcel.writeString(this.mDescriptor);
    paramParcel.writeInt(this.mIsExternal);
    paramParcel.writeInt(this.mSources);
    paramParcel.writeInt(this.mKeyboardType);
    this.mKeyCharacterMap.writeToParcel(paramParcel, paramInt);
    paramParcel.writeInt(this.mHasVibrator);
    paramParcel.writeInt(this.mHasMicrophone);
    paramParcel.writeInt(this.mHasButtonUnderPad);
    int i = this.mMotionRanges.size();
    paramParcel.writeInt(i);
    for (paramInt = 0; paramInt < i; paramInt++)
    {
      MotionRange localMotionRange = (MotionRange)this.mMotionRanges.get(paramInt);
      paramParcel.writeInt(localMotionRange.mAxis);
      paramParcel.writeInt(localMotionRange.mSource);
      paramParcel.writeFloat(localMotionRange.mMin);
      paramParcel.writeFloat(localMotionRange.mMax);
      paramParcel.writeFloat(localMotionRange.mFlat);
      paramParcel.writeFloat(localMotionRange.mFuzz);
      paramParcel.writeFloat(localMotionRange.mResolution);
    }
  }
  
  public static final class MotionRange
  {
    private int mAxis;
    private float mFlat;
    private float mFuzz;
    private float mMax;
    private float mMin;
    private float mResolution;
    private int mSource;
    
    private MotionRange(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
    {
      this.mAxis = paramInt1;
      this.mSource = paramInt2;
      this.mMin = paramFloat1;
      this.mMax = paramFloat2;
      this.mFlat = paramFloat3;
      this.mFuzz = paramFloat4;
      this.mResolution = paramFloat5;
    }
    
    public int getAxis()
    {
      return this.mAxis;
    }
    
    public float getFlat()
    {
      return this.mFlat;
    }
    
    public float getFuzz()
    {
      return this.mFuzz;
    }
    
    public float getMax()
    {
      return this.mMax;
    }
    
    public float getMin()
    {
      return this.mMin;
    }
    
    public float getRange()
    {
      return this.mMax - this.mMin;
    }
    
    public float getResolution()
    {
      return this.mResolution;
    }
    
    public int getSource()
    {
      return this.mSource;
    }
    
    public boolean isFromSource(int paramInt)
    {
      boolean bool;
      if ((getSource() & paramInt) == paramInt) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */