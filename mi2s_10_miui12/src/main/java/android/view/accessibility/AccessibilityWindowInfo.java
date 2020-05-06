package android.view.accessibility;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.LongArray;
import android.util.Pools.SynchronizedPool;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class AccessibilityWindowInfo
  implements Parcelable
{
  public static final int ACTIVE_WINDOW_ID = Integer.MAX_VALUE;
  public static final int ANY_WINDOW_ID = -2;
  private static final int BOOLEAN_PROPERTY_ACCESSIBILITY_FOCUSED = 4;
  private static final int BOOLEAN_PROPERTY_ACTIVE = 1;
  private static final int BOOLEAN_PROPERTY_FOCUSED = 2;
  private static final int BOOLEAN_PROPERTY_PICTURE_IN_PICTURE = 8;
  public static final Parcelable.Creator<AccessibilityWindowInfo> CREATOR = new Parcelable.Creator()
  {
    public AccessibilityWindowInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      AccessibilityWindowInfo localAccessibilityWindowInfo = AccessibilityWindowInfo.obtain();
      localAccessibilityWindowInfo.initFromParcel(paramAnonymousParcel);
      return localAccessibilityWindowInfo;
    }
    
    public AccessibilityWindowInfo[] newArray(int paramAnonymousInt)
    {
      return new AccessibilityWindowInfo[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG = false;
  private static final int MAX_POOL_SIZE = 10;
  public static final int PICTURE_IN_PICTURE_ACTION_REPLACER_WINDOW_ID = -3;
  public static final int TYPE_ACCESSIBILITY_OVERLAY = 4;
  public static final int TYPE_APPLICATION = 1;
  public static final int TYPE_INPUT_METHOD = 2;
  public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;
  public static final int TYPE_SYSTEM = 3;
  public static final int UNDEFINED_WINDOW_ID = -1;
  private static AtomicInteger sNumInstancesInUse;
  private static final Pools.SynchronizedPool<AccessibilityWindowInfo> sPool = new Pools.SynchronizedPool(10);
  private long mAnchorId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
  private int mBooleanProperties;
  private final Rect mBoundsInScreen = new Rect();
  private LongArray mChildIds;
  private int mConnectionId = -1;
  private int mId = -1;
  private int mLayer = -1;
  private int mParentId = -1;
  private CharSequence mTitle;
  private int mType = -1;
  
  private AccessibilityWindowInfo() {}
  
  AccessibilityWindowInfo(AccessibilityWindowInfo paramAccessibilityWindowInfo)
  {
    init(paramAccessibilityWindowInfo);
  }
  
  private void clear()
  {
    this.mType = -1;
    this.mLayer = -1;
    this.mBooleanProperties = 0;
    this.mId = -1;
    this.mParentId = -1;
    this.mBoundsInScreen.setEmpty();
    LongArray localLongArray = this.mChildIds;
    if (localLongArray != null) {
      localLongArray.clear();
    }
    this.mConnectionId = -1;
    this.mAnchorId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
    this.mTitle = null;
  }
  
  private boolean getBooleanProperty(int paramInt)
  {
    boolean bool;
    if ((this.mBooleanProperties & paramInt) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void init(AccessibilityWindowInfo paramAccessibilityWindowInfo)
  {
    this.mType = paramAccessibilityWindowInfo.mType;
    this.mLayer = paramAccessibilityWindowInfo.mLayer;
    this.mBooleanProperties = paramAccessibilityWindowInfo.mBooleanProperties;
    this.mId = paramAccessibilityWindowInfo.mId;
    this.mParentId = paramAccessibilityWindowInfo.mParentId;
    this.mBoundsInScreen.set(paramAccessibilityWindowInfo.mBoundsInScreen);
    this.mTitle = paramAccessibilityWindowInfo.mTitle;
    this.mAnchorId = paramAccessibilityWindowInfo.mAnchorId;
    LongArray localLongArray = paramAccessibilityWindowInfo.mChildIds;
    if ((localLongArray != null) && (localLongArray.size() > 0))
    {
      localLongArray = this.mChildIds;
      if (localLongArray == null) {
        this.mChildIds = paramAccessibilityWindowInfo.mChildIds.clone();
      } else {
        localLongArray.addAll(paramAccessibilityWindowInfo.mChildIds);
      }
    }
    this.mConnectionId = paramAccessibilityWindowInfo.mConnectionId;
  }
  
  private void initFromParcel(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mLayer = paramParcel.readInt();
    this.mBooleanProperties = paramParcel.readInt();
    this.mId = paramParcel.readInt();
    this.mParentId = paramParcel.readInt();
    this.mBoundsInScreen.readFromParcel(paramParcel);
    this.mTitle = paramParcel.readCharSequence();
    this.mAnchorId = paramParcel.readLong();
    int i = paramParcel.readInt();
    if (i > 0)
    {
      if (this.mChildIds == null) {
        this.mChildIds = new LongArray(i);
      }
      for (int j = 0; j < i; j++)
      {
        int k = paramParcel.readInt();
        this.mChildIds.add(k);
      }
    }
    this.mConnectionId = paramParcel.readInt();
  }
  
  public static AccessibilityWindowInfo obtain()
  {
    Object localObject1 = (AccessibilityWindowInfo)sPool.acquire();
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = new AccessibilityWindowInfo();
    }
    localObject1 = sNumInstancesInUse;
    if (localObject1 != null) {
      ((AtomicInteger)localObject1).incrementAndGet();
    }
    return (AccessibilityWindowInfo)localObject2;
  }
  
  public static AccessibilityWindowInfo obtain(AccessibilityWindowInfo paramAccessibilityWindowInfo)
  {
    AccessibilityWindowInfo localAccessibilityWindowInfo = obtain();
    localAccessibilityWindowInfo.init(paramAccessibilityWindowInfo);
    return localAccessibilityWindowInfo;
  }
  
  private void setBooleanProperty(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mBooleanProperties |= paramInt;
    } else {
      this.mBooleanProperties &= paramInt;
    }
  }
  
  public static void setNumInstancesInUseCounter(AtomicInteger paramAtomicInteger)
  {
    if (sNumInstancesInUse != null) {
      sNumInstancesInUse = paramAtomicInteger;
    }
  }
  
  private static String typeToString(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 3)
        {
          if (paramInt != 4)
          {
            if (paramInt != 5) {
              return "<UNKNOWN>";
            }
            return "TYPE_SPLIT_SCREEN_DIVIDER";
          }
          return "TYPE_ACCESSIBILITY_OVERLAY";
        }
        return "TYPE_SYSTEM";
      }
      return "TYPE_INPUT_METHOD";
    }
    return "TYPE_APPLICATION";
  }
  
  public void addChild(int paramInt)
  {
    if (this.mChildIds == null) {
      this.mChildIds = new LongArray();
    }
    this.mChildIds.add(paramInt);
  }
  
  public boolean changed(AccessibilityWindowInfo paramAccessibilityWindowInfo)
  {
    if (paramAccessibilityWindowInfo.mId == this.mId)
    {
      if (paramAccessibilityWindowInfo.mType == this.mType)
      {
        if (!this.mBoundsInScreen.equals(paramAccessibilityWindowInfo.mBoundsInScreen)) {
          return true;
        }
        if (this.mLayer != paramAccessibilityWindowInfo.mLayer) {
          return true;
        }
        if (this.mBooleanProperties != paramAccessibilityWindowInfo.mBooleanProperties) {
          return true;
        }
        if (this.mParentId != paramAccessibilityWindowInfo.mParentId) {
          return true;
        }
        LongArray localLongArray = this.mChildIds;
        if (localLongArray == null)
        {
          if (paramAccessibilityWindowInfo.mChildIds != null) {
            return true;
          }
        }
        else if (!localLongArray.equals(paramAccessibilityWindowInfo.mChildIds)) {
          return true;
        }
        return false;
      }
      throw new IllegalArgumentException("Not same type.");
    }
    throw new IllegalArgumentException("Not same window.");
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int differenceFrom(AccessibilityWindowInfo paramAccessibilityWindowInfo)
  {
    if (paramAccessibilityWindowInfo.mId == this.mId)
    {
      if (paramAccessibilityWindowInfo.mType == this.mType)
      {
        int i = 0;
        if (!TextUtils.equals(this.mTitle, paramAccessibilityWindowInfo.mTitle)) {
          i = 0x0 | 0x4;
        }
        int j = i;
        if (!this.mBoundsInScreen.equals(paramAccessibilityWindowInfo.mBoundsInScreen)) {
          j = i | 0x8;
        }
        i = j;
        if (this.mLayer != paramAccessibilityWindowInfo.mLayer) {
          i = j | 0x10;
        }
        j = i;
        if (getBooleanProperty(1) != paramAccessibilityWindowInfo.getBooleanProperty(1)) {
          j = i | 0x20;
        }
        i = j;
        if (getBooleanProperty(2) != paramAccessibilityWindowInfo.getBooleanProperty(2)) {
          i = j | 0x40;
        }
        j = i;
        if (getBooleanProperty(4) != paramAccessibilityWindowInfo.getBooleanProperty(4)) {
          j = i | 0x80;
        }
        i = j;
        if (getBooleanProperty(8) != paramAccessibilityWindowInfo.getBooleanProperty(8)) {
          i = j | 0x400;
        }
        j = i;
        if (this.mParentId != paramAccessibilityWindowInfo.mParentId) {
          j = i | 0x100;
        }
        i = j;
        if (!Objects.equals(this.mChildIds, paramAccessibilityWindowInfo.mChildIds)) {
          i = j | 0x200;
        }
        return i;
      }
      throw new IllegalArgumentException("Not same type.");
    }
    throw new IllegalArgumentException("Not same window.");
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AccessibilityWindowInfo)paramObject;
    if (this.mId != ((AccessibilityWindowInfo)paramObject).mId) {
      bool = false;
    }
    return bool;
  }
  
  public AccessibilityNodeInfo getAnchor()
  {
    if ((this.mConnectionId != -1) && (this.mAnchorId != AccessibilityNodeInfo.UNDEFINED_NODE_ID) && (this.mParentId != -1)) {
      return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mParentId, this.mAnchorId, true, 0, null);
    }
    return null;
  }
  
  public void getBoundsInScreen(Rect paramRect)
  {
    paramRect.set(this.mBoundsInScreen);
  }
  
  public AccessibilityWindowInfo getChild(int paramInt)
  {
    LongArray localLongArray = this.mChildIds;
    if (localLongArray != null)
    {
      if (this.mConnectionId == -1) {
        return null;
      }
      paramInt = (int)localLongArray.get(paramInt);
      return AccessibilityInteractionClient.getInstance().getWindow(this.mConnectionId, paramInt);
    }
    throw new IndexOutOfBoundsException();
  }
  
  public int getChildCount()
  {
    LongArray localLongArray = this.mChildIds;
    int i;
    if (localLongArray != null) {
      i = localLongArray.size();
    } else {
      i = 0;
    }
    return i;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getLayer()
  {
    return this.mLayer;
  }
  
  public AccessibilityWindowInfo getParent()
  {
    if ((this.mConnectionId != -1) && (this.mParentId != -1)) {
      return AccessibilityInteractionClient.getInstance().getWindow(this.mConnectionId, this.mParentId);
    }
    return null;
  }
  
  public AccessibilityNodeInfo getRoot()
  {
    if (this.mConnectionId == -1) {
      return null;
    }
    return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mId, AccessibilityNodeInfo.ROOT_NODE_ID, true, 4, null);
  }
  
  public CharSequence getTitle()
  {
    return this.mTitle;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public int hashCode()
  {
    return this.mId;
  }
  
  public boolean isAccessibilityFocused()
  {
    return getBooleanProperty(4);
  }
  
  public boolean isActive()
  {
    return getBooleanProperty(1);
  }
  
  public boolean isFocused()
  {
    return getBooleanProperty(2);
  }
  
  public boolean isInPictureInPictureMode()
  {
    return getBooleanProperty(8);
  }
  
  public void recycle()
  {
    clear();
    sPool.release(this);
    AtomicInteger localAtomicInteger = sNumInstancesInUse;
    if (localAtomicInteger != null) {
      localAtomicInteger.decrementAndGet();
    }
  }
  
  public void setAccessibilityFocused(boolean paramBoolean)
  {
    setBooleanProperty(4, paramBoolean);
  }
  
  public void setActive(boolean paramBoolean)
  {
    setBooleanProperty(1, paramBoolean);
  }
  
  public void setAnchorId(long paramLong)
  {
    this.mAnchorId = paramLong;
  }
  
  public void setBoundsInScreen(Rect paramRect)
  {
    this.mBoundsInScreen.set(paramRect);
  }
  
  public void setConnectionId(int paramInt)
  {
    this.mConnectionId = paramInt;
  }
  
  public void setFocused(boolean paramBoolean)
  {
    setBooleanProperty(2, paramBoolean);
  }
  
  public void setId(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public void setLayer(int paramInt)
  {
    this.mLayer = paramInt;
  }
  
  public void setParentId(int paramInt)
  {
    this.mParentId = paramInt;
  }
  
  public void setPictureInPicture(boolean paramBoolean)
  {
    setBooleanProperty(8, paramBoolean);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTitle = paramCharSequence;
  }
  
  public void setType(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("AccessibilityWindowInfo[");
    localStringBuilder.append("title=");
    localStringBuilder.append(this.mTitle);
    localStringBuilder.append(", id=");
    localStringBuilder.append(this.mId);
    localStringBuilder.append(", type=");
    localStringBuilder.append(typeToString(this.mType));
    localStringBuilder.append(", layer=");
    localStringBuilder.append(this.mLayer);
    localStringBuilder.append(", bounds=");
    localStringBuilder.append(this.mBoundsInScreen);
    localStringBuilder.append(", focused=");
    localStringBuilder.append(isFocused());
    localStringBuilder.append(", active=");
    localStringBuilder.append(isActive());
    localStringBuilder.append(", pictureInPicture=");
    localStringBuilder.append(isInPictureInPictureMode());
    localStringBuilder.append(", hasParent=");
    int i = this.mParentId;
    boolean bool1 = true;
    boolean bool2;
    if (i != -1) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    localStringBuilder.append(bool2);
    localStringBuilder.append(", isAnchored=");
    if (this.mAnchorId != AccessibilityNodeInfo.UNDEFINED_NODE_ID) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    localStringBuilder.append(bool2);
    localStringBuilder.append(", hasChildren=");
    LongArray localLongArray = this.mChildIds;
    if ((localLongArray != null) && (localLongArray.size() > 0)) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    localStringBuilder.append(bool2);
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mLayer);
    paramParcel.writeInt(this.mBooleanProperties);
    paramParcel.writeInt(this.mId);
    paramParcel.writeInt(this.mParentId);
    this.mBoundsInScreen.writeToParcel(paramParcel, paramInt);
    paramParcel.writeCharSequence(this.mTitle);
    paramParcel.writeLong(this.mAnchorId);
    LongArray localLongArray = this.mChildIds;
    if (localLongArray == null)
    {
      paramParcel.writeInt(0);
    }
    else
    {
      int i = localLongArray.size();
      paramParcel.writeInt(i);
      for (paramInt = 0; paramInt < i; paramInt++) {
        paramParcel.writeInt((int)localLongArray.get(paramInt));
      }
    }
    paramParcel.writeInt(this.mConnectionId);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/AccessibilityWindowInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */