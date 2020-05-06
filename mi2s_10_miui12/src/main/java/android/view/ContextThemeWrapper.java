package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

public class ContextThemeWrapper
  extends ContextWrapper
{
  @UnsupportedAppUsage
  private LayoutInflater mInflater;
  private Configuration mOverrideConfiguration;
  @UnsupportedAppUsage
  private Resources mResources;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768723L)
  private Resources.Theme mTheme;
  @UnsupportedAppUsage
  private int mThemeResource;
  
  public ContextThemeWrapper()
  {
    super(null);
  }
  
  public ContextThemeWrapper(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.mThemeResource = paramInt;
  }
  
  public ContextThemeWrapper(Context paramContext, Resources.Theme paramTheme)
  {
    super(paramContext);
    this.mTheme = paramTheme;
  }
  
  private Resources getResourcesInternal()
  {
    if (this.mResources == null)
    {
      Configuration localConfiguration = this.mOverrideConfiguration;
      if (localConfiguration == null) {
        this.mResources = super.getResources();
      } else {
        this.mResources = createConfigurationContext(localConfiguration).getResources();
      }
    }
    return this.mResources;
  }
  
  @UnsupportedAppUsage
  private void initializeTheme()
  {
    boolean bool;
    if (this.mTheme == null) {
      bool = true;
    } else {
      bool = false;
    }
    if (bool)
    {
      this.mTheme = getResources().newTheme();
      Resources.Theme localTheme = getBaseContext().getTheme();
      if (localTheme != null) {
        this.mTheme.setTo(localTheme);
      }
    }
    onApplyThemeResource(this.mTheme, this.mThemeResource, bool);
  }
  
  public void applyOverrideConfiguration(Configuration paramConfiguration)
  {
    if (this.mResources == null)
    {
      if (this.mOverrideConfiguration == null)
      {
        this.mOverrideConfiguration = new Configuration(paramConfiguration);
        return;
      }
      throw new IllegalStateException("Override configuration has already been set");
    }
    throw new IllegalStateException("getResources() or getAssets() has already been called");
  }
  
  protected void attachBaseContext(Context paramContext)
  {
    super.attachBaseContext(paramContext);
  }
  
  public AssetManager getAssets()
  {
    return getResourcesInternal().getAssets();
  }
  
  public Configuration getOverrideConfiguration()
  {
    return this.mOverrideConfiguration;
  }
  
  public Resources getResources()
  {
    return getResourcesInternal();
  }
  
  public Object getSystemService(String paramString)
  {
    if ("layout_inflater".equals(paramString))
    {
      if (this.mInflater == null) {
        this.mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
      }
      return this.mInflater;
    }
    return getBaseContext().getSystemService(paramString);
  }
  
  public Resources.Theme getTheme()
  {
    Resources.Theme localTheme = this.mTheme;
    if (localTheme != null) {
      return localTheme;
    }
    this.mThemeResource = Resources.selectDefaultTheme(this.mThemeResource, getApplicationInfo().targetSdkVersion);
    initializeTheme();
    return this.mTheme;
  }
  
  @UnsupportedAppUsage
  public int getThemeResId()
  {
    return this.mThemeResource;
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    paramTheme.applyStyle(paramInt, true);
  }
  
  public void setTheme(int paramInt)
  {
    if (this.mThemeResource != paramInt)
    {
      this.mThemeResource = paramInt;
      initializeTheme();
    }
  }
  
  public void setTheme(Resources.Theme paramTheme)
  {
    this.mTheme = paramTheme;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ContextThemeWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */