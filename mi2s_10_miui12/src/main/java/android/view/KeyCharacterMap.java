package android.view;

import android.annotation.UnsupportedAppUsage;
import android.hardware.input.InputManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AndroidRuntimeException;
import android.util.SparseIntArray;
import java.text.Normalizer;
import java.text.Normalizer.Form;

public class KeyCharacterMap
  implements Parcelable
{
  private static final int ACCENT_ACUTE = 180;
  private static final int ACCENT_BREVE = 728;
  private static final int ACCENT_CARON = 711;
  private static final int ACCENT_CEDILLA = 184;
  private static final int ACCENT_CIRCUMFLEX = 710;
  private static final int ACCENT_CIRCUMFLEX_LEGACY = 94;
  private static final int ACCENT_COMMA_ABOVE = 8125;
  private static final int ACCENT_COMMA_ABOVE_RIGHT = 700;
  private static final int ACCENT_DOT_ABOVE = 729;
  private static final int ACCENT_DOT_BELOW = 46;
  private static final int ACCENT_DOUBLE_ACUTE = 733;
  private static final int ACCENT_GRAVE = 715;
  private static final int ACCENT_GRAVE_LEGACY = 96;
  private static final int ACCENT_HOOK_ABOVE = 704;
  private static final int ACCENT_HORN = 39;
  private static final int ACCENT_MACRON = 175;
  private static final int ACCENT_MACRON_BELOW = 717;
  private static final int ACCENT_OGONEK = 731;
  private static final int ACCENT_REVERSED_COMMA_ABOVE = 701;
  private static final int ACCENT_RING_ABOVE = 730;
  private static final int ACCENT_STROKE = 45;
  private static final int ACCENT_TILDE = 732;
  private static final int ACCENT_TILDE_LEGACY = 126;
  private static final int ACCENT_TURNED_COMMA_ABOVE = 699;
  private static final int ACCENT_UMLAUT = 168;
  private static final int ACCENT_VERTICAL_LINE_ABOVE = 712;
  private static final int ACCENT_VERTICAL_LINE_BELOW = 716;
  public static final int ALPHA = 3;
  @Deprecated
  public static final int BUILT_IN_KEYBOARD = 0;
  private static final int CHAR_SPACE = 32;
  public static final int COMBINING_ACCENT = Integer.MIN_VALUE;
  public static final int COMBINING_ACCENT_MASK = Integer.MAX_VALUE;
  public static final Parcelable.Creator<KeyCharacterMap> CREATOR = new Parcelable.Creator()
  {
    public KeyCharacterMap createFromParcel(Parcel paramAnonymousParcel)
    {
      return new KeyCharacterMap(paramAnonymousParcel, null);
    }
    
    public KeyCharacterMap[] newArray(int paramAnonymousInt)
    {
      return new KeyCharacterMap[paramAnonymousInt];
    }
  };
  public static final int FULL = 4;
  public static final char HEX_INPUT = '';
  public static final int MODIFIER_BEHAVIOR_CHORDED = 0;
  public static final int MODIFIER_BEHAVIOR_CHORDED_OR_TOGGLED = 1;
  public static final int NUMERIC = 1;
  public static final char PICKER_DIALOG_INPUT = '';
  public static final int PREDICTIVE = 2;
  public static final int SPECIAL_FUNCTION = 5;
  public static final int VIRTUAL_KEYBOARD = -1;
  private static final SparseIntArray sAccentToCombining;
  private static final SparseIntArray sCombiningToAccent = new SparseIntArray();
  private static final StringBuilder sDeadKeyBuilder;
  private static final SparseIntArray sDeadKeyCache;
  private long mPtr;
  
  static
  {
    sAccentToCombining = new SparseIntArray();
    addCombining(768, 715);
    addCombining(769, 180);
    addCombining(770, 710);
    addCombining(771, 732);
    addCombining(772, 175);
    addCombining(774, 728);
    addCombining(775, 729);
    addCombining(776, 168);
    addCombining(777, 704);
    addCombining(778, 730);
    addCombining(779, 733);
    addCombining(780, 711);
    addCombining(781, 712);
    addCombining(786, 699);
    addCombining(787, 8125);
    addCombining(788, 701);
    addCombining(789, 700);
    addCombining(795, 39);
    addCombining(803, 46);
    addCombining(807, 184);
    addCombining(808, 731);
    addCombining(809, 716);
    addCombining(817, 717);
    addCombining(821, 45);
    sCombiningToAccent.append(832, 715);
    sCombiningToAccent.append(833, 180);
    sCombiningToAccent.append(835, 8125);
    sAccentToCombining.append(96, 768);
    sAccentToCombining.append(94, 770);
    sAccentToCombining.append(126, 771);
    sDeadKeyCache = new SparseIntArray();
    sDeadKeyBuilder = new StringBuilder();
    addDeadKey(45, 68, 272);
    addDeadKey(45, 71, 484);
    addDeadKey(45, 72, 294);
    addDeadKey(45, 73, 407);
    addDeadKey(45, 76, 321);
    addDeadKey(45, 79, 216);
    addDeadKey(45, 84, 358);
    addDeadKey(45, 100, 273);
    addDeadKey(45, 103, 485);
    addDeadKey(45, 104, 295);
    addDeadKey(45, 105, 616);
    addDeadKey(45, 108, 322);
    addDeadKey(45, 111, 248);
    addDeadKey(45, 116, 359);
  }
  
  @UnsupportedAppUsage
  private KeyCharacterMap(long paramLong)
  {
    this.mPtr = paramLong;
  }
  
  private KeyCharacterMap(Parcel paramParcel)
  {
    if (paramParcel != null)
    {
      this.mPtr = nativeReadFromParcel(paramParcel);
      if (this.mPtr != 0L) {
        return;
      }
      throw new RuntimeException("Could not read KeyCharacterMap from parcel.");
    }
    throw new IllegalArgumentException("parcel must not be null");
  }
  
  private static void addCombining(int paramInt1, int paramInt2)
  {
    sCombiningToAccent.append(paramInt1, paramInt2);
    sAccentToCombining.append(paramInt2, paramInt1);
  }
  
  private static void addDeadKey(int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt1 = sAccentToCombining.get(paramInt1);
    if (paramInt1 != 0)
    {
      sDeadKeyCache.put(paramInt1 << 16 | paramInt2, paramInt3);
      return;
    }
    throw new IllegalStateException("Invalid dead key declaration.");
  }
  
  public static boolean deviceHasKey(int paramInt)
  {
    return InputManager.getInstance().deviceHasKeys(new int[] { paramInt })[0];
  }
  
  public static boolean[] deviceHasKeys(int[] paramArrayOfInt)
  {
    return InputManager.getInstance().deviceHasKeys(paramArrayOfInt);
  }
  
  public static int getDeadChar(int paramInt1, int paramInt2)
  {
    if ((paramInt2 != paramInt1) && (32 != paramInt2))
    {
      int i = sAccentToCombining.get(paramInt1);
      int j = 0;
      if (i == 0) {
        return 0;
      }
      int k = i << 16 | paramInt2;
      synchronized (sDeadKeyCache)
      {
        int m = sDeadKeyCache.get(k, -1);
        paramInt1 = m;
        if (m == -1)
        {
          sDeadKeyBuilder.setLength(0);
          sDeadKeyBuilder.append((char)paramInt2);
          sDeadKeyBuilder.append((char)i);
          String str = Normalizer.normalize(sDeadKeyBuilder, Normalizer.Form.NFC);
          if (str.codePointCount(0, str.length()) == 1) {
            paramInt1 = str.codePointAt(0);
          } else {
            paramInt1 = j;
          }
          sDeadKeyCache.put(k, paramInt1);
        }
        return paramInt1;
      }
    }
    return paramInt1;
  }
  
  public static KeyCharacterMap load(int paramInt)
  {
    InputManager localInputManager = InputManager.getInstance();
    InputDevice localInputDevice = localInputManager.getInputDevice(paramInt);
    Object localObject = localInputDevice;
    if (localInputDevice == null)
    {
      localObject = localInputManager.getInputDevice(-1);
      if (localObject == null)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Could not load key character map for device ");
        ((StringBuilder)localObject).append(paramInt);
        throw new UnavailableException(((StringBuilder)localObject).toString());
      }
    }
    return ((InputDevice)localObject).getKeyCharacterMap();
  }
  
  private static native void nativeDispose(long paramLong);
  
  private static native char nativeGetCharacter(long paramLong, int paramInt1, int paramInt2);
  
  private static native char nativeGetDisplayLabel(long paramLong, int paramInt);
  
  private static native KeyEvent[] nativeGetEvents(long paramLong, char[] paramArrayOfChar);
  
  private static native boolean nativeGetFallbackAction(long paramLong, int paramInt1, int paramInt2, FallbackAction paramFallbackAction);
  
  private static native int nativeGetKeyboardType(long paramLong);
  
  private static native char nativeGetMatch(long paramLong, int paramInt1, char[] paramArrayOfChar, int paramInt2);
  
  private static native char nativeGetNumber(long paramLong, int paramInt);
  
  private static native long nativeReadFromParcel(Parcel paramParcel);
  
  private static native void nativeWriteToParcel(long paramLong, Parcel paramParcel);
  
  public int describeContents()
  {
    return 0;
  }
  
  protected void finalize()
    throws Throwable
  {
    long l = this.mPtr;
    if (l != 0L)
    {
      nativeDispose(l);
      this.mPtr = 0L;
    }
  }
  
  public int get(int paramInt1, int paramInt2)
  {
    paramInt2 = KeyEvent.normalizeMetaState(paramInt2);
    paramInt1 = nativeGetCharacter(this.mPtr, paramInt1, paramInt2);
    paramInt2 = sCombiningToAccent.get(paramInt1);
    if (paramInt2 != 0) {
      return 0x80000000 | paramInt2;
    }
    return paramInt1;
  }
  
  public char getDisplayLabel(int paramInt)
  {
    return nativeGetDisplayLabel(this.mPtr, paramInt);
  }
  
  public KeyEvent[] getEvents(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar != null) {
      return nativeGetEvents(this.mPtr, paramArrayOfChar);
    }
    throw new IllegalArgumentException("chars must not be null.");
  }
  
  public FallbackAction getFallbackAction(int paramInt1, int paramInt2)
  {
    FallbackAction localFallbackAction = FallbackAction.obtain();
    paramInt2 = KeyEvent.normalizeMetaState(paramInt2);
    if (nativeGetFallbackAction(this.mPtr, paramInt1, paramInt2, localFallbackAction))
    {
      localFallbackAction.metaState = KeyEvent.normalizeMetaState(localFallbackAction.metaState);
      return localFallbackAction;
    }
    localFallbackAction.recycle();
    return null;
  }
  
  @Deprecated
  public boolean getKeyData(int paramInt, KeyData paramKeyData)
  {
    if (paramKeyData.meta.length >= 4)
    {
      int i = nativeGetDisplayLabel(this.mPtr, paramInt);
      if (i == 0) {
        return false;
      }
      paramKeyData.displayLabel = ((char)i);
      paramKeyData.number = nativeGetNumber(this.mPtr, paramInt);
      paramKeyData.meta[0] = nativeGetCharacter(this.mPtr, paramInt, 0);
      paramKeyData.meta[1] = nativeGetCharacter(this.mPtr, paramInt, 1);
      paramKeyData.meta[2] = nativeGetCharacter(this.mPtr, paramInt, 2);
      paramKeyData.meta[3] = nativeGetCharacter(this.mPtr, paramInt, 3);
      return true;
    }
    throw new IndexOutOfBoundsException("results.meta.length must be >= 4");
  }
  
  public int getKeyboardType()
  {
    return nativeGetKeyboardType(this.mPtr);
  }
  
  public char getMatch(int paramInt, char[] paramArrayOfChar)
  {
    return getMatch(paramInt, paramArrayOfChar, 0);
  }
  
  public char getMatch(int paramInt1, char[] paramArrayOfChar, int paramInt2)
  {
    if (paramArrayOfChar != null)
    {
      paramInt2 = KeyEvent.normalizeMetaState(paramInt2);
      return nativeGetMatch(this.mPtr, paramInt1, paramArrayOfChar, paramInt2);
    }
    throw new IllegalArgumentException("chars must not be null.");
  }
  
  public int getModifierBehavior()
  {
    int i = getKeyboardType();
    if ((i != 4) && (i != 5)) {
      return 1;
    }
    return 0;
  }
  
  public char getNumber(int paramInt)
  {
    return nativeGetNumber(this.mPtr, paramInt);
  }
  
  public boolean isPrintingKey(int paramInt)
  {
    switch (Character.getType(nativeGetDisplayLabel(this.mPtr, paramInt)))
    {
    default: 
      return true;
    }
    return false;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (paramParcel != null)
    {
      nativeWriteToParcel(this.mPtr, paramParcel);
      return;
    }
    throw new IllegalArgumentException("parcel must not be null");
  }
  
  public static final class FallbackAction
  {
    private static final int MAX_RECYCLED = 10;
    private static FallbackAction sRecycleBin;
    private static final Object sRecycleLock = new Object();
    private static int sRecycledCount;
    @UnsupportedAppUsage
    public int keyCode;
    @UnsupportedAppUsage
    public int metaState;
    private FallbackAction next;
    
    public static FallbackAction obtain()
    {
      synchronized (sRecycleLock)
      {
        FallbackAction localFallbackAction;
        if (sRecycleBin == null)
        {
          localFallbackAction = new android/view/KeyCharacterMap$FallbackAction;
          localFallbackAction.<init>();
        }
        else
        {
          localFallbackAction = sRecycleBin;
          sRecycleBin = localFallbackAction.next;
          sRecycledCount -= 1;
          localFallbackAction.next = null;
        }
        return localFallbackAction;
      }
    }
    
    public void recycle()
    {
      synchronized (sRecycleLock)
      {
        if (sRecycledCount < 10)
        {
          this.next = sRecycleBin;
          sRecycleBin = this;
          sRecycledCount += 1;
        }
        else
        {
          this.next = null;
        }
        return;
      }
    }
  }
  
  @Deprecated
  public static class KeyData
  {
    public static final int META_LENGTH = 4;
    public char displayLabel;
    public char[] meta = new char[4];
    public char number;
  }
  
  public static class UnavailableException
    extends AndroidRuntimeException
  {
    public UnavailableException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/KeyCharacterMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */