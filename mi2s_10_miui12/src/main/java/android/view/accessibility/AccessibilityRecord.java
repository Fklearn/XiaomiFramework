package android.view.accessibility;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcelable;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class AccessibilityRecord
{
  protected static final boolean DEBUG_CONCISE_TOSTRING = false;
  private static final int GET_SOURCE_PREFETCH_FLAGS = 7;
  private static final int MAX_POOL_SIZE = 10;
  private static final int PROPERTY_CHECKED = 1;
  private static final int PROPERTY_ENABLED = 2;
  private static final int PROPERTY_FULL_SCREEN = 128;
  private static final int PROPERTY_IMPORTANT_FOR_ACCESSIBILITY = 512;
  private static final int PROPERTY_PASSWORD = 4;
  private static final int PROPERTY_SCROLLABLE = 256;
  private static final int UNDEFINED = -1;
  private static AccessibilityRecord sPool;
  private static final Object sPoolLock = new Object();
  private static int sPoolSize;
  int mAddedCount = -1;
  CharSequence mBeforeText;
  int mBooleanProperties = 0;
  CharSequence mClassName;
  int mConnectionId = -1;
  CharSequence mContentDescription;
  int mCurrentItemIndex = -1;
  int mFromIndex = -1;
  private boolean mIsInPool;
  int mItemCount = -1;
  int mMaxScrollX = -1;
  int mMaxScrollY = -1;
  private AccessibilityRecord mNext;
  Parcelable mParcelableData;
  int mRemovedCount = -1;
  int mScrollDeltaX = -1;
  int mScrollDeltaY = -1;
  int mScrollX = -1;
  int mScrollY = -1;
  @UnsupportedAppUsage
  boolean mSealed;
  @UnsupportedAppUsage
  long mSourceNodeId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
  int mSourceWindowId = -1;
  final List<CharSequence> mText = new ArrayList();
  int mToIndex = -1;
  
  private void append(StringBuilder paramStringBuilder, String paramString, int paramInt)
  {
    appendPropName(paramStringBuilder, paramString).append(paramInt);
  }
  
  private void append(StringBuilder paramStringBuilder, String paramString, Object paramObject)
  {
    appendPropName(paramStringBuilder, paramString).append(paramObject);
  }
  
  private StringBuilder appendPropName(StringBuilder paramStringBuilder, String paramString)
  {
    paramStringBuilder.append("; ");
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(": ");
    return paramStringBuilder;
  }
  
  private void appendUnless(boolean paramBoolean, int paramInt, StringBuilder paramStringBuilder)
  {
    paramBoolean = getBooleanProperty(paramInt);
    appendPropName(paramStringBuilder, singleBooleanPropertyToString(paramInt)).append(paramBoolean);
  }
  
  private boolean getBooleanProperty(int paramInt)
  {
    boolean bool;
    if ((this.mBooleanProperties & paramInt) == paramInt) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static AccessibilityRecord obtain()
  {
    synchronized (sPoolLock)
    {
      if (sPool != null)
      {
        localAccessibilityRecord = sPool;
        sPool = sPool.mNext;
        sPoolSize -= 1;
        localAccessibilityRecord.mNext = null;
        localAccessibilityRecord.mIsInPool = false;
        return localAccessibilityRecord;
      }
      AccessibilityRecord localAccessibilityRecord = new android/view/accessibility/AccessibilityRecord;
      localAccessibilityRecord.<init>();
      return localAccessibilityRecord;
    }
  }
  
  public static AccessibilityRecord obtain(AccessibilityRecord paramAccessibilityRecord)
  {
    AccessibilityRecord localAccessibilityRecord = obtain();
    localAccessibilityRecord.init(paramAccessibilityRecord);
    return localAccessibilityRecord;
  }
  
  private void setBooleanProperty(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mBooleanProperties |= paramInt;
    } else {
      this.mBooleanProperties &= paramInt;
    }
  }
  
  private static String singleBooleanPropertyToString(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 4)
        {
          if (paramInt != 128)
          {
            if (paramInt != 256)
            {
              if (paramInt != 512) {
                return Integer.toHexString(paramInt);
              }
              return "ImportantForAccessibility";
            }
            return "Scrollable";
          }
          return "FullScreen";
        }
        return "Password";
      }
      return "Enabled";
    }
    return "Checked";
  }
  
  StringBuilder appendTo(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(" [ ClassName: ");
    paramStringBuilder.append(this.mClassName);
    appendPropName(paramStringBuilder, "Text").append(this.mText);
    append(paramStringBuilder, "ContentDescription", this.mContentDescription);
    append(paramStringBuilder, "ItemCount", this.mItemCount);
    append(paramStringBuilder, "CurrentItemIndex", this.mCurrentItemIndex);
    appendUnless(true, 2, paramStringBuilder);
    appendUnless(false, 4, paramStringBuilder);
    appendUnless(false, 1, paramStringBuilder);
    appendUnless(false, 128, paramStringBuilder);
    appendUnless(false, 256, paramStringBuilder);
    append(paramStringBuilder, "BeforeText", this.mBeforeText);
    append(paramStringBuilder, "FromIndex", this.mFromIndex);
    append(paramStringBuilder, "ToIndex", this.mToIndex);
    append(paramStringBuilder, "ScrollX", this.mScrollX);
    append(paramStringBuilder, "ScrollY", this.mScrollY);
    append(paramStringBuilder, "MaxScrollX", this.mMaxScrollX);
    append(paramStringBuilder, "MaxScrollY", this.mMaxScrollY);
    append(paramStringBuilder, "AddedCount", this.mAddedCount);
    append(paramStringBuilder, "RemovedCount", this.mRemovedCount);
    append(paramStringBuilder, "ParcelableData", this.mParcelableData);
    paramStringBuilder.append(" ]");
    return paramStringBuilder;
  }
  
  void clear()
  {
    this.mSealed = false;
    this.mBooleanProperties = 0;
    this.mCurrentItemIndex = -1;
    this.mItemCount = -1;
    this.mFromIndex = -1;
    this.mToIndex = -1;
    this.mScrollX = -1;
    this.mScrollY = -1;
    this.mMaxScrollX = -1;
    this.mMaxScrollY = -1;
    this.mAddedCount = -1;
    this.mRemovedCount = -1;
    this.mClassName = null;
    this.mContentDescription = null;
    this.mBeforeText = null;
    this.mParcelableData = null;
    this.mText.clear();
    this.mSourceNodeId = 2147483647L;
    this.mSourceWindowId = -1;
    this.mConnectionId = -1;
  }
  
  void enforceNotSealed()
  {
    if (!isSealed()) {
      return;
    }
    throw new IllegalStateException("Cannot perform this action on a sealed instance.");
  }
  
  void enforceSealed()
  {
    if (isSealed()) {
      return;
    }
    throw new IllegalStateException("Cannot perform this action on a not sealed instance.");
  }
  
  public int getAddedCount()
  {
    return this.mAddedCount;
  }
  
  public CharSequence getBeforeText()
  {
    return this.mBeforeText;
  }
  
  public CharSequence getClassName()
  {
    return this.mClassName;
  }
  
  public CharSequence getContentDescription()
  {
    return this.mContentDescription;
  }
  
  public int getCurrentItemIndex()
  {
    return this.mCurrentItemIndex;
  }
  
  public int getFromIndex()
  {
    return this.mFromIndex;
  }
  
  public int getItemCount()
  {
    return this.mItemCount;
  }
  
  public int getMaxScrollX()
  {
    return this.mMaxScrollX;
  }
  
  public int getMaxScrollY()
  {
    return this.mMaxScrollY;
  }
  
  public Parcelable getParcelableData()
  {
    return this.mParcelableData;
  }
  
  public int getRemovedCount()
  {
    return this.mRemovedCount;
  }
  
  public int getScrollDeltaX()
  {
    return this.mScrollDeltaX;
  }
  
  public int getScrollDeltaY()
  {
    return this.mScrollDeltaY;
  }
  
  public int getScrollX()
  {
    return this.mScrollX;
  }
  
  public int getScrollY()
  {
    return this.mScrollY;
  }
  
  public AccessibilityNodeInfo getSource()
  {
    enforceSealed();
    if ((this.mConnectionId != -1) && (this.mSourceWindowId != -1) && (AccessibilityNodeInfo.getAccessibilityViewId(this.mSourceNodeId) != Integer.MAX_VALUE)) {
      return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mSourceWindowId, this.mSourceNodeId, false, 7, null);
    }
    return null;
  }
  
  @UnsupportedAppUsage
  public long getSourceNodeId()
  {
    return this.mSourceNodeId;
  }
  
  public List<CharSequence> getText()
  {
    return this.mText;
  }
  
  public int getToIndex()
  {
    return this.mToIndex;
  }
  
  public int getWindowId()
  {
    return this.mSourceWindowId;
  }
  
  void init(AccessibilityRecord paramAccessibilityRecord)
  {
    this.mSealed = paramAccessibilityRecord.mSealed;
    this.mBooleanProperties = paramAccessibilityRecord.mBooleanProperties;
    this.mCurrentItemIndex = paramAccessibilityRecord.mCurrentItemIndex;
    this.mItemCount = paramAccessibilityRecord.mItemCount;
    this.mFromIndex = paramAccessibilityRecord.mFromIndex;
    this.mToIndex = paramAccessibilityRecord.mToIndex;
    this.mScrollX = paramAccessibilityRecord.mScrollX;
    this.mScrollY = paramAccessibilityRecord.mScrollY;
    this.mMaxScrollX = paramAccessibilityRecord.mMaxScrollX;
    this.mMaxScrollY = paramAccessibilityRecord.mMaxScrollY;
    this.mAddedCount = paramAccessibilityRecord.mAddedCount;
    this.mRemovedCount = paramAccessibilityRecord.mRemovedCount;
    this.mClassName = paramAccessibilityRecord.mClassName;
    this.mContentDescription = paramAccessibilityRecord.mContentDescription;
    this.mBeforeText = paramAccessibilityRecord.mBeforeText;
    this.mParcelableData = paramAccessibilityRecord.mParcelableData;
    this.mText.addAll(paramAccessibilityRecord.mText);
    this.mSourceWindowId = paramAccessibilityRecord.mSourceWindowId;
    this.mSourceNodeId = paramAccessibilityRecord.mSourceNodeId;
    this.mConnectionId = paramAccessibilityRecord.mConnectionId;
  }
  
  public boolean isChecked()
  {
    return getBooleanProperty(1);
  }
  
  public boolean isEnabled()
  {
    return getBooleanProperty(2);
  }
  
  public boolean isFullScreen()
  {
    return getBooleanProperty(128);
  }
  
  public boolean isImportantForAccessibility()
  {
    return getBooleanProperty(512);
  }
  
  public boolean isPassword()
  {
    return getBooleanProperty(4);
  }
  
  public boolean isScrollable()
  {
    return getBooleanProperty(256);
  }
  
  boolean isSealed()
  {
    return this.mSealed;
  }
  
  public void recycle()
  {
    if (!this.mIsInPool)
    {
      clear();
      synchronized (sPoolLock)
      {
        if (sPoolSize <= 10)
        {
          this.mNext = sPool;
          sPool = this;
          this.mIsInPool = true;
          sPoolSize += 1;
        }
        return;
      }
    }
    throw new IllegalStateException("Record already recycled!");
  }
  
  public void setAddedCount(int paramInt)
  {
    enforceNotSealed();
    this.mAddedCount = paramInt;
  }
  
  public void setBeforeText(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length());
    }
    this.mBeforeText = paramCharSequence;
  }
  
  public void setChecked(boolean paramBoolean)
  {
    enforceNotSealed();
    setBooleanProperty(1, paramBoolean);
  }
  
  public void setClassName(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    this.mClassName = paramCharSequence;
  }
  
  public void setConnectionId(int paramInt)
  {
    enforceNotSealed();
    this.mConnectionId = paramInt;
  }
  
  public void setContentDescription(CharSequence paramCharSequence)
  {
    enforceNotSealed();
    if (paramCharSequence == null) {
      paramCharSequence = null;
    } else {
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length());
    }
    this.mContentDescription = paramCharSequence;
  }
  
  public void setCurrentItemIndex(int paramInt)
  {
    enforceNotSealed();
    this.mCurrentItemIndex = paramInt;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    enforceNotSealed();
    setBooleanProperty(2, paramBoolean);
  }
  
  public void setFromIndex(int paramInt)
  {
    enforceNotSealed();
    this.mFromIndex = paramInt;
  }
  
  public void setFullScreen(boolean paramBoolean)
  {
    enforceNotSealed();
    setBooleanProperty(128, paramBoolean);
  }
  
  public void setImportantForAccessibility(boolean paramBoolean)
  {
    enforceNotSealed();
    setBooleanProperty(512, paramBoolean);
  }
  
  public void setItemCount(int paramInt)
  {
    enforceNotSealed();
    this.mItemCount = paramInt;
  }
  
  public void setMaxScrollX(int paramInt)
  {
    enforceNotSealed();
    this.mMaxScrollX = paramInt;
  }
  
  public void setMaxScrollY(int paramInt)
  {
    enforceNotSealed();
    this.mMaxScrollY = paramInt;
  }
  
  public void setParcelableData(Parcelable paramParcelable)
  {
    enforceNotSealed();
    this.mParcelableData = paramParcelable;
  }
  
  public void setPassword(boolean paramBoolean)
  {
    enforceNotSealed();
    setBooleanProperty(4, paramBoolean);
  }
  
  public void setRemovedCount(int paramInt)
  {
    enforceNotSealed();
    this.mRemovedCount = paramInt;
  }
  
  public void setScrollDeltaX(int paramInt)
  {
    enforceNotSealed();
    this.mScrollDeltaX = paramInt;
  }
  
  public void setScrollDeltaY(int paramInt)
  {
    enforceNotSealed();
    this.mScrollDeltaY = paramInt;
  }
  
  public void setScrollX(int paramInt)
  {
    enforceNotSealed();
    this.mScrollX = paramInt;
  }
  
  public void setScrollY(int paramInt)
  {
    enforceNotSealed();
    this.mScrollY = paramInt;
  }
  
  public void setScrollable(boolean paramBoolean)
  {
    enforceNotSealed();
    setBooleanProperty(256, paramBoolean);
  }
  
  public void setSealed(boolean paramBoolean)
  {
    this.mSealed = paramBoolean;
  }
  
  public void setSource(View paramView)
  {
    setSource(paramView, -1);
  }
  
  public void setSource(View paramView, int paramInt)
  {
    enforceNotSealed();
    boolean bool = true;
    int i = Integer.MAX_VALUE;
    this.mSourceWindowId = -1;
    if (paramView != null)
    {
      bool = paramView.isImportantForAccessibility();
      i = paramView.getAccessibilityViewId();
      this.mSourceWindowId = paramView.getAccessibilityWindowId();
    }
    setBooleanProperty(512, bool);
    this.mSourceNodeId = AccessibilityNodeInfo.makeNodeId(i, paramInt);
  }
  
  public void setSourceNodeId(long paramLong)
  {
    this.mSourceNodeId = paramLong;
  }
  
  public void setToIndex(int paramInt)
  {
    enforceNotSealed();
    this.mToIndex = paramInt;
  }
  
  public void setWindowId(int paramInt)
  {
    this.mSourceWindowId = paramInt;
  }
  
  public String toString()
  {
    return appendTo(new StringBuilder()).toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */