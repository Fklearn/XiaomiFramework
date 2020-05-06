package android.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.AllCaps;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;

@Deprecated
public class DialerFilter
  extends RelativeLayout
{
  public static final int DIGITS_AND_LETTERS = 1;
  public static final int DIGITS_AND_LETTERS_NO_DIGITS = 2;
  public static final int DIGITS_AND_LETTERS_NO_LETTERS = 3;
  public static final int DIGITS_ONLY = 4;
  public static final int LETTERS_ONLY = 5;
  EditText mDigits;
  EditText mHint;
  ImageView mIcon;
  InputFilter[] mInputFilters;
  private boolean mIsQwerty;
  EditText mLetters;
  int mMode;
  EditText mPrimary;
  
  public DialerFilter(Context paramContext)
  {
    super(paramContext);
  }
  
  public DialerFilter(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void makeDigitsPrimary()
  {
    if (this.mPrimary == this.mLetters) {
      swapPrimaryAndHint(false);
    }
  }
  
  private void makeLettersPrimary()
  {
    if (this.mPrimary == this.mDigits) {
      swapPrimaryAndHint(true);
    }
  }
  
  private void swapPrimaryAndHint(boolean paramBoolean)
  {
    Editable localEditable1 = this.mLetters.getText();
    Editable localEditable2 = this.mDigits.getText();
    KeyListener localKeyListener1 = this.mLetters.getKeyListener();
    KeyListener localKeyListener2 = this.mDigits.getKeyListener();
    if (paramBoolean)
    {
      this.mLetters = this.mPrimary;
      this.mDigits = this.mHint;
    }
    else
    {
      this.mLetters = this.mHint;
      this.mDigits = this.mPrimary;
    }
    this.mLetters.setKeyListener(localKeyListener1);
    this.mLetters.setText(localEditable1);
    localEditable1 = this.mLetters.getText();
    Selection.setSelection(localEditable1, localEditable1.length());
    this.mDigits.setKeyListener(localKeyListener2);
    this.mDigits.setText(localEditable2);
    localEditable2 = this.mDigits.getText();
    Selection.setSelection(localEditable2, localEditable2.length());
    this.mPrimary.setFilters(this.mInputFilters);
    this.mHint.setFilters(this.mInputFilters);
  }
  
  public void append(String paramString)
  {
    int i = this.mMode;
    if (i != 1)
    {
      if (i != 2) {
        if ((i != 3) && (i != 4))
        {
          if (i != 5) {
            return;
          }
        }
        else
        {
          this.mDigits.getText().append(paramString);
          return;
        }
      }
      this.mLetters.getText().append(paramString);
    }
    else
    {
      this.mDigits.getText().append(paramString);
      this.mLetters.getText().append(paramString);
    }
  }
  
  public void clearText()
  {
    this.mLetters.getText().clear();
    this.mDigits.getText().clear();
    if (this.mIsQwerty) {
      setMode(1);
    } else {
      setMode(4);
    }
  }
  
  public CharSequence getDigits()
  {
    if (this.mDigits.getVisibility() == 0) {
      return this.mDigits.getText();
    }
    return "";
  }
  
  public CharSequence getFilterText()
  {
    if (this.mMode != 4) {
      return getLetters();
    }
    return getDigits();
  }
  
  public CharSequence getLetters()
  {
    if (this.mLetters.getVisibility() == 0) {
      return this.mLetters.getText();
    }
    return "";
  }
  
  public int getMode()
  {
    return this.mMode;
  }
  
  public boolean isQwertyKeyboard()
  {
    return this.mIsQwerty;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mInputFilters = new InputFilter[] { new InputFilter.AllCaps() };
    this.mHint = ((EditText)findViewById(16908293));
    EditText localEditText = this.mHint;
    if (localEditText != null)
    {
      localEditText.setFilters(this.mInputFilters);
      this.mLetters = this.mHint;
      this.mLetters.setKeyListener(TextKeyListener.getInstance());
      this.mLetters.setMovementMethod(null);
      this.mLetters.setFocusable(false);
      this.mPrimary = ((EditText)findViewById(16908300));
      localEditText = this.mPrimary;
      if (localEditText != null)
      {
        localEditText.setFilters(this.mInputFilters);
        this.mDigits = this.mPrimary;
        this.mDigits.setKeyListener(DialerKeyListener.getInstance());
        this.mDigits.setMovementMethod(null);
        this.mDigits.setFocusable(false);
        this.mIcon = ((ImageView)findViewById(16908294));
        setFocusable(true);
        this.mIsQwerty = true;
        setMode(1);
        return;
      }
      throw new IllegalStateException("DialerFilter must have a child EditText named primary");
    }
    throw new IllegalStateException("DialerFilter must have a child EditText named hint");
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    paramRect = this.mIcon;
    if (paramRect != null)
    {
      if (paramBoolean) {
        paramInt = 0;
      } else {
        paramInt = 8;
      }
      paramRect.setVisibility(paramInt);
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    if (paramInt != 66)
    {
      if (paramInt != 67) {}
      switch (paramInt)
      {
      default: 
        int i = this.mMode;
        if (i != 1)
        {
          if (i != 2) {
            if ((i != 3) && (i != 4))
            {
              if (i != 5)
              {
                bool2 = bool1;
                break;
              }
            }
            else
            {
              bool2 = this.mDigits.onKeyDown(paramInt, paramKeyEvent);
              break;
            }
          }
          bool2 = this.mLetters.onKeyDown(paramInt, paramKeyEvent);
        }
        else
        {
          bool1 = this.mLetters.onKeyDown(paramInt, paramKeyEvent);
          if (KeyEvent.isModifierKey(paramInt))
          {
            this.mDigits.onKeyDown(paramInt, paramKeyEvent);
            bool2 = true;
          }
          else if ((!paramKeyEvent.isPrintingKey()) && (paramInt != 62))
          {
            bool2 = bool1;
            if (paramInt != 61) {
              break;
            }
          }
          else
          {
            if (paramKeyEvent.getMatch(DialerKeyListener.CHARACTERS) != 0)
            {
              bool2 = bool1 & this.mDigits.onKeyDown(paramInt, paramKeyEvent);
            }
            else
            {
              setMode(2);
              bool2 = bool1;
            }
            break;
            i = this.mMode;
            if (i != 1)
            {
              if (i != 2)
              {
                if (i != 3)
                {
                  if (i != 4)
                  {
                    if (i == 5) {
                      bool2 = this.mLetters.onKeyDown(paramInt, paramKeyEvent);
                    }
                  }
                  else {
                    bool2 = this.mDigits.onKeyDown(paramInt, paramKeyEvent);
                  }
                }
                else
                {
                  if (this.mDigits.getText().length() == this.mLetters.getText().length())
                  {
                    this.mLetters.onKeyDown(paramInt, paramKeyEvent);
                    setMode(1);
                  }
                  bool2 = this.mDigits.onKeyDown(paramInt, paramKeyEvent);
                }
              }
              else
              {
                bool1 = this.mLetters.onKeyDown(paramInt, paramKeyEvent);
                bool2 = bool1;
                if (this.mLetters.getText().length() == this.mDigits.getText().length())
                {
                  setMode(1);
                  bool2 = bool1;
                }
              }
            }
            else {
              bool2 = this.mDigits.onKeyDown(paramInt, paramKeyEvent) & this.mLetters.onKeyDown(paramInt, paramKeyEvent);
            }
          }
        }
        break;
      }
    }
    else
    {
      bool2 = bool1;
    }
    if (!bool2) {
      return super.onKeyDown(paramInt, paramKeyEvent);
    }
    return true;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool1 = this.mLetters.onKeyUp(paramInt, paramKeyEvent);
    boolean bool2 = this.mDigits.onKeyUp(paramInt, paramKeyEvent);
    if ((!bool1) && (!bool2)) {
      bool1 = false;
    } else {
      bool1 = true;
    }
    return bool1;
  }
  
  protected void onModeChange(int paramInt1, int paramInt2) {}
  
  public void removeFilterWatcher(TextWatcher paramTextWatcher)
  {
    Editable localEditable;
    if (this.mMode != 4) {
      localEditable = this.mLetters.getText();
    } else {
      localEditable = this.mDigits.getText();
    }
    localEditable.removeSpan(paramTextWatcher);
  }
  
  public void setDigitsWatcher(TextWatcher paramTextWatcher)
  {
    Editable localEditable = this.mDigits.getText();
    ((Spannable)localEditable).setSpan(paramTextWatcher, 0, localEditable.length(), 18);
  }
  
  public void setFilterWatcher(TextWatcher paramTextWatcher)
  {
    if (this.mMode != 4) {
      setLettersWatcher(paramTextWatcher);
    } else {
      setDigitsWatcher(paramTextWatcher);
    }
  }
  
  public void setLettersWatcher(TextWatcher paramTextWatcher)
  {
    Editable localEditable = this.mLetters.getText();
    ((Spannable)localEditable).setSpan(paramTextWatcher, 0, localEditable.length(), 18);
  }
  
  public void setMode(int paramInt)
  {
    if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 3)
        {
          if (paramInt != 4)
          {
            if (paramInt == 5)
            {
              makeLettersPrimary();
              this.mLetters.setVisibility(0);
              this.mDigits.setVisibility(8);
            }
          }
          else
          {
            makeDigitsPrimary();
            this.mLetters.setVisibility(8);
            this.mDigits.setVisibility(0);
          }
        }
        else
        {
          makeDigitsPrimary();
          this.mLetters.setVisibility(4);
          this.mDigits.setVisibility(0);
        }
      }
      else
      {
        makeLettersPrimary();
        this.mLetters.setVisibility(0);
        this.mDigits.setVisibility(4);
      }
    }
    else
    {
      makeDigitsPrimary();
      this.mLetters.setVisibility(0);
      this.mDigits.setVisibility(0);
    }
    int i = this.mMode;
    this.mMode = paramInt;
    onModeChange(i, paramInt);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DialerFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */