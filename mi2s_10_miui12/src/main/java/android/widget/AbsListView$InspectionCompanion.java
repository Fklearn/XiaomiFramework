package android.widget;

import android.util.SparseArray;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.InspectionCompanion.UninitializedPropertyMapException;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import java.util.Objects;

public final class AbsListView$InspectionCompanion
  implements InspectionCompanion<AbsListView>
{
  private int mCacheColorHintId;
  private int mChoiceModeId;
  private int mDrawSelectorOnTopId;
  private int mFastScrollEnabledId;
  private int mListSelectorId;
  private boolean mPropertiesMapped = false;
  private int mScrollingCacheId;
  private int mSmoothScrollbarId;
  private int mStackFromBottomId;
  private int mTextFilterEnabledId;
  private int mTranscriptModeId;
  
  public void mapProperties(PropertyMapper paramPropertyMapper)
  {
    this.mCacheColorHintId = paramPropertyMapper.mapColor("cacheColorHint", 16843009);
    SparseArray localSparseArray = new SparseArray();
    localSparseArray.put(0, "none");
    localSparseArray.put(1, "singleChoice");
    localSparseArray.put(2, "multipleChoice");
    localSparseArray.put(3, "multipleChoiceModal");
    Objects.requireNonNull(localSparseArray);
    this.mChoiceModeId = paramPropertyMapper.mapIntEnum("choiceMode", 16843051, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mDrawSelectorOnTopId = paramPropertyMapper.mapBoolean("drawSelectorOnTop", 16843004);
    this.mFastScrollEnabledId = paramPropertyMapper.mapBoolean("fastScrollEnabled", 16843302);
    this.mListSelectorId = paramPropertyMapper.mapObject("listSelector", 16843003);
    this.mScrollingCacheId = paramPropertyMapper.mapBoolean("scrollingCache", 16843006);
    this.mSmoothScrollbarId = paramPropertyMapper.mapBoolean("smoothScrollbar", 16843313);
    this.mStackFromBottomId = paramPropertyMapper.mapBoolean("stackFromBottom", 16843005);
    this.mTextFilterEnabledId = paramPropertyMapper.mapBoolean("textFilterEnabled", 16843007);
    localSparseArray = new SparseArray();
    localSparseArray.put(0, "disabled");
    localSparseArray.put(1, "normal");
    localSparseArray.put(2, "alwaysScroll");
    Objects.requireNonNull(localSparseArray);
    this.mTranscriptModeId = paramPropertyMapper.mapIntEnum("transcriptMode", 16843008, new _..Lambda.QY3N4tzLteuFdjRnyJFCbR1ajSI(localSparseArray));
    this.mPropertiesMapped = true;
  }
  
  public void readProperties(AbsListView paramAbsListView, PropertyReader paramPropertyReader)
  {
    if (this.mPropertiesMapped)
    {
      paramPropertyReader.readColor(this.mCacheColorHintId, paramAbsListView.getCacheColorHint());
      paramPropertyReader.readIntEnum(this.mChoiceModeId, paramAbsListView.getChoiceMode());
      paramPropertyReader.readBoolean(this.mDrawSelectorOnTopId, paramAbsListView.isDrawSelectorOnTop());
      paramPropertyReader.readBoolean(this.mFastScrollEnabledId, paramAbsListView.isFastScrollEnabled());
      paramPropertyReader.readObject(this.mListSelectorId, paramAbsListView.getSelector());
      paramPropertyReader.readBoolean(this.mScrollingCacheId, paramAbsListView.isScrollingCacheEnabled());
      paramPropertyReader.readBoolean(this.mSmoothScrollbarId, paramAbsListView.isSmoothScrollbarEnabled());
      paramPropertyReader.readBoolean(this.mStackFromBottomId, paramAbsListView.isStackFromBottom());
      paramPropertyReader.readBoolean(this.mTextFilterEnabledId, paramAbsListView.isTextFilterEnabled());
      paramPropertyReader.readIntEnum(this.mTranscriptModeId, paramAbsListView.getTranscriptMode());
      return;
    }
    throw new InspectionCompanion.UninitializedPropertyMapException();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AbsListView$InspectionCompanion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */