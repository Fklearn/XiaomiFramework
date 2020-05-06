package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class ViewAnimator$InspectionCompanion
  implements InspectionCompanion<ViewAnimator>
{
  private int mAnimateFirstViewId;
  private int mInAnimationId;
  private int mOutAnimationId;
  private boolean mPropertiesMapped = false;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mAnimateFirstViewId = paramPropertyMapper.mapBoolean("animateFirstView", 16843477);
    this.mInAnimationId = paramPropertyMapper.mapObject("inAnimation", 16843127);
    this.mOutAnimationId = paramPropertyMapper.mapObject("outAnimation", 16843128);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ViewAnimator paramViewAnimator, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mAnimateFirstViewId, paramViewAnimator.getAnimateFirstView());
      paramPropertyReader.readObject(this.mInAnimationId, paramViewAnimator.getInAnimation());
      paramPropertyReader.readObject(this.mOutAnimationId, paramViewAnimator.getOutAnimation());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ViewAnimator$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */