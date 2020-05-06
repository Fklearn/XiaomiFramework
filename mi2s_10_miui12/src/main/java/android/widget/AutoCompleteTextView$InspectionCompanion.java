package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class AutoCompleteTextView$InspectionCompanion
  implements InspectionCompanion<AutoCompleteTextView>
{
  private int mCompletionHintId;
  private int mCompletionThresholdId;
  private int mDropDownHeightId;
  private int mDropDownHorizontalOffsetId;
  private int mDropDownVerticalOffsetId;
  private int mDropDownWidthId;
  private int mPopupBackgroundId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mCompletionHintId = paramPropertyMapper.mapObject("completionHint", 16843122);
    this.mCompletionThresholdId = paramPropertyMapper.mapInt("completionThreshold", 16843124);
    this.mDropDownHeightId = paramPropertyMapper.mapInt("dropDownHeight", 16843395);
    this.mDropDownHorizontalOffsetId = paramPropertyMapper.mapInt("dropDownHorizontalOffset", 16843436);
    this.mDropDownVerticalOffsetId = paramPropertyMapper.mapInt("dropDownVerticalOffset", 16843437);
    this.mDropDownWidthId = paramPropertyMapper.mapInt("dropDownWidth", 16843362);
    this.mPopupBackgroundId = paramPropertyMapper.mapObject("popupBackground", 16843126);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(AutoCompleteTextView paramAutoCompleteTextView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readObject(this.mCompletionHintId, paramAutoCompleteTextView.getCompletionHint());
      paramPropertyReader.readInt(this.mCompletionThresholdId, paramAutoCompleteTextView.getThreshold());
      paramPropertyReader.readInt(this.mDropDownHeightId, paramAutoCompleteTextView.getDropDownHeight());
      paramPropertyReader.readInt(this.mDropDownHorizontalOffsetId, paramAutoCompleteTextView.getDropDownHorizontalOffset());
      paramPropertyReader.readInt(this.mDropDownVerticalOffsetId, paramAutoCompleteTextView.getDropDownVerticalOffset());
      paramPropertyReader.readInt(this.mDropDownWidthId, paramAutoCompleteTextView.getDropDownWidth());
      paramPropertyReader.readObject(this.mPopupBackgroundId, paramAutoCompleteTextView.getDropDownBackground());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AutoCompleteTextView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */