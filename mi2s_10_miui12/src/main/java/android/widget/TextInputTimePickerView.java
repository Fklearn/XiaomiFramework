package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

public class TextInputTimePickerView
  extends RelativeLayout
{
  private static final int AM = 0;
  public static final int AMPM = 2;
  public static final int HOURS = 0;
  public static final int MINUTES = 1;
  private static final int PM = 1;
  private final Spinner mAmPmSpinner;
  private final TextView mErrorLabel;
  private boolean mErrorShowing;
  private final EditText mHourEditText;
  private boolean mHourFormatStartsAtZero;
  private final TextView mHourLabel;
  private final TextView mInputSeparatorView;
  private boolean mIs24Hour;
  private OnValueTypedListener mListener;
  private final EditText mMinuteEditText;
  private final TextView mMinuteLabel;
  private boolean mTimeSet;
  
  public TextInputTimePickerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TextInputTimePickerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TextInputTimePickerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TextInputTimePickerView(final Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    inflate(paramContext, 17367349, this);
    this.mHourEditText = ((EditText)findViewById(16909039));
    this.mMinuteEditText = ((EditText)findViewById(16909040));
    this.mInputSeparatorView = ((TextView)findViewById(16909042));
    this.mErrorLabel = ((TextView)findViewById(16909068));
    this.mHourLabel = ((TextView)findViewById(16909069));
    this.mMinuteLabel = ((TextView)findViewById(16909070));
    this.mHourEditText.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        if ((TextInputTimePickerView.this.parseAndSetHourInternal(paramAnonymousEditable.toString())) && (paramAnonymousEditable.length() > 1) && (!((AccessibilityManager)paramContext.getSystemService("accessibility")).isEnabled())) {
          TextInputTimePickerView.this.mMinuteEditText.requestFocus();
        }
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
    this.mMinuteEditText.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        TextInputTimePickerView.this.parseAndSetMinuteInternal(paramAnonymousEditable.toString());
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
    this.mAmPmSpinner = ((Spinner)findViewById(16908727));
    paramAttributeSet = TimePicker.getAmPmStrings(paramContext);
    paramContext = new ArrayAdapter(paramContext, 17367049);
    paramContext.add(TimePickerClockDelegate.obtainVerbatim(paramAttributeSet[0]));
    paramContext.add(TimePickerClockDelegate.obtainVerbatim(paramAttributeSet[1]));
    this.mAmPmSpinner.setAdapter(paramContext);
    this.mAmPmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
    {
      public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (paramAnonymousInt == 0) {
          TextInputTimePickerView.this.mListener.onValueChanged(2, 0);
        } else {
          TextInputTimePickerView.this.mListener.onValueChanged(2, 1);
        }
      }
      
      public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
    });
  }
  
  private int getHourOfDayFromLocalizedHour(int paramInt)
  {
    int i = paramInt;
    int j;
    if (this.mIs24Hour)
    {
      j = i;
      if (!this.mHourFormatStartsAtZero)
      {
        j = i;
        if (paramInt == 24) {
          j = 0;
        }
      }
    }
    else
    {
      int k = i;
      if (!this.mHourFormatStartsAtZero)
      {
        k = i;
        if (paramInt == 12) {
          k = 0;
        }
      }
      j = k;
      if (this.mAmPmSpinner.getSelectedItemPosition() == 1) {
        j = k + 12;
      }
    }
    return j;
  }
  
  private boolean isTimeSet()
  {
    return this.mTimeSet;
  }
  
  private boolean isValidLocalizedHour(int paramInt)
  {
    boolean bool1 = this.mHourFormatStartsAtZero;
    boolean bool2 = true;
    int i = bool1 ^ true;
    int j;
    if (this.mIs24Hour) {
      j = 23;
    } else {
      j = 11;
    }
    if ((paramInt < i) || (paramInt > j + i)) {
      bool2 = false;
    }
    return bool2;
  }
  
  private boolean parseAndSetHourInternal(String paramString)
  {
    try
    {
      int i = Integer.parseInt(paramString);
      boolean bool = isValidLocalizedHour(i);
      int j = 1;
      if (!bool)
      {
        if (this.mHourFormatStartsAtZero) {
          j = 0;
        }
        int k;
        if (this.mIs24Hour) {
          k = 23;
        } else {
          k = j + 11;
        }
        this.mListener.onValueChanged(0, getHourOfDayFromLocalizedHour(MathUtils.constrain(i, j, k)));
        return false;
      }
      this.mListener.onValueChanged(0, getHourOfDayFromLocalizedHour(i));
      setTimeSet(true);
      return true;
    }
    catch (NumberFormatException paramString) {}
    return false;
  }
  
  private boolean parseAndSetMinuteInternal(String paramString)
  {
    try
    {
      int i = Integer.parseInt(paramString);
      if ((i >= 0) && (i <= 59))
      {
        this.mListener.onValueChanged(1, i);
        setTimeSet(true);
        return true;
      }
      this.mListener.onValueChanged(1, MathUtils.constrain(i, 0, 59));
      return false;
    }
    catch (NumberFormatException paramString) {}
    return false;
  }
  
  private void setError(boolean paramBoolean)
  {
    this.mErrorShowing = paramBoolean;
    TextView localTextView = this.mErrorLabel;
    int i = 0;
    if (paramBoolean) {
      j = 0;
    } else {
      j = 4;
    }
    localTextView.setVisibility(j);
    localTextView = this.mHourLabel;
    if (paramBoolean) {
      j = 4;
    } else {
      j = 0;
    }
    localTextView.setVisibility(j);
    localTextView = this.mMinuteLabel;
    int j = i;
    if (paramBoolean) {
      j = 4;
    }
    localTextView.setVisibility(j);
  }
  
  private void setTimeSet(boolean paramBoolean)
  {
    if ((!this.mTimeSet) && (!paramBoolean)) {
      paramBoolean = false;
    } else {
      paramBoolean = true;
    }
    this.mTimeSet = paramBoolean;
  }
  
  void setHourFormat(int paramInt)
  {
    this.mHourEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(paramInt) });
    this.mMinuteEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(paramInt) });
    LocaleList localLocaleList = this.mContext.getResources().getConfiguration().getLocales();
    this.mHourEditText.setImeHintLocales(localLocaleList);
    this.mMinuteEditText.setImeHintLocales(localLocaleList);
  }
  
  void setListener(OnValueTypedListener paramOnValueTypedListener)
  {
    this.mListener = paramOnValueTypedListener;
  }
  
  void updateSeparator(String paramString)
  {
    this.mInputSeparatorView.setText(paramString);
  }
  
  void updateTextInputValues(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mIs24Hour = paramBoolean1;
    this.mHourFormatStartsAtZero = paramBoolean2;
    Spinner localSpinner = this.mAmPmSpinner;
    int i;
    if (paramBoolean1) {
      i = 4;
    } else {
      i = 0;
    }
    localSpinner.setVisibility(i);
    if (paramInt3 == 0) {
      this.mAmPmSpinner.setSelection(0);
    } else {
      this.mAmPmSpinner.setSelection(1);
    }
    if (isTimeSet())
    {
      this.mHourEditText.setText(String.format("%d", new Object[] { Integer.valueOf(paramInt1) }));
      this.mMinuteEditText.setText(String.format("%02d", new Object[] { Integer.valueOf(paramInt2) }));
    }
    else
    {
      this.mHourEditText.setHint(String.format("%d", new Object[] { Integer.valueOf(paramInt1) }));
      this.mMinuteEditText.setHint(String.format("%02d", new Object[] { Integer.valueOf(paramInt2) }));
    }
    if (this.mErrorShowing) {
      validateInput();
    }
  }
  
  boolean validateInput()
  {
    String str1;
    if (TextUtils.isEmpty(this.mHourEditText.getText())) {
      str1 = this.mHourEditText.getHint().toString();
    } else {
      str1 = this.mHourEditText.getText().toString();
    }
    String str2;
    if (TextUtils.isEmpty(this.mMinuteEditText.getText())) {
      str2 = this.mMinuteEditText.getHint().toString();
    } else {
      str2 = this.mMinuteEditText.getText().toString();
    }
    boolean bool1 = parseAndSetHourInternal(str1);
    boolean bool2 = true;
    if ((bool1) && (parseAndSetMinuteInternal(str2))) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    if (bool1) {
      bool2 = false;
    }
    setError(bool2);
    return bool1;
  }
  
  static abstract interface OnValueTypedListener
  {
    public abstract void onValueChanged(int paramInt1, int paramInt2);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TextInputTimePickerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */