package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.android.internal.R.styleable;
import com.android.internal.view.menu.ShowableListMenu;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_Widget_ListPopupWindow.Extension;
import com.miui.internal.variable.api.v29.Android_Widget_ListPopupWindow.Interface;

public class ListPopupWindow
  implements ShowableListMenu
{
  private static final boolean DEBUG = false;
  private static final int EXPAND_LIST_TIMEOUT = 250;
  public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
  public static final int INPUT_METHOD_NEEDED = 1;
  public static final int INPUT_METHOD_NOT_NEEDED = 2;
  public static final int MATCH_PARENT = -1;
  public static final int POSITION_PROMPT_ABOVE = 0;
  public static final int POSITION_PROMPT_BELOW = 1;
  private static final String TAG = "ListPopupWindow";
  public static final int WRAP_CONTENT = -2;
  private ListAdapter mAdapter;
  private Context mContext;
  private boolean mDropDownAlwaysVisible = false;
  private View mDropDownAnchorView;
  private int mDropDownGravity = 0;
  private int mDropDownHeight = -2;
  private int mDropDownHorizontalOffset;
  @UnsupportedAppUsage
  private DropDownListView mDropDownList;
  private Drawable mDropDownListHighlight;
  private int mDropDownVerticalOffset;
  private boolean mDropDownVerticalOffsetSet;
  private int mDropDownWidth = -2;
  private int mDropDownWindowLayoutType = 1002;
  private Rect mEpicenterBounds;
  private boolean mForceIgnoreOutsideTouch = false;
  private final Handler mHandler;
  private final ListSelectorHider mHideSelector = new ListSelectorHider(null);
  private boolean mIsAnimatedFromAnchor = true;
  private AdapterView.OnItemClickListener mItemClickListener;
  private AdapterView.OnItemSelectedListener mItemSelectedListener;
  int mListItemExpandMaximum = Integer.MAX_VALUE;
  private boolean mModal;
  private DataSetObserver mObserver;
  private boolean mOverlapAnchor;
  private boolean mOverlapAnchorSet;
  @UnsupportedAppUsage
  PopupWindow mPopup;
  private int mPromptPosition = 0;
  private View mPromptView;
  private final ResizePopupRunnable mResizePopupRunnable = new ResizePopupRunnable(null);
  private final PopupScrollListener mScrollListener = new PopupScrollListener(null);
  private Runnable mShowDropDownRunnable;
  private final Rect mTempRect = new Rect();
  private final PopupTouchInterceptor mTouchInterceptor = new PopupTouchInterceptor(null);
  
  static
  {
    Android_Widget_ListPopupWindow.Extension.get().bindOriginal(new Impl(null));
  }
  
  public ListPopupWindow(Context paramContext)
  {
    this(paramContext, null, 16843519, 0);
  }
  
  public ListPopupWindow(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843519, 0);
  }
  
  public ListPopupWindow(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ListPopupWindow(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler(paramContext.getMainLooper());
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListPopupWindow, paramInt1, paramInt2);
    this.mDropDownHorizontalOffset = localTypedArray.getDimensionPixelOffset(0, 0);
    this.mDropDownVerticalOffset = localTypedArray.getDimensionPixelOffset(1, 0);
    if (this.mDropDownVerticalOffset != 0) {
      this.mDropDownVerticalOffsetSet = true;
    }
    localTypedArray.recycle();
    this.mPopup = new PopupWindow(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mPopup.setInputMethodMode(1);
    if (Android_Widget_ListPopupWindow.Extension.get().getExtension() != null) {
      ((Android_Widget_ListPopupWindow.Interface)Android_Widget_ListPopupWindow.Extension.get().getExtension().asInterface()).init(this, paramContext, paramAttributeSet, paramInt1, paramInt2);
    }
  }
  
  @UnsupportedAppUsage
  private int buildDropDown()
  {
    int i = 0;
    int j = 0;
    Object localObject1 = this.mDropDownList;
    boolean bool = false;
    Object localObject3;
    int k;
    if (localObject1 == null)
    {
      Object localObject2 = this.mContext;
      this.mShowDropDownRunnable = new Runnable()
      {
        public void run()
        {
          View localView = ListPopupWindow.this.getAnchorView();
          if ((localView != null) && (localView.getWindowToken() != null)) {
            ListPopupWindow.this.show();
          }
        }
      };
      this.mDropDownList = createDropDownListView((Context)localObject2, this.mModal ^ true);
      localObject1 = this.mDropDownListHighlight;
      if (localObject1 != null) {
        this.mDropDownList.setSelector((Drawable)localObject1);
      }
      this.mDropDownList.setAdapter(this.mAdapter);
      this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
      this.mDropDownList.setFocusable(true);
      this.mDropDownList.setFocusableInTouchMode(true);
      this.mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
        public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if (paramAnonymousInt != -1)
          {
            paramAnonymousAdapterView = ListPopupWindow.this.mDropDownList;
            if (paramAnonymousAdapterView != null) {
              paramAnonymousAdapterView.setListSelectionHidden(false);
            }
          }
        }
        
        public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
      });
      this.mDropDownList.setOnScrollListener(this.mScrollListener);
      localObject1 = this.mItemSelectedListener;
      if (localObject1 != null) {
        this.mDropDownList.setOnItemSelectedListener((AdapterView.OnItemSelectedListener)localObject1);
      }
      localObject3 = this.mDropDownList;
      View localView = this.mPromptView;
      localObject1 = localObject3;
      if (localView != null)
      {
        localObject1 = new LinearLayout((Context)localObject2);
        ((LinearLayout)localObject1).setOrientation(1);
        localObject2 = new LinearLayout.LayoutParams(-1, 0, 1.0F);
        j = this.mPromptPosition;
        if (j != 0)
        {
          if (j != 1)
          {
            localObject3 = new StringBuilder();
            ((StringBuilder)localObject3).append("Invalid hint position ");
            ((StringBuilder)localObject3).append(this.mPromptPosition);
            Log.e("ListPopupWindow", ((StringBuilder)localObject3).toString());
          }
          else
          {
            ((LinearLayout)localObject1).addView((View)localObject3, (ViewGroup.LayoutParams)localObject2);
            ((LinearLayout)localObject1).addView(localView);
          }
        }
        else
        {
          ((LinearLayout)localObject1).addView(localView);
          ((LinearLayout)localObject1).addView((View)localObject3, (ViewGroup.LayoutParams)localObject2);
        }
        if (this.mDropDownWidth >= 0)
        {
          i = Integer.MIN_VALUE;
          j = this.mDropDownWidth;
        }
        else
        {
          i = 0;
          j = 0;
        }
        localView.measure(View.MeasureSpec.makeMeasureSpec(j, i), 0);
        localObject3 = (LinearLayout.LayoutParams)localView.getLayoutParams();
        k = localView.getMeasuredHeight();
        i = ((LinearLayout.LayoutParams)localObject3).topMargin;
        j = ((LinearLayout.LayoutParams)localObject3).bottomMargin;
        j = k + i + j;
      }
      this.mPopup.setContentView((View)localObject1);
    }
    else
    {
      localObject3 = this.mPromptView;
      j = i;
      if (localObject3 != null)
      {
        localObject1 = (LinearLayout.LayoutParams)((View)localObject3).getLayoutParams();
        j = ((View)localObject3).getMeasuredHeight() + ((LinearLayout.LayoutParams)localObject1).topMargin + ((LinearLayout.LayoutParams)localObject1).bottomMargin;
      }
    }
    localObject1 = this.mPopup.getBackground();
    if (localObject1 != null)
    {
      ((Drawable)localObject1).getPadding(this.mTempRect);
      i = this.mTempRect.top + this.mTempRect.bottom;
      k = i;
      if (!this.mDropDownVerticalOffsetSet)
      {
        this.mDropDownVerticalOffset = (-this.mTempRect.top);
        k = i;
      }
    }
    else
    {
      this.mTempRect.setEmpty();
      k = 0;
    }
    if (this.mPopup.getInputMethodMode() == 2) {
      bool = true;
    }
    int m = this.mPopup.getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset, bool);
    if ((!this.mDropDownAlwaysVisible) && (this.mDropDownHeight != -1))
    {
      i = this.mDropDownWidth;
      if (i != -2)
      {
        if (i != -1) {
          i = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        } else {
          i = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
        }
      }
      else {
        i = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
      }
      m = this.mDropDownList.measureHeightOfChildren(i, 0, -1, m - j, -1);
      i = j;
      if (m > 0) {
        i = j + (k + (this.mDropDownList.getPaddingTop() + this.mDropDownList.getPaddingBottom()));
      }
      return m + i;
    }
    return m + k;
  }
  
  private void removePromptView()
  {
    Object localObject = this.mPromptView;
    if (localObject != null)
    {
      localObject = ((View)localObject).getParent();
      if ((localObject instanceof ViewGroup)) {
        ((ViewGroup)localObject).removeView(this.mPromptView);
      }
    }
  }
  
  public void clearListSelection()
  {
    DropDownListView localDropDownListView = this.mDropDownList;
    if (localDropDownListView != null)
    {
      localDropDownListView.setListSelectionHidden(true);
      localDropDownListView.hideSelector();
      localDropDownListView.requestLayout();
    }
  }
  
  public View.OnTouchListener createDragToOpenListener(View paramView)
  {
    new ForwardingListener(paramView)
    {
      public ShowableListMenu getPopup()
      {
        return ListPopupWindow.this;
      }
    };
  }
  
  DropDownListView createDropDownListView(Context paramContext, boolean paramBoolean)
  {
    return new DropDownListView(paramContext, paramBoolean);
  }
  
  public void dismiss()
  {
    this.mPopup.dismiss();
    removePromptView();
    this.mPopup.setContentView(null);
    this.mDropDownList = null;
    this.mHandler.removeCallbacks(this.mResizePopupRunnable);
  }
  
  public View getAnchorView()
  {
    return this.mDropDownAnchorView;
  }
  
  public int getAnimationStyle()
  {
    return this.mPopup.getAnimationStyle();
  }
  
  public Drawable getBackground()
  {
    return this.mPopup.getBackground();
  }
  
  public Rect getEpicenterBounds()
  {
    Rect localRect = this.mEpicenterBounds;
    if (localRect != null) {
      localRect = new Rect(localRect);
    } else {
      localRect = null;
    }
    return localRect;
  }
  
  public int getHeight()
  {
    return this.mDropDownHeight;
  }
  
  public int getHorizontalOffset()
  {
    return this.mDropDownHorizontalOffset;
  }
  
  public int getInputMethodMode()
  {
    return this.mPopup.getInputMethodMode();
  }
  
  public ListView getListView()
  {
    return this.mDropDownList;
  }
  
  public int getPromptPosition()
  {
    return this.mPromptPosition;
  }
  
  public Object getSelectedItem()
  {
    if (!isShowing()) {
      return null;
    }
    return this.mDropDownList.getSelectedItem();
  }
  
  public long getSelectedItemId()
  {
    if (!isShowing()) {
      return Long.MIN_VALUE;
    }
    return this.mDropDownList.getSelectedItemId();
  }
  
  public int getSelectedItemPosition()
  {
    if (!isShowing()) {
      return -1;
    }
    return this.mDropDownList.getSelectedItemPosition();
  }
  
  public View getSelectedView()
  {
    if (!isShowing()) {
      return null;
    }
    return this.mDropDownList.getSelectedView();
  }
  
  public int getSoftInputMode()
  {
    return this.mPopup.getSoftInputMode();
  }
  
  public int getVerticalOffset()
  {
    if (!this.mDropDownVerticalOffsetSet) {
      return 0;
    }
    return this.mDropDownVerticalOffset;
  }
  
  public int getWidth()
  {
    return this.mDropDownWidth;
  }
  
  @UnsupportedAppUsage
  public boolean isDropDownAlwaysVisible()
  {
    return this.mDropDownAlwaysVisible;
  }
  
  public boolean isInputMethodNotNeeded()
  {
    boolean bool;
    if (this.mPopup.getInputMethodMode() == 2) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isModal()
  {
    return this.mModal;
  }
  
  public boolean isShowing()
  {
    return this.mPopup.isShowing();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((isShowing()) && (paramInt != 62) && ((this.mDropDownList.getSelectedItemPosition() >= 0) || (!KeyEvent.isConfirmKey(paramInt))))
    {
      int i = this.mDropDownList.getSelectedItemPosition();
      boolean bool1 = this.mPopup.isAboveAnchor() ^ true;
      ListAdapter localListAdapter = this.mAdapter;
      int j = Integer.MAX_VALUE;
      int k = Integer.MIN_VALUE;
      if (localListAdapter != null)
      {
        boolean bool2 = localListAdapter.areAllItemsEnabled();
        if (bool2) {
          k = 0;
        } else {
          k = this.mDropDownList.lookForSelectablePosition(0, true);
        }
        j = k;
        if (bool2) {
          k = localListAdapter.getCount() - 1;
        } else {
          k = this.mDropDownList.lookForSelectablePosition(localListAdapter.getCount() - 1, false);
        }
      }
      if (((bool1) && (paramInt == 19) && (i <= j)) || ((!bool1) && (paramInt == 20) && (i >= k)))
      {
        clearListSelection();
        this.mPopup.setInputMethodMode(1);
        show();
        return true;
      }
      this.mDropDownList.setListSelectionHidden(false);
      if (this.mDropDownList.onKeyDown(paramInt, paramKeyEvent))
      {
        this.mPopup.setInputMethodMode(2);
        this.mDropDownList.requestFocusFromTouch();
        show();
        if ((paramInt == 19) || (paramInt == 20) || (paramInt == 23) || (paramInt == 66)) {
          return true;
        }
      }
      else if ((bool1) && (paramInt == 20))
      {
        if (i == k) {
          return true;
        }
      }
      else if ((!bool1) && (paramInt == 19) && (i == j))
      {
        return true;
      }
    }
    return false;
  }
  
  public boolean onKeyPreIme(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 4) && (isShowing()))
    {
      Object localObject = this.mDropDownAnchorView;
      if ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getRepeatCount() == 0))
      {
        localObject = ((View)localObject).getKeyDispatcherState();
        if (localObject != null) {
          ((KeyEvent.DispatcherState)localObject).startTracking(paramKeyEvent, this);
        }
        return true;
      }
      if (paramKeyEvent.getAction() == 1)
      {
        localObject = ((View)localObject).getKeyDispatcherState();
        if (localObject != null) {
          ((KeyEvent.DispatcherState)localObject).handleUpEvent(paramKeyEvent);
        }
        if ((paramKeyEvent.isTracking()) && (!paramKeyEvent.isCanceled()))
        {
          dismiss();
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((isShowing()) && (this.mDropDownList.getSelectedItemPosition() >= 0))
    {
      boolean bool = this.mDropDownList.onKeyUp(paramInt, paramKeyEvent);
      if ((bool) && (KeyEvent.isConfirmKey(paramInt))) {
        dismiss();
      }
      return bool;
    }
    return false;
  }
  
  void originalShow()
  {
    int i = buildDropDown();
    boolean bool1 = isInputMethodNotNeeded();
    this.mPopup.setAllowScrollingAnchorParent(bool1 ^ true);
    this.mPopup.setWindowLayoutType(this.mDropDownWindowLayoutType);
    boolean bool2 = this.mPopup.isShowing();
    boolean bool3 = true;
    boolean bool4 = true;
    int j;
    int k;
    PopupWindow localPopupWindow;
    if (bool2)
    {
      if (!getAnchorView().isAttachedToWindow()) {
        return;
      }
      j = this.mDropDownWidth;
      if (j == -1) {
        j = -1;
      } else if (j == -2) {
        j = getAnchorView().getWidth();
      } else {
        j = this.mDropDownWidth;
      }
      k = this.mDropDownHeight;
      if (k == -1)
      {
        if (!bool1) {
          i = -1;
        }
        if (bool1)
        {
          localPopupWindow = this.mPopup;
          if (this.mDropDownWidth == -1) {
            k = -1;
          } else {
            k = 0;
          }
          localPopupWindow.setWidth(k);
          this.mPopup.setHeight(0);
        }
        else
        {
          localPopupWindow = this.mPopup;
          if (this.mDropDownWidth == -1) {
            k = -1;
          } else {
            k = 0;
          }
          localPopupWindow.setWidth(k);
          this.mPopup.setHeight(-1);
        }
      }
      else if (k != -2)
      {
        i = this.mDropDownHeight;
      }
      localPopupWindow = this.mPopup;
      if ((this.mForceIgnoreOutsideTouch) || (this.mDropDownAlwaysVisible)) {
        bool4 = false;
      }
      localPopupWindow.setOutsideTouchable(bool4);
      localPopupWindow = this.mPopup;
      View localView = getAnchorView();
      k = this.mDropDownHorizontalOffset;
      int m = this.mDropDownVerticalOffset;
      if (j < 0) {
        j = -1;
      }
      if (i < 0) {
        i = -1;
      }
      localPopupWindow.update(localView, k, m, j, i);
      this.mPopup.getContentView().restoreDefaultFocus();
    }
    else
    {
      j = this.mDropDownWidth;
      if (j == -1) {
        j = -1;
      } else if (j == -2) {
        j = getAnchorView().getWidth();
      } else {
        j = this.mDropDownWidth;
      }
      k = this.mDropDownHeight;
      if (k == -1) {
        i = -1;
      } else if (k != -2) {
        i = this.mDropDownHeight;
      }
      this.mPopup.setWidth(j);
      this.mPopup.setHeight(i);
      this.mPopup.setIsClippedToScreen(true);
      localPopupWindow = this.mPopup;
      if ((!this.mForceIgnoreOutsideTouch) && (!this.mDropDownAlwaysVisible)) {
        bool4 = bool3;
      } else {
        bool4 = false;
      }
      localPopupWindow.setOutsideTouchable(bool4);
      this.mPopup.setTouchInterceptor(this.mTouchInterceptor);
      this.mPopup.setEpicenterBounds(this.mEpicenterBounds);
      if (this.mOverlapAnchorSet) {
        this.mPopup.setOverlapAnchor(this.mOverlapAnchor);
      }
      this.mPopup.showAsDropDown(getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
      this.mDropDownList.setSelection(-1);
      this.mPopup.getContentView().restoreDefaultFocus();
      if ((!this.mModal) || (this.mDropDownList.isInTouchMode())) {
        clearListSelection();
      }
      if (!this.mModal) {
        this.mHandler.post(this.mHideSelector);
      }
    }
  }
  
  public boolean performItemClick(int paramInt)
  {
    if (isShowing())
    {
      if (this.mItemClickListener != null)
      {
        DropDownListView localDropDownListView = this.mDropDownList;
        View localView = localDropDownListView.getChildAt(paramInt - localDropDownListView.getFirstVisiblePosition());
        ListAdapter localListAdapter = localDropDownListView.getAdapter();
        this.mItemClickListener.onItemClick(localDropDownListView, localView, paramInt, localListAdapter.getItemId(paramInt));
      }
      return true;
    }
    return false;
  }
  
  public void postShow()
  {
    this.mHandler.post(this.mShowDropDownRunnable);
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    DataSetObserver localDataSetObserver = this.mObserver;
    if (localDataSetObserver == null)
    {
      this.mObserver = new PopupDataSetObserver(null);
    }
    else
    {
      ListAdapter localListAdapter = this.mAdapter;
      if (localListAdapter != null) {
        localListAdapter.unregisterDataSetObserver(localDataSetObserver);
      }
    }
    this.mAdapter = paramListAdapter;
    if (this.mAdapter != null) {
      paramListAdapter.registerDataSetObserver(this.mObserver);
    }
    paramListAdapter = this.mDropDownList;
    if (paramListAdapter != null) {
      paramListAdapter.setAdapter(this.mAdapter);
    }
  }
  
  public void setAnchorView(View paramView)
  {
    this.mDropDownAnchorView = paramView;
  }
  
  public void setAnimationStyle(int paramInt)
  {
    this.mPopup.setAnimationStyle(paramInt);
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    this.mPopup.setBackgroundDrawable(paramDrawable);
  }
  
  public void setContentWidth(int paramInt)
  {
    Drawable localDrawable = this.mPopup.getBackground();
    if (localDrawable != null)
    {
      localDrawable.getPadding(this.mTempRect);
      this.mDropDownWidth = (this.mTempRect.left + this.mTempRect.right + paramInt);
    }
    else
    {
      setWidth(paramInt);
    }
  }
  
  @UnsupportedAppUsage
  public void setDropDownAlwaysVisible(boolean paramBoolean)
  {
    this.mDropDownAlwaysVisible = paramBoolean;
  }
  
  public void setDropDownGravity(int paramInt)
  {
    this.mDropDownGravity = paramInt;
  }
  
  public void setEpicenterBounds(Rect paramRect)
  {
    if (paramRect != null) {
      paramRect = new Rect(paramRect);
    } else {
      paramRect = null;
    }
    this.mEpicenterBounds = paramRect;
  }
  
  @UnsupportedAppUsage
  public void setForceIgnoreOutsideTouch(boolean paramBoolean)
  {
    this.mForceIgnoreOutsideTouch = paramBoolean;
  }
  
  public void setHeight(int paramInt)
  {
    if ((paramInt < 0) && (-2 != paramInt) && (-1 != paramInt)) {
      if (this.mContext.getApplicationInfo().targetSdkVersion < 26)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Negative value ");
        localStringBuilder.append(paramInt);
        localStringBuilder.append(" passed to ListPopupWindow#setHeight produces undefined results");
        Log.e("ListPopupWindow", localStringBuilder.toString());
      }
      else
      {
        throw new IllegalArgumentException("Invalid height. Must be a positive value, MATCH_PARENT, or WRAP_CONTENT.");
      }
    }
    this.mDropDownHeight = paramInt;
  }
  
  public void setHorizontalOffset(int paramInt)
  {
    this.mDropDownHorizontalOffset = paramInt;
  }
  
  public void setInputMethodMode(int paramInt)
  {
    this.mPopup.setInputMethodMode(paramInt);
  }
  
  @UnsupportedAppUsage
  void setListItemExpandMax(int paramInt)
  {
    this.mListItemExpandMaximum = paramInt;
  }
  
  public void setListSelector(Drawable paramDrawable)
  {
    this.mDropDownListHighlight = paramDrawable;
  }
  
  public void setModal(boolean paramBoolean)
  {
    this.mModal = paramBoolean;
    this.mPopup.setFocusable(paramBoolean);
  }
  
  public void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener)
  {
    this.mPopup.setOnDismissListener(paramOnDismissListener);
  }
  
  public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    this.mItemClickListener = paramOnItemClickListener;
  }
  
  public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener paramOnItemSelectedListener)
  {
    this.mItemSelectedListener = paramOnItemSelectedListener;
  }
  
  public void setOverlapAnchor(boolean paramBoolean)
  {
    this.mOverlapAnchorSet = true;
    this.mOverlapAnchor = paramBoolean;
  }
  
  public void setPromptPosition(int paramInt)
  {
    this.mPromptPosition = paramInt;
  }
  
  public void setPromptView(View paramView)
  {
    boolean bool = isShowing();
    if (bool) {
      removePromptView();
    }
    this.mPromptView = paramView;
    if (bool) {
      show();
    }
  }
  
  public void setSelection(int paramInt)
  {
    DropDownListView localDropDownListView = this.mDropDownList;
    if ((isShowing()) && (localDropDownListView != null))
    {
      localDropDownListView.setListSelectionHidden(false);
      localDropDownListView.setSelection(paramInt);
      if (localDropDownListView.getChoiceMode() != 0) {
        localDropDownListView.setItemChecked(paramInt, true);
      }
    }
  }
  
  public void setSoftInputMode(int paramInt)
  {
    this.mPopup.setSoftInputMode(paramInt);
  }
  
  public void setVerticalOffset(int paramInt)
  {
    this.mDropDownVerticalOffset = paramInt;
    this.mDropDownVerticalOffsetSet = true;
  }
  
  public void setWidth(int paramInt)
  {
    this.mDropDownWidth = paramInt;
  }
  
  public void setWindowLayoutType(int paramInt)
  {
    this.mDropDownWindowLayoutType = paramInt;
  }
  
  public void show()
  {
    if (Android_Widget_ListPopupWindow.Extension.get().getExtension() != null) {
      ((Android_Widget_ListPopupWindow.Interface)Android_Widget_ListPopupWindow.Extension.get().getExtension().asInterface()).show(this);
    } else {
      originalShow();
    }
  }
  
  private static class Impl
    implements Android_Widget_ListPopupWindow.Interface
  {
    public void init(ListPopupWindow paramListPopupWindow, Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {}
    
    public void show(ListPopupWindow paramListPopupWindow)
    {
      paramListPopupWindow.originalShow();
    }
  }
  
  private class ListSelectorHider
    implements Runnable
  {
    private ListSelectorHider() {}
    
    public void run()
    {
      ListPopupWindow.this.clearListSelection();
    }
  }
  
  private class PopupDataSetObserver
    extends DataSetObserver
  {
    private PopupDataSetObserver() {}
    
    public void onChanged()
    {
      if (ListPopupWindow.this.isShowing()) {
        ListPopupWindow.this.show();
      }
    }
    
    public void onInvalidated()
    {
      ListPopupWindow.this.dismiss();
    }
  }
  
  private class PopupScrollListener
    implements AbsListView.OnScrollListener
  {
    private PopupScrollListener() {}
    
    public void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt)
    {
      if ((paramInt == 1) && (!ListPopupWindow.this.isInputMethodNotNeeded()) && (ListPopupWindow.this.mPopup.getContentView() != null))
      {
        ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
        ListPopupWindow.this.mResizePopupRunnable.run();
      }
    }
  }
  
  private class PopupTouchInterceptor
    implements View.OnTouchListener
  {
    private PopupTouchInterceptor() {}
    
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getAction();
      int j = (int)paramMotionEvent.getX();
      int k = (int)paramMotionEvent.getY();
      if ((i == 0) && (ListPopupWindow.this.mPopup != null) && (ListPopupWindow.this.mPopup.isShowing()) && (j >= 0) && (j < ListPopupWindow.this.mPopup.getWidth()) && (k >= 0) && (k < ListPopupWindow.this.mPopup.getHeight())) {
        ListPopupWindow.this.mHandler.postDelayed(ListPopupWindow.this.mResizePopupRunnable, 250L);
      } else if (i == 1) {
        ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
      }
      return false;
    }
  }
  
  private class ResizePopupRunnable
    implements Runnable
  {
    private ResizePopupRunnable() {}
    
    public void run()
    {
      if ((ListPopupWindow.this.mDropDownList != null) && (ListPopupWindow.this.mDropDownList.isAttachedToWindow()) && (ListPopupWindow.this.mDropDownList.getCount() > ListPopupWindow.this.mDropDownList.getChildCount()) && (ListPopupWindow.this.mDropDownList.getChildCount() <= ListPopupWindow.this.mListItemExpandMaximum))
      {
        ListPopupWindow.this.mPopup.setInputMethodMode(2);
        ListPopupWindow.this.show();
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ListPopupWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */