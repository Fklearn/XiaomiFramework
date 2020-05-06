package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class ListView$InspectionCompanion
  implements InspectionCompanion<ListView>
{
  private int mDividerHeightId;
  private int mDividerId;
  private int mFooterDividersEnabledId;
  private int mHeaderDividersEnabledId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mDividerId = paramPropertyMapper.mapObject("divider", 16843049);
    this.mDividerHeightId = paramPropertyMapper.mapInt("dividerHeight", 16843050);
    this.mFooterDividersEnabledId = paramPropertyMapper.mapBoolean("footerDividersEnabled", 16843311);
    this.mHeaderDividersEnabledId = paramPropertyMapper.mapBoolean("headerDividersEnabled", 16843310);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ListView paramListView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readObject(this.mDividerId, paramListView.getDivider());
      paramPropertyReader.readInt(this.mDividerHeightId, paramListView.getDividerHeight());
      paramPropertyReader.readBoolean(this.mFooterDividersEnabledId, paramListView.areFooterDividersEnabled());
      paramPropertyReader.readBoolean(this.mHeaderDividersEnabledId, paramListView.areHeaderDividersEnabled());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ListView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */