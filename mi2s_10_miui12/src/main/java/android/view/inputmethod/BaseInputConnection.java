package android.view.inputmethod;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Editable.Factory;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

public class BaseInputConnection
  implements InputConnection
{
  static final Object COMPOSING = new ComposingText();
  private static final boolean DEBUG = false;
  private static int INVALID_INDEX = -1;
  private static final String TAG = "BaseInputConnection";
  private Object[] mDefaultComposingSpans;
  final boolean mDummyMode;
  Editable mEditable;
  protected final InputMethodManager mIMM;
  KeyCharacterMap mKeyCharacterMap;
  final View mTargetView;
  
  public BaseInputConnection(View paramView, boolean paramBoolean)
  {
    this.mIMM = ((InputMethodManager)paramView.getContext().getSystemService("input_method"));
    this.mTargetView = paramView;
    this.mDummyMode = (paramBoolean ^ true);
  }
  
  BaseInputConnection(InputMethodManager paramInputMethodManager, boolean paramBoolean)
  {
    this.mIMM = paramInputMethodManager;
    this.mTargetView = null;
    this.mDummyMode = (paramBoolean ^ true);
  }
  
  private void ensureDefaultComposingSpans()
  {
    if (this.mDefaultComposingSpans == null)
    {
      Object localObject = this.mTargetView;
      if (localObject != null) {
        localObject = ((View)localObject).getContext();
      } else if (this.mIMM.mServedView != null) {
        localObject = this.mIMM.mServedView.getContext();
      } else {
        localObject = null;
      }
      if (localObject != null)
      {
        localObject = ((Context)localObject).getTheme().obtainStyledAttributes(new int[] { 16843312 });
        CharSequence localCharSequence = ((TypedArray)localObject).getText(0);
        ((TypedArray)localObject).recycle();
        if ((localCharSequence != null) && ((localCharSequence instanceof Spanned))) {
          this.mDefaultComposingSpans = ((Spanned)localCharSequence).getSpans(0, localCharSequence.length(), Object.class);
        }
      }
    }
  }
  
  private static int findIndexBackward(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    int j = 0;
    paramInt1 = paramCharSequence.length();
    if ((i >= 0) && (paramInt1 >= i))
    {
      if (paramInt2 < 0) {
        return INVALID_INDEX;
      }
      paramInt1 = paramInt2;
      paramInt2 = j;
      for (;;)
      {
        if (paramInt1 == 0) {
          return i;
        }
        i--;
        if (i < 0)
        {
          if (paramInt2 != 0) {
            return INVALID_INDEX;
          }
          return 0;
        }
        char c = paramCharSequence.charAt(i);
        if (paramInt2 != 0)
        {
          if (!Character.isHighSurrogate(c)) {
            return INVALID_INDEX;
          }
          paramInt2 = 0;
          paramInt1--;
        }
        else if (!Character.isSurrogate(c))
        {
          paramInt1--;
        }
        else
        {
          if (Character.isHighSurrogate(c)) {
            return INVALID_INDEX;
          }
          paramInt2 = 1;
        }
      }
    }
    return INVALID_INDEX;
  }
  
  private static int findIndexForward(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = paramCharSequence.length();
    if ((paramInt1 >= 0) && (j >= paramInt1))
    {
      if (paramInt2 < 0) {
        return INVALID_INDEX;
      }
      int k = paramInt2;
      paramInt2 = i;
      for (;;)
      {
        if (k == 0) {
          return paramInt1;
        }
        if (paramInt1 >= j)
        {
          if (paramInt2 != 0) {
            return INVALID_INDEX;
          }
          return j;
        }
        char c = paramCharSequence.charAt(paramInt1);
        if (paramInt2 != 0)
        {
          if (!Character.isLowSurrogate(c)) {
            return INVALID_INDEX;
          }
          k--;
          paramInt2 = 0;
          paramInt1++;
        }
        else if (!Character.isSurrogate(c))
        {
          k--;
          paramInt1++;
        }
        else
        {
          if (Character.isLowSurrogate(c)) {
            return INVALID_INDEX;
          }
          paramInt2 = 1;
          paramInt1++;
        }
      }
    }
    return INVALID_INDEX;
  }
  
  public static int getComposingSpanEnd(Spannable paramSpannable)
  {
    return paramSpannable.getSpanEnd(COMPOSING);
  }
  
  public static int getComposingSpanStart(Spannable paramSpannable)
  {
    return paramSpannable.getSpanStart(COMPOSING);
  }
  
  public static final void removeComposingSpans(Spannable paramSpannable)
  {
    paramSpannable.removeSpan(COMPOSING);
    Object[] arrayOfObject = paramSpannable.getSpans(0, paramSpannable.length(), Object.class);
    if (arrayOfObject != null) {
      for (int i = arrayOfObject.length - 1; i >= 0; i--)
      {
        Object localObject = arrayOfObject[i];
        if ((paramSpannable.getSpanFlags(localObject) & 0x100) != 0) {
          paramSpannable.removeSpan(localObject);
        }
      }
    }
  }
  
  private void replaceText(CharSequence paramCharSequence, int paramInt, boolean paramBoolean)
  {
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return;
    }
    beginBatchEdit();
    int i = getComposingSpanStart(localEditable);
    int j = getComposingSpanEnd(localEditable);
    int k = i;
    int m = j;
    if (j < i)
    {
      k = j;
      m = i;
    }
    if ((k != -1) && (m != -1))
    {
      removeComposingSpans(localEditable);
    }
    else
    {
      j = Selection.getSelectionStart(localEditable);
      m = Selection.getSelectionEnd(localEditable);
      i = j;
      if (j < 0) {
        i = 0;
      }
      j = m;
      if (m < 0) {
        j = 0;
      }
      k = i;
      m = j;
      if (j < i)
      {
        m = i;
        k = j;
      }
    }
    Object localObject1 = paramCharSequence;
    if (paramBoolean)
    {
      Object localObject3;
      if (!(paramCharSequence instanceof Spannable))
      {
        localObject1 = new SpannableStringBuilder(paramCharSequence);
        Object localObject2 = localObject1;
        ensureDefaultComposingSpans();
        localObject3 = localObject1;
        paramCharSequence = (CharSequence)localObject2;
        if (this.mDefaultComposingSpans != null)
        {
          for (i = 0;; i++)
          {
            paramCharSequence = this.mDefaultComposingSpans;
            if (i >= paramCharSequence.length) {
              break;
            }
            ((Spannable)localObject1).setSpan(paramCharSequence[i], 0, ((Spannable)localObject1).length(), 289);
          }
          localObject3 = localObject1;
          paramCharSequence = (CharSequence)localObject2;
        }
      }
      else
      {
        localObject3 = (Spannable)paramCharSequence;
      }
      setComposingSpans((Spannable)localObject3);
      localObject1 = paramCharSequence;
    }
    if (paramInt > 0) {
      i = paramInt + (m - 1);
    } else {
      i = paramInt + k;
    }
    paramInt = i;
    if (i < 0) {
      paramInt = 0;
    }
    i = paramInt;
    if (paramInt > localEditable.length()) {
      i = localEditable.length();
    }
    Selection.setSelection(localEditable, i);
    localEditable.replace(k, m, (CharSequence)localObject1);
    endBatchEdit();
  }
  
  private void sendCurrentText()
  {
    if (!this.mDummyMode) {
      return;
    }
    Editable localEditable = getEditable();
    if (localEditable != null)
    {
      int i = localEditable.length();
      if (i == 0) {
        return;
      }
      if (i == 1)
      {
        if (this.mKeyCharacterMap == null) {
          this.mKeyCharacterMap = KeyCharacterMap.load(-1);
        }
        Object localObject = new char[1];
        localEditable.getChars(0, 1, (char[])localObject, 0);
        localObject = this.mKeyCharacterMap.getEvents((char[])localObject);
        if (localObject != null)
        {
          for (i = 0; i < localObject.length; i++) {
            sendKeyEvent(localObject[i]);
          }
          localEditable.clear();
          return;
        }
      }
      sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), localEditable.toString(), -1, 0));
      localEditable.clear();
    }
  }
  
  public static void setComposingSpans(Spannable paramSpannable)
  {
    setComposingSpans(paramSpannable, 0, paramSpannable.length());
  }
  
  public static void setComposingSpans(Spannable paramSpannable, int paramInt1, int paramInt2)
  {
    Object[] arrayOfObject = paramSpannable.getSpans(paramInt1, paramInt2, Object.class);
    if (arrayOfObject != null) {
      for (int i = arrayOfObject.length - 1; i >= 0; i--)
      {
        Object localObject = arrayOfObject[i];
        if (localObject == COMPOSING)
        {
          paramSpannable.removeSpan(localObject);
        }
        else
        {
          int j = paramSpannable.getSpanFlags(localObject);
          if ((j & 0x133) != 289) {
            paramSpannable.setSpan(localObject, paramSpannable.getSpanStart(localObject), paramSpannable.getSpanEnd(localObject), j & 0xFFFFFFCC | 0x100 | 0x21);
          }
        }
      }
    }
    paramSpannable.setSpan(COMPOSING, paramInt1, paramInt2, 289);
  }
  
  public boolean beginBatchEdit()
  {
    return false;
  }
  
  public boolean clearMetaKeyStates(int paramInt)
  {
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return false;
    }
    MetaKeyKeyListener.clearMetaKeyState(localEditable, paramInt);
    return true;
  }
  
  public void closeConnection()
  {
    finishComposingText();
  }
  
  public boolean commitCompletion(CompletionInfo paramCompletionInfo)
  {
    return false;
  }
  
  public boolean commitContent(InputContentInfo paramInputContentInfo, int paramInt, Bundle paramBundle)
  {
    return false;
  }
  
  public boolean commitCorrection(CorrectionInfo paramCorrectionInfo)
  {
    return false;
  }
  
  public boolean commitText(CharSequence paramCharSequence, int paramInt)
  {
    replaceText(paramCharSequence, paramInt, false);
    sendCurrentText();
    return true;
  }
  
  public boolean deleteSurroundingText(int paramInt1, int paramInt2)
  {
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return false;
    }
    beginBatchEdit();
    int i = Selection.getSelectionStart(localEditable);
    int j = Selection.getSelectionEnd(localEditable);
    int k = i;
    int m = j;
    if (i > j)
    {
      k = j;
      m = i;
    }
    int n = getComposingSpanStart(localEditable);
    int i1 = getComposingSpanEnd(localEditable);
    j = n;
    i = i1;
    if (i1 < n)
    {
      j = i1;
      i = n;
    }
    int i2 = k;
    i1 = m;
    if (j != -1)
    {
      i2 = k;
      i1 = m;
      if (i != -1)
      {
        n = k;
        if (j < k) {
          n = j;
        }
        i2 = n;
        i1 = m;
        if (i > m)
        {
          i1 = i;
          i2 = n;
        }
      }
    }
    k = 0;
    if (paramInt1 > 0)
    {
      k = i2 - paramInt1;
      paramInt1 = k;
      if (k < 0) {
        paramInt1 = 0;
      }
      localEditable.delete(paramInt1, i2);
      k = i2 - paramInt1;
    }
    if (paramInt2 > 0)
    {
      k = i1 - k;
      paramInt2 = k + paramInt2;
      paramInt1 = paramInt2;
      if (paramInt2 > localEditable.length()) {
        paramInt1 = localEditable.length();
      }
      localEditable.delete(k, paramInt1);
    }
    endBatchEdit();
    return true;
  }
  
  public boolean deleteSurroundingTextInCodePoints(int paramInt1, int paramInt2)
  {
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return false;
    }
    beginBatchEdit();
    int i = Selection.getSelectionStart(localEditable);
    int j = Selection.getSelectionEnd(localEditable);
    int k = i;
    int m = j;
    if (i > j)
    {
      k = j;
      m = i;
    }
    int n = getComposingSpanStart(localEditable);
    int i1 = getComposingSpanEnd(localEditable);
    j = n;
    i = i1;
    if (i1 < n)
    {
      j = i1;
      i = n;
    }
    int i2 = k;
    i1 = m;
    if (j != -1)
    {
      i2 = k;
      i1 = m;
      if (i != -1)
      {
        n = k;
        if (j < k) {
          n = j;
        }
        i2 = n;
        i1 = m;
        if (i > m)
        {
          i1 = i;
          i2 = n;
        }
      }
    }
    if ((i2 >= 0) && (i1 >= 0))
    {
      paramInt1 = findIndexBackward(localEditable, i2, Math.max(paramInt1, 0));
      if (paramInt1 != INVALID_INDEX)
      {
        k = findIndexForward(localEditable, i1, Math.max(paramInt2, 0));
        if (k != INVALID_INDEX)
        {
          paramInt2 = i2 - paramInt1;
          if (paramInt2 > 0) {
            localEditable.delete(paramInt1, i2);
          }
          if (k - i1 > 0) {
            localEditable.delete(i1 - paramInt2, k - paramInt2);
          }
        }
      }
    }
    endBatchEdit();
    return true;
  }
  
  public boolean endBatchEdit()
  {
    return false;
  }
  
  public boolean finishComposingText()
  {
    Editable localEditable = getEditable();
    if (localEditable != null)
    {
      beginBatchEdit();
      removeComposingSpans(localEditable);
      sendCurrentText();
      endBatchEdit();
    }
    return true;
  }
  
  public int getCursorCapsMode(int paramInt)
  {
    if (this.mDummyMode) {
      return 0;
    }
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return 0;
    }
    int i = Selection.getSelectionStart(localEditable);
    int j = Selection.getSelectionEnd(localEditable);
    int k = i;
    if (i > j) {
      k = j;
    }
    return TextUtils.getCapsMode(localEditable, k, paramInt);
  }
  
  public Editable getEditable()
  {
    if (this.mEditable == null)
    {
      this.mEditable = Editable.Factory.getInstance().newEditable("");
      Selection.setSelection(this.mEditable, 0);
    }
    return this.mEditable;
  }
  
  public ExtractedText getExtractedText(ExtractedTextRequest paramExtractedTextRequest, int paramInt)
  {
    return null;
  }
  
  public Handler getHandler()
  {
    return null;
  }
  
  public CharSequence getSelectedText(int paramInt)
  {
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return null;
    }
    int i = Selection.getSelectionStart(localEditable);
    int j = Selection.getSelectionEnd(localEditable);
    int k = i;
    int m = j;
    if (i > j)
    {
      k = j;
      m = i;
    }
    if ((k != m) && (k >= 0))
    {
      if ((paramInt & 0x1) != 0) {
        return localEditable.subSequence(k, m);
      }
      return TextUtils.substring(localEditable, k, m);
    }
    return null;
  }
  
  public CharSequence getTextAfterCursor(int paramInt1, int paramInt2)
  {
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return null;
    }
    int i = Selection.getSelectionStart(localEditable);
    int j = Selection.getSelectionEnd(localEditable);
    int k = j;
    if (i > j) {
      k = i;
    }
    i = k;
    if (k < 0) {
      i = 0;
    }
    k = paramInt1;
    if (i + paramInt1 > localEditable.length()) {
      k = localEditable.length() - i;
    }
    if ((paramInt2 & 0x1) != 0) {
      return localEditable.subSequence(i, i + k);
    }
    return TextUtils.substring(localEditable, i, i + k);
  }
  
  public CharSequence getTextBeforeCursor(int paramInt1, int paramInt2)
  {
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return null;
    }
    int i = Selection.getSelectionStart(localEditable);
    int j = Selection.getSelectionEnd(localEditable);
    int k = i;
    if (i > j) {
      k = j;
    }
    if (k <= 0) {
      return "";
    }
    i = paramInt1;
    if (paramInt1 > k) {
      i = k;
    }
    if ((paramInt2 & 0x1) != 0) {
      return localEditable.subSequence(k - i, k);
    }
    return TextUtils.substring(localEditable, k - i, k);
  }
  
  public boolean performContextMenuAction(int paramInt)
  {
    return false;
  }
  
  public boolean performEditorAction(int paramInt)
  {
    long l = SystemClock.uptimeMillis();
    sendKeyEvent(new KeyEvent(l, l, 0, 66, 0, 0, -1, 0, 22));
    sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), l, 1, 66, 0, 0, -1, 0, 22));
    return true;
  }
  
  public boolean performPrivateCommand(String paramString, Bundle paramBundle)
  {
    return false;
  }
  
  public boolean reportFullscreenMode(boolean paramBoolean)
  {
    return true;
  }
  
  public boolean requestCursorUpdates(int paramInt)
  {
    return false;
  }
  
  public boolean sendKeyEvent(KeyEvent paramKeyEvent)
  {
    this.mIMM.dispatchKeyEventFromInputMethod(this.mTargetView, paramKeyEvent);
    return false;
  }
  
  public boolean setComposingRegion(int paramInt1, int paramInt2)
  {
    Editable localEditable = getEditable();
    if (localEditable != null)
    {
      beginBatchEdit();
      removeComposingSpans(localEditable);
      int i = paramInt1;
      int j = i;
      paramInt1 = paramInt2;
      if (i > paramInt2)
      {
        paramInt1 = i;
        j = paramInt2;
      }
      i = localEditable.length();
      paramInt2 = j;
      if (j < 0) {
        paramInt2 = 0;
      }
      j = paramInt1;
      if (paramInt1 < 0) {
        j = 0;
      }
      paramInt1 = paramInt2;
      if (paramInt2 > i) {
        paramInt1 = i;
      }
      paramInt2 = j;
      if (j > i) {
        paramInt2 = i;
      }
      ensureDefaultComposingSpans();
      if (this.mDefaultComposingSpans != null) {
        for (j = 0;; j++)
        {
          Object[] arrayOfObject = this.mDefaultComposingSpans;
          if (j >= arrayOfObject.length) {
            break;
          }
          localEditable.setSpan(arrayOfObject[j], paramInt1, paramInt2, 289);
        }
      }
      localEditable.setSpan(COMPOSING, paramInt1, paramInt2, 289);
      sendCurrentText();
      endBatchEdit();
    }
    return true;
  }
  
  public boolean setComposingText(CharSequence paramCharSequence, int paramInt)
  {
    replaceText(paramCharSequence, paramInt, true);
    return true;
  }
  
  public boolean setSelection(int paramInt1, int paramInt2)
  {
    Editable localEditable = getEditable();
    if (localEditable == null) {
      return false;
    }
    int i = localEditable.length();
    if ((paramInt1 <= i) && (paramInt2 <= i) && (paramInt1 >= 0) && (paramInt2 >= 0))
    {
      if ((paramInt1 == paramInt2) && (MetaKeyKeyListener.getMetaState(localEditable, 2048) != 0)) {
        Selection.extendSelection(localEditable, paramInt1);
      } else {
        Selection.setSelection(localEditable, paramInt1, paramInt2);
      }
      return true;
    }
    return true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/BaseInputConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */