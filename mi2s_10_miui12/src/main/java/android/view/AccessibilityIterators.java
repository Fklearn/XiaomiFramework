package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.res.Configuration;
import android.os.LocaleList;
import java.text.BreakIterator;
import java.util.Locale;

public final class AccessibilityIterators
{
  public static abstract class AbstractTextSegmentIterator
    implements AccessibilityIterators.TextSegmentIterator
  {
    private final int[] mSegment = new int[2];
    @UnsupportedAppUsage
    protected String mText;
    
    protected int[] getRange(int paramInt1, int paramInt2)
    {
      if ((paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt1 != paramInt2))
      {
        int[] arrayOfInt = this.mSegment;
        arrayOfInt[0] = paramInt1;
        arrayOfInt[1] = paramInt2;
        return arrayOfInt;
      }
      return null;
    }
    
    public void initialize(String paramString)
    {
      this.mText = paramString;
    }
  }
  
  static class CharacterTextSegmentIterator
    extends AccessibilityIterators.AbstractTextSegmentIterator
    implements ViewRootImpl.ConfigChangedCallback
  {
    private static CharacterTextSegmentIterator sInstance;
    protected BreakIterator mImpl;
    private Locale mLocale;
    
    private CharacterTextSegmentIterator(Locale paramLocale)
    {
      this.mLocale = paramLocale;
      onLocaleChanged(paramLocale);
      ViewRootImpl.addConfigCallback(this);
    }
    
    public static CharacterTextSegmentIterator getInstance(Locale paramLocale)
    {
      if (sInstance == null) {
        sInstance = new CharacterTextSegmentIterator(paramLocale);
      }
      return sInstance;
    }
    
    public int[] following(int paramInt)
    {
      int i = this.mText.length();
      if (i <= 0) {
        return null;
      }
      if (paramInt >= i) {
        return null;
      }
      i = paramInt;
      paramInt = i;
      if (i < 0) {
        paramInt = 0;
      }
      while (!this.mImpl.isBoundary(paramInt))
      {
        i = this.mImpl.following(paramInt);
        paramInt = i;
        if (i == -1) {
          return null;
        }
      }
      i = this.mImpl.following(paramInt);
      if (i == -1) {
        return null;
      }
      return getRange(paramInt, i);
    }
    
    public void initialize(String paramString)
    {
      super.initialize(paramString);
      this.mImpl.setText(paramString);
    }
    
    public void onConfigurationChanged(Configuration paramConfiguration)
    {
      paramConfiguration = paramConfiguration.getLocales().get(0);
      if (paramConfiguration == null) {
        return;
      }
      if (!this.mLocale.equals(paramConfiguration))
      {
        this.mLocale = paramConfiguration;
        onLocaleChanged(paramConfiguration);
      }
    }
    
    protected void onLocaleChanged(Locale paramLocale)
    {
      this.mImpl = BreakIterator.getCharacterInstance(paramLocale);
    }
    
    public int[] preceding(int paramInt)
    {
      int i = this.mText.length();
      if (i <= 0) {
        return null;
      }
      if (paramInt <= 0) {
        return null;
      }
      int j = paramInt;
      paramInt = j;
      if (j > i) {
        paramInt = i;
      }
      while (!this.mImpl.isBoundary(paramInt))
      {
        j = this.mImpl.preceding(paramInt);
        paramInt = j;
        if (j == -1) {
          return null;
        }
      }
      j = this.mImpl.preceding(paramInt);
      if (j == -1) {
        return null;
      }
      return getRange(j, paramInt);
    }
  }
  
  static class ParagraphTextSegmentIterator
    extends AccessibilityIterators.AbstractTextSegmentIterator
  {
    private static ParagraphTextSegmentIterator sInstance;
    
    public static ParagraphTextSegmentIterator getInstance()
    {
      if (sInstance == null) {
        sInstance = new ParagraphTextSegmentIterator();
      }
      return sInstance;
    }
    
    private boolean isEndBoundary(int paramInt)
    {
      boolean bool;
      if ((paramInt > 0) && (this.mText.charAt(paramInt - 1) != '\n') && ((paramInt == this.mText.length()) || (this.mText.charAt(paramInt) == '\n'))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private boolean isStartBoundary(int paramInt)
    {
      boolean bool;
      if ((this.mText.charAt(paramInt) != '\n') && ((paramInt == 0) || (this.mText.charAt(paramInt - 1) == '\n'))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public int[] following(int paramInt)
    {
      int i = this.mText.length();
      if (i <= 0) {
        return null;
      }
      if (paramInt >= i) {
        return null;
      }
      int j = paramInt;
      paramInt = j;
      if (j < 0) {}
      for (paramInt = 0; (paramInt < i) && (this.mText.charAt(paramInt) == '\n') && (!isStartBoundary(paramInt)); paramInt++) {}
      if (paramInt >= i) {
        return null;
      }
      for (j = paramInt + 1; (j < i) && (!isEndBoundary(j)); j++) {}
      return getRange(paramInt, j);
    }
    
    public int[] preceding(int paramInt)
    {
      int i = this.mText.length();
      if (i <= 0) {
        return null;
      }
      if (paramInt <= 0) {
        return null;
      }
      int j = paramInt;
      paramInt = j;
      if (j > i) {}
      for (paramInt = i; (paramInt > 0) && (this.mText.charAt(paramInt - 1) == '\n') && (!isEndBoundary(paramInt)); paramInt--) {}
      if (paramInt <= 0) {
        return null;
      }
      for (j = paramInt - 1; (j > 0) && (!isStartBoundary(j)); j--) {}
      return getRange(j, paramInt);
    }
  }
  
  public static abstract interface TextSegmentIterator
  {
    public abstract int[] following(int paramInt);
    
    public abstract int[] preceding(int paramInt);
  }
  
  static class WordTextSegmentIterator
    extends AccessibilityIterators.CharacterTextSegmentIterator
  {
    private static WordTextSegmentIterator sInstance;
    
    private WordTextSegmentIterator(Locale paramLocale)
    {
      super(null);
    }
    
    public static WordTextSegmentIterator getInstance(Locale paramLocale)
    {
      if (sInstance == null) {
        sInstance = new WordTextSegmentIterator(paramLocale);
      }
      return sInstance;
    }
    
    private boolean isEndBoundary(int paramInt)
    {
      boolean bool;
      if ((paramInt > 0) && (isLetterOrDigit(paramInt - 1)) && ((paramInt == this.mText.length()) || (!isLetterOrDigit(paramInt)))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private boolean isLetterOrDigit(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.mText.length())) {
        return Character.isLetterOrDigit(this.mText.codePointAt(paramInt));
      }
      return false;
    }
    
    private boolean isStartBoundary(int paramInt)
    {
      boolean bool;
      if ((isLetterOrDigit(paramInt)) && ((paramInt == 0) || (!isLetterOrDigit(paramInt - 1)))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public int[] following(int paramInt)
    {
      if (this.mText.length() <= 0) {
        return null;
      }
      if (paramInt >= this.mText.length()) {
        return null;
      }
      int i = paramInt;
      paramInt = i;
      if (i < 0) {
        paramInt = 0;
      }
      while ((!isLetterOrDigit(paramInt)) && (!isStartBoundary(paramInt)))
      {
        i = this.mImpl.following(paramInt);
        paramInt = i;
        if (i == -1) {
          return null;
        }
      }
      i = this.mImpl.following(paramInt);
      if ((i != -1) && (isEndBoundary(i))) {
        return getRange(paramInt, i);
      }
      return null;
    }
    
    protected void onLocaleChanged(Locale paramLocale)
    {
      this.mImpl = BreakIterator.getWordInstance(paramLocale);
    }
    
    public int[] preceding(int paramInt)
    {
      int i = this.mText.length();
      if (i <= 0) {
        return null;
      }
      if (paramInt <= 0) {
        return null;
      }
      int j = paramInt;
      paramInt = j;
      if (j > i) {
        paramInt = i;
      }
      while ((paramInt > 0) && (!isLetterOrDigit(paramInt - 1)) && (!isEndBoundary(paramInt)))
      {
        j = this.mImpl.preceding(paramInt);
        paramInt = j;
        if (j == -1) {
          return null;
        }
      }
      j = this.mImpl.preceding(paramInt);
      if ((j != -1) && (isStartBoundary(j))) {
        return getRange(j, paramInt);
      }
      return null;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/AccessibilityIterators.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */