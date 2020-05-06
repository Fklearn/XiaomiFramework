package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class KeyboardShortcutGroup
  implements Parcelable
{
  public static final Parcelable.Creator<KeyboardShortcutGroup> CREATOR = new Parcelable.Creator()
  {
    public KeyboardShortcutGroup createFromParcel(Parcel paramAnonymousParcel)
    {
      return new KeyboardShortcutGroup(paramAnonymousParcel, null);
    }
    
    public KeyboardShortcutGroup[] newArray(int paramAnonymousInt)
    {
      return new KeyboardShortcutGroup[paramAnonymousInt];
    }
  };
  private final List<KeyboardShortcutInfo> mItems;
  private final CharSequence mLabel;
  private boolean mSystemGroup;
  
  private KeyboardShortcutGroup(Parcel paramParcel)
  {
    this.mItems = new ArrayList();
    this.mLabel = paramParcel.readCharSequence();
    paramParcel.readTypedList(this.mItems, KeyboardShortcutInfo.CREATOR);
    int i = paramParcel.readInt();
    boolean bool = true;
    if (i != 1) {
      bool = false;
    }
    this.mSystemGroup = bool;
  }
  
  public KeyboardShortcutGroup(CharSequence paramCharSequence)
  {
    this(paramCharSequence, Collections.emptyList());
  }
  
  public KeyboardShortcutGroup(CharSequence paramCharSequence, List<KeyboardShortcutInfo> paramList)
  {
    this.mLabel = paramCharSequence;
    this.mItems = new ArrayList((Collection)Preconditions.checkNotNull(paramList));
  }
  
  public KeyboardShortcutGroup(CharSequence paramCharSequence, List<KeyboardShortcutInfo> paramList, boolean paramBoolean)
  {
    this.mLabel = paramCharSequence;
    this.mItems = new ArrayList((Collection)Preconditions.checkNotNull(paramList));
    this.mSystemGroup = paramBoolean;
  }
  
  public KeyboardShortcutGroup(CharSequence paramCharSequence, boolean paramBoolean)
  {
    this(paramCharSequence, Collections.emptyList(), paramBoolean);
  }
  
  public void addItem(KeyboardShortcutInfo paramKeyboardShortcutInfo)
  {
    this.mItems.add(paramKeyboardShortcutInfo);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public List<KeyboardShortcutInfo> getItems()
  {
    return this.mItems;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public boolean isSystemGroup()
  {
    return this.mSystemGroup;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeCharSequence(this.mLabel);
    paramParcel.writeTypedList(this.mItems);
    paramParcel.writeInt(this.mSystemGroup);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/KeyboardShortcutGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */