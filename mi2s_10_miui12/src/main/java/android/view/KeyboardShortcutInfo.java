package android.view;

import android.graphics.drawable.Icon;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;

public final class KeyboardShortcutInfo
  implements Parcelable
{
  public static final Parcelable.Creator<KeyboardShortcutInfo> CREATOR = new Parcelable.Creator()
  {
    public KeyboardShortcutInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new KeyboardShortcutInfo(paramAnonymousParcel, null);
    }
    
    public KeyboardShortcutInfo[] newArray(int paramAnonymousInt)
    {
      return new KeyboardShortcutInfo[paramAnonymousInt];
    }
  };
  private final char mBaseCharacter;
  private final Icon mIcon;
  private final int mKeycode;
  private final CharSequence mLabel;
  private final int mModifiers;
  
  private KeyboardShortcutInfo(Parcel paramParcel)
  {
    this.mLabel = paramParcel.readCharSequence();
    this.mIcon = ((Icon)paramParcel.readParcelable(null));
    this.mBaseCharacter = ((char)(char)paramParcel.readInt());
    this.mKeycode = paramParcel.readInt();
    this.mModifiers = paramParcel.readInt();
  }
  
  public KeyboardShortcutInfo(CharSequence paramCharSequence, char paramChar, int paramInt)
  {
    this.mLabel = paramCharSequence;
    boolean bool;
    if (paramChar != 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    this.mBaseCharacter = ((char)paramChar);
    this.mKeycode = 0;
    this.mModifiers = paramInt;
    this.mIcon = null;
  }
  
  public KeyboardShortcutInfo(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    this(paramCharSequence, null, paramInt1, paramInt2);
  }
  
  public KeyboardShortcutInfo(CharSequence paramCharSequence, Icon paramIcon, int paramInt1, int paramInt2)
  {
    this.mLabel = paramCharSequence;
    this.mIcon = paramIcon;
    boolean bool1 = false;
    this.mBaseCharacter = ((char)0);
    boolean bool2 = bool1;
    if (paramInt1 >= 0)
    {
      bool2 = bool1;
      if (paramInt1 <= KeyEvent.getMaxKeyCode()) {
        bool2 = true;
      }
    }
    Preconditions.checkArgument(bool2);
    this.mKeycode = paramInt1;
    this.mModifiers = paramInt2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public char getBaseCharacter()
  {
    return this.mBaseCharacter;
  }
  
  public Icon getIcon()
  {
    return this.mIcon;
  }
  
  public int getKeycode()
  {
    return this.mKeycode;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public int getModifiers()
  {
    return this.mModifiers;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeCharSequence(this.mLabel);
    paramParcel.writeParcelable(this.mIcon, 0);
    paramParcel.writeInt(this.mBaseCharacter);
    paramParcel.writeInt(this.mKeycode);
    paramParcel.writeInt(this.mModifiers);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/KeyboardShortcutInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */