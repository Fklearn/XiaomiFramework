package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.icu.text.DecimalFormatSymbols;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.style.TtsSpan.VerbatimBuilder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R.styleable;
import com.android.internal.widget.NumericTextView;
import com.android.internal.widget.NumericTextView.OnValueChangedListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

class TimePickerClockDelegate
  extends TimePicker.AbstractTimePickerDelegate
{
  private static final int AM = 0;
  private static final int[] ATTRS_DISABLED_ALPHA = { 16842803 };
  private static final int[] ATTRS_TEXT_COLOR = { 16842904 };
  private static final long DELAY_COMMIT_MILLIS = 2000L;
  private static final int FROM_EXTERNAL_API = 0;
  private static final int FROM_INPUT_PICKER = 2;
  private static final int FROM_RADIAL_PICKER = 1;
  private static final int HOURS_IN_HALF_DAY = 12;
  private static final int HOUR_INDEX = 0;
  private static final int MINUTE_INDEX = 1;
  private static final int PM = 1;
  private boolean mAllowAutoAdvance;
  private final RadioButton mAmLabel;
  private final View mAmPmLayout;
  private final View.OnClickListener mClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      switch (paramAnonymousView.getId())
      {
      default: 
        return;
      case 16909278: 
        TimePickerClockDelegate.this.setAmOrPm(1);
        break;
      case 16909135: 
        TimePickerClockDelegate.this.setCurrentItemShowing(1, true, true);
        break;
      case 16909004: 
        TimePickerClockDelegate.this.setCurrentItemShowing(0, true, true);
        break;
      case 16908726: 
        TimePickerClockDelegate.this.setAmOrPm(0);
      }
      TimePickerClockDelegate.this.tryVibrate();
    }
  };
  private final Runnable mCommitHour = new Runnable()
  {
    public void run()
    {
      TimePickerClockDelegate localTimePickerClockDelegate = TimePickerClockDelegate.this;
      localTimePickerClockDelegate.setHour(localTimePickerClockDelegate.mHourView.getValue());
    }
  };
  private final Runnable mCommitMinute = new Runnable()
  {
    public void run()
    {
      TimePickerClockDelegate localTimePickerClockDelegate = TimePickerClockDelegate.this;
      localTimePickerClockDelegate.setMinute(localTimePickerClockDelegate.mMinuteView.getValue());
    }
  };
  private int mCurrentHour;
  private int mCurrentMinute;
  private final NumericTextView.OnValueChangedListener mDigitEnteredListener = new NumericTextView.OnValueChangedListener()
  {
    public void onValueChanged(NumericTextView paramAnonymousNumericTextView, int paramAnonymousInt, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      Runnable localRunnable;
      NumericTextView localNumericTextView;
      if (paramAnonymousNumericTextView == TimePickerClockDelegate.this.mHourView)
      {
        localRunnable = TimePickerClockDelegate.this.mCommitHour;
        if (paramAnonymousNumericTextView.isFocused()) {
          localNumericTextView = TimePickerClockDelegate.this.mMinuteView;
        } else {
          localNumericTextView = null;
        }
      }
      else
      {
        if (paramAnonymousNumericTextView != TimePickerClockDelegate.this.mMinuteView) {
          return;
        }
        localRunnable = TimePickerClockDelegate.this.mCommitMinute;
        localNumericTextView = null;
      }
      paramAnonymousNumericTextView.removeCallbacks(localRunnable);
      if (paramAnonymousBoolean1) {
        if (paramAnonymousBoolean2)
        {
          localRunnable.run();
          if (localNumericTextView != null) {
            localNumericTextView.requestFocus();
          }
        }
        else
        {
          paramAnonymousNumericTextView.postDelayed(localRunnable, 2000L);
        }
      }
      return;
    }
  };
  private final View.OnFocusChangeListener mFocusListener = new View.OnFocusChangeListener()
  {
    public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
      {
        switch (paramAnonymousView.getId())
        {
        default: 
          return;
        case 16909278: 
          TimePickerClockDelegate.this.setAmOrPm(1);
          break;
        case 16909135: 
          TimePickerClockDelegate.this.setCurrentItemShowing(1, true, true);
          break;
        case 16909004: 
          TimePickerClockDelegate.this.setCurrentItemShowing(0, true, true);
          break;
        case 16908726: 
          TimePickerClockDelegate.this.setAmOrPm(0);
        }
        TimePickerClockDelegate.this.tryVibrate();
      }
    }
  };
  private boolean mHourFormatShowLeadingZero;
  private boolean mHourFormatStartsAtZero;
  private final NumericTextView mHourView;
  private boolean mIs24Hour;
  private boolean mIsAmPmAtLeft = false;
  private boolean mIsAmPmAtTop = false;
  private boolean mIsEnabled = true;
  private boolean mLastAnnouncedIsHour;
  private CharSequence mLastAnnouncedText;
  private final NumericTextView mMinuteView;
  private final RadialTimePickerView.OnValueSelectedListener mOnValueSelectedListener = new RadialTimePickerView.OnValueSelectedListener()
  {
    public void onValueSelected(int paramAnonymousInt1, int paramAnonymousInt2, boolean paramAnonymousBoolean)
    {
      int i = 0;
      int j = 0;
      int k = 0;
      if (paramAnonymousInt1 != 0)
      {
        if (paramAnonymousInt1 != 1)
        {
          k = j;
        }
        else
        {
          if (TimePickerClockDelegate.this.getMinute() != paramAnonymousInt2) {
            k = 1;
          }
          TimePickerClockDelegate.this.setMinuteInternal(paramAnonymousInt2, 1, true);
        }
      }
      else
      {
        paramAnonymousInt1 = i;
        if (TimePickerClockDelegate.this.getHour() != paramAnonymousInt2) {
          paramAnonymousInt1 = 1;
        }
        if ((TimePickerClockDelegate.this.mAllowAutoAdvance) && (paramAnonymousBoolean)) {
          i = 1;
        } else {
          i = 0;
        }
        Object localObject = TimePickerClockDelegate.this;
        if (i == 0) {
          paramAnonymousBoolean = true;
        } else {
          paramAnonymousBoolean = false;
        }
        ((TimePickerClockDelegate)localObject).setHourInternal(paramAnonymousInt2, 1, paramAnonymousBoolean, true);
        k = paramAnonymousInt1;
        if (i != 0)
        {
          TimePickerClockDelegate.this.setCurrentItemShowing(1, true, false);
          paramAnonymousInt2 = TimePickerClockDelegate.this.getLocalizedHour(paramAnonymousInt2);
          localObject = TimePickerClockDelegate.this.mDelegator;
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append(paramAnonymousInt2);
          localStringBuilder.append(". ");
          localStringBuilder.append(TimePickerClockDelegate.this.mSelectMinutes);
          ((TimePicker)localObject).announceForAccessibility(localStringBuilder.toString());
          k = paramAnonymousInt1;
        }
      }
      if ((TimePickerClockDelegate.this.mOnTimeChangedListener != null) && (k != 0)) {
        TimePickerClockDelegate.this.mOnTimeChangedListener.onTimeChanged(TimePickerClockDelegate.this.mDelegator, TimePickerClockDelegate.this.getHour(), TimePickerClockDelegate.this.getMinute());
      }
    }
  };
  private final TextInputTimePickerView.OnValueTypedListener mOnValueTypedListener = new TextInputTimePickerView.OnValueTypedListener()
  {
    public void onValueChanged(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (paramAnonymousInt1 != 0)
      {
        if (paramAnonymousInt1 != 1)
        {
          if (paramAnonymousInt1 == 2) {
            TimePickerClockDelegate.this.setAmOrPm(paramAnonymousInt2);
          }
        }
        else {
          TimePickerClockDelegate.this.setMinuteInternal(paramAnonymousInt2, 2, true);
        }
      }
      else {
        TimePickerClockDelegate.this.setHourInternal(paramAnonymousInt2, 2, false, true);
      }
    }
  };
  private final RadioButton mPmLabel;
  private boolean mRadialPickerModeEnabled = true;
  private final View mRadialTimePickerHeader;
  private final ImageButton mRadialTimePickerModeButton;
  private final String mRadialTimePickerModeEnabledDescription;
  private final RadialTimePickerView mRadialTimePickerView;
  private final String mSelectHours;
  private final String mSelectMinutes;
  private final TextView mSeparatorView;
  private final Calendar mTempCalendar;
  private final View mTextInputPickerHeader;
  private final String mTextInputPickerModeEnabledDescription;
  private final TextInputTimePickerView mTextInputPickerView;
  
  public TimePickerClockDelegate(TimePicker paramTimePicker, Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramTimePicker, paramContext);
    TypedArray localTypedArray = this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TimePicker, paramInt1, paramInt2);
    Object localObject1 = (LayoutInflater)this.mContext.getSystemService("layout_inflater");
    Object localObject2 = this.mContext.getResources();
    this.mSelectHours = ((Resources)localObject2).getString(17041076);
    this.mSelectMinutes = ((Resources)localObject2).getString(17041080);
    localObject2 = ((LayoutInflater)localObject1).inflate(localTypedArray.getResourceId(12, 17367348), paramTimePicker);
    ((View)localObject2).setSaveFromParentEnabled(false);
    this.mRadialTimePickerHeader = ((View)localObject2).findViewById(16909505);
    this.mRadialTimePickerHeader.setOnTouchListener(new NearestTouchDelegate(null));
    this.mHourView = ((NumericTextView)((View)localObject2).findViewById(16909004));
    this.mHourView.setOnClickListener(this.mClickListener);
    this.mHourView.setOnFocusChangeListener(this.mFocusListener);
    this.mHourView.setOnDigitEnteredListener(this.mDigitEnteredListener);
    this.mHourView.setAccessibilityDelegate(new ClickActionDelegate(paramContext, 17041076));
    this.mSeparatorView = ((TextView)((View)localObject2).findViewById(16909376));
    this.mMinuteView = ((NumericTextView)((View)localObject2).findViewById(16909135));
    this.mMinuteView.setOnClickListener(this.mClickListener);
    this.mMinuteView.setOnFocusChangeListener(this.mFocusListener);
    this.mMinuteView.setOnDigitEnteredListener(this.mDigitEnteredListener);
    this.mMinuteView.setAccessibilityDelegate(new ClickActionDelegate(paramContext, 17041080));
    this.mMinuteView.setRange(0, 59);
    this.mAmPmLayout = ((View)localObject2).findViewById(16908728);
    this.mAmPmLayout.setOnTouchListener(new NearestTouchDelegate(null));
    paramTimePicker = TimePicker.getAmPmStrings(paramContext);
    this.mAmLabel = ((RadioButton)this.mAmPmLayout.findViewById(16908726));
    this.mAmLabel.setText(obtainVerbatim(paramTimePicker[0]));
    this.mAmLabel.setOnClickListener(this.mClickListener);
    ensureMinimumTextWidth(this.mAmLabel);
    this.mPmLabel = ((RadioButton)this.mAmPmLayout.findViewById(16909278));
    this.mPmLabel.setText(obtainVerbatim(paramTimePicker[1]));
    this.mPmLabel.setOnClickListener(this.mClickListener);
    ensureMinimumTextWidth(this.mPmLabel);
    paramTimePicker = null;
    int i = localTypedArray.getResourceId(1, 0);
    if (i != 0)
    {
      localObject1 = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, i);
      paramTimePicker = applyLegacyColorFixes(((TypedArray)localObject1).getColorStateList(0));
      ((TypedArray)localObject1).recycle();
    }
    localObject1 = paramTimePicker;
    if (paramTimePicker == null) {
      localObject1 = localTypedArray.getColorStateList(11);
    }
    this.mTextInputPickerHeader = ((View)localObject2).findViewById(16909038);
    if (localObject1 != null)
    {
      this.mHourView.setTextColor((ColorStateList)localObject1);
      this.mSeparatorView.setTextColor((ColorStateList)localObject1);
      this.mMinuteView.setTextColor((ColorStateList)localObject1);
      this.mAmLabel.setTextColor((ColorStateList)localObject1);
      this.mPmLabel.setTextColor((ColorStateList)localObject1);
    }
    if (localTypedArray.hasValueOrEmpty(0))
    {
      this.mRadialTimePickerHeader.setBackground(localTypedArray.getDrawable(0));
      this.mTextInputPickerHeader.setBackground(localTypedArray.getDrawable(0));
    }
    localTypedArray.recycle();
    this.mRadialTimePickerView = ((RadialTimePickerView)((View)localObject2).findViewById(16909306));
    this.mRadialTimePickerView.applyAttributes(paramAttributeSet, paramInt1, paramInt2);
    this.mRadialTimePickerView.setOnValueSelectedListener(this.mOnValueSelectedListener);
    this.mTextInputPickerView = ((TextInputTimePickerView)((View)localObject2).findViewById(16909041));
    this.mTextInputPickerView.setListener(this.mOnValueTypedListener);
    this.mRadialTimePickerModeButton = ((ImageButton)((View)localObject2).findViewById(16909520));
    this.mRadialTimePickerModeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        TimePickerClockDelegate.this.toggleRadialPickerMode();
      }
    });
    this.mRadialTimePickerModeEnabledDescription = paramContext.getResources().getString(17041244);
    this.mTextInputPickerModeEnabledDescription = paramContext.getResources().getString(17041245);
    this.mAllowAutoAdvance = true;
    updateHourFormat();
    this.mTempCalendar = Calendar.getInstance(this.mLocale);
    initialize(this.mTempCalendar.get(11), this.mTempCalendar.get(12), this.mIs24Hour, 0);
  }
  
  private ColorStateList applyLegacyColorFixes(ColorStateList paramColorStateList)
  {
    if ((paramColorStateList != null) && (!paramColorStateList.hasState(16843518)))
    {
      int i;
      int j;
      if (paramColorStateList.hasState(16842913))
      {
        i = paramColorStateList.getColorForState(StateSet.get(10), 0);
        j = paramColorStateList.getColorForState(StateSet.get(8), 0);
      }
      else
      {
        i = paramColorStateList.getDefaultColor();
        j = multiplyAlphaComponent(i, this.mContext.obtainStyledAttributes(ATTRS_DISABLED_ALPHA).getFloat(0, 0.3F));
      }
      if ((i != 0) && (j != 0))
      {
        paramColorStateList = new int[0];
        return new ColorStateList(new int[][] { { 16843518 }, paramColorStateList }, new int[] { i, j });
      }
      return null;
    }
    return paramColorStateList;
  }
  
  private static void ensureMinimumTextWidth(TextView paramTextView)
  {
    paramTextView.measure(0, 0);
    int i = paramTextView.getMeasuredWidth();
    paramTextView.setMinWidth(i);
    paramTextView.setMinimumWidth(i);
  }
  
  private int getCurrentItemShowing()
  {
    return this.mRadialTimePickerView.getCurrentItemShowing();
  }
  
  private static String getHourMinSeparatorFromPattern(String paramString)
  {
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      int k = paramString.charAt(j);
      if (k != 32) {
        if (k != 39)
        {
          if ((k != 72) && (k != 75) && (k != 104) && (k != 107))
          {
            if (i != 0) {
              return Character.toString(paramString.charAt(j));
            }
          }
          else {
            i = 1;
          }
        }
        else if (i != 0)
        {
          paramString = new SpannableStringBuilder(paramString.substring(j));
          return paramString.subSequence(0, DateFormat.appendQuotedText(paramString, 0)).toString();
        }
      }
    }
    return ":";
  }
  
  private int getLocalizedHour(int paramInt)
  {
    int i = paramInt;
    if (!this.mIs24Hour) {
      i = paramInt % 12;
    }
    paramInt = i;
    if (!this.mHourFormatStartsAtZero)
    {
      paramInt = i;
      if (i == 0) {
        if (this.mIs24Hour) {
          paramInt = 24;
        } else {
          paramInt = 12;
        }
      }
    }
    return paramInt;
  }
  
  private void initialize(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    this.mCurrentHour = paramInt1;
    this.mCurrentMinute = paramInt2;
    this.mIs24Hour = paramBoolean;
    updateUI(paramInt3);
  }
  
  private static int lastIndexOfAny(String paramString, char[] paramArrayOfChar)
  {
    int i = paramArrayOfChar.length;
    if (i > 0) {
      for (int j = paramString.length() - 1; j >= 0; j--)
      {
        int k = paramString.charAt(j);
        for (int m = 0; m < i; m++) {
          if (k == paramArrayOfChar[m]) {
            return j;
          }
        }
      }
    }
    return -1;
  }
  
  private int multiplyAlphaComponent(int paramInt, float paramFloat)
  {
    return (int)((paramInt >> 24 & 0xFF) * paramFloat + 0.5F) << 24 | 0xFFFFFF & paramInt;
  }
  
  static final CharSequence obtainVerbatim(String paramString)
  {
    return new SpannableStringBuilder().append(paramString, new TtsSpan.VerbatimBuilder(paramString).build(), 0);
  }
  
  private void onTimeChanged()
  {
    this.mDelegator.sendAccessibilityEvent(4);
    if (this.mOnTimeChangedListener != null) {
      this.mOnTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
    }
    if (this.mAutoFillChangeListener != null) {
      this.mAutoFillChangeListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
    }
  }
  
  private void setAmOrPm(int paramInt)
  {
    updateAmPmLabelStates(paramInt);
    if (this.mRadialTimePickerView.setAmOrPm(paramInt))
    {
      this.mCurrentHour = getHour();
      updateTextInputPicker();
      if (this.mOnTimeChangedListener != null) {
        this.mOnTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
      }
    }
  }
  
  private void setAmPmStart(boolean paramBoolean)
  {
    RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)this.mAmPmLayout.getLayoutParams();
    int i;
    if ((localLayoutParams.getRule(1) == 0) && (localLayoutParams.getRule(0) == 0))
    {
      if ((localLayoutParams.getRule(3) != 0) || (localLayoutParams.getRule(2) != 0))
      {
        if (this.mIsAmPmAtTop == paramBoolean) {
          return;
        }
        if (paramBoolean)
        {
          i = localLayoutParams.getRule(3);
          localLayoutParams.removeRule(3);
          localLayoutParams.addRule(2, i);
        }
        else
        {
          i = localLayoutParams.getRule(2);
          localLayoutParams.removeRule(2);
          localLayoutParams.addRule(3, i);
        }
        View localView = this.mRadialTimePickerHeader.findViewById(i);
        i = localView.getPaddingTop();
        int j = localView.getPaddingBottom();
        localView.setPadding(localView.getPaddingLeft(), j, localView.getPaddingRight(), i);
        this.mIsAmPmAtTop = paramBoolean;
      }
    }
    else
    {
      i = (int)(this.mContext.getResources().getDisplayMetrics().density * 8.0F);
      boolean bool;
      if (TextUtils.getLayoutDirectionFromLocale(this.mLocale) == 0) {
        bool = paramBoolean;
      } else {
        bool = paramBoolean ^ true;
      }
      if (bool)
      {
        localLayoutParams.removeRule(1);
        localLayoutParams.addRule(0, this.mHourView.getId());
      }
      else
      {
        localLayoutParams.removeRule(0);
        localLayoutParams.addRule(1, this.mMinuteView.getId());
      }
      if (paramBoolean)
      {
        localLayoutParams.setMarginStart(0);
        localLayoutParams.setMarginEnd(i);
      }
      else
      {
        localLayoutParams.setMarginStart(i);
        localLayoutParams.setMarginEnd(0);
      }
      this.mIsAmPmAtLeft = bool;
    }
    this.mAmPmLayout.setLayoutParams(localLayoutParams);
  }
  
  private void setCurrentItemShowing(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mRadialTimePickerView.setCurrentItemShowing(paramInt, paramBoolean1);
    if (paramInt == 0)
    {
      if (paramBoolean2) {
        this.mDelegator.announceForAccessibility(this.mSelectHours);
      }
    }
    else if (paramBoolean2) {
      this.mDelegator.announceForAccessibility(this.mSelectMinutes);
    }
    NumericTextView localNumericTextView = this.mHourView;
    paramBoolean2 = false;
    if (paramInt == 0) {
      paramBoolean1 = true;
    } else {
      paramBoolean1 = false;
    }
    localNumericTextView.setActivated(paramBoolean1);
    localNumericTextView = this.mMinuteView;
    paramBoolean1 = paramBoolean2;
    if (paramInt == 1) {
      paramBoolean1 = true;
    }
    localNumericTextView.setActivated(paramBoolean1);
  }
  
  private void setHourInternal(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mCurrentHour == paramInt1) {
      return;
    }
    resetAutofilledValue();
    this.mCurrentHour = paramInt1;
    updateHeaderHour(paramInt1, paramBoolean1);
    updateHeaderAmPm();
    int i = 1;
    if (paramInt2 != 1)
    {
      this.mRadialTimePickerView.setCurrentHour(paramInt1);
      RadialTimePickerView localRadialTimePickerView = this.mRadialTimePickerView;
      if (paramInt1 < 12) {
        i = 0;
      }
      localRadialTimePickerView.setAmOrPm(i);
    }
    if (paramInt2 != 2) {
      updateTextInputPicker();
    }
    this.mDelegator.invalidate();
    if (paramBoolean2) {
      onTimeChanged();
    }
  }
  
  private void setMinuteInternal(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (this.mCurrentMinute == paramInt1) {
      return;
    }
    resetAutofilledValue();
    this.mCurrentMinute = paramInt1;
    updateHeaderMinute(paramInt1, true);
    if (paramInt2 != 1) {
      this.mRadialTimePickerView.setCurrentMinute(paramInt1);
    }
    if (paramInt2 != 2) {
      updateTextInputPicker();
    }
    this.mDelegator.invalidate();
    if (paramBoolean) {
      onTimeChanged();
    }
  }
  
  private void toggleRadialPickerMode()
  {
    if (this.mRadialPickerModeEnabled)
    {
      this.mRadialTimePickerView.setVisibility(8);
      this.mRadialTimePickerHeader.setVisibility(8);
      this.mTextInputPickerHeader.setVisibility(0);
      this.mTextInputPickerView.setVisibility(0);
      this.mRadialTimePickerModeButton.setImageResource(17301797);
      this.mRadialTimePickerModeButton.setContentDescription(this.mRadialTimePickerModeEnabledDescription);
      this.mRadialPickerModeEnabled = false;
    }
    else
    {
      this.mRadialTimePickerView.setVisibility(0);
      this.mRadialTimePickerHeader.setVisibility(0);
      this.mTextInputPickerHeader.setVisibility(8);
      this.mTextInputPickerView.setVisibility(8);
      this.mRadialTimePickerModeButton.setImageResource(17301874);
      this.mRadialTimePickerModeButton.setContentDescription(this.mTextInputPickerModeEnabledDescription);
      updateTextInputPicker();
      InputMethodManager localInputMethodManager = (InputMethodManager)this.mContext.getSystemService(InputMethodManager.class);
      if (localInputMethodManager != null) {
        localInputMethodManager.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
      }
      this.mRadialPickerModeEnabled = true;
    }
  }
  
  private void tryAnnounceForAccessibility(CharSequence paramCharSequence, boolean paramBoolean)
  {
    if ((this.mLastAnnouncedIsHour != paramBoolean) || (!paramCharSequence.equals(this.mLastAnnouncedText)))
    {
      this.mDelegator.announceForAccessibility(paramCharSequence);
      this.mLastAnnouncedText = paramCharSequence;
      this.mLastAnnouncedIsHour = paramBoolean;
    }
  }
  
  private void tryVibrate()
  {
    this.mDelegator.performHapticFeedback(4);
  }
  
  private void updateAmPmLabelStates(int paramInt)
  {
    boolean bool1 = false;
    if (paramInt == 0) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mAmLabel.setActivated(bool2);
    this.mAmLabel.setChecked(bool2);
    boolean bool2 = bool1;
    if (paramInt == 1) {
      bool2 = true;
    }
    this.mPmLabel.setActivated(bool2);
    this.mPmLabel.setChecked(bool2);
  }
  
  private void updateHeaderAmPm()
  {
    if (this.mIs24Hour)
    {
      this.mAmPmLayout.setVisibility(8);
    }
    else
    {
      setAmPmStart(DateFormat.getBestDateTimePattern(this.mLocale, "hm").startsWith("a"));
      int i;
      if (this.mCurrentHour < 12) {
        i = 0;
      } else {
        i = 1;
      }
      updateAmPmLabelStates(i);
    }
  }
  
  private void updateHeaderHour(int paramInt, boolean paramBoolean)
  {
    paramInt = getLocalizedHour(paramInt);
    this.mHourView.setValue(paramInt);
    if (paramBoolean) {
      tryAnnounceForAccessibility(this.mHourView.getText(), true);
    }
  }
  
  private void updateHeaderMinute(int paramInt, boolean paramBoolean)
  {
    this.mMinuteView.setValue(paramInt);
    if (paramBoolean) {
      tryAnnounceForAccessibility(this.mMinuteView.getText(), false);
    }
  }
  
  private void updateHeaderSeparator()
  {
    Locale localLocale = this.mLocale;
    if (this.mIs24Hour) {
      str = "Hm";
    } else {
      str = "hm";
    }
    String str = getHourMinSeparatorFromPattern(DateFormat.getBestDateTimePattern(localLocale, str));
    this.mSeparatorView.setText(str);
    this.mTextInputPickerView.updateSeparator(str);
  }
  
  private void updateHourFormat()
  {
    Locale localLocale = this.mLocale;
    if (this.mIs24Hour) {
      localObject = "Hm";
    } else {
      localObject = "hm";
    }
    Object localObject = DateFormat.getBestDateTimePattern(localLocale, (String)localObject);
    int i = ((String)localObject).length();
    boolean bool1 = false;
    int j = 0;
    int i3;
    for (int k = 0;; k++)
    {
      bool2 = bool1;
      i1 = j;
      if (k >= i) {
        break label150;
      }
      i3 = ((String)localObject).charAt(k);
      if ((i3 == 72) || (i3 == 104) || (i3 == 75) || (i3 == 107)) {
        break;
      }
    }
    j = i3;
    boolean bool2 = bool1;
    int i1 = j;
    if (k + 1 < i)
    {
      bool2 = bool1;
      i1 = j;
      if (i3 == ((String)localObject).charAt(k + 1))
      {
        bool2 = true;
        i1 = j;
      }
    }
    label150:
    this.mHourFormatShowLeadingZero = bool2;
    if ((i1 != 75) && (i1 != 72)) {
      bool2 = false;
    } else {
      bool2 = true;
    }
    this.mHourFormatStartsAtZero = bool2;
    int m = true ^ this.mHourFormatStartsAtZero;
    if (this.mIs24Hour) {
      i1 = 23;
    } else {
      i1 = 11;
    }
    this.mHourView.setRange(m, i1 + m);
    this.mHourView.setShowLeadingZeroes(this.mHourFormatShowLeadingZero);
    localObject = DecimalFormatSymbols.getInstance(this.mLocale).getDigitStrings();
    int n = 0;
    for (int i2 = 0; i2 < 10; i2++) {
      n = Math.max(n, localObject[i2].length());
    }
    this.mTextInputPickerView.setHourFormat(n * 2);
  }
  
  private void updateRadialPicker(int paramInt)
  {
    this.mRadialTimePickerView.initialize(this.mCurrentHour, this.mCurrentMinute, this.mIs24Hour);
    setCurrentItemShowing(paramInt, false, true);
  }
  
  private void updateTextInputPicker()
  {
    TextInputTimePickerView localTextInputTimePickerView = this.mTextInputPickerView;
    int i = getLocalizedHour(this.mCurrentHour);
    int j = this.mCurrentMinute;
    int k;
    if (this.mCurrentHour < 12) {
      k = 0;
    } else {
      k = 1;
    }
    localTextInputTimePickerView.updateTextInputValues(i, j, k, this.mIs24Hour, this.mHourFormatStartsAtZero);
  }
  
  private void updateUI(int paramInt)
  {
    updateHeaderAmPm();
    updateHeaderHour(this.mCurrentHour, false);
    updateHeaderSeparator();
    updateHeaderMinute(this.mCurrentMinute, false);
    updateRadialPicker(paramInt);
    updateTextInputPicker();
    this.mDelegator.invalidate();
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    onPopulateAccessibilityEvent(paramAccessibilityEvent);
    return true;
  }
  
  public View getAmView()
  {
    return this.mAmLabel;
  }
  
  public int getBaseline()
  {
    return -1;
  }
  
  public int getHour()
  {
    int i = this.mRadialTimePickerView.getCurrentHour();
    if (this.mIs24Hour) {
      return i;
    }
    if (this.mRadialTimePickerView.getAmOrPm() == 1) {
      return i % 12 + 12;
    }
    return i % 12;
  }
  
  public View getHourView()
  {
    return this.mHourView;
  }
  
  public int getMinute()
  {
    return this.mRadialTimePickerView.getCurrentMinute();
  }
  
  public View getMinuteView()
  {
    return this.mMinuteView;
  }
  
  public View getPmView()
  {
    return this.mPmLabel;
  }
  
  public boolean is24Hour()
  {
    return this.mIs24Hour;
  }
  
  public boolean isEnabled()
  {
    return this.mIsEnabled;
  }
  
  public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    int i;
    if (this.mIs24Hour) {
      i = 0x1 | 0x80;
    } else {
      i = 0x1 | 0x40;
    }
    this.mTempCalendar.set(11, getHour());
    this.mTempCalendar.set(12, getMinute());
    String str1 = DateUtils.formatDateTime(this.mContext, this.mTempCalendar.getTimeInMillis(), i);
    String str2;
    if (this.mRadialTimePickerView.getCurrentItemShowing() == 0) {
      str2 = this.mSelectHours;
    } else {
      str2 = this.mSelectMinutes;
    }
    paramAccessibilityEvent = paramAccessibilityEvent.getText();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(str1);
    localStringBuilder.append(" ");
    localStringBuilder.append(str2);
    paramAccessibilityEvent.add(localStringBuilder.toString());
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof TimePicker.AbstractTimePickerDelegate.SavedState))
    {
      paramParcelable = (TimePicker.AbstractTimePickerDelegate.SavedState)paramParcelable;
      initialize(paramParcelable.getHour(), paramParcelable.getMinute(), paramParcelable.is24HourMode(), paramParcelable.getCurrentItemShowing());
      this.mRadialTimePickerView.invalidate();
    }
  }
  
  public Parcelable onSaveInstanceState(Parcelable paramParcelable)
  {
    return new TimePicker.AbstractTimePickerDelegate.SavedState(paramParcelable, getHour(), getMinute(), is24Hour(), getCurrentItemShowing());
  }
  
  public void setDate(int paramInt1, int paramInt2)
  {
    setHourInternal(paramInt1, 0, true, false);
    setMinuteInternal(paramInt2, 0, false);
    onTimeChanged();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mHourView.setEnabled(paramBoolean);
    this.mMinuteView.setEnabled(paramBoolean);
    this.mAmLabel.setEnabled(paramBoolean);
    this.mPmLabel.setEnabled(paramBoolean);
    this.mRadialTimePickerView.setEnabled(paramBoolean);
    this.mIsEnabled = paramBoolean;
  }
  
  public void setHour(int paramInt)
  {
    setHourInternal(paramInt, 0, true, true);
  }
  
  public void setIs24Hour(boolean paramBoolean)
  {
    if (this.mIs24Hour != paramBoolean)
    {
      this.mIs24Hour = paramBoolean;
      this.mCurrentHour = getHour();
      updateHourFormat();
      updateUI(this.mRadialTimePickerView.getCurrentItemShowing());
    }
  }
  
  public void setMinute(int paramInt)
  {
    setMinuteInternal(paramInt, 0, true);
  }
  
  public boolean validateInput()
  {
    return this.mTextInputPickerView.validateInput();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ChangeSource {}
  
  private static class ClickActionDelegate
    extends View.AccessibilityDelegate
  {
    private final AccessibilityNodeInfo.AccessibilityAction mClickAction;
    
    public ClickActionDelegate(Context paramContext, int paramInt)
    {
      this.mClickAction = new AccessibilityNodeInfo.AccessibilityAction(16, paramContext.getString(paramInt));
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.addAction(this.mClickAction);
    }
  }
  
  private static class NearestTouchDelegate
    implements View.OnTouchListener
  {
    private View mInitialTouchTarget;
    
    private View findNearestChild(ViewGroup paramViewGroup, int paramInt1, int paramInt2)
    {
      Object localObject = null;
      int i = Integer.MAX_VALUE;
      int j = 0;
      int k = paramViewGroup.getChildCount();
      while (j < k)
      {
        View localView = paramViewGroup.getChildAt(j);
        int m = paramInt1 - (localView.getLeft() + localView.getWidth() / 2);
        int n = paramInt2 - (localView.getTop() + localView.getHeight() / 2);
        n = m * m + n * n;
        m = i;
        if (i > n)
        {
          localObject = localView;
          m = n;
        }
        j++;
        i = m;
      }
      return (View)localObject;
    }
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getActionMasked();
      if (i == 0) {
        if ((paramView instanceof ViewGroup)) {
          this.mInitialTouchTarget = findNearestChild((ViewGroup)paramView, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
        } else {
          this.mInitialTouchTarget = null;
        }
      }
      View localView = this.mInitialTouchTarget;
      if (localView == null) {
        return false;
      }
      float f1 = paramView.getScrollX() - localView.getLeft();
      float f2 = paramView.getScrollY() - localView.getTop();
      paramMotionEvent.offsetLocation(f1, f2);
      boolean bool = localView.dispatchTouchEvent(paramMotionEvent);
      paramMotionEvent.offsetLocation(-f1, -f2);
      if ((i == 1) || (i == 3)) {
        this.mInitialTouchTarget = null;
      }
      return bool;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TimePickerClockDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */