package android.view.textclassifier;

import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.ParcelFileDescriptor;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.view.textclassifier.intent.ClassificationIntentFactory;
import android.view.textclassifier.intent.LabeledIntent;
import android.view.textclassifier.intent.LabeledIntent.Result;
import android.view.textclassifier.intent.LegacyClassificationIntentFactory;
import android.view.textclassifier.intent.TemplateClassificationIntentFactory;
import android.view.textclassifier.intent.TemplateIntentFactory;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.google.android.textclassifier.ActionsSuggestionsModel;
import com.google.android.textclassifier.ActionsSuggestionsModel.ActionSuggestion;
import com.google.android.textclassifier.ActionsSuggestionsModel.Conversation;
import com.google.android.textclassifier.ActionsSuggestionsModel.ConversationMessage;
import com.google.android.textclassifier.AnnotatorModel;
import com.google.android.textclassifier.AnnotatorModel.AnnotatedSpan;
import com.google.android.textclassifier.AnnotatorModel.AnnotationOptions;
import com.google.android.textclassifier.AnnotatorModel.AnnotationUsecase;
import com.google.android.textclassifier.AnnotatorModel.ClassificationOptions;
import com.google.android.textclassifier.AnnotatorModel.ClassificationResult;
import com.google.android.textclassifier.AnnotatorModel.SelectionOptions;
import com.google.android.textclassifier.LangIdModel;
import com.google.android.textclassifier.LangIdModel.LanguageResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TextClassifierImpl
  implements TextClassifier
{
  private static final String ACTIONS_FACTORY_MODEL_FILENAME_REGEX = "actions_suggestions\\.(.*)\\.model";
  private static final String ANNOTATOR_FACTORY_MODEL_FILENAME_REGEX = "textclassifier\\.(.*)\\.model";
  private static final File ANNOTATOR_UPDATED_MODEL_FILE;
  private static final boolean DEBUG = false;
  private static final File FACTORY_MODEL_DIR = new File("/etc/textclassifier/");
  private static final String LANG_ID_FACTORY_MODEL_FILENAME_REGEX = "lang_id.model";
  private static final String LOG_TAG = "androidtc";
  private static final File UPDATED_ACTIONS_MODEL = new File("/data/misc/textclassifier/actions_suggestions.model");
  private static final File UPDATED_LANG_ID_MODEL_FILE;
  @GuardedBy({"mLock"})
  private ModelFileManager.ModelFile mActionModelInUse;
  @GuardedBy({"mLock"})
  private ActionsSuggestionsModel mActionsImpl;
  private final ModelFileManager mActionsModelFileManager;
  private final Supplier<ActionsModelParamsSupplier.ActionsModelParams> mActionsModelParamsSupplier;
  @GuardedBy({"mLock"})
  private AnnotatorModel mAnnotatorImpl;
  private final ModelFileManager mAnnotatorModelFileManager;
  @GuardedBy({"mLock"})
  private ModelFileManager.ModelFile mAnnotatorModelInUse;
  private final ClassificationIntentFactory mClassificationIntentFactory;
  private final Context mContext;
  private final TextClassifier mFallback;
  private final GenerateLinksLogger mGenerateLinksLogger;
  @GuardedBy({"mLock"})
  private LangIdModel mLangIdImpl;
  private final ModelFileManager mLangIdModelFileManager;
  @GuardedBy({"mLock"})
  private ModelFileManager.ModelFile mLangIdModelInUse;
  private final Object mLock = new Object();
  private final SelectionSessionLogger mSessionLogger = new SelectionSessionLogger();
  private final TextClassificationConstants mSettings;
  private final TemplateIntentFactory mTemplateIntentFactory;
  private final TextClassifierEventTronLogger mTextClassifierEventTronLogger = new TextClassifierEventTronLogger();
  
  static
  {
    ANNOTATOR_UPDATED_MODEL_FILE = new File("/data/misc/textclassifier/textclassifier.model");
    UPDATED_LANG_ID_MODEL_FILE = new File("/data/misc/textclassifier/lang_id.model");
  }
  
  public TextClassifierImpl(Context paramContext, TextClassificationConstants paramTextClassificationConstants)
  {
    this(paramContext, paramTextClassificationConstants, TextClassifier.NO_OP);
  }
  
  public TextClassifierImpl(Context paramContext, TextClassificationConstants paramTextClassificationConstants, TextClassifier paramTextClassifier)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext));
    this.mFallback = ((TextClassifier)Preconditions.checkNotNull(paramTextClassifier));
    this.mSettings = ((TextClassificationConstants)Preconditions.checkNotNull(paramTextClassificationConstants));
    this.mGenerateLinksLogger = new GenerateLinksLogger(this.mSettings.getGenerateLinksLogSampleRate());
    this.mAnnotatorModelFileManager = new ModelFileManager(new ModelFileManager.ModelFileSupplierImpl(FACTORY_MODEL_DIR, "textclassifier\\.(.*)\\.model", ANNOTATOR_UPDATED_MODEL_FILE, _..Lambda.jJq8RXuVdjYF3lPq_77PEw1NJLM.INSTANCE, _..Lambda.NxwbyZSxofZ4Z5SQhfXmtLQ1nxk.INSTANCE));
    this.mLangIdModelFileManager = new ModelFileManager(new ModelFileManager.ModelFileSupplierImpl(FACTORY_MODEL_DIR, "lang_id.model", UPDATED_LANG_ID_MODEL_FILE, _..Lambda.0biFK4yZBmWN1EO2wtnXskzuEcE.INSTANCE, _..Lambda.TextClassifierImpl.RRbXefHgcUymI9_P95ArUyMvfbw.INSTANCE));
    this.mActionsModelFileManager = new ModelFileManager(new ModelFileManager.ModelFileSupplierImpl(FACTORY_MODEL_DIR, "actions_suggestions\\.(.*)\\.model", UPDATED_ACTIONS_MODEL, _..Lambda.9N8WImc0VBjy2oxI_Gk5_Pbye_A.INSTANCE, _..Lambda.XeE_KI7QgMKzF9vYRSoFWAolyuA.INSTANCE));
    this.mTemplateIntentFactory = new TemplateIntentFactory();
    if (this.mSettings.isTemplateIntentFactoryEnabled()) {
      paramContext = new TemplateClassificationIntentFactory(this.mTemplateIntentFactory, new LegacyClassificationIntentFactory());
    } else {
      paramContext = new LegacyClassificationIntentFactory();
    }
    this.mClassificationIntentFactory = paramContext;
    this.mActionsModelParamsSupplier = new ActionsModelParamsSupplier(this.mContext, new _..Lambda.TextClassifierImpl.iSt_Guet_O6Vtdk0MA4z_Z4lzaM(this));
  }
  
  private static String concatenateLocales(LocaleList paramLocaleList)
  {
    if (paramLocaleList == null) {
      paramLocaleList = "";
    } else {
      paramLocaleList = paramLocaleList.toLanguageTags();
    }
    return paramLocaleList;
  }
  
  private TextClassification createClassificationResult(AnnotatorModel.ClassificationResult[] paramArrayOfClassificationResult, String paramString, int paramInt1, int paramInt2, Instant paramInstant)
  {
    Object localObject1 = paramString.substring(paramInt1, paramInt2);
    TextClassification.Builder localBuilder = new TextClassification.Builder().setText((String)localObject1);
    int i = paramArrayOfClassificationResult.length;
    boolean bool = false;
    if (i > 0) {
      localObject2 = paramArrayOfClassificationResult[0];
    } else {
      localObject2 = null;
    }
    int j = 0;
    for (Object localObject3 = localObject2; j < i; localObject3 = localObject2)
    {
      localBuilder.setEntityType(paramArrayOfClassificationResult[j]);
      localObject2 = localObject3;
      if (paramArrayOfClassificationResult[j].getScore() > ((AnnotatorModel.ClassificationResult)localObject3).getScore()) {
        localObject2 = paramArrayOfClassificationResult[j];
      }
      j++;
    }
    paramArrayOfClassificationResult = generateLanguageBundles(paramString, paramInt1, paramInt2);
    Object localObject2 = (Bundle)paramArrayOfClassificationResult.first;
    Object localObject4 = (Bundle)paramArrayOfClassificationResult.second;
    localBuilder.setForeignLanguageExtra((Bundle)localObject4);
    j = 1;
    Object localObject5 = this.mClassificationIntentFactory;
    paramArrayOfClassificationResult = this.mContext;
    if (localObject4 != null) {
      bool = true;
    }
    paramInstant = ((ClassificationIntentFactory)localObject5).create(paramArrayOfClassificationResult, (String)localObject1, bool, paramInstant, (AnnotatorModel.ClassificationResult)localObject3);
    paramArrayOfClassificationResult = _..Lambda.TextClassifierImpl.naj1VfHYH1Qfut8yLHu8DlsggQE.INSTANCE;
    localObject3 = paramInstant.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject5 = (LabeledIntent)((Iterator)localObject3).next();
      localObject4 = ((LabeledIntent)localObject5).resolve(this.mContext, paramArrayOfClassificationResult, (Bundle)localObject2);
      if (localObject4 != null)
      {
        localObject1 = ((LabeledIntent.Result)localObject4).resolvedIntent;
        localObject4 = ((LabeledIntent.Result)localObject4).remoteAction;
        if (j != 0)
        {
          localBuilder.setIcon(((RemoteAction)localObject4).getIcon().loadDrawable(this.mContext));
          localBuilder.setLabel(((RemoteAction)localObject4).getTitle().toString());
          localBuilder.setIntent((Intent)localObject1);
          localBuilder.setOnClickListener(TextClassification.createIntentOnClickListener(TextClassification.createPendingIntent(this.mContext, (Intent)localObject1, ((LabeledIntent)localObject5).requestCode)));
          j = 0;
        }
        localBuilder.addAction((RemoteAction)localObject4, (Intent)localObject1);
      }
    }
    return localBuilder.setId(createId(paramString, paramInt1, paramInt2)).build();
  }
  
  private ConversationActions createConversationActionResult(ConversationActions.Request paramRequest, ActionsSuggestionsModel.ActionSuggestion[] paramArrayOfActionSuggestion)
  {
    Collection localCollection = resolveActionTypesFromRequest(paramRequest);
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfActionSuggestion.length;
    for (int j = 0; j < i; j++)
    {
      ActionsSuggestionsModel.ActionSuggestion localActionSuggestion = paramArrayOfActionSuggestion[j];
      String str = localActionSuggestion.getActionType();
      if (localCollection.contains(str))
      {
        LabeledIntent.Result localResult = ActionsSuggestionsHelper.createLabeledIntentResult(this.mContext, this.mTemplateIntentFactory, localActionSuggestion);
        localObject = null;
        Bundle localBundle = new Bundle();
        if (localResult != null)
        {
          localObject = localResult.remoteAction;
          ExtrasUtils.putActionIntent(localBundle, localResult.resolvedIntent);
        }
        ExtrasUtils.putSerializedEntityData(localBundle, localActionSuggestion.getSerializedEntityData());
        ExtrasUtils.putEntitiesExtras(localBundle, TemplateIntentFactory.nameVariantsToBundle(localActionSuggestion.getEntityData()));
        localArrayList.add(new ConversationAction.Builder(str).setConfidenceScore(localActionSuggestion.getScore()).setTextReply(localActionSuggestion.getResponseText()).setAction((RemoteAction)localObject).setExtras(localBundle).build());
      }
    }
    Object localObject = ActionsSuggestionsHelper.removeActionsWithDuplicates(localArrayList);
    paramArrayOfActionSuggestion = (ActionsSuggestionsModel.ActionSuggestion[])localObject;
    if (paramRequest.getMaxSuggestions() >= 0)
    {
      paramArrayOfActionSuggestion = (ActionsSuggestionsModel.ActionSuggestion[])localObject;
      if (((List)localObject).size() > paramRequest.getMaxSuggestions()) {
        paramArrayOfActionSuggestion = ((List)localObject).subList(0, paramRequest.getMaxSuggestions());
      }
    }
    return new ConversationActions(paramArrayOfActionSuggestion, ActionsSuggestionsHelper.createResultId(this.mContext, paramRequest.getConversation(), this.mActionModelInUse.getVersion(), this.mActionModelInUse.getSupportedLocales()));
  }
  
  private String createId(String paramString, int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      paramString = SelectionSessionLogger.createId(paramString, paramInt1, paramInt2, this.mContext, this.mAnnotatorModelInUse.getVersion(), this.mAnnotatorModelInUse.getSupportedLocales());
      return paramString;
    }
  }
  
  private String detectLanguageTagsFromText(CharSequence paramCharSequence)
  {
    if (!this.mSettings.isDetectLanguagesFromTextEnabled()) {
      return null;
    }
    float f = getLangIdThreshold();
    if ((f >= 0.0F) && (f <= 1.0F))
    {
      TextLanguage localTextLanguage = detectLanguage(new TextLanguage.Request.Builder(paramCharSequence).build());
      int i = localTextLanguage.getLocaleHypothesisCount();
      ArrayList localArrayList = new ArrayList();
      for (int j = 0; j < i; j++)
      {
        paramCharSequence = localTextLanguage.getLocale(j);
        if (localTextLanguage.getConfidenceScore(paramCharSequence) < f) {
          break;
        }
        localArrayList.add(paramCharSequence.toLanguageTag());
      }
      if (localArrayList.isEmpty()) {
        return null;
      }
      return String.join(",", localArrayList);
    }
    paramCharSequence = new StringBuilder();
    paramCharSequence.append("[detectLanguageTagsFromText] unexpected threshold is found: ");
    paramCharSequence.append(f);
    Log.w("androidtc", paramCharSequence.toString());
    return null;
  }
  
  private EntityConfidence detectLanguages(String paramString)
    throws FileNotFoundException
  {
    paramString = getLangIdImpl().detectLanguages(paramString);
    ArrayMap localArrayMap = new ArrayMap();
    int i = paramString.length;
    for (int j = 0; j < i; j++)
    {
      Object localObject = paramString[j];
      localArrayMap.put(((LangIdModel.LanguageResult)localObject).getLanguage(), Float.valueOf(((LangIdModel.LanguageResult)localObject).getScore()));
    }
    return new EntityConfidence(localArrayMap);
  }
  
  private EntityConfidence detectLanguages(String paramString, int paramInt1, int paramInt2)
    throws FileNotFoundException
  {
    boolean bool;
    if (paramInt1 >= 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    if (paramInt2 <= paramString.length()) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    if (paramInt1 <= paramInt2) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    Object localObject1 = this.mSettings.getLangIdContextSettings();
    int i = (int)localObject1[0];
    float f1 = localObject1[1];
    float f2 = localObject1[2];
    float f3 = 1.0F - f2;
    Log.v("androidtc", String.format(Locale.US, "LangIdContextSettings: minimumTextSize=%d, penalizeRatio=%.2f, subjectTextScoreRatio=%.2f, moreTextScoreRatio=%.2f", new Object[] { Integer.valueOf(i), Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3) }));
    if ((paramInt2 - paramInt1 < i) && (f1 <= 0.0F)) {
      return new EntityConfidence(Collections.emptyMap());
    }
    Object localObject2 = paramString.substring(paramInt1, paramInt2);
    localObject1 = detectLanguages((String)localObject2);
    if (((String)localObject2).length() < i) {
      if (((String)localObject2).length() != paramString.length())
      {
        if (f2 * f1 < 1.0F)
        {
          if (f3 >= 0.0F) {
            paramString = detectLanguages(TextClassifier.Utils.getSubString(paramString, paramInt1, paramInt2, i));
          } else {
            paramString = new EntityConfidence(Collections.emptyMap());
          }
          localObject2 = new ArrayMap();
          Object localObject3 = new ArraySet();
          ((Set)localObject3).addAll(((EntityConfidence)localObject1).getEntities());
          ((Set)localObject3).addAll(paramString.getEntities());
          Iterator localIterator = ((Set)localObject3).iterator();
          while (localIterator.hasNext())
          {
            localObject3 = (String)localIterator.next();
            ((Map)localObject2).put(localObject3, Float.valueOf((((EntityConfidence)localObject1).getConfidenceScore((String)localObject3) * f2 + paramString.getConfidenceScore((String)localObject3) * f3) * f1));
          }
          return new EntityConfidence((Map)localObject2);
        }
      }
      else {}
    }
    return (EntityConfidence)localObject1;
  }
  
  private Pair<Bundle, Bundle> generateLanguageBundles(String paramString, int paramInt1, int paramInt2)
  {
    if (!this.mSettings.isTranslateInClassificationEnabled()) {
      return null;
    }
    try
    {
      float f1 = getLangIdThreshold();
      if ((f1 >= 0.0F) && (f1 <= 1.0F))
      {
        Object localObject1 = detectLanguages(paramString, paramInt1, paramInt2);
        if (((EntityConfidence)localObject1).getEntities().isEmpty()) {
          return Pair.create(null, null);
        }
        paramString = new android/os/Bundle;
        paramString.<init>();
        ExtrasUtils.putTopLanguageScores(paramString, (EntityConfidence)localObject1);
        Object localObject2 = (String)((EntityConfidence)localObject1).getEntities().get(0);
        float f2 = ((EntityConfidence)localObject1).getConfidenceScore((String)localObject2);
        if (f2 < f1) {
          return Pair.create(paramString, null);
        }
        Log.v("androidtc", String.format(Locale.US, "Language detected: <%s:%.2f>", new Object[] { localObject2, Float.valueOf(f2) }));
        localObject1 = new java/util/Locale;
        ((Locale)localObject1).<init>((String)localObject2);
        localObject2 = LocaleList.getDefault();
        paramInt2 = ((LocaleList)localObject2).size();
        for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++) {
          if (((LocaleList)localObject2).get(paramInt1).getLanguage().equals(((Locale)localObject1).getLanguage())) {
            return Pair.create(paramString, null);
          }
        }
        return Pair.create(paramString, ExtrasUtils.createForeignLanguageExtra(((Locale)localObject1).getLanguage(), f2, getLangIdImpl().getVersion()));
      }
      paramString = new java/lang/StringBuilder;
      paramString.<init>();
      paramString.append("[detectForeignLanguage] unexpected threshold is found: ");
      paramString.append(f1);
      Log.w("androidtc", paramString.toString());
      paramString = Pair.create(null, null);
      return paramString;
    }
    finally
    {
      Log.e("androidtc", "Error generating language bundles.", paramString);
    }
    return Pair.create(null, null);
  }
  
  private ActionsSuggestionsModel getActionsImpl()
    throws FileNotFoundException
  {
    Object localObject5;
    synchronized (this.mLock)
    {
      Object localObject2 = this.mActionsModelFileManager.findBestModelFile(LocaleList.getDefault());
      if (localObject2 == null) {
        return null;
      }
      if ((this.mActionsImpl == null) || (!Objects.equals(this.mActionModelInUse, localObject2)))
      {
        localObject5 = new java/lang/StringBuilder;
        ((StringBuilder)localObject5).<init>();
        ((StringBuilder)localObject5).append("Loading ");
        ((StringBuilder)localObject5).append(localObject2);
        Log.d("androidtc", ((StringBuilder)localObject5).toString());
        localObject5 = new java/io/File;
        ((File)localObject5).<init>(((ModelFileManager.ModelFile)localObject2).getPath());
        localObject5 = ParcelFileDescriptor.open((File)localObject5, 268435456);
        if (localObject5 != null) {}
      }
      try
      {
        localObject6 = new java/lang/StringBuilder;
        ((StringBuilder)localObject6).<init>();
        ((StringBuilder)localObject6).append("Failed to read the model file: ");
        ((StringBuilder)localObject6).append(((ModelFileManager.ModelFile)localObject2).getPath());
        Log.d("androidtc", ((StringBuilder)localObject6).toString());
        maybeCloseAndLogError((ParcelFileDescriptor)localObject5);
        return null;
      }
      finally
      {
        Object localObject6;
        ActionsModelParamsSupplier.ActionsModelParams localActionsModelParams;
        maybeCloseAndLogError((ParcelFileDescriptor)localObject5);
      }
      localActionsModelParams = (ActionsModelParamsSupplier.ActionsModelParams)this.mActionsModelParamsSupplier.get();
      localObject6 = new com/google/android/textclassifier/ActionsSuggestionsModel;
      ((ActionsSuggestionsModel)localObject6).<init>(((ParcelFileDescriptor)localObject5).getFd(), localActionsModelParams.getSerializedPreconditions((ModelFileManager.ModelFile)localObject2));
      this.mActionsImpl = ((ActionsSuggestionsModel)localObject6);
      this.mActionModelInUse = ((ModelFileManager.ModelFile)localObject2);
      maybeCloseAndLogError((ParcelFileDescriptor)localObject5);
      localObject2 = this.mActionsImpl;
      return (ActionsSuggestionsModel)localObject2;
    }
  }
  
  /* Error */
  private AnnotatorModel getAnnotatorImpl(LocaleList paramLocaleList)
    throws FileNotFoundException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 100	android/view/textclassifier/TextClassifierImpl:mLock	Ljava/lang/Object;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_1
    //   8: ifnonnull +14 -> 22
    //   11: invokestatic 692	android/os/LocaleList:getDefault	()Landroid/os/LocaleList;
    //   14: astore_1
    //   15: goto +7 -> 22
    //   18: astore_1
    //   19: goto +179 -> 198
    //   22: aload_0
    //   23: getfield 160	android/view/textclassifier/TextClassifierImpl:mAnnotatorModelFileManager	Landroid/view/textclassifier/ModelFileManager;
    //   26: aload_1
    //   27: invokevirtual 721	android/view/textclassifier/ModelFileManager:findBestModelFile	(Landroid/os/LocaleList;)Landroid/view/textclassifier/ModelFileManager$ModelFile;
    //   30: astore_3
    //   31: aload_3
    //   32: ifnull +124 -> 156
    //   35: aload_0
    //   36: getfield 774	android/view/textclassifier/TextClassifierImpl:mAnnotatorImpl	Lcom/google/android/textclassifier/AnnotatorModel;
    //   39: ifnull +14 -> 53
    //   42: aload_0
    //   43: getfield 498	android/view/textclassifier/TextClassifierImpl:mAnnotatorModelInUse	Landroid/view/textclassifier/ModelFileManager$ModelFile;
    //   46: aload_3
    //   47: invokestatic 728	java/util/Objects:equals	(Ljava/lang/Object;Ljava/lang/Object;)Z
    //   50: ifne +97 -> 147
    //   53: new 550	java/lang/StringBuilder
    //   56: astore_1
    //   57: aload_1
    //   58: invokespecial 551	java/lang/StringBuilder:<init>	()V
    //   61: aload_1
    //   62: ldc_w 730
    //   65: invokevirtual 557	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: pop
    //   69: aload_1
    //   70: aload_3
    //   71: invokevirtual 733	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   74: pop
    //   75: ldc 25
    //   77: aload_1
    //   78: invokevirtual 561	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   81: invokestatic 736	android/view/textclassifier/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   84: new 68	java/io/File
    //   87: astore_1
    //   88: aload_1
    //   89: aload_3
    //   90: invokevirtual 739	android/view/textclassifier/ModelFileManager$ModelFile:getPath	()Ljava/lang/String;
    //   93: invokespecial 74	java/io/File:<init>	(Ljava/lang/String;)V
    //   96: aload_1
    //   97: ldc_w 740
    //   100: invokestatic 746	android/os/ParcelFileDescriptor:open	(Ljava/io/File;I)Landroid/os/ParcelFileDescriptor;
    //   103: astore_1
    //   104: aload_1
    //   105: ifnull +38 -> 143
    //   108: new 776	com/google/android/textclassifier/AnnotatorModel
    //   111: astore 4
    //   113: aload 4
    //   115: aload_1
    //   116: invokevirtual 763	android/os/ParcelFileDescriptor:getFd	()I
    //   119: invokespecial 777	com/google/android/textclassifier/AnnotatorModel:<init>	(I)V
    //   122: aload_0
    //   123: aload 4
    //   125: putfield 774	android/view/textclassifier/TextClassifierImpl:mAnnotatorImpl	Lcom/google/android/textclassifier/AnnotatorModel;
    //   128: aload_0
    //   129: aload_3
    //   130: putfield 498	android/view/textclassifier/TextClassifierImpl:mAnnotatorModelInUse	Landroid/view/textclassifier/ModelFileManager$ModelFile;
    //   133: goto +10 -> 143
    //   136: astore_3
    //   137: aload_1
    //   138: invokestatic 752	android/view/textclassifier/TextClassifierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
    //   141: aload_3
    //   142: athrow
    //   143: aload_1
    //   144: invokestatic 752	android/view/textclassifier/TextClassifierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
    //   147: aload_0
    //   148: getfield 774	android/view/textclassifier/TextClassifierImpl:mAnnotatorImpl	Lcom/google/android/textclassifier/AnnotatorModel;
    //   151: astore_1
    //   152: aload_2
    //   153: monitorexit
    //   154: aload_1
    //   155: areturn
    //   156: new 571	java/io/FileNotFoundException
    //   159: astore 4
    //   161: new 550	java/lang/StringBuilder
    //   164: astore_3
    //   165: aload_3
    //   166: invokespecial 551	java/lang/StringBuilder:<init>	()V
    //   169: aload_3
    //   170: ldc_w 779
    //   173: invokevirtual 557	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   176: pop
    //   177: aload_3
    //   178: aload_1
    //   179: invokevirtual 225	android/os/LocaleList:toLanguageTags	()Ljava/lang/String;
    //   182: invokevirtual 557	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: pop
    //   186: aload 4
    //   188: aload_3
    //   189: invokevirtual 561	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokespecial 780	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   195: aload 4
    //   197: athrow
    //   198: aload_2
    //   199: monitorexit
    //   200: aload_1
    //   201: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	202	0	this	TextClassifierImpl
    //   0	202	1	paramLocaleList	LocaleList
    //   4	195	2	localObject1	Object
    //   30	100	3	localModelFile	ModelFileManager.ModelFile
    //   136	6	3	localObject2	Object
    //   164	25	3	localStringBuilder	StringBuilder
    //   111	85	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   11	15	18	finally
    //   22	31	18	finally
    //   35	53	18	finally
    //   53	104	18	finally
    //   137	143	18	finally
    //   143	147	18	finally
    //   147	154	18	finally
    //   156	198	18	finally
    //   198	200	18	finally
    //   108	133	136	finally
  }
  
  private Collection<String> getEntitiesForHints(Collection<String> paramCollection)
  {
    boolean bool = paramCollection.contains("android.text_is_editable");
    int i;
    if (bool == paramCollection.contains("android.text_is_not_editable")) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      return this.mSettings.getEntityListDefault();
    }
    if (bool) {
      return this.mSettings.getEntityListEditable();
    }
    return this.mSettings.getEntityListNotEditable();
  }
  
  /* Error */
  private LangIdModel getLangIdImpl()
    throws FileNotFoundException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 100	android/view/textclassifier/TextClassifierImpl:mLock	Ljava/lang/Object;
    //   4: astore_1
    //   5: aload_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 172	android/view/textclassifier/TextClassifierImpl:mLangIdModelFileManager	Landroid/view/textclassifier/ModelFileManager;
    //   11: aconst_null
    //   12: invokevirtual 721	android/view/textclassifier/ModelFileManager:findBestModelFile	(Landroid/os/LocaleList;)Landroid/view/textclassifier/ModelFileManager$ModelFile;
    //   15: astore_2
    //   16: aload_2
    //   17: ifnull +124 -> 141
    //   20: aload_0
    //   21: getfield 798	android/view/textclassifier/TextClassifierImpl:mLangIdImpl	Lcom/google/android/textclassifier/LangIdModel;
    //   24: ifnull +14 -> 38
    //   27: aload_0
    //   28: getfield 800	android/view/textclassifier/TextClassifierImpl:mLangIdModelInUse	Landroid/view/textclassifier/ModelFileManager$ModelFile;
    //   31: aload_2
    //   32: invokestatic 728	java/util/Objects:equals	(Ljava/lang/Object;Ljava/lang/Object;)Z
    //   35: ifne +97 -> 132
    //   38: new 550	java/lang/StringBuilder
    //   41: astore_3
    //   42: aload_3
    //   43: invokespecial 551	java/lang/StringBuilder:<init>	()V
    //   46: aload_3
    //   47: ldc_w 730
    //   50: invokevirtual 557	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: pop
    //   54: aload_3
    //   55: aload_2
    //   56: invokevirtual 733	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   59: pop
    //   60: ldc 25
    //   62: aload_3
    //   63: invokevirtual 561	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: invokestatic 736	android/view/textclassifier/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   69: new 68	java/io/File
    //   72: astore_3
    //   73: aload_3
    //   74: aload_2
    //   75: invokevirtual 739	android/view/textclassifier/ModelFileManager$ModelFile:getPath	()Ljava/lang/String;
    //   78: invokespecial 74	java/io/File:<init>	(Ljava/lang/String;)V
    //   81: aload_3
    //   82: ldc_w 740
    //   85: invokestatic 746	android/os/ParcelFileDescriptor:open	(Ljava/io/File;I)Landroid/os/ParcelFileDescriptor;
    //   88: astore_3
    //   89: aload_3
    //   90: ifnull +38 -> 128
    //   93: new 577	com/google/android/textclassifier/LangIdModel
    //   96: astore 4
    //   98: aload 4
    //   100: aload_3
    //   101: invokevirtual 763	android/os/ParcelFileDescriptor:getFd	()I
    //   104: invokespecial 801	com/google/android/textclassifier/LangIdModel:<init>	(I)V
    //   107: aload_0
    //   108: aload 4
    //   110: putfield 798	android/view/textclassifier/TextClassifierImpl:mLangIdImpl	Lcom/google/android/textclassifier/LangIdModel;
    //   113: aload_0
    //   114: aload_2
    //   115: putfield 800	android/view/textclassifier/TextClassifierImpl:mLangIdModelInUse	Landroid/view/textclassifier/ModelFileManager$ModelFile;
    //   118: goto +10 -> 128
    //   121: astore_2
    //   122: aload_3
    //   123: invokestatic 752	android/view/textclassifier/TextClassifierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
    //   126: aload_2
    //   127: athrow
    //   128: aload_3
    //   129: invokestatic 752	android/view/textclassifier/TextClassifierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
    //   132: aload_0
    //   133: getfield 798	android/view/textclassifier/TextClassifierImpl:mLangIdImpl	Lcom/google/android/textclassifier/LangIdModel;
    //   136: astore_2
    //   137: aload_1
    //   138: monitorexit
    //   139: aload_2
    //   140: areturn
    //   141: new 571	java/io/FileNotFoundException
    //   144: astore_2
    //   145: aload_2
    //   146: ldc_w 803
    //   149: invokespecial 780	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
    //   152: aload_2
    //   153: athrow
    //   154: astore_2
    //   155: aload_1
    //   156: monitorexit
    //   157: aload_2
    //   158: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	159	0	this	TextClassifierImpl
    //   4	152	1	localObject1	Object
    //   15	100	2	localModelFile	ModelFileManager.ModelFile
    //   121	6	2	localObject2	Object
    //   136	17	2	localObject3	Object
    //   154	4	2	localObject4	Object
    //   41	88	3	localObject5	Object
    //   96	13	4	localLangIdModel	LangIdModel
    // Exception table:
    //   from	to	target	type
    //   93	118	121	finally
    //   7	16	154	finally
    //   20	38	154	finally
    //   38	89	154	finally
    //   122	128	154	finally
    //   128	132	154	finally
    //   132	139	154	finally
    //   141	154	154	finally
    //   155	157	154	finally
  }
  
  private float getLangIdThreshold()
  {
    try
    {
      float f;
      if (this.mSettings.getLangIdThresholdOverride() >= 0.0F) {
        f = this.mSettings.getLangIdThresholdOverride();
      } else {
        f = getLangIdImpl().getLangIdThreshold();
      }
      return f;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Log.v("androidtc", "Using default foreign language threshold: 0.5");
    }
    return 0.5F;
  }
  
  private String getResourceLocalesString()
  {
    try
    {
      String str = this.mContext.getResources().getConfiguration().getLocales().toLanguageTags();
      return str;
    }
    catch (NullPointerException localNullPointerException) {}
    return LocaleList.getDefault().toLanguageTags();
  }
  
  private static void maybeCloseAndLogError(ParcelFileDescriptor paramParcelFileDescriptor)
  {
    if (paramParcelFileDescriptor == null) {
      return;
    }
    try
    {
      paramParcelFileDescriptor.close();
    }
    catch (IOException paramParcelFileDescriptor)
    {
      Log.e("androidtc", "Error closing file.", paramParcelFileDescriptor);
    }
  }
  
  private Collection<String> resolveActionTypesFromRequest(ConversationActions.Request paramRequest)
  {
    List localList;
    if (paramRequest.getHints().contains("notification")) {
      localList = this.mSettings.getNotificationConversationActionTypes();
    } else {
      localList = this.mSettings.getInAppConversationActionTypes();
    }
    return paramRequest.getTypeConfig().resolveEntityListModifications(localList);
  }
  
  public TextClassification classifyText(TextClassification.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    try
    {
      int i = paramRequest.getEndIndex();
      int j = paramRequest.getStartIndex();
      String str1 = paramRequest.getText().toString();
      if ((str1.length() > 0) && (i - j <= this.mSettings.getClassifyTextMaxRangeLength()))
      {
        String str2 = concatenateLocales(paramRequest.getDefaultLocales());
        Object localObject1 = detectLanguageTagsFromText(paramRequest.getText());
        Object localObject2;
        if (paramRequest.getReferenceTime() != null) {
          localObject2 = paramRequest.getReferenceTime();
        } else {
          localObject2 = ZonedDateTime.now();
        }
        AnnotatorModel localAnnotatorModel = getAnnotatorImpl(paramRequest.getDefaultLocales());
        i = paramRequest.getStartIndex();
        j = paramRequest.getEndIndex();
        AnnotatorModel.ClassificationOptions localClassificationOptions = new com/google/android/textclassifier/AnnotatorModel$ClassificationOptions;
        localClassificationOptions.<init>(((ZonedDateTime)localObject2).toInstant().toEpochMilli(), ((ZonedDateTime)localObject2).getZone().getId(), str2, (String)localObject1);
        localObject1 = localAnnotatorModel.classifyText(str1, i, j, localClassificationOptions, this.mContext, getResourceLocalesString());
        if (localObject1.length > 0)
        {
          localObject2 = createClassificationResult((AnnotatorModel.ClassificationResult[])localObject1, str1, paramRequest.getStartIndex(), paramRequest.getEndIndex(), ((ZonedDateTime)localObject2).toInstant());
          return (TextClassification)localObject2;
        }
      }
    }
    finally
    {
      Log.e("androidtc", "Error getting text classification info.", localThrowable);
    }
    return this.mFallback.classifyText(paramRequest);
  }
  
  public TextLanguage detectLanguage(TextLanguage.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    try
    {
      Object localObject = new android/view/textclassifier/TextLanguage$Builder;
      ((TextLanguage.Builder)localObject).<init>();
      LangIdModel.LanguageResult[] arrayOfLanguageResult = getLangIdImpl().detectLanguages(paramRequest.getText().toString());
      for (int i = 0; i < arrayOfLanguageResult.length; i++) {
        ((TextLanguage.Builder)localObject).putLocale(ULocale.forLanguageTag(arrayOfLanguageResult[i].getLanguage()), arrayOfLanguageResult[i].getScore());
      }
      localObject = ((TextLanguage.Builder)localObject).build();
      return (TextLanguage)localObject;
    }
    finally
    {
      Log.e("androidtc", "Error detecting text language.", localThrowable);
    }
    return this.mFallback.detectLanguage(paramRequest);
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      paramIndentingPrintWriter.println("TextClassifierImpl:");
      paramIndentingPrintWriter.increaseIndent();
      paramIndentingPrintWriter.println("Annotator model file(s):");
      paramIndentingPrintWriter.increaseIndent();
      Iterator localIterator = this.mAnnotatorModelFileManager.listModelFiles().iterator();
      while (localIterator.hasNext()) {
        paramIndentingPrintWriter.println(((ModelFileManager.ModelFile)localIterator.next()).toString());
      }
      paramIndentingPrintWriter.decreaseIndent();
      paramIndentingPrintWriter.println("LangID model file(s):");
      paramIndentingPrintWriter.increaseIndent();
      localIterator = this.mLangIdModelFileManager.listModelFiles().iterator();
      while (localIterator.hasNext()) {
        paramIndentingPrintWriter.println(((ModelFileManager.ModelFile)localIterator.next()).toString());
      }
      paramIndentingPrintWriter.decreaseIndent();
      paramIndentingPrintWriter.println("Actions model file(s):");
      paramIndentingPrintWriter.increaseIndent();
      localIterator = this.mActionsModelFileManager.listModelFiles().iterator();
      while (localIterator.hasNext()) {
        paramIndentingPrintWriter.println(((ModelFileManager.ModelFile)localIterator.next()).toString());
      }
      paramIndentingPrintWriter.decreaseIndent();
      paramIndentingPrintWriter.printPair("mFallback", this.mFallback);
      paramIndentingPrintWriter.decreaseIndent();
      paramIndentingPrintWriter.println();
      return;
    }
  }
  
  public TextLinks generateLinks(TextLinks.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkTextLength(paramRequest.getText(), getMaxGenerateLinksTextLength());
    TextClassifier.Utils.checkMainThread();
    if ((!this.mSettings.isSmartLinkifyEnabled()) && (paramRequest.isLegacyFallback())) {
      return TextClassifier.Utils.generateLegacyLinks(paramRequest);
    }
    String str = paramRequest.getText().toString();
    Object localObject1 = new TextLinks.Builder(str);
    try
    {
      long l1 = System.currentTimeMillis();
      Object localObject2 = ZonedDateTime.now();
      Object localObject3;
      if (paramRequest.getEntityConfig() != null) {
        localObject3 = paramRequest.getEntityConfig().resolveEntityListModifications(getEntitiesForHints(paramRequest.getEntityConfig().getHints()));
      } else {
        localObject3 = this.mSettings.getEntityListDefault();
      }
      Object localObject4 = concatenateLocales(paramRequest.getDefaultLocales());
      Object localObject5 = detectLanguageTagsFromText(paramRequest.getText());
      AnnotatorModel localAnnotatorModel = getAnnotatorImpl(paramRequest.getDefaultLocales());
      boolean bool = ExtrasUtils.isSerializedEntityDataEnabled(paramRequest);
      Object localObject6 = new com/google/android/textclassifier/AnnotatorModel$AnnotationOptions;
      ((AnnotatorModel.AnnotationOptions)localObject6).<init>(((ZonedDateTime)localObject2).toInstant().toEpochMilli(), ((ZonedDateTime)localObject2).getZone().getId(), (String)localObject4, (String)localObject5, (Collection)localObject3, AnnotatorModel.AnnotationUsecase.SMART.getValue(), bool);
      for (str : localAnnotatorModel.annotate(str, (AnnotatorModel.AnnotationOptions)localObject6))
      {
        localObject6 = str.getClassification();
        if ((localObject6.length != 0) && (((Collection)localObject3).contains(localObject6[0].getCollection())))
        {
          localObject4 = new android/util/ArrayMap;
          ((ArrayMap)localObject4).<init>();
          for (int k = 0; k < localObject6.length; k++) {
            ((Map)localObject4).put(localObject6[k].getCollection(), Float.valueOf(localObject6[k].getScore()));
          }
          localObject2 = new android/os/Bundle;
          ((Bundle)localObject2).<init>();
          if (bool) {
            ExtrasUtils.putEntities((Bundle)localObject2, (AnnotatorModel.ClassificationResult[])localObject6);
          }
          ((TextLinks.Builder)localObject1).addLink(str.getStartIndex(), str.getEndIndex(), (Map)localObject4, (Bundle)localObject2);
        }
      }
      localObject1 = ((TextLinks.Builder)localObject1).build();
      long l2 = System.currentTimeMillis();
      if (paramRequest.getCallingPackageName() == null) {
        localObject3 = this.mContext.getPackageName();
      } else {
        localObject3 = paramRequest.getCallingPackageName();
      }
      this.mGenerateLinksLogger.logGenerateLinks(paramRequest.getText(), (TextLinks)localObject1, (String)localObject3, l2 - l1);
      return (TextLinks)localObject1;
    }
    finally
    {
      Log.e("androidtc", "Error getting links info.", localThrowable);
    }
    return this.mFallback.generateLinks(paramRequest);
  }
  
  public int getMaxGenerateLinksTextLength()
  {
    return this.mSettings.getGenerateLinksMaxTextLength();
  }
  
  public void onSelectionEvent(SelectionEvent paramSelectionEvent)
  {
    this.mSessionLogger.writeEvent(paramSelectionEvent);
  }
  
  public void onTextClassifierEvent(TextClassifierEvent paramTextClassifierEvent)
  {
    try
    {
      SelectionEvent localSelectionEvent = paramTextClassifierEvent.toSelectionEvent();
      if (localSelectionEvent != null) {
        this.mSessionLogger.writeEvent(localSelectionEvent);
      } else {
        this.mTextClassifierEventTronLogger.writeEvent(paramTextClassifierEvent);
      }
    }
    catch (Exception paramTextClassifierEvent)
    {
      Log.e("androidtc", "Error writing event", paramTextClassifierEvent);
    }
  }
  
  public ConversationActions suggestConversationActions(ConversationActions.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    try
    {
      Object localObject1 = getActionsImpl();
      if (localObject1 == null) {
        return this.mFallback.suggestConversationActions(paramRequest);
      }
      Object localObject2 = paramRequest.getConversation();
      Object localObject3 = new android/view/textclassifier/_$$Lambda$TextClassifierImpl$ftq_sQqJYwUdrdbbr9jz3p4AWos;
      ((_..Lambda.TextClassifierImpl.ftq_sQqJYwUdrdbbr9jz3p4AWos)localObject3).<init>(this);
      localObject2 = ActionsSuggestionsHelper.toNativeMessages((List)localObject2, (Function)localObject3);
      if (localObject2.length == 0) {
        return this.mFallback.suggestConversationActions(paramRequest);
      }
      localObject3 = new com/google/android/textclassifier/ActionsSuggestionsModel$Conversation;
      ((ActionsSuggestionsModel.Conversation)localObject3).<init>((ActionsSuggestionsModel.ConversationMessage[])localObject2);
      localObject1 = createConversationActionResult(paramRequest, ((ActionsSuggestionsModel)localObject1).suggestActionsWithIntents((ActionsSuggestionsModel.Conversation)localObject3, null, this.mContext, getResourceLocalesString(), getAnnotatorImpl(LocaleList.getDefault())));
      return (ConversationActions)localObject1;
    }
    finally
    {
      Log.e("androidtc", "Error suggesting conversation actions.", localThrowable);
    }
    return this.mFallback.suggestConversationActions(paramRequest);
  }
  
  public TextSelection suggestSelection(TextSelection.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    TextClassifier.Utils.checkMainThread();
    try
    {
      int i = paramRequest.getEndIndex();
      int j = paramRequest.getStartIndex();
      String str1 = paramRequest.getText().toString();
      if (str1.length() > 0) {
        if (i - j <= this.mSettings.getSuggestSelectionMaxRangeLength())
        {
          String str2 = concatenateLocales(paramRequest.getDefaultLocales());
          String str3 = detectLanguageTagsFromText(paramRequest.getText());
          Object localObject1 = ZonedDateTime.now();
          AnnotatorModel localAnnotatorModel = getAnnotatorImpl(paramRequest.getDefaultLocales());
          Object localObject2;
          if ((this.mSettings.isModelDarkLaunchEnabled()) && (!paramRequest.isDarkLaunchAllowed()))
          {
            i = paramRequest.getStartIndex();
            j = paramRequest.getEndIndex();
          }
          else
          {
            j = paramRequest.getStartIndex();
            i = paramRequest.getEndIndex();
            localObject2 = new com/google/android/textclassifier/AnnotatorModel$SelectionOptions;
            ((AnnotatorModel.SelectionOptions)localObject2).<init>(str2, str3);
            localObject2 = localAnnotatorModel.suggestSelection(str1, j, i, (AnnotatorModel.SelectionOptions)localObject2);
            i = localObject2[0];
            j = localObject2[1];
          }
          if ((i < j) && (i >= 0)) {
            if (j <= str1.length())
            {
              if ((i <= paramRequest.getStartIndex()) && (j >= paramRequest.getEndIndex()))
              {
                localObject2 = new android/view/textclassifier/TextSelection$Builder;
                ((TextSelection.Builder)localObject2).<init>(i, j);
                AnnotatorModel.ClassificationOptions localClassificationOptions = new com/google/android/textclassifier/AnnotatorModel$ClassificationOptions;
                localClassificationOptions.<init>(((ZonedDateTime)localObject1).toInstant().toEpochMilli(), ((ZonedDateTime)localObject1).getZone().getId(), str2, str3);
                localObject1 = localAnnotatorModel.classifyText(str1, i, j, localClassificationOptions, null, null);
                j = localObject1.length;
                for (i = 0; i < j; i++) {
                  ((TextSelection.Builder)localObject2).setEntityType(localObject1[i].getCollection(), localObject1[i].getScore());
                }
                return ((TextSelection.Builder)localObject2).setId(createId(str1, paramRequest.getStartIndex(), paramRequest.getEndIndex())).build();
              }
            }
            else {}
          }
          Log.d("androidtc", "Got bad indices for input text. Ignoring result.");
        }
        else {}
      }
    }
    finally
    {
      Log.e("androidtc", "Error suggesting selection for text. No changes to selection suggested.", localThrowable);
    }
    return this.mFallback.suggestSelection(paramRequest);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassifierImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */