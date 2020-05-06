package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class TableRow$LayoutParams$InspectionCompanion
  implements InspectionCompanion<TableRow.LayoutParams>
{
  private int mLayout_columnId;
  private int mLayout_spanId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mLayout_columnId = paramPropertyMapper.mapInt("layout_column", 16843084);
    this.mLayout_spanId = paramPropertyMapper.mapInt("layout_span", 16843085);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(TableRow.LayoutParams paramLayoutParams, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readInt(this.mLayout_columnId, paramLayoutParams.column);
      paramPropertyReader.readInt(this.mLayout_spanId, paramLayoutParams.span);
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TableRow$LayoutParams$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */