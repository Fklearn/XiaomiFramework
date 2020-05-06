package android.view.textclassifier.intent;

import android.content.Context;
import android.content.Intent;
import com.google.android.textclassifier.AnnotatorModel.ClassificationResult;
import java.time.Instant;
import java.util.List;

public abstract interface ClassificationIntentFactory
{
  public static void insertTranslateAction(List<LabeledIntent> paramList, Context paramContext, String paramString)
  {
    paramList.add(new LabeledIntent(paramContext.getString(17041249), null, paramContext.getString(17041250), null, new Intent("android.intent.action.TRANSLATE").putExtra("android.intent.extra.TEXT", paramString), paramString.hashCode()));
  }
  
  public abstract List<LabeledIntent> create(Context paramContext, String paramString, boolean paramBoolean, Instant paramInstant, AnnotatorModel.ClassificationResult paramClassificationResult);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/intent/ClassificationIntentFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */