package android.view;

import android.graphics.Rect;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Pools.SynchronizedPool;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.List;

public class WindowInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WindowInfo> CREATOR = new Parcelable.Creator()
  {
    public WindowInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      WindowInfo localWindowInfo = WindowInfo.obtain();
      localWindowInfo.initFromParcel(paramAnonymousParcel);
      return localWindowInfo;
    }
    
    public WindowInfo[] newArray(int paramAnonymousInt)
    {
      return new WindowInfo[paramAnonymousInt];
    }
  };
  private static final int MAX_POOL_SIZE = 10;
  private static final Pools.SynchronizedPool<WindowInfo> sPool = new Pools.SynchronizedPool(10);
  public long accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
  public IBinder activityToken;
  public final Rect boundsInScreen = new Rect();
  public List<IBinder> childTokens;
  public boolean focused;
  public boolean hasFlagWatchOutsideTouch;
  public boolean inPictureInPicture;
  public int layer;
  public IBinder parentToken;
  public CharSequence title;
  public IBinder token;
  public int type;
  
  private void clear()
  {
    this.type = 0;
    this.layer = 0;
    this.token = null;
    this.parentToken = null;
    this.activityToken = null;
    this.focused = false;
    this.boundsInScreen.setEmpty();
    List localList = this.childTokens;
    if (localList != null) {
      localList.clear();
    }
    this.inPictureInPicture = false;
    this.hasFlagWatchOutsideTouch = false;
  }
  
  private void initFromParcel(Parcel paramParcel)
  {
    this.type = paramParcel.readInt();
    this.layer = paramParcel.readInt();
    this.token = paramParcel.readStrongBinder();
    this.parentToken = paramParcel.readStrongBinder();
    this.activityToken = paramParcel.readStrongBinder();
    int i = paramParcel.readInt();
    int j = 0;
    boolean bool;
    if (i == 1) {
      bool = true;
    } else {
      bool = false;
    }
    this.focused = bool;
    this.boundsInScreen.readFromParcel(paramParcel);
    this.title = paramParcel.readCharSequence();
    this.accessibilityIdOfAnchor = paramParcel.readLong();
    if (paramParcel.readInt() == 1) {
      bool = true;
    } else {
      bool = false;
    }
    this.inPictureInPicture = bool;
    if (paramParcel.readInt() == 1) {
      bool = true;
    } else {
      bool = false;
    }
    this.hasFlagWatchOutsideTouch = bool;
    if (paramParcel.readInt() == 1) {
      j = 1;
    }
    if (j != 0)
    {
      if (this.childTokens == null) {
        this.childTokens = new ArrayList();
      }
      paramParcel.readBinderList(this.childTokens);
    }
  }
  
  public static WindowInfo obtain()
  {
    WindowInfo localWindowInfo1 = (WindowInfo)sPool.acquire();
    WindowInfo localWindowInfo2 = localWindowInfo1;
    if (localWindowInfo1 == null) {
      localWindowInfo2 = new WindowInfo();
    }
    return localWindowInfo2;
  }
  
  public static WindowInfo obtain(WindowInfo paramWindowInfo)
  {
    WindowInfo localWindowInfo = obtain();
    localWindowInfo.type = paramWindowInfo.type;
    localWindowInfo.layer = paramWindowInfo.layer;
    localWindowInfo.token = paramWindowInfo.token;
    localWindowInfo.parentToken = paramWindowInfo.parentToken;
    localWindowInfo.activityToken = paramWindowInfo.activityToken;
    localWindowInfo.focused = paramWindowInfo.focused;
    localWindowInfo.boundsInScreen.set(paramWindowInfo.boundsInScreen);
    localWindowInfo.title = paramWindowInfo.title;
    localWindowInfo.accessibilityIdOfAnchor = paramWindowInfo.accessibilityIdOfAnchor;
    localWindowInfo.inPictureInPicture = paramWindowInfo.inPictureInPicture;
    localWindowInfo.hasFlagWatchOutsideTouch = paramWindowInfo.hasFlagWatchOutsideTouch;
    List localList = paramWindowInfo.childTokens;
    if ((localList != null) && (!localList.isEmpty()))
    {
      localList = localWindowInfo.childTokens;
      if (localList == null) {
        localWindowInfo.childTokens = new ArrayList(paramWindowInfo.childTokens);
      } else {
        localList.addAll(paramWindowInfo.childTokens);
      }
    }
    return localWindowInfo;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void recycle()
  {
    clear();
    sPool.release(this);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("WindowInfo[");
    localStringBuilder.append("title=");
    localStringBuilder.append(this.title);
    localStringBuilder.append(", type=");
    localStringBuilder.append(this.type);
    localStringBuilder.append(", layer=");
    localStringBuilder.append(this.layer);
    localStringBuilder.append(", token=");
    localStringBuilder.append(this.token);
    localStringBuilder.append(", bounds=");
    localStringBuilder.append(this.boundsInScreen);
    localStringBuilder.append(", parent=");
    localStringBuilder.append(this.parentToken);
    localStringBuilder.append(", focused=");
    localStringBuilder.append(this.focused);
    localStringBuilder.append(", children=");
    localStringBuilder.append(this.childTokens);
    localStringBuilder.append(", accessibility anchor=");
    localStringBuilder.append(this.accessibilityIdOfAnchor);
    localStringBuilder.append(", pictureInPicture=");
    localStringBuilder.append(this.inPictureInPicture);
    localStringBuilder.append(", watchOutsideTouch=");
    localStringBuilder.append(this.hasFlagWatchOutsideTouch);
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.type);
    paramParcel.writeInt(this.layer);
    paramParcel.writeStrongBinder(this.token);
    paramParcel.writeStrongBinder(this.parentToken);
    paramParcel.writeStrongBinder(this.activityToken);
    paramParcel.writeInt(this.focused);
    this.boundsInScreen.writeToParcel(paramParcel, paramInt);
    paramParcel.writeCharSequence(this.title);
    paramParcel.writeLong(this.accessibilityIdOfAnchor);
    paramParcel.writeInt(this.inPictureInPicture);
    paramParcel.writeInt(this.hasFlagWatchOutsideTouch);
    List localList = this.childTokens;
    if ((localList != null) && (!localList.isEmpty()))
    {
      paramParcel.writeInt(1);
      paramParcel.writeBinderList(this.childTokens);
    }
    else
    {
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */