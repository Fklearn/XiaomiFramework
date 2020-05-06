package android.widget;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class LinearLayout$InspectionCompanion
  implements InspectionCompanion<LinearLayout>
{
  private int mBaselineAlignedChildIndexId;
  private int mBaselineAlignedId;
  private int mDividerId;
  private int mGravityId;
  private int mMeasureWithLargestChildId;
  private int mOrientationId;
  private boolean mPropertiesMapped = false;
  private int mWeightSumId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mBaselineAlignedId = paramPropertyMapper.mapBoolean("baselineAligned", 16843046);
    this.mBaselineAlignedChildIndexId = paramPropertyMapper.mapInt("baselineAlignedChildIndex", 16843047);
    this.mDividerId = paramPropertyMapper.mapObject("divider", 16843049);
    this.mGravityId = paramPropertyMapper.mapGravity("gravity", 16842927);
    this.mMeasureWithLargestChildId = paramPropertyMapper.mapBoolean("measureWithLargestChild", 16843476);
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(0, "horizontal");
    localSparseArray.put(1, "vertical");
    Objects.requireNonNull(localSparseArray);
    this.mOrientationId = paramPropertyMapper.mapIntEnum("orientation", 16842948, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mWeightSumId = paramPropertyMapper.mapFloat("weightSum", 16843048);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(LinearLayout paramLinearLayout, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mBaselineAlignedId, paramLinearLayout.isBaselineAligned());
      paramPropertyReader.readInt(this.mBaselineAlignedChildIndexId, paramLinearLayout.getBaselineAlignedChildIndex());
      paramPropertyReader.readObject(this.mDividerId, paramLinearLayout.getDividerDrawable());
      paramPropertyReader.readGravity(this.mGravityId, paramLinearLayout.getGravity());
      paramPropertyReader.readBoolean(this.mMeasureWithLargestChildId, paramLinearLayout.isMeasureWithLargestChildEnabled());
      paramPropertyReader.readIntEnum(this.mOrientationId, paramLinearLayout.getOrientation());
      paramPropertyReader.readFloat(this.mWeightSumId, paramLinearLayout.getWeightSum());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/LinearLayout$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */