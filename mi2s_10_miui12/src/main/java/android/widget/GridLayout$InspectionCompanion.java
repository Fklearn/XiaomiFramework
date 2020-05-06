package android.widget;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class GridLayout$InspectionCompanion
  implements InspectionCompanion<GridLayout>
{
  private int mAlignmentModeId;
  private int mColumnCountId;
  private int mColumnOrderPreservedId;
  private int mOrientationId;
  private boolean mPropertiesMapped = false;
  private int mRowCountId;
  private int mRowOrderPreservedId;
  private int mUseDefaultMarginsId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(0, "alignBounds");
    localSparseArray.put(1, "alignMargins");
    Objects.requireNonNull(localSparseArray);
    this.mAlignmentModeId = paramPropertyMapper.mapIntEnum("alignmentMode", 16843642, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mColumnCountId = paramPropertyMapper.mapInt("columnCount", 16843639);
    this.mColumnOrderPreservedId = paramPropertyMapper.mapBoolean("columnOrderPreserved", 16843640);
    localSparseArray = new SparseArray();
    localSparseArray.put(0, "horizontal");
    localSparseArray.put(1, "vertical");
    Objects.requireNonNull(localSparseArray);
    this.mOrientationId = paramPropertyMapper.mapIntEnum("orientation", 16842948, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mRowCountId = paramPropertyMapper.mapInt("rowCount", 16843637);
    this.mRowOrderPreservedId = paramPropertyMapper.mapBoolean("rowOrderPreserved", 16843638);
    this.mUseDefaultMarginsId = paramPropertyMapper.mapBoolean("useDefaultMargins", 16843641);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(GridLayout paramGridLayout, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readIntEnum(this.mAlignmentModeId, paramGridLayout.getAlignmentMode());
      paramPropertyReader.readInt(this.mColumnCountId, paramGridLayout.getColumnCount());
      paramPropertyReader.readBoolean(this.mColumnOrderPreservedId, paramGridLayout.isColumnOrderPreserved());
      paramPropertyReader.readIntEnum(this.mOrientationId, paramGridLayout.getOrientation());
      paramPropertyReader.readInt(this.mRowCountId, paramGridLayout.getRowCount());
      paramPropertyReader.readBoolean(this.mRowOrderPreservedId, paramGridLayout.isRowOrderPreserved());
      paramPropertyReader.readBoolean(this.mUseDefaultMarginsId, paramGridLayout.getUseDefaultMargins());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/GridLayout$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */