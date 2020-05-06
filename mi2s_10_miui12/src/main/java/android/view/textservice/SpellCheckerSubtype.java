package android.view.textservice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.inputmethod.SubtypeLocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class SpellCheckerSubtype
  implements Parcelable
{
  public static final Parcelable.Creator<SpellCheckerSubtype> CREATOR = new Parcelable.Creator()
  {
    public SpellCheckerSubtype createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SpellCheckerSubtype(paramAnonymousParcel);
    }
    
    public SpellCheckerSubtype[] newArray(int paramAnonymousInt)
    {
      return new SpellCheckerSubtype[paramAnonymousInt];
    }
  };
  private static final String EXTRA_VALUE_KEY_VALUE_SEPARATOR = "=";
  private static final String EXTRA_VALUE_PAIR_SEPARATOR = ",";
  public static final int SUBTYPE_ID_NONE = 0;
  private static final String SUBTYPE_LANGUAGE_TAG_NONE = "";
  private static final String TAG = SpellCheckerSubtype.class.getSimpleName();
  private HashMap<String, String> mExtraValueHashMapCache;
  private final String mSubtypeExtraValue;
  private final int mSubtypeHashCode;
  private final int mSubtypeId;
  private final String mSubtypeLanguageTag;
  private final String mSubtypeLocale;
  private final int mSubtypeNameResId;
  
  @Deprecated
  public SpellCheckerSubtype(int paramInt, String paramString1, String paramString2)
  {
    this(paramInt, paramString1, "", paramString2, 0);
  }
  
  public SpellCheckerSubtype(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2)
  {
    this.mSubtypeNameResId = paramInt1;
    String str = "";
    if (paramString1 == null) {
      paramString1 = "";
    }
    this.mSubtypeLocale = paramString1;
    if (paramString2 != null) {
      paramString1 = paramString2;
    } else {
      paramString1 = "";
    }
    this.mSubtypeLanguageTag = paramString1;
    paramString1 = str;
    if (paramString3 != null) {
      paramString1 = paramString3;
    }
    this.mSubtypeExtraValue = paramString1;
    this.mSubtypeId = paramInt2;
    paramInt1 = this.mSubtypeId;
    if (paramInt1 == 0) {
      paramInt1 = hashCodeInternal(this.mSubtypeLocale, this.mSubtypeExtraValue);
    }
    this.mSubtypeHashCode = paramInt1;
  }
  
  SpellCheckerSubtype(Parcel paramParcel)
  {
    this.mSubtypeNameResId = paramParcel.readInt();
    Object localObject = paramParcel.readString();
    String str1 = "";
    if (localObject == null) {
      localObject = "";
    }
    this.mSubtypeLocale = ((String)localObject);
    localObject = paramParcel.readString();
    if (localObject == null) {
      localObject = "";
    }
    this.mSubtypeLanguageTag = ((String)localObject);
    String str2 = paramParcel.readString();
    localObject = str1;
    if (str2 != null) {
      localObject = str2;
    }
    this.mSubtypeExtraValue = ((String)localObject);
    this.mSubtypeId = paramParcel.readInt();
    int i = this.mSubtypeId;
    if (i == 0) {
      i = hashCodeInternal(this.mSubtypeLocale, this.mSubtypeExtraValue);
    }
    this.mSubtypeHashCode = i;
  }
  
  private HashMap<String, String> getExtraValueHashMap()
  {
    if (this.mExtraValueHashMapCache == null)
    {
      this.mExtraValueHashMapCache = new HashMap();
      String[] arrayOfString1 = this.mSubtypeExtraValue.split(",");
      int i = arrayOfString1.length;
      for (int j = 0; j < i; j++)
      {
        String[] arrayOfString2 = arrayOfString1[j].split("=");
        if (arrayOfString2.length == 1)
        {
          this.mExtraValueHashMapCache.put(arrayOfString2[0], null);
        }
        else if (arrayOfString2.length > 1)
        {
          if (arrayOfString2.length > 2) {
            Slog.w(TAG, "ExtraValue has two or more '='s");
          }
          this.mExtraValueHashMapCache.put(arrayOfString2[0], arrayOfString2[1]);
        }
      }
    }
    return this.mExtraValueHashMapCache;
  }
  
  private static int hashCodeInternal(String paramString1, String paramString2)
  {
    return Arrays.hashCode(new Object[] { paramString1, paramString2 });
  }
  
  public static List<SpellCheckerSubtype> sort(Context paramContext, int paramInt, SpellCheckerInfo paramSpellCheckerInfo, List<SpellCheckerSubtype> paramList)
  {
    if (paramSpellCheckerInfo == null) {
      return paramList;
    }
    paramList = new HashSet(paramList);
    paramContext = new ArrayList();
    int i = paramSpellCheckerInfo.getSubtypeCount();
    for (paramInt = 0; paramInt < i; paramInt++)
    {
      SpellCheckerSubtype localSpellCheckerSubtype = paramSpellCheckerInfo.getSubtypeAt(paramInt);
      if (paramList.contains(localSpellCheckerSubtype))
      {
        paramContext.add(localSpellCheckerSubtype);
        paramList.remove(localSpellCheckerSubtype);
      }
    }
    paramSpellCheckerInfo = paramList.iterator();
    while (paramSpellCheckerInfo.hasNext()) {
      paramContext.add((SpellCheckerSubtype)paramSpellCheckerInfo.next());
    }
    return paramContext;
  }
  
  public boolean containsExtraValueKey(String paramString)
  {
    return getExtraValueHashMap().containsKey(paramString);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof SpellCheckerSubtype;
    boolean bool2 = false;
    boolean bool3 = false;
    if (bool1)
    {
      paramObject = (SpellCheckerSubtype)paramObject;
      if ((((SpellCheckerSubtype)paramObject).mSubtypeId == 0) && (this.mSubtypeId == 0))
      {
        if ((((SpellCheckerSubtype)paramObject).hashCode() == hashCode()) && (((SpellCheckerSubtype)paramObject).getNameResId() == getNameResId()) && (((SpellCheckerSubtype)paramObject).getLocale().equals(getLocale())) && (((SpellCheckerSubtype)paramObject).getLanguageTag().equals(getLanguageTag())) && (((SpellCheckerSubtype)paramObject).getExtraValue().equals(getExtraValue()))) {
          bool3 = true;
        }
        return bool3;
      }
      bool3 = bool2;
      if (((SpellCheckerSubtype)paramObject).hashCode() == hashCode()) {
        bool3 = true;
      }
      return bool3;
    }
    return false;
  }
  
  public CharSequence getDisplayName(Context paramContext, String paramString, ApplicationInfo paramApplicationInfo)
  {
    Object localObject = getLocaleObject();
    if (localObject != null) {
      localObject = ((Locale)localObject).getDisplayName();
    } else {
      localObject = this.mSubtypeLocale;
    }
    if (this.mSubtypeNameResId == 0) {
      return (CharSequence)localObject;
    }
    paramContext = paramContext.getPackageManager().getText(paramString, this.mSubtypeNameResId, paramApplicationInfo);
    if (!TextUtils.isEmpty(paramContext)) {
      return String.format(paramContext.toString(), new Object[] { localObject });
    }
    return (CharSequence)localObject;
  }
  
  public String getExtraValue()
  {
    return this.mSubtypeExtraValue;
  }
  
  public String getExtraValueOf(String paramString)
  {
    return (String)getExtraValueHashMap().get(paramString);
  }
  
  public String getLanguageTag()
  {
    return this.mSubtypeLanguageTag;
  }
  
  @Deprecated
  public String getLocale()
  {
    return this.mSubtypeLocale;
  }
  
  public Locale getLocaleObject()
  {
    if (!TextUtils.isEmpty(this.mSubtypeLanguageTag)) {
      return Locale.forLanguageTag(this.mSubtypeLanguageTag);
    }
    return SubtypeLocaleUtils.constructLocaleFromString(this.mSubtypeLocale);
  }
  
  public int getNameResId()
  {
    return this.mSubtypeNameResId;
  }
  
  public int hashCode()
  {
    return this.mSubtypeHashCode;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSubtypeNameResId);
    paramParcel.writeString(this.mSubtypeLocale);
    paramParcel.writeString(this.mSubtypeLanguageTag);
    paramParcel.writeString(this.mSubtypeExtraValue);
    paramParcel.writeInt(this.mSubtypeId);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textservice/SpellCheckerSubtype.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */