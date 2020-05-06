package android.widget;

import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;

public final class ImageView$InspectionCompanion
  implements InspectionCompanion<ImageView>
{
  private int mAdjustViewBoundsId;
  private int mBaselineAlignBottomId;
  private int mBaselineId;
  private int mBlendModeId;
  private int mCropToPaddingId;
  private int mMaxHeightId;
  private int mMaxWidthId;
  private boolean mPropertiesMapped = false;
  private int mScaleTypeId;
  private int mSrcId;
  private int mTintId;
  private int mTintModeId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mAdjustViewBoundsId = paramPropertyMapper.mapBoolean("adjustViewBounds", 16843038);
    this.mBaselineId = paramPropertyMapper.mapInt("baseline", 16843548);
    this.mBaselineAlignBottomId = paramPropertyMapper.mapBoolean("baselineAlignBottom", 16843042);
    this.mBlendModeId = paramPropertyMapper.mapObject("blendMode", 9);
    this.mCropToPaddingId = paramPropertyMapper.mapBoolean("cropToPadding", 16843043);
    this.mMaxHeightId = paramPropertyMapper.mapInt("maxHeight", 16843040);
    this.mMaxWidthId = paramPropertyMapper.mapInt("maxWidth", 16843039);
    this.mScaleTypeId = paramPropertyMapper.mapObject("scaleType", 16843037);
    this.mSrcId = paramPropertyMapper.mapObject("src", 16843033);
    this.mTintId = paramPropertyMapper.mapObject("tint", 16843041);
    this.mTintModeId = paramPropertyMapper.mapObject("tintMode", 16843771);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ImageView paramImageView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mAdjustViewBoundsId, paramImageView.getAdjustViewBounds());
      paramPropertyReader.readInt(this.mBaselineId, paramImageView.getBaseline());
      paramPropertyReader.readBoolean(this.mBaselineAlignBottomId, paramImageView.getBaselineAlignBottom());
      paramPropertyReader.readObject(this.mBlendModeId, paramImageView.getImageTintBlendMode());
      paramPropertyReader.readBoolean(this.mCropToPaddingId, paramImageView.getCropToPadding());
      paramPropertyReader.readInt(this.mMaxHeightId, paramImageView.getMaxHeight());
      paramPropertyReader.readInt(this.mMaxWidthId, paramImageView.getMaxWidth());
      paramPropertyReader.readObject(this.mScaleTypeId, paramImageView.getScaleType());
      paramPropertyReader.readObject(this.mSrcId, paramImageView.getDrawable());
      paramPropertyReader.readObject(this.mTintId, paramImageView.getImageTintList());
      paramPropertyReader.readObject(this.mTintModeId, paramImageView.getImageTintMode());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ImageView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */