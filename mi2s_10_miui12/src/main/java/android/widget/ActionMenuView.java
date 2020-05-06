package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.view.menu.ActionMenuItemView;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuBuilder.Callback;
import com.android.internal.view.menu.MenuBuilder.ItemInvoker;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPresenter.Callback;
import com.android.internal.view.menu.MenuView;

public class ActionMenuView
  extends LinearLayout
  implements MenuBuilder.ItemInvoker, MenuView
{
  static final int GENERATED_ITEM_PADDING = 4;
  static final int MIN_CELL_SIZE = 56;
  private static final String TAG = "ActionMenuView";
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  private boolean mFormatItems;
  private int mFormatItemsWidth;
  private int mGeneratedItemPadding;
  private MenuBuilder mMenu;
  private MenuBuilder.Callback mMenuBuilderCallback;
  private int mMinCellSize;
  private OnMenuItemClickListener mOnMenuItemClickListener;
  private Context mPopupContext;
  private int mPopupTheme;
  private ActionMenuPresenter mPresenter;
  private boolean mReserveOverflow;
  
  public ActionMenuView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionMenuView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setBaselineAligned(false);
    float f = paramContext.getResources().getDisplayMetrics().density;
    this.mMinCellSize = ((int)(56.0F * f));
    this.mGeneratedItemPadding = ((int)(4.0F * f));
    this.mPopupContext = paramContext;
    this.mPopupTheme = 0;
  }
  
  static int measureChildForCells(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt3) - paramInt4, View.MeasureSpec.getMode(paramInt3));
    ActionMenuItemView localActionMenuItemView;
    if ((paramView instanceof ActionMenuItemView)) {
      localActionMenuItemView = (ActionMenuItemView)paramView;
    } else {
      localActionMenuItemView = null;
    }
    boolean bool1 = false;
    if ((localActionMenuItemView != null) && (localActionMenuItemView.hasText())) {
      paramInt4 = 1;
    } else {
      paramInt4 = 0;
    }
    int j = 0;
    paramInt3 = j;
    if (paramInt2 > 0) {
      if (paramInt4 != 0)
      {
        paramInt3 = j;
        if (paramInt2 < 2) {}
      }
      else
      {
        paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 * paramInt2, Integer.MIN_VALUE), i);
        j = paramView.getMeasuredWidth();
        paramInt3 = j / paramInt1;
        paramInt2 = paramInt3;
        if (j % paramInt1 != 0) {
          paramInt2 = paramInt3 + 1;
        }
        paramInt3 = paramInt2;
        if (paramInt4 != 0)
        {
          paramInt3 = paramInt2;
          if (paramInt2 < 2) {
            paramInt3 = 2;
          }
        }
      }
    }
    boolean bool2 = bool1;
    if (!localLayoutParams.isOverflowButton)
    {
      bool2 = bool1;
      if (paramInt4 != 0) {
        bool2 = true;
      }
    }
    localLayoutParams.expandable = bool2;
    localLayoutParams.cellsUsed = paramInt3;
    paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt3 * paramInt1, 1073741824), i);
    return paramInt3;
  }
  
  private void onMeasureExactFormat(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    int k = getPaddingLeft() + getPaddingRight();
    int m = getPaddingTop() + getPaddingBottom();
    int n = getChildMeasureSpec(paramInt2, m, -2);
    int i1 = paramInt1 - k;
    paramInt1 = this.mMinCellSize;
    int i2 = i1 / paramInt1;
    int i3 = i1 % paramInt1;
    if (i2 == 0)
    {
      setMeasuredDimension(i1, 0);
      return;
    }
    int i4 = paramInt1 + i3 / i2;
    int i5 = 0;
    int i6 = getChildCount();
    int i7 = 0;
    int i8 = 0;
    int i9 = 0;
    int i10 = 0;
    paramInt1 = i2;
    int i11 = 0;
    long l1 = 0L;
    Object localObject1;
    Object localObject2;
    int i12;
    while (i11 < i6)
    {
      localObject1 = getChildAt(i11);
      if (((View)localObject1).getVisibility() == 8)
      {
        paramInt2 = i9;
      }
      else
      {
        boolean bool = localObject1 instanceof ActionMenuItemView;
        i8++;
        if (bool)
        {
          paramInt2 = this.mGeneratedItemPadding;
          ((View)localObject1).setPadding(paramInt2, 0, paramInt2, 0);
        }
        localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
        ((LayoutParams)localObject2).expanded = false;
        ((LayoutParams)localObject2).extraPixels = 0;
        ((LayoutParams)localObject2).cellsUsed = 0;
        ((LayoutParams)localObject2).expandable = false;
        ((LayoutParams)localObject2).leftMargin = 0;
        ((LayoutParams)localObject2).rightMargin = 0;
        if ((bool) && (((ActionMenuItemView)localObject1).hasText())) {
          bool = true;
        } else {
          bool = false;
        }
        ((LayoutParams)localObject2).preventEdgeOffset = bool;
        if (((LayoutParams)localObject2).isOverflowButton) {
          paramInt2 = 1;
        } else {
          paramInt2 = paramInt1;
        }
        i12 = measureChildForCells((View)localObject1, i4, paramInt2, n, m);
        i10 = Math.max(i10, i12);
        paramInt2 = i9;
        if (((LayoutParams)localObject2).expandable) {
          paramInt2 = i9 + 1;
        }
        if (((LayoutParams)localObject2).isOverflowButton) {
          i5 = 1;
        }
        paramInt1 -= i12;
        i7 = Math.max(i7, ((View)localObject1).getMeasuredHeight());
        if (i12 == 1) {
          l1 |= 1 << i11;
        }
      }
      i11++;
      i9 = paramInt2;
    }
    if ((i5 != 0) && (i8 == 2)) {
      i11 = 1;
    } else {
      i11 = 0;
    }
    i3 = 0;
    k = paramInt1;
    paramInt2 = i6;
    paramInt1 = i3;
    m = i1;
    while ((i9 > 0) && (k > 0))
    {
      long l2 = 0L;
      i1 = Integer.MAX_VALUE;
      i3 = 0;
      i6 = 0;
      long l3;
      while (i6 < paramInt2)
      {
        localObject2 = (LayoutParams)getChildAt(i6).getLayoutParams();
        if (!((LayoutParams)localObject2).expandable)
        {
          i2 = i3;
          i12 = i1;
          l3 = l2;
        }
        else if (((LayoutParams)localObject2).cellsUsed < i1)
        {
          i12 = ((LayoutParams)localObject2).cellsUsed;
          l3 = 1 << i6;
          i2 = 1;
        }
        else
        {
          i2 = i3;
          i12 = i1;
          l3 = l2;
          if (((LayoutParams)localObject2).cellsUsed == i1)
          {
            l3 = 1 << i6;
            i2 = i3 + 1;
            l3 = l2 | l3;
            i12 = i1;
          }
        }
        i6++;
        i3 = i2;
        i1 = i12;
        l2 = l3;
      }
      i2 = paramInt1;
      paramInt1 = paramInt2;
      l1 |= l2;
      if (i3 > k)
      {
        i9 = paramInt1;
        paramInt1 = i2;
        break label765;
      }
      paramInt2 = 0;
      while (paramInt2 < paramInt1)
      {
        localObject2 = getChildAt(paramInt2);
        localObject1 = (LayoutParams)((View)localObject2).getLayoutParams();
        if ((l2 & 1 << paramInt2) == 0L)
        {
          i2 = k;
          l3 = l1;
          if (((LayoutParams)localObject1).cellsUsed == i1 + 1)
          {
            l3 = l1 | 1 << paramInt2;
            i2 = k;
          }
        }
        else
        {
          if ((i11 != 0) && (((LayoutParams)localObject1).preventEdgeOffset) && (k == 1))
          {
            i2 = this.mGeneratedItemPadding;
            ((View)localObject2).setPadding(i2 + i4, 0, i2, 0);
          }
          ((LayoutParams)localObject1).cellsUsed += 1;
          ((LayoutParams)localObject1).expanded = true;
          i2 = k - 1;
          l3 = l1;
        }
        paramInt2++;
        k = i2;
        l1 = l3;
      }
      i3 = 1;
      paramInt2 = paramInt1;
      paramInt1 = i3;
    }
    i9 = paramInt2;
    label765:
    if ((i5 == 0) && (i8 == 1)) {
      i5 = 1;
    } else {
      i5 = 0;
    }
    paramInt2 = paramInt1;
    if (k > 0)
    {
      paramInt2 = paramInt1;
      if (l1 != 0L) {
        if ((k >= i8 - 1) && (i5 == 0))
        {
          paramInt2 = paramInt1;
          if (i10 <= 1) {}
        }
        else
        {
          float f1 = Long.bitCount(l1);
          if (i5 == 0)
          {
            float f2;
            if ((l1 & 1L) != 0L)
            {
              f2 = f1;
              if (!((LayoutParams)getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                f2 = f1 - 0.5F;
              }
            }
            else
            {
              f2 = f1;
            }
            f1 = f2;
            if ((l1 & 1 << i9 - 1) != 0L)
            {
              f1 = f2;
              if (!((LayoutParams)getChildAt(i9 - 1).getLayoutParams()).preventEdgeOffset) {
                f1 = f2 - 0.5F;
              }
            }
          }
          i5 = 0;
          if (f1 > 0.0F) {
            i5 = (int)(k * i4 / f1);
          }
          i3 = 0;
          while (i3 < i9)
          {
            if ((l1 & 1 << i3) == 0L)
            {
              paramInt2 = paramInt1;
            }
            else
            {
              localObject1 = getChildAt(i3);
              localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
              if ((localObject1 instanceof ActionMenuItemView))
              {
                ((LayoutParams)localObject2).extraPixels = i5;
                ((LayoutParams)localObject2).expanded = true;
                if ((i3 == 0) && (!((LayoutParams)localObject2).preventEdgeOffset)) {
                  ((LayoutParams)localObject2).leftMargin = (-i5 / 2);
                }
                paramInt2 = 1;
              }
              else if (((LayoutParams)localObject2).isOverflowButton)
              {
                ((LayoutParams)localObject2).extraPixels = i5;
                ((LayoutParams)localObject2).expanded = true;
                ((LayoutParams)localObject2).rightMargin = (-i5 / 2);
                paramInt2 = 1;
              }
              else
              {
                if (i3 != 0) {
                  ((LayoutParams)localObject2).leftMargin = (i5 / 2);
                }
                paramInt2 = paramInt1;
                if (i3 != i9 - 1)
                {
                  ((LayoutParams)localObject2).rightMargin = (i5 / 2);
                  paramInt2 = paramInt1;
                }
              }
            }
            i3++;
            paramInt1 = paramInt2;
          }
          paramInt2 = paramInt1;
        }
      }
    }
    if (paramInt2 != 0) {
      for (paramInt1 = 0; paramInt1 < i9; paramInt1++)
      {
        localObject2 = getChildAt(paramInt1);
        localObject1 = (LayoutParams)((View)localObject2).getLayoutParams();
        if (((LayoutParams)localObject1).expanded) {
          ((View)localObject2).measure(View.MeasureSpec.makeMeasureSpec(((LayoutParams)localObject1).cellsUsed * i4 + ((LayoutParams)localObject1).extraPixels, 1073741824), n);
        }
      }
    }
    if (i != 1073741824) {
      paramInt1 = i7;
    } else {
      paramInt1 = j;
    }
    setMeasuredDimension(m, paramInt1);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    boolean bool;
    if ((paramLayoutParams != null) && ((paramLayoutParams instanceof LayoutParams))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void dismissPopupMenus()
  {
    ActionMenuPresenter localActionMenuPresenter = this.mPresenter;
    if (localActionMenuPresenter != null) {
      localActionMenuPresenter.dismissPopupMenus();
    }
  }
  
  public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    return false;
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    LayoutParams localLayoutParams = new LayoutParams(-2, -2);
    localLayoutParams.gravity = 16;
    return localLayoutParams;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams != null)
    {
      if ((paramLayoutParams instanceof LayoutParams)) {
        paramLayoutParams = new LayoutParams((LayoutParams)paramLayoutParams);
      } else {
        paramLayoutParams = new LayoutParams(paramLayoutParams);
      }
      if (paramLayoutParams.gravity <= 0) {
        paramLayoutParams.gravity = 16;
      }
      return paramLayoutParams;
    }
    return generateDefaultLayoutParams();
  }
  
  public LayoutParams generateOverflowButtonLayoutParams()
  {
    LayoutParams localLayoutParams = generateDefaultLayoutParams();
    localLayoutParams.isOverflowButton = true;
    return localLayoutParams;
  }
  
  public Menu getMenu()
  {
    if (this.mMenu == null)
    {
      Object localObject = getContext();
      this.mMenu = new MenuBuilder((Context)localObject);
      this.mMenu.setCallback(new MenuBuilderCallback(null));
      this.mPresenter = new ActionMenuPresenter((Context)localObject);
      this.mPresenter.setReserveOverflow(true);
      ActionMenuPresenter localActionMenuPresenter = this.mPresenter;
      localObject = this.mActionMenuPresenterCallback;
      if (localObject == null) {
        localObject = new ActionMenuPresenterCallback(null);
      }
      localActionMenuPresenter.setCallback((MenuPresenter.Callback)localObject);
      this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
      this.mPresenter.setMenuView(this);
    }
    return this.mMenu;
  }
  
  public Drawable getOverflowIcon()
  {
    getMenu();
    return this.mPresenter.getOverflowIcon();
  }
  
  public int getPopupTheme()
  {
    return this.mPopupTheme;
  }
  
  public int getWindowAnimations()
  {
    return 0;
  }
  
  @UnsupportedAppUsage
  protected boolean hasDividerBeforeChildAt(int paramInt)
  {
    if (paramInt == 0) {
      return false;
    }
    View localView1 = getChildAt(paramInt - 1);
    View localView2 = getChildAt(paramInt);
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramInt < getChildCount())
    {
      bool2 = bool1;
      if ((localView1 instanceof ActionMenuChildView)) {
        bool2 = false | ((ActionMenuChildView)localView1).needsDividerAfter();
      }
    }
    bool1 = bool2;
    if (paramInt > 0)
    {
      bool1 = bool2;
      if ((localView2 instanceof ActionMenuChildView)) {
        bool1 = bool2 | ((ActionMenuChildView)localView2).needsDividerBefore();
      }
    }
    return bool1;
  }
  
  public boolean hideOverflowMenu()
  {
    ActionMenuPresenter localActionMenuPresenter = this.mPresenter;
    boolean bool;
    if ((localActionMenuPresenter != null) && (localActionMenuPresenter.hideOverflowMenu())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void initialize(MenuBuilder paramMenuBuilder)
  {
    this.mMenu = paramMenuBuilder;
  }
  
  public boolean invokeItem(MenuItemImpl paramMenuItemImpl)
  {
    return this.mMenu.performItemAction(paramMenuItemImpl, 0);
  }
  
  @UnsupportedAppUsage
  public boolean isOverflowMenuShowPending()
  {
    ActionMenuPresenter localActionMenuPresenter = this.mPresenter;
    boolean bool;
    if ((localActionMenuPresenter != null) && (localActionMenuPresenter.isOverflowMenuShowPending())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean isOverflowMenuShowing()
  {
    ActionMenuPresenter localActionMenuPresenter = this.mPresenter;
    boolean bool;
    if ((localActionMenuPresenter != null) && (localActionMenuPresenter.isOverflowMenuShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  public boolean isOverflowReserved()
  {
    return this.mReserveOverflow;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    paramConfiguration = this.mPresenter;
    if (paramConfiguration != null)
    {
      paramConfiguration.updateMenuView(false);
      if (this.mPresenter.isOverflowMenuShowing())
      {
        this.mPresenter.hideOverflowMenu();
        this.mPresenter.showOverflowMenu();
      }
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    dismissPopupMenus();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Object localObject = this;
    if (!((ActionMenuView)localObject).mFormatItems)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    int i = getChildCount();
    int j = (paramInt4 - paramInt2) / 2;
    int k = getDividerWidth();
    paramInt2 = 0;
    int m = 0;
    int n = 0;
    paramInt4 = paramInt3 - paramInt1 - getPaddingRight() - getPaddingLeft();
    int i1 = 0;
    paramBoolean = isLayoutRtl();
    View localView;
    LayoutParams localLayoutParams;
    for (int i2 = 0; i2 < i; i2++)
    {
      localView = ((ActionMenuView)localObject).getChildAt(i2);
      if (localView.getVisibility() != 8)
      {
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int i3;
        int i4;
        if (localLayoutParams.isOverflowButton)
        {
          i1 = localView.getMeasuredWidth();
          paramInt2 = i1;
          if (((ActionMenuView)localObject).hasDividerBeforeChildAt(i2)) {
            paramInt2 = i1 + k;
          }
          i3 = localView.getMeasuredHeight();
          if (paramBoolean)
          {
            i1 = getPaddingLeft() + localLayoutParams.leftMargin;
            i4 = i1 + paramInt2;
          }
          else
          {
            i4 = getWidth() - getPaddingRight() - localLayoutParams.rightMargin;
            i1 = i4 - paramInt2;
          }
          int i5 = j - i3 / 2;
          localView.layout(i1, i5, i4, i5 + i3);
          paramInt4 -= paramInt2;
          i1 = 1;
        }
        else
        {
          i3 = localView.getMeasuredWidth() + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
          i4 = m + i3;
          paramInt4 -= i3;
          m = i4;
          if (((ActionMenuView)localObject).hasDividerBeforeChildAt(i2)) {
            m = i4 + k;
          }
          n++;
        }
      }
    }
    m = 1;
    if ((i == 1) && (i1 == 0))
    {
      localObject = ((ActionMenuView)localObject).getChildAt(0);
      paramInt4 = ((View)localObject).getMeasuredWidth();
      paramInt2 = ((View)localObject).getMeasuredHeight();
      paramInt1 = (paramInt3 - paramInt1) / 2 - paramInt4 / 2;
      paramInt3 = j - paramInt2 / 2;
      ((View)localObject).layout(paramInt1, paramInt3, paramInt1 + paramInt4, paramInt3 + paramInt2);
      return;
    }
    paramInt1 = m;
    if (i1 != 0) {
      paramInt1 = 0;
    }
    paramInt1 = n - paramInt1;
    if (paramInt1 > 0) {
      paramInt1 = paramInt4 / paramInt1;
    } else {
      paramInt1 = 0;
    }
    m = Math.max(0, paramInt1);
    if (paramBoolean)
    {
      paramInt4 = getWidth() - getPaddingRight();
      paramInt1 = 0;
      paramInt3 = k;
      while (paramInt1 < i)
      {
        localView = ((ActionMenuView)localObject).getChildAt(paramInt1);
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if ((localView.getVisibility() != 8) && (!localLayoutParams.isOverflowButton))
        {
          n = paramInt4 - localLayoutParams.rightMargin;
          i1 = localView.getMeasuredWidth();
          i2 = localView.getMeasuredHeight();
          paramInt4 = j - i2 / 2;
          localView.layout(n - i1, paramInt4, n, paramInt4 + i2);
          paramInt4 = n - (localLayoutParams.leftMargin + i1 + m);
        }
        paramInt1++;
      }
    }
    else
    {
      paramInt2 = getPaddingLeft();
      paramInt1 = 0;
      while (paramInt1 < i)
      {
        localView = getChildAt(paramInt1);
        localObject = (LayoutParams)localView.getLayoutParams();
        paramInt3 = paramInt2;
        if (localView.getVisibility() != 8) {
          if (((LayoutParams)localObject).isOverflowButton)
          {
            paramInt3 = paramInt2;
          }
          else
          {
            paramInt2 += ((LayoutParams)localObject).leftMargin;
            paramInt4 = localView.getMeasuredWidth();
            paramInt3 = localView.getMeasuredHeight();
            i2 = j - paramInt3 / 2;
            localView.layout(paramInt2, i2, paramInt2 + paramInt4, i2 + paramInt3);
            paramInt3 = paramInt2 + (((LayoutParams)localObject).rightMargin + paramInt4 + m);
          }
        }
        paramInt1++;
        paramInt2 = paramInt3;
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    boolean bool1 = this.mFormatItems;
    boolean bool2;
    if (View.MeasureSpec.getMode(paramInt1) == 1073741824) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mFormatItems = bool2;
    if (bool1 != this.mFormatItems) {
      this.mFormatItemsWidth = 0;
    }
    int i = View.MeasureSpec.getSize(paramInt1);
    Object localObject;
    if (this.mFormatItems)
    {
      localObject = this.mMenu;
      if ((localObject != null) && (i != this.mFormatItemsWidth))
      {
        this.mFormatItemsWidth = i;
        ((MenuBuilder)localObject).onItemsChanged(true);
      }
    }
    int j = getChildCount();
    if ((this.mFormatItems) && (j > 0))
    {
      onMeasureExactFormat(paramInt1, paramInt2);
    }
    else
    {
      for (i = 0; i < j; i++)
      {
        localObject = (LayoutParams)getChildAt(i).getLayoutParams();
        ((LayoutParams)localObject).rightMargin = 0;
        ((LayoutParams)localObject).leftMargin = 0;
      }
      super.onMeasure(paramInt1, paramInt2);
    }
  }
  
  @UnsupportedAppUsage
  public MenuBuilder peekMenu()
  {
    return this.mMenu;
  }
  
  @UnsupportedAppUsage
  public void setExpandedActionViewsExclusive(boolean paramBoolean)
  {
    this.mPresenter.setExpandedActionViewsExclusive(paramBoolean);
  }
  
  @UnsupportedAppUsage
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1)
  {
    this.mActionMenuPresenterCallback = paramCallback;
    this.mMenuBuilderCallback = paramCallback1;
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    this.mOnMenuItemClickListener = paramOnMenuItemClickListener;
  }
  
  public void setOverflowIcon(Drawable paramDrawable)
  {
    getMenu();
    this.mPresenter.setOverflowIcon(paramDrawable);
  }
  
  public void setOverflowReserved(boolean paramBoolean)
  {
    this.mReserveOverflow = paramBoolean;
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
  
  public void setPresenter(ActionMenuPresenter paramActionMenuPresenter)
  {
    this.mPresenter = paramActionMenuPresenter;
    this.mPresenter.setMenuView(this);
  }
  
  public boolean showOverflowMenu()
  {
    ActionMenuPresenter localActionMenuPresenter = this.mPresenter;
    boolean bool;
    if ((localActionMenuPresenter != null) && (localActionMenuPresenter.showOverflowMenu())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static abstract interface ActionMenuChildView
  {
    public abstract boolean needsDividerAfter();
    
    @UnsupportedAppUsage
    public abstract boolean needsDividerBefore();
  }
  
  private class ActionMenuPresenterCallback
    implements MenuPresenter.Callback
  {
    private ActionMenuPresenterCallback() {}
    
    public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {}
    
    public boolean onOpenSubMenu(MenuBuilder paramMenuBuilder)
    {
      return false;
    }
  }
  
  public static class LayoutParams
    extends LinearLayout.LayoutParams
  {
    @ViewDebug.ExportedProperty(category="layout")
    @UnsupportedAppUsage
    public int cellsUsed;
    @ViewDebug.ExportedProperty(category="layout")
    @UnsupportedAppUsage
    public boolean expandable;
    @UnsupportedAppUsage
    public boolean expanded;
    @ViewDebug.ExportedProperty(category="layout")
    @UnsupportedAppUsage
    public int extraPixels;
    @ViewDebug.ExportedProperty(category="layout")
    @UnsupportedAppUsage
    public boolean isOverflowButton;
    @ViewDebug.ExportedProperty(category="layout")
    @UnsupportedAppUsage
    public boolean preventEdgeOffset;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      this.isOverflowButton = false;
    }
    
    public LayoutParams(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      super(paramInt2);
      this.isOverflowButton = paramBoolean;
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.isOverflowButton = paramLayoutParams.isOverflowButton;
    }
    
    protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
    {
      super.encodeProperties(paramViewHierarchyEncoder);
      paramViewHierarchyEncoder.addProperty("layout:overFlowButton", this.isOverflowButton);
      paramViewHierarchyEncoder.addProperty("layout:cellsUsed", this.cellsUsed);
      paramViewHierarchyEncoder.addProperty("layout:extraPixels", this.extraPixels);
      paramViewHierarchyEncoder.addProperty("layout:expandable", this.expandable);
      paramViewHierarchyEncoder.addProperty("layout:preventEdgeOffset", this.preventEdgeOffset);
    }
  }
  
  private class MenuBuilderCallback
    implements MenuBuilder.Callback
  {
    private MenuBuilderCallback() {}
    
    public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem)
    {
      boolean bool;
      if ((ActionMenuView.this.mOnMenuItemClickListener != null) && (ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(paramMenuItem))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void onMenuModeChange(MenuBuilder paramMenuBuilder)
    {
      if (ActionMenuView.this.mMenuBuilderCallback != null) {
        ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(paramMenuBuilder);
      }
    }
  }
  
  public static abstract interface OnMenuItemClickListener
  {
    public abstract boolean onMenuItemClick(MenuItem paramMenuItem);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ActionMenuView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */