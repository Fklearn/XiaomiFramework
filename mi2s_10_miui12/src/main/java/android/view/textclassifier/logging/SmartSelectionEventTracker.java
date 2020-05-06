package android.view.textclassifier.logging;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.metrics.LogMaker;
import android.util.Log;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextSelection;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.UUID;

public final class SmartSelectionEventTracker
{
  private static final String CUSTOM_EDITTEXT = "customedit";
  private static final String CUSTOM_TEXTVIEW = "customview";
  private static final String CUSTOM_UNSELECTABLE_TEXTVIEW = "nosel-customview";
  private static final boolean DEBUG_LOG_ENABLED = true;
  private static final String EDITTEXT = "edittext";
  private static final String EDIT_WEBVIEW = "edit-webview";
  private static final int ENTITY_TYPE = 1254;
  private static final int EVENT_END = 1251;
  private static final int EVENT_START = 1250;
  private static final int INDEX = 1120;
  private static final String LOG_TAG = "SmartSelectEventTracker";
  private static final int MODEL_NAME = 1256;
  private static final int PREV_EVENT_DELTA = 1118;
  private static final int SESSION_ID = 1119;
  private static final int SMART_END = 1253;
  private static final int SMART_START = 1252;
  private static final int START_EVENT_DELTA = 1117;
  private static final String TEXTVIEW = "textview";
  private static final String UNKNOWN = "unknown";
  private static final String UNSELECTABLE_TEXTVIEW = "nosel-textview";
  private static final String WEBVIEW = "webview";
  private static final int WIDGET_TYPE = 1255;
  private static final int WIDGET_VERSION = 1262;
  private static final String ZERO = "0";
  private final Context mContext;
  private int mIndex;
  private long mLastEventTime;
  private final MetricsLogger mMetricsLogger = new MetricsLogger();
  private String mModelName;
  private int mOrigStart;
  private final int[] mPrevIndices = new int[2];
  private String mSessionId;
  private long mSessionStartTime;
  private final int[] mSmartIndices = new int[2];
  private boolean mSmartSelectionTriggered;
  private final int mWidgetType;
  private final String mWidgetVersion;
  
  @UnsupportedAppUsage
  public SmartSelectionEventTracker(Context paramContext, int paramInt)
  {
    this.mWidgetType = paramInt;
    this.mWidgetVersion = null;
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext));
  }
  
  public SmartSelectionEventTracker(Context paramContext, int paramInt, String paramString)
  {
    this.mWidgetType = paramInt;
    this.mWidgetVersion = paramString;
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext));
  }
  
  private static String createSessionId()
  {
    return UUID.randomUUID().toString();
  }
  
  private static void debugLog(LogMaker paramLogMaker)
  {
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
      Log.d("SmartSelectEventTracker", String.format("New selection session: %s (%s)", new Object[] { str1, str2.substring(str2.lastIndexOf("-") + 1) }));
    }
    str2 = Objects.toString(paramLogMaker.getTaggedData(1256), "unknown");
    String str3 = Objects.toString(paramLogMaker.getTaggedData(1254), "unknown");
    Object localObject = getLogTypeString(paramLogMaker.getType());
    int j = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1252), "0"));
    int k = Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1253), "0"));
    Log.d("SmartSelectEventTracker", String.format("%2d: %s/%s, range=%d,%d - smart_range=%d,%d (%s/%s)", new Object[] { Integer.valueOf(i), localObject, str3, Integer.valueOf(Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1250), "0"))), Integer.valueOf(Integer.parseInt(Objects.toString(paramLogMaker.getTaggedData(1251), "0"))), Integer.valueOf(j), Integer.valueOf(k), str1, str2 }));
  }
  
  private void endSession()
  {
    this.mOrigStart = 0;
    int[] arrayOfInt = this.mSmartIndices;
    arrayOfInt[1] = 0;
    arrayOfInt[0] = 0;
    arrayOfInt = this.mPrevIndices;
    arrayOfInt[1] = 0;
    arrayOfInt[0] = 0;
    this.mIndex = 0;
    this.mSessionStartTime = 0L;
    this.mLastEventTime = 0L;
    this.mSmartSelectionTriggered = false;
    this.mModelName = getModelName(null);
    this.mSessionId = null;
  }
  
  private static int getLogType(SelectionEvent paramSelectionEvent)
  {
    int i = paramSelectionEvent.mEventType;
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
  
  private String getModelName(SelectionEvent paramSelectionEvent)
  {
    String str = "";
    if (paramSelectionEvent == null) {
      paramSelectionEvent = str;
    } else {
      paramSelectionEvent = Objects.toString(paramSelectionEvent.mVersionTag, "");
    }
    return paramSelectionEvent;
  }
  
  private int getRangeDelta(int paramInt)
  {
    return paramInt - this.mOrigStart;
  }
  
  private int getSmartRangeDelta(int paramInt)
  {
    if (this.mSmartSelectionTriggered) {
      paramInt = getRangeDelta(paramInt);
    } else {
      paramInt = 0;
    }
    return paramInt;
  }
  
  private String getWidgetTypeName()
  {
    switch (this.mWidgetType)
    {
    default: 
      return "unknown";
    case 8: 
      return "nosel-customview";
    case 7: 
      return "customedit";
    case 6: 
      return "customview";
    case 5: 
      return "nosel-textview";
    case 4: 
      return "edit-webview";
    case 3: 
      return "edittext";
    case 2: 
      return "webview";
    }
    return "textview";
  }
  
  private String startNewSession()
  {
    endSession();
    this.mSessionId = createSessionId();
    return this.mSessionId;
  }
  
  private void writeEvent(SelectionEvent paramSelectionEvent, long paramLong)
  {
    long l1 = this.mLastEventTime;
    long l2 = 0L;
    if (l1 != 0L) {
      l2 = paramLong - l1;
    }
    LogMaker localLogMaker = new LogMaker(1100).setType(getLogType(paramSelectionEvent)).setSubtype(1).setPackageName(this.mContext.getPackageName()).addTaggedData(1117, Long.valueOf(paramLong - this.mSessionStartTime)).addTaggedData(1118, Long.valueOf(l2)).addTaggedData(1120, Integer.valueOf(this.mIndex)).addTaggedData(1255, getWidgetTypeName()).addTaggedData(1262, this.mWidgetVersion).addTaggedData(1256, this.mModelName).addTaggedData(1254, paramSelectionEvent.mEntityType).addTaggedData(1252, Integer.valueOf(getSmartRangeDelta(this.mSmartIndices[0]))).addTaggedData(1253, Integer.valueOf(getSmartRangeDelta(this.mSmartIndices[1]))).addTaggedData(1250, Integer.valueOf(getRangeDelta(paramSelectionEvent.mStart))).addTaggedData(1251, Integer.valueOf(getRangeDelta(paramSelectionEvent.mEnd))).addTaggedData(1119, this.mSessionId);
    this.mMetricsLogger.write(localLogMaker);
    debugLog(localLogMaker);
    this.mLastEventTime = paramLong;
    this.mPrevIndices[0] = paramSelectionEvent.mStart;
    this.mPrevIndices[1] = paramSelectionEvent.mEnd;
    this.mIndex += 1;
  }
  
  @UnsupportedAppUsage
  public void logEvent(SelectionEvent paramSelectionEvent)
  {
    Preconditions.checkNotNull(paramSelectionEvent);
    int i = paramSelectionEvent.mEventType;
    boolean bool = true;
    if ((i != 1) && (this.mSessionId == null))
    {
      Log.d("SmartSelectEventTracker", "Selection session not yet started. Ignoring event");
      return;
    }
    long l = System.currentTimeMillis();
    i = paramSelectionEvent.mEventType;
    if (i != 1)
    {
      if (i != 2) {
        if ((i != 3) && (i != 4))
        {
          if (i != 5) {
            break label180;
          }
        }
        else
        {
          this.mSmartSelectionTriggered = true;
          this.mModelName = getModelName(paramSelectionEvent);
          this.mSmartIndices[0] = paramSelectionEvent.mStart;
          this.mSmartIndices[1] = paramSelectionEvent.mEnd;
          break label180;
        }
      }
      if ((this.mPrevIndices[0] != paramSelectionEvent.mStart) || (this.mPrevIndices[1] != paramSelectionEvent.mEnd)) {}
    }
    else
    {
      this.mSessionId = startNewSession();
      if (paramSelectionEvent.mEnd != paramSelectionEvent.mStart + 1) {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      this.mOrigStart = paramSelectionEvent.mStart;
      this.mSessionStartTime = l;
    }
    label180:
    writeEvent(paramSelectionEvent, l);
    if (paramSelectionEvent.isTerminal()) {
      endSession();
    }
  }
  
  public static final class SelectionEvent
  {
    private static final String NO_VERSION_TAG = "";
    public static final int OUT_OF_BOUNDS = Integer.MAX_VALUE;
    public static final int OUT_OF_BOUNDS_NEGATIVE = Integer.MIN_VALUE;
    private final int mEnd;
    private final String mEntityType;
    private int mEventType;
    private final int mStart;
    private final String mVersionTag;
    
    private SelectionEvent(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2)
    {
      boolean bool;
      if (paramInt2 >= paramInt1) {
        bool = true;
      } else {
        bool = false;
      }
      Preconditions.checkArgument(bool, "end cannot be less than start");
      this.mStart = paramInt1;
      this.mEnd = paramInt2;
      this.mEventType = paramInt3;
      this.mEntityType = ((String)Preconditions.checkNotNull(paramString1));
      this.mVersionTag = ((String)Preconditions.checkNotNull(paramString2));
    }
    
    private static String getSourceClassifier(String paramString)
    {
      int i = paramString.indexOf("|");
      if (i >= 0) {
        return paramString.substring(0, i);
      }
      return "";
    }
    
    private static String getVersionInfo(String paramString)
    {
      int i = paramString.indexOf("|");
      int j = paramString.indexOf("|", i);
      if ((i >= 0) && (j >= i)) {
        return paramString.substring(i, j);
      }
      return "";
    }
    
    private boolean isTerminal()
    {
      switch (this.mEventType)
      {
      default: 
        return false;
      }
      return true;
    }
    
    @UnsupportedAppUsage
    public static SelectionEvent selectionAction(int paramInt1, int paramInt2, int paramInt3)
    {
      return new SelectionEvent(paramInt1, paramInt2, paramInt3, "", "");
    }
    
    @UnsupportedAppUsage
    public static SelectionEvent selectionAction(int paramInt1, int paramInt2, int paramInt3, TextClassification paramTextClassification)
    {
      String str;
      if (paramTextClassification.getEntityCount() > 0) {
        str = paramTextClassification.getEntity(0);
      } else {
        str = "";
      }
      return new SelectionEvent(paramInt1, paramInt2, paramInt3, str, getVersionInfo(paramTextClassification.getId()));
    }
    
    @UnsupportedAppUsage
    public static SelectionEvent selectionModified(int paramInt1, int paramInt2)
    {
      return new SelectionEvent(paramInt1, paramInt2, 2, "", "");
    }
    
    @UnsupportedAppUsage
    public static SelectionEvent selectionModified(int paramInt1, int paramInt2, TextClassification paramTextClassification)
    {
      String str;
      if (paramTextClassification.getEntityCount() > 0) {
        str = paramTextClassification.getEntity(0);
      } else {
        str = "";
      }
      return new SelectionEvent(paramInt1, paramInt2, 2, str, getVersionInfo(paramTextClassification.getId()));
    }
    
    @UnsupportedAppUsage
    public static SelectionEvent selectionModified(int paramInt1, int paramInt2, TextSelection paramTextSelection)
    {
      int i;
      if (getSourceClassifier(paramTextSelection.getId()).equals("androidtc"))
      {
        if (paramInt2 - paramInt1 > 1) {
          i = 4;
        } else {
          i = 3;
        }
      }
      else {
        i = 5;
      }
      String str;
      if (paramTextSelection.getEntityCount() > 0) {
        str = paramTextSelection.getEntity(0);
      } else {
        str = "";
      }
      return new SelectionEvent(paramInt1, paramInt2, i, str, getVersionInfo(paramTextSelection.getId()));
    }
    
    @UnsupportedAppUsage
    public static SelectionEvent selectionStarted(int paramInt)
    {
      return new SelectionEvent(paramInt, paramInt + 1, 1, "", "");
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface ActionType
    {
      public static final int ABANDON = 107;
      public static final int COPY = 101;
      public static final int CUT = 103;
      public static final int DRAG = 106;
      public static final int OTHER = 108;
      public static final int OVERTYPE = 100;
      public static final int PASTE = 102;
      public static final int RESET = 201;
      public static final int SELECT_ALL = 200;
      public static final int SHARE = 104;
      public static final int SMART_SHARE = 105;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private static @interface EventType
    {
      public static final int AUTO_SELECTION = 5;
      public static final int SELECTION_MODIFIED = 2;
      public static final int SELECTION_STARTED = 1;
      public static final int SMART_SELECTION_MULTI = 4;
      public static final int SMART_SELECTION_SINGLE = 3;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface WidgetType
  {
    public static final int CUSTOM_EDITTEXT = 7;
    public static final int CUSTOM_TEXTVIEW = 6;
    public static final int CUSTOM_UNSELECTABLE_TEXTVIEW = 8;
    public static final int EDITTEXT = 3;
    public static final int EDIT_WEBVIEW = 4;
    public static final int TEXTVIEW = 1;
    public static final int UNSELECTABLE_TEXTVIEW = 5;
    public static final int UNSPECIFIED = 0;
    public static final int WEBVIEW = 2;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/logging/SmartSelectionEventTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */