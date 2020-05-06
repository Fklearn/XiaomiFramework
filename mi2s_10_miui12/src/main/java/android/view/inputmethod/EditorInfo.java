package android.view.inputmethod;

import android.os.Bundle;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Printer;
import java.util.Arrays;

public class EditorInfo
  implements InputType, Parcelable
{
  public static final Parcelable.Creator<EditorInfo> CREATOR = new Parcelable.Creator()
  {
    public EditorInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      EditorInfo localEditorInfo = new EditorInfo();
      localEditorInfo.inputType = paramAnonymousParcel.readInt();
      localEditorInfo.imeOptions = paramAnonymousParcel.readInt();
      localEditorInfo.privateImeOptions = paramAnonymousParcel.readString();
      localEditorInfo.actionLabel = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramAnonymousParcel));
      localEditorInfo.actionId = paramAnonymousParcel.readInt();
      localEditorInfo.initialSelStart = paramAnonymousParcel.readInt();
      localEditorInfo.initialSelEnd = paramAnonymousParcel.readInt();
      localEditorInfo.initialCapsMode = paramAnonymousParcel.readInt();
      localEditorInfo.hintText = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramAnonymousParcel));
      localEditorInfo.label = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramAnonymousParcel));
      localEditorInfo.packageName = paramAnonymousParcel.readString();
      localEditorInfo.fieldId = paramAnonymousParcel.readInt();
      localEditorInfo.fieldName = paramAnonymousParcel.readString();
      localEditorInfo.extras = paramAnonymousParcel.readBundle();
      LocaleList localLocaleList = (LocaleList)LocaleList.CREATOR.createFromParcel(paramAnonymousParcel);
      if (localLocaleList.isEmpty()) {
        localLocaleList = null;
      }
      localEditorInfo.hintLocales = localLocaleList;
      localEditorInfo.contentMimeTypes = paramAnonymousParcel.readStringArray();
      localEditorInfo.targetInputMethodUser = UserHandle.readFromParcel(paramAnonymousParcel);
      return localEditorInfo;
    }
    
    public EditorInfo[] newArray(int paramAnonymousInt)
    {
      return new EditorInfo[paramAnonymousInt];
    }
  };
  public static final int IME_ACTION_DONE = 6;
  public static final int IME_ACTION_GO = 2;
  public static final int IME_ACTION_NEXT = 5;
  public static final int IME_ACTION_NONE = 1;
  public static final int IME_ACTION_PREVIOUS = 7;
  public static final int IME_ACTION_SEARCH = 3;
  public static final int IME_ACTION_SEND = 4;
  public static final int IME_ACTION_UNSPECIFIED = 0;
  public static final int IME_FLAG_FORCE_ASCII = Integer.MIN_VALUE;
  public static final int IME_FLAG_NAVIGATE_NEXT = 134217728;
  public static final int IME_FLAG_NAVIGATE_PREVIOUS = 67108864;
  public static final int IME_FLAG_NO_ACCESSORY_ACTION = 536870912;
  public static final int IME_FLAG_NO_ENTER_ACTION = 1073741824;
  public static final int IME_FLAG_NO_EXTRACT_UI = 268435456;
  public static final int IME_FLAG_NO_FULLSCREEN = 33554432;
  public static final int IME_FLAG_NO_PERSONALIZED_LEARNING = 16777216;
  public static final int IME_MASK_ACTION = 255;
  public static final int IME_NULL = 0;
  public int actionId = 0;
  public CharSequence actionLabel = null;
  public String[] contentMimeTypes = null;
  public Bundle extras;
  public int fieldId;
  public String fieldName;
  public LocaleList hintLocales = null;
  public CharSequence hintText;
  public int imeOptions = 0;
  public int initialCapsMode = 0;
  public int initialSelEnd = -1;
  public int initialSelStart = -1;
  public int inputType = 0;
  public CharSequence label;
  public String packageName;
  public String privateImeOptions = null;
  public UserHandle targetInputMethodUser = null;
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("inputType=0x");
    localStringBuilder.append(Integer.toHexString(this.inputType));
    localStringBuilder.append(" imeOptions=0x");
    localStringBuilder.append(Integer.toHexString(this.imeOptions));
    localStringBuilder.append(" privateImeOptions=");
    localStringBuilder.append(this.privateImeOptions);
    paramPrinter.println(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("actionLabel=");
    localStringBuilder.append(this.actionLabel);
    localStringBuilder.append(" actionId=");
    localStringBuilder.append(this.actionId);
    paramPrinter.println(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("initialSelStart=");
    localStringBuilder.append(this.initialSelStart);
    localStringBuilder.append(" initialSelEnd=");
    localStringBuilder.append(this.initialSelEnd);
    localStringBuilder.append(" initialCapsMode=0x");
    localStringBuilder.append(Integer.toHexString(this.initialCapsMode));
    paramPrinter.println(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("hintText=");
    localStringBuilder.append(this.hintText);
    localStringBuilder.append(" label=");
    localStringBuilder.append(this.label);
    paramPrinter.println(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("packageName=");
    localStringBuilder.append(this.packageName);
    localStringBuilder.append(" fieldId=");
    localStringBuilder.append(this.fieldId);
    localStringBuilder.append(" fieldName=");
    localStringBuilder.append(this.fieldName);
    paramPrinter.println(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("extras=");
    localStringBuilder.append(this.extras);
    paramPrinter.println(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("hintLocales=");
    localStringBuilder.append(this.hintLocales);
    paramPrinter.println(localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("contentMimeTypes=");
    localStringBuilder.append(Arrays.toString(this.contentMimeTypes));
    paramPrinter.println(localStringBuilder.toString());
    if (this.targetInputMethodUser != null)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("targetInputMethodUserId=");
      localStringBuilder.append(this.targetInputMethodUser.getIdentifier());
      paramPrinter.println(localStringBuilder.toString());
    }
  }
  
  public final void makeCompatible(int paramInt)
  {
    if (paramInt < 11)
    {
      paramInt = this.inputType;
      int i = paramInt & 0xFFF;
      if ((i != 2) && (i != 18))
      {
        if (i != 209)
        {
          if (i == 225) {
            this.inputType = (paramInt & 0xFFF000 | 0x81);
          }
        }
        else {
          this.inputType = (paramInt & 0xFFF000 | 0x21);
        }
      }
      else {
        this.inputType = (this.inputType & 0xFFF000 | 0x2);
      }
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.inputType);
    paramParcel.writeInt(this.imeOptions);
    paramParcel.writeString(this.privateImeOptions);
    TextUtils.writeToParcel(this.actionLabel, paramParcel, paramInt);
    paramParcel.writeInt(this.actionId);
    paramParcel.writeInt(this.initialSelStart);
    paramParcel.writeInt(this.initialSelEnd);
    paramParcel.writeInt(this.initialCapsMode);
    TextUtils.writeToParcel(this.hintText, paramParcel, paramInt);
    TextUtils.writeToParcel(this.label, paramParcel, paramInt);
    paramParcel.writeString(this.packageName);
    paramParcel.writeInt(this.fieldId);
    paramParcel.writeString(this.fieldName);
    paramParcel.writeBundle(this.extras);
    LocaleList localLocaleList = this.hintLocales;
    if (localLocaleList != null) {
      localLocaleList.writeToParcel(paramParcel, paramInt);
    } else {
      LocaleList.getEmptyLocaleList().writeToParcel(paramParcel, paramInt);
    }
    paramParcel.writeStringArray(this.contentMimeTypes);
    UserHandle.writeToParcel(this.targetInputMethodUser, paramParcel);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/EditorInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */