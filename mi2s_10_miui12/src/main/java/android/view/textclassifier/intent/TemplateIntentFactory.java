package android.view.textclassifier.intent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.textclassifier.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.google.android.textclassifier.NamedVariant;
import com.google.android.textclassifier.RemoteActionTemplate;
import java.util.ArrayList;
import java.util.List;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class TemplateIntentFactory
{
  private static final String TAG = "androidtc";
  
  private static Intent createIntent(RemoteActionTemplate paramRemoteActionTemplate)
  {
    Intent localIntent = new Intent(paramRemoteActionTemplate.action);
    boolean bool = TextUtils.isEmpty(paramRemoteActionTemplate.data);
    String str = null;
    if (bool) {
      localObject = null;
    } else {
      localObject = Uri.parse(paramRemoteActionTemplate.data).normalizeScheme();
    }
    if (!TextUtils.isEmpty(paramRemoteActionTemplate.type)) {
      str = Intent.normalizeMimeType(paramRemoteActionTemplate.type);
    }
    localIntent.setDataAndType((Uri)localObject, str);
    Object localObject = paramRemoteActionTemplate.flags;
    int i = 0;
    int j;
    if (localObject == null) {
      j = 0;
    } else {
      j = paramRemoteActionTemplate.flags.intValue();
    }
    localIntent.setFlags(j);
    if (paramRemoteActionTemplate.category != null)
    {
      localObject = paramRemoteActionTemplate.category;
      int k = localObject.length;
      for (j = i; j < k; j++)
      {
        str = localObject[j];
        if (str != null) {
          localIntent.addCategory(str);
        }
      }
    }
    localIntent.putExtras(nameVariantsToBundle(paramRemoteActionTemplate.extras));
    return localIntent;
  }
  
  private static boolean isValidTemplate(RemoteActionTemplate paramRemoteActionTemplate)
  {
    if (paramRemoteActionTemplate == null)
    {
      Log.w("androidtc", "Invalid RemoteActionTemplate: is null");
      return false;
    }
    if ((TextUtils.isEmpty(paramRemoteActionTemplate.titleWithEntity)) && (TextUtils.isEmpty(paramRemoteActionTemplate.titleWithoutEntity)))
    {
      Log.w("androidtc", "Invalid RemoteActionTemplate: title is null");
      return false;
    }
    if (TextUtils.isEmpty(paramRemoteActionTemplate.description))
    {
      Log.w("androidtc", "Invalid RemoteActionTemplate: description is null");
      return false;
    }
    if (!TextUtils.isEmpty(paramRemoteActionTemplate.packageName))
    {
      Log.w("androidtc", "Invalid RemoteActionTemplate: package name is set");
      return false;
    }
    if (TextUtils.isEmpty(paramRemoteActionTemplate.action))
    {
      Log.w("androidtc", "Invalid RemoteActionTemplate: intent action not set");
      return false;
    }
    return true;
  }
  
  public static Bundle nameVariantsToBundle(NamedVariant[] paramArrayOfNamedVariant)
  {
    if (paramArrayOfNamedVariant == null) {
      return Bundle.EMPTY;
    }
    Bundle localBundle = new Bundle();
    int i = paramArrayOfNamedVariant.length;
    for (int j = 0; j < i; j++)
    {
      NamedVariant localNamedVariant = paramArrayOfNamedVariant[j];
      if (localNamedVariant != null) {
        switch (localNamedVariant.getType())
        {
        default: 
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Unsupported type found in nameVariantsToBundle : ");
          localStringBuilder.append(localNamedVariant.getType());
          Log.w("androidtc", localStringBuilder.toString());
          break;
        case 6: 
          localBundle.putString(localNamedVariant.getName(), localNamedVariant.getString());
          break;
        case 5: 
          localBundle.putBoolean(localNamedVariant.getName(), localNamedVariant.getBool());
          break;
        case 4: 
          localBundle.putDouble(localNamedVariant.getName(), localNamedVariant.getDouble());
          break;
        case 3: 
          localBundle.putFloat(localNamedVariant.getName(), localNamedVariant.getFloat());
          break;
        case 2: 
          localBundle.putLong(localNamedVariant.getName(), localNamedVariant.getLong());
          break;
        case 1: 
          localBundle.putInt(localNamedVariant.getName(), localNamedVariant.getInt());
        }
      }
    }
    return localBundle;
  }
  
  public List<LabeledIntent> create(RemoteActionTemplate[] paramArrayOfRemoteActionTemplate)
  {
    if (paramArrayOfRemoteActionTemplate.length == 0) {
      return new ArrayList();
    }
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfRemoteActionTemplate.length;
    for (int j = 0; j < i; j++)
    {
      RemoteActionTemplate localRemoteActionTemplate = paramArrayOfRemoteActionTemplate[j];
      if (!isValidTemplate(localRemoteActionTemplate))
      {
        Log.w("androidtc", "Invalid RemoteActionTemplate skipped.");
      }
      else
      {
        String str1 = localRemoteActionTemplate.titleWithoutEntity;
        String str2 = localRemoteActionTemplate.titleWithEntity;
        String str3 = localRemoteActionTemplate.description;
        String str4 = localRemoteActionTemplate.descriptionWithAppName;
        Intent localIntent = createIntent(localRemoteActionTemplate);
        int k;
        if (localRemoteActionTemplate.requestCode == null) {
          k = 0;
        } else {
          k = localRemoteActionTemplate.requestCode.intValue();
        }
        localArrayList.add(new LabeledIntent(str1, str2, str3, str4, localIntent, k));
      }
    }
    return localArrayList;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/intent/TemplateIntentFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */