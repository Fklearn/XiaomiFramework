package android.view.textclassifier;

import android.annotation.UnsupportedAppUsage;
import android.app.ActivityThread;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.ServiceManager.ServiceNotFoundException;
import android.provider.DeviceConfig;
import android.provider.DeviceConfig.OnPropertiesChangedListener;
import android.provider.DeviceConfig.Properties;
import android.provider.Settings.Global;
import android.service.textclassifier.TextClassifierService;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.lang.ref.WeakReference;

public final class TextClassificationManager
{
  private static final String LOG_TAG = "TextClassificationManager";
  private static final TextClassificationConstants sDefaultSettings = new TextClassificationConstants(_..Lambda.TextClassificationManager.VwZ4EV_1i6FbjO7TtyaAnFL3oe0.INSTANCE);
  private final Context mContext;
  @GuardedBy({"mLock"})
  private TextClassifier mCustomTextClassifier;
  private final TextClassificationSessionFactory mDefaultSessionFactory = new _..Lambda.TextClassificationManager.SIydN2POphTO3AmPTLEMmXPLSKY(this);
  @GuardedBy({"mLock"})
  private TextClassifier mLocalTextClassifier;
  private final Object mLock = new Object();
  @GuardedBy({"mLock"})
  private TextClassificationSessionFactory mSessionFactory;
  @GuardedBy({"mLock"})
  private TextClassificationConstants mSettings;
  private final SettingsObserver mSettingsObserver;
  @GuardedBy({"mLock"})
  private TextClassifier mSystemTextClassifier;
  
  public TextClassificationManager(Context paramContext)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext));
    this.mSessionFactory = this.mDefaultSessionFactory;
    this.mSettingsObserver = new SettingsObserver(this);
  }
  
  private TextClassifier getLocalTextClassifier()
  {
    synchronized (this.mLock)
    {
      if (this.mLocalTextClassifier == null) {
        if (getSettings().isLocalTextClassifierEnabled())
        {
          localObject2 = new android/view/textclassifier/TextClassifierImpl;
          ((TextClassifierImpl)localObject2).<init>(this.mContext, getSettings(), TextClassifier.NO_OP);
          this.mLocalTextClassifier = ((TextClassifier)localObject2);
        }
        else
        {
          Log.d("TextClassificationManager", "Local TextClassifier disabled");
          this.mLocalTextClassifier = TextClassifier.NO_OP;
        }
      }
      Object localObject2 = this.mLocalTextClassifier;
      return (TextClassifier)localObject2;
    }
  }
  
  private TextClassificationConstants getSettings()
  {
    synchronized (this.mLock)
    {
      if (this.mSettings == null)
      {
        localTextClassificationConstants = new android/view/textclassifier/TextClassificationConstants;
        _..Lambda.TextClassificationManager.oweIEhDWxy3_0kZSXp3oRbSuNW4 localoweIEhDWxy3_0kZSXp3oRbSuNW4 = new android/view/textclassifier/_$$Lambda$TextClassificationManager$oweIEhDWxy3_0kZSXp3oRbSuNW4;
        localoweIEhDWxy3_0kZSXp3oRbSuNW4.<init>(this);
        localTextClassificationConstants.<init>(localoweIEhDWxy3_0kZSXp3oRbSuNW4);
        this.mSettings = localTextClassificationConstants;
      }
      TextClassificationConstants localTextClassificationConstants = this.mSettings;
      return localTextClassificationConstants;
    }
  }
  
  public static TextClassificationConstants getSettings(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext);
    paramContext = (TextClassificationManager)paramContext.getSystemService(TextClassificationManager.class);
    if (paramContext != null) {
      return paramContext.getSettings();
    }
    return sDefaultSettings;
  }
  
  private TextClassifier getSystemTextClassifier()
  {
    synchronized (this.mLock)
    {
      if (this.mSystemTextClassifier == null)
      {
        boolean bool = isSystemTextClassifierEnabled();
        if (bool) {
          try
          {
            SystemTextClassifier localSystemTextClassifier = new android/view/textclassifier/SystemTextClassifier;
            localSystemTextClassifier.<init>(this.mContext, getSettings());
            this.mSystemTextClassifier = localSystemTextClassifier;
            Log.d("TextClassificationManager", "Initialized SystemTextClassifier");
          }
          catch (ServiceManager.ServiceNotFoundException localServiceNotFoundException)
          {
            Log.e("TextClassificationManager", "Could not initialize SystemTextClassifier", localServiceNotFoundException);
          }
        }
      }
      ??? = this.mSystemTextClassifier;
      if (??? != null) {
        return (TextClassifier)???;
      }
      return TextClassifier.NO_OP;
    }
  }
  
  private void invalidate()
  {
    synchronized (this.mLock)
    {
      this.mSettings = null;
      this.mLocalTextClassifier = null;
      this.mSystemTextClassifier = null;
      return;
    }
  }
  
  private boolean isSystemTextClassifierEnabled()
  {
    boolean bool;
    if ((getSettings().isSystemTextClassifierEnabled()) && (TextClassifierService.getServiceComponentName(this.mContext) != null)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public TextClassifier createTextClassificationSession(TextClassificationContext paramTextClassificationContext)
  {
    Preconditions.checkNotNull(paramTextClassificationContext);
    paramTextClassificationContext = this.mSessionFactory.createTextClassificationSession(paramTextClassificationContext);
    Preconditions.checkNotNull(paramTextClassificationContext, "Session Factory should never return null");
    return paramTextClassificationContext;
  }
  
  public TextClassifier createTextClassificationSession(TextClassificationContext paramTextClassificationContext, TextClassifier paramTextClassifier)
  {
    Preconditions.checkNotNull(paramTextClassificationContext);
    Preconditions.checkNotNull(paramTextClassifier);
    return new TextClassificationSession(paramTextClassificationContext, paramTextClassifier);
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    getLocalTextClassifier().dump(paramIndentingPrintWriter);
    getSystemTextClassifier().dump(paramIndentingPrintWriter);
    getSettings().dump(paramIndentingPrintWriter);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mSettingsObserver != null)
      {
        getApplicationContext().getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        DeviceConfig.removeOnPropertiesChangedListener(this.mSettingsObserver);
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  Context getApplicationContext()
  {
    Context localContext;
    if (this.mContext.getApplicationContext() != null) {
      localContext = this.mContext.getApplicationContext();
    } else {
      localContext = this.mContext;
    }
    return localContext;
  }
  
  public TextClassifier getTextClassifier()
  {
    synchronized (this.mLock)
    {
      if (this.mCustomTextClassifier != null)
      {
        localTextClassifier = this.mCustomTextClassifier;
        return localTextClassifier;
      }
      if (isSystemTextClassifierEnabled())
      {
        localTextClassifier = getSystemTextClassifier();
        return localTextClassifier;
      }
      TextClassifier localTextClassifier = getLocalTextClassifier();
      return localTextClassifier;
    }
  }
  
  @UnsupportedAppUsage
  public TextClassifier getTextClassifier(int paramInt)
  {
    if (paramInt != 0) {
      return getSystemTextClassifier();
    }
    return getLocalTextClassifier();
  }
  
  @VisibleForTesting
  public void invalidateForTesting()
  {
    invalidate();
  }
  
  public void setTextClassificationSessionFactory(TextClassificationSessionFactory paramTextClassificationSessionFactory)
  {
    Object localObject = this.mLock;
    if (paramTextClassificationSessionFactory != null) {}
    try
    {
      this.mSessionFactory = paramTextClassificationSessionFactory;
      break label27;
      this.mSessionFactory = this.mDefaultSessionFactory;
      label27:
      return;
    }
    finally {}
  }
  
  public void setTextClassifier(TextClassifier paramTextClassifier)
  {
    synchronized (this.mLock)
    {
      this.mCustomTextClassifier = paramTextClassifier;
      return;
    }
  }
  
  private static final class SettingsObserver
    extends ContentObserver
    implements DeviceConfig.OnPropertiesChangedListener
  {
    private final WeakReference<TextClassificationManager> mTcm;
    
    SettingsObserver(TextClassificationManager paramTextClassificationManager)
    {
      super();
      this.mTcm = new WeakReference(paramTextClassificationManager);
      paramTextClassificationManager.getApplicationContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("text_classifier_constants"), false, this);
      DeviceConfig.addOnPropertiesChangedListener("textclassifier", ActivityThread.currentApplication().getMainExecutor(), this);
    }
    
    private void invalidateSettings()
    {
      TextClassificationManager localTextClassificationManager = (TextClassificationManager)this.mTcm.get();
      if (localTextClassificationManager != null) {
        localTextClassificationManager.invalidate();
      }
    }
    
    public void onChange(boolean paramBoolean)
    {
      invalidateSettings();
    }
    
    public void onPropertiesChanged(DeviceConfig.Properties paramProperties)
    {
      invalidateSettings();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassificationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */