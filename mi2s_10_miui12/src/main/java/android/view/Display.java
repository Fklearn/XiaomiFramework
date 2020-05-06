package android.view;

import android.annotation.UnsupportedAppUsage;
import android.app.WindowConfiguration;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.ColorSpace;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.MiuiMultiWindowAdapter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public final class Display
{
  private static final int CACHED_APP_SIZE_DURATION_MILLIS = 20;
  public static final int COLOR_MODE_ADOBE_RGB = 8;
  public static final int COLOR_MODE_BT601_525 = 3;
  public static final int COLOR_MODE_BT601_525_UNADJUSTED = 4;
  public static final int COLOR_MODE_BT601_625 = 1;
  public static final int COLOR_MODE_BT601_625_UNADJUSTED = 2;
  public static final int COLOR_MODE_BT709 = 5;
  public static final int COLOR_MODE_DCI_P3 = 6;
  public static final int COLOR_MODE_DEFAULT = 0;
  public static final int COLOR_MODE_DISPLAY_P3 = 9;
  public static final int COLOR_MODE_INVALID = -1;
  public static final int COLOR_MODE_SRGB = 7;
  private static final boolean DEBUG = false;
  public static final int DEFAULT_DISPLAY = 0;
  public static final int FLAG_CAN_SHOW_WITH_INSECURE_KEYGUARD = 32;
  public static final int FLAG_PRESENTATION = 8;
  public static final int FLAG_PRIVATE = 4;
  public static final int FLAG_ROUND = 16;
  public static final int FLAG_SCALING_DISABLED = 1073741824;
  public static final int FLAG_SECURE = 2;
  public static final int FLAG_SHOULD_SHOW_SYSTEM_DECORATIONS = 64;
  public static final int FLAG_SUPPORTS_PROTECTED_BUFFERS = 1;
  public static final int INVALID_DISPLAY = -1;
  public static final int REMOVE_MODE_DESTROY_CONTENT = 1;
  public static final int REMOVE_MODE_MOVE_CONTENT_TO_PRIMARY = 0;
  public static final int STATE_DOZE = 3;
  public static final int STATE_DOZE_SUSPEND = 4;
  public static final int STATE_OFF = 1;
  public static final int STATE_ON = 2;
  public static final int STATE_ON_SUSPEND = 6;
  public static final int STATE_UNKNOWN = 0;
  public static final int STATE_VR = 5;
  private static final String TAG = "Display";
  public static final int TYPE_BUILT_IN = 1;
  @UnsupportedAppUsage
  public static final int TYPE_HDMI = 2;
  public static final int TYPE_OVERLAY = 4;
  @UnsupportedAppUsage
  public static final int TYPE_UNKNOWN = 0;
  @UnsupportedAppUsage
  public static final int TYPE_VIRTUAL = 5;
  @UnsupportedAppUsage
  public static final int TYPE_WIFI = 3;
  private final DisplayAddress mAddress;
  private int mCachedAppHeightCompat;
  private int mCachedAppWidthCompat;
  private DisplayAdjustments mDisplayAdjustments;
  private final int mDisplayId;
  @UnsupportedAppUsage
  private DisplayInfo mDisplayInfo;
  private final int mFlags;
  private final DisplayManagerGlobal mGlobal;
  private boolean mIsValid;
  private long mLastCachedAppSizeUpdate;
  private final int mLayerStack;
  private final String mOwnerPackageName;
  private final int mOwnerUid;
  private final Resources mResources;
  private final DisplayMetrics mTempMetrics = new DisplayMetrics();
  private final int mType;
  
  public Display(DisplayManagerGlobal paramDisplayManagerGlobal, int paramInt, DisplayInfo paramDisplayInfo, Resources paramResources)
  {
    this(paramDisplayManagerGlobal, paramInt, paramDisplayInfo, null, paramResources);
  }
  
  public Display(DisplayManagerGlobal paramDisplayManagerGlobal, int paramInt, DisplayInfo paramDisplayInfo, DisplayAdjustments paramDisplayAdjustments)
  {
    this(paramDisplayManagerGlobal, paramInt, paramDisplayInfo, paramDisplayAdjustments, null);
  }
  
  private Display(DisplayManagerGlobal paramDisplayManagerGlobal, int paramInt, DisplayInfo paramDisplayInfo, DisplayAdjustments paramDisplayAdjustments, Resources paramResources)
  {
    this.mGlobal = paramDisplayManagerGlobal;
    this.mDisplayId = paramInt;
    this.mDisplayInfo = paramDisplayInfo;
    this.mResources = paramResources;
    paramDisplayManagerGlobal = this.mResources;
    if (paramDisplayManagerGlobal != null) {
      paramDisplayManagerGlobal = new DisplayAdjustments(paramDisplayManagerGlobal.getConfiguration());
    } else if (paramDisplayAdjustments != null) {
      paramDisplayManagerGlobal = new DisplayAdjustments(paramDisplayAdjustments);
    } else {
      paramDisplayManagerGlobal = null;
    }
    this.mDisplayAdjustments = paramDisplayManagerGlobal;
    this.mIsValid = true;
    this.mLayerStack = paramDisplayInfo.layerStack;
    this.mFlags = paramDisplayInfo.flags;
    this.mType = paramDisplayInfo.type;
    this.mAddress = paramDisplayInfo.address;
    this.mOwnerUid = paramDisplayInfo.ownerUid;
    this.mOwnerPackageName = paramDisplayInfo.ownerPackageName;
  }
  
  public static boolean hasAccess(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool;
    if (((paramInt2 & 0x4) != 0) && (paramInt1 != paramInt3) && (paramInt1 != 1000) && (paramInt1 != 0) && (!DisplayManagerGlobal.getInstance().isUidPresentOnDisplay(paramInt1, paramInt4))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean hasSmallFreeformFeature()
  {
    return MiuiMultiWindowAdapter.hasSmallFreeformFeature();
  }
  
  public static boolean isDozeState(int paramInt)
  {
    boolean bool;
    if ((paramInt != 3) && (paramInt != 4)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isSuspendedState(int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = bool1;
    if (paramInt != 1)
    {
      bool2 = bool1;
      if (paramInt != 4) {
        if (paramInt == 6) {
          bool2 = bool1;
        } else {
          bool2 = false;
        }
      }
    }
    return bool2;
  }
  
  public static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 6: 
      return "ON_SUSPEND";
    case 5: 
      return "VR";
    case 4: 
      return "DOZE_SUSPEND";
    case 3: 
      return "DOZE";
    case 2: 
      return "ON";
    case 1: 
      return "OFF";
    }
    return "UNKNOWN";
  }
  
  public static String typeToString(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 3)
          {
            if (paramInt != 4)
            {
              if (paramInt != 5) {
                return Integer.toString(paramInt);
              }
              return "VIRTUAL";
            }
            return "OVERLAY";
          }
          return "WIFI";
        }
        return "HDMI";
      }
      return "BUILT_IN";
    }
    return "UNKNOWN";
  }
  
  private void updateCachedAppSizeIfNeededLocked()
  {
    long l = SystemClock.uptimeMillis();
    if (l > this.mLastCachedAppSizeUpdate + 20L)
    {
      updateDisplayInfoLocked();
      this.mDisplayInfo.getAppMetrics(this.mTempMetrics, getDisplayAdjustments());
      this.mCachedAppWidthCompat = this.mTempMetrics.widthPixels;
      this.mCachedAppHeightCompat = this.mTempMetrics.heightPixels;
      this.mLastCachedAppSizeUpdate = l;
    }
  }
  
  private void updateDisplayInfoLocked()
  {
    DisplayInfo localDisplayInfo = this.mGlobal.getDisplayInfo(this.mDisplayId);
    if (localDisplayInfo == null)
    {
      if (this.mIsValid) {
        this.mIsValid = false;
      }
    }
    else
    {
      this.mDisplayInfo = localDisplayInfo;
      if (!this.mIsValid) {
        this.mIsValid = true;
      }
    }
  }
  
  @UnsupportedAppUsage
  public DisplayAddress getAddress()
  {
    return this.mAddress;
  }
  
  public long getAppVsyncOffsetNanos()
  {
    try
    {
      updateDisplayInfoLocked();
      long l = this.mDisplayInfo.appVsyncOffsetNanos;
      return l;
    }
    finally {}
  }
  
  public int getColorMode()
  {
    try
    {
      updateDisplayInfoLocked();
      int i = this.mDisplayInfo.colorMode;
      return i;
    }
    finally {}
  }
  
  public void getCurrentSizeRange(Point paramPoint1, Point paramPoint2)
  {
    try
    {
      updateDisplayInfoLocked();
      paramPoint1.x = this.mDisplayInfo.smallestNominalAppWidth;
      paramPoint1.y = this.mDisplayInfo.smallestNominalAppHeight;
      paramPoint2.x = this.mDisplayInfo.largestNominalAppWidth;
      paramPoint2.y = this.mDisplayInfo.largestNominalAppHeight;
      return;
    }
    finally {}
  }
  
  public DisplayCutout getCutout()
  {
    try
    {
      updateDisplayInfoLocked();
      DisplayCutout localDisplayCutout = this.mDisplayInfo.displayCutout;
      return localDisplayCutout;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public DisplayAdjustments getDisplayAdjustments()
  {
    Object localObject = this.mResources;
    if (localObject != null)
    {
      localObject = ((Resources)localObject).getDisplayAdjustments();
      if (!this.mDisplayAdjustments.equals(localObject)) {
        this.mDisplayAdjustments = new DisplayAdjustments((DisplayAdjustments)localObject);
      }
    }
    return this.mDisplayAdjustments;
  }
  
  public int getDisplayId()
  {
    return this.mDisplayId;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  public boolean getDisplayInfo(DisplayInfo paramDisplayInfo)
  {
    try
    {
      updateDisplayInfoLocked();
      paramDisplayInfo.copyFrom(this.mDisplayInfo);
      boolean bool = this.mIsValid;
      return bool;
    }
    finally {}
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public HdrCapabilities getHdrCapabilities()
  {
    try
    {
      updateDisplayInfoLocked();
      HdrCapabilities localHdrCapabilities = this.mDisplayInfo.hdrCapabilities;
      return localHdrCapabilities;
    }
    finally {}
  }
  
  @Deprecated
  public int getHeight()
  {
    try
    {
      updateCachedAppSizeIfNeededLocked();
      this.mCachedAppHeightCompat = MiuiMultiWindowAdapter.getHeight(this.mResources, this.mCachedAppHeightCompat);
      int i = this.mCachedAppHeightCompat;
      return i;
    }
    finally {}
  }
  
  public int getLayerStack()
  {
    return this.mLayerStack;
  }
  
  @UnsupportedAppUsage
  public int getMaximumSizeDimension()
  {
    try
    {
      updateDisplayInfoLocked();
      int i = Math.max(this.mDisplayInfo.logicalWidth, this.mDisplayInfo.logicalHeight);
      return i;
    }
    finally {}
  }
  
  public void getMetrics(DisplayMetrics paramDisplayMetrics)
  {
    try
    {
      updateDisplayInfoLocked();
      this.mDisplayInfo.getAppMetrics(paramDisplayMetrics, getDisplayAdjustments());
      MiuiMultiWindowAdapter.getMetrics(this.mResources, paramDisplayMetrics);
      return;
    }
    finally {}
  }
  
  public Mode getMode()
  {
    try
    {
      updateDisplayInfoLocked();
      Mode localMode = this.mDisplayInfo.getMode();
      return localMode;
    }
    finally {}
  }
  
  public String getName()
  {
    try
    {
      updateDisplayInfoLocked();
      String str = this.mDisplayInfo.name;
      return str;
    }
    finally {}
  }
  
  @Deprecated
  public int getOrientation()
  {
    return getRotation();
  }
  
  public void getOverscanInsets(Rect paramRect)
  {
    try
    {
      updateDisplayInfoLocked();
      paramRect.set(this.mDisplayInfo.overscanLeft, this.mDisplayInfo.overscanTop, this.mDisplayInfo.overscanRight, this.mDisplayInfo.overscanBottom);
      return;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public String getOwnerPackageName()
  {
    return this.mOwnerPackageName;
  }
  
  public int getOwnerUid()
  {
    return this.mOwnerUid;
  }
  
  @Deprecated
  public int getPixelFormat()
  {
    return 1;
  }
  
  public ColorSpace getPreferredWideGamutColorSpace()
  {
    try
    {
      updateDisplayInfoLocked();
      if (this.mDisplayInfo.isWideColorGamut())
      {
        ColorSpace localColorSpace = this.mGlobal.getPreferredWideGamutColorSpace();
        return localColorSpace;
      }
      return null;
    }
    finally {}
  }
  
  public long getPresentationDeadlineNanos()
  {
    try
    {
      updateDisplayInfoLocked();
      long l = this.mDisplayInfo.presentationDeadlineNanos;
      return l;
    }
    finally {}
  }
  
  public void getRealMetrics(DisplayMetrics paramDisplayMetrics)
  {
    try
    {
      updateDisplayInfoLocked();
      this.mDisplayInfo.getLogicalMetrics(paramDisplayMetrics, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
      MiuiMultiWindowAdapter.getMetrics(this.mResources, paramDisplayMetrics);
      return;
    }
    finally {}
  }
  
  public void getRealSize(Point paramPoint)
  {
    try
    {
      updateDisplayInfoLocked();
      paramPoint.x = this.mDisplayInfo.logicalWidth;
      paramPoint.y = this.mDisplayInfo.logicalHeight;
      MiuiMultiWindowAdapter.getSize(this.mResources, paramPoint);
      return;
    }
    finally {}
  }
  
  public void getRectSize(Rect paramRect)
  {
    try
    {
      updateDisplayInfoLocked();
      this.mDisplayInfo.getAppMetrics(this.mTempMetrics, getDisplayAdjustments());
      paramRect.set(0, 0, this.mTempMetrics.widthPixels, this.mTempMetrics.heightPixels);
      return;
    }
    finally {}
  }
  
  public float getRefreshRate()
  {
    try
    {
      updateDisplayInfoLocked();
      float f = this.mDisplayInfo.getMode().getRefreshRate();
      return f;
    }
    finally {}
  }
  
  public int getRemoveMode()
  {
    return this.mDisplayInfo.removeMode;
  }
  
  public int getRotation()
  {
    Resources localResources = this.mResources;
    if ((localResources != null) && (localResources.getConfiguration().windowConfiguration.getWindowingMode() == 5)) {
      return this.mResources.getConfiguration().windowConfiguration.getRotation();
    }
    try
    {
      updateDisplayInfoLocked();
      int i = this.mDisplayInfo.rotation;
      return i;
    }
    finally {}
  }
  
  public void getSize(Point paramPoint)
  {
    try
    {
      updateDisplayInfoLocked();
      this.mDisplayInfo.getAppMetrics(this.mTempMetrics, getDisplayAdjustments());
      paramPoint.x = this.mTempMetrics.widthPixels;
      paramPoint.y = this.mTempMetrics.heightPixels;
      MiuiMultiWindowAdapter.getSize(this.mResources, paramPoint);
      return;
    }
    finally {}
  }
  
  public int getState()
  {
    try
    {
      updateDisplayInfoLocked();
      int i;
      if (this.mIsValid) {
        i = this.mDisplayInfo.state;
      } else {
        i = 0;
      }
      return i;
    }
    finally {}
  }
  
  public int[] getSupportedColorModes()
  {
    try
    {
      updateDisplayInfoLocked();
      int[] arrayOfInt = this.mDisplayInfo.supportedColorModes;
      arrayOfInt = Arrays.copyOf(arrayOfInt, arrayOfInt.length);
      return arrayOfInt;
    }
    finally {}
  }
  
  public Mode[] getSupportedModes()
  {
    try
    {
      updateDisplayInfoLocked();
      Mode[] arrayOfMode = this.mDisplayInfo.supportedModes;
      arrayOfMode = (Mode[])Arrays.copyOf(arrayOfMode, arrayOfMode.length);
      return arrayOfMode;
    }
    finally {}
  }
  
  @Deprecated
  public float[] getSupportedRefreshRates()
  {
    try
    {
      updateDisplayInfoLocked();
      float[] arrayOfFloat = this.mDisplayInfo.getDefaultRefreshRates();
      return arrayOfFloat;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public int getType()
  {
    return this.mType;
  }
  
  public String getUniqueId()
  {
    return this.mDisplayInfo.uniqueId;
  }
  
  @Deprecated
  public int getWidth()
  {
    try
    {
      updateCachedAppSizeIfNeededLocked();
      this.mCachedAppWidthCompat = MiuiMultiWindowAdapter.getWidth(this.mResources, this.mCachedAppWidthCompat);
      int i = this.mCachedAppWidthCompat;
      return i;
    }
    finally {}
  }
  
  public boolean hasAccess(int paramInt)
  {
    return hasAccess(paramInt, this.mFlags, this.mOwnerUid, this.mDisplayId);
  }
  
  public boolean isHdr()
  {
    try
    {
      updateDisplayInfoLocked();
      boolean bool = this.mDisplayInfo.isHdr();
      return bool;
    }
    finally {}
  }
  
  public boolean isPublicPresentation()
  {
    boolean bool;
    if ((this.mFlags & 0xC) == 8) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isValid()
  {
    try
    {
      updateDisplayInfoLocked();
      boolean bool = this.mIsValid;
      return bool;
    }
    finally {}
  }
  
  public boolean isWideColorGamut()
  {
    try
    {
      updateDisplayInfoLocked();
      boolean bool = this.mDisplayInfo.isWideColorGamut();
      return bool;
    }
    finally {}
  }
  
  public void requestColorMode(int paramInt)
  {
    this.mGlobal.requestColorMode(this.mDisplayId, paramInt);
  }
  
  public String toString()
  {
    try
    {
      updateDisplayInfoLocked();
      this.mDisplayInfo.getAppMetrics(this.mTempMetrics, getDisplayAdjustments());
      Object localObject1 = new java/lang/StringBuilder;
      ((StringBuilder)localObject1).<init>();
      ((StringBuilder)localObject1).append("Display id ");
      ((StringBuilder)localObject1).append(this.mDisplayId);
      ((StringBuilder)localObject1).append(": ");
      ((StringBuilder)localObject1).append(this.mDisplayInfo);
      ((StringBuilder)localObject1).append(", ");
      ((StringBuilder)localObject1).append(this.mTempMetrics);
      ((StringBuilder)localObject1).append(", isValid=");
      ((StringBuilder)localObject1).append(this.mIsValid);
      localObject1 = ((StringBuilder)localObject1).toString();
      return (String)localObject1;
    }
    finally {}
  }
  
  public static final class HdrCapabilities
    implements Parcelable
  {
    public static final Parcelable.Creator<HdrCapabilities> CREATOR = new Parcelable.Creator()
    {
      public Display.HdrCapabilities createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Display.HdrCapabilities(paramAnonymousParcel, null);
      }
      
      public Display.HdrCapabilities[] newArray(int paramAnonymousInt)
      {
        return new Display.HdrCapabilities[paramAnonymousInt];
      }
    };
    public static final int HDR_TYPE_DOLBY_VISION = 1;
    public static final int HDR_TYPE_HDR10 = 2;
    public static final int HDR_TYPE_HDR10_PLUS = 4;
    public static final int HDR_TYPE_HLG = 3;
    public static final float INVALID_LUMINANCE = -1.0F;
    private float mMaxAverageLuminance = -1.0F;
    private float mMaxLuminance = -1.0F;
    private float mMinLuminance = -1.0F;
    private int[] mSupportedHdrTypes = new int[0];
    
    public HdrCapabilities() {}
    
    private HdrCapabilities(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    @UnsupportedAppUsage
    public HdrCapabilities(int[] paramArrayOfInt, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.mSupportedHdrTypes = paramArrayOfInt;
      this.mMaxLuminance = paramFloat1;
      this.mMaxAverageLuminance = paramFloat2;
      this.mMinLuminance = paramFloat3;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof HdrCapabilities)) {
        return false;
      }
      paramObject = (HdrCapabilities)paramObject;
      if ((!Arrays.equals(this.mSupportedHdrTypes, ((HdrCapabilities)paramObject).mSupportedHdrTypes)) || (this.mMaxLuminance != ((HdrCapabilities)paramObject).mMaxLuminance) || (this.mMaxAverageLuminance != ((HdrCapabilities)paramObject).mMaxAverageLuminance) || (this.mMinLuminance != ((HdrCapabilities)paramObject).mMinLuminance)) {
        bool = false;
      }
      return bool;
    }
    
    public float getDesiredMaxAverageLuminance()
    {
      return this.mMaxAverageLuminance;
    }
    
    public float getDesiredMaxLuminance()
    {
      return this.mMaxLuminance;
    }
    
    public float getDesiredMinLuminance()
    {
      return this.mMinLuminance;
    }
    
    public int[] getSupportedHdrTypes()
    {
      return this.mSupportedHdrTypes;
    }
    
    public int hashCode()
    {
      return (((23 * 17 + Arrays.hashCode(this.mSupportedHdrTypes)) * 17 + Float.floatToIntBits(this.mMaxLuminance)) * 17 + Float.floatToIntBits(this.mMaxAverageLuminance)) * 17 + Float.floatToIntBits(this.mMinLuminance);
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      this.mSupportedHdrTypes = new int[i];
      for (int j = 0; j < i; j++) {
        this.mSupportedHdrTypes[j] = paramParcel.readInt();
      }
      this.mMaxLuminance = paramParcel.readFloat();
      this.mMaxAverageLuminance = paramParcel.readFloat();
      this.mMinLuminance = paramParcel.readFloat();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mSupportedHdrTypes.length);
      for (paramInt = 0;; paramInt++)
      {
        int[] arrayOfInt = this.mSupportedHdrTypes;
        if (paramInt >= arrayOfInt.length) {
          break;
        }
        paramParcel.writeInt(arrayOfInt[paramInt]);
      }
      paramParcel.writeFloat(this.mMaxLuminance);
      paramParcel.writeFloat(this.mMaxAverageLuminance);
      paramParcel.writeFloat(this.mMinLuminance);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface HdrType {}
  }
  
  public static final class Mode
    implements Parcelable
  {
    public static final Parcelable.Creator<Mode> CREATOR = new Parcelable.Creator()
    {
      public Display.Mode createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Display.Mode(paramAnonymousParcel, null);
      }
      
      public Display.Mode[] newArray(int paramAnonymousInt)
      {
        return new Display.Mode[paramAnonymousInt];
      }
    };
    public static final Mode[] EMPTY_ARRAY = new Mode[0];
    private final int mHeight;
    private final int mModeId;
    private final float mRefreshRate;
    private final int mWidth;
    
    @UnsupportedAppUsage
    public Mode(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      this.mModeId = paramInt1;
      this.mWidth = paramInt2;
      this.mHeight = paramInt3;
      this.mRefreshRate = paramFloat;
    }
    
    private Mode(Parcel paramParcel)
    {
      this(paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.readFloat());
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof Mode)) {
        return false;
      }
      paramObject = (Mode)paramObject;
      if ((this.mModeId != ((Mode)paramObject).mModeId) || (!matches(((Mode)paramObject).mWidth, ((Mode)paramObject).mHeight, ((Mode)paramObject).mRefreshRate))) {
        bool = false;
      }
      return bool;
    }
    
    public int getModeId()
    {
      return this.mModeId;
    }
    
    public int getPhysicalHeight()
    {
      return this.mHeight;
    }
    
    public int getPhysicalWidth()
    {
      return this.mWidth;
    }
    
    public float getRefreshRate()
    {
      return this.mRefreshRate;
    }
    
    public int hashCode()
    {
      return (((1 * 17 + this.mModeId) * 17 + this.mWidth) * 17 + this.mHeight) * 17 + Float.floatToIntBits(this.mRefreshRate);
    }
    
    public boolean matches(int paramInt1, int paramInt2, float paramFloat)
    {
      boolean bool;
      if ((this.mWidth == paramInt1) && (this.mHeight == paramInt2) && (Float.floatToIntBits(this.mRefreshRate) == Float.floatToIntBits(paramFloat))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("{");
      localStringBuilder.append("id=");
      localStringBuilder.append(this.mModeId);
      localStringBuilder.append(", width=");
      localStringBuilder.append(this.mWidth);
      localStringBuilder.append(", height=");
      localStringBuilder.append(this.mHeight);
      localStringBuilder.append(", fps=");
      localStringBuilder.append(this.mRefreshRate);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mModeId);
      paramParcel.writeInt(this.mWidth);
      paramParcel.writeInt(this.mHeight);
      paramParcel.writeFloat(this.mRefreshRate);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/Display.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */