package android.view.textclassifier;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Base64;
import android.util.KeyValueListParser;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Supplier;

public final class ActionsModelParamsSupplier
  implements Supplier<ActionsModelParams>
{
  @VisibleForTesting
  static final String KEY_REQUIRED_LOCALES = "required_locales";
  @VisibleForTesting
  static final String KEY_REQUIRED_MODEL_VERSION = "required_model_version";
  @VisibleForTesting
  static final String KEY_SERIALIZED_PRECONDITIONS = "serialized_preconditions";
  private static final String TAG = "androidtc";
  @GuardedBy({"mLock"})
  private ActionsModelParams mActionsModelParams;
  private final Context mAppContext;
  private final Object mLock = new Object();
  private final Runnable mOnChangedListener;
  @GuardedBy({"mLock"})
  private boolean mParsed = true;
  private final SettingsObserver mSettingsObserver;
  
  public ActionsModelParamsSupplier(Context paramContext, Runnable paramRunnable)
  {
    Context localContext = ((Context)Preconditions.checkNotNull(paramContext)).getApplicationContext();
    if (localContext != null) {
      paramContext = localContext;
    }
    this.mAppContext = paramContext;
    if (paramRunnable == null) {
      paramContext = _..Lambda.ActionsModelParamsSupplier.GCXILXtg_S2la6x__ANOhbYxetw.INSTANCE;
    } else {
      paramContext = paramRunnable;
    }
    this.mOnChangedListener = paramContext;
    this.mSettingsObserver = new SettingsObserver(this.mAppContext, new _..Lambda.ActionsModelParamsSupplier.zElxNeuL3A8paTXvw8GWdpp4rFo(this));
  }
  
  private ActionsModelParams parse(ContentResolver paramContentResolver)
  {
    paramContentResolver = Settings.Global.getString(paramContentResolver, "text_classifier_action_model_params");
    if (TextUtils.isEmpty(paramContentResolver)) {
      return ActionsModelParams.INVALID;
    }
    try
    {
      Object localObject = new android/util/KeyValueListParser;
      ((KeyValueListParser)localObject).<init>(',');
      ((KeyValueListParser)localObject).setString(paramContentResolver);
      int i = ((KeyValueListParser)localObject).getInt("required_model_version", -1);
      if (i == -1)
      {
        Log.w("androidtc", "ActionsModelParams.Parse, invalid model version");
        return ActionsModelParams.INVALID;
      }
      paramContentResolver = ((KeyValueListParser)localObject).getString("required_locales", null);
      if (paramContentResolver == null)
      {
        Log.w("androidtc", "ActionsModelParams.Parse, invalid locales");
        return ActionsModelParams.INVALID;
      }
      localObject = ((KeyValueListParser)localObject).getString("serialized_preconditions", null);
      if (localObject == null)
      {
        Log.w("androidtc", "ActionsModelParams.Parse, invalid preconditions");
        return ActionsModelParams.INVALID;
      }
      paramContentResolver = new ActionsModelParams(i, paramContentResolver, Base64.decode((String)localObject, 2));
      return paramContentResolver;
    }
    finally
    {
      Log.e("androidtc", "Invalid TEXT_CLASSIFIER_ACTION_MODEL_PARAMS, ignore", paramContentResolver);
    }
    return ActionsModelParams.INVALID;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mAppContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public ActionsModelParams get()
  {
    synchronized (this.mLock)
    {
      if (this.mParsed)
      {
        this.mActionsModelParams = parse(this.mAppContext.getContentResolver());
        this.mParsed = false;
      }
      return this.mActionsModelParams;
    }
  }
  
  public static final class ActionsModelParams
  {
    public static final ActionsModelParams INVALID = new ActionsModelParams(-1, "", new byte[0]);
    private final String mRequiredModelLocales;
    private final int mRequiredModelVersion;
    private final byte[] mSerializedPreconditions;
    
    public ActionsModelParams(int paramInt, String paramString, byte[] paramArrayOfByte)
    {
      this.mRequiredModelVersion = paramInt;
      this.mRequiredModelLocales = ((String)Preconditions.checkNotNull(paramString));
      this.mSerializedPreconditions = ((byte[])Preconditions.checkNotNull(paramArrayOfByte));
    }
    
    public byte[] getSerializedPreconditions(ModelFileManager.ModelFile paramModelFile)
    {
      if (this == INVALID) {
        return null;
      }
      int i = paramModelFile.getVersion();
      int j = this.mRequiredModelVersion;
      if (i != j)
      {
        Log.w("androidtc", String.format("Not applying mSerializedPreconditions, required version=%d, actual=%d", new Object[] { Integer.valueOf(j), Integer.valueOf(paramModelFile.getVersion()) }));
        return null;
      }
      if (!Objects.equals(paramModelFile.getSupportedLocalesStr(), this.mRequiredModelLocales))
      {
        Log.w("androidtc", String.format("Not applying mSerializedPreconditions, required locales=%s, actual=%s", new Object[] { this.mRequiredModelLocales, paramModelFile.getSupportedLocalesStr() }));
        return null;
      }
      return this.mSerializedPreconditions;
    }
  }
  
  private static final class SettingsObserver
    extends ContentObserver
  {
    private final WeakReference<Runnable> mOnChangedListener;
    
    SettingsObserver(Context paramContext, Runnable paramRunnable)
    {
      super();
      this.mOnChangedListener = new WeakReference(paramRunnable);
      paramContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("text_classifier_action_model_params"), false, this);
    }
    
    public void onChange(boolean paramBoolean)
    {
      if (this.mOnChangedListener.get() != null) {
        ((Runnable)this.mOnChangedListener.get()).run();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/ActionsModelParamsSupplier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */