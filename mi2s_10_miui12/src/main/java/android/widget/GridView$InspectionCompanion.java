package android.widget;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class GridView$InspectionCompanion
  implements InspectionCompanion<GridView>
{
  private int mColumnWidthId;
  private int mGravityId;
  private int mHorizontalSpacingId;
  private int mNumColumnsId;
  private boolean mPropertiesMapped = false;
  private int mStretchModeId;
  private int mVerticalSpacingId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mColumnWidthId = paramPropertyMapper.mapInt("columnWidth", 16843031);
    this.mGravityId = paramPropertyMapper.mapGravity("gravity", 16842927);
    this.mHorizontalSpacingId = paramPropertyMapper.mapInt("horizontalSpacing", 16843028);
    this.mNumColumnsId = paramPropertyMapper.mapInt("numColumns", 16843032);
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(0, "none");
    localSparseArray.put(1, "spacingWidth");
    localSparseArray.put(2, "columnWidth");
    localSparseArray.put(3, "spacingWidthUniform");
    Objects.requireNonNull(localSparseArray);
    this.mStretchModeId = paramPropertyMapper.mapIntEnum("stretchMode", 16843030, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mVerticalSpacingId = paramPropertyMapper.mapInt("verticalSpacing", 16843029);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(GridView paramGridView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readInt(this.mColumnWidthId, paramGridView.getColumnWidth());
      paramPropertyReader.readGravity(this.mGravityId, paramGridView.getGravity());
      paramPropertyReader.readInt(this.mHorizontalSpacingId, paramGridView.getHorizontalSpacing());
      paramPropertyReader.readInt(this.mNumColumnsId, paramGridView.getNumColumns());
      paramPropertyReader.readIntEnum(this.mStretchModeId, paramGridView.getStretchMode());
      paramPropertyReader.readInt(this.mVerticalSpacingId, paramGridView.getVerticalSpacing());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/GridView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */