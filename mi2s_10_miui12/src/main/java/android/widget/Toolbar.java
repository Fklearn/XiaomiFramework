package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.CollapsibleActionView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import com.android.internal.R.styleable;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuBuilder.Callback;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPresenter;
import com.android.internal.view.menu.MenuPresenter.Callback;
import com.android.internal.view.menu.MenuView;
import com.android.internal.view.menu.SubMenuBuilder;
import com.android.internal.widget.DecorToolbar;
import com.android.internal.widget.ToolbarWidgetWrapper;
import java.util.ArrayList;
import java.util.List;

public class Toolbar
  extends ViewGroup
{
  private static final String TAG = "Toolbar";
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  private int mButtonGravity;
  private ImageButton mCollapseButtonView;
  private CharSequence mCollapseDescription;
  private Drawable mCollapseIcon;
  private boolean mCollapsible;
  private int mContentInsetEndWithActions;
  private int mContentInsetStartWithNavigation;
  private RtlSpacingHelper mContentInsets;
  private boolean mEatingTouch;
  View mExpandedActionView;
  private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
  private int mGravity = 8388627;
  private final ArrayList<View> mHiddenViews = new ArrayList();
  private ImageView mLogoView;
  private int mMaxButtonHeight;
  private MenuBuilder.Callback mMenuBuilderCallback;
  private ActionMenuView mMenuView;
  private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener = new ActionMenuView.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      if (Toolbar.this.mOnMenuItemClickListener != null) {
        return Toolbar.this.mOnMenuItemClickListener.onMenuItemClick(paramAnonymousMenuItem);
      }
      return false;
    }
  };
  private int mNavButtonStyle;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private ImageButton mNavButtonView;
  private OnMenuItemClickListener mOnMenuItemClickListener;
  private ActionMenuPresenter mOuterActionMenuPresenter;
  private Context mPopupContext;
  private int mPopupTheme;
  private final Runnable mShowOverflowMenuRunnable = new Runnable()
  {
    public void run()
    {
      Toolbar.this.showOverflowMenu();
    }
  };
  private CharSequence mSubtitleText;
  private int mSubtitleTextAppearance;
  private int mSubtitleTextColor;
  private TextView mSubtitleTextView;
  private final int[] mTempMargins = new int[2];
  private final ArrayList<View> mTempViews = new ArrayList();
  @UnsupportedAppUsage
  private int mTitleMarginBottom;
  @UnsupportedAppUsage
  private int mTitleMarginEnd;
  @UnsupportedAppUsage
  private int mTitleMarginStart;
  @UnsupportedAppUsage
  private int mTitleMarginTop;
  private CharSequence mTitleText;
  private int mTitleTextAppearance;
  private int mTitleTextColor;
  @UnsupportedAppUsage
  private TextView mTitleTextView;
  private ToolbarWidgetWrapper mWrapper;
  
  public Toolbar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Toolbar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843946);
  }
  
  public Toolbar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public Toolbar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Toolbar, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.Toolbar, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mTitleTextAppearance = localTypedArray.getResourceId(4, 0);
    this.mSubtitleTextAppearance = localTypedArray.getResourceId(5, 0);
    this.mNavButtonStyle = localTypedArray.getResourceId(27, 0);
    this.mGravity = localTypedArray.getInteger(0, this.mGravity);
    this.mButtonGravity = localTypedArray.getInteger(23, 48);
    paramInt1 = localTypedArray.getDimensionPixelOffset(17, 0);
    this.mTitleMarginBottom = paramInt1;
    this.mTitleMarginTop = paramInt1;
    this.mTitleMarginEnd = paramInt1;
    this.mTitleMarginStart = paramInt1;
    paramInt1 = localTypedArray.getDimensionPixelOffset(18, -1);
    if (paramInt1 >= 0) {
      this.mTitleMarginStart = paramInt1;
    }
    paramInt1 = localTypedArray.getDimensionPixelOffset(19, -1);
    if (paramInt1 >= 0) {
      this.mTitleMarginEnd = paramInt1;
    }
    paramInt1 = localTypedArray.getDimensionPixelOffset(20, -1);
    if (paramInt1 >= 0) {
      this.mTitleMarginTop = paramInt1;
    }
    paramInt1 = localTypedArray.getDimensionPixelOffset(21, -1);
    if (paramInt1 >= 0) {
      this.mTitleMarginBottom = paramInt1;
    }
    this.mMaxButtonHeight = localTypedArray.getDimensionPixelSize(22, -1);
    int i = localTypedArray.getDimensionPixelOffset(6, Integer.MIN_VALUE);
    paramInt1 = localTypedArray.getDimensionPixelOffset(7, Integer.MIN_VALUE);
    paramInt2 = localTypedArray.getDimensionPixelSize(8, 0);
    int j = localTypedArray.getDimensionPixelSize(9, 0);
    ensureContentInsets();
    this.mContentInsets.setAbsolute(paramInt2, j);
    if ((i != Integer.MIN_VALUE) || (paramInt1 != Integer.MIN_VALUE)) {
      this.mContentInsets.setRelative(i, paramInt1);
    }
    this.mContentInsetStartWithNavigation = localTypedArray.getDimensionPixelOffset(25, Integer.MIN_VALUE);
    this.mContentInsetEndWithActions = localTypedArray.getDimensionPixelOffset(26, Integer.MIN_VALUE);
    this.mCollapseIcon = localTypedArray.getDrawable(24);
    this.mCollapseDescription = localTypedArray.getText(13);
    paramContext = localTypedArray.getText(1);
    if (!TextUtils.isEmpty(paramContext)) {
      setTitle(paramContext);
    }
    paramContext = localTypedArray.getText(3);
    if (!TextUtils.isEmpty(paramContext)) {
      setSubtitle(paramContext);
    }
    this.mPopupContext = this.mContext;
    setPopupTheme(localTypedArray.getResourceId(10, 0));
    paramContext = localTypedArray.getDrawable(11);
    if (paramContext != null) {
      setNavigationIcon(paramContext);
    }
    paramContext = localTypedArray.getText(12);
    if (!TextUtils.isEmpty(paramContext)) {
      setNavigationContentDescription(paramContext);
    }
    paramContext = localTypedArray.getDrawable(2);
    if (paramContext != null) {
      setLogo(paramContext);
    }
    paramContext = localTypedArray.getText(16);
    if (!TextUtils.isEmpty(paramContext)) {
      setLogoDescription(paramContext);
    }
    if (localTypedArray.hasValue(14)) {
      setTitleTextColor(localTypedArray.getColor(14, -1));
    }
    if (localTypedArray.hasValue(15)) {
      setSubtitleTextColor(localTypedArray.getColor(15, -1));
    }
    localTypedArray.recycle();
  }
  
  private void addCustomViewsWithGravity(List<View> paramList, int paramInt)
  {
    int i = getLayoutDirection();
    int j = 1;
    if (i != 1) {
      j = 0;
    }
    int k = getChildCount();
    i = Gravity.getAbsoluteGravity(paramInt, getLayoutDirection());
    paramList.clear();
    View localView;
    LayoutParams localLayoutParams;
    if (j != 0) {
      for (paramInt = k - 1; paramInt >= 0; paramInt--)
      {
        localView = getChildAt(paramInt);
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if ((localLayoutParams.mViewType == 0) && (shouldLayout(localView)) && (getChildHorizontalGravity(localLayoutParams.gravity) == i)) {
          paramList.add(localView);
        }
      }
    } else {
      for (paramInt = 0; paramInt < k; paramInt++)
      {
        localView = getChildAt(paramInt);
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if ((localLayoutParams.mViewType == 0) && (shouldLayout(localView)) && (getChildHorizontalGravity(localLayoutParams.gravity) == i)) {
          paramList.add(localView);
        }
      }
    }
  }
  
  private void addSystemView(View paramView, boolean paramBoolean)
  {
    Object localObject = paramView.getLayoutParams();
    if (localObject == null) {
      localObject = generateDefaultLayoutParams();
    } else if (!checkLayoutParams((ViewGroup.LayoutParams)localObject)) {
      localObject = generateLayoutParams((ViewGroup.LayoutParams)localObject);
    } else {
      localObject = (LayoutParams)localObject;
    }
    ((LayoutParams)localObject).mViewType = 1;
    if ((paramBoolean) && (this.mExpandedActionView != null))
    {
      paramView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.mHiddenViews.add(paramView);
    }
    else
    {
      addView(paramView, (ViewGroup.LayoutParams)localObject);
    }
  }
  
  private void ensureCollapseButtonView()
  {
    if (this.mCollapseButtonView == null)
    {
      this.mCollapseButtonView = new ImageButton(getContext(), null, 0, this.mNavButtonStyle);
      this.mCollapseButtonView.setImageDrawable(this.mCollapseIcon);
      this.mCollapseButtonView.setContentDescription(this.mCollapseDescription);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (0x800003 | this.mButtonGravity & 0x70);
      localLayoutParams.mViewType = 2;
      this.mCollapseButtonView.setLayoutParams(localLayoutParams);
      this.mCollapseButtonView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Toolbar.this.collapseActionView();
        }
      });
    }
  }
  
  private void ensureContentInsets()
  {
    if (this.mContentInsets == null) {
      this.mContentInsets = new RtlSpacingHelper();
    }
  }
  
  private void ensureLogoView()
  {
    if (this.mLogoView == null) {
      this.mLogoView = new ImageView(getContext());
    }
  }
  
  private void ensureMenu()
  {
    ensureMenuView();
    if (this.mMenuView.peekMenu() == null)
    {
      MenuBuilder localMenuBuilder = (MenuBuilder)this.mMenuView.getMenu();
      if (this.mExpandedMenuPresenter == null) {
        this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter(null);
      }
      this.mMenuView.setExpandedActionViewsExclusive(true);
      localMenuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
    }
  }
  
  private void ensureMenuView()
  {
    if (this.mMenuView == null)
    {
      this.mMenuView = new ActionMenuView(getContext());
      this.mMenuView.setPopupTheme(this.mPopupTheme);
      this.mMenuView.setOnMenuItemClickListener(this.mMenuViewItemClickListener);
      this.mMenuView.setMenuCallbacks(this.mActionMenuPresenterCallback, this.mMenuBuilderCallback);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (0x800005 | this.mButtonGravity & 0x70);
      this.mMenuView.setLayoutParams(localLayoutParams);
      addSystemView(this.mMenuView, false);
    }
  }
  
  private void ensureNavButtonView()
  {
    if (this.mNavButtonView == null)
    {
      this.mNavButtonView = new ImageButton(getContext(), null, 0, this.mNavButtonStyle);
      LayoutParams localLayoutParams = generateDefaultLayoutParams();
      localLayoutParams.gravity = (0x800003 | this.mButtonGravity & 0x70);
      this.mNavButtonView.setLayoutParams(localLayoutParams);
    }
  }
  
  private int getChildHorizontalGravity(int paramInt)
  {
    int i = getLayoutDirection();
    int j = Gravity.getAbsoluteGravity(paramInt, i) & 0x7;
    if (j != 1)
    {
      paramInt = 3;
      if ((j != 3) && (j != 5))
      {
        if (i == 1) {
          paramInt = 5;
        }
        return paramInt;
      }
    }
    return j;
  }
  
  private int getChildTop(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramView.getMeasuredHeight();
    if (paramInt > 0) {
      paramInt = (i - paramInt) / 2;
    } else {
      paramInt = 0;
    }
    int j = getChildVerticalGravity(localLayoutParams.gravity);
    if (j != 48)
    {
      if (j != 80)
      {
        int k = getPaddingTop();
        int m = getPaddingBottom();
        paramInt = getHeight();
        j = (paramInt - k - m - i) / 2;
        if (j < localLayoutParams.topMargin)
        {
          paramInt = localLayoutParams.topMargin;
        }
        else
        {
          i = paramInt - m - i - j - k;
          paramInt = j;
          if (i < localLayoutParams.bottomMargin) {
            paramInt = Math.max(0, j - (localLayoutParams.bottomMargin - i));
          }
        }
        return k + paramInt;
      }
      return getHeight() - getPaddingBottom() - i - localLayoutParams.bottomMargin - paramInt;
    }
    return getPaddingTop() - paramInt;
  }
  
  private int getChildVerticalGravity(int paramInt)
  {
    paramInt &= 0x70;
    if ((paramInt != 16) && (paramInt != 48) && (paramInt != 80)) {
      return this.mGravity & 0x70;
    }
    return paramInt;
  }
  
  private int getHorizontalMargins(View paramView)
  {
    paramView = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return paramView.getMarginStart() + paramView.getMarginEnd();
  }
  
  private MenuInflater getMenuInflater()
  {
    return new MenuInflater(getContext());
  }
  
  private int getVerticalMargins(View paramView)
  {
    paramView = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return paramView.topMargin + paramView.bottomMargin;
  }
  
  private int getViewListMeasuredWidth(List<View> paramList, int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt[0];
    int j = paramArrayOfInt[1];
    int k = 0;
    int m = paramList.size();
    for (int n = 0; n < m; n++)
    {
      paramArrayOfInt = (View)paramList.get(n);
      LayoutParams localLayoutParams = (LayoutParams)paramArrayOfInt.getLayoutParams();
      i = localLayoutParams.leftMargin - i;
      j = localLayoutParams.rightMargin - j;
      int i1 = Math.max(0, i);
      int i2 = Math.max(0, j);
      i = Math.max(0, -i);
      j = Math.max(0, -j);
      k += paramArrayOfInt.getMeasuredWidth() + i1 + i2;
    }
    return k;
  }
  
  private boolean isChildOrHidden(View paramView)
  {
    boolean bool;
    if ((paramView.getParent() != this) && (!this.mHiddenViews.contains(paramView))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private static boolean isCustomView(View paramView)
  {
    boolean bool;
    if (((LayoutParams)paramView.getLayoutParams()).mViewType == 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private int layoutChildLeft(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = localLayoutParams.leftMargin - paramArrayOfInt[0];
    paramInt1 += Math.max(0, i);
    paramArrayOfInt[0] = Math.max(0, -i);
    i = getChildTop(paramView, paramInt2);
    paramInt2 = paramView.getMeasuredWidth();
    paramView.layout(paramInt1, i, paramInt1 + paramInt2, paramView.getMeasuredHeight() + i);
    return paramInt1 + (localLayoutParams.rightMargin + paramInt2);
  }
  
  private int layoutChildRight(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = localLayoutParams.rightMargin - paramArrayOfInt[1];
    paramInt1 -= Math.max(0, i);
    paramArrayOfInt[1] = Math.max(0, -i);
    paramInt2 = getChildTop(paramView, paramInt2);
    i = paramView.getMeasuredWidth();
    paramView.layout(paramInt1 - i, paramInt2, paramInt1, paramView.getMeasuredHeight() + paramInt2);
    return paramInt1 - (localLayoutParams.leftMargin + i);
  }
  
  private int measureChildCollapseMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = localMarginLayoutParams.leftMargin - paramArrayOfInt[0];
    int j = localMarginLayoutParams.rightMargin - paramArrayOfInt[1];
    int k = Math.max(0, i) + Math.max(0, j);
    paramArrayOfInt[0] = Math.max(0, -i);
    paramArrayOfInt[1] = Math.max(0, -j);
    paramView.measure(getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight + k + paramInt2, localMarginLayoutParams.width), getChildMeasureSpec(paramInt3, this.mPaddingTop + this.mPaddingBottom + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin + paramInt4, localMarginLayoutParams.height));
    return paramView.getMeasuredWidth() + k;
  }
  
  private void measureChildConstrained(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = getChildMeasureSpec(paramInt1, this.mPaddingLeft + this.mPaddingRight + localMarginLayoutParams.leftMargin + localMarginLayoutParams.rightMargin + paramInt2, localMarginLayoutParams.width);
    paramInt2 = getChildMeasureSpec(paramInt3, this.mPaddingTop + this.mPaddingBottom + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin + paramInt4, localMarginLayoutParams.height);
    paramInt3 = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = paramInt2;
    if (paramInt3 != 1073741824)
    {
      paramInt1 = paramInt2;
      if (paramInt5 >= 0)
      {
        if (paramInt3 != 0) {
          paramInt5 = Math.min(View.MeasureSpec.getSize(paramInt2), paramInt5);
        }
        paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt5, 1073741824);
      }
    }
    paramView.measure(i, paramInt1);
  }
  
  private void postShowOverflowMenu()
  {
    removeCallbacks(this.mShowOverflowMenuRunnable);
    post(this.mShowOverflowMenuRunnable);
  }
  
  private boolean shouldCollapse()
  {
    if (!this.mCollapsible) {
      return false;
    }
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((shouldLayout(localView)) && (localView.getMeasuredWidth() > 0) && (localView.getMeasuredHeight() > 0)) {
        return false;
      }
    }
    return true;
  }
  
  private boolean shouldLayout(View paramView)
  {
    boolean bool;
    if ((paramView != null) && (paramView.getParent() == this) && (paramView.getVisibility() != 8)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  void addChildrenForExpandedActionView()
  {
    for (int i = this.mHiddenViews.size() - 1; i >= 0; i--) {
      addView((View)this.mHiddenViews.get(i));
    }
    this.mHiddenViews.clear();
  }
  
  public boolean canShowOverflowMenu()
  {
    if (getVisibility() == 0)
    {
      ActionMenuView localActionMenuView = this.mMenuView;
      if ((localActionMenuView != null) && (localActionMenuView.isOverflowReserved())) {
        return true;
      }
    }
    boolean bool = false;
    return bool;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    boolean bool;
    if ((super.checkLayoutParams(paramLayoutParams)) && ((paramLayoutParams instanceof LayoutParams))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void collapseActionView()
  {
    Object localObject = this.mExpandedMenuPresenter;
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = ((ExpandedActionViewMenuPresenter)localObject).mCurrentExpandedItem;
    }
    if (localObject != null) {
      ((MenuItemImpl)localObject).collapseActionView();
    }
  }
  
  public void dismissPopupMenus()
  {
    ActionMenuView localActionMenuView = this.mMenuView;
    if (localActionMenuView != null) {
      localActionMenuView.dismissPopupMenus();
    }
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ActionBar.LayoutParams)) {
      return new LayoutParams((ActionBar.LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public CharSequence getCollapseContentDescription()
  {
    Object localObject = this.mCollapseButtonView;
    if (localObject != null) {
      localObject = ((ImageButton)localObject).getContentDescription();
    } else {
      localObject = null;
    }
    return (CharSequence)localObject;
  }
  
  public Drawable getCollapseIcon()
  {
    Object localObject = this.mCollapseButtonView;
    if (localObject != null) {
      localObject = ((ImageButton)localObject).getDrawable();
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  public int getContentInsetEnd()
  {
    RtlSpacingHelper localRtlSpacingHelper = this.mContentInsets;
    int i;
    if (localRtlSpacingHelper != null) {
      i = localRtlSpacingHelper.getEnd();
    } else {
      i = 0;
    }
    return i;
  }
  
  public int getContentInsetEndWithActions()
  {
    int i = this.mContentInsetEndWithActions;
    if (i == Integer.MIN_VALUE) {
      i = getContentInsetEnd();
    }
    return i;
  }
  
  public int getContentInsetLeft()
  {
    RtlSpacingHelper localRtlSpacingHelper = this.mContentInsets;
    int i;
    if (localRtlSpacingHelper != null) {
      i = localRtlSpacingHelper.getLeft();
    } else {
      i = 0;
    }
    return i;
  }
  
  public int getContentInsetRight()
  {
    RtlSpacingHelper localRtlSpacingHelper = this.mContentInsets;
    int i;
    if (localRtlSpacingHelper != null) {
      i = localRtlSpacingHelper.getRight();
    } else {
      i = 0;
    }
    return i;
  }
  
  public int getContentInsetStart()
  {
    RtlSpacingHelper localRtlSpacingHelper = this.mContentInsets;
    int i;
    if (localRtlSpacingHelper != null) {
      i = localRtlSpacingHelper.getStart();
    } else {
      i = 0;
    }
    return i;
  }
  
  public int getContentInsetStartWithNavigation()
  {
    int i = this.mContentInsetStartWithNavigation;
    if (i == Integer.MIN_VALUE) {
      i = getContentInsetStart();
    }
    return i;
  }
  
  public int getCurrentContentInsetEnd()
  {
    int i = 0;
    Object localObject = this.mMenuView;
    if (localObject != null)
    {
      localObject = ((ActionMenuView)localObject).peekMenu();
      if ((localObject != null) && (((MenuBuilder)localObject).hasVisibleItems())) {
        i = 1;
      } else {
        i = 0;
      }
    }
    if (i != 0) {
      i = Math.max(getContentInsetEnd(), Math.max(this.mContentInsetEndWithActions, 0));
    } else {
      i = getContentInsetEnd();
    }
    return i;
  }
  
  public int getCurrentContentInsetLeft()
  {
    int i;
    if (isLayoutRtl()) {
      i = getCurrentContentInsetEnd();
    } else {
      i = getCurrentContentInsetStart();
    }
    return i;
  }
  
  public int getCurrentContentInsetRight()
  {
    int i;
    if (isLayoutRtl()) {
      i = getCurrentContentInsetStart();
    } else {
      i = getCurrentContentInsetEnd();
    }
    return i;
  }
  
  public int getCurrentContentInsetStart()
  {
    int i;
    if (getNavigationIcon() != null) {
      i = Math.max(getContentInsetStart(), Math.max(this.mContentInsetStartWithNavigation, 0));
    } else {
      i = getContentInsetStart();
    }
    return i;
  }
  
  public Drawable getLogo()
  {
    Object localObject = this.mLogoView;
    if (localObject != null) {
      localObject = ((ImageView)localObject).getDrawable();
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  public CharSequence getLogoDescription()
  {
    Object localObject = this.mLogoView;
    if (localObject != null) {
      localObject = ((ImageView)localObject).getContentDescription();
    } else {
      localObject = null;
    }
    return (CharSequence)localObject;
  }
  
  public Menu getMenu()
  {
    ensureMenu();
    return this.mMenuView.getMenu();
  }
  
  public CharSequence getNavigationContentDescription()
  {
    Object localObject = this.mNavButtonView;
    if (localObject != null) {
      localObject = ((ImageButton)localObject).getContentDescription();
    } else {
      localObject = null;
    }
    return (CharSequence)localObject;
  }
  
  public Drawable getNavigationIcon()
  {
    Object localObject = this.mNavButtonView;
    if (localObject != null) {
      localObject = ((ImageButton)localObject).getDrawable();
    } else {
      localObject = null;
    }
    return (Drawable)localObject;
  }
  
  public View getNavigationView()
  {
    return this.mNavButtonView;
  }
  
  ActionMenuPresenter getOuterActionMenuPresenter()
  {
    return this.mOuterActionMenuPresenter;
  }
  
  public Drawable getOverflowIcon()
  {
    ensureMenu();
    return this.mMenuView.getOverflowIcon();
  }
  
  Context getPopupContext()
  {
    return this.mPopupContext;
  }
  
  public int getPopupTheme()
  {
    return this.mPopupTheme;
  }
  
  public CharSequence getSubtitle()
  {
    return this.mSubtitleText;
  }
  
  public CharSequence getTitle()
  {
    return this.mTitleText;
  }
  
  public int getTitleMarginBottom()
  {
    return this.mTitleMarginBottom;
  }
  
  public int getTitleMarginEnd()
  {
    return this.mTitleMarginEnd;
  }
  
  public int getTitleMarginStart()
  {
    return this.mTitleMarginStart;
  }
  
  public int getTitleMarginTop()
  {
    return this.mTitleMarginTop;
  }
  
  public DecorToolbar getWrapper()
  {
    if (this.mWrapper == null) {
      this.mWrapper = new ToolbarWidgetWrapper(this, true);
    }
    return this.mWrapper;
  }
  
  public boolean hasExpandedActionView()
  {
    ExpandedActionViewMenuPresenter localExpandedActionViewMenuPresenter = this.mExpandedMenuPresenter;
    boolean bool;
    if ((localExpandedActionViewMenuPresenter != null) && (localExpandedActionViewMenuPresenter.mCurrentExpandedItem != null)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean hideOverflowMenu()
  {
    ActionMenuView localActionMenuView = this.mMenuView;
    boolean bool;
    if ((localActionMenuView != null) && (localActionMenuView.hideOverflowMenu())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void inflateMenu(int paramInt)
  {
    getMenuInflater().inflate(paramInt, getMenu());
  }
  
  public boolean isOverflowMenuShowPending()
  {
    ActionMenuView localActionMenuView = this.mMenuView;
    boolean bool;
    if ((localActionMenuView != null) && (localActionMenuView.isOverflowMenuShowPending())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isOverflowMenuShowing()
  {
    ActionMenuView localActionMenuView = this.mMenuView;
    boolean bool;
    if ((localActionMenuView != null) && (localActionMenuView.isOverflowMenuShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isTitleTruncated()
  {
    Object localObject = this.mTitleTextView;
    if (localObject == null) {
      return false;
    }
    localObject = ((TextView)localObject).getLayout();
    if (localObject == null) {
      return false;
    }
    int i = ((Layout)localObject).getLineCount();
    for (int j = 0; j < i; j++) {
      if (((Layout)localObject).getEllipsisCount(j) > 0) {
        return true;
      }
    }
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    for (Object localObject = getParent(); (localObject != null) && ((localObject instanceof ViewGroup)); localObject = ((ViewGroup)localObject).getParent())
    {
      localObject = (ViewGroup)localObject;
      if (((ViewGroup)localObject).isKeyboardNavigationCluster())
      {
        setKeyboardNavigationCluster(false);
        if (!((ViewGroup)localObject).getTouchscreenBlocksFocus()) {
          break;
        }
        setTouchscreenBlocksFocus(false);
        break;
      }
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    removeCallbacks(this.mShowOverflowMenuRunnable);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    if (getLayoutDirection() == 1) {
      i = 1;
    } else {
      i = 0;
    }
    int j = getWidth();
    int k = getHeight();
    int m = getPaddingLeft();
    int n = getPaddingRight();
    int i1 = getPaddingTop();
    int i2 = getPaddingBottom();
    int i3 = m;
    int i4 = j - n;
    int[] arrayOfInt = this.mTempMargins;
    arrayOfInt[1] = 0;
    arrayOfInt[0] = 0;
    paramInt1 = getMinimumHeight();
    if (paramInt1 >= 0) {
      paramInt3 = Math.min(paramInt1, paramInt4 - paramInt2);
    } else {
      paramInt3 = 0;
    }
    paramInt1 = i3;
    paramInt4 = i4;
    if (shouldLayout(this.mNavButtonView)) {
      if (i != 0)
      {
        paramInt4 = layoutChildRight(this.mNavButtonView, i4, arrayOfInt, paramInt3);
        paramInt1 = i3;
      }
      else
      {
        paramInt1 = layoutChildLeft(this.mNavButtonView, i3, arrayOfInt, paramInt3);
        paramInt4 = i4;
      }
    }
    paramInt2 = paramInt1;
    i3 = paramInt4;
    if (shouldLayout(this.mCollapseButtonView)) {
      if (i != 0)
      {
        i3 = layoutChildRight(this.mCollapseButtonView, paramInt4, arrayOfInt, paramInt3);
        paramInt2 = paramInt1;
      }
      else
      {
        paramInt2 = layoutChildLeft(this.mCollapseButtonView, paramInt1, arrayOfInt, paramInt3);
        i3 = paramInt4;
      }
    }
    paramInt4 = paramInt2;
    paramInt1 = i3;
    if (shouldLayout(this.mMenuView)) {
      if (i != 0)
      {
        paramInt4 = layoutChildLeft(this.mMenuView, paramInt2, arrayOfInt, paramInt3);
        paramInt1 = i3;
      }
      else
      {
        paramInt1 = layoutChildRight(this.mMenuView, i3, arrayOfInt, paramInt3);
        paramInt4 = paramInt2;
      }
    }
    paramInt2 = getCurrentContentInsetLeft();
    i3 = getCurrentContentInsetRight();
    arrayOfInt[0] = Math.max(0, paramInt2 - paramInt4);
    arrayOfInt[1] = Math.max(0, i3 - (j - n - paramInt1));
    paramInt2 = Math.max(paramInt4, paramInt2);
    paramInt4 = Math.min(paramInt1, j - n - i3);
    i3 = paramInt2;
    paramInt1 = paramInt4;
    if (shouldLayout(this.mExpandedActionView)) {
      if (i != 0)
      {
        paramInt1 = layoutChildRight(this.mExpandedActionView, paramInt4, arrayOfInt, paramInt3);
        i3 = paramInt2;
      }
      else
      {
        i3 = layoutChildLeft(this.mExpandedActionView, paramInt2, arrayOfInt, paramInt3);
        paramInt1 = paramInt4;
      }
    }
    paramInt2 = i3;
    paramInt4 = paramInt1;
    if (shouldLayout(this.mLogoView)) {
      if (i != 0)
      {
        paramInt4 = layoutChildRight(this.mLogoView, paramInt1, arrayOfInt, paramInt3);
        paramInt2 = i3;
      }
      else
      {
        paramInt2 = layoutChildLeft(this.mLogoView, i3, arrayOfInt, paramInt3);
        paramInt4 = paramInt1;
      }
    }
    paramBoolean = shouldLayout(this.mTitleTextView);
    boolean bool = shouldLayout(this.mSubtitleTextView);
    paramInt1 = 0;
    Object localObject1;
    if (paramBoolean)
    {
      localObject1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
      paramInt1 = 0 + (((LayoutParams)localObject1).topMargin + this.mTitleTextView.getMeasuredHeight() + ((LayoutParams)localObject1).bottomMargin);
    }
    i4 = paramInt1;
    if (bool)
    {
      localObject1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
      i4 = paramInt1 + (((LayoutParams)localObject1).topMargin + this.mSubtitleTextView.getMeasuredHeight() + ((LayoutParams)localObject1).bottomMargin);
    }
    if ((!paramBoolean) && (!bool))
    {
      paramInt1 = paramInt2;
      paramInt2 = paramInt4;
    }
    else
    {
      if (paramBoolean) {
        localObject1 = this.mTitleTextView;
      } else {
        localObject1 = this.mSubtitleTextView;
      }
      if (bool) {
        localObject2 = this.mSubtitleTextView;
      } else {
        localObject2 = this.mTitleTextView;
      }
      localObject1 = (LayoutParams)((View)localObject1).getLayoutParams();
      Object localObject2 = (LayoutParams)((View)localObject2).getLayoutParams();
      if (paramBoolean) {
        if (this.mTitleTextView.getMeasuredWidth() > 0) {
          break label691;
        }
      }
      if ((bool) && (this.mSubtitleTextView.getMeasuredWidth() > 0)) {
        label691:
        i3 = 1;
      } else {
        i3 = 0;
      }
      paramInt1 = this.mGravity & 0x70;
      int i5;
      if (paramInt1 != 48)
      {
        if (paramInt1 != 80)
        {
          i5 = (k - i1 - i2 - i4) / 2;
          if (i5 < ((LayoutParams)localObject1).topMargin + this.mTitleMarginTop)
          {
            paramInt1 = ((LayoutParams)localObject1).topMargin + this.mTitleMarginTop;
          }
          else
          {
            i4 = k - i2 - i4 - i5 - i1;
            paramInt1 = i5;
            if (i4 < ((LayoutParams)localObject1).bottomMargin + this.mTitleMarginBottom) {
              paramInt1 = Math.max(0, i5 - (((LayoutParams)localObject2).bottomMargin + this.mTitleMarginBottom - i4));
            }
          }
          paramInt1 = i1 + paramInt1;
        }
        else
        {
          paramInt1 = k - i2 - ((LayoutParams)localObject2).bottomMargin - this.mTitleMarginBottom - i4;
        }
      }
      else {
        paramInt1 = getPaddingTop() + ((LayoutParams)localObject1).topMargin + this.mTitleMarginTop;
      }
      i4 = paramInt2;
      if (i != 0)
      {
        if (i3 != 0) {
          paramInt2 = this.mTitleMarginStart;
        } else {
          paramInt2 = 0;
        }
        i = paramInt2 - arrayOfInt[1];
        paramInt2 = paramInt4 - Math.max(0, i);
        arrayOfInt[1] = Math.max(0, -i);
        i = paramInt2;
        paramInt4 = paramInt2;
        if (paramBoolean)
        {
          localObject1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
          i1 = i - this.mTitleTextView.getMeasuredWidth();
          i5 = this.mTitleTextView.getMeasuredHeight() + paramInt1;
          this.mTitleTextView.layout(i1, paramInt1, i, i5);
          paramInt1 = i1 - this.mTitleMarginEnd;
          i5 += ((LayoutParams)localObject1).bottomMargin;
        }
        else
        {
          i5 = paramInt1;
          paramInt1 = i;
        }
        i = paramInt4;
        if (bool)
        {
          localObject1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
          i1 = i5 + ((LayoutParams)localObject1).topMargin;
          i5 = this.mSubtitleTextView.getMeasuredWidth();
          i = this.mSubtitleTextView.getMeasuredHeight() + i1;
          this.mSubtitleTextView.layout(paramInt4 - i5, i1, paramInt4, i);
          i = paramInt4 - this.mTitleMarginEnd;
          paramInt4 = ((LayoutParams)localObject1).bottomMargin;
        }
        if (i3 != 0) {
          paramInt2 = Math.min(paramInt1, i);
        }
        paramInt1 = i4;
      }
      else
      {
        if (i3 != 0) {
          paramInt2 = this.mTitleMarginStart;
        } else {
          paramInt2 = 0;
        }
        i = paramInt2 - arrayOfInt[0];
        paramInt2 = i4 + Math.max(0, i);
        arrayOfInt[0] = Math.max(0, -i);
        i4 = paramInt2;
        i = paramInt2;
        if (paramBoolean)
        {
          localObject1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
          i1 = this.mTitleTextView.getMeasuredWidth() + i4;
          i5 = this.mTitleTextView.getMeasuredHeight() + paramInt1;
          this.mTitleTextView.layout(i4, paramInt1, i1, i5);
          i4 = i1 + this.mTitleMarginEnd;
          i5 += ((LayoutParams)localObject1).bottomMargin;
        }
        else
        {
          i5 = paramInt1;
        }
        paramInt1 = paramInt2;
        i1 = i;
        if (bool)
        {
          localObject1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
          paramInt2 = i5 + ((LayoutParams)localObject1).topMargin;
          i1 = this.mSubtitleTextView.getMeasuredWidth() + i;
          i5 = this.mSubtitleTextView.getMeasuredHeight() + paramInt2;
          this.mSubtitleTextView.layout(i, paramInt2, i1, i5);
          i1 += this.mTitleMarginEnd;
          paramInt2 = ((LayoutParams)localObject1).bottomMargin;
        }
        paramInt2 = paramInt4;
        if (i3 != 0)
        {
          paramInt1 = Math.max(i4, i1);
          paramInt2 = paramInt4;
        }
      }
    }
    paramInt4 = paramInt3;
    addCustomViewsWithGravity(this.mTempViews, 3);
    i3 = this.mTempViews.size();
    for (paramInt3 = 0; paramInt3 < i3; paramInt3++) {
      paramInt1 = layoutChildLeft((View)this.mTempViews.get(paramInt3), paramInt1, arrayOfInt, paramInt4);
    }
    addCustomViewsWithGravity(this.mTempViews, 5);
    i3 = this.mTempViews.size();
    for (paramInt3 = 0; paramInt3 < i3; paramInt3++) {
      paramInt2 = layoutChildRight((View)this.mTempViews.get(paramInt3), paramInt2, arrayOfInt, paramInt4);
    }
    addCustomViewsWithGravity(this.mTempViews, 1);
    i3 = getViewListMeasuredWidth(this.mTempViews, arrayOfInt);
    paramInt3 = m + (j - m - n) / 2 - i3 / 2;
    i3 = paramInt3 + i3;
    if (paramInt3 >= paramInt1)
    {
      paramInt1 = paramInt3;
      if (i3 > paramInt2) {
        paramInt1 = paramInt3 - (i3 - paramInt2);
      }
    }
    paramInt3 = this.mTempViews.size();
    paramInt2 = paramInt1;
    for (paramInt1 = 0; paramInt1 < paramInt3; paramInt1++) {
      paramInt2 = layoutChildLeft((View)this.mTempViews.get(paramInt1), paramInt2, arrayOfInt, paramInt4);
    }
    this.mTempViews.clear();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = 0;
    int[] arrayOfInt = this.mTempMargins;
    if (isLayoutRtl())
    {
      k = 1;
      m = 0;
    }
    else
    {
      k = 0;
      m = 1;
    }
    int n = 0;
    if (shouldLayout(this.mNavButtonView))
    {
      measureChildConstrained(this.mNavButtonView, paramInt1, 0, paramInt2, 0, this.mMaxButtonHeight);
      n = this.mNavButtonView.getMeasuredWidth() + getHorizontalMargins(this.mNavButtonView);
      i = Math.max(0, this.mNavButtonView.getMeasuredHeight() + getVerticalMargins(this.mNavButtonView));
      j = combineMeasuredStates(0, this.mNavButtonView.getMeasuredState());
    }
    int i1 = i;
    int i2 = j;
    if (shouldLayout(this.mCollapseButtonView))
    {
      measureChildConstrained(this.mCollapseButtonView, paramInt1, 0, paramInt2, 0, this.mMaxButtonHeight);
      n = this.mCollapseButtonView.getMeasuredWidth() + getHorizontalMargins(this.mCollapseButtonView);
      i1 = Math.max(i, this.mCollapseButtonView.getMeasuredHeight() + getVerticalMargins(this.mCollapseButtonView));
      i2 = combineMeasuredStates(j, this.mCollapseButtonView.getMeasuredState());
    }
    j = getCurrentContentInsetStart();
    i = 0 + Math.max(j, n);
    arrayOfInt[k] = Math.max(0, j - n);
    if (shouldLayout(this.mMenuView))
    {
      measureChildConstrained(this.mMenuView, paramInt1, i, paramInt2, 0, this.mMaxButtonHeight);
      k = this.mMenuView.getMeasuredWidth();
      n = getHorizontalMargins(this.mMenuView);
      i1 = Math.max(i1, this.mMenuView.getMeasuredHeight() + getVerticalMargins(this.mMenuView));
      j = combineMeasuredStates(i2, this.mMenuView.getMeasuredState());
      i2 = k + n;
    }
    else
    {
      j = i2;
      i2 = 0;
    }
    n = getCurrentContentInsetEnd();
    int k = i + Math.max(n, i2);
    arrayOfInt[m] = Math.max(0, n - i2);
    if (shouldLayout(this.mExpandedActionView))
    {
      k += measureChildCollapseMargins(this.mExpandedActionView, paramInt1, k, paramInt2, 0, arrayOfInt);
      i = Math.max(i1, this.mExpandedActionView.getMeasuredHeight() + getVerticalMargins(this.mExpandedActionView));
      j = combineMeasuredStates(j, this.mExpandedActionView.getMeasuredState());
    }
    else
    {
      i = i1;
    }
    int m = k;
    n = i;
    i1 = j;
    if (shouldLayout(this.mLogoView))
    {
      m = k + measureChildCollapseMargins(this.mLogoView, paramInt1, k, paramInt2, 0, arrayOfInt);
      n = Math.max(i, this.mLogoView.getMeasuredHeight() + getVerticalMargins(this.mLogoView));
      i1 = combineMeasuredStates(j, this.mLogoView.getMeasuredState());
    }
    int i3 = getChildCount();
    k = n;
    i = 0;
    j = i2;
    n = i3;
    i2 = k;
    while (i < n)
    {
      View localView = getChildAt(i);
      if ((((LayoutParams)localView.getLayoutParams()).mViewType == 0) && (shouldLayout(localView)))
      {
        m += measureChildCollapseMargins(localView, paramInt1, m, paramInt2, 0, arrayOfInt);
        i2 = Math.max(i2, localView.getMeasuredHeight() + getVerticalMargins(localView));
        i1 = combineMeasuredStates(i1, localView.getMeasuredState());
      }
      i++;
    }
    n = 0;
    j = 0;
    k = this.mTitleMarginTop + this.mTitleMarginBottom;
    i3 = this.mTitleMarginStart + this.mTitleMarginEnd;
    i = i1;
    if (shouldLayout(this.mTitleTextView))
    {
      measureChildCollapseMargins(this.mTitleTextView, paramInt1, m + i3, paramInt2, k, arrayOfInt);
      n = this.mTitleTextView.getMeasuredWidth() + getHorizontalMargins(this.mTitleTextView);
      j = this.mTitleTextView.getMeasuredHeight() + getVerticalMargins(this.mTitleTextView);
      i = combineMeasuredStates(i1, this.mTitleTextView.getMeasuredState());
    }
    if (shouldLayout(this.mSubtitleTextView))
    {
      n = Math.max(n, measureChildCollapseMargins(this.mSubtitleTextView, paramInt1, m + i3, paramInt2, j + k, arrayOfInt));
      k = this.mSubtitleTextView.getMeasuredHeight();
      i1 = getVerticalMargins(this.mSubtitleTextView);
      i = combineMeasuredStates(i, this.mSubtitleTextView.getMeasuredState());
      i1 = j + (k + i1);
    }
    else
    {
      i1 = j;
    }
    i1 = Math.max(i2, i1);
    i3 = getPaddingLeft();
    k = getPaddingRight();
    i2 = getPaddingTop();
    j = getPaddingBottom();
    n = resolveSizeAndState(Math.max(m + n + (i3 + k), getSuggestedMinimumWidth()), paramInt1, 0xFF000000 & i);
    paramInt1 = resolveSizeAndState(Math.max(i1 + (i2 + j), getSuggestedMinimumHeight()), paramInt2, i << 16);
    if (shouldCollapse()) {
      paramInt1 = 0;
    }
    setMeasuredDimension(n, paramInt1);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    paramParcelable = this.mMenuView;
    if (paramParcelable != null) {
      paramParcelable = paramParcelable.peekMenu();
    } else {
      paramParcelable = null;
    }
    if ((localSavedState.expandedMenuItemId != 0) && (this.mExpandedMenuPresenter != null) && (paramParcelable != null))
    {
      paramParcelable = paramParcelable.findItem(localSavedState.expandedMenuItemId);
      if (paramParcelable != null) {
        paramParcelable.expandActionView();
      }
    }
    if (localSavedState.isOverflowOpen) {
      postShowOverflowMenu();
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    ensureContentInsets();
    RtlSpacingHelper localRtlSpacingHelper = this.mContentInsets;
    boolean bool = true;
    if (paramInt != 1) {
      bool = false;
    }
    localRtlSpacingHelper.setDirection(bool);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    ExpandedActionViewMenuPresenter localExpandedActionViewMenuPresenter = this.mExpandedMenuPresenter;
    if ((localExpandedActionViewMenuPresenter != null) && (localExpandedActionViewMenuPresenter.mCurrentExpandedItem != null)) {
      localSavedState.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
    }
    localSavedState.isOverflowOpen = isOverflowMenuShowing();
    return localSavedState;
  }
  
  protected void onSetLayoutParams(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (!checkLayoutParams(paramLayoutParams)) {
      paramView.setLayoutParams(generateLayoutParams(paramLayoutParams));
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionMasked();
    if (i == 0) {
      this.mEatingTouch = false;
    }
    if (!this.mEatingTouch)
    {
      boolean bool = super.onTouchEvent(paramMotionEvent);
      if ((i == 0) && (!bool)) {
        this.mEatingTouch = true;
      }
    }
    if ((i == 1) || (i == 3)) {
      this.mEatingTouch = false;
    }
    return true;
  }
  
  void removeChildrenForExpandedActionView()
  {
    for (int i = getChildCount() - 1; i >= 0; i--)
    {
      View localView = getChildAt(i);
      if ((((LayoutParams)localView.getLayoutParams()).mViewType != 2) && (localView != this.mMenuView))
      {
        removeViewAt(i);
        this.mHiddenViews.add(localView);
      }
    }
  }
  
  public void setCollapseContentDescription(int paramInt)
  {
    CharSequence localCharSequence;
    if (paramInt != 0) {
      localCharSequence = getContext().getText(paramInt);
    } else {
      localCharSequence = null;
    }
    setCollapseContentDescription(localCharSequence);
  }
  
  public void setCollapseContentDescription(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureCollapseButtonView();
    }
    ImageButton localImageButton = this.mCollapseButtonView;
    if (localImageButton != null) {
      localImageButton.setContentDescription(paramCharSequence);
    }
  }
  
  public void setCollapseIcon(int paramInt)
  {
    setCollapseIcon(getContext().getDrawable(paramInt));
  }
  
  public void setCollapseIcon(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      ensureCollapseButtonView();
      this.mCollapseButtonView.setImageDrawable(paramDrawable);
    }
    else
    {
      paramDrawable = this.mCollapseButtonView;
      if (paramDrawable != null) {
        paramDrawable.setImageDrawable(this.mCollapseIcon);
      }
    }
  }
  
  public void setCollapsible(boolean paramBoolean)
  {
    this.mCollapsible = paramBoolean;
    requestLayout();
  }
  
  public void setContentInsetEndWithActions(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = Integer.MIN_VALUE;
    }
    if (i != this.mContentInsetEndWithActions)
    {
      this.mContentInsetEndWithActions = i;
      if (getNavigationIcon() != null) {
        requestLayout();
      }
    }
  }
  
  public void setContentInsetStartWithNavigation(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 0) {
      i = Integer.MIN_VALUE;
    }
    if (i != this.mContentInsetStartWithNavigation)
    {
      this.mContentInsetStartWithNavigation = i;
      if (getNavigationIcon() != null) {
        requestLayout();
      }
    }
  }
  
  public void setContentInsetsAbsolute(int paramInt1, int paramInt2)
  {
    ensureContentInsets();
    this.mContentInsets.setAbsolute(paramInt1, paramInt2);
  }
  
  public void setContentInsetsRelative(int paramInt1, int paramInt2)
  {
    ensureContentInsets();
    this.mContentInsets.setRelative(paramInt1, paramInt2);
  }
  
  public void setLogo(int paramInt)
  {
    setLogo(getContext().getDrawable(paramInt));
  }
  
  public void setLogo(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      ensureLogoView();
      if (!isChildOrHidden(this.mLogoView)) {
        addSystemView(this.mLogoView, true);
      }
    }
    else
    {
      localImageView = this.mLogoView;
      if ((localImageView != null) && (isChildOrHidden(localImageView)))
      {
        removeView(this.mLogoView);
        this.mHiddenViews.remove(this.mLogoView);
      }
    }
    ImageView localImageView = this.mLogoView;
    if (localImageView != null) {
      localImageView.setImageDrawable(paramDrawable);
    }
  }
  
  public void setLogoDescription(int paramInt)
  {
    setLogoDescription(getContext().getText(paramInt));
  }
  
  public void setLogoDescription(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureLogoView();
    }
    ImageView localImageView = this.mLogoView;
    if (localImageView != null) {
      localImageView.setContentDescription(paramCharSequence);
    }
  }
  
  public void setMenu(MenuBuilder paramMenuBuilder, ActionMenuPresenter paramActionMenuPresenter)
  {
    if ((paramMenuBuilder == null) && (this.mMenuView == null)) {
      return;
    }
    ensureMenuView();
    MenuBuilder localMenuBuilder = this.mMenuView.peekMenu();
    if (localMenuBuilder == paramMenuBuilder) {
      return;
    }
    if (localMenuBuilder != null)
    {
      localMenuBuilder.removeMenuPresenter(this.mOuterActionMenuPresenter);
      localMenuBuilder.removeMenuPresenter(this.mExpandedMenuPresenter);
    }
    if (this.mExpandedMenuPresenter == null) {
      this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter(null);
    }
    paramActionMenuPresenter.setExpandedActionViewsExclusive(true);
    if (paramMenuBuilder != null)
    {
      paramMenuBuilder.addMenuPresenter(paramActionMenuPresenter, this.mPopupContext);
      paramMenuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
    }
    else
    {
      paramActionMenuPresenter.initForMenu(this.mPopupContext, null);
      this.mExpandedMenuPresenter.initForMenu(this.mPopupContext, null);
      paramActionMenuPresenter.updateMenuView(true);
      this.mExpandedMenuPresenter.updateMenuView(true);
    }
    this.mMenuView.setPopupTheme(this.mPopupTheme);
    this.mMenuView.setPresenter(paramActionMenuPresenter);
    this.mOuterActionMenuPresenter = paramActionMenuPresenter;
  }
  
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    this.mActionMenuPresenterCallback = paramCallback;
    this.mMenuBuilderCallback = paramCallback1;
    ActionMenuView localActionMenuView = this.mMenuView;
    if (localActionMenuView != null) {
      localActionMenuView.setMenuCallbacks(paramCallback, paramCallback1);
    }
  }
  
  public void setNavigationContentDescription(int paramInt)
  {
    CharSequence localCharSequence;
    if (paramInt != 0) {
      localCharSequence = getContext().getText(paramInt);
    } else {
      localCharSequence = null;
    }
    setNavigationContentDescription(localCharSequence);
  }
  
  public void setNavigationContentDescription(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      ensureNavButtonView();
    }
    ImageButton localImageButton = this.mNavButtonView;
    if (localImageButton != null) {
      localImageButton.setContentDescription(paramCharSequence);
    }
  }
  
  public void setNavigationIcon(int paramInt)
  {
    setNavigationIcon(getContext().getDrawable(paramInt));
  }
  
  public void setNavigationIcon(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      ensureNavButtonView();
      if (!isChildOrHidden(this.mNavButtonView)) {
        addSystemView(this.mNavButtonView, true);
      }
    }
    else
    {
      localImageButton = this.mNavButtonView;
      if ((localImageButton != null) && (isChildOrHidden(localImageButton)))
      {
        removeView(this.mNavButtonView);
        this.mHiddenViews.remove(this.mNavButtonView);
      }
    }
    ImageButton localImageButton = this.mNavButtonView;
    if (localImageButton != null) {
      localImageButton.setImageDrawable(paramDrawable);
    }
  }
  
  public void setNavigationOnClickListener(View.OnClickListener paramOnClickListener)
  {
    ensureNavButtonView();
    this.mNavButtonView.setOnClickListener(paramOnClickListener);
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    this.mOnMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void setOverflowIcon(Drawable paramDrawable)
  {
    ensureMenu();
    this.mMenuView.setOverflowIcon(paramDrawable);
  }
  
  public void setPopupTheme(int paramInt)
  {
    if (this.mPopupTheme != paramInt)
    {
      this.mPopupTheme = paramInt;
      if (paramInt == 0) {
        this.mPopupContext = this.mContext;
      } else {
        this.mPopupContext = new ContextThemeWrapper(this.mContext, paramInt);
      }
    }
  }
  
  public void setSubtitle(int paramInt)
  {
    setSubtitle(getContext().getText(paramInt));
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      if (this.mSubtitleTextView == null)
      {
        this.mSubtitleTextView = new TextView(getContext());
        this.mSubtitleTextView.setSingleLine();
        this.mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        int i = this.mSubtitleTextAppearance;
        if (i != 0) {
          this.mSubtitleTextView.setTextAppearance(i);
        }
        i = this.mSubtitleTextColor;
        if (i != 0) {
          this.mSubtitleTextView.setTextColor(i);
        }
      }
      if (!isChildOrHidden(this.mSubtitleTextView)) {
        addSystemView(this.mSubtitleTextView, true);
      }
    }
    else
    {
      localTextView = this.mSubtitleTextView;
      if ((localTextView != null) && (isChildOrHidden(localTextView)))
      {
        removeView(this.mSubtitleTextView);
        this.mHiddenViews.remove(this.mSubtitleTextView);
      }
    }
    TextView localTextView = this.mSubtitleTextView;
    if (localTextView != null) {
      localTextView.setText(paramCharSequence);
    }
    this.mSubtitleText = paramCharSequence;
  }
  
  public void setSubtitleTextAppearance(Context paramContext, int paramInt)
  {
    this.mSubtitleTextAppearance = paramInt;
    paramContext = this.mSubtitleTextView;
    if (paramContext != null) {
      paramContext.setTextAppearance(paramInt);
    }
  }
  
  public void setSubtitleTextColor(int paramInt)
  {
    this.mSubtitleTextColor = paramInt;
    TextView localTextView = this.mSubtitleTextView;
    if (localTextView != null) {
      localTextView.setTextColor(paramInt);
    }
  }
  
  public void setTitle(int paramInt)
  {
    setTitle(getContext().getText(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      if (this.mTitleTextView == null)
      {
        this.mTitleTextView = new TextView(getContext());
        this.mTitleTextView.setSingleLine();
        this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        int i = this.mTitleTextAppearance;
        if (i != 0) {
          this.mTitleTextView.setTextAppearance(i);
        }
        i = this.mTitleTextColor;
        if (i != 0) {
          this.mTitleTextView.setTextColor(i);
        }
      }
      if (!isChildOrHidden(this.mTitleTextView)) {
        addSystemView(this.mTitleTextView, true);
      }
    }
    else
    {
      localTextView = this.mTitleTextView;
      if ((localTextView != null) && (isChildOrHidden(localTextView)))
      {
        removeView(this.mTitleTextView);
        this.mHiddenViews.remove(this.mTitleTextView);
      }
    }
    TextView localTextView = this.mTitleTextView;
    if (localTextView != null) {
      localTextView.setText(paramCharSequence);
    }
    this.mTitleText = paramCharSequence;
  }
  
  public void setTitleMargin(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mTitleMarginStart = paramInt1;
    this.mTitleMarginTop = paramInt2;
    this.mTitleMarginEnd = paramInt3;
    this.mTitleMarginBottom = paramInt4;
    requestLayout();
  }
  
  public void setTitleMarginBottom(int paramInt)
  {
    this.mTitleMarginBottom = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginEnd(int paramInt)
  {
    this.mTitleMarginEnd = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginStart(int paramInt)
  {
    this.mTitleMarginStart = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginTop(int paramInt)
  {
    this.mTitleMarginTop = paramInt;
    requestLayout();
  }
  
  public void setTitleTextAppearance(Context paramContext, int paramInt)
  {
    this.mTitleTextAppearance = paramInt;
    paramContext = this.mTitleTextView;
    if (paramContext != null) {
      paramContext.setTextAppearance(paramInt);
    }
  }
  
  public void setTitleTextColor(int paramInt)
  {
    this.mTitleTextColor = paramInt;
    TextView localTextView = this.mTitleTextView;
    if (localTextView != null) {
      localTextView.setTextColor(paramInt);
    }
  }
  
  public boolean showOverflowMenu()
  {
    ActionMenuView localActionMenuView = this.mMenuView;
    boolean bool;
    if ((localActionMenuView != null) && (localActionMenuView.showOverflowMenu())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private class ExpandedActionViewMenuPresenter
    implements MenuPresenter
  {
    MenuItemImpl mCurrentExpandedItem;
    MenuBuilder mMenu;
    
    private ExpandedActionViewMenuPresenter() {}
    
    public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
    {
      if ((Toolbar.this.mExpandedActionView instanceof CollapsibleActionView)) {
        ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewCollapsed();
      }
      paramMenuBuilder = Toolbar.this;
      paramMenuBuilder.removeView(paramMenuBuilder.mExpandedActionView);
      paramMenuBuilder = Toolbar.this;
      paramMenuBuilder.removeView(paramMenuBuilder.mCollapseButtonView);
      paramMenuBuilder = Toolbar.this;
      paramMenuBuilder.mExpandedActionView = null;
      paramMenuBuilder.addChildrenForExpandedActionView();
      this.mCurrentExpandedItem = null;
      Toolbar.this.requestLayout();
      paramMenuItemImpl.setActionViewExpanded(false);
      return true;
    }
    
    public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl)
    {
      Toolbar.this.ensureCollapseButtonView();
      ViewParent localViewParent = Toolbar.this.mCollapseButtonView.getParent();
      paramMenuBuilder = Toolbar.this;
      if (localViewParent != paramMenuBuilder) {
        paramMenuBuilder.addView(paramMenuBuilder.mCollapseButtonView);
      }
      Toolbar.this.mExpandedActionView = paramMenuItemImpl.getActionView();
      this.mCurrentExpandedItem = paramMenuItemImpl;
      localViewParent = Toolbar.this.mExpandedActionView.getParent();
      paramMenuBuilder = Toolbar.this;
      if (localViewParent != paramMenuBuilder)
      {
        paramMenuBuilder = paramMenuBuilder.generateDefaultLayoutParams();
        paramMenuBuilder.gravity = (0x800003 | Toolbar.this.mButtonGravity & 0x70);
        paramMenuBuilder.mViewType = 2;
        Toolbar.this.mExpandedActionView.setLayoutParams(paramMenuBuilder);
        paramMenuBuilder = Toolbar.this;
        paramMenuBuilder.addView(paramMenuBuilder.mExpandedActionView);
      }
      Toolbar.this.removeChildrenForExpandedActionView();
      Toolbar.this.requestLayout();
      paramMenuItemImpl.setActionViewExpanded(true);
      if ((Toolbar.this.mExpandedActionView instanceof CollapsibleActionView)) {
        ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewExpanded();
      }
      return true;
    }
    
    public boolean flagActionItems()
    {
      return false;
    }
    
    public int getId()
    {
      return 0;
    }
    
    public MenuView getMenuView(ViewGroup paramViewGroup)
    {
      return null;
    }
    
    public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder)
    {
      paramContext = this.mMenu;
      if (paramContext != null)
      {
        MenuItemImpl localMenuItemImpl = this.mCurrentExpandedItem;
        if (localMenuItemImpl != null) {
          paramContext.collapseItemActionView(localMenuItemImpl);
        }
      }
      this.mMenu = paramMenuBuilder;
    }
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public void onRestoreInstanceState(Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState()
    {
      return null;
    }
    
    public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
    {
      return false;
    }
    
    public void setCallback(MenuPresenter.Callback paramCallback) {}
    
    public void updateMenuView(boolean paramBoolean)
    {
      if (this.mCurrentExpandedItem != null)
      {
        int i = 0;
        MenuBuilder localMenuBuilder = this.mMenu;
        int j = i;
        if (localMenuBuilder != null)
        {
          int k = localMenuBuilder.size();
          for (int m = 0;; m++)
          {
            j = i;
            if (m >= k) {
              break;
            }
            if (this.mMenu.getItem(m) == this.mCurrentExpandedItem)
            {
              j = 1;
              break;
            }
          }
        }
        if (j == 0) {
          collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
        }
      }
    }
  }
  
  public static class LayoutParams
    extends ActionBar.LayoutParams
  {
    static final int CUSTOM = 0;
    static final int EXPANDED = 2;
    static final int SYSTEM = 1;
    int mViewType = 0;
    
    public LayoutParams(int paramInt)
    {
      this(-2, -1, paramInt);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.gravity = 8388627;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2);
      this.gravity = paramInt3;
    }
    
    public LayoutParams(ActionBar.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
      copyMarginsFrom(paramMarginLayoutParams);
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.mViewType = paramLayoutParams.mViewType;
    }
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public Toolbar.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Toolbar.SavedState(paramAnonymousParcel);
      }
      
      public Toolbar.SavedState[] newArray(int paramAnonymousInt)
      {
        return new Toolbar.SavedState[paramAnonymousInt];
      }
    };
    public int expandedMenuItemId;
    public boolean isOverflowOpen;
    
    public SavedState(Parcel paramParcel)
    {
      super();
      this.expandedMenuItemId = paramParcel.readInt();
      boolean bool;
      if (paramParcel.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      this.isOverflowOpen = bool;
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.expandedMenuItemId);
      paramParcel.writeInt(this.isOverflowOpen);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Toolbar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */