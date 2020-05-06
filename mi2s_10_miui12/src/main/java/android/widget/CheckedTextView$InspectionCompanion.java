package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class CheckedTextView$InspectionCompanion
  implements InspectionCompanion<CheckedTextView>
{
  private int mCheckMarkId;
  private int mCheckMarkTintBlendModeId;
  private int mCheckMarkTintId;
  private int mCheckMarkTintModeId;
  private int mCheckedId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mCheckMarkId = paramPropertyMapper.mapObject("checkMark", 16843016);
    this.mCheckMarkTintId = paramPropertyMapper.mapObject("checkMarkTint", 16843943);
    this.mCheckMarkTintBlendModeId = paramPropertyMapper.mapObject("checkMarkTintBlendMode", 3);
    this.mCheckMarkTintModeId = paramPropertyMapper.mapObject("checkMarkTintMode", 16843944);
    this.mCheckedId = paramPropertyMapper.mapBoolean("checked", 16843014);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(CheckedTextView paramCheckedTextView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readObject(this.mCheckMarkId, paramCheckedTextView.getCheckMarkDrawable());
      paramPropertyReader.readObject(this.mCheckMarkTintId, paramCheckedTextView.getCheckMarkTintList());
      paramPropertyReader.readObject(this.mCheckMarkTintBlendModeId, paramCheckedTextView.getCheckMarkTintBlendMode());
      paramPropertyReader.readObject(this.mCheckMarkTintModeId, paramCheckedTextView.getCheckMarkTintMode());
      paramPropertyReader.readBoolean(this.mCheckedId, paramCheckedTextView.isChecked());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CheckedTextView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */