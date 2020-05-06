package android.view.textclassifier;

import android.metrics.LogMaker;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.Preconditions;

public final class TextClassifierEventTronLogger
{
  private static final String TAG = "TCEventTronLogger";
  private final MetricsLogger mMetricsLogger;
  
  public TextClassifierEventTronLogger()
  {
    this(new MetricsLogger());
  }
  
  @VisibleForTesting
  public TextClassifierEventTronLogger(MetricsLogger paramMetricsLogger)
  {
    this.mMetricsLogger = ((MetricsLogger)Preconditions.checkNotNull(paramMetricsLogger));
  }
  
  private void debugLog(LogMaker paramLogMaker)
  {
    if (!Log.ENABLE_FULL_LOGGING) {
      return;
    }
    String str1 = String.valueOf(paramLogMaker.getTaggedData(1634));
    String str2 = toCategoryName(paramLogMaker.getCategory());
    String str3 = toEventName(paramLogMaker.getSubtype());
    String str4 = String.valueOf(paramLogMaker.getTaggedData(1639));
    String str5 = String.valueOf(paramLogMaker.getTaggedData(1640));
    String str6 = String.valueOf(paramLogMaker.getTaggedData(1256));
    String str7 = String.valueOf(paramLogMaker.getTaggedData(1635));
    String str8 = String.valueOf(paramLogMaker.getTaggedData(1636));
    String str9 = String.valueOf(paramLogMaker.getTaggedData(1637));
    paramLogMaker = String.valueOf(paramLogMaker.getTaggedData(1638));
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("writeEvent: ");
    localStringBuilder.append("id=");
    localStringBuilder.append(str1);
    localStringBuilder.append(", category=");
    localStringBuilder.append(str2);
    localStringBuilder.append(", eventName=");
    localStringBuilder.append(str3);
    localStringBuilder.append(", widgetType=");
    localStringBuilder.append(str4);
    localStringBuilder.append(", widgetVersion=");
    localStringBuilder.append(str5);
    localStringBuilder.append(", model=");
    localStringBuilder.append(str6);
    localStringBuilder.append(", firstEntityType=");
    localStringBuilder.append(str7);
    localStringBuilder.append(", secondEntityType=");
    localStringBuilder.append(str8);
    localStringBuilder.append(", thirdEntityType=");
    localStringBuilder.append(str9);
    localStringBuilder.append(", score=");
    localStringBuilder.append(paramLogMaker);
    Log.v("TCEventTronLogger", localStringBuilder.toString());
  }
  
  private static int getCategory(TextClassifierEvent paramTextClassifierEvent)
  {
    int i = paramTextClassifierEvent.getEventCategory();
    if (i != 3)
    {
      if (i != 4) {
        return -1;
      }
      return 1614;
    }
    return 1615;
  }
  
  private static int getLogType(TextClassifierEvent paramTextClassifierEvent)
  {
    int i = paramTextClassifierEvent.getEventType();
    if (i != 6)
    {
      if (i != 13)
      {
        if (i != 19)
        {
          if (i != 20) {
            return 0;
          }
          return 1619;
        }
        return 1618;
      }
      return 1113;
    }
    return 1616;
  }
  
  private static String getModelName(TextClassifierEvent paramTextClassifierEvent)
  {
    if (paramTextClassifierEvent.getModelName() != null) {
      return paramTextClassifierEvent.getModelName();
    }
    return SelectionSessionLogger.SignatureParser.getModelName(paramTextClassifierEvent.getResultId());
  }
  
  private String toCategoryName(int paramInt)
  {
    if (paramInt != 1614)
    {
      if (paramInt != 1615) {
        return "unknown";
      }
      return "conversation_actions";
    }
    return "language_detection";
  }
  
  private String toEventName(int paramInt)
  {
    if (paramInt != 1113)
    {
      if (paramInt != 1616)
      {
        if (paramInt != 1618)
        {
          if (paramInt != 1619) {
            return "unknown";
          }
          return "actions_generated";
        }
        return "manual_reply";
      }
      return "actions_shown";
    }
    return "smart_share";
  }
  
  public void writeEvent(TextClassifierEvent paramTextClassifierEvent)
  {
    Preconditions.checkNotNull(paramTextClassifierEvent);
    int i = getCategory(paramTextClassifierEvent);
    if (i == -1)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Unknown category: ");
      ((StringBuilder)localObject).append(paramTextClassifierEvent.getEventCategory());
      Log.w("TCEventTronLogger", ((StringBuilder)localObject).toString());
      return;
    }
    Object localObject = new LogMaker(i).setSubtype(getLogType(paramTextClassifierEvent)).addTaggedData(1634, paramTextClassifierEvent.getResultId()).addTaggedData(1256, getModelName(paramTextClassifierEvent));
    if (paramTextClassifierEvent.getScores().length >= 1) {
      ((LogMaker)localObject).addTaggedData(1638, Float.valueOf(paramTextClassifierEvent.getScores()[0]));
    }
    String[] arrayOfString = paramTextClassifierEvent.getEntityTypes();
    if (arrayOfString.length >= 1) {
      ((LogMaker)localObject).addTaggedData(1635, arrayOfString[0]);
    }
    if (arrayOfString.length >= 2) {
      ((LogMaker)localObject).addTaggedData(1636, arrayOfString[1]);
    }
    if (arrayOfString.length >= 3) {
      ((LogMaker)localObject).addTaggedData(1637, arrayOfString[2]);
    }
    paramTextClassifierEvent = paramTextClassifierEvent.getEventContext();
    if (paramTextClassifierEvent != null)
    {
      ((LogMaker)localObject).addTaggedData(1639, paramTextClassifierEvent.getWidgetType());
      ((LogMaker)localObject).addTaggedData(1640, paramTextClassifierEvent.getWidgetVersion());
      ((LogMaker)localObject).setPackageName(paramTextClassifierEvent.getPackageName());
    }
    this.mMetricsLogger.write((LogMaker)localObject);
    debugLog((LogMaker)localObject);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassifierEventTronLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */