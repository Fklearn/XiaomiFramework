package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.autofill.AutofillValue;
import com.android.internal.R.styleable;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_Widget_AbsSpinner.Extension;
import com.miui.internal.variable.api.v29.Android_Widget_AbsSpinner.Interface;

public abstract class AbsSpinner
  extends AdapterView<SpinnerAdapter>
{
  private static final String LOG_TAG = AbsSpinner.class.getSimpleName();
  SpinnerAdapter mAdapter;
  private DataSetObserver mDataSetObserver;
  int mHeightMeasureSpec;
  final RecycleBin mRecycler = new RecycleBin();
  int mSelectionBottomPadding = 0;
  int mSelectionLeftPadding = 0;
  int mSelectionRightPadding = 0;
  int mSelectionTopPadding = 0;
  final Rect mSpinnerPadding = new Rect();
  private Rect mTouchFrame;
  int mWidthMeasureSpec;
  
  static
  {
    Android_Widget_AbsSpinner.Extension.get().bindOriginal(new Android_Widget_AbsSpinner.Interface()
    {
      public void setAdapter(AbsSpinner paramAnonymousAbsSpinner, SpinnerAdapter paramAnonymousSpinnerAdapter)
      {
        paramAnonymousAbsSpinner.originalSetAdapter(paramAnonymousSpinnerAdapter);
      }
    });
  }
  
  public AbsSpinner(Context paramContext)
  {
    super(paramContext);
    initAbsSpinner();
  }
  
  public AbsSpinner(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AbsSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AbsSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    if (getImportantForAutofill() == 0) {
      setImportantForAutofill(1);
    }
    initAbsSpinner();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AbsSpinner, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.AbsSpinner, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramAttributeSet = localTypedArray.getTextArray(0);
    if (paramAttributeSet != null)
    {
      paramContext = new ArrayAdapter(paramContext, 17367048, paramAttributeSet);
      paramContext.setDropDownViewResource(17367049);
      setAdapter(paramContext);
    }
    localTypedArray.recycle();
  }
  
  private void initAbsSpinner()
  {
    setFocusable(true);
    setWillNotDraw(false);
  }
  
  public void autofill(AutofillValue paramAutofillValue)
  {
    if (!isEnabled()) {
      return;
    }
    if (!paramAutofillValue.isList())
    {
      String str = LOG_TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramAutofillValue);
      localStringBuilder.append(" could not be autofilled into ");
      localStringBuilder.append(this);
      Log.w(str, localStringBuilder.toString());
      return;
    }
    setSelection(paramAutofillValue.getListValue());
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    super.dispatchRestoreInstanceState(paramSparseArray);
    handleDataChanged();
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new ViewGroup.LayoutParams(-1, -2);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return AbsSpinner.class.getName();
  }
  
  public SpinnerAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public int getAutofillType()
  {
    int i;
    if (isEnabled()) {
      i = 3;
    } else {
      i = 0;
    }
    return i;
  }
  
  public AutofillValue getAutofillValue()
  {
    AutofillValue localAutofillValue;
    if (isEnabled()) {
      localAutofillValue = AutofillValue.forList(getSelectedItemPosition());
    } else {
      localAutofillValue = null;
    }
    return localAutofillValue;
  }
  
  int getChildHeight(View paramView)
  {
    return paramView.getMeasuredHeight();
  }
  
  int getChildWidth(View paramView)
  {
    return paramView.getMeasuredWidth();
  }
  
  public int getCount()
  {
    return this.mItemCount;
  }
  
  public View getSelectedView()
  {
    if ((this.mItemCount > 0) && (this.mSelectedPosition >= 0)) {
      return getChildAt(this.mSelectedPosition - this.mFirstPosition);
    }
    return null;
  }
  
  abstract void layout(int paramInt, boolean paramBoolean);
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    Object localObject = this.mSpinnerPadding;
    int j = this.mPaddingLeft;
    int k = this.mSelectionLeftPadding;
    if (j > k) {
      k = this.mPaddingLeft;
    }
    ((Rect)localObject).left = k;
    localObject = this.mSpinnerPadding;
    j = this.mPaddingTop;
    k = this.mSelectionTopPadding;
    if (j > k) {
      k = this.mPaddingTop;
    }
    ((Rect)localObject).top = k;
    localObject = this.mSpinnerPadding;
    j = this.mPaddingRight;
    k = this.mSelectionRightPadding;
    if (j > k) {
      k = this.mPaddingRight;
    }
    ((Rect)localObject).right = k;
    localObject = this.mSpinnerPadding;
    j = this.mPaddingBottom;
    k = this.mSelectionBottomPadding;
    if (j > k) {
      k = this.mPaddingBottom;
    }
    ((Rect)localObject).bottom = k;
    if (this.mDataChanged) {
      handleDataChanged();
    }
    int m = 0;
    int n = 0;
    int i1 = 1;
    int i2 = getSelectedItemPosition();
    j = m;
    k = n;
    int i3 = i1;
    if (i2 >= 0)
    {
      localObject = this.mAdapter;
      j = m;
      k = n;
      i3 = i1;
      if (localObject != null)
      {
        j = m;
        k = n;
        i3 = i1;
        if (i2 < ((SpinnerAdapter)localObject).getCount())
        {
          View localView = this.mRecycler.get(i2);
          localObject = localView;
          if (localView == null)
          {
            localView = this.mAdapter.getView(i2, null, this);
            localObject = localView;
            if (localView.getImportantForAccessibility() == 0)
            {
              localView.setImportantForAccessibility(1);
              localObject = localView;
            }
          }
          this.mRecycler.put(i2, (View)localObject);
          if (((View)localObject).getLayoutParams() == null)
          {
            this.mBlockLayoutRequests = true;
            ((View)localObject).setLayoutParams(generateDefaultLayoutParams());
            this.mBlockLayoutRequests = false;
          }
          measureChild((View)localObject, paramInt1, paramInt2);
          j = getChildHeight((View)localObject) + this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
          k = getChildWidth((View)localObject) + this.mSpinnerPadding.left + this.mSpinnerPadding.right;
          i3 = 0;
        }
      }
    }
    n = j;
    j = k;
    if (i3 != 0)
    {
      i3 = this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
      n = i3;
      j = k;
      if (i == 0)
      {
        j = this.mSpinnerPadding.left + this.mSpinnerPadding.right;
        n = i3;
      }
    }
    k = Math.max(n, getSuggestedMinimumHeight());
    j = Math.max(j, getSuggestedMinimumWidth());
    k = resolveSizeAndState(k, paramInt2, 0);
    setMeasuredDimension(resolveSizeAndState(j, paramInt1, 0), k);
    this.mHeightMeasureSpec = paramInt2;
    this.mWidthMeasureSpec = paramInt1;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (paramParcelable.selectedId >= 0L)
    {
      this.mDataChanged = true;
      this.mNeedSync = true;
      this.mSyncRowId = paramParcelable.selectedId;
      this.mSyncPosition = paramParcelable.position;
      this.mSyncMode = 0;
      requestLayout();
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.selectedId = getSelectedItemId();
    if (localSavedState.selectedId >= 0L) {
      localSavedState.position = getSelectedItemPosition();
    } else {
      localSavedState.position = -1;
    }
    return localSavedState;
  }
  
  void originalSetAdapter(SpinnerAdapter paramSpinnerAdapter)
  {
    SpinnerAdapter localSpinnerAdapter = this.mAdapter;
    if (localSpinnerAdapter != null)
    {
      localSpinnerAdapter.unregisterDataSetObserver(this.mDataSetObserver);
      resetList();
    }
    this.mAdapter = paramSpinnerAdapter;
    int i = -1;
    this.mOldSelectedPosition = -1;
    this.mOldSelectedRowId = Long.MIN_VALUE;
    if (this.mAdapter != null)
    {
      this.mOldItemCount = this.mItemCount;
      this.mItemCount = this.mAdapter.getCount();
      checkFocus();
      this.mDataSetObserver = new AdapterView.AdapterDataSetObserver(this);
      this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
      if (this.mItemCount > 0) {
        i = 0;
      }
      setSelectedPositionInt(i);
      setNextSelectedPositionInt(i);
      if (this.mItemCount == 0) {
        checkSelectionChanged();
      }
    }
    else
    {
      checkFocus();
      resetList();
      checkSelectionChanged();
    }
    requestLayout();
  }
  
  public int pointToPosition(int paramInt1, int paramInt2)
  {
    Object localObject1 = this.mTouchFrame;
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      this.mTouchFrame = new Rect();
      localObject2 = this.mTouchFrame;
    }
    for (int i = getChildCount() - 1; i >= 0; i--)
    {
      localObject1 = getChildAt(i);
      if (((View)localObject1).getVisibility() == 0)
      {
        ((View)localObject1).getHitRect((Rect)localObject2);
        if (((Rect)localObject2).contains(paramInt1, paramInt2)) {
          return this.mFirstPosition + i;
        }
      }
    }
    return -1;
  }
  
  void recycleAllViews()
  {
    int i = getChildCount();
    RecycleBin localRecycleBin = this.mRecycler;
    int j = this.mFirstPosition;
    for (int k = 0; k < i; k++) {
      localRecycleBin.put(j + k, getChildAt(k));
    }
  }
  
  public void requestLayout()
  {
    if (!this.mBlockLayoutRequests) {
      super.requestLayout();
    }
  }
  
  void resetList()
  {
    this.mDataChanged = false;
    this.mNeedSync = false;
    removeAllViewsInLayout();
    this.mOldSelectedPosition = -1;
    this.mOldSelectedRowId = Long.MIN_VALUE;
    setSelectedPositionInt(-1);
    setNextSelectedPositionInt(-1);
    invalidate();
  }
  
  public void setAdapter(SpinnerAdapter paramSpinnerAdapter)
  {
    if (Android_Widget_AbsSpinner.Extension.get().getExtension() != null) {
      ((Android_Widget_AbsSpinner.Interface)Android_Widget_AbsSpinner.Extension.get().getExtension().asInterface()).setAdapter(this, paramSpinnerAdapter);
    } else {
      originalSetAdapter(paramSpinnerAdapter);
    }
  }
  
  public void setSelection(int paramInt)
  {
    setNextSelectedPositionInt(paramInt);
    requestLayout();
    invalidate();
  }
  
  public void setSelection(int paramInt, boolean paramBoolean)
  {
    boolean bool = true;
    if ((paramBoolean) && (this.mFirstPosition <= paramInt) && (paramInt <= this.mFirstPosition + getChildCount() - 1)) {
      paramBoolean = bool;
    } else {
      paramBoolean = false;
    }
    setSelectionInt(paramInt, paramBoolean);
  }
  
  void setSelectionInt(int paramInt, boolean paramBoolean)
  {
    if (paramInt != this.mOldSelectedPosition)
    {
      this.mBlockLayoutRequests = true;
      int i = this.mSelectedPosition;
      setNextSelectedPositionInt(paramInt);
      layout(paramInt - i, paramBoolean);
      this.mBlockLayoutRequests = false;
    }
  }
  
  class RecycleBin
  {
    private final SparseArray<View> mScrapHeap = new SparseArray();
    
    RecycleBin() {}
    
    void clear()
    {
      SparseArray localSparseArray = this.mScrapHeap;
      int i = localSparseArray.size();
      for (int j = 0; j < i; j++)
      {
        View localView = (View)localSparseArray.valueAt(j);
        if (localView != null) {
          AbsSpinner.this.removeDetachedView(localView, true);
        }
      }
      localSparseArray.clear();
    }
    
    View get(int paramInt)
    {
      View localView = (View)this.mScrapHeap.get(paramInt);
      if (localView != null) {
        this.mScrapHeap.delete(paramInt);
      }
      return localView;
    }
    
    public void put(int paramInt, View paramView)
    {
      this.mScrapHeap.put(paramInt, paramView);
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public AbsSpinner.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AbsSpinner.SavedState(paramAnonymousParcel);
      }
      
      public AbsSpinner.SavedState[] newArray(int paramAnonymousInt)
      {
        return new AbsSpinner.SavedState[paramAnonymousInt];
      }
    };
    int position;
    long selectedId;
    
    SavedState(Parcel paramParcel)
    {
      super();
      this.selectedId = paramParcel.readLong();
      this.position = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("AbsSpinner.SavedState{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" selectedId=");
      localStringBuilder.append(this.selectedId);
      localStringBuilder.append(" position=");
      localStringBuilder.append(this.position);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeLong(this.selectedId);
      paramParcel.writeInt(this.position);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AbsSpinner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */