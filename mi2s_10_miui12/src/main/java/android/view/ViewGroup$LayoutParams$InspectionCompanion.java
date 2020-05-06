package android.view;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class ViewGroup$LayoutParams$InspectionCompanion
  implements InspectionCompanion<ViewGroup.LayoutParams>
{
  private int mLayout_heightId;
  private int mLayout_widthId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(-2, "wrap_content");
    localSparseArray.put(-1, "match_parent");
    Objects.requireNonNull(localSparseArray);
    this.mLayout_heightId = paramPropertyMapper.mapIntEnum("layout_height", 16842997, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    localSparseArray = new SparseArray();
    localSparseArray.put(-2, "wrap_content");
    localSparseArray.put(-1, "match_parent");
    Objects.requireNonNull(localSparseArray);
    this.mLayout_widthId = paramPropertyMapper.mapIntEnum("layout_width", 16842996, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ViewGroup.LayoutParams paramLayoutParams, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readIntEnum(this.mLayout_heightId, paramLayoutParams.height);
      paramPropertyReader.readIntEnum(this.mLayout_widthId, paramLayoutParams.width);
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewGroup$LayoutParams$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */