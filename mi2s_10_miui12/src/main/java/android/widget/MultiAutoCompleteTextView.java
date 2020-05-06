package android.widget;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.QwertyKeyListener;
import android.util.AttributeSet;

public class MultiAutoCompleteTextView
  extends AutoCompleteTextView
{
  private Tokenizer mTokenizer;
  
  public MultiAutoCompleteTextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public MultiAutoCompleteTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842859);
  }
  
  public MultiAutoCompleteTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public MultiAutoCompleteTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public boolean enoughToFilter()
  {
    Editable localEditable = getText();
    int i = getSelectionEnd();
    if (i >= 0)
    {
      Tokenizer localTokenizer = this.mTokenizer;
      if (localTokenizer != null) {
        return i - localTokenizer.findTokenStart(localEditable, i) >= getThreshold();
      }
    }
    return false;
  }
  
  void finishInit() {}
  
  public CharSequence getAccessibilityClassName()
  {
    return MultiAutoCompleteTextView.class.getName();
  }
  
  protected void performFiltering(CharSequence paramCharSequence, int paramInt)
  {
    if (enoughToFilter())
    {
      int i = getSelectionEnd();
      performFiltering(paramCharSequence, this.mTokenizer.findTokenStart(paramCharSequence, i), i, paramInt);
    }
    else
    {
      dismissDropDown();
      paramCharSequence = getFilter();
      if (paramCharSequence != null) {
        paramCharSequence.filter(null);
      }
    }
  }
  
  protected void performFiltering(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    getFilter().filter(paramCharSequence.subSequence(paramInt1, paramInt2), this);
  }
  
  public void performValidation()
  {
    AutoCompleteTextView.Validator localValidator = getValidator();
    if ((localValidator != null) && (this.mTokenizer != null))
    {
      Editable localEditable = getText();
      int j;
      for (int i = getText().length(); i > 0; i = j)
      {
        j = this.mTokenizer.findTokenStart(localEditable, i);
        CharSequence localCharSequence = localEditable.subSequence(j, this.mTokenizer.findTokenEnd(localEditable, j));
        if (TextUtils.isEmpty(localCharSequence)) {
          localEditable.replace(j, i, "");
        } else if (!localValidator.isValid(localCharSequence)) {
          localEditable.replace(j, i, this.mTokenizer.terminateToken(localValidator.fixText(localCharSequence)));
        }
      }
      return;
    }
  }
  
  protected void replaceText(CharSequence paramCharSequence)
  {
    clearComposingText();
    int i = getSelectionEnd();
    int j = this.mTokenizer.findTokenStart(getText(), i);
    Editable localEditable = getText();
    QwertyKeyListener.markAsReplaced(localEditable, j, i, TextUtils.substring(localEditable, j, i));
    localEditable.replace(j, i, this.mTokenizer.terminateToken(paramCharSequence));
  }
  
  public void setTokenizer(Tokenizer paramTokenizer)
  {
    this.mTokenizer = paramTokenizer;
  }
  
  public static class CommaTokenizer
    implements MultiAutoCompleteTextView.Tokenizer
  {
    public int findTokenEnd(CharSequence paramCharSequence, int paramInt)
    {
      int i = paramCharSequence.length();
      while (paramInt < i)
      {
        if (paramCharSequence.charAt(paramInt) == ',') {
          return paramInt;
        }
        paramInt++;
      }
      return i;
    }
    
    public int findTokenStart(CharSequence paramCharSequence, int paramInt)
    {
      int j;
      for (int i = paramInt;; i--)
      {
        j = i;
        if (i <= 0) {
          break;
        }
        j = i;
        if (paramCharSequence.charAt(i - 1) == ',') {
          break;
        }
      }
      while ((j < paramInt) && (paramCharSequence.charAt(j) == ' ')) {
        j++;
      }
      return j;
    }
    
    public CharSequence terminateToken(CharSequence paramCharSequence)
    {
      for (int i = paramCharSequence.length(); (i > 0) && (paramCharSequence.charAt(i - 1) == ' '); i--) {}
      if ((i > 0) && (paramCharSequence.charAt(i - 1) == ',')) {
        return paramCharSequence;
      }
      if ((paramCharSequence instanceof Spanned))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(paramCharSequence);
        ((StringBuilder)localObject).append(", ");
        localObject = new SpannableString(((StringBuilder)localObject).toString());
        TextUtils.copySpansFrom((Spanned)paramCharSequence, 0, paramCharSequence.length(), Object.class, (Spannable)localObject, 0);
        return (CharSequence)localObject;
      }
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append(paramCharSequence);
      ((StringBuilder)localObject).append(", ");
      return ((StringBuilder)localObject).toString();
    }
  }
  
  public static abstract interface Tokenizer
  {
    public abstract int findTokenEnd(CharSequence paramCharSequence, int paramInt);
    
    public abstract int findTokenStart(CharSequence paramCharSequence, int paramInt);
    
    public abstract CharSequence terminateToken(CharSequence paramCharSequence);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/MultiAutoCompleteTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */