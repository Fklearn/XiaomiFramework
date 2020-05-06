package android.widget;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils.TruncateAt;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;

public class EditText
  extends TextView
{
  public EditText(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public EditText(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842862);
  }
  
  public EditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public EditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public void extendSelection(int paramInt)
  {
    Selection.extendSelection(getText(), paramInt);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return EditText.class.getName();
  }
  
  protected boolean getDefaultEditable()
  {
    return true;
  }
  
  protected MovementMethod getDefaultMovementMethod()
  {
    return ArrowKeyMovementMethod.getInstance();
  }
  
  public boolean getFreezesText()
  {
    return true;
  }
  
  public Editable getText()
  {
    CharSequence localCharSequence = super.getText();
    if (localCharSequence == null) {
      return null;
    }
    if ((localCharSequence instanceof Editable)) {
      return (Editable)super.getText();
    }
    super.setText(localCharSequence, TextView.BufferType.EDITABLE);
    return (Editable)super.getText();
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (isEnabled()) {
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT);
    }
  }
  
  public void selectAll()
  {
    Selection.selectAll(getText());
  }
  
  public void setEllipsize(TextUtils.TruncateAt paramTruncateAt)
  {
    if (paramTruncateAt != TextUtils.TruncateAt.MARQUEE)
    {
      super.setEllipsize(paramTruncateAt);
      return;
    }
    throw new IllegalArgumentException("EditText cannot use the ellipsize mode TextUtils.TruncateAt.MARQUEE");
  }
  
  public void setSelection(int paramInt)
  {
    Selection.setSelection(getText(), paramInt);
  }
  
  public void setSelection(int paramInt1, int paramInt2)
  {
    Selection.setSelection(getText(), paramInt1, paramInt2);
  }
  
  public void setText(CharSequence paramCharSequence, TextView.BufferType paramBufferType)
  {
    super.setText(paramCharSequence, TextView.BufferType.EDITABLE);
  }
  
  protected boolean supportsAutoSizeText()
  {
    return false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/EditText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */