package android.view.textclassifier;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import java.util.Objects;

public final class SelectionEvent
  implements Parcelable
{
  public static final int ACTION_ABANDON = 107;
  public static final int ACTION_COPY = 101;
  public static final int ACTION_CUT = 103;
  public static final int ACTION_DRAG = 106;
  public static final int ACTION_OTHER = 108;
  public static final int ACTION_OVERTYPE = 100;
  public static final int ACTION_PASTE = 102;
  public static final int ACTION_RESET = 201;
  public static final int ACTION_SELECT_ALL = 200;
  public static final int ACTION_SHARE = 104;
  public static final int ACTION_SMART_SHARE = 105;
  public static final Parcelable.Creator<SelectionEvent> CREATOR = new Parcelable.Creator()
  {
    public SelectionEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SelectionEvent(paramAnonymousParcel, null);
    }
    
    public SelectionEvent[] newArray(int paramAnonymousInt)
    {
      return new SelectionEvent[paramAnonymousInt];
    }
  };
  public static final int EVENT_AUTO_SELECTION = 5;
  public static final int EVENT_SELECTION_MODIFIED = 2;
  public static final int EVENT_SELECTION_STARTED = 1;
  public static final int EVENT_SMART_SELECTION_MULTI = 4;
  public static final int EVENT_SMART_SELECTION_SINGLE = 3;
  public static final int INVOCATION_LINK = 2;
  public static final int INVOCATION_MANUAL = 1;
  public static final int INVOCATION_UNKNOWN = 0;
  static final String NO_SIGNATURE = "";
  private final int mAbsoluteEnd;
  private final int mAbsoluteStart;
  private long mDurationSincePreviousEvent;
  private long mDurationSinceSessionStart;
  private int mEnd;
  private final String mEntityType;
  private int mEventIndex;
  private long mEventTime;
  private int mEventType;
  private int mInvocationMethod;
  private String mPackageName = "";
  private String mResultId;
  private TextClassificationSessionId mSessionId;
  private int mSmartEnd;
  private int mSmartStart;
  private int mStart;
  private int mUserId = 55536;
  private String mWidgetType = "unknown";
  private String mWidgetVersion;
  
  SelectionEvent(int paramInt1, int paramInt2, int paramInt3, String paramString1, int paramInt4, String paramString2)
  {
    boolean bool;
    if (paramInt2 >= paramInt1) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "end cannot be less than start");
    this.mAbsoluteStart = paramInt1;
    this.mAbsoluteEnd = paramInt2;
    this.mEventType = paramInt3;
    this.mEntityType = ((String)Preconditions.checkNotNull(paramString1));
    this.mResultId = paramString2;
    this.mInvocationMethod = paramInt4;
  }
  
  private SelectionEvent(Parcel paramParcel)
  {
    this.mAbsoluteStart = paramParcel.readInt();
    this.mAbsoluteEnd = paramParcel.readInt();
    this.mEventType = paramParcel.readInt();
    this.mEntityType = paramParcel.readString();
    int i = paramParcel.readInt();
    Object localObject1 = null;
    Object localObject2;
    if (i > 0) {
      localObject2 = paramParcel.readString();
    } else {
      localObject2 = null;
    }
    this.mWidgetVersion = ((String)localObject2);
    this.mPackageName = paramParcel.readString();
    this.mWidgetType = paramParcel.readString();
    this.mInvocationMethod = paramParcel.readInt();
    this.mResultId = paramParcel.readString();
    this.mEventTime = paramParcel.readLong();
    this.mDurationSinceSessionStart = paramParcel.readLong();
    this.mDurationSincePreviousEvent = paramParcel.readLong();
    this.mEventIndex = paramParcel.readInt();
    if (paramParcel.readInt() > 0) {
      localObject2 = (TextClassificationSessionId)TextClassificationSessionId.CREATOR.createFromParcel(paramParcel);
    } else {
      localObject2 = localObject1;
    }
    this.mSessionId = ((TextClassificationSessionId)localObject2);
    this.mStart = paramParcel.readInt();
    this.mEnd = paramParcel.readInt();
    this.mSmartStart = paramParcel.readInt();
    this.mSmartEnd = paramParcel.readInt();
    this.mUserId = paramParcel.readInt();
  }
  
  private static void checkActionType(int paramInt)
    throws IllegalArgumentException
  {
    if ((paramInt != 200) && (paramInt != 201)) {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException(String.format(Locale.US, "%d is not an eventType", new Object[] { Integer.valueOf(paramInt) }));
      }
    }
  }
  
  public static SelectionEvent createSelectionActionEvent(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool;
    if (paramInt2 >= paramInt1) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "end cannot be less than start");
    checkActionType(paramInt3);
    return new SelectionEvent(paramInt1, paramInt2, paramInt3, "", 0, "");
  }
  
  public static SelectionEvent createSelectionActionEvent(int paramInt1, int paramInt2, int paramInt3, TextClassification paramTextClassification)
  {
    boolean bool;
    if (paramInt2 >= paramInt1) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "end cannot be less than start");
    Preconditions.checkNotNull(paramTextClassification);
    checkActionType(paramInt3);
    String str;
    if (paramTextClassification.getEntityCount() > 0) {
      str = paramTextClassification.getEntity(0);
    } else {
      str = "";
    }
    return new SelectionEvent(paramInt1, paramInt2, paramInt3, str, 0, paramTextClassification.getId());
  }
  
  public static SelectionEvent createSelectionModifiedEvent(int paramInt1, int paramInt2)
  {
    boolean bool;
    if (paramInt2 >= paramInt1) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "end cannot be less than start");
    return new SelectionEvent(paramInt1, paramInt2, 2, "", 0, "");
  }
  
  public static SelectionEvent createSelectionModifiedEvent(int paramInt1, int paramInt2, TextClassification paramTextClassification)
  {
    boolean bool;
    if (paramInt2 >= paramInt1) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "end cannot be less than start");
    Preconditions.checkNotNull(paramTextClassification);
    String str;
    if (paramTextClassification.getEntityCount() > 0) {
      str = paramTextClassification.getEntity(0);
    } else {
      str = "";
    }
    return new SelectionEvent(paramInt1, paramInt2, 2, str, 0, paramTextClassification.getId());
  }
  
  public static SelectionEvent createSelectionModifiedEvent(int paramInt1, int paramInt2, TextSelection paramTextSelection)
  {
    boolean bool;
    if (paramInt2 >= paramInt1) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "end cannot be less than start");
    Preconditions.checkNotNull(paramTextSelection);
    String str;
    if (paramTextSelection.getEntityCount() > 0) {
      str = paramTextSelection.getEntity(0);
    } else {
      str = "";
    }
    return new SelectionEvent(paramInt1, paramInt2, 5, str, 0, paramTextSelection.getId());
  }
  
  public static SelectionEvent createSelectionStartedEvent(int paramInt1, int paramInt2)
  {
    return new SelectionEvent(paramInt2, paramInt2 + 1, 1, "", paramInt1, "");
  }
  
  public static boolean isTerminal(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SelectionEvent)) {
      return false;
    }
    paramObject = (SelectionEvent)paramObject;
    if ((this.mAbsoluteStart != ((SelectionEvent)paramObject).mAbsoluteStart) || (this.mAbsoluteEnd != ((SelectionEvent)paramObject).mAbsoluteEnd) || (this.mEventType != ((SelectionEvent)paramObject).mEventType) || (!Objects.equals(this.mEntityType, ((SelectionEvent)paramObject).mEntityType)) || (!Objects.equals(this.mWidgetVersion, ((SelectionEvent)paramObject).mWidgetVersion)) || (!Objects.equals(this.mPackageName, ((SelectionEvent)paramObject).mPackageName)) || (this.mUserId != ((SelectionEvent)paramObject).mUserId) || (!Objects.equals(this.mWidgetType, ((SelectionEvent)paramObject).mWidgetType)) || (this.mInvocationMethod != ((SelectionEvent)paramObject).mInvocationMethod) || (!Objects.equals(this.mResultId, ((SelectionEvent)paramObject).mResultId)) || (this.mEventTime != ((SelectionEvent)paramObject).mEventTime) || (this.mDurationSinceSessionStart != ((SelectionEvent)paramObject).mDurationSinceSessionStart) || (this.mDurationSincePreviousEvent != ((SelectionEvent)paramObject).mDurationSincePreviousEvent) || (this.mEventIndex != ((SelectionEvent)paramObject).mEventIndex) || (!Objects.equals(this.mSessionId, ((SelectionEvent)paramObject).mSessionId)) || (this.mStart != ((SelectionEvent)paramObject).mStart) || (this.mEnd != ((SelectionEvent)paramObject).mEnd) || (this.mSmartStart != ((SelectionEvent)paramObject).mSmartStart) || (this.mSmartEnd != ((SelectionEvent)paramObject).mSmartEnd)) {
      bool = false;
    }
    return bool;
  }
  
  int getAbsoluteEnd()
  {
    return this.mAbsoluteEnd;
  }
  
  int getAbsoluteStart()
  {
    return this.mAbsoluteStart;
  }
  
  public long getDurationSincePreviousEvent()
  {
    return this.mDurationSincePreviousEvent;
  }
  
  public long getDurationSinceSessionStart()
  {
    return this.mDurationSinceSessionStart;
  }
  
  public int getEnd()
  {
    return this.mEnd;
  }
  
  public String getEntityType()
  {
    return this.mEntityType;
  }
  
  public int getEventIndex()
  {
    return this.mEventIndex;
  }
  
  public long getEventTime()
  {
    return this.mEventTime;
  }
  
  public int getEventType()
  {
    return this.mEventType;
  }
  
  public int getInvocationMethod()
  {
    return this.mInvocationMethod;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public String getResultId()
  {
    return this.mResultId;
  }
  
  public TextClassificationSessionId getSessionId()
  {
    return this.mSessionId;
  }
  
  public int getSmartEnd()
  {
    return this.mSmartEnd;
  }
  
  public int getSmartStart()
  {
    return this.mSmartStart;
  }
  
  public int getStart()
  {
    return this.mStart;
  }
  
  public int getUserId()
  {
    return this.mUserId;
  }
  
  public String getWidgetType()
  {
    return this.mWidgetType;
  }
  
  public String getWidgetVersion()
  {
    return this.mWidgetVersion;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.mAbsoluteStart), Integer.valueOf(this.mAbsoluteEnd), Integer.valueOf(this.mEventType), this.mEntityType, this.mWidgetVersion, this.mPackageName, Integer.valueOf(this.mUserId), this.mWidgetType, Integer.valueOf(this.mInvocationMethod), this.mResultId, Long.valueOf(this.mEventTime), Long.valueOf(this.mDurationSinceSessionStart), Long.valueOf(this.mDurationSincePreviousEvent), Integer.valueOf(this.mEventIndex), this.mSessionId, Integer.valueOf(this.mStart), Integer.valueOf(this.mEnd), Integer.valueOf(this.mSmartStart), Integer.valueOf(this.mSmartEnd) });
  }
  
  boolean isTerminal()
  {
    return isTerminal(this.mEventType);
  }
  
  SelectionEvent setDurationSincePreviousEvent(long paramLong)
  {
    this.mDurationSincePreviousEvent = paramLong;
    return this;
  }
  
  SelectionEvent setDurationSinceSessionStart(long paramLong)
  {
    this.mDurationSinceSessionStart = paramLong;
    return this;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public SelectionEvent setEnd(int paramInt)
  {
    this.mEnd = paramInt;
    return this;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public SelectionEvent setEventIndex(int paramInt)
  {
    this.mEventIndex = paramInt;
    return this;
  }
  
  SelectionEvent setEventTime(long paramLong)
  {
    this.mEventTime = paramLong;
    return this;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public void setEventType(int paramInt)
  {
    this.mEventType = paramInt;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public void setInvocationMethod(int paramInt)
  {
    this.mInvocationMethod = paramInt;
  }
  
  SelectionEvent setResultId(String paramString)
  {
    this.mResultId = paramString;
    return this;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public SelectionEvent setSessionId(TextClassificationSessionId paramTextClassificationSessionId)
  {
    this.mSessionId = paramTextClassificationSessionId;
    return this;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public SelectionEvent setSmartEnd(int paramInt)
  {
    this.mSmartEnd = paramInt;
    return this;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public SelectionEvent setSmartStart(int paramInt)
  {
    this.mSmartStart = paramInt;
    return this;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public SelectionEvent setStart(int paramInt)
  {
    this.mStart = paramInt;
    return this;
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
  public void setTextClassificationSessionContext(TextClassificationContext paramTextClassificationContext)
  {
    this.mPackageName = paramTextClassificationContext.getPackageName();
    this.mWidgetType = paramTextClassificationContext.getWidgetType();
    this.mWidgetVersion = paramTextClassificationContext.getWidgetVersion();
    this.mUserId = paramTextClassificationContext.getUserId();
  }
  
  void setUserId(int paramInt)
  {
    this.mUserId = paramInt;
  }
  
  public String toString()
  {
    return String.format(Locale.US, "SelectionEvent {absoluteStart=%d, absoluteEnd=%d, eventType=%d, entityType=%s, widgetVersion=%s, packageName=%s, widgetType=%s, invocationMethod=%s, userId=%d, resultId=%s, eventTime=%d, durationSinceSessionStart=%d, durationSincePreviousEvent=%d, eventIndex=%d,sessionId=%s, start=%d, end=%d, smartStart=%d, smartEnd=%d}", new Object[] { Integer.valueOf(this.mAbsoluteStart), Integer.valueOf(this.mAbsoluteEnd), Integer.valueOf(this.mEventType), this.mEntityType, this.mWidgetVersion, this.mPackageName, this.mWidgetType, Integer.valueOf(this.mInvocationMethod), Integer.valueOf(this.mUserId), this.mResultId, Long.valueOf(this.mEventTime), Long.valueOf(this.mDurationSinceSessionStart), Long.valueOf(this.mDurationSincePreviousEvent), Integer.valueOf(this.mEventIndex), this.mSessionId, Integer.valueOf(this.mStart), Integer.valueOf(this.mEnd), Integer.valueOf(this.mSmartStart), Integer.valueOf(this.mSmartEnd) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mAbsoluteStart);
    paramParcel.writeInt(this.mAbsoluteEnd);
    paramParcel.writeInt(this.mEventType);
    paramParcel.writeString(this.mEntityType);
    Object localObject = this.mWidgetVersion;
    int i = 1;
    int j;
    if (localObject != null) {
      j = 1;
    } else {
      j = 0;
    }
    paramParcel.writeInt(j);
    localObject = this.mWidgetVersion;
    if (localObject != null) {
      paramParcel.writeString((String)localObject);
    }
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeString(this.mWidgetType);
    paramParcel.writeInt(this.mInvocationMethod);
    paramParcel.writeString(this.mResultId);
    paramParcel.writeLong(this.mEventTime);
    paramParcel.writeLong(this.mDurationSinceSessionStart);
    paramParcel.writeLong(this.mDurationSincePreviousEvent);
    paramParcel.writeInt(this.mEventIndex);
    if (this.mSessionId != null) {
      j = i;
    } else {
      j = 0;
    }
    paramParcel.writeInt(j);
    localObject = this.mSessionId;
    if (localObject != null) {
      ((TextClassificationSessionId)localObject).writeToParcel(paramParcel, paramInt);
    }
    paramParcel.writeInt(this.mStart);
    paramParcel.writeInt(this.mEnd);
    paramParcel.writeInt(this.mSmartStart);
    paramParcel.writeInt(this.mSmartEnd);
    paramParcel.writeInt(this.mUserId);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ActionType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface EventType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface InvocationMethod {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/SelectionEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */