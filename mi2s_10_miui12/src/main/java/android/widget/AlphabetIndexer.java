package android.widget;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.SparseIntArray;
import java.text.Collator;

public class AlphabetIndexer
  extends DataSetObserver
  implements SectionIndexer
{
  private SparseIntArray mAlphaMap;
  protected CharSequence mAlphabet;
  private String[] mAlphabetArray;
  private int mAlphabetLength;
  private Collator mCollator;
  protected int mColumnIndex;
  protected Cursor mDataCursor;
  
  public AlphabetIndexer(Cursor paramCursor, int paramInt, CharSequence paramCharSequence)
  {
    this.mDataCursor = paramCursor;
    this.mColumnIndex = paramInt;
    this.mAlphabet = paramCharSequence;
    this.mAlphabetLength = paramCharSequence.length();
    this.mAlphabetArray = new String[this.mAlphabetLength];
    int i;
    for (paramInt = 0;; paramInt++)
    {
      i = this.mAlphabetLength;
      if (paramInt >= i) {
        break;
      }
      this.mAlphabetArray[paramInt] = Character.toString(this.mAlphabet.charAt(paramInt));
    }
    this.mAlphaMap = new SparseIntArray(i);
    if (paramCursor != null) {
      paramCursor.registerDataSetObserver(this);
    }
    this.mCollator = Collator.getInstance();
    this.mCollator.setStrength(0);
  }
  
  protected int compare(String paramString1, String paramString2)
  {
    if (paramString1.length() == 0) {
      paramString1 = " ";
    } else {
      paramString1 = paramString1.substring(0, 1);
    }
    return this.mCollator.compare(paramString1, paramString2);
  }
  
  public int getPositionForSection(int paramInt)
  {
    SparseIntArray localSparseIntArray = this.mAlphaMap;
    Cursor localCursor = this.mDataCursor;
    if ((localCursor != null) && (this.mAlphabet != null))
    {
      if (paramInt <= 0) {
        return 0;
      }
      int i = this.mAlphabetLength;
      int j = paramInt;
      if (paramInt >= i) {
        j = i - 1;
      }
      int k = localCursor.getPosition();
      int m = localCursor.getCount();
      paramInt = 0;
      i = m;
      char c = this.mAlphabet.charAt(j);
      String str1 = Character.toString(c);
      int n = localSparseIntArray.get(c, Integer.MIN_VALUE);
      if (Integer.MIN_VALUE != n) {
        if (n < 0) {
          i = -n;
        } else {
          return n;
        }
      }
      n = paramInt;
      if (j > 0)
      {
        j = localSparseIntArray.get(this.mAlphabet.charAt(j - 1), Integer.MIN_VALUE);
        n = paramInt;
        if (j != Integer.MIN_VALUE) {
          n = Math.abs(j);
        }
      }
      paramInt = (i + n) / 2;
      for (;;)
      {
        j = paramInt;
        if (paramInt >= i) {
          break;
        }
        localCursor.moveToPosition(paramInt);
        String str2 = localCursor.getString(this.mColumnIndex);
        if (str2 == null)
        {
          if (paramInt == 0)
          {
            j = paramInt;
            break;
          }
          paramInt--;
        }
        else
        {
          j = compare(str2, str1);
          if (j != 0)
          {
            if (j < 0)
            {
              j = paramInt + 1;
              n = j;
              paramInt = i;
              if (j < m) {
                break label291;
              }
              j = m;
              break;
            }
          }
          else if (n == paramInt)
          {
            j = paramInt;
            break;
          }
          label291:
          j = (n + paramInt) / 2;
          i = paramInt;
          paramInt = j;
        }
      }
      localSparseIntArray.put(c, j);
      localCursor.moveToPosition(k);
      return j;
    }
    return 0;
  }
  
  public int getSectionForPosition(int paramInt)
  {
    int i = this.mDataCursor.getPosition();
    this.mDataCursor.moveToPosition(paramInt);
    String str = this.mDataCursor.getString(this.mColumnIndex);
    this.mDataCursor.moveToPosition(i);
    for (paramInt = 0; paramInt < this.mAlphabetLength; paramInt++) {
      if (compare(str, Character.toString(this.mAlphabet.charAt(paramInt))) == 0) {
        return paramInt;
      }
    }
    return 0;
  }
  
  public Object[] getSections()
  {
    return this.mAlphabetArray;
  }
  
  public void onChanged()
  {
    super.onChanged();
    this.mAlphaMap.clear();
  }
  
  public void onInvalidated()
  {
    super.onInvalidated();
    this.mAlphaMap.clear();
  }
  
  public void setCursor(Cursor paramCursor)
  {
    Cursor localCursor = this.mDataCursor;
    if (localCursor != null) {
      localCursor.unregisterDataSetObserver(this);
    }
    this.mDataCursor = paramCursor;
    if (paramCursor != null) {
      this.mDataCursor.registerDataSetObserver(this);
    }
    this.mAlphaMap.clear();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AlphabetIndexer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */