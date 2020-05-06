package android.view.textclassifier;

import android.app.Person;
import android.app.RemoteAction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Pair;
import android.view.textclassifier.intent.LabeledIntent;
import android.view.textclassifier.intent.LabeledIntent.Result;
import android.view.textclassifier.intent.LabeledIntent.TitleChooser;
import android.view.textclassifier.intent.TemplateIntentFactory;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.google.android.textclassifier.ActionsSuggestionsModel.ActionSuggestion;
import com.google.android.textclassifier.ActionsSuggestionsModel.ConversationMessage;
import com.google.android.textclassifier.RemoteActionTemplate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class ActionsSuggestionsHelper
{
  private static final int FIRST_NON_LOCAL_USER = 1;
  private static final String TAG = "ActionsSuggestions";
  private static final int USER_LOCAL = 0;
  
  public static LabeledIntent.Result createLabeledIntentResult(Context paramContext, TemplateIntentFactory paramTemplateIntentFactory, ActionsSuggestionsModel.ActionSuggestion paramActionSuggestion)
  {
    RemoteActionTemplate[] arrayOfRemoteActionTemplate = paramActionSuggestion.getRemoteActionTemplates();
    if (arrayOfRemoteActionTemplate == null)
    {
      paramContext = new StringBuilder();
      paramContext.append("createRemoteAction: Missing template for type ");
      paramContext.append(paramActionSuggestion.getActionType());
      Log.w("ActionsSuggestions", paramContext.toString());
      return null;
    }
    paramTemplateIntentFactory = paramTemplateIntentFactory.create(arrayOfRemoteActionTemplate);
    if (paramTemplateIntentFactory.isEmpty()) {
      return null;
    }
    paramActionSuggestion = createTitleChooser(paramActionSuggestion.getActionType());
    return ((LabeledIntent)paramTemplateIntentFactory.get(0)).resolve(paramContext, paramActionSuggestion, null);
  }
  
  public static String createResultId(Context paramContext, List<ConversationActions.Message> paramList, int paramInt, List<Locale> paramList1)
  {
    StringJoiner localStringJoiner = new StringJoiner(",");
    paramList1 = paramList1.iterator();
    while (paramList1.hasNext()) {
      localStringJoiner.add(((Locale)paramList1.next()).toLanguageTag());
    }
    return SelectionSessionLogger.SignatureParser.createSignature("androidtc", String.format(Locale.US, "%s_v%d", new Object[] { localStringJoiner.toString(), Integer.valueOf(paramInt) }), Objects.hash(new Object[] { paramList.stream().mapToInt(_..Lambda.ActionsSuggestionsHelper.YTQv8oPvlmJL4tITUFD4z4JWKRk.INSTANCE), paramContext.getPackageName(), Long.valueOf(System.currentTimeMillis()) }));
  }
  
  public static LabeledIntent.TitleChooser createTitleChooser(String paramString)
  {
    if ("open_url".equals(paramString)) {
      return _..Lambda.ActionsSuggestionsHelper.sY0w9od2zcl4YFel0lG4VB3vf7I.INSTANCE;
    }
    return null;
  }
  
  private static Pair<String, String> getRepresentation(ConversationAction paramConversationAction)
  {
    Object localObject = paramConversationAction.getAction();
    String str = null;
    if (localObject == null) {
      return null;
    }
    localObject = ExtrasUtils.getActionIntent(paramConversationAction.getExtras()).getComponent();
    if (localObject != null) {
      str = ((ComponentName)localObject).getPackageName();
    }
    return new Pair(paramConversationAction.getAction().getTitle().toString(), str);
  }
  
  private static int hashMessage(ConversationActions.Message paramMessage)
  {
    return Objects.hash(new Object[] { paramMessage.getAuthor(), paramMessage.getText(), paramMessage.getReferenceTime() });
  }
  
  public static List<ConversationAction> removeActionsWithDuplicates(List<ConversationAction> paramList)
  {
    ArrayMap localArrayMap = new ArrayMap();
    Object localObject1 = paramList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = getRepresentation((ConversationAction)((Iterator)localObject1).next());
      if (localObject2 != null) {
        localArrayMap.put(localObject2, Integer.valueOf(((Integer)localArrayMap.getOrDefault(localObject2, Integer.valueOf(0))).intValue() + 1));
      }
    }
    localObject1 = new ArrayList();
    Object localObject2 = paramList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      ConversationAction localConversationAction = (ConversationAction)((Iterator)localObject2).next();
      paramList = getRepresentation(localConversationAction);
      if ((paramList == null) || (((Integer)localArrayMap.getOrDefault(paramList, Integer.valueOf(0))).intValue() == 1)) {
        ((List)localObject1).add(localConversationAction);
      }
    }
    return (List<ConversationAction>)localObject1;
  }
  
  public static ActionsSuggestionsModel.ConversationMessage[] toNativeMessages(List<ConversationActions.Message> paramList, Function<CharSequence, String> paramFunction)
  {
    List localList = (List)paramList.stream().filter(_..Lambda.ActionsSuggestionsHelper.6oTtcn9bDE_u_8FbiyGdntqoQG0.INSTANCE).collect(Collectors.toCollection(_..Lambda.OGSS2qx6njxlnp0dnKb4lA3jnw8.INSTANCE));
    if (localList.isEmpty()) {
      return new ActionsSuggestionsModel.ConversationMessage[0];
    }
    ArrayDeque localArrayDeque = new ArrayDeque();
    PersonEncoder localPersonEncoder = new PersonEncoder(null);
    for (int i = localList.size() - 1; i >= 0; i--)
    {
      ConversationActions.Message localMessage = (ConversationActions.Message)localList.get(i);
      long l;
      if (localMessage.getReferenceTime() == null) {
        l = 0L;
      } else {
        l = localMessage.getReferenceTime().toInstant().toEpochMilli();
      }
      if (localMessage.getReferenceTime() == null) {
        paramList = null;
      } else {
        paramList = localMessage.getReferenceTime().getZone().getId();
      }
      localArrayDeque.push(new ActionsSuggestionsModel.ConversationMessage(localPersonEncoder.encode(localMessage.getAuthor()), localMessage.getText().toString(), l, paramList, (String)paramFunction.apply(localMessage.getText())));
    }
    return (ActionsSuggestionsModel.ConversationMessage[])localArrayDeque.toArray(new ActionsSuggestionsModel.ConversationMessage[localArrayDeque.size()]);
  }
  
  private static final class PersonEncoder
  {
    private final Map<Person, Integer> mMapping = new ArrayMap();
    private int mNextUserId = 1;
    
    private int encode(Person paramPerson)
    {
      if (ConversationActions.Message.PERSON_USER_SELF.equals(paramPerson)) {
        return 0;
      }
      Integer localInteger1 = (Integer)this.mMapping.get(paramPerson);
      Integer localInteger2 = localInteger1;
      if (localInteger1 == null)
      {
        this.mMapping.put(paramPerson, Integer.valueOf(this.mNextUserId));
        localInteger2 = Integer.valueOf(this.mNextUserId);
        this.mNextUserId += 1;
      }
      return localInteger2.intValue();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/ActionsSuggestionsHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */