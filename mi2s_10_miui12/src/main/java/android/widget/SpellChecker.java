package android.widget;

import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.WordIterator;
import android.text.style.SpellCheckSpan;
import android.text.style.SuggestionSpan;
import android.util.Log;
import android.util.LruCache;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.util.Locale;

public class SpellChecker
  implements SpellCheckerSession.SpellCheckerSessionListener
{
  public static final int AVERAGE_WORD_LENGTH = 7;
  private static final boolean DBG = false;
  public static final int MAX_NUMBER_OF_WORDS = 50;
  private static final int MIN_SENTENCE_LENGTH = 50;
  private static final int SPELL_PAUSE_DURATION = 400;
  private static final int SUGGESTION_SPAN_CACHE_SIZE = 10;
  private static final String TAG = SpellChecker.class.getSimpleName();
  private static final int USE_SPAN_RANGE = -1;
  public static final int WORD_ITERATOR_INTERVAL = 350;
  final int mCookie;
  private Locale mCurrentLocale;
  private int[] mIds;
  private boolean mIsSentenceSpellCheckSupported;
  private int mLength;
  private int mSpanSequenceCounter = 0;
  private SpellCheckSpan[] mSpellCheckSpans;
  SpellCheckerSession mSpellCheckerSession;
  private SpellParser[] mSpellParsers = new SpellParser[0];
  private Runnable mSpellRunnable;
  private final LruCache<Long, SuggestionSpan> mSuggestionSpanCache = new LruCache(10);
  private TextServicesManager mTextServicesManager;
  private final TextView mTextView;
  private WordIterator mWordIterator;
  
  public SpellChecker(TextView paramTextView)
  {
    this.mTextView = paramTextView;
    this.mIds = ArrayUtils.newUnpaddedIntArray(1);
    this.mSpellCheckSpans = new SpellCheckSpan[this.mIds.length];
    setLocale(this.mTextView.getSpellCheckerLocale());
    this.mCookie = hashCode();
  }
  
  private void addSpellCheckSpan(Editable paramEditable, int paramInt1, int paramInt2)
  {
    int i = nextSpellCheckSpanIndex();
    SpellCheckSpan localSpellCheckSpan = this.mSpellCheckSpans[i];
    paramEditable.setSpan(localSpellCheckSpan, paramInt1, paramInt2, 33);
    localSpellCheckSpan.setSpellCheckInProgress(false);
    paramEditable = this.mIds;
    paramInt1 = this.mSpanSequenceCounter;
    this.mSpanSequenceCounter = (paramInt1 + 1);
    paramEditable[i] = paramInt1;
  }
  
  private void createMisspelledSuggestionSpan(Editable paramEditable, SuggestionsInfo paramSuggestionsInfo, SpellCheckSpan paramSpellCheckSpan, int paramInt1, int paramInt2)
  {
    int i = paramEditable.getSpanStart(paramSpellCheckSpan);
    int j = paramEditable.getSpanEnd(paramSpellCheckSpan);
    if ((i >= 0) && (j > i))
    {
      if ((paramInt1 != -1) && (paramInt2 != -1))
      {
        paramInt1 = i + paramInt1;
        j = paramInt1 + paramInt2;
        paramInt2 = paramInt1;
        paramInt1 = j;
      }
      else
      {
        paramInt2 = i;
        paramInt1 = j;
      }
      i = paramSuggestionsInfo.getSuggestionsCount();
      if (i > 0)
      {
        paramSpellCheckSpan = new String[i];
        for (j = 0; j < i; j++) {
          paramSpellCheckSpan[j] = paramSuggestionsInfo.getSuggestionAt(j);
        }
        paramSuggestionsInfo = paramSpellCheckSpan;
      }
      else
      {
        paramSuggestionsInfo = (String[])ArrayUtils.emptyArray(String.class);
      }
      SuggestionSpan localSuggestionSpan = new SuggestionSpan(this.mTextView.getContext(), paramSuggestionsInfo, 3);
      if (this.mIsSentenceSpellCheckSupported)
      {
        paramSpellCheckSpan = Long.valueOf(TextUtils.packRangeInLong(paramInt2, paramInt1));
        paramSuggestionsInfo = (SuggestionSpan)this.mSuggestionSpanCache.get(paramSpellCheckSpan);
        if (paramSuggestionsInfo != null) {
          paramEditable.removeSpan(paramSuggestionsInfo);
        }
        this.mSuggestionSpanCache.put(paramSpellCheckSpan, localSuggestionSpan);
      }
      paramEditable.setSpan(localSuggestionSpan, paramInt2, paramInt1, 33);
      this.mTextView.invalidateRegion(paramInt2, paramInt1, false);
      return;
    }
  }
  
  public static boolean haveWordBoundariesChanged(Editable paramEditable, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool;
    if ((paramInt4 != paramInt1) && (paramInt3 != paramInt2)) {
      bool = true;
    } else if ((paramInt4 == paramInt1) && (paramInt1 < paramEditable.length())) {
      bool = Character.isLetterOrDigit(Character.codePointAt(paramEditable, paramInt1));
    } else if ((paramInt3 == paramInt2) && (paramInt2 > 0)) {
      bool = Character.isLetterOrDigit(Character.codePointBefore(paramEditable, paramInt2));
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isSessionActive()
  {
    boolean bool;
    if (this.mSpellCheckerSession != null) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private int nextSpellCheckSpanIndex()
  {
    int j;
    for (int i = 0;; i++)
    {
      j = this.mLength;
      if (i >= j) {
        break;
      }
      if (this.mIds[i] < 0) {
        return i;
      }
    }
    this.mIds = GrowingArrayUtils.append(this.mIds, j, 0);
    this.mSpellCheckSpans = ((SpellCheckSpan[])GrowingArrayUtils.append(this.mSpellCheckSpans, this.mLength, new SpellCheckSpan()));
    this.mLength += 1;
    return this.mLength - 1;
  }
  
  private SpellCheckSpan onGetSuggestionsInternal(SuggestionsInfo paramSuggestionsInfo, int paramInt1, int paramInt2)
  {
    if ((paramSuggestionsInfo != null) && (paramSuggestionsInfo.getCookie() == this.mCookie))
    {
      Editable localEditable = (Editable)this.mTextView.getText();
      int i = paramSuggestionsInfo.getSequence();
      for (int j = 0; j < this.mLength; j++) {
        if (i == this.mIds[j])
        {
          int k = paramSuggestionsInfo.getSuggestionsAttributes();
          int m = 0;
          if ((k & 0x1) > 0) {
            i = 1;
          } else {
            i = 0;
          }
          if ((k & 0x2) > 0) {
            m = 1;
          }
          SpellCheckSpan localSpellCheckSpan = this.mSpellCheckSpans[j];
          if ((i == 0) && (m != 0))
          {
            createMisspelledSuggestionSpan(localEditable, paramSuggestionsInfo, localSpellCheckSpan, paramInt1, paramInt2);
          }
          else if (this.mIsSentenceSpellCheckSupported)
          {
            j = localEditable.getSpanStart(localSpellCheckSpan);
            i = localEditable.getSpanEnd(localSpellCheckSpan);
            if ((paramInt1 != -1) && (paramInt2 != -1))
            {
              paramInt1 = j + paramInt1;
              paramInt2 = paramInt1 + paramInt2;
            }
            else
            {
              paramInt1 = j;
              paramInt2 = i;
            }
            if ((j >= 0) && (i > j) && (paramInt2 > paramInt1))
            {
              paramSuggestionsInfo = Long.valueOf(TextUtils.packRangeInLong(paramInt1, paramInt2));
              SuggestionSpan localSuggestionSpan = (SuggestionSpan)this.mSuggestionSpanCache.get(paramSuggestionsInfo);
              if (localSuggestionSpan != null)
              {
                localEditable.removeSpan(localSuggestionSpan);
                this.mSuggestionSpanCache.remove(paramSuggestionsInfo);
              }
              else {}
            }
          }
          return localSpellCheckSpan;
        }
      }
      return null;
    }
    return null;
  }
  
  private void scheduleNewSpellCheck()
  {
    Runnable localRunnable = this.mSpellRunnable;
    if (localRunnable == null) {
      this.mSpellRunnable = new Runnable()
      {
        public void run()
        {
          int i = SpellChecker.this.mSpellParsers.length;
          for (int j = 0; j < i; j++)
          {
            SpellChecker.SpellParser localSpellParser = SpellChecker.this.mSpellParsers[j];
            if (!localSpellParser.isFinished())
            {
              localSpellParser.parse();
              break;
            }
          }
        }
      };
    } else {
      this.mTextView.removeCallbacks(localRunnable);
    }
    this.mTextView.postDelayed(this.mSpellRunnable, 400L);
  }
  
  private void setLocale(Locale paramLocale)
  {
    this.mCurrentLocale = paramLocale;
    resetSession();
    if (paramLocale != null) {
      this.mWordIterator = new WordIterator(paramLocale);
    }
    this.mTextView.onLocaleChanged();
  }
  
  private void spellCheck()
  {
    if (this.mSpellCheckerSession == null) {
      return;
    }
    Editable localEditable = (Editable)this.mTextView.getText();
    int i = Selection.getSelectionStart(localEditable);
    int j = Selection.getSelectionEnd(localEditable);
    TextInfo[] arrayOfTextInfo = new TextInfo[this.mLength];
    int k = 0;
    int m = 0;
    Object localObject;
    for (;;)
    {
      int n = this.mLength;
      int i1 = 0;
      int i2 = 0;
      if (m >= n) {
        break;
      }
      localObject = this.mSpellCheckSpans[m];
      n = k;
      if (this.mIds[m] >= 0) {
        if (((SpellCheckSpan)localObject).isSpellCheckInProgress())
        {
          n = k;
        }
        else
        {
          int i3 = localEditable.getSpanStart(localObject);
          int i4 = localEditable.getSpanEnd(localObject);
          if ((i == i4 + 1) && (WordIterator.isMidWordPunctuation(this.mCurrentLocale, Character.codePointBefore(localEditable, i4 + 1))))
          {
            i2 = 0;
          }
          else if (this.mIsSentenceSpellCheckSupported)
          {
            if ((j <= i3) || (i > i4)) {
              i2 = 1;
            }
          }
          else if (j >= i3)
          {
            i2 = i1;
            if (i <= i4) {}
          }
          else
          {
            i2 = 1;
          }
          n = k;
          if (i3 >= 0)
          {
            n = k;
            if (i4 > i3)
            {
              n = k;
              if (i2 != 0)
              {
                ((SpellCheckSpan)localObject).setSpellCheckInProgress(true);
                arrayOfTextInfo[k] = new TextInfo(localEditable, i3, i4, this.mCookie, this.mIds[m]);
                n = k + 1;
              }
            }
          }
        }
      }
      m++;
      k = n;
    }
    if (k > 0)
    {
      localObject = arrayOfTextInfo;
      if (k < arrayOfTextInfo.length)
      {
        localObject = new TextInfo[k];
        System.arraycopy(arrayOfTextInfo, 0, localObject, 0, k);
      }
      if (this.mIsSentenceSpellCheckSupported) {
        this.mSpellCheckerSession.getSentenceSuggestions((TextInfo[])localObject, 5);
      } else {
        this.mSpellCheckerSession.getSuggestions((TextInfo[])localObject, 5, false);
      }
    }
  }
  
  public void closeSession()
  {
    Object localObject = this.mSpellCheckerSession;
    if (localObject != null) {
      ((SpellCheckerSession)localObject).close();
    }
    int i = this.mSpellParsers.length;
    for (int j = 0; j < i; j++) {
      this.mSpellParsers[j].stop();
    }
    localObject = this.mSpellRunnable;
    if (localObject != null) {
      this.mTextView.removeCallbacks((Runnable)localObject);
    }
  }
  
  public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] paramArrayOfSentenceSuggestionsInfo)
  {
    Editable localEditable = (Editable)this.mTextView.getText();
    for (int i = 0; i < paramArrayOfSentenceSuggestionsInfo.length; i++)
    {
      SentenceSuggestionsInfo localSentenceSuggestionsInfo = paramArrayOfSentenceSuggestionsInfo[i];
      if (localSentenceSuggestionsInfo != null)
      {
        Object localObject1 = null;
        int j = 0;
        while (j < localSentenceSuggestionsInfo.getSuggestionsCount())
        {
          Object localObject2 = localSentenceSuggestionsInfo.getSuggestionsInfoAt(j);
          if (localObject2 == null)
          {
            localObject2 = localObject1;
          }
          else
          {
            SpellCheckSpan localSpellCheckSpan = onGetSuggestionsInternal((SuggestionsInfo)localObject2, localSentenceSuggestionsInfo.getOffsetAt(j), localSentenceSuggestionsInfo.getLengthAt(j));
            localObject2 = localObject1;
            if (localObject1 == null)
            {
              localObject2 = localObject1;
              if (localSpellCheckSpan != null) {
                localObject2 = localSpellCheckSpan;
              }
            }
          }
          j++;
          localObject1 = localObject2;
        }
        if (localObject1 != null) {
          localEditable.removeSpan(localObject1);
        }
      }
    }
    scheduleNewSpellCheck();
  }
  
  public void onGetSuggestions(SuggestionsInfo[] paramArrayOfSuggestionsInfo)
  {
    Editable localEditable = (Editable)this.mTextView.getText();
    for (int i = 0; i < paramArrayOfSuggestionsInfo.length; i++)
    {
      SpellCheckSpan localSpellCheckSpan = onGetSuggestionsInternal(paramArrayOfSuggestionsInfo[i], -1, -1);
      if (localSpellCheckSpan != null) {
        localEditable.removeSpan(localSpellCheckSpan);
      }
    }
    scheduleNewSpellCheck();
  }
  
  public void onSelectionChanged()
  {
    spellCheck();
  }
  
  public void onSpellCheckSpanRemoved(SpellCheckSpan paramSpellCheckSpan)
  {
    for (int i = 0; i < this.mLength; i++) {
      if (this.mSpellCheckSpans[i] == paramSpellCheckSpan)
      {
        this.mIds[i] = -1;
        return;
      }
    }
  }
  
  void resetSession()
  {
    closeSession();
    this.mTextServicesManager = this.mTextView.getTextServicesManagerForUser();
    if ((this.mCurrentLocale != null) && (this.mTextServicesManager != null) && (this.mTextView.length() != 0) && (this.mTextServicesManager.isSpellCheckerEnabled()) && (this.mTextServicesManager.getCurrentSpellCheckerSubtype(true) != null))
    {
      this.mSpellCheckerSession = this.mTextServicesManager.newSpellCheckerSession(null, this.mCurrentLocale, this, false);
      this.mIsSentenceSpellCheckSupported = true;
    }
    else
    {
      this.mSpellCheckerSession = null;
    }
    for (int i = 0; i < this.mLength; i++) {
      this.mIds[i] = -1;
    }
    this.mLength = 0;
    TextView localTextView = this.mTextView;
    localTextView.removeMisspelledSpans((Editable)localTextView.getText());
    this.mSuggestionSpanCache.evictAll();
  }
  
  public void spellCheck(int paramInt1, int paramInt2)
  {
    Object localObject = this.mTextView.getSpellCheckerLocale();
    boolean bool1 = isSessionActive();
    if (localObject != null)
    {
      Locale localLocale = this.mCurrentLocale;
      if ((localLocale != null) && (localLocale.equals(localObject)))
      {
        localObject = this.mTextServicesManager;
        boolean bool2;
        if ((localObject != null) && (((TextServicesManager)localObject).isSpellCheckerEnabled())) {
          bool2 = true;
        } else {
          bool2 = false;
        }
        i = paramInt1;
        j = paramInt2;
        if (bool1 == bool2) {
          break label114;
        }
        resetSession();
        i = paramInt1;
        j = paramInt2;
        break label114;
      }
    }
    setLocale((Locale)localObject);
    int i = 0;
    int j = this.mTextView.getText().length();
    label114:
    if (!bool1) {
      return;
    }
    paramInt2 = this.mSpellParsers.length;
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
    {
      localObject = this.mSpellParsers[paramInt1];
      if (((SpellParser)localObject).isFinished())
      {
        ((SpellParser)localObject).parse(i, j);
        return;
      }
    }
    localObject = new SpellParser[paramInt2 + 1];
    System.arraycopy(this.mSpellParsers, 0, localObject, 0, paramInt2);
    this.mSpellParsers = ((SpellParser[])localObject);
    localObject = new SpellParser(null);
    this.mSpellParsers[paramInt2] = localObject;
    ((SpellParser)localObject).parse(i, j);
  }
  
  private class SpellParser
  {
    private Object mRange = new Object();
    
    private SpellParser() {}
    
    private void removeRangeSpan(Editable paramEditable)
    {
      paramEditable.removeSpan(this.mRange);
    }
    
    private <T> void removeSpansAt(Editable paramEditable, int paramInt, T[] paramArrayOfT)
    {
      int i = paramArrayOfT.length;
      for (int j = 0; j < i; j++)
      {
        T ? = paramArrayOfT[j];
        if ((paramEditable.getSpanStart(?) <= paramInt) && (paramEditable.getSpanEnd(?) >= paramInt)) {
          paramEditable.removeSpan(?);
        }
      }
    }
    
    private void setRangeSpan(Editable paramEditable, int paramInt1, int paramInt2)
    {
      paramEditable.setSpan(this.mRange, paramInt1, paramInt2, 33);
    }
    
    public boolean isFinished()
    {
      boolean bool;
      if (((Editable)SpellChecker.this.mTextView.getText()).getSpanStart(this.mRange) < 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void parse()
    {
      Editable localEditable = (Editable)SpellChecker.this.mTextView.getText();
      int i;
      if (SpellChecker.this.mIsSentenceSpellCheckSupported) {
        i = Math.max(0, localEditable.getSpanStart(this.mRange) - 50);
      } else {
        i = localEditable.getSpanStart(this.mRange);
      }
      int j = localEditable.getSpanEnd(this.mRange);
      int k = Math.min(j, i + 350);
      SpellChecker.this.mWordIterator.setCharSequence(localEditable, i, k);
      int m = SpellChecker.this.mWordIterator.preceding(i);
      int i1;
      if (m == -1)
      {
        n = SpellChecker.this.mWordIterator.following(i);
        i1 = n;
        if (n != -1)
        {
          m = SpellChecker.this.mWordIterator.getBeginning(n);
          i1 = n;
        }
      }
      else
      {
        i1 = SpellChecker.this.mWordIterator.getEnd(m);
      }
      if (i1 == -1)
      {
        removeRangeSpan(localEditable);
        return;
      }
      Object localObject1 = (SpellCheckSpan[])localEditable.getSpans(i - 1, j + 1, SpellCheckSpan.class);
      Object localObject2 = (SuggestionSpan[])localEditable.getSpans(i - 1, j + 1, SuggestionSpan.class);
      int i2 = 0;
      int i3 = 0;
      int n = 0;
      int i4;
      int i5;
      if (SpellChecker.this.mIsSentenceSpellCheckSupported)
      {
        if (k < j) {
          n = 1;
        }
        i3 = SpellChecker.this.mWordIterator.preceding(k);
        if (i3 != -1) {
          i2 = 1;
        } else {
          i2 = 0;
        }
        i4 = i3;
        i5 = i2;
        if (i2 != 0)
        {
          i2 = SpellChecker.this.mWordIterator.getEnd(i3);
          if (i2 != -1) {
            i4 = 1;
          } else {
            i4 = 0;
          }
          i5 = i4;
          i4 = i2;
        }
        if (i5 == 0)
        {
          removeRangeSpan(localEditable);
          return;
        }
        i2 = m;
        int i6 = 1;
        i5 = i4;
        i3 = 0;
        i4 = i1;
        int i7;
        for (i1 = i5; i3 < SpellChecker.this.mLength; i1 = i7)
        {
          localObject2 = SpellChecker.this.mSpellCheckSpans[i3];
          if (SpellChecker.this.mIds[i3] >= 0)
          {
            if (((SpellCheckSpan)localObject2).isSpellCheckInProgress())
            {
              i5 = i2;
              i7 = i1;
            }
            else
            {
              int i8 = localEditable.getSpanStart(localObject2);
              int i9 = localEditable.getSpanEnd(localObject2);
              i5 = i2;
              i7 = i1;
              if (i9 >= i2) {
                if (i1 < i8)
                {
                  i5 = i2;
                  i7 = i1;
                }
                else
                {
                  if ((i8 <= i2) && (i1 <= i9))
                  {
                    m = 0;
                    break label543;
                  }
                  localEditable.removeSpan(localObject2);
                  i5 = Math.min(i8, i2);
                  i7 = Math.max(i9, i1);
                }
              }
            }
          }
          else
          {
            i7 = i1;
            i5 = i2;
          }
          i3++;
          i2 = i5;
        }
        m = i6;
        label543:
        if (i1 >= i) {
          if (i1 <= i2)
          {
            localObject2 = SpellChecker.TAG;
            localObject1 = new StringBuilder();
            ((StringBuilder)localObject1).append("Trying to spellcheck invalid region, from ");
            ((StringBuilder)localObject1).append(i);
            ((StringBuilder)localObject1).append(" to ");
            ((StringBuilder)localObject1).append(j);
            Log.w((String)localObject2, ((StringBuilder)localObject1).toString());
          }
          else if (m != 0)
          {
            SpellChecker.this.addSpellCheckSpan(localEditable, i2, i1);
          }
        }
      }
      else
      {
        i4 = i1;
        for (;;)
        {
          i1 = m;
          n = i3;
          if (m > j) {
            break;
          }
          n = i2;
          if (i4 >= i)
          {
            n = i2;
            if (i4 > m)
            {
              if (i2 >= 50)
              {
                n = 1;
                i1 = m;
                break;
              }
              if ((m < i) && (i4 > i))
              {
                removeSpansAt(localEditable, i, (Object[])localObject1);
                removeSpansAt(localEditable, i, (Object[])localObject2);
              }
              if ((m < j) && (i4 > j))
              {
                removeSpansAt(localEditable, j, (Object[])localObject1);
                removeSpansAt(localEditable, j, (Object[])localObject2);
              }
              i5 = 1;
              i1 = i5;
              if (i4 == i) {
                for (n = 0;; n++)
                {
                  i1 = i5;
                  if (n >= localObject1.length) {
                    break;
                  }
                  if (localEditable.getSpanEnd(localObject1[n]) == i)
                  {
                    i1 = 0;
                    break;
                  }
                }
              }
              i5 = i1;
              if (m == j) {
                for (n = 0;; n++)
                {
                  i5 = i1;
                  if (n >= localObject1.length) {
                    break;
                  }
                  if (localEditable.getSpanStart(localObject1[n]) == j)
                  {
                    i5 = 0;
                    break;
                  }
                }
              }
              if (i5 != 0) {
                SpellChecker.this.addSpellCheckSpan(localEditable, m, i4);
              }
              n = i2 + 1;
            }
          }
          i1 = SpellChecker.this.mWordIterator.following(i4);
          if ((k < j) && ((i1 == -1) || (i1 >= k)))
          {
            k = Math.min(j, i4 + 350);
            SpellChecker.this.mWordIterator.setCharSequence(localEditable, i4, k);
            i1 = SpellChecker.this.mWordIterator.following(i4);
          }
          if (i1 == -1)
          {
            i1 = m;
            n = i3;
            break;
          }
          m = SpellChecker.this.mWordIterator.getBeginning(i1);
          if (m == -1)
          {
            i1 = m;
            n = i3;
            break;
          }
          i4 = i1;
          i2 = n;
        }
      }
      if ((n != 0) && (i1 != -1) && (i1 <= j)) {
        setRangeSpan(localEditable, i1, j);
      } else {
        removeRangeSpan(localEditable);
      }
      SpellChecker.this.spellCheck();
    }
    
    public void parse(int paramInt1, int paramInt2)
    {
      int i = SpellChecker.this.mTextView.length();
      if (paramInt2 > i)
      {
        String str = SpellChecker.TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Parse invalid region, from ");
        localStringBuilder.append(paramInt1);
        localStringBuilder.append(" to ");
        localStringBuilder.append(paramInt2);
        Log.w(str, localStringBuilder.toString());
        paramInt2 = i;
      }
      if (paramInt2 > paramInt1)
      {
        setRangeSpan((Editable)SpellChecker.this.mTextView.getText(), paramInt1, paramInt2);
        parse();
      }
    }
    
    public void stop()
    {
      removeRangeSpan((Editable)SpellChecker.this.mTextView.getText());
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SpellChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */