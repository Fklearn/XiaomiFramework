package android.view.textclassifier;

import android.app.RemoteAction;
import android.content.Intent;
import android.icu.util.ULocale;
import android.os.Bundle;
import com.android.internal.util.ArrayUtils;
import com.google.android.textclassifier.AnnotatorModel.ClassificationResult;
import java.util.ArrayList;
import java.util.List;

public final class ExtrasUtils
{
  private static final String ACTIONS_INTENTS = "actions-intents";
  private static final String ACTION_INTENT = "action-intent";
  private static final String ENTITIES = "entities";
  private static final String ENTITIES_EXTRAS = "entities-extras";
  private static final String ENTITY_TYPE = "entity-type";
  private static final String FOREIGN_LANGUAGE = "foreign-language";
  private static final String IS_SERIALIZED_ENTITY_DATA_ENABLED = "is-serialized-entity-data-enabled";
  private static final String MODEL_NAME = "model-name";
  private static final String MODEL_VERSION = "model-version";
  private static final String SCORE = "score";
  private static final String SERIALIZED_ENTITIES_DATA = "serialized-entities-data";
  private static final String TEXT_LANGUAGES = "text-languages";
  
  static Bundle createForeignLanguageExtra(String paramString, float paramFloat, int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("entity-type", paramString);
    localBundle.putFloat("score", paramFloat);
    localBundle.putInt("model-version", paramInt);
    paramString = new StringBuilder();
    paramString.append("langId_v");
    paramString.append(paramInt);
    localBundle.putString("model-name", paramString.toString());
    return localBundle;
  }
  
  public static RemoteAction findAction(TextClassification paramTextClassification, String paramString)
  {
    if ((paramTextClassification != null) && (paramString != null))
    {
      ArrayList localArrayList = getActionsIntents(paramTextClassification);
      if (localArrayList != null)
      {
        int i = localArrayList.size();
        for (int j = 0; j < i; j++)
        {
          Intent localIntent = (Intent)localArrayList.get(j);
          if ((localIntent != null) && (paramString.equals(localIntent.getAction()))) {
            return (RemoteAction)paramTextClassification.getActions().get(j);
          }
        }
      }
      return null;
    }
    return null;
  }
  
  public static RemoteAction findTranslateAction(TextClassification paramTextClassification)
  {
    return findAction(paramTextClassification, "android.intent.action.TRANSLATE");
  }
  
  public static Intent getActionIntent(Bundle paramBundle)
  {
    return (Intent)paramBundle.getParcelable("action-intent");
  }
  
  public static ArrayList<Intent> getActionsIntents(TextClassification paramTextClassification)
  {
    if (paramTextClassification == null) {
      return null;
    }
    return paramTextClassification.getExtras().getParcelableArrayList("actions-intents");
  }
  
  public static String getCopyText(Bundle paramBundle)
  {
    paramBundle = (Bundle)paramBundle.getParcelable("entities-extras");
    if (paramBundle == null) {
      return null;
    }
    return paramBundle.getString("text");
  }
  
  public static List<Bundle> getEntities(Bundle paramBundle)
  {
    return paramBundle.getParcelableArrayList("entities");
  }
  
  public static String getEntityType(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    return paramBundle.getString("entity-type");
  }
  
  public static Bundle getForeignLanguageExtra(TextClassification paramTextClassification)
  {
    if (paramTextClassification == null) {
      return null;
    }
    return paramTextClassification.getExtras().getBundle("foreign-language");
  }
  
  public static String getModelName(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    return paramBundle.getString("model-name");
  }
  
  public static float getScore(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return -1.0F;
    }
    return paramBundle.getFloat("score", -1.0F);
  }
  
  public static byte[] getSerializedEntityData(Bundle paramBundle)
  {
    return paramBundle.getByteArray("serialized-entities-data");
  }
  
  public static ULocale getTopLanguage(Intent paramIntent)
  {
    if (paramIntent == null) {
      return null;
    }
    paramIntent = paramIntent.getBundleExtra("android.view.textclassifier.extra.FROM_TEXT_CLASSIFIER");
    if (paramIntent == null) {
      return null;
    }
    Object localObject = paramIntent.getBundle("text-languages");
    if (localObject == null) {
      return null;
    }
    paramIntent = ((Bundle)localObject).getStringArray("entity-type");
    localObject = ((Bundle)localObject).getFloatArray("score");
    if ((paramIntent != null) && (localObject != null) && (paramIntent.length != 0) && (paramIntent.length == localObject.length))
    {
      int i = 0;
      int j = 1;
      while (j < paramIntent.length)
      {
        int k = i;
        if (localObject[i] < localObject[j]) {
          k = j;
        }
        j++;
        i = k;
      }
      return ULocale.forLanguageTag(paramIntent[i]);
    }
    return null;
  }
  
  public static boolean isSerializedEntityDataEnabled(TextLinks.Request paramRequest)
  {
    return paramRequest.getExtras().getBoolean("is-serialized-entity-data-enabled");
  }
  
  public static void putActionIntent(Bundle paramBundle, Intent paramIntent)
  {
    paramBundle.putParcelable("action-intent", paramIntent);
  }
  
  static void putActionsIntents(Bundle paramBundle, ArrayList<Intent> paramArrayList)
  {
    paramBundle.putParcelableArrayList("actions-intents", paramArrayList);
  }
  
  public static void putEntities(Bundle paramBundle, AnnotatorModel.ClassificationResult[] paramArrayOfClassificationResult)
  {
    if (ArrayUtils.isEmpty(paramArrayOfClassificationResult)) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfClassificationResult.length;
    for (int j = 0; j < i; j++)
    {
      AnnotatorModel.ClassificationResult localClassificationResult = paramArrayOfClassificationResult[j];
      if (localClassificationResult != null)
      {
        Bundle localBundle = new Bundle();
        localBundle.putString("entity-type", localClassificationResult.getCollection());
        localBundle.putByteArray("serialized-entities-data", localClassificationResult.getSerializedEntityData());
        localArrayList.add(localBundle);
      }
    }
    if (!localArrayList.isEmpty()) {
      paramBundle.putParcelableArrayList("entities", localArrayList);
    }
  }
  
  public static void putEntitiesExtras(Bundle paramBundle1, Bundle paramBundle2)
  {
    paramBundle1.putParcelable("entities-extras", paramBundle2);
  }
  
  static void putForeignLanguageExtra(Bundle paramBundle1, Bundle paramBundle2)
  {
    paramBundle1.putParcelable("foreign-language", paramBundle2);
  }
  
  public static void putIsSerializedEntityDataEnabled(Bundle paramBundle, boolean paramBoolean)
  {
    paramBundle.putBoolean("is-serialized-entity-data-enabled", paramBoolean);
  }
  
  public static void putSerializedEntityData(Bundle paramBundle, byte[] paramArrayOfByte)
  {
    paramBundle.putByteArray("serialized-entities-data", paramArrayOfByte);
  }
  
  public static void putTextLanguagesExtra(Bundle paramBundle1, Bundle paramBundle2)
  {
    paramBundle1.putBundle("text-languages", paramBundle2);
  }
  
  static void putTopLanguageScores(Bundle paramBundle, EntityConfidence paramEntityConfidence)
  {
    int i = Math.min(3, paramEntityConfidence.getEntities().size());
    String[] arrayOfString = (String[])paramEntityConfidence.getEntities().subList(0, i).toArray(new String[0]);
    float[] arrayOfFloat = new float[arrayOfString.length];
    for (i = 0; i < arrayOfString.length; i++) {
      arrayOfFloat[i] = paramEntityConfidence.getConfidenceScore(arrayOfString[i]);
    }
    paramBundle.putStringArray("entity-type", arrayOfString);
    paramBundle.putFloatArray("score", arrayOfFloat);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/ExtrasUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */