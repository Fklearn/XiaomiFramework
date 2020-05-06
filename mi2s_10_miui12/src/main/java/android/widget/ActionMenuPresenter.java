package android.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.ActionProvider;
import android.view.ActionProvider.SubUiVisibilityListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.view.ActionBarPolicy;
import com.android.internal.view.menu.ActionMenuItemView;
import com.android.internal.view.menu.ActionMenuItemView.PopupCallback;
import com.android.internal.view.menu.BaseMenuPresenter;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPopup;
import com.android.internal.view.menu.MenuPopupHelper;
import com.android.internal.view.menu.MenuPresenter.Callback;
import com.android.internal.view.menu.MenuView;
import com.android.internal.view.menu.MenuView.ItemView;
import com.android.internal.view.menu.ShowableListMenu;
import com.android.internal.view.menu.SubMenuBuilder;
import java.util.ArrayList;
import java.util.List;

public class ActionMenuPresenter
  extends BaseMenuPresenter
  implements ActionProvider.SubUiVisibilityListener
{
  private static final boolean ACTIONBAR_ANIMATIONS_ENABLED = false;
  private static final int ITEM_ANIMATION_DURATION = 150;
  private final SparseBooleanArray mActionButtonGroups = new SparseBooleanArray();
  private ActionButtonSubmenu mActionButtonPopup;
  private int mActionItemWidthLimit;
  private View.OnAttachStateChangeListener mAttachStateChangeListener = new View.OnAttachStateChangeListener()
  {
    public void onViewAttachedToWindow(View paramAnonymousView) {}
    
    public void onViewDetachedFromWindow(View paramAnonymousView)
    {
      ((View)ActionMenuPresenter.this.mMenuView).getViewTreeObserver().removeOnPreDrawListener(ActionMenuPresenter.this.mItemAnimationPreDrawListener);
      ActionMenuPresenter.this.mPreLayoutItems.clear();
      ActionMenuPresenter.this.mPostLayoutItems.clear();
    }
  };
  private boolean mExpandedActionViewsExclusive;
  private ViewTreeObserver.OnPreDrawListener mItemAnimationPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      ActionMenuPresenter.this.computeMenuItemAnimationInfo(false);
      ((View)ActionMenuPresenter.this.mMenuView).getViewTreeObserver().removeOnPreDrawListener(this);
      ActionMenuPresenter.this.runItemAnimations();
      return true;
    }
  };
  private int mMaxItems;
  private boolean mMaxItemsSet;
  private int mMinCellSize;
  int mOpenSubMenuId;
  private OverflowMenuButton mOverflowButton;
  private OverflowPopup mOverflowPopup;
  private Drawable mPendingOverflowIcon;
  private boolean mPendingOverflowIconSet;
  private ActionMenuPopupCallback mPopupCallback;
  final PopupPresenterCallback mPopupPresenterCallback = new PopupPresenterCallback(null);
  private SparseArray<MenuItemLayoutInfo> mPostLayoutItems = new SparseArray();
  private OpenOverflowRunnable mPostedOpenRunnable;
  private SparseArray<MenuItemLayoutInfo> mPreLayoutItems = new SparseArray();
  private boolean mReserveOverflow;
  private boolean mReserveOverflowSet;
  private List<ItemAnimationInfo> mRunningItemAnimations = new ArrayList();
  private boolean mStrictWidthLimit;
  private int mWidthLimit;
  private boolean mWidthLimitSet;
  
  public ActionMenuPresenter(Context paramContext)
  {
    super(paramContext, 17367071, 17367070);
  }
  
  private void computeMenuItemAnimationInfo(boolean paramBoolean)
  {
    ViewGroup localViewGroup = (ViewGroup)this.mMenuView;
    int i = localViewGroup.getChildCount();
    SparseArray localSparseArray;
    if (paramBoolean) {
      localSparseArray = this.mPreLayoutItems;
    } else {
      localSparseArray = this.mPostLayoutItems;
    }
    for (int j = 0; j < i; j++)
    {
      View localView = localViewGroup.getChildAt(j);
      int k = localView.getId();
      if ((k > 0) && (localView.getWidth() != 0) && (localView.getHeight() != 0)) {
        localSparseArray.put(k, new MenuItemLayoutInfo(localView, paramBoolean));
      }
    }
  }
  
  private View findViewForItem(MenuItem paramMenuItem)
  {
    ViewGroup localViewGroup = (ViewGroup)this.mMenuView;
    if (localViewGroup == null) {
      return null;
    }
    int i = localViewGroup.getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = localViewGroup.getChildAt(j);
      if (((localView instanceof MenuView.ItemView)) && (((MenuView.ItemView)localView).getItemData() == paramMenuItem)) {
        return localView;
      }
    }
    return null;
  }
  
  private void runItemAnimations()
  {
    int j;
    int k;
    Object localObject2;
    Object localObject3;
    float f1;
    float f2;
    for (int i = 0; i < this.mPreLayoutItems.size(); i++)
    {
      j = this.mPreLayoutItems.keyAt(i);
      final Object localObject1 = (MenuItemLayoutInfo)this.mPreLayoutItems.get(j);
      k = this.mPostLayoutItems.indexOfKey(j);
      if (k >= 0)
      {
        MenuItemLayoutInfo localMenuItemLayoutInfo = (MenuItemLayoutInfo)this.mPostLayoutItems.valueAt(k);
        localObject2 = null;
        localObject3 = null;
        if (((MenuItemLayoutInfo)localObject1).left != localMenuItemLayoutInfo.left) {
          localObject2 = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, new float[] { ((MenuItemLayoutInfo)localObject1).left - localMenuItemLayoutInfo.left, 0.0F });
        }
        if (((MenuItemLayoutInfo)localObject1).top != localMenuItemLayoutInfo.top) {
          localObject3 = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[] { ((MenuItemLayoutInfo)localObject1).top - localMenuItemLayoutInfo.top, 0.0F });
        }
        if ((localObject2 != null) || (localObject3 != null))
        {
          for (k = 0; k < this.mRunningItemAnimations.size(); k++)
          {
            localObject1 = (ItemAnimationInfo)this.mRunningItemAnimations.get(k);
            if ((((ItemAnimationInfo)localObject1).id == j) && (((ItemAnimationInfo)localObject1).animType == 0)) {
              ((ItemAnimationInfo)localObject1).animator.cancel();
            }
          }
          if (localObject2 != null)
          {
            if (localObject3 != null) {
              localObject2 = ObjectAnimator.ofPropertyValuesHolder(localMenuItemLayoutInfo.view, new PropertyValuesHolder[] { localObject2, localObject3 });
            } else {
              localObject2 = ObjectAnimator.ofPropertyValuesHolder(localMenuItemLayoutInfo.view, new PropertyValuesHolder[] { localObject2 });
            }
          }
          else {
            localObject2 = ObjectAnimator.ofPropertyValuesHolder(localMenuItemLayoutInfo.view, new PropertyValuesHolder[] { localObject3 });
          }
          ((ObjectAnimator)localObject2).setDuration(150L);
          ((ObjectAnimator)localObject2).start();
          localObject3 = new ItemAnimationInfo(j, localMenuItemLayoutInfo, (Animator)localObject2, 0);
          this.mRunningItemAnimations.add(localObject3);
          ((ObjectAnimator)localObject2).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              for (int i = 0; i < ActionMenuPresenter.this.mRunningItemAnimations.size(); i++) {
                if (((ActionMenuPresenter.ItemAnimationInfo)ActionMenuPresenter.this.mRunningItemAnimations.get(i)).animator == paramAnonymousAnimator)
                {
                  ActionMenuPresenter.this.mRunningItemAnimations.remove(i);
                  break;
                }
              }
            }
          });
        }
        this.mPostLayoutItems.remove(j);
      }
      else
      {
        f1 = 1.0F;
        k = 0;
        while (k < this.mRunningItemAnimations.size())
        {
          localObject2 = (ItemAnimationInfo)this.mRunningItemAnimations.get(k);
          f2 = f1;
          if (((ItemAnimationInfo)localObject2).id == j)
          {
            f2 = f1;
            if (((ItemAnimationInfo)localObject2).animType == 1)
            {
              f2 = ((ItemAnimationInfo)localObject2).menuItemLayoutInfo.view.getAlpha();
              ((ItemAnimationInfo)localObject2).animator.cancel();
            }
          }
          k++;
          f1 = f2;
        }
        localObject2 = ObjectAnimator.ofFloat(((MenuItemLayoutInfo)localObject1).view, View.ALPHA, new float[] { f1, 0.0F });
        ((ViewGroup)this.mMenuView).getOverlay().add(((MenuItemLayoutInfo)localObject1).view);
        ((ObjectAnimator)localObject2).setDuration(150L);
        ((ObjectAnimator)localObject2).start();
        localObject3 = new ItemAnimationInfo(j, (MenuItemLayoutInfo)localObject1, (Animator)localObject2, 2);
        this.mRunningItemAnimations.add(localObject3);
        ((ObjectAnimator)localObject2).addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            for (int i = 0; i < ActionMenuPresenter.this.mRunningItemAnimations.size(); i++) {
              if (((ActionMenuPresenter.ItemAnimationInfo)ActionMenuPresenter.this.mRunningItemAnimations.get(i)).animator == paramAnonymousAnimator)
              {
                ActionMenuPresenter.this.mRunningItemAnimations.remove(i);
                break;
              }
            }
            ((ViewGroup)ActionMenuPresenter.this.mMenuView).getOverlay().remove(localObject1.view);
          }
        });
      }
    }
    for (i = 0; i < this.mPostLayoutItems.size(); i++)
    {
      j = this.mPostLayoutItems.keyAt(i);
      k = this.mPostLayoutItems.indexOfKey(j);
      if (k >= 0)
      {
        localObject3 = (MenuItemLayoutInfo)this.mPostLayoutItems.valueAt(k);
        f1 = 0.0F;
        k = 0;
        while (k < this.mRunningItemAnimations.size())
        {
          localObject2 = (ItemAnimationInfo)this.mRunningItemAnimations.get(k);
          f2 = f1;
          if (((ItemAnimationInfo)localObject2).id == j)
          {
            f2 = f1;
            if (((ItemAnimationInfo)localObject2).animType == 2)
            {
              f2 = ((ItemAnimationInfo)localObject2).menuItemLayoutInfo.view.getAlpha();
              ((ItemAnimationInfo)localObject2).animator.cancel();
            }
          }
          k++;
          f1 = f2;
        }
        localObject2 = ObjectAnimator.ofFloat(((MenuItemLayoutInfo)localObject3).view, View.ALPHA, new float[] { f1, 1.0F });
        ((ObjectAnimator)localObject2).start();
        ((ObjectAnimator)localObject2).setDuration(150L);
        localObject3 = new ItemAnimationInfo(j, (MenuItemLayoutInfo)localObject3, (Animator)localObject2, 1);
        this.mRunningItemAnimations.add(localObject3);
        ((ObjectAnimator)localObject2).addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            for (int i = 0; i < ActionMenuPresenter.this.mRunningItemAnimations.size(); i++) {
              if (((ActionMenuPresenter.ItemAnimationInfo)ActionMenuPresenter.this.mRunningItemAnimations.get(i)).animator == paramAnonymousAnimator)
              {
                ActionMenuPresenter.this.mRunningItemAnimations.remove(i);
                break;
              }
            }
          }
        });
      }
    }
    this.mPreLayoutItems.clear();
    this.mPostLayoutItems.clear();
  }
  
  private void setupItemAnimations()
  {
    computeMenuItemAnimationInfo(true);
    ((View)this.mMenuView).getViewTreeObserver().addOnPreDrawListener(this.mItemAnimationPreDrawListener);
  }
  
  public void bindItemView(MenuItemImpl paramMenuItemImpl, MenuView.ItemView paramItemView)
  {
    paramItemView.initialize(paramMenuItemImpl, 0);
    paramMenuItemImpl = (ActionMenuView)this.mMenuView;
    paramItemView = (ActionMenuItemView)paramItemView;
    paramItemView.setItemInvoker(paramMenuItemImpl);
    if (this.mPopupCallback == null) {
      this.mPopupCallback = new ActionMenuPopupCallback(null);
    }
    paramItemView.setPopupCallback(this.mPopupCallback);
  }
  
  @UnsupportedAppUsage
  public boolean dismissPopupMenus()
  {
    return hideOverflowMenu() | hideSubMenus();
  }
  
  public boolean filterLeftoverView(ViewGroup paramViewGroup, int paramInt)
  {
    if (paramViewGroup.getChildAt(paramInt) == this.mOverflowButton) {
      return false;
    }
    return super.filterLeftoverView(paramViewGroup, paramInt);
  }
  
  public boolean flagActionItems()
  {
    Object localObject1 = this;
    ArrayList localArrayList;
    int i;
    if (((ActionMenuPresenter)localObject1).mMenu != null)
    {
      localArrayList = ((ActionMenuPresenter)localObject1).mMenu.getVisibleItems();
      i = localArrayList.size();
    }
    else
    {
      localArrayList = null;
      i = 0;
    }
    int j = ((ActionMenuPresenter)localObject1).mMaxItems;
    int k = ((ActionMenuPresenter)localObject1).mActionItemWidthLimit;
    int m = View.MeasureSpec.makeMeasureSpec(0, 0);
    ViewGroup localViewGroup = (ViewGroup)((ActionMenuPresenter)localObject1).mMenuView;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    while (i4 < i)
    {
      localObject2 = (MenuItemImpl)localArrayList.get(i4);
      if (((MenuItemImpl)localObject2).requiresActionButton()) {
        n++;
      } else if (((MenuItemImpl)localObject2).requestsActionButton()) {
        i1++;
      } else {
        i3 = 1;
      }
      i5 = j;
      if (((ActionMenuPresenter)localObject1).mExpandedActionViewsExclusive)
      {
        i5 = j;
        if (((MenuItemImpl)localObject2).isActionViewExpanded()) {
          i5 = 0;
        }
      }
      i4++;
      j = i5;
    }
    i4 = j;
    if (((ActionMenuPresenter)localObject1).mReserveOverflow) {
      if (i3 == 0)
      {
        i4 = j;
        if (n + i1 <= j) {}
      }
      else
      {
        i4 = j - 1;
      }
    }
    int i6 = i4 - n;
    Object localObject2 = ((ActionMenuPresenter)localObject1).mActionButtonGroups;
    ((SparseBooleanArray)localObject2).clear();
    i1 = 0;
    i4 = 0;
    if (((ActionMenuPresenter)localObject1).mStrictWidthLimit)
    {
      j = ((ActionMenuPresenter)localObject1).mMinCellSize;
      i4 = k / j;
      i1 = j + k % j / i4;
    }
    int i5 = 0;
    j = i2;
    i2 = n;
    i3 = k;
    n = i6;
    k = i;
    for (;;)
    {
      localObject1 = this;
      if (i5 >= k) {
        break;
      }
      MenuItemImpl localMenuItemImpl = (MenuItemImpl)localArrayList.get(i5);
      View localView;
      if (localMenuItemImpl.requiresActionButton())
      {
        localView = ((ActionMenuPresenter)localObject1).getItemView(localMenuItemImpl, null, localViewGroup);
        if (((ActionMenuPresenter)localObject1).mStrictWidthLimit) {
          i4 -= ActionMenuView.measureChildForCells(localView, i1, i4, m, 0);
        } else {
          localView.measure(m, m);
        }
        i6 = localView.getMeasuredWidth();
        i3 -= i6;
        i = j;
        if (j == 0) {
          i = i6;
        }
        j = localMenuItemImpl.getGroupId();
        if (j != 0) {
          ((SparseBooleanArray)localObject2).put(j, true);
        }
        localMenuItemImpl.setIsActionButton(true);
        j = i;
      }
      else if (localMenuItemImpl.requestsActionButton())
      {
        int i7 = localMenuItemImpl.getGroupId();
        boolean bool = ((SparseBooleanArray)localObject2).get(i7);
        int i8;
        if (((n > 0) || (bool)) && (i3 > 0) && ((!((ActionMenuPresenter)localObject1).mStrictWidthLimit) || (i4 > 0))) {
          i8 = 1;
        } else {
          i8 = 0;
        }
        if (i8 != 0)
        {
          localView = ((ActionMenuPresenter)localObject1).getItemView(localMenuItemImpl, null, localViewGroup);
          if (((ActionMenuPresenter)localObject1).mStrictWidthLimit)
          {
            i = ActionMenuView.measureChildForCells(localView, i1, i4, m, 0);
            i4 -= i;
            if (i == 0) {
              i8 = 0;
            }
          }
          else
          {
            localView.measure(m, m);
          }
          i6 = localView.getMeasuredWidth();
          i3 -= i6;
          i = j;
          if (j == 0) {
            i = i6;
          }
          if (((ActionMenuPresenter)localObject1).mStrictWidthLimit)
          {
            if (i3 >= 0) {
              j = 1;
            } else {
              j = 0;
            }
            i8 = j & i8;
          }
          else
          {
            if (i3 + i > 0) {
              j = 1;
            } else {
              j = 0;
            }
            i8 = j & i8;
          }
        }
        else
        {
          i = j;
        }
        j = n;
        if ((i8 != 0) && (i7 != 0))
        {
          ((SparseBooleanArray)localObject2).put(i7, true);
          n = j;
        }
        else if (bool)
        {
          ((SparseBooleanArray)localObject2).put(i7, false);
          i6 = 0;
          while (i6 < i5)
          {
            localObject1 = (MenuItemImpl)localArrayList.get(i6);
            n = j;
            if (((MenuItemImpl)localObject1).getGroupId() == i7)
            {
              n = j;
              if (((MenuItemImpl)localObject1).isActionButton()) {
                n = j + 1;
              }
              ((MenuItemImpl)localObject1).setIsActionButton(false);
            }
            i6++;
            j = n;
          }
          n = j;
        }
        else
        {
          n = j;
        }
        j = n;
        if (i8 != 0) {
          j = n - 1;
        }
        localMenuItemImpl.setIsActionButton(i8);
        n = j;
        j = i;
      }
      else
      {
        localMenuItemImpl.setIsActionButton(false);
      }
      i5++;
    }
    return true;
  }
  
  public View getItemView(MenuItemImpl paramMenuItemImpl, View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramMenuItemImpl.getActionView();
    if ((localView == null) || (paramMenuItemImpl.hasCollapsibleActionView())) {
      localView = super.getItemView(paramMenuItemImpl, paramView, paramViewGroup);
    }
    int i;
    if (paramMenuItemImpl.isActionViewExpanded()) {
      i = 8;
    } else {
      i = 0;
    }
    localView.setVisibility(i);
    paramView = (ActionMenuView)paramViewGroup;
    paramMenuItemImpl = localView.getLayoutParams();
    if (!paramView.checkLayoutParams(paramMenuItemImpl)) {
      localView.setLayoutParams(paramView.generateLayoutParams(paramMenuItemImpl));
    }
    return localView;
  }
  
  public MenuView getMenuView(ViewGroup paramViewGroup)
  {
    MenuView localMenuView = this.mMenuView;
    paramViewGroup = super.getMenuView(paramViewGroup);
    if (localMenuView != paramViewGroup)
    {
      ((ActionMenuView)paramViewGroup).setPresenter(this);
      if (localMenuView != null) {
        ((View)localMenuView).removeOnAttachStateChangeListener(this.mAttachStateChangeListener);
      }
      ((View)paramViewGroup).addOnAttachStateChangeListener(this.mAttachStateChangeListener);
    }
    return paramViewGroup;
  }
  
  public Drawable getOverflowIcon()
  {
    OverflowMenuButton localOverflowMenuButton = this.mOverflowButton;
    if (localOverflowMenuButton != null) {
      return localOverflowMenuButton.getDrawable();
    }
    if (this.mPendingOverflowIconSet) {
      return this.mPendingOverflowIcon;
    }
    return null;
  }
  
  public boolean hideOverflowMenu()
  {
    if ((this.mPostedOpenRunnable != null) && (this.mMenuView != null))
    {
      ((View)this.mMenuView).removeCallbacks(this.mPostedOpenRunnable);
      this.mPostedOpenRunnable = null;
      return true;
    }
    OverflowPopup localOverflowPopup = this.mOverflowPopup;
    if (localOverflowPopup != null)
    {
      localOverflowPopup.dismiss();
      return true;
    }
    return false;
  }
  
  public boolean hideSubMenus()
  {
    ActionButtonSubmenu localActionButtonSubmenu = this.mActionButtonPopup;
    if (localActionButtonSubmenu != null)
    {
      localActionButtonSubmenu.dismiss();
      return true;
    }
    return false;
  }
  
  public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder)
  {
    super.initForMenu(paramContext, paramMenuBuilder);
    paramMenuBuilder = paramContext.getResources();
    paramContext = ActionBarPolicy.get(paramContext);
    if (!this.mReserveOverflowSet) {
      this.mReserveOverflow = paramContext.showsOverflowMenuButton();
    }
    if (!this.mWidthLimitSet) {
      this.mWidthLimit = paramContext.getEmbeddedMenuWidthLimit();
    }
    if (!this.mMaxItemsSet) {
      this.mMaxItems = paramContext.getMaxActionButtons();
    }
    int i = this.mWidthLimit;
    if (this.mReserveOverflow)
    {
      if (this.mOverflowButton == null)
      {
        this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
        if (this.mPendingOverflowIconSet)
        {
          this.mOverflowButton.setImageDrawable(this.mPendingOverflowIcon);
          this.mPendingOverflowIcon = null;
          this.mPendingOverflowIconSet = false;
        }
        int j = View.MeasureSpec.makeMeasureSpec(0, 0);
        this.mOverflowButton.measure(j, j);
      }
      i -= this.mOverflowButton.getMeasuredWidth();
    }
    else
    {
      this.mOverflowButton = null;
    }
    this.mActionItemWidthLimit = i;
    this.mMinCellSize = ((int)(paramMenuBuilder.getDisplayMetrics().density * 56.0F));
  }
  
  public boolean isOverflowMenuShowPending()
  {
    boolean bool;
    if ((this.mPostedOpenRunnable == null) && (!isOverflowMenuShowing())) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  public boolean isOverflowMenuShowing()
  {
    OverflowPopup localOverflowPopup = this.mOverflowPopup;
    boolean bool;
    if ((localOverflowPopup != null) && (localOverflowPopup.isShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isOverflowReserved()
  {
    return this.mReserveOverflow;
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    dismissPopupMenus();
    super.onCloseMenu(paramMenuBuilder, paramBoolean);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if (!this.mMaxItemsSet) {
      this.mMaxItems = ActionBarPolicy.get(this.mContext).getMaxActionButtons();
    }
    if (this.mMenu != null) {
      this.mMenu.onItemsChanged(true);
    }
  }
  
  @UnsupportedAppUsage
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    if (paramParcelable.openSubMenuId > 0)
    {
      paramParcelable = this.mMenu.findItem(paramParcelable.openSubMenuId);
      if (paramParcelable != null) {
        onSubMenuSelected((SubMenuBuilder)paramParcelable.getSubMenu());
      }
    }
  }
  
  @UnsupportedAppUsage
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState();
    localSavedState.openSubMenuId = this.mOpenSubMenuId;
    return localSavedState;
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
  {
    if (!paramSubMenuBuilder.hasVisibleItems()) {
      return false;
    }
    for (Object localObject = paramSubMenuBuilder; ((SubMenuBuilder)localObject).getParentMenu() != this.mMenu; localObject = (SubMenuBuilder)((SubMenuBuilder)localObject).getParentMenu()) {}
    View localView = findViewForItem(((SubMenuBuilder)localObject).getItem());
    if (localView == null) {
      return false;
    }
    this.mOpenSubMenuId = paramSubMenuBuilder.getItem().getItemId();
    boolean bool1 = false;
    int i = paramSubMenuBuilder.size();
    boolean bool2;
    for (int j = 0;; j++)
    {
      bool2 = bool1;
      if (j >= i) {
        break;
      }
      localObject = paramSubMenuBuilder.getItem(j);
      if ((((MenuItem)localObject).isVisible()) && (((MenuItem)localObject).getIcon() != null))
      {
        bool2 = true;
        break;
      }
    }
    this.mActionButtonPopup = new ActionButtonSubmenu(this.mContext, paramSubMenuBuilder, localView);
    this.mActionButtonPopup.setForceShowIcon(bool2);
    this.mActionButtonPopup.show();
    super.onSubMenuSelected(paramSubMenuBuilder);
    return true;
  }
  
  public void onSubUiVisibilityChanged(boolean paramBoolean)
  {
    if (paramBoolean) {
      super.onSubMenuSelected(null);
    } else if (this.mMenu != null) {
      this.mMenu.close(false);
    }
  }
  
  public void setExpandedActionViewsExclusive(boolean paramBoolean)
  {
    this.mExpandedActionViewsExclusive = paramBoolean;
  }
  
  public void setItemLimit(int paramInt)
  {
    this.mMaxItems = paramInt;
    this.mMaxItemsSet = true;
  }
  
  public void setMenuView(ActionMenuView paramActionMenuView)
  {
    if (paramActionMenuView != this.mMenuView)
    {
      if (this.mMenuView != null) {
        ((View)this.mMenuView).removeOnAttachStateChangeListener(this.mAttachStateChangeListener);
      }
      this.mMenuView = paramActionMenuView;
      paramActionMenuView.initialize(this.mMenu);
      paramActionMenuView.addOnAttachStateChangeListener(this.mAttachStateChangeListener);
    }
  }
  
  public void setOverflowIcon(Drawable paramDrawable)
  {
    OverflowMenuButton localOverflowMenuButton = this.mOverflowButton;
    if (localOverflowMenuButton != null)
    {
      localOverflowMenuButton.setImageDrawable(paramDrawable);
    }
    else
    {
      this.mPendingOverflowIconSet = true;
      this.mPendingOverflowIcon = paramDrawable;
    }
  }
  
  public void setReserveOverflow(boolean paramBoolean)
  {
    this.mReserveOverflow = paramBoolean;
    this.mReserveOverflowSet = true;
  }
  
  public void setWidthLimit(int paramInt, boolean paramBoolean)
  {
    this.mWidthLimit = paramInt;
    this.mStrictWidthLimit = paramBoolean;
    this.mWidthLimitSet = true;
  }
  
  public boolean shouldIncludeItem(int paramInt, MenuItemImpl paramMenuItemImpl)
  {
    return paramMenuItemImpl.isActionButton();
  }
  
  public boolean showOverflowMenu()
  {
    if ((this.mReserveOverflow) && (!isOverflowMenuShowing()) && (this.mMenu != null) && (this.mMenuView != null) && (this.mPostedOpenRunnable == null) && (!this.mMenu.getNonActionItems().isEmpty()))
    {
      this.mPostedOpenRunnable = new OpenOverflowRunnable(new OverflowPopup(this.mContext, this.mMenu, this.mOverflowButton, true));
      ((View)this.mMenuView).post(this.mPostedOpenRunnable);
      super.onSubMenuSelected(null);
      return true;
    }
    return false;
  }
  
  public void updateMenuView(boolean paramBoolean)
  {
    Object localObject = (ViewGroup)((View)this.mMenuView).getParent();
    super.updateMenuView(paramBoolean);
    ((View)this.mMenuView).requestLayout();
    if (this.mMenu != null)
    {
      ArrayList localArrayList = this.mMenu.getActionItems();
      i = localArrayList.size();
      for (j = 0; j < i; j++)
      {
        localObject = ((MenuItemImpl)localArrayList.get(j)).getActionProvider();
        if (localObject != null) {
          ((ActionProvider)localObject).setSubUiVisibilityListener(this);
        }
      }
    }
    if (this.mMenu != null) {
      localObject = this.mMenu.getNonActionItems();
    } else {
      localObject = null;
    }
    int i = 0;
    int j = i;
    boolean bool;
    if (this.mReserveOverflow)
    {
      j = i;
      if (localObject != null)
      {
        i = ((ArrayList)localObject).size();
        j = 0;
        if (i == 1) {
          bool = ((MenuItemImpl)((ArrayList)localObject).get(0)).isActionViewExpanded() ^ true;
        } else if (i > 0) {
          bool = true;
        }
      }
    }
    if (bool)
    {
      if (this.mOverflowButton == null) {
        this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
      }
      localObject = (ViewGroup)this.mOverflowButton.getParent();
      if (localObject != this.mMenuView)
      {
        if (localObject != null) {
          ((ViewGroup)localObject).removeView(this.mOverflowButton);
        }
        localObject = (ActionMenuView)this.mMenuView;
        ((ActionMenuView)localObject).addView(this.mOverflowButton, ((ActionMenuView)localObject).generateOverflowButtonLayoutParams());
      }
    }
    else
    {
      localObject = this.mOverflowButton;
      if ((localObject != null) && (((OverflowMenuButton)localObject).getParent() == this.mMenuView)) {
        ((ViewGroup)this.mMenuView).removeView(this.mOverflowButton);
      }
    }
    ((ActionMenuView)this.mMenuView).setOverflowReserved(this.mReserveOverflow);
  }
  
  private class ActionButtonSubmenu
    extends MenuPopupHelper
  {
    public ActionButtonSubmenu(Context paramContext, SubMenuBuilder paramSubMenuBuilder, View paramView)
    {
      super(paramSubMenuBuilder, paramView, false, 16843844);
      if (!((MenuItemImpl)paramSubMenuBuilder.getItem()).isActionButton())
      {
        if (ActionMenuPresenter.this.mOverflowButton == null) {
          paramContext = (View)ActionMenuPresenter.this.mMenuView;
        } else {
          paramContext = ActionMenuPresenter.this.mOverflowButton;
        }
        setAnchorView(paramContext);
      }
      setPresenterCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
    }
    
    protected void onDismiss()
    {
      ActionMenuPresenter.access$1702(ActionMenuPresenter.this, null);
      ActionMenuPresenter.this.mOpenSubMenuId = 0;
      super.onDismiss();
    }
  }
  
  private class ActionMenuPopupCallback
    extends ActionMenuItemView.PopupCallback
  {
    private ActionMenuPopupCallback() {}
    
    public ShowableListMenu getPopup()
    {
      MenuPopup localMenuPopup;
      if (ActionMenuPresenter.this.mActionButtonPopup != null) {
        localMenuPopup = ActionMenuPresenter.this.mActionButtonPopup.getPopup();
      } else {
        localMenuPopup = null;
      }
      return localMenuPopup;
    }
  }
  
  private static class ItemAnimationInfo
  {
    static final int FADE_IN = 1;
    static final int FADE_OUT = 2;
    static final int MOVE = 0;
    int animType;
    Animator animator;
    int id;
    ActionMenuPresenter.MenuItemLayoutInfo menuItemLayoutInfo;
    
    ItemAnimationInfo(int paramInt1, ActionMenuPresenter.MenuItemLayoutInfo paramMenuItemLayoutInfo, Animator paramAnimator, int paramInt2)
    {
      this.id = paramInt1;
      this.menuItemLayoutInfo = paramMenuItemLayoutInfo;
      this.animator = paramAnimator;
      this.animType = paramInt2;
    }
  }
  
  private static class MenuItemLayoutInfo
  {
    int left;
    int top;
    View view;
    
    MenuItemLayoutInfo(View paramView, boolean paramBoolean)
    {
      this.left = paramView.getLeft();
      this.top = paramView.getTop();
      if (paramBoolean)
      {
        this.left = ((int)(this.left + paramView.getTranslationX()));
        this.top = ((int)(this.top + paramView.getTranslationY()));
      }
      this.view = paramView;
    }
  }
  
  private class OpenOverflowRunnable
    implements Runnable
  {
    private ActionMenuPresenter.OverflowPopup mPopup;
    
    public OpenOverflowRunnable(ActionMenuPresenter.OverflowPopup paramOverflowPopup)
    {
      this.mPopup = paramOverflowPopup;
    }
    
    public void run()
    {
      if (ActionMenuPresenter.this.mMenu != null) {
        ActionMenuPresenter.this.mMenu.changeMenuMode();
      }
      View localView = (View)ActionMenuPresenter.this.mMenuView;
      if ((localView != null) && (localView.getWindowToken() != null) && (this.mPopup.tryShow())) {
        ActionMenuPresenter.access$1102(ActionMenuPresenter.this, this.mPopup);
      }
      ActionMenuPresenter.access$1202(ActionMenuPresenter.this, null);
    }
  }
  
  private class OverflowMenuButton
    extends ImageButton
    implements ActionMenuView.ActionMenuChildView
  {
    public OverflowMenuButton(Context paramContext)
    {
      super(null, 16843510);
      setClickable(true);
      setFocusable(true);
      setVisibility(0);
      setEnabled(true);
      setOnTouchListener(new ForwardingListener(this)
      {
        public ShowableListMenu getPopup()
        {
          if (ActionMenuPresenter.this.mOverflowPopup == null) {
            return null;
          }
          return ActionMenuPresenter.this.mOverflowPopup.getPopup();
        }
        
        public boolean onForwardingStarted()
        {
          ActionMenuPresenter.this.showOverflowMenu();
          return true;
        }
        
        public boolean onForwardingStopped()
        {
          if (ActionMenuPresenter.this.mPostedOpenRunnable != null) {
            return false;
          }
          ActionMenuPresenter.this.hideOverflowMenu();
          return true;
        }
      });
    }
    
    public boolean needsDividerAfter()
    {
      return false;
    }
    
    public boolean needsDividerBefore()
    {
      return false;
    }
    
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.setCanOpenPopup(true);
    }
    
    public boolean performClick()
    {
      if (super.performClick()) {
        return true;
      }
      playSoundEffect(0);
      ActionMenuPresenter.this.showOverflowMenu();
      return true;
    }
    
    protected boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      boolean bool = super.setFrame(paramInt1, paramInt2, paramInt3, paramInt4);
      Drawable localDrawable1 = getDrawable();
      Drawable localDrawable2 = getBackground();
      if ((localDrawable1 != null) && (localDrawable2 != null))
      {
        int i = getWidth();
        paramInt4 = getHeight();
        paramInt1 = Math.max(i, paramInt4) / 2;
        int j = getPaddingLeft();
        int k = getPaddingRight();
        paramInt3 = getPaddingTop();
        paramInt2 = getPaddingBottom();
        i = (i + (j - k)) / 2;
        paramInt2 = (paramInt4 + (paramInt3 - paramInt2)) / 2;
        localDrawable2.setHotspotBounds(i - paramInt1, paramInt2 - paramInt1, i + paramInt1, paramInt2 + paramInt1);
      }
      return bool;
    }
  }
  
  private class OverflowPopup
    extends MenuPopupHelper
  {
    public OverflowPopup(Context paramContext, MenuBuilder paramMenuBuilder, View paramView, boolean paramBoolean)
    {
      super(paramMenuBuilder, paramView, paramBoolean, 16843844);
      setGravity(8388613);
      setPresenterCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
    }
    
    protected void onDismiss()
    {
      if (ActionMenuPresenter.this.mMenu != null) {
        ActionMenuPresenter.this.mMenu.close();
      }
      ActionMenuPresenter.access$1102(ActionMenuPresenter.this, null);
      super.onDismiss();
    }
  }
  
  private class PopupPresenterCallback
    implements MenuPresenter.Callback
  {
    private PopupPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
    {
      if ((paramMenuBuilder instanceof SubMenuBuilder)) {
        paramMenuBuilder.getRootMenu().close(false);
      }
      MenuPresenter.Callback localCallback = ActionMenuPresenter.this.getCallback();
      if (localCallback != null) {
        localCallback.onCloseMenu(paramMenuBuilder, paramBoolean);
      }
    }
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      boolean bool = false;
      if (paramMenuBuilder == null) {
        return false;
      }
      ActionMenuPresenter.this.mOpenSubMenuId = ((SubMenuBuilder)paramMenuBuilder).getItem().getItemId();
      MenuPresenter.Callback localCallback = ActionMenuPresenter.this.getCallback();
      if (localCallback != null) {
        bool = localCallback.onOpenSubMenu(paramMenuBuilder);
      }
      return bool;
    }
  }
  
  private static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ActionMenuPresenter.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ActionMenuPresenter.SavedState(paramAnonymousParcel);
      }
      
      public ActionMenuPresenter.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ActionMenuPresenter.SavedState[paramAnonymousInt];
      }
    };
    public int openSubMenuId;
    
    SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      this.openSubMenuId = paramParcel.readInt();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.openSubMenuId);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ActionMenuPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */