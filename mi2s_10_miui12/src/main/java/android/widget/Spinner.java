package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R.styleable;
import com.android.internal.view.menu.ShowableListMenu;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_Widget_Spinner.Extension;
import com.miui.internal.variable.api.v29.Android_Widget_Spinner.Interface;

public class Spinner
  extends AbsSpinner
  implements DialogInterface.OnClickListener
{
  private static final int MAX_ITEMS_MEASURED = 15;
  public static final int MODE_DIALOG = 0;
  public static final int MODE_DROPDOWN = 1;
  private static final int MODE_THEME = -1;
  private static final String TAG = "Spinner";
  private boolean mDisableChildrenWhenDisabled;
  int mDropDownWidth;
  @UnsupportedAppUsage
  private ForwardingListener mForwardingListener;
  private int mGravity;
  @UnsupportedAppUsage
  private SpinnerPopup mPopup;
  private final Context mPopupContext;
  private SpinnerAdapter mTempAdapter;
  private final Rect mTempRect = new Rect();
  
  static
  {
    Android_Widget_Spinner.Extension.get().bindOriginal(new Android_Widget_Spinner.Interface()
    {
      public void setPrompt(Spinner paramAnonymousSpinner, CharSequence paramAnonymousCharSequence)
      {
        paramAnonymousSpinner.originalSetPrompt(paramAnonymousCharSequence);
      }
    });
  }
  
  public Spinner(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Spinner(Context paramContext, int paramInt)
  {
    this(paramContext, null, 16842881, paramInt);
  }
  
  public Spinner(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842881);
  }
  
  public Spinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0, -1);
  }
  
  public Spinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this(paramContext, paramAttributeSet, paramInt1, 0, paramInt2);
  }
  
  public Spinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramContext, paramAttributeSet, paramInt1, paramInt2, paramInt3, null);
  }
  
  public Spinner(final Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2, int paramInt3, Resources.Theme paramTheme)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Spinner, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.Spinner, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    if (paramTheme != null)
    {
      this.mPopupContext = new ContextThemeWrapper(paramContext, paramTheme);
    }
    else
    {
      int i = localTypedArray.getResourceId(7, 0);
      if (i != 0) {
        this.mPopupContext = new ContextThemeWrapper(paramContext, i);
      } else {
        this.mPopupContext = paramContext;
      }
    }
    if (paramInt3 == -1) {
      paramInt3 = localTypedArray.getInt(5, 0);
    }
    if (paramInt3 != 0)
    {
      if (paramInt3 == 1)
      {
        paramContext = new DropdownPopup(this.mPopupContext, paramAttributeSet, paramInt1, paramInt2);
        paramAttributeSet = this.mPopupContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Spinner, paramInt1, paramInt2);
        this.mDropDownWidth = paramAttributeSet.getLayoutDimension(4, -2);
        if (paramAttributeSet.hasValueOrEmpty(1)) {
          paramContext.setListSelector(paramAttributeSet.getDrawable(1));
        }
        paramContext.setBackgroundDrawable(paramAttributeSet.getDrawable(2));
        paramContext.setPromptText(localTypedArray.getString(3));
        paramAttributeSet.recycle();
        this.mPopup = paramContext;
        this.mForwardingListener = new ForwardingListener(this)
        {
          public ShowableListMenu getPopup()
          {
            return paramContext;
          }
          
          public boolean onForwardingStarted()
          {
            if (!Spinner.this.mPopup.isShowing()) {
              Spinner.this.mPopup.show(Spinner.this.getTextDirection(), Spinner.this.getTextAlignment());
            }
            return true;
          }
        };
      }
    }
    else
    {
      this.mPopup = new DialogPopup(null);
      this.mPopup.setPromptText(localTypedArray.getString(3));
    }
    this.mGravity = localTypedArray.getInt(0, 17);
    this.mDisableChildrenWhenDisabled = localTypedArray.getBoolean(8, false);
    localTypedArray.recycle();
    paramContext = this.mTempAdapter;
    if (paramContext != null)
    {
      setAdapter(paramContext);
      this.mTempAdapter = null;
    }
  }
  
  private View makeView(int paramInt, boolean paramBoolean)
  {
    if (!this.mDataChanged)
    {
      localView = this.mRecycler.get(paramInt);
      if (localView != null)
      {
        setUpChild(localView, paramBoolean);
        return localView;
      }
    }
    View localView = this.mAdapter.getView(paramInt, null, this);
    setUpChild(localView, paramBoolean);
    return localView;
  }
  
  private void setUpChild(View paramView, boolean paramBoolean)
  {
    ViewGroup.LayoutParams localLayoutParams1 = paramView.getLayoutParams();
    ViewGroup.LayoutParams localLayoutParams2 = localLayoutParams1;
    if (localLayoutParams1 == null) {
      localLayoutParams2 = generateDefaultLayoutParams();
    }
    addViewInLayout(paramView, 0, localLayoutParams2);
    paramView.setSelected(hasFocus());
    if (this.mDisableChildrenWhenDisabled) {
      paramView.setEnabled(isEnabled());
    }
    int i = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, localLayoutParams2.height);
    paramView.measure(ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, localLayoutParams2.width), i);
    int j = this.mSpinnerPadding.top + (getMeasuredHeight() - this.mSpinnerPadding.bottom - this.mSpinnerPadding.top - paramView.getMeasuredHeight()) / 2;
    i = paramView.getMeasuredHeight();
    paramView.layout(0, j, 0 + paramView.getMeasuredWidth(), i + j);
    if (!paramBoolean) {
      removeViewInLayout(paramView);
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return Spinner.class.getName();
  }
  
  public int getBaseline()
  {
    Object localObject1 = null;
    Object localObject2;
    if (getChildCount() > 0)
    {
      localObject2 = getChildAt(0);
    }
    else
    {
      localObject2 = localObject1;
      if (this.mAdapter != null)
      {
        localObject2 = localObject1;
        if (this.mAdapter.getCount() > 0)
        {
          localObject2 = makeView(0, false);
          this.mRecycler.put(0, (View)localObject2);
        }
      }
    }
    int i = -1;
    if (localObject2 != null)
    {
      int j = ((View)localObject2).getBaseline();
      if (j >= 0) {
        i = ((View)localObject2).getTop() + j;
      }
      return i;
    }
    return -1;
  }
  
  public int getDropDownHorizontalOffset()
  {
    return this.mPopup.getHorizontalOffset();
  }
  
  public int getDropDownVerticalOffset()
  {
    return this.mPopup.getVerticalOffset();
  }
  
  public int getDropDownWidth()
  {
    return this.mDropDownWidth;
  }
  
  public int getGravity()
  {
    return this.mGravity;
  }
  
  public Drawable getPopupBackground()
  {
    return this.mPopup.getBackground();
  }
  
  public Context getPopupContext()
  {
    return this.mPopupContext;
  }
  
  public CharSequence getPrompt()
  {
    return this.mPopup.getHintText();
  }
  
  public boolean isPopupShowing()
  {
    SpinnerPopup localSpinnerPopup = this.mPopup;
    boolean bool;
    if ((localSpinnerPopup != null) && (localSpinnerPopup.isShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  void layout(int paramInt, boolean paramBoolean)
  {
    int i = this.mSpinnerPadding.left;
    int j = this.mRight - this.mLeft - this.mSpinnerPadding.left - this.mSpinnerPadding.right;
    if (this.mDataChanged) {
      handleDataChanged();
    }
    if (this.mItemCount == 0)
    {
      resetList();
      return;
    }
    if (this.mNextSelectedPosition >= 0) {
      setSelectedPositionInt(this.mNextSelectedPosition);
    }
    recycleAllViews();
    removeAllViewsInLayout();
    this.mFirstPosition = this.mSelectedPosition;
    if (this.mAdapter != null)
    {
      View localView = makeView(this.mSelectedPosition, true);
      int k = localView.getMeasuredWidth();
      paramInt = i;
      int m = getLayoutDirection();
      m = Gravity.getAbsoluteGravity(this.mGravity, m) & 0x7;
      if (m != 1)
      {
        if (m == 5) {
          paramInt = i + j - k;
        }
      }
      else {
        paramInt = j / 2 + i - k / 2;
      }
      localView.offsetLeftAndRight(paramInt);
    }
    this.mRecycler.clear();
    invalidate();
    checkSelectionChanged();
    this.mDataChanged = false;
    this.mNeedSync = false;
    setNextSelectedPositionInt(this.mSelectedPosition);
  }
  
  int measureContentWidth(SpinnerAdapter paramSpinnerAdapter, Drawable paramDrawable)
  {
    if (paramSpinnerAdapter == null) {
      return 0;
    }
    int i = 0;
    View localView = null;
    int j = 0;
    int k = View.MeasureSpec.makeSafeMeasureSpec(getMeasuredWidth(), 0);
    int m = View.MeasureSpec.makeSafeMeasureSpec(getMeasuredHeight(), 0);
    int n = Math.max(0, getSelectedItemPosition());
    int i1 = Math.min(paramSpinnerAdapter.getCount(), n + 15);
    n = Math.max(0, n - (15 - (i1 - n)));
    while (n < i1)
    {
      int i2 = paramSpinnerAdapter.getItemViewType(n);
      int i3 = j;
      if (i2 != j)
      {
        i3 = i2;
        localView = null;
      }
      localView = paramSpinnerAdapter.getView(n, localView, this);
      if (localView.getLayoutParams() == null) {
        localView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
      }
      localView.measure(k, m);
      i = Math.max(i, localView.getMeasuredWidth());
      n++;
      j = i3;
    }
    n = i;
    if (paramDrawable != null)
    {
      paramDrawable.getPadding(this.mTempRect);
      n = i + (this.mTempRect.left + this.mTempRect.right);
    }
    return n;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    setSelection(paramInt);
    paramDialogInterface.dismiss();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    SpinnerPopup localSpinnerPopup = this.mPopup;
    if ((localSpinnerPopup != null) && (localSpinnerPopup.isShowing())) {
      this.mPopup.dismiss();
    }
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (this.mAdapter != null) {
      paramAccessibilityNodeInfo.setCanOpenPopup(true);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mInLayout = true;
    layout(0, false);
    this.mInLayout = false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ((this.mPopup != null) && (View.MeasureSpec.getMode(paramInt1) == Integer.MIN_VALUE))
    {
      paramInt2 = getMeasuredWidth();
      setMeasuredDimension(Math.min(Math.max(paramInt2, measureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(paramInt1)), getMeasuredHeight());
    }
  }
  
  public PointerIcon onResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt)
  {
    if ((getPointerIcon() == null) && (isClickable()) && (isEnabled())) {
      return PointerIcon.getSystemIcon(getContext(), 1002);
    }
    return super.onResolvePointerIcon(paramMotionEvent, paramInt);
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (paramParcelable.showDropdown)
    {
      paramParcelable = getViewTreeObserver();
      if (paramParcelable != null) {
        paramParcelable.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
          public void onGlobalLayout()
          {
            if (!Spinner.this.mPopup.isShowing()) {
              Spinner.this.mPopup.show(Spinner.this.getTextDirection(), Spinner.this.getTextAlignment());
            }
            ViewTreeObserver localViewTreeObserver = Spinner.this.getViewTreeObserver();
            if (localViewTreeObserver != null) {
              localViewTreeObserver.removeOnGlobalLayoutListener(this);
            }
          }
        });
      }
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    SpinnerPopup localSpinnerPopup = this.mPopup;
    boolean bool;
    if ((localSpinnerPopup != null) && (localSpinnerPopup.isShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    localSavedState.showDropdown = bool;
    return localSavedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    ForwardingListener localForwardingListener = this.mForwardingListener;
    if ((localForwardingListener != null) && (localForwardingListener.onTouch(this, paramMotionEvent))) {
      return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  void originalSetPrompt(CharSequence paramCharSequence)
  {
    this.mPopup.setPromptText(paramCharSequence);
  }
  
  public boolean performClick()
  {
    boolean bool1 = super.performClick();
    boolean bool2 = bool1;
    if (!bool1)
    {
      bool1 = true;
      bool2 = bool1;
      if (!this.mPopup.isShowing())
      {
        this.mPopup.show(getTextDirection(), getTextAlignment());
        bool2 = bool1;
      }
    }
    return bool2;
  }
  
  public void setAdapter(SpinnerAdapter paramSpinnerAdapter)
  {
    if (this.mPopup == null)
    {
      this.mTempAdapter = paramSpinnerAdapter;
      return;
    }
    super.setAdapter(paramSpinnerAdapter);
    this.mRecycler.clear();
    if ((this.mContext.getApplicationInfo().targetSdkVersion >= 21) && (paramSpinnerAdapter != null) && (paramSpinnerAdapter.getViewTypeCount() != 1)) {
      throw new IllegalArgumentException("Spinner adapter view type count must be 1");
    }
    Context localContext1 = this.mPopupContext;
    Context localContext2 = localContext1;
    if (localContext1 == null) {
      localContext2 = this.mContext;
    }
    this.mPopup.setAdapter(new DropDownAdapter(paramSpinnerAdapter, localContext2.getTheme()));
  }
  
  public void setDropDownHorizontalOffset(int paramInt)
  {
    this.mPopup.setHorizontalOffset(paramInt);
  }
  
  public void setDropDownVerticalOffset(int paramInt)
  {
    this.mPopup.setVerticalOffset(paramInt);
  }
  
  public void setDropDownWidth(int paramInt)
  {
    if (!(this.mPopup instanceof DropdownPopup))
    {
      Log.e("Spinner", "Cannot set dropdown width for MODE_DIALOG, ignoring");
      return;
    }
    this.mDropDownWidth = paramInt;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if (this.mDisableChildrenWhenDisabled)
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++) {
        getChildAt(j).setEnabled(paramBoolean);
      }
    }
  }
  
  public void setGravity(int paramInt)
  {
    if (this.mGravity != paramInt)
    {
      int i = paramInt;
      if ((paramInt & 0x7) == 0) {
        i = paramInt | 0x800003;
      }
      this.mGravity = i;
      requestLayout();
    }
  }
  
  public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    throw new RuntimeException("setOnItemClickListener cannot be used with a spinner.");
  }
  
  @UnsupportedAppUsage
  public void setOnItemClickListenerInt(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    super.setOnItemClickListener(paramOnItemClickListener);
  }
  
  public void setPopupBackgroundDrawable(Drawable paramDrawable)
  {
    SpinnerPopup localSpinnerPopup = this.mPopup;
    if (!(localSpinnerPopup instanceof DropdownPopup))
    {
      Log.e("Spinner", "setPopupBackgroundDrawable: incompatible spinner mode; ignoring...");
      return;
    }
    localSpinnerPopup.setBackgroundDrawable(paramDrawable);
  }
  
  public void setPopupBackgroundResource(int paramInt)
  {
    setPopupBackgroundDrawable(getPopupContext().getDrawable(paramInt));
  }
  
  public void setPrompt(CharSequence paramCharSequence)
  {
    if (Android_Widget_Spinner.Extension.get().getExtension() != null) {
      ((Android_Widget_Spinner.Interface)Android_Widget_Spinner.Extension.get().getExtension().asInterface()).setPrompt(this, paramCharSequence);
    } else {
      originalSetPrompt(paramCharSequence);
    }
  }
  
  public void setPromptId(int paramInt)
  {
    setPrompt(getContext().getText(paramInt));
  }
  
  private class DialogPopup
    implements Spinner.SpinnerPopup, DialogInterface.OnClickListener
  {
    private ListAdapter mListAdapter;
    private AlertDialog mPopup;
    private CharSequence mPrompt;
    
    private DialogPopup() {}
    
    public void dismiss()
    {
      AlertDialog localAlertDialog = this.mPopup;
      if (localAlertDialog != null)
      {
        localAlertDialog.dismiss();
        this.mPopup = null;
      }
    }
    
    public Drawable getBackground()
    {
      return null;
    }
    
    public CharSequence getHintText()
    {
      return this.mPrompt;
    }
    
    public int getHorizontalOffset()
    {
      return 0;
    }
    
    public int getVerticalOffset()
    {
      return 0;
    }
    
    @UnsupportedAppUsage
    public boolean isShowing()
    {
      AlertDialog localAlertDialog = this.mPopup;
      boolean bool;
      if (localAlertDialog != null) {
        bool = localAlertDialog.isShowing();
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      Spinner.this.setSelection(paramInt);
      if (Spinner.this.mOnItemClickListener != null) {
        Spinner.this.performItemClick(null, paramInt, this.mListAdapter.getItemId(paramInt));
      }
      dismiss();
    }
    
    public void setAdapter(ListAdapter paramListAdapter)
    {
      this.mListAdapter = paramListAdapter;
    }
    
    public void setBackgroundDrawable(Drawable paramDrawable)
    {
      Log.e("Spinner", "Cannot set popup background for MODE_DIALOG, ignoring");
    }
    
    public void setHorizontalOffset(int paramInt)
    {
      Log.e("Spinner", "Cannot set horizontal offset for MODE_DIALOG, ignoring");
    }
    
    public void setPromptText(CharSequence paramCharSequence)
    {
      this.mPrompt = paramCharSequence;
    }
    
    public void setVerticalOffset(int paramInt)
    {
      Log.e("Spinner", "Cannot set vertical offset for MODE_DIALOG, ignoring");
    }
    
    public void show(int paramInt1, int paramInt2)
    {
      if (this.mListAdapter == null) {
        return;
      }
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(Spinner.this.getPopupContext());
      Object localObject = this.mPrompt;
      if (localObject != null) {
        localBuilder.setTitle((CharSequence)localObject);
      }
      this.mPopup = localBuilder.setSingleChoiceItems(this.mListAdapter, Spinner.this.getSelectedItemPosition(), this).create();
      localObject = this.mPopup.getListView();
      ((ListView)localObject).setTextDirection(paramInt1);
      ((ListView)localObject).setTextAlignment(paramInt2);
      this.mPopup.show();
    }
  }
  
  private static class DropDownAdapter
    implements ListAdapter, SpinnerAdapter
  {
    private SpinnerAdapter mAdapter;
    private ListAdapter mListAdapter;
    
    public DropDownAdapter(SpinnerAdapter paramSpinnerAdapter, Resources.Theme paramTheme)
    {
      this.mAdapter = paramSpinnerAdapter;
      if ((paramSpinnerAdapter instanceof ListAdapter)) {
        this.mListAdapter = ((ListAdapter)paramSpinnerAdapter);
      }
      if ((paramTheme != null) && ((paramSpinnerAdapter instanceof ThemedSpinnerAdapter)))
      {
        paramSpinnerAdapter = (ThemedSpinnerAdapter)paramSpinnerAdapter;
        if (paramSpinnerAdapter.getDropDownViewTheme() == null) {
          paramSpinnerAdapter.setDropDownViewTheme(paramTheme);
        }
      }
    }
    
    public boolean areAllItemsEnabled()
    {
      ListAdapter localListAdapter = this.mListAdapter;
      if (localListAdapter != null) {
        return localListAdapter.areAllItemsEnabled();
      }
      return true;
    }
    
    public int getCount()
    {
      SpinnerAdapter localSpinnerAdapter = this.mAdapter;
      int i;
      if (localSpinnerAdapter == null) {
        i = 0;
      } else {
        i = localSpinnerAdapter.getCount();
      }
      return i;
    }
    
    public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      SpinnerAdapter localSpinnerAdapter = this.mAdapter;
      if (localSpinnerAdapter == null) {
        paramView = null;
      } else {
        paramView = localSpinnerAdapter.getDropDownView(paramInt, paramView, paramViewGroup);
      }
      return paramView;
    }
    
    public Object getItem(int paramInt)
    {
      Object localObject = this.mAdapter;
      if (localObject == null) {
        localObject = null;
      } else {
        localObject = ((SpinnerAdapter)localObject).getItem(paramInt);
      }
      return localObject;
    }
    
    public long getItemId(int paramInt)
    {
      SpinnerAdapter localSpinnerAdapter = this.mAdapter;
      long l;
      if (localSpinnerAdapter == null) {
        l = -1L;
      } else {
        l = localSpinnerAdapter.getItemId(paramInt);
      }
      return l;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return getDropDownView(paramInt, paramView, paramViewGroup);
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      SpinnerAdapter localSpinnerAdapter = this.mAdapter;
      boolean bool;
      if ((localSpinnerAdapter != null) && (localSpinnerAdapter.hasStableIds())) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean isEmpty()
    {
      boolean bool;
      if (getCount() == 0) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean isEnabled(int paramInt)
    {
      ListAdapter localListAdapter = this.mListAdapter;
      if (localListAdapter != null) {
        return localListAdapter.isEnabled(paramInt);
      }
      return true;
    }
    
    public void registerDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      SpinnerAdapter localSpinnerAdapter = this.mAdapter;
      if (localSpinnerAdapter != null) {
        localSpinnerAdapter.registerDataSetObserver(paramDataSetObserver);
      }
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      SpinnerAdapter localSpinnerAdapter = this.mAdapter;
      if (localSpinnerAdapter != null) {
        localSpinnerAdapter.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
  
  private class DropdownPopup
    extends ListPopupWindow
    implements Spinner.SpinnerPopup
  {
    private ListAdapter mAdapter;
    private CharSequence mHintText;
    
    public DropdownPopup(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      super(paramAttributeSet, paramInt1, paramInt2);
      setAnchorView(Spinner.this);
      setModal(true);
      setPromptPosition(0);
      setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          Spinner.this.setSelection(paramAnonymousInt);
          if (Spinner.this.mOnItemClickListener != null) {
            Spinner.this.performItemClick(paramAnonymousView, paramAnonymousInt, Spinner.DropdownPopup.this.mAdapter.getItemId(paramAnonymousInt));
          }
          Spinner.DropdownPopup.this.dismiss();
        }
      });
    }
    
    void computeContentWidth()
    {
      Object localObject = getBackground();
      int i = 0;
      if (localObject != null)
      {
        ((Drawable)localObject).getPadding(Spinner.this.mTempRect);
        if (Spinner.this.isLayoutRtl()) {
          i = Spinner.this.mTempRect.right;
        } else {
          i = -Spinner.this.mTempRect.left;
        }
      }
      else
      {
        localObject = Spinner.this.mTempRect;
        Spinner.this.mTempRect.right = 0;
        ((Rect)localObject).left = 0;
      }
      int j = Spinner.this.getPaddingLeft();
      int k = Spinner.this.getPaddingRight();
      int m = Spinner.this.getWidth();
      if (Spinner.this.mDropDownWidth == -2)
      {
        int n = Spinner.this.measureContentWidth((SpinnerAdapter)this.mAdapter, getBackground());
        int i1 = Spinner.this.mContext.getResources().getDisplayMetrics().widthPixels - Spinner.this.mTempRect.left - Spinner.this.mTempRect.right;
        int i2 = n;
        if (n > i1) {
          i2 = i1;
        }
        setContentWidth(Math.max(i2, m - j - k));
      }
      else if (Spinner.this.mDropDownWidth == -1)
      {
        setContentWidth(m - j - k);
      }
      else
      {
        setContentWidth(Spinner.this.mDropDownWidth);
      }
      if (Spinner.this.isLayoutRtl()) {
        i += m - k - getWidth();
      } else {
        i += j;
      }
      setHorizontalOffset(i);
    }
    
    public CharSequence getHintText()
    {
      return this.mHintText;
    }
    
    public void setAdapter(ListAdapter paramListAdapter)
    {
      super.setAdapter(paramListAdapter);
      this.mAdapter = paramListAdapter;
    }
    
    public void setPromptText(CharSequence paramCharSequence)
    {
      this.mHintText = paramCharSequence;
    }
    
    public void show(int paramInt1, int paramInt2)
    {
      boolean bool = isShowing();
      computeContentWidth();
      setInputMethodMode(2);
      super.show();
      Object localObject = getListView();
      ((ListView)localObject).setChoiceMode(1);
      ((ListView)localObject).setTextDirection(paramInt1);
      ((ListView)localObject).setTextAlignment(paramInt2);
      setSelection(Spinner.this.getSelectedItemPosition());
      if (bool) {
        return;
      }
      localObject = Spinner.this.getViewTreeObserver();
      if (localObject != null)
      {
        final ViewTreeObserver.OnGlobalLayoutListener local2 = new ViewTreeObserver.OnGlobalLayoutListener()
        {
          public void onGlobalLayout()
          {
            if (!Spinner.this.isVisibleToUser())
            {
              Spinner.DropdownPopup.this.dismiss();
            }
            else
            {
              Spinner.DropdownPopup.this.computeContentWidth();
              Spinner.DropdownPopup.this.show();
            }
          }
        };
        ((ViewTreeObserver)localObject).addOnGlobalLayoutListener(local2);
        setOnDismissListener(new PopupWindow.OnDismissListener()
        {
          public void onDismiss()
          {
            ViewTreeObserver localViewTreeObserver = Spinner.this.getViewTreeObserver();
            if (localViewTreeObserver != null) {
              localViewTreeObserver.removeOnGlobalLayoutListener(local2);
            }
          }
        });
      }
    }
  }
  
  static class SavedState
    extends AbsSpinner.SavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public Spinner.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Spinner.SavedState(paramAnonymousParcel, null);
      }
      
      public Spinner.SavedState[] newArray(int paramAnonymousInt)
      {
        return new Spinner.SavedState[paramAnonymousInt];
      }
    };
    boolean showDropdown;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      boolean bool;
      if (paramParcel.readByte() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      this.showDropdown = bool;
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeByte((byte)this.showDropdown);
    }
  }
  
  private static abstract interface SpinnerPopup
  {
    public abstract void dismiss();
    
    public abstract Drawable getBackground();
    
    public abstract CharSequence getHintText();
    
    public abstract int getHorizontalOffset();
    
    public abstract int getVerticalOffset();
    
    @UnsupportedAppUsage
    public abstract boolean isShowing();
    
    public abstract void setAdapter(ListAdapter paramListAdapter);
    
    public abstract void setBackgroundDrawable(Drawable paramDrawable);
    
    public abstract void setHorizontalOffset(int paramInt);
    
    public abstract void setPromptText(CharSequence paramCharSequence);
    
    public abstract void setVerticalOffset(int paramInt);
    
    public abstract void show(int paramInt1, int paramInt2);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Spinner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */