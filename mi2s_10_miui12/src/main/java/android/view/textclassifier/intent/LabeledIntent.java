package android.view.textclassifier.intent;

import android.app.RemoteAction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.textclassifier.ExtrasUtils;
import android.view.textclassifier.Log;
import android.view.textclassifier.TextClassification;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class LabeledIntent
{
  public static final int DEFAULT_REQUEST_CODE = 0;
  private static final TitleChooser DEFAULT_TITLE_CHOOSER = _..Lambda.LabeledIntent.LaL7EfxShgNu4lrdo3mv85g49Jg.INSTANCE;
  private static final String TAG = "LabeledIntent";
  public final String description;
  public final String descriptionWithAppName;
  public final Intent intent;
  public final int requestCode;
  public final String titleWithEntity;
  public final String titleWithoutEntity;
  
  public LabeledIntent(String paramString1, String paramString2, String paramString3, String paramString4, Intent paramIntent, int paramInt)
  {
    if ((TextUtils.isEmpty(paramString2)) && (TextUtils.isEmpty(paramString1))) {
      throw new IllegalArgumentException("titleWithEntity and titleWithoutEntity should not be both null");
    }
    this.titleWithoutEntity = paramString1;
    this.titleWithEntity = paramString2;
    this.description = ((String)Preconditions.checkNotNull(paramString3));
    this.descriptionWithAppName = paramString4;
    this.intent = ((Intent)Preconditions.checkNotNull(paramIntent));
    this.requestCode = paramInt;
  }
  
  private String getApplicationName(ResolveInfo paramResolveInfo, PackageManager paramPackageManager)
  {
    if (paramResolveInfo.activityInfo == null) {
      return null;
    }
    if ("android".equals(paramResolveInfo.activityInfo.packageName)) {
      return null;
    }
    if (paramResolveInfo.activityInfo.applicationInfo == null) {
      return null;
    }
    return (String)paramPackageManager.getApplicationLabel(paramResolveInfo.activityInfo.applicationInfo);
  }
  
  private Bundle getFromTextClassifierExtra(Bundle paramBundle)
  {
    if (paramBundle != null)
    {
      Bundle localBundle = new Bundle();
      ExtrasUtils.putTextLanguagesExtra(localBundle, paramBundle);
      return localBundle;
    }
    return Bundle.EMPTY;
  }
  
  private String resolveDescription(ResolveInfo paramResolveInfo, PackageManager paramPackageManager)
  {
    if (!TextUtils.isEmpty(this.descriptionWithAppName))
    {
      paramResolveInfo = getApplicationName(paramResolveInfo, paramPackageManager);
      if (!TextUtils.isEmpty(paramResolveInfo)) {
        return String.format(this.descriptionWithAppName, new Object[] { paramResolveInfo });
      }
    }
    return this.description;
  }
  
  public Result resolve(Context paramContext, TitleChooser paramTitleChooser, Bundle paramBundle)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    ResolveInfo localResolveInfo = localPackageManager.resolveActivity(this.intent, 0);
    if ((localResolveInfo != null) && (localResolveInfo.activityInfo != null))
    {
      String str1 = localResolveInfo.activityInfo.packageName;
      String str2 = localResolveInfo.activityInfo.name;
      if ((str1 != null) && (str2 != null))
      {
        Intent localIntent = new Intent(this.intent);
        localIntent.putExtra("android.view.textclassifier.extra.FROM_TEXT_CLASSIFIER", getFromTextClassifierExtra(paramBundle));
        boolean bool1 = false;
        Object localObject = null;
        boolean bool2 = bool1;
        paramBundle = (Bundle)localObject;
        if (!"android".equals(str1))
        {
          localIntent.setComponent(new ComponentName(str1, str2));
          bool2 = bool1;
          paramBundle = (Bundle)localObject;
          if (localResolveInfo.activityInfo.getIconResource() != 0)
          {
            paramBundle = Icon.createWithResource(str1, localResolveInfo.activityInfo.getIconResource());
            bool2 = true;
          }
        }
        localObject = paramBundle;
        if (paramBundle == null) {
          localObject = Icon.createWithResource("android", 17302744);
        }
        paramBundle = TextClassification.createPendingIntent(paramContext, localIntent, this.requestCode);
        if (paramTitleChooser == null) {
          paramContext = DEFAULT_TITLE_CHOOSER;
        } else {
          paramContext = paramTitleChooser;
        }
        paramTitleChooser = paramContext.chooseTitle(this, localResolveInfo);
        paramContext = paramTitleChooser;
        if (TextUtils.isEmpty(paramTitleChooser))
        {
          Log.w("LabeledIntent", "Custom titleChooser return null, fallback to the default titleChooser");
          paramContext = DEFAULT_TITLE_CHOOSER.chooseTitle(this, localResolveInfo);
        }
        paramContext = new RemoteAction((Icon)localObject, paramContext, resolveDescription(localResolveInfo, localPackageManager), paramBundle);
        paramContext.setShouldShowIcon(bool2);
        return new Result(localIntent, paramContext);
      }
      Log.w("LabeledIntent", "packageName or className is null");
      return null;
    }
    Log.w("LabeledIntent", "resolveInfo or activityInfo is null");
    return null;
  }
  
  public static final class Result
  {
    public final RemoteAction remoteAction;
    public final Intent resolvedIntent;
    
    public Result(Intent paramIntent, RemoteAction paramRemoteAction)
    {
      this.resolvedIntent = ((Intent)Preconditions.checkNotNull(paramIntent));
      this.remoteAction = ((RemoteAction)Preconditions.checkNotNull(paramRemoteAction));
    }
  }
  
  public static abstract interface TitleChooser
  {
    public abstract CharSequence chooseTitle(LabeledIntent paramLabeledIntent, ResolveInfo paramResolveInfo);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/intent/LabeledIntent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */