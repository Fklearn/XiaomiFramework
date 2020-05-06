package android.view;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class ViewGroup$InspectionCompanion
  implements InspectionCompanion<ViewGroup>
{
  private int mAddStatesFromChildrenId;
  private int mAlwaysDrawnWithCacheId;
  private int mAnimationCacheId;
  private int mClipChildrenId;
  private int mClipToPaddingId;
  private int mDescendantFocusabilityId;
  private int mLayoutAnimationId;
  private int mLayoutModeId;
  private int mPersistentDrawingCacheId;
  private boolean mPropertiesMapped = false;
  private int mSplitMotionEventsId;
  private int mTouchscreenBlocksFocusId;
  private int mTransitionGroupId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mAddStatesFromChildrenId = paramPropertyMapper.mapBoolean("addStatesFromChildren", 16842992);
    this.mAlwaysDrawnWithCacheId = paramPropertyMapper.mapBoolean("alwaysDrawnWithCache", 16842991);
    this.mAnimationCacheId = paramPropertyMapper.mapBoolean("animationCache", 16842989);
    this.mClipChildrenId = paramPropertyMapper.mapBoolean("clipChildren", 16842986);
    this.mClipToPaddingId = paramPropertyMapper.mapBoolean("clipToPadding", 16842987);
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(131072, "beforeDescendants");
    localSparseArray.put(262144, "afterDescendants");
    localSparseArray.put(393216, "blocksDescendants");
    Objects.requireNonNull(localSparseArray);
    this.mDescendantFocusabilityId = paramPropertyMapper.mapIntEnum("descendantFocusability", 16842993, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mLayoutAnimationId = paramPropertyMapper.mapObject("layoutAnimation", 16842988);
    localSparseArray = new SparseArray();
    localSparseArray.put(0, "clipBounds");
    localSparseArray.put(1, "opticalBounds");
    Objects.requireNonNull(localSparseArray);
    this.mLayoutModeId = paramPropertyMapper.mapIntEnum("layoutMode", 16843738, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    localSparseArray = new SparseArray();
    localSparseArray.put(0, "none");
    localSparseArray.put(1, "animation");
    localSparseArray.put(2, "scrolling");
    localSparseArray.put(3, "all");
    Objects.requireNonNull(localSparseArray);
    this.mPersistentDrawingCacheId = paramPropertyMapper.mapIntEnum("persistentDrawingCache", 16842990, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mSplitMotionEventsId = paramPropertyMapper.mapBoolean("splitMotionEvents", 16843503);
    this.mTouchscreenBlocksFocusId = paramPropertyMapper.mapBoolean("touchscreenBlocksFocus", 16843919);
    this.mTransitionGroupId = paramPropertyMapper.mapBoolean("transitionGroup", 16843777);
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(ViewGroup paramViewGroup, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readBoolean(this.mAddStatesFromChildrenId, paramViewGroup.addStatesFromChildren());
      paramPropertyReader.readBoolean(this.mAlwaysDrawnWithCacheId, paramViewGroup.isAlwaysDrawnWithCacheEnabled());
      paramPropertyReader.readBoolean(this.mAnimationCacheId, paramViewGroup.isAnimationCacheEnabled());
      paramPropertyReader.readBoolean(this.mClipChildrenId, paramViewGroup.getClipChildren());
      paramPropertyReader.readBoolean(this.mClipToPaddingId, paramViewGroup.getClipToPadding());
      paramPropertyReader.readIntEnum(this.mDescendantFocusabilityId, paramViewGroup.getDescendantFocusability());
      paramPropertyReader.readObject(this.mLayoutAnimationId, paramViewGroup.getLayoutAnimation());
      paramPropertyReader.readIntEnum(this.mLayoutModeId, paramViewGroup.getLayoutMode());
      paramPropertyReader.readIntEnum(this.mPersistentDrawingCacheId, paramViewGroup.getPersistentDrawingCache());
      paramPropertyReader.readBoolean(this.mSplitMotionEventsId, paramViewGroup.isMotionEventSplittingEnabled());
      paramPropertyReader.readBoolean(this.mTouchscreenBlocksFocusId, paramViewGroup.getTouchscreenBlocksFocus());
      paramPropertyReader.readBoolean(this.mTransitionGroupId, paramViewGroup.isTransitionGroup());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewGroup$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */