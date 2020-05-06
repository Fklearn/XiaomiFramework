package android.view.accessibility;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Pools.SynchronizedPool;
import com.android.internal.util.BitUtils;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public final class AccessibilityEvent
  extends AccessibilityRecord
  implements Parcelable
{
  public static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 4;
  public static final int CONTENT_CHANGE_TYPE_PANE_APPEARED = 16;
  public static final int CONTENT_CHANGE_TYPE_PANE_DISAPPEARED = 32;
  public static final int CONTENT_CHANGE_TYPE_PANE_TITLE = 8;
  public static final int CONTENT_CHANGE_TYPE_SUBTREE = 1;
  public static final int CONTENT_CHANGE_TYPE_TEXT = 2;
  public static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0;
  public static final Parcelable.Creator<AccessibilityEvent> CREATOR = new Parcelable.Creator()
  {
    public AccessibilityEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
      localAccessibilityEvent.initFromParcel(paramAnonymousParcel);
      return localAccessibilityEvent;
    }
    
    public AccessibilityEvent[] newArray(int paramAnonymousInt)
    {
      return new AccessibilityEvent[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG = false;
  public static final boolean DEBUG_ORIGIN = false;
  public static final int INVALID_POSITION = -1;
  private static final int MAX_POOL_SIZE = 10;
  @Deprecated
  public static final int MAX_TEXT_LENGTH = 500;
  public static final int TYPES_ALL_MASK = -1;
  public static final int TYPE_ANNOUNCEMENT = 16384;
  public static final int TYPE_ASSIST_READING_CONTEXT = 16777216;
  public static final int TYPE_GESTURE_DETECTION_END = 524288;
  public static final int TYPE_GESTURE_DETECTION_START = 262144;
  public static final int TYPE_NOTIFICATION_STATE_CHANGED = 64;
  public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 1024;
  public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 512;
  public static final int TYPE_TOUCH_INTERACTION_END = 2097152;
  public static final int TYPE_TOUCH_INTERACTION_START = 1048576;
  public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 32768;
  public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 65536;
  public static final int TYPE_VIEW_CLICKED = 1;
  public static final int TYPE_VIEW_CONTEXT_CLICKED = 8388608;
  public static final int TYPE_VIEW_FOCUSED = 8;
  public static final int TYPE_VIEW_HOVER_ENTER = 128;
  public static final int TYPE_VIEW_HOVER_EXIT = 256;
  public static final int TYPE_VIEW_LONG_CLICKED = 2;
  public static final int TYPE_VIEW_SCROLLED = 4096;
  public static final int TYPE_VIEW_SELECTED = 4;
  public static final int TYPE_VIEW_TEXT_CHANGED = 16;
  public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 8192;
  public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 131072;
  public static final int TYPE_WINDOWS_CHANGED = 4194304;
  public static final int TYPE_WINDOW_CONTENT_CHANGED = 2048;
  public static final int TYPE_WINDOW_STATE_CHANGED = 32;
  public static final int WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED = 128;
  public static final int WINDOWS_CHANGE_ACTIVE = 32;
  public static final int WINDOWS_CHANGE_ADDED = 1;
  public static final int WINDOWS_CHANGE_BOUNDS = 8;
  public static final int WINDOWS_CHANGE_CHILDREN = 512;
  public static final int WINDOWS_CHANGE_FOCUSED = 64;
  public static final int WINDOWS_CHANGE_LAYER = 16;
  public static final int WINDOWS_CHANGE_PARENT = 256;
  public static final int WINDOWS_CHANGE_PIP = 1024;
  public static final int WINDOWS_CHANGE_REMOVED = 2;
  public static final int WINDOWS_CHANGE_TITLE = 4;
  private static final Pools.SynchronizedPool<AccessibilityEvent> sPool = new Pools.SynchronizedPool(10);
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  int mAction;
  int mContentChangeTypes;
  private long mEventTime;
  @UnsupportedAppUsage
  private int mEventType;
  int mMovementGranularity;
  private CharSequence mPackageName;
  private ArrayList<AccessibilityRecord> mRecords;
  int mWindowChangeTypes;
  public StackTraceElement[] originStackTrace = null;
  
  private static String contentChangeTypesToString(int paramInt)
  {
    return BitUtils.flagsToString(paramInt, _..Lambda.AccessibilityEvent.gjyLj65KEDUo5PJZiVYxPrd2Vug.INSTANCE);
  }
  
  public static String eventTypeToString(int paramInt)
  {
    if (paramInt == -1) {
      return "TYPES_ALL_MASK";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = paramInt;
    for (paramInt = i; j != 0; paramInt++)
    {
      i = 1 << Integer.numberOfTrailingZeros(j);
      j &= i;
      if (paramInt > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(singleEventTypeToString(i));
    }
    if (paramInt > 1)
    {
      localStringBuilder.insert(0, '[');
      localStringBuilder.append(']');
    }
    return localStringBuilder.toString();
  }
  
  public static AccessibilityEvent obtain()
  {
    AccessibilityEvent localAccessibilityEvent1 = (AccessibilityEvent)sPool.acquire();
    AccessibilityEvent localAccessibilityEvent2 = localAccessibilityEvent1;
    if (localAccessibilityEvent1 == null) {
      localAccessibilityEvent2 = new AccessibilityEvent();
    }
    return localAccessibilityEvent2;
  }
  
  public static AccessibilityEvent obtain(int paramInt)
  {
    AccessibilityEvent localAccessibilityEvent = obtain();
    localAccessibilityEvent.setEventType(paramInt);
    return localAccessibilityEvent;
  }
  
  public static AccessibilityEvent obtain(AccessibilityEvent paramAccessibilityEvent)
  {
    AccessibilityEvent localAccessibilityEvent = obtain();
    localAccessibilityEvent.init(paramAccessibilityEvent);
    Object localObject = paramAccessibilityEvent.mRecords;
    if (localObject != null)
    {
      int i = ((ArrayList)localObject).size();
      localAccessibilityEvent.mRecords = new ArrayList(i);
      for (int j = 0; j < i; j++)
      {
        localObject = AccessibilityRecord.obtain((AccessibilityRecord)paramAccessibilityEvent.mRecords.get(j));
        localAccessibilityEvent.mRecords.add(localObject);
      }
    }
    return localAccessibilityEvent;
  }
  
  public static AccessibilityEvent obtainWindowsChangedEvent(int paramInt1, int paramInt2)
  {
    AccessibilityEvent localAccessibilityEvent = obtain(4194304);
    localAccessibilityEvent.setWindowId(paramInt1);
    localAccessibilityEvent.setWindowChanges(paramInt2);
    localAccessibilityEvent.setImportantForAccessibility(true);
    return localAccessibilityEvent;
  }
  
  private void readAccessibilityRecordFromParcel(AccessibilityRecord paramAccessibilityRecord, Parcel paramParcel)
  {
    paramAccessibilityRecord.mBooleanProperties = paramParcel.readInt();
    paramAccessibilityRecord.mCurrentItemIndex = paramParcel.readInt();
    paramAccessibilityRecord.mItemCount = paramParcel.readInt();
    paramAccessibilityRecord.mFromIndex = paramParcel.readInt();
    paramAccessibilityRecord.mToIndex = paramParcel.readInt();
    paramAccessibilityRecord.mScrollX = paramParcel.readInt();
    paramAccessibilityRecord.mScrollY = paramParcel.readInt();
    paramAccessibilityRecord.mScrollDeltaX = paramParcel.readInt();
    paramAccessibilityRecord.mScrollDeltaY = paramParcel.readInt();
    paramAccessibilityRecord.mMaxScrollX = paramParcel.readInt();
    paramAccessibilityRecord.mMaxScrollY = paramParcel.readInt();
    paramAccessibilityRecord.mAddedCount = paramParcel.readInt();
    paramAccessibilityRecord.mRemovedCount = paramParcel.readInt();
    paramAccessibilityRecord.mClassName = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    paramAccessibilityRecord.mContentDescription = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    paramAccessibilityRecord.mBeforeText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    paramAccessibilityRecord.mParcelableData = paramParcel.readParcelable(null);
    paramParcel.readList(paramAccessibilityRecord.mText, null);
    paramAccessibilityRecord.mSourceWindowId = paramParcel.readInt();
    paramAccessibilityRecord.mSourceNodeId = paramParcel.readLong();
    int i = paramParcel.readInt();
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    paramAccessibilityRecord.mSealed = bool;
  }
  
  private static String singleContentChangeTypeToString(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 1)
      {
        if (paramInt != 2)
        {
          if (paramInt != 4)
          {
            if (paramInt != 8)
            {
              if (paramInt != 16)
              {
                if (paramInt != 32) {
                  return Integer.toHexString(paramInt);
                }
                return "CONTENT_CHANGE_TYPE_PANE_DISAPPEARED";
              }
              return "CONTENT_CHANGE_TYPE_PANE_APPEARED";
            }
            return "CONTENT_CHANGE_TYPE_PANE_TITLE";
          }
          return "CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION";
        }
        return "CONTENT_CHANGE_TYPE_TEXT";
      }
      return "CONTENT_CHANGE_TYPE_SUBTREE";
    }
    return "CONTENT_CHANGE_TYPE_UNDEFINED";
  }
  
  private static String singleEventTypeToString(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        switch (paramInt)
        {
        default: 
          return Integer.toHexString(paramInt);
        case 16777216: 
          return "TYPE_ASSIST_READING_CONTEXT";
        case 8388608: 
          return "TYPE_VIEW_CONTEXT_CLICKED";
        case 4194304: 
          return "TYPE_WINDOWS_CHANGED";
        case 2097152: 
          return "TYPE_TOUCH_INTERACTION_END";
        case 1048576: 
          return "TYPE_TOUCH_INTERACTION_START";
        case 524288: 
          return "TYPE_GESTURE_DETECTION_END";
        case 262144: 
          return "TYPE_GESTURE_DETECTION_START";
        case 131072: 
          return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
        case 65536: 
          return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
        case 32768: 
          return "TYPE_VIEW_ACCESSIBILITY_FOCUSED";
        case 16384: 
          return "TYPE_ANNOUNCEMENT";
        case 8192: 
          return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
        case 4096: 
          return "TYPE_VIEW_SCROLLED";
        case 2048: 
          return "TYPE_WINDOW_CONTENT_CHANGED";
        case 1024: 
          return "TYPE_TOUCH_EXPLORATION_GESTURE_END";
        case 512: 
          return "TYPE_TOUCH_EXPLORATION_GESTURE_START";
        case 256: 
          return "TYPE_VIEW_HOVER_EXIT";
        case 128: 
          return "TYPE_VIEW_HOVER_ENTER";
        case 64: 
          return "TYPE_NOTIFICATION_STATE_CHANGED";
        case 32: 
          return "TYPE_WINDOW_STATE_CHANGED";
        case 16: 
          return "TYPE_VIEW_TEXT_CHANGED";
        case 8: 
          return "TYPE_VIEW_FOCUSED";
        }
        return "TYPE_VIEW_SELECTED";
      }
      return "TYPE_VIEW_LONG_CLICKED";
    }
    return "TYPE_VIEW_CLICKED";
  }
  
  private static String singleWindowChangeTypeToString(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 4)
        {
          if (paramInt != 8)
          {
            if (paramInt != 16)
            {
              if (paramInt != 32)
              {
                if (paramInt != 64)
                {
                  if (paramInt != 128)
                  {
                    if (paramInt != 256)
                    {
                      if (paramInt != 512)
                      {
                        if (paramInt != 1024) {
                          return Integer.toHexString(paramInt);
                        }
                        return "WINDOWS_CHANGE_PIP";
                      }
                      return "WINDOWS_CHANGE_CHILDREN";
                    }
                    return "WINDOWS_CHANGE_PARENT";
                  }
                  return "WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED";
                }
                return "WINDOWS_CHANGE_FOCUSED";
              }
              return "WINDOWS_CHANGE_ACTIVE";
            }
            return "WINDOWS_CHANGE_LAYER";
          }
          return "WINDOWS_CHANGE_BOUNDS";
        }
        return "WINDOWS_CHANGE_TITLE";
      }
      return "WINDOWS_CHANGE_REMOVED";
    }
    return "WINDOWS_CHANGE_ADDED";
  }
  
  private static String windowChangeTypesToString(int paramInt)
  {
    return BitUtils.flagsToString(paramInt, _..Lambda.AccessibilityEvent.c6ikd5OkCnJv2aVsheVXIxBvSTk.INSTANCE);
  }
  
  private void writeAccessibilityRecordToParcel(AccessibilityRecord paramAccessibilityRecord, Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(paramAccessibilityRecord.mBooleanProperties);
    paramParcel.writeInt(paramAccessibilityRecord.mCurrentItemIndex);
    paramParcel.writeInt(paramAccessibilityRecord.mItemCount);
    paramParcel.writeInt(paramAccessibilityRecord.mFromIndex);
    paramParcel.writeInt(paramAccessibilityRecord.mToIndex);
    paramParcel.writeInt(paramAccessibilityRecord.mScrollX);
    paramParcel.writeInt(paramAccessibilityRecord.mScrollY);
    paramParcel.writeInt(paramAccessibilityRecord.mScrollDeltaX);
    paramParcel.writeInt(paramAccessibilityRecord.mScrollDeltaY);
    paramParcel.writeInt(paramAccessibilityRecord.mMaxScrollX);
    paramParcel.writeInt(paramAccessibilityRecord.mMaxScrollY);
    paramParcel.writeInt(paramAccessibilityRecord.mAddedCount);
    paramParcel.writeInt(paramAccessibilityRecord.mRemovedCount);
    TextUtils.writeToParcel(paramAccessibilityRecord.mClassName, paramParcel, paramInt);
    TextUtils.writeToParcel(paramAccessibilityRecord.mContentDescription, paramParcel, paramInt);
    TextUtils.writeToParcel(paramAccessibilityRecord.mBeforeText, paramParcel, paramInt);
    paramParcel.writeParcelable(paramAccessibilityRecord.mParcelableData, paramInt);
    paramParcel.writeList(paramAccessibilityRecord.mText);
    paramParcel.writeInt(paramAccessibilityRecord.mSourceWindowId);
    paramParcel.writeLong(paramAccessibilityRecord.mSourceNodeId);
    paramParcel.writeInt(paramAccessibilityRecord.mSealed);
  }
  
  public void appendRecord(AccessibilityRecord paramAccessibilityRecord)
  {
    enforceNotSealed();
    if (this.mRecords == null) {
      this.mRecords = new ArrayList();
    }
    this.mRecords.add(paramAccessibilityRecord);
  }
  
  protected void clear()
  {
    super.clear();
    this.mEventType = 0;
    this.mMovementGranularity = 0;
    this.mAction = 0;
    this.mContentChangeTypes = 0;
    this.mWindowChangeTypes = 0;
    this.mPackageName = null;
    this.mEventTime = 0L;
    if (this.mRecords != null) {
      while (!this.mRecords.isEmpty()) {
        ((AccessibilityRecord)this.mRecords.remove(0)).recycle();
      }
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAction()
  {
    return this.mAction;
  }
  
  public int getContentChangeTypes()
  {
    return this.mContentChangeTypes;
  }
  
  public long getEventTime()
  {
    return this.mEventTime;
  }
  
  public int getEventType()
  {
    return this.mEventType;
  }
  
  public int getMovementGranularity()
  {
    return this.mMovementGranularity;
  }
  
  public CharSequence getPackageName()
  {
    return this.mPackageName;
  }
  
  public AccessibilityRecord getRecord(int paramInt)
  {
    Object localObject = this.mRecords;
    if (localObject != null) {
      return (AccessibilityRecord)((ArrayList)localObject).get(paramInt);
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Invalid index ");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(", size is 0");
    throw new IndexOutOfBoundsException(((StringBuilder)localObject).toString());
  }
  
  public int getRecordCount()
  {
    ArrayList localArrayList = this.mRecords;
    int i;
    if (localArrayList == null) {
      i = 0;
    } else {
      i = localArrayList.size();
    }
    return i;
  }
  
  public int getWindowChanges()
  {
    return this.mWindowChangeTypes;
  }
  
  void init(AccessibilityEvent paramAccessibilityEvent)
  {
    super.init(paramAccessibilityEvent);
    this.mEventType = paramAccessibilityEvent.mEventType;
    this.mMovementGranularity = paramAccessibilityEvent.mMovementGranularity;
    this.mAction = paramAccessibilityEvent.mAction;
    this.mContentChangeTypes = paramAccessibilityEvent.mContentChangeTypes;
    this.mWindowChangeTypes = paramAccessibilityEvent.mWindowChangeTypes;
    this.mEventTime = paramAccessibilityEvent.mEventTime;
    this.mPackageName = paramAccessibilityEvent.mPackageName;
  }
  
  public void initFromParcel(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    this.mSealed = bool;
    this.mEventType = paramParcel.readInt();
    this.mMovementGranularity = paramParcel.readInt();
    this.mAction = paramParcel.readInt();
    this.mContentChangeTypes = paramParcel.readInt();
    this.mWindowChangeTypes = paramParcel.readInt();
    this.mPackageName = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.mEventTime = paramParcel.readLong();
    this.mConnectionId = paramParcel.readInt();
    readAccessibilityRecordFromParcel(this, paramParcel);
    int j = paramParcel.readInt();
    if (j > 0)
    {
      this.mRecords = new ArrayList(j);
      for (i = 0; i < j; i++)
      {
        AccessibilityRecord localAccessibilityRecord = AccessibilityRecord.obtain();
        readAccessibilityRecordFromParcel(localAccessibilityRecord, paramParcel);
        localAccessibilityRecord.mConnectionId = this.mConnectionId;
        this.mRecords.add(localAccessibilityRecord);
      }
    }
  }
  
  public void recycle()
  {
    clear();
    sPool.release(this);
  }
  
  public void setAction(int paramInt)
  {
    enforceNotSealed();
    this.mAction = paramInt;
  }
  
  public void setContentChangeTypes(int paramInt)
  {
    enforceNotSealed();
    this.mContentChangeTypes = paramInt;
  }
  
  public void setEventTime(long paramLong)
  {
    enforceNotSealed();
    this.mEventTime = paramLong;
  }
  
  public void setEventType(int paramInt)
  {
    enforceNotSealed();
    this.mEventType = paramInt;
  }
  
  public void setMovementGranularity(int paramInt)
  {
    enforceNotSealed();
    this.mMovementGranularity = paramInt;
  }
  
  public void setPackageName(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    this.mPackageName = paramCharSequence;
  }
  
  public void setSealed(boolean paramBoolean)
  {
    super.setSealed(paramBoolean);
    ArrayList localArrayList = this.mRecords;
    if (localArrayList != null)
    {
      int i = localArrayList.size();
      for (int j = 0; j < i; j++) {
        ((AccessibilityRecord)localArrayList.get(j)).setSealed(paramBoolean);
      }
    }
  }
  
  public void setWindowChanges(int paramInt)
  {
    this.mWindowChangeTypes = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("EventType: ");
    localStringBuilder.append(eventTypeToString(this.mEventType));
    localStringBuilder.append("; EventTime: ");
    localStringBuilder.append(this.mEventTime);
    localStringBuilder.append("; PackageName: ");
    localStringBuilder.append(this.mPackageName);
    localStringBuilder.append("; MovementGranularity: ");
    localStringBuilder.append(this.mMovementGranularity);
    localStringBuilder.append("; Action: ");
    localStringBuilder.append(this.mAction);
    localStringBuilder.append("; ContentChangeTypes: ");
    localStringBuilder.append(contentChangeTypesToString(this.mContentChangeTypes));
    localStringBuilder.append("; WindowChangeTypes: ");
    localStringBuilder.append(windowChangeTypesToString(this.mWindowChangeTypes));
    super.appendTo(localStringBuilder);
    localStringBuilder.append("; recordCount: ");
    localStringBuilder.append(getRecordCount());
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(isSealed());
    paramParcel.writeInt(this.mEventType);
    paramParcel.writeInt(this.mMovementGranularity);
    paramParcel.writeInt(this.mAction);
    paramParcel.writeInt(this.mContentChangeTypes);
    paramParcel.writeInt(this.mWindowChangeTypes);
    TextUtils.writeToParcel(this.mPackageName, paramParcel, 0);
    paramParcel.writeLong(this.mEventTime);
    paramParcel.writeInt(this.mConnectionId);
    writeAccessibilityRecordToParcel(this, paramParcel, paramInt);
    int i = getRecordCount();
    paramParcel.writeInt(i);
    for (int j = 0; j < i; j++) {
      writeAccessibilityRecordToParcel((AccessibilityRecord)this.mRecords.get(j), paramParcel, paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ContentChangeTypes {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface EventType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface WindowsChangeTypes {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */