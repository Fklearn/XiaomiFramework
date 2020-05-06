package android.view.textclassifier;

import android.os.LocaleList;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.ArrayMap;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract interface TextClassifier
{
  public static final String DEFAULT_LOG_TAG = "androidtc";
  public static final String EXTRA_FROM_TEXT_CLASSIFIER = "android.view.textclassifier.extra.FROM_TEXT_CLASSIFIER";
  public static final String HINT_TEXT_IS_EDITABLE = "android.text_is_editable";
  public static final String HINT_TEXT_IS_NOT_EDITABLE = "android.text_is_not_editable";
  public static final int LOCAL = 0;
  public static final TextClassifier NO_OP = new TextClassifier()
  {
    public String toString()
    {
      return "TextClassifier.NO_OP";
    }
  };
  public static final int SYSTEM = 1;
  public static final String TYPE_ADDRESS = "address";
  public static final String TYPE_DATE = "date";
  public static final String TYPE_DATE_TIME = "datetime";
  public static final String TYPE_DICTIONARY = "dictionary";
  public static final String TYPE_EMAIL = "email";
  public static final String TYPE_FLIGHT_NUMBER = "flight";
  public static final String TYPE_OTHER = "other";
  public static final String TYPE_PHONE = "phone";
  public static final String TYPE_UNKNOWN = "";
  public static final String TYPE_URL = "url";
  public static final String WIDGET_TYPE_CUSTOM_EDITTEXT = "customedit";
  public static final String WIDGET_TYPE_CUSTOM_TEXTVIEW = "customview";
  public static final String WIDGET_TYPE_CUSTOM_UNSELECTABLE_TEXTVIEW = "nosel-customview";
  public static final String WIDGET_TYPE_EDITTEXT = "edittext";
  public static final String WIDGET_TYPE_EDIT_WEBVIEW = "edit-webview";
  public static final String WIDGET_TYPE_NOTIFICATION = "notification";
  public static final String WIDGET_TYPE_TEXTVIEW = "textview";
  public static final String WIDGET_TYPE_UNKNOWN = "unknown";
  public static final String WIDGET_TYPE_UNSELECTABLE_TEXTVIEW = "nosel-textview";
  public static final String WIDGET_TYPE_WEBVIEW = "webview";
  
  public TextClassification classifyText(TextClassification.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    Utils.checkMainThread();
    return TextClassification.EMPTY;
  }
  
  public TextClassification classifyText(CharSequence paramCharSequence, int paramInt1, int paramInt2, LocaleList paramLocaleList)
  {
    return classifyText(new TextClassification.Request.Builder(paramCharSequence, paramInt1, paramInt2).setDefaultLocales(paramLocaleList).build());
  }
  
  public void destroy() {}
  
  public TextLanguage detectLanguage(TextLanguage.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    Utils.checkMainThread();
    return TextLanguage.EMPTY;
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter) {}
  
  public TextLinks generateLinks(TextLinks.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    Utils.checkMainThread();
    return new TextLinks.Builder(paramRequest.getText().toString()).build();
  }
  
  public int getMaxGenerateLinksTextLength()
  {
    return Integer.MAX_VALUE;
  }
  
  public boolean isDestroyed()
  {
    return false;
  }
  
  public void onSelectionEvent(SelectionEvent paramSelectionEvent) {}
  
  public void onTextClassifierEvent(TextClassifierEvent paramTextClassifierEvent) {}
  
  public ConversationActions suggestConversationActions(ConversationActions.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    Utils.checkMainThread();
    return new ConversationActions(Collections.emptyList(), null);
  }
  
  public TextSelection suggestSelection(TextSelection.Request paramRequest)
  {
    Preconditions.checkNotNull(paramRequest);
    Utils.checkMainThread();
    return new TextSelection.Builder(paramRequest.getStartIndex(), paramRequest.getEndIndex()).build();
  }
  
  public TextSelection suggestSelection(CharSequence paramCharSequence, int paramInt1, int paramInt2, LocaleList paramLocaleList)
  {
    return suggestSelection(new TextSelection.Request.Builder(paramCharSequence, paramInt1, paramInt2).setDefaultLocales(paramLocaleList).build());
  }
  
  public static final class EntityConfig
    implements Parcelable
  {
    public static final Parcelable.Creator<EntityConfig> CREATOR = new Parcelable.Creator()
    {
      public TextClassifier.EntityConfig createFromParcel(Parcel paramAnonymousParcel)
      {
        return new TextClassifier.EntityConfig(paramAnonymousParcel, null);
      }
      
      public TextClassifier.EntityConfig[] newArray(int paramAnonymousInt)
      {
        return new TextClassifier.EntityConfig[paramAnonymousInt];
      }
    };
    private final List<String> mExcludedTypes;
    private final List<String> mHints;
    private final boolean mIncludeTypesFromTextClassifier;
    private final List<String> mIncludedTypes;
    
    private EntityConfig(Parcel paramParcel)
    {
      this.mIncludedTypes = new ArrayList();
      paramParcel.readStringList(this.mIncludedTypes);
      this.mExcludedTypes = new ArrayList();
      paramParcel.readStringList(this.mExcludedTypes);
      ArrayList localArrayList = new ArrayList();
      paramParcel.readStringList(localArrayList);
      this.mHints = Collections.unmodifiableList(localArrayList);
      boolean bool;
      if (paramParcel.readByte() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      this.mIncludeTypesFromTextClassifier = bool;
    }
    
    private EntityConfig(List<String> paramList1, List<String> paramList2, List<String> paramList3, boolean paramBoolean)
    {
      this.mIncludedTypes = ((List)Preconditions.checkNotNull(paramList1));
      this.mExcludedTypes = ((List)Preconditions.checkNotNull(paramList2));
      this.mHints = ((List)Preconditions.checkNotNull(paramList3));
      this.mIncludeTypesFromTextClassifier = paramBoolean;
    }
    
    @Deprecated
    public static EntityConfig create(Collection<String> paramCollection1, Collection<String> paramCollection2, Collection<String> paramCollection3)
    {
      return new Builder().setIncludedTypes(paramCollection2).setExcludedTypes(paramCollection3).setHints(paramCollection1).includeTypesFromTextClassifier(true).build();
    }
    
    @Deprecated
    public static EntityConfig createWithExplicitEntityList(Collection<String> paramCollection)
    {
      return new Builder().setIncludedTypes(paramCollection).includeTypesFromTextClassifier(false).build();
    }
    
    @Deprecated
    public static EntityConfig createWithHints(Collection<String> paramCollection)
    {
      return new Builder().includeTypesFromTextClassifier(true).setHints(paramCollection).build();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public Collection<String> getHints()
    {
      return this.mHints;
    }
    
    public Collection<String> resolveEntityListModifications(Collection<String> paramCollection)
    {
      HashSet localHashSet = new HashSet();
      if (this.mIncludeTypesFromTextClassifier) {
        localHashSet.addAll(paramCollection);
      }
      localHashSet.addAll(this.mIncludedTypes);
      localHashSet.removeAll(this.mExcludedTypes);
      return localHashSet;
    }
    
    public boolean shouldIncludeTypesFromTextClassifier()
    {
      return this.mIncludeTypesFromTextClassifier;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeStringList(this.mIncludedTypes);
      paramParcel.writeStringList(this.mExcludedTypes);
      paramParcel.writeStringList(this.mHints);
      paramParcel.writeByte((byte)this.mIncludeTypesFromTextClassifier);
    }
    
    public static final class Builder
    {
      private Collection<String> mExcludedTypes;
      private Collection<String> mHints;
      private boolean mIncludeTypesFromTextClassifier = true;
      private Collection<String> mIncludedTypes;
      
      public TextClassifier.EntityConfig build()
      {
        Object localObject1 = this.mIncludedTypes;
        if (localObject1 == null) {
          localObject1 = Collections.emptyList();
        } else {
          localObject1 = new ArrayList((Collection)localObject1);
        }
        Object localObject2 = this.mExcludedTypes;
        if (localObject2 == null) {
          localObject2 = Collections.emptyList();
        } else {
          localObject2 = new ArrayList((Collection)localObject2);
        }
        Object localObject3 = this.mHints;
        if (localObject3 == null) {
          localObject3 = Collections.emptyList();
        } else {
          localObject3 = Collections.unmodifiableList(new ArrayList((Collection)localObject3));
        }
        return new TextClassifier.EntityConfig((List)localObject1, (List)localObject2, (List)localObject3, this.mIncludeTypesFromTextClassifier, null);
      }
      
      public Builder includeTypesFromTextClassifier(boolean paramBoolean)
      {
        this.mIncludeTypesFromTextClassifier = paramBoolean;
        return this;
      }
      
      public Builder setExcludedTypes(Collection<String> paramCollection)
      {
        this.mExcludedTypes = paramCollection;
        return this;
      }
      
      public Builder setHints(Collection<String> paramCollection)
      {
        this.mHints = paramCollection;
        return this;
      }
      
      public Builder setIncludedTypes(Collection<String> paramCollection)
      {
        this.mIncludedTypes = paramCollection;
        return this;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface EntityType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Hints {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface TextClassifierType {}
  
  public static final class Utils
  {
    @GuardedBy({"WORD_ITERATOR"})
    private static final BreakIterator WORD_ITERATOR = ;
    
    private static void addLinks(TextLinks.Builder paramBuilder, String paramString1, String paramString2)
    {
      SpannableString localSpannableString = new SpannableString(paramString1);
      if (Linkify.addLinks(localSpannableString, linkMask(paramString2)))
      {
        int i = localSpannableString.length();
        int j = 0;
        URLSpan[] arrayOfURLSpan = (URLSpan[])localSpannableString.getSpans(0, i, URLSpan.class);
        i = arrayOfURLSpan.length;
        while (j < i)
        {
          paramString1 = arrayOfURLSpan[j];
          paramBuilder.addLink(localSpannableString.getSpanStart(paramString1), localSpannableString.getSpanEnd(paramString1), entityScores(paramString2), paramString1);
          j++;
        }
      }
    }
    
    static void checkArgument(CharSequence paramCharSequence, int paramInt1, int paramInt2)
    {
      boolean bool1 = true;
      boolean bool2;
      if (paramCharSequence != null) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      if (paramInt1 >= 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      if (paramInt2 <= paramCharSequence.length()) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      if (paramInt2 > paramInt1) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
    }
    
    static void checkMainThread()
    {
      if (Looper.myLooper() == Looper.getMainLooper()) {
        Log.w("androidtc", "TextClassifier called on main thread");
      }
    }
    
    static void checkTextLength(CharSequence paramCharSequence, int paramInt)
    {
      Preconditions.checkArgumentInRange(paramCharSequence.length(), 0, paramInt, "text.length()");
    }
    
    private static Map<String, Float> entityScores(String paramString)
    {
      ArrayMap localArrayMap = new ArrayMap();
      localArrayMap.put(paramString, Float.valueOf(1.0F));
      return localArrayMap;
    }
    
    public static TextLinks generateLegacyLinks(TextLinks.Request paramRequest)
    {
      String str = paramRequest.getText().toString();
      TextLinks.Builder localBuilder = new TextLinks.Builder(str);
      paramRequest = paramRequest.getEntityConfig().resolveEntityListModifications(Collections.emptyList());
      if (paramRequest.contains("url")) {
        addLinks(localBuilder, str, "url");
      }
      if (paramRequest.contains("phone")) {
        addLinks(localBuilder, str, "phone");
      }
      if (paramRequest.contains("email")) {
        addLinks(localBuilder, str, "email");
      }
      return localBuilder.build();
    }
    
    public static String getSubString(String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      boolean bool1 = true;
      boolean bool2;
      if (paramInt1 >= 0) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      if (paramInt2 <= paramString.length()) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      if (paramInt1 <= paramInt2) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2);
      if (paramString.length() < paramInt3) {
        return paramString;
      }
      int i = paramInt2 - paramInt1;
      if (i >= paramInt3) {
        return paramString.substring(paramInt1, paramInt2);
      }
      paramInt1 = Math.max(0, Math.min(paramInt1 - (paramInt3 - i) / 2, paramString.length() - paramInt3));
      paramInt2 = Math.min(paramString.length(), paramInt1 + paramInt3);
      synchronized (WORD_ITERATOR)
      {
        WORD_ITERATOR.setText(paramString);
        if (!WORD_ITERATOR.isBoundary(paramInt1)) {
          paramInt1 = Math.max(0, WORD_ITERATOR.preceding(paramInt1));
        }
        if (!WORD_ITERATOR.isBoundary(paramInt2)) {
          paramInt2 = Math.max(paramInt2, WORD_ITERATOR.following(paramInt2));
        }
        WORD_ITERATOR.setText("");
        paramString = paramString.substring(paramInt1, paramInt2);
        return paramString;
      }
    }
    
    private static int linkMask(String paramString)
    {
      int i = paramString.hashCode();
      if (i != 116079) {
        if (i != 96619420)
        {
          break label23;
          break label23;
          if (i != 106642798) {
            break label68;
          }
        }
      }
      label23:
      while (!paramString.equals("url"))
      {
        do
        {
          while (!paramString.equals("phone")) {}
          i = 1;
          break;
        } while (!paramString.equals("email"));
        i = 2;
        break;
      }
      i = 0;
      break label70;
      label68:
      i = -1;
      label70:
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2) {
            return 0;
          }
          return 2;
        }
        return 4;
      }
      return 1;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface WidgetType {}
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */