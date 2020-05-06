package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class RelativeLayout$InspectionCompanion
  implements InspectionCompanion<RelativeLayout>
{
  private int mGravityId;
  private int mIgnoreGravityId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mGravityId = paramPropertyMapper.mapGravity("gravity", 16842927);
    this.mIgnoreGravityId = paramPropertyMapper.mapInt("ignoreGravity", 16843263);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(RelativeLayout paramRelativeLayout, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readGravity(this.mGravityId, paramRelativeLayout.getGravity());
      paramPropertyReader.readInt(this.mIgnoreGravityId, paramRelativeLayout.getIgnoreGravity());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RelativeLayout$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */