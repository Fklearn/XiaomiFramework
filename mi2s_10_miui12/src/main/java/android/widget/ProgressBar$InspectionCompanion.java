package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class ProgressBar$InspectionCompanion
  implements InspectionCompanion<ProgressBar>
{
  private int mIndeterminateDrawableId;
  private int mIndeterminateId;
  private int mIndeterminateTintBlendModeId;
  private int mIndeterminateTintId;
  private int mIndeterminateTintModeId;
  private int mInterpolatorId;
  private int mMaxId;
  private int mMinId;
  private int mMirrorForRtlId;
  private int mProgressBackgroundTintBlendModeId;
  private int mProgressBackgroundTintId;
  private int mProgressBackgroundTintModeId;
  private int mProgressDrawableId;
  private int mProgressId;
  private int mProgressTintBlendModeId;
  private int mProgressTintId;
  private int mProgressTintModeId;
  private boolean mPropertiesMapped = false;
  private int mSecondaryProgressId;
  private int mSecondaryProgressTintBlendModeId;
  private int mSecondaryProgressTintId;
  private int mSecondaryProgressTintModeId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mIndeterminateId = paramPropertyMapper.mapBoolean("indeterminate", 16843065);
    this.mIndeterminateDrawableId = paramPropertyMapper.mapObject("indeterminateDrawable", 16843067);
    this.mIndeterminateTintId = paramPropertyMapper.mapObject("indeterminateTint", 16843881);
    this.mIndeterminateTintBlendModeId = paramPropertyMapper.mapObject("indeterminateTintBlendMode", 23);
    this.mIndeterminateTintModeId = paramPropertyMapper.mapObject("indeterminateTintMode", 16843882);
    this.mInterpolatorId = paramPropertyMapper.mapObject("interpolator", 16843073);
    this.mMaxId = paramPropertyMapper.mapInt("max", 16843062);
    this.mMinId = paramPropertyMapper.mapInt("min", 16844089);
    this.mMirrorForRtlId = paramPropertyMapper.mapBoolean("mirrorForRtl", 16843726);
    this.mProgressId = paramPropertyMapper.mapInt("progress", 16843063);
    this.mProgressBackgroundTintId = paramPropertyMapper.mapObject("progressBackgroundTint", 16843877);
    this.mProgressBackgroundTintBlendModeId = paramPropertyMapper.mapObject("progressBackgroundTintBlendMode", 19);
    this.mProgressBackgroundTintModeId = paramPropertyMapper.mapObject("progressBackgroundTintMode", 16843878);
    this.mProgressDrawableId = paramPropertyMapper.mapObject("progressDrawable", 16843068);
    this.mProgressTintId = paramPropertyMapper.mapObject("progressTint", 16843875);
    this.mProgressTintBlendModeId = paramPropertyMapper.mapObject("progressTintBlendMode", 17);
    this.mProgressTintModeId = paramPropertyMapper.mapObject("progressTintMode", 16843876);
    this.mSecondaryProgressId = paramPropertyMapper.mapInt("secondaryProgress", 16843064);
    this.mSecondaryProgressTintId = paramPropertyMapper.mapObject("secondaryProgressTint", 16843879);
    this.mSecondaryProgressTintBlendModeId = paramPropertyMapper.mapObject("secondaryProgressTintBlendMode", 21);
    this.mSecondaryProgressTintModeId = paramPropertyMapper.mapObject("secondaryProgressTintMode", 16843880);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ProgressBar paramProgressBar, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mIndeterminateId, paramProgressBar.isIndeterminate());
      paramPropertyReader.readObject(this.mIndeterminateDrawableId, paramProgressBar.getIndeterminateDrawable());
      paramPropertyReader.readObject(this.mIndeterminateTintId, paramProgressBar.getIndeterminateTintList());
      paramPropertyReader.readObject(this.mIndeterminateTintBlendModeId, paramProgressBar.getIndeterminateTintBlendMode());
      paramPropertyReader.readObject(this.mIndeterminateTintModeId, paramProgressBar.getIndeterminateTintMode());
      paramPropertyReader.readObject(this.mInterpolatorId, paramProgressBar.getInterpolator());
      paramPropertyReader.readInt(this.mMaxId, paramProgressBar.getMax());
      paramPropertyReader.readInt(this.mMinId, paramProgressBar.getMin());
      paramPropertyReader.readBoolean(this.mMirrorForRtlId, paramProgressBar.getMirrorForRtl());
      paramPropertyReader.readInt(this.mProgressId, paramProgressBar.getProgress());
      paramPropertyReader.readObject(this.mProgressBackgroundTintId, paramProgressBar.getProgressBackgroundTintList());
      paramPropertyReader.readObject(this.mProgressBackgroundTintBlendModeId, paramProgressBar.getProgressBackgroundTintBlendMode());
      paramPropertyReader.readObject(this.mProgressBackgroundTintModeId, paramProgressBar.getProgressBackgroundTintMode());
      paramPropertyReader.readObject(this.mProgressDrawableId, paramProgressBar.getProgressDrawable());
      paramPropertyReader.readObject(this.mProgressTintId, paramProgressBar.getProgressTintList());
      paramPropertyReader.readObject(this.mProgressTintBlendModeId, paramProgressBar.getProgressTintBlendMode());
      paramPropertyReader.readObject(this.mProgressTintModeId, paramProgressBar.getProgressTintMode());
      paramPropertyReader.readInt(this.mSecondaryProgressId, paramProgressBar.getSecondaryProgress());
      paramPropertyReader.readObject(this.mSecondaryProgressTintId, paramProgressBar.getSecondaryProgressTintList());
      paramPropertyReader.readObject(this.mSecondaryProgressTintBlendModeId, paramProgressBar.getSecondaryProgressTintBlendMode());
      paramPropertyReader.readObject(this.mSecondaryProgressTintModeId, paramProgressBar.getSecondaryProgressTintMode());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ProgressBar$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */