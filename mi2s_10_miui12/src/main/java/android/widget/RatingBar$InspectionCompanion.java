package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class RatingBar$InspectionCompanion
  implements InspectionCompanion<RatingBar>
{
  private int mIsIndicatorId;
  private int mNumStarsId;
  private boolean mPropertiesMapped = false;
  private int mRatingId;
  private int mStepSizeId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mIsIndicatorId = paramPropertyMapper.mapBoolean("isIndicator", 16843079);
    this.mNumStarsId = paramPropertyMapper.mapInt("numStars", 16843076);
    this.mRatingId = paramPropertyMapper.mapFloat("rating", 16843077);
    this.mStepSizeId = paramPropertyMapper.mapFloat("stepSize", 16843078);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(RatingBar paramRatingBar, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mIsIndicatorId, paramRatingBar.isIndicator());
      paramPropertyReader.readInt(this.mNumStarsId, paramRatingBar.getNumStars());
      paramPropertyReader.readFloat(this.mRatingId, paramRatingBar.getRating());
      paramPropertyReader.readFloat(this.mStepSizeId, paramRatingBar.getStepSize());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/RatingBar$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */