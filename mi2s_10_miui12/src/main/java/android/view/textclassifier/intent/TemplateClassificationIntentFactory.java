package android.view.textclassifier.intent;

import android.content.Context;
import android.view.textclassifier.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import com.google.android.textclassifier.AnnotatorModel.ClassificationResult;
import com.google.android.textclassifier.RemoteActionTemplate;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class TemplateClassificationIntentFactory
  implements ClassificationIntentFactory
{
  private static final String TAG = "androidtc";
  private final ClassificationIntentFactory mFallback;
  private final TemplateIntentFactory mTemplateIntentFactory;
  
  public TemplateClassificationIntentFactory(TemplateIntentFactory paramTemplateIntentFactory, ClassificationIntentFactory paramClassificationIntentFactory)
  {
    this.mTemplateIntentFactory = ((TemplateIntentFactory)Preconditions.checkNotNull(paramTemplateIntentFactory));
    this.mFallback = ((ClassificationIntentFactory)Preconditions.checkNotNull(paramClassificationIntentFactory));
  }
  
  public List<LabeledIntent> create(Context paramContext, String paramString, boolean paramBoolean, Instant paramInstant, AnnotatorModel.ClassificationResult paramClassificationResult)
  {
    if (paramClassificationResult == null) {
      return Collections.emptyList();
    }
    RemoteActionTemplate[] arrayOfRemoteActionTemplate = paramClassificationResult.getRemoteActionTemplates();
    if (arrayOfRemoteActionTemplate == null)
    {
      Log.w("androidtc", "RemoteActionTemplate is missing, fallback to LegacyClassificationIntentFactory.");
      return this.mFallback.create(paramContext, paramString, paramBoolean, paramInstant, paramClassificationResult);
    }
    paramInstant = this.mTemplateIntentFactory.create(arrayOfRemoteActionTemplate);
    if (paramBoolean) {
      ClassificationIntentFactory.insertTranslateAction(paramInstant, paramContext, paramString.trim());
    }
    return paramInstant;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/intent/TemplateClassificationIntentFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */