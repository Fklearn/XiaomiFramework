package android.view;

import android.annotation.UnsupportedAppUsage;
import android.app.WindowConfiguration;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.proto.ProtoOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public final class DisplayInfo
  implements Parcelable
{
  public static final Parcelable.Creator<DisplayInfo> CREATOR = new Parcelable.Creator()
  {
    public DisplayInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DisplayInfo(paramAnonymousParcel, null);
    }
    
    public DisplayInfo[] newArray(int paramAnonymousInt)
    {
      return new DisplayInfo[paramAnonymousInt];
    }
  };
  public DisplayAddress address;
  public int appHeight;
  public long appVsyncOffsetNanos;
  public int appWidth;
  public int colorMode;
  public int defaultModeId;
  @UnsupportedAppUsage(maxTargetSdk=28)
  public DisplayCutout displayCutout;
  public int displayId;
  public int flags;
  public Display.HdrCapabilities hdrCapabilities;
  public int largestNominalAppHeight;
  public int largestNominalAppWidth;
  public int layerStack;
  public int logicalDensityDpi;
  @UnsupportedAppUsage
  public int logicalHeight;
  @UnsupportedAppUsage
  public int logicalWidth;
  public int modeId;
  public String name;
  public int overscanBottom;
  public int overscanLeft;
  public int overscanRight;
  public int overscanTop;
  public String ownerPackageName;
  public int ownerUid;
  public float physicalXDpi;
  public float physicalYDpi;
  public long presentationDeadlineNanos;
  public int removeMode = 0;
  @UnsupportedAppUsage
  public int rotation;
  public int smallestNominalAppHeight;
  public int smallestNominalAppWidth;
  public int state;
  public int[] supportedColorModes = { 0 };
  public Display.Mode[] supportedModes = Display.Mode.EMPTY_ARRAY;
  public int type;
  public String uniqueId;
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769467L)
  public DisplayInfo() {}
  
  private DisplayInfo(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public DisplayInfo(DisplayInfo paramDisplayInfo)
  {
    copyFrom(paramDisplayInfo);
  }
  
  private Display.Mode findMode(int paramInt)
  {
    for (int i = 0;; i++)
    {
      localObject = this.supportedModes;
      if (i >= localObject.length) {
        break;
      }
      if (localObject[i].getModeId() == paramInt) {
        return this.supportedModes[i];
      }
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Unable to locate mode ");
    ((StringBuilder)localObject).append(paramInt);
    throw new IllegalStateException(((StringBuilder)localObject).toString());
  }
  
  private static String flagsToString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramInt & 0x2) != 0) {
      localStringBuilder.append(", FLAG_SECURE");
    }
    if ((paramInt & 0x1) != 0) {
      localStringBuilder.append(", FLAG_SUPPORTS_PROTECTED_BUFFERS");
    }
    if ((paramInt & 0x4) != 0) {
      localStringBuilder.append(", FLAG_PRIVATE");
    }
    if ((paramInt & 0x8) != 0) {
      localStringBuilder.append(", FLAG_PRESENTATION");
    }
    if ((0x40000000 & paramInt) != 0) {
      localStringBuilder.append(", FLAG_SCALING_DISABLED");
    }
    if ((paramInt & 0x10) != 0) {
      localStringBuilder.append(", FLAG_ROUND");
    }
    return localStringBuilder.toString();
  }
  
  private void getMetricsWithSize(DisplayMetrics paramDisplayMetrics, CompatibilityInfo paramCompatibilityInfo, Configuration paramConfiguration, int paramInt1, int paramInt2)
  {
    int i = this.logicalDensityDpi;
    paramDisplayMetrics.noncompatDensityDpi = i;
    paramDisplayMetrics.densityDpi = i;
    float f = i * 0.00625F;
    paramDisplayMetrics.noncompatDensity = f;
    paramDisplayMetrics.density = f;
    f = paramDisplayMetrics.density;
    paramDisplayMetrics.noncompatScaledDensity = f;
    paramDisplayMetrics.scaledDensity = f;
    f = this.physicalXDpi;
    paramDisplayMetrics.noncompatXdpi = f;
    paramDisplayMetrics.xdpi = f;
    f = this.physicalYDpi;
    paramDisplayMetrics.noncompatYdpi = f;
    paramDisplayMetrics.ydpi = f;
    if (paramConfiguration != null) {
      paramConfiguration = paramConfiguration.windowConfiguration.getAppBounds();
    } else {
      paramConfiguration = null;
    }
    if (paramConfiguration != null) {
      paramInt1 = paramConfiguration.width();
    }
    if (paramConfiguration != null) {
      paramInt2 = paramConfiguration.height();
    }
    paramDisplayMetrics.widthPixels = paramInt1;
    paramDisplayMetrics.noncompatWidthPixels = paramInt1;
    paramDisplayMetrics.heightPixels = paramInt2;
    paramDisplayMetrics.noncompatHeightPixels = paramInt2;
    if (!paramCompatibilityInfo.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
      paramCompatibilityInfo.applyToDisplayMetrics(paramDisplayMetrics);
    }
  }
  
  public void copyFrom(DisplayInfo paramDisplayInfo)
  {
    this.layerStack = paramDisplayInfo.layerStack;
    this.flags = paramDisplayInfo.flags;
    this.type = paramDisplayInfo.type;
    this.displayId = paramDisplayInfo.displayId;
    this.address = paramDisplayInfo.address;
    this.name = paramDisplayInfo.name;
    this.uniqueId = paramDisplayInfo.uniqueId;
    this.appWidth = paramDisplayInfo.appWidth;
    this.appHeight = paramDisplayInfo.appHeight;
    this.smallestNominalAppWidth = paramDisplayInfo.smallestNominalAppWidth;
    this.smallestNominalAppHeight = paramDisplayInfo.smallestNominalAppHeight;
    this.largestNominalAppWidth = paramDisplayInfo.largestNominalAppWidth;
    this.largestNominalAppHeight = paramDisplayInfo.largestNominalAppHeight;
    this.logicalWidth = paramDisplayInfo.logicalWidth;
    this.logicalHeight = paramDisplayInfo.logicalHeight;
    this.overscanLeft = paramDisplayInfo.overscanLeft;
    this.overscanTop = paramDisplayInfo.overscanTop;
    this.overscanRight = paramDisplayInfo.overscanRight;
    this.overscanBottom = paramDisplayInfo.overscanBottom;
    this.displayCutout = paramDisplayInfo.displayCutout;
    this.rotation = paramDisplayInfo.rotation;
    this.modeId = paramDisplayInfo.modeId;
    this.defaultModeId = paramDisplayInfo.defaultModeId;
    Object localObject = paramDisplayInfo.supportedModes;
    this.supportedModes = ((Display.Mode[])Arrays.copyOf((Object[])localObject, localObject.length));
    this.colorMode = paramDisplayInfo.colorMode;
    localObject = paramDisplayInfo.supportedColorModes;
    this.supportedColorModes = Arrays.copyOf((int[])localObject, localObject.length);
    this.hdrCapabilities = paramDisplayInfo.hdrCapabilities;
    this.logicalDensityDpi = paramDisplayInfo.logicalDensityDpi;
    this.physicalXDpi = paramDisplayInfo.physicalXDpi;
    this.physicalYDpi = paramDisplayInfo.physicalYDpi;
    this.appVsyncOffsetNanos = paramDisplayInfo.appVsyncOffsetNanos;
    this.presentationDeadlineNanos = paramDisplayInfo.presentationDeadlineNanos;
    this.state = paramDisplayInfo.state;
    this.ownerUid = paramDisplayInfo.ownerUid;
    this.ownerPackageName = paramDisplayInfo.ownerPackageName;
    this.removeMode = paramDisplayInfo.removeMode;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(DisplayInfo paramDisplayInfo)
  {
    boolean bool;
    if ((paramDisplayInfo != null) && (this.layerStack == paramDisplayInfo.layerStack) && (this.flags == paramDisplayInfo.flags) && (this.type == paramDisplayInfo.type) && (this.displayId == paramDisplayInfo.displayId) && (Objects.equals(this.address, paramDisplayInfo.address)) && (Objects.equals(this.uniqueId, paramDisplayInfo.uniqueId)) && (this.appWidth == paramDisplayInfo.appWidth) && (this.appHeight == paramDisplayInfo.appHeight) && (this.smallestNominalAppWidth == paramDisplayInfo.smallestNominalAppWidth) && (this.smallestNominalAppHeight == paramDisplayInfo.smallestNominalAppHeight) && (this.largestNominalAppWidth == paramDisplayInfo.largestNominalAppWidth) && (this.largestNominalAppHeight == paramDisplayInfo.largestNominalAppHeight) && (this.logicalWidth == paramDisplayInfo.logicalWidth) && (this.logicalHeight == paramDisplayInfo.logicalHeight) && (this.overscanLeft == paramDisplayInfo.overscanLeft) && (this.overscanTop == paramDisplayInfo.overscanTop) && (this.overscanRight == paramDisplayInfo.overscanRight) && (this.overscanBottom == paramDisplayInfo.overscanBottom) && (Objects.equals(this.displayCutout, paramDisplayInfo.displayCutout)) && (this.rotation == paramDisplayInfo.rotation) && (this.modeId == paramDisplayInfo.modeId) && (this.defaultModeId == paramDisplayInfo.defaultModeId) && (this.colorMode == paramDisplayInfo.colorMode) && (Arrays.equals(this.supportedColorModes, paramDisplayInfo.supportedColorModes)) && (Objects.equals(this.hdrCapabilities, paramDisplayInfo.hdrCapabilities)) && (this.logicalDensityDpi == paramDisplayInfo.logicalDensityDpi) && (this.physicalXDpi == paramDisplayInfo.physicalXDpi) && (this.physicalYDpi == paramDisplayInfo.physicalYDpi) && (this.appVsyncOffsetNanos == paramDisplayInfo.appVsyncOffsetNanos) && (this.presentationDeadlineNanos == paramDisplayInfo.presentationDeadlineNanos) && (this.state == paramDisplayInfo.state) && (this.ownerUid == paramDisplayInfo.ownerUid) && (Objects.equals(this.ownerPackageName, paramDisplayInfo.ownerPackageName)) && (this.removeMode == paramDisplayInfo.removeMode)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool;
    if (((paramObject instanceof DisplayInfo)) && (equals((DisplayInfo)paramObject))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public int findDefaultModeByRefreshRate(float paramFloat)
  {
    Display.Mode[] arrayOfMode = this.supportedModes;
    Display.Mode localMode = getDefaultMode();
    for (int i = 0; i < arrayOfMode.length; i++) {
      if (arrayOfMode[i].matches(localMode.getPhysicalWidth(), localMode.getPhysicalHeight(), paramFloat)) {
        return arrayOfMode[i].getModeId();
      }
    }
    return 0;
  }
  
  public void getAppMetrics(DisplayMetrics paramDisplayMetrics)
  {
    getAppMetrics(paramDisplayMetrics, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
  }
  
  public void getAppMetrics(DisplayMetrics paramDisplayMetrics, CompatibilityInfo paramCompatibilityInfo, Configuration paramConfiguration)
  {
    getMetricsWithSize(paramDisplayMetrics, paramCompatibilityInfo, paramConfiguration, this.appWidth, this.appHeight);
  }
  
  public void getAppMetrics(DisplayMetrics paramDisplayMetrics, DisplayAdjustments paramDisplayAdjustments)
  {
    getMetricsWithSize(paramDisplayMetrics, paramDisplayAdjustments.getCompatibilityInfo(), paramDisplayAdjustments.getConfiguration(), this.appWidth, this.appHeight);
  }
  
  public Display.Mode getDefaultMode()
  {
    return findMode(this.defaultModeId);
  }
  
  public float[] getDefaultRefreshRates()
  {
    Display.Mode[] arrayOfMode = this.supportedModes;
    Object localObject1 = new ArraySet();
    Object localObject2 = getDefaultMode();
    for (int i = 0; i < arrayOfMode.length; i++)
    {
      Display.Mode localMode = arrayOfMode[i];
      if ((localMode.getPhysicalWidth() == ((Display.Mode)localObject2).getPhysicalWidth()) && (localMode.getPhysicalHeight() == ((Display.Mode)localObject2).getPhysicalHeight())) {
        ((ArraySet)localObject1).add(Float.valueOf(localMode.getRefreshRate()));
      }
    }
    localObject2 = new float[((ArraySet)localObject1).size()];
    i = 0;
    localObject1 = ((ArraySet)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2[i] = ((Float)((Iterator)localObject1).next()).floatValue();
      i++;
    }
    return (float[])localObject2;
  }
  
  public void getLogicalMetrics(DisplayMetrics paramDisplayMetrics, CompatibilityInfo paramCompatibilityInfo, Configuration paramConfiguration)
  {
    getMetricsWithSize(paramDisplayMetrics, paramCompatibilityInfo, paramConfiguration, this.logicalWidth, this.logicalHeight);
  }
  
  public Display.Mode getMode()
  {
    return findMode(this.modeId);
  }
  
  public int getNaturalHeight()
  {
    int i = this.rotation;
    if ((i != 0) && (i != 2)) {
      i = this.logicalWidth;
    } else {
      i = this.logicalHeight;
    }
    return i;
  }
  
  public int getNaturalWidth()
  {
    int i = this.rotation;
    if ((i != 0) && (i != 2)) {
      i = this.logicalHeight;
    } else {
      i = this.logicalWidth;
    }
    return i;
  }
  
  public boolean hasAccess(int paramInt)
  {
    return Display.hasAccess(paramInt, this.flags, this.ownerUid, this.displayId);
  }
  
  public int hashCode()
  {
    return 0;
  }
  
  public boolean isHdr()
  {
    Object localObject = this.hdrCapabilities;
    if (localObject != null) {
      localObject = ((Display.HdrCapabilities)localObject).getSupportedHdrTypes();
    } else {
      localObject = null;
    }
    boolean bool;
    if ((localObject != null) && (localObject.length > 0)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isWideColorGamut()
  {
    int[] arrayOfInt = this.supportedColorModes;
    int i = arrayOfInt.length;
    int j = 0;
    while (j < i)
    {
      int k = arrayOfInt[j];
      if ((k != 6) && (k <= 7)) {
        j++;
      } else {
        return true;
      }
    }
    return false;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.layerStack = paramParcel.readInt();
    this.flags = paramParcel.readInt();
    this.type = paramParcel.readInt();
    this.displayId = paramParcel.readInt();
    this.address = ((DisplayAddress)paramParcel.readParcelable(null));
    this.name = paramParcel.readString();
    this.appWidth = paramParcel.readInt();
    this.appHeight = paramParcel.readInt();
    this.smallestNominalAppWidth = paramParcel.readInt();
    this.smallestNominalAppHeight = paramParcel.readInt();
    this.largestNominalAppWidth = paramParcel.readInt();
    this.largestNominalAppHeight = paramParcel.readInt();
    this.logicalWidth = paramParcel.readInt();
    this.logicalHeight = paramParcel.readInt();
    this.overscanLeft = paramParcel.readInt();
    this.overscanTop = paramParcel.readInt();
    this.overscanRight = paramParcel.readInt();
    this.overscanBottom = paramParcel.readInt();
    this.displayCutout = DisplayCutout.ParcelableWrapper.readCutoutFromParcel(paramParcel);
    this.rotation = paramParcel.readInt();
    this.modeId = paramParcel.readInt();
    this.defaultModeId = paramParcel.readInt();
    int i = paramParcel.readInt();
    this.supportedModes = new Display.Mode[i];
    for (int j = 0; j < i; j++) {
      this.supportedModes[j] = ((Display.Mode)Display.Mode.CREATOR.createFromParcel(paramParcel));
    }
    this.colorMode = paramParcel.readInt();
    i = paramParcel.readInt();
    this.supportedColorModes = new int[i];
    for (j = 0; j < i; j++) {
      this.supportedColorModes[j] = paramParcel.readInt();
    }
    this.hdrCapabilities = ((Display.HdrCapabilities)paramParcel.readParcelable(null));
    this.logicalDensityDpi = paramParcel.readInt();
    this.physicalXDpi = paramParcel.readFloat();
    this.physicalYDpi = paramParcel.readFloat();
    this.appVsyncOffsetNanos = paramParcel.readLong();
    this.presentationDeadlineNanos = paramParcel.readLong();
    this.state = paramParcel.readInt();
    this.ownerUid = paramParcel.readInt();
    this.ownerPackageName = paramParcel.readString();
    this.uniqueId = paramParcel.readString();
    this.removeMode = paramParcel.readInt();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DisplayInfo{\"");
    localStringBuilder.append(this.name);
    localStringBuilder.append(", displayId ");
    localStringBuilder.append(this.displayId);
    localStringBuilder.append("\", uniqueId \"");
    localStringBuilder.append(this.uniqueId);
    localStringBuilder.append("\", app ");
    localStringBuilder.append(this.appWidth);
    localStringBuilder.append(" x ");
    localStringBuilder.append(this.appHeight);
    localStringBuilder.append(", real ");
    localStringBuilder.append(this.logicalWidth);
    localStringBuilder.append(" x ");
    localStringBuilder.append(this.logicalHeight);
    if ((this.overscanLeft != 0) || (this.overscanTop != 0) || (this.overscanRight != 0) || (this.overscanBottom != 0))
    {
      localStringBuilder.append(", overscan (");
      localStringBuilder.append(this.overscanLeft);
      localStringBuilder.append(",");
      localStringBuilder.append(this.overscanTop);
      localStringBuilder.append(",");
      localStringBuilder.append(this.overscanRight);
      localStringBuilder.append(",");
      localStringBuilder.append(this.overscanBottom);
      localStringBuilder.append(")");
    }
    localStringBuilder.append(", largest app ");
    localStringBuilder.append(this.largestNominalAppWidth);
    localStringBuilder.append(" x ");
    localStringBuilder.append(this.largestNominalAppHeight);
    localStringBuilder.append(", smallest app ");
    localStringBuilder.append(this.smallestNominalAppWidth);
    localStringBuilder.append(" x ");
    localStringBuilder.append(this.smallestNominalAppHeight);
    localStringBuilder.append(", mode ");
    localStringBuilder.append(this.modeId);
    localStringBuilder.append(", defaultMode ");
    localStringBuilder.append(this.defaultModeId);
    localStringBuilder.append(", modes ");
    localStringBuilder.append(Arrays.toString(this.supportedModes));
    localStringBuilder.append(", colorMode ");
    localStringBuilder.append(this.colorMode);
    localStringBuilder.append(", supportedColorModes ");
    localStringBuilder.append(Arrays.toString(this.supportedColorModes));
    localStringBuilder.append(", hdrCapabilities ");
    localStringBuilder.append(this.hdrCapabilities);
    localStringBuilder.append(", rotation ");
    localStringBuilder.append(this.rotation);
    localStringBuilder.append(", density ");
    localStringBuilder.append(this.logicalDensityDpi);
    localStringBuilder.append(" (");
    localStringBuilder.append(this.physicalXDpi);
    localStringBuilder.append(" x ");
    localStringBuilder.append(this.physicalYDpi);
    localStringBuilder.append(") dpi, layerStack ");
    localStringBuilder.append(this.layerStack);
    localStringBuilder.append(", appVsyncOff ");
    localStringBuilder.append(this.appVsyncOffsetNanos);
    localStringBuilder.append(", presDeadline ");
    localStringBuilder.append(this.presentationDeadlineNanos);
    localStringBuilder.append(", type ");
    localStringBuilder.append(Display.typeToString(this.type));
    if (this.address != null)
    {
      localStringBuilder.append(", address ");
      localStringBuilder.append(this.address);
    }
    localStringBuilder.append(", state ");
    localStringBuilder.append(Display.stateToString(this.state));
    if ((this.ownerUid != 0) || (this.ownerPackageName != null))
    {
      localStringBuilder.append(", owner ");
      localStringBuilder.append(this.ownerPackageName);
      localStringBuilder.append(" (uid ");
      localStringBuilder.append(this.ownerUid);
      localStringBuilder.append(")");
    }
    localStringBuilder.append(flagsToString(this.flags));
    localStringBuilder.append(", removeMode ");
    localStringBuilder.append(this.removeMode);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.layerStack);
    paramParcel.writeInt(this.flags);
    paramParcel.writeInt(this.type);
    paramParcel.writeInt(this.displayId);
    paramParcel.writeParcelable(this.address, paramInt);
    paramParcel.writeString(this.name);
    paramParcel.writeInt(this.appWidth);
    paramParcel.writeInt(this.appHeight);
    paramParcel.writeInt(this.smallestNominalAppWidth);
    paramParcel.writeInt(this.smallestNominalAppHeight);
    paramParcel.writeInt(this.largestNominalAppWidth);
    paramParcel.writeInt(this.largestNominalAppHeight);
    paramParcel.writeInt(this.logicalWidth);
    paramParcel.writeInt(this.logicalHeight);
    paramParcel.writeInt(this.overscanLeft);
    paramParcel.writeInt(this.overscanTop);
    paramParcel.writeInt(this.overscanRight);
    paramParcel.writeInt(this.overscanBottom);
    DisplayCutout.ParcelableWrapper.writeCutoutToParcel(this.displayCutout, paramParcel, paramInt);
    paramParcel.writeInt(this.rotation);
    paramParcel.writeInt(this.modeId);
    paramParcel.writeInt(this.defaultModeId);
    paramParcel.writeInt(this.supportedModes.length);
    Object localObject;
    for (int i = 0;; i++)
    {
      localObject = this.supportedModes;
      if (i >= localObject.length) {
        break;
      }
      localObject[i].writeToParcel(paramParcel, paramInt);
    }
    paramParcel.writeInt(this.colorMode);
    paramParcel.writeInt(this.supportedColorModes.length);
    for (i = 0;; i++)
    {
      localObject = this.supportedColorModes;
      if (i >= localObject.length) {
        break;
      }
      paramParcel.writeInt(localObject[i]);
    }
    paramParcel.writeParcelable(this.hdrCapabilities, paramInt);
    paramParcel.writeInt(this.logicalDensityDpi);
    paramParcel.writeFloat(this.physicalXDpi);
    paramParcel.writeFloat(this.physicalYDpi);
    paramParcel.writeLong(this.appVsyncOffsetNanos);
    paramParcel.writeLong(this.presentationDeadlineNanos);
    paramParcel.writeInt(this.state);
    paramParcel.writeInt(this.ownerUid);
    paramParcel.writeString(this.ownerPackageName);
    paramParcel.writeString(this.uniqueId);
    paramParcel.writeInt(this.removeMode);
  }
  
  public void writeToProto(ProtoOutputStream paramProtoOutputStream, long paramLong)
  {
    paramLong = paramProtoOutputStream.start(paramLong);
    paramProtoOutputStream.write(1120986464257L, this.logicalWidth);
    paramProtoOutputStream.write(1120986464258L, this.logicalHeight);
    paramProtoOutputStream.write(1120986464259L, this.appWidth);
    paramProtoOutputStream.write(1120986464260L, this.appHeight);
    paramProtoOutputStream.write(1138166333445L, this.name);
    paramProtoOutputStream.end(paramLong);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/DisplayInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */