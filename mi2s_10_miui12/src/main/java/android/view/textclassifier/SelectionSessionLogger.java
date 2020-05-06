package android.view.textclassifier;

import android.content.Context;
import android.metrics.LogMaker;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.Preconditions;
import java.text.BreakIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

public final class SelectionSessionLogger
{
  static final String CLASSIFIER_ID = "androidtc";
  private static final int ENTITY_TYPE = 1254;
  private static final int EVENT_END = 1251;
  private static final int EVENT_START = 1250;
  private static final int INDEX = 1120;
  private static final String LOG_TAG = "SelectionSessionLogger";
  private static final int MODEL_NAME = 1256;
  private static final int PREV_EVENT_DELTA = 1118;
  private static final int SESSION_ID = 1119;
  private static final int SMART_END = 1253;
  private static final int SMART_START = 1252;
  private static final int START_EVENT_DELTA = 1117;
  private static final String UNKNOWN = "unknown";
  private static final int WIDGET_TYPE = 1255;
  private static final int WIDGET_VERSION = 1262;
  private static final String ZERO = "0";
  private final MetricsLogger mMetricsLogger;
  
  public SelectionSessionLogger()
  {
    this.mMetricsLogger = new MetricsLogger();
  }
  
  @VisibleForTesting
  public SelectionSessionLogger(MetricsLogger paramMetricsLogger)
  {
    this.mMetricsLogger = ((MetricsLogger)Preconditions.checkNotNull(paramMetricsLogger));
  }
  
  public static String createId(String paramString, int paramInt1, int paramInt2, Context paramContext, int paramInt3, List<Locale> paramList)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkNotNull(paramContext);
    Preconditions.checkNotNull(paramList);
    StringJoiner localStringJoiner = new StringJoiner(",");
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localStringJoiner.add(((Locale)paramList.next()).toLanguageTag());
    }
    return SignatureParser.createSignature("androidtc", String.format(Locale.US, "%s_v%d", new Object[] { localStringJoiner.toString(), Integer.valueOf(paramInt3) }), Objects.hash(new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramContext.getPackageName() }));
  }
  
  private static void debugLog(LogMaker paramLogMaker)
  {
    if (!Log.ENABLE_FULL_LOGGING) {
      return;
    }
    String str1 = Objects.toString(paramLogMaker.getTaggedData(1255), "unknown");
    String str2 = Objects.toString(paramLogMaker.getTaggedData(1262), "");
    if (!str2.isEmpty())
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(str1);
      ((StringBuilder)localObject).append("-");
      ((StringBuilder)localObject).append(str2);
      str1 = ((StringBuilder)localObject).toString();
    }
    int i = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1120), "0"));
    if (paramLogMaker.getType() == 1101)
    {
      str2 = Objects.toString(paramLogMaker.getTaggedData(1119), "");
      Log.d("SelectionSessionLogger", String.format("New selection session: %s (%s)", new Object[] { str1, str2.substring(str2.lastIndexOf("-") + 1) }));
    }
    String str3 = Objects.toString(paramLogMaker.getTaggedData(1256), "unknown");
    Object localObject = Objects.toString(paramLogMaker.getTaggedData(1254), "unknown");
    String str4 = getLogTypeString(paramLogMaker.getType());
    str2 = getLogSubTypeString(paramLogMaker.getSubtype());
    int j = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1252), "0"));
    int k = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1253), "0"));
    int m = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1250), "0"));
    int n = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1251), "0"));
    Log.v("SelectionSessionLogger", String.format(Locale.US, "%2d: %s/%s/%s, range=%d,%d - smart_range=%d,%d (%s/%s)", new Object[] { Integer.valueOf(i), str4, str2, localObject, Integer.valueOf(m), Integer.valueOf(n), Integer.valueOf(j), Integer.valueOf(k), str1, str3 }));
  }
  
  private static int getLogSubType(SelectionEvent paramSelectionEvent)
  {
    int i = paramSelectionEvent.getInvocationMethod();
    if (i != 1)
    {
      if (i != 2) {
        return 0;
      }
      return 2;
    }
    return 1;
  }
  
  private static String getLogSubTypeString(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2) {
        return "unknown";
      }
      return "LINK";
    }
    return "MANUAL";
  }
  
  private static int getLogType(SelectionEvent paramSelectionEvent)
  {
    int i = paramSelectionEvent.getEventType();
    if (i != 1)
    {
      if (i != 2)
      {
        if (i != 3)
        {
          if (i != 4)
          {
            if (i != 5)
            {
              if (i != 200)
              {
                if (i != 201)
                {
                  switch (i)
                  {
                  default: 
                    return 0;
                  case 108: 
                    return 1116;
                  case 107: 
                    return 1115;
                  case 106: 
                    return 1114;
                  case 105: 
                    return 1113;
                  case 104: 
                    return 1112;
                  case 103: 
                    return 1111;
                  case 102: 
                    return 1110;
                  case 101: 
                    return 1109;
                  }
                  return 1108;
                }
                return 1104;
              }
              return 1103;
            }
            return 1107;
          }
          return 1106;
        }
        return 1105;
      }
      return 1102;
    }
    return 1101;
  }
  
  private static String getLogTypeString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 1116: 
      return "OTHER";
    case 1115: 
      return "ABANDON";
    case 1114: 
      return "DRAG";
    case 1113: 
      return "SMART_SHARE";
    case 1112: 
      return "SHARE";
    case 1111: 
      return "CUT";
    case 1110: 
      return "PASTE";
    case 1109: 
      return "COPY";
    case 1108: 
      return "OVERTYPE";
    case 1107: 
      return "AUTO_SELECTION";
    case 1106: 
      return "SMART_SELECTION_MULTI";
    case 1105: 
      return "SMART_SELECTION_SINGLE";
    case 1104: 
      return "RESET";
    case 1103: 
      return "SELECT_ALL";
    case 1102: 
      return "SELECTION_MODIFIED";
    }
    return "SELECTION_STARTED";
  }
  
  public static BreakIterator getTokenIterator(Locale paramLocale)
  {
    return BreakIterator.getWordInstance((Locale)Preconditions.checkNotNull(paramLocale));
  }
  
  static boolean isPlatformLocalTextClassifierSmartSelection(String paramString)
  {
    return "androidtc".equals(SignatureParser.getClassifierId(paramString));
  }
  
  public void writeEvent(SelectionEvent paramSelectionEvent)
  {
    Preconditions.checkNotNull(paramSelectionEvent);
    LogMaker localLogMaker = new LogMaker(1100).setType(getLogType(paramSelectionEvent)).setSubtype(getLogSubType(paramSelectionEvent)).setPackageName(paramSelectionEvent.getPackageName()).addTaggedData(1117, Long.valueOf(paramSelectionEvent.getDurationSinceSessionStart())).addTaggedData(1118, Long.valueOf(paramSelectionEvent.getDurationSincePreviousEvent())).addTaggedData(1120, Integer.valueOf(paramSelectionEvent.getEventIndex())).addTaggedData(1255, paramSelectionEvent.getWidgetType()).addTaggedData(1262, paramSelectionEvent.getWidgetVersion()).addTaggedData(1254, paramSelectionEvent.getEntityType()).addTaggedData(1250, Integer.valueOf(paramSelectionEvent.getStart())).addTaggedData(1251, Integer.valueOf(paramSelectionEvent.getEnd()));
    if (isPlatformLocalTextClassifierSmartSelection(paramSelectionEvent.getResultId())) {
      localLogMaker.addTaggedData(1256, SignatureParser.getModelName(paramSelectionEvent.getResultId())).addTaggedData(1252, Integer.valueOf(paramSelectionEvent.getSmartStart())).addTaggedData(1253, Integer.valueOf(paramSelectionEvent.getSmartEnd()));
    }
    if (paramSelectionEvent.getSessionId() != null) {
      localLogMaker.addTaggedData(1119, paramSelectionEvent.getSessionId().flattenToString());
    }
    this.mMetricsLogger.write(localLogMaker);
    debugLog(localLogMaker);
  }
  
  @VisibleForTesting
  public static final class SignatureParser
  {
    static String createSignature(String paramString1, String paramString2, int paramInt)
    {
      return String.format(Locale.US, "%s|%s|%d", new Object[] { paramString1, paramString2, Integer.valueOf(paramInt) });
    }
    
    static String getClassifierId(String paramString)
    {
      if (paramString == null) {
        return "";
      }
      int i = paramString.indexOf("|");
      if (i >= 0) {
        return paramString.substring(0, i);
      }
      return "";
    }
    
    static int getHash(String paramString)
    {
      if (paramString == null) {
        return 0;
      }
      int i = paramString.indexOf("|", paramString.indexOf("|"));
      if (i > 0) {
        return Integer.parseInt(paramString.substring(i));
      }
      return 0;
    }
    
    static String getModelName(String paramString)
    {
      if (paramString == null) {
        return "";
      }
      int i = paramString.indexOf("|") + 1;
      int j = paramString.indexOf("|", i);
      if ((i >= 1) && (j >= i)) {
        return paramString.substring(i, j);
      }
      return "";
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/SelectionSessionLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */