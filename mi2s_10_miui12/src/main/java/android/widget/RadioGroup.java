package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewStructure;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import com.android.internal.R.styleable;

public class RadioGroup
  extends LinearLayout
{
  private static final String LOG_TAG = RadioGroup.class.getSimpleName();
  private int mCheckedId = -1;
  @UnsupportedAppUsage
  private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
  private int mInitialCheckedId = -1;
  @UnsupportedAppUsage
  private OnCheckedChangeListener mOnCheckedChangeListener;
  private PassThroughHierarchyChangeListener mPassThroughListener;
  private boolean mProtectFromCheckedChange = false;
  
  public RadioGroup(Context paramContext)
  {
    super(paramContext);
    setOrientation(1);
    init();
  }
  
  public RadioGroup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (getImportantForAutofill() == 0) {
      setImportantForAutofill(1);
    }
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RadioGroup, 16842878, 0);
    saveAttributeDataForStyleable(paramContext, R.styleable.RadioGroup, paramAttributeSet, localTypedArray, 16842878, 0);
    int i = localTypedArray.getResourceId(1, -1);
    if (i != -1)
    {
      this.mCheckedId = i;
      this.mInitialCheckedId = i;
    }
    setOrientation(localTypedArray.getInt(0, 1));
    localTypedArray.recycle();
    init();
  }
  
  private void init()
  {
    this.mChildOnCheckedChangeListener = new CheckedStateTracker(null);
    this.mPassThroughListener = new PassThroughHierarchyChangeListener(null);
    super.setOnHierarchyChangeListener(this.mPassThroughListener);
  }
  
  private void setCheckedId(int paramInt)
  {
    int i;
    if (paramInt != this.mCheckedId) {
      i = 1;
    } else {
      i = 0;
    }
    this.mCheckedId = paramInt;
    Object localObject = this.mOnCheckedChangeListener;
    if (localObject != null) {
      ((OnCheckedChangeListener)localObject).onCheckedChanged(this, this.mCheckedId);
    }
    if (i != 0)
    {
      localObject = (AutofillManager)this.mContext.getSystemService(AutofillManager.class);
      if (localObject != null) {
        ((AutofillManager)localObject).notifyValueChanged(this);
      }
    }
  }
  
  private void setCheckedStateForView(int paramInt, boolean paramBoolean)
  {
    View localView = findViewById(paramInt);
    if ((localView != null) && ((localView instanceof RadioButton))) {
      ((RadioButton)localView).setChecked(paramBoolean);
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramView instanceof RadioButton))
    {
      RadioButton localRadioButton = (RadioButton)paramView;
      if (localRadioButton.isChecked())
      {
        this.mProtectFromCheckedChange = true;
        int i = this.mCheckedId;
        if (i != -1) {
          setCheckedStateForView(i, false);
        }
        this.mProtectFromCheckedChange = false;
        setCheckedId(localRadioButton.getId());
      }
    }
    super.addView(paramView, paramInt, paramLayoutParams);
  }
  
  public void autofill(AutofillValue paramAutofillValue)
  {
    if (!isEnabled()) {
      return;
    }
    if (!paramAutofillValue.isList())
    {
      String str = LOG_TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramAutofillValue);
      localStringBuilder.append(" could not be autofilled into ");
      localStringBuilder.append(this);
      Log.w(str, localStringBuilder.toString());
      return;
    }
    int i = paramAutofillValue.getListValue();
    paramAutofillValue = getChildAt(i);
    if (paramAutofillValue == null)
    {
      paramAutofillValue = new StringBuilder();
      paramAutofillValue.append("RadioGroup.autoFill(): no child with index ");
      paramAutofillValue.append(i);
      Log.w("View", paramAutofillValue.toString());
      return;
    }
    check(paramAutofillValue.getId());
  }
  
  public void check(int paramInt)
  {
    if ((paramInt != -1) && (paramInt == this.mCheckedId)) {
      return;
    }
    int i = this.mCheckedId;
    if (i != -1) {
      setCheckedStateForView(i, false);
    }
    if (paramInt != -1) {
      setCheckedStateForView(paramInt, true);
    }
    setCheckedId(paramInt);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void clearCheck()
  {
    check(-1);
  }
  
  protected LinearLayout.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return RadioGroup.class.getName();
  }
  
  public int getAutofillType()
  {
    int i;
    if (isEnabled()) {
      i = 3;
    } else {
      i = 0;
    }
    return i;
  }
  
  public AutofillValue getAutofillValue()
  {
    if (!isEnabled()) {
      return null;
    }
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      if (getChildAt(j).getId() == this.mCheckedId) {
        return AutofillValue.forList(j);
      }
    }
    return null;
  }
  
  public int getCheckedRadioButtonId()
  {
    return this.mCheckedId;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    int i = this.mCheckedId;
    if (i != -1)
    {
      this.mProtectFromCheckedChange = true;
      setCheckedStateForView(i, true);
      this.mProtectFromCheckedChange = false;
      setCheckedId(this.mCheckedId);
    }
  }
  
  protected void onProvideStructure(ViewStructure paramViewStructure, int paramInt1, int paramInt2)
  {
    super.onProvideStructure(paramViewStructure, paramInt1, paramInt2);
    boolean bool = true;
    if (paramInt1 == 1)
    {
      if (this.mCheckedId == this.mInitialCheckedId) {
        bool = false;
      }
      paramViewStructure.setDataIsSensitive(bool);
    }
  }
  
  public void setOnCheckedChangeListener(OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    this.mOnCheckedChangeListener = paramOnCheckedChangeListener;
  }
  
  public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener paramOnHierarchyChangeListener)
  {
    PassThroughHierarchyChangeListener.access$202(this.mPassThroughListener, paramOnHierarchyChangeListener);
  }
  
  private class CheckedStateTracker
    implements CompoundButton.OnCheckedChangeListener
  {
    private CheckedStateTracker() {}
    
    public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
    {
      if (RadioGroup.this.mProtectFromCheckedChange) {
        return;
      }
      RadioGroup.access$302(RadioGroup.this, true);
      if (RadioGroup.this.mCheckedId != -1)
      {
        RadioGroup localRadioGroup = RadioGroup.this;
        localRadioGroup.setCheckedStateForView(localRadioGroup.mCheckedId, false);
      }
      RadioGroup.access$302(RadioGroup.this, false);
      int i = paramCompoundButton.getId();
      RadioGroup.this.setCheckedId(i);
    }
  }
  
  public static class LayoutParams
    extends LinearLayout.LayoutParams
  {
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2, paramFloat);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    protected void setBaseAttributes(TypedArray paramTypedArray, int paramInt1, int paramInt2)
    {
      if (paramTypedArray.hasValue(paramInt1)) {
        this.width = paramTypedArray.getLayoutDimension(paramInt1, "layout_width");
      } else {
        this.width = -2;
      }
      if (paramTypedArray.hasValue(paramInt2)) {
        this.height = paramTypedArray.getLayoutDimension(paramInt2, "layout_height");
      } else {
        this.height = -2;
      }
    }
  }
  
  public static abstract interface OnCheckedChangeListener
  {
    public abstract void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt);
  }
  
  private class PassThroughHierarchyChangeListener
    implements ViewGroup.OnHierarchyChangeListener
  {
    private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;
    
    private PassThroughHierarchyChangeListener() {}
    
    public void onChildViewAdded(View paramView1, View paramView2)
    {
      if ((paramView1 == RadioGroup.this) && ((paramView2 instanceof RadioButton)))
      {
        if (paramView2.getId() == -1) {
          paramView2.setId(View.generateViewId());
        }
        ((RadioButton)paramView2).setOnCheckedChangeWidgetListener(RadioGroup.this.mChildOnCheckedChangeListener);
      }
      ViewGroup.OnHierarchyChangeListener localOnHierarchyChangeListener = this.mOnHierarchyChangeListener;
      if (localOnHierarchyChangeListener != null) {
        localOnHierarchyChangeListener.onChildViewAdded(paramView1, paramView2);
      }
    }
    
    public void onChildViewRemoved(View paramView1, View paramView2)
    {
      if ((paramView1 == RadioGroup.this) && ((paramView2 instanceof RadioButton))) {
        ((RadioButton)paramView2).setOnCheckedChangeWidgetListener(null);
      }
      ViewGroup.OnHierarchyChangeListener localOnHierarchyChangeListener = this.mOnHierarchyChangeListener;
      if (localOnHierarchyChangeListener != null) {
        localOnHierarchyChangeListener.onChildViewRemoved(paramView1, paramView2);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RadioGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */