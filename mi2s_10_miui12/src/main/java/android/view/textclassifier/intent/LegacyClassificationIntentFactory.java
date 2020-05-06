package android.view.textclassifier.intent;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.textclassifier.Log;
import com.google.android.textclassifier.AnnotatorModel.ClassificationResult;
import com.google.android.textclassifier.AnnotatorModel.DatetimeResult;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class LegacyClassificationIntentFactory
  implements ClassificationIntentFactory
{
  private static final long DEFAULT_EVENT_DURATION = TimeUnit.HOURS.toMillis(1L);
  private static final long MIN_EVENT_FUTURE_MILLIS = TimeUnit.MINUTES.toMillis(5L);
  private static final String TAG = "LegacyClassificationIntentFactory";
  
  private static LabeledIntent createCalendarCreateEventIntent(Context paramContext, Instant paramInstant, String paramString)
  {
    boolean bool = "date".equals(paramString);
    return new LabeledIntent(paramContext.getString(17039478), null, paramContext.getString(17039479), null, new Intent("android.intent.action.INSERT").setData(CalendarContract.Events.CONTENT_URI).putExtra("allDay", bool).putExtra("beginTime", paramInstant.toEpochMilli()).putExtra("endTime", paramInstant.toEpochMilli() + DEFAULT_EVENT_DURATION), paramInstant.hashCode());
  }
  
  private static LabeledIntent createCalendarViewIntent(Context paramContext, Instant paramInstant)
  {
    Uri.Builder localBuilder = CalendarContract.CONTENT_URI.buildUpon();
    localBuilder.appendPath("time");
    ContentUris.appendId(localBuilder, paramInstant.toEpochMilli());
    return new LabeledIntent(paramContext.getString(17041308), null, paramContext.getString(17041309), null, new Intent("android.intent.action.VIEW").setData(localBuilder.build()), 0);
  }
  
  private static List<LabeledIntent> createForAddress(Context paramContext, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      paramString = URLEncoder.encode(paramString, "UTF-8");
      LabeledIntent localLabeledIntent = new android/view/textclassifier/intent/LabeledIntent;
      String str = paramContext.getString(17040367);
      paramContext = paramContext.getString(17040368);
      Intent localIntent = new android/content/Intent;
      localIntent.<init>("android.intent.action.VIEW");
      localLabeledIntent.<init>(str, null, paramContext, null, localIntent.setData(Uri.parse(String.format("geo:0,0?q=%s", new Object[] { paramString }))), 0);
      localArrayList.add(localLabeledIntent);
    }
    catch (UnsupportedEncodingException paramContext)
    {
      Log.e("LegacyClassificationIntentFactory", "Could not encode address", paramContext);
    }
    return localArrayList;
  }
  
  private static List<LabeledIntent> createForDatetime(Context paramContext, String paramString, Instant paramInstant1, Instant paramInstant2)
  {
    Instant localInstant = paramInstant1;
    if (paramInstant1 == null) {
      localInstant = Instant.now();
    }
    paramInstant1 = new ArrayList();
    paramInstant1.add(createCalendarViewIntent(paramContext, paramInstant2));
    if (localInstant.until(paramInstant2, ChronoUnit.MILLIS) > MIN_EVENT_FUTURE_MILLIS) {
      paramInstant1.add(createCalendarCreateEventIntent(paramContext, paramInstant2, paramString));
    }
    return paramInstant1;
  }
  
  private static List<LabeledIntent> createForDictionary(Context paramContext, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new LabeledIntent(paramContext.getString(17039882), null, paramContext.getString(17039883), null, new Intent("android.intent.action.DEFINE").putExtra("android.intent.extra.TEXT", paramString), paramString.hashCode()));
    return localArrayList;
  }
  
  private static List<LabeledIntent> createForEmail(Context paramContext, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new LabeledIntent(paramContext.getString(17039939), null, paramContext.getString(17039945), null, new Intent("android.intent.action.SENDTO").setData(Uri.parse(String.format("mailto:%s", new Object[] { paramString }))), 0));
    localArrayList.add(new LabeledIntent(paramContext.getString(17039480), null, paramContext.getString(17039481), null, new Intent("android.intent.action.INSERT_OR_EDIT").setType("vnd.android.cursor.item/contact").putExtra("email", paramString), paramString.hashCode()));
    return localArrayList;
  }
  
  private static List<LabeledIntent> createForFlight(Context paramContext, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new LabeledIntent(paramContext.getString(17041310), null, paramContext.getString(17041311), null, new Intent("android.intent.action.WEB_SEARCH").putExtra("query", paramString), paramString.hashCode()));
    return localArrayList;
  }
  
  private static List<LabeledIntent> createForPhone(Context paramContext, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = (UserManager)paramContext.getSystemService(UserManager.class);
    if (localObject != null) {
      localObject = ((UserManager)localObject).getUserRestrictions();
    } else {
      localObject = new Bundle();
    }
    if (!((Bundle)localObject).getBoolean("no_outgoing_calls", false)) {
      localArrayList.add(new LabeledIntent(paramContext.getString(17039899), null, paramContext.getString(17039900), null, new Intent("android.intent.action.DIAL").setData(Uri.parse(String.format("tel:%s", new Object[] { paramString }))), 0));
    }
    localArrayList.add(new LabeledIntent(paramContext.getString(17039480), null, paramContext.getString(17039481), null, new Intent("android.intent.action.INSERT_OR_EDIT").setType("vnd.android.cursor.item/contact").putExtra("phone", paramString), paramString.hashCode()));
    if (!((Bundle)localObject).getBoolean("no_sms", false)) {
      localArrayList.add(new LabeledIntent(paramContext.getString(17041137), null, paramContext.getString(17041142), null, new Intent("android.intent.action.SENDTO").setData(Uri.parse(String.format("smsto:%s", new Object[] { paramString }))), 0));
    }
    return localArrayList;
  }
  
  private static List<LabeledIntent> createForUrl(Context paramContext, String paramString)
  {
    Object localObject = paramString;
    if (Uri.parse(paramString).getScheme() == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("http://");
      ((StringBuilder)localObject).append(paramString);
      localObject = ((StringBuilder)localObject).toString();
    }
    paramString = new ArrayList();
    paramString.add(new LabeledIntent(paramContext.getString(17039634), null, paramContext.getString(17039635), null, new Intent("android.intent.action.VIEW").setDataAndNormalize(Uri.parse((String)localObject)).putExtra("com.android.browser.application_id", paramContext.getPackageName()), 0));
    return paramString;
  }
  
  public List<LabeledIntent> create(Context paramContext, String paramString, boolean paramBoolean, Instant paramInstant, AnnotatorModel.ClassificationResult paramClassificationResult)
  {
    String str1;
    if (paramClassificationResult != null) {
      str1 = paramClassificationResult.getCollection().trim().toLowerCase(Locale.ENGLISH);
    } else {
      str1 = "";
    }
    String str2 = paramString.trim();
    int i = -1;
    switch (str1.hashCode())
    {
    }
    for (;;)
    {
      break;
      if (str1.equals("datetime"))
      {
        i = 5;
        break;
        if (str1.equals("dictionary"))
        {
          i = 7;
          break;
          if (str1.equals("phone"))
          {
            i = 1;
            break;
            if (str1.equals("email"))
            {
              i = 0;
              break;
              if (str1.equals("date"))
              {
                i = 4;
                break;
                if (str1.equals("url"))
                {
                  i = 3;
                  break;
                  if (str1.equals("address"))
                  {
                    i = 2;
                    break;
                    if (str1.equals("flight")) {
                      i = 6;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    switch (i)
    {
    default: 
      paramString = new ArrayList();
      break;
    case 7: 
      paramString = createForDictionary(paramContext, str2);
      break;
    case 6: 
      paramString = createForFlight(paramContext, str2);
      break;
    case 4: 
    case 5: 
      if (paramClassificationResult.getDatetimeResult() != null) {
        paramString = createForDatetime(paramContext, str1, paramInstant, Instant.ofEpochMilli(paramClassificationResult.getDatetimeResult().getTimeMsUtc()));
      } else {
        paramString = new ArrayList();
      }
      break;
    case 3: 
      paramString = createForUrl(paramContext, str2);
      break;
    case 2: 
      paramString = createForAddress(paramContext, str2);
      break;
    case 1: 
      paramString = createForPhone(paramContext, str2);
      break;
    case 0: 
      paramString = createForEmail(paramContext, str2);
    }
    if (paramBoolean) {
      ClassificationIntentFactory.insertTranslateAction(paramString, paramContext, str2);
    }
    return paramString;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/intent/LegacyClassificationIntentFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */