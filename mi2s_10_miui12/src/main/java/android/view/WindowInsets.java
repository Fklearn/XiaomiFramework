package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Insets;
import android.graphics.Rect;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;

public final class WindowInsets
{
  @UnsupportedAppUsage
  public static final WindowInsets CONSUMED = new WindowInsets((Rect)null, null, false, false, null);
  private final boolean mAlwaysConsumeSystemBars;
  private final DisplayCutout mDisplayCutout;
  private final boolean mDisplayCutoutConsumed;
  private final boolean mIsRound;
  private final boolean mStableInsetsConsumed;
  private final boolean mSystemWindowInsetsConsumed;
  private Rect mTempRect;
  private final Insets[] mTypeInsetsMap;
  private final Insets[] mTypeMaxInsetsMap;
  private final boolean[] mTypeVisibilityMap;
  
  @UnsupportedAppUsage
  public WindowInsets(Rect paramRect)
  {
    this(createCompatTypeMap(paramRect), null, new boolean[7], false, false, null);
  }
  
  public WindowInsets(Rect paramRect1, Rect paramRect2, boolean paramBoolean1, boolean paramBoolean2, DisplayCutout paramDisplayCutout)
  {
    this(createCompatTypeMap(paramRect1), createCompatTypeMap(paramRect2), createCompatVisibilityMap(createCompatTypeMap(paramRect1)), paramBoolean1, paramBoolean2, paramDisplayCutout);
  }
  
  public WindowInsets(WindowInsets paramWindowInsets)
  {
    this(arrayOfInsets2, arrayOfInsets1, paramWindowInsets.mTypeVisibilityMap, paramWindowInsets.mIsRound, paramWindowInsets.mAlwaysConsumeSystemBars, displayCutoutCopyConstructorArgument(paramWindowInsets));
  }
  
  public WindowInsets(Insets[] paramArrayOfInsets1, Insets[] paramArrayOfInsets2, boolean[] paramArrayOfBoolean, boolean paramBoolean1, boolean paramBoolean2, DisplayCutout paramDisplayCutout)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramArrayOfInsets1 == null) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mSystemWindowInsetsConsumed = bool2;
    if (this.mSystemWindowInsetsConsumed) {
      paramArrayOfInsets1 = new Insets[7];
    } else {
      paramArrayOfInsets1 = (Insets[])paramArrayOfInsets1.clone();
    }
    this.mTypeInsetsMap = paramArrayOfInsets1;
    if (paramArrayOfInsets2 == null) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mStableInsetsConsumed = bool2;
    if (this.mStableInsetsConsumed) {
      paramArrayOfInsets1 = new Insets[7];
    } else {
      paramArrayOfInsets1 = (Insets[])paramArrayOfInsets2.clone();
    }
    this.mTypeMaxInsetsMap = paramArrayOfInsets1;
    this.mTypeVisibilityMap = paramArrayOfBoolean;
    this.mIsRound = paramBoolean1;
    this.mAlwaysConsumeSystemBars = paramBoolean2;
    if (paramDisplayCutout == null) {
      paramBoolean1 = bool1;
    } else {
      paramBoolean1 = false;
    }
    this.mDisplayCutoutConsumed = paramBoolean1;
    if ((!this.mDisplayCutoutConsumed) && (!paramDisplayCutout.isEmpty())) {
      break label165;
    }
    paramDisplayCutout = null;
    label165:
    this.mDisplayCutout = paramDisplayCutout;
  }
  
  static void assignCompatInsets(Insets[] paramArrayOfInsets, Rect paramRect)
  {
    paramArrayOfInsets[Type.indexOf(1)] = Insets.of(0, paramRect.top, 0, 0);
    paramArrayOfInsets[Type.indexOf(4)] = Insets.of(paramRect.left, 0, paramRect.right, paramRect.bottom);
  }
  
  private static Insets[] createCompatTypeMap(Rect paramRect)
  {
    if (paramRect == null) {
      return null;
    }
    Insets[] arrayOfInsets = new Insets[7];
    assignCompatInsets(arrayOfInsets, paramRect);
    return arrayOfInsets;
  }
  
  private static boolean[] createCompatVisibilityMap(Insets[] paramArrayOfInsets)
  {
    boolean[] arrayOfBoolean = new boolean[7];
    if (paramArrayOfInsets == null) {
      return arrayOfBoolean;
    }
    int i = 1;
    while (i <= 64)
    {
      int j = Type.indexOf(i);
      if (!Insets.NONE.equals(paramArrayOfInsets[j])) {
        arrayOfBoolean[j] = true;
      }
      i <<= 1;
    }
    return arrayOfBoolean;
  }
  
  private static DisplayCutout displayCutoutCopyConstructorArgument(WindowInsets paramWindowInsets)
  {
    if (paramWindowInsets.mDisplayCutoutConsumed) {
      return null;
    }
    paramWindowInsets = paramWindowInsets.mDisplayCutout;
    if (paramWindowInsets == null) {
      return DisplayCutout.NO_CUTOUT;
    }
    return paramWindowInsets;
  }
  
  private static Insets getInsets(Insets[] paramArrayOfInsets, int paramInt)
  {
    Object localObject = null;
    int i = 1;
    while (i <= 64)
    {
      if ((paramInt & i) != 0)
      {
        Insets localInsets = paramArrayOfInsets[Type.indexOf(i)];
        if (localInsets != null) {
          if (localObject == null) {
            localObject = localInsets;
          } else {
            localObject = Insets.max((Insets)localObject, localInsets);
          }
        }
      }
      i <<= 1;
    }
    if (localObject == null) {
      localObject = Insets.NONE;
    }
    return (Insets)localObject;
  }
  
  private static Insets insetInsets(Insets paramInsets, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = Math.max(0, paramInsets.left - paramInt1);
    int j = Math.max(0, paramInsets.top - paramInt2);
    int k = Math.max(0, paramInsets.right - paramInt3);
    int m = Math.max(0, paramInsets.bottom - paramInt4);
    if ((i == paramInt1) && (j == paramInt2) && (k == paramInt3) && (m == paramInt4)) {
      return paramInsets;
    }
    return Insets.of(i, j, k, m);
  }
  
  private static Insets[] insetInsets(Insets[] paramArrayOfInsets, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 0;
    int j = 0;
    while (j < 7)
    {
      Insets localInsets1 = paramArrayOfInsets[j];
      int k;
      Insets[] arrayOfInsets;
      if (localInsets1 == null)
      {
        k = i;
        arrayOfInsets = paramArrayOfInsets;
      }
      else
      {
        Insets localInsets2 = insetInsets(localInsets1, paramInt1, paramInt2, paramInt3, paramInt4);
        k = i;
        arrayOfInsets = paramArrayOfInsets;
        if (localInsets2 != localInsets1)
        {
          k = i;
          arrayOfInsets = paramArrayOfInsets;
          if (i == 0)
          {
            arrayOfInsets = (Insets[])paramArrayOfInsets.clone();
            k = 1;
          }
          arrayOfInsets[j] = localInsets2;
        }
      }
      j++;
      i = k;
      paramArrayOfInsets = arrayOfInsets;
    }
    return paramArrayOfInsets;
  }
  
  private static void setInsets(Insets[] paramArrayOfInsets, int paramInt, Insets paramInsets)
  {
    int i = 1;
    while (i <= 64)
    {
      if ((paramInt & i) != 0) {
        paramArrayOfInsets[Type.indexOf(i)] = paramInsets;
      }
      i <<= 1;
    }
  }
  
  public WindowInsets consumeDisplayCutout()
  {
    Insets[] arrayOfInsets1;
    if (this.mSystemWindowInsetsConsumed) {
      arrayOfInsets1 = null;
    } else {
      arrayOfInsets1 = this.mTypeInsetsMap;
    }
    Insets[] arrayOfInsets2;
    if (this.mStableInsetsConsumed) {
      arrayOfInsets2 = null;
    } else {
      arrayOfInsets2 = this.mTypeMaxInsetsMap;
    }
    return new WindowInsets(arrayOfInsets1, arrayOfInsets2, this.mTypeVisibilityMap, this.mIsRound, this.mAlwaysConsumeSystemBars, null);
  }
  
  public WindowInsets consumeStableInsets()
  {
    Insets[] arrayOfInsets;
    if (this.mSystemWindowInsetsConsumed) {
      arrayOfInsets = null;
    } else {
      arrayOfInsets = this.mTypeInsetsMap;
    }
    return new WindowInsets(arrayOfInsets, null, this.mTypeVisibilityMap, this.mIsRound, this.mAlwaysConsumeSystemBars, displayCutoutCopyConstructorArgument(this));
  }
  
  public WindowInsets consumeSystemWindowInsets()
  {
    Insets[] arrayOfInsets;
    if (this.mStableInsetsConsumed) {
      arrayOfInsets = null;
    } else {
      arrayOfInsets = this.mTypeMaxInsetsMap;
    }
    return new WindowInsets(null, arrayOfInsets, this.mTypeVisibilityMap, this.mIsRound, this.mAlwaysConsumeSystemBars, displayCutoutCopyConstructorArgument(this));
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && ((paramObject instanceof WindowInsets)))
    {
      paramObject = (WindowInsets)paramObject;
      if ((this.mIsRound != ((WindowInsets)paramObject).mIsRound) || (this.mAlwaysConsumeSystemBars != ((WindowInsets)paramObject).mAlwaysConsumeSystemBars) || (this.mSystemWindowInsetsConsumed != ((WindowInsets)paramObject).mSystemWindowInsetsConsumed) || (this.mStableInsetsConsumed != ((WindowInsets)paramObject).mStableInsetsConsumed) || (this.mDisplayCutoutConsumed != ((WindowInsets)paramObject).mDisplayCutoutConsumed) || (!Arrays.equals(this.mTypeInsetsMap, ((WindowInsets)paramObject).mTypeInsetsMap)) || (!Arrays.equals(this.mTypeMaxInsetsMap, ((WindowInsets)paramObject).mTypeMaxInsetsMap)) || (!Arrays.equals(this.mTypeVisibilityMap, ((WindowInsets)paramObject).mTypeVisibilityMap)) || (!Objects.equals(this.mDisplayCutout, ((WindowInsets)paramObject).mDisplayCutout))) {
        bool = false;
      }
      return bool;
    }
    return false;
  }
  
  public DisplayCutout getDisplayCutout()
  {
    return this.mDisplayCutout;
  }
  
  public Insets getInsets(int paramInt)
  {
    return getInsets(this.mTypeInsetsMap, paramInt);
  }
  
  public Insets getMandatorySystemGestureInsets()
  {
    return getInsets(this.mTypeInsetsMap, 16);
  }
  
  public Insets getMaxInsets(int paramInt)
    throws IllegalArgumentException
  {
    if ((paramInt & 0x2) == 0) {
      return getInsets(this.mTypeMaxInsetsMap, paramInt);
    }
    throw new IllegalArgumentException("Unable to query the maximum insets for IME");
  }
  
  public int getStableInsetBottom()
  {
    return getStableInsets().bottom;
  }
  
  public int getStableInsetLeft()
  {
    return getStableInsets().left;
  }
  
  public int getStableInsetRight()
  {
    return getStableInsets().right;
  }
  
  public int getStableInsetTop()
  {
    return getStableInsets().top;
  }
  
  public Insets getStableInsets()
  {
    return getInsets(this.mTypeMaxInsetsMap, Type.compatSystemInsets());
  }
  
  public Insets getSystemGestureInsets()
  {
    return getInsets(this.mTypeInsetsMap, 8);
  }
  
  public int getSystemWindowInsetBottom()
  {
    return getSystemWindowInsets().bottom;
  }
  
  public int getSystemWindowInsetLeft()
  {
    return getSystemWindowInsets().left;
  }
  
  public int getSystemWindowInsetRight()
  {
    return getSystemWindowInsets().right;
  }
  
  public int getSystemWindowInsetTop()
  {
    return getSystemWindowInsets().top;
  }
  
  public Insets getSystemWindowInsets()
  {
    return getInsets(this.mTypeInsetsMap, Type.compatSystemInsets());
  }
  
  @Deprecated
  public Rect getSystemWindowInsetsAsRect()
  {
    if (this.mTempRect == null) {
      this.mTempRect = new Rect();
    }
    Insets localInsets = getSystemWindowInsets();
    this.mTempRect.set(localInsets.left, localInsets.top, localInsets.right, localInsets.bottom);
    return this.mTempRect;
  }
  
  public Insets getTappableElementInsets()
  {
    return getInsets(this.mTypeInsetsMap, 32);
  }
  
  public boolean hasInsets()
  {
    boolean bool;
    if ((getInsets(this.mTypeInsetsMap, Type.all()).equals(Insets.NONE)) && (getInsets(this.mTypeMaxInsetsMap, Type.all()).equals(Insets.NONE)) && (this.mDisplayCutout == null)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean hasStableInsets()
  {
    return getStableInsets().equals(Insets.NONE) ^ true;
  }
  
  public boolean hasSystemWindowInsets()
  {
    return getSystemWindowInsets().equals(Insets.NONE) ^ true;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(Arrays.hashCode(this.mTypeInsetsMap)), Integer.valueOf(Arrays.hashCode(this.mTypeMaxInsetsMap)), Integer.valueOf(Arrays.hashCode(this.mTypeVisibilityMap)), Boolean.valueOf(this.mIsRound), this.mDisplayCutout, Boolean.valueOf(this.mAlwaysConsumeSystemBars), Boolean.valueOf(this.mSystemWindowInsetsConsumed), Boolean.valueOf(this.mStableInsetsConsumed), Boolean.valueOf(this.mDisplayCutoutConsumed) });
  }
  
  public WindowInsets inset(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Preconditions.checkArgumentNonnegative(paramInt1);
    Preconditions.checkArgumentNonnegative(paramInt2);
    Preconditions.checkArgumentNonnegative(paramInt3);
    Preconditions.checkArgumentNonnegative(paramInt4);
    Insets[] arrayOfInsets1;
    if (this.mSystemWindowInsetsConsumed) {
      arrayOfInsets1 = null;
    } else {
      arrayOfInsets1 = insetInsets(this.mTypeInsetsMap, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    Insets[] arrayOfInsets2;
    if (this.mStableInsetsConsumed) {
      arrayOfInsets2 = null;
    } else {
      arrayOfInsets2 = insetInsets(this.mTypeMaxInsetsMap, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    boolean[] arrayOfBoolean = this.mTypeVisibilityMap;
    boolean bool1 = this.mIsRound;
    boolean bool2 = this.mAlwaysConsumeSystemBars;
    DisplayCutout localDisplayCutout;
    if (this.mDisplayCutoutConsumed)
    {
      localDisplayCutout = null;
    }
    else
    {
      localDisplayCutout = this.mDisplayCutout;
      if (localDisplayCutout == null) {
        localDisplayCutout = DisplayCutout.NO_CUTOUT;
      } else {
        localDisplayCutout = localDisplayCutout.inset(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    return new WindowInsets(arrayOfInsets1, arrayOfInsets2, arrayOfBoolean, bool1, bool2, localDisplayCutout);
  }
  
  public WindowInsets inset(Insets paramInsets)
  {
    return inset(paramInsets.left, paramInsets.top, paramInsets.right, paramInsets.bottom);
  }
  
  @Deprecated
  public WindowInsets inset(Rect paramRect)
  {
    return inset(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public boolean isConsumed()
  {
    boolean bool;
    if ((this.mSystemWindowInsetsConsumed) && (this.mStableInsetsConsumed) && (this.mDisplayCutoutConsumed)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isRound()
  {
    return this.mIsRound;
  }
  
  boolean isSystemWindowInsetsConsumed()
  {
    return this.mSystemWindowInsetsConsumed;
  }
  
  public boolean isVisible(int paramInt)
  {
    int i = 1;
    while (i <= 64)
    {
      if (((paramInt & i) != 0) && (this.mTypeVisibilityMap[Type.indexOf(i)] == 0)) {
        return false;
      }
      i <<= 1;
    }
    return true;
  }
  
  @Deprecated
  public WindowInsets replaceSystemWindowInsets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.mSystemWindowInsetsConsumed) {
      return this;
    }
    return new Builder(this).setSystemWindowInsets(Insets.of(paramInt1, paramInt2, paramInt3, paramInt4)).build();
  }
  
  @Deprecated
  public WindowInsets replaceSystemWindowInsets(Rect paramRect)
  {
    return replaceSystemWindowInsets(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public boolean shouldAlwaysConsumeSystemBars()
  {
    return this.mAlwaysConsumeSystemBars;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("WindowInsets{systemWindowInsets=");
    localStringBuilder.append(getSystemWindowInsets());
    localStringBuilder.append(" stableInsets=");
    localStringBuilder.append(getStableInsets());
    localStringBuilder.append(" sysGestureInsets=");
    localStringBuilder.append(getSystemGestureInsets());
    Object localObject = this.mDisplayCutout;
    String str = "";
    if (localObject != null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(" cutout=");
      ((StringBuilder)localObject).append(this.mDisplayCutout);
      localObject = ((StringBuilder)localObject).toString();
    }
    else
    {
      localObject = "";
    }
    localStringBuilder.append((String)localObject);
    localObject = str;
    if (isRound()) {
      localObject = " round";
    }
    localStringBuilder.append((String)localObject);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public static final class Builder
  {
    private boolean mAlwaysConsumeSystemBars;
    private DisplayCutout mDisplayCutout;
    private boolean mIsRound;
    private boolean mStableInsetsConsumed = true;
    private boolean mSystemInsetsConsumed = true;
    private final Insets[] mTypeInsetsMap;
    private final Insets[] mTypeMaxInsetsMap;
    private final boolean[] mTypeVisibilityMap;
    
    public Builder()
    {
      this.mTypeInsetsMap = new Insets[7];
      this.mTypeMaxInsetsMap = new Insets[7];
      this.mTypeVisibilityMap = new boolean[7];
    }
    
    public Builder(WindowInsets paramWindowInsets)
    {
      this.mTypeInsetsMap = ((Insets[])paramWindowInsets.mTypeInsetsMap.clone());
      this.mTypeMaxInsetsMap = ((Insets[])paramWindowInsets.mTypeMaxInsetsMap.clone());
      this.mTypeVisibilityMap = ((boolean[])paramWindowInsets.mTypeVisibilityMap.clone());
      this.mSystemInsetsConsumed = paramWindowInsets.mSystemWindowInsetsConsumed;
      this.mStableInsetsConsumed = paramWindowInsets.mStableInsetsConsumed;
      this.mDisplayCutout = WindowInsets.displayCutoutCopyConstructorArgument(paramWindowInsets);
      this.mIsRound = paramWindowInsets.mIsRound;
      this.mAlwaysConsumeSystemBars = paramWindowInsets.mAlwaysConsumeSystemBars;
    }
    
    public WindowInsets build()
    {
      Insets[] arrayOfInsets1;
      if (this.mSystemInsetsConsumed) {
        arrayOfInsets1 = null;
      } else {
        arrayOfInsets1 = this.mTypeInsetsMap;
      }
      Insets[] arrayOfInsets2;
      if (this.mStableInsetsConsumed) {
        arrayOfInsets2 = null;
      } else {
        arrayOfInsets2 = this.mTypeMaxInsetsMap;
      }
      return new WindowInsets(arrayOfInsets1, arrayOfInsets2, this.mTypeVisibilityMap, this.mIsRound, this.mAlwaysConsumeSystemBars, this.mDisplayCutout);
    }
    
    public Builder setAlwaysConsumeSystemBars(boolean paramBoolean)
    {
      this.mAlwaysConsumeSystemBars = paramBoolean;
      return this;
    }
    
    public Builder setDisplayCutout(DisplayCutout paramDisplayCutout)
    {
      if (paramDisplayCutout == null) {
        paramDisplayCutout = DisplayCutout.NO_CUTOUT;
      }
      this.mDisplayCutout = paramDisplayCutout;
      return this;
    }
    
    public Builder setInsets(int paramInt, Insets paramInsets)
    {
      Preconditions.checkNotNull(paramInsets);
      WindowInsets.setInsets(this.mTypeInsetsMap, paramInt, paramInsets);
      this.mSystemInsetsConsumed = false;
      return this;
    }
    
    public Builder setMandatorySystemGestureInsets(Insets paramInsets)
    {
      WindowInsets.setInsets(this.mTypeInsetsMap, 16, paramInsets);
      return this;
    }
    
    public Builder setMaxInsets(int paramInt, Insets paramInsets)
      throws IllegalArgumentException
    {
      if (paramInt != 2)
      {
        Preconditions.checkNotNull(paramInsets);
        WindowInsets.setInsets(this.mTypeMaxInsetsMap, paramInt, paramInsets);
        this.mStableInsetsConsumed = false;
        return this;
      }
      throw new IllegalArgumentException("Maximum inset not available for IME");
    }
    
    public Builder setRound(boolean paramBoolean)
    {
      this.mIsRound = paramBoolean;
      return this;
    }
    
    public Builder setStableInsets(Insets paramInsets)
    {
      Preconditions.checkNotNull(paramInsets);
      WindowInsets.assignCompatInsets(this.mTypeMaxInsetsMap, paramInsets.toRect());
      this.mStableInsetsConsumed = false;
      return this;
    }
    
    public Builder setSystemGestureInsets(Insets paramInsets)
    {
      WindowInsets.setInsets(this.mTypeInsetsMap, 8, paramInsets);
      return this;
    }
    
    public Builder setSystemWindowInsets(Insets paramInsets)
    {
      Preconditions.checkNotNull(paramInsets);
      WindowInsets.assignCompatInsets(this.mTypeInsetsMap, paramInsets.toRect());
      this.mSystemInsetsConsumed = false;
      return this;
    }
    
    public Builder setTappableElementInsets(Insets paramInsets)
    {
      WindowInsets.setInsets(this.mTypeInsetsMap, 32, paramInsets);
      return this;
    }
    
    public Builder setVisible(int paramInt, boolean paramBoolean)
    {
      int i = 1;
      while (i <= 64)
      {
        if ((paramInt & i) != 0) {
          this.mTypeVisibilityMap[WindowInsets.Type.indexOf(i)] = paramBoolean;
        }
        i <<= 1;
      }
      return this;
    }
  }
  
  public static final class Type
  {
    static final int FIRST = 1;
    static final int IME = 2;
    static final int LAST = 64;
    static final int MANDATORY_SYSTEM_GESTURES = 16;
    static final int SIDE_BARS = 4;
    static final int SIZE = 7;
    static final int SYSTEM_GESTURES = 8;
    static final int TAPPABLE_ELEMENT = 32;
    static final int TOP_BAR = 1;
    static final int WINDOW_DECOR = 64;
    
    public static int all()
    {
      return -1;
    }
    
    static int compatSystemInsets()
    {
      return 7;
    }
    
    public static int ime()
    {
      return 2;
    }
    
    static int indexOf(int paramInt)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 4)
          {
            if (paramInt != 8)
            {
              if (paramInt != 16)
              {
                if (paramInt != 32)
                {
                  if (paramInt == 64) {
                    return 6;
                  }
                  StringBuilder localStringBuilder = new StringBuilder();
                  localStringBuilder.append("type needs to be >= FIRST and <= LAST, type=");
                  localStringBuilder.append(paramInt);
                  throw new IllegalArgumentException(localStringBuilder.toString());
                }
                return 5;
              }
              return 4;
            }
            return 3;
          }
          return 2;
        }
        return 1;
      }
      return 0;
    }
    
    public static int mandatorySystemGestures()
    {
      return 16;
    }
    
    public static int sideBars()
    {
      return 4;
    }
    
    public static int systemBars()
    {
      return 5;
    }
    
    public static int systemGestures()
    {
      return 8;
    }
    
    public static int tappableElement()
    {
      return 32;
    }
    
    public static int topBar()
    {
      return 1;
    }
    
    public static int windowDecor()
    {
      return 64;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface InsetType {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowInsets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */