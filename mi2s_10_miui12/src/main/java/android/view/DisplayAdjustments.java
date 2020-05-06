package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import java.util.Objects;

public class DisplayAdjustments
{
  public static final DisplayAdjustments DEFAULT_DISPLAY_ADJUSTMENTS = new DisplayAdjustments();
  private volatile CompatibilityInfo mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
  private Configuration mConfiguration;
  
  @UnsupportedAppUsage
  public DisplayAdjustments() {}
  
  public DisplayAdjustments(Configuration paramConfiguration)
  {
    if (paramConfiguration == null) {
      paramConfiguration = Configuration.EMPTY;
    }
    this.mConfiguration = new Configuration(paramConfiguration);
  }
  
  public DisplayAdjustments(DisplayAdjustments paramDisplayAdjustments)
  {
    setCompatibilityInfo(paramDisplayAdjustments.mCompatInfo);
    paramDisplayAdjustments = paramDisplayAdjustments.mConfiguration;
    if (paramDisplayAdjustments == null) {
      paramDisplayAdjustments = Configuration.EMPTY;
    }
    this.mConfiguration = new Configuration(paramDisplayAdjustments);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof DisplayAdjustments;
    boolean bool2 = false;
    if (!bool1) {
      return false;
    }
    paramObject = (DisplayAdjustments)paramObject;
    if ((Objects.equals(((DisplayAdjustments)paramObject).mCompatInfo, this.mCompatInfo)) && (Objects.equals(((DisplayAdjustments)paramObject).mConfiguration, this.mConfiguration))) {
      bool2 = true;
    }
    return bool2;
  }
  
  public CompatibilityInfo getCompatibilityInfo()
  {
    return this.mCompatInfo;
  }
  
  @UnsupportedAppUsage
  public Configuration getConfiguration()
  {
    return this.mConfiguration;
  }
  
  public int hashCode()
  {
    return (17 * 31 + Objects.hashCode(this.mCompatInfo)) * 31 + Objects.hashCode(this.mConfiguration);
  }
  
  @UnsupportedAppUsage
  public void setCompatibilityInfo(CompatibilityInfo paramCompatibilityInfo)
  {
    if (this != DEFAULT_DISPLAY_ADJUSTMENTS)
    {
      if ((paramCompatibilityInfo != null) && ((paramCompatibilityInfo.isScalingRequired()) || (!paramCompatibilityInfo.supportsScreen()))) {
        this.mCompatInfo = paramCompatibilityInfo;
      } else {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
      }
      return;
    }
    throw new IllegalArgumentException("setCompatbilityInfo: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
  }
  
  public void setConfiguration(Configuration paramConfiguration)
  {
    if (this != DEFAULT_DISPLAY_ADJUSTMENTS)
    {
      Configuration localConfiguration = this.mConfiguration;
      if (paramConfiguration == null) {
        paramConfiguration = Configuration.EMPTY;
      }
      localConfiguration.setTo(paramConfiguration);
      return;
    }
    throw new IllegalArgumentException("setConfiguration: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/DisplayAdjustments.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */