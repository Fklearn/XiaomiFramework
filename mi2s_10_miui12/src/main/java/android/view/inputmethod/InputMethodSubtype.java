package android.view.inputmethod;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.text.DisplayContext;
import android.icu.text.LocaleDisplayNames;
import android.os.LocaleList;
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
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class InputMethodSubtype
  implements Parcelable
{
  public static final Parcelable.Creator<InputMethodSubtype> CREATOR = new Parcelable.Creator()
  {
    public InputMethodSubtype createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InputMethodSubtype(paramAnonymousParcel);
    }
    
    public InputMethodSubtype[] newArray(int paramAnonymousInt)
    {
      return new InputMethodSubtype[paramAnonymousInt];
    }
  };
  private static final String EXTRA_KEY_UNTRANSLATABLE_STRING_IN_SUBTYPE_NAME = "UntranslatableReplacementStringInSubtypeName";
  private static final String EXTRA_VALUE_KEY_VALUE_SEPARATOR = "=";
  private static final String EXTRA_VALUE_PAIR_SEPARATOR = ",";
  private static final String LANGUAGE_TAG_NONE = "";
  private static final int SUBTYPE_ID_NONE = 0;
  private static final String TAG = InputMethodSubtype.class.getSimpleName();
  private volatile Locale mCachedLocaleObj;
  private volatile HashMap<String, String> mExtraValueHashMapCache;
  private final boolean mIsAsciiCapable;
  private final boolean mIsAuxiliary;
  private final Object mLock = new Object();
  private final boolean mOverridesImplicitlyEnabledSubtype;
  private final String mSubtypeExtraValue;
  private final int mSubtypeHashCode;
  private final int mSubtypeIconResId;
  private final int mSubtypeId;
  private final String mSubtypeLanguageTag;
  private final String mSubtypeLocale;
  private final String mSubtypeMode;
  private final int mSubtypeNameResId;
  
  @Deprecated
  public InputMethodSubtype(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, 0);
  }
  
  @Deprecated
  public InputMethodSubtype(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, int paramInt3)
  {
    this(getBuilder(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, paramInt3, false));
  }
  
  InputMethodSubtype(Parcel paramParcel)
  {
    this.mSubtypeNameResId = paramParcel.readInt();
    this.mSubtypeIconResId = paramParcel.readInt();
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
    localObject = paramParcel.readString();
    if (localObject == null) {
      localObject = "";
    }
    this.mSubtypeMode = ((String)localObject);
    String str2 = paramParcel.readString();
    localObject = str1;
    if (str2 != null) {
      localObject = str2;
    }
    this.mSubtypeExtraValue = ((String)localObject);
    int i = paramParcel.readInt();
    boolean bool1 = false;
    if (i == 1) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mIsAuxiliary = bool2;
    if (paramParcel.readInt() == 1) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mOverridesImplicitlyEnabledSubtype = bool2;
    this.mSubtypeHashCode = paramParcel.readInt();
    this.mSubtypeId = paramParcel.readInt();
    boolean bool2 = bool1;
    if (paramParcel.readInt() == 1) {
      bool2 = true;
    }
    this.mIsAsciiCapable = bool2;
  }
  
  private InputMethodSubtype(InputMethodSubtypeBuilder paramInputMethodSubtypeBuilder)
  {
    this.mSubtypeNameResId = paramInputMethodSubtypeBuilder.mSubtypeNameResId;
    this.mSubtypeIconResId = paramInputMethodSubtypeBuilder.mSubtypeIconResId;
    this.mSubtypeLocale = paramInputMethodSubtypeBuilder.mSubtypeLocale;
    this.mSubtypeLanguageTag = paramInputMethodSubtypeBuilder.mSubtypeLanguageTag;
    this.mSubtypeMode = paramInputMethodSubtypeBuilder.mSubtypeMode;
    this.mSubtypeExtraValue = paramInputMethodSubtypeBuilder.mSubtypeExtraValue;
    this.mIsAuxiliary = paramInputMethodSubtypeBuilder.mIsAuxiliary;
    this.mOverridesImplicitlyEnabledSubtype = paramInputMethodSubtypeBuilder.mOverridesImplicitlyEnabledSubtype;
    this.mSubtypeId = paramInputMethodSubtypeBuilder.mSubtypeId;
    this.mIsAsciiCapable = paramInputMethodSubtypeBuilder.mIsAsciiCapable;
    int i = this.mSubtypeId;
    if (i != 0) {
      this.mSubtypeHashCode = i;
    } else {
      this.mSubtypeHashCode = hashCodeInternal(this.mSubtypeLocale, this.mSubtypeMode, this.mSubtypeExtraValue, this.mIsAuxiliary, this.mOverridesImplicitlyEnabledSubtype, this.mIsAsciiCapable);
    }
  }
  
  private static InputMethodSubtypeBuilder getBuilder(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, boolean paramBoolean3)
  {
    InputMethodSubtypeBuilder localInputMethodSubtypeBuilder = new InputMethodSubtypeBuilder();
    InputMethodSubtypeBuilder.access$102(localInputMethodSubtypeBuilder, paramInt1);
    InputMethodSubtypeBuilder.access$202(localInputMethodSubtypeBuilder, paramInt2);
    InputMethodSubtypeBuilder.access$302(localInputMethodSubtypeBuilder, paramString1);
    InputMethodSubtypeBuilder.access$402(localInputMethodSubtypeBuilder, paramString2);
    InputMethodSubtypeBuilder.access$502(localInputMethodSubtypeBuilder, paramString3);
    InputMethodSubtypeBuilder.access$602(localInputMethodSubtypeBuilder, paramBoolean1);
    InputMethodSubtypeBuilder.access$702(localInputMethodSubtypeBuilder, paramBoolean2);
    InputMethodSubtypeBuilder.access$802(localInputMethodSubtypeBuilder, paramInt3);
    InputMethodSubtypeBuilder.access$902(localInputMethodSubtypeBuilder, paramBoolean3);
    return localInputMethodSubtypeBuilder;
  }
  
  private HashMap<String, String> getExtraValueHashMap()
  {
    try
    {
      HashMap localHashMap = this.mExtraValueHashMapCache;
      if (localHashMap != null) {
        return localHashMap;
      }
      localHashMap = new java/util/HashMap;
      localHashMap.<init>();
      String[] arrayOfString1 = this.mSubtypeExtraValue.split(",");
      for (int i = 0; i < arrayOfString1.length; i++)
      {
        String[] arrayOfString2 = arrayOfString1[i].split("=");
        if (arrayOfString2.length == 1)
        {
          localHashMap.put(arrayOfString2[0], null);
        }
        else if (arrayOfString2.length > 1)
        {
          if (arrayOfString2.length > 2) {
            Slog.w(TAG, "ExtraValue has two or more '='s");
          }
          localHashMap.put(arrayOfString2[0], arrayOfString2[1]);
        }
      }
      this.mExtraValueHashMapCache = localHashMap;
      return localHashMap;
    }
    finally {}
  }
  
  private static String getLocaleDisplayName(Locale paramLocale1, Locale paramLocale2, DisplayContext paramDisplayContext)
  {
    if (paramLocale2 == null) {
      return "";
    }
    if (paramLocale1 == null) {
      paramLocale1 = Locale.getDefault();
    }
    return LocaleDisplayNames.getInstance(paramLocale1, new DisplayContext[] { paramDisplayContext }).localeDisplayName(paramLocale2);
  }
  
  private static Locale getLocaleFromContext(Context paramContext)
  {
    if (paramContext == null) {
      return null;
    }
    if (paramContext.getResources() == null) {
      return null;
    }
    paramContext = paramContext.getResources().getConfiguration();
    if (paramContext == null) {
      return null;
    }
    return paramContext.getLocales().get(0);
  }
  
  private static int hashCodeInternal(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((paramBoolean3 ^ true)) {
      return Arrays.hashCode(new Object[] { paramString1, paramString2, paramString3, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2) });
    }
    return Arrays.hashCode(new Object[] { paramString1, paramString2, paramString3, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2), Boolean.valueOf(paramBoolean3) });
  }
  
  public static List<InputMethodSubtype> sort(Context paramContext, int paramInt, InputMethodInfo paramInputMethodInfo, List<InputMethodSubtype> paramList)
  {
    if (paramInputMethodInfo == null) {
      return paramList;
    }
    paramList = new HashSet(paramList);
    paramContext = new ArrayList();
    int i = paramInputMethodInfo.getSubtypeCount();
    for (paramInt = 0; paramInt < i; paramInt++)
    {
      InputMethodSubtype localInputMethodSubtype = paramInputMethodInfo.getSubtypeAt(paramInt);
      if (paramList.contains(localInputMethodSubtype))
      {
        paramContext.add(localInputMethodSubtype);
        paramList.remove(localInputMethodSubtype);
      }
    }
    paramInputMethodInfo = paramList.iterator();
    while (paramInputMethodInfo.hasNext()) {
      paramContext.add((InputMethodSubtype)paramInputMethodInfo.next());
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
    boolean bool1 = paramObject instanceof InputMethodSubtype;
    boolean bool2 = false;
    boolean bool3 = false;
    if (bool1)
    {
      paramObject = (InputMethodSubtype)paramObject;
      if ((((InputMethodSubtype)paramObject).mSubtypeId == 0) && (this.mSubtypeId == 0))
      {
        if ((((InputMethodSubtype)paramObject).hashCode() == hashCode()) && (((InputMethodSubtype)paramObject).getLocale().equals(getLocale())) && (((InputMethodSubtype)paramObject).getLanguageTag().equals(getLanguageTag())) && (((InputMethodSubtype)paramObject).getMode().equals(getMode())) && (((InputMethodSubtype)paramObject).getExtraValue().equals(getExtraValue())) && (((InputMethodSubtype)paramObject).isAuxiliary() == isAuxiliary()) && (((InputMethodSubtype)paramObject).overridesImplicitlyEnabledSubtype() == overridesImplicitlyEnabledSubtype()) && (((InputMethodSubtype)paramObject).isAsciiCapable() == isAsciiCapable())) {
          bool3 = true;
        }
        return bool3;
      }
      bool3 = bool2;
      if (((InputMethodSubtype)paramObject).hashCode() == hashCode()) {
        bool3 = true;
      }
      return bool3;
    }
    return false;
  }
  
  public CharSequence getDisplayName(Context paramContext, String paramString, ApplicationInfo paramApplicationInfo)
  {
    if (this.mSubtypeNameResId == 0) {
      return getLocaleDisplayName(getLocaleFromContext(paramContext), getLocaleObject(), DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU);
    }
    paramApplicationInfo = paramContext.getPackageManager().getText(paramString, this.mSubtypeNameResId, paramApplicationInfo);
    if (TextUtils.isEmpty(paramApplicationInfo)) {
      return "";
    }
    String str = paramApplicationInfo.toString();
    if (containsExtraValueKey("UntranslatableReplacementStringInSubtypeName"))
    {
      paramContext = getExtraValueOf("UntranslatableReplacementStringInSubtypeName");
    }
    else
    {
      if (TextUtils.equals(str, "%s")) {
        paramString = DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU;
      } else if (str.startsWith("%s")) {
        paramString = DisplayContext.CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE;
      } else {
        paramString = DisplayContext.CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE;
      }
      paramContext = getLocaleDisplayName(getLocaleFromContext(paramContext), getLocaleObject(), paramString);
    }
    paramString = paramContext;
    if (paramContext == null) {
      paramString = "";
    }
    try
    {
      paramContext = String.format(str, new Object[] { paramString });
      return paramContext;
    }
    catch (IllegalFormatException paramString)
    {
      str = TAG;
      paramContext = new StringBuilder();
      paramContext.append("Found illegal format in subtype name(");
      paramContext.append(paramApplicationInfo);
      paramContext.append("): ");
      paramContext.append(paramString);
      Slog.w(str, paramContext.toString());
    }
    return "";
  }
  
  public String getExtraValue()
  {
    return this.mSubtypeExtraValue;
  }
  
  public String getExtraValueOf(String paramString)
  {
    return (String)getExtraValueHashMap().get(paramString);
  }
  
  public int getIconResId()
  {
    return this.mSubtypeIconResId;
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
    if (this.mCachedLocaleObj != null) {
      return this.mCachedLocaleObj;
    }
    synchronized (this.mLock)
    {
      if (this.mCachedLocaleObj != null)
      {
        localLocale = this.mCachedLocaleObj;
        return localLocale;
      }
      if (!TextUtils.isEmpty(this.mSubtypeLanguageTag)) {
        this.mCachedLocaleObj = Locale.forLanguageTag(this.mSubtypeLanguageTag);
      } else {
        this.mCachedLocaleObj = SubtypeLocaleUtils.constructLocaleFromString(this.mSubtypeLocale);
      }
      Locale localLocale = this.mCachedLocaleObj;
      return localLocale;
    }
  }
  
  public String getMode()
  {
    return this.mSubtypeMode;
  }
  
  public int getNameResId()
  {
    return this.mSubtypeNameResId;
  }
  
  public final int getSubtypeId()
  {
    return this.mSubtypeId;
  }
  
  public final boolean hasSubtypeId()
  {
    boolean bool;
    if (this.mSubtypeId != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public int hashCode()
  {
    return this.mSubtypeHashCode;
  }
  
  public boolean isAsciiCapable()
  {
    return this.mIsAsciiCapable;
  }
  
  public boolean isAuxiliary()
  {
    return this.mIsAuxiliary;
  }
  
  public boolean overridesImplicitlyEnabledSubtype()
  {
    return this.mOverridesImplicitlyEnabledSubtype;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSubtypeNameResId);
    paramParcel.writeInt(this.mSubtypeIconResId);
    paramParcel.writeString(this.mSubtypeLocale);
    paramParcel.writeString(this.mSubtypeLanguageTag);
    paramParcel.writeString(this.mSubtypeMode);
    paramParcel.writeString(this.mSubtypeExtraValue);
    paramParcel.writeInt(this.mIsAuxiliary);
    paramParcel.writeInt(this.mOverridesImplicitlyEnabledSubtype);
    paramParcel.writeInt(this.mSubtypeHashCode);
    paramParcel.writeInt(this.mSubtypeId);
    paramParcel.writeInt(this.mIsAsciiCapable);
  }
  
  public static class InputMethodSubtypeBuilder
  {
    private boolean mIsAsciiCapable = false;
    private boolean mIsAuxiliary = false;
    private boolean mOverridesImplicitlyEnabledSubtype = false;
    private String mSubtypeExtraValue = "";
    private int mSubtypeIconResId = 0;
    private int mSubtypeId = 0;
    private String mSubtypeLanguageTag = "";
    private String mSubtypeLocale = "";
    private String mSubtypeMode = "";
    private int mSubtypeNameResId = 0;
    
    public InputMethodSubtype build()
    {
      return new InputMethodSubtype(this, null);
    }
    
    public InputMethodSubtypeBuilder setIsAsciiCapable(boolean paramBoolean)
    {
      this.mIsAsciiCapable = paramBoolean;
      return this;
    }
    
    public InputMethodSubtypeBuilder setIsAuxiliary(boolean paramBoolean)
    {
      this.mIsAuxiliary = paramBoolean;
      return this;
    }
    
    public InputMethodSubtypeBuilder setLanguageTag(String paramString)
    {
      if (paramString == null) {
        paramString = "";
      }
      this.mSubtypeLanguageTag = paramString;
      return this;
    }
    
    public InputMethodSubtypeBuilder setOverridesImplicitlyEnabledSubtype(boolean paramBoolean)
    {
      this.mOverridesImplicitlyEnabledSubtype = paramBoolean;
      return this;
    }
    
    public InputMethodSubtypeBuilder setSubtypeExtraValue(String paramString)
    {
      if (paramString == null) {
        paramString = "";
      }
      this.mSubtypeExtraValue = paramString;
      return this;
    }
    
    public InputMethodSubtypeBuilder setSubtypeIconResId(int paramInt)
    {
      this.mSubtypeIconResId = paramInt;
      return this;
    }
    
    public InputMethodSubtypeBuilder setSubtypeId(int paramInt)
    {
      this.mSubtypeId = paramInt;
      return this;
    }
    
    public InputMethodSubtypeBuilder setSubtypeLocale(String paramString)
    {
      if (paramString == null) {
        paramString = "";
      }
      this.mSubtypeLocale = paramString;
      return this;
    }
    
    public InputMethodSubtypeBuilder setSubtypeMode(String paramString)
    {
      if (paramString == null) {
        paramString = "";
      }
      this.mSubtypeMode = paramString;
      return this;
    }
    
    public InputMethodSubtypeBuilder setSubtypeNameResId(int paramInt)
    {
      this.mSubtypeNameResId = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputMethodSubtype.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */